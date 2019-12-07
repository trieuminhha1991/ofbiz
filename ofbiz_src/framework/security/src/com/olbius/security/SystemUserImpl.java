package com.olbius.security;

public class SystemUserImpl implements SystemUser{

	@Override
	public boolean isSystemUser(String user) {
		if(ADMIN.equals(user) || SYSTEM.equals(user)) {
			return true;
		}
		return false;
	}

}
