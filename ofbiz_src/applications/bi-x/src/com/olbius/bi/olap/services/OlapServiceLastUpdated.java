package com.olbius.bi.olap.services;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.system.ProcessServices;
import com.olbius.service.OlbiusService;

public class OlapServiceLastUpdated implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {
		Delegator delegator = dctx.getDelegator();
		Timestamp timestamp = null;
		String service = (String) context.get("service");
		try {
			timestamp = ProcessServices.getLastUpdated(delegator, service);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), OlapServiceLastUpdated.class.getName());
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		if (timestamp != null) {
			result.put("lastupdated", timestamp.getTime());
		} else {
			result.put("lastupdated", new Long(0));
		}
		return result;
	}

}
