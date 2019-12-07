package com.olbius.jackrabbit.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.security.Privilege;

import org.apache.jackrabbit.core.security.authorization.PrivilegeRegistry;
import org.ofbiz.base.container.ContainerException;

public abstract class OlbiusContainer {
	private static Set<String> adminPermission = null;

	private static Map<String, Set<String>> mapComponent = null;

	private static final Map<String, String[]> permission = initPermissionMap();

	public abstract void addComponent() throws ContainerException;

	private static String admin;
	
	private static String pwd;
	
	public static Map<String, Set<String>> getMapComponent() {
		if (mapComponent == null) {
			mapComponent = new HashMap<String, Set<String>>();
		}
		return mapComponent;
	}

	public static Set<String> getAdminPermission() {
		if (adminPermission == null) {
			adminPermission = new TreeSet<String>();
		}
		return adminPermission;
	}

	public static Map<String, String[]> getMapPermission() {
		return permission;
	}

	private static Map<String, String[]> initPermissionMap() {
		
		Map<String, String[]> map = new HashMap<String, String[]>();
//		map.put("ADMIN", new String[] { Privilege.JCR_WRITE, Privilege.JCR_READ, Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_READ_ACCESS_CONTROL,
//				Privilege.JCR_MODIFY_ACCESS_CONTROL, Privilege.JCR_VERSION_MANAGEMENT });
		map.put("ADMIN", new String[] {Privilege.JCR_READ, PrivilegeRegistry.REP_WRITE, Privilege.JCR_READ_ACCESS_CONTROL,
				Privilege.JCR_MODIFY_ACCESS_CONTROL, Privilege.JCR_VERSION_MANAGEMENT });
		map.put("VIEW", new String[] { Privilege.JCR_READ, Privilege.JCR_VERSION_MANAGEMENT });
		map.put("DELETE", new String[] { Privilege.JCR_REMOVE_NODE, Privilege.JCR_READ, Privilege.JCR_VERSION_MANAGEMENT });
		map.put("UPDATE", new String[] { Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_READ, Privilege.JCR_READ_ACCESS_CONTROL,
				Privilege.JCR_MODIFY_ACCESS_CONTROL, Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_VERSION_MANAGEMENT });
		map.put("CREATE", new String[] { Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_READ, Privilege.JCR_READ_ACCESS_CONTROL,
				Privilege.JCR_MODIFY_ACCESS_CONTROL, Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_VERSION_MANAGEMENT });
		return map;
	}
	
	protected static void setAdmin(String s) {
		admin = s;
	}
	
	protected static void setPwd(String s) {
		pwd = s;
	}
	
	protected static String getAdmin() {
		return admin;
	}
	
	protected static String getPwd() {
		return pwd;
	}
}
