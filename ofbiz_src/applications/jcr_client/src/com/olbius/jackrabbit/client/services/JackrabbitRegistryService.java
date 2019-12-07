package com.olbius.jackrabbit.client.services;

import java.util.Map;
import java.util.Properties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.service.OlbiusService;

public class JackrabbitRegistryService implements OlbiusService {

	public final static String module = JackrabbitRegistryService.class.getName();

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		initPrivate(dctx.getDispatcher(), (GenericValue) context.get("userLogin"));

		return ServiceUtil.returnSuccess();
	}

	private void initPrivate(LocalDispatcher dispatcher, GenericValue userLogin) throws Exception {

		Properties properties = UtilProperties.getProperties("jcr_folder");

		for (Object obj : properties.keySet()) {
			String s = (String) obj;
			String permissions = properties.getProperty(s).trim();
			dispatcher.runSync("jackrabbitRegistryFolder",
					UtilMisc.toMap("userLogin", userLogin, "folder", s, "permissions", permissions));
		}
	}

}
