package com.cinvestav.mx.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;

public class RDFSerialization {
	static String genericURI = "http://tamps.cinvestav.mx/rdf/resources";
	static String genericGraphURI = "htttp://tamps.cinvestav.mx/rdf/graph";
	static final String rdfPropertyURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static final String keywordResourceURI = "http://tamps.cinvestav.mx/rdf/resource/keywords/";
	static final String keywordPropertyURI = "http://tamps.cinvestav.mx/rdf/property/keyword";
	static final String haveKeywordPropertyURI = "http://tamps.cinvestav.mx/rdf/property/haveKeywords";
	static final String sentencePropertyURI = "http://tamps.cinvestav.mx/rdf/property/sentence";
	static final String topicPropertyURI = "http://tamps.cinvestav.mx/rdf/property/topic";
	
	public void writeModel(String outputModel, List<Sentence> sentences, String alchemyTopic, String openCalaisTopic,
			Map<String,Double> mapKeywords,String nameDoc) {
		Model jenaModel = ModelFactory.createDefaultModel();
		System.out.println("Creating RDF model");
		Resource subject = jenaModel.createResource(genericGraphURI + "/" + nameDoc);
		jenaModel.setNsPrefix("cinves", "http://tamps.cinvestav.mx/rdf/property/#");
		jenaModel.setNsPrefix("cinvesRsc", "http://tamps.cinvestav.mx/rdf/property/");
//		jenaModel.setNsPrefix("rdf", "rdfPropertyURI");
		int counter = 1;
		
		System.out.println("Number of sentences to add: " + sentences.size());
		for (Sentence sentence : sentences) {
			jenaModel.add(subject,
					jenaModel.createProperty(sentencePropertyURI),
					(RDFNode) jenaModel.createLiteral(++counter + "-" + sentence.getSentence()));
		}
		
		System.out.println("Alchemy Topic = " + alchemyTopic);
		if (alchemyTopic.length() > 0) {
			jenaModel.add(subject,
					jenaModel.createProperty(topicPropertyURI),
					(RDFNode) jenaModel.createLiteral(alchemyTopic));
		}
		System.out.println("OpenCalais Topic = " + alchemyTopic);
		if (openCalaisTopic.length() > 0) {
			jenaModel.add(subject,
					jenaModel.createProperty(topicPropertyURI),
					(RDFNode) jenaModel.createLiteral(openCalaisTopic));
		}
		
//		create a SEQ
		
		Seq seqKeywords = jenaModel.createSeq( );
		System.out.println("number of keywords found = " + mapKeywords.size());
		Resource keywords = jenaModel.createResource(keywordResourceURI);
		
		for(String keyword : mapKeywords.keySet()){
			seqKeywords.add((RDFNode)jenaModel.createLiteral(keyword));
		}
		
		jenaModel.add(keywords, jenaModel.createProperty(keywordPropertyURI),seqKeywords);
		jenaModel.add(subject, jenaModel.createProperty(haveKeywordPropertyURI),keywords);
		System.out.println("JenaModel Size = " + jenaModel.size());
		try {
			System.out.println("Writing to file");
			FileWriter fw = new FileWriter(outputModel+"/"+nameDoc);
//			FileWriter fwTtl = new FileWriter(outputModel+"/"+nameDoc+".ttl");
//			jenaModel.write(fwTtl, "TURTLE");
//			jenaModel.write(System.out,"RDF/XML");
			
			jenaModel.write(fw, "RDF/XML");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
