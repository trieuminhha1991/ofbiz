package com.olbius.baselogistics.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

import javolution.util.FastList;
import javolution.util.FastMap;


public class LogisticsUtil {
	public static String module = LogisticsUtil.class.getName();
	public static List<GenericValue> getIteratorPartialList(EntityListIterator listIterator, Map<String, String[]> parameters, Map<String, Object> successResult) {
    	List<GenericValue> returnValue = new ArrayList<GenericValue>();
    	String viewIndexStr = null;
    	if (UtilValidate.isNotEmpty(parameters.get("pagenum")) && parameters.get("pagenum").length > 0) {
    		viewIndexStr = (String)parameters.get("pagenum")[0];
		}
    	String viewSizeStr = null;
    	if (UtilValidate.isNotEmpty(parameters.get("pagesize")) && parameters.get("pagesize").length > 0) {
    		viewSizeStr = (String)parameters.get("pagesize")[0];
    	}
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
	public static Map<String, Object> getBiggestUom(Delegator delegator, String productId) {
		Map<String, Object> uomMap = FastMap.newInstance();
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conds.add(EntityCondition.makeCondition("largest", EntityOperator.EQUALS, "Y"));
		
		try {
			List<GenericValue> listUom = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null, null, null, false);
			
			if (UtilValidate.isNotEmpty(listUom)){
				GenericValue uom = listUom.get(0);
				uomMap.put("uomId", uom.getString("uomFromId"));
				uomMap.put("quantityConvert", uom.getBigDecimal("quantityConvert"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return uomMap;
	}
	public static String getUomDescription(Delegator delegator, Locale locale, Object uomId) throws GenericEntityException {
		String description = "";
		GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), true);
		if (UtilValidate.isNotEmpty(uom)) {
			description = (String) uom.get("description", locale);
		}
		return description;
	}
}
