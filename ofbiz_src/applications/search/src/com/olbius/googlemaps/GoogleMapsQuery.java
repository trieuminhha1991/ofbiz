package com.olbius.googlemaps;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Response;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;



public class GoogleMapsQuery {
	public String name(){
		return GoogleMapsQuery.class.getName();
	}
	
	public double computeDistanceHaversine(double lat1, double long1,
			double lat2, double long2) {
		double SCALE = 1;
		double PI = 3.14159265;
		long1 = long1 * 1.0 / SCALE;
		lat1 = lat1 * 1.0 / SCALE;
		long2 = long2 * 1.0 / SCALE;
		lat2 = lat2 * 1.0 / SCALE;

		double dlat1 = lat1 * PI / 180;
		double dlong1 = long1 * PI / 180;
		double dlat2 = lat2 * PI / 180;
		double dlong2 = long2 * PI / 180;

		double dlong = dlong2 - dlong1;
		double dlat = dlat2 - dlat1;

		double aHarv = Math.pow(Math.sin(dlat / 2), 2.0) + Math.cos(dlat1)
				* Math.cos(dlat2) * Math.pow(Math.sin(dlong / 2), 2.0);
		double cHarv = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1.0 - aHarv));

		double R = 6378.137; // in km

		return R * cHarv * SCALE; // in km

	}

	public LatLng getCoordinate(String address) {
		if(address == null || address.equals("")) return null;
		
		URL url = null;
		try {
			url = new URL(
					"http://maps.google.com/maps/api/geocode/xml?address="
							+ URLEncoder.encode(address, "UTF-8")
							//+ address
							+ "&sensor=false");
			System.out.println(name() + "::getCoordinate, url = " + url);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		HttpURLConnection urlConn = null;
		try {
			// URL connection channel.
			urlConn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);

		// Let the RTS know that we want to do output.
		urlConn.setDoOutput(true);
		// No caching, we want the real thing.
		urlConn.setUseCaches(false);
		try {
			urlConn.setRequestMethod("POST");
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}
		try {
			urlConn.connect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		DataOutputStream output = null;
		DataInputStream input = null;
		try {
			output = new DataOutputStream(urlConn.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Get response data.
		String str = null;
		try {
			PrintWriter out = new PrintWriter(new FileWriter(
					"http-post-log.txt"));
			input = new DataInputStream(urlConn.getInputStream());
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();

				NodeList nl = doc.getElementsByTagName("geometry");
				Node nod = nl.item(0);
				Element e = (Element) nod;
				if (e == null) {
					return null;
				}

				nl = e.getElementsByTagName("location");
				nod = nl.item(nl.getLength() - 1);
				
				e = (Element) nod;
				nl = e.getElementsByTagName("lat");
				nod = nl.item(0);

				String d_s = nod.getChildNodes().item(0).getNodeValue();
				double lat = Double.valueOf(d_s);

				nl = e.getElementsByTagName("lng");
				nod = nl.item(0);
				d_s = nod.getChildNodes().item(0).getNodeValue();
				double lng = Double.valueOf(d_s);

				return new LatLng(lat, lng);
			} catch (Exception e) {
				e.printStackTrace();
			}

			input.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void createJSONLatlng(Map<String, Object> mId2LatLng, HashSet<String> nonLatLngDB, String filename){
		try{
			ArrayList<PartyIDLatLng> list = new ArrayList<PartyIDLatLng>();//PartyIDLatLng[mId2LatLng.keySet().size()];
			int idx = -1;
			for(String id: mId2LatLng.keySet())if(nonLatLngDB.contains(id)){
				idx++;
				LatLng ll = (LatLng)mId2LatLng.get(id);
				list.add(new PartyIDLatLng(id,ll));
				System.out.println("item " + idx + ", id = " + id + ", location = " + ll.toString());
			}
			
			PartyIDLatLng[] a = new PartyIDLatLng[list.size()];
			for(int i= 0;i < list.size(); i++)
				a[i] = list.get(i);
			
			PartyLocationInfo pl = new PartyLocationInfo(a);
			
			Gson gson = new Gson();
			//gson.toJson(pl,new FileWriter(filename));
			
			String json = gson.toJson(pl);
			
			
			PrintWriter out = new PrintWriter(filename);
			out.print(json);
			out.close();
			
			System.out.println(name() + "::createJSONLatLng, latlng.sz = " + mId2LatLng.keySet().size() + ", nonLatLngDB.sz = "
					+ nonLatLngDB.size() + ", result list.sz = " + list.size());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public Map<String, Object> loadLatLng(String filename){
		try{
			System.out.println(name() + "::loadLatLng, filename = " + filename);
		
			Map<String, Object> mId2LatLng = FastMap.newInstance();
		
			Scanner in = new Scanner(new File(filename));
			
			
			int count = 0;
			while(true){
				String line = in.nextLine();
				if(line.equals("-1")) break;
				
				String[] s = line.split("\t");
				
				
				String lat_lng = s[3];
				
				if(!lat_lng.equals("NULL")){
					String[] sll = lat_lng.split(",");
					double lat = Double.valueOf(sll[0]);
					double lng = Double.valueOf(sll[1]);
					LatLng ll = new LatLng(lat, lng);
					mId2LatLng.put(s[1], ll);
					
					count++;
					System.out.println(name() + "::loadLatLng, count = " + count + ", "
							+ "ID = " + s[1] + " latlng = " + ll.lat + "," + ll.lng);
				}
				
			}
			in.close();
			
			return mId2LatLng;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
		
	}
	public HashSet<String> loadNonLatLng(String filename){
		try{
			System.out.println(name() + "::loadNonLatLng, filename = " + filename);
		
			HashSet<String> S = new HashSet<String>();
		
			Scanner in = new Scanner(new File(filename));
			
			
			int count = 0;
			while(true){
				String line = in.nextLine();
				if(line.equals("-1")) break;
				
				String[] s = line.split("\t");
				
				
				String lat_lng = s[3];
				
				if(lat_lng.equals("NULL")){
					S.add(s[1]);
					
					count++;
					System.out.println(name() + "::loadLatLng, count = " + count + ", "
							+ "ID = " + s[1]);
				}
				
			}
			in.close();
			
			return S;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
		
	}

	
	public void queryCoordinate(String fn, String notSuccessFilename){

		try{
			PrintWriter log = new PrintWriter("C:/DungPQ/logs/FailAddr.txt");
			
			ArrayList<String> indices = new ArrayList<String>();
			ArrayList<String> code = new ArrayList<String>();
			ArrayList<String> addr = new ArrayList<String>();
			ArrayList<String> latlng = new ArrayList<String>();
			HashSet<String> notSuccess = new HashSet<String>();
			
			Scanner tmpIn = new Scanner(notSuccessFilename);
			while(tmpIn.hasNext()){
				String s = tmpIn.nextLine();
				notSuccess.add(s);
			}
			
			Scanner in = new Scanner(new File(fn));
			int idx = 0;
			int count = 0;
			int nbQuery = 0;
			int nbLatLng = 0;
			while(true){
				String line = in.nextLine();
				if(line.equals("-1")) break;
				idx++;
				String[] s = line.split("\t");
				//System.out.println(idx + "\t" + s[1] + "\t" + s[2] + "\t" + "NULL");
				indices.add(s[0]);
				code.add(s[1]);
				addr.add(s[2]);
				String lat_lng = s[3];
				
				System.out.println(s[0] + "\t" + s[1] + "\t" + s[2] + "\t" + s[3]);
				
				if(lat_lng.equals("NULL") && nbQuery < 10000 && !notSuccess.contains(s[1])){
					LatLng ll = getCoordinate(s[2]);
					nbQuery++;
					if(ll != null){
						lat_lng = ll.lat + "," + ll.lng;
						count++;
						nbLatLng++;
					}else{
						notSuccess.add(s[1]);
						log.println(s[0] + "\t" + s[1] + "\t" + s[2] + "\t" + s[3]);
					}
					System.out.println(s[0] + "\t" + s[1] + "\t" + s[2] + "\t" + s[3] + " --> query");
				}else{
					if(!lat_lng.equals("NULL"))nbLatLng++;
				}
				System.out.println(s[0] + "\t" + s[1] + "\t" + s[2] + "\t" + s[3] + "\t" + lat_lng +
						", count = " + count + ", nbLatLng = " + nbLatLng + ", nbQuery = " + nbQuery);
				
				latlng.add(lat_lng);
			}
			log.close();
			in.close();
			
			PrintWriter out = new PrintWriter(fn);
			for(int i = 0; i < indices.size(); i++){
				out.println(indices.get(i) + "\t" + code.get(i) + "\t" + addr.get(i) + "\t" + latlng.get(i));
			}
			out.println("-1");
			out.close();
			
			out = new PrintWriter(notSuccessFilename);
			for(String s: notSuccess){
				out.println(s);
			}
			out.println("-1");
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void createJSON(){
		GoogleMapsQuery G = new GoogleMapsQuery();
		
		Map<String, Object> m_new_query = G.loadLatLng("C:/DungPQ/logs/outlets.txt");
		HashSet<String> m_db = G.loadNonLatLng("C:/DungPQ/logs/outlets-DB.txt");
		G.createJSONLatlng(m_new_query, m_db, "C:/DungPQ/logs/outlets.json");
		
	}
	public void queryCoordinates(){
		GoogleMapsQuery G = new GoogleMapsQuery();
		G.queryCoordinate("C:/DungPQ/logs/outlets.txt","C:/DungPQ/logs/notSuccess.txt");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		
		
		//G.createJSON();
		G.queryCoordinates();
		
		/*
		G.queryCoordinate("C:/DungPQ/logs/outlets.txt","C:/DungPQ/logs/notSuccess.txt");
		if(true) return;
		
		String addr = "";
		try{
			Scanner in = new Scanner(new File("C:/tmp/addr.txt"));
			addr = in.nextLine();
			System.out.println("addr = " + addr);
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		LatLng ll = G.getCoordinate(addr);
		if(ll != null){
			System.out.println(ll.toString());
		}else System.out.println("NULL");
		*/
	}

}
