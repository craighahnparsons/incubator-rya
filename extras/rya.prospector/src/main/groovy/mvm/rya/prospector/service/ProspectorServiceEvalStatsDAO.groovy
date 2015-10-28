package mvm.rya.prospector.service

import mvm.rya.api.RdfCloudTripleStoreConfiguration
import mvm.rya.api.persist.RdfEvalStatsDAO
import mvm.rya.prospector.domain.TripleValueType
import mvm.rya.prospector.utils.ProspectorConstants
import org.apache.hadoop.conf.Configuration
import org.openrdf.model.Resource
import org.openrdf.model.Value

import mvm.rya.api.persist.RdfEvalStatsDAO.CARDINALITY_OF

/**
 * An ${@link mvm.rya.api.persist.RdfEvalStatsDAO} that uses the Prospector Service underneath return counts.
 */
class ProspectorServiceEvalStatsDAO implements RdfEvalStatsDAO<RdfCloudTripleStoreConfiguration> {

    def ProspectorService prospectorService

    ProspectorServiceEvalStatsDAO() {
    }

    ProspectorServiceEvalStatsDAO(ProspectorService prospectorService, RdfCloudTripleStoreConfiguration conf) {
        this.prospectorService = prospectorService
    }

    public ProspectorServiceEvalStatsDAO(def connector, RdfCloudTripleStoreConfiguration conf) {
        this.prospectorService = new ProspectorService(connector, getProspectTableName(conf))
    }

    @Override
    void init() {
        assert prospectorService != null
    }

    @Override
    boolean isInitialized() {
        return prospectorService != null
    }

    @Override
    void destroy() {

    }

	@Override
    public double getCardinality(RdfCloudTripleStoreConfiguration conf, CARDINALITY_OF card, List<Value> val) {

        assert conf != null && card != null && val != null
        String triplePart = null;
        switch (card) {
            case (CARDINALITY_OF.SUBJECT):
                triplePart = TripleValueType.subject
                break;
            case (CARDINALITY_OF.PREDICATE):
                triplePart = TripleValueType.predicate
                break;
            case (CARDINALITY_OF.OBJECT):
                triplePart = TripleValueType.object
                break;
            case (CARDINALITY_OF.SUBJECTPREDICATE):
                triplePart = TripleValueType.subjectpredicate
                break;
             case (CARDINALITY_OF.SUBJECTOBJECT):
                triplePart = TripleValueType.subjectobject
                break;
             case (CARDINALITY_OF.PREDICATEOBJECT):
                triplePart = TripleValueType.predicateobject
                break;
       }

        String[] auths = conf.getAuths()
		List<String> indexedValues = new ArrayList<String>();
		Iterator<Value> valueIt = val.iterator();
		while (valueIt.hasNext()){
			indexedValues.add(valueIt.next().stringValue());
		}

        def indexEntries = prospectorService.query(null, ProspectorConstants.COUNT, triplePart, indexedValues, null /** what is the datatype here? */,
                auths)

        return indexEntries.size() > 0 ? indexEntries.head().count : -1
    }

	@Override
	double getCardinality(RdfCloudTripleStoreConfiguration conf, CARDINALITY_OF card, List<Value> val, Resource context) {
		return getCardinality(conf, card, val) //TODO: Not sure about the context yet
	}

    @Override
    public void setConf(RdfCloudTripleStoreConfiguration conf) {

    }

    @Override
    RdfCloudTripleStoreConfiguration getConf() {
        return null
    }

    public static String getProspectTableName(RdfCloudTripleStoreConfiguration conf) {
        return conf.getTablePrefix() + "prospects";
    }
}
