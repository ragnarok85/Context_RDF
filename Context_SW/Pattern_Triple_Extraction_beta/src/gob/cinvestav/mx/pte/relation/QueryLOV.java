package gob.cinvestav.mx.pte.relation;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class QueryLOV {
	
static final String USER_AGENT = "Mozilla/5.0";
	
	public static void main(String args[]){
		try {
			//sendGet("Ottawa","Canada");
			sendGet("is based");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    public static List<String> sendGet(String predicate) throws Exception {
    	List<String> predUri = new ArrayList<String>();
    	predicate = predicate.replaceAll(" ", "%20");
    	//URL uris = new URL("http://lov.okfn.org/dataset/lov/api/v2/term/search?q="+predicate+"&type=property");
    	
        String url = "http://lov.okfn.org/dataset/lov/api/v2/term/search?q="+predicate+"&type=property";
       // System.out.println(url);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        
//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);

//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
        InputStreamReader in = new InputStreamReader(con.getInputStream());
        JsonReader reader = Json.createReader(in);
        
        JsonObject generalObj = reader.readObject();
        
        JsonArray results = generalObj.getJsonArray("results");
        
        String[] prefixedName;
        String[] vocabularyPrefixed;
        String[] uri;
        double score = 0.0d;
        String[] highlight;
        
        if(!results.isEmpty()){
        	for(JsonObject item : results.getValuesAs(JsonObject.class)){
        		JsonArray prefixN = item.getJsonArray("prefixedName");
        		prefixedName = new String[prefixN.size()];
        		for(int i = 0 ; i < prefixN.size() ; i++){
        			prefixedName[i] = prefixN.getString(i);
        			predUri.add(prefixedName[i]);
        			//System.out.println(prefixedName[i]);
        		}
        		JsonArray vocabularyPrefix = item.getJsonArray("vocabulary.prefix");
        		vocabularyPrefixed = new String[vocabularyPrefix.size()];
        		for(int i = 0 ; i < vocabularyPrefix.size() ; i++){
        			vocabularyPrefixed[i] = vocabularyPrefix.getString(i);
        			//System.out.println(vocabularyPrefixed[i]);
        		}
        		JsonArray urlPre = item.getJsonArray("uri");
        		
        		uri = new String[urlPre.size()];
        		for(int i = 0; i < urlPre.size() ; i++){
        			uri[i] = urlPre.getString(i);
        			predUri.add(uri[i]);
        			//System.out.println(uri[i]);
        		}
        		score = item.getJsonNumber("score").doubleValue();
        		JsonObject highlights = item.getJsonObject("highlight");
        		JsonArray h = highlights.getJsonArray("http://www.w3.org/2000/01/rdf-schema#label@en");
        		if(h != null && !h.isEmpty()){
        		highlight = new String[h.size()];
        			for(int i = 0; i < h.size(); i++){
            			highlight[i] = h.getString(i);
            			//System.out.println(highlight[i]);
            		}
        		}
        		
        		break;
            }
        }
        
        
        in.close();

        return predUri;
    }

}
