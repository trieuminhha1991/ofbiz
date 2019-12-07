package com.olbius.service.permission;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelPermission;

import com.olbius.security.util.SecurityUtil;

@SuppressWarnings("serial")
public class ModelOlbiusPermission extends ModelPermission{

	private String type;
	private String app;
	
	public ModelOlbiusPermission(String type, String app) {
		this.type = type;
		this.app = app;
	}
	
	@Override
	public boolean evalPermission(DispatchContext dctx, Map<String, ? extends Object> context) {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            Debug.logInfo("Secure service requested with no userLogin object", module);
            return false;
        }
		return SecurityUtil.getOlbiusSecurity(dctx.getSecurity()).olbiusHasPermission(userLogin, nameOrRole, type, app);
	}
	
}
