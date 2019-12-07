package com.olbius.basedelivery;

import com.olbius.basedelivery.trip.TripWorker;
import com.olbius.basesales.util.SalesUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 11/28/17.
 */
public class DeliveryServices {

    public static final String module = DeliveryServices.class.getName();
    public static final String resource = "BaseDeliveryUiLabels";
    public static final String resourceError = "BaseDeliveryUiLabels";
    public static final String DELIVERY_PROPERTIES = "basedelivery.properties";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAllVehicles(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        List<GenericValue> listVehicles = delegator.findList("VehicleV2", EntityCondition
                .makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
        successResult.put("listIterator", listVehicles);
        successResult.put("TotalRows", String.valueOf(listVehicles.size()));
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAllVehicleType(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> listVehicleType = delegator.findList("VehicleType", null, null, null, null, false);
        successResult.put("listVehicleType", listVehicleType);
        return successResult;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListVehicleType(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator del = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<GenericValue> listIterator = FastList.newInstance();
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            EntityCondition tmpCondition = null;
            tmpCondition = EntityCondition.makeCondition(listAllConditions);
            listIterator = del.findList("VehicleType", tmpCondition, null, listSortFields, opts, false);
        } catch (Exception e) {
            e.printStackTrace();
            successResult = ServiceUtil.returnError("error");
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static Map<String, Object> getVehicleTypeName(DispatchContext ctx, Map<String, ? extends Object> context) {
        String vehicleTypeId = (String) context.get("vehicleTypeId");
        String name = null;
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue mech = delegator.findOne("VehicleType", UtilMisc.toMap("vehicleTypeId", vehicleTypeId), false);
            name = (String) mech.get("name");
        } catch (GenericEntityException e) {
            ServiceUtil.returnError(e.toString());
        }
        Map<String, Object> result = FastMap.newInstance();
        result.put("name", name);
        return result;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator del = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
        String TotalRows = "0";
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            listSortFields.add("deliveryDate DESC");
            EntityCondition tmpCondition = null;
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DLV_EXPORTED"
//                    , "DLV_PROPOSED"
            )));
            listAllConditions.add(EntityCondition.makeCondition("deliveryTypeId", "DELIVERY_SALES"));
            tmpCondition = EntityCondition.makeCondition(listAllConditions);
            List<GenericValue> listDelivery = del.findList("Delivery", tmpCondition, null, listSortFields, opts, false);
            TotalRows = String.valueOf(listDelivery.size());
            for(GenericValue item : listDelivery) {
                BigDecimal totalWeight = TripWorker.getWeightInDelivery(del, dispatcher, (String) item.get("deliveryId"));
                Map<String, Object> deliveryFinal = FastMap.newInstance();
                deliveryFinal.putAll(item);
                deliveryFinal.put("totalWeight", totalWeight);
                listIterator.add(deliveryFinal);
            }
        } catch (Exception e) {
            e.printStackTrace();
            successResult = ServiceUtil.returnError("error");
        }
        successResult.put("listIterator", listIterator);
        successResult.put("TotalRows", TotalRows);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDeliveryByGroup(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator del = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
        String TotalRows = "0";
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
            String productStoreGroupId = SalesUtil.getParameter(parameters, "productStoreGroupId");
            EntityCondition tmpCondition = null;
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DLV_EXPORTED")));
            listAllConditions.add(EntityCondition.makeCondition("deliveryTypeId", "DELIVERY_SALES"));
            if("EMPTY".equals(productStoreGroupId)) {
                listAllConditions.add(EntityCondition.makeCondition("primaryStoreGroupId", null));
            }
            else if(!"ALL".equals(productStoreGroupId) && !"EMPTY".equals(productStoreGroupId) && productStoreGroupId != null) {
                listAllConditions.add(EntityCondition.makeCondition("primaryStoreGroupId", productStoreGroupId));
            }
            tmpCondition = EntityCondition.makeCondition(listAllConditions);
            List<GenericValue> listDelivery = del.findList("DeliveryProductStoreGroup", tmpCondition, null, listSortFields, opts, false);
            for(GenericValue item : listDelivery) {
                BigDecimal totalWeight = TripWorker.getWeightInDelivery(del, dispatcher, (String) item.get("deliveryId"));
                Map<String, Object> deliveryFinal = FastMap.newInstance();
                deliveryFinal.putAll(item);
                deliveryFinal.put("totalWeight", totalWeight);
                listIterator.add(deliveryFinal);
            }
        } catch (Exception e) {
            e.printStackTrace();
            successResult = ServiceUtil.returnError("error");
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetParty(DispatchContext ctx, Map<String, ? extends
            Object> context) {
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

            listIterator = delegator.find("Person", null, null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling jqGetVehicle service: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        successResult.put("listIterator", listIterator);
        return successResult;
    }
}
