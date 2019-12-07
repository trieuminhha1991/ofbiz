import java.util.*;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

/*
boolean isSalesSup = false;
boolean isAsm = false;
boolean isSalesAdmin = false;
boolean isRsm = false;
boolean isNbd = false;
boolean isCeo = false;
boolean isDistributor = false;
boolean isChiefAccountant = false;
 */
boolean isPlacingCustomer = false;
boolean isEditOrderAdjustment = false;
boolean isEditExpireDate = false;
boolean isEditQuantity = false;
boolean isAddNewItem = false;
boolean isViewAtpQoh = false;
if (isChiefAccountant) {
	isEditQuantity = true;
	isAddNewItem = true;
	isEditOrderAdjustment = true;
	isViewAtpQoh = true;
}
if (isSalesAdmin) {
	isEditQuantity = true;
	isAddNewItem = true;
	isEditExpireDate = true;
	isViewAtpQoh = true;
}
if (isLog) {
	isEditExpireDate = true;
	isViewAtpQoh = true;
}
if (isDistributor) {
	String orderId = parameters.orderId;
	GenericValue orderRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false));
	if (UtilValidate.isNotEmpty(orderRole)) {
		if (orderRole.getString("partyId") != null && orderRole.getString("partyId").equals(userLogin.getString("partyId"))) {
			isPlacingCustomer = true;
		}
	}
}
if (isDistributor || isSalesSup) {
	isEditQuantity = true;
	isAddNewItem = true;
}

context.isPlacingCustomer = isPlacingCustomer;
context.isEditOrderAdjustment = isEditOrderAdjustment;
context.isEditExpireDate = isEditExpireDate;
context.isEditQuantity = isEditQuantity;
context.isAddNewItem = isAddNewItem;
context.isViewAtpQoh = isViewAtpQoh;