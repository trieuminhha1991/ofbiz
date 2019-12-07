package com.olbius.jackrabbit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import javolution.util.FastMap;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.core.OlbiusSession;
import com.olbius.jackrabbit.security.OlbiusUserManager;

public class JackrabbitOlbiusGroupServices {
	public final static String module = JackrabbitOlbiusGroupServices.class.getName();

	public static Map<String, Object> jackrabbitAddUserToGroup(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String userId = (String) context.get("userLoginId");
		String groupId = (String) context.get("groupId");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				OlbiusUserManager userManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				Group group = (Group) userManager.getAuthorizable(groupId, OlbiusUserManager.GROUP);
				User user = (User) userManager.getAuthorizable(userId, OlbiusUserManager.USER);
				if (user != null && group != null) {
					boolean flag = group.addMember(user);
					if (flag) {
						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
					} else {
						jcrSession.logout();
						throw new GenericServiceException("JCR error: can not add user " + userId + " to group " + groupId);
					}
				}
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}

	public static Map<String, Object> jackrabbitCreateGroup(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String groupId = (String) context.get("groupId");
		if (jcrSession != null) {
			try {
				OlbiusUserManager userManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				userManager.createGroup(groupId);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		return result;
	}

	public static Map<String, Object> jackrabbitGetListGroup(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String type = (String) context.get("type");

		if (jcrSession != null) {
			try {
				OlbiusUserManager userManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				List<Authorizable> groups = userManager.getAuthorizables(OlbiusUserManager.GROUP);
				if (type != null) {
					if (type.equals("GROUP")) {
						groups = userManager.getAuthorizables(OlbiusUserManager.GROUP);
					} else if(type.equals("PARTY")) {
						groups = userManager.getAuthorizables(OlbiusUserManager.PARTY);
					} else if(type.equals("RELATIONSHIP")) {
						groups = userManager.getAuthorizables(OlbiusUserManager.RELATIONSHIP);
					}
				} else {
					groups = new ArrayList<Authorizable>();
					groups.addAll(userManager.getAuthorizables(OlbiusUserManager.GROUP));
					groups.addAll(userManager.getAuthorizables(OlbiusUserManager.PARTY));
					groups.addAll(userManager.getAuthorizables(OlbiusUserManager.RELATIONSHIP));
				}
				List<String> list = new ArrayList<String>();
				for (Authorizable g : groups) {
					list.add(g.getID());
				}
				result.put("groups", list);
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

	public static Map<String, Object> jackrabbitGetMember(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String groupId = (String) context.get("groupId");
		if (jcrSession != null) {
			try {
				OlbiusUserManager userManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				List<Authorizable> auths = userManager.member((Group) userManager.getManager().getAuthorizable(groupId));
				List<String> list = new ArrayList<String>();
				for (Authorizable a : auths) {
					list.add(a.toString());
				}
				result.put("auths", list);
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

	public static Map<String, Object> jackrabbitRemoveUserOfGroup(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String userId = (String) context.get("userLoginId");
		String groupId = (String) context.get("groupId");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				OlbiusUserManager userManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				Group group = (Group) userManager.getAuthorizable(groupId, OlbiusUserManager.GROUP);
				User user = (User) userManager.getAuthorizable(userId, OlbiusUserManager.USER);
				if (user != null && group != null) {
					boolean flag = group.removeMember(user);
					if (flag) {
						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
					} else {
						jcrSession.logout();
						throw new GenericServiceException("JCR error: can not remove user " + userId + " of group " + groupId);
					}
				}
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
