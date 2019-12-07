package com.olbius.jackrabbit.client.core;

import org.ofbiz.base.util.Debug;

import com.olbius.jackrabbit.client.api.ClientNodeSecurity;
import com.olbius.jackrabbit.client.api.ClientNodeSecurityFactory;
import com.olbius.jackrabbit.client.api.ClientSession;

public class OlbiusNodeSecurityFactory implements ClientNodeSecurityFactory{

	@Override
	public ClientNodeSecurity newInstance(ClientSession session) {
		try {
			return new OlbiusNodeSecurity(session);
		} catch (Exception e) {
			Debug.logError(e, OlbiusNodeSecurityFactory.class.getName());;
			return null;
		}
	}

}
