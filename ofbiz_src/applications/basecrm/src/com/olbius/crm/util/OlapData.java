package com.olbius.crm.util;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.cache.dimension.PartyDimension;

public class OlapData {

	public static Object getParty(Delegator delegator, String partyId) {
		if (partyId != null) {
			return PartyDimension.D.getId(delegator, partyId);
		}
		return null;
	}
}
