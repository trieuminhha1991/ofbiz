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
        for (ShoppingCartItem item : cartLines) {
        	int cartLineIndex = cart.getItemIndex(item);
        	
            String orderItemSeqId = UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
            item.setOrderItemSeqId(orderItemSeqId);
            nextItemSeq++;
            
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
         	String weightUomId = (String) item.getAttribute("weightUomId");
            if (weightUomId != null) {
                orderItem.put("weightUomId", weightUomId);
            }
            BigDecimal alternativeQuantity = (BigDecimal) item.getAttribute("alternativeQuantity");
            if (alternativeQuantity != null) {
            	orderItem.put("alternativeQuantity", alternativeQuantity);
            }
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
				result.add(orderItemMap);
			}
			
            // don't do anything with adjustments here, those will be added below in makeAllAdjustments
        }
        return result;
    }
}

cart = ShoppingCartEvents.getCartObject(request);
context.shoppingCart = cart;
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
List<Map<String, Object>> orderItemsMap = makeOrderItems(cart, false, false, nextItemSeq);
if (orderItemsMap != null && orderItemsMap.orderItems) {
	for (Map<String, Object> orderItemMap : orderItemsMap) {
		GenericValue orderItem = orderItemMap.orderItemGeneric;
		if (orderItem != null) orderItems.add(orderItem);
	}
}
context.orderItems = orderItems;
context.orderItemsMap = orderItemsMap;

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

workEfforts = cart.makeWorkEfforts();
context.workEfforts = workEfforts;

orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(orderAdjustments, null);
context.orderHeaderAdjustments = orderHeaderAdjustments;
context.headerAdjustmentsToShow = OrderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);

orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, orderAdjustments);
context.orderSubTotal = orderSubTotal;
context.placingCustomerPerson = userLogin?.getRelatedOne("Person", false);
context.shippingAddress = cart.getShippingAddress();

paymentMethods = cart.getPaymentMethods();
paymentMethod = null;
if (paymentMethods) {
    paymentMethod = paymentMethods.get(0);
    context.paymentMethod = paymentMethod;
}

if ("CREDIT_CARD".equals(paymentMethod?.paymentMethodTypeId)) {
    creditCard = paymentMethod.getRelatedOne("CreditCard", true);
    context.creditCard = creditCard;
    context.formattedCardNumber = ContactHelper.formatCreditCard(creditCard);
} else if ("EFT_ACCOUNT".equals(paymentMethod?.paymentMethodTypeId)) {
    eftAccount = paymentMethod.getRelatedOne("EftAccount", true);
    context.eftAccount = eftAccount;
}

paymentMethodTypeIds = cart.getPaymentMethodTypeIds();
paymentMethodType = null;
paymentMethodTypeId = null;
if (paymentMethodTypeIds) {
    paymentMethodTypeId = paymentMethodTypeIds.get(0);
    paymentMethodType = delegator.findOne("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId], false);
    context.paymentMethodType = paymentMethodType;
}

webSiteId = WebSiteWorker.getWebSiteId(request);
productStoreId = ProductStoreWorker.getProductStoreId(request);
productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
if (productStore) {
    payToPartyId = productStore.payToPartyId;
    paymentAddress =  PaymentWorker.getPaymentAddress(delegator, payToPartyId);
    if (paymentAddress) context.paymentAddress = paymentAddress;
}

billingAddress = null;
if (paymentMethod) {
    creditCard = paymentMethod.getRelatedOne("CreditCard", false);
    billingAddress = creditCard?.getRelatedOne("PostalAddress", false);
}
if (billingAddress) context.billingAddress = billingAddress;

billingAccount = cart.getBillingAccountId() ? delegator.findOne("BillingAccount", [billingAccountId : cart.getBillingAccountId()], false) : null;
if (billingAccount) context.billingAccount = billingAccount;

context.customerPoNumber = cart.getPoNumber();
context.carrierPartyId = cart.getCarrierPartyId();
context.shipmentMethodTypeId = cart.getShipmentMethodTypeId();
context.shippingInstructions = cart.getShippingInstructions();
context.internalOrderNotes = cart.getInternalOrderNotes();
context.maySplit = cart.getMaySplit();
context.giftMessage = cart.getGiftMessage();
context.isGift = cart.getIsGift();
String defaultItemDeliveryDateStr = cart.getDefaultItemDeliveryDate();
if (UtilValidate.isNotEmpty(defaultItemDeliveryDateStr)) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date parsedDate = dateFormat.parse(defaultItemDeliveryDateStr);
    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
	context.defaultItemDeliveryDate = parsedDate;
}
context.shipBeforeDate = cart.getShipBeforeDate();
context.shipAfterDate = cart.getShipAfterDate();

shipmentMethodType = delegator.findOne("ShipmentMethodType", [shipmentMethodTypeId : cart.getShipmentMethodTypeId()], false);
if (shipmentMethodType) context.shipMethDescription = shipmentMethodType.description;

orh = new OrderReadHelper(orderAdjustments, orderItems);
context.localOrderReadHelper = orh;

shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
context.orderShippingTotal = shippingAmount;

taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
context.orderTaxTotal = taxAmount;
BigDecimal orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
context.orderGrandTotal = orderGrandTotal;

orderName = cart.getOrderName();
context.orderName = orderName;

orderPartyId = cart.getPartyId();
if (orderPartyId) {
    partyMap = PartyWorker.getPartyOtherValues(request, orderPartyId, "orderParty", "orderPerson", "orderPartyGroup");
    if (partyMap) {
        partyMap.each { key, value ->
            context[key] = value;
        }
    }
}

orderTerms = cart.getOrderTerms();
if (orderTerms) {
   context.orderTerms = orderTerms;
}

orderType = cart.getOrderType();
context.orderType = orderType;