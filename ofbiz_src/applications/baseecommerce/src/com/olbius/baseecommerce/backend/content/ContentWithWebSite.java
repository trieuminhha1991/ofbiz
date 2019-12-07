package com.olbius.baseecommerce.backend.content;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.baseecommerce.backend.ConfigWebSiteServices;

public class ContentWithWebSite {
	
	public static void create(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Map<String, ?> lookupKeyValue)
			throws GenericServiceException, GenericEntityException {
		GenericValue content = delegator.makeValidValue("Content", lookupKeyValue);
		content.create();
		// link content to WebSite createWebSiteContent
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		dispatcher.runSync("createWebSiteContent", UtilMisc.toMap("webSiteId", webSiteId, "contentId", lookupKeyValue.get("contentId"),
				"webSiteContentTypeId", "CONTENT", "userLogin", userLogin));
	}
	public static void create(Delegator delegator, String webSiteId, Map<String, ?> lookupKeyValue)
			throws GenericEntityException, GenericServiceException {
		GenericValue content = delegator.makeValidValue("Content", lookupKeyValue);
		content.create();
		GenericValue webSiteContent = delegator.makeValidValue("WebSiteContent",
				UtilMisc.toMap("webSiteId", webSiteId, "contentId", lookupKeyValue.get("contentId"),
						"webSiteContentTypeId", "CONTENT", "fromDate", new Timestamp(System.currentTimeMillis())));
		webSiteContent.create();
	}
	public static void create(DispatchContext ctx, GenericValue userLogin, Map<String, ?> lookupKeyValue)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue content = delegator.makeValidValue("Content", lookupKeyValue);
		content.create();
		// link content to WebSite createWebSiteContent
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		dispatcher.runSync("createWebSiteContent", UtilMisc.toMap("webSiteId", webSiteId, "contentId", lookupKeyValue.get("contentId"),
				"webSiteContentTypeId", "CONTENT", "userLogin", userLogin));
	}
}
