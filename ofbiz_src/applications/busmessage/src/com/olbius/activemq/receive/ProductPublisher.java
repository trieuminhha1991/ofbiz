package com.olbius.activemq.receive;

import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;

/**
 * @author Nguyen Ha
 *
 */
public class ProductPublisher extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData) throws Exception {
	}

	@Override
	protected void end() {
		OlbiusMessage messeData = new OlbiusMessage();

		messeData.setType(GET_PRODUCT);

		send(messeData);
	}

}
