package com.olbius.salesmtl.util;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class RouteUtils {
    public static final String module = SupUtil.class.getName();
    public static final String ROUTE_ENABLED = "ROUTE_ENABLED";
    public static final String ROUTE_DISABLED = "ROUTE_DISABLED";
    public static final String ENABLED = "ENABLED";
    public static final String DISABLED = "DISABLED";
    public static String RESOURCE_PROPERTIES = "basesalesmtl.properties";

    public static Map<String, Object> bindCustomerToRoute(Delegator delegator, String customerId, String routeId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (UtilValidate.isEmpty(customerId) || UtilValidate.isEmpty(routeId)) {
                ServiceUtil.returnError("Error: bindCustomerToRoute customerId or routeId null");
            }
            conds.add(EntityUtil.getFilterByDateExpr());
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            if (UtilValidate.isNotEmpty(routeCustomers)) {
                ServiceUtil.returnError("Error: bindCustomerToRoute RouteCustomer existed");
            }
            Timestamp fromDate = new Timestamp(System.currentTimeMillis());
            GenericValue rc = delegator.makeValue("RouteCustomer");
            rc.put("routeId", routeId);
            rc.put("customerId", customerId);
            rc.put("fromDate", fromDate);
            delegator.create(rc);
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error: bindCustomerToRoute");
        }
        return successReturn;
    }

    public static Map<String, Object> unbindCustomerToRoute(Delegator delegator, String customerId, String routeId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (UtilValidate.isEmpty(customerId) || UtilValidate.isEmpty(routeId)) {
                ServiceUtil.returnError("Error: unbindCustomerToRoute customerId or routeId null");
            }
            conds.add(EntityUtil.getFilterByDateExpr());
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            if (UtilValidate.isEmpty(routeCustomers)) {
                ServiceUtil.returnError("Error: unbindCustomerToRoute RouteCustomer not existed");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (GenericValue routeCustomer : routeCustomers) {
                routeCustomer.set("thruDate", now);
            }
            delegator.storeAll(routeCustomers);
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error: unbindCustomerToRoute");
        }
        return successReturn;
    }

    public static Map<String, Object> unbindCustomerToRoute(Delegator delegator, String customerId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (UtilValidate.isEmpty(customerId)) {
                ServiceUtil.returnError("Error: unbindCustomerToRoute customerId null");
            }
            conds.add(EntityUtil.getFilterByDateExpr());
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            if (UtilValidate.isEmpty(routeCustomers)) {
                ServiceUtil.returnError("Error: unbindCustomerToRoute RouteCustomer not existed");
            }
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (GenericValue routeCustomer : routeCustomers) {
                routeCustomer.set("thruDate", now);
            }
            delegator.storeAll(routeCustomers);
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error: unbindCustomerToRoute");
        }
        return successReturn;
    }

    public static Map<String, Object> removeRouteScheduleDetailDateByRoute(Delegator delegator, String routeId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(routeId)) {
                ServiceUtil.returnError("Error: removeRouteScheduleDetailDateByRoute routeId null");
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            delegator.removeByCondition("RouteScheduleDetailDate", EntityCondition.makeCondition(conds));
        } catch (Exception e) {
            ServiceUtil.returnError("Error: removeRouteScheduleDetailDateByRoute");
        }
        return successReturn;
    }

    public static Map<String, Object> removeRouteScheduleDetailDateByCustomer(Delegator delegator, String customerId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(customerId)) {
                ServiceUtil.returnError("Error: removeRouteScheduleDetailDateByCustomer customerId null");
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            delegator.removeByCondition("RouteScheduleDetailDate", EntityCondition.makeCondition(conds));
        } catch (Exception e) {
            ServiceUtil.returnError("Error: removeRouteScheduleDetailDateByCustomer");
        }
        return successReturn;
    }

    public static Map<String, Object> removeDeliveryClusterCustomerByCustomer(Delegator delegator, String customerId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (UtilValidate.isEmpty(customerId)) {
                ServiceUtil.returnError("Error: removeDeliveryClusterCustomerByCustomer customerId null");
            }
            conds.add(EntityUtil.getFilterByDateExpr());
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            List<GenericValue> deliveryClusterCustomers = delegator.findList("DeliveryClusterCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (GenericValue deliveryClusterCustomer : deliveryClusterCustomers) {
                deliveryClusterCustomer.set("thruDate", now);
            }
            if (UtilValidate.isNotEmpty(deliveryClusterCustomers)) {
                delegator.storeAll(deliveryClusterCustomers);
            }
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error: removeDeliveryClusterCustomerByCustomer");
        }
        return successReturn;
    }

    public static Map<String, Object> removeRouteScheduleDetailDateBySalesman(Delegator delegator, String salesmanId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(salesmanId)) {
                ServiceUtil.returnError("Error: removeRouteScheduleDetailDateBySalesman salesmanId null");
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("salesmanId", salesmanId));
            delegator.removeByCondition("RouteScheduleDetailDate", EntityCondition.makeCondition(conds));
        } catch (Exception e) {
            ServiceUtil.returnError("Error: removeRouteScheduleDetailDateBySalesman");
        }
        return successReturn;
    }

    public static Map<String, Object> removeRouteScheduleDetailDate(Delegator delegator, String customerId, String routeId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(customerId) || UtilValidate.isEmpty(routeId)) {
                ServiceUtil.returnError("Error: removeRouteScheduleDetailDate customerId or routeId null");
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            delegator.removeByCondition("RouteScheduleDetailDate", EntityCondition.makeCondition(conds));
        } catch (Exception e) {
            ServiceUtil.returnError("Error: removeRouteScheduleDetailDate");
        }
        return successReturn;
    }

    public static Map<String, Object> bindSalesmanToCustomer(Delegator delegator, String salesmanId, String customerId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(salesmanId) || UtilValidate.isEmpty(customerId)) {
                ServiceUtil.returnError("Error: bindSalesmanToCustomer salesmanId or customerId null");
            }
            GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", customerId),false);
            if (UtilValidate.isNotEmpty(partyCustomer)) {
                partyCustomer.set("salesmanId", salesmanId);
                delegator.store(partyCustomer);
            }
        } catch (Exception e) {
            ServiceUtil.returnError("Error: bindSalesmanToCustomer");
        }
        return successReturn;
    }

    public static Map<String, Object> unbindSalesmanToCustomer(Delegator delegator, String customerId) {
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(customerId)) {
                ServiceUtil.returnError("Error: unbindSalesmanToCustomer customerId null");
            }
            GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", customerId),false);
            if (UtilValidate.isNotEmpty(partyCustomer)) {
                partyCustomer.set("salesmanId", null);
                delegator.store(partyCustomer);
            }
        } catch (Exception e) {
            ServiceUtil.returnError("Error: unbindSalesmanToCustomer");
        }
        return successReturn;
    }

    public static Map<String, Object> createRouteStore(Delegator delegator, String customerId, String routeId) {
        Map<String, Object> res = ServiceUtil.returnSuccess();
        try {
            if (UtilValidate.isEmpty(customerId) || UtilValidate.isEmpty(routeId)) {
                ServiceUtil.returnError("Error: RouteUtils.createRouteStore customerId or routeId null");
            }
            Timestamp fromDate = new Timestamp(System.currentTimeMillis());
            GenericValue rc = delegator.makeValue("RouteCustomer");
            rc.put("routeId", routeId);
            rc.put("customerId", customerId);
            rc.put("fromDate", fromDate);
            delegator.create(rc);
            SupUtil.generateSaleRouteScheduleDetailDateForOneCustomerAdded(delegator, routeId, customerId);
        } catch (Exception e) {
            return ServiceUtil.returnError("Error: RouteUtils.createRouteStore");
        }
        return res;
    }

}