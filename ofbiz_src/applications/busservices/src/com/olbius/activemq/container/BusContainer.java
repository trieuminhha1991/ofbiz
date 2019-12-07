package com.olbius.activemq.container;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerConfig.Container.Property;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;

import com.olbius.activemq.api.ActivemqFactory;
import com.olbius.activemq.api.ActivemqSession;
import com.olbius.activemq.api.HandleError;
import com.olbius.activemq.api.OlbiusDelegator;
import com.olbius.activemq.core.OlbiusActivemqFactory;
import com.olbius.jms.event.OlbiusEventFactory;

/**
 * @author Nguyen Ha
 *
 */
public class BusContainer implements Container {

	public static final String module = BusContainer.class.getName();

	public static ActivemqFactory ACTIVEMQ_FACTORY;
	
	public static OlbiusEventFactory EVENT_FACTORY;

	private ContainerConfig.Container cfg;
	private String CONTAINER_NAME;

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {

		cfg = ContainerConfig.getContainer(name, configFile);

		CONTAINER_NAME = name;

		Debug.logInfo("Initializing " + CONTAINER_NAME, module);

		if (ACTIVEMQ_FACTORY == null) {
			String url = UtilProperties.getPropertyValue("activemq", "activemq.broker.URL");
			String user = UtilProperties.getPropertyValue("activemq", "activemq.broker.user");
			String pwd = UtilProperties.getPropertyValue("activemq", "activemq.broker.pwd");
			int maxConnection = new Integer(UtilProperties.getPropertyValue("activemq", "activemq.broker.maxConnections"));
			ACTIVEMQ_FACTORY = new OlbiusActivemqFactory(url, user, pwd, maxConnection);
			ACTIVEMQ_FACTORY.setSend(cfg.getProperty("send").value);
			ACTIVEMQ_FACTORY.setReceive(cfg.getProperty("receive").value);
			ACTIVEMQ_FACTORY.setHandleError(new HandleError() {
				@Override
				public void handle(Exception e, String module) {
					Debug.logError(e, module);
				}
			});
			ACTIVEMQ_FACTORY.setOlbiusDelegator(new OlbiusDelegator() {

				@Override
				public String getTenantId(Object delegator) {

					if (delegator instanceof Delegator) {
						String tenant = ((Delegator) delegator).getDelegatorTenantId();
						if (tenant == null || tenant.isEmpty()) {
							tenant = "default";
						}
						return tenant;
					} else {
						return null;
					}

				}

				@Override
				public String getInstanceId(Object delegator) {
					return null;
				}
			});
		}
		
		if(EVENT_FACTORY == null) {
			EVENT_FACTORY = new OlbiusEventFactory(ACTIVEMQ_FACTORY);
		}
	}

	@Override
	public boolean start() throws ContainerException {

		try {
			listener(cfg.getProperty("listenerQueue"), ActivemqSession.QUEUE);
			listener(cfg.getProperty("listenerTopic"), ActivemqSession.TOPIC);
		} catch (Exception e) {
			throw new ContainerException(e);
		}

		return true;
	}

	private void listener(Property property, String type) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		if (property == null) {
			return;
		}

		Delegator delegator = DelegatorFactory.getDelegator("default");

		for (String s : property.properties.keySet()) {
			Property p = property.properties.get(s);

			Delegator tmp;

			if (!"default".equals(s)) {
				String delegatorName = delegator.getDelegatorBaseName() + "#" + s;
				tmp = DelegatorFactory.getDelegator(delegatorName);
			} else {
				tmp = delegator;
			}

			for (String x : p.properties.keySet()) {
				String listenerClass = p.properties.get(x).value;
				if (listenerClass == null) {
					continue;
				}

				EVENT_FACTORY.getReceiveEvent(tmp).putEvent(x, Class.forName(listenerClass));
			}

			ACTIVEMQ_FACTORY.createConsumer(tmp).receiveMessage(ACTIVEMQ_FACTORY.getReceive(), type,
					EVENT_FACTORY.getReceiveEvent(tmp), false);

		}

	}

	@Override
	public void stop() throws ContainerException {
		ACTIVEMQ_FACTORY.close();
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

}
