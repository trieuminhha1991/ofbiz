
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basepo.product.ProductUtils;

import javolution.util.FastMap;

String productId = parameters.productId;
if (productId) {
	Map<String, Object> resultImages = dispatcher.runSync("loadConfigOfProduct", UtilMisc.toMap("productId", productId, "userLogin", userLogin, "locale", locale));
	if (ServiceUtil.isSuccess(resultImages)) {
		context.dataProdImages = resultImages.config;
	}
	Map<String, Object> resultProductInfo = dispatcher.runSync("loadProductInfo", UtilMisc.toMap("productId", productId));
	if (ServiceUtil.isSuccess(resultProductInfo)) {
		Map<String, Object> dataProdInfo = resultProductInfo.product;
		if (dataProdInfo) {
			GenericValue productDefaultPrice = dataProdInfo.productDefaultPrice;
			GenericValue productListPrice = dataProdInfo.productListPrice;
			Map<String, Object> taxCategoryGV = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			BigDecimal taxPercentage = (BigDecimal) taxCategoryGV.get("taxPercentage");
			
			BigDecimal productDefaultPriceValue = null;
			if (productDefaultPrice != null && productDefaultPrice.get("price") != null) {
				productDefaultPriceValue = productDefaultPrice.getBigDecimal("price");
				if (taxPercentage != null) {
					productDefaultPriceValue = ProductUtils.calculatePriceAfterTax(productDefaultPriceValue, taxPercentage);
				}
			}
			dataProdInfo.productDefaultPriceValue = productDefaultPriceValue;
			
			BigDecimal productListPriceValue = null;
			if (productListPrice != null && productListPrice.get("price") != null) {
				productListPriceValue = productListPrice.getBigDecimal("price");
				if (taxPercentage != null) {
					productListPriceValue = ProductUtils.calculatePriceAfterTax(productListPriceValue, taxPercentage);
				}
			}
			dataProdInfo.productListPriceValue = productListPriceValue;
		}
		context.dataProdInfo = dataProdInfo;
	}
	
	List<GenericValue> productFeatureAppls = delegator.findByAnd("ProductFeatureAppl", ["productId": productId], null, false);
	
	if (productFeatureAppls) {
		Map<String, Object> productFeatureApplMap = FastMap.newInstance();
		for (GenericValue featureAppl in productFeatureAppls) {
			GenericValue feature = delegator.findOne("ProductFeature", ["productFeatureId": featureAppl.productFeatureId], false);
			if (feature) {
				GenericValue featureType = delegator.findOne("ProductFeatureType", ["productFeatureTypeId": feature.productFeatureTypeId], false);
				if (featureType) {
					if (productFeatureApplMap.containsKey(featureType.getString("productFeatureTypeId"))) {
						List<GenericValue> values = productFeatureApplMap.get(featureType.getString("productFeatureTypeId"));
						values.add(feature);
					} else {
						List<GenericValue> values = new ArrayList<GenericValue>();
						values.add(feature);
						productFeatureApplMap.put(featureType.getString("productFeatureTypeId"), values);
					}
				}
			}
		}
		context.productFeatureApplMap = productFeatureApplMap;
	}
	
	EntityFindOptions opts = new EntityFindOptions();
	opts.setDistinct(true);
	List<EntityCondition> conds = new ArrayList<EntityCondition>();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<GenericValue> listProductPrices = delegator.findList("ProductQuotationAndPriceRCADetail", EntityCondition.makeCondition(conds), null, ["-fromDate"], opts, false);
	context.listProductPrices = listProductPrices;
	
	// barcode
	List<GenericValue> listProdBarcode = delegator.findByAnd("GoodIdentification", ["productId": productId, "goodIdentificationTypeId": "SKU", "uomId": product.quantityUomId], null, false);
	if (listProdBarcode) {
		List<String> prodBarcodes = new ArrayList<String>();
		for (GenericValue prodBarcodeItem : listProdBarcode) {
			if (prodBarcodeItem.get("iupprm") != null && "1".equals(prodBarcodeItem.getString("iupprm"))) {
				prodBarcodes.add("<span class='red'>" + prodBarcodeItem.getString("idValue") + "</span>");
			} else {
				prodBarcodes.add(prodBarcodeItem.getString("idValue"));
			}
		}
		context.prodBarcode = StringUtils.join(prodBarcodes, ", ");
	}
	
	// PLU code
	List<String> listProdPLUCode = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("GoodIdentification", ["productId": productId, "goodIdentificationTypeId": "PLU", "uomId": product.quantityUomId], null, false), "idValue", true);
	if (listProdPLUCode) {
		context.prodPLUCode = StringUtils.join(listProdPLUCode, ", ");
	}
}
