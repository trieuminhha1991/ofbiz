package com.olbius.jackrabbit.core;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.RepositoryContext;
import org.apache.jackrabbit.core.XASessionImpl;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.security.authentication.AuthContext;

import com.olbius.jackrabbit.security.OlbiusUserManager;
import com.olbius.jackrabbit.security.OlbiusUserManagerImpl;

public class OlbiusSessionImpl extends XASessionImpl implements OlbiusSession{

	private OlbiusUserManager userManager;
	private OlbiusNode olbiusNode;
	
	protected OlbiusSessionImpl(RepositoryContext repositoryContext, AuthContext loginContext, WorkspaceConfig wspConfig)
			throws AccessDeniedException, RepositoryException {
		super(repositoryContext, loginContext, wspConfig);
	}

	protected OlbiusSessionImpl(RepositoryContext repositoryContext, Subject subject, WorkspaceConfig wspConfig)
			throws AccessDeniedException, RepositoryException {
		super(repositoryContext, subject, wspConfig);
	}

	@Override
	public Node getOlbiusTenantRootNode() throws RepositoryException {
		return getNode("/"+this.getUserID().substring(0, this.getUserID().indexOf("#")));
	}

	@Override
	public OlbiusUserManager getOlbiusUserManager() throws RepositoryException {
		if(userManager == null) {
			userManager = new OlbiusUserManagerImpl(this);
		}
		return userManager;
	}
	
	@Override
	public OlbiusUserManager getOlbiusUserManager(String tenant) throws RepositoryException {
		if(userManager == null) {
			userManager = new OlbiusUserManagerImpl(this, tenant);
		}
		return userManager;
	}

	@Override
	public OlbiusNode getOlbiusNode() throws RepositoryException {
		if(olbiusNode ==null) {
			olbiusNode = new OlbiusNodeImpl(this.getRootNode());
		}
		return olbiusNode;
	}

	@Override
	public String getTenantID() {
		return this.getUserID().substring(0, this.getUserID().indexOf("#"));
	}
}
