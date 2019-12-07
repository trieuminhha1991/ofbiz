package com.olbius.insurance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.script.ScriptException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.PayrollUtil;

public class InsuranceEngine {

	public static String buildFormula(DispatchContext dctx, GenericValue partyInsuranceReport, String code, String[] paramValue, TimeZone timeZone, Locale locale) throws GenericEntityException, ScriptException {
		// TODO Auto-generated method stub
		Delegator delegator = dctx.getDelegator();
		String strReturn = null;
		if("BQL_SO_THANG_TRUOC_NGHI".equals(code)){
			if(paramValue == null || paramValue.length < 1){
				return "0";
			}
			int month = Integer.parseInt(paramValue[0]);
			strReturn = getAverageSalaryInPeriodMonth(dctx, partyInsuranceReport, month, timeZone, locale);
		}else{
			GenericValue function = delegator.findOne("InsuranceFormula", UtilMisc.toMap("code", code), false);
			String strFunction = function.getString("function");
			List<GenericValue> formulaParams = delegator.findByAnd("InsuranceFormulaParameters", UtilMisc.toMap("code", code), UtilMisc.toList("sequenceId"), false);
			if(UtilValidate.isNotEmpty(formulaParams)){
				if(paramValue == null || formulaParams.size() != paramValue.length){
					//FIXME need throw exception paramList doesn't math requirement with function
					return "0";
				}
				for(int i = 0; i < formulaParams.size(); i++){
					strFunction = strFunction.replace(formulaParams.get(i).getString("parameterName"), paramValue[i].trim());
				}
			}
			strReturn = strFunction;
			String[] strs = strFunction.split("[\\+\\-\\*\\/\\%]"); // split parameters and formulas
			for(String str: strs){
				if(str.contains("(") && str.endsWith(")")){
					str = str.trim();									
					//function have sign as function(param1, param2,.., paramN), so get index of character of "(" and ")"
					int lastIndexOfParenthesis = str.lastIndexOf("(");//get index of character "(" of function 
					int indexOfCloseParenthesis = str.indexOf(")");//get index of character ")" of function
					//end
					int indexOfParenthesis = str.indexOf("(");//get firts index of operand "("
					//int lastIndexOfCloseParenthesis = str.lastIndexOf(")");
					//int toIndex = indexOfCloseParenthesis;
					if(indexOfParenthesis != lastIndexOfParenthesis){
						//int fromIndex = indexOfParenthesis;
						String tempStr = str.substring(indexOfParenthesis, lastIndexOfParenthesis);
						int fromIndex = tempStr.lastIndexOf("(");
						str = str.substring(fromIndex + 1, indexOfCloseParenthesis + 1);
						//update index of characters "(" and ")"
						indexOfCloseParenthesis = str.indexOf(")");
						lastIndexOfParenthesis = str.lastIndexOf("(");
					}
					/*if(indexOfCloseParenthesis != lastIndexOfCloseParenthesis){
						str = str.substring(0, indexOfCloseParenthesis);
					}*/
					
					String functionCode = str.substring(0, lastIndexOfParenthesis);
					String tempParamValue = str.substring(lastIndexOfParenthesis + 1, indexOfCloseParenthesis);
					String []tempParamValueList = null;
					if(tempParamValue != null && tempParamValue.length() > 0){
						tempParamValueList = tempParamValue.split(",");
						
					}
					String strTMPResult = buildFormula(dctx, partyInsuranceReport, functionCode, tempParamValueList, timeZone, locale);
					strReturn = strReturn.replace(str, strTMPResult);
				}
			}
		}
		return strReturn;
	}

	public static String calcValueInsuranceAmount(DispatchContext dctx,
			GenericValue partyInsuranceReport, String value, Map<String, String> dataMap) {
		// TODO Auto-generated method stub
		String strTMP = value.replaceAll("\\s+",""); // remove all space characters
		String[] strs = strTMP.split("[\\+\\-\\*\\/\\(\\)]");
		String strReturn = value;
		Timestamp fromDate = partyInsuranceReport.getTimestamp("fromDate");
		Timestamp thruDate = partyInsuranceReport.getTimestamp("thruDate");
		if(UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.nowTimestamp();
		}
		for(String str: strs){
			if(dataMap.containsKey(str)){
				strReturn = strReturn.replace(str, dataMap.get(str));
			}else{
				if(str.matches(".*[a-zA-Z]+.*")){
					if(str.equals("SO_NGAY_NGHI_HUONG_CHE_DO")){
						int nbrDay = UtilDateTime.getIntervalInDays(fromDate, thruDate);
						dataMap.put(str, String.valueOf(nbrDay));
						strReturn = strReturn.replace(str, String.valueOf(nbrDay));
					}else if(str.equals("SO_THANG_NGHI_HUONG_CHE_DO")){
						DateTime fromDateTime = new DateTime(fromDate.getTime());
						DateTime thruDateTime = new DateTime(thruDate.getTime());
						int nbrMonth = Months.monthsBetween(fromDateTime, thruDateTime).getMonths();
						dataMap.put(str, String.valueOf(nbrMonth));
						strReturn = strReturn.replace(str, String.valueOf(nbrMonth));
					}else{
						//FIXME need calculate specific value
						strReturn = strReturn.replace(str, "0");
					}
				}
			}
		}
		return strReturn;
	}
	
	public static String getAverageSalaryInPeriodMonth(DispatchContext dctx, GenericValue partyInsuranceReport, int month, TimeZone timeZone, Locale locale) throws GenericEntityException, ScriptException{
		Delegator delegator = dctx.getDelegator();
		String partyId = partyInsuranceReport.getString("partyId");
		String insuranceTypeId = partyInsuranceReport.getString("insuranceTypeId");
		Calendar cal = Calendar.getInstance();
		Timestamp fromDate = partyInsuranceReport.getTimestamp("fromDate");
		cal.setTimeInMillis(fromDate.getTime());
		Timestamp tempFromDate;
		Timestamp tempThruDate;
		Timestamp tempTimestamp;
		GenericValue partyInsurance;
		BigDecimal totalSalary = BigDecimal.ZERO;
		for(int i = 1; i <= month; i++){
			cal.set(Calendar.MONTH, -i);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			conditions.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
			tempTimestamp = new Timestamp(cal.getTimeInMillis());
			tempFromDate = UtilDateTime.getMonthStart(tempTimestamp);
			tempThruDate = UtilDateTime.getMonthEnd(tempTimestamp, timeZone, locale);
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, tempFromDate));
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), 
															EntityOperator.OR, 
														 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, tempThruDate)));
			List<GenericValue> partyInsuranceList = delegator.findList("PartyInsurance", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceList)){
				partyInsurance = EntityUtil.getFirst(partyInsuranceList);
				BigDecimal salaryInsurance = partyInsurance.getBigDecimal("salaryInsurance");
				totalSalary = totalSalary.add(salaryInsurance); 
			}
		}
		String retValue = PayrollUtil.evaluateStringExpression(totalSalary.toString() + "/" + month);
		return retValue;
	}
	/*get insurance report for jqxgrid*/
	public static Map<String, Object> getInsuranceReports(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("reportId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("ParticipateInsuranceReport", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*get insurance type for jqxgrid*/
	public static Map<String, Object> getInsuranceType(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("insuranceTypeId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("InsuranceType", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*get insurance payment for jqxgrid*/
	public static Map<String, Object> getInsurancePayment(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("insurancePaymentId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("InsurancePayment", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*get list suspend insurance reason for jqx*/
	public static Map<String, Object> getSuspendInsuranceReason(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("suspendReasonId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("SuspendParticipateInsuranceReason", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*get list insurance formula for jqx*/
	public static Map<String, Object> getInsuranceFormula(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("code ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("InsuranceFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*get list insurance formula for jqx*/
	public static Map<String, Object> getPartyInsuranceReport(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("partyId ASC");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String[] report = parameters.get("reportId");
		if(report != null){
			String reportId = report[0];
			listAllConditions.add(EntityCondition.makeCondition("reportId", reportId));
		}
		String[] type = parameters.get("parentTypeId");
		if(type != null){
			String parentTypeId = type[0];
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", parentTypeId));
		}		
		String[] payment = parameters.get("insurancePaymentId");
		String insuranceTypeId = "";
		if(payment != null){
			String insurancePaymentId = payment[0];
			try {
				GenericValue pm = delegator.findOne("InsurancePayment", UtilMisc.toMap("insurancePaymentId", insurancePaymentId), true);
				Timestamp fromDate = pm.getTimestamp("fromDate");
				Timestamp thruDate = pm.getTimestamp("thruDate");
				insuranceTypeId = pm.getString("insuranceTypeId");
				listAllConditions.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
				listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<Map<String, Object>> res = FastList.newInstance();
		try {
			opts.setDistinct(true);
			Set<String> fields = UtilMisc.toSet("reportId", "partyId", "insuranceParticipateTypeId", "fromDate", "thruDate", "comments");
			fields.add("firstName");
			fields.add("middleName");
			fields.add("lastName");
			listIterator = delegator.find("PartyInsuranceReportPerson", tmpCond, null, 
					fields, listSortFields, opts);
			int total = 0;
			if(insuranceTypeId == null || insuranceTypeId.isEmpty()){
				List<GenericValue> tmp = listIterator.getPartialList(start, end);
				for(GenericValue ip : tmp){
					Map<String, Object> cur = FastMap.newInstance();
					cur.putAll(ip);
					List<EntityCondition> cond = FastList.newInstance();
					cond.add(EntityCondition.makeCondition("partyId", ip.getString("partyId")));
					List<GenericValue> im = delegator.findList("PartyInsuranceReportPartyInsurance", 
											EntityCondition.makeCondition(cond, EntityOperator.AND), 
											UtilMisc.toSet( "insuranceTypeId", "insuranceNumber", "partyHealthCareId", "description", "fromDate", "thruDate"), null, null, true);
					cur.put("rowDetail", im);
					res.add(cur);
				}
				total = listIterator.getCompleteList().size();
			}else {
				int i = 0;
				List<EntityCondition> tmp = FastList.newInstance();
				List<GenericValue> insupay = FastList.newInstance();
				Map<String, Object> cur = FastMap.newInstance();
				List<EntityCondition> ipcondlist = FastList.newInstance();
				ipcondlist.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
				ipcondlist.add(EntityCondition.makeCondition("parentTypeId", type[0]));
				ipcondlist.add(EntityCondition.makeCondition("insurancePaymentId", payment[0]));
				EntityListIterator resIp = delegator.find("PartyInsurancePaymentDetail", EntityCondition.makeCondition(ipcondlist, EntityOperator.AND), null, 
						null, null, null);
				
				while (size >= i && res.size() <= size){
					GenericValue insu = listIterator.next();
					if(insu == null){
						break;
					}
					tmp = FastList.newInstance();
					tmp.add(EntityCondition.makeCondition("insurancePaymentId", payment[0]));
					tmp.add(EntityCondition.makeCondition("partyId", insu.getString("partyId")));
					tmp.add(EntityCondition.makeCondition("reportId", insu.getString("reportId")));
					tmp.add(EntityCondition.makeCondition("insuranceParticipateTypeId", insu.getString("insuranceParticipateTypeId")));
					tmp.add(EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
					insupay = delegator.findList("PartyInsurancePayment", EntityCondition.makeCondition(tmp, EntityOperator.AND), null, null, null, false);
					if(insupay == null || insupay.size() == 0){
						cur = FastMap.newInstance();
						cur.putAll(insu);
						cur.put("insuranceTypeId", insuranceTypeId);
						res.add(cur);
					}	
					i++;
				}
				total = listIterator.getCompleteList().size();
				int hasPayment = resIp.getCompleteList().size();
				resIp.close();
				total -= hasPayment;
			}
			listIterator.close();
			successResult.put("TotalRows", String.valueOf(total));
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", res);
		return successResult;
	}
	/*get list insurance formula for jqx*/
	public static Map<String, Object> getPartyInsurancePayment(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String insurancePaymentId = (String) parameters.get("insurancePaymentId")[0];
		listAllConditions.add(EntityCondition.makeCondition("insurancePaymentId", insurancePaymentId));
		listSortFields.add("insurancePaymentId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PartyInsurancePaymentDetail", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
