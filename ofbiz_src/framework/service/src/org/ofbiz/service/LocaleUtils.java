package org.ofbiz.service;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;

public class LocaleUtils {
	public static Locale getLocale(Map<String, ? extends Object> context) {
		Locale locale = null;
		String lastLocale = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(userLogin != null) {
			lastLocale = userLogin.getString("lastLocale");
		}
		if(lastLocale != null) {
			locale = UtilMisc.parseLocale(lastLocale);
		}else {
			lastLocale = UtilProperties.getPropertyValue("start.properties", "ofbiz.locale.default", "vi");
			locale = UtilMisc.parseLocale(lastLocale);
		}
		return locale;
	}
}
