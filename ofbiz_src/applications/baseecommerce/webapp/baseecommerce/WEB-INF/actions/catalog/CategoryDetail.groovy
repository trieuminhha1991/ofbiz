import java.lang.invoke.SwitchPoint;
import java.security.Policy.Parameters

import javax.wsdl.Import;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.product.ProductEvents;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.product.NewProductContentWrapper;
import com.olbius.product.category.CategoryUtils;

if (productCategoryId) {
//	String productStoreId = ProductEvents.getProductStoreId(request);
//	productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
	LAST_VIEWED_TO_KEEP = 8; // modify this to change the number of last viewed to keep
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
	// little routine to see if any members have a quantity > 0 assigned
	members = context.get("productCategoryMembers");
	thumbs = context.thumb;
	if(!thumbs){
		thumbs = [];
	}
	if (UtilValidate.isNotEmpty(members)) {
		for (i = 0; i < members.size(); i++) {
			productCategoryMember = (GenericValue) members.get(i);
			productId = productCategoryMember.getString("productId");
			product = delegator.findOne("Product", [productId : productId], false);
			productContentWrapper = new NewProductContentWrapper(product, request, webSiteId);
			thumb = contentPathPrefix + productContentWrapper.get("LARGE_IMAGE_URL");
			if(UtilValidate.isNotEmpty(thumb)){
				thumbs.add(thumb)
			}
			if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {
				context.put("hasQuantities", new Boolean(true));
				break;
			}
		}
	}
	context.thumb = thumbs;
}