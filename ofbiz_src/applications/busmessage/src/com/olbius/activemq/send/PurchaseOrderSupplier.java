package com.olbius.activemq.send;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
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

/**
 * @author Nguyen Ha
 *
 */
public class PurchaseOrderSupplier {

	public static Map<String, Object> orderItemCancelSupplier(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		String orderId = (String) context.get("orderId");

		String orderItemSeqId = (String) context.get("orderItemSeqId");

		OlbiusMessage message = new OlbiusMessage();

		message.setType(OlbiusEvent.ORDER_ITEM_CANCELLED);

		Order order = new Order();

		order.setOwnId(orderId);

		OrderItem item = new OrderItem();

		GenericValue value = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);

		Product product = new Product();
		
		product.setOwnId(value.getString("productId"));
		
		item.setProduct(product);

		order.getItems().add(item);

		message.getDatas().add(order);

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(message);

		return ServiceUtil.returnSuccess();

	}

	public static Map<String, Object> orderCancelSupplier(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		String orderId = (String) context.get("orderId");

		OlbiusMessage message = new OlbiusMessage();

		message.setType(OlbiusEvent.ORDER_CANCELLED);

		Order order = new Order();

		order.setOwnId(orderId);

		message.getDatas().add(order);

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(message);

		return ServiceUtil.returnSuccess();

	}

	public static Map<String, Object> updatePurchaseOrderSupplier(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

		String orderId = (String) context.get("orderId");

		GenericValue value = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

		if (!"PURCHASE_ORDER".equals(value.getString("orderTypeId"))) {
			return ServiceUtil.returnSuccess();
		}

		List<EntityCondition> conditions = new ArrayList<EntityCondition>();

		conditions.add(EntityCondition.makeCondition("orderId", orderId));

		conditions.add(EntityCondition.makeCondition("isPromo", "N"));
		
		conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));

		List<GenericValue> values = delegator.findList("OrderItem", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null,
				false);

		OlbiusMessage message = new OlbiusMessage();

		message.setType(OlbiusEvent.UPDATE_ORDER_ITEMS);

		Order order = new Order();

		order.setOwnId(orderId);

		for (GenericValue v : values) {

			String productId = v.getString("productId");

			BigDecimal quantity = v.getBigDecimal("quantity");

			Product product = new Product();

			product.setOwnId(productId);

			product.getMessageData().put("quantity", quantity);

			product.setQuantityUom(OfbizDataServices.getUom(delegator, v.getString("quantityUomId")));

			OrderItem item = new OrderItem();

			item.setProduct(product);

			order.getItems().add(item);

		}

		message.getDatas().add(order);

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(message);

		return ServiceUtil.returnSuccess();

	}

	public static Map<String, Object> purchaseOrderSupplier(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = ctx.getDelegator();

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

		List<GenericValue> list = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose",
				UtilMisc.toMap("partyId", "company", "contactMechPurposeTypeId", "BILLING_LOCATION"), null, false));

		if (list != null && !list.isEmpty()) {
			partyFrom.setPostalAddress(OfbizDataServices.getPostalAddress(delegator, list.get(0).getString("contactMechId")));
		}

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
