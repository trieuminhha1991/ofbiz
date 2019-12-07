package com.olbius.jackrabbit.webdav;

import java.io.IOException;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.ofbiz.base.util.Debug;

import com.olbius.jackrabbit.client.loader.JackrabbitClientContainer;

@SuppressWarnings("serial")
public class OlbiusSimpleWebdavServlet extends SimpleWebdavServlet {

	private DavSessionProvider davSessionProvider;

	@Override
	public Repository getRepository() {
		try {
			return JackrabbitClientContainer.SESSION_FACTORY.getRepository("server");
		} catch (RepositoryException e) {
			Debug.logError(e, OlbiusSimpleWebdavServlet.class.getName());
			return null;
		}
	}

	@Override
	public synchronized DavSessionProvider getDavSessionProvider() {
		if (davSessionProvider == null) {
			davSessionProvider = new OlbiusDavSessionProvider(JackrabbitClientContainer.SESSION_FACTORY, getSessionProvider());
		}
		return davSessionProvider;
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			super.service(request, response);
		} catch(Exception e) {
			response.sendError(DavServletResponse.SC_NOT_FOUND);
		}
	}
}
