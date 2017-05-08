package gob.cinvestav.mx.pte.clausie;

import java.util.ArrayList;
import java.util.List;

public class Relation implements TripleElement{
	String text;
	String uri;
	List<Integer> listPosition;
	int start;
	int end;
	
	public Relation(){
		this.listPosition = new ArrayList<Integer>();
	}

	//***********Getter // Setter **************************//
	
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
	
	public List<Integer> getListPosition() {
		return listPosition;
	}

	public void setListPosition(List<Integer> listPosition) {
		this.listPosition.addAll(listPosition);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
