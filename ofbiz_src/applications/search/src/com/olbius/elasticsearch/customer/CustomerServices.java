package com.olbius.elasticsearch.customer;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.olbius.elasticsearch.ElasticIndex;
import com.olbius.elasticsearch.ElasticIndexFactory;
import com.olbius.elasticsearch.client.IndexExecutor;
import com.olbius.elasticsearch.loader.ElasticSearchContainer;
import com.olbius.elasticsearch.loader.ESVersion;
import com.olbius.elasticsearch.object.Data;
import com.olbius.elasticsearch.object.GeoPoint;
import com.olbius.googlemaps.GoogleMapsQuery;
import com.olbius.googlemaps.LatLng;

public class CustomerServices {

	public static final String module = CustomerServices.class.getName();

	public static Map<String, Object> deleteIndex(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String index_name = (String) context.get("indexName");
		Debug.log(module + "::deleteIndex, index_name = " + index_name);
		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setElasticVeriosn(ESVersion.ES_1_4);
		index.setIndex(delegator, index_name);
		index.setType("venues");
		
		
		index.deleteIndex();// reset (remove) all items in the existing indices
		Debug.log(module + "::deleteIndex, index_name = " + index_name + " FINISHED");

		return ServiceUtil.returnSuccess();
	}

		
	public static Map<String, Object> indexPartyCustomers(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();
        String index_name = (String) context.get("indexName");
		List<EntityCondition> conds = FastList.newInstance();
		if ("customer".equals(index_name)) {
            conds.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.NOT_EQUAL, "SMCHANNEL_MT"));
        } else if ("mtcustomer".equals(index_name)) {
            conds.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_MT"));
        } else {
            return ServiceUtil.returnSuccess();
        }

		List<GenericValue> lst = delegator.findList("PartyCustomerAddressGeoPoint", EntityCondition.makeCondition(conds), null, null, null, false);

		Debug.log(module + "::indexPartyCustomers, number of items = " + lst.size());
		
		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setElasticVeriosn(ESVersion.ES_1_4);
		index.setIndex(delegator, index_name);
		index.setType("venues");
		
		Debug.log(module + "::indexPartyCustomers, index_name = " + index_name);
		

		
		/*
		OutletIndex ci = new OutletIndex();
		ci.setId("123456");
		ci.setPartyId(ci.getId());
		ci.setLocation(new GeoPoint(21, 105));
		
		index.indexDatas(ci);
		index.commit();
		Debug.log(module + "::indexOutlets, finish indexing ONE point");
		*/
		
		//if(true) return ServiceUtil.returnSuccess();
		
		//GenericValue value = null;
		
		try {
			int idx = 0;
			if(lst == null || lst.size() == 0){
				Debug.log(module + "::indexPartyCustomers, NO RECORDS");	
			}
			int ex = 0;
			for(GenericValue value: lst){
				
				OutletIndex cIndex = new OutletIndex();
	
				cIndex.setId(value.getString("partyId"));
				cIndex.setPartyId(cIndex.getId());
				if(value.getDouble("latitude") == null){
					Debug.log(module + "::indexPartyCustomers, partyId " + cIndex.getId() + " DOES NOT have latitude");
					ex++;
					continue;
				}
				if(value.getDouble("longitude") == null){
					Debug.log(module + "::indexPartyCustomers, partyId " + cIndex.getId() + " DOES NOT have longitude");
					continue;
				}
				idx++;
				GeoPoint p = new GeoPoint(value.getDouble("latitude"), value.getDouble("longitude"));
				cIndex.setLocation(p);
				
				index.indexDatas(cIndex);
				
				Debug.log(module + "::indexPartyCustomers, finished " + idx + "/" + lst.size() + ", Id = " + cIndex.getId() + ", partyId = " +
				cIndex.getPartyId() + ", location = " + 
						cIndex.getLocation().getLat() + "," + cIndex.getLocation().getLon() + ", none lat,lng = " + ex);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		} finally {
			index.commit();
		}

		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> indexAPartyCustomer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		double latitude = (Double)context.get("latitude");
		double longitude = (Double)context.get("longitude");
		
		
		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setIndex(delegator, (String) context.get("indexName"));
		index.setType("venues");

		
		try {
			
				OutletIndex cIndex = new OutletIndex();
	
				cIndex.setId(partyId);
				cIndex.setPartyId(partyId);
				
				GeoPoint p = new GeoPoint(latitude,longitude);
				cIndex.setLocation(p);
				
				index.indexDatas(cIndex);
				
				Debug.log(module + "::indexAPartyCustomer, finished,  Id = " + cIndex.getId() + ", partyId = " +
				cIndex.getPartyId() + ", location = " + 
						cIndex.getLocation().getLat() + "," + cIndex.getLocation().getLon());
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}

		index.commit();

		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> indexOutlets(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "RETAIL_OUTLET"));

		//EntityListIterator iterator = delegator.find(("OutletGeo", entityConditions, null, null, null, null);
		List<GenericValue> lst = delegator.findList("OutletGeo", entityConditions.get(0), null, null, null, false);
		
		String index_name = (String) context.get("indexName");
		Debug.log(module + "::indexOutlets, number of items = " + lst.size());
		
		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setElasticVeriosn(ESVersion.ES_1_4);
		index.setIndex(delegator, index_name);
		index.setType("venues");
		
		Debug.log(module + "::indexOutlets, index_name = " + index_name);
		

		
		/*
		OutletIndex ci = new OutletIndex();
		ci.setId("123456");
		ci.setPartyId(ci.getId());
		ci.setLocation(new GeoPoint(21, 105));
		
		index.indexDatas(ci);
		index.commit();
		Debug.log(module + "::indexOutlets, finish indexing ONE point");
		*/
		
		//if(true) return ServiceUtil.returnSuccess();
		
		//GenericValue value = null;
		
		try {
			int idx = 0;
			if(lst == null || lst.size() == 0){
				Debug.log(module + "::indexOutlets, NO RECORDS");	
			}
			int ex = 0;
			//while ((value = iterator.next()) != null) {
			for(GenericValue value: lst){
				
				OutletIndex cIndex = new OutletIndex();
	
				cIndex.setId(value.getString("partyId"));
				cIndex.setPartyId(cIndex.getId());
				if(value.getDouble("latitude") == null){
					Debug.log(module + "::indexOutlets, partyId " + cIndex.getId() + " DOES NOT have latitude");
					ex++;
					continue;
				}
				if(value.getDouble("longitude") == null){
					Debug.log(module + "::indexOutlets, partyId " + cIndex.getId() + " DOES NOT have longitude");
					continue;
				}
				idx++;
				GeoPoint p = new GeoPoint(value.getDouble("latitude"), value.getDouble("longitude"));
				cIndex.setLocation(p);
				
				index.indexDatas(cIndex);
				
				Debug.log("indexOutlets, finished " + idx + "/" + lst.size() + ", Id = " + cIndex.getId() + ", partyId = " +
				cIndex.getPartyId() + ", location = " + 
						cIndex.getLocation().getLat() + "," + cIndex.getLocation().getLon() + ", none lat,lng = " + ex);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		} finally {
			index.commit();
		}

		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> indexAnOutlet(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		double latitude = (Double)context.get("latitude");
		double longitude = (Double)context.get("longitude");
		
		
		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setIndex(delegator, (String) context.get("indexName"));
		index.setType("venues");

		
		try {
			
				OutletIndex cIndex = new OutletIndex();
	
				cIndex.setId(partyId);
				cIndex.setPartyId(partyId);
				
				GeoPoint p = new GeoPoint(latitude,longitude);
				cIndex.setLocation(p);
				
				index.indexDatas(cIndex);
				
				Debug.log(module + "::indexAnOutlet, finished,  Id = " + cIndex.getId() + ", partyId = " +
				cIndex.getPartyId() + ", location = " + 
						cIndex.getLocation().getLat() + "," + cIndex.getLocation().getLon());
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}

		index.commit();

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> updateCoordinates(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		Delegator delegator = ctx.getDelegator();
		try{	
			//PrintWriter out = new PrintWriter("C:/DungPQ/logs/outlets.txt","UTF-8");
			
			List<GenericValue> lst = delegator.findList("PartyContactTempData", null, null, null, null, false);
			int idx = 0;
			int nullGeoPointId = 0;
			int nonNullGeoPointId = 0;
			int nonAddr = 0;
			int countSucc = 0;
			
			for(GenericValue gv: lst){
				idx++;
				String addr = (String)gv.get("postalAddressName");
				
				//out.println(gv.get("partyId") + ",\t" + addr);
				
				
				String postalAddressId = (String)gv.get("postalAddressId");
				GenericValue gvAdr = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId",postalAddressId));
				if(gvAdr == null){
					nonAddr++;
					Debug.log("updateCoordinates, party " + (String)gv.get("partyId") + ", postalAddressId = " + postalAddressId
							+ ", but no corresponding record in PostalAddress table, nonAddr = " + nonAddr);
					
					
					continue;
				}
				if(gvAdr.get("geoPointId") != null){
					nonNullGeoPointId++;
					continue;
				}
				
				nullGeoPointId++;
				
				
				Debug.log("updateCoordinates, idx = " + idx + "/" + lst.size() + ", addr = " + addr + ", postalAddressId = " + postalAddressId + 
						" nulGeoPointId = " + nullGeoPointId + ", nonNullGeoPointId = " + nonNullGeoPointId);
				//if(true) continue;
					
				LatLng ll = G.getCoordinate(addr);
				
				//out.println(nullGeoPointId + "\t" + gv.get("partyId") + "\t" + addr);
				
				
				double lat = -10000;
				double lng = -10000;
				
				if(ll != null){
					countSucc++;
					lat = ll.lat;
					lng = ll.lng;
				
					//String postalAddressId = (String)gv.get("postalAddressId");
					
					String geoId = (String)delegator.getNextSeqId("GeoPoint");
					GenericValue gvgeo = delegator.makeValue("GeoPoint");
					gvgeo.put("geoPointId", geoId);
					gvgeo.put("latitude", lat);
					gvgeo.put("longitude", lng);
					delegator.create(gvgeo);
					Debug.log("updateCoordinates: " + countSucc + " create GeoPoint(" + geoId + "," + lat + "," + lng + ")");
					
					gvAdr.put("geoPointId", geoId);
					
					delegator.store(gvAdr);
					Debug.log("updateCoordinates: " + countSucc + " update geoPointId = " + geoId + " for record " + postalAddressId);
					
				}
				Debug.log("addr[" + idx + "] = " + addr + ", coordinate = (" + lat + "," + lng + "), countSucc = " + countSucc);
				if(idx > 1000) break;
				
				
				
			}
			
			//out.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	public static Map<String, Object> loadOutletAddress2File(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		//String filename = "C:/DungPQ/logs/outlets.txt";//(String)context.get("filename");
		String filename = (String)context.get("filename");
		Debug.log(module + "::loadOutletAddress2File, filename = " + filename);
		
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		Delegator delegator = ctx.getDelegator();
		try{	
			PrintWriter out = new PrintWriter(filename,"UTF-8");
			
			List<GenericValue> lst = delegator.findList("PartyContactTempData", null, null, null, null, false);
			int idx = 0;
			int nullGeoPointId = 0;
			int nonNullGeoPointId = 0;
			int nonAddr = 0;
			int countSucc = 0;
			
			for(GenericValue gv: lst){
				idx++;
				String addr = (String)gv.get("postalAddressName");
				
				//out.println(gv.get("partyId") + ",\t" + addr);
				
				
				String postalAddressId = (String)gv.get("postalAddressId");
				GenericValue gvAdr = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId",postalAddressId));
				if(gvAdr == null){
					nonAddr++;
					Debug.log("updateCoordinates, party " + (String)gv.get("partyId") + ", postalAddressId = " + postalAddressId
							+ ", but no corresponding record in PostalAddress table, nonAddr = " + nonAddr);
					
					
					continue;
				}
				String latlng = "NULL";
				if(gvAdr.get("geoPointId") != null){
					nonNullGeoPointId++;
					//continue;
					GenericValue gp = delegator.findOne("GeoPoint", false, UtilMisc.toMap("geoPointId",gvAdr.get("geoPointId")));
					latlng = (Double)gp.get("latitude") + "," + (Double)gp.get("longitude");
				}else{
				
					nullGeoPointId++;
				}
				
				
				Debug.log("updateCoordinates, idx = " + idx + "/" + lst.size() + ", addr = " + addr + ", postalAddressId = " + postalAddressId + 
						" nulGeoPointId = " + nullGeoPointId + ", nonNullGeoPointId = " + nonNullGeoPointId);
				//if(true) continue;
					
				//LatLng ll = G.getCoordinate(addr);
				
				out.println(nullGeoPointId + "\t" + gv.get("partyId") + "\t" + addr + "\t" + latlng);
				
				/*
				double lat = -10000;
				double lng = -10000;
				
				if(ll != null){
					countSucc++;
					lat = ll.lat;
					lng = ll.lng;
				
					//String postalAddressId = (String)gv.get("postalAddressId");
					
					String geoId = (String)delegator.getNextSeqId("GeoPoint");
					GenericValue gvgeo = delegator.makeValue("GeoPoint");
					gvgeo.put("geoPointId", geoId);
					gvgeo.put("latitude", lat);
					gvgeo.put("longitude", lng);
					delegator.create(gvgeo);
					Debug.log("updateCoordinates: " + countSucc + " create GeoPoint(" + geoId + "," + lat + "," + lng + ")");
					
					gvAdr.put("geoPointId", geoId);
					
					delegator.store(gvAdr);
					Debug.log("updateCoordinates: " + countSucc + " update geoPointId = " + geoId + " for record " + postalAddressId);
					
				}
				Debug.log("addr[" + idx + "] = " + addr + ", coordinate = (" + lat + "," + lng + "), countSucc = " + countSucc);
				if(idx > 1000) break;
				
				*/
				
			}
			
			out.println("-1");
			out.println("nul-geo-points = " + nullGeoPointId + ", non-null=geo-points = " + nonNullGeoPointId);
			
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> updateCoordinatesFromExtenalJSON(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		//String filename = "C:/DungPQ/logs/outlets.txt";//(String)context.get("filename");
		//Debug.log(module + "::updateCoordinatesFromFile, filename = " + filename);
		Debug.log(module + "::updateCoordinatesFromExtenalJSON");
		//if(true) return ServiceUtil.returnError("abc");
			
		//GenericValue I = (GenericValue)context.get("list");
		
		List<GenericValue> L = null;//(List<GenericValue>)context.get("list");
		//Gson gson = new Gson();
		String json = (String)context.get("list");
		Debug.log(module + "::updateCoordinatesFromExtenalJSON, json = " + json);
	
		//JSONArray arr = JSONArray.fromObject(json);
		JSONObject o = JSONObject.fromObject(json);////arr.getJSONObject(0);
		JSONArray list = (JSONArray)o.get("list");
		
		Map<String, Object> partyId2LatLng = FastMap.newInstance();
		
		for(int i = 0; i < list.size(); i++){
			JSONObject oi = list.getJSONObject(i);
			String partyId = (String)oi.get("partyId");
			JSONObject llo = (JSONObject)oi.get("location");
			double lat = (Double)llo.get("lat");
			double lng = (Double)llo.get("lng");
			LatLng ll = new LatLng(lat, lng);
			Debug.log(module + "::updateCoordinatesFromExtenalJSON, id = " + partyId + ", location = " + ll.toString());
			partyId2LatLng.put(partyId, ll);
		}
		//if(true) return null;
		
		//Debug.log(module + "::updateCoordinatesFromExtenalJSON, L.sz = " + L.size());
		
		//Map<String, Object> partyId2LatLng = FastMap.newInstance();
		//for(GenericValue gv: L){
			//String partyId = (String)gv.get("partyId");
			//LatLng ll = (LatLng)gv.get("location");
			
			//Debug.log(module + "::updateCoordinatesFromExtenalJSON, partyId " + partyId + " has location " + ll.toString());
		//}
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		Delegator delegator = ctx.getDelegator();
		
		try{	
			//Map<String, Object> partyId2LatLng = G.loadLatLng(filename);
			
			//if(partyId2LatLng == null) return ServiceUtil.returnError("loadLatLng from file " + filename + " failed");
			
			List<GenericValue> lst = delegator.findList("PartyContactTempData", null, null, null, null, false);
			int idx = 0;
			int nullGeoPointId = 0;
			int nonNullGeoPointId = 0;
			int nonAddr = 0;
			int countSucc = 0;
			
			for(GenericValue gv: lst){
				idx++;
				String addr = (String)gv.get("postalAddressName");
				
				//out.println(gv.get("partyId") + ",\t" + addr);
				
				
				String postalAddressId = (String)gv.get("postalAddressId");
				GenericValue gvAdr = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId",postalAddressId));
				if(gvAdr == null){
					nonAddr++;
					Debug.log(module + "::updateCoordinatesFromExtenalJSON, party " + (String)gv.get("partyId") + ", postalAddressId = " + postalAddressId
							+ ", but no corresponding record in PostalAddress table, nonAddr = " + nonAddr);
					
					
					continue;
				}
				if(gvAdr.get("geoPointId") != null){
					nonNullGeoPointId++;
					continue;
				}
				
				nullGeoPointId++;
				
				
				Debug.log(module + "::updateCoordinatesFromExtenalJSON, idx = " + idx + "/" + lst.size() + ", addr = " + addr + ", postalAddressId = " + postalAddressId + 
						" nulGeoPointId = " + nullGeoPointId + ", nonNullGeoPointId = " + nonNullGeoPointId);
				//if(true) continue;
					
				LatLng ll = (LatLng)partyId2LatLng.get(gv.get("partyId"));//G.getCoordinate(addr);
				
				
				//System.out.println(nullGeoPointId + "\t" + gv.get("partyId") + "\t" + addr);
				
				
				double lat = -10000;
				double lng = -10000;
				
				if(ll != null){
					countSucc++;
					lat = ll.lat;
					lng = ll.lng;
				
					//String postalAddressId = (String)gv.get("postalAddressId");
					
					String geoId = (String)delegator.getNextSeqId("GeoPoint");
					GenericValue gvgeo = delegator.makeValue("GeoPoint");
					gvgeo.put("geoPointId", geoId);
					gvgeo.put("latitude", lat);
					gvgeo.put("longitude", lng);
					delegator.create(gvgeo);
					Debug.log(module + "::updateCoordinatesFromExtenalJSON: " + countSucc + " create GeoPoint(" + geoId + "," + lat + "," + lng + ")");
					
					gvAdr.put("geoPointId", geoId);
					
					delegator.store(gvAdr);
					Debug.log(module + "::updateCoordinatesFromExtenalJSON: " + countSucc + " update geoPointId = " + geoId + " for record " + postalAddressId);
					
				}
				Debug.log(module + "::updateCoordinatesFromExtenalJSON, addr[" + idx + "] = " + addr + ", coordinate = (" + lat + "," + lng + "), countSucc = " + countSucc);
				//if(idx > 1000) break;
				
				
				
			}
			
			//out.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	
	public static Map<String, Object> updateCoordinateOfACustomer(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Debug.log(module + "::updateCoordinateOfACustomer, partyId = " + partyId);
		
		retSucc.put("partyId", partyId);
		
		try{	
			
			
			List<GenericValue> lst = delegator.findList("PartyContactTempData", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)), 
					null, null, null, false);
			if(lst.size() == 0){
				return ServiceUtil.returnError("no party having partyId = " + partyId + " in Table PartyContactTempData");
			}
			if(lst.size() > 1){
				return ServiceUtil.returnError("many parties having partyId = " + partyId + " in Table PartyContactTempData");
			}
			
			GenericValue gv = lst.get(0);
				String addr = (String)gv.get("postalAddressName");
				String postalAddressId = (String)gv.get("postalAddressId");
				GenericValue gvAdr = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId",postalAddressId));
				if(gvAdr == null){
					Debug.log(module + "::updateCoordinateOfACustomer, party " + (String)gv.get("partyId") + ", postalAddressId = " + postalAddressId
							+ ", but no corresponding record in PostalAddress table");
				}
				if(gvAdr.get("geoPointId") != null){
					return retSucc;					
				}
				
				LatLng ll = G.getCoordinate(addr);
				double lat = -1000000;
				double lng = -1000000;
				
				if(ll != null){
					lat = ll.lat;
					lng = ll.lng;
					
					String geoId = (String)delegator.getNextSeqId("GeoPoint");
					GenericValue gvgeo = delegator.makeValue("GeoPoint");
					gvgeo.put("geoPointId", geoId);
					gvgeo.put("latitude", lat);
					gvgeo.put("longitude", lng);
					delegator.create(gvgeo);
					Debug.log(module + "::updateCoordinateOfACustomer:  create GeoPoint(" + geoId + "," + lat + "," + lng + ")");
					
					gvAdr.put("geoPointId", geoId);
					
					delegator.store(gvAdr);
					Debug.log(module + "::updateCoordinateOfACustomer:  update geoPointId = " + geoId + " for record " + postalAddressId);
			
				}
				Debug.log(module + "::updateCoordinateOfACustomer, partyId = " + partyId + ", latitude = " + lat + ", longitude = " + lng);
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	public static Map<String, Object> queryCoordinates(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Debug.log(module + "::queryCoordinate, partyId = " + partyId);
		
		try{	
		
			List<GenericValue> lst = delegator.findList("PartyContactTempData", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)), 
					null, null, null, false);
			if(lst.size() == 0){
				return ServiceUtil.returnError("no party having partyId = " + partyId + " in Table PartyContactTempData");
			}
			if(lst.size() > 1){
				return ServiceUtil.returnError("many parties having partyId = " + partyId + " in Table PartyContactTempData");
			}
			
			GenericValue gv = lst.get(0);
				String addr = (String)gv.get("postalAddressName");
				
				LatLng ll = G.getCoordinate(addr);
				double lat = -1000000;
				double lng = -1000000;
				
				if(ll != null){
					lat = ll.lat;
					lng = ll.lng;
				}
				Debug.log(module + "::queryCoordinate, partyId = " + partyId + ", latitude = " + lat + ", longitude = " + lng);
				
				
				retSucc.put("latitude", lat);
				retSucc.put("longitude", lng);
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> indexPotentialCustomers(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		EntityCondition condition = null;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(EntityCondition.makeCondition("ctmThruDate", EntityOperator.EQUALS, null));

		entityConditions
				.add(EntityCondition.makeCondition("ctmThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timestamp));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.OR);

		entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(condition);

		entityConditions
				.add(EntityCondition.makeCondition("ctmFromDate", EntityOperator.LESS_THAN_EQUAL_TO, timestamp));

		entityConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
		entityConditions
				.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

		EntityListIterator iterator = delegator.find("PartyGroupFromGeoView", condition, null, null, null, null);

		String index = (String) context.get("indexName");
		String type = "venues";
		
		GenericValue value = null;
		IndexExecutor executor = null;
		
		try{
			while ((value = iterator.next()) != null) {
				CustomerIndex cIndex = new CustomerIndex();
	
				cIndex.setId(value.getString("partyIdFrom"));
				cIndex.setCustomerId(value.getString("partyIdFrom"));
				cIndex.setPartyIdTo(value.getString("partyIdTo"));
				cIndex.setStateProvinceGeoId(value.getString("stateProvinceGeoId"));
				cIndex.setDistrictGeoId(value.getString("districtGeoId"));
				cIndex.setLocation(new GeoPoint(value.getDouble("latitude"), value.getDouble("longitude")));
	
				Data data = Data.buildData(index, type, cIndex);
				
				executor = ElasticSearchContainer.FACTORY.client(ESVersion.ES_1_4).indexData(data);
			}
		} finally {
			if (executor != null) {
				executor.commit();
			}
			if(iterator != null) {
				iterator.close();
			}
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> indexCustomers(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		EntityCondition condition = null;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(EntityCondition.makeCondition("ctmThruDate", EntityOperator.EQUALS, null));

		entityConditions
				.add(EntityCondition.makeCondition("ctmThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timestamp));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.OR);

		entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(condition);

		entityConditions
				.add(EntityCondition.makeCondition("ctmFromDate", EntityOperator.LESS_THAN_EQUAL_TO, timestamp));

		entityConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ROUTE"));
		entityConditions
				.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "CUSTOMER"));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

		EntityListIterator iterator = delegator.find("PartyGroupFromGeoView", condition, null, null, null, null);

		String index = (String) context.get("indexName");
		String type = "venues";
		
		GenericValue value = null;
		IndexExecutor executor = null;
		
		try {
			while ((value = iterator.next()) != null) {
				CustomerIndex cIndex = new CustomerIndex();
	
				cIndex.setId(value.getString("partyIdTo"));
				cIndex.setPartyId(value.getString("partyIdTo"));
				cIndex.setRoutePartyId(value.getString("partyIdFrom"));
				cIndex.setLocation(new GeoPoint(value.getDouble("latitude"), value.getDouble("longitude")));
				
				value = delegator.findOne("Party", UtilMisc.toMap("partyId", value.getString("partyIdFrom")), false);
	
				if(value != null) {
					value = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", value.getString("createdByUserLogin")), false);
					if(value != null) {
						cIndex.setSupPartyId(value.getString("partyId"));
					}
				}
				
				Data data = Data.buildData(index, type, cIndex);
				
				executor = ElasticSearchContainer.FACTORY.client(ESVersion.ES_1_4).indexData(data);
			}
		} finally {
			if (executor != null) {
				executor.commit();
			}
			if(iterator != null) {
				iterator.close();
			}
		}

		return ServiceUtil.returnSuccess();
	}

}
