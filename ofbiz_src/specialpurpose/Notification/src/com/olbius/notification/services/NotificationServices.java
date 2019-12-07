package com.olbius.notification.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class NotificationServices {
	
	public static final String module = NotificationServices.class.getName();
    public static final String resource = "notificationUiLabels";
    public static final String resourceError = "notificationErrorUiLabels";
	/*
     * Description: get all notification for user with id=partyId
     * */
    public static Map<String, Object> listAllNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listNofification = null;
    	String strPartyId = (String)context.get("partyId");
    	try {
    		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
        	listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", strPartyId)));
        	listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("openTime",EntityJoinOperator.GREATER_THAN_EQUAL_TO, new Timestamp((new Date()).getTime()))));
    		listNofification = delegator.findList("Notification", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), UtilMisc.toSet("ntfId","targetLink","dateTime","header","action","ntfType"), UtilMisc.toList("-dateTime"), null, false);
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	result.put("listntf", listNofification); 
    	return result;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> createNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = ctx.getDelegator();
    	String strPartyId = (String)context.get("partyId");
    	String strNtfType = (String)context.get("ntfType");
    	List<String> partiesList = (List<String>)context.get("partiesList");
    	String strTargetLink = (String)context.get("targetLink");
    	String strAction = (String)context.get("action");
    	String strRoleTypeId = (String)context.get("roleTypeId");
    	String strSendToGroup = (String)context.get("sendToGroup");
    	String strSendrecursive = (String)context.get("sendrecursive");
    	String strSendToSender = (String)context.get("sendToSender");
    	List<String> listRole = (List<String>)context.get("roleList");
    	Timestamp tsDateTime = (Timestamp)context.get("dateTime");
    	if(tsDateTime == null){
    		tsDateTime = new Timestamp(new Date().getTime());
    	}
    	Timestamp tsOpenTime = (Timestamp)context.get("openTime");
    	String strHeader = (String)context.get("header");
    	//String strState = (String)context.get("state"); --> Status = open by default
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String strSenderId = (String)userLogin.get("partyId");
    	try{
    		List<String> listReceiver = new ArrayList<String>();
    		// roleTypePartyId
    		if(!"".equals(strRoleTypeId)){
    			listReceiver.addAll(getListPartiesByRoleTypeId(delegator, strRoleTypeId));
    		}
    		// List of roleTypeId
    		if(listRole != null){
    			for (String string : listRole) {
        			listReceiver.addAll(getListPartiesByRoleTypeId(delegator, string));
    			}
    		}
    		// strPartyId
    		if(strPartyId != null){
    			if(partyIsGroup(delegator, strPartyId)){
    				
    				listReceiver.addAll(getListPartiesByPartyId(delegator, strPartyId, strSendrecursive));
    			} else {
					listReceiver.add(strPartyId);
				}
    		}
    		// List of parties
    		if(partiesList != null){
    			for (String string : partiesList) {
    				if(partyIsGroup(delegator, string)){
						listReceiver.addAll(getListPartiesByPartyId(delegator, string, strSendrecursive));
    				} else {
    					listReceiver.add(string);
    				}
				}
    		}
    		// send to partyGroup
    		if("Y".equals(strSendToGroup)){
    			if(strPartyId != null){
    				listReceiver.add(strPartyId);
    			}
    			if(partiesList != null){
    				listReceiver.addAll(partiesList);
    			}
    		}
    		// remove duplicate values
    		HashSet hs = new HashSet();
    		hs.addAll(listReceiver);
    		listReceiver.clear();
    		listReceiver.addAll(hs);
    		// create notification for all parties
    		for (String partyId : listReceiver) {
    			if(strSenderId.equals(partyId) && "N".equals(strSendToSender)){
    				continue;
    			}
    			// check party has user login only
    			if (!partyHasUserLogin(delegator, partyId)) {
    				continue;
    			}
    			//comment by hoanm
    			insertNotify(delegator, strTargetLink, strAction, strHeader, partyId, tsOpenTime, tsDateTime, strSenderId, strNtfType);
			}
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		result.put(ModelService.SUCCESS_MESSAGE, 
            UtilProperties.getMessage(resourceError, "createSuccessfully", locale));
    	} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	return result;
    }
    private static boolean partyHasUserLogin(Delegator delegator, String strPartyId){
    	try {
    		GenericValue tmpGV = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", strPartyId), null, false));
    		if(tmpGV != null){
				return true;
    		}
    	} catch(Exception ex) {
    		Debug.logWarning("Party " + strPartyId + " not have any user login", module);
    		return false;
    	}
    	return false;
    }
    private static boolean partyIsGroup(Delegator delegator, String strPartyId){
    	try{
    		Map<String, String> tmpMap = new HashMap<String, String>();
    		tmpMap.put("partyId", strPartyId);
    		GenericValue tmpGV = delegator.findOne("Party", tmpMap, false);
    		if(tmpGV != null){
    			if("PARTY_GROUP".equals(tmpGV.getString("partyTypeId"))){
    				return true;
    			}
    		}
    	}catch(Exception ex){
    		return false;
    	}
    	return false;
    }
    private static List<String> getListPartiesByRoleTypeId(Delegator delegator, String strRoleTypeId) throws GenericEntityException{
    	Set<String> tmpSet = new HashSet<String>();
		tmpSet.add("partyId");
		List<String> returnValue = new ArrayList<String>();
		List<GenericValue> listEmployeeRole = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId", strRoleTypeId), tmpSet, null, null, false);
		for(int i = 0; i < listEmployeeRole.size();i++){
			returnValue.add(listEmployeeRole.get(i).getString("partyId"));
		}
		return returnValue;
    }
    private static List<String> getListPartiesByPartyId(Delegator delegator, String strPartyId, String strSendrecursive) throws GenericEntityException{
    	List<String> returnValue = new ArrayList<String>();
    	GenericValue partyTmp = delegator.makeValue("Party",
                UtilMisc.toMap("partyId", strPartyId));
		partyTmp = delegator.findOne(partyTmp.getEntityName(), partyTmp, false);
		if(((String)partyTmp.get("partyTypeId")).equals("PARTY_GROUP")){
			Organization composite = PartyUtil.buildOrg(delegator, strPartyId, false);
			List<GenericValue> listEmployee;
			if("Y".equals(strSendrecursive)){
				listEmployee = composite.getEmployeeInOrg(delegator);
			}else{
				listEmployee = composite.getDirectChildList(delegator);
			}
			if (listEmployee != null) {
				for(int i = 0; i < listEmployee.size();i++){
					returnValue.add(listEmployee.get(i).getString("partyId"));
				}
			}
		}else{
			returnValue.add(strPartyId);
		}
    	return returnValue;
    }
    private static void insertNotify(Delegator delegator, String strTargetLink, String strAction, String strHeader, String strPartyId, Timestamp tsOpenTime, Timestamp tsDateTime, String strSenderId, String strNtfType) throws GenericEntityException{
    	String strNextId = delegator.getNextSeqId("Notification");
    	if(strNtfType == null || strNtfType.isEmpty()){
    		strNtfType = "MANY";
    	}
    	strNtfType = strNtfType.toUpperCase();
		GenericValue tmpNtf = delegator.makeValue("Notification", 
				UtilMisc.toMap("ntfId", strNextId, 
				"targetLink", strTargetLink,
				"action", strAction,
				"ntfType", strNtfType,
				"header", strHeader,
				"state", "open",
				"partyId", strPartyId, 
				"openTime", tsOpenTime, 
				"senderId", strSenderId, 
				"dateTime", tsDateTime));
		tmpNtf.create();
    }
    public static Map<String, Object> updateNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = ctx.getDelegator();
    	//String strPartyId = (String)context.get("partyId");
    	//String strTargetLink = (String)context.get("targetLink");
    	//String strDateTime = (String)context.get("dateTime");
    	//String strHeader = (String)context.get("header");
    	//String strState = (String)context.get("state");
    	String strNextId = (String)context.get("ntfId");
    	try{
    		GenericValue tmpNtf = delegator.findOne("Notification", UtilMisc.toMap("ntfId", strNextId),false);
//    		tmpNtf.put("targetLink", strTargetLink);
//    		tmpNtf.put("header", strHeader);
    		tmpNtf.put("state", "close");
//    		tmpNtf.put("partyId", strPartyId);
//    		tmpNtf.put("dateTime", strDateTime);
    		tmpNtf.store();
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		/*result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceError, "updateSuccessfully", locale));*/
    	} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	return result;
    }
    public static Map<String, Object> closeAllNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue user = (GenericValue) context.get("userLogin");
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
    	listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", user.getString("partyId"))));
    	listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("state", "open")));
    	try {
			int i = delegator.storeByCondition("Notification", UtilMisc.toMap("state", "close"), EntityCondition.makeCondition(listCond, EntityJoinOperator.AND));
			result.put("numberOfNotify", i);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	return result;
    }
    
    public static Map<String, Object> deleteNotification(DispatchContext ctx, Map<String, ? extends Object> context) throws Exception {
    	Delegator delegator = ctx.getDelegator();
    	
		Timestamp timestamp = new Timestamp(System.currentTimeMillis()); 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_WEEK, -30);
		Timestamp newTimestamp = new Timestamp(calendar.getTime().getTime());
		
		int size = 500;
		boolean isContinue = true;
		EntityFindOptions opts = new EntityFindOptions();
		opts.setLimit(size);
		int maxNotiDeleteTime = 1599;
		int countDeleteTime = 0;

		while(isContinue) {
			List<GenericValue> listNotifications = null;
			try {
	    		listNotifications = delegator.findList("Notification", EntityCondition.makeCondition("dateTime", EntityOperator.LESS_THAN, newTimestamp), null, null, opts, false);
	    		if (UtilValidate.isNotEmpty(listNotifications)) {
					if (listNotifications.size() < size || countDeleteTime >= maxNotiDeleteTime) {
						isContinue = false;
					}
					for (GenericValue notification : listNotifications) {
						delegator.removeValue(notification);
					}
	    		}
	    		else {
	    			isContinue = false;
	    		}			
			} catch (GeneralException e) {
				Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
			} 
			countDeleteTime++;
		}
    	
    	return ServiceUtil.returnSuccess();
    }
}
