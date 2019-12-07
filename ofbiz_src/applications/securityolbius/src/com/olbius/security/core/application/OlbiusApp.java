package com.olbius.security.core.application;

import java.util.Map;

import com.olbius.security.api.Application;

public class OlbiusApp implements Application{

	@Override
	public void setId(String id) {
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public void setApp(String app) {
	}

	@Override
	public String getApp() {
		return null;
	}

	@Override
	public void setDefaultPermission(String permission) {
	}

	@Override
	public String getDefaultPermission() {
		return null;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setModule(Application module) {
	}

	@Override
	public Application getModule() {
		return null;
	}

	@Override
	public void putOverridePermission(String permission, String overridePermission) {
	}

	@Override
	public String getOverridePermission(String permission) {
		return null;
	}

	@Override
	public Map<String, String> getOverridePermission() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

}
