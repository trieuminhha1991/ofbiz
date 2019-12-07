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

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import com.olbius.order.OrderReadHelper;
import net.sf.json.JSONObject;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

import javolution.util.FastMap;

//import org.ofbiz.order.order.*;

orderId = parameters.orderId;
context.orderId = orderId;

println "VIETTB" + orderId;
orderHeader = null;
orderItems = null;
orderAdjustments = null;

if (orderId) {
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
}

if (orderHeader) {
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	//orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	//Edit by ViettB
	orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments();
	orderSubTotal = orderReadHelper.getNewOrderItemsSubTotal();
	context.orderSubTotal = orderSubTotal;
	println "VIETTB" + orderSubTotal;
	context.currencyUomId = orderReadHelper.getCurrency();

	// get the order type
	orderType = orderHeader.orderTypeId;
	context.orderType = orderType;

	otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);
	context.otherAdjAmount = otherAdjAmount;

	shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
	shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
	context.shippingAmount = shippingAmount;

	taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	context.taxAmount = taxAmount;

	//grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	grandTotal = OrderReadHelper.getNewOrderGrandTotal(orderItems, orderReadHelper.getNewAdjustments());	
	context.grandTotal = grandTotal;
	avi = grandTotal / 3;
	avi = avi.setScale(decimals,rounding);
	avo = grandTotal - avi;
	context.avo = avo;
	context.avi = avi;
}
