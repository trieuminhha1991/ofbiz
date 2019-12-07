
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.basesales.product.ProductWorker;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

filterType = parameters.filterType;
productIds = parameters.productId;
List<GenericValue> listProduct = FastList.newInstance();
List<GenericValue> listProductResult = FastList.newInstance();

boolean isRun = true;
String inlucdeProductPrice = parameters.includePriceRule;
GenericValue productStore = null;
if ("Y".equals(inlucdeProductPrice)) {
	String productStoreId = parameters.productStoreId;
	productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
	if (productStore == null) {
		isRun = false;
	}
}

if (isRun) { // process
List<EntityCondition> conds = FastList.newInstance();

printAlterUom = parameters.printAlterUom;
printNormalUPC = parameters.printNormalUPC;
String entityNameFind = "ProductTddAndSkuAndSupplierRef";
if ("Y".equals(printAlterUom)) {
	entityNameFind = "ProductTddAndSkusAndSupplierRef";
}
if (!"Y".equals(printNormalUPC)) {
	conds.add(EntityCondition.makeCondition("iupprm", 1L));
}

EntityFindOptions opts = new EntityFindOptions();
opts.setDistinct(true);
Set<String> listFieldSelect = FastSet.newInstance();
listFieldSelect.add("productId");
listFieldSelect.add("productCode");
listFieldSelect.add("unitPrice");
listFieldSelect.add("unitListPrice");
listFieldSelect.add("idSKU");
listFieldSelect.add("supplierCode");
listFieldSelect.add("uomId");
listFieldSelect.add("productName");
listFieldSelect.add("quantityUomId");
listFieldSelect.add("taxPercentage");
listFieldSelect.add("currencyUomId");

Map<String, Object> mapIdSkuNum = FastMap.newInstance();
if (productIds) {
	if (productIds instanceof java.util.Collection) {
		for (String idSkuNum : productIds) {
			if (UtilValidate.isNotEmpty(idSkuNum)) {
				String[] idSkuArr = idSkuNum.split("@");
				if (idSkuArr.length > 1) mapIdSkuNum.put(idSkuArr[0], idSkuArr[1]);
			}
		}
	} else {
		String[] idSkuArr = productIds.split("@");
		if (idSkuArr.length > 1) mapIdSkuNum.put(idSkuArr[0], idSkuArr[1]);
	}
	
	List<String> listSortFields = null; //UtilMisc.toList("productCode");
	EntityCondition cond = null;
	Set<String> idSKUKey = mapIdSkuNum.keySet();
	if (UtilValidate.isNotEmpty(idSKUKey)) {
		if (idSKUKey.size() > 1) {
			cond = EntityCondition.makeCondition("idSKU", EntityOperator.IN, idSKUKey);
		} else {
			String idSkuValue = idSKUKey.iterator().next();
			cond = EntityCondition.makeCondition("idSKU", idSkuValue);
		}
		conds.add(cond);
		listProduct = delegator.findList(entityNameFind, EntityCondition.makeCondition(conds), listFieldSelect, listSortFields, opts, false);
	}
} else if (filterType) {
	List<String> listSortFields = UtilMisc.toList("productCode");
	/*EntityFindOptions opts = new EntityFindOptions();
	opts.setLimit(10);
	listProduct = delegator.findList("ProductTempDataDetailAndSupplier", null, null, listSortFields, opts, false);
	if (UtilValidate.isNotEmpty(listProduct)) {
		for (GenericValue item : listProduct) {
			item.unitPrice = com.olbius.basesales.product.ProductWorker.calcPriceTaxDisplay(item.getBigDecimal("unitPrice"), item.getBigDecimal("taxPercentage"), item.getString("currencyUomId"));
		}
	}*/
	if ("ALLPROD".equals(filterType)) {
		listProduct = delegator.findList(entityNameFind, EntityCondition.makeCondition(conds), listFieldSelect, listSortFields, opts, false);
	} else if ("BYCATALOG".equals(filterType)) {
		String prodCatalogId = parameters.prodCatalogId;
		List<String> productCategoryIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogId, "PCCT_BROWSE_ROOT");
			if (listCategory != null) {
				for (GenericValue categoryItem : listCategory) {
					productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
				}
			}
		}
		if (UtilValidate.isNotEmpty(productCategoryIds)) {
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, productCategoryIds));
			listProduct = delegator.findList(entityNameFind, EntityCondition.makeCondition(conds), listFieldSelect, listSortFields, opts, false);
		}
	} else if ("BYCATEGORY".equals(filterType)) {
		String productCategoryId = parameters.productCategoryId;
		if (UtilValidate.isNotEmpty(productCategoryId)) {
			List<String> productCategoryIds = FastList.newInstance();
			productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, productCategoryId, "CATALOG_CATEGORY"));
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, productCategoryIds));
			listProduct = delegator.findList(entityNameFind, EntityCondition.makeCondition(conds), listFieldSelect, listSortFields, opts, false);
		}
	}
	if (UtilValidate.isNotEmpty(listProduct)) {
		for (GenericValue item : listProduct) {
			item.unitPrice = com.olbius.basesales.product.ProductWorker.calcPriceTaxDisplay(item.getBigDecimal("unitPrice"), item.getBigDecimal("taxPercentage"), item.getString("currencyUomId"));
		}
	}
}

if (UtilValidate.isNotEmpty(listProduct)) {
	if (productStore != null) {
		Map<String, Object> productIdPrice = FastMap.newInstance();
		for (GenericValue item : listProduct) {
			String productId = item.getString("productId");
			String uomId = item.getString("uomId");
			String productIdKey = uomId != null ? productId + "@" + uomId : productId + "@_NA_";
			if (!productIdPrice.containsKey(productIdKey)) {
				Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap(
						"productId", productId, "productStoreId", productStore.getString("productStoreId"),
						"quantityUomId", uomId);
				Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
				if (ServiceUtil.isError(resultCalPrice)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));
				}
				BigDecimal basePrice = (BigDecimal) resultCalPrice.get("basePrice");
	        	BigDecimal listPrice = (BigDecimal) resultCalPrice.get("listPrice");
	        	item.put("unitPrice", basePrice);
	        	item.put("unitListPrice", listPrice);
				
	        	productIdPrice.put(productIdKey, UtilMisc.toMap("basePrice", basePrice, "listPrice", listPrice));
			} else {
				Map<String, Object> pricesInfo = (Map<String, Object>) productIdPrice.get(productIdKey);
				item.put("unitPrice", pricesInfo.get("basePrice"));
				item.put("unitListPrice", pricesInfo.get("listPrice"));
			}
			
			// get info tax category
			String taxCategoryId = null;
			BigDecimal taxPercentage = null;
			List<EntityCondition> condsTax = FastList.newInstance();
			condsTax.add(EntityCondition.makeCondition("productId", productId));
			condsTax.add(EntityUtil.getFilterByDateExpr());
			GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), null, null, null, false));
			if (taxCategoryGV != null) {
				taxCategoryId = taxCategoryGV.getString("taxCategoryId");
				taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
			}
			item.put("taxCategoryId", taxCategoryId);
			item.put("taxPercentage", taxPercentage);
			listProductResult.add(item);
			if (mapIdSkuNum.containsKey(item.idSKU)) {
				int numCopy = Integer.parseInt(mapIdSkuNum.get(item.idSKU));
				if (numCopy > 1) {
					for (int i = 0; i < numCopy - 1; i++) {
						listProductResult.add(item);
					}
				}
			}
		}
	} else {
		Map<String, Object> productIdPrice = FastMap.newInstance();
		for (GenericValue item : listProduct) {
			String productId = item.getString("productId");
			String quantityUomId = item.getString("quantityUomId");
			String uomId = item.getString("uomId");
			if (quantityUomId != null && !quantityUomId.equals(uomId)) {
				String productIdKey = uomId != null ? productId + "@" + uomId : productId + "@_NA_";
				if (!productIdPrice.containsKey(productIdKey)) {
					Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", null,
							"quantityUomId", uomId);
					Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
					if (ServiceUtil.isError(resultCalPrice)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));
					}
					BigDecimal basePrice = (BigDecimal) resultCalPrice.get("basePrice");
					BigDecimal listPrice = (BigDecimal) resultCalPrice.get("listPrice");
					item.put("unitPrice", basePrice);
					item.put("unitListPrice", listPrice);
					
					productIdPrice.put(productIdKey, UtilMisc.toMap("basePrice", basePrice, "listPrice", listPrice));
				} else {
					Map<String, Object> pricesInfo = (Map<String, Object>) productIdPrice.get(productIdKey);
					item.put("unitPrice", pricesInfo.get("basePrice"));
					item.put("unitListPrice", pricesInfo.get("listPrice"));
				}
			}
			
			// get info tax category
			String taxCategoryId = null;
			BigDecimal taxPercentage = null;
			List<EntityCondition> condsTax = FastList.newInstance();
			condsTax.add(EntityCondition.makeCondition("productId", productId));
			condsTax.add(EntityUtil.getFilterByDateExpr());
			GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), null, null, null, false));
			if (taxCategoryGV != null) {
				taxCategoryId = taxCategoryGV.getString("taxCategoryId");
				taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
			}
			item.put("taxCategoryId", taxCategoryId);
			item.put("taxPercentage", taxPercentage);
			listProductResult.add(item);
			if (mapIdSkuNum.containsKey(item.idSKU)) {
				int numCopy = item.idSKU;
				if (numCopy > 1) {
					for (int i = 0; i < numCopy - 1; i++) {
						listProductResult.add(item);
					}
				}
			}
		}
	}
}
} // check isRun
context.listProduct = listProductResult;
