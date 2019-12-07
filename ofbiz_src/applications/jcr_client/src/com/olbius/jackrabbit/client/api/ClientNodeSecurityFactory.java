package com.olbius.jackrabbit.client.api;

public interface ClientNodeSecurityFactory {
	
	ClientNodeSecurity newInstance(ClientSession session);
	
}
