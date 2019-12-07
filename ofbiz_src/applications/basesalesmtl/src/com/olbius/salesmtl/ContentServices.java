package com.olbius.salesmtl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import net.sf.json.JSONObject;

public class ContentServices {
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> uploadFileAttachment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String entityName = (String) context.get("entityName");
			JSONObject fields = JSONObject.fromObject(context.get("fields"));
			Object userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			//	initialization root content
			GenericValue entity = delegator.findOne(entityName, fields, false);
			GenericValue rootContent = entity.getRelatedOne("Content", false);
			Object rootContentId = null;
			if (UtilValidate.isEmpty(rootContent)) {
				result = dispatcher.runSync("createContent", UtilMisc.toMap("userLogin", userLogin));
				rootContentId = result.get("contentId");
				entity.set("contentId", rootContentId);
				entity.store();
			} else {
				rootContentId = rootContent.get("contentId");
			}
			if (UtilValidate.isNotEmpty(rootContentId)) {
				context.remove("entityName");
				context.remove("fields");
				//	uploadFile
				result = dispatcher.runSync("jackrabbitUploadFile", context);
				Object path = result.get("path");
				if (UtilValidate.isNotEmpty(path)) {
					//	createDataResource
					result = dispatcher.runSync("createDataResource", UtilMisc.toMap("dataResourceTypeId", "LINK", "dataTemplateTypeId", "NONE",
							"statusId", "CTNT_AVAILABLE", "mimeTypeId", "text/xml", "objectInfo", result.get("path"), "isPublic", "N",
							"userLogin", userLogin));
					Object dataResourceId = result.get("dataResourceId");
					if (UtilValidate.isNotEmpty(dataResourceId)) {
						//	createContent Item
						result = dispatcher.runSync("createContent", UtilMisc.toMap("dataResourceId", dataResourceId, "userLogin", userLogin));
						Object contentId = result.get("contentId");
						if (UtilValidate.isNotEmpty(contentId)) {
							//	createContentAssoc
							dispatcher.runSync("createContentAssoc", UtilMisc.toMap("contentIdTo", rootContentId, "contentId", contentId,
									"contentAssocTypeId", "LIST_ENTRY", "fromDate", new Timestamp(System.currentTimeMillis()), "userLogin", userLogin));
						} else {
							throw new Exception("Error while createContent Item");
						}
					} else {
						throw new Exception("Error while createDataResource");
					}
				} else {
					throw new Exception("Error while get path file");
				}
			} else {
				throw new Exception("Error while get rootContentId");
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadFileAttachment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String entityName = (String) context.get("entityName");
			JSONObject fields = JSONObject.fromObject(context.get("fields"));
			GenericValue entity = delegator.findOne(entityName, fields, false);
			GenericValue rootContent = entity.getRelatedOne("Content", false);
			if (UtilValidate.isNotEmpty(rootContent)) {
				Object contentId = rootContent.get("contentId");
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("caFromDate", "caThruDate")));
				conditions.add(EntityCondition.makeCondition("caContentIdTo", EntityJoinOperator.EQUALS, contentId));
				List<GenericValue> contents = delegator.findList("ContentAssocDataResourceViewFrom",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("drObjectInfo"), null, null, false);
				result.put("fileAttachment", EntityUtil.getFieldListFromEntityList(contents, "drObjectInfo", true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
