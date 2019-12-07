package com.olbius.administration.backup;

import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class BackupServices {
	public static Map<String, Object> mcsBackup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
}
