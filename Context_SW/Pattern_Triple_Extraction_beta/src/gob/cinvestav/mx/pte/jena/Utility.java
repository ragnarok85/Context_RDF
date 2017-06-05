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

	Property inDocprop = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "inDoc");
	Property inSntprop = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "inSentence");
	Property composedOf = jenaModel.createProperty(LocalProperties.LOCALPROPERTY.url() + "composedOf");
	Property hasTopic = jenaModel.createProperty(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic");

	List<Quad> ctxQuads = new ArrayList<Quad>();
	List<Quad> docQuads = new ArrayList<Quad>();
	
	Node topicGraph;
	Node docGraph;
	
	public Utility() {
		jenaModel.add(inDocprop, RDF.type, RDF.Property);
		jenaModel.add(inSntprop, RDF.type, RDF.Property);
		jenaModel.add(composedOf, RDF.type, RDF.Property);
		jenaModel.add(hasTopic, RDF.type, RDF.Property);
		jenaModel.setNsPrefix("cnvsr", LocalProperties.LOCALRESOURCE.url());
		jenaModel.setNsPrefix("cnvsp", LocalProperties.LOCALPROPERTY.url());
		jenaModel.setNsPrefix("cnvsdcs", LocalProperties.GRAPHDOCURI.url());
		jenaModel.setNsPrefix("cnvsctx", LocalProperties.GRAPHCTXURI.url());
		jenaModel.setNsPrefix("wibi", LocalProperties.WIBIURI.url());
		jenaModel.setNsPrefix("onto", LocalProperties.ONTOPDESIGNPATTERNS.url());
		jenaModel.setNsPrefix("ontotext", LocalProperties.ONTOTEXT.url());
		jenaModel.setNsPrefix("rdf", RDF.uri);
		jenaModel.setNsPrefix("rdfs", RDFS.uri);
		jenaModel.setNsPrefix("owl", OWL.getURI());
		jenaModel.setNsPrefix("dbr", "http://dbpedia.org/resource/");
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
//TODO inserted by lti [May 26, 2017,6:00:03 PM] the composedOf property must relate local resources
		//and the local resources (one noun) must be related (if exist) with Dbpedia through 
		//OWL.sameAs property
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
			logger.info("sameAs (subject) = " + sbj);
			jenaModel.add(subject, OWL.sameAs, jenaModel.createResource(triple.getTriple().getSubjectUris().get(0)));
		}
		if (triple.getTriple().getArgumentUris().size() == 1) {
			if (object == null)
				object = jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + obj);
			logger.info("sameAs (argument) = " + obj);
			jenaModel.add(object, OWL.sameAs, jenaModel.createResource(triple.getTriple().getArgumentUris().get(0)));
		}

		if (subject != null && object != null) {

			jenaModel.add(property, RDF.type, RDF.Property);
			jenaModel.add(subject, hasTopic,
					jenaModel.createResource(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize)));
			jenaModel.add(object, hasTopic,
					jenaModel.createResource(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize)));
			jenaModel.add(subject, inDocprop,
					(RDFNode) jenaModel.createResource(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName));
			jenaModel.add(subject, inSntprop, jenaModel.createLiteral(triple.getOrgSentence()));
			jenaModel.add(object, inDocprop,
					(RDFNode) jenaModel.createResource(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName));
			jenaModel.add(object, inSntprop, jenaModel.createLiteral(triple.getOrgSentence()));
			logger.info("Property: " + prdt);

			populateTypes(triple.getSubject().getTextNE(), triple.getSubject().getEntity());
			populateTypes(triple.getArgument().getTextNE(), triple.getArgument().getEntity());

			jenaModel.add(subject, property, object);
//			createQuad(topic, sbj, obj, prdt);
		}

	}

	public void populateQuadModel(ClausieTriple triple, String rdfModelFileName, List<String> topic) {
		String sbj = triple.getSubject().getTextNE();
		String obj = triple.getArgument().getTextNE();
		String prdt = triple.getTriple().getRelationUri();
		int topicSize = topic.size() - 1;
		Node property = NodeFactory.createURI(prdt);
		Node subject = null;
		Node object = null;
		List<Quad> localQuad = new ArrayList<Quad>();
		
		docGraph = NodeFactory.createURI(LocalProperties.GRAPHDOCURI.url()+rdfModelFileName);
		
		if (triple.getTriple().getSubjectUris().size() > 1) {
			subject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + sbj);
			for (String sbjUri : triple.getTriple().getSubjectUris())
				localQuad.add(new Quad(docGraph,subject,NodeFactory.createURI(composedOf.getURI()),NodeFactory.createURI(sbjUri)));
		}

		if (triple.getTriple().getArgumentUris().size() > 1) {
			object = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + obj);
			for (String objUri : triple.getTriple().getArgumentUris())
				localQuad.add(new Quad(docGraph,object,NodeFactory.createURI(composedOf.getURI()),NodeFactory.createURI(objUri)));
		}

		if (triple.getTriple().getSubjectUris().size() == 1) {
			if (subject == null)
				subject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + sbj);
			localQuad.add(new Quad(docGraph,subject,NodeFactory.createURI(OWL.sameAs.getURI()),NodeFactory.createURI(triple.getTriple().getSubjectUris().get(0))));
		}
		if (triple.getTriple().getArgumentUris().size() == 1) {
			if (object == null)
				object = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + obj);
			localQuad.add(new Quad(docGraph,object,NodeFactory.createURI(OWL.sameAs.getURI()),NodeFactory.createURI(triple.getTriple().getArgumentUris().get(0))));
		}

		if (subject != null && object != null) {
			localQuad.add(new Quad(docGraph,property, NodeFactory.createURI(RDF.type.getURI()), NodeFactory.createURI(RDF.Property.getURI())));
			localQuad.add(new Quad(docGraph, subject, NodeFactory.createURI(hasTopic.getURI()),
					NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize))));
			localQuad.add(new Quad(docGraph,object, NodeFactory.createURI(hasTopic.getURI()),
					NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize))));
			localQuad.add(new Quad(docGraph,subject, NodeFactory.createURI(inDocprop.getURI()),
					NodeFactory.createURI(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName)));
			localQuad.add(new Quad(docGraph,subject, NodeFactory.createURI(inSntprop.getURI()), NodeFactory.createLiteral(triple.getOrgSentence())));
			localQuad.add(new Quad(docGraph,object, NodeFactory.createURI(inDocprop.getURI()),
					NodeFactory.createURI(LocalProperties.GRAPHDOCURI.url() + rdfModelFileName)));
			localQuad.add(new Quad(docGraph,object, NodeFactory.createURI(inSntprop.getURI()), NodeFactory.createLiteral(triple.getOrgSentence())));
			logger.info("(docQuad) Property: " + prdt);
			
			localQuad.addAll(populateQuadTypes(triple.getSubject().getTextNE(),triple.getSubject().getEntity(), docGraph));
			localQuad.addAll(populateQuadTypes(triple.getArgument().getTextNE(),triple.getArgument().getEntity(), docGraph));

			createTopicQuad(topic, localQuad);
		}
		docQuads.addAll(localQuad);

	}
	public void createTopicQuad(List<String> topic, List<Quad>localQuad){
		for(Quad q : localQuad){
			ctxQuads.add(new Quad(topicGraph, q.getSubject(), q.getPredicate(),q.getObject()));
		}
	}

	public void createQuad(List<String> topic, String subject, String object, String predicate) {
		Node nSubject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + subject);
		Node nObject = NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + object);
		Node nProperty = NodeFactory.createURI(predicate);
		Node thing = NodeFactory.createURI(OWL.Thing.getURI());
		Node type = NodeFactory.createURI(RDF.type.getURI());
		Quad quad = new Quad(topicGraph, nSubject, nProperty, nObject);
		ctxQuads.add(quad);
		quad = new Quad(topicGraph, nSubject, type, thing);
		ctxQuads.add(quad);
		quad = new Quad(topicGraph, nObject, type, thing);
		ctxQuads.add(quad);
	}

	public void InitializeQuadTopics(List<String> topic) {
		int topicSize = topic.size() - 1;
		topicGraph = NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize));
		Node hasSubTopic = NodeFactory.createURI(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasSubTopic");
		Node hasTopic = NodeFactory.createURI(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic");
		// BlankNodeId ctx = BlankNodeId.create("ctx");

		Quad quad = null;
		// quad = new Quad(graph, NodeFactory.createBlankNode(ctx),hasTopic,
		// NodeFactory.createURI(ownNameSpace + topic.get(topicSize)));
		quad = new Quad(topicGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url()+topic.get(topicSize)), hasTopic,
				NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(topicSize)));
		ctxQuads.add(quad);
		logger.info("topicSize = " + topicSize);
		for (int i = topicSize - 1; i >= 0; i--) {
			logger.info("adding subtopic: " + topic.get(i));
			// quad = new Quad(graph, NodeFactory.createBlankNode(ctx),
			// hasSubTopic,
			// NodeFactory.createURI(ownNameSpace + topic.get(i)));
			quad = new Quad(topicGraph, topicGraph, hasSubTopic,
					NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(i)));
			ctxQuads.add(quad);
		}

	}

	public void createQuadClasses(List<String> topic) {
		int topicSize = topic.size() - 1;
		// Node subClassOf = NodeFactory.createURI(RDFS.subClassOf.getURI());
		Node subTopicOf = NodeFactory.createURI(LocalProperties.ONTOTEXT.url() + "subTopicOf");
		// Node clss = NodeFactory.createURI(RDFS.Class.getURI());
		Node type = NodeFactory.createURI(RDF.type.getURI());
		Node ontoTopic = NodeFactory.createURI(LocalProperties.ONTOTEXT.url() + "Topic");
//		Node graph = NodeFactory.createURI(LocalProperties.GRAPHCTXURI.url() + topic.get(topicSize));
		Quad quad = null;
		quad = new Quad(topicGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(topicSize)), type,
				ontoTopic);
		ctxQuads.add(quad);
		for (int i = topicSize, j = topicSize - 1; j >= 0; j--, i--) {
			quad = new Quad(topicGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(i)),
					subTopicOf, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(j)));
			ctxQuads.add(quad);
			quad = new Quad(topicGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + topic.get(j)), type,
					ontoTopic);
			ctxQuads.add(quad);
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

//	public void populateTypes(String textNE, List<Entity> entities) {
//		for (Entity entity : entities) {
//			for (String wibiUri : entity.getWikiUris()) {
//				jenaModel.add(jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + textNE), RDF.type,
//						jenaModel.createResource(wibiUri));
//			}
//		}
//	}
	
	public void populateTypes(String textNE, Map<String,Entity> entities) {
		for (String entity : entities.keySet()) {
			for (String wibiUri : entities.get(entity).getWikiUris()) {
				jenaModel.add(jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + textNE), RDF.type,
						jenaModel.createResource(wibiUri));
			}
		}
	}

//	public List<Quad> populateQuadTypes(String textNE, List<Entity> entities, Node docGraph) {
//		List<Quad> typeQuads = new ArrayList<Quad>();
//		for (Entity entity : entities) {
//			for (String wibiUri : entity.getWikiUris()) {
//				typeQuads.add(new Quad(docGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + textNE),
//						NodeFactory.createURI(RDF.type.getURI()), NodeFactory.createURI(wibiUri)));
//			}
//		}
//		return typeQuads;
//	}
	public List<Quad> populateQuadTypes(String textNE, Map<String,Entity> entities, Node docGraph) {
		List<Quad> typeQuads = new ArrayList<Quad>();
		for (String entity : entities.keySet()) {
			for (String wibiUri : entities.get(entity).getWikiUris()) {
				typeQuads.add(new Quad(docGraph, NodeFactory.createURI(LocalProperties.LOCALRESOURCE.url() + textNE),
						NodeFactory.createURI(RDF.type.getURI()), NodeFactory.createURI(wibiUri)));
			}
		}
		return typeQuads;
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
		jenaModel.add(jenaModel.createResource(LocalProperties.LOCALRESOURCE.url() + "OrgSentence"), RDFS.comment,
				jenaModel.createLiteral(comment));
	}

	public void writeRDFTriples(String path) {
		try {
			FileWriter fos = new FileWriter(path);
			// jenaModel.write(fos, "RDF/XML");
			jenaModel.write(fos, "TURTLE");
			fos.close();
			// jenaModel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeTopicQuad(String path) {
		try (OutputStream fos = new FileOutputStream(path.replace(".txt", ".nq"))) {
			RDFDataMgr.writeQuads(fos, ctxQuads.iterator());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeDocQuad(String path) {
		try (OutputStream fos = new FileOutputStream(path.replace(".txt", ".nq"))) {
			RDFDataMgr.writeQuads(fos, docQuads.iterator());
			fos.close();
		} catch (IOException e) {
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
	public void extractSeedsTwo(List<ClausieTriple> triples, Set<String> seeds){
		for(ClausieTriple triple : triples){
			for(String entity : triple.getSubject().getEntity().keySet()){
				seeds.add(entity.replace("_", " "));
			}
			for(String entity : triple.getArgument().getEntity().keySet()){
				seeds.add(entity.replace("_", " "));
			}
//			seeds.add(triple.getSubject().getTextNE().replace("_", ""));
//			seeds.add(triple.getArgument().getTextNE().replace("_", ""));
		}
	}
	public void extractSeeds(Set<String> seeds) {
		StmtIterator iter = jenaModel.listStatements(new SimpleSelector(null, null, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (!s.getPredicate().toString().contains(RDFS.comment.getURI())
						|| !s.getPredicate().toString().contains(OWL.sameAs.getURI())
						|| !s.getPredicate().toString().contains(inDocprop.getURI())
						|| !s.getPredicate().toString().contains(inSntprop.getURI())
						|| !s.getPredicate().toString()
								.contains(LocalProperties.ONTOPDESIGNPATTERNS.url() + "hasTopic")
						|| !s.getPredicate().toString().contains(topicGraph.getURI())
						|| !s.getPredicate().toString().contains(docGraph.getURI())
						|| !s.getPredicate().toString().contains(LocalProperties.ONTOTEXT.url() + ""));
			}
		});
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			String[] subject = stmt.getSubject().toString().split("/");
			String[] object = stmt.getObject().toString().split("/");

			String sbj = subject[subject.length - 1].toLowerCase().replace("#", "");
			String obj = object[object.length - 1].toLowerCase().replace("#", "");

			seeds.add(sbj.replace("_", " "));
			seeds.add(obj.replace("_", " "));

			logger.info("seeds : [" + sbj.replace("_", " ") + "," + obj.replace("_", " ") + "]" + " was added....\n\n");
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
