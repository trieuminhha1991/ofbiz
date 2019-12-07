import com.olbius.acc.report.financialstm.Fomular
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

import java.math.RoundingMode

List listCond = new ArrayList();
// get 4 nearest closed CustomTimePeriod
listCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, parameters.organizationPartyId));
listCond.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
lastTimePeriodHistory = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("thruDate DESC"), null, false);
if(!lastTimePeriodHistory || lastTimePeriodHistory.size() < 1){
	return;
}

String organizationPartyId = (String)parameters.get("organizationPartyId");
List<EntityCondition> listConds = new ArrayList<EntityCondition>();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9000"));
listConds.add(EntityCondition.makeCondition("flag", "T"));
List<GenericValue> listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportId1 = "";
if(listReport.size() > 0)
    reportId1 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9000"));
listConds.add(EntityCondition.makeCondition("flag", "M"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportIdM1 = "";
if(listReport.size() > 0)
    reportIdM1 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9001"));
listConds.add(EntityCondition.makeCondition("flag", "T"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportId2 = "";
if(listReport.size() > 0)
    reportId2 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9001"));
listConds.add(EntityCondition.makeCondition("flag", "M"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportIdM2 = "";
if(listReport.size() > 0)
    reportIdM2 = EntityUtil.getFirst(listReport).getString("reportId");

List listCycle = new ArrayList();
List listROE = new ArrayList();
lastTimePeriodHistory.each{ period->
	listCycle.add(period.get("periodName"));
	netIncome = Fomular.evalueTargetValue(delegator, "9784", reportId2, reportIdM2, reportId2, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	totalResources = Fomular.evalueTargetValue(delegator, "9746", reportId1, reportIdM1, reportId1, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	if(totalResources.compareTo(BigDecimal.ZERO) != 0){
		listROE.add(netIncome.divide(totalResources, 2, RoundingMode.DOWN));
	} else {
		listROE.add(BigDecimal.ZERO);
	}
}
context.listCycle = listCycle;
context.listROE = listROE;