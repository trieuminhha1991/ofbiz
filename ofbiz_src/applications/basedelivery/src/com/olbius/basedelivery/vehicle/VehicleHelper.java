package com.olbius.basedelivery.vehicle;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Created by user on 12/6/17.
 */
public class VehicleHelper {

    public static final String ENTITY_NAME = "VehicleV2";
    public static String getVehicleName(Delegator delegator, String vehicleId) {
        try {
            GenericValue vehicle = delegator.findOne(ENTITY_NAME, UtilMisc.toMap("vehicleId", vehicleId), false);
            if(vehicle == null) return vehicleId;
            return (vehicle.get("licensePlate")).toString() + "[" + vehicleId + "]";
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return vehicleId;
        }
    }
}
