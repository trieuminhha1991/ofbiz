package com.olbius.service.callback;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

public abstract class ServiceCallback {

	private final ModelService model;

	public ServiceCallback(ModelService model) {
		this.model = model;
	}

	public ModelService getModel() {
		return model;
	}

	public abstract Map<String, Object> execute(ClassLoader cl, DispatchContext dctx, Map<String, Object> context)
			throws Exception;

}