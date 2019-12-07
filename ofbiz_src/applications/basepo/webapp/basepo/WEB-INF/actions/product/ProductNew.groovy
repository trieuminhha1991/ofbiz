
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.basepo.product.ProductContentUtils;
import com.olbius.basepo.product.ProductUtils;

if (product) {
	String productId = product.productId;
	String currencyUomId = "";
	
	Map<String, Object> taxCategoryGV = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
	BigDecimal taxPercentage = (BigDecimal) taxCategoryGV.get("taxPercentage");
	
	String quantityUomId = product.quantityUomId;
	List<EntityCondition> conds = new ArrayList<EntityCondition>();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
	conds.add(EntityCondition.makeCondition("termUomId", quantityUomId));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<GenericValue> listProductDefaultPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), null, null, null, false);
	GenericValue productDefaultPrice = EntityUtil.getFirst(listProductDefaultPrice);
	//context.productDefaultPrice = productDefaultPrice;
	if (productDefaultPrice) {
		BigDecimal productDefaultPriceValue = null;
		if (productDefaultPrice.get("price") != null) {
			productDefaultPriceValue = productDefaultPrice.getBigDecimal("price");
			if (taxPercentage != null) {
				productDefaultPriceValue = ProductUtils.calculatePriceAfterTax(productDefaultPriceValue, taxPercentage);
			}
		}
		context.productDefaultPriceValue = productDefaultPriceValue;
		currencyUomId = productDefaultPrice.currencyUomId;
	}
	
	conds.clear();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityCondition.makeCondition("productPriceTypeId", "LIST_PRICE"));
	conds.add(EntityCondition.makeCondition("termUomId", quantityUomId));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<GenericValue> listProductListPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), null, null, null, false);
	GenericValue productListPrice = EntityUtil.getFirst(listProductListPrice);
	//context.productListPrice = productListPrice;
	if (productListPrice) {
		BigDecimal productListPriceValue = null;
		if (productListPrice.get("price") != null) {
			productListPriceValue = productListPrice.getBigDecimal("price");
			if (taxPercentage != null) {
				productListPriceValue = ProductUtils.calculatePriceAfterTax(productListPriceValue, taxPercentage);
			}
		}
		context.productListPriceValue = productListPriceValue;
		currencyUomId = productListPrice.currencyUomId;
	}
	context.currencyUomId = currencyUomId;
	
	// list feature type
	GenericValue featureTypeIdsGV = delegator.findOne("ProductAttribute", ["productId": productId, "attrName": "featureTypes"], false);
	if (featureTypeIdsGV) {
		String featureTypeIdsStr = featureTypeIdsGV.getString("attrValue");
		if (featureTypeIdsStr) {
			List<String> featureTypeIds = SalesUtil.processKeyProperty(featureTypeIdsStr);
			context.featureTypeIds = featureTypeIds;
		}
	}
	
	// parent product
	GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator, null);
	if (parentProduct) {
		context.parentProductId = parentProduct.productId;
	}
	
	// product category member
	conds.clear();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, null, null, false), "productCategoryId", true);
	String primaryProductCategoryId = product.primaryProductCategoryId;
	if (UtilValidate.isNotEmpty(productCategoryIds) && UtilValidate.isNotEmpty(primaryProductCategoryId)) {
		productCategoryIds.remove(primaryProductCategoryId);
	}
	context.productCategoryIds = productCategoryIds;
	
	// tax category
	conds.clear();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<String> productCategoryTaxIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, null, null, false), "productCategoryId", true);
	context.productCategoryTaxIds = productCategoryTaxIds;
	
	// images
	Map<String, Object> getImagesResult = dispatcher.runSync("loadConfigOfProduct", ["productId": productId, "userLogin": userLogin, "locale": locale]);
	if (ServiceUtil.isSuccess(getImagesResult)) {
		context.contentProductMap = getImagesResult.config;
	}
	
	// barcode
	List<String> listProdBarcode = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("GoodIdentification", ["productId": productId, "goodIdentificationTypeId": "SKU", "uomId": product.quantityUomId], null, false), "idValue", true);
	if (listProdBarcode) {
		context.prodBarcode = StringUtils.join(listProdBarcode, ",");
	}
	
	// plucode
	List<String> listProdPlucode = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("GoodIdentification", ["productId": productId, "goodIdentificationTypeId": "PLU", "uomId": product.quantityUomId], null, false), "idValue", true);
	if (UtilValidate.isNotEmpty(listProdPlucode)) {
		context.prodPlucode = listProdPlucode.get(0);
	}
	
	// config packing
	List<EntityCondition> condsUom = new ArrayList<EntityCondition>();
	condsUom.add(EntityCondition.makeCondition("productId", product.productId));
	condsUom.add(EntityUtil.getFilterByDateExpr());
	
	List<Map<String, Object>> configPackingApplsResult = new ArrayList<Map<String, Object>>();
	List<GenericValue> configPackingAppls = delegator.findList("ConfigPacking", EntityCondition.makeCondition(condsUom), null, null, null, false);
	if (configPackingAppls) {
		List<EntityCondition> conds2 = new ArrayList<EntityCondition>();
		for (GenericValue item : configPackingAppls) {
			Map<String, Object> itemNew = item.getAllFields();
			// price
			conds2.clear();
			conds2.add(EntityCondition.makeCondition("productId", productId));
			conds2.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
			conds2.add(EntityUtil.getFilterByDateExpr());
			conds2.add(EntityCondition.makeCondition("termUomId", item.uomFromId));
			GenericValue defaultValue2 = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(conds2), null, null, null, false));
			if (defaultValue2) {
				BigDecimal priceValue = null;
				if (defaultValue2.get("price") != null) {
					priceValue = defaultValue2.getBigDecimal("price");
					if (defaultValue2.get("taxAmount") != null) {
						priceValue = priceValue.add(defaultValue2.getBigDecimal("taxAmount"));
					}
				}
				itemNew.put("price", "" + priceValue);
			}
			// barcode
			List<String> listBarcode2 = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("GoodIdentification", ["productId": productId, "goodIdentificationTypeId": "SKU", "uomId": item.uomFromId], null, false), "idValue", true);
			if (listBarcode2) {
				itemNew.put("barcode", StringUtils.join(listBarcode2, ","));
			}
			// add
			configPackingApplsResult.add(itemNew);
		}
	}
	context.configPackingAppls = configPackingApplsResult;
}