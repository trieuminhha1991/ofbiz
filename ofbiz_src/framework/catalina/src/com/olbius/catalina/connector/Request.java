package com.olbius.catalina.connector;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;

/**
 * @author Nguyen Ha
 */
public class Request extends org.apache.catalina.connector.Request {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.catalina.connector.Request#doGetSession(boolean)
	 */
	@Override
	protected Session doGetSession(boolean create) {

		boolean disable = "true".equalsIgnoreCase(this.getContext().findParameter("disableSessionCreation"));

		if (disable) {
			return null;
		}

		if (session == null && isCross()) {

			Manager manager = null;
			if (context != null) {
				manager = context.getManager();
			}
			if (manager == null) {
				return null;
			}
			session = manager.createSession(null);
			session.access();

			return session;

		}

		return super.doGetSession(create);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getRequestURI();
	}

	public boolean isCross() {

		String referrer = this.getHeader("referer");

		if (referrer != null) {
			URL url;
			try {
				url = new URL(referrer);
				if (url.getPath().indexOf(this.getContextPath()) != 0) {
					return true;
				}
			} catch (MalformedURLException e) {
				return false;
			}

		}

		return false;
	}
}
