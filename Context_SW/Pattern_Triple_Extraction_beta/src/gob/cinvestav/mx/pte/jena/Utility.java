package gob.cinvestav.mx.pte.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;

import gob.cinvestav.mx.pte.clausie.ClausieTriple;
import gob.cinvestav.mx.pte.ws.Entity;

public class Utility {
	final static Logger logger = Logger.getLogger(Utility.class);

	Model jenaModel = ModelFactory.createDefaultModel();
	
	Property inDocprop;
	Property inSntprop;
	Property composedOf;
	Property hasTopic;
	
	List<Quad> quads = new ArrayList<Quad>();
	
	public Utility() {
		inDocprop = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "inDoc");
		inSntprop = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "inSentence");
		composedOf = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "composedOf");
		hasTopic = jenaModel.createProperty(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic");
		jenaModel.setNsPrefix("cnvsr", LocalProperties.LOCALRESOURCE.url());
		jenaModel.setNsPrefix("cnvsp", LocalProperties.LOCALPROPERTY.url());
		jenaModel.setNsPrefix("cnvsdcs", LocalProperties.GRAPHDOCURI.url());
		jenaModel.setNsPrefix("cnvsctx", LocalProperties.GRAPHCTXURI.url());
		jenaModel.setNsPrefix("wibi", LocalProperties.WIBIURI.url());
		jenaModel.setNsPrefix("onto", LocalProperties.ONTOPDESIGNPATTERNS.url());
		jenaModel.setNsPrefix("ontotext", LocalProperties.ONTOTEXT.url());
		jenaModel.add(inDocprop,RDF.type,RDF.Property);
		jenaModel.add(inSntprop,RDF.type,RDF.Property);
		jenaModel.add(composedOf,RDF.type,RDF.Property);
		jenaModel.add(hasTopic,RDF.type,RDF.Property);
	}

	public static void fuseSeeds(String seedFolder, Set<String> allSeeds) {
		File folder = new File(seedFolder);
		File[] seedFiles = folder.listFiles();

		for (File seedFile : seedFiles) {
			try (BufferedReader br = new BufferedReader(new FileReader(seedFile))) {
				String line = "";
				while ((line = br.readLine()) != null) {
					if (!allSeeds.contains(line)) {
						allSeeds.add(line);
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Model getJenaModel() {
		return this.jenaModel;
	}

	public void publicLovNameSpace(List<String> lovUris) {
		for (String nms : lovUris) {
			// System.out.println("lovUris = " + nms);
			// nms = nms;http://....
			String[] parts = nms.split(";");
			// System.out.println("parts[0,1] = " + parts[0] + " " + parts[1]);
			jenaModel.setNsPrefix(parts[0], parts[1]);
		}
	}

	public void populateModel(ClausieTriple triple, String rdfModelFileName, List<String> topic) {
		String sbj = triple.getSubject().getTextNE();
		String obj = triple.getArgument().getTextNE();
		String prdt = triple.getTriple().getRelationUri();
		int topicSize = topic.size() - 1;

		Property property = jenaModel.createProperty(prdt);
		
		Resource subject = null;
		Resource object = null;
		
		jenaModel.add(property,RDF.type,RDF.Property);

		if (triple.getTriple().getSubjectUris().size() > 1) {
			subject = jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + sbj);
			for (String sbjUri : triple.getTriple().getSubjectUris())
				jenaModel.add(subject, composedOf, jenaModel.createResource(sbjUri));
		}

		if (triple.getTriple().getArgumentUris().size() > 1) {
			object = jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + obj);
			for (String objUri : triple.getTriple().getArgumentUris())
				jenaModel.add(object, composedOf, jenaModel.createResource(objUri));
		}

		if (triple.getTriple().getSubjectUris().size() == 1) {
			if (subject == null)
				subject = jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + sbj);
			jenaModel.add(subject, OWL.sameAs, jenaModel.createResource(triple.getTriple().getSubjectUris().get(0)));
		}
		if (triple.getTriple().getArgumentUris().size() == 1) {
			if (object == null)
				object = jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + obj);
			jenaModel.add(object, OWL.sameAs, jenaModel.createResource(triple.getTriple().getArgumentUris().get(0)));
		}

		if (subject != null && object != null) {
			jenaModel.add(subject, hasTopic, jenaModel.createResource(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize)));
			jenaModel.add(object, hasTopic, jenaModel.createResource(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize)));
			jenaModel.add(subject, inDocprop, (RDFNode) jenaModel.createResource(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName));
			jenaModel.add(subject, inSntprop, jenaModel.createLiteral(triple.getOrgSentence()));
			jenaModel.add(object, inDocprop, (RDFNode) jenaModel.createResource(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName));
			jenaModel.add(object, inSntprop, jenaModel.createLiteral(triple.getOrgSentence()));
			logger.info("Property: " + prdt);

			jenaModel.add(subject, property, object);
			createQuad(topic, sbj, obj, prdt);
		}

	}

	public void createQuad(List<String> topic, String subject, String object, String predicate) {
		Node graph = NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url()+ topic.get(topic.size() - 1));
		Node nSubject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + subject);
		Node nObject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + object);
		Node nProperty = NodeFactory.createURI(predicate);
		Node thing = NodeFactory.createURI(OWL.Thing.getURI());
		Node type = NodeFactory.createURI(RDF.type.getURI());
		Quad quad = new Quad(graph, nSubject, nProperty, nObject);
		quads.add(quad);
		quad = new Quad(graph, nSubject, type, thing);
		quads.add(quad);
		quad = new Quad(graph, nObject, type, thing);
		quads.add(quad);
	}

	public void createQuadTopics(List<String> topic) {
		int topicSize = topic.size() - 1;
		Node graph = NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize));
		Node hasSubTopic = NodeFactory.createURI(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasSubTopic");
		Node hasTopic = NodeFactory.createURI(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic");
//		BlankNodeId ctx = BlankNodeId.create("ctx");
		
		Quad quad = null;
//		quad = new Quad(graph, NodeFactory.createBlankNode(ctx),hasTopic,
//				NodeFactory.createURI(ownNameSpace + topic.get(topicSize)));
		quad = new Quad(graph, NodeFactory.createURI(topic.get(topicSize)),hasTopic,
				NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(topicSize)));
		quads.add(quad);
		logger.info("topicSize = " + topicSize);
		for (int i = topicSize - 1; i >= 0; i--) {
			logger.info("adding subtopic: " + topic.get(i));
//			quad = new Quad(graph, NodeFactory.createBlankNode(ctx), hasSubTopic,
//					NodeFactory.createURI(ownNameSpace + topic.get(i)));
			quad = new Quad(graph, graph, hasSubTopic,
					NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(i)));
			quads.add(quad);
		}

	}

	public void createQuadClasses(List<String> topic) {
		int topicSize = topic.size() - 1;
		// Node subClassOf = NodeFactory.createURI(RDFS.subClassOf.getURI());
		Node subTopicOf = NodeFactory.createURI(LocalProperties.ONTOTEXT.url() + "subTopicOf");
		// Node clss = NodeFactory.createURI(RDFS.Class.getURI());
		Node type = NodeFactory.createURI(RDF.type.getURI());
		Node ontoTopic = NodeFactory.createURI(LocalProperties.ONTOTEXT.url() + "Topic");
		Node graph = NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize));
		Quad quad = null;

		quad = new Quad(graph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url()+topic.get(topicSize)), type, ontoTopic);
		quads.add(quad);
		for (int i = topicSize, j = topicSize - 1; j >= 0; j--, i--) {
			quad = new Quad(graph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(i)), subTopicOf,
					NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(j)));
			quads.add(quad);
			quad = new Quad(graph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(j)), type, ontoTopic);
			quads.add(quad);
		}
	}

	public void populateTopic(Entity entity, String topic) {
		for (String uri : entity.getUris()) {
			Resource subject = jenaModel.createResource(uri);
			Property mainTopic = jenaModel.createProperty(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic");
			jenaModel.add(subject, mainTopic, jenaModel.createLiteral(topic));
		}

	}

	public void populateTypes(Entity entity) {
		if (!entity.getWikiUris().isEmpty()) {
			for (String sbjUri : entity.getUris()) {
				Resource subject = jenaModel.createResource(sbjUri);
				// jenaModel.add(subject,sameAs,sbjUri);
				for (String wibiUri : entity.getWikiUris()) {
					Resource object = jenaModel.createResource(wibiUri);
					jenaModel.add(subject, RDF.type, object);
				}
			}
		}
	}

	public void printTriples(List<String> triples, String output) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
			for (String triple : triples) {
				pw.write(triple + "\n");
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addRdfsComment(String comment) {
		jenaModel.add(jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + "OrgSentence"),
				jenaModel.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"),
				jenaModel.createLiteral(comment));
	}

	public void writeRDFTriples(String path) {
		try {
			FileWriter fos = new FileWriter(path);
			jenaModel.write(fos, "RDF/XML");
			fos.close();
			// jenaModel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void writeRDFQuad(String path){
		try(OutputStream fos = new FileOutputStream(path.replace(".txt", ".nq"))){
			RDFDataMgr.writeQuads(fos, quads.iterator());
			fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void writeProblematicSentences(String problematicSentencesOuput,
			Map<String, List<String>> problematicSentences) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(problematicSentencesOuput))) {
			for (String key : problematicSentences.keySet()) {
				for (String problematicSentence : problematicSentences.get(key)) {
					String[] splitProblematicSentence = problematicSentence.split("********");
					if (splitProblematicSentence.length == 2) {
						pw.write(key + "\t" + splitProblematicSentence[0] + "\t" + splitProblematicSentence[1] + "\n");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extractSeeds(Set<String> seeds) {
		StmtIterator iter = jenaModel.listStatements(new SimpleSelector(null, null, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (!s.getPredicate().toString().contains("http://www.w3.org/2000/01/rdf-schema#comment")
						|| !s.getPredicate().toString().contains("http://www.w3.org/2002/07/owl#sameAs")
						|| !s.getPredicate().toString().contains("http://tamps.cinvestav.com.mx/rdf/#inDoc")
						|| !s.getPredicate().toString().contains("http://tamps.cinvestav.com.mx/rdf/#inSentence")
						|| !s.getPredicate().toString().contains(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic"));
			}
		});
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			String[] subject = stmt.getSubject().toString().split("/");
			String[] object = stmt.getObject().toString().split("/");

			String sbj = subject[subject.length - 1].toLowerCase();
			String obj = object[object.length - 1].toLowerCase();

			seeds.add(sbj.replace("_", " "));
			seeds.add(obj.replace("_", " "));

			logger.info("seeds : [" + sbj + "," + obj + "]" + " was added....\n\n");
			jenaModel.close();
		}
	}

	public static String queryNEDBpedia(String neText) {
		String neURI = "";
		String service = "http://dbpedia.org/sparql";
		String queryString = "SELECT ?subject ?label WHERE {"
				+ " ?subject <http://www.w3.org/2000/01/rdf-schema#label> ?label. " + " FILTER regex(?label, '^"
				+ neText + "$', \"i\")" + "}";
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query)) {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution rsn = results.next();
				neURI = rsn.get("?subject").toString();
			}
		}

		return neURI;
	}
}
