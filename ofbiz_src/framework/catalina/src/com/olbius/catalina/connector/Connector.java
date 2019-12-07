package com.olbius.catalina.connector;

/**
 * @author Nguyen Ha
 */
public class Connector extends org.apache.catalina.connector.Connector {

	public Connector() {
		super();
	}
	
	public Connector(String protocol) {
		super(protocol);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.catalina.connector.Connector#createRequest()
	 */
	@Override
	public Request createRequest() {
		Request request = new Request();
		request.setConnector(this);
		return (request);
	}

}
