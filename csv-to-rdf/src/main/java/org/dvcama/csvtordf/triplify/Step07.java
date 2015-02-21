package org.dvcama.csvtordf.triplify;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * @author geodi
 *
 */
public class Step07 {
	// null per host default
	String host = "community-dati-piemonte-it.nexacenter.org";

	// LDP url
	String url = "http://localhost:8080/bygle";

	public static void main(String[] args) throws IOException {
		new Step07().importa("hackathon-test/musei", "/id/contaner103");

		// new Step07().elimina("/id/contaner102");
	}

	private void elimina(String container) throws ClientProtocolException, IOException {
		System.out.println("Step07.main() eliminare via LDP i dati di una resource ");

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpDelete httpMethod = new HttpDelete(url + container);
		if (host != null && !host.trim().isEmpty())
			httpMethod.setHeader("Host", host);
		HttpResponse response = httpclient.execute(httpMethod);

		System.out.println(response.toString());
		EntityUtils.consume(response.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		httpclient.close();

	}

	private void importa(String directory, String container) throws ClientProtocolException, IOException {

		System.out.println("Step07.main() postare via LDP i dati allineati ");

		String turtleContainer = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n";
		turtleContainer += "@prefix dcterms: <http://purl.org/dc/terms/>.\n";
		turtleContainer += "@prefix ldp: <http://www.w3.org/ns/ldp#>.\n";
		turtleContainer += "	\n";
		turtleContainer += "<> a ldp:Container, ldp:Resource, ldp:RDFSource;\n";
		turtleContainer += "  dcterms:title \"Dataset tal dei tali\" .\n";
		postContainer(container, turtleContainer.getBytes(), host, url);

		// loading temp dataset
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.READ);

		Model model = dataset.getDefaultModel();

		ResIterator it = model.listSubjects();
		while (it.hasNext()) {
			Resource r = it.next();

			// creiamo un modello temporaneo per ogni risorsa
			Model resourceModel = ModelFactory.createDefaultModel();
			StmtIterator stmi = model.listStatements(r, null, (RDFNode) null);
			while (stmi.hasNext()) {
				Statement s = stmi.next();
				resourceModel.add(s);
			}
			OutputStream out = new ByteArrayOutputStream();
			RDFDataMgr.write(out, resourceModel, RDFFormat.TURTLE);
			String data = out.toString();

			// estraggo lo slug che è da appendere al container
			String slug = r.getURI().replaceAll("http://[^/]+/", "").replaceAll("^id/", "");

			// rendo il doc RDF "relativo" e non più con un IRI assoluta
			data = data.replaceFirst("<http://[^>]+>", "<>");
			System.out.println(data);

			// testo la presenza della risorsa
			String etag = get(container + "/" + slug, host, url);

			postResource(container, slug, data.getBytes("utf-8"), host, url, etag);

			System.out.println("------------------------------");
		}

		dataset.end();
		dataset.close();
	}

	public static String get(String resource, String host, String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		System.out.println("testando la presenza di " + url + resource);
		HttpGet httpMethod = new HttpGet(url + resource);
		httpMethod.setHeader("Accept", "application/rdf+xml");
		if (host != null && !host.trim().isEmpty())
			httpMethod.setHeader("Host", host);
		HttpResponse response = httpclient.execute(httpMethod);
		// System.out.println(response.toString());
		// String resultBody = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		// System.out.println(resultBody);
		httpclient.close();

		try {
			return response.getHeaders("ETAG")[0].getValue();
		} catch (Exception e) {
			return "";
		}
	}

	public static void postContainer(String resource, byte[] content, String host, String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpMethod = new HttpPost(url + resource);
		httpMethod.setHeader("Accept", "text/turtle");
		httpMethod.setHeader("Content-Type", "text/turtle");
		if (host != null && !host.trim().isEmpty())
			httpMethod.setHeader("Host", host);
		httpMethod.setEntity(new ByteArrayEntity(content));
		HttpResponse response = httpclient.execute(httpMethod);
		System.out.println(response.toString());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		String resultBody = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		System.out.println(resultBody);
		httpclient.close();
	}

	public static void postResource(String resource, String slug, byte[] content, String host, String url, String etag) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = null;
		if (etag == null || etag.equals("")) {

			// nuovo inserimento
			System.out.println("nuovo inserimento " + url + resource);
			HttpPost httpMethod = new HttpPost(url + resource);

			httpMethod.setHeader("Accept", "text/turtle");
			if (slug != null) {
				httpMethod.setHeader("Slug", slug);
			}
			httpMethod.setHeader("Content-Type", "text/turtle");
			if (host != null && !host.trim().isEmpty())
				httpMethod.setHeader("Host", host);
			httpMethod.setEntity(new ByteArrayEntity(content));
			response = httpclient.execute(httpMethod);

			System.out.println(response.toString());
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			String resultBody = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			System.out.println(resultBody);
		} else {

			// aggiornamento
			System.out.println("aggiornamento");
			HttpPut httpMethod = new HttpPut(url + resource + "/" + slug);

			httpMethod.setHeader("Accept", "text/turtle");
			httpMethod.setHeader("If-Match", etag);
			httpMethod.setHeader("Content-Type", "text/turtle");
			if (host != null && !host.trim().isEmpty())
				httpMethod.setHeader("Host", host);
			httpMethod.setEntity(new ByteArrayEntity(content));
			response = httpclient.execute(httpMethod);

			System.out.println(response.toString());
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		}

		httpclient.close();
	}
}
