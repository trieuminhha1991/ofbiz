package com.olbius.activemq.receive.handle.data;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.receive.handle.OlbiusReceiveData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.PostalAddress;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;

public class PostalAddressData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {

		PostalAddress address = (PostalAddress) message;

		String contactMechId = address.getOwnId();

		Map<String, Object> input = new HashMap<String, Object>();

		input.put("userLogin", userLogin);

		input.putAll(address.getMessageData());

		input.put("countryGeoId", address.getCountry() != null ? address.getCountry().getOwnId() : null);
		input.put("stateProvinceGeoId", address.getState() != null ? address.getState().getOwnId() : null);
		input.put("districtGeoId", address.getDistrict() != null ? address.getDistrict().getOwnId() : null);

		if(input.get("city") == null) {
			input.put("city", "city");
		}
		
		GenericValue contactMech = null;

		if (contactMechId != null) {
			contactMech = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		}

		if (contactMech == null) {
			try {
				contactMechId = OlbiusReceiveData.BUS_CODE
						+ OlbiusReceiveData.CACHE.get(delegator, address.getBusId() + "#PostalAddress");
			} catch (Exception e) {
				throw new GenericServiceException(e);
			}
			contactMech = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		}

		if (contactMech == null) {
			input.put("contactMechId", contactMechId);
			dispatcher.runSync("createPostalAddress", input);
			address.setOwnId(contactMechId);
			callback.run(address, dispatcher, delegator, userLogin);

		} else if (!"Y".equals(address.getOwnParty()) && address.isUpdate()) {
			GenericValue value = delegator.makeValue("PostalAddress");
			input.put("contactMechId", contactMechId);
			for (String s : input.keySet()) {
				if ("userLogin".equals(s)) {
					continue;
				}
				value.set(s, input.get(s));
			}
			value.store();
			address.setOwnId(contactMechId);
		} else {
			address.setOwnId(contactMechId);
		}
	}

}
