package com.olbius.acc.equip;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import java.util.List;

public class EquipmentUtils {

	public static Boolean isEquipmentPosted(Delegator delegator, String equipmentId) throws GenericEntityException {
		List<GenericValue> equipmentItemList = delegator.findByAnd("EquipmentIncreaseItem", UtilMisc.toMap("equipmentId", equipmentId), null, false);
		if(UtilValidate.isNotEmpty(equipmentItemList)){
			return true;
		}
		equipmentItemList = delegator.findByAnd("EquipmentDecreaseItem", UtilMisc.toMap("equipmentId", equipmentId), null, false);
		if(UtilValidate.isNotEmpty(equipmentItemList)){
			return true;
		}
		equipmentItemList = delegator.findByAnd("EquipmentAllocItem", UtilMisc.toMap("equipmentId", equipmentId), null, false);
		if(UtilValidate.isNotEmpty(equipmentItemList)){
			return true;
		}
		return false;
	}

}
