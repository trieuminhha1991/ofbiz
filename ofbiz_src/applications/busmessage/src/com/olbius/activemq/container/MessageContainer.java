package com.olbius.activemq.container;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ContainerConfig.Container.Property;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.activemq.api.HandleError;
import com.olbius.activemq.api.OlbiusDelegator;
import com.olbius.activemq.core.OlbiusActivemqFactory;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.OlbiusEvent;
import com.olbius.jms.event.OlbiusEventFactory;

/**
 * @author Nguyen Ha
 *
 */
public class MessageContainer implements Container {

	public static final String module = MessageContainer.class.getName();
	private ContainerConfig.Container cfg;
	private String CONTAINER_NAME;

	public static OlbiusActivemqFactory ACTIVEMQ_FACTORY;

	public static OlbiusEventFactory EVENT_FACTORY;

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		cfg = ContainerConfig.getContainer(name, configFile);
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);

		if (ACTIVEMQ_FACTORY == null) {
			String url = UtilProperties.getPropertyValue("activemq", "activemq.broker.URL");
			String user = UtilProperties.getPropertyValue("activemq", "activemq.broker.user");
			String pwd = UtilProperties.getPropertyValue("activemq", "activemq.broker.pwd");
			int maxConnection = new Integer(
					UtilProperties.getPropertyValue("activemq", "activemq.broker.maxConnections"));
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
					if (delegator instanceof Delegator) {

						Delegator dlg = ((Delegator) delegator);

						try {
							List<GenericValue> values = dlg.findByAnd("BusId", null, null, false);
							if (!values.isEmpty()) {
								return values.get(0).getString("busId");
							}
						} catch (GenericEntityException e) {
							ACTIVEMQ_FACTORY.getHandleError().handle(e, module);
						}

					}

					return "anonymous";
				}
			});
		}

		if (EVENT_FACTORY == null) {
			EVENT_FACTORY = new OlbiusEventFactory(ACTIVEMQ_FACTORY);
		}

	}

	@Override
	public boolean start() throws ContainerException {

		try {
			final Property propertyQueue = cfg.getProperty("listenerQueue");
			final Property propertyTopic = cfg.getProperty("listenerTopic");

			Set<String> set = new TreeSet<String>();

			String[] values;

			if (propertyQueue != null) {
				values = propertyQueue.value.trim().split(",");
				for (String s : values) {
					set.add(s);
				}
			}

			if (propertyTopic != null) {
				values = propertyTopic.value.trim().split(",");
				for (String s : values) {
					set.add(s);
				}
			}

			Delegator delegator = DelegatorFactory.getDelegator("default");

			for (String s : set) {

				final Delegator tmp;

				if (!"default".equals(s)) {
					String delegatorName = delegator.getDelegatorBaseName() + "#" + s;
					tmp = DelegatorFactory.getDelegator(delegatorName);
				} else {
					tmp = delegator;
				}

				if (ACTIVEMQ_FACTORY.getInstanceId(tmp).equals("anonymous")) {
					OlbiusMessage data = new OlbiusMessage();
					data.setType(OlbiusEvent.REGISTER_TENANT);

					OlbiusMessage message = EVENT_FACTORY.getSendEvent(tmp).get(data);

					if (!message.getDatas().isEmpty()) {
						String busId = (String) message.getDatas().get(0).getMessageData().get("busId");
						GenericValue value = tmp.makeValue("BusId");
						value.set("busId", busId);
						try {
							value.create();
							createReceiveMessage(tmp, propertyQueue, ActivemqSession.QUEUE);
							createReceiveMessage(tmp, propertyTopic, ActivemqSession.TOPIC);
						} catch (GenericEntityException e) {
							ACTIVEMQ_FACTORY.getHandleError().handle(e, module);
						}
					}

				} else {
					createReceiveMessage(tmp, propertyQueue, ActivemqSession.QUEUE);
					createReceiveMessage(tmp, propertyTopic, ActivemqSession.TOPIC);
				}
			}
		} catch (Exception e) {
			throw new ContainerException(e);
		}

		return true;
	}

	private void createReceiveMessage(Delegator tmp, Property property, String type) {

		if (property == null) {
			return;
		}

		for (String x : property.properties.keySet()) {
			String listenerClass = property.properties.get(x).value;
			if (listenerClass == null) {
				continue;
			}

			try {
				EVENT_FACTORY.getReceiveEvent(tmp).putEvent(x, Class.forName(listenerClass));
			} catch (Exception e) {
				ACTIVEMQ_FACTORY.getHandleError().handle(e, module);
			}
		}

		ACTIVEMQ_FACTORY.createConsumer(tmp).receiveMessage(ACTIVEMQ_FACTORY.getReceive(), type,
				EVENT_FACTORY.getReceiveEvent(tmp), true);
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
