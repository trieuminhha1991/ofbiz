package com.olbius.service;

import java.util.Map;

import org.ofbiz.service.DispatchContext;

public interface OlbiusService {

	Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception;
	
}
