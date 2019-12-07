package com.olbius.jackrabbit.client.api;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;

public interface ClientNodePermission {

	public static final String ADMIN = "ADMIN";

	public static final String VIEW = "VIEW";

	public static final String DELETE = "DELETE";

	public static final String UPDATE = "UPDATE";

	public static final String CREATE = "CREATE";

	String[] permission(String name);

	String[] permission(ClientNodeSecurity security, Privilege[] privileges) throws AccessControlException, RepositoryException;

}
