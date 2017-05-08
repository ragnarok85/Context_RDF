package gob.cinvestav.mx.pte.type;

import java.util.ArrayList;
import java.util.List;

public class WiBiTypes {
	
	List<String> uris;
	
	public WiBiTypes(){
		this.uris = new ArrayList<String>();
	}

	//**********************Getters/Setters*****************************//
	
	public List<String> getUris() {
		return uris;
	}

	public void setUris(List<String> uris) {
		this.uris = uris;
	}

}
