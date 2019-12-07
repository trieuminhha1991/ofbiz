package com.olbius.security.core.cache;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.entity.cache.OlbiusCache;
import com.olbius.security.api.Application;
import com.olbius.security.core.OlbiusSecurityProviderImpl;
import com.olbius.security.core.application.OlbiusApplicationPermission;

public class ApplicationPermission extends OlbiusCache<List<OlbiusApplicationPermission>> {

	private ApplicationCache appCache;
	private PermissionCache permCache;

	public ApplicationPermission(ApplicationCache applicationCache, PermissionCache permCache) {
		this.appCache = applicationCache;
		this.permCache = permCache;
	}

	@Override
	public List<OlbiusApplicationPermission> loadCache(Delegator delegator, String key) throws Exception {
		String[] tmp = key.split(OlbiusSecurityProviderImpl.symbol);
		String permission = tmp[0];
		String appType = tmp[1];
		String app = tmp[2];

		List<OlbiusApplicationPermission> list = new ArrayList<OlbiusApplicationPermission>();

		Application application = appCache.get(delegator, appType + OlbiusSecurityProviderImpl.symbol + app);

		if (!application.isEmpty()) {
			list.add(new OlbiusApplicationPermission(application, permission));
			if (application.getModule() != null) {
				if (application.getOverridePermission(permission) != null) {
					list.addAll(this.get(delegator, application.getOverridePermission(permission) + OlbiusSecurityProviderImpl.symbol
							+ application.getModule().getType() + OlbiusSecurityProviderImpl.symbol + application.getModule().getApp()));
				} else {
					for (String s : application.getOverridePermission().keySet()) {
						if (permCache.get(delegator, s).contains(permission)) {
							list.addAll(this.get(delegator, s + OlbiusSecurityProviderImpl.symbol + appType + OlbiusSecurityProviderImpl.symbol + app));
						}
					}
				}
			}
		}

		return list;
	}
}
