package com.olbius.test;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.activemq.core.OlbiusMessageResponse;
import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;

public class TestMessageHandle2 extends AbstractOlbiusReceiveEvent {

	@Override
	protected ActivemqMessageResponse response() {
		return new OlbiusMessageResponse() {
			
			@Override
			public OlbiusMessage getMessage() {
				OlbiusMessage message = new OlbiusMessage();
				message.setMessageId("test message 2");
				return message;
			}
		};
	}

	@Override
	public void receiveHandle(MessageData messageData) throws Exception {
		System.out.println("HANDLE 2");
	}

}
