package com.olbius.bi.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.ofbiz.base.util.Debug;

public class HttpSendRequest {

	public static final String module = HttpSendRequest.class.getName();

	public static void send(String url, String path, String[] param) {

		url += "/"+path;
		
		if (param != null && param.length > 0) {
			url += "?";
			for (int i = 0; i < param.length; i++) {
				url += param[i];
				if (i < param.length - 1) {
					url += "&";
				}
			}
		}

		URL tmp;

		try {

			tmp = new URL(url);
			HttpURLConnection con = (HttpURLConnection) tmp.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			responseContent(con);

		} catch (MalformedURLException e) {
			Debug.logError(e, module);
		} catch (IOException e) {
			Debug.logError(e, module);
		}

	}

	private static void responseContent(URLConnection con) {
		if (con != null) {

			try {

				Debug.logInfo("****** Content of the URL ********", module);
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null) {
					Debug.logInfo(input, module);
				}
				br.close();

			} catch (IOException e) {
				Debug.logError(e, module);
			}

		}

	}

	public static void upload(String url, String folder,String pathFile) {
		String charset = "UTF-8";
		File file = new File(pathFile);

		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n";

		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url+"/upload").openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			OutputStream output = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

			writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\""+folder+"\"; filename=\"" + file.getName() + "\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF).flush();
		    Files.copy(file.toPath(), output);
		    output.flush();
		    writer.append(CRLF).flush();
			
		    writer.append("--" + boundary + "--").append(CRLF).flush();
		    
		    int responseCode = connection.getResponseCode();
			Debug.logInfo("Upload response code: " + Integer.toString(responseCode), module);
		} catch (IOException e) {
			Debug.logError(e, module);
		}

	}

}
