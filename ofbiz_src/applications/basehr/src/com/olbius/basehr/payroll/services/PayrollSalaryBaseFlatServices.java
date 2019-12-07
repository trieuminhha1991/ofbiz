package com.olbius.basehr.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.payroll.worker.PayrollWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PersonHelper;
import com.olbius.basehr.util.PropertiesUtil;

public class PayrollSalaryBaseFlatServices {
	public static final String module = PayrollSalaryBaseFlatServices.class.getName();
	public static Map<String, Object> updatePartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String workEffortId = (String)context.get("workEffortId");
		String rateTypeId = (String)context.get("rateTypeId");
		String rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
		String periodTypeId = (String)context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp effectiveFromDate = (Timestamp)context.get("effectiveFromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		
		try {
			//expire current rateAmount of employee
			GenericValue rateAmount = delegator.findOne("RateAmount", UtilMisc.toMap("workEffortId", workEffortId, "rateTypeId", rateTypeId, 
																					"rateCurrencyUomId", rateCurrencyUomId, "periodTypeId", periodTypeId,
																					"fromDate", fromDate,
																					"emplPositionTypeId", emplPositionTypeId,
																					"partyId", partyId), false);
			Timestamp thruDate = UtilDateTime.getDayEnd(effectiveFromDate, -1L);
			rateAmount.set("thruDate", thruDate);
			rateAmount.store();
			context.put("fromDate", UtilDateTime.getDayStart(effectiveFromDate));
			//create new rateAmount employee
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyRateAmount", context, userLogin, timeZone, locale);
			dispatcher.runSync("createPartyRateAmount", ctxMap);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String rateTypeId = (String)context.get("rateTypeId");
		String rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
		String periodTypeId = (String)context.get("periodTypeId");
		String workEffortId = (String)context.get("workEffortId");
		String partyId = (String)context.get("partyId");
		String orgId = (String)context.get("orgId");
		Locale locale = (Locale)context.get("locale");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Map<String, Object> primKeys = FastMap.newInstance();
		primKeys.put("rateTypeId", rateTypeId);
		primKeys.put("rateCurrencyUomId", rateCurrencyUomId);
		primKeys.put("periodTypeId", periodTypeId);
		primKeys.put("workEffortId", workEffortId);
		primKeys.put("partyId", partyId);
		primKeys.put("emplPositionTypeId", emplPositionTypeId);
		primKeys.put("fromDate", fromDate);
		primKeys.put("orgId", orgId);
		try {
			GenericValue rateAmount = delegator.findOne("RateAmount", primKeys, false);
			if(rateAmount == null){
				return ServiceUtil.returnError("cannot find rateAmount to update");
			}
			if(thruDate != null){
				if(thruDate.before(fromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
				}
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", partyId));
				conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
															EntityJoinOperator.OR,
															EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
				conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
				conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.NOT_EQUAL, fromDate));
				List<GenericValue> checkEtt = delegator.findList("RateAmount", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(checkEtt)){
					Calendar cal = Calendar.getInstance();
					GenericValue rateAmountErr = checkEtt.get(0);
					Timestamp rateAmountFromDate = rateAmountErr.getTimestamp("fromDate");
					Timestamp rateAmountThruDate = rateAmountErr.getTimestamp("thruDate");
					BigDecimal amount = rateAmountErr.getBigDecimal("rateAmount");
					cal.setTimeInMillis(rateAmountFromDate.getTime());
					String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
					String thruDateErr = null;
					if(rateAmountThruDate != null){
						cal.setTimeInMillis(rateAmountThruDate.getTime());
						thruDateErr = DateUtil.getDateMonthYearDesc(cal);
					}else{
						thruDateErr = UtilProperties.getMessage("BaseHRUiLabels", "CommonAfterThat", locale);	
					}
					cal.setTimeInMillis(fromDate.getTime());
					String fromDateSub = DateUtil.getDateMonthYearDesc(cal);
					String thruDateSet = null;
					cal.setTimeInMillis(thruDate.getTime());
					thruDateSet = DateUtil.getDateMonthYearDesc(cal);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdateRateAmount",  UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
							"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", amount, "partyId", PartyUtil.getPersonName(delegator, partyId)), locale));
				}
			}
			//check whether employee have fullfilment the emplPositionTypeId
			Timestamp thruDateFulfillment = PartyUtil.getThruDateEmplPosition(delegator, emplPositionTypeId, partyId, fromDate);
			if(thruDateFulfillment != null && (thruDate == null || thruDateFulfillment.before(thruDate))){
				String fromDateDesc = DateUtil.getDateMonthYearDesc(fromDate);
				String thruDateFulfillmentDesc = DateUtil.getDateMonthYearDesc(thruDateFulfillment);
				String thruDateDesc = null;
				if(thruDate != null){
					thruDateDesc = DateUtil.getDateMonthYearDesc(thruDate);
				}else{
					thruDateDesc = UtilProperties.getMessage("BaseHRUiLabels", "CommonAfterThat", locale);	
				}
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				String description = emplPositionType.getString("description");
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdateRateAmountEmplNotFullfillPos", 
						UtilMisc.toMap("fromDate", fromDateDesc, "thruDate", thruDateDesc, 
								"partyName", PartyUtil.getPersonName(delegator, partyId),
								"thruDateFulfillment", thruDateFulfillmentDesc,
								"emplPositionType", description), locale));
			}
			rateAmount.setNonPKFields(context);
			rateAmount.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createPartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workEffortId = (String)context.get("workEffortId");
		String rateTypeId = (String)context.get("rateTypeId");
		Locale locale = (Locale)context.get("locale");
		
		try {
			//create new rateAmount 
			GenericValue rateAmount = delegator.makeValidValue("RateAmount", context);			
			rateAmount.setAllFields(context, false, null, null);
			if(UtilValidate.isEmpty(workEffortId)){
				rateAmount.set("workEffortId", "_NA_");
			}
			if(UtilValidate.isEmail(rateTypeId)){
				rateAmount.set("rateTypeId", "_NA_");
			}
			
			//rateAmount.set("fromDate", fromDate);
			delegator.create(rateAmount);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> createPartyRateAmountSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String uomId = (String)context.get("uomId");
		String periodTypeId = (String)context.get("periodTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amount = (String)context.get("amount");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = null;
		Locale locale = (Locale)context.get("locale");
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		if(thruDate != null && fromDate.after(thruDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
		}
		BigDecimal amountValue = new BigDecimal(amount);
		List<EntityCondition> conditions  = FastList.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		List<EntityCondition> dateConds = FastList.newInstance();
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			conditions.add(EntityCondition.makeCondition("orgId", orgId));
			//check time of employee contract
			Timestamp dateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			Timestamp dateEmplLeaveOrg = PersonHelper.getDateEmplLeaveOrg(delegator, partyId);
			if(dateJoinCompany != null && fromDate.before(dateJoinCompany)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotSetSalaryForEmplBeforeJoinCompany", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,"dateJoinCompany", DateUtil.getDateMonthYearDesc(dateJoinCompany)), locale));
			}
			
			if(dateEmplLeaveOrg != null && fromDate.after(dateEmplLeaveOrg)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotSetSalaryForEmplAfterEndContract", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,"dateEndContract", DateUtil.getDateMonthYearDesc(dateEmplLeaveOrg)), locale));
			}
			
			if(thruDate == null){
	    		/*dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	    													EntityOperator.OR,
	    													EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));*/
				EntityCondition tmpConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null),
																		EntityJoinOperator.AND,
																		EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
															EntityJoinOperator.OR,
															tmpConds));
	    	}else{
	    		if(thruDate.before(fromDate)){
	    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
	    		}
	    		EntityCondition condition1 = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null),
											EntityOperator.AND,
											EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	    		condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	    		
	    		EntityCondition condition2 = EntityCondition.makeCondition("thruDate", null);
	    		condition2 = EntityCondition.makeCondition(condition2, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
	    		dateConds.add(EntityCondition.makeCondition(condition1, EntityOperator.OR, condition2));
	    		
	    	}
			List<GenericValue> rateAmountList = delegator.findList("RateAmount", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), EntityOperator.AND, EntityCondition.makeCondition(dateConds)), null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(rateAmountList)){
				GenericValue rateAmountErr = EntityUtil.getFirst(rateAmountList);
				Timestamp rateAmountFromDate = rateAmountErr.getTimestamp("fromDate");
				Timestamp rateAmountThruDate = rateAmountErr.getTimestamp("thruDate");
				BigDecimal rateAmount = rateAmountErr.getBigDecimal("rateAmount");
				String errMsg = "";
				if(thruDate == null && rateAmountThruDate == null){
					errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplSalaryInPeriodIsSetFromFrom", 
							UtilMisc.toMap("amount", rateAmount,
									"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
									"fromDate", DateUtil.getDateMonthYearDesc(rateAmountFromDate),
									"partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
				}else if(thruDate == null && rateAmountThruDate != null){
					errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplSalaryInPeriodIsSetFromFromThru", 
							UtilMisc.toMap("amount", rateAmount,
									"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
									"fromDate", DateUtil.getDateMonthYearDesc(rateAmountFromDate),
									"thruDate", DateUtil.getDateMonthYearDesc(rateAmountThruDate),
									"partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
				}else if(thruDate != null && rateAmountThruDate == null){
					errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplSalaryInPeriodIsSetFromThruFrom", 
							UtilMisc.toMap("amount", rateAmount,
									"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
									"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
									"fromDate", DateUtil.getDateMonthYearDesc(rateAmountFromDate),
									"partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
				}else if(thruDate != null && rateAmountThruDate != null){
					errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplSalaryInPeriodIsSetFromThruFromThru", 
							UtilMisc.toMap("amount", rateAmount,
									"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
									"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
									"fromDate", DateUtil.getDateMonthYearDesc(rateAmountFromDate),
									"thruDate", DateUtil.getDateMonthYearDesc(rateAmountThruDate),
									"partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			
			EntityCondition expireConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
			List<GenericValue> rateAmountExpired = delegator.findList("RateAmount", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
					EntityOperator.AND, 
								expireConds), null, UtilMisc.toList("fromDate"), null, false);
			Timestamp thruDateExpired = UtilDateTime.getDayEnd(fromDate, -1L);
			for(GenericValue tempGv: rateAmountExpired){
				tempGv.set("thruDate", thruDateExpired);
				tempGv.store();
			}
			
			dispatcher.runSync("createPartyRateAmount", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, 
																		"emplPositionTypeId", emplPositionTypeId,
																		"periodTypeId", periodTypeId,
																		"rateCurrencyUomId", uomId,
																		"rateAmount", amountValue,
																		"fromDate", fromDate,
																		"orgId", orgId,
																		"thruDate", thruDate));
		} catch (GenericEntityException e) { 
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> editEmployeeSalaryBase(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			List<GenericValue> payHistoryList = delegator.findList("PayHistory", EntityCondition.makeCondition("partyIdTo", partyId), 
					null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isEmpty(payHistoryList)){
				return ServiceUtil.returnError("Cannot find employee salary to upadte");
			}
			GenericValue payHistoryLastest = payHistoryList.get(0);
			String periodTypeIdLastest = payHistoryLastest.getString("periodTypeId");
			Timestamp thruDateLastest = null;
			switch (periodTypeIdLastest) {
			case "DAILY":
				fromDate = UtilDateTime.getDayStart(fromDate);
				thruDateLastest = UtilDateTime.getDayEnd(fromDate, -1L);
				break;
			case "MONTHLY":
				fromDate = UtilDateTime.getMonthStart(fromDate);
				thruDateLastest = UtilDateTime.getMonthEnd(UtilDateTime.getMonthStart(fromDate, 0, -1), timeZone, locale);
				break;
			case "YEARLY":
				fromDate = UtilDateTime.getYearStart(fromDate);
				thruDateLastest = UtilDateTime.getYearEnd(UtilDateTime.getYearStart(fromDate, 0, -1), timeZone, locale);
				break;
			default:
				return ServiceUtil.returnError("period is not valid");
			}
			payHistoryLastest.set("thruDate", thruDateLastest);
			payHistoryLastest.store();
			Map<String, Object> resultService = dispatcher.runSync("createPartySalaryBase", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());

		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	
	public static Map<String, Object> createPartySalaryBase(DispatchContext dctx, Map<String, Object> context){
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String periodTypeId = (String)context.get("periodTypeId");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Timestamp dateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			Timestamp dateEmplLeaveOrg = PersonHelper.getDateEmplLeaveOrg(delegator, partyId);
			fromDate = DateUtil.getPeriodStart(fromDate, periodTypeId, locale, timeZone);
			thruDate = DateUtil.getPeriodEnd(thruDate, periodTypeId, locale, timeZone);
			Timestamp dateJoinCompanyPeriod = DateUtil.getPeriodStart(dateJoinCompany, periodTypeId, locale, timeZone);
			dateEmplLeaveOrg = DateUtil.getPeriodEnd(dateEmplLeaveOrg, periodTypeId, locale, timeZone);
			if(dateJoinCompanyPeriod != null && fromDate.before(dateJoinCompanyPeriod)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotSetSalaryForEmplBeforeJoinCompany", 
						UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId), "fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,
								"dateJoinCompany", DateUtil.getDateMonthYearDesc(dateJoinCompanyPeriod)), locale));
			}
			
			if(dateEmplLeaveOrg != null && fromDate.after(dateEmplLeaveOrg)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotSetSalaryForEmplAfterEndContract", 
						UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId),
								"fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,"dateEndContract", DateUtil.getDateMonthYearDesc(dateEmplLeaveOrg)), locale));
			}
			if(thruDate != null && thruDate.before(fromDate)){
    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
    		}
			if(dateJoinCompany != null && fromDate.before(dateJoinCompany)){
				fromDate = dateJoinCompany;
			}
			List<EntityCondition> conditions  = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
			conditions.add(EntityCondition.makeCondition("partyIdFrom", orgId));
			conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			List<GenericValue> payHistoryList = delegator.findList("PayHistory", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(payHistoryList)){
				GenericValue payHistory = payHistoryList.get(0);
				Timestamp payHisFromDate = payHistory.getTimestamp("fromDate");
				Timestamp payHisThruDate = payHistory.getTimestamp("thruDate");
				BigDecimal amount = payHistory.getBigDecimal("amount");
				String errMsg = "";
				Map<String, Object> errMap = UtilMisc.toMap("amount", amount,
						"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
						"fromDate", DateUtil.getDateMonthYearDesc(payHisFromDate),
						"partyName", PartyUtil.getPersonName(delegator, partyId));
				if(thruDate == null && payHisThruDate == null){
					errMsg = "EmplSalaryInPeriodIsSetFromFrom";
				}else if(thruDate == null && payHisThruDate != null){
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(payHisThruDate));
					errMsg = "EmplSalaryInPeriodIsSetFromFromThru";
				}else if(thruDate != null && payHisThruDate == null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMsg = "EmplSalaryInPeriodIsSetFromThruFrom";
				}else if(thruDate != null && payHisThruDate != null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(payHisThruDate));
					errMsg = "EmplSalaryInPeriodIsSetFromThruFromThru";
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", errMsg, errMap, locale));
			}
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("partyIdFrom", orgId);
			ctxMap.put("partyIdTo", partyId);
			ctxMap.put("roleTypeIdFrom", PropertiesUtil.ORG_ROLE);
			ctxMap.put("roleTypeIdTo", PropertiesUtil.EMPL_ROLE);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			ctxMap.put("periodTypeId", periodTypeId);
			ctxMap.put("amount", context.get("amount"));
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("locale", locale);
			ctxMap.put("timeZone", timeZone);
			Map<String, Object> resultService = dispatcher.runSync("createPayHistory", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
	}
	
	public static Map<String, Object> updatePartySalaryBase(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	String roleTypeIdTo = (String)context.get("roleTypeIdTo");
    	String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
    	String partyIdTo = (String)context.get("partyIdTo");
    	String partyIdFrom = (String)context.get("partyIdFrom");
    	String periodTypeId = (String)context.get("periodTypeId");
    	List<EntityCondition> conds = FastList.newInstance();
    	conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
    	conds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
    	conds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
    	conds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.NOT_EQUAL, fromDate));
    	conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
    	try {
			List<GenericValue> payHisList = delegator.findList("PayHistory", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(payHisList)){
				GenericValue payHistory = payHisList.get(0);
				Timestamp payHisFromDate = payHistory.getTimestamp("fromDate");
				Timestamp payHisThruDate = payHistory.getTimestamp("thruDate");
				BigDecimal amount = payHistory.getBigDecimal("amount");
				String errMsg = "";
				Map<String, Object> errMap = UtilMisc.toMap("amount", amount,
						"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
						"fromDate", DateUtil.getDateMonthYearDesc(payHisFromDate),
						"partyName", PartyUtil.getPersonName(delegator, partyIdTo));
				if(thruDate == null && payHisThruDate == null){
					errMsg = "UpdateEmplSalaryInPeriodErrFromFrom";
				}else if(thruDate == null && payHisThruDate != null){
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(payHisThruDate));
					errMsg = "UpdateEmplSalaryInPeriodErrFromFromThru";
				}else if(thruDate != null && payHisThruDate == null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMsg = "UpdateEmplSalaryInPeriodErrFromThruFrom";
				}else if(thruDate != null && payHisThruDate != null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(payHisThruDate));
					errMsg = "UpdateEmplSalaryInPeriodErrFromThruFromThru";
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", errMsg, errMap, locale));
			}
			thruDate = DateUtil.getPeriodEnd(thruDate, periodTypeId, locale, timeZone);
			context.put("thruDate", thruDate);
			Map<String, Object> resultService = dispatcher.runSync("updatePayHistory", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deletePartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String fromDateStr = (String)context.get("fromDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		context.put("fromDate", fromDate);
		GenericValue rateAmount = delegator.makeValidValue("RateAmount", context);
		try {
			rateAmount.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplPositionTypeRateConvertDate(DispatchContext dctx, Map<String, Object> context){
		String fromDateStr = (String)context.get("fromDate");
	    String thruDateStr = (String)context.get("thruDate");
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
	    Timestamp thruDate = null;
	    if(thruDateStr != null){
	    	thruDate = new Timestamp(Long.parseLong(thruDateStr));
	    }
	    Map<String, Object> map = FastMap.newInstance();
	    map.putAll(context);
	    map.put("fromDate", fromDate);
	    map.put("thruDate", thruDate);
	    Map<String, Object> retMap = FastMap.newInstance();
	    try {
			retMap = dispatcher.runSync("createEmplPositionTypeRateAmount", map);
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
	    return retMap;
	}
	
	public static Map<String, Object> createEmplPositionTypeRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//String fromDateStr = (String)context.get("fromDate");
		//String thruDateStr = (String)context.get("thruDate");
		String uomId = (String)context.get("uomId");
		String periodTypeId = (String)context.get("periodTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amount = (String)context.get("rateAmount");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String includeGeoId = (String)context.get("includeGeoId");
		String excludeGeoId = (String)context.get("excludeGeoId");
		Locale locale = (Locale)context.get("locale");
		if(thruDate!= null){
			thruDate = UtilDateTime.getDayEnd(thruDate);
			if(fromDate.after(thruDate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
			}
		}
		List<String> includeGeoList = FastList.newInstance();
		List<String> excludeGeoList = null;
		if(includeGeoId != null){
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
		
		BigDecimal amountValue = new BigDecimal(amount);
		List<EntityCondition> conditions  = FastList.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if(uomId == null){
			uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		}
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
		List<EntityCondition> dateConds = FastList.newInstance();
		dateConds.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		List<GenericValue> emplPositionTypeRateList;
		try {
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			emplPositionTypeRateList = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
																					EntityOperator.AND, 
																					EntityCondition.makeCondition(dateConds)), 
																					null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPositionTypeRateList)){
				List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateList, "emplPositionTypeRateId", true);
				List<EntityCondition> emplPositionTypeRateCondList = FastList.newInstance();
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.IN, emplPositionTypeRateIdList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition emplPositionTypeRateCond = EntityCondition.makeCondition(emplPositionTypeRateCondList); 
				
				List<GenericValue> emplPositionTypeRateGeoApplList = delegator.findList("EmplPositionTypeRateGeoAppl", 
						emplPositionTypeRateCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(emplPositionTypeRateGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					for(String includeGeoIdTemp: includeGeoList){
						GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
						includeGeoName.add(includeGeo.getString("geoName"));
					}
					GenericValue emplPositionTypeRateGeoAppl = emplPositionTypeRateGeoApplList.get(0);
					GenericValue emplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateGeoAppl.getString("emplPositionTypeRateId")), false);
					String geoId = emplPositionTypeRateGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp emplPosTypeRateFromDate = emplPositionTypeRate.getTimestamp("fromDate");
					Timestamp emplPosTypeRateThruDate = emplPositionTypeRate.getTimestamp("thruDate");
					BigDecimal rateAmount = emplPositionTypeRate.getBigDecimal("rateAmount");
					String errMsg = "";
					if(thruDate == null && emplPosTypeRateThruDate == null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate == null && emplPosTypeRateThruDate != null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromThruGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromThruGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate != null && emplPosTypeRateThruDate == null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate != null && emplPosTypeRateThruDate != null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}
					}
					
					return ServiceUtil.returnError(errMsg);
				}
			}
			
			/*EntityCondition expireConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), 
					EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
			conditions.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
			conditions.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
			List<GenericValue> emplPosTypeRateExpired = delegator.findList("OldEmplPositionTypeRateAndGeoAppl", 
					EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
																			EntityOperator.AND, 
																			expireConds), null, UtilMisc.toList("fromDate"), null, false);
			List<String> emplPosTypeRateExpiredList = EntityUtil.getFieldListFromEntityList(emplPosTypeRateExpired, "emplPositionTypeRateId", true);
			Timestamp thruDateExpired = UtilDateTime.getDayEnd(fromDate, -1L);
			for(String tempEmplPositionTypeRateId: emplPosTypeRateExpiredList){
				GenericValue tempEmplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", tempEmplPositionTypeRateId), false);
				tempEmplPositionTypeRate.set("thruDate", thruDateExpired);
				tempEmplPositionTypeRate.store();
			}*/
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if(UtilValidate.isEmpty(includeGeoList)){
				List<GenericValue> orgAddrList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, fromDate, thruDate);
				if(UtilValidate.isNotEmpty(orgAddrList)){
					GenericValue orgAddr = orgAddrList.get(0);
					String stateProvinceGeoId = orgAddr.getString("stateProvinceGeoId");
					String regionId = CommonUtil.getRegionOfStateProvince(delegator, stateProvinceGeoId, "REGION", "REGIONS");
					if(regionId != null){
						includeGeoList.add(regionId);
					}else{
						regionId = CommonUtil.getRegionOfStateProvince(delegator, stateProvinceGeoId, "COUNTRY", "REGIONS");
						if(regionId != null){
							includeGeoList.add(regionId);
						}
					}
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotSetSalaryPositionType_OrgAddrNotSet", locale));
				}
			}
			if(UtilValidate.isNotEmpty(includeGeoList)){
				//create emplPositiontype rate by geo
				Map<String, Object> serviceCtx = FastMap.newInstance();
				serviceCtx.put("emplPositionTypeId", emplPositionTypeId);
				serviceCtx.put("roleTypeGroupId", roleTypeGroupId);
				serviceCtx.put("fromDate", fromDate);
				serviceCtx.put("thruDate", thruDate);
				serviceCtx.put("userLogin", userLogin);
				serviceCtx.put("periodTypeId", periodTypeId);
				serviceCtx.put("rateCurrencyUomId", uomId);
				serviceCtx.put("rateAmount", amountValue);
				Map<String, Object> resultService = dispatcher.runSync("createEmplPositionTypeRate", serviceCtx);
				if(ServiceUtil.isSuccess(resultService)){
					String emplPositionTypeRateId = (String)resultService.get("emplPositionTypeRateId");
					for(String tempIncludeGeoId: includeGeoList){
						dispatcher.runSync("createEmplPositionTypeRateGeoAppl", UtilMisc.toMap("geoId", tempIncludeGeoId, 
								"emplPositionTypeRateId",emplPositionTypeRateId,
								"userLogin", userLogin,
								"enumId", "PYRLL_INCLUDE"));
					}
					if(UtilValidate.isNotEmpty(excludeGeoList)){
						for(String tempExcludeGeoId: excludeGeoList){
							dispatcher.runSync("createEmplPositionTypeRateGeoAppl", UtilMisc.toMap("geoId", tempExcludeGeoId, 
									"emplPositionTypeRateId",emplPositionTypeRateId,
									"userLogin", userLogin,
									"enumId", "PYRLL_EXCLUDE"));
						}
					}
				}			
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindRegionApplySalaryForPositionType", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		return retMap; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateOldEmplPositionTypeRateAndGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String emplPositionTypeRateId = (String)context.get("emplPositionTypeRateId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<String> includeGeoList = (List<String>)context.get("includeGeoList");
		List<String> excludeGeoList = (List<String>)context.get("excludeGeoList");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(thruDate!= null){
			thruDate = UtilDateTime.getDayEnd(thruDate);
			if(fromDate.after(thruDate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
			}
		}
		List<EntityCondition> conditions  = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		if(roleTypeGroupId != null && roleTypeGroupId.trim().length() > 0){
			conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
		}
		conditions.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.NOT_EQUAL, emplPositionTypeRateId));
		conditions.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		try {
			GenericValue oldEmplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId), false);
			if(oldEmplPositionTypeRate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
			}
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			List<GenericValue> emplPositionTypeRateList = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(conditions), 
					null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPositionTypeRateList)){
				List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateList, "emplPositionTypeRateId", true);
				List<EntityCondition> emplPositionTypeRateCondList = FastList.newInstance();
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.IN, emplPositionTypeRateIdList));
				if(UtilValidate.isNotEmpty(includeGeoList)){
					emplPositionTypeRateCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				}
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition emplPositionTypeRateCond = EntityCondition.makeCondition(emplPositionTypeRateCondList); 
				
				List<GenericValue> emplPositionTypeRateGeoApplList = delegator.findList("EmplPositionTypeRateGeoAppl", 
						emplPositionTypeRateCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(emplPositionTypeRateGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					if(UtilValidate.isNotEmpty(includeGeoList)){
						for(String includeGeoIdTemp: includeGeoList){
							GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
							includeGeoName.add(includeGeo.getString("geoName"));
						}
					}
					GenericValue emplPositionTypeRateGeoAppl = emplPositionTypeRateGeoApplList.get(0);
					GenericValue emplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateGeoAppl.getString("emplPositionTypeRateId")), false);
					String geoId = emplPositionTypeRateGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp emplPosTypeRateFromDate = emplPositionTypeRate.getTimestamp("fromDate");
					Timestamp emplPosTypeRateThruDate = emplPositionTypeRate.getTimestamp("thruDate");
					BigDecimal rateAmount = emplPositionTypeRate.getBigDecimal("rateAmount");
					String errMsg = "";
					if(thruDate == null && emplPosTypeRateThruDate == null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate == null && emplPosTypeRateThruDate != null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromThruGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromFromThruGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate != null && emplPosTypeRateThruDate == null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFromGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"amount", rateAmount), locale);
						}
					}else if(thruDate != null && emplPosTypeRateThruDate != null){
						if(UtilValidate.isNotEmpty(includeGeoList)){
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFromGeo", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"geoErr", StringUtils.join(includeGeoName, ", "),
											"geo", geoExists.getString("geoName"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}else{
							errMsg = UtilProperties.getMessage("BaseHRPayrollUiLabels", "SalaryInPeriodIsSetFromThruFrom", 
									UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"),
											"fromDateSet", DateUtil.getDateMonthYearDesc(fromDate),
											"thruDateSet", DateUtil.getDateMonthYearDesc(thruDate),
											"fromDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateFromDate),
											"thruDate", DateUtil.getDateMonthYearDesc(emplPosTypeRateThruDate),
											"amount", rateAmount), locale);
						}
					}
					return ServiceUtil.returnError(errMsg);
				}
			}
			oldEmplPositionTypeRate.setNonPKFields(context);
			oldEmplPositionTypeRate.store();
			if(UtilValidate.isNotEmpty(includeGeoList)){
				dispatcher.runSync("updateEmplPositionTypeRateGeoAppl", 
						UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "geoIdList", includeGeoList, "enumId", "PYRLL_INCLUDE", "userLogin", userLogin));
			}
			dispatcher.runSync("updateEmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "geoIdList", excludeGeoList, "enumId", "PYRLL_EXCLUDE", "userLogin", userLogin));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateEmplPositionTypeRateGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		List<String> geoIdList = (List<String>)context.get("geoIdList");
		String enumId = (String)context.get("enumId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplPositionTypeRateId = (String)context.get("emplPositionTypeRateId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<GenericValue> geoApplList = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", enumId), null, false);
			for(GenericValue geoAppl: geoApplList){
				String geoId = geoAppl.getString("geoId");
				if(UtilValidate.isEmpty(geoIdList) || !geoIdList.contains(geoId)){
					geoAppl.remove();
				}
			}
			if(UtilValidate.isNotEmpty(geoIdList)){
				List<String> geoIdExists = EntityUtil.getFieldListFromEntityList(geoApplList, "geoId", true);
				for(String geoId: geoIdList){
					if(!geoIdExists.contains(geoId)){
						dispatcher.runSync("createEmplPositionTypeRateGeoAppl", 
								UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", enumId, "geoId", geoId, "userLogin", userLogin));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplPositionTypeRate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue emplPositionTypeRate = delegator.makeValue("OldEmplPositionTypeRate");
		emplPositionTypeRate.setNonPKFields(context);
		String emplPositionTypeRateId = delegator.getNextSeqId("OldEmplPositionTypeRate");
		emplPositionTypeRate.set("emplPositionTypeRateId", emplPositionTypeRateId);
		try {
			emplPositionTypeRate.create();
			retMap.put("emplPositionTypeRateId", emplPositionTypeRateId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createEmplPositionTypeRateGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue emplPositionTypeRateGepAppl = delegator.makeValue("EmplPositionTypeRateGeoAppl");
		emplPositionTypeRateGepAppl.setAllFields(context, false, null, null);
		try {
			emplPositionTypeRateGepAppl.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	//FIXME maybe delete
	public static Map<String, Object> createEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		//String periodTypeId = (String)context.get("periodTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String contactMechId = (String)context.get("contactMechId");
		Locale locale = (Locale)context.get("locale");
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try {
			GenericValue checkedEntity = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("fromDate", fromDate, /*"periodTypeId", periodTypeId,*/ "emplPositionTypeId", emplPositionTypeId, "contactMechId", contactMechId, "roleTypeGroupId", roleTypeGroupId), false);
			if(checkedEntity != null){
				GenericValue emplPosType = delegator.findOne("EmplPositionTypeId", UtilMisc.toMap("emplPositionTypeId", checkedEntity.getString("emplPositionTypeId")), false);
				Calendar cal = Calendar.getInstance();
				cal.setTime(checkedEntity.getTimestamp("fromDate"));
				String fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateStr = "___";
				if(checkedEntity.getTimestamp("thruDate") != null){
					cal.setTime(checkedEntity.getTimestamp("thruDate"));
					thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplPositionTypeRateIsSet", UtilMisc.toMap("emplPositionType", emplPosType.getString("description"), 
																																		"fromDate", fromDateStr, "thruDate", thruDateStr), locale));
			}
			GenericValue newEntity = delegator.makeValidValue("OldEmplPositionTypeRate", context);
			newEntity.set("fromDate", fromDate);
			newEntity.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	//maybe delete
	public static Map<String, Object> getEmplPostionTypeNotSetSalary(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listEmplPositionTypeNotSet", listReturn);
		try {
			
			List<GenericValue> listEmplPosTypeSet = delegator.findList("OldEmplPositionTypeRate", EntityUtil.getFilterByDateExpr(), null, UtilMisc.toList("emplPositionTypeId"), null, false);
			//GenericValue tempPosType;
			List<GenericValue> emplPositionTypeList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listEmplPosTypeSet)){
				List<String> emplPositionTypes = EntityUtil.getFieldListFromEntityList(listEmplPosTypeSet, "emplPositionTypeId", true);
				emplPositionTypeList = delegator.findList("EmplPositionType", EntityCondition.makeCondition(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_IN, emplPositionTypes),
																											EntityOperator.AND,
																											EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_EQUAL, "_NA_")),
																											null, UtilMisc.toList("emplPositionTypeId"), null, false);
			}else{
				emplPositionTypeList = delegator.findList("EmplPositionType", EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_EQUAL, "_NA_"), null, UtilMisc.toList("emplPositionTypeId"), null, false);
			}
			
			for(GenericValue tempGv: emplPositionTypeList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String emplPositionTypeId = tempGv.getString("emplPositionTypeId");
				tempMap.put("emplPositionTypeId", emplPositionTypeId);				
				tempMap.put("description", tempGv.getString("description"));				
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();	
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String periodTypeId = (String) context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Locale locale = (Locale)context.get("locale");
		try {
			List<EntityCondition> commonConds = FastList.newInstance();
			commonConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			commonConds.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
			commonConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, fromDate));
			EntityCondition dateConds;
			if(thruDate != null){
				if(thruDate.before(fromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "DateEnterNotValid", locale));
				}
				dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate), 
							EntityOperator.AND, 
							EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
			}else{
				dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
															EntityOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate));
			}
			EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition(commonConds), EntityOperator.AND, dateConds); 
			List<GenericValue> checkEntityNotValid = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(conds, EntityOperator.AND, EntityCondition.makeCondition(dateConds)), null, null, null, false);
			if(UtilValidate.isNotEmpty(checkEntityNotValid)){
				GenericValue entityNotValid = checkEntityNotValid.get(0);
				Timestamp fromDateErr = entityNotValid.getTimestamp("fromDate");
				Timestamp thruDateErr = entityNotValid.getTimestamp("thruDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateErr);
				String fromDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(fromDate);
				String fromDateUpdateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateUpdateStr = "____";
				if(thruDate != null){
					cal.setTime(thruDate);
					thruDateUpdateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				String thruDateErrStr = "____";
				if(thruDateErr != null){
					cal.setTime(thruDateErr);
					thruDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdatePayrolParamDateInvalid", 
																	UtilMisc.toMap("fromDateUpdate", fromDateUpdateStr, "thruDateUpdate", thruDateUpdateStr,
																					"fromDateError", fromDateErrStr, "thruDateErr", thruDateErrStr), locale));
			}
			GenericValue emplPosTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, /*"periodTypeId", periodTypeId,*/ "fromDate", fromDate), false);
			if(UtilValidate.isEmpty(emplPosTypeRate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
			}
			emplPosTypeRate.setNonPKFields(context);
			if(thruDate != null){
				thruDate = UtilDateTime.getDayEnd(thruDate);
				emplPosTypeRate.set("thruDate", thruDate);
			}
			emplPosTypeRate.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
		
	public static Map<String, Object> deleteEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		//String periodTypeId = (String) context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue emplPosTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, /*"periodTypeId", periodTypeId,*/ "fromDate", fromDate), false);
			if(UtilValidate.isEmpty(emplPosTypeRate)){
				ServiceUtil.returnError(UtilProperties.getMessage("hrCommonUiLabels", "NotFoundRecordToDelete", locale));
			}
			/*emplPosTypeRate.set("thruDate", UtilDateTime.nowTimestamp());*/
			emplPosTypeRate.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSalaryAmountEmpl(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String orgId = (String)context.get("orgId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			if(orgId == null){
				orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("partyId", partyId);
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("orgId", orgId);
			serviceCtx.put("fromDate", fromDate);
			serviceCtx.put("thruDate", thruDate);
			Map<String, Object> resultServices = dispatcher.runSync("getPartyPayrollHistory", serviceCtx);
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
			List<GenericValue> listReturn = (List<GenericValue>)resultServices.get("listPartyPayrollHistory");
			if(UtilValidate.isNotEmpty(listReturn)){
				retMap.put("rateAmount", listReturn.get(0).get("amount"));
				retMap.put("periodTypeId", listReturn.get(0).get("periodTypeId"));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplSalaryBaseFlatJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Map<String, Object> retMap = FastMap.newInstance();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
    		
	    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
	    	Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
	    	fromDate = UtilDateTime.getDayStart(fromDate);
	    	thruDate = UtilDateTime.getDayEnd(thruDate);
			String currOrgId = PartyUtil.getRootOrganization(delegator, userLogin.getString("userLoginId"));
			List<EntityCondition> whereConds = FastList.newInstance();
			whereConds.add(EntityCondition.makeCondition("partyIdFrom", currOrgId));
			whereConds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			EntityCondition cond = EntityCondition.makeCondition(EntityCondition.makeCondition(whereConds), EntityCondition.makeCondition(listAllConditions));
			listIterator = delegator.find("PayHistoryAndDetail", cond, null, null, listSortFields, opts);
			retMap.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplBaseSalaryMaxDateJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		EntityListIterator listIterator = null;
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> retMap = FastMap.newInstance();
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String fromDateStr = parameters.get("fromDate") != null? (String)parameters.get("fromDate")[0] : null;
		String periodTypeId = parameters.get("fromDate") != null? (String)parameters.get("periodTypeId")[0] : null;
		if(fromDateStr != null && periodTypeId != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			Timestamp thruDatePreviousPeriod = null;
			List<String> periodTypeIdList = FastList.newInstance();
			periodTypeIdList.add(periodTypeId);
			
			switch (periodTypeId) {
				case "DAILY":
					fromDate = UtilDateTime.getDayStart(fromDate); 
					thruDatePreviousPeriod = UtilDateTime.getDayEnd(fromDate, -1L);
					if(fromDate.compareTo(UtilDateTime.getMonthStart(fromDate)) == 0){
						periodTypeIdList.add("MONTHLY");
					}
					if(fromDate.compareTo(UtilDateTime.getYearStart(fromDate)) == 0){
						periodTypeIdList.add("YEARLY");
					}
					break;
					
				case "MONTHLY":
					fromDate = UtilDateTime.getMonthStart(fromDate);
					Timestamp monthPreviousStart = UtilDateTime.getMonthStart(fromDate, 0, -1);
					thruDatePreviousPeriod = UtilDateTime.getMonthEnd(monthPreviousStart, timeZone, locale);
					periodTypeIdList.add("DAILY");
					if(fromDate.compareTo(UtilDateTime.getYearStart(fromDate)) == 0){
						periodTypeIdList.add("YEARLY");
					}
					break;
					
				case "YEARLY":
					fromDate = UtilDateTime.getYearStart(fromDate);
					Timestamp yearPreviousStart = UtilDateTime.getYearStart(fromDate, 0, -1);
					thruDatePreviousPeriod = UtilDateTime.getYearEnd(yearPreviousStart, timeZone, locale);
					periodTypeIdList.add("DAILY");
					periodTypeIdList.add("MONTHLY");
					break;
				default:
					return retMap;
			}
			listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityJoinOperator.IN, periodTypeIdList));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("thruDate", thruDatePreviousPeriod)));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			try {
				listIterator = delegator.find("PayHistoryMaxDateAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				retMap.put("listIterator", listIterator);
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionTypeRate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	List<GenericValue> emplPositionTypeRateList = FastList.newInstance();
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Timestamp fromDate, thruDate;
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	if(fromDateStr != null){
    		fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	}else{
    		fromDate = UtilDateTime.getMonthStart(nowTimestamp);
    	}
    	
    	if(thruDateStr != null){
    		thruDate = new Timestamp(Long.parseLong(thruDateStr));
    	}else{
    		thruDate = UtilDateTime.getMonthEnd(nowTimestamp, timeZone, locale);
    	}
    	listAllConditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
    	//listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = UtilMisc.toList("emplPositionTypeId");
    		listSortFields.add("-fromDate");
    	}
    	try {
    		listIterator = delegator.find("OldEmplPositionTypeRate", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
//    		emplPositionTypeRateList = listIterator.getPartialList(start, size);
//    		totalRows = listIterator.getResultsSizeAfterPartialList();
//    		listIterator.close();
    		emplPositionTypeRateList = listIterator.getCompleteList();
    		List<String> listFieldInEntity = FastList.newInstance();
    		listFieldInEntity.add("emplPositionTypeRateId");
    		listFieldInEntity.add("emplPositionTypeId");
    		listFieldInEntity.add("periodTypeId");
    		listFieldInEntity.add("payGradeId");
    		listFieldInEntity.add("roleTypeGroupId");
    		listFieldInEntity.add("salaryStepSeqId");
    		listFieldInEntity.add("rateTypeId");
    		listFieldInEntity.add("fromDate");
    		listFieldInEntity.add("thruDate");
    		listFieldInEntity.add("rate");
    		listFieldInEntity.add("rateAmount");
    		listFieldInEntity.add("rateCurrencyUomId");
    		
    		List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
    		List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
    		EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
    		
    		List<String> sortedFieldInEntity = FastList.newInstance();
    		List<String> sortedFieldNotInEntity = FastList.newInstance();
    		if(listSortFields != null){
    			EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
    		}
    		if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
    			emplPositionTypeRateList = EntityConditionUtils.doFilterGenericValue(emplPositionTypeRateList, condsForFieldInEntity);
    		}
    		if(UtilValidate.isEmpty(sortedFieldInEntity)){
    			sortedFieldInEntity.add("emplPositionTypeId");
    		}
    		emplPositionTypeRateList = EntityUtil.orderBy(emplPositionTypeRateList, sortedFieldInEntity);
    		
    		boolean isFilterAdvance = false;
    		if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
    			totalRows = emplPositionTypeRateList.size();
    			if(end > emplPositionTypeRateList.size()){
    				end = emplPositionTypeRateList.size();
    			}
    			emplPositionTypeRateList = emplPositionTypeRateList.subList(start, end);
    		}else{
    			isFilterAdvance = true;
    		}
    		if(end > emplPositionTypeRateList.size()){
    			end = emplPositionTypeRateList.size();
    		}
    		
    		listIterator.close();
    		for(GenericValue tempGv: emplPositionTypeRateList){
    			Map<String, Object> tempMap = FastMap.newInstance();
    			String emplPositionTypeRateId = tempGv.getString("emplPositionTypeRateId");
    			List<GenericValue> emplPositionTypeRateGeoInclude = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
    					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_INCLUDE"), null, false);
    			List<GenericValue> emplPositionTypeRateGeoExclude = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
    					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_EXCLUDE"), null, false);
    			List<String> includeGeoList = FastList.newInstance();
    			List<String> excludeGeoList = FastList.newInstance();
    			for(GenericValue includeGeo: emplPositionTypeRateGeoInclude){
    				String geoId = includeGeo.getString("geoId");
    				GenericValue geoIncludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				includeGeoList.add(geoIncludeGv.getString("geoName"));
    			}
    			
    			for(GenericValue excludeGeo: emplPositionTypeRateGeoExclude){
    				String geoId = excludeGeo.getString("geoId");
    				GenericValue geoExcludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				excludeGeoList.add(geoExcludeGv.getString("geoName"));
    			}
    			if(UtilValidate.isNotEmpty(excludeGeoList)){
    				tempMap.put("excludeGeo", StringUtils.join(excludeGeoList, ", "));
    			}
    			tempMap.put("includeGeo", StringUtils.join(includeGeoList, ", "));	
    			tempMap.put("emplPositionTypeRateId", emplPositionTypeRateId);
    			tempMap.put("emplPositionTypeId", tempGv.getString("emplPositionTypeId"));
    			tempMap.put("periodTypeId", tempGv.getString("periodTypeId"));
    			tempMap.put("payGradeId", tempGv.getString("payGradeId"));
    			tempMap.put("roleTypeGroupId", tempGv.getString("roleTypeGroupId"));
    			tempMap.put("salaryStepSeqId", tempGv.getString("salaryStepSeqId"));
    			tempMap.put("rateTypeId", tempGv.getString("rateTypeId"));
    			tempMap.put("fromDate", tempGv.getTimestamp("fromDate"));
    			tempMap.put("thruDate", tempGv.getTimestamp("thruDate"));
    			tempMap.put("rate", tempGv.get("rate"));
    			tempMap.put("rateAmount", tempGv.get("rateAmount"));
    			tempMap.put("rateCurrencyUomId", tempGv.get("rateCurrencyUomId"));
    			listReturn.add(tempMap);
    		}
    		if(isFilterAdvance){
    			if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
    				listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
    			}
    			if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
    				listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
    			}
    			totalRows = listReturn.size();
    			if(end > listReturn.size()){
    				end = listReturn.size();
    			}
    			listReturn = listReturn.subList(start, end);
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListEmplPositionTypeRate service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listReturn);
    	successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;		
	}
	
	//maybe delete
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyPayrollHistoryDetails(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> resultService = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		try {
			resultService = dispatcher.runSync("getPartyPayrollHistory", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
			List<Map<String, Object>> listRowDetails = (List<Map<String,Object>>)resultService.get("listPartyPayrollHistory");		
			List<Map<String, Object>> rowDetails = FastList.newInstance();
			retMap.put("rowDetail", rowDetails);			
			Collections.sort(listRowDetails, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					Timestamp obj1Time = (Timestamp)o1.get("fromDate");
					Timestamp obj2Time = (Timestamp)o2.get("fromDate");
					if(obj1Time.before(obj2Time)){
						return -1;
					}else if(obj1Time.after(obj2Time)){
						return 1;
					}
					return 0;
				}
			});
			for(Map<String, Object> entry: listRowDetails){
				Map<String, Object> childRowDetail = FastMap.newInstance();
				childRowDetail.put("fromDateDetail", ((Timestamp)entry.get("fromDate")).getTime());
				childRowDetail.put("thruDateDetail", entry.get("thruDate") != null? ((Timestamp)entry.get("thruDate")).getTime(): null);
				childRowDetail.put("workEffortIdDetail", entry.get("workEffortId")); 
				childRowDetail.put("rateTypeIdDetail", entry.get("rateTypeId")); 
				childRowDetail.put("rateCurrencyUomIdDetail", entry.get("rateCurrencyUomId"));
				childRowDetail.put("periodTypeIdDetail", entry.get("periodTypeId"));
				childRowDetail.put("rateAmountDetail", entry.get("rateAmount"));
				childRowDetail.put("emplPositionTypeIdDetail", entry.get("emplPositionTypeId"));
				childRowDetail.put("rateTypeIdDetail", entry.get("rateTypeId"));
				childRowDetail.put("workEffortIdDetail", entry.get("workEffortId"));
				childRowDetail.put("orgId", entry.get("orgId"));
				childRowDetail.put("groupName", PartyHelper.getPartyName(delegator, (String)entry.get("orgId"), false));
				rowDetails.add(childRowDetail);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	public static Map<String, Object> getPartyPayrollHistory(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String orgId = (String)context.get("orgId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdTo", partyId));
			conds.add(EntityCondition.makeCondition("partyIdFrom", orgId));
			conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			EntityCondition commonConds = EntityCondition.makeCondition(conds);
			List<GenericValue> payHistoryList = delegator.findList("PayHistory", commonConds, null, UtilMisc.toList("-fromDate"), null, false);
			retMap.put("listPartyPayrollHistory", payHistoryList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplPositionTypeRateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		List<GenericValue> listReturn = FastList.newInstance();
		
		try {
			List<GenericValue> emplPartyRel = PartyUtil.getOrgOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
			/*EntityCondition commonConds = EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS");
			commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND,  EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));*/
			for(GenericValue tempPartyRel: emplPartyRel){
				Timestamp tempFromDate = tempPartyRel.getTimestamp("fromDate");
				Timestamp tempThruDate = tempPartyRel.getTimestamp("thruDate");
				if(tempFromDate == null || fromDate.after(tempFromDate)){
					tempFromDate = fromDate;
				}
				if(tempThruDate == null || thruDate.before(tempThruDate)){
					tempThruDate = thruDate;
				}
				String orgId = tempPartyRel.getString("partyIdFrom");
				List<EntityCondition> addrConditions = FastList.newInstance();
				addrConditions.add(EntityCondition.makeCondition("partyId", orgId));
				addrConditions.add(EntityConditionUtils.makeDateConds(tempFromDate, tempThruDate));
				List<GenericValue> orgAddrList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, tempFromDate, tempThruDate);
				//String roleTypeGroupId = PartyUtil.getRoleTypeGroupInPeriod(delegator, orgId, tempFromDate, tempThruDate);
				Map<String, Object> roleTypeGroupMap = PartyUtil.getListRoleTypeGroupInPeriod(delegator, orgId, tempFromDate, tempThruDate);
				for(Map.Entry<String, Object> entry: roleTypeGroupMap.entrySet()){
					String roleTypeGroupId = entry.getKey(); 
					EntityCondition roleTypeGroupConds = EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId);
					List<Map<String, Timestamp>> roleTypeGroupList = (List<Map<String, Timestamp>>)entry.getValue();
					for(Map<String, Timestamp> tempMap: roleTypeGroupList){
						Timestamp fromDateRoleTypeGroup = tempMap.get("fromDate");
						Timestamp thruDateRoleTypeGroup = tempMap.get("thruDate");
						if(fromDateRoleTypeGroup == null || tempFromDate.after(fromDateRoleTypeGroup)){
							fromDateRoleTypeGroup = tempFromDate;
						}
						if(thruDateRoleTypeGroup == null || tempThruDate.before(thruDateRoleTypeGroup)){
							thruDateRoleTypeGroup = tempThruDate;
						}
						for(GenericValue tempOrgAddr: orgAddrList){
							Timestamp tempAddrFromDate = tempOrgAddr.getTimestamp("fromDate");
							Timestamp tempAddrThruDate = tempOrgAddr.getTimestamp("thruDate");
							if(tempAddrFromDate == null || fromDateRoleTypeGroup.after(tempAddrFromDate)){
								tempAddrFromDate = fromDateRoleTypeGroup;
							}
							if(tempAddrThruDate == null || thruDateRoleTypeGroup.before(tempAddrThruDate)){
								tempAddrThruDate = thruDateRoleTypeGroup;
							}
							List<EntityCondition> tempConds = FastList.newInstance();
							tempConds.add(EntityConditionUtils.makeDateConds(tempAddrFromDate, tempAddrThruDate));
							tempConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
							List<GenericValue> emplPositionTypeRate = delegator.findList("OldEmplPositionTypeRate", 
									EntityCondition.makeCondition(roleTypeGroupConds, 
											EntityOperator.AND, 
											EntityCondition.makeCondition(tempConds)), null, UtilMisc.toList("fromDate"), null, false);
							List<GenericValue> tempList = PayrollUtil.getEmplPositionTypeRate(delegator, emplPositionTypeRate, tempOrgAddr);
							for(GenericValue tempGv: tempList){
								Timestamp tempGvFromDate = tempGv.getTimestamp("fromDate");
								Timestamp tempGvThruDate = tempGv.getTimestamp("thruDate");
								if(tempGvFromDate.before(tempAddrFromDate)){
									tempGv.set("fromDate", tempAddrFromDate);
								}
								if(tempGvThruDate == null || (tempAddrThruDate != null && tempGvThruDate.after(tempAddrThruDate))){
									tempGv.set("thruDate", tempAddrThruDate);
								}
								listReturn.add(tempGv);
							}
							
						}
					}
				}
			}
			retMap.put("emplPositionTypeRate", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	//maybe delete
	public static Map<String, Object> settingEmplBaseSalaryByPosType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyGroup = (String)context.get("partyGroupId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String overrideDataWay = (String)context.get("overrideDataWay");
		String rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
		if(rateCurrencyUomId == null){
			rateCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroup, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			for(GenericValue empl: emplList){
				String partyId = empl.getString("partyId");
				List<String> partyGroupList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				partyGroupList = PartyUtil.filterListPartyByAncestor(delegator, orgId, partyGroupList);
				if(UtilValidate.isNotEmpty(partyGroupList)){
					List<String> roleTypeGroupList = PartyUtil.getRoleTypeGroupListInPeriod(delegator, partyGroupList, fromDate, thruDate);
					PayrollWorker.settingEmplBaseSalary(delegator, dispatcher, partyGroupList, orgId, partyId, roleTypeGroupList, fromDate, thruDate, overrideDataWay, rateCurrencyUomId, userLogin);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPayHistoryOfEmplJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale)context.get("locale");
		EntityListIterator listIterator = null;
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-fromDate");
			}
			String partyCode = parameters.get("partyCode") != null ? parameters.get("partyCode")[0] : null;
			List<GenericValue> party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isEmpty(party)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "NoEmployeeIdIsHavePartyId", UtilMisc.toMap("partyId", partyCode), locale));
			}
			String partyId = party.get(0).getString("partyId");
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
			EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			listIterator = delegator.find("PayHistoryAndDetail", tmpCond, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	
	}
	/*public static Map<String, Object> updateEmplPositionTypeContactMechRate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String fromDateStr = (String)context.get("fromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String contactMechId = (String)context.get("contactMechId");
		String uomId = (String)context.get("uomId");
		String thruDateStr = (String)context.get("thruDate");
		if(uomId == null){
			uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		try {
			GenericValue updateEtt = delegator.findOne("OldEmplPositionTypeRate", 
					UtilMisc.toMap("fromDate", fromDate, "emplPositionTypeId", emplPositionTypeId, "roleTypeGroupId", roleTypeGroupId, "contactMechId", contactMechId), false);
			if(updateEtt == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
			}
			Map<String, Object> updateMap = FastMap.newInstance();
			updateMap.putAll(context);
			updateMap.put("rateCurrencyUomId", uomId);
			updateMap.put("rateAmount", new BigDecimal((String)context.get("rateAmount")));
			if(thruDateStr != null){
				Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
				updateMap.put("thruDate", thruDate);
			}
			updateEtt.setNonPKFields(updateMap);
			updateEtt.store();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}*/
}
