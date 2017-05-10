package main.mybatis.tutorial;

import java.io.Serializable;

public class PagesVO implements Serializable{
	
private static final long serialVersionUID = 4872640461000241018L;
	
	private String cl_to;
	private String cl_sortkey; 
	
	public String getCl_to() {
		return cl_to;
	}
	public void setCl_to(String cat_title) {
		this.cl_to = cat_title;
	}
	public String getCl_sortkey() {
		return cl_sortkey;
	}
	public void setCl_sortkey(String cl_sortkey) {
		this.cl_sortkey = cl_sortkey;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override 
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagesVO other = (PagesVO) obj;
		if (cl_sortkey == null) {
			if (other.cl_sortkey != null)
				return false;
		} else if (!cl_sortkey.equals(other.cl_to))
			return false;
		return true;
	}
}
