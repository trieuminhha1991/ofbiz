package com.olbius.jackrabbit.client.core;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.rmi.client.security.ClientAccessControlManager;

import com.olbius.jackrabbit.client.api.ClientNodePermission;
import com.olbius.jackrabbit.client.api.ClientNodeSecurity;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jcr.rmi.client.security.OlbiusClientAccessControlManager;
import com.olbius.jcr.security.principal.OlbiusPrincipal;

public class OlbiusNodeSecurity implements ClientNodeSecurity {

	private ClientSession session;
	private AccessControlManager acm;
	private ClientNodePermission permission;

	public OlbiusNodeSecurity(ClientSession session) throws Exception {
		this.session = session;
		this.acm = session.getJcrSession("rmi").getAccessControlManager();
		this.permission = new OlbiusNodePermission();
	}

	@Override
	public AccessControlManager getAccessControlManager() {
		return acm;
	}

	@Override
	public AccessControlEntry[] getEntry() throws Exception {
		return getEntry(session.getNode());
	}

	@Override
	public AccessControlEntry[] getEntry(Principal principal) throws Exception {
		return getEntry(session.getNode(), principal);
	}

	@Override
	public AccessControlEntry[] getEntry(Node node) throws RepositoryException {
		AccessControlList acl;

		try {
			acl = (AccessControlList) acm.getPolicies(node.getPath())[0];
		} catch (Exception e) {
			acl = (AccessControlList) acm.getApplicablePolicies(node.getPath()).nextAccessControlPolicy();
		}

		return acl.getAccessControlEntries();
	}

	@Override
	public AccessControlEntry[] getEntry(Node node, Principal principal) throws RepositoryException {
		List<AccessControlEntry> list = new ArrayList<AccessControlEntry>();
		AccessControlEntry[] entries = getEntry(node);
		if (principal == null) {
			return entries;
		}
		for (AccessControlEntry entry : entries) {
			if (entry.getPrincipal().getName().equals(principal.getName())) {
				list.add(entry);
			}
		}
		entries = new AccessControlEntry[list.size()];
		list.toArray(entries);
		return entries;
	}

	@Override
	public void addEntry(Node node, OlbiusPrincipal principal, String[] privileges, boolean isAllow) throws RepositoryException {
		if (acm instanceof ClientAccessControlManager) {
			((OlbiusClientAccessControlManager) acm).setPolicy(node.getPath(), principal, privileges, isAllow);
			session.save("rmi");
		}
	}

	@Override
	public void removeEntry(Node node, OlbiusPrincipal principal) throws RepositoryException {
		if (acm instanceof ClientAccessControlManager) {
			((OlbiusClientAccessControlManager) acm).removePolicy(node.getPath(), principal);
			session.save("rmi");
		}
	}

	@Override
	public void removeEntry(Node node, OlbiusPrincipal principal, String privilege) throws RepositoryException {
		if (acm instanceof ClientAccessControlManager) {
			((OlbiusClientAccessControlManager) acm).removePolicy(node.getPath(), principal, privilege);
			session.save("rmi");
		}
	}

	@Override
	public void removeEntry(Node node, OlbiusPrincipal principal, String[] privileges) throws RepositoryException {
		if (acm instanceof ClientAccessControlManager) {
			for (String privilege : privileges) {
				((OlbiusClientAccessControlManager) acm).removePolicy(node.getPath(), principal, privilege);
			}
			session.save("rmi");
		}
	}

	@Override
	public ClientNodePermission getNodePermission() {
		return permission;
	}

	@Override
	public Privilege[] getPrivileges(Node node, Principal principal) throws RepositoryException {
		ArrayList<Privilege> privileges = new ArrayList<Privilege>();
		AccessControlEntry[] entries = getEntry(node, principal);
		for (AccessControlEntry entry : entries) {
			if (entry instanceof JackrabbitAccessControlEntry) {
				if (!((JackrabbitAccessControlEntry) entry).isAllow()) {
					continue;
				}
			} 
			for (Privilege privilege : entry.getPrivileges()) {
				privileges.add(privilege);
			}
		}
		Privilege[] tmp = new Privilege[privileges.size()];
		tmp = privileges.toArray(tmp);
		return tmp;
	}

	@Override
	public boolean hasPrivilege(Node node, Principal principal, String privilege) throws RepositoryException {
		Privilege[] privileges = getPrivileges(node, principal);
		for (Privilege p : privileges) {
			if (p.getName().equals(acm.privilegeFromName(privilege).getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] getPermissionName(Node node, Principal principal) throws AccessControlException, RepositoryException {
		return permission.permission(this, getPrivileges(node, principal));
	}
}
