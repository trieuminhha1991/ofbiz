package com.olbius.activemq;

import org.ofbiz.base.util.UtilProperties;

/**
 * @author Nguyen Ha
 *
 */
public class ActivemqFactory {
	private static Activemq activemq;

	public static Activemq getInstance() {

		if (activemq == null) {

			String url = UtilProperties.getPropertyValue("messaging", "activemq.broker.URL");
			String user = UtilProperties.getPropertyValue("messaging", "activemq.broker.user");
			String pwd = UtilProperties.getPropertyValue("messaging", "activemq.broker.pwd");
			int connection = new Integer(UtilProperties.getPropertyValue("messaging", "activemq.broker.maxConnections"));
			activemq = new ActivemqImpl(url, user, pwd);
			activemq.setMaxConnections(connection);
		}

		return activemq;
	}

}
