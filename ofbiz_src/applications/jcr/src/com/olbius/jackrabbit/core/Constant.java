package com.olbius.jackrabbit.core;

import com.olbius.jackrabbit.api.Node;

public class Constant {
	private static String TENANT_DEFAULT;
	private static String URL_CLUSTER;
	
	public static String getTenantDefault() {
		if(TENANT_DEFAULT == null) {
			TENANT_DEFAULT = "TENANT_DEFAULT";
		}
		return TENANT_DEFAULT;
	}
	public static void setTenantDefault(String tenantId) {
		TENANT_DEFAULT = tenantId;
//		com.olbius.olap.Constant.setTenantDefault(tenantId);
	}
	public static String getUrl() {
		if(URL_CLUSTER == null) {
			URL_CLUSTER = Node.WEBDAV_URL;
		}
		return URL_CLUSTER;
	}
	public static void setUrl(String url) {
		URL_CLUSTER = url;
	}
	
}
