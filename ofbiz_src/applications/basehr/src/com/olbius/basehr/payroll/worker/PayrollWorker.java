package com.olbius.basehr.payroll.worker;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Date;
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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.payroll.PeriodEnum;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.timekeeping.utils.TimekeepingUtils;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PersonHelper;

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

	public static String getConstantParametersValue(GenericValue timekeepingSummaryParty, String value, String periodParammeters) throws ScriptException{
		Double workdayStandard = timekeepingSummaryParty.getDouble("workdayStandard");
		String workdayStandardValue = String.valueOf(workdayStandard);
		String retValue = value;
		if(UtilValidate.areEqual(periodParammeters, null)){
			return "0";
		}else{
		switch (periodParammeters) {
			case "DAILY":
				retValue = PayrollUtil.evaluateStringExpression(value + "*" + workdayStandardValue);
				break;
			case "WEEKLY":
				retValue = PayrollUtil.evaluateStringExpression(value + "* 4");
				break;
			case "MONTHLY":
				retValue = value;
				break;
			case "QUARTERLY":
				retValue = PayrollUtil.evaluateStringExpression(value + "/ 3");
				break;
			case "YEARLY":
				retValue = PayrollUtil.evaluateStringExpression(value + "/ 12");
				break;
			default:
				break;
		}
		}
		return retValue;
	}

	/*private static float convertPeriodEnum(DispatchContext dctx, String partyId, String mainPeriodTypeId, String convertPeriodTypeId,
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
	}*/
	
	public static BigDecimal getTotalAmountParamValue(List<GenericValue> paramList, String fieldAmount){
		BigDecimal ret = BigDecimal.ZERO;
		for(GenericValue tempGv: paramList){
			String value = tempGv.getString(fieldAmount);
			ret = ret.add(new BigDecimal(value));
		}
		return ret;
	}
	
	//maybe delete
	public static String getBaseOnWorkDayParametersValue(DispatchContext dctx, String emplTimesheetId, Timestamp fromDateSalaryCalc, Timestamp thruDateSalaryCalc,
			Timestamp fromDateEmplParam, Timestamp thruDateEmplParam, String partyId, PeriodEnum period, 
			TimeZone timeZone, Locale locale, String defaultValue) throws GenericServiceException, Exception{
		String value = "0";
		switch (period) {
			case YEARLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateSalaryCalc);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx,emplTimesheetId, partyId, fromDateSalaryCalc, thruDateSalaryCalc, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateEmplParam);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateSalaryCalc, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateSalaryCalc);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateEmplParam, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp yearStart = UtilDateTime.getYearStart(fromDateEmplParam);
					Timestamp yearEnd = UtilDateTime.getYearEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateEmplParam, yearStart, yearEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
			break;
			case QUARTERLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateSalaryCalc, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateEmplParam, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateEmplParam, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateSalaryCalc, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateEmplParam, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp quarterStart = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					Timestamp quarterEnd = DateUtil.getQuarterEnd(fromDateSalaryCalc, locale, timeZone);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateEmplParam, quarterStart, quarterEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
			break;
			case MONTHLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateSalaryCalc);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateSalaryCalc, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateEmplParam);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateSalaryCalc, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateSalaryCalc);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateEmplParam, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp monthStart = UtilDateTime.getMonthStart(fromDateEmplParam);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateEmplParam, monthStart, monthEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
			case WEEKLY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateSalaryCalc);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateSalaryCalc, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateEmplParam);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateSalaryCalc, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) && DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateSalaryCalc);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateEmplParam, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp weekStart = UtilDateTime.getWeekStart(fromDateEmplParam);
					Timestamp weekEnd = UtilDateTime.getWeekEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateEmplParam, weekStart, weekEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
				
			case DAILY:
				if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && fromDateEmplParam.before(fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateSalaryCalc);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateSalaryCalc, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}else if((thruDateEmplParam == null || DateUtil.afterOrEquals(thruDateEmplParam, thruDateSalaryCalc)) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateEmplParam);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateSalaryCalc, dayStart, dayEnd, defaultValue, period, locale, timeZone);					
				}else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && fromDateEmplParam.before(fromDateSalaryCalc) 
							&& DateUtil.afterOrEquals(thruDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateSalaryCalc);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateSalaryCalc, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateSalaryCalc, thruDateEmplParam, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				else if(DateUtil.beforeOrEquals(thruDateEmplParam, thruDateSalaryCalc) && DateUtil.afterOrEquals(fromDateEmplParam, fromDateSalaryCalc)){
					Timestamp dayStart = UtilDateTime.getDayStart(fromDateEmplParam);
					Timestamp dayEnd = UtilDateTime.getDayEnd(fromDateEmplParam, timeZone, locale);
					value = PayrollUtil.getActualValueByPeriod(dctx, emplTimesheetId, partyId, fromDateEmplParam, thruDateEmplParam, dayStart, dayEnd, defaultValue, period, locale, timeZone);
					//result.put(code, value);
				}
				break;
			case NA:
				value = defaultValue;
				break;
			default:
				break;
		}
		return value;
	}

	public static Map<String, Object> getPayrollTableRecordOfPartyInfo(
			DispatchContext dctx, String payrollTableId, String emplTimesheetId, String partyId, String partyParentId, Locale locale, TimeZone timeZone,
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
			Map<String, Object> mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, emplTimesheetId, fromDate, thruDate, partyId);
			Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
			float leavePaid = (Float)mapEmpDayLeave.get("leavePaid");
			Float dayWorkActual = dayWork - (totalDayLeave - leavePaid);
			retMap.put("numberWorkDay", dayWorkActual.toString() + "/" + dayWork.toString());
			GenericValue salaryRateParam = delegator.findOne("PayrollTable", 
					UtilMisc.toMap("payrollTableId", payrollTableId, "partyId", partyId, "code", "TI_LE_HUONG_LUONG", "fromDate", fromDate), false);
			GenericValue allowanceRateParam = delegator.findOne("PayrollTable", 
					UtilMisc.toMap("payrollTableId", payrollTableId, "partyId", partyId, "code", "TI_LE_TRO_CAP", "fromDate", fromDate), false);
			BigDecimal salaryRate = BigDecimal.ONE;
			BigDecimal allowanceRate = BigDecimal.ONE;
			if(salaryRateParam != null){
				salaryRate = salaryRateParam.getBigDecimal("value");
			}
			if(allowanceRateParam != null){
				allowanceRate = allowanceRateParam.getBigDecimal("value");
			}
			MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
			retMap.put("salaryRate", salaryRate.multiply(new BigDecimal(100), mc));
			retMap.put("allowanceRate", allowanceRate.multiply(new BigDecimal(100), mc));
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
				Map<String, Object> tempMap = getPayrollTableRecordOfPartyInfo(dctx, payrollTableId, emplTimesheetId, tempChildPartyId, partyId, locale, timeZone, fromDate, thruDate, listAllFormula);
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
		BigDecimal value = BigDecimal.ZERO;
		if(payrollTable != null){
			value = payrollTable.getBigDecimal("value");
		}
		return value.doubleValue();
	}

	//may be delete
	public static void settingEmplBaseSalary(Delegator delegator,
			LocalDispatcher dispatcher, List<String> departmentList, String orgId, String partyId, List<String> roleTypeGroupList,
			Timestamp fromDate, Timestamp thruDate, String overrideData, String rateCurrencyUomId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
		List<GenericValue> postalAddrs = PartyUtil.getPostalAddressOfOrg(delegator, departmentList, fromDate, thruDate);
		EntityCondition roleTypeGroupConds = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeGroupId", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("roleTypeGroupId", EntityJoinOperator.IN, roleTypeGroupList));
		List<Timestamp> listTimestamp = FastList.newInstance();
		List<GenericValue> allEmplPosTypeRate = FastList.newInstance();
		for(GenericValue emplPos: emplPosList){
			Timestamp tempFromDate = emplPos.getTimestamp("fromDate");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			Timestamp tempThruDate = emplPos.getTimestamp("thruDate");
			if(thruDate != null && (tempThruDate == null || tempFromDate.after(thruDate))){
				tempThruDate = thruDate;
			}
			String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
			EntityCondition dateConds = EntityConditionUtils.makeDateConds(tempFromDate, tempThruDate);
			EntityCondition tempCond = dateConds;
			if(roleTypeGroupConds != null){
				tempCond = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, roleTypeGroupConds);
			}
			List<GenericValue> emplPosTypeRate = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(
					EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId),
					EntityOperator.AND,
					tempCond), null, UtilMisc.toList("fromDate"), null, false);
			allEmplPosTypeRate.addAll(emplPosTypeRate);
			List<GenericValue> emplPositionTypeRateByContact = FastList.newInstance();
			for(GenericValue addr: postalAddrs){
				List<GenericValue> tempList = PayrollUtil.getEmplPositionTypeRate(delegator, emplPosTypeRate, addr);
				List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateByContact, "emplPositionTypeRateId", true);
				EntityCondition filterConds = EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.NOT_IN, emplPositionTypeRateIdList);
				tempList = EntityUtil.filterByCondition(tempList, filterConds);
				emplPositionTypeRateByContact.addAll(tempList);
			}
			for(GenericValue tempGv: emplPositionTypeRateByContact){
				Timestamp rateFromDate = tempGv.getTimestamp("fromDate");
				Timestamp rateThruDate = tempGv.getTimestamp("thruDate");
				if(rateFromDate.before(tempFromDate)){
					rateFromDate = tempFromDate;
				}
				if(tempThruDate != null && (rateThruDate == null || rateThruDate.after(tempThruDate))){
					rateThruDate = tempThruDate;
				}
				if(!listTimestamp.contains(rateFromDate)){
					listTimestamp.add(rateFromDate);
				}
				
				if(!listTimestamp.contains(rateThruDate)){
					listTimestamp.add(rateThruDate);
				}
			}
		}
		List<EntityCondition> listRateAmountCond = FastList.newInstance();
		listRateAmountCond.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		listRateAmountCond.add(EntityCondition.makeCondition("partyId", partyId));
		listRateAmountCond.add(EntityCondition.makeCondition("orgId", orgId));
		List<GenericValue> rateAmountList = delegator.findList("RateAmount", EntityCondition.makeCondition(listRateAmountCond), null, UtilMisc.toList("fromDate"), null, false);
		for(GenericValue tempRateAmount: rateAmountList){
			Timestamp tempFromDate = tempRateAmount.getTimestamp("fromDate");
			Timestamp tempThruDate = tempRateAmount.getTimestamp("thruDate");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			if(thruDate != null && (tempThruDate == null || tempThruDate.after(thruDate))){
				tempThruDate = thruDate;
			}
			if(!listTimestamp.contains(tempFromDate)){
				listTimestamp.add(tempFromDate);
			}
			
			if(!listTimestamp.contains(tempThruDate)){
				listTimestamp.add(tempThruDate);
			}
		}
		DateUtil.sortList(listTimestamp);
		List<Map<String, Timestamp>> listFromThruDate = DateUtil.buildMapPeriodTime(listTimestamp);
		createEmplSalaryBaseFlat(delegator, dispatcher, userLogin, allEmplPosTypeRate, rateAmountList, emplPosList, partyId, orgId, listFromThruDate, overrideData);
	}
	
	//maybe delete
	public static void createEmplSalaryBaseFlat(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin,
			List<GenericValue> allEmplPosTypeRate, List<GenericValue> rateAmountList, List<GenericValue> positionTypeList,
			String partyId, String orgId, List<Map<String, Timestamp>> listFromThruDate, String overrideData) throws GenericServiceException, GenericEntityException {
		List<EntityCondition> commonCondList = FastList.newInstance();
		commonCondList.add(EntityCondition.makeCondition("partyId", partyId));
		commonCondList.add(EntityCondition.makeCondition("orgId", orgId));
		EntityCondition commonConds = EntityCondition.makeCondition(commonCondList);
		Timestamp dateLeaveOrg = PersonHelper.getDateEmplLeaveOrg(delegator, partyId);
		Timestamp dateJoinOrg = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
		for(Map<String, Timestamp> tempMap: listFromThruDate){
			Timestamp fromDate = tempMap.get("fromDate");
			Timestamp thruDate = tempMap.get("thruDate");
			if((dateJoinOrg != null && thruDate != null && dateJoinOrg.compareTo(thruDate) >= 0) 
					|| (dateLeaveOrg != null && dateLeaveOrg.compareTo(fromDate) <= 0)){
				continue;
			}
			List<EntityCondition> dateCondList = FastList.newInstance();
			dateCondList.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			if(thruDate != null){
				dateCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
						EntityJoinOperator.OR,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			}else{
				dateCondList.add(EntityCondition.makeCondition("thruDate", null));
			}
			EntityCondition dateConds = EntityCondition.makeCondition(dateCondList);
			List<GenericValue> rateAmounts = EntityUtil.filterByCondition(rateAmountList, EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, dateConds));
			//check whether salary is set for employee
			if(UtilValidate.isEmpty(rateAmounts)){
				List<GenericValue> emplPos = EntityUtil.filterByCondition(positionTypeList, dateConds);
				List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPos, "emplPositionTypeId", true);
				EntityCondition tempConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.IN, emplPositionTypeList));
				List<GenericValue> emplPositionTypeRates = EntityUtil.filterByCondition(allEmplPosTypeRate, tempConds);
				if(UtilValidate.isNotEmpty(emplPositionTypeRates)){
					BigDecimal rateAmountValue = BigDecimal.ZERO;
					String emplPositionTypeRateId = emplPositionTypeRates.get(0).getString("emplPositionTypeRateId");
					for(GenericValue tempEmplPositionTypeRate: emplPositionTypeRates){
						BigDecimal tempRateAmount = tempEmplPositionTypeRate.getBigDecimal("rateAmount");
						if("getValueLowest".equals(overrideData)){
							if(tempRateAmount.compareTo(rateAmountValue) < 0){
								rateAmountValue = tempRateAmount;
								emplPositionTypeRateId = tempEmplPositionTypeRate.getString("emplPositionTypeRateId");
							}
						}else{
							if(tempRateAmount.compareTo(rateAmountValue) > 0){
								rateAmountValue = tempRateAmount;
								emplPositionTypeRateId = tempEmplPositionTypeRate.getString("emplPositionTypeRateId");
							}
						}
					}
					GenericValue emplPositionTypeRate = EntityUtil.filterByCondition(allEmplPosTypeRate, EntityCondition.makeCondition("emplPositionTypeRateId", emplPositionTypeRateId)).get(0);
					Timestamp amountFromDate = fromDate;
					Timestamp amountThruDate = thruDate;
					
					if(dateJoinOrg != null && dateJoinOrg.compareTo(fromDate) > 0){
						amountFromDate = dateJoinOrg;
					}
					if(dateLeaveOrg != null && (thruDate == null || thruDate.compareTo(dateLeaveOrg) > 0)){
						amountThruDate = dateLeaveOrg;
					}
					dispatcher.runSync("createPartyRateAmount", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, 
							"emplPositionTypeId", emplPositionTypeRate.get("emplPositionTypeId"),
							"orgId", orgId,
							"periodTypeId", emplPositionTypeRate.get("periodTypeId"),
							"rateCurrencyUomId", emplPositionTypeRate.get("rateCurrencyUomId"),
							"rateAmount", rateAmountValue,
							"fromDate", amountFromDate,
							"thruDate", amountThruDate));
				}
			}
		}
	}

	//maybe delete
	public static void setEmplPayrollParamByPosType(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, List<String> roleTypeGroupList, String partyId, String orgId, 
			String overrideDataWay, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException, GenericServiceException {
		List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
		EntityCondition commonCond = null;
		if(UtilValidate.isNotEmpty(roleTypeGroupList)){
			commonCond = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeGroupId", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("roleTypeGroupId", EntityJoinOperator.IN, roleTypeGroupList));
		}
		List<GenericValue> allPayrollParamPositionType = FastList.newInstance();
		Map<String, List<Timestamp>> payrollParamMap = FastMap.newInstance(); 		
		for(GenericValue emplPos: emplPosList){
			Timestamp tempFromDate = emplPos.getTimestamp("fromDate");
			Timestamp tempThruDate = emplPos.getTimestamp("thruDate");
			String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			
			if(thruDate != null && (tempThruDate == null || tempThruDate.after(thruDate))){
				tempThruDate = thruDate;
			}
			EntityCondition dateConds = EntityConditionUtils.makeDateConds(tempFromDate, tempThruDate);
			EntityCondition emplPosTypeConds = EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
			EntityCondition tempConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, emplPosTypeConds);
			if(commonCond != null){
				tempConds = EntityCondition.makeCondition(commonCond, EntityJoinOperator.AND, tempConds);
			}
			List<GenericValue> payrollParamPositionTypeList = delegator.findList("PayrollParamPositionType", tempConds, null, UtilMisc.toList("fromDate"), null, false);
			allPayrollParamPositionType.addAll(payrollParamPositionTypeList);
			for(GenericValue payrollParamPositionType: payrollParamPositionTypeList){
				String code = payrollParamPositionType.getString("code");				
				Timestamp payrollParamPosTypeFromDate = payrollParamPositionType.getTimestamp("fromDate"); 
				Timestamp payrollParamPosTypeThruDate = payrollParamPositionType.getTimestamp("thruDate");
				if(payrollParamPosTypeFromDate.before(tempFromDate)){
					payrollParamPosTypeFromDate = tempFromDate;
				}
				if(tempThruDate != null && (payrollParamPosTypeThruDate == null || payrollParamPosTypeThruDate.after(tempThruDate))){
					payrollParamPosTypeThruDate = tempThruDate;
				}
				List<Timestamp> listTimestamp = payrollParamMap.get(code);
				if(listTimestamp == null){
					listTimestamp = FastList.newInstance();
					payrollParamMap.put(code, listTimestamp);
				}
				if(!listTimestamp.contains(payrollParamPosTypeFromDate)){
					listTimestamp.add(payrollParamPosTypeFromDate);
				}
				if(!listTimestamp.contains(payrollParamPosTypeThruDate)){
					listTimestamp.add(payrollParamPosTypeThruDate);
				}
			}
		}
		List<EntityCondition> listEmplPayrollParamCond = FastList.newInstance();
		listEmplPayrollParamCond.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		listEmplPayrollParamCond.add(EntityCondition.makeCondition("partyId", partyId));
		listEmplPayrollParamCond.add(EntityCondition.makeCondition("orgId", orgId));
		EntityCondition commonEmplPayrollParamCond = EntityCondition.makeCondition(listEmplPayrollParamCond);
		for(Map.Entry<String, List<Timestamp>> entry: payrollParamMap.entrySet()){
			List<Timestamp> tempList = entry.getValue();
			String code = entry.getKey();
			EntityCondition tempCodeConds = EntityCondition.makeCondition("code", code);
			List<GenericValue> payrollEmplParamList = delegator.findList("PayrollEmplParameters", 
					EntityCondition.makeCondition(tempCodeConds, EntityJoinOperator.AND, commonEmplPayrollParamCond), null, UtilMisc.toList("fromDate"), null, false);
			for(GenericValue tempPayrollEmplParamList: payrollEmplParamList){
				Timestamp tempFromDate = tempPayrollEmplParamList.getTimestamp("fromDate");
				Timestamp tempThruDate = tempPayrollEmplParamList.getTimestamp("thruDate");								
				if(tempFromDate.before(fromDate)){
					tempFromDate = fromDate;
				}
				if(thruDate != null && (tempThruDate == null || tempThruDate.after(thruDate))){
					tempThruDate = thruDate;
				}
				if(!tempList.contains(tempFromDate)){
					tempList.add(tempFromDate);
				}
				if(!tempList.contains(tempThruDate)){
					tempList.add(tempThruDate);
				}
			}
			DateUtil.sortList(tempList);	
			List<Map<String, Timestamp>> listFromThruDate = DateUtil.buildMapPeriodTime(tempList);
			createPayrollEmplParameters(delegator, dispatcher, userLogin, allPayrollParamPositionType, emplPosList, code, partyId, orgId, listFromThruDate, overrideDataWay);	
		}
	}

	//maybe delete
	public static void createPayrollEmplParameters(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, 
			List<GenericValue> allPayrollParamPositionType, List<GenericValue> positionTypeList, String code, String partyId, String orgId, 
			List<Map<String, Timestamp>> listFromThruDate, String overrideDataWay) throws GenericEntityException, GenericServiceException {
		List<EntityCondition> commonCondList = FastList.newInstance();
		commonCondList.add(EntityCondition.makeCondition("code", code));
		commonCondList.add(EntityCondition.makeCondition("partyId", partyId));
		commonCondList.add(EntityCondition.makeCondition("orgId", orgId));
		EntityCondition commonConds = EntityCondition.makeCondition(commonCondList);
		for(Map<String, Timestamp> tempMap: listFromThruDate){
			Timestamp fromDate = tempMap.get("fromDate");
			Timestamp thruDate = tempMap.get("thruDate");
			//check payroll parameters is set for party. if is set, ignore
			List<EntityCondition> dateCondList = FastList.newInstance();
			dateCondList.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			if(thruDate != null){
				dateCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
						EntityJoinOperator.OR,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			}else{
				dateCondList.add(EntityCondition.makeCondition("thruDate", null));
			}
			EntityCondition dateConds = EntityCondition.makeCondition(dateCondList);
			List<GenericValue> payrollEmplParam = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, dateConds), null, null, null, false);
			if(UtilValidate.isEmpty(payrollEmplParam)){				
				List<GenericValue> emplPos = EntityUtil.filterByCondition(positionTypeList, dateConds);
				List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPos, "emplPositionTypeId", true);
				EntityCondition tempConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.IN, emplPositionTypeList));
				tempConds = EntityCondition.makeCondition(tempConds, EntityJoinOperator.AND, EntityCondition.makeCondition("code", code));
				//payrollParam not set for party in period => set payroll parameters for party
				List<GenericValue> payrollEmplPosParam = EntityUtil.filterByCondition(allPayrollParamPositionType, tempConds);
				if(UtilValidate.isNotEmpty(payrollEmplPosParam)){
					BigDecimal rateAmount = BigDecimal.ZERO;
					for(GenericValue tempPayrollEmplPosParam: payrollEmplPosParam){
						BigDecimal tempRateAmount = tempPayrollEmplPosParam.getBigDecimal("rateAmount");
						if("getValueLowest".equals(overrideDataWay)){
							if(tempRateAmount.compareTo(rateAmount) < 0){
								rateAmount = tempRateAmount;
							}
						}else{
							if(tempRateAmount.compareTo(rateAmount) > 0){
								rateAmount = tempRateAmount;
							}
						}
					}
					Map<String, Object> ctxMap = FastMap.newInstance();
					ctxMap.put("partyId", partyId);
					ctxMap.put("fromDate", fromDate);
					ctxMap.put("thruDate", thruDate);
					ctxMap.put("orgId", orgId);
					ctxMap.put("code", code);
					ctxMap.put("value", rateAmount.toString());
					ctxMap.put("userLogin", userLogin);
					dispatcher.runSync("assignEmployeePayrollParameters", ctxMap);
				}
			}
		}
	}

	public static String getEmplTimesheetByPayrollTable(Delegator delegator, String payrollTableId) throws GenericEntityException {
		GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
		return getEmplTimesheetByPayrollTable(delegator, payrollTableRecord);
	}
	public static String getEmplTimesheetByPayrollTable(Delegator delegator, GenericValue payrollTableRecord) throws GenericEntityException {
		if(payrollTableRecord == null){
			return null;	
		}
		return payrollTableRecord.getString("timekeepingSummaryId");
	}

	public static String getInvoiceItemTypeByPartyAndCode(Delegator delegator,String code, String partyGroupId, String customTimePeriodId, String userLoginId) throws GenericEntityException {
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		if(customTimePeriod != null){
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = new Timestamp(thruDate.getTime());
			return getInvoiceItemTypeByPartyAndCode(delegator, code, partyGroupId, fromDateTs, thruDateTs, userLoginId);
		}
		return null;
	}

	public static String getInvoiceItemTypeByPartyAndCode(Delegator delegator,
			String code, String partyGroupId, Timestamp fromDate, Timestamp thruDate, String userLoginId) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		String rootPartyId = PartyUtil.getRootOrganization(delegator, userLoginId);
		String tempPartyId = partyGroupId;
		do{
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyId", tempPartyId));
			conds.add(EntityCondition.makeCondition("code", code));
			conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), 
													EntityJoinOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			List<GenericValue> invoiceItemTypeList = delegator.findList("PartyPayrollFormulaInvoiceItemType", 
					EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceItemTypeList)){
				return invoiceItemTypeList.get(0).getString("invoiceItemTypeId");
			}
			GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempPartyId); 
			if(parentOrg == null){
				break;
			}
			tempPartyId = parentOrg.getString("partyIdFrom");
		}while(!tempPartyId.equals(rootPartyId));
		return null;
	}

	public static List<String> getListInvoiceSalOfEmpl(Delegator delegator, String partyId, String customTimePeriodId) throws GenericEntityException {
		List<GenericValue> paySalList = delegator.findByAnd("PaySalaryHistory", UtilMisc.toMap("partyIdTo", partyId, "customTimePeriodId", customTimePeriodId), UtilMisc.toList("invoiceId"), false);
		List<String> retList = EntityUtil.getFieldListFromEntityList(paySalList, "invoiceId", true);
		return retList;
	}

	public static BigDecimal getTotalAmountPaySal(Delegator delegator, String partyId, String customTimePeriodId, String payrollCharacteristicId) throws GenericEntityException {
		BigDecimal retVal = BigDecimal.ZERO;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
		conds.add(EntityCondition.makeCondition("partyIdTo", partyId));
		conds.add(EntityCondition.makeCondition("payrollCharacteristicId", payrollCharacteristicId));
		List<GenericValue> paySalaryItemList = delegator.findList("PaySalaryItemHistoryAndFormula", 
				EntityCondition.makeCondition(conds), null, null, null, false);
		for(GenericValue income: paySalaryItemList){
			BigDecimal tempAmount = income.getBigDecimal("amount");
			if(tempAmount != null){
				retVal = retVal.add(tempAmount);
			}
		}
		return retVal;
	}

	public static Map<String, BigDecimal> getSalaryAllowanceRate(Delegator delegator, String customTimePeriodId, String partyId) throws GenericEntityException {
		Map<String, BigDecimal> retMap = FastMap.newInstance();
		List<GenericValue> perfCriteriaAssessmentPartyList = delegator.findByAnd("PerfCriteriaAssessmentParty", 
				UtilMisc.toMap("partyId", partyId, "customTimePeriodId", customTimePeriodId, "statusId", "KAS_ACCEPTED"), UtilMisc.toList("-point"), false);
		if(UtilValidate.isNotEmpty(perfCriteriaAssessmentPartyList)){
			BigDecimal oneHundred = new BigDecimal(100);
			GenericValue perfCriteriaAssessmentParty = EntityUtil.getFirst(perfCriteriaAssessmentPartyList);
			retMap.put("salaryRate", perfCriteriaAssessmentParty.get("salaryRate") != null? 
					perfCriteriaAssessmentParty.getBigDecimal("salaryRate").divide(oneHundred) : BigDecimal.ONE);
			retMap.put("allowanceRate", perfCriteriaAssessmentParty.get("allowanceRate") != null? 
					perfCriteriaAssessmentParty.getBigDecimal("allowanceRate").divide(oneHundred) : BigDecimal.ONE);
			retMap.put("bonusAmount", perfCriteriaAssessmentParty.get("bonusAmount") != null? perfCriteriaAssessmentParty.getBigDecimal("bonusAmount") : BigDecimal.ZERO);
			retMap.put("punishmentAmount", perfCriteriaAssessmentParty.get("punishmentAmount") != null? perfCriteriaAssessmentParty.getBigDecimal("punishmentAmount") : BigDecimal.ZERO);
		}else{
			retMap.put("salaryRate", BigDecimal.ONE);
			retMap.put("allowanceRate", BigDecimal.ONE);
			retMap.put("bonusAmount", BigDecimal.ZERO);
			retMap.put("punishmentAmount", BigDecimal.ZERO);
		}
		return retMap;
	}
	public static void createPayrollTableRecordParty(Delegator delegator, String payrollTableId, String partyId, String partyGroupId, String statusId) throws GenericEntityException {
		GenericValue payrollTableRecordParty = delegator.makeValue("PayrollTableRecordParty");
		payrollTableRecordParty.put("payrollTableId", payrollTableId);
		payrollTableRecordParty.put("partyId", partyId);
		payrollTableRecordParty.put("statusId", statusId);
		payrollTableRecordParty.put("partyGroupId", partyGroupId);
		delegator.createOrStore(payrollTableRecordParty);
	}
	
	public static BigDecimal getPrllTableRecordePartyAmountByCode(Delegator delegator, String partyId, String payrollTableId, String formulaCode) throws GenericEntityException {
		GenericValue payrollTableRecordPartyAmount = delegator.findOne("PayrollTableRecordPartyAmount", 
				UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId, "code", formulaCode), false);
		if(payrollTableRecordPartyAmount != null){
			return payrollTableRecordPartyAmount.getBigDecimal("amount");
		}
		return null;
	}
	public static BigDecimal getPrllTableRecordePartyAmountByPrllItemType(Delegator delegator, String partyId, String payrollTableId, String payrollItemTypeId) throws GenericEntityException {
		List<GenericValue> payrollItemTypeList = delegator.findByAnd("PayrollItemType", UtilMisc.toMap("parentTypeId", payrollItemTypeId), null, true);
		List<String> payrollItemTypeIdList = null;
		if(UtilValidate.isNotEmpty(payrollItemTypeList)){
			payrollItemTypeIdList = EntityUtil.getFieldListFromEntityList(payrollItemTypeList, "payrollItemTypeId", true);
		}else{
			payrollItemTypeIdList = FastList.newInstance();
		}
		payrollItemTypeIdList.add(payrollItemTypeId);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
		conds.add(EntityCondition.makeCondition("payrollItemTypeId", EntityJoinOperator.IN, payrollItemTypeIdList));
		List<GenericValue> payrollTableRecordPartyAmountList = delegator.findList("PayrollTableRecordPartyAmountAndFormula", EntityCondition.makeCondition(conds), null, null, null, false);
		BigDecimal totalAmount = null;
		for(GenericValue payrollTableRecordPartyAmount: payrollTableRecordPartyAmountList){
			BigDecimal amount = payrollTableRecordPartyAmount.getBigDecimal("amount");
			if(amount != null){
				totalAmount = totalAmount != null? totalAmount.add(amount) : amount;
			}
		}
		return totalAmount;
	}

	public static void updateAcutalSalReceive(Delegator delegator, String payrollTableId) throws GenericEntityException {
		List<GenericValue> payrollTableRecordPartyList = delegator.findByAnd("PayrollTableRecordParty", UtilMisc.toMap("payrollTableId", payrollTableId), null, false);
		for(GenericValue payrollTableRecordParty: payrollTableRecordPartyList){
			updateAcutalSalReceiveParty(delegator, payrollTableRecordParty);
		}
	}

	public static void updateAcutalSalReceiveParty(Delegator delegator, GenericValue payrollTableRecordParty) throws GenericEntityException {
		String partyId = payrollTableRecordParty.getString("partyId");
		String payrollTableId = payrollTableRecordParty.getString("payrollTableId");
		List<GenericValue> totalIncomeList = delegator.findByAnd("PayrollTableRecordPartyIncomeAmount", 
				UtilMisc.toMap("payrollTableId", payrollTableId, "partyId", partyId), null, false);
		BigDecimal income = BigDecimal.ZERO;
		BigDecimal deduction = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(totalIncomeList)){
			income = totalIncomeList.get(0).getBigDecimal("totalIncome");
		}
		List<GenericValue> totalDeductionList = delegator.findByAnd("PayrollTableRecordPartyDeductionAmount", 
				UtilMisc.toMap("payrollTableId", payrollTableId, "partyId", partyId), null, false);
		if(UtilValidate.isNotEmpty(totalDeductionList)){
			deduction = totalDeductionList.get(0).getBigDecimal("totalDedution");
		}
		payrollTableRecordParty.set("actualSalReceive", income.subtract(deduction));
		delegator.store(payrollTableRecordParty);
	}
}
