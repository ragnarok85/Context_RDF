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
}
