package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

public class OlbiusScreen extends AbstractAppliaction {

	@Override
	public String getType() {
		return SCREEN;
	}

	@Override
	public String defaultPermission() {
		return Permission.VIEW;
	}

}
