package com.olbius.jackrabbit.client.api;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import com.olbius.jackrabbit.client.OlbiusProvider;
import com.olbius.jcr.security.principal.OlbiusPrincipal;

public interface ClientSession {

	void setTenantId(String tenantId);
	
	void setUserLoginId(String userLoginId);

	void setPassword(String pwd);

	void setWorkspace(String workspace);

	String getUserLoginId();

	String getPassword();

	String getWorkspace();
	
	String getTenantId();
	
	Node getNode() throws Exception;
	
	Node getNode(String path) throws Exception;

	void setPath(String path) throws Exception;
	
	Session getJcrSession(String remote) throws Exception;
	
	void logout();
	
	void setSystem(boolean b);
	
	boolean isSystem();
	
	ClientNode getClientNode();
	
	ClientNodeSecurity getNodeSecurity();
	
	boolean isRoot();
	
	boolean save(String remote) throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException;
	
	void setSave(boolean b);

	OlbiusPrincipal getPrincipal();
	
	void setSessionFactory(ClientSessionFactory factory);
	
	ClientSessionFactory getSessionFactory();
	
	String getFullPath(Node node) throws RepositoryException;
	
	void setOlbiusProvider(OlbiusProvider provider);

	Repository getRepository(String remote) throws RepositoryException;
	
}
