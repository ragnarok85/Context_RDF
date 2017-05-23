package gob.cinvestav.mx.pte.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WiBiTypes {
	
	Set<String> uris;
	
	public WiBiTypes(){
		this.uris = new HashSet<String>();
	}

	//**********************Getters/Setters*****************************//
	
	public Set<String> getUris() {
		return uris;
	}

	public void setUris(Set<String> uris) {
		this.uris = uris;
	}

}
