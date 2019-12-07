package com.olbius.util;

import java.util.Locale;

import org.ofbiz.base.util.UtilProperties;

public class PropertiesUtil {
	public static final String RESOURCE_NOTI = "NotificationUiLabels";
	public static final int retiredAge = 60;
	public static final String PERMANENT_RESIDENCE = "PERMANENT_RESIDENCE";
	public static final String CURRENT_RESIDENCE = "CURRENT_RESIDENCE";
	public static final String PERSON_TYPE = "PERSON";
	public static final String GROUP_TYPE = "PARTY_GROUP";
	public static String getProperty(String key, Locale locale){
		return UtilProperties.getMessage(RESOURCE_NOTI, key, locale);
	}
}
