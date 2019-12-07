package com.olbius.jcr.servlet;

import javax.jcr.Repository;

import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;

import com.olbius.jackrabbit.loader.JackrabbitOlbiusContainer;

public class OlbiusSimpleWebdavServlet extends SimpleWebdavServlet {
	private static final long serialVersionUID = 1L;
	private Repository repository;
	private DavSessionProvider davSessionProvider;
	@Override
	public Repository getRepository() {
		// TODO Auto-generated method stub
		if (repository == null){
			repository = JackrabbitOlbiusContainer.getRepository();
		}
		return repository;
	}
	 
	@Override
    public synchronized DavSessionProvider getDavSessionProvider() {
        if (davSessionProvider == null) {
            davSessionProvider = new OlbiusDavSessionProvider(getRepository(), getSessionProvider());
        }
        return davSessionProvider;
    }

}
