import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

/**
 * Created by user on 11/29/17.
 */

vehicleId = parameters.vehicleId;
ordCond = null;
if(vehicleId) {
    ordCond = EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId);
}
List<GenericValue> listVehicle = delegator.findList("VehicleV2", ordCond , null, null, null, false);
context.listVehicle = listVehicle;
