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

productStores = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreRoleDetail", ["partyId" : userLogin.partyId, roleTypeId : "MANAGER"], ["productStoreId", "storeName"], true), true);
currentStoreId = "NA";
if (productStores != null && productStores.size() > 0) {
	productStore = EntityUtil.getFirst(productStores);
	currentStoreId = productStore.getString("productStoreId");
}
context.currentStoreId = currentStoreId;

// Get current catalog
List<String> catalogIdsAvailable = null;
if (currentStoreId != null) {
	catalogIdsAvailable = CatalogWorker.getCatalogIdsAvailable(delegator, currentStoreId, null);
}

String currentCatalogId = "";
if (catalogIdsAvailable) {
	currentCatalogId = catalogIdsAvailable.get(0);
	context.currentCatalogId = currentCatalogId;
}