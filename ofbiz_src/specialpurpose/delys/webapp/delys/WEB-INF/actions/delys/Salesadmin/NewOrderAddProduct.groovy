import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.order.shoppingcart.shipping.*;


shipToParty = delegator.findOne("Party", [partyId : shoppingCart.getShipToCustomerPartyId()], true);
shippingContactMechList = ContactHelper.getContactMech(shipToParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);

shippingAddressContactMechId = "";
for (shippingContactMech in shippingContactMechList) {
	shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false);
	shippingAddressContactMechId = shippingAddress.contactMechId;
}
context.shippingAddressContactMechId = shippingAddressContactMechId;