package com.olbius.basehr.insurance.worker;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.insurance.helper.InsuranceHelper;
import com.olbius.basehr.util.DateUtil;

public class InsuranceWorker {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getBenefitInsuranceForPartyLeave(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, 
			String benefitTypeId, String emplLeaveId, Date dateParticipateIns, TimeZone timeZone, Locale locale) throws GenericEntityException, GenericServiceException{
		Date nowDate = new Date(UtilDateTime.nowTimestamp().getTime());
		Integer totalMonth = DateUtil.getMonthBetweenTwoDate(dateParticipateIns, nowDate);
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
		GenericValue insuranceAllowanceBenefitType = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false);
		String partyId = emplLeave.getString("partyId");
		String isIncAnnualLeave = insuranceAllowanceBenefitType.getString("isIncAnnualLeave");
		String frequenceId = insuranceAllowanceBenefitType.getString("frequenceId");
		Float totalDayLeave = null;
		BigDecimal monthPregnant = BigDecimal.ONE;
		Long nbrChildBorn = 1l;
		if("Y".equals(isIncAnnualLeave)){
			totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave, true);
		}else{
			totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave, false);
		}
		Float totalDayLeavePaidBefore = InsuranceHelper.getAccumulatedLeave(delegator, benefitTypeId, partyId, emplLeave.getTimestamp("fromDate"));
		BigDecimal totalDayLeaveBig = new BigDecimal(totalDayLeave);
		BigDecimal totalDayLeavePaidBeforeBig = new BigDecimal(totalDayLeavePaidBefore);
		BigDecimal totalDayLeavePaid = BigDecimal.ZERO;
		BigDecimal totalDayLeavePaidExceed = BigDecimal.ZERO; //so ngay nghi vuot qua' quy dinh duoc huong tro cap
		
		Map<String, Object> data = FastMap.newInstance();
		data.put("totalMontParticipateIns", totalMonth);
		data.put("totalDayLeave", totalDayLeaveBig.add(totalDayLeavePaidBeforeBig));
		data.put("monthPregnant", monthPregnant);
		data.put("childYear", 0);
		data.put("nbrChildBorn", nbrChildBorn);
		List<Map<String, Object>> actionCondMapList = getListActionAndCondBenefitType(delegator, benefitTypeId, data);
		BigDecimal maxDayLeave = BigDecimal.ZERO;
		BigDecimal insuranceSalary = BigDecimal.ZERO;
		BigDecimal insuranceSalaryExceed = BigDecimal.ZERO;
		BigDecimal rateBenefit = BigDecimal.ZERO;
		BigDecimal rateBenefitLeaveExceed = BigDecimal.ZERO;
		Timestamp monthStartCalc = UtilDateTime.getMonthStart(emplLeave.getTimestamp("fromDate"));
		
		for(Map<String, Object> tempMap: actionCondMapList){
			List<GenericValue> actions = (List<GenericValue>)tempMap.get("actions");
			List<GenericValue> conditions = (List<GenericValue>)tempMap.get("conditions");
			List<String> inputParamEnumList = null;
			if(UtilValidate.isNotEmpty(conditions)){
				inputParamEnumList = EntityUtil.getFieldListFromEntityList(conditions, "inputParamEnumId", false);
			}
			if(actions != null){
				for(GenericValue action: actions){
					String benefitTypeActionEnumId = action.getString("benefitTypeActionEnumId");
					BigDecimal quantity = action.getBigDecimal("quantity");
					BigDecimal amount = action.getBigDecimal("amount");
					String uomId = action.getString("uomId");
					if(inputParamEnumList != null && inputParamEnumList.contains("IB_MAX_TIME_LEAVE")){
						if("INS_BE_RATE_SAL_MON".equals(benefitTypeActionEnumId)){
							insuranceSalaryExceed = InsuranceHelper.getAvgInsuranceSalaryInMonths(delegator, dispatcher, userLogin, partyId, monthStartCalc, amount.doubleValue(), timeZone, locale);
							rateBenefitLeaveExceed = quantity;
						}else if("INS_BE_SAL_MIN".equals(benefitTypeActionEnumId)){
							insuranceSalaryExceed = InsuranceHelper.getCommonSalaryMinimum(delegator, monthStartCalc, emplLeave.getTimestamp("thruDate"));
							rateBenefitLeaveExceed = quantity;
						}
					}else{
						if("INS_BE_MAX_LEAVE".equals(benefitTypeActionEnumId)){
							if("TF_mon".equals(uomId)){
								quantity = quantity.multiply(new BigDecimal(30));
							}else if("TF_wk".equals(uomId)){
								quantity = quantity.multiply(new BigDecimal(7));
							}else if("TF_yr".equals(uomId)){
								quantity = quantity.multiply(new BigDecimal(365));
							}
							maxDayLeave = quantity;
						}else if("INS_BE_RATE_SAL_MON".equals(benefitTypeActionEnumId)){
							insuranceSalary = InsuranceHelper.getAvgInsuranceSalaryInMonths(delegator, dispatcher, userLogin, partyId, monthStartCalc, amount.doubleValue(), timeZone, locale);
							rateBenefit = quantity;
						}else if("INS_BE_SAL_MIN".equals(benefitTypeActionEnumId)){
							insuranceSalary = InsuranceHelper.getCommonSalaryMinimum(delegator, monthStartCalc, emplLeave.getTimestamp("thruDate"));
							rateBenefit = quantity;
						}else if("INS_BE_LEAVE_ADD".equals(benefitTypeActionEnumId)){
							
						}
					}
				}
			}
		}
		if("PER_YEAR".equals(frequenceId) && maxDayLeave != null){
			if(totalDayLeaveBig.add(totalDayLeavePaidBeforeBig).compareTo(maxDayLeave) > 0){
				if(totalDayLeavePaidBeforeBig.compareTo(maxDayLeave) > 0){
					totalDayLeavePaidExceed = totalDayLeaveBig;
					totalDayLeavePaid = BigDecimal.ZERO;
				}else{
					totalDayLeavePaidExceed = totalDayLeaveBig.add(totalDayLeavePaidBeforeBig).subtract(maxDayLeave);
					totalDayLeavePaid = maxDayLeave.subtract(totalDayLeavePaidBeforeBig);
				}
			}else{
				totalDayLeavePaid = totalDayLeaveBig;
			}
		}else if("EACH_TIME".equals(frequenceId) && maxDayLeave != null){
			if(totalDayLeaveBig.compareTo(maxDayLeave) > 0){
				totalDayLeavePaidExceed = totalDayLeavePaidBeforeBig.subtract(maxDayLeave);
				totalDayLeavePaid = maxDayLeave;
			}else{
				totalDayLeavePaid = totalDayLeaveBig;
			}
		}
		if(totalDayLeavePaid.compareTo(BigDecimal.ZERO) < 0){
			totalDayLeavePaid = BigDecimal.ZERO;
		}
		retMap.put("totalDayLeavePaid", totalDayLeavePaid);
		retMap.put("insuranceSalary", insuranceSalary);
		retMap.put("rateBenefit", rateBenefit);
		retMap.put("totalDayLeave", totalDayLeaveBig);
		retMap.put("totalDayLeavePaidBefore", totalDayLeavePaidBeforeBig);
		retMap.put("nbrChildBorn", nbrChildBorn);
		retMap.put("monthPregnant", monthPregnant);
		if(insuranceSalaryExceed.compareTo(BigDecimal.ZERO) > 0){
			retMap.put("totalDayLeavePaidExceed", totalDayLeavePaidExceed);
			retMap.put("insuranceSalaryExceed", insuranceSalaryExceed);
			retMap.put("rateBenefitLeaveExceed", rateBenefitLeaveExceed);
		}
		return retMap;
	}
	public static List<Map<String, Object>> getListActionAndCondBenefitType(Delegator delegator, String benefitTypeId, Map<String, Object> data, String inputParamEnumCond) throws GenericEntityException{
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<GenericValue> insRuleList = delegator.findByAnd("InsBenefitTypeRule", UtilMisc.toMap("benefitTypeId", benefitTypeId), null, false);
		for(GenericValue insRule: insRuleList){
			Map<String, Object> tempMap = FastMap.newInstance();
			String benefitTypeRuleId = insRule.getString("benefitTypeRuleId");
			Map<String, Object> mapConds = FastMap.newInstance();
			mapConds.put("benefitTypeRuleId", benefitTypeRuleId);
			mapConds.put("benefitTypeId", benefitTypeId);
			if(inputParamEnumCond != null){
				mapConds.put("inputParamEnumId", inputParamEnumCond);
			}
			List<GenericValue> insConds = delegator.findByAnd("InsBenefitTypeCond", mapConds, null, false);
			boolean condResult = true;
			for(GenericValue insCond: insConds){
				condResult = checkCondition(insCond, data);
				if(!condResult){
					break;
				}
			}
			if(condResult){
				List<GenericValue> insActions = delegator.findByAnd("InsBenefitTypeAction", UtilMisc.toMap("benefitTypeRuleId", benefitTypeRuleId, "benefitTypeId", benefitTypeId), null, false);
				if(UtilValidate.isNotEmpty(insActions)){
					listReturn.add(tempMap);
					tempMap.put("actions", insActions);
					if(UtilValidate.isNotEmpty(insConds)){
						tempMap.put("conditions", insConds);
					}
				}
			}
		}
		return listReturn;
	}
	public static List<Map<String, Object>> getListActionAndCondBenefitType(Delegator delegator, String benefitTypeId, Map<String, Object> data) throws GenericEntityException{
		return getListActionAndCondBenefitType(delegator, benefitTypeId, data, null);
	}
	
	protected static boolean checkCondition(GenericValue insCond, Map<String, Object> data) {
		String inputParamEnumId = insCond.getString("inputParamEnumId");
        String operatorEnumId = insCond.getString("operatorEnumId");
        BigDecimal condValue = insCond.getBigDecimal("condValue");
        BigDecimal valueCompare = BigDecimal.ZERO;
        if("IB_YEAR_PAR".equals(inputParamEnumId)){
        	Integer totalMontParticipateIns = (Integer)data.get("totalMontParticipateIns");
        	if(totalMontParticipateIns == null){
        		return false;
        	}
        	valueCompare = new BigDecimal(totalMontParticipateIns);
        	condValue = condValue.multiply(new BigDecimal(12));
        }else if("IB_CHILD_YEAR".equals(inputParamEnumId)){
        	Integer childYear = (Integer)data.get("childYear");
        	valueCompare = new BigDecimal(childYear);
        }else if("IB_MONTH_PREGNANT".equals(inputParamEnumId)){
        	BigDecimal monthPregnant = (BigDecimal)data.get("monthPregnant");
        	valueCompare = monthPregnant;
        }else if("IB_MAX_TIME_LEAVE".equals(inputParamEnumId)){
        	valueCompare = (BigDecimal)data.get("totalDayLeave"); 
        }else{
        	return false;
        }
        int compare = valueCompare.compareTo(condValue);
        if("IB_EQ".equals(operatorEnumId)) {
			if(compare == 0) return true;
        }else if("IB_NEQ".equals(operatorEnumId)){
        	if( compare != 0) return true;
        }else if("IB_LT".equals(operatorEnumId)){
          if(compare < 0) return true;
        }else if("IB_LTE".equals(operatorEnumId)){
          if(compare <= 0) return true;
        }else if("IB_GT".equals(operatorEnumId)){
          if(compare > 0) return true;
        }else if("IB_GTE".equals(operatorEnumId)){
          if(compare >= 0) return true;
        }else {
            return false;
        }
		return false;
	}
	
	public static BigDecimal calculateAllowanceAmountImproveHealth(
			Delegator delegator, Timestamp fromDate,
			BigDecimal dayLeaveConcentrate, BigDecimal dayLeaveFamily) throws GenericEntityException {
		BigDecimal allowanceAmount = BigDecimal.ZERO;
		Date fromDateDayLeaveFamily = null;
		if(dayLeaveConcentrate != null){
			Date fromDateDayLeaveConcentrate = new Date(fromDate.getTime());
			Date thruDateDayLeaveConcentrate = new Date(UtilDateTime.getDayEnd(fromDate, dayLeaveConcentrate.longValue()).getTime());
			fromDateDayLeaveFamily = new Date(UtilDateTime.getDayEnd(fromDate, dayLeaveConcentrate.longValue() + 1).getTime());
			allowanceAmount = allowanceAmount.add(getAmountBenefitBaseCommonSalMinAndRate(delegator, fromDateDayLeaveConcentrate, thruDateDayLeaveConcentrate, new BigDecimal("0.4")));
		}
		if(dayLeaveFamily != null){
			if(fromDateDayLeaveFamily == null){
				fromDateDayLeaveFamily = new Date(fromDate.getTime());
			}
			Date thruDateDayLeaveFamily = new Date(UtilDateTime.getDayEnd(fromDate, dayLeaveFamily.longValue()).getTime());
			allowanceAmount = allowanceAmount.add(getAmountBenefitBaseCommonSalMinAndRate(delegator, fromDateDayLeaveFamily, thruDateDayLeaveFamily, new BigDecimal("0.25")));
		}
		return allowanceAmount;
	}
	
	public static BigDecimal getAmountBenefitBaseCommonSalMinAndRate(Delegator delegator, Date fromDate, Date thruDate, BigDecimal rate) throws GenericEntityException{
		if(rate == null){
			rate = BigDecimal.ONE;
		}
		BigDecimal retVal = BigDecimal.ZERO;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
												EntityJoinOperator.OR,
												EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
		List<GenericValue> commonSalaryMinimum = delegator.findList("CommonSalaryMinimum", EntityCondition.makeCondition(conds), null, null, null, false);
		for(GenericValue tempGv: commonSalaryMinimum){
			Date tempFromDate = tempGv.getDate("fromDate");
			Date tempThruDate = tempGv.getDate("thruDate");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			if(tempThruDate == null || tempThruDate.after(thruDate)){
				tempThruDate = thruDate;
			}
			BigDecimal amount = tempGv.getBigDecimal("amount");
			long totalDay = (tempThruDate.getTime() - tempFromDate.getTime())/DateUtil.ONE_DAY_MILLIS + 1;
			BigDecimal tempAmount = amount.multiply(rate).multiply(new BigDecimal(totalDay));
			retVal = retVal.add(tempAmount);
		}
		return retVal;
	}
}
