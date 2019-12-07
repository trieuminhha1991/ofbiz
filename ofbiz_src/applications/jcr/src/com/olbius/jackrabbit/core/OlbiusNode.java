package com.olbius.jackrabbit.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.JackrabbitNode;

public interface OlbiusNode extends JackrabbitNode {
	
	public Session getSession() throws RepositoryException;
	
	public Node getNode();

	public void setNode(Node node);
	
	public Node addFolder(String name) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException;
	
	public Node addFile(String name, String mimeType, InputStream data, boolean version) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException, IOException;
	
	public Node addFile(String name, String mimeType, InputStream data, String nodeType, Map<String, String> properties, boolean version) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException, IOException, ParseException;
	
	public boolean isFile() throws RepositoryException;
	
	public void remove() throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, RepositoryException;
	
	public List<Node> getNodes() throws RepositoryException;
	
	public List<Node> getNodes(boolean isSystem) throws RepositoryException;
}
