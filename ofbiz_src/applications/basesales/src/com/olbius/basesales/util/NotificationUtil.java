package com.olbius.basesales.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class NotificationUtil {
	public static final String module = NotificationUtil.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    /**
     * Create a notification going to send to parties who has id in "partyIds" list
     * @param dispatcher
     * @param locale
     * @param partyIds
     * @param header
     * @param state
     * @param action
     * @param targetLink
     * @param ntfType
     * @param sendToGroup
     * @param sendrecursive
     * @param dateTime
     * @param userLogin
     * @param otherParams: openTime
     * @return
     * @throws GenericServiceException
     */
    public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, List<String> partyIds, 
			String header, String state, String action, String targetLink, String ntfType, String sendToGroup, String sendrecursive, 
			Timestamp dateTime, GenericValue userLogin, Map<String, Object> otherParams) throws GenericServiceException{
		if (UtilValidate.isEmpty(partyIds)) return ServiceUtil.returnSuccess();
		
		Map<String, Object> contextMap = UtilMisc.<String, Object>toMap("partiesList", partyIds, 
					"header", header, 
					"state", state, 
					"action", action, 
					"targetLink", targetLink, 
					"dateTime", dateTime, 
					"ntfType", ntfType, 
					"sendToGroup", sendToGroup, 
					"sendrecursive", sendrecursive, 
					"userLogin", userLogin);
		if (UtilValidate.isNotEmpty(otherParams)) {
			if (otherParams.containsKey("openTime")) contextMap.put("openTime", otherParams.get("openTime"));
		}
 		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", contextMap);
 		if (ServiceUtil.isError(tmpResult)) {
 			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
 		}
 		return ServiceUtil.returnSuccess();
	}
    
    public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, List<String> partyIds, 
			String header, String state, String action, String targetLink, String ntfType, String sendToGroup, String sendrecursive, 
			Timestamp dateTime, GenericValue userLogin) throws GenericServiceException{
		return sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, dateTime, userLogin, null);
	}
    
    public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, String partyId, 
			String header, String state, String action, String targetLink, String ntfType, String sendToGroup, String sendrecursive, 
			Timestamp dateTime, GenericValue userLogin) throws GenericServiceException{
		if (UtilValidate.isEmpty(partyId)) return ServiceUtil.returnSuccess();
 		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
 				UtilMisc.<String, Object>toMap("partyId", partyId, 
 						"header", header, 
 						"state", state, 
 						"action", action, 
 						"targetLink", targetLink, 
 						"dateTime", dateTime, 
 						"ntfType", ntfType, 
 						"sendToGroup", sendToGroup, 
 						"sendrecursive", sendrecursive, 
 						"userLogin", userLogin)
 				);
 		if (ServiceUtil.isError(tmpResult)) {
 			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
 		}
 		return ServiceUtil.returnSuccess();
	}
}
