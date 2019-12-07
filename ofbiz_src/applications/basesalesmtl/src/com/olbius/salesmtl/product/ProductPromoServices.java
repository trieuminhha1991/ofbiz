package com.olbius.salesmtl.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProductPromoServices {
	public static final String module = ProductPromoServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static final String resources = "AccountingUiLabels";
    
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoExt(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean showData = false;
			boolean isRoleEmployee = false;
			OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
			boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_VIEW");
			boolean hasUpdatePermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_UPDATE") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_APPROVE");
			if (hasPermission) {
				showData = true;
				isRoleEmployee = true;
			} else {
				hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PRODPROMOTION");
			}
			if (!hasPermission) {
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
	        }
			String customerId = null;
    		if (parameters.containsKey("isCustomer") && parameters.get("isCustomer").length > 0) {
    			String isCustomer = parameters.get("isCustomer")[0];
    			if ("Y".equals(isCustomer)) {
    				customerId = userLogin.getString("partyId");
    			}
			}
    		String ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		if (parameters.containsKey("isOwner") && parameters.get("isOwner").length > 0) {
    			String isOwner = parameters.get("isOwner")[0];
    			if ("Y".equals(isOwner)) {
    				ownerId = userLogin.getString("partyId");
    			}
			}
    		String _statusId = null;
    		if (parameters.containsKey("_statusId") && parameters.get("_statusId").length > 0) {
    			_statusId = parameters.get("_statusId")[0];
    		}
    		if (UtilValidate.isNotEmpty(_statusId)) {
    			listAllConditions.add(EntityCondition.makeCondition("statusId", _statusId));
    		}
    		
    		if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
    		if (UtilValidate.isNotEmpty(customerId)) {
    			List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyId", customerId));
    			listConds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
    			listConds.add(EntityUtil.getFilterByDateExpr());
    			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
    			if (UtilValidate.isNotEmpty(productStoreIds)) {
    				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
    				showData = true;
    			}
    		} else {
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payToPartyId", ownerId), EntityOperator.OR, EntityCondition.makeCondition("organizationPartyId", ownerId)));
				showData = true;
    		}
    		if (!isRoleEmployee || !hasUpdatePermission) listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
    		if (showData) {
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productPromoId");
				listSelectFields.add("productPromoTypeId");
				listSelectFields.add("promoName");
				listSelectFields.add("createdDate");
				listSelectFields.add("fromDate");
				listSelectFields.add("thruDate");
				listSelectFields.add("statusId");
				listSelectFields.add("organizationPartyId");
				opts.setDistinct(true);
				listIterator = delegator.find("ProductPromoExtApplStore", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSelectFields, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoExt service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> sendNotiChangePromoExtStatus(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String productPromoId = (String) context.get("productPromoId");
		try {
			Map<String, Object> resultValue = NotificationWorker.sendNotiWhenChangePromoExtStatus(delegator, dispatcher, locale, productPromoId, userLogin);
			if (ServiceUtil.isError(resultValue)) {
				return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling sendNotiChangePromoExtStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("productPromoId", productPromoId);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoSettlement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
			opts.setDistinct(true);
			listIterator = delegator.find("ProductPromoSettlement", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoSettlement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListPromoSettlementOrderCommit(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
			opts.setDistinct(true);
			listIterator = delegator.find("PromoSettleResultOrderCommit", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoSettlementOrderCommit service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListPromoRulePkByProdId(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	List<GenericPK> listResult = FastList.newInstance();
    	String productId = (String) context.get("productId");
    	
    	try {
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    		if (product != null) {
    			List<String> listProductId = FastList.newInstance();
    			listProductId.add(product.getString("productId"));
    			
    			GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator, nowTimestamp);
    			if (parentProduct != null) listProductId.add(parentProduct.getString("productId"));
    			
    			List<String> listProductCategoryId = FastList.newInstance();
    			List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMember", 
    					EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
    				List<String> listCategoryTemp = FastList.newInstance();
    				for (String productCategoryId : productCategoryIds) {
    					listCategoryTemp = ProductWorker.getAllCategoryParentTree(delegator, productCategoryId, nowTimestamp);
    					if (listCategoryTemp != null) {
    						listProductCategoryId.addAll(listCategoryTemp);
    						listCategoryTemp.clear();
    					}
    				}
    			}
    			
    			// find promotion product PPPA_INCLUDE
    			List<EntityCondition> conds1 = FastList.newInstance();
    			conds1.add(EntityCondition.makeCondition("productPromoActionSeqId", "_NA_"));
    			conds1.add(EntityCondition.makeCondition("productPromoCondSeqId", EntityOperator.NOT_EQUAL, "_NA_"));
    			conds1.add(EntityCondition.makeCondition("productPromoApplEnumId", "PPPA_INCLUDE"));
    			conds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductId));
    			List<GenericValue> productPromoProducts = delegator.findList("ProductPromoProduct", EntityCondition.makeCondition(conds1), null, null, null, false);
    			if (UtilValidate.isNotEmpty(productPromoProducts)) {
    				for (GenericValue productPromoProduct : productPromoProducts) {
    					GenericPK tmp = delegator.makePK("ProductPromoRule");
    					tmp.put("productPromoId", productPromoProduct.get("productPromoId"));
    					tmp.put("productPromoRuleId", productPromoProduct.get("productPromoRuleId"));
    					if (!listResult.contains(tmp)) {
    						listResult.add(tmp);
    					}
    				}
    			}
    			
    			// find promotion category PPPA_INCLUDE
    			List<EntityCondition> conds2 = FastList.newInstance();
    			conds2.add(EntityCondition.makeCondition("productPromoActionSeqId", "_NA_"));
    			conds2.add(EntityCondition.makeCondition("productPromoCondSeqId", EntityOperator.NOT_EQUAL, "_NA_"));
    			conds2.add(EntityCondition.makeCondition("productPromoApplEnumId", "PPPA_INCLUDE"));
    			conds2.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, listProductCategoryId));
    			List<GenericValue> productPromoCategorys = delegator.findList("ProductPromoCategory", EntityCondition.makeCondition(conds2), null, null, null, false);
    			if (UtilValidate.isNotEmpty(productPromoCategorys)) {
    				for (GenericValue productPromoCategory : productPromoCategorys) {
    					GenericPK tmp = delegator.makePK("ProductPromoRule");
    					tmp.put("productPromoId", productPromoCategory.get("productPromoId"));
    					tmp.put("productPromoRuleId", productPromoCategory.get("productPromoRuleId"));
    					if (!listResult.contains(tmp)) {
    						listResult.add(tmp);
    					}
    				}
    			}
    			
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListPromoRulePkByProdId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listPromotionRulePk", listResult);
    	return successResult;
    }
	
	public static Map<String, Object> createPromoSettlement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		//check permission
		if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "CREATE", "MODULE", "PROMOSETTLEMENT_NEW")) {
			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " don't have create permission!", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSYouHavenotCreatePermission", locale));
		}
		
		String promoSettlementId = (String) context.get("promoSettlementId");
		String promoSettlementName = (String) context.get("promoSettlementName");
		//String promoSettlementTypeId = (String) context.get("promoSettlementTypeId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoExtId = (String) context.get("productPromoExtId");
		
		String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	
		Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	Debug.logError(e, UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }
		
		try {
			if (UtilValidate.isNotEmpty(promoSettlementId)) {
				GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
				if (promoSettlement != null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale));
			}
			if (UtilValidate.isEmpty(promoSettlementId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPromoSettlementIdMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(fromDate)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSFromDateMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(thruDate)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThruDateMustNotBeEmpty", locale));
			}
		
			/*List<EntityCondition> findCondsSMT = FastList.newInstance();
			findCondsSMT.add(EntityCondition.makeCondition("promoSettlementId", promoSettlementId));
			findCondsSMT.add(EntityCondition.makeCondition("internalPartyId", internalPartyId));
			findCondsSMT.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			findCondsSMT.add(EntityCondition.makeCondition("salesStatementTypeId", salesStatementTypeId));
			List<GenericValue> listSMT = delegator.findList("SalesStatement", EntityCondition.makeCondition(findCondsSMT, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listSMT)) {
				String errorStr = UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale);
				return ServiceUtil.returnError(errorStr);
			}*/
			
			if (UtilValidate.isEmpty(promoSettlementId)) promoSettlementId = delegator.getNextSeqId("ProductPromoSettlement");
			GenericValue productSettlement = delegator.makeValue("ProductPromoSettlement");
			productSettlement.set("promoSettlementId", promoSettlementId);
			//productSettlement.set("promoSettlementTypeId", promoSettlementTypeId);
			productSettlement.set("promoSettlementName", promoSettlementName);
			productSettlement.set("productPromoId", productPromoId);
			productSettlement.set("productPromoExtId", productPromoExtId);
			productSettlement.set("fromDate", fromDate);
			productSettlement.set("thruDate", thruDate);
			productSettlement.set("createdBy", userLogin.get("userLoginId"));
			productSettlement.set("createdDate", UtilDateTime.nowTimestamp());
			productSettlement.set("statusId", "PSETTLE_CREATED");
			productSettlement.create();
		} catch (Exception e) {
			String errMsg = "Fatal error calling createPromoSettlement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("promoSettlementId", promoSettlementId);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListOrderItemPromoNeedSettleByRole(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String promoSettlementId = null;
    		if (parameters.containsKey("promoSettlementId") && parameters.get("promoSettlementId").length > 0) {
    			promoSettlementId = parameters.get("promoSettlementId")[0];
    		}
			if (promoSettlementId != null) {
				GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
				if (promoSettlement != null) {
					String productPromoId = promoSettlement.getString("productPromoId");
					Timestamp fromDate = promoSettlement.getTimestamp("fromDate");
					Timestamp thruDate = promoSettlement.getTimestamp("thruDate");
					listAllConditions.add(EntityCondition.makeCondition("productPromoId", productPromoId));
					listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
					listAllConditions.add(EntityCondition.makeCondition("orderId2", null));
					listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId2", null));
					List<String> distributorIds = PartyWorker.getDistributorIdsBySup(delegator, userLogin.getString("partyId"));
					listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, distributorIds));
					if (UtilValidate.isEmpty(listSortFields)) {
						listSortFields.add("sellerId");
						listSortFields.add("customerId");
						listSortFields.add("-orderDate");
					}
					opts.setDistinct(true);
					listIterator = delegator.find("OrderItemPromoNeedSettlement", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderItemPromoNeedSettleByRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListPromoExtPromoNeedSettleByRole(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String promoSettlementId = null;
    		if (parameters.containsKey("promoSettlementId") && parameters.get("promoSettlementId").length > 0) {
    			promoSettlementId = parameters.get("promoSettlementId")[0];
    		}
			if (promoSettlementId != null) {
				GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
				if (promoSettlement != null) {
					String productPromoExtId = promoSettlement.getString("productPromoExtId");
					//Timestamp fromDate = promoSettlement.getTimestamp("fromDate");
					//Timestamp thruDate = promoSettlement.getTimestamp("thruDate");
					listAllConditions.add(EntityCondition.makeCondition("productPromoId", productPromoExtId));
					//listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
					List<String> customerIds = PartyWorker.getCustomerIdsBySup(delegator, userLogin.getString("partyId"));
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
					listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"));
					listAllConditions.add(EntityCondition.makeCondition("resultEnumId", "PROMO_REG_EVAL_PASS"));
					listAllConditions.add(EntityCondition.makeCondition("productPromoId2", null));
					listAllConditions.add(EntityCondition.makeCondition("productPromoRuleId2", null));
					listAllConditions.add(EntityCondition.makeCondition("partyId2", null));
					listAllConditions.add(EntityCondition.makeCondition("fromDate2", null));
					if (UtilValidate.isEmpty(listSortFields)) {
						listSortFields.add("productPromoId");
						listSortFields.add("productPromoRuleId");
					}
					opts.setDistinct(true);
					listIterator = delegator.find("PromoExtRegisterNeedSettlement", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoExtPromoNeedSettleByRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoSettlementDetail(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String promoSettlementId = null;
    		if (parameters.containsKey("promoSettlementId") && parameters.get("promoSettlementId").length > 0) {
    			promoSettlementId = parameters.get("promoSettlementId")[0];
    		}
			if (UtilValidate.isNotEmpty(promoSettlementId)) {
				listAllConditions.add(EntityCondition.makeCondition("promoSettlementId", promoSettlementId));
				if (SalesPartyUtil.isSalessup(delegator, userLogin.getString("partyId"))) {
					List<String> distributorIds = PartyWorker.getDistributorIdsBySup(delegator, userLogin.getString("partyId"));
					listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, distributorIds));
				}
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("promoSettlementId");
					listSortFields.add("sellerId");
					listSortFields.add("customerId");
				}
				opts.setDistinct(true);
				listIterator = delegator.find("ProductPromoSettlementDetailMore", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoSettlementDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoSettlementResult(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PROMOSETTLEMENT_VIEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String promoSettlementId = null;
    		if (parameters.containsKey("promoSettlementId") && parameters.get("promoSettlementId").length > 0) {
    			promoSettlementId = parameters.get("promoSettlementId")[0];
    		}
    		if (UtilValidate.isNotEmpty(promoSettlementId)) {
    			listAllConditions.add(EntityCondition.makeCondition("promoSettlementId", promoSettlementId));
    			if (SalesPartyUtil.isSalessup(delegator, userLogin.getString("partyId"))) {
    				List<String> distributorIds = PartyWorker.getDistributorIdsBySup(delegator, userLogin.getString("partyId"));
    				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, distributorIds));
    			}
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("partyId");
    				listSortFields.add("productId");
    				listSortFields.add("promoSettlementId");
    			}
    			opts.setDistinct(true);
    			listIterator = delegator.find("ProductPromoSettlementResultDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoSettlementResult service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListOrderSettlementCommitment(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean isRoleEmployee = false;
			boolean hasPermission = security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_VIEW", userLogin);
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String orderId = null;
    		if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
    			orderId = parameters.get("orderId")[0];
    		}
    		if (UtilValidate.isNotEmpty(orderId)) {
    			listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
    			listIterator = delegator.find("OrderSettlementCommitmentDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderSettlementCommitment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListOrderItem(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String orderId = null;
    		if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
    			orderId = parameters.get("orderId")[0];
    		}
    		listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
    		listAllConditions.add(EntityCondition.makeCondition("orderItemTypeId", "PRODPROMO_ORDER_ITEM"));
    		listIterator = delegator.find("OrderItemAndProduct", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListPromoSettlementResultForReturn(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String partyId = null;
			String productId = null;
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}
			if (parameters.containsKey("productId") && parameters.get("productId").length > 0) {
				productId = parameters.get("productId")[0];
			}
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("productId", productId));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PSETTLE_APPROVED"));
			listAllConditions.add(EntityCondition.makeCondition("quantityRemain", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
			//listIterator = delegator.find("ProductPromoSettlementResultDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			listIterator = delegator.find("PromoSettleResultOrderCommit", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoSettlementResult service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addItemToPromoSettlement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String promoSettlementId = (String) context.get("promoSettlementId");
		List<Object> listItemsParam = (List<Object>) context.get("listItems");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		try {
			// check permission
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PROMOSETTLEMENT_ADDITEM");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
			}
			
			GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
			if (promoSettlement == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPromoSettlementHasIdIsNotFound", locale));
			
			String productPromoId = promoSettlement.getString("productPromoId");
			String productPromoExtId = promoSettlement.getString("productPromoExtId");
			if (UtilValidate.isNotEmpty(productPromoId)) {
				// process data
		    	boolean isJson = false;
		    	if (UtilValidate.isNotEmpty(listItemsParam) && listItemsParam.size() > 0){
		    		if (listItemsParam.get(0) instanceof String) isJson = true;
		    	}
		    	List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
		    	if (isJson){
					String listItemsStr = "[" + (String) listItemsParam.get(0) + "]";
					JSONArray jsonArray = new JSONArray();
					if (UtilValidate.isNotEmpty(listItemsStr)) {
						jsonArray = JSONArray.fromObject(listItemsStr);
					}
					if (jsonArray != null && jsonArray.size() > 0) {
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject itemObj = jsonArray.getJSONObject(i);
							Map<String, Object> item = FastMap.newInstance();
							item.put("orderId", itemObj.getString("orderId"));
							item.put("orderItemSeqId", itemObj.getString("orderItemSeqId"));
							String quantityApproveStr = (String) itemObj.getString("quantityApprove");
							if (UtilValidate.isNotEmpty(quantityApproveStr)) {
								try {
									BigDecimal quantityApprove = (BigDecimal) ObjectType.simpleTypeConvert(quantityApproveStr, "BigDecimal", null, locale);
									item.put("quantityApprove", quantityApprove);
								} catch (Exception e) {
					                Debug.logWarning(e, "Problems parsing quantity string: " + quantityApproveStr, module);
					            }
							}
							item.put("isPay", itemObj.getString("isPay"));
							item.put("comment", itemObj.getString("comment"));
							
							listItems.add(item);
						}
					}
		    	} else {
		    		listItems = (List<Map<String, Object>>) context.get("listItems");
		    	}
		    	
		    	// store data
		    	List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		    	for (Map<String, Object> item : listItems) {
		    		String orderId = (String) item.get("orderId");
		    		String orderItemSeqId = (String) item.get("orderItemSeqId");
		    		String isPay = (String) item.get("isPay");
		    		if (!"Y".equals(isPay)) isPay = "N";
		    		BigDecimal quantityApprove = (BigDecimal) item.get("quantityApprove");
		    		String comment = (String) item.get("comment");
		    		GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
		    		if (orderItem != null) {
		    			String customerId = null;
		    			GenericValue customerRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false));
		    			if (customerRole != null) customerId = customerRole.getString("partyId");
		    			String sellerId = null;
		    			GenericValue sellerRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"), null, false));
		    			if (sellerRole != null) sellerId = sellerRole.getString("partyId");
		    			
		    			GenericValue promoAdjustment = EntityUtil.getFirst(delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"), null, false));
		    			
		    			BigDecimal quantityItem = orderItem.getBigDecimal("quantity");
		    			BigDecimal quantityCancelItem = orderItem.getBigDecimal("cancelQuantity");
		    			BigDecimal quantity = quantityItem;
		    			if (quantityItem != null) {
		    				if (quantityCancelItem != null) quantity = quantityItem.subtract(quantityCancelItem);
		    			} else {
		    				quantity = BigDecimal.ZERO;
		    			}
		    			GenericValue promoSettlementItem = EntityUtil.getFirst(delegator.findByAnd("ProductPromoSettlementDetail", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false));
		    			if (promoSettlementItem != null) {
		    				// update quantity, quantityApprove, isPay and comment
		    				promoSettlementItem.put("quantity", quantity);
		    				promoSettlementItem.put("quantityApprove", quantityApprove);
		    				promoSettlementItem.put("isPay", isPay);
		    				promoSettlementItem.put("comment", comment);
		    				promoSettlementItem.put("sellerId", sellerId);
		    				promoSettlementItem.put("customerId", customerId);
		    				promoSettlementItem.put("productPromoId", promoAdjustment.get("productPromoId"));
		    				promoSettlementItem.put("productPromoRuleId", promoAdjustment.get("productPromoRuleId"));
		    				promoSettlementItem.put("productPromoActionSeqId", promoAdjustment.get("productPromoActionSeqId"));
		    				promoSettlementItem.put("productId", orderItem.getString("productId"));
		    			} else {
		    				promoSettlementItem = delegator.makeValue("ProductPromoSettlementDetail");
		    				String promoSettlementDetailId = delegator.getNextSeqId("ProductPromoSettlementDetail");
		    				promoSettlementItem.put("promoSettlementDetailId", promoSettlementDetailId);
		    				promoSettlementItem.put("promoSettlementId", promoSettlementId);
		    				promoSettlementItem.put("isPay", isPay);
		    				promoSettlementItem.put("quantity", quantity);
		    				promoSettlementItem.put("quantityApprove", quantityApprove);
		    				promoSettlementItem.put("comment", comment);
		    				promoSettlementItem.put("orderId", orderId);
		    				promoSettlementItem.put("orderItemSeqId", orderItemSeqId);
		    				promoSettlementItem.put("sellerId", sellerId);
		    				promoSettlementItem.put("customerId", customerId);
		    				promoSettlementItem.put("productPromoId", promoAdjustment.get("productPromoId"));
		    				promoSettlementItem.put("productPromoRuleId", promoAdjustment.get("productPromoRuleId"));
		    				promoSettlementItem.put("productPromoActionSeqId", promoAdjustment.get("productPromoActionSeqId"));
		    				promoSettlementItem.put("createdDate", nowTimeStamp);
		    				promoSettlementItem.put("createdBy", userLogin.get("userLoginId"));
		    				promoSettlementItem.put("productId", orderItem.getString("productId"));
		    			}
		    			tobeStored.add(promoSettlementItem);
		    		}
		    	}
		    	delegator.storeAll(tobeStored);
			} else if (UtilValidate.isNotEmpty(productPromoExtId)) {
				// process data
		    	boolean isJson = false;
		    	if (UtilValidate.isNotEmpty(listItemsParam) && listItemsParam.size() > 0){
		    		if (listItemsParam.get(0) instanceof String) isJson = true;
		    	}
		    	List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
		    	if (isJson){
					String listItemsStr = "[" + (String) listItemsParam.get(0) + "]";
					JSONArray jsonArray = new JSONArray();
					if (UtilValidate.isNotEmpty(listItemsStr)) {
						jsonArray = JSONArray.fromObject(listItemsStr);
					}
					if (jsonArray != null && jsonArray.size() > 0) {
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject itemObj = jsonArray.getJSONObject(i);
							Map<String, Object> item = FastMap.newInstance();
							item.put("productPromoId", productPromoExtId);
							item.put("productPromoRuleId", itemObj.getString("productPromoRuleId"));
							item.put("partyId", itemObj.getString("partyId"));
							String fromDateStr = (String) itemObj.get("fromDate");
							Timestamp fromDate = null;
					        try {
						        if (UtilValidate.isNotEmpty(fromDateStr)) {
						        	Long fromDateL = Long.parseLong(fromDateStr);
						        	fromDate = new Timestamp(fromDateL);
						        }
					        } catch (Exception e) {
					        	Debug.logWarning(e, "Problems parsing fromDate string: " + fromDate, module);
					        }
					        item.put("fromDate", fromDate);
					        item.put("isPay", itemObj.getString("isPay"));
							item.put("comment", itemObj.getString("comment"));
							listItems.add(item);
						}
					}
		    	} else {
		    		listItems = (List<Map<String, Object>>) context.get("listItems");
		    	}
		    	
		    	// store data
		    	List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		    	for (Map<String, Object> item : listItems) {
		    		//String productPromoId = (String) item.get("productPromoId");
		    		String productPromoRuleId = (String) item.get("productPromoRuleId");
		    		String customerId = (String) item.get("partyId");
		    		Timestamp fromDate = (Timestamp) item.get("fromDate");
		    		String isPay = (String) item.get("isPay");
		    		if (!"Y".equals(isPay)) isPay = "N";
		    		String comment = (String) item.get("comment");
		    		GenericValue promoRegisterItem = delegator.findOne("ProductPromoExtRegister", 
		    				UtilMisc.toMap("productPromoId", productPromoExtId, "productPromoRuleId", productPromoRuleId, "partyId", customerId, "fromDate", fromDate), false);
		    		if (promoRegisterItem != null) {
		    			String sellerId = PartyWorker.getDistributorByCustomer(delegator, customerId);
		    			
		    			List<GenericValue> listPromoAction = delegator.findByAnd("ProductPromoExtAction", UtilMisc.toMap("productPromoId", productPromoExtId, "productPromoRuleId", productPromoRuleId), null, false);
		    			if (UtilValidate.isNotEmpty(listPromoAction)) {
		    				for (GenericValue promoAction : listPromoAction) {
		    					GenericValue productAction = EntityUtil.getFirst(delegator.findByAnd("ProductPromoExtProduct", 
		    							UtilMisc.toMap("productPromoId", productPromoExtId, "productPromoRuleId", productPromoRuleId, 
		    									"productPromoActionSeqId", promoAction.get("productPromoActionSeqId"), 
		    									"productPromoCondSeqId", "_NA_"), null, false));
		    					String productIdAction = productAction != null ? productAction.getString("productId") : null;
		    					BigDecimal quantity = promoAction.getBigDecimal("quantity");
		    					BigDecimal amount = promoAction.getBigDecimal("amount");
		    					
		    					GenericValue promoSettlementItem = EntityUtil.getFirst(delegator.findByAnd("ProductPromoSettlementDetail", 
		    							UtilMisc.toMap("productPromoId", productPromoExtId, "productPromoRuleId", productPromoRuleId, 
		    									"productPromoActionSeqId", promoAction.get("productPromoActionSeqId"), "customerId", customerId, "fromDate", fromDate), null, false));
				    			if (promoSettlementItem != null) {
				    				// update quantity, quantityApprove, isPay and comment
				    				promoSettlementItem.put("quantity", quantity);
				    				promoSettlementItem.put("quantityApprove", quantity);
				    				promoSettlementItem.put("amount", amount);
				    				promoSettlementItem.put("isPay", isPay);
				    				promoSettlementItem.put("comment", comment);
				    				promoSettlementItem.put("sellerId", sellerId);
				    				promoSettlementItem.put("customerId", customerId);
				    				promoSettlementItem.put("productPromoId", productPromoExtId);
				    				promoSettlementItem.put("productPromoRuleId", productPromoRuleId);
				    				promoSettlementItem.put("productPromoActionSeqId", promoAction.get("productPromoActionSeqId"));
				    				promoSettlementItem.put("productId", productIdAction);
				    			} else {
				    				promoSettlementItem = delegator.makeValue("ProductPromoSettlementDetail");
				    				String promoSettlementDetailId = delegator.getNextSeqId("ProductPromoSettlementDetail");
				    				promoSettlementItem.put("promoSettlementDetailId", promoSettlementDetailId);
				    				promoSettlementItem.put("promoSettlementId", promoSettlementId);
				    				promoSettlementItem.put("isPay", isPay);
				    				promoSettlementItem.put("quantity", quantity);
				    				promoSettlementItem.put("quantityApprove", quantity);
				    				promoSettlementItem.put("amount", amount);
				    				promoSettlementItem.put("amountApprove", amount);
				    				promoSettlementItem.put("comment", comment);
				    				promoSettlementItem.put("orderId", null);
				    				promoSettlementItem.put("orderItemSeqId", null);
				    				promoSettlementItem.put("sellerId", sellerId);
				    				promoSettlementItem.put("customerId", customerId);
				    				promoSettlementItem.put("fromDate", fromDate);
				    				promoSettlementItem.put("productPromoId", productPromoExtId);
				    				promoSettlementItem.put("productPromoRuleId", productPromoRuleId);
				    				promoSettlementItem.put("productPromoActionSeqId", promoAction.get("productPromoActionSeqId"));
				    				promoSettlementItem.put("createdDate", nowTimeStamp);
				    				promoSettlementItem.put("createdBy", userLogin.get("userLoginId"));
				    				promoSettlementItem.put("productId", productIdAction);
				    			}
				    			tobeStored.add(promoSettlementItem);
		    				}
		    			}
		    			
		    		}
		    	}
		    	delegator.storeAll(tobeStored);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling addItemToPromoSettlement service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		successResult.put("promoSettlementId", promoSettlementId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateNumberApproveSettleResult(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String promoSettlementId = (String) context.get("promoSettlementId");
		List<Object> listItemsParam = (List<Object>) context.get("listItems");
		try {
			// check permission
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "UPDATE", "MODULE", "PROMOSETTLEMENT_EDIT");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
			}
			
			GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
			if (promoSettlement == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPromoSettlementHasIdIsNotFound", locale));
			
			// process data
			boolean isJson = false;
			if (UtilValidate.isNotEmpty(listItemsParam) && listItemsParam.size() > 0){
				if (listItemsParam.get(0) instanceof String) isJson = true;
			}
			List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
			if (isJson){
				String listItemsStr = "[" + (String) listItemsParam.get(0) + "]";
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(listItemsStr)) {
					jsonArray = JSONArray.fromObject(listItemsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject itemObj = jsonArray.getJSONObject(i);
						Map<String, Object> item = FastMap.newInstance();
						item.put("promoSettlementResultId", itemObj.getString("promoSettlementResultId"));
						String quantityApproveStr = null;
						String amountApproveStr = null;
						if (!"null".equals(itemObj.getString("quantityApprove"))) quantityApproveStr = (String) itemObj.getString("quantityApprove");
						if (!"null".equals(itemObj.getString("amountApprove"))) amountApproveStr = (String) itemObj.getString("amountApprove");
						try {
							if (UtilValidate.isNotEmpty(quantityApproveStr)) {
								BigDecimal quantityApprove = (BigDecimal) ObjectType.simpleTypeConvert(quantityApproveStr, "BigDecimal", null, locale);
								item.put("quantityApprove", quantityApprove);
							}
							if (UtilValidate.isNotEmpty(amountApproveStr)) {
								BigDecimal amountApprove = (BigDecimal) ObjectType.simpleTypeConvert(amountApproveStr, "BigDecimal", null, locale);
								item.put("amountApprove", amountApprove);
							}
						} catch (Exception e) {
							Debug.logWarning(e, "Problems parsing quantity string!", module);
						}
						String comment = itemObj.get("comment") != null ? itemObj.getString("comment") : null;
						item.put("comment", comment);
						
						listItems.add(item);
					}
				}
			} else {
				listItems = (List<Map<String, Object>>) context.get("listItems");
			}
			
			// store data
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			for (Map<String, Object> item : listItems) {
				String promoSettlementResultId = (String) item.get("promoSettlementResultId");
				BigDecimal quantityApprove = (BigDecimal) item.get("quantityApprove");
				BigDecimal amountApprove = (BigDecimal) item.get("amountApprove");
				String comment = (String) item.get("comment");
				GenericValue promoSettlementResult = delegator.findOne("ProductPromoSettlementResult", UtilMisc.toMap("promoSettlementResultId", promoSettlementResultId), false);
				if (promoSettlementResult != null) {
					promoSettlementResult.put("quantityApprove", quantityApprove);
					promoSettlementResult.put("amountApprove", amountApprove);
					promoSettlementResult.put("comment", comment);
					tobeStored.add(promoSettlementResult);
				}
			}
			delegator.storeAll(tobeStored);
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateNumberApproveSettleResult service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("promoSettlementId", promoSettlementId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateNumberAcceptSettleResult(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = "";
		
		String promoSettlementId = (String) context.get("promoSettlementId");
		List<Object> listItemsParam = (List<Object>) context.get("listItems");
		try {
			// check permission
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "UPDATE", "MODULE", "PROMOSETTLEMENT_EDIT");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
			}
			
			GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
			if (promoSettlement == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPromoSettlementHasIdIsNotFound", locale));
			
			// process data
			boolean isJson = false;
			if (UtilValidate.isNotEmpty(listItemsParam) && listItemsParam.size() > 0){
				if (listItemsParam.get(0) instanceof String) isJson = true;
			}
			List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
			if (isJson){
				String listItemsStr = "[" + (String) listItemsParam.get(0) + "]";
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(listItemsStr)) {
					jsonArray = JSONArray.fromObject(listItemsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject itemObj = jsonArray.getJSONObject(i);
						Map<String, Object> item = FastMap.newInstance();
						item.put("promoSettlementResultId", itemObj.getString("promoSettlementResultId"));
						String quantityAcceptStr = (String) itemObj.getString("quantityAccept");
						String amountAcceptStr = (String) itemObj.getString("amountAccept");
						String productId = (String) itemObj.getString("productId");
						partyId = (String) itemObj.getString("partyId");
						item.put("productId", productId);
						item.put("partyId", partyId);
						BigDecimal price = BigDecimal.ZERO;
						BigDecimal quantityAccept = BigDecimal.ZERO;
						try {
							if (UtilValidate.isNotEmpty(quantityAcceptStr)) {
								quantityAccept = (BigDecimal) ObjectType.simpleTypeConvert(quantityAcceptStr, "BigDecimal", null, locale);
								item.put("quantityAccept", quantityAccept);
							}
							// Process by VietTB							
							if (UtilValidate.isNotEmpty(productId))
							{
								GenericValue product = null;
								product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					        	Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("product", product);
					        	Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
					        	if (ServiceUtil.isError(resultCalPrice)) {
					        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));				        		
					        	}
					        	price = (BigDecimal) resultCalPrice.get("price");
					        	item.put("unitPriceAccept", price);
							}
							
							if (UtilValidate.isNotEmpty(amountAcceptStr)) {
								BigDecimal amountAccept = (BigDecimal) ObjectType.simpleTypeConvert(amountAcceptStr, "BigDecimal", null, locale);
								item.put("amountAccept", amountAccept);
							}
							else item.put("amountAccept", quantityAccept.multiply(price));
						} catch (Exception e) {
							Debug.logWarning(e, "Problems parsing quantity string!", module);
						}
						item.put("comment", itemObj.getString("comment"));
						
						listItems.add(item);
					}
				}
			} else {
				listItems = (List<Map<String, Object>>) context.get("listItems");
			}
			
			// store data
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			for (Map<String, Object> item : listItems) {
				String promoSettlementResultId = (String) item.get("promoSettlementResultId");
				BigDecimal quantityAccept = (BigDecimal) item.get("quantityAccept");
				BigDecimal amountAccept = (BigDecimal) item.get("amountAccept");
				BigDecimal unitPriceAccept = (BigDecimal) item.get("unitPriceAccept");
				String comment = (String) item.get("comment");
				GenericValue promoSettlementResult = delegator.findOne("ProductPromoSettlementResult", UtilMisc.toMap("promoSettlementResultId", promoSettlementResultId), false);
				if (promoSettlementResult != null) {
					promoSettlementResult.put("quantityAccept", quantityAccept);
					promoSettlementResult.put("amountAccept", amountAccept);
					promoSettlementResult.put("comment", comment);
					promoSettlementResult.put("unitPriceAccept", unitPriceAccept);
					tobeStored.add(promoSettlementResult);
				}
			}
			delegator.storeAll(tobeStored);
			
			// Create Invoice by VietTB
			GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", organizationPartyId), false);
			String baseCurrencyUomId = (String) partyAcctgPreference.get("baseCurrencyUomId");
			String invoiceDescription = UtilProperties.getMessage(resources, "InvoiceForSalesSettlement", locale);
			String listPartyId = "";
			for (int i = 0; i < listItems.size(); i ++) {
				Map<String, Object> item = listItems.get(i);
				String invPartyId = (String) item.get("partyId");
				if ( !listPartyId.contains(invPartyId))
				{
					listPartyId = listPartyId + ";" + invPartyId;
					Map<String, Object> input = UtilMisc.<String, Object>toMap("invoiceTypeId", "SETTLEMENT_INVOICE", "statusId", "INVOICE_IN_PROCESS");
					
					input.put("partyId", organizationPartyId);
					input.put("partyIdFrom", invPartyId);
		            input.put("currencyUomId", baseCurrencyUomId);
		            input.put("invoiceDate", UtilDateTime.nowTimestamp());
		            input.put("description", invoiceDescription);
		            input.put("userLogin", userLogin);
		            // call the service to create the invoice
		            Map<String, Object> serviceResults = dispatcher.runSync("createInvoice", input);
		            if (ServiceUtil.isError(serviceResults)) {
		                return ServiceUtil.returnError("OLBIUS: Create invoice from settlement sales error", null, null, serviceResults);
		            }
		            String invoiceId = (String) serviceResults.get("invoiceId");	
		            // call the service to create the invoice item
	                input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", "ITM_PROMPRO_ADJ");
	                input.put("amount", (BigDecimal) item.get("unitPriceAccept"));
	                input.put("quantity", (BigDecimal) item.get("quantityAccept"));
	                input.put("productId", (String) item.get("productId"));
					GenericValue product = null;
					product = delegator.findOne("Product", UtilMisc.toMap("productId", (String) item.get("productId")), false);
	                input.put("taxableFlag", product.get("taxable"));
	                input.put("description", product.get("productName"));
	                input.put("userLogin", userLogin);
	                serviceResults = dispatcher.runSync("createInvoiceItem", input);
	                if (ServiceUtil.isError(serviceResults)) {
	                    return ServiceUtil.returnError("OLBIUS: create invoice item error", null, null, serviceResults);
	                }
		            for (int j = i + 1; j < listItems.size(); j++)
		            {
		            	Map<String, Object> itemMap = listItems.get(j);
						String itemPartyId = (String) itemMap.get("partyId");
						if (itemPartyId.equals(invPartyId))
						{
							input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", "ITM_PROMPRO_ADJ");
			                input.put("amount", (BigDecimal) itemMap.get("unitPriceAccept"));
			                input.put("quantity", (BigDecimal) itemMap.get("quantityAccept"));
			                input.put("productId", (String) itemMap.get("productId"));
							product = null;
							product = delegator.findOne("Product", UtilMisc.toMap("productId", (String) itemMap.get("productId")), false);
			                input.put("taxableFlag", product.get("taxable"));
			                input.put("description", product.get("productName"));
			                input.put("userLogin", userLogin);
			                serviceResults = dispatcher.runSync("createInvoiceItem", input);
			                if (ServiceUtil.isError(serviceResults)) {
			                    return ServiceUtil.returnError("OLBIUS: create invoice item error", null, null, serviceResults);
			                }
						}
		            }
		         // Set the invoice to READY
		            serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
		            if (ServiceUtil.isError(serviceResults)) {
		                return ServiceUtil.returnError("OLBIUS: setInvoiceStatus error", null, null, serviceResults);
		            }
				}
			}
			
			
			
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateNumberAcceptSettleResult service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("promoSettlementId", promoSettlementId);
		return successResult;
	}
	
	public static Map<String, Object> calculatePromoSettlement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String promoSettlementId = (String) context.get("promoSettlementId");
		try {
			// check permission
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "UPDATE", "MODULE", "PROMOSETTLEMENT_EDIT");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
			}
			
			GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
			if (promoSettlement == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPromoSettlementHasIdIsNotFound", locale));
			
			List<GenericValue> listSettlementResultsRemove = delegator.findByAnd("ProductPromoSettlementResult", UtilMisc.toMap("promoSettlementId", promoSettlementId), null, false);
			if (UtilValidate.isNotEmpty(listSettlementResultsRemove)) {
				// delete all
				delegator.removeAll(listSettlementResultsRemove);
			}
			
			List<GenericValue> listPromoSettlementDetails = delegator.findByAnd("ProductPromoSettlementDetail", UtilMisc.toMap("promoSettlementId", promoSettlementId, "isPay", "Y"), null, false);
			if (listPromoSettlementDetails != null) {
				for (GenericValue promoSettlementDetail : listPromoSettlementDetails) {
					String productPromoId = promoSettlementDetail.getString("productPromoId");
					String productPromoRuleId = promoSettlementDetail.getString("productPromoRuleId");
					String productPromoActionSeqId = promoSettlementDetail.getString("productPromoActionSeqId");
					String partyId = promoSettlementDetail.getString("sellerId");
					String productId = promoSettlementDetail.getString("productId");
					
					GenericValue settlementResultNew = EntityUtil.getFirst(delegator.findByAnd("ProductPromoSettlementResult", 
							UtilMisc.toMap("promoSettlementId", promoSettlementId, 
									"productPromoId", productPromoId, 
									"productPromoRuleId", productPromoRuleId, 
									"productPromoActionSeqId", productPromoActionSeqId, 
									"partyId", partyId, 
									"productId", productId), null, false));
					if (UtilValidate.isNotEmpty(settlementResultNew)) {
						// update
						BigDecimal quantity = settlementResultNew.getBigDecimal("quantity");
						//BigDecimal amount = settlementResultNew.getBigDecimal("amount");
						if (quantity == null) {
							quantity = BigDecimal.ZERO;
						}
						if (UtilValidate.isNotEmpty(promoSettlementDetail.get("quantityApprove"))) 
							quantity = quantity.add(promoSettlementDetail.getBigDecimal("quantityApprove"));
						settlementResultNew.put("quantity", quantity);
						delegator.store(settlementResultNew);
					} else {
						// create
						settlementResultNew = delegator.makeValue("ProductPromoSettlementResult", 
								UtilMisc.toMap("promoSettlementId", promoSettlementId, 
									"productPromoId", productPromoId, 
									"productPromoRuleId", productPromoRuleId, 
									"productPromoActionSeqId", productPromoActionSeqId, 
									"partyId", partyId, 
									"productId", productId));
						settlementResultNew.put("promoSettlementResultId", delegator.getNextSeqId("ProductPromoSettlementResult"));
						settlementResultNew.put("quantity", promoSettlementDetail.get("quantityApprove"));
						delegator.create(settlementResultNew);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling calculatePromoSettlement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("promoSettlementId", promoSettlementId);
		return successResult;
	}
	
	public static Map<String, Object> addItemOrderSettlementCommitment(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String orderSettlementCommitId = null;
		try {
			// check permission
			boolean hasPermission = security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_CREATE", userLogin);
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission",locale));
			}
			
			String orderId = (String) context.get("orderId");
			String orderItemSeqId = (String) context.get("orderItemSeqId");
			String promoSettlementResultId = (String) context.get("promoSettlementResultId");
			orderSettlementCommitId = delegator.getNextSeqId("OrderSettlementCommitment");
			GenericValue orderSettlementCommitment = delegator.makeValue("OrderSettlementCommitment");
			orderSettlementCommitment.put("orderSettlementCommitId", orderSettlementCommitId);
			orderSettlementCommitment.put("orderId", orderId);
			orderSettlementCommitment.put("orderItemSeqId", orderItemSeqId);
			orderSettlementCommitment.put("promoSettlementResultId", promoSettlementResultId);
			delegator.create(orderSettlementCommitment);
		} catch (Exception e) {
			String errMsg = "Fatal error calling addItemOrderSettlementCommitment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("orderSettlementCommitId", orderSettlementCommitId);
		return successResult;
	}
	
	public static Map<String, Object> removeItemOrderSettlementCommitment(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String orderSettlementCommitId = (String) context.get("orderSettlementCommitId");
		try {
			// check permission
			boolean hasPermission = security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_DELETE", userLogin);
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotDeletePermission",locale));
			}
			
			GenericValue orderSettlementCommitment = delegator.findOne("OrderSettlementCommitment", UtilMisc.<String, Object>toMap("orderSettlementCommitId", orderSettlementCommitId), false);
			if (orderSettlementCommitment == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordHasIdIsNotFound", UtilMisc.toList(orderSettlementCommitId), locale));
			}
			
			delegator.removeValue(orderSettlementCommitment);
		} catch (Exception e) {
			String errMsg = "Fatal error calling removeItemOrderSettlementCommitment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> sendNotifyWhenCreatePromoSettlement(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String promoSettlementId = (String) context.get("promoSettlementId");
    	try {
    		GenericValue promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
    		if (promoSettlement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordHasIdIsNotFound", UtilMisc.toList(promoSettlementId), locale));
    		
        	NotificationWorker.sendNotifyWhenCreatePromoSettlement(delegator, dispatcher, locale, promoSettlement, userLogin);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyWhenCreatePromoSettlement service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
}
