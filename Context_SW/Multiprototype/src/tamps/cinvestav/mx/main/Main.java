package tamps.cinvestav.mx.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tamps.cinvestav.mx.Multiprototype.Corpus;
import tamps.cinvestav.mx.Multiprototype.CorpusDocuments;
import tamps.cinvestav.mx.Multiprototype.MovMF;
import tamps.cinvestav.mx.Multiprototype.Word;
import tamps.cinvestav.mx.lucene.index.Indexer;
import tamps.cinvestav.mx.lucene.index.SearchEngine;
import tamps.cinvestav.mx.preprocessing.PreProcessing;
import tamps.cinvestav.mx.tfidf.TFIDF;
import tamps.cinvestav.mx.utils.ContextMetrics;
import tamps.cinvestav.mx.utils.Metrics;
import tamps.cinvestav.mx.utils.Utils;
import tamps.cinvestav.mx.utils.jri.VonMisesFisher;



public class Main {
	
	// this is used only to create the new Corpus (only one time)
	//static String OrgcorpusPath = "OrgCorpus";
	
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	static String outputFolder = "";
	
	static String stopWordsPath = "StopWords.txt";
	static String swPunctualSignsPath = "swPunctualSigns.txt";
	static String luceneIndexPath = "/LuceneIndex";
	static String outputCorpus = "/Corpus";
	
	static String externalCorpusPath = "/Corpus_Vector";
	static String neighboursOutputFile = "/Metrics/Neighbours";
	static String neighboursPerWordOutput = "/neighboursPerWord.txt";
	static String wordStatisticsOutput = "/wordStatistics.csv";
	static String coOccurrenceMatrixOutput = "/coOccurrence.txt";
	static String normalizedCoOcurrenceMatrixOutput = "/nomalizedCoOccurrence.txt";
	static String corpusWindowsOutput = "/Metrics/Windows/";
	static String movMfWordsOutput = "/Metrics/Clusters_Values/";
	static String movMfWordsCentroidsOutput = "/movMFCentroid.txt";
	static String wordSpaceOutput = "/wordSpace.txt";
	static String contextVectorOutput = "/contextVectorOutput.txt";
	static String outputLemmaWord = "/lemmaWord/";
	
	static String avgSimOutput = "/Metrics/AvgSim/";
	static String maxSimOutput = "/Metrics/MaxSim/";
	static String avgSimCOutput = "/Metrics/AvgSimC/";
	static String maxSimCOutput = "/Metrics/MaxSimC/";
	
	static String clustersOutput = "/Metrics/Clusters/";
	static String resultsOutputFile = "/Metrics/finalResults/";
	static String resultsSimpleOutputFile = "/Metrics/finalSimpleResults/";
	
	static String termMatrixOutput = "/TermDocMatrix.txt";
	// IMPORTANT=========================================
	// If number of clusters is n then the number of samples (number of times
	// which appears the word w)
	// it is need to be n+1.
	static final int numberClusters = 3;
	static final int numberNeighbours = 10;
	static final int maxNumberFeatures = 100;

	static List<String> stopWords = null;
	static List<String> swPunctualSigns = null;
	
	public static void main(String... args) {
		
		logger.trace("============Multiprototype module=============");
		
		String OrigCorpusPath  = "";
		String seedWordsPath = "";
		String seedWordsContent = "";
		Main main = new Main();
		/*
		 * args[0] - Input = Corpus path
		 * args[1] - Seed file	
		 * args[2] - output folders
		 * args[3] - output final result
		 **/
		File seedWordsFile = null;
		String outputFinalResult = "";
		
		main.initialSettings(args);
		
		if(args.length == 0){
			OrigCorpusPath = "./CorpusPapers";
		}else{
			OrigCorpusPath = args[0];
			if(args[1] != null){
				seedWordsPath = args[1];
				seedWordsFile = new File(seedWordsPath);
				seedWordsContent = Utils.getFileContentAsString(seedWordsPath);
			}
			outputFolder = args[2];
			outputFinalResult = args[3];
		}
		
		//Setting folders****************
		List<String> directories = new ArrayList<String>();
		directories.add("/Corpus");
		directories.add("/LuceneIndex");
		directories.add("/Corpus_Vector");
		directories.add("/Metrics");
		directories.add("/lemmaWord");
		directories.add("/Metrics/Neighbours");
		directories.add("/Metrics/Windows");
		directories.add("/Metrics/Clusters");
		directories.add("/Metrics/Clusters_Values");
		directories.add("/Metrics/AvgSim");
		directories.add("/Metrics/MaxSim");
		directories.add("/Metrics/AvgSimC");
		directories.add("/Metrics/MaxSimC");
		directories.add("/Metrics/finalResults");
		directories.add("/Metrics/finalSimpleResults");
		Utils.folderManagement(outputFolder,directories);
		//********************************
		
		
		long startTime = System.currentTimeMillis();
		
		List<String> filesPath = Utils.getFilesPathAsString(OrigCorpusPath);
		Map<String, List<Word>> corpus = new HashMap<String, List<Word>>();
		
		PreProcessing prep = new PreProcessing();
		
		Corpus corp = new Corpus();
		System.out.println("stopWords path: " + stopWordsPath);
		stopWords = Utils.getStopWords(stopWordsPath);
		swPunctualSigns = Utils.getStopWords(swPunctualSignsPath);

		// *************Phase 0*******************///
		
		//Pre-Processing file information (get files content as String, 
		//remove stop words, get lemmatized words, get only noun words)
		int numberWords = 0;
		List<String> idDocList = new ArrayList<String>();
		List<CorpusDocuments> documents = new ArrayList<CorpusDocuments>();
		for (String path : filesPath) {
			CorpusDocuments document = new CorpusDocuments();
			String documentContent = "";
			File file = new File(path);
			idDocList.add(file.getName());
			documentContent = Utils.getFileContentAsString(path);
			List<Word> documentWords = prep.preProcessing(documentContent, stopWords, swPunctualSigns);
			numberWords += documentWords.size();
			
			document.setId(file.getName());
			document.setDocumentWords(documentWords);
			documents.add(document);
			corpus.put(file.getName(), documentWords);
		}
		prep.printLemmaWord(outputFolder+outputLemmaWord+"Lemma-Words.txt");
		List<String> seedWords = prep.preProcessingSeeds(seedWordsContent,stopWords);
		corp.setDocuments(documents);
		corp.setNumberWordsCorpus(numberWords);
		corp.setIdDocList(idDocList);
		corp.setCorpus(corpus);

		// Write corpus to file 
		Utils.writeCorpus(outputFolder+outputCorpus, corp);
		
		// The index is created only one time
		Indexer.createIndexCorpus(outputFolder+luceneIndexPath, outputFolder+outputCorpus);
		
//		if(seedWords.size() > 0){
//			corp.setGeneralIndex(Utils.createSeedsIndex(corp,seedWords));
//		}else{
//			corp.setGeneralIndex(Utils.createCorpusIndex(corp));
//		}
		corp.setGeneralIndex(Utils.createCorpusIndex(corp));
		
		Utils.printCorpusIndex(corp, outputFolder+"/generalIndex.txt");
		
		System.out.println("Index size = " + corp.getGeneralIndex().size());
		
		SearchEngine.searchIndex(outputFolder+luceneIndexPath, corp);

		// *************Phase 1*******************///

//		 TFIDF calculation
		TFIDF tfidf = new TFIDF();
		Set<String> keys = corp.getCorpus().keySet();
		for (String key : keys) {
			for (Word token : corpus.get(key)) {
				tfidf.tf(key, corpus.get(key), token);
				tfidf.idf(corpus, token);
			}
		}

		tfidf.tfIdf(corp);

		//Utils.createMatrixTermDoc(corp);
		//Utils.printMatrixTermDoc(corp, outputFolder+termMatrixOutput);
		Utils.findNeighboursPerWord(corp, numberNeighbours, outputFolder+neighboursOutputFile);
		Utils.findMostFrequentNeighbours(corp, maxNumberFeatures);
		Utils.calculateWordFrequency(corp);
		Utils.printNeighbours(corp, outputFolder+neighboursOutputFile);
//		Utils.printNeighboursPerWord(corp, outputFolder+neighboursPerWordOutput);
		Utils.createWordSpace(corp);
		Utils.printWordSpace(corp, outputFolder+wordSpaceOutput);
		
		System.out.println("Index size = " + corp.getGeneralIndex().size());
		//Utils.createContextVector(corp);
		//Utils.printContextVector(corp, outputFolder+contextVectorOutput);
		Utils.createCoOccurrenceMatrix(corp);
		Utils.normalizeCoOccurrenceMatrix(corp);
		
		Utils.createVector(corp,numberNeighbours);
		Utils.printStatistics(corp, outputFolder+wordStatisticsOutput);
		Utils.createFeatureVectors(corp);
		System.out.println("Index size" + corp.getGeneralIndex().size());
		Utils.printCoOccurrenceMatrix(corp, outputFolder+coOccurrenceMatrixOutput);
		Utils.printNormalizedCoOccurrenceMatrix(corp, outputFolder+normalizedCoOcurrenceMatrixOutput);
		Utils.printCorpusWindows(corp, outputFolder+corpusWindowsOutput);
		Utils.printCorpusVector(corp,outputFolder+externalCorpusPath);
		
		
		// Apply movMF Function
 		System.out.println("Applying VonMisesFisher");
		VonMisesFisher vmf = new VonMisesFisher(); 
		List<MovMF> movMFs = new ArrayList<MovMF>();
		movMFs = vmf.applyVonMisesFisherDistribution(outputFolder+externalCorpusPath, numberClusters);
		
		System.out.println("Setting values");
		Utils.setValuesToMovMFs(movMFs, corp);
		Utils.printClusters(movMFs, outputFolder+clustersOutput);

		Utils.calculateCentroidValue(movMFs);
		Utils.printMovMFWordsCentroids(movMFs, outputFolder+movMfWordsCentroidsOutput);
		Utils.printMovMFWords(movMFs, outputFolder+movMfWordsOutput); // The method prints word-values information
		Metrics.avgSim(movMFs, numberClusters);
		Metrics.maxSim(movMFs, numberClusters);
		ContextMetrics.avgSimC(movMFs, numberClusters, corp);
		ContextMetrics.maxSimC(movMFs, numberClusters, corp);
		Metrics.printAvgSim(movMFs, outputFolder+avgSimOutput);
		Metrics.printMaxSim(movMFs, outputFolder+maxSimOutput);
		ContextMetrics.printAvgSimC(movMFs, outputFolder+avgSimCOutput);
		ContextMetrics.printMaxSimC(movMFs, outputFolder+maxSimCOutput);
		Utils.printResults(movMFs, outputFolder+resultsOutputFile);
		Utils.printSimpleResults(movMFs, outputFolder+resultsSimpleOutputFile);
		long endTime = System.currentTimeMillis();

		System.out.println(
				"End of the complete process. Time elapse = " + (((endTime - startTime) / 1000l) / 60l) + " Minutos");
		System.exit(0);
	}
	
	public void initialSettings(String args[]){
		if(args.length < 4){
			logger.trace("There are less than four initial parameter");
			System.exit(0);
		}
	}
	
}
