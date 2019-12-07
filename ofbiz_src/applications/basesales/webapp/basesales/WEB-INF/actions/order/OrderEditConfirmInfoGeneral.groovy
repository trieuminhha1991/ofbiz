
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

import com.olbius.basesales.util.SalesUtil;

import java.text.SimpleDateFormat;
import com.olbius.basesales.order.OrderWorker;

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
String estimateDistanceDelivery = cart.getOrderAttribute("estimateDistanceDelivery");
if (estimateDistanceDelivery) context.estimateDistanceDelivery = new BigDecimal(estimateDistanceDelivery);

shipmentMethodType = delegator.findOne("ShipmentMethodType", [shipmentMethodTypeId : cart.getShipmentMethodTypeId()], false);
if (shipmentMethodType) context.shipMethDescription = shipmentMethodType.description;

workEfforts = cart.makeWorkEfforts();
context.workEfforts = workEfforts;

orderName = cart.getOrderName();
context.orderName = orderName;

orderTerms = cart.getOrderTerms();
if (orderTerms) {
   context.orderTerms = orderTerms;
}

orderPartyId = cart.getPartyId();
if (orderPartyId) {
    partyMap = PartyWorker.getPartyOtherValues(request, orderPartyId, "orderParty", "orderPerson", "orderPartyGroup");
    if (partyMap) {
        partyMap.each { key, value ->
            context[key] = value;
        }
    }
}

// check liability of customer
currentOrganizationPartyId = SalesUtil.getCurrentOrganization(delegator, userLogin.userLoginId);
Map<String, Object> resultValueLiability = dispatcher.runSync("getLiabilityParty", UtilMisc.toMap("partyId", orderPartyId, "organizationPartyId", currentOrganizationPartyId, "userLogin", userLogin));
if (ServiceUtil.isSuccess(resultValueLiability)) {
	context.partyLiability = resultValueLiability.totalLiability;
	context.partyLiabilityAfter = orderGrandTotal.add((BigDecimal) resultValueLiability.totalLiability);
	context.currentCurrencyId = cart.getCurrency();
}