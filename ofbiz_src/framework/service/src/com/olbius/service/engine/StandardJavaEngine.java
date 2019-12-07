package com.olbius.service.engine;

import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.engine.GenericAsyncEngine;

import com.olbius.service.OlbiusService;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;

/**
 * Standard Java Static Method Service Engine
 */
public final class StandardJavaEngine extends GenericAsyncEngine {

	public static final String module = StandardJavaEngine.class.getName();

	public StandardJavaEngine(ServiceDispatcher dispatcher) {
		super(dispatcher);
	}

	/**
	 * @see org.ofbiz.service.engine.GenericEngine#runSyncIgnore(java.lang.String,
	 *      org.ofbiz.service.ModelService, java.util.Map)
	 */
	@Override
	public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context)
			throws GenericServiceException {
		runSync(localName, modelService, context);
	}

	/**
	 * @see org.ofbiz.service.engine.GenericEngine#runSync(java.lang.String,
	 *      org.ofbiz.service.ModelService, java.util.Map)
	 */
	@Override
	public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context)
			throws GenericServiceException {
		Object result = serviceInvoker(localName, modelService, context);

		if (result == null || !(result instanceof Map<?, ?>)) {
			throw new GenericServiceException("Service [" + modelService.name + "] did not return a Map object");
		}
		return UtilGenerics.checkMap(result);
	}

	// Invoke the java method service.
	private Object serviceInvoker(String localName, ModelService modelService, Map<String, Object> context)
			throws GenericServiceException {

		DispatchContext dctx = dispatcher.getLocalContext(localName);

		if (modelService == null) {
			Debug.logError("ERROR: Null Model Service.", module);
		}
		if (dctx == null) {
			Debug.logError("ERROR: Null DispatchContext.", module);
		}
		if (context == null) {
			Debug.logError("ERROR: Null Service Context.", module);
		}

		Object result = null;

		// check the package and method names
		if (modelService.location == null) {
			throw new GenericServiceException(
					"Service [" + modelService.name + "] is missing location values which are required for execution.");
		}

		// get the classloader to use
		ClassLoader cl = null;

		if (dctx == null) {
			cl = this.getClass().getClassLoader();
		} else {
			cl = dctx.getClassLoader();
		}

		try {
			Class<?> c = cl.loadClass(this.getLocation(modelService));

			if (!OlbiusService.class.isAssignableFrom(c)) {
				throw new GenericServiceException("Service [" + modelService.name
						+ "] location class is not assignable from com.olbius.OlbiusService");
			}

			OlbiusService service = (OlbiusService) c.newInstance();

			result = service.run(dctx, context);

		} catch (ClassNotFoundException cnfe) {
			throw new GenericServiceException("Cannot find service [" + modelService.name + "] location class", cnfe);
		} catch (SecurityException se) {
			throw new GenericServiceException("Service [" + modelService.name + "] Access denied", se);
		} catch (IllegalAccessException iae) {
			throw new GenericServiceException("Service [" + modelService.name + "] Method not accessible", iae);
		} catch (IllegalArgumentException iarge) {
			throw new GenericServiceException("Service [" + modelService.name + "] Invalid parameter match", iarge);
		} catch (NullPointerException npe) {
			throw new GenericServiceException("Service [" + modelService.name + "] ran into an unexpected null object",
					npe);
		} catch (ExceptionInInitializerError eie) {
			throw new GenericServiceException("Service [" + modelService.name + "] Initialization failed", eie);
		} catch (Throwable th) {
			throw new GenericServiceException("Service [" + modelService.name + "] Error or unknown exception", th);
		}

		return result;
	}
}
