package com.olbius.insurance.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.insurance.InsuranceEngine;
import com.olbius.payroll.PayrollUtil;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class InsuraceServices {
	public static Map<String, Object> createInsuranceReport(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		GenericValue participateInsuranceReport = delegator.makeValidValue("ParticipateInsuranceReport", context);
		String reportId = delegator.getNextSeqId("ParticipateInsuranceReport");
		participateInsuranceReport.set("reportId", reportId);
		participateInsuranceReport.set("fromDate", fromDate);
		participateInsuranceReport.set("thruDate", thruDate);
		participateInsuranceReport.set("createdDate", UtilDateTime.nowTimestamp());
		participateInsuranceReport.set("lastModified", UtilDateTime.nowTimestamp());
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			delegator.create(participateInsuranceReport);
			retMap.put("reportId", reportId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("hrolbiusUiLabels", "createInsuranceReportError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("hrolbiusUiLabels", "createInsuranceReportSuccess", locale));
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createEmplParticipateInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> insuranceTypeId = (List<String>)context.get("insuranceTypeId");
		String partyId = (String)context.get("partyId");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		Locale locale = (Locale)context.get("locale");
		try {
			for(String tempInsuranceType: insuranceTypeId){
				if("REPARTICIPATE".equals(insuranceParticipateTypeId)){
					Map<String, Object> resultService = dispatcher.runSync("getCurrStatusPartyInsuranceType", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", tempInsuranceType, "userLogin", context.get("userLogin"), "timeZone", context.get("timeZone")));
					String statusId = (String)resultService.get("statusId");
					if(!"SUSPEND_PARTICIPATE".equals(statusId)){
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", "SUSPEND_PARTICIPATE"), false);
						GenericValue insuranceType = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", tempInsuranceType), false);
						return ServiceUtil.returnError(UtilProperties.getMessage("hrolbiusUiLabels", "CannotReparicipate_statusIsNotSuspend", UtilMisc.toMap("insuranceType", insuranceType.getString("description"), "status", statusItem.getString("description")), locale));
					}
				}
				GenericValue partyInsuranceReport = delegator.makeValue("PartyInsuranceReport");
				partyInsuranceReport.set("reportId", (String) context.get("reportId"));
				partyInsuranceReport.set("partyId", (String) context.get("partyId"));
				partyInsuranceReport.set("insuranceParticipateTypeId", (String) context.get("insuranceParticipateTypeId"));
				partyInsuranceReport.set("fromDate", (Timestamp) context.get("fromDate"));
				partyInsuranceReport.set("thruDate", (Timestamp) context.get("thruDate"));
				partyInsuranceReport.set("insuranceTypeId", tempInsuranceType);
				partyInsuranceReport.create();
				//update status
				GenericValue partyParticipateInsurance = delegator.makeValue("PartyParticipateInsurance");
				partyParticipateInsurance.set("partyId", partyId);
				partyParticipateInsurance.set("statusId", "PARTICIPATING");
				partyParticipateInsurance.set("statusDatetime", UtilDateTime.nowTimestamp());
				partyParticipateInsurance.set("insuranceTypeId", tempInsuranceType);
				partyParticipateInsurance.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "AddEmplParticipateInsuraceSuccessful", locale));
	}
	public static Map<String, Object> updateInsuranceReport(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String reportId = (String)context.get("reportId");
		try {
			GenericValue insuranceReport = delegator.findOne("ParticipateInsuranceReport", UtilMisc.toMap("reportId", reportId), false);
			insuranceReport.setNonPKFields(context);
			insuranceReport.set("lastModified", UtilDateTime.nowTimestamp());
			insuranceReport.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getCurrStatusPartyInsuranceType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> partyParticipateInsurance = delegator.findByAnd("PartyParticipateInsurance", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", insuranceTypeId), UtilMisc.toList("-statusDatetime"), false);
			if(UtilValidate.isNotEmpty(partyParticipateInsurance)){
				String statusId = EntityUtil.getFirst(partyParticipateInsurance).getString("statusId");
				retMap.put("statusId", statusId);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getSuspendInsuranceReasonList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		Map<String, Object> retMap = FastMap.newInstance();
		List<String> suspendReasonList = FastList.newInstance();
		try {
			List<GenericValue> suspendReasonListGv = delegator.findByAnd("SuspendParticipateInsuranceReason", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), UtilMisc.toList("suspendReasonId"), false);
			for(GenericValue tempSuspend: suspendReasonListGv){
				suspendReasonList.add(tempSuspend.getString("description") + ": " + tempSuspend.getString("suspendReasonId"));
			}
			retMap.put("suspendReasonList", suspendReasonList);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> suspendEmplParticipateInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		Map<String, Object> resultService = FastMap.newInstance();
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		Locale locale = (Locale)context.get("locale");
		List<String> insuranceTypeId = (List<String>)context.get("insuranceTypeId");
		try {
			//List<GenericValue> insuranceType = delegator.findByAnd("InsuranceType", null, null, false);
			for(String tempInsuranceTypeId: insuranceTypeId){
				//String insuranceTypeId = tempInsuranceType.getString("insuranceTypeId");
				resultService = dispatcher.runSync("getCurrStatusPartyInsuranceType", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", tempInsuranceTypeId, "userLogin", context.get("userLogin"), "timeZone", context.get("timeZone")));
				String statusId = (String)resultService.get("statusId");
				if("PARTICIPATING".equals(statusId)){
					GenericValue partyInsuranceReport = delegator.makeValue("PartyInsuranceReport");
					partyInsuranceReport.setNonPKFields(context);
					partyInsuranceReport.setPKFields(context);
					partyInsuranceReport.set("insuranceTypeId", tempInsuranceTypeId);
					partyInsuranceReport.set("statusPaymentId", "INS_PAYMENT_CREATED");
					partyInsuranceReport.create();
					
					//update status
					String newStatusId = null;
					if("SUSPEND_PARTICIPATE".equals(insuranceParticipateTypeId)){
						newStatusId = "SUSPEND_PARTICIPATE";
					}else if("STOP_PARTICIPATE".equals(insuranceParticipateTypeId)){
						newStatusId = "STOP_PARTICIPATE";
					}
					if(UtilValidate.isNotEmpty(newStatusId)){
						GenericValue partyParticipateInsurance = delegator.makeValue("PartyParticipateInsurance");
						partyParticipateInsurance.set("partyId", partyId);
						partyParticipateInsurance.set("statusId", newStatusId);
						partyParticipateInsurance.set("statusDatetime", UtilDateTime.nowTimestamp());
						partyParticipateInsurance.set("insuranceTypeId", tempInsuranceTypeId);
						partyParticipateInsurance.set("statusId", newStatusId);
						partyParticipateInsurance.create();	
					}
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "SuspendEmplParticipateInsuraceSuccessful", locale));
	}
	
	public static Map<String, Object> createInsuranceType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "CreateInsuranceTypeSuccessful", locale));
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(UtilValidate.isEmpty(insuranceTypeId)){
				insuranceTypeId = delegator.getNextSeqId("insuranceTypeId");
			}else{
				insuranceTypeId = insuranceTypeId.trim();
				GenericValue checkEntity = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", insuranceTypeId), false);
				if(checkEntity != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "InsuranceTypeIsExists", locale));
				}
				if(insuranceTypeId.contains(" ")){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "IdCannotContainsSpaceCharacters", locale));
				}
			}
			Map<String, Object> results = dispatcher.runSync("createPayrollParameters", UtilMisc.toMap("code", insuranceTypeId, "userLogin", userLogin, "name", context.get("description"), "type", "INSURANCE_REF"));
			if(ServiceUtil.isSuccess(results)){
				String description= (String)context.get("description");
				String isCompulsory=(String)context.get("isCompulsory");
				String employerRate=(String)context.get("employerRate");
				String employeeRate=(String)context.get("employeeRate");
				Double employerDouble =0d;
				Double employeeDouble = 0d;
				if(UtilValidate.isNotEmpty(employerRate)&& !employerRate.equals("null")){
					employerDouble = Double.parseDouble(employerRate)/100;
					
				}
				if(UtilValidate.isNotEmpty(employeeRate)&& !employeeRate.equals("null")){
					
					employeeDouble= Double.parseDouble(employeeRate)/100;
				}
				
				GenericValue insuranceType = delegator.makeValue("InsuranceType");
				insuranceType.put("description",description);
				insuranceType.put("isCompulsory",isCompulsory);
				insuranceType.put("employerRate", employerDouble);
				insuranceType.put("employeeRate", employeeDouble);
				insuranceType.set("insuranceTypeId", insuranceTypeId);
				delegator.create(insuranceType);
			}else{
				String errorMsg = ServiceUtil.getErrorMessage(results);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotCreateInsuranceType_ErrorWhenCreateParam", UtilMisc.toMap("errorMsg", errorMsg), locale));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.put("insuranceTypeId", insuranceTypeId);
		return result;
	}
	/*update insurance type*/
	public static Map<String, Object> updateInsuranceType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue insuranceType = delegator.makeValue("InsuranceType");
		insuranceType.setAllFields(context, false, null, null);
		String employeeRate = (String) context.get("employeeRate");
		String employerRate = (String) context.get("employeeRate");
		Double eer = new Double(employeeRate);
		Double err = new Double(employerRate);
		insuranceType.set("employeeRate", eer);
		insuranceType.set("employerRate", err);
		try {
			insuranceType.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	/*delete insurance type*/
	public static Map<String, Object> deleteInsuranceType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		try {
			GenericValue input = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", insuranceTypeId), false);
			input.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> createEmplInsuranceNbr(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyInsurance = delegator.makeValue("PartyInsurance");
		Locale locale = (Locale)context.get("locale");
		partyInsurance.setAllFields(context, false, null, null);
		try {
			partyInsurance.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "createEmplInsuranceNbrSuccessful", locale));
	}
	
	public static Map<String, Object> updateSocialAndHealthInsurance(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String socialInsuranceNbr = (String)context.get("socialInsuranceNbr");
		String healthInsuranceNbr = (String)context.get("healthInsuranceNbr");
		Timestamp socialInsuranceFromDate = (Timestamp)context.get("socialInsuranceFromDate");
		Timestamp socialInsuranceThruDate = (Timestamp)context.get("socialInsuranceThruDate");
		Timestamp healthInsuranceFromDate = (Timestamp)context.get("healthInsuranceFromDate");
		Timestamp healthInsuranceThruDate = (Timestamp)context.get("healthInsuranceThruDate");
		String partyHealthCareId = (String)context.get("partyHealthCareId");
		try {
			List<GenericValue> partySocialInsuranceList = delegator.findByAnd("PartyInsurance", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", "BHXH"), UtilMisc.toList("-fromDate"), false);
			List<GenericValue> partyHealthInsuranceList = delegator.findByAnd("PartyInsurance", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", "BHYT"), UtilMisc.toList("-fromDate"), false);
			GenericValue partySocialInsurance, partyHealthInsurance;  
			if(UtilValidate.isEmpty(partySocialInsuranceList)){
				partySocialInsurance = delegator.makeValue("PartyInsurance");
				partySocialInsurance.set("partyId", partyId);
				partySocialInsurance.set("insuranceTypeId", "BHXH");
				//partySocialInsurance.create();
			}else{
				partySocialInsurance = EntityUtil.getFirst(partySocialInsuranceList);
			}
			if(UtilValidate.isEmpty(partyHealthInsuranceList)){
				partyHealthInsurance = delegator.makeValue("PartyInsurance");
				partyHealthInsurance.set("partyId", partyId);
				partyHealthInsurance.set("insuranceTypeId", "BHYT");
				//partyHealthInsurance.create();
			}else{
				partyHealthInsurance = EntityUtil.getFirst(partyHealthInsuranceList);
			}
			
			if(UtilValidate.isNotEmpty(socialInsuranceNbr)){
				partySocialInsurance.set("insuranceNumber", socialInsuranceNbr);
			}
			if(UtilValidate.isNotEmpty(healthInsuranceNbr)){
				partyHealthInsurance.set("insuranceNumber", healthInsuranceNbr);
			}
			if(UtilValidate.isNotEmpty(socialInsuranceFromDate)){
				partySocialInsurance.set("fromDate", socialInsuranceFromDate);
			}
			if(UtilValidate.isNotEmpty(socialInsuranceThruDate)){
				partySocialInsurance.set("thruDate", socialInsuranceThruDate);			
			}
			if(UtilValidate.isNotEmpty(healthInsuranceFromDate)){
				partyHealthInsurance.set("fromDate", healthInsuranceFromDate);
			}
			if(UtilValidate.isNotEmpty(healthInsuranceThruDate)){
				partyHealthInsurance.set("thruDate", healthInsuranceThruDate);
			}
			if(UtilValidate.isNotEmpty(partyHealthCareId)){
				partyHealthInsurance.set("partyHealthCareId", partyHealthCareId);
			}
			delegator.createOrStore(partySocialInsurance);
			delegator.createOrStore(partyHealthInsurance);			
		}catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> getSocialAndHealthInsuranceNbr(DispatchContext dctx, Map<String, Object> context){
		//Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Map<String, Object> retMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.SUCCESS_MESSAGE);
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		try {
			Map<String, Object> resultService = dispatcher.runSync("getInsuranceNbr", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", "BHXH"));
			retMap.put("socialInsuranceNbr", resultService.get("insuranceNbr"));
			resultService = dispatcher.runSync("getInsuranceNbr", UtilMisc.toMap("partyId", partyId, "insuranceTypeId", "BHYT"));	
			retMap.put("healthInsuranceNbr", resultService.get("insuranceNbr"));
			
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	public static Map<String, Object> getInsuranceNbr(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> partyInsurance = delegator.findList("PartyInsurance", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyInsurance)){
				GenericValue partyIns = EntityUtil.getFirst(partyInsurance);
				retMap.put("insuranceNbr", partyIns.getString("insuranceNumber"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> calcPartyParticipateMonthInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		Timestamp calcFromDate = (Timestamp)context.get("calcFromDate");
		Timestamp calcThruDate = (Timestamp)context.get("calcThruDate");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		List<EntityCondition> conditions = FastList.newInstance();
		int month = 0;
		DateTime calFrom;
		DateTime calThru;
		if(UtilValidate.isEmpty(calcThruDate)){
			/*Calendar cal = Calendar.getInstance();
			int date = cal.get(Calendar.DATE);*/
			calcThruDate = UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), timeZone, locale);	
		}
		Map<String, Object> retMap = FastMap.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
		if(UtilValidate.isNotEmpty(calcFromDate)){
			conditions.add(EntityCondition.makeCondition("thruDateDate", EntityOperator.GREATER_THAN, calcFromDate));
		}		
		try {
			List<GenericValue> partyInsuranceList = delegator.findList("PartyInsurance", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("fromDate"), null, false);
			for(GenericValue tempPartyInsurance: partyInsuranceList){
				Timestamp thruDate = tempPartyInsurance.getTimestamp("thruDate");
				Timestamp fromDate = tempPartyInsurance.getTimestamp("fromDate");
				if(fromDate.before(calcFromDate)){
					fromDate = calcFromDate;
				}
				if(thruDate == null || thruDate.after(calcThruDate)){
					thruDate = calcThruDate;
				}
				calFrom = new DateTime(fromDate.getTime());
				calThru = new DateTime(thruDate.getTime());//
				month += Months.monthsBetween(calFrom, calThru).getMonths();
				//month = month + (calThru.get(Calendar.MONTH) - calFrom.get(Calendar.MONTH) + 1);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retMap.put("numberMonthParticipate", month);
		return retMap;
	}
	
	public static Map<String, Object> calcPartyInsurancePaymentAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String reportId = (String)context.get("reportId");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String insurancePaymentId = (String)context.get("insurancePaymentId");
		String uomId = (String)context.get("uomId");
		Map<String, Object> retMap = FastMap.newInstance();
		String calcValue = "0";
		retMap.put("insurancePaymentId", insurancePaymentId);
		try {
			GenericValue partyInsuranceReport = delegator.findOne("PartyInsuranceReport", UtilMisc.toMap("partyId", partyId, "reportId", reportId, "insuranceParticipateTypeId", insuranceParticipateTypeId, "insuranceTypeId", insuranceTypeId), false);			
			String suspendReasonId = partyInsuranceReport.getString("suspendReasonId");
			GenericValue suspendReason = delegator.findOne("SuspendParticipateInsuranceReason", UtilMisc.toMap("suspendReasonId", suspendReasonId), false);
			String formulaCode = suspendReason.getString("functionCalcBenefit");
			//GenericValue insuranceFormula = delegator.findOne("InsuranceFormula", UtilMisc.toMap("code", formulaCode), false);
			//String code = insuranceFormula.getString("code");
			String value = InsuranceEngine.buildFormula(dctx, partyInsuranceReport, formulaCode, null, timeZone, locale);
			Map<String, String> dataMap = FastMap.newInstance();
			calcValue = InsuranceEngine.calcValueInsuranceAmount(dctx, partyInsuranceReport, value, dataMap);
			calcValue = PayrollUtil.evaluateStringExpression(calcValue);
			BigDecimal paymentAmount = new BigDecimal(calcValue);
			GenericValue partyInsurancePayment = delegator.findOne("PartyInsurancePayment", UtilMisc.toMap("insurancePaymentId", insurancePaymentId, "partyId", partyId, "reportId", reportId, "insuranceParticipateTypeId", insuranceParticipateTypeId, "insuranceTypeId", insuranceTypeId), false);
			partyInsurancePayment.set("paymentAmount", paymentAmount);
			if(uomId == null){
				Properties generalProperties = UtilProperties.getProperties("general");				
				uomId = generalProperties.getProperty("currency.uom.id.default");
			}
			partyInsurancePayment.set("uomId", uomId);
			partyInsurancePayment.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	public static Map<String, Object> createInsurancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String insurancePaymentName = (String)context.get("insurancePaymentName");
		Integer month = (Integer)context.get("month");
		Integer year = (Integer)context.get("year");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		HttpServletRequest request = (HttpServletRequest) context.get("request");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		if(timeZone == null){
			timeZone = UtilHttp.getTimeZone(request);
		}
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		Timestamp timetamp = new Timestamp(cal.getTimeInMillis());
		Timestamp fromDate = UtilDateTime.getMonthStart(timetamp);
		Timestamp thruDate = UtilDateTime.getMonthEnd(timetamp, timeZone, locale);
		GenericValue insurancePayment = delegator.makeValue("InsurancePayment");
		String insurancePaymentId = delegator.getNextSeqId("InsurancePayment");
		insurancePayment.set("insurancePaymentName", insurancePaymentName);
		insurancePayment.set("fromDate", fromDate);
		insurancePayment.set("thruDate", thruDate);
		insurancePayment.set("insuranceTypeId", insuranceTypeId);
		insurancePayment.set("createdDate", UtilDateTime.nowTimestamp());
		insurancePayment.set("insurancePaymentId", insurancePaymentId);
		retMap.put("insurancePaymentId", insurancePaymentId);
		try {
			insurancePayment.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("hrolbiusUiLabels", "createInsurancePaymentSuccess", locale));
		return retMap;
	}
	/*update insurance payment*/
	public static Map<String, Object> updateInsurancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue insuranceFormula = delegator.makeValue("InsurancePayment");
		insuranceFormula.setAllFields(context, false, null, null);
		try {
			insuranceFormula.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	/*delete insurance payment*/
	public static Map<String, Object> deleteInsurancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String insurancePaymentId = (String)context.get("insurancePaymentId");
		try {
			GenericValue input = delegator.findOne("InsurancePayment", UtilMisc.toMap("insurancePaymentId", insurancePaymentId), false);
			input.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("hrolbiusUiLabels	", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> createPartyInsurancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyInsuranePayment = delegator.makeValue("PartyInsurancePayment");
		partyInsuranePayment.setAllFields(context, false, null, null);
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");		
		String insurancePaymentId = (String)context.get("insurancePaymentId");
		try {
			List<GenericValue> partyInsurancePayment = delegator.findByAnd("PartyInsurancePayment", UtilMisc.toMap("insurancePaymentId", insurancePaymentId), null, false);
			List<String> partyList = EntityUtil.getFieldListFromEntityList(partyInsurancePayment, "partyId", false);
			if(partyList.contains(partyId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("hrolbiusUiLabels", "PartyAddedToInsurancePayment", locale));
			}
			partyInsuranePayment.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "AddEmplInsurancePaymentSuccess", locale));
		retMap.put("insurancePaymentId", insurancePaymentId);
		return retMap;
	}
	
	/*delete insurance payment*/
	public static Map<String, Object> deletePartyInsurancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String reportId = (String)context.get("reportId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		String insurancePaymentId = (String)context.get("insurancePaymentId");
		try {
			GenericValue input = delegator.findOne("PartyInsurancePayment", 
											UtilMisc.toMap("insuranceTypeId", insuranceTypeId, "partyId", 
											partyId, "reportId", reportId, "insuranceParticipateTypeId", 
											insuranceParticipateTypeId, "insurancePaymentId", insurancePaymentId), false);
			input.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> updatePartyInsuranceReportPaymentStt(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String reportId = (String)context.get("reportId");
		String partyId = (String)context.get("partyId");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		String statusId = (String)context.get("statusPaymentId");
		try {
			GenericValue partyInsuranceReport = delegator.findOne("PartyInsuranceReport", UtilMisc.toMap("partyId", partyId, "reportId", reportId, "insuranceParticipateTypeId", insuranceParticipateTypeId, "insuranceTypeId", insuranceTypeId), false);
			partyInsuranceReport.set("statusPaymentId", statusId);
			partyInsuranceReport.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getInsuranceTypePartyParticipate(DispatchContext dctx, Map<String, Object>context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> partyParticipateIns = delegator.findByAnd("PartyParticipateInsuranceAndStatus", UtilMisc.toMap("partyId", partyId, "statusId", "PARTICIPATING"), UtilMisc.toList("-statusDatetimeLast"), false);
			retMap.put("listInsuranceParicipate", partyParticipateIns);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.SUCCESS_MESSAGE);
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return retMap;
	}
	
	public static Map<String, Object> getFunctionFormula(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String code = (String)context.get("code");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			GenericValue insuranceFormula = delegator.findOne("InsuranceFormula", UtilMisc.toMap("code", code), false);
			if(insuranceFormula != null){
				retMap.put("functionStr", insuranceFormula.getString("function"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateSuspendInsuranceReason(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue suspendReason = delegator.makeValue("SuspendParticipateInsuranceReason");
		suspendReason.setPKFields(context);
		suspendReason.setNonPKFields(context);
		try {
			suspendReason.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createSuspendInsuranceReason(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String suspendReasonId = (String)context.get("suspendReasonId");
		Map<String, Object> retMap = FastMap.newInstance();
		Map<String, Object> ctx = FastMap.newInstance();
		ctx.putAll(context);
		if(suspendReasonId == null){
			suspendReasonId = delegator.getNextSeqId("SuspendParticipateInsuranceReason");
			ctx.put("suspendReasonId", suspendReasonId);
		}
		GenericValue suspendReason = delegator.makeValue("SuspendParticipateInsuranceReason");
		suspendReason.setPKFields(ctx);
		suspendReason.setNonPKFields(ctx);		
		try {
			suspendReason.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retMap.put("suspendReasonId", suspendReasonId);
		retMap.put(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
		return retMap;
	}
	/* remove suspend insurance reason*/
	public static Map<String, Object> deleteSuspendInsuranceReason(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String suspendReasonId = (String)context.get("suspendReasonId");
		try {
			GenericValue input = delegator.findOne("SuspendParticipateInsuranceReason", UtilMisc.toMap("suspendReasonId", suspendReasonId), false);
			input.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("hrolbiusUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("hrolbiusUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createInsuranceFormula(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String code = (String)context.get("code");
		Locale locale = (Locale)context.get("locale");
		if(code == null){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "InsuranceFormulaCodeEmpty", locale));
		}
		code = code.trim();
		if(!CommonUtil.containsValidCharacter(code)){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CodeContainsInvalidLetters", locale));
		}
		String description = (String)context.get("description");
		if(description == null){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "InsuranceFormulaDescEmpty", locale));
		}
		String function = (String)context.get("function");
		if(function == null || function.trim().length() <= 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "InsuranceFormulaFunctionEmpty", locale));
		}
		if(function.endsWith("+") || function.endsWith("-") || function.endsWith("*") || function.endsWith("/") || function.endsWith(".")){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "FormulaIsNotValid", locale));
		}
		description = description.trim();
		/*String[] functionArr = function.split("[\\+\\-\\*\\/\\%]");
		for(String tempStr: functionArr){
			tempStr = tempStr.trim();
			if(tempStr.contains("(")){
				
			}
		}*/
		Map<String, Object> entityMap = FastMap.newInstance();
		entityMap.putAll(context);
		entityMap.put("description", description);
		entityMap.put("code", code);
		
		GenericValue insuranceFormula = delegator.makeValue("InsuranceFormula");
		insuranceFormula.setAllFields(context, false, null, null);
		try {
			insuranceFormula.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updateInsuranceFormula(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue insuranceFormula = delegator.makeValue("InsuranceFormula");
		insuranceFormula.setAllFields(context, false, null, null);
		try {
			insuranceFormula.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	/*delete insurance formula*/
	public static Map<String, Object> deleteInsuranceFormula(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String code = (String)context.get("code");
		try {
			GenericValue input = delegator.findOne("InsuranceFormula", UtilMisc.toMap("code", code), false);
			input.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createPartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		if(thruDate != null && thruDate.before(fromDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
		}
		EntityCondition dateConds = DateUtil.getDateValidConds(fromDate, thruDate);
		EntityCondition condition = EntityCondition.makeCondition(dateConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyId));
		
		try {
			List<GenericValue> checkedEntity = delegator.findList("PartyInsuranceSalary", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(checkedEntity)){
				GenericValue entityErr = checkedEntity.get(0);
				Timestamp fromDateErr = entityErr.getTimestamp("fromDate");
				Timestamp thruDateErr = entityErr.getTimestamp("thruDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateErr);
				String fromDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(fromDate);
				String fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateStr = "___";
				if(thruDate != null){
					cal.setTime(thruDate);
					thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				String thruDateErrStr = "___";
				if(thruDateErr != null){
					cal.setTime(thruDateErr);
					thruDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				BigDecimal amount = entityErr.getBigDecimal("salaryInsurance");
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotCreateBecauseDateInvalid", 
						UtilMisc.toMap("fromDate", fromDateStr, "thruDate", thruDateStr, "emplName", PartyUtil.getPersonName(delegator, partyId),
										"fromDateErr", fromDateErrStr, "thruDateErr", thruDateErrStr, "amount", amount), locale));
			}
			EntityCondition expireConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), 
																	EntityOperator.AND, 
																	EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
			Timestamp thruDateExpired = UtilDateTime.getDayEnd(fromDate, -1L);
			List<GenericValue> exprireList = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(expireConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyId)), null, null, null, false);
			for(GenericValue tempGv: exprireList){
				tempGv.set("thruDate", thruDateExpired);
				tempGv.store();
			}
			GenericValue partyInsuranceSalary = delegator.makeValue("PartyInsuranceSalary");
			String uomId = (String)context.get("uomId");
			if(uomId == null){
				Properties generalProperties = UtilProperties.getProperties("general");				
				uomId = generalProperties.getProperty("currency.uom.id.default");
				context.put("uomId", uomId);
			}
			partyInsuranceSalary.setAllFields(context, false, null, null);
			partyInsuranceSalary.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updatePartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			GenericValue partyInsuranceSalary = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate), false);
			partyInsuranceSalary.setNonPKFields(context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> deleteInsuranceReport(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String reportId = (String)context.get("reportId");
		try {
			List<GenericValue> iplist = delegator.findList("PartyInsurancePayment", EntityCondition.makeCondition("reportId", reportId), null, null, null, false);
			if(iplist == null || iplist.size() == 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
			}
			List<GenericValue> irlist = delegator.findList("PartyInsuranceReport", EntityCondition.makeCondition("reportId", reportId), null, null, null, false);
			if(irlist == null || irlist.size() == 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
			}
			GenericValue reportInsurance = delegator.findOne("ParticipateInsuranceReport", UtilMisc.toMap("reportId", reportId), false);
			reportInsurance.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteReportSet", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> deletePartyInsuranceReport(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String reportId = (String)context.get("reportId");
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");	
		try {
			List<GenericValue> partyInsuraceReport = delegator.findByAnd("PartyInsuranceReport", UtilMisc.toMap("partyId", partyId, "reportId", reportId, "insuranceParticipateTypeId", insuranceParticipateTypeId), null,false);
			if(UtilValidate.isNotEmpty(partyInsuraceReport)){
				for(GenericValue tempPartyInsuranceReport: partyInsuraceReport){
					tempPartyInsuranceReport.remove();
				}
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotFindRecordToDelete", locale));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeletePartyInsurancePayment", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createHealthCareProvider(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> resultService = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String countryGeoId = (String)context.get("countryGeoId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		GenericValue userLogin;
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			resultService = dispatcher.runSync("createPartyGroup", ServiceUtil.setServiceFields(dispatcher, "createPartyGroup", context, userLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resultService)){
				String partyId = (String)resultService.get("partyId");
				retMap.put("partyId", partyId);
				dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "HEALTH_CARE_PROVIDER", "userLogin", userLogin));
				if(countryGeoId != null && stateProvinceGeoId != null){
					GenericValue stateProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
					GenericValue country = delegator.findOne("Geo", UtilMisc.toMap("geoId", countryGeoId), false);
					String address1 = stateProvince.getString("geoName") + ", " + country.getString("geoName");
					dispatcher.runSync("createPartyPostalAddress", UtilMisc.toMap("address1", address1, "city", stateProvince.getString("geoName"), "postalCode", "10000", 
																					"stateProvinceGeoId", stateProvinceGeoId, "countryGeoId", countryGeoId, "partyId", partyId,
																					"contactMechPurposeTypeId", "PRIMARY_LOCATION", "userLogin", userLogin));
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = FastMap.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	successResult.put("listIterator", listReturn);
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, Object> resultService = FastMap.newInstance();
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String partyIdParam = (String[])parameters.get("partyId") != null? ((String[])parameters.get("partyId"))[0] : null;
    	String partyNameParam = (String[])parameters.get("partyName") != null? ((String[])parameters.get("partyName"))[0]: null;
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyGroupId");
    	Organization orgParty;
		try {
			orgParty = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> emplList = orgParty.getEmployeeInOrg(delegator);
			if(partyIdParam != null){
				emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyIdParam + "%")));
			}
			if(partyNameParam != null){
				partyNameParam = partyNameParam.replaceAll("\\s", "");
				List<EntityCondition> tempConds = FastList.newInstance();
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameFirstNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam.toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameLastNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastNameFirstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstNameLastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				emplList = EntityUtil.filterByOr(emplList, tempConds);
				
			}
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
			successResult.put("TotalRows", String.valueOf(totalRows));
			GenericValue socialInsurance = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", "BHXH"), false);
			GenericValue healthInsurance = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", "BHYT"), false);
			Double insuranceSocialRate = 0.0;
			Double insuranceHealthRate = 0.0;
			if(socialInsurance != null){
				insuranceSocialRate = socialInsurance.getDouble("employerRate") + socialInsurance.getDouble("employeeRate");
			}
			if(healthInsurance != null){
				insuranceHealthRate = healthInsurance.getDouble("employerRate") + healthInsurance.getDouble("employeeRate");
			}
			for(GenericValue empl: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = empl.getString("partyId");
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempPartyId));
				tempMap.put("socialRate", insuranceSocialRate);
				tempMap.put("healthRate", insuranceHealthRate);
				
				GenericValue currDept = PartyUtil.getDepartmentOfEmployee(delegator, tempPartyId);
				if(currDept != null){
					tempMap.put("partyGroupId", PartyHelper.getPartyName(delegator, PartyHelper.getPartyName(delegator, currDept.getString("partyIdFrom"), false), false));
				}
				resultService = dispatcher.runSync("getInsuranceNbr", UtilMisc.toMap("partyId", tempPartyId, "insuranceTypeId", "BHXH", "userLogin", userLogin));
				tempMap.put("socialInsuranceNbr", resultService.get("insuranceNbr"));
				resultService = dispatcher.runSync("getInsuranceNbr", UtilMisc.toMap("partyId", tempPartyId, "insuranceTypeId", "BHYT", "userLogin", userLogin));
				tempMap.put("healthInsuranceNbr", resultService.get("insuranceNbr"));
				List<GenericValue> partyInsuranceSalary = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(),
																																	EntityOperator.AND,
																																	EntityCondition.makeCondition("partyId", tempPartyId)), 
																					null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(partyInsuranceSalary)){
					tempMap.put("salaryInsurance", partyInsuranceSalary.get(0).get("salaryInsurance"));
					tempMap.put("salaryInsuranceUomId", partyInsuranceSalary.get(0).get("uomId"));
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyParticipateInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	
    	
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = FastList.newInstance();
    		listSortFields.add("-statusDatetime");
    	}
    	listAllConditions.add(tmpConditon);
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	if(partyId != null){
    		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	}
    	try {
    		listIterator = delegator.find("PartyParticipateInsurance", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQPartyParticipateInsurance service: " + e.toString();
			Debug.logError(e, errMsg);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyTimeInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = FastList.newInstance();
    		listSortFields.add("-fromDate");
    		listSortFields.add("insuranceTypeId");
    	}
    	listAllConditions.add(tmpConditon);
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	if(partyId != null){
    		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	}
    	try {
    		listIterator = delegator.find("PartyInsurance", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPartyTimeInsurance service: " + e.toString();
			Debug.logError(e, errMsg);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = FastList.newInstance();
    		listSortFields.add("-fromDate");    		
    	}
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	if(partyId != null){
    		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	}
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("PartyInsuranceSalary", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPartyInsuranceSalary service: " + e.toString();
			Debug.logError(e, errMsg);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> createPartyInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyInsurance = delegator.makeValue("PartyInsurance");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		if(thruDate != null){
			thruDate = UtilDateTime.getDayEnd(thruDate);
		}
		Map<String, Object> entityMap = FastMap.newInstance();
		entityMap.putAll(context);
		entityMap.put("fromDate", fromDate);
		entityMap.put("thruDate", thruDate);
		try {
			partyInsurance.setAllFields(entityMap, false, null, null);
			partyInsurance.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> deletePartyInsurance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String partyId = (String)context.get("partyId");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		try {
			GenericValue deleteEntity = delegator.findOne("PartyInsurance", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "insuranceTypeId", insuranceTypeId), false);
			if(deleteEntity != null){
				deleteEntity.remove();
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToDelete", (Locale)context.get("locale")));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	/*public static Map<String, Object> getPartyParicipateInsuranceDetails(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listRowDetails = FastList.newInstance();
		retMap.put("listRowDetails", listRowDetails);
		try {
			List<GenericValue> listPartyInsurance = delegator.findByAnd("PartyInsurance", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("insuranceTypeId"), false);
			for(GenericValue tempGv: listPartyInsurance){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("insuranceTypeId", tempGv.getString("insuranceTypeId"));
				tempMap.put("insuranceNumber", tempGv.getString("insuranceNumber"));
				tempMap.put("partyHealthCareId", tempGv.getString("partyHealthCareId"));
				tempMap.put("fromDate", tempGv.getTimestamp("fromDate") != null? tempGv.getTimestamp("fromDate").getTime(): null);
				tempMap.put("thruDate", tempGv.getTimestamp("thruDate") != null? tempGv.getTimestamp("thruDate").getTime(): null);
				listRowDetails.add(tempMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}*/
}
