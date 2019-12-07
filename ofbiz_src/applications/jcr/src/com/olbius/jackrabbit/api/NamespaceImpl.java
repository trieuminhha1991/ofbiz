package com.olbius.jackrabbit.api;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class NamespaceImpl implements Namespace {

	private Session session;

	public NamespaceImpl(Session session) {
		this.session = session;
	}

	@Override
	public void registry(String name, String url) throws RepositoryException {
		NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
		registry.registerNamespace(name, url);
		session.save();
	}

	@Override
	public boolean isExist(String name) throws RepositoryException {
		NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
		try {
			registry.getURI(name);
		} catch (NamespaceException e) {
			return false;
		}
		return true;
	}

	@Override
	public void logoutSession() {
		if(session != null) {
			session.logout();
		}
	}
}
