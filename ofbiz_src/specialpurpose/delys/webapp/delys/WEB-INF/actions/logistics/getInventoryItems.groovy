import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');

Map<String, Object> result = dispatcher.runSync("performFind", UtilMisc.<String, Object>toMap("entityName", "InventoryItemAndLocation"
	,"inputFields", parameters, "userLogin", userLogin));
EntityListIterator listIt = (EntityListIterator) result.get("listIt");
if (listIt){
	listItems = listIt.getCompleteList();
	listIt.close();
	context.listItems = listItems;
}
String parametersStr = "";
if (parameters.inventoryItemId){
	parametersStr = parametersStr + "&inventoryItemId=" +parameters.inventoryItemId;
}
if (parameters.inventoryItemId_op){
	parametersStr = parametersStr + "&inventoryItemId_op=" + parameters.inventoryItemId_op;
}
if (parameters.facilityId){
	parametersStr = parametersStr + "&facilityId=" + parameters.facilityId;
}
if (parameters.productId_op){
	parametersStr = parametersStr + "&productId_op=" + parameters.productId_op;
}
if (parameters.productId){
	parametersStr = parametersStr + "&productId=" + parameters.productId;
}
if (parameters.productId_ic){
	parametersStr = parametersStr + "&productId_ic=" + parameters.productId_ic;
}
if (parameters.internalName_op){
	parametersStr = parametersStr + "&internalName_op=" + parameters.internalName_op;
}
if (parameters.internalName){
	parametersStr = parametersStr + "&internalName=" + parameters.internalName;
}
if (parameters.internalName_ic){
	parametersStr = parametersStr + "&internalName_ic=" + parameters.internalName_ic;
}
if (parameters.inventoryItemId_ic){
	parametersStr = parametersStr + "&inventoryItemId_ic=" + parameters.inventoryItemId_ic;
}
if (parameters.datetimeReceived_fld0_value){
	parametersStr = parametersStr + "&datetimeReceived_fld0_value=" + parameters.datetimeReceived_fld0_value;
}
if (parameters.datetimeReceived_fld0_op){
	parametersStr = parametersStr + "&datetimeReceived_fld0_op=" + parameters.datetimeReceived_fld0_op;
}
if (parameters.datetimeReceived_fld1_value){
	parametersStr = parametersStr + "&datetimeReceived_fld1_value=" + parameters.datetimeReceived_fld1_value;
}
if (parameters.datetimeReceived_fld1_op){
	parametersStr = parametersStr + "&datetimeReceived_fld1_op=" + parameters.datetimeReceived_fld1_op;
}
if (parameters.containerId_op){
	parametersStr = parametersStr + "&containerId_op=" + parameters.containerId_op;
}
if (parameters.containerId){
	parametersStr = parametersStr + "&containerId=" + parameters.containerId;
}
if (parameters.containerId_ic){
	parametersStr = parametersStr + "&containerId_ic=" + parameters.containerId_ic;
}
if (parameters.lotId_op){
	parametersStr = parametersStr + "&lotId_op=" + parameters.lotId_op;
}
if (parameters.lotId){
	parametersStr = parametersStr + "&lotId=" + parameters.lotId;
}
if (parameters.lotId_ic){
	parametersStr = parametersStr + "&lotId_ic=" + parameters.lotId_ic;
}
if (parameters.serialNumber_op){
	parametersStr = parametersStr + "&serialNumber_op=" + parameters.serialNumber_op;
}
if (parameters.serialNumber){
	parametersStr = parametersStr + "&serialNumber=" + parameters.serialNumber;
}
if (parameters.serialNumber_ic){
	parametersStr = parametersStr + "&serialNumber_ic=" + parameters.serialNumber_ic;
}
if (parameters.statusId){
	parametersStr = parametersStr + "&statusId=" + parameters.statusId;
}

if (parametersStr) {
	context.parametersStr = parametersStr;	
}
