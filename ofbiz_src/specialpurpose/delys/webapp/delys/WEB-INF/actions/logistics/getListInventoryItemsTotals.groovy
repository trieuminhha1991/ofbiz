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

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

java.util.Date date= new java.util.Date();
exprList = [];
List<GenericValue> listOrders = new ArrayList<GenericValue>();
for (GenericValue store : listProductStores) {
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_APPROVED")));
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", store.get("productStoreId")))));
	Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	
	List<GenericValue> listOrderTmp = delegator.findList("OrderHeader", Cond, null, null, null, false);
	if (!listOrderTmp.isEmpty()){
		listOrders.addAll(listOrderTmp);
	}
}
List<GenericValue> listOrderItems = new ArrayList<GenericValue>();
if (!listOrders.isEmpty()){
	for (GenericValue order : listOrders){
		List<GenericValue> listProductOrderTmp = new ArrayList<GenericValue>();
		listProductOrderTmp = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", order.get("orderId"), "productId", productId)), null, null, null, false);
		if (!listProductOrderTmp.isEmpty()){
			listOrderItems.addAll(listProductOrderTmp);
		}
	}
}

quantityOnOrdered = BigDecimal.ZERO;
if (!listOrderItems.isEmpty()){
	for (GenericValue item : listOrderItems){
		if (item.getBigDecimal("quantity")){
			if (item.getBigDecimal("cancelQuantity") != null){
				quantityOnOrdered = quantityOnOrdered.add(item.getBigDecimal("quantity").subtract(item.getBigDecimal("cancelQuantity")));
			} else {
				quantityOnOrdered = quantityOnOrdered.add(item.getBigDecimal("quantity"));
			}
		}
	}
}
context.quantityOnOrdered = quantityOnOrdered;