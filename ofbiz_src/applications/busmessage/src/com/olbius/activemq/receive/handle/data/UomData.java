package com.olbius.activemq.receive.handle.data;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.receive.handle.OlbiusReceiveData;
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

		String uomId = uom.getOwnId();

		GenericValue value = null;

		if (uomId == null) {
			value = EntityUtil.getFirst(delegator.findByAnd("Uom",
					UtilMisc.toMap("abbreviation", uom.getMessageData().get("abbreviation")), null, false));
			
			if (value == null) {
				try {
					uom.setOwnId(OlbiusReceiveData.BUS_CODE + OlbiusReceiveData.CACHE.get(delegator, uom.getBusId() + "#Uom"));
				} catch (Exception e) {
					throw new GenericServiceException(e);
				}
				value = delegator.findOne("Uom", UtilMisc.toMap("uomId", uom.getOwnId()), false);
			} else {
				uom.setOwnId(value.getString("uomId"));
				list.add(value);
			}
		
		} else {
			value = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
		}


		if (value == null) {

			GenericValue uomType = delegator.findOne("UomType", UtilMisc.toMap("uomTypeId", uom.getType()), false);

			if (uomType == null) {
				uomType = delegator.makeValue("UomType");
				uomType.set("uomTypeId", uom.getType());
				list.add(uomType);
			}

			GenericValue uomValue = delegator.makeValue("Uom");
			uomValue.set("uomId", uom.getOwnId());
			uomValue.set("uomTypeId", uom.getType());
			uomValue.set("abbreviation", uom.getMessageData().get("abbreviation"));
			uomValue.set("description", uom.getMessageData().get("description"));
			list.add(uomValue);

		}

		delegator.storeAll(list);

		if (!list.isEmpty()) {
			callback.run(uom, dispatcher, delegator, userLogin);
		}

	}

}
