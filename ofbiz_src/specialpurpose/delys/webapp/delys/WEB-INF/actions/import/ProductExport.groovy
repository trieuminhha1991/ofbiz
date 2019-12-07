import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

List<GenericValue> listProduct = delegator.findList("Product", null, null, null, null, false);
ListMapProduct = [];
for (GenericValue product : listProduct) {
	resultMap = [:];
	String productId = (String) product.get("productId");
	String productTypeId = (String) product.get("productTypeId");
	String primaryProductCategoryId = (String) product.get("primaryProductCategoryId");
	String internalName = (String) product.get("internalName");
	String description = (String) product.get("description");
	String weight = (String) product.get("weight");
	String productWidth = (String) product.get("productWidth");
	resultMap = [productId: productId, productTypeId: productTypeId, primaryProductCategoryId: primaryProductCategoryId, internalName: internalName, description: description, weight: weight, productWidth: productWidth];
	ListMapProduct.add(resultMap);
}
context.listProduct = ListMapProduct;