import org.ofbiz.entity.GenericValue

List<GenericValue> listVehicles = delegator.findList("VehicleV2",
        null, null, null, null, false);
context.listVehicles = listVehicles;