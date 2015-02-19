/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

/**
 * @author geodi
 *
 */
public class Step02 {

	public static void main(String[] args) throws IOException {
		// download del dataset
		System.out.println("Step02.main() cleaning del dataset ");

		File inf = new File("hackathon-test/dataset.csv");

		CSVParser parser = CSVParser.parse(inf, Charsets.toCharset("Windows-1252"), //
				CSVFormat.DEFAULT //
						.withSkipHeaderRecord() //
						.withHeader() //
						.withDelimiter(';') //
				);
		System.out.println("\n\n\n");
		System.out.println((parser.getHeaderMap() + "").replaceAll(", ", "\n"));
		Iterable<CSVRecord> records = parser.getRecords();
		System.out.println("\n\n\n");

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		// http://schema.org/Museum

		for (CSVRecord record : records) {

			Map<String, String> singleResult = new HashMap<String, String>();

			// utile per identificare il dataset nel sistema di origine
			// (speriamo)
			String id = record.get("id");
			// System.out.println(id);
			singleResult.put("about", "http://localhost/id/musei/" + id);

			// eliminando elementi di disturbo dal titolo
			String nome = record.get("nome");
			nome = nome.trim().replaceAll("\\(.+\\)$", "");
			nome = cleanWin(nome);
			// System.out.println(nome);
			singleResult.put("rdfs:label", nome);

			// inutili nella trattazione di un solo dataset
			// String id_tipologia = record.get("id_tipologia");
			// String nome_tipologia = record.get("nome_tipologia");
			// String descrizione_tipologia =
			// record.get("descrizione_tipologia");
			// System.out.println(id_tipologia+" - "+nome_tipologia+" - "+descrizione_tipologia);

			// categoria
			String id_categoria = record.get("id_categoria");
			String nome_categoria = record.get("nome_categoria");
			// String descrizione_categoria =
			// record.get("descrizione_categoria");
			// System.out.println(id_categoria+" - "+nome_categoria+" - "+descrizione_categoria);
			singleResult.put("categoria/id", id_categoria);
			singleResult.put("categoria/id/rdfs:label", nome_categoria);

			// ignoro abstract perché parte di descrizione
			String descr = cleanWin(record.get("descrizione"));
			// contiene cose tipo {phocagallery
			// view=category|categoryid=7|detail=6|jakorientation=vertical}
			descr = descr.replaceAll("\\{[^\\}]+\\}", "");
			// System.out.println(descr);
			singleResult.put("dc:description", descr);

			// importanza la ignoriamo

			// definiamo una location
			singleResult.put("address/id", "http://localhost/id/musei/" + id + "/address");

			// info_utili
			String info_utili = cleanWin(record.get("info_utili"));
			String tel = info_utili.replaceAll(".*[Tt]el[.: ]*(\\+39[0-9.]+).*", "$1");
			// System.out.println(info_utili);
			if (!tel.equals(info_utili)) {
				tel = tel.replaceAll("\\.", "").replaceAll("\\+39", "+39-");
				// System.out.println("--- tel: " + tel);
				singleResult.put("address/id:telephone", tel);
			}

			String fax = info_utili.replaceAll(".*[Ff]ax[.: ]*(\\+39[0-9.]+).*", "$1");
			if (!fax.equals(info_utili)) {
				fax = fax.replaceAll("\\.", "").replaceAll("\\+39", "+39-");
				// System.out.println("--- fax: " + fax);
				singleResult.put("address/id:faxNumber", fax);
			}
			String email = info_utili.replaceAll(".*[Ee]-mail[.: ]*([^@]+@[^@\\s]+\\.[^\\s]+).*", "$1");
			if (!email.equals(info_utili)) {
				// System.out.println("--- email: " + email);
				singleResult.put("address/id:email", email);
			}

			// info_utili_sire
			String info_utili_sire = cleanWin(record.get("info_utili_sire"));
			info_utili_sire = info_utili_sire.replaceAll("\\. ", ".\n").replaceAll("\n\\s+", "\n");
			singleResult.put("schema:openingHours", info_utili_sire);
			// System.out.println(info_utili_sire);

			// imagefiles1
			String imagefiles1 = record.get("imagefiles1");
			imagefiles1 = imagefiles1.replaceAll("^tools/", "http://www.piemonteitalia.eu/images/tools/");
			// System.out.println(imagefiles1);
			singleResult.put("schema:image", imagefiles1);

			// didascalia1, la ignoriamo, contiene nella migliore delle ipotesi
			// il nome del museo

			// modified, formattiamo la data di modifica secondo
			// http://www.w3.org/TR/NOTE-datetime
			String modified = record.get("modified");
			modified = modified.replaceAll("(.+) (.+)$", "$1T$2Z");
			singleResult.put("myschema:update", modified);
			// System.out.println(modified);

			// indirizzo
			String indirizzo = cleanWin(record.get("indirizzo"));
			// System.out.println(indirizzo);

			// numero_civico
			String numero_civico = cleanWin(record.get("numero_civico"));
			// System.out.println(numero_civico);

			singleResult.put("address/id:streetAddress", indirizzo + (numero_civico.equals("") ? "" : ", " + numero_civico));

			// CAP
			String CAP = cleanWin(record.get("CAP"));
			// System.out.println(CAP);
			singleResult.put("address/id:postalCode", CAP);

			// latitudine longitudine
			String latitudine = record.get("latitudine").replaceAll("0.00000000", "");
			String longitudine = record.get("longitudine").replaceAll("0.00000000", "");
			// System.out.println(latitudine + " - " + longitudine);
			singleResult.put("geo:long", longitudine);
			singleResult.put("geo:lat", latitudine);

			// id_comune ignoriamo il codice del sistema locale preferendo il
			// codice istat

			// istat_comune
			String istat_comune = (record.get("istat_comune"));
			// System.out.println(istat_comune);
			singleResult.put("place/comune_id", "http://localhost/id/luogo/comune/" + istat_comune);
			singleResult.put("place/myschema:codiceIstat", istat_comune);

			// nome_comune
			String nome_comune = cleanWin(record.get("nome_comune"));
			// System.out.println(nome_comune);
			singleResult.put("place/comune_id:label", nome_comune);

			// id_provincia ignoriamo il codice del sistema locale preferendo la
			// sigla

			// nome_provincia
			String nome_provincia = (record.get("nome_provincia"));
			// System.out.println(nome_provincia);
			singleResult.put("place/provincia_id:label", nome_provincia);

			// sigla_provincia=27}
			String sigla_provincia = cleanWin(record.get("sigla_provincia"));
			singleResult.put("place/provincia_id", "http://localhost/id/luogo/provincia/" + sigla_provincia.toLowerCase());
			// System.out.println(sigla_provincia);

			result.add(singleResult);
		}
		Gson gson = new Gson();
		String json = gson.toJson(result).replaceAll("\\},\\{", "},\n{");

		// perdonatemi per questo: eliminando i campi vuoti
		json = json.replaceAll(",\"[^\"]+\":\"\"", "");
		// e faccio il trim
		json = json.replaceAll("\\s+\",\"", "\",\"");
		System.out.println(json);
		FileUtils.writeStringToFile(new File("hackathon-test/dataset.json"), json);
	}

	private static String cleanWin(String s) {
		s = s.replaceAll("´", "'").replaceAll("’", "'").replaceAll("[“”]", "\"").replaceAll("–", "-").replaceAll("\t{2,}", "\t");
		return s;
	}
}
