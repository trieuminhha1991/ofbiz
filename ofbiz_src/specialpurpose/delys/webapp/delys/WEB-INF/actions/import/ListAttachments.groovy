import javolution.util.FastSet;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

String containerId = (String)parameters.containerId;
Set<String> fieldToSelects = FastSet.newInstance();
fieldToSelects.add("orderId");
List<GenericValue> listOrder = delegator.findList("OrderAndContainerAndAgreementAndOrderDetail", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), fieldToSelects, null, null, false);
fieldToSelects.clear();
fieldToSelects.add("orderId");
fieldToSelects.add("productId");
fieldToSelects.add("quantity");
fieldToSelects.add("unitPrice");
fieldToSelects.add("itemDescription");
fieldToSelects.add("statusId");
List<GenericValue> listOrderItems = new ArrayList<GenericValue>();
for (GenericValue z : listOrder) {
	String orderId = (String)z.get("orderId");
	List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), fieldToSelects, null, null, false);
	listOrderItems.addAll(listOrderItem);
}
List<Map> listInvoiceView = new ArrayList<Map>();
Set<String> listProductId = new HashSet<String>();

BigDecimal unitPrice = BigDecimal.ZERO;
BigDecimal quantity = BigDecimal.ZERO;
BigDecimal quantityTotal = BigDecimal.ZERO;
String internalName = "";
String weightUnit = "";
String currencyUnit = "";
String quantityUomId = "";
String weightUomId = "";
String currencyUom = "";
String brandName = "";
BigDecimal grandTotal = BigDecimal.ZERO;
BigDecimal grandTotalAll = BigDecimal.ZERO;
BigDecimal weight = BigDecimal.ZERO;
BigDecimal weightTotal = BigDecimal.ZERO;
BigDecimal productWeight = BigDecimal.ZERO;
BigDecimal productWeightTotal = BigDecimal.ZERO;
for (GenericValue x : listOrderItems) {
	Map<String, Object> result = new FastMap<String, Object>();
	quantity = (BigDecimal)x.get("quantity");
	unitPrice = (BigDecimal)x.get("unitPrice");
	String productId = (String)x.get("productId");
	listProductId.add(productId);
	GenericValue thisProduct = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	internalName = (String)thisProduct.get("internalName");
	quantityUomId = (String)thisProduct.get("quantityUomId");
	weightUomId = (String)thisProduct.get("weightUomId");
	brandName = (String)thisProduct.get("brandName");
	weight = (BigDecimal)thisProduct.get("weight");
	productWeight = (BigDecimal)thisProduct.get("productWeight");
	String orderId2 = (String)x.get("orderId");
	GenericValue thisProductQuantityUomId = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
	GenericValue thisProductWeightUomId = delegator.findOne("Uom", UtilMisc.toMap("uomId", weightUomId), false);
	GenericValue thisOrderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId2), false);
	quantityUomId = (String)thisProductQuantityUomId.get("description");
	weightUomId = (String)thisProductWeightUomId.get("description");
	if(thisOrderHeader != null){
		currencyUom = (String)thisOrderHeader.get("currencyUom");
		grandTotal = (BigDecimal)thisOrderHeader.get("grandTotal");
	}
	result.put("productWeight", productWeight);
	result.put("weight", weight);
	result.put("grandTotal", grandTotal);
	result.put("productId", productId);
	result.put("quantity", quantity);
	result.put("unitPrice", unitPrice);
	result.put("internalName", internalName);
	result.put("quantityUomId", quantityUomId);
	result.put("weightUomId", weightUomId);
	result.put("currencyUom", currencyUom);
	result.put("brandName", brandName);
	listInvoiceView.add(result);
}
List<Map> listInvoiceViews = new ArrayList<Map>();
List<String> listProductIds = new ArrayList<String>();
listProductIds.addAll(listProductId);
for (String s : listProductIds) {
	quantity = BigDecimal.ZERO;
	unitPrice = BigDecimal.ZERO;
	grandTotal = BigDecimal.ZERO;
	productWeight = BigDecimal.ZERO;
	weight = BigDecimal.ZERO;
	Map<String, Object> views = new FastMap<String, Object>();
	for (Map thisMap : listInvoiceView) {
		String thisProductId = thisMap.productId;
		if(s.equals(thisProductId)){
			quantity += thisMap.quantity;
			unitPrice += thisMap.unitPrice;
			internalName = thisMap.internalName;
			quantityUomId = thisMap.quantityUomId;
			weightUomId = thisMap.weightUomId;
			currencyUom = thisMap.currencyUom;
			brandName = thisMap.brandName;
			productWeight = thisMap.productWeight;
			weight = thisMap.weight;
			grandTotal += thisMap.grandTotal;
		}
	}
	if( weightUomId =="Kilogram"){
		weightUomId = "KG";
	}
	weight = weight*quantity;
	productWeight = productWeight*quantity;
	views.put("productWeight", productWeight);
	views.put("weight", weight);
	views.put("grandTotal", grandTotal);
	views.put("brandName", brandName);
	views.put("quantity", quantity);
	views.put("unitPrice", unitPrice);
	views.put("internalName", internalName);
	views.put("quantityUomId", quantityUomId);
	views.put("weightUomId", weightUomId);
	views.put("currencyUom", currencyUom);
	listInvoiceViews.add(views);
	grandTotalAll += grandTotal;
	quantityTotal += quantity;
	weightTotal += weight;
	productWeightTotal += productWeight;
	
	weightUnit = weightUomId;
	currencyUnit = currencyUom;
}
context.weightUnit = weightUnit;
context.currencyUnit = currencyUnit;
context.listOrder = listInvoiceViews;
context.grandTotalAll = grandTotalAll;
context.quantityTotal = quantityTotal;
context.weightTotal = weightTotal;
context.productWeightTotal = productWeightTotal;