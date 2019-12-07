/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastMap;
import javolution.util.FastList;
// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

List separateRootType(roots) {
	if(roots) {
		prodRootTypeTree = [];
		roots.each { root ->
			prodCateMap = [:];
			productCategory = root.getRelatedOne("ProductCategory", false);
			prodCateMap.productCategoryId = productCategory.getString("productCategoryId");
			prodCateMap.categoryName = productCategory.getString("categoryName");
			prodCateMap.isCatalog = false;
			prodCateMap.isCategoryType = true;
			prodRootTypeTree.add(prodCateMap);
		}
		return prodRootTypeTree;
	}
}

completedTree =  [];

// Get the Catalogs
List<GenericValue> listProduct = new ArrayList<GenericValue>();
List<GenericValue> listCatalog = new ArrayList<GenericValue>();
List<String> listProductStr = new ArrayList<String>();
String partyId = userLogin.getString("partyId");
if (UtilValidate.isNotEmpty(partyId)) {
	List<GenericValue> listProductStoreOwner = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "OWNER"), null, false);
	List<GenericValue> listProductStoreCatalog = new ArrayList<GenericValue>();
	List<GenericValue> listProductStoreCategoryProduct = new ArrayList<GenericValue>();
	if (listProductStoreOwner != null) {
		for (GenericValue storeItem : listProductStoreOwner) {
			List<GenericValue> listCatalogTemp = delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId", storeItem.getString("productStoreId")), null, false);
			if (listCatalogTemp != null) {
				listProductStoreCatalog.addAll(listCatalogTemp);
			}
		}
		for (GenericValue catalogItem : listProductStoreCatalog) {
			GenericValue catalogLog = delegator.findOne("ProdCatalog", UtilMisc.toMap("prodCatalogId", catalogItem.getString("prodCatalogId")), false);
			listCatalog.add(catalogLog);
			List<GenericValue> listCategoryProductTemp = delegator.findByAnd("ProdCatalogCategoryAndProduct", UtilMisc.toMap("prodCatalogId", catalogItem.getString("prodCatalogId")), null, false);
			listProductStoreCategoryProduct.addAll(listCategoryProductTemp);
		}
		for (GenericValue categoryProductItem : listProductStoreCategoryProduct) {
			/*if (!listCategoryStr.contains(categoryProductItem.getString("productCategoryId"))) {
				listCategoryStr.add(categoryProductItem.getString("productCategoryId"));
			}*/
			if (!listProductStr.contains(categoryProductItem.getString("productId"))) {
				listProductStr.add(categoryProductItem.getString("productId"));
			}
		}
		/*for (String categoryStrItem : listCategoryStr) {
			GenericValue categoryTemp = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryStrItem), false);
			listCategory.add(categoryTemp);
		}*/
		for (String productStrItem : listProductStr) {
			GenericValue productTemp = delegator.findOne("Product", UtilMisc.toMap("productId", productStrItem), false);
			listProduct.add(productTemp);
		}
	}
}
context.listProduct = listProduct;

prodCatalogs = listCatalog;//delegator.findList("ProdCatalog", null, null, null, null, false);
if (prodCatalogs) {
	prodCatalogs.each { prodCatalog ->
		prodCatalogMap = [:];
		prodCatalogMap.productCategoryId = prodCatalog.getString("prodCatalogId");
		prodCatalogMap.categoryName = prodCatalog.getString("catalogName");
		prodCatalogMap.isCatalog = true;
		prodCatalogMap.isCategoryType = false;
		prodCatalogCategories = EntityUtil.filterByDate(delegator.findByAnd("ProdCatalogCategory", ["prodCatalogId" : prodCatalog.prodCatalogId], null, false));
		if (prodCatalogCategories) {
			prodCatalogMap.child = separateRootType(prodCatalogCategories);
		}
		completedTree.add(prodCatalogMap);
	}
}
// The complete tree list for the category tree
context.completedTree = completedTree;

stillInCatalogManager = true;
productCategoryId = null;
prodCatalogId = null;
showProductCategoryId = null;

// Reset tree condition check. Are we still in the Catalog Manager ?. If not , then reset the tree.
if ((parameters.productCategoryId != null) || (parameters.showProductCategoryId != null)) {
	stillInCatalogManager = false;
	productCategoryId = parameters.productCategoryId;
	showProductCategoryId = parameters.showProductCategoryId;
} else if (parameters.prodCatalogId != null) {
	stillInCatalogManager = false;
	prodCatalogId = parameters.prodCatalogId;
}
context.stillInCatalogManager = stillInCatalogManager;
context.productCategoryId = productCategoryId;
context.prodCatalogId = prodCatalogId;
context.showProductCategoryId = showProductCategoryId;

