package com.cinvestav.mx.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class Utils {
	
	public String countTopicRepetitions(Map<String,Integer> topics){
		String topic = "";
		
		int counter = 0;
		for(String key :topics.keySet()){
			//System.out.println(key + " - " +alchemyTopics.get(key));
			if(counter < topics.get(key)){
				counter = topics.get(key);
				topic = key;
			}
		}
		return topic;
	}
	public Map<String,Integer> findTopicRepetitions(List<String> listTopics){
		Map<String,Integer> mapTopics = new HashMap<String,Integer>();
		
		for(String topics : listTopics){
			//System.out.println(topics);
			if(!mapTopics.containsKey(topics)){
				mapTopics.put(topics, new Integer(1));
			}else{
				Integer counter = mapTopics.get(topics);
				mapTopics.replace(topics, counter, ++counter);
			}
		}
		
		return mapTopics;
	}

	public List<Sentence> divideIntoSentences(String document){
		List<Sentence> sentences = new ArrayList<Sentence>();
//		try(InputStream is = new FileInputStream(
//				new File(getModelDir(), "en-sent.bin"))){
		System.out.println(GlobalContext.class.getClass().getResourceAsStream(getModelDir() +"en-sent.bin"));
		try(InputStream is = 
				GlobalContext.class.getClass().getResourceAsStream(getModelDir() +"en-sent.bin")){
			
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			Span spans[]  = detector.sentPosDetect(document);
			for(Span span : spans){
				Sentence sentence = new Sentence();
				sentence.setBegin(span.getStart());
				sentence.setEnd(span.getEnd());
				sentence.setSentence(document.substring(
						span.getStart(),span.getEnd()));
//				System.out.println(span + "[" + document.substring( 
//						span.getStart(),span.getEnd()) + "]");
				sentences.add(sentence);
			}
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return sentences;
		
	}
	
	private String getModelDir() {
		return "/OpenNLP/Models/";
	}
	
	public List<File> getFilesList(String filesPath){
		File files = new File(filesPath);
		List<File> listFiles = new ArrayList<File>();
		for(File file : files.listFiles()){
			if(file.isFile()){
				listFiles.add(file);
			}
		}
		return listFiles;
	}
	
//	public String readDocument(File documentPath){
//		String document = "";
//		try{
//			BufferedReader br = new BufferedReader(new FileReader(documentPath));
//			String line = "";
//			while((line = br.readLine()) != null){
//				if(line.length() > 3){
//					document += line;
//				}
//				
//			}
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//		
//		return document;
//	}
	
	public List<Sentence> readDocument(File documentPath){
		List<Sentence> document = new ArrayList<Sentence>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(documentPath));
			String line = "";
			while((line = br.readLine()) != null){
				if(line.length() > 3){
					Sentence sentence = new Sentence();
					sentence.setSentence(line);
					document.add(sentence);
				}
				
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return document;
	}
	
	public void writeTopics(String output, Map<String, Integer> alchemyTopics, Map<String, Integer> openCalaisTopics,
			File documentPath) {
		try(PrintWriter pw = new PrintWriter(output)){
			pw.write("Document\tTopic\t#Times\n");
			pw.write("Alchemy\n");
			for(String key : alchemyTopics.keySet()){
				pw.write(documentPath.getName() + "\t" + key + "\t" + alchemyTopics.get(key)+ "\n");
			}
			pw.write("OpenCalais\n");
			for(String key : openCalaisTopics.keySet()){
				pw.write(documentPath.getName() + "\t" + key + "\t" + openCalaisTopics.get(key) + "\n");
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeSentenceTopics(String output, List<Sentence> sentences, File documentPath) {
		try(PrintWriter pw = new PrintWriter(output)){
			pw.write("Document\tSentence\tTopic\n");
			pw.write("Alchemy\n");
			for(Sentence sentence :sentences ){
				pw.write(documentPath.getName() + "\t" + sentence.getSentence() + "\t");
				for(String alchemyTopic : sentence.getListAlchemyTopics().keySet())
					pw.write(alchemyTopic + " - " + sentence.getListAlchemyTopics().get(alchemyTopic) + " , ");
				pw.write("\n");
			}
			pw.write("OpenCalais\n");
			for(Sentence sentence : sentences){
				pw.write(documentPath.getName() + "\t" + sentence.getSentence()+ "\t");
				for(String openCalaisTopic : sentence.getListOpenCalaisTopics().keySet())
					pw.write(openCalaisTopic + " - " + sentence.getListOpenCalaisTopics().get(openCalaisTopic) + " , ");
				pw.write("\n");
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeDocumentsTopics(String outputFile, List<String> listTopicsPerDoc){
		try(PrintWriter pw = new PrintWriter(new FileWriter(outputFile))){
			for(String topicsPerDoc : listTopicsPerDoc){
				pw.write(topicsPerDoc + "\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	
	}
}
