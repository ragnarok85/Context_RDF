package gob.cinvestav.mx.pte.ws;

import java.io.BufferedReader;
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

public class TargetHypernymDiscovery {

	public static void main(String... args){
		TargetHypernymDiscovery thd = new TargetHypernymDiscovery();
		//String sentence = "This existential question has been raised by a series of experiments conducted recently at the Large Hadron Collider and the Relativistic Heavy Ion Collider that smash various atomic particles together at nearly the speed of light in order to create tiny drops of primordial soup";
		String sentence = "The Charles Bridge is a famous historic bridge that crosses the Vltava river in Prague, Czech Republic.";
		
		try {
			thd.sendPostNE(sentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPostNE(String texto) throws Exception {

		HttpPost post = new HttpPost("https://entityclassifier.eu/thd/api/v2/extraction");
		HttpClient cliente = new DefaultHttpClient();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("apikey", "405cea20bcae46c68bab3f7558ccf133"));
		formparams.add(new BasicNameValuePair("text", texto));
		formparams.add(new BasicNameValuePair("format", "json"));

		// para mas parametros
		// http://www.alchemyapi.com/api/entity/textc.html
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

		post.setEntity(entity);
		System.out.println(post +"  " +entity);
		HttpResponse respons = cliente.execute(post);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(respons.getEntity().getContent()));
      String line = "";
      while ((line = rd.readLine()) != null) {
          //String[] lines = line.split("");
      	System.out.println(line);
      }
      System.out.println("something");

//		InputStreamReader input = new InputStreamReader(respons.getEntity().getContent());
//		JsonReader jrdr = Json.createReader(input);


	}


}
