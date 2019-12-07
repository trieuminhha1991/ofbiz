package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

public class OlbiusModule extends AbstractAppliaction {

	@Override
	public String getType() {
		return MODULE;
	}

	@Override
	public String defaultPermission() {
		return Permission.VIEW;
	}

}
