package com.olbius.appbasemtl.hr;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;

import com.olbius.basehr.recruitment.helper.RecruitmentHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;
import com.olbius.basehr.util.SecurityUtil;

public class HRWorkFlowActionServices {
	
	public static Map<String, Object> getRecruitmentAnticipateStatusAfterAppr(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String statusId = null;
		try {
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			String currtatusId = recruitmentAnticipate.getString("statusId");
			if("REC_ATCP_HR_ACC".equals(currtatusId)){
				List<GenericValue> recruitmentAnticipateItemReject = delegator.findByAnd("RecruitmentAnticipateItem", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "statusId", "REC_ATCP_DRT_REJ"), null, false);
				if(UtilValidate.isNotEmpty(recruitmentAnticipateItemReject)){
					statusId = "REC_ATCP_DRT_REJ";
				}else{
					statusId = "REC_ATCP_DRT_ACC";
				}
			}else if("REC_ATCP_HR_WAIT".equals(currtatusId)){
				List<GenericValue> recruitmentAnticipateItemReject = delegator.findByAnd("RecruitmentAnticipateItem", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "statusId", "REC_ATCP_HR_REJ"), null, false);
				if(UtilValidate.isNotEmpty(recruitmentAnticipateItemReject)){
					statusId = "REC_ATCP_HR_REJ";
				}else{
					statusId = "REC_ATCP_HR_ACC";
				}
			}
			retMap.put("newStatusId", statusId);
		} catch(GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> checkPermissionApprRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess("success");
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		boolean hasPermission = false;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		//String recruitAnticipateSeqId = (String)context.get("recruitAnticipateSeqId");
		Security security = dctx.getSecurity();
		try {
			if (!security.hasEntityPermission("RECRUITOFFICEPLAN", "_APPROVE", userLogin)) {
				retMap.put("hasPermission", false);
				retMap.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "HRTransactionNotAuthorized", locale));
				return retMap;
			}
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", 
					UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			if(recruitmentAnticipate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotFindRecruitmentAnticipateToAppr", locale));
			}
			String statusId = recruitmentAnticipate.getString("statusId");
			if("REC_ATCP_HR_WAIT".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.HRM_REC_SPEC, partyId, delegator) 
						|| SecurityUtil.hasRole(PropertiesUtil.HRM_ROLE, partyId, delegator)){
					hasPermission = true;
				}
			}else if("REC_ATCP_HR_ACC".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.DIRECTOR_ROLE, partyId, delegator)
						|| SecurityUtil.hasRole(PropertiesUtil.GENERAL_DIRECTOR_ROLE, partyId, delegator)){
					hasPermission = true;
				}
			}
			retMap.put("hasPermission", hasPermission);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage()) ;
		}
		return retMap;
	}
	public static Map<String, Object> checkPermissionApprRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess("success");
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		boolean hasPermission = false;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		Security security = dctx.getSecurity();
		try {
			if (!security.hasEntityPermission("RECRUITOFFICEPLAN", "_APPROVE", userLogin)) {
				retMap.put("hasPermission", false);
				retMap.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "HRTransactionNotAuthorized", locale));
				return retMap;
			}
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			if(recruitmentAnticipate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotFindRecruitmentRequireToAppr", locale));
			}
			String statusId = recruitmentAnticipate.getString("statusId");
			if("RECREQ_HR_WAIT".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.HRM_REC_SPEC, partyId, delegator) 
						|| SecurityUtil.hasRole(PropertiesUtil.HRM_ROLE, partyId, delegator)){
					hasPermission = true;
				}
			}else if("RECREQ_HR_ACC".equals(statusId) || "RECREQ_DRT_WAIT".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.DIRECTOR_ROLE, partyId, delegator)
						|| SecurityUtil.hasRole(PropertiesUtil.GENERAL_DIRECTOR_ROLE, partyId, delegator)){
					hasPermission = true;
				}
			}
			retMap.put("hasPermission", hasPermission);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage()) ;
		}
		return retMap;
	}
	public static Map<String, Object> getStatusListApprRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		String partyId = userLogin.getString("partyId");
		List<GenericValue> statusList = FastList.newInstance();
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		String statusIdNeedAppr = null;
		try {
			GenericValue recruitAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			String statusId = recruitAnticipate.getString("statusId");
			if("REC_ATCP_HR_WAIT".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.HRM_REC_SPEC, partyId, delegator) 
						|| SecurityUtil.hasRole(PropertiesUtil.HRM_ROLE, partyId, delegator)){
					statusList = delegator.findList("StatusItem", 
							EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("REC_ATCP_HR_WAIT", "REC_ATCP_HR_ACC", "REC_ATCP_HR_REJ")), 
							UtilMisc.toSet("statusId", "statusCode", "description"), 
							UtilMisc.toList("sequenceId"), null, false);
					statusIdNeedAppr = "REC_ATCP_HR_WAIT";
				}
			}else if("REC_ATCP_HR_ACC".equals(statusId)){
				if(SecurityUtil.hasRole(PropertiesUtil.DIRECTOR_ROLE, partyId, delegator)
						|| SecurityUtil.hasRole(PropertiesUtil.GENERAL_DIRECTOR_ROLE, partyId, delegator)){
					statusList = delegator.findList("StatusItem", 
							EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("REC_ATCP_HR_ACC", "REC_ATCP_DRT_ACC", "REC_ATCP_DRT_REJ")), 
							UtilMisc.toSet("statusId", "statusCode", "description"), 
							UtilMisc.toList("sequenceId"), null, false);
				}
				statusIdNeedAppr = "REC_ATCP_HR_ACC";
			}
			retMap.put("statusList", statusList);
			retMap.put("statusIdNeedAppr", statusIdNeedAppr);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> getRecruitAnticipateItemListCanEdit(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		successResult.put("listReturn", listIterator);
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(recruitAnticipateId != null){
			try {
				GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
				if(recruitmentAnticipate == null){
					return ServiceUtil.returnError("cannot found RecruitmentAnticipate record");
				}
				String createdByPartyId = recruitmentAnticipate.getString("createdByPartyId");
				if(!createdByPartyId.equals(userLogin.getString("partyId"))){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "YouCannotEditBecauseYouNotCreated", locale));
				}
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("recruitAnticipateId", recruitAnticipateId));
				conds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("REC_ATCP_HR_WAIT", "REC_ATCP_HR_REJ", "REC_ATCP_DRT_REJ")));
				List<GenericValue> recruitmentAnticipateItemList = delegator.findList("RecruitmentAnticipateItem", 
						EntityCondition.makeCondition(conds), null, UtilMisc.toList("recruitAnticipateSeqId"), null, false);
				for(GenericValue tempGv: recruitmentAnticipateItemList){
					Map<String, Object> tempMap = tempGv.getFields(UtilMisc.toList("recruitAnticipateId", "recruitAnticipateSeqId", "quantity", "statusId"));
					Long recruitAnticipateSeqId = tempGv.getLong("recruitAnticipateSeqId");
					tempMap.put("month", UtilProperties.getMessage("BaseHRUiLabels", "HRCommonMonth", locale) + " " + (recruitAnticipateSeqId + 1));
					String statusId = tempGv.getString("statusId");
					if("REC_ATCP_HR_WAIT".equals(statusId)){
						String changeReason = RecruitmentHelper.getChangeReasonRecruitmentAnticipateItem(delegator, recruitAnticipateId, recruitAnticipateSeqId);
						tempMap.put("changeReason", changeReason);
					}
					String apprReason = RecruitmentHelper.getChangeReasonRecruitmentAnticipateItem(delegator, recruitAnticipateId, 
							recruitAnticipateSeqId, UtilMisc.toList("REC_ATCP_HR_REJ", "REC_ATCP_DRT_REJ"));
					tempMap.put("apprReason", apprReason);
					listIterator.add(tempMap);
				}
			} catch (GenericEntityException e){
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return successResult;
	}
	public static Map<String, Object> editRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		String editRecruitAnticipateItemListParam = (String)context.get("editRecruitAnticipateItemList");
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		if(editRecruitAnticipateItemListParam == null){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NoRowSelected", locale));
		}
		JSONArray editRecruitAnticipateItemListJson = JSONArray.fromObject(editRecruitAnticipateItemListParam);
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("locale", locale);
		ctxMap.put("recruitAnticipateId", recruitAnticipateId);
		Map<String, Object> resultService = null;
		try {
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			if(recruitmentAnticipate == null){
				return ServiceUtil.returnError("not found record recruitment anitcipate to update");
			}
			List<Long> listItemUpdateSuccess = FastList.newInstance();
			List<Long> listItemUpdateFail = FastList.newInstance();
			for(int i = 0; i < editRecruitAnticipateItemListJson.size(); i++){
				JSONObject editRecruitAnticipateItemJson = editRecruitAnticipateItemListJson.getJSONObject(i);
				Long recruitAnticipateSeqId = editRecruitAnticipateItemJson.getLong("recruitAnticipateSeqId");
				GenericValue recruitmentAnticipateItem = delegator.findOne("RecruitmentAnticipateItem", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "recruitAnticipateSeqId", recruitAnticipateSeqId), false);
				String statusIdItem = recruitmentAnticipateItem.getString("statusId");
				if("REC_ATCP_HR_WAIT".equals(statusIdItem) || "REC_ATCP_HR_REJ".equals(statusIdItem) || "REC_ATCP_DRT_REJ".equals(statusIdItem)){
					String quantity = editRecruitAnticipateItemJson.getString("quantity");
					recruitmentAnticipateItem.set("quantity", new BigDecimal(quantity));
					String changeReason = null;
					if(editRecruitAnticipateItemJson.has("changeReason")){
						changeReason = editRecruitAnticipateItemJson.getString("changeReason");
					}
					if("REC_ATCP_HR_WAIT".equals(statusIdItem)){
						List<GenericValue> recruitmentAnticipateStatusList = delegator.findByAnd("RecruitmentAnticipateStatus", 
								UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "recruitAnticipateSeqId", recruitAnticipateSeqId,
										"statusId", statusIdItem, "statusUserLogin", userLogin.getString("userLoginId")), UtilMisc.toList("-statusDatetime"), false);
						if(UtilValidate.isNotEmpty(recruitmentAnticipateStatusList)){
							GenericValue recruitmentAnticipateStatus = recruitmentAnticipateStatusList.get(0);
							recruitmentAnticipateStatus.set("changeReason", changeReason);
							recruitmentAnticipateStatus.store();
						}
					}else{
						ctxMap.put("recruitAnticipateSeqId", recruitAnticipateSeqId);
						ctxMap.put("changeReason", changeReason);
						ctxMap.put("statusId", "REC_ATCP_HR_WAIT");
						recruitmentAnticipateItem.set("statusId", "REC_ATCP_HR_WAIT");
						resultService = dispatcher.runSync("createRecruitmentAnticipateStatus", ctxMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
						}
					}
					recruitmentAnticipateItem.store();
					listItemUpdateSuccess.add(recruitAnticipateSeqId + 1);
				}else{
					listItemUpdateFail.add(recruitAnticipateSeqId + 1);
				}
			}
			String statusId = recruitmentAnticipate.getString("statusId");
			if(!"REC_ATCP_HR_WAIT".equals(statusId)){
				recruitmentAnticipate.set("statusId", "REC_ATCP_HR_WAIT");
				recruitmentAnticipate.store();
				dispatcher.runSync("sendNotifyApprRecruitmentAnticipate", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "userLogin", userLogin, "locale", locale));
			}
			if(UtilValidate.isNotEmpty(listItemUpdateSuccess)){
				retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "UpdateRecruitmentAnticipateSuccessful",
						UtilMisc.toMap("months", StringUtils.join(listItemUpdateSuccess, ", ")), locale));
			}else{
				retMap = ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "UpdateRecruitmentAnticipateFail", 
						UtilMisc.toMap("months", StringUtils.join(listItemUpdateFail, ", ")),locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> sendNotifyApprRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String statusId = (String)context.get("statusId");
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		try {
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			if(statusId == null){
				statusId = recruitmentAnticipate.getString("statusId");
			}
			String header = null;
			String action = "ViewRecruitmentAnticipateList";
			String emplPositionTypeId = recruitmentAnticipate.getString("emplPositionTypeId");
			String partyId = recruitmentAnticipate.getString("partyId");
			Long year = recruitmentAnticipate.getLong("year");
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
			String createdByPartyId = recruitmentAnticipate.getString("createdByPartyId");
			List<String> roleTypes = FastList.newInstance();
			List<String> partyIds = FastList.newInstance();
			String propertykey = null;
			Map<String, Object> map = UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"), 
					"year", String.valueOf(year), 
					"groupName", partyGroup.get("groupName"));
			if("REC_ATCP_HR_ACC".equals(statusId)){
				roleTypes.add(PropertiesUtil.DIRECTOR_ROLE);
				roleTypes.add(PropertiesUtil.GENERAL_DIRECTOR_ROLE);
				propertykey = "RecruitmentAnticipateApprNotify";
			}else if("REC_ATCP_HR_WAIT".equals(statusId)){
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				propertykey = "RecruitmentAnticipateApprNotify";
			}else if("REC_ATCP_HR_REJ".equals(statusId)){
				partyIds.add(createdByPartyId);
				propertykey = "RecruitmentAnticipateRejectNotify";
			}else if("REC_ATCP_DRT_REJ".equals(statusId)){
				partyIds.add(createdByPartyId);
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				roleTypes.add(PropertiesUtil.HRM_ROLE);
				propertykey = "RecruitmentAnticipateRejectNotify";
			}else if("REC_ATCP_DRT_ACC".equals(statusId)){
				partyIds.add(createdByPartyId);
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				roleTypes.add(PropertiesUtil.HRM_ROLE);
				propertykey = "RecruitmentAnticipateAccpetNotify";
			}
			if(propertykey != null){
				header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", propertykey, map, locale);
				CommonUtil.sendNotify(dispatcher, locale, partyIds, roleTypes, userLogin, header, action, null, null, null, null, null, null);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateRecruitmentRequireStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		try {
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			if(recruitmentRequire == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "cannot find recruitment require", locale));
			}
			String enumRecruitReqTypeId = recruitmentRequire.getString("enumRecruitReqTypeId");
			String statusId = recruitmentRequire.getString("statusId");
			if("RECRUIT_REQUIRE_PLANNED".equals(enumRecruitReqTypeId)){
				statusId = "RECREQ_HR_WAIT";
			}else if("RECRUIT_REQUIRE_UNPLANNED".equals(enumRecruitReqTypeId)){
				statusId = "RECREQ_DRT_WAIT";
			}
			recruitmentRequire.set("statusId", statusId);
			recruitmentRequire.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> sendNotifyApprRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String statusId = (String)context.get("statusId");
		try {
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			String header = null;
			String action = "ViewListRecruitmentRequirement";
			String emplPositionTypeId = recruitmentRequire.getString("emplPositionTypeId");
			String partyId = recruitmentRequire.getString("partyId");
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
			List<String> roleTypes = FastList.newInstance();
			List<String> partyIds = FastList.newInstance();
			String propertykey = null;
			Map<String, Object> map = UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"), 
					"month", (recruitmentRequire.getLong("month") + 1), "year", String.valueOf(recruitmentRequire.get("year")) ,
					"groupName", partyGroup.get("groupName"));
			String createdByPartyId = recruitmentRequire.getString("createdByPartyId");
			if(statusId == null){
				statusId = recruitmentRequire.getString("statusId");
			}
			if("RECREQ_HR_ACC".equals(statusId) || "RECREQ_DRT_WAIT".equals(statusId)){
				roleTypes.add(PropertiesUtil.DIRECTOR_ROLE);
				roleTypes.add(PropertiesUtil.GENERAL_DIRECTOR_ROLE);
				propertykey = "RecruitmentRequireAppr";
			}else if("RECREQ_HR_WAIT".equals(statusId)){
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				propertykey = "RecruitmentRequireAppr";
			}else if("RECREQ_HR_REJ".equals(statusId)){
				partyIds.add(createdByPartyId);
				propertykey = "RecruitmentRequireReject";
			}else if("RECREQ_DRT_REJ".equals(statusId)){
				partyIds.add(createdByPartyId);
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				roleTypes.add(PropertiesUtil.HRM_ROLE);
				propertykey = "RecruitmentRequireReject";
			}else if("RECREQ_DRT_ACC".equals(statusId)){
				partyIds.add(createdByPartyId);
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				roleTypes.add(PropertiesUtil.HRM_ROLE);
				propertykey = "RecruitmentRequireAccpet";
			}else if("RECREQ_CAN".equals(statusId)){
				partyIds.add(createdByPartyId);
				roleTypes.add(PropertiesUtil.HRM_REC_SPEC);
				roleTypes.add(PropertiesUtil.HRM_ROLE);
				propertykey = "RecruitmentRequireCancel";
			}
			if(propertykey != null){
				String enumRecruitReqTypeId = recruitmentRequire.getString("enumRecruitReqTypeId");
				if("RECRUIT_REQUIRE_PLANNED".equals(enumRecruitReqTypeId)){
					propertykey += "PlannedNotify";
				}else if("RECRUIT_REQUIRE_UNPLANNED".equals(enumRecruitReqTypeId)){
					propertykey += "UnplannedNotify";
				}
				header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", propertykey, map, locale);
				CommonUtil.sendNotify(dispatcher, locale, partyIds, roleTypes, userLogin, header, action, null, null, null, null, null, null);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getNextStatusRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		String statusCode = (String)context.get("statusCode");
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "checkPermissionApprRecruitmentRequire", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("checkPermissionApprRecruitmentRequire", ctxMap);
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			String statusId = recruitmentRequire.getString("statusId");
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			Boolean hasPermission = (Boolean)resultService.get("hasPermission");
			if(hasPermission == null || !hasPermission){
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotApprRecruitmentRequireBecausePermission", 
						UtilMisc.toMap("status", status.get("description", locale)), locale));
			}
			String newStatusId = statusId;
			if("RECREQ_HR_WAIT".equals(statusId)){
				if("accept".equals(statusCode)){
					newStatusId = "RECREQ_HR_ACC";
				}else if("reject".equals(statusId)){
					newStatusId = "RECREQ_HR_REJ";
				}else if("cancel".equals(statusCode)){
					newStatusId = "RECREQ_CAN";
				}
			}else if("RECREQ_DRT_WAIT".equals(statusId) || "RECREQ_HR_ACC".equals(statusId)){
				if("accept".equals(statusCode)){
					newStatusId = "RECREQ_DRT_ACC";
				}else if("reject".equals(statusCode)){
					newStatusId = "RECREQ_DRT_REJ";
				}else if("cancel".equals(statusCode)){
					newStatusId = "RECREQ_CAN";
				}
			}
			retMap.put("newStatusId", newStatusId);
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> checkRecruitmentRequireEditable(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			if(recruitmentRequire == null){
				return ServiceUtil.returnError("cannot find RecruitmentRequire in checkRecruitmentRequireEditable services");
			}
			String createdByPartyId = recruitmentRequire.getString("createdByPartyId");
			if(!userLogin.getString("partyId").equals(createdByPartyId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotEditRecruitmentRequireUserLoginNotCreated", locale));
			}
			String statusId = recruitmentRequire.getString("statusId");
			if("RECREQ_HR_WAIT".equals(statusId) || "RECREQ_HR_REJ".equals(statusId) || "RECREQ_DRT_REJ".equals(statusId)){
				retMap.put("isEditable", true);
			}else{
				retMap.put("isEditable", false);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> getNextStatusRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		String recruitmentPlanSalesId = (String)context.get("recruitmentPlanSalesId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyIdUserLogin = userLogin.getString("partyId");
		String approvalType = (String)context.get("approvalType");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue recruitmentSalesEmpl = delegator.findOne("RecruitmentSalesEmpl", UtilMisc.toMap("partyId", partyId, "recruitmentPlanSalesId", recruitmentPlanSalesId), false);
			if(recruitmentSalesEmpl == null){
				return ServiceUtil.returnError("cannot find employee in recruitment sales plan");
			}
			String statusId = recruitmentSalesEmpl.getString("statusId");
			String newStatusId = null;
			switch (statusId) {
			case "RSALEEMPL_CREATED":
				if(SecurityUtil.hasRole(PropertiesUtil.SUP_ROLE, partyIdUserLogin, delegator)){
					newStatusId = "RSALEEMPL_NOT_OFFER";
					if(PropertiesUtil.APPR_ACCEPT.equals(approvalType)){
						newStatusId = "RSALEEMPL_ASM_WAIT";
					}
					retMap.put("newStatusId", newStatusId);
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "YouHaveNotPermissionUpdateThisPlan", locale));
				}
				break;
				
			case "RSALEEMPL_ASM_WAIT":
				if(SecurityUtil.hasRole(PropertiesUtil.ASM_ROLE, partyIdUserLogin, delegator)){
					newStatusId = "RSALEEMPL_ASM_REJECT";
					if(PropertiesUtil.APPR_ACCEPT.equals(approvalType)){
						newStatusId = "RSALEEMPL_ASM_ACCEPT";
					}
					retMap.put("newStatusId", newStatusId);
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "YouHaveNotPermissionUpdateThisPlan", locale));
				}
				break;
				
			case "RSALEEMPL_RSM_WAIT":
				if(SecurityUtil.hasRole(PropertiesUtil.RSM_ROLE, partyIdUserLogin, delegator)){
					newStatusId = "RSALEEMPL_RSM_REJECT";
					if(PropertiesUtil.APPR_ACCEPT.equals(approvalType)){
						newStatusId = "RSALEEMPL_RSM_ACCEPT";
					}
					retMap.put("newStatusId", newStatusId);
					
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "YouHaveNotPermissionUpdateThisPlan", locale));
				}
				break;
				
			case "RSALEEMPL_CSM_WAIT":
				if(SecurityUtil.hasRole(PropertiesUtil.CSM_ROLE, partyIdUserLogin, delegator)){
					newStatusId = "RSALEEMPL_CSM_REJECT";
					if(PropertiesUtil.APPR_ACCEPT.equals(approvalType)){
						newStatusId = "RSALEEMPL_CSM_ACCEPT";
					}
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "YouHaveNotPermissionUpdateThisPlan", locale));
				}
				break;
				
			case "RSALEEMPL_ASM_ACCEPT":
				newStatusId = "RSALEEMPL_RSM_WAIT";
				break;
				
			case "RSALEEMPL_RSM_ACCEPT":
				newStatusId = "RSALEEMPL_CSM_WAIT";
				break;
				
			default:
				break;
			}
			retMap.put("newStatusId", newStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> getListViewableStatusRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = dctx.getDelegator();
		List<String> statusIdList = FastList.newInstance();
		if(SecurityUtil.hasRole(PropertiesUtil.SUP_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_CSM_WAIT");
			statusIdList.add("RSALEEMPL_CSM_ACCEPT");
			statusIdList.add("RSALEEMPL_CSM_REJECT");
			statusIdList.add("RSALEEMPL_RSM_WAIT");
			statusIdList.add("RSALEEMPL_RSM_ACCEPT");
			statusIdList.add("RSALEEMPL_RSM_REJECT");
			statusIdList.add("RSALEEMPL_ASM_WAIT");
			statusIdList.add("RSALEEMPL_ASM_ACCEPT");
			statusIdList.add("RSALEEMPL_ASM_REJECT");
			statusIdList.add("RSALEEMPL_CREATED");
			statusIdList.add("RSALEEMPL_NOT_OFFER");
		}else if(SecurityUtil.hasRole(PropertiesUtil.ASM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_CSM_WAIT");
			statusIdList.add("RSALEEMPL_CSM_ACCEPT");
			statusIdList.add("RSALEEMPL_CSM_REJECT");
			statusIdList.add("RSALEEMPL_RSM_WAIT");
			statusIdList.add("RSALEEMPL_RSM_ACCEPT");
			statusIdList.add("RSALEEMPL_RSM_REJECT");
			statusIdList.add("RSALEEMPL_ASM_WAIT");
			statusIdList.add("RSALEEMPL_ASM_ACCEPT");
			statusIdList.add("RSALEEMPL_ASM_REJECT");
		}else if(SecurityUtil.hasRole(PropertiesUtil.RSM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_CSM_WAIT");
			statusIdList.add("RSALEEMPL_CSM_ACCEPT");
			statusIdList.add("RSALEEMPL_CSM_REJECT");
			statusIdList.add("RSALEEMPL_RSM_WAIT");
			statusIdList.add("RSALEEMPL_RSM_ACCEPT");
			statusIdList.add("RSALEEMPL_RSM_REJECT");
		}else if(SecurityUtil.hasRole(PropertiesUtil.CSM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_CSM_WAIT");
			statusIdList.add("RSALEEMPL_CSM_ACCEPT");
			statusIdList.add("RSALEEMPL_CSM_REJECT");
		}
		retMap.put("statusIdList", statusIdList);
		return retMap;
	}
	public static Map<String, Object> getListRecruitSalesEmplNotApprStatus(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = dctx.getDelegator();
		List<String> statusIdList = FastList.newInstance();
		if(SecurityUtil.hasRole(PropertiesUtil.ASM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_ASM_WAIT");
		}else if(SecurityUtil.hasRole(PropertiesUtil.RSM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_RSM_WAIT");
		}else if(SecurityUtil.hasRole(PropertiesUtil.CSM_ROLE, partyId, delegator)){
			statusIdList.add("RSALEEMPL_CSM_WAIT");
		}else{
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "HRTransactionNotAuthorized", (Locale)context.get("locale")));
		}
		retMap.put("statusIdList", statusIdList);
		return retMap;
	}
	public static Map<String, Object> getStatusSalesEmplProposable(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = dctx.getDelegator();
		String statusId = null;
		if(SecurityUtil.hasRole(PropertiesUtil.ASM_ROLE, partyId, delegator)){
			statusId = "RSALEEMPL_ASM_ACCEPT";
		}else if(SecurityUtil.hasRole(PropertiesUtil.RSM_ROLE, partyId, delegator)){
			statusId = "RSALEEMPL_RSM_ACCEPT";
		}else if(SecurityUtil.hasRole(PropertiesUtil.CSM_ROLE, partyId, delegator)){
			statusId = "RSALEEMPL_CSM_ACCEPT";
		}else{
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "HRTransactionNotAuthorized", (Locale)context.get("locale")));
		}
		retMap.put("statusId", statusId);
		return retMap;
	}
	
	public static Map<String, Object> getPartyIdOfferByStatus(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String statusId = (String)context.get("statusId");
		String partyGroupId = (String)context.get("partyGroupId");
		Delegator delegator = dctx.getDelegator();
		String partyIdOffer = null;
		try {
			switch (statusId) {
				case "RSALEEMPL_ASM_ACCEPT":
					while(true){
						GenericValue parentParty = PartyUtil.getParentOrgOfDepartmentCurr(delegator, partyGroupId);
						if(parentParty != null){
							String parentPartyId = parentParty.getString("partyIdFrom");
							if(SecurityUtil.hasRole(PropertiesUtil.ASM_GT_DEPT_ROLE, parentPartyId, delegator) ||
									SecurityUtil.hasRole(PropertiesUtil.ASM_MT_DEPT_ROLE, parentPartyId, delegator)){
								partyIdOffer = parentPartyId;
								break;
							}else{
								partyGroupId = parentPartyId;
							}
						}else{
							break;
						}
					}
					break;
					
				case "RSALEEMPL_RSM_ACCEPT":
					while(true){
						GenericValue parentParty = PartyUtil.getParentOrgOfDepartmentCurr(delegator, partyGroupId);
						if(parentParty != null){
							String parentPartyId = parentParty.getString("partyIdFrom");
							if(SecurityUtil.hasRole(PropertiesUtil.RSM_GT_DEPT_ROLE, parentPartyId, delegator) ||
									SecurityUtil.hasRole(PropertiesUtil.RSM_MT_DEPT_ROLE, parentPartyId, delegator)){
								partyIdOffer = parentPartyId;
								break;
							}else{
								partyGroupId = parentPartyId;
							}
						}else{
							break;
						}
					}
					break;
				default:
					break;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("partyIdOffer", partyIdOffer);
		return retMap;
	}
}
