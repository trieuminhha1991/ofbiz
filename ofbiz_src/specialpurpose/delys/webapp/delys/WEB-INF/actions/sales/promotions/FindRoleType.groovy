import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



if (context.noConditionFind == null) {
	context.noConditionFind = parameters.noConditionFind;
}
if (context.noConditionFind == null) {
	//context.noConditionFind = UtilProperties.getPropertyValue("widget", "widget.defaultNoConditionFind");
	context.noConditionFind = "Y";
}
performResult = dispatcher.runSync("performFind", [entityName : context.entityName,                                                   
                                                   inputFields : parameters,
                                                   noConditionFind : context.noConditionFind]);
if (performResult.listIt == null) {
	Debug.log("No list found for query string + [" + performResult.queryString + "]");
}
context.listIt = performResult.listIt;
context.queryString = performResult.queryString;
context.queryStringMap = performResult.queryStringMap;