package main.mybatis.tutorial;

import java.util.List;

import org.apache.ibatis.annotations.Select;

public interface PagesDAO {
	
	String GET_ALL_PAGES = "SELECT cl_to,cl_sortkey FROM SimpleWikiAll.categorylinks WHERE "
			+ "lower(convert(cl_to using utf8)) LIKE #{cl_to} AND "
					+ "lower(convert(cl_type using utf8)) LIKE 'page'";
	
	@Select(GET_ALL_PAGES)
	public List<PagesVO> getAllCategoryPages(PagesVO cat) throws Exception;

}
