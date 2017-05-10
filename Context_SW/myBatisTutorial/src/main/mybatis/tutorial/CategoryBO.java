package main.mybatis.tutorial;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class CategoryBO {
	
	static List<SubCategory> subCats = new ArrayList<SubCategory>();
	static Map<Integer,List<Map<String,List<CategoryVO>>>> treeMap = new HashMap<Integer,List<Map<String,List<CategoryVO>>>>();
	
	//Begin - Functions to retrieve information from the DB
	public List<CategoryVO> getInformation(CategoryVO cat) throws Exception{
		SqlSession session = ConnectionFactory.getSession().openSession();
		CategoryDAO dao = session.getMapper(CategoryDAO.class);
		List<CategoryVO> categories = dao.getInformation(cat);
		session.close();
		return categories;
	}
	//End
	
	public static void main(String[] args) throws Exception{
		CategoryBO bo = new CategoryBO();
		CategoryVO cat = new CategoryVO();
		
		String mainCategory = "computer_science";
		
		//Begin: initial parameters to retrieve all categories and subcategories
		cat.setCat_title(mainCategory);
		cat.setCl_type("subcat"); //options: subcat or page
		//End
		
		//retrieve sub-categories with initial parameters
		List<CategoryVO> catVO = bo.getInformation(cat);
		
		//Begin: setting root node parameters
		SubCategory subCat = new SubCategory();
		subCat.setParent("root");
		subCat.setSubcategory(mainCategory);
		subCat.setChilden(catVO);
		subCats.add(subCat);
		//End
		
		//Begins the recursive process to retrieve all sub-categories
		File root = new File(mainCategory);
		root.mkdir();
		bo.createDirectoryTreeRoot(root,mainCategory, catVO);
//		bo.retrieveCats(0,mainCategory,catVO);
		
		
		//With the list of all categories a tree is created using a list of map elements
//		bo.createTree("root",0);
//		bo.printTreeMap();
		
		//Finally, the result is saved to a file
//		bo.printCategoriesToFile();
	}
	
	public void createDirectoryTreeRoot(File root, String category,List<CategoryVO> categories){
		CategoryBO bo = new CategoryBO();
		File categoryDir = null;
		
		if(!root.getName().equals(category)){
			categoryDir = new File(root.getAbsolutePath()+"/"+category);
		}else
			categoryDir = root;
		
//		if(!bo.categoryDirExist(root,categoryDir)){
//			
//		}
		
		for(CategoryVO cats : categories){
			File categ = new File(categoryDir.getAbsolutePath()+"/"+cats.getCl_sortkey());
			categ.mkdir();
			
			CategoryVO cat = new CategoryVO();
			cat.setCat_title(cats.getCl_sortkey());
			cat.setCl_type("subcat");
			
			try {
				List<CategoryVO> listCats = bo.getInformation(cat);
				
				if(!listCats.isEmpty()){
					createDirectoryTreeRoot(categ,cats.getCl_sortkey(),listCats);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}
	
	public boolean categoryDirExist(File rootDir, File categoryDir){
		boolean dirExists = false;
		File[] listRootDirs = rootDir.listFiles();
		for(File rDir : listRootDirs){
			if(rDir.isDirectory()){
				categoryDirExist(rDir, categoryDir);
			}else if(rDir.getAbsolutePath().equals(categoryDir.getAbsolutePath())){
				dirExists = true;
			}else
				dirExists = false;
		}
		return dirExists;
	}
	
	public void retrieveCats(int level,String category,List<CategoryVO> categories){
		CategoryBO bo = new CategoryBO();
		
		for(CategoryVO cats : categories){
			SubCategory subCat = new SubCategory();
			
			System.out.println("\n\n*****processing Subcategories of " + category +": " + cats.getCl_sortkey() + "\n");
			
			CategoryVO cat = new CategoryVO();
			cat.setCat_title(cats.getCl_sortkey());
			cat.setCl_type("subcat");
			
			try {
				List<CategoryVO> listCats = bo.getInformation(cat);
				
				if(!listCats.isEmpty()){
					System.out.println("=======Printing results========");
					printResults(listCats);
					//adding results to the list of nodes
					subCat.setLevel(level);
					subCat.setChilden(listCats);
					subCat.setParent(cats.getCat_title());
					subCat.setSubcategory(cats.getCl_sortkey());
					subCats.add(subCat);	
					retrieveCats(level+1,cats.getCl_sortkey(),listCats);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public void printResults(List<CategoryVO> catVO){
		for(CategoryVO cats : catVO){
			System.out.println("\t\tcl_from: " + cats.getCl_from());
			System.out.println("\t\tcat_title: " + cats.getCat_title());
			System.out.println("\t\tcl_sortedkey: " + cats.getCl_sortkey());
			System.out.println("\t\tcat_pages: " + cats.getCat_pages());
			System.out.println("\t\tcat_subcats: " + cats.getCat_subcats());
			System.out.println("\t\tcl_type: " + cats.getCl_type());
			System.out.println("=================================");
		}
	}
	
	public void printCategoriesToFile(){
		try(PrintWriter pw = new PrintWriter(new FileWriter("categories.txt"))){
			for(Integer key1 : treeMap.keySet()){
				for(Map<String,List<CategoryVO>> listMap : treeMap.get(key1)){
					for(String key2 : listMap.keySet()){
						pw.write(key1 + " - (" + key2 + ") - ");
						for(CategoryVO children : listMap.get(key2)){
							pw.write(children.getCl_sortkey() + " - ");
						}
						pw.write("\n");
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void createTree(String root, Integer level){
		List<Map<String,List<CategoryVO>>> listNode = new ArrayList<Map<String,List<CategoryVO>>>();
		for(SubCategory subCat : subCats){
			if(subCat.getParent().equals(root)){
				Map<String,List<CategoryVO>> node = new HashMap<String,List<CategoryVO>>();
				node.put(subCat.getSubcategory(), subCat.getChilden());
				
				if(!treeMap.containsKey(level)){
					listNode.add(node);
					treeMap.put(level, listNode);
				}else{
					listNode = treeMap.get(level);
					listNode.add(node);
					treeMap.remove(level);
					treeMap.put(level, listNode);
				}
				
				//Print new map element
				System.out.println("Level : " + level);
				System.out.println("subCat : " + subCat.getSubcategory());
				System.out.println("Children : ");
				for(CategoryVO cat : subCat.getChilden()){
					System.out.print(cat.getCl_sortkey() + " - ");
				}
				System.out.println("\n---------------------");
				//end
				if(subCat.getChilden().size() > 0)
					createTreeInner(subCat.getChilden(),level+1);
			}
		}
	}
	
	public void createTreeInner(List<CategoryVO> nodes, Integer level){
		List<Map<String,List<CategoryVO>>> listNode = new ArrayList<Map<String,List<CategoryVO>>>();
		for(CategoryVO node : nodes){
			for(SubCategory subCat : subCats){
				if(subCat.getSubcategory().equals(node.getCl_sortkey())){
					Map<String,List<CategoryVO>> newNode = new HashMap<String,List<CategoryVO>>();
					newNode.put(subCat.getSubcategory(), subCat.getChilden());
					
					//Here is checked if the map contains or not the key level
					//if it is contained then the existing list is retrieve and 
					//the map information with level as key is deleted
					//As the list contain elements it is necessary
					//to avoid insert repeated elements
					if(!treeMap.containsKey(level)){
						listNode.add(newNode);
						treeMap.put(level, listNode);
					}else{
						listNode = treeMap.get(level);
						boolean repeatedNode = false;
						for(Map<String,List<CategoryVO>> mapNode : listNode){
							if(mapNode.containsKey(subCat.getSubcategory())){
								repeatedNode = true;
							}
						}
						if(!repeatedNode){
							listNode.add(newNode);
							treeMap.remove(level);
							treeMap.put(level, listNode);
						}
					}
					
					
					//Print new map element
					System.out.println("Level : " + level);
					System.out.println("subCat : " + subCat.getSubcategory());
					System.out.println("Children : ");
					for(CategoryVO cat : subCat.getChilden()){
						System.out.print(cat.getCl_sortkey() + " - ");
					}
					System.out.println("\n---------------------");
					//end
					if(subCat.getChilden().size() > 0)
						createTreeInner(subCat.getChilden(),level+1);
				}
			}
		}
		
	}
	
	public void printTreeMap(){
		for(Integer key1 : treeMap.keySet()){
			for(Map<String,List<CategoryVO>> listMap : treeMap.get(key1)){
				for(String key2 : listMap.keySet()){
					System.out.print(key1 + " - (" + key2 + ") - ");
					for(CategoryVO children : listMap.get(key2)){
						System.out.print(children.getCl_sortkey() + " - ");
					}
					System.out.println();
				}
			}
		}
		
	}

}
