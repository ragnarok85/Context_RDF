package main.mybatis.tutorial;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

public class PagesBO {

	public List<PagesVO> getAllCategoryPages(PagesVO pag) throws Exception{
		SqlSession session = ConnectionFactory.getSession().openSession();
		PagesDAO dao = session.getMapper(PagesDAO.class);
		List<PagesVO> categories = dao.getAllCategoryPages(pag);
		session.close();
		return categories;
	}
	
	public static void main(String[] args) {
		PagesBO bo = new PagesBO();
		PagesVO vo = new PagesVO();
		List<PagesVO> listPages = null;
		
		File rootDir = new File(args[0]);
		System.out.println(args[0]);
		
		bo.extractPagesName(rootDir);
		
//		vo.setCl_to("computer_science");
//		
//		try{
//			listPages = bo.getAllCategoryPages(vo);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		for(PagesVO page : listPages){
//			System.out.println(page.getCl_sortkey());
//		}
//		bo.printPages(listPages);
		
	}
	
	public void extractPagesName(File rootDir){
		PagesVO vo = new PagesVO();
		List<PagesVO> listPages = null;
		String[] listDirs = rootDir.list();
		
		vo.setCl_to(rootDir.getName());
		try {
			listPages = getAllCategoryPages(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		printPages(rootDir.getAbsolutePath(),listPages);
		
		for(String dir : listDirs){
			File dirPath = new File(rootDir.getAbsolutePath()+"/"+dir);
			if(dirPath.isDirectory()){
				if(dirPath.list().length > 0){
					extractPagesName(dirPath);
				}else{
					vo.setCl_to(dir);
					try {
						listPages = getAllCategoryPages(vo);
					} catch (Exception e) {
						e.printStackTrace();
					}

//					for (PagesVO page : listPages) {
//						System.out.println(page.getCl_sortkey());
//					}
					printPages(dirPath.getAbsolutePath(),listPages);
				}
			}
			
			
		}
		
	}
	
	public void printPages(String path, List<PagesVO> listPages){
		try(PrintWriter pw = new PrintWriter(new FileWriter(path +"/pages.txt"))){
			for(PagesVO page : listPages){
				if(!page.getCl_sortkey().isEmpty())
					pw.write(page.getCl_sortkey().toLowerCase() + "\n");
			}
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
