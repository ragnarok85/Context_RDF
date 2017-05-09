package main.mybatis.tutorial;

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
	
	public List<CategoryVO> getInformation(CategoryVO cat) throws Exception{
		SqlSession session = ConnectionFactory.getSession().openSession();
		CategoryDAO dao = session.getMapper(CategoryDAO.class);
		List<CategoryVO> categories = dao.getInformation(cat);
		session.close();
		return categories;
	}
	
	public static void main(String[] args) throws Exception{
		CategoryBO bo = new CategoryBO();
		CategoryVO vo = new CategoryVO();
		
		CategoryVO cat = new CategoryVO();
		
		String mainCategory = "computer_science";
		
		cat.setCat_title(mainCategory);
		//subcat or page
		cat.setCl_type("subcat");
		List<CategoryVO> catVO = bo.getInformation(cat);
		
		SubCategory subCat = new SubCategory();
		subCat.setParent("root");
		subCat.setSubcategory(mainCategory);
		subCat.setChilden(catVO);
		subCats.add(subCat);
		
		bo.retrieveCats(0,mainCategory,catVO);
		bo.printListCategory();
		bo.printCategoriesToFile();
		
		List<String> parents = new ArrayList<String>();
		parents.add("root");
		bo.printHiearchy(parents);
	}
	
	public void retrieveCats(int level,String category,List<CategoryVO> categories){
		CategoryBO bo = new CategoryBO();
		
		for(CategoryVO cats : categories){
			SubCategory subCat = new SubCategory();
			
			System.out.println("\n\n*****processing Subcategories of " + category +": " + cats.getCl_sortkey());
			
			CategoryVO cat = new CategoryVO();
			cat.setCat_title(cats.getCl_sortkey());
			cat.setCl_type("subcat");
			
			try {
				List<CategoryVO> listCats = bo.getInformation(cat);
				System.out.println("=======Printing results========");
				
				if(!listCats.isEmpty()){
					printResults(listCats);
					subCat.setLevel(level);
					subCat.setChilden(listCats);
					subCat.setParent(category);
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
			pw.write("Category\tSubcategories\n");
			
			for(SubCategory subCat : subCats){
//				pw.write("("+lookParents(subCat.getParent())+subCat.getParent()+")" + "\t");
				pw.write(subCat.getLevel() + "-" + "("+subCat.getParent()+")" + "\t");
				
				for(CategoryVO cat : subCat.getChilden()){
					pw.write(cat.getCl_sortkey() + "\t");
				}
				pw.write("\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
//	public String lookParents(String parent){
//		String parents = "";
//		String newParent = "";
//		for(SubCategory subCat : subCats){
//			for(CategoryVO cat : subCat.getChilden()){
//				if(cat.getCl_sortkey().equals(parent)){
//					newParent = cat.getCl_sortkey();
//					parents += lookParents(newParent) + "-";
//				}
//			}
//		}
//		return parents;
//	}
	
	
	public void printListCategory(){
		for(SubCategory subCat : subCats){
			System.out.println("Parent: "  + subCat.getParent());
			System.out.println("category: " + subCat.getSubcategory());
			for(CategoryVO cat : subCat.getChilden()){
				System.out.print(cat.getCl_sortkey() + " - ");
			}
			System.out.println();
			System.out.println("===================================");
		}
	}
	
	public void printHiearchy(List<String> parents){
		List<String> newParents = new ArrayList<String>();
			for(SubCategory subCat : subCats){
					for(CategoryVO cat: subCat.getChilden()){
						System.out.println(subCat.getParent() + " - " + cat.getCl_sortkey());
					}
					
					newParents.add(subCat.getSubcategory());
			}
		if(newParents.size() > 0)
			printHiearchy(newParents);
		
	}

}
