package com.olbius.basehr.recruitment.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;

public class RecruitmentSalesServices {
	/**
	 * Lay ve danh sach cac de xuat phe duyet tuyen dung theo cac cap ASM -> RSM -> CSM
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentSalesOfferList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String monthCustomTimePeriodId = parameters.get("monthCustomTimePeriodId") != null? parameters.get("monthCustomTimePeriodId")[0] : null;
    	String yearCustomTimePeriodId = parameters.get("yearCustomTimePeriodId") != null? parameters.get("yearCustomTimePeriodId")[0] : null;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
	    	if(monthCustomTimePeriodId != null){
	    		listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", monthCustomTimePeriodId));
	    	}else if(yearCustomTimePeriodId != null){
				List<GenericValue> monthCustomTimePeriodList = delegator.findByAnd("CustomTimePeriod", 
						UtilMisc.toMap("parentPeriodId", yearCustomTimePeriodId, "periodTypeId", "MONTHLY"), null, false);
				if(UtilValidate.isEmpty(monthCustomTimePeriodList)){
					return successResult;
				}
				List<String> monthCustomTimePeriodIdList = EntityUtil.getFieldListFromEntityList(monthCustomTimePeriodList, "customTimePeriodId", true);
				listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN, monthCustomTimePeriodIdList));
	    	}else{
	    		return successResult;
	    	}
	    	List<String> orgManagedByParty = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), null, false);
	    	if(UtilValidate.isEmpty(orgManagedByParty)){
	    		return successResult;
	    	}
	    	listAllConditions.add(EntityCondition.makeCondition("partyIdAppr", EntityJoinOperator.IN, orgManagedByParty));
	    	if(UtilValidate.isEmpty(listSortFields)){
	    		listSortFields.add("-fromDate");
	    		listSortFields.add("partyIdOfferName");
	    	}
	    	listIterator = delegator.find("RecruitmentSalesOfferAndCustomTime", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    	successResult.put("listIterator", listIterator);
    	} catch (GenericEntityException e) {
    		e.printStackTrace();
    		return ServiceUtil.returnError(e.getLocalizedMessage());
    	}
		return successResult;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProposalSalesEmplRecruitment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String customTimePeriodId = parameters.get("customTimePeriodId") != null? parameters.get("customTimePeriodId")[0] : null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
		try {
			List<String> orgManagedByParty = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), null, true);
			if(UtilValidate.isEmpty(orgManagedByParty)){
				return successResult;
			}
			Map<String, Object> resultServices = dispatcher.runSync("getStatusSalesEmplProposable", UtilMisc.toMap("userLogin", userLogin, "locale", context.get("locale")));
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
			List<String> partyGroupIdList = FastList.newInstance();
			for(String partyGroupId: orgManagedByParty){
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> listDepartment = buildOrg.getAllDepartmentList(delegator);
				if(listDepartment != null){
					List<String> tempList = EntityUtil.getFieldListFromEntityList(listDepartment, "partyId", true);
					partyGroupIdList.addAll(tempList);
				}
			}
			String statusIdProposal = (String)resultServices.get("statusId");
			listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, partyGroupIdList));
			listAllConditions.add(EntityCondition.makeCondition("statusId", statusIdProposal));
			if(UtilValidate.isEmpty(listSortFields)){
	    		listSortFields.add("firstName");
	    	}
			listIterator = delegator.find("RecruitmentSalesEmplFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    	successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	
	public static Map<String, Object> proposalApprovalRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SendProposalSuccessful", locale));
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, Object> ctxMap = FastMap.newInstance();
    	List<EntityCondition> listAllConditions = FastList.newInstance();
    	List<EntityCondition> listSortFields = FastList.newInstance();
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	Map<String,String[]> parameters = FastMap.newInstance();
    	parameters.put("customTimePeriodId", new String[]{customTimePeriodId});
    	ctxMap.put("userLogin", userLogin);
    	ctxMap.put("locale", locale);
    	ctxMap.put("timeZone", context.get("timeZone"));
    	ctxMap.put("parameters", parameters);
    	ctxMap.put("listAllConditions", listAllConditions);
    	ctxMap.put("listSortFields", listSortFields);
    	ctxMap.put("opts", new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, false));
    	try {
			Map<String, Object> resultService = dispatcher.runSync("JQGetListProposalSalesEmplRecruitment", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			EntityListIterator listIterator = (EntityListIterator)resultService.get("listIterator");
			ctxMap.clear();
			ctxMap.put("userLogin", userLogin);
	    	ctxMap.put("locale", locale);
	    	ctxMap.put("timeZone", context.get("timeZone"));
	    	Map<String, Long> totalProposalMap = FastMap.newInstance();
	    	int totalEmplProposal = 0;
	    	Map<String, String> recruitmentSalesOfferMap = FastMap.newInstance();
	    	GenericValue tempGv = null;
			while((tempGv = listIterator.next()) != null){
				totalEmplProposal++;
				String partyId = tempGv.getString("partyId");
				String recruitmentPlanSalesId = tempGv.getString("recruitmentPlanSalesId");
				ctxMap.put("partyId", partyId);
				ctxMap.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
				resultService = dispatcher.runSync("updateRecruitmentSalesEmpl", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				String partyIdSUP = tempGv.getString("partyGroupId");
				String partyIdOfferNext = null;
				if(recruitmentSalesOfferMap.containsKey(partyIdSUP)){
					partyIdOfferNext = recruitmentSalesOfferMap.get(partyIdSUP);
				}else{
					Map<String, Object> tempResultServices = dispatcher.runSync("getPartyIdOfferByStatus", UtilMisc.toMap("userLogin", userLogin, "locale", locale,
																													"partyGroupId", partyIdSUP,
																													"statusId", tempGv.get("statusId")));
					if(!ServiceUtil.isSuccess(tempResultServices)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(tempResultServices));
					}
					partyIdOfferNext = (String)tempResultServices.get("partyIdOffer");
				}
				long tempTotalPropsal = 1;
				if(totalProposalMap.containsKey(partyIdOfferNext)){
					long totalPropsalExists = totalProposalMap.get(partyIdOfferNext);
					tempTotalPropsal += totalPropsalExists;
				}
				totalProposalMap.put(partyIdOfferNext, tempTotalPropsal);
			}
			listIterator.close();
			if(totalEmplProposal == 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NoSalesEmplPropsalApproval", locale));
			}
			//create RecruitmentSalesOffer
			ctxMap.clear();
			ctxMap.put("userLogin", userLogin);
	    	ctxMap.put("locale", locale);
	    	ctxMap.put("customTimePeriodId", customTimePeriodId);
			for(Map.Entry<String, Long> entry: totalProposalMap.entrySet()){
				String partyIdOffer = entry.getKey();
				Long quantityOffer = entry.getValue();
				ctxMap.put("partyIdOffer", partyIdOffer);
				ctxMap.put("quantityOffer", quantityOffer);
				resultService = dispatcher.runSync("createRecruitmentSalesOffer", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return successResult;
	}
	
	/**
	 * Lay ve danh sach salesman ma SUP de xuat len ASM duyet
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getRecruitmentSalesEmplOffer(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String recruitmentPlanSalesId = (String)context.get("recruitmentPlanSalesId");
		List<GenericValue> listReturn = null;
		if(recruitmentPlanSalesId == null){
			return retMap;
		}
		try {
			listReturn = delegator.findList("RecruitmentSalesEmplAndDetail", EntityCondition.makeCondition("recruitmentPlanSalesId", recruitmentPlanSalesId), 
					null, UtilMisc.toList("firstName"), null, false);
			retMap.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentPlanSales(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String customTimePeriodId = parameters.get("customTimePeriodId") != null? parameters.get("customTimePeriodId")[0] : null; 
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(customTimePeriodId == null || partyId == null){
    		return successResult;
    	}
    	try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
			List<String> allDeptId = FastList.newInstance();
			if(UtilValidate.isNotEmpty(allDept)){
				allDeptId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
			}
			allDeptId.add(partyId);
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-fromDate");
			}
			List<GenericValue> monthCustomTimePeriod = delegator.findByAnd("CustomTimePeriod", 
					UtilMisc.toMap("parentPeriodId", customTimePeriodId, "periodTypeId", "MONTHLY"), null, false);
			List<String> monthsCustomTimePeriod = EntityUtil.getFieldListFromEntityList(monthCustomTimePeriod, "customTimePeriodId", true);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allDeptId));
			listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN, monthsCustomTimePeriod));
			listIterator = delegator.find("RecruitmentPlanSalesAndEmpl", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> createRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String partyGroupId = (String)context.get("partyGroupId");
		try {
			String recruitmentPlanSalesId = null;
			List<GenericValue> recruitmentPlanSalesList = delegator.findByAnd("RecruitmentPlanSales", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "partyId", partyGroupId), null, false);
			if(UtilValidate.isEmpty(recruitmentPlanSalesList)){
				Map<String, Object> resultService = dispatcher.runSync("createRecruitmentPlanSales", 
						UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "partyId", partyGroupId,
									"userLogin", userLogin, "locale", context.get("locale")));
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				recruitmentPlanSalesId = (String)resultService.get("recruitmentPlanSalesId");
			}else{
				recruitmentPlanSalesId = recruitmentPlanSalesList.get(0).getString("recruitmentPlanSalesId");
			}
			GenericValue recruitmentSalesEmpl = delegator.makeValue("RecruitmentSalesEmpl");
			recruitmentSalesEmpl.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
			recruitmentSalesEmpl.put("partyId", context.get("partyId"));
			recruitmentSalesEmpl.setNonPKFields(context, false);
			recruitmentSalesEmpl.put("statusId", "RSALEEMPL_CREATED");
			delegator.create(recruitmentSalesEmpl);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createRecruitmentPlanSales(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String createdByPartyId = userLogin.getString("partyId");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(customTimePeriod == null){
				return ServiceUtil.returnError("cannot find customTimePeriod in createRecruitmentPlanSales service");
			}
			String periodName = CommonUtil.getMonthYearPeriodNameByCustomTimePeriod(delegator, customTimePeriodId);
			String recruitmentPlanSalesName = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentPlan", locale) + " " + periodName;
			GenericValue recruitmentPlanSales = delegator.makeValue("RecruitmentPlanSales");
			recruitmentPlanSales.setAllFields(context, false, null, null);
			String recruitmentPlanSalesId = delegator.getNextSeqId("RecruitmentPlanSales");
			recruitmentPlanSales.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
			recruitmentPlanSales.put("createdByPartyId", createdByPartyId);
			recruitmentPlanSales.put("recruitmentPlanSalesName", recruitmentPlanSalesName);
			recruitmentPlanSales.put("statusId", "RECSALES_CREATED");
			delegator.create(recruitmentPlanSales);
			retMap.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateRecruitmentPlanSales(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String recruitmentPlanSalesId = (String)context.get("recruitmentPlanSalesId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue recruitmentPlanSales = delegator.findOne("RecruitmentPlanSales", UtilMisc.toMap("recruitmentPlanSalesId", recruitmentPlanSalesId), false);
			if(recruitmentPlanSales == null){
				return ServiceUtil.returnError("cannot find recruitment plan sales to update");
			}
			String statusId = recruitmentPlanSales.getString("statusId");
			if(!"RECSALES_CREATED".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotUpDateRecruitmentPlanBecauseStatus", UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
			String createdByPartyId = recruitmentPlanSales.getString("createdByPartyId");
			if(!userLogin.getString("partyId").equals(createdByPartyId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotUpdateUserLoginNotCreatePlan", locale));
			}
			recruitmentPlanSales.setNonPKFields(context);
			recruitmentPlanSales.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String recruitmentPlanSalesId = (String)context.get("recruitmentPlanSalesId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		try {
			GenericValue recruitmentSalesEmpl = delegator.findOne("RecruitmentSalesEmpl", UtilMisc.toMap("partyId", partyId, "recruitmentPlanSalesId", recruitmentPlanSalesId), false);
			if(recruitmentSalesEmpl == null){
				return ServiceUtil.returnError("cannot find employee in recruitment sales plan");
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getNextStatusRecruitmentSalesEmpl", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("getNextStatusRecruitmentSalesEmpl", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String statusId = (String)resultService.get("newStatusId");
			recruitmentSalesEmpl.set("statusId", statusId);
			recruitmentSalesEmpl.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentSalesOffer(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String partyIdOffer = (String)context.get("partyIdOffer");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		Long quantityOffer = (Long)context.get("quantityOffer");
		try {
			List<GenericValue> checkRecruitmentSalesOfferExists = delegator.findByAnd("RecruitmentSalesOffer", 
					UtilMisc.toMap("partyIdOffer", partyIdOffer, "customTimePeriodId", customTimePeriodId), null, false);
			GenericValue recruitmentSalesOffer = null;
			String recruitmentSalesOfferId = null;
			if(UtilValidate.isNotEmpty(checkRecruitmentSalesOfferExists)){
				recruitmentSalesOffer = checkRecruitmentSalesOfferExists.get(0);
				recruitmentSalesOfferId = recruitmentSalesOffer.getString("recruitmentSalesOfferId");
			}else{
				recruitmentSalesOffer = delegator.makeValue("RecruitmentSalesOffer");
				recruitmentSalesOffer.put("partyIdOffer", partyIdOffer);
				recruitmentSalesOffer.put("customTimePeriodId", customTimePeriodId);
				GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, partyIdOffer);
				recruitmentSalesOfferId = delegator.getNextSeqId("RecruitmentSalesOffer");
				recruitmentSalesOffer.put("recruitmentSalesOfferId", recruitmentSalesOfferId);
				if(parentOrg != null){
					String partyIdAppr = parentOrg.getString("partyIdFrom");
					recruitmentSalesOffer.put("partyIdAppr", partyIdAppr);
				}
			}
			String statusId = recruitmentSalesOffer.getString("statusId");
			Long currQuantityOffer = recruitmentSalesOffer.getLong("quantityOffer");
			if(currQuantityOffer == null){
				currQuantityOffer = quantityOffer;
			}else{
				currQuantityOffer += quantityOffer;
			}
			recruitmentSalesOffer.put("quantityOffer", currQuantityOffer);
			if(statusId == null || "RECSALES_NOT_APPROVED".equals(statusId)){
				recruitmentSalesOffer.put("statusId", "RECSALES_NOT_APPROVED");
			}else{
				recruitmentSalesOffer.put("statusId", "RECSALES_ADDITIONAL");
			}
			delegator.createOrStore(recruitmentSalesOffer);
			retMap.put("recruitmentSalesOfferId", recruitmentSalesOfferId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> sendNotifyApprRecruitmentSales(DispatchContext dctx, Map<String, Object> context){
		String recruitmentSalesOfferId = (String)context.get("recruitmentSalesOfferId");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue recruitmentSalesOffer = delegator.findOne("RecruitmentSalesOffer", UtilMisc.toMap("recruitmentSalesOfferId", recruitmentSalesOfferId), false);
			String partyIdOffer = recruitmentSalesOffer.getString("partyIdOffer");
			String partyIdAppr = recruitmentSalesOffer.getString("partyIdAppr");
			List<String> partyMgrList = PartyUtil.getManagerbyOrg(partyIdAppr, delegator, UtilDateTime.nowTimestamp(), null, userLogin.getString("userLoginId"));
			String action = "ViewApprRecruitmentSalesList";
			GenericValue partyOffer = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdOffer), false); 
			String partyOfferName = partyOffer.getString("groupName"); 
			String header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "ApprRecruitmentSalesPlanNotify", UtilMisc.toMap("partyOffer", partyOfferName), locale);
			CommonUtil.sendNotify(dispatcher, locale, partyMgrList, userLogin, header, action, null);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanSalesId = parameters.get("recruitmentPlanSalesId") != null? parameters.get("recruitmentPlanSalesId")[0] : null;
    	if(recruitmentPlanSalesId == null){
    		return successResult;
    	}
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanSalesId", recruitmentPlanSalesId));
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("firstName");
    	}
    	try {
			listIterator = delegator.find("RecruitmentSalesEmplAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListOfferedSalesEmplRecruitment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentSalesOfferId = parameters.get("recruitmentSalesOfferId") != null? parameters.get("recruitmentSalesOfferId")[0] : null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(recruitmentSalesOfferId == null){
    		return successResult;
    	}
    	try {
			GenericValue recruitmentSalesOffer = delegator.findOne("RecruitmentSalesOffer", UtilMisc.toMap("recruitmentSalesOfferId", recruitmentSalesOfferId), false);
			if(recruitmentSalesOffer == null){
				return ServiceUtil.returnError("cannot find offer sales recruitment");
			}
			Map<String, Object> resultService = dispatcher.runSync("getListViewableStatusRecruitmentSalesEmpl", UtilMisc.toMap("userLogin", context.get("userLogin")));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			List<String> statusIdList = (List<String>)resultService.get("statusIdList");
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, statusIdList));
			String partyIdOffer = recruitmentSalesOffer.getString("partyIdOffer");
			String customTimePeriodId = recruitmentSalesOffer.getString("customTimePeriodId");
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyIdOffer, true, false);
			List<GenericValue> listDepartment = buildOrg.getAllDepartmentList(delegator);
			List<String> listDepartmentId = null;
			if(UtilValidate.isNotEmpty(listDepartment)){
				listDepartmentId = EntityUtil.getFieldListFromEntityList(listDepartment, "partyId", true);
			}else{
				listDepartmentId = FastList.newInstance();
			}
			listDepartmentId.add(partyIdOffer);
			listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, listDepartmentId));
			listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			listIterator = delegator.find("RecruitmentSalesEmplFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentSalesEmplSummary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	String monthCustomTimePeriodId = parameters.get("monthCustomTimePeriodId") != null? parameters.get("monthCustomTimePeriodId")[0] : null;
    	String yearCustomTimePeriodId = parameters.get("yearCustomTimePeriodId") != null? parameters.get("yearCustomTimePeriodId")[0] : null;
    	try {
	    	if(monthCustomTimePeriodId != null){
	    		listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", monthCustomTimePeriodId));
	    	}else if(yearCustomTimePeriodId != null){
				List<GenericValue> monthCustomTimePeriodList = delegator.findByAnd("CustomTimePeriod", 
							UtilMisc.toMap("parentPeriodId", yearCustomTimePeriodId, "periodTypeId", "MONTHLY"), null, false);
				if(UtilValidate.isEmpty(monthCustomTimePeriodList)){
					return successResult;
				}
				List<String> monthCustomTimePeriodIdList = EntityUtil.getFieldListFromEntityList(monthCustomTimePeriodList, "customTimePeriodId", true);
				listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN, monthCustomTimePeriodIdList));
	    	}else{
	    		return successResult;
	    	}
	    	Map<String, Object> resultService = dispatcher.runSync("getListViewableStatusRecruitmentSalesEmpl", UtilMisc.toMap("userLogin", context.get("userLogin")));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			List<String> statusIdList = (List<String>)resultService.get("statusIdList");
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, statusIdList));
			List<String> partyList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
			List<String> listPartyGroupId = FastList.newInstance();
			for(String partyGroupId: partyList){
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
				List<String> tempList = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
				listPartyGroupId.addAll(tempList);
			}
			listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, listPartyGroupId));
			listIterator = delegator.find("RecruitmentSalesEmplFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
    	} catch (GenericEntityException e) {
    		e.printStackTrace();
    		return ServiceUtil.returnError(e.getLocalizedMessage());
    	} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplSalesRecruited(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//LocalDispatcher dispatcher = dctx.getDispatcher();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	String monthCustomTimePeriodId = parameters.get("monthCustomTimePeriodId") != null? parameters.get("monthCustomTimePeriodId")[0] : null;
    	String yearCustomTimePeriodId = parameters.get("yearCustomTimePeriodId") != null? parameters.get("yearCustomTimePeriodId")[0] : null;
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null;
    	try {
	    	if(monthCustomTimePeriodId != null){
	    		listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", monthCustomTimePeriodId));
	    	}else if(yearCustomTimePeriodId != null){
				List<GenericValue> monthCustomTimePeriodList;
				monthCustomTimePeriodList = delegator.findByAnd("CustomTimePeriod", 
							UtilMisc.toMap("parentPeriodId", yearCustomTimePeriodId, "periodTypeId", "MONTHLY"), null, false);
				if(UtilValidate.isEmpty(monthCustomTimePeriodList)){
					return successResult;
				}
				List<String> monthCustomTimePeriodIdList = EntityUtil.getFieldListFromEntityList(monthCustomTimePeriodList, "customTimePeriodId", true);
				listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN, monthCustomTimePeriodIdList));
	    	}else{
	    		return successResult;
	    	}
	    	Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
	    	List<GenericValue> listDept = buildOrg.getAllDepartmentList(delegator);
	    	List<String> listDeptId = null;
	    	if(listDept == null){
	    		listDeptId = FastList.newInstance();
	    	}else{
	    		listDeptId = EntityUtil.getFieldListFromEntityList(listDept, "partyId", true);
	    	}
	    	listDeptId.add(partyId);
	    	listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, listDeptId));
	    	listAllConditions.add(EntityCondition.makeCondition("statusId", "RSALEEMPL_CSM_ACCEPT"));
	    	listIterator = delegator.find("RecruitmentSalesEmplFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
    	} catch (GenericEntityException e) {
    		e.printStackTrace();
    		return ServiceUtil.returnError(e.getLocalizedMessage());
    	}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentSalesEmplListNotAppr(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String recruitmentSalesOfferId = (String)context.get("recruitmentSalesOfferId");
		try {
			GenericValue recruitmentSalesOffer = delegator.findOne("RecruitmentSalesOffer", UtilMisc.toMap("recruitmentSalesOfferId", recruitmentSalesOfferId), false);
			if(recruitmentSalesOffer == null){
				return ServiceUtil.returnError("cannot find offer sales recruitment");
			}
			Map<String, Object> resultService = dispatcher.runSync("getListRecruitSalesEmplNotApprStatus", 
					UtilMisc.toMap("userLogin", context.get("userLogin"), "locale", context.get("locale")));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			List<String> statusIdList = (List<String>)resultService.get("statusIdList");
			List<EntityCondition> listAllConditions = FastList.newInstance();
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, statusIdList));
			String partyIdOffer = recruitmentSalesOffer.getString("partyIdOffer");
			String customTimePeriodId = recruitmentSalesOffer.getString("customTimePeriodId");
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyIdOffer, true, false);
			List<GenericValue> listDepartment = buildOrg.getAllDepartmentList(delegator);
			List<String> listDepartmentId = null;
			if(UtilValidate.isNotEmpty(listDepartment)){
				listDepartmentId = EntityUtil.getFieldListFromEntityList(listDepartment, "partyId", true);
			}else{
				listDepartmentId = FastList.newInstance();
			}
			listDepartmentId.add(partyIdOffer);
			listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, listDepartmentId));
			listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			Set<String> listSelectedField = FastSet.newInstance();
			listSelectedField.add("partyId");
			listSelectedField.add("recruitmentPlanSalesId");
			listSelectedField.add("partyCode");
			listSelectedField.add("fullName");
			listSelectedField.add("emplPositionTypeId");
			listSelectedField.add("emplPositionTypeDesc");
			listSelectedField.add("enumRecruitmentTypeId");
			listSelectedField.add("statusId");
			List<GenericValue> listEmplNotAppr = delegator.findList("RecruitmentSalesEmplFullDetail", 
					EntityCondition.makeCondition(listAllConditions), listSelectedField, UtilMisc.toList("firstName"), null, false);
			retMap.put("listReturn", listEmplNotAppr);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> approvalRecruitmentSalesEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String recruitmentSalesOfferId = (String)context.get("recruitmentSalesOfferId");
		List<String> listPartyAccepted = (List<String>)context.get("listPartyAccepted");
		try {
			GenericValue recruitmentSalesOffer = delegator.findOne("RecruitmentSalesOffer", UtilMisc.toMap("recruitmentSalesOfferId", recruitmentSalesOfferId), false);
			if(recruitmentSalesOffer == null){
				return ServiceUtil.returnError("cannot find sales recruiment offer");
			}
			String currentStatusId = recruitmentSalesOffer.getString("statusId");
			if("RECSALES_APPROVED".equals(currentStatusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentSalesOfferIsApproved", locale));
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getRecruitmentSalesEmplListNotAppr", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("getRecruitmentSalesEmplListNotAppr", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			List<GenericValue> listNotAppr = (List<GenericValue>)resultService.get("listReturn");
			ctxMap.clear();
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("locale", locale);
			for(GenericValue partyNotAppr: listNotAppr){
				String approvalType = PropertiesUtil.APPR_ACCEPT;
				String partyId = partyNotAppr.getString("partyId");
				String recruitmentPlanSalesId = partyNotAppr.getString("recruitmentPlanSalesId");
				if(!listPartyAccepted.contains(partyId)){
					approvalType = PropertiesUtil.APPR_REJECT;
				}
				ctxMap.put("partyId", partyId);
				ctxMap.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
				ctxMap.put("approvalType", approvalType);
				resultService = dispatcher.runSync("updateRecruitmentSalesEmpl", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			recruitmentSalesOffer.set("statusId", "RECSALES_APPROVED");
			recruitmentSalesOffer.store();
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
		return ServiceUtil.returnSuccess();
	}
}
