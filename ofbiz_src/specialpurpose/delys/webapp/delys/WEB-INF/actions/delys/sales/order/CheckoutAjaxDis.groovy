
import org.ofbiz.base.util.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.accounting.payment.BillingAccountWorker;
import org.ofbiz.party.contact.ContactHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

cart = session.getAttribute("shoppingCart");
if (cart) {
	currencyUomId = cart.getCurrency();
	userLogin = session.getAttribute("userLogin");
	partyId = cart.getPartyId();
	if (parameters.isGetPartyId != null && parameters.partyId != null && parameters.isGetPartyId == "true") {
		partyId = parameters.partyId;
	}
	party = delegator.findOne("Party", [partyId : partyId], true);
	productStoreId = ProductStoreWorker.getProductStoreId(request);
	if (productStoreId == null) {
		productStoreId = parameters.productStoreId;
	}
	context.partyIdSelected = party.partyId;
	//DONENOACCTRANS: deleted
	/*checkOutPaymentId = "";
	if (cart) {
		if (cart.getPaymentMethodIds()) {
			checkOutPaymentId = cart.getPaymentMethodIds().get(0);
		} else if (cart.getPaymentMethodTypeIds()) {
			checkOutPaymentId = cart.getPaymentMethodTypeIds().get(0);
		}
	}*/
	
	context.shoppingCart = cart;
	context.userLogin = userLogin;
	context.productStoreId = productStoreId;
	
	//DONENOACCTRANS: deleted
	/*finAccounts = delegator.findByAnd("FinAccountAndRole", [partyId : partyId, roleTypeId : "OWNER"], null, false);
	finAccounts = EntityUtil.filterByDate(finAccounts, UtilDateTime.nowTimestamp(), "roleFromDate", "roleThruDate", true);
	finAccounts = EntityUtil.filterByDate(finAccounts);
	context.finAccounts = finAccounts;*/
	//context.checkOutPaymentId = checkOutPaymentId;
	//context.paymentMethodList = EntityUtil.filterByDate(party.getRelated("PaymentMethod", null, ["paymentMethodTypeId"], false), true);
	/*billingAccountList = BillingAccountWorker.makePartyBillingAccountList(userLogin, currencyUomId, partyId, delegator, dispatcher);
	if (billingAccountList) {
		context.selectedBillingAccountId = cart.getBillingAccountId();
		context.billingAccountList = billingAccountList;
	}*/
	productStore = ProductStoreWorker.getProductStore(request);
	/*checkIdealPayment = false;
	if (productStore) {
		productStorePaymentSettingList = productStore.getRelated("ProductStorePaymentSetting", null, null, true);
		productStorePaymentSettingIter = productStorePaymentSettingList.iterator();
		while (productStorePaymentSettingIter.hasNext()) {
			productStorePaymentSetting = productStorePaymentSettingIter.next();
			if (productStorePaymentSetting.get("paymentMethodTypeId") == "EXT_IDEAL") {
				checkIdealPayment = true;
			}
		}
		
		if (checkIdealPayment) {
			issuerList = org.ofbiz.accounting.thirdparty.ideal.IdealEvents.getIssuerList();
			if (issuerList) {
				context.issuerList = issuerList;
			}
		}
	}*/
	
	// File 2: CheckoutOptions.groovy
	
	//DONENOACCTRANS: deleted
	//shippingEstWpr = null;
	/*shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
	context.shippingEstWpr = shippingEstWpr;
	context.carrierShipmentMethodList = shippingEstWpr.getShippingMethods();*/
	// Reassign items requiring drop-shipping to new or existing drop-ship groups
	cart.createDropShipGroups(dispatcher);
	
	profiledefs = delegator.findOne("PartyProfileDefault", [partyId : userLogin.partyId, productStoreId : productStoreId], false);
	context.profiledefs = profiledefs;
	
	context.shoppingCart = cart;
	context.userLogin = userLogin;
	if (productStore == null) {
		context.productStoreId = productStoreId;
	} else {
		context.productStoreId = productStore.get("productStoreId");
	}
	context.productStore = productStore;
	shipToParty = delegator.findOne("Party", [partyId : cart.getShipToCustomerPartyId()], true);
	if (parameters.isGetPartyId != null && parameters.partyId != null && parameters.isGetPartyId == "true") {
		shipToParty = delegator.findOne("Party", [partyId : partyId], true);
	}
	context.shippingContactMechList = ContactHelper.getContactMech(shipToParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
	//DONENOACCTRANS: deleted
	//context.emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
	//DONENOACCTRANS: deleted
	/*if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) {
		context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
	} else if (profiledefs?.defaultShipMeth) {
		context.chosenShippingMethod = profiledefs.defaultShipMeth;
	}*/
	
	// other profile defaults
	if (!cart.getShippingAddress() && profiledefs?.defaultShipAddr) {
		cart.setShippingContactMechId(profiledefs.defaultShipAddr);
	}
	if (cart.selectedPayments() == 0 && profiledefs?.defaultPayMeth) {
		cart.addPayment(profiledefs.defaultPayMeth);
	}
	
	// create a list containing all the parties associated to the current cart, useful to change
	// the ship to party id
	cartParties = [cart.getShipToCustomerPartyId()];
	if (parameters.isGetPartyId != null && parameters.partyId != null && parameters.isGetPartyId == "true") {
		cartParties = [];
	}
	if (!cartParties.contains(partyId)) {
		cartParties.add(partyId);
	}
	if (!cartParties.contains(cart.getOrderPartyId())) {
		cartParties.add(cart.getOrderPartyId());
	}
	if (!cartParties.contains(cart.getPlacingCustomerPartyId())) {
		cartParties.add(cart.getPlacingCustomerPartyId());
	}
	if (!cartParties.contains(cart.getBillToCustomerPartyId())) {
		cartParties.add(cart.getBillToCustomerPartyId());
	}
	if (!cartParties.contains(cart.getEndUserCustomerPartyId())) {
		cartParties.add(cart.getEndUserCustomerPartyId());
	}
	if (!cartParties.contains(cart.getSupplierAgentPartyId())) {
		cartParties.add(cart.getSupplierAgentPartyId());
	}
	salesReps = cart.getAdditionalPartyRoleMap().SALES_REP;
	if (salesReps) {
		salesReps.each { salesRep ->
			if (!cartParties.contains(salesRep)) {
				cartParties.add(salesRep);
			}
		}
	}
	context.cartParties = cartParties;
	
	// File 3: StorePaymentOptions.groovy
	//DONENOACCTRANS: deleted
	/*productStorePaymentMethodTypeIdMap = new HashMap();
	productStorePaymentSettingList = productStore.getRelated("ProductStorePaymentSetting", null, null, true);
	productStorePaymentSettingIter = productStorePaymentSettingList.iterator();
	while (productStorePaymentSettingIter.hasNext()) {
		productStorePaymentSetting = productStorePaymentSettingIter.next();
		productStorePaymentMethodTypeIdMap.put(productStorePaymentSetting.get("paymentMethodTypeId"), true);
	}
	context.put("productStorePaymentMethodTypeIdMap", productStorePaymentMethodTypeIdMap);*/
} else {
	partyId = parameters.partyId;
	if (partyId) {
		party = delegator.findOne("Party", [partyId : partyId], true);
		if (party) {
			// Create a list containing all the parties associated to the current cart, useful to change
			// the ship to party id
			String shipToCustomerPartyId = partyId;
			cartParties = [shipToCustomerPartyId];
			context.cartParties = cartParties;
			
			shipToParty = delegator.findOne("Party", [partyId : shipToCustomerPartyId], true);
			context.shippingContactMechList = ContactHelper.getContactMech(shipToParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
			
			productStoreId = parameters.productStoreId;
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpSession session = httpRequest.getSession(false);
			if (session != null) {
				session.setAttribute("productStoreId", productStoreId);
			}
			productStore = ProductStoreWorker.getProductStore(request);
			context.productStore = productStore;
			context.partyIdSelected = party.partyId;
		}
	}
}

