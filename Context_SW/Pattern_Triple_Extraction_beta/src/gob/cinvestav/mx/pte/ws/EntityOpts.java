package gob.cinvestav.mx.pte.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class EntityOpts {
	/*
	 * (i) 		extract AlchemyEntities 
	 * (ii) 	extract BabelfyEntities 
	 * (iii) 	fuse uris
	 */
	
	final static Logger logger = Logger.getLogger(EntityOpts.class);
	
	//(i)
	public static Set<AlchemyEntities> extractAlchemyEntities(String sentence) {
		Set<AlchemyEntities> alchemy;
		alchemy = new AlchemyWS().extractEntities(sentence);
		return alchemy;
	}
	
	//(ii)
	public static Set<BabelfyEntities> extractBabelfyEntities(String sentence) {
		Set<BabelfyEntities> babelfy = null;

		try {
			babelfy = new BabelfyWS().extractEntities(sentence);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return babelfy;
	}

	//(iii) fuse uris
	public static void fuseAlchemyUris(Set<AlchemyEntities> alchemy, Set<Entity> entities){
		if(!entities.isEmpty()){
			for(AlchemyEntities alchemyEntity : alchemy){
				Entity entity = new Entity();
				Set<String> uris = new HashSet<String>();
				entity.setText(alchemyEntity.getText().toLowerCase());
				if(alchemyEntity.getDbpediaURL().length() > 0)
					uris.add(alchemyEntity.getDbpediaURL());
//				if(alchemyEntity.getFreebaseURL().length() > 0)
//					uris.add(alchemyEntity.getFreebaseURL());
				entity.setUris(uris);
				entities.add(entity);
			}
		}else{
			for(AlchemyEntities alchemyEntity : alchemy){
				if(!entities.contains(alchemyEntity.getText())){
					Entity entity = new Entity();
					Set<String> uris = new HashSet<String>();
					entity.setText(alchemyEntity.getText().toLowerCase());
					if(alchemyEntity.getDbpediaURL().length() > 0)
						uris.add(alchemyEntity.getDbpediaURL());
//					if(alchemyEntity.getFreebaseURL().length() > 0)
//						uris.add(alchemyEntity.getFreebaseURL());
					entity.setUris(uris);
					entities.add(entity);
				}
			}
		}
	}
	
	public static void fuseBabelfyUris(Set<BabelfyEntities> babelfy, Set<Entity> entities){
		if(!entities.isEmpty()){
			for(BabelfyEntities babelfyEntity : babelfy){
				Entity entity = new Entity();
				Set<String> uris = new HashSet<String>();
				entity.setText(babelfyEntity.getText());
//				if(babelfyEntity.getDbpediaURL().length() > 0)
				if(babelfyEntity.getDbpediaURL() != null)
					uris.add(babelfyEntity.getDbpediaURL());
//				if(babelfyEntity.getBabelNetURL().length() > 0)
//					uris.add(babelfyEntity.getBabelNetURL());
				entity.setUris(uris);
				entities.add(entity);
			}
		}else{
			for(BabelfyEntities babelfyEntity : babelfy){
				if(!entities.contains(babelfyEntity.getText())){
					Entity entity = new Entity();
					Set<String> uris = new HashSet<String>();
					entity.setText(babelfyEntity.getText());
//					if(babelfyEntity.getDbpediaURL().length() > 0)
					if(babelfyEntity.getDbpediaURL() != null)
						uris.add(babelfyEntity.getDbpediaURL());
//					if(babelfyEntity.getBabelNetURL().length() > 0)
//						uris.add(babelfyEntity.getBabelNetURL());
					entity.setUris(uris);
					entities.add(entity);
				}
			}
		}
	}
	
	public static void printEntities(Set<Entity> entities){
		for(Entity entity : entities){
			if(!entity.getUris().isEmpty()){
				logger.info("Text = " + entity.getText());
				logger.info("Uris = " + entity.getUris());
			}
		}
	}
}
