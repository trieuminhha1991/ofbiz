package com.olbius.jackrabbit.client.services;

import java.util.Map;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.loader.JackrabbitClientContainer;

import javolution.util.FastMap;

public class JackrabbitServices {

	public static Map<String, Object> serviceBuilder(Map<String, ?> context, Callback callback)
			throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		ClientSession session = null;

		try {
			session = JackrabbitClientContainer.SESSION_FACTORY.newInstance(context);
			callback.execute(context, session, result);
		} catch (Exception e) {
			throw new GenericServiceException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static interface Callback {

		void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception;

	}
}
