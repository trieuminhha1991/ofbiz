package com.olbius.baseecommerce.backend;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.baseecommerce.backend.content.ContentWithWebSite;
import com.olbius.basehr.util.PartyHelper;

import javolution.util.FastList;
import javolution.util.FastMap;

public class TopicServices {
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listContentCategories(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			listAllConditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
			listAllConditions.add(EntityCondition.makeCondition("contentCategoryTypeId", EntityJoinOperator.EQUALS, "ARTICLE"));
			listIterator = delegator.find("WebsiteContentCategoryDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	public static List<GenericValue> contentCategories(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
		conditions.add(EntityCondition.makeCondition("contentCategoryTypeId", EntityJoinOperator.EQUALS, "ARTICLE"));
		List<GenericValue> contentCategories = delegator.findList("WebsiteContentCategoryDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentCategoryId", "categoryName"),
				UtilMisc.toList("categoryName"), null, false);
		return contentCategories;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listTopicContent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("contentCategoryId");
		listSortFields.add("-createdStamp");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String TotalRows = "0";
		List<Map<String, Object>> listTopicContent = FastList.newInstance();
		try {
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		    int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		    int start = pageNum * pagesize + 1;
			if (parameters.containsKey("contentCategoryId")) {
				if (UtilValidate.isNotEmpty(parameters.get("contentCategoryId")[0])) {
					listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentCategoryId", parameters.get("contentCategoryId")[0])));
				}
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			listAllConditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
			listAllConditions.add(EntityCondition.makeCondition("contentCategoryTypeId", EntityJoinOperator.EQUALS, "ARTICLE"));
			listIterator = delegator.find("TopicDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			TotalRows = String.valueOf(listIterator.getResultsTotalSize());
			List<GenericValue> dummy = listIterator.getPartialList(start, pagesize);
			for (GenericValue x : dummy) {
				Map<String, Object> topicContent = FastMap.newInstance();
				topicContent.putAll(x);
				topicContent.putAll(isHotContent(delegator, x.getString("contentId"), x.getString("contentCategoryId")));
				listTopicContent.add(topicContent);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} finally {
			if (listIterator != null) {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("listIterator", listTopicContent);
		result.put("TotalRows", TotalRows);
		return result;
	}
	private static Map<String, Object> isHotContent(Delegator delegator, String contentId, String contentCategoryId)
			throws GenericEntityException {
		Map<String, Object> bestSell = FastMap.newInstance();
		String contentCategoryId_HOT = contentCategoryId.toUpperCase() + "_HOT";
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentId", contentId, "contentCategoryId", contentCategoryId_HOT)));

		List<GenericValue> contentTypeMembers = delegator.findList("ContentCategoryMember",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(contentTypeMembers)) {
			bestSell.put("isHot", true);
			GenericValue contentTypeMember = EntityUtil.getFirst(contentTypeMembers);
			bestSell.put("isHotFromDate", contentTypeMember.getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isHot", false);
		}
		return bestSell;
	}
	
	public static Map<String, Object> configContent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String contentId = (String) context.get("contentId");
			String statusId = (String) context.get("statusId");
			String contentCategoryId = (String) context.get("contentCategoryId");
			String isHot = (String) context.get("isHot");
			Long isHotFromDateL = (Long) context.get("isHotFromDate");
			Timestamp isHotFromDate = null;
			if (UtilValidate.isNotEmpty(isHotFromDateL)) {
				isHotFromDate = new Timestamp(isHotFromDateL);
			}
			List<EntityCondition> conditions = FastList.newInstance();
			String contentCategoryId_HOT = contentCategoryId.toUpperCase() + "_HOT";
			if ("true".equals(isHot) && UtilValidate.isEmpty(isHotFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentCategoryId", contentCategoryId_HOT, "contentId", contentId)));
				List<GenericValue> contentTypeMembers = delegator.findList("ContentCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(contentTypeMembers)) {
					//	createContentCategoryMember
					dispatcher.runSync("createContentCategoryMember",
							UtilMisc.toMap("contentId", contentId, "contentCategoryId", contentCategoryId_HOT, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isHot) && UtilValidate.isNotEmpty(isHotFromDate)) {
					//	updateContentCategoryMember
					dispatcher.runSync("updateContentCategoryMember",
							UtilMisc.toMap("contentId", contentId, "contentCategoryId", contentCategoryId_HOT, "fromDate", isHotFromDate, 
									"thruDate", new Timestamp(System.currentTimeMillis()), "userLogin", userLogin));
				}
			}
			//	updateContent
			if (UtilValidate.isNotEmpty(statusId)) {
				dispatcher.runSync("updateContent", UtilMisc.toMap("contentId", contentId, "statusId", statusId, "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> saveContentTopic(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String contentCategoryId = (String) context.get("contentCategoryId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			Locale locale = (Locale) context.get("locale");
			String contentName = (String) context.get("contentName");
			String description = (String) context.get("description");
			String longDescription = (String) context.get("editor");
			String contentId = "TP" + delegator.getNextSeqId("Content");
			//	createContent
			String originalImageUrl = ContentServices.uploadFile(dispatcher, context.get("userLogin"), context.get("titleImage"));
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "ARTICLE", "contentName", contentName, "author", userLoginId,
							"description", description, "longDescription", longDescription, "statusId", "CTNT_DEACTIVATED",
							"originalImageUrl", originalImageUrl,
							"createdDate", new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
			//	createContentCategoryMember
			dispatcher.runSync("createContentCategoryMember",
					UtilMisc.toMap("contentId", contentId, "contentCategoryId", contentCategoryId));
			result = ServiceUtil.returnSuccess(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSCreateContentSuccess", locale));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		result.put("contentCategoryId", contentCategoryId);
		result.put("type", context.get("type"));
		return result;
	}
	public static Map<String, Object> updateContentTopic(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String contentTypeId = (String) context.get("contentTypeId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			Locale locale = (Locale) context.get("locale");
			String contentId = (String) context.get("contentId");
			String contentName = (String) context.get("contentName");
			String description = (String) context.get("description");
			String longDescription = (String) context.get("editor");
			//	updateContent
			GenericValue content = delegator.makeValidValue("Content",
					UtilMisc.toMap("contentId", contentId, "contentName", contentName, "author", userLoginId,
							"description", description, "longDescription", longDescription, "lastModifiedDate",
							new Timestamp(System.currentTimeMillis()), "lastModifiedByUserLogin", userLoginId));
			String originalImageUrl = ContentServices.uploadFile(dispatcher, context.get("userLogin"), context.get("titleImage"));
			if (UtilValidate.isNotEmpty(originalImageUrl)) {
				content.set("originalImageUrl", originalImageUrl);
			}
			delegator.store(content);
			result = ServiceUtil.returnSuccess(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSUpdateContentSuccess", locale));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		result.put("contentTypeId", contentTypeId);
		result.put("type", context.get("type"));
		return result;
	}
	
	public static Map<String, Object> listCommentsByTopic(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		String contentId = (String) context.get("contentId");
		String isProduct = (String) context.get("isProduct");
		List<Map<String, Object>> comments = FastList.newInstance();
		if ("true".equals(isProduct)) {
			comments = ConfigProductServices.contentByProductId(delegator, security, locale, contentId, UtilMisc.toList("-createdStamp"), true);
		} else {
			comments = contentByContentId(delegator, security, locale, contentId, UtilMisc.toList("-createdStamp"), true);
		}
		result.put("comments", comments);
		return result;
	}

	public static Map<String, Object> listRepliesByComment(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		String contentId = (String) context.get("contentId");
		List<Map<String, Object>> replies = contentByContentId(delegator, security, locale, contentId, UtilMisc.toList("-createdStamp"), false);
		result.put("replies", replies);
		return result;
	}
	public static List<Map<String, Object>> contentByContentId(Delegator delegator, Security security, Locale locale, String contentId,
			List<String> orderBy, boolean isComment)
					throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentIdTo", contentId, "contentAssocTypeId", "COMMENT")));
		List<GenericValue> contentAssocs = delegator.findList("ContentAssoc",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), null, null, false);
		List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentAssocs, "contentId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "COMMENT")));
		List<GenericValue> listContent = delegator.findList("Content",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "contentName", "longDescription", "statusId", "createdStamp", "author"),
				orderBy, null, false);
		List<Map<String, Object>> contents = FastList.newInstance();
		for (GenericValue x : listContent) {
			Map<String, Object> content = FastMap.newInstance();
			content.putAll(x);
			content.put("createdStamp", ContentUtils.getTimeAgo(locale, x.getTimestamp("createdStamp")));
			String author = x.getString("author");
			if (UtilValidate.isNotEmpty(author)) {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", author), false);
				if (UtilValidate.isNotEmpty(userLogin)) {
					if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
						content.put("partyRole", UtilProperties.getMessage("DpcEcommerceBackendUiLabels", "BSAdmin", locale));
					}
				}
			}
			if (isComment) {
				content.put("numberOfReplies", ContentServices.numberOfReplies(delegator, x.getString("contentId")));
			}
			contents.add(content);
		}
		return contents;
	}
	
	public static Map<String, Object> commentsByTopic(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		String contentId = (String) context.get("contentId");
		String isProduct = (String) context.get("isProduct");
		List<Map<String, Object>> comments = FastList.newInstance();
		List<String> orderBy = UtilMisc.toList("-createdStamp");
		List<Map<String, Object>> contents = FastList.newInstance();
		if ("true".equals(isProduct)) {
			contents = ConfigProductServices.commentsByProductId(delegator, security, locale, contentId, orderBy);
		} else {
			contents = commentsByContentId(delegator, security, locale, contentId, orderBy);
		}
		orderBy = UtilMisc.toList("createdStamp");
		for (Map<String, Object> x : contents) {
			Map<String, Object> content = FastMap.newInstance();
			content.putAll(x);
			content.put("replys", commentsByContentId(delegator, security, locale,(String) x.get("contentId"), orderBy));
			comments.add(content);
		}
		result.put("comments", comments);
		return result;
	}
	private static List<Map<String, Object>> commentsByContentId(Delegator delegator, Security security, Locale locale, String contentId, List<String> orderBy)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentIdTo", contentId, "contentAssocTypeId", "COMMENT")));
		List<GenericValue> contentAssocs = delegator.findList("ContentAssoc",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), null, null, false);
		List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentAssocs, "contentId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("statusId", "CTNT_PUBLISHED", "contentTypeId", "COMMENT")));
		List<GenericValue> contents = delegator.findList("Content",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "contentName", "longDescription", "createdStamp", "author"),
				orderBy, null, false);
		List<Map<String, Object>> comments = FastList.newInstance();
		for (GenericValue x : contents) {
			Map<String, Object> comment = FastMap.newInstance();
			comment.putAll(x);
			comment.put("createdStamp", ContentUtils.getTimeAgo(locale, x.getTimestamp("createdStamp")));
			String author = x.getString("author");
			if (UtilValidate.isNotEmpty(author)) {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", author), false);
				if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
					comment.put("partyRole", UtilProperties.getMessage("DpcEcommerceBackendUiLabels", "BSAdmin", locale));
					comment.put("author", PartyHelper.getPartyName(delegator, author, true, true));
				}
			}
			comments.add(comment);
		}
		return comments;
	}

	public static String commentToTopic(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        String userLoginId = null;
        if (UtilValidate.isNotEmpty(userLogin)) {
        	userLoginId = userLogin.getString("userLoginId");
		}
        String isProduct = request.getParameter("isProduct");
        String contentIdTo = request.getParameter("contentId");
        String email = request.getParameter("email");
        String description = request.getParameter("description");
        String author = request.getParameter("author");
        boolean beganTx = TransactionUtil.begin(7200);
        String contentId = "CMT" + delegator.getNextSeqId("Content");
        try {
			GenericValue content = delegator.makeValidValue("Content",
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "COMMENT", "infoString", email, "contentName", author, "author", author,
							"longDescription", description, "statusId", "CTNT_PUBLISHED", "createdDate", new Timestamp(System.currentTimeMillis()),
							"createdByUserLogin", userLoginId));
			delegator.create(content);
			if ("true".equals(isProduct)) {
				//	create ProductContent
				GenericValue productContent = delegator.makeValidValue("ProductContent",
						UtilMisc.toMap("productId", contentIdTo, "contentId", contentId, "productContentTypeId", "COMMENT",
								"fromDate", new Timestamp(System.currentTimeMillis())));
				delegator.create(productContent);
			} else {
				//	createContentAssoc
				GenericValue contentAssoc = delegator.makeValidValue("ContentAssoc",
						UtilMisc.toMap("contentIdTo", contentIdTo, "contentId", contentId, "contentAssocTypeId", "COMMENT",
								"fromDate", new Timestamp(System.currentTimeMillis())));
				delegator.create(contentAssoc);
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		} finally {
			TransactionUtil.commit(beganTx);
		}
        return "success";
    }
}
