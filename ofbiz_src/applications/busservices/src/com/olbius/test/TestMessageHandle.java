package com.olbius.test;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.activemq.container.BusContainer;
import com.olbius.activemq.core.OlbiusHandleResponse;
import com.olbius.activemq.core.OlbiusMessageResponse;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;

public class TestMessageHandle extends AbstractOlbiusBusEvent {

	private String text = "test message 1";
	
	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {

		System.out.println("HANDLE 1");
		
		OlbiusMessage data = new OlbiusMessage();
		
		data.setType("test");
		
		data.getDatas().add(new Notify());
		
//		OlbiusMessage message = BusContainer.EVENT_FACTORY.getSendEvent(delegator).get(data);
		
//		text += " :: " + message.getMessageId();
		
		BusContainer.EVENT_FACTORY.getSendEvent(delegator).send(data, new OlbiusHandleResponse() {
			
			@Override
			public void handle(OlbiusMessage message) {
				System.out.println("HANLDE 3");
			}
		});
	}
	
	@Override
	protected ActivemqMessageResponse response() {
		return new OlbiusMessageResponse() {
			
			@Override
			public OlbiusMessage getMessage() {
				OlbiusMessage message = new OlbiusMessage();
				message.setMessageId(text);
				return message;
			}
		};
	}
	
}
