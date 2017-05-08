package gob.cinvestav.mx.pte.clausie;

import java.util.List;

public interface TripleElement {
	public String getText();

	public void setText(String text);

	public int getStart();

	public void setStart(int start);

	public int getEnd();

	public void setEnd(int end);
	
	public List<Integer> getListPosition();

	public void setListPosition(List<Integer> listPosition);
	
}
