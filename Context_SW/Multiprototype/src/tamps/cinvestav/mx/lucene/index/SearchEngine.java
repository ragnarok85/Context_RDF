package tamps.cinvestav.mx.lucene.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import tamps.cinvestav.mx.Multiprototype.Corpus;
import tamps.cinvestav.mx.Multiprototype.Word;

public class SearchEngine {
	
	IndexSearcher indexSearcher;
	   QueryParser queryParser;
	   Query query;
	   
	public static void searchIndex(String luceneIndexPath, Corpus corp) {
		try {
			List<String> docs = new ArrayList<String>();
			SearchEngine searcher = new SearchEngine(luceneIndexPath);
			TopDocs hits;
			List<Word> tokens2Delete = new ArrayList<Word>();

			try {
				for (Word token : corp.getGeneralIndex()) {
					hits = searcher.search(token.getWord());
					for (ScoreDoc scoreDoc : hits.scoreDocs) {
						Document doc = searcher.getDocument(scoreDoc);
						docs.add(doc.get("IdDoc"));
					}
					if (!docs.isEmpty()) {
						corp.getWordReferences().put(token, new ArrayList<String>(docs));
						token.setIdDocuments(docs);
					} else {
						System.out.println("The word " + token.getWord() + " is not in index");
						tokens2Delete.add(token);
					}
					docs.clear();
					searcher.close();
				}
				for (Word token : tokens2Delete) {
					corp.getGeneralIndex().remove(token);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	   public SearchEngine(String indexDirectoryPath) throws IOException{
		   IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectoryPath)));
	      indexSearcher = new IndexSearcher(reader);
	      Analyzer analyzer = new StandardAnalyzer();
	      queryParser = new QueryParser("contents", analyzer);
	   }

	   public TopDocs search( String searchQuery) 
	      throws IOException, ParseException{
	      query = queryParser.parse(QueryParser.escape(searchQuery));
	      return indexSearcher.search(query, 100);
	   }

	   public Document getDocument(ScoreDoc scoreDoc) 
	      throws CorruptIndexException, IOException{
	     return indexSearcher.doc(scoreDoc.doc);	
	   }

	   public void close() throws IOException{
//	      indexSearcher.close();
	   }

}
