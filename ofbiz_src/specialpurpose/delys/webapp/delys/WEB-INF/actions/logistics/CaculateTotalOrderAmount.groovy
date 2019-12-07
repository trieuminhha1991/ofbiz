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

exprList = [];
expr = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
exprList.add(expr);
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

listItems = delegator.findList("DeliveryItem", Cond, null, null, null, false);
orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
totalOrderAmount = 0;
totalPriceVAT = 0;
if (!listItems.isEmpty()){
	for (GenericValue item : listItems) {
		orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", (String)item.get("fromOrderId"), "orderItemSeqId", (String)item.get("fromOrderItemSeqId")));
		totalOrderAmount = totalOrderAmount + orderItem.get("quantity") * orderItem.get("unitPrice");
		itemAmount = orderItem.get("quantity") * orderItem.get("unitPrice");
		currencyUomId = orderHeader.get("currencyUom");
		
		exprList = [];
		expr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, orderItem.get("productId"));
		exprList.add(expr);
		expr = EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "DEFAULT_PRICE");
		exprList.add(expr);
		expr = EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "PURCHASE");
		exprList.add(expr);
		expr = EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId);
		exprList.add(expr);
		expr = EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, "_NA_");
		exprList.add(expr);
		
		expr = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, orderHeader.get("orderDate"));
		exprList.add(expr);
		
		Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		orderBy = [];
		orderBy.add("-fromDate");
		listProductPrice = delegator.findList("ProductPrice", Cond, null, orderBy, null, false);
		listProductPrice = EntityUtil.filterByDate(listProductPrice);
		
		if (!listProductPrice.isEmpty()){
			ProductPrice = listProductPrice.get(0);
			diffPrice = ProductPrice.get("price") - orderItem.get("unitPrice");
			if (ProductPrice.get("taxPercentage") != null){
				unitPriceVAT = orderItem.get("unitPrice") * (1-ProductPrice.get("taxPercentage")/100);
				totalPriceVAT = unitPriceVAT * orderItem.get("quantity");
			} else {
				unitPriceVAT = orderItem.get("unitPrice");
				totalPriceVAT = totalPriceVAT + itemAmount;
			}
		} else {
			diffPrice = 0;
			totalPriceVAT = itemAmount;
			unitPriceVAT = orderItem.get("unitPrice");
		}
	}
} else {
	context.NoPermission = "Y";
}
context.totalOrderAmount = totalOrderAmount;
context.totalPriceVAT = totalPriceVAT;
if (orderHeader != null){
	context.currencyUomId = (String)orderHeader.get("currencyUom");
} else {
	context.currencyUomId = null;
}

