package org.ofbiz.mobileUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

public class OrderUtils {
	/* get new order */
	public static List<GenericValue> getNewOrder(Delegator delegator,
			String customerId, Timestamp fromDateTime)
			throws GenericEntityException {
		EntityCondition party = EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, customerId);

		Set<String> fields = UtilMisc.toSet("productId", "productName",
				"quantity", "orderDate", "orderId");
		EntityCondition orderStatus = EntityCondition.makeCondition("statusId",
				EntityOperator.EQUALS, "ORDER_COMPLETED");
		EntityCondition rt = EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "BILL_TO_CUSTOMER");
		List<EntityCondition> conditionList = UtilMisc.toList(party,
				orderStatus, rt);
		if (fromDateTime != null) {
			EntityCondition greaterOrderDate = EntityCondition
					.makeCondition("statusDatetime",
							EntityOperator.GREATER_THAN, fromDateTime);
			conditionList.add(greaterOrderDate);
		}
		EntityCondition orderCon = EntityCondition.makeCondition(conditionList,
				EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("orderDate DESC");
		List<GenericValue> productOrders = delegator.findList(
				"OrderProductDetail", orderCon, fields, orderBy, null, false);
		return productOrders;
	}
	@SuppressWarnings("unchecked")
	public static int getTotalOrderByStatus(Delegator delegator, String createdBy, List<String> partyIds, Timestamp fromDate, Timestamp thruDate, String statusId) throws GenericEntityException {
		EntityListIterator list = null;
		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			EntityCondition conditions = EntityCondition.makeCondition(UtilMisc.toList(
				EntityCondition.makeCondition("roleTypeId", "PLACING_CUSTOMER"),
				EntityCondition.makeCondition("statusId", statusId),
				EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds),
				EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, createdBy),
				EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate),
				EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate)
			));
			list = delegator.find("OrderHeaderDetail", conditions, null, null, null, options);
			return list.getResultsTotalSize();
		} catch (Exception e) {
			Debug.log(e.getMessage());
		} finally{
			list.close();
		}
		return 0;
		// "orderStatusState", statusState
	}
}
