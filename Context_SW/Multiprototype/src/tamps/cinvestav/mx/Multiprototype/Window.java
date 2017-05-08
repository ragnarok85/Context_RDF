package tamps.cinvestav.mx.Multiprototype;

import java.util.ArrayList;
import java.util.List;

public class Window {
	Word word;
	List<Word> rightNeighbours;
	List<Word> leftNeighbours;
	
	public Window(){
		this.rightNeighbours = new ArrayList<Word>();
		this.leftNeighbours = new ArrayList<Word>();
	}
	
	/*
	 * Getter and Setters
	 */

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<Word> getRightNeighbours() {
		return rightNeighbours;
	}

	public void setRightNeighbours(List<Word> rightNeighbours) {
		this.rightNeighbours = rightNeighbours;
	}
	
	public List<Word> getLeftNeighbours() {
		return leftNeighbours;
	}

	public void setLeftNeighbours(List<Word> leftNeighbours) {
		this.leftNeighbours = leftNeighbours;
	}

}
