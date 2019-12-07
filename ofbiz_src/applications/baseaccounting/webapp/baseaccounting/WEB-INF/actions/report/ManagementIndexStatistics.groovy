import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.math.RoundingMode;

import com.olbius.acc.report.AccountingReportUtil;

import org.ofbiz.base.util.UtilMisc;

String strPeriodType = parameters.periodtype;
List listCond = new ArrayList();
// get 4 nearest closed CustomTimePeriod
listCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, parameters.organizationPartyId));
listCond.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, strPeriodType));
listCond.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "Y"));
lastTimePeriodHistory = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("thruDate DESC"), null, false);
if(!lastTimePeriodHistory || lastTimePeriodHistory.size() < 1){
	return;
}
// get list "LN sau thue"
List listProfitAfterTax = new ArrayList();
// get list "Tong tai san"
List listAssetTotal = new ArrayList();
// get list "Von chu so huu"
List listEquityTotal = new ArrayList();
// get list "Doanh thu"
List listIncome = new ArrayList();
// get list "ROS"
List listROS = new ArrayList();
// get list "ROA"
List listROA = new ArrayList();
// get list "ROE"
List listROE = new ArrayList();
// get list "Period name"
List listCycle = new ArrayList();
int icount = 0;
lastTimePeriodHistory.each{ period->
	listProfitAfterTax.add(AccountingReportUtil.evalueTargetValue(delegator, "9217", period.get("customTimePeriodId"), parameters.organizationPartyId));
	listAssetTotal.add(AccountingReportUtil.evalueTargetValue(delegator, "9062", period.get("customTimePeriodId"), parameters.organizationPartyId));
	listEquityTotal.add(AccountingReportUtil.evalueTargetValue(delegator, "9093", period.get("customTimePeriodId"), parameters.organizationPartyId));
	listIncome.add(AccountingReportUtil.evalueTargetValue(delegator, "9202", period.get("customTimePeriodId"), parameters.organizationPartyId));
	listCycle.add(period.get("periodName"));
	icount++;
	if(icount==6){
		return false;
	}
}
Collections.reverse(listEquityTotal);
Collections.reverse(listProfitAfterTax);
Collections.reverse(listAssetTotal);
Collections.reverse(listIncome);
Collections.reverse(listCycle);
// calculate ROS value
for(int i = 0; i < listIncome.size(); i++){
	BigDecimal bd1 = listProfitAfterTax.get(i);
	BigDecimal bd2 = listIncome.get(i);
	if(bd2.compareTo(0) == 0){
		listROS.add(0);
	}else{
		bd1 = bd1.divide(bd2, 2, RoundingMode.DOWN);
		bd1 = bd1.multiply(100);
		listROS.add(bd1); 
	}
}
// calculate ROA value
for(int i = 0; i < listIncome.size(); i++){
	BigDecimal bd1 = listProfitAfterTax.get(i);
	BigDecimal bd2 = listAssetTotal.get(i);
	if(bd2.compareTo(0) == 0){
		listROA.add(0);
	}else{
		bd1 = bd1.divide(bd2, 4, RoundingMode.DOWN);
		bd1 = bd1.multiply(100);
		listROA.add(bd1); 
	}
}
// calculate ROE value
for(int i = 0; i < listIncome.size(); i++){
	BigDecimal bd1 = listProfitAfterTax.get(i);
	BigDecimal bd2 = listEquityTotal.get(i);
	if(bd2.compareTo(0) == 0){
		listROE.add(0);
	}else{
		bd1 = bd1.divide(bd2, 4, RoundingMode.DOWN);
		bd1 = bd1.multiply(100);
		listROE.add(bd1); 
	}
}
context.listEquityTotal = listEquityTotal;
context.listProfitAfterTax = listProfitAfterTax;
context.listAssetTotal = listAssetTotal;
context.listIncome = listIncome;
context.listROS = listROS;
context.listROA = listROA;
context.listROE = listROE;
context.listCycle = listCycle;