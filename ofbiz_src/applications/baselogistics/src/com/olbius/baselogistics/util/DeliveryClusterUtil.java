package com.olbius.baselogistics.util;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class DeliveryClusterUtil {
    public static final String module = DeliveryClusterUtil.class.getName();
    public static final String DELIVERY_CLUSTER_ENABLED = "DELIVERY_CLUSTER_ENABLED";
    public static final String DELIVERY_CLUSTER_DISABLED = "DELIVERY_CLUSTER_DISABLED";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        if(parameters.get("type")!=null){
            String type=(String)parameters.get("type")[0];
            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesMethodChannelEnumId",null),EntityOperator.OR,EntityCondition.makeCondition("salesMethodChannelEnumId",type)));
        }
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);
            listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", null));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ConsigneeCustomerFullAndDeliveryCluster",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListCustomerCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDeliveryCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);
            listAllConditions.add(EntityCondition.makeCondition("statusId", DeliveryClusterUtil.DELIVERY_CLUSTER_ENABLED));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "DeliveryClusterAndPartyDetail",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListDeliveryCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListShipperCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("partyCode");
            fieldsToSelect.add("fullName");
            listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "LOG_DELIVERER"));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "PartyAndRoleFullNameSimple",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListShipperCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerByCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);

            String deliveryClusterId = "";
            if (parameters.get("deliveryClusterId") != null && parameters.get("deliveryClusterId").length > 0) {
                deliveryClusterId = (String) parameters.get("deliveryClusterId")[0];
            }
            ;
            if (UtilValidate.isEmpty(deliveryClusterId)) {
                ServiceUtil.returnError("error jqGetListCustomerByCluster: deliveryClusterId not existed");
            }
            listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", deliveryClusterId));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ConsigneeCustomerFullAndDeliveryCluster",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListCustomerByCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDeliveryClusterSimple(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);
            String shipperId = "";
            if (parameters.get("shipperId") != null && parameters.get("shipperId").length > 0) {
                shipperId = (String) parameters.get("shipperId")[0];
            }
            ;
            if (UtilValidate.isNotEmpty(shipperId)) {
                listAllConditions.add(EntityCondition.makeCondition("executorId", shipperId));
            }
            listAllConditions.add(EntityCondition.makeCondition("statusId", DeliveryClusterUtil.DELIVERY_CLUSTER_ENABLED));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "DeliveryCluster",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListCustomerCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListCustomerPerDeliveryCluster(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = dpct.getDelegator();
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> customers = FastList.newInstance();
        List<String> deliveryClusterIds = FastList.newInstance();
        List<EntityCondition> conds = FastList.newInstance();
        String clusterIdsStr = null;
        try {
            if (context.containsKey("deliveryClusterIds") && context.get("deliveryClusterIds") != null) {
                clusterIdsStr = (String) context.get("deliveryClusterIds");
            } else {
                return ServiceUtil.returnError("Error getListCustomerPerDeliveryCluster");
            }
            clusterIdsStr = clusterIdsStr.substring(1, clusterIdsStr.length() - 1);
            String[] routeIdsArr = clusterIdsStr.split(",");
            for (int i = 0; i < routeIdsArr.length; i++) {
                String routeId = routeIdsArr[i].substring(1, routeIdsArr[i].length() - 1);
                deliveryClusterIds.add(routeId);
            }
            conds.add(EntityCondition.makeCondition("deliveryClusterId", EntityOperator.IN, deliveryClusterIds));
            conds.add(EntityUtil.getFilterByDateExpr());
            customers = delegator.findList("DeliveryClusterCustomerView", EntityCondition.makeCondition(conds), null, null, null, false);
            returnSuccess.put("customers", customers);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error getListCustomerPerDeliveryCluster");
        }
        return returnSuccess;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerShipperByCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);

            String deliveryClusterId = "";
            if (parameters.get("deliveryClusterId") != null && parameters.get("deliveryClusterId").length > 0) {
                deliveryClusterId = (String) parameters.get("deliveryClusterId")[0];
            }
            ;
            if (UtilValidate.isEmpty(deliveryClusterId)) {
                ServiceUtil.returnError("error jqGetListCustomerShipperByCluster: deliveryClusterId not existed");
            }
            listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", deliveryClusterId));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ConsigneeCustomerFullAndDeliveryCluster",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListCustomerShipperByCluster service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetCustAvailableClusterPurpose(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityListIterator listIterator = null;
        try {
            opts.setDistinct(true);
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("partyCode");
            fieldsToSelect.add("partyName");
            fieldsToSelect.add("deliveryClusterId");
            fieldsToSelect.add("executorId");
            fieldsToSelect.add("postalAddressName");

            String deliveryClusterId = "";
            if (parameters.get("deliveryClusterId") != null && parameters.get("deliveryClusterId").length > 0) {
                deliveryClusterId = (String) parameters.get("deliveryClusterId")[0];
            }
            ;

            String shipperId = "";
            if (parameters.get("shipperId") != null && parameters.get("shipperId").length > 0) {
                shipperId = (String) parameters.get("shipperId")[0];
            }

            if(parameters.get("type")!=null){
                String type=(String)parameters.get("type")[0];
                listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesMethodChannelEnumId",null),EntityOperator.OR,EntityCondition.makeCondition("salesMethodChannelEnumId",type)));
            }

            if (UtilValidate.isEmpty(deliveryClusterId) || UtilValidate.isEmpty(shipperId)) {
                ServiceUtil.returnError("error jqGetCustAvailableClusterPurpose: deliveryClusterId, shipperId not existed");
            }
            listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", null));
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ConsigneeCustomerFullAndDeliveryCluster",
                    EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toSet(fieldsToSelect), listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetCustAvailableClusterPurpose service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        result.put("listIterator", listIterator);
        return result;
    }

    public static Map<String, Object> createDeliveryClusterCustomers(DispatchContext dpct, Map<String, Object> context) {
        Map<String, Object> res = ServiceUtil.returnSuccess();
        Delegator delegator = dpct.getDelegator();
        String customers = (String) context.get("parties");
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        try {
            customers = customers.substring(1, customers.length() - 1);
            String[] customer = customers.split(",");
            for (int i = 0; i < customer.length; i++) {
                String customerId = customer[i].substring(1, customer[i].length() - 1);
                Timestamp fromDate = new Timestamp(System.currentTimeMillis());
                GenericValue rc = delegator.makeValue("DeliveryClusterCustomer");
                rc.put("deliveryClusterId", deliveryClusterId);
                rc.put("customerId", customerId);
                rc.put("fromDate", fromDate);
                delegator.create(rc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ServiceUtil.returnError(ex.getMessage());
        }
        return res;
    }

    public static Map<String, Object> removeDeliveryClusterCustomers(DispatchContext dpct, Map<String, Object> context) {
        Map<String, Object> res = ServiceUtil.returnSuccess();
        Delegator delegator = dpct.getDelegator();
        String customers = (String) context.get("parties");
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        try {
            customers = customers.substring(1, customers.length() - 1);
            String[] customer = customers.split(",");
            List<EntityCondition> conds = FastList.newInstance();

            for (int i = 0; i < customer.length; i++) {
                String customerId = customer[i].substring(1, customer[i].length() - 1);
                conds.clear();
                conds.add(EntityCondition.makeCondition("deliveryClusterId", EntityOperator.EQUALS, deliveryClusterId));
                conds.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, customerId));
                conds.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
                List<GenericValue> lst = delegator.findList("DeliveryClusterCustomer",
                        EntityCondition.makeCondition(conds), null, null, null,
                        false);
                for (GenericValue rc : lst) {
                    Timestamp thruDate = new Timestamp(System.currentTimeMillis());
                    rc.put("thruDate", thruDate);
                    delegator.store(rc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ServiceUtil.returnError(ex.getMessage());
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getCustomersInDeliveryCluster(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = dpct.getDelegator();
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        List<GenericValue> customers = FastList.newInstance();
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        List<EntityCondition> conds = FastList.newInstance();
        try {
            conds.add(EntityCondition.makeCondition("deliveryClusterId", deliveryClusterId));
            conds.add(EntityUtil.getFilterByDateExpr());
            customers = delegator.findList("CustomerFullAndDeliveryCluster", EntityCondition.makeCondition(conds), null, null, null, false);
            returnSuccess.put("customers", customers);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error getCustomersInDeliveryCluster");
        }
        return returnSuccess;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getCustomersAvailableInDeliveryCluster(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = dpct.getDelegator();
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        List<String> listSortFields = FastList.newInstance();
        Map<String,String[]> parameters = FastMap.newInstance();
        EntityFindOptions opts = new EntityFindOptions();
        opts.setDistinct(true);
        parameters.put("pagenum", new String[]{"0"});
        parameters.put("pagesize", new String[]{"100"});

        List<GenericValue> customers = FastList.newInstance();
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        String latLngBound = null;
        Double minLat = null, maxLat = null, minLng = null, maxLng = null;
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (context.containsKey("latLngBound")) {
                latLngBound = (String) context.get("latLngBound");
            }
            String[] latLngArr = null;
            /*Struct  self.minLat + "_" +  self.maxLat + "_" +self.minLng + "_" +self.maxLng;*/
            if (UtilValidate.isNotEmpty(latLngBound)) {
                latLngArr = latLngBound.split("_");
            }
            if (UtilValidate.isNotEmpty(latLngArr) && latLngArr.length > 3) {
                minLat = Double.parseDouble(latLngArr[0]);
                maxLat = Double.parseDouble(latLngArr[1]);
                minLng = Double.parseDouble(latLngArr[2]);
                maxLng = Double.parseDouble(latLngArr[3]);
                conds.add(EntityCondition.makeCondition("latitude", EntityOperator.LESS_THAN_EQUAL_TO, maxLat));
                conds.add(EntityCondition.makeCondition("latitude", EntityOperator.GREATER_THAN_EQUAL_TO, minLat));
                conds.add(EntityCondition.makeCondition("longitude", EntityOperator.LESS_THAN_EQUAL_TO, maxLng));
                conds.add(EntityCondition.makeCondition("longitude", EntityOperator.GREATER_THAN_EQUAL_TO, minLng));
            }
            conds.add(EntityCondition.makeCondition("deliveryClusterId", null));
            Map<String, Object> resultService = ServiceUtil.returnSuccess();
            customers = EntityMiscUtil.processIteratorToList(parameters, resultService, delegator, "CustomerFullAndDeliveryCluster", EntityCondition.makeCondition(conds), null, UtilMisc.toSet(listSortFields), listSortFields, opts);
            returnSuccess.put("customers", customers);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error getCustomersAvailableInDeliveryCluster");
        }
        return returnSuccess;
    }
}
