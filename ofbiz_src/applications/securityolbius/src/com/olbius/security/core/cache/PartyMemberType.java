package com.olbius.security.core.cache;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.entity.cache.OlbiusCache;

public class PartyMemberType extends OlbiusCache<String>{

	@Override
	public String loadCache(Delegator delegator, String key) throws Exception {
		GenericValue value = delegator.findOne("OlbiusPartyRelationshipType",
				UtilMisc.toMap("partyRelationshipTypeId", key), false);
		if (value != null) {
			return value.getString("member");
		}
		return "";
	}

}
