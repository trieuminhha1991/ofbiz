/*
* Prepare for Delivery Note
*/

import com.olbius.basesales.order.OrderReadHelper
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
EntityCondition cond1 = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
listConds.add(cond1);
if (!"DLV_CANCELLED".equals(statusId)) {
	EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
	listConds.add(cond2);
}

listItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("fromOrderItemSeqId"), null, false);

listItem = new ArrayList<DeliveryItemEntity>();
listItemPromo = new ArrayList<DeliveryItemEntity>();

total = 0;
grandTotal = 0;
totalWithTax = 0;
for(GenericValue item: listItems){
	deliveryItem = new DeliveryItemEntity();
	itemTotal = 0;
	BigDecimal quantity = item.getBigDecimal("quantity");
	String statusItemId = item.getString("statusId");
	if ("DELI_ITEM_EXPORTED".equals(statusItemId) || "DELI_ITEM_DELIVERED".equals(statusItemId)){
		quantity = item.getBigDecimal("actualExportedQuantity");
	}
	orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")), false);
	
	boolean isPromo = false;
	if (orderItem.get("isPromo") != null && "Y".equals(orderItem.getString("isPromo"))) { 
		isPromo = true;
	}
	
	String productId = orderItem.getString("productId");

	Map<String, Object> mapTaxTmp = dispatcher.runSync("logisticsGetTaxRateProductTaxCalc", [productStoreId: productStoreIdTmp, productId: orderItem.getString("productId"), billToPartyId: billToPartyId, basePrice: orderItem.getBigDecimal("unitPrice"), quantity: quantity, userLogin: userLogin]);
	BigDecimal priceWithTax = (BigDecimal) mapTaxTmp.get("priceWithTax");

	BigDecimal convert = BigDecimal.ONE;
	Boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
	if (!isKg) {
		convert = ProductUtil.getConvertPackingToBaseUom(delegator, orderItem.getString("productId"), orderItem.getString("quantityUomId"));
	}
	deliveryItem.setFromOrderItemSeqId(orderItem.getString("orderItemSeqId"));
	deliveryItem.setConvertNumber(convert);
	deliveryItem.setStatusId(statusItemId);
	if (isKg) {
		deliveryItem.setIsKg("Y");
	}
	if ("DELI_ITEM_EXPORTED".equals(statusItemId)){
		deliveryItem.setActualExportedQuantity(item.getBigDecimal("actualExportedQuantity"));
		deliveryItem.setActualExportedAmount(item.getBigDecimal("actualExportedAmount"));
	} else if ("DELI_ITEM_DELIVERED".equals(statusItemId)){
		deliveryItem.setActualExportedQuantity(item.getBigDecimal("actualExportedQuantity"));
		deliveryItem.setActualExportedAmount(item.getBigDecimal("actualExportedAmount"));
		deliveryItem.setActualDeliveredQuantity(item.getBigDecimal("actualDeliveredQuantity"));
		deliveryItem.setActualDeliveredAmount(item.getBigDecimal("actualDeliveredAmount"));
	} else {
		deliveryItem.setActualExportedQuantity(BigDecimal.ZERO);
		deliveryItem.setActualExportedAmount(BigDecimal.ZERO);
		deliveryItem.setActualDeliveredQuantity(BigDecimal.ZERO);
		deliveryItem.setActualDeliveredAmount(BigDecimal.ZERO);
	}

	deliveryItem.setQuantity(item.getBigDecimal("quantity"));
	if (UtilValidate.isNotEmpty(item.get("amount"))){
		deliveryItem.setAmount(item.getBigDecimal("amount"));
	}

	product = delegator.findOne("Product", UtilMisc.toMap("productId", orderItem.getString("productId")), false);
	
	if (isKg) {
		if (orderItem.weightUomId != null) { 
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", orderItem.getString("weightUomId")), false);
		} else { 
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", product.getString("weightUomId")), false);
		}
		deliveryItem.setUnit(uom.getString("abbreviation"));
	} else { 
		uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", orderItem.getString("quantityUomId")), false);
		deliveryItem.setUnit(uom.getString("description"));
	}
	
	deliveryItem.setUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice"));
	
	if ("DELI_ITEM_EXPORTED".equals(item.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(item.getString("statusId"))){
		if (UtilValidate.isNotEmpty(item.get("actualExportedQuantity")) && item.getBigDecimal("actualExportedQuantity").compareTo(BigDecimal.ZERO) > 0){
		 	if(isKg) {
	 			if (!isPromo) { 
	 				total += item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
		 			itemTotal += item.getBigDecimal("actualExportedAmount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
		 			totalWithTax += item.getBigDecimal("actualExportedAmount") * priceWithTax/orderItem.getBigDecimal("selectedAmount");
	 			}
		 	} else {
				if (!isPromo) { 
					total += item.getBigDecimal("actualExportedQuantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
					itemTotal += item.getBigDecimal("actualExportedQuantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
					totalWithTax += item.getBigDecimal("actualExportedQuantity") * priceWithTax;
				}
		 	}
		}
	} else{
		if(isKg) {
			if (!isPromo) { 
				total += item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
				itemTotal += item.getBigDecimal("amount") * orderItem.getBigDecimal("alternativeUnitPrice")/orderItem.getBigDecimal("selectedAmount");
				totalWithTax += item.getBigDecimal("amount") * priceWithTax/orderItem.getBigDecimal("selectedAmount");
			}
		} else {
			if (!isPromo) { 
				total += item.getBigDecimal("quantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
				itemTotal += item.getBigDecimal("quantity")/convert * orderItem.getBigDecimal("alternativeUnitPrice");
				totalWithTax += item.getBigDecimal("quantity") * priceWithTax;
			}
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
	deliveryItem.setProductName(product.getString("productName"));
	deliveryItem.setProductId(product.getString("productId"));
	deliveryItem.setProductCode(product.getString("productCode"));
	deliveryItem.setDeliveryId(item.deliveryId);
	deliveryItem.setDeliveryItemSeqId(item.deliveryItemSeqId);
	deliveryItem.setUnitPriceWithTax(priceWithTax);
	deliveryItem.setTotal(itemTotal);
	deliveryItem.setSelectedAmount(orderItem.getBigDecimal("selectedAmount"));

	if (orderItem.getString("isPromo").equals("Y")){
		listItemPromo.add(deliveryItem);
	} else {
		listItem.add(deliveryItem);
	}
}

BigDecimal rotate = LogisticsOrderUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);
List<Map<String, Object>> orderAdjustmentsPromoDelivery = FastList.newInstance();
List<Map<String, Object>> allOrderAdjustmentsPromoDelivery = FastList.newInstance();

BigDecimal totalDiscount = 0;
List<GenericValue> listOrderAdjustments = delegator.findByAnd("OrderAdjustment", ["orderId": orderHeader.orderId, "orderAdjustmentTypeId": "PROMOTION_ADJUSTMENT"], null, false);

orderReadHelper = new OrderReadHelper(orderHeader);
List<GenericValue> listOrderItemPromo = delegator.findByAnd("OrderItem", ["orderId": orderHeader.orderId, "isPromo": "Y"], null, false);
orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments(listOrderItemPromo);
orderSubTotal = orderReadHelper.getNewOrderItemsSubTotal();
context.orderHeaderAdjustments = orderHeaderAdjustments;
context.orderSubTotal = orderSubTotal;

otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);
context.otherAdjAmount = otherAdjAmount;

List<String> listPromoExceptIds = EntityUtil.getFieldListFromEntityList(listOrderAdjustments, "productPromoId", true);

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
        if (!listPromoExceptIds.contains(promoId)){ 
        	totalDiscount = totalDiscount.add(amountTotal);
        }
        Map<String, Object> orderAdjMap = FastMap.newInstance();
        orderAdjMap.put("productPromoId", promoId);
        orderAdjMap.put("promoName", promoName);
        orderAdjMap.put("amount", amountTotal);
        orderAdjMap.put("productPromoCodeIds", productPromoCodeIds);
        allOrderAdjustmentsPromoDelivery.add(orderAdjMap);
        
        if (!listPromoExceptIds.contains(promoId)){ 
        	orderAdjustmentsPromoDelivery.add(orderAdjMap);
        }
    }
}

grandTotal += otherAdjAmount;

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
grandTotal += taxTotalByDelivery;

List<Map<String, Object>> listItemTotalExport = new ArrayList<Map<String, Object>>();
List<Map<String, Object>> listItemTotal = new ArrayList<Map<String, Object>>();
List<String> listPrIds = new ArrayList<String>();
for (DeliveryItemEntity t : listItem) {
	if (!listPrIds.contains(t.getProductId())){
		listPrIds.add(t.getProductId());
	}
}
List<DeliveryItemEntity> listItemDetail = new ArrayList<DeliveryItemEntity>();
listItemDetail.addAll(listItem);

for (String idTmp : listPrIds) {
	String prodNameTmp = "";
	String prodIdTmp = "";
	String unitTmp = "";
	BigDecimal unitPriceWithTax = BigDecimal.ZERO;
	GenericValue prTmp = delegator.findOne("Product", UtilMisc.toMap("productId", idTmp), false);
	BigDecimal convert = BigDecimal.ONE;
    String orderSeqId = "";
    List<String> seqOis = FastList.newInstance();
    String isKg = "N";
    
    List<BigDecimal> selectedAmounts = FastList.newInstance();
    for (DeliveryItemEntity item : listItem){
   		isKg = item.getIsKg();
   		if ("Y".equals(isKg) && idTmp.equals(item.getProductId())) { 
			boolean check = false;
			BigDecimal sltAmout = item.getSelectedAmount();
			if (sltAmout != null) { 
				for(BigDecimal bd : selectedAmounts) {
				    if (bd.compareTo(sltAmout) == 0) {
				        check = true;
				    }
				} 
				if (!check) selectedAmounts.add(sltAmout);
			}
   		}
    }
    if (!selectedAmounts.isEmpty()) { 
    	for (BigDecimal selectedAm : selectedAmounts) { 
    		BigDecimal actualExportedQuantity = BigDecimal.ZERO;
			BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
			BigDecimal quantityCreated = BigDecimal.ZERO;
			BigDecimal unitPriceTmp = BigDecimal.ZERO;
			BigDecimal itemTotalTmp = BigDecimal.ZERO;
    		for (DeliveryItemEntity item : listItem){
		    	BigDecimal selectedAmTmp = item.getSelectedAmount();
		    	if (idTmp.equals(item.getProductId()) && selectedAmTmp.compareTo(selectedAm) == 0){
		    		isKg = item.getIsKg();
					if (item.getStatusId() == "DELI_ITEM_EXPORTED") {
						if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
			    		} else {
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
			    		}
					}
					if (item.getStatusId() == "DELI_ITEM_DELIVERED") {
						if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
			    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredAmount());
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
			    		} else {
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
			    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredQuantity());
			    		}
					}
					
					if (isKg){
	    				quantityCreated = item.getAmount();
		    		} else {
		    			item.getQuantity();
		    		}
					itemTotalTmp = itemTotalTmp.add(item.getTotal());
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
		    mapTmp.put("selectedAmount", selectedAm);
		    mapTmp.put("code", idTmp);
		    mapTmp.put("productName", prodNameTmp);
		    mapTmp.put("productId", prodIdTmp);
		    mapTmp.put("productCode", prTmp.getString("productCode"));
		    mapTmp.put("unit", unitTmp);
		    mapTmp.put("actualExportedQuantity", actualExportedQuantity/selectedAm);
		    mapTmp.put("actualDeliveredQuantity", actualDeliveredQuantity/selectedAm);
		    mapTmp.put("quantity", quantityCreated/selectedAm);
		    mapTmp.put("convertNumber", convert);
		    mapTmp.put("unitPrice", unitPriceTmp);
		    mapTmp.put("unitPriceWithTax", unitPriceWithTax);
		    mapTmp.put("itemTotal", itemTotalTmp);
		    mapTmp.put("isKg", isKg);
		
		    listItemTotal.add(mapTmp);
		    if (actualExportedQuantity > 0) {
		    	listItemTotalExport.add(mapTmp);
		   	} else if ("DLV_APPROVED".equals(statusId)){
		   		listItemTotalExport.add(mapTmp);
		   	}
    	}
    } else {
    	BigDecimal actualExportedQuantity = BigDecimal.ZERO;
		BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
		BigDecimal quantityCreated = BigDecimal.ZERO;
		BigDecimal unitPriceTmp = BigDecimal.ZERO;
		BigDecimal itemTotalTmp = BigDecimal.ZERO;
    	for (DeliveryItemEntity item : listItem){
	    	if (idTmp.equals(item.getProductId())){
	    		isKg = item.getIsKg();
				if (item.getStatusId() == "DELI_ITEM_EXPORTED") {
					if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
		    		} else {
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
		    		}
				}
				if (item.getStatusId() == "DELI_ITEM_DELIVERED") {
					if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
		    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredAmount());
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
		    		} else {
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
		    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredQuantity());
		    		}
				}
				if (isKg){
    				quantityCreated = item.getAmount();
	    		} else {
    				quantityCreated = item.getQuantity();
	    		}
				itemTotalTmp = itemTotalTmp.add(item.getTotal());
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
	    mapTmp.put("actualExportedQuantity", actualExportedQuantity);
	    mapTmp.put("actualDeliveredQuantity", actualDeliveredQuantity);
	    mapTmp.put("quantity", quantityCreated);
	    mapTmp.put("convertNumber", convert);
	    mapTmp.put("unitPrice", unitPriceTmp);
	    mapTmp.put("unitPriceWithTax", unitPriceWithTax);
	    mapTmp.put("itemTotal", itemTotalTmp);
	    mapTmp.put("isKg", isKg);
	
	    listItemTotal.add(mapTmp);
	    if (actualExportedQuantity > 0) {
	    	listItemTotalExport.add(mapTmp);
	   	} else if ("DLV_APPROVED".equals(statusId)){
	   		listItemTotalExport.add(mapTmp);
	   	}
    }
}
listPrIds = new ArrayList<String>();
for (DeliveryItemEntity t : listItemPromo) {
	if (!listPrIds.contains(t.getProductId())){
		listPrIds.add(t.getProductId());
	}
}
listItemDetail.addAll(listItemPromo);
for (String idTmp : listPrIds) {
	String prodNameTmp = "";
	String unitTmp = "";
	String isKg = "N";
	GenericValue prTmp = delegator.findOne("Product", UtilMisc.toMap("productId", idTmp), false);
	List<String> seqOis = FastList.newInstance();
	
	selectedAmounts = FastList.newInstance();
    for (DeliveryItemEntity item : listItemPromo){
   		isKg = item.getIsKg();
   		if ("Y".equals(isKg) && idTmp.equals(item.getProductId())) { 
			boolean check = false;
			BigDecimal sltAmout = item.getSelectedAmount();
			if (sltAmout != null) { 
				for(BigDecimal bd : selectedAmounts) {
				    if (bd.compareTo(sltAmout) == 0) {
				        check = true;
				    }
				} 
				if (!check) selectedAmounts.add(sltAmout);
			}
   		}
    }
    if (!selectedAmounts.isEmpty()) { 
    	for (BigDecimal selectedAm : selectedAmounts) { 
    		BigDecimal unitPriceTmp = BigDecimal.ZERO;
    		BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
			BigDecimal actualExportedQuantity = BigDecimal.ZERO;
			BigDecimal quantityCreated = BigDecimal.ZERO;
			BigDecimal totalTmp = BigDecimal.ZERO;
		    for (DeliveryItemEntity item : listItemPromo){
		    	String orderItemSeqId = item.getFromOrderItemSeqId();
		    	BigDecimal selectedAmTmp = item.getSelectedAmount();
		    	if (idTmp.equals(item.getProductId()) && selectedAmTmp.compareTo(selectedAm) == 0){
		    		isKg = item.getIsKg();
		    		if (item.getStatusId() == "DELI_ITEM_EXPORTED") {
			    		if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
			    		} else {
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
			    		}
					}
		    		if (item.getStatusId() == "DELI_ITEM_DELIVERED") {
			    		if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
			    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredAmount());
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
			    		} else {
			    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredQuantity());
			    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
			    		}
					}
					if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
						if (!seqOis.contains(orderItemSeqId)){ 
				    		quantityCreated = quantityCreated + item.getAmount();
				    	} 
					} else {
						if (!seqOis.contains(orderItemSeqId)){ 
							quantityCreated = quantityCreated + item.getQuantity();
						} 
					}
					unitPriceTmp = item.getUnitPrice();
					totalTmp = totalTmp.add(item.getTotal());
		    		prodNameTmp = item.getProductName();
		    		unitTmp = item.getUnit();
		    	}
		    	if (!seqOis.contains(orderItemSeqId)){ 
		    		seqOis.add(orderItemSeqId);
		    	}
		    }
		    Map<String, Object> mapTmp = FastMap.newInstance();
		    mapTmp.put("code", idTmp);
		    mapTmp.put("productName", prodNameTmp);
		    mapTmp.put("selectedAmount", selectedAm);
		    mapTmp.put("productId", prTmp.getString("productId"));
		    mapTmp.put("productCode", prTmp.getString("productCode"));
		    mapTmp.put("unit", unitTmp);
		    mapTmp.put("actualExportedQuantity", actualExportedQuantity);
		    mapTmp.put("actualDeliveredQuantity", actualDeliveredQuantity);
		    mapTmp.put("quantity", quantityCreated);
		    mapTmp.put("unitPrice", unitPriceTmp);
		    mapTmp.put("isPromo", "Y");
		    mapTmp.put("unitPriceWithTax", 0);
		    mapTmp.put("itemTotal", totalTmp);
		    mapTmp.put("isKg", isKg);
		
		    listItemTotal.add(mapTmp);
		    if (actualExportedQuantity > 0) {
		    	listItemTotalExport.add(mapTmp);
		   	} else if ("DLV_APPROVED".equals(statusId)){
		   		listItemTotalExport.add(mapTmp);
		   	}
	   	}
   	} else {
   		BigDecimal unitPriceTmp = BigDecimal.ZERO;
   		BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
		BigDecimal actualExportedQuantity = BigDecimal.ZERO;
		BigDecimal quantityCreated = BigDecimal.ZERO;
		BigDecimal totalTmp = BigDecimal.ZERO; 
   		for (DeliveryItemEntity item : listItemPromo){
	    	String orderItemSeqId = item.getFromOrderItemSeqId();
	    	if (idTmp.equals(item.getProductId())){
	    		isKg = item.getIsKg();
	    		if (item.getStatusId() == "DELI_ITEM_EXPORTED") {
		    		if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
		    		} else {
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
		    		}
				}
	    		if (item.getStatusId() == "DELI_ITEM_DELIVERED") {
		    		if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
		    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredAmount());
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedAmount());
		    		} else {
		    			actualDeliveredQuantity = actualDeliveredQuantity.add(item.getActualDeliveredQuantity());
		    			actualExportedQuantity = actualExportedQuantity.add(item.getActualExportedQuantity());
		    		}
				}
				if (UtilValidate.isNotEmpty(prTmp.get("requireAmount")) && "Y".equals(prTmp.getString("requireAmount"))){
					if (!seqOis.contains(orderItemSeqId)){ 
			    		quantityCreated = quantityCreated + item.getAmount();
			    	} 
				} else {
					if (!seqOis.contains(orderItemSeqId)){ 
						quantityCreated = quantityCreated + item.getQuantity();
					} 
				}
				unitPriceTmp = item.getUnitPrice();
				totalTmp = totalTmp.add(item.getTotal());
	    		prodNameTmp = item.getProductName();
	    		unitTmp = item.getUnit();
	    	}
	    	if (!seqOis.contains(orderItemSeqId)){ 
	    		seqOis.add(orderItemSeqId);
	    	}
	    }
	    Map<String, Object> mapTmp = FastMap.newInstance();
	    mapTmp.put("code", idTmp);
	    mapTmp.put("productName", prodNameTmp);
	    mapTmp.put("productId", prTmp.getString("productId"));
	    mapTmp.put("productCode", prTmp.getString("productCode"));
	    mapTmp.put("unit", unitTmp);
	    mapTmp.put("actualExportedQuantity", actualExportedQuantity);
	    mapTmp.put("actualDeliveredQuantity", actualDeliveredQuantity);
	    mapTmp.put("quantity", quantityCreated);
	    mapTmp.put("unitPrice", unitPriceTmp);
	    mapTmp.put("isPromo", "Y");
	    mapTmp.put("unitPriceWithTax", 0);
	    mapTmp.put("itemTotal", totalTmp);
	    mapTmp.put("isKg", isKg);
	
	    listItemTotal.add(mapTmp);
	    if (actualExportedQuantity > 0) {
	    	listItemTotalExport.add(mapTmp);
	   	} else if ("DLV_APPROVED".equals(statusId)){
	   		listItemTotalExport.add(mapTmp);
	   	}
   	}
}

// shipping address
String shippingAddress = null;
List<GenericValue> orderContactMechs = delegator.findList("OrderContactMech", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderHeader.orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), null, null, null, false);
if (!orderContactMechs.isEmpty()){
	String shippingContactMechId = orderContactMechs.get(0).getString("contactMechId");
	address3 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", shippingContactMechId), false);
	shippingAddress = address3.fullName;
}
grandTotal += total;

context.orderAdjustmentsPromoDelivery = orderAdjustmentsPromoDelivery;
context.allOrderAdjustmentsPromoDelivery = allOrderAdjustmentsPromoDelivery;
context.shippingAddress = shippingAddress;
context.statusId = statusId;
context.listTaxTotals = listTaxTotals;
context.total = total;
context.grandTotal = grandTotal;
context.listItem = listItemTotalExport;
context.listItemTotal = listItemTotal;
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