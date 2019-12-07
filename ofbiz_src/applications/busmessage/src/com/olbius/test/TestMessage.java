package com.olbius.test;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.service.OlbiusService;

public class TestMessage implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		OlbiusMessage data = new OlbiusMessage();
		
		data.setType("test");
		
		/*MessageContainer.EVENT_FACTORY.getSendEvent(dctx.getDelegator(), MessageContainer.ACTIVEMQ_FACTORY).send(data, new OlbiusHandleResponse() {
			@Override
			public void handle(OlbiusMessage message) {
				System.out.println("TEST MESSAGE: " + message.getMessageId());
			}
		});*/
		
		data.getDatas().add(new Notify());
		MessageContainer.EVENT_FACTORY.getSendEvent(dctx.getDelegator()).send(data);
//		OlbiusMessage message = MessageContainer.EVENT_FACTORY.getSendEvent(dctx.getDelegator()).get(data);
//		System.out.println("TEST MESSAGE: " + message.getMessageId());
		return ServiceUtil.returnSuccess();
	}
	
	
}
