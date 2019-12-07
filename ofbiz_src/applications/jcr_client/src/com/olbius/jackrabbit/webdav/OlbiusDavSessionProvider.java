package com.olbius.jackrabbit.webdav;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.simple.DavSessionImpl;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.entity.tenant.OlbiusTenant;
import com.olbius.jackrabbit.client.api.ClientSessionFactory;
import com.olbius.jackrabbit.client.core.OlbiusSession;
import com.olbius.webapp.OlbiusLogin;

public class OlbiusDavSessionProvider implements DavSessionProvider {
	private ClientSessionFactory factory;
	private final SessionProvider sesProvider;
	public static String module = OlbiusDavSessionProvider.class.getName();

	public OlbiusDavSessionProvider(ClientSessionFactory factory, SessionProvider sesProvider) {
		this.factory = factory;
		this.sesProvider = sesProvider;
	}

	@Override
	public boolean attachSession(WebdavRequest request) throws DavException {
		GenericValue userLogin = null;
		try {
			userLogin = OlbiusLogin.checkExternalLoginKey(request);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		try {
			String workspaceName = request.getRequestLocator().getWorkspaceName();
			if (workspaceName != null && "".equals(workspaceName)) {
				workspaceName = OlbiusSession.WORKSPACE_DEFAULT;
			}
			Session repSession;
			if (OlbiusSession.WORKSPACE_DEFAULT.equals(workspaceName) || userLogin == null) {
				repSession = factory.newInstance(null, null, null, workspaceName).getJcrSession("server");
			} else {
				String tenantId;
				tenantId = OlbiusTenant.getTenantId(userLogin.getDelegator());
				String userName = userLogin.getString("userLoginId");
				String pwd = userLogin.getString("currentPassword");
				repSession = factory.newInstance(userName, pwd, tenantId, workspaceName).getJcrSession("server");
			}
			if (repSession == null) {
				Debug.logError("Could not to retrieve a repository session.", module);
				return false;
			}

			DavSession ds = new DavSessionImpl(repSession);
			Debug.log("Attaching session '" + ds + "' to request '" + request + "'");
			request.setDavSession(ds);
			return true;
		} catch (LoginException e) {
			throw new DavException(HttpServletResponse.SC_NOT_FOUND);
		} catch (RepositoryException e) {
			Debug.logError(e.getMessage(), module);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
		return false;
	}

	@Override
	public void releaseSession(WebdavRequest request) {
		DavSession ds = request.getDavSession();
		try {
			if (ds != null && ds instanceof DavSessionImpl) {
				Session repSession = ((DavSessionImpl) ds).getRepositorySession();
				for (String lockToken : repSession.getWorkspace().getLockManager().getLockTokens()) {
					repSession.getWorkspace().getLockManager().removeLockToken(lockToken);
				}
				sesProvider.releaseSession(repSession);
				Debug.logVerbose("Releasing session '" + ds + "' from request '" + request + "'", module);
			}
		} catch (RepositoryException e) {
			Debug.logError(e, module);
		}
		request.setDavSession(null);
	}

}
