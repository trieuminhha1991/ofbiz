package com.olbius.activemq;

import javax.jms.MessageListener;

/**
 * @author Nguyen Ha
 *
 */
public interface Activemq {

	public final static String module = Activemq.class.getName();
	
	public final static String TOPIC = "topic";
	
	public final static String QUEUE = "queue";
	
	public final static String EVENT = "event";
	
	void setMaxConnections(int connection);
	
	void sendMessage(OlbiusMessage message);
	
	void sendMessageTopic(OlbiusMessage message);
	
	void sendMessageQueue(OlbiusMessage message);
	
	void receiveMessage(MessageListener listener, OlbiusMessage message);
	
	void receiveMessageTopic(MessageListener listener, OlbiusMessage message);
	
	void receiveMessageQueue(MessageListener listener, OlbiusMessage message);
}
