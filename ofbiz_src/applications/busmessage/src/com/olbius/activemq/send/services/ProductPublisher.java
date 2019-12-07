package com.olbius.activemq.send.services;

import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.OfbizDataServices;
import com.olbius.jms.event.OlbiusEvent;
import com.olbius.service.OlbiusService;

public class ProductPublisher implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {
		
		Delegator delegator = dctx.getDelegator();

		String productId = (String) context.get("productId");

		OlbiusMessage data = new OlbiusMessage();
		
		data.setType(OlbiusEvent.PRODUCT_PUBLISH);
		
		data.getDatas().add(OfbizDataServices.getProduct(delegator, productId));

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(data);
		
		return ServiceUtil.returnSuccess();
		
	}

}
