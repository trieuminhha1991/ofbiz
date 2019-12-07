import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import com.olbius.product.category.CategoryUtils;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.product.ProductEvents;

// view catalog or category
productCategoryId = request.getAttribute("productCategoryId1");
currentCatalogId = NewCatalogWorker.getCurrentCatalogId(request);
context.productCategoryId = productCategoryId;
// set the default view size
if (productCategoryId) {

	// get the product category & members
	andMap = [productCategoryId : productCategoryId,
			  defaultViewSize:4,
			  limitView:true];
	andMap.put("prodCatalogId", currentCatalogId);
	andMap.put("checkViewAllow", true);
	andMap.put("orderByFields", ["sequenceNum", "productId"]);
	catResult = CategoryUtils.getProductCategoryAndLimitedMembers(delegator, andMap);

	productCategory = catResult.productCategory;
	productCategoryMembers = catResult.productCategoryMembers;

	// Prevents out of stock product to be displayed on site
	String productStoreId = ProductEvents.getProductStoreId(request);
	productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
	if(productStore) {
		if("N".equals(productStore.showOutOfStockProducts)) {
			productsInStock = [];
			productCategoryMembers.each { productCategoryMember ->
				product = delegator.findOne("Product", [productId : productCategoryMember.productId], true);
				boolean isMarketingPackage = EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG");
				context.isMarketingPackage = (isMarketingPackage? "true": "false");
				if (isMarketingPackage) {
					resultOutput = dispatcher.runSync("getMktgPackagesAvailable", [productId : productCategoryMember.productId]);
					availableInventory = resultOutput.availableToPromiseTotal;
					if(availableInventory > 0) {
						productsInStock.add(productCategoryMember);
					}
				} else {
					facilities = delegator.findList("ProductFacility", EntityCondition.makeCondition([productId : productCategoryMember.productId]), null, null, null, true);
					availableInventory = 0.0;
					if (facilities) {
						facilities.each { facility ->
							lastInventoryCount = facility.lastInventoryCount;
							if (lastInventoryCount != null) {
								availableInventory += lastInventoryCount;
							}
						}
						if (availableInventory > 0) {
							productsInStock.add(productCategoryMember);
						}
					}
				}
			}
			context.productCategoryMembers = productsInStock;
		} else {
			context.productCategoryMembers = productCategoryMembers;
		}
	}
	context.productCategory = productCategory;

	// set the content path prefix
	contentPathPrefix = NewCatalogWorker.getContentPathPrefix(request);
	context.put("contentPathPrefix", contentPathPrefix);

	// little routine to see if any members have a quantity > 0 assigned
	members = context.get("productCategoryMembers");
	if (UtilValidate.isNotEmpty(members)) {
		for (i = 0; i < members.size(); i++) {
			productCategoryMember = (GenericValue) members.get(i);
			if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {
				context.put("hasQuantities", new Boolean(true));
				break;
			}
		}
	}

	CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(productCategory, request);
	context.put("categoryContentWrapper", categoryContentWrapper);
} else {
	context.productCategoryMembers = null;
}