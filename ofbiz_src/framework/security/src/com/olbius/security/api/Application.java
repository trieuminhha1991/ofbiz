package com.olbius.security.api;

import java.util.Map;

public interface Application {

	public static final String SCREEN = "SCREEN";
	
	public static final String ENTITY = "ENTITY";
	
	public static final String SERVICE = "SERVICE";
	
	public static final String MODULE = "MODULE";
	
	public static final String MENU = "MENU";
	
	public static final String WEBAPP = "WEBAPP";

	void setId(String id);

	String getId();

	String getType();

	void setApp(String app);

	String getApp();

	void setDefaultPermission(String permission);

	String getDefaultPermission();

	void setName(String name);

	String getName();

	void setModule(Application module);

	Application getModule();
	
	void putOverridePermission(String permission, String overridePermission);
	
	String getOverridePermission(String permission);
	
	Map<String, String> getOverridePermission();
	
	boolean isEmpty();
}
