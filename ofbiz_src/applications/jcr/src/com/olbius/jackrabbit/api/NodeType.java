package com.olbius.jackrabbit.api;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

public interface NodeType {

	public void registerNodeType(String name, String[] heritates, Map<String, String> properties) throws RepositoryException;
	
	public void addPropertyNodeType(String name, String propertyName, String propertyType) throws RepositoryException;

	public void removePropertyNodeType(String name, String propertyName) throws RepositoryException;

	public Map<String, String> getProperties(String name) throws RepositoryException;
	
	public Map<String, String> getProperties(String name, String namespace) throws RepositoryException;
	
	public List<String> getNodeTypes(String namespace) throws RepositoryException;
	
	public List<String> getNodeTypes() throws RepositoryException;
	
	public void unRegisterNodeType(String name) throws RepositoryException;
	
	public void logoutSession();
}
