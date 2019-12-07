package com.olbius.baselogistics.delivery;

import java.math.BigDecimal;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class DeliveryItemSubject extends ItemSubject{
	enum Status{
		DELI_ITEM_EXPORTED, DELI_ITEM_DELIVERED;
	}
	
	public static final String module = DeliveryItemSubject.class.getName();
	
	@Override
	public void attach(Observer o) {
		observers.add(o);
	}

	@Override
	public void deattach(Observer o) {
		observers.remove(o);
		
	}

	@Override
	public void updateDeliveryStatus(String deliveryId, Delegator delegator) throws GenericEntityException {
		for(Observer o : observers){
			switch (Status.valueOf(statusId)) {
			case DELI_ITEM_DELIVERED:
				o.updateDelivered(deliveryId, delegator);
				break;
			case DELI_ITEM_EXPORTED:
				o.updateExported(deliveryId, delegator);
				break;
			default:
				break;
			}
		}
	}
	
	public void updateExportedAmount(Map<String, Object> parameters) throws GenericEntityException{
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId");
		String deliveryItemId = (String)parameters.get("deliveryItemSeqId");
		BigDecimal exportedAmount = (BigDecimal)parameters.get("actualExportedAmount");
		statusId = "DELI_ITEM_EXPORTED";
		Delegator delegator = (Delegator)parameters.get("delegator");
		
		//Update DeliveryItem
		GenericValue deliveryItem = null;
		try {
			deliveryItem = delegator.findOne("DeliveryItem", UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		deliveryItem.put("actualExportedAmount", exportedAmount);
		deliveryItem.put("statusId", statusId);
		try {
			deliveryItem.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		//Update Delivery
		updateDeliveryStatus(deliveryId, delegator);
	}
	
	public void updateDeliveredAmount(Map<String, Object> parameters) throws GenericEntityException{
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId");
		String deliveryItemId = (String)parameters.get("deliveryItemSeqId");
		BigDecimal deliveredAmount = (BigDecimal)parameters.get("actualDeliveredAmount");
		statusId = "DELI_ITEM_DELIVERED";
		Delegator delegator = (Delegator)parameters.get("delegator");
		
		//Update DeliveryItem
		GenericValue deliveryItem = null;
		try {
			deliveryItem = delegator.findOne("DeliveryItem", UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		deliveryItem.put("actualDeliveredAmount", deliveredAmount);
		deliveryItem.put("statusId", statusId);
		try {
			deliveryItem.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		//Update Delivery
		updateDeliveryStatus(deliveryId, delegator);
	}
	
	public void updateExportedQuantity(Map<String, Object> parameters) throws GenericEntityException{
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId");
		String deliveryItemId = (String)parameters.get("deliveryItemSeqId");
		BigDecimal exportedQuantity = (BigDecimal)parameters.get("actualExportedQuantity");
		statusId = "DELI_ITEM_EXPORTED";
		Delegator delegator = (Delegator)parameters.get("delegator");
		
		//Update DeliveryItem
		GenericValue deliveryItem = null;
		try {
			deliveryItem = delegator.findOne("DeliveryItem", UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		deliveryItem.put("actualExportedQuantity", exportedQuantity);
		deliveryItem.put("statusId", statusId);
		try {
			deliveryItem.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		//Update Delivery
		updateDeliveryStatus(deliveryId, delegator);
	}
	
	public void updateDeliveredQuantity(Map<String, Object> parameters) throws GenericEntityException{
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId");
		String deliveryItemId = (String)parameters.get("deliveryItemSeqId");
		BigDecimal deliveredQuantity = (BigDecimal)parameters.get("actualDeliveredQuantity");
		statusId = "DELI_ITEM_DELIVERED";
		Delegator delegator = (Delegator)parameters.get("delegator");
		
		//Update DeliveryItem
		GenericValue deliveryItem = null;
		try {
			deliveryItem = delegator.findOne("DeliveryItem", UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		deliveryItem.put("actualDeliveredQuantity", deliveredQuantity);
		deliveryItem.put("statusId", statusId);
		try {
			deliveryItem.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		//Update Delivery
		updateDeliveryStatus(deliveryId, delegator);
	}
	
}
