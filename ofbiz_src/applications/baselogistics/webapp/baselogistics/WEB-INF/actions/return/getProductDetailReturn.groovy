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
import com.olbius.security.util.SecurityUtil;

returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
String originContactMechId =  returnHeader.getString("originContactMechId");
String currencyUomId =  returnHeader.getString("currencyUomId");
String fullOriginContactMechId = "";
postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", originContactMechId), false);
if(postalAddress != null){
	String toName = postalAddress.getString("toName");
	String attnName = postalAddress.getString("attnName");
	String address1 = postalAddress.getString("address1");
	String city = postalAddress.getString("city");
	String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
	if(address1 != null){
		fullOriginContactMechId = address1;
	}
}

String userLoginId = returnHeader.createdBy;
GenericValue userLoginCreatedBy = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
String partyId = userLoginCreatedBy.getString("partyId");
GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false); 
/*String firstName = null;
String middleName = null;
String lastName = null;
if (person != null) {
	firstName = person.getString("firstName");
	middleName = person.getString("middleName");
	lastName = person.getString("lastName");
}*/

String firstName = person.getString("firstName");
String middleName = person.getString("middleName");
String lastName = person.getString("lastName");

String fullNameCreateBy = "";
if(middleName != null){
	fullNameCreateBy = lastName + " " + middleName + " " + firstName;
}else{
	fullNameCreateBy = lastName + " " + firstName;
}
GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", currencyUomId), false);
String uomIdContext = "";
if (uom != null) {
	String abbreviation =  uom.getString("abbreviation");
	String descriptionUomId = uom.getString("description");
	//String uomIdContext = abbreviation + "-" + descriptionUomId;
	uomIdContext = abbreviation + "-" + descriptionUomId;
}
if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "LOG_RETURN")){
	if ("VENDOR_RETURN".equals(returnHeader.getString("returnHeaderTypeId"))){
		context.selectedSubMenuItem = "ListSupReturn";
	} else {
		context.selectedSubMenuItem = "ListCusReturn";
	}
	context.selectedMenuItem = "Return";
} else {
	if ("VENDOR_RETURN".equals(returnHeader.getString("returnHeaderTypeId"))){
		context.selectedMenuItem = "StockOut";
		context.selectedSubMenuItem = "ReturnPO";
	} else {
		context.selectedMenuItem = "StockIn";
		context.selectedSubMenuItem = "ReturnSO";
	}
}

context.returnHeader = returnHeader;
context.fullOriginContactMechId = fullOriginContactMechId;
context.fullNameCreateBy = fullNameCreateBy;
context.uomIdContext = uomIdContext;