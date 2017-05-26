package gob.cinvestav.mx.pte.main;

import java.util.ArrayList;
import java.util.List;

public class Triple {
//TODO inserted by lti [May 26, 2017,5:57:57 PM] Change list for Map
	List<String> subjectUris;
	List<String> argumentUris;
//	Map<String,String> subjectUris;
//	Map<String,String> argumentUris;
	String relation;
	String relationUri;
	
	public Triple(){
		this.subjectUris = new ArrayList<String>();
		this.argumentUris = new ArrayList<String>();
//		this.subjectUris = new HashMap<String,String>();
//		this.argumentUris = new HashMap<String,String>();
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

//	public Map<String, String> getSubjectUris() {
//		return subjectUris;
//	}
//
//	public void setSubjectUris(Map<String, String> subjectUris) {
//		this.subjectUris.putAll(subjectUris);
//	}
//
//	public Map<String, String> getArgumentUris() {
//		return argumentUris;
//	}
//
//	public void setArgumentUris(Map<String, String> argumentUris) {
//		this.argumentUris.putAll(argumentUris);
//	}
}
