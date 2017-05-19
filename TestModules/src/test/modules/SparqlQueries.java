package test.modules;

import java.util.Set;
import java.util.TreeSet;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class SparqlQueries {

	public static Set<String> queryClasses(String nameEntity) {
		Set<String> subClassOfSet = new TreeSet<String>();

		String service = "http://dbpedia.org/sparql";
		String queryString = "SELECT ?class ?subClassOf WHERE {" + " <http://dbpedia.org/resource/" + nameEntity
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class. "
				+ " ?class <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?subClassOf. "
				+ " FILTER regex(?subClassOf,\"dbpedia.org/ontology\")" + "}";
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query)) {
			ResultSet results = qexec.execSelect();

			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				subClassOfSet.add(soln.get("?subClassOf").toString());
				subClassOfSet.add(soln.get("?class").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subClassOfSet;
	}

	public static Set<String> joinTwoSets(Set<String> setOne, Set<String> setTwo) {
		Set<String> joinResults = new TreeSet<String>();

		for (String one : setOne) {
			boolean exists = false;
			for (String two : setTwo) {
				if (one.equals(two))
					exists = true;
			}
			if (exists)
				joinResults.add(one);
		}

		return joinResults;
	}

	public static boolean queryIsSubClassOf(String entityOne, String entityTwo) {
		boolean isSubClassOf = false;
		String service = "http://dbpedia.org/sparql";
		String queryString = "ASK { <" + entityOne + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <" + entityTwo
				+ ">}";
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query)) {
			isSubClassOf = qexec.execAsk();
		}
		return isSubClassOf;
	}

	public static void main(String[] args) {
		Set<String> subClassOfOne = queryClasses("Person");
		Set<String> subClassOfTwo = queryClasses("Computer_program");

		Set<String> joinResults = joinTwoSets(subClassOfOne, subClassOfTwo);

		for (String subClassOf : joinResults) {
			System.out.println(subClassOf);
		}

		for (String subClassOf : joinResults) {
			for (String subClass : joinResults) {
				System.out.println(subClassOf.replace("http://dbpedia.org/ontology/", "") + " is subClassOf "
						+ subClass.replace("http://dbpedia.org/ontology/", "") + " = "
						+ queryIsSubClassOf(subClassOf, subClass));
			}
		}
	}

}
