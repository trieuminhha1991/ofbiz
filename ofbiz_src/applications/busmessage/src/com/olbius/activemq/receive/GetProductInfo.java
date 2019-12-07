package com.olbius.activemq.receive;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.api.ActivemqMessageResponse;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Product;
import com.olbius.jms.event.AbstractOlbiusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class GetProductInfo extends AbstractOlbiusEvent{

	@Override
	public ActivemqMessageResponse handle(OlbiusMessage data) {

		try {

			Delegator delegator = (Delegator) this.delegator;
			
			LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", delegator);
			
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			
			for(MessageData p : data.getDatas()) {
				dispatcher.runAsync("productPublisher", UtilMisc.toMap("productId", ((Product) p).getOwnId(), "userLogin", userLogin));
			}
			
		} catch (Exception e) {
			Debug.logError(e, GetProductInfo.class.getName());
		}
		return null;
	}

}
