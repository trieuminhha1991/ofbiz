package com.olbius.accounting.appr;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class DeliveryWorker {
	public static Long getAvailableToDelivery(Delegator delegator, String orderId, String orderItemSeqId) throws GenericEntityException{
		GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
		Long total = orderItem.getBigDecimal("quantity").longValue();
		EntityCondition orderIdCon = EntityCondition.makeCondition("fromOrderId", orderId);
		EntityCondition orderItemSeqIdCon = EntityCondition.makeCondition("fromOrderItemSeqId", orderItemSeqId);
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem",EntityCondition.makeCondition(EntityJoinOperator.AND, orderIdCon, orderItemSeqIdCon), null, null, null, false);
		Long delivered = 0l;
		for (GenericValue item : deliveryItems){
			Long quanity = item.getBigDecimal("quantity").longValue();
			delivered += quanity; 
		}
		return total - delivered;
	}
}
