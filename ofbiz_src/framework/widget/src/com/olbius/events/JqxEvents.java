package com.olbius.events;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.json.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

public class JqxEvents {
	public static final String module = JqxEvents.class.getName();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String jqxEventProcessor(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator((String) request.getSession().getAttribute("delegatorName"));
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceContainer.getLocalDispatcher(null, delegator);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        TimeZone timeZone = (TimeZone) request.getSession().getAttribute("timeZone");
        Map<String, String[]> params = request.getParameterMap();
        Locale locale = UtilHttp.getLocale(request);
        Map<String,Object> context = new HashMap<String,Object>();
        context.put("parameters", params);
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        if(params.containsKey("hasrequest")){
        	context.put("request", request); 
        }
        JqAction jqAction = JqAction.valueOf((params.get("jqaction") != null?((String)params.get("jqaction")[0]):("S")));
        try {
        	Map<String,Object> results = null;
        	switch (jqAction) {
			case S:{
				results = dispatcher.runSync("jqxGridGeneralServicer", context);
				break;
			}	
			case C:{
				results = dispatcher.runSync("jqxGridGeneralServicerCreate", context);
				break;
			}
			case CL:{
				results = dispatcher.runSync("jqxGridGeneralServicerCreate", context);
				break;
			}
			case U:{
				results = dispatcher.runSync("jqxGridGeneralServicerUpdate", context);
				break;
			}
			case UL:{
				results = dispatcher.runSync("jqxGridGeneralServicerUpdateLocal", context);
				break;
			}
			case D:{
				results = dispatcher.runSync("jqxGridGeneralServicerDelete", context);
				break;
			}
			case DL:{
				results = dispatcher.runSync("jqxGridGeneralServicerDeleteLocal", context);
				break;
			}
			default:
				break;
			}
        	
        	if(params.containsKey("otherParams") && "S".equals(jqAction.toString())){
	        	String strOtherParam = (params.get("otherParams")[0]);
				if(!"".equals(strOtherParam)){
					List<Object> list = (List<Object>) results.get("results");
					List<Map<String,Object>> listTMP = new ArrayList<Map<String,Object>>();
					String[] fieldMethod = strOtherParam.split(";");
					for(int j = 0; j < list.size();j++){
						Map<String,Object> tmpMap = new HashMap<String,Object>();
						GenericValue tmpGeneric = (GenericValue)list.get(j);
						for(String str:tmpGeneric.getAllKeys()){
							tmpMap.put(str, tmpGeneric.get(str));
						}
						for(int i = 0; i < fieldMethod.length;i++){
							String[] fieldOperator = fieldMethod[i].split(":");
							String strFName = fieldOperator[0];
							if(fieldOperator[1].startsWith("S-")){
								String strSName = fieldOperator[1].substring(fieldOperator[1].indexOf("S-") + 2,fieldOperator[1].indexOf("("));
								String strOName = fieldOperator[1].substring(fieldOperator[1].indexOf("(") + 1,fieldOperator[1].indexOf(")"));
								String strResult = fieldOperator[1].substring(fieldOperator[1].indexOf("<") + 1,fieldOperator[1].indexOf(">"));
								// check for multi-input
								String[] strONameArr = strOName.split(",");
								Map<String, Object> tmp = new HashMap<String, Object>();
								for(int t = 0; t < strONameArr.length;t++){
									if(strONameArr[t].contains("{")){
										tmp.put(strONameArr[t].substring(0, strONameArr[t].indexOf("{")),tmpMap.get(strONameArr[t].substring(strONameArr[t].indexOf("{") + 1, strONameArr[t].indexOf("}"))));
									} else if(strONameArr[t].contains("*")){
										tmp.put(strONameArr[t].substring(0, strONameArr[t].indexOf("*")), strONameArr[t].substring(strONameArr[t].indexOf("*") + 1, strONameArr[t].length()));
									} else{
										tmp.put(strONameArr[t], tmpMap.get(strONameArr[t]));
									}
								}
								tmp.put("userLogin", userLogin);
								Map<String, Object> resultTMP;
								String[] strFNameArr = strFName.split(",");
								String[] strResultArr = strResult.split(",");
								try {
									resultTMP = dispatcher.runSync(strSName, tmp);
									for(int k = 0; k < strFNameArr.length;k++){
										tmpMap.put(strFNameArr[k], resultTMP.get(strResultArr[k]));
									}
								} catch(Exception ex){
									for(int k = 0; k < strFNameArr.length;k++){
										tmpMap.put(strFNameArr[k], "");
									}
								}
							}else if(fieldOperator[1].startsWith("SL-")){
								String strSName = fieldOperator[1].substring(fieldOperator[1].indexOf("SL-") + 3,fieldOperator[1].indexOf("("));
								String strOName = fieldOperator[1].substring(fieldOperator[1].indexOf("(") + 1,fieldOperator[1].indexOf(")"));
								String strResult = fieldOperator[1].substring(fieldOperator[1].indexOf("<") + 1,fieldOperator[1].indexOf(">"));
								// check for multi-input
								String[] strONameArr = strOName.split(",");
								Map<String, Object> tmp = new HashMap<String, Object>();
								for(int t = 0; t < strONameArr.length;t++){
									if(strONameArr[t].contains("{")){
										tmp.put(strONameArr[t].substring(0, strONameArr[t].indexOf("{")),tmpMap.get(strONameArr[t].substring(strONameArr[t].indexOf("{") + 1, strONameArr[t].indexOf("}"))));
									}else if(strONameArr[t].contains("*")){
										tmp.put(strONameArr[t].substring(0, strONameArr[t].indexOf("*")), strONameArr[t].substring(strONameArr[t].indexOf("*") + 1, strONameArr[t].length()));
									}else{
										tmp.put(strONameArr[t], tmpMap.get(strONameArr[t]));
									}
								}
								tmp.put("userLogin", userLogin);
								Map<String, Object> resultTMP = dispatcher.runSync(strSName, tmp);
								List<Object> listObject = (List<Object>) resultTMP.get(strResult);
								String[] arrFName = strFName.split(",", -1);
								if(listObject == null || listObject.isEmpty()){
									for(int t=0; t < arrFName.length;t++){
										tmpMap.put(arrFName[t],null);
									}
								}
								for(int t=0; t < arrFName.length;t++){
									String strLeft = arrFName[t];
									String strRight = arrFName[t];
									if(strLeft.contains("(")){
										strLeft = arrFName[t].substring(0, arrFName[t].indexOf("("));
										strRight = arrFName[t].substring(arrFName[t].indexOf("(") + 1, arrFName[t].length() - 1);
									}
									if(listObject.get(0) instanceof javolution.util.FastMap){
										tmpMap.put(strLeft,((javolution.util.FastMap)listObject.get(0)).get(strRight));
									}else if(listObject.get(0) instanceof GenericValue){
										tmpMap.put(strLeft,((GenericValue)listObject.get(0)).get(strRight));
									}
								}
							}else if(fieldOperator[1].startsWith("M-")){
								String strClassName = fieldOperator[1].substring(fieldOperator[1].indexOf("M-") + 2,fieldOperator[1].indexOf("("));
								String strMethodName = fieldOperator[1].substring(fieldOperator[1].indexOf("(") + 1,fieldOperator[1].indexOf(")"));
								String strInput = fieldOperator[1].substring(fieldOperator[1].indexOf("<") + 1,fieldOperator[1].indexOf(">"));
								Class cls = Class.forName(strClassName);
								//Object obj = cls.newInstance();
								//String parameter Delegator delegator, String partyId, boolean lastNameFirst
								Class[] paramsMethod = new Class[3];	
								paramsMethod[0] = Delegator.class;
								paramsMethod[1] = String.class;
								paramsMethod[2] = boolean.class;
								Method method = cls.getDeclaredMethod(strMethodName, paramsMethod);
								Object object = method.invoke(null, delegator,tmpMap.get(strInput),false);
								tmpMap.put(strFName, object);
							}
						}
						listTMP.add(tmpMap);
					}
					results.put("results", listTMP);
				}
			}
        	JSON json = JSON.from(results);
            writeJSONtoResponse(json, response);
		} catch (Exception e) {
			Debug.logError("Problems with jqxEventProcessor: " + e.toString(), module);
			Map<String, Object> mapError = new HashMap<String, Object>();
			mapError.put("responseMessage", "error");
			mapError.put("errorMessage", e.toString());
			mapError.put("TotalRows", "0");
			JSON json;
			try {
				json = JSON.from(mapError);
				writeJSONtoResponse(json, response);
			} catch (IOException e1) {
				Debug.logError(e1, module);
			}
			return "error";
		}
		return "success";
	}
	
	public static String toString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        df.setTimeZone( tz );
        String output = df.format( date );
        int inset0 = 9;
        int inset1 = 6;
        String s0 = output.substring( 0, output.length() - inset0 );
        String s1 = output.substring( output.length() - inset1, output.length() );
        String result = s0 + s1;
        result = result.replaceAll( "UTC", "+00:00" );
        return result;
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
enum JqAction{
	S,U,D,C,CL,UL,DL
}