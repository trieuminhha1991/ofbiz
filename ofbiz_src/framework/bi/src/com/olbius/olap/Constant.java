package com.olbius.olap;

public class Constant {
	private static String TENANT_DEFAULT;
	
	public static String getTenantDefault() {
		if(TENANT_DEFAULT == null) {
			TENANT_DEFAULT = "TENANT_DEFAULT";
		}
		return TENANT_DEFAULT;
	}
	public static void setTenantDefault(String tenantId) {
		TENANT_DEFAULT = tenantId;
	}
}
