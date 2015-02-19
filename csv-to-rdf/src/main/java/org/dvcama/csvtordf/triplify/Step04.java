/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.File;
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
public class Step04 {

	public static void main(String[] args) throws IOException {

		System.out.println("Step04.main() caricando nel modello ");

		String directory = "hackathon-test/musei";

		// cancelliamo il grafo
		if (new File(directory).exists()) {
			for (File file : new File(directory).listFiles()) {
				file.delete();
			}
		} else {
			new File(directory).mkdirs();
		}

		// e carichiamolo
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.WRITE);

		FileManager.get().readModel(dataset.getDefaultModel(), "hackathon-test/dataset.xml");

		dataset.commit();
		dataset.end();
		dataset.close();
		System.out.println("created!");
		

		System.out.println("testing...");
		dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create("select distinct ?s {?s ?f []}",dataset);
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
