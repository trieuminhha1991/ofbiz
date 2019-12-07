package com.olbius.jackrabbit.tenant;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.principal.EveryonePrincipal;
import org.apache.jackrabbit.core.security.principal.PrincipalImpl;

import com.olbius.jackrabbit.core.OlbiusRepository;
import com.olbius.jackrabbit.loader.OlbiusContainer;
import com.olbius.jackrabbit.security.OlbiusAccessControlManager;
import com.olbius.jackrabbit.security.OlbiusAccessControlManagerImpl;
import com.olbius.jackrabbit.security.OlbiusUserManager;
import com.olbius.jackrabbit.security.OlbiusUserManagerImpl;

public class OlbiusTenantImpl implements OlbiusTenant {
	
	private String tenantId;
	private Session session;
	private Group tenantGroup;
	private Node tenantRootPrivate;
	private Node tenantRootPublic;
	private OlbiusUserManager manager;
	
	public OlbiusTenantImpl(String tenantId, Session session) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		this.tenantId = tenantId;
		this.session = session;
		this.manager = new OlbiusUserManagerImpl(session, tenantId);
	}
	
	public void init(Map<String, Set<String>> webapp, List<String> parties, Map<String, Map<String, String>> partyRelationship,Map<String, Map<String, String>> mapUser, Map<String, Set<String>> mapGroup,
			Map<String, Set<String>> mapPermission, Set<String> admin) throws RepositoryException {
		createTenant();
		createWebAppFolder(webapp);
		createParty(parties);
		createPartyRelationship(partyRelationship);
		createUser(mapUser);
		createGroup(mapGroup);
		createPermission(mapPermission);
		setAdminPermission(admin);
		tenantRootPublic.getSession().logout();
	}

	@Override
	public void createPartyRelationship(Map<String, Map<String, String>> partyRelationship) throws RepositoryException {
		for(String x : partyRelationship.keySet()) {
			for(String y : partyRelationship.get(x).keySet()) {
				Group group = manager.createGroupRelationship(x, partyRelationship.get(x).get(y));
				Authorizable party = manager.getAuthorizable(y, OlbiusUserManager.PARTY);
				if(group != null && party!= null) {
					if(!group.isMember(party)) {
						group.addMember(party);
					}
				}
			}
		}
	}
	
	@Override
	public void createParty(List<String> parties) throws RepositoryException {
		Authorizable auth = null;
		for (String x : parties) {
			auth = manager.getAuthorizable(x, OlbiusUserManager.PARTY);
			if (auth == null) {
				auth = manager.createGroupParty(x);
			} else if (!auth.isGroup()) {
				auth.remove();
				manager.createGroupParty(x);
			}
		}
	}
	
	@Override
	public void createTenant() throws RepositoryException {
		if (((SessionImpl) session).isAdmin()) {
			Authorizable tenantGroup = manager.getManager().getAuthorizable(tenantId);
			if (tenantGroup == null) {
				tenantGroup = manager.getManager().createGroup(tenantId, new PrincipalImpl(tenantId), tenantId);
			} else if (!tenantGroup.isGroup()) {
				tenantGroup.remove();
				tenantGroup = manager.getManager().createGroup(tenantId, new PrincipalImpl(tenantId), tenantId);
			}
			this.tenantGroup = (Group) tenantGroup;
			Node tenantRoot;
			try {
				tenantRoot = session.getRootNode().getNode(tenantId);
			} catch (PathNotFoundException e) {
				tenantRoot = session.getRootNode().addNode(tenantId, NodeType.NT_FOLDER);
				session.save();
			}

			this.tenantRootPrivate = tenantRoot;

			Session session2 = ((SessionImpl)session).createSession(OlbiusRepository.WSP_DEFAULT);
			
			try {
				tenantRoot = session2.getRootNode().getNode(tenantId);
			} catch (PathNotFoundException e) {
				tenantRoot = session2.getRootNode().addNode(tenantId, NodeType.NT_FOLDER);
				session2.save();
			}
			
			this.tenantRootPublic = tenantRoot;
			
			OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(session);

			Privilege[] privileges = new Privilege[] { acm.privilege(Privilege.JCR_READ) };
			if(!checkSecurity(acm, privileges, tenantRoot, EveryonePrincipal.getInstance(), false)) {
				acm.addEntry(tenantRoot, EveryonePrincipal.getInstance(), privileges, false);
			}
			if(!checkSecurity(acm, privileges, tenantRoot, tenantGroup.getPrincipal(), true)) {
				acm.addEntry(tenantRoot, tenantGroup, privileges, true);
			}
			
		} else {
			return;
		}
	}

	@Override
	public void createWebAppFolder(Map<String, Set<String>> webapp) throws RepositoryException {
		OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(session);
		OlbiusAccessControlManager acmPublic = new OlbiusAccessControlManagerImpl(tenantRootPublic.getSession());
		
		for (String x : webapp.keySet()) {
			Node app = null;
			try {
//				app = tenantRootPrivate.getNode(x.toLowerCase());
				app = tenantRootPrivate.getNode(x);
			} catch (PathNotFoundException e) {
//				app = tenantRootPrivate.addNode(x.toLowerCase(), NodeType.NT_FOLDER);
				app = tenantRootPrivate.addNode(x, NodeType.NT_FOLDER);
				session.save();
			}

			Node appPublic = null;
			try {
//				appPublic = tenantRootPublic.getNode(x.toLowerCase());
				appPublic = tenantRootPublic.getNode(x);
			} catch (PathNotFoundException e) {
//				appPublic = tenantRootPublic.addNode(x.toLowerCase(), NodeType.NT_FOLDER);
				appPublic = tenantRootPublic.addNode(x, NodeType.NT_FOLDER);
				tenantRootPublic.getSession().save();
			}
			
			Privilege[] privileges = new Privilege[] { acm.privilege(Privilege.JCR_READ) };
			
			if(!checkSecurity(acm, privileges, app, EveryonePrincipal.getInstance(), false)) {
				acm.addEntry(app, EveryonePrincipal.getInstance(), privileges, false);
			}
			
			for (String y : webapp.get(x)) {

				for (String z : OlbiusContainer.getMapPermission().keySet()) {
					Authorizable permissionGroup = manager.getAuthorizable(y + "_" + z, OlbiusUserManager.PERMISSION);

					if (permissionGroup == null) {
						permissionGroup = manager.createGroupPermission(y + "_" + z);
					} else if (!permissionGroup.isGroup()) {
						permissionGroup.remove();
						permissionGroup = manager.createGroupPermission(y + "_" + z);
					}

					privileges = acm.privilege(OlbiusContainer.getMapPermission().get(z));

					if(!checkSecurity(acm, privileges, app, permissionGroup.getPrincipal(), true)) {
						acm.addEntry(app, permissionGroup, privileges, true);
					}
					
					if(!checkSecurity(acmPublic, privileges, appPublic, permissionGroup.getPrincipal(), true)) {
						acmPublic.addEntry(appPublic, permissionGroup, privileges, true);
					}
					
					if (!tenantGroup.isMember(permissionGroup)) {
						tenantGroup.addMember(permissionGroup);
					}
				}
			}
		}
	}

	@Override
	public void createUser(Map<String, Map<String, String>> mapUser) throws RepositoryException {
		Authorizable auth = null;
		for (String x : mapUser.keySet()) {
			auth = manager.getAuthorizable(x, OlbiusUserManager.USER);
			if (auth == null) {
				auth = manager.createUser(x, mapUser.get(x).get("pwd"));
			} else if (auth.isGroup()) {
				auth.remove();
				manager.createUser(x, mapUser.get(x).get("pwd"));
			}
			String partyId = mapUser.get(x).get("partyId");
			if(partyId != null) {
				Authorizable party = manager.getAuthorizable(partyId, OlbiusUserManager.PARTY);
				if(party != null && party.isGroup()) {
					if (!((Group) party).isMember(auth)) {
						((Group) party).addMember(auth);
					}
				}
			}
		}
	}

	@Override
	public void createGroup(Map<String, Set<String>> mapGroup) throws RepositoryException {
		Authorizable auth = null;
		Group group = null;
		User user = null;
		for (String x : mapGroup.keySet()) {
			auth = manager.getAuthorizable(x, OlbiusUserManager.GROUP);
			if (auth == null) {
				group = manager.createGroup(x);
			} else if (!auth.isGroup()) {
				auth.remove();
				group = manager.createGroup(x);
			} else {
				group = (Group) auth;
			}

			if (!tenantGroup.isMember(group)) {
				tenantGroup.addMember(group);
			}

			for (String y : mapGroup.get(x)) {
				user = (User) manager.getAuthorizable(y, OlbiusUserManager.USER);
				if (!group.isMember(user)) {
					group.addMember(user);
				}
			}
		}
	}

	@Override
	public void createPermission(Map<String, Set<String>> mapPermission) throws RepositoryException {
		Authorizable auth = null;
		Group group = null;
		for (String x : mapPermission.keySet()) {
			auth = manager.getAuthorizable(x, OlbiusUserManager.PERMISSION);
			if(auth == null) {
				auth = manager.createGroupPermission(x);
			} else if (!auth.isGroup()) {
				auth.remove();
				auth = manager.createGroupPermission(x);
			}
			
			for (String y : mapPermission.get(x)) {
				group = (Group) manager.getAuthorizable(y, OlbiusUserManager.GROUP);

				if (!((Group) auth).isMember(group)) {
					((Group) auth).addMember(group);
				}
			}
		}
	}

	@Override
	public void setAdminPermission(Set<String> admin) throws RepositoryException {
		if (session.getWorkspace().getName().equals(OlbiusRepository.WSP_DEFAULT)) {
			return;
		}
		OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(session);
		OlbiusUserManager manager = new OlbiusUserManagerImpl(session, tenantId);
		Privilege[] privileges = null;
		privileges = new Privilege[] { acm.privilege(Privilege.JCR_READ) };
		
		Node user = session.getNode("/rep:security/rep:authorizables/rep:users/" + tenantId);
		Node group = session.getNode("/rep:security/rep:authorizables/rep:groups/" + tenantId);
		
//		acm.addEntry(session.getNode("/rep:security/rep:authorizables/rep:groups/" + tenantId), EveryonePrincipal.getInstance(), privileges, false);
//		acm.addEntry(session.getNode("/rep:security/rep:authorizables/rep:users/" + tenantId), tenantGroup, privileges, true);
//		acm.addEntry(session.getNode("/rep:security/rep:authorizables/rep:groups/" + tenantId), tenantGroup, privileges, true);
		
		if(!checkSecurity(acm, privileges, user, EveryonePrincipal.getInstance(), false)) {
			acm.addEntry(user, EveryonePrincipal.getInstance(), privileges, false);
		}
		
		if(!checkSecurity(acm, privileges, group, EveryonePrincipal.getInstance(), false)) {
			acm.addEntry(group, EveryonePrincipal.getInstance(), privileges, false);
		}
		
		if(!checkSecurity(acm, privileges, user, tenantGroup.getPrincipal(), true)) {
			acm.addEntry(user, tenantGroup, privileges, true);
		}
		
		if(!checkSecurity(acm, privileges, group, tenantGroup.getPrincipal(), true)) {
			acm.addEntry(group, tenantGroup, privileges, true);
		}
		
		for (String x : admin) {
			for (String z : OlbiusContainer.getMapPermission().keySet()) {

				Authorizable permissionGroup = manager.getAuthorizable(x + "_" + z, OlbiusUserManager.PERMISSION);
				if (permissionGroup == null) {
					permissionGroup = manager.createGroupPermission(x + "_" + z);
				} else if (!permissionGroup.isGroup()) {
					permissionGroup.remove();
					permissionGroup = manager.createGroupPermission(x + "_" + z);
				}

				privileges = acm.privilege(OlbiusContainer.getMapPermission().get(z));
				if(!checkSecurity(acm, privileges, user, permissionGroup.getPrincipal(), true)) {
					acm.addEntry(user, permissionGroup, privileges, true);
				}
				
				if(!checkSecurity(acm, privileges, group, permissionGroup.getPrincipal(), true)) {
					acm.addEntry(group, permissionGroup, privileges, true);
				}
//				acm.addEntry(session.getNode("/rep:security/rep:authorizables/rep:users/" + tenantId), permissionGroup, privileges, true);
//				acm.addEntry(session.getNode("/rep:security/rep:authorizables/rep:groups/" + tenantId), permissionGroup, privileges, true);
			}
		}
	}

	private boolean checkSecurity(OlbiusAccessControlManager acm, Privilege[] privileges, Node node, Principal principal, boolean isAllow) throws RepositoryException {
		String[] strings = new String[privileges.length];
		for(int i = 0; i < privileges.length; i++) {
			strings[i] = privileges[i].getName();
		}
		Arrays.sort(strings);
		AccessControlEntry[] entries = acm.getEntry(node, principal);
		if(entries.length != 0) {
			AccessControlEntry entry = entries[0];
			JackrabbitAccessControlEntry entry2 = (JackrabbitAccessControlEntry) entry;
			if(entry2.isAllow()!=isAllow) {
				return false;
			}
			privileges = entry.getPrivileges();
			String[] strings2 = new String[privileges.length];
			for(int i = 0; i < privileges.length; i++) {
				strings2[i] = privileges[i].getName();
			}
			Arrays.sort(strings2);
			return Arrays.equals(strings, strings2);
		} else {
			return false;
		}
	}
	
	@Override
	public String getTenantID() {
		return tenantId;
	}

	@Override
	public Session getSession() {
		return session;
	}

}
