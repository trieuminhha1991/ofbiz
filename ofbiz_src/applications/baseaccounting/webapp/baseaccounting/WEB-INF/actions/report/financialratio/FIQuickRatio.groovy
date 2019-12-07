import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.acc.report.financialstm.Fomular;
import java.math.RoundingMode;
import org.ofbiz.entity.util.EntityUtil;

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
String reportId = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9000"));
listConds.add(EntityCondition.makeCondition("flag", "M"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportIdM = EntityUtil.getFirst(listReport).getString("reportId");

List listCycle = new ArrayList();
List listQuickRatio = new ArrayList();
lastTimePeriodHistory.each{ period->
	listCycle.add(period.get("periodName"));
	currentAsset = Fomular.evalueTargetValue(delegator, "9654", reportId, reportIdM, reportId, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	accountReceivable = Fomular.evalueTargetValue(delegator, "9671", reportId, reportIdM, reportId, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	shortTermDebt = Fomular.evalueTargetValue(delegator, "9717", reportId, reportIdM, reportId, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	if(shortTermDebt.compareTo(BigDecimal.ZERO) != 0){
		listQuickRatio.add((currentAsset.subtract(accountReceivable)).divide(shortTermDebt, 2, RoundingMode.DOWN));
	} else {
		listQuickRatio.add(BigDecimal.ZERO);
	}
}
context.listCycle = listCycle;
context.listQuickRatio = listQuickRatio;