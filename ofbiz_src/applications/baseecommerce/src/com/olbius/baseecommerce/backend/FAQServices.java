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

public class FAQServices {
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFAQ(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listSortFields.add("-createdStamp");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.EQUALS, "FAQ_ROOT"));
			listAllConditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
			listIterator = delegator.find("ContentAndContentType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static String sendFAQ(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String contentTypeId = request.getParameter("contentTypeId");
		String email = request.getParameter("email");
		String description = request.getParameter("description");
		String author = request.getParameter("author");
		String webSiteId = request.getParameter("webSiteId");
		boolean beganTx = TransactionUtil.begin(7200);
		String contentId = "FAQ" + delegator.getNextSeqId("Content");
		try {
			ContentWithWebSite.create(delegator, webSiteId,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", contentTypeId, "infoString", email, "contentName", author, "author", author,
							"longDescription", description, "statusId", "CTNT_PUBLISHED", "createdDate", new Timestamp(System.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		} finally {
			TransactionUtil.commit(beganTx);
		}
		return "success";
	}
	
	public static Map<String, Object> createReply(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String contentId = "CMT" + delegator.getNextSeqId("Content");
        try {
        	GenericValue userLogin = (GenericValue) context.get("userLogin");
            String author = userLogin.getString("userLoginId");
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "COMMENT", "contentName", author,
							"longDescription", context.get("longDescription"), "statusId", "CTNT_PUBLISHED", "author", author));
			//	createContentAssoc
			GenericValue contentAssoc = delegator.makeValidValue("ContentAssoc",
					UtilMisc.toMap("contentIdTo", context.get("contentIdTo"), "contentId", contentId, "contentAssocTypeId", "COMMENT",
							"fromDate", new Timestamp(System.currentTimeMillis())));
			delegator.create(contentAssoc);
		} catch (Exception e) {
			e.printStackTrace();
		}
        result.put("contentId", contentId);
        result.put("index", context.get("index"));
		return result;
	}
	public static Map<String, Object> updateReply(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String contentId = (String) context.get("contentId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String userLoginId = userLogin.getString("userLoginId");
			String longDescription = (String) context.get("longDescription");
			String statusId = (String) context.get("statusId");
			//	updateContent
			Map<String, Object> mapUpdateContent = FastMap.newInstance();
			mapUpdateContent.put("contentId", contentId);
			mapUpdateContent.put("author", userLoginId);
			if (UtilValidate.isNotEmpty(longDescription)) {
				mapUpdateContent.put("longDescription", longDescription);
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				mapUpdateContent.put("statusId", statusId);
			}
			GenericValue content = delegator.makeValidValue("Content", mapUpdateContent);
			delegator.store(content);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("contentId", contentId);
		result.put("index", context.get("index"));
		return result;
	}
	
	public static Map<String, Object> getAnswerFAQ(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		String contentId = (String) context.get("contentId");
		List<Map<String, Object>> answers = TopicServices.contentByContentId(delegator, security, locale, contentId, UtilMisc.toList("-createdStamp"), true);
		Map<String, Object> answerFAQ = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(answers)) {
			answerFAQ = answers.get(0);
			answerFAQ.put("author", PartyHelper.getPartyName(delegator, (String)answers.get(0).get("author"), true, true));
		}
		result.put("answerFAQ", answerFAQ);
		return result;
	}
	public static Map<String, Object> getFAQ(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String contentId = (String) context.get("contentId");
		List<GenericValue> listContentAndContentType = delegator.findList("ContentAndContentType",
				EntityCondition.makeCondition("contentId", EntityJoinOperator.EQUALS, contentId), null, null, null, false);
		Map<String, Object> faq = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(listContentAndContentType)) {
			GenericValue contentAndContentType = EntityUtil.getFirst(listContentAndContentType);
			faq.putAll(contentAndContentType);
			faq.put("createdStamp", ContentUtils.getTimeAgo(locale, contentAndContentType.getTimestamp("createdStamp")));
			Map<String, Object> resultAnswerFAQ = dispatcher.runSync("getAnswerFAQ",
					UtilMisc.toMap("contentId", contentAndContentType.get("contentId")));
			faq.put("answerFAQ", resultAnswerFAQ.get("answerFAQ"));
		}
		result.put("faq", faq);
		return result;
	}
	
	public static Map<String, Object> listAsk(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		String sortBy = (String) context.get("sortBy");
		String contentTypeId = (String) context.get("contentTypeId");

		List<String> orderBy = FastList.newInstance();
		if ("date".equals(sortBy)) {
			orderBy.add("-createdStamp");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> conditions = FastList.newInstance();
		if (UtilValidate.isEmpty(contentTypeId)) {
			conditions.add(EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.EQUALS, "FAQ_ROOT"));
		} else {
			conditions.add(EntityCondition.makeCondition("contentTypeId", EntityJoinOperator.EQUALS, contentTypeId));
		}
		conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
		
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		conditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
		List<GenericValue> contentAndContentType = delegator.findList("ContentAndContentType",
				EntityCondition.makeCondition(conditions), null, orderBy, null, false);

		List<Map<String, Object>> listAsk = FastList.newInstance();
		for (GenericValue x : contentAndContentType) {
			Map<String, Object> ask = FastMap.newInstance();
			ask.putAll(x);
			ask.put("createdStamp", ContentUtils.getTimeAgo(locale, x.getTimestamp("createdStamp")));
			String author = x.getString("author");
			if (UtilValidate.isNotEmpty(author)) {
				userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", author), false);
				if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
					ask.put("partyRole", UtilProperties.getMessage("DpcEcommerceBackendUiLabels", "BSAdmin", locale));
					ask.put("author", PartyHelper.getPartyName(delegator, author, true, true));
				}
			}
			listAsk.add(ask);
		}
		result.put("listAsk", listAsk);
		return result;
	}
}
