package com.olbius.services;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;

public class FavouriteMenuService {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updatePreferMenu(DispatchContext ctx, Map<String, ? extends Object> context) throws Exception {
		JSONArray data = (JSONArray) context.get("data");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = ctx.getDelegator();
		// clear user favourite menu
		delegator.removeByCondition("UserLoginFunction", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.get("userLoginId")));
		// sotre user favourite menu
		for(int i = 0; i < data.size();i++){
			JSONObject tmpObject = data.getJSONObject(i);
			GenericValue tmpGV = delegator.makeValue("UserLoginFunction");
			tmpGV.put("userLoginId", userLogin.get("userLoginId"));
			tmpGV.put("link", tmpObject.getString("href"));
			tmpGV.put("title", tmpObject.getString("text"));
			tmpGV.put("sequenceNumber", String.valueOf(i + 1));
			tmpGV.create();
		}
		Map<String, Object> returnM = new HashMap<String, Object>();
		return returnM;
	}
}