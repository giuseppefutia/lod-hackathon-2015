/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.IOException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author geodi
 *
 */
public class Step06 {

	public static void main(String[] args) throws IOException {

		System.out.println("Step06.main() aggiungere i dati allineati ");

		String directory = "hackathon-test/musei";

		// e carichiamolo
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.WRITE);

		FileManager.get().readModel(dataset.getDefaultModel(), "hackathon-test/interlinking.nt");

		dataset.commit();
		dataset.end();
		dataset.close();
		System.out.println("created!");

		System.out.println("testing...");
		dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create("select distinct ?s {?s <http://www.w3.org/2002/07/owl#sameAs> ?o}", dataset);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String subject = qs.get("s").asNode().toString();
			System.out.println(subject);
		}
		dataset.end();
		dataset.close();
	}

}
