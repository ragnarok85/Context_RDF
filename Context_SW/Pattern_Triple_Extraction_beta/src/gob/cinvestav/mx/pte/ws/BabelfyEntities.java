package gob.cinvestav.mx.pte.ws;

import java.util.List;
import java.util.ArrayList;


public class BabelfyEntities implements Entities{

	String text;
	String dbpediaURL;
	String babelNetURL;
	String source;
	List<Integer> listPosition;
	double score;
	double coherenceScore;
	double globalScore;
	int start;
	int end;
	
	public BabelfyEntities(){
		this.listPosition = new ArrayList<Integer>();
	}
	
	//************************************Getter // Setter ******************************************//

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

	public List<Integer> getListPosition() {
		return listPosition;
	}

	public void setListPosition(List<Integer> listPosition) {
		this.listPosition.addAll(listPosition);
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

	public String getBabelNetURL() {
		return babelNetURL;
	}

	public void setBabelNetURL(String babelNetURL) {
		this.babelNetURL = babelNetURL;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getCoherenceScore() {
		return coherenceScore;
	}

	public void setCoherenceScore(double coherenceScore) {
		this.coherenceScore = coherenceScore;
	}

	public double getGlobalScore() {
		return globalScore;
	}

	public void setGlobalScore(double globalScore) {
		this.globalScore = globalScore;
	}
}
