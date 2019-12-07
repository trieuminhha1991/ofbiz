package com.olbius.basehr.util;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtilProperties;

public class MultiOrganizationUtil {
	
	//@Deprecated
	public static String getCurrentOrganization(Delegator delegator){
		String strCurrentOrganization = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		return strCurrentOrganization;
	}
	
	public static String getCurrentOrganization(Delegator delegator, String userLoginId){
		OrganizationConfig config = new MultiOrganization();
		return config.getCurrentOrganization(delegator, userLoginId);
	}
	
	public static String getLastOrganization(Delegator delegator, String userLoginId){
		OrganizationConfig config = new MultiOrganization();
		return config.getLastOrganization(delegator, userLoginId);
	}
}