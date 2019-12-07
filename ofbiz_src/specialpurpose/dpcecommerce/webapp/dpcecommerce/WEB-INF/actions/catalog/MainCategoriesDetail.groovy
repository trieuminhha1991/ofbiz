import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
// variable

// methods
public static String getCurrentCatalogId(ServletRequest request) {
	HttpSession session = ((HttpServletRequest) request).getSession();
	Map<String, Object> requestParameters = UtilHttp.getParameterMap((HttpServletRequest) request);
	String prodCatalogId = null;
	boolean fromSession = false;

	// first see if a new catalog was specified as a parameter
	prodCatalogId = (String) requestParameters.get("CURRENT_CATALOG_ID");
	// if no parameter, try from session
	if (prodCatalogId == null) {
		prodCatalogId = (String) session.getAttribute("CURRENT_CATALOG_ID");
		if (prodCatalogId != null) fromSession = true;
	}
	// get it from the database
	if (prodCatalogId == null) {
		List<String> catalogIds = getCatalogIdsAvailable(request);
		if (UtilValidate.isNotEmpty(catalogIds)) prodCatalogId = catalogIds.get(0);
	}

	if (!fromSession) {
		session.setAttribute("CURRENT_CATALOG_ID", prodCatalogId);
		CategoryWorker.setTrail(request, FastList.<String>newInstance());
	}
	return prodCatalogId;
}
public static String getCatalogWhatNewCategoryId(ServletRequest request) {
	return getCatalogWhatNewCategoryId(request, getCurrentCatalogId(request));
}
public static String getCatalogWhatNewCategoryId(ServletRequest request, String prodCatalogId) {
	if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

	List<GenericValue> prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId, "PCCT_BEST_SELL");

	if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
		GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);

		return prodCatalogCategory.getString("productCategoryId");
	} else {
		return null;
	}
}
public static List<GenericValue> getProdCatalogCategories(ServletRequest request, String prodCatalogId, String prodCatalogCategoryTypeId) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	return getProdCatalogCategories(delegator, prodCatalogId, prodCatalogCategoryTypeId);
}

public static List<GenericValue> getProdCatalogCategories(Delegator delegator, String prodCatalogId, String prodCatalogCategoryTypeId) {
	try {
		List<GenericValue> prodCatalogCategories = EntityUtil.filterByDate(delegator.findByAnd("ProdCatalogCategory",
					UtilMisc.toMap("prodCatalogId", prodCatalogId),
					UtilMisc.toList("sequenceNum", "productCategoryId"), true), true);

		if (UtilValidate.isNotEmpty(prodCatalogCategoryTypeId) && prodCatalogCategories != null) {
			prodCatalogCategories = EntityUtil.filterByAnd(prodCatalogCategories,
						UtilMisc.toMap("prodCatalogCategoryTypeId", prodCatalogCategoryTypeId));
		}
		return prodCatalogCategories;
	} catch (GenericEntityException e) {
	}
	return null;
}

// get current catalogid
String currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
productCategoryId = getCatalogWhatNewCategoryId(request);

context.currentCatalogId = currentCatalogId;
context.prodCatalogId = productCategoryId;
viewSize = parameters.VIEW_SIZE;
viewIndex = parameters.VIEW_INDEX;
// get data


context.productCategoryId = productCategoryId;

viewSize = parameters.VIEW_SIZE;
viewIndex = parameters.VIEW_INDEX;
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);

// set the default view size
defaultViewSize = request.getAttribute("defaultViewSize") ?: 20;
context.defaultViewSize = defaultViewSize;

// set the limit view
limitView = request.getAttribute("limitView") ?: true;
context.limitView = limitView;

// get the product category & members
andMap = [productCategoryId : productCategoryId,
        viewIndexString : viewIndex,
        viewSizeString : viewSize,
        defaultViewSize : defaultViewSize,
        limitView : limitView];
andMap.put("prodCatalogId", currentCatalogId);
andMap.put("checkViewAllow", true);
if (context.orderByFields) {
    andMap.put("orderByFields", context.orderByFields);
} else {
    andMap.put("orderByFields", ["sequenceNum", "productId"]);
}
catResult = dispatcher.runSync("getProductCategoryAndLimitedMembers", andMap);

productCategory = catResult.productCategory;
productCategoryMembers = catResult.productCategoryMembers;

// Prevents out of stock product to be displayed on site
productStore = ProductStoreWorker.getProductStore(request);
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
                facilities = delegator.findList("ProductFacility", EntityCondition.makeCondition([productId : productCategoryMember.productId]), null, null, null, false);
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
context.viewIndex = catResult.viewIndex;
context.viewSize = catResult.viewSize;
context.lowIndex = catResult.lowIndex;
context.highIndex = catResult.highIndex;
context.listSize = catResult.listSize;

// set this as a last viewed
// DEJ20070220: WHY is this done this way? why not use the existing CategoryWorker stuff?
LAST_VIEWED_TO_KEEP = 10; // modify this to change the number of last viewed to keep
lastViewedCategories = session.getAttribute("lastViewedCategories");
if (!lastViewedCategories) {
    lastViewedCategories = [];
    session.setAttribute("lastViewedCategories", lastViewedCategories);
}
lastViewedCategories.remove(productCategoryId);
lastViewedCategories.add(0, productCategoryId);
while (lastViewedCategories.size() > LAST_VIEWED_TO_KEEP) {
    lastViewedCategories.remove(lastViewedCategories.size() - 1);
}

// set the content path prefix
contentPathPrefix = CatalogWorker.getContentPathPrefix(request);
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

preCatChilds = delegator.findByAnd("ProductCategoryRollup", ["parentProductCategoryId": productCategoryId], null, false);
catChilds = EntityUtil.getRelated("CurrentProductCategory",null,preCatChilds,false);

context.productCategoryList = catChilds;