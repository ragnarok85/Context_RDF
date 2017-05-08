package gob.cinvestav.mx.pte.relation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.open.kmi.watson.clientapi.EntityResult;
import uk.ac.open.kmi.watson.clientapi.EntitySearch;
import uk.ac.open.kmi.watson.clientapi.EntitySearchServiceLocator;
import uk.ac.open.kmi.watson.clientapi.SearchConf;

public class QueryWatson {
	
	
	
	
	public QueryWatson() {
		
	}
	
	static public List<String> asearch(String predicate) throws RemoteException {
		EntitySearch es = null;
		EntitySearchServiceLocator locator = new EntitySearchServiceLocator();

		try {
			es = locator.getUrnEntitySearch();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
		
		System.out.println("***** Searching properties match for \""+ predicate+"\" *****");
		List<String> newProperty = new ArrayList<String>();
		SearchConf conf = new SearchConf();
		conf.setScope(SearchConf.LOCAL_NAME+SearchConf.LABEL);
//		conf.setEntities(SearchConf.CLASS+SearchConf.PROPERTY+SearchConf.INDIVIDUAL);
		conf.setEntities(SearchConf.PROPERTY);
		conf.setMatch(SearchConf.TOKEN_MATCH);
		conf.setInc(10);
		EntityResult[] res = es.getAnyEntityByKeyword(predicate, conf);
		for (EntityResult s : res){
			newProperty.add(s.getURI());
			System.out.println(s.getURI() + " in " + s.getSCURI());
		}
		
		return newProperty;
	}
}
