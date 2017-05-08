package tamps.cinvestav.mx.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
	Map<String,Double> map;
	
	public ValueComparator(Map<String,Double> map){
		this.map = new HashMap<String,Double>();
		this.map.putAll(map);
	}
	
	public int compare(String s1, String s2) {
		if(map.get(s1) >= map.get(s2)){
			return -1;
		}else{
			return 1;
		}	
	}

}
