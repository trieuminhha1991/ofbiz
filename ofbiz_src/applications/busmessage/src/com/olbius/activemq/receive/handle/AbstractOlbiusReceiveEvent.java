package com.olbius.activemq.receive.handle;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.AbstractOlbiusEvent;

public abstract class AbstractOlbiusReceiveEvent extends AbstractOlbiusEvent {

	protected LocalDispatcher dispatcher;
	protected GenericValue userLogin;
	protected Delegator delegator;
	
	@Override
	public ActivemqMessageResponse handle(OlbiusMessage data) {
		
		this.delegator = (Delegator) super.delegator;
		
		data = new OlbiusReceiveConvert(this.delegator).receive(data);
		
		dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", this.delegator);

		try {
			userLogin = this.delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			userLogin.set("lastLocale", "en");
		} catch (GenericEntityException e) {
			Debug.logError(e, this.getClass().getName());
		}
		
		start();
		for(MessageData messageData : data.getDatas()) {
			try {
				receiveHandle(messageData);
			} catch (Exception e) {
				Debug.logError(e, this.getClass().getName());
			}
		}
		end();
		
		return response();
	}

	public abstract void receiveHandle(MessageData messageData) throws Exception;
	
	protected void start() {
	}
	
	protected void end() {
	}
	
	protected ActivemqMessageResponse response() {
		return null;
	}
	
	public void send(OlbiusMessage message) {
		MessageContainer.EVENT_FACTORY.getSendEvent(this.delegator).send(message);
	}
	
}
