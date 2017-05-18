package gob.cinvestav.mx.pte.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gob.cinvestav.mx.pte.main.Main;

public class Utils {

	final static Logger logger = Logger.getLogger(Main.class);
	
	public static List<String> readLines(File inputFile){
		List<String> sentences = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			String line = "";
			int counterLines = 0;
			while((line = br.readLine()) != null){
				if(line.split(" ").length > 3){
					sentences.add(line.toLowerCase());
					counterLines++;
				}
			}
			System.out.println("\tNumber of sentences:\t" + counterLines);
		}catch(IOException e){
			logger.error("Error reading file - " + e);
		}
		return sentences;
	}
}
