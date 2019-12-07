package com.olbius.jackrabbit.client.core;

import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.ofbiz.entity.GenericValue;

import com.olbius.entity.tenant.OlbiusTenant;
import com.olbius.jackrabbit.client.OlbiusProvider;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.api.ClientSessionFactory;

public class OlbiusSessionFactory implements ClientSessionFactory {

	private String admin;
	private String pwd;
	private OlbiusProvider provider;

	@Override
	public ClientSession newInstance(String userLoginId, String pwd, String tenantId, String workspace) {
		ClientSession session = new OlbiusSession(userLoginId, pwd, tenantId, workspace);
		session.setOlbiusProvider(provider);
		session.setSessionFactory(this);
		return session;
	}

	@Override
	public ClientSession newSystemInstance(String workspace) {
		ClientSession session = new OlbiusSession(admin, pwd, null, workspace);
		session.setSystem(true);
		session.setSave(true);
		session.setOlbiusProvider(provider);
		session.setSessionFactory(this);
		return session;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public void setPassword(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public void setOlbiusProvider(OlbiusProvider provider) {
		this.provider = provider;
	}

	@Override
	public ClientSession newInstance(Map<String, ?> context) {

		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String wsp = OlbiusSession.WORKSPACE_DEFAULT;

		String isPublic = (String) context.get("public");

		if ("N".equals(isPublic)) {
			wsp = OlbiusSession.WORKSPACE_SECURITY;
		}

		ClientSession session;
		
		session = newInstance(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"),
				OlbiusTenant.getTenantId(userLogin.getDelegator()), wsp);
		
		session.setSave(true);

		return session;
	}

	@Override
	public Repository getRepository(String remote) throws RepositoryException {
		return provider.getRepository(remote);
	}

}
