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

import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

exprList = [];
List<Map<String, Object>> listInventoryItemTotals = new ArrayList<GenericValue>();
List<GenericValue> listProductId = new ArrayList<GenericValue>();
if (listInventoryItem){
	for (GenericValue item : listInventoryItem) {
		if (!listProductId.contains(item.get("productId"))){
			listProductId.add(item.get("productId"));
		}
	}
	if (listProductId){
		for (String idPr : listProductId){
			BigDecimal quantityOnOrder = BigDecimal.ZERO;
			BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
			BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
			BigDecimal accountingQuantityTotal = BigDecimal.ZERO;
			for (GenericValue item : listInventoryItem) {
				if (idPr.equals(item.get("productId"))){
					if (item.getBigDecimal("quantityOnHandTotal")){
						quantityOnHandTotal = quantityOnHandTotal.add(item.getBigDecimal("quantityOnHandTotal"));
					}
					if (item.getBigDecimal("availableToPromiseTotal")){
						availableToPromiseTotal = availableToPromiseTotal.add(item.getBigDecimal("availableToPromiseTotal"));
					}
					if (item.getBigDecimal("accountingQuantityTotal")){
						accountingQuantityTotal = accountingQuantityTotal.add(item.getBigDecimal("accountingQuantityTotal"));
					}
				}
			}
			product = delegator.findOne("Product", ["productId" : idPr], false);
			productContentWrapper = ProductContentWrapper.makeProductContentWrapper(product, request);
			productName = product.getString("PRODUCT_NAME");
			if (!productName) {
				productName = "";
			}
			Map<String, Object> newPartyMap = UtilMisc.toMap("productId", idPr, "quantityOnHandTotal", quantityOnHandTotal, "availableToPromiseTotal", availableToPromiseTotal, "accountingQuantityTotal", accountingQuantityTotal, "internalName", productName);
			
			expr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, idPr);
			exprList.add(expr);
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_APPROVED")));
			Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			List<GenericValue> listProductOrder = new ArrayList<GenericValue>();
			listProductOrder = delegator.findList("OrderItem", Cond, null, null, null, false);
			
			if (!listProductOrder.isEmpty()){
				for (GenericValue order : listProductOrder){
					quantityOnOrder =quantityOnOrder.add(order.getBigDecimal("quantity"));
				}
			}
			//newPartyMap.put("quantityOnOrder", quantityOnOrder);
			//GenericValue inventoryItem = delegator.makeValue("ProductNameByInventoryItemTotal", newPartyMap);
			listInventoryItemTotals.add(newPartyMap);
		}
	}
}

if (listFacilities){
	context.listInventoryItemTotals = listInventoryItemTotals;
}