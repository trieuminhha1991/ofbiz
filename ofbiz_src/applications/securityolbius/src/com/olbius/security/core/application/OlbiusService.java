package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

public class OlbiusService extends AbstractAppliaction {

	@Override
	public String getType() {
		return SERVICE;
	}

	@Override
	public String defaultPermission() {
		return Permission.INVOKE;
	}

}
