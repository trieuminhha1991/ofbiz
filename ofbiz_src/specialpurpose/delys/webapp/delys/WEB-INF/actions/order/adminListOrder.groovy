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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.order.order.OrderListState;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastMap;

import com.olbius.services.DelysServices;
partyId = request.getParameter("partyId");
facilityId = request.getParameter("facilityId");

state = OrderListState.getInstance(request);
state.update(request);
context.state = state;

// check permission for each order type
hasPermission = false;
if (security.hasPermission("DELYS_ORDER_LIST", session)) {
	
		hasPermission = false;
		context.hasPermission = hasPermission;
		
		if (state.hasType("view_SALES") || (!(state.hasType("view_SALES")) && !(state.hasType("view_PURCHASE")))) {
			hasPermission = true;
			salesOrdersCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES");
		}
		if (state.hasType("view_PURCHASE") || (!(state.hasType("view_SALES")) && !(state.hasType("view_PURCHASE")))) {
			hasPermission = true;
			purchaseOrdersCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE");
		}
		context.hasPermission = hasPermission;
		orderHeaderList = state.getListOrders(facilityId, filterDate, delegator, parameters.listAllStore, null);
		context.orderHeaderList = orderHeaderList;
		List<GenericValue> listFilter = new ArrayList<GenericValue>();
		for (GenericValue value : orderHeaderList){
			OrderReadHelper helper = OrderReadHelper.getHelper(value);
			GenericValue billToParty = helper.getBillToParty();
			GenericValue billFromParty = helper.getBillFromParty();
			Map<String, Object> map = UtilMisc.toMap("partyId", billToParty.partyId, "compareDate", value.orderDate,
				"userLogin", userLogin);
			billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", map);
			String billTo = billToPartyNameResult.fullName;
			String billFrom = PartyHelper.getPartyName(billFromParty);
			if (!parameters.listAllDistributor.isEmpty() && parameters.productStoreId == null){
				for (GenericValue store : parameters.listAllDistributor){
					List<EntityExpr> exprs1 = FastList.newInstance();
					exprs1.add(EntityCondition.makeCondition("roleTypeId",
							EntityOperator.EQUALS,  "MANAGER"));
					exprs1.add(EntityCondition.makeCondition("productStoreId",
							EntityOperator.EQUALS, store.get("productStoreId")));
					List<GenericValue> listManager  = delegator.findList("ProductStoreRole",EntityCondition.makeCondition(exprs1, EntityOperator.AND),
							null, null, null, false);
					if (!listManager.isEmpty()){
						for (GenericValue mgr : listManager) {
							if (mgr.get("partyId").equals(billToParty.partyId)){
								listFilter.add(value);
							}
						}
					}
				}
			} else {
				if (parameters.productStoreId != null){
					List<EntityExpr> exprs1 = FastList.newInstance();
					exprs1.add(EntityCondition.makeCondition("roleTypeId",
							EntityOperator.EQUALS,  "MANAGER"));
					exprs1.add(EntityCondition.makeCondition("productStoreId",
							EntityOperator.EQUALS, parameters.productStoreId));
					List<GenericValue> listManager  = delegator.findList("ProductStoreRole",EntityCondition.makeCondition(exprs1, EntityOperator.AND),
							null, null, null, false);
					if (!listManager.isEmpty()){
						for (GenericValue mgr : listManager) {
							if (mgr.get("partyId").equals(billToParty.partyId)){
								listFilter.add(value);
							}
						}
					}
				}
			}
		}
		context.orderHeaderList = listFilter;
		
		if (parameters.productStoreId == null || (parameters.productStoreId).equals("")){
			context.productStoreId = "1";
		} else {
			context.productStoreId = parameters.productStoreId;
			productStore = delegator.findOne("ProductStore",
								UtilMisc.toMap("productStoreId", parameters.productStoreId), false);
			context.storeName = productStore.get("storeName");
		}
		// a list of order type descriptions
		ordertypes = delegator.findList("OrderType", null, null, null, null, true);
		ordertypes.each { type ->
			context["descr_" + type.orderTypeId] = type.get("description",locale);
		}
		
		context.filterDate = filterDate;
	
} else {
	hasPermission = false;
	context.hasPermission = hasPermission;
}
	
