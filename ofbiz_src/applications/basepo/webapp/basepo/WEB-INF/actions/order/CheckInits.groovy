import org.ofbiz.entity.util.EntityUtilProperties;

import java.sql.Timestamp;
import java.util.List;

import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.entity.util.EntityUtilProperties;

// Get the Cart
ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

// Set Shipping From the Party
partyId = null;
partyId = parameters.partyId;
if (partyId != null && (shoppingCart == null || !partyId.equals(shoppingCart.getOrderPartyId()))) {
	// remove cart if send parameters
	org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, null);
}

if (shoppingCart) {
	String estimateDeliveryDateStr = shoppingCart.getDefaultItemDeliveryDate();
	if (estimateDeliveryDateStr) {
		Timestamp defaultDesiredDeliveryDate = Timestamp.valueOf(estimateDeliveryDateStr);
		if (defaultDesiredDeliveryDate) context.defaultDesiredDeliveryDate = defaultDesiredDeliveryDate;
	}
	Timestamp defaultShipAfterDate = shoppingCart.getShipAfterDate();
	Timestamp defaultShipBeforeDate = shoppingCart.getShipBeforeDate();
	String defaultFacilityId = shoppingCart.getFacilityId();
	String defaultSupplierId = shoppingCart.getOrderPartyId();
	String defaultContactMechId = shoppingCart.getShippingContactMechId();
	if (defaultShipAfterDate) context.defaultShipAfterDate = defaultShipAfterDate;
	if (defaultShipBeforeDate) context.defaultShipBeforeDate = defaultShipBeforeDate;
	if (defaultFacilityId) context.defaultFacilityId = defaultFacilityId;
	if (defaultSupplierId) context.defaultSupplierId = defaultSupplierId;
	if (defaultContactMechId) context.defaultContactMechId = defaultContactMechId;
}