package gob.cinvestav.mx.pte.clausie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gob.cinvestav.mx.pte.type.WiBiTypes;
import gob.cinvestav.mx.pte.ws.AlchemyEntities;
import gob.cinvestav.mx.pte.ws.BabelfyEntities;
import gob.cinvestav.mx.pte.ws.Entity;

public class Argument implements TripleElement{
	int numArgText = 0;
	String text = "";
	String textNE ="";
	List<Integer> listPosition;
	List<AlchemyEntities> alchemy;
	List<BabelfyEntities> babelfy;
//	List<Entity> entity;
	List<String> words;
	Map<String,Entity> entity;
	WiBiTypes wibi;
	int start;
	int end;
	
	public Argument(){
		this.listPosition = new ArrayList<Integer>();
		this.alchemy = new ArrayList<AlchemyEntities>();
		this.babelfy = new ArrayList<BabelfyEntities>();
		this.wibi = new WiBiTypes();
//		this.entity = new ArrayList<Entity>();
		this.entity = new HashMap<String, Entity>();
		this.words = new ArrayList<String>();
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

//	public List<Entity> getEntity() {
//		return entity;
//	}
//
//	public void setEntity(List<Entity> entity) {
//		this.entity.addAll(entity);
//	}
	public Map<String,Entity> getEntity() {
		return entity;
	}

	public void setEntity(Map<String,Entity> entity) {
		this.entity.putAll(entity);
	}

	public int getNumArgText() {
		return numArgText;
	}

	public void setNumArgText(int numArgText) {
		this.numArgText = numArgText;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	
}
