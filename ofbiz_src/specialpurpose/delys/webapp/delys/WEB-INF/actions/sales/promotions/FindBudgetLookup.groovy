import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;


if (context.noConditionFind == null) {
	context.noConditionFind = parameters.noConditionFind;
}
if (context.noConditionFind == null) {
	context.noConditionFind = UtilProperties.getPropertyValue("widget", "widget.defaultNoConditionFind");
}
System.out.println("entityName"  + context.entityName);
result = dispatcher.runSync("performFind", [entityName : context.entityName,
											orderBy : context.orderBy,
											inputFields : parameters,	
											viewSize : viewSize,
											viewIndex : viewIndex,																			
											noConditionFind: context.noConditionFind]);

if (result.listIt == null) {
	Debug.log("No list found for query string + [" + result.queryString + "]");
}
listSize = result.listSize;
highIndex = (viewIndex + 1) * viewSize;
if(highIndex > listSize){
	highIndex = listSize;
}

EntityListIterator listIt = result.listIt;

lowIndex = (viewIndex * viewSize) + 1;

if(listIt){
	context.listIt = listIt.getPartialList(lowIndex, highIndex);
}
//println ("parameters.presentation: " + parameters.presentation);
context.lowIndex=lowIndex;
context.highIndex = highIndex;
context.viewSize = viewSize;
context.viewIndex = viewIndex;
context.paramList = result.queryString;
context.listSize = listSize;
context.queryString = result.queryString;
context.queryStringMap = result.queryStringMap;