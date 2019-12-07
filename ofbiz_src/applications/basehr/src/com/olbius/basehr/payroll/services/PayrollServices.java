package com.olbius.basehr.payroll.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.olbius.common.util.EntityMiscUtil;
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
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;
import com.olbius.basehr.payroll.PayrollEngine;
import com.olbius.basehr.payroll.worker.PayrollWorker;
import com.olbius.basehr.payroll.entity.EntityEmployeeSalary;
import com.olbius.basehr.workflow.WorkFlowUtils;

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
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	try {
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(!PayrollUtil.isPayrolTableEditable(userLogin, delegator, payrollTableId)){
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdatePayrollTableHaveCalculated", 
						UtilMisc.toMap("status", status.get("description")), locale));
			}
			Timestamp fromDate = payrollTableRecord.getTimestamp("fromDate");
			Timestamp thruDate = payrollTableRecord.getTimestamp("thruDate");

			String timekeepingSummaryId = PayrollWorker.getEmplTimesheetByPayrollTable(delegator, payrollTableRecord);
//			String departmentId = payrollTableRecord.getString("partyId");
			if(timekeepingSummaryId == null){
				String fromDateDes = DateUtil.getDateMonthYearDesc(fromDate);
				String thruDateDes = DateUtil.getDateMonthYearDesc(thruDate);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotCalcPayrollTableEmplTimesheetNotCreate",
						UtilMisc.toMap("fromDate", fromDateDes, "thruDate", thruDateDes), locale));
			}
			//List<GenericValue> formulaList = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			
			//List<String> formulaListStr = EntityUtil.getFieldListFromEntityList(formulaList, "code", true);
			Map<String, Object> ctxMap = FastMap.newInstance();
			//ctxMap.put("formulaList", formulaListStr);
//			ctxMap.put("departmentId", departmentId);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			ctxMap.put("periodTypeId", "MONTHLY");
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	Map<String, Object> retMap = FastMap.newInstance();
    	retMap.put("payrollTableId", payrollTableId);
    	retMap.put("listEmplSalaryInPeriod", listResult);
    	retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "CaclculateSuccessful", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(statusId == null || statusId.equals("PYRLL_TABLE_CREATED")){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "PayrollTableNotCalc", locale));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			EntityCondition commonConds = EntityCondition.makeCondition(EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList),
																		EntityJoinOperator.AND,
																		EntityCondition.makeCondition("code", EntityJoinOperator.NOT_IN, UtilMisc.toList("TI_LE_HUONG_LUONG", "TI_LE_TRO_CAP")));
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
					List<GenericValue> employeeList = buildOrg.getDirectEmployee(delegator, fromDateTs, thruDateTs);
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
				String emplTimesheetId = PayrollWorker.getEmplTimesheetByPayrollTable(delegator, payrollTableRecord);
				Map<String, Object> tempMap = PayrollWorker.getPayrollTableRecordOfPartyInfo(dctx, payrollTableId, emplTimesheetId, tempPartyId, partyParentId, 
						locale, timeZone, fromDateTs, thruDateTs, listAllFormula);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(statusId.equals("PYRLL_TABLE_INVOICED")){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotDeletedPayrollTableHaveInvoiced", locale));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
    }
    
    public static Map<String, Object> updatePayrollTableRecord(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String newStatusId = (String)context.get("statusId");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue  payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(UtilValidate.isEmpty(payrollTableRecord)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundPayrollTable", locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if(!PayrollUtil.isPayrolTableEditable(userLogin, delegator, payrollTableId)){
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotUpdatePayrollTableHaveCalculated", 
						UtilMisc.toMap("status", status.get("description")), locale));
			}
			payrollTableRecord.setNonPKFields(context);
			payrollTableRecord.store();
			if(newStatusId != null && !statusId.equals(newStatusId)){
				Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPayrollTableRecordStatus", context, userLogin, timeZone, locale);
				Map<String, Object> resultService = dispatcher.runSync("createPayrollTableRecordStatus", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
    }
    
    public static Map<String, Object> createPayrollTableRecordStatus(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue payrollTableRecordStatus = delegator.makeValue("PayrollTableRecordStatus");
		payrollTableRecordStatus.setNonPKFields(context);
		payrollTableRecordStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
		payrollTableRecordStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
		String payrollTableRecordStatusId = delegator.getNextSeqId("PayrollTableRecordStatus");
		payrollTableRecordStatus.put("payrollTableRecordStatusId", payrollTableRecordStatusId);
		try {
			delegator.create(payrollTableRecordStatus);
			retMap.put("payrollTableRecordStatusId", payrollTableRecordStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
    }
    
    public static Map<String, Object> calcSalaryInPeriod(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	Locale locale = (Locale)context.get("locale");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	
		Map<String, Object> resultService = FastMap.newInstance();
		List<Map<String,Object>> listResult = FastList.newInstance();
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getSalaryAmountList", context, userLogin, timeZone, locale);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			resultService = dispatcher.runSync("getSalaryAmountList", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			listResult.add(resultService);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
    	Timestamp tThruDate = (Timestamp)context.get("thruDate");
    	Timestamp tFromDate = (Timestamp)context.get("fromDate");
    	String periodTypeId = (String)context.get("periodTypeId");
    	String payrollTableId = (String)context.get("payrollTableId");
    	//String strEmployeeId = (String)context.get("pdfPartyId");
    	Locale locale = (Locale) context.get("locale");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	try {
    		EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("payrollCharacteristicId", EntityJoinOperator.NOT_EQUAL, null),
    															  EntityJoinOperator.OR,
    															  EntityCondition.makeCondition("includedPayrollTable", "Y"));
    		List<GenericValue> listFormula = delegator.findList("PayrollFormula", conds, null, null, null, false);
    		if(UtilValidate.isEmpty(listFormula)){
    			return ServiceUtil.returnError("cannot find formula to calculate");
    		}
    		List<String> listInputFormulas = EntityUtil.getFieldListFromEntityList(listFormula, "code", true);
        	//listInputFormulas.addAll(formulaRelatedList);
        	List<GenericValue> emplList = delegator.findByAnd("PayrollTableRecordParty", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
        	GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
        	String partyGroupId = payrollTableRecord.getString("orgId");
        	String orgId = PartyUtil.getSubsidiaryOfPartyGroup(delegator, partyGroupId);
        	String timekeepingSummaryId = PayrollWorker.getEmplTimesheetByPayrollTable(delegator, payrollTableRecord);
    		listEntitySalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, orgId, timekeepingSummaryId, periodTypeId, emplList, listInputFormulas, tFromDate, tThruDate, locale, timeZone);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put("salaryAmountList", listEntitySalaryAmount); 
			result.put("formulaList", listInputFormulas);
			result.put("fromDate", tFromDate);
			result.put("thruDate", tThruDate);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "generalError", new Object[] { e.getMessage() }, locale));
		}
    	return result;
    }
    
    
    /*
     * Description: raise invoice and payment
     * */    
   /* public static Map<String, Object> PayrollInvoiceAndPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	List<String> listInputs = (List<String>)context.get("formulaCode");
    	Map<String, Object> result = FastMap.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> invoiceMap;
    	String emplTimesheetId = (String)context.get("emplTimesheetId");
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
    			listSalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, emplTimesheetId, periodTypeId, null, UtilMisc.toList("LUONG"), tsFromDate, tsThruDate, locale, timeZone);
    		}else{
    			listSalaryAmount = PayrollEngine.getSalaryList(ctx, userLogin, emplTimesheetId, periodTypeId, null, UtilMisc.toList("LUONG"), tsFromDate, tsThruDate, locale, timeZone);
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
	    		        	EntitySalaryAmount ettTMPResult = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, value,tsFromDate,tsThruDate,strEmployeeId,PeriodWorker.getParameterByPeriod(ctx, periodTypeId, emplTimesheetId, PayrollDataPreparation.getEmployeeParametersCache(ctx, userLogin, emplTimesheetId, strEmployeeId, tsFromDate, tsThruDate, timeZone), tsFromDate, tsThruDate, timeZone, locale));
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
	    		} catch (Exception e) {
	    			Debug.logError(e, module);
	    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
	    		}
	    	}
    	}
		return result;
    }*/
   
    
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundPayrollTable", locale));
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
					List<String> partyGroupList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
					if(partyGroupList != null){
						tempMap.put("partyGroupId", partyGroupList);
						List<String> partyGroupListName = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", partyGroupList, "partyId", "groupName");
						tempMap.put("partyGroupName", StringUtils.join(partyGroupListName, ", "));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
    	List<String> formulaList = (List<String>)context.get("formulaList");
    	List<EntityEmployeeSalary> listEntitySalaryAmount = (List<EntityEmployeeSalary>)context.get("salaryAmountList");
    	/*Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");*/
    	String payrollTableId = (String)context.get("payrollTableId");
    	Locale locale = (Locale) context.get("locale");
    	
    	//List payroll for all Employee in a payroll period
    	for(int i = 0; i < listEntitySalaryAmount.size(); i++){
    		for(int j = 0; j < formulaList.size(); j++){
    			String valueTmp = listEntitySalaryAmount.get(i).getListSalaryAmount().get(j).getAmount();
    			BigDecimal amount = new BigDecimal(valueTmp);
				GenericValue payrollTable = delegator.makeValue("PayrollTableRecordPartyAmount");
				payrollTable.set("payrollTableId", payrollTableId);
				payrollTable.set("partyId", listEntitySalaryAmount.get(i).getListSalaryAmount().get(0).getPartyId());
				payrollTable.set("code", formulaList.get(j));
				payrollTable.set("amount", amount.setScale(0, RoundingMode.HALF_UP));
				payrollTable.set("statusId", "PYRLL_TABLE_CALC");
				try {
					delegator.createOrStore(payrollTable);
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage(), module);
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
				}
    		}
    	}
    	try {
			PayrollWorker.updateAcutalSalReceive(delegator, payrollTableId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "EmplPosTypeRateIsSet", locale));
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
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		Long month = (Long)context.get("month");
		Long year = (Long)context.get("year");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("month", month));
			conds.add(EntityCondition.makeCondition("year", year));
			conds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "PYRLL_TABLE_CANCEL"));
			List<GenericValue> payrollExistsList = delegator.findList("PayrollTableRecord", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(payrollExistsList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "PayrollTableIsExists", 
						UtilMisc.toMap("month", String.valueOf(month + 1), "year", String.valueOf(year)), locale));
			}
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, month.intValue());
			cal.set(Calendar.YEAR, year.intValue());
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
			Timestamp startMonth = UtilDateTime.getMonthStart(timestamp);
			Timestamp endMonth = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
			GenericValue payrollTableRecord = delegator.makeValue("PayrollTableRecord");
			payrollTableRecord.setNonPKFields(context);		
			payrollTableRecord.set("statusId", "PYRLL_TABLE_CREATED");
			String payrollTableId = delegator.getNextSeqId("PayrollTableRecord");
			payrollTableRecord.put("payrollTableId", payrollTableId);
			payrollTableRecord.put("fromDate", startMonth);
			payrollTableRecord.put("thruDate", endMonth);
			payrollTableRecord.put("orgId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
			payrollTableRecord.create();
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, startMonth, endMonth);
			if(emplList != null){
				for(GenericValue empl: emplList){
					String partyId = empl.getString("partyId");
					List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, startMonth, endMonth);
					String partyGroupId = UtilValidate.isNotEmpty(departmentList)? departmentList.get(0): null;
					PayrollWorker.createPayrollTableRecordParty(delegator, payrollTableId, partyId, partyGroupId, "PYRLL_TABLE_CREATED");
				}
			}
			retMap.put("payrollTableId", payrollTableId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotFindPayrollTableIdToProposal", 
						UtilMisc.toMap("payrollTableId", payrollTableId), locale));
			}
			String statusId = payrollTableRecord.getString("statusId");
			if("PYRLL_TABLE_CREATED".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotProposalPayrollTableNotCalc", locale));
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
				List<String> chiefAccList = PartyUtil.getListMgrOfOrgByRoleType(delegator, "ACC_DEPARTMENT", userLogin.getString("userLoginId"));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SentRequestApproval", locale));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getTempPayrollTableList(DispatchContext ctx,
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListEmplSalaryItemPayroll(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String partyGroupId = request.getParameter("partyGroupId");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(customTimePeriod != null){
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDate.getTime()));
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				boolean isManagePartyGroup = PartyUtil.checkPartyManageOrg(delegator, userLogin.getString("userLoginId"), partyGroupId, fromDateTs, thruDateTs);
				if(!isManagePartyGroup){
					return ServiceUtil.returnError("You don't manage organization " + PartyHelper.getPartyName(delegator, partyGroupId, false));
				}
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
				emplList = EntityUtil.orderBy(emplList, listSortFields);
				EntityCondition commonCond = EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId);
				List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
				EntityCondition paySalaryItemConds = EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, emplListId);
				List<GenericValue> paySalaryHisList = delegator.findList("PaySalaryHistoryGroup", EntityCondition.makeCondition(commonCond, EntityJoinOperator.AND, paySalaryItemConds), null, UtilMisc.toList("partyIdTo"), null, false);
				List<String> listFieldInEntity = FastList.newInstance();
				listFieldInEntity.add("partyIdTo");
				
				List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
				List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
				EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
				
				List<String> sortedFieldInEntity = FastList.newInstance();
				List<String> sortedFieldNotInEntity = FastList.newInstance();
				if(listSortFields != null){
					EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
				}
				
				if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
					paySalaryHisList = EntityConditionUtils.doFilterGenericValue(paySalaryHisList, condsForFieldInEntity);
				}
				if(UtilValidate.isEmpty(sortedFieldInEntity)){
					sortedFieldInEntity.add("partyIdTo");
				}
				paySalaryHisList = EntityUtil.orderBy(paySalaryHisList, sortedFieldInEntity);
				
				boolean isFilterAdvance = false;
				if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
					totalRows = paySalaryHisList.size();
					if(end > paySalaryHisList.size()){
						end = paySalaryHisList.size();
					}
					paySalaryHisList = paySalaryHisList.subList(start, end);
				}else{
					isFilterAdvance = true;
				}
				if(end > paySalaryHisList.size()){
					end  = paySalaryHisList.size();
				}
//				totalRows = paySalaryHisList.size();
//				if(end > totalRows){
//					end = totalRows;
//				}
//				paySalaryHisList = paySalaryHisList.subList(start, end);
				for(GenericValue paySalaryHis: paySalaryHisList){
					Map<String, Object> tempMap = FastMap.newInstance();
					BigDecimal totalIncome = BigDecimal.ZERO;
					BigDecimal totalDeduction = BigDecimal.ZERO;
					String partyId = paySalaryHis.getString("partyIdTo");
					totalIncome = PayrollWorker.getTotalAmountPaySal(delegator, partyId, customTimePeriodId, "INCOME");
					totalDeduction = PayrollWorker.getTotalAmountPaySal(delegator, partyId, customTimePeriodId, "DEDUCTION");
					List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDateTs, thruDateTs);
					List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
					List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDateTs, thruDateTs);
					List<String> invoiceIdList = PayrollWorker.getListInvoiceSalOfEmpl(delegator, partyId, customTimePeriodId);
					tempMap.put("emplPositionType", StringUtils.join(emplPositionTypeList, ", "));
					if(departmentList.size() > 1){
						tempMap.put("highlight", true);
					}else{
						tempMap.put("highlight", false);
					}
					List<String> departmentName = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId", "groupName");
					tempMap.put("currDept", StringUtils.join(departmentName, ", "));
					tempMap.put("partyIdTo", partyId);
					tempMap.put("customTimePeriodId", customTimePeriodId);
					tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
					tempMap.put("fromDate", fromDateTs);
					tempMap.put("thruDate", thruDateTs);
					tempMap.put("totalIncome", totalIncome);
					tempMap.put("totalDeduction", totalDeduction);
					tempMap.put("invoiceIds", invoiceIdList);
					tempMap.put("salaryActual", totalIncome.subtract(totalDeduction));
					listIterator.add(tempMap);
				}
				if(isFilterAdvance){
					if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
						listIterator = EntityConditionUtils.doFilter(listIterator, condsForFieldNotInEntity);
					}
					if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
						listIterator = EntityConditionUtils.sortList(listIterator, sortedFieldNotInEntity);
					}
					totalRows = listIterator.size();
					if(end > listIterator.size()){
						end = listIterator.size();
					}
					listIterator = listIterator.subList(start, end);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> updatePaySalaryItemFromPayrollTable(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String payrollTableId = (String)context.get("payrollTableId");
		String overrideData = (String)context.get("overrideData");
		if(overrideData == null){
			overrideData = "Y";
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(payrollTableRecord == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotFindPayrollTableId", UtilMisc.toMap("payrollTableId", payrollTableId), locale));
			}
			String partyId = payrollTableRecord.getString("partyId");
			String orgId = PartyUtil.getSubsidiaryOfPartyGroup(delegator, partyId);
			String customTimePeriodId = payrollTableRecord.getString("customTimePeriodId");
			List<EntityCondition> conds = FastList.newInstance();
			List<String> payrollCharacteristicList = UtilMisc.toList("INCOME", "DEDUCTION", "ORG_PAID");
			conds.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
			conds.add(EntityCondition.makeCondition("payrollCharacteristicId", EntityJoinOperator.IN, payrollCharacteristicList));
			List<GenericValue> payrollTableList = delegator.findList("PayrollTableAndFormulaAndChar", EntityCondition.makeCondition(conds), null, null, null, false);
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
					
			for(GenericValue payrollTable: payrollTableList){
				String emplId = payrollTable.getString("partyId");
				String code = payrollTable.getString("code");
				//String payrollCharacteristicId = payrollTable.getString("payrollCharacteristicId");
				List<String> deptList = PartyUtil.getDepartmentOfEmployee(delegator, emplId, fromDateTs, thruDateTs);
				String deptMapInvoicePayrollFormula = "";
				for(String deptId: deptList){
					if(deptId.equals(partyId) || PartyUtil.checkAncestorOfParty(delegator, partyId, deptId)){
						deptMapInvoicePayrollFormula = deptId;
						break;
					}
				}
				String invoiceItemTypeId = PayrollWorker.getInvoiceItemTypeByPartyAndCode(delegator, code, deptMapInvoicePayrollFormula, customTimePeriodId, userLogin.getString("userLoginId"));
				BigDecimal amount = payrollTable.getBigDecimal("value");
				Map<String, Object> primKeys = FastMap.newInstance();				
				primKeys.put("partyIdFrom", orgId);
				primKeys.put("partyIdTo", emplId);
				primKeys.put("customTimePeriodId", customTimePeriodId);
				GenericValue paySalaryHistory = delegator.findOne("PaySalaryHistory", primKeys, false);
				if(paySalaryHistory == null){
					paySalaryHistory = delegator.makeValue("PaySalaryHistory");
					paySalaryHistory.setAllFields(primKeys, false, null, null);
					paySalaryHistory.create();
				}
				primKeys.put("salaryItem", code);
				GenericValue paySalaryItemHistory = delegator.findOne("PaySalaryItemHistory", primKeys, false);
				if(amount != null && amount.compareTo(BigDecimal.ZERO) != 0){
					if(paySalaryItemHistory != null && "Y".equals(overrideData)){
						paySalaryItemHistory.set("amount", amount);
						paySalaryItemHistory.set("invoiceItemTypeId", invoiceItemTypeId);
						paySalaryItemHistory.store();
					}else if(paySalaryItemHistory == null){
						paySalaryItemHistory = delegator.makeValue("PaySalaryItemHistory");
						paySalaryItemHistory.setAllFields(primKeys, false, null, null);
						paySalaryItemHistory.set("amount", amount);
						paySalaryItemHistory.set("invoiceItemTypeId", invoiceItemTypeId);
						paySalaryItemHistory.create();
					}
				}else if(paySalaryItemHistory != null){
					paySalaryItemHistory.remove();
				}
				
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> getListEmplSalaryItemDetail(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		//int totalRows = 0;
		Delegator delegator = dctx.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyId = request.getParameter("partyId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String payrollCharacteristicId = request.getParameter("payrollCharacteristicId");
		listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
		listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", payrollCharacteristicId));
		listSortFields.add("salaryItem");
		try {
			listIterator = delegator.find("PaySalaryItemHistoryAndFormulaAndPG", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		//successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listIterator);
		return successResult; 
	}
	
	public static Map<String, Object> getListOrgPaidItemDetail(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String orgId = request.getParameter("orgId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String partyIdTo = request.getParameter("partyIdTo");
		try {
			List<GenericValue> listSub = PartyUtil.getListSubsidiaryOfParty(delegator, orgId, customTimePeriodId);
			List<String> listPartyIdSub = EntityUtil.getFieldListFromEntityList(listSub, "partyIdTo", true);
			listPartyIdSub.add(orgId);
			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, listPartyIdSub));
			listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", EntityJoinOperator.IN, UtilMisc.toList("INCOME", "ORG_PAID")));
			listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
			listIterator = delegator.find("PaySalaryItemHistoryAndFormula", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListInvoiceItemSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyId = request.getParameter("partyId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
		listAllConditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.NOT_EQUAL, null));
		try {
			listIterator = delegator.find("PaySalaryItemHisFormulaInvoiceItemPG", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> updatePaySalaryItemHistory(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyIdFrom = (String)context.get("partyIdFrom");
		String partyIdTo = (String)context.get("partyIdTo");
		String salaryItem = (String)context.get("salaryItem");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		try {
			GenericValue paySalaryItemHistory = delegator.findOne("PaySalaryItemHistory", 
					UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "salaryItem", salaryItem, "customTimePeriodId", customTimePeriodId), false);
			if(paySalaryItemHistory != null){
				paySalaryItemHistory.setNonPKFields(context);
				paySalaryItemHistory.store();
			}else{
				return ServiceUtil.returnError("cannot find record to update");
			}
				
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	/** ===== add in 15/06/2016 ====== **/
	public static Map<String, Object> getListPayrollTableRecordJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-fromDate");
		}
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			String partyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("orgId", partyId));

			EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			//listIterator = delegator.find("PayrollTableRecordAndSum", null, tmpCond, null, listSortFields, opts);
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PayrollTableRecordAndSum", EntityCondition.makeCondition(tmpCond), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> getPayrollTableRecordPartyNotInvoiceJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String payrollTableId = parameters.get("payrollTableId") != null? ((String[])parameters.get("payrollTableId"))[0] : null;
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("firstName");
		}
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "PYRLL_TABLE_INVOICED"));
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PayrollTableRecordPartyAndTotalReceipt", tmpCond, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> getPayrollTableRecordPartyInvoiceJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String payrollTableId = parameters.get("payrollTableId") != null? ((String[])parameters.get("payrollTableId"))[0] : null;
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("firstName");
		}
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
		listAllConditions.add(EntityCondition.makeCondition("statusId", "PYRLL_TABLE_INVOICED"));
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PayrollTableRecordPartyAndTotalReceipt", tmpCond, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> createPayrollTableRecordPartyAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue payrollTableRecordPartyAmount = delegator.makeValue("PayrollTableRecordPartyAmount");
		payrollTableRecordPartyAmount.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(payrollTableRecordPartyAmount);
			GenericValue payrollTableRecordParty = delegator.findOne("PayrollTableRecordParty", 
					UtilMisc.toMap("partyId", context.get("partyId"), "payrollTableId", context.get("payrollTableId")), false);
			PayrollWorker.updateAcutalSalReceiveParty(delegator, payrollTableRecordParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> deletePayrollTableRecordPartyAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue payrollTableRecordPartyAmount = delegator.makeValue("PayrollTableRecordPartyAmount");
		payrollTableRecordPartyAmount.setPKFields(context);
		payrollTableRecordPartyAmount.set("amount", BigDecimal.ZERO);
		try {
			payrollTableRecordPartyAmount.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> getPayrollTableRecordPartyDetailJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyCode = parameters.get("partyCode") != null? ((String[])parameters.get("partyCode"))[0] : null;
		String payrollCharacteristicId = parameters.get("payrollCharacteristicId") != null? ((String[])parameters.get("payrollCharacteristicId"))[0] : null;
		String payrollTableId = parameters.get("payrollTableId") != null? ((String[])parameters.get("payrollTableId"))[0] : null;
		if(partyCode != null && payrollCharacteristicId != null && payrollTableId != null){
			List<GenericValue> party;
			try {
				party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
				if(UtilValidate.isEmpty(party)){
					return ServiceUtil.returnError("cannot find employee have code: " + partyCode);
				}
				String partyId = party.get(0).getString("partyId");
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
				listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", payrollCharacteristicId));
				listAllConditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
				listAllConditions.add(EntityCondition.makeCondition("amount", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
				if(UtilValidate.isEmpty(listSortFields)){
					listSortFields.add("formulaName");
				}
				listIterator = delegator.find("PayrollTableRecordPartyAmountAndFormula", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				successResult.put("listIterator", listIterator);
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return successResult;
	}
	public static Map<String, Object> getListPayrollTableRecordPartyJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String payrollTableId = parameters.get("payrollTableId") != null? ((String[])parameters.get("payrollTableId"))[0]: null;
		if(payrollTableId != null){
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
			try {
				List<GenericValue> payrollTableRecordPartyList = delegator.findList("PayrollTableRecordPartyAmountAndSal", 
						EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
				totalRows = payrollTableRecordPartyList.size();
				if(end > totalRows){
					end = totalRows;
				}
				List<GenericValue> payrollItemTypeList = delegator.findByAnd("PayrollItemType", UtilMisc.toMap("parentTypeId", null), null, true);
				List<GenericValue> orgPaidFormulaList = delegator.findByAnd("PayrollFormulaAndCharAndPyllItemType", UtilMisc.toMap("payrollCharacteristicId", "ORG_PAID"), null, true);
				List<GenericValue> deductionFormulaList = delegator.findByAnd("PayrollFormulaAndCharAndPyllItemType", UtilMisc.toMap("payrollCharacteristicId", "DEDUCTION"), null, true);
				List<String> formulaCodeList = FastList.newInstance();
				List<String> payrollItemTypeIdList = FastList.newInstance();
				for(GenericValue payrollItemType: payrollItemTypeList){
					String payrollItemTypeId = payrollItemType.getString("payrollItemTypeId");
					List<GenericValue> payrollFormulaList = delegator.findByAnd("PayrollFormula", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId), null, true);
					if("OTHER_INCOME".equals(payrollItemTypeId) || payrollFormulaList.size() < 2){
						payrollItemTypeIdList.add(payrollItemTypeId);
					}else{
						for(GenericValue payrollFormula: payrollFormulaList){
							formulaCodeList.add(payrollFormula.getString("code"));
						}
					}
				}
				
				for(GenericValue payrollTableRecordParty: payrollTableRecordPartyList){
					Map<String, Object> tempMap = payrollTableRecordParty.getAllFields();
					for(String formulaCode: formulaCodeList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, formulaCode);
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(formulaCode, amount);
						}
					}
					for(String payrollItemTypeId: payrollItemTypeIdList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByPrllItemType(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, payrollItemTypeId);
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(payrollItemTypeId, amount);
						}
					}
					for(GenericValue orgPaidFormula: orgPaidFormulaList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, orgPaidFormula.getString("code"));
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(orgPaidFormula.getString("code"), amount);
						}
					}
					for(GenericValue deductionFormula: deductionFormulaList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, deductionFormula.getString("code"));
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(deductionFormula.getString("code"), amount);
						}
					}
					listIterator.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> getListPayrollTableRecordPartyJQFast(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String payrollTableId = parameters.get("payrollTableId") != null? ((String[])parameters.get("payrollTableId"))[0]: null;
		if(payrollTableId != null){
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
			try {
				List<GenericValue> payrollTableRecordPartyList = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PayrollTableRecordPartyAmountAndSal",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				totalRows = payrollTableRecordPartyList.size();
				if(end > totalRows){
					end = totalRows;
				}
				List<GenericValue> payrollItemTypeList = delegator.findByAnd("PayrollItemType", UtilMisc.toMap("parentTypeId", null), null, true);
				List<GenericValue> orgPaidFormulaList = delegator.findByAnd("PayrollFormulaAndCharAndPyllItemType", UtilMisc.toMap("payrollCharacteristicId", "ORG_PAID"), null, true);
				List<GenericValue> deductionFormulaList = delegator.findByAnd("PayrollFormulaAndCharAndPyllItemType", UtilMisc.toMap("payrollCharacteristicId", "DEDUCTION"), null, true);
				List<String> formulaCodeList = FastList.newInstance();
				List<String> payrollItemTypeIdList = FastList.newInstance();
				for(GenericValue payrollItemType: payrollItemTypeList){
					String payrollItemTypeId = payrollItemType.getString("payrollItemTypeId");
					List<GenericValue> payrollFormulaList = delegator.findByAnd("PayrollFormula", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId), null, true);
					if("OTHER_INCOME".equals(payrollItemTypeId) || payrollFormulaList.size() < 2){
						payrollItemTypeIdList.add(payrollItemTypeId);
					}else{
						for(GenericValue payrollFormula: payrollFormulaList){
							formulaCodeList.add(payrollFormula.getString("code"));
						}
					}
				}

				for(GenericValue payrollTableRecordParty: payrollTableRecordPartyList){
					Map<String, Object> tempMap = payrollTableRecordParty.getAllFields();
					for(String formulaCode: formulaCodeList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, formulaCode);
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(formulaCode, amount);
						}
					}
					for(String payrollItemTypeId: payrollItemTypeIdList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByPrllItemType(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, payrollItemTypeId);
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(payrollItemTypeId, amount);
						}
					}
					for(GenericValue orgPaidFormula: orgPaidFormulaList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, orgPaidFormula.getString("code"));
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(orgPaidFormula.getString("code"), amount);
						}
					}
					for(GenericValue deductionFormula: deductionFormulaList){
						BigDecimal amount = PayrollWorker.getPrllTableRecordePartyAmountByCode(delegator, payrollTableRecordParty.getString("partyId"), payrollTableId, deductionFormula.getString("code"));
						if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
							tempMap.put(deductionFormula.getString("code"), amount);
						}
					}
					listIterator.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> sendReqApprPayrollTable(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String payrollTableId = (String)context.get("payrollTableId");
		try {
			Map<String, Object> resultService = dispatcher.runSync("updatePayrollTableRecord", 
					UtilMisc.toMap("userLogin", userLogin, "timeZone", timeZone, "locale", locale, "payrollTableId", payrollTableId, "statusId", "PYRLL_WAIT_APPR"));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SendRequestApprSuccessfully", locale));
	}
	public static Map<String, Object> approvalPayrollTable(DispatchContext dctx, Map<String, Object> context){
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String approvalType = (String)context.get("approvalType");
		String payrollTableId = (String)context.get("payrollTableId");
		try {
			Boolean isApproval = PayrollUtil.isPayrolTableEditable(userLogin, delegator, payrollTableId);
			if(!isApproval){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "YouCannotApprovalPayrollTable", locale));
			}
			if(approvalType == null){
				approvalType = PropertiesUtil.APPR_ACCEPT;
			}
			String newStatusId = null;
			switch (approvalType) {
			case PropertiesUtil.APPR_ACCEPT:
				newStatusId = "PYRLL_TABLE_ACCEPT";
				break;
				
			case PropertiesUtil.APPR_REJECT:
				newStatusId = "PYRLL_TABLE_REJECT";
				break;
			default:
				break;
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "updatePayrollTableRecord", context, userLogin, timeZone, locale);
			ctxMap.put("statusId", newStatusId);
			Map<String, Object> resultService = dispatcher.runSync("updatePayrollTableRecord", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
    }
	public static Map<String, Object> roundingAmountPayrollTableRecord(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Integer roundingNumber = (Integer)context.get("roundingNumber");
		String payrollTableId = (String)context.get("payrollTableId");
		try {
			GenericValue payrollTable = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
			if(payrollTable == null){
				return ServiceUtil.returnError("Cannot find payroll table");
			}
			String statusId = payrollTable.getString("statusId");
			if(!(statusId.equals("PYRLL_TABLE_CREATED") || statusId.equals("PYRLL_TABLE_CALC") || statusId.equals("PYRLL_TABLE_REJECT"))){
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotRoundingBecauseStatus", UtilMisc.toMap("status", status.get("description")), locale));
			}
			List<GenericValue> payrollTablePartyList = delegator.findByAnd("PayrollTableRecordParty", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
			for(GenericValue payrollTableParty: payrollTablePartyList){
				BigDecimal amount = payrollTableParty.getBigDecimal("actualSalReceive");
				if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal tempAmount = CommonUtil.roundingNumber(amount, roundingNumber, false);
					payrollTableParty.set("actualSalReceive", tempAmount);
					payrollTableParty.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updatePayrollTableRecordParty(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String payrollTableId = (String)context.get("payrollTableId");
		String partyCode = (String)context.get("partyCode");
		String partyId = (String)context.get("partyId");
		try {
			if(partyId == null){
				if(partyCode == null){
					return ServiceUtil.returnError("Cannot find employee");
				}
				List<GenericValue> party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
				if(UtilValidate.isEmpty(party)){
					return ServiceUtil.returnError("Cannot find party to update");
				}
				partyId = party.get(0).getString("partyId");
			}
			GenericValue payrollTableRecordParty = delegator.findOne("PayrollTableRecordParty", UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId), false);
			if(payrollTableRecordParty == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindEmployeeInPayrollToUpdate", locale));
			}
			String statusId = payrollTableRecordParty.getString("statusId");
			if("PYRLL_TABLE_INVOICED".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdateEmplHaveCreateInvoiced", locale));
			}
			payrollTableRecordParty.setNonPKFields(context);
			payrollTableRecordParty.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
}