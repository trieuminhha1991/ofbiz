package com.olbius.security.util;

import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.api.OlbiusSecurityProvider;

public class SecurityUtil {

	private static OlbiusSecurity olbiusSecurity;

	public static OlbiusSecurity getOlbiusSecurity(Security security) {
		if (security instanceof OlbiusSecurity)
			return (OlbiusSecurity) security;
		else {
			if (olbiusSecurity == null) {
				olbiusSecurity = new OlbiusSecurity() {

					@Override
					public boolean olbiusHasPermission(HttpSession session, String permission, String appType,
							String app) {
						return false;
					}

					@Override
					public boolean olbiusHasPermission(GenericValue userLogin, String permission, String appType,
							String app) {
						return false;
					}

					@Override
					public boolean olbiusHasPermission(String partyId, String permission, String appType, String app) {
						return false;
					}

					@Override
					public boolean olbiusEntityPermission(HttpSession session, String permission, String entity) {
						return false;
					}

					@Override
					public OlbiusSecurityProvider getProvider() {
						return null;
					}
				};
			}
			return olbiusSecurity;
		}

	}

}
