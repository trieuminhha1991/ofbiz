package com.olbius.jcr.servlet;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.simple.DavSessionImpl;
import org.ofbiz.base.util.Debug;

//Let only anonymous user to access the repository via webdav

public class AnonymousOnlyDavSessionProvider implements DavSessionProvider {
	private final Repository repository;
	public static String module = AnonymousOnlyDavSessionProvider.class.getName();
	public AnonymousOnlyDavSessionProvider(Repository rep) {
        this.repository = rep;
    }

	@Override
	public boolean attachSession(WebdavRequest request) throws DavException {
		try {
			// retrieve the workspace name
	        String workspaceName = request.getRequestLocator().getWorkspaceName();
	        // empty workspaceName rather means default -> must be 'null'
	        if (workspaceName != null && "".equals(workspaceName)) {
	            workspaceName = null;
	        }
	        // login to repository by anonymous
	        Session repSession;
			repSession = repository.login(workspaceName);
	        if (repSession == null) {
	            Debug.logError("Could not to retrieve a repository session.",module);
	            return false;
	        }
	        
	        DavSession ds = new DavSessionImpl(repSession);
            Debug.log("Attaching session '"+ ds + "' to request '" + request + "'");
            request.setDavSession(ds);
            return true;
		} catch (LoginException e) {
			Debug.logError(e.getMessage(), module);
		} catch (RepositoryException e) {
			Debug.logError(e.getMessage(), module);
		}
		return false;
	}

	@Override
	public void releaseSession(WebdavRequest request) {
        request.setDavSession(null);
	}

}
