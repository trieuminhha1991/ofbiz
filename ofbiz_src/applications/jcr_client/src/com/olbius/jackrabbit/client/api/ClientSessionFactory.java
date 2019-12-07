package com.olbius.jackrabbit.client.api;

import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import com.olbius.jackrabbit.client.OlbiusProvider;

public interface ClientSessionFactory {

	ClientSession newInstance(String userLoginId, String pwd, String tenantId, String workspace);
	
	ClientSession newSystemInstance(String workspace);

	void setOlbiusProvider(OlbiusProvider provider);
	
	Repository getRepository(String remote) throws RepositoryException;

	ClientSession newInstance(Map<String, ?> context);
}
