package com.olbius.activemq.container;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerConfig.Container.Property;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.DelegatorFactory;

import com.olbius.activemq.ActivemqFactory;
import com.olbius.activemq.TextMessage;
import com.olbius.activemq.listener.OlbiusMessageListener;

/**
 * @author Nguyen Ha
 *
 */
public class MessagingContainer implements Container {

	public static final String module = MessagingContainer.class.getName();
	private ContainerConfig.Container cfg;
	private String CONTAINER_NAME;

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		cfg = ContainerConfig.getContainer(name, configFile);
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
	}

	@Override
	public boolean start() throws ContainerException {
		for (String s : cfg.getProperty("listener").properties.keySet()) {
			Property p = cfg.getProperty("listener").properties.get(s);
			
			TextMessage message = new TextMessage();
			message.setDestination(s);
			String type = p.properties.get("type").value;
			if (type != null) {
				message.setType(type);
			}
			String delegatorName = p.properties.get("delegator").value;
			Boolean transactionEnabled = Boolean.parseBoolean(p.properties.get("transactionEnabled").value);
			if (type != null) {
				message.setTransactionEnabled(transactionEnabled);
			}
			int acknowledge = Integer.parseInt(p.properties.get("acknowledge").value);
			if (type != null) {
				message.setAcknowledge(acknowledge);
			}
			String listenerClass = p.properties.get("class").value;
			if (listenerClass != null) {
				try {
					OlbiusMessageListener listener = (OlbiusMessageListener) Class.forName(listenerClass).newInstance();
					listener.setDelegator(DelegatorFactory.getDelegator(delegatorName));
					ActivemqFactory.getInstance().receiveMessage(listener, message);
				} catch (Exception e) {
					Debug.logError(e, module);
				}
			}
		}
		return true;
	}

	@Override
	public void stop() throws ContainerException {
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

}
