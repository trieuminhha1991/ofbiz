package com.olbius.bi.loader;

import java.io.File;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;

import com.olbius.entity.tenant.OlbiusTenant;

public class BiContairner implements Container {

	public static final String module = BiContairner.class.getName();
	private String CONTAINER_NAME;
	
	public static final boolean DEV = UtilProperties.getPropertyAsBoolean("BiLoader", "dev.mode", true);
	public static final String URL = UtilProperties.getPropertyValue("BiLoader", "remote.url");
	
	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
	}

	@Override
	public boolean start() throws ContainerException {
		
		if(!DEV) {
			
			String tenantId = OlbiusTenant.getTenantId(null);
			
			//HttpSendRequest.send(URL, "clear", new String[] {"tenant="+tenantId});
			
			uploadFolder(tenantId, "applications/bi-x/dimension");
			uploadFolder(tenantId, "applications/bi-x/fact");
			uploadFolder(tenantId, "applications/bi-x/job");
			
		}
		
		return true;
	}

	private void uploadFolder(String tenantId, String path) {
		File dir = new File(path);

		File[] files = null;

		if (dir.isDirectory()) {
			files = dir.listFiles();
		} else {
			files = new File[] {};
		}
		
		for (int i = 0; i < files.length; i++) {
			if (!files[i].getName().startsWith("$")) {
				HttpSendRequest.upload(URL, tenantId+"/"+dir.getName(), files[i].getPath());
			}
		}
	}
	
	@Override
	public void stop() throws ContainerException {
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

}
