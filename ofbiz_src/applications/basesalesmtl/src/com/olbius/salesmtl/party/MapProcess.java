package com.olbius.salesmtl.party;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.PartyUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class MapProcess {

	public static Map<String, Object> cache = FastMap.newInstance();
	private static long expiredTime = 3600000;
//	public static Map<String, Object> getBoundary(Delegator delegator, EntityCondition extra, Double neLat, Double neLong, Double swLat, Double swLong){
//		Map<String, Object> res = FastMap.newInstance();
//		EntityListIterator list = null;
//		List<EntityCondition> listConditions = FastList.newInstance();
//		Double latSize = neLat - swLat;
//		double tmp = range;
//		Double total = Math.ceil(latSize / tmp);
//		Integer totalIv = total.intValue();
//		//Check in boundary first
//		if(extra != null){
//			listConditions.add(extra);
//		}
////		listConditions.add(EntityCondition.makeCondition("latitude", EntityOperator.LESS_THAN_EQUAL_TO, neLat));
////		listConditions.add(EntityCondition.makeCondition("latitude", EntityOperator.GREATER_THAN_EQUAL_TO, swLat));
//		listConditions.add(EntityCondition.makeCondition("longitude", EntityOperator.LESS_THAN_EQUAL_TO, neLong));
//		listConditions.add(EntityCondition.makeCondition("longitude", EntityOperator.GREATER_THAN_EQUAL_TO, swLong));
//		//check in layer
//		double start = 0;
//		double end = 0;
//		List<Map<String, Object>> tmList;
//		int endIndex = totalIv - 1;
//		int size = 0;
//		List<Map<String, Object>> left = FastList.newInstance();
//		List<Map<String, Object>> right = FastList.newInstance();
//		for(int i = 0; i < totalIv; i++){
//			start = i * tmp + swLat;
//			end = start + tmp;
//			end = end <= neLat ? end : neLat;
//			List<EntityCondition> tmp2 = FastList.newInstance();
//			tmp2.addAll(listConditions);
//			tmp2.add(EntityCondition.makeCondition("latitude", EntityOperator.GREATER_THAN_EQUAL_TO, start));
//			if(i == endIndex){
//				tmp2.add(EntityCondition.makeCondition("latitude", EntityOperator.LESS_THAN_EQUAL_TO, end));
//			}else{
//				tmp2.add(EntityCondition.makeCondition("latitude", EntityOperator.LESS_THAN, end));
//			}
//			tmList = getBoundary(delegator, tmp2);
//			size = tmList.size();
//			if(size == 1){
//				left.addAll(tmList);
//			}else if(size == 2){
//				left.add(tmList.get(0));
//				right.add(tmList.get(1));
//			}
//		}
//		res.put("left", left);
//		res.put("right", right);
//		return res;
//	}
//	public static List<Map<String, Object>> getBoundary(Delegator delegator, List<EntityCondition> listConditions){
//		List<Map<String, Object>> res = FastList.newInstance();
//		EntityListIterator list = null;
//		try {
//			EntityFindOptions ops = new EntityFindOptions();
//			ops.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
//			list = delegator.find("PartyGroupFromGeoView", EntityCondition.makeCondition(listConditions), null, null, UtilMisc.toList("longitude"), ops);
//			GenericValue e =  list.next();
//			if(e != null){
//				Map<String, Object> o = UtilMisc.toMap("latitude", e.getDouble("latitude"), "longitude", e.getDouble("longitude"), "partyIdFrom", e.getString("partyIdFrom"));
//				res.add(o);
//			}
//			list.afterLast();
//			GenericValue f = list.previous();
//			if(f != null && !f.getString("geoPointId").equals(e.getString("geoPointId"))){
//				Map<String, Object> o = UtilMisc.toMap("latitude", f.getDouble("latitude"), "longitude", f.getDouble("longitude"), "partyIdFrom", f.getString("partyIdFrom"));
//				res.add(o);
//			}
//			int size = list.getResultsTotalSize();
//		} catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//		} finally {
//			if(list != null){
//				try {
//					list.close();
//				} catch (GenericEntityException e) {
//					Debug.log(e.getMessage());
//				}
//			}
//		}
//		return res;
//	}
	public static Map<String, Object> getBoundary(Delegator delegator, String key, EntityCondition extra){
		Map<String, Object> tmp = (Map<String, Object>) getCache(key);
		if(tmp != null){
			return tmp;
		}
		Map<String, Object> res = FastMap.newInstance();
		List<EntityCondition> listConditions = FastList.newInstance();
		//Check in boundary first
		if(extra != null){
			listConditions.add(extra);
		}
		listConditions.add(EntityCondition.makeCondition("latitude", EntityOperator.NOT_EQUAL, null));
		listConditions.add(EntityCondition.makeCondition("longitude", EntityOperator.NOT_EQUAL, null));
		//check in layer
		List<Map<String, Object>> tmList;
		tmList = getBoundary(delegator, listConditions, UtilMisc.toList("latitude"));
		res.put("bottom", tmList);
		tmList = getBoundary(delegator, listConditions, UtilMisc.toList("-latitude"));
		res.put("top", tmList);
		tmList = getBoundary(delegator, listConditions, UtilMisc.toList("longitude"));
		res.put("left", tmList);
		tmList = getBoundary(delegator, listConditions, UtilMisc.toList("-longitude"));
		res.put("right", tmList);
		setCache(key, res);
		return res;
	}
	public static List<Map<String, Object>> getBoundary(Delegator delegator, List<EntityCondition> listConditions, List<String> orders){
		List<Map<String, Object>> res = FastList.newInstance();
		try {
			EntityFindOptions ops = new EntityFindOptions();
			ops.setMaxRows(10);
			ops.setLimit(10);
			List<GenericValue> tmp = delegator.findList("PartyGroupFromGeoView", EntityCondition.makeCondition(listConditions), UtilMisc.toSet("latitude", "longitude"), orders, ops, false);
			res.addAll(tmp);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return res;
	}
	public static Object getCache(String key){
		StringBuilder tmp = new StringBuilder();
		tmp.append(key).append("-FROMDATE");
		String tmpKey = tmp.toString();
		Timestamp fromDate = (Timestamp) cache.get(tmpKey);
		Timestamp now = UtilDateTime.nowTimestamp();
		long during = 0;
		if(fromDate != null){
			during = now.getTime() - fromDate.getTime();
		}
		if(cache.containsKey(key) && during < expiredTime){
			return (Object) cache.get(key);
		}
		return null;
	}
	public static Object setCache(String key, Object value){
		StringBuilder tmp = new StringBuilder();
		tmp.append(key).append("-FROMDATE");
		String tmpKey = tmp.toString();
		Timestamp now = UtilDateTime.nowTimestamp();
		cache.put(tmpKey, now);
		cache.put(key, value);
		return null;
	}
}
