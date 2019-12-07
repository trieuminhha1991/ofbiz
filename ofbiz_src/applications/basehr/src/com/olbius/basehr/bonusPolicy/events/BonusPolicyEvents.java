package com.olbius.basehr.bonusPolicy.events;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.bonusPolicy.worker.BonusPolicyWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.EntityConditionUtils;

public class BonusPolicyEvents {
	public static String createDistributorBonusPolicy(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null;
		try{
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
			if(thruDateStr != null){
				thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			}
		}catch(NumberFormatException e){
			fromDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		}
		try{
			try{
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createSalesBonusPolicy", paramMap, userLogin, timeZone, locale);
				context.put("fromDate", UtilDateTime.getDayStart(fromDate));
				context.put("thruDate", thruDate);
				TransactionUtil.begin();
				context.put("salesBonusPolicyTypeId", "DISTRIBUTOR_POLICY");
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				Map<String, Object> resultService = dispatcher.runSync("createSalesBonusPolicy", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String salesBonusPolicyId = (String)resultService.get("salesBonusPolicyId");
				String turnoverDataParam = request.getParameter("turnoverData");
				String skuData = request.getParameter("skuData");
				if(turnoverDataParam != null){
					JSONArray turnoverDataJson = JSONArray.fromObject(turnoverDataParam);
					resultService = BonusPolicyWorker.createDistributorBonusPolicyRule(dispatcher, userLogin, locale, timeZone, 
							salesBonusPolicyId, turnoverDataJson, "DIST_TURNOVER", "SBPACT_ACTUAL_PERCENT"); 
					if(!ServiceUtil.isSuccess(resultService)){
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						return "error";
					}
				}
				if(skuData != null){
					JSONArray skuDataJson = JSONArray.fromObject(skuData);
					resultService = BonusPolicyWorker.createDistributorBonusPolicyRule(dispatcher, userLogin, locale, timeZone, 
							salesBonusPolicyId, skuDataJson, "DIST_SKU", "SBPACT_ACTUAL_PERCENT"); 
					if(!ServiceUtil.isSuccess(resultService)){
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GenericServiceException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String addRuleToSalesBonusPolicy(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String salesBonusPolicyId = request.getParameter("salesBonusPolicyId");
		String ruleDataParam = request.getParameter("ruleData");
		String ruleEnumId = request.getParameter("ruleEnumId");
		Map<String, Object> resultService = null;
		try {
			if(ruleDataParam != null){
				JSONArray ruleDataJson = JSONArray.fromObject(ruleDataParam);
				resultService = BonusPolicyWorker.createDistributorBonusPolicyRule(dispatcher, userLogin, locale, timeZone, 
					salesBonusPolicyId, ruleDataJson, ruleEnumId, "SBPACT_ACTUAL_PERCENT");
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} 
		return "success";
	}
	
	public static String getSalesPolicyRuleAndCondAndAct(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		/*GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Security security = (Security) request.getAttribute("security");
		if(!security.hasEntityPermission("HR_KPIPERF", "_VIEW", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}*/
		String salesBonusPolicyId = request.getParameter("salesBonusPolicyId");
		try {
			List<GenericValue> listPerfCriteriaRule = delegator.findByAnd("SalesBonusPolicyRuleAndEnum", UtilMisc.toMap("salesBonusPolicyId", salesBonusPolicyId), UtilMisc.toList("sequenceId","ruleId"), false);
			Map<String, Object> results = FastMap.newInstance();
			for(GenericValue perfCriteriaRule: listPerfCriteriaRule){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("ruleEnumId", perfCriteriaRule.get("ruleEnumId"));
				String salesBonusPolicyRuleId = perfCriteriaRule.getString("ruleId");
				EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("salesBonusPolicyId", salesBonusPolicyId),
						EntityCondition.makeCondition("ruleId", salesBonusPolicyRuleId));
				List<GenericValue> salesBonusPolicyCond = delegator.findList("SalesBonusPolicyCond", conds, UtilMisc.toSet("salesBonusPolicyId", "ruleId", "condSeqId", "inputParamEnumId", "operatorEnumId", "condValue"), 
																							UtilMisc.toList("condSeqId"), null, false);
				List<GenericValue> salesBonusPolicyAction = delegator.findList("SalesBonusPolicyAct", conds, UtilMisc.toSet("salesBonusPolicyId", "ruleId", "actSeqId", "actionEnumId", "quantity", "amount"), 
						UtilMisc.toList("actSeqId"), null, false);
				tempMap.put("condition", salesBonusPolicyCond);
				tempMap.put("action", salesBonusPolicyAction);
				results.put(salesBonusPolicyRuleId, tempMap);
			}
			request.setAttribute("results", results);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String getDistributorBonusPolicyByCustomTimePeriod(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(customTimePeriod != null){
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("salesBonusPolicyTypeId", "DISTRIBUTOR_POLICY"));
				conds.add(EntityConditionUtils.makeDateConds(fromDateTs, thruDateTs));
				List<GenericValue> salesBonusPolicyList = delegator.findList("SalesBonusPolicy", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("listReturn", salesBonusPolicyList);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String createDistributorBonusSummary(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Map<String, Object> context = FastMap.newInstance();
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		context.put("salesBonusSummaryName", request.getParameter("salesBonusSummaryName"));
		context.put("customTimePeriodId", request.getParameter("customTimePeriodId"));
		context.put("salesBonusPolicyId", request.getParameter("salesBonusPolicyId"));
		context.put("salesBonusSummaryTypeId", "BONUS_SUMMARY_DISTRIBUTOR");
		context.put("userLogin", userLogin);
		context.put("timeZone", timeZone);
		context.put("locale", locale);
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> resultService = dispatcher.runSync("createSalesBonusSummary", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String salesBonusSummaryId = (String)resultService.get("salesBonusSummaryId");
				context.clear();
				context.put("userLogin", userLogin);
				context.put("timeZone", timeZone);
				context.put("locale", locale);
				context.put("salesBonusSummaryId", salesBonusSummaryId);
				resultService = dispatcher.runSync("addPartyToSalesBonusSummary", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GenericServiceException e) {
				TransactionUtil.rollback();
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getSalePolicyCondAndActOfRule(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Security security = (Security) request.getAttribute("security");
		Locale locale = UtilHttp.getLocale(request);
		if(!security.hasEntityPermission("HR_KPIPERF", "_VIEW", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		String salesBonusPolicyId = request.getParameter("salesBonusPolicyId");
		String ruleId = request.getParameter("ruleId");
		EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("salesBonusPolicyId", salesBonusPolicyId),
				EntityCondition.makeCondition("ruleId", ruleId));
		try {
			List<GenericValue> salesBonusPolicyCond = delegator.findList("SalesBonusPolicyCond", conds, UtilMisc.toSet("salesBonusPolicyId", "ruleId", "condSeqId", "inputParamEnumId", "operatorEnumId", "condValue"), 
					UtilMisc.toList("condSeqId"), null, false);
			List<GenericValue> salesBonusPolicyAction = delegator.findList("SalesBonusPolicyAct", conds, UtilMisc.toSet("salesBonusPolicyId", "ruleId", "actSeqId", "actionEnumId", "quantity", "amount"), 
					UtilMisc.toList("actSeqId"), null, false);
			request.setAttribute("condition", salesBonusPolicyCond);
			request.setAttribute("action", salesBonusPolicyAction);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String updateSalePolicyCondAndAct(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String salesBonusPolicyId = request.getParameter("salesBonusPolicyId");
		String ruleId = request.getParameter("ruleId");
		String condSeqIdFrom = request.getParameter("condSeqIdFrom");
		String condSeqIdTo = request.getParameter("condSeqIdTo");
		String valueFrom = request.getParameter("valueFrom");
		String valueTo = request.getParameter("valueTo");
		String operatorEnumIdFrom = request.getParameter("operatorEnumIdFrom");
		String operatorEnumIdTo = request.getParameter("operatorEnumIdTo");
		String actSeqId = request.getParameter("actSeqId");
		String actionValue = request.getParameter("actionValue");
		Map<String, Object> result;
		try {
			result = BonusPolicyWorker.editSalePolicyCond(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId, ruleId, 
					condSeqIdFrom, operatorEnumIdFrom, valueFrom);
			if(!ServiceUtil.isSuccess(result)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(result));
				return "error";
			}
			result = BonusPolicyWorker.editSalePolicyCond(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId, ruleId, 
					condSeqIdTo, operatorEnumIdTo, valueTo);
			if(!ServiceUtil.isSuccess(result)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(result));
				return "error";
			}
			BigDecimal quantity = BigDecimal.ZERO;
			if(actionValue != null && actionValue.length() > 0){
				quantity = new BigDecimal(actionValue);
			}
			Map<String, Object> resultService = BonusPolicyWorker.editSalePolicyAction(dispatcher, userLogin, locale, timeZone, salesBonusPolicyId,
					ruleId, actSeqId, null, quantity);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
