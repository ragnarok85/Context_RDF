package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import gob.cinvestav.mx.pte.relation.QueryLOV;
import gob.cinvestav.mx.pte.relation.QueryNell;
import gob.cinvestav.mx.pte.relation.QueryWatson;
import gob.cinvestav.mx.pte.type.QueryWiBi;
import gob.cinvestav.mx.pte.utils.Utils;

public class UtilsWS {
	
	static final Logger logger = Logger.getLogger(UtilsWS.class);
	
	public static List<String> lovUris = new ArrayList<String>();
	
	public static String lookRelation(String subject, String predicate, String argument) {
		String uri = "";

		uri = queryNELL(subject, argument);
//		if(uri.contains("http"))
//			System.out.println("NELL result = " + uri);
//		else{
//			List<String> luri = queryLOV(predicate);
//			if(luri != null && !luri.isEmpty()){
//				//luri.get(1) = http://....
//				uri =luri.get(1);
//				if(uri.contains("http")){
//					System.out.println("LOV result = " + uri);
//					//luri.get(0) = nms:id
//					String[] parts = luri.get(0).split(":");
//					lovUris.add(parts[0]+";"+uri.substring(0,uri.length()-parts[1].length()));
//				}
//			}
//			else if(luri == null || luri.isEmpty()){
		if (uri.contains("http"))
			logger.info("NELL result = " + uri);

		if (uri.length() == 0) {
			List<String> luri = new ArrayList<String>();
			//luri = queryWatson(predicate); // watson's web service is not working
			if (luri.size() > 0) {
				Iterator<String> iter = luri.iterator();
				uri = iter.next();
				if (uri.contains("http")) {
					System.out.println("Watson's result = " + uri);
					// luri.get(0) = nms:id
				}
			}
		}
		if(uri.length() == 0 ){
			List<String> luri = queryLOV(predicate);
			if(luri != null && !luri.isEmpty()){
				//luri.get(1) = http://....
				uri =luri.get(1);
				if(uri.contains("http")){
					logger.info("LOV result = " + uri);
					//luri.get(0) = nms:id
					String[] parts = luri.get(0).split(":");
					lovUris.add(parts[0]+";"+uri.substring(0,uri.length()-parts[1].length()));
				}
			}
		}
		
		if (uri.length() == 0 && Utils.predicateRestrictions(predicate)) {
			predicate = predicate.replaceAll(" ", "_");
			uri = "http://www.cinvestav.com.mx/rdf/#" + predicate;
			logger.info("The default uri will be = " + uri);
		}
		
		if(uri.length() == 0){
			uri = "http://www.cinvestav.com.mx/rdf/#RelatedWith";
			logger.info("The default uri will be = " + uri);
		}

		return uri;
	}
	
	public static String queryNELL(String subject, String argument) {
		String pred = "";
		try {
			pred = QueryNell.sendGet(subject, argument);
			if(pred.length() > 0)
				pred = "http://www.cinvestav.com.mx/rdf/#" + pred;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pred;
	}

	public static List<String> queryLOV(String predicate) {
		List<String> uri = null;
		try {
			uri = QueryLOV.sendGet(predicate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public static List<String> queryWatson(String predicate) {
		List<String> uri = null;
		try {
			uri = QueryWatson.asearch(predicate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public static List<String> queryWiBi(String concept){
		List<String> wibiTypes = new ArrayList<String>();
		
		try {
			wibiTypes = QueryWiBi.sendGet(concept);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(wibiTypes.isEmpty()){
			logger.info("WiBi does not return something for: " + concept);
		}
		
		return wibiTypes;
	}

}
