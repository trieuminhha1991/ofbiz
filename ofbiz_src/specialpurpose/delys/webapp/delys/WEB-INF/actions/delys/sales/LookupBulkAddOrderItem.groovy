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

import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.product.catalog.CatalogWorker;

shoppingCart = session.getAttribute("shoppingCart");
if (shoppingCart) {
	customerId = shoppingCart.getOrderPartyId();
	if (customerId) {
		mapAllStoreNameByUserLogin = dispatcher.runSync("listAllStoreNameByUserLogin", [userLogin: userLogin]);
		if (mapAllStoreNameByUserLogin.listAllStoreNameByUserLogin) {
			conditionList2 = [];
			conditionList2.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			conditionList2.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
			conditionList2.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "ITEM_APPROVED"));
			conditionList2.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, customerId));
			conditionList2.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, mapAllStoreNameByUserLogin.listAllStoreNameByUserLogin));
			conditions2 = EntityCondition.makeCondition(conditionList2, EntityOperator.AND);
			context.orderItemList = delegator.findList("OrderHeaderItemsPromosAndItemIssuance", conditions2, null, null, null, false);
		}
	}
}