package com.olbius.security.api;

import java.sql.Timestamp;
import java.util.Set;

public interface OlbiusSecurityProvider {

	Application getAppliction(String type, String app);

	boolean hasPermission(String party, String permission, String appType, String app);

	boolean isMember(String partyIdMember, String partyId, Timestamp timestamp);

	boolean isMember(String partyIdMember, String partyId);

	Set<String> member(String partyId, Timestamp timestamp);

	Set<String> memberOf(String partyId, Timestamp timestamp);

	void clearCache();

	void clearCache(String entity);
	
}
