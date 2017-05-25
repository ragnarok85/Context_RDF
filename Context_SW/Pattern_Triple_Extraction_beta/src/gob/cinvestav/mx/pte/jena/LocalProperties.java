package gob.cinvestav.mx.pte.jena;

public enum LocalProperties {
	LOCALRESOURCE("http://tamps.cinvestav.com.mx/resource/#"),
	LOCALPROPERTY("http://tamps.cinvestav.com.mx/property/#"),
	GRAPHDOCURI("http://tamps.cinvestav.com.mx/graph/doc/"),
	GRAPHCTXURI("http://tamps.cinvestav.com.mx/graph/ctx/"),
	WIBIURI("http://wibitaxonomy.org/"),
	ONTOPDESIGNPATTERNS("http://www.ontologydesignpatterns.org/ont/dul/ontopic.owl#"),
	ONTOTEXT("http://www.ontotext.com/proton/protontop#");
	
	private String url;
	
	LocalProperties(String url){
		this.url = url;
	}
	public String url(){
		return url;
	}
}
