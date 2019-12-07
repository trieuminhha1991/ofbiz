package com.olbius.baselogistics.transfer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.product.util.ProductUtil;
import javolution.util.FastList;

public class TransferReadHepler {
    private static String module = TransferReadHepler.class.getName();
    
	public static BigDecimal getTransferTotal(Delegator delegator, String transferId) throws GenericEntityException{
		BigDecimal grandTotal = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(transferId)) {
			GenericValue objTransferHeader = null;
			try {
				objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne TransferHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return BigDecimal.ZERO;
			}
			String statusId = null;
			if (UtilValidate.isNotEmpty(objTransferHeader)) {
				statusId = objTransferHeader.getString("statusId");
				String facilityId = objTransferHeader.getString("originFacilityId");
				if (UtilValidate.isNotEmpty(facilityId)) {
					GenericValue objFacility = null;
					try {
						objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne Facility: " + e.toString();
						Debug.logError(e, errMsg, module);
						return BigDecimal.ZERO;
					}
					
					String organizationPartyId = objFacility.getString("ownerPartyId");
					EntityCondition cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
					EntityCondition cond2 = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(cond1);
					conds.add(cond2);
					if (!"TRANSFER_CANCELLED".equals(statusId)){
						EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TRANSFER_CANCELLED");
						conds.add(cond3);
					} 
					List<GenericValue> listTransferItems = FastList.newInstance();
					try {
						listTransferItems = delegator.findList("TransferItem", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList TransferItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return BigDecimal.ZERO;
					}
					if (!listTransferItems.isEmpty()){
						for (GenericValue item : listTransferItems) {
							BigDecimal quantity = item.getBigDecimal("quantity");
							BigDecimal unitCost = BigDecimal.ZERO;
							String transferItemSeqId = item.getString("transferItemSeqId");
							
							if (UtilValidate.isNotEmpty(item.get("unitCost"))) {
								// sau khi xuat hang, gia von thoi diem xuat se duoc luu lai de lay ra cho nhanh | khong dung neu xuat nhieu lan
								unitCost = item.getBigDecimal("unitCost");
								BigDecimal itemTotal = quantity.multiply(unitCost);
								grandTotal = grandTotal.add(itemTotal);
							} else {
								String productId = item.getString("productId");
								String productAverageCostTypeId = "SIMPLE_AVG_COST";
								
								// cac tranfer qua khu, chua co unitCost, lay gia von tai thoi diem xuat va luu lai unitCost
								// 1. lay gia von
								EntityCondition cond4 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
								EntityCondition cond5 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO);
								conds = FastList.newInstance();
								conds.add(cond1);
								conds.add(cond4);
								conds.add(cond5);
								List<GenericValue> listDetail = FastList.newInstance();
								try {
									listDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
									Debug.logError(e, errMsg, module);
									return BigDecimal.ZERO;
								}
								if (!listDetail.isEmpty()){
									BigDecimal exportedQty = BigDecimal.ZERO;
									for (GenericValue detail : listDetail) {
										Timestamp effectiveDate = detail.getTimestamp("effectiveDate");
										unitCost = ProductUtil.getAverageCostByTime(delegator, productId, facilityId, productAverageCostTypeId, organizationPartyId, effectiveDate);	
										BigDecimal quantityDiff = detail.getBigDecimal("quantityOnHandDiff");
										if (ProductUtil.isWeightProduct(delegator, productId)){
											quantityDiff = detail.getBigDecimal("amountOnHandDiff");
										}
										exportedQty = exportedQty.add(quantityDiff.negate());
										BigDecimal itemTotal = quantityDiff.multiply(unitCost);
										grandTotal = grandTotal.add(itemTotal.negate());
									}
									if (quantity.compareTo(exportedQty) > 0){
										unitCost = ProductUtil.getAverageCostByTime(delegator, productId, facilityId, productAverageCostTypeId, organizationPartyId, null);	
										
										BigDecimal remainQty = quantity.subtract(exportedQty);
										BigDecimal itemTotal = remainQty.multiply(unitCost);
										grandTotal = grandTotal.add(itemTotal);
									}
								} else {
									// chua xuat thi lay gia von hien tai
									unitCost = ProductUtil.getAverageCostByTime(delegator, productId, facilityId, productAverageCostTypeId, organizationPartyId, null);	
									BigDecimal itemTotal = quantity.multiply(unitCost);
									grandTotal = grandTotal.add(itemTotal);
								}
							}
						}
					}
				}
			}
		} 
		
		return grandTotal;
	}
	
	public static BigDecimal getAverageCostProductExportedByDelivery(Delegator delegator, String deliveryId, String productId, String facilityId, String productAverageCostTypeId, String organizationPartyId){
		GenericValue objDelivery = null;
		try {
			objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (UtilValidate.isEmpty(productAverageCostTypeId)) {
			productAverageCostTypeId = "SIMPLE_AVG_COST"; //default
		}
		BigDecimal unitCost = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(objDelivery)) {
			String shipmentId = objDelivery.getString("shipmentId");
			if (UtilValidate.isNotEmpty(shipmentId)) {
				EntityCondition cond1 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
				EntityCondition cond3 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				conds.add(cond3);
				List<GenericValue> listDetail = FastList.newInstance();
				try {
					listDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
					return BigDecimal.ZERO;
				}
				if (!listDetail.isEmpty()){
					for (GenericValue detail : listDetail) {
						Timestamp effectiveDate = detail.getTimestamp("effectiveDate");
						unitCost = ProductUtil.getAverageCostByTime(delegator, productId, facilityId, productAverageCostTypeId, organizationPartyId, effectiveDate);	
					}
				}
			}
		}
		return unitCost;
	}
	
	public static String checkTransferItemStatus(Delegator delegator, String transferId, String transferItemSeqId){
		GenericValue objTransferItem = null;
		try {
			objTransferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne TransferItem: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		String statusId = null;
		if (UtilValidate.isNotEmpty(objTransferItem)) {
			statusId = objTransferItem.getString("statusId");
			if ("TRANS_ITEM_APPROVED".equals(statusId)){
				String productId = objTransferItem.getString("productId");
				EntityCondition cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
				EntityCondition cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
				EntityCondition cond3 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				conds.add(cond2);
				conds.add(cond3);
				List<GenericValue> listDetails = FastList.newInstance();
				try {
					listDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
				}
				BigDecimal quantity = objTransferItem.getBigDecimal("quantity");
				if (ProductUtil.isWeightProduct(delegator, productId)){
					quantity = objTransferItem.getBigDecimal("amount");
				}
				BigDecimal quantityExported = BigDecimal.ZERO;
				if (!listDetails.isEmpty()){
					for (GenericValue detail : listDetails) {
						if (ProductUtil.isWeightProduct(delegator, productId)){
							quantityExported = quantityExported.add(detail.getBigDecimal("amountOnHandDiff"));
						} else {
							quantityExported = quantityExported.add(detail.getBigDecimal("quantityOnHandDiff"));
						}
					}
				}
				quantityExported = quantityExported.negate();
				if (quantityExported.compareTo(quantity) >= 0){
					statusId = "TRANS_ITEM_EXPORTED";
				}
			} else if ("TRANS_ITEM_EXPORTED".equals(statusId)){
				String productId = objTransferItem.getString("productId");
				EntityCondition cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
				EntityCondition cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
				EntityCondition cond3 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				conds.add(cond2);
				conds.add(cond3);
				List<GenericValue> listDetails = FastList.newInstance();
				try {
					listDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
				}
				BigDecimal quantity = objTransferItem.getBigDecimal("quantity");
				if (ProductUtil.isWeightProduct(delegator, productId)){
					quantity = objTransferItem.getBigDecimal("amount");
				}
				BigDecimal quantityDelivered = BigDecimal.ZERO;
				if (!listDetails.isEmpty()){
					for (GenericValue detail : listDetails) {
						if (ProductUtil.isWeightProduct(delegator, productId)){
							quantityDelivered = quantityDelivered.add(detail.getBigDecimal("amountOnHandDiff"));
						} else {
							quantityDelivered = quantityDelivered.add(detail.getBigDecimal("quantityOnHandDiff"));
						}
					}
				}
				if (quantityDelivered.compareTo(quantity) >= 0){
					statusId = "TRANS_ITEM_DELIVERED";
				}
			}
		}
		return statusId;
	}
	
	public static boolean checkTransferDeliveredAPart(Delegator delegator, String transferId){
		GenericValue objTransferHeader = null;
		try {
			objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne TransferHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return false;
		}
		if ("TRANSFER_COMPLETED".equals(objTransferHeader.getString("statusId"))) return false;
		
		EntityCondition cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
		EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DLV_DELIVERED");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond3);
		List<GenericValue> listDlveds = FastList.newInstance();
		try {
			listDlveds = delegator.findList("Delivery", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (!listDlveds.isEmpty()) {
			EntityCondition cond4 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DLV_EXPORTED");
			conds = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond4);
			List<GenericValue> listExporteds = FastList.newInstance();
			try {
				listExporteds = delegator.findList("Delivery", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			if (listExporteds.isEmpty()) return true;
		}
		return false;
	}
	
	
}
