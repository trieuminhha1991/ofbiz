import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.webapp.website.WebSiteWorker;
import com.olbius.product.product.ProductEvents;

import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.basepo.product.ProductContentUtils;

//either optProduct, optProductId or productId must be specified
product = request.getAttribute("optProduct");
optProductId = request.getAttribute("optProductId");
productId = product?.productId ?: optProductId ?: request.getAttribute("productId");
context.productId = productId;
webSiteId = WebSiteWorker.getWebSiteId(request);
catalogId = NewCatalogWorker.getCurrentCatalogId(request);
cart = ShoppingCartEvents.getCartObject(request);
productStore = null;
productStoreId = null;
facilityId = null;
if (cart.isSalesOrder()) {
	productStoreId = ProductEvents.getProductStoreId(request);
	productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
    productStoreId = productStore.productStoreId;
    context.productStoreId = productStoreId;
    facilityId = productStore.inventoryFacilityId;
}
autoUserLogin = session.getAttribute("autoUserLogin");
userLogin = session.getAttribute("userLogin");

// get the product entity
if (!product && productId) {
    product = delegator.findOne("Product", [productId : productId], true);
}
if (product) {
    // make the productContentWrapper
    productContentWrapper = new ProductContentWrapper(product, request);
    context.productContentWrapper = productContentWrapper;
}

categoryId = null;
reviews = null;
if (product) {
	categoryId = parameters.category_id ?: request.getAttribute("productCategoryId");
    // get the product price
    if (cart.isSalesOrder()) {
        // sales order: run the "calculateProductPrice" service
        priceContext = [product : product, currencyUomId : cart.getCurrency(),
                autoUserLogin : autoUserLogin, userLogin : userLogin];
        priceContext.webSiteId = webSiteId;
        priceContext.prodCatalogId = catalogId;
        priceContext.productStoreId = productStoreId;
        priceContext.agreementId = cart.getAgreementId();
        priceContext.partyId = cart.getPartyId();  // IMPORTANT: otherwise it'll be calculating prices using the logged in user which could be a CSR instead of the customer
        priceContext.checkIncludeVat = "Y";
		priceContext.quantityUomId= product.quantityUomId;
        priceMap = dispatcher.runSync("calculateProductPriceCustom", priceContext);
        context.price = priceMap;
    } else {
        // purchase order: run the "calculatePurchasePrice" service
        priceContext = [product : product, currencyUomId : cart.getCurrency(),
                partyId : cart.getPartyId(), userLogin : userLogin];
        priceMap = dispatcher.runSync("calculatePurchasePrice", priceContext);

        context.price = priceMap;
    }

    // get aggregated product totalPrice
    if ("AGGREGATED".equals(product.productTypeId)||"AGGREGATED_SERVICE".equals(product.productTypeId)||"AGGR_DIGSERV".equals(product.productTypeId)) {
        configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency(), request);
        if (configWrapper) {
            configWrapper.setDefaultConfig();
            context.totalPrice = configWrapper.getTotalPrice();
        }
    }

    // get the product review(s)
    reviewByAnd = [:];
    reviewByAnd.statusId = "PRR_APPROVED";
//    reviews = product.getRelated("ProductReview", reviewByAnd, ["-postedDateTime"], false);
    reviews = ProductContentUtils.productReview(delegator, userLogin, productId, productStoreId);
}

// get the average rating
numRatings = 0;
totalProductRating = 0;
averageRating = 0;
if (reviews) {
    reviews.each { productReview ->
        productRating = productReview.productRating;
        if (productRating) {
            totalProductRating += productRating;
            numRatings++;
        }
    }
    if (numRatings > 0) {
        averageRating = totalProductRating/numRatings;
    }
}
context.numRatings = numRatings;
context.averageRating = averageRating;

context.productMini = product;
context.categoryId = categoryId;
context.productReviews = reviews;