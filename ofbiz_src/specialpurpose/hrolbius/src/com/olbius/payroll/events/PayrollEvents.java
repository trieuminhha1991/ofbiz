package com.olbius.payroll.events;

import java.math.BigDecimal;
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
import net.sf.json.JSONObject;

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
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.PayrollUtil;
import com.olbius.util.CommonUtil;
import com.olbius.util.PartyUtil;

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
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));	
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
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}
		String rateAmountStr = request.getParameter("rateAmount");
		BigDecimal rateAmount = new BigDecimal(rateAmountStr);
		String includeGeoId = request.getParameter("includeGeoId");
		String excludeGeoId = request.getParameter("excludeGeoId");
		List<String> includeGeoList = FastList.newInstance();
		List<String> excludeGeoList = null;
		JSONArray includeGeoJson = JSONArray.fromObject(includeGeoId);
		for(int i = 0; i < includeGeoJson.size(); i++){
			includeGeoList.add(includeGeoJson.getJSONObject(i).getString("includeGeoId"));
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
		List<String> includeGeoList = FastList.newInstance();
		List<String> excludeGeoList = null;
		JSONArray includeGeoJson = JSONArray.fromObject(includeGeoId);
		for(int i = 0; i < includeGeoJson.size(); i++){
			includeGeoList.add(includeGeoJson.getJSONObject(i).getString("includeGeoId"));
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
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("PayrollUiLabels", "ConfirmSuccessfully", UtilHttp.getLocale(request)));
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
	
	public static String getEmplParamCharacteristic(HttpServletRequest request, HttpServletResponse response){
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
	}
	
	public static String createEmplPayrollParameters(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createEmplPayrollParameters", ServiceUtil.setServiceFields(dispatcher, "createEmplPayrollParameters", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, (String)resultService.get(ModelService.SUCCESS_MESSAGE));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
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
				Double newValue = Double.parseDouble(value);
				payrollTable.set("value", String.valueOf(newValue));
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
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
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
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
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
}
