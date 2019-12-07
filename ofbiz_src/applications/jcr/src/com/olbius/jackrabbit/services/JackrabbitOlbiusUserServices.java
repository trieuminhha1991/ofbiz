package com.olbius.jackrabbit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import javolution.util.FastMap;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.core.OlbiusSession;
import com.olbius.jackrabbit.loader.JackrabbitOlbiusContainer;
import com.olbius.jackrabbit.security.OlbiusUserManager;

public class JackrabbitOlbiusUserServices {
	public final static String module = JackrabbitOlbiusUserServices.class.getName();

	public static Map<String, Object> jackrabbitChangePwd(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String userId = (String) context.get("userLoginId");
		GenericValue userLogin = null;
		try {
			Delegator delegator = ctx.getDelegator();
			userLogin = delegator.findOne("UserLogin", false, "userLoginId", userId);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}
		if (userLogin != null) {
//			Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
			Session jcrSession = null;
			try {
				jcrSession = JackrabbitOlbiusContainer.getSession();
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
			if (jcrSession != null) {
				try {
					OlbiusUserManager olbiusUserManager = ((OlbiusSession) jcrSession).getOlbiusUserManager(userLogin.getDelegator().getDelegatorTenantId());
					Authorizable user = olbiusUserManager.getAuthorizable(userId, OlbiusUserManager.USER);
					((User) user).changePassword(userLogin.getString("currentPassword"));
				} catch (RepositoryException e) {
					throw new GenericServiceException(e.getMessage());
				} finally {
					jcrSession.logout();
				}
			} else {
				throw new GenericServiceException("JCR_SESSION not found");
			}
		}
		return result;
	}

	public static Map<String, Object> jackrabbitCreateUser(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String userLoginId = (String) context.get("userLoginId");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}
		String pwd = null;
		if (userLogin != null) {
			pwd = userLogin.getString("currentPassword");
		}
		if (pwd == null) {
			throw new GenericServiceException("Invalide JCR User/Pass, Jcr UserLogin is not created");
		}

		String partyId = userLogin.getString("partyId");
		
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusUserManager olbiusUserManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				User user = olbiusUserManager.createUser(userLoginId, pwd);
				
				if(partyId!=null) {
					Group party = (Group) olbiusUserManager.getAuthorizable(partyId, OlbiusUserManager.PARTY);
					if (user != null && party != null) {
						boolean flag = party.addMember(user);
						if (flag) {
							result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
						} else {
							jcrSession.logout();
							throw new GenericServiceException("JCR error: can not add user " + userLoginId + " to party " + partyId);
						}
					}
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

	public static Map<String, Object> jackrabbitDeleteUser(DispatchContext ctx, Map<String, ?> context) {
		return null;
	}

	public static Map<String, Object> jackrabbitGetListUser(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				OlbiusUserManager olbiusUserManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				List<Authorizable> users = olbiusUserManager.getAuthorizables(OlbiusUserManager.USER);
				List<String> list = new ArrayList<String>();
				for (Authorizable u : users) {
					list.add(u.getID());
				}
				result.put("users", list);
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

	public static Map<String, Object> jackrabbitMemberOfGroup(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String userLoginId = (String) context.get("userLoginId");
		if (jcrSession != null) {
			try {
				OlbiusUserManager olbiusUserManager = ((OlbiusSession) jcrSession).getOlbiusUserManager();
				List<Group> groups = olbiusUserManager.memberOfGroup((User) olbiusUserManager.getManager().getAuthorizable(userLoginId));
				List<String> list = new ArrayList<String>();
				for (Group g : groups) {
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
}
