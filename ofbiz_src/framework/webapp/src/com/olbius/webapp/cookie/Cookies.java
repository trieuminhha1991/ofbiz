package com.olbius.webapp.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nguyen Ha
 */
public class Cookies {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String, Cookie> cookies = new HashMap<String, Cookie>();
	private Set<String> updateCookies = new TreeSet<String>();
	private boolean load = false;
	
	public Cookies(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public Cookie getCookie(String key) {
		
		if (!load) {
			Cookie[] cookies = this.request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					this.cookies.put(cookie.getName(), cookie);
				}
			}
			load = true;
		}
		
		Cookie cookie = this.cookies.get(key);
		
		if (cookie == null) {
			cookie = new Cookie(key, "");
			this.cookies.put(key, cookie);
		}
		
		return cookie;
		
	}
	
	public String getCookieValue(String key) {
		return getCookie(key).getValue();
	}

	public void update(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
		updateCookies.add(cookie.getName());
	}
	
	public void updateValue(String key, String value) {
		Cookie cookie = getCookie(key);
		cookie.setValue(value);
		update(cookie);
	}
	
	public void remove(String key) {
		Cookie cookie = getCookie(key);
		cookie.setMaxAge(0);
		update(cookie);
	}
	
	public void apply() {
		for (String s : this.updateCookies) {
			response.addCookie(cookies.get(s));
		}
		clear();
	}
	
	public void clear() {
		updateCookies.clear();
	}
	
	public void clear(String key) {
		updateCookies.remove(key);
	}
	
}
