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

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.order.order.OrderListState;
import org.ofbiz.security.Security;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.DelysServices;
partyId = request.getParameter("partyId");
facilityId = request.getParameter("facilityId");

state = OrderListState.getInstanceSecond(request);
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
		
		isFilterProductStoreId = parameters.isFilterProductStoreId;
		
		/*if (parameters.resultService.responseMessage == "success") {
			if (isFilterProductStoreId) {
				//filter by productStoreId only when filter action from orderSearchOption.ftl file
				orderHeaderList = state.getListOrdersAdvance(facilityId, filterDate, delegator, parameters.resultService.listAllStore, parameters.productStoreId);
			} else {
				orderHeaderList = state.getListOrdersAdvance(facilityId, filterDate, delegator, parameters.resultService.listAllStore, null);
			}
		}*/
		
		orderHeaderList = state.getListOrdersSm(delegator, userLogin);
		
		orderListSize = state.getSize();
		context.orderListSize = orderListSize;
		context.listSize = orderListSize;
		context.listSizeDisplay = orderHeaderList.size();
		lowIndex = request.getAttribute("lowIndex");
		context.lowIndex = lowIndex;
		highIndex = request.getAttribute("highIndex");
		context.highIndex = highIndex;
		context.orderHeaderList = orderHeaderList;
		
		if (parameters.productStoreId == null || (parameters.productStoreId).equals("")){
			context.productStoreId = "1";
		} else {
			context.productStoreId = parameters.productStoreId;
			productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", parameters.productStoreId), false);
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
	
