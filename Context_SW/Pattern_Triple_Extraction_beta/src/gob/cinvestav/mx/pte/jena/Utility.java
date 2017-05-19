package gob.cinvestav.mx.pte.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.Logger;

import gob.cinvestav.mx.pte.clausie.ClausieTriple;

public class Utility {
	final static Logger logger = Logger.getLogger(Utility.class);

	Model jenaModel = ModelFactory.createDefaultModel();
	String namespace = "http://www.cinvestav.com.mx/rdf/#";
//	String wibiNamespace = "http://wibitaxonomy.org/";
//	String dbr = "http://dbpedia.org/resource/";
	String owlNameSpace = "http://www.w3.org/2002/07/owl#";
	String ownNameSpace = "http://tamps.cinvestav.mx/rdf/resources/";
	Property sameAs;
	Property rdfType;
	
	public Utility() {
//		jenaModel.setNsPrefix("cinvestav", namespace);
//		jenaModel.setNsPrefix("dbr", dbr);
//		jenaModel.setNsPrefix("wibi", wibiNamespace);
		jenaModel.setNsPrefix("owl", owlNameSpace);
		jenaModel.setNsPrefix("cinves", "http://tamps.cinvestav.mx/rdf/#");
		sameAs = jenaModel.createProperty(owlNameSpace, "sameAs");
		rdfType = jenaModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	}
	
	public static void fuseSeeds(String seedFolder, List<String> allSeeds){
		File folder = new File(seedFolder);
		File[] seedFiles = folder.listFiles();
		
		for(File seedFile :seedFiles){
			try(BufferedReader br = new BufferedReader(new FileReader(seedFile))){
				String line  ="";
				while((line = br.readLine()) != null){
					if(!allSeeds.contains(line)){
						allSeeds.add(line);
					}
				}
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public Model getJenaModel(){
		return this.jenaModel;
	}
	
	public void publicLovNameSpace(List<String> lovUris){
		for(String nms : lovUris){
			//System.out.println("lovUris = " + nms);
			//nms = nms;http://....
			String[] parts = nms.split(";");
//			System.out.println("parts[0,1] = " + parts[0] + " " + parts[1]); 
			jenaModel.setNsPrefix(parts[0], parts[1]);
		}
	}
	
	public void populateModel(ClausieTriple triple, String rdfModelFileName) {
		String graphURI = "http://tamps.cinvestav.mx/rdf/graph/";
		Property inDocprop = jenaModel.createProperty("http://tamps.cinvestav.mx/rdf/#inDoc");
		Property inSntprop = jenaModel.createProperty("http://tamps.cinvestav.mx/rdf/#inSentence");
		for (String sbjUri : triple.getTriple().getSubjectUris()) {
			if (sbjUri.length() > 0) {
				String sbj = triple.getSubject().getTextNE().replace(" ", "_");
				Resource subject = jenaModel.createResource(ownNameSpace+sbj);
				jenaModel.add(subject,inDocprop,(RDFNode)jenaModel.createResource(graphURI+rdfModelFileName));
				jenaModel.add(subject,sameAs,jenaModel.createResource(sbjUri));
				jenaModel.add(subject,inSntprop,jenaModel.createLiteral(triple.getOrgSentence()));
				for (String objUri : triple.getTriple().getArgumentUris()) {
					if (objUri.length() > 0) {
						String obj = triple.getArgument().getTextNE().replace(" ", "_");
						Resource object = jenaModel.createResource(ownNameSpace+obj);
						jenaModel.add(object,inDocprop,(RDFNode)jenaModel.createResource(graphURI+rdfModelFileName));
						jenaModel.add(object,inSntprop,jenaModel.createLiteral(triple.getOrgSentence()));
						jenaModel.add(object,sameAs,jenaModel.createResource(objUri));
						logger.info("Property: " + triple.getTriple().getRelationUri());
						Property property = jenaModel.createProperty(triple.getTriple().getRelationUri());
						jenaModel.add(subject, property, object);
					}
				}
			}
		}
	}
   	
	public void populateTypes(ClausieTriple triple) {
		if (!triple.getSubject().getWibi().getUris().isEmpty()) {
			for (@SuppressWarnings("unused") String sbjUri : triple.getTriple().getSubjectUris()) {
				String sbj = triple.getSubject().getTextNE().replace(" ", "_");
				Resource subject = jenaModel.createResource(ownNameSpace+sbj);
				//jenaModel.add(subject,sameAs,sbjUri);
				for (String wibiUri : triple.getSubject().getWibi().getUris()) {
					Resource object = jenaModel.createResource(wibiUri);
					jenaModel.add(subject, rdfType, object);
				}
			}
		}
		if (!triple.getArgument().getWibi().getUris().isEmpty()) {
			for (@SuppressWarnings("unused") String argUri : triple.getTriple().getArgumentUris()) {
				String obj = triple.getArgument().getTextNE().replace(" ", "_");
				Resource argument = jenaModel.createResource(ownNameSpace+obj);
				//jenaModel.add(argument,sameAs,argUri);
				for (String wibiUri : triple.getArgument().getWibi().getUris()) {
					Resource object = jenaModel.createResource(wibiUri);
					jenaModel.add(argument, rdfType, object);
				}
			}
		}
	}
	
	public static void printTriples(List<String> triples, String output){
		try(PrintWriter pw = new PrintWriter(new FileWriter(output))){
			for(String triple: triples){
				pw.write(triple + "\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void addRdfsComment(String comment){
		jenaModel.add(jenaModel.createResource(namespace + "OrgSentence"),
				jenaModel.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"),
				jenaModel.createLiteral(comment));
	}

	public void writeTriple(String path) {
		try {
			FileWriter fos = new FileWriter(path);
			jenaModel.write(fos, "RDF/XML");
			fos.close();
			//jenaModel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void writeProblematicSentences(String problematicSentencesOuput,Map<String,List<String>> problematicSentences){
		try(PrintWriter pw = new PrintWriter(new FileWriter(problematicSentencesOuput))){
			for(String key : problematicSentences.keySet()){
				for(String problematicSentence : problematicSentences.get(key)){
					String[] splitProblematicSentence = problematicSentence.split("********");
					if(splitProblematicSentence.length == 2){
						pw.write(key + "\t" + splitProblematicSentence[0] + "\t" + splitProblematicSentence[1] + "\n");
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void getSeeds(List<String> seeds){
		StmtIterator iter = jenaModel.listStatements(new SimpleSelector(null, null, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (!s.getPredicate().toString().contains("http://www.w3.org/2000/01/rdf-schema#comment")
						|| !s.getPredicate().toString().contains("http://www.w3.org/2002/07/owl#sameAs"));
			}
		});
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			String[] subject = stmt.getSubject().toString().split("/");
			String[] object = stmt.getObject().toString().split("/");

			String sbj = subject[subject.length - 1].toLowerCase();
			String obj = object[object.length - 1].toLowerCase();

			if (sbj.contains("_")) {
				String[] sbjSplitted = sbj.split("_");
				for (String sbjSplit : sbjSplitted) {
					if (!seeds.contains(sbjSplit))
						seeds.add(sbjSplit);
				}
			}
			if (obj.contains("_")) {
				String[] objSplitted = obj.split("_");
				for (String objSplit : objSplitted) {
					if (!seeds.contains(objSplit))
						seeds.add(objSplit);
				}
				logger.info("seeds : [" + sbj + "," + obj + "]" + " was added....\n\n");
			}
			jenaModel.close();
		}
	}
	
	public static String queryNEDBpedia(String neText){
		String neURI  ="";
		String service = "http://dbpedia.org/sparql";
		String queryString = "SELECT ?subject ?label WHERE {"
				+ " ?subject <http://www.w3.org/2000/01/rdf-schema#label> ?label. "
				+ " FILTER regex(?label, '^"+neText+"$', \"i\")"
						+ "}";
		Query query = QueryFactory.create(queryString);
		try(QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query)){
			ResultSet results = qexec.execSelect();
			while(results.hasNext()){
				QuerySolution rsn = results.next();
				neURI = rsn.get("?subject").toString();
			}
		}
		
		return neURI;
	}

}
