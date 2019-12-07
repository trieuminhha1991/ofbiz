import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.productData.catalog.*;
import org.ofbiz.productData.store.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.webapp.website.WebSiteWorker
import org.ofbiz.product.product.ProductContentWrapper;

import java.text.NumberFormat;
import com.olbius.product.product.ProductEvents;
import com.olbius.product.catalog.NewCatalogWorker;
//either optProduct, optProductId or productId must be specified
product = request.getAttribute("optProduct");
optProductId = request.getAttribute("optProductId");
productId = product?.productId ?: optProductId ?: request.getAttribute("productId");
if(!productId){
	productId = parameters.product_id;
}
webSiteId = WebSiteWorker.getWebSiteId(request);
catalogId = NewCatalogWorker.getCurrentCatalogId(request);
categoryId = parameters.category_id ?: request.getAttribute("productCategoryId");
cart = ShoppingCartEvents.getCartObject(request);

def productStoreId = context.productStoreId;

if (!productStoreId) {
	productStoreId = ProductEvents.getProductStoreId(request);
}
productData = null;
// get the productData entity
if (!productData && productId) {
    productData = delegator.findOne("ProductCacheData", [productId : productId, productStoreId : productStoreId, partyId : "_NA_"], true);
}
if(!product && productId){
	product = delegator.findOne("Product", [productId : productId], true);
	productContentWrapper = new ProductContentWrapper(product, request);
}
if (productData) {
	priceMap = [
		listPrice : productData.listPrice,
		price : productData.price,
		defaultPrice : productData.defaultPrice,
		currencyUsed : productData.currencyUsed
	];
	context.price = priceMap;
	totalProductRating = productData.rating;
	numRatings = productData.totalReview;
	averageRating = 0;
	if(numRatings != 0){
		averageRating = totalProductRating/numRatings
	}
	context.averageRating = averageRating;
	context.numRatings = numRatings;
}

def prefixUrl = context.prefixUrl;
if (!prefixUrl) {
	prefixUrl = request.getScheme() + "://" +
			request.getServerName() +
			("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() );
	context.prefixUrl = prefixUrl;
}


context.product = product;
context.productData = productData;
context.productContentWrapper = productContentWrapper;
context.categoryId = categoryId;
context.productId = productId;
context.productStoreId = productStoreId;