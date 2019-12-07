package com.olbius.baselogistics.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

public class GeoUtil {
	public static List<GenericValue> getGeoAssocs(Delegator delegator, String geoId, String geoAssocTypeId, String geoTypeId) throws GenericServiceException, GenericEntityException{
		List<GenericValue> listGeos = new ArrayList<GenericValue>();
		List<GenericValue> listGeoAssocs = delegator.findList("GeoAssoc", EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", geoAssocTypeId)), null, null, null, false);
		if (!listGeoAssocs.isEmpty()){
			for (GenericValue item : listGeoAssocs){
				GenericValue geo = delegator.findOne("Geo", false, UtilMisc.toMap("geoId", item.getString("geoIdTo")));
				if (geo != null && geoTypeId.equals(geo.getString("geoTypeId"))){
					listGeos.add(geo);
				}
			}
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listGeos", listGeos);
    	return listGeos;
	}
	
	public static List<Map<String, String>> loadGeoAssocListByGeoId(Delegator delegator, String geoId) throws GenericEntityException{
		GenericValue geoAssoc = null;
		String geoIdTo = null;
		List<GenericValue> listGeoAssoc = delegator.findList("GeoAssoc", EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", "REGIONS")), null, null, null, false);
		List<Map<String, String>> listGeoAssocMap = new ArrayList<Map<String,String>>();
		for (GenericValue geoAssocData : listGeoAssoc) {
			if(geoAssocData != null){
				geoIdTo = (String) geoAssocData.get("geoIdTo");
				geoAssoc = delegator.findOne("Geo", false, UtilMisc.toMap("geoId", geoIdTo));
			}
			Map<String, String> geoAssocMap =  new HashMap<String,String>(); 		
			if (geoAssoc != null){
				String geoName = (String) geoAssoc.get("geoName");
				geoAssocMap.put(geoIdTo, geoName);
			}
	    	listGeoAssocMap.add(geoAssocMap);
		}
		return listGeoAssocMap; 
	}
}
