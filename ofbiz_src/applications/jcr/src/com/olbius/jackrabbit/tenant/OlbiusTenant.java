package com.olbius.jackrabbit.tenant;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface OlbiusTenant {

	public String getTenantID();

	public Session getSession();

	public void createTenant() throws RepositoryException;

	public void createWebAppFolder(Map<String, Set<String>> webapp) throws RepositoryException;

	public void createGroup(Map<String, Set<String>> mapGroup) throws RepositoryException;

	public void createPermission(Map<String, Set<String>> mapPermission) throws RepositoryException;

	public void setAdminPermission(Set<String> admin) throws RepositoryException;

	void createUser(Map<String, Map<String, String>> mapUser) throws RepositoryException;

	void createParty(List<String> parties) throws RepositoryException;

	void createPartyRelationship(Map<String, Map<String, String>> partyRelationship) throws RepositoryException;

}
