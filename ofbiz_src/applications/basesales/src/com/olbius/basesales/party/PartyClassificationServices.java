package com.olbius.basesales.party;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

public class PartyClassificationServices {
	public static final String module = PartyClassificationServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPartyClassificationGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listIterator = delegator.find("PartyClassificationGroup", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyClassificationGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    } 
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPartyClassificationGroupParties(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition condition= EntityCondition.makeCondition(UtilMisc.toMap("partyClassificationGroupId", parameters.get("partyClassificationGroupId")[0]));
    	listAllConditions.add(condition);
    	try {
			listIterator = delegator.find("PartyClassificationParty", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyClassificationGroupParties service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListParty(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED");
    	listAllConditions.add(condition);
    	try {
			listIterator = delegator.find("PartyNameView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyClassificationGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLoyaltyCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
    	EntityCondition condition= EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", organizationPartyId));
    	listAllConditions.add(condition);
    	try {
    		listIterator = delegator.find("LoyaltyCustomer", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListLoyaltyCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLoyaltyCustomerDetail(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String loyaltyPointId = SalesUtil.getParameter(parameters, "loyaltyPointId");
    		String partyId = SalesUtil.getParameter(parameters, "partyId");
        	if (loyaltyPointId != null) listAllConditions.add(EntityCondition.makeCondition("loyaltyPointId", loyaltyPointId));
        	if (partyId != null) listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
        	// LoyaltyPointDetail
        	if (UtilValidate.isEmpty(listSortFields)) {
        		listSortFields.add("-effectiveDate");
        	}
    		listIterator = delegator.find("LoyaltyPointAndItems", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListLoyaltyCustomerDetail service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static String createPartyClassificationGroup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	HttpSession session = request.getSession(true);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Security security = (Security) request.getAttribute("security");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_NEW")){
    		String partyClassificationGroupId= (String) request.getParameter("partyClassificationGroupId");
    		String partyClassificationTypeId= (String) request.getParameter("partyClassificationTypeId");
    		String parentGroupId= (String) request.getParameter("parentGroupId");
    		String description= (String) request.getParameter("description");
    		GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
    		mapContext.put("partyClassificationTypeId", partyClassificationTypeId);
    		mapContext.put("parentGroupId", parentGroupId);
    		mapContext.put("description", description);
    		mapContext.put("userLogin", userLoginS);
    		
    		GenericValue partyClassificationGroup = delegator.findOne("PartyClassificationGroup", UtilMisc.toMap("partyClassificationGroupId", partyClassificationGroupId), false);
    		try {
    			if (UtilValidate.isEmpty(partyClassificationGroup)){
    				dispatcher.runSync("createPartyClassificationGroup", mapContext);
    			} else {
    				String errorMessage = UtilProperties.getMessage(resource_error, "BSIdExisted", UtilHttp.getLocale(request));
    				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
    				return "error";
    			}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", UtilHttp.getLocale(request));
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	return "success";
    }
    
    public static String updatePartyClassificationGroup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	HttpSession session = request.getSession(true);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Security security = (Security) request.getAttribute("security");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_EDIT")){
    		String partyClassificationGroupId= (String) request.getParameter("partyClassificationGroupId");
    		String partyClassificationTypeId= (String) request.getParameter("partyClassificationTypeId");
    		String parentGroupId= (String) request.getParameter("parentGroupId");
    		String description= (String) request.getParameter("description");
    		GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
    		mapContext.put("partyClassificationTypeId", partyClassificationTypeId);
    		mapContext.put("parentGroupId", parentGroupId);
    		mapContext.put("description", description);
    		mapContext.put("userLogin", userLoginS);
    		try {
    			dispatcher.runSync("updatePartyClassificationGroup", mapContext);
    		} catch (GenericServiceException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", UtilHttp.getLocale(request));
    		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
    		return "error";
    	}
    	return "success";
    }
    
    public static String deletePartyClassificationGroup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	HttpSession session = request.getSession(true);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Security security = (Security) request.getAttribute("security");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_DELETE")){
    		String partyClassificationGroupId= (String) request.getParameter("partyClassificationGroupId");
    		GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
    		mapContext.put("userLogin", userLoginS);
    		List<GenericValue> listPartyClassification = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyClassificationGroupId", partyClassificationGroupId), null, false);
    		try {
    			if (UtilValidate.isEmpty(listPartyClassification)){
    				dispatcher.runSync("deletePartyClassificationGroup", mapContext);
    			} else {
    				String errorMessage = UtilProperties.getMessage(resource_error, "BSCanNotDeletePartyClassificationGroup", UtilMisc.toMap("partyClassificationGroupId", partyClassificationGroupId),UtilHttp.getLocale(request));
    				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
    				return "error";
    			}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BSYouHavenotDeletePermission", UtilHttp.getLocale(request));
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	return "success";
    }
    
    public static Map<String, Object> updatePartyClassification(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Security security = dctx.getSecurity();
        if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_EDIT")){
        	GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
            String partyClassificationGroupId = (String) context.get("partyClassificationGroupId");
            String partyId = (String) context.get("partyId");
            Timestamp fromDate = (Timestamp) context.get("fromDate");
            Timestamp thruDate = (Timestamp) context.get("thruDate");
            Map<String, Object> mapContext = new HashMap<String, Object>();
            mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
            mapContext.put("partyId", partyId);
            mapContext.put("fromDate", fromDate);
            mapContext.put("thruDate", thruDate);
            mapContext.put("userLogin", userLoginS);
            try {
    			dispatcher.runSync("updatePartyClassification", mapContext);
    		} catch (GenericServiceException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } else {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
        }
        
		return ServiceUtil.returnSuccess();
    }
    
    public static String deletePartyClassification(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	HttpSession session = request.getSession(true);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Security security = (Security) request.getAttribute("security");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_DELETE")){
    		String partyClassificationGroupId= (String) request.getParameter("partyClassificationGroupId");
    		String partyId= (String) request.getParameter("partyId");
    		GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
    		String fromDateStr = (String) request.getParameter("fromDate");
            Timestamp fromDate = null;
            if (UtilValidate.isNotEmpty(fromDateStr)) {
            	Long fromDateL = Long.parseLong(fromDateStr);
            	fromDate = new Timestamp(fromDateL);
            }
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
    		mapContext.put("partyId", partyId);
    		mapContext.put("fromDate", fromDate);
    		mapContext.put("userLogin", userLoginS);
    		try {
				dispatcher.runSync("deletePartyClassification", mapContext);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BSYouHavenotDeletePermission", UtilHttp.getLocale(request));
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	return "success";
    }
    
    public static String createPartyClassification(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	HttpSession session = request.getSession(true);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Security security = (Security) request.getAttribute("security");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_PTYCLASSIFI_NEW")){
    		String partyClassificationGroupId= (String) request.getParameter("partyClassificationGroupId");
    		String partyId= (String) request.getParameter("partyId");
    		String fromDateStr = (String) request.getParameter("fromDate");
            Timestamp fromDate = null;
            if (UtilValidate.isNotEmpty(fromDateStr)) {
            	Long fromDateL = Long.parseLong(fromDateStr);
            	fromDate = new Timestamp(fromDateL);
            }
            String thruDateStr = (String) request.getParameter("thruDate");
            Timestamp thruDate = null;
            if (UtilValidate.isNotEmpty(thruDateStr)) {
            	Long thruDateL = Long.parseLong(thruDateStr);
            	thruDate = new Timestamp(thruDateL);
            }
    		GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", partyClassificationGroupId);
    		mapContext.put("partyId", partyId);
    		mapContext.put("fromDate", fromDate);
    		mapContext.put("thruDate", thruDate);
    		mapContext.put("userLogin", userLoginS);
    		try {
				dispatcher.runSync("createPartyClassification", mapContext);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", UtilHttp.getLocale(request));
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	return "success";
    }
    
}
