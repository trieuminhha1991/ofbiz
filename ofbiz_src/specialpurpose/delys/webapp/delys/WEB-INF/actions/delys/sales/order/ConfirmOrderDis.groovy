import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.service.ServiceUtil;

import com.sun.xml.internal.ws.resources.UtilMessages;

import javolution.util.FastMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.ofbiz.order.shoppingcart.ShoppingCart;

List<Map<String, Object>> listOrderItem = new ArrayList<Map<String, Object>>();
List<String> listWorkEffort = new ArrayList<String>();
List<Map<String, Object>> listItemAdjustment = new ArrayList<Map<String, Object>>();
boolean isFull = false;
if (orderItems != null) {
	/*
	 * DAProduct
	 * DAQuantity
	 * DAUnitPrice
	 * DAQuantityUomId
	 * DAAlternativeQuantity
	 * DAAlternativeUnitPrice
	 * DAAdjustment
	 * DAItemTotal
	 * */
	for (GenericValue orderItem : orderItems) {
		GenericValue itemType = orderItem.getRelatedOne("OrderItemType", false);
		
		String productId = "";
		String itemDescription = "";
		BigDecimal quantity = null;
		BigDecimal unitPrice = null;
		String quantityUomId = "";
		String quantityUomDescription = "";
		String expireDate = "";
		BigDecimal alternativeQuantity = null;
		BigDecimal alternativeUnitPrice = null;
		BigDecimal adjustment = null;
		BigDecimal subTotal = null;
		String productPromoId = "";
		String productPromoRuleId = "";
		String productPromoActionSeqId = "";
		if (orderItem.productId != null && orderItem.productId == "_?_") {
			itemDescription = orderItem.itemDescription;
			isFull = true;
		} else {
			if (orderItem.productId != null) {
				itemDescription = orderItem.productId + "-" + orderItem.itemDescription;
			} else {
				itemDescription = "<b>" + itemType.description + "</b> : " + orderItem.itemDescription;
			}
			if (orderItem.quantityUomId != null) {
				GenericValue quantityUom = delegator.findOne("Uom", ["uomId" : orderItem.quantityUomId], true);
				if (quantityUom != null) quantityUomDescription = quantityUom.get("description", locale);
			}
			if (orderItem.expireDate != null) {
				expireDate = UtilFormatOut.formatDateTime(orderItem.expireDate, "dd/MM/yyyy", locale, timeZone);
			}
			productId = orderItem.productId;
			quantity = orderItem.quantity;
			unitPrice = orderItem.unitPrice;
			quantityUomId = orderItem.quantityUomId;
			alternativeQuantity = orderItem.alternativeQuantity;
			alternativeUnitPrice = orderItem.alternativeUnitPrice;
			adjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
			subTotal = localOrderReadHelper.getOrderItemSubTotal(orderItem);
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
				String itemAdjStr = "<b><i>" + UtilProperties.getMessage("DelysAdminUiLabels", "DAAdjustment", locale) + "</i>:</b> ";
				itemAdjStr += localOrderReadHelper.getAdjustmentType(orderItemAdjustment) + ".&nbsp;";
				if (orderItemAdjustment.description != null) {
					itemAdjStr += ":&nbsp;" + orderItemAdjustment.get("description",locale) + ".&nbsp;";
				}
				if (orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX") {
					if (orderItemAdjustment.primaryGeoId != null) {
						primaryGeo = orderItemAdjustment.getRelatedOne("PrimaryGeo", true);
						if (primaryGeo.geoName != null) {
							itemAdjStr += "<b>" + UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale) + ":</b>&nbsp;" + primaryGeo.geoName + "[" + primaryGeo.abbreviation + "].&nbsp;";
						}
						if (orderItemAdjustment.secondaryGeoId != null) {
							secondaryGeo = orderItemAdjustment.getRelatedOne("SecondaryGeo", true);
							itemAdjStr += "(<b>in:</b> "+ secondaryGeo.geoName +"&nbsp;[" + secondaryGeo.abbreviation + "]).&nbsp;";
						}
					}
					if (orderItemAdjustment.sourcePercentage != null) {
						String template = "#,##0.###";
						sourcePercentageStr = UtilFormatOut.formatDecimalNumber(orderItemAdjustment.sourcePercentage.doubleValue(), template, locale);
						itemAdjStr += "<b>" + UtilProperties.getMessage("OrderUiLabels", "OrderRate", locale) + ":</b>&nbsp;" + sourcePercentageStr + "% &nbsp;";
					}
					if (orderItemAdjustment.customerReferenceId != null) itemAdjStr += "<b>" + UtilProperties.getMessage("OrderUiLabels", "OrderCustomerTaxId", locale) + ":</b> " + orderItemAdjustment.customerReferenceId + "&nbsp;";
					if (orderItemAdjustment.exemptAmount != null) itemAdjStr += "<b>" + UtilProperties.getMessage("OrderUiLabels", "OrderExemptAmount", locale) + ":</b> " + orderItemAdjustment.exemptAmount + "&nbsp;";
				}
				
				Map<String, Object> itemAdjMap = FastMap.newInstance();
				itemAdjMap.put("description", itemAdjStr);
				itemAdjMap.put("value", localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment));
				listItemAdjustment.add(itemAdjMap);
				
				// check is promo product
				if (isPromo) {
					if (orderItemAdjustment.orderAdjustmentTypeId == "PROMOTION_ADJUSTMENT" 
							&& orderItemAdjustment.productPromoId != null 
							&& orderItemAdjustment.productPromoRuleId != null 
							&& orderItemAdjustment.productPromoActionSeqId != null) {
						boolean isSearched = false;
						// duyet danh sach order item da them vao gio
						for (Map<String, Object> oItem : listOrderItem) {
							if (orderItem.productId != null 
										&& orderItem.productId == oItem.productId 
										&& orderItem.quantityUomId == oItem.quantityUomId 
										&& orderItemAdjustment.productPromoId == oItem.get("productPromoId") 
										&& orderItemAdjustment.productPromoRuleId == oItem.get("productPromoRuleId") 
										&& orderItemAdjustment.productPromoActionSeqId == oItem.get("productPromoActionSeqId")) {
								isSearched = true;
								
								// cong them vao item
								quantity = quantity.add(oItem.get("quantity"));
								alternativeQuantity = alternativeQuantity.add(oItem.get("alternativeQuantity"));
								//alternativeUnitPrice = alternativeUnitPrice.add(oItem.get("alternativeUnitPrice"));
								adjustment = adjustment.add(oItem.get("adjustment"));
								subTotal = subTotal.add(oItem.get("itemTotal"));
								oItem.put("quantity", quantity);
								oItem.put("alternativeQuantity", alternativeQuantity);
								oItem.put("alternativeUnitPrice", alternativeUnitPrice);
								oItem.put("adjustment", adjustment);
								oItem.put("itemTotal", subTotal);
								oItem.put("productPromoId", oItem.productPromoId);
								oItem.put("productPromoRuleId", oItem.productPromoRuleId);
								oItem.put("productPromoActionSeqId", oItem.productPromoActionSeqId);
							}
						}
						if (!isSearched) {
							isAdd = true;
							productPromoId = orderItemAdjustment.productPromoId;
							productPromoRuleId = orderItemAdjustment.productPromoRuleId;
							productPromoActionSeqId = orderItemAdjustment.productPromoActionSeqId;
						}
					}
				}
			}
		}
		
		if (isAdd) {
			Map<String, Object> itemMap = FastMap.newInstance();
			itemMap.put("isPromo", isPromo);
			itemMap.put("isFull", isFull);
			itemMap.put("productId", productId);
			itemMap.put("productName", "");
			itemMap.put("itemDescription", itemDescription);
			itemMap.put("quantity", quantity);
			itemMap.put("unitPrice", unitPrice);
			itemMap.put("quantityUomId", quantityUomId);
			itemMap.put("quantityUomDescription", quantityUomDescription);
			itemMap.put("expireDate", expireDate);
			itemMap.put("alternativeQuantity", alternativeQuantity);
			itemMap.put("alternativeUnitPrice", alternativeUnitPrice);
			itemMap.put("adjustment", adjustment);
			itemMap.put("itemTotal", subTotal);
			itemMap.put("productPromoId", productPromoId);
			itemMap.put("productPromoRuleId", productPromoRuleId);
			itemMap.put("productPromoActionSeqId", productPromoActionSeqId);
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
if (shoppingCart != null) {
	ShoppingCart cart = shoppingCart;
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
					GenericValue address = contactMech.getRelatedOne("PostalAddress", false);
					if (address != null) {
						String addressDescription = "";
						if (address.toName != null) addressDescription += "<b>" + UtilProperties.getMessage("DelysAdminUiLabels", "DATo", locale) + ":</b>&nbsp;" + address.toName + "<br />";
						if (address.attnName != null) addressDescription += "<b>" + UtilProperties.getMessage("CommonUiLabels", "CommonAttn", locale) + ":</b>&nbsp;" + address.attnName + "<br />";
						if (address.address1 != null) addressDescription += address.address1 + "<br />";
						if (address.address2 != null) addressDescription += address.address2 + "<br />";
						if (address.city != null) addressDescription += address.city;
						if (address.stateProvinceGeoId != null) addressDescription += "&nbsp;" + address.stateProvinceGeoId;
						if (address.postalCode != null) addressDescription += ", " + address.postalCode;
						sgItem.put("address", addressDescription);
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
							GenericValue productGV = delegator.findOne("Product", ["productId" : productIdName], true);
							if (productGV != null && productGV.quantityUomId != null) {
								GenericValue quantityUom = delegator.findOne("Uom", ["uomId" : productGV.quantityUomId], true);
								quantityUomDesc = quantityUom.get("description", locale);
							}
							BigDecimal shipItemInfoQuantity = cartShipInfo.getShipItemInfo(shipItem).getItemQuantity() ?: "0";
							shipItemMap.put("itemIndex", itemIndex);
							shipItemMap.put("productId", productIdName);
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

context.listOrderItem = listOrderItem;
context.listWorkEffort = listWorkEffort;
context.listItemAdjustment = listItemAdjustment;
context.isFull = isFull;
context.listShipGroup = listShipGroup;