import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilProperties;
import com.olbius.baselogistics.util.*;
import com.olbius.product.util.ProductUtil;
import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.olbius.basehr.util.PartyHelper;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.baselogistics.transfer.TransferReadHepler;

deliveryId = parameters.deliveryId;
delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);
deliveryType = delegator.findOne("DeliveryType", UtilMisc.toMap("deliveryTypeId", delivery.get("deliveryTypeId")), false);

String transferId = delivery.transferId;
String statusId = delivery.statusId;
String originContactMechId = delivery.getString("originContactMechId");
String destContactMechId = delivery.getString("destContactMechId");
String supplierAddress = null;
String originAddress = null;
String destAddress = null;

if (destContactMechId){ 
	address1 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", destContactMechId), false);
	if (address1 != null) { 
		destAddress = address1.getString("fullName");
	}
}
if (originContactMechId){ 
	address2 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", originContactMechId), false);
	if (address2 != null) { 
		originAddress = address2.getString("fullName");
	}
}

String originFacilityId = "";
if (delivery.getString("originFacilityId") != null){
	originFacilityId = delivery.getString("originFacilityId");
}          
originFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", originFacilityId));


String destFacilityId = "";
if (delivery.getString("destFacilityId") != null){
	destFacilityId = delivery.getString("destFacilityId");
}           
destFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", destFacilityId));

listShipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition([transferId : transferId]), null, null, null, false);
String shipmentMethodTypeId = null;
if (!listShipGroups.isEmpty()) { 
	shipmentMethodTypeId = listShipGroups.get(0).getString("shipmentMethodTypeId");
}

listItemTmps = delegator.findList("DeliveryItemTransferView", EntityCondition.makeCondition([deliveryId : deliveryId]), null, null, null, false);

List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
BigDecimal grandTotal = BigDecimal.ZERO;

String productAverageCostTypeId = "SIMPLE_AVG_COST";
String organizationPartyId = originFacility.getString("ownerPartyId");
List<String> listPrIds = EntityUtil.getFieldListFromEntityList(listItemTmps, "productId", true)
if (!listPrIds.isEmpty()) { 
	for (String productIdTmp : listPrIds) { 
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal actualExportedQuantity = BigDecimal.ZERO;
		BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
		Map<String, Object> map = FastMap.newInstance();
		BigDecimal itemTotal = BigDecimal.ZERO;
		BigDecimal unitCost = BigDecimal.ZERO;
		String unit = null;
		for (GenericValue x : listItemTmps) { 
			String productId = x.getString("productId");
			if (!productIdTmp.equals(productId)) continue;
			String statusItem = x.getString("statusId");
			if (!"DLV_CANCELLED".equals(statusId)) { 
				if ("DELI_ITEM_CANCELLED".equals(statusItem)) { 
					continue;
				}
			}
			String uomId = x.getString("quantityUomId");
			if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
				quantity = x.getBigDecimal("quantity");
			}
			if (UtilValidate.isNotEmpty(x.getBigDecimal("actualExportedQuantity"))) {
				actualExportedQuantity = actualExportedQuantity.add(x.getBigDecimal("actualExportedQuantity"));
			}
			if (UtilValidate.isNotEmpty(x.getBigDecimal("actualDeliveredQuantity"))) {
				actualDeliveredQuantity = actualDeliveredQuantity.add(x.getBigDecimal("actualDeliveredQuantity"));
			}
			
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
			unit = StringUtil.wrapString(uom.get("description", locale));
			
			if (ProductUtil.isWeightProduct(delegator, productId)) {
				uomId = x.getString("weightUomId");
				uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
				unit = StringUtil.wrapString(uom.get("abbreviation", locale));
				if (UtilValidate.isNotEmpty(x.getBigDecimal("amount"))) {
					quantity = x.getBigDecimal("amount");
				}
				if (UtilValidate.isNotEmpty(x.getBigDecimal("actualExportedAmount"))) {
					actualExportedQuantity = actualExportedQuantity.add(x.getBigDecimal("actualExportedAmount"));
				}
				if (UtilValidate.isNotEmpty(x.getBigDecimal("actualDeliveredAmount"))) {
					actualDeliveredQuantity = actualDeliveredQuantity.add(x.getBigDecimal("actualDeliveredAmount"));
				}
			}
			
			map.putAll(x);
		}
		
		if ("DLV_EXPORTED".equals(statusId) || "DLV_DELIVERED".equals(statusId)) { 
			unitCost = TransferReadHepler.getAverageCostProductExportedByDelivery(delegator, deliveryId, productIdTmp, originFacilityId, productAverageCostTypeId, organizationPartyId);
			itemTotal = unitCost.multiply(actualExportedQuantity);		
		} else { 
			unitCost = ProductUtil.getAverageCostByTime(delegator, productIdTmp, originFacilityId, productAverageCostTypeId, organizationPartyId, null);
			itemTotal = unitCost.multiply(quantity);	
		}
		
		map.put("unit", unit);
		map.put("quantity", quantity);
		map.put("actualExportedQuantity", actualExportedQuantity);
		map.put("actualDeliveredQuantity", actualDeliveredQuantity);
		map.put("unitCost", unitCost);
		map.put("itemTotal", itemTotal);
		
		grandTotal = grandTotal.add(itemTotal);
		listItems.add(map);
	}
}

context.originAddress = originAddress;
context.destAddress = destAddress;
context.originFacility = originFacility;
context.destFacility = destFacility;
context.listItems = listItems;
context.statusId = statusId;
context.grandTotal = grandTotal;
context.shipmentMethodTypeId = shipmentMethodTypeId;