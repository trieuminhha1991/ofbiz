package com.olbius.activemq.listener;

import javax.jms.MessageListener;

import org.ofbiz.entity.Delegator;

public interface OlbiusMessageListener extends MessageListener{
	
	void setDelegator(Delegator delegator);
	
}
