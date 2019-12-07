package com.olbius.baselogistics.delivery;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

public abstract class Observer {
	public  abstract void updateExported(String deliveryId, Delegator delegator) throws GenericEntityException;
	public  abstract void updateDelivered(String deliveryId, Delegator delegator) throws GenericEntityException;
}
