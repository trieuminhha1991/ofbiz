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
import org.ofbiz.order.shoppingcart.product.ProductDisplayWorker;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.entity.util.EntityUtilProperties;

productStore = ProductStoreWorker.getProductStore(request);
if (productStore) {
	context.defaultProductStore = productStore;
	if (productStore.defaultSalesChannelEnumId)
		context.defaultSalesChannel = delegator.findOne("Enumeration", [enumId : productStore.defaultSalesChannelEnumId], true);
}
// Get the Cart
shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

salesChannels = delegator.findByAnd("Enumeration", [enumTypeId : "ORDER_SALES_CHANNEL"], ["sequenceId"], true);
context.salesChannels = salesChannels;

//productStores = delegator.findList("ProductStore", null, null, ["productStoreId", "storeName"], null, true);
//context.productStores = productStores;

productStores = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreRoleDetail", ["partyId" : userLogin.partyId, roleTypeId : "MANAGER"], ["productStoreId", "storeName"], true), true);
context.productStores = productStores;

currencies = delegator.findByAnd("Uom", ["uomTypeId": "CURRENCY_MEASURE"], null, true);
context.currencies = currencies;
if (shoppingCart) {
	context.currencyUomId = shoppingCart.getCurrency();
} else {
	context.currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
}

suppliers = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "SUPPLIER"], ["groupName", "partyId"], false);
context.suppliers = suppliers;

organizations = delegator.findByAnd("PartyAcctgPrefAndGroup", null, null, false);
context.organizations = organizations;

// Set Shipping From the Party
partyId = null;
partyId = parameters.partyId;
if (partyId) {
	party = delegator.findOne("Person", [partyId : partyId], false);
	contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false));
	if (contactMech) {
		ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
		shoppingCart.setAllShippingContactMechId(contactMech.contactMechId);
	}
}
