package com.olbius.security.core.application;

import java.util.HashMap;
import java.util.Map;

import com.olbius.security.api.Application;

public abstract class AbstractAppliaction implements Application {

	private String id;
	
	private String permission;
	
	private Application module;
	
	private String name;
	
	private String app;
	
	private Map<String, String> overridePermission;
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setDefaultPermission(String permission) {
		this.permission = permission;
	}
	
	@Override
	public String getDefaultPermission() {
		if(permission == null || permission.isEmpty()) {
			return defaultPermission();
		}
		return permission;
	}
	
	@Override
	public void setModule(Application module) {
		this.module = module;
	}
	
	@Override
	public Application getModule() {
		return module;
	}
	
	@Override
	public String getApp() {
		return app;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setApp(String app) {
		this.app = app;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Map<String, String> getOverridePermission() {
		if(overridePermission == null) {
			overridePermission = new HashMap<String, String>();
		}
		return overridePermission;
	}
	
	@Override
	public String getOverridePermission(String permission) {
		return getOverridePermission().get(permission);
	}
	
	@Override
	public void putOverridePermission(String permission, String overridePermission) {
		getOverridePermission().put(permission, overridePermission);
	}
	
	@Override
	public boolean isEmpty() {
		return getId() == null || getId().isEmpty();
	}
	
	public abstract String defaultPermission();
}
