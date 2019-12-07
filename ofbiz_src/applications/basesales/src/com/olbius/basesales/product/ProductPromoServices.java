package com.olbius.basesales.product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.olbius.basesales.party.PartyWorker;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;

public class ProductPromoServices {
	public static final String module = ProductPromoServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
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
			boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_VIEW");
			boolean hasUpdatePermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EDIT") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_APPROVE");
			String ownerId = null;
			if (hasPermission) {
				showData = true;
				isRoleEmployee = true;
				ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			} else {
				hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PRODPROMOTION");
			}
			if (!hasPermission) {
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
	        }
			String userLoginPartyId = userLogin.getString("partyId");
			String customerId = null;
    		if (parameters.containsKey("isCustomer") && parameters.get("isCustomer").length > 0) {
    			String isCustomer = parameters.get("isCustomer")[0];
    			if ("Y".equals(isCustomer)) {
    				customerId = userLoginPartyId;
    			}
			}
    		if (parameters.containsKey("isOwner") && parameters.get("isOwner").length > 0) {
    			String isOwner = parameters.get("isOwner")[0];
    			if ("Y".equals(isOwner)) {
    				ownerId = userLoginPartyId;
    			}
			}
    		if (parameters.containsKey("_statusId") && parameters.get("_statusId").length > 0) {
    			String _statusId = parameters.get("_statusId")[0];
    			if (UtilValidate.isNotEmpty(_statusId)) {
        			listAllConditions.add(EntityCondition.makeCondition("statusId", _statusId));
        		}
    		}
    		if (parameters.containsKey("_productPromoTypeId") && parameters.get("_productPromoTypeId").length >0){
    			String _productPromoTypeId = parameters.get("_productPromoTypeId")[0];
    			if(UtilValidate.isNotEmpty(_productPromoTypeId)){
    				listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", _productPromoTypeId));
    			}
    		}
    		
    		boolean jumpOnCheckOwner = false;
    		// get list by product store
    		String productStoreId = null;
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			productStoreId = parameters.get("productStoreId")[0];
    			if (UtilValidate.isNotEmpty(productStoreId)) {
        			GenericValue productRoleSeller = EntityUtil.getFirst(
        					delegator.findList("ProductStoreRoleDetail", 
        							EntityCondition.makeCondition(
        									EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "SELLER", "productStoreId", productStoreId)), 
        									EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false));
        			if (productRoleSeller != null) {
        				listAllConditions.add(EntityCondition.makeCondition("productStoreId", productRoleSeller.get("productStoreId")));
        				jumpOnCheckOwner = true;
        				showData = true;
        			}
        		}
    		}
    		if (parameters.containsKey("checkActive") && parameters.get("checkActive").length > 0) {
    			String checkActiveStr = parameters.get("checkActive")[0];
    			if ("Y".equals(checkActiveStr)) {
    				Timestamp fromDate = null;
    				Timestamp thruDate = null;
    				try {
	    				if (parameters.containsKey("fromDate") && parameters.get("fromDate").length > 0) {
	    					String fromDateStr = parameters.get("fromDate")[0];
	    					if (UtilValidate.isNotEmpty(fromDateStr)) {
					        	fromDate = new Timestamp(Long.parseLong(fromDateStr));
					        }
	    				}
	    				if (parameters.containsKey("thruDate") && parameters.get("thruDate").length > 0) {
	    					String thruDateStr = parameters.get("thruDate")[0];
	    					if (UtilValidate.isNotEmpty(thruDateStr)) {
	    						thruDate = new Timestamp(Long.parseLong(thruDateStr));
	    					}
	    				}
			        } catch (Exception e) {
			        	Debug.logWarning(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale), module);
			        }
    				listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
    				listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", "SALES_PROMO"));
    				if (fromDate == null && thruDate == null) {
    					listAllConditions.add(EntityUtil.getFilterByDateExpr());
    				} else {
    					if (fromDate != null) {
    						listAllConditions.add(EntityCondition.makeCondition(
    				                EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null),
    				                EntityOperator.OR,
    				                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate)
    							));
    					}
    					if (thruDate != null) {
    						listAllConditions.add(EntityCondition.makeCondition(
    				                EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
    				                EntityOperator.OR,
    				                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, thruDate)
								));
    					}
    				}
    			}
    		}
    		if (parameters.containsKey("isSeller") && parameters.get("isSeller").length > 0) {
    			String isSellerStr = parameters.get("isSeller")[0];
    			if ("Y".equals(isSellerStr)) {
    				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, true, null, null, null, null), "productStoreId", true);
    				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
    				jumpOnCheckOwner = true;
    				showData = true;
    			}
    		}
    		
    		if (UtilValidate.isNotEmpty(customerId)) {
    			List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyId", customerId));
    			listConds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
    			if (UtilValidate.isNotEmpty(productStoreId)) listConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			listConds.add(EntityUtil.getFilterByDateExpr());
    			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
    			if (UtilValidate.isNotEmpty(productStoreIds)) {
    				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
    				showData = true;
    			}
    			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDateAppl", "thruDateAppl"));
    		} else if (!jumpOnCheckOwner){
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payToPartyId", ownerId), EntityOperator.OR, EntityCondition.makeCondition("organizationPartyId", ownerId)));
				showData = true;
    		}
    		if (!isRoleEmployee || !hasUpdatePermission) listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
    		if (!isRoleEmployee || !hasUpdatePermission) listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", "SALES_PROMO"));
    		if (showData) {
    			if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-createdDate");
				}
    			Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productPromoId");
				listSelectFields.add("promoName");
				listSelectFields.add("createdDate");
				listSelectFields.add("fromDate");
				listSelectFields.add("thruDate");
				listSelectFields.add("statusId");
				listSelectFields.add("productPromoTypeId");
				listSelectFields.add("stateId");
				listSelectFields.add("organizationPartyId");
				listSelectFields.add("promoText");
				opts.setDistinct(true);
				listIterator = delegator.find("ProductPromoApplStore", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSelectFields, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoDisplay(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
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
			boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_VIEW");
			boolean hasUpdatePermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EDIT") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_APPROVE");
			String ownerId = null;
			if (hasPermission) {
				showData = true;
				isRoleEmployee = true;
				ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			} else {
				hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PRODPROMOTION");
			}
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission",locale));
			}
			String userLoginPartyId = userLogin.getString("partyId");
			String customerId = null;
			boolean isSearch = true;
			List<String> listProductStoreIds = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
				conds.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
			} else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
				isSearch = false;
				List<EntityCondition> condSalesAdmin = FastList.newInstance();
				condSalesAdmin.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "SELLER")));
				condSalesAdmin.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				List<GenericValue> listProductStoreByAdmins = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(condSalesAdmin), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductStoreByAdmins)) {
					for (GenericValue gv : listProductStoreByAdmins)
						listProductStoreIds.add(gv.getString("productStoreId"));
				}
			} else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
				//nothing
				isSearch = false;
			}else{
				isSearch = false;
			}
			List<String> listDistributorsId = FastList.newInstance();
			if (isSearch) {
				List<GenericValue> listDistributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition(conds), null, null, null, false);
				for(GenericValue gv: listDistributor)
					listDistributorsId.add(gv.getString("partyId"));
				conds.clear();
				if (UtilValidate.isNotEmpty(listDistributorsId))
					for(String s : listDistributorsId){
						conds.add(EntityCondition.makeCondition("partyId", s));
						conds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
						conds.add(EntityUtil.getFilterByDateExpr());
						List<GenericValue> listProductStores = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
						if (UtilValidate.isNotEmpty(listProductStores))
							for(GenericValue gv : listProductStores)
								if(!listProductStoreIds.contains(gv.getString("productStoreId")))
									listProductStoreIds.add(gv.getString("productStoreId"));
					}
			}
			String isCustomer = SalesUtil.getParameter(parameters, "isCustomer");
			if ("Y".equals(isCustomer)) {
				customerId = userLoginPartyId;
			}
			
			String isOwner = SalesUtil.getParameter(parameters, "isOwner");
			if ("Y".equals(isOwner)) {
				ownerId = userLoginPartyId;
			}
			
			String _statusId = SalesUtil.getParameter(parameters, "_statusId");
			if (UtilValidate.isNotEmpty(_statusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", _statusId));
			}
			
			boolean jumpOnCheckOwner = false;
			// check product store
			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			if (UtilValidate.isNotEmpty(productStoreId)) {
				GenericValue productRoleSeller = EntityUtil.getFirst(
						delegator.findList("ProductStoreRoleDetail", 
								EntityCondition.makeCondition(
										EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "SELLER", "productStoreId", productStoreId)), 
										EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false));
				if (productRoleSeller != null) {
					//listAllConditions.add(EntityCondition.makeCondition("productStoreId", productRoleSeller.get("productStoreId")));
					if(!listProductStoreIds.contains(productRoleSeller.getString("productStoreId")))
						listProductStoreIds.add(productRoleSeller.getString("productStoreId"));
					jumpOnCheckOwner = true;
					showData = true;
				}
			}
			// check promo's status is active
			String checkActiveStr = SalesUtil.getParameter(parameters, "checkActive");
			if ("Y".equals(checkActiveStr)) {
				Timestamp fromDate = null;
				Timestamp thruDate = null;
				try {
					if (parameters.containsKey("fromDate") && parameters.get("fromDate").length > 0) {
						String fromDateStr = parameters.get("fromDate")[0];
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
					}
					if (parameters.containsKey("thruDate") && parameters.get("thruDate").length > 0) {
						String thruDateStr = parameters.get("thruDate")[0];
						if (UtilValidate.isNotEmpty(thruDateStr)) {
							thruDate = new Timestamp(Long.parseLong(thruDateStr));
						}
					}
				} catch (Exception e) {
					Debug.logWarning(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale), module);
				}
				listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
				if (fromDate == null && thruDate == null) {
					listAllConditions.add(EntityUtil.getFilterByDateExpr());
				} else {
					if (fromDate != null) {
						listAllConditions.add(EntityCondition.makeCondition(
								EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null),
								EntityOperator.OR,
								EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate)
								));
					}
					if (thruDate != null) {
						listAllConditions.add(EntityCondition.makeCondition(
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
								EntityOperator.OR,
								EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, thruDate)
								));
					}
				}
			}
			// check permission is seller
			String isSellerStr = SalesUtil.getParameter(parameters, "isSeller");
			if ("Y".equals(isSellerStr)) {
				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, true, null, null, null, null), "productStoreId", true);
				//listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				for(String s : productStoreIds)
					if(!listProductStoreIds.contains(s))
						listProductStoreIds.add(s);
				jumpOnCheckOwner = true;
				showData = true;
			}
			
			if (UtilValidate.isNotEmpty(customerId)) {
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyId", customerId));
				listConds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
				listConds.add(EntityUtil.getFilterByDateExpr());
				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
				if (UtilValidate.isNotEmpty(productStoreIds)) {
					//listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
					for(String s : productStoreIds)
						if(!listProductStoreIds.contains(s))
							listProductStoreIds.add(s);
					showData = true;
				}
				listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDateAppl", "thruDateAppl"));
			} else if (!jumpOnCheckOwner){
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payToPartyId", ownerId), EntityOperator.OR, EntityCondition.makeCondition("organizationPartyId", ownerId)));
				showData = true;
			}
			if (UtilValidate.isNotEmpty(listProductStoreIds)){
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, listProductStoreIds));
			}
			if (!isRoleEmployee || !hasUpdatePermission) listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
			if (showData) {
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-createdDate");
				}
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productPromoId");
				listSelectFields.add("promoName");
				listSelectFields.add("createdDate");
				listSelectFields.add("fromDate");
				listSelectFields.add("thruDate");
				listSelectFields.add("statusId");
				listSelectFields.add("stateId");
				listSelectFields.add("organizationPartyId");
				listSelectFields.add("storeNames");
				opts.setDistinct(true);
				listIterator = delegator.find("ProductPromoApplStoreDisplay", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSelectFields, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoDisplay service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromoCode(DispatchContext ctx, Map<String, Object> context) {
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
			if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_VIEW")) {
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission",locale));
	        }
			String productPromoId = null;
    		if (parameters.containsKey("productPromoId") && parameters.get("productPromoId").length > 0) {
    			productPromoId = parameters.get("productPromoId")[0];
			}
    		if (UtilValidate.isNotEmpty(productPromoId)) {
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("-createdDate");
    			}
    			listAllConditions.add(EntityCondition.makeCondition("productPromoId", productPromoId));
    			// manual / user entered
    			// String manualOnly = context.get("manualOnly") // default = Y
    			// listAllConditions.add(EntityCondition.makeCondition("userEntered", manualOnly));
    			listIterator = delegator.find("ProductPromoCodeCountUse", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoCode service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> updateProductPromoCodeCustom(DispatchContext ctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSUpdateSuccessful", locale));
    	
    	String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	
    	String productPromoCodeId = (String) context.get("productPromoCodeId");
    	try {
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
            context.remove("fromDate");
            context.remove("thruDate");
            
            context.put("fromDate", fromDate);
            context.put("thruDate", thruDate);
            
            Map<String, Object> contextCtx = ServiceUtil.setServiceFields(dispatcher, "updateProductPromoCode", context, userLogin, null, locale);
            Map<String, Object> resultValue = dispatcher.runSync("updateProductPromoCode", contextCtx);
            if (ServiceUtil.isError(resultValue)) {
            	return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
            }
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling updateProductPromoCodeCustom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("productPromoCodeId", productPromoCodeId);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> jqGetListProductPromoCodeEmail(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String productPromoCodeId = null;
			if (parameters.containsKey("productPromoCodeId") && parameters.get("productPromoCodeId").length > 0) {
				productPromoCodeId = parameters.get("productPromoCodeId")[0];
			}
			if (UtilValidate.isNotEmpty(productPromoCodeId)) {
				listAllConditions.add(EntityCondition.makeCondition("productPromoCodeId", productPromoCodeId));
				listIterator = delegator.find("ProductPromoCodeEmail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoCodeEmail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> jqGetListProductPromoCodeParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String productPromoCodeId = SalesUtil.getParameter(parameters, "productPromoCodeId");
			if (UtilValidate.isNotEmpty(productPromoCodeId)) {
				listAllConditions.add(EntityCondition.makeCondition("productPromoCodeId", productPromoCodeId));
				listIterator = delegator.find("ProductPromoCodePartyDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoCodeParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> sendNotiChangePromoStatus(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String productPromoId = (String) context.get("productPromoId");
		try {
			Map<String, Object> resultValue = NotificationWorker.sendNotiWhenChangePromoStatus(delegator, dispatcher, locale, productPromoId, userLogin);
			if (ServiceUtil.isError(resultValue)) {
				return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling sendNotiChangePromoStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("productPromoId", productPromoId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPromosRuleByProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productId = (String) context.get("productId");
		String productStoreId = (String) context.get("productStoreId");
		List<String> productStoreIds = (List<String>) context.get("productStoreIds");
		String isGetAll = (String) context.get("isGetAll");
		String isGetFutureActive = (String) context.get("isGetFutureActive");
		List<GenericValue> listProductPromoRules = new ArrayList<GenericValue>();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			if (productStoreIds == null) productStoreIds = new ArrayList<String>();
			if (UtilValidate.isNotEmpty(productStoreId)) productStoreIds.add(productStoreId);
			
			if (UtilValidate.isEmpty(isGetAll) || "N".equals(isGetAll)) {
				if (productStoreIds.size() == 1) {
					listConds.add(EntityCondition.makeCondition("productStoreId", productStoreIds.get(0)));
				} else {
					listConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				}
			}
			
			Timestamp timePoint = (Timestamp) context.get("timePoint");
			if (timePoint != null) {
				listConds.add(EntityUtil.getFilterByDateExpr(timePoint));
			} else {
				if ("Y".equals(isGetFutureActive)) {
					listConds.add(EntityCondition.makeCondition(
			                EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			                EntityOperator.OR,
			                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)
			           ));
				} else {
					listConds.add(EntityUtil.getFilterByDateExpr());
				}
			}
			List<String> listPromoIdAppl = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStorePromoApplFilter", 
							EntityCondition.makeCondition(listConds), UtilMisc.toSet("productPromoId"), null, null, false), "productPromoId", true);
			
			List<String> productIds = FastList.newInstance();
			productIds.add(productId);
			
			// get current category
			List<GenericValue> listCurrentCategory = ProductWorker.getCurrentProductCategories(delegator, productId);
			if (listCurrentCategory == null) listCurrentCategory = new ArrayList<GenericValue>();
			GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
			if (parentProduct != null) {
				productIds.add(parentProduct.getString("productId"));
				List<GenericValue> listCurrentCategoryTmp = ProductWorker.getCurrentProductCategories(delegator, parentProduct.getString("productId"));
				if (listCurrentCategoryTmp != null) {
					listCurrentCategory.addAll(listCurrentCategoryTmp);
				}
			}
			
			// find product category rollup
			List<String> categoryIds = new ArrayList<String>();
			for (GenericValue currentCategory : listCurrentCategory) {
				categoryIds.add(currentCategory.getString("productCategoryId"));
				List<String> parentCategoryTmp = com.olbius.basesales.product.ProductWorker.getAllCategoryParentTree(delegator, currentCategory.getString("productCategoryId"), nowTimestamp);
				if (parentCategoryTmp != null) categoryIds.addAll(parentCategoryTmp);
			}
			
			List<GenericValue> totalRow = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productPromoId", EntityOperator.IN, listPromoIdAppl));
			conds.add(EntityCondition.makeCondition("productPromoActionSeqId", "_NA_"));
			conds.add(EntityCondition.makeCondition("productPromoApplEnumId", "PPPA_INCLUDE"));
			conds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
			List<GenericValue> listProductInline = delegator.findList("ProductPromoProduct", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(listProductInline)) {
				totalRow.addAll(listProductInline);
			}
			conds.clear();
			conds.add(EntityCondition.makeCondition("productPromoId", EntityOperator.IN, listPromoIdAppl));
			conds.add(EntityCondition.makeCondition("productPromoActionSeqId", "_NA_"));
			conds.add(EntityCondition.makeCondition("productPromoApplEnumId", "PPPA_INCLUDE"));
			conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
			List<GenericValue> listCategoryInline = delegator.findList("ProductPromoCategory", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(listCategoryInline)) {
				totalRow.addAll(listCategoryInline);
			}
			
			List<GenericPK> listRulePK = FastList.newInstance();
			for (GenericValue row : totalRow) {
				GenericPK newPK = delegator.makePK("ProductPromoRule");
				newPK.put("productPromoId", row.get("productPromoId"));
				newPK.put("productPromoRuleId", row.get("productPromoRuleId"));
				listRulePK.add(newPK);
			}
			
			// check promo not related to product or category
			List<EntityCondition> condTmp = FastList.newInstance();
			List<GenericValue> promoRules = delegator.findList("ProductPromoRule", EntityCondition.makeCondition("productPromoId", EntityOperator.IN, listPromoIdAppl), null, null, null, false);
			for (GenericValue rule : promoRules) {
				if (listRulePK.contains(rule.getPrimaryKey())) {
					listProductPromoRules.add(rule);
					continue;
				}
				condTmp.clear();
				condTmp.add(EntityCondition.makeCondition("productPromoId", rule.get("productPromoId")));
				condTmp.add(EntityCondition.makeCondition("productPromoRuleId", rule.get("productPromoRuleId")));
				condTmp.add(EntityCondition.makeCondition("productPromoActionSeqId", "_NA_"));
				List<GenericValue> tmp1 = delegator.findList("ProductPromoProduct", EntityCondition.makeCondition(condTmp), null, null, null, false);
				List<GenericValue> tmp2 = delegator.findList("ProductPromoCategory", EntityCondition.makeCondition(condTmp), null, null, null, false);
				if (UtilValidate.isEmpty(tmp1) && UtilValidate.isEmpty(tmp2)) {
					listProductPromoRules.add(rule);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListPromosOfProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listPromoRules", listProductPromoRules);
		return successResult;
	}
	
	public static Map<String, Object> getProductTimelineData(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		List<Map<String, Object>> listPromos = new ArrayList<Map<String, Object>>();
		try {
			List<GenericValue> promotions = null;
			List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
			String userLoginPartyId = userLogin.getString("partyId");
			
			if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) 
					|| SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId) 
					|| SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
			} else {
				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLogin.getString("partyId"), true, null, null, null, null), "productStoreId", true);
				if(UtilValidate.isNotEmpty(productStoreIds))
					listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
			}
			
			String ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payToPartyId", ownerId), EntityOperator.OR, EntityCondition.makeCondition("organizationPartyId", ownerId)));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
			
			Set<String> listSelectFields = new HashSet<String>();
			listSelectFields.add("productPromoId");
			listSelectFields.add("promoName");
			listSelectFields.add("promoText");
			listSelectFields.add("createdDate");
			listSelectFields.add("fromDate");
			listSelectFields.add("thruDate");
			listSelectFields.add("statusId");
			listSelectFields.add("organizationPartyId");
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			
			promotions = delegator.findList("ProductPromoApplStore", EntityCondition.makeCondition(listAllConditions), listSelectFields, null, opts, false);
			
			if (UtilValidate.isNotEmpty(promotions)) {
				Calendar cal = Calendar.getInstance();
				String formatDate = "dd/MM/yyyy HH:mm:ss";
				String viewDetailUiLabel = UtilProperties.getMessage("BaseSalesUiLabels", "BSViewDetail", locale);
				for (GenericValue promotion : promotions) {
					Map<String, Object> item = new HashMap<String, Object>();
					Map<String, Object> startDate = new HashMap<String, Object>();
					Map<String, Object> endDate = new HashMap<String, Object>();
					Map<String, Object> text = new HashMap<String, Object>();
					Timestamp fromDate = promotion.getTimestamp("fromDate");
					Timestamp thruDate = promotion.getTimestamp("thruDate");
					if (fromDate != null) {
						cal.setTimeInMillis(fromDate.getTime());
						startDate.put("year", cal.get(Calendar.YEAR));
						startDate.put("month", cal.get(Calendar.MONTH) + 1);
						startDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
						startDate.put("hour", cal.get(Calendar.HOUR_OF_DAY));
						startDate.put("minute", cal.get(Calendar.MINUTE));
						startDate.put("second", cal.get(Calendar.SECOND));
						startDate.put("millisecond", cal.get(Calendar.MILLISECOND));
						startDate.put("display_date", UtilFormatOut.formatDateTime(fromDate, formatDate, locale, timeZone));
					}
					item.put("start_date", startDate);
					if (thruDate != null) {
						cal.setTimeInMillis(thruDate.getTime());
						endDate.put("year", cal.get(Calendar.YEAR));
						endDate.put("month", cal.get(Calendar.MONTH) + 1);
						endDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
						endDate.put("hour", cal.get(Calendar.HOUR_OF_DAY));
						endDate.put("minute", cal.get(Calendar.MINUTE));
						endDate.put("second", cal.get(Calendar.SECOND));
						endDate.put("millisecond", cal.get(Calendar.MILLISECOND));
						endDate.put("display_date", UtilFormatOut.formatDateTime(thruDate, formatDate, locale, timeZone));
						item.put("end_date", endDate);
					}
					
					String productPromoId = promotion.getString("productPromoId");
					List<String> ruleTexts = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductPromoRule", UtilMisc.toMap("productPromoId", productPromoId), null, false), "ruleText", true);
					StringBuilder ruleTextStr = new StringBuilder();
					if (UtilValidate.isNotEmpty(promotion.get("promoText"))) {
						ruleTextStr.append(promotion.getString("promoText"));
						ruleTextStr.append("\u003cbr/\u003e");
					}
					if (UtilValidate.isNotEmpty(ruleTexts)) {
						Iterator<String> iter = ruleTexts.listIterator();
						String delim = ", ";
						while (iter.hasNext()) {
							ruleTextStr.append(iter.next());
							if (iter.hasNext()) ruleTextStr.append(delim);
						}
					}
					ruleTextStr.append("\u003cbr/\u003e\u003ci\u003e\u003ca href\u003d\"viewPromotion\u003fproductPromoId\u003d" + productPromoId + "\" target\u003d\"_blank\" class\u003d\"font-size-mini\"\u003e" + viewDetailUiLabel + " \u003e\u003e\u003c/a\u003e\u003c/i\u003e");
					text.put("headline", promotion.get("promoName"));
					text.put("text", ruleTextStr.toString());
					item.put("text", text);
					listPromos.add(item);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getProductTimelineData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listPromos", listPromos);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPromosByProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		/*EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");*/
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

		List<Map<String, Object>> listPromos = new ArrayList<Map<String, Object>>();
		try {
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				Map<String, Object> resultGetList = dispatcher.runSync("getListPromosRuleByProduct", 
						UtilMisc.toMap("productId", productId, "isGetAll", "Y", "isGetFutureActive", "Y", "userLogin", userLogin, "locale", locale));
				if (ServiceUtil.isSuccess(resultGetList)) {
					List<GenericValue> listPromoRules = (List<GenericValue>) resultGetList.get("listPromoRules");
					if (UtilValidate.isNotEmpty(listPromoRules)) {
						List<String> productPromoIds = EntityUtil.getFieldListFromEntityList(listPromoRules, "productPromoId", true);
						List<GenericValue> listProductPromo = delegator.findList("ProductPromo", EntityCondition.makeCondition("productPromoId", EntityOperator.IN, productPromoIds), null, null, null, false);
						
						for (GenericValue promo : listProductPromo) {
							Map<String, Object> productPromoMap = promo.getAllFields();
							
							String ruleTextStr = "";
							List<GenericValue> promoRules = EntityUtil.filterByCondition(listPromoRules, EntityCondition.makeCondition("productPromoId", promo.get("productPromoId")));
							if (UtilValidate.isNotEmpty(promoRules)) {
								List<String> ruleTexts = EntityUtil.getFieldListFromEntityList(promoRules, "ruleText", false);
								if (UtilValidate.isNotEmpty(ruleTexts)) ruleTextStr = StringUtils.join(ruleTexts, "<br/>");
							}
							productPromoMap.put("ruleTexts", ruleTextStr);
							listPromos.add(productPromoMap);
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromosByProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listPromos);
		return successResult;
	}
	
	public static Map<String, Object> checkAndUpdateStatePromotion(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String productPromoId = (String) context.get("productPromoId");
			GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
			if (productPromo != null) {
				Long useLimitPerPromotion = productPromo.getLong("useLimitPerPromotion");
				if (useLimitPerPromotion != null && useLimitPerPromotion > 0) {
					// count number used to this promotion
					Long numberUsed = delegator.findCountByCondition("ProductPromoUse", EntityCondition.makeCondition("productPromoId", productPromoId), null, null);
					if (numberUsed != null && numberUsed >= useLimitPerPromotion) {
						// send notification alert finish promotion
                        NotificationWorker.sendNotificationFinishPromotion(delegator, dispatcher, productPromo, userLogin, locale);
                        productPromo.set("stateId", "STATE_FINISH");
                        delegator.store(productPromo);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling checkAndUpdateStatePromotion service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
}
