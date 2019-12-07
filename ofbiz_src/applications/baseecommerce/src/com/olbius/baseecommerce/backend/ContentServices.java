package com.olbius.baseecommerce.backend;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javolution.util.FastMap;

public class ContentServices {

	public static Map<String, Object> quickUpload(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		result.put("fileName", "filename");
		result.put("uploaded", "1");
		result.put("error", FastMap.newInstance());
		result.put("url", uploadFile(dispatcher, context.get("userLogin"), context.get("upload")));
		return result;
	}
	public static String uploadFile(LocalDispatcher dispatcher, Object userLogin, Object uploadedFile)
			throws GenericServiceException {
		Map<String, Object> uploadedFileCtx = FastMap.newInstance();
		ByteBuffer fileBytes = (ByteBuffer) uploadedFile;
		if (fileBytes.capacity() == 0) {
			return null;
		} else {
			uploadedFileCtx.put("uploadedFile", fileBytes);
			uploadedFileCtx.put("_uploadedFile_fileName", "img");
			uploadedFileCtx.put("userLogin", userLogin);
			Map<String, Object> resultUploaded = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			return (String)resultUploaded.get("path");
		}
	}

	public static Map<String, Object> saveContent(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Locale locale = (Locale) context.get("locale");
		String type = (String) context.get("type");
		switch (type) {
		case "PRODUCT":
			return ConfigProductServices.saveContentProduct(ctx, context);
		case "TOPIC":
			return TopicServices.saveContentTopic(ctx, context);
		default:
			break;
		}
		return ServiceUtil.returnError(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSContentNotValid", locale));
	}

	public static Map<String, Object> renovateContent(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Locale locale = (Locale) context.get("locale");
		String type = (String) context.get("type");
		switch (type) {
		case "PRODUCT":
			return ConfigProductServices.updateContentProduct(ctx, context);
		case "TOPIC":
			return TopicServices.updateContentTopic(ctx, context);
		default:
			break;
		}
		return ServiceUtil.returnError(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSContentNotValid", locale));
	}
	
	public static int numberOfReplies(Delegator delegator, String contentId)
			throws GenericDataSourceException {
		int number = 0;
		SQLProcessor processor = new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz"));
		try {
			Timestamp current = new Timestamp(System.currentTimeMillis());
			ResultSet resultSet = processor.executeQuery("SELECT COUNT(*)"
					+ " FROM public.content_assoc as ca"
					+ " WHERE ca.content_id_to = '" + contentId + "' AND ca.content_assoc_type_id = 'COMMENT'"
					+ " AND ((ca.thru_date IS NULL OR ca.thru_date > '" + current + "')"
					+ " AND (ca.from_date IS NULL OR ca.from_date <= '" + current + "'))");
			while(resultSet.next()) {
				number = resultSet.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			processor.close();
		}
		return number;
	}
	
	public static Map<String, Object> createContentCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue contentCategory = delegator.makeValidValue("ContentCategory", context);
			if (UtilValidate.isEmpty(context.get("contentCategoryTypeId"))) {
				contentCategory.set("contentCategoryTypeId", "ARTICLE");
			}
			contentCategory.create();
			//	createWebsiteContentCategory
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			dispatcher.runSync("createWebsiteContentCategory",
					UtilMisc.toMap("webSiteId", webSiteId, "contentCategoryId", context.get("contentCategoryId"), "userLogin", userLogin));
			dispatcher.runSync("fixCategory", UtilMisc.toMap("userLogin", context.get("userLogin")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> updateContentCategory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue contentCategory = delegator.makeValidValue("ContentCategory", context);
		contentCategory.set("contentCategoryTypeId", "ARTICLE");
		contentCategory.store();
		return result;
	}
	
	public static Map<String, Object> createWebsiteContentCategory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue websiteContentCategory = delegator.makeValidValue("WebsiteContentCategory",
				UtilMisc.toMap("webSiteId", context.get("webSiteId"), "contentCategoryId", context.get("contentCategoryId"),
						"fromDate", new Timestamp(System.currentTimeMillis())));
		websiteContentCategory.create();
		return result;
	}
	public static Map<String, Object> updateWebsiteContentCategory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue websiteContentCategory = delegator.makeValidValue("WebsiteContentCategory", context);
		websiteContentCategory.store();
		return result;
	}
	
	public static Map<String, Object> createContentCategoryMember(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue contentCategoryMember = delegator.makeValidValue("ContentCategoryMember",
				UtilMisc.toMap("contentId", context.get("contentId"), "contentCategoryId", context.get("contentCategoryId"),
						"comments", context.get("comments"), "sequenceNum", context.get("sequenceNum"),
						"fromDate", new Timestamp(System.currentTimeMillis())));
		contentCategoryMember.create();
		return result;
	}
	public static Map<String, Object> updateContentCategoryMember(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue contentCategoryMember = delegator.makeValidValue("ContentCategoryMember", context);
		contentCategoryMember.store();
		return result;
	}
}
