import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

List<GenericValue> listShipmentTypes = new ArrayList<GenericValue>();
GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", parameters.shipmentId));
String shipmentTypeId = null;
if (shipment != null){
	shipmentTypeId = (String)shipment.get("shipmentTypeId");
	if (shipmentTypeId != null){
		GenericValue shipmentType = delegator.findOne("ShipmentType", false, UtilMisc.toMap("shipmentTypeId", shipmentTypeId));
		listShipmentTypes.add(shipmentType);
	}
} else {
	List<GenericValue> listAllTypes = new ArrayList<GenericValue>();
	listAllTypes = delegator.findByAnd("ShipmentType", null);
	
	for (GenericValue type : listAllTypes){
		List<GenericValue> listChildTypes = delegator.findList("ShipmentType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", (String)type.get("shipmentTypeId"))), null, null, null, false);
		if (((type.get("parentTypeId") == null) && listChildTypes.isEmpty()) || ((type.get("parentTypeId") != null))){
			listShipmentTypes.add(type);
		}		
	}
}
context.listShipmentTypes = listShipmentTypes;