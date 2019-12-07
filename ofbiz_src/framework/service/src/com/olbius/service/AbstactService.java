package com.olbius.service;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;

public abstract class AbstactService {

	private DispatchContext dispatchContext;

	public DispatchContext getDispatchContext() {
		return dispatchContext;
	}

	public void setDispatchContext(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
	}

	public abstract Map<String, Object> run(Map<String, Object> context) throws GenericServiceException;

}
