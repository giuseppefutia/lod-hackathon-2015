/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * @author geodi and giuseppe futia
 *
 */
public class Step05 {

	public static void main(String[] args) throws IOException {

		System.out.println("Step05.main() interlinking ");

		String directory = "hackathon-test/musei";
		File dest = new File("hackathon-test/interlinking.nt");
		if (dest.exists()) {
			// dest.delete();
		}

		Dataset dataset = TDBFactory.createDataset(directory);

		dataset.begin(ReadWrite.READ);

		Map<String, String> musei = getLabels("select distinct ?s ?label {?s a <http://schema.org/Museum>; <http://www.w3.org/2000/01/rdf-schema#label> ?label}", dataset);
		System.out.println(musei);
		allinea(musei, dest, "http://dbpedia.org/ontology/Museum", "http://it.dbpedia.org/sparql", dataset, false);
		allinea(musei, dest, "http://dbpedia.org/ontology/Museum", "http://dbpedia.org/sparql", dataset, false);

		Map<String, String> comuni = getLabels("select distinct ?s ?label {?s a <http://schema.org/AdministrativeArea>; <http://www.w3.org/2000/01/rdf-schema#label> ?label}", dataset);
		allinea(comuni, dest, "http://spcdata.digitpa.gov.it/Comune", "http://spcdata.digitpa.gov.it:8899/sparql", dataset, true);
		allinea(comuni, dest, "http://dbpedia.org/ontology/Place", "http://dbpedia.org/sparql", dataset, true);
		allinea(comuni, dest, "http://dbpedia.org/ontology/Place", "http://it.dbpedia.org/sparql", dataset, true);

		addNewTriplesWithTMF("dc:description");
		
		dataset.end();
		dataset.close();
	}

	private static void allinea(Map<String, String> lista, File destFile, String className, String ep, Dataset dataset, boolean exactMatch) throws IOException {
		System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
		System.out.println(className + " --> " + ep);
		System.out.println("-------------------------------------");
		int tot = lista.size();
		int a = 0;
		for (String about : lista.keySet()) {
			try {

				String test = exactMatch ? null : getTest(about, dataset);

				// imposto un limit per non farli tutti
				a++;
				if (a < 330) {
				//	continue;
				}
				if (a % 10 == 0) {
					System.out.println(a + "/" + tot);
				}
				String query = "SELECT DISTINCT ?s ?sameas ?foto ?opera ?operaIt {" //
						+ "?s a <" + className + ">; <http://www.w3.org/2000/01/rdf-schema#label> ?label " //
						+ generateFilter(lista.get(about), test) //
						+ " OPTIONAL{?s <http://xmlns.com/foaf/0.1/depiction> ?foto}" //
						+ " OPTIONAL{?opera <http://dbpedia.org/property/museum> ?s; a <http://dbpedia.org/ontology/Artwork>}" //
						+ " OPTIONAL{?operaIt <http://it.dbpedia.org/property/ubicazione> ?s; a <http://dbpedia.org/ontology/Artwork>}" //
						+ " OPTIONAL{?s <http://www.w3.org/2002/07/owl#sameAs> ?sameas}}";

				System.out.println(query);

				QueryExecution qe = QueryExecutionFactory.sparqlService(ep, query);

				ResultSet rs = qe.execSelect();
				List<String> inserite = new ArrayList<String>();
				while (rs.hasNext()) {
					QuerySolution qs = rs.next();
					String tripla = "";
					if (qs.get("s") != null) {
						tripla = "<" + about + ">\t<http://www.w3.org/2002/07/owl#sameAs>\t<" + qs.get("s").asNode() + "> .\n ";
						if (!inserite.contains(tripla)) {
							FileUtils.writeStringToFile(destFile, tripla, true);
							System.out.println("\t" + qs.get("s").asNode().toString());
							inserite.add(tripla);
						}
						if (qs.get("opera") != null) {
							// salto i sameas di lingua di dbpedia
							tripla = "<" + about + ">\t<http://localhost/prop/haInEsposizione>\t<" + qs.get("opera").asNode() + "> .\n ";
							if (!inserite.contains(tripla)) {
								FileUtils.writeStringToFile(destFile, tripla, true);
								inserite.add(tripla);
							}
						}
						if (qs.get("operaIt") != null) {
							// salto i sameas di lingua di dbpedia
							tripla = "<" + about + ">\t<http://localhost/prop/haInEsposizione>\t<" + qs.get("operaIt").asNode() + "> .\n ";
							if (!inserite.contains(tripla)) {
								FileUtils.writeStringToFile(destFile, tripla, true);
								inserite.add(tripla);
							}
						}
						if (qs.get("sameas") != null) {

							// salto i sameas di lingua di dbpedia
							if (qs.get("sameas").asNode().toString().contains(".dbpedia.org")) {
								continue;
							}
							tripla = "<" + about + ">\t<http://www.w3.org/2002/07/owl#sameAs>\t<" + qs.get("sameas").asNode() + "> .\n ";
							if (!inserite.contains(tripla)) {
								FileUtils.writeStringToFile(destFile, tripla, true);
								inserite.add(tripla);
							}
						}
						if (qs.get("foto") != null) {
							tripla = "<" + about + ">\t<http://xmlns.com/foaf/0.1/depiction>\t<" + qs.get("foto").asNode() + "> .\n ";
							if (!inserite.contains(tripla)) {
								FileUtils.writeStringToFile(destFile, tripla, true);
								inserite.add(tripla);
							}
						}
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private static String getTest(String about, Dataset dataset) {
		// recuperiamo il nome della provincia
		String query = "SELECT ?label {<" + about + "> <http://schema.org/location> ?loc. ?loc <http://www.w3.org/2000/01/rdf-schema#label> ?label} LIMIT 1";
		QueryExecution qe = QueryExecutionFactory.create(query, dataset.getDefaultModel());
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			return qs.get("label").asLiteral().getLexicalForm();
		}
		return null;

	}

	private static String generateFilter(String label, String test) {
		// generazione dei filtri per il matching
		String filter = "";
		if (test != null) {
			String filters[] = label.split(" ");
			filter += "; ?p ?literal . FILTER(isLiteral(?literal)) .";
			for (String string : filters) {
				string = string.replaceAll("\n", "").replaceAll("\"", "\\\\\"");
				if (!filter.equals("del") && !filter.equals("di") && !filter.toLowerCase().equals("Museo") && !filter.equals("a") && !filter.equals("da") && !filter.equals("in") && !filter.equals("con") && !filter.equals("su") && !filter.equals("per") && !filter.equals("e"))
					filter += " FILTER(REGEX(?label,\"(^|[^a-z])" + string + "($|[^a-z])\",'i')) .";
			}
			// esiste ammeno un literal che contiene il luogo?
			filter += " FILTER(REGEX(?literal,\"(^|[^a-z])" + test + "($|[^a-z])\",'i')) .";
		} else {

			filter = ". FILTER(REGEX(?label,\"^" + label + "$\",'i')) .";
		}

		// System.out.println(filter);
		return filter;
	}

	private static Map<String, String> getLabels(String query, Dataset dataset) {
		// elenco di tutte le etichette
		Map<String, String> result = new TreeMap<String, String>();

		QueryExecution qe = QueryExecutionFactory.create(query, dataset.getDefaultModel());
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String subject = qs.get("s").asNode().toString();
			String label = qs.get("label").asLiteral().getLexicalForm();
			result.put(subject, label);
		}

		return result;
	}
	
	private static void addNewTriplesWithTMF(String field){
		File destFile = new File("hackathon-test/interlinking-tmf.nt");
		String tmfSubProperty = "<http://localhost/id/tmfTopic>\t"
				+ "<http://www.w3.org/2000/01/rdf-schema#subPropertyOf>\t"
				+ "<http://purl.org/dc/terms/relation> .\n ";
		
		String tmfLabel = "<http://localhost/id/tmfTopic>\t"
				+ "<http://www.w3.org/2000/01/rdf-schema#label>\t"
				+ " 'Topic extracted with TellMeFirst' .\n";
		
		String json = "";
		
		try {
			FileUtils.writeStringToFile(destFile, tmfSubProperty, true);
			FileUtils.writeStringToFile(destFile, tmfLabel, true);
			json = FileUtils.readFileToString(new File("hackathon-test/dataset.json"));
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(json, JsonNode.class);
			
			for (JsonNode record : rootNode) {
				String about = prendiValore("about", record);
				String text = prendiValore("rdfs:label", record) + " " + prendiValore(field, record);
				String classifyResponse = classifyWithTMF(text);
				System.out.println(text);
				System.out.println(classifyResponse);
				if(classifyResponse != "") {
					ObjectMapper classifyMapper = new ObjectMapper();
					JsonNode classifyRootNode = classifyMapper.readValue(classifyResponse, JsonNode.class);
					JsonNode dbpediaResources = classifyRootNode.get("Resources");
					int index = 0;
					while(dbpediaResources.get(index) != null) {					
						String tripla = "<" + about + ">\t<http://localhost/id/tmfTopic>\t<" + dbpediaResources.get(index).get("@uri") + "> .\n ";
						FileUtils.writeStringToFile(destFile, tripla.replace("\"", ""), true);
						index++;
					}
				}
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ClassifyResult{
		String service;
		ArrayList<String> Resources[];
	}
	
	private static String classifyWithTMF(String textToClassify){
		String charset = "UTF-8";
        String requestURL = "http://tellmefirst.polito.it:2222/rest/classify";
        String jsonResponse = "";
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);    
            multipart.addFormField("text", textToClassify);
            multipart.addFormField("numTopics", "7");
            multipart.addFormField("lang", "italian");
            List<String> response = multipart.finish();
            for (String line : response) {
            	jsonResponse += line;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return jsonResponse;
	}
	
	private static String prendiValore(String string, JsonNode record) {
		return record.get(string) != null ? record.get(string).asText() : "";
	}
}
