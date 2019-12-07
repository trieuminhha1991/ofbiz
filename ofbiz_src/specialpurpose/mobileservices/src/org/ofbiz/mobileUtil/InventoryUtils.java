package org.ofbiz.mobileUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

public class InventoryUtils {
	/* init array of inventory object */
	public static List<Map<String, Object>> initListInventoryObject(
			List<GenericValue> productOrders) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		List<Map<String, Object>> res = FastList.newInstance();
		for (GenericValue inventory : productOrders) {
			String productId = inventory.getString("productId");
			String productName = inventory.getString("productName");
			String orderId = inventory.getString("orderId");
			Timestamp orderDate = inventory.getTimestamp("orderDate");
			String orderStr = simpleDateFormat.format(orderDate);
			BigDecimal quantity = inventory.getBigDecimal("quantity");
			Map<String, Object> tempMap = initInventoryObject(productId,
					productName, orderId, orderStr, quantity);
			res.add(tempMap);
		}
		return res;
	}
	/* init inventory object */
	public static Map<String, Object> initInventoryObject(String productId,
			String productName, String orderId, String orderStr,
			BigDecimal quantity) {
		Map<String, Object> tempMap = FastMap.newInstance();
		tempMap.put("productId", productId);
		tempMap.put("productName", productName);
		tempMap.put("orderId", orderId);
		tempMap.put("orderDate", orderStr);
		tempMap.put("qtyInInventory", quantity);
		return tempMap;
	}
	public static void createCustomerInventory(Delegator delegator, String productId, String partyId, BigDecimal quantity, Timestamp fromDate, String createdBy){
		try {
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("productId", productId);
			ctxMap.put("partyId", partyId);
			ctxMap.put("qtyInInventory", quantity);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("createdBy", createdBy);
			ctxMap.put("isLastUpdateInventory", "Y");
			delegator.create("CustomerProductInventory", ctxMap);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
	}
}
