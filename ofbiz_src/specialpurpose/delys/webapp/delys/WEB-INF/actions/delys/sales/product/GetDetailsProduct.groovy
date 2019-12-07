import java.util.*;
import org.ofbiz.base.util.*;

if(UtilValidate.isNotEmpty(parameters.productId)){
	def productId = parameters.productId;
	def fromDate = Timestamp.valueOf(parameters.fromDate);
	def productCategoryId = parameters.productCategoryId;
	GenericValue thisProduct = delegator.findOne("ProductAndProductCategoryMember", UtilMisc.toMap("productId", productId, "fromDate", fromDate, "productCategoryId", productCategoryId), false);
	context.thisProduct = thisProduct;
}