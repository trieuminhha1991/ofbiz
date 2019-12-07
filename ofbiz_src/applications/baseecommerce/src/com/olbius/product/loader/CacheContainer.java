package com.olbius.product.loader;

import java.util.Map;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import javolution.util.FastMap;


public class CacheContainer implements Container{
	public static final String module = CacheContainer.class.getName();

	private String CONTAINER_NAME;

	protected GenericValue userLogin;

	protected Delegator delegator = null;

	protected LocalDispatcher dispatcher = null;

	protected String defaultService = null;
	@Override
	public void init(String[] args, String name, String configFile)
			throws ContainerException {
		CONTAINER_NAME = name;
		ContainerConfig.Container cc = ContainerConfig.getContainer(name, configFile);
		if (cc == null) {
            throw new ContainerException("No catalina-container configuration found in container config!");
        }
		String dispatcherName = ContainerConfig.getPropertyValue(cc, "dispatcher-name", "default");
		String userLoginId = ContainerConfig.getPropertyValue(cc, "userLoginId", "system");
		this.delegator = DelegatorFactory.getDelegator(ContainerConfig.getPropertyValue(cc, "delegator-name", "default"));
		this.dispatcher = ServiceContainer.getLocalDispatcher(dispatcherName, this.delegator);
		this.defaultService = ContainerConfig.getPropertyValue(cc, "defaultService", "synchronizeListProduct");
		try {
			this.userLogin = this.delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), true);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
	}

	@Override
	public boolean start() throws ContainerException {
		if(UtilValidate.isNotEmpty(this.userLogin) && UtilValidate.isNotEmpty(this.dispatcher) && UtilValidate.isNotEmpty(this.defaultService)){
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", this.userLogin);
			try {
				dispatcher.runSync(this.defaultService, input);
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void stop() throws ContainerException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}