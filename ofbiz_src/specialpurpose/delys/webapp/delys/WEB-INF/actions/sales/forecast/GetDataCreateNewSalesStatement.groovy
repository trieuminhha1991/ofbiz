import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

import com.olbius.sales.SalesEvents;

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

listCategoryId = parameters.categoryId;
listProductId = parameters.productId;
listCategoryOnlyId = parameters.categoryOnlyId;
String salesStatementTypeId = parameters.salesStatementTypeId;
String customTimePeriodId = parameters.customTimePeriodId;
String catalogId = parameters.catalogId;
context.salesStatementTypeId = salesStatementTypeId;
context.customTimePeriodId = customTimePeriodId;
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
				
				// chuan bi data test
				listData = [];
				/*// row 1
				iData = [:];
				iData.partyId = "Person1";
				iData.fullName = "Person1";
				i = 0;
				for (String iProdName : productListName) {
					i = i + 1;
					iData[iProdName] = "1000" + i;
				}
				iData.parentId = "";
				listData.add(iData);
				
				// row 2
				iData2 = [:];
				iData2.partyId = "Person2";
				iData2.fullName = "Person2";
				i = 0;
				for (String iProdName : productListName) {
					i = i + 1;
					iData2[iProdName] = "2000" + i;
				}
				iData2.parentId = "Person1";
				listData.add(iData2);
				
				iData3 = [:];
				iData3.partyId = "Person3";
				iData3.fullName = "Person3";
				i = 0;
				for (String iProdName : productListName) {
					i = i + 1;
					iData3[iProdName] = "3000" + i;
				}
				iData3.parentId = "Person1";
				listData.add(iData3);
				*/
				
				// get list employee
				List<Map<String, Object>> eventMapValue = SalesEvents.getListEmployeeDSAHalf(request, delegator);
				//[{id=NBD, parentId=DSA, groupName=National Business Director, fullName=National Business Director - Nguyễn  Mạnh  Đức , 
				//firstName=Đức, middleName= Mạnh , lastName=Nguyễn, statusId=PARTY_ENABLED, description=null, birthday=1977-01-09},
				for (Map<String, Object> emItem : eventMapValue) {
					iData = [:];
					iData.partyId = emItem.id;
					iData.parentId = emItem.parentId;
					iData.fullName = emItem.fullName;
					iData.level = emItem.level;
					iData["recordChildIds"] = (List<String>) emItem["records"];
					for (String iProdName : productListName) {
						iData[iProdName] = "0";
					}
					listData.add(iData);
				}
				context.listProductKey = productListName;
				context.listData = listData;
			}
		}
	}
}
context.catalogId = catalogId;