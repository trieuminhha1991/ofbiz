import java.lang.*;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.accounting.payment.PaymentWorker;
import javolution.util.FastMap;

if (userLogin) {
    partyId = userLogin.partyId;
    Map<String, Object> info = FastMap.newInstance();
    basicInfo = delegator.findOne("Person", [partyId : partyId], false);
    if(basicInfo != null){
	firstName = UtilValidate.isNotEmpty(basicInfo.firstName) ? basicInfo.firstName : "";
	    middleName = UtilValidate.isNotEmpty(basicInfo.middleName) ? basicInfo.middleName : "";
	    lastName = UtilValidate.isNotEmpty(basicInfo.lastName) ? basicInfo.lastName : "";
	    fullName = lastName + " " + middleName + " " + firstName;
	    fullName = fullName.replaceAll("\\s+", " ");
	    info.fullName = fullName;
		info.gender = basicInfo.gender;
    }

    listEmail = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition([partyId : partyId]),
														EntityCondition.makeCondition(contactMechPurposeTypeId : "PRIMARY_EMAIL"))), null, UtilMisc.toList("-contactMechId"), null, false);

	if(UtilValidate.isNotEmpty(listEmail)){
		email = listEmail[0];
		info.email = email.infoString;
	}
	listShipping = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition([partyId : partyId]),
														EntityCondition.makeCondition(contactMechPurposeTypeId : "SHIPPING_LOCATION"))), null, UtilMisc.toList("-contactMechId"), null, false);
	if(UtilValidate.isNotEmpty(listShipping)){
		shipping = listShipping[0];
		info.address = shipping.address1;
		info.city = shipping.stateProvinceGeoId;
		info.district = shipping.districtGeoId;
		cityGeo = delegator.findOne("Geo", [geoId : shipping.stateProvinceGeoId], false);
		if(cityGeo != null){
			info.cityGeoName = cityGeo.geoName;
		}

		districtGeo = delegator.findOne("Geo", [geoId : shipping.districtGeoId], false);
		if(districtGeo != null){
			info.districtGeoName = districtGeo.geoName;
		}

	}
	listPhone = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition([partyId : partyId]),
														EntityCondition.makeCondition(contactMechPurposeTypeId : "PHONE_BILLING"))), null, UtilMisc.toList("-contactMechId"), null, false);
	if(UtilValidate.isNotEmpty(listPhone)){
		phone = listPhone[0];
		info.phoneNumber = phone.contactNumber;
	}
	context.basicInfo = info;
}
