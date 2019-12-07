package com.olbius.activemq.receive.handle;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.AbstractOlbiusEvent;

public class UpdateInfo extends AbstractOlbiusEvent {

	@Override
	public ActivemqMessageResponse handle(OlbiusMessage data) {

		try {
			
			Delegator delegator = (Delegator) this.delegator;
			
			LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", delegator);

			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);

			for (MessageData d : data.getDatas()) {

				Notify notify = (Notify) d;

				if (MessageId.PRODUCT.equals(notify.getDataType())) {
					dispatcher.runAsync("productPublisher", UtilMisc.toMap("productId", notify.getOwnId(), "userLogin", userLogin));
				}

			}
		} catch (Exception e) {
			Debug.logError(e, UpdateInfo.class.getName());
		}

		return null;
	}

}
