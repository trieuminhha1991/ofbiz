package com.olbius.security.core;

import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.OFBizSecurity;

import com.olbius.security.api.Application;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.api.OlbiusSecurityProvider;

public class OlbiusSecurityImpl extends OFBizSecurity implements OlbiusSecurity {

	private OlbiusSecurityProvider provider;

	@Override
	public OlbiusSecurityProvider getProvider() {
		if (provider == null) {
			provider = new OlbiusSecurityProviderImpl(delegator);
		}
		return provider;
	}

	@Override
	public boolean olbiusHasPermission(String partyId, String permission, String appType, String app) {
		return getProvider().hasPermission(partyId, permission, appType, app);
	}

	@Override
	public boolean olbiusHasPermission(GenericValue userLogin, String permission, String appType, String app) {
		if (userLogin == null) {
			return false;
		}
		if("Y".equals(userLogin.getString("isSystem")) && "N".equals(userLogin.getString("enabled"))) {
			return true;
		}
		String partyId = userLogin.getString("partyId");
		if (partyId == null) {
			return false;
		}
		return olbiusHasPermission(partyId, permission, appType, app);
	}

	@Override
	public boolean olbiusHasPermission(HttpSession session, String permission, String appType, String app) {
		if (session == null) {
			return false;
		}
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (userLogin == null) {
			return false;
		}
		return olbiusHasPermission(userLogin, permission, appType, app);
	}
	
	@Override
	public boolean olbiusEntityPermission(HttpSession session, String permission, String entity) {
		return olbiusHasPermission(session, permission, Application.ENTITY, entity);
	}

}
