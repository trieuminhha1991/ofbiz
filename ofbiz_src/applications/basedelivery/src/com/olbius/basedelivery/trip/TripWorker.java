package com.olbius.basedelivery.trip;

import com.olbius.basesales.order.OrderWorker;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by user on 12/1/17.
 */
public class TripWorker {
    public static boolean createTripDetail(Delegator delegator, String tripId, List<String> listDeliveryId) {
        try {
            String[] deliveryIds = listDeliveryId.get(0).split(",");
            for (String deliveryId : deliveryIds) {
                String originDeliveryId = deliveryId.replace("\"", "");
                String tripDetailId = delegator.getNextSeqId("TripDetail");
                GenericValue tripDetail = delegator.makeValue("TripDetail", UtilMisc.toMap("tripDetailId", tripDetailId, "tripId", tripId, "deliveryId", originDeliveryId));
                delegator.create(tripDetail);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static BigDecimal getWeightInDelivery(Delegator delegator, LocalDispatcher dispatcher, String deliveryId) {
        EntityCondition condition = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
        BigDecimal totalWeight = BigDecimal.ZERO;
        try {
            List<GenericValue> listDeliveryItem = delegator.findList("DeliveryItem", condition, null, null, null, false);
            for(GenericValue deliveryItem : listDeliveryItem) {
                String productId = getProductIdByOrderIdAndOrderItemSeqId(delegator, (String) deliveryItem.get("fromOrderId"), (String) deliveryItem.get("fromOrderItemSeqId"));
                if(productId == null) continue;
                BigDecimal quantity = (BigDecimal) deliveryItem.get("actualExportedQuantity");
                totalWeight = totalWeight.add(OrderWorker.getTotalWeightProduct(delegator, dispatcher, productId, quantity));
            }
        }
        catch (GenericEntityException e) {

        }

        return totalWeight;
    }

    public static String getProductIdByOrderIdAndOrderItemSeqId(Delegator delegator, String orderId, String orderItemSeqId) {
        try {
            GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
            if(orderItem == null) {
                System.out.println("orderId = " + orderId + ", orderItemSeqId = " + orderItemSeqId);
                return null;
            }
            return (String) orderItem.get("productId");
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
