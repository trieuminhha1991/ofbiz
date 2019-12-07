package com.olbius.service;

import org.ofbiz.service.ModelService;

import com.olbius.service.callback.ServiceCallback;

@SuppressWarnings("serial")
public class OlbiusModelService extends ModelService {

	private ServiceCallback executeCallback;

	public ServiceCallback getExecuteCallback() {
		return executeCallback;
	}

	public void setExecuteCallback(ServiceCallback executeCallback) {
		this.executeCallback = executeCallback;
	}

}
