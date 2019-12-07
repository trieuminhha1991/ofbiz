package com.olbius.activemq.receive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;

public class OrderItemCancel extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData) throws GenericEntityException, GenericServiceException {
		
		Order order = (Order) messageData;

		Map<String, Object> context = new HashMap<String, Object>();

		context.put("userLogin", userLogin);

		context.put("orderId", order.getOwnId());

		context.put("shipGroupSeqId", "00001");

		for (OrderItem item : order.getItems()) {

			List<GenericValue> values = delegator.findByAnd("OrderItem",
					UtilMisc.toMap("orderId", order.getOwnId(), "productId", item.getProduct().getOwnId()), null, false);

			for (GenericValue value : values) {
				if (!"ITEM_CANCELLED".equals(value.getString("statusId"))) {
					context.put("orderItemSeqId", value.get("orderItemSeqId"));
					dispatcher.runSync("cancelOrderItem", context);
				}

			}

		}

		SalesOrderConfirm.confirm(delegator, order.getOwnId());

	}

}
