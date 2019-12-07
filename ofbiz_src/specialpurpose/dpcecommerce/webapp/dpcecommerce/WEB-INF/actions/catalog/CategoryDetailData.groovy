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
 * NOTE: This script is also referenced by the webpos and ecommerce's screens and
 * should not contain order component's specific code.
 */

import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastSet;
import com.olbius.ecommerce.*;

productCategoryId = request.getAttribute("productCategoryId");


public void returnEmpty(Map<String, Object> context, int viewIndex, int viewSize, productCategoryMembers, listSize){
	context.listSize = listSize;
	context.productCategoryMembers = productCategoryMembers;
	context.viewIndex = viewIndex;
	context.viewSize = viewSize;
	context.paginationSize = ConfigUtils.CATEGORY_DETAIL_PAGINATION_SIZE;
	context.link = "categorydetail?productCategoryId="+ productCategoryId;
}

orderBy = parameters.orb;
orderField = null;
if(UtilValidate.isNotEmpty(orderBy)){
	if(orderBy.equals("PRAS")){
		orderField = "+price";
	}else if(orderBy.equals("PRDS")){
		orderField = "-price";
	}else if(orderBy.equals("NDS")){
		orderField = "-productName";
	}else if(orderBy.equals("NAS")){
		orderField = "-productName";
	}else{
		orderField = "+productName";
	}
}else{
	orderField = "-fromDate";
}
List<EntityCondition> listAllFilterCondition = FastList.newInstance();
genderFilter = parameters.genderFilter;
if(UtilValidate.isNotEmpty(genderFilter)){
	listAllFilterCondition.add(EntityCondition.makeCondition("productCategoryId", genderFilter));
}
brandFilter = parameters.brandFilter;
if(UtilValidate.isNotEmpty(brandFilter)){
	listAllFilterCondition.add(EntityCondition.makeCondition("brandName", brandFilter));
}
originFilter = parameters.originFilter;
if(UtilValidate.isNotEmpty(originFilter)){
	listAllFilterCondition.add(EntityCondition.makeCondition("originGeoId", originFilter));
}

List<EntityCondition> listAllConditions = FastList.newInstance();

if(UtilValidate.isNotEmpty(listAllFilterCondition)){
	EntityFindOptions opProduct = new EntityFindOptions();
	opProduct.setDistinct(true);
	listProductFilter = delegator.findList("ProductCategorySupplier", EntityCondition.makeCondition(listAllFilterCondition), UtilMisc.toSet("productId"), UtilMisc.toList("+productId"), opProduct, false);
	listTmp = FastList.newInstance();
	for(GenericValue e : listProductFilter){
		listTmp.add(e.getString("productId"));
	}
	listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listTmp));
}

context.productCategoryId = productCategoryId;

session = request.getSession();
viewSize = parameters.VIEW_SIZE;
viewIndex = UtilValidate.isNotEmpty(parameters.VIEW_INDEX) ? Integer.parseInt(parameters.VIEW_INDEX) : 0;

currentCatalogId = CatalogWorker.getCurrentCatalogId(request);

listSortFields = UtilMisc.toList(orderField);

EntityFindOptions opts = new EntityFindOptions();
opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);

feature =  productCategoryId + "_TOP";
condCatalog = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("prodCatalogId", currentCatalogId),
EntityCondition.makeCondition("productCategoryId", productCategoryId),
EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

listCat = delegator.findList("ProdCatalogCategory", condCatalog, null, null, null, false);

if(UtilValidate.isNotEmpty(listCat)){
	List<EntityCondition> cats = UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", feature),
									EntityCondition.makeCondition("productCategoryId", productCategoryId));
	listAllConditions.add(EntityCondition.makeCondition(cats, EntityOperator.OR));
	listAllConditions.add(EntityUtil.getFilterByDateExpr());

	EntityListIterator list = null;
	i = -1;
	start = viewIndex * viewSize;
	end = start + viewSize;
	list = delegator.find("ProductCategoryMemberAndPriceFilter", EntityCondition.makeCondition(listAllConditions), null,
							UtilMisc.toSet("productCategoryId", "productId"), listSortFields, opts);
	maxSize = list.getResultsTotalSize();
	if(start < maxSize){
		GenericValue product = null;
		List<GenericValue> res = FastList.newInstance();
		Set<String> tmProduct = FastSet.newInstance();
		while((product = list.next()) != null){
			i++;
			if(product.getString("productCategoryId").equals(feature)){
				i++;
			}
			if(i >= end){
				break;
			}

			if(i < start){
				continue;
			}else{
				res.add(product);
			}
		}
		list.close();

		listAllConditions = UtilMisc.toList(EntityUtil.getFilterByDateExpr(),
											EntityCondition.makeCondition("productCategoryId", productCategoryId));
		list = delegator.find("ProductCategoryMemberAndPriceFilter", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		totalInMain = list.getResultsTotalSize();
		list.close();

		listAllConditions = UtilMisc.toList(EntityUtil.getFilterByDateExpr(),
			EntityCondition.makeCondition("productCategoryId", feature));
		list = delegator.find("ProductCategoryMemberAndPriceFilter", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		totalTop = list.getResultsTotalSize();
		list.close();

		listSize = totalTop * 2 + totalInMain;

		returnEmpty(context, viewIndex, viewSize, res, listSize);
	}else{
		returnEmpty(context, viewIndex, viewSize, [], 0);
	}

}else{
	returnEmpty(context, viewIndex, viewSize, [], 0);
}
