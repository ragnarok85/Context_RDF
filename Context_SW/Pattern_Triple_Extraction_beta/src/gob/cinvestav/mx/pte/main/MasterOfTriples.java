package gob.cinvestav.mx.pte.main;

import java.util.ArrayList;
import java.util.List;

import gob.cinvestav.mx.pte.clausie.ClausieTriple;
import gob.cinvestav.mx.pte.sentence.Word;
import gob.cinvestav.mx.pte.ws.AlchemyEntities;
import gob.cinvestav.mx.pte.ws.BabelfyEntities;
import gob.cinvestav.mx.pte.ws.Entity;

public class MasterOfTriples {

	List<Word> sntsWrds;
	List<AlchemyEntities> alchemyEntities;
	List<BabelfyEntities> babelfyEntities;
	List<Entity> entities;
	List<ClausieTriple> clausieTriples;
	
	public MasterOfTriples(){
		this.sntsWrds = new ArrayList<Word>();
		this.alchemyEntities = new ArrayList<AlchemyEntities>();
		this.babelfyEntities = new ArrayList<BabelfyEntities>();
		this.clausieTriples = new ArrayList<ClausieTriple>();
		this.entities = new ArrayList<Entity>();
	}
	
	public void printClausieTriples(){
		int counter = 1;
		System.out.println("clausie triples information : \n");
		for(ClausieTriple triple : clausieTriples){
			System.out.println("=================================");
			System.out.println(counter + ".-" + " [" + "(" + triple.getSubject().getStart() + "-"
					+ triple.getSubject().getEnd() + ")" + triple.getSubject().getText() + ", " + "("
					+ triple.getRelation().getStart() + "-" + triple.getRelation().getEnd() + ")"
					+ triple.getRelation().getText() + ", " + "(" + triple.getArgument().getStart() + "-"
					+ triple.getArgument().getEnd() + ")" + triple.getArgument().getText() + "]");
			counter++;
		}
		System.out.println("\n");
	}
	
	public void printWords(){
		System.out.println("Word's information : ");
		System.out.println("__________________________________________________");
		System.out.println("||Word" + "\t" + "POS-Tag" + "\t" + "(start-end)\t||");
		for(Word word : sntsWrds){
			System.out.println("____________________________________________");
			System.out.println("||" + word.getWord() + "\t" + word.getPosTag() + "\t(" + word.getStart()
					+ "-" + word.getEnd() + ")\t||");
		}
		System.out.println("__________________________________________________");
		System.out.println("\n");
	}
	
	public void printAlchemyEntities(){
		System.out.println("Alchemy entities information : \n");
		for(AlchemyEntities alchemy :alchemyEntities){
			System.out.println("=================================");
			System.out.println("text = " + alchemy.getText());
			System.out.println("relevance = " + alchemy.getRelevance());
			System.out.println("type = " + alchemy.getType());
			System.out.println("dbpedia URL = " + alchemy.getDbpediaURL());
			System.out.println("freebase URL = " + alchemy.getFreebaseURL());
			System.out.println("count = " + alchemy.getCount());
			System.out.println("Position in list = " + alchemy.getListPosition());
		}
		System.out.println("\n");
	}
	
	public void printBabelfyEntities(){
		System.out.println("Babelfy entities information : \n");
		for(BabelfyEntities babelfy :babelfyEntities){
			System.out.println("=================================");
			System.out.println("text = " + babelfy.getText());
			System.out.println("start = " + babelfy.getStart());
			System.out.println("end = " + babelfy.getEnd());
			System.out.println("BabelNet URL = " + babelfy.getBabelNetURL());
			System.out.println("dbpedia URL = " + babelfy.getDbpediaURL());
			System.out.println("score = " + babelfy.getScore());
			System.out.println("coherence score = " + babelfy.getCoherenceScore());
			System.out.println("global score = " + babelfy.getGlobalScore());
			System.out.println("Position in list = " + babelfy.getListPosition());
		}
		System.out.println("\n");
	}

	//**********************Getters//Setters******************************//
	public List<Word> getSntsWrds() {
		return sntsWrds;
	}

	public void setSntsWrds(List<Word> sntsWrds) {
		this.sntsWrds.addAll(sntsWrds);
	}

	public List<AlchemyEntities> getAlchemyEntities() {
		return alchemyEntities;
	}

	public void setAlchemyEntities(List<AlchemyEntities> alchemyEntities) {
		this.alchemyEntities.addAll(alchemyEntities);
	}

	public List<BabelfyEntities> getBabelfyEntities() {
		return babelfyEntities;
	}

	public void setBabelfyEntities(List<BabelfyEntities> babelfyEntities) {
		this.babelfyEntities.addAll(babelfyEntities);
	}

	public List<ClausieTriple> getClausieTriples() {
		return clausieTriples;
	}

	public void setClausieTriples(List<ClausieTriple> clausieTriples) {
		this.clausieTriples.addAll(clausieTriples);
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities.addAll(entities);
	}
	
	
}
