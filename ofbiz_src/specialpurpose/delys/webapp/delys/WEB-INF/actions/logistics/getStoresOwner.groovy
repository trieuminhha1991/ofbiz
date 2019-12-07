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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

java.util.Date date= new java.util.Date();
exprList = [];
exprOrList = [];
expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
exprList.add(expr);
expr = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
exprList.add(expr);
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

listProductStoresParty = delegator.findList("ProductStoreRole", Cond, null, ["partyId"], null, false);
listProductStoresParty = EntityUtil.filterByDate(listProductStoresParty);

List<GenericValue> listProductStores = new ArrayList<GenericValue>();
List<GenericValue> listCatalogs = new ArrayList<GenericValue>();
List<GenericValue> listCategories = new ArrayList<GenericValue>();
if (!listProductStoresParty.isEmpty()){
	for (GenericValue item : listProductStoresParty) {
		GenericValue prStore = null;
		prStoreId = item.get('productStoreId');
		prStore = delegator.findOne("ProductStore", [productStoreId : prStoreId], false);
		listProductStores.add(prStore);
		List<GenericValue> listCatalogTmp = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", prStoreId)), null, null, null, false);
		if (!listCatalogTmp.isEmpty()){
			for (GenericValue catalog : listCatalogTmp){
				GenericValue prodCatalog = delegator.findOne("ProdCatalog", false, UtilMisc.toMap("prodCatalogId", (String)catalog.get("prodCatalogId")));
				if (!listCatalogs.contains(prodCatalog)){
					listCatalogs.add(prodCatalog);
				}
			}
		}
	}
	if (!listCatalogs.isEmpty()){
		for (GenericValue catalog : listCatalogs) {
			List<GenericValue> listCategoryTmp = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", (String)catalog.get("prodCatalogId"))), null, null, null, false);
			if (!listCategoryTmp.isEmpty()){
				for (GenericValue item : listCategoryTmp){
					List<GenericValue> listCategoryChild = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("primaryParentCategoryId", (String)item.get("productCategoryId"))), null, null, null, false);
					GenericValue category = delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId", (String)item.get("productCategoryId")));
					if (category != null){
						if (!listCategories.contains(category)){
							listCategories.add(category);
						}
					}
					if (!listCategoryChild.isEmpty()){
						for (GenericValue categoryTmp : listCategoryChild){
							if (!listCategories.contains(categoryTmp)){
								listCategories.add(categoryTmp);
							}
						}
					}
				}
			}
		}
	}
	
} else {
	context.NoPermission = "Y";
}
context.listCatalogs = listCatalogs;
context.listCategories = listCategories;
if (!listProductStores.isEmpty()){
	context.listProductStores = listProductStores;
}