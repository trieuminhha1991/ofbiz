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

exprList = [];
String facilityId = parameters.facilityId;
String locationSeqId = parameters.info;
//
//expr = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
///*expr1 = EntityCondition.makeCondition("locationSeqId", EntityOperator.EQUALS, locationSeqId);*/
//exprList.add(expr);
///*exprList.add(expr1);*/
//Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

locationSeqId = "";
internalName = "";
expireDate = "";
quantityOnHand = "";
availableToPromise= "";
List<GenericValue> listItems = delegator.findList("ListInventoryItemForPhysical", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
if(!listItems.isEmpty()){
	for (GenericValue item in listItems) {
		locationSeqId = item.get("locationSeqId");
		internalName = item.get("internalName");
		expireDate = item.get("expireDate");
		quantityOnHand = item.get("quantityOnHandTotal");
		availableToPromise = item.get("availableToPromiseTotal");
	}
}