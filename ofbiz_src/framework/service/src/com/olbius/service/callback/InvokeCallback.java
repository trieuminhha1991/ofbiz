package com.olbius.service.callback;

import java.lang.reflect.Method;
import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

public class InvokeCallback extends ServiceCallback {

	public InvokeCallback(ModelService model) {
		super(model);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> execute(ClassLoader cl, DispatchContext dctx, Map<String, Object> context)
			throws Exception {
		Class<?> c = cl.loadClass(getModel().location);
		Method m = c.getMethod(getModel().invoke, DispatchContext.class, Map.class);
		return (Map<String, Object>) m.invoke(c.newInstance(), dctx, context);
	}

}
