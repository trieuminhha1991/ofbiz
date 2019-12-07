package com.olbius.appbase.events;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class OlbWizardEvents {
	public final static String module = OlbWizardEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String jqGetAssociatedStateOtherListGeo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator((String) request.getSession().getAttribute("delegatorName"));
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceContainer.getLocalDispatcher(null, delegator);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        TimeZone timeZone = (TimeZone) request.getSession().getAttribute("timeZone");
        Map<String, String[]> params = request.getParameterMap();
        Locale locale = UtilHttp.getLocale(request);
        
        Map<String, String[]> paramsTmp = new HashMap<String, String[]>();
        if (params != null) {
        	for (Map.Entry<String, String[]> entry : params.entrySet()) {
        		paramsTmp.put(entry.getKey(), entry.getValue());
        	}
        	paramsTmp.put("sname", new String[] {"JQGetAssociatedStateOtherListGeo"});
        }
        
        Map<String,Object> context = new HashMap<String,Object>();
        context.put("parameters", paramsTmp);
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        try {
        	GenericValue userLoginSystem = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        	context.put("userLogin", userLoginSystem);
        	Map<String,Object> results = dispatcher.runSync("jqxGridGeneralServicer", context);
        	JsonConfig config = new JsonConfig();
        	JSONObject json = JSONObject.fromObject(results, config);
            writeJSONtoResponse(json, response);
		} catch (Exception e) {
		    e.printStackTrace(); // TODO remove this line when go to production mode.
			Debug.logError("Problems with jqxEventProcessor: " + e.toString(), module);
			Map<String, Object> mapError = new HashMap<String, Object>();
			mapError.put("responseMessage", "error");
			mapError.put("errorMessage", e.toString());
			mapError.put("TotalRows", "0");
			JSONObject json = JSONObject.fromObject(mapError);
			writeJSONtoResponse(json, response);
		}
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
