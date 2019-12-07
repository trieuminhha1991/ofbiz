import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import com.olbius.product.catalog.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.category.NewCategoryWorker;
import com.olbius.product.product.ProductEvents;

detailScreen = "categorydetail";
catalogName = NewCatalogWorker.getCatalogName(request);

productCategoryId = request.getAttribute("productCategoryId") ?: parameters.category_id;
if(productCategoryId == null){
	productCategoryId = parameters.catId;
}
if(productCategoryId == null){
	catalogId = parameters.catalogId;
	request.setAttribute("CURRENT_CATALOG_ID", catalogId);
	productCategoryId = NewCatalogWorker.getCatalogTopCategoryId(request);
}
catMap = UtilMisc.toMap("productCategoryId", productCategoryId);
category = delegator.makeValue("ProductCategoryRollupAndChild", catMap);
listChildrenCategory = NewCategoryWorker.getChildrenCategory(category) ;
if(listChildrenCategory.size() != 0){
	context.listChildrenCategory = listChildrenCategory;
}
context.productCategoryId = productCategoryId;

String productStoreId = ProductEvents.getProductStoreId(request);
context.productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);

pageTitle = null;
metaDescription = null;
metaKeywords = null;

category = delegator.findOne("ProductCategory", [productCategoryId : productCategoryId], true);
if (category) {
    if (category.detailScreen) {
        detailScreen = category.detailScreen;
    }
    categoryPageTitle = delegator.findByAnd("ProductCategoryContentAndInfo", [productCategoryId : productCategoryId, prodCatContentTypeId : "PAGE_TITLE"], null, true);
    if (categoryPageTitle) {
        pageTitle = delegator.findOne("ElectronicText", [dataResourceId : categoryPageTitle.get(0).dataResourceId], true);
    }
    categoryMetaDescription = delegator.findByAnd("ProductCategoryContentAndInfo", [productCategoryId : productCategoryId, prodCatContentTypeId : "META_DESCRIPTION"], null, true);
    if (categoryMetaDescription) {
        metaDescription = delegator.findOne("ElectronicText", [dataResourceId : categoryMetaDescription.get(0).dataResourceId], true);
    }
    categoryMetaKeywords = delegator.findByAnd("ProductCategoryContentAndInfo", [productCategoryId : productCategoryId, prodCatContentTypeId : "META_KEYWORD"], null, true);
    if (categoryMetaKeywords) {
        metaKeywords = delegator.findOne("ElectronicText", [dataResourceId : categoryMetaKeywords.get(0).dataResourceId], true);
    }
    categoryContentWrapper = new CategoryContentWrapper(category, request);

    categoryDescription = categoryContentWrapper.DESCRIPTION;

//    if (pageTitle) {
//        context.title = pageTitle.textData;
//    } else {
//        context.title = categoryContentWrapper.CATEGORY_NAME;
//    }

    if (metaDescription) {
        context.metaDescription = metaDescription.textData;
    } else {
        if (categoryDescription) {
            context.metaDescription = categoryDescription;
        }
    }

    if (metaKeywords) {
        context.metaKeywords = metaKeywords.textData;
    } else {
        if (categoryDescription) {
            context.metaKeywords = categoryDescription + ", " + catalogName;
        } else {
            context.metaKeywords = catalogName;
        }
    }
    context.productCategory = category;
}

// check the catalogs template path and update
templatePathPrefix = NewCatalogWorker.getTemplatePathPrefix(request);
if (templatePathPrefix) {
    detailScreen = templatePathPrefix + detailScreen;
}
context.detailScreen = detailScreen;
context.hoz = parameters.hoz;

request.setAttribute("productCategoryId", productCategoryId);
request.setAttribute("defaultViewSize", 20);
request.setAttribute("limitView", true);
