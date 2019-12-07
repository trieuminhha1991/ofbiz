package com.olbius.activemq.receive;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;

import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Order;

public class OrderCancel extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData) throws GenericServiceException, GenericEntityException {

		Order order = (Order) messageData;

		GenericValue value = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", order.getOwnId()), false);

		if ("ORDER_CANCELLED".equals(value.getString("statusId"))) {
			return;
		}

		Map<String, Object> context = new HashMap<String, Object>();

		context.put("userLogin", userLogin);

		context.put("orderId", order.getOwnId());

		context.put("statusId", "ORDER_CANCELLED");

		context.put("setItemStatus", "Y");

		dispatcher.runSync("changeOrderStatus", context);

	}

}
