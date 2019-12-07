import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;

orderHeader = null;
orderId = orderId;
if (orderId) {
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
}
orderType = orderHeader.orderTypeId;
orderReadHelper = new OrderReadHelper(orderHeader);

displayParty = null;
if ("PURCHASE_ORDER".equals(orderType)) {
	displayParty = orderReadHelper.getSupplierAgent();
} else {
	displayParty = orderReadHelper.getPlacingParty();
}
if (displayParty) {
	partyId = displayParty.partyId;
	context.partyId = partyId;
}
productStore = orderReadHelper.getProductStore();
context.productStore = productStore;
if ("SALES_ORDER".equals(orderType)) {
	context.returnHeaderTypeId = "CUSTOMER_RETURN";
	// also set the product store facility Id for sales orders
	if (productStore) {
		context.storeFacilityId = productStore.inventoryFacilityId;
		if (productStore.reqReturnInventoryReceive) {
			context.needsInventoryReceive = productStore.reqReturnInventoryReceive;
		} else {
			context.needsInventoryReceive = "Y";
		}
	}
} else {
	context.returnHeaderTypeId = "VENDOR_RETURN";
}