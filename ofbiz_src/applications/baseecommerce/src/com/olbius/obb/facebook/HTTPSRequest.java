package com.olbius.obb.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.olbius.json.JSONObject;

public class HTTPSRequest {
	private String url;

	public HTTPSRequest() {
	}

	public HTTPSRequest(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getHTTPSContent() {
		try {
			URL urlObj = new URL(this.url);
			HttpsURLConnection con = (HttpsURLConnection) urlObj
					.openConnection();
			InputStream ins = con.getInputStream();
			if (con.getResponseCode() >= 400) {
				ins = con.getErrorStream();
			} else {
				ins = con.getInputStream();
			}
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(isr);

			String line;
			StringBuffer results = new StringBuffer();
			while ((line = br.readLine()) != null) {
				results.append(line);
			}
			br.close();
			JSONObject res = new JSONObject(results.toString());
			return res;

		} catch (IOException e) {
			e.printStackTrace();
			JSONObject obj = new JSONObject("{'status':'error'}");
			return obj;
		}
	}

	public String getHTTPSHtml() {
		try {
			URL urlObj = new URL(this.url);
			HttpsURLConnection con = (HttpsURLConnection) urlObj
					.openConnection();
			InputStream ins = con.getInputStream();
			if (con.getResponseCode() >= 400) {
				ins = con.getErrorStream();
			} else {
				ins = con.getInputStream();
			}
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(isr);

			String line;
			StringBuffer results = new StringBuffer();
			while ((line = br.readLine()) != null) {
				results.append(line);
			}
			br.close();
			return results.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
