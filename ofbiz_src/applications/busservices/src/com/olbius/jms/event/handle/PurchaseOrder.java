package com.olbius.jms.event.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import com.olbius.activemq.api.ActivemqSession;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;
import com.olbius.jms.event.OlbiusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class PurchaseOrder extends AbstractOlbiusBusEvent {

	private List<GenericValue> values = new ArrayList<GenericValue>();
	private Map<String, ArrayList<MessageData>> map = new HashMap<String, ArrayList<MessageData>>();

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
		Order order = (Order) messageData;

		String partyId = null;

		List<GenericValue> genericValues = delegator.findByAnd("BusConvert", UtilMisc.toMap("busId", order.getGroupTo().getBusId(), "own", "Y"), null,
				false);

		if (genericValues != null && !genericValues.isEmpty()) {
			partyId = genericValues.get(0).getString("partyId");
		}

		if (partyId != null) {
			ArrayList<OrderItem> list = order.getItems();

			order.setItems(new ArrayList<OrderItem>());

			for (OrderItem item : list) {

				GenericValue value = delegator.findOne("BusProductStore",
						UtilMisc.toMap("partyId", partyId, "productId", item.getProduct().getBusId()), false);

				if (value != null) {
					if (order.getProductStore() == null) {
						order.setProductStore(value.getString("productStoreId"));
					} else {
						if (!order.getProductStore().equals(value.getString("productStoreId"))) {
							break;
						}
					}
				} else {
					break;
				}

				order.getItems().add(item);
				
			}

			if (map.get(partyId) == null) {
				map.put(partyId, new ArrayList<MessageData>());
			}

			if (order.getItems().size() == list.size()) {

				map.get(partyId).add(order);

			}

			GenericValue value = delegator.makeValue("BusOrder");

			value.set("orderId", order.getBusId());
			value.set("partyFromId", user);
			value.set("partyToId", partyId);
			value.set("orderType", "PURCHASE");

			values.add(value);
		}
	}

	@Override
	protected void end(String id, String user, String type) {
		try {
			delegator.storeAll(values);

			for (String partyId : map.keySet()) {

				OlbiusMessage tmp = new OlbiusMessage();

				tmp.setType(OlbiusEvent.PURCHASE_ORDER);

				tmp.setUser(partyId);

				tmp.setDatas(map.get(partyId));

				send(ActivemqSession.QUEUE, tmp, tmp.getUser(), true);

			}
		} catch (Exception e) {
			Debug.logError(e, PurchaseOrder.class.getName());
		}
	}

}
