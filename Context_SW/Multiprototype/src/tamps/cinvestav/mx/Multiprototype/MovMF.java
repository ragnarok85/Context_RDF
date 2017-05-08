package tamps.cinvestav.mx.Multiprototype;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

//the centroid value for each cluster will be the word "Centroid***"

public class MovMF {
	private String mainWord;
	private List<Integer> movMFClusters;
//	private List<Map<String,Double>> clusters;
	private Map<Integer,Map<String,List<Double>>> clusters;
//	private List<List<String>> keys;
//	private List<Double> centroids;
	private List<List<Double>> centroids;
	private String[][] clustersWords;
	private Map<String,Double> avgSimC;
	private Map<String,Double> maxSimC;
	private Map<String,Double> avgSim;
	private Map<String,Double> maxSim;
	double clusterVal[][];
	int numCol;
	int numRow;
	
	public MovMF(){
		this.mainWord = "";
		//this.clusters = new ArrayList<Map<String,Double>>();
		this.clusters = new HashMap<Integer, Map<String,List<Double>>>();
		this.movMFClusters = new ArrayList<Integer>();
//		this.keys = new ArrayList<List<String>>();
//		this.centroids = new ArrayList<Double>();
		this.centroids = new ArrayList<List<Double>>();
		this.avgSimC = new HashMap<String,Double>();
		this.maxSimC = new HashMap<String,Double>();
		this.avgSim = new HashMap<String,Double>();
		this.maxSim = new HashMap<String,Double>();
		this.clustersWords = null;
		this.clusterVal = null;
	}

	/*******************************************Setter/Getter*************************************************/
	
	public String getMainWord() {
		return mainWord;
	}

	public void setMainWord(String mainWord) {
		this.mainWord = mainWord;
	}

//	public List<Map<String, Double>> getClusters() {
//		return clusters;
//	}

//	public void setClusters(List<Map<String, Double>> clusters) {
//		this.clusters.addAll(clusters);
//	}
//
	public String[][] getClustersWords() {
		return clustersWords;
	}

	public void setClustersWords(String[][] clustersWords) {
		this.clustersWords = clustersWords;
	}

	public double[][] getClusterVal() {
		return clusterVal;
	}

	public void setClusterVal(double[][] clusterVal) {
		this.clusterVal = clusterVal;
	}

	public int getNumCol() {
		return numCol;
	}

	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}

	public int getNumRow() {
		return numRow;
	}

	public void setNumRow(int numRow) {
		this.numRow = numRow;
	}

//	public List<List<String>> getKeys() {
//		return keys;
//	}
//
//	public void setKeys(List<List<String>> keys) {
//		this.keys.addAll(keys);
//	}

//	public List<Double> getCentroids() {
//		return centroids;
//	}
//
//	public void setCentroids(List<Double> centroids) {
//		this.centroids.addAll(centroids);
//	}

	public Map<String, Double> getAvgSimC() {
		return avgSimC;
	}

	public void setAvgSimC(Map<String, Double> avgSimC) {
		this.avgSimC.putAll(avgSimC);
	}

	public Map<String, Double> getMaxSimC() {
		return maxSimC;
	}

	public void setMaxSimC(Map<String, Double> maxSimC) {
		this.maxSimC.putAll(maxSimC);
	}

	public Map<String, Double> getAvgSim() {
		return avgSim;
	}

	public void setAvgSim(Map<String, Double> avgSim) {
		this.avgSim.putAll(avgSim);
	}

	public Map<String, Double> getMaxSim() {
		return maxSim;
	}

	public void setMaxSim(Map<String, Double> maxSim) {
		this.maxSim.putAll(maxSim);
	}

	public List<Integer> getMovMFClusters() {
		return movMFClusters;
	}

	public void setMovMFClusters(List<Integer> movMFClusters) {
		this.movMFClusters.addAll(movMFClusters);
	}

	public Map<Integer, Map<String, List<Double>>> getClusters() {
		return clusters;
	}

	public void setClusters(Map<Integer, Map<String, List<Double>>> clusters) {
		this.clusters.putAll(clusters);
	}

	public List<List<Double>> getCentroids() {
		return centroids;
	}

	public void setCentroids(List<List<Double>> centroids) {
		this.centroids = centroids;
	}
}
