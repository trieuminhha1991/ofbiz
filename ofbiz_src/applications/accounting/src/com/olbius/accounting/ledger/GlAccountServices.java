package com.olbius.accounting.ledger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class GlAccountServices {
	 public static final String module = GlAccountServices.class.getName();
	@SuppressWarnings("unchecked")
    public static void getChildAccountsTree(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String glAccountId = request.getParameter("glAccountId");
        String onclickFunction = request.getParameter("onclickFunction");
        String additionParam = request.getParameter("additionParam");
        String hrefString = request.getParameter("hrefString");
        String hrefString2 = request.getParameter("hrefString2");
        String entityName = "GlAccount";
        String primaryKeyName = "glAccountId";
        
        List<Map<Object, Object>> accountList = FastList.newInstance();
        List<GenericValue> childAccounts;
        List<String> sortList = org.ofbiz.base.util.UtilMisc.toList("accountCode");
        
        try {
            GenericValue account = delegator.findOne(entityName ,UtilMisc.toMap(primaryKeyName, glAccountId), false);
            if (UtilValidate.isNotEmpty(account)) {
            	childAccounts = delegator.findByAnd(entityName, UtilMisc.toMap("parentGlAccountId", glAccountId), null, false);
                if (UtilValidate.isNotEmpty(childAccounts)) {
                    for (GenericValue childAccount : childAccounts ) {
                        
                        Object accountId = null;
                        
                        accountId = childAccount.get(primaryKeyName);
                        
                        Map<Object,Object> josonMap = FastMap.newInstance();
                        List<GenericValue> childList = null;
                        
                        // Get the child list of chosen account
                        childList = delegator.findByAnd(entityName, UtilMisc.toMap("parentGlAccountId", accountId), null, false);
                        
                        // Get the chosen category information for the categoryContentWrapper
                        GenericValue acc = delegator.findOne(entityName ,UtilMisc.toMap(primaryKeyName,accountId), false);
                        
                        // If chosen category's child exists, then put the arrow before category icon
                        if (UtilValidate.isNotEmpty(childList)) {
                            josonMap.put("state", "closed");
                        }
                        Map dataMap = FastMap.newInstance();
                        Map dataAttrMap = FastMap.newInstance();
                        
                        String title = null;
                        String accName = (String) acc.get("accountName");
                        String accCode = (String) acc.get("accountCode");
                        String accTaxForm = (String) acc.get("glTaxFormId");
                        if (UtilValidate.isNotEmpty(accName) && UtilValidate.isNotEmpty(accTaxForm)) {
                            title = accCode + " " + accName + " " + accTaxForm;
                            dataMap.put("title", title);
                        } else if (UtilValidate.isNotEmpty(accName))
                        {
                        	 title = accCode + " " + accName;
                        	 dataMap.put("title", title);
                        }
                        else
                        {
                            title = accCode;
                            dataMap.put("title", title);
                        }
                        dataAttrMap.put("onClick", onclickFunction + "('" + accountId + additionParam + "')");
                        
                        String hrefStr = hrefString + accountId;
                        if (UtilValidate.isNotEmpty(hrefString2)) {
                            hrefStr = hrefStr + hrefString2;
                        }
                        dataAttrMap.put("href", hrefStr);
                        
                        dataMap.put("attr", dataAttrMap);
                        josonMap.put("data", dataMap);
                        Map attrMap = FastMap.newInstance();
                        attrMap.put("id", accountId);
                        attrMap.put("rel", "CATEGORY");
                        josonMap.put("attr",attrMap);
                        josonMap.put("title",title);
                        
                        accountList.add(josonMap);
                    }
                    List<Map<Object, Object>> sortedCategoryList = UtilMisc.sortMaps(accountList, sortList);
                    toJsonObjectList(sortedCategoryList,response);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
	
	@SuppressWarnings("unchecked")
    public static void toJsonObjectList(List attrList, HttpServletResponse response){
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Object attrMap : attrList) {
            JSONObject json = JSONObject.fromObject(attrMap);
            jsonBuilder.append(json.toString());
            jsonBuilder.append(',');
        }
        jsonBuilder.append("{ } ]");
        String jsonStr = jsonBuilder.toString();
        if (UtilValidate.isEmpty(jsonStr)) {
            Debug.logError("JSON Object was empty; fatal error!",module);
        }
        // set the X-JSON content type
        response.setContentType("application/json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding",module);
        }
        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError("Unable to get response writer",module);
        }
    }

}
