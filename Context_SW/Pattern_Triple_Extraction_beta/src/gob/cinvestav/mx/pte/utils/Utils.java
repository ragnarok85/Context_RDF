package gob.cinvestav.mx.pte.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpii.clausie.ClausIE;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import gob.cinvestav.mx.pte.clausie.ClausieTriple;
import gob.cinvestav.mx.pte.jena.LocalProperties;
import gob.cinvestav.mx.pte.main.Main;
import gob.cinvestav.mx.pte.main.Triple;
import gob.cinvestav.mx.pte.sentence.Word;
import gob.cinvestav.mx.pte.ws.Entity;

public class Utils {

	final static Logger logger = Logger.getLogger(Main.class);
	
	public static void checkEntitiesRep(List<String> uris) {
		Set<String> setOfUris = new HashSet<String>();
		for (String uriOne : uris) {
			String splitUriOne[] = uriOne.split("/");
			String compareOne = splitUriOne[splitUriOne.length - 1].toLowerCase();
			boolean contained = false;
			for (String uriTwo : uris) {
				if (uris.indexOf(uriTwo) != uris.indexOf(uriOne)) {
					String splitUriTwo[] = uriTwo.split("/");
					String compareTwo = splitUriTwo[splitUriTwo.length - 1].toLowerCase();
					if (compareTwo.contains(compareOne) && compareTwo.length() > compareOne.length()) {
						contained = true;
						break;
					}
				}
			}
			if (!contained) {
				setOfUris.add(uriOne);
			}
		}
		uris.clear();
		uris.addAll(setOfUris);
	}
	
//	public static List<String> collectUris(List<Entity> entities) {
//		List<String> listUris = new ArrayList<String>();
//		for (Entity entity : entities) {
//			if (!entity.getUris().isEmpty()) {
//				listUris.addAll(entity.getUris());
//			}
//		}
//		checkEntitiesRep(listUris);
//		return listUris;
//	}
	
	public static List<String> collectUris(Map<String,Entity> entities) {
		List<String> listUris = new ArrayList<String>();
		for (String entity : entities.keySet()) {
			if (!entities.get(entity).getUris().isEmpty()) {
				for(String uri : entities.get(entity).getUris())
					listUris.add(uri);
			}
		}
		checkEntitiesRep(listUris);
		return listUris;
	}
	
	
	public static int createTriple(List<ClausieTriple> clTriple) {
		int numTrip = 0;
		for (ClausieTriple trip : clTriple) {
			//TODO inserted by lti [May 26, 2017,5:58:34 PM] It is necessary to keep all subjects and arguments
			//even if they don't have a dbpedia URI.
			Triple triple = new Triple();
			if (!trip.getSubject().getEntity().keySet().isEmpty()) {
				List<String> uris = Utils.collectUris(trip.getSubject().getEntity());
				if(!uris.isEmpty()){
					trip.getSubject().setTextNE(trip.getSubject().getText());
					triple.setSubjectUris(uris);
				}
				else
					continue;
//				triple.getSubjectUris().put(trip.getSubject().getTextNE(), trip.getSubject().getEntity().get(0));
			}
			if (!trip.getArgument().getEntity().keySet().isEmpty()) {
				List<String> uris = Utils.collectUris(trip.getArgument().getEntity());
				if(!uris.isEmpty()){
					trip.getArgument().setTextNE(trip.getArgument().getText());
					triple.setArgumentUris(uris);
				}
				else
					continue;
			}
//			triple.setRelation(trip.getRelation().getText());
			if (!triple.getArgumentUris().isEmpty() && !triple.getSubjectUris().isEmpty()) {
				logger.info("Triple: " + triple);
				trip.setTriple(triple);
				numTrip++;
			}
//			trip.setTriple(triple);
		}
		return numTrip;
	}
	
	public static List<Word> extractWords(ClausIE clausIE, String sentence) {
		List<Word> words = new ArrayList<Word>();

		for (CoreLabel token : clausIE.getDepTree().taggedLabeledYield()) {
			Word wrd = new Word();
			wrd.setWord(token.get(TextAnnotation.class));
			//wrd.setWord(token.get(LemmaAnnotation.class));
			wrd.setPosTag(token.get(PartOfSpeechAnnotation.class));
			
			words.add(wrd);
		}
		// Extract start-end of each word
		CoreLabelTokenFactory ctf = new CoreLabelTokenFactory();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		PTBTokenizer ptb = new PTBTokenizer(new StringReader(sentence), ctf, "invertible=true");
		while (ptb.hasNext()) {
			CoreLabel label = (CoreLabel) ptb.next();
			for (Word word : words) {
				if (word.getWord().equalsIgnoreCase(label.originalText()) && !(word.getEnd() > 0)) {
					word.setStart(label.beginPosition());
					word.setEnd(label.endPosition());
					break;
				}
			}

		}
		return words;
	}
	
	public static List<String> getClTriples(List<ClausieTriple> clTriple){
		List<String> triples = new ArrayList<String>();
//		logger.info("saving ClausIE triple: ");
		for(ClausieTriple triple : clTriple){
			if (triple.getSubject().getText().length() > 0 && triple.getArgument().getText().length() > 0){
				String trip = "";
				trip = "(" + triple.getSubject().getText()+ "," + triple.getRelation().getText() + "," + triple.getArgument().getText() + ")";
//				logger.info("\t" + trip);
				triple.setClsTriple(trip);
				triples.add(trip);
			}
			
		}
		return triples;
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
	
	public static void initializeUris(List<ClausieTriple> clsTriples){
		for(ClausieTriple clTrip : clsTriples){
			String splitSbj[] = clTrip.getSubject().getText().split("_");
			String splitArg[] = clTrip.getArgument().getText().split("_");
			clTrip.getSubject().setNumNounsText(splitSbj.length);
			clTrip.getArgument().setNumArgText(splitArg.length);
			for(String sbj : splitSbj){
				Entity entity = new Entity();
				entity.setText(sbj);
				Set<String> prop = new HashSet<String>();
				prop.add(LocalProperties.LOCALRESOURCE.url()+sbj);
				logger.info("new local property for subject =" + sbj + " " + LocalProperties.LOCALRESOURCE.url()+sbj);
				entity.setUris(prop);
				clTrip.getSubject().setTextNE(sbj);
				clTrip.getSubject().getEntity().put(sbj, entity);
			}
			for(String arg : splitArg){
				Entity entity = new Entity();
				entity.setText(arg);
				Set<String> prop = new HashSet<String>();
				prop.add(LocalProperties.LOCALRESOURCE.url()+arg);
				logger.info("new local property for argument =" + arg + " " + LocalProperties.LOCALRESOURCE.url()+arg);
				entity.setUris(prop);
				clTrip.getArgument().setTextNE(arg);
				clTrip.getArgument().getEntity().put(arg,entity);
			}
		}
	}
	
	private static String joinNN(String so, List<Word> listWords ){
		String[] splitString = so.split(" ");
		String newString = "";
		for(String splitS : splitString){
			for(Word word : listWords){
				if(word.getWord().equals(splitS)){
					if(word.getPosTag().contains("NN")){
						if(newString.length() > 0)
							newString += "_" + word.getWord();
						else
							newString += word.getWord();
						break;
					}
				}
			}
		}
		
		return newString;
	}
	
	public static void printTriples(List<ClausieTriple> clTriple) {
		for (ClausieTriple triple : clTriple) {
			logger.info(triple.printTriple());
		}

	}
	
//	public static void mapTxtToNE(List<ClausieTriple> triples, Set<Entity> entities) {
//		for (ClausieTriple triple : triples) {
//			logger.info("Look entities for: " + triple.toString());
//			for (Entity entity : entities) {
//				
//				if (triple.getSubject().getText().contains(entity.getText().replace(" ", "_")))
//					triple.getSubject().getEntity().put(entity.getText().replace(" ", "_"), entity);
//				
//				if (triple.getArgument().getText().contains(entity.getText().replace(" ", "_")))
//					triple.getArgument().getEntity().put(entity.getText().replace(" ", "_"), entity);
//
//			}
//
//		}
//	}
	
	public static void mapTxtToNE(List<ClausieTriple> triples, Set<Entity> entities) {
		for (ClausieTriple triple : triples) {
			logger.info("Look entities for: " + triple.toString());
			
			for (Entity entity : entities) {
				
				if (triple.getSubject().getText().contains(entity.getText().replace(" ", "_"))){
					triple.getSubject().getEntity().put(entity.getText().replace(" ", "_"), entity);
					eliminateWords(entity.getText().replace(" ", "_"), triple.getSubject().getWords());
				}
					
				
				if (triple.getArgument().getText().contains(entity.getText().replace(" ", "_"))){
					triple.getArgument().getEntity().put(entity.getText().replace(" ", "_"), entity);
					eliminateWords(entity.getText().replace(" ", "_"), triple.getArgument().getWords());
				}

			}
			
			if(triple.getSubject().getWords().size() > 0){
				String subject = createLocalEntity(triple.getSubject().getWords());
				Entity ent = new Entity();
				ent.setText(subject);
				ent.getUris().add(LocalProperties.LOCALRESOURCE.url() + subject);
				logger.info("adding new entity (subject): " + subject);
				triple.getSubject().getEntity().put(subject, ent);
			}
			if(triple.getArgument().getWords().size() > 0){
				String argument = createLocalEntity(triple.getArgument().getWords());
				Entity ent = new Entity();
				ent.setText(argument);
				ent.getUris().add(LocalProperties.LOCALRESOURCE.url() + argument);
				logger.info("adding new entity (argument): " + argument);
				triple.getArgument().getEntity().put(argument,ent);
			}

		}
	}
	
	private static void eliminateWords(String namedEntity, List<String> words){
		List<String> deleteWords = new ArrayList<String>();
		for(String ne : namedEntity.split("_")){
			for(String word : words){
				if(ne.equalsIgnoreCase(word)){
					deleteWords.add(word);
					break;
				}
			}
		}
		for(String delete : deleteWords){
			words.remove(delete);
		}
	}
	
	private static String createLocalEntity(List<String> words){
		String newEntity = "";
		for(int i = 0; i < words.size() ; i++){
			if(i < words.size() -1)
				newEntity += words.get(i) + "_";
			else
				newEntity += words.get(i);
		}
		return newEntity;
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
	
	public static void reCreateClTriples(List<ClausieTriple> clTriples, List<Word> listWords){
		List<ClausieTriple> toDelete = new ArrayList<ClausieTriple>();
		for(ClausieTriple triple : clTriples){
//			logger.info("Original subject: " + triple.getSubject().getText());
			String subject = joinNN(triple.getSubject().getText(), listWords);
//			logger.info("New subject: " + subject);
			if(subject.length() > 0){
				triple.getSubject().setText(subject);
				String[] splitSbj = subject.split("_");
				if(splitSbj.length > 1){
					for(String sbj : splitSbj)
						triple.getSubject().getWords().add(sbj);
				}
			}
			else{
				toDelete.add(triple);
				continue;
			}
//			logger.info("Original argument: " + triple.getArgument().getText());
			String object = joinNN(triple.getArgument().getText(),listWords);
//			logger.info("New Argument: " + object);
			if(object.length() > 0){
				triple.getArgument().setText(object);
				String[] splitArg = object.split("_");
				if(splitArg.length > 1){
					for(String arg : splitArg)
						triple.getArgument().getWords().add(arg);
				}
			}
			else
				toDelete.add(triple);
		}
		for(ClausieTriple delete : toDelete){
			logger.info("==Deleting triple: " + delete.getSubject().getText() + "," + delete.getRelation().getText() + "," + delete.getArgument().getText());
			clTriples.remove(clTriples.indexOf(delete));
		}
	}
	
	public static List<String> readLines(File inputFile){
		List<String> sentences = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			String line = "";
			int counterLines = 0;
			while((line = br.readLine()) != null){
				if(line.split(" ").length > 3){
					sentences.add(line.toLowerCase());
					counterLines++;
				}
			}
			System.out.println("\tNumber of sentences:\t" + counterLines);
		}catch(IOException e){
			logger.error("Error reading file - " + e);
		}
		return sentences;
	}
	
	
	
	public static List<Integer> searchListPosition(String text, List<Word> words) {
		String[] splitText = text.split(" ");
		List<Integer> positions = new ArrayList<Integer>();
		for (int i = 0 ; i <  words.size() ; i++) {
			if (words.get(i).getWord().equalsIgnoreCase(splitText[0])) {
				positions.add(i);
				for(int j = 1; j < splitText.length ; j++){
					i++;
					if (words.get(i).getWord().equalsIgnoreCase(splitText[j])) {
						positions.add(i);
					}else{
						positions.clear();
						break;
					}
				}
			}
		}
		//deleteDiscontinuousElements(positions);
		return positions;
	}
	
}
