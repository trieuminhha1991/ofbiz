import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;

if (context.filterByDate == null) {
    context.filterByDate = parameters.filterByDate;
}
context.noConditionFind = "Y";
result = ServiceUtil.returnSuccess();
String strSortOrder = "";

parameters = context.parameters.parameters;
if(parameters.sortorder != null && parameters.sortorder[0] == "asc" && parameters.sortdatafield != null){
	strSortOrder = parameters.sortdatafield[0];
}else if(parameters.sortdatafield != null){
	strSortOrder = "-" + parameters.sortdatafield[0];
}
if(strSortOrder==null){
	strSortOrder = "partyId";
}

tmpMap = new HashMap();
if(parameters.filterdatafield0 != null){
	tmpMap.put(parameters.filterdatafield0, parameters.filtervalue0);
}
prepareResult = dispatcher.runSync("prepareFind", [entityName : "PartyNameView",
                                                   orderBy : strSortOrder,
                                                   inputFields : convertMap(tmpMap),
                                                   filterByDate : context.filterByDate,
                                                   filterByDateValue : context.filterByDateValue,
                                                   noConditionFind:"Y",
                                                   userLogin : context.userLogin] );

exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")
            , EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
CondList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
CondList1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
statusPartyDisable = EntityCondition.makeCondition([CondList1, CondList], EntityOperator.OR);

entityConditionList = null;
if (prepareResult != null && prepareResult.entityConditionList != null) {
    ConditionList = [ prepareResult.entityConditionList, statusPartyDisable ];
    entityConditionList = EntityCondition.makeCondition(ConditionList);
}else if (context.noConditionFind == "Y") {
    entityConditionList = statusPartyDisable;
}

for(int i = 0;i < context.listAllConditions.size();i++){
	entityConditionList = EntityCondition.makeCondition([entityConditionList, context.listAllConditions.get(i)], EntityOperator.AND);
} 
context.listAllConditions.add(entityConditionList);
executeResult = dispatcher.runSync("executeFind", [entityName : "PartyNameView",
                                                   orderByList : prepareResult.orderByList,
                                                   entityConditionList : entityConditionList,
                                                   noConditionFind :"Y"
                                                   ] );
if (executeResult.listIt == null) {
    Debug.log("No list found for query string + [" + prepareResult.queryString + "]");
}
result.listIterator = executeResult.listIt;
return result;

//context.queryString = prepareResult.queryString;
//context.queryStringMap = prepareResult.queryStringMap;

Map<String, String> convertMap(Map<String, String[]> mapArray){
    	Map<String, String> returnValue = new HashMap<String, String>();
    	Iterator it = mapArray.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if(pairs.getValue() instanceof String[]){
            	returnValue.put(pairs.getKey().toString(), ((String[])pairs.getValue())[0]);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    	return returnValue;
    }