
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderContentWrapper;

import com.olbius.util.SalesPartyUtil;
import org.ofbiz.party.party.PartyHelper

List<Map<String, Object>> listItemLine = new ArrayList<Map<String, Object>>();
BigDecimal taxTotalOrderItems = BigDecimal.ZERO;
BigDecimal subAmountExportOrder = BigDecimal.ZERO;
BigDecimal subAmountExportInvoice = BigDecimal.ZERO;
List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();
Timestamp desiredDeliveryDate = null;

//process to group orderItem
 
if (orderItemList != null) {
	for (GenericValue orderItem : orderItemList) {
		GenericValue orderItemType = orderItem.getRelatedOne("OrderItemType", false);
		OrderContentWrapper orderItemContentWrapper = OrderContentWrapper.makeOrderContentWrapper(orderItem, request);
		List<GenericValue> orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem);
		if (orderHeader != null && orderHeader.orderTypeId == "SALES_ORDER") {
			BigDecimal pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
		}
		GenericValue product = orderItem.getRelatedOne("Product", false);
		
		String productId = orderItem.getString("productId");
		String seqId = orderItem.getString("orderItemSeqId");
		String itemDescription = orderItem.getString("itemDescription");;
		String supplierProductId = orderItem.getString("supplierProductId");
		GenericValue barcodeGeneric = delegator.findOne("GoodIdentification", ["goodIdentificationTypeId" : "SKU", "productId" : productId], false);
		String barcode = barcodeGeneric.idValue;
		Timestamp expireDate = orderItem.getTimestamp("expireDate");
		String displayPackingPerTray = "";
		
		boolean isNormal = true;
		BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
		BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
		if (alternativeQuantity != null && alternativeUnitPrice != null) {
			isNormal = false;
		}
		
		BigDecimal packingPerTray = null;
		Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
		if (ServiceUtil.isSuccess(resultValue)) {
			packingPerTray = resultValue.convertNumber;
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
		
		BigDecimal quantity = null;
		BigDecimal sumTray = null;
		BigDecimal unitPrice = null;
		BigDecimal productQuantityPerTray = null;
		if (isNormal) {
			if (product.productPackingUomId != null) {
				Map<String, Object> resultValue1 = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
				if (ServiceUtil.isSuccess(resultValue1)) {
					productQuantityPerTray = resultValue1.convertNumber;
				}
			}
			quantity = orderItem.quantity;
			if (quantity != null && productQuantityPerTray != null) {
				sumTray = quantity.divide(productQuantityPerTray, 2, RoundingMode.HALF_UP);
			}
			unitPrice = orderItem.unitPrice;
		} else {
			Map<String, Object> resultValue1 = dispatcher.runSync("getConvertPackingNumber", ["productId" : productId, "uomFromId" : "DELYS_KHAY", "uomToId" : orderItem.quantityUomId, "userLogin" : userLogin]);
			if (ServiceUtil.isSuccess(resultValue1)) {
				productQuantityPerTray = resultValue1.convertNumber;
			}
			quantity = orderItem.alternativeQuantity;
			if (quantity != null && productQuantityPerTray != null) {
				sumTray = quantity.divide(productQuantityPerTray, 2, RoundingMode.HALF_UP);
			}
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
						if (itemAdjustment.comments != null) description += " (" + itemAdjustment.comments + ").&nbsp;";
						if (itemAdjustment.productPromoId != null) {
							description += "<a class='btn btn-mini btn-primary' href='/catalog/control/EditProductPromo?productPromoId=" + itemAdjustment.productPromoId + externalKeyParam + "'>";
							description += itemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName") + "</a>";
						}
						if (itemAdjustment.primaryGeoId != null) {
							GenericValue primaryGeo = itemAdjustment.getRelatedOne("PrimaryGeo", true);
							if (primaryGeo.geoName != null) {
								String orderJurisdictionStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += "<span>" + orderJurisdictionStr + "</span>&nbsp;" + primaryGeo.geoName + " [" + primaryGeo.abbreviation + "].&nbsp;";
							}
							if (itemAdjustment.secondaryGeoId != null) {
								GenericValue secondaryGeo = itemAdjustment.getRelatedOne("SecondaryGeo", true);
								String commonInStr = UtilProperties.getMessage("OrderUiLabels", "OrderJurisdiction", locale);
								description += "<span>" + commonInStr + "</span>&nbsp;" + secondaryGeo.geoName + " [" + secondaryGeo.abbreviation + "]).&nbsp;";
							}
						}
						if (itemAdjustment.sourcePercentage != null) {
							String orderRateStr = UtilProperties.getMessage("OrderUiLabels", "OrderRate", locale);
							String template = "#,##0.###";
							sourcePercentageStr = UtilFormatOut.formatDecimalNumber(itemAdjustment.sourcePercentage.doubleValue(), template, locale);
							description += "<span>" + orderRateStr + "</span>&nbsp;" + sourcePercentageStr + "%"; //?string("0.######")
						}
						if (itemAdjustment.customerReferenceId != null) {
							String orderCustomerTaxIdStr = UtilProperties.getMessage("OrderUiLabels", "OrderCustomerTaxId", locale);
							description += "<span>" + orderCustomerTaxIdStr + "</span>&nbsp;" + itemAdjustment.customerReferenceId;
						}
						if (itemAdjustment.exemptAmount != null) {
							String orderExemptAmountStr = UtilProperties.getMessage("OrderUiLabels", "OrderExemptAmount", locale);
							description += "<span>" + orderExemptAmountStr + "</span>&nbsp;" + itemAdjustment.exemptAmount;
						}
						taxTotalItemNew.put("description", description);
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
								
								BigDecimal oproductQuantityPerTray = oItem.productQuantityPerTray;
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
					}
				}
			}
		}
		
		if (isAdd) {
			Map<String, Object> itemLine = FastMap.newInstance();
			itemLine.put("seqId", seqId);
			itemLine.put("productId", productId);
			itemLine.put("productName", product.getString("productName"));
			itemLine.put("barcode", barcode);
			itemLine.put("expireDate", expireDate);
			itemLine.put("packingPerTray", packingPerTray);
			itemLine.put("quantityUomId", orderItem.getString("quantityUomId"));
			itemLine.put("quantityUomDescription", quantityUomDescription);
			itemLine.put("quantity", quantity);
			itemLine.put("sumTray", sumTray);
			itemLine.put("unitPriceBeVAT", unitPrice); //before VAT
			itemLine.put("adjustment", adjustment);
			itemLine.put("subTotalBeVAT", subTotalBeVAT);
			itemLine.put("invoicePrice", unitPriceInvoiceAfVAT);
			itemLine.put("invoiceSubTotal", subTotalInvoiceExport);
			itemLine.put("itemDescription", itemDescription);
			itemLine.put("supplierProductId", supplierProductId);
			itemLine.put("product", product);
			itemLine.put("orderItemType", orderItemType);
			itemLine.put("isPromo", isPromo);
			itemLine.put("productPromoId", productPromoId);
			itemLine.put("productPromoRuleId", productPromoRuleId);
			itemLine.put("productPromoActionSeqId", productPromoActionSeqId);
			itemLine.put("isNormal", isNormal);
			itemLine.put("productQuantityPerTray", productQuantityPerTray);
			listItemLine.add(itemLine);
		}
		if (!desiredDeliveryDate) {
			desiredDeliveryDate = orderItem.getTimestamp("estimatedDeliveryDate");
		}
	}
}

String displayPartyNameResult = "";
String displaySUPsNameResult = "";
if (displayParty != null) {
	Map<String, Object> resultValue1 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin));
	if (ServiceUtil.isSuccess(resultValue1)) {
		displayPartyNameResult = resultValue1.fullName;
		if (displayPartyNameResult == null || displayPartyNameResult == "") {
			displayPartyNameResult = UtilProperties.getMessage("OrderErrorUiLabels", "OrderPartyNameNotFound", locale);
		}
	}
	
	List<String> listCompany = SalesPartyUtil.getListCompanyInProperties(delegator);
	if (UtilValidate.isNotEmpty(listCompany)) {
		GenericValue orderRoleVender = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderHeader.orderId, "roleTypeId", "BILL_FROM_VENDOR"), null, false));
		if (UtilValidate.isNotEmpty(orderRoleVender)) {
			String partySeller = orderRoleVender.partyId;
			String distributorId = null;
			/*if (listCompany.contains(partySeller)) {
				distributorId = displayParty.partyId;
				// the order of company
				List<GenericValue> listEmployeeSUP = new ArrayList<GenericValue>();
				List<GenericValue> listRelationToSUP = delegator.findByAnd("PartyRelationship", ["partyIdTo" : displayParty.partyId, "roleTypeIdFrom" : "DELYS_SALESSUP_GT"], null, false);
				for (supItem in listRelationToSUP) {
					List<GenericValue> listEmployeeSUP0 = delegator.findByAnd("PartyRelationship", ["partyIdTo" : supItem.partyIdFrom, "partyRelationshipTypeId" : "MANAGER"], null, false);
					if (listEmployeeSUP0 != null) {
						listEmployeeSUP.addAll(listEmployeeSUP0);
					}
				}
				for (GenericValue employeeSUP : listEmployeeSUP) {
					Map<String, Object> resultValue2 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", employeeSUP.partyIdFrom, "userLogin", userLogin));
					if (ServiceUtil.isSuccess(resultValue2)) {
						if (displaySUPsNameResult == "") {
							displaySUPsNameResult = resultValue2.fullName;
						} else {
							displaySUPsNameResult += ", " + resultValue2.fullName;
						}
					}
				}
			} else {
				// the order of distributor
				List<String> listEmployeeSUP = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, partySeller);
				if (UtilValidate.isNotEmpty(listEmployeeSUP)) {
					for (String employeeSUP : listEmployeeSUP) {
						Map<String, Object> resultValue2 = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", employeeSUP, "userLogin", userLogin));
						if (ServiceUtil.isSuccess(resultValue2)) {
							if (displaySUPsNameResult == "") {
								displaySUPsNameResult = resultValue2.fullName;
							} else {
								displaySUPsNameResult += ", " + resultValue2.fullName;
							}
						}
					}
				}
			}*/
			if (listCompany.contains(partySeller)) {
				// the order of company
				distributorId = displayParty.partyId;
			} else {
				// the order of distributor
				distributorId = partySeller;
			}
			if (UtilValidate.isNotEmpty(distributorId)) {
				if (orderHeader.salesMethodChannelEnumId != null && orderHeader.salesMethodChannelEnumId == "SALES_GT_CHANNEL") {
					List<String> supPersonIds = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, distributorId);
					if (supPersonIds != null) {
						for (String supPersonId : supPersonIds) {
							String supNameFull = PartyHelper.getPartyName(delegator, supPersonId, true, true);
							if (UtilValidate.isNotEmpty(supNameFull)) {
								if (UtilValidate.isNotEmpty(displaySUPsNameResult)) displaySUPsNameResult += ", ";
								displaySUPsNameResult += supNameFull;
							}
						}
					}
				} else {
					List<String> supPersonIds = SalesPartyUtil.getListSupPersonIdByCustomerDirect(delegator, displayParty.partyId);
					if (supPersonIds != null) {
						for (String supPersonId : supPersonIds) {
							String supNameFull = PartyHelper.getPartyName(delegator, supPersonId, true, true);
							if (UtilValidate.isNotEmpty(supNameFull)) {
								if (UtilValidate.isNotEmpty(displaySUPsNameResult)) displaySUPsNameResult += ", ";
								displaySUPsNameResult += supNameFull;
							}
						}
					}
				}
			}
		}
	}
	
	if (displaySUPsNameResult == "") {
		displaySUPsNameResult = UtilProperties.getMessage("OrderUiLabels", "OrderPartyNameNotFound", locale);
	}
}
List<String> listSup = new ArrayList<String>();
List<String> listSalesman = new ArrayList<String>();
if (orderHeader.orderId) {
	List<GenericValue> listOrderRole = delegator.findByAnd("OrderRole", ["orderId" : orderHeader.orderId], null, false);
	String roleIdSup = EntityUtilProperties.getPropertyValue("delys.properties", "party.role.sup.delys", delegator);
	String roleIdSalesman = EntityUtilProperties.getPropertyValue("delys.properties", "party.role.salesman.delys", delegator);
	List<String> roleIdsSup = SalesPartyUtil.getListDescendantRoleInclude(roleIdSup, delegator);
	List<String> roleIdsSalesman = SalesPartyUtil.getListDescendantRoleInclude(roleIdSalesman, delegator);
	EntityCondition condSup = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleIdsSup);
	EntityCondition condSalesman = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleIdsSalesman);
	List<String> listOrderRoleSupId = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByCondition(listOrderRole, condSup), "partyId", true);
	List<String> listOrderRoleSalesmanId = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByCondition(listOrderRole, condSalesman), "partyId", true);
	if (UtilValidate.isNotEmpty(listOrderRoleSupId)) {
		for (String supId : listOrderRoleSupId) {
			String partyName = PartyHelper.getPartyName(delegator, supId, true, true);
			if (partyName) listSup.add(partyName);
		}
	}
	if (UtilValidate.isNotEmpty(listOrderRoleSalesmanId)) {
		for (String salesmanId : listOrderRoleSalesmanId) {
			String partyName = PartyHelper.getPartyName(delegator, salesmanId, true, true);
			if (partyName) listSalesman.add(partyName);
		}
	}
}

context.listItemLine = listItemLine;
context.subAmountExportOrder = subAmountExportOrder;
context.subAmountExportInvoice = subAmountExportInvoice;
context.desiredDeliveryDate = desiredDeliveryDate;
context.displayPartyNameResult = displayPartyNameResult;
context.displaySUPsNameResult = displaySUPsNameResult;
context.taxTotalOrderItems = taxTotalOrderItems;
context.listTaxTotal = listTaxTotal;
context.listSup = listSup;
context.listSalesman = listSalesman;
