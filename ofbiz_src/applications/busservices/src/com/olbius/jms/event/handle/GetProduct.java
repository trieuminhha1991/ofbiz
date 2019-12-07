package com.olbius.jms.event.handle;

import java.sql.Timestamp;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;
import com.olbius.jms.event.OfbizDataServices;

/**
 * @author Nguyen Ha
 *
 */
public class GetProduct extends AbstractOlbiusBusEvent {

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
	}

	@Override
	protected void end(String id, String user, String type) {
		try {
			Timestamp timestamp = BusEvent.getTimeStamp(delegator, user, type);

			List<GenericValue> products = delegator.findList("Product",
					EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, timestamp), null, null, null, false);

			OlbiusMessage tmp = new OlbiusMessage();

			tmp.setType(GET_PRODUCT);

			tmp.setUser(user);

			for (GenericValue product : products) {

				GenericValue value = delegator.findOne("BusConvert", UtilMisc.toMap("partyId", user, "busId", product.getString("productId")), false);

				if (value == null || "Y".equals(value.getString("own"))) {
					continue;
				}

				tmp.getDatas().add(OfbizDataServices.getProductBus(delegator, product.getString("productId")));

			}

			send(ActivemqSession.QUEUE, tmp, user, true);

			BusEvent.udpateTimeStamp(delegator, user, type);
		} catch (Exception e) {
			Debug.logError(e, GetProduct.class.getName());
		}
	}
}
