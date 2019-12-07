import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;


List mainAndExprs = FastList.newInstance();
String strFType = parameters.periodtype;
if(strFType == null){
	strFType = "FISCAL_QUARTER";
}
mainAndExprs.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, strFType));
mainAndExprs.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "Y"));
listCTP = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), null, ["thruDate DESC"], null, false);

context.listCTP = listCTP;
listHeader = FastList.newInstance();
listKey= FastList.newInstance();
listCTP.each{ tmpelem ->
	listHeader.add(tmpelem.get("periodName"));
	listKey.add("F" + tmpelem.get("customTimePeriodId"));
}
context.listHeader = listHeader;
context.listKey = listKey;
