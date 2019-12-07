import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

import java.text.DecimalFormat;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

if (!"ITEM_COMPLETED".equals(statusId)){
	quantityAccepted = BigDecimal.ZERO;
	quantityRejected = BigDecimal.ZERO;
	quantityWillBeReceived = BigDecimal.ZERO;
	quantityOrdered = BigDecimal.ZERO;
	quantityCanReceive = BigDecimal.ZERO;
	if (item.getBigDecimal("cancelQuantity") != null){
		if (item.getBigDecimal("quantity") != null){
			quantityOrdered = item.getBigDecimal("quantity").subtract(item.getBigDecimal("cancelQuantity"));
		} else {
			quantityOrdered = BigDecimal.ZERO;
		}
	} else {
		if (item.getBigDecimal("quantity") != null){
			quantityOrdered = item.getBigDecimal("quantity");
		} else {
			quantityOrdered = BigDecimal.ZERO;
		}
	}
	if (!listShipmentReceipts.isEmpty()){
		for (GenericValue shipmentItem : listShipmentReceipts){
			quantityAccepted = quantityAccepted.add(shipmentItem.getBigDecimal("quantityAccepted"));
			quantityRejected = quantityRejected.add(shipmentItem.getBigDecimal("quantityRejected"));
		}
		if (quantityAccepted == null){
			quantityAccepted = BigDecimal.ZERO;
		}
		if (quantityRejected == null){
			quantityRejected = BigDecimal.ZERO;
		}
	}
	quantityCanReceive = quantityOrdered.subtract(quantityAccepted.add(quantityRejected));
	quantityWillBeReceived = quantityOrdered.subtract(quantityAccepted.add(quantityRejected));
} else {
	quantityOrdered = BigDecimal.ZERO;
	quantityCanReceive = BigDecimal.ZERO;
	quantityAccepted = BigDecimal.ZERO;
	quantityRejected = BigDecimal.ZERO;
	if (!listShipmentReceipts.isEmpty()){
		for (GenericValue shipmentItem : listShipmentReceipts){
			quantityAccepted = quantityAccepted.add(shipmentItem.getBigDecimal("quantityAccepted"));
			quantityRejected = quantityRejected.add(shipmentItem.getBigDecimal("quantityRejected"));
		}
		if (quantityAccepted == null){
			quantityAccepted = BigDecimal.ZERO;
		}
		if (quantityRejected == null){
			quantityRejected = BigDecimal.ZERO;
		}
	} else {
		if (item.getBigDecimal("cancelQuantity") != null){
			if (item.getBigDecimal("quantity") != null){
				quantityAccepted = item.getBigDecimal("quantity").subtract(item.getBigDecimal("cancelQuantity"));
			} else {
				quantityAccepted = BigDecimal.ZERO;
			}
		} else {
			if (item.getBigDecimal("quantity") != null){
				quantityAccepted = item.getBigDecimal("quantity");
			} else {
				quantityAccepted = BigDecimal.ZERO;
			}
		}
		quantityRejected = BigDecimal.ZERO;
	}
	if (item.getBigDecimal("cancelQuantity") != null){
		if (item.getBigDecimal("quantity") != null){
			quantityOrdered = item.getBigDecimal("quantity").subtract(item.getBigDecimal("cancelQuantity"));
		} else {
			quantityOrdered = BigDecimal.ZERO;
		}
	} else {
		if (item.getBigDecimal("quantity") != null){
			quantityOrdered = item.getBigDecimal("quantity");
		} else {
			quantityOrdered = BigDecimal.ZERO;
		}
	}
	quantityWillBeReceived = BigDecimal.ZERO;
}
DecimalFormat df = new DecimalFormat("#0");
context.quantityWillBeReceived = df.format(quantityWillBeReceived);
context.quantityAccepted = df.format(quantityAccepted);
context.quantityRejected = df.format(quantityRejected);
context.quantityOrdered = df.format(quantityOrdered);
context.quantityCanReceive = df.format(quantityCanReceive);