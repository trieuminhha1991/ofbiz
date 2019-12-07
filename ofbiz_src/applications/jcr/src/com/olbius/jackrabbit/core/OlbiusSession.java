package com.olbius.jackrabbit.core;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.JackrabbitSession;

import com.olbius.jackrabbit.security.OlbiusUserManager;

public interface OlbiusSession extends JackrabbitSession {
	
	public Node getOlbiusTenantRootNode() throws RepositoryException;

	public OlbiusUserManager getOlbiusUserManager() throws RepositoryException;
	
	public OlbiusUserManager getOlbiusUserManager(String tenant) throws RepositoryException;
	
	public OlbiusNode getOlbiusNode() throws RepositoryException;

	public String getTenantID();
}
