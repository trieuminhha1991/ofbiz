package com.olbius.jackrabbit.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;

import com.olbius.jackrabbit.client.api.ClientNodePermission;
import com.olbius.jackrabbit.client.api.ClientNodeSecurity;

public class OlbiusNodePermission implements ClientNodePermission {

	public static final String[] ADMIN = new String[] { Privilege.JCR_ALL };
	public static final String[] VIEW = new String[] { Privilege.JCR_READ, Privilege.JCR_NODE_TYPE_MANAGEMENT };
	public static final String[] DELETE = new String[] { Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES, Privilege.JCR_READ,
			Privilege.JCR_NODE_TYPE_MANAGEMENT };
	public static final String[] CREATE = new String[] { Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_READ, Privilege.JCR_NODE_TYPE_MANAGEMENT };
	public static final String[] UPDATE = new String[] { Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_READ, Privilege.JCR_READ_ACCESS_CONTROL,
			Privilege.JCR_MODIFY_ACCESS_CONTROL, Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_VERSION_MANAGEMENT };

	@Override
	public String[] permission(String name) {

		switch (name) {
		case ClientNodePermission.ADMIN:
			return ADMIN;
		case ClientNodePermission.VIEW:
			return VIEW;
		case ClientNodePermission.DELETE:
			return DELETE;
		case ClientNodePermission.CREATE:
			return CREATE;
		case ClientNodePermission.UPDATE:
			return UPDATE;
		default:
			return null;
		}

	}

	@Override
	public String[] permission(ClientNodeSecurity security, Privilege[] privileges) throws AccessControlException, RepositoryException {

		Map<String, Privilege> map = new HashMap<String, Privilege>();

		for (Privilege privilege : privileges) {
			map.put(privilege.getName(), privilege);
		}

		ArrayList<String> permission = new ArrayList<String>();

		if (map.get(security.getAccessControlManager().privilegeFromName(Privilege.JCR_ALL).getName()) != null) {
			permission.add(ClientNodePermission.ADMIN);
		} else {
			String[] permissionName = new String[] { ClientNodePermission.VIEW, ClientNodePermission.CREATE, ClientNodePermission.DELETE,
					ClientNodePermission.UPDATE };

			for(String name : permissionName) {
				boolean flag = true;
				
				for(String p : permission(name)) {
					if (map.get(security.getAccessControlManager().privilegeFromName(p).getName()) == null) {
						flag = false;
						break;
					}
				}
				
				if(flag) {
					permission.add(name);
				}
			}
		}

		String[] tmp = new String[permission.size()];
		tmp = permission.toArray(tmp);
		return tmp;

	}

}
