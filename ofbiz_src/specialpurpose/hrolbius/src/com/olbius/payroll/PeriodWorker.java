package com.olbius.payroll;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.util.DateUtil;
import com.olbius.payroll.entity.EntityEmplParameters;
import com.olbius.payroll.entity.EntityParameter;

// TODO important feature
public class PeriodWorker {
	public static final String MODULE = PeriodWorker.class.getName();
	public static final String NA = "NA";
	
	/**
	 * Convert out put of getEmployeeParametersCache to parameters with hour
	 * period
	 * 
	 * @param dpct
	 * @param parameters
	 *            : Out put of getEmployeeParametersCache
	 * @return Map parameters with hour period
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public static Map<String, String> getParameterByPeriod(
			DispatchContext dpct, String periodTypeIdCalcSalary, EntityEmplParameters emplParamters, 
			Timestamp fromDate, Timestamp thruDate, TimeZone timeZone, Locale locale)
			throws NumberFormatException, Exception {
		Delegator delegator = dpct.getDelegator();
		
		//TODO Need configuration Parameters
		//Map<Enum<PeriodEnum>, Long> periodTypeLength = PayrollUtil.getPeriodLength(delegator);
		Map<String, String> result = new HashMap<String, String>();
			
		// Get Employee Id
		String partyId = emplParamters.getPartyId();
		//get periodType of frequency salary paid
		PeriodEnum periodCalcSalaryEnum = PeriodEnum.NA;
		if("YEARLY".equals(periodTypeIdCalcSalary)){
			periodCalcSalaryEnum = PeriodEnum.YEARLY;
		}else if("QUARTERLY".equals(periodTypeIdCalcSalary)){
			periodCalcSalaryEnum = PeriodEnum.QUARTERLY;
		}else if("MONTHLY".equals(periodTypeIdCalcSalary)){
			periodCalcSalaryEnum = PeriodEnum.MONTHLY;
		}else if("WEEKLY".equals(periodTypeIdCalcSalary)){
			periodCalcSalaryEnum = PeriodEnum.WEEKLY;
		}else if("DAILY".equals(periodTypeIdCalcSalary)){
			periodCalcSalaryEnum = PeriodEnum.DAILY;
		}
		
		List<EntityParameter> parameters = emplParamters.getEmplParameters(); 
		for (EntityParameter element : parameters) {
			String code = element.getCode();
			String value = element.getValue();
			Timestamp tfromDate = element.getFromDate();
			//if fromDate == null, employee have no this parameters, so get value parameters from PayrollParameters
			if(tfromDate == null){
				//Check parameter is default
				GenericValue parameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
				//String calcOnActual = parameter.getString("calcOnActual");
				String parameterType = parameter.getString("type");
				PeriodEnum period = PeriodEnum.NA;
				if("YEARLY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.YEARLY;
				}else if("QUARTERLY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.QUARTERLY;
				}else if("MONTHLY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.MONTHLY;
				}else if("WEEKLY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.WEEKLY;
				}else if("DAILY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.DAILY;
				}/*else if("HOURLY".equals(parameter.getString("periodTypeId"))){
					period = PeriodEnum.HOURLY;
				}*/
				if("QUOTA".equals(parameterType) || "QUOTA_REF".equals(parameterType)){
					value = PayrollWorker.getQuotaParametersValue(dpct, partyId, period, periodCalcSalaryEnum, fromDate, thruDate, null, null, value, locale, timeZone);
					result.put(code, value);
				}else if("CONST".equals(parameterType)){
					value = PayrollWorker.getConstantParametersValue(dpct, partyId, fromDate, thruDate, parameter.getString("periodTypeId"), periodTypeIdCalcSalary, value, locale, timeZone);
					result.put(code, value);
				}else{									
					switch (period) {
						case YEARLY:
							Timestamp yearStart = UtilDateTime.getYearStart(fromDate);
							Timestamp yearEnd = UtilDateTime.getYearEnd(fromDate, timeZone, locale);
							value = PayrollUtil.getActualValueByPeriod(dpct, partyId, fromDate, thruDate, yearStart, yearEnd, value, period, locale, timeZone);
							result.put(code, value);
							break;
						case QUARTERLY:
							Timestamp quarterStart = DateUtil.getQuarterEnd(fromDate, locale, timeZone);
							Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDate, locale, timeZone);
							value = PayrollUtil.getActualValueByPeriod(dpct, partyId, fromDate, thruDate, quarterStart, quarterEnd, value, period, locale, timeZone);
							result.put(code, value);
							break;
						case MONTHLY:
							Timestamp monthStart = UtilDateTime.getMonthStart(fromDate);
							Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDate, timeZone, locale);
							value = PayrollUtil.getActualValueByPeriod(dpct, partyId, fromDate, thruDate, monthStart, monthEnd, value, period, locale, timeZone);
							result.put(code, value);
							break;
						case WEEKLY:
							Timestamp weekStart = UtilDateTime.getWeekStart(fromDate);
							Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDate, timeZone, locale);
							value = PayrollUtil.getActualValueByPeriod(dpct, partyId, fromDate, thruDate, weekStart, weekEnd, value, period, locale, timeZone);
							result.put(code, value);
							break;
						case DAILY:
							Timestamp dayStart = UtilDateTime.getWeekStart(fromDate);
							Timestamp dayEnd = UtilDateTime.getWeekEnd(fromDate, timeZone, locale);
							value = PayrollUtil.getActualValueByPeriod(dpct, partyId, fromDate, thruDate, dayStart, dayEnd, value, period, locale, timeZone);
							result.put(code, value);
							break;
						//paid hourly is pending	
						/*case HOURLY:
							value = PayrollUtil.evaluateStringExpression(value + "*" + DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
							result.put(code, value);
							break;*/
						case NA:
							result.put(code, value);
							break;
						default:
							throw new Exception("Parameter Period is not valid");
					}
				}
			}
		}
		
		for (EntityParameter element : parameters) {
			String code = element.getCode();
			Timestamp tfromDate = element.getFromDate();
			Timestamp tThruDate = element.getThruDate();
			String value = element.getValue();//"0"
			GenericValue payrollParameters = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			if(payrollParameters == null){
				System.err.println("code error: " + code);
			}
			String parameterType = payrollParameters.getString("type");
			if(tfromDate != null ){
				//Check parameter is not default
				//GenericValue parameter = delegator.findOne("PayrollEmplParameters", UtilMisc.toMap("code", code, "partyId", emplParamters.getPartyId(), "fromDate", tfromDate), false);				
				PeriodEnum period = PeriodEnum.NA;
				if("YEARLY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.YEARLY;
				}else if("QUARTERLY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.QUARTERLY;
				}else if("MONTHLY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.MONTHLY;
				}else if("WEEKLY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.WEEKLY;
				}else if("DAILY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.DAILY;
				}/*else if("HOURLY".equals(element.getPeriodTypeId())){
					period = PeriodEnum.HOURLY;
				}*/
				if("QUOTA".equals(parameterType) || "QUOTA_REF".equals(parameterType)){
					value = PayrollWorker.getQuotaParametersValue(dpct, partyId, period, periodCalcSalaryEnum, fromDate, thruDate, tfromDate, tThruDate, value, locale, timeZone);
				}else if("CONST".equals(parameterType)){
					value = PayrollWorker.getConstantParametersValue(dpct, partyId, fromDate, thruDate, element.getPeriodTypeId(), periodTypeIdCalcSalary, value, locale, timeZone);
				}else{
					value = PayrollWorker.getBaseOnWorkDayParametersValue(dpct, fromDate, thruDate, tfromDate, tThruDate, partyId, period, timeZone, locale, element.getValue());
				}
				
				//Total in same code
				for (EntityParameter elementTmp : parameters) {
					String codeTmp = elementTmp.getCode();
					Timestamp tfromDateTmp = elementTmp.getFromDate();
					Timestamp tThruDateTmp = elementTmp.getThruDate();
					String valueTmp = elementTmp.getValue();//"0"
					if(tfromDateTmp != null && code.equals(codeTmp) && element != elementTmp){
	
						//Check parameter is not default
						//GenericValue parameterTmp = delegator.findOne("PayrollEmplParameters", UtilMisc.toMap("code", code, "partyId", emplParamters.getPartyId(), "fromDate", tfromDateTmp), false);
						GenericValue payrollParameterTemp = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", codeTmp), false);
						String typeTmp = payrollParameterTemp.getString("type");
						PeriodEnum periodTmp = PeriodEnum.NA;
						if("YEARLY".equals(elementTmp.getPeriodTypeId())){
							periodTmp = PeriodEnum.YEARLY;
						}else if("QUARTERLY".equals(elementTmp.getPeriodTypeId())){
							periodTmp = PeriodEnum.QUARTERLY;
						}else if("MONTHLY".equals(elementTmp.getPeriodTypeId())){
							periodTmp = PeriodEnum.MONTHLY;
						}else if("WEEKLY".equals(elementTmp.getPeriodTypeId())){
							periodTmp = PeriodEnum.WEEKLY;
						}else if("DAILY".equals(elementTmp.getPeriodTypeId())){
							periodTmp = PeriodEnum.DAILY;
						}
						if("QUOTA".equals(typeTmp) || "QUOTA_REF".equals(typeTmp)){
							PayrollWorker.getQuotaParametersValue(dpct, partyId, periodTmp, periodCalcSalaryEnum, fromDate, thruDate, tfromDateTmp, tThruDateTmp, valueTmp, locale, timeZone);
						}else if("CONST".equals(typeTmp)){
							valueTmp = PayrollWorker.getConstantParametersValue(dpct, partyId, fromDate, thruDate, elementTmp.getPeriodTypeId(), periodTypeIdCalcSalary, valueTmp, locale, timeZone);
						}else{
							valueTmp = PayrollWorker.getBaseOnWorkDayParametersValue(dpct, fromDate, thruDate, tfromDateTmp, tThruDateTmp, partyId, periodTmp, timeZone, locale, elementTmp.getValue());
						}
						value = PayrollUtil.evaluateStringExpression(value + "+" + valueTmp);
					}
				}
				result.put(code, value);
			}
		}
		result.put("partyId", partyId);
		return result;
	}
		
}
