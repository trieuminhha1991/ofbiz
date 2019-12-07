package com.olbius.jackrabbit.client.api;

public interface ClientNodeFactory {

	ClientNode newInstance(ClientSession session);
	
}
