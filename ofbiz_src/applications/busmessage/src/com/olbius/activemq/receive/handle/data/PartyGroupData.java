package com.olbius.activemq.receive.handle.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.receive.handle.OlbiusReceiveData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.data.PartyGroup;

public class PartyGroupData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {
		
		PartyGroup group = (PartyGroup) message;
		
		String partyId = group.getOwnId();

		Map<String, Object> input = new HashMap<String, Object>();

		input.putAll(group.getMessageData());

		input.put("userLogin", userLogin);
		input.put("preferredCurrencyUomId", group.getCurrencyUom() != null ? group.getCurrencyUom().getOwnId() : null);

		GenericValue party = null;

		if (partyId != null) {
			party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
		}

		if (party == null) {
			try {
				partyId = OlbiusReceiveData.BUS_CODE + OlbiusReceiveData.CACHE.get(delegator, group.getBusId() + "#PartyGroup");
			} catch (Exception e) {
				throw new GenericServiceException(e);
			}
			party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
		}

		if (party == null) {
			input.put("partyId", partyId);
			dispatcher.runSync("createPartyGroup", input);
			group.setOwnId(partyId);
			callback.run(group, dispatcher, delegator, userLogin);
		} else if (!"Y".equals(group.getOwnParty()) && group.isUpdate()) {
			input.put("partyId", partyId);
			dispatcher.runSync("updatePartyGroup", input);
			group.setOwnId(partyId);
		} else {
			group.setOwnId(partyId);
		}

		/*if(group.getCurrencyUom() != null) {
			GenericValue value = delegator.findOne("Party", UtilMisc.toMap("partyId", group.getOwnId()), false);
			value.set("preferredCurrencyUomId", group.getCurrencyUom().getOwnId());
			value.store();
		}*/
		
		if (group.getPostalAddress() != null) {

			List<GenericValue> list = delegator.findByAnd(
					"PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId",
							group.getPostalAddress().getOwnId(), "contactMechPurposeTypeId", "BILLING_LOCATION"),
					null, false);

			if (list == null || list.isEmpty()) {

				Map<String, Object> tmp = new HashMap<String, Object>();

				tmp.put("userLogin", userLogin);
				tmp.put("partyId", partyId);
				tmp.put("contactMechId", group.getPostalAddress().getOwnId());
				tmp.put("contactMechPurposeTypeId", "BILLING_LOCATION");
				dispatcher.runSync("createPartyContactMechPurpose", tmp);
			}

		}
	}

}
