package com.olbius.baselogistics.deliveryCluster;

import com.olbius.baselogistics.util.DeliveryClusterUtil;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryClusterServices {

    public static final String module = DeliveryClusterServices.class.getName();
    public static final String resource = "BaseLogisticsUiLabels";
    public static final String resourceCommonEntity = "CommonEntityLabels";
    public static final String OrderEntityLabels = "OrderEntityLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

    public static Map<String, Object> createNewDeliveryCluster(DispatchContext ctx, Map<String, Object> context){
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String[] customerIdArr = new String[]{};
        List<String> customerIds = FastList.newInstance();
        String deliveryClusterCode = (String) context.get("deliveryClusterCode");
        String deliveryClusterName = (String) context.get("deliveryClusterName");
        String shipperId = (String) context.get("shipperId");
        String customerIdStr = (String) context.get("customerIds");
        String description = (String) context.get("description");

        if (UtilValidate.isNotEmpty(customerIdStr)) {
            customerIdArr = customerIdStr.split(",");
        }
        for (String customerId : customerIdArr) {
            customerIds.add(customerId);
        }
        GenericValue gv = delegator.makeValue("DeliveryCluster");
        String deliveryClusterId = delegator.getNextSeqId("DeliveryCluster");
        gv.put("deliveryClusterId", deliveryClusterId);
        gv.put("deliveryClusterCode", deliveryClusterCode);
        gv.put("deliveryClusterName", deliveryClusterName);
        gv.put("description", description);
        gv.put("createdDate", UtilDateTime.nowTimestamp());
        gv.put("createdByUserLoginId", userLogin.get("userLoginId"));
        gv.put("managerId", userLogin.get("partyId"));
        gv.put("executorId", shipperId);
        gv.put("statusId", DeliveryClusterUtil.DELIVERY_CLUSTER_ENABLED);
        try {
            delegator.createOrStore(gv);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLCreateDeliveryClusterError", locale));
        }
        for(String customerId: customerIds) {
            GenericValue deliveryClusterCustomer = delegator.makeValue("DeliveryClusterCustomer");
            deliveryClusterCustomer.put("deliveryClusterId", deliveryClusterId);
            deliveryClusterCustomer.put("customerId", customerId);
            deliveryClusterCustomer.put("fromDate", UtilDateTime.nowTimestamp());
            try {
                delegator.createOrStore(deliveryClusterCustomer);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLCreateDeliveryClusterCustomerError", locale));
            }
        }
        successResult.put("deliveryClusterId", deliveryClusterId);
        return successResult;
    }

    public static Map<String, Object> deleteDeliveryCluster(DispatchContext dpct, Map<String, ? extends Object> context){
        Delegator delegator = dpct.getDelegator();
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        Map<String,Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (deliveryClusterId == null) {
                ServiceUtil.returnError("error deleteDeliveryCluster");
            }
            //Disable DeliveryCluster
            GenericValue aCluster = delegator.findOne("DeliveryCluster", UtilMisc.toMap("deliveryClusterId", deliveryClusterId),false);
            aCluster.set("statusId", DeliveryClusterUtil.DELIVERY_CLUSTER_DISABLED);
            delegator.store(aCluster);

            //Disable DeliveryClusterCustomer
            conds.clear();
            conds.add(EntityCondition.makeCondition("deliveryClusterId", deliveryClusterId));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> deliveryClusterCustomers = delegator.findList("DeliveryClusterCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            for (GenericValue gv : deliveryClusterCustomers) {
                gv.set("thruDate", UtilDateTime.nowTimestamp());
            }
            delegator.storeAll(deliveryClusterCustomers);
        } catch (Exception e) {
            ServiceUtil.returnError("error deleteDeliveryCluster");
        }
        return successReturn;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateDeliveryCluster(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) dpct.getDelegator();
        LocalDispatcher dispatcher = dpct.getDispatcher();
        Map<String,Object> successReturn = ServiceUtil.returnSuccess();
        String deliveryClusterId = (String) context.get("deliveryClusterId");
        String deliveryClusterCode = (String) context.get("deliveryClusterCode");
        String deliveryClusterName = (String) context.get("deliveryClusterName");
        String executorId = (String) context.get("executorId");
        String description = (String) context.get("description");
        Locale locale = (Locale) context.get("locale");
        try {
            GenericValue aCluster = delegator.findOne("DeliveryCluster", UtilMisc.toMap("deliveryClusterId", deliveryClusterId),false);
            if (UtilValidate.isEmpty(aCluster)) {
                return ServiceUtil.returnError("error updateDeliveryCluster aCluster not existed");
            }
            aCluster.set("deliveryClusterCode", deliveryClusterCode);
            aCluster.set("deliveryClusterName", deliveryClusterName);
            aCluster.set("executorId", executorId);
            aCluster.set("description", description);
            aCluster.store();
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("error updateDeliveryCluster");
        }
        return successReturn;
    }
}