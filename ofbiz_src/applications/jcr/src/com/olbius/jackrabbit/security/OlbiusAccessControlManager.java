package com.olbius.jackrabbit.security;

import java.security.Principal;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.security.user.Authorizable;

public interface OlbiusAccessControlManager {
	public void addEntry(Node node, AccessControlEntry entry, boolean isAllow) throws AccessControlException, RepositoryException;

	public void addEntry(Node node, Authorizable auth, Privilege[] privileges, boolean isAllow) throws AccessDeniedException,
			UnsupportedRepositoryOperationException, PathNotFoundException, AccessControlException, LockException, VersionException,
			RepositoryException;

	public void addEntry(Node node, Principal principal, Privilege[] privileges, boolean isAllow) throws AccessDeniedException,
			PathNotFoundException, AccessControlException, LockException, VersionException, RepositoryException;

	public AccessControlManager getAccessControlManager();
	
	public AccessControlEntry[] getEntry(Node node) throws RepositoryException;
	
	public AccessControlEntry[] getEntry(Node node, Principal principal) throws RepositoryException;
	
	public Session getSession();
	
	public Privilege privilege(String s) throws AccessControlException, RepositoryException;
	
	public Privilege[] privilege(String[] s) throws AccessControlException, RepositoryException;
	
	public void removeEntry(Node node) throws RepositoryException;
	
	public void removeEntry(Node node, AccessControlEntry entry) throws UnsupportedRepositoryOperationException, RepositoryException;
	
	public void removeEntry(Node node, AccessControlEntry[] entries) throws UnsupportedRepositoryOperationException, RepositoryException;
	
	public void removeEntry(Node node, Principal principal) throws UnsupportedRepositoryOperationException, RepositoryException;
	
	public void removePolicy(Node node) throws AccessDeniedException, PathNotFoundException, RepositoryException;
	
}
