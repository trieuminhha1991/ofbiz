package com.olbius.jackrabbit.client.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

public interface ClientNode {

	void setSession(ClientSession session);

	boolean delete(Node node) throws Exception;

	Node createFolder(String path, boolean isCreate) throws Exception;

	Node createFolder(String path) throws Exception;
	
	Node createFile(String fileName, String mimeType, InputStream data) throws Exception;
	
	Node createFile(Node node, String fileName, String mimeType, InputStream data) throws Exception;
	
	Node createFile(String path, String fileName, String mimeType, InputStream data, boolean isCreate) throws Exception;
	
	Node createFileRandomName(String path, String fileName, String mimeType, InputStream data) throws Exception;

	Node rename(Node node, String name) throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException,
			InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException, Exception;

	Node getParent() throws Exception;

	Node move(Node node, String newPath) throws Exception;

	Node copy(Node node, Node parent) throws Exception;

	List<Node> getChilds(Node node) throws Exception;

	Map<String, List<String>> getChildItems(Node node) throws Exception;

}
