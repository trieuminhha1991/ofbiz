package com.olbius.security.api;

import java.sql.Timestamp;
import java.util.List;

public interface Permission {

	public static final String VIEW = "VIEW";
	
	public static final String CREATE = "CREATE";
	
	public static final String UPDATE = "UPDATE";
	
	public static final String DELETE = "DELETE";
	
	public static final String ADMIN = "ADMIN";

	public static final String INVOKE = "INVOKE";
	
	String getName();
	
	void setName(String name);
	
	List<String> getPermissionInclude();
	
	void setPermissionInclude(List<String> permissionInclude);

	Timestamp getFromDate();

	void setFromDate(Timestamp fromDate);

	Timestamp getThruDate();

	void setThruDate(Timestamp thruDate);

	boolean isAllow();

	void setAllow(boolean allow);
	
	boolean isTime(Timestamp timestamp);
	
	boolean isInclude(String perm);
	
}
