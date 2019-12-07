package com.olbius.jackrabbit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import javolution.util.FastMap;

import org.apache.jackrabbit.api.security.user.Group;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.core.OlbiusSession;
import com.olbius.jackrabbit.security.OlbiusAccessControlManager;
import com.olbius.jackrabbit.security.OlbiusAccessControlManagerImpl;
import com.olbius.jackrabbit.security.OlbiusUserManager;

public class JackrabbitOlbiusSecurityServices {
	public final static String module = JackrabbitOlbiusSecurityServices.class.getName();

	public static Map<String, Object> jackrabbitAddEntry(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String authId = (String) context.get("authId");
		String path = (String) context.get("path");
		List<?> list = (List<?>) context.get("privileges");
		
		boolean allow = (Boolean) context.get("allow");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusSession session = (OlbiusSession) jcrSession;
				OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(jcrSession);
				
				Privilege[] privileges = new Privilege[list.size()];
				for(int i = 0; i < list.size(); i++) {
					privileges[i] = acm.privilege((String)list.get(i));
				}
				
//				OlbiusNode node = ((OlbiusSession)session).getOlbiusNode();
//				node.setNode(session.getNode(path));
				acm.addEntry(session.getNode(path), session.getPrincipalManager().getPrincipal(authId), privileges, allow);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}

	public static Map<String, Object> jackrabbitRemoveEntry(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String authId = (String) context.get("authId");
		String path = (String) context.get("path");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusSession session = (OlbiusSession) jcrSession;
				OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(jcrSession);
//				OlbiusNode node = session.getOlbiusTenantNode(path);
				acm.removeEntry(session.getNode(path), session.getPrincipalManager().getPrincipal(authId));
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}

	public static Map<String, Object> jackrabbitGetEntry(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String authId = (String) context.get("authId");
		String path = (String) context.get("path");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusSession session = (OlbiusSession) jcrSession;
				OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(jcrSession);
//				OlbiusNode node = session.getOlbiusTenantNode(path);
				AccessControlEntry[] entries = null;
				if(authId != null) {
					entries = acm.getEntry(session.getNode(path), session.getPrincipalManager().getPrincipal(authId));
				} else {
					entries = acm.getEntry(session.getNode(path));
				}
				
				List<AccessControlEntry> list = new ArrayList<AccessControlEntry>();
				for (AccessControlEntry ace : entries) {
					list.add(ace);
				}
				result.put("entries", list);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}

	public static Map<String, Object> jackrabbitAddSecurityPermission(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String groupId = (String) context.get("groupId");
		String permissionId = (String) context.get("permissionId");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusSession session = (OlbiusSession) jcrSession;
				OlbiusUserManager olbiusUserManager = session.getOlbiusUserManager();
				Group group = (Group) olbiusUserManager.getAuthorizable(groupId, OlbiusUserManager.GROUP);
				Group permission = (Group) olbiusUserManager.getAuthorizable(permissionId, OlbiusUserManager.PERMISSION);
				if (group != null && permission != null) {
					permission.addMember(group);
				} else {
					jcrSession.logout();
					throw new GenericServiceException("GroupId or Permissionid not found");
				}
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}
	public static Map<String, Object> jackrabbitRemoveSecurityPermission(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String groupId = (String) context.get("groupId");
		String permissionId = (String) context.get("permissionId");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusSession session = (OlbiusSession) jcrSession;
				OlbiusUserManager olbiusUserManager = session.getOlbiusUserManager();
				Group group = (Group) olbiusUserManager.getAuthorizable(groupId, OlbiusUserManager.GROUP);
				Group permission = (Group) olbiusUserManager.getAuthorizable(permissionId, OlbiusUserManager.PERMISSION);
				if (group != null && permission != null) {
					if(permission.isMember(group)) {
						permission.removeMember(group);
					}
				} else {
					jcrSession.logout();
					throw new GenericServiceException("GroupId or Permissionid not found");
				}
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}
}
