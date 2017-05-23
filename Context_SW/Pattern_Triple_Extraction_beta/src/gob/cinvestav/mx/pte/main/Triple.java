package gob.cinvestav.mx.pte.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Triple {

	List<String> subjectUris;
	List<String> argumentUris;
	String relation;
	String relationUri;
	
	public Triple(){
		this.subjectUris = new ArrayList<String>();
		this.argumentUris = new ArrayList<String>();
		this.relation = "";
		this.relationUri = "";
	}
	
	@Override
	public String toString(){
		return subjectUris + " " + relation  + " " + argumentUris;
		
	}

	//*****************Getter // Setters *************************
	
	public List<String> getSubjectUris() {
		return subjectUris;
	}

	public void setSubjectUris(List<String> subjectUris) {
		this.subjectUris.addAll(subjectUris);
	}

	public List<String> getArgumentUris() {
		return argumentUris;
	}

	public void setArgumentUris(List<String> argumentUris) {
		this.argumentUris.addAll(argumentUris);
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getRelationUri() {
		return relationUri;
	}

	public void setRelationUri(String relationUri) {
		this.relationUri = relationUri;
	}
}
