import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

List<GenericValue> listShipmentRoutes = delegator.findList("ShipmentRouteSegment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
List<GenericValue> listAllFacilities = delegator.findList("Facility", null, null, null, null, false);
List<GenericValue> listOriginFacilities = new ArrayList<GenericValue>();
List<GenericValue> listDestFacilities = new ArrayList<GenericValue>();
GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
GenericValue originfacility =  delegator.findOne("Facility", UtilMisc.toMap("facilityId", shipment.get("originFacilityId")), false);
GenericValue destfacility =  delegator.findOne("Facility", UtilMisc.toMap("facilityId", shipment.get("destinationFacilityId")), false);
listOriginFacilities.add(originfacility);
listDestFacilities.addAll(listAllFacilities);
listDestFacilities.remove(destfacility);
listDestFacilities.remove(originfacility);
if (!listShipmentRoutes.isEmpty()){
	for (GenericValue route : listShipmentRoutes){
		GenericValue originfacilityTmp =  delegator.findOne("Facility", UtilMisc.toMap("facilityId", route.get("originFacilityId")), false);
		if (!listOriginFacilities.contains(originfacilityTmp)){
			listOriginFacilities.add(originfacilityTmp);
		}
	}	
}
context.listOriginFacilities = listOriginFacilities;
context.listDestFacilities = listDestFacilities;