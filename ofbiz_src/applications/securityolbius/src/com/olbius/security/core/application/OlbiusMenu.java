package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

public class OlbiusMenu extends AbstractAppliaction {

	@Override
	public String getType() {
		return MENU;
	}

	@Override
	public String defaultPermission() {
		return Permission.VIEW;
	}

}
