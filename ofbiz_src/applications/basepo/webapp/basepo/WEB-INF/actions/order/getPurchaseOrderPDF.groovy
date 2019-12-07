import java.util.Map;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import java.util.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import javolution.util.FastList;

String userLoginId = userLogin.userLoginId;
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
String createdBy = orderHeader.getString("createdBy");
GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
String partyId = userLogin.getString("partyId");
GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false); 
String firstName = person.getString("firstName");
String middleName = person.getString("middleName");
String lastName = person.getString("lastName");
List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
Timestamp shipByDate = null;
List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
listOrderItem = EntityUtil.filterByCondition(listOrderItem, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
if(!listOrderItemShipGroup.isEmpty()){
	for (GenericValue orderItemShipGroup : listOrderItemShipGroup) {
		shipByDate = orderItemShipGroup.getTimestamp("shipByDate");
	}
}

String originFacilityId = orderHeader.getString("originFacilityId");
Date currentDate = new Date();
long currentDateLong = currentDate.getTime();
Timestamp currentDateTime = new Timestamp(currentDateLong);
List<GenericValue> listFacilityParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", originFacilityId, "roleTypeId", UtilProperties.getPropertyValue("baselogistics.properties", "role.storekeeper"))), null, null, null, false);
String partyIdFacility = "";
for (GenericValue facilityParty : listFacilityParty) {
	Timestamp thruDate = facilityParty.getTimestamp("thruDate");
	if(thruDate != null){
		long thruDateLog = thruDate.getTime();
		if(thruDateLog.compareTo(currentDateTime) >= 0){
			partyIdFacility = facilityParty.getString("partyId");
		}
	}else{
		partyIdFacility = facilityParty.getString("partyId");
	}
}

List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", originFacilityId)), null, null, null, false);
String contactMechIdFacility = "";
for (GenericValue facilityContactMech : listFacilityContactMech) {
	Timestamp thruDateContactMech = facilityContactMech.getTimestamp("thruDate");
	if(thruDateContactMech != null){
		if(thruDateContactMech.compareTo(currentDateTime) >= 0){
			contactMechIdFacility = facilityContactMech.getString("contactMechId");
		}
	}else{
		contactMechIdFacility = facilityContactMech.getString("contactMechId");
	}
}
String contactNumber = "";

String lastNameStorekeeper = "";

List<GenericValue> listOrderContactMech = delegator.findList("OrderContactMech", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), null, null, null, false);
GenericValue postalAddress = null;
if(!listOrderContactMech.isEmpty()){
	for (GenericValue orderContactMech : listOrderContactMech) {
		String contactMechId = orderContactMech.getString("contactMechId");
		postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		if(postalAddress.getString("toName") != null){
			lastNameStorekeeper = postalAddress.getString("toName");
		}
		if(postalAddress.getString("attnName") != null){
			contactNumber = postalAddress.getString("attnName");
		}
	}
}
String address = "";
if(postalAddress != null){
	address = postalAddress.getString("address1");
}



String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", orgId), false);
String groupName = "";
if(partyGroup != null){
	groupName = partyGroup.getString("groupName");
}

String orderDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(orderHeader.getTimestamp("orderDate"));
String shipByDateStr = new SimpleDateFormat("MM/dd/yyyy").format(shipByDate);
context.orderHeader = orderHeader;
context.orderDate = orderDate;
context.address = address;
context.middleName = middleName;
context.firstName = firstName;
context.lastName = lastName;
context.shipByDateStr = shipByDateStr;
context.listOrderItem = listOrderItem;
context.lastNameStorekeeper = lastNameStorekeeper;
context.contactNumber = contactNumber;
context.groupName = groupName;

	List<GenericValue> orderRole = FastList.newInstance();
	orderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
	String partySupplierName = null;
	if (UtilValidate.isNotEmpty(orderRole)){
		String partyId2 = (String)orderRole.get(0).get("partyId");
			GenericValue partyGroup2 = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId2), false);
			if(partyGroup2 != null){
				partySupplierName = partyGroup2.getString("groupName"); 
			}
	}
	context.partySupplierName = partySupplierName;
