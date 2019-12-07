package com.olbius.baselogistics.util;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtil {
	public static String convertListMapToJSON(List<Map<String, Object>> list) {       
   	    JSONArray json_arr=new JSONArray();
   	    for (Map<String, Object> map : list) {
   	        JSONObject json_obj=new JSONObject();
   	        for (Map.Entry<String, Object> entry : map.entrySet()) {
   	            String key = entry.getKey();
   	            Object value = entry.getValue();
                json_obj.put(key,value);
   	        }
   	        json_arr.add(json_obj);
   	    }
   	    return json_arr.toString();
   	}
}
