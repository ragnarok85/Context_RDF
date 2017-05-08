package gob.cinvestav.mx.pte.main;

public class Garbage {

	public static void main(String... args){
		String test = "algo??";
		
		predicateRestrictions(test);
		
	}
	
	public static boolean predicateRestrictions(String predicate){
		boolean isAlpha = true;
		if(!predicate.contains("?")){
			if(!Character.isLetter(predicate.charAt(0))){
				System.out.println("the string begins without any letter " + predicate);
				isAlpha = false;
			}else
				System.out.println("the string begins with a letter " + predicate);
		}else
			System.out.println("the string contains the \"?\" character " +  predicate);
		return isAlpha;
	}
}
