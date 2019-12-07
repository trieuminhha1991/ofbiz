package com.olbius.basehr.bonusPolicy.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.bonusPolicy.worker.BonusPolicyWorker;
import com.olbius.basehr.util.CommonUtil;

public class BonusPolicyServices {
	public static Map<String, Object> createSalesBonusPolicy(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue salesBonusPolicy = delegator.makeValue("SalesBonusPolicy");
		salesBonusPolicy.setNonPKFields(context);
		String salesBonusPolicyId = delegator.getNextSeqId("SalesBonusPolicy");
		salesBonusPolicy.put("salesBonusPolicyId", salesBonusPolicyId);
		salesBonusPolicy.put("createdDate", UtilDateTime.nowTimestamp());
		try {
			delegator.create(salesBonusPolicy);
			retMap.put("salesBonusPolicyId", salesBonusPolicyId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createSalesBonusPolicyRule(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(); 
		GenericValue salesBonusPolicyRule = delegator.makeValue("SalesBonusPolicyRule");
		salesBonusPolicyRule.setAllFields(context, false, null, null);
		delegator.setNextSubSeqId(salesBonusPolicyRule, "ruleId", 2, 1);
		try {
			delegator.create(salesBonusPolicyRule);
			String salesBonusPolicyRuleId = salesBonusPolicyRule.getString("ruleId");
			retMap.put("ruleId", salesBonusPolicyRuleId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createSalesBonusPolicyCond(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue salesBonusPolicyCond = delegator.makeValue("SalesBonusPolicyCond");
		salesBonusPolicyCond.setAllFields(context, false, null, null);
		delegator.setNextSubSeqId(salesBonusPolicyCond, "condSeqId", 2, 1);
		try {
			delegator.create(salesBonusPolicyCond);
			String salesBonusPolicyCondSeqId = salesBonusPolicyCond.getString("condSeqId");
			retMap.put("condSeqId", salesBonusPolicyCondSeqId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> updateSalesBonusPolicyCond(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue salesBonusPolicyCond = delegator.makeValue("SalesBonusPolicyCond");
		salesBonusPolicyCond.setAllFields(context, false, null, null);
		try {
			delegator.store(salesBonusPolicyCond);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> deleteSalesBonusPolicyCond(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue salesBonusPolicyCond = delegator.makeValue("SalesBonusPolicyCond");
		salesBonusPolicyCond.setPKFields(context);
		try {
			salesBonusPolicyCond.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createSalesBonusPolicyAction(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			GenericValue salesBonusPolicyAction = delegator.makeValue("SalesBonusPolicyAct");
			salesBonusPolicyAction.setAllFields(context, false, null, null);
			delegator.setNextSubSeqId(salesBonusPolicyAction, "actSeqId", 2, 1);
			delegator.create(salesBonusPolicyAction);
			String salesBonusPolicyActSeqId = salesBonusPolicyAction.getString("actSeqId");
			retMap.put("actSeqId", salesBonusPolicyActSeqId);
		}catch(GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> updateSalesBonusPolicyAction(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			GenericValue salesBonusPolicyAction = delegator.makeValue("SalesBonusPolicyAct");
			salesBonusPolicyAction.setAllFields(context, false, null, null);
			delegator.store(salesBonusPolicyAction);
		}catch(GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListSalesBonusSummaryJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String salesBonusSummaryTypeId = parameters.get("salesBonusSummaryTypeId") != null? ((String[])parameters.get("salesBonusSummaryTypeId"))[0] : null; 
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-thruDate");
		}
		try {
			if(salesBonusSummaryTypeId != null){
				listAllConditions.add(EntityCondition.makeCondition("salesBonusSummaryTypeId", salesBonusSummaryTypeId));
				listIterator = delegator.find("SalesBonusSummaryAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				successResult.put("listIterator", listIterator);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListDistributorBonusSummaryPartyJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String salesBonusSummaryId = parameters.get("salesBonusSummaryId") != null? ((String[])parameters.get("salesBonusSummaryId"))[0] : null; 
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("groupName");
		}
		try {
			if(salesBonusSummaryId != null){
				listAllConditions.add(EntityCondition.makeCondition("salesBonusSummaryId", salesBonusSummaryId));
				listIterator = delegator.find("DistBonusSummaryPartyAndAmount", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				successResult.put("listIterator", listIterator);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> createSalesBonusSummary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue salesBonusSummary = delegator.makeValue("SalesBonusSummary");
		salesBonusSummary.setNonPKFields(context);
		String salesBonusSummaryId = delegator.getNextSeqId("SalesBonusSummary");
		salesBonusSummary.put("salesBonusSummaryId", salesBonusSummaryId);
		try {
			delegator.create(salesBonusSummary);
			successResult.put("salesBonusSummaryId", salesBonusSummaryId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> createSalesBonusSummaryParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue salesBonusSummaryParty = delegator.makeValue("SalesBonusSummaryParty");
		salesBonusSummaryParty.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(salesBonusSummaryParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createSalesBonusSummaryPartyAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue salesBonusSummaryPartyAmount = delegator.makeValue("SalesBonusSummaryPartyAmount");
		salesBonusSummaryPartyAmount.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(salesBonusSummaryPartyAmount);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> addPartyToSalesBonusSummary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String salesBonusSummaryId = (String)context.get("salesBonusSummaryId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		try {
			GenericValue salesBonusSummary = delegator.findOne("SalesBonusSummary", UtilMisc.toMap("salesBonusSummaryId", salesBonusSummaryId), false);
			if(salesBonusSummary == null){
				return ServiceUtil.returnError("cannot find summary have id: " + salesBonusSummaryId);
			}
			String salesBonusSummaryTypeId = salesBonusSummary.getString("salesBonusSummaryTypeId");
			if(salesBonusSummaryTypeId == null){
				return ServiceUtil.returnError("Bonus summary have no type");
			}
			Map<String, Object> resultService = null;
			String customTimePeriodId = salesBonusSummary.getString("customTimePeriodId");
			String salesBonusPolicyId = salesBonusSummary.getString("salesBonusPolicyId");
			switch (salesBonusSummaryTypeId) {
				case "BONUS_SUMMARY_DISTRIBUTOR":
					resultService = BonusPolicyWorker.addPartyToSalesBonusSummary(dispatcher, delegator, userLogin, locale, timeZone, 
							salesBonusSummaryId, customTimePeriodId, salesBonusPolicyId);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
					break;
				default:
					break;
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
	public static Map<String, Object> updateSalesBonusSummaryData(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String salesBonusSummaryId = (String)context.get("salesBonusSummaryId");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("timeZone", timeZone);
		ctxMap.put("locale", locale);
		ctxMap.put("salesBonusSummaryId", salesBonusSummaryId);
		try {
			Map<String, Object> resultService = dispatcher.runSync("addPartyToSalesBonusSummary", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	public static Map<String, Object> deleteSalesBonusSummaryParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String salesBonusSummaryId = (String)context.get("salesBonusSummaryId");
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		try {
			Map<String, Object> mapConds = UtilMisc.toMap("salesBonusSummaryId", salesBonusSummaryId, "partyId", partyId); 
			List<GenericValue> summaryPartyAmountList = delegator.findByAnd("SalesBonusSummaryPartyAmount", mapConds, null, false);
			GenericValue summaryParty = delegator.findOne("SalesBonusSummaryParty", mapConds, false);
			if(summaryParty == null){
				return ServiceUtil.returnError("Cannot find party in summary to delete");
			}
			for(GenericValue tempGv: summaryPartyAmountList){
				tempGv.remove();
			}
			summaryParty.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	public static Map<String, Object> deleteSalesBonusPolicyRule(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String salesBonusPolicyId = (String)context.get("salesBonusPolicyId");
		String ruleId = (String)context.get("ruleId");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> mapConds = UtilMisc.toMap("salesBonusPolicyId", salesBonusPolicyId, "ruleId", ruleId);
		try {
			List<GenericValue> actList = delegator.findByAnd("SalesBonusPolicyAct", mapConds, null, false);
			List<GenericValue> condList = delegator.findByAnd("SalesBonusPolicyCond", mapConds, null, false);
			GenericValue rule = delegator.findOne("SalesBonusPolicyRule", mapConds, false);
			for(GenericValue tempGv: actList){
				tempGv.remove();
			}
			for(GenericValue tempGv: condList){
				tempGv.remove();
			}
			rule.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
}
