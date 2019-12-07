package com.olbius.security.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.security.core.party.OlbiusParty;

public class PartyMemberOf extends OlbiusSecurityCache<Map<String, List<OlbiusParty>>> {
	
	private PartyMemberType memberType;

	public PartyMemberOf(PartyMemberType memberType) {
		this.memberType = memberType;
	}

	@Override
	public Map<String, List<OlbiusParty>> loadCache(Delegator delegator, String key) throws Exception {
		Map<String, List<OlbiusParty>> map = new HashMap<>();

		EntityCondition condition = null;

		List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, key));
		entityConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, key));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.OR);

		entityConditions = new ArrayList<EntityCondition>();

		entityConditions.add(condition);
		entityConditions.add(thruDateCondition(new Timestamp(System.currentTimeMillis())));

		condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

		List<GenericValue> values = delegator.findList("PartyRelationship", condition, null,
				UtilMisc.toList("fromDate"), null, false);

		for (GenericValue value : values) {
			if(value.getString("partyRelationshipTypeId") == null || value.getString("partyRelationshipTypeId").isEmpty()) {
				continue;
			}
			String type = memberType.get(delegator, value.getString("partyRelationshipTypeId"));
			if (!type.isEmpty()) {
				if (key.equals(value.getString(type))) {

					OlbiusParty party = new OlbiusParty(value.getString(type.equals("partyIdFrom") ? "partyIdTo" : "partyIdFrom"));
					party.setFromDate(value.getTimestamp("fromDate"));
					party.setThruDate(value.getTimestamp("thruDate"));

					if (map.get(party.getPartyId()) == null) {
						map.put(party.getPartyId(), new ArrayList<OlbiusParty>());
					}

					map.get(party.getPartyId()).add(party);

					Map<String, List<OlbiusParty>> mapTmp = this.get(delegator, party.getPartyId());

					for (String s : mapTmp.keySet()) {
						List<OlbiusParty> list = mapTmp.get(s);
						for (OlbiusParty p : list) {
							if (map.get(p.getPartyId()) == null) {
								map.put(p.getPartyId(), new ArrayList<OlbiusParty>());
							}
							p = p.mergeTime(party);
							map.get(p.getPartyId()).add(p);
						}
					}
				}
			}
		}

		return map;
	}

}
