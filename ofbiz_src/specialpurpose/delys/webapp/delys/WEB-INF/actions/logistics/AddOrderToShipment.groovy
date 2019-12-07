import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

shipmentId = request.getParameter("shipmentId");
orderId = request.getParameter("orderId");
if (shipmentId && orderId){
	List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	if (!listOrderItems.isEmpty()){
		for (GenericValue item : listOrderItems){
			GenericValue OrderShipment = delegator.makeValue("OrderShipment");
			OrderShipment.put("orderId", orderId);
			OrderShipment.put("orderItemSeqId", item.get("orderItemSeqId"));
			if(!listOrderItemShipGroup.isEmpty()){
				for (GenericValue group : listOrderItemShipGroup){
					OrderShipment.put("shipGroupSeqId", group.get("shipGroupSeqId"));
					OrderShipment.put("shipmentItemSeqId", item.get("orderItemSeqId"));
    				OrderShipment.put("shipmentId", shipmentId);
    				OrderShipment.put("quantity", item.getBigDecimal("quantity"));
				}
				delegator.createOrStore(OrderShipment);
			} else {
				Map<String, Object> resultTmp = new FastMap<String, Object>();
				Map<String, Object> OrderShipGroup = new FastMap<String, Object>();
				OrderShipGroup.put("orderId", orderId);
				OrderShipGroup.put("userLogin", userLogin);
				try {
					resultTmp = dispatcher.runSync("createOrderItemShipGroup", OrderShipGroup);
					OrderShipment.put("shipGroupSeqId", resultTmp.get("shipGroupSeqId"));
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
				OrderShipment.put("shipmentItemSeqId", item.get("orderItemSeqId"));
				OrderShipment.put("shipmentId", shipmentId);
				OrderShipment.put("quantity", item.getBigDecimal("quantity"));
				delegator.createOrStore(OrderShipment);
			}
			
		}
	}
}
