package com.olbius.basehr.insurance.events;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.insurance.helper.InsuranceHelper;
import com.olbius.basehr.insurance.worker.InsuranceWorker;

public class InsuranceEvents {
	
	public static String getInsAllowancePaymentDeclSeqNum(HttpServletRequest request, HttpServletResponse response){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		try {
			List<GenericValue> listInsDeclSeq = delegator.findList("InsuranceAllowancePaymentDecl", EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId), UtilMisc.toSet("sequenceNum"), UtilMisc.toList("sequenceNum"), opts, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listInsDeclSeq);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "sucess";
	}
	
	public static String getPartyInsuranceSocialNbr(HttpServletRequest request, HttpServletResponse response){
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
	
	public static String getPartyInsuranceHealthHosiptal(HttpServletRequest request, HttpServletResponse response){
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
	
	public static String createPartyInsuranceHealth(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		String birthDateStr = (String)parameterMap.get("birthDate");
		String gender = (String)parameterMap.get("gender");
		String insuranceSocialNbr = (String)parameterMap.get("insuranceSocialNbr");
		try {
			Map<String, Object> resultService = null;
			TransactionUtil.begin();
			if(birthDateStr != null || gender != null || insuranceSocialNbr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				Map<String, Object> updatePersonCtx = ServiceUtil.setServiceFields(dispatcher, "updatePerson", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
				updatePersonCtx.put("birthDate", birthDate);
				resultService = dispatcher.runSync("updatePerson", updatePersonCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			resultService = dispatcher.runSync("createPartyInsuranceHealth", ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceHealth", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			TransactionUtil.commit();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updatePartyInsuranceHealth(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		parameterMap.put("fromDate", fromDate);
		parameterMap.put("thruDate", thruDate);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePartyInsuranceHealth", parameterMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePartyInsuranceHealth", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
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
		String benefitClassTypeId = request.getParameter("benefitClassTypeId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> insAllowancePaymentDecl = delegator.findByAnd("InsuranceAllowancePaymentDecl", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "benefitClassTypeId", benefitClassTypeId), UtilMisc.toList("sequenceNum"), false);
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
		String benefitClassTypeId = request.getParameter("benefitClassTypeId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("customTimePeriodId", customTimePeriodId);
		ctxMap.put("benefitClassTypeId", benefitClassTypeId);
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
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceAllowancePaymentDecl", paramMap, userLogin, 
					UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			context.put("locale", UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("createPartyInsuranceAllowancePaymentDecl", context);
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
	
	public static String addEmplLeaveToInsAllowancePaymentDecl(HttpServletRequest request, HttpServletResponse response){
		String emplLeaveInInsDeclJson = request.getParameter("emplLeaveInInsDecl");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String insAllowancePaymentDeclId = request.getParameter("insAllowancePaymentDeclId");
		if(emplLeaveInInsDeclJson == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find emplLeave to add");
			return "error";
		}
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		JSONArray jsonArr = JSONArray.fromObject(emplLeaveInInsDeclJson);
		Map<String, Object> resultService = null;
		List<String> errMessageList = FastList.newInstance();
		try {
			for(int i = 0; i < jsonArr.size(); i++){
				JSONObject emplLeaveJsonObj = jsonArr.getJSONObject(i);
				String emplLeaveId = emplLeaveJsonObj.getString("emplLeaveId");
				String benefitTypeId = emplLeaveJsonObj.getString("benefitTypeId");
				String statusConditionBenefit = emplLeaveJsonObj.has("statusConditionBenefit")? emplLeaveJsonObj.getString("statusConditionBenefit"): null;
				GenericValue emplLeave;
				emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
				GenericValue benefitType = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false); 
				if(emplLeave != null && benefitType != null){
					String dateParticipateInsStr = emplLeaveJsonObj.has("dateParticipateIns")? emplLeaveJsonObj.getString("dateParticipateIns") : null;
					if(dateParticipateInsStr != null){
						InsuranceHelper.updatePartyParticipateInsurance(delegator, emplLeave.getString("partyId"), new Date(Long.parseLong(dateParticipateInsStr)));
					}
					String benefitClassTypeId = benefitType.getString("benefitClassTypeId");
					Map<String, Object> context = FastMap.newInstance();
					if("SICKNESS_PREGNANCY".equals(benefitClassTypeId)){
						String insuranceSalaryStr = emplLeaveJsonObj.has("insuranceSalary")? emplLeaveJsonObj.getString("insuranceSalary"): null;
						String dayLeaveInRegulationStr = emplLeaveJsonObj.has("dayLeaveInRegulation")? emplLeaveJsonObj.getString("dayLeaveInRegulation"): null;
						String totalDayLeaveStr = emplLeaveJsonObj.has("totalDayLeave")?emplLeaveJsonObj.getString("totalDayLeave"): null;
						String accumulatedLeaveStr = emplLeaveJsonObj.has("accumulatedLeave")? emplLeaveJsonObj.getString("accumulatedLeave"): null;
						BigDecimal totalDayLeave = BigDecimal.ZERO;
						if(totalDayLeaveStr != null){
							context.put("totalDayLeave", new BigDecimal(totalDayLeaveStr));
						}
						if(insuranceSalaryStr != null){
							context.put("insuranceSalary", new BigDecimal(insuranceSalaryStr));
						}
						if(accumulatedLeaveStr != null){
							context.put("accumulatedLeave", Double.parseDouble(accumulatedLeaveStr));
						}
						if(dayLeaveInRegulationStr != null){
							BigDecimal totalDayLeavePaid = null;
							BigDecimal dayLeaveInRegulation = new BigDecimal(dayLeaveInRegulationStr);
							if(totalDayLeave.compareTo(dayLeaveInRegulation) <= 0){
								totalDayLeavePaid = totalDayLeave;
							}else{
								totalDayLeavePaid = dayLeaveInRegulation;
							}
							context.put("totalDayLeavePaid", totalDayLeavePaid);
						}
					}else if("HEALTH_IMPROVEMENT".equals(benefitClassTypeId)){
						context.put("statusConditionBenefit", statusConditionBenefit);
						String dayLeaveConcentrateStr = emplLeaveJsonObj.has("dayLeaveConcentrate")? emplLeaveJsonObj.getString("dayLeaveConcentrate"): null;
						String dayLeaveFamilyStr = emplLeaveJsonObj.has("dayLeaveFamily")? emplLeaveJsonObj.getString("dayLeaveFamily"): null;
						if(dayLeaveConcentrateStr != null){
							context.put("dayLeaveConcentrate", new BigDecimal(dayLeaveConcentrateStr));
						}
						if(dayLeaveFamilyStr != null){
							context.put("dayLeaveFamily", new BigDecimal(dayLeaveFamilyStr));
						}
					}
					String allowanceAmountStr = emplLeaveJsonObj.has("allowanceAmount")? emplLeaveJsonObj.getString("allowanceAmount"): null;
					context.put("emplLeaveId", emplLeaveId);
					context.put("benefitTypeId", benefitTypeId);
					if(allowanceAmountStr != null){
						context.put("allowanceAmount", new BigDecimal(allowanceAmountStr));
					}
					
					context.put("insAllowancePaymentDeclId", insAllowancePaymentDeclId);
					context.put("userLogin", userLogin);
					context.put("locale", UtilHttp.getLocale(request));
					context.put("timeZone", UtilHttp.getTimeZone(request));
					try {
						resultService = dispatcher.runSync("createPartyInsuranceAllowancePaymentDecl", context);
						if(!ServiceUtil.isSuccess(resultService)){
							Timestamp fromDate = emplLeave.getTimestamp("fromDate");
							Timestamp thruDate = emplLeave.getTimestamp("thruDate");
							String partyId = emplLeave.getString("partyId");
							String errMess = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotAddEmplLeaveToPartyInsuranceAllowance", 
									UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "fromDate", DateUtil.getDateMonthYearDesc(fromDate), "thruDate", DateUtil.getDateMonthYearDesc(thruDate)), UtilHttp.getLocale(request));
							errMessageList.add(errMess);
						}
					}catch(GenericServiceException e) {
						e.printStackTrace();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		if(errMessageList.size() > 0){
			request.setAttribute(ModelService.SUCCESS_MESSAGE, StringUtils.join(errMessageList, ", "));
		}else{
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", UtilHttp.getLocale(request)));
		}
		return "success";
	}
	
	public static String getTimeParticipateInsuranceOfParty(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(person == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find employee with id: " + partyId);
				return "error";
			}
			Date dateParticipateIns = person.getDate("dateParticipateIns");
			if(dateParticipateIns == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "DateParticipateInsuranceNotDecl", UtilHttp.getLocale(request)));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			Date nowDate = new Date(UtilDateTime.nowTimestamp().getTime());
			Integer totalMonth = DateUtil.getMonthBetweenTwoDate(dateParticipateIns, nowDate);
			Integer nbrYear = totalMonth / 12;
			Integer nbrMonth = totalMonth - 12 * nbrYear;
			String year = nbrYear < 10 ? "0" + String.valueOf(nbrYear): String.valueOf(nbrYear);
			String month = nbrMonth < 10 ? "0" + String.valueOf(nbrMonth): String.valueOf(nbrMonth);
			request.setAttribute("dateParticipateIns", year + "-" + month);
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String settingEmplInsuranceSalaryByPosType(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		if(thruDateStr != null){
			paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		Locale locale = UtilHttp.getLocale(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "settingEmplInsuranceSalaryByPosType", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("settingEmplInsuranceSalaryByPosType", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "SettingInsuranceSalaryByPosConfigSuccessful", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createPartyInsuranceSalary(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		if(thruDateStr != null){
			paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context;
		try {
			context = ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceSalary", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createPartyInsuranceSalary", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	
	public static String updatePartyInsuranceSalary(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		if(thruDateStr != null){
			paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context;
		try {
			context = ServiceUtil.setServiceFields(dispatcher, "updatePartyInsuranceSalary", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePartyInsuranceSalary", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createInsBenefitTypeRule(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String benefitTypeId = request.getParameter("benefitTypeId");
		String benefitTypeCondJson = request.getParameter("benefitTypeCond");
		String benefitTypeActJson = request.getParameter("benefitTypeAct");
		Locale locale = UtilHttp.getLocale(request);
		try {
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("createInsBenefitTypeRule", UtilMisc.toMap("benefitTypeId", benefitTypeId, "userLogin", userLogin, "locale", locale));
			if(ServiceUtil.isSuccess(resultService)){
				String benefitTypeRuleId = (String)resultService.get("benefitTypeRuleId");
				JSONArray benefitTypeCondJsonArr = JSONArray.fromObject(benefitTypeCondJson); 
				JSONArray benefitTypeActJsonArr = JSONArray.fromObject(benefitTypeActJson);
				for(int i = 0; i < benefitTypeCondJsonArr.size(); i++){
					JSONObject jsonObject = benefitTypeCondJsonArr.getJSONObject(i);
					String inputParamEnumId = jsonObject.getString("inputParamEnumId");
					String operatorEnumId = jsonObject.getString("operatorEnumId");
					String condValue = jsonObject.getString("condValue");
					resultService = dispatcher.runSync("createInsBenefitTypeCond", 
							UtilMisc.toMap("inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId,
											"condValue", new BigDecimal(condValue), "benefitTypeRuleId", benefitTypeRuleId,
											"benefitTypeId", benefitTypeId, "userLogin", userLogin, "locale", locale));
					if(!ServiceUtil.isSuccess(resultService)){
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
						return "error";
					}
				}
				for(int i = 0; i < benefitTypeActJsonArr.size(); i++){
					JSONObject jsonObject = benefitTypeActJsonArr.getJSONObject(i);
					String benefitTypeActionEnumId = jsonObject.getString("benefitTypeActionEnumId");
					BigDecimal amount = null, quantity = null;
					if(jsonObject.has("amount")){
						amount = new BigDecimal(jsonObject.getString("amount"));
					}
					if(jsonObject.has("quantity")){
						quantity = new BigDecimal(jsonObject.getString("quantity"));
					}
					String uomId = null;
					if(jsonObject.has("uomId")){
						uomId = jsonObject.getString("uomId");
					}
					resultService = dispatcher.runSync("createInsBenefitTypeAction", 
							UtilMisc.toMap("benefitTypeId", benefitTypeId, "benefitTypeRuleId", benefitTypeRuleId,
											"benefitTypeActionEnumId", benefitTypeActionEnumId, "quantity", quantity,
											"amount", amount, "uomId", uomId, "userLogin", userLogin, "locale", locale));
					if(!ServiceUtil.isSuccess(resultService)){
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
						return "error";
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
				TransactionUtil.commit();
			}else {
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String exportInsuranceExcel(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String sequenceNumStr = request.getParameter("sequenceNum");
		String insuranceContentTypeId = request.getParameter("insuranceContentTypeId");
		Locale locale = UtilHttp.getLocale(request);
		if(customTimePeriodId == null || sequenceNumStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CustomTimePeriodOrTimesDeclNull", locale));
			return "error";
		}
		try {
			if("D02-TS".equals(insuranceContentTypeId)){
				Workbook wb = InsuranceHelper.exportD02TSTemplate(delegator, customTimePeriodId, Long.parseLong(sequenceNumStr), locale, response);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				wb.write(baos);
				byte[] bytes = baos.toByteArray();
				String titleFile = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "D02TSFileName", 
						UtilMisc.toMap("sequence", sequenceNumStr, "period", CommonUtil.getMonthYearPeriodNameByCustomTimePeriod(delegator, customTimePeriodId)), locale);
				response.setHeader("content-disposition", "attachment;filename=" + titleFile + ".xls");
				response.setContentType("application/vnd.xls");
				response.getOutputStream().write(bytes);
				return "success";
			}else if("C66a-HD".equals(insuranceContentTypeId) || "C67a-HD".equals(insuranceContentTypeId) || "C68a-HD".equals(insuranceContentTypeId) ||
						"C69a-HD".equals(insuranceContentTypeId)){
				Workbook wb = InsuranceHelper.exportInsuranceBenefitAllowance(delegator, customTimePeriodId, insuranceContentTypeId, Long.parseLong(sequenceNumStr), locale, response);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				wb.write(baos);
				byte[] bytes = baos.toByteArray();
				//TODO chane title fileName
				String titleFile = insuranceContentTypeId;
				response.setHeader("content-disposition", "attachment;filename=" + titleFile + ".xls");
				response.setContentType("application/vnd.xls");
				response.getOutputStream().write(bytes);
			}else if("C70a-HD".equals(insuranceContentTypeId)){
				
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, 
						UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceContentTypeIsNotSupportNow",
								UtilMisc.toMap("insuranceContentTypeId", insuranceContentTypeId), locale));
				return "error";
			}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (IOException e) {
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
	
	public static String calcInsAllowanceAmountEmplLeave(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        if (!security.hasEntityPermission("HR_INS", "_ADMIN",  userLogin)) {
        	request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You have not permission to do this");
        	return "error";
        }
        String emplLeaveAllowanceParam = request.getParameter("emplLeaveAllowance");
        JSONArray emplLeaveAllowanceJsonList = JSONArray.fromObject(emplLeaveAllowanceParam);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        try {
        	Map<String, Object> retMap = FastMap.newInstance();
	        for(int i = 0; i < emplLeaveAllowanceJsonList.size(); i++){
	        	JSONObject emplLeaveAllowanceJson = emplLeaveAllowanceJsonList.getJSONObject(i);
	        	String emplLeaveId = emplLeaveAllowanceJson.getString("emplLeaveId");
	        	String benefitTypeId = emplLeaveAllowanceJson.getString("benefitTypeId");
				GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
				GenericValue benefitType = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false);
	        	if(emplLeave != null && benefitType != null){
	        		String benefitClassTypeId = benefitType.getString("benefitClassTypeId");
	        		if("SICKNESS_PREGNANCY".equals(benefitClassTypeId)){
	        			Date dateParticipateIns = null;
	        			String dateParticipateInsStr = emplLeaveAllowanceJson.has("dateParticipateIns")? emplLeaveAllowanceJson.getString("dateParticipateIns") : null;
	        			if(dateParticipateInsStr != null){
	        				dateParticipateIns = new Date(Long.parseLong(dateParticipateInsStr));
	        			}else{
	        				dateParticipateIns = InsuranceHelper.getDateParticipateIns(delegator, emplLeave.getString("partyId"));
	        				if(dateParticipateIns == null){
	        					dateParticipateIns = new Date(UtilDateTime.nowTimestamp().getTime());
	        				}
	        			}
	        			String insuranceSalaryStr = emplLeaveAllowanceJson.has("insuranceSalary")? emplLeaveAllowanceJson.getString("insuranceSalary"): null;
	        			String dayLeaveInRegulationStr = emplLeaveAllowanceJson.has("dayLeaveInRegulation")? emplLeaveAllowanceJson.getString("dayLeaveInRegulation"): null;
	        			String totalDayLeaveStr = emplLeaveAllowanceJson.has("totalDayLeave")?emplLeaveAllowanceJson.getString("totalDayLeave"): null;
	        			String accumulatedLeaveStr = emplLeaveAllowanceJson.has("accumulatedLeave")? emplLeaveAllowanceJson.getString("accumulatedLeave"): null;
	        			Map<String, Object> benefitInsuranceForPartyLeave = InsuranceWorker.getBenefitInsuranceForPartyLeave(delegator, dispatcher, userLogin, benefitTypeId, 
	        					emplLeaveId, dateParticipateIns, timeZone, locale);
	        			BigDecimal totalDayLeavePaidBefore = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaidBefore");
	        			BigDecimal totalDayLeave = null, insuranceSalary = null, totalDayLeavePaid = null, totalDayLeavePaidExceed = null;
	        			BigDecimal rateBenefit = (BigDecimal)benefitInsuranceForPartyLeave.get("rateBenefit");
	        			BigDecimal insuranceSalaryExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("insuranceSalaryExceed");
	        			BigDecimal rateBenefitLeaveExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("rateBenefitLeaveExceed");
	        			if(totalDayLeaveStr != null){
	        				totalDayLeave = new BigDecimal(totalDayLeaveStr);
	        			}else{
	        				totalDayLeave = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeave");
	        			}
	        			if(insuranceSalaryStr != null){
	        				insuranceSalary = new BigDecimal(insuranceSalaryStr);
	        			}else{
	        				insuranceSalary = (BigDecimal)benefitInsuranceForPartyLeave.get("insuranceSalary");
	        			}
	        			if(dayLeaveInRegulationStr != null){
	        				BigDecimal dayLeaveInRegulation = new BigDecimal(dayLeaveInRegulationStr);
	        				if(totalDayLeavePaidBefore.add(totalDayLeave).compareTo(dayLeaveInRegulation) <= 0){
	        					totalDayLeavePaid = totalDayLeave;
	        					totalDayLeavePaidExceed = BigDecimal.ZERO;
	        				}else{
	        					totalDayLeavePaid = dayLeaveInRegulation.subtract(totalDayLeavePaidBefore);
	        					totalDayLeavePaidExceed = totalDayLeavePaidBefore.add(totalDayLeave).subtract(dayLeaveInRegulation);
	        				}
	        			}else{
	        				totalDayLeavePaid = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaid");
	        				totalDayLeavePaidExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaidExceed");
	        			}
	        			Double accumulatedLeave = null;
	        			if(accumulatedLeaveStr != null){
	        				accumulatedLeave = Double.parseDouble(accumulatedLeaveStr);
	        			}else{
	        				accumulatedLeave = totalDayLeavePaid.doubleValue() + totalDayLeavePaidBefore.doubleValue();
	        				if(totalDayLeavePaidExceed != null){
	        					accumulatedLeave += totalDayLeavePaidExceed.doubleValue();
	        				}
	        			}
	        			BigDecimal allowanceAmount = InsuranceHelper.getInsuranceAllowanceAmount(insuranceSalary, totalDayLeavePaid, rateBenefit);
	        			if(insuranceSalaryExceed != null){
	        				BigDecimal allowanceAmountExceed = InsuranceHelper.getInsuranceAllowanceAmount(insuranceSalaryExceed, totalDayLeavePaidExceed, rateBenefitLeaveExceed);
	        				allowanceAmount = allowanceAmount.add(allowanceAmountExceed);
	        			}
	        			Map<String, Object> tempMap = FastMap.newInstance();
	        			tempMap.put("totalDayLeave", totalDayLeave);
	        			tempMap.put("insuranceSalary", insuranceSalary);
	        			tempMap.put("accumulatedLeave", accumulatedLeave);
	        			tempMap.put("allowanceAmount", allowanceAmount);
	        			retMap.put(emplLeaveId, tempMap);
	        		}else if("HEALTH_IMPROVEMENT".equals(benefitClassTypeId)){
	        			String dayLeaveInFamilyStr = emplLeaveAllowanceJson.has("dayLeaveFamily")? emplLeaveAllowanceJson.getString("dayLeaveFamily"): null;
	        			String dayLeaveConcentrateStr = emplLeaveAllowanceJson.has("dayLeaveConcentrate")? emplLeaveAllowanceJson.getString("dayLeaveConcentrate"):null;
	        			BigDecimal dayLeaveInFamily = null, dayLeaveConcentrate = null;
	        			if(dayLeaveInFamilyStr != null){
	        				dayLeaveInFamily = new BigDecimal(dayLeaveInFamilyStr);
	        			}
	        			if(dayLeaveConcentrateStr != null){
	        				dayLeaveConcentrate = new BigDecimal(dayLeaveConcentrateStr);
	        			}
	        			BigDecimal allowanceAmount = InsuranceWorker.calculateAllowanceAmountImproveHealth(delegator, emplLeave.getTimestamp("fromDate"), dayLeaveConcentrate, dayLeaveInFamily);
	        			Map<String, Object> tempMap = FastMap.newInstance();
	        			tempMap.put("allowanceAmount", allowanceAmount);
	        			retMap.put(emplLeaveId, tempMap);
	        		}
	        	}
	        }
	        request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
	        request.setAttribute("results", retMap);
        } catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String getMaxDayLeaveOfBenefitByEmplLeave(HttpServletRequest request, HttpServletResponse response){
		String emplLeaveId = request.getParameter("emplLeaveId");
		String benefitTypeId = request.getParameter("benefitTypeId");
		String dateParticipateInsStr = request.getParameter("dateParticipateIns");
		Date dateParticipateIns = null;
		if(dateParticipateInsStr != null){
			dateParticipateIns = new Date(Long.parseLong(dateParticipateInsStr));
		}
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			if(emplLeave != null && benefitTypeId != null){
				String partyId = emplLeave.getString("partyId");
				if(dateParticipateIns == null){
					dateParticipateIns = InsuranceHelper.getDateParticipateIns(delegator, partyId);
					if(dateParticipateIns == null){
						dateParticipateIns = new Date(Calendar.getInstance().getTimeInMillis());
					}
				}
				BigDecimal maxDayLeave = BigDecimal.ZERO;
				Date nowDate = new Date(Calendar.getInstance().getTimeInMillis());
				Integer totalMonth = DateUtil.getMonthBetweenTwoDate(dateParticipateIns, nowDate);
				Map<String, Object> data = FastMap.newInstance();
				data.put("totalMontParticipateIns", totalMonth);
				List<Map<String, Object>> actionCondMapList = InsuranceWorker.getListActionAndCondBenefitType(delegator, benefitTypeId, data, "IB_YEAR_PAR");
				GenericValue insuranceAllowanceBenefitType = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false);
				String isIncAnnualLeave = insuranceAllowanceBenefitType.getString("isIncAnnualLeave");
				Float totalDayLeave = 0f;
				if("Y".equals(isIncAnnualLeave)){
					totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave, true);
				}else{
					totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave, false);
				}
				for(Map<String, Object> tempMap: actionCondMapList){
					List<GenericValue> actions = (List<GenericValue>)tempMap.get("actions");
					if(actions != null){
						for(GenericValue action: actions){
							String benefitTypeActionEnumId = action.getString("benefitTypeActionEnumId");
							BigDecimal quantity = action.getBigDecimal("quantity");
							String uomId = action.getString("uomId");
							if("INS_BE_MAX_LEAVE".equals(benefitTypeActionEnumId)){
								if("TF_mon".equals(uomId)){
									quantity = quantity.multiply(new BigDecimal(30));
								}else if("TF_wk".equals(uomId)){
									quantity = quantity.multiply(new BigDecimal(7));
								}else if("TF_yr".equals(uomId)){
									quantity = quantity.multiply(new BigDecimal(365));
								}
								maxDayLeave = quantity;
							}
						}
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("maxDayLeave", maxDayLeave);
				request.setAttribute("totalDayLeave", totalDayLeave);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	@SuppressWarnings("unchecked")
	public static String getInsuranceSalaryOfEmplInPeriod(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		Timestamp thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		try {
			Map<String, Object> resultService = dispatcher.runSync("getPartyInsuranceSalary", UtilMisc.toMap("partyId", partyId, 
							"fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			List<String> partyInsSalIdList = (List<String>) resultService.get("partyInsSalId");
			if(UtilValidate.isNotEmpty(partyInsSalIdList)){
				String partyInsSalId = partyInsSalIdList.get(0);
				GenericValue partyInsuranceSalary = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyInsSalId", partyInsSalId), false);
				request.setAttribute("salary", partyInsuranceSalary.get("amount"));
				request.setAttribute("allowanceSeniority", partyInsuranceSalary.get("allowanceSeniority"));
				request.setAttribute("allowanceSeniorityExces", partyInsuranceSalary.get("allowanceSeniorityExces"));
				request.setAttribute("allowancePosition", partyInsuranceSalary.get("allowancePosition"));
				request.setAttribute("allowanceOther", partyInsuranceSalary.get("allowanceOther"));
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
	
	public static String createInsEmplNewlyAdjustParticipate(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String partyId = (String)paramMap.get("partyId");
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(fromDateStr)));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong(thruDateStr)), timeZone, locale);
		}
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createInsEmplAdjustParticipate", paramMap, userLogin, timeZone, locale);
				context.put("statusId", "PARTICIPATING");
				context.put("insuranceOriginateTypeId", "NEWLY_PARTICIPATE");
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				Map<String, Object> resultServices = dispatcher.runSync("createInsEmplAdjustParticipate", context);
				if(!ServiceUtil.isSuccess(resultServices)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
					TransactionUtil.rollback();
					return "error";
				}
				String insuranceSocialNbr = (String)paramMap.get("insuranceSocialNbr");
				String dateParticipateInsStr = (String)context.get("dateParticipateIns");
				Date dateParticipateIns = dateParticipateInsStr != null? new Date(Long.parseLong(dateParticipateInsStr)) : null;
				if(insuranceSocialNbr != null || dateParticipateIns != null){
					resultServices = dispatcher.runSync("updatePerson", UtilMisc.toMap("partyId", partyId, "insuranceSocialNbr", insuranceSocialNbr, 
							"dateParticipateIns", dateParticipateIns, "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
				}
				context = ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceSalary", paramMap, userLogin, timeZone, locale);
				resultServices = dispatcher.runSync("createPartyInsuranceSalary", context);
				if(!ServiceUtil.isSuccess(resultServices)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
					TransactionUtil.rollback();
					return "error";
				}
				List<GenericValue> insuranceTypeCompulsoryList = delegator.findByAnd("InsuranceType", UtilMisc.toMap("isCompulsory", "Y"), null, false);
				List<String> insuranceTypeIdList = EntityUtil.getFieldListFromEntityList(insuranceTypeCompulsoryList, "insuranceTypeId", true);
				String insuranceTypeNotCompulsory = (String)paramMap.get("insuranceTypeNotCompulsory");
				if(insuranceTypeNotCompulsory != null){
					JSONArray insuranceTypeNotCompulsoryJson = JSONArray.fromObject(insuranceTypeNotCompulsory);
					for(int i = 0; i < insuranceTypeNotCompulsoryJson.size(); i++){
						insuranceTypeIdList.add(insuranceTypeNotCompulsoryJson.getString(i));
					}
				}
				context.clear();
				context.put("partyId", partyId);
				context.put("statusId", "PARTICIPATING");
				context.put("fromDate", fromDate);
				context.put("thruDate", thruDate);
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				context.put("userLogin", userLogin);
				for(String insuranceTypeId: insuranceTypeIdList){
					context.put("insuranceTypeId", insuranceTypeId);
					resultServices = dispatcher.runSync("createPartyParticipateInsurance", context);
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
				}
				String insHealthCard = (String)paramMap.get("insHealthCard");
				String insHealthFromDateStr = (String)paramMap.get("insHealthFromDate");
				String insHealthThruDateStr = (String)paramMap.get("insHealthThruDate");
				if(insHealthCard != null && insHealthCard.trim().length() > 0 && insHealthFromDateStr != null){
					String hospitalId = (String)paramMap.get("hospitalId");
					Timestamp insHealthFromDate = insHealthFromDateStr != null? UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(insHealthFromDateStr))): null;
					Timestamp insHealthThruDate = insHealthThruDateStr != null? UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong(insHealthThruDateStr)), timeZone, locale): null;
					context.clear();
					context.put("insHealthCard", insHealthCard);
					context.put("partyId", partyId);
					context.put("hospitalId", hospitalId);
					context.put("fromDate", insHealthFromDate);
					context.put("thruDate", insHealthThruDate);
					context.put("locale", locale);
					context.put("timeZone", timeZone);
					context.put("userLogin", userLogin);
					resultServices = dispatcher.runSync("createPartyInsuranceHealth", context);
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceNewlyParticipateAdjustSuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createEmplReparicipate(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Timestamp fromDate = null, thruDate = null, previousMonthEnd = null;
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		if(fromDateStr != null){
			fromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(fromDateStr)));
			previousMonthEnd = UtilDateTime.getMonthEnd(UtilDateTime.getMonthStart(fromDate, 0, -1), timeZone, locale);
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong(thruDateStr)), timeZone, locale);
		}
		String partyIdParam = (String)paramMap.get("partyIds");
		String isInsuranceSalaryUnchange = (String)paramMap.get("isInsuranceSalaryUnchange");
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		if(partyIdParam == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmploymentUiLabels", "NoPartyChoose", locale));
			return "error";
		}
		try {
			TransactionUtil.begin();
			Map<String, Object> context;
			try {
				context = ServiceUtil.setServiceFields(dispatcher, "createInsEmplAdjustParticipate", paramMap, userLogin, timeZone, locale);
				context.put("statusId", "PARTICIPATING");
				context.put("insuranceOriginateTypeId", "REPARTICIPATE");
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				JSONArray partyIdJson = JSONArray.fromObject(partyIdParam);
				List<GenericValue> insuranceTypeCompulsoryList = delegator.findByAnd("InsuranceType", UtilMisc.toMap("isCompulsory", "Y"), null, false);
				List<String> insuranceTypeIdList = EntityUtil.getFieldListFromEntityList(insuranceTypeCompulsoryList, "insuranceTypeId", true);
				String insuranceTypeNotCompulsory = (String)paramMap.get("insuranceTypeNotCompulsory");
				if(insuranceTypeNotCompulsory != null){
					JSONArray insuranceTypeNotCompulsoryJson = JSONArray.fromObject(insuranceTypeNotCompulsory);
					for(int i = 0; i < insuranceTypeNotCompulsoryJson.size(); i++){
						insuranceTypeIdList.add(insuranceTypeNotCompulsoryJson.getString(i));
					}
				}
				Map<String, Object> partyParticipateInsMap = FastMap.newInstance();
				partyParticipateInsMap.put("statusId", "PARTICIPATING");
				partyParticipateInsMap.put("fromDate", fromDate);
				partyParticipateInsMap.put("thruDate", thruDate);
				partyParticipateInsMap.put("locale", locale);
				partyParticipateInsMap.put("timeZone", timeZone);
				partyParticipateInsMap.put("userLogin", userLogin);
				
				Map<String, Object> partySalMap = FastMap.newInstance();
				partySalMap.put("locale", locale);
				partySalMap.put("timeZone", timeZone);
				partySalMap.put("userLogin", userLogin);
				partySalMap.put("fromDate", fromDate);
				partySalMap.put("thruDate", thruDate);
				if(!"Y".equals(isInsuranceSalaryUnchange)){
					partySalMap.put("amount", paramMap.get("amount") != null? new BigDecimal((String)paramMap.get("amount")) : null);
					partySalMap.put("allowanceSeniority", paramMap.get("allowanceSeniority") != null? new BigDecimal((String)paramMap.get("allowanceSeniority")) : null);
					partySalMap.put("allowanceSeniorityExces", paramMap.get("allowanceSeniorityExces") != null? new BigDecimal((String)paramMap.get("allowanceSeniorityExces")) : null);
					partySalMap.put("allowancePosition", paramMap.get("allowancePosition") != null? new BigDecimal((String)paramMap.get("allowancePosition")) : null);
					partySalMap.put("allowanceOther", paramMap.get("allowanceOther") != null? new BigDecimal((String)paramMap.get("allowanceOther")) : null);
				}
				for(int i = 0; i < partyIdJson.size(); i++){
					String partyId = partyIdJson.getString(i);
					context.put("partyId", partyId);
					Map<String, Object> resultServices = dispatcher.runSync("createInsEmplAdjustParticipate", context);
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
					partyParticipateInsMap.put("partyId", partyId);
					for(String insuranceTypeId: insuranceTypeIdList){
						partyParticipateInsMap.put("insuranceTypeId", insuranceTypeId);
						resultServices = dispatcher.runSync("createPartyParticipateInsurance", partyParticipateInsMap);
						if(!ServiceUtil.isSuccess(resultServices)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
							TransactionUtil.rollback();
							return "error";
						}
					}
					//thruDate party insurance salary before reparticipate
					GenericValue partyInsuranceSalary = InsuranceHelper.getPartyInsuranceSalaryLastest(delegator, partyId);
					partySalMap.put("partyGroupId", partyInsuranceSalary.get("partyGroupId"));
					partySalMap.put("emplPositionTypeId", partyInsuranceSalary.get("emplPositionTypeId"));
					if("Y".equals(isInsuranceSalaryUnchange)){
						partySalMap.put("amount", partyInsuranceSalary.get("amount"));
						partySalMap.put("allowanceSeniority", partyInsuranceSalary.get("allowanceSeniority"));
						partySalMap.put("allowanceSeniorityExces", partyInsuranceSalary.get("allowanceSeniorityExces"));
						partySalMap.put("allowancePosition", partyInsuranceSalary.get("allowancePosition"));
						partySalMap.put("allowanceOther", partyInsuranceSalary.get("allowanceOther"));
					}
					partyInsuranceSalary.put("thruDate", previousMonthEnd);
					partyInsuranceSalary.store();
					
					//create new party insurance salary
					partySalMap.put("partyId", partyId);
					resultServices = dispatcher.runSync("createPartyInsuranceSalary", partySalMap);
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", 
						"InsuranceReparticipateAdjustSuccess", UtilMisc.toMap("totalEmpl", partyIdJson.size()), locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createEmplSuspendOrStopParticipate(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		String supplementFromDateStr = (String)paramMap.get("supplementFromDate");
		String supplementThruDateStr = (String)paramMap.get("supplementThruDate");
		Timestamp fromDate = null, thruDate = null;
		String suspendReasonId = (String)paramMap.get("suspendReasonId");
		if(fromDateStr != null){
			fromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(fromDateStr)));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong(thruDateStr)), timeZone, locale);
		}
		if(supplementFromDateStr != null){
			paramMap.put("supplementFromDate", UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(supplementFromDateStr))));
		}
		if(supplementThruDateStr != null){
			paramMap.put("supplementThruDate", UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(supplementThruDateStr))));
		}
		String partyIdParam = (String)paramMap.get("partyIds");
		try {
			try {
				TransactionUtil.begin();
				GenericValue suspendInsReasonType = delegator.findOne("SuspendInsReasonType", UtilMisc.toMap("suspendReasonId", suspendReasonId), false);
				String insuranceOriginateTypeId = suspendInsReasonType.getString("insuranceOriginateTypeId");
				GenericValue insuranceOrginateType = delegator.findOne("InsuranceOriginateType", UtilMisc.toMap("insuranceOriginateTypeId", insuranceOriginateTypeId), false);
				paramMap.put("fromDate", fromDate);
				paramMap.put("thruDate", thruDate);
				if("STOP_PARTICIPATE".equals(insuranceOriginateTypeId)){
					paramMap.put("thruDate", null);
				}
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createInsEmplAdjustParticipate", paramMap, userLogin, timeZone, locale);
				context.put("insuranceOriginateTypeId", insuranceOriginateTypeId);
				context.put("statusId", insuranceOrginateType.get("statusId"));
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				JSONArray partyIdJson = JSONArray.fromObject(partyIdParam);
				for(int i = 0; i < partyIdJson.size(); i++){
					String partyId = partyIdJson.getString(i);
					context.put("partyId", partyId);
					Map<String, Object> resultServices = dispatcher.runSync("createInsEmplAdjustParticipate", context);
					if(!ServiceUtil.isSuccess(resultServices)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", 
						"InsuranceAdjustSuspendStopParticipateSuccess", UtilMisc.toMap("totalEmpl", partyIdJson.size()), locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String createInsAdjustEmplSalaryAndJob(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		if(fromDateStr != null){
			Timestamp fromDate = UtilDateTime.getMonthStart(new Timestamp(Long.parseLong(fromDateStr)));
			paramMap.put("fromDate", fromDate);
		}
		if(thruDateStr != null){
			Timestamp thruDate = UtilDateTime.getMonthEnd(new Timestamp(Long.parseLong(thruDateStr)), timeZone, locale);
			paramMap.put("thruDate", thruDate);
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createInsAdjustEmplSalaryAndJob", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultServices = dispatcher.runSync("createInsAdjustEmplSalaryAndJob", context);
			if(!ServiceUtil.isSuccess(resultServices)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAdjustSalaryAndJobTitleSuccess", locale));
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
	
	public static String getPartyInsuranceHealthLastest(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> partyHealthInsuranceList = delegator.findByAnd("PartyHealthInsuranceAndHospitalPerson", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-fromDate"), false);
			if(UtilValidate.isNotEmpty(partyHealthInsuranceList)){
				GenericValue partyHealthInsurance = partyHealthInsuranceList.get(0);
				Map<String, Object> result = partyHealthInsurance.getAllFields();
				result.put("fromDate", partyHealthInsurance.getTimestamp("fromDate").getTime());
				result.put("thruDate", partyHealthInsurance.get("thruDate") != null? partyHealthInsurance.getTimestamp("thruDate").getTime(): null);
				request.setAttribute("result", result);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
