package tamps.cinvestav.mx.Multiprototype;

import java.util.ArrayList;
import java.util.List;


public class CorpusDocuments {
	String id;
	List<Word> documentWords;
	
	public CorpusDocuments(){
		this.documentWords = new ArrayList<Word>();
	}

	/////////////*************Getter/Setter*******************///////////
	public List<Word> getDocumentWords() {
		return documentWords;
	}

	public void setDocumentWords(List<Word> documentWords) {
		this.documentWords = documentWords;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
