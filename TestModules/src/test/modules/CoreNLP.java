package test.modules;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLP {

	public static void main(String[] args) {
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = "An example of this is the Hello world program.";

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        
        // these are all the sentences in this document
     // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
     List<CoreMap> sentences = document.get(SentencesAnnotation.class);

     for(CoreMap sentence: sentences) {
       // traversing the words in the current sentence
       // a CoreLabel is a CoreMap with additional token-specific methods
    	 String lemmaSnt  ="";
         System.out.println(sentence.toString());
       for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
         // this is the text of the token
         String word = token.get(TextAnnotation.class);
         System.out.println("word = "+ word);
         // this is the POS tag of the token
         String pos = token.get(PartOfSpeechAnnotation.class);
         System.out.println("pos = " + pos);
         // this is the NER label of the token
         String ne = token.get(NamedEntityTagAnnotation.class);
         System.out.println("ne = " + ne);
         
         String lemmatoken = token.get(LemmaAnnotation.class);
         lemmaSnt += lemmatoken + " ";
         System.out.println("lemma = " + lemmatoken);
       }
       System.out.println("lemma Sentence  = " + lemmaSnt.replace("-lrb-", "(").replace("-rrb-", ")"));
       // this is the parse tree of the current sentence
       Tree tree = sentence.get(TreeAnnotation.class);

       // this is the Stanford dependency graph of the current sentence
       SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
     }

     // This is the coreference link graph
     // Each chain stores a set of mentions that link to each other,
     // along with a method for getting the most representative mention
     // Both sentence and token offsets start at 1!
     Map<Integer, CorefChain> graph = 
       document.get(CorefChainAnnotation.class);
     
     for(Integer key : graph.keySet()){
    	 System.out.println("graph = " + graph.get(key));
     }
	}

}
