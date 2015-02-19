/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author geodi
 *
 */
public class Step03 {

	public static void main(String[] args) throws IOException {

		System.out.println("Step03.main() to RDF ");
		String json = FileUtils.readFileToString(new File("hackathon-test/dataset.json"));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(json, JsonNode.class);

		// chiavi
		for (JsonNode record : rootNode) {
			Iterator<Entry<String, JsonNode>> f = record.fields();
			while (f.hasNext()) {
				System.out.println(f.next().getKey());
			}
			break;
		}

		Model m = ModelFactory.createDefaultModel();
		Map<String, String> prefixMap = new HashMap<String, String>();

		prefixMap.put("rdfs", RDFS.getURI());
		prefixMap.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		prefixMap.put("schema", "http://schema.org/");
		prefixMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixMap.put("gn", "http://www.geonames.org/ontology#");
		prefixMap.put("rdf", RDF.getURI());
		prefixMap.put("dcterms", DCTerms.getURI());

		m.setNsPrefixes(prefixMap);

		for (JsonNode record : rootNode) {
			Resource IRI = m.createResource(prendiValore("about", record));

			// usando le costanti di jena (es. RDF)
			m.add(IRI, RDF.type, m.createResource("http://schema.org/Museum"));

			// definendo una propria proprietà
			m.add(IRI, m.createProperty("http://www.w3.org/2000/01/rdf-schema#", "label"), m.createLiteral(prendiValore("rdfs:label", record), "it"));

			// aggiungendo alla risorsa i valori nella maniera più pratica
			if (record.get("geo:lat") != null) {
				IRI.addProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat"), prendiValore("geo:lat", record));
				IRI.addProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "long"), prendiValore("geo:long", record));
			}

			IRI.addProperty(DCTerms.description, prendiValore("dc:description", record), "it");
			IRI.addProperty(m.createProperty("http://schema.org/", "openingHours"), prendiValore("schema:openingHours", record), "it");
			if (record.get("schema:image") != null) {
				IRI.addProperty(m.createProperty("http://schema.org/", "image"), m.createResource(prendiValore("schema:image", record)));
			}

			// indirizzo e contatti
			Resource IRIaddress = m.createResource(prendiValore("address/id", record));
			IRIaddress.addProperty(RDF.type, m.createResource("http://schema.org/PostalAddress"));
			IRIaddress.addProperty(RDFS.label, "indirizzo e contatti: " + prendiValore("rdfs:label", record), "it");

			IRI.addProperty(m.createProperty("http://schema.org/", "address"), IRIaddress);

			IRIaddress.addProperty(m.createProperty("http://schema.org/", "streetAddress"), prendiValore("address/id:streetAddress", record));
			if (record.get("address/id:faxNumber") != null) {
				IRIaddress.addProperty(m.createProperty("http://schema.org/", "faxNumber"), prendiValore("address/id:faxNumber", record));
			}
			if (record.get("address/address/id:telephone") != null) {
				IRIaddress.addProperty(m.createProperty("http://schema.org/", "telephone"), prendiValore("address/address/id:telephone", record));
			}
			IRIaddress.addProperty(m.createProperty("http://schema.org/", "addressCountry"), m.createResource("http://sws.geonames.org/3175395"));

			if (record.get("place/comune_id") != null) {

				// comune
				Resource IRIcomune = m.createResource(prendiValore("place/comune_id", record));
				IRIcomune.addProperty(RDF.type, m.createResource("http://schema.org/AdministrativeArea"));
				IRIcomune.addProperty(RDF.type, m.createResource("http://www.geonames.org/ontology#Feature"));

				IRI.addProperty(m.createProperty("http://schema.org/", "location"), IRIcomune);

				//

				IRIcomune.addProperty(DCTerms.identifier, m.createResource("istat:" + prendiValore("place/myschema:codiceIstat", record)));
				IRIcomune.addProperty(m.createProperty("http://www.geonames.org/ontology#", "countryCode"), m.createLiteral("IT"));
				IRIcomune.addProperty(RDFS.label, prendiValore("place/comune_id:label", record), "it");

				// provincia
				if (record.get("place/provincia_id") != null) {
					Resource IRIprovincia = m.createResource(prendiValore("place/provincia_id", record));
					IRIprovincia.addProperty(RDF.type, m.createResource("http://schema.org/AdministrativeArea"));
					IRIprovincia.addProperty(RDF.type, m.createResource("http://www.geonames.org/ontology#Feature"));

					//

					IRIprovincia.addProperty(m.createProperty("http://www.geonames.org/ontology#", "countryCode"), m.createLiteral("IT"));
					IRIprovincia.addProperty(RDFS.label, prendiValore("place/provincia_id:label", record), "it");

					IRIcomune.addProperty(m.createProperty("http://www.geonames.org/ontology#", "parentFeature"), IRIprovincia);
					IRIcomune.addProperty(m.createProperty("http://www.geonames.org/ontology#", "parentADM1"), IRIprovincia);
				}
			}
		}

		// scrivendo RDF
		OutputStream out = new FileOutputStream(new File("hackathon-test/dataset.xml"));
		OutputStream outAbbr = new FileOutputStream(new File("hackathon-test/dataset.abbr.xml"));
		OutputStream outNt = new FileOutputStream(new File("hackathon-test/dataset.nt"));
		OutputStream outTurtle = new FileOutputStream(new File("hackathon-test/dataset.turtle"));
		OutputStream outJson = new FileOutputStream(new File("hackathon-test/dataset.json"));
		
		RDFDataMgr.write(out, m, RDFFormat.RDFXML_ABBREV);
		RDFDataMgr.write(outAbbr, m, RDFFormat.RDFXML_ABBREV);
		RDFDataMgr.write(outNt, m, RDFFormat.NT);
		RDFDataMgr.write(outTurtle, m, RDFFormat.TURTLE_PRETTY);
		RDFDataMgr.write(outJson, m, RDFFormat.JSONLD);

		// mostrandolo a video
		RDFDataMgr.write(System.out, m, RDFFormat.RDFXML_ABBREV);

	}

	private static String prendiValore(String string, JsonNode record) {
		return record.get(string) != null ? record.get(string).asText() : "";
	}
}
