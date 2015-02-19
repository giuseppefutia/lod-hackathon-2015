/**
 * 
 */
package org.dvcama.csvtordf.triplify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author geodi
 *
 */
public class Step01 {

	public static void main(String[] args) throws IOException {
		// download del dataset
		System.out.println("Step01.main() download del dataset ");

		String urlDataset = "http://www.dati.piemonte.it/catalogodati/scarica.html?idallegato=458";

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(urlDataset);

			System.out.println("Executing request " + httpget.getRequestLine());

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();

						if (entity != null) {
							BufferedInputStream bis = new BufferedInputStream(entity.getContent());
							String filePath = "hackathon-test/dataset.csv";
							File file = new File(filePath.replaceAll("(.+)/[^/]+", "$1"));
							file.mkdirs();

							BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
							int inByte;
							while ((inByte = bis.read()) != -1)
								bos.write(inByte);
							bis.close();
							bos.close();
						}

						return entity != null ? "ok!" : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			String responseBody = httpclient.execute(httpget, responseHandler);
			System.out.println(responseBody);
			System.out.println("----------------------------------------");
		} finally {
			httpclient.close();
		}

	}

}
