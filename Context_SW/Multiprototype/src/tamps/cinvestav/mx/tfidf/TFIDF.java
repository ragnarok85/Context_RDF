package tamps.cinvestav.mx.tfidf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import tamps.cinvestav.mx.Multiprototype.Corpus;
import tamps.cinvestav.mx.Multiprototype.Word;

public class TFIDF {
	
	public void tf(String idDoc, List<Word> words, Word term){
		double frequency = 0;
		int counter = 0;
		for(Word word : words){
			if(term.getWord().equalsIgnoreCase(word.getWord())){
				frequency++;
				counter++;
			}
		}
		frequency = frequency/words.size();
		term.addDocFrequency(idDoc, frequency);
		//term.setIdDoc(idDoc);
		//term.setFrequencyDoc(counter);
		//term.setTf(frequency/words.size());
	}
	
	public void idf(Map<String,List<Word>> corpus, Word term){
		double result = 0;
		int counter = 0;
			Set<String> keys = corpus.keySet();
			for(String key : keys){
				for(Word word : corpus.get(key)){
					if(term.getWord().equalsIgnoreCase(word.getWord())){
						result++;
						counter++;
						break;
					}
				}
			}
			
		term.setFrequencyInAllCorp(counter);
//		System.out.println(keys.size()  + " "+result +" ln(keys.size()/result) = "+ Math.log(keys.size()/result));
		term.setIdf(Math.log(keys.size()/result));
	}
	
	public void tfIdf(Corpus corp) {
		Set<String> keys = corp.getCorpus().keySet();
		for(String key : keys){
			for(Word word :corp.getCorpus().get(key) ){
				word.setTfidf(word.getDocFrequency().get(key) * word.getIdf());
			}
		}
	}


}
