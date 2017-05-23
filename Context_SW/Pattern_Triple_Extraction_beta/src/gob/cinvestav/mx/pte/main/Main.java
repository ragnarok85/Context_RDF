package gob.cinvestav.mx.pte.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Proposition;
import gob.cinvestav.mx.pte.clausie.Argument;
import gob.cinvestav.mx.pte.clausie.ClausieTriple;
import gob.cinvestav.mx.pte.clausie.Relation;
import gob.cinvestav.mx.pte.clausie.Subject;
import gob.cinvestav.mx.pte.clausie.TripleElement;
import gob.cinvestav.mx.pte.jena.Utility;
import gob.cinvestav.mx.pte.relation.QueryLOV;
import gob.cinvestav.mx.pte.relation.QueryNell;
import gob.cinvestav.mx.pte.relation.QueryWatson;
import gob.cinvestav.mx.pte.sentence.Word;
import gob.cinvestav.mx.pte.type.QueryWiBi;
import gob.cinvestav.mx.pte.type.WiBiTypes;
import gob.cinvestav.mx.pte.utils.Utils;
import gob.cinvestav.mx.pte.ws.AlchemyEntities;
import gob.cinvestav.mx.pte.ws.BabelfyEntities;
import gob.cinvestav.mx.pte.ws.Entity;
import gob.cinvestav.mx.pte.ws.EntityOpts;

public class Main {
	public static List<String> lovUris = new ArrayList<String>();
	public static Map<String,List<String>> problematicSentences = new HashMap<String,List<String>>();
	final static Logger logger = Logger.getLogger(Main.class);
	
	/*
	 * args[0] - input folder
	 * args[1] - output folder
	 * args[2] - output seeds folder
	 * args[3] - output text Triples
	 */
	
	public static void main(String args[]) {

		Main main = new Main();
	
//		if(args == null){
//			main.testProcessing();
//		}else{
//			main.batchProcessing(args);
//		}
		main.testProcessing();
	}
	
	public void testProcessing(){
		File inputFile = new File("./test/lemmatizeProgrammer.txt");
		List<String> sentences = null;
		List<String> triples = new ArrayList<String>();
		Set<String> seeds = new HashSet<String>();
		
		Main main = new Main();
		
		sentences = Utils.readLines(inputFile);
		
		triples.addAll(main.extractTriples(inputFile.getName(),sentences, inputFile.getName()+".rdf", seeds, inputFile.getName()+".rdf", "triples"+inputFile.getName()));
		main.writeSeeds(seeds, "Seeds-"+inputFile.getName());
		Utility.printTriples(triples,"AllTriples.txt");
	}
	
	public void batchProcessing(String[] args){
		String problematicSentencesOuput = "problematicSnts.txt";
		logger.info("Input path: " + args[0]);
		logger.info("Output path: " + args[1]);
		logger.info("Output seeds directory: " + args[2]);
		logger.info("Output text triples: " + args[3]);
		logger.info("Problematic Sentences file: " + problematicSentencesOuput);
		
		initialRestrictions(args);
		
		File inputDirectory = new File(args[0]);
		File outputDirectory = new File(args[1]);
		
		List<File> inputFiles = getFiles(inputDirectory);
		List<String> processedFiles = getFilesNames(outputDirectory);
		
		Main main = new Main();
		
		if(inputFiles.size() > 0){
			Set<String>allSeeds = new HashSet<String>();
			List<String> triples = new ArrayList<String>();
			
			logger.info("number of inputFiles = " + inputFiles.size() + " number of processedFiles = " + processedFiles.size());
			
			for(File inputFile : inputFiles){
				
				Set<String> seeds = new HashSet<String>();
				List<String> sentences = null;
				
				if(processedFiles.contains(inputFile.getName())){
					logger.info("The File \"" + inputFile.getName() + "\" was previously processed");
					continue;
				}
				
				logger.info("Processing file: " + inputFile);
				
				sentences = Utils.readLines(inputFile);
				
				if(sentences.size() > 0){
					logger.info("Staring the triple extraction process\n");
					triples.addAll(main.extractTriples(inputFile.getName(),sentences, args[1]+inputFile.getName()+".rdf", seeds, inputFile.getName()+".rdf", args[3]+inputFile.getName()));
				}
				
				main.writeSeeds(seeds, args[2]+"/Seeds-"+inputFile.getName());
			}
			Utility.writeProblematicSentences(problematicSentencesOuput, problematicSentences);
			Utility.fuseSeeds(args[2],allSeeds);
			main.writeSeeds(allSeeds, args[2]+"/AllSeeds.txt");
			Utility.printTriples(triples,args[3]+"/AllTriples.txt");
			
			
		}
	}
	
	public List<String> extractTriples(String inputFile, List<String> sentences, String outputTriple, Set<String> seeds,
		String rdfModelFileName, String outputTriples ) {
		
		Utility utility = new Utility();
		List<String> triples = new ArrayList<String>();
		List<String> listProblematicSentences = new ArrayList<String>();
		int sentenceCounter = 1;
		
		for (String sentence : sentences) {
			logger.info("\n\n\tProcessing sentence (" + sentenceCounter++ + "-" + sentences.size() + ") : " + sentence);

			MasterOfTriples MT = new MasterOfTriples();
			ClausIE clausIE = new ClausIE();
			
			MT.setSentence(sentence);
//			logger.info("\tProcessing sentences with ClausIE\n");
			clausIE = processingClausIE(listProblematicSentences,sentence);
			
			if (clausIE == null){
				logger.info("No triples get extracted from ClausIE");
				continue;
			}
			
			List<ClausieTriple> clsTriples = extractClausieTriples(clausIE.getPropositions(), sentence);
			MT.setClausieTriples(clsTriples);
			List<String> stringClTriples = getClTriples(MT.getClausieTriples());
			triples.addAll(stringClTriples);
			List<Word> wordsInf = Utils.extractWords(clausIE, sentence);
			MT.setSntsWrds(wordsInf);
			
			Utils.reCreateClTriples(clsTriples,wordsInf);
			

			// *****************NE/concept processing
			// ***********************************//
			Set<AlchemyEntities> alchemyEntities = EntityOpts.extractAlchemyEntities(sentence);
			Set<BabelfyEntities> babelfyEntities = EntityOpts.extractBabelfyEntities(sentence);
			
			MT.setAlchemyEntities(alchemyEntities);
			MT.setBabelfyEntities(babelfyEntities);
			
			Set<Entity> entities = new HashSet<Entity>();
			EntityOpts.fuseAlchemyUris(MT.getAlchemyEntities(), entities);
			EntityOpts.fuseBabelfyUris(MT.getBabelfyEntities(), entities);
			
			
			
			EntityOpts.printEntities(entities);

			// System.exit(0);
//			if (!MT.getAlchemyEntities().isEmpty()) {
//				for (AlchemyEntities alchemy : MT.getAlchemyEntities()) {
//					List<Integer> entityListIndex = Utils.searchListPosition(alchemy.getText(), MT.getSntsWrds());
//					alchemy.setListPosition(entityListIndex);
//				}
//			}
//
//			if (!MT.getBabelfyEntities().isEmpty()) {
//				for (BabelfyEntities babelfy : MT.babelfyEntities) {
//					List<Integer> entityListIndex = Utils.searchListPosition(babelfy.getText(), MT.getSntsWrds());
//					babelfy.setListPosition(entityListIndex);
//				}
//			}

//			lookTriplesPositionList(MT.clausieTriples, MT.sntsWrds);
//			calculateStartEndTriples(MT.clausieTriples, MT.sntsWrds);

//			logger.info("=============reducing entities=======================");
//			reduceAlchemyEntities(MT.getAlchemyEntities());
//			reduceBabelfyEntities(MT.getBabelfyEntities(), MT.sntsWrds);
//			logger.info("=============remove entities=======================");
//			removeAlchemyEntites(MT.getAlchemyEntities());
//			removeBabelfyEntites(MT.getBabelfyEntities());
//			lookEntities(MT.clausieTriples, MT.alchemyEntities, MT.babelfyEntities);
			logger.info("=============look for named entities=======================");
			lookEntities(MT.clausieTriples, entities);
			logger.info("=============Create triples=======================");
			createTriple(MT.clausieTriples);
//			eliminateDuplicateURL(MT.clausieTriples);
			logger.info("=============Search/create relation=======================");
//			for (ClausieTriple triple : MT.clausieTriples) {
//				String uri = "";
//				if (triple.getSubject().getTextNE().length() > 0 && triple.getArgument().getTextNE().length() > 0) {
//					uri = lookRelation(triple.getSubject().getTextNE(), triple.getRelation().getText(),
//							triple.getArgument().getTextNE());
//					triple.getRelation().setUri(uri);
//					triple.getTriple().setRelationUri(uri);
//				}
//
//			}
			for (ClausieTriple triple : MT.clausieTriples) {
				String uri = "";
				if (triple.getSubject().getTextNE().length() > 0 && triple.getArgument().getTextNE().length() > 0) {
					uri = "http://tamps.cinvestav.com.mx/rdf/#"+triple.getRelation().getText().replace(" ", "");
					triple.getRelation().setUri(uri);
					triple.getTriple().setRelationUri(uri);
				}

			}
			logger.info("=============Search and create type triples =======================");
			for(Entity entity : entities){
				List<String> wibiTypes = queryWiBi(entity.getText());
				if (wibiTypes != null && !wibiTypes.isEmpty()) {
					WiBiTypes wibi = new WiBiTypes();
					wibi.getUris().addAll(wibiTypes);
					entity.getWikiUris().addAll(wibiTypes);
				}
			}
//			for (ClausieTriple triple : MT.clausieTriples) {
//				if (triple.getSubject().getTextNE().length() > 0) {
//					List<String> wibiSubjectTypes = queryWiBi(triple.getSubject().getTextNE());
//					if (wibiSubjectTypes != null && !wibiSubjectTypes.isEmpty()) {
//						WiBiTypes wibi = new WiBiTypes();
//						wibi.setUris(wibiSubjectTypes);
//						triple.getSubject().setWibi(wibi);
//					}
//				}
//
//				if (triple.getArgument().getTextNE().length() > 0) {
//					List<String> wibiArgTypes = queryWiBi(triple.getArgument().getTextNE());
//					if (wibiArgTypes != null && !wibiArgTypes.isEmpty()) {
//						WiBiTypes wibi = new WiBiTypes();
//						wibi.setUris(wibiArgTypes);
//						triple.getArgument().setWibi(wibi);
//					}
//				}
//
//			}

//			for (ClausieTriple triple : MT.clausieTriples) {
//				if (!triple.getSubject().getWibi().getUris().isEmpty()) {
//					createTypeTriple(triple);
//				}
//				if (!triple.getArgument().getWibi().getUris().isEmpty()) {
//					createTypeTriple(triple);
//				}
//			}

			logger.info("=============Create and write model=======================");
			// Utility utility = new Utility();
			
			for(Entity entity : entities){
				utility.populateTypes(entity);
			}
			for (ClausieTriple triple : MT.getClausieTriples()) {
//				utility.publicLovNameSpace(lovUris);
				// utility.addRdfsComment(sentence);
				utility.populateModel(triple, rdfModelFileName);
//				utility.populateTypes(triple);
				// utility.writeTriple(outputTriple);
			}

			logger.info("=============Triples=======================");
			printTriples(MT.clausieTriples);
			// System.out.println("=============Additional Inf.
			// types=======================");
			// System.out.println("\n original sentence = " + sentence);
			// MT.printWords();
			// MT.printClausieTriples();
			// MT.printAlchemyEntities();
			// MT.printBabelfyEntities();
		}
		if(listProblematicSentences.size() > 0){
			problematicSentences.put(inputFile, listProblematicSentences);
		}
		logger.info("Saving information in: " + outputTriple);
		Utility.printTriples(triples, outputTriples);
		utility.writeTriple(outputTriple);
		utility.extractSeeds(seeds);
		return triples;
	}
	
	/*
	 ************Phase 0 ClausIE *************** 
	 * (i) Processing sentence with ClausIE
	 * (ii) extract words from sentence 
	 * (iii) extract ClausIE triples
	*/
	
	//(i)
	public static ClausIE processingClausIE(List<String> listProblematicSentences, String sentence){
		ClausIE clausIE = new ClausIE();
		clausIE.initParser();
		
		try{
			clausIE.parse(sentence);
			clausIE.detectClauses();
			clausIE.generatePropositions();
		}catch(StackOverflowError s){
			listProblematicSentences.add(sentence + "********StackOverflowError");
			logger.error("StackOverflowError.... in sentence: " + sentence);
			return null;
		}catch(NullPointerException e){
			listProblematicSentences.add(sentence + "********NullPointerException");
			logger.error("sentence :" + sentence + "--- give no clause");
			return null;
		}
		
		return clausIE;
	}
	
	//(ii)
	

	//(iii)
	public static List<ClausieTriple> extractClausieTriples(List<Proposition> propositions, String sentence) {
		List<ClausieTriple> clTriples = new ArrayList<ClausieTriple>();
		logger.info("CLAUSIE PROPOSITIONS");
		for (Proposition proposition : propositions) {
			if (proposition.noArguments() > 0) {
				
				ClausieTriple clTriple = new ClausieTriple();
				Subject subject = new Subject();
				Relation relation = new Relation();
				Argument argument = new Argument();
				
				subject.setText(proposition.subject());
				relation.setText(proposition.relation());
				argument.setText(proposition.argument(0));
								
				clTriple.setTriple(subject, relation, argument);
				clTriple.setOrgSentence(sentence);
				
				logger.info("\t(" + subject.getText() + "," + relation.getText() + "," + argument.getText()+")");
				clTriples.add(clTriple);
			}
		}
		return clTriples;

	}

	// **************Phase 1  NEs/Concepts**********************//
	/*
	 * (i) 		extract AlchemyEntities 
	 * (ii) 	extract BabelfyEntities 
	 * (iii)	look for elements in list (List<Word>) 
	 * (iv) 	Calculate start and end of each element
	 * 
	 */
	
	

	public static void lookTriplesPositionList(List<ClausieTriple> clTriple, List<Word> words) {
		for (ClausieTriple triple : clTriple) {
			triple.getSubject().setListPosition(Utils.searchListPosition(triple.getSubject().getText(), words));
			triple.getRelation().setListPosition(Utils.searchListPosition(triple.getRelation().getText(), words));
			triple.getArgument().setListPosition(Utils.searchListPosition(triple.getArgument().getText(), words));
		}
	}

	public static void calculateStartEndTriples(List<ClausieTriple> clTriple, List<Word> words) {
		for (ClausieTriple triple : clTriple) {
			if (!triple.getSubject().getListPosition().isEmpty())
				setStartEndTripleElement((TripleElement) triple.getSubject(), words);
			if (!triple.getRelation().getListPosition().isEmpty())
				setStartEndTripleElement((TripleElement) triple.getRelation(), words);
			if (!triple.getArgument().getListPosition().isEmpty())
				setStartEndTripleElement((TripleElement) triple.getArgument(), words);
		}
	}

	private static void setStartEndTripleElement(TripleElement element, List<Word> words) {
		int start = element.getListPosition().get(0);
		int end = element.getListPosition().get(element.getListPosition().size() - 1);
		element.setStart(words.get(start).getStart());
		element.setEnd(words.get(end).getEnd());
	}

	

	

	// ************************ Phase 2 **********************************//
	/*
	 * (i) Concepts inside other concepts - discard the "small" concept (ii)
	 * NE/Concepts which are not in List<Word> - discard (iii)Look for subject's
	 * NE/Cs (ClausIE) (iv) Look for argument's NE/Cs (ClausIE)
	 * 
	 */

//	public static void reduceAlchemyEntities(List<AlchemyEntities> entities) {
//		//TODO avoid compare the same object
//		
//		List<Entities> toDiscard = new ArrayList<Entities>();
//		for (Entities entity : entities) {
//			String entityText = entity.getText();
//			for (Entities innerEntity : entities) {
//				String iEntityText = innerEntity.getText();
//				if (!entityText.equalsIgnoreCase(iEntityText) && entityText.contains(iEntityText)) {
////					System.out.println(
////							"\"" + entityText + "\"" + " contains " + "\"" + iEntityText + "\"" + " (discarted)");
//					logger.info("\"" + entityText + "\"" + " contains " + "\"" + iEntityText + "\"" + " (discarted)");
//					toDiscard.add(innerEntity);
//				}
//			}
//		}
//		for (Entities discard : toDiscard) {
//			entities.remove(discard);
//		}
//
//	}

//	public static void reduceBabelfyEntities(List<BabelfyEntities> entities, List<Word> words) {
//		List<BabelfyEntities> toDiscard = new ArrayList<BabelfyEntities>();
//		for (BabelfyEntities entity : entities) {
//			String entityText = entity.getText();
//
//			// the concept is not a noun
//			if (entity.getListPosition().size() == 1) {
//				if (!words.get(entity.getListPosition().get(0)).getPosTag().contains("NN")) {
//					toDiscard.add(entity);
//					logger.info("\"" + entityText + "\"" + " is not a noun " + " (discarted)");
//				}
//			}
//			// score is equal to 0
//			if (entity.getScore() == 0.0d) {
////				System.out.println("\"" + entityText + "\"" + " have score = 0.0 " + " (discarted)");
//				logger.info("\"" + entityText + "\"" + " have score = 0.0 " + " (discarted)");
//				toDiscard.add(entity);
//			}
//			// An entity is contained inside another entity
//			for (BabelfyEntities innerEntity : entities) {
//				String iEntityText = innerEntity.getText();
//				if (!entityText.equalsIgnoreCase(iEntityText) && entityText.contains(iEntityText)) {
////					System.out.println(
////							"\"" + entityText + "\"" + " contains " + "\"" + iEntityText + "\"" + " (discarted)");
//					logger.info("\"" + entityText + "\"" + " contains " + "\"" + iEntityText + "\"" + "--> \"" + iEntityText + "\"" +"(discarted)");
//					toDiscard.add(innerEntity);
//				}
//
//			}
//		}
//		for (Entities discard : toDiscard) {
//			entities.remove(discard);
//		}
//
//	}

	//Discard entities which are not part of the sentence
	//that means, Alchemy return an URI which main word are not in the sentence
//	public static void removeAlchemyEntites(List<AlchemyEntities> entities) {
//		List<Entities> toDiscard = new ArrayList<Entities>();
//		for (Entities entity : entities) {
//			if (entity.getListPosition().isEmpty()) {
////				System.out.println("\"" + entity.getText() + "\"" + " -  didn't match with any element in the list");
//				logger.info("\"" + entity.getText() + "\"" + " -  didn't match with any element in the list");
//				toDiscard.add(entity);
//			}
//		}
//		for (Entities discard : toDiscard) {
//			entities.remove(discard);
//		}
//	}

//	public static void removeBabelfyEntites(List<BabelfyEntities> entities) {
//		List<Entities> toDiscard = new ArrayList<Entities>();
//		for (Entities entity : entities) {
//			if (entity.getListPosition().isEmpty()) {
////				System.out.println("\"" + entity.getText() + "\"" + " -  didn't match with any element in the list");
//				logger.info("\"" + entity.getText() + "\"" + " -  didn't match with any element in the list");
//				toDiscard.add(entity);
//			}
//		}
//		for (Entities discard : toDiscard) {
//			entities.remove(discard);
//		}
//	}

	public static void lookEntities(List<ClausieTriple> triples, List<AlchemyEntities> alchemyEntities,
			List<BabelfyEntities> babelfyEntities) {
		for (ClausieTriple triple : triples) {
//			System.out.println("Look entities for: " + triple.toString());
			logger.info("Look entities for: " + triple.toString());

			for (AlchemyEntities alchemy : alchemyEntities) {
				if (triple.getSubject().getText().contains(alchemy.getText().replace(" ", ""))) {
					triple.getSubject().getAlchemy().add(alchemy);
				}
				if (triple.getArgument().getText().contains(alchemy.getText().replace(" ", ""))) {
					triple.getArgument().getAlchemy().add(alchemy);
				}
			}

			for (BabelfyEntities babelfy : babelfyEntities) {
				if (triple.getSubject().getText().contains(babelfy.getText().replace(" ", ""))) {
					triple.getSubject().getBabelfy().add(babelfy);
				}
				if (triple.getArgument().getText().contains(babelfy.getText().replace(" ", ""))) {
					triple.getArgument().getBabelfy().add(babelfy);
				}
			}
		}
	}
	
	public static void lookEntities(List<ClausieTriple> triples, Set<Entity> entities) {
		for (ClausieTriple triple : triples) {
//			System.out.println("Look entities for: " + triple.toString());
			logger.info("Look entities for: " + triple.toString());

			for (Entity entity : entities) {
				if (triple.getSubject().getText().contains(entity.getText().replace(" ", "_"))) {
					triple.getSubject().getEntity().add(entity);
				}
				if (triple.getArgument().getText().contains(entity.getText().replace(" ", "_"))) {
					triple.getArgument().getEntity().add(entity);
				}
			}
		}
	}

	/*
	 * (i) create triple (ii) delete duplicated uris
	 */

//	public static void createTriple(List<ClausieTriple> clTriple) {
//		for (ClausieTriple trip : clTriple) {
//			Triple triple = new Triple();
//
//			if (!trip.getSubject().getAlchemy().isEmpty()) {
//				for (AlchemyEntities alchemy : trip.getSubject().getAlchemy()) {
//					trip.getSubject().setTextNE(alchemy.getText());
//					if (!alchemy.getDbpediaURL().isEmpty()) {
//						triple.getSubjectUris().add(alchemy.getDbpediaURL());
//					}
//				}
//			}
//			if (!trip.getArgument().getAlchemy().isEmpty()) {
//				for (AlchemyEntities alchemy : trip.getArgument().getAlchemy()) {
//					trip.getArgument().setTextNE(alchemy.getText());
//					if (!alchemy.getDbpediaURL().isEmpty()) {
//						triple.getArgumentUris().add(alchemy.getDbpediaURL());
//					}
//				}
//			}
//
//			if (!trip.getSubject().getBabelfy().isEmpty()) {
//				for (BabelfyEntities babelfy : trip.getSubject().getBabelfy()) {
//					trip.getSubject().setTextNE(babelfy.getText());
//					if (babelfy.getDbpediaURL() != null && !babelfy.getDbpediaURL().isEmpty()) {
//						triple.getSubjectUris().add(babelfy.getDbpediaURL());
//					}
//				}
//			}
//			if (!trip.getArgument().getBabelfy().isEmpty()) {
//				for (BabelfyEntities babelfy : trip.getArgument().getBabelfy()) {
//					trip.getArgument().setTextNE(babelfy.getText());
//					if (babelfy.getDbpediaURL() != null && !babelfy.getDbpediaURL().isEmpty()) {
//						triple.getArgumentUris().add(babelfy.getDbpediaURL());
//					}
//				}
//			}
//
//			triple.setRelation(trip.getRelation().getText());
//			if (!triple.getArgumentUris().isEmpty() && !triple.getSubjectUris().isEmpty()) {
//				logger.info("Triple: " + triple);
//				trip.setTriple(triple);
//			}
//
//		}
//	}
	
	public static void createTriple(List<ClausieTriple> clTriple) {
		for (ClausieTriple trip : clTriple) {
			Triple triple = new Triple();

			if (!trip.getSubject().getEntity().isEmpty()) {
				for (Entity entity : trip.getSubject().getEntity()) {
//					trip.getSubject().setTextNE(entity.getText());
					trip.getSubject().setTextNE(trip.getSubject().getText());
					if (!entity.getUris().isEmpty()) {
						triple.getSubjectUris().addAll(entity.getUris());
					}
				}
			}
			if (!trip.getArgument().getEntity().isEmpty()) {
				for (Entity entity : trip.getArgument().getEntity()) {
//					trip.getArgument().setTextNE(entity.getText());
					trip.getArgument().setTextNE(trip.getArgument().getText());
					if (!entity.getUris().isEmpty()) {
						triple.getArgumentUris().addAll(entity.getUris());
					}
				}
			}

			triple.setRelation(trip.getRelation().getText());
			if (!triple.getArgumentUris().isEmpty() && !triple.getSubjectUris().isEmpty()) {
				logger.info("Triple: " + triple);
				trip.setTriple(triple);
			}

		}
	}

	public static void eliminateDuplicateURL(List<ClausieTriple> clTriple) {
		logger.info("Eliminating duplicated URLs");
		for (ClausieTriple trip : clTriple) {
			List<Integer> deleteSubject = new ArrayList<Integer>();
			List<Integer> deleteArgument = new ArrayList<Integer>();
			Collections.sort(trip.getTriple().getSubjectUris());
			if (trip.getTriple().getSubjectUris().size() > 1) {
				for (int i = 0; i < trip.getTriple().getSubjectUris().size(); i++) {
					if (i + 1 < trip.getTriple().getSubjectUris().size()) {
						if (trip.getTriple().getSubjectUris().get(i)
								.equalsIgnoreCase(trip.getTriple().getSubjectUris().get(i + 1))) {
							deleteSubject.add(i + 1);
						}
					}
				}
			}
			Collections.sort(trip.getTriple().getArgumentUris());
			if (trip.getTriple().getArgumentUris().size() > 1) {
				for (int i = 0; i < trip.getTriple().getArgumentUris().size(); i++) {
					if (i + 1 < trip.getTriple().getArgumentUris().size()) {
						if (trip.getTriple().getArgumentUris().get(i)
								.equalsIgnoreCase(trip.getTriple().getArgumentUris().get(i + 1))) {
							deleteArgument.add(i + 1);
						}
					}
				}
			}
			Collections.sort(deleteSubject, Collections.reverseOrder());
			Collections.sort(deleteArgument, Collections.reverseOrder());
			for (int delSub : deleteSubject) {
				logger.info(trip.getTriple().getSubjectUris().get(delSub) + " --> subject eliminated");
				trip.getTriple().getSubjectUris().remove(delSub);
			}
			for (int delArg : deleteArgument) {
				logger.info(trip.getTriple().getArgumentUris().get(delArg) + " --> argument eliminated");
				trip.getTriple().getArgumentUris().remove(delArg);
			}

		}
	}

	public static void printTriples(List<ClausieTriple> clTriple) {
		for (ClausieTriple triple : clTriple) {
			logger.info(triple.printTriple());
		}

	}
	
	public static boolean predicateRestrictions(String predicate){
		boolean isAlpha = true;
		char[] charSet = predicate.toCharArray();
		
		for(Character aChar : charSet){
			if( !aChar.equals("_") && !Character.isLetter(aChar)){
				isAlpha = false;
				break;
			}
		}
		
		return isAlpha;
	}

	public List<String> getClTriples(List<ClausieTriple> clTriple){
		List<String> triples = new ArrayList<String>();
//		logger.info("saving ClausIE triple: ");
		for(ClausieTriple triple : clTriple){
			if (triple.getSubject().getText().length() > 0 && triple.getArgument().getText().length() > 0){
				String trip = "";
				trip = triple.getSubject().getText() + "\t" + triple.getRelation().getText() + "\t" + triple.getArgument().getText();
//				logger.info("\t" + trip);
				triples.add(trip);
			}
			
		}
		return triples;
	}
	/*
	 * Look for predicates (i) consult NELL (ii) consult LOV
	 * 
	 */

	public static String lookRelation(String subject, String predicate, String argument) {
		String uri = "";

		uri = queryNELL(subject, argument);
//		if(uri.contains("http"))
//			System.out.println("NELL result = " + uri);
//		else{
//			List<String> luri = queryLOV(predicate);
//			if(luri != null && !luri.isEmpty()){
//				//luri.get(1) = http://....
//				uri =luri.get(1);
//				if(uri.contains("http")){
//					System.out.println("LOV result = " + uri);
//					//luri.get(0) = nms:id
//					String[] parts = luri.get(0).split(":");
//					lovUris.add(parts[0]+";"+uri.substring(0,uri.length()-parts[1].length()));
//				}
//			}
//			else if(luri == null || luri.isEmpty()){
		if (uri.contains("http"))
			logger.info("NELL result = " + uri);

		if (uri.length() == 0) {
			List<String> luri = new ArrayList<String>();
			//luri = queryWatson(predicate); // watson's web service is not working
			if (luri.size() > 0) {
				Iterator<String> iter = luri.iterator();
				uri = iter.next();
				if (uri.contains("http")) {
					System.out.println("Watson's result = " + uri);
					// luri.get(0) = nms:id
				}
			}
		}
		if(uri.length() == 0 ){
			List<String> luri = queryLOV(predicate);
			if(luri != null && !luri.isEmpty()){
				//luri.get(1) = http://....
				uri =luri.get(1);
				if(uri.contains("http")){
					logger.info("LOV result = " + uri);
					//luri.get(0) = nms:id
					String[] parts = luri.get(0).split(":");
					lovUris.add(parts[0]+";"+uri.substring(0,uri.length()-parts[1].length()));
				}
			}
		}
		
		if (uri.length() == 0 && predicateRestrictions(predicate)) {
			predicate = predicate.replaceAll(" ", "_");
			uri = "http://www.cinvestav.com.mx/rdf/#" + predicate;
			logger.info("The default uri will be = " + uri);
		}
		
		if(uri.length() == 0){
			uri = "http://www.cinvestav.com.mx/rdf/#RelatedWith";
			logger.info("The default uri will be = " + uri);
		}

		return uri;
	}

	public static String queryNELL(String subject, String argument) {
		String pred = "";
		try {
			pred = QueryNell.sendGet(subject, argument);
			if(pred.length() > 0)
				pred = "http://www.cinvestav.com.mx/rdf/#" + pred;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pred;
	}

	public static List<String> queryLOV(String predicate) {
		List<String> uri = null;
		try {
			uri = QueryLOV.sendGet(predicate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public static List<String> queryWatson(String predicate) {
		List<String> uri = null;
		try {
			uri = QueryWatson.asearch(predicate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public static List<String> queryWiBi(String concept){
		List<String> wibiTypes = new ArrayList<String>();
		
		try {
			wibiTypes = QueryWiBi.sendGet(concept);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(wibiTypes.isEmpty()){
			logger.info("WiBi does not return something for: " + concept);
		}
		
		return wibiTypes;
	}
	
//	public static TypeTriple createTypeTriple(ClausieTriple triple){
//		TypeTriple tTriple = new TypeTriple();
//		
//		if(!triple.getTriple().getSubjectUris().isEmpty()){
//			tTriple.setSubject(triple.getTriple().getSubjectUris().get(0));
//			List<String> objects = new ArrayList<String>();
//			for(String object : triple.getSubject().getWibi().getUris())
//				objects.add(object);
//			if(!objects.isEmpty()){
//				tTriple.setObject(objects);
//			}
//		}
//		
//		if(!triple.getTriple().getArgumentUris().isEmpty()){
//			tTriple.setSubject(triple.getTriple().getArgumentUris().get(0));
//			List<String> objects = new ArrayList<String>();
//			for(String object : triple.getArgument().getWibi().getUris())
//				objects.add(object);
//			if(!objects.isEmpty()){
//				tTriple.setObject(objects);
//			}
//		}
//		return tTriple;
//		
//	}
	
	public static TypeTriple createTypeTriple(Set<Entity> entities, WiBiTypes wibi){
		TypeTriple tTriple = new TypeTriple();
		
		for(Entity entity : entities){
			for(String uri : entity.getUris()){
				for(String wiki : wibi.getUris()){
					
				}
			}
		}
		return tTriple;
		
	}
	
	public void initialRestrictions(String[] args){
		File file = new File(args[0]);
		File output = new File(args[1]);
		File outputSeeds = new File(args[2]);
		File outputTriples = new File(args[3]);
		System.out.println("args size = " + args.length + "["+args[0]+","+args[1]+","+args[2]+"]");
		if(!file.exists()){
			System.out.println("the input path doesn't exist");
			System.exit(0);
		}else if(!file.isDirectory()){
			System.out.println("the input path isn't a directory");
			System.exit(0);
		}
		if(!output.exists()){
			output.mkdir();
		}
		if(!outputSeeds.exists()){
			outputSeeds.mkdir();
		}
		if(!outputTriples.exists()){
			outputTriples.mkdir();
		}
	}
	
	public static List<File> getFiles(File inputDirectory){
		List<File> inputFiles = new ArrayList<File>();
		for(File file : inputDirectory.listFiles()){
			if(file.isFile() && file.getName().endsWith("txt")){
				inputFiles.add(file);
			}
		}
		return inputFiles;
		
	}
	
	public static List<String> getFilesNames(File inputDirectory){
		List<String> inputFiles = new ArrayList<String>();
		for(File file : inputDirectory.listFiles()){
			if(file.isFile() && file.getName().endsWith("rdf")){
				inputFiles.add(file.getName().replace(".rdf", ""));
			}
		}
		return inputFiles;
		
	}
	
	public void writeSeeds(Set<String> seeds, String output){
		try(PrintWriter pw = new PrintWriter(new FileWriter(output))){
			for(String seed : seeds){
				pw.write(seed+"\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
