package com.olbius.activemq;

import javax.jms.Session;

/**
 * @author Nguyen Ha
 *
 */
public class TextMessage implements OlbiusMessage {

	private boolean transactionEnabled;
	private int acknowledge;
	private String destination;
	private String type;
	private String message;
	
	public TextMessage() {
		transactionEnabled = false;
		acknowledge = Session.AUTO_ACKNOWLEDGE;
		type = Activemq.QUEUE;
		destination = Activemq.EVENT;
	}
	
	@Override
	public int getAcknowledge() {
		return acknowledge;
	}

	@Override
	public String getDestination() {
		return destination;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}

	@Override
	public void setAcknowledge(int acknowledge) {
		this.acknowledge = acknowledge;
	}

	@Override
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public void setMessage(Object message) {
		if (message instanceof String) {
			this.message = (String) message;
		}
	}

	@Override
	public void setTransactionEnabled(boolean b) {
		this.transactionEnabled = b;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

}
