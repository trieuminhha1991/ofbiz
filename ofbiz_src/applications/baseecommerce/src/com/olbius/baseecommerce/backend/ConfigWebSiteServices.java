package com.olbius.baseecommerce.backend;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastList;

import com.olbius.baseecommerce.backend.content.ContentWithWebSite;

public class ConfigWebSiteServices {
	public static String RESOURCE_PROPERTIES = "ecommerce.properties";
	public static final String PARTY_ATTRIBUTE_WEBSITEID = "party.attribute.webSiteId";
	
	public static List<GenericValue> availableWebsites(Delegator delegator) {
		List<GenericValue> webSites = FastList.newInstance();
		try {
			webSites = delegator.findList("WebSite",
					null, UtilMisc.toSet("webSiteId", "siteName"), UtilMisc.toList("siteName"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return webSites;
	}
	public static Map<String, Object> activeWebsite(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = userLogin.getString("partyId");
			String webSiteId = (String) context.get("webSiteId");
			String attrName = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, PARTY_ATTRIBUTE_WEBSITEID, "", delegator);
			Map<String, Object> mapPartyAttribute = UtilMisc.toMap("partyId", partyId, "attrName", attrName);
			GenericValue partyAttribute = delegator.findOne("PartyAttribute", mapPartyAttribute, false);
			mapPartyAttribute.put("userLogin", userLogin);
			mapPartyAttribute.put("attrValue", webSiteId);
			if (UtilValidate.isEmpty(partyAttribute)) {
				//	createPartyAttribute
				result = dispatcher.runSync("createPartyAttribute", mapPartyAttribute);
			} else {
				//	updatePartyAttribute
				result = dispatcher.runSync("updatePartyAttribute", mapPartyAttribute);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static String getCurrentWebSite(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		String partyId = userLogin.getString("partyId");
		String webSiteId = "";
		String attrName = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, PARTY_ATTRIBUTE_WEBSITEID, "", delegator);
		GenericValue partyAttribute = delegator.findOne("PartyAttribute",
				UtilMisc.toMap("partyId", partyId, "attrName", attrName), false);
		if (UtilValidate.isNotEmpty(partyAttribute)) {
			webSiteId = partyAttribute.getString("attrValue");
		}
		if (UtilValidate.isEmpty(webSiteId)) {
			List<GenericValue> webSites = delegator.findList("WebSite",
					EntityCondition.makeCondition("isDefault", "Y"), null, null, null, true);
			if (UtilValidate.isNotEmpty(webSites)) {
				webSiteId = EntityUtil.getFirst(webSites).getString("webSiteId");
			}
		}
		return webSiteId;
	}
	
	public static GenericValue footer(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		GenericValue footer = new GenericValue();
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteId", webSiteId, "webSiteContentTypeId", "FOOTER_CONFIG")));
		List<GenericValue> footers = delegator.findList("WebSiteContentDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "longDescription"), null, null, false);
		if (UtilValidate.isNotEmpty(footers)) {
			footer = EntityUtil.getFirst(footers);
		}
		return footer;
	}
	public static Map<String, Object> updateFooter(DispatchContext ctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		Delegator delegator = ctx.getDelegator();
		try {
			String editor = (String) context.get("editor");
			delegator.storeByCondition("Content", UtilMisc.toMap("longDescription", editor),
					EntityCondition.makeCondition("contentId", context.get("contentId")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnFailure(UtilProperties.getMessage("WidgetUiLabels", "wgupdateerror", locale));
		}
		return result;
	}
	
	public static GenericValue store(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		GenericValue store = new GenericValue();
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteId", webSiteId, "webSiteContentTypeId", "STORES_CONFIG")));
		List<GenericValue> stores = delegator.findList("WebSiteContentDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "longDescription"), null, null, false);
		if (UtilValidate.isNotEmpty(stores)) {
			store = EntityUtil.getFirst(stores);
		}
		return store;
	}
	public static Map<String, Object> updateStores(DispatchContext ctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		Delegator delegator = ctx.getDelegator();
		try {
			String editor = (String) context.get("editor");
			delegator.storeByCondition("Content", UtilMisc.toMap("longDescription", editor),
					EntityCondition.makeCondition("contentId", context.get("contentId")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnFailure(UtilProperties.getMessage("WidgetUiLabels", "wgupdateerror", locale));
		}
		return result;
	}
	
	public static GenericValue header(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		GenericValue header = new GenericValue();
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteId", webSiteId, "webSiteContentTypeId", "HEADER_CONFIG")));
		List<GenericValue> headers = delegator.findList("WebSiteContentDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "longDescription"), null, null, false);
		if (UtilValidate.isNotEmpty(headers)) {
			header = EntityUtil.getFirst(headers);
		}
		return header;
	}
	public static Map<String, Object> updateHeader(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String longDescription = (String) context.get("longDescription");
			delegator.storeByCondition("Content", UtilMisc.toMap("longDescription", longDescription),
					EntityCondition.makeCondition("contentId", context.get("contentId")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnFailure();
		}
		return result;
	}
	
	public static Map<String, Object> mainSlide(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String type = (String) context.get("type");
		List<GenericValue> listSlide = FastList.newInstance();
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		if ("preview".equals(type)) {
			listSlide = ContentUtils.mainSlide(delegator, webSiteId, false);
		} else {
			listSlide = ContentUtils.mainSlide(delegator, webSiteId, true);
		}
		result.put("listSlide", listSlide);
		return result;
	}
	public static Map<String, Object> addImageToMainSlide(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String url = (String) context.get("url");
			String contentId = "SLIDE_ITEM" + delegator.getNextSeqId("Content");
			//	create Content
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "SLIDE", "contentName", "Main slide of website",
							"author", userLoginId,
							"url", url, "originalImageUrl", originalImageUrl, "statusId", "CTNT_PUBLISHED",
							"createdDate", new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
			//	createContentAssoc
			GenericValue contentAssoc = delegator.makeValidValue("ContentAssoc",
				UtilMisc.toMap("contentIdTo", "MAIN_SLIDE", "contentId", contentId, "contentAssocTypeId", "LIST_ENTRY",
						"fromDate", new Timestamp(System.currentTimeMillis())));
			delegator.create(contentAssoc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> verticalBanners(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		result.put("listBanners", ContentUtils.verticalBanners(delegator, webSiteId, true));
		return result;
	}
	public static Map<String, Object> addVerticalBanner(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String url = (String) context.get("url");
			String contentId = "VTCB_" + delegator.getNextSeqId("Content");
			//	create Content
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "VERTICALBANNER", "contentName", "Vertical Banner",
							"author", userLoginId,
							"url", url, "originalImageUrl", originalImageUrl, "statusId", "CTNT_PUBLISHED",
							"createdDate", new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> horizontalBanners(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		result.put("listBanners", ContentUtils.horizontalBanners(delegator, webSiteId, true));
		return result;
	}
	public static Map<String, Object> addHorizontalBanner(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String url = (String) context.get("url");
			String contentId = "HZTB_" + delegator.getNextSeqId("Content");
			//	create Content
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "HORIZONTALBANNER", "contentName", "Horizontal Banner",
							"author", userLoginId,
							"url", url, "originalImageUrl", originalImageUrl, "statusId", "CTNT_PUBLISHED",
							"createdDate", new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> partnerBanners(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		result.put("listBanners", ContentUtils.partnerBanners(delegator, webSiteId, true));
		return result;
	}
	public static Map<String, Object> addPartnerBanner(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String url = (String) context.get("url");
			String contentId = "PRNB_" + delegator.getNextSeqId("Content");
			//	create Content
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "PARTNERBANNER", "contentName", "Horizontal Banner",
							"author", userLoginId,
							"url", url, "originalImageUrl", originalImageUrl, "statusId", "CTNT_PUBLISHED",
							"createdDate", new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listWebsites(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			listIterator = delegator.find("WebSite", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String, Object> loadBackGround(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		Map<String, Object> webSiteBackGround = ContentUtils.webSiteBackGround(delegator, webSiteId, true);
		result.put("webSiteBackGround", webSiteBackGround);
		return result;
	}
	
	public static Map<String, Object> configBackGround(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			storeBackGround(delegator, "HEADER_BACKGROUND01", (String) context.get("headerImage_s"), (String) context.get("headerImage"));
			storeBackGround(delegator, "INFO_BACKGROUND01", (String) context.get("infoImage_s"), (String) context.get("infoImage"));
			storeBackGround(delegator, "FOOTER_BACKGROUND01", (String) context.get("footerImage_s"), (String) context.get("footerImage"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	private static void storeBackGround(Delegator delegator, String contentId, String statusId, String objectInfo)
			throws GenericEntityException{
		Map<String, Object> contentUpdate = UtilMisc.toMap("contentId", contentId, "statusId", statusId);
		if (UtilValidate.isNotEmpty(objectInfo)) {
			contentUpdate.put("objectInfo", objectInfo);
		}
		GenericValue content = delegator.makeValidValue("Content", contentUpdate);
		content.store();
	}
	public static GenericValue getContentById(Delegator delegator, GenericValue userLogin, Object contentId)
			throws GenericEntityException {
		GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
		if (UtilValidate.isEmpty(content)) {
			content = delegator.create("Content", UtilMisc.toMap("contentId", contentId, "contentTypeId", "CONFIG_WEBSITE", "statusId", "CTNT_PUBLISHED"));
		}
		return content;
	}
}
