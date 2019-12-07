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
import org.ofbiz.order.shoppingcart.product.ProductDisplayWorker;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.entity.util.EntityUtilProperties;

// Get the Cart
shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

/*salesChannels = delegator.findByAnd("Enumeration", [enumTypeId : "ORDER_SALES_CHANNEL"], ["sequenceId"], true);
context.salesChannels = salesChannels;*/

//productStores = delegator.findList("ProductStore", null, null, ["productStoreId", "storeName"], null, true);
//context.productStores = productStores;

productStores = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreRoleDetail", ["partyId" : userLogin.partyId, roleTypeId : "CUSTOMER"], ["productStoreId", "storeName"], true), true);
currentStoreId = "NA";
partyId = "";
if (shoppingCart != null) {
	currentStoreId = shoppingCart.getProductStoreId();
	partyId = shoppingCart.getOrderPartyId();
} else {
	if (productStores != null && productStores.size() > 0) {
		currentStoreId = EntityUtil.getFirst(productStores).getString("productStoreId");
	}
}
context.currentPartyId = partyId;
productStore = ProductStoreWorker.getProductStore(request);
if (productStore == null) {
	productStoreId = parameters.productStoreId;
	if (productStoreId != null) {
		productStore = delegator.findOne("ProductStore", ["productStoreId" : productStoreId], false);
	} else {
		productStore = EntityUtil.getFirst(productStores);
	}
}
if (productStore) {
	context.defaultProductStore = productStore;
	currentStoreId = productStore.getString("productStoreId");
	if (productStore.defaultSalesChannelEnumId)
		context.defaultSalesChannel = delegator.findOne("Enumeration", [enumId : productStore.defaultSalesChannelEnumId], true);
}
context.currentStoreId = currentStoreId;
context.productStores = productStores;

// Get current catalog
List<String> catalogIdsAvailable = null;
if (currentStoreId != null) {
	//catalogIdsAvailable = CatalogWorker.getCatalogIdsAvailable(delegator, currentStoreId, shoppingCart.getOrderPartyId());
	catalogIdsAvailable = CatalogWorker.getCatalogIdsAvailable(delegator, currentStoreId, null);
}

String currentCatalogId = "";
if (catalogIdsAvailable) {
	currentCatalogId = catalogIdsAvailable.get(0);
	context.currentCatalogId = currentCatalogId;
}

/*
// Get current products
if (currentCatalogId) {
	conditionList = [];
	orConditionList = [];
	mainConditionList = [];
	// do not include configurable products
	conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED"));
	conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED_SERVICE"));
	conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGR_DIGSERV"));
	conditionList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, currentCatalogId));
	conditions = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	// no virtual products: note that isVirtual could be null,
	// we consider those products to be non-virtual and hence addable to the order in bulk
	orConditionList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
	orConditionList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, null));
	orConditions = EntityCondition.makeCondition(orConditionList, EntityOperator.OR);
	mainConditionList.add(orConditions);
	mainConditionList.add(conditions);
	mainConditions = EntityCondition.makeCondition(mainConditionList, EntityOperator.AND);
	context.productList = delegator.findList("ProdCatalogCategoryAndProduct", mainConditions, ["productId", "brandName", "internalName"] as Set, ["productId"], null, false);
}
 */

currencies = delegator.findByAnd("Uom", ["uomTypeId": "CURRENCY_MEASURE"], null, true);
context.currencies = currencies;
if (shoppingCart) {
	context.currencyUomId = shoppingCart.getCurrency();
} else {
	context.currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
}

//suppliers = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "SUPPLIER"], ["groupName", "partyId"], false);
//context.suppliers = suppliers;

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
