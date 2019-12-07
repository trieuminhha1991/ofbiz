package com.olbius.basehr.kpiperfreview.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.PropertiesUtil;

public class PerfReviewKPIHelper {

	public static List<GenericValue> getListKPIOfEmplPositionType(
			Delegator delegator, String emplPositionTypeId) throws GenericEntityException {
		List<GenericValue> kpiList = delegator.findByAnd("EmplPosTypePerfCri", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "statusId", "KPI_ACTIVE"), null, false);
		return kpiList;
	}
	
	public static List<GenericValue> getListKPIEnableByPartyId(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		return getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, null);
	}
	
	public static List<GenericValue> getListKPIEnable(Delegator delegator, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		return getListKPIEnableByPartyId(delegator, null, fromDate, thruDate);
	}
	
	public static List<GenericValue> getListKPIEnableByPartyId(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate, String periodTypeId) throws GenericEntityException{
		List<GenericValue> listKPI = null;
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		List<EntityCondition> listCondition = FastList.newInstance();
		if(UtilValidate.isNotEmpty(partyId)){
			listCondition.add(EntityCondition.makeCondition("partyId", partyId));
		}
		listCondition.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("thruDate", null));
		listConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate));
		listCondition.add(EntityCondition.makeCondition(listConds, EntityJoinOperator.OR));
		if(UtilValidate.isNotEmpty(periodTypeId)){
			listCondition.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
		}
		listKPI = delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(listCondition, EntityJoinOperator.AND), null, null, opts, false);
		return listKPI;
	}
	
	public static List<GenericValue> getListKPIEnableInSpecificDateByPartyId(Delegator delegator, String partyId, Timestamp dateReviewed, String periodTypeId) throws GenericEntityException{
		List<GenericValue> listKPI = FastList.newInstance();
		Date date = new Date(dateReviewed.getTime());
		if( !"".equals(periodTypeId)){
			Timestamp thruDate = UtilDateTime.getDayEnd(dateReviewed);
			Timestamp fromDate = UtilDateTime.getDayStart(dateReviewed);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, null);
		}
		if("DAILY".equals(periodTypeId)){
			Timestamp thruDate = UtilDateTime.getDayEnd(dateReviewed);
			Timestamp fromDate = UtilDateTime.getDayStart(dateReviewed);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
		}
		if("WEEKLY".equals(periodTypeId)){
			Timestamp fromDate = UtilDateTime.getWeekStart(dateReviewed);
			Timestamp thruDate = UtilDateTime.getWeekEnd(dateReviewed);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
		}
		if("MONTHLY".equals(periodTypeId)){
			Timestamp fromDate = UtilDateTime.getMonthStart(dateReviewed);
			Timestamp thruDate = DateUtil.getDateEndOfMonth(date);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
		}
		if("QUARTERLY".equals(periodTypeId)){
			Timestamp fromDate = DateUtil.getDateStartOfQuarter(date);
			Timestamp thruDate = DateUtil.getDateEndOfQuarter(date);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
		}
		if("YEARLY".equals(periodTypeId)){
			Timestamp fromDate = UtilDateTime.getYearStart(dateReviewed);
			Timestamp thruDate = DateUtil.getDateEndOfYear(date);
			listKPI = getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
		}
		return listKPI;
	}
	
	public static List<GenericValue> getListKPIEnableInSpecificDate(Delegator delegator, Timestamp dateReviewed, String periodTypeId) throws GenericEntityException{
		return getListKPIEnableInSpecificDateByPartyId(delegator, null, dateReviewed, periodTypeId);
	}
	
	public static List<GenericValue> getListKPIInCurrenDateByPartyId(Delegator delegator, String partyId, String periodTypeId) throws GenericEntityException{
		List<GenericValue> listKPI = FastList.newInstance();
		Timestamp now = UtilDateTime.nowTimestamp();
		listKPI = getListKPIEnableInSpecificDateByPartyId(delegator, partyId, now, periodTypeId);
		return listKPI;
	}
	
	public static List<GenericValue> getListKPIInCurrenDate(Delegator delegator, String periodTypeId) throws GenericEntityException{
		return getListKPIInCurrenDateByPartyId(delegator, null, periodTypeId);
	}
	
	public static List<GenericValue> getListAllKPIInCurrenDateByPartyId(Delegator delegator, String partyId) throws GenericEntityException{
		List<GenericValue> listKPI = FastList.newInstance();
		Timestamp now = UtilDateTime.nowTimestamp();
		listKPI = getListKPIEnableInSpecificDateByPartyId(delegator, partyId, now, null);
		return listKPI;
	}
	
	public static Map<String,Object> getListAllKPIInCurrenDateByPartyIdService(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String partyId = (String) context.get("partyId");
		List<GenericValue> listIterator = FastList.newInstance();
		Timestamp now = UtilDateTime.nowTimestamp();
		listIterator = getListKPIEnableInSpecificDateByPartyId(delegator, partyId, now, null);
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getPerfCriteria(Delegator delegator, String criteriaId){
		Map<String, Object> Map = FastMap.newInstance();
		try {
			GenericValue PerfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			Map.putAll(PerfCriteria);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return Map;
	}
	
	public static BigDecimal calcKPIPoint(Delegator delegator, List<GenericValue> criteriaList, String criteriaId) throws GenericEntityException {
		BigDecimal point = BigDecimal.ZERO;
		BigDecimal tempPoint = BigDecimal.ZERO;
		GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
		String perfCriDevelopmetTypeId = perfCriteria.getString("perfCriDevelopmetTypeId");
		if(UtilValidate.isNotEmpty(criteriaList)){
			for(GenericValue criteria: criteriaList){
				String hasItem = criteria.getString("hasItem");
				if("Y".equals(hasItem)){
					String partyId = criteria.getString("partyId");
					Timestamp fromDate = criteria.getTimestamp("fromDate");
					Timestamp dateReviewed = criteria.getTimestamp("dateReviewed");
					String enumIdKpiCalc = criteria.getString("enumIdKpiCalc");
					List<GenericValue> partyPerfCriteriaItemProductList = delegator.findByAnd("PartyPerfCriteriaItemProduct", 
							UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "criteriaId", criteriaId), null, false);
					BigDecimal tempResult = null;
					for(GenericValue partyPerfCriteriaItemProduct: partyPerfCriteriaItemProductList){
						String productId = partyPerfCriteriaItemProduct.getString("productId");
						BigDecimal quantityTarget = partyPerfCriteriaItemProduct.getBigDecimal("quantityTarget");
						GenericValue partyPerfCriItemProductResult = delegator.findOne("PartyPerfCriItemProductResult", 
								UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "criteriaId", criteriaId,
										"productId", productId, "dateReviewed", dateReviewed), false);
						BigDecimal result = partyPerfCriItemProductResult.getBigDecimal("result");
						if(quantityTarget.compareTo(BigDecimal.ZERO) != 0 && result != null){
							BigDecimal itemResult = result.divide(quantityTarget, 5, RoundingMode.HALF_UP);
							itemResult = itemResult.multiply(new BigDecimal("100"));
							if(tempResult == null){
								tempResult = itemResult;
							}
							if("KPI_CALC_MIN".equals(enumIdKpiCalc)){
								if(itemResult.compareTo(tempResult) < 0){
									tempResult = itemResult;
								}
							}else if("KPI_CALC_MAX".equals(enumIdKpiCalc)){
								if(itemResult.compareTo(tempResult) > 0){
									tempResult = itemResult;
								}
							}else if("KPI_CALC_AVG".equals(enumIdKpiCalc)){
								tempResult = tempResult.add(itemResult);
							}
						}
					}
					if("KPI_CALC_AVG".equals(enumIdKpiCalc) && partyPerfCriteriaItemProductList.size() > 0){
						tempResult = tempResult.divide(new BigDecimal(partyPerfCriteriaItemProductList.size()), 5, RoundingMode.HALF_UP);
					}
					tempPoint = tempPoint.add(tempResult);
				}else{
					BigDecimal result = criteria.getBigDecimal("result");
					BigDecimal target = criteria.getBigDecimal("target");
					/*BigDecimal weight = criteria.getBigDecimal("weight");
					weight = weight.multiply(new BigDecimal(100));*/
					if(target.compareTo(BigDecimal.ZERO) != 0 && result != null){
						BigDecimal tempResult = BigDecimal.ZERO;
						if("INCREASING".equals(perfCriDevelopmetTypeId)){
							tempResult = result.divide(target, 5, RoundingMode.HALF_UP);
						}else if("DECREASING".equals(perfCriDevelopmetTypeId)){
							tempResult = target.subtract(result).divide(target, 5, RoundingMode.HALF_UP);
						}else if("UNCHANGING".equals(perfCriDevelopmetTypeId)){
							tempResult = BigDecimal.ONE.subtract(target.subtract(result).abs().divide(target, 5, RoundingMode.HALF_UP));
						}
						tempResult = tempResult.multiply(new BigDecimal("100"));
						tempPoint = tempPoint.add(tempResult);
					}
				}
			}
			tempPoint = tempPoint.divide(new BigDecimal(criteriaList.size()), RoundingMode.HALF_UP);
			point = point.add(tempPoint);
		}
		//point = point.divide(new BigDecimal(criteriaIdList.size()), RoundingMode.HALF_UP);
		return point;
	}
	public static String getPerfCriteriaRateGradeByPoint(Delegator delegator, BigDecimal point) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("fromRating", EntityJoinOperator.LESS_THAN_EQUAL_TO, point));
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("toRating", null),
											EntityJoinOperator.OR,
											EntityCondition.makeCondition("toRating", EntityJoinOperator.GREATER_THAN_EQUAL_TO, point)));
		List<GenericValue> perfCriteriaRateGradeList = delegator.findList("PerfCriteriaRateGrade", 
				EntityCondition.makeCondition(conds), null, UtilMisc.toList("fromRating"), null, false);
		if(UtilValidate.isEmpty(perfCriteriaRateGradeList)){
			perfCriteriaRateGradeList = delegator.findByAnd("PerfCriteriaRateGrade", null, UtilMisc.toList("fromRating"), false);
		}
		if(UtilValidate.isNotEmpty(perfCriteriaRateGradeList)){
			return perfCriteriaRateGradeList.get(0).getString("perfCriteriaRateGradeId");
		}
		return null;
	}
	
	public static List<Map<String, Object>> getListMapDateFromTo(Timestamp fromDate, Timestamp thruDate, String periodTypeId){
		List<Map<String,Object>> listMap = FastList.newInstance();
		Timestamp fromDate_start = null;
		Date fromDate_start_date = null;
		Timestamp fromDate_end = null;
		Timestamp thruDate_start = null;
		Date thruDate_start_date = null;
		
		Date fromDate_date = new Date(fromDate.getTime());
		Date thruDate_date = new Date(thruDate.getTime());
		Calendar cal = Calendar.getInstance();
		
		if(periodTypeId.equals("DAILY")){
			fromDate_start = UtilDateTime.getDayStart(fromDate);
			fromDate_start_date = new Date(fromDate_start.getTime());
			fromDate_end = UtilDateTime.getDayEnd(fromDate);
			thruDate_start = UtilDateTime.getDayStart(thruDate);
			thruDate_start_date = new Date(thruDate_start.getTime());
			cal.setTime(fromDate_start_date);
			int day_start = cal.get(Calendar.DAY_OF_YEAR);
			
			cal.setTime(thruDate_start_date);
			int day_end = cal.get(Calendar.DAY_OF_YEAR);
			
			if(day_end == day_start){
				Map<String, Object> map = FastMap.newInstance();
				map.put("fromDate", fromDate_start);
				map.put("thruDate", fromDate_end);
				listMap.add(map);
			}else{
				for(int i = 0; i <= (day_end - day_start) ; i++){
					Map<String, Object> map = FastMap.newInstance();
					cal.setTime(fromDate_start_date);
					cal.add(Calendar.DAY_OF_YEAR, i);
					java.util.Date new_date = cal.getTime();
					Timestamp new_date_time = new Timestamp(new_date.getTime());
					Timestamp new_date_start = UtilDateTime.getDayStart(new_date_time);
					Timestamp new_date_end = UtilDateTime.getDayEnd(new_date_time);
					map.put("fromDate", new_date_start);
					map.put("thruDate", new_date_end);
					listMap.add(map);
				}
			}
		}else if(periodTypeId.equals("WEEKLY")){
			fromDate_start = UtilDateTime.getWeekStart(fromDate);
			fromDate_start_date = new Date(fromDate_start.getTime());
			fromDate_end = UtilDateTime.getWeekEnd(thruDate);
			thruDate_start = UtilDateTime.getWeekStart(thruDate);
			thruDate_start_date = new Date(thruDate_start.getTime());
			cal.setTime(fromDate_start_date);
			int week_start = cal.get(Calendar.WEEK_OF_YEAR);
			
			cal.setTime(thruDate_start_date);
			int week_end = cal.get(Calendar.WEEK_OF_YEAR);
			
			if(week_start == week_end){
				Map<String, Object> map = FastMap.newInstance();
				map.put("fromDate", fromDate_start);
				map.put("thruDate", fromDate_end);
				listMap.add(map);
			}else{
				for(int i = 0; i<= (week_end - week_start); i++){
					Map<String, Object> map = FastMap.newInstance();
					cal.setTime(fromDate_start_date);
					cal.add(Calendar.WEEK_OF_YEAR, i);
					java.util.Date new_date = cal.getTime();
					Timestamp new_date_time = new Timestamp(new_date.getTime());
					Timestamp new_date_start = UtilDateTime.getWeekStart(new_date_time);
					Timestamp new_date_end = UtilDateTime.getWeekEnd(new_date_time);
					map.put("fromDate", new_date_start);
					map.put("thruDate", new_date_end);
					listMap.add(map);
				}
			}
		}else if(periodTypeId.equals("MONTHLY")){
			fromDate_start = UtilDateTime.getMonthStart(fromDate);
			fromDate_start_date = new Date(fromDate_start.getTime());
			fromDate_end = DateUtil.getDateEndOfMonth(fromDate_date);
			thruDate_start = UtilDateTime.getMonthStart(thruDate);
			thruDate_start_date = new Date(thruDate_start.getTime());
			
			cal.setTime(fromDate_start_date);
			int month_start = cal.get(Calendar.MONTH);
			int year_start = cal.get(Calendar.YEAR);
			
			cal.setTime(thruDate_start_date);
			int month_end = cal.get(Calendar.MONTH);
			int year_end = cal.get(Calendar.YEAR);
			int month_end_check = month_end + (year_end - year_start) *12;
			
			if(month_start == month_end_check){
				Map<String, Object> map = FastMap.newInstance();
				map.put("fromDate", fromDate_start);
				map.put("thruDate", fromDate_end);
				listMap.add(map);
			}else{
				for(int i=0; i <= (month_end_check - month_start); i++){
					Map<String, Object> map = FastMap.newInstance();
					cal.setTime(fromDate_start_date);
					cal.add(Calendar.MONTH, i);
					java.util.Date new_date = cal.getTime();
					Timestamp new_date_time = new Timestamp(new_date.getTime());
					Timestamp new_date_start = UtilDateTime.getMonthStart(new_date_time);
					Timestamp new_date_end = DateUtil.getDateEndOfMonth(new_date);
					map.put("fromDate", new_date_start);
					map.put("thruDate", new_date_end);
					listMap.add(map);
				}
			}
		}else if(periodTypeId.equals("QUARTERLY")){
			fromDate_start = DateUtil.getDateStartOfQuarter(fromDate_date);
			fromDate_start_date = new Date(fromDate_start.getTime());
			fromDate_end = DateUtil.getDateEndOfQuarter(fromDate_date);
			thruDate_start = DateUtil.getDateStartOfQuarter(thruDate_date);
			thruDate_start_date = new Date(thruDate_start.getTime());
			
			cal.setTime(fromDate_start_date);
			int year_start = cal.get(Calendar.YEAR);
			int quarter_start = cal.get(Calendar.MONTH)/3 + 1;
			
			cal.setTime(thruDate_start);
			int year_end = cal.get(Calendar.YEAR);
			int quarter_end = cal.get(Calendar.MONTH)/3 + 1;
			int quarter_end_check = quarter_end + (year_end - year_start) * 4;
			
			if(quarter_start == quarter_end_check){
				Map<String, Object> map = FastMap.newInstance();
				map.put("fromDate", fromDate_start);
				map.put("thruDate", fromDate_end);
				listMap.add(map);
			}else{
				for(int i = 0; i <= (quarter_end_check - quarter_start); i++){
					Map<String, Object> map = FastMap.newInstance();
					cal.setTime(fromDate_start_date);
					cal.add(Calendar.MONTH, i * 3);
					java.util.Date new_date = cal.getTime();
					Timestamp new_date_time = new Timestamp(new_date.getTime());
					Date new_date_time_date = new Date(new_date_time.getTime());
					Timestamp new_date_start = DateUtil.getDateStartOfQuarter(new_date_time_date);
					Timestamp new_date_end = DateUtil.getDateEndOfQuarter(new_date_time_date);
					map.put("fromDate", new_date_start);
					map.put("thruDate", new_date_end);
					listMap.add(map);
				}
			}
		}else{
			Map<String, Object> map = FastMap.newInstance();
			map.put("fromDate", fromDate);
			map.put("thruDate", thruDate);
			listMap.add(map);
		}
		
		return listMap;
	}
	public static Timestamp startDayMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		
		cal.set(year, month -1, 1);
		java.util.Date time_date = cal.getTime();
		Timestamp time = new Timestamp(time_date.getTime());
		return time;
	}
	public static boolean checkSetupKpiPolicyItem(BigDecimal x, BigDecimal fromRating, BigDecimal y, BigDecimal toRating){
		if(UtilValidate.isNotEmpty(y)){
			if((fromRating.compareTo(x) >= 0 && fromRating.compareTo(y) <= 0) || (toRating.compareTo(x) >= 0 && toRating.compareTo(y) <= 0)){
				return false;
			}else{
				if(fromRating.compareTo(x) < 0 && toRating.compareTo(x) >= 0){
					return false;
				}
			}
		}else{
			if(toRating.compareTo(x) > 0){
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkSetupKpiPolicy(Timestamp x, Timestamp x1, Timestamp y, Timestamp y1){
		if(UtilValidate.isEmpty(y)){
			if(UtilValidate.isEmpty(y1)){
				return false;
			}else{
				if(y1.after(x)){
					return false;
				}
			}
		}else{
			if(UtilValidate.isEmpty(y1)){
				if(y.after(x1)){
					return false;
				}
			}else{
				if((x.before(y1) && x.after(x1)) || (y.before(y1) && y.after(x1))){
					return false;
				}else{
					if((x.before(x1) && y.after(x1)) || (x.after(y1) && (y.before(x1)))){
						return false;
					}
				}
			}
		}
		
		return true;
	}
	public static Map<String, Object> getBonusPunishmentAmountKPI(Delegator delegator, BigDecimal point, String criteriaId, Timestamp moment, 
			BigDecimal result, BigDecimal target) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		conds.add(EntityUtil.getFilterByDateExpr(moment));
		List<GenericValue> perfCriteriaPolicyList = delegator.findList("PerfCriteriaPolicy", EntityCondition.makeCondition(conds), null, 
				UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isNotEmpty(perfCriteriaPolicyList)){
			GenericValue  perfCriteriaPolicy = perfCriteriaPolicyList.get(0);
			String perfCriteriaPolicyId = perfCriteriaPolicy.getString("perfCriteriaPolicyId");
			conds.clear();
			conds.add(EntityCondition.makeCondition("perfCriteriaPolicyId", perfCriteriaPolicyId));
			conds.add(EntityCondition.makeCondition("fromRating", EntityJoinOperator.LESS_THAN_EQUAL_TO, point));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("toRating", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("toRating", EntityJoinOperator.GREATER_THAN_EQUAL_TO, point)));
			List<GenericValue> perfCriteriaPolicyItemList = delegator.findList("PerfCriteriaPolicyItem", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaPolicyItemList)){
				GenericValue perfCriteriaPolicyItem = perfCriteriaPolicyItemList.get(0);
				Map<String, Object> resultServices = FastMap.newInstance();
				String kpiCalcTypeEnumId = perfCriteriaPolicyItem.getString("kpiCalcTypeEnumId");
				BigDecimal amount = BigDecimal.ZERO;
				if("KPI_CALC_FORMULA".equals(kpiCalcTypeEnumId)){
					BigDecimal rate = perfCriteriaPolicyItem.getBigDecimal("rate");
					String bonusBaseOnEnumId = perfCriteriaPolicyItem.getString("bonusBaseOnEnumId");
					if("KPI_BASEON_TARGET".equals(bonusBaseOnEnumId)){
						amount = rate.multiply(target);
					}else if("KPI_BASEON_ACTUAL".equals(bonusBaseOnEnumId)){
						amount = rate.multiply(result);
					}
				}else{
					amount = perfCriteriaPolicyItem.getBigDecimal("amount");
				}
				resultServices.put("amount", amount);
				resultServices.put("kpiPolicyEnumId", perfCriteriaPolicyItem.get("kpiPolicyEnumId"));
				return resultServices;
			}
		}
		return null;
	}
	
	public static List<String> getListKPIForDistributor(){
		List<String> retList = FastList.newInstance();
		retList.add(PropertiesUtil.KPI_SKU);
		retList.add(PropertiesUtil.KPI_TURN_OVER);
		return retList;
	}
}
