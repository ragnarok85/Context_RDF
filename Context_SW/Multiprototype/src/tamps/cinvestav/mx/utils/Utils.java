package tamps.cinvestav.mx.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import tamps.cinvestav.mx.Multiprototype.Corpus;
import tamps.cinvestav.mx.Multiprototype.CorpusDocuments;
import tamps.cinvestav.mx.Multiprototype.MovMF;
import tamps.cinvestav.mx.Multiprototype.Vector;
import tamps.cinvestav.mx.Multiprototype.Window;
import tamps.cinvestav.mx.Multiprototype.Word;

public class Utils {
	
	public static void calculateWordFrequency(Corpus corpus){
		for(Word gIndex : corpus.getGeneralIndex()){
			int wordFrequency = 0;
			for(CorpusDocuments document : corpus.getDocuments()){
				for(Word docWord : document.getDocumentWords()){
					if(gIndex.getWord().equals(docWord.getWord())){
						wordFrequency++;
					}
				}
			}
			gIndex.setFrequencyInAllCorp(wordFrequency);
		}
	}
	
	public static void calculateCentroidValue(List<MovMF> movMFs) {
		
		for (MovMF movMF : movMFs) {
				List<List<Double>> centroids = new ArrayList<List<Double>>();
				for (Integer Keys : movMF.getClusters().keySet()) {//clusters
					List<Double> clusterL = new ArrayList<Double>();
					double op = 0.0d;
					List<List<Double>> wordsPerCluster = new ArrayList<List<Double>>();
					//System.out.println("movMF.getClusters().get(Keys).size()" + movMF.getClusters().get(Keys).size());
					//for (int j = 0; j < movMF.getClusters().get(Keys).size(); j++) {//how many words per cluster
						Set<String> clusterKeys = movMF.getClusters().get(Keys).keySet(); //
						for (String clusterKey : clusterKeys) {
							wordsPerCluster.add(movMF.getClusters().get(Keys).get(clusterKey));
						}
					//}

					for (int l = 0; l < wordsPerCluster.get(0).size(); l++) {
						for (int m = 0; m < wordsPerCluster.size(); m++) {
							op += wordsPerCluster.get(m).get(l);
						}

						double prom = op / wordsPerCluster.size();
						clusterL.add(prom);
						op = 0.0d;
					}
					centroids.add(clusterL);

				}
				movMF.setCentroids(centroids);
		}
	}
	
	private static List<Double> calculateAverageVector(List<List<Double>> wordSpaceVectors){
		List<Double> result = new ArrayList<Double>();
		double partialResult = 0d;
		for(int j = 0 ; j < wordSpaceVectors.get(0).size() ; j++){
			for(int i = 0 ; i < wordSpaceVectors.size(); i++){
				 partialResult = wordSpaceVectors.get(i).get(j);
			}
			result.add(partialResult/wordSpaceVectors.size());
		}
		return result;
	}
	
	private static double calculateT(Corpus corpus, String word){
		double result = 0d;
		for(String key: corpus.getWordSpace().keySet()){
			if(!(word.equals("0123456789")) && key.equals(word)){
				for(Double value : corpus.getWordSpace().get(key)){
					result += value;
				}
			}else if(word.equals("0123456789")){
				for(Double value : corpus.getWordSpace().get(key)){
					result += value;
				}
				if(result == 0)
					System.out.println("result = 0 for word: " + word + " and wordSpace = " + corpus.getWordSpace().get(key));
			}
			
		}
		System.out.println("The sum of all attribues of " + word +" are =  " + result );
		return result;
	}
	
	private static double calculateAttribT(Corpus corpus){
		double result = 0d;
		for(Word classs : corpus.getGeneralIndex()){
			for(int i = 0 ; i < corpus.getGeneralFrequentWIndex().size(); i++){
				result += corpus.getWordSpace().get(classs.getWord()).get(i);
			}
		}
		
		System.out.println("The sum of all attributes =  " + result );
		return result;
	}
	
	private static double calculateProbCond(Corpus corpus, String word, int n2, double T1){
		double result = 0d;
		
//		result = corpus.getWordSpace().get(word).get(n2)*2;
		/*
		 * (w intersection with Att)/(total elements in class word)
		 */
		result = corpus.getWordSpace().get(word).get(n2);
		result /= T1;
		
		return result;
	}
	
	private static double calculateProb(Corpus corpus, String word, double N) {
		double result = 0d;
		for (Double value : corpus.getWordSpace().get(word)) {
			result += value;
		}
		if(result > 0){
			result = result / N;
		}
		// result = (result*2)/N;
		
		return result;
	}
	private static double calculateAttribProb(Corpus corpus, int attribPosition, double N) {
		double result = 0d;
		for(Word classs : corpus.getGeneralIndex()){
				result += corpus.getWordSpace().get(classs.getWord()).get(attribPosition);
		}
		if(result > 0){
			result /=N;
		}
		return result;
	}
	
	private static int countNeighbour(Word orgWord, Word wordNeighbour){
		int count1 = 0;
		int count2 = 0;
		int finalResult = 0;
		for(String idDoc : orgWord.getWordNeighbours().keySet()){
			for(Window window : orgWord.getWordNeighbours().get(idDoc)){
				for(Word leftW : window.getLeftNeighbours()){
					if(leftW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						count1++;
					}
						
				}
				for(Word rightW : window.getRightNeighbours()){
					if(rightW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						count1++;
					}
				}
//				if(window.getLeftNeighbours().contains(wordNeighbour) || window.getRightNeighbours().contains(wordNeighbour)){
//					count1++;
//				}
			}
		}
		
		for(String idDoc : wordNeighbour.getWordNeighbours().keySet()){
			for(Window window : wordNeighbour.getWordNeighbours().get(idDoc)){
				for(Word leftW : window.getLeftNeighbours()){
					if(leftW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						count2++;
					}
						
				}
				for(Word rightW : window.getRightNeighbours()){
					if(rightW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						count2++;
					}
				}
//				if(window.getLeftNeighbours().contains(orgWord) || window.getRightNeighbours().contains(orgWord)){
//					count2++;
//				}
			}
		}
		
		if(count1 > count2){
			finalResult = count1;
		}else{
			finalResult = count2;
		}
		
		return finalResult;
	}
	
	public static List<Word> createCorpusIndex(Corpus corpus) {
		List<Word> generalIndex = new ArrayList<Word>();
		for(CorpusDocuments documents: corpus.getDocuments()){
			for(Word token : documents.getDocumentWords()){
//				if (!generalIndex.contains(token) && !StringUtils.isNumeric(token.getWord())) {
				if (!searchToken(token,generalIndex) && !StringUtils.isNumeric(token.getWord())) {
					generalIndex.add(token);
				}
			}
		}
		return generalIndex;
	}
	
	public static List<Word> createSeedsIndex(Corpus corpus, List<String> seedWords) {
		List<Word> generalIndex = new ArrayList<Word>();
		for(String seed : seedWords){
			for(CorpusDocuments documents: corpus.getDocuments()){
				for(Word token : documents.getDocumentWords()){
					if(!generalIndex.contains(token) && token.getWord().contains(seed)){
						generalIndex.add(token);
					}
				}
			}
		}
		
		return generalIndex;
	}
	
	public static void createMatrixTermDoc(Corpus corpus) {
		Map<String,List<Word>> docs = corpus.getCorpus();
		Map<String,List<Double>> wordsVector = new HashMap<String,List<Double>>();

		System.out.println("# Characteristics = " + corpus.getIdDocList().size());
		System.out.println("# words = " + corpus.getGeneralIndex().size());
		for(Word word : corpus.getGeneralIndex()){
			List<Double> vector = new ArrayList<Double>();
			for(String idDocKey : corpus.getIdDocList()){
				boolean wordInDoc = false;
				for(Word w : docs.get(idDocKey)){
					if(!wordInDoc && w.getWord().equals(word.getWord())){
						vector.add(w.getTfidf());
						wordInDoc = true;
					}
				}
				if(!wordInDoc){
					vector.add(0d);
				}
				
			}
//			System.out.println("===========Vector of Word = " + word.getWord());
			for(Double value : vector){
				System.out.print(value + ",");
			}
//			System.out.println();
			wordsVector.put(word.getWord(), vector);
		}
		corpus.setWordMatrixTermDoc(wordsVector);
		
	}
	
	public static void createCoOccurrenceMatrix(Corpus corpus){
		Map<String, List<Double>> coOccurrence = new HashMap<String, List<Double>>();
		corpus.getCoOccurrence().clear();
		
		double T = 0d;
		T = calculateT(corpus,"0123456789"); // T = the total sum in the wordspace
		System.out.println("T = " + T);
		double T1 = 0d;
		double T2 = 0d;
		T2 = calculateAttribT(corpus); // T2 = the sum of all rows
		//rows
		for(int i = 0 ; i < corpus.getGeneralIndex().size() ; i++){
			
			T1 = calculateT(corpus,corpus.getGeneralIndex().get(i).getWord()); // T1 = the sum of the row of word i
			List<Double> element = new ArrayList<Double>();
			double probN1 = 0d;
			//probN1 = calculateProb(corpus, corpus.getGeneralIndex().get(i).getWord(), corpus.getNumberWordsCorpus());
			probN1 = calculateProb(corpus, corpus.getGeneralIndex().get(i).getWord(), T); //probN1 = probability of word i
			//columns
			for (int j = 0; j < corpus.getGeneralFrequentWIndex().size(); j++) {
				System.out.println("Calculating MI for " + i + "/" + corpus.getGeneralIndex().size() + " "+corpus.getGeneralIndex().get(i).getWord() + " - " + j + ".-" +corpus.getGeneralFrequentWIndex().get(j).getWord());
				
				double counter = 0d;
				
				double probCond = 0d;
				double probCondResult = 0d;
				
				double probN2 = 0d;
				double division = 0d;
				
				Double mutualInf = 0d; // log(P(w,c)/P(w)P(c))
			
				probN2 = calculateAttribProb(corpus, j, T2);
				
				probCond = calculateProbCond(corpus, corpus.getGeneralIndex().get(i).getWord() ,j, T1); //only returns the number of coincidence between class and attribute in the spaceword
//				probCondResult = probCond/T1;
				
//				probN2 = calculateProb(corpus, corpus.getGeneralFrequentWIndex().get(j).getWord(), T);
//				division = probCondResult/(probN1*probN2);
				division = probCond/(probN1*probN2);
				if(division > 0)
					mutualInf = Math.log(division)/Math.log(2);
				else
					mutualInf = 0d;
//				mutualInf = mutualInf * corpus.getGeneralFrequentWIndex().get(j).getIdf();
				mutualInf = probCond * mutualInf;
				if(mutualInf.isInfinite()){
					mutualInf = 0.0d;
				}
				element.add(mutualInf);
			}
			double count = 0;
			for(Double val : element){
				count +=(double)val;
			}
			if(count > 0){
				coOccurrence.put(corpus.getGeneralIndex().get(i).getWord(), element);
			}
				
		}
		corpus.setCoOccurrence(coOccurrence);
	}
	
	public static void normalizeCoOccurrenceMatrix(Corpus corpus){
		double min = 0d;
		double max = 0d;
		Map<String,List<Double>> normalizedCoOccurrence = new HashMap<String,List<Double>>();
		
		for(String word : corpus.getCoOccurrence().keySet()){
			double collectionMax = 0d;
			double collectionMin = 0d;
			
			collectionMax = Collections.max(corpus.getCoOccurrence().get(word));
			collectionMin = Collections.min(corpus.getCoOccurrence().get(word));
			
			if(collectionMax > max)
				max = collectionMax;
			if(collectionMin < min)
				min = collectionMin;
		}
		
		
		for(String word : corpus.getCoOccurrence().keySet()){
			List<Double> normalizedData = new ArrayList<Double>();
			Double nomalizedVal = 0d;
			for(Double val : corpus.getCoOccurrence().get(word)){
				nomalizedVal = (val - min)/(max-min);
				normalizedData.add(nomalizedVal);
			}
			normalizedCoOccurrence.put(word, normalizedData);
		}
		corpus.setNormalizedCoOccurrence(normalizedCoOccurrence);
	}
	
	private static void createSeveralDirs(File mainFolder, List<String> directories){
		
		for(String directory : directories){
			try{
				File dir = new File(mainFolder+directory);
				System.out.println("Creating " + dir.getCanonicalPath() + " directory");
				dir.mkdir();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void createVector(Corpus corpus, int numNeighbours) {
		for (Word token : corpus.getGeneralIndex()) {
			Vector vector = new Vector();
			List<String> idDocs = new ArrayList<String>();
			vector.setMainWord(token);
			// for each idDoc get word window
			for(String idDoc : token.getWordNeighbours().keySet()){
				idDocs.add(idDoc);
				for(Window window : token.getWordNeighbours().get(idDoc)){
					List<Word> wrd = new ArrayList<Word>();
					if(window.getLeftNeighbours().size() > 0){
						wrd.addAll(window.getLeftNeighbours());
					}
					if(window.getRightNeighbours().size() > 0){
						wrd.addAll(window.getRightNeighbours());
					}
					if(wrd.size() > 0){
						vector.getWordWindow().add(new ArrayList<Word>(wrd));
					}
				}
			}
			vector.setIdDocs(idDocs);
			if(vector.getMainWord().getWord() != null && vector.getIdDocs().size() > 0)
				corpus.getVectorList().add(vector);
		}
	}
	
	public static void createFeatureVectors(Corpus corpus) {
		List<Vector> vector2Delete = new ArrayList<Vector>();
		
		for (Vector vector : corpus.getVectorList()) {
			Map<Integer,Map<String,List<Double>>> allFeatureVectors = new HashMap<Integer,Map<String,List<Double>>>();
			List<Map<String,List<Double>>> featureVectors = new ArrayList<Map<String,List<Double>>>();
			int counterSize = 0;
			int allFeaturesCounter = 0;
			for (List<Word> wordWindow : vector.getWordWindow()) {

				//System.out.println("vector.mainWord = " + vector.getMainWord());
				Map<String, List<Double>> featureVector = new HashMap<String, List<Double>>();
				
				for (Word window : wordWindow) {
					double count = 0;
					
					//There are words which all feature values are zero?
					//for(Double value : corpus.getWordMatrixTermDoc().get(window.getWord())){
					if(corpus.getCoOccurrence().containsKey(window.getWord())){
						for (Double value : corpus.getCoOccurrence().get(window.getWord())) {
							// System.out.print(value + "\t");
							count += value;
						}
					}
					
					if(count > 0d){
						//featureVector.put(window.getWord(), corpus.getWordMatrixTermDoc().get(window.getWord()));
						featureVector.put(window.getWord(), corpus.getCoOccurrence().get(window.getWord()));
						
					}else{
						//Who is the guilty which has no values in their features?
						System.out.println("not in matrix = " + window.getWord());
					}
					
				}
				counterSize += featureVector.size();
				featureVectors.add(featureVector);
			}
			if(counterSize > 5){
				for(Map<String,List<Double>> keys : featureVectors){
					for(String key : keys.keySet()){
						Map<String,List<Double>> temporal = new HashMap<String,List<Double>>();
						temporal.put(key, keys.get(key));
						allFeatureVectors.put(allFeaturesCounter++,temporal);
					}
				}
				vector.getMainWord().setFeatureVector(featureVectors);
				vector.getMainWord().setAllFeatureVectors(allFeatureVectors);
			}else{
				vector2Delete.add(vector);
				
			}
			
		}
		
		for(Vector vectorDelete : vector2Delete){
			corpus.getVectorList().remove(vectorDelete);
			
		}
	}
	
	public static void createWordSpace(Corpus corpus){
		Map<String, List<Double>> wordSpace = new HashMap<String, List<Double>>();
		corpus.getCoOccurrence().clear();
//		for(int i = 0 ; i < corpus.getGeneralIndex().size() ; i++){
//			Word dimWrd = corpus.getGeneralIndex().get(i);
//			//System.out.print(dimWrd.getWord() + "\t");
//			List<Double> element = new ArrayList<Double>();
//			for (int j = 0; j < corpus.getGeneralIndex().size(); j++) {
//				Word featWrd = corpus.getGeneralIndex().get(j); 
///*				if (featWrd.getWord().equals("exception")) {
//					System.out.println();
//				}*/
//				double counter = 0d;
//				
//				if (isNeighbour(dimWrd,featWrd)){
//					counter = countNeighbour(dimWrd,featWrd);
//				}
//				element.add(counter);
//				//corpus.getGeneralIndex().get(i).addFrequencyNeighbour(counter2);
//			}
//			double count = 0;
//			for(Double val : element){
//				count +=val;
//			}
//			if(count > 0)
//				wordSpace.put(dimWrd.getWord(), element);
//		}
		//rows
		System.out.println("===============CoOccurrence Matrix======================");
		System.out.println("word - # of coincidences");
		List<Word> deleteWords = new ArrayList<Word>();
		for(int i = 0 ; i < corpus.getGeneralIndex().size() ; i++){
			if(corpus.getGeneralIndex().get(i).getWord().contains("/")){
				deleteWords.add(corpus.getGeneralIndex().get(i));
				continue;
			}
			
			Word dimWrd = corpus.getGeneralIndex().get(i);
			//System.out.print(dimWrd.getWord() + "\t");
			List<Double> element = new ArrayList<Double>();
			//columns
			for (int j = 0; j < corpus.getGeneralFrequentWIndex().size(); j++) {
				Word featWrd = corpus.getGeneralFrequentWIndex().get(j); 
/*				if (featWrd.getWord().equals("exception")) {
					System.out.println();
				}*/
				double counter = 0d;
				
				if (isNeighbour(dimWrd,featWrd)){
					counter = countNeighbour(dimWrd,featWrd);
				}
				element.add(counter);
				//corpus.getGeneralIndex().get(i).addFrequencyNeighbour(counter2);
			}
			double count = 0;
			for(Double val : element){
				count +=val;
			}
			if(count > 0){
				System.out.println(dimWrd.getWord() + " " + count);
				wordSpace.put(dimWrd.getWord(), element);
			}else{
				deleteWords.add(dimWrd);
			}
		}
		
		//delete from general index those words which row elements sum zero
		System.out.println("number of words to be deleted from generalIndex list: " + deleteWords.size());
		for(Word deleteWord : deleteWords){
			System.out.println(deleteWord.getWord() + " - deleted from general Index");
			corpus.getGeneralIndex().remove(deleteWord);
		}
		
		corpus.setWordSpace(wordSpace);
	}
	
	//A context vector is the centroid (or sum) of the vectors of the words occurring in the context.
	public static void createContextVector(Corpus corpus){
		List<List<Double>> wordSpaceVectors = new ArrayList<List<Double>>();
		Map<String, List<Double>> wordSpace = corpus.getWordSpace();
		
		List<List<Double>> contextVector = new ArrayList<List<Double>>();
		for(Word mainWord : corpus.getGeneralIndex()){
			Map<String,List<List<Double>>> contextV = new HashMap<String,List<List<Double>>>();
			for(String idDoc : mainWord.getWordNeighbours().keySet()){
				for(Window window : mainWord.getWordNeighbours().get(idDoc)){
					for(Word rightWord : window.getRightNeighbours()){
						if(wordSpace.get(rightWord.getWord()) != null){
							List<Double> vector = wordSpace.get(rightWord.getWord());
							wordSpaceVectors.add(vector);
						}
					}
					for (Word leftWord : window.getLeftNeighbours()) {
						if(wordSpace.get(leftWord.getWord()) != null){
							List<Double> vector = wordSpace.get(leftWord.getWord());
							wordSpaceVectors.add(vector);
						}
					}
					if(wordSpaceVectors.size() > 0){
						contextVector.add(calculateAverageVector(wordSpaceVectors));
					}
				}
				if(!contextV.containsKey(idDoc)){
					contextV.put(idDoc, contextVector);
				}
				mainWord.setContextVector(contextV);
			}
		}
	}
	
	public static boolean directoryEmpty(String path){
		boolean result = false;
		File file = new File(path);
		if(file.isDirectory()){
			if(file.listFiles().length > 0){
				result = false;
			}else{
				result = true;
			}
		}
		
		return result;
	}
	
	public static void deleteDirectoryContent(String path){
		File directory = new File(path);
		if(!directory.exists()){
			directory.mkdir();
		}else if(directory.isDirectory()){
			for(File file : directory.listFiles()){
				if(!file.isDirectory())
					file.delete();
			}
		}
	}
	
	public static void findNeighboursPerWord(Corpus corpus, int numNeighbours, String neighboursOutputFile) {
		Utils.deleteDirectoryContent(neighboursOutputFile);
		for (Word word : corpus.getGeneralIndex()) {
			Map<String, List<Window>> wrdWndws = new HashMap<String, List<Window>>();

			for (String idDoc : word.getIdDocuments()) {
				List<Window> windows = new ArrayList<Window>();
				CorpusDocuments document = new CorpusDocuments();
				for (CorpusDocuments doc : corpus.getDocuments()) {
					if (doc.getId().equals(idDoc)) {
						document = doc;
						break;
					}
				}
				List<Integer> indxWrd = new ArrayList<Integer>();
				for (int i = 0; i < document.getDocumentWords().size() ; i++) {
					if (document.getDocumentWords().get(i).getWord().equals(word.getWord())) {
						indxWrd.add(i);
					}
				}

				if (indxWrd.size() > 0) {
					for (Integer indexWord : indxWrd) {
						Window window = new Window();
						window.setWord(word);
						List<Word> rightNeighbours = new ArrayList<Word>();
						List<Word> leftNeighbours = new ArrayList<Word>();

						int windowSize = numNeighbours / 2;
						for (int i = indexWord - 1, j = indexWord + 1, cntrI = 0, cntrJ = 0; cntrI < windowSize
								|| cntrJ < windowSize; i--, j++) {
							if (i >= 0 && cntrI < windowSize) {
								leftNeighbours.add(document.getDocumentWords().get(i));
								cntrI++;
							}
							if (j < document.getDocumentWords().size() && cntrJ < windowSize) {
								rightNeighbours.add(document.getDocumentWords().get(j));
								cntrJ++;
							}
							if (i < 0 && j > document.getDocumentWords().size()) {
								break;
							}
						}
						
						if(rightNeighbours.size() > 0 || leftNeighbours.size() > 0){
							window.setLeftNeighbours(leftNeighbours);
							window.setRightNeighbours(rightNeighbours);
							windows.add(window);
						}
						
					}
					wrdWndws.put(idDoc, windows);
				}
			}
			word.setWordNeighbours(wrdWndws);
		}
	}
	
	public static void findMostFrequentNeighbours(Corpus corpus, int maxNumberFeatures){
		//Step 1 : measure the number of time a word feature is neighbour of another word
		System.out.println("");
		for(Word word : corpus.getGeneralIndex()){
			int frequencyNeighbour = 0;
			for(Word wordI : corpus.getGeneralIndex()){
				for(String idDoc : wordI.getWordNeighbours().keySet()){
					for(Window window : wordI.getWordNeighbours().get(idDoc)){
						for(Word leftW : window.getLeftNeighbours()){
							if(leftW.getWord().equals(word.getWord())){
								frequencyNeighbour++;
							}
						}
						for(Word rightW : window.getRightNeighbours()){
							if(rightW.getWord().equals(word.getWord())){
								frequencyNeighbour++;
							}
						}
						
					}
				}
			}
			word.setFrequencyAsNeighbour(frequencyNeighbour);
		}
		
		//Step 2 : replace GeneralIndex variable with the new set of features
		Collections.sort(corpus.getGeneralIndex());
		List<Word> newGeneralIndex = new ArrayList<Word>();
		for(int counter = 0; counter < maxNumberFeatures ; counter++){
			newGeneralIndex.add(corpus.getGeneralIndex().get(counter));
		}
//		corpus.getGeneralIndex().clear();
//		corpus.setGeneralIndex(newGeneralIndex);
		corpus.setGeneralFrequentWIndex(newGeneralIndex);
	}
	
	public static void folderManagement(String mainDirectory, List<String> directories){
		File mainFolder = new File(mainDirectory);
		
		//if Folder doesn't exists
		if(!mainFolder.exists()){
			try{
				System.out.println("Creating main folder " + mainFolder.getCanonicalPath());
				mainFolder.mkdir();
				createSeveralDirs(mainFolder,directories);
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			deleteDirectoryContent(mainDirectory);
			for(String dir : directories){
				deleteDirectoryContent(mainDirectory+dir);
			}
		}
		
	}
	
	public static String getFileContentAsString(String filePath){
		String fileContent = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			
			while((line = br.readLine()) != null){
				fileContent += line + "\n";
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return fileContent;
	}
	
	public static List<String> getFilesPathAsString(String directory){
		List<String> filesPath = new ArrayList<String>();
		File dir = new File(directory);
		
		if(dir.isDirectory()){
			for(File path : dir.listFiles()){
				if(!path.isDirectory() && path.getName().endsWith(".txt"))
					filesPath.add(path.getAbsolutePath());
			}
			
		}
		return filesPath;
	}
	
	public static List<String> getStopWords(String stopWordsPath){
		List<String> stopWords = new ArrayList<String>();
		
		try{
			//BufferedReader br = new BufferedReader(new FileReader(stopWordsPath));
			System.out.println("utils.class " + Utils.class.getResource("/"+stopWordsPath));
			BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/"+stopWordsPath)));
			String line;
			
			while((line = br.readLine())!= null){
				stopWords.add(line.trim());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return stopWords;
	}
	
	private static boolean isNeighbour(Word orgWord, Word wordNeighbour){
		boolean result = false;
		outer:for(String idDoc : orgWord.getWordNeighbours().keySet()){
			for(Window window : orgWord.getWordNeighbours().get(idDoc)){
				for(Word leftW : window.getLeftNeighbours()){
					if(leftW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						result = true;
						break outer;
					}
						
				}
				for(Word rightW : window.getRightNeighbours()){
					if(rightW.getWord().equalsIgnoreCase(wordNeighbour.getWord())){
						result = true;
						break outer;
					}
				}
//				if(window.getLeftNeighbours().contains(wordNeighbour) || window.getRightNeighbours().contains(wordNeighbour)){
//					result = true;
//					break outer;
//				}
			}
		}
		return result;
	}
	
	
	public static void printMatrixTermDoc(Corpus corpus, String outputFile){
		try{
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
			Set<String> keys = corpus.getWordMatrixTermDoc().keySet();
			for(String docs : corpus.getIdDocList()){
				pw.write(docs+"\t");
			}
			pw.write("\n");
			for(String key : keys){
				pw.write(key + "\t");
				for(Double feature : corpus.getWordMatrixTermDoc().get(key)){
					pw.write(feature + "\t");
				}
				pw.write("\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printStatistics(Corpus corpus, String wordStatisticsOutput) {
		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new FileWriter(wordStatisticsOutput));
			pw.write("Word\t" + "POS\t" + "FrequencyNeighbour\t" + "FrequencyCorp\t" + "idDoc\t" 
					+ "\n");
			System.out.println("corpus.getIndex().size() = " + corpus.getGeneralIndex().size());
			for (Word word : corpus.getGeneralIndex()) {
				pw.write(word.getWord() + "\t" + word.getPos() + "\t" + word.getFrequencyAsNeighbour() + "\t" + word.getFrequencyInAllCorp() + "\t" + word.getIdDocuments()  +"\n");
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printCoOccurrenceMatrix(Corpus corpus, String coOccurrenceMatrixOutput){
		try{
			PrintWriter pw = new PrintWriter(new FileWriter(coOccurrenceMatrixOutput));
			for(int i = 0; i < corpus.getGeneralFrequentWIndex().size(); i++){
				pw.write(corpus.getGeneralFrequentWIndex().get(i).getWord() + "\t");
			}
			
			pw.write("\n");
			
			for(int i = 0 ; i < corpus.getGeneralIndex().size(); i++){
				
				if(corpus.getCoOccurrence().get(corpus.getGeneralIndex().get(i).getWord()) != null){
					pw.write(corpus.getGeneralIndex().get(i).getWord()+"\t");
					for(Double element : corpus.getCoOccurrence().get(corpus.getGeneralIndex().get(i).getWord())){
						pw.write(element+"\t");
					}
					pw.write("\n");
				}
				
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printNormalizedCoOccurrenceMatrix(Corpus corpus, String normalizedCoOcurrenceMatrixOutput){
		try(PrintWriter pw = new PrintWriter(new FileWriter(normalizedCoOcurrenceMatrixOutput))){
			for(int i = 0; i < corpus.getGeneralFrequentWIndex().size(); i++){
				pw.write(corpus.getGeneralFrequentWIndex().get(i).getWord() + "\t");
			}
			
			pw.write("\n");
			
			for(int i = 0 ; i < corpus.getGeneralIndex().size(); i++){
				
				if(corpus.getNormalizedCoOccurrence().get(corpus.getGeneralIndex().get(i).getWord()) != null){
					pw.write(corpus.getGeneralIndex().get(i).getWord()+"\t");
					for(Double element : corpus.getNormalizedCoOccurrence().get(corpus.getGeneralIndex().get(i).getWord())){
						pw.write(element+"\t");
					}
					pw.write("\n");
				}
				
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	public static void printCorpusWindows(Corpus corpus, String corpusWindowsOutput){
		try{
			Utils.deleteDirectoryContent(corpusWindowsOutput);
			for(Vector vector : corpus.getVectorList()){
				System.out.println("main Word = " + vector.getMainWord().getWord());
//				if(vector.getMainWord().getWord().equals("widen")){
//					System.out.println("widen");
//				}
				
				PrintWriter pw = new PrintWriter(new FileWriter(corpusWindowsOutput+vector.getMainWord().getWord()+".txt"));
				int counter = 0;
				pw.write("main word = " + vector.getMainWord().getWord()+ "\n");
				for(List<Word> words : vector.getWordWindow()){
					pw.write("window #: " + counter++ + "\n");
					for(Word word : words){
						pw.write(word.getWord()+"\t");
					}
					pw.write("\n");
				}
				pw.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printCorpusIndex(Corpus corp, String outputIndex){
		try(PrintWriter pw = new PrintWriter(new FileWriter(outputIndex))){
			for(Word word : corp.getGeneralIndex()){
				pw.write(word.getWord() + "\n");
			}
		}catch(IOException e){
				e.printStackTrace();
		}
	}
	
	public static void printCorpusVector(Corpus corpus, String output) {
		Utils.deleteDirectoryContent(output+"/");
		try {
			PrintWriter wordIndex = new PrintWriter(new FileWriter(output + "/indexxxx.txt"));
			PrintWriter info = new PrintWriter(new FileWriter(output + "/GeneralInformation.txt"));
			boolean firstTime = true;
			for (Vector vector : corpus.getVectorList()) {
//				if(vector.getMainWord().getWord().equals("ability")){
//					System.out.println("ability");
//					System.out.println("ability word windows = " + vector.getWordWindow().size());
//				}
					List<String> featureVectorIndex = new ArrayList<String>();
					PrintWriter pw = new PrintWriter(
							new FileWriter(output + "/" + vector.getMainWord().getWord() + ".txt"));
					int windowCounter = 1;
					// pw.write(columnNames + "\n");
					info.write("Vector word:\t" + vector.getMainWord().getWord() + "\n");
					info.write("Number of Windows =\t" + vector.getWordWindow().size() + "\n");
					//info.write("Number of different Words =\t" + corpus.getGeneralIndex().size() + "\n");
					
					info.write("================================================================\n");
					info.write("Window number =\t" + windowCounter++ + "\n");
//					System.out.println(vector.getMainWord().getFeatureVector().size());
					for (Map<String, List<Double>> features : vector.getMainWord().getFeatureVector()) {
						for (String feature : features.keySet()) {
							if (firstTime) {
//								for (String featureIndx : features.keySet()) {
//									wordIndex.write(featureIndx + "\t");
//									//System.out.print(featureIndx + "\t");
//								}
								int counter = 1;
								for(Word featureIndx : corpus.getGeneralFrequentWIndex()){
									if(counter < corpus.getGeneralFrequentWIndex().size())
										wordIndex.write(featureIndx.getWord() + "\t");
									else
										wordIndex.write(featureIndx.getWord());
									counter++;
									
								}
								//System.out.println("");
								wordIndex.close();
								firstTime = false;
							}
							featureVectorIndex.add(feature);
							info.write(feature + "\t");
							//pw.write(feature + "\t");
							double count = 0d;
							for(Double value : features.get(feature)){
								pw.write(value + "\t");
								count += value;
							}
							if(count == 0d){
								System.out.println("word = " + feature);
							}
							pw.write("\n");
						}
						info.write("\n");
						
					}
					vector.getMainWord().setFeatureVectorIndex(featureVectorIndex);
					info.write("Number of different Words =\t" + vector.getMainWord().getFeatureVectorIndex().size() + "\n");
					info.write("================================================================\n");
					pw.close();
				}
//			}
			info.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void printMovMFWords(List<MovMF> movMFs, String movMFWordsOutput) {
		try {
			Utils.deleteDirectoryContent(movMFWordsOutput);
			for (MovMF mfs : movMFs) {
				System.out.println("(printMovMFWords) Printing word: " + mfs.getMainWord());
				String mainWord = mfs.getMainWord();
				PrintWriter pw10 = new PrintWriter(new FileWriter(movMFWordsOutput + mainWord + ".txt"));
				for (Integer Ikey : mfs.getClusters().keySet()) {
					pw10.write("Cluster number\t" + Ikey + " *********\n");
					for (String keyWord : mfs.getClusters().get(Ikey).keySet()) {
						pw10.write(keyWord + "\t");
						for(Double value : mfs.getClusters().get(Ikey).get(keyWord)){
								pw10.write(value + "\t");
							}
						pw10.write("\n");
					}
				}
				pw10.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printMovMFWordsCentroids(List<MovMF> movMFs, String movMFWordsCentroidsOutput) {
		try {
			PrintWriter pw10 = new PrintWriter(new FileWriter(movMFWordsCentroidsOutput));
			for (MovMF mfs : movMFs) {
				pw10.write("\nmain word\t" + mfs.getMainWord() + "\n");
				int counter = 1;
				for (List<Double> centroids : mfs.getCentroids()) {
					pw10.write("cluster #: " + counter++ + "\n");
					for (Double centroid : centroids) {
							pw10.write(centroid + "\t");
					}
					pw10.write("\n");
				}

			}
			pw10.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printClusters(List<MovMF> movMFs, String clustersOutput){
		try{
			Utils.deleteDirectoryContent(clustersOutput);
			for(MovMF movMF : movMFs){
				PrintWriter pw = new PrintWriter(new FileWriter(clustersOutput+movMF.getMainWord()+".txt"));
				pw.write("main word = " + movMF.getMainWord() + "\n");
				for(Integer keyCluster : movMF.getClusters().keySet()){
					pw.write("cluster #: " + keyCluster + "\n");
					for(String clusterWord : movMF.getClusters().get(keyCluster).keySet()){
						pw.write(clusterWord+ "\t");
					}
					pw.write("\n\n");
				}
				pw.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printWordSpace(Corpus corpus, String wordSpaceOutput){
		try{
			PrintWriter pw = new PrintWriter(new FileWriter(wordSpaceOutput));
			pw.write("\t");
			System.out.println("generalFrequentWIndex = " + corpus.getGeneralFrequentWIndex().size());
			for(Word key : corpus.getGeneralFrequentWIndex()){
				pw.write(key.getWord() +"\t");
			}
			pw.write("\n");
			for(String key : corpus.getWordSpace().keySet()){
				pw.write(key + "\t");
				for(Double value : corpus.getWordSpace().get(key)){
					pw.write(value + "\t");
				}
				pw.write("\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printContextVector(Corpus corp, String contextVectorOutput){
		try{
			PrintWriter pw = new PrintWriter(new FileWriter(contextVectorOutput));
			for(Word indexWord : corp.getGeneralIndex()){
				pw.write("Main word\t" + indexWord.getWord() + "\n");
				for(String idDoc : indexWord.getContextVector().keySet()){
					for(List<Double> contextVector : indexWord.getContextVector().get(idDoc)){
						for(Double value : contextVector){
							pw.write(value + "\t");
						}
						pw.write("\n");
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printResults(List<MovMF> movMFs, String resultsOutputFile){
		try{
			deleteDirectoryContent(resultsOutputFile);
			for(MovMF movMF : movMFs){
				PrintWriter pw = new PrintWriter(new FileWriter(resultsOutputFile+movMF.getMainWord()+".txt"));
				pw.write("MAIN WORD ==== " + movMF.getMainWord() + "\n\n");
				pw.write("AvgSim: \n");
				TreeMap<String, Double> sortedAvgSim = sortMapByValue(movMF.getAvgSim());
				int counter = 0;
				for(String key : sortedAvgSim.keySet()){
					if(counter < 10){
						//System.out.println(key+"-"+movMF.getAvgSim().get(key));
						pw.write(key+"-"+movMF.getAvgSim().get(key)+"\t");
						counter++;
					}
				}
				pw.write("\n");
				
				counter = 0;
				pw.write("AvgSimC: \n");
				TreeMap<String, Double> sortedAvgSimC = sortMapByValue(movMF.getAvgSimC());
				for(String key : sortedAvgSimC.keySet()){
					if(counter < 10){
						pw.write(key+"-"+movMF.getAvgSimC().get(key)+"\t");
						counter++;
					}
				}
				pw.write("\n");
				
				pw.write("MaxSim: \n");
				TreeMap<String, Double> MaxSim = sortMapByValue(movMF.getMaxSim());
				for(String key : MaxSim.keySet()){
					pw.write(key+"-"+movMF.getMaxSim().get(key)+"\t");
					break;
				}
				pw.write("\n");
				pw.write("MaxSimC: \n");
				TreeMap<String, Double> MaxSimC = sortMapByValue(movMF.getMaxSimC());
				for(String key : MaxSimC.keySet()){
					pw.write(key+"-"+movMF.getMaxSimC().get(key)+"\t");
					break;
				}
				pw.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printNeighbours(Corpus corp, String neighboursOutputFile){
		for(Word word : corp.getGeneralIndex()){
			try (PrintWriter pw = new PrintWriter(new FileWriter(neighboursOutputFile+"/"+word.getWord().replace("/", "-") + ".txt"))) {
				Map<String, List<Window>> neighbours = word.getWordNeighbours();
				Set<String> neighboursKey = neighbours.keySet();
				for (String key : neighboursKey) {
					pw.write(key+"\n");
					List<Window> windows = neighbours.get(key);
					int windowCounter = 1;
					for (Window window : windows) {
						pw.write("====Window "+windowCounter++ + "\n");
						pw.write("+left: ");
						for(Word left : window.getLeftNeighbours()){
							pw.write(left.getWord() + "\t");
						}
						pw.write("\n");
						pw.write("+right: ");
						for(Word right : window.getRightNeighbours()){
							pw.write(right.getWord() + "\t");
						}
						pw.write("\n\n");
					}
				}
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printSimpleResults(List<MovMF> movMFs, String resultsOutputFile){
		try{
			deleteDirectoryContent(resultsOutputFile);
			PrintWriter pw = new PrintWriter(new FileWriter(resultsOutputFile+"finalSimpleResults.txt"));
			for(MovMF movMF : movMFs){
				TreeMap<String, Double> sortedAvgSim = sortMapByValue(movMF.getAvgSim());
				//AvgSim
				int counter = 0;
				String fiveSimWords = "";
				String tenSimWords = "";
				
				for(String key : sortedAvgSim.keySet()){
					if(counter < 10){
						if(counter < 5){
							if(counter == 4){
								fiveSimWords += key;
							}else
								fiveSimWords += key + ",";
						}
						if(counter == 9){
							tenSimWords += key;
						}else{
							tenSimWords += key + ",";
						}
						counter++;
					}
				}
				pw.write("AvgSim:5:"+ movMF.getMainWord() + ":" + fiveSimWords + "\n");
				pw.write("AvgSim:10:"+ movMF.getMainWord() + ":" + tenSimWords + "\n");
				
				//AvgSimC
				counter = 0;
				fiveSimWords = "";
				tenSimWords = "";
				
				TreeMap<String, Double> sortedAvgSimC = sortMapByValue(movMF.getAvgSimC());
				
				for(String key : sortedAvgSimC.keySet()){
					if(counter < 10){
						if(counter < 5){
							if(counter == 4){
								fiveSimWords += key;
							}else
								fiveSimWords += key + ",";
						}
						if(counter == 9){
							tenSimWords += key;
						}else{
							tenSimWords += key + ",";
						}
						counter++;
					}
				}

				pw.write("AvgSimC:5:"+ movMF.getMainWord() + ":" + fiveSimWords + "\n");
				pw.write("AvgSimC:10:"+ movMF.getMainWord() + ":" + tenSimWords + "\n");
				
				//MaxSim
				TreeMap<String, Double> MaxSim = sortMapByValue(movMF.getMaxSim());
				for(String key : MaxSim.keySet()){
					pw.write("MaxSim:"+movMF.getMainWord()+":"+key);
					break;
				}
				pw.write("\n");
				//MaxSimC
				TreeMap<String, Double> MaxSimC = sortMapByValue(movMF.getMaxSimC());
				for(String key : MaxSimC.keySet()){
					pw.write("MaxSimC:"+movMF.getMainWord()+":"+key + "\n");
					break;
				}
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean searchToken(Word token, List<Word> generalIndex){
		boolean tokenExist = false;
		
		for(Word word : generalIndex){
			if(word.getWord().equalsIgnoreCase(token.getWord())){
				tokenExist = true;
				break;
			}
		}
		
		return tokenExist;
	}
	
	public static void setValuesToMovMFs(List<MovMF> movMFs, Corpus corp) {
		List<Word> index = corp.getGeneralIndex();

		for (MovMF movMF : movMFs) {
			System.out.println("Setting value to word: " + movMF.getMainWord());
			Map<Integer, Map<String, List<Double>>> clusters = new HashMap<Integer, Map<String, List<Double>>>();

			List<Integer> clusterMovMF = movMF.getMovMFClusters();
			inside: for (Word word : index) {
				if (word.getWord().equals(movMF.getMainWord())) {
//					String[] wordFeatures = {};
//					wordFeatures = word.getAllFeatureVectors().keySet().toArray(wordFeatures);
					for (int k = 0; k < clusterMovMF.size(); k++) {
						if (clusters.containsKey(clusterMovMF.get(k))) {
							//clusters.get(clusterMovMF.get(k)).add(word.getFeatureVectors().get(k));
							if(k < word.getAllFeatureVectors().size()){
								String[] wf = {};
								wf = word.getAllFeatureVectors().get(k).keySet().toArray(wf);
								clusters.get(clusterMovMF.get(k)).put(wf[0],word.getAllFeatureVectors().get(k).get(wf[0]));
							}
							
							
						} else {
							Map<String, List<Double>> cluster = new HashMap<String, List<Double>>();
							//cluster.put(wordFeatures[k],word.getAllFeatureVectors().get(k));
							if (k < word.getAllFeatureVectors().size()) {
								String[] wf = {};
								wf = word.getAllFeatureVectors().get(k).keySet().toArray(wf);
								cluster.put(wf[0], word.getAllFeatureVectors().get(k).get(wf[0]));
								clusters.put(clusterMovMF.get(k), cluster);
							}
						}

					}
					break inside;
				}
			}
			movMF.setClusters(clusters);
		}

	}
	
	public static TreeMap<String, Double> sortMapByValue(Map<String, Double> map){
		Comparator<String> comparator = new ValueComparator(map);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, Double> result = new TreeMap<String, Double>(comparator);
		result.putAll(map);
		return result;
	}
	
	public static void writeCorpus(String output, Corpus corpus){
		Utils.deleteDirectoryContent(output);
		if (Utils.directoryEmpty(output)) {
			try {
				for(CorpusDocuments document : corpus.getDocuments()){
					PrintWriter pw = new PrintWriter(new FileWriter(output + "/" + document.getId()));
					for(Word token : document.getDocumentWords()){
						pw.write(token.getWord() + " ");
					}
					pw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
		
	
	
}
