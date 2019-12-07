package com.olbius.insurance.events;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

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
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.export.ExportDataInfo;
import com.olbius.export.ExportDocument;
import com.olbius.insurance.helper.InsuranceHelper;
import com.olbius.util.DateUtil;
import com.olbius.util.PartyUtil;

public class InsuranceEvents {
	public static String getInsDeclJoinFirstByCustomPeriod(HttpServletRequest request, HttpServletResponse respone){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> insuranceDeclaration = delegator.findByAnd("InsuranceDeclaration", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "declarationTypeId", "DECLARATION_FIRST"), UtilMisc.toList("sequenceNum"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tempGv: insuranceDeclaration){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("insuranceDelarationId", tempGv.getString("insuranceDelarationId"));
				tempMap.put("sequenceNum", tempGv.get("sequenceNum"));
				listReturn.add(tempMap);
			}
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		return "success";
	}
	public static String getInsDeclParticipatetByCustomPeriod(HttpServletRequest request, HttpServletResponse respone){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> insuranceDeclaration = delegator.findByAnd("InsuranceDeclaration", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "declarationTypeId", "DECL_PARTICIPATE"), UtilMisc.toList("sequenceNum"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tempGv: insuranceDeclaration){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("insuranceDelarationId", tempGv.getString("insuranceDelarationId"));
				tempMap.put("sequenceNum", tempGv.get("sequenceNum"));
				listReturn.add(tempMap);
			}
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createNewJoinFirstInsurance(HttpServletRequest request, HttpServletResponse respone){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("customTimePeriodId", customTimePeriodId);
		ctxMap.put("declarationTypeId", "DECLARATION_FIRST");
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createInsuranceDeclaration", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("insuranceDelarationId", resultService.get("insuranceDelarationId"));
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
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
	
	public static String createEmplInsuranceSuspend(HttpServletRequest request, HttpServletResponse respone){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("customTimePeriodId", customTimePeriodId);
		ctxMap.put("declarationTypeId", "DECLARATION_SUSPEND");
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createInsuranceDeclaration", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("insuranceDelarationId", resultService.get("insuranceDelarationId"));
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
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
	
	public static String createEmplInsuranceParticipate(HttpServletRequest request, HttpServletResponse respone){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("customTimePeriodId", customTimePeriodId);
		ctxMap.put("declarationTypeId", "DECL_PARTICIPATE");
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createInsuranceDeclaration", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("insuranceDelarationId", resultService.get("insuranceDelarationId"));
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
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
	
	public static String getInsSuspendByCustomPeriod(HttpServletRequest request, HttpServletResponse respone){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> insuranceDeclaration = delegator.findByAnd("InsuranceDeclaration", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "declarationTypeId", "DECLARATION_SUSPEND"), UtilMisc.toList("sequenceNum"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tempGv: insuranceDeclaration){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("insuranceDelarationId", tempGv.getString("insuranceDelarationId"));
				tempMap.put("sequenceNum", tempGv.get("sequenceNum"));
				listReturn.add(tempMap);
			}
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getPartyInsuranceSocialNbr(HttpServletRequest request, HttpServletResponse respone){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(partyId != null){
			try {
				GenericValue party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if(party != null){
					request.setAttribute("insuranceSocialNbr", party.get("insuranceSocialNbr"));
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}
	
	public static String getPartyInsuranceHealthHosiptal(HttpServletRequest request, HttpServletResponse respone){
		String partyId = request.getParameter("partyId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(partyId == null || customTimePeriodId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(customTimePeriod == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				return "error";
			}
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = new Timestamp(thruDate.getTime());
			EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDateTs),
																	EntityJoinOperator.AND,
																	EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDateTs));
			EntityCondition partyConds = EntityCondition.makeCondition("partyId", partyId);
			List<GenericValue> partyHealthInsuranceList = delegator.findList("PartyHealthInsurance", EntityCondition.makeCondition(conds, EntityJoinOperator.AND, partyConds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyHealthInsuranceList)){
				GenericValue partyHealthInsurance = partyHealthInsuranceList.get(0);
				String hospitalId = partyHealthInsurance.getString("hospitalId");
				GenericValue hospital = delegator.findOne("Hospital", UtilMisc.toMap("hospitalId", hospitalId), false);
				String contactMechId = hospital.getString("contactMechId");
				GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
				GenericValue stateProvinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
				request.setAttribute("hositalName", hospital.getString("hospitalName"));
				request.setAttribute("hospitalCode", hospital.getString("hospitalCode"));
				request.setAttribute("hospitalId", hospitalId);
				request.setAttribute("stateProvinceGeoId", stateProvinceGeoId);
				request.setAttribute("geoName", stateProvinceGeo.getString("geoName"));
				request.setAttribute("codeNumber", stateProvinceGeo.getString("codeNumber"));
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createPartyInsuranceDeclaration(HttpServletRequest request, HttpServletResponse respone){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parametersMap = UtilHttp.getParameterMap(request);
		String agreementFromDateStr = request.getParameter("agreementFromDate");
		String agreementThruDateStr = request.getParameter("agreementThruDate");
		String agreementSignDateStr = request.getParameter("agreementSignDate");
		String insuranceTypeIdJson = request.getParameter("insuranceTypeId");
		String dateReturnCardStr = request.getParameter("dateReturnCard");
		List<String> insuranceTypeList = FastList.newInstance();
		if(insuranceTypeIdJson != null){
			JSONArray insuranceTypeIdJsonArr = JSONArray.fromObject(insuranceTypeIdJson);
			for(int i = 0; i < insuranceTypeIdJsonArr.size(); i++){
				insuranceTypeList.add(insuranceTypeIdJsonArr.getString(i));
			}
			parametersMap.put("insuranceTypeList", insuranceTypeList);
		}
		if(agreementFromDateStr != null){
			parametersMap.put("agreementFromDate", new Timestamp(Long.parseLong(agreementFromDateStr)));
		}
		if(agreementThruDateStr != null){
			parametersMap.put("agreementThruDate", new Timestamp(Long.parseLong(agreementThruDateStr)));
		}
		if(agreementSignDateStr != null){
			parametersMap.put("agreementSignDate", new Timestamp(Long.parseLong(agreementSignDateStr)));
		}
		if(dateReturnCardStr != null){
			parametersMap.put("dateReturnCard", new Date(Long.parseLong(dateReturnCardStr)));
		}
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceDeclaration", parametersMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			ctxMap.put("locale", UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("createPartyInsuranceDeclaration", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", UtilHttp.getLocale(request)));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createPartyInsuranceHealth(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createPartyInsuranceHealth", ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceHealth", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String createEmplPositionTypeInsuranceSalary(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		parameterMap.put("fromDate", fromDate);
		if(thruDateStr != null){
			Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
			parameterMap.put("thruDate", thruDate);
		}
		String allowanceSeniority = request.getParameter("allowanceSeniority");
		if(allowanceSeniority != null){
			parameterMap.put("allowanceSeniority", Double.parseDouble(allowanceSeniority));
		}
		String allowanceSeniorityExces = request.getParameter("allowanceSeniorityExces");
		if(allowanceSeniorityExces != null){
			parameterMap.put("allowanceSeniorityExces", Double.parseDouble(allowanceSeniorityExces));
		}
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			Map<String, Object> resultService = dispatcher.runSync("createEmplPositionTypeInsuranceSalary", 
					ServiceUtil.setServiceFields(dispatcher, "createEmplPositionTypeInsuranceSalary", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", UtilHttp.getLocale(request)));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
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
	
	public static String getInsAllowancePaymentByCustomPeriod(HttpServletRequest request, HttpServletResponse response){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> insAllowancePaymentDecl = delegator.findByAnd("InsuranceAllowancePaymentDecl", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId), UtilMisc.toList("sequenceNum"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tempGv: insAllowancePaymentDecl){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("insAllowancePaymentDeclId", tempGv.getString("insAllowancePaymentDeclId"));
				tempMap.put("sequenceNum", tempGv.get("sequenceNum"));
				listReturn.add(tempMap);
			}
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String createNewInsuranceAllowancePaymentDecl(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("customTimePeriodId", customTimePeriodId);
		ctxMap.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createNewInsuranceAllowancePaymentDecl", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("insAllowancePaymentDeclId", resultService.get("insAllowancePaymentDeclId"));
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e){
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createPartyInsuranceAllowancePaymentDecl(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String timeConditionBenefitStr = request.getParameter("timeConditionBenefit");
		if(timeConditionBenefitStr != null){
			paramMap.put("timeConditionBenefit", new Timestamp(Long.parseLong(timeConditionBenefitStr)));
		}
		String fromDateLeaveStr = request.getParameter("fromDateLeave");
		String thruDateLeaveStr = request.getParameter("leaveThruDate");
		if(fromDateLeaveStr != null){
			paramMap.put("fromDateLeave", new Timestamp(Long.parseLong(fromDateLeaveStr)));
		}
		if(thruDateLeaveStr != null){
			paramMap.put("thruDateLeave", new Timestamp(Long.parseLong(thruDateLeaveStr)));
		}
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			Map<String, Object> resultService = dispatcher.runSync("createPartyInsuranceAllowancePaymentDecl", 
					ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceAllowancePaymentDecl", paramMap, userLogin, 
									UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", UtilHttp.getLocale(request)));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String exportInsuranceDeclarationSuspend(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String insuranceDelarationId = request.getParameter("insuranceDelarationId");
		//LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		//GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			GenericValue insuranceDeclaration = delegator.findOne("InsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), false);
			if(insuranceDeclaration != null){
				String customTimePeriodId = insuranceDeclaration.getString("customTimePeriodId");
				Long sequenceNum = insuranceDeclaration.getLong("sequenceNum");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDateCus = customTimePeriod.getDate("fromDate");
				Date thruDateCus = customTimePeriod.getDate("thruDate");
				Timestamp thruDateCusTs = UtilDateTime.getDayEnd(new Timestamp(thruDateCus.getTime()));
				Timestamp fromDateCusTs = UtilDateTime.getDayStart(new Timestamp(fromDateCus.getTime()));
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateCus);				
				String fileName = "Bao cao giam lao dong dot " + sequenceNum + " thang " + (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.YEAR)) + ".xls";
				List<GenericValue> partyInsuranceDeclarationList = delegator.findByAnd("PartyInsuranceDeclaration", 
						UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), UtilMisc.toList("partyId"), false);
				List<Map<Integer, String>> listData = FastList.newInstance();
				EntityCondition conditionInsuranceHealth = EntityCondition.makeCondition(
						EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDateCusTs),
						EntityJoinOperator.AND,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDateCusTs));
				for(GenericValue tmpGv: partyInsuranceDeclarationList){
					Map<Integer, String> tempMap = FastMap.newInstance();
					listData.add(tempMap);
					String partyId = tmpGv.getString("partyId");
					String partyName = PartyUtil.getPersonName(delegator, partyId);
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					tempMap.put(0, partyName);
					tempMap.put(1, person.getString("insuranceSocialNbr"));
					String insuranceParticipateTypeId = tmpGv.getString("insuranceParticipateTypeId");
					GenericValue insuranceParticipateType = delegator.findOne("InsuranceParticipateType", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), false);
					Date birthDate = person.getDate("birthDate");
					if(birthDate != null){
						cal.setTime(birthDate);
						tempMap.put(2, DateUtil.getDateMonthYearDesc(cal));
					}
					String gender = person.getString("gender");
					if("F".equals(gender)){
						tempMap.put(3, "x");
					}
					tempMap.put(4, tmpGv.getString("jobDescription"));
					tempMap.put(5, tmpGv.getBigDecimal("salary") != null? String.valueOf(tmpGv.getBigDecimal("salary")): null);
					tempMap.put(6, tmpGv.get("ratioSalary") != null? String.valueOf(tmpGv.getDouble("ratioSalary") * 100): null);
					tempMap.put(7, tmpGv.get("allowancePosition") != null? String.valueOf(tmpGv.get("allowancePosition")): null);
					tempMap.put(8, tmpGv.get("allowanceSeniorityExces") != null? String.valueOf(tmpGv.getDouble("allowanceSeniorityExces") * 100): null);
					tempMap.put(9, tmpGv.get("allowanceSeniority") != null? String.valueOf(tmpGv.getDouble("allowanceSeniority") * 100): null);
					tempMap.put(10, tmpGv.get("allowanceOther") != null? String.valueOf(tmpGv.get("allowanceOther")):null);					
					Timestamp agreementFromDate = tmpGv.getTimestamp("agreementFromDate");
					if(agreementFromDate != null){
						cal.setTime(agreementFromDate);
						tempMap.put(11, DateUtil.getDateMonthYearDesc(cal));
					}
					Timestamp thruDate = tmpGv.getTimestamp("agreementThruDate");
					if(thruDate != null){
						cal.setTime(thruDate);
						tempMap.put(12, DateUtil.getDateMonthYearDesc(cal));
					}
					tempMap.put(13, insuranceParticipateType.getString("sign"));
					tempMap.put(14, tmpGv.getString("agreementNbr"));
					Timestamp agreementSignDate = tmpGv.getTimestamp("agreementSignDate");
					if(agreementSignDate != null){
						cal.setTime(agreementSignDate);
						tempMap.put(15, DateUtil.getDateMonthYearDesc(cal));
					}
					tempMap.put(16, tmpGv.get("rateContribution") != null? String.valueOf(tmpGv.getDouble("rateContribution") * 100)  : null);
					String isReducedBefore = tmpGv.getString("isReducedBefore");
					if("Y".equals(isReducedBefore)){
						tempMap.put(17, "X");
					}
					String suspendReasonId = tmpGv.getString("suspendReasonId");
					if(suspendReasonId != null){
						GenericValue suspendReason = delegator.findOne("SuspendInsuranceReasonType", UtilMisc.toMap("suspendReasonId", suspendReasonId), false);
						tempMap.put(18, suspendReason.getString("sign"));
					}
					String isRetInsHealthCard = tmpGv.getString("isRetInsHealthCard");
					if("Y".equals(isRetInsHealthCard)){
						tempMap.put(19, "X");
					}else{
						EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
						List<GenericValue> insuranceHealthList = delegator.findList("PartyHealthInsuranceAndHospitalPerson", 
								EntityCondition.makeCondition(partyCond, EntityJoinOperator.AND, conditionInsuranceHealth), null, UtilMisc.toList("-fromDate"), null, false);
						if(UtilValidate.isNotEmpty(insuranceHealthList)){
							GenericValue insuranceHealth = insuranceHealthList.get(0);
							tempMap.put(20, insuranceHealth.getString("insHealthCard"));
							Date dateReturnCard = tmpGv.getDate("dateReturnCard");
							if(dateReturnCard != null){
								cal.setTime(dateReturnCard);
								tempMap.put(21, DateUtil.getDateMonthYearDesc(cal));
							}
						}
					}
				}
				String path = InsuranceHelper.getInsuranceTemplatePath(delegator, "SUSPEND_INS");
				if(path != null){
					String scheme = request.getScheme();//https
					String serverName = request.getServerName();//localhost
					int port = request.getServerPort();//8443
					String url = scheme + "://" + serverName + ":" + port + path;
					ExportDataInfo data = new ExportDataInfo(fileName, listData);
					ExportDocument.exportDataToExcel(request, response, data, url);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	
	
	public static String exportInsuranceDeclarationParticipate(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String insuranceDelarationId = request.getParameter("insuranceDelarationId");
		Map<String, Object> resultService;
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			GenericValue insuranceDeclaration = delegator.findOne("InsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), false);
			if(insuranceDeclaration != null){
				String customTimePeriodId = insuranceDeclaration.getString("customTimePeriodId");
				Long sequenceNum = insuranceDeclaration.getLong("sequenceNum");
				//String declarationTypeId = insuranceDeclaration.getString("declarationTypeId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDateCus = customTimePeriod.getDate("fromDate");
				Date thruDateCus = customTimePeriod.getDate("thruDate");
				Timestamp thruDateCusTs = UtilDateTime.getDayEnd(new Timestamp(thruDateCus.getTime()));
				Timestamp fromDateCusTs = UtilDateTime.getDayStart(new Timestamp(fromDateCus.getTime()));
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateCus);				
				String fileName = "Bao cao tang lao dong dot " + sequenceNum + " thang " + (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.YEAR)) + ".xls";
				//fileName = StringUtil.wrapString(fileName).toString();
				List<GenericValue> partyInsuranceDeclarationList = delegator.findByAnd("PartyInsuranceDeclaration", 
						UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), UtilMisc.toList("partyId"), false);
				List<Map<Integer, String>> listData = FastList.newInstance();
				EntityCondition conditionInsuranceHealth = EntityCondition.makeCondition(
						EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDateCusTs),
						EntityJoinOperator.AND,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDateCusTs));
				for(GenericValue tmpGv: partyInsuranceDeclarationList){
					Map<Integer, String> tempMap = FastMap.newInstance();
					listData.add(tempMap);
					String partyId = tmpGv.getString("partyId");
					String partyName = PartyUtil.getPersonName(delegator, partyId);
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					tempMap.put(0, partyName);
					tempMap.put(1, person.getString("insuranceSocialNbr"));
					String insuranceParticipateTypeId = tmpGv.getString("insuranceParticipateTypeId");
					GenericValue insuranceParticipateType = delegator.findOne("InsuranceParticipateType", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), false);
					Date birthDate = person.getDate("birthDate");
					if(birthDate != null){
						cal.setTime(birthDate);
						tempMap.put(2, DateUtil.getDateMonthYearDesc(cal));
					}
					String gender = person.getString("gender");
					if("F".equals(gender)){
						tempMap.put(3, "x");
					}
					tempMap.put(4, tmpGv.getString("jobDescription"));
					tempMap.put(5, tmpGv.getBigDecimal("salary") != null? String.valueOf(tmpGv.getBigDecimal("salary")): null);
					tempMap.put(6, tmpGv.get("ratioSalary") != null? String.valueOf(tmpGv.getDouble("ratioSalary") * 100): null);
					tempMap.put(7, tmpGv.get("allowancePosition") != null? String.valueOf(tmpGv.get("allowancePosition")): null);
					tempMap.put(8, tmpGv.get("allowanceSeniorityExces") != null? String.valueOf(tmpGv.getDouble("allowanceSeniorityExces") * 100): null);
					tempMap.put(9, tmpGv.get("allowanceSeniority") != null? String.valueOf(tmpGv.getDouble("allowanceSeniority") * 100): null);
					tempMap.put(10, tmpGv.get("allowanceOther") != null? String.valueOf(tmpGv.get("allowanceOther")):null);					
					Timestamp agreementFromDate = tmpGv.getTimestamp("agreementFromDate");
					if(agreementFromDate != null){
						cal.setTime(agreementFromDate);
						tempMap.put(11, DateUtil.getDateMonthYearDesc(cal));
					}
					Timestamp thruDate = tmpGv.getTimestamp("agreementThruDate");
					if(thruDate != null){
						cal.setTime(thruDate);
						tempMap.put(12, DateUtil.getDateMonthYearDesc(cal));
					}
					tempMap.put(13, insuranceParticipateType.getString("sign"));
					tempMap.put(14, tmpGv.getString("agreementNbr"));
					tempMap.put(15, tmpGv.getString("agreemenType"));
					Timestamp agreementSignDate = tmpGv.getTimestamp("agreementSignDate");
					if(agreementSignDate != null){
						cal.setTime(agreementSignDate);
						tempMap.put(16, DateUtil.getDateMonthYearDesc(cal));
					}
					tempMap.put(17, tmpGv.get("rateContribution") != null? String.valueOf(tmpGv.getDouble("rateContribution") * 100)  : null);
					tempMap.put(18, InsuranceHelper.getInsuranceTypeParticipateInDecl(delegator, insuranceDelarationId, partyId));
					String statusSocialInsId = InsuranceHelper.getPartyInsuranceStatus(delegator, partyId, thruDateCusTs);
					if(statusSocialInsId != null){
						GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusSocialInsId), false);
						tempMap.put(19, status.getString("statusCode"));
					}
					String proposalInsuredMonth = tmpGv.getString("proposalInsuredMonth");
					if("Y".equals(proposalInsuredMonth)){
						tempMap.put(20, "x");
					}
					EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
					List<GenericValue> insuranceHealthList = delegator.findList("PartyHealthInsuranceAndHospitalPerson", 
							EntityCondition.makeCondition(partyCond, EntityJoinOperator.AND, conditionInsuranceHealth), null, null, null, false);
					if(UtilValidate.isNotEmpty(insuranceHealthList)){
						GenericValue insuranceHealth = insuranceHealthList.get(0);
						String stateProvinceGeoHospital = insuranceHealth.getString("stateProvinceGeoId");
						GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoHospital), false);
						tempMap.put(21, geo.getString("codeNumber"));
						tempMap.put(22, insuranceHealth.getString("hospitalCode"));
					}
					String nationalityId = person.getString("nationality");
					if(nationalityId != null && !"Vietnamese".equals(nationalityId)){
						GenericValue nationality = delegator.findOne("Nationality", UtilMisc.toMap("nationalityId", nationalityId), false);
						tempMap.put(23, nationality.getString("code"));
					}
					String ethnicOriginId = person.getString("ethnicOrigin");
					if(ethnicOriginId != null){
						GenericValue ethnicOrigin = delegator.findOne("EthnicOrigin", UtilMisc.toMap("ethnicOriginId", ethnicOriginId), false);
						tempMap.put(24, ethnicOrigin.getString("code")) ;
					}
					tempMap.put(25, person.getString("idNumber")) ;
					Date idIssueDate = person.getDate("idIssueDate");
					if(idIssueDate != null){
						cal.setTime(idIssueDate);
						tempMap.put(26, DateUtil.getDateMonthYearDesc(cal));
					}
					
					resultService = dispatcher.runSync("getPartyPostalAddress", 
							UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "BIRTH_CERT_LOCATION", "userLogin", userLogin));
					if(ServiceUtil.isSuccess(resultService)){
						String contactMechId = (String)resultService.get("contactMechId");
						if(contactMechId != null){
							GenericValue geoCurrRes;
							GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
							String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
							if(stateProvinceGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
								tempMap.put(30, geoCurrRes.getString("geoName"));
							}
							String wardGeoId = postalAddr.getString("wardGeoId");
							if(wardGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
								tempMap.put(28, geoCurrRes.getString("geoName"));
							}
							String districtGeoId = postalAddr.getString("districtGeoId");
							if(districtGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
								tempMap.put(29, geoCurrRes.getString("geoName"));
							}
						}
					}
					
					resultService = dispatcher.runSync("getPartyPostalAddress", 
							UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PERMANENT_RESIDENCE", "userLogin", userLogin));
					if(ServiceUtil.isSuccess(resultService)){
						String contactMechId = (String)resultService.get("contactMechId");
						if(contactMechId != null){
							GenericValue geoCurrRes;
							GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
							String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
							if(stateProvinceGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
								tempMap.put(34, geoCurrRes.getString("geoName"));
							}
							String wardGeoId = postalAddr.getString("wardGeoId");
							if(wardGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
								tempMap.put(32, geoCurrRes.getString("geoName"));
							}
							String districtGeoId = postalAddr.getString("districtGeoId");
							if(districtGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
								tempMap.put(33, geoCurrRes.getString("geoName"));
							}
							String address1Permanent = postalAddr.getString("address1");
							tempMap.put(31, address1Permanent);
						}
					}
					resultService = dispatcher.runSync("getPartyPostalAddress", 
							UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "CURRENT_RESIDENCE", "userLogin", userLogin));
					if(ServiceUtil.isSuccess(resultService)){
						String contactMechId = (String)resultService.get("contactMechId");
						if(contactMechId != null){
							GenericValue geoCurrRes;
							GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
							String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
							if(stateProvinceGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
								tempMap.put(38, geoCurrRes.getString("geoName"));
							}
							String wardGeoId = postalAddr.getString("wardGeoId");
							if(wardGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
								tempMap.put(36, geoCurrRes.getString("geoName"));
							}
							String districtGeoId = postalAddr.getString("districtGeoId");
							if(districtGeoId != null){
								geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
								tempMap.put(37, geoCurrRes.getString("geoName"));
							}
							String address1Permanent = postalAddr.getString("address1");
							tempMap.put(35, address1Permanent);
						}
					}
					resultService = dispatcher.runSync("getPartyTelephone", 
							UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", userLogin));
					if(ServiceUtil.isSuccess(resultService)){
						tempMap.put(39, (String)resultService.get("contactNumber"));
					}
					String partyRelation = InsuranceHelper.getParentRelation(delegator, partyId);
					if(partyRelation != null){
						tempMap.put(41, PartyUtil.getPersonName(delegator, partyRelation));
					}
				}
				String path = InsuranceHelper.getInsuranceTemplatePath(delegator, "PARTICIPATE_INS");
				if(path != null){
					String scheme = request.getScheme();//https
					String serverName = request.getServerName();//localhost
					int port = request.getServerPort();//8443
					String url = scheme + "://" + serverName + ":" + port + path;
					ExportDataInfo data = new ExportDataInfo(fileName, listData);
					ExportDocument.exportDataToExcel(request, response, data, url);
				}
			}
		} catch (GenericEntityException e) {			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
