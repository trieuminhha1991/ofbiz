package com.olbius.security.core.application;

import com.olbius.security.api.Application;
import com.olbius.security.api.Permission;

public class OlbiusPartyPermisision {

	private Application application;
	private String partyId;
	private Permission permission;

	public OlbiusPartyPermisision() {
	}

	public OlbiusPartyPermisision(String partyId, Application application) {
		this.application = application;
		this.partyId = partyId;
	}

	public Application getApplication() {
		return application;
	}

	public String getPartyId() {
		return partyId;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public boolean isEmpty() {
		return application == null || partyId == null || application.isEmpty() || partyId.isEmpty();
	}
	
}
