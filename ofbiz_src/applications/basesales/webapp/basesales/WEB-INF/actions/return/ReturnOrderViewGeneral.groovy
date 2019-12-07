
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.order.order.OrderReadHelper;
import java.math.BigDecimal;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basesales.returnorder.ReturnWorker;

import javolution.util.FastMap;

BigDecimal returnSubTotal = BigDecimal.ZERO;
BigDecimal taxTotalOrderItems = BigDecimal.ZERO;
BigDecimal otherAdjAmount = BigDecimal.ZERO;
BigDecimal taxAmount = BigDecimal.ZERO;
BigDecimal shippingAmount = BigDecimal.ZERO;
List<Map<String, Object>> listTaxTotal = new ArrayList<Map<String, Object>>();

if (returnHeader) {
	String currentStatusId = returnHeader.statusId;
	GenericValue currentStatus = delegator.findOne("StatusItem", ["statusId" : currentStatusId], true);
	String orderStatusMgs = "";
	if (currentStatus) orderStatusMgs = (String) currentStatus.get("description", locale);
	context.currentStatusId = currentStatusId;
	context.orderStatusMgs = orderStatusMgs;
	
	// party was returned product
	GenericValue fromPartyNameView = delegator.findOne("PartyFullNameDetailSimple", ["partyId" : returnHeader.fromPartyId], false);
	String displayPartyNameResult = "" + returnHeader.fromPartyId;
	if (fromPartyNameView) {
		displayPartyNameResult = fromPartyNameView.fullName + " [" + fromPartyNameView.partyCode + "]";
	}
	context.displayPartyNameResult = displayPartyNameResult;
	
	// party receive
	GenericValue toPartyNameView = delegator.findOne("PartyFullNameDetailSimple", ["partyId" : returnHeader.toPartyId], false);
	String displayNamePartyTo = "" + returnHeader.toPartyId;
	if (toPartyNameView) {
		displayNamePartyTo = toPartyNameView.fullName + " [" + toPartyNameView.partyCode + "]";
	}
	context.displayNamePartyTo = displayNamePartyTo;
	
	List<GenericValue> returnAdjustments = delegator.findByAnd("ReturnAdjustment", ["returnId": returnHeader.returnId], null, false);
	
	// get list item order
	List<GenericValue> returnItems = delegator.findByAnd("ReturnItemDetail", ["returnId" : returnHeader.returnId], ["orderItemSeqId"], false);
	List<Map<String, Object>> listItemLine = new ArrayList<Map<String, Object>>();
	if (returnItems) {
		for (GenericValue returnItem : returnItems) {
			Map<String, Object> itemLine = returnItem.getAllFields();
			GenericValue product = delegator.findOne("Product", [productId: returnItem.productId], false);
			if (product != null) {
				itemLine.productName = product.productName;
			}
			
			// reason
			String reasonDesc = "";
			GenericValue reasonEnum = delegator.findOne("ReturnReason", ["returnReasonId": returnItem.returnReasonId], false);
			if (reasonEnum) reasonDesc = (String) reasonEnum.get("description", locale);
			itemLine.put("reasonDesc", reasonDesc);
			
			// quantity uom
			String quantityUomDesc = "";
			GenericValue quantityUom = delegator.findOne("Uom", ["uomId": returnItem.quantityUomId], false);
			if (quantityUom) quantityUomDesc = (String) quantityUom.get("description", locale);
			itemLine.put("quantityUomDesc", quantityUomDesc);
			
			// adjustment
			BigDecimal adjustment = BigDecimal.ZERO;
			List<GenericValue> returnItemAdjustments = EntityUtil.filterByAnd(returnAdjustments, UtilMisc.toMap("returnItemSeqId", returnItem.returnItemSeqId, "returnAdjustmentTypeId", "RET_PROMOTION_ADJ"));
			if (returnItemAdjustments) {
				for (GenericValue returnItemAdjustment : returnItemAdjustments) {
					if (UtilValidate.isNotEmpty(returnItemAdjustment.amount)) {
						adjustment = adjustment.add(returnItemAdjustment.getBigDecimal("amount"));
					}
				}
			}
			itemLine.put("adjustment", adjustment);
			
			// sub item total
			BigDecimal subTotalBeVAT = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(returnItem.returnQuantity) && UtilValidate.isNotEmpty(returnItem.returnPrice)) {
				subTotalBeVAT = returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
				subTotalBeVAT = subTotalBeVAT.add(adjustment);
			}
			itemLine.subTotalBeVAT = subTotalBeVAT;
			returnSubTotal = returnSubTotal.add(subTotalBeVAT);
			
			listItemLine.add(itemLine);
		}
	}
	context.listItemLine = listItemLine;
	
	// get list tax adjustment
	List<GenericValue> returnItemTaxAdjustments = EntityUtil.filterByAnd(returnAdjustments, UtilMisc.toMap("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ"));
	if (returnItemTaxAdjustments) {
		for (GenericValue itemAdjustment : returnItemTaxAdjustments) {
			GenericValue adjustmentType = delegator.findOne("ReturnAdjustmentType", ["returnAdjustmentTypeId": itemAdjustment.getString("returnAdjustmentTypeId")], false);
			if (adjustmentType.returnAdjustmentTypeId == "RET_SALES_TAX_ADJ") {
				BigDecimal taxValue = itemAdjustment.getBigDecimal("amount");
				
				taxAmount = taxAmount.add(taxValue);
				taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
				
				boolean isExists = false;
				for (Map<String, Object> taxTotalItem : listTaxTotal) {
					if (taxTotalItem.sourcePercentage == itemAdjustment.sourcePercentage) {
						// exists item
						BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
						amount = amount.add(itemAdjustment.getBigDecimal("amount"));
						taxTotalItem.put("amount", amount);
						/*if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
							BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
							taxTotalItem.put("amountForIXP", amountForInvoicePrice);
						}*/
						isExists = true;
					}
				}
				if (!isExists) {
					// not exists item
					Map<String, Object> taxTotalItemNew = FastMap.newInstance();
					BigDecimal amount = itemAdjustment.getBigDecimal("amount");
					taxTotalItemNew.put("sourcePercentage", itemAdjustment.getBigDecimal("sourcePercentage"));
					taxTotalItemNew.put("amount", amount);
					/*if (subTotalInvoiceExport != null && subTotalBeVAT != null) {
						BigDecimal amountForInvoicePrice = amount.multiply(subTotalInvoiceExport.divide(subTotalBeVAT, 2, RoundingMode.HALF_UP));
						taxTotalItemNew.put("amountForIXP", amountForInvoicePrice);
					}*/
					// add description in first (only 1 times)
					String description = UtilProperties.getMessage("OrderUiLabels", "OrderAdjustment", locale);
					description += " " + adjustmentType.get("description",locale);
					if (itemAdjustment.description != null) description += " " + itemAdjustment.get("description",locale);
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
		}
	}
	
	// get return header adjustment
	List<EntityCondition> headerAdjConds = new ArrayList<EntityCondition>();
	headerAdjConds.add(EntityCondition.makeCondition("returnItemSeqId", null));
	headerAdjConds.add(EntityCondition.makeCondition("returnItemSeqId", "_NA_"));
	headerAdjConds.add(EntityCondition.makeCondition("returnItemSeqId", ""));
	List<GenericValue> returnHeaderAdjustmentsGV = EntityUtil.filterByCondition(returnAdjustments, EntityCondition.makeCondition(headerAdjConds, EntityOperator.OR));
	List<Map<String, Object>> returnHeaderAdjustments = new ArrayList<Map<String, Object>>();
	if (returnHeaderAdjustmentsGV) {
		for (GenericValue returnHeaderAdj : returnHeaderAdjustmentsGV) {
			String returnAdjustmentTypeId = returnHeaderAdj.getString("returnAdjustmentTypeId");
			if ("RET_SALES_TAX_ADJ".equals(returnAdjustmentTypeId)) {
				/*BigDecimal taxValue = returnHeaderAdj.getBigDecimal("amount");
				
				taxAmount = taxAmount.add(taxValue);
				taxTotalOrderItems = taxTotalOrderItems.add(taxValue);
				
				boolean isExists = false;
				for (Map<String, Object> taxTotalItem : returnHeaderAdjustments) {
					if ("RET_SALES_TAX_ADJ".equals(returnAdjustmentTypeId)) {
						if (taxTotalItem.sourcePercentage == returnHeaderAdj.sourcePercentage) {
							// exists item
							BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
							amount = amount.add(returnHeaderAdj.getBigDecimal("amount"));
							taxTotalItem.put("amount", amount);
							isExists = true;
						}
					}
				}
				if (!isExists) {
					// not exists item
					Map<String, Object> taxTotalItemNew = FastMap.newInstance();
					BigDecimal amount = returnHeaderAdj.getBigDecimal("amount");
					taxTotalItemNew.put("returnAdjustmentTypeId", returnHeaderAdj.returnAdjustmentTypeId);
					taxTotalItemNew.put("sourcePercentage", returnHeaderAdj.getBigDecimal("sourcePercentage"));
					taxTotalItemNew.put("productPromoId", returnHeaderAdj.productPromoId);
					taxTotalItemNew.put("amount", amount);
					taxTotalItemNew.put("description", returnHeaderAdj.description);
					taxTotalItemNew.put("comments", returnHeaderAdj.comments);
					returnHeaderAdjustments.add(taxTotalItemNew);
				}*/
			} else if ("RET_PROMOTION_ADJ".equals(returnAdjustmentTypeId)) {
				if (UtilValidate.isNotEmpty(returnHeaderAdj.amount)) {
					otherAdjAmount = otherAdjAmount.add(returnHeaderAdj.getBigDecimal("amount"));
				}
				
				boolean isExists = false;
				for (Map<String, Object> taxTotalItem : returnHeaderAdjustments) {
					if ("RET_PROMOTION_ADJ".equals(returnAdjustmentTypeId)) {
						if (taxTotalItem.productPromoId == returnHeaderAdj.productPromoId) {
							// exists item
							BigDecimal amount = (BigDecimal) taxTotalItem.get("amount");
							amount = amount.add(returnHeaderAdj.getBigDecimal("amount"));
							taxTotalItem.put("amount", amount);
							isExists = true;
						}
					}
				}
				if (!isExists) {
					// not exists item
					Map<String, Object> taxTotalItemNew = FastMap.newInstance();
					BigDecimal amount = returnHeaderAdj.getBigDecimal("amount");
					taxTotalItemNew.put("returnAdjustmentTypeId", returnHeaderAdj.returnAdjustmentTypeId);
					taxTotalItemNew.put("sourcePercentage", returnHeaderAdj.getBigDecimal("sourcePercentage"));
					taxTotalItemNew.put("productPromoId", returnHeaderAdj.productPromoId);
					taxTotalItemNew.put("amount", amount);
					taxTotalItemNew.put("description", returnHeaderAdj.description);
					taxTotalItemNew.put("comments", returnHeaderAdj.comments);
					returnHeaderAdjustments.add(taxTotalItemNew);
				}
			} else {
				if (UtilValidate.isNotEmpty(returnHeaderAdj.amount)) {
					otherAdjAmount = otherAdjAmount.add(returnHeaderAdj.getBigDecimal("amount"));
				}
				
				Map<String, Object> taxTotalItemNew = FastMap.newInstance();
				BigDecimal amount = returnHeaderAdj.getBigDecimal("amount");
				taxTotalItemNew.put("returnAdjustmentTypeId", returnHeaderAdj.returnAdjustmentTypeId);
				taxTotalItemNew.put("sourcePercentage", returnHeaderAdj.getBigDecimal("sourcePercentage"));
				taxTotalItemNew.put("productPromoId", returnHeaderAdj.productPromoId);
				taxTotalItemNew.put("amount", amount);
				taxTotalItemNew.put("description", returnHeaderAdj.description);
				taxTotalItemNew.put("comments", returnHeaderAdj.comments);
				returnHeaderAdjustments.add(taxTotalItemNew);
			}
		}
	}
	context.returnHeaderAdjustments = returnHeaderAdjustments;
	
	// get return shipping adjustment
	List<GenericValue> returnShippingAdjs = EntityUtil.filterByAnd(returnAdjustments, UtilMisc.toMap("returnAdjustmentTypeId", "RET_SHIPPING_ADJ"));
	if (returnShippingAdjs) {
		for (GenericValue returnShippingAdj : returnShippingAdjs) {
			shippingAmount = shippingAmount.add(returnShippingAdj.getBigDecimal("amount"));
		}
	}
	
	BigDecimal grandTotalReturn = ReturnWorker.getGrandTotalReturn(delegator, returnHeader.returnId);
	context.grandTotalReturn = grandTotalReturn;
	
	String facilityId = returnHeader.getString("destinationFacilityId");
	if (UtilValidate.isNotEmpty(facilityId)) {
		GenericValue facility = delegator.findOne("Facility", ["facilityId" : facilityId], true);
		if (UtilValidate.isNotEmpty(facility)) {
			String facDesc = null;
			if (UtilValidate.isNotEmpty(facility.get("facilityCode"))) {
				facDesc = facility.getString("facilityCode") + " - " + facility.getString("facilityName");
			} else {
				facDesc = facility.getString("facilityId") + " - " + facility.getString("facilityName");
			}
			context.destinationFacility = facDesc;
		}
	}
}

context.taxTotalOrderItems = taxTotalOrderItems;
context.otherAdjAmount = otherAdjAmount;
context.returnSubTotal = returnSubTotal;
context.taxAmount = taxAmount;
context.shippingAmount = shippingAmount;
context.listTaxTotal = listTaxTotal;
