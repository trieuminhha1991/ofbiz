package com.olbius.payroll;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.script.ScriptException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;

import com.olbius.payroll.util.TimekeepingUtils;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyHelper;
import com.olbius.util.PartyUtil;

public class PayrollWorker {
	public static String getQuotaParametersValue(DispatchContext dctx, String partyId, PeriodEnum periodEnum, PeriodEnum periodCalcSalaryEnum,
			Timestamp fromDate, Timestamp thruDate, Timestamp dateParamEffective, Timestamp dateParamExpire,  String value, Locale locale, TimeZone timeZone) throws GenericEntityException, GenericServiceException, ScriptException{
		String retValue = "0";
		// ngay bat dau cua chu ky tinh luong, vd: chu ky la tuan thi ngay bat dau la thu 2, chu ky la thang thi ngay bat dau la ngay mung 1
		Timestamp dateStartSalaryPeriod = getStartEndTimestampPeriod(fromDate, locale, timeZone, periodCalcSalaryEnum, DateUtil.STARTMODE, 0);
		// ngay ket thuc cua chu ky tinh luong
		Timestamp dateEndSalaryPeriod = getStartEndTimestampPeriod(thruDate, locale, timeZone, periodCalcSalaryEnum, DateUtil.ENDMODE, 0);
		if(dateParamEffective != null && dateParamEffective.after(dateStartSalaryPeriod)){
			dateStartSalaryPeriod = dateParamEffective;
		}
		if(dateParamExpire != null && dateParamExpire.before(dateEndSalaryPeriod)){
			dateEndSalaryPeriod = dateParamExpire;
		}
		Timestamp dateStartParamByEffectiveDate = getStartEndTimestampPeriod(dateStartSalaryPeriod, locale, timeZone, periodEnum, DateUtil.STARTMODE, 0);
		Timestamp dateEndParamByEffictivedate = getStartEndTimestampPeriod(dateStartSalaryPeriod, locale, timeZone, periodEnum, DateUtil.ENDMODE, 0);
		while(dateStartParamByEffectiveDate.before(dateEndSalaryPeriod)){
			Float totalDayWork1 = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, dateStartParamByEffectiveDate, dateEndParamByEffictivedate, locale, timeZone); 
			if(DateUtil.beforeOrEquals(dateEndParamByEffictivedate, dateEndSalaryPeriod) && dateStartSalaryPeriod.after(dateStartParamByEffectiveDate)){
				Float totalDayWork2 = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, dateStartSalaryPeriod, dateEndParamByEffictivedate, locale, timeZone);
				retValue = PayrollUtil.evaluateStringExpression(retValue + "+" + value + "*" + totalDayWork2 + "/" + totalDayWork1);
			}else if(DateUtil.beforeOrEquals(dateEndParamByEffictivedate, dateEndSalaryPeriod) && DateUtil.beforeOrEquals(dateStartSalaryPeriod, dateStartParamByEffectiveDate)){
				retValue = PayrollUtil.evaluateStringExpression(retValue + "+" + value);
			}else if(DateUtil.afterOrEquals(dateStartSalaryPeriod,dateStartParamByEffectiveDate) && dateEndParamByEffictivedate.after(dateEndSalaryPeriod)){
				Float totalDayWork2 = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, dateStartSalaryPeriod, dateEndSalaryPeriod, locale, timeZone); 
				retValue = PayrollUtil.evaluateStringExpression(retValue + "+" + value + "*" + totalDayWork2 + "/" + totalDayWork1);
			}else if(dateEndParamByEffictivedate.after(dateEndSalaryPeriod)){
				Float totalDayWork2 = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, dateStartParamByEffectiveDate, dateEndSalaryPeriod, locale, timeZone);
				retValue = PayrollUtil.evaluateStringExpression(retValue + "+" + value + "*" + totalDayWork2 + "/" + totalDayWork1);
			}
			dateStartParamByEffectiveDate = getStartEndTimestampPeriod(dateStartParamByEffectiveDate, locale, timeZone, periodEnum, DateUtil.STARTMODE, 1);
			dateEndParamByEffictivedate = getStartEndTimestampPeriod(dateEndParamByEffictivedate, locale, timeZone, periodEnum, DateUtil.ENDMODE, 1);
		}
		
		return retValue;
	}
	
	public static Timestamp getStartEndTimestampPeriod(Timestamp timestamp,
			Locale locale, TimeZone timeZone, PeriodEnum period,
			String mode, int later) {
		Timestamp retValue = timestamp;
		switch (period) {
			case YEARLY:
				retValue = UtilDateTime.getYearStart(timestamp, 0, later);
				if(mode.equals(DateUtil.ENDMODE)){
					retValue = UtilDateTime.getYearEnd(retValue, timeZone, locale);
				}
				break;
			case QUARTERLY:
				retValue = DateUtil.getQuarterStart(timestamp, locale, timeZone, later);
				if(mode.equals(DateUtil.ENDMODE)){
					retValue = DateUtil.getQuarterEnd(retValue, locale, timeZone);
				}
				break;
			case MONTHLY:
				retValue = UtilDateTime.getMonthStart(timestamp, 0, later);
				if(mode.equals(DateUtil.ENDMODE)){
					retValue = UtilDateTime.getMonthEnd(retValue, timeZone, locale);
				}
				break;
			case WEEKLY:
				retValue = UtilDateTime.getWeekStart(timestamp, 0, later);
				if(mode.equals(DateUtil.ENDMODE)){
					retValue = UtilDateTime.getWeekEnd(retValue, timeZone, locale);
				}
				break;
			case DAILY:
				retValue = UtilDateTime.getDayStart(timestamp, later);
				if(mode.equals(DateUtil.ENDMODE)){
					retValue = UtilDateTime.getDayEnd(retValue, timeZone, locale);
				}
				break;
			default:
				break;
		}				
		return retValue;
	}

	public static String getConstantParametersValue(DispatchContext dctx, String partyId, Timestamp fromDate, Timestamp thruDate,
			String periodTypeIdOfParam, String periodTypeIdCalcSalaryEnum, String value, Locale locale, TimeZone timeZone) throws ScriptException, GenericEntityException, GenericServiceException {
		String retValue = value;
		float convertValue = convertPeriodEnum(dctx, partyId, periodTypeIdOfParam, periodTypeIdCalcSalaryEnum, fromDate, thruDate, locale, timeZone);
		retValue = PayrollUtil.evaluateStringExpression(value + "/" + String.valueOf(convertValue));
		return retValue;
	}

	private static float convertPeriodEnum(DispatchContext dctx, String partyId, String mainPeriodTypeId, String convertPeriodTypeId,
			Timestamp fromDate, Timestamp thruDate, Locale locale, TimeZone timeZone)throws GenericEntityException, GenericServiceException{
		float retValue = 1f;
		if(mainPeriodTypeId.equals(convertPeriodTypeId)){
			retValue = 1f;
		}else{
			if("DAILY".equals(mainPeriodTypeId)){
				float dayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
				if(dayWork != 0){
					return 1f/dayWork;
				}
			}else if("DAILY".equals(convertPeriodTypeId)){
				float dayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
				if(dayWork != 0){
					return dayWork;
				}
			}else if("WEEKLY".equals(mainPeriodTypeId)){
				if("MONTHLY".equals(convertPeriodTypeId)){
					retValue = 1f/4f;
				}else if("QUARTERLY".equals(convertPeriodTypeId)){
					retValue = 1f/13f;
				}else if("YEARLY".equals(convertPeriodTypeId)){
					retValue = 1f/52f;
				}
			}else if("MONTHLY".equals(mainPeriodTypeId)){
				if("WEEKLY".equals(convertPeriodTypeId)){
					retValue = 4f;
				}else if("QUARTERLY".equals(convertPeriodTypeId)){
					retValue = 1f/3f;
				}else if("YEARLY".equals(convertPeriodTypeId)){
					retValue = 1f/12f;
				}
			}else if("QUARTERLY".equals(mainPeriodTypeId)){
				if("WEEKLY".equals(convertPeriodTypeId)){
					retValue = 13f;
				}else if("MONTHLY".equals(convertPeriodTypeId)){
					retValue = 3f;
				}else if("YEARLY".equals(convertPeriodTypeId)){
					retValue = 1f/4f;
				}
			}else if("YEARLY".equals(mainPeriodTypeId)){
				if("WEEKLY".equals(convertPeriodTypeId)){
					retValue = 52;
				}else if("MONTHLY".equals(convertPeriodTypeId)){
					retValue = 12f;
				}else if("YEARLY".equals(convertPeriodTypeId)){
					retValue = 4f;
				}
			}
		}
		return retValue;
	}
	
	public static BigDecimal getTotalAmountParamValue(List<GenericValue> paramList, String fieldAmount){
		BigDecimal ret = BigDecimal.ZERO;
		for(GenericValue tempGv: paramList){
			String value = tempGv.getString(fieldAmount);
			ret = ret.add(new BigDecimal(value));
		}
		return ret;
	}
	
	public static String getBaseOnWorkDayParametersValue(DispatchContext dctx, Timestamp fromDateSalaryCalc, Timestamp thruDateSalaryCalc,
			Timestamp fromDateEmplParam, Timestamp thruDateEmplParam, String partyId, PeriodEnum period, 
			TimeZone timeZone, Locale locale, String defaultValue) throws GenericServiceException, Exception{
		String value = "0";
		switch (period) {
			case YEARLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateSalaryCalc);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateSalaryCalc, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateEmplParam);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateSalaryCalc, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateSalaryCalc);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateEmplParam, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateEmplParam);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateEmplParam, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
			break;
			case QUARTERLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateSalaryCalc, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateEmplParam, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateEmplParam, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateSalaryCalc, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateEmplParam, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateEmplParam, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
			break;
			case MONTHLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateSalaryCalc);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateSalaryCalc, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateEmplParam);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateSalaryCalc, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateSalaryCalc);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateEmplParam, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateEmplParam);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateEmplParam, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
			case WEEKLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateSalaryCalc);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateSalaryCalc, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateEmplParam);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateSalaryCalc, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateSalaryCalc);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateEmplParam, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateEmplParam);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateEmplParam, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
				
			case DAILY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateSalaryCalc);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateSalaryCalc, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateEmplParam);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateSalaryCalc, dayStart, dayEnd, defaultValue, period, locale, timeZone);					
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) 
							&& DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateSalaryCalc);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateSalaryCalc, thruDateEmplParam, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateEmplParam);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, partyId, fromDateEmplParam, thruDateEmplParam, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
			//paid hourly is pending	
			/*case HOURLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					value = PayrollUtil.evaluateStringExpression(element.getValue() + "*" + DateUtil.getWorkingHourBetweenTwoDates(fromDateSalaryCalc, thruDateSalaryCalc));
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					value = PayrollUtil.evaluateStringExpression(element.getValue() + "*" + DateUtil.getWorkingHourBetweenTwoDates(fromDateEmplParam, thruDateSalaryCalc));
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					value = PayrollUtil.evaluateStringExpression(element.getValue() + "*" + DateUtil.getWorkingHourBetweenTwoDates(fromDateSalaryCalc, thruDateEmplParam));
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					value = PayrollUtil.evaluateStringExpression(element.getValue() + "*" + DateUtil.getWorkingHourBetweenTwoDates(fromDateEmplParam, thruDateEmplParam));
				}
				break;*/
			case NA:
				value = defaultValue;
				break;
			default:
				break;
		}
		return value;
	}

	public static Map<String, Object> getPayrollTableRecordOfPartyInfo(
			DispatchContext dctx, String payrollTableId, String partyId, String partyParentId, Locale locale, TimeZone timeZone,
			Timestamp fromDate, Timestamp thruDate, List<GenericValue> listAllFormula) throws GenericEntityException, GenericServiceException {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		retMap.put("partyId", partyId);
		retMap.put("partyParentId", partyParentId);
		List<GenericValue> payrollCharacteristicList = delegator.findByAnd("PayrollCharacteristic", null, null, false);
		Double realSalaryPaid = 0d;
		if("PERSON".equals(party.getString("partyTypeId"))){
			retMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
			Float dayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
			Map<String, Object> mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, partyId);
			Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
			Float dayWorkActual = dayWork - totalDayLeave;
			retMap.put("numberWorkDay", dayWorkActual.toString() + "/" + dayWork.toString());
			for(GenericValue payrollCharacteristic: payrollCharacteristicList){
				String payrollCharacteristicId = payrollCharacteristic.getString("payrollCharacteristicId");
				List<GenericValue> tempSubListFormula = EntityUtil.filterByCondition(listAllFormula, EntityCondition.makeCondition("payrollCharacteristicId", payrollCharacteristicId));
				Double totalValue = getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempSubListFormula);
				if("INCOME".equals(payrollCharacteristicId)){
					realSalaryPaid += totalValue;
					List<String> payrollItemTypeIdList = EntityUtil.getFieldListFromEntityList(tempSubListFormula, "payrollItemTypeId", true);
					for(String payrollItemTypeId: payrollItemTypeIdList){
						List<GenericValue> tempFormulaIncome = EntityUtil.filterByCondition(tempSubListFormula, EntityCondition.makeCondition("payrollItemTypeId", payrollItemTypeId));
						Double tempPayrollItemTypeValue = getTotalValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempFormulaIncome);
						retMap.put(payrollItemTypeId + "_payrollItemType", Math.round(tempPayrollItemTypeValue));
					}
				}else if("DEDUCTION".equals(payrollCharacteristicId)){
					realSalaryPaid -= totalValue;
				}
				retMap.put(payrollCharacteristicId + "_payrollChar",  Math.round(totalValue));
			}
			List<GenericValue> otherFormula = EntityUtil.filterByCondition(listAllFormula, EntityCondition.makeCondition("payrollCharacteristicId", null));
			for(GenericValue tempGv: otherFormula){
				Double value = getValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, tempGv);
				retMap.put(tempGv.getString("code"),  Math.round(value));
			}
			retMap.put("realSalaryPaid",  Math.round(realSalaryPaid));
		}else{
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, false, false);
			List<GenericValue> listDepartment = buildOrg.getDirectChildList(delegator);
			List<GenericValue> employeeList = buildOrg.getDirectEmployee(delegator);
			List<GenericValue> allPartyList = FastList.newInstance();			
			if(listDepartment != null){
				allPartyList.addAll(listDepartment);
			}
			if(employeeList != null){
				allPartyList.addAll(employeeList);
			}
			retMap.put("partyName", PartyHelper.getPartyName(delegator, partyId, false));
			for(GenericValue tempGv: allPartyList){
				String tempChildPartyId = tempGv.getString("partyId");
				Map<String, Object> tempMap = getPayrollTableRecordOfPartyInfo(dctx, payrollTableId, tempChildPartyId, partyId, locale, timeZone, fromDate, thruDate, listAllFormula);
				for(Map.Entry<String, Object> entry: tempMap.entrySet()){
					String key = entry.getKey();
					Object value = entry.getValue();
					if(value instanceof Long){
						Long valueFormula = (Long)value;
						if(retMap.get(key) != null){
							Long existValue = (Long)retMap.get(key);
							retMap.put(key, existValue + valueFormula);
						}else{
							retMap.put(key, value);
						}
					}
				}				
			}
			
		}
		return retMap;
	}

	private static Double getTotalValuePartyPayrollFormula(Delegator delegator,String partyId, String payrollTableId, 
			Timestamp fromDate, List<GenericValue> listFormula) throws GenericEntityException {
		Double retValue = 0d;
		for(GenericValue formula: listFormula){
			Double value = getValuePartyPayrollFormula(delegator, partyId, payrollTableId, fromDate, formula);
			retValue += value;
		}
		return retValue;
	}
	
	private static Double getValuePartyPayrollFormula(Delegator delegator, String partyId, String payrollTableId, Timestamp fromDate, GenericValue formula) throws GenericEntityException {
		GenericValue payrollTable = delegator.findOne("PayrollTable", UtilMisc.toMap("partyId", partyId, "code", formula.getString("code"), 
				"payrollTableId", payrollTableId, "fromDate", fromDate), false);
		String value = payrollTable.getString("value");
		return Double.parseDouble(value);
	}
}
