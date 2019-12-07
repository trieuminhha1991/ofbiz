import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.entity.condition.EntityJoinOperator;

def productId = parameters.productId;
if(UtilValidate.isNotEmpty(productId)){
	def attrName = EntityUtilProperties.getPropertyValue("dms.properties", "productAttrName.displayColor", delegator);
	GenericValue productAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", attrName), false);
	if (UtilValidate.isNotEmpty(productAttribute)) {
		def attrValue = productAttribute.getString("attrValue");
		context.displayColor = attrValue;
	}

	GenericValue thisProduct = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	context.thisProduct = thisProduct;
	context.productId = productId;
	List<GenericValue> listProductAssoc = delegator.findList("ProductAssoc", EntityCondition.makeCondition(UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT")), null, null, null, false);
	if (UtilValidate.isNotEmpty(listProductAssoc)) {
		GenericValue productAssoc = EntityUtil.getFirst(listProductAssoc);
		String productIdTo = productAssoc.getString("productId");
		context.productIdTo = productIdTo;
	}
	List<GenericValue> listProductVirtualFeature = delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "STANDARD_FEATURE")), null, null, null, false);
	for (GenericValue x : listProductVirtualFeature) {
		GenericValue productFeature = delegator.findOne("ProductFeature", UtilMisc.toMap("productFeatureId", x.getString("productFeatureId")), false);
		String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
		switch (productFeatureTypeId) {
		case "TASTE":
			context.productFeatureTaste = productFeature.getString("productFeatureId");
			break;
		case "SIZE":
			context.productFeatureSize = productFeature.getString("productFeatureId");
			break;
		case "COLOR":
			context.productFeatureColor = productFeature.getString("productFeatureId");
			break;
		default:
			break;
		}
	}

	List<GenericValue> listProductVirtualFeature2 = delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE")), null, null, null, false);
	List<String> listProductFeatureTaste2 = FastList.newInstance();
	List<String> listProductFeatureSize2 = FastList.newInstance();
	List<String> listProductFeatureColor2 = FastList.newInstance();
	for (GenericValue x : listProductVirtualFeature2) {
		GenericValue productFeature = delegator.findOne("ProductFeature", UtilMisc.toMap("productFeatureId", x.getString("productFeatureId")), false);
		String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
		switch (productFeatureTypeId) {
		case "TASTE":
			listProductFeatureTaste2.add(productFeature.getString("productFeatureId"));
			break;
		case "SIZE":
			listProductFeatureSize2.add(productFeature.getString("productFeatureId"));
			break;
		case "COLOR":
			listProductFeatureColor2.add(productFeature.getString("productFeatureId"));
			break;
		default:
			break;
		}
	}
	context.listProductFeatureTaste2 = listProductFeatureTaste2;
	context.listProductFeatureSize2 = listProductFeatureSize2;
	context.listProductFeatureColor2 = listProductFeatureColor2;

	List<EntityCondition> conditions = FastList.newInstance();
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE", "productPricePurposeId",
			"PURCHASE", "productStoreGroupId", "_NA_")));
	listProductPrice = delegator.findList("ProductPrice",
			EntityCondition.makeCondition(conditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listProductPrice)) {
		context.productListPrice = EntityUtil.getFirst(listProductPrice);
	}
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId",
			"PURCHASE", "productStoreGroupId", "_NA_")));
	listProductPrice = delegator.findList("ProductPrice",
			EntityCondition.makeCondition(conditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listProductPrice)) {
		context.productDefaultPrice = EntityUtil.getFirst(listProductPrice);
	}
//	get tax category
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY")));
	List<GenericValue> listProductCategory = delegator.findList("ProductCategory",
			EntityCondition.makeCondition(conditions, EntityJoinOperator.AND), null, null, null, false);
	List<String> listProductCategoryId = EntityUtil.getFieldListFromEntityList(listProductCategory, "productCategoryId", true);
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, listProductCategoryId));
	conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
	List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
			EntityCondition.makeCondition(conditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listProductCategoryMember)) {
		GenericValue productCategoryMember = EntityUtil.getFirst(listProductCategoryMember);
		context.productCategoryTaxId = productCategoryMember.getString("productCategoryId");
	}

}