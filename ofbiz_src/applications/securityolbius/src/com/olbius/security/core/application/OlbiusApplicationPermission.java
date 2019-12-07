package com.olbius.security.core.application;

import com.olbius.security.api.Application;

public class OlbiusApplicationPermission {

	private Application application;
	private String permission;
	
	public OlbiusApplicationPermission() {
	}
	
	public OlbiusApplicationPermission(Application application, String permision) {
		this.application = application;
		this.permission = permision;
	}
	
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permision) {
		this.permission = permision;
	}
}
