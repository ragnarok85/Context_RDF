package tamps.cinvestav.mx.Multiprototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Vector {

	Word mainWord;
	List<List<Word>> wordWindow;//All occurrence 
	List<List<Word>> windows;//k occurrences
	
	double tfIdf;
	List<String> idDocs;
	
	{
		this.mainWord = new Word();
		this.wordWindow = new ArrayList<List<Word>>();
		this.idDocs = new ArrayList<String>();
	}
	
	public Vector(){
		
	}
	
	public Vector(Vector vector){
		this.mainWord = vector.getMainWord();
		this.wordWindow = vector.getWordWindow();
		this.idDocs = vector.getIdDocs();
	}
	
	public void printVector() {
		if (mainWord.getWord() != null) {
			System.out.format("main word : %s (%.4f)%n",this.mainWord.getWord(), mainWord.getTfidf());
			for (List<Word> windows : wordWindow) {
				System.out.print("neighbours : ");
				for (Word token : windows) {
					System.out.format("%s (%.4f) ",token.getWord(),token.getTfidf());
				}
				System.out.println("\n");
			}
		}

	}

	//********************Getter/Setter**************************//
	public Word getMainWord() {
		return mainWord;
	}

	public void setMainWord(Word mainWord) {
		this.mainWord = mainWord;
	}

	public List<List<Word>> getWordWindow() {
		return wordWindow;
	}

	public void setWordWindow(List<List<Word>> wordWindow) {
		this.wordWindow.addAll(wordWindow);
	}

	public double getTfIdf() {
		return tfIdf;
	}

	public void setTfIdf(double tfIdf) {
		this.tfIdf = tfIdf;
	}

	public List<String> getIdDocs() {
		return idDocs;
	}

	public void setIdDocs(List<String> idDocs) {
		this.idDocs.addAll(idDocs);
	}
}
