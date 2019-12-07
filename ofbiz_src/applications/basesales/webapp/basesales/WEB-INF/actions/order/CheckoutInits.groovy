import org.ofbiz.order.shoppingcart.*;

ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

if (shoppingCart) {
	// checkout payment
	checkOutPaymentId = "";
	if (shoppingCart.getPaymentMethodIds()) {
		checkOutPaymentId = shoppingCart.getPaymentMethodIds().get(0);
	} else if (shoppingCart.getPaymentMethodTypeIds()) {
		checkOutPaymentId = shoppingCart.getPaymentMethodTypeIds().get(0);
	}
	context.checkOutPaymentId = checkOutPaymentId;
	
	// checkout option
	/*profiledefs = delegator.findOne("PartyProfileDefault", [partyId : userLogin.partyId, productStoreId : productStore.productStoreId], false);
	context.profiledefs = profiledefs;*/
	
	if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId()) {
		context.chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
	}
	/* else if (profiledefs?.defaultShipMeth) {
		context.chosenShippingMethod = profiledefs.defaultShipMeth;
	}*/
}
