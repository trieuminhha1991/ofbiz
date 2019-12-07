package com.olbius.basehr.payroll.events;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.payroll.worker.PayrollWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;

public class PayrollEvents {
	public static String approvalPayrollTable(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    Delegator delegator = (Delegator)request.getAttribute("delegator");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    TimeZone timeZone = UtilHttp.getTimeZone(request);
	    Locale locale = UtilHttp.getLocale(request);
	    String requestId = request.getParameter("requestId");
	    try {
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("approvalWorkFlowRequest", ServiceUtil.setServiceFields(dispatcher, "approvalWorkFlowRequest", parameterMap, userLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resultService)){
				GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
				String processStatusId = workFlowRequest.getString("processStatusId");
				GenericValue workFlowProcessStatus = delegator.findOne("WorkFlowProcessStatus", UtilMisc.toMap("processStatusId", processStatusId), false);
				String statusId = workFlowProcessStatus.getString("statusId");
				GenericValue workFlowRequestAtt = delegator.findOne("WorkFlowRequestAttr", UtilMisc.toMap("requestId", requestId, "attrName", "payrollTableId"), false);
				if("WORK_FLOW_START".equals(statusId) && PartyUtil.isAdmin(delegator, userLogin)){
					Map<String, Object> context = FastMap.newInstance();
					context.put("userLogin", userLogin);
					context.put("requestId", requestId);
					context.put("actionTypeId", "APPROVE");
					dispatcher.runSync("approvalWorkFlowRequest", context);
				}
				String payrolStt = PayrollUtil.updatePayrollTableStatus(delegator, workFlowRequestAtt.getString("attrValue"), requestId);
				GenericValue statusPayrollGv = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", payrolStt), false);
				request.setAttribute("statusPayroll", statusPayrollGv.getString("description"));
				String ntfId = request.getParameter("ntfId");
				if(ntfId != null){
					dispatcher.runSync("updateNotification", UtilMisc.toMap("ntfId", ntfId, "userLogin", userLogin));
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));	
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getEmplPositionTypeRateGeoAppl(HttpServletRequest request, HttpServletResponse response){
		String emplPositionTypeRateId = request.getParameter("emplPositionTypeRateId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		List<String> expandIncludeGeoList = FastList.newInstance();
		List<String> expandExcludeGeoList = FastList.newInstance();
		List<String> checkIncludeGeoList = FastList.newInstance();
		List<String> checkExcludeGeoList = FastList.newInstance();
		try {
			String topGeoTypeId = EntityUtilProperties.getPropertyValue("hrolbius.properties", "top.geoTypeId.emplPositionTypeRate.geo.apply", delegator);
			List<GenericValue> includeGeoAppl = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_INCLUDE"), null, false);
			List<GenericValue> excludeGeoAppl = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_EXCLUDE"), null, false);
			for(GenericValue includeGeo: includeGeoAppl){
				GenericValue includeGeoGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeo.getString("geoId")), false);
				if(!topGeoTypeId.equals(includeGeoGv.getString("geoTypeId"))){
					CommonUtil.addGeoAssoc(delegator, includeGeoGv.getString("geoId"), expandIncludeGeoList, topGeoTypeId);
				}
				if(!checkIncludeGeoList.contains(includeGeoGv.getString("geoId"))){
					checkIncludeGeoList.add(includeGeoGv.getString("geoId"));	
				}
			}
			for(GenericValue excludeGeo: excludeGeoAppl){
				GenericValue excludeGeoGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", excludeGeo.getString("geoId")), false);
				if(!topGeoTypeId.equals(excludeGeoGv.getString("geoTypeId"))){
					CommonUtil.addGeoAssoc(delegator, excludeGeoGv.getString("geoId"), expandExcludeGeoList, topGeoTypeId);
				}
				if(!checkExcludeGeoList.contains(excludeGeoGv.getString("geoId"))){
					checkExcludeGeoList.add(excludeGeoGv.getString("geoId"));	
				}
			}
			request.setAttribute("expandIncludeGeoList", expandIncludeGeoList);
			request.setAttribute("expandExcludeGeoList", expandExcludeGeoList);
			request.setAttribute("checkIncludeGeoList", checkIncludeGeoList);
			request.setAttribute("checkExcludeGeoList", checkExcludeGeoList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getpayrollParamPositionTypeGeoAppl(HttpServletRequest request, HttpServletResponse response){
		String payrollParamPositionTypeId = request.getParameter("payrollParamPositionTypeId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		List<String> expandIncludeGeoList = FastList.newInstance();
		List<String> expandExcludeGeoList = FastList.newInstance();
		List<String> checkIncludeGeoList = FastList.newInstance();
		List<String> checkExcludeGeoList = FastList.newInstance();
		try {
			String topGeoTypeId = EntityUtilProperties.getPropertyValue("hrolbius.properties", "top.geoTypeId.emplPositionTypeRate.geo.apply", delegator);
			List<GenericValue> includeGeoAppl = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_INCLUDE"), null, false);
			List<GenericValue> excludeGeoAppl = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_EXCLUDE"), null, false);
			for(GenericValue includeGeo: includeGeoAppl){
				GenericValue includeGeoGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeo.getString("geoId")), false);
				if(!topGeoTypeId.equals(includeGeoGv.getString("geoTypeId"))){
					CommonUtil.addGeoAssoc(delegator, includeGeoGv.getString("geoId"), expandIncludeGeoList, topGeoTypeId);
				}
				if(!checkIncludeGeoList.contains(includeGeoGv.getString("geoId"))){
					checkIncludeGeoList.add(includeGeoGv.getString("geoId"));	
				}
			}
			for(GenericValue excludeGeo: excludeGeoAppl){
				GenericValue excludeGeoGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", excludeGeo.getString("geoId")), false);
				if(!topGeoTypeId.equals(excludeGeoGv.getString("geoTypeId"))){
					CommonUtil.addGeoAssoc(delegator, excludeGeoGv.getString("geoId"), expandExcludeGeoList, topGeoTypeId);
				}
				if(!checkExcludeGeoList.contains(excludeGeoGv.getString("geoId"))){
					checkExcludeGeoList.add(excludeGeoGv.getString("geoId"));	
				}
			}
			request.setAttribute("expandIncludeGeoList", expandIncludeGeoList);
			request.setAttribute("expandExcludeGeoList", expandExcludeGeoList);
			request.setAttribute("checkIncludeGeoList", checkIncludeGeoList);
			request.setAttribute("checkExcludeGeoList", checkExcludeGeoList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String updateEmplPositionTypeRateGeoAppl(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");	
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parametersMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		String rateAmountStr = request.getParameter("rateAmount");
		BigDecimal rateAmount = new BigDecimal(rateAmountStr);
		String includeGeoId = request.getParameter("includeGeoId");
		String excludeGeoId = request.getParameter("excludeGeoId");
		List<String> includeGeoList = null;
		List<String> excludeGeoList = null;
		if(includeGeoId != null){
			JSONArray includeGeoJson = JSONArray.fromObject(includeGeoId);
			includeGeoList = FastList.newInstance();
			for(int i = 0; i < includeGeoJson.size(); i++){
				includeGeoList.add(includeGeoJson.getJSONObject(i).getString("includeGeoId"));
			}
		}
		
		if(excludeGeoId != null){
			excludeGeoList = FastList.newInstance();
			JSONArray excludeGeoJson = JSONArray.fromObject(excludeGeoId);
			for(int i = 0; i < excludeGeoJson.size(); i++){
				excludeGeoList.add(excludeGeoJson.getJSONObject(i).getString("excludeGeoId"));
			}
		}
		try {
			TransactionUtil.begin();
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.putAll(parametersMap);
			serviceCtx.put("fromDate", fromDate);
			serviceCtx.put("thruDate", thruDate);
			serviceCtx.put("rateAmount", rateAmount);
			serviceCtx.put("includeGeoList", includeGeoList);
			serviceCtx.put("excludeGeoList", excludeGeoList);
			serviceCtx.put("locale", UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("updateOldEmplPositionTypeRateAndGeoAppl", 
					ServiceUtil.setServiceFields(dispatcher, "updateOldEmplPositionTypeRateAndGeoAppl", serviceCtx, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, (String)resultService.get(ModelService.SUCCESS_MESSAGE));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updatePayrollParamPosTypeGeoAppl(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");	
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parametersMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		String rateAmountStr = request.getParameter("rateAmount");
		BigDecimal rateAmount = new BigDecimal(rateAmountStr);
		String includeGeoId = request.getParameter("includeGeoId");
		String excludeGeoId = request.getParameter("excludeGeoId");
		List<String> includeGeoList = null;
		List<String> excludeGeoList = null;
		if(includeGeoId != null){
			includeGeoList = FastList.newInstance();
			JSONArray includeGeoJson = JSONArray.fromObject(includeGeoId);
			for(int i = 0; i < includeGeoJson.size(); i++){
				includeGeoList.add(includeGeoJson.getJSONObject(i).getString("includeGeoId"));
			}
		}
		
		if(excludeGeoId != null){
			excludeGeoList = FastList.newInstance();
			JSONArray excludeGeoJson = JSONArray.fromObject(excludeGeoId);
			for(int i = 0; i < excludeGeoJson.size(); i++){
				excludeGeoList.add(excludeGeoJson.getJSONObject(i).getString("excludeGeoId"));
			}
		}
		try {
			TransactionUtil.begin();			
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.putAll(parametersMap);
			serviceCtx.put("fromDate", fromDate);
			serviceCtx.put("thruDate", thruDate);
			serviceCtx.put("rateAmount", rateAmount);
			serviceCtx.put("includeGeoList", includeGeoList);
			serviceCtx.put("excludeGeoList", excludeGeoList);
			serviceCtx.put("locale", UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("updatePayrollParamPosTypeAndGeoAppl", 
					ServiceUtil.setServiceFields(dispatcher, "updatePayrollParamPosTypeAndGeoAppl", serviceCtx, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, (String)resultService.get(ModelService.SUCCESS_MESSAGE));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createBonusParamEmplSales(HttpServletRequest request, HttpServletResponse response){
		String salesCommissionParam = request.getParameter("salesCommissionId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");	
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		if(salesCommissionParam != null){
			JSONArray salesCommissionJson = JSONArray.fromObject(salesCommissionParam);
			Map<String, Object> resultService;
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "ConfirmSuccessfully", UtilHttp.getLocale(request)));
			for(int i = 0; i < salesCommissionJson.size(); i++){
				try {
					try {
						JSONObject jsonObj = salesCommissionJson.getJSONObject(i);
						String salesCommissionId = jsonObj.getString("salesCommissionId");
						GenericValue salesCommisstionData = delegator.findOne("SalesCommissionData", UtilMisc.toMap("salesCommissionId", salesCommissionId), false);
						String partyId = salesCommisstionData.getString("partyId");
						Timestamp fromDate = salesCommisstionData.getTimestamp("fromDate");
						Timestamp thruDate = salesCommisstionData.getTimestamp("thruDate");
						List<GenericValue> salesCommissionAdjs = delegator.findByAnd("SalesCommissionAdjustment", UtilMisc.toMap("salesCommissionId", salesCommissionId), null, false);
						TransactionUtil.begin();
						try {
							for(GenericValue salesCommissionAdj: salesCommissionAdjs){
								BigDecimal amount = salesCommissionAdj.getBigDecimal("amount");
								String salesPolicyId = salesCommissionAdj.getString("salesPolicyId");
								String salesPolicyRuleId = salesCommissionAdj.getString("salesPolicyRuleId");
								List<GenericValue> salesPolicyCond = delegator.findByAnd("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId), null, false);
								if(UtilValidate.isNotEmpty(salesPolicyCond)){
									String inputParamEnumId = salesPolicyCond.get(0).getString("inputParamEnumId");
									List<GenericValue> payrollParameters = delegator.findByAnd("PayrollParameters", UtilMisc.toMap("inputEnumSalesId", inputParamEnumId), null, false);
									if(UtilValidate.isNotEmpty(payrollParameters)){
										String code = payrollParameters.get(0).getString("code");
										Map<String, Object> ctxMap = FastMap.newInstance();
										ctxMap.put("code", code);
										ctxMap.put("partyId", partyId);
										ctxMap.put("fromDate", fromDate);
										ctxMap.put("thruDate", thruDate);
										ctxMap.put("value", amount.toString());
										ctxMap.put("userLogin", userLogin);
										resultService = dispatcher.runSync("createEmplPayrollParameters", ctxMap);
										if(!ServiceUtil.isSuccess(resultService)){
											throw new GenericServiceException((String)resultService.get(ModelService.ERROR_MESSAGE));
										}
									}
								}
							}
							salesCommisstionData.set("statusId", "SALES_COMM_COMPLETED");
							salesCommisstionData.store();
							TransactionUtil.commit();
						} catch (GenericServiceException e) {
							e.printStackTrace();
							TransactionUtil.rollback();
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
						}
					} catch (GenericTransactionException e) {
						e.printStackTrace();
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
					} catch (GenericEntityException e) {
						e.printStackTrace();
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
					} 
				} catch (GenericTransactionException e1) {
					e1.printStackTrace();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, e1.getLocalizedMessage());
				}
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "not found sales commission");
		}
		return "success";
	}
	
	/*public static String getEmplParamCharacteristic(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		String paramCharacteristicId = request.getParameter("paramCharacteristicId");
		Map<String, Object> resultService;
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("partyId", partyId);
		ctxMap.put("fromDate", fromDate);
		ctxMap.put("thruDate", thruDate);
		ctxMap.put("paramCharacteristicId", paramCharacteristicId);
		ctxMap.put("userLogin", userLogin);
		try {
			resultService = dispatcher.runSync("getEmplParameters", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("payrollEmplParamDetails", resultService.get("payrollEmplParam"));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}*/
	
	public static String createEmplPayrollParameters(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		Locale locale = UtilHttp.getLocale(request);
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		String partyIds = (String)parameterMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		parameterMap.put("locale", locale);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEmplPayrollParameters", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("createEmplPayrollParameters", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
			}
			if(successCreate == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CreateAllowanceForListEmplFail", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CreateAllowanceForListEmplSuccess",
					UtilMisc.toMap("successCreate", successCreate, "total", totalEmpl), locale));
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getPayrollTableDetailParty(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String payrollTableId = request.getParameter("payrollTableId");
		String fromDateStr = request.getParameter("fromDate");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(request.getParameter("pagesize"));
		int page = Integer.parseInt(request.getParameter("pagenum"));
		if(partyId != null && payrollTableId != null && fromDateStr != null){
			try {
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));	
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyId", partyId));
				conds.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
				conds.add(EntityCondition.makeCondition("fromDate", fromDate));
				conds.add(EntityCondition.makeCondition("payrollCharacteristicId", EntityJoinOperator.NOT_EQUAL, null));
				List<GenericValue> payrollTableList = delegator.findList("PayrollTableAndFormulaAndChar", 
						EntityCondition.makeCondition(conds), null, UtilMisc.toList("sequenceNum", "code"), null, false);
				int start = size * page;
				int end = start + size;
				int totalRows = payrollTableList.size();
				if(end > totalRows){
					end = totalRows;
				}
				payrollTableList = payrollTableList.subList(start, end);
				request.setAttribute("totalRows", totalRows);
				for(GenericValue tempGv: payrollTableList){
					Map<String, Object> tempMap = FastMap.newInstance();
					listReturn.add(tempMap);
					Double value;
					try{
						value = Double.parseDouble(tempGv.getString("value"));
					}catch(NumberFormatException e){
						value = 0d;
					}
					String code = tempGv.getString("code");
					//GenericValue formula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
					String payrollCharacteristicId = tempGv.getString("payrollCharacteristicId");
					tempMap.put("value", Math.round(value));
					tempMap.put("code", code);
					tempMap.put("name", tempGv.getString("name"));
					tempMap.put("sequenceNum", tempGv.get("sequenceNum"));
					tempMap.put("payrollCharacteristicId", payrollCharacteristicId);
					tempMap.put("partyId", partyId);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}
		request.setAttribute("listReturn", listReturn);
		return "success";
	}
	
	public static String updatePayrollTableOfParty(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String payrollTableId = request.getParameter("payrollTableId");
		String fromDateStr = request.getParameter("fromDate");
		String value = request.getParameter("value");
		String code = request.getParameter("code");
		if(value != null && fromDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			try {
				GenericValue payrollTable = delegator.findOne("PayrollTable", 
						UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId, "fromDate", fromDate, "code", code), false);
				BigDecimal newValue = new BigDecimal(value);
				payrollTable.set("value", newValue);
				payrollTable.store();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String getPartyFormulaInvoiceItemType(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		int pagesize = 15, pagenum = 0;
		try {
			pagesize = Integer.parseInt(request.getParameter("pagesize"));
		} catch (NumberFormatException e) {
			pagesize = 15;
		}
		try {
			pagenum = Integer.parseInt(request.getParameter("pagenum"));
		} catch (NumberFormatException e) {
			pagenum = 0;
		}
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("fromDate", fromDate);
		ctxMap.put("thruDate", thruDate);
		ctxMap.put("locale", locale);
		ctxMap.put("timeZone", timeZone);
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("pagesize", pagesize);
		ctxMap.put("pagenum", pagenum);
		ctxMap.put("partyId", partyId);
		try {
			Map<String, Object> resultService = dispatcher.runSync("getPartyFormulaInvoiceItemType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("listReturn", resultService.get("listReturn"));
				request.setAttribute("totalRows", resultService.get("totalRows"));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createPartyFormulaInvoiceItemType(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		Map<String, Object> ctxMap = FastMap.newInstance();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		ctxMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		ctxMap.put("invoiceItemTypeId", request.getParameter("invoiceItemTypeId"));
		ctxMap.put("code", request.getParameter("code"));
		ctxMap.put("partyListId", request.getParameter("partyListId"));
		ctxMap.put("locale", locale);
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createPartyFormulaInvoiceItemType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	public static String updatePartyFormulaInvoiceItemType(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		if(thruDateStr != null){
			ctxMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		ctxMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		ctxMap.put("partyId", request.getParameter("partyId"));
		ctxMap.put("code", request.getParameter("code"));
		ctxMap.put("invoiceItemTypeId", request.getParameter("invoiceItemTypeId"));
		ctxMap.put("locale", locale);
		ctxMap.put("userLogin", userLogin);
		Map<String, Object> resultService;
		try {
			resultService = dispatcher.runSync("updatePartyFormulaInvoiceItemType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String deletePartyFormulaInvoiceItemType(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		ctxMap.put("partyId", request.getParameter("partyId"));
		ctxMap.put("code", request.getParameter("code"));
		ctxMap.put("invoiceItemTypeId", request.getParameter("invoiceItemTypeId"));
		ctxMap.put("locale", locale);
		ctxMap.put("userLogin", userLogin);
		Map<String, Object> resultService;
		try {
			resultService = dispatcher.runSync("deletePartyFormulaInvoiceItemType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String settingEmplBaseSalaryByPosType(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String overrideDataWay = request.getParameter("overrideDataWay");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			if(thruDate.before(fromDate)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
				return "error";
			}
		}
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("partyGroupId", partyGroupId);
		ctxMap.put("fromDate", fromDate);
		ctxMap.put("thruDate", thruDate);
		ctxMap.put("overrideDataWay", overrideDataWay);
		ctxMap.put("locale", locale);
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("settingEmplBaseSalaryByPosType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "ConfigSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updatePartyRateAmount(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		paramMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		if(thruDateStr != null){
			paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "updateRateAmountOlbius", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			ctxMap.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("updateRateAmountOlbius", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
			
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getEmplPositionTypeByRoleTypeGroup(HttpServletRequest request, HttpServletResponse response){
		String roleTypeGroupId = request.getParameter("roleTypeGroupId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		if(roleTypeGroupId != null){
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
			try {
				List<GenericValue> roleTypeListGv = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(conditions), null, null, null, false);
				Set<String> allParty = FastSet.newInstance();
				for(GenericValue roleType: roleTypeListGv){
					String roleTypeId = roleType.getString("roleTypeId");
					List<String> listParty = SecurityUtil.getPartiesByRoles(roleTypeId, delegator); 
					allParty.addAll(listParty);
				}
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allParty));
				conditions.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
				List<GenericValue> emplPositionTypeList = delegator.findList("EmplPositionAndTypeAndParty", EntityCondition.makeCondition(conditions), UtilMisc.toSet("emplPositionTypeId"), UtilMisc.toList("emplPositionTypeId"), null, false);
				for(GenericValue tempEmplPositionType: emplPositionTypeList){
					Map<String, Object> tempMap = FastMap.newInstance();
					String emplPositionTypeId = tempEmplPositionType.getString("emplPositionTypeId");
					GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
					tempMap.put("emplPositionTypeId", emplPositionTypeId);
					tempMap.put("description", emplPositionType.getString("description"));
					listReturn.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			}
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		request.setAttribute("listReturn", listReturn);
		return "success";
	}
	
	public static String settingEmplPayrollParamByConfig(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Timestamp fromDate = null, thruDate = null;
		Locale locale = UtilHttp.getLocale(request);
		String partyGroupId = request.getParameter("partyId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		fromDate = new Timestamp(Long.parseLong(fromDateStr));
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		String overrideDataWay = request.getParameter("overrideDataWay");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("fromDate", fromDate);
		ctxMap.put("thruDate", thruDate);
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("overrideDataWay", overrideDataWay);
		ctxMap.put("partyGroupId", partyGroupId);
		try {
			Map<String, Object> resultService = dispatcher.runSync("setEmplPyrllParamByEmplPosType", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "SettingPayrollParamSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	
	//maybe delete
	public static String getInvoiceItemTypeByPartyAndFormual(HttpServletRequest request, HttpServletResponse response){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String partyId = request.getParameter("partyId");
		String salaryItem = request.getParameter("salaryItem");
		//LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		List<Map<String, Object>> retList = FastList.newInstance();
		Set<String> invoiceItemTyppeSetId = FastSet.newInstance();
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			List<String> orgList = null;
			if(customTimePeriod != null){
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				orgList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDateTs, thruDateTs);		
			}else{
				orgList = PartyUtil.getOrgOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
			}
			for(String orgId: orgList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempInvoiceItemTypeId = PayrollWorker.getInvoiceItemTypeByPartyAndCode(delegator, salaryItem, orgId, customTimePeriodId, userLogin.getString("userLoginId"));
				if(tempInvoiceItemTypeId != null){
					if(!invoiceItemTyppeSetId.contains(tempInvoiceItemTypeId)){
						GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", tempInvoiceItemTypeId), false);
						tempMap.put("invoiceItemTypeId", tempInvoiceItemTypeId);
						tempMap.put("description", invoiceItemType.getString("description"));
						retList.add(tempMap);
						invoiceItemTyppeSetId.add(tempInvoiceItemTypeId);
					}
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", retList);
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");			
			e.printStackTrace();
		}
		return "success";
	}
	
	//maybe delete
	public static String createInvoiceItemSalary(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String partyIdList = request.getParameter("partyIdList");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		JSONArray partyJson = JSONArray.fromObject(partyIdList);
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("locale", locale);
		Map<String, Object> resultService;
		List<String> listPartyCannotCreateInvoice = FastList.newInstance();
		for(int i = 0; i < partyJson.size(); i++){
			String partyId = partyJson.getString(i);
			ctxMap.put("partyId", partyId);
			ctxMap.put("customTimePeriodId", customTimePeriodId);
			try {
				resultService = dispatcher.runSync("createPartyInvoiceItemSalary", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					listPartyCannotCreateInvoice.add(partyId);
				}
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		try {
			if(listPartyCannotCreateInvoice.size() > 0){
				List<String> partyErrName = FastList.newInstance();
				for(String partyErrId: listPartyCannotCreateInvoice){
					partyErrName.add(PartyUtil.getPersonName(delegator, partyErrId));
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "CannotCreateInvoiceForListParty", UtilMisc.toMap("partyIdErrList", StringUtils.join(partyErrName, ", ")), locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "CreateInvoiceSuccessfully", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String getEmplSalaryBaseFlat(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, Integer.parseInt(month));
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
			Timestamp fromDateTs = UtilDateTime.getMonthStart(timestamp);
			Timestamp thruDateTs = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Map<String, Object> context = FastMap.newInstance();
			context.put("fromDate", fromDateTs);
			context.put("thruDate", thruDateTs);
			context.put("partyId", userLogin.getString("partyId"));
			context.put("orgId", orgId);
			context.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("getPartyPayrollHistory", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			List<GenericValue> listPartyPayrollHistory = (List<GenericValue>)resultService.get("listPartyPayrollHistory");
			context.remove("orgId");
			resultService = dispatcher.runSync("getPartyInsuranceSalary", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			List<GenericValue> listPartyInsSal = (List<GenericValue>)resultService.get("listPartyInsSal");
			request.setAttribute("salaryBaseInfo", listPartyPayrollHistory);
			request.setAttribute("salaryInsuranceInfo", listPartyInsSal);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		}  catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getEmplPayrolloverview(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String yearStr = request.getParameter("year");
		String monthStr = request.getParameter("month");
		if(yearStr != null && monthStr != null){
			Long month = Long.parseLong(monthStr);
			Long year = Long.parseLong(yearStr);
			String partyId = userLogin.getString("partyId");
			try {
				List<GenericValue> payrollTableRecordList = delegator.findByAnd("PayrollTableRecord", 
						UtilMisc.toMap("month", month, "year", year, "statusId", "PYRLL_TABLE_INVOICED"), null, false);
				if(UtilValidate.isNotEmpty(payrollTableRecordList)){
					String payrollTableId = payrollTableRecordList.get(0).getString("payrollTableId");
					List<GenericValue> payrollTableRecordPartyList = delegator.findByAnd("PayrollTableRecordPartyAmountAndSal", 
							UtilMisc.toMap("payrollTableId", payrollTableId, "partyId", partyId, "statusId", "PYRLL_TABLE_INVOICED"), null, false);
					if(UtilValidate.isNotEmpty(payrollTableRecordPartyList)){
						GenericValue payrollTableRecordParty = payrollTableRecordPartyList.get(0);
						request.setAttribute("totalIncome", payrollTableRecordParty.get("totalIncome"));
						request.setAttribute("totalDedution", payrollTableRecordParty.get("totalDedution"));
						request.setAttribute("actualSalReceive", payrollTableRecordParty.get("actualSalReceive"));
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					}
				}
			} catch (GenericEntityException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String getEmplAllowance(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, Integer.parseInt(month));
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp fromDate = UtilDateTime.getMonthStart(timestamp);
		Timestamp thruDate = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Map<String, Object> context = FastMap.newInstance();
			context.put("fromDate", fromDate);
			context.put("thruDate", thruDate);
			context.put("partyId", userLogin.getString("partyId"));
			context.put("orgId", orgId);
			context.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("getEmplAllowance", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			List<GenericValue> allowanceList = (List<GenericValue>)resultService.get("listEmplAllowance");
			if(UtilValidate.isNotEmpty(allowanceList)){
				Map<String, Object> retMap = FastMap.newInstance();
				for(GenericValue allowance: allowanceList){
					String code = allowance.getString("code");
					List<GenericValue> tempList = (List<GenericValue>)retMap.get(code);
					if(tempList == null){
						tempList = FastList.newInstance();
						retMap.put(code, tempList);
					}
					tempList.add(allowance);
				}
				request.setAttribute("results", retMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	public static String getEmplPositionSalary(HttpServletRequest request, HttpServletResponse response){
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String date = request.getParameter("dateJoinCompany");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Timestamp moment = null;
		if(date != null){
			moment = new Timestamp(Long.parseLong(date));
		}else{
			moment = UtilDateTime.nowTimestamp();
		}
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> postalAddrList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, moment, moment);
			EntityCondition dateConds = EntityUtil.getFilterByDateExpr(moment);
			List<GenericValue> emplPosTypeRate = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(
					EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId),
					EntityOperator.AND,
					dateConds), null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(postalAddrList)){
				GenericValue postalAddr = postalAddrList.get(0);
				List<GenericValue> emplPositionTypeRate = PayrollUtil.getEmplPositionTypeRate(delegator, emplPosTypeRate, postalAddr);
				if(UtilValidate.isNotEmpty(emplPositionTypeRate)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute("rateAmount", emplPositionTypeRate.get(0).get("rateAmount"));
					return "success";
				}
			}
			if(UtilValidate.isNotEmpty(emplPosTypeRate)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("rateAmount", emplPosTypeRate.get(0).get("rateAmount"));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRBaseHRPayrollUiLabels", "SalaryNotSetEmplPositionType", UtilHttp.getLocale(request)));
			}
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getPayrollTableRecordPartyAmount(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Security security = (Security) request.getAttribute("security");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		boolean hasPermisson = security.hasEntityPermission("HR_MGRPAYROLL", "_VIEW", userLogin);
		Locale locale = UtilHttp.getLocale(request);
		if(!hasPermisson){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
        	request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
        	return "error";
        }
		String payrollTableId = request.getParameter("payrollTableId");
		String partyCode = request.getParameter("partyCode");
		try {
			List<GenericValue> party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isEmpty(party)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
	        	request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "NoEmployeeHavePartyIdInPayrollTable", UtilMisc.toMap("partyCode", partyCode), locale));
	        	return "error";
			}
			String partyId = party.get(0).getString("partyId");
			GenericValue partySalAmount = delegator.findOne("PayrollTableRecordParty", UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId), false);
			if(partySalAmount == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
	        	request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindPartyInPayrollTable", UtilMisc.toMap("partyCode", partyCode), locale));
	        	return "error";
			}
			GenericValue basicAmount = delegator.findOne("PayrollTableRecordPartyAmount", UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId, "code", "LUONG_CO_BAN"), false);
			GenericValue insAmount = delegator.findOne("PayrollTableRecordPartyAmount", UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId, "code", "MUC_LUONG_DONG_BH"), false);
			if(basicAmount != null){
				request.setAttribute("basicAmount", basicAmount.get("amount"));
			}
			if(insAmount != null){
				request.setAttribute("insAmount", insAmount.get("amount"));
			}
			request.setAttribute("fullName", PartyUtil.getPersonName(delegator, partyId));
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	@SuppressWarnings("unchecked")
	public static String createPayrollTablePartyInvoice(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Security security = (Security) request.getAttribute("security");
		boolean hasPermisson = security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", userLogin);
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
		if(!hasPermisson){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
        	request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
        	return "error";
        }
		String partyIdUnselectedParam = request.getParameter("partyIdUnselected");
		String payrollTableId = request.getParameter("payrollTableId");
		List<String> partyIdUnselected = FastList.newInstance();
		if(partyIdUnselectedParam != null){
			JSONArray partyIdUnselectedJson = JSONArray.fromObject(partyIdUnselectedParam);
			for(int i = 0; i < partyIdUnselectedJson.size(); i++){
				partyIdUnselected.add(partyIdUnselectedJson.getString(i));
			}
		}
		paramsExtend.put("pagesize", new String[]{"0"});
		paramsExtend.put("sname", new String[]{"JQGetPayrollTableRecordPartyNotInvoice"});
		Map<String,Object> context = new HashMap<String,Object>();
		context.put("parameters", paramsExtend);
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        try {
			Map<String, Object> results = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(results)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(results));
				return "error";
			}
			List<GenericValue> listEmpl = (List<GenericValue>)results.get("results");
			String totalRowsStr = (String)results.get("TotalRows");
			int totalRow = Integer.parseInt(totalRowsStr);
			int totalEmplCreateInvoice = totalRow - partyIdUnselected.size();
			int emplCreateInvoiceSuccess = 0;
			context.clear();
			context.put("userLogin", userLogin);
	        context.put("timeZone", timeZone);
	        context.put("locale", locale);
	        context.put("payrollTableId", payrollTableId);
			for(GenericValue tempGv: listEmpl){
				String partyId = tempGv.getString("partyId");
				if(!partyIdUnselected.contains(partyId)){
					context.put("partyId", partyId);
					results = dispatcher.runSync("createPayrollTablePartyInvoice", context);
					if(ServiceUtil.isSuccess(results)){
						emplCreateInvoiceSuccess++;
					}
				}
			}
			if(emplCreateInvoiceSuccess > 0){
				GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
				if(!"PYRLL_TABLE_INVOICED".equals(payrollTableRecord.get("statusId"))){
					payrollTableRecord.set("statusId", "PYRLL_TABLE_INVOICED");
					payrollTableRecord.store();
					GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", "PYRLL_TABLE_INVOICED"), false);
					request.setAttribute("statusIdDesc", statusItem.get("description"));
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CreatePayrollInvoiceForEmplSuccess", 
					UtilMisc.toMap("total", totalEmplCreateInvoice, "success", emplCreateInvoiceSuccess), locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getListDepartmentInvoiceMapped(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyCode = request.getParameter("partyCode");
		String payrollTableId = request.getParameter("payrollTableId");
		Locale locale = UtilHttp.getLocale(request);
		try {
			List<GenericValue> party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			GenericValue payrollTable = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(party)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", 
						"NoEmployeeIdIsHavePartyId", UtilMisc.toMap("partyId", partyCode), locale));
				return "error";
			}
			if(payrollTable == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find payroll table");
				return "error";
			}
			String partyId = party.get(0).getString("partyId");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "PYRLL_TABLE_INVOICED"));
			List<GenericValue> partySalaryInfo = delegator.findList("PayrollTableRecordPartyAndTotalReceipt", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isEmpty(partySalaryInfo)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindEmplHaveIdNotCreateInvoice", UtilMisc.toMap("partyCode", partyCode), locale));
				return "error";
			}
			request.setAttribute("fullName", partySalaryInfo.get(0).getString("fullName"));
			request.setAttribute("actualReceipt", partySalaryInfo.get(0).getString("actualReceipt"));
			request.setAttribute("partyGroupId", partySalaryInfo.get(0).getString("partyGroupId"));
			Timestamp fromDate = payrollTable.getTimestamp("fromDate");
			Timestamp thruDate = payrollTable.getTimestamp("thruDate");
			List<String> listDeptId = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
			List<GenericValue> listData = FastList.newInstance();
			for(String deptId: listDeptId){
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", deptId), false);
				listData.add(partyGroup);
			}
			
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("partyGroupData", listData);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String editEmployeeSalaryBase(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = (String)parameterMap.get("fromDate");
		String thruDateStr = (String)parameterMap.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		String partyIds = (String)parameterMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		parameterMap.put("locale", locale);
		parameterMap.put("timeZone", timeZone);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "editEmployeeSalaryBase", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("editEmployeeSalaryBase", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
			}
			if(successCreate == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "UpdateBaseSalaryForListEmplFail", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "UpdateBaseSalaryForListEmplSuccess",
					UtilMisc.toMap("successCreate", successCreate, "total", totalEmpl), locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createPartySalaryBase(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		String partyIds = (String)parameterMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		parameterMap.put("locale", locale);
		parameterMap.put("timeZone", timeZone);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPartySalaryBase", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("createPartySalaryBase", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
			}
			if(successCreate == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CreateBaseSalaryForListEmplFail", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CreateBaseSalaryForListEmplSuccess",
					UtilMisc.toMap("successCreate", successCreate, "total", totalEmpl), locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
