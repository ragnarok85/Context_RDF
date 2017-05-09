package main.mybatis.tutorial;

import java.util.ArrayList;
import java.util.List;

public class SubCategory {
	int level;
	String parent;
	String subcategory;
	List<CategoryVO> childen;
	
	public SubCategory(){
		this.childen = new ArrayList<CategoryVO>();
	}
	
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public List<CategoryVO> getChilden() {
		return childen;
	}
	public void setChilden(List<CategoryVO> childen) {
		this.childen.addAll(childen);
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	 

}
