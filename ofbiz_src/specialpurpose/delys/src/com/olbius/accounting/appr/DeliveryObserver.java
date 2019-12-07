package com.olbius.accounting.appr;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public class DeliveryObserver extends Observer {
	public static final String module = Observer.class.getName();
	
	//Update Delivery Status
	@Override
	public void updateExported(String deliveryId, Delegator delegator) throws GenericEntityException {
		List<GenericValue> listDeliveryItem = null;
		GenericValue delivery = null;
		try {
			listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false);
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		
		//Check if all delivery item is exported
		boolean isAllExported = true;
		for(GenericValue item: listDeliveryItem){
			if(!item.getString("statusId").equals("DELI_ITEM_EXPORTED")){
				isAllExported = false;
				break;
			}
		}
		
		//Update delivery status if All item are exported
		if(isAllExported){
			delivery.put("statusId", "DLV_EXPORTED");
			delivery.store();
		}
		
	}
	
	public void updateDelivered(String deliveryId, Delegator delegator) throws GenericEntityException {
		List<GenericValue> listDeliveryItem = null;
		GenericValue delivery = null;
		listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false);
		delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		
		//Check if all delivery item is delivered
		boolean isAllDelivered = true;
		for(GenericValue item: listDeliveryItem){
			if(!item.getString("statusId").equals("DELI_ITEM_DELIVERED")){
				isAllDelivered = false;
				break;
			}
		}
		
		//Update delivery status if All item are exported
		if(isAllDelivered){
			delivery.put("statusId", "DLV_DELIVERED");
			delivery.store();
		}
	}
	
}
