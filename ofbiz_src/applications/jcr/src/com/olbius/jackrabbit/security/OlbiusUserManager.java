package com.olbius.jackrabbit.security;

import java.security.Principal;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;

public interface OlbiusUserManager extends UserManager {
	public static final String GROUP = "#GROUP_";
	public static final String PARTY = "#PARTY_";
	public static final String PERMISSION = "#PERMISSION_";
	public static final String USER = "#USER_";
	public static final String RELATIONSHIP = "#RELATIONSHIP_";
	
	public void addAuthorizableToGroup(Authorizable auth, Group group) throws RepositoryException;

	public void addAuthorizableToGroup(Authorizable auth, String groupID) throws RepositoryException;

	public void addAuthorizableToGroup(Principal authID, Principal groupID) throws RepositoryException;

	public void addAuthorizableToGroup(Principal authID, String groupID) throws RepositoryException;

	public void addAuthorizableToGroup(String authID, Group group) throws RepositoryException;

	public void addAuthorizableToGroup(String authID, Principal groupID) throws RepositoryException;

	public void addAuthorizableToGroup(String authID, String groupID) throws RepositoryException;

	public void clean() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

	public Group createGroupParty(String party) throws AuthorizableExistsException, RepositoryException;
	
	public Group createGroupPermission(String permission) throws AuthorizableExistsException, RepositoryException;

	public Authorizable getAuthorizable(String id, String type) throws RepositoryException;

	public List<Authorizable> getAuthorizables(String type) throws AccessDeniedException, UnsupportedRepositoryOperationException,
			RepositoryException;

	public UserManager getManager();

	public Session getSession();

	public List<Authorizable> member(Group group) throws RepositoryException;

	public List<Authorizable> member(String group) throws RepositoryException;

	public List<Group> memberOfGroup(Authorizable auth) throws RepositoryException;

	public List<Group> memberOfGroup(String auth) throws RepositoryException;

	public void removeAuthorizableOfGroup(Authorizable auth, Group group) throws RepositoryException;

	public void removeAuthorizableOfGroup(Authorizable auth, Principal group) throws RepositoryException;

	public void removeAuthorizableOfGroup(Authorizable auth, String group) throws RepositoryException;

	public void removeAuthorizableOfGroup(Principal auth, Principal group) throws RepositoryException;

	public void removeAuthorizableOfGroup(Principal auth, String group) throws RepositoryException;

	public void removeAuthorizableOfGroup(String auth, Group group) throws RepositoryException;

	public void removeAuthorizableOfGroup(String auth, String group) throws RepositoryException;
	
	public Group createGroupRelationship(String party, String relationship) throws RepositoryException;
}
