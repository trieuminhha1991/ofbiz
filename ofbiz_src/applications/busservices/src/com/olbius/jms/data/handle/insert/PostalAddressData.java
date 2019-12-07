package com.olbius.jms.data.handle.insert;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.PostalAddress;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;

public class PostalAddressData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {

		PostalAddress address = (PostalAddress) message;

		String contactMechId = address.getBusId();

		Map<String, Object> input = new HashMap<String, Object>();

		input.put("userLogin", userLogin);

		input.putAll(address.getMessageData());

		input.put("countryGeoId", address.getCountry() != null ? address.getCountry().getBusId() : null);
		input.put("stateProvinceGeoId", address.getState() != null ? address.getState().getBusId() : null);
		input.put("districtGeoId", address.getDistrict() != null ? address.getDistrict().getBusId() : null);

		if(input.get("city") == null) {
			input.put("city", "city");
		}
		
		GenericValue contactMech = null;

		if (contactMechId != null) {
			contactMech = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		}

		if (contactMech == null) {

			input.put("contactMechId", contactMechId);
			dispatcher.runSync("createPostalAddress", input);

		} else if (!"Y".equals(address.getOwnParty()) && address.isUpdate()){
			GenericValue value = delegator.makeValue("PostalAddress");
			input.put("contactMechId", contactMechId);
			for (String s : input.keySet()) {
				if ("userLogin".equals(s)) {
					continue;
				}
				value.set(s, input.get(s));
			}
			value.store();
		}
		
	}

}
