package com.olbius.jms.event.handle;

import org.ofbiz.base.util.Debug;
import com.olbius.activemq.container.BusContainer;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class ProductPublisher extends AbstractOlbiusBusEvent {

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
	}
	
	@Override
	protected void end(String id, String user, String type) {
		try {

			OlbiusMessage tmp = new OlbiusMessage();

			tmp.setType(PRODUCT_PUBLISH);

			BusContainer.EVENT_FACTORY.getSendEvent(delegator).sendTopic(tmp);

		} catch (Exception e) {
			Debug.logError(e, ProductPublisher.class.getName());
		}
	}

}
