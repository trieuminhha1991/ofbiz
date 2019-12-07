import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.order.*;

import com.sun.xml.internal.ws.resources.UtilMessages;

import javolution.util.FastMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.ofbiz.order.shoppingcart.ShoppingCart;

List<Map<String, Object>> listOrderItem = new ArrayList<Map<String, Object>>();
List<Map<String, Object>> listItemAdjustment = new ArrayList<Map<String, Object>>();
boolean isFull = false;
orderItems = shoppingCart.makeOrderItems();
orderAdjustments = shoppingCart.makeAllAdjustments();
localOrderReadHelper = new OrderReadHelper(orderAdjustments, orderItems);

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
				itemDescription = orderItem.productId + " - " + orderItem.itemDescription;
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
				// if (orderItemAdjustment.orderAdjustmentTypeId != "SALES_TAX" && orderItemAdjustment.orderAdjustmentTypeId != "PROMOTION_ADJUSTMENT"){
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
promoCodes = shoppingCart.getProductPromoCodesEntered()
context.promoCode = promoCodes;
context.listOrderItem = listOrderItem;
context.isFull = isFull;
