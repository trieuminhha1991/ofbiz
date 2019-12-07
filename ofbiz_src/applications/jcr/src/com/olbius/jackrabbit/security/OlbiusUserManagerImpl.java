package com.olbius.jackrabbit.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.QueryBuilder.Direction;
import org.apache.jackrabbit.core.security.principal.PrincipalImpl;

import com.olbius.jackrabbit.core.Constant;

public class OlbiusUserManagerImpl implements OlbiusUserManager {

	private UserManager manager;
	private Session session;
	private String tenantId;

	public OlbiusUserManagerImpl(Session session) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		this.session = session;
		this.manager = ((JackrabbitSession) session).getUserManager();
		this.tenantId = session.getUserID().substring(0, session.getUserID().indexOf("#"));
	}

	public OlbiusUserManagerImpl(Session session, String tenantId) throws AccessDeniedException, UnsupportedRepositoryOperationException,
			RepositoryException {
		this.session = session;
		this.manager = ((JackrabbitSession) session).getUserManager();
		if(tenantId == null) {
			this.tenantId = Constant.getTenantDefault();
		} else {
			this.tenantId = tenantId;
		}
	}

	@Override
	public void addAuthorizableToGroup(Authorizable auth, Group group) throws RepositoryException {
		group.addMember(auth);
	}

	@Override
	public void addAuthorizableToGroup(Authorizable auth, String groupID) throws RepositoryException {
		Authorizable group = getAuthorizable(groupID);
		if (group == null) {
			throw new RepositoryException(groupID + "not exist");
		} else if (!group.isGroup()) {
			throw new RepositoryException(groupID + "is not group");
		}
		addAuthorizableToGroup(auth, (Group) group);
	}

	@Override
	public void addAuthorizableToGroup(Principal authID, Principal groupID) throws RepositoryException {
		Authorizable group = getAuthorizable(groupID);
		if (group == null) {
			throw new RepositoryException(groupID.getName() + "not exist");
		} else if (!group.isGroup()) {
			throw new RepositoryException(groupID.getName() + "is not group");
		}
		Authorizable auth = getAuthorizable(authID);
		if (auth == null) {
			throw new RepositoryException(authID.getName() + "not exist");
		}
		addAuthorizableToGroup(auth, (Group) group);
	}

	@Override
	public void addAuthorizableToGroup(Principal authID, String groupID) throws RepositoryException {
		Authorizable group = getAuthorizable(groupID);
		if (group == null) {
			throw new RepositoryException(groupID + "not exist");
		} else if (!group.isGroup()) {
			throw new RepositoryException(groupID + "is not group");
		}
		Authorizable auth = getAuthorizable(authID);
		if (auth == null) {
			throw new RepositoryException(authID.getName() + "not exist");
		}
		addAuthorizableToGroup(auth, (Group) group);
	}

	@Override
	public void addAuthorizableToGroup(String authID, Group group) throws RepositoryException {
		Authorizable auth = getAuthorizable(authID);
		if (auth == null) {
			throw new RepositoryException(authID + "not exist");
		}
		addAuthorizableToGroup(auth, group);
	}

	@Override
	public void addAuthorizableToGroup(String authID, Principal groupID) throws RepositoryException {
		Authorizable group = getAuthorizable(groupID);
		if (group == null) {
			throw new RepositoryException(groupID.getName() + "not exist");
		} else if (!group.isGroup()) {
			throw new RepositoryException(groupID.getName() + "is not group");
		}
		Authorizable auth = getAuthorizable(authID);
		if (auth == null) {
			throw new RepositoryException(authID + "not exist");
		}
		addAuthorizableToGroup(auth, (Group) group);
	}

	@Override
	public void addAuthorizableToGroup(String authID, String groupID) throws RepositoryException {
		Authorizable group = getAuthorizable(groupID);
		if (group == null) {
			throw new RepositoryException(groupID + "not exist");
		} else if (!group.isGroup()) {
			throw new RepositoryException(groupID + "is not group");
		}
		Authorizable auth = getAuthorizable(authID);
		if (auth == null) {
			throw new RepositoryException(authID + "not exist");
		}
		addAuthorizableToGroup(auth, (Group) group);
	}

	@Override
	public void autoSave(boolean enable) throws UnsupportedRepositoryOperationException, RepositoryException {
		manager.autoSave(enable);
	}

	@Override
	public void clean() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		Iterator<Authorizable> auths = getAuthorizables(Authorizable.class);
		while (auths.hasNext()) {
			auths.next().remove();
		}
		getSession().save();
	}

	@Override
	public Group createGroup(Principal principal) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(principal, tenantId);
	}

	@Override
	public Group createGroup(Principal principal, String intermediatePath) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(principal, intermediatePath);
	}

	@Override
	public Group createGroup(String groupID) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(tenantId + GROUP + groupID, new PrincipalImpl(tenantId + GROUP + groupID), tenantId);
	}

	@Override
	public Group createGroup(String groupID, Principal principal, String intermediatePath) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(groupID, principal, intermediatePath);
	}

	@Override
	public Group createGroupParty(String party) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(tenantId + PARTY + party, new PrincipalImpl(tenantId + PARTY + party), tenantId);
	}

	@Override
	public Group createGroupPermission(String permission) throws AuthorizableExistsException, RepositoryException {
		return manager.createGroup(tenantId + PERMISSION + permission, new PrincipalImpl(tenantId + PERMISSION + permission), tenantId);
	}

	@Override
	public User createUser(String userID, String password) throws AuthorizableExistsException, RepositoryException {
		return manager.createUser(tenantId + USER + userID, password, new PrincipalImpl(tenantId + USER + userID), tenantId);
	}

	@Override
	public User createUser(String userID, String password, Principal principal, String intermediatePath) throws AuthorizableExistsException,
			RepositoryException {
		return manager.createUser(tenantId + USER + userID, password, principal, intermediatePath);
	}

	@Override
	public Iterator<Authorizable> findAuthorizables(Query query) throws RepositoryException {
		return manager.findAuthorizables(query);
	}

	@Override
	public Iterator<Authorizable> findAuthorizables(String relPath, String value) throws RepositoryException {
		return manager.findAuthorizables(relPath, value);
	}

	@Override
	public Iterator<Authorizable> findAuthorizables(String relPath, String value, int searchType) throws RepositoryException {
		return manager.findAuthorizables(relPath, value, searchType);
	}

	@Override
	public Authorizable getAuthorizable(Principal principal) throws RepositoryException {
		return manager.getAuthorizable(principal);
	}

	@Override
	public Authorizable getAuthorizable(String id) throws RepositoryException {
		return manager.getAuthorizable(id);
	}

	@Override
	public Authorizable getAuthorizable(String id, String type) throws RepositoryException {
		return manager.getAuthorizable(tenantId + type + id);
	}

	@Override
	public Authorizable getAuthorizableByPath(String path) throws UnsupportedRepositoryOperationException, RepositoryException {
		return manager.getAuthorizableByPath(path);
	}

	protected Iterator<Authorizable> getAuthorizables(final Class<? extends Authorizable> selector) throws AccessDeniedException,
			UnsupportedRepositoryOperationException, RepositoryException {
		Iterator<Authorizable> auths = findAuthorizables(new Query() {
			@Override
			@SuppressWarnings("unchecked")
			public void build(@SuppressWarnings("rawtypes") QueryBuilder builder) {
				builder.setSortOrder("@name", Direction.ASCENDING);
				builder.setSelector(selector);
			}
		});
		return auths;
	}

	@Override
	public List<Authorizable> getAuthorizables(String type) throws AccessDeniedException, UnsupportedRepositoryOperationException,
			RepositoryException {
		List<Authorizable> list = new ArrayList<Authorizable>();
		Iterator<Authorizable> iterator = null;
		iterator = getAuthorizables(Authorizable.class);
		while (iterator.hasNext()) {
			Authorizable auth = iterator.next();
			if (auth.getID().startsWith(tenantId + type)) {
				list.add(auth);
			}
		}
		return list;
	}

	@Override
	public UserManager getManager() {
		return manager;
	}

	@Override
	public Session getSession() {
		return session;
	}

	// @Override
	// public String getTenantID() {
	// return tenantId;
	// }

	@Override
	public boolean isAutoSave() {
		return manager.isAutoSave();
	}

	@Override
	public List<Authorizable> member(Group group) throws RepositoryException {
		List<Authorizable> auths = new ArrayList<Authorizable>();
		Iterator<Authorizable> iterator = group.getMembers();
		while (iterator.hasNext()) {
			auths.add(iterator.next());
		}
		return auths;
	}

	@Override
	public List<Authorizable> member(String group) throws RepositoryException {
		Authorizable auth = getAuthorizable(group);
		if (auth == null) {
			return new ArrayList<Authorizable>();
		}
		if (auth.isGroup()) {
			return member((Group) auth);
		} else {
			return new ArrayList<Authorizable>();
		}
	}

	@Override
	public List<Group> memberOfGroup(Authorizable auth) throws RepositoryException {
		if (auth == null) {
			return new ArrayList<Group>();
		}
		List<Group> groups = new ArrayList<Group>();
		Iterator<Group> iterator = auth.memberOf();
		while (iterator.hasNext()) {
			groups.add(iterator.next());
		}
		return groups;
	}

	@Override
	public List<Group> memberOfGroup(String auth) throws RepositoryException {
		return memberOfGroup(getAuthorizable(auth));
	}

	@Override
	public void removeAuthorizableOfGroup(Authorizable auth, Group group) throws RepositoryException {
		group.removeMember(auth);
	}

	@Override
	public void removeAuthorizableOfGroup(Authorizable auth, Principal group) throws RepositoryException {
		Authorizable g = getAuthorizable(group);
		if (g == null) {
			throw new RepositoryException(group.getName() + "not exist");
		} else if (!g.isGroup()) {
			throw new RepositoryException(group.getName() + "is not group");
		}
		removeAuthorizableOfGroup(auth, (Group) g);
	}

	@Override
	public void removeAuthorizableOfGroup(Authorizable auth, String group) throws RepositoryException {
		Authorizable g = getAuthorizable(group);
		if (g == null) {
			throw new RepositoryException(group + "not exist");
		} else if (!g.isGroup()) {
			throw new RepositoryException(group + "is not group");
		}
		removeAuthorizableOfGroup(auth, (Group) g);
	}

	@Override
	public void removeAuthorizableOfGroup(Principal auth, Principal group) throws RepositoryException {
		Authorizable g = getAuthorizable(group);
		if (g == null) {
			throw new RepositoryException(group.getName() + "not exist");
		} else if (!g.isGroup()) {
			throw new RepositoryException(group.getName() + "is not group");
		}
		Authorizable a = getAuthorizable(auth);
		if (a == null) {
			throw new RepositoryException(auth.getName() + "not exist");
		}
		removeAuthorizableOfGroup(a, (Group) g);
	}

	@Override
	public void removeAuthorizableOfGroup(Principal auth, String group) throws RepositoryException {
		Authorizable g = getAuthorizable(group);
		if (g == null) {
			throw new RepositoryException(group + "not exist");
		} else if (!g.isGroup()) {
			throw new RepositoryException(group + "is not group");
		}
		Authorizable a = getAuthorizable(auth);
		if (a == null) {
			throw new RepositoryException(auth.getName() + "not exist");
		}
		removeAuthorizableOfGroup(a, (Group) g);
	}

	@Override
	public void removeAuthorizableOfGroup(String auth, Group group) throws RepositoryException {
		Authorizable a = getAuthorizable(auth);
		if (a == null) {
			throw new RepositoryException(auth + "not exist");
		}
		removeAuthorizableOfGroup(a, group);
	}

	@Override
	public void removeAuthorizableOfGroup(String auth, String group) throws RepositoryException {
		Authorizable g = getAuthorizable(group);
		if (g == null) {
			throw new RepositoryException(group + "not exist");
		} else if (!g.isGroup()) {
			throw new RepositoryException(group + "is not group");
		}
		Authorizable a = getAuthorizable(auth);
		if (a == null) {
			throw new RepositoryException(auth + "not exist");
		}
		removeAuthorizableOfGroup(a, (Group) g);
	}

	@Override
	public Group createGroupRelationship(String party, String relationship) throws RepositoryException {

		Authorizable partyGroup = getAuthorizable(party, OlbiusUserManager.PARTY);

		if (partyGroup == null) {
			return null;
		}
		if (!partyGroup.isGroup()) {
			return null;
		}

		Authorizable rels = getAuthorizable(tenantId + RELATIONSHIP + party + "_" + relationship);
		
		if(rels == null) {
			rels = manager.createGroup(tenantId + RELATIONSHIP + party + "_" + relationship, new PrincipalImpl(tenantId + RELATIONSHIP + party
					+ "_" + relationship), tenantId);
		} else if(!rels.isGroup()) {
			rels.remove();
			rels = manager.createGroup(tenantId + RELATIONSHIP + party + "_" + relationship, new PrincipalImpl(tenantId + RELATIONSHIP + party
					+ "_" + relationship), tenantId);
		}

		if(!((Group)partyGroup).isMember(rels)) {
			((Group)partyGroup).addMember(rels);
		}
		
		return (Group) rels;
	}

}
