package com.olbius.jackrabbit.client.api;

import java.security.Principal;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import com.olbius.jcr.security.principal.OlbiusPrincipal;

public interface ClientNodeSecurity {

	void removeEntry(Node node, OlbiusPrincipal principal, String[] privileges) throws RepositoryException;

	void removeEntry(Node node, OlbiusPrincipal principal, String privilege) throws RepositoryException;

	void addEntry(Node node, OlbiusPrincipal principal, String[] privileges, boolean isAllow) throws RepositoryException;

	AccessControlEntry[] getEntry(Node node, Principal principal) throws RepositoryException;

	AccessControlEntry[] getEntry(Node node) throws RepositoryException;

	AccessControlEntry[] getEntry(Principal principal) throws Exception;

	AccessControlEntry[] getEntry() throws Exception;

	AccessControlManager getAccessControlManager();
	
	ClientNodePermission getNodePermission();

	void removeEntry(Node node, OlbiusPrincipal principal) throws RepositoryException;

	Privilege[] getPrivileges(Node node, Principal principal) throws RepositoryException;

	String[] getPermissionName(Node node, Principal principal) throws AccessControlException, RepositoryException;

	boolean hasPrivilege(Node node, Principal principal, String privilege) throws RepositoryException;
}
