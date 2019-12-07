package com.olbius.jackrabbit.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;

public class OlbiusAccessControlManagerImpl implements OlbiusAccessControlManager {

	private AccessControlList acl;
	private AccessControlManager acm;
	private Node node;
	private Session session;
	private boolean flag;
	
	public OlbiusAccessControlManagerImpl(Session session) throws UnsupportedRepositoryOperationException, RepositoryException {
		this.session = session;
		acm = session.getAccessControlManager();
	}

	@Override
	public void addEntry(Node node, AccessControlEntry entry, boolean isAllow) throws AccessControlException, RepositoryException {
		addEntry(node, entry.getPrincipal(), entry.getPrivileges(), isAllow);
	}

	@Override
	public void addEntry(Node node, Authorizable auth, Privilege[] privileges, boolean isAllow) throws AccessDeniedException,
			UnsupportedRepositoryOperationException, PathNotFoundException, AccessControlException, LockException, VersionException,
			RepositoryException {
		addEntry(node, auth.getPrincipal(), privileges, isAllow);
	}

	@Override
	public void addEntry(Node node, Principal principal, Privilege[] privileges, boolean isAllow) throws AccessDeniedException,
			PathNotFoundException, AccessControlException, LockException, VersionException, RepositoryException {
		setAccessControl(node);
		addEntry(principal, privileges, isAllow);
		setPolicy();
	}

	private void addEntry(Principal principal, Privilege[] privileges, boolean isAllow) throws AccessControlException, RepositoryException {
		checkout();
		JackrabbitAccessControlList list = (JackrabbitAccessControlList) acl;
		list.addEntry(principal, privileges, isAllow);
	}

	@Override
	public AccessControlManager getAccessControlManager() {
		return acm;
	}

	@Override
	public AccessControlEntry[] getEntry(Node node) throws RepositoryException {
		setAccessControl(node);
		return acl.getAccessControlEntries();
	}

	@Override
	public AccessControlEntry[] getEntry(Node node, Principal principal) throws RepositoryException {
		List<AccessControlEntry> list = new ArrayList<AccessControlEntry>();
		AccessControlEntry[] entries = getEntry(node);
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
	public Session getSession() {
		return session;
	}

	@Override
	public Privilege privilege(String s) throws AccessControlException, RepositoryException {
		return acm.privilegeFromName(s);
	}

	@Override
	public Privilege[] privilege(String[] s) throws AccessControlException, RepositoryException {
		Privilege[] privileges = new Privilege[s.length];
		for (int i = 0; i < s.length; i++) {
			privileges[i] = privilege(s[i]);
		}
		return privileges;
	}

	@Override
	public void removeEntry(Node node) throws RepositoryException {
		AccessControlEntry[] entries = getEntry(node);
		removeEntry(node, entries);
	}

	@Override
	public void removeEntry(Node node, AccessControlEntry entry) throws UnsupportedRepositoryOperationException, RepositoryException {
		setAccessControl(node);
		checkout();
		acl.removeAccessControlEntry(entry);
		setPolicy();
	}

	@Override
	public void removeEntry(Node node, AccessControlEntry[] entries) throws UnsupportedRepositoryOperationException, RepositoryException {
		setAccessControl(node);
		checkout();
		for (AccessControlEntry entry : entries) {
			acl.removeAccessControlEntry(entry);
		}
		setPolicy();
	}

	@Override
	public void removeEntry(Node node, Principal principal) throws UnsupportedRepositoryOperationException, RepositoryException {
		setAccessControl(node);
		AccessControlEntry[] entries = getEntry(node, principal);
		removeEntry(node, entries);
	}

	private void removePolicy() throws AccessDeniedException, PathNotFoundException, AccessControlException, LockException, VersionException,
			RepositoryException {
		acm.removePolicy(node.getPath(), acl);
		session.save();
	}

	@Override
	public void removePolicy(Node node) throws AccessDeniedException, PathNotFoundException, RepositoryException {
		setAccessControl(node);
		removePolicy();
	}

	private void setAccessControl(Node node) throws AccessDeniedException, PathNotFoundException, RepositoryException {
		if (this.node != null && this.node.getPath().equals(node.getPath())) {
			return;
		}
		this.node = node;
		try {
			acl = (AccessControlList) acm.getPolicies(node.getPath())[0];
		} catch (Exception e) {
			acl = (AccessControlList) acm.getApplicablePolicies(node.getPath()).nextAccessControlPolicy();
		}
	}

	private void checkout() throws UnsupportedRepositoryOperationException, RepositoryException {
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();

		if (!versionManager.isCheckedOut(node.getPath())) {
			versionManager.checkout(node.getPath());
			session.save();
			flag = true;
		}
	}
	
	private void setPolicy() throws AccessDeniedException, PathNotFoundException, AccessControlException, LockException, VersionException,
			RepositoryException {
		acm.setPolicy(node.getPath(), acl);
		session.save();
		if(flag) {
			Workspace workspace = session.getWorkspace();
			VersionManager versionManager = workspace.getVersionManager();
			versionManager.checkin(node.getPath());
			session.save();
			flag = false;
		}
	}
	
	public String toString(Node node) throws PathNotFoundException, RepositoryException {
		AccessControlEntry[] entries = getEntry(node);
		return OlbiusEntry.toString(entries);
	}
}
