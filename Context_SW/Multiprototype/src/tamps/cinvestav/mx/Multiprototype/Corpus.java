package tamps.cinvestav.mx.Multiprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Corpus {
	//IdDoc,list<words>
	Map<String,List<Word>> corpus;
	List<CorpusDocuments> documents;
	//word,list<IdDocs>
	Map<Word,List<String>> wordReferences;
	Map<String,List<Double>> wordMatrixTermDoc;
	Map<String,List<Double>> coOccurrence;
	Map<String,List<Double>> normalizedCoOccurrence;
	Map<String,List<Double>> wordSpace;
	List<String> idDocList;
	List<Vector> vectorList;
	List<Word> generalIndex;
	List<Word> generalFrequentWIndex;
	List<Word> commonFeatures;
	int numberWordsCorpus;
	
	{
		corpus = new HashMap<String,List<Word>>();
		documents = new ArrayList<CorpusDocuments>();
		wordReferences = new HashMap<Word,List<String>>();
		wordMatrixTermDoc = new HashMap<String,List<Double>>();
		coOccurrence = new HashMap<String,List<Double>>();
		normalizedCoOccurrence = new HashMap<String,List<Double>>();
		wordSpace = new HashMap<String,List<Double>>();
		idDocList = new ArrayList<String>();
		vectorList = new ArrayList<Vector>();
		generalIndex = new ArrayList<Word>();
		generalFrequentWIndex = new ArrayList<Word>();
		commonFeatures = new ArrayList<Word>();
	}
	
	public Corpus(){
		
	}
	
	public Corpus(Corpus corpus){
		this.corpus.putAll(corpus.getCorpus());
		this.wordReferences.putAll(corpus.getWordReferences());
		this.vectorList.addAll(corpus.getVectorList());
	}
	
	public void printCorpus(){
		Set<String> keys = corpus.keySet();
		for(String key : keys){
			for(Word token : corpus.get(key)){
				System.out.print(", " + token.getWord());
			}
			System.out.println("\n");
		}
	}
	
	public void printWordReferences(){
		Set<Word> keys = this.wordReferences.keySet();
		for(Word key : keys){
			System.out.println(key.getWord() + " - Ref: ");
			for(String token : this.wordReferences.get(key)){
				System.out.print(" , " + token);
			}
			System.out.println("\n");
		}
	}
	
	public void printGeneralIndex(){
		for(Word token : this.generalIndex){
			System.out.println(token.getWord());
		}
	}
	
	public void printCoOccurrence(){
		for(String key : coOccurrence.keySet()){
			System.out.println("main Word = "+key);
			for(Double element : coOccurrence.get(key)){
				System.out.print(element+"\t");
			}
			System.out.println("\n");
		}
	}
	
	
//************************************Getter/Setter***************************************************//
	
	
	public Map<String, List<Word>> getCorpus() {
		return corpus;
	}

	public void setCorpus(Map<String, List<Word>> corpus) {
		this.corpus.putAll(corpus);
	}

	public Map<Word, List<String>> getWordReferences() {
		return wordReferences;
	}

	public void setWordReferences(Map<Word, List<String>> wordReferences) {
		this.wordReferences.putAll(wordReferences);
	}

	public List<Vector> getVectorList() {
		return vectorList;
	}

	public void setVectorList(List<Vector> vectorList) {
		this.vectorList.addAll(vectorList);
	}

	public List<Word> getGeneralIndex() {
		return generalIndex;
	}

	public void setGeneralIndex(List<Word> generalIndex) {
		this.generalIndex.addAll(generalIndex);
	}

	public List<String> getIdDocList() {
		return idDocList;
	}

	public void setIdDocList(List<String> idDocList) {
		this.idDocList.addAll(idDocList);
	}

	public Map<String, List<Double>> getWordMatrixTermDoc() {
		return wordMatrixTermDoc;
	}

	public void setWordMatrixTermDoc(Map<String, List<Double>> wordMatrixTermDoc) {
		this.wordMatrixTermDoc.putAll(wordMatrixTermDoc);
	}

	public Map<String, List<Double>> getCoOccurrence() {
		return coOccurrence;
	}

	public void setCoOccurrence(Map<String, List<Double>> coOccurrence) {
		this.coOccurrence.putAll(coOccurrence);
	}
	
	public Map<String, List<Double>> getNormalizedCoOccurrence() {
		return normalizedCoOccurrence;
	}

	public void setNormalizedCoOccurrence(Map<String, List<Double>> normalizedCoOccurrence) {
		this.normalizedCoOccurrence.putAll(normalizedCoOccurrence);
	}

	public List<Word> getCommonFeatures() {
		return commonFeatures;
	}

	public void setCommonFeatures(List<Word> commonFeatures) {
		this.commonFeatures.addAll(commonFeatures);
	}

	public int getNumberWordsCorpus() {
		return numberWordsCorpus;
	}

	public void setNumberWordsCorpus(int numberWordsCorpus) {
		this.numberWordsCorpus = numberWordsCorpus;
	}

	public List<CorpusDocuments> getDocuments() {
		return documents;
	}

	public void setDocuments(List<CorpusDocuments> documents) {
		this.documents.addAll(documents);
	}

	public Map<String, List<Double>> getWordSpace() {
		return wordSpace;
	}

	public void setWordSpace(Map<String, List<Double>> wordSpace) {
		this.wordSpace.putAll(wordSpace);
	}

	public List<Word> getGeneralFrequentWIndex() {
		return generalFrequentWIndex;
	}

	public void setGeneralFrequentWIndex(List<Word> generalFrequentWIndex) {
		this.generalFrequentWIndex.addAll(generalFrequentWIndex);
	}
}
