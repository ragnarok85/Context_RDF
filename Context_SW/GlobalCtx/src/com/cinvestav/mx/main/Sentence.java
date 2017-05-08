package com.cinvestav.mx.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sentence {
	int begin;
	int end;
	String sentence;
	Map<String,Double> listAlchemyTopics;
	Map<String,Double> listOpenCalaisTopics;
	String alchemyTopic;
	String openCalaisTopic;
	
	public Sentence(){
		this.begin = 0;
		this.end = 0;
		this.sentence = "";
		this.alchemyTopic = "";
		this.openCalaisTopic = "";
		this.listAlchemyTopics = new HashMap<String,Double>();
		this.listOpenCalaisTopics = new HashMap<String,Double>();
	}
	
	//**************Getter//Setter***********************//

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public Map<String,Double> getListAlchemyTopics() {
		return listAlchemyTopics;
	}

	public void setListAlchemyTopics(Map<String,Double> listAlchemyTopics) {
		this.listAlchemyTopics.putAll(listAlchemyTopics);
	}

	public Map<String,Double> getListOpenCalaisTopics() {
		return listOpenCalaisTopics;
	}

	public void setListOpenCalaisTopics(Map<String,Double> listOpenCalaisTopics) {
		this.listOpenCalaisTopics.putAll(listOpenCalaisTopics);
	}

	public String getAlchemyTopic() {
		return alchemyTopic;
	}

	public void setAlchemyTopic(String alchemyTopic) {
		this.alchemyTopic = alchemyTopic;
	}

	public String getOpenCalaisTopic() {
		return openCalaisTopic;
	}

	public void setOpenCalaisTopic(String openCalaisTopic) {
		this.openCalaisTopic = openCalaisTopic;
	}

}
