package com.olbius.payroll;

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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;

import com.olbius.util.DateUtil;

public class TaxWorker {
	//get tax grades base on individual income
	public static String getTaxAuthorityRatePayroll(DispatchContext dctx, String thu_nhap_tinh_thue, Timestamp fromDate, Timestamp thruDate) throws Exception {
		//TODO Auto-generated method stub
		Delegator delegator = dctx.getDelegator();
		
		String taxAuthGeoId = EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", "VNM", delegator);
		float thuNhapTinhThueValue = Float.parseFloat(thu_nhap_tinh_thue);
		GenericValue taxAuthority = EntityUtil.getFirst(delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", taxAuthGeoId),null, false));
		String taxAuthPartyId = taxAuthority.getString("taxAuthPartyId");
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("taxAuthGeoId", taxAuthGeoId));
		conditions.add(EntityCondition.makeCondition("taxAuthPartyId", taxAuthPartyId));
		List<GenericValue> taxAuthorityRatePayrolls = delegator.findList("TaxAuthorityRatePayroll", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		//Map<Enum<PeriodEnum>, Long> periodLength = PayrollUtil.getPeriodLength(delegator);				
		for(GenericValue temp: taxAuthorityRatePayrolls){
			//PeriodEnum period = PeriodEnum.MONTHLY;
			//String periodTypeId = temp.getString("periodTypeId");
			String fromValue = temp.getString("fromValue");
			String thruValue = temp.getString("thruValue");
			/*if("YEARLY".equals(periodTypeId)){
				period = PeriodEnum.YEARLY;
			}else if("QUARTERLY".equals(periodTypeId)){
				period = PeriodEnum.QUARTERLY;
			}else if("MONTHLY".equals(periodTypeId)){
				period = PeriodEnum.MONTHLY;
			}else if("WEEKLY".equals(periodTypeId)){
				period = PeriodEnum.WEEKLY;
			}else if("DAILY".equals(periodTypeId)){
				period = PeriodEnum.DAILY;
			}else if("HOURLY".equals(periodTypeId)){
				period = PeriodEnum.HOURLY;
			}*/
			//String fromValueConvert = fromValue;
			//String thruValueConvert = thruValue;
			/*switch (period) {
				case YEARLY:
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.YEARLY) + "*" 
												+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.YEARLY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					break;
				case QUARTERLY:
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.QUARTERLY) + "*" 
							+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.QUARTERLY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					
					break;
				case MONTHLY:
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.MONTHLY) + "*" 
							+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.MONTHLY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					
					break;
				case WEEKLY:
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.WEEKLY) + "*" 
							+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.WEEKLY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					break;
				case DAILY:
					
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.DAILY) + "*" 
							+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.DAILY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					break;
				case HOURLY:
					fromValueConvert = PayrollUtil.evaluateStringExpression(fromValue + "/" + periodLength.get(PeriodEnum.HOURLY) + "*" 
							+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate) );
					if(thruValue != null){
						thruValueConvert = PayrollUtil.evaluateStringExpression(thruValue + "/" + periodLength.get(PeriodEnum.HOURLY) + "*"
								+ DateUtil.getWorkingHourBetweenTwoDates(fromDate, thruDate));
					}
					
					break;
				case NA:
					break;
				default:
					throw new Exception("Tax Period is not valid");
			}*/
			if(Float.parseFloat(fromValue) <= thuNhapTinhThueValue && (thruValue == null || thuNhapTinhThueValue <= Float.parseFloat(thruValue))){
				return temp.getString("taxAuthorityRateSeqId");
			}
		}
		return null;
	}
	
	//calculate income tax individual base on tax grades
	public static String calculateIncomeTaxIndividual(DispatchContext dctx, String taxGrades, 
									String taxableIncome, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException, ScriptException{
		Delegator delegator = dctx.getDelegator();
		GenericValue taxAuthRatePayroll = delegator.findOne("TaxAuthorityRatePayroll", UtilMisc.toMap("taxAuthorityRateSeqId", taxGrades), false);
		//Map<Enum<PeriodEnum>, Long> periodLengthMap = PayrollUtil.getPeriodLength(delegator);
		String fromValue = taxAuthRatePayroll.getString("fromValue");
		//String fromValueConvert = fromValue;
		String resultValue = "0";
		//String periodTypeId = taxAuthRatePayroll.getString("periodTypeId");
		//fromValueConvert = PayrollUtil.convertValueCorrespondingPerHour(periodLengthMap, fromValue, periodTypeId);
		float fromValueFloat = Float.parseFloat(fromValue);
		String taxAuthGeoId = EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", "VNM", delegator);
		GenericValue taxAuthority = EntityUtil.getFirst(delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", taxAuthGeoId),null, false));
		String taxAuthPartyId = taxAuthority.getString("taxAuthPartyId");
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("taxAuthGeoId", taxAuthGeoId));
		conditions.add(EntityCondition.makeCondition("taxAuthPartyId", taxAuthPartyId));
		List<GenericValue> taxAuthorityRatePayrolls = delegator.findList("TaxAuthorityRatePayroll", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		
		for(GenericValue taxAuthRateTemp: taxAuthorityRatePayrolls){
			String thruValueTemp = taxAuthRateTemp.getString("thruValue");			
			if(UtilValidate.isNotEmpty(thruValueTemp)){
				//String thruValueConvertTmp = PayrollUtil.convertValueCorrespondingPerHour(periodLengthMap, thruValueTemp, taxAuthRateTemp.getString("periodTypeId"));
				if(fromValueFloat > Float.parseFloat(thruValueTemp)){
					String fromValueTemp = taxAuthRateTemp.getString("fromValue");
					//String fromValueConvertTemp = PayrollUtil.convertValueCorrespondingPerHour(periodLengthMap, fromValueTemp, taxAuthRateTemp.getString("periodTypeId"));
					resultValue = PayrollUtil.evaluateStringExpression(resultValue + "+" + "(" + thruValueTemp + "-" + fromValueTemp + ")" + "*" 																		
																		+ taxAuthRateTemp.getString("taxPercentage") + "/100");
				}
			}
		}
		resultValue = PayrollUtil.evaluateStringExpression(resultValue + "+" + "(" + taxableIncome + "-" + fromValue+ ")" 
															+ "*" + taxAuthRatePayroll.getString("taxPercentage") + "/100");
		return resultValue;
	}

	public static List<Map<String, Timestamp>> getPeriodTimeCalcIncIndividualTax(
			Timestamp fromDate, Timestamp thruDate, TimeZone timeZone, Locale locale) {
		// TODO Auto-generated method stub
		List<Map<String, Timestamp>> retList = FastList.newInstance();
		//Timestamp startMonth = UtilDateTime.getMonthStart(fromDate);
		Timestamp endMonth = UtilDateTime.getMonthEnd(fromDate, timeZone, locale);
		Timestamp tempTimestamp = UtilDateTime.getDayEnd(endMonth, 1L);
		Timestamp tempFromDate = fromDate;
		do{
			Map<String, Timestamp> tempMap = FastMap.newInstance();
			tempMap.put("fromDate", tempFromDate);
			if(thruDate.before(tempTimestamp)){
				tempMap.put("thruDate", thruDate);
				retList.add(tempMap);
				break;
			}else{
				tempMap.put("thruDate", tempTimestamp);
				retList.add(tempMap);
			}
			tempFromDate = UtilDateTime.getDayStart(tempTimestamp);
			tempTimestamp = UtilDateTime.getDayEnd(UtilDateTime.getMonthEnd(tempTimestamp, timeZone, locale), 1L);
		}while(true);
		return retList;
	}
}
