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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.order.order.OrderReadHelper;
import java.math.BigDecimal;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;
returnId = parameters.returnId;

if (!returnId) return;
context.returnId = returnId;

orderId = parameters.orderId;
context.orderId = orderId;

returnHeader = delegator.findOne("ReturnHeader", [returnId : returnId], false);
context.returnHeader = returnHeader;
returnHeaderTypeId = returnHeader.returnHeaderTypeId;
context.toPartyId = returnHeader.toPartyId;

returnItems = delegator.findByAnd("ReturnItem", [returnId : returnId], null, false);
context.returnItems = returnItems;

// these are just the adjustments not associated directly with a return item--the rest are gotten with a .getRelated on the returnItems in the .FTL
returnAdjustments = delegator.findByAnd("ReturnAdjustment", [returnId : returnId, returnItemSeqId : "_NA_"], ["returnItemSeqId", "returnAdjustmentTypeId"], false);
context.returnAdjustments = returnAdjustments;

returnTypes = delegator.findList("ReturnType", null, null, ["sequenceId"], null, false);
context.returnTypes = returnTypes;

itemStatus = delegator.findByAnd("StatusItem", [statusTypeId : "INV_SERIALIZED_STTS"], ["statusId", "description"], false);
context.itemStatus = itemStatus;

returnReasons = delegator.findList("ReturnReason", null, null, ["sequenceId"], null, false);
context.returnReasons = returnReasons;

listReturnReasons = delegator.findList("ReturnReason", null, null, null, null, false);
context.listReturnReasons = listReturnReasons;

itemStts = delegator.findByAnd("StatusItem", [statusTypeId : "INV_SERIALIZED_STTS"], ["sequenceId"], false);
context.itemStts = itemStts;

returnItemTypeMap = delegator.findByAnd("ReturnItemTypeMap", [returnHeaderTypeId : returnHeaderTypeId], null, false);
typeMap = [:];
returnItemTypeMap.each { value ->
    typeMap[value.returnItemMapKey] = value.returnItemTypeId;
}
context.returnItemTypeMap = typeMap;

if (orderId) {
    order = delegator.findOne("OrderHeader", [orderId : orderId], false);
    returnRes = dispatcher.runSync("getReturnableItems", [orderId : orderId]);
    context.returnableItems = returnRes.returnableItems;

    orh = new OrderReadHelper(order);
    context.orh = orh;
    context.orderHeaderAdjustments = orh.getAvailableOrderHeaderAdjustments();

    // get the order shipping amount
    shipRes = dispatcher.runSync("getOrderShippingAmount", [orderId : orderId]);
    shippingAmount = shipRes.shippingAmount;
    context.shippingAmount = shippingAmount;
}
roleTypeId = "PLACING_CUSTOMER";
partyId = returnHeader.fromPartyId;
if (returnHeaderTypeId == "VENDOR_RETURN") {
    roleTypeId = "BILL_FROM_VENDOR";
    partyId = returnHeader.toPartyId;
}
partyOrders = delegator.findByAnd("OrderHeaderAndRoles", [roleTypeId : roleTypeId, partyId : partyId], ["orderId", "orderDate"], false);
context.partyOrders = partyOrders;
context.partyId = partyId;

listPartyComReturn = new ArrayList<GenericValue>();
List<GenericValue> partyCompleteOrder = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
for(GenericValue listCom : partyCompleteOrder){
	GenericValue orderRoleCheckOrg = delegator.findOne("OrderRole", UtilMisc.toMap("orderId", listCom.getString("orderId"), "partyId", returnHeader.fromPartyId, "roleTypeId", "BILL_TO_CUSTOMER"), false);
	if(orderRoleCheckOrg != null){
		listPartyComReturn.add(listCom);
	} 
}

context.partyCompleteOrder = listPartyComReturn;

// get the list of return shipments associated to the return
findOptions = new EntityFindOptions();
findOptions.setDistinct(true);
returnShipmentIds = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition("returnId", returnId), ["shipmentId"] as Set, null, findOptions, true);
context.returnShipmentIds = returnShipmentIds;
