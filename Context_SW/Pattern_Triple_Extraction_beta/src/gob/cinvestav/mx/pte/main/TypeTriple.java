package gob.cinvestav.mx.pte.main;

import java.util.ArrayList;
import java.util.List;

public class TypeTriple {

	String subject;
	List<String> object;
	String relation;
	
	public TypeTriple(){
		this.subject = "";
		this.object = new ArrayList<String>();
		this.relation = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	}

	//*******************************Getter/Setter***************************//
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getObject() {
		return object;
	}

	public void setObject(List<String> object) {
		this.object.addAll(object);
	}

	
}
