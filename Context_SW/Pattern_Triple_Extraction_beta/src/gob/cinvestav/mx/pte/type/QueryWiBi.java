package gob.cinvestav.mx.pte.type;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;

public class QueryWiBi {

	static final String USER_AGENT = "Mozilla/5.0";
	
	final static Logger logger = Logger.getLogger(QueryWiBi.class);

	public static void main(String args[]) {
		try {
			// sendGet("Ottawa","Canada");
			sendGet("experiment");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> sendGet(String concept) throws Exception {
		List<String> relationReturn = new ArrayList<String>();
		concept = concept.replaceAll(" ","_");
		concept = concept.substring(0,1).toUpperCase() + concept.substring(1);
		//concept = concept.substring(0,1).toUpperCase() + concept.substring(1);
		String url = "http://www.wibitaxonomy.org/rdf/?type=page&item=" + concept + "&lang=EN&pageH=2&cateH=3&output=xml";
//		logger.info(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);

		// BufferedReader in = new BufferedReader(
		// new InputStreamReader(con.getInputStream()));
		// String line = "";
		// while((line = in.readLine())!= null){
		// System.out.println(line);
		// }

		// in.close();

		// Create an empty model
		Model model = ModelFactory.createDefaultModel();

		InputStream in = con.getInputStream();
		
		if(in != null && responseCode == 200){
			// read the ttl stream
			try{
				model.read(in, "RDF/XML");

				// write the model
				//model.write(System.out);
				StmtIterator iter = model
						.listStatements(new SimpleSelector(model.createResource("http://wibitaxonomy.org/"+concept),
								model.getProperty("http://www.w3.org/2004/02/skos/core#broader"), (RDFNode) null));

				if (iter.hasNext()) {
					logger.info("content: ");
					while (iter.hasNext()) {
						relationReturn.add(iter.next().getResource().getURI());
					}
					logger.info("   " + relationReturn);
				}
			}catch(RiotException e){
				//System.out.println("WiBi - nothing return for: ");
			}
			
		}else{
			
		}
		
		// print result
		return relationReturn;
	}

}
