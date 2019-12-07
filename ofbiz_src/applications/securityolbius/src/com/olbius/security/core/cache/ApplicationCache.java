package com.olbius.security.core.cache;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.entity.cache.OlbiusCache;
import com.olbius.security.api.Application;
import com.olbius.security.core.OlbiusSecurityProviderImpl;
import com.olbius.security.core.application.OlbiusApplictionFactory;

public class ApplicationCache extends OlbiusCache<Application> {

	@Override
	public Application loadCache(Delegator delegator, String key) throws Exception {
		GenericValue value = null;

		Application application = null;

		if (key.indexOf(OlbiusSecurityProviderImpl.symbol) == -1) {
			value = delegator.findOne("OlbiusApplication", UtilMisc.toMap("applicationId", key), false);
		} else {
			String tmp[] = key.split(OlbiusSecurityProviderImpl.symbol);
			String type = tmp[0];
			String app = tmp[1];
			value = EntityUtil.getFirst(delegator.findByAnd("OlbiusApplication",
					UtilMisc.toMap("applicationType", type, "application", app), null, false));
		}

		if (value != null) {

			application = OlbiusApplictionFactory.newInstance(value.getString("applicationType"));

			application.setId(value.getString("applicationId"));
			application.setApp(value.getString("application"));
			application.setName(value.getString("name"));
			application.setDefaultPermission(value.getString("permissionId"));

			if (value.getString("moduleId") != null && !value.getString("moduleId").isEmpty()) {

				value = delegator.findOne("OlbiusApplication",
						UtilMisc.toMap("applicationId", value.getString("moduleId")), false);

				if (value != null) {
					Application tmpApp = this.get(delegator,
							value.getString("applicationType") + OlbiusSecurityProviderImpl.symbol + value.getString("application"));

					if (tmpApp.getId() != null && !tmpApp.getId().isEmpty()) {
						application.setModule(tmpApp);
					}
				}

			}

			List<GenericValue> values = delegator.findByAnd("OlbiusOverridePermission",
					UtilMisc.toMap("applicationId", application.getId()), null, false);

			for (GenericValue val : values) {
				application.putOverridePermission(val.getString("permissionId"), val.getString("overridePermissionId"));
			}

		} else {
			application = OlbiusApplictionFactory.newInstance(null);
		}

		return application;
	}

}
