package com.olbius.activemq.receive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Order;
import com.olbius.jms.data.OrderItem;

public class UpdateOrderItems extends AbstractOlbiusReceiveEvent {

	@Override
	public void receiveHandle(MessageData messageData) throws Exception {
		
		Order order = (Order) messageData;
		
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		
		for(OrderItem item : order.getItems()) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("shipGroupSeqId", "00001");
			map.put("productId", item.getProduct().getOwnId());
			map.put("quantity", ((BigDecimal)item.getProduct().getMessageData().get("quantity")).setScale(0).toString());
			if(item.getProduct().getQuantityUom() != null) {
				map.put("quantityUomId", item.getProduct().getQuantityUom().getOwnId());
			} else {
				map.put("quantityUomId", "");
			}
			
			map.put("orderId", order.getOwnId());
			
			map.put("userLogin", userLogin);
			
			maps.add(map);
			
		}
		
		Map<String, Object> context = new HashMap<String, Object>();
		
		context.put("userLogin", userLogin);
		
		context.put("orderId", order.getOwnId());
		
		context.put("productList", maps);
		
		dispatcher.runSync("appendOrderItemsCustomAdvance", context);
		
		SalesOrderConfirm.confirm(delegator, order.getOwnId());
	}

}
