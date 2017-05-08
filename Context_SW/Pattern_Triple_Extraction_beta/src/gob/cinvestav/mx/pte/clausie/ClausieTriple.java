package gob.cinvestav.mx.pte.clausie;

import gob.cinvestav.mx.pte.main.Triple;

public class ClausieTriple {

	Subject subject;
	Relation relation;
	Argument argument;
	Triple triple; 
	public ClausieTriple(){
		this.subject = new Subject();
		this.relation = new Relation();
		this.argument = new Argument();
		this.triple = new Triple();
	}
	
	public String toString(){
		return "["+this.subject.getText() +","+ this.relation.getText() +","+this.argument.getText()+"]";
	}
	
	public String printTriple(){
		String text;
		if(!this.triple.getArgumentUris().isEmpty()){
			if(triple.getRelationUri().length() == 0){
				text = "["+triple.getSubjectUris()+","+triple.getRelation()+","+triple.getArgumentUris()+"]";
			}else{
				text = "["+triple.getSubjectUris()+","+triple.getRelationUri()+","+triple.getArgumentUris()+"]";
			}
			
			
		}else{
			text = "no URL triple for: " + "["+this.subject.getText()+"," + this.relation.getText()+","+this.argument.getText()+"]";
		}
			
		if(!subject.getWibi().getUris().isEmpty() && !triple.getSubjectUris().isEmpty()){
			System.out.println("[" + triple.getSubjectUris()+", http://www.w3.org/1999/02/22-rdf-syntax-ns#type," + subject.getWibi().getUris()+"]");
		}
		
		if(!argument.getWibi().getUris().isEmpty() && !triple.getArgumentUris().isEmpty()){
			System.out.println("[" + triple.getArgumentUris()+", http://www.w3.org/1999/02/22-rdf-syntax-ns#type," + argument.getWibi().getUris()+"]");
		}
		
		return text;
	}
	
	//***********Getter // Setter **************************//

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Argument getArgument() {
		return argument;
	}

	public void setArgument(Argument argument) {
		this.argument = argument;
	}
	
	public void setTriple(Subject subject, Relation relation, Argument argument) {
		this.subject = subject;
		this.relation = relation;
		this.argument = argument;
	}

	public Triple getTriple() {
		return triple;
	}

	public void setTriple(Triple triple) {
		this.triple = triple;
	}
	
	
	
}
