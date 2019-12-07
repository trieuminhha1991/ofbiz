package com.olbius.webapp;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class OlbiusCookie {

	public static void removeCookie(String key, HttpServletResponse response) {
		Cookie cookie = new Cookie(key, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void addUserCookie(String userLoginId, Delegator delegator, HttpServletResponse response) {
		GenericValue value = null;
		String iu = "";
		try {
			value = delegator.findOne("UserCookie", true, "userId", userLoginId);
		} catch (GenericEntityException e) {

		}
		if (value != null) {
			iu = value.getString("cookie");
		} else {
			iu = UUID.randomUUID().toString().replace("-", "");
			value = delegator.makeValue("UserCookie", UtilMisc.toMap("userId", userLoginId, "cookie", iu));
			try {
				delegator.create(value);
			} catch (GenericEntityException e) {

			}
		}
		Cookie cookie = new Cookie("iu", iu);
		cookie.setMaxAge(31536000);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void addTrackingCookie(String key, HttpServletRequest request, HttpServletResponse response) {
		
		Cookie[] cookies = request.getCookies();

		boolean flagCk = true;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					cookie.setPath("/");
					cookie.setMaxAge(31536000);
					response.addCookie(cookie);
					flagCk = false;
					break;
				}
			}
		}

		if (flagCk) {
			Cookie cookie = new Cookie(key, UUID.randomUUID().toString().replace("-", ""));
			cookie.setMaxAge(31536000);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}
}
