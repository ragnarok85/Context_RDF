package tamps.cinvestav.mx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tamps.cinvestav.mx.Multiprototype.Word;

public class CosineSimilarity {
	public static double cosineVectorsVector(List<Map<String,Double>> vectors, List<Word> vectorB) {
		double finalResult = 0d;
		
		//for(Map<String,Double> vectorA : vectors){
		for(Map<String,Double> vectorA : vectors){
			double result1 = 0d;
			double result2 = 0d;
			double result3 = 0d;
			List<Double> v1 = new ArrayList<Double>();
			List<Double> v2 = new ArrayList<Double>();
			
//			if(vectorA.equals(vectorB))
//				continue;
			
			Set<String> kv1 = vectorA.keySet();
			//Set<String> kv2 = vectorB.keySet();
			
//			for(String k1 : kv1){
//				v1.add(vectorA.get(k1));
//			}
//			for(String k2: kv2){
//				v2.add(vectorB.get(k2));
//			}
			for(String k1 : kv1){
				v1.add(vectorA.get(k1));
			}
			for(Word k2: vectorB){
				v2.add(k2.getTfidf());
			}
			
			for(int i = 0 ; i < v1.size() ; i++){
				result1 += v1.get(i) * v2.get(i); 
				result2 += Math.pow(v1.get(i), 2);
				result3 += Math.pow(v2.get(i), 2);
			}
			result2 = Math.sqrt(result2);
			result3 = Math.sqrt(result3);
			finalResult += result1 / (result2 * result3);
		}
		
		return finalResult;
	}
	
	public static double cosineAvg(List<Double> word, List<Double> wordPrime) {
		double result1 = 0d;
		double result2 = 0d;
		double result3 = 0d;
		double finalResult = 0d;
		
		for(int i = 0 ; i < word.size() ; i++){
			result1 += word.get(i) * wordPrime.get(i); 
			result2 += Math.pow(word.get(i), 2);
			result3 += Math.pow(wordPrime.get(i), 2);
		}
		result2 = Math.sqrt(result2);
		result3 = Math.sqrt(result3);
		finalResult = result1 / (result2 * result3);
		return finalResult;
	}
	
	public static double cosineAvgC(List<Double> features, List<Double> centroid) {
		double result1 = 0d;
		double result2 = 0d;
		double result3 = 0d;
		double finalResult = 0d;
		
		int index = 0;
//		System.out.println("features - " + features);
		for(Double value : features){
			result1 += value * centroid.get(index);
			result2 += Math.pow(value, 2);
			result3 += Math.pow(centroid.get(index), 2);
			index++;
		}
		result2 = Math.sqrt(result2);
		result3 = Math.sqrt(result3);
		if((result2 * result3) == 0){
			finalResult = 0;
		}else{
			finalResult += result1 / (result2 * result3);
		}
		
		return finalResult;
	}

	public static double cosineVectorCentroid(Map<String, Double> vector, double centroid) {
		double result1 = 0d;
		double result2 = 0d;
		double result3 = 0d;
		double finalResult = 0d;
		for (String key : vector.keySet()) {
			if (key.equals("Centroid***"))
				continue;
			result1 += vector.get(key) * centroid;
			// result2 += Math.pow(vector.get(key), 2);
			// System.out.println(key + " " + vector.get(key));
			result2 += vector.get(key) * vector.get(key);
		}
		result2 = Math.sqrt(result2);
		result3 = Math.pow(centroid, 2);
		result3 = Math.sqrt(result3);
		finalResult = result1 / (result2 * result3);
		return finalResult;
	}

	public static double cosineCentroidCentroid(List<Double> centroidA, List<Double> centroidB) {
		double result1 = 0d;
		double result2 = 0d;
		double result3 = 0d;
		double finalResult = 0d;

		for (int i = 0; i < centroidA.size(); i++) {
			result1 += centroidA.get(i) * centroidB.get(i);
			result2 += Math.pow(centroidA.get(i), 2);
			result3 += Math.pow(centroidB.get(i), 2);
		}
		result2 = Math.sqrt(result2);
		result3 = Math.sqrt(result3);
		finalResult = result1 / (result2 * result3);
		return finalResult;
	}
}
