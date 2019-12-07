import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

private Map<String, Object> getCategoryAndProductChild(String objectId, String objectType) {
	Map<String, Object> resultValue = FastMap.newInstance();
	
	if ("PRODUCT" == objectType) {
		GenericValue thisProduct = delegator.findOne("Product", UtilMisc.toMap("productId", objectId), false);
		if (thisProduct) { // return {key : item}. item -> [name, items]. items -> {key : [name, items]}
			String key = thisProduct.get("productId");
			Map<String, Object> item = FastMap.newInstance();
			String name = thisProduct.get("internalName");
			List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
			
			item.put("name", name);
			item.put("items", items);
			resultValue.put(key, item);
		}
	} else if ("CATEGORY" == objectType) {
		GenericValue thisCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", objectId), false);
		if (thisCategory) { // return {key : item}. item -> [name, items]. items -> {key : [name, items]}
			String key = thisCategory.get("productCategoryId");
			Map<String, Object> item = FastMap.newInstance();
			String name = thisCategory.get("categoryName");
			List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
			
			List<GenericValue> listCategoryRollup = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryRollupAndChild",
							UtilMisc.toMap("parentProductCategoryId", key, "productCategoryTypeId", "CATALOG_CATEGORY"), null, false));
			if (listCategoryRollup != null) {
				for (GenericValue categoryRollup : listCategoryRollup) {
					Map<String, Object> item2 = getCategoryAndProductChild(categoryRollup.getString("productCategoryId"), "CATEGORY");
					if (item2 != null) items.add(item2);
				}
			}
			
			List<GenericValue> listProductMember = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryMemberDetail",
							UtilMisc.toMap("productCategoryId", key), null, false));
			if (listProductMember != null) {
				for (GenericValue productMember : listProductMember) {
					Map<String, Object> item2 = getCategoryAndProductChild(productMember.getString("productId"), "PRODUCT");
					if (item2 != null) items.add(item2);
				}
			}
			
			item.put("name", name);
			item.put("items", items);
			resultValue.put(key, item);
		}
	}
	
	return resultValue;
}

private List<Map<String, Object>> buildColumnList(List<Map<String, Object>> listItem, String parentId, String parentName, String grandParentId, List<Map<String, Object>> listGroupResult, List<String> productListName) {
	List<Map<String, Object>> returnValue = new ArrayList<Map<String, Object>>();
	Map<String, Object> oItem = FastMap.newInstance();
	oItem.columnGroupId = parentId;
	oItem.columnGroupName = parentName;
	oItem.columnGroupGroupId = grandParentId;
	listGroupResult.add(oItem);
	for (Map<String, Object> item : listItem) {
		for (Entry<String, Map<String, Object>> eItem : item.entrySet()) {
			String id = eItem.getKey();
			Map<String, Object> pItem = eItem.getValue();
			String name = pItem.name;
			if (UtilValidate.isEmpty(pItem.items)) {
				Map<String, Object> iItem = FastMap.newInstance();
				iItem.columnId = id;
				iItem.columnName = name;
				iItem.columnGroupId = parentId;
				returnValue.add(iItem);
				productListName.add(id);
			} else {
				returnValue.addAll(buildColumnList(pItem.items, id));
			}
		}
	}
	return returnValue;
}

String customTimePeriodId = parameters.customTimePeriodId;
String salesTypeId = parameters.salesTypeId;
String statusId = parameters.statusId;
String catalogId = "DelysCatalog";
context.customTimePeriodId = customTimePeriodId;
context.salesTypeId = salesTypeId;
context.catalogId = catalogId;
if (catalogId) {
	GenericValue catalog = delegator.findOne("ProdCatalog", ["prodCatalogId" : catalogId], false);
	if (catalog) {
		GenericValue categoryRoot = EntityUtil.getFirst(delegator.findByAnd("ProdCatalogCategory", UtilMisc.toMap("prodCatalogId", catalogId), null, false));
		if (categoryRoot != null) {
			Map<String, Object> categoryProductMap = getCategoryAndProductChild(categoryRoot.getString("productCategoryId"), "CATEGORY");
			if (UtilValidate.isNotEmpty(categoryProductMap)) {
				// remove item root
				
				// build column list
				Map.Entry<String, Map<String, Object>> entry = categoryProductMap.entrySet().iterator().next();
				Map<String, Object> itemMap = entry.getValue();
				List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> listGroupResult = new ArrayList<Map<String, Object>>();
				List<String> productListName = new ArrayList<String>();
				if (itemMap != null) {
					String parentId = entry.getKey();
					String parentName = itemMap.name;
					Map<String, Object> oItem = FastMap.newInstance();
					oItem.columnGroupId = parentId;
					oItem.columnGroupName = parentName;
					oItem.columnGroupGroupId = "";
					listGroupResult.add(oItem);
					for (Map<String, Object> item : itemMap.items) {
						for (Entry<String, Map<String, Object>> eItem : item.entrySet()) {
							String id = eItem.getKey();
							Map<String, Object> pItem = eItem.getValue();
							String name = pItem.name;
							if (UtilValidate.isEmpty(pItem.items)) {
								Map<String, Object> iItem = FastMap.newInstance();
								iItem.columnId = id;
								iItem.columnName = name;
								iItem.columnGroupId = parentId;
								listResult.add(iItem);
								productListName.add(id);
							} else {
								listResult.addAll(buildColumnList(pItem.items, id, name, parentId, listGroupResult, productListName));
							}
						}
					}
				}
				context.columListData = listResult;
				context.columnGroupListData = listGroupResult;
			}
		}
	}
}

List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatementHeader", ["customTimePeriodId" : customTimePeriodId, "salesTypeId" : salesTypeId, "statusId" : statusId], ["salesId"], false);
if (listSalesStatement) {
	List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	for (GenericValue salesHeader : listSalesStatement) {
		Map<String, Object> iHeader = FastMap.newInstance();
		iHeader.put("salesId", salesHeader.getString("salesId"));
		iHeader.put("partyId", salesHeader.getString("internalPartyId"));
		iHeader.put("parentId", salesHeader.getString("parentSalesId"));
		List<GenericValue> listSalesItem = delegator.findByAnd("SalesStatementItem", ["salesId": salesHeader.get("salesId")], null, false);
		if (listSalesItem) {
			for (GenericValue salesItem : listSalesItem) {
				String key = "";
				BigDecimal value = null;
				BigDecimal actual = null;
				BigDecimal percent = null;
				if (UtilValidate.isNotEmpty(salesItem.getString("productId"))) {
					key = salesItem.getString("productId");
					value = salesItem.getBigDecimal("quantity");
					actual = salesItem.getBigDecimal("quantityActual");
					if (BigDecimal.ZERO.compareTo(value) != 0 && actual != null) {
						percent = actual.multiply(new BigDecimal(100)).divide(value, 2, RoundingMode.HALF_UP);
					}
				} else if (UtilValidate.isNotEmpty(salesItem.getString("productCategoryId"))) {
					key = salesItem.getString("productCategoryId");
					value = salesItem.getBigDecimal("quantity");
					actual = salesItem.getBigDecimal("quantityActual");
					if (BigDecimal.ZERO.compareTo(value) != 0 && actual != null) {
						percent = actual.multiply(new BigDecimal(100)).divide(value, 2, RoundingMode.HALF_UP);
					}
				}
				iHeader.put(key + "_target", value);
				iHeader.put(key + "_actual", actual);
				iHeader.put(key + "_percent", percent);
			}
		}
		listData.add(iHeader);
	}
	context.listData = listData;
}