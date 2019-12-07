package com.olbius.jms.event.handle;

import java.util.UUID;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.activemq.core.OlbiusMessageResponse;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.AbstractOlbiusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class RegisterTenant extends AbstractOlbiusEvent {

	@Override
	public ActivemqMessageResponse handle(OlbiusMessage data) {

		try {

			Delegator delegator = (Delegator) this.delegator;

			GenericValue value = delegator.findOne("Party", UtilMisc.toMap("partyId", data.getUser()), false);

			if (value == null) {

				final String uuid = UUID.randomUUID().toString().replaceAll("-", "");

				value = delegator.makeValue("Party");
				value.set("partyId", uuid);
				delegator.create(value);

				return new OlbiusMessageResponse() {
					@Override
					public OlbiusMessage getMessage() {
						OlbiusMessage message = new OlbiusMessage();
						message.getDatas().add(new Notify());
						message.getDatas().get(0).getMessageData().put("busId", uuid);
						return message;
					}
				};

			}

		} catch (Exception e) {
			Debug.logError(e, RegisterTenant.class.getName());
		}
		return null;
	}

}
