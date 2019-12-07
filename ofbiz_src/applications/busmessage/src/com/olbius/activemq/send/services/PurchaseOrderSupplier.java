package com.olbius.activemq.send.services;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;
import com.olbius.jms.data.PartyGroup;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.Ship;
import com.olbius.jms.event.OfbizDataServices;
import com.olbius.jms.event.OlbiusEvent;
import com.olbius.service.OlbiusService;

public class PurchaseOrderSupplier implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dctx.getDelegator();

		String partyFromId = (String) context.get("partyFromId");

		String partyToId = (String) context.get("partyToId");

		String orderId = (String) context.get("orderId");

		String currencyUomId = (String) context.get("currencyUom");

		List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));

		List<GenericValue> orderItemShipGroupInfo = UtilGenerics.checkList(context.get("orderItemShipGroupInfo"));

		OlbiusMessage messeData = new OlbiusMessage();

		messeData.setType(OlbiusEvent.PURCHASE_ORDER);

		Order order = new Order();

		order.setOwnId(orderId);

		order.setCurrencyUom(OfbizDataServices.getUom(delegator, currencyUomId));

		PartyGroup partyFrom = OfbizDataServices.getPartyGroup(delegator, partyFromId);

		order.setGroupFrom(partyFrom);

		order.setGroupTo(OfbizDataServices.getPartyGroup(delegator, partyToId));

		for (GenericValue value : orderItems) {

			Product product = new Product();

			product.setOwnId(value.getString("productId"));
			product.getMessageData().put("itemDescription", value.getString("itemDescription"));
			product.getMessageData().put("unitPrice", value.getBigDecimal("unitPrice"));
			product.getMessageData().put("quantity", value.getString("quantity"));

			product.setQuantityUom(OfbizDataServices.getUom(delegator, value.getString("quantityUomId")));

			OrderItem item = new OrderItem();

			item.setProduct(product);

			order.getItems().add(item);

		}

		for (GenericValue value : orderItemShipGroupInfo) {

			if (!"OrderItemShipGroup".equals(value.getEntityName())) {
				continue;
			}

			Ship ship = new Ship();

			ship.setShipByDate(value.getTimestamp("shipByDate") != null ? value.getTimestamp("shipByDate").getTime() : null);
			ship.setEstimatedDeliveryDate(
					value.getTimestamp("estimatedDeliveryDate") != null ? value.getTimestamp("estimatedDeliveryDate").getTime() : null);
			ship.setEstimatedShipDate(value.getTimestamp("estimatedShipDate") != null ? value.getTimestamp("estimatedShipDate").getTime() : null);
			ship.setShipAfterDate(value.getTimestamp("shipAfterDate") != null ? value.getTimestamp("shipAfterDate").getTime() : null);
			ship.setMaySplit(value.getString("maySplit"));
			ship.setIsGift(value.getString("isGift"));

			ship.setPostalAddress(OfbizDataServices.getPostalAddress(delegator, value.getString("contactMechId")));

			order.getShips().add(ship);
		}

		messeData.getDatas().add(order);

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(messeData);

		return ServiceUtil.returnSuccess();
	}

}
