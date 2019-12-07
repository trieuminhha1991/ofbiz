import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

List<GenericValue> listVehicles = delegator.findList("VehicleV2",
        null, null, null, null, false);
context.listVehicles = listVehicles;
String tripId = parameters.tripId;
GenericValue trip = delegator.findOne("Trip", UtilMisc.toMap("tripId", tripId), false);
EntityCondition cond = EntityCondition.makeCondition("tripId", EntityOperator.EQUALS, tripId);
List<GenericValue> listTripDetail = delegator.findList("TripDetail", cond, null, null, null, false);
List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
for(GenericValue tripDetail : listTripDetail) {
    Map<String, Object> de = FastMap.newInstance();
    GenericValue delivery =delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", (String)tripDetail.get("deliveryId")), false);
    de.putAll(delivery);
    listIterator.add(de);
}
context.listDelivery = listIterator;
context.trip = trip;