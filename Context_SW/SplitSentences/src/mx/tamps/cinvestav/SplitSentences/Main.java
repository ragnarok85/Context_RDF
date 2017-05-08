package mx.tamps.cinvestav.SplitSentences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Main {

	final static Logger logger = Logger.getLogger(Main.class);
	/*
	 * args[0] = input folder
	 * args[1] = output folder
	 * args[2] = output statistics file
	 * args[3] = words Per doc file
	 */
	
	public static void main(String[] args) {
		
//		String outputPath = "/Users/lti/Documents/testMultiprototype/1/sentences/";
//		String inputPath="/Users/lti/Documents/testMultiprototype/1/";
		try{
			new Main().initialRestrictions(args);
		}catch(ArrayIndexOutOfBoundsException e){
			logger.info("there are no arguments!!");
			System.exit(0);
		}
		
		File inputDir = new File(args[0]);
		List<String> listSentences = new ArrayList<String>();
		Map<String,Integer> wordsPerDoc = new HashMap<String,Integer>();
		Map<String,Map<String,Integer>> statistics = new HashMap<String,Map<String,Integer>>();
		String statisticsFile = args[2];
		
		logger.info("Input files directory: " + args[0]);
		logger.info("Output files directory: " + args[1]);
		
		for(File inputFile : inputDir.listFiles()){
			Map<String,Integer> mapSentences = new HashMap<String,Integer>();
			if(inputFile.isFile() && inputFile.getName().endsWith("txt")){
				logger.info("Processing file: " + inputFile.getName());
				Properties props = new Properties();
				props.setProperty("annotators", "tokenize,ssplit");
				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				
				String docContent = new Main().readDoc(inputFile);
				Annotation document = new Annotation(docContent);
				
				pipeline.annotate(document);
				
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				Integer wordsDoc = 0;
				for(CoreMap sentence: sentences){
//					System.out.println(sentence.toString() + " - " +sentence.toString().split(" ").length);
					wordsDoc += sentence.toString().split(" ").length;
					mapSentences.put(sentence.toString().replace("\n", " "), wordsDoc);
					listSentences.add(sentence.toString());
				}
				wordsPerDoc.put(inputFile.getName(), wordsDoc);
				writeSenteces(args[1]+inputFile.getName(), listSentences);
				listSentences.clear();
//				try(PrintWriter pw = new PrintWriter(new FileWriter(args[1]+inputFile.getName()))){
//					System.out.println("Writing splitted sentences in the file: " + args[1] + inputFile.getName());
//					for(CoreMap sentence: sentences){
//						pw.write(sentence+"\n");
//					}
//					pw.close();
//				}catch(IOException e){
//					e.printStackTrace();
//				}
			}
			statistics.put(inputFile.getName(), mapSentences);
		}
		
		writeStatistics(statisticsFile, statistics);
		writeWordsPerDoc(args[3], wordsPerDoc);

	}
	
	public String readDoc(File inputFile){
		try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			String line = "";
			String docContent = "";
			
			while((line = br.readLine()) != null){
				//docContent += line+"\n";
				if(line.split(" ").length > 2){
					docContent += line+" ";
				}
				
			}
			//System.out.println(docContent);
			return docContent;
		}catch(IOException e){
			e.printStackTrace();
		}
		return "";
	}
	
	public void directoryManagement(String path){
		File directory = new File(path);
		if(!directory.exists()){
			directory.mkdir();
		}else if(directory.isDirectory()){
			File[] dirContent = directory.listFiles();
			for(File file : dirContent){
				file.delete();
			}
		}
	}
	
	static void writeSenteces(String output, List<String> listSentences){
		try(PrintWriter pw = new PrintWriter(new FileWriter(output))){
			for(String sentence : listSentences){
				pw.write(sentence+"\n");
			}
			pw.close();
		}catch(IOException e){
			
		}
	}
	
	static void writeStatistics(String statisticsFile, Map<String,Map<String,Integer>> statistics){
		try(PrintWriter pw = new PrintWriter(new FileWriter(statisticsFile))){
			pw.write("File\tSentence\tnumWords\n");
			for(String statistic : statistics.keySet()){
				int totalNumWords = 0;
				for(String sentence : statistics.get(statistic).keySet()){
					logger.info("statistic: " + statistic + "\t sentence: " + sentence + "\t #Words: " + statistics.get(statistic).get(sentence));
					pw.write(statistic + "\t" + sentence + "\t" + statistics.get(statistic).get(sentence));
					pw.write("\n");
				}
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	static void writeWordsPerDoc(String writerPerDocFile, Map<String,Integer> wordsPerDoc){
		try(PrintWriter pw = new PrintWriter(new FileWriter(writerPerDocFile))){
			pw.write("Document\tNum. Words\n");
			for(String doc : wordsPerDoc.keySet()){
				pw.write(doc + "\t" + wordsPerDoc.get(doc) + "\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void initialRestrictions(String args[]){
		if(args == null){
			System.out.println("The input and outjput paths must be set!");
		}
		if(args[0] != null){
			File file = new File(args[0]);
			if(file.isDirectory()){
				if(file.list().length == 0){
					System.out.println("The path : \"" + args[0] + "\" is empty");
				}
			}
		}
		if(!args[0].endsWith("/")){
			System.out.println("The input path must end with \"/\"");
			System.exit(0);
		}
		
		if(!args[1].endsWith("/")){
			System.out.println("The output path must end with \"/\"");
			System.exit(0);
		}else{
			directoryManagement(args[1]);
		}
		
		if(args[2] == null){
			System.exit(0);
		}
	}

}
