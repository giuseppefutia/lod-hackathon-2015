package org.dvcama.csvtordf.test;

import java.io.FileNotFoundException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public class DbMaker {

	public static void main(String[] args) throws FileNotFoundException {

		// loading data into test db

		String directory = "db/testCsvToRdf";
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.WRITE);

		FileManager.get().readModel(dataset.getDefaultModel(), "db-toLoad/triples.nt");

		dataset.commit();
		dataset.end();
		dataset.close();
		System.out.println("created!");
	}

}
