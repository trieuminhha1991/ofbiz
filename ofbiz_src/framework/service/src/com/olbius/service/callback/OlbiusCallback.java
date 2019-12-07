package com.olbius.service.callback;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.service.AbstactService;

public class OlbiusCallback extends ServiceCallback {

	public OlbiusCallback(ModelService model) {
		super(model);
	}

	@Override
	public Map<String, Object> execute(ClassLoader cl, DispatchContext dctx, Map<String, Object> context)
			throws Exception {
		Class<?> c = cl.loadClass(getModel().location);

		AbstactService service = (AbstactService) c.newInstance();

		service.setDispatchContext(dctx);

		return service.run(context);
	}

}