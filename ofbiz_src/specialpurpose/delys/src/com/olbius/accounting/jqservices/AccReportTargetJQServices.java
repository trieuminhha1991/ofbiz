package com.olbius.accounting.jqservices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class AccReportTargetJQServices {
	public static final String module = AccReportTargetJQServices.class.getName();
	public static Map<String, Object> jqListAccReportTarget(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String[] reportTypeIds = parameters.get("reportTypeId");
    	String reportTypeId = null;
    	if(UtilValidate.isNotEmpty(reportTypeIds)){
    		reportTypeId = reportTypeIds[0];
    	}else{
    	
    		List<GenericValue> listReportType = delegator.findList("AccReportType", null, null, null, null, false);
    		if(UtilValidate.isNotEmpty(listReportType) && listReportType.size() > 0){
    			GenericValue reportType = listReportType.get(0);
    			reportTypeId = reportType.getString("reportTypeId");
    		}
    	}
    	List<GenericValue> listAccReports = FastList.newInstance();
    	EntityCondition accReportCond = EntityCondition.makeCondition("reportTypeId", EntityOperator.EQUALS, reportTypeId);
    	listAccReports = delegator.findList("AccReport", accReportCond, null, null, null, false);
    	List<String> accReportIds = FastList.newInstance();
    	if(UtilValidate.isNotEmpty(listAccReports)){
    		for (GenericValue accreport : listAccReports) {
				String accreportId = accreport.getString("reportId");
				accReportIds.add(accreportId);
			}
    	}
    	EntityCondition tmpCond  = null;
    	if(UtilValidate.isNotEmpty(accReportIds)){
    		tmpCond =  EntityCondition.makeCondition("reportId", EntityOperator.IN, accReportIds);
    	}
    	if(UtilValidate.isNotEmpty(tmpCond)){
    		listAllConditions.add(tmpCond);
    	}
    	
    	EntityCondition cond = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		
    		listIterator = delegator.find("AccReportTarget", cond, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListAccReportTarget service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqListAccReportFunction(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	EntityCondition tmpCond  = null;
    	
    	if(UtilValidate.isNotEmpty(tmpCond)){
    		listAllConditions.add(tmpCond);
    	}
    	
    	EntityCondition cond = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		
    		listIterator = delegator.find("AccReportFunction", cond, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListAccReportFunction service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static String saveTargetReport(HttpServletRequest request, HttpServletResponse reponse){
		Delegator delegator =  (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String params = request.getParameter("accTargetReports");
        String reportTypeId = request.getParameter("reportTypeId");
        String organizationPartyId = request.getParameter("organizationPartyId");
        JSONArray accTargetReports = JSONArray.fromObject(params);		
		if(UtilValidate.isNotEmpty(accTargetReports)){
			for (int i = 0; i < accTargetReports.size(); i++) {
				JSONObject accTarget = accTargetReports.getJSONObject(i);
				if(UtilValidate.isNotEmpty(accTarget)){
					Map<String, Object> accTargetServiceMap = new HashMap<String, Object>();
					if(UtilValidate.isNotEmpty(accTarget.getString("code")) && !accTarget.getString("code").equalsIgnoreCase("null")){
						accTargetServiceMap.put("code", accTarget.getString("code"));
					}else{
						accTargetServiceMap.put("code", null);
					}
					if(UtilValidate.isNotEmpty(accTarget.getString("parentTargetId")) && !accTarget.getString("parentTargetId").equalsIgnoreCase("null")){
						accTargetServiceMap.put("parentTargetId", accTarget.getString("parentTargetId"));
					}else{
						accTargetServiceMap.put("parentTargetId", null);
					}
					if(UtilValidate.isNotEmpty(accTarget.getString("name")) && !accTarget.getString("name").equalsIgnoreCase("null")){
						accTargetServiceMap.put("name", accTarget.getString("name"));
					}else{
						accTargetServiceMap.put("name", null);
					}
					if(UtilValidate.isNotEmpty(accTarget.getString("formula")) && !accTarget.getString("formula").equalsIgnoreCase("null")){
						String formular = accTarget.getString("formula");
						if(formular.contains("add")){
							formular =  formular.replace("add", "+");
						}
						if(formular.contains("minus")){
							formular = formular.replace("minus", "-");
						}
						accTargetServiceMap.put("formula", formular);
					}else{
						accTargetServiceMap.put("formula", null);
					}
					if(UtilValidate.isNotEmpty(accTarget.getString("demonstration"))&& !accTarget.getString("demonstration").equalsIgnoreCase("null")){
						accTargetServiceMap.put("demonstration", accTarget.getString("demonstration"));
					}else{
						accTargetServiceMap.put("demonstration", null);
					}
					if(UtilValidate.isNotEmpty(accTarget.getString("description")) && !accTarget.getString("description").equalsIgnoreCase("null")){
						accTargetServiceMap.put("description", accTarget.getString("description"));
					}else{
						accTargetServiceMap.put("description", null);
					}
					
					if(UtilValidate.isNotEmpty(accTarget.getString("displaySign")) && !accTarget.getString("displaySign").equalsIgnoreCase("null") ){
						accTargetServiceMap.put("displaySign", accTarget.getString("displaySign"));
					}else{
						accTargetServiceMap.put("displaySign", null);
					}
					
					if(UtilValidate.isNotEmpty(accTarget.getString("displayStyle")) && !accTarget.getString("displayStyle").equalsIgnoreCase("null")){
						accTargetServiceMap.put("displayStyle", accTarget.getString("displayStyle"));
					}else{
						accTargetServiceMap.put("displayStyle", null);
					}
					
					if(UtilValidate.isNotEmpty(accTarget.getString("orderIndex")) && !accTarget.getString("orderIndex").equalsIgnoreCase("null")){
						Long orderIndex = Long.valueOf(accTarget.getString("orderIndex"));
						accTargetServiceMap.put("orderIndex", orderIndex);
					}else{
						accTargetServiceMap.put("orderIndex", null);
					}
					
					if(UtilValidate.isNotEmpty(accTarget.getString("unionSign")) && !accTarget.getString("unionSign").equalsIgnoreCase("null")){
						accTargetServiceMap.put("unionSign", accTarget.get("unionSign"));
					}else{
						accTargetServiceMap.put("unionSign", null);
					}
					
					accTargetServiceMap.put("userLogin", userLogin);
					if(UtilValidate.isEmpty(accTarget.getString("targetId")) || accTarget.getString("targetId").equalsIgnoreCase("null")){
						if(UtilValidate.isNotEmpty(reportTypeId)){
							accTargetServiceMap.put("reportTypeId", reportTypeId);
						}
						if(UtilValidate.isNotEmpty(organizationPartyId)){
							accTargetServiceMap.put("partyId", organizationPartyId);
						}
						try {
							dispatcher.runSync("createAccReportTarget", accTargetServiceMap);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else{
						accTargetServiceMap.put("targetId", accTarget.getString("targetId"));
						accTargetServiceMap.put("reportId", accTarget.getString("reportId"));
						try {
							dispatcher.runSync("updateAccReportTarget", accTargetServiceMap);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return "success";
	}
	
}
