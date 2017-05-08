package main.mybatis.tutorial;

import java.io.Serializable;

public class CategoryVO implements Serializable{
	
	private static final long serialVersionUID = 4872640461000241018L;
	
	private long cl_from;
	private String cat_title;
	private String cl_sortkey; 
	private String cl_type;
	private int cat_pages;
	private int cat_subcats;
	
	
	public long getCl_from() {
		return cl_from;
	}


	public void setCl_from(long cl_from) {
		this.cl_from = cl_from;
	}


	public String getCat_title() {
		return cat_title;
	}
	
	public void setCat_title(String cat_title) {
		this.cat_title = cat_title.toLowerCase();
	}

	public String getCl_sortkey() {
		return cl_sortkey;
	}


	public void setCl_sortkey(String cl_sortkey) {
		this.cl_sortkey = cl_sortkey.toLowerCase().replace(" ", "_");
	}

	public String getCl_type() {
		return cl_type;
	}


	public void setCl_type(String cl_type) {
		this.cl_type = cl_type;
	}


	public int getCat_pages() {
		return cat_pages;
	}


	public void setCat_pages(int cat_pages) {
		this.cat_pages = cat_pages;
	}


	public int getCat_subcats() {
		return cat_subcats;
	}


	public void setCat_subcats(int cat_subcats) {
		this.cat_subcats = cat_subcats;
	}

	
	 
	@Override 
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryVO other = (CategoryVO) obj;
		if (cl_sortkey == null) {
			if (other.cl_sortkey != null)
				return false;
		} else if (!cl_sortkey.equals(other.cat_title))
			return false;
		if (cl_from != other.cl_from)
			return false;
		return true;
	}
}
