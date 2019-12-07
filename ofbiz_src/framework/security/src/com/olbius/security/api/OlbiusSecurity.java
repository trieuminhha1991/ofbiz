package com.olbius.security.api;

import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericValue;

public interface OlbiusSecurity {

	OlbiusSecurityProvider getProvider();
	
	boolean olbiusHasPermission(String partyId, String permission, String appType, String app);
	
	boolean olbiusHasPermission(GenericValue userLogin, String permission, String appType, String app);
	
	boolean olbiusHasPermission(HttpSession session, String permission, String appType, String app);

	boolean olbiusEntityPermission(HttpSession session, String permission, String entity);
}
