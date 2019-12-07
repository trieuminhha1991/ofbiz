package com.olbius.payroll;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.util.TimekeepingUtils;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class PayrollUtil {
	
	public static Map<String, String> specialChar = FastMap.newInstance();	
	static{
		specialChar.put("lt", "<");
		specialChar.put("gt", ">");
		specialChar.put("AND", "&&");
		specialChar.put("OR", "||");
		specialChar.put("=", "==");
	}
	
	/* Input: input string to calculate
	 * Output: value after process.
	 * Description: calculate string value by using Javascript engine
	 */
	// TODO thought Exception
	public static String evaluateStringExpression(String strFunction) throws ScriptException{
		return evaluateStringExpression(strFunction,true);
	}
	/* Input: input string to calculate, boolean value to use round or not
	 * Output: value after process.
	 * Description: calculate string value by using Javascript engine
	 */
	public static String evaluateStringExpression(String strFunction, Boolean useRound) throws ScriptException{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");    
		String str = "";
		try{ // use try catch for debugging
			String strParseString = null;
			if(useRound){
				strParseString = "parseFloat(" + evaluateStringExpression(strFunction,false) + ").toFixed(3)";
			}else{
				strParseString = strFunction;
			}
			str = engine.eval(strParseString).toString();
		}catch(Exception ex){
			System.out.print(ex);
		}
		return str;
	}
	
	public static String evaluateFunctionExpression(String functionExpr, ScriptContext context){
		ScriptEngineManager manager = new ScriptEngineManager();		
		ScriptEngine engine = manager.getEngineByName("groovy");
		String str = "";
		String function = escapeSpecialChar(functionExpr);
		try {
			str = engine.eval(function, context).toString();
		} catch (ScriptException e) {
			// 
			e.printStackTrace();
		}
		return str;
	}
	
	private static String escapeSpecialChar(String functionExpr) {
		
		for(Map.Entry<String, String> entry: specialChar.entrySet()){
			functionExpr = functionExpr.replace(entry.getKey(), entry.getValue());
		}
		return functionExpr;
	}
	/*
	 *
	 * Description: make (greater than and equal) or (less than and equal) condition
	 * */
	public static EntityCondition makeGreaterOrLessTEcondition(String strField, Object value,EntityComparisonOperator<?,?> operator){
		// check for other condition
		if(operator != EntityOperator.GREATER_THAN_EQUAL_TO && operator != EntityOperator.LESS_THAN_EQUAL_TO){
			return null;
		}
		EntityExpr expr1 = EntityCondition.makeCondition(strField, operator, value);
		EntityExpr expr2 = EntityCondition.makeCondition(strField, EntityOperator.EQUALS, null);
		return EntityCondition.makeCondition(UtilMisc.toList(expr1,expr2),EntityJoinOperator.OR);
	}
	/*
	 *
	 * Description: make (greater than and equal) condition
	 * */
	public static EntityCondition makeGTEcondition(String strField, Object value){
		return makeGreaterOrLessTEcondition(strField,value,EntityOperator.GREATER_THAN_EQUAL_TO);
	}
	/*
	 *
	 * Description: make (less than and equal) condition
	 * */
	public static EntityCondition makeLTEcondition(String strField, Object value){
		return makeGreaterOrLessTEcondition(strField,value,EntityOperator.LESS_THAN_EQUAL_TO);
	}
	/*
	 *
	 * Description: define formula type
	 * */
	public enum CallBuildFormulaType{
		NATIVE,
		PRIMITIVE
	}	
	//FIXME Ignore time, Need work time configuration
	public static int daysBetween(Timestamp start, Timestamp end){
		DateTime startTmp = new DateTime(start.getTime());
		DateTime endTmp = new DateTime(end.getTime());
		return Days.daysBetween(startTmp, endTmp).getDays();
	}
	
	//FIXME maybe deleted
	public static Map<Enum<PeriodEnum>, Long> getPeriodLength(Delegator delegator) throws GenericEntityException{
		GenericValue yearlyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "YEARLY"), false);
		GenericValue quarterlyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "QUARTERLY"), false);
		GenericValue monthlyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "MONTHLY"), false);
		GenericValue weeklyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "WEEKLY"), false);
		GenericValue dailyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "DAILY"), false);
		GenericValue hourlyGv = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "HOURLY"), false);
		Map<Enum<PeriodEnum>, Long> retMap = FastMap.newInstance();
		retMap.put(PeriodEnum.YEARLY, yearlyGv.getLong("periodLength"));
		retMap.put(PeriodEnum.QUARTERLY, quarterlyGv.getLong("periodLength"));
		retMap.put(PeriodEnum.MONTHLY, monthlyGv.getLong("periodLength"));
		retMap.put(PeriodEnum.WEEKLY, weeklyGv.getLong("periodLength"));
		retMap.put(PeriodEnum.DAILY, dailyGv.getLong("periodLength"));
		retMap.put(PeriodEnum.HOURLY, hourlyGv.getLong("periodLength"));
		return retMap;
	}
	
	//FIXME maybe deleted
	public static String convertValueCorrespondingPerHour(Map<Enum<PeriodEnum>, Long> periodLength, String value, String periodTypeId) throws ScriptException{
		String convertValue = value;
		if("YEARLY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.YEARLY));
		}else if("QUARTERLY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.QUARTERLY));
		}else if("MONTHLY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.MONTHLY));
		}else if("WEEKLY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.WEEKLY));
		}else if("DAILY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.DAILY));
		}else if("HOURLY".equals(periodTypeId)){
			convertValue = PayrollUtil.evaluateStringExpression(value + "/" + periodLength.get(PeriodEnum.HOURLY));
		}
		return convertValue;
	}
	
	
	public static String getActualValueByPeriod(DispatchContext dctx, String partyId, Timestamp fromDate,
			Timestamp thruDate, Timestamp startCycle, Timestamp endCycle,
			String value, PeriodEnum period, Locale locale, TimeZone timeZone) throws GenericServiceException, Exception {		
		//Map<String,Object> resultsMap = FastMap.newInstance();
		Map<String, Object> dayLeaveMap;
				
		Float totalDayWorkInCycle = 0f;
		Float countTimekeeping = 0f;
		String retValue = "0";
		if(PeriodEnum.DAILY.equals(period)){
			dayLeaveMap = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, partyId);
			float totalDayEmplLeave = (Float)dayLeaveMap.get("totalDayLeave");
			float totalDayEmplLeavePaid = (Float)dayLeaveMap.get("leavePaid");
			float dayWorkInFromDateToThruDate = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
			float dayWorkActual = dayWorkInFromDateToThruDate - totalDayEmplLeave + totalDayEmplLeavePaid;
			retValue = PayrollUtil.evaluateStringExpression(value + "*" + dayWorkActual);
		}else{
			if(thruDate.after(endCycle)){
				Timestamp tempFromDate = fromDate;
				Timestamp tempThrudate = endCycle;
				Timestamp tempStartCycle = startCycle;
				Timestamp tempEndCycle = endCycle;	
				
				while(tempStartCycle.before(thruDate)){
					Float dayWorkInCycle = 0f;
					dayLeaveMap = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, tempFromDate, tempThrudate, partyId);
					float tempDayEmplLeave = (Float)dayLeaveMap.get("totalDayLeave");
					float tempDayEmplLeaveApproved = (Float)dayLeaveMap.get("leavePaid");
					
					dayWorkInCycle = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, tempStartCycle, tempEndCycle, locale, timeZone);
					float dayWorkInFromThruDate = dayWorkInCycle;
					if(!tempFromDate.equals(tempStartCycle) || !tempThrudate.equals(tempEndCycle)){
						dayWorkInFromThruDate = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, tempFromDate, tempThrudate, locale, timeZone);
					}
					float tempCountTimekeeping = dayWorkInFromThruDate - tempDayEmplLeave + tempDayEmplLeaveApproved;
					if(dayWorkInCycle != 0){
						retValue = PayrollUtil.evaluateStringExpression(retValue + "+" + value + "*" + tempCountTimekeeping + "/" + dayWorkInCycle);
					}
					
					switch(period) {
						case YEARLY:
							tempStartCycle = UtilDateTime.getYearStart(tempStartCycle, 0, 1);
							tempEndCycle = UtilDateTime.getYearEnd(tempStartCycle, timeZone, locale);
							break;
						case QUARTERLY:
							tempStartCycle = DateUtil.getQuarterStart(tempStartCycle,locale, timeZone, 1);
							tempEndCycle = UtilDateTime.getMonthEnd(UtilDateTime.getMonthStart(tempStartCycle, 0, 3), timeZone, locale);
							break;
						case MONTHLY:
							tempStartCycle = UtilDateTime.getMonthStart(tempStartCycle, 0, 1);
							tempEndCycle = UtilDateTime.getMonthEnd(tempStartCycle, timeZone, locale);
							break;
						case WEEKLY:
							tempStartCycle = UtilDateTime.getWeekStart(tempStartCycle, 0, 1);
							tempEndCycle = UtilDateTime.getWeekEnd(tempStartCycle, timeZone, locale);
							break;
						default:
							break;
					}
					tempFromDate = tempStartCycle;
					if(tempEndCycle.after(thruDate)){
						tempThrudate = thruDate;
					}else{
						tempThrudate = tempEndCycle;
					}
				}
			}else{			
				totalDayWorkInCycle = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, startCycle, endCycle, locale, timeZone);
				float totalDayWorkInFromThruDate = totalDayWorkInCycle;
				if(!startCycle.equals(fromDate) || !endCycle.equals(thruDate)){
					totalDayWorkInFromThruDate = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
				}
				dayLeaveMap = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, partyId); 
				float tempDayEmplLeave = (Float)dayLeaveMap.get("totalDayLeave");
				float tempDayEmplLeaveApproved = (Float)dayLeaveMap.get("leavePaid");
				countTimekeeping += totalDayWorkInFromThruDate - tempDayEmplLeave + tempDayEmplLeaveApproved; 
				retValue = PayrollUtil.evaluateStringExpression(value + "*" + countTimekeeping + "/" + totalDayWorkInCycle); 
			}
		}
		if(retValue.equals("NaN")){
			retValue = "0";
		}
		return retValue;
	}
	
	/*public static Float getTotalDayWorkByEmplPosType(
			List<GenericValue> emplPosTypes, Timestamp fromDate,
			Timestamp thruDate, LocalDispatcher dispatcher, TimeZone timeZone, Locale locale) throws GenericServiceException {
		
		Float totalDayWork = 0f;
		Map<String, Object> resultService; 
		for(GenericValue tempEmplPosType: emplPosTypes){
			resultService = dispatcher.runSync("getDayWorkEmplPosTypeInPeriod", UtilMisc.toMap("emplPositionTypeId", tempEmplPosType.getString("emplPositionTypeId"), "fromDate", fromDate, "thruDate", thruDate, "timeZone", timeZone, "locale", locale));
			Float tempDayWork = (Float)resultService.get("dayWork");
			if(tempDayWork > totalDayWork){
				totalDayWork = tempDayWork;
			}
		}
		return totalDayWork;
	}*/
	
	public static Set<String> getAllRelatedFunction(Delegator delegator, GenericValue payrollFormula) throws GenericEntityException{
		Set<String> retSet = FastSet.newInstance();
		String function = payrollFormula.getString("function");
		String[] functionArr = function.split("[\\+\\-\\*\\/\\%]");
		for(String tempFunc: functionArr){
			if(tempFunc.contains("()") && !tempFunc.contains("if")){
				tempFunc = tempFunc.trim();
				tempFunc = tempFunc.replace("(","");
				tempFunc = tempFunc.replace(")","");
				retSet.add(tempFunc);
				retSet.addAll(getAllRelatedFunction(delegator, delegator.findOne("PayrollFormula", UtilMisc.toMap("code", tempFunc), false)));
			}
		}
		return retSet;
	}
	
	
	public static Double getTotalValuePartyPayrollFormula(Delegator delegator,
			String partyId, String payrollTableId, Timestamp fromDate,
			Map<String, Object> tempMap, Map<String, Double> parentTotalResult, List<GenericValue> listFormula) throws GenericEntityException {
		Double retValue = 0d;
		for(GenericValue formula: listFormula){
			GenericValue tempGv = delegator.findOne("PayrollTable", UtilMisc.toMap("partyId", partyId, "code", formula.getString("code"), "payrollTableId", payrollTableId, "fromDate", fromDate), false);
			String value = tempGv.getString("value");
			Double tempValue = Double.parseDouble(value);
			retValue += tempValue;
			tempMap.put(formula.getString("code"), Math.round(tempValue));
			Double totalValueCode = parentTotalResult.get(formula.get("code"));
			if(totalValueCode != null){
				parentTotalResult.put(formula.getString("code"), totalValueCode + tempValue);
			}else{
				parentTotalResult.put(formula.getString("code"), tempValue);
			}
		}
		return retValue;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSalaryCalculateParty(DispatchContext dctx, Delegator delegator, String orgId, String payrollTableId, Timestamp fromDate, Timestamp thruDate,
			Locale locale, TimeZone timeZone,
			List<GenericValue> listFormulaIncome, List<GenericValue> listFormulaTaxDeduction, List<GenericValue> listFormulaDeduction, List<GenericValue> listFormulaOrgPaid, List<GenericValue> listFormulaOthers) throws GenericEntityException, GenericServiceException {
		
		Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, false, false);
		List<GenericValue> directEmployee = buildOrg.getDirectEmployee(delegator);
		List<GenericValue> directChildOrg = buildOrg.getDirectChildList(delegator);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<Map<String, Object>> listChildren = FastList.newInstance();
		Map<String, Object> parentOrgMap = FastMap.newInstance();
		/* Map contain result calculate of orgId's children */
		Map<String, Double> parentTotalResult = FastMap.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		parentOrgMap.put("partyId", orgId);
		parentOrgMap.put("partyName", PartyHelper.getPartyName(delegator, orgId, false));
		parentOrgMap.put("expanded", true);
		
		parentOrgMap.put("children", listChildren);
		listReturn.add(parentOrgMap);
		retMap.put("listReturn", listReturn);
		retMap.put("totalCalculateResult", parentTotalResult);
		Double totalRealSalatyPaid = 0d;
		Double totalFormulaIncome = 0d;
		Double totalFormulaDeduction = 0d;
		Double totalFormulaTaxDeduction = 0d;
		Double totalOrgPaid = 0d;
		
		if(directEmployee != null){
			for(GenericValue employee: directEmployee){
				String partyId = employee.getString("partyId");
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", partyId);
				if(thruDate != null){
					Float dayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
					Map<String, Object> mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, partyId);
					Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
					Float dayWorkActual = dayWork - totalDayLeave;
					if(dayWorkActual < 0){
						System.out.println("partyId: " + partyId);
						System.out.println("dayWork: " + dayWork);
						System.out.println("totalDayLeave: " + totalDayLeave);
						dayWorkActual = 0f;
					}
					tempMap.put("numberWorkDay", dayWorkActual.toString() + "/" + dayWork.toString());
				}
				
				Double realSalaryPaid = 0d;
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("expanded", true);
				PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempMap, parentTotalResult, listFormulaOthers);
				Double totalValue = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempMap, parentTotalResult, listFormulaIncome);
				realSalaryPaid += totalValue;
				totalFormulaIncome += totalValue;
				tempMap.put("totalFormulaIncome", Math.round(totalValue));
				
				totalValue = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempMap, parentTotalResult, listFormulaDeduction);
				realSalaryPaid -= totalValue;
				totalFormulaDeduction += totalValue;
				tempMap.put("totalFormulaDeduction", Math.round(totalValue));
				
				totalValue = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempMap, parentTotalResult,listFormulaTaxDeduction);
				totalFormulaTaxDeduction += totalValue;
				tempMap.put("totalFormulaTaxDeduction", Math.round(totalValue));
				
				totalValue = PayrollUtil.getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempMap, parentTotalResult, listFormulaOrgPaid);
				totalOrgPaid += totalValue;
				tempMap.put("totalOrgPaid", Math.round(totalValue));
				tempMap.put("realSalaryPaid", Math.round(realSalaryPaid));	
				totalRealSalatyPaid += realSalaryPaid;
				listChildren.add(tempMap);
			}
		}
		parentTotalResult.put("totalFormulaIncome", totalFormulaIncome);
		parentTotalResult.put("totalFormulaDeduction", totalFormulaDeduction);
		parentTotalResult.put("totalFormulaTaxDeduction", totalFormulaTaxDeduction);
		parentTotalResult.put("totalOrgPaid", totalOrgPaid);
		parentTotalResult.put("realSalaryPaid", totalRealSalatyPaid);
		
		if(directChildOrg != null){
			for(GenericValue tempOrg: directChildOrg){
				String childOrgId = tempOrg.getString("partyId");
				Map<String, Object> temp = getSalaryCalculateParty(dctx, delegator, childOrgId, payrollTableId, fromDate, thruDate,
						locale, timeZone,
						listFormulaIncome, listFormulaTaxDeduction, listFormulaDeduction, listFormulaOrgPaid, listFormulaOthers); 
				listChildren.addAll((List<Map<String, Object>>)temp.get("listReturn"));
				Map<String, Double> tempParentCalcResult = (Map<String, Double>)temp.get("totalCalculateResult");
				for(Map.Entry<String, Double> entry: tempParentCalcResult.entrySet()){
					Double tempValue = parentTotalResult.get(entry.getKey());
					if(tempValue != null){
						parentTotalResult.put(entry.getKey(), tempValue + entry.getValue());
					}else {
						parentTotalResult.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		for(Map.Entry<String, Double> entry: parentTotalResult.entrySet()){
			parentOrgMap.put(entry.getKey(), Math.round(entry.getValue()));
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static String convertCalcSalaryEmplToJson(LocalDispatcher dispatcher, String payrollTableId, Timestamp fromDate, Timestamp thruDate, 
			GenericValue userLogin, Locale locale, TimeZone timeZone){
		String retStr = "";
		try {
			Map<String, Object> resultService = dispatcher.runSync("getPayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId, 
					"fromDate", String.valueOf(fromDate.getTime()),
					"thruDate", thruDate,
					"locale", locale,
					"timeZone", timeZone,
					"userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				List<Map<String, Object>> listReturn = (List<Map<String,Object>>)resultService.get("listIterator");
				retStr = excuteConvertCalcSalaryEmplToJson(listReturn);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retStr;
	}
	@SuppressWarnings("unchecked")
	private static String excuteConvertCalcSalaryEmplToJson(List<Map<String, Object>> listReturn) {
		StringBuffer buffer = new StringBuffer();
		for(Map<String, Object> tempMap: listReturn){
			buffer.append("{");
			for(Map.Entry<String, Object> entry: tempMap.entrySet()){
				if(!entry.getKey().equals("children")){
					buffer.append("'" + entry.getKey() + "': '" + entry.getValue() + "',");
				}else{
					List<Map<String, Object>> childrenList = (List<Map<String, Object>>) entry.getValue();
					if(UtilValidate.isNotEmpty(childrenList)){
						buffer.append("'" + entry.getKey() + "': [" );
						buffer.append(excuteConvertCalcSalaryEmplToJson(childrenList));
						buffer.append("],"); 
					}
				}				
			}
			buffer.append("},");
		}
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param delegator
	 * @param departmentId
	 * @param codeSetId
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<GenericValue> getListInvoiceItemTypeOfFormulaByParty(
			Delegator delegator, String departmentId, Set<String> codeSetId) throws GenericEntityException {
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("code", EntityOperator.IN, codeSetId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> partyPayrollFormulaInvoiceItemType = delegator.findList("PartyPayrollFormulaInvoiceItemType", EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> partyIdList = EntityUtil.getFieldListFromEntityList(partyPayrollFormulaInvoiceItemType, "partyId", true);	
		if(partyIdList.contains(departmentId)){
			//departmentId set invoiceItemType by formula, so return list is setup
			return EntityUtil.filterByCondition(partyPayrollFormulaInvoiceItemType, EntityCondition.makeCondition("partyId", departmentId));
		}else{
			//departmentId is not set invoiceItemType by formula, so find in parent of departmentId 
			//FIXME 
			for(String tempPartyId: partyIdList){
				Organization buildOrg = PartyUtil.buildOrg(delegator, tempPartyId, true, false);
				List<GenericValue> childList = buildOrg.getChildList();
				if(childList != null){
					List<String> childListId = EntityUtil.getFieldListFromEntityList(childList, "partyId", true);
					if(childListId.contains(departmentId)){
						return EntityUtil.filterByCondition(partyPayrollFormulaInvoiceItemType, EntityCondition.makeCondition("partyId", tempPartyId));
					}
				}
			}
		}
		return null;
	}
	public static String updatePayrollTableStatus(Delegator delegator,
			String payrollTableId, String requestId) throws GenericEntityException {
		GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
		String processStatusId = workFlowRequest.getString("processStatusId");
		GenericValue workFlowProcessStatus = delegator.findOne("WorkFlowProcessStatus", UtilMisc.toMap("processStatusId", processStatusId), false);
		GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
		String statusId = workFlowProcessStatus.getString("statusId");
		String updateStatusId;
		if("WORK_FLOW_COMPPLETE".equals(statusId)){
			updateStatusId = "PYRLL_TABLE_APPR";
			payrollTableRecord.set("statusId", "PYRLL_TABLE_APPR");
			payrollTableRecord.store();
		}else if("WORK_FLOW_DENIED".equals(statusId)){
			updateStatusId = "PYRLL_TABLE_RECALC";
			payrollTableRecord.set("statusId", "PYRLL_TABLE_RECALC");
			payrollTableRecord.store();
		}else{
			updateStatusId = "PYRLL_WAIT_APPR";
			payrollTableRecord.set("statusId", "PYRLL_WAIT_APPR");
			payrollTableRecord.store();
		}
		return updateStatusId;
	}
	
	public static List<GenericValue> getParamPosTypeGeoAppl(Delegator delegator, List<GenericValue> paramPosTypes, GenericValue postalAddr) throws GenericEntityException {
		List<GenericValue> retList = FastList.newInstance(); 
		List<String> payrollParamPositionTypeIdList = EntityUtil.getFieldListFromEntityList(paramPosTypes, "payrollParamPositionTypeId", true);
		EntityCondition payrollParamPositionTypeConds = EntityCondition.makeCondition("payrollParamPositionTypeId", EntityJoinOperator.IN, payrollParamPositionTypeIdList); 
		EntityCondition pyrllParamPosTypeInclConds = EntityCondition.makeCondition(payrollParamPositionTypeConds,
				EntityJoinOperator.AND,
				EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE")); 
		List<GenericValue> payrollParamPositionTypeInclude = delegator.findList("PyrllParamPosTypeGeoAppl", 
				pyrllParamPosTypeInclConds, null, null, null, false);
		List<String> geoListBoundary = EntityUtil.getFieldListFromEntityList(payrollParamPositionTypeInclude, "geoId", true);
		for(GenericValue paramPosType: paramPosTypes){
			List<Map<String, Timestamp>> fromThruDateMapList = FastList.newInstance();
			Timestamp fromDate = paramPosType.getTimestamp("fromDate");
			Timestamp thruDate = paramPosType.getTimestamp("thruDate");
			Map<String, Timestamp> tempMap = FastMap.newInstance();
			tempMap.put("fromDate", fromDate);
			tempMap.put("thruDate", thruDate);
			fromThruDateMapList.add(tempMap);
			String payrollParamPositionTypeId = paramPosType.getString("payrollParamPositionTypeId");
			List<GenericValue> pyrllParamPosTypeGeoIncList = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_INCLUDE"), null, false);
			List<GenericValue> pyrllParamPosTypeGeoExcList = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_EXCLUDE"), null, false);
			List<String> geoIncludeList = EntityUtil.getFieldListFromEntityList(pyrllParamPosTypeGeoIncList, "geoId", true);
			List<String> geoExcludeList = EntityUtil.getFieldListFromEntityList(pyrllParamPosTypeGeoExcList, "geoId", true);
			boolean addressInExclude = CommonUtil.checkPostalAddressInGeoList(delegator, postalAddr, geoExcludeList);
			boolean addressInInclude = CommonUtil.checkPostalAddressInGeoList(delegator, postalAddr, geoIncludeList);
			if(!addressInExclude && addressInInclude){
				for(GenericValue pyrllParamPosTypeGeoInc: pyrllParamPosTypeGeoIncList){
					String geoIncludeId = pyrllParamPosTypeGeoInc.getString("geoId");
					List<String> geoChildList = CommonUtil.getAllChildOfGeoInGeoBoundary(delegator, geoIncludeId, geoListBoundary);
					if(UtilValidate.isNotEmpty(geoChildList)){
						List<GenericValue> pyrllParamPosTypeGeoChild = delegator.findList("PayrollParamPositionTypeAndGeo", 
								EntityCondition.makeCondition(EntityCondition.makeCondition(pyrllParamPosTypeInclConds),
																EntityJoinOperator.AND,
																EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, geoChildList)), null, null, null, false);
						for(GenericValue tempGv: pyrllParamPosTypeGeoChild){
							Timestamp tempFromDate = tempGv.getTimestamp("fromDate");
							Timestamp tempThruDate = tempGv.getTimestamp("thruDate");
							fromThruDateMapList = DateUtil.splitFromThruDateMap(fromThruDateMapList, tempFromDate, tempThruDate);
						}
					}
				}
				
				for(Map<String, Timestamp> entry: fromThruDateMapList){
					Timestamp tempFromDate = entry.get("fromDate");
					Timestamp tempThruDate = entry.get("thruDate");
					GenericValue tempGv = delegator.makeValue("PayrollParamPositionType");
					tempGv.set("fromDate", tempFromDate);
					tempGv.set("thruDate", tempThruDate);
					tempGv.set("periodTypeId", paramPosType.getString("periodTypeId"));
					tempGv.set("rateAmount", paramPosType.getBigDecimal("rateAmount"));
					tempGv.set("code", paramPosType.getString("code"));
					retList.add(tempGv);
				}
			}
		}
		return retList;
	}
	
	public static List<GenericValue> getEmplPositionTypeRate(Delegator delegator, List<GenericValue> emplPositionTypeRateList, GenericValue postalAddr) throws GenericEntityException {
		List<GenericValue> retList = FastList.newInstance();
		List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateList, "emplPositionTypeRateId", true);
		EntityCondition emplPositionTypeRateConds = EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.IN, emplPositionTypeRateIdList);
		EntityCondition emplPositionTypeRateInclConds = EntityCondition.makeCondition(emplPositionTypeRateConds,
				EntityJoinOperator.AND,
				EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
		List<GenericValue> emplPositionTypeRateInclude = delegator.findList("EmplPositionTypeRateGeoAppl", 
				emplPositionTypeRateInclConds, null, null, null, false);
		List<String> geoListBoundary = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateInclude, "geoId", true);
		for(GenericValue emplPositionTypeRate: emplPositionTypeRateList){
			String emplPositionTypeRateId = emplPositionTypeRate.getString("emplPositionTypeRateId");
			List<Map<String, Timestamp>> fromThruDateMapList = FastList.newInstance();
			Timestamp fromDate = emplPositionTypeRate.getTimestamp("fromDate");
			Timestamp thruDate = emplPositionTypeRate.getTimestamp("thruDate");
			Map<String, Timestamp> tempMap = FastMap.newInstance();
			tempMap.put("fromDate", fromDate);
			tempMap.put("thruDate", thruDate);
			fromThruDateMapList.add(tempMap);
			List<GenericValue> emplPositionTypeRateGeoIncList = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_INCLUDE"), null, false);
			List<GenericValue> emplPositionTypeRateExcList = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_EXCLUDE"), null, false);
			List<String> geoIncludeList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateGeoIncList, "geoId", true);
			List<String> geoExcludeList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateExcList, "geoId", true);
			boolean addressInExclude = CommonUtil.checkPostalAddressInGeoList(delegator, postalAddr, geoExcludeList);
			boolean addressInInclude = CommonUtil.checkPostalAddressInGeoList(delegator, postalAddr, geoIncludeList);
			if(!addressInExclude && addressInInclude){
				for(GenericValue emplPositionTypeRateGeoInc: emplPositionTypeRateGeoIncList){
					String geoIncludeId = emplPositionTypeRateGeoInc.getString("geoId");
					List<String> geoChildList = CommonUtil.getAllChildOfGeoInGeoBoundary(delegator, geoIncludeId, geoListBoundary);
					if(UtilValidate.isNotEmpty(geoChildList)){
						List<GenericValue> pyrllParamPosTypeGeoChild = delegator.findList("OldEmplPositionTypeRateAndGeoAppl", 
								EntityCondition.makeCondition(EntityCondition.makeCondition(emplPositionTypeRateInclConds),
																EntityJoinOperator.AND,
																EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, geoChildList)), null, null, null, false);
						for(GenericValue tempGv: pyrllParamPosTypeGeoChild){
							Timestamp tempFromDate = tempGv.getTimestamp("fromDate");
							Timestamp tempThruDate = tempGv.getTimestamp("thruDate");
							fromThruDateMapList = DateUtil.splitFromThruDateMap(fromThruDateMapList, tempFromDate, tempThruDate);
						}
					}
				}
				for(Map<String, Timestamp> entry: fromThruDateMapList){
					Timestamp tempFromDate = entry.get("fromDate");
					Timestamp tempThruDate = entry.get("thruDate");
					GenericValue tempGv = delegator.makeValue("OldEmplPositionTypeRate");
					//if(tempFromDate)
					tempGv.set("emplPositionTypeId", emplPositionTypeRate.getString("emplPositionTypeId"));
					tempGv.set("rateCurrencyUomId", emplPositionTypeRate.getString("rateCurrencyUomId"));
					tempGv.set("fromDate", tempFromDate);
					tempGv.set("thruDate", tempThruDate);
					tempGv.set("periodTypeId", emplPositionTypeRate.getString("periodTypeId"));
					tempGv.set("rateAmount", emplPositionTypeRate.getBigDecimal("rateAmount"));
					retList.add(tempGv);
				}
			}
		}
		return retList;
	}
}
