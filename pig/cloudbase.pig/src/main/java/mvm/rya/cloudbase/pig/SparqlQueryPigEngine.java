package mvm.rya.cloudbase.pig;

import cloudbase.core.client.Connector;
import cloudbase.core.client.ZooKeeperInstance;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import mvm.rya.cloudbase.CloudbaseRdfConfiguration;
import mvm.rya.cloudbase.CloudbaseRdfEvalStatsDAO;
import mvm.rya.cloudbase.CloudbaseRyaDAO;
import mvm.rya.cloudbase.pig.optimizer.SimilarVarJoinOptimizer;
import mvm.rya.rdftriplestore.evaluation.QueryJoinOptimizer;
import mvm.rya.rdftriplestore.evaluation.RdfCloudTripleStoreEvaluationStatistics;
import mvm.rya.rdftriplestore.inference.InferenceEngine;
import mvm.rya.rdftriplestore.inference.InverseOfVisitor;
import mvm.rya.rdftriplestore.inference.SymmetricPropertyVisitor;
import mvm.rya.rdftriplestore.inference.TransitivePropertyVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/23/12
 * Time: 9:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class SparqlQueryPigEngine {
    private static final Log logger = LogFactory.getLog(SparqlQueryPigEngine.class);

    private String hadoopDir;
    private ExecType execType = ExecType.MAPREDUCE; //default to mapreduce
    private boolean inference = true;
    private boolean stats = true;
    private SparqlToPigTransformVisitor sparqlToPigTransformVisitor;
    private PigServer pigServer;
    private InferenceEngine inferenceEngine = null;
    private RdfCloudTripleStoreEvaluationStatistics rdfCloudTripleStoreEvaluationStatistics;
    private CloudbaseRyaDAO ryaDAO;
    private CloudbaseRdfEvalStatsDAO rdfEvalStatsDAO;

    public CloudbaseRdfConfiguration getConf() {
        return conf;
    }

    public void setConf(CloudbaseRdfConfiguration conf) {
        this.conf = conf;
    }

    CloudbaseRdfConfiguration conf = new CloudbaseRdfConfiguration();

    public void init() throws Exception {
        Preconditions.checkNotNull(sparqlToPigTransformVisitor, "Sparql To Pig Transform Visitor must not be null");
        logger.info("Initializing Sparql Query Pig Engine");
        if (hadoopDir != null) {
            //set hadoop dir property
            System.setProperty("HADOOPDIR", hadoopDir);
        }
        //TODO: Maybe have validation of the HadoopDir system property

        if (pigServer == null) {
            pigServer = new PigServer(execType);
        }

        if (inference || stats) {
            Connector connector = new ZooKeeperInstance("stratus", "stratus13:2181").getConnector("root", "password".getBytes());

            String tablePrefix = sparqlToPigTransformVisitor.getTablePrefix();
            conf.setTablePrefix(tablePrefix);
            if (inference) {
                logger.info("Using inference");
                inferenceEngine = new InferenceEngine();
                ryaDAO = new CloudbaseRyaDAO();
                ryaDAO.setConf(conf);
                ryaDAO.setConnector(connector);
                ryaDAO.init();

                inferenceEngine.setRyaDAO(ryaDAO);
                inferenceEngine.setConf(conf);
                inferenceEngine.setSchedule(false);
                inferenceEngine.init();
            }
            if (stats) {
                logger.info("Using stats");
                rdfEvalStatsDAO = new CloudbaseRdfEvalStatsDAO();
                rdfEvalStatsDAO.setConf(conf);
                rdfEvalStatsDAO.setConnector(connector);
//                rdfEvalStatsDAO.setEvalTable(tablePrefix + RdfCloudTripleStoreConstants.TBL_EVAL_SUFFIX);
                rdfEvalStatsDAO.init();
                rdfCloudTripleStoreEvaluationStatistics = new RdfCloudTripleStoreEvaluationStatistics(conf, rdfEvalStatsDAO);
            }
        }
    }

    public void destroy() throws Exception {
        logger.info("Shutting down Sparql Query Pig Engine");
        pigServer.shutdown();
        if (ryaDAO != null) {
            ryaDAO.destroy();
        }
        if (inferenceEngine != null) {
            inferenceEngine.destroy();
        }
        if (rdfEvalStatsDAO != null) {
            rdfEvalStatsDAO.destroy();
        }
    }

    /**
     * Transform a sparql query into a pig script and execute it. Save results in hdfsSaveLocation
     *
     * @param sparql           to execute
     * @param hdfsSaveLocation to save the execution
     * @throws IOException
     */
    public void runQuery(String sparql, String hdfsSaveLocation) throws IOException {
        Preconditions.checkNotNull(sparql, "Sparql query cannot be null");
        Preconditions.checkNotNull(hdfsSaveLocation, "Hdfs save location cannot be null");
        logger.info("Running query[" + sparql + "]\n to Location[" + hdfsSaveLocation + "]");
        pigServer.deleteFile(hdfsSaveLocation);
        try {
            String pigScript = generatePigScript(sparql);
            if (logger.isDebugEnabled()) {
                logger.debug("Pig script [" + pigScript + "]");
            }
            pigServer.registerScript(new ByteArrayInputStream(pigScript.getBytes()));
            pigServer.store("PROJ", hdfsSaveLocation); //TODO: Make this a constant
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String generatePigScript(String sparql) throws Exception {
        Preconditions.checkNotNull(sparql, "Sparql query cannot be null");
        QueryParser parser = new SPARQLParser();
        ParsedQuery parsedQuery = parser.parseQuery(sparql, null);
        QueryRoot tupleExpr = new QueryRoot(parsedQuery.getTupleExpr());

        SimilarVarJoinOptimizer similarVarJoinOptimizer = new SimilarVarJoinOptimizer();
        similarVarJoinOptimizer.optimize(tupleExpr, null, null);

        if (inference || stats) {
            if (inference) {
                tupleExpr.visit(new TransitivePropertyVisitor(conf, inferenceEngine));
                tupleExpr.visit(new SymmetricPropertyVisitor(conf, inferenceEngine));
                tupleExpr.visit(new InverseOfVisitor(conf, inferenceEngine));
            }
            if (stats) {
                (new QueryJoinOptimizer(rdfCloudTripleStoreEvaluationStatistics)).optimize(tupleExpr, null, null);
            }
        }

        sparqlToPigTransformVisitor.meet(tupleExpr);
        return sparqlToPigTransformVisitor.getPigScript();
    }

    public static void main(String[] args) {
        try {
            Preconditions.checkArgument(args.length == 7, "Usage: java -cp <jar>:$PIG_LIB <class> sparqlFile hdfsSaveLocation cbinstance cbzk cbuser cbpassword rdfTablePrefix.\n " +
                    "Sample command: java -cp java -cp cloudbase.pig-2.0.0-SNAPSHOT-shaded.jar:/usr/local/hadoop-etc/hadoop-0.20.2/hadoop-0.20.2-core.jar:/srv_old/hdfs-tmp/pig/pig-0.9.2/pig-0.9.2.jar:$HADOOP_HOME/conf mvm.rya.cloudbase.pig.SparqlQueryPigEngine tstSpqrl.query temp/engineTest stratus stratus13:2181 root password l_");
            String sparql = new String(ByteStreams.toByteArray(new FileInputStream(args[0])));
            String hdfsSaveLocation = args[1];
            SparqlToPigTransformVisitor visitor = new SparqlToPigTransformVisitor();
            visitor.setTablePrefix(args[6]);
            visitor.setInstance(args[2]);
            visitor.setZk(args[3]);
            visitor.setUser(args[4]);
            visitor.setPassword(args[5]);

            SparqlQueryPigEngine engine = new SparqlQueryPigEngine();
            engine.setSparqlToPigTransformVisitor(visitor);
            engine.init();

            engine.runQuery(sparql, hdfsSaveLocation);

            engine.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHadoopDir() {
        return hadoopDir;
    }

    public void setHadoopDir(String hadoopDir) {
        this.hadoopDir = hadoopDir;
    }

    public PigServer getPigServer() {
        return pigServer;
    }

    public void setPigServer(PigServer pigServer) {
        this.pigServer = pigServer;
    }

    public ExecType getExecType() {
        return execType;
    }

    public void setExecType(ExecType execType) {
        this.execType = execType;
    }

    public boolean isInference() {
        return inference;
    }

    public void setInference(boolean inference) {
        this.inference = inference;
    }

    public boolean isStats() {
        return stats;
    }

    public void setStats(boolean stats) {
        this.stats = stats;
    }

    public SparqlToPigTransformVisitor getSparqlToPigTransformVisitor() {
        return sparqlToPigTransformVisitor;
    }

    public void setSparqlToPigTransformVisitor(SparqlToPigTransformVisitor sparqlToPigTransformVisitor) {
        this.sparqlToPigTransformVisitor = sparqlToPigTransformVisitor;
    }
}
