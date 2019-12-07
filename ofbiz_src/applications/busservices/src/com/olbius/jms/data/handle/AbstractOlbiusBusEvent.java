package com.olbius.jms.data.handle;

import javax.xml.bind.JAXBException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.activemq.api.HandleResponse;
import com.olbius.activemq.container.BusContainer;
import com.olbius.jms.data.JaxbConvert;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.AbstractOlbiusEvent;

public abstract class AbstractOlbiusBusEvent extends AbstractOlbiusEvent {

	protected LocalDispatcher dispatcher;
	protected GenericValue userLogin;
	protected Delegator delegator;

	@Override
	public ActivemqMessageResponse handle(OlbiusMessage data) {

		this.delegator = (Delegator) super.delegator;
		
		data = new OlbiusBusConvert(this.delegator).bus(data, true);

		dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", this.delegator);

		try {
			userLogin = this.delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			userLogin.set("lastLocale", "en");
		} catch (GenericEntityException e) {
			Debug.logError(e, this.getClass().getName());
		}

		start(data.getMessageId(), data.getUser(), data.getType());
		for(MessageData messageData : data.getDatas()) {
			try {
				busHandle(messageData, data.getUser());
			} catch (Exception e) {
				Debug.logError(e, this.getClass().getName());
			}
		}
		end(data.getMessageId(), data.getUser(), data.getType());
		
		return response();
	}

	public abstract void busHandle(MessageData messageData, String user) throws Exception;

	protected void start(String id, String user, String type) {
	}

	protected void end(String id, String user, String type) {
	}
	
	protected ActivemqMessageResponse response() {
		return null;
	}

	public void send(String type, OlbiusMessage message, String user, boolean update) throws JAXBException {
		send(type, message, user, update, null);
	}
	
	public void send(String type, OlbiusMessage message, String user, boolean update, HandleResponse handleResponse) throws JAXBException {
		BusContainer.ACTIVEMQ_FACTORY.createProducer(this.delegator).sendMessage(BusContainer.ACTIVEMQ_FACTORY.getSend(), type,
				JaxbConvert.toString(new OlbiusBusConvert(this.delegator).setUpdate(update).bus(message, false)), user, handleResponse);
	}

	public void send(String type, OlbiusMessage message, String user) throws JAXBException {
		send(type, message, user, false);
	}

}
