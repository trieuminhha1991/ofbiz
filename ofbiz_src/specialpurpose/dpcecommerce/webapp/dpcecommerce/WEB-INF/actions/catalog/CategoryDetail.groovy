import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.feature.*;
import org.ofbiz.product.product.*;
import org.ofbiz.entity.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import com.olbius.ecommerce.ConfigUtils;

productCategoryId = request.getParameter("productCategoryId");

request.setAttribute("productCategoryId", productCategoryId);

parameters.VIEW_SIZE = ConfigUtils.CATEGORY_DETAIL_PAGE_SIZE;

if(UtilValidate.isNotEmpty(productCategoryId)){
	category = delegator.findOne("ProductCategory", [productCategoryId : productCategoryId], false);
	context.category = category;
}
