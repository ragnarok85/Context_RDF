package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.jlt.util.Language;

public class BabelfyWS {
	final static Logger logger = Logger.getLogger(BabelfyWS.class);
	
	public List<BabelfyEntities> extractEntities(String texto) throws Exception {
		

        List<BabelfyEntities> bEntities = new ArrayList<BabelfyEntities>();

        Babelfy bfy = new Babelfy();
        
        List<SemanticAnnotation> bfyAnnotations = bfy.babelfy(texto,Language.EN);
        
        for(SemanticAnnotation annotation : bfyAnnotations){
        	BabelfyEntities bEntity = new BabelfyEntities();
            	bEntity.setStart(annotation.getCharOffsetFragment().getStart());
            	bEntity.setEnd(annotation.getCharOffsetFragment().getEnd()+1);
            	bEntity.setText(texto.substring(annotation.getCharOffsetFragment().getStart(),annotation.getCharOffsetFragment().getEnd()+1));
            	bEntity.setDbpediaURL(annotation.getDBpediaURL());
            	bEntity.setBabelNetURL(annotation.getBabelNetURL());
            	bEntity.setScore(annotation.getScore());
            	bEntity.setCoherenceScore(annotation.getCoherenceScore());
            	bEntity.setGlobalScore(annotation.getGlobalScore());
            	bEntity.setSource(annotation.getSource().name());
            	if(!(bEntity.getDbpediaURL() == null) && !bEntity.getDbpediaURL().isEmpty())
            		if(namedEntityContainText(bEntity.getDbpediaURL(),bEntity.getText()))
            			bEntities.add(bEntity);
        	//}
        	
        }
        return reduceList(bEntities);
    }
	
	public static boolean namedEntityContainText(String uri, String text){
		boolean containText = false;
		
		String splitUri[] = uri.split("/");
		String lastUriElement = splitUri[splitUri.length - 1].toLowerCase();
		lastUriElement = lastUriElement.replace("_", " ");
		logger.info("++++This text \"" + text + "\" is contained in this segment of URI \"" + lastUriElement+"\"? = " + lastUriElement.equals(text));
		
		if(lastUriElement.equals(text))
			containText = true;
		return containText;
	}
	
	public static List<BabelfyEntities> reduceList(List<BabelfyEntities> entities){
		List<BabelfyEntities> endResult = new ArrayList<BabelfyEntities>();
		
		boolean isContained = false;
		for(BabelfyEntities entityOne : entities){
			for(BabelfyEntities entityTwo : entities){
				if(entityTwo.text.contains(" ") && !entityOne.text.contains(" ") && entityTwo.text.contains(entityOne.text))
					isContained = true;
			}
			if(!isContained){
				endResult.add(entityOne);
				System.out.println(entityOne.text);
			}
			isContained=false;
		}
		
		return endResult;
	}
	
	public static void main(String args[]) throws Exception{
		BabelfyWS ws = new BabelfyWS();
		
		String texto = "a computer programmer is a person who makes computer programs using a programming language.";
		List<BabelfyEntities> entities = ws.extractEntities(texto);
		
		for(BabelfyEntities entity : entities){
				texto = texto.replaceAll("\\b"+entity.text.toLowerCase()+"\\b", "<"+entity.dbpediaURL+">");
		}
		
		System.out.println(texto);
	}
}
