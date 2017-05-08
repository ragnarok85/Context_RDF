package tamps.cinvestav.mx.Multiprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word implements Comparable<Word>{
	
	String word;
	String pos;
	List<String> idDocuments;
	//<idDoc, TermfrequencyInDoc> tf
	Map<String, Double> docFrequency;
	//<idDoc,windows>
	Map<String,List<Window>> wordNeighbours;
	//<idDoc,windowsPerDoc>
	Map<String,List<List<Double>>> contextVector;
	int frequencyAsNeighbour;
	int frequencyInAllCorp;
	double idf;
	double tfidf;
	
	List<Map<String, List<Double>>> featureVector;
	//<word,#ofTimes>
	Map<String,Integer> allNeighbours;
	List<Map<String,List<List<Word>>>> neighboursPerDoc;
	Map<Integer,Map<String,List<Double>>> allFeatureVectors;
	List<String> featureVectorIndex;
	//List<Word> contextVector;
	
	{
		this.featureVector = new ArrayList<Map<String,List<Double>>>();
		this.idDocuments = new ArrayList<String>();
		this.docFrequency = new HashMap<String, Double>();
		this.wordNeighbours = new HashMap<String,List<Window>>();
		this.featureVectorIndex = new ArrayList<String>();
		this.allFeatureVectors = new HashMap<Integer,Map<String,List<Double>>>();
		this.allNeighbours = new HashMap<String,Integer>();
		this.neighboursPerDoc = new ArrayList<Map<String,List<List<Word>>>>();
		this.contextVector = new HashMap<String,List<List<Double>>>();
		//this.contextVector = new ArrayList<Word>();
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(!(other instanceof Word)) return false;
		Word otherWord = (Word) other;
		if (otherWord.getWord().equalsIgnoreCase(this.getWord()) && otherWord.getPos().equalsIgnoreCase(this.getPos()))
			return true;
		return false;
	}
	
	public int compareTo(Word o) {
		// TODO Auto-generated method stub
		int returnValue = 0;
		if(this.getFrequencyAsNeighbour() > o.getFrequencyAsNeighbour()){
			returnValue = -1;
		}else if(this.getFrequencyAsNeighbour() < o.getFrequencyAsNeighbour()){
			returnValue = 1;
		}else if(this.getFrequencyAsNeighbour() == o.getFrequencyAsNeighbour()){
			returnValue = 0;
		}
		return returnValue;
	}

	//*********************************Setter/Getter************************************//
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public double getIdf() {
		return idf;
	}
	public void setIdf(double idf) {
		this.idf = idf;
	}
	public double getTfidf() {
		return tfidf;
	}
	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}
	
	public List<Map<String, List<Double>>> getFeatureVector() {
		return featureVector;
	}

	public void setFeatureVector(List<Map<String, List<Double>>> featureVector) {
		this.featureVector.addAll(featureVector);
	}

	public List<String> getFeatureVectorIndex() {
		return featureVectorIndex;
	}

	public void setFeatureVectorIndex(List<String> featureVectorIndex) {
		this.featureVectorIndex.addAll(featureVectorIndex);
	}

	public Map<Integer,Map<String,List<Double>>> getAllFeatureVectors() {
		return allFeatureVectors;
	}

	public void setAllFeatureVectors(Map<Integer,Map<String,List<Double>>> allFeatureVectors) {
		this.allFeatureVectors.putAll(allFeatureVectors);
	}

	public int getFrequencyAsNeighbour() {
		return frequencyAsNeighbour;
	}

	public void setFrequencyAsNeighbour(int frequencyAsNeighbour) {
		this.frequencyAsNeighbour = frequencyAsNeighbour;
	}
	
	public void addFrequencyAsNeighbour(int frequencyAsNeighbour) {
		this.frequencyAsNeighbour += frequencyAsNeighbour;
	}

	public Map<String, Integer> getAllNeighbours() {
		return allNeighbours;
	}

	public void setAllNeighbours(Map<String, Integer> allNeighbours) {
		this.allNeighbours.putAll(allNeighbours);
	}

	public List<Map<String, List<List<Word>>>> getNeighboursPerDoc() {
		return neighboursPerDoc;
	}

	public void setNeighboursPerDoc(List<Map<String, List<List<Word>>>> neighboursPerDoc) {
		this.neighboursPerDoc.addAll(neighboursPerDoc);
	}

	public int getFrequencyInAllCorp() {
		return frequencyInAllCorp;
	}

	public void setFrequencyInAllCorp(int frequencyInAllCorp) {
		this.frequencyInAllCorp = frequencyInAllCorp;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public List<String> getIdDocuments() {
		return idDocuments;
	}

	public void setIdDocuments(List<String> idDocuments) {
		this.idDocuments.addAll(idDocuments);
	}

	public Map<String, List<Window>> getWordNeighbours() {
		return wordNeighbours;
	}

	public void setWordNeighbours(Map<String, List<Window>> wordNeighbours) {
		this.wordNeighbours.putAll(wordNeighbours);
	}

	public Map<String, List<List<Double>>> getContextVector() {
		return contextVector;
	}

	public void setContextVector(Map<String, List<List<Double>>> contextVector) {
		this.contextVector.putAll(contextVector);
	}

	public Map<String, Double> getDocFrequency() {
		return docFrequency;
	}

	public void setDocFrequency(Map<String, Double> docFrequency) {
		this.docFrequency.putAll(docFrequency);
	}

	public void addDocFrequency(String idDoc, Double frequency){
		this.docFrequency.put(idDoc, frequency);
	}
	
}
