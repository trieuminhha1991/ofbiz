/*
* Prepare for Delivery Note
*/
import javolution.util.FastSet;
import org.ofbiz.base.util.Debug;
import com.olbius.baselogistics.delivery.DeliveryItemEntity
import com.olbius.baselogistics.util.LogisticsOrderUtil
import com.olbius.product.util.ProductUtil
import com.olbius.util.*
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);
String statusId = delivery.getString("statusId");
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", delivery.getString("orderId")), false);
String productStoreIdTmp = orderHeader.productStoreId;

String originFacilityId = delivery.getString("originFacilityId");
originFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", originFacilityId), false);

String originContactMechId = delivery.getString("originContactMechId");
String destContactMechId = delivery.getString("destContactMechId");
String customerAddress = null;
String originAddress = null;

if (originContactMechId){
	address1 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", originContactMechId), false);
	if (address1 != null) {
		originAddress = address1.getString("fullName");
	}
}
if (destContactMechId){
	address2 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", destContactMechId), false);
	if (address2 != null) {
		customerAddress = address2.getString("fullName");
	}
}

String partyToFullName = "";
String partyIdTo = delivery.getString("partyIdTo");
GenericValue partyTo = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyIdTo), false);
if(partyTo != null){
	partyToFullName = partyTo.fullName;
}

List<GenericValue> phones = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo, "contactMechPurposeTypeId", "PHONE_MOBILE")), null, null, null, false);
List<GenericValue> phone2s = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
phones = EntityUtil.filterByDate(phones);
String phoneCustomer = "";
if (!phones.isEmpty()) {
	String phoneId = phones.get(0).getString("contactMechId");
	telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", phoneId), false);
	if (telecomNumber != null) {
		phoneCustomer = telecomNumber.getString("contactNumber");
	}
}
if (!phone2s.isEmpty()) {
	String phoneId = phone2s.get(0).getString("contactMechId");
	telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", phoneId), false);
	if (telecomNumber != null && phoneCustomer != "") {
		phoneCustomer = phoneCustomer + " - " + telecomNumber.getString("contactNumber");
	} else {
		phoneCustomer = telecomNumber.getString("contactNumber");
	}
}

List<GenericValue> listOrderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", delivery.getString("orderId"))), null, null, null, false);
String partySallerFullName = "";
String phoneSaller = "";
String billToPartyId = null;
if(!listOrderRole.isEmpty()){
	for(GenericValue orderRole: listOrderRole ){
		String roleTypeId = orderRole.getString("roleTypeId");
		if(roleTypeId.equals("SALES_EXECUTIVE")){
			String partyIdSale = orderRole.getString("partyId");
			GenericValue partySales = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyIdSale), false);
			if(partySales != null){
				partySallerFullName = partySales.fullName;
				List<GenericValue> listCTMSales = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdSale, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
				if(!listCTMSales.isEmpty()){
					contactMechIdSales = listCTMSales.get(0).getString("contactMechId");
					telecomNumberSales = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechIdSales), false);
					if(telecomNumberSales != null){
						phoneSaller = telecomNumberSales.getString("contactNumber");
					}
				}
			}
		}
		if ("BILL_TO_CUSTOMER".equals(orderRole.getString("roleTypeId"))){
			billToPartyId = orderRole.getString("partyId");
		}
	}
}


List<DeliveryItemEntity> listDeliveryItems = new ArrayList<DeliveryItemEntity>();
List<EntityCondition> listConds = FastList.newInstance();
EntityCondition cond1 = EntityCondition.makeCondition("shippingTripId", EntityOperator.EQUALS, shippingTripId);
listConds.add(cond1);
if (!"DLV_CANCELLED".equals(statusId)) {
	EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
	listConds.add(cond2);
}

	listItems = delegator.findList("ShippingTripDeliveryOrderItemView", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("fromOrderItemSeqId"), null, false);


total = 0;
grandTotal = 0;
totalWithTax = 0;

context.orderAdjustmentsPromoDelivery = orderAdjustmentsPromoDelivery;
context.allOrderAdjustmentsPromoDelivery = allOrderAdjustmentsPromoDelivery;
context.shippingAddress = shippingAddress;
context.statusId = statusId;
context.listTaxTotals = listTaxTotals;
context.total = total;
context.grandTotal = grandTotal;
context.listItem = listItems;
context.listItemTotal = listItems;
context.deliveryId = deliveryId;
context.partyTo = partyTo;
context.partyToFullName = partyToFullName;
context.originAddress = originAddress;
context.customerAddress = customerAddress;
context.originFacility = originFacility;
context.partySallerFullName = partySallerFullName;
context.phoneCustomer = phoneCustomer;
context.phoneSaller = phoneSaller;
context.taxTotalByDelivery = taxTotalByDelivery;
context.orderHeader = orderHeader;
context.totalDiscount = totalDiscount;