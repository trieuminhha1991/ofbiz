package com.olbius.basehr.bonusPolicy.worker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyUtil;

public class BonusPolicyWorker {
	public static Map<String, Object> createDistributorBonusPolicyRule(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale,
			TimeZone timeZone, String salesBonusPolicyId, JSONArray dataJson, String ruleEnumId, String actionEnumId) throws GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("salesBonusPolicyId", salesBonusPolicyId);
		context.put("ruleEnumId", ruleEnumId);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		Map<String, Object> resultService = null;
		for(int i = 0; i < dataJson.size(); i++){
			JSONObject jsonObj = dataJson.getJSONObject(i);
			String valueFrom = jsonObj.has("valueFrom")? jsonObj.getString("valueFrom") : null;
			String operatorEnumIdFrom = jsonObj.has("operatorEnumIdFrom")? jsonObj.getString("operatorEnumIdFrom") : null;
			String valueTo = jsonObj.has("valueTo")? jsonObj.getString("valueTo") : null;
			String operatorEnumIdTo = jsonObj.has("operatorEnumIdTo")? jsonObj.getString("operatorEnumIdTo") : null;
			String actionValue = jsonObj.has("actionValue")? jsonObj.getString("actionValue") : null;
			resultService = dispatcher.runSync("createSalesBonusPolicyRule", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return resultService;
			}
			String salesBonusPolicyRuleId = (String)resultService.get("ruleId");
			resultService = createDistributorBonusPolicyCond(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId, salesBonusPolicyRuleId, 
					null, valueFrom, operatorEnumIdFrom);
			if(!ServiceUtil.isSuccess(resultService)){
				return resultService;
			}
			resultService = createDistributorBonusPolicyCond(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId, salesBonusPolicyRuleId, 
					null, valueTo, operatorEnumIdTo);
			if(!ServiceUtil.isSuccess(resultService)){
				return resultService;
			}
			if(actionValue != null){
				BigDecimal quantity = new BigDecimal(actionValue);
				resultService = createDistributorBonusPolicyAct(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId, salesBonusPolicyRuleId, 
						actionEnumId, null, quantity);
			}
		}
		return resultService;
	}

	public static Map<String, Object> createDistributorBonusPolicyAct(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale,
			TimeZone timeZone, String salesBonusPolicyId, String ruleId, String actionEnumId, BigDecimal amount, BigDecimal quantity) throws GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("salesBonusPolicyId", salesBonusPolicyId);
		context.put("ruleId", ruleId);
		context.put("actionEnumId", actionEnumId);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		context.put("amount", amount);
		context.put("quantity", quantity);
		Map<String, Object> resultService = dispatcher.runSync("createSalesBonusPolicyAction", context);
		return resultService;
	}

	public static Map<String, Object> createDistributorBonusPolicyCond(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale,
			TimeZone timeZone, String salesBonusPolicyId, String ruleId, String inputParamEnumId, String condValueStr, String operatorEnumId) throws GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("salesBonusPolicyId", salesBonusPolicyId);
		context.put("ruleId", ruleId);
		context.put("inputParamEnumId", inputParamEnumId);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		Map<String, Object> resultService = null;
		if(condValueStr != null && operatorEnumId != null){
			context.put("condValue", new BigDecimal(condValueStr));
			context.put("operatorEnumId", operatorEnumId);
			resultService = dispatcher.runSync("createSalesBonusPolicyCond", context);
		}
		return resultService;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addPartyToSalesBonusSummary(LocalDispatcher dispatcher, Delegator delegator,
			GenericValue userLogin, Locale locale, TimeZone timeZone, String salesBonusSummaryId, String customTimePeriodId, 
			String salesBonusPolicyId) throws GenericEntityException, GenericServiceException{
		List<EntityCondition> conds = FastList.newInstance();
		String currentOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
		conds.add(EntityCondition.makeCondition("parentSalesStatementId", EntityJoinOperator.NOT_EQUAL, null));
		conds.add(EntityCondition.makeCondition("statusId", "SALES_SM_APPROVED"));
		conds.add(EntityCondition.makeCondition("organizationPartyId", currentOrgId));
		conds.add(EntityCondition.makeCondition("salesStatementTypeId", "SALES_IN"));
		List<GenericValue> salesStatementList = delegator.findList("SalesStatement", EntityCondition.makeCondition(conds), null, null, null, false);
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("timeZone", timeZone);
		context.put("salesBonusSummaryId", salesBonusSummaryId);
		Map<String, Object> resultService = null;
		for(GenericValue salesStatement: salesStatementList){
			context.remove("amount");
			context.remove("salesBonusTypeId");
			context.remove("ratioAchieve");
			String partyId = salesStatement.getString("internalPartyId");
			String salesStatementId = salesStatement.getString("salesStatementId");
			context.put("partyId", partyId);
			context.put("salesStatementId", salesStatementId);
			resultService = dispatcher.runSync("createSalesBonusSummaryParty", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return resultService;
			}
			Map<String, Object> bonusAmount = getDistributorBonusAmount(delegator, salesStatementId, salesBonusPolicyId);
			context.remove("salesStatementId");
			for(Map.Entry<String, Object> entry: bonusAmount.entrySet()){
				String salesBonusTypeId = entry.getKey();
				Map<String, Object> amountMap = (Map<String, Object>)entry.getValue();
				context.put("amount", amountMap.get("amount"));
				context.put("ratioAchieve", amountMap.get("ratioAchieve"));
				context.put("salesBonusTypeId", salesBonusTypeId);
				resultService = dispatcher.runSync("createSalesBonusSummaryPartyAmount", context);
				if(!ServiceUtil.isSuccess(resultService)){
					return resultService;
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> getDistributorBonusAmount(Delegator delegator, String salesStatementId, String salesBonusPolicyId) throws GenericEntityException {
		List<GenericValue> salesStatementDetailList = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", salesStatementId), null, false);
		BigDecimal totalActualTurnoverAmount = BigDecimal.ZERO, totalTargerTurnoverAmount = BigDecimal.ZERO;
		BigDecimal percentSKUCompletion = null;
		for(GenericValue salesStatementDetail: salesStatementDetailList){
			BigDecimal targetQuantity = salesStatementDetail.get("quantity") != null? salesStatementDetail.getBigDecimal("quantity") : BigDecimal.ONE;
			BigDecimal targetAmount = salesStatementDetail.get("amount") != null? salesStatementDetail.getBigDecimal("amount") : BigDecimal.ZERO;
			BigDecimal actualQuantity = salesStatementDetail.get("actualQuantity") != null? salesStatementDetail.getBigDecimal("actualQuantity") : BigDecimal.ZERO;
			BigDecimal actualAmount = salesStatementDetail.get("actualAmount") != null? salesStatementDetail.getBigDecimal("actualAmount") : BigDecimal.ZERO;
			totalActualTurnoverAmount = totalActualTurnoverAmount.add(actualAmount);
			totalTargerTurnoverAmount = totalTargerTurnoverAmount.add(targetAmount);
			BigDecimal percentQtyProduct = actualQuantity.multiply(new BigDecimal(100)).divide(targetQuantity, 2, RoundingMode.HALF_UP);
			percentSKUCompletion = percentSKUCompletion != null? (percentSKUCompletion.compareTo(percentQtyProduct) < 0? percentSKUCompletion: percentQtyProduct)
																	: percentQtyProduct;
		}
		if(percentSKUCompletion == null){
			percentSKUCompletion = BigDecimal.ZERO;
		}
		BigDecimal skuBonusAmount = BonusPolicyWorker.performSalesBonusPolicyRule(delegator, salesBonusPolicyId, "DIST_SKU", percentSKUCompletion, totalActualTurnoverAmount, totalTargerTurnoverAmount);
		if(totalTargerTurnoverAmount.compareTo(BigDecimal.ZERO) == 0){
			totalTargerTurnoverAmount = BigDecimal.ONE;
		}
		BigDecimal ratio = totalActualTurnoverAmount.multiply(new BigDecimal(100)).divide(totalTargerTurnoverAmount, 2, RoundingMode.HALF_UP);
		BigDecimal turnoverBonusAmount = BonusPolicyWorker.performSalesBonusPolicyRule(delegator, salesBonusPolicyId, "DIST_TURNOVER", ratio, totalActualTurnoverAmount, totalTargerTurnoverAmount);
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("DIST_SKU_BONUS", UtilMisc.toMap("amount", skuBonusAmount, "ratioAchieve", percentSKUCompletion));
		retMap.put("DIST_TURNOVER_BONUS", UtilMisc.toMap("amount", turnoverBonusAmount, "ratioAchieve", ratio));
		return retMap;
	}

	public static BigDecimal performSalesBonusPolicyRule(Delegator delegator,String salesBonusPolicyId, String ruleEnumId,
			BigDecimal percentComplete, BigDecimal actualAmount, BigDecimal targerAmount) throws GenericEntityException {
		List<GenericValue> salesBonusPolicyRuleList = delegator.findByAnd("SalesBonusPolicyRule", 
				UtilMisc.toMap("salesBonusPolicyId", salesBonusPolicyId, "ruleEnumId", ruleEnumId), null, false);
		if(UtilValidate.isEmpty(salesBonusPolicyRuleList)){
			return BigDecimal.ZERO;
		}
		BigDecimal actionValue = null;
		for(GenericValue rule: salesBonusPolicyRuleList){
			actionValue = BigDecimal.ZERO;
			String ruleId = rule.getString("ruleId");
			List<GenericValue> salesBonusPolicyConds = delegator.findByAnd("SalesBonusPolicyCond", 
					UtilMisc.toMap("salesBonusPolicyId", salesBonusPolicyId, "ruleId", ruleId), 
					UtilMisc.toList("condSeqId"), false);
			boolean performActions = true;
			for(GenericValue salesBonusPolicyCond: salesBonusPolicyConds){
				boolean condResult = checkSalesBonusPolicyCondition(salesBonusPolicyCond, percentComplete);
				if(!condResult){
					performActions = false;
					break;
				}
			}
			if(performActions){
				List<GenericValue> salesBonusPolicyActs = delegator.findByAnd("SalesBonusPolicyAct", 
						UtilMisc.toMap("salesBonusPolicyId", salesBonusPolicyId, "ruleId", ruleId), UtilMisc.toList("actSeqId"), false);
				for(GenericValue salesBonusPolicyAct: salesBonusPolicyActs){
					BigDecimal temp = performSalesBonusPolicyAction(salesBonusPolicyAct, actualAmount);
					if(temp != null){
						actionValue = actionValue.add(temp);
					}
				}
			}
		}
		return actionValue;
	}

	protected static BigDecimal performSalesBonusPolicyAction(GenericValue salesBonusPolicyAct, BigDecimal amount) {
		String actionEnumId = salesBonusPolicyAct.getString("actionEnumId");
		if("SBPACT_ACTUAL_PERCENT".equals(actionEnumId)){
			BigDecimal quantity = salesBonusPolicyAct.getBigDecimal("quantity");
			if(quantity != null){
				BigDecimal tempValue = quantity.multiply(amount).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);
				return tempValue;
			}
		}
		return BigDecimal.ZERO;
	}

	protected static boolean checkSalesBonusPolicyCondition(GenericValue salesBonusPolicyCond, BigDecimal percentComplete) {
		BigDecimal condValue = salesBonusPolicyCond.getBigDecimal("condValue");
		String operatorEnumId = salesBonusPolicyCond.getString("operatorEnumId");
		Integer compareBase = null;
		compareBase = percentComplete.compareTo(condValue);
		if(compareBase != null){
			int compare = compareBase.intValue();
			if("BPC_EQ".equals(operatorEnumId)){
                if (compare == 0) return true;
            }else if("BPC_LT".equals(operatorEnumId)){
                if(compare < 0) return true;
            }else if("BPC_LTE".equals(operatorEnumId)){
                if(compare <= 0) return true;
            }else if ("BPC_GT".equals(operatorEnumId)){
                if(compare > 0) return true;
            }else if ("BPC_GTE".equals(operatorEnumId)){
                if(compare >= 0) return true;
            }
		}
		return false;
	}

	public static Map<String, Object> editSalePolicyCond(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale,
			TimeZone timeZone, String salesBonusPolicyId, String ruleId, String condSeqId, String operatorEnumId, String value) throws GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		Map<String, Object> resultService = null;
		context.put("salesBonusPolicyId", salesBonusPolicyId);
		context.put("ruleId", ruleId);
		context.put("condSeqId", condSeqId);
		context.put("userLogin", userLogin);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		if(condSeqId != null && condSeqId.length() > 0){
			context.put("condSeqId", condSeqId);
			if(value != null && operatorEnumId != null && value.length() > 0 && operatorEnumId.length() > 0){
				context.put("operatorEnumId", operatorEnumId);
				context.put("condValue", new BigDecimal(value));
				resultService = dispatcher.runSync("updateSalesBonusPolicyCond", context);
			}else{
				resultService = dispatcher.runSync("deleteSalesBonusPolicyCond", context);
			}
		}else if(value != null && operatorEnumId != null && value.length() > 0 && operatorEnumId.length() > 0){
			context.put("operatorEnumId", operatorEnumId);
			context.put("condValue", new BigDecimal(value));
			resultService = dispatcher.runSync("createSalesBonusPolicyCond", context);
		}
		return resultService;
	}

	public static Map<String, Object> editSalePolicyAction(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale,
			TimeZone timeZone, String salesBonusPolicyId, String ruleId, String actSeqId, BigDecimal amount, BigDecimal quantity) throws GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		Map<String, Object> resultService = FastMap.newInstance();
		context.put("salesBonusPolicyId", salesBonusPolicyId);
		context.put("ruleId", ruleId);
		context.put("userLogin", userLogin);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		context.put("amount", amount);
		context.put("quantity", quantity);
		context.put("actSeqId", actSeqId);
		resultService = dispatcher.runSync("updateSalesBonusPolicyAction", context);
		return resultService;
	}

}
