package com.olbius.claims;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class EmplClaimService {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createEmplClaim(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		//String claimTypeId = (String)context.get("claimTypeId");
		String partyClaimSettlement = (String)context.get("partyClaimSettlement");
		List<Map<String,String>> observerIdList = (List<Map<String,String>>)context.get("observerIdList");
		String emplClaimIdTo = (String)context.get("emplClaimIdTo");
		Map<String,Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "EmplClaimCreatedSuccessful", locale));
		List<String> listObserverStr = FastList.newInstance();
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			String emplClaimId = delegator.getNextSeqId("EmplClaim");
			GenericValue emplClaim = delegator.makeValue("EmplClaim");			
			emplClaim.setNonPKFields(context);
			emplClaim.set("statusId", "EMPL_CLAIM_CREATED");
			emplClaim.set("partyId", userLogin.getString("partyId"));
			emplClaim.set("createdDate", UtilDateTime.nowTimestamp());
			emplClaim.set("emplClaimId", emplClaimId);
			emplClaim.create();
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyClaimSettlement, "roleTypeId", "CLAIM_SETTLEMENT", "userLogin", system));
			GenericValue emplClaimRoleType = delegator.makeValue("EmplClaimRoleType");
			emplClaimRoleType.set("emplClaimId", emplClaimId);
			emplClaimRoleType.set("partyId", partyClaimSettlement);
			emplClaimRoleType.set("roleTypeId", "CLAIM_SETTLEMENT");
			emplClaimRoleType.set("fromDate", UtilDateTime.nowTimestamp());
			emplClaimRoleType.create();
			retMap.put("emplClaimId", emplClaimId);
			if(emplClaimIdTo != null){
				GenericValue emplClaimAssoc = delegator.makeValue("EmplClaimAssoc");
				emplClaimAssoc.set("emplClaimId", emplClaimId);
				emplClaimAssoc.set("emplClaimIdTo", emplClaimIdTo);
				emplClaimAssoc.create();
			}
			if(observerIdList != null){
				for(int i  = 0 ;i < observerIdList.size();i ++){
					String index  = String.valueOf(i);
					listObserverStr.add(observerIdList.get(i).get("observer"+index));
					dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", observerIdList.get(i).get("observer"+index), "roleTypeId", "CLAIM_VIEWER", "userLogin", system));
					GenericValue emplClaimRoleTypeObserver = delegator.makeValue("EmplClaimRoleType");
					emplClaimRoleTypeObserver.set("emplClaimId", emplClaimId);
					emplClaimRoleTypeObserver.set("partyId", observerIdList.get(i).get("observer"+index));
					emplClaimRoleTypeObserver.set("roleTypeId", "CLAIM_VIEWER");
					emplClaimRoleTypeObserver.set("fromDate", UtilDateTime.nowTimestamp());
					emplClaimRoleTypeObserver.create();
				}	
			}
			//create notify
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("header", UtilProperties.getMessage("NotificationUiLabels", "EmplSendClaim", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false)), locale));
			ntfCtx.put("action", "EmplClaimApproval");
			ntfCtx.put("targetLink", "emplClaimId=" + emplClaimId);
			ntfCtx.put("state", "open");
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("partyId", partyClaimSettlement);
			ntfCtx.put("ntfType", "MANY");
			dispatcher.runSync("createNotification", ntfCtx);
			if(observerIdList != null){
				ntfCtx.put("partiesList", listObserverStr);
				ntfCtx.put("ntfType", "ONE");
				dispatcher.runSync("createNotification", ntfCtx);
			}
						
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEmplClaimStatus(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplClaimId = (String)context.get("emplClaimId");
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("emplClaimId", emplClaimId));
		conditions.add(EntityCondition.makeCondition("roleTypeId", "CLAIM_SETTLEMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		try {
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, (TimeZone)context.get("timeZone"), locale));
			GenericValue emplClaim = delegator.findOne("EmplClaim", UtilMisc.toMap("emplClaimId", emplClaimId), false);
			List<GenericValue> emplClaimRoleType = delegator.findList("EmplClaimRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplClaimRoleType)){
				emplClaim.set("statusId", statusId);
				emplClaim.set("comment", context.get("comment"));
				emplClaim.store();
				//notify to party
				if("EMPL_CLAIM_RESPONED".equals(statusId) || "EMPL_CLAIM_REJECTED".equals(statusId)){
					List<GenericValue> emplClaimList = delegator.findList("EmplClaimRoleType", EntityCondition.makeCondition(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()), EntityOperator.AND, EntityCondition.makeCondition("emplClaimId", emplClaimId)), null, null, null, false);
					List<String> ntfPartyList = FastList.newInstance();
					List<String> emplClaimPartyList = EntityUtil.getFieldListFromEntityList(emplClaimList, "partyId", true); 
					ntfPartyList.addAll(emplClaimPartyList);
					ntfPartyList.add(emplClaim.getString("partyId"));
					Map<String, Object> ntfCtx= FastMap.newInstance();
					ntfCtx.put("header", UtilProperties.getMessage("HrCommonUiLabels", "ClaimResponed", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, emplClaim.getString("partyId"), false)), locale));
					ntfCtx.put("action", "EmplClaimApproval");
					ntfCtx.put("partiesList", ntfPartyList);
					ntfCtx.put("targetLink", "emplClaimId=" + emplClaimId);
					ntfCtx.put("state", "open");
					ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
					ntfCtx.put("userLogin", userLogin);
					ntfCtx.put("ntfType", "ONE");
					dispatcher.runSync("createNotification", ntfCtx);	
				}
			}else if("EMPL_CLAIM_RESPONED".equals(emplClaim.getString("statusId"))){
				if(userLogin.getString("partyId").equals(emplClaim.getString("partyId"))){
					emplClaim.set("statusId", statusId);
					emplClaim.store();
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DonotPermissionUpdate", locale));
				}
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DonotPermissionUpdate", locale));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
}
