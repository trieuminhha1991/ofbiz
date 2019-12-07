/*
* Prepare for Delivery Note
*/

import com.olbius.basehr.util.PartyHelper
import com.olbius.baselogistics.delivery.DeliveryItemEntity
import com.olbius.baselogistics.util.LogisticsOrderUtil
import com.olbius.baselogistics.util.LogisticsProductUtil
import com.olbius.product.util.ProductUtil
import com.olbius.util.*
import org.ofbiz.base.util.StringUtil
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import javolution.util.FastSet;
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

deliveryId = parameters.deliveryId;
delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);
deliveryType = delegator.findOne("DeliveryType", UtilMisc.toMap("deliveryTypeId", delivery.get("deliveryTypeId")), false);

BigDecimal rotate = LogisticsOrderUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);

String destContactMechId = delivery.getString("destContactMechId");
String supplierAddress = null;
String destAddress = null;

if (destContactMechId){ 
	address1 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", destContactMechId), false);
	if (address1 != null) { 
		destAddress = address1.getString("fullName");
	}
}
List<GenericValue> orderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderHeader.get("orderId"), "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
if (!orderRole.isEmpty()){
	String partySupplierId = (String)orderRole.get(0).get("partyId");
	List<GenericValue> partyContactMech = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partySupplierId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
	partyContactMech = EntityUtil.filterByDate(partyContactMech);
	if (!partyContactMech.isEmpty()){
		String contactMechId = partyContactMech.get(0).getString("contactMechId");
		postalAddress = delegator.findOne("PostalAddressFullNameDetail", false, UtilMisc.toMap("contactMechId", contactMechId));			
		supplierAddress = postalAddress.getString("fullName");
	}
}

Timestamp createDate = delivery.getTimestamp("createDate");
listItem = new ArrayList<DeliveryItemEntity>();

List<EntityCondition> conds = FastList.newInstance();
EntityCondition cond1 = EntityCondition.makeCondition("deliveryId", deliveryId);
conds.add(cond1);
if (!"DLV_CANCELLED".equals(delivery.statusId)){
    EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
	conds.add(cond2);
}


listDeliveryItem = delegator.findList("DeliveryItemGroupByOrderItem", EntityCondition.makeCondition(conds), null, null, null, false);
total = BigDecimal.ZERO;
BigDecimal itemPriceTotal = BigDecimal.ZERO;
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",delivery.getString("orderId")), false);
for(GenericValue item: listDeliveryItem ){
	deliveryItem = new DeliveryItemEntity();
	orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")), false);
	String isPromo = orderItem.getString("isPromo");
	deliveryItem.setIsPromo(isPromo);
	product = delegator.findOne("Product", UtilMisc.toMap("productId",orderItem.getString("productId")), false);
	
	boolean isKg = false;
	deliveryItem.setIsKg("N");
	if (ProductUtil.isWeightProduct(delegator, orderItem.getString("productId"))) { 
		isKg = true;
		deliveryItem.setIsKg("Y");
	}
	
	String orderQuantityUomId = orderItem.getString("quantityUomId");
	String baseQuantityUomId = product.getString("quantityUomId");
	BigDecimal convertNumber = BigDecimal.ONE;
	if (UtilValidate.isNotEmpty(orderQuantityUomId)) {
		convertNumber = ProductUtil.getConvertPackingNumber(delegator, orderItem.getString("productId"), orderQuantityUomId, baseQuantityUomId);
	}
	deliveryItem.setConvertNumber(convertNumber);
	if ("DELI_ITEM_DELIVERED".equals(item.statusId)){
		if (isKg) {
			deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualDeliveredAmount"));
			total = total.add(item.getBigDecimal("actualDeliveredAmount"));
		} else {
			deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualDeliveredQuantity"));
			total = total.add(item.getBigDecimal("actualDeliveredQuantity"));
		}
		
	} else if ("DELI_ITEM_EXPORTED".equals(item.statusId)) {
		if (isKg) {
			if (item.getBigDecimal("actualExportedAmount") != null){
				deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualExportedAmount"));
				total = total.add(item.getBigDecimal("actualExportedAmount"));
			} else {
				deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("amount"));
				total = total.add(item.getBigDecimal("amount"));
			}	
		} else {
			if (item.getBigDecimal("actualExportedQuantity") != null){
				deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualExportedQuantity"));
				total = total.add(item.getBigDecimal("actualExportedQuantity"));
			} else {
				deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualExportedQuantity"));
				total = total.add(item.getBigDecimal("quantity"));
			}
		}
	} else {
		if (isKg) {
			total = total.add(item.getBigDecimal("amount"));
		} else {
			total = total.add(item.getBigDecimal("quantity"));
		}
	}
	deliveryItem.setQuantity(item.getBigDecimal("quantity"));
	if (isKg) {
		deliveryItem.setQuantity(item.getBigDecimal("amount"));
	}
	String unitTmp = orderItem.getString("quantityUomId");
	if (isKg) {
		unitTmp = orderItem.getString("weightUomId");
	}
	uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", unitTmp), false);
	String unit = StringUtil.wrapString(uom.get("description", locale));
	if (isKg) {
		unit = StringUtil.wrapString(uom.get("abbreviation", locale));
	}
	
	deliveryItem.setUnit(unit);
	deliveryItem.setProductName(product.getString("productName"));
	deliveryItem.setProductId(product.getString("productId"));
	deliveryItem.setProductCode(product.getString("productCode"));
	
	deliveryItem.setCode(orderItem.getString("productId"));
	listItem.add(deliveryItem);
	
	
	deliveryItem.setUnitPrice(orderItem.getBigDecimal("unitPrice"));
	deliveryItem.setAlternativeUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice"));
	
	String quantityUomId = product.getString("quantityUomId");
	String purchaseUomId = orderItem.getString("quantityUomId");
	BigDecimal itemPrice = BigDecimal.ZERO;
	BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
	if (isKg && selectedAmount != null)  {
	
		deliveryItem.setUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice")/selectedAmount);
		deliveryItem.setAlternativeUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice")/selectedAmount);
		
		if ("DELI_ITEM_DELIVERED".equals(item.statusId)){
			itemPrice = deliveryItem.getUnitPrice().multiply(item.getBigDecimal("actualDeliveredAmount"));
		} else if ("DELI_ITEM_EXPORTED".equals(item.statusId)) {
			itemPrice = deliveryItem.getUnitPrice().multiply(item.getBigDecimal("actualExportedAmount"));
		} else {
			itemPrice = deliveryItem.getUnitPrice().multiply(item.getBigDecimal("amount"));
		}
	} else{ 
		if ("DELI_ITEM_DELIVERED".equals(item.statusId)){
			itemPrice = orderItem.getBigDecimal("unitPrice").multiply(item.getBigDecimal("actualDeliveredQuantity"));
		} else if ("DELI_ITEM_EXPORTED".equals(item.statusId)) {
			itemPrice = orderItem.getBigDecimal("unitPrice").multiply(item.getBigDecimal("actualExportedQuantity"));
		} else {
			itemPrice = orderItem.getBigDecimal("unitPrice").multiply(item.getBigDecimal("quantity"));
		}
	}
		
	itemPriceTotal = itemPriceTotal.add(itemPrice);
	deliveryItem.setTotal(itemPrice);
}

mapTmp = LogisticsProductUtil.getOrderSupplier(delegator, delivery.getString("orderId"));
cal = Calendar.getInstance();
year = cal.get(Calendar.YEAR);
month = cal.get(Calendar.MONTH);
day = cal.get(Calendar.DAY_OF_MONTH);

GenericValue orderShipGroup = delegator.findOne("OrderItemShipGroup", false, UtilMisc.toMap("orderId", orderHeader.get("orderId"), "shipGroupSeqId", "00001"));
String facilityId = "";
if (delivery.getString("destFacilityId") != null){
	facilityId = delivery.getString("destFacilityId");
} else {
	facilityId = orderShipGroup.getString("facilityId");
}            
destFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));

String deliveryTypeDescription = StringUtil.wrapString(deliveryType.get("description", locale));

List<String, Object> listTaxTmp = LogisticsOrderUtil.getListTaxForDelivery(delegator, listTaxTotal, deliveryId);

BigDecimal taxTotalByDelivery = BigDecimal.ZERO;
List<String, Object> listTaxTotals = new ArrayList<String, Object>();
if (!listTaxTmp.isEmpty()){
	for (Map<String, Object> maptax : listTaxTmp) {
		if (maptax.get("descriptionLog") != null){
			String descTmp = maptax.get("descriptionLog");
			descTmp = descTmp.replaceAll("&nbsp;", "");
			descTmp = descTmp.replaceAll("<span>", " ");
			descTmp = descTmp.replaceAll("</span>", " ");
			maptax.put("description", descTmp);
			maptax.put("sourcePercentage", maptax.get("sourcePercentage"));
			listTaxTotals.add(maptax);
		}
	}
}

for (Map<String, Object> maptax : listTaxTmp) {
	taxTotalByDelivery = taxTotalByDelivery.add(maptax.get("amount"));
}

BigDecimal grandTotalByDelivery = itemPriceTotal.add(taxTotalByDelivery);

String numInstanceStr = UtilProperties.getPropertyValue("baselogistics.properties", "order.purchase.delivery.instance");

// shipping address
String shippingAddress = null;
GenericValue orderContactMech = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
if (!orderContactMech.isEmpty()){ 
	String shippingContactMechId = orderContactMech.getString("destContactMechId");
	address3 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", shippingContactMechId), false);
	shippingAddress = address3.fullName;
}

List<Map<String, Object>> allOrderAdjustmentsPromoDelivery = FastList.newInstance();
List<GenericValue> listProductPromoUse = delegator.findByAnd("ProductPromoUse", ["orderId": orderHeader.orderId], null, false);
if (listProductPromoUse) {
    List<String> listPromoIds = EntityUtil.getFieldListFromEntityList(listProductPromoUse, "productPromoId", true);
    for (String promoId : listPromoIds) {
        List<GenericValue> listObjAdjTmp = EntityUtil.filterByAnd(listProductPromoUse, ["productPromoId" : promoId]);
        BigDecimal amountTotal = BigDecimal.ZERO;
        String promoName = "";

        GenericValue productPromo = delegator.findOne("ProductPromo", ["productPromoId": promoId], false);
        if (productPromo) promoName = productPromo.getString("promoName");
        Set<String> productPromoCodeIds = FastSet.newInstance();
        for (GenericValue adjItem : listObjAdjTmp) {
        	amountTotal = amountTotal.add(adjItem.getBigDecimal("totalDiscountAmount"));
	    	if (adjItem.get("productPromoCodeId")) productPromoCodeIds.add(adjItem.getString("productPromoCodeId"));
        }
        amountTotal = amountTotal.multiply(rotate);
        if (amountTotal != 0){
            Map<String, Object> orderAdjMap = FastMap.newInstance();
	        orderAdjMap.put("productPromoId", promoId);
	        orderAdjMap.put("promoName", promoName);
	        orderAdjMap.put("amount", amountTotal);
	        orderAdjMap.put("productPromoCodeIds", productPromoCodeIds);
	        allOrderAdjustmentsPromoDelivery.add(orderAdjMap);               
    	}
    }
}

if (taxDiscountTotal != null){
	taxDiscountTotal = taxDiscountTotal.multiply(rotate);                         
}

Integer numInstance = new Integer(numInstanceStr);
context.numInstance = numInstance;
context.grandTotalByDelivery = grandTotalByDelivery;
context.taxTotalByDelivery = taxTotalByDelivery;
context.listTaxTotals = listTaxTotals;
context.statusId = delivery.statusId;
context.itemPriceTotal = itemPriceTotal;					
context.facilityName = destFacility.getString("facilityName");
context.createdBy = PartyHelper.getPartyName(delegator, orderHeader.getString("createdBy"), true, true);
context.year = year;
context.month = month;
context.day = day;
context.listItem = listItem;
context.total = total;
context.orderId = orderHeader.getString("orderId");
context.supplierName = mapTmp.get("partyName");
context.supplierId = mapTmp.get("partyId");
context.supplierCode = mapTmp.get("partyCode");
context.createDate = createDate;
context.deliveryId = deliveryId;
context.deliveryTypeDescription = deliveryTypeDescription;
context.destFacility = destFacility;
context.shippingAddress = shippingAddress;
context.destAddress = destAddress;
context.supplierAddress = supplierAddress;
context.currencyUomId = orderHeader.currencyUom;
context.allOrderAdjustmentsPromoDelivery = allOrderAdjustmentsPromoDelivery;
context.taxDiscountTotal = taxDiscountTotal;