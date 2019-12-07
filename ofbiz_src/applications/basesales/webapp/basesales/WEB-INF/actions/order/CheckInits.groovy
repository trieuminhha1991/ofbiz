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
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.product.ProductDisplayWorker;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.entity.util.EntityUtilProperties;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.basesales.product.ProductWorker;

/* OLD:
productStore = ProductStoreWorker.getProductStore(request);
if (productStore) {
	context.defaultProductStore = productStore;
	if (productStore.defaultSalesChannelEnumId)
		context.defaultSalesChannel = delegator.findOne("Enumeration", [enumId : productStore.defaultSalesChannelEnumId], true);
}
 */

// Get the Cart

// check copy from order
String orderId = parameters.orderId;
if (orderId) {
	
}

ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

salesChannels = delegator.findByAnd("Enumeration", [enumTypeId : "ORDER_SALES_CHANNEL"], ["sequenceId"], true);
context.salesChannels = salesChannels;

if (productStores == null) {
	//OLD: productStores = delegator.findList("ProductStore", null, null, ["productStoreId", "storeName"], null, true);
	List<GenericValue> productStores = com.olbius.basesales.product.ProductStoreWorker.getListProductStoreSell(delegator, userLogin);
	context.productStores = productStores;
}

/*
 suppliers = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "SUPPLIER"], ["groupName", "partyId"], false);
 context.suppliers = suppliers;
 
 organizations = delegator.findByAnd("PartyAcctgPrefAndGroup", null, null, false);
 context.organizations = organizations;
 */

// Set Shipping From the Party
partyId = null;
partyId = parameters.partyId;
if (partyId != null && (shoppingCart == null || !partyId.equals(shoppingCart.getOrderPartyId()))) {
	// remove cart if send parameters
	org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, null);
}
GenericValue party = null;
if (partyId) {
	party = delegator.findOne("Party", [partyId : partyId], false);
	contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false));
	if (contactMech) {
		/* Old
		if (shoppingCart == null) shoppingCart = ShoppingCartEvents.getCartObject(request);
		shoppingCart.setAllShippingContactMechId(contactMech.contactMechId);
		*/
		if (shoppingCart != null) shoppingCart.setAllShippingContactMechId(contactMech.contactMechId);
	}
}

// NEW CODE
if (shoppingCart != null) partyId = shoppingCart.getOrderPartyId();
context.defaultPartyId = partyId;
String defaultPartyFullName = null;
if (partyId) {
	GenericValue partyFullName = delegator.findOne("PartyFullNameDetail", [partyId: partyId], false);
	if (partyFullName) {
		defaultPartyFullName = partyFullName.getString("fullName");
		context.defaultPartyFullName = defaultPartyFullName;
	}
}
if (defaultPartyFullName == null) context.defaultPartyFullName = partyId;

productStore = ProductStoreWorker.getProductStore(request);
if (productStore) {
	context.defaultProductStore = productStore;
	if (productStore.defaultSalesChannelEnumId)
		context.defaultSalesChannel = delegator.findOne("Enumeration", [enumId : productStore.defaultSalesChannelEnumId], true);
} else {
	if (party != null && productStores != null) {
		List<String> productStoreIds = com.olbius.basesales.product.ProductStoreWorker.getProductStoreIdContainCustomer(delegator, party.partyId);
		if (UtilValidate.isNotEmpty(productStoreIds)) {
			List<String> productStoreIdsAll = EntityUtil.getFieldListFromEntityList(productStores, "productStoreId", true);
			for (String productStoreIdItem : productStoreIds) {
				if (productStoreIdsAll.contains(productStoreIdItem)) {
					productStore = delegator.findOne("ProductStore", ["productStoreId" : productStoreIdItem], true);
					break;
				}
			}
		} else {
			// la khach hang tiem nang, chuyen thanh khach hang ban le
			context.openChooseProductStore = true;
			/*
			String convertResultProductStoreId = ProductWorker.convertCustomType(delegator, party.partyId, "CONTACT", "INDIVIDUAL", userLogin, true);
			productStore = delegator.findOne("ProductStore", ["productStoreId" : convertResultProductStoreId], true);
			*/
		}
	} else {
		productStoreId = parameters.productStoreId;
		productStore = null;
		if (productStoreId != null) {
			productStore = delegator.findOne("ProductStore", ["productStoreId" : productStoreId], false);
		} else if (productStores != null && productStores.size() > 0) {
			productStore = EntityUtil.getFirst(productStores);
		}
		if (productStore) context.defaultProductStore = productStore;
	}
}
String defaultProductStoreId = null;
if (productStore) {
	defaultProductStoreId = productStore.getString("productStoreId");
	context.defaultProductStoreId = defaultProductStoreId;
}

/* Old catalog:
// Get current catalog
List<String> catalogIdsAvailable = null;
if (defaultProductStoreId) {
	//OLD: catalogIdsAvailable = CatalogWorker.getCatalogIdsAvailable(delegator, currentStoreId, shoppingCart.getOrderPartyId());
	catalogIdsAvailable = CatalogWorker.getCatalogIdsAvailable(delegator, defaultProductStoreId, null);
	if (catalogIdsAvailable) {
		context.defaultCatalogId = catalogIdsAvailable.get(0);
	}
}
 */

/* Old currency: 
context.currencies = delegator.findByAnd("Uom", ["uomTypeId": "CURRENCY_MEASURE"], null, true);

if (shoppingCart) {
	context.defaultCurrencyUomId = shoppingCart.getCurrency();
} else {
	context.defaultCurrencyUomId = SalesUtil.getCurrentCurrencyUom(delegator);
}
 */

if (shoppingCart) {
	String estimateDeliveryDateStr = shoppingCart.getDefaultItemDeliveryDate();
	if (estimateDeliveryDateStr) {
		Timestamp defaultDesiredDeliveryDate = Timestamp.valueOf(estimateDeliveryDateStr);
		if (defaultDesiredDeliveryDate) context.defaultDesiredDeliveryDate = defaultDesiredDeliveryDate;
	}
	Timestamp defaultShipAfterDate = shoppingCart.getShipAfterDate();
	Timestamp defaultShipBeforeDate = shoppingCart.getShipBeforeDate();
	if (defaultShipAfterDate) context.defaultShipAfterDate = defaultShipAfterDate;
	if (defaultShipBeforeDate) context.defaultShipBeforeDate = defaultShipBeforeDate;
}

String currentOrganizationPartyId = SalesUtil.getCurrentOrganization(delegator,userLogin.userLoginId);
context.currentOrganizationPartyId = currentOrganizationPartyId;

context.currentAgreementId = parameters.agreementId;
