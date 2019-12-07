package com.olbius.docmanager.helper;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.jackrabbit.core.Constant;

public class DocumentHelper {
	public static String getPath(String dataResourceId, boolean dms, DispatchContext ctx) throws GenericEntityException {
		
		Delegator delegator = ctx.getDelegator();
		
		GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
		String path = dataResource.getString("objectInfo");
		path = path.substring(34);
		
		String tenant = ctx.getDelegator().getDelegatorTenantId();

		if (tenant == null) {
			tenant = Constant.getTenantDefault();
		}

		if (dms) {
			path = path.replaceFirst("/" + tenant, "");
			if (path.equals("")) {
				return "/";
			}
			return path;
		}

		if (path.startsWith("/")) {
			return "/" + tenant + path;
		} else {
			return "/" + tenant + "/" + path;
		}
	}
}
