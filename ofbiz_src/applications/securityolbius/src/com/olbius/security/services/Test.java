package com.olbius.security.services;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.security.api.OlbiusSecurityProvider;
import com.olbius.security.core.OlbiusSecurityProviderImpl;

public class Test {
	
	public static Map<String, Object> test(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		OlbiusSecurityProvider provider = new OlbiusSecurityProviderImpl(ctx.getDelegator());
		provider.clearCache();
		
		String partyId = (String) context.get("partyId");
		
		System.out.println(provider.member(partyId, new Timestamp(System.currentTimeMillis())));
		System.out.println(provider.memberOf(partyId, new Timestamp(System.currentTimeMillis())));
		
		return ServiceUtil.returnSuccess();
	}
	
}
