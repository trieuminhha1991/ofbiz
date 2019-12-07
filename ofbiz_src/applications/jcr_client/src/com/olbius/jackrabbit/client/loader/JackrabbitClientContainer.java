package com.olbius.jackrabbit.client.loader;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.start.Start;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import com.olbius.jackrabbit.client.OlbiusProvider;
import com.olbius.jackrabbit.client.core.OlbiusSessionFactory;

public class JackrabbitClientContainer implements Container {

	public static final String module = JackrabbitClientContainer.class.getName();
	private String CONTAINER_NAME;

	public static final OlbiusSessionFactory SESSION_FACTORY = new OlbiusSessionFactory();

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
	}

	@Override
	public boolean start() throws ContainerException {
		
		if(Start.getInstance().isInstall()) {
			return true;
		}
		
		String admin = UtilProperties.getPropertyValue("jcr_client", "jcr.user");
		String pwd = UtilProperties.getPropertyValue("jcr_client", "jcr.password");
		boolean dev = false;
		try {
			dev = OlbiusProvider.dev(admin);

			if (dev) {
				pwd = admin;
			}

			SESSION_FACTORY.setAdmin(admin);
			SESSION_FACTORY.setPassword(pwd);
			SESSION_FACTORY.setOlbiusProvider(OlbiusProvider.getInstance());

			Delegator delegator = DelegatorFactory.getDelegator("default");

			GenericValue value = delegator.findOne("JobSandbox", UtilMisc.toMap("jobId", "jackrabbitRegistry"), false);

			if (value == null) {

				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);

				LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
				
				dispatcher.runAsync("entityImport", UtilMisc.toMap("userLogin", userLogin, "filename",
						"applications/jcr_client/config/job/initi.xml"));
			}

		} catch (Exception e) {
			Debug.logError(e, module);
			return false;
		}

		return true;
	}

	@Override
	public void stop() throws ContainerException {
		OlbiusProvider.destroy();
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

}
