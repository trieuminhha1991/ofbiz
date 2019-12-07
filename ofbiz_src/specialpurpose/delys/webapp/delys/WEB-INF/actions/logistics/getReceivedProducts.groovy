import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.minilang.method.conditional.ElseIf;
import org.ofbiz.service.ServiceUtil;

String facilityId = (String)context.get("facilityId");
userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
List<GenericValue> listProducts = new ArrayList<GenericValue>();
List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
parameters.put("orderTypeId", "PURCHASE_ORDER")
Map<String, Object> result = dispatcher.runSync("performFind", UtilMisc.<String, Object>toMap("entityName", "OrderItemTotal"
	,"inputFields", parameters, "userLogin", userLogin));
EntityListIterator listIt = (EntityListIterator) result.get("listIt");

if (listIt != null){
	listItems = listIt.getCompleteList();
	listIt.close();
	if (!listItems.isEmpty()){
		listProductTmps.addAll(listItems);
	}	
}

List<String> productIds = new ArrayList<String>();
if (!listProductTmps.isEmpty()){
	for (GenericValue product : listProductTmps){
		if (!productIds.contains(product.get("productId"))){
			productIds.add(product.get("productId"));	
		}
	}
}
List<GenericValue> listShipmentTransInternals = new ArrayList<GenericValue>();
List<GenericValue> listShipmentTransIntemediarys = new ArrayList<GenericValue>();
List<GenericValue> listShipmentTransDistributors = new ArrayList<GenericValue>();
listShipmentTransInternals = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentTypeId", "TRANS_INTERNAL")), null, null, null, false);
listShipmentTransIntemediarys = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentTypeId", "TRANS_INTERMEDIARY")), null, null, null, false);
listShipmentTransDistributors = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentTypeId", "TRANS_DISTRIBUTOR")), null, null, null, false);
if (!productIds.isEmpty()){
	for (String productId : productIds){
		GenericValue productTmp = null;
		productTmp = delegator.makeValue("ProductAndOrderQuantity");
		productTmp.put("productId", productId);
		for (GenericValue product : listProductTmps){
			if (productId.equals((String)product.get("productId"))){
				if ("PURCHASE_ORDER".equals(product.get("orderTypeId"))){
					BigDecimal quantityPurchase = productTmp.getBigDecimal("quantityPurchase");
					if (quantityPurchase != null){
						quantityPurchase = quantityPurchase.add(product.getBigDecimal("quantity"));
					} else {
						quantityPurchase = product.getBigDecimal("quantity");
					}
					productTmp.put("quantityPurchase", quantityPurchase);
				}
			}
		}
		if (!listShipmentTransIntemediarys.isEmpty()){
			
		}
		listProducts.add(productTmp);
	}
}
context.listProducts = listProducts;