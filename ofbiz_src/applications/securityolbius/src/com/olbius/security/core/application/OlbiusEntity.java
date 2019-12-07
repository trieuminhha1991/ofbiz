package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

public class OlbiusEntity extends AbstractAppliaction{

	@Override
	public String getType() {
		return ENTITY;
	}

	@Override
	public String defaultPermission() {
		return Permission.VIEW;
	}

}
