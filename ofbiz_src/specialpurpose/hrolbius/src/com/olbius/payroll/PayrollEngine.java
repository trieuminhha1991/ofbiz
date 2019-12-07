package com.olbius.payroll;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.PayrollUtil.CallBuildFormulaType;
import com.olbius.payroll.entity.EntityEmplParameters;
import com.olbius.payroll.entity.EntityEmployeeSalary;
import com.olbius.payroll.entity.EntityParameter;
import com.olbius.payroll.entity.EntitySalaryAmount;
import com.olbius.payroll.util.TimekeepingUtils;

public class PayrollEngine {
	/* Input:
	 * Output:
	 * Description: 1. if str
	 *              2. calculate amount by input formula 
	 */
	//FIXME replace by getActualValueOfParameter(List<EntityParameter>, Delegator, String, String, String, Timestamp, Timestamp) function
	/*public static String getActualParameterValue(Delegator delegator, String strParameter, String strEmployeeId, Timestamp tFromDate, Timestamp tThruDate) throws Exception{
		if(strParameter.matches(".*[a-zA-Z]+.*")){
			// check in rank fromDate
			EntityCondition conditionDate1 = PayrollUtil.makeLTEcondition("fromDate", tFromDate);
			// check in rank thruDate 
			EntityCondition conditionDate2 = PayrollUtil.makeGTEcondition("thruDate", tThruDate);			
						
			// if parameter is rateAmount, rateAmount calculate by [EmployeeId,]
			if(strParameter.toLowerCase().contains("positiontype.rateamount")){
				String strPositionType = strParameter.substring(strParameter.indexOf("[") + 1, strParameter.indexOf("]"));
				//EntityExpr entityExpr1 = EntityCondition.makeCondition("partyId",
				//		strEmployeeId);
				EntityExpr entityExpr1 = EntityCondition.makeCondition("emplPositionTypeId",
						strPositionType);
				List<GenericValue> listData = delegator.findList("RateAmount", EntityCondition.makeCondition(UtilMisc.toList(entityExpr1,conditionDate1,conditionDate2),
											EntityJoinOperator.AND), UtilMisc.toSet("rateAmount"), null, null, false);
				if(listData.size() > 1){
					throw new Exception("Employee have more than one position type: " + strPositionType);
				}else if(listData.size() == 1){
					BigDecimal tmpBD = ((BigDecimal)listData.get(0).get("rateAmount"));
					return tmpBD.toString();
				}else{
					return "0";
				}
			}else if(strParameter.toLowerCase().contains("positiontype.grade")){ // if parameter is calculated by pay grade and salary step
				String strPayGrade = strParameter.substring(strParameter.indexOf("[") + 1, strParameter.indexOf("]"));
				String strSalaryStep = strParameter.substring(strParameter.lastIndexOf("[") + 1, strParameter.lastIndexOf("]"));
				String strPositionType = strParameter.substring(strParameter.indexOf("(") + 1, strParameter.indexOf(")"));
				EntityExpr entityExpr1 = EntityCondition.makeCondition("emplPositionTypeId",
						strPositionType);
				EntityExpr entityExpr2 = EntityCondition.makeCondition("payGradeId",
						strPayGrade);
				EntityExpr entityExpr3 = EntityCondition.makeCondition("salaryStepSeqId",
						strSalaryStep);
				List<GenericValue> listData = delegator.findList("EmplPositionTypeRate", EntityCondition.makeCondition(UtilMisc.toList(entityExpr1,entityExpr2,entityExpr3,conditionDate1,conditionDate2),
						EntityJoinOperator.AND), UtilMisc.toSet("emplPositionTypeId"), null, null, false);
				if(listData.size() > 1){
					throw new Exception("Employee have more than one position type: " + strPositionType);
				}else if(listData.size() == 1){ // make sure 
					entityExpr1 = EntityCondition.makeCondition("payGradeId",
							strPayGrade);
					entityExpr2 = EntityCondition.makeCondition("salaryStepSeqId",
							strSalaryStep);
					listData = delegator.findList("SalaryStep", EntityCondition.makeCondition(UtilMisc.toList(entityExpr1,entityExpr2),
							EntityJoinOperator.AND), UtilMisc.toSet("amount"), null, null, false);
					BigDecimal tmpBD = ((BigDecimal)listData.get(0).get("amount"));
					return tmpBD.toString();
				}else{
					return "0";
				}
			}else{
				return "0"; // Other cases will be replaced by zero.
			}
		}else{
			return strParameter;
		}
	}*/
	/* getActualValueOfParameter for emplParameters that value have contains characters but maybe payrollEmplParameters have no characters, 
	 * so this function maybe deleted */   
	public static void getActualValueOfParameter(List<EntityParameter> parameters, Delegator delegator, String strParameter, 
			String strEmployeeId, String code, String periodTypeId, Timestamp tFromDate, Timestamp tThruDate) throws Exception{
		if(strParameter.matches(".*[a-zA-Z]+.*")){
			//EntityCondition conditionDate1 = PayrollUtil.makeLTEcondition("fromDate", tFromDate);
			// check in rank thruDate 
			//EntityCondition conditionDate2 = PayrollUtil.makeGTEcondition("thruDate", tThruDate);
			EntityCondition conditionDate3 = PayrollUtil.makeGTEcondition("thruDate", tFromDate);
			EntityCondition conditionDate4 = PayrollUtil.makeLTEcondition("fromDate", tThruDate);
			String entityName = strParameter.substring(0, strParameter.indexOf("."));
			String[] fieldQuerys = strParameter.substring(strParameter.indexOf(".") + 1, strParameter.indexOf("(")).split("_");
			String selectedField= strParameter.substring(strParameter.indexOf("(") + 1, strParameter.indexOf(")"));
			ModelEntity modelEntity = delegator.getModelEntity(entityName);			
			List<EntityCondition> conditions = FastList.newInstance(); 
				
			for(String field: fieldQuerys){
				String tmpField = field.trim();
				String fieldName = tmpField.substring(0, tmpField.indexOf("["));
				String fieldValue = tmpField.substring(tmpField.indexOf("[") + 1, tmpField.indexOf("]"));
				if(fieldValue.contains("${currentEmpId}")){
					fieldValue = strEmployeeId;
				}
				//ModelField modelField = modelEntity.getField(fieldName);
				Object endValue = modelEntity.convertFieldValue(fieldName, fieldValue, delegator);
				conditions.add(EntityCondition.makeCondition(fieldName, endValue));
			}					
			
			if(modelEntity.areFields(UtilMisc.toSet("fromDate", "thruDate"))){
				conditions.add(conditionDate3);
				conditions.add(conditionDate4);
			}
			
			List<GenericValue> listDataFind = delegator.findList(entityName, EntityCondition.makeCondition(conditions, EntityOperator.AND), 
													null, null, null, false);
			
			
			for(GenericValue data: listDataFind){
				if(UtilValidate.isNotEmpty(selectedField)){
					Object valueSelected = data.get(selectedField);	
					if(UtilValidate.isNotEmpty(valueSelected)){
						EntityParameter parameter= new EntityParameter();
						parameter.setCode(code);
						parameter.setFromDate(data.getTimestamp("fromDate"));
						parameter.setThruDate(data.getTimestamp("thruDate"));
						parameter.setPeriodTypeId(periodTypeId);
						parameter.setValue(String.valueOf(valueSelected));
						parameters.add(parameter);
					}else if(entityName.equals("RateAmount")){
						//if entity is RateAmount and value is null, value will retrieve from SalaryStep entity
						Timestamp rateAmountFromDate = data.getTimestamp("fromDate");
						Timestamp rateAmountThruDate = data.getTimestamp("thruDate");
						String rateTypeId = data.getString("rateTypeId");
						String emplPositionTypeId = data.getString("emplPositionTypeId");
						List<EntityCondition> emplPositionTypeRateConditions = FastList.newInstance();
						emplPositionTypeRateConditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
						emplPositionTypeRateConditions.add(EntityCondition.makeCondition("rateTypeId", rateTypeId));
						if(rateAmountThruDate != null){
							emplPositionTypeRateConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, rateAmountThruDate));
						}					
						
						emplPositionTypeRateConditions.add(PayrollUtil.makeGTEcondition("thruDate", rateAmountFromDate));
						
						List<GenericValue> listSalaryStep = delegator.findList("EmplPositionTypeRate", EntityCondition.makeCondition(emplPositionTypeRateConditions, EntityOperator.AND), 
								null, null, null, false);					
						for(GenericValue tempGv: listSalaryStep){
							EntityParameter parameter= new EntityParameter();
							parameter.setPeriodTypeId(periodTypeId);
							Timestamp eptrFromDate = tempGv.getTimestamp("fromDate");
							Timestamp eptrThruDate = tempGv.getTimestamp("thruDate");
							GenericValue amountGv = delegator.findOne("SalaryStep", 
																	UtilMisc.toMap("salaryStepSeqId", tempGv.getString("salaryStepSeqId"), 
																				   "payGradeId", tempGv.getString("payGradeId")), false);
							if(UtilValidate.isNotEmpty(amountGv)){
								parameter.setValue(amountGv.getBigDecimal("amount").toString());
							}else{
								parameter.setValue("0");
							}
							if(rateAmountFromDate.before(eptrFromDate)){
								parameter.setFromDate(eptrFromDate);
							}else{
								parameter.setFromDate(rateAmountFromDate);
							}
							if(UtilValidate.isEmpty(rateAmountThruDate) || rateAmountThruDate.after(eptrThruDate)){
								parameter.setThruDate(eptrThruDate);
							}else{
								parameter.setThruDate(rateAmountThruDate);
							}
							parameter.setCode(code);
							parameters.add(parameter);
						}
					}else{
						EntityParameter parameter= new EntityParameter();
						parameter.setCode(code);
						parameter.setFromDate(data.getTimestamp("fromDate"));
						parameter.setThruDate(data.getTimestamp("thruDate"));
						parameter.setPeriodTypeId(periodTypeId);
						parameter.setValue("0");
						parameters.add(parameter);
					}
				}else{
					EntityParameter parameter= new EntityParameter();
					parameter.setCode(code);
					parameter.setFromDate(data.getTimestamp("fromDate"));
					parameter.setThruDate(data.getTimestamp("thruDate"));
					parameter.setPeriodTypeId(periodTypeId);
					parameter.setValue("0");
					parameters.add(parameter);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void getRefOrQuotaPayrollParameter(List<EntityParameter> parameters, GenericValue userLogin,
			DispatchContext dctx, String partyId, Timestamp fromDate,
			Timestamp thruDate, GenericValue parameter, TimeZone timeZone) throws GenericEntityException, GenericServiceException {
		// TODO Auto-generated method stub		
		Delegator delegator = dctx.getDelegator();
		String code = parameter.getString("code");
		String type = parameter.getString("type");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if(code.equals("LUONG_CO_BAN")){
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("partyId", partyId);
			serviceCtx.put("userLogin", userLogin);
			Map<String, Object> resultServices = dispatcher.runSync("getPartyPayrollHistory", serviceCtx);
			List<Map<String, Object>> listReturn = (List<Map<String, Object>>)resultServices.get("listPartyPayrollHistory");
			for(Map<String, Object> tempMap: listReturn){
				Timestamp fromDateFromMap = (Timestamp)tempMap.get("fromDate");
				Timestamp thruDateFromMap = (Timestamp)tempMap.get("thruDate");
				if(fromDateFromMap.before(thruDate) && (thruDateFromMap == null || thruDateFromMap.after(fromDate))){
					Timestamp tempFromDate = fromDateFromMap;
					Timestamp tempThruDate = thruDateFromMap;
					if(fromDateFromMap.before(fromDate)){
						tempFromDate = fromDate;
					}
					if(thruDateFromMap == null || thruDateFromMap.after(thruDate)){
						tempThruDate = thruDate;
					}
					EntityParameter newParameters = new EntityParameter();
					newParameters.setCode(code);
					newParameters.setFromDate(tempFromDate);
					newParameters.setThruDate(tempThruDate);
					newParameters.setPeriodTypeId((String)tempMap.get("periodTypeId"));
					newParameters.setValue(((BigDecimal)tempMap.get("rateAmount")).toString());
					parameters.add(newParameters);
				}
			}
		}else if(code.equals("NGAY_CONG_THUC_TE")){
			GenericValue tempParameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			String periodTypeId = tempParameter.getString("periodTypeId");
			EntityParameter newParameter = new EntityParameter();
			newParameter.setCode(code);
			newParameter.setFromDate(fromDate);
			newParameter.setThruDate(thruDate);
			newParameter.setPeriodTypeId(periodTypeId);
			Map<String, Object> results = dispatcher.runSync("getTimeKeepingEmplInPeriod", UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, 
																										  "partyId", partyId, "timeZone", timeZone));
			Float totaldayWork = (Float)results.get("countTimekeeping"); 
			newParameter.setValue(String.valueOf(totaldayWork));
			parameters.add(newParameter);
		}else if("NGAY_CONG_CHUAN".equals(code)){
			float value = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, null, timeZone, true);
			GenericValue tempParameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			EntityParameter newParameter = new EntityParameter();
			newParameter.setCode(code);
			newParameter.setFromDate(fromDate);
			newParameter.setThruDate(thruDate);
			newParameter.setPeriodTypeId(tempParameter.getString("periodTypeId"));
			newParameter.setValue(String.valueOf(value));
			parameters.add(newParameter);
		}else if("LAM_THEM_NGAY_THUONG".equals(code) || "LAM_THEM_NGAY_NGHI".equals(code) || "LAM_THEM_NGAY_LE".equals(code)){
			float value = TimekeepingUtils.getTotalHoursWorkOvertime(dctx, partyId, fromDate, thruDate, code);
			GenericValue tempParameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			EntityParameter newParameter = new EntityParameter();
			newParameter.setCode(code);
			newParameter.setFromDate(fromDate);
			newParameter.setThruDate(thruDate);
			newParameter.setPeriodTypeId(tempParameter.getString("periodTypeId"));
			newParameter.setValue(String.valueOf(value));
			parameters.add(newParameter);
		}else if("MUC_LUONG_DONG_BH".equals(code)){
			/*List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
														EntityOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)));
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> partyInsuranceSalary = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromDate"), null, false);*/
			
			Map<String, Object> resultService =  dispatcher.runSync("getEmplPosTypeInsSalaryInPeriod", 
					UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "partyId", partyId, "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				List<Map<String, Object>> partyInsuranceSalary = (List<Map<String, Object>>)resultService.get("listReturn");
				Timestamp salaryFromDate, salaryThruDate;
				for(Map<String, Object> tempMap: partyInsuranceSalary){
					salaryFromDate = (Timestamp)tempMap.get("fromDate");
					salaryThruDate = (Timestamp)tempMap.get("thruDate");
					if(salaryFromDate.before(fromDate)){
						salaryFromDate = fromDate;
					}
					if(salaryThruDate == null || salaryThruDate.after(thruDate)){
						salaryThruDate = thruDate;
					}
					String value = "0";
					BigDecimal salaryInsurance = (BigDecimal)tempMap.get("insuranceSalary");
					if(salaryInsurance != null){
						value = salaryInsurance.toString();
					}
					String periodTypeId = (String)tempMap.get("periodTypeId");
					EntityParameter newParameter = new EntityParameter();
					newParameter.setCode(code);
					newParameter.setFromDate(salaryFromDate);
					newParameter.setThruDate(salaryThruDate);
					newParameter.setPeriodTypeId(periodTypeId);
					newParameter.setValue(value);
					parameters.add(newParameter);
				}
			}
		}else if("INSURANCE_REF".equals(type)){
			GenericValue insuranceType = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", code), false);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), 
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			EntityCondition commonConds = EntityCondition.makeCondition(conds);
			if(insuranceType != null){
				List<GenericValue> partyParicipateInsurance = delegator.findList("PartyParticipateInsurance", 
						EntityCondition.makeCondition(commonConds,
						EntityOperator.AND,
						EntityCondition.makeCondition("insuranceTypeId", code)), null, UtilMisc.toList("-fromDate"), null, false);
				String value = "0", valueEmployer = "0";
				if(UtilValidate.isNotEmpty(partyParicipateInsurance)){
					String statusId = partyParicipateInsurance.get(0).getString("statusId");
					if("PARTICIPATING".equals(statusId)){
						double employeeRate = insuranceType.getDouble("employeeRate");
						value = String.valueOf(employeeRate);
						double employerRate = insuranceType.getDouble("employerRate");
						valueEmployer = String.valueOf(employerRate);
					}
				}
				EntityParameter newParameter = new EntityParameter();
				newParameter.setCode(code);
				newParameter.setValue(value);
				newParameter.setFromDate(fromDate);
				newParameter.setThruDate(thruDate);
				newParameter.setPeriodTypeId("NA");
				parameters.add(newParameter);
				
				EntityParameter newParameterEmployer = new EntityParameter();
				newParameterEmployer.setCode(code + "_CTY");
				newParameterEmployer.setValue(valueEmployer);
				newParameterEmployer.setFromDate(fromDate);
				newParameterEmployer.setThruDate(thruDate);
				newParameterEmployer.setPeriodTypeId("NA");
				parameters.add(newParameterEmployer);
			}
		}
	}
	 
	
	/* Input:
	 * Output:
	 * Description: 1. get all assigned employee's parameters
	 *              2. calculate amount by input formula 
	 * 
	 */
	// FIXME update below method by using calculateParticipateFunctionForEmployee method
	/*public static List<EntitySalaryAmount> calculateParticipateFunction(DispatchContext ctx, String strFunction, Timestamp tFromDate, 
																		Timestamp tThruDate, String strEmployeeId) throws Exception{
		Delegator delegator = ctx.getDelegator();
		List<EntitySalaryAmount> listReturn = null;
		try {
			// get data by using OFBiz persistent layer
			// check in rank fromDate
			EntityCondition conditionDate1 = PayrollUtil.makeGTEcondition("date", tFromDate);
			//check in rank thruDate
			EntityCondition conditionDate2 = PayrollUtil.makeGTEcondition("date", tThruDate);
			
			List<GenericValue> listData = null;
			if(strEmployeeId != null && !strEmployeeId.isEmpty()){ // if calculate for only employee
				EntityExpr entityExpr1 = EntityCondition.makeCondition("partyId",
						strEmployeeId);
				listData = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(UtilMisc.toList(entityExpr1,conditionDate1,conditionDate2),EntityJoinOperator.AND), UtilMisc.toSet("code","value","actualPercent","partyId","type"), null, null, false);
			}else{
				listData = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(UtilMisc.toList(conditionDate1,conditionDate2),EntityJoinOperator.AND), UtilMisc.toSet("code","value","actualPercent","partyId","type"), UtilMisc.toList("partyId"), null, false);
			}
			List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
			String strTMP = strFunction.replaceAll("\\s+",""); // remove all space characters
			strTMP = strTMP.replace("(", "");
			strTMP = strTMP.replace(")", "");
			String[] strs = strTMP.split("[\\+\\-\\*\\/\\(\\)]");
			String strPreviousPartyId = "";
			Map<String,String> tmpMap = null;
			int iCount = 0;
			for(GenericValue genericValue:listData){
				iCount = 1;
				String strCurrentPartyId = genericValue.getString("partyId");
				String strTmpAP = genericValue.getString("actualPercent");
				if(!strCurrentPartyId.equals(strPreviousPartyId)){
					if(tmpMap != null){ // check for first loop and next employee is detected
						listMap.add(tmpMap);
					}
					tmpMap = new HashMap<String, String>();
					tmpMap.put("partyId", strCurrentPartyId);
					for(String str:strs){
						tmpMap.put(str,"0"); // aplly all, default value = 0 if employee does not assign this parameters
					}
					strPreviousPartyId = strCurrentPartyId;
				}
				// put code and value to map
				if(strTmpAP == null || strTmpAP.isEmpty()){ // check if actualPercent is null 
					tmpMap.put(genericValue.getString("code"), getActualParameterValue(delegator, genericValue.getString("value"), strEmployeeId, tFromDate, tThruDate));
				}else if(strTmpAP != null && strTmpAP.length() > 2){ // if actualPercent is greater than 100%
					throw new Exception("Wrong actual percent for employee: " + strEmployeeId);
				}
				else{
					tmpMap.put(genericValue.getString("code"), "(100-" + genericValue.getString("actualPercent") + "/100)*" + getActualParameterValue(delegator, genericValue.getString("value"), strEmployeeId, tFromDate, tThruDate));
				}
			}
			// add last record, add partyId to map, a map can have some parameters but have only one party
			if(iCount == 1){
				tmpMap.put("partyId", strPreviousPartyId);
				listMap.add(tmpMap);
			}
			iCount = 0;
			if(!listMap.isEmpty()){
				listReturn = new ArrayList<EntitySalaryAmount>(); 
				strPreviousPartyId = listMap.get(0).get("partyId");
				String strTmpFunction = strFunction;
				for(Map<String,String> map:listMap){
					iCount = 1;
					String strCurrentPartyId = map.get("partyId");
					EntitySalaryAmount entity = new EntitySalaryAmount();
					if(!strCurrentPartyId.equals(strPreviousPartyId)){ // check for first record and next employee
						entity.setAmount(PayrollUtil.evaluateStringExpression(strTmpFunction));
						entity.setPartyId(strPreviousPartyId);
						listReturn.add(entity);
						strTmpFunction = strFunction;
						strPreviousPartyId = strCurrentPartyId;
					}
					for (Map.Entry<String, String> entry : map.entrySet()) {
						strTmpFunction = strTmpFunction.replace(entry.getKey(), entry.getValue());
					}
				}
				// calculate and add last record
				if(iCount == 1){
					EntitySalaryAmount entity = new EntitySalaryAmount();
					entity.setAmount(PayrollUtil.evaluateStringExpression(strTmpFunction));
					entity.setPartyId(strPreviousPartyId);
					listReturn.add(entity);
				}
			}else{
				return null;
			}
//			conn.close();
		} catch (Exception exc) {
			throw exc;
		}
		return listReturn;
	}*/
	/* Input:
	 * Output:
	 * Description: 1. get all employee's parameters
	 *              2. calculate amount by input formula 
	 * 
	 */
	public static EntitySalaryAmount calculateParticipateFunctionForEmployee(DispatchContext ctx, String strFunction, Timestamp tFromDate, 
																			 Timestamp tThruDate, String strEmployeeId, Map<String,String> dataMap) throws Exception{
		//Delegator delegator = ctx.getDelegator();
		EntitySalaryAmount returnSalaryAmount = null;
		String strTMP = strFunction.replaceAll("\\s+",""); // remove all space characters
		strTMP = strTMP.replace("(", "");
		strTMP = strTMP.replace(")", "");
		String[] strs = strTMP.split("[\\+\\-\\*\\/\\(\\)]");
		// init map with default value
		for(String str:strs){
			// check if str was not existed in cacheMap by checking contain any alphabet character.
			if(str.matches(".*[a-zA-Z]+.*") && !dataMap.containsKey(str)){
				dataMap.put(str,"0"); // aplly all, default value = 0 if employee does not assign this parameters
			}
		}
		// end 
		if(!dataMap.isEmpty()){
			returnSalaryAmount = new EntitySalaryAmount(); 
			String strTmpFunction = strFunction;
			strTmpFunction = strFunction;
			for (Map.Entry<String, String> entry : dataMap.entrySet()) {
				strTmpFunction = strTmpFunction.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
			}
			String amount = PayrollUtil.evaluateStringExpression(strTmpFunction);
			if("NaN".equals(amount)){
				amount = "0";
			}
			returnSalaryAmount.setAmount(amount);
			returnSalaryAmount.setPartyId(strEmployeeId);
		}else{
			return null;
		}
		return returnSalaryAmount;
	}
	
	/* Input: input formula will be processed 
	 * Output: replace all current formula with its content.
	 * Description: Replace formula with formula's content
	 * periodTypeId is cycle calculate salary (Yearly, monthly, quarterly, daily), so fromDate, thruDate is between start of period and end of period
	 */
	public static List<EntityEmployeeSalary> getSalaryList(DispatchContext ctx, GenericValue userLogin, String periodTypeId, List<GenericValue> emplList, List<String> listFormula,
			Timestamp tFromDate, Timestamp tThruDate, Locale locale, TimeZone timeZone) throws Exception{
		List<EntityEmployeeSalary> listEntitySalaryAmount = new ArrayList<EntityEmployeeSalary>();
		List<GenericValue> listEmployee = emplList;
		Delegator delegator = ctx.getDelegator();
		// Formula cache Map
    	Map<String,String> mapFormulaCache = null;
		if(listEmployee == null || listEmployee.isEmpty()){
    		// get all employees who has been assign any parameters
    		EntityFindOptions findOption = new EntityFindOptions(true,EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
    		listEmployee = delegator.findList("PayrollEmplParameters", null, UtilMisc.toSet("partyId"), null, findOption, false);
		}
		EntityEmplParameters emplParamtersTmp = null;
		Map<String,String> mapParamtersEmployee = null;
		//if(strEmployeeId == null || strEmployeeId.isEmpty()){
		if((listEmployee == null) || (listEmployee.isEmpty())){
			return listEntitySalaryAmount; // end of service
		}else{
			for(GenericValue generic:listEmployee){
				mapFormulaCache = new HashMap<String,String>();
				emplParamtersTmp = PayrollDataPreparation.getEmployeeParametersCache(ctx, userLogin, generic.get("partyId").toString(), tFromDate, tThruDate, timeZone);
				mapParamtersEmployee = PeriodWorker.getParameterByPeriod(ctx, periodTypeId, emplParamtersTmp, tFromDate, tThruDate, timeZone, locale);
				EntityEmployeeSalary entityEmployeeSal = new EntityEmployeeSalary();
				for(String str:listFormula){
					entityEmployeeSal.getListSalaryAmount().add(PayrollEngine.calculateParticipateFunctionForEmployee(ctx, PayrollEngine.buildFormula(ctx,str + "()",tFromDate,tThruDate,PayrollUtil.CallBuildFormulaType.NATIVE,generic.getString("partyId"),mapFormulaCache,mapParamtersEmployee),
																													  tFromDate,tThruDate,generic.getString("partyId"),mapParamtersEmployee));
				}
				entityEmployeeSal.setMapFormulaVqalue(mapFormulaCache);
				listEntitySalaryAmount.add(entityEmployeeSal);
			}
		}
		/*}else{
			mapFormulaCache = new HashMap<String,String>();
			emplParamtersTmp = PayrollDataPreparation.getEmployeeParametersCache(ctx, strEmployeeId, tFromDate, tThruDate);
			mapParamtersEmployee = PeriodWorker.getParameterByPeriod(ctx, periodTypeId,emplParamtersTmp, tFromDate, tThruDate, timeZone, locale);
			EntityEmployeeSalary entityEmployeeSal = new EntityEmployeeSalary();
			for(String str:listFormula){
				entityEmployeeSal.getListSalaryAmount().add(PayrollEngine.calculateParticipateFunctionForEmployee(ctx, PayrollEngine.buildFormula(ctx,str + "()",tFromDate,tThruDate,PayrollUtil.CallBuildFormulaType.NATIVE,strEmployeeId,mapFormulaCache,mapParamtersEmployee),tFromDate,tThruDate,strEmployeeId,mapParamtersEmployee));
			}
			entityEmployeeSal.setMapFormulaVqalue(mapFormulaCache);
			listEntitySalaryAmount.add(entityEmployeeSal);
		}*/
		
		return listEntitySalaryAmount;
	}
	
	/* Input: input formula will be processed 
	 * Output: replace all current formula with its content.
	 * Description: Replace formula with formula's content
	 */
	// FIXME Detect Recursive detection
	// FIXME Check formula format
	// TODO support input parameters for formula
	public static String buildFormula(DispatchContext ctx, String strGlobalFormula,Timestamp ttFromDate, Timestamp ttThruDate,
									  PayrollUtil.CallBuildFormulaType callType, String strPaytyId, Map<String,String> mapCache, Map<String,String> dataMap) throws Exception{
		if(strGlobalFormula.contains("()")){
			String strReturn = strGlobalFormula;
			String[] strs = strGlobalFormula.split("[\\+\\-\\*\\/\\%]"); // split parameters and formulas
			for(String str:strs){
				if(str.contains("()")){
					str = str.trim();
					str = str.replace("(","");
					str = str.replace(")","");	
					// if existed in Map
					if(mapCache.containsKey(str + "()") /*&& !mapCache.get(str + "()").matches(".*[a-zA-Z]+.*")*/){
						strReturn = strReturn.replace(str + "()","(" + mapCache.get(str + "()") + ")");
					}else{
						// str = str.substring(0,str.length()-2);// remove ()
						// get formula
						Delegator delegator = ctx.getDelegator();
						Map<String,String> mapParameter = new HashMap<String, String>();
						mapParameter.put("code", str);
						try {
							GenericValue tmpGV = delegator.findOne("PayrollFormula", mapParameter, false);
							String strFunctionType = (String)tmpGV.get("functionType");
							// if formula is general type(general type use for calculating tax, insurance)
							if(strFunctionType != null && strFunctionType.equals("COMPLEX") && callType!= PayrollUtil.CallBuildFormulaType.PRIMITIVE){
								//calculate income tax individual
								/*String strResult = TaxCalculator.calculateIncomeIndividualTax(ctx, str, ttFromDate, ttThruDate, strPaytyId, mapCache, dataMap);
								strReturn = strReturn.replace(str + "()","(" + strResult + ")");
								mapCache.put(str + "()", strResult);*/
								
							}else if(strFunctionType != null && strFunctionType.equals("LIMITVALUE") && callType!= PayrollUtil.CallBuildFormulaType.PRIMITIVE){
								EntitySalaryAmount ettMainValue = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, PayrollEngine.buildFormula(ctx,str + "()",ttFromDate,ttThruDate,PayrollUtil.CallBuildFormulaType.PRIMITIVE,strPaytyId,mapCache,dataMap),ttFromDate,ttThruDate,strPaytyId,dataMap);
								String strMaxValue = (String)tmpGV.get("maxValue");
								String strMainValue = "";
								if(strMaxValue.matches(".*[a-zA-Z]+.*")){ // contain alphabet character
									EntitySalaryAmount ettMaxValue = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, PayrollEngine.buildFormula(ctx,strMaxValue,ttFromDate,ttThruDate,PayrollUtil.CallBuildFormulaType.PRIMITIVE,strPaytyId,mapCache,dataMap),ttFromDate,ttThruDate,strPaytyId,dataMap);
									strMainValue = ettMaxValue.getAmount();
								}else{
									strMainValue = PayrollUtil.evaluateStringExpression(strMaxValue);
								}
								strMainValue = strMainValue.replace("-", ""); // strMainValue = abs(strMainValue)
								if(Double.valueOf(strMainValue) < Double.valueOf(ettMainValue.getAmount())){
									strReturn = strReturn.replace(str + "()","(" + strMainValue + ")");
									// push to cache
									mapCache.put(str + "()", strMainValue);
								}else{
									strReturn = strReturn.replace(str + "()","(" + ettMainValue.getAmount() + ")");
									// push to cache
									mapCache.put(str + "()", ettMainValue.getAmount());
								}
							}else if(str.equals("THUE_THU_NHAP")){
								String strResult = TaxCalculator.calculateIncomeIndividualTax(ctx, str, ttFromDate, ttThruDate, strPaytyId, mapCache, dataMap);
								strReturn = strReturn.replace(str + "()","(" + strResult + ")");
								mapCache.put(str + "()", strResult);
							}else{
								String tmpStr = (String)tmpGV.get("function");
								if(tmpStr.contains("if")){
									//function contain if-else statement, so get actual return value
									tmpStr = getReturnValueFromScriptCode(ctx, str, ttFromDate, ttThruDate, callType, strPaytyId,mapCache,dataMap);
								}
								if(tmpStr.contains("()")){
									String strTMPResult = buildFormula(ctx, tmpStr, ttFromDate, ttThruDate, callType, strPaytyId,mapCache,dataMap);
									strReturn = strReturn.replace(str + "()","(" + strTMPResult + ")");
									// push to cache
									mapCache.put(str + "()", strTMPResult);
								}else{
									EntitySalaryAmount ettTMPResult = PayrollEngine.calculateParticipateFunctionForEmployee(ctx, tmpStr,ttFromDate,ttThruDate,strPaytyId,dataMap);
									strReturn = strReturn.replace(str + "()","(" + ettTMPResult.getAmount() + ")");
									// push to cache
									mapCache.put(str + "()", ettTMPResult.getAmount());
								}
							}
						} catch (GenericEntityException e) {
							throw e;
						}
					}
				}
			}
			return strReturn; 
		}
		return strGlobalFormula;
	}
	
	private static String getReturnValueFromScriptCode(DispatchContext ctx,
			String code, Timestamp ttFromDate, Timestamp ttThruDate,
			CallBuildFormulaType callType, String strPaytyId,
			Map<String, String> mapCache, Map<String, String> dataMap) throws Exception {
		// TODO Auto-generated method stub
		Delegator delegator = ctx.getDelegator();
		GenericValue formula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
		String functionExpr = formula.getString("function");
		String functionRelated = formula.getString("functionRelated");
		ScriptContext context = new SimpleScriptContext();
		Map<String, String> tempMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(functionRelated)){
			String []functionAndParam = functionRelated.split(",");
			for(String tempFunction: functionAndParam){
				tempFunction = tempFunction.trim();				
				String strReturn = buildFormula(ctx, tempFunction, ttFromDate, ttThruDate, callType, strPaytyId, mapCache, dataMap);
				String calcValue = calculateFunction(strReturn, dataMap);
				if(tempFunction.contains("()")){
					//remove "()" characters in "if()" statement, replace with "F_" prefix
					String tempRemoveParenthesis = "F_" + tempFunction.replace("()", "");
					functionExpr = functionExpr.replace(tempFunction, tempRemoveParenthesis);
					//if tempFunction in return statement of functionExpr, it will be replaced with "F_" in prefix, so put tempFunction and tempRemoveParenthesis in map, 
					//in order to, replace to tempFunction with tempRemoveParenthesis after calculate value of tempFunction in functionExpr 
					tempMap.put(tempRemoveParenthesis, tempFunction);
					tempFunction = tempRemoveParenthesis;
				}
				if(calcValue != null){
					context.setAttribute(tempFunction, Float.parseFloat(calcValue), ScriptContext.ENGINE_SCOPE);
				}
			}
		}
		//replace character have prefix "F_" with origin characters
		String retValue = PayrollUtil.evaluateFunctionExpression(functionExpr, context);
		for(Entry<String, String> entry: tempMap.entrySet()){
			retValue = retValue.replace(entry.getKey(), entry.getValue());
		}
		return retValue;
	}

	public static String calculateFunction(String strFunction,
			Map<String, String> dataMap) throws ScriptException {
		// TODO Auto-generated method stub
		String strTMP = strFunction.replaceAll("\\s+",""); // remove all space characters
		strTMP = strTMP.replace("(", "");
		strTMP = strTMP.replace(")", "");
		String[] strs = strTMP.split("[\\+\\-\\*\\/\\(\\)]");
		// init map with default value
		for(String str:strs){
			// check if str was not existed in cacheMap by checking contain any alphabet character.
			if(str.matches(".*[a-zA-Z]+.*") && !dataMap.containsKey(str)){
				dataMap.put(str,"0"); // aplly all, default value = 0 if employee does not assign this parameters
			}
		}
		if(!dataMap.isEmpty()){
			String strTmpFunction = strFunction;
			strTmpFunction = strFunction;
			for (Map.Entry<String, String> entry : dataMap.entrySet()) {
				strTmpFunction = strTmpFunction.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
			}
			String amount = PayrollUtil.evaluateStringExpression(strTmpFunction);
			if("NaN".equals(amount)){
				amount = "0";
			}
			return amount;
		}else{
			return null;
		}
	}
}
