package gob.cinvestav.mx.pte.ws;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity {
	String text;
	Set<String> uris;
	
	public Entity(){
		this.uris = new HashSet<String>();
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

	public Set<String> getUris() {
		return uris;
	}

	public void setUris(Set<String> uris) {
		this.uris = uris;
	}
}
