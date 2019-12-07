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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.order.order.OrderListState;
import org.ofbiz.security.Security;

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
	        salesOrdersCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER");
	    }
	    if (state.hasType("view_PURCHASE") || (!(state.hasType("view_SALES")) && !(state.hasType("view_PURCHASE")))) {
	        hasPermission = true;
	        purchaseOrdersCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER");
	    }
		context.hasPermission = hasPermission;
		
		// set the page parameters
		viewIndex = request.getParameter("viewIndex") ? Integer.valueOf(request.getParameter("viewIndex")) : 0;
		viewSize = 1;//request.getParameter("viewSize") ? Integer.valueOf(request.getParameter("viewSize")) : UtilProperties.getPropertyValue("widget", "widget.form.defaultViewSize");
		context.viewIndex = viewIndex;
		context.viewSize = viewSize;
		// get the lookup flag
		lookupFlag = request.getParameter("lookupFlag");
		// fields from the service call
		paramList = request.getAttribute("paramList") ?: "";
		context.paramList = paramList;
		if (paramList) {
			paramIds = paramList.split("&amp;");
			context.paramIdList = Arrays.asList(paramIds);
		}
		
		/*
		 * Old Code
		 */
		isFilterProductStoreId = parameters.isFilterProductStoreId;
		
		if (parameters.resultService.responseMessage == "success") {
			if (isFilterProductStoreId) {
				//filter by productStoreId only when filter action from orderSearchOption.ftl file
				orderHeaderList = state.getListOrdersAdvance(facilityId, filterDate, delegator, parameters.resultService.listAllStore, parameters.productStoreId);
			} else {
				orderHeaderList = state.getListOrdersAdvance(facilityId, filterDate, delegator, parameters.resultService.listAllStore, null);
			}
		}
		
		orderListSize = state.getSize();
		context.orderListSize = orderListSize;
		context.listSize = orderListSize;
		context.listSizeDisplay = orderHeaderList.size();
		lowIndex = request.getAttribute("lowIndex");
		context.lowIndex = lowIndex;
		highIndex = request.getAttribute("highIndex");
		context.highIndex = highIndex;
		
		context.orderHeaderList = orderHeaderList;
		
		/*
		 * Start New Code: filter order list by userLogin
		 */
//		orderList = request.getAttribute("orderList");
//		context.orderList = orderList;
//		orderListSize = request.getAttribute("orderListSize");
//		context.orderListSize = orderListSize;
		/*lowIndex = request.getAttribute("lowIndex");
		context.lowIndex = lowIndex;
		highIndex = request.getAttribute("highIndex");
		context.highIndex = highIndex;
		
		isFilterProductStoreId = parameters.isFilterProductStoreId;
		if (isFilterProductStoreId) {
			//filter by productStoreId only when filter action from orderSearchOption.ftl file
			orderHeaderList = state.getListOrders(facilityId, filterDate, delegator, parameters.listAllStore, parameters.productStoreId);
		} else {
			orderHeaderList = state.getListOrders(facilityId, filterDate, delegator, parameters.listAllStore, null);
		}
		boolean isChecked = false;
		int countRemove = 0;
		List<GenericValue> orderHeaderListGet = new ArrayList<GenericValue>();
		if (requestNameScreen && (userLogin.partyId != null)) {
			if ("purcharseOrderListDis".equals(requestNameScreen)) {
				isChecked = false;
				for (orderHeaderItem in orderHeaderList) {
					orderRoleItem = delegator.findOne("OrderRole", ["orderId" : orderHeaderItem.orderId, "partyId" : userLogin.partyId, "roleTypeId" : "PLACING_CUSTOMER"], false);
					if (orderRoleItem) {
						orderHeaderListGet.add(orderHeaderItem);
					} else {
						countRemove++;
					}
				}
			} else if ("salesOrderListDis".equals(requestNameScreen)) {
				isChecked = false;
				// filter the issuances
				Map<String, Object> filter = UtilMisc.toMap("createdBy", userLogin.userLoginId);
				orderHeaderList = EntityUtil.filterByAnd(orderHeaderList, filter);
				for (orderHeaderItem in orderHeaderList) {
					partyCreate = delegator.findOne("UserLogin", ["userLoginId" : orderHeaderItem.getString("createdBy")], false);
					if (partyCreate && partyCreate.getString("partyId") != null) {
						orderRoleItem = delegator.findOne("PartyRelationship", ["partyIdFrom" : userLogin.partyId, "partyIdTo" : partyCreate.getString("partyId"), "roleTypeIdFrom" : "DELYS_DISTRIBUTOR", "roleTypeIdTo" : "DELYS_SALESMAN_GT"], false);
						if (orderRoleItem) {
							orderHeaderListGet.add(orderHeaderItem);
						} else {
							countRemove++;
						}
					}
				}
			}
		} else {
			// isChecked = true;
			isChecked = false;
			for (orderHeaderItem in orderHeaderList) {
				orderRoleList = delegator.findByAnd("OrderRole", ["orderId" : orderHeaderItem.orderId, "roleTypeId" : "PLACING_CUSTOMER"], null, false);
				if (orderRoleList) {
					orderRoleItem = orderRoleList.get(0);
					partyRoleItem = delegator.findOne("PartyRole", ["partyId": orderRoleItem.getString("partyId"), "roleTypeId": "DELYS_DISTRIBUTOR"], false);
					if (partyRoleItem) {
						orderHeaderListGet.add(orderHeaderItem);
					} else {
						countRemove++;
					}
				} else {
					countRemove++;
				}
			}
		}
		if (isChecked) {
			orderListSize = state.getSize();
			context.orderListSize = orderListSize;
			context.listSize = orderListSize;
			context.listSizeDisplay = orderHeaderList.size();
			context.orderHeaderList = orderHeaderList;
		} else {
			orderListSize = state.getSize() - countRemove;
			context.orderListSize = orderListSize;
			context.listSize = orderListSize;
			context.listSizeDisplay = orderHeaderListGet.size();
			context.orderHeaderList = orderHeaderListGet;
		}*/
		/*
		 * End New Code
		 */
		
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
	
