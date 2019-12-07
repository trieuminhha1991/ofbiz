import java.text.DateFormat;
import java.util.Calendar;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

String customTimePeriodId = (String)parameters.get("customTimePeriodId");
String productId = (String)parameters.get("productId");

if(customTimePeriodId != null && productId != null){
	
	Date date = new Date();
	System.out.println("date:" +date);
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.HOUR, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	calendar.set(Calendar.MILLISECOND, 0);
	java.sql.Date currentDate = new java.sql.Date(calendar.getTimeInMillis());
	
	Conds2 = [];
	Conds2.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, currentDate ));
	Conds2.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, currentDate ));
	Conds2.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "IMPORT_MONTH"));
	planCond2 = EntityCondition.makeCondition(Conds2, EntityOperator.AND);
	List<GenericValue> listMonth = delegator.findList("CustomTimePeriod", planCond2, null, null, null, false);
	GenericValue month = EntityUtil.getFirst(listMonth);
	List<GenericValue> listPlanMonth = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", month.customTimePeriodId)), null, null, null,false);
	if(!UtilValidate.isEmpty(listPlanMonth)){
		GenericValue monthPlanHeaderId = EntityUtil.getFirst(listPlanMonth);
//		List<GenericValue> listMonthChildPlanId = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", yearPlanHeaderId.productPlanId)), selectField, null, null, false);
		
		List<Map> resultMonth = new ArrayList<Map>();
			List<GenericValue> productMonthPlans = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPlanId", monthPlanHeaderId.productPlanId)), null, null, null, false);
			GenericValue productMonth = EntityUtil.getFirst(productMonthPlans);
			resultMonth.addAll(productMonth);
			BigDecimal rec = new BigDecimal(1);
			if((BigDecimal)productMonth.recentPlanQuantity != null){
				(BigDecimal) rec = (BigDecimal)productMonth.recentPlanQuantity;
			}
		context.planQuantity = productMonth.planQuantity;
		context.recentPlanQuantity = rec;
//	System.out.println("KQ:" +productMonth.productPlanId);
//	System.out.println("KQ:" +productId);
		
	}
	
	
selectField = ["productPlanId"] as Set;
//Conds = [];
//Conds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
//planCond = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId);
List<GenericValue> listMonthCustomTimeId = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId, "periodTypeId", "IMPORT_MONTH")), null, null, null,false);
if(!UtilValidate.isEmpty(listMonthCustomTimeId)){
	List<Map> result = new ArrayList<Map>();
	for(listMonthCustom in listMonthCustomTimeId){
		List<GenericValue> listMonthPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listMonthCustom.customTimePeriodId)), null, null, null, false);
		if(listMonthPlan != null){
			GenericValue firstMonthPlan = EntityUtil.getFirst(listMonthPlan);
//			System.out.println("Kq1: " +productPlans);
			List<GenericValue> productPlans = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPlanId", firstMonthPlan.productPlanId)), null, null, null, false);
//			System.out.println("Kq1: " +productPlans);
			if(productPlans != null){
				GenericValue product = EntityUtil.getFirst(productPlans);
				BigDecimal recentPQ = new BigDecimal(1);
				if((BigDecimal)product.recentPlanQuantity != null){
					(BigDecimal) recentPQ = (BigDecimal)product.recentPlanQuantity;
				}
				product.put("recentPlanQuantity", recentPQ);
				result.addAll(product);
			}
			
		}
	}
	context.result = result;
}
}

