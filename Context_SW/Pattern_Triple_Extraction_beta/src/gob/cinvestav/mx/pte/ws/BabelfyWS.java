package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.List;

import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.jlt.util.Language;

public class BabelfyWS {
	
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
            	bEntities.add(bEntity);
        	//}
        	
        }
        return bEntities;
    }
	
	public static void main(String args[]) throws Exception{
		BabelfyWS ws = new BabelfyWS();
		
		String texto = "a computer programmer is a person who makes computer programs using a programming language.";
		List<BabelfyEntities> entities = ws.extractEntities(texto);
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
		
		for(BabelfyEntities entity : endResult){
				texto = texto.replace(entity.text, " < "+ entity.getDbpediaURL() + "> ");
		}
		
		System.out.println(texto);
	}
}
