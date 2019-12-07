import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public List<GenericValue> getRoleChilds (String ancestor) {
	List<GenericValue> returnValue = FastList.newInstance();
	GenericValue thisValue = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", ancestor), false);
	returnValue.add(thisValue);
	List<GenericValue> listRoleChild = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", ancestor), null, false);
	if (listRoleChild != null) {
		for (GenericValue roleItem : listRoleChild) {
			List<GenericValue> listRoleChild2 = getRoleChilds(roleItem.getString("roleTypeId"));
			if (listRoleChild2 != null) {
				returnValue.addAll(listRoleChild2);
			}
		}
	}
	return returnValue;
}

public String getDisplayName(GenericValue value) {
	String displayName = "";
	if (value.get("firstName") != null) {
		String firstName = value.get("firstName");
		String middleName = null;
		String lastName = null;
		if (value.get("middleName")) middleName = value.get("middleName");
		if (value.get("lastName")) middleName = value.get("lastName");
		if (lastName) displayName += lastName;
		if (middleName) {
			if (lastName) displayName += " ";
			displayName += middleName;
		}
		if (firstName) {
			if (lastName || middleName) displayName += " ";
			displayName += firstName;
		}
	} else {
		if (value.get("groupName")) displayName += value.get("groupName");
	}
	return displayName;
}

String customerStr = "";
String distributorStr = "";
String postalAddressStr = "";
if (requirement) {
	List<GenericValue> listRoleCustomer = getRoleChilds("DELYS_CUSTOMER");
	if (listRoleCustomer != null) {
		List<String> listRoleIdCustomer = EntityUtil.getFieldListFromEntityList(listRoleCustomer, "roleTypeId", true);
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("requirementId", requirement.requirementId));
		listConds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleIdCustomer));
		List<GenericValue> listReqRole = delegator.findList("RequirementRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		if (listReqRole != null) {
			List<String> listCustomerId = EntityUtil.getFieldListFromEntityList(listReqRole, "partyId", true);
			if (listCustomerId != null) {
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<GenericValue> listCustomerName = delegator.findList("PartyNameView", EntityCondition.makeCondition("partyId", EntityOperator.IN, listCustomerId), null, null, findOptions, false);
				if (listCustomerName != null) {
					for (int i = 0; i < listCustomerName.size(); i++) {
						GenericValue customerName = listCustomerName.get(i);
						if (i > 0) {
							customerStr += ", ";
						}
						customerStr += getDisplayName(customerName);
					}
				}
			}
		}
	}
	
	List<GenericValue> listRoleDistributor = getRoleChilds("DELYS_DISTRIBUTOR");
	if (listRoleDistributor != null) {
		List<String> listRoleIdDistributor = EntityUtil.getFieldListFromEntityList(listRoleDistributor, "roleTypeId", true);
		List<EntityCondition> listConds2 = FastList.newInstance();
		listConds2.add(EntityCondition.makeCondition("requirementId", requirement.requirementId));
		listConds2.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleIdDistributor));
		List<GenericValue> listReqRole2 = delegator.findList("RequirementRole", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
		if (listReqRole2 != null) {
			List<String> listDistributorId = EntityUtil.getFieldListFromEntityList(listReqRole2, "partyId", true);
			if (listDistributorId != null) {
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<GenericValue> listDistributorName = delegator.findList("PartyNameView", EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistributorId), null, null, findOptions, false);
				if (listDistributorName != null) {
					for (int i = 0; i < listDistributorName.size(); i++) {
						GenericValue distributorName = listDistributorName.get(i);
						if (i > 0) {
							distributorStr += ", ";
						}
						distributorStr += getDisplayName(distributorName);
					}
				}
			}
		}
	}
	
	if (requirement.contactMechId != null) {
		GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", requirement.contactMechId), false);
		if (postalAddress != null) {
			if (postalAddress.address1 != null) postalAddressStr += postalAddress.address1;
		}
	}
	
	GenericValue currentStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", requirement.statusId), false);
	context.currentStatus = currentStatus;
	
	List<GenericValue> listStatusGV = delegator.findByAnd("RequirementStatus", UtilMisc.toMap("requirementId", requirement.requirementId), null, false);
	List<Map<String, Object>> listStatus = FastList.newInstance();
	if (listStatusGV != null) {
		for (GenericValue reqStatusItem : listStatusGV) {
			Map<String, Object> statusItemMap = FastMap.newInstance();
			GenericValue statusItemGV = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", reqStatusItem.get("statusId")), false);
			if (statusItemGV != null) {
				statusItemMap.statusItem = statusItemGV;
				statusItemMap.statusUserLogin = reqStatusItem.get("statusUserLogin");
				listStatus.add(statusItemMap);
			}
		}
	}
	context.listStatus = listStatus;
	
	List<GenericValue> returnRequirementCommitment = delegator.findByAnd("ReturnRequirementCommitment", UtilMisc.toMap("requirementId", requirement.get("requirementId")), null, false);
	if (returnRequirementCommitment != null) {
		List<String> returnIds = EntityUtil.getFieldListFromEntityList(returnRequirementCommitment, "returnId", true);
		if (returnIds != null && returnIds.size() > 0) {
			context.returnIds = returnIds;
		}
	}
	
	List<String> orderSoldIds = EntityUtil.getFieldListFromEntityList(
		delegator.findByAnd("OrderRequirementCommitment", UtilMisc.toMap("requirementId", requirement.get("requirementId")), null, false), "orderId", true);
	if (UtilValidate.isNotEmpty(orderSoldIds)) {
		context.orderSoldIds = orderSoldIds;
	}
}
context.customerStr = customerStr;
context.distributorStr = distributorStr;
context.postalAddressStr = postalAddressStr;