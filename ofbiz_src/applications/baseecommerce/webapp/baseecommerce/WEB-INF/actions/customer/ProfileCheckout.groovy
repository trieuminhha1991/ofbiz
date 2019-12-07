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

import com.olbius.baseecommerce.party.ProfileUtils;

if (UtilValidate.isNotEmpty(userLogin)) {
	String partyId = userLogin.partyId;
	def party = ProfileUtils.CustomerProfile(delegator, partyId);

	context.party = party;
	if(party.TELECOM_NUMBER) {
		if(party.TELECOM_NUMBER.telecomNumber) {
			def telecomNumber = party.TELECOM_NUMBER.telecomNumber;
			context.contactNumber = telecomNumber.contactNumber;
		}
		if(party.TELECOM_NUMBER.contactMech) {
			def contactMech = party.TELECOM_NUMBER.contactMech;
			context.contactNumberId = contactMech.contactMechId
		}
	}
	if(party.EMAIL_ADDRESS) {
		if(party.EMAIL_ADDRESS.contactMech) {
			def contactMech = party.EMAIL_ADDRESS.contactMech;
			context.infoString = contactMech.infoString;
			context.infoStringId = contactMech.contactMechId;
		}
	}
	if(party.BILLING_LOCATION) {
		def postalAddress = party.BILLING_LOCATION;
		context.postalAddressId = postalAddress.contactMechId;
		context.address1 = postalAddress.address1;
		context.stateProvinceGeoId = postalAddress.stateProvinceGeoId;
		context.districtGeoId = postalAddress.districtGeoId;
	}
}else{
	Map<String, Object> info = FastMap.newInstance();
	info.put("fullName", request.getAttribute("fullName"));
	info.put("phoneNumber", request.getAttribute("phone"));
	info.put("email", request.getAttribute("email"));
	contactMechId = (String) request.getAttribute("contactMechId");
	if(UtilValidate.isNotEmpty(contactMechId)){
		shipping = delegator.find("PostalAddress", ["contactMechId" : contactMechId], false);
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
	context.party = info;
}
