import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

/**
 * Created by user on 11/29/17.
 */

tripId = parameters.tripId;
ordCond = null;
GenericValue trip = null
if(tripId) {
    ordCond = EntityCondition.makeCondition("tripId", EntityOperator.EQUALS, tripId);
    trip = delegator.findOne("Trip", UtilMisc.toMap("tripId", tripId), false);
    context.trip = trip;
}
List<GenericValue> listTrip = delegator.findList("Trip", ordCond , null, null, null, false);
context.listTrip = listTrip

if(trip) {
    String currentStatusId = trip.statusId;
    GenericValue currentStatus = delegator.findOne("StatusItem", ["statusId" : currentStatusId], true);
    String currentStatusMsg = "";
    if (currentStatus) currentStatusMsg = (String) currentStatus.get("description", locale);
    context.currentStatusId = currentStatusId;
    context.currentStatusMsg = currentStatusMsg;
}