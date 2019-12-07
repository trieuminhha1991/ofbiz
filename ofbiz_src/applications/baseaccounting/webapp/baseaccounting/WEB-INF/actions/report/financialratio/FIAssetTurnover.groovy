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
listConds.add(EntityCondition.makeCondition("flag", "M"));
List<GenericValue> listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportId1 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9000"));
listConds.add(EntityCondition.makeCondition("flag", "T"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportIdT1 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9001"));
listConds.add(EntityCondition.makeCondition("flag", "M"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportId2 = EntityUtil.getFirst(listReport).getString("reportId");

listConds.clear();
listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
listConds.add(EntityCondition.makeCondition("reportTypeId", "9001"));
listConds.add(EntityCondition.makeCondition("flag", "T"));
listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
String reportIdT2 = EntityUtil.getFirst(listReport).getString("reportId");

List listCycle = new ArrayList();
List listAssetTurnovers = new ArrayList();
lastTimePeriodHistory.each{ period->
	listCycle.add(period.get("periodName"));
	asset = Fomular.evalueTargetValue(delegator, "9653", reportId1, reportId1, reportIdT1, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	netSales = Fomular.evalueTargetValue(delegator, "9769", reportId2, reportId2, reportIdT2, period.get("customTimePeriodId"), period.getString("isClosed"), parameters.organizationPartyId);
	if(asset.compareTo(BigDecimal.ZERO) != 0){
		listAssetTurnovers.add(netSales.divide(asset, 2, RoundingMode.DOWN));
	} else {
		listAssetTurnovers.add(BigDecimal.ZERO);
	}
}
context.listCycle = listCycle;
context.listAssetTurnovers = listAssetTurnovers;