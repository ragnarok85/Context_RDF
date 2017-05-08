package main.mybatis.tutorial;

import java.util.List;

import org.apache.ibatis.annotations.Select;

public interface CategoryDAO {
	
		String GET_ALL_SUBCATEGORIES = "SELECT cl_from, cat_title, cl_to, cl_sortkey, cl_type, cat_pages, cat_subcats  FROM SimpleWikiAll.category, SimpleWikiAll.categorylinks WHERE "
				+ "lower(convert(cl_to using utf8)) LIKE #{cat_title} AND "
						+ "lower(convert(cl_to using utf8)) LIKE lower(convert(cat_title using utf8)) AND "
						+ "cl_type like #{cl_type}";
		
		@Select(GET_ALL_SUBCATEGORIES)
		public List<CategoryVO> getInformation(CategoryVO cat) throws Exception;
}
