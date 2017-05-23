package gob.cinvestav.mx.pte.ws;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity {
	String text;
	Set<String> uris;
	Set<String> wikiUris;
	
	public Entity(){
		this.uris = new HashSet<String>();
		this.text = "";
		this.wikiUris = new HashSet<String>();
	}
	
	@Override
	public boolean equals(Object text){
		boolean returnVal = false;
		Entity txt = (Entity) text;
		if(txt instanceof Entity){
			this.text.equalsIgnoreCase(txt.getText());
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

	public Set<String> getWikiUris() {
		return wikiUris;
	}

	public void setWikiUris(Set<String> wikiUris) {
		this.wikiUris.addAll(wikiUris);
	}
}
