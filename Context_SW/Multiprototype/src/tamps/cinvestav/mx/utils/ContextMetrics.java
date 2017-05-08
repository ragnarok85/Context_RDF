package tamps.cinvestav.mx.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tamps.cinvestav.mx.Multiprototype.Corpus;
import tamps.cinvestav.mx.Multiprototype.MovMF;
import tamps.cinvestav.mx.Multiprototype.Word;

public class ContextMetrics {

	public static void avgSimC(List<MovMF> movMFs, int numberClusters, Corpus corp) {
		double result1 = 0d;
		List<Double> outerContextFeatures = null;
		
		result1 = 1 / (Math.pow(numberClusters, 2)); // 1/K^2
		for (MovMF outerMovMF : movMFs) {
			System.out.println("(AvgSimC) - Processing " + outerMovMF.getMainWord());
			// result1 = 1 / (Math.pow(outerMovMF.getClusters().size(), 2)); //
			// 1/K^2
			Map<String, Double> avgSims = new HashMap<String, Double>();
			System.out.println("==========================================================================");

			outerContextFeatures = corp.getCoOccurrence().get(outerMovMF.getMainWord());

			for (MovMF innerMovMF : movMFs) {
				double finalResult = 0d;
				double result2 = 0d;
				double result3 = 0d;
				double result4 = 0d;
				List<Double> innerContextFeatures = null;
				
				if (outerMovMF.getMainWord().equals(innerMovMF.getMainWord())) {
					continue;
				}
				innerContextFeatures = corp.getCoOccurrence().get(innerMovMF.getMainWord());
				// SUM^K_j * SUM^K_k
				for (List<Double> vectorA : outerMovMF.getCentroids()) {
					for (List<Double> vectorB : innerMovMF.getCentroids()) {
						result2 = CosineSimilarity.cosineAvgC(innerContextFeatures, vectorB);
						result3 = CosineSimilarity.cosineAvgC(outerContextFeatures, vectorA);
						result4 = CosineSimilarity.cosineAvg(vectorB, vectorA);
						finalResult += result2 * result3 * result4;
					}
				}

				finalResult *= result1;
				avgSims.put(innerMovMF.getMainWord(), finalResult);
				System.out.println("The similarity of " + outerMovMF.getMainWord() + " with " + innerMovMF.getMainWord()
						+ " is = " + finalResult);

			}
			outerMovMF.setAvgSimC(avgSims);

		}

		// return finalResult;
	}

	public static void maxSimC(List<MovMF> movMFs, int numberClusters, Corpus corp) {
		// w
		outerFor: for (MovMF outerMovMF : movMFs) {
			System.out.println("(MaxSimC) - Processing " + outerMovMF.getMainWord());
			Map<String, Double> maxSims = new HashMap<String, Double>();
			List<Double> maxOuterCentroid = new ArrayList<Double>();
			List<Double> outerFeatureVectors = null;
			double tempOuterResult = 0d;
			double maxOuterResult = 0d;

			System.out.println("==========================================================================");

			for (Word indexWord : corp.getGeneralIndex()) {
				if (outerMovMF.getMainWord().equals("reuter")) {
					continue outerFor;
				}
				if (indexWord.getWord().equals(outerMovMF.getMainWord())) {
					outerFeatureVectors = corp.getCoOccurrence().get(indexWord.getWord());
					break;
				}
			}
			for (List<Double> centroid : outerMovMF.getCentroids()) {
				tempOuterResult = CosineSimilarity.cosineAvgC(outerFeatureVectors, centroid);
				if (tempOuterResult > maxOuterResult) {
					maxOuterCentroid = centroid;
					maxOuterResult = tempOuterResult;
				}
			}
			// w'
			innerFor: for (MovMF innerMovMF : movMFs) {
				List<Double> innerFeatureVectors = null;
				List<Double> maxInnerCentroid = new ArrayList<Double>();
				double tempInnerResult = 0d;
				double maxInnerResult = 0d;
				double finalResult = 0d;

				if (outerMovMF.getMainWord().equals(innerMovMF.getMainWord())) {
					continue;
				}

				for (Word indexWord : corp.getGeneralIndex()) {
					if (innerMovMF.getMainWord().equals("reuter")) {
						continue innerFor;
					}
					if (indexWord.getWord().equals(innerMovMF.getMainWord())) {
						innerFeatureVectors = corp.getCoOccurrence().get(indexWord.getWord());
						break;
					}
				}
				for (List<Double> centroid : innerMovMF.getCentroids()) {
					tempInnerResult = CosineSimilarity.cosineAvgC(innerFeatureVectors, centroid);
					if (tempInnerResult > maxInnerResult) {
						maxInnerCentroid = centroid;
						maxInnerResult = tempInnerResult;
					}
				}
				System.out.println();

				finalResult = CosineSimilarity.cosineAvg(maxOuterCentroid, maxInnerCentroid);
				maxSims.put(innerMovMF.getMainWord(), finalResult);
				System.out.println("The Max similarity of " + outerMovMF.getMainWord() + " with "
						+ innerMovMF.getMainWord() + " is = " + finalResult);

			}
			outerMovMF.setMaxSimC(maxSims);

		}
	}

	public static void printAvgSimC(List<MovMF> movMFs, String avgSimCOutput) {
		try {

			Utils.deleteDirectoryContent(avgSimCOutput);
			for (MovMF movMF : movMFs) {
				System.out.println("(printAvgSimC) Printing file: " + movMF.getMainWord());
				PrintWriter pw = new PrintWriter(new FileWriter(avgSimCOutput + movMF.getMainWord() + ".txt"));
				pw.write("main Word = " + movMF.getMainWord() + "\n");
				pw.write("Word\tValue\n");
				for (String key : movMF.getAvgSimC().keySet()) {
					pw.write(key + "\t" + movMF.getAvgSimC().get(key) + "\n");
				}
				// pw.write("\n==================////////////**************--------------\n");
				pw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printMaxSimC(List<MovMF> movMFs, String maxSimCOutput) {
		try {

			Utils.deleteDirectoryContent(maxSimCOutput);
			for (MovMF movMF : movMFs) {
				System.out.println("(printMaxSimC) Printing file: " + movMF.getMainWord());
				PrintWriter pw = new PrintWriter(new FileWriter(maxSimCOutput + movMF.getMainWord() + ".txt"));
				pw.write("main Word = " + movMF.getMainWord() + "\n");
				pw.write("Word\tValue\n");
				for (String key : movMF.getMaxSimC().keySet()) {
					pw.write(key + "\t" + movMF.getMaxSimC().get(key) + "\n");
				}
				// pw.write("\n==================////////////**************--------------\n");
				pw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
