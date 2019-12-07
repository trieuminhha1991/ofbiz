package com.olbius.jackrabbit.api;

import javax.jcr.RepositoryException;

public interface Namespace {
	
	public static String OLBIUS_URI = "http://www.olbius.com";
	public static String OLBIUS_PREFIX = "olbius";
	
	public void registry(String name, String url) throws RepositoryException;
	
	public boolean isExist(String name) throws RepositoryException;
	
	public void logoutSession();
}
