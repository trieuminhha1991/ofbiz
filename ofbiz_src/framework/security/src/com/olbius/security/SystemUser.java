package com.olbius.security;

public interface SystemUser {
	/*
	 * User is admin
	 */
	public static final String ADMIN = "admin";
	/*
	 * User is system
	 */
	public static final String SYSTEM = "system";
	
	public boolean isSystemUser(String user);
}
