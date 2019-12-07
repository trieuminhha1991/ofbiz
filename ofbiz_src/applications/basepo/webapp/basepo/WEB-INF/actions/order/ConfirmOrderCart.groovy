import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.service.ServiceUtil;

import com.olbius.product.util.ProductUtil;
import com.sun.xml.internal.ws.resources.UtilMessages;

import javolution.util.FastMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;

List<Map<String, Object>> listOrderItem = new ArrayList<Map<String, Object>>();
List<String> listWorkEffort = new ArrayList<String>();
List<Map<String, Object>> listItemAdjustment = new ArrayList<Map<String, Object>>();
boolean isFull = false;
if (orderItemsMap != null) {
	/*
	 * BSProduct
	 * BSQuantity
	 * BSUnitPrice
	 * BSQuantityUomId
	 * BSAlternativeQuantity
	 * BSAlternativeUnitPrice
	 * BSAdjustment
	 * BSItemTotal
	 * */
	for (Map<String, Object> orderItemMap : orderItemsMap) {
		GenericValue orderItem = orderItemMap.orderItemGeneric;
		GenericValue itemType = orderItem.getRelatedOne("OrderItemType", false);
		GenericValue product = orderItem.getRelatedOne("Product", false);
		
		String productId = "";
		String itemDescription = "";
		BigDecimal quantity = null;
		BigDecimal unitPrice = null;
		String quantityUomId = "";
		String weightUomId = "";
		String quantityUomDescription = "";
		String weightUomDescription = "";
		String expireDate = "";
		BigDecimal alternativeQuantity = null;
		BigDecimal alternativeUnitPrice = null;
		BigDecimal adjustment = null;
		BigDecimal subTotal = null;
		BigDecimal selectedAmount = null;
		String baseWeightUomId = null;
		String productPromoId = "";
		String requireAmount = "";
		String productPromoRuleId = "";
		String productPromoActionSeqId = "";
		String itemComment = "";
		int cartLineIndex = orderItemMap.cartLineIndex;
		
		if (orderItem.productId != null && orderItem.productId == "_?_") {
			itemDescription = orderItem.itemDescription;
			isFull = true;
		} else {
			if (orderItem.productId != null) {
				itemDescription = product.productCode + " - " + orderItem.itemDescription;
			} else {
				itemDescription = orderItem.itemDescription;
			}
			if (orderItem.quantityUomId != null) {
				GenericValue quantityUom = delegator.findOne("Uom", ["uomId" : orderItem.quantityUomId], true);
				if (quantityUom != null) quantityUomDescription = quantityUom.get("description", locale);
			}
			if (orderItem.weightUomId != null) {
				GenericValue weightUom = delegator.findOne("Uom", ["uomId" : orderItem.weightUomId], true);
				if (weightUom != null) weightUomDescription = weightUom.get("abbreviation", locale);
			}
			if (orderItem.expireDate != null) {
				expireDate = UtilFormatOut.formatDateTime(orderItem.expireDate, "dd/MM/yyyy", locale, timeZone);
			}
			productId = orderItem.productId;
			itemComment = orderItemMap.comments;
			quantity = orderItem.quantity;
			selectedAmount = orderItem.selectedAmount;
			unitPrice = orderItem.unitPrice;
			quantityUomId = orderItem.quantityUomId;
			weightUomId = orderItem.weightUomId;
			alternativeQuantity = orderItem.alternativeQuantity;
			alternativeUnitPrice = orderItem.alternativeUnitPrice;
			if (orderItem.isPromo.equals("Y"))
			{			 	
			 	adjustment =  localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
			 	subTotal = localOrderReadHelper.getOrderItemSubTotal(orderItem);
			}
			else 
			{
				adjustment =  BigDecimal.ZERO;
				//List<BigDecimal> orderAdjustmentss = new ArrayList<BigDecimal>();
				subTotal = localOrderReadHelper.getNewOrderItemSubTotal(orderItem);
			}			
			//adjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
			//subTotal = localOrderReadHelper.getOrderItemSubTotal(orderItem);
		}
		
		boolean isAdd = false;
		boolean isPromo = false;
		if ("Y" == orderItem.isPromo) {
			isPromo = true;
		} else {
			isAdd = true;
		}
		
		// now show adjustment details per line item - listItemAdjustment
		List<GenericValue> itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem);
		if (itemAdjustments != null && itemAdjustments.size() > 0) {
			for (orderItemAdjustment in itemAdjustments) {
				String itemAdjStr = "<b><i>" + UtilProperties.getMessage("BaseSalesUiLabels", "BSAdjustment", locale) + "</i>:</b> ";
				//itemAdjStr += localOrderReadHelper.getAdjustmentType(orderItemAdjustment) + ".&nbsp;";
				if (orderItemAdjustment.description != null) {
					itemAdjStr += "&nbsp;" + orderItemAdjustment.get("description", locale);
				} else if (orderItemAdjustment.comments != null) {
					itemAdjStr += "&nbsp;" + orderItemAdjustment.get("comments");
				}
			    boolean isFined = false;
				// duyet danh sach order item da them vao gio
				if (orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX") {
					for (Map<String, Object> oItemAdj : listItemAdjustment){
						if (orderItemAdjustment.taxAuthorityRateSeqId == oItemAdj.get("taxAuthorityRateSeqId")){
							isFined = true;
							value = localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment);
							value = value.add(oItemAdj.get("value"));
							oItemAdj.put("value", value);
						}
					}
				} else {
					for (Map<String, Object> oItemAdj : listItemAdjustment){
						if (orderItemAdjustment.productPromoId == oItemAdj.get("productPromoId") &&
							orderItemAdjustment.productPromoRuleId == oItemAdj.get("productPromoRuleId") &&
							orderItemAdjustment.productPromoActionSeqId == oItemAdj.get("productPromoActionSeqId")){
							isFined = true;
							value = localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment);
							value = value.add(oItemAdj.get("value"));
							oItemAdj.put("value", value);
						}
					}
				}
				
				if (!isFined){
					Map<String, Object> itemAdjMap = FastMap.newInstance();
					itemAdjMap.put("description", itemAdjStr);
					itemAdjMap.put("value", localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment));
					itemAdjMap.put("productPromoId", orderItemAdjustment.productPromoId );
					itemAdjMap.put("productPromoRuleId", orderItemAdjustment.productPromoRuleId );
					itemAdjMap.put("productPromoActionSeqId", orderItemAdjustment.productPromoActionSeqId );
					itemAdjMap.put("taxAuthorityRateSeqId", orderItemAdjustment.taxAuthorityRateSeqId );
					listItemAdjustment.add(itemAdjMap);
				}
			}
		}
        // check is promo product
        if (isPromo) {
            isAdd = true;
        }
		
		if (isAdd) {
			GenericValue pr = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			BigDecimal convertQuantityByAmount = selectedAmount;
			requireAmount = pr.getString("requireAmount");
			baseWeightUomId = pr.getString("weightUomId");
			baseQuantityUomId = pr.getString("quantityUomId");
			BigDecimal convertNumber = BigDecimal.ONE;
			if (weightUomId != null && !"".equals(weightUomId) && requireAmount != null && requireAmount == 'Y') {
				GenericValue conversion = null;
				conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomId, "uomIdTo", baseWeightUomId));
				if (conversion == null) {
					conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", baseWeightUomId, "uomIdTo", weightUomId));
				} 
				if (conversion != null) {
					convertQuantityByAmount = convertQuantityByAmount.divide(conversion.getBigDecimal("conversionFactor"));
				}
				convertNumber = BigDecimal.ONE;
			} else {
				if (quantityUomId != null && baseQuantityUomId != null) {
					convertNumber = ProductUtil.getConvertPackingNumber(delegator, pr.getString("productId"), quantityUomId, baseQuantityUomId);
				}
			}
			Map<String, Object> itemMap = FastMap.newInstance();
			itemMap.put("isPromo", isPromo);
			itemMap.put("isFull", isFull);
			itemMap.put("requireAmount", requireAmount);
			itemMap.put("productId", productId);
			itemMap.put("convertNumber", convertNumber);
			itemMap.put("productName", "");
			itemMap.put("itemDescription", itemDescription);
			itemMap.put("comments", itemComment);
			itemMap.put("quantity", quantity);
			itemMap.put("unitPrice", unitPrice);
			itemMap.put("quantityUomId", quantityUomId);
			itemMap.put("weightUomId", weightUomId);
			itemMap.put("baseWeightUomId", weightUomId);
			itemMap.put("quantityUomDescription", quantityUomDescription);
			itemMap.put("weightUomDescription", weightUomDescription);
			itemMap.put("expireDate", expireDate);
			itemMap.put("alternativeQuantity", alternativeQuantity);
			itemMap.put("convertQuantityByAmount", convertQuantityByAmount);
			itemMap.put("alternativeUnitPrice", alternativeUnitPrice);
			itemMap.put("adjustment", adjustment);
			itemMap.put("itemTotal", subTotal);
			itemMap.put("selectedAmount", selectedAmount);
			itemMap.put("productPromoId", productPromoId);
			itemMap.put("productPromoRuleId", productPromoRuleId);
			itemMap.put("productPromoActionSeqId", productPromoActionSeqId);
			itemMap.put("cartLineIndexes", ["" + cartLineIndex]);
			itemMap.put("alternativeOptionProductIds", orderItemMap.alternativeOptionProductIds);
			itemMap.put("orderItemTypeId", orderItem.orderItemTypeId);
			listOrderItem.add(itemMap);
		}
		// show info from workeffort if it was a rental item
		if (orderItem.orderItemTypeId != null && orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM") {
			List<GenericValue> workOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false);
			if (workOrderItemFulfillments != null) {
				GenericValue workOrderItemFulfillment = workOrderItemFulfillments.get(0);
				GenericValue workEffort =  workOrderItemFulfillment.getRelatedOne("WorkEffort", true);
				String workEffortStr = "" + UtilProperties.getMessage("CommonUiLabels", "CommonFrom", locale) + workEffort.estimatedStartDate 
											+ UtilProperties.getMessage("CommonUiLabels", "CommonTo", locale) + workEffort.estimatedCompletionDate 
											+ UtilProperties.getMessage("OrderUiLabels", "OrderNbrPersons", locale) + workEffort.reservPersons;
			}
		}
	}
}
List<Map<String, Object>> listShipGroup = new ArrayList<Map<String, Object>>();
if (cart != null) {
	List<CartShipInfo> cartShipInfos = cart.getShipGroups();
	if (cartShipInfos != null) {
		for (CartShipInfo cartShipInfo : cartShipInfos) {
			int numberOfItems = cartShipInfo.getShipItems().size();
			if (numberOfItems > 0) {
				// spacer goes here
				Map<String, Object> sgItem = FastMap.newInstance();
				sgItem.put("numberOfItems", numberOfItems);
				// td rowspan = numberOfItems | valign="top"
				// address destination column (spans a number of rows = number of cart items in it)
				GenericValue contactMech = delegator.findOne("ContactMech", ["contactMechId" : cartShipInfo.contactMechId], false);
				if (contactMech != null) {
					/*GenericValue address = contactMech.getRelatedOne("PostalAddress", false);
					if (address != null) {
						String addressDescription = "";
						if (address.toName != null) addressDescription += "<b>" + UtilProperties.getMessage("BaseSalesUiLabels", "BSReceiverName", locale) + ":</b>&nbsp;" + address.toName + "<br />";
						if (address.attnName != null) addressDescription += "<b>" + UtilProperties.getMessage("BaseSalesUiLabels", "BSOtherInfo", locale) + ":</b>&nbsp;" + address.attnName + "<br />";
						if (address.address1 != null) addressDescription += address.address1 + "<br />";
						if (address.address2 != null) addressDescription += address.address2 + "<br />";
						if (address.city != null) addressDescription += address.city;
						if (address.stateProvinceGeoId != null) addressDescription += "&nbsp;" + address.stateProvinceGeoId;
						if (address.postalCode != null) addressDescription += ", " + address.postalCode;
						sgItem.put("address", addressDescription);
					}*/
					GenericValue address = delegator.findOne("PostalAddressFullNameDetail", ["contactMechId": contactMech.contactMechId], false);
					if (address != null) {
						sgItem.put("address", address.getString("fullName"));
					}
				}
				
				// td rowspan = numberOfItems | valign="top"
				// supplier id (for drop shipments) (also spans rows = number of items)
				/* String supplierName = "";
				   GenericValue supplier =  delegator.findOne("PartyGroup", ["partyId" : cartShipInfo.getSupplierPartyId()], false);
				   if (supplier != null) {
					   supplierName = supplier.groupName;
				   } else {
					   supplierName = supplier.partyId;
				   } */
				// td rowspan = numberOfItems | valign="top"
				// carrier column (also spans rows = number of items)
				/*
				 * carrier = delegator.findOne("PartyGroup", ["partyId" : cartShipInfo.getCarrierPartyId()], false);
				 * method = delegator.findOne("ShipmentMethodType", ["shipmentMethodTypeId" : cartShipInfo.getShipmentMethodTypeId()], false);
				 * if (carrier != null) carrier.groupName else carrier.partyId
				 * if (method != null) method.description else method.shipmentMethodTypeId
				 * */
				
				// list each ShoppingCartItem in this group
				int itemIndex = 0;
				cartShipItems = cartShipInfo.getShipItems();
				if (cartShipItems != null) {
					List<Map<String, Object>> listShipItems = new ArrayList<Map<String, Object>>();
					for (shipItem in cartShipItems) {
						boolean isSearchedIItem = false;
						for (iitem in listShipItems) {
							if (shipItem.productId == iitem.productId) {
								isSearchedIItem = true;
								BigDecimal shipItemInfoQuantity = cartShipInfo.getShipItemInfo(shipItem).getItemQuantity() ?: "0";
								iitem.put("quantity", shipItemInfoQuantity.add(iitem.quantity));
							}
						}
						if (!isSearchedIItem) {
							Map<String, Object> shipItemMap = FastMap.newInstance();
							String productIdName = shipItem.getProductId() ?: "";
							String quantityUomDesc = "";
							//productIdName += " - ";
							//productIdName += shipItem.getName() ?: "";
							String productCode = "";
							GenericValue productGV = delegator.findOne("Product", ["productId" : productIdName], true);
							if (productGV != null && productGV.quantityUomId != null) {
								GenericValue quantityUom = delegator.findOne("Uom", ["uomId" : productGV.quantityUomId], true);
								quantityUomDesc = quantityUom.get("description", locale);
								productCode = productGV.productCode;
							}
							BigDecimal shipItemInfoQuantity = cartShipInfo.getShipItemInfo(shipItem).getItemQuantity() ?: "0";
							shipItemMap.put("itemIndex", itemIndex);
							shipItemMap.put("productId", productIdName);
							shipItemMap.put("productCode", productCode);
							shipItemMap.put("quantity", shipItemInfoQuantity);
							shipItemMap.put("quantityUomDesc", quantityUomDesc);
							listShipItems.add(shipItemMap);
							itemIndex = itemIndex + 1;
						}
					}
					sgItem.put("productShipItems", listShipItems);
				}
				sgItem.put("numberOfItems", itemIndex + 1);
				listShipGroup.add(sgItem);
			}
		}
	}
}

println("listOrderItem" + listOrderItem);
context.listOrderItem = listOrderItem;
context.listWorkEffort = listWorkEffort;
context.listItemAdjustment = listItemAdjustment;
context.isFull = isFull;
context.listShipGroup = listShipGroup;listOrderItem = listOrderItem;
context.listWorkEffort = listWorkEffort;
context.listItemAdjustment = listItemAdjustment;
context.isFull = isFull;
context.listShipGroup = listShipGroup;
context.shipToCustomerPartyId = cart.shipToCustomerPartyId