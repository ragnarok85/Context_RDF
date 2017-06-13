package gob.cinvestav.mx.pte.ws;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;


public class TopicWS {
	
	static final Logger logger = Logger.getLogger(TopicWS.class);
	
//	public List<String> queryAlchemy(String sentence) {
	public String queryAlchemy(String sentence) {
//		List<String> topic = new ArrayList<String>();
		String topic = "";
		HttpPost post = new HttpPost("http://gateway-a.watsonplatform.net/calls/text/TextGetRankedTaxonomy");
		@SuppressWarnings("resource")
		HttpClient cliente = new DefaultHttpClient();

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("apikey", "60ff19e9d37316b5e234ce576487566df76a457e"));
		formparams.add(new BasicNameValuePair("text", sentence));
		formparams.add(new BasicNameValuePair("outputMode", "json"));
		try {
			// para mas parametros
			// http://www.alchemyapi.com/api/entity/textc.html
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			
			post.setEntity(entity);

			HttpResponse respons = cliente.execute(post);
			InputStreamReader input = new InputStreamReader(respons.getEntity().getContent());
			JsonReader jrdr = Json.createReader(input);

			JsonObject obj = jrdr.readObject();
			JsonArray taxonomy = obj.getJsonArray("taxonomy");
			boolean first = true;
			if (taxonomy != null) {
				for (JsonObject tax : taxonomy.getValuesAs(JsonObject.class)) {
					String label = tax.getString("label");
					double score = Double.parseDouble(tax.getString("score"));
					if (first) {
//						topic = divideTopics(label);
						topic  = label;
						first = false;
					}
					// I consider only the first result because it is the best scored
					// break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return topic;
	}
	
//	private static List<String> divideTopics(String topic){
//		String[] splitTopic = topic.split("/");
//		List<String> topics = new ArrayList<String>();
//		for(String tpc : splitTopic){
//			if(!tpc.isEmpty()){
//				logger.info("topic - "  +tpc);
//				topics.add(tpc.replace(" ", "_"));
//			}
//			
//		}
//		return topics;
//	}

	public String queryOpenCalais(String sentence) {
		String topic = "";
		PostMethod method = new PostMethod("https://api.thomsonreuters.com/permid/calais");
		org.apache.commons.httpclient.HttpClient client;

		client = new org.apache.commons.httpclient.HttpClient();

		// Set mandatory parameters
		method.setRequestHeader("X-AG-Access-Token", "1UHsG2u10TuNnwtVm1P3XI69kSvQeA67");
		// Set input content type
		method.setRequestHeader("Content-Type", "text/raw");
		// Set response/output format
		method.setRequestHeader("outputformat", "application/json");

		method.setRequestEntity(new StringRequestEntity(sentence));

		try {
			int returnCode = client.executeMethod(method);
			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				method.getResponseBodyAsString();
			} else if (returnCode == HttpStatus.SC_OK) {
				System.out.println("Sentence post succeeded: " + sentence);
				InputStreamReader input = new InputStreamReader(method.getResponseBodyAsStream());
				JsonReader jrdr = Json.createReader(input);

				JsonObject jobj = jrdr.readObject();

				Set<String> indexes = jobj.keySet();
				boolean first = true;
				for (String index : indexes) {
					// System.out.println(index);
					JsonObject obj = jobj.getJsonObject(index);
					// System.out.println(obj + " " +
					// obj.containsKey("_typeGroup"));
					if (obj.containsKey("_typeGroup")
							&& obj.getJsonString("_typeGroup").toString().contains("topics")) {
						String name = obj.getString("name");
						double score = obj.getJsonNumber("score").doubleValue();
						if (first) {
							topic = name;
							first = false;
						}

					}

				}

			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return topic;

	}
}
