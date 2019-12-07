package com.olbius.activemq.receive;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Order;

public class OrderAcept extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData) throws Exception {
		
		Order order = (Order) messageData;
		
		GenericValue value = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", order.getOwnId()), false);
		
		if(value != null && value.get("statusId") == null) {
			value.set("statusId", "ORDER_CREATED");
			value.store();
		}
		
	}

}
