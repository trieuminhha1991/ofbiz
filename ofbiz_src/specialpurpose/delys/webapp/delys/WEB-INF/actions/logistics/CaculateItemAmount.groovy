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
taxPercentage = 0;
GenericValue OrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
totalAmount = OrderItem.get("quantity") * OrderItem.get("unitPrice");

GenericValue OrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
currencyUomId = OrderHeader.get("currencyUom");

exprList = [];
expr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, OrderItem.get("productId"));
exprList.add(expr);
expr = EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "DEFAULT_PRICE");
exprList.add(expr);
expr = EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "PURCHASE");
exprList.add(expr);
expr = EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId);
exprList.add(expr);
expr = EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, "_NA_");
exprList.add(expr);

expr = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, OrderHeader.get("orderDate"));
exprList.add(expr);

Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
orderBy = [];
orderBy.add("-fromDate");
listProductPrice = delegator.findList("ProductPrice", Cond, null, orderBy, null, false);
listProductPrice = EntityUtil.filterByDate(listProductPrice);

if (!listProductPrice.isEmpty()){
	ProductPrice = listProductPrice.get(0);
	diffPrice = ProductPrice.get("price") - OrderItem.get("unitPrice");
	if (ProductPrice.get("taxPercentage") != null){
		unitPriceVAT = OrderItem.get("unitPrice") * (1-ProductPrice.get("taxPercentage")/100);
		totalPriceVAT = unitPriceVAT * OrderItem.get("quantity");
		taxPercentage = ProductPrice.get("taxPercentage");
	} else {
		unitPriceVAT = OrderItem.get("unitPrice");
		totalPriceVAT = totalAmount;
	}
} else {
	diffPrice = 0;
	totalPriceVAT = totalAmount;
	unitPriceVAT = OrderItem.get("unitPrice");
}

if (!OrderItem.isEmpty()){
	context.OrderItem = OrderItem;
	context.totalAmount = totalAmount;
	context.diffPrice = diffPrice;
	context.currencyUomId = currencyUomId;
	context.unitPriceVAT = unitPriceVAT;
	context.taxPercentage = taxPercentage;
	context.totalPriceVAT = totalPriceVAT;
} else {
}