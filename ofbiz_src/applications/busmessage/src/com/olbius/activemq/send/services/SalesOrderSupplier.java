package com.olbius.activemq.send.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.activemq.receive.handle.OlbiusReceiveData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;
import com.olbius.jms.data.PartyGroup;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.Ship;
import com.olbius.jms.event.OfbizDataServices;
import com.olbius.service.OlbiusService;

public class SalesOrderSupplier implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dctx.getDelegator();

		String partyToId = (String) context.get("partyToId");

		String orderId = (String) context.get("orderId");

		String currencyUomId = (String) context.get("currencyUom");

		List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));

		List<GenericValue> orderItemShipGroupInfo = UtilGenerics.checkList(context.get("orderItemShipGroupInfo"));

		PartyGroup partyTo = OfbizDataServices.getPartyGroup(delegator, partyToId);

		List<GenericValue> list = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose",
				UtilMisc.toMap("partyId", partyToId, "contactMechPurposeTypeId", "BILLING_LOCATION"), null, false));

		if (list != null && !list.isEmpty()) {
			partyTo.setPostalAddress(
					OfbizDataServices.getPostalAddress(delegator, list.get(0).getString("contactMechId")));
		}

		OlbiusMessage messeData = new OlbiusMessage();

		messeData.setType("sales");

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Map<String, Order> orders = new HashMap<>();

		ArrayList<Ship> ships = new ArrayList<>();
		
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

			ships.add(ship);
		}
		
		for (GenericValue value : orderItems) {

			List<EntityCondition> conditions = new ArrayList<>();

			conditions.add(EntityCondition.makeCondition("availableFromDate", EntityOperator.LESS_THAN_EQUAL_TO, timestamp));
			conditions
					.add(EntityCondition
							.makeCondition(
									UtilMisc.toList(
											EntityCondition.makeCondition("availableThruDate", EntityOperator.EQUALS, null),
											EntityCondition.makeCondition("availableThruDate",
													EntityOperator.GREATER_THAN_EQUAL_TO, timestamp)),
									EntityOperator.OR));
			conditions.add(
					EntityCondition.makeCondition("productId", EntityOperator.EQUALS, value.getString("productId")));

			GenericValue tmp = EntityUtil.getFirst(delegator.findList("SupplierProduct",
					EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false));

			if (tmp != null) {
				Order order;
				if (orders.get(tmp.getString("partyId")) == null && tmp.getString("partyId").indexOf(OlbiusReceiveData.BUS_CODE) != -1) {
					order = new Order();
					orders.put(tmp.getString("partyId"), order);
					order.setGroupTo(partyTo);
					order.setGroupFrom(OfbizDataServices.getPartyGroup(delegator, tmp.getString("partyId")));
					order.setShips(ships);
					order.setOwnId(orderId);
					order.setCurrencyUom(OfbizDataServices.getUom(delegator, currencyUomId));
				} else {
					order = orders.get(tmp.getString("partyId"));
				}

				if(order != null) {
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

			}

		}
		
		for(String s : orders.keySet()) {
			messeData.getDatas().add(orders.get(s));
		}

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(messeData);
		
		return ServiceUtil.returnSuccess();
	}

}
