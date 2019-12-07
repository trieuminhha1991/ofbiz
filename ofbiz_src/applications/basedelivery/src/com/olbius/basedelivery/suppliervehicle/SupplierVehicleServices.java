package com.olbius.basedelivery.suppliervehicle;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by user on 11/23/17.
 */
public class SupplierVehicleServices {

    public static final String module = SupplierVehicleServices.class.getName();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createSupplierVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String partyId = (String) context.get("partyId");
            String vehicleId = (String) context.get("vehicleId");
            Timestamp fromDate = UtilDateTime.nowTimestamp();
            GenericValue supplierVehicle = delegator.makeValue("SupplierVehicle",
                    UtilMisc.toMap(
                            "supplierVehicleId", delegator.getNextSeqId("SupplierVehicle"),
                            "partyId", partyId,
                            "vehicleId", vehicleId,
                            "fromDate", fromDate,
                            "statusId", "SUP_V_WAITING"));
            delegator.create(supplierVehicle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> updateSupplierVehicle(DispatchContext dctx, Map<String, ? extends Object> context) {
//        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
//        GenericValue userLogin = (GenericValue) context.get("userLogin");
//        Locale locale = (Locale) context.get("locale");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        JSONArray jsonArray = JSONArray.fromObject(request.getParameter("listSupplierVehicle"));

        List<GenericValue> listSupplierVehicle = new LinkedList<GenericValue>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String supplierVehicleId = jsonObject.getString("supplierVehicleId");
            String partyId = jsonObject.getString("partyId");
            String vehicleId = jsonObject.getString("vehicleId");

            GenericValue supplierVehicle = delegator.makeValue("SupplierVehicle", UtilMisc.toMap(
                    "supplierVehicleId", supplierVehicleId,
                    "partyId", partyId,
                    "vehicleId", vehicleId));
            listSupplierVehicle.add(supplierVehicle);
        }
        try {
            delegator.storeAll(listSupplierVehicle);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Errors");
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        return result;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSupplierVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        EntityListIterator listIterator = null;
        Map<String, String> mapCondition = new HashMap<String, String>();
        EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        listAllConditions.add(tmpConditon);
        listAllConditions.add(EntityUtil.getFilterByDateExpr());
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
            listIterator = delegator.find("SupplierVehicle", cond, null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling jqGetListSupplierVehicle service: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        successResult.put("listIterator", listIterator);
        return successResult;
    }

}
