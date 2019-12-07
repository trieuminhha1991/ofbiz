package com.olbius.jms.data.handle.insert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.data.PartyGroup;

public class PartyGroupData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {
		
		PartyGroup group = (PartyGroup) message;
		
		Map<String, Object> input = new HashMap<String, Object>();

		input.put("userLogin", userLogin);

		input.putAll(group.getMessageData());

		input.put("preferredCurrencyUomId", group.getCurrencyUom() != null ? group.getCurrencyUom().getBusId() : null);

		GenericValue value = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", group.getBusId()), false);

		if (value == null) {
			input.put("partyId", group.getBusId());
			dispatcher.runSync("createPartyGroup", input);
		} else {
			input.put("partyId", group.getBusId());
			dispatcher.runSync("updatePartyGroup", input);
		}
		
		/*if(group.getCurrencyUom() != null) {
			value = delegator.findOne("Party", UtilMisc.toMap("partyId", group.getBusId()), false);
			value.set("preferredCurrencyUomId", group.getCurrencyUom().getBusId());
			value.store();
		}*/
		
		if (group.getPostalAddress() != null) {

			List<GenericValue> list = delegator.findByAnd(
					"PartyContactMechPurpose", UtilMisc.toMap("partyId", group.getBusId(), "contactMechId",
							group.getPostalAddress().getBusId(), "contactMechPurposeTypeId", "BILLING_LOCATION"),
					null, false);

			if (list == null || list.isEmpty()) {

				Map<String, Object> tmp = new HashMap<String, Object>();

				tmp.put("userLogin", userLogin);
				tmp.put("partyId", group.getBusId());
				tmp.put("contactMechId", group.getPostalAddress().getBusId());
				tmp.put("contactMechPurposeTypeId", "BILLING_LOCATION");
				dispatcher.runSync("createPartyContactMechPurpose", tmp);
			
			}

		}
		
	}

}
