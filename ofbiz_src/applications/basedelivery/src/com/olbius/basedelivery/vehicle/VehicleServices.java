package com.olbius.basedelivery.vehicle;

import com.olbius.basesales.util.SalesUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by user on 11/23/17.
 */
public class VehicleServices {

    public static final String module = VehicleServices.class.getName();
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String loading = (String) context.get("loading");
            String licensePlate = (String) context.get("licensePlate");
            String volume = (String) context.get("volume");
            String reqNo = (String) context.get("reqNo");
            String vehicleTypeId = (String) context.get("vehicleTypeId");
            String description = (String) context.get("description");
            String width = (String) context.get("width");
            String height = (String) context.get("height");
            String longitude = (String) context.get("longitude");
            GenericValue vehicleV2 = delegator.makeValue("VehicleV2");
            vehicleV2.setNextSeqId();
            vehicleV2.set("loading", loading);
            vehicleV2.set("licensePlate", licensePlate);
            vehicleV2.set("volume", volume);
            vehicleV2.set("reqNo", reqNo);
            vehicleV2.set("vehicleTypeId", vehicleTypeId);
            vehicleV2.set("description", description);
            vehicleV2.set("width", width);
            vehicleV2.set("height", height);
            vehicleV2.set("longitude", longitude);
            delegator.create(vehicleV2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> updateVehicle(DispatchContext dctx, Map<String, ? extends
            Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        JSONArray jsonArray = JSONArray.fromObject(request.getParameter
                ("listVehicle"));

        List<GenericValue> listVehicle = new LinkedList<GenericValue>();
        for(int i = 0; i < jsonArray.size(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            GenericValue vehicle = delegator.makeValue("VehicleV2");
            String vehicleId = jsonObject.getString("vehicleId");
            String loading = jsonObject.getString("loading");
            String licensePlate = jsonObject.getString("licensePlate");
            String volume = jsonObject.getString("volume");
            String reqNo = jsonObject.getString("reqNo");
            String vehicleTypeId = jsonObject.getString("vehicleTypeId");
            String description = jsonObject.getString("description");
            String longitude = jsonObject.getString("longitude");
            String width = jsonObject.getString("width");
            String height = jsonObject.getString("height");
            vehicle.set("vehicleId", vehicleId);
            vehicle.set("loading", loading);
            vehicle.set("licensePlate", licensePlate);
            vehicle.set("volume", volume);
            vehicle.set("reqNo", reqNo);
            vehicle.set("vehicleTypeId", vehicleTypeId);
            vehicle.set("description", description);
            vehicle.set("longitude", longitude);
            vehicle.set("width", width);
            vehicle.set("height", height);
            listVehicle.add(vehicle);
        }
        try {
            delegator.storeAll(listVehicle);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Errors");
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        return result;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        EntityListIterator listIterator = null;
        Map<String, String> mapCondition = new HashMap<String, String>();
        EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        listAllConditions.add(tmpConditon);
        listAllConditions.add(EntityUtil.getFilterByDateExpr());
        try {
            GenericValue userLogin = (GenericValue)context.get("userLogin");
            EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);

            listIterator = delegator.find("VehicleV2", cond, null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling jqGetVehicle service: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetVehicleBySupplierId(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        EntityListIterator listIterator = null;
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        String supplierId = SalesUtil.getParameter(parameters, "supplierId");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();

        try {

            List<GenericValue> supplierVehicles = delegator.findList("SupplierVehicle", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId), null, null, null, false);
            if(supplierVehicles == null || supplierVehicles.size() == 0) {
                successResult.put("listIterator", null);
                return successResult;
            }
            List<String> listVehicleId = new ArrayList<>();
            for(GenericValue supplierVehicle : supplierVehicles) {
                listVehicleId.add((String) supplierVehicle.get("vehicleId"));
            }
            listAllConditions.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN, listVehicleId));
            EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
            listIterator = delegator.find("VehicleV2", cond, null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling jqGetVehicle service: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

}
