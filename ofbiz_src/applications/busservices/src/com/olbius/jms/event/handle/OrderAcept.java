package com.olbius.jms.event.handle;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;
import com.olbius.jms.event.OlbiusEvent;

public class OrderAcept extends AbstractOlbiusBusEvent {

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {

		Order order = (Order) messageData;

		List<GenericValue> values = delegator.findByAnd("BusOrder",
				UtilMisc.toMap("orderId", order.getBusId(), "partyFromId", user, "orderType", "PURCHASE"), null, false);

		if (values != null && !values.isEmpty()) {

			String partyId = values.get(0).getString("partyToId");

			OlbiusMessage tmp = new OlbiusMessage();

			tmp.setType(OlbiusEvent.ORDER_ACEPT);

			tmp.setUser(partyId);

			tmp.getDatas().add(order);

			send(ActivemqSession.QUEUE, tmp, tmp.getUser());

		}
	}

}
