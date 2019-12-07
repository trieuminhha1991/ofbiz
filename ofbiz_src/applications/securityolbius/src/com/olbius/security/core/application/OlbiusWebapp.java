package com.olbius.security.core.application;

import com.olbius.security.api.Permission;

/**
 * @author Nguyen Ha
 */
public class OlbiusWebapp extends AbstractAppliaction{

	/* (non-Javadoc)
	 * @see com.olbius.security.api.Application#getType()
	 */
	@Override
	public String getType() {
		return WEBAPP;
	}

	/* (non-Javadoc)
	 * @see com.olbius.security.core.application.AbstractAppliaction#defaultPermission()
	 */
	@Override
	public String defaultPermission() {
		return Permission.VIEW;
	}

}
