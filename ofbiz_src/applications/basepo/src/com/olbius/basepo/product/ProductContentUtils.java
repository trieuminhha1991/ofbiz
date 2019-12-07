package com.olbius.basepo.product;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyHelper;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductContentUtils {
	public static String RESOURCE_PROPERTIES = "ecommerce.properties";
	public static final String PARTY_ATTRIBUTE_WEBSITEID = "party.attribute.webSiteId";
	
	public static Map<String, Object> loadProductSpecifications(Delegator delegator, String webSiteId, String productId)
			throws GenericEntityException {
		Map<String, Object> productSpecifications = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
		conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
		List<GenericValue> productContents = delegator.findList("ProductAndContent",
				EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("contentId", "productContentTypeId", "longDescription", "dataResourceId"), null, null,
				false);
		for (GenericValue x : productContents) {
			String productContentTypeId = x.getString("productContentTypeId");
			String contentId = x.getString("contentId");
			String longDescription = x.getString("longDescription");
			String dataResourceId = x.getString("dataResourceId");
			switch (productContentTypeId) {
			case "EFFECTS":
				productSpecifications.put("effects", longDescription);
				productSpecifications.put("effectsId", contentId);
				break;
			case "COMPOSITION":
				productSpecifications.put("composition", longDescription);
				productSpecifications.put("compositionId", contentId);
				break;
			case "SHELFLIFE":
				productSpecifications.put("shelfLife", longDescription);
				productSpecifications.put("shelfLifeId", contentId);
				break;
			case "USERS":
				productSpecifications.put("users", longDescription);
				productSpecifications.put("usersId", contentId);
				break;
			case "INSTRUCTIONS":
				productSpecifications.put("instructions", longDescription);
				productSpecifications.put("instructionsId", contentId);
				break;
			case "LICENSE":
				productSpecifications.put("license", longDescription);
				productSpecifications.put("licenseId", contentId);
				break;
			case "PACKING":
				productSpecifications.put("packing", longDescription);
				productSpecifications.put("packingId", contentId);
				break;
			case "CONTRAINDICATIONS":
				productSpecifications.put("contraindications", longDescription);
				productSpecifications.put("contraindicationsId", contentId);
				break;
			case "ADDITIONAL_IMAGE_1":
				productSpecifications.put("ADDITIONAL_IMAGE_1", getLinkImageInDataResource(delegator, dataResourceId));
				productSpecifications.put("ADDITIONAL_IMAGE_1Id", dataResourceId);
				break;
			case "ADDITIONAL_IMAGE_2":
				productSpecifications.put("ADDITIONAL_IMAGE_2", getLinkImageInDataResource(delegator, dataResourceId));
				productSpecifications.put("ADDITIONAL_IMAGE_2Id", dataResourceId);
				break;
			case "ADDITIONAL_IMAGE_3":
				productSpecifications.put("ADDITIONAL_IMAGE_3", getLinkImageInDataResource(delegator, dataResourceId));
				productSpecifications.put("ADDITIONAL_IMAGE_3Id", dataResourceId);
				break;
			case "ADDITIONAL_IMAGE_4":
				productSpecifications.put("ADDITIONAL_IMAGE_4", getLinkImageInDataResource(delegator, dataResourceId));
				productSpecifications.put("ADDITIONAL_IMAGE_4Id", dataResourceId);
				break;
			default:
				break;
			}
		}
		return productSpecifications;
	}

	private static String getLinkImageInDataResource(Delegator delegator, String dataResourceId)
			throws GenericEntityException {
		String objectInfo = null;
		if (UtilValidate.isNotEmpty(dataResourceId)) {
			GenericValue dataResource = delegator.findOne("DataResource",
					UtilMisc.toMap("dataResourceId", dataResourceId), false);
			if (UtilValidate.isNotEmpty(dataResource)) {
				objectInfo = dataResource.getString("objectInfo");
			}
		}
		return objectInfo;
	}

	public static GenericValue productIntroduction(Delegator delegator, String productId, String contentId)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		if (UtilValidate.isEmpty(contentId)) {
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productContentTypeId",
					"INTRODUCTION", "statusId", "CTNT_PUBLISHED")));
		} else {
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "contentId", contentId,
					"productContentTypeId", "INTRODUCTION")));
		}
		List<GenericValue> productContents = delegator.findList("ProductAndContent",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		GenericValue productContent = new GenericValue();
		if (UtilValidate.isNotEmpty(productContents)) {
			productContent = EntityUtil.getFirst(productContents);
		}
		return productContent;
	}

	public static List<Map<String, Object>> productReviews(Delegator delegator, GenericValue userLogin,
			String productId, String productStoreId) {
		List<Map<String, Object>> productReviews = FastList.newInstance();
		try {
			List<GenericValue> productReview = delegator.findList("ProductReview",
					EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "productId",
							productId, "statusId", "PRR_APPROVED")),
					null, UtilMisc.toList("-postedDateTime"), null, false);
			if (UtilValidate.isNotEmpty(userLogin)) {
				List<GenericValue> productReviewOfHim = delegator.findList("ProductReview",
						EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "productId",
								productId, "statusId", "PRR_PENDING", "userLoginId", userLogin.get("userLoginId"))),
						null, UtilMisc.toList("-postedDateTime"), null, false);
				productReview.addAll(productReviewOfHim);
				productReview = EntityUtil.orderBy(productReview, UtilMisc.toList("-postedDateTime"));
			}
			for (GenericValue x : productReview) {
				Map<String, Object> review = FastMap.newInstance();
				review.putAll(x);
				review.put("userName", getUserName(delegator, x.getString("userLoginId")));
				productReviews.add(review);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return productReviews;
	}

	public static List<GenericValue> productReview(Delegator delegator, GenericValue userLogin, String productId,
			String productStoreId) {
		List<GenericValue> productReview = FastList.newInstance();
		try {
			productReview = delegator.findList("ProductReview",
					EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "productId",
							productId, "statusId", "PRR_APPROVED")),
					null, UtilMisc.toList("-postedDateTime"), null, false);
			if (UtilValidate.isNotEmpty(userLogin)) {
				List<GenericValue> productReviewOfHim = delegator.findList("ProductReview",
						EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "productId",
								productId, "statusId", "PRR_PENDING", "userLoginId", userLogin.get("userLoginId"))),
						null, UtilMisc.toList("-postedDateTime"), null, false);
				productReview.addAll(productReviewOfHim);
				productReview = EntityUtil.orderBy(productReview, UtilMisc.toList("-postedDateTime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productReview;
	}

	public static String getUserName(Delegator delegator, String userLoginId) throws GenericEntityException {
		String userName = "";
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		if (UtilValidate.isNotEmpty(userLogin)) {
			String partyId = userLogin.getString("partyId");
			userName = PartyHelper.getPartyName(delegator, partyId, true, true);
		}
		return userName;
	}
	
	/**
	 * Copy from BaseEcommerce
	 * @param delegator
	 * @param userLogin
	 * @return
	 * @throws GenericEntityException
	 */
	public static String getCurrentWebSite(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		String partyId = userLogin.getString("partyId");
		String webSiteId = "";
		String attrName = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, PARTY_ATTRIBUTE_WEBSITEID, "", delegator);
		GenericValue partyAttribute = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", attrName), false);
		if (UtilValidate.isNotEmpty(partyAttribute)) {
			webSiteId = partyAttribute.getString("attrValue");
		}
		if (UtilValidate.isEmpty(webSiteId)) {
			List<GenericValue> webSites = delegator.findList("WebSite", EntityCondition.makeCondition("isDefault", "Y"), null, null, null, true);
			if (UtilValidate.isNotEmpty(webSites)) {
				webSiteId = EntityUtil.getFirst(webSites).getString("webSiteId");
			}
		}
		return webSiteId;
	}
	
	/**
	 * Copy from BaseEcommerce
	 * @param dispatcher
	 * @param delegator
	 * @param userLogin
	 * @param lookupKeyValue
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> createContentWithWebSite(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Map<String, ?> lookupKeyValue) throws GenericServiceException, GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue content = delegator.makeValidValue("Content", lookupKeyValue);
		content.create();
		// link content to WebSite createWebSiteContent
		String webSiteId = getCurrentWebSite(delegator, userLogin);
		Map<String, Object> createWebsiteCntResult = dispatcher.runSync("createWebSiteContent", UtilMisc.toMap("webSiteId", webSiteId, "contentId", lookupKeyValue.get("contentId"), "webSiteContentTypeId", "CONTENT", "userLogin", userLogin));
		if (ServiceUtil.isError(createWebsiteCntResult)) {
			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createWebsiteCntResult));
		}
		return successResult;
	}
	
	
}
