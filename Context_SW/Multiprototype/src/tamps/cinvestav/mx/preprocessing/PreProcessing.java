package tamps.cinvestav.mx.preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import tamps.cinvestav.mx.Multiprototype.Word;

public class PreProcessing {
	Map<String,String> lemmaWord = new HashMap<String,String>();
	public List<Word> preProcessing(String fileContent, List<String> stopWords, List<String> swPunctualSigns){
		Properties props = new Properties();
		List<Word> doc = new ArrayList<Word>();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(fileContent);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		
		for(CoreMap sentence : sentences){
			for(CoreLabel token : sentence.get(TokensAnnotation.class)){
				String lemma = token.get(LemmaAnnotation.class);
				String wrd = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				
				if(lemma.length() >= 3 && !avoidStopWords(stopWords, swPunctualSigns,lemma)){
					if (!StringUtils.isNumeric(lemma)) {
						if (!firstWordNumber(lemma)) {
							lemmaWord.put(wrd, lemma);
							if (lemma.contains(".")) {
								String[] splitLemma = lemma.split(".");
								for (String split : splitLemma) {
									Word word = new Word();
									word.setWord(split.toLowerCase());
									word.setPos(pos);
									doc.add(word);
								}
							} else {
								Word word = new Word();
								word.setWord(lemma.toLowerCase());
								word.setPos(pos);
								doc.add(word);
							}
						}

					}
				}
			}
			
		}
		return doc;
	}
	
	public boolean avoidStopWords(List<String> stopWords, List<String> swPunctualSigns, String lemma) {
		boolean val = false;
		
		if(stopWords.contains(lemma))
			val = true;
		else{
			for (String stopWord : swPunctualSigns) {
				if (lemma.contains(stopWord)) {
					System.out.println(lemma + " contains stopword: " + stopWord);
					val = true;
					break;
				}
			}
		}
		
		return val;
	}
		
	
	public void printLemmaWord(String outputLemmaWord){
		try(PrintWriter pw = new PrintWriter(new FileWriter(outputLemmaWord))){
			pw.write("word\tlemma\n");
			for(String word : lemmaWord.keySet()){
				pw.write(word + "\t" + lemmaWord.get(word) + "\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean firstWordNumber(String word){
		boolean response = false;
		switch(word.substring(0,1)){
		case "0" : 
			response = true;
			break;
		case "1" :
			response = true;
			break;
		case "2" :
			response = true;
			break;
		case "3" :
			response = true;
			break;
		case "4" :
			response = true;
			break;
		case "5" :
			response = true;
			break;
		case "6" :
			response = true;
			break;
		case "7" :
			response = true;
			break;
		case "8" :
			response = true;
			break;
		case "9" :
			response = true;
			break;
			
		}
		return response;
	}
	
	public List<String> preProcessingSeeds(String seedWordsFile, List<String> stopWords){
		List<String> seeds = new ArrayList<String>();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(seedWordsFile);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String lemma = token.get(LemmaAnnotation.class).toLowerCase();
				String pos = token.get(PartOfSpeechAnnotation.class);
				if(lemma.length() >= 3 && pos.equals("NN") && !stopWords.contains(lemma)){
					seeds.add(lemma.toLowerCase());
					System.out.println(lemma + " - Added");
				}
			}
		}
		System.out.println("number of words added: " + seeds.size());
		return seeds;

	}
		
	
}
