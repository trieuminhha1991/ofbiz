package com.olbius.bi.olap.services;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.system.ProcessServices;
import com.olbius.service.OlbiusService;

public class OlapServiceStatus implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {
		Delegator delegator = dctx.getDelegator();

		String status = null;

		String service = (String) context.get("service");

		try {
			status = ProcessServices.getStatus(delegator, service);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), OlapServiceStatus.class.getName());
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("status", status);
		return result;
	}

}
