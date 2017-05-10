package com.simplewiki.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CollectPages {
	
	/*
	 * args[0] - list of name pages
	 * args[1] - Path of simple wiki files
	 * args[2] - output of documents
	 */
	public static void main(String[] args) {
		if(args.length <= 0){
			System.out.println("Information is needed: list of pages file and path of the simplewiki pages");
		}
		
		File mainDirectory = new File(args[0]);
		String simpleWikiFilesPath = args[1];
		String mainOutpuWikiFiles = args[2];
		
		CollectPages cp = new CollectPages();
		
		cp.extractAllPages(mainDirectory,simpleWikiFilesPath);
		
//		String listPageFilePath = args[0];
//		String simpleWikiFilesPath = args[1];
//		String outpuWikiFiles = args[2];
//		
//		List<String> listPages = null;
//		List<String> listAbsolutePathSWFiles = new ArrayList<String>();
//		
//		CollectPages cp = new CollectPages();
//		
//		cp.initialSetup(args);
//		
//		listPages = cp.readListPageFile(listPageFilePath);
//		listAbsolutePathSWFiles = cp.extractSimpleWikiFilesPath(simpleWikiFilesPath);
//		
//		cp.extractSimpleWikiPages(outpuWikiFiles, listPages, listAbsolutePathSWFiles);
//		
//		for(String file : listAbsolutePathSWFiles){
//			System.out.println(file);
//		}
	}
	
	public void extractAllPages(File mainDirectory, String simpleWikiFilesPath){
		String[] files = mainDirectory.list();
		for(String file : files){
			System.out.println(file);
			File nFile = new File(mainDirectory.getAbsolutePath()+"/"+file);
			System.out.println("isDirectory? = " + nFile.isDirectory() + " " + nFile);
			if(nFile.isDirectory()){
				extractAllPages(nFile,simpleWikiFilesPath);
			}else if(nFile.isFile()){
				if(nFile.getName().equals("pages.txt")){
					List<String> listPages = readListPageFile(nFile.getAbsolutePath());
					List<String> listAbsolutePathSWFiles = new ArrayList<String>();
					
					listAbsolutePathSWFiles = extractSimpleWikiFilesPath(simpleWikiFilesPath);
					
					extractSimpleWikiPages(nFile.getAbsolutePath().replace(nFile.getName(), ""), listPages, listAbsolutePathSWFiles);
					
				}
			}
		}
	}
	
	public void initialSetup(String[] args){
		File outputDirectory = new File(args[2]);
		if(outputDirectory.listFiles().length > 0){
			for(File output : outputDirectory.listFiles()){
				if(!output.getName().equals("page.txt"))
					output.delete();
			}
		}
	}
	
	public List<String> readListPageFile(String listPageFilePath){
		List<String> listPages = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(listPageFilePath))){
			String line = "";
			while((line = br.readLine()) != null){
				listPages.add(line.trim());
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return listPages;
	}
	
	public List<String> extractSimpleWikiFilesPath(String simpleWikiFilesPath){
		File simpleWikiFiles = new File(simpleWikiFilesPath);
		File[] listSimpleWikiFiles = simpleWikiFiles.listFiles();
		List<String> listSWFiles = new ArrayList<String>();
		for(File listSimpleWikiFile : listSimpleWikiFiles){
			if(listSimpleWikiFile.isDirectory()){
				listSWFiles.addAll(extractDirectoryFiles(listSimpleWikiFile));
			}else{
				if(listSimpleWikiFile.getName().startsWith("wiki"))
					listSWFiles.add(listSimpleWikiFile.getAbsolutePath());
			}
		}
		return listSWFiles;
		
	}
	
	public List<String> extractDirectoryFiles(File listSimpleWikiFile){
		List<String> listSWFiles = new ArrayList<String>();
		File[] listFiles = listSimpleWikiFile.listFiles();
		
		for(File listFile : listFiles){
			if(listFile.getName().startsWith("wiki"))
				listSWFiles.add(listFile.getAbsolutePath());
		}
		
		return listSWFiles;
	}
	
	public void extractSimpleWikiPages(String outputDirectory,List<String> listPages, List<String> listAbsolutePathSWFiles){
		for(String AbsolutePathSWFile : listAbsolutePathSWFiles){
			try(BufferedReader br = new BufferedReader(new FileReader(AbsolutePathSWFile))){
				String line = "";
				boolean insideFile = false;
				while((line = br.readLine()) != null){
					if(line.contains("<doc") && line.contains("id=")){
						insideFile = true;
						//get the immediately next line which is the name of the file
						line = br.readLine();
					}else
						insideFile = false;
					
					if(insideFile){
						for(String page : listPages){
							if(line.trim().toLowerCase().equals(page)){
								if(page.contains("/")){
									page = page.replace("/", "-");
								}
								try(PrintWriter pw = new PrintWriter(new FileWriter(outputDirectory+"/"+page.replace(" ", "_")+".txt"))){
									while(!line.contains("</doc>")){
										pw.write(line + "\n");
										line = br.readLine();
									}
									pw.close();
								}catch(IOException e){
									e.printStackTrace();
								}
							}
						}
					}
				}
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

}
