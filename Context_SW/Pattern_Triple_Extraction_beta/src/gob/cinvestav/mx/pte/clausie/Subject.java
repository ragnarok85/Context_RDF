package gob.cinvestav.mx.pte.clausie;

import java.util.ArrayList;
import java.util.List;

import gob.cinvestav.mx.pte.type.WiBiTypes;
import gob.cinvestav.mx.pte.ws.AlchemyEntities;
import gob.cinvestav.mx.pte.ws.BabelfyEntities;

public class Subject implements TripleElement{
	String text = "";
	String textNE = "";
	List<AlchemyEntities> alchemy;
	List<BabelfyEntities> babelfy;
	WiBiTypes wibi;
	List<Integer> listPosition;
	int start;
	int end;
	
	public Subject(){
		this.listPosition = new ArrayList<Integer>();
		this.alchemy = new ArrayList<AlchemyEntities>();
		this.babelfy = new ArrayList<BabelfyEntities>();
		this.wibi = new WiBiTypes();
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

	public List<AlchemyEntities> getAlchemy() {
		return alchemy;
	}

	public void setAlchemy(List<AlchemyEntities> alchemy) {
		this.alchemy = alchemy;
	}

	public List<BabelfyEntities> getBabelfy() {
		return babelfy;
	}

	public void setBabelfy(List<BabelfyEntities> babelfy) {
		this.babelfy = babelfy;
	}

	public String getTextNE() {
		return textNE;
	}

	public void setTextNE(String textNE) {
		this.textNE = textNE;
	}

	public WiBiTypes getWibi() {
		return wibi;
	}

	public void setWibi(WiBiTypes wibi) {
		this.wibi = wibi;
	}
	

}
