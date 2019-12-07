package com.olbius.activemq.receive;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceValidationException;

import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderAdjustment;
import com.olbius.jms.data.OrderItem;

public class OrderConfirm extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData)
			throws GenericEntityException, ServiceAuthException, ServiceValidationException, GenericServiceException {

		Order order = (Order) messageData;

		String orderId = order.getOwnId();

		for (OrderItem item : order.getItems()) {

			List<GenericValue> values = new ArrayList<>();

			String orderItemId;

			GenericValue value = EntityUtil.getFirst(delegator.findByAnd("OrderItem",
					UtilMisc.toMap("orderId", orderId, "productId", item.getProduct().getOwnId(), "statusId",
							item.getMessageData().get("statusId"), "quantityUomId",
							item.getProduct().getQuantityUom().getOwnId(), "isPromo",
							item.getMessageData().get("isPromo")),
					null, false));
			if (value == null) {
				value = delegator.makeValue("OrderItem");
				value.put("orderId", orderId);
				value.put("orderItemSeqId", UUID.randomUUID().toString().replaceAll("-", ""));
				value.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
				value.put("productId", item.getProduct().getOwnId());
				value.put("statusId", item.getMessageData().get("statusId"));
				value.put("isPromo", item.getMessageData().get("isPromo"));
			}
			value.put("quantity", item.getMessageData().get("quantity"));
			value.put("cancelQuantity", item.getMessageData().get("cancelQuantity"));
			value.put("unitPrice", item.getMessageData().get("unitPrice"));
			if (item.getMessageData().get("shipBeforeDate") != null) {
				value.put("shipBeforeDate", new Timestamp((long) item.getMessageData().get("shipBeforeDate")));
			}
			if (item.getMessageData().get("shipAfterDate") != null) {
				value.put("shipAfterDate", new Timestamp((long) item.getMessageData().get("shipAfterDate")));
			}

			orderItemId = value.getString("orderItemSeqId");

			values.add(value);

			for (OrderAdjustment adjustment : item.getAdjustments()) {

				value = EntityUtil.getFirst(delegator.findByAnd(
						"OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemId,
								"orderAdjustmentTypeId", adjustment.getMessageData().get("orderAdjustmentTypeId")),
						null, false));
				if (value == null) {
					value = delegator.makeValue("OrderAdjustment");
					value.put("orderAdjustmentId", UUID.randomUUID().toString().replaceAll("-", ""));
					value.put("orderId", orderId);
					value.put("orderItemSeqId", orderItemId);
					value.put("orderAdjustmentTypeId", adjustment.getMessageData().get("orderAdjustmentTypeId"));
				}

				value.put("amount", adjustment.getMessageData().get("amount"));

				values.add(value);

			}

			delegator.storeAll(values);
		}

		/*
		 * GenericValue value = delegator.findOne("OrderHeader",
		 * UtilMisc.toMap("orderId", orderId), false);
		 * 
		 * value.put("remainingSubTotal",
		 * order.getMessageData().get("remainingSubTotal"));
		 * value.put("grandTotal", order.getMessageData().get("grandTotal"));
		 * 
		 * value.store();
		 */

		dispatcher.runAsync("resetGrandTotal", UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));

	}

}
