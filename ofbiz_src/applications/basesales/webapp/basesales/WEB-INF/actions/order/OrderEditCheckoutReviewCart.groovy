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

import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.product.store.*;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.website.WebSiteWorker;

import javolution.util.FastMap;
import javolution.util.FastList;

import java.text.SimpleDateFormat;
import com.olbius.basesales.order.OrderWorker;

long nextItemSeq = 1;
public List<Map<String, Object>> makeOrderItems(ShoppingCart cart, boolean explodeItems, boolean replaceAggregatedId, long nextItemSeq) {
    // do the explosion
    if (explodeItems && dispatcher != null) {
        explodeItems(dispatcher);
    }
	
	cartLines = cart.items();

    // now build the lines
    synchronized (cartLines) {
        List<Map<String, Object>> result = FastList.newInstance();
        //Timestamp nowStamp = UtilDateTime.nowTimestamp();
        for (ShoppingCartItem item : cartLines) {
        	int cartLineIndex = cart.getItemIndex(item);
        	
            /*if (UtilValidate.isEmpty(item.getOrderItemSeqId())) {
                String orderItemSeqId = UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
                item.setOrderItemSeqId(orderItemSeqId);
                nextItemSeq++;
            } else {
                try {
                    int thisSeqId = Integer.parseInt(item.getOrderItemSeqId());
                    if (thisSeqId > nextItemSeq) {
                        nextItemSeq = thisSeqId + 1;
                    }
                } catch (NumberFormatException e) {
                    Debug.logError(e, module);
                }
            }*/
            
            // the initial status for all item types
            String initialStatus = "ITEM_CREATED";
            String status = item.getStatusId();
            if (status == null) {
                status = initialStatus;
            }
            
            //check for aggregated products
            String aggregatedInstanceId = null;
            if (replaceAggregatedId && UtilValidate.isNotEmpty(item.getConfigWrapper())) {
                aggregatedInstanceId = getAggregatedInstanceId(item, dispatcher);
            }

            GenericValue orderItem = delegator.makeValue("OrderItem");
            orderItem.put("orderItemSeqId", item.getOrderItemSeqId());
            orderItem.put("externalId", item.getExternalId());
            orderItem.put("orderItemTypeId", item.getItemType());
            if (item.getItemGroup() != null) orderItem.put("orderItemGroupSeqId", item.getItemGroup().getGroupNumber());
            orderItem.put("productId", UtilValidate.isNotEmpty(aggregatedInstanceId) ? aggregatedInstanceId : item.getProductId());
            orderItem.put("supplierProductId", item.getSupplierProductId());
            orderItem.put("prodCatalogId", item.getProdCatalogId());
            orderItem.put("productCategoryId", item.getProductCategoryId());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("selectedAmount", item.getSelectedAmount());
            orderItem.put("unitPrice", item.getBasePrice());
            orderItem.put("unitListPrice", item.getListPrice());
            orderItem.put("isModifiedPrice",item.getIsModifiedPrice() ? "Y" : "N");
            orderItem.put("isPromo", item.getIsPromo() ? "Y" : "N");

            orderItem.put("shoppingListId", item.getShoppingListId());
            orderItem.put("shoppingListItemSeqId", item.getShoppingListItemSeqId());

            orderItem.put("itemDescription", item.getName());
            orderItem.put("comments", item.getItemComment());
            orderItem.put("estimatedDeliveryDate", item.getDesiredDeliveryDate());
            orderItem.put("correspondingPoId", cart.getPoNumber());
            orderItem.put("quoteId", item.getQuoteId());
            orderItem.put("quoteItemSeqId", item.getQuoteItemSeqId());
            orderItem.put("statusId", status);

            orderItem.put("shipBeforeDate", item.getShipBeforeDate());
            orderItem.put("shipAfterDate", item.getShipAfterDate());
            orderItem.put("estimatedShipDate", item.getEstimatedShipDate());
            orderItem.put("cancelBackOrderDate", item.getCancelBackOrderDate());
            if (cart.getUserLogin() != null) {
                orderItem.put("changeByUserLoginId", cart.getUserLogin().get("userLoginId"));
            }

            String fromInventoryItemId = (String) item.getAttribute("fromInventoryItemId");
            if (fromInventoryItemId != null) {
                orderItem.put("fromInventoryItemId", fromInventoryItemId);
            }
            
            // TODOCHANGE add new attribute: "quantityUomId", "alternativeQuantity", "alternativeUnitPrice", "expireDate"
            String quantityUomId = (String) item.getAttribute("quantityUomId");
            if (quantityUomId != null) {
                orderItem.put("quantityUomId", quantityUomId);
            }
            BigDecimal alternativeQuantity = (BigDecimal) item.getAttribute("alternativeQuantity");
            if (alternativeQuantity != null) {
            	orderItem.put("alternativeQuantity", alternativeQuantity);
            }
            //BigDecimal alternativeUnitPrice = (BigDecimal) item.getAttribute("alternativeUnitPrice");
            BigDecimal alternativeUnitPrice = item.getAlternativeUnitPrice();
            if (alternativeUnitPrice != null) {
            	orderItem.put("alternativeUnitPrice", alternativeUnitPrice);
            }
            Timestamp expireDate = (Timestamp) item.getAttribute("expireDate");
            if (expireDate != null) {
            	orderItem.put("expireDate", expireDate);
            }
            
            Map<String, Object> orderItemMap = FastMap.newInstance();
            GenericValue product = item.getProduct();
            if (product != null) {
            	orderItemMap.put("productCode", product.productCode);
            }
            orderItemMap.put("cartLineIndex", cartLineIndex);
            orderItemMap.put("alternativeOptionProductIds", item.getAlternativeOptionProductIds());

			// make map order item
			if (orderItem) {
				orderItemMap.putAll(orderItem.getAllFields());
				orderItemMap.put("orderItemGeneric", orderItem);
				BigDecimal alternativeBasePrice = (BigDecimal) item.getAttribute("alternativeUnitPrice");
				if (alternativeBasePrice != null) orderItemMap.put("alternativeBasePrice", alternativeBasePrice);
				result.add(orderItemMap);
			}
			
            // don't do anything with adjustments here, those will be added below in makeAllAdjustments
        }
        return result;
    }
}

cart = com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request);
context.shoppingCart = cart;
if (cart) {
	context.currencyUomId = cart.getCurrency();
	context.partyId = cart.getPartyId();
	context.cart = cart;
	context.currencyUomId = cart.getCurrency();
	context.productStore = ProductStoreWorker.getProductStore(request);
	
	// nuke the event messages
	request.removeAttribute("_EVENT_MESSAGE_");
	
	// orderItems = cart.makeOrderItems();
	// TODOCHANGE
	List<GenericValue> orderItems = FastList.newInstance();
	cart.makeNextSeqCartItems();
	List<Map<String, Object>> orderItemsMap = makeOrderItems(cart, false, false, nextItemSeq);
	if (orderItemsMap != null && orderItemsMap.orderItems) {
		for (Map<String, Object> orderItemMap : orderItemsMap) {
			GenericValue orderItem = orderItemMap.orderItemGeneric;
			if (orderItem != null) orderItems.add(orderItem);
		}
	}
	context.orderItems = orderItems;
	context.orderItemsMap = orderItemsMap;
	context.totalTaxOrderItemPromo = OrderWorker.getTotalTaxOrderItemPromo(delegator, orderItems, nowTimestamp);
	
	orderAdjustments = cart.makeAllAdjustments();
	
	orderItemShipGroupInfo = cart.makeAllShipGroupInfos();
	if (orderItemShipGroupInfo) {
		orderItemShipGroupInfo.each { osiInfo ->
			if ("OrderAdjustment".equals(osiInfo.getEntityName())) {
				// shipping / tax adjustment(s)
				orderAdjustments.add(osiInfo);
			}
		}
	}
	context.orderAdjustments = orderAdjustments;
	
	orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(orderAdjustments, null);
	context.orderHeaderAdjustments = orderHeaderAdjustments;
	context.headerAdjustmentsToShow = OrderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);
	
	orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, orderAdjustments);
	context.orderSubTotal = orderSubTotal;
	
	orh = new OrderReadHelper(orderAdjustments, orderItems);
	context.localOrderReadHelper = orh;
	
	shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
	shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
	context.orderShippingTotal = shippingAmount;
	
	taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	context.orderTaxTotal = taxAmount;
	BigDecimal orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	context.orderGrandTotal = orderGrandTotal;
	
	orderType = cart.getOrderType();
	context.orderType = orderType;
	
}
