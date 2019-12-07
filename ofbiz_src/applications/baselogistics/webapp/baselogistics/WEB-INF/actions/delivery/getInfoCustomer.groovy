/*
* Prepare for Delivery Note
*/
import java.util.*;
import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;

deliveryId = parameters.deliveryId;
delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);
String partyIdTo = delivery.getString("partyIdTo");
String orderIdByDelivery = delivery.getString("orderId");
String contactMechId;
List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
listPartyContactMechPurpose = EntityUtil.filterByDate(listPartyContactMechPurpose);
if(!listPartyContactMechPurpose.isEmpty()){
	for(GenericValue partyContactMechPurpose: listPartyContactMechPurpose ){
		contactMechId = partyContactMechPurpose.getString("contactMechId");
	}
}
telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId",contactMechId), false);
String contactNumber;
if(telecomNumber != null){
	contactNumber = telecomNumber.getString("contactNumber");
}

List<GenericValue> listOrderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderIdByDelivery)), null, null, null, false);
String partyIdCall = "";
String partyIdSale = "";
if(!listOrderRole.isEmpty()){
	for(GenericValue orderRole: listOrderRole ){
		String roleTypeId = orderRole.getString("roleTypeId");
		if(roleTypeId.equals("CALLCENTER_EMPL")){
			partyIdCall = orderRole.getString("partyId");
		}
		if(roleTypeId.equals("SALES_EXECUTIVE")){
			partyIdSale = orderRole.getString("partyId");
		}
	}
}
if(!partyIdCall.equals("")){
	personCall = delegator.findOne("Person", UtilMisc.toMap("partyId",partyIdCall), false);
	if(!partyIdSale.equals("")){
		personSale = delegator.findOne("Person", UtilMisc.toMap("partyId",partyIdSale), false);
		context.personSale = personSale;
	}
	context.personCall = personCall;
}else{
	if(!partyIdSale.equals("")){
		personSale = delegator.findOne("Person", UtilMisc.toMap("partyId",partyIdSale), false);
		context.personSale = personSale;
	}
}

String contactMechIdSales = null;
String contactNumberSales = null;
if (partyIdSale != null && partyIdSale != ""){
	List<GenericValue> listCTMSales = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdSale, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
	if(!listCTMSales.isEmpty()){
		for(GenericValue ctm : listCTMSales ){
			contactMechIdSales = ctm.getString("contactMechId");
		}
	}
	telecomNumberSales = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechIdSales), false);

	if(telecomNumberSales != null){
		contactNumberSales = telecomNumberSales.getString("contactNumber");
	}
}

String contactMechIdCall;
String contactNumberCall;
if (partyIdCall != null && partyIdCall != ""){
	List<GenericValue> listCTMCall = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdCall, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
	if(!listCTMCall.isEmpty()){
		for(GenericValue ctm : listCTMCall ){
			contactMechIdCall = ctm.getString("contactMechId");
		}
	}
	telecomNumberCalls = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechIdCall), false);
	if(telecomNumberCalls != null){
		contactNumberCall = telecomNumberCalls.getString("contactNumber");
	}
}
if (contactNumberSales != null){
	context.contactNumberSales = contactNumberSales;
} else {
	context.contactNumberSales = '';
}

if (contactNumberCall != null){
	context.contactNumberCall = contactNumberCall;
} else {
	context.contactNumberCall = '';
}

context.contactNumber = contactNumber;
context.orderIdByDelivery = orderIdByDelivery;
