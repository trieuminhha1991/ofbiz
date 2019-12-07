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

import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
//import org.ofbiz.order.shoppingcart.*;
//import org.ofbiz.party.party.PartyWorker;
//import org.ofbiz.product.catalog.CatalogWorker;
import com.olbius.order.DeliveryRequirementCart;
//import org.ofbiz.order.order.*;
import com.olbius.order.OrderReadHelper;

// Get the Cart and Prepare Size
deliveryCart = DeliveryRequirementCart.getCartObject(request);
context.deliveryCartSize = deliveryCart.size();
context.deliveryCart = deliveryCart;

List<GenericValue> listAllItems = new ArrayList<GenericValue>();
listItemByOrder = [];
cartLines = deliveryCart.items();
for (cartLine in cartLines) {
	orderHeader = null;
	orderId = cartLine.getOrderId();
	if (orderId) {
		orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
	}
	if (orderHeader) {
		orderReadHelper = new OrderReadHelper(orderHeader);
		orderItems = orderReadHelper.getOrderItemsDetails();
		if (orderItems) {
			listAllItems.addAll(orderItems);
			itemmap = [:];
			itemmap.orderId = orderHeader.orderId;
			itemmap.listValue = orderItems;
			listItemByOrder.add(itemmap);
		}
	}
}
context.listAllItems = listAllItems;
context.listItemByOrder = listItemByOrder;

//Sort by Facility
List<String> listFacility = new ArrayList<String>();
for (item in listAllItems) {
	if ((item.facilityId) && !listFacility.contains(item.facilityId)) {
		listFacility.add(item.facilityId);
	}
}

listItemByFacility = [];
//get item by each facility
for (itemFacility in listFacility) {
	itemmap = [:];
	List<GenericValue> listItem = new ArrayList<GenericValue>();
	for (item in listAllItems) {
		if (item.facilityId && item.facilityId == itemFacility) {
			listItem.add(item);
		}
	}
	itemmap.facilityId = itemFacility;
	itemmap.listValue = listItem;
	listItemByFacility.add(itemmap);
}

itemmapNull = [:];
List<GenericValue> listItemNull = new ArrayList<GenericValue>();
for (item in listAllItems) {
	if (item.facilityId == "" || item.facilityId == null) {
		listItemNull.add(item);
	}
}
itemmapNull.facilityId = "";
itemmapNull.listValue = listItemNull;
listItemByFacility.add(itemmapNull);

context.listItemByFacility = listItemByFacility;

