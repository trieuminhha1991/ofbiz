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

prodCatalogId = parameters.prodCatalogId;
productCategoryId = parameters.prodCategoryId;

List<GenericValue> listProducts = new ArrayList<GenericValue>();
if (prodCatalogId == null || "".equals(prodCatalogId)){
	if (productCategoryId == null || "".equals(prodCatalogId)){
		if (listCategories != null){
			for (GenericValue category : listCategories){
				exprList = [];
				expr = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, (String)category.get("productCategoryId"));
				exprList.add(expr);
				Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
				List<GenericValue> listProductTmp = new ArrayList<GenericValue>();
				listProductTmp = delegator.findList("ProductCategoryMember", Cond, null, null, null, false);
				listProductTmp = EntityUtil.filterByDate(listProductTmp);
				if (!listProductTmp.isEmpty()){
					for (GenericValue prodCategory : listProductTmp){
						GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)prodCategory.get("productId")));
						if (product != null){
							if (!listProducts.contains(product)){
								listProducts.add(product);
							}
						}
					}
				}
			}
		}
	}
} else {
	exprList = [];
	expr = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId);
	exprList.add(expr);
	Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	listProductsTmp = delegator.findList("ProductCategoryMember", Cond, null, null, null, false);
	listProductsTmp = EntityUtil.filterByDate(listProductsTmp);
	if (!listProductsTmp.isEmpty()){
		for (GenericValue prodCategory : listProductsTmp){
			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)prodCategory.get("productId")));
			if (product != null){
				if (!listProducts.contains(product)){
					listProducts.add(product);
				}
			}
		}
	}
}
context.listProducts = listProducts;
