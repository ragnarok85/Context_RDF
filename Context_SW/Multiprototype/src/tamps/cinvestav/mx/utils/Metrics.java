package tamps.cinvestav.mx.utils;

import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import tamps.cinvestav.mx.Multiprototype.MovMF;

public class Metrics {
	
	public static void avgSim(List<MovMF> movMFs, int numberClusters){
		Map<String,Double> avgSim = new HashMap<String,Double>();
		for(MovMF outerMovMF : movMFs){
			System.out.println("Processing " + outerMovMF.getMainWord());
			double result1 = 0d;
			double finalResult = 0d;
			result1 = 1/(Math.pow(outerMovMF.getClusters().size(), 2));
			for(MovMF innerMovMF : movMFs){
				double result2 = 0d;
				if(innerMovMF.getMainWord().equals(outerMovMF.getMainWord())){
					continue;
				}
				for(List<Double> vectorA : outerMovMF.getCentroids()){
					for(List<Double> vectorB : innerMovMF.getCentroids()){
						//result2 += CosineSimilarity.cosineVectorVector(vectorB, vectorA);
						result2 += CosineSimilarity.cosineAvg(vectorB, vectorA);
					}
				}
				finalResult = result1*result2;
				avgSim.put(innerMovMF.getMainWord(), finalResult);
			}
			outerMovMF.setAvgSim(avgSim);
		}
	}
	
	public static void maxSim(List<MovMF> movMFs, int numberClusters){
		for(MovMF movMF : movMFs){
			System.out.println("(MaxSim) - Processing " + movMF.getMainWord());
			double result1 = 0d;
			double result2 = 0d;
			String keyResult = "";
			Map<String,Double> maxSim = new HashMap<String,Double>();
			
			for(String key : movMF.getAvgSim().keySet()){
				result1 = movMF.getAvgSim().get(key);
				if(result1 > result2){
					result2 = result1;
					keyResult = key;
				}
			}
			maxSim.put(keyResult, movMF.getAvgSim().get(keyResult));
			movMF.setMaxSim(maxSim);
		}
	}
	
	public static void printAvgSim(List<MovMF> movMFs, String avgSimOutput) {
		try {
			
			Utils.deleteDirectoryContent(avgSimOutput);
			for (MovMF movMF : movMFs) {
				System.out.println("(printAvgSim)Printing... " + movMF.getMainWord());
				PrintWriter pw = new PrintWriter(new FileWriter(avgSimOutput+movMF.getMainWord()+".txt"));
				pw.write("main Word = " + movMF.getMainWord() + "\n");
				pw.write("Word\tValue\n");
				for (String key : movMF.getAvgSim().keySet()) {
					pw.write(key + "\t" + movMF.getAvgSim().get(key) + "\n");
				}
				//pw.write("\n==================////////////**************--------------\n");
				pw.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printMaxSim(List<MovMF> movMFs, String maxSimOutput) {
		try {
			
			Utils.deleteDirectoryContent(maxSimOutput);
			for (MovMF movMF : movMFs) {
				System.out.println("(printMaxSim) Printing file " + movMF.getMainWord());
				PrintWriter pw = new PrintWriter(new FileWriter(maxSimOutput+movMF.getMainWord()+".txt"));
				pw.write("main Word = " + movMF.getMainWord() + "\n");
				pw.write("Word\tValue\n");
				for (String key : movMF.getMaxSim().keySet()) {
					pw.write(key + "\t" + movMF.getMaxSim().get(key) + "\n");
				}
				//pw.write("\n==================////////////**************--------------\n");
				pw.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
