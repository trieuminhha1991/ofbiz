package com.olbius.basehr.util;

import org.ofbiz.entity.Delegator;

public interface OrganizationConfig {
	public String getLastOrganization(Delegator delegator, String userLoginId);
	public String getCurrentOrganization(Delegator delegator, String userLoginId);
}
