package com.olbius.events;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

public class FreMenuEvents {
	public static final String module = FreMenuEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String freMenuEventProcessor(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, String[]> params = request.getParameterMap();
        JSONArray jsonArray = JSONArray.fromObject(params.get("data")); 
        jsonArray = jsonArray.getJSONArray(0);
        Map<String,Object> context = new HashMap<String,Object>();
        context.put("data", jsonArray);
        context.put("userLogin", userLogin);
        JSONObject json;
        Map<String, Object> mapResult;
        try {
        	dispatcher.runSync("updatePreferMenu", context);
		} catch (Exception e) {
			Debug.logError("Problems with freMenuEventProcessor: " + e.toString(), module);
			mapResult = new HashMap<String, Object>();
			mapResult.put("responseMessage", "error");
			mapResult.put("errorMessage", e.toString());
			json = JSONObject.fromObject(mapResult);
			writeJSONtoResponse(json, response);
		}
        mapResult = new HashMap<String, Object>();
        mapResult.put("responseMessage", "success");
		json = JSONObject.fromObject(mapResult);
		writeJSONtoResponse(json, response);
		return "success";
	}
	private static void writeJSONtoResponse(JSON json, HttpServletResponse response) {
        String jsonStr = json.toString();
        if (jsonStr == null) {
            Debug.logError("JSON Object was empty; fatal error!", module);
            return;
        }

        // set the X-JSON content type
        response.setContentType("application/x-json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding: " + e, module);
        }

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }
    }
}