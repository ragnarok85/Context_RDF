package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.List;

public class AlchemyEntities implements Entities{
	
	String text = "";
	String dbpediaURL = "";
	String freebaseURL = "";
	String type = "";
	List<Integer> listPosition;
	double relevance;
	int count = 0;
	int start = 0;
	int end = 0;
	
	
	//***************** Getters // Setters *************************************//
	
	public AlchemyEntities(){
		this.listPosition = new ArrayList<Integer>();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDbpediaURL() {
		return dbpediaURL;
	}

	public void setDbpediaURL(String dbpediaURL) {
		this.dbpediaURL = dbpediaURL;
	}

	public String getFreebaseURL() {
		return freebaseURL;
	}

	public void setFreebaseURL(String freebaseURL) {
		this.freebaseURL = freebaseURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Integer> getListPosition() {
		return listPosition;
	}

	public void setListPosition(List<Integer> listPosition) {
		this.listPosition.addAll(listPosition);
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
	
	

}
