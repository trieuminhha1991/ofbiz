package com.olbius.activemq;

/**
 * @author Nguyen Ha
 *
 */
public interface OlbiusMessage {

	int getAcknowledge();

	String getDestination();

	Object getMessage();

	String getType();

	boolean isTransactionEnabled();

	void setAcknowledge(int acknowledge);

	void setDestination(String destination);

	void setMessage(Object message);

	void setTransactionEnabled(boolean b);

	void setType(String type);

}
