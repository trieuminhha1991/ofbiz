package com.olbius.activemq.receive;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderAdjustment;
import com.olbius.jms.data.OrderItem;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.Ship;
import com.olbius.jms.event.OfbizDataServices;
import com.olbius.jms.event.OlbiusEvent;

public class SalesOrderConfirm {

	private Delegator delegator;

	public static void confirm(Delegator delegator, String orderId) throws GenericEntityException {
		
		Order confirm = new SalesOrderConfirm(delegator).getOrder(orderId);

		OlbiusMessage messeData = new OlbiusMessage();

		messeData.setType(OlbiusEvent.ORDER_CONFIRM);

		messeData.getDatas().add(confirm);

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(messeData);
		
	}

	public SalesOrderConfirm(Delegator delegator) {
		this.delegator = delegator;
	}

	public Order getOrder(String orderId) throws GenericEntityException {

		if (orderId == null) {
			return null;
		}

		Order order = null;

		GenericValue value = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

		if (value != null) {
			order = new Order();
			order.setOwnId(orderId);

			order.getMessageData().put("orderDate", value.getTimestamp("orderDate") != null ? value.getTimestamp("orderDate").getTime() : null);
			order.getMessageData().put("entryDate", value.getTimestamp("entryDate") != null ? value.getTimestamp("entryDate").getTime() : null);
			order.getMessageData().put("statusId", "ORDER_CONFIRM");

			order.setCurrencyUom(OfbizDataServices.getUom(delegator, value.getString("currencyUom")));

			order.getMessageData().put("remainingSubTotal", value.getBigDecimal("remainingSubTotal"));
			order.getMessageData().put("grandTotal", value.getBigDecimal("grandTotal"));

			order.setShips(getShipGroup(orderId));

			order.setItems(getOrderItem(orderId));
		}

		return order;
	}

	public ArrayList<Ship> getShipGroup(String orderId) throws GenericEntityException {

		if (orderId == null) {
			return null;
		}

		ArrayList<Ship> ships = new ArrayList<Ship>();

		List<GenericValue> values = delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId), null, false);

		for (GenericValue value : values) {

			Ship ship = new Ship();

			ship.setGroupSeqId(value.getString("shipGroupSeqId"));

			ship.setEstimatedDeliveryDate(
					value.getTimestamp("estimatedDeliveryDate") != null ? value.getTimestamp("estimatedDeliveryDate").getTime() : null);
			ship.setEstimatedShipDate(value.getTimestamp("estimatedShipDate") != null ? value.getTimestamp("estimatedShipDate").getTime() : null);
			ship.setShipAfterDate(value.getTimestamp("shipAfterDate") != null ? value.getTimestamp("shipAfterDate").getTime() : null);
			ship.setShipByDate(value.getTimestamp("shipByDate") != null ? value.getTimestamp("shipByDate").getTime() : null);

			ship.setIsGift(value.getString("isGift"));
			ship.setMaySplit(value.getString("maySplit"));

			ship.setPostalAddress(OfbizDataServices.getPostalAddress(delegator, value.getString("contactMechId")));

			ships.add(ship);
		}

		return ships;

	}

	public ArrayList<OrderItem> getOrderItem(String orderId) throws GenericEntityException {

		if (orderId == null) {
			return null;
		}

		ArrayList<OrderItem> items = new ArrayList<OrderItem>();

		List<GenericValue> values = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null, false);

		for (GenericValue value : values) {

			OrderItem item = new OrderItem();

			item.getMessageData().put("orderItemSeqId", value.getString("orderItemSeqId"));

			Product product = new Product();
			product.setOwnId(value.getString("productId"));
			product.setQuantityUom(OfbizDataServices.getUom(delegator, value.getString("quantityUomId")));
			item.setProduct(product);

			item.getMessageData().put("statusId", value.getString("statusId"));
			item.getMessageData().put("isPromo", value.getString("isPromo"));
			item.getMessageData().put("quantity", value.getBigDecimal("quantity"));
			item.getMessageData().put("cancelQuantity", value.getBigDecimal("cancelQuantity"));
			item.getMessageData().put("unitPrice", value.getBigDecimal("unitPrice"));
			item.getMessageData().put("itemDescription", value.getString("itemDescription"));
			item.getMessageData().put("shipBeforeDate",
					value.getTimestamp("shipBeforeDate") != null ? value.getTimestamp("shipBeforeDate").getTime() : null);
			item.getMessageData().put("shipAfterDate",
					value.getTimestamp("shipAfterDate") != null ? value.getTimestamp("shipAfterDate").getTime() : null);

			items.add(item);

			List<GenericValue> adjustments = delegator.findByAnd("OrderAdjustment",
					UtilMisc.toMap("orderId", orderId, "orderItemSeqId", value.getString("orderItemSeqId")), null, false);

			for (GenericValue adjustment : adjustments) {

				OrderAdjustment orderAdjustment = new OrderAdjustment();

				orderAdjustment.getMessageData().put("shipGroupSeqId", adjustment.getString("adjustment"));
				orderAdjustment.getMessageData().put("orderAdjustmentTypeId", adjustment.getString("orderAdjustmentTypeId"));
				orderAdjustment.getMessageData().put("amount", adjustment.getBigDecimal("amount"));

				item.getAdjustments().add(orderAdjustment);
			}

		}

		return items;
	}

}
