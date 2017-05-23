package gob.cinvestav.mx.pte.sentence;

public class Word {
	
	String word;
	String posTag;
	int numberNouns;
	int start;
	int end;
	
	public Word(){
		
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(!(other instanceof Word)) return false;
		Word otherWord = (Word) other;
		if (otherWord.getWord().equalsIgnoreCase(this.getWord())
				&& otherWord.getPosTag().equalsIgnoreCase(this.getPosTag()) && otherWord.getStart() == this.getStart())
			return true;
		return false;
	}
	
	public int compareTo(Word o) {
		int returnValue = 0;
		if(this.getStart() > o.getStart()){
			returnValue = 1;
		}else if(this.getStart() < o.getStart()){
			returnValue = -1;
		}else if(this.getStart() == o.getStart()){
			returnValue = 0;
		}
		return returnValue;
	}
	

	//*************************************Getters // Setters ****************************************//
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getNumberNouns() {
		return numberNouns;
	}

	public void setNumberNouns(int numberNouns) {
		this.numberNouns = numberNouns;
	}
	
	
	
}
