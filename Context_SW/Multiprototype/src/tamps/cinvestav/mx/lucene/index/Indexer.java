package tamps.cinvestav.mx.lucene.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import tamps.cinvestav.mx.utils.Utils;

public class Indexer {
	
	 private IndexWriter writer;
	 
	public static void createIndexCorpus(String indexPath, String outputCorpus) {
		try {
			Utils.deleteDirectoryContent(indexPath + "/");
			Indexer indexer = new Indexer(indexPath);
			int numIndexed;

			numIndexed = indexer.createIndexLucene(outputCorpus);
			
			indexer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	   public Indexer(String indexDirectoryPath) throws IOException{
	      //this directory will contain the indexes
	      Directory indexDirectory = 
	         FSDirectory.open(Paths.get(indexDirectoryPath));
	      
	      IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	      //create the indexer
	      writer = new IndexWriter(indexDirectory, config);
	   }

	   public void close() throws CorruptIndexException, IOException{
	      writer.close();
	   }

	   private Document getDocument(File file) throws IOException{
	      Document document = new Document();
	      try{
	    	  InputStream stream = Files.newInputStream(Paths.get(file.getAbsolutePath()));
	      
	      Field pathField = new StringField("IdDoc", file.getName(), Field.Store.YES);
	      document.add(pathField);
	      document.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
	      }catch(IOException e){
	    	  e.printStackTrace();
	      }
	      
	      return document;
	   }   

	   private void indexFile(File file) throws IOException{
	      System.out.println("Indexing "+file.getCanonicalPath());
	      Document document = getDocument(file);
	      writer.addDocument(document);
	   }

	   private int createIndexLucene(String dataDirPath) 
	      throws IOException{
	      //get all files in the data directory
	      File[] files = new File(dataDirPath).listFiles();

	      for (File file : files) {
	         if(!file.isDirectory()
	            && !file.isHidden()
	            && file.exists()
	            && file.canRead()
	         ){
	            indexFile(file);
	         }
	      }
	      return writer.numDocs();
	   }
}
