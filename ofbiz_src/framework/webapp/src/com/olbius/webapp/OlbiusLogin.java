package com.olbius.webapp;

import java.util.List;
import java.util.UUID;

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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.control.RequestHandler;

public class OlbiusLogin {

	public final static String module = OlbiusLogin.class.getName();
	public final static String COOKIE_KEY = "m_ek"; // OLD DATA is "ek"

	public static GenericValue checkExternalLoginKey(HttpServletRequest request) throws GenericEntityException {

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			String ek = null;
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(COOKIE_KEY)) {
					ek = cookie.getValue();
					break;
				}
			}
			if (ek != null) {

				Delegator delegator = (Delegator) request.getAttribute("delegator");

				if (delegator == null) {
					delegator = DelegatorFactory.getDelegator(null);
					String useMultitenant = UtilProperties.getPropertyValue("general.properties", "multitenant");
					if ("Y".equals(useMultitenant)) {
						String serverName = request.getServerName();
						List<GenericValue> tenants = delegator.findList("Tenant", EntityCondition.makeCondition("domainName", serverName), null,
								UtilMisc.toList("-createdStamp"), null, false);
						if (UtilValidate.isNotEmpty(tenants)) {
							GenericValue tenant = EntityUtil.getFirst(tenants);
							String tenantId = tenant.getString("tenantId");
							String tenantDelegatorName = delegator.getDelegatorBaseName() + "#" + tenantId;
							delegator = DelegatorFactory.getDelegator(tenantDelegatorName);
						}
					}
				}

				GenericValue value = getExternalValue(ek, delegator);

				if (value != null) {

					Boolean remb = value.getBoolean("remb");

					Long lastAccessed = value.getLong("lastAccessed");

					Long interval = value.getLong("intervalTime");

					if (System.currentTimeMillis() - lastAccessed > interval && !remb) {
						value.setIsFromEntitySync(true);
						value.remove();
						return null;
					}

					return delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", value.getString("userLoginId")), false);

				}
			}
		}

		return null;
	}

	private static GenericValue getExternalValue(String uuid, Delegator delegator) throws GenericEntityException {
		if (uuid == null) {
			return null;
		}
		return delegator.findOne("ExternalLogin", UtilMisc.toMap("uuid", uuid), false);
	}

	private static void removeExternalValue(String uuid, Delegator delegator) throws GenericEntityException {
		GenericValue value = getExternalValue(uuid, delegator);
		if (value != null) {
			value.setIsFromEntitySync(true);
			value.remove();
		}
	}

	private static void putExternalValue(String uuid, long lastAccessed, long interval, boolean remb, String userLogin, Delegator delegator)
			throws GenericEntityException {

		GenericValue value = delegator.makeValue("ExternalLogin");

		value.set("uuid", uuid);
		value.set("lastAccessed", lastAccessed);
		value.set("intervalTime", interval);
		value.set("remb", remb);
		value.set("userLoginId", userLogin);

		value.create();
	}

	public static String getExternalLoginKey(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		boolean externalLoginKeyEnabled = "true"
				.equals(UtilProperties.getPropertyValue("security", "security.login.externalLoginKey.enabled", "true"));

		if (!externalLoginKeyEnabled) {
			removeCookie(response);
			return "";
		}

		HttpSession session = request.getSession();

		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		synchronized (session) {

			String sesExtKey = (String) session.getAttribute(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR);

			if (sesExtKey != null) {
				if (LoginWorker.isAjax(request)) {
					return "";
				}

				removeExternalValue(sesExtKey, (Delegator) request.getAttribute("delegator"));
			}

			if (userLogin == null) {
				return "";
			}

			String uuid = UUID.randomUUID().toString().replaceAll("-", "");

			session.setAttribute(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, uuid);

			putExternalValue(uuid, session.getLastAccessedTime(), session.getMaxInactiveInterval() * 1000, false, userLogin.getString("userLoginId"),
					userLogin.getDelegator());

			addCookie(uuid, response);
			return "";
		}
	}

	public static void addCookie(String key, HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_KEY, key);
		cookie.setMaxAge(31536000);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void removeCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_KEY, "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static String logout(GenericValue userLogin, HttpServletRequest request, HttpServletResponse response) {
		RequestHandler rh = RequestHandler.getRequestHandler(request.getSession().getServletContext());
		rh.runBeforeLogoutEvents(request, response);

		LoginWorker.doBasicLogout(userLogin, request, response);

		if (request.getAttribute("_AUTO_LOGIN_LOGOUT_") == null) {
			return LoginWorker.autoLoginCheck(request, response);
		}
		return "success";
	}

	public static void cleanupExternalValue(HttpSession session) {
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
