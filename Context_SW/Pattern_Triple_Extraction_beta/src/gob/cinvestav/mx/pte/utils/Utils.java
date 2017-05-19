package gob.cinvestav.mx.pte.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpii.clausie.ClausIE;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import gob.cinvestav.mx.pte.clausie.ClausieTriple;
import gob.cinvestav.mx.pte.main.Main;
import gob.cinvestav.mx.pte.sentence.Word;

public class Utils {

	final static Logger logger = Logger.getLogger(Main.class);
	
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
	
	private static void deleteDiscontinuousElements(List<Integer> positions) {
		// delete discontinuous elements from beginning and ending
		Collections.sort(positions);
		List<Integer> delete = new ArrayList<Integer>();
		for (int i = positions.size() - 1; i >= 0; i--) {
			if (i - 1 > 0) {
				int op = positions.get(i) - positions.get(i - 1);
				if (op > 1) {
					delete.add(Math.max(positions.get(i), positions.get(i - 1)));
				}
			} else {
				int op = positions.get(i) - positions.get(0);
				if (op > 1) {
					delete.add(Math.min(positions.get(i), positions.get(0)));
				}
			}

		}
		for (Integer del : delete) {
			positions.remove(del);
		}
	}
	
	public static void reCreateClTriples(List<ClausieTriple> clTriples, List<Word> listWords){
		List<ClausieTriple> toDelete = new ArrayList<ClausieTriple>();
		for(ClausieTriple triple : clTriples){
			logger.info("Original subject: " + triple.getSubject().getText());
			String subject = joinNN(triple.getSubject().getText(), listWords);
			logger.info("New subject: " + subject);
			if(subject.length() > 0)
				triple.getSubject().setText(subject);
			else{
				toDelete.add(triple);
				continue;
			}
			logger.info("Original argument: " + triple.getArgument().getText());
			String object = joinNN(triple.getArgument().getText(),listWords);
			logger.info("New Argument: " + object);
			if(object.length() > 0)
				triple.getArgument().setText(object);
			else
				toDelete.add(triple);
		}
		for(ClausieTriple delete : toDelete){
			logger.info("==Deleting triple: " + delete.getSubject().getText() + "," + delete.getRelation().getText() + "," + delete.getArgument().getText());
			clTriples.remove(clTriples.indexOf(delete));
		}
	}
	
	private static String joinNN(String so, List<Word> listWords ){
		String[] splitString = so.split(" ");
		String newString = "";
		for(String splitS : splitString){
			for(Word word : listWords){
				if(word.getWord().equals(splitS)){
					if(word.getPosTag().contains("NN")){
						newString += word.getWord();
						break;
					}
				}
			}
		}
		return newString;
	}
	
}
