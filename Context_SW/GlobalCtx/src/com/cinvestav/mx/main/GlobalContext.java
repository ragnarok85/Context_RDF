package com.cinvestav.mx.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalContext {

	/*
	 * args[0] - Input folder - documents divided into sentences 
	 * args[1] - Output folder: topics and sentences 
	 * args[2] - Output file - : * filename:Topic 
	 * args[3] - Output RDF model folder
	 * 
	 */
	public static void main(String[] args) {

		if (args == null) {
			System.exit(0);
		} else {
			new GlobalContext().initialRestrictions(args);
		}

		QueryWS query = new QueryWS();
		Utils utils = new Utils();
		RDFSerialization serialization = new RDFSerialization();
		Map<String, Double> mapKeywords = new HashMap<String, Double>();
		String outputModel = args[3];

		String documentPath = args[0];
		String outputTopics = args[1] + "/topics/";
		String outputSentences = args[1] + "/sentences/";
		String outputFile = args[2];

		List<File> listFiles = utils.getFilesList(documentPath);
		if (listFiles.size() == 0) {
			System.out.println("The folder is empty");
			System.exit(0);
		}

		List<String> listTopicsPerDoc = new ArrayList<String>();

		for (File file : listFiles) {
			String document = "";
			List<Sentence> listSentences = null;
			if (file.getName().endsWith(".txt")) {
				// document = utils.readDocument(file);
				listSentences = utils.readDocument(file);
				for (Sentence sentence : listSentences) {
					document += sentence.getSentence() + "\n";
				}
			} else {
				continue;
			}
			// List<Sentence> listSentences =
			// utils.divideIntoSentences(document);

			List<String> listAlchemyTopics = new ArrayList<String>();
			List<String> listOpenCalaisTopics = new ArrayList<String>();
			System.out.println("\n\n");
			mapKeywords = query.queryKeywords(document);
			System.out.println("num. of keywords found = " + mapKeywords.size() + "\n\n");

			for (Sentence sentence : listSentences) {
				System.out.println("Processing sentence: " + sentence.getSentence());
				query.queryAlchemy(sentence.getSentence(), sentence.getListAlchemyTopics(), sentence);
				listAlchemyTopics.addAll(sentence.getListAlchemyTopics().keySet());
				query.queryOpenCalais(sentence.getSentence(), sentence.getListOpenCalaisTopics(), sentence);
				listOpenCalaisTopics.addAll(sentence.getListOpenCalaisTopics().keySet());
			}

			Map<String, Integer> alchemyTopics = utils.findTopicRepetitions(listAlchemyTopics);
			Map<String, Integer> openCalaisTopics = utils.findTopicRepetitions(listOpenCalaisTopics);

			String alchemyTopic = utils.countTopicRepetitions(alchemyTopics);
			String openCalaisTopic = utils.countTopicRepetitions(openCalaisTopics);

			System.out.println("Topic selected from Alchemy : " + alchemyTopic);
			System.out.println("Topic selected from OpenCalais : " + openCalaisTopic);
			listTopicsPerDoc.add(file.getName() + ":" + alchemyTopic + ":" + openCalaisTopic + "\n");
			listAlchemyTopics.clear();
			listOpenCalaisTopics.clear();

			// gctx.queryAlchemy(document, listAlchemyTopics, new Sentence());
			// gctx.queryFileOpenCalais(documentPath);
			//
			String globalAlchemy = query.queryFileAlchemy(document);
			String globalOpenCalais = query.queryFileOpenCalais(document);
			System.out.println("Topic selected from Alchemy (Global) : " + globalAlchemy);
			System.out.println("Topic selected from OpenCalais (Global) : " + globalOpenCalais);
			listTopicsPerDoc.add(file.getName() + ":" + globalAlchemy + ":" + globalOpenCalais + "\n");
			utils.writeTopics(outputTopics + file.getName(), alchemyTopics, openCalaisTopics, file);
			utils.writeSentenceTopics(outputSentences + file.getName(), listSentences, file);

			// gctx.queryAlchemy("Open Calais demo is best viewed in Google
			// Chrome.");
			// gctx.queryOpenCalais("Open Calais demo is best viewed in Google
			// Chrome.");

			System.out.println("\n\nSerializing model to file = " + outputModel + file.getName().replace(" ", "_") + ".rdf");
			serialization.writeModel(outputModel, listSentences, globalAlchemy, globalOpenCalais, mapKeywords,
					file.getName().replace(" ", "_") + ".rdf");
		}
		utils.writeDocumentsTopics(outputFile, listTopicsPerDoc);

	}

	// ****************************________________------------------////////////////\\\\\\\\\\

	public void initialRestrictions(String[] args) {
		File file = new File(args[0]);
		File output = new File(args[1]);
		File outputRDF = new File(args[3]);
		if (!file.exists()) {
			System.out.println("the input path doesn't exist");
			System.exit(0);
		} else if (!file.isDirectory()) {
			System.out.println("the input path isn't a directory");
			System.exit(0);
		}
		if (!output.exists()) {
			output.mkdir();
			new File(output.getPath() + "/topics/").mkdir();
			new File(output.getPath() + "/sentences/").mkdir();
		} else {
			for (File topic : new File(output.getPath() + "/topics/").listFiles()) {
				topic.delete();
			}
			for (File sentence : new File(output.getPath() + "/sentences/").listFiles()) {
				sentence.delete();
			}
		}
		if(!outputRDF.exists()){
			outputRDF.mkdir();
		}else{
			for(File f : outputRDF.listFiles()){
				f.delete();
			}
		}
	}

}
