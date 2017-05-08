package gob.cinvestav.mx.pte.ws;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

public class AlchemyWS {
	
	private List<AlchemyEntities> namedEntities;

	
	public List<AlchemyEntities> extractEntities(String snts){
		namedEntities = new ArrayList<AlchemyEntities>();
		try {
			sendPostNE(snts);
			sendPostConcepts(snts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return namedEntities;
	}
	
	private void sendPostNE(String texto) throws Exception {
    	
        HttpPost post = new HttpPost("http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities");
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
        
//        System.out.println("status = " + obj.getString("status"));
//        System.out.println("usage = " + obj.getString("usage"));
//        System.out.println("url = " + obj.getString("url"));
//        System.out.println("language = " + obj.getString("language"));
//        System.out.println();
        for(JsonObject entt : entities.getValuesAs(JsonObject.class)){
        	AlchemyEntities namedEntity = new AlchemyEntities();
//        	System.out.println("type = " + entt.getString("type"));
//        	System.out.println("relevance = " + entt.getString("relevance"));
//        	System.out.println("count = " + entt.getString("count"));
//        	System.out.println("text = " + entt.getString("text"));
        	namedEntity.setType(entt.getString("type"));
        	namedEntity.setRelevance(Double.parseDouble(entt.getString("relevance")));
        	namedEntity.setCount(Integer.parseInt(entt.getString("count")));
        	namedEntity.setText(entt.getString("text"));
        	namedEntity.setDbpediaURL("http://dbpedia.org/resource/"+namedEntity.getText());
        	namedEntities.add(namedEntity);
        }
        System.out.println();
    }
    
    public void sendPostConcepts(String texto) throws Exception {

        HttpPost post = new HttpPost("http://gateway-a.watsonplatform.net/calls/text/TextGetRankedConcepts");
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
        	namedEntities.add(cncpt);
//        	System.out.println("================================");
        }
        
    }

}
