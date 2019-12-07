package com.olbius.jms.event.handle;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * @author Nguyen Ha
 *
 */
public class BusEvent {

	public static Timestamp getTimeStamp(Delegator delegator, String partyId, String event) throws GenericEntityException {
		
		List<GenericValue> list = delegator.findByAnd("BusEvent", UtilMisc.toMap("partyId", partyId, "event", event), null, false);

		if (!list.isEmpty()) {
			return list.get(0).getTimestamp("update");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(1990, 0, 1, 0, 0, 0);
		
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public static void udpateTimeStamp(Delegator delegator, String partyId, String event) throws GenericEntityException {
		
		GenericValue value = delegator.findOne("BusEvent", UtilMisc.toMap("partyId", partyId, "event", event), false);

		if (value != null) {
			value.set("update", new Timestamp(System.currentTimeMillis()));
			value.store();
		} else {
			value = delegator.makeValue("BusEvent");
			value.set("partyId", partyId);
			value.set("event", event);
			value.set("update", new Timestamp(System.currentTimeMillis()));
			value.create();
		}

	}
	
}
