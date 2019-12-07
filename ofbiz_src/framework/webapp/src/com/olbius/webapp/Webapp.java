package com.olbius.webapp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

import com.olbius.security.api.Application;
import com.olbius.security.util.SecurityUtil;
import com.olbius.webapp.cookie.Cookies;

/**
 * @author Nguyen Ha
 */
public class Webapp {

	public static final String module = Webapp.class.getName();
	
	public static boolean hasBasePermission(GenericValue userLogin, HttpServletRequest request) {
        Security security = (Security) request.getAttribute("security");
        if (security != null) {
            ServletContext context = (ServletContext) request.getAttribute("servletContext");
            String serverId = (String) context.getAttribute("_serverId");
            
            // get a context path from the request, if it is empty then assume it is the root mount point
            String contextPath = request.getContextPath();
            if (UtilValidate.isEmpty(contextPath)) {
                contextPath = "/";
            }
            
            ComponentConfig.WebappInfo info = ComponentConfig.getWebAppInfo(serverId, contextPath);
            
            
            
            if (info != null) {
            	
            	if (info.getBasePermission().length > 0) {
            		for (String permission: info.getBasePermission()) {
                        if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin)) {
                            return false;
                        }
                    }
            	} else {
            		return SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, Application.WEBAPP, contextPath);
            	}
            	
            } else {
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextPath, module);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", module);
        }

        return true;
    }
	
	public static String cookies(HttpServletRequest request, HttpServletResponse response) {
		
		Cookies cookies = new Cookies(request, response);
		
		request.setAttribute("_COOKIE_HANDLER_", cookies);
		
		return "success";
		
	}
	
	public static String apply(HttpServletRequest request, HttpServletResponse response) {
		
		Cookies cookies = (Cookies) request.getAttribute("_COOKIE_HANDLER_");
		
		cookies.apply();
		
		return "success";
		
	}
	
	public static String responseCodeUnauthorized(HttpServletRequest request, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		return "success";
	}
	
}
