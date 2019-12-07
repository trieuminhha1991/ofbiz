package com.olbius.activemq.send.services;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.event.OlbiusEvent;
import com.olbius.service.OlbiusService;

public class ConfirmOrder implements OlbiusService{

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {
		
		String orderId = (String) context.get("orderId");
		
		OlbiusMessage data = new OlbiusMessage();
		
		data.setType(OlbiusEvent.ORDER_ACEPT);
		
		Order order = new Order();
		order.setOwnId(orderId);
		data.getDatas().add(order);
		
		MessageContainer.EVENT_FACTORY.getSendEvent(dctx.getDelegator()).send(data);
		
		return ServiceUtil.returnSuccess();
	}

}
