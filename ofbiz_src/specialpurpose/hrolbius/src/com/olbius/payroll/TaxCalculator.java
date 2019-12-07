package com.olbius.payroll;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;

import com.olbius.payroll.PayrollUtil.CallBuildFormulaType;
import com.olbius.payroll.entity.EntityEmployeeSalary;
import com.olbius.payroll.entity.EntitySalaryAmount;
import com.olbius.payroll.services.PayrollServices;

/*
 * 
 * Description: 1. Calculate tax
 * */
public class TaxCalculator {
	public static String calculateIncomeTax(DispatchContext ctx, String strFormulaCode, Timestamp tsFromDate, Timestamp tsThruDate, 
											String strPaytyId, Map<String,String> mapCache, Map<String,String> dataMap) throws Exception{
		// Tax: begin calculate tax
		// tax-1: get formula code
		GenericValue payrollFormula = ctx.getDelegator().findOne("PayrollFormula", UtilMisc.toMap("code",strFormulaCode), false);
		// tax-1.1: calculate salary
		EntitySalaryAmount salary = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, PayrollEngine.buildFormula(ctx,payrollFormula.get("function").toString(),tsFromDate,tsThruDate,PayrollUtil.CallBuildFormulaType.PRIMITIVE,strPaytyId,mapCache,dataMap),tsFromDate,tsThruDate,strPaytyId,dataMap);
		EntityExpr condition = EntityCondition.makeCondition("formulaCode", EntityOperator.EQUALS, payrollFormula.get("function").toString().replace("(", "").replace(")", ""));
		// Tax-1.2: get tax authority info
		List<GenericValue> listData = ctx.getDelegator().findList("TaxAuthority", condition,null, null, null, false);
		if(listData == null || listData.isEmpty()){
			return salary.getAmount();
		}else{
			// tax-2: calculate
			// tax-2.1: get tax percentage
			EntityCondition conditionDate1 = PayrollUtil.makeGTEcondition("fromDate", tsFromDate);
			EntityCondition conditionDate2 = PayrollUtil.makeLTEcondition("thruDate", tsThruDate);
			
			EntityExpr condition1 = EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, listData.get(0).get("taxAuthGeoId"));
			EntityExpr condition2 = EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, listData.get(0).get("taxAuthPartyId"));
			
			EntityCondition conditionValue1 = PayrollUtil.makeLTEcondition("fromValue", salary.getAmount());
			EntityCondition conditionValue2 = PayrollUtil.makeGTEcondition("thruValue", salary.getAmount());
			
			List<GenericValue> listPercentage = ctx.getDelegator().findList("TaxAuthorityRatePayroll", 
													EntityCondition.makeCondition(UtilMisc.toList(conditionDate1,conditionDate2,condition1,condition2,conditionValue1,conditionValue2)),null, null, null, false);
			if(listPercentage == null || listPercentage.isEmpty()){
				// if not in rank(logic: there are no tax apply for this salary)
				return "0";
			}else{
				return PayrollUtil.evaluateStringExpression(salary.getAmount() + "*" + listPercentage.get(0).getString("taxPercentage") + "/100");				
			}
		}
	}
	
	public static String calculateIncomeIndividualTax(DispatchContext dctx, String strFormula, Timestamp fromDate, Timestamp thruDate, 
			String partyId, Map<String,String> mapCache, Map<String,String> dataMap) throws Exception {
		if("THUE_THU_NHAP".equals(strFormula)){
			Delegator delegator = dctx.getDelegator();
			String incomeIndividualTaxValue = "0";
			//get all income formula
			List<GenericValue> allIncomeFormual = delegator.findByAnd("PayrollFormula", UtilMisc.toMap("payrollCharacteristicId", "INCOME"), null, false);
			String totalIncomeValue = "0";
			for(GenericValue tempIncome: allIncomeFormual){
				String tempCode = tempIncome.getString("code");
				String tempIncomeValue = "0";
				if(mapCache.containsKey(tempCode + "()")){
					tempIncomeValue = mapCache.get(tempCode + "()");
				}else{
					if(partyId == null || fromDate == null || thruDate == null || tempCode == null){
						System.out.println("partyId: " + partyId);
						System.out.println("fromDate: " + fromDate);
						System.out.println("thruDate: " + thruDate);
						System.out.println("tempCode: " + tempCode);
					}
					tempIncomeValue = PayrollEngine.buildFormula(dctx, tempCode + "()", fromDate, thruDate, CallBuildFormulaType.NATIVE, partyId, mapCache, dataMap);					
				}
				tempIncomeValue = PayrollEngine.calculateFunction(tempIncomeValue, dataMap);
				totalIncomeValue = PayrollUtil.evaluateStringExpression(totalIncomeValue + "+" + tempIncomeValue);
			}
			
			//get all deduction exempted formula
			String totalExemptedDeductionValue = "0";
			List<EntityCondition> exemptedConds = FastList.newInstance();
			exemptedConds.add(EntityCondition.makeCondition("exempted", "Y"));
			exemptedConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payrollCharacteristicId", "TAX_DEDUCTION"),
															EntityOperator.OR,
															EntityCondition.makeCondition("payrollCharacteristicId", "DEDUCTION")));
			List<GenericValue> exemptedDeductionFormula = delegator.findList("PayrollFormula", EntityCondition.makeCondition(exemptedConds), null, null, null, false);
			for(GenericValue tempExempted: exemptedDeductionFormula){
				String tempDeductionCode = tempExempted.getString("code");
				String tempDeductionValue = "0";
				if(mapCache.containsKey(tempDeductionCode + "()")){
					tempDeductionValue = mapCache.get(tempDeductionCode + "()");
				}else{
					tempDeductionValue = PayrollEngine.buildFormula(dctx, tempDeductionCode  + "()", fromDate, thruDate, CallBuildFormulaType.NATIVE, partyId, mapCache, dataMap);
				}
				tempDeductionValue = PayrollEngine.calculateFunction(tempDeductionValue, dataMap);
				totalExemptedDeductionValue = PayrollUtil.evaluateStringExpression(totalExemptedDeductionValue + "+" + tempDeductionValue); 
			}
			String thu_nhap_tinh_thue = PayrollUtil.evaluateStringExpression(totalIncomeValue + "-" + totalExemptedDeductionValue);
			try{
				if(Float.parseFloat(thu_nhap_tinh_thue) > 0){
					//TODO need process when periodType of TaxAuthorityRatePayroll is not same period of calculate salary 
					String taxRate = TaxWorker.getTaxAuthorityRatePayroll(dctx, thu_nhap_tinh_thue, fromDate, thruDate);
					if(taxRate != null){
						incomeIndividualTaxValue = TaxWorker.calculateIncomeTaxIndividual(dctx, taxRate, thu_nhap_tinh_thue, fromDate, thruDate);
					}else{
						return "0";
					}
					return incomeIndividualTaxValue;
				}
			}catch(Exception e){
				return "0";
			}
		}
		return "0";
	}
	
	/*FIXME replace calculateIncomeIndividualTax2() with calculateIncomeIndividualTax() */
	@SuppressWarnings("unchecked")
	public static String calculateIncomeIndividualTax2(DispatchContext dctx, String strFormula, Timestamp fromDate, Timestamp thruDate, 
															String partyId, Map<String,String> mapCache, Map<String,String> dataMap) throws Exception{		
		/*Delegator delegator = dctx.getDelegator();
		GenericValue formula = delegator.findOne("formulaCode", UtilMisc.toMap("code", strFormula), false);
		String function = formula.getString("function");
		String strReturn = function;
		String[] strs = function.split("[\\+\\-\\*\\/\\%]"); // split function
		for(String str: strs){
			str = str.trim();
			if(str.endsWith("()")){
				str = str.replace("\\s", "");
				str = str.replace("()", "");
				strReturn = strReturn.replace(str + "()", "(" + PayrollEngine.buildFormula(dctx, str + "()", fromDate, thruDate, PayrollUtil.CallBuildFormulaType.PRIMITIVE, partyId, mapCache, dataMap) + ")" );	
			}else if(str.matches(".*[a-zA-Z]+(\\w")){
				
			}
		}*/
		String returnValue = "0";
		//FIXME need edit hard fix THUE_THU_NHAP code
		if(strFormula.equals("THUE_THU_NHAP")){
			//GenericValue formula = delegator.findOne("formulaCode", UtilMisc.toMap("code", strFormula), false);
			 
			//get period time will be used to calculate individual income tax if fromDate and thruDate not in same month 
			List<Map<String, Timestamp>> periodIncIndividualTax = TaxWorker.getPeriodTimeCalcIncIndividualTax(fromDate, thruDate, TimeZone.getDefault(), Locale.getDefault()); 					
			
			//FIXME need edit hard fix THU_NHAP_TINH_THUE() code 
			for(Map<String, Timestamp> tempMap: periodIncIndividualTax){
				//Map<String, String> tempMapCache = FastMap.newInstance();
				Timestamp fromDateTmp = tempMap.get("fromDate");
				Timestamp thruDateTmp = tempMap.get("thruDate");
				/*EntitySalaryAmount incomeIndividualTax = PayrollEngine.calculateParticipateFunctionForEmployee(dctx,
									PayrollEngine.buildFormula(dctx, "THU_NHAP_TINH_THUE()", fromDateTmp, thruDateTmp, PayrollUtil.CallBuildFormulaType.PRIMITIVE, partyId, tempMapCache, dataMap),
												fromDateTmp, thruDateTmp, partyId, dataMap);*/
				Map<String, Object> incIndividualTaxMap = 
						PayrollServices.getSalaryAmountList(dctx, UtilMisc.toMap("fromDate", fromDateTmp,
																				 "thruDate", thruDateTmp,
																				 "pdfPartyId", partyId,
																				 "formulaList", UtilMisc.toList("THU_NHAP_TINH_THUE"),
																				 "locale", Locale.getDefault()));
				String thu_nhap_tinh_thue = "0";
				List<EntityEmployeeSalary> emplIncIndividualTaxSalary = (List<EntityEmployeeSalary>)incIndividualTaxMap.get("salaryAmountList");
				if(UtilValidate.isNotEmpty(emplIncIndividualTaxSalary)){
					EntityEmployeeSalary ettEmpSalary = emplIncIndividualTaxSalary.get(0);
					thu_nhap_tinh_thue = ettEmpSalary.getListSalaryAmount().get(0).getAmount();
				}
				
				if(Float.parseFloat(thu_nhap_tinh_thue) > 0){
					String taxRate = TaxWorker.getTaxAuthorityRatePayroll(dctx, thu_nhap_tinh_thue, fromDateTmp, thruDateTmp);
					String incomeIndividualTaxValue = TaxWorker.calculateIncomeTaxIndividual(dctx, taxRate, thu_nhap_tinh_thue, fromDateTmp, thruDateTmp);
					returnValue = PayrollUtil.evaluateStringExpression(returnValue + "+" + incomeIndividualTaxValue);
				}
			}
		
		}else if(strFormula.equals("THU_NHAP_TINH_THUE")){
			//FIXME need edit hard fix THU_NHAP_TINH_THUE() code
			//get period time will be used to calculate individual income tax if fromDate and thruDate not in same month
			List<Map<String, Timestamp>> periodCalcTimes = TaxWorker.getPeriodTimeCalcIncIndividualTax(fromDate, thruDate, TimeZone.getDefault(), Locale.getDefault());
			GenericValue tempGv = dctx.getDelegator().findOne("PayrollFormula", UtilMisc.toMap("code", strFormula), false);
			String function = tempGv.getString("function");
			String[] strs = function.split("[\\+\\-\\*\\/\\%]");
			List<String> formulaList = FastList.newInstance();
			for(String str: strs){
				if(str.contains("()")){
					str = str.trim();
					str = str.replace("(", "");
					str = str.replace(")", "");
					formulaList.add(str);	
				}
			}
			for(Map<String, Timestamp> tempMap: periodCalcTimes){
				String tempValue = function;
				Timestamp fromDateTmp = tempMap.get("fromDate");
				Timestamp thruDateTmp = tempMap.get("thruDate");					
				
				Map<String, Object> incIndividualTaxMap = 
						PayrollServices.getSalaryAmountList(dctx, UtilMisc.toMap("fromDate", fromDateTmp,
																				 "thruDate", thruDateTmp,
																				 "pdfPartyId", partyId,
																				 "formulaList", formulaList,
																				 "locale", Locale.getDefault()));
				
				List<EntityEmployeeSalary> emplSalary = (List<EntityEmployeeSalary>)incIndividualTaxMap.get("salaryAmountList");
				
				for(EntityEmployeeSalary tempEES: emplSalary){
					for(int j = 0; j < formulaList.size(); j++){
						tempValue = tempValue.replace(formulaList.get(j) + "()", tempEES.getListSalaryAmount().get(j).getAmount());
					}
				}
				tempValue = PayrollEngine.calculateParticipateFunctionForEmployee(dctx, tempValue, fromDateTmp, thruDateTmp, partyId, dataMap).getAmount();
				if(Float.parseFloat(tempValue) > 0){
					returnValue = PayrollUtil.evaluateStringExpression(returnValue + "+" + tempValue);
				}
			}
		}
		return returnValue;
	}
	
	
}
