package com.olbius.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
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
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;
import org.ofbiz.service.calendar.RecurrenceRule;

import com.ibm.icu.text.DateFormat;
import com.olbius.accounting.invoice.InvoiceWorker;
import com.olbius.payroll.PayrollDataPreparation;
import com.olbius.payroll.PayrollEngine;
import com.olbius.payroll.PayrollUtil;
import com.olbius.payroll.PayrollWorker;
import com.olbius.payroll.PeriodEnum;
import com.olbius.payroll.PeriodWorker;
import com.olbius.payroll.entity.EntityEmployeeSalary;
import com.olbius.payroll.entity.EntitySalaryAmount;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;
import com.olbius.workflow.WorkFlowUtils;

@SuppressWarnings("unchecked")
public class PayrollServices {
	public static final String module = PayrollServices.class.getName();
    public static final String resource = "hrolbiusUiLabels";
    public static final String resourceNoti = "NotificationUiLabels";
    
    /*
     * Description: Create or update Tax Authority Rate
     * */
    // TODO change method name
    public static Map<String, Object> createTaxAuthorityRate(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	String strTaxAuthorityRateSeqId = (String)context.get("taxAuthorityRateSeqId");
    	String strTaxAuthGeoId = (String)context.get("taxAuthGeoId");
    	String strTaxAuthPartyId = (String)context.get("taxAuthPartyId");
    	String strName = (String)context.get("name");
    	String strFromValue = (String)context.get("fromValue");
    	String strThruValue = (String)context.get("thruValue");
    	String strTaxPercentage = (String)context.get("taxPercentage");
    	Timestamp ttFromDate = (Timestamp)context.get("fromDate");
    	Timestamp ttThruDate = (Timestamp)context.get("thruDate");
    	try {
       	// check update or create
    	GenericValue tempPayrollFormula = delegator.findOne("TaxAuthorityRatePayroll", UtilMisc.toMap("taxAuthorityRateSeqId", strTaxAuthorityRateSeqId),false);
    	if(tempPayrollFormula != null && !tempPayrollFormula.isEmpty()){
    		// update 
    		tempPayrollFormula.put("name", strName);
    		tempPayrollFormula.put("fromValue", strFromValue);
    		tempPayrollFormula.put("thruValue", strThruValue);
    		tempPayrollFormula.put("taxPercentage", strTaxPercentage);
    		tempPayrollFormula.put("fromDate", ttFromDate);
    		tempPayrollFormula.put("thruDate", ttThruDate);
    		tempPayrollFormula.store();
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
    	}else{
			// create
    		GenericValue tempTaxAuthorityRate = delegator.makeValue("TaxAuthorityRatePayroll", UtilMisc.toMap("taxAuthorityRateSeqId",delegator.getNextSeqId("PayrollTaxAuthority"),
    																							"taxAuthGeoId", strTaxAuthGeoId, 
																								"taxAuthPartyId", strTaxAuthPartyId,
																								"name", strName, 
																								"fromValue", strFromValue, 
																								"thruValue", strThruValue,
																								"taxPercentage", strTaxPercentage,
																								"fromDate", ttFromDate,
																								"thruDate", ttThruDate));
    	
    		tempTaxAuthorityRate.create();
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
    	}
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "createError", new Object[] { e.getMessage() }, locale));
		}
    	return result;
    }
    
    public static Map<String, Object> calcPayrollTable(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	List<Map<String,Object>> listResult = FastList.newInstance();
    	try {
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(!("PYRLL_TABLE_CREATED".equals(statusId) || "PYRLL_TABLE_CALC".equals(statusId))){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "PayrollTabelHaveCalculated", locale));
			}
			List<GenericValue> formulaList = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			String departmentId = payrollTableRecord.getString("partyId");
			String customTimePeriodId = payrollTableRecord.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = new Timestamp(thruDate.getTime());
			thruDateTs = UtilDateTime.getDayEnd(thruDateTs);
			List<String> formulaListStr = EntityUtil.getFieldListFromEntityList(formulaList, "code", true);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("formulaList", formulaListStr);
			ctxMap.put("departmentId", departmentId);
			ctxMap.put("fromDate", fromDateTs);
			ctxMap.put("thruDate", thruDateTs);
			ctxMap.put("periodTypeId", customTimePeriod.getString("periodTypeId"));
			ctxMap.put("userLogin", context.get("userLogin"));
			ctxMap.put("timeZone", context.get("timeZone"));
			ctxMap.put("payrollTableId", payrollTableId);
			Map<String, Object> results = dispatcher.runSync("calcSalaryInPeriod", ctxMap);
			if(ServiceUtil.isSuccess(results)){
				listResult = (List<Map<String,Object>>)results.get("listEmplSalaryInPeriod");
				if("PYRLL_TABLE_CREATED".equals(statusId)){
					payrollTableRecord.set("statusId", "PYRLL_TABLE_CALC");
					payrollTableRecord.store();	
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	Map<String, Object> retMap = FastMap.newInstance();
    	retMap.put("payrollTableId", payrollTableId);
    	retMap.put("listEmplSalaryInPeriod", listResult);
    	retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("PayrollUiLabels", "CaclculateSuccessful", locale));
    	return retMap;
    }
    
    public static Map<String, Object> getPayrollTableRecordTimestamp(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	String payrollTableId = (String)context.get("payrollTableId");    	
    	Locale locale = (Locale)context.get("locale");
    	Map<String, Object> retMap = FastMap.newInstance();
    	GenericValue payrollTableRecord;
    	List<Map<String, Timestamp>> listReturn = FastList.newInstance();
    	retMap.put("listTimestamp", listReturn);
		try {
			payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(statusId == null || statusId.equals("PYRLL_TABLE_CREATED")){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "PayrollTableNotCalc", locale));
			}
			EntityFindOptions findOption = new EntityFindOptions();
			findOption.setDistinct(true);
			List<GenericValue> payrollTable = delegator.findList("PayrollTable", EntityCondition.makeCondition("payrollTableId", payrollTableId), UtilMisc.toSet("fromDate"), null, findOption, false);
			List<Timestamp> fromDateList = EntityUtil.getFieldListFromEntityList(payrollTable, "fromDate", true);
			//Timestamp fromDate = fromDateList.get(0);
			//retMap.put("fromDate", fromDate);
			//Map<String, Object> resultSerive = dispatcher.runSync("getPayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId, "fromDate", fromDate));
			/*retMap.put("listEntitySalaryAmount", resultSerive.get("listEntitySalaryAmount"));
			retMap.put("formulaList", resultSerive.get("formulaList"));
			retMap.put("thruDate", resultSerive.get("thruDate"));*/
			for(Timestamp tempFromDate : fromDateList){
				Map<String, Timestamp> tempMap = FastMap.newInstance();
				List<GenericValue> tempPayrollTable = delegator.findByAnd("PayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId, "fromDate", tempFromDate), UtilMisc.toList("partyId", "code"), false);
				Timestamp temThruDate = tempPayrollTable.get(0).getTimestamp("thruDate");
				tempMap.put("fromDate", tempFromDate);
				tempMap.put("thruDate", temThruDate);
				listReturn.add(tempMap);
			}
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return retMap;
    }
    
    public static Map<String, Object> getPayrollTableRecord(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	Map<String, Object> retMap = FastMap.newInstance();
    	//String payrollTableId = parameters.get("payrollTableId")[0];
    	String payrollTableId = (String)context.get("payrollTableId");
    	
    	//String formulaList = parameters.get("formulaList")[0];
    	Long fromDateLong = Long.parseLong((String)context.get("fromDate"));
    	Timestamp fromDate = new Timestamp(fromDateLong);
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	Locale locale = (Locale)context.get("locale");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	
    	String pagesize = (String)context.get("pagesize");
    	if(pagesize == null){
    		pagesize = "20";
    	}
    	String pagenum = (String)context.get("pagenum");
    	if(pagenum == null){
    		pagenum = "0";
    	}
    	retMap.put("listIterator", listReturn);
    	try {
    		int totalRows = 0;
			//List<GenericValue> payrollTable = delegator.findByAnd("PayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId, "fromDate", fromDate), UtilMisc.toList("partyId", "code"), false);
			List<GenericValue> payrollRecordCode = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), UtilMisc.toList("code"), false);
			List<String> payrollRecordCodeList = EntityUtil.getFieldListFromEntityList(payrollRecordCode, "code", true);
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			//List<GenericValue> partyPayrollTableRecord = delegator.findByAnd("PartyPayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			
			EntityCondition commonConds = EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList);
			EntityCondition incomeConds = EntityCondition.makeCondition("payrollCharacteristicId", "INCOME");
			EntityCondition deductionCondition = EntityCondition.makeCondition("payrollCharacteristicId", "DEDUCTION");
			EntityCondition taxDeductionCondition = EntityCondition.makeCondition("payrollCharacteristicId", "TAX_DEDUCTION");
			EntityCondition orgPaidCondition = EntityCondition.makeCondition("payrollCharacteristicId", "ORG_PAID");
			List<GenericValue> listFormulaIncome = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, incomeConds), null, null, null, false);
			List<GenericValue> listFormulaDeduction = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, deductionCondition), null, null, null, false);
			List<GenericValue> listFormulaTaxDeduction= delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, taxDeductionCondition), null, null, null, false);
			List<GenericValue> listFormulaOrgPaid = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, orgPaidCondition), null, null, null, false);
			List<GenericValue> listFormulaOthers = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("payrollCharacteristicId", null)), null, null, null, false);
			
			retMap.put("TotalRows", String.valueOf(totalRows));
			
			String orgId = payrollTableRecord.getString("partyId");
			Map<String, Object> tempMap = PayrollUtil.getSalaryCalculateParty(dctx, delegator, orgId, payrollTableId, fromDate, thruDate, locale, timeZone,
					listFormulaIncome, listFormulaTaxDeduction, listFormulaDeduction, listFormulaOrgPaid, listFormulaOthers);
			listReturn.addAll((List<Map<String, Object>>)tempMap.get("listReturn"));
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	return retMap;
    }
    
    public static Map<String, Object> getPayrollTableRecordOfParty(DispatchContext dctx, Map<String, Object> context){
    	Map<String, Object> retMap = FastMap.newInstance();
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	String payrollTableId = (String)context.get("payrollTableId");
    	String partyId = (String)context.get("partyId");
    	Delegator delegator = dctx.getDelegator();
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	Locale locale = (Locale)context.get("locale");
    	try {
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(payrollTableRecord == null){
				return ServiceUtil.returnError("Cannot find payroll table have id: " + payrollTableId);
			}
			List<GenericValue> payrollRecordCode = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), UtilMisc.toList("code"), false);
			List<String> payrollRecordCodeList = EntityUtil.getFieldListFromEntityList(payrollRecordCode, "code", true);
			EntityCondition commonConds = EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList);
			List<GenericValue> listAllFormula = delegator.findList("PayrollFormula", commonConds, null, null, null, false);			
			
			String customTimePeriodId = payrollTableRecord.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			List<String> listPartyId = FastList.newInstance();
			String partyParentId = null;
			if(partyId == null){
				partyId = payrollTableRecord.getString("partyId");
				listPartyId.add(partyId);
			}else{
				partyParentId = partyId;
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if(!"PERSON".equals(party.getString("partyTypeId"))){
					Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, false, false);
					List<GenericValue> departmentList = buildOrg.getDirectChildList(delegator);
					//FIXME need add condition about fromDate, thruDate
					List<GenericValue> employeeList = buildOrg.getDirectEmployee(delegator);
					if(departmentList != null){
						List<String> tempListPartyGroupId = EntityUtil.getFieldListFromEntityList(departmentList, "partyId", true);
						listPartyId.addAll(tempListPartyGroupId);
					}
					if(employeeList != null){
						List<String> tempListEmplId = EntityUtil.getFieldListFromEntityList(employeeList, "partyId", true);
						listPartyId.addAll(tempListEmplId);
					}
				}
			}
			for(String tempPartyId: listPartyId){
				Map<String, Object> tempMap = PayrollWorker.getPayrollTableRecordOfPartyInfo(dctx, payrollTableId, tempPartyId, partyParentId, 
						locale, timeZone, fromDateTs, thruDateTs, listAllFormula);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	retMap.put("listReturn", listReturn);
    	return retMap;
    }
    
    public static Map<String, Object> deletePayrollTable(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	GenericValue payrollTableRecord;
		try {
			payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(statusId.equals("PYRLL_TABLE_INVOICED")){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeletedPayrollTableHaveInvoiced", locale));
			}
			List<GenericValue> formulaList = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			//List<GenericValue> departmentList = delegator.findByAnd("PartyPayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			List<GenericValue> payrollTable = delegator.findByAnd("PayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			
			for(GenericValue temp: formulaList){
				temp.remove();
			}
			/*for(GenericValue temp: departmentList){
				temp.remove();
			}*/
			for(GenericValue temp: payrollTable){
				temp.remove();
			}
			payrollTableRecord.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
    }
    
    public static Map<String, Object> updatePayrollTableRecord(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	GenericValue payrollTableRecord;
		try {
			payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(!statusId.equals("PYRLL_TABLE_CREATED")){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotUpdatePayrollTableHaveCalculated", locale));
			}
			payrollTableRecord.setNonPKFields(context);
			payrollTableRecord.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
    }
    
    public static Map<String, Object> calcSalaryInPeriod(DispatchContext dctx, Map<String, Object> context){
    	String periodTypeId = (String)context.get("periodTypeId");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	Locale locale = (Locale)context.get("locale");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String departmentId = (String)context.get("departmentId");
    	List<GenericValue> emplList = FastList.newInstance();
    	
    	PeriodEnum period = PeriodEnum.NA;
		if("YEARLY".equals(periodTypeId)){
			period = PeriodEnum.YEARLY;
		}else if("QUARTERLY".equals(periodTypeId)){
			period = PeriodEnum.QUARTERLY;
		}else if("MONTHLY".equals(periodTypeId)){
			period = PeriodEnum.MONTHLY;
		}else if("WEEKLY".equals(periodTypeId)){
			period = PeriodEnum.WEEKLY;
		}else if("DAILY".equals(periodTypeId)){
			period = PeriodEnum.DAILY;
		}
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Timestamp tmpFromDate = fromDate;
		Timestamp tmpThruDate = null;
		Map<String, Object> resultService = FastMap.newInstance();
		List<Map<String,Object>> listResult = FastList.newInstance();
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getSalaryAmountList", context, userLogin, timeZone, locale);
			
    		Organization org = PartyUtil.buildOrg(delegator, departmentId, true, false);
    		emplList.addAll(org.getEmployeeInOrg(delegator));
			if(UtilValidate.isNotEmpty(emplList)){
				ctxMap.put("emplList", emplList);
				switch (period) {
					case YEARLY:
						tmpThruDate = UtilDateTime.getYearEnd(tmpFromDate, timeZone, locale);
						if(thruDate.after(tmpThruDate)){
							while(tmpFromDate.before(thruDate)){
								if(tmpThruDate.after(thruDate)){
									ctxMap.put("thruDate", thruDate);
								}else{
									ctxMap.put("thruDate", tmpThruDate);
								}
								ctxMap.put("fromDate", tmpFromDate);
								resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
								if(ServiceUtil.isSuccess(resultService)){
									listResult.add(resultService);
								}
								tmpFromDate = UtilDateTime.getYearStart(tmpFromDate, 0, 1);
								tmpThruDate = UtilDateTime.getYearEnd(tmpFromDate, timeZone, locale);
							}
						}else{
							resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								listResult.add(resultService);
							}
						}
						break;
					case QUARTERLY:
						tmpThruDate = DateUtil.getQuarterEnd(tmpFromDate, locale, timeZone);
						if(thruDate.after(tmpThruDate)){
							while(tmpFromDate.before(thruDate)){
								if(tmpThruDate.after(thruDate)){
									ctxMap.put("thruDate", thruDate);
								}else{
									ctxMap.put("thruDate", tmpThruDate);
								}
								ctxMap.put("fromDate", tmpFromDate);
								resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
								if(ServiceUtil.isSuccess(resultService)){
									listResult.add(resultService);
								}
								tmpFromDate = DateUtil.getQuarterStart(tmpFromDate, locale, timeZone, 1);
								tmpThruDate = DateUtil.getQuarterEnd(tmpFromDate, locale, timeZone);
							}
						}else{
							resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								listResult.add(resultService);
							}
						}
						break;	
					case MONTHLY:
						tmpThruDate = UtilDateTime.getMonthEnd(tmpFromDate, timeZone, locale);
						if(thruDate.after(tmpThruDate)){
							while(tmpFromDate.before(thruDate)){
								if(tmpThruDate.after(thruDate)){
									ctxMap.put("thruDate", thruDate);
								}else{
									ctxMap.put("thruDate", tmpThruDate);
								}
								ctxMap.put("fromDate", tmpFromDate);
								//ctxMap.put("thruDate", tmpThruDate);
								resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
								if(ServiceUtil.isSuccess(resultService)){
									listResult.add(resultService);
								}
								tmpFromDate = UtilDateTime.getMonthStart(tmpFromDate, 0, 1);
								tmpThruDate = UtilDateTime.getMonthEnd(tmpFromDate, timeZone, locale);
							}
						}else{
							resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								listResult.add(resultService);
							}
						}
						break;
					case WEEKLY:
						tmpThruDate = UtilDateTime.getWeekEnd(tmpFromDate, timeZone, locale);
						if(thruDate.after(tmpThruDate)){
							while(tmpFromDate.before(thruDate)){
								if(tmpThruDate.after(thruDate)){
									ctxMap.put("thruDate", thruDate);
								}else{
									ctxMap.put("thruDate", tmpThruDate);
								}
								ctxMap.put("fromDate", tmpFromDate);
								resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
								if(ServiceUtil.isSuccess(resultService)){
									listResult.add(resultService);
								}
								ctxMap.put("fromDate", tmpFromDate);
								tmpFromDate = UtilDateTime.getWeekStart(tmpFromDate, 0, 1);
								tmpThruDate = UtilDateTime.getWeekEnd(tmpFromDate);
							}
						}else{
							resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								listResult.add(resultService);
							}
						}
						break;
					case DAILY:
						tmpThruDate = UtilDateTime.getDayEnd(tmpFromDate, timeZone, locale);
						if(thruDate.after(tmpThruDate)){
							while(tmpFromDate.before(thruDate)){
								if(tmpThruDate.after(thruDate)){
									ctxMap.put("thruDate", thruDate);
								}else{
									ctxMap.put("thruDate", tmpThruDate);
								}
								ctxMap.put("fromDate", tmpFromDate);
								//ctxMap.put("thruDate", tmpThruDate);
								resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
								if(ServiceUtil.isSuccess(resultService)){
									listResult.add(resultService);
								}
								tmpFromDate = UtilDateTime.getDayStart(tmpFromDate, 1);
								tmpThruDate = UtilDateTime.getDayEnd(tmpFromDate);
							}
						}else{
							resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								listResult.add(resultService);
							}
						}
						break;
					default:
						break;
				}
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "NoEmployeeToCalcSalary", UtilMisc.toMap("departmentList", departmentId), locale));
			}
			
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		retMap.put("listEmplSalaryInPeriod", listResult);
    	return retMap;
    }
    
    /*
     * Description: calculate and output salary for parties
     * */
    // TODO Cache parameters before calculating!
    public static Map<String, Object> getSalaryAmountList(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	List<EntityEmployeeSalary> listEntitySalaryAmount = null;
		List<String> listInputFormulas = (List<String>)context.get("formulaList");
    	Timestamp tThruDate = (Timestamp)context.get("thruDate");
    	Timestamp tFromDate = (Timestamp)context.get("fromDate");
    	String periodTypeId = (String)context.get("periodTypeId");
    	//String strEmployeeId = (String)context.get("pdfPartyId");
    	List<GenericValue> emplList = (List<GenericValue>)context.get("emplList");
    	Locale locale = (Locale) context.get("locale");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	// first access function
    	if((listInputFormulas == null) || (listInputFormulas.isEmpty())){
    		return ServiceUtil.returnSuccess();
    	}
    	
    	try {
    		//get all Function that related to function in listInputFormulas
    		//List<String> formulaRelatedList = FastList.newInstance();
        	for(String formula: listInputFormulas){
        		GenericValue formulaGv = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", formula), false);
        		if(formulaGv == null){
        			Debug.log("code null: " + formula);
        			//System.out.println("code null: " + formula);
        		}
        		/*String function = formulaGv.getString("function");
        		if(!function.contains("if")){
        			String[] functionArr = function.split("[\\+\\-\\*\\/\\%]");
        			for(String tempFunc: functionArr){
        				tempFunc = tempFunc.trim();
        				if(tempFunc.contains("()")){
        					tempFunc = tempFunc.replace("(","");
            				tempFunc = tempFunc.replace(")",""); 
            				if(!listInputFormulas.contains(tempFunc) && !formulaRelatedList.contains(tempFunc)){
            					formulaRelatedList.add(tempFunc);
            				}
        				}
        			}
        		}*/
        	}
        	//listInputFormulas.addAll(formulaRelatedList);
        	
    		listEntitySalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, periodTypeId, emplList, listInputFormulas, tFromDate, tThruDate, locale, timeZone);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	result.put("salaryAmountList", listEntitySalaryAmount); 
    	result.put("formulaList", listInputFormulas);
    	result.put("fromDate", tFromDate);
    	result.put("thruDate", tThruDate);
    	return result;
    }
    /*
     * Description: raise invoice and payment
     * */    
    public static Map<String, Object> PayrollInvoiceAndPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	List<String> listInputs = (List<String>)context.get("formulaCode");
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> invoiceMap;
    	String periodTypeId = (String)context.get("periodTypeId");
    	List<EntityEmployeeSalary>  listSalaryAmount = FastList.newInstance();
    	Timestamp tsFromDate = (Timestamp)context.get("fromDate");
    	Timestamp tsThruDate = (Timestamp)context.get("thruDate");
    	Map<String,String> mapFormulaItemType = new HashMap<String,String>();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	// Get salaryAmountList
    	try {
    		EntityExpr entityExpr1 = EntityCondition.makeCondition("invoiceItemTypeId",EntityJoinOperator.NOT_EQUAL, "");
    		List<GenericValue> listFormula = delegator.findList("PayrollFormula", entityExpr1, UtilMisc.toSet("code","invoiceItemTypeId"), null, null, false);
        	for(GenericValue generic:listFormula){
        		mapFormulaItemType.put(generic.get("code").toString(), generic.get("invoiceItemTypeId").toString());
        	}
    		if(listInputs != null && !listInputs.isEmpty()){
//    			for(String str:listInputs){
//    				//listSalaryAmount.add(PayrollEngine.calculateParticipateFunction(ctx, PayrollEngine.buildFormula(ctx,str + "()",tsFromDate,tsThruDate,PayrollUtil.CallBuildFormulaType.NATIVE,"",mapFormulaCache),tsFromDate,tsThruDate,""));
//    			}
    			// TODO implement for input formulas
    			listSalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, periodTypeId, null, UtilMisc.toList("LUONG"), tsFromDate, tsThruDate, locale, timeZone);
    		}else{
    			listSalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, periodTypeId, null, UtilMisc.toList("LUONG"), tsFromDate, tsThruDate, locale, timeZone);
    		}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
		}
    	if(!listSalaryAmount.isEmpty()){
	    	for(EntityEmployeeSalary salaryEmployeeAmount:listSalaryAmount){
	    		try {
		    		String strEmployeeId = salaryEmployeeAmount.getListSalaryAmount().get(0).getPartyId();
		    		// create invoice assign Internal Organization to Employees 
		    		invoiceMap = FastMap.newInstance();
	    			invoiceMap.put("partyId", "company");
	    			invoiceMap.put("statusId",context.get("statusId"));
	    			invoiceMap.put("currencyUomId",context.get("currencyUomId"));
	    			invoiceMap.put("partyIdFrom",strEmployeeId);
	    			invoiceMap.put("invoiceTypeId","PAYROL_INVOICE");
	    			invoiceMap.put("partyIdFrom",strEmployeeId);
	    			invoiceMap.put("userLogin",context.get("userLogin"));
	    			Map<String, Object> tmpMap = dispatcher.runSync("createInvoice", invoiceMap);
	    			tmpMap.put("userLogin",context.get("userLogin"));
	    			tmpMap.put("description","Payment for payroll in HRMS for user: " + strEmployeeId);
	    			
	    			// TODO move the following code to PayrollEngine
	    		    for (Map.Entry<String, String> entry : salaryEmployeeAmount.getMapFormulaVqalue().entrySet()) {
	    		        String key = entry.getKey();
	    		        String value = entry.getValue();
	    		        if(key.contains("()") && !value.matches(".*[a-zA-Z]+.*")){
	    		        	// FIXME use global configuration 
	    		        	if(key.equals("LUONG_CO_BAN()")){
	    		        		tmpMap.put("amount",value);
	    		        	}else{
	    		        		tmpMap.put("amount","-" + value);
	    		        	}
		    		        if(mapFormulaItemType.containsKey(key.replace("()", ""))){
		    		        	tmpMap.put("invoiceItemTypeId",mapFormulaItemType.get(key.replace("()", "")));
		    		        }else{
		    		        	tmpMap.put("invoiceItemTypeId","PAYROL_SALARY");
		    		        }
			    			//createInvoiceItemPayrol event
			    			dispatcher.runSync("createInvoiceItem", tmpMap);
	    		        }else{
	    		        	EntitySalaryAmount ettTMPResult = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, value,tsFromDate,tsThruDate,strEmployeeId,PeriodWorker.getParameterByPeriod(ctx, periodTypeId, PayrollDataPreparation.getEmployeeParametersCache(ctx, userLogin, strEmployeeId, tsFromDate, tsThruDate, timeZone), tsFromDate, tsThruDate, timeZone, locale));
	    		        	// FIXME use global configuration 
	    		        	if(!key.equals("LUONG()") && !key.equals("LUONG_CO_BAN()")){
	    		        		tmpMap.put("amount","-" + ettTMPResult.getAmount());
	    		        		if(mapFormulaItemType.containsKey(key.replace("()", ""))){
			    		        	tmpMap.put("invoiceItemTypeId",mapFormulaItemType.get(key.replace("()", "")));
			    		        }else{
			    		        	tmpMap.put("invoiceItemTypeId","PAYROL_SALARY");
			    		        }
				    			//createInvoiceItemPayrol event
				    			dispatcher.runSync("createInvoiceItem", tmpMap);
	    		        	}
	    		        }
	    		    }
		    		// Update invoice's state to INVOICE_APPROVED state  
	    			invoiceMap = FastMap.newInstance();
	    			invoiceMap.put("invoiceId",tmpMap.get("invoiceId"));
	    			invoiceMap.put("statusId","INVOICE_READY");
	    			invoiceMap.put("userLogin",context.get("userLogin"));
	    			dispatcher.runSync("setInvoiceStatus", invoiceMap);
	    			// Update invoice's state to INVOICE_PAID state to complete Invoice
//	    			invoiceMap.put("statusId","INVOICE_PAID");
//	    			dispatcher.runSync("setInvoiceStatus", invoiceMap);
//	        		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//	        		result.put(ModelService.SUCCESS_MESSAGE, 
//	                        UtilProperties.getMessage(resourceNoti, "generateInvoiceAndPaymentSuccessfully", locale));
	    		} catch (Exception e) {
	    			Debug.logError(e, module);
	    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
	    		}
	    	}
    	}
		return result;
    }
    /**
     * Create Invoice for a employee
     * @param ctx
     * @param context
     * @return
     * @throws GenericServiceException 
     * @throws GenericEntityException 
     */
    public static Map<String, Object> createPayrollInvoiceAndPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	//Get parameters
    	//Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> invoiceMap;
    	
    	//FIXME use global configuration 
    	String partyId = (String)context.get("partyId");
    	invoiceMap = FastMap.newInstance();
    	Properties generalProp = UtilProperties.getProperties("general");
    	String partyIdCompany = (String)generalProp.get("ORGANIZATION_PARTY");
    	//String codeFormula = (String) context.get("code");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	//Timestamp thruDate = (Timestamp) context.get("thruDate");
    	String payrollTableId = (String)context.get("payrollTableId");
		invoiceMap.put("partyId",partyIdCompany);
		invoiceMap.put("statusId","INVOICE_IN_PROCESS");
		invoiceMap.put("currencyUomId", context.get("currencyUomId"));
		invoiceMap.put("partyIdFrom",partyId);
		invoiceMap.put("invoiceTypeId", "PAYROL_INVOICE");
		invoiceMap.put("userLogin",context.get("userLogin"));
		Map<String, Object> tmpMap = null;
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		
		//Create Invoice for employee		
		try {
			//get all function of code
			//GenericValue payrollFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", codeFormula), false);
			//GenericValue payrollTable =  delegator.findOne("PayrollTable", UtilMisc.toMap("code", codeFormula, "partyId", partyId, "fromDate", fromDate, "thruDate", thruDate), false);
			List<GenericValue> listPayrollFormula = delegator.findByAnd("PayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId, "fromDate", fromDate,"partyId", partyId ,"statusId", "PAYR_APP"), null, false);
			Set<String> codeSetId = FastSet.newInstance();
			
			for(GenericValue payrollFormula: listPayrollFormula){
				String code = payrollFormula.getString("code");
				codeSetId.add(code);
				GenericValue codeTmp = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
				if(codeTmp == null){
					System.out.println("code null: " + code);
				}
				//codeSetId.addAll(PayrollUtil.getAllRelatedFunction(delegator, delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false)));
				/*String function = payrollFormula.getString("function");
				String[] functionArr = function.split("[\\+\\-\\*\\/\\%]");
				for(String tempFunc: functionArr){
					if(tempFunc.contains("()")){
						tempFunc = tempFunc.trim();
						tempFunc = tempFunc.replace("(","");
						tempFunc = tempFunc.replace(")","");
						codeSetId.add(tempFunc);
					}
				}*/
			}
			//check whether codeSetId have invoiceItemType, if not return and not create invoice
			GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
    		String departmentId = department.getString("partyIdFrom");
    		/*List<EntityCondition> partyPayrollInvoiceItemTypeConds = FastList.newInstance();
    		partyPayrollInvoiceItemTypeConds.add(EntityCondition.makeCondition("partyId", departmentId));
    		partyPayrollInvoiceItemTypeConds.add(EntityCondition.makeCondition("code", EntityOperator.IN, codeSetId));
    		partyPayrollInvoiceItemTypeConds.add(EntityUtil.getFilterByDateExpr());*/
    		
    		//List<GenericValue> listInvoiceItemTypeOfCodes = delegator.findList("PartyPayrollFormulaInvoiceItemType", EntityCondition.makeCondition(partyPayrollInvoiceItemTypeConds), null, null, null, false);
    		List<GenericValue> listInvoiceItemTypeOfCodes = PayrollUtil.getListInvoiceItemTypeOfFormulaByParty(delegator, departmentId, codeSetId);
    		if(UtilValidate.isNotEmpty(listInvoiceItemTypeOfCodes)){
    			tmpMap = dispatcher.runSync("createInvoice", invoiceMap);					
    			tmpMap.put("userLogin",context.get("userLogin"));
    			retMap.put("invoiceId", tmpMap.get("invoiceId"));	
    			for(String code: codeSetId){
    				//String code = codeGen.getString("code");
    	    		GenericValue tempPayrollFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
    	    		String desc = tempPayrollFormula.getString("description");
    	    		if(UtilValidate.isEmpty(desc)){
    	    			desc = tempPayrollFormula.getString("name");
    	    		}
    	    		tmpMap.put("description", desc);
    	    		
    	    		//String invoiceItemTypeId = tempPayrollFormula.getString("invoiceItemTypeId");
    	    		/*List<EntityCondition> conditions = FastList.newInstance();
    	    		conditions.add(EntityUtil.getFilterByDateExpr());
    	    		conditions.add(EntityCondition.makeCondition("partyId", departmentId));
    	    		conditions.add(EntityCondition.makeCondition("code", code));
    	    		List<GenericValue> invoiceItemTypeList = delegator.findList("PartyPayrollFormulaInvoiceItemType", EntityCondition.makeCondition(conditions), null, null, null, false);*/
    	    		List<GenericValue> invoiceItemTypeList = EntityUtil.filterByCondition(listInvoiceItemTypeOfCodes, EntityCondition.makeCondition("code", code));
    	    		//createInvoiceItemPayrol event
    	    		if(UtilValidate.isNotEmpty(invoiceItemTypeList)){
    	    			GenericValue invoiceItemType = EntityUtil.getFirst(invoiceItemTypeList);
    	    			String invoiceItemTypeId = invoiceItemType.getString("invoiceItemTypeId");
    	    			//GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
    	    			//String parentTypeId = invoiceItemType.getString("parentTypeId");
    	    			GenericValue codeGen = delegator.findOne("PayrollTable", 
    	    														UtilMisc.toMap("payrollTableId", payrollTableId, "code", code, 
    	    																"partyId", partyId, "fromDate", fromDate), false);
    	    			//if("PAYROL_EARN_HOURS".equals(parentTypeId)){
    	    			GenericValue payrollFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
    	    			String payrollCharacteristicId = payrollFormula.getString("payrollCharacteristicId");
    	    			
    	    			String amount = codeGen.getString("value");
    	    			if(amount != null){
    	    				BigDecimal amountNbr = new BigDecimal(amount);
    	    				if(payrollCharacteristicId != null){
    	    					if("DEDUCTION".equals(payrollCharacteristicId)){
    	    						amountNbr = amountNbr.negate();
    	    					}
        	    				tmpMap.put("amount", amountNbr);
        	    				tmpMap.put("quantity", BigDecimal.ONE);
        	    				tmpMap.put("invoiceItemTypeId", invoiceItemTypeId);
            	    			dispatcher.runSync("createInvoiceItem", tmpMap);
            	    			//Update payroll table's state to PAYR_PAID state
            	    			codeGen.put("statusId", "PAYR_PAID");
            					codeGen.store();
        	    			}
    	    			}
    	    		}
    			}
    		}
			//payrollTable.set("statusId", "PAYR_PAID");
			//payrollTable.store();
			// Update invoice's state to INVOICE_APPROVED state  
			/*invoiceMap = FastMap.newInstance();
			invoiceMap.put("invoiceId", tmpMap.get("invoiceId"));
			invoiceMap.put("statusId", "INVOICE_READY");
			invoiceMap.put("userLogin",context.get("userLogin"));
			dispatcher.runSync("setInvoiceStatus", invoiceMap);*/			
		} catch (GenericServiceException e) {
			e.printStackTrace();						
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
    }
    
    public static Map<String, Object> getListEmplSalaryPaidActual(DispatchContext dctx, Map<String, Object> context){
    	Map<String, Object> retMap = FastMap.newInstance();
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String payrollTableId = request.getParameter("payrollTableId");
    	Locale locale = (Locale)context.get("locale");
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = dctx.getDelegator();    	
    	//LocalDispatcher dispatcher = dctx.getDispatcher();
    	String fromDateStr = request.getParameter("fromDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	GenericValue payrollTableRecord;
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	int totalRows = 0;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		try {
			payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundPayrollTable", locale));
			}
			EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("payrollTableId", payrollTableId),
																EntityJoinOperator.AND, EntityCondition.makeCondition("statusId", "PAYR_APP"));
			conds = EntityCondition.makeCondition(conds, EntityJoinOperator.AND, EntityCondition.makeCondition("fromDate", fromDate));
			List<GenericValue> listPartyPayrollTable = delegator.findList("PayrollTableGroupBy", conds, null, UtilMisc.toList("partyId"), null, false);
			if(UtilValidate.isNotEmpty(listPartyPayrollTable)){
				Timestamp tempThruDate = listPartyPayrollTable.get(0).getTimestamp("thruDate");
				List<String> emplList = EntityUtil.getFieldListFromEntityList(listPartyPayrollTable, "partyId", true);
				
				List<GenericValue> payrollRecordCode = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), 
						UtilMisc.toList("code"), false);
				List<String> payrollRecordCodeList = EntityUtil.getFieldListFromEntityList(payrollRecordCode, "code", true);
				EntityCondition commonConds = EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList);
				EntityCondition incomeConds = EntityCondition.makeCondition("payrollCharacteristicId", "INCOME");
				EntityCondition deductionCondition = EntityCondition.makeCondition("payrollCharacteristicId", "DEDUCTION");
				
				List<GenericValue> listFormulaIncome = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, incomeConds), null, null, null, false);
				List<GenericValue> listFormulaDeduction = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, deductionCondition), null, null, null, false);
				totalRows = emplList.size();
				if(end > emplList.size()){
					end = emplList.size();
				}
				emplList = emplList.subList(start, end);
				for(String partyId: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					tempMap.put("partyId", partyId);
					tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
					GenericValue partyGroup = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
					if(partyGroup != null){
						tempMap.put("partyGroupId", partyGroup.getString("partyIdFrom"));
						tempMap.put("partyGroupName", PartyHelper.getPartyName(delegator, partyGroup.getString("partyIdFrom"), false));
					}
					double totalIncome = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, FastMap.<String, Object>newInstance(), FastMap.<String, Double>newInstance(), listFormulaIncome);
					double totalDeduction = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, FastMap.<String, Object>newInstance(), FastMap.<String, Double>newInstance(), listFormulaDeduction);
					double actualPaid = totalIncome - totalDeduction;
					tempMap.put("salaryActualPaid", actualPaid);
					tempMap.put("fromDate", fromDate);
					tempMap.put("thruDate", tempThruDate);
					listReturn.add(tempMap);	
				}
			}
			
			
			retMap.put("listIterator", listReturn);
			retMap.put("TotalRows", String.valueOf(totalRows));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return retMap;
    }
    
    /**
     * 
     */
    public static Map<String, Object> activeSalaryPeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
    	//Get parameters
    	String jobName = (String)context.get("jobName");
    	Long jobFrequency = (Long)context.get("jobFrequency");
    	Timestamp startTime = (Timestamp)context.get("startTime");
    	Timestamp expireTime = (Timestamp)context.get("expireTime");
    	String description = (String)context.get("description");
    	List<String> formulaList = (List<String>)context.get("formulaList");
    	Locale locale = (Locale)context.get("locale");
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	//Insert into PayrollScheduleLog
    	GenericValue payrollScheduleLog = delegator.makeValue("PayrollScheduleLog");
    	payrollScheduleLog.set("jobName", jobName);
    	payrollScheduleLog.set("jobFrequency", jobFrequency);
    	payrollScheduleLog.set("startTime", startTime);
    	//Set first run time equal start time
    	payrollScheduleLog.set("runTime", startTime);
    	payrollScheduleLog.set("expireTime",expireTime);
    	payrollScheduleLog.set("description", description);
    	
    	try {
			payrollScheduleLog.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
    	
    	//Insert into PayrollScheduleFormula
    	for(String formula : formulaList){
    		GenericValue payrollScheduleFormula = delegator.makeValue("PayrollScheduleFormula");
    		payrollScheduleFormula.set("jobName", jobName);
    		payrollScheduleFormula.set("code", formula);
    		try {
    			payrollScheduleFormula.create();
    		} catch (GenericEntityException e) {
    			Debug.log(e.getMessage(), module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
    		}
    	}
    	
    	//Active Schedule Salary
    	LocalDispatcher localDpc = ctx.getDispatcher();
    	Map<String, Object> scheduleSalaryCtx = UtilMisc.toMap("jobName", jobName,"formulaList", formulaList, "userLogin", (GenericValue)context.get("userLogin"));
    	try {
    		//FIXME Need config global
			localDpc.schedule(jobName, "pool", "perpareSalaryPeriod", scheduleSalaryCtx, startTime.getTime(), jobFrequency.intValue(), 1, -1, expireTime.getTime(), 5);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
    }
    /**
     * 
     *
     */
    //FIXME Context is not exist
    public static Map<String, Object> perpareSalaryPeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	//Get parameters
    	String jobName = (String)context.get("jobName");
    	Locale locale = (Locale)context.get("locale");
    	
    	List<GenericValue> payrollScheduleFormulaList = FastList.newInstance();
		try {
			payrollScheduleFormulaList = delegator.findList("PayrollScheduleFormula", EntityCondition.makeCondition("jobName", jobName), null, null, null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e1.getMessage()}, locale));
		}
    	
		List<String> formulaList = FastList.newInstance(); 
    	for(GenericValue item: payrollScheduleFormulaList){
    		formulaList.add(item.getString("code"));
    	}
    	
    	GenericValue payrollScheduleLog = null;
    	try {
			payrollScheduleLog = delegator.findOne("PayrollScheduleLog", false, UtilMisc.toMap("jobName", jobName));
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[]{e.getMessage()}, locale));
		}
    	
    	Timestamp oldRuntime = payrollScheduleLog.getTimestamp("runTime");
    	Timestamp nowRuntime =  new Timestamp(new Date().getTime());
    	
    	payrollScheduleLog.put("runTime", nowRuntime);
    	try {
			payrollScheduleLog.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
    	
    	Map<String, Object> result = FastMap.newInstance();
    	result.put("formulaList", formulaList);
    	result.put("fromDate", oldRuntime);
    	result.put("thruDate", nowRuntime);
    	result.put("userLogin", (GenericValue)context.get("userLogin"));
    	result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
		return result;
    }
    
    public static Map<String, Object> logPayrollTable(DispatchContext ctx, Map<String, ? extends Object> context) {
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	//Get parameters
    	List<String> formulaList = (List<String>)context.get("formulaList");
    	List<EntityEmployeeSalary> listEntitySalaryAmount = (List<EntityEmployeeSalary>)context.get("salaryAmountList");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	String payrollTableId = (String)context.get("payrollTableId");
    	Locale locale = (Locale) context.get("locale");
    	
    	//List payroll for all Employee in a payroll period
    	for(int i = 0; i < listEntitySalaryAmount.size(); i++){
    		for(int j = 0; j < formulaList.size(); j++){
    			GenericValue payrollTable = delegator.makeValue("PayrollTable");
    			payrollTable.set("payrollTableId", payrollTableId);
    			payrollTable.set("partyId", listEntitySalaryAmount.get(i).getListSalaryAmount().get(0).getPartyId());
    			payrollTable.set("code", formulaList.get(j));
    			payrollTable.set("fromDate", fromDate);
    			payrollTable.set("thruDate", thruDate);
    			payrollTable.set("value", listEntitySalaryAmount.get(i).getListSalaryAmount().get(j).getAmount());
    			payrollTable.set("statusId", "PAYR_APP");
    			try {
    				delegator.createOrStore(payrollTable);
					//payrollTable.create();
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage(), module);
					ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
				}
    		}
    	}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
    }
    
    public static Map<String, Object> activeCreatePayrollInvoiceAndPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	LocalDispatcher localDispatcher = ctx.getDispatcher();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	//Get paid employee
    	EntityCondition condition1 = EntityCondition.makeCondition("statusId", "PAYR_APP");
    	//FIXME code "LUONG" is now hard fix, need use global setting
    	//EntityCondition condition2 = EntityCondition.makeCondition("code", "LUONG");
    	EntityCondition condition2 = EntityCondition.makeCondition("payrollTableId", payrollTableId);
    	String fromDateStr = (String)context.get("fromDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(condition1);
    	conditions.add(condition2);
    	conditions.add(EntityCondition.makeCondition("fromDate", fromDate));
    	List<GenericValue> paidEmployeeList = FastList.newInstance();
    	try {
    		paidEmployeeList = delegator.findList("PayrollTableGroupBy", EntityCondition.makeCondition(conditions, EntityOperator.AND), UtilMisc.toSet("payrollTableId", "partyId", "fromDate", "thruDate"), null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
		}
    	if(UtilValidate.isEmpty(paidEmployeeList)){
    		ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels.xml", "NoEmplCalcPayrollSalary", locale));
    	}
    	for(GenericValue paidEmployee : paidEmployeeList){
    		String partyId = paidEmployee.getString("partyId");
    		Map<String, Object> contextTmp = FastMap.newInstance();
    		contextTmp.put("partyId", partyId);
    		contextTmp.put("userLogin", context.get("userLogin"));
    		contextTmp.put("locale", context.get("locale"));
    		contextTmp.put("currencyUomId", context.get("currencyUomId"));
    		contextTmp.put("fromDate", paidEmployee.getTimestamp("fromDate"));
    		contextTmp.put("thruDate", paidEmployee.getTimestamp("thruDate"));
    		contextTmp.put("payrollTableId", payrollTableId);
    		try {
    			//String code = paidEmployee.getString("code");
    			//if("LUONG".equals(code)){
    				//contextTmp.put("code", code);
    				//localDispatcher.runAsync("createPayrollInvoiceAndPayment", contextTmp);
    				localDispatcher.schedule("pool", "createPayrollInvoiceAndPayment", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
    			//}
			}catch (ServiceAuthException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			} catch (ServiceValidationException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			}
    	}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "generateInvoiceAndPaymentActived", locale));
    }
    
    public static Map<String, Object> createNtfAndEmailPartyPayroll(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String invoiceId = (String) context.get("invoiceId");
    	try {
			GenericValue invoiceGv = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			String emplPartyId = invoiceGv.getString("partyIdFrom");
			String companyId = invoiceGv.getString("partyId");
			Locale locale = (Locale) context.get("locale");
			Properties generalProp = UtilProperties.getProperties("general");
			String email = generalProp.getProperty("mail.smtp.auth.user");
			String password = generalProp.getProperty("lbqiacdmftrmdiad");
			//TimeZone timeZone = (TimeZone) context.get("timeZone");
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			String header = UtilProperties.getMessage("PayrollUiLabels", "HRPayrollInformation", locale);
			String commonFromDate = UtilProperties.getMessage("HrCommonUiLabels", "CommonFromDate", locale);
			String commonThruDate = UtilProperties.getMessage("HrCommonUiLabels", "CommonThruDate", locale);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("header", header + " " + commonFromDate + " " + df.format(new Date(fromDate.getTime())) + " " + commonThruDate + " " + df.format(new Date(thruDate.getTime())));
			ntfCtx.put("partyId", emplPartyId);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("state", "open");
			ntfCtx.put("userLogin", context.get("userLogin"));
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("targetLink", "partyId=" + emplPartyId + ";fromDate=" + fromDate + ";thruDate=" + thruDate + ";statusId=PAYR_PAID");
			ntfCtx.put("action", "PayrollTablePartyHistory");
			dispatcher.runSync("createNotification", ntfCtx);
			
			//send email to employee
			Map<String, Object> emailAddress = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", emplPartyId, "userLogin", context.get("userLogin"), "locale",context.get("userLogin") ));
			Map<String, Object> emailCtx = FastMap.newInstance();
			Map<String, Object> bodyParameters = FastMap.newInstance();
			List<GenericValue> emplPosition = PartyUtil.getCurrPositionTypeOfEmpl(delegator, emplPartyId);
			String emplPositionStr = "";
			if(UtilValidate.isNotEmpty(emplPosition)){
				GenericValue emplPos = EntityUtil.getFirst(emplPosition);
				String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
				GenericValue emplPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				if(UtilValidate.isNotEmpty(emplPosType)){
					emplPositionStr = emplPosType.getString("description");
				}
			}			
			bodyParameters.put("companyName", PartyHelper.getPartyName(delegator, companyId, false));
			//bodyParameters.put("companyAddress", ContactMechWorker.getPartyPostalAddresses(request, partyId, curContactMechId));
			//bodyParameters.put("title", "Thông tin lương");
			bodyParameters.put("employeeId", emplPartyId);
			bodyParameters.put("employeeName", PartyHelper.getPartyName(delegator, emplPartyId, false));
			GenericValue dept = PartyUtil.getDepartmentOfEmployee(delegator, emplPartyId);
			
			bodyParameters.put("emplDept", PartyHelper.getPartyName(delegator, dept.getString("partyIdFrom"), false));
			bodyParameters.put("fromDate", fromDate);
			bodyParameters.put("thruDate", thruDate);
			bodyParameters.put("emplPosition", emplPositionStr);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
			conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, companyId));
			GenericValue companyContactMech = EntityUtil.getFirst(delegator.findList("PartyContactMechPurposeView", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false));
			String companyAddressDetails = "";
			if(UtilValidate.isNotEmpty(companyContactMech)){
				GenericValue companyPostallAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", companyContactMech.getString("contactMechId")), false);
				//List<GenericValue> companyAddr = d			
				String companyCountry = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("countryGeoId")), false).getString("geoName");	
				String companyStateProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("stateProvinceGeoId")), false).getString("geoName");
				
				GenericValue companyDistrictGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("districtGeoId")), false);
				String companyDistrictGeoId = "";
				if(UtilValidate.isNotEmpty(companyDistrictGeo)){
					companyDistrictGeoId = companyDistrictGeo.getString("geoName"); 
				}
				GenericValue companyWardGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("wardGeoId")), false);
				String companyWardGeoId = "";
				if(UtilValidate.isNotEmpty(companyWardGeo)){
					companyWardGeoId = companyWardGeo.getString("geoName");
				}
				companyAddressDetails = companyPostallAddr.getString("address1") + ", " + companyWardGeoId + ", " + companyDistrictGeoId + ", " + companyStateProvince + ", " + companyCountry;
			}else{
				companyAddressDetails = UtilProperties.getMessage("HrCommonUiLabels", "AddressNotExists", locale);
			}
			
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyId", companyId));
			List<GenericValue> partyTelecomNbr = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false);
			
			List<EntityCondition> incomeConditions = FastList.newInstance();
			incomeConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			incomeConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("PAYROL_EARN_HOURS")));
			List<GenericValue> payrollIncomes = delegator.findList("InvoiceItemAndType", EntityCondition.makeCondition(incomeConditions, EntityOperator.AND),null, null, null, false);
			
			List<EntityCondition> deductionConditions = FastList.newInstance();
			deductionConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			deductionConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("PAYROL_DD_FROM_GROSS", "PAYROL_TAXES")));
			List<GenericValue> payrollDeduction = delegator.findList("InvoiceItemAndType", EntityCondition.makeCondition(deductionConditions, EntityOperator.AND),null, null, null, false);
			
			
			bodyParameters.put("payrollIncomes", payrollIncomes);
			bodyParameters.put("dateJoin", dept.getTimestamp("fromDate"));
			bodyParameters.put("payrollDeductions", payrollDeduction);
			bodyParameters.put("uomId", invoiceGv.getString("currencyUomId"));
			bodyParameters.put("companyAddress", companyAddressDetails);
			bodyParameters.put("phoneNumber", partyTelecomNbr);
			bodyParameters.put("currencyUomId", invoiceGv.get("currencyUomId"));
			bodyParameters.put("actualReceipt", InvoiceWorker.getInvoiceTotal(delegator,invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator,invoiceId)));
			emailCtx.put("userLogin", context.get("userLogin"));
			emailCtx.put("locale", context.get("locale"));
			emailCtx.put("sendTo", emailAddress.get("emailAddress"));//emailAddress.get("emailAddress")
			emailCtx.put("partyIdTo", emplPartyId);
			emailCtx.put("bodyParameters", bodyParameters);
			emailCtx.put("authUser", email);
			emailCtx.put("authPass", password);
			emailCtx.put("sendFrom", email);
			//emailCtx.put("subject", subject);
			emailCtx.put("emailTemplateSettingId", "PARTY_PAYROLL_NOTIFY");
		    Map<String, Object> results = dispatcher.runSync("sendMailFromTemplateSetting", emailCtx);
		    if(ServiceUtil.isError(results)){
		    	ntfCtx.put("partyId", userLogin.getString("partyId"));
		    	ntfCtx.put("header", "Xảy ra lỗi khi gửi email phiếu lương đến " + PartyHelper.getPartyName(delegator, emplPartyId, false));
		    	dispatcher.runSync("createNotification", ntfCtx);
		    }
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();			
		} catch (Exception e) {
			e.printStackTrace();
		}	
    	return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> notifyErrCreateInvoicePayment(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = dctx.getDelegator();
    	String payrollTableId = (String)context.get("payrollTableId");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	Calendar calFromDate = Calendar.getInstance();
    	calFromDate.setTime(fromDate);
    	Calendar calThruDate = Calendar.getInstance();
    	calThruDate.setTime(thruDate);
    	String partyId = (String)context.get("partyId");
    	String displayFromDate = calFromDate.get(Calendar.YEAR) + "-" + calFromDate.get(Calendar.MONTH) + "-" + calFromDate.get(Calendar.DATE);
    	String displayThruDate = calThruDate.get(Calendar.YEAR) + "-" + calThruDate.get(Calendar.MONTH) + "-" + calThruDate.get(Calendar.DATE);
    	Map<String, Object> ntfCtx = FastMap.newInstance();
    		
		ntfCtx.put("partyId", userLogin.getString("partyId"));
		ntfCtx.put("header", "Xảy ra lỗi khi tạo hóa đơn tính lương từ ngày " + displayFromDate + "đến ngày " + displayThruDate +" cho " + PartyHelper.getPartyName(delegator, partyId, false));
		ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
		ntfCtx.put("targetLink", "payrollTableId=" + payrollTableId);
		ntfCtx.put("state", "open");
		ntfCtx.put("ntfType", "ONE");
		ntfCtx.put("action", "ApprovalPayrollTable");
		ntfCtx.put("userLogin", userLogin);
		try {
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
	
	/*public static Map<String, Object> createEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			//check whether emplPositionType have setting
			List<GenericValue> checkEmplPosTypeRate = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(
																										EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(),
																											EntityOperator.AND,
																											EntityCondition.makeCondition("emplPositionTypeId",emplPositionTypeId))), 
																			null, null, null, false);
			if(UtilValidate.isNotEmpty(checkEmplPosTypeRate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "EmplPosTypeRateIsSet", locale));
			}
			//create new entity
			GenericValue emplPosTypeRate = delegator.makeValidValue("OldEmplPositionTypeRate", context);
			if(fromDate == null){
				fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			}
			emplPosTypeRate.set("fromDate", fromDate);
			emplPosTypeRate.create();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}*/
	
	/*public static Map<String, Object> getEmplPositionTypeRateHistory(DispatchContext dctx, Map<String, Object> context){
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("listEmplPositionTypeRate", listReturn);
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> listEmplPosTypeRate = delegator.findByAnd("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), UtilMisc.toList("fromDate"), false);
			for(GenericValue tempGv: listEmplPosTypeRate){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("fromDateDetail", tempGv.getTimestamp("fromDate").getTime());
				tempMap.put("thruDateDetail", tempGv.getTimestamp("thruDate") != null ? tempGv.getTimestamp("thruDate").getTime(): null);
				tempMap.put("rateCurrencyUomIdDetail", tempGv.getString("rateCurrencyUomId"));
				tempMap.put("periodTypeIdDetail", tempGv.getString("periodTypeId"));
				tempMap.put("rateAmountDetail", tempGv.get("rateAmount"));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	public static Map<String, Object> createPayrollTableRecord(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String formulaListStr = (String)context.get("formulaList");
		GenericValue payrollTableRecord = delegator.makeValue("PayrollTableRecord");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		Locale locale = (Locale)context.get("locale");
		String payrollTableName = (String) context.get("payrollTableName");
		String payrollTableNameNoSpace = CommonUtil.removeWhiteSpace(payrollTableName);
		String partyId = (String)context.get("partyId");
		if(!CommonUtil.containsValidCharacter(payrollTableNameNoSpace)){
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "PayrollTableNameContainInvalidChar", locale));
		}
		payrollTableRecord.set("customTimePeriodId", customTimePeriodId);		
		payrollTableRecord.set("payrollTableName", payrollTableName);
		payrollTableRecord.set("statusId", "PYRLL_TABLE_CREATED");
		payrollTableRecord.set("partyId", partyId);
		String payrollTableId = delegator.getNextSeqId("PayrollTableRecord");
		payrollTableRecord.set("payrollTableId", payrollTableId);
		
		List<String> formulaList = FastList.newInstance();

		try {
			List<GenericValue> allPayrollCharacteristic = delegator.findByAnd("PayrollCharacteristic", null, null, false);
			List<EntityCondition> conditions = FastList.newInstance();
			for(GenericValue tempGv: allPayrollCharacteristic){
				conditions.add(EntityCondition.makeCondition("payrollCharacteristicId", tempGv.getString("payrollCharacteristicId")));
			}
			conditions.add(EntityCondition.makeCondition("includedPayrollTable", "Y"));
			List<GenericValue> defaultFormulaCodeCalc = delegator.findList("PayrollFormula", EntityCondition.makeCondition(conditions, EntityOperator.OR), null, null, null, false);
			for(GenericValue tempGv: defaultFormulaCodeCalc){
				formulaList.add(tempGv.getString("code"));
			}
			if(formulaListStr != null){
				JSONArray formulaListJson = JSONArray.fromObject(formulaListStr);
				for(int i = 0; i < formulaListJson.size(); i++){
					if(!formulaList.contains(formulaListJson.getString(i))){
						formulaList.add(formulaListJson.getString(i));
					}
				}
			}
			payrollTableRecord.create();
			for(String formula: formulaList){
				GenericValue payrollTableCode = delegator.makeValue("PayrollTableCode");
				payrollTableCode.put("code", formula);
				payrollTableCode.put("payrollTableId", payrollTableId);
				payrollTableCode.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> sendProposalPayrollTable(DispatchContext dctx, Map<String, Object> context){
		//Map<String, Object> retMap = FastMap.newInstance();
		String payrollTableId = (String)context.get("payrollTableId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(payrollTableRecord == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotFindPayrollTableIdToProposal", 
						UtilMisc.toMap("payrollTableId", payrollTableId), locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if("PYRLL_TABLE_CREATED".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotProposalPayrollTableNotCalc", locale));
			}
			Map<String, Object> resultService = dispatcher.runSync("createWorkFlowProcess", UtilMisc.toMap("processName", "payroll table approval process", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				String processId = (String)resultService.get("processId");
				//create process state
				String startState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_START", "start");
				String approveState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_APPROVE", "approve");
				String completeState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_COMPPLETE", "complete");
				String deniedState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_DENIED", "request recalculate");
				//String cancelState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_CANCEL", "cancel");
				
				//create request approval payroll
				Map<String, Object> workFlowReqestMap = FastMap.newInstance();
				workFlowReqestMap.put("processId", processId);
				workFlowReqestMap.put("description", "Request approval payroll table");
				workFlowReqestMap.put("dateRequested", UtilDateTime.nowTimestamp());
				workFlowReqestMap.put("partyId", userLogin.getString("partyId"));
				workFlowReqestMap.put("userLogin", userLogin);
				//workFlowReqestMap.put("processStatusId", startState);
				resultService = dispatcher.runSync("createWorkFlowRequest", workFlowReqestMap);
				String requestId = (String)resultService.get("requestId");
				
				//create request att
				WorkFlowUtils.createRequestAttr(delegator, requestId, "targetLink", "payrollTableId=" + payrollTableId);
				WorkFlowUtils.createRequestAttr(delegator, requestId, "action", "viewPayrollTable");
				WorkFlowUtils.createRequestAttr(delegator, requestId, "payrollTableId", payrollTableId);
				//create group
				String groupHrmAdminId = WorkFlowUtils.createWrokFlowGroup(delegator, processId);
				String groupExecutiveId = WorkFlowUtils.createWrokFlowGroup(delegator, processId);
				String groupAccountantId = WorkFlowUtils.createWrokFlowGroup(delegator, processId);
				
				//add member to group
				String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
				String ceoId = PartyUtil.getCEO(delegator);
				List<String> chiefAccList = PartyUtil.getListMgrOfOrgByRoleType(delegator, "ACC_DEPARTMENT");
				WorkFlowUtils.addMemberToGroup(delegator, UtilMisc.toList(hrmAdmin), groupHrmAdminId);
				WorkFlowUtils.addMemberToGroup(delegator, UtilMisc.toList(ceoId), groupExecutiveId);
				WorkFlowUtils.addMemberToGroup(delegator, chiefAccList, groupAccountantId);
				
				//create process transition
				String transitionStartAppr = WorkFlowUtils.createTransition(delegator, processId, startState, approveState);
				String transitionStartDenied = WorkFlowUtils.createTransition(delegator, processId, startState, deniedState);
				String transitionDeniedStart = WorkFlowUtils.createTransition(delegator, processId, deniedState, startState);
				String transitionApprDenied = WorkFlowUtils.createTransition(delegator, processId, approveState, deniedState);
				String transitionApprComplete = WorkFlowUtils.createTransition(delegator, processId, approveState, completeState);
				
				//create action
				String actionApprHRM = WorkFlowUtils.createWorkFlowAction(delegator, processId, "APPROVE", null, "hrm approve"); 
				String actionDeniedHRM = WorkFlowUtils.createWorkFlowAction(delegator, processId, "DENY", null, "hrm denied");
				String actionApprAccountant = WorkFlowUtils.createWorkFlowAction(delegator, processId, "APPROVE", null, "accountant approve");
				String actionDenyAccountant = WorkFlowUtils.createWorkFlowAction(delegator, processId, "DENY", null, "accountant denied");
				String actionApprExecutive = WorkFlowUtils.createWorkFlowAction(delegator, processId, "APPROVE", null, "ceo approve");
				String actionDenyExecutive = WorkFlowUtils.createWorkFlowAction(delegator, processId, "DENY", null, "ceo denied");
				String actionRecalculate = WorkFlowUtils.createWorkFlowAction(delegator, processId, "RESTART", null, "recalculate");
				
				//create transition action
				WorkFlowUtils.createTransitionAction(delegator, transitionStartAppr, actionApprHRM);
				WorkFlowUtils.createTransitionAction(delegator, transitionStartDenied, actionDeniedHRM);
				WorkFlowUtils.createTransitionAction(delegator, transitionStartAppr, actionApprAccountant);
				WorkFlowUtils.createTransitionAction(delegator, transitionApprDenied, actionDenyAccountant);
				WorkFlowUtils.createTransitionAction(delegator, transitionApprDenied, actionDenyExecutive);
				WorkFlowUtils.createTransitionAction(delegator, transitionApprComplete, actionApprExecutive);
				WorkFlowUtils.createTransitionAction(delegator, transitionDeniedStart, actionRecalculate);
				
				//create action roleType
				WorkFlowUtils.createActionRoleType(delegator, actionApprHRM, null, groupHrmAdminId);
				WorkFlowUtils.createActionRoleType(delegator, actionDeniedHRM, null, groupHrmAdminId);
				WorkFlowUtils.createActionRoleType(delegator, actionApprExecutive, null, groupExecutiveId);
				WorkFlowUtils.createActionRoleType(delegator, actionDenyExecutive, null, groupExecutiveId);
				WorkFlowUtils.createActionRoleType(delegator, actionApprAccountant, null, groupAccountantId);
				WorkFlowUtils.createActionRoleType(delegator, actionDenyAccountant, null, groupAccountantId);
				
				//create activity
				String activitySendNotifyHrm = WorkFlowUtils.createWorkFlowActivity(delegator, processId, "SEND_NOTIFY", "send notify to hrm", "Approval payroll table");
				String activitySendNotifyChiefAcct = WorkFlowUtils.createWorkFlowActivity(delegator, processId, "SEND_NOTIFY", "send notify chief accountant", "Approval payroll table");
				String activitySendNotifyCEO = WorkFlowUtils.createWorkFlowActivity(delegator, processId, "SEND_NOTIFY", "send notify to ceo", "Approval payroll table");
				String activitySendNtfRecalcHRM = WorkFlowUtils.createWorkFlowActivity(delegator, processId, "SEND_NOTIFY", "send notify to recalculate", "recalculate payroll table");
				
				//create activity RoleType
				WorkFlowUtils.createActivityRoleType(delegator, activitySendNotifyHrm, null, groupHrmAdminId);
				WorkFlowUtils.createActivityRoleType(delegator, activitySendNotifyChiefAcct, null, groupAccountantId);
				WorkFlowUtils.createActivityRoleType(delegator, activitySendNotifyCEO, null, groupExecutiveId);
				WorkFlowUtils.createActivityRoleType(delegator, activitySendNtfRecalcHRM, null, groupHrmAdminId);
				
				//create stateActivity
				WorkFlowUtils.createStateActivity(delegator, activitySendNotifyHrm, startState);
				WorkFlowUtils.createStateActivity(delegator, activitySendNtfRecalcHRM, deniedState);
			
				//create action activity
				WorkFlowUtils.createActivityAction(delegator, activitySendNotifyChiefAcct, actionApprHRM);
				WorkFlowUtils.createActivityAction(delegator, activitySendNotifyCEO, actionApprAccountant);
				
				//add party to WorkFlowRequestStakeHolder
				Set<String> stakeHolder = FastSet.newInstance();
				stakeHolder.addAll(chiefAccList);
				stakeHolder.add(ceoId);
				stakeHolder.add(hrmAdmin);
				stakeHolder.add(userLogin.getString("partyId"));
				WorkFlowUtils.createWorkFlowRequestStakeHolder(delegator, requestId, stakeHolder);
				
				//create request action
				WorkFlowUtils.createRequestAction(delegator, requestId, actionApprHRM, transitionStartAppr, "Y", "N");
				WorkFlowUtils.createRequestAction(delegator, requestId, actionDeniedHRM, transitionStartDenied, "Y", "N");
				WorkFlowUtils.createRequestAction(delegator, requestId, actionApprAccountant, transitionStartAppr, "Y", "N");
				
				if(PartyUtil.isAdmin(delegator, userLogin)){
					//userLogin is HRMADMIN => actionApprHRM is kickoff
					Map<String, Object> approvalRequestMap = FastMap.newInstance();
					approvalRequestMap.put("requestId", requestId);
					approvalRequestMap.put("partyId", userLogin.getString("partyId"));
					approvalRequestMap.put("actionTypeId", "APPROVE");
					approvalRequestMap.put("userLogin", userLogin);
					dispatcher.runSync("approvalWorkFlowRequest", approvalRequestMap);
					PayrollUtil.updatePayrollTableStatus(delegator, payrollTableId, requestId);
				}else{
					dispatcher.runSync("updateWorkFlowRequest", UtilMisc.toMap("requestId", requestId, "processStatusId", startState, "userLogin", userLogin));
					payrollTableRecord.set("statusId", "PYRLL_WAIT_APPR");
					payrollTableRecord.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "SentRequestApproval", locale));
	}
	
	public static Map<String, Object> updateFormulaIncludedPayrollTable(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String codeSelected = (String)context.get("codeSelected");
		JSONArray codeSelectedJson = JSONArray.fromObject(codeSelected);
		List<String> codeList = FastList.newInstance();
		for(int i = 0; i < codeSelectedJson.size(); i++){
			String code = codeSelectedJson.getJSONObject(i).getString("code");
			codeList.add(code);
		}
		try {
			if(UtilValidate.isNotEmpty(codeList)){
				EntityCondition conditionIncluded = EntityCondition.makeCondition("code", EntityOperator.IN, codeList);
				List<GenericValue> codeIncluded = delegator.findList("PayrollFormula", conditionIncluded, null, null, null, false);
				EntityCondition conditionNotInclude = EntityCondition.makeCondition("code", EntityOperator.NOT_IN, codeList);
				List<GenericValue> codeNotIncluded = delegator.findList("PayrollFormula", conditionNotInclude, null, null, null, false);
				for(GenericValue formula: codeIncluded){
					formula.set("includedPayrollTable", "Y");
					formula.store();
				}
				for(GenericValue formula: codeNotIncluded){
					formula.set("includedPayrollTable", "N");
					formula.store();
				}
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	/*get day leave of partyid */
	public static Map<String, Object> getDayLeaveApproved(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		Map<String, Object> res = FastMap.newInstance();
		String monthStr = (String)context.get("month");
		String yearStr = (String)context.get("year");
		//TimeZone timeZone = (TimeZone)context.get("timeZone");
		//Locale locale = (Locale)context.get("locale");
		int month = Integer.parseInt(monthStr);
		int year = Integer.parseInt(yearStr);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		//Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		//Timestamp startMonth = UtilDateTime.getMonthStart(timestamp);
		//Timestamp endMonth = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
		try {
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("partyId", partyId));
			cond.add(EntityCondition.makeCondition("leaveStatus", "LEAVE_APPROVED"));
			List<GenericValue> data = delegator.findList(
					"EmplLeave",
					EntityCondition.makeCondition(cond, EntityOperator.AND), null,
					UtilMisc.toList("fromDate DESC"), null, true);
			res.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static Map<String, Object> getSalesCommissionData(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Delegator delegator = dctx.getDelegator();
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		if(partyGroupId == null || fromDateStr == null || thruDateStr == null){
			retMap.put("TotalRows", String.valueOf(totalRows));
			return retMap;
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "SALES_COMM_ACCEPTED"));
			List<GenericValue> salesCommissonData = delegator.findList("SalesCommissionData", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			totalRows = salesCommissonData.size();
			if(end > totalRows){
				end = totalRows;
			}
			salesCommissonData = salesCommissonData.subList(start, end);
			for(GenericValue data: salesCommissonData){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("salesCommissionId", data.getString("salesCommissionId"));
				String partyId = data.getString("partyId");
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				List<String> departmentName = FastList.newInstance();
				for(String departmentId: departmentList){
					GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", departmentId), false);
					departmentName.add(partyGroup.getString("groupName"));
				}
				tempMap.put("department", StringUtils.join(departmentName, ", "));
				List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				List<String> emplPositionType = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("emplPositionType", StringUtils.join(emplPositionType, ", "));
				tempMap.put("amount", data.get("amount"));
				tempMap.put("fromDate", data.getTimestamp("fromDate").getTime());
				tempMap.put("thruDate", data.getTimestamp("thruDate").getTime());
				tempMap.put("statusId", data.get("statusId"));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getSalesCommnissionAdj(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String salesCommissionId = (String)context.get("salesCommissionId");
		List<Map<String, Object>> salesCommissionDetail = FastList.newInstance();
		retMap.put("salesCommissionDetail", salesCommissionDetail);
		try {
			List<GenericValue> salesCommissionAdj = delegator.findByAnd("SalesCommissionAdjustment", UtilMisc.toMap("salesCommissionId", salesCommissionId), UtilMisc.toList("-amount"), false);
			for(GenericValue salesCommission: salesCommissionAdj){
				Map<String, Object> tempMap = FastMap.newInstance();
				salesCommissionDetail.add(tempMap);
				String salesPolicyId = salesCommission.getString("salesPolicyId");
				String salesPolicyRuleId = salesCommission.getString("salesPolicyRuleId");
				List<GenericValue> salesPolicyConds = delegator.findByAnd("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId), null, false);
				tempMap.put("salesPolicyId", salesPolicyId);
				tempMap.put("salesPolicyRuleId", salesPolicyRuleId);
				tempMap.put("salesCommnissionId", salesCommissionId);
				tempMap.put("salesPolicyActionSeqId", salesCommission.get("salesPolicyActionSeqId"));
				tempMap.put("amount", salesCommission.get("amount"));
				tempMap.put("description", salesCommission.get("description"));
				if(UtilValidate.isNotEmpty(salesPolicyConds)){
					GenericValue salesPolicyCond = salesPolicyConds.get(0);
					String inputParamEnumId = salesPolicyCond.getString("inputParamEnumId");
					tempMap.put("inputParamEnumId", inputParamEnumId);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getEmplListBonusInPeriod(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Delegator delegator = dctx.getDelegator();
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page + 1;
		//int end = start + size;
		if(partyGroupId == null || fromDateStr == null || thruDateStr == null){
			retMap.put("TotalRows", String.valueOf(totalRows));
			return retMap;
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		Organization buildOrg;
		DynamicViewEntity dynamicView = new DynamicViewEntity();
		dynamicView.addMemberEntity("EMPLPP", "PayrollEmplParameters");
		dynamicView.addAlias("EMPLPP", "partyId", null, null, null, true, null);
		dynamicView.addAlias("EMPLPP", "fromDate");
		dynamicView.addAlias("EMPLPP", "thruDate");
		dynamicView.addAlias("EMPLPP", "code");
		
		try {
			buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																EntityJoinOperator.OR, 
																EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			EntityCondition partyConds =  EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId);
			List<GenericValue> bonusParam = delegator.findByAnd("PayrollParameters", UtilMisc.toMap("paramCharacteristicId", "THUONG"), null, false);
			List<String> codeList = EntityUtil.getFieldListFromEntityList(bonusParam, "code", true);
			listAllConditions.add(EntityCondition.makeCondition("code", EntityJoinOperator.IN, codeList));
			EntityCondition commonConds = EntityCondition.makeCondition(listAllConditions);
			EntityListIterator emplParametersIterator = delegator.findListIteratorByCondition(dynamicView, EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, partyConds), null, UtilMisc.toSet("partyId"), null, opts);
			List<GenericValue> emplParameters = emplParametersIterator.getPartialList(start, size);
			totalRows = emplParametersIterator.getResultsSizeAfterPartialList();
			emplParametersIterator.close();
			
			for(GenericValue data: emplParameters){
				Map<String, Object> tempMap = FastMap.newInstance();				
				String partyId = data.getString("partyId");
				EntityCondition tempPartyCond = EntityCondition.makeCondition("partyId", partyId);
				List<GenericValue> payrollEmplParam = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, tempPartyCond), null, null, null, false);
				//total bonus amount of employee
				BigDecimal amount = PayrollWorker.getTotalAmountParamValue(payrollEmplParam, "value");
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				List<String> departmentName = FastList.newInstance();
				for(String departmentId: departmentList){
					GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", departmentId), false);
					departmentName.add(partyGroup.getString("groupName"));
				}
				List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				List<String> emplPositionType = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
				
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("department", StringUtils.join(departmentName, ", "));
				tempMap.put("emplPositionType", StringUtils.join(emplPositionType, ", "));
				//tempMap.put("code", data.get("code"));
				tempMap.put("value", amount);
				tempMap.put("fromDate", fromDate);
				tempMap.put("thruDate", thruDate);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getPayrolls(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("fromDate DESC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PayrollTableRecordAndCustomTimeAndParty", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}