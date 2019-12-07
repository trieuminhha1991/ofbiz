package com.olbius.webapp.cookie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.security.SecurityConfigurationException;
import org.ofbiz.security.SecurityFactory;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.control.LoginWorker;

import com.olbius.webapp.Webapp;

/**
 * @author Nguyen Ha
 */
public class ExternalKey {

	public final static String module = ExternalKey.class.getName();
	
	private static KeyChecker checker;
	
	public static KeyChecker getChecker() {
		
		if (checker != null) {
			return checker;
		}
		
		boolean externalLoginKeyEnabled = "true"
				.equals(UtilProperties.getPropertyValue("security", "security.login.externalLoginKey.enabled", "true"));
		
		if (externalLoginKeyEnabled) {
			checker = new ExternalCookie();
		} else {
			checker = new KeyChecker() {
				
				@Override
				public String check(HttpServletRequest request, HttpServletResponse response) {
					return "success";
				}

				@Override
				public void clear(HttpSession session) {
				}
			};
		}
		return checker;
	}
	
	public static String check(HttpServletRequest request, HttpServletResponse response) {
		return getChecker().check(request, response);
	}
	
	public static interface KeyChecker {
		String check(HttpServletRequest request, HttpServletResponse response);
		void clear(HttpSession session);
	}
	
	public static class ExternalCookie implements KeyChecker {
		
		public final static String COOKIE_KEY = "m_ek";

		public String check(HttpServletRequest request, HttpServletResponse response) {
			
			GenericValue userLogin = getKey(request, response);
			
			HttpSession session = request.getSession();

			GenericValue currentUserLogin = (GenericValue) session.getAttribute("userLogin");

			if (currentUserLogin != null) {
				return "success";
			}

			if (userLogin != null) {
				if (Webapp.hasBasePermission(userLogin, request)) {
					LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
					Delegator delegator = (Delegator) request.getAttribute("delegator");
					String oldDelegatorName = delegator.getDelegatorName();
					ServletContext servletContext = session.getServletContext();
					if (!oldDelegatorName.equals(userLogin.getDelegator().getDelegatorName())) {
						delegator = DelegatorFactory.getDelegator(userLogin.getDelegator().getDelegatorName());
						dispatcher = ContextFilter.makeWebappDispatcher(servletContext, delegator);
						setWebContextObjects(request, response, delegator, dispatcher);
					}
					LoginWorker.doBasicLogin(userLogin, request);
				}
			}

			return "success";
		}
		
		private void setWebContextObjects(HttpServletRequest request, HttpServletResponse response, Delegator delegator, LocalDispatcher dispatcher) {
	        HttpSession session = request.getSession();
	        Security security = null;
	        try {
	            security = SecurityFactory.getInstance(delegator);
	        } catch (SecurityConfigurationException e) {
	            Debug.logError(e, module);
	        }
	        request.setAttribute("delegator", delegator);
	        request.setAttribute("dispatcher", dispatcher);
	        request.setAttribute("security", security);

	        session.setAttribute("delegatorName", delegator.getDelegatorName());
	        session.setAttribute("delegator", delegator);
	        session.setAttribute("dispatcher", dispatcher);
	        session.setAttribute("security", security);

	    }

		public GenericValue getKey(HttpServletRequest request, HttpServletResponse response) {

			Cookies cookies = (Cookies) request.getAttribute("_COOKIE_HANDLER_");
			
			if (!isCross(request)) {
				
				String appKey = COOKIE_KEY + request.getContextPath().replaceAll("/", "_");
				Cookie cookie = cookies.getCookie(appKey);
				
				HttpSession session = request.getSession();
				GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
				
				if (LoginWorker.isAjax(request)) {
					return userLogin;
				}
				
				if (userLogin != null) {

					if (UtilValidate.isNotEmpty(cookie.getValue())) {
						try {
							removeExternalValue(cookie.getValue(), userLogin.getDelegator());
						} catch (GenericEntityException e) {
							Debug.logError(e, module);
						}
					}
						
					try {

						String uuid = UUID.randomUUID().toString().replaceAll("-", "");

						cookie.setValue(uuid);
						cookie.setMaxAge(session.getMaxInactiveInterval());
						cookie.setPath("/");

						cookies.update(cookie);

						putExternalValue(uuid, session.getLastAccessedTime(), session.getMaxInactiveInterval() * 1000,
								false, userLogin.getString("userLoginId"), userLogin.getDelegator());

						synchronized (session) {
							session.setAttribute(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, uuid);
						}
						
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
					}

				}
				return userLogin;
			} else {

				String appKey = COOKIE_KEY + getWebappReferer(request).replaceAll("/", "_");
				Cookie cookie = cookies.getCookie(appKey);
				
				if (UtilValidate.isNotEmpty(cookie.getValue())) {
					Delegator delegator = (Delegator) request.getAttribute("delegator");
					
					try {
						return getUserLogin(cookie.getValue(), delegator);
					} catch (GenericEntityException e) {
						return null;
					}
					
				}
				
				return null;
			}

		}

		private GenericValue getUserLogin(String uuid, Delegator delegator) throws GenericEntityException {
			GenericValue externalLogin = getExternalValue(uuid, delegator);
			
			if (externalLogin != null) {
				
				Boolean remb = externalLogin.getBoolean("remb");

				Long lastAccessed = externalLogin.getLong("lastAccessed");

				Long interval = externalLogin.getLong("interval");

				if (System.currentTimeMillis() - lastAccessed > interval && !remb) {
					externalLogin.setIsFromEntitySync(true);
					externalLogin.remove();
					return null;
				}

				return delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", externalLogin.getString("userLoginId")), false);
			}
			
			return null;
			
		}
		
		private GenericValue getExternalValue(String uuid, Delegator delegator) throws GenericEntityException {
			if (uuid == null) {
				return null;
			}
			return delegator.findOne("ExternalLogin", UtilMisc.toMap("uuid", uuid), false);
		}

		private void removeExternalValue(String uuid, Delegator delegator) throws GenericEntityException {
			GenericValue value = getExternalValue(uuid, delegator);
			if (value != null) {
				value.setIsFromEntitySync(true);
				value.remove();
			}
		}

		private void putExternalValue(String uuid, long lastAccessed, long interval, boolean remb, String userLogin,
				Delegator delegator) throws GenericEntityException {

			GenericValue value = delegator.makeValue("ExternalLogin");

			value.set("uuid", uuid);
			value.set("lastAccessed", lastAccessed);
			value.set("intervalTime", interval);
			value.set("remb", remb);
			value.set("userLoginId", userLogin);

			value.create();
		}

		public boolean isCross(HttpServletRequest request) {

			String referrer = request.getHeader("referer");

			if (referrer != null) {
				URL url;
				try {
					url = new URL(referrer);
					if (url.getPath().indexOf(request.getContextPath()) != 0) {
						return true;
					}
				} catch (MalformedURLException e) {
					return false;
				}

			}

			return false;
		}
		
		public String getWebappReferer(HttpServletRequest request) {
			String referrer = request.getHeader("referer");
			if (referrer != null) {
				URL url;
				try {
					url = new URL(referrer);
					String path = url.getPath();
					int index = path.indexOf('/', 1);
					return path.substring(0, index);
				} catch (MalformedURLException e) {
				}
			}
			return "";
		}
		
		public void clear(HttpSession session) {
			String sesExtKey = (String) session.getAttribute(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR);
			if (sesExtKey != null) {
				try {
					removeExternalValue(sesExtKey, DelegatorFactory.getDelegator((String) session.getAttribute("delegatorName")));
				} catch (GenericEntityException e) {
					Debug.logError(e.getMessage(), module);
				}
			}
		}
	}

}
