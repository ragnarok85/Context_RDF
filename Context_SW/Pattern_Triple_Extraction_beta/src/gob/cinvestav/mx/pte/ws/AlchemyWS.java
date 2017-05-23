package gob.cinvestav.mx.pte.ws;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import gob.cinvestav.mx.pte.jena.Utility;

@SuppressWarnings("deprecation")
public class AlchemyWS {
	
	private Set<AlchemyEntities> namedEntities;

	
	public Set<AlchemyEntities> extractEntities(String snts){
		namedEntities = new HashSet<AlchemyEntities>();
		try {
//			sendPostNE(snts);
			sendPostConcepts(snts);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return reduceEntities(namedEntities);
		return namedEntities;
	}
	
	private void sendPostNE(String texto) throws Exception {
    	
        HttpPost post = new HttpPost("http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities");
		@SuppressWarnings("resource")
		HttpClient cliente = new DefaultHttpClient();
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        formparams.add(new BasicNameValuePair("apikey", "60ff19e9d37316b5e234ce576487566df76a457e"));
        formparams.add(new BasicNameValuePair("text", texto));
        formparams.add(new BasicNameValuePair("outputMode", "json"));

        //para mas parametros
        //http://www.alchemyapi.com/api/entity/textc.html
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        post.setEntity(entity);

        HttpResponse respons = cliente.execute(post);

        InputStreamReader input = new InputStreamReader(respons.getEntity().getContent());
        JsonReader jrdr = Json.createReader(input);
        
        JsonObject obj = jrdr.readObject();
        JsonArray entities = obj.getJsonArray("entities");
        String dbpediaURI = "";
//        System.out.println("status = " + obj.getString("status"));
//        System.out.println("usage = " + obj.getString("usage"));
//        System.out.println("url = " + obj.getString("url"));
//        System.out.println("language = " + obj.getString("language"));
//        System.out.println();
        for(JsonObject entt : entities.getValuesAs(JsonObject.class)){
        	String text = entt.getString("text");
        	dbpediaURI = Utility.queryNEDBpedia(text);
        	if(dbpediaURI.contains("http:")){
        		AlchemyEntities namedEntity = new AlchemyEntities();
//            	System.out.println("type = " + entt.getString("type"));
//            	System.out.println("relevance = " + entt.getString("relevance"));
//            	System.out.println("count = " + entt.getString("count"));
//            	System.out.println("text = " + entt.getString("text"));
            	namedEntity.setType(entt.getString("type"));
            	namedEntity.setRelevance(Double.parseDouble(entt.getString("relevance")));
            	namedEntity.setCount(Integer.parseInt(entt.getString("count")));
            	namedEntity.setText(text);
            	namedEntity.setDbpediaURL(dbpediaURI);
            	namedEntities.add(namedEntity);
        	}
        }
        System.out.println();
    }
    
    public void sendPostConcepts(String texto) throws Exception {

        HttpPost post = new HttpPost("http://gateway-a.watsonplatform.net/calls/text/TextGetRankedConcepts");
		@SuppressWarnings("resource")
		HttpClient cliente = new DefaultHttpClient();
		
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        formparams.add(new BasicNameValuePair("apikey", "60ff19e9d37316b5e234ce576487566df76a457e"));
        formparams.add(new BasicNameValuePair("text", texto));
        formparams.add(new BasicNameValuePair("outputMode", "json"));

        //para mas parametros
        //http://www.alchemyapi.com/api/entity/textc.html
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        post.setEntity(entity);

        HttpResponse respons = cliente.execute(post);

        InputStreamReader in =  new InputStreamReader(respons.getEntity().getContent());
        JsonReader jrd = Json.createReader(in);
        
        JsonObject obj = jrd.readObject();
        JsonArray concepts = obj.getJsonArray("concepts");
        
//        System.out.println("status = " + obj.getString("status") );
//        System.out.println("usage = " + obj.getString("usage") );
//        System.out.println("language = " + obj.getString("language") );
//        System.out.println();
        for(JsonObject concept : concepts.getValuesAs(JsonObject.class)){
        	AlchemyEntities cncpt = new AlchemyEntities();
//        	System.out.println("text = " + concept.getString("text"));
//        	System.out.println("relevance = " + concept.getString("relevance"));
        	cncpt.setText(concept.getString("text"));
        	
        	cncpt.setRelevance(Double.parseDouble(concept.getString("relevance")));
        	if(concept.containsKey("dbpedia")){
//        		System.out.println("dbpedia = " + concept.getString("dbpedia"));
        		cncpt.setDbpediaURL( concept.getString("dbpedia"));
        	}
        	if(concept.containsKey("freebase")){
//        		System.out.println("freebase = " + concept.getString("freebase"));
        		cncpt.setFreebaseURL( concept.getString("freebase"));
        	}
        	cncpt.setListPosition(findIndexes(texto,cncpt));
        	namedEntities.add(cncpt);
//        	System.out.println("================================");
        }
        
    }
    
    public static List<AlchemyEntities> reduceEntities(List<AlchemyEntities> entityList){
    	List<AlchemyEntities> endResult = new ArrayList<AlchemyEntities>();
    	boolean isContained = false;
    	
    	for(AlchemyEntities entityOne : entityList){
    		for(AlchemyEntities entityTwo : entityList){
    			if(entityTwo.text.contains(" ") && !entityOne.text.contains(" ") && entityTwo.text.contains(entityOne.text)){
    				isContained = true;
    				break;
    			}
    		}
    		if(!isContained){
				endResult.add(entityOne);
				System.out.println(entityOne.text);
			}
			isContained=false;
    	}
    	return endResult;
    }
    
	public static List<Integer> findIndexes(String text, AlchemyEntities entity) {
		List<Integer> indexes = new ArrayList<Integer>();
		String pattern = "\\b" + entity.text.toLowerCase() + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		if (m.find()) {
			indexes.add(m.start());
			indexes.add(m.end());
		}
		return indexes;
	}
    
    public static void main(String args[]){
    	AlchemyWS ws = new AlchemyWS();
    	String text = "a computer programmer is a person who makes computer programs using a programming language";
    	String textCopy = text;
    	Set<AlchemyEntities> entityList = ws.extractEntities(text);
    	
    	for(AlchemyEntities entity : entityList){
	    	text = text.replaceAll("\\b"+entity.text.toLowerCase()+"\\b", "<"+entity.dbpediaURL+">");
	    	if(entity.getListPosition().size() > 0){
	    		for(Integer position:entity.getListPosition()){
		    		System.out.print(position+"-");
		    	}
		    	System.out.println();
	    	}
	    	
    	}
    	System.out.println(text);
    	
      
    	
    }

}
