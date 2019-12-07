package com.olbius.administration.util;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import javolution.util.FastMap;

public class CrabEntity {
	public static Map<String, Object> fastMaking(Delegator delegator, String entityName, Map<String, ? extends Object> context) {
		Map<String, Object> fields = FastMap.newInstance();
		GenericValue lookedUpValue = delegator.makeValidValue(entityName, context);
		fields.putAll(lookedUpValue);
		fields.put("userLogin", context.get("userLogin"));
		return fields;
	}
	public static String getPartyName(Delegator delegator, Object partyId) throws GenericEntityException {
		GenericValue party = delegator.findOne("PersonAndPartyGroupSimple", UtilMisc.toMap("partyId", partyId), false);
		return party!=null?party.getString("partyName"):null;
	}
	public static String getPartyCode(Delegator delegator, Object partyId) throws GenericEntityException {
		GenericValue party = delegator.findOne("PersonAndPartyGroupSimple", UtilMisc.toMap("partyId", partyId), false);
		return party!=null?party.getString("partyCode"):null;
	}
	public static String noDuplicateId(Delegator delegator, String entityName, String field, String id) throws GenericEntityException {
		GenericValue dummy = delegator.findOne(entityName, UtilMisc.toMap(field, id), false);
		if (UtilValidate.isNotEmpty(dummy)) {
			return noDuplicateId(delegator, entityName, field, id + "P");
		}
		return id;
	}
}
