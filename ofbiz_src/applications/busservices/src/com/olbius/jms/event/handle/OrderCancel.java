package com.olbius.jms.event.handle;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;
import com.olbius.jms.event.OlbiusEvent;

public class OrderCancel extends AbstractOlbiusBusEvent {

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
		Order order = (Order) messageData;

		List<EntityCondition> conditions = new ArrayList<EntityCondition>();

		conditions.add(EntityCondition.makeCondition("partyFromId", user));

		conditions.add(EntityCondition.makeCondition("partyToId", user));

		EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.OR);

		conditions = new ArrayList<EntityCondition>();

		conditions.add(condition);

		conditions.add(EntityCondition.makeCondition("orderType", "PURCHASE"));

		List<GenericValue> values = delegator.findList("BusOrder",
				EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);

		if (values != null && !values.isEmpty()) {

			String partyId = null;

			if (values.get(0).getString("partyToId").equals(user)) {
				partyId = values.get(0).getString("partyFromId");
			} else {
				partyId = values.get(0).getString("partyToId");
			}

			OlbiusMessage tmp = new OlbiusMessage();

			tmp.setType(OlbiusEvent.ORDER_CANCELLED);

			tmp.setUser(partyId);

			tmp.getDatas().add(order);

			send(ActivemqSession.QUEUE, tmp, tmp.getUser());

		}
	}

}
