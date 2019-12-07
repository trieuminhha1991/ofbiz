/**
 * Copy from OrderViewSimple.groovy and OrderViewGeneral.groovy
 */

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.order.OrderContentWrapper;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.party.party.PartyHelper;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastList;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.olbius.basesales.order.OrderReadHelper;
import com.olbius.basesales.order.OrderWorker;
import com.olbius.basesales.util.SalesUtil;

orderId = parameters.orderId;
context.orderId = orderId;

def partyId = null;
orderHeader = null;
orderItems = null;
orderAdjustments = null;
orderItemList = null;
cmvm = null;
GenericValue billingCustomerParty;

if (orderId) {
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
}
if (orderHeader) {
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	
	// is modified
	List<GenericValue> listOrderItemPromo = delegator.findByAnd("OrderItem", ["orderId": orderId, "isPromo": "Y"], null, false);
	orderHeaderAdjustments = orderReadHelper.getNewOrderHeaderAdjustments(listOrderItemPromo);
	orderSubTotal = orderReadHelper.getNewOrderItemsSubTotal();
	// end new --
	orderTerms = orderHeader.getRelated("OrderTerm", null, null, false);
	
	context.orderHeader = orderHeader;
	context.orderReadHelper = orderReadHelper;
	context.orderItems = orderItems;
	context.orderAdjustments = orderAdjustments;
	context.orderHeaderAdjustments = orderHeaderAdjustments;
	context.orderSubTotal = orderSubTotal;
	context.currencyUomId = orderReadHelper.getCurrency();
	context.orderTerms = orderTerms;
	
	
	// get the order type
	orderType = orderHeader.orderTypeId;
	context.orderType = orderType;
	
	// get the display party
	displayParty = null;
	if ("PURCHASE_ORDER".equals(orderType)) {
		displayParty = orderReadHelper.getSupplierAgent();
	} else {
		displayParty = orderReadHelper.getPlacingParty();
	}
	if (displayParty) {
		partyId = displayParty.partyId;
		context.displayParty = displayParty;
		context.partyId = partyId;

		//paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, displayParty.partyId, false);
		//context.paymentMethodValueMaps = paymentMethodValueMaps;
	}

	consignee=null
	if("SALES_ORDER".equals(orderType)){
		consignee=orderReadHelper.getShipToParty();
		context.consignee = consignee;
	}
	// get billing customer
	billingCustomerParty = orderReadHelper.getBillToParty();
	
	otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);
	context.otherAdjAmount = otherAdjAmount;
	
	shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
	shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
	context.shippingAmount = shippingAmount;
	
	taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	context.taxAmount = taxAmount;
	
	//grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	grandTotal = OrderReadHelper.getNewOrderGrandTotal(orderItems, orderReadHelper.getNewAdjustments());
	context.grandTotal = grandTotal;
	
	// TODOCHANGE
	orderItemList = orderReadHelper.getOrderItems();
	if (!"ORDER_CANCELLED".equals(orderHeader.statusId)) {
		// Retrieve all non-promo items that aren't cancelled
		orderItemList = orderReadHelper.getOrderItems().findAll { item ->
			//(item.isPromo == null || item.isPromo == 'N')  && !(item.statusId.equals('ITEM_CANCELLED'))
			!(item.statusId.equals('ITEM_CANCELLED'))
		}
		context.orderItemList = orderItemList;
	} else {
		// Retrieve all non-promo items that aren't cancelled
		context.orderItemList = orderReadHelper.getOrderItems();
	}
	
	// REMOVE ...
	
	// MODULE ACC
	if (security.hasPermission("PMN_SOVER_VIEW", session)) {
		// get a list of all invoices
		orderBilling = delegator.findByAnd("OrderItemBilling", [orderId : orderId], ["invoiceId"], false);
		context.invoices = orderBilling*.invoiceId.unique();
        List<GenericValue> paymentAppls = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, context.invoices), null, null, null, false)
        List<GenericValue> paymentListAppl = FastList.newInstance()
        BigDecimal amountPaid = BigDecimal.ZERO;
        for(GenericValue paymentAppl : paymentAppls) {
            GenericValue payment = paymentAppl.getRelatedOne("Payment", false);
            paymentListAppl.add(payment)
            if("PMNT_CONFIRMED".equals(payment.getString("statusId"))) amountPaid = amountPaid.add(paymentAppl.getBigDecimal("amountApplied"))
        }
        context.amountPaid = amountPaid
        context.paymentListAppl = paymentListAppl
        System.println("context.invoices = " + context.invoices);
		
		ecl = EntityCondition.makeCondition([
			EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
			EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")],
		EntityOperator.AND);
		orderPaymentPreferences = delegator.findList("OrderPaymentPreference", ecl, null, null, null, false);
		context.orderPaymentPreferences = orderPaymentPreferences;
	}
	// end MODULE ACC
	
	// REMOVE ...
	
	statusChange = delegator.findByAnd("StatusValidChange", [statusId : orderHeader.statusId], null, false);
	context.statusChange = statusChange;
	
	currentStatus = orderHeader.getRelatedOne("StatusItem", false);
	context.currentStatus = currentStatus;
	
	orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
	context.orderHeaderStatuses = orderHeaderStatuses;
	
	if (userLogin) {
		isPrivateOrg = true;
	} else {
		isPrivateOrg = false;
	}
	if (isPrivateOrg) {
		notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId], ["-noteDateTime"], false);
		context.orderNotes = notes;
	} else {
		notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId, internalNote : 'N'], ["-noteDateTime"], false);
		context.orderNotes = notes;
	}
	
	cmvm = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
	context.orderContactMechValueMaps = cmvm;
	//context.orderContactMechValueMaps = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
	
	// get inventory summary with respect to facility
	context.productStore = orderReadHelper.getProductStore();
	
	// QUANTITY: get the returned quantity by order item map
	/* TODO comment: not necessary in this case - need*/
	context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();
	
	if (orderItems) {
		orderItem = EntityUtil.getFirst(orderItems);
		context.orderItem = orderItem;
	}
	
	context.desiredDeliveryDate = orderHeader.getTimestamp("estimatedDeliveryDate");
	context.shipAfterDate = orderHeader.getTimestamp("shipAfterDate");
	context.shipBeforeDate = orderHeader.getTimestamp("shipBeforeDate");
}

// MODULE ACC
workEffortId = parameters.workEffortId;
assignPartyId = parameters.partyId;
assignRoleTypeId = parameters.roleTypeId;
fromDate = parameters.fromDate;
delegate = parameters.delegate;
if (delegate && fromDate) {
	fromDate = parameters.toFromDate;
}
paramString = "";
if (orderId) paramString += "orderId=" + orderId;
if (workEffortId) paramString += "&workEffortId=" + workEffortId;
if (assignPartyId) paramString += "&partyId=" + assignPartyId;
if (assignRoleTypeId) paramString += "&roleTypeId=" + assignRoleTypeId;
if (fromDate) paramString += "&fromDate=" + fromDate;
context.paramString = paramString;
// end MODULE ACC

List<Map<String, Object>> listItemLine = new ArrayList<Map<String, Object>>();
BigDecimal taxTotalOrderItems = BigDecimal.ZERO;
BigDecimal subAmountExportOrder = BigDecimal.ZERO;
BigDecimal subAmountExportInvoice = BigDecimal.ZERO;
List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();

//process to group orderItem
boolean hasPromoSettlement = false;
if (orderItemList) {
	for (GenericValue orderItem : orderItemList) {
		if (!hasPromoSettlement && "PRODPROMO_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) hasPromoSettlement = true;
		
		GenericValue orderItemType = orderItem.getRelatedOne("OrderItemType", false);
		//OrderContentWrapper orderItemContentWrapper = OrderContentWrapper.makeOrderContentWrapper(orderItem, request);
		//List<GenericValue> orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem);
		//if (orderHeader.orderTypeId == "SALES_ORDER") {
		//	BigDecimal pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
		//}
		GenericValue product = orderItem.getRelatedOne("Product", false);
		
		String productId = orderItem.getString("productId");
		String seqId = orderItem.getString("orderItemSeqId");
		String itemDescription = orderItem.getString("itemDescription");;
		String supplierProductId = orderItem.getString("supplierProductId");
		
		GenericValue barcodeGV = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", ["goodIdentificationTypeId" : "SKU", "productId" : productId, "uomId" : orderItem.quantityUomId], null, false));
		String barcode = barcodeGV != null ? barcodeGV.idValue : null;
		
		Timestamp expireDate = orderItem.getTimestamp("expireDate");
		
		// check order item is OLD VERSION or NEW VERSION with alternative quantity by quantity uom id
		boolean isNormal = true; // OLD VERSION
		BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
		BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
		if (alternativeQuantity != null && alternativeUnitPrice != null) {
			isNormal = false;
		}
		
		String quantityUomDescription = "";
		GenericValue quantityUomGV = delegator.findOne("Uom", ["uomId" : orderItem.quantityUomId], false);
		if (quantityUomGV != null) {
			quantityUomDescription = quantityUomGV.description != null ? quantityUomGV.description : quantityUomGV.uomId;
		}
		
		BigDecimal quantity = null;
		BigDecimal unitPrice = null;
		BigDecimal baseQuantity = orderItem.quantity;
		BigDecimal basePrice = orderItem.unitPrice;
		if (isNormal) {
			quantity = orderItem.quantity;
			unitPrice = orderItem.unitPrice;
		} else {
			quantity = orderItem.alternativeQuantity;
			unitPrice = orderItem.alternativeUnitPrice;
		}
		
		boolean isPromo = false;
		boolean isAdd = false;
		BigDecimal adjustment;
		BigDecimal subTotalBeVAT;
		BigDecimal adjustmentOnlyPromo;
		if ("Y".equals(orderItem.isPromo)) {
			isPromo = true;
			
			//adjustment =  OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false);
			//subTotalBeVAT = OrderReadHelper.getNewOrderItemSubTotal(orderItem); // Modify by VietTB
			
			adjustment = OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false);
			subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments);
		} else {
			isAdd = true;
			
			adjustment =  BigDecimal.ZERO;
			subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, new ArrayList<GenericValue>());
			adjustmentOnlyPromo = OrderReadHelper.getOrderItemAdjustmentsPromoTotal(orderItem, orderAdjustments)
		}

		// BigDecimal subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments);
		if (orderItem.statusId != "ITEM_CANCELLED") { // Modify by VietTB
			subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
		} else {
			subAmountExportOrder = subAmountExportOrder.add(subTotalBeVAT);
		}
		
		// Unit price after VAT
		BigDecimal unitPriceInvoiceAfVAT = null;
		BigDecimal subTotalInvoiceExport = null;
		List<GenericValue> listProductPriceInvoice = delegator.findByAnd("ProductPrice", ["productId" : productId, "productPriceTypeId" : "INVOICE_PRICE_GT"], null, false);
		listProductPriceInvoice = EntityUtil.filterByDate(listProductPriceInvoice);
		GenericValue productPriceInvoiceGeneric = EntityUtil.getFirst(listProductPriceInvoice);
		if (productPriceInvoiceGeneric != null) {
			unitPriceInvoiceAfVAT = productPriceInvoiceGeneric.getBigDecimal("price");
			if (subTotalBeVAT.compareTo(BigDecimal.ZERO) != 0) {
				subTotalInvoiceExport = quantity.multiply(unitPriceInvoiceAfVAT);
			}
		}
		
		if (subTotalInvoiceExport != null) {
			subAmountExportInvoice = subAmountExportInvoice.add(subTotalInvoiceExport);
		}
		
		/* Caculate tax prices sum from order items */
		String productPromoId = "";
		String productPromoRuleId = "";
		String productPromoActionSeqId = "";
		List<GenericValue> orderItemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orderAdjustments);
		if (orderItemAdjustments) {
			for (GenericValue itemAdjustment : orderItemAdjustments) {
				GenericValue adjustmentType = itemAdjustment.getRelatedOne("OrderAdjustmentType", true);
				if ("SALES_TAX".equals(adjustmentType.orderAdjustmentTypeId)) {
					BigDecimal taxValue = OrderReadHelper.calcItemAdjustment(itemAdjustment, orderItem);
					if (taxValue != null) taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
					
					// check and add item in list
					boolean isExists = false;
					for (Map<String, Object> taxTotalItem : listTaxTotal) {
						if (taxTotalItem.sourcePercentage == itemAdjustment.sourcePercentage) {
							// is exists in list stored, then add value to item existed
							BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
							amount = amount.add(itemAdjustment.getBigDecimal("amount"));
							taxTotalItem.put("amount", amount);
							if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
								BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
								taxTotalItem.put("amountForIXP", amountForInvoicePrice);
							}
							isExists = true;
						}
					}
					
					if (!isExists) {
						// item is not exists in list
						Map<String, Object> taxTotalItemNew = FastMap.newInstance();
						BigDecimal amount = itemAdjustment.getBigDecimal("amount");
						taxTotalItemNew.put("sourcePercentage", itemAdjustment.getBigDecimal("sourcePercentage"));
						taxTotalItemNew.put("amount", amount);
						if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
							BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
							taxTotalItemNew.put("amountForIXP", amountForInvoicePrice);
						}
						// add description in first (only 1 times)
						String description = UtilProperties.getMessage("OrderUiLabels", "OrderAdjustment", locale);
						description += " " + adjustmentType.get("description",locale);
						if (itemAdjustment.description != null) description += itemAdjustment.get("description",locale);
						if (itemAdjustment.comments != null) description += " (" + itemAdjustment.comments + "). ";
						if (itemAdjustment.productPromoId != null) {
							description += "<a class='btn btn-mini btn-primary' href='/catalog/control/EditProductPromo?productPromoId=" + itemAdjustment.productPromoId + externalKeyParam + "'>";
							description += itemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName") + "</a>";
						}
						String descriptionLog = description;
						if (itemAdjustment.primaryGeoId != null) {
							GenericValue primaryGeo = itemAdjustment.getRelatedOne("PrimaryGeo", true);
							if (primaryGeo.geoName != null) {
								String orderJurisdictionStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += " " + orderJurisdictionStr + " " + primaryGeo.geoName + " [" + primaryGeo.abbreviation + "]. ";
							}
							if (itemAdjustment.secondaryGeoId != null) {
								GenericValue secondaryGeo = itemAdjustment.getRelatedOne("SecondaryGeo", true);
								String commonInStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += " " + commonInStr + " " + secondaryGeo.geoName + " [" + secondaryGeo.abbreviation + "]). ";
							}
						}
						if (itemAdjustment.sourcePercentage != null) {
							String orderRateStr = UtilProperties.getMessage("OrderUiLabels", "OrderRate", locale);
							String template = "#,##0.###";
							sourcePercentageStr = UtilFormatOut.formatDecimalNumber(itemAdjustment.sourcePercentage.doubleValue(), template, locale);
							description += " " + orderRateStr + " " + sourcePercentageStr + "%"; //?string("0.######");
							descriptionLog += " " + orderRateStr + " " + sourcePercentageStr + "%"; //?string("0.######");
						}
						if (itemAdjustment.customerReferenceId != null) {
							String orderCustomerTaxIdStr = UtilProperties.getMessage("OrderUiLabels", "OrderCustomerTaxId", locale);
							description += " " + orderCustomerTaxIdStr + " " + itemAdjustment.customerReferenceId;
						}
						if (itemAdjustment.exemptAmount != null) {
							String orderExemptAmountStr = UtilProperties.getMessage("OrderUiLabels", "OrderExemptAmount", locale);
							description += " " + orderExemptAmountStr + " " + itemAdjustment.exemptAmount;
						}
						taxTotalItemNew.put("description", description);
						taxTotalItemNew.put("descriptionLog", descriptionLog);
						listTaxTotal.add(taxTotalItemNew);
					}
				}
				
				// item is promo product
				if (isPromo) {
					if ("PROMOTION_ADJUSTMENT".equals(itemAdjustment.orderAdjustmentTypeId)) {
						if (itemAdjustment.productPromoId != null && itemAdjustment.productPromoRuleId != null && itemAdjustment.productPromoActionSeqId != null) {
							boolean isSearched = false;
							// duyet danh sach order item da them vao gio
							for (Map<String, Object> oItem : listItemLine) {
								if (orderItem.productId != null
											&& orderItem.productId == oItem.productId
											&& orderItem.quantityUomId == oItem.quantityUomId
											&& itemAdjustment.productPromoId == oItem.get("productPromoId")
											&& itemAdjustment.productPromoRuleId == oItem.get("productPromoRuleId")
											&& itemAdjustment.productPromoActionSeqId == oItem.get("productPromoActionSeqId")) {
									isSearched = true;
									
									BigDecimal oquantity = BigDecimal.ZERO; //oItem.get("quantity");
									BigDecimal oadjustment = oItem.get("adjustment");
									BigDecimal osubTotalBeVAT = oItem.get("subTotalBeVAT");
									if (oItem.isNormal) {
										oquantity = orderItem.quantity;
										unitPrice = orderItem.unitPrice;
									} else {
										oquantity = orderItem.alternativeQuantity;
										unitPrice = orderItem.alternativeUnitPrice;
									}
									
									// cong them vao item
									oquantity = oquantity.add(oItem.get("quantity"));
									oadjustment = oadjustment.add(adjustment);
									osubTotalBeVAT = osubTotalBeVAT.add(subTotalBeVAT);
									//oadjustment = oadjustment.add(oItem.get("adjustment"));
									//osubTotalBeVAT = osubTotalBeVAT.add(oItem.get("subTotalBeVAT"));
									oItem.put("quantity", oquantity);
									oItem.put("adjustment", oadjustment);
									oItem.put("subTotalBeVAT", osubTotalBeVAT);
									oItem.put("productPromoId", oItem.productPromoId);
									oItem.put("productPromoRuleId", oItem.productPromoRuleId);
									oItem.put("productPromoActionSeqId", oItem.productPromoActionSeqId);
								}
							}
							if (!isSearched) {
								isAdd = true;
								productPromoId = itemAdjustment.productPromoId;
								productPromoRuleId = itemAdjustment.productPromoRuleId;
								productPromoActionSeqId = itemAdjustment.productPromoActionSeqId;
							}
						} else if (itemAdjustment.productPromoId == null && itemAdjustment.productPromoRuleId == null && itemAdjustment.productPromoActionSeqId == null) {
							isAdd = true;
						}
					}
				}
			}
		}
		
		if (isAdd) {
			Map<String, Object> itemLine = FastMap.newInstance();
			itemLine.put("seqId", seqId);
			itemLine.put("productId", productId);
			itemLine.put("productCode", product.getString("productCode"));
			itemLine.put("productName", product.getString("productName"));
			itemLine.put("barcode", barcode);
			itemLine.put("expireDate", expireDate);
			itemLine.put("quantityUomId", orderItem.getString("quantityUomId"));
			itemLine.put("quantityUomDescription", quantityUomDescription);
			itemLine.put("quantity", quantity);
			itemLine.put("unitPriceBeVAT", unitPrice); //before VAT
			itemLine.put("baseUnitPriceBeVAT", basePrice); //before VAT
			itemLine.put("adjustmentOnlyPromo", adjustmentOnlyPromo);
			itemLine.put("adjustment", adjustment);
			itemLine.put("subTotalBeVAT", subTotalBeVAT);
			itemLine.put("invoicePrice", unitPriceInvoiceAfVAT);
			itemLine.put("invoiceSubTotal", subTotalInvoiceExport);
			itemLine.put("itemDescription", itemDescription);
			itemLine.put("supplierProductId", supplierProductId);
			itemLine.put("product", product);
			itemLine.put("orderItemType", orderItemType);
			if (isPromo) {
				itemLine.put("isPromo", "Y");
			} else {
				itemLine.put("isPromo", "N");
			}
			itemLine.put("productPromoId", productPromoId);
			itemLine.put("productPromoRuleId", productPromoRuleId);
			itemLine.put("productPromoActionSeqId", productPromoActionSeqId);
			itemLine.put("isNormal", isNormal);
			itemLine.put("comments", orderItem.comments);
			itemLine.put("orderItem", orderItem);
			itemLine.put("orderItemTypeId", orderItem.orderItemTypeId);
			itemLine.put("baseQuantity", baseQuantity);
			itemLine.put("basePrice", basePrice);
			listItemLine.add(itemLine);
		}
	}
}

if (orderHeader) {
	GenericValue estimateDistanceDeliveryGV = delegator.findOne("OrderAttribute", ["orderId" : orderHeader.orderId, "attrName" : "estimateDistanceDelivery"], false);
	if (estimateDistanceDeliveryGV) context.estimateDistanceDelivery = new BigDecimal(estimateDistanceDeliveryGV.attrValue);
	context.totalTaxOrderItemPromo = OrderWorker.getTotalTaxOrderItemPromo(delegator, orderHeader.orderId);
	
	listOrderRoleTypeId = ["SALES_EXECUTIVE", "CALLCENTER_EMPL", "SALESADMIN_EMPL"];
	List<GenericValue> listOrderRole = delegator.findList("OrderRole",
		EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", orderHeader.orderId), EntityOperator.AND,
			EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listOrderRoleTypeId)), null, null, null, false);
	if (listOrderRole) {
		List<GenericValue> listSalesExecutive = EntityUtil.filterByAnd(listOrderRole, UtilMisc.toMap("roleTypeId", "SALES_EXECUTIVE"));
		if (UtilValidate.isNotEmpty(listSalesExecutive)) {
			GenericValue salesExecutive = listSalesExecutive.get(0);
			GenericValue salesExecutiveName = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", salesExecutive.getString("partyId")), false);
			if (salesExecutiveName != null) {
				String salesExecutiveNameStr = salesExecutiveName.getString("fullName");
				if (UtilValidate.isNotEmpty(salesExecutiveNameStr)) {
					context.salesExecutiveFullName = salesExecutiveNameStr;
					context.salesExecutiveId = salesExecutiveNameStr + " [" + salesExecutiveName.getString("partyCode") + "]";
				} else {
					context.salesExecutiveFullName = salesExecutive.getString("partyId");
					context.salesExecutiveId = salesExecutive.getString("partyId");
				}
			}
		}
		List<GenericValue> listCallcenter = EntityUtil.filterByAnd(listOrderRole, UtilMisc.toMap("roleTypeId", "CALLCENTER_EMPL"));
		if (UtilValidate.isNotEmpty(listCallcenter)) {
			GenericValue callcenter = listCallcenter.get(0);
			GenericValue callcenterName = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", callcenter.getString("partyId")), false);
			if (callcenterName != null) {
				String callcenterNameStr = callcenterName.getString("fullName");
				if (UtilValidate.isNotEmpty(callcenterNameStr)) {
					context.callcenterFullName = callcenterNameStr;
					context.callcenterId = callcenterNameStr + " [" + callcenterName.getString("partyCode") + "]";
				} else {
					context.callcenterFullName = callcenter.getString("partyId");
					context.callcenterId = callcenter.getString("partyId");
				}
			}
		} else {
			List<GenericValue> listSalesAdmin = EntityUtil.filterByAnd(listOrderRole, UtilMisc.toMap("roleTypeId", "SALESADMIN_EMPL"));
			if (UtilValidate.isNotEmpty(listSalesAdmin)) {
				GenericValue salesAdmin = listSalesAdmin.get(0);
				GenericValue salesAdminName = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", salesAdmin.getString("partyId")), false);
				if (salesAdminName != null) {
					String callcenterNameStr = salesAdminName.getString("fullName");
					if (UtilValidate.isNotEmpty(callcenterNameStr)) {
						context.salesadminFullName = callcenterNameStr;
						context.salesadminId = callcenterNameStr + " [" + salesAdminName.getString("partyCode") + "]";
					} else {
						context.salesadminFullName = salesAdmin.getString("partyId");
						context.salesadminId = salesAdmin.getString("partyId");
					}
				}
			}
		}
	}
	
	if (orderHeader.agreementId) {
		GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", orderHeader.agreementId), false);
		if (agreement) context.agreementCode = agreement.agreementCode;
	}
	
	// get info: payment method
	List<GenericValue> orderPaymentInfos = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderHeader.orderId), null, false);
	if (UtilValidate.isNotEmpty(orderPaymentInfos)) {
		GenericValue orderPaymentInfoFirst = EntityUtil.getFirst(orderPaymentInfos);
		if (UtilValidate.isNotEmpty(orderPaymentInfoFirst)) {
			if (UtilValidate.isNotEmpty(orderPaymentInfoFirst.paymentMethodTypeId))
			{
				GenericValue paymentMethod = delegator.findOne("PaymentMethodType", UtilMisc.toMap("paymentMethodTypeId", orderPaymentInfoFirst.paymentMethodTypeId), true);
				if (paymentMethod != null) {
					String description = paymentMethod.get("description", locale);
					if (UtilValidate.isNotEmpty(description)) {
						context.paymentMethodTypeName = description;
						context.paymentMethodTypeId = description + " [" + orderPaymentInfoFirst.paymentMethodTypeId + "]";
					} else {
						context.paymentMethodTypeName = orderPaymentInfoFirst.paymentMethodTypeId;
						context.paymentMethodTypeId = orderPaymentInfoFirst.paymentMethodTypeId;
					}
				}
			}
		}
	}
	
	String displayPartyNameResult = "";
	String customerFullName = "";
	String customerIdOrCode = "";
	if (orderHeader.createdBy != null) {
		GenericValue createdByUser = orderHeader.getRelatedOne("CreatedByUserLogin", true);
		if (createdByUser.getString("partyId") != null) {
			GenericValue createdByParty = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", createdByUser.getString("partyId")), false);
			if (createdByParty != null) {
				String createdByPartyNameStr = createdByParty.getString("fullName");
				if (UtilValidate.isNotEmpty(createdByPartyNameStr)) {
					if (UtilValidate.isNotEmpty(createdByParty.getString("partyCode"))){
						context.createdByPartyName = createdByPartyNameStr + " [" + createdByParty.getString("partyCode") + "]";
					} else {
						context.createdByPartyName = createdByPartyNameStr + " [" + createdByParty.getString("partyId") + "]";
					}
				} else {
					context.createdByPartyName = createdByParty.getString("partyId");
				}
			}
		}
	}
	
	// get info: displayPartyName of customer
	/*Map<String, Object> resultValue1 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "lastNameFirst", "Y", "userLogin", userLogin));
	if (ServiceUtil.isSuccess(resultValue1)) {
		GenericValue displayPartyGV = delegator.findOne("Party", ["partyId": displayParty.partyId], false);
		customerFullName = resultValue1.fullName;
		customerIdOrCode = displayParty.partyId;
		if (displayPartyGV) customerIdOrCode = displayPartyGV.partyCode;
		displayPartyNameResult = customerFullName + " [" + customerIdOrCode + "]";
		
		if (displayPartyNameResult == null || displayPartyNameResult == "") {
			displayPartyNameResult = UtilProperties.getMessage("OrderErrorUiLabels", "OrderPartyNameNotFound", locale);
		}
	}*/
	GenericValue customerNameGV = EntityUtil.getFirst(delegator.findByAnd("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", displayParty.partyId), null, false));
	if (customerNameGV != null) {
		customerFullName = customerNameGV.fullName;
		customerIdOrCode = customerNameGV.partyId;
		if (customerNameGV.partyCode) customerIdOrCode = customerNameGV.partyCode;
		displayPartyNameResult = customerFullName + " [" + customerIdOrCode + "]";
		
		if (displayPartyNameResult == null || displayPartyNameResult == "") {
			displayPartyNameResult = UtilProperties.getMessage("OrderErrorUiLabels", "OrderPartyNameNotFound", locale);
		}
	}
	
	GenericValue billingCustomer = EntityUtil.getFirst(delegator.findByAnd("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", billingCustomerParty.get("partyId")), null, false));
	if (billingCustomer != null) context.billingCustomer = billingCustomer;
		
	context.customerFullName = customerFullName;
	context.customerIdOrCode = customerIdOrCode;
	context.displayPartyNameResult = displayPartyNameResult;
	
	List<Map<String, Object>> orderAdjustmentsPromo = FastList.newInstance();
	/*
	List<GenericValue> listOrderAdjOrderItemPromo = delegator.findByAnd("OrderAdjustment", ["orderId": orderHeader.orderId, "orderAdjustmentTypeId": "PROMOTION_ADJUSTMENT"], null, false);
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
			orderAdjustmentsPromo.add(orderAdjMap);
		}
	}
	*/
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
			Map<String, Object> orderAdjMap = FastMap.newInstance();
			orderAdjMap.put("productPromoId", promoId);
			orderAdjMap.put("promoName", promoName);
			orderAdjMap.put("amount", amountTotal);
			orderAdjMap.put("productPromoCodeIds", productPromoCodeIds);
			orderAdjustmentsPromo.add(orderAdjMap);
		}
	}
	context.orderAdjustmentsPromo = orderAdjustmentsPromo;
	
	// get info: shipping address of customer
	List<String> shippingAddressList = FastList.newInstance();
	for (Map<String, GenericValue> orderContactMechValueMap : cmvm) {
		contactMech = orderContactMechValueMap.contactMech;
		contactMechPurpose = orderContactMechValueMap.contactMechPurposeType;
		
		if (contactMech.contactMechTypeId == "POSTAL_ADDRESS") {
			GenericValue postalAddress = orderContactMechValueMap.postalAddress;
			
			if (postalAddress) {
				GenericValue postalAddressFullNameDetail = EntityUtil.getFirst(delegator.findByAnd("PostalAddressFullNameDetail", ["contactMechId": postalAddress.contactMechId], null, false));
				String addressFullName = "";
				if (postalAddress.toName) addressFullName += postalAddress.toName;
				if (postalAddress.attnName) addressFullName += " (" + postalAddress.attnName + ")";
				if (postalAddress.toName || postalAddress.attnName) addressFullName += ". ";
				if (postalAddressFullNameDetail) {
					addressFullName += postalAddressFullNameDetail.fullName;
				}
				shippingAddressList.add(addressFullName);
			}
		}
	}
	context.shippingAddressList = shippingAddressList;
	
	// get shipping method type
	EntityFindOptions shippingMethodOpts = new EntityFindOptions();
	shippingMethodOpts.setLimit(1);
	GenericValue shippingMethodTypeGv = EntityUtil.getFirst(delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", orderHeader.orderId), null, null, shippingMethodOpts, false));
	context.shippingMethodTypeGv = shippingMethodTypeGv;
	
	// get info loyalty point
	List<GenericValue> listLoyaltyUse = delegator.findByAnd("LoyaltyUse", ["orderId": orderHeader.orderId], null, false);
	if (UtilValidate.isNotEmpty(listLoyaltyUse)) {
		BigDecimal totalLoyaltyPoint = BigDecimal.ZERO;
		for (GenericValue loyaltyUse : listLoyaltyUse) {
			if (loyaltyUse.get("quantityLeftInActions")) totalLoyaltyPoint = totalLoyaltyPoint.add(loyaltyUse.getBigDecimal("quantityLeftInActions"));
		}
		context.totalLoyaltyPoint = totalLoyaltyPoint;
	}
	if (displayParty && displayParty.partyId) {
		List<GenericValue> listLoyaltyPoint = delegator.findByAnd("LoyaltyPoint", ["partyId": displayParty.partyId], null, false);
		if (UtilValidate.isNotEmpty(listLoyaltyPoint)) {
			context.listLoyaltyPoint = listLoyaltyPoint;
			
			List<EntityCondition> listCondsPartyClass = FastList.newInstance();
			listCondsPartyClass.add(EntityCondition.makeCondition("partyId", displayParty.partyId));
			listCondsPartyClass.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listPartyClassification = delegator.findList("PartyClassification", EntityCondition.makeCondition(listCondsPartyClass), null, null, null, false);
			if (UtilValidate.isNotEmpty(listPartyClassification)) {
				context.partyClassification = EntityUtil.getFirst(listPartyClassification);
			}
		}
	}
	
	if ( orderHeader.orderDate != null ){
		List<EntityCondition> listConditions = FastList.newInstance(); 
		listConditions.add(EntityUtil.getFilterByDateExpr(orderHeader.orderDate));
		listConditions.add(EntityCondition.makeCondition("partyId", displayParty.partyId));
		List<GenericValue> displayPartyPhones = delegator.findList("PartyAndTelecomNumberOrderTemp",EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(displayPartyPhones)) {
				context.displayPartyPhones = displayPartyPhones; 
		}
	}

	if ( orderHeader.orderDate != null ){
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityUtil.getFilterByDateExpr(orderHeader.orderDate));
		listConditions.add(EntityCondition.makeCondition("partyId", consignee.partyId));
		List<GenericValue> consigneePhones = delegator.findList("PartyAndTelecomNumberOrderTemp",EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(consigneePhones)) {
			context.consigneePhones = consigneePhones;
		}
	}
}
context.listItemLine = listItemLine;
context.subAmountExportOrder = subAmountExportOrder;
context.subAmountExportInvoice = subAmountExportInvoice;
context.taxTotalOrderItems = taxTotalOrderItems;
context.listTaxTotal = listTaxTotal;
context.hasPromoSettlement = hasPromoSettlement;
