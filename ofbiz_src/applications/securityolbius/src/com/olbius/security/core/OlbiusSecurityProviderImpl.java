package com.olbius.security.core;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import com.olbius.security.api.Application;
import com.olbius.security.api.OlbiusSecurityProvider;
import com.olbius.security.core.application.OlbiusApplicationPermission;
import com.olbius.security.core.application.OlbiusPartyPermisision;
import com.olbius.security.core.cache.ApplicationCache;
import com.olbius.security.core.cache.ApplicationPermission;
import com.olbius.security.core.cache.PartyMember;
import com.olbius.security.core.cache.PartyMemberOf;
import com.olbius.security.core.cache.PartyMemberType;
import com.olbius.security.core.cache.PartyPermission;
import com.olbius.security.core.cache.PermissionCache;
import com.olbius.security.core.party.OlbiusParty;

public class OlbiusSecurityProviderImpl implements OlbiusSecurityProvider {

	public static final String symbol = ";";
	
	private static ApplicationCache olbiusApp = new ApplicationCache();

	private static PermissionCache olbiusPermission = new PermissionCache();
	
	private static ApplicationPermission olbiusAppPerm = new ApplicationPermission(olbiusApp, olbiusPermission);
	
	private static PartyPermission olbiusPartyPerm = new PartyPermission(olbiusApp, olbiusPermission);
	
	private static PartyMemberType olbiusPartyMemberType = new PartyMemberType();
	
	private static PartyMember olbiusPartyMember = new PartyMember(olbiusPartyMemberType);
	
	private static PartyMemberOf olbiusPartyMemberOf = new PartyMemberOf(olbiusPartyMemberType);
	
	private Delegator delegator;

	public OlbiusSecurityProviderImpl(Delegator delegator) {
		this.delegator = delegator;
	}

	@Override
	public Application getAppliction(String type, String app) {
		return olbiusApp.get(delegator, type + symbol + app);
	}

	@Override
	public boolean hasPermission(String party, String permission, String appType, String app) {

		if (permission == null || permission.isEmpty()) {
			permission = olbiusApp.get(delegator, appType + symbol + app).getDefaultPermission();
		}

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {
			return hasPermission(party, permission, appType, app, timestamp);
		} catch (Exception e) {
			Debug.logError(e, OlbiusSecurityProviderImpl.class.getName());
			return false;
		}

	}

	private boolean hasPermission(String party, String permission, String appType, String app, Timestamp timestamp) {
		List<OlbiusApplicationPermission> appPermissions = olbiusAppPerm.get(delegator, permission + symbol + appType + symbol + app);
		for (OlbiusApplicationPermission appPermission : appPermissions) {

			List<OlbiusPartyPermisision> partyPermisisions = olbiusPartyPerm.get(delegator,
					appPermission.getApplication().getType() + symbol + appPermission.getApplication().getApp());

			for (OlbiusPartyPermisision partyPermisision : partyPermisisions) {

				if (isMember(party, partyPermisision.getPartyId(), timestamp)
						&& partyPermisision.getPermission().isInclude(appPermission.getPermission())
						&& partyPermisision.getPermission().isTime(timestamp)) {
					return partyPermisision.getPermission().isAllow();
				}

			}

		}
		return false;
	}

	@Override
	public boolean isMember(String partyIdMember, String partyId, Timestamp timestamp) {

		if (partyIdMember.equals(partyId)) {
			return true;
		}

		Map<String, List<OlbiusParty>> map = olbiusPartyMemberOf.get(delegator, partyIdMember);
		if (map.get(partyId) != null) {
			for (OlbiusParty party : map.get(partyId)) {
				if (party.isMember(timestamp) && party.getPartyId().equals(partyId)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isMember(String partyIdMember, String partyId) {
		return isMember(partyIdMember, partyId, new Timestamp(System.currentTimeMillis()));
	}

	@Override
	public Set<String> member(String partyId, Timestamp timestamp) {
		Set<String> s = new TreeSet<String>();
		Map<String, List<OlbiusParty>> map = olbiusPartyMember.get(delegator, partyId);
		for (String key : map.keySet()) {
			for (OlbiusParty party : map.get(key)) {
				if (party.isMember(timestamp)) {
					s.add(key);
					break;
				}
			}
		}
		return s;
	}

	@Override
	public Set<String> memberOf(String partyId, Timestamp timestamp) {
		Set<String> s = new TreeSet<String>();
		Map<String, List<OlbiusParty>> map = olbiusPartyMemberOf.get(delegator, partyId);
		for (String key : map.keySet()) {
			for (OlbiusParty party : map.get(key)) {
				if (party.isMember(timestamp)) {
					s.add(key);
					break;
				}
			}
		}
		return s;
	}

	@Override
	public synchronized void clearCache(String entity) {
		switch (entity) {
			case "OlbiusApplication":
				olbiusAppPerm.clean(delegator);
				olbiusApp.clean(delegator);
				break;
			case "PartyRelationship":
				olbiusPartyMember.clean(delegator);
				olbiusPartyMemberOf.clean(delegator);
				break;
			case "OlbiusPartyRelationshipType":
				olbiusPartyMember.clean(delegator);
				olbiusPartyMemberOf.clean(delegator);
				olbiusPartyMemberType.clean(delegator);
				break;
			case "OlbiusPartyPermission":
				olbiusPartyPerm.clean(delegator);
				break;
			case "OlbiusPermission":
				olbiusPermission.clean(delegator);
				olbiusPartyPerm.clean(delegator);
				break;
			case "OlbiusOverridePermission":
				olbiusAppPerm.clean(delegator);
				olbiusApp.clean(delegator);
				break;
			default:
				break;
		}
	}

	@Override
	public synchronized void clearCache() {
		olbiusApp.clean(delegator);
		olbiusAppPerm.clean(delegator);
		olbiusPartyMember.clean(delegator);
		olbiusPartyMemberOf.clean(delegator);
		olbiusPartyMemberType.clean(delegator);
		olbiusPartyPerm.clean(delegator);
		olbiusPermission.clean(delegator);
	}
}
