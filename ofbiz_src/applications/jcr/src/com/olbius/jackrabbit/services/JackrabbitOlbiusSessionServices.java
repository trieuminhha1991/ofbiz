package com.olbius.jackrabbit.services;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.core.OlbiusRepository;
import com.olbius.jackrabbit.loader.JackrabbitOlbiusContainer;

public class JackrabbitOlbiusSessionServices {
	
	public static final String JCR_SESSION = "JCR_SESSION";
	public static final String module = JackrabbitOlbiusSessionServices.class.getName();

	public static Map<String, Object> jackrabbitLogin(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String pwd = userLogin.getString("currentPassword");
		// String wps = (String) context.get("workspace");
		String wsp = OlbiusRepository.WSP_DEFAULT;
		String isPublic = (String) context.get("public");
		if(isPublic==null) {
			isPublic = "Y";
		}
		if(isPublic.equals("Y")) {
			wsp = OlbiusRepository.WSP_DEFAULT;
		}
		if(isPublic.equals("N")) {
			wsp = OlbiusRepository.WSP_SECURITY;
		}
		Session jcrSession = null;
		try {
			jcrSession = ((OlbiusRepository)JackrabbitOlbiusContainer.getRepository()).login(userLoginId, pwd, ctx.getDelegator().getDelegatorTenantId(), wsp);
		} catch (RepositoryException e) {
			throw new GenericServiceException(e.getMessage());
		}
		result.put(JackrabbitOlbiusSessionServices.JCR_SESSION, jcrSession);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitLogout(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			jcrSession.logout();
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
