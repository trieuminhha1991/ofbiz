package com.olbius.docmanager.event;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;

import com.olbius.util.CategoryUtil;
import com.olbius.util.JsonUtil;

public class DocumentMrgEvent {
	public static void getDataCategoryChild(HttpServletRequest request, HttpServletResponse response){
		List<Map<String, Object>> jsonList = FastList.newInstance();
		
		//Get request parameters
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        String dataCategoryId = request.getParameter("dataCategoryId");
        String onclickFunction = request.getParameter("onclickFunction");
        
        //Get Child
        List<GenericValue> dataCategoryList = FastList.newInstance();
        List<GenericValue> dataResourceList = FastList.newInstance();
        dataCategoryList = CategoryUtil.getCategoryChild(dataCategoryId, delegator);
        dataResourceList = CategoryUtil.getDataReSource(dataCategoryId, delegator);
        if(UtilValidate.isNotEmpty(dataCategoryList)){
        	for(GenericValue item: dataCategoryList){
        		Map<String, Object> attrMap = FastMap.newInstance();
        		Map<String, Object> dataMap = FastMap.newInstance();
        		Map<String, Object> jsonMap = FastMap.newInstance();
        		
        		//Set Attribute
        		attrMap.put("id", item.getString("dataCategoryId"));
        		attrMap.put("rel", "E");
        		
        		//Set Data
        		dataMap.put("title", item.getString("categoryName"));
        		
        		//Check hasChild
        		if(CategoryUtil.hasChild(item.getString("dataCategoryId"), delegator)){
        			jsonMap.put("state", "closed");
        		}
        		
        		//Set Json
        		jsonMap.put("attr", attrMap);
        		jsonMap.put("data", dataMap);
        		
        		jsonList.add(jsonMap);
         	}
        }
        
        if(UtilValidate.isNotEmpty(dataResourceList)){
        	for(GenericValue item: dataResourceList){
        		Map<String, Object> attrMap = FastMap.newInstance();
        		Map<String, Object> dataMap = FastMap.newInstance();
        		Map<String, Object> jsonMap = FastMap.newInstance();
        		Map<String, Object> dataAttrMap = FastMap.newInstance();
        		
        		//Set Attribute
        		String dataResourceId = item.getString("dataResourceId");
        		attrMap.put("id", dataResourceId);
        		try {
					GenericValue data = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
					String path = "https://localhost:8443" + data.getString("objectInfo");
					attrMap.put("path", path);
        		} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
        		attrMap.put("rel", "L");
        		//Set Data
        		dataMap.put("title", item.getString("dataResourceName"));
        		dataAttrMap.put("onClick", onclickFunction + "('" + item.getString("dataResourceId") + "')");
        		dataMap.put("attr", dataAttrMap);
        		//Set Json
        		jsonMap.put("attr", attrMap);
        		jsonMap.put("data", dataMap);
        		
        		jsonList.add(jsonMap);
         	}
        }
        
        JsonUtil.toJsonObjectList(jsonList, response);
	}
}
