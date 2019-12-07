package com.olbius.basehr.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public class ReportUtils {
	// check PAYROL_EARN_HOURS parent_type
	public static Boolean checkInvoiceItemType(String parentTypeId, String invoiceItemType, Delegator delegator) throws GenericEntityException{
		List<String> listData = new ArrayList<String>();
		listData = getAllInvoiceItemTypeIdChildren(parentTypeId,delegator);
		if (listData != null)
		{
			for (String strValue : listData) {
				if (strValue.equals(invoiceItemType))
					return true;
			}
		}
		return false;
	}
	
	public static List<String> getAllInvoiceItemTypeIdChildren(String parentTypeId, Delegator delegator) throws GenericEntityException{
		List<String> listData = new ArrayList<String>();
		List<GenericValue> listTmp = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId), UtilMisc.toSet("invoiceItemTypeId"), null, null, false);
		if(listTmp != null){
			for (GenericValue genericValue : listTmp) {
				listData.add((String)genericValue.get("invoiceItemTypeId"));
				listData.addAll(getAllInvoiceItemTypeIdChildren((String)genericValue.get("invoiceItemTypeId"), delegator));
			}
		}
		return listData;
	}	
}
