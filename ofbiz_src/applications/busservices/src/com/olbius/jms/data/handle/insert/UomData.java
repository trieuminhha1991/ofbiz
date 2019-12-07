package com.olbius.jms.data.handle.insert;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.data.Uom;

public class UomData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {

		Uom uom = (Uom) message;

		List<GenericValue> list = new ArrayList<GenericValue>();

		GenericValue value = delegator.findOne("Uom", UtilMisc.toMap("uomId", uom.getBusId()), false);

		if (value == null) {

			GenericValue uomType = delegator.findOne("UomType", UtilMisc.toMap("uomTypeId", uom.getType()), false);

			if (uomType == null) {
				uomType = delegator.makeValue("UomType");
				uomType.set("uomTypeId", uom.getType());
				list.add(uomType);
			}

			GenericValue uomValue = delegator.makeValue("Uom");
			uomValue.set("uomId", uom.getBusId());
			uomValue.set("uomTypeId", uom.getType());
			uomValue.set("abbreviation", uom.getMessageData().get("abbreviation"));
			uomValue.set("description", uom.getMessageData().get("description"));
			list.add(uomValue);

		}

		delegator.storeAll(list);

	}

}
