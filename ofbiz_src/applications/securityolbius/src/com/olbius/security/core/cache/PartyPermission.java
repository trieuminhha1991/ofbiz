package com.olbius.security.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.security.api.Application;
import com.olbius.security.api.Permission;
import com.olbius.security.core.application.OlbiusPartyPermisision;
import com.olbius.security.core.application.OlbiusPermission;

public class PartyPermission extends OlbiusSecurityCache<List<OlbiusPartyPermisision>> {

	private ApplicationCache appCache;
	private PermissionCache permCache;

	public PartyPermission(ApplicationCache applicationCache, PermissionCache permCache) {
		this.appCache = applicationCache;
		this.permCache = permCache;
	}

	@Override
	public List<OlbiusPartyPermisision> loadCache(Delegator delegator, String key) throws Exception {
		Application application = appCache.get(delegator, key);
		List<OlbiusPartyPermisision> list = new ArrayList<>();

		if (!application.isEmpty()) {

			EntityCondition condition = null;

			List<EntityCondition> entityConditions = new ArrayList<EntityCondition>();

			entityConditions.add(thruDateCondition(new Timestamp(System.currentTimeMillis())));

			entityConditions
					.add(EntityCondition.makeCondition("applicationId", EntityOperator.EQUALS, application.getId()));

			condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

			List<GenericValue> values = delegator.findList("OlbiusPartyPermission", condition, null,
					UtilMisc.toList("-createdStamp"), null, false);

			for (GenericValue value : values) {

				String partyId = value.getString("partyId");

				OlbiusPartyPermisision partyAppPermisision = null;

				partyAppPermisision = new OlbiusPartyPermisision();

				partyAppPermisision.setApplication(application);

				partyAppPermisision.setPartyId(partyId);

				list.add(partyAppPermisision);

				Permission permission = new OlbiusPermission(value.getString("permissionId"),
						value.getTimestamp("fromDate"), value.getTimestamp("thruDate"),
						value.getBoolean("allow") != null ? value.getBoolean("allow").booleanValue() : true);
				permission.setPermissionInclude(permCache.get(delegator, value.getString("permissionId")));
				partyAppPermisision.setPermission(permission);
			}

		}
		return list;
	}

}
