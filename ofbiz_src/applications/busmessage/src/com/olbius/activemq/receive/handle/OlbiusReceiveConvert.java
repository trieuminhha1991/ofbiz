package com.olbius.activemq.receive.handle;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.ofbiz.entity.Delegator;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusMessage;

public class OlbiusReceiveConvert {

	private OlbiusReceiveData receiveData;

	public OlbiusReceiveConvert(Delegator delegator) {
		this.receiveData = OlbiusReceiveDataFactory.getInstance(delegator);
	}

	public OlbiusMessage receive(OlbiusMessage data) {
		for (MessageData mesData : data.getDatas()) {
			receive(mesData);
		}
		return data;
	}

	public void receive(Object id) {

		if (id == null) {
			return;
		}

		receiveMethod(id);

	}

	@SuppressWarnings("rawtypes")
	private void receiveMethod(Object id) {

		if(id == null) {
			return;
		}
		
		for (Method method : id.getClass().getMethods()) {
			if (MessageId.class.isAssignableFrom(method.getReturnType())) {

				try {
					MessageId tmp = (MessageId) method.invoke(id);
					receive(tmp);
				} catch (Exception e) {
					MessageContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusReceiveConvert.class.getName());
				}

			} else if (ArrayList.class.isAssignableFrom(method.getReturnType())) {

				try {
					ArrayList tmp = (ArrayList) method.invoke(id);
					for (Object object : tmp) {
						receive(object);
					}

				} catch (Exception e) {
					MessageContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusReceiveConvert.class.getName());
				}

			} else if (MessageData.class.isAssignableFrom(method.getReturnType())) {
				try {
					MessageData tmp = (MessageData) method.invoke(id);
					receiveMethod(tmp);
				} catch (Exception e) {
					MessageContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusReceiveConvert.class.getName());
				}
			}
		}

		if (id instanceof MessageId) {
			try {
				receiveData.insert((MessageId)id);
			} catch (Exception e) {
				MessageContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusReceiveConvert.class.getName());
			}
		}

	}
}
