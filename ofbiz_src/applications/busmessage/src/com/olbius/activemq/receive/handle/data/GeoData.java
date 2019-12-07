package com.olbius.activemq.receive.handle.data;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.receive.handle.OlbiusReceiveData;
import com.olbius.jms.data.Geo;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;

public class GeoData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws Exception {

		Geo geo = (Geo) message;

		String geoId = geo.getOwnId();

		if (geo.getMessageData().get("geoTypeId") != null) {

			GenericValue geoType = delegator.findOne("GeoType",
					UtilMisc.toMap("geoTypeId", geo.getMessageData().get("geoTypeId")), false);

			if (geoType == null) {
				geoType = delegator.makeValue("GeoType");
				geoType.set("geoTypeId", geo.getMessageData().get("geoTypeId"));
				geoType.create();
			}

		}

		Map<String, Object> input = new HashMap<String, Object>();

		input.put("userLogin", userLogin);

		input.putAll(geo.getMessageData());

		GenericValue geoVal = null;

		if (geoId == null) {
			geoVal = EntityUtil.getFirst(delegator.findByAnd("Geo",
					UtilMisc.toMap("abbreviation", geo.getMessageData().get("abbreviation")), null, false));
		} else {
			geoVal = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
		}

		if (geoVal == null) {
			try {
				geoId = OlbiusReceiveData.BUS_CODE + OlbiusReceiveData.CACHE.get(delegator, geo.getBusId() + "#Geo");
			} catch (Exception e) {
				throw new GenericServiceException(e);
			}
			geoVal = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
		}

		if (geoVal == null) {
			input.put("geoId", geoId);
			dispatcher.runSync("createGeo", input);
			geo.setOwnId(geoId);
		} else if (!"Y".equals(geo.getOwnParty()) && geo.isUpdate()) {
			input.put("geoId", geoVal.get("geoId"));
			dispatcher.runSync("updateGeo", input);
			geo.setOwnId((String) geoVal.get("geoId"));
		} else {
			geo.setOwnId((String) geoVal.get("geoId"));
		}
		
		callback.run(geo, dispatcher, delegator, userLogin);
	}

}
