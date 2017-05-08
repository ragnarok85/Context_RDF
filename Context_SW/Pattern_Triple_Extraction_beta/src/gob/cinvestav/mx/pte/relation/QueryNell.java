package gob.cinvestav.mx.pte.relation;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Logger;

public class QueryNell {
	
	static final String USER_AGENT = "Mozilla/5.0";
	final static Logger logger = Logger.getLogger(QueryNell.class);
	public static void main(String args[]){
		try {
			//sendGet("Ottawa","Canada");
			sendGet("Nintendo","satoru_iwata");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    public static String sendGet(String subject, String argument) throws Exception {
    	subject = subject.replaceAll(" ", "_");
    	argument = argument.replaceAll(" ", "_");
    	String relationReturn = "";
    	String agent = "KB";
    	String predicate = "*"; 
        String url = "http://rtw.ml.cmu.edu/rtw/api/json0?lit1="+subject+"&predicate="+predicate+"&lit2="+argument+"&agent="+agent;

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
        
        JsonArray items = generalObj.getJsonArray("items");
        
        if(!items.isEmpty()){
        	for(JsonObject item : items.getValuesAs(JsonObject.class)){
            	String[] entOne = item.getString("ent1").split(":");
            	logger.info(entOne[2]);
            	String[] entTwo = item.getString("ent2").split(":");
            	logger.info(entTwo[2]);
            	String relation = item.getString("predicate");
            	logger.info("**"+relation+"**");
            	//******************
            	relationReturn = relation;
            	//********************
            	
            	JsonArray justifications = item.getJsonArray("justifications");
            	if(!justifications.isEmpty()){
            		for(JsonObject justification : justifications.getValuesAs(JsonObject.class)){
            			double score = justification.getJsonNumber("score").doubleValue();
            			logger.info(score);
            			String agentJ = justification.getString("agent");
            			logger.info(agentJ);
            			String agentType = justification.getString("agentType");
            			logger.info(agentType);
            			String date = justification.getString("date");
            			logger.info(date);
            			int iteration = justification.getJsonNumber("iteration").intValue();
            			logger.info(iteration);
            			String comment = justification.getString("comment");
            			logger.info(comment);
            			int updateIteration = justification.getJsonNumber("updateIteration").intValue();
            			logger.info(updateIteration);
            		}
            	}
            }
        }
        
        
        in.close();

        //print result
        return relationReturn;
    }
}
