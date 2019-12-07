package com.olbius.jackrabbit.core;

import javax.jcr.AccessDeniedException;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.security.authentication.AuthContext;

import com.olbius.jackrabbit.security.OlbiusUserManager;

public class OlbiusRepository extends RepositoryImpl{

	public static final String WSP_SECURITY = "security";
	public static final String WSP_DEFAULT = "default";
	
	protected OlbiusRepository(RepositoryConfig repConfig) throws RepositoryException {
		super(repConfig);
	}

	public static OlbiusRepository create(RepositoryConfig config) throws RepositoryException {
		return new OlbiusRepository(config);
	}
	
	@Override
	protected SessionImpl createSessionInstance(AuthContext loginContext, WorkspaceConfig wspConfig) throws AccessDeniedException,
			RepositoryException {
		return new OlbiusSessionImpl(context, loginContext, wspConfig);
	}

	@Override
	protected SessionImpl createSessionInstance(Subject subject, WorkspaceConfig wspConfig) throws AccessDeniedException, RepositoryException {
		return new OlbiusSessionImpl(context, subject, wspConfig);
	}
	
	public Session login(String user, String pwd, String tenantId, String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
		if(tenantId == null || tenantId.isEmpty()) {
			tenantId = Constant.getTenantDefault();
		}
		Session session = super.login(new SimpleCredentials(tenantId+OlbiusUserManager.USER+user, pwd.toCharArray()), workspaceName);
//		((OlbiusSessionImpl) session).setTenantID(tenantId);
		return session;
	}
	
	public Session login(String user, String pwd, String tenantId) throws LoginException, NoSuchWorkspaceException, RepositoryException {
		return login(user, pwd, tenantId, WSP_DEFAULT);
	}
	
	public static String swapWorkSpace(String curWsp) {
		if(curWsp.equals(WSP_DEFAULT)) {
			return WSP_SECURITY;
		}
		if(curWsp.equals(WSP_SECURITY)) {
			return WSP_DEFAULT;
		}
		return null;
	}
}
