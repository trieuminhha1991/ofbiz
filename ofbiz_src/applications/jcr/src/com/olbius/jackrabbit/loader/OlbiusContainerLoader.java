package com.olbius.jackrabbit.loader;

import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;


public class OlbiusContainerLoader extends OlbiusContainer implements Container{

	public static final String module = OlbiusContainerLoader.class.getName();
	
	private ContainerConfig.Container cfg;
	private String CONTAINER_NAME;
	
	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		cfg = ContainerConfig.getContainer(name, configFile);
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
		addComponent();
	}

	@Override
	public boolean start() throws ContainerException {
		return true;
	}

	@Override
	public void stop() throws ContainerException {
		
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

	@Override
	public void addComponent() throws ContainerException {
		String permission = cfg.getProperty("permission").value.trim();
		String component = cfg.getProperty("component").value.trim();
		String[] permissions = permission.split(",");
		String[] components = component.split(",");
		if(permissions.length == components.length) {
			for(int i = 0; i < permissions.length; i++) {
				Set<String> tmp = getMapComponent().get(components[i]);
				if(tmp == null) {
					tmp = new FastSet<String>();
					getMapComponent().put(components[i], tmp);
				}
				tmp.add(permissions[i]);
			}
		} else {
			throw new ContainerException("ERROR: permissions length != components length");
		}
	}
}
