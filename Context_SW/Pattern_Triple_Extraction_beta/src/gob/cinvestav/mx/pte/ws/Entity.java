package gob.cinvestav.mx.pte.ws;

import java.util.HashSet;
import java.util.Set;

public class Entity {
	//TODO inserted by lti [May 26, 2017,5:58:17 PM] change Set for Map
	String text;
	Set<String> uris;
	Set<String> wikiUris;
//	Map<String,String> uri;
//	Map<String,String> wikiUri;
	
	public Entity(){
		this.uris = new HashSet<String>();
		this.text = "";
		this.wikiUris = new HashSet<String>();
//		this.uri = new HashMap<String,String>();
//		this.wikiUri = new HashMap<String,String>();
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

//	public Map<String, String> getUri() {
//		return uri;
//	}
//
//	public void setUri(Map<String, String> uri) {
//		this.uri.putAll(uri);
//	}
//
//	public Map<String, String> getWikiUri() {
//		return wikiUri;
//	}
//
//	public void setWikiUri(Map<String, String> wikiUri) {
//		this.wikiUri.putAll(wikiUri);
//	}

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
