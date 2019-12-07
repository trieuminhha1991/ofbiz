package com.olbius.activemq.receive;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;
import com.olbius.jms.data.Ship;
import com.olbius.jms.event.OlbiusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class SalesOrder extends AbstractOlbiusReceiveEvent {

	private void createCustomer(String partyId, String productStoreId) throws GenericEntityException {

		List<GenericValue> values = delegator.findByAnd("ProductStoreRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER", "productStoreId", productStoreId), null, false);

		if (values == null || values.isEmpty()) {

			GenericValue value = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"), false);

			if (value == null) {
				value = delegator.makeValue("PartyRole");

				value.set("partyId", partyId);
				value.set("roleTypeId", "CUSTOMER");

				value.create();
			}

			value = delegator.makeValue("ProductStoreRole");

			value.set("partyId", partyId);
			value.set("roleTypeId", "CUSTOMER");
			value.set("productStoreId", productStoreId);
			value.set("fromDate", new Timestamp(System.currentTimeMillis()));

			value.create();

		}

	}

	@Override
	public void receiveHandle(MessageData messageData) throws Exception {

		Order order = (Order) messageData;

		Ship ship = order.getShips().get(0);

		createCustomer(order.getGroupTo().getOwnId(), order.getProductStore());
		
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userLogin", userLogin);

		List<String> checkOutPayments = new ArrayList<String>();

		checkOutPayments.add("EXT_OFFLINE");

		map.put("checkOutPaymentId", checkOutPayments);

		map.put("shipAfterDate", ship.getShipAfterDate() != null ? Long.toString(ship.getShipAfterDate()) : null);
		map.put("shipBeforeDate", ship.getShipByDate() != null ? Long.toString(ship.getShipByDate()) : null);
		map.put("desiredDeliveryDate", ship.getEstimatedDeliveryDate() != null ? Long.toString(ship.getEstimatedDeliveryDate()) : null);

		map.put("shipping_contact_mech_id", ship.getPostalAddress().getOwnId());
		map.put("shipping_method", "NO_SHIPPING@_NA_");

		map.put("partyId", order.getGroupTo().getOwnId());
		map.put("productStoreId", order.getProductStore());

		List<Map<String, String>> maps = new ArrayList<>();
		
		for(OrderItem item : order.getItems()) {
			Map<String, String> tmp = new HashMap<>();
			tmp.put("productId", item.getProduct().getOwnId());
			tmp.put("quantityUomId", item.getProduct().getQuantityUom() != null ? item.getProduct().getQuantityUom().getOwnId(): null);
			tmp.put("quantityStr", (String) item.getProduct().getMessageData().get("quantity"));
			maps.add(tmp);
		}
		
		map.put("listProd", maps);
		
		Map<String, Object> result = dispatcher.runSync("initializeSalesOrderEntry", map);

		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, (ShoppingCart) result.get("shoppingCart"));
		
		result = checkOutHelper.createOrder(userLogin);
		
		order.setOwnId((String) result.get("orderId"));
		
		OlbiusMessage messeData = new OlbiusMessage();

		messeData.setType(OlbiusEvent.UPDATE_ID);

		Notify notify = new Notify();

		notify.setBusId(order.getBusId());

		notify.setOwnId(order.getOwnId());

		notify.setDataType(order.getDataType());

		notify.setUpdate(true);

		messeData.getDatas().add(notify);

		send(messeData);

//		SalesOrderConfirm.confirm(delegator, order.getOwnId());
		
	}

}
