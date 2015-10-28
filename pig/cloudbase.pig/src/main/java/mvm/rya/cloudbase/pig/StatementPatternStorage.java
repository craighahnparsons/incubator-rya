package mvm.rya.cloudbase.pig;

import cloudbase.core.client.ZooKeeperInstance;
import cloudbase.core.client.mock.MockInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import mvm.rya.api.RdfCloudTripleStoreConstants;
import mvm.rya.api.RdfCloudTripleStoreUtils;
import mvm.rya.api.domain.RyaStatement;
import mvm.rya.api.domain.RyaType;
import mvm.rya.api.domain.RyaURI;
import mvm.rya.api.persist.RyaDAOException;
import mvm.rya.api.query.strategy.ByteRange;
import mvm.rya.api.query.strategy.TriplePatternStrategy;
import mvm.rya.api.resolver.RdfToRyaConversions;
import mvm.rya.api.resolver.RyaContext;
import mvm.rya.api.resolver.triple.TripleRow;
import mvm.rya.cloudbase.CloudbaseRdfConfiguration;
import mvm.rya.cloudbase.CloudbaseRyaDAO;
import mvm.rya.rdftriplestore.inference.InferenceEngine;
import mvm.rya.rdftriplestore.inference.InferenceEngineException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParser;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static mvm.rya.api.RdfCloudTripleStoreConstants.TABLE_LAYOUT;

/**
 */
public class StatementPatternStorage extends CloudbaseStorage {
    private static final Log logger = LogFactory.getLog(StatementPatternStorage.class);
    protected TABLE_LAYOUT layout;
    protected String subject = "?s";
    protected String predicate = "?p";
    protected String object = "?o";
    protected String context;
    private Value subject_value;
    private Value predicate_value;
    private Value object_value;

    private RyaContext ryaContext = RyaContext.getInstance();

    /**
     * whether to turn inferencing on or off
     */
    private boolean infer = true;

    public StatementPatternStorage() {

    }

    private Value getValue(Var subjectVar) {
        return subjectVar.hasValue() ? subjectVar.getValue() : null;
    }

    @Override
    public void setLocation(String location, Job job) throws IOException {
        super.setLocation(location, job);
    }

    @Override
    protected void setLocationFromUri(String uri, Job job) throws IOException {
        super.setLocationFromUri(uri, job);
        // ex: cloudbase://tablePrefix?instance=myinstance&user=root&password=secret&zookeepers=127.0.0.1:2181&auths=PRIVATE,PUBLIC&subject=a&predicate=b&object=c&context=c&infer=true
        addStatementPatternRange(subject, predicate, object, context);
        if (infer) {
            addInferredRanges(table, job);
        }

        if (layout == null || ranges.size() == 0)
            throw new IllegalArgumentException("Range and/or layout is null. Check the query");
        table = RdfCloudTripleStoreUtils.layoutPrefixToTable(layout, table);
        tableName = new Text(table);
    }

    @Override
    protected void addLocationFromUriPart(String[] pair) {
        if (pair[0].equals("subject")) {
            this.subject = pair[1];
        } else if (pair[0].equals("predicate")) {
            this.predicate = pair[1];
        } else if (pair[0].equals("object")) {
            this.object = pair[1];
        } else if (pair[0].equals("context")) {
            this.context = pair[1];
        } else if (pair[0].equals("infer")) {
            this.infer = Boolean.parseBoolean(pair[1]);
        }
    }

    protected void addStatementPatternRange(String subj, String pred, String obj, String ctxt) throws IOException {
        logger.info("Adding statement pattern[subject:" + subj + ", predicate:" + pred + ", object:" + obj + ", context:" + ctxt + "]");
        StringBuilder sparqlBuilder = new StringBuilder();
        sparqlBuilder.append("select * where {\n");
        if (ctxt != null) {
            /**
             * select * where {
             GRAPH ?g {
             <http://www.example.org/exampleDocument#Monica> ?p ?o.
             }
             }
             */
            sparqlBuilder.append("GRAPH ").append(ctxt).append(" {\n");
        }
        sparqlBuilder.append(subj).append(" ").append(pred).append(" ").append(obj).append(".\n");
        if (ctxt != null) {
            sparqlBuilder.append("}\n");
        }
        sparqlBuilder.append("}\n");
        String sparql = sparqlBuilder.toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Sparql statement range[" + sparql + "]");
        }

        QueryParser parser = new SPARQLParser();
        ParsedQuery parsedQuery = null;
        try {
            parsedQuery = parser.parseQuery(sparql, null);
        } catch (MalformedQueryException e) {
            throw new IOException(e);
        }
        parsedQuery.getTupleExpr().visitChildren(new QueryModelVisitorBase<IOException>() {
            @Override
            public void meet(StatementPattern node) throws IOException {
                Var subjectVar = node.getSubjectVar();
                Var predicateVar = node.getPredicateVar();
                Var objectVar = node.getObjectVar();
                subject_value = getValue(subjectVar);
                predicate_value = getValue(predicateVar);
                object_value = getValue(objectVar);
                Map.Entry<TABLE_LAYOUT, Range> temp = createRange(subject_value, predicate_value, object_value);
//                Map.Entry<TABLE_LAYOUT, Range> temp =
//                        queryRangeFactory.defineRange(subject_value, predicate_value, object_value, null);
                layout = temp.getKey();
                Range range = temp.getValue();
                addRange(range);
                Var contextVar = node.getContextVar();
                if (contextVar != null && contextVar.getValue() != null) {
                    String context_str = contextVar.getValue().stringValue();
                    addColumnPair(context_str, "");
                }
            }
        });
    }

    protected Map.Entry<TABLE_LAYOUT, Range> createRange(Value s_v, Value p_v, Value o_v) throws IOException {
        RyaURI subject_rya = RdfToRyaConversions.convertResource((Resource) s_v);
        RyaURI predicate_rya = RdfToRyaConversions.convertURI((URI) p_v);
        RyaType object_rya = RdfToRyaConversions.convertValue(o_v);
        TriplePatternStrategy strategy = ryaContext.retrieveStrategy(subject_rya, predicate_rya, object_rya, null);
        if (strategy == null)
            return new RdfCloudTripleStoreUtils.CustomEntry<TABLE_LAYOUT, Range>(TABLE_LAYOUT.SPO, new Range());
        Map.Entry<TABLE_LAYOUT, ByteRange> entry = strategy.defineRange(subject_rya, predicate_rya, object_rya, null, null);
        ByteRange byteRange = entry.getValue();
        return new RdfCloudTripleStoreUtils.CustomEntry<mvm.rya.api.RdfCloudTripleStoreConstants.TABLE_LAYOUT, Range>(
                entry.getKey(), new Range(new Text(byteRange.getStart()), new Text(byteRange.getEnd()))
        );
    }

    protected void addInferredRanges(String tablePrefix, Job job) throws IOException {
        logger.info("Adding inferences to statement pattern[subject:" + subject_value + ", predicate:" + predicate_value + ", object:" + object_value + "]");
        //inference engine
        CloudbaseRyaDAO ryaDAO = new CloudbaseRyaDAO();
        CloudbaseRdfConfiguration rdfConf = new CloudbaseRdfConfiguration(job.getConfiguration());
        rdfConf.setTablePrefix(tablePrefix);
        ryaDAO.setConf(rdfConf);
        InferenceEngine inferenceEngine = new InferenceEngine();
        inferenceEngine.setConf(rdfConf);
        inferenceEngine.setRyaDAO(ryaDAO);
        inferenceEngine.setSchedule(false);
        try {
            if (!mock) {
                ryaDAO.setConnector(new ZooKeeperInstance(inst, zookeepers).getConnector(user, password.getBytes()));
            } else {
                ryaDAO.setConnector(new MockInstance(inst).getConnector(user, password.getBytes()));
            }

            ryaDAO.init();
            inferenceEngine.init();
            //is it subclassof or subpropertyof
            if (RDF.TYPE.equals(predicate_value)) {
                //try subclassof
                Collection<URI> parents = inferenceEngine.findParents(inferenceEngine.getSubClassOfGraph(), (URI) object_value);
                if (parents != null && parents.size() > 0) {
                    //subclassof relationships found
                    //don't add self, that will happen anyway later
                    //add all relationships
                    for (URI parent : parents) {
                        Map.Entry<TABLE_LAYOUT, Range> temp = createRange(subject_value, predicate_value, parent);
//                                queryRangeFactory.defineRange(subject_value, predicate_value, parent, rdfConf);
                        Range range = temp.getValue();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found subClassOf relationship [type:" + object_value + " is subClassOf:" + parent + "]");
                        }
                        addRange(range);
                    }
                }
            } else if (predicate_value != null) {
                //subpropertyof check
                Set<URI> parents = inferenceEngine.findParents(inferenceEngine.getSubPropertyOfGraph(), (URI) predicate_value);
                for (URI parent : parents) {
                    Map.Entry<TABLE_LAYOUT, Range> temp = createRange(subject_value, parent, object_value);
//                            queryRangeFactory.defineRange(subject_value, parent, object_value, rdfConf);
                    Range range = temp.getValue();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found subPropertyOf relationship [type:" + predicate_value + " is subPropertyOf:" + parent + "]");
                    }
                    addRange(range);
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (inferenceEngine != null) {
                try {
                    inferenceEngine.destroy();
                } catch (InferenceEngineException e) {
                    throw new IOException(e);
                }
            }

            if (ryaDAO != null)
                try {
                    ryaDAO.destroy();
                } catch (RyaDAOException e) {
                    throw new IOException(e);
                }
        }

    }

    @Override
    public Tuple getNext() throws IOException {
        try {
            if (reader.nextKeyValue()) {
                Key key = (Key) reader.getCurrentKey();
                cloudbase.core.data.Value value = (cloudbase.core.data.Value) reader.getCurrentValue();
                ByteArrayDataInput input = ByteStreams.newDataInput(key.getRow().getBytes());
                RyaStatement ryaStatement = ryaContext.deserializeTriple(layout, new TripleRow(key.getRow().getBytes(),
                        key.getColumnFamily().getBytes(), key.getColumnQualifier().getBytes()));
//                        RdfCloudTripleStoreUtils.translateStatementFromRow(input,
//                        key.getColumnFamily(), layout, RdfCloudTripleStoreConstants.VALUE_FACTORY);

                Tuple tuple = TupleFactory.getInstance().newTuple(4);
                tuple.set(0, ryaStatement.getSubject().getData());
                tuple.set(1, ryaStatement.getPredicate().getData());
                tuple.set(2, ryaStatement.getObject().getData());
                tuple.set(3, (ryaStatement.getContext() != null) ? (ryaStatement.getContext().getData()) : (null));
                return tuple;
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return null;
    }
}
