package com.olbius.basepo.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;


public class POUtil {
	public static String module = POUtil.class.getName();
	public static List<GenericValue> getIteratorPartialList(EntityListIterator listIterator, Map<String, String[]> parameters, Map<String, Object> successResult) {
    	List<GenericValue> returnValue = new ArrayList<GenericValue>();
    	String viewIndexStr = (String) parameters.get("pagenum")[0];
    	String viewSizeStr = (String) parameters.get("pagesize")[0];
    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
    	try {
    		if (UtilValidate.isNotEmpty(listIterator) && listIterator.getResultsTotalSize() > 0) {
    			if (viewSize != 0) {
    				if (viewIndex == 0) {
    					returnValue = listIterator.getPartialList(0, viewSize);
    				} else {
    					returnValue = listIterator.getPartialList(viewIndex * viewSize + 1, viewSize);
    				}
    			} else {
    				returnValue = listIterator.getCompleteList();
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling processIterator service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally {
			if (listIterator != null) {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
		}
		
    	if (listIterator != null) {
			try {
				int totalRows = listIterator.getResultsSizeAfterPartialList();
				successResult.put("TotalRows", String.valueOf(totalRows));
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error when get size of list iterator", module);
			} finally {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
    	}
		
    	return returnValue;
    }
	
	public static List<GenericValue> getOrderItemEditable(Delegator delegator, String orderId) throws GenericEntityException {
		List<GenericValue> listOrderItems = FastList.newInstance();
		EntityCondition condOd = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition condSt = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ITEM_APPROVED", "ITEM_CREATED"));
		EntityCondition condPromo1 = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "N");
		EntityCondition condPromo2 = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, null);
		List<EntityCondition> condOrs = FastList.newInstance();
		condOrs.add(condPromo1);
		condOrs.add(condPromo2);
		EntityCondition condPromo = EntityCondition.makeCondition(condOrs, EntityOperator.OR);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(condOd);
		conds.add(condSt);
		conds.add(condPromo);
		List<GenericValue> listItemInit = delegator.findList("InventoryItemReceiveAndOrderItem", EntityCondition.makeCondition(conds), null, null, null, false);
		List<String> listItemReceived = FastList.newInstance();
		if (!listItemInit.isEmpty()){
			for (GenericValue item : listItemInit) {
				String orderItemSeqId = item.getString("orderItemSeqId");
				EntityCondition cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
				EntityCondition cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
				List<GenericValue> listExportInv = delegator.findList("InventoryItemReceiveByOrder",
						EntityCondition.makeCondition(cond1, cond2), null, null, null, false);
				if (!listExportInv.isEmpty()){
					BigDecimal quantity = item.getBigDecimal("quantity");
					BigDecimal quantityReceived = ProductUtil.getOrderItemQuantityReceived(delegator, orderId, orderItemSeqId);
					quantity = quantity.subtract(quantityReceived); 
					if (quantity.compareTo(BigDecimal.ZERO) > 0){
						String quantityPurchase = item.getString("quantityUomId");
						String productId = item.getString("productId");
						String quantityUomIdBase = item.getString("quantityUomIdBase");
						BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, quantityPurchase, quantityUomIdBase);
						BigDecimal alternativeQuantity = quantity.divide(convert, RoundingMode.HALF_DOWN);
						item.put("quantity", quantity);
						item.put("alternativeQuantity", alternativeQuantity);
						listOrderItems.add(item);
					}
					listItemReceived.add(orderItemSeqId);
				} else {
					listOrderItems.add(item);
				}
			}
		}
		
		if (!listItemReceived.isEmpty()){
			EntityCondition condRc = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_IN, listItemReceived);
			conds.add(condRc);
		} 
		List<GenericValue> listItemOthers = delegator.findList("OrderItemAndProductDetail", EntityCondition.makeCondition(conds), null, null, null, false);
		if (!listItemOthers.isEmpty()){
			listOrderItems.addAll(listItemOthers);
		}
		return listOrderItems;
	}
	
	public static String checkOrderEditable(Delegator delegator, String orderId) throws GenericEntityException {
		String editable = "N";
		GenericValue objOrderHeader = null;
		try {
			objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne OrderHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return editable;
		}
		if (UtilValidate.isNotEmpty(objOrderHeader)) {
			if ("ORDER_CREATED".equals(objOrderHeader.getString("statusId")) || "ORDER_IN_TRANSIT".equals(objOrderHeader.getString("statusId")) || "ORDER_APPROVED".equals(objOrderHeader.getString("statusId"))){
				editable = "Y";
				List<GenericValue> listItems = FastList.newInstance();
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				try {
					listItems = delegator.findList("ProductPromoUse", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList OrderItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return editable;
				}
				if (!listItems.isEmpty()){
					List<GenericValue> listShipmentReceipts = FastList.newInstance();
					try {
						listShipmentReceipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList ShipmentReceipt: " + e.toString();
						Debug.logError(e, errMsg, module);
						return editable;
					}
					if (!listShipmentReceipts.isEmpty()){
						editable = "N";
					}
				}
			}
		}
		return editable;
	}
}
