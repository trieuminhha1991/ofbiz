import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.category.CategoryUtils;
import com.olbius.product.product.ProductEvents;

currentCatalogId = NewCatalogWorker.getCurrentCatalogId(request);

viewSize = parameters.VIEW_SIZE;
viewIndex = parameters.VIEW_INDEX;
brand = parameters.brand;

sortBy = parameters.sb;
sortValue = parameters.vl;

lowPrice = "";
highPrice = "";
if(sortBy && sortValue){
	switch(sortBy){
		case "price" :
			price = sortValue.split("-");
			if(price.length == 2){
				lowPrice = price[0];
				highPrice = price[1];
			}
			break;
	}
}

defaultViewSize = EntityUtilProperties.getPropertyAsInteger("ecommerce.properties", "category.detail.viewsize", 0);
limitView = request.getAttribute("limitView") ?: EntityUtilProperties.getPropertyAsBoolean("ecommerce.properties", "category.detail.limitView", true);
if (productCategoryId) {
	andMap = [productCategoryId : productCategoryId,
			viewIndexString : viewIndex,
			viewSizeString : viewSize,
			defaultViewSize: defaultViewSize,
			limitView : limitView];
	andMap.put("prodCatalogId", currentCatalogId);
	andMap.put("checkViewAllow", true);
	if (context.orderByFields) {
		andMap.put("orderByFields", context.orderByFields);
	} else {
		andMap.put("orderByFields", ["sequenceNum", "productId"]);
	}
	if(highPrice != "" && lowPrice != ""){
		andMap.put("lowPrice", lowPrice);
		andMap.put("highPrice", highPrice);
	}
	if (UtilValidate.isNotEmpty(brand)) {
		andMap.put("brand", brand);
	}
	def productAppeared = context.productAppeared;
	if (UtilValidate.isNotEmpty(productAppeared)) {
		andMap.put("productAppeared", productAppeared);
	}
	catResult = CategoryUtils.getProductCategoryAndLimitedMembers(delegator, andMap);
	productCategory = catResult.productCategory;
	productCategoryMembers = catResult.productCategoryMembers;
	CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(productCategory, request);

	context.viewIndex = catResult.viewIndex;
	context.viewSize = catResult.viewSize;
	context.listSize = catResult.listSize;
	context.productCategoryMembers = productCategoryMembers;
	context.productCategory = productCategory;
	contentPathPrefix = NewCatalogWorker.getContentPathPrefix(request);
	context.put("contentPathPrefix", contentPathPrefix);
	context.put("categoryContentWrapper", categoryContentWrapper);
} else {
	context.productCategoryMembers = null;
}
context.productCategoryId = productCategoryId;
context.limitView = limitView;
context.defaultViewSize = defaultViewSize;
context.currentCatalogId = currentCatalogId;
