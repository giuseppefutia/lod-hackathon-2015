package org.dvcama.csvtordf.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.tdb.TDBFactory;

public class DbTester {

	public static void main(String[] args) throws FileNotFoundException {

		// loading data into test db
		
		String directory = "db/testCsvToRDF";
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.READ);
		Model model = dataset.getDefaultModel() ;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RDFWriter rdfWriter = model.getWriter(RDFLanguages.strLangRDFXML);
		rdfWriter.write(model, baos, "");

		byte[] resultByteArray = baos.toByteArray();
		String result = new String(resultByteArray);
		System.out.println(result);
		
		dataset.end();
		dataset.close();

	}

}
