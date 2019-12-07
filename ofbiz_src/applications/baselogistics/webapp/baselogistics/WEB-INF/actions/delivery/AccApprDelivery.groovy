/*
* Prepare for Delivery Note
*/
import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import com.olbius.baselogistics.util.*;
import org.ofbiz.base.util.UtilFormatOut;
import java.math.RoundingMode;
import com.olbius.baselogistics.delivery.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.product.util.*;
	
locale = context.get("locale");
deliveryId = parameters.deliveryId;
delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);
statusId = delivery.getString("statusId");
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", delivery.getString("orderId")), false);
String currency = orderHeader.getString("currencyUom");
String partyIdTo = delivery.getString("partyIdTo");
String orderIdByDelivery = delivery.getString("orderId");
List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderIdByDelivery)), null, null, null, false);
String toOrderId = "";
if(!listOrderItemAssoc.isEmpty()){
	for(GenericValue orderItemAssoc: listOrderItemAssoc ){
		toOrderId = orderItemAssoc.getString("toOrderId");
	}
}

String originFacilityId = delivery.getString("originFacilityId");
originFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", originFacilityId), false);

String originFacilityName = "";
if (originFacility != null) {
	originFacilityName = originFacility.getString("facilityName");
}

Map<String, Object> mapAddressTmp = dispatcher.runSync("getFacilityContactMechs", [facilityId: originFacilityId, contactMechPurposeTypeId: "SHIP_ORIG_LOCATION", userLogin: userLogin]);
List<GenericValue> listFacilityContactMechs = (List<GenericValue>)mapAddressTmp.get("listFacilityContactMechs");
String originFacilityAddress = "";
if (!listFacilityContactMechs.isEmpty()) {
	 originFacilityAddress = listFacilityContactMechs.get(0).get("address1");
}
Timestamp createDate = delivery.getTimestamp("createDate");

String fullName = "";
GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyIdTo), false); 
if(person != null){
	String firstName = person.getString("firstName");
	String middleName = person.getString("middleName");
	String lastName = person.getString("lastName");
	fullName = lastName + " " + middleName + " " + firstName;
}

GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderIdByDelivery), false);

String productStoreIdTmp = orderHeader.productStoreId;

List<GenericValue> listOrderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderIdByDelivery)), null, null, null, false);
String billToPartyId = null;
for (GenericValue role : listOrderRoles) {
	if ("BILL_TO_CUSTOMER".equals(role.getString("roleTypeId"))){
		billToPartyId = partyId;
		break;
	}
}

String salesMethodChannelEnumId = orderHeader.getString("salesMethodChannelEnumId");
GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", salesMethodChannelEnumId), false);
String descriptionChannel = "";
if(enumeration != null){
	descriptionChannel = StringUtil.wrapString(enumeration.get("description", locale));
}

listItem = new ArrayList<DeliveryItemEntity>();
EntityCondition cond1 = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(cond1, cond2), null, null, null, false);
total = 0;
totalWithTax = 0;
for(GenericValue item: listDeliveryItem ){
	itemTotal = 0;
	String statusItemId = item.getString("statusId");
	orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")), false);
	
	Map<String, Object> mapTaxTmp = dispatcher.runSync("logisticsGetTaxRateProductTaxCalc", [productStoreId: productStoreIdTmp, productId: orderItem.getString("productId"), billToPartyId: billToPartyId, basePrice: orderItem.getBigDecimal("unitPrice"), quantity: item.getBigDecimal("actualExportedQuantity"), userLogin: userLogin]);
	
	BigDecimal priceWithTax = (BigDecimal) mapTaxTmp.get("priceWithTax");
	deliveryItem = new DeliveryItemEntity();
	BigDecimal convert = ProductUtil.getConvertPackingToBaseUom(delegator, orderItem.getString("productId"), orderItem.getString("quantityUomId"));
	deliveryItem.setConvertNumber(convert);
	deliveryItem.setStatusId(statusItemId);
	if ("DELI_ITEM_EXPORTED".equals(statusItemId) || "DELI_ITEM_DELIVERED".equals(statusItemId)){
		deliveryItem.setActualExportedQuantity(item.getBigDecimal("actualExportedQuantity"));
		deliveryItem.setActualExportedAmount(item.getBigDecimal("actualExportedAmount"));
	} else {
		deliveryItem.setActualExportedQuantity(BigDecimal.ZERO);
		deliveryItem.setActualExportedAmount(BigDecimal.ZERO);
	}
	String invId = item.getString("inventoryItemId"); 
	if (invId){
		GenericValue inv = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", invId), false);
		Timestamp manufacturedDate = inv.getTimestamp("datetimeManufactured");
		if (manufacturedDate != null){
			int date = manufacturedDate.getDate();
			int month = manufacturedDate.getMonth();
			int year = manufacturedDate.getYear() + 1900;
			String lotId = inv.getString("lotId");
			deliveryItem.setLotId(lotId);
			if (lotId != null){
				String lotAndManuf = date.toString()+month.toString()+year.toString()+lotId;
				deliveryItem.setLotAndManufacturedDate(lotAndManuf);			
			} else {
				deliveryItem.setLotAndManufacturedDate("");
			}
		}
	} else {
		deliveryItem.setLotAndManufacturedDate("");
		deliveryItem.setLotId("");
	}
	
	deliveryItem.setQuantity(item.getBigDecimal("quantity"));
	if (UtilValidate.isNotEmpty(item.get("amount"))){ 
		deliveryItem.setAmount(item.getBigDecimal("amount"));
	}
	
	deliveryItem.setActualExpireDate(item.getTimestamp("actualExpireDate"));
	//Check Uom
	if(orderItem.getString("isPromo").equals("N")){
		product = delegator.findOne("Product", UtilMisc.toMap("productId",orderItem.getString("productId")), false);
		if ("DELI_ITEM_EXPORTED".equals(item.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(item.getString("statusId"))){
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", orderItem.getString("quantityUomId")), false);
			deliveryItem.setUnit(uom.getString("description"));
			deliveryItem.setUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice"));
			
			if (UtilValidate.isNotEmpty(item.get("actualExportedQuantity")) && item.getBigDecimal("actualExportedQuantity").compareTo(BigDecimal.ZERO) > 0){
			 	if(UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals(product.getString("requireAmount"))) {
			 		total += item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
			 		itemTotal += item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
					totalWithTax += item.getBigDecimal("actualExportedAmount") * priceWithTax/orderItem.getBigDecimal("selectedAmount");
					
					deliveryItem.setTotal(item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount"));
			 	} else { 
					total += item.getBigDecimal("actualExportedQuantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
					itemTotal += item.getBigDecimal("actualExportedQuantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
					totalWithTax += item.getBigDecimal("actualExportedQuantity") * priceWithTax;			 	
					
					deliveryItem.setTotal(item.getBigDecimal("actualExportedQuantity") * orderItem.getBigDecimal("alternativeUnitPrice"));
			 	}
			}
			
			GenericValue configPacking = null;
			if (orderItem.getString("quantityUomId").equals(product.getString("quantityUomId"))){
				uomFrom = delegator.findOne("Uom", UtilMisc.toMap("uomId",orderItem.getString("quantityUomId")), false);
				uomTo = uomFrom;
				
				List<GenericValue> listConfigPackings = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId",orderItem.getString("productId"), "uomFromId", orderItem.getString("quantityUomId"), "uomToId", product.getString("quantityUomId"))), null, null, null, false);
				if (!listConfigPackings.isEmpty()){
					configPacking = listConfigPackings.get(0);
				}
				
			} else {
				List<GenericValue> listConfigPacking3s = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId",orderItem.getString("productId"), "uomFromId", orderItem.getString("quantityUomId"), "uomToId", product.getString("quantityUomId"))), null, null, null, false);
				if (!listConfigPacking3s.isEmpty()){
					configPacking = listConfigPacking3s.get(0);
				}
				uomFrom = delegator.findOne("Uom", UtilMisc.toMap("uomId",configPacking.getString("uomFromId")), false);
				uomTo = delegator.findOne("Uom", UtilMisc.toMap("uomId",configPacking.getString("uomToId")), false);
			}
		} else{
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId",product.getString("quantityUomId")), false);
			deliveryItem.setUnit(uom.getString("description"));
			deliveryItem.setUnitPrice(orderItem.getBigDecimal("unitPrice"));
			if(UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals(product.getString("requireAmount"))) {
				deliveryItem.setTotal(item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice"));
				total += item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
				itemTotal += item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
				totalWithTax += item.getBigDecimal("amount") * priceWithTax/orderItem.getBigDecimal("selectedAmount");
			} else { 
				deliveryItem.setTotal(item.getBigDecimal("quantity") * orderItem.getBigDecimal("alternativeUnitPrice"));
				total += item.getBigDecimal("quantity") * orderItem.getBigDecimal("alternativeUnitPrice");
				itemTotal += item.getBigDecimal("quantity") * orderItem.getBigDecimal("alternativeUnitPrice");
				totalWithTax += item.getBigDecimal("quantity") * priceWithTax;
			}	
		}
		List<GenericValue> listGoodIdentifications = delegator.findList("GoodIdentification", EntityCondition.makeCondition(UtilMisc.toMap("productId",orderItem.getString("productId"), "goodIdentificationTypeId", "SKU")), null, null, null, false);
		GenericValue goodId = null;
		if (!listGoodIdentifications.isEmpty()){
			goodId = listGoodIdentifications.get(0);
		}
		if (UtilValidate.isNotEmpty(goodId)) {
			deliveryItem.setSku(goodId.getString("idValue"));
		}
		deliveryItem.setCode(product.getString("productId"));
		deliveryItem.setProductName(product.getString("productName"));
		deliveryItem.setProductId(product.getString("productId"));
		deliveryItem.setProductCode(product.getString("productCode"));
		deliveryItem.setDeliveryId(item.deliveryId);
		deliveryItem.setDeliveryItemSeqId(item.deliveryItemSeqId);
		deliveryItem.setUnitPriceWithTax(priceWithTax);
		deliveryItem.setTotal(itemTotal);
		
		if ("DELI_ITEM_EXPORTED".equals(statusItemId) || "DELI_ITEM_DELIVERED".equals(statusItemId)){
			BigDecimal atExported = item.getBigDecimal("actualExportedQuantity");
			if (UtilValidate.isNotEmpty(atExported)) {
				if (atExported.compareTo(BigDecimal.ZERO) > 0) {
					listItem.add(deliveryItem);				
				}
			}
		} else {
			if (!"DELI_ITEM_CANCELLED".equals(statusItemId)){
				listItem.add(deliveryItem);
			}
		}
	}
}

listItemPromo = new ArrayList<DeliveryItemEntity>();
for(GenericValue item: listDeliveryItem ){
	String statusItemId = item.getString("statusId");
	orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")), false);
	deliveryItem = new DeliveryItemEntity();
	deliveryItem.setUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice"));
	BigDecimal convert = ProductUtil.getConvertPackingToBaseUom(delegator, orderItem.getString("productId"), orderItem.getString("quantityUomId"));
	deliveryItem.setConvertNumber(convert);
	if ("DELI_ITEM_EXPORTED".equals(item.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(item.getString("statusId"))){
		deliveryItem.setActualExportedQuantity(item.getBigDecimal("actualExportedQuantity"));
		deliveryItem.setActualExportedAmount(item.getBigDecimal("actualExportedAmount"));
	} else{
		deliveryItem.setActualExportedQuantity(BigDecimal.ZERO);
		deliveryItem.setActualExportedAmount(BigDecimal.ZERO);
	}
	deliveryItem.setQuantity(item.getBigDecimal("quantity"));
	if (UtilValidate.isNotEmpty(item.get("amount"))){ 
		deliveryItem.setAmount(item.getBigDecimal("amount"));
	}
	deliveryItem.setActualExpireDate(item.getTimestamp("actualExpireDate"));
	deliveryItem.setProductName(orderItem.getString("itemDescription"));
	deliveryItem.setProductId(orderItem.getString("productId"));
	product = delegator.findOne("Product", UtilMisc.toMap("productId",orderItem.getString("productId")), false);
	deliveryItem.setProductCode(product.getString("productCode"));
	if(orderItem.getString("isPromo").equals("Y")){
		uom = delegator.findOne("Uom", UtilMisc.toMap("uomId",orderItem.getString("quantityUomId")), false);
		deliveryItem.setUnit(uom.getString("description"));
		List<GenericValue> listGoodIdentifications = delegator.findList("GoodIdentification", EntityCondition.makeCondition(UtilMisc.toMap("productId",orderItem.getString("productId"), "goodIdentificationTypeId", "SKU")), null, null, null, false);
		GenericValue goodId = null;
		if (!listGoodIdentifications.isEmpty()){
			goodId = listGoodIdentifications.get(0);
		}
		if (UtilValidate.isNotEmpty(goodId)) {
			deliveryItem.setSku(goodId.getString("idValue"));
		}
		deliveryItem.setCode(product.getString("productId"));
		deliveryItem.setDeliveryId(item.deliveryId);
		deliveryItem.setDeliveryItemSeqId(item.deliveryItemSeqId);
		
		if ("DELI_ITEM_EXPORTED".equals(statusItemId) || "DELI_ITEM_DELIVERED".equals(statusItemId)){
			BigDecimal atExported = item.getBigDecimal("actualExportedQuantity");
			
			if(UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals(product.getString("requireAmount"))) {
		 		total += item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
		 	} else { 
				total += item.getBigDecimal("actualExportedQuantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
		 	}
			 	
			if (UtilValidate.isNotEmpty(atExported)) {
				if (atExported.compareTo(BigDecimal.ZERO) > 0) {
					listItemPromo.add(deliveryItem);				
				}
			}
		} else {
			if (!"DELI_ITEM_CANCELLED".equals(statusItemId)){
				if(UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals(product.getString("requireAmount"))) {
			 		total += item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
			 	} else { 
					total += item.getBigDecimal("quantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
			 	}
				listItemPromo.add(deliveryItem);
			}
		}
	}
}

cal = Calendar.getInstance();
year = cal.get(Calendar.YEAR);
month = cal.get(Calendar.MONTH);
day = cal.get(Calendar.DAY_OF_MONTH);
String shippingInstructions;
List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderIdByDelivery)), null, null, null, false);
for(GenericValue orderItemShipGroup: listOrderItemShipGroup){
	if(orderItemShipGroup != null){
		shippingInstructions = orderItemShipGroup.getString("shippingInstructions");
	}
}

List<GenericValue>  listOrderAdjustment;
List<GenericValue> listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition([deliveryId : deliveryId]), null, null, null, false);
if(!listDeliveryItem.isEmpty()){
	for(GenericValue deliveryItem: listDeliveryItem ){
		fromOrderId = deliveryItem.getString("fromOrderId");
		
		listOrderAdjustment = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(UtilMisc.toMap("orderId", fromOrderId, "orderAdjustmentTypeId", "SALES_TAX")), null, null, null, false);
	}
}

List<Map<String, Object>> list = FastList.newInstance();
List<String> listPercent = new ArrayList<String>();
if(!listOrderAdjustment.isEmpty()){
	for(GenericValue orderAdjustment: listOrderAdjustment ){
		if (!listPercent.isEmpty()){
			Boolean tf = false;
			for (String percent : listPercent){
				if (percent.equals(orderAdjustment.getBigDecimal("sourcePercentage").toString())){
					tf = true;
					break;
				} else {
					tf = false;
				}
			}
			if (!tf){
				listPercent.add(orderAdjustment.getBigDecimal("sourcePercentage").toString());
			}
		} else {
			listPercent.add(orderAdjustment.getBigDecimal("sourcePercentage").toString());
		}
	}
}
List<Map<String, Object>> listTax = FastList.newInstance();
if(!list.isEmpty() && "DLV_EXPORTED".equals(delivery.getString("statusId"))){
	for(Map<String, Object> mem : list){
	
		BigDecimal percentTmp = mem.getBigDecimal("sourcePercentage");
		List<GenericValue> listOrderItemTmp = (List<GenericValue>)mem.get("listOrderItems");
		
		if (!listOrderItemTmp.isEmpty()){
			Map<String, Object> mTmp = FastMap.newInstance();
			BigDecimal itemAmountTotal = BigDecimal.ZERO;
			for (GenericValue oi : listOrderItemTmp){
				BigDecimal itemAmount = BigDecimal.ZERO;
				List<GenericValue> listDlvItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", oi.get("orderId"), "fromOrderItemSeqId", oi.get("orderItemSeqId"), "deliveryId", deliveryId)), null, null, null, false);
				if (!listDlvItem.isEmpty()){
					for (GenericValue dlvItem : listDlvItem){
						itemAmount = itemAmount.add(dlvItem.getBigDecimal("actualExportedQuantity"));
					}
					itemAmount = itemAmount.multiply(oi.getBigDecimal("unitPrice"));
					itemAmount = itemAmount.multiply(percentTmp);
				}
				itemAmountTotal = itemAmountTotal.add(itemAmount);
			}
			mTmp.put("sourcePercentage", percentTmp);
			mTmp.put("amount", itemAmountTotal.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
			listTax.add(mTmp);
		}
	}
}
BigDecimal totalDeliveryByOrder = total;
BigDecimal totalDeliveryByOrderWithTax = totalWithTax;

for(Map<String, Object> map: listTax){
	amountSumTotal = map.get("amount");
	totalDeliveryByOrder += amountSumTotal;
}

// paryment method
List<GenericValue> listPayments = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderIdByDelivery)), null, null, null, false);
paymentMethodType = null;
if (!listPayments.isEmpty()){
	orderPaymentMethod = listPayments.get(0);
	paymentMethodType = delegator.findOne("PaymentMethodType", UtilMisc.toMap("paymentMethodTypeId", orderPaymentMethod.getString("paymentMethodTypeId")), false);
	paymentMethodDescription = StringUtil.wrapString(paymentMethodType.get("description", locale));
} else {
	paymentMethodDescription = "";
}

if (locale.getLanguage().equals("en")){
	String abc = UtilFormatOut.formatDecimalNumber(grandTotal, "#,##0", locale);
	abc = abc.replaceAll("\\,", "");
	totalString = LogisticsStringUtil.changeToWords(abc, true);
	
	String totalNotTax = UtilFormatOut.formatDecimalNumber(total, "#,##0", locale);
	totalNotTax = totalNotTax.replaceAll("\\,", "");
	totalStringNotTax = LogisticsStringUtil.changeToWords(totalNotTax, true);
} else if (locale.getLanguage().equals("vi")){
	totalString = LogisticsStringUtil.ConvertDecimalToString(grandTotal);
	totalStringNotTax = LogisticsStringUtil.ConvertDecimalToString(total);
} else {
	totalString = "";
	totalStringNotTax = "";
}
GenericValue orderAttr = delegator.findOne("OrderAttribute", false, UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "attrName", "estimateDistanceDelivery"));
BigDecimal estimateDistanceDelivery = null;
if (orderAttr != null){
	estimateDistanceDelivery = new BigDecimal(orderAttr.getString("attrValue"));
}

List<Map<String, Object>> orderAdjustmentsPromo = FastList.newInstance();
List<GenericValue> listOrderAdjOrderItemPromo = LogisticsOrderUtil.getListPromotionForDelivery(delegator, deliveryId);
if (listOrderAdjOrderItemPromo) {
	List<String> listPromoIds = EntityUtil.getFieldListFromEntityList(listOrderAdjOrderItemPromo, "productPromoId", true);
	for (String promoId : listPromoIds) {
		List<GenericValue> listObjAdjTmp = EntityUtil.filterByAnd(listOrderAdjOrderItemPromo, ["productPromoId" : promoId]);
		BigDecimal amountTotal = BigDecimal.ZERO;
		String promoName = "";
		for (GenericValue adjItem : listObjAdjTmp) {
			amountTotal = amountTotal.add(adjItem.getBigDecimal("amount"));
			promoName = adjItem.getString("description");
		}
		Map<String, Object> orderAdjMap = FastMap.newInstance();
		orderAdjMap.put("productPromoId", promoId);
		orderAdjMap.put("promoName", promoName);
		orderAdjMap.put("amount", amountTotal);
		if (amountTotal.compareTo(BigDecimal.ZERO) != 0) {
			orderAdjustmentsPromo.add(orderAdjMap);
		}
	}
}
List<Map<String, Object>> orderAdjustmentsPromoWithTax = LogisticsProductUtil.getListPromotionForDeliveryWithTax(delegator, deliveryId);

Boolean check = false;
List<Map<String, Object>> orderAdjustmentsPromo2 = FastList.newInstance();
EntityCondition adjTypeCond = EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, UtilMisc.toList("PROMOTION_ADJUSTMENT", "DISCOUNT_ADJUSTMENT"));
EntityCondition orderCond = EntityCondition.makeCondition("orderId", orderHeader.orderId);
EntityCondition orderItemCond = EntityCondition.makeCondition("orderItemSeqId", "_NA_");

List<GenericValue> listOrderAdjOrderItemPromo2 = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(UtilMisc.toList(adjTypeCond, orderCond, orderItemCond)), null, null, null, false);
if (listOrderAdjOrderItemPromo2) {
	check = true;
	List<String> listPromoIds2 = EntityUtil.getFieldListFromEntityList(listOrderAdjOrderItemPromo2, "productPromoId", true);
	for (String promoId : listPromoIds2) {
		List<GenericValue> listObjAdjTmp2 = EntityUtil.filterByAnd(listOrderAdjOrderItemPromo2, ["productPromoId" : promoId]);
		BigDecimal amountTotal = BigDecimal.ZERO;
		String promoName = "";
		for (GenericValue adjItem : listObjAdjTmp2) {
			amountTotal = amountTotal.add(adjItem.getBigDecimal("amount"));
			promoName = adjItem.getString("description");
		}
		Map<String, Object> orderAdjMap = FastMap.newInstance();
		orderAdjMap.put("productPromoId", promoId);
		orderAdjMap.put("promoName", promoName);
		orderAdjMap.put("amount", amountTotal);
		orderAdjustmentsPromo2.add(orderAdjMap);
	}
}
if (!check){
	List<GenericValue> listAdjPromos = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(UtilMisc.toList(adjTypeCond, orderCond)), null, null, null, false);
	if (!listAdjPromos.isEmpty()){
		for (GenericValue adjTmp : listAdjPromos){
			if (UtilValidate.isNotEmpty(adjTmp.get("orderItemSeqId"))){
				check = true;
				break;
			}
		}
	}
}
BigDecimal discountAmountTotal = BigDecimal.ZERO;
List<GenericValue> orderAdjustmentsDiscount = FastList.newInstance();
orderAdjustmentsDiscount = delegator.findByAnd("OrderAdjustment", ["orderId": orderHeader.orderId, "orderAdjustmentTypeId": "DISCOUNT_ADJUSTMENT"], null, false);
if (!orderAdjustmentsDiscount.isEmpty()) {
	for (GenericValue adjTmp : orderAdjustmentsDiscount) {
		discountAmountTotal = discountAmountTotal.add(adjTmp.getBigDecimal("amount"));
	}
}

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
if (check){
	BigDecimal rotation = LogisticsOrderUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);
	taxTotalByDelivery = taxAmount.multiply(rotation);
} else {
	for (Map<String, Object> maptax : listTaxTmp) {
		taxTotalByDelivery = taxTotalByDelivery.add(maptax.get("amount"));
	}
}

List<Map<String, Object>> listItemTotalTmp = new ArrayList<Map<String, Object>>();
List<String> listPrIds = new ArrayList<String>();
for (DeliveryItemEntity t : listItem) {
	if (!listPrIds.contains(t.getCode())){
		listPrIds.add(t.getCode());
	}
}

List<DeliveryItemEntity> listItemDetail = new ArrayList<DeliveryItemEntity>();
listItemDetail.addAll(listItem);

for (String idTmp : listPrIds) {
	BigDecimal quantityTmp = BigDecimal.ZERO;
	BigDecimal quantityCreated = BigDecimal.ZERO;
	String prodNameTmp = "";
	String prodIdTmp = "";
	String unitTmp = "";
	BigDecimal unitPriceTmp = BigDecimal.ZERO;
	BigDecimal unitPriceWithTax = BigDecimal.ZERO;
	BigDecimal itemTotalTmp = BigDecimal.ZERO;
	GenericValue prTmp = delegator.findOne("Product", UtilMisc.toMap("productId", idTmp), false);
	BigDecimal convert = BigDecimal.ONE;
    for (DeliveryItemEntity item : listItem){
    	if (idTmp.equals(item.getCode())){
			if (item.getStatusId() == "DELI_ITEM_EXPORTED" || item.getStatusId() == "DELI_ITEM_DELIVERED") {
				if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
	    			quantityCreated = quantityCreated.add(item.getActualExportedAmount());
	    			quantityTmp = quantityTmp.add(item.getActualExportedAmount());
	    		} else { 
	    			quantityCreated = quantityCreated.add(item.getActualExportedQuantity());
	    			quantityTmp = quantityTmp.add(item.getActualExportedQuantity());
	    		}
			} else {
				if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
	    			quantityTmp = quantityTmp.add(item.getAmount());
	    			quantityCreated = quantityCreated.add(item.getAmount());			
	    		} else { 
	    			quantityTmp = quantityTmp.add(item.getQuantity());
	    			quantityCreated = quantityCreated.add(item.getQuantity());			
	    		}
			}
    		prodNameTmp = item.getProductName();
    		prodIdTmp = item.getProductId();
    		unitTmp = item.getUnit();
    		unitPriceTmp = item.getUnitPrice();
    		unitPriceWithTax = item.getUnitPriceWithTax();
    		convert = item.getConvertNumber();
    	}
    }
    
    Map<String, Object> mapTmp = FastMap.newInstance();
    mapTmp.put("requireAmount", prTmp.get("requireAmount"));
    mapTmp.put("code", idTmp);
    mapTmp.put("productName", prodNameTmp);
    mapTmp.put("productId", prodIdTmp);
    mapTmp.put("productCode", prTmp.getString("productCode"));
    mapTmp.put("unit", unitTmp);
    mapTmp.put("actualExportedQuantity", quantityTmp);
    mapTmp.put("quantity", quantityCreated);
    mapTmp.put("convertNumber", convert);
    mapTmp.put("unitPrice", unitPriceTmp);
    mapTmp.put("unitPriceWithTax", unitPriceWithTax);
    mapTmp.put("itemTotal", itemTotalTmp);
    
    listItemTotalTmp.add(mapTmp);
}
List<Map<String, Object>> listItemPromoSum = new ArrayList<Map<String, Object>>();
listPrIds = new ArrayList<String>();
for (DeliveryItemEntity t : listItemPromo) {
	if (!listPrIds.contains(t.getCode())){
		listPrIds.add(t.getCode());
	}
}
listItemDetail.addAll(listItemPromo);

for (String idTmp : listPrIds) {
	BigDecimal quantityTmp = BigDecimal.ZERO;
	BigDecimal quantityCreated = BigDecimal.ZERO;
	String prodNameTmp = "";
	String unitTmp = "";
	BigDecimal unitPriceTmp = BigDecimal.ZERO;
	BigDecimal unitPriceWithTax = BigDecimal.ZERO;
	BigDecimal itemTotalTmp = BigDecimal.ZERO;
	BigDecimal convert = BigDecimal.ONE;
	GenericValue prTmp = delegator.findOne("Product", UtilMisc.toMap("productId", idTmp), false);
    for (DeliveryItemEntity item : listItemPromo){
    	if (idTmp.equals(item.getCode())){
    		if (item.getStatusId() == "DELI_ITEM_EXPORTED" || item.getStatusId() == "DELI_ITEM_DELIVERED") {
	    		if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
	    			quantityTmp = quantityTmp.add(item.getActualExportedAmount());
	    			quantityCreated = quantityCreated.add(item.getActualExportedAmount());
	    		} else { 
	    			quantityTmp = quantityTmp.add(item.getActualExportedQuantity());
	    			quantityCreated = quantityCreated.add(item.getActualExportedQuantity());
	    		}
			} else { 
				if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
					quantityCreated = quantityCreated.add(item.getAmount());	
					quantityTmp = quantityTmp.add(item.getAmount());
				} else { 
					quantityCreated = quantityCreated.add(item.getQuantity());	
					quantityTmp = quantityTmp.add(item.getQuantity());
				}
			}
						
    		prodNameTmp = item.getProductName();
    		unitTmp = item.getUnit();
    		unitPriceTmp = item.getUnitPrice();
    		unitPriceWithTax = item.getUnitPriceWithTax();
    		convert = item.getConvertNumber();
    	}
    }
    Map<String, Object> mapTmp = FastMap.newInstance();
    mapTmp.put("code", idTmp);
    mapTmp.put("productName", prodNameTmp);
    mapTmp.put("productId", prTmp.getString("productId"));
    mapTmp.put("productCode", prTmp.getString("productCode"));
    mapTmp.put("unit", unitTmp);
    mapTmp.put("convertNumber", convert);
    mapTmp.put("actualExportedQuantity", quantityTmp);
    mapTmp.put("quantity", quantityCreated);
    mapTmp.put("unitPrice", unitPriceTmp);
    mapTmp.put("isPromo", "Y");
    mapTmp.put("unitPriceWithTax", unitPriceWithTax);
    listItemPromoSum.add(mapTmp);
}

listItemTotalTmp.addAll(listItemPromoSum);

context.listItemDetail = listItemDetail;
context.check = check;
context.discountAmountTotal = discountAmountTotal;
context.orderAdjustmentsPromo2 = orderAdjustmentsPromo2;
context.taxTotalByDelivery = taxTotalByDelivery;
context.orderAdjustmentsPromo = orderAdjustmentsPromo;
context.estimateDistanceDelivery = estimateDistanceDelivery;
context.listTaxTotals = listTaxTotals;
context.totalString = totalString;
context.totalDeliveryByOrder = totalDeliveryByOrder;
context.totalDeliveryByOrderWithTax = totalDeliveryByOrderWithTax;
context.list = list;
context.listTax = listTax; 
context.year = year;
context.month = month;
context.day = day;
context.listItem = listItemTotalTmp;
context.listItemPromo = listItemPromoSum;
context.deliveryId = deliveryId;
context.partyIdTo = partyIdTo;
context.fullName = fullName;
context.toOrderId = toOrderId;
context.descriptionChannel = descriptionChannel;
context.createDate = createDate;
context.shippingInstructions = shippingInstructions;
context.total = total;
context.totalWithTax = totalWithTax;
context.totalStringNotTax = totalStringNotTax;
context.currency = currency;
context.originFacilityName = originFacilityName;
context.paymentMethodDescription = paymentMethodDescription;
context.paymentMethodType = paymentMethodType;
if (delivery.actualStartDate != null){
context.startDate = delivery.actualStartDate;
} else {
context.startDate = delivery.estimatedStartDate;
}

if (delivery.actualArrivalDate != null){
context.arrivalDate = delivery.actualArrivalDate;
} else {
context.arrivalDate = delivery.estimatedArrivalDate;
}

context.estimatedStartDate = delivery.estimatedStartDate;
context.estimatedArrivalDate = delivery.estimatedArrivalDate;

context.orderAdjustmentsPromoWithTax = orderAdjustmentsPromoWithTax;
context.originFacilityAddress = originFacilityAddress;    
context.statusId = statusId;    
context.originFacility = originFacility;    