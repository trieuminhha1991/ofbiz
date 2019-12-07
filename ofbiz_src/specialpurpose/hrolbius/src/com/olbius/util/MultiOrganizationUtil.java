package com.olbius.util;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtilProperties;

public class MultiOrganizationUtil {
	public static String getCurrentOrganization(Delegator delegator){
		String strCurrentOrganization = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		return strCurrentOrganization;
	}
}
