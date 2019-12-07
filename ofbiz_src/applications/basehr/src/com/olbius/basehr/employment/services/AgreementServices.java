package com.olbius.basehr.employment.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServletRequest;

import com.olbius.basehr.employment.helper.AgreementHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.SecurityUtil;

public class AgreementServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplAgreement(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		Delegator delegator = dctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		//Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityListIterator listIterator = null;
		String statusId = request.getParameter("statusId");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			Timestamp startDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			if("EMPL_AGR_EFFECTIVE".equals(statusId)){
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "EMPL_AGR_EXPIRED"));
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
									EntityJoinOperator.OR,
									EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate)));
			}else if("EMPL_AGR_EXPIRED".equals(statusId)){
				Timestamp nowDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", "EMPL_AGR_EXPIRED"),
										EntityJoinOperator.OR, EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN, nowDate)));
			}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-agreementDate");
			}
			listIterator = delegator.find("EmploymentAgreementAndDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);		
			retMap.put("listIterator", listIterator);	
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return retMap;
	}
	
	public static Map<String, Object> getAgreementChangeStatus(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		String agreementId = (String)context.get("agreementId");
		String partyIdTo = (String)context.get("partyIdTo");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Delegator delegator = dctx.getDelegator();
		try {
			List<String> agreementTypeList = AgreementHelper.getAgreementType(delegator, CommonUtil.EMPL_AGREEMENT_TYPE);
			List<EntityCondition> conditions = FastList.newInstance();
			if(UtilValidate.isNotEmpty(agreementTypeList)){
				conditions.add(EntityCondition.makeCondition("agreementTypeId", EntityJoinOperator.IN, agreementTypeList));
			}
			conditions.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
			conditions.add(EntityCondition.makeCondition("agreementId", EntityJoinOperator.NOT_EQUAL, agreementId));
			List<GenericValue> listAgreement = delegator.findList("Agreement", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(listAgreement)){
				GenericValue agreementUpdate = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
				String statusIdAgrUpdate = agreementUpdate.getString("statusId");
				GenericValue agreementCheck = listAgreement.get(0);
				Timestamp fromDateArgrCheck = agreementCheck.getTimestamp("fromDate");
				if(fromDate.before(fromDateArgrCheck)){
					if("EMPL_AGR_EFFECTIVE".equals(statusIdAgrUpdate)){
						retMap.put("agreementIdChangeStt", agreementCheck.getString("agreementId"));
						retMap.put("statusIdChange", "EMPL_AGR_EFFECTIVE");
						retMap.put("statusIdUpdate", "EMPL_AGR_EXPIRED");
					}
				}else if(fromDate.after(fromDateArgrCheck)){
					if("EMPL_AGR_EXPIRED".equals(statusIdAgrUpdate)){
						retMap.put("agreementIdChangeStt", agreementCheck.getString("agreementId"));
						retMap.put("statusIdChange", "EMPL_AGR_EXPIRED");
						retMap.put("statusIdUpdate", "EMPL_AGR_EFFECTIVE");
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getAgreementEffectivePartyInPeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String partyIdTo = (String)context.get("partyIdTo");
		String agreementTypeId = (String)context.get("agreementTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
		conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "AGREEMENT_EXPIRED"));
		try {
			List<String> agreementTypeList = AgreementHelper.getAgreementType(delegator, CommonUtil.EMPL_AGREEMENT_TYPE);
			if(UtilValidate.isNotEmpty(agreementTypeList)){
				conditions.add(EntityCondition.makeCondition("agreementTypeId", EntityJoinOperator.IN, agreementTypeList));
			}
			if("UNLIMITED_TIME_AGR".equals(agreementTypeId)){
				thruDate = null;
			}
			conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			List<GenericValue> agreementEffectList = delegator.findList("Agreement", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(agreementEffectList)){
				String agreementCode = agreementEffectList.get(0).getString("agreementCode");
				retMap.put("agreementCode", agreementCode);
				retMap.put("agreementId", agreementEffectList.get(0).getString("agreementId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> uploadedFileAgreement(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "UploadedFileSuccessfully", locale));
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "hrmdoc";
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("public", "N");
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			String path = (String)resultService.get("path");
			Map<String, Object> dataResourceCtx = FastMap.newInstance();
			dataResourceCtx.put("objectInfo", path);
	        dataResourceCtx.put("dataResourceName", uploadFileNameStr);
	        dataResourceCtx.put("userLogin", systemUserLogin);
	        dataResourceCtx.put("dataResourceTypeId", "URL_RESOURCE");
	        dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
	        dataResourceCtx.put("isPublic", "N");
	        resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
	        String dataResourceId = (String) resultService.get("dataResourceId");
	        Map<String, Object> contentCtx = FastMap.newInstance();
	        contentCtx.put("dataResourceId", dataResourceId);
	        contentCtx.put("contentTypeId", "DOCUMENT");
	        contentCtx.put("contentName", uploadFileNameStr);
	        contentCtx.put("userLogin", systemUserLogin);
	        resultService = dispatcher.runSync("createContent", contentCtx);
	        if(!ServiceUtil.isSuccess(resultService)){
	        	return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
	        }
	        String contentId = (String)resultService.get("contentId");
	        retMap.put("contentId", contentId);
	        retMap.put("path", path);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> preCreateEmploymentAgreement(DispatchContext dctx, Map<String, Object> context){
		//Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String agreementDateStr = (String)context.get("agreementDate");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String listContentId = (String)context.get("listContentId");
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp agreementDate = null;
		if(agreementDateStr != null){
			agreementDate = new Timestamp(Long.parseLong(agreementDateStr));
		}
		try {
			Map<String, Object> ctx = FastMap.newInstance();
			ctx.putAll(context);
			ctx.put("fromDate", fromDate);
			ctx.put("thruDate", thruDate);
			ctx.put("agreementDate", agreementDate);
			Map<String, Object> resultService = dispatcher.runSync("createEmploymentAgreement", ServiceUtil.setServiceFields(dispatcher, "createEmploymentAgreement", ctx, userLogin, timeZone, locale)); 
			if(ServiceUtil.isSuccess(resultService)){
				String agreementId = (String)resultService.get("agreementId");
				String partyIdRep = (String)context.get("partyIdRep");
				if(partyIdRep != null){
					dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdRep, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE, "userLogin", userLogin));
					dispatcher.runSync("createAgreementRole", UtilMisc.toMap("partyId", partyIdRep, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE, "agreementId", agreementId, "userLogin", userLogin));
				}
				if(listContentId != null){
					JSONArray listContentIdJson = JSONArray.fromObject(listContentId);
					for(int i = 0; i < listContentIdJson.size(); i++){
						String contentId = listContentIdJson.getString(i);
						resultService = dispatcher.runSync("createAgreementContent", UtilMisc.toMap("contentId", contentId, "agreementId", agreementId, "userLogin", userLogin));
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
						}
					}
				}
			}else{
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> preUpdateEmploymentAgreement(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String agreementDateStr = (String)context.get("agreementDate");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String agreementId = (String)context.get("agreementId");
		Timestamp thruDate = null;
		String listContentId = (String)context.get("listContentId");
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp agreementDate = null;
		if(agreementDateStr != null){
			agreementDate = new Timestamp(Long.parseLong(agreementDateStr));
		}
		Map<String, Object> ctx = FastMap.newInstance();
		ctx.putAll(context);
		ctx.put("fromDate", fromDate);
		ctx.put("thruDate", thruDate);
		ctx.put("agreementDate", agreementDate);
		try {
			Map<String, Object> resultService = dispatcher.runSync("updateEmploymentAgreement", ServiceUtil.setServiceFields(dispatcher, "updateEmploymentAgreement", ctx, userLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resultService)){
				String partyIdRep = (String)context.get("partyIdRep");
				if(partyIdRep != null){
					GenericValue agreementRole = delegator.findOne("AgreementRole", UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdRep, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE), false);
					if(agreementRole == null){
						List<GenericValue> agreementRoleList = delegator.findByAnd("AgreementRole", UtilMisc.toMap("agreementId", agreementId, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE), null, false);
						for(GenericValue tempGv: agreementRoleList){
							tempGv.remove();
						}
						dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdRep, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE, "userLogin", userLogin));
						dispatcher.runSync("createAgreementRole", UtilMisc.toMap("partyId", partyIdRep, "roleTypeId", AgreementHelper.AGREEMENT_ROLE_REPRESENTATIVE, "agreementId", agreementId, "userLogin", userLogin));
					}
				}
				if(listContentId != null){
					JSONArray listContentIdJson = JSONArray.fromObject(listContentId);
					for(int i = 0; i < listContentIdJson.size(); i++){
						String contentId = listContentIdJson.getString(i);
						resultService = dispatcher.runSync("createAgreementContent", UtilMisc.toMap("contentId", contentId, "agreementId", agreementId, "userLogin", userLogin));
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
						}
					}
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateEmploymentAgreement(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String agreementTypeId = (String)context.get("agreementTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String agreementId = (String)context.get("agreementId");
		BigDecimal insuranceSalary = (BigDecimal)context.get("insuranceSalary");
		BigDecimal basicSalary = (BigDecimal)context.get("basicSalary");
		BigDecimal payRate = (BigDecimal)context.get("payRate");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String agreementDuration = (String)context.get("agreementDuration");
		String partyIdTo = (String)context.get("partyIdTo");
		//String partyIdFrom = (String)context.get("partyIdFrom");
		Locale locale = (Locale)context.get("locale");
		String allowanceParam = (String)context.get("allowance");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String workPlace = (String)context.get("workPlace");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(!SecurityUtil.hasRole("EMPLOYEE", partyIdTo, delegator)){
				dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", "EMPLOYEE", "userLogin", userLogin));
			}
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			agreement.setNonPKFields(context);
			if("UNLIMITED_TIME_AGR".equals(agreementTypeId)){
				thruDate = null;
				agreement.set("thruDate", null);
			}
			
			Map<String, Object> resutlService = dispatcher.runSync("getAgreementChangeStatus", 
					UtilMisc.toMap("partyIdTo", partyIdTo, "agreementId", agreementId, "fromDate", fromDate, "userLogin", userLogin));
			String agreementIdChangeStt = (String)resutlService.get("agreementIdChangeStt");
			if(agreementIdChangeStt != null){
				String statusIdChange = (String)resutlService.get("statusIdChange");
				dispatcher.runSync("updateAgreement", UtilMisc.toMap("agreementId", agreementIdChangeStt, "statusId", statusIdChange, "userLogin", systemUserLogin));
				agreement.set("statusId", resutlService.get("statusIdUpdate"));
			}
			agreement.store();
			if(agreementDuration != null && !"UNLIMITED_TIME_AGR".equals(agreementTypeId)){
				AgreementHelper.createOrStoreAgreementAttribute(delegator, agreementId, agreementDuration);
			}
			if(basicSalary != null){
				AgreementHelper.createOrUpdateAgreementTerm(dispatcher, delegator, agreementId, "SAL_BASE_TERM", basicSalary, null, fromDate, thruDate, systemUserLogin);
			}
			if(insuranceSalary != null){
				AgreementHelper.createOrUpdateAgreementTerm(dispatcher, delegator, agreementId, "INS_SAL_TERM", insuranceSalary, null, fromDate, thruDate, systemUserLogin);
			}
			if(payRate != null){
				AgreementHelper.createOrUpdateAgreementTerm(dispatcher, delegator, agreementId, "PAY_RATE_TERM", payRate, null, fromDate, thruDate, systemUserLogin);
			}
			if(emplPositionTypeId != null){
				AgreementHelper.createOrUpdateAgreementTerm(dispatcher, delegator, agreementId, "POSITION_TYPE_TERM", null, emplPositionTypeId, fromDate, thruDate, systemUserLogin);
			}
			if(workPlace != null){
				AgreementHelper.createOrUpdateAgreementTerm(dispatcher, delegator, agreementId, "WORKPLACE_TERM", null, workPlace, fromDate, thruDate, systemUserLogin);
			}
			if(allowanceParam != null){
				Map<String, BigDecimal> allowanceMap = FastMap.newInstance();
				JSONArray allowanceJsonList = JSONArray.fromObject(allowanceParam);
				for(int i = 0; i < allowanceJsonList.size(); i++){
					JSONObject allowanceJson = allowanceJsonList.getJSONObject(i);
					String code = allowanceJson.getString("code");
					String valueStr = allowanceJson.getString("value");
					BigDecimal value = new BigDecimal(valueStr);
					allowanceMap.put(code, value);
				}
				Map<String, Object> result = AgreementHelper.updateAgreementAllowanceTerm(dispatcher, delegator, systemUserLogin, agreementId, allowanceMap, fromDate, thruDate);
				if(ModelService.ERROR_MESSAGE.equals(result.get(ModelService.RESPONSE_MESSAGE))){
					return ServiceUtil.returnError(ModelService.ERROR_MESSAGE);
				}
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmploymentAgreement(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp agreementDate = (Timestamp)context.get("agreementDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String statusId = (String)context.get("statusId");
		String agreementTypeId = (String)context.get("agreementTypeId");
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String)context.get("roleTypeIdTo");
		String partyIdTo = (String)context.get("partyIdTo");
		String partyIdFrom = (String)context.get("partyIdFrom");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String agreementDuration = (String)context.get("agreementDuration");
		BigDecimal insuranceSalary = (BigDecimal)context.get("insuranceSalary");
		BigDecimal basicSalary = (BigDecimal)context.get("basicSalary");
		BigDecimal payRate = (BigDecimal)context.get("payRate");
		String allowanceParam = (String)context.get("allowance");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String workPlace = (String)context.get("workPlace");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createAgreement", context, systemUserLogin, timeZone, locale);
			if(agreementDate == null){
				agreementDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				ctxMap.put("agreementDate", agreementDate);
			}
			if("UNLIMITED_TIME_AGR".equals(agreementTypeId)){
				ctxMap.put("thruDate", null);
			}
			if(roleTypeIdFrom == null){
				roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
				if(!SecurityUtil.hasRole(roleTypeIdFrom, partyIdFrom, delegator)){
					dispatcher.runSync("createPartyRole", 
							UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", roleTypeIdFrom, "userLogin", userLogin));
				}
				ctxMap.put("roleTypeIdFrom", roleTypeIdFrom);
			}
			if(roleTypeIdTo == null){
				roleTypeIdTo = "EMPLOYEE";
				if(!SecurityUtil.hasRole(roleTypeIdTo, partyIdTo, delegator)){
					dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", roleTypeIdTo, "userLogin", userLogin));
				}
				ctxMap.put("roleTypeIdTo", roleTypeIdTo);
			}
			//expiry agreement still effective
			Map<String, Object> resultService = dispatcher.runSync("getAgreementEffectivePartyInPeriod", 
					UtilMisc.toMap("partyIdTo", partyIdTo, "fromDate", fromDate, "thruDate", thruDate, "agreementTypeId", agreementTypeId, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String agreementId = (String)resultService.get("agreementId");
			if(agreementId != null){
				GenericValue agreementEff = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
				Timestamp fromDateEff = agreementEff.getTimestamp("fromDate");
				if(fromDateEff.equals(fromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmploymentUiLabels", "NewAgreementHaveSameFromDateOldAgreement", 
							UtilMisc.toMap("agreementCode", agreementEff.getString("agreementCode"), "fromDate", DateUtil.getDateMonthYearDesc(fromDate)), locale));
				}
				if(!fromDateEff.after(fromDate)){
					dispatcher.runSync("updateAgreement", UtilMisc.toMap("agreementId", agreementId, "statusId", "EMPL_AGR_EXPIRED", "userLogin", systemUserLogin));
					statusId = "EMPL_AGR_EFFECTIVE";
				}else{
					statusId = "EMPL_AGR_EXPIRED";
				}
			}else{
				statusId = "EMPL_AGR_EFFECTIVE";
			}
			ctxMap.put("statusId", statusId);
			resultService = dispatcher.runSync("createAgreement", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String newAgreementId = (String)resultService.get("agreementId");
			retMap.put("agreementId", newAgreementId);
			if(agreementDuration != null && !"UNLIMITED_TIME_AGR".equals(agreementTypeId)){
				AgreementHelper.createOrStoreAgreementAttribute(delegator, newAgreementId, agreementDuration);
			}
			if(basicSalary != null){
				dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "SAL_BASE_TERM", "termValue", basicSalary, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
			}
			if(insuranceSalary != null){
				dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "INS_SAL_TERM", "termValue", insuranceSalary, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
			}
			if(payRate != null){
				dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "PAY_RATE_TERM", "termValue", payRate, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
			}
			if(allowanceParam != null){
				JSONArray allowanceJsonList = JSONArray.fromObject(allowanceParam);
				for(int i = 0; i < allowanceJsonList.size(); i++){
					JSONObject allowanceJson = allowanceJsonList.getJSONObject(i);
					String code = allowanceJson.getString("code");
					String valueStr = allowanceJson.getString("value");
					BigDecimal value = new BigDecimal(valueStr);
					resultService = dispatcher.runSync("createAgreementTerm", 
							UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "ALLOWANCE_TERM", "termValue", value, 
									"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
					if(ServiceUtil.isSuccess(resultService)){
						String agreementTermId = (String)resultService.get("agreementTermId");
						AgreementHelper.createAgreementTermAttr(delegator, agreementTermId, "ALLOWANCE_CODE", code);
					}
				}
			}
			if(emplPositionTypeId != null){
				dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "POSITION_TYPE_TERM", "textValue", emplPositionTypeId, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
			}
			if(workPlace != null){
				dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", newAgreementId, "termTypeId", "WORKPLACE_TERM", "textValue", workPlace, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", systemUserLogin));
			}
		} catch (GeneralServiceException e){
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
	
	public static Map<String, Object> createAgreementContent(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue agreementContent = delegator.makeValue("AgreementContent");
		agreementContent.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(agreementContent);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteAgreementContent(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String contentId = (String)context.get("contentId");
		String agreementId = (String)context.get("agreementId");
		try {
			GenericValue agreementContent = delegator.findOne("AgreementContent", UtilMisc.toMap("agreementId", agreementId, "contentId", contentId), false);
			if(agreementContent == null){
				return ServiceUtil.returnError("cannot find content to delete");
			}
			agreementContent.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> getAgreementContent(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess(ModelService.RESPOND_SUCCESS);
		Delegator delegator = dctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		try {
			List<GenericValue> listContent = delegator.findList("AgreementContentAndDataResource", EntityCondition.makeCondition("agreementId", agreementId), null, null, null, false);
			retMap.put("listReturn", listContent);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getAgreementAllowance(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess(ModelService.RESPOND_SUCCESS);
		Delegator delegator = dctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		try {
			List<GenericValue> allowanceList = delegator.findByAnd("AgreementTermAllowanceAndAttr", 
					UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ALLOWANCE_TERM", "attrName", "ALLOWANCE_CODE"), null, false);
			retMap.put("listReturn", allowanceList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	/**
	 * 1. Create PartyRole if not exist
	 * 2. Create Agreement
	 * 3. Create Position Fulfillment
	 * 4. Create Employment
	 * @param dpcx
	 * @param context
	 * @return
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	//FIXME Create Employment Agreement Yet
	public static Map<String, Object> createEmplAgreement(DispatchContext dpcx, Map<String, ? extends Object> context){
		//Get Delegator
		Delegator delegator = dpcx.getDelegator();
		//Get LocalDispatcher
		LocalDispatcher dispatcher = dpcx.getDispatcher();
		//Get Parameters
		String emplId = (String) context.get("partyIdTo");
		String partyIdFrom = (String)context.get("partyIdFrom");
		Locale locale = (Locale) context.get("locale");
		String agreementTypeId = (String)context.get("agreementTypeId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String agreementId = (String) context.get("agreementId");
		Timestamp agreementDate = (Timestamp)context.get("agreementDate");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyIdFromRepresent = (String)context.get("partyIdFromRepresent");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> agreementCtx = FastMap.newInstance();
		agreementCtx.putAll(context);
		if(agreementDate == null){
			agreementDate = UtilDateTime.nowTimestamp();
			agreementCtx.put("agreementDate", agreementDate);
		}
		if(fromDate != null && fromDate.before(agreementDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "HrolbiusRequiredValueGreatherAgreementDate", locale));
		}
		if(fromDate != null && thruDate != null && thruDate.before(fromDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ThruDateMustGreaterThanFromDate", locale));
		}
		
		final String ROLE_TYPE_ID_TO = "EMPLOYEE";
		// Run Service createAgreement
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> createAgrResult;
		try {
			GenericValue empl = delegator.findOne("Person", UtilMisc.toMap("partyId", emplId), false);
			GenericValue partyFrom = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFrom), false);
			GenericValue partyFromRepresent = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromRepresent), false);
			if(empl == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindParty", UtilMisc.toMap("partyId", emplId), locale));
			}
			if(partyFrom == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindPartyGroup", UtilMisc.toMap("partyId", partyIdFrom), locale));
			}
			if(partyFromRepresent == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindParty", UtilMisc.toMap("partyId", partyIdFromRepresent), locale));
			}
			// Check Agreement is already exists
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if(agreement != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "alreadyExists", locale));
			}		
			//Create PartyRole If not Exist
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", emplId, "roleTypeId", ROLE_TYPE_ID_TO, "userLogin", context.get("userLogin")));
			createAgrResult = dispatcher.runSync("createAgreement", ServiceUtil.setServiceFields(dispatcher, "createAgreement", agreementCtx, userLogin, timeZone, locale));
			agreementId = (String)createAgrResult.get("agreementId");
			result.put("agreementId", agreementId);			
			if(partyIdFromRepresent != null){
				dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdFromRepresent, "roleTypeId", "REPRESENT_PARTY_FROM", "userLogin", context.get("userLogin")));	
				dispatcher.runSync("createAgreementRole", UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "REPRESENT_PARTY_FROM", "partyId", partyIdFromRepresent, "userLogin", context.get("userLogin")));
			}
			//create agreement term
			List<GenericValue> allTerm = FastList.newInstance();
			//set all term of employment or trial term
			if(CommonUtil.EMPL_AGREEMENT_TYPE.equals(agreementTypeId)){
				allTerm = getAllTerm(dpcx, "EMPLOYEMENT_TERM");
			}else if("TRIAL_AGREEMENT".equals(agreementTypeId)){
				allTerm = getAllTerm(dpcx, "PROBATION_TERM");
			}
			
			for(GenericValue termType: allTerm){
				String termTypeId = termType.getString("termTypeId");
				if(termTypeId.equals("EMPL_POSITION")){
					dispatcher.runSync("createAgreementTerm", UtilMisc.toMap("agreementId", agreementId, 
							"emplPositionId", context.get("emplPositionId"), 
							"termTypeId", termTypeId, 
							"fromDate", context.get("fromDate"),
							"thruDate", context.get("thruDate"),
							"userLogin", userLogin));
				}else{
					//check whether text value of term type pass from context
					String textValue = (String)context.get(termTypeId);
					//get defaultValue of termType's textValue
					GenericValue termTypeAttr = delegator.findOne("TermTypeAttr", UtilMisc.toMap("termTypeId", termTypeId, "attrName", "defaultValue"), false);
					if(textValue == null && termTypeAttr != null){
						textValue = termTypeAttr.getString("attrValue");
					}
					if(textValue != null){
						dispatcher.runSync("createAgreementTermHR", UtilMisc.toMap("agreementId", agreementId, 
																				"textValue", textValue, 
																				"termTypeId", termTypeId, 
																				"fromDate", context.get("fromDate"),
																				"thruDate", context.get("thruDate"),
																				"userLogin", userLogin));
					}	
				}
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHREmploymentUiLabels", "createSuccessfully", locale));
		return result;
	}
	
	public static List<GenericValue> getAllTerm(DispatchContext dctx,
			String parentTermTypeId) throws GenericEntityException {
		// TODO Auto-generated method stub
		Delegator delegator = dctx.getDelegator();		
		List<GenericValue> retList = FastList.newInstance();
		List<GenericValue> allChildTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", parentTermTypeId), null, false);
		for(GenericValue tempTermType: allChildTermType){
			retList.add(tempTermType);
			List<GenericValue> tempChildTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", tempTermType.getString("termTypeId")), null, false);
			if(UtilValidate.isNotEmpty(tempChildTermType)){
				retList.addAll(getAllTerm(dctx, tempTermType.getString("termTypeId")));
			}
		}
		return retList;
	}

	public static Map<String, Object> deleteEmplAgreement(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		try {
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if(agreement == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindAgreementToDelete", UtilMisc.toMap("agreementId", agreementId), locale));
			}
			agreement.set("thruDate", UtilDateTime.nowTimestamp());
			agreement.set("statusId", "AGREEMENT_CANCELLED");
			agreement.store();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createAgreementTerm(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue agreementTerm = delegator.makeValue("AgreementTerm");
		String agreementTermId = (String)context.get("agreementTermId");		
		agreementTerm.setAllFields(context, false, null, null);
		if(agreementTermId == null){
			agreementTermId = delegator.getNextSeqId("AgreementTerm");
			agreementTerm.set("agreementTermId", agreementTermId);
		}
		try {
			agreementTerm.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String,Object> createAgrDuration(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		String uomId = (String)context.get("uomId");
		Long periodLength = (Long)context.get("periodLength");
		try {
			Long periodLengthCompare = periodLength; 
			if("TF_yr".equals(uomId)){
				periodLengthCompare *= 12;
			}
			List<GenericValue> kpiAssessmentPeriodList = delegator.findByAnd("PeriodType", UtilMisc.toMap("groupPeriodTypeId", "AGREEMENT_PERIOD"), null, false);
			for(GenericValue kpiAssessmentPeriod: kpiAssessmentPeriodList){
				Long tempPeriodLength = kpiAssessmentPeriod.getLong("periodLength");
				String tempUomId = kpiAssessmentPeriod.getString("uomId");
				if("TF_yr".equals(tempUomId)){
					tempPeriodLength *= 12;
				}
				if(tempPeriodLength == periodLengthCompare){
					GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "AgreementPeriodIsExistsed", 
							UtilMisc.toMap("periodLength", periodLength, "uom", uom.get("abbreviation", locale)), locale));
				}
			}
			GenericValue periodType = delegator.makeValue("PeriodType");
			String periodTypeId = delegator.getNextSeqId("PeriodType");
			periodType.setNonPKFields(context);
			periodType.set("periodTypeId", periodTypeId);
			periodType.set("groupPeriodTypeId", "AGREEMENT_PERIOD");
			delegator.create(periodType);
			successResult.put("periodTypeId", periodTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> getAgrDurationList(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityFindOptions opts = new EntityFindOptions();
		List<GenericValue> listReturn = FastList.newInstance();
		opts.setDistinct(true);
		try {
			listReturn = delegator.findList("PeriodType", EntityCondition.makeCondition("groupPeriodTypeId", "AGREEMENT_PERIOD"), 
					UtilMisc.toSet("periodTypeId", "description", "periodLength", "uomId"), 
					UtilMisc.toList("description"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		successResult.put("listReturn", listReturn);
		return successResult;
	}
}
