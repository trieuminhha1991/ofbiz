package com.olbius.accounting.appr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

public abstract class ItemSubject {
	
	//Variables
	protected String statusId;
	protected List<Observer> observers;
	
	public ItemSubject(){
		observers = new ArrayList<Observer>();
	}
	public abstract void attach(Observer o);
	public abstract void deattach(Observer o);
	public abstract void updateDeliveredQuantity(Map<String, Object> parameters) throws GenericEntityException;
	public abstract void updateExportedQuantity(Map<String, Object> parameters) throws GenericEntityException;
	public abstract void updateDeliveryStatus(String deliveryId, Delegator delegator) throws GenericEntityException;
}
