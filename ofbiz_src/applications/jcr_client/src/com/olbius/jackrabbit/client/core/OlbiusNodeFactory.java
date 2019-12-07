package com.olbius.jackrabbit.client.core;

import com.olbius.jackrabbit.client.api.ClientNode;
import com.olbius.jackrabbit.client.api.ClientNodeFactory;
import com.olbius.jackrabbit.client.api.ClientSession;

public class OlbiusNodeFactory implements ClientNodeFactory {

	@Override
	public ClientNode newInstance(ClientSession session) {
		return new OlbiusNode(session);
	}

}
