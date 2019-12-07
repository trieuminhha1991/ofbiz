
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;
import com.olbius.basesales.order.OrderWorker;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastList;

import com.olbius.product.util.ProductUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderContentWrapper;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.basesales.util.SalesUtil;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.common.uom.UomWorker;

List<Map<String, Object>> listItemLine = new ArrayList<Map<String, Object>>();
BigDecimal taxTotalOrderItems = BigDecimal.ZERO;
BigDecimal subAmountExportOrder = BigDecimal.ZERO;
BigDecimal subAmountExportInvoice = BigDecimal.ZERO;
List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();
BigDecimal taxDiscountTotal = BigDecimal.ZERO; // thue giam tru khi co khuyen mai
Timestamp desiredDeliveryDate = null;
Timestamp shipAfterDate = null;
Timestamp shipBeforeDate = null;

//process to group orderItem
boolean hasPromoSettlement = false;
if (orderItemList != null) {
	for (GenericValue orderItem : orderItemList) {
		if (!hasPromoSettlement && "PRODPROMO_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) hasPromoSettlement = true;
		GenericValue orderItemType = orderItem.getRelatedOne("OrderItemType", false);
		OrderContentWrapper orderItemContentWrapper = OrderContentWrapper.makeOrderContentWrapper(orderItem, request);
		List<GenericValue> orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem);
		if (orderHeader != null && orderHeader.orderTypeId == "SALES_ORDER") {
			BigDecimal pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
		}
		GenericValue product = orderItem.getRelatedOne("Product", false);
		
		String requireAmount = product.requireAmount;
		String productId = orderItem.getString("productId");
		String seqId = orderItem.getString("orderItemSeqId");
		String itemDescription = orderItem.getString("itemDescription");;
		String supplierProductId = orderItem.getString("supplierProductId");
		List<GenericValue> listBarcodeGeneric = delegator.findByAnd("GoodIdentification", ["goodIdentificationTypeId" : "SKU", "productId" : productId], null, false);
		String barcode = null;
		if (listBarcodeGeneric) {
			GenericValue barcodeGeneric = null;
			if (orderItem.quantityUomId) {
				barcodeGeneric = EntityUtil.getFirst(EntityUtil.filterByAnd(listBarcodeGeneric, ["uomId" : orderItem.quantityUomId]));
			} else {
				barcodeGeneric = EntityUtil.getFirst(listBarcodeGeneric);
			}
			if (barcodeGeneric) barcode = barcodeGeneric.idValue;
		}
		Timestamp expireDate = orderItem.getTimestamp("expireDate");
		String displayPackingPerTray = "";
		
		boolean isNormal = true;
		BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
		BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
		if (alternativeQuantity != null && alternativeUnitPrice != null) {
			isNormal = false;
		}
		String quantityUomDescription = "";
		GenericValue quantityUomGeneric = delegator.findOne("Uom", ["uomId" : orderItem.quantityUomId], false);
		if (quantityUomGeneric != null) {
			if (quantityUomGeneric.description != null) {
				quantityUomDescription = quantityUomGeneric.description;
			} else {
				quantityUomDescription = quantityUomGeneric.uomId;
			}
		}
		
		String weightUomDescription = "";
		if (orderItem.weightUomId != null){
			GenericValue weightUomGeneric = delegator.findOne("Uom", ["uomId" : orderItem.weightUomId], false);
			if (weightUomGeneric != null) {
				weightUomDescription = weightUomGeneric.abbreviation;
			}
		}
		
		BigDecimal packing = UomWorker.customConvertUom(productId,
				orderItem.getString("quantityUomId"), product.getString("quantityUomId"), BigDecimal.ONE, delegator);
		
		BigDecimal quantity = null;
		BigDecimal selectedAmount = orderItem.selectedAmount;;
		BigDecimal sumTray = null;
		BigDecimal unitPrice = null;
		BigDecimal productQuantityPerTray = null;
		BigDecimal baseQuantity = orderItem.quantity;
		BigDecimal basePrice = orderItem.unitPrice;
		if (isNormal) {
			quantity = orderItem.quantity;
			unitPrice = orderItem.unitPrice;
		} else {
			quantity = orderItem.alternativeQuantity;
			unitPrice = orderItem.alternativeUnitPrice;
		}
		// Modify by VietTB
		BigDecimal adjustment;
		//DASubTotalBeforeVAT
		BigDecimal subTotalBeVAT;
		if (orderItem.isPromo.equals("Y"))
		{
		 	//adjustment =  OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false);
		 	orderAdjustmentss = delegator.findList("OrderAdjustment", EntityCondition.makeCondition([orderId : orderHeader.orderId]), null, null, null, false);
		 	adjustment =  OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustmentss, true, false, false);
		 	subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustmentss);
		 	//subTotalBeVAT = OrderReadHelper.getNewOrderItemSubTotal(orderItem);
		}
		else 
		{
			adjustment =  BigDecimal.ZERO;
			List<BigDecimal> orderAdjustmentss = new ArrayList<BigDecimal>();
			subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustmentss);
		}

		// DASubTotalBeforeVAT
		// BigDecimal subTotalBeVAT = OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments);
		if (orderItem.statusId != "ITEM_CANCELLED") {
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
		
		String productPromoId = "";
		String productPromoRuleId = "";
		String productPromoActionSeqId = "";
		boolean isAdd = false;
		boolean isPromo = false;
		if ("Y" == orderItem.isPromo) {
			isPromo = true;
		} else {
			isAdd = true;
		}
		
		/* Caculate tax prices sum from order items */
		List<GenericValue> orderItemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orderAdjustments);
		if (orderItemAdjustments != null) {
			for (GenericValue itemAdjustment : orderItemAdjustments) {
				GenericValue adjustmentType = itemAdjustment.getRelatedOne("OrderAdjustmentType", true);
				if (adjustmentType.orderAdjustmentTypeId == "SALES_TAX") {
					BigDecimal taxValue = OrderReadHelper.calcItemAdjustment(itemAdjustment, orderItem);
					taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
					boolean isExists = false;
					for (Map<String, Object> taxTotalItem : listTaxTotal) {
						if (taxTotalItem.sourcePercentage == itemAdjustment.sourcePercentage) {
							// exists item
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
						// not exists item
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
				
				// check is promo product
				if (isPromo) {
					if (itemAdjustment.orderAdjustmentTypeId == "PROMOTION_ADJUSTMENT"
							&& itemAdjustment.productPromoId != null
							&& itemAdjustment.productPromoRuleId != null
							&& itemAdjustment.productPromoActionSeqId != null) {
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
								
								BigDecimal osumTray = BigDecimal.ZERO;
								BigDecimal oquantity = oItem.get("quantity");
								BigDecimal oadjustment = oItem.get("adjustment");
								BigDecimal osubTotalBeVAT = oItem.get("subTotalBeVAT");
								if (oquantity != null && productQuantityPerTray != null) {
									osumTray = oquantity.divide(productQuantityPerTray, 2, RoundingMode.HALF_UP);
								}
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
								oadjustment = oadjustment.add(oItem.get("adjustment"));
								osubTotalBeVAT = osubTotalBeVAT.add(oItem.get("subTotalBeVAT"));
								oItem.put("quantity", oquantity);
								oItem.put("sumTray", osumTray);
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
					} else if (itemAdjustment.orderAdjustmentTypeId == "PROMOTION_ADJUSTMENT"
							&& itemAdjustment.productPromoId == null
							&& itemAdjustment.productPromoRuleId == null
							&& itemAdjustment.productPromoActionSeqId == null) {
						isAdd = true;
					}
				}
			}
		}

        if(isPromo) {
            isAdd = true;
        }
		
		if (isAdd) {
			GenericValue pr = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			BigDecimal convertQuantityByAmount = selectedAmount;
			String weightUomId = orderItem.getString("weightUomId");
			baseWeightUomId = pr.getString("weightUomId");
			baseQuantityUomId = pr.getString("quantityUomId");
			BigDecimal convertNumber = BigDecimal.ONE;
			if (weightUomId != null && !"".equals(weightUomId) && requireAmount != null && requireAmount == 'Y') {
				GenericValue conversion = null;
				conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomId, "uomIdTo", baseWeightUomId));
				if (conversion == null) {
					conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", baseWeightUomId, "uomIdTo", weightUomId));
					if (conversion != null) {
						convertQuantityByAmount = convertQuantityByAmount.divide(conversion.getBigDecimal("conversionFactor"), 3, RoundingMode.HALF_UP);
					}
				} else { 
					convertQuantityByAmount = convertQuantityByAmount.multiply(conversion.getBigDecimal("conversionFactor"));
				}
				convertNumber = BigDecimal.ONE;
			} else {
				if (orderItem.getString("quantityUomId") != null && baseQuantityUomId != null) {
					convertNumber = ProductUtil.getConvertPackingNumber(delegator, pr.getString("productId"), orderItem.getString("quantityUomId"), baseQuantityUomId);
				}
			}
			
			Map<String, Object> itemLine = FastMap.newInstance();
			itemLine.put("seqId", seqId);
			itemLine.put("productId", productId);
			itemLine.put("convertNumber", convertNumber);
			itemLine.put("productCode", product.getString("productCode"));
			itemLine.put("productName", product.getString("productName"));
			itemLine.put("barcode", barcode);
			itemLine.put("expireDate", expireDate);
			itemLine.put("quantityUomId", orderItem.getString("quantityUomId"));
			itemLine.put("quantityUomDescription", quantityUomDescription);
			itemLine.put("weightUomId", weightUomId);
			itemLine.put("weightUomDescription", weightUomDescription);
			itemLine.put("packing", packing);
			itemLine.put("quantity", quantity);
			itemLine.put("cancelQuantity", orderItem.getBigDecimal("cancelQuantity"));
			itemLine.put("cancelAmount", orderItem.getBigDecimal("cancelAmount"));
			itemLine.put("convertQuantityByAmount", convertQuantityByAmount);
			itemLine.put("selectedAmount", selectedAmount);
			itemLine.put("sumTray", sumTray);
			itemLine.put("unitPriceBeVAT", unitPrice); //before VAT
			itemLine.put("baseUnitPriceBeVAT", basePrice); //before VAT
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
			itemLine.put("productQuantityPerTray", productQuantityPerTray);
			itemLine.put("comments", orderItem.comments);
			itemLine.put("orderItem", orderItem);
			itemLine.put("orderItemTypeId", orderItem.orderItemTypeId);
			itemLine.put("baseQuantity", baseQuantity);
			itemLine.put("basePrice", basePrice);
			itemLine.put("requireAmount", requireAmount);
			listItemLine.add(itemLine);
		}
		if (!desiredDeliveryDate) {
			desiredDeliveryDate = orderItem.getTimestamp("estimatedDeliveryDate");
		}
		if (!shipAfterDate) {
			shipAfterDate = orderItem.getTimestamp("shipAfterDate");
		}
		if (!shipBeforeDate) {
			shipBeforeDate = orderItem.getTimestamp("shipBeforeDate");
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
						context.createdByPartyName =  "[" + createdByParty.getString("partyCode") + "] " + createdByPartyNameStr;
					} else {
						context.createdByPartyName = "[" + createdByParty.getString("partyId") + "] " + createdByPartyNameStr;
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
	List<Map<String, GenericValue>> orderContactMechValueMaps = org.ofbiz.party.contact.ContactMechWorker.getOrderContactMechValueMaps(delegator, orderHeader.orderId);
	for (Map<String, GenericValue> orderContactMechValueMap : orderContactMechValueMaps) {
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
}

List<GenericValue> listAdjustmentTaxDiscount = delegator.findByAnd("OrderAdjustment", ["orderId": orderHeader.orderId, "orderAdjustmentTypeId": "SALES_TAX", "orderItemSeqId": "_NA_"], null, false);
if (!listAdjustmentTaxDiscount.isEmpty()) {
	for (GenericValue adj : listAdjustmentTaxDiscount) {
		taxDiscountTotal += adj.getBigDecimal("amount"); 
	}
}
	
context.listItemLine = listItemLine;
context.subAmountExportOrder = subAmountExportOrder;
context.subAmountExportInvoice = subAmountExportInvoice;
context.desiredDeliveryDate = desiredDeliveryDate;
context.shipAfterDate = shipAfterDate;
context.shipBeforeDate = shipBeforeDate;
context.taxTotalOrderItems = taxTotalOrderItems;
context.listTaxTotal = listTaxTotal;
context.hasPromoSettlement = hasPromoSettlement;
context.taxDiscountTotal = taxDiscountTotal;