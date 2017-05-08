package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.List;

public class Entity {
	String text;
	List<String> uris;
	
	public Entity(){
		this.uris = new ArrayList<String>();
		this.text = "";
	}
	
	@Override
	public boolean equals(Object text){
		boolean returnVal = false;
		if(text instanceof String){
			this.text.equalsIgnoreCase((String)text);
			returnVal = true;
		}else{
			returnVal = false;
		}
		return returnVal;
	}
	
	//**********************Getters/Setters*************************
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getUris() {
		return uris;
	}

	public void setUris(List<String> uris) {
		this.uris = uris;
	}
}
