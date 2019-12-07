import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.product.ProductEvents;
import com.olbius.product.category.CategoryUtils;
import com.olbius.product.category.NewCategoryWorker;
import org.ofbiz.entity.util.EntityUtilProperties;

detailScreen = EntityUtilProperties.getPropertyValue("ecommerce.properties", "category.detail.view", "categorydetail", delegator);

catalogName = NewCatalogWorker.getCatalogName(request);

productCategoryId = request.getAttribute("productCategoryId") ?: parameters.catId;

if(productCategoryId == null){
	productCategoryId = parameters.category_id;
}
if(productCategoryId == null){
	catalogId = parameters.catalogId;
	request.setAttribute("CURRENT_CATALOG_ID", catalogId);
	productCategoryId = NewCatalogWorker.getCatalogTopCategoryId(request);
}

String productStoreId = ProductEvents.getProductStoreId(request);

categoryData = CategoryUtils.getCategoryData(delegator, request, productCategoryId);

detailScreen = categoryData.detailScreen ?: detailScreen;
category = categoryData.productCategory;

listChildrenCategory = NewCategoryWorker.getChildrenCategory(category);
if(UtilValidate.isNotEmpty(listChildrenCategory)){
	context.listChildrenCategory = listChildrenCategory;
}

// check the catalogs template path and update
templatePathPrefix = NewCatalogWorker.getTemplatePathPrefix(request);
if (templatePathPrefix) {
	detailScreen = templatePathPrefix + detailScreen;
}

context.productCategoryId = productCategoryId;
context.productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
context.title = categoryData.title;
context.metaDescription = categoryData.metaDescription;
context.metaKeywords = categoryData.metaKeywords;
context.productCategory = category;
context.productCategoryId = productCategoryId;
context.detailScreen = detailScreen;
context.hoz = parameters.hoz;
