package com.olbius.basesales.setting;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
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
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;

import net.sf.json.JSONObject;

public class SettingServices {
	public static final String module = SettingServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRoleType(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String hasRoleGroup = SalesUtil.getParameter(parameters, "hrg");
			if ("Y".equals(hasRoleGroup)) {
				String roleTypeGroupId = SalesUtil.getParameter(parameters, "roleTypeGroupId");
				if (UtilValidate.isNotEmpty(roleTypeGroupId)) {
					listAllConditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
					listAllConditions.add(EntityUtil.getFilterByDateExpr());
					listSortFields.add("sequenceNum");
					listIterator = delegator.findList("RoleTypeGroupMemberDetail", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
				}
			} else {
				listIterator = delegator.findList("RoleType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			}
		} catch(Exception e){
			String errMsg = "Fatal error calling getListRoleType service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecurrenceInfo(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-startDateTime");
    		}
			listIterator = delegator.find("RecurrenceInfoAndRule", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch(Exception e){
    		String errMsg = "Fatal error calling jqGetListRecurrenceInfo service: " + e.toString();
    		Debug.log(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> createRecurrenceInfoRule(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
			String recurrenceInfoId = (String) context.get("recurrenceInfoId");
    		if (UtilValidate.isEmpty(recurrenceInfoId)) {
    			recurrenceInfoId = delegator.getNextSeqId("RecurrenceInfo");
    		} else {
    			GenericValue recurrenceInfoExist = delegator.findOne("RecurrenceInfo", UtilMisc.toMap("recurrenceInfoId", recurrenceInfoId), false);
    			if (recurrenceInfoExist != null) {
    				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisDataIsExisted", locale));
    			}
    		}
    		
    		/* <RecurrenceRule recurrenceRuleId="205" frequency="DAILY" intervalNumber="1" countNumber="-1" byHourList="15,16,17" byDayList="MO,TU,WE,TH,FR"/>
			   <RecurrenceInfo recurrenceInfoId="205" startDateTime="2016-10-10 00:00:00.000" recurrenceRuleId="205" recurrenceCount="0"/> 
    		 */
    		String byHourList = (String) context.get("byHourList");
    		String byDayList = (String) context.get("byDayList");
    		String recurrenceRuleId = delegator.getNextSeqId("RecurrenceRule");
    		GenericValue recurrenceRule = delegator.makeValue("RecurrenceRule");
    		recurrenceRule.put("recurrenceRuleId", recurrenceRuleId);
    		recurrenceRule.put("frequency", "DAILY");
    		recurrenceRule.put("intervalNumber", new Long(1));
    		recurrenceRule.put("countNumber", new Long(-1));
    		recurrenceRule.put("byHourList", byHourList);
    		recurrenceRule.put("byDayList", byDayList);
    		delegator.create(recurrenceRule);
    		
    		GenericValue recurrenceInfo = delegator.makeValue("RecurrenceInfo");
    		recurrenceInfo.put("recurrenceInfoId", recurrenceInfoId);
    		recurrenceInfo.put("startDateTime", UtilDateTime.nowTimestamp());
    		recurrenceInfo.put("recurrenceRuleId", recurrenceRuleId);
    		recurrenceInfo.put("recurrenceCount", new Long(0));
    		delegator.create(recurrenceInfo);
    		
    		successResult.put("recurrenceInfoId", recurrenceInfoId);
    	} catch(Exception e){
    		String errMsg = "Fatal error calling createRecurrenceInfoRule service: " + e.toString();
    		Debug.log(e, errMsg, module);
    	}
    	
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListTaxAuthority(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("TaxAuthorityAndDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListTaxAuthority service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListCarrierShipment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("CarrierShipmentMethod", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCarrierShipment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
  
    //start
    @SuppressWarnings("rawtypes")
	public static Map<String, Object> listRoleTypee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProducts = delegator.findList("RoleType", null, null, null, null, false);
		List<Map> listRoleType = FastList.newInstance();
		for (GenericValue x : listProducts) {
			Map<String, Object> mapProductsInfo = FastMap.newInstance();
			String roleTypeId = x.getString("roleTypeId");
			mapProductsInfo.put("roleTypeId", roleTypeId);
			mapProductsInfo.put("description", x.getString("description"));
			mapProductsInfo.put("parentTypeId", x.getString("parentTypeId"));
			listRoleType.add(mapProductsInfo);
		}
		result.put("listRoleType", listRoleType);
		return result;
	}
    
    @SuppressWarnings("rawtypes")
	public static Map<String, Object> listFeatureTypee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> featureTypeList = delegator.findList("ProductFeatureType", null, null, null, null, false);
		List<Map> listFeatureType = FastList.newInstance();
		for (GenericValue x : featureTypeList) {
			Map<String, Object> mapProductsInfo = FastMap.newInstance();
			String productFeatureTypeId = x.getString("productFeatureTypeId");
			mapProductsInfo.put("productFeatureTypeId", productFeatureTypeId);
			mapProductsInfo.put("description", x.getString("description"));
			mapProductsInfo.put("parentTypeId", x.getString("parentTypeId"));
			listFeatureType.add(mapProductsInfo);
		}
		result.put("listFeatureType", listFeatureType);
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPriority(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator del = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try{
			EntityCondition tmpCondition = null;
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId", "ORDER_PRIORITY"));
			tmpCondition = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("sequenceId");
			}
			listIterator = del.findList("Enumeration", tmpCondition, null, listSortFields, opts, false);
		} catch(Exception e){
			String errMsg = "Fatal error calling jqGetListPriority service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> createPriority(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String enumId = (String) context.get("enumId");
			enumId = enumId.toUpperCase();
			String prefixPriority = "PRIO_";
			String priorityId = prefixPriority + enumId;
			String description = (String)context.get("description");
			String enumTypeId = "ORDER_PRIORITY";
			String enumCode = (String)context.get("enumCode");
			String sequenceId = (String)context.get("sequenceId");
			
			List<GenericValue> priorityNotEmpty = delegator.findList("Enumeration", EntityCondition.makeCondition("enumId", priorityId), null, null, null, false);
			if (UtilValidate.isNotEmpty(priorityNotEmpty)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSEnumcodeNotCoincidence", locale));
			}
			
			GenericValue priority = delegator.makeValue("Enumeration");
			priority.set("enumId", priorityId);
			priority.set("enumCode", enumCode);
			priority.set("sequenceId", sequenceId);
			priority.set("description", description);
			priority.set("enumTypeId", enumTypeId);
		
			delegator.create(priority);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createPriority service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    public static Map<String, Object> updatePriority(DispatchContext dcpt, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator= dcpt.getDelegator();
		String enumId = (String)context.get("enumId");
		String description = (String)context.get("description");
		String sequenceId = (String)context.get("sequenceId");
		
		GenericValue priority = delegator.findOne("Enumeration", UtilMisc.toMap("enumId",enumId), false);
		if(UtilValidate.isNotEmpty(priority)){
			priority.set("enumId", enumId);
			priority.set("description", description);
			priority.set("sequenceId", sequenceId);
			priority.store();
		}
		Map<String,Object> result= ServiceUtil.returnSuccess();
		return result;
	}
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrganizationParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	try {
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, organization));
			listIterator = delegator.find("PartyAcctgPrefAndGroup", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrganizationPartyAcctg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
//    public static Map<String, Object> createProductStore(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
//		Delegator delegator = dctx.getDelegator();
//		
//		String productStoreId = (String)context.get("productStoreId");
//		productStoreId = productStoreId.toUpperCase();
//		String storeName = (String)context.get("storeName");
//		String payToPartyId = (String)context.get("payToPartyId");
//		String title = (String)context.get("title");
//		String subtitle = (String)context.get("subtitle");
//		String defaultCurrencyUomId = (String)context.get("defaultCurrencyUomId");
//		String storeCreditAccountEnumId = (String)context.get("storeCreditAccountEnumId");
//		String salesMethodChannelEnumId = (String)context.get("salesMethodChannelEnumId");
//		String checkInventory = (String)context.get("checkInventory");
////		String vatTaxAuthGeoId = (String) context.get("vatTaxAuthGeoId"); 
////		String vatTaxAuthPartyId = (String) context.get("vatTaxAuthPartyId");
//		
//		String manualAuthIsCapture, autoSaveCart, viewCartOnAdd, autoApproveReviews, isDemoStore, isImmediatelyFulfilled, oneInventoryFacility, requireInventory, balanceResOnOrderCreation, explodeOrderItems, checkGcBalance, usePrimaryEmailUsername, requireCustomerRole, selectPaymentTypePerItem, showPricesWithVatTax, enableAutoSuggestionList, enableDigProdUpload, managedByLot;
//		manualAuthIsCapture = autoSaveCart = viewCartOnAdd = autoApproveReviews = isDemoStore = isImmediatelyFulfilled = oneInventoryFacility = requireInventory = balanceResOnOrderCreation = explodeOrderItems = checkGcBalance = usePrimaryEmailUsername = requireCustomerRole = selectPaymentTypePerItem = showPricesWithVatTax = enableAutoSuggestionList = enableDigProdUpload = managedByLot = "N";
//		String prorateShipping, prorateTaxes, reserveInventory, allowPassword, retryFailedAuths, autoInvoiceDigitalItems, reqShipAddrForDigItems, showCheckoutGiftOptions, showTaxIsExempt, prodSearchExcludeVariants, autoOrderCcTryExp, autoOrderCcTryOtherCards, autoOrderCcTryLaterNsf, autoApproveInvoice, autoApproveOrder, shipIfCaptureFails, showOutOfStockProducts, addToCartRemoveIncompat, addToCartReplaceUpsell, splitPayPrefPerShpGrp;
//		prorateShipping = prorateTaxes = reserveInventory = allowPassword = retryFailedAuths = autoInvoiceDigitalItems = reqShipAddrForDigItems = showCheckoutGiftOptions = showTaxIsExempt = prodSearchExcludeVariants = autoOrderCcTryExp = autoOrderCcTryOtherCards = autoOrderCcTryLaterNsf = autoApproveInvoice = autoApproveOrder = shipIfCaptureFails = showOutOfStockProducts = addToCartRemoveIncompat = addToCartReplaceUpsell = splitPayPrefPerShpGrp = "Y";
//		String headerApprovedStatus = "ORDER_APPROVED" ;
//		String itemApprovedStatus = "ITEM_APPROVED" ;
//		String digitalItemApprovedStatus = "ITEM_APPROVED" ;
//		String headerDeclinedStatus = "ORDER_REJECTED" ;
//		String itemDeclinedStatus = "ITEM_REJECTED" ;
//		String headerCancelStatus = "ORDER_CANCELLED" ;
//		String itemCancelStatus = "ITEM_CANCELLED" ;
//		String visualThemeId = "ACEADMIN" ;
//		String inventoryFacilityId, defaultSalesChannelEnumId;
//		inventoryFacilityId = defaultSalesChannelEnumId = null;
//		String vatTaxAuthGeoId = "VNM";
//		String vatTaxAuthPartyId = "VNM_TAX";
//		
//		GenericValue productStore = delegator.makeValue("ProductStore");
//		EntityFindOptions findOptions = new EntityFindOptions();
//		findOptions.setDistinct(true);
//		Map<String,Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", (Locale)context.get("locale")));
//		productStore.set("productStoreId", productStoreId);
//		productStore.set("storeName", storeName);
//		productStore.set("payToPartyId", payToPartyId);
//		productStore.set("title", title);
//		productStore.set("subtitle", subtitle);
//		productStore.set("manualAuthIsCapture", manualAuthIsCapture);
//		productStore.set("prorateShipping", prorateShipping);
//		productStore.set("prorateTaxes", prorateTaxes);
//		productStore.set("viewCartOnAdd", viewCartOnAdd);
//		productStore.set("autoSaveCart", autoSaveCart);
//		productStore.set("autoApproveReviews", autoApproveReviews);
//		productStore.set("isDemoStore", isDemoStore);
//		productStore.set("isImmediatelyFulfilled", isImmediatelyFulfilled);
//		productStore.set("inventoryFacilityId", inventoryFacilityId);
//		productStore.set("oneInventoryFacility", oneInventoryFacility);
//		productStore.set("checkInventory", checkInventory);
//		productStore.set("reserveInventory", reserveInventory);
//		productStore.set("requireInventory", requireInventory);
//		productStore.set("balanceResOnOrderCreation", balanceResOnOrderCreation);
//		productStore.set("defaultCurrencyUomId", defaultCurrencyUomId);
//		productStore.set("allowPassword", allowPassword);
//		productStore.set("explodeOrderItems", explodeOrderItems);
//		productStore.set("checkGcBalance", checkGcBalance);
//		productStore.set("retryFailedAuths", retryFailedAuths);
//		productStore.set("headerApprovedStatus", headerApprovedStatus);
//		productStore.set("itemApprovedStatus", itemApprovedStatus);
//		productStore.set("digitalItemApprovedStatus", digitalItemApprovedStatus);
//		productStore.set("headerDeclinedStatus", headerDeclinedStatus);
//		productStore.set("itemDeclinedStatus", itemDeclinedStatus);
//		productStore.set("headerCancelStatus", headerCancelStatus);
//		productStore.set("itemCancelStatus", itemCancelStatus);
//		productStore.set("visualThemeId", visualThemeId);
//		productStore.set("storeCreditAccountEnumId", storeCreditAccountEnumId);
//		productStore.set("usePrimaryEmailUsername", usePrimaryEmailUsername);
//		productStore.set("requireCustomerRole", requireCustomerRole);
//		productStore.set("autoInvoiceDigitalItems", autoInvoiceDigitalItems);
//		productStore.set("reqShipAddrForDigItems", reqShipAddrForDigItems);
//		productStore.set("showCheckoutGiftOptions", showCheckoutGiftOptions);
//		productStore.set("selectPaymentTypePerItem", selectPaymentTypePerItem);
//		productStore.set("showPricesWithVatTax", showPricesWithVatTax);
//		productStore.set("showTaxIsExempt", showTaxIsExempt);
//		productStore.set("enableAutoSuggestionList", enableAutoSuggestionList);
//		productStore.set("enableDigProdUpload", enableDigProdUpload);
//		productStore.set("prodSearchExcludeVariants", prodSearchExcludeVariants);
//		productStore.set("autoOrderCcTryExp", autoOrderCcTryExp);
//		productStore.set("autoOrderCcTryOtherCards", autoOrderCcTryOtherCards);
//		productStore.set("autoOrderCcTryLaterNsf", autoOrderCcTryLaterNsf);
//		productStore.set("autoApproveInvoice", autoApproveInvoice);
//		productStore.set("autoApproveOrder", autoApproveOrder);
//		productStore.set("shipIfCaptureFails", shipIfCaptureFails);
//		productStore.set("addToCartRemoveIncompat", addToCartRemoveIncompat);
//		productStore.set("addToCartReplaceUpsell", addToCartReplaceUpsell);
//		productStore.set("splitPayPrefPerShpGrp", splitPayPrefPerShpGrp);
//		productStore.set("managedByLot", managedByLot);
//		productStore.set("showOutOfStockProducts", showOutOfStockProducts);
//		productStore.set("defaultSalesChannelEnumId", defaultSalesChannelEnumId);
//		productStore.set("vatTaxAuthGeoId", vatTaxAuthGeoId);
//		productStore.set("vatTaxAuthPartyId", vatTaxAuthPartyId);
//		productStore.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
//		try {
//			delegator.create(productStore);
//		} catch (GenericEntityException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStoresRole(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
    		if (UtilValidate.isNotEmpty(productStoreId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("-thruDate");
    				listSortFields.add("-fromDate");
    			}
    			listIterator = delegator.find("ProductStoreRoleAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    /* TODOCHANGE deleted
    public static Map<String, Object> createProductStoreRoles(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		String roleTypeId = (String)context.get("roleTypeId");
		String partyId = (String)context.get("partyId");
		String productStoreId = (String)context.get("productStoreId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		if(UtilValidate.isNotEmpty(thruDate)){
			thruDate = (Timestamp)context.get("thruDate");
		}
		GenericValue productStoreRole = delegator.makeValue("ProductStoreRole");
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", (Locale)context.get("locale")));
		productStoreRole.set("roleTypeId", roleTypeId);
		productStoreRole.set("partyId", partyId);
		productStoreRole.set("productStoreId", productStoreId);
		productStoreRole.set("fromDate", fromDate);
		productStoreRole.set("thruDate", thruDate);
		try {
			delegator.create(productStoreRole);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			Map<String, Object> resultError = ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSERoleTypeNotAvailable", (Locale)context.get("locale")));
			return resultError;
		}
		result.put("roleTypeId", roleTypeId);
		result.put("partyId", partyId);
		result.put("productStoreId", productStoreId);
		result.put("thruDate", thruDate);
		result.put("fromDate", fromDate);
		
		return result;
	}*/
    
    /* TODOCHANGE deleted
    public static Map<String, Object> deleteProductStoreRole(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aProductStoreRole = (String)context.get("aProductStoreRole");
		JSONObject listJson = JSONObject.fromObject(aProductStoreRole);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productStoreId = (String) listJson.get("productStoreId");
			String partyId = (String) listJson.get("partyId");
			String roleTypeId = (String) listJson.get("roleTypeId");
			Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
			Timestamp thruDateNew = UtilDateTime.nowTimestamp();
			GenericValue member = delegator.findOne("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyId, "roleTypeId" , roleTypeId, "fromDate", fromDate), false);		
			if(UtilValidate.isNotEmpty(member)){
				member.set("productStoreId", productStoreId);
				member.set("partyId", partyId);
				member.set("roleTypeId", roleTypeId);
				member.set("fromDate", fromDate);
				if(fromDate.after(thruDateNew)){
					member.set("thruDate", fromDate);
				}else {
					member.set("thruDate", thruDateNew);
				}
				member.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			resultSc.put("productStoreId", productStoreId);
			resultSc.put("roleTypeId", roleTypeId);
			resultSc.put("partyId", partyId);
			resultSc.put("fromDate", fromDate);
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}*/
    
    /*public static Map<String, Object> updateProductStoreCatalog(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String cMemberr = (String)context.get("cMemberr");
		JSONObject listJson = JSONObject.fromObject(cMemberr);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productStoreId = (String) listJson.get("productStoreId");
			String prodCatalogId = (String) listJson.get("prodCatalogId");
			String thruDateee = (String) listJson.getString("thruDate");
			Long sequenceNum = Long.parseLong(listJson.getString("sequenceNum"));
			Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
			Timestamp thruDateNew = null;
			if(thruDateee.equals(null)){
				thruDateNew = new Timestamp(Long.parseLong(thruDateee));
			}
			GenericValue member = delegator.findOne("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId, "prodCatalogId", prodCatalogId, "fromDate", fromDate), false);		
			if(UtilValidate.isNotEmpty(member)){
				member.set("productStoreId", productStoreId);
				member.set("prodCatalogId", prodCatalogId);
				member.set("sequenceNum", sequenceNum);
				member.set("fromDate", fromDate);
				if(UtilValidate.isNotEmpty(thruDateNew)){
					member.set("thruDate", thruDateNew);
				}
				member.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
    
    public static Map<String, Object> deleteProductStoreCatalog(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String cMemberr = (String)context.get("cMemberr");
		JSONObject listJson = JSONObject.fromObject(cMemberr);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productStoreId = (String) listJson.get("productStoreId");
			String prodCatalogId = (String) listJson.get("prodCatalogId");
			Long sequenceNum = Long.parseLong(listJson.getString("sequenceNum"));
			Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
			Timestamp thruDateNew = UtilDateTime.nowTimestamp();
			GenericValue member = delegator.findOne("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId, "prodCatalogId", prodCatalogId, "fromDate", fromDate), false);		
			if(UtilValidate.isNotEmpty(member)){
				member.set("productStoreId", productStoreId);
				member.set("prodCatalogId", prodCatalogId);
				member.set("sequenceNum", sequenceNum);
				member.set("fromDate", fromDate);
				if(fromDate.after(thruDateNew)){
					member.set("thruDate", fromDate);
				}else {
					member.set("thruDate", thruDateNew);
				}
				member.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}*/
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStoreShipmentMethod(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
    		if (productStoreId != null) {
    			listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("sequenceNumber");
    			}
    			listIterator = delegator.find("ProductStoreShipmentMethView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreShipmentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createProductStoreShipmentMethod(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productStoreId = (String)context.get("productStoreId");
		String productStoreShipMethId = delegator.getNextSeqId("ProductStoreShipmentMeth");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		try {
			List<GenericValue> shipmentMethExist = delegator.findByAnd("ProductStoreShipmentMeth", 
					UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId, "partyId", partyId, "roleTypeId", roleTypeId), null, false);
			if (UtilValidate.isNotEmpty(shipmentMethExist)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisDataHasExisted", locale));
			}
			
			BigDecimal minWeight = (BigDecimal)context.get("minWeight");
			BigDecimal maxWeight = (BigDecimal)context.get("maxWeight");
			BigDecimal minSize = (BigDecimal)context.get("minSize");
			BigDecimal maxSize = (BigDecimal)context.get("maxSize");
			BigDecimal minTotal = (BigDecimal)context.get("minTotal");
			BigDecimal maxTotal = (BigDecimal)context.get("maxTotal");
			String configProps = (String)context.get("configProps");
			String shipmentCustomMethodId = null;
			String allowUspsAddr = "N";
			String requireUspsAddr = "N";
			String shipmentGatewayConfigId = null;
			Long sequenceNumber = (Long) context.get("sequenceNumber");
			
			GenericValue productStoreShipment = delegator.makeValue("ProductStoreShipmentMeth");
			productStoreShipment.set("productStoreShipMethId", productStoreShipMethId);
			productStoreShipment.set("productStoreId", productStoreId);
			productStoreShipment.set("shipmentMethodTypeId", shipmentMethodTypeId);
			productStoreShipment.set("roleTypeId", roleTypeId);
			productStoreShipment.set("partyId", partyId);
			productStoreShipment.set("minWeight", minWeight);
			productStoreShipment.set("maxWeight", maxWeight);
			productStoreShipment.set("minSize", minSize);
			productStoreShipment.set("maxSize", maxSize);
			productStoreShipment.set("minTotal", minTotal);
			productStoreShipment.set("maxTotal", maxTotal);
			productStoreShipment.set("configProps", configProps);
			productStoreShipment.set("shipmentCustomMethodId", shipmentCustomMethodId);
			productStoreShipment.set("allowUspsAddr", allowUspsAddr);
			productStoreShipment.set("requireUspsAddr", requireUspsAddr);
			productStoreShipment.set("shipmentGatewayConfigId", shipmentGatewayConfigId);
			productStoreShipment.set("sequenceNumber", sequenceNumber);
			
			delegator.create(productStoreShipment);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreShipmentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		successResult.put("shipmentMethodTypeId", shipmentMethodTypeId);
		successResult.put("productStoreId", productStoreId);
		
		return successResult;
	}
	
	public static Map<String, Object> updateProductStoreShipmentMethod(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productStoreShipMethId = (String) context.get("productStoreShipMethId");
		try {
			/*String productStoreId = (String) context.get("productStoreId");
			String shipmentMethodTypeId = (String) context.get("shipmentMethodTypeId");
			String roleTypeId = (String) context.get("roleTypeId");
			String partyId = (String) context.get("partyId");
			BigDecimal minWeight = new BigDecimal(context.getString("minWeight"));
			BigDecimal maxWeight = new BigDecimal(context.getString("maxWeight"));
			BigDecimal minSize = new BigDecimal(context.getString("minSize"));
			BigDecimal maxSize = new BigDecimal(context.getString("maxSize"));
			BigDecimal minTotal = new BigDecimal(context.getString("minTotal"));
			BigDecimal maxTotal = new BigDecimal(context.getString("maxTotal"));
			String configProps = (String) context.get("configProps");*/
			Long sequenceNumber = (Long) context.get("sequenceNumber");
			
			GenericValue member = delegator.findOne("ProductStoreShipmentMeth", UtilMisc.toMap("productStoreShipMethId", productStoreShipMethId), false);		
			if(UtilValidate.isNotEmpty(member)){
				/*member.set("productStoreId", productStoreId);
				member.set("shipmentMethodTypeId", shipmentMethodTypeId);
				member.set("roleTypeId", roleTypeId);
				member.set("partyId", partyId);
				member.set("minWeight", minWeight);
				member.set("maxWeight", maxWeight);
				member.set("minSize", minSize);
				member.set("maxSize", maxSize);
				member.set("minTotal", minTotal);
				member.set("maxTotal", maxTotal);
				member.set("configProps", configProps);*/
				member.set("sequenceNumber", sequenceNumber);
				member.store();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductStoreShipmentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		successResult.put("productStoreShipMethId", productStoreShipMethId);
		return successResult;
	}
	
	public static Map<String, Object> deleteProductStoreShipmentMethod(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productStoreShipMethId = (String) context.get("productStoreShipMethId");
		try {
			GenericValue member = delegator.findOne("ProductStoreShipmentMeth", UtilMisc.toMap("productStoreShipMethId", productStoreShipMethId), false);		
			if(UtilValidate.isNotEmpty(member)){
				member.remove();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductStoreShipmentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStorePaymentMethod(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			String productStoreId = parameters.get("productStoreId")[0];
    			listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    			listIterator = delegator.find("ProductStorePaymentSetting", tmpConditon, null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStorePaymentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createRoleType2(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", locale));
		
		String roleTypeId = null;
		try {
			String aRoleType = (String)context.get("aRoleType");
			JSONObject listJson = JSONObject.fromObject(aRoleType);
			
			if(UtilValidate.isNotEmpty(listJson)){
				roleTypeId = (String) listJson.get("roleTypeId");
				String parentTypeId = (String) listJson.get("parentTypeId");
				String description = (String) listJson.get("description");
				
				GenericValue roleType = delegator.makeValue("RoleType");
				roleType.set("roleTypeId", roleTypeId);
				if(UtilValidate.isNotEmpty(parentTypeId)){
					roleType.set("parentTypeId", parentTypeId);
				}
				roleType.set("description", description);
				roleType.set("hasTable", "N");
				delegator.create(roleType);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createRoleType2 service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		successResult.put("roleTypeId", roleTypeId);
		return successResult;
	}
	
	public static Map<String, Object> addRoleTypeGroupMember(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", locale));
		
		String roleTypeId = (String) context.get("roleTypeId");
		String roleTypeGroupId = (String) context.get("roleTypeGroupId");
		try {
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);
			GenericValue roleTypeGroup = delegator.findOne("RoleTypeGroup", UtilMisc.toMap("roleTypeGroupId", roleTypeGroupId), false);
			if (roleType != null && roleTypeGroup != null) {
				List<EntityCondition> conds = new ArrayList<EntityCondition>();
				conds.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
				conds.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> roleTypeGroupMembersExists = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(conds), null, null, null, false);
				if (UtilValidate.isNotEmpty(roleTypeGroupMembersExists)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale));
				}
				
				EntityFindOptions opts = new EntityFindOptions();
				opts.setLimit(1);
				opts.setMaxRows(1);
				List<GenericValue> roleTypeGroupMembers = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId), null, UtilMisc.toList("-sequenceNum"), opts, false);
				long nextSeq = 1;
				if (UtilValidate.isNotEmpty(roleTypeGroupMembers)) {
					Long sequenceNumMax = roleTypeGroupMembers.get(0).getLong("sequenceNum");
					if (sequenceNumMax != null) nextSeq = sequenceNumMax + 1;
				}
				GenericValue roleTypeGroupMember = delegator.makeValue("RoleTypeGroupMember");
				roleTypeGroupMember.put("roleTypeId", roleTypeId);
				roleTypeGroupMember.put("roleTypeGroupId", roleTypeGroupId);
				roleTypeGroupMember.put("fromDate", UtilDateTime.nowTimestamp());
				roleTypeGroupMember.put("sequenceNum", nextSeq);
				delegator.create(roleTypeGroupMember);
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordIsNotFound", locale));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling addRoleTypeGroupMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		successResult.put("roleTypeId", roleTypeId);
		return successResult;
	}
	
	public static Map<String, Object> removeRoleTypeGroupMember(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", locale));
		
		String roleTypeId = (String) context.get("roleTypeId");
		String roleTypeGroupId = (String) context.get("roleTypeGroupId");
		try {
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			conds.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
			conds.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> roleTypeGroupMembersExists = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isEmpty(roleTypeGroupMembersExists)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordIsNotFound", locale));
			}
			
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			for (GenericValue roleTypeGroupMember : roleTypeGroupMembersExists) {
				roleTypeGroupMember.set("thruDate", nowTimestamp);
			}
			delegator.storeAll(roleTypeGroupMembersExists);
		} catch (Exception e) {
			String errMsg = "Fatal error calling removeRoleTypeGroupMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> editRoleType2(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aRoleType = (String)context.get("aRoleType");
		JSONObject listJson = JSONObject.fromObject(aRoleType);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String roleTypeId = (String)listJson.get("roleTypeId");
			String parentTypeId = (String)listJson.get("parentTypeId");
			String description = (String)listJson.get("description");
			
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);		
			if(UtilValidate.isNotEmpty(roleType)){
				roleType.set("roleTypeId", roleTypeId);
				roleType.set("description", description);
				if(UtilValidate.isNotEmpty(parentTypeId)){
					roleType.set("parentTypeId", parentTypeId);
				}
				roleType.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			resultSc.put("roleTypeId", roleTypeId);
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	public static Map<String, Object> createFeatureType2(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aFeatureType = (String)context.get("aFeatureType");
		JSONObject listJson = JSONObject.fromObject(aFeatureType);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productFeatureTypeId = (String)listJson.get("productFeatureTypeId");
			String parentTypeId = (String)listJson.get("parentTypeId");
			String description = (String)listJson.get("description");
			GenericValue roleType = delegator.makeValue("ProductFeatureType");
			Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", (Locale)context.get("locale")));
			roleType.set("productFeatureTypeId", productFeatureTypeId);
			if(UtilValidate.isNotEmpty(parentTypeId)){
				roleType.set("parentTypeId", parentTypeId);
			}
			roleType.set("description", description);
			try {
				delegator.create(roleType);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			result.put("productFeatureTypeId", productFeatureTypeId);
			return result;
		}
		Map<String, Object> result2 = FastMap.newInstance();
		return result2;
	}
	
	public static Map<String, Object> editFeatureType2(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aFeatureType = (String)context.get("aFeatureType");
		JSONObject listJson = JSONObject.fromObject(aFeatureType);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productFeatureTypeId = (String)listJson.get("productFeatureTypeId");
			String parentTypeId = (String)listJson.get("parentTypeId");
			String description = (String)listJson.get("description");
			
			GenericValue featureType = delegator.findOne("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId), false);		
			if(UtilValidate.isNotEmpty(featureType)){
				featureType.set("productFeatureTypeId", productFeatureTypeId);
				featureType.set("description", description);
				if(UtilValidate.isNotEmpty(parentTypeId)){
					featureType.set("parentTypeId", parentTypeId);
				}
				featureType.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			resultSc.put("productFeatureTypeId", productFeatureTypeId);
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	public static Map<String, Object> createProductStorePaymentMethod(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productStoreId = (String)context.get("productStoreId");
		String paymentMethodTypeId = (String)context.get("paymentMethodTypeId");
		String paymentServiceTypeEnumId = (String)context.get("paymentServiceTypeEnumId");
		try {
			GenericValue paymentSettingExist = delegator.findOne("ProductStorePaymentSetting", 
					UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId), false);
			if (paymentSettingExist != null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisDataHasExisted", locale));
			}
			
			String paymentService = (String)context.get("paymentService");
			String paymentCustomMethodId = (String)context.get("paymentCustomMethodId");
			String paymentGatewayConfigId = (String)context.get("paymentGatewayConfigId");
			String paymentPropertiesPath = (String)context.get("paymentPropertiesPath");
			String applyToAllProducts = (String)context.get("applyToAllProducts");
			
			GenericValue productStorePayment = delegator.makeValue("ProductStorePaymentSetting");
			productStorePayment.set("productStoreId", productStoreId);
			productStorePayment.set("paymentMethodTypeId", paymentMethodTypeId);
			productStorePayment.set("paymentServiceTypeEnumId", paymentServiceTypeEnumId);
			
			if (UtilValidate.isNotEmpty(paymentService)) productStorePayment.set("paymentService", paymentService);
			if (UtilValidate.isNotEmpty(paymentCustomMethodId)) productStorePayment.set("paymentCustomMethodId", paymentCustomMethodId);
			if (UtilValidate.isNotEmpty(paymentGatewayConfigId)) productStorePayment.set("paymentGatewayConfigId", paymentGatewayConfigId);
			if (UtilValidate.isNotEmpty(paymentPropertiesPath)) productStorePayment.set("paymentPropertiesPath", paymentPropertiesPath);
			if (UtilValidate.isNotEmpty(applyToAllProducts)) productStorePayment.set("applyToAllProducts", applyToAllProducts);
			delegator.create(productStorePayment);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStorePaymentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		successResult.put("productStoreId", productStoreId);
		successResult.put("paymentMethodTypeId", paymentMethodTypeId);
		successResult.put("paymentServiceTypeEnumId", paymentServiceTypeEnumId);
		return successResult;
	}
	
	public static Map<String, Object> deleteProductStorePaymentMethod(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productStoreId = (String) context.get("productStoreId");
		String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
		String paymentServiceTypeEnumId = (String) context.get("paymentServiceTypeEnumId");
		try {
			GenericValue paymentSetting = delegator.findOne("ProductStorePaymentSetting", 
					UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId), false);
			if (paymentSetting != null) {
				paymentSetting.remove();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductStorePaymentMethod service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	/* FEATURE */
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListFeature(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator del = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		@SuppressWarnings("unused")
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			EntityCondition tmpCondition = null;
			tmpCondition = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = del.find("ProductFeatureCategory", tmpCondition, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFeature service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListFeatureCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator del = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = del.find("ProductFeatureCategory", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFeatureCategory service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    
    public static Map<String, Object> createFeature(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = dctx.getDelegator();
    	String cMemberr = (String)context.get("cMemberr");
		JSONObject listJson = JSONObject.fromObject(cMemberr);
		if(UtilValidate.isNotEmpty(listJson)){
			String productFeatureCategoryId = (String)listJson.getString("productFeatureCategoryId");
	    	String description = (String)listJson.getString("description");
	    	GenericValue feature = delegator.makeValue("ProductFeatureCategory");
	    	EntityFindOptions findOptions = new EntityFindOptions();
	    	findOptions.setDistinct(true);
	    	feature.set("productFeatureCategoryId", productFeatureCategoryId);
	    	feature.set("description", description);
	    	try {
	    		delegator.create(feature);
	    	} catch (GenericEntityException e) {
	    		e.printStackTrace();
	    	}
		}
		Map<String,Object> result= ServiceUtil.returnSuccess();
    	return result;
    }
    
    public static Map<String, Object> updateFeature(DispatchContext dcpt, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator= dcpt.getDelegator();
		String productFeatureCategoryId = (String)context.get("productFeatureCategoryId");
		String description = (String)context.get("description");
		
		GenericValue feature = delegator.findOne("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId",productFeatureCategoryId), false);
		if(UtilValidate.isNotEmpty(feature)){
			feature.set("productFeatureCategoryId", productFeatureCategoryId);
			feature.set("description", description);
			feature.store();
		}
		Map<String,Object> result= ServiceUtil.returnSuccess();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListFeature(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		
		try {
			String productFeatureCategoryId = SalesUtil.getParameter(parameters, "productFeatureCategoryId");
			listAllConditions.add(EntityCondition.makeCondition("productFeatureCategoryId", productFeatureCategoryId));
			
			listIterator = delegator.find("ProductFeature", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch(Exception e) {
			String errString = "Fatal error calling jqGetListFeature service: " + e.toString();
			Debug.log(e, errString, module);
		}
		
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createFeatureChild(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		String productFeatureCategoryId = (String)context.get("productFeatureCategoryId");
		String productFeatureId = (String)context.get("productFeatureId");
		String productFeatureTypeId = (String)context.get("productFeatureTypeId");
		String description = (String)context.get("description");
		String uomId = (String)context.get("uomId");
		String abbrev = (String)context.get("abbrev");
		String idCode = (String)context.get("idCode");
		BigDecimal numberSpecified = (BigDecimal)context.get("numberSpecified");
		BigDecimal defaultAmount = (BigDecimal)context.get("defaultAmount");
		Long defaultSequenceNum = (Long)context.get("defaultSequenceNum");
		
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List<EntityCondition> checkSequenceNumber = FastList.newInstance();
		checkSequenceNumber.add(EntityCondition.makeCondition("defaultSequenceNum", EntityOperator.EQUALS, defaultSequenceNum));
		checkSequenceNumber.add(EntityCondition.makeCondition("productFeatureCategoryId", productFeatureCategoryId));
		List<GenericValue> notEmpty = delegator.findList("ProductFeature", EntityCondition.makeCondition(checkSequenceNumber, EntityOperator.AND), null, null, findOptions, false);
		if(UtilValidate.isNotEmpty(notEmpty)){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSSequenceIdNotCoincidence", (Locale)context.get("locale")));
		}
		GenericValue featureChild = delegator.makeValue("ProductFeature");
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", (Locale)context.get("locale")));
		featureChild.set("productFeatureCategoryId", productFeatureCategoryId);
		featureChild.set("productFeatureId", productFeatureId);
		featureChild.set("productFeatureTypeId", productFeatureTypeId);
		featureChild.set("description", description);
		featureChild.set("uomId", uomId);
		featureChild.set("abbrev", abbrev);
		featureChild.set("idCode", idCode);
		featureChild.set("numberSpecified", numberSpecified);
		featureChild.set("defaultAmount", defaultAmount);
		featureChild.set("defaultSequenceNum", defaultSequenceNum);
		try {
			delegator.create(featureChild);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("productFeatureCategoryId", productFeatureCategoryId);
		result.put("productFeatureId", productFeatureId);
		result.put("productFeatureTypeId", productFeatureTypeId);
		result.put("description", description);
		
		return result;
	}
	
	public static Map<String, Object> editFeatureChild(DispatchContext dcpt, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator= dcpt.getDelegator();
		
		String productFeatureCategoryId = (String)context.get("productFeatureCategoryId");
		String productFeatureId = (String)context.get("productFeatureId");
		String productFeatureTypeId = (String)context.get("productFeatureTypeId");
		String description = (String)context.get("description");
		String uomId = (String)context.get("uomId");
		String abbrev = (String)context.get("abbrev");
		String idCode = (String)context.get("idCode");
		BigDecimal numberSpecified = (BigDecimal)context.get("numberSpecified");
		BigDecimal defaultAmount = (BigDecimal)context.get("defaultAmount");
		Long defaultSequenceNum = (Long)context.get("defaultSequenceNum");
		
		
		GenericValue featureChild = delegator.findOne("ProductFeature", UtilMisc.toMap("productFeatureId",productFeatureId), false);
		if(UtilValidate.isNotEmpty(featureChild)){
			featureChild.set("productFeatureCategoryId", productFeatureCategoryId);
			featureChild.set("productFeatureId", productFeatureId);
			featureChild.set("productFeatureTypeId", productFeatureTypeId);
			featureChild.set("description", description);
			featureChild.set("uomId", uomId);
			featureChild.set("abbrev", abbrev);
			featureChild.set("idCode", idCode);
			featureChild.set("numberSpecified", numberSpecified);
			featureChild.set("defaultAmount", defaultAmount);
			featureChild.set("defaultSequenceNum", defaultSequenceNum);
			featureChild.store();
		}
		Map<String,Object> result= ServiceUtil.returnSuccess();
		result.put("productFeatureCategoryId", productFeatureCategoryId);
		result.put("productFeatureId", productFeatureId);
		result.put("productFeatureTypeId", productFeatureTypeId);
		result.put("description", description);
		return result;
	}
	/* END FEATURE */
	
	/* PRODUCT STORE */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductStore(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			String productCatalogId = SalesUtil.getParameter(parameters, "prodCatalogId");
			if (productCatalogId != null) {
				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", 
						EntityCondition.makeCondition(EntityCondition.makeCondition("prodCatalogId", productCatalogId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false), "productStoreId", true);
				if (productStoreIds != null) {
					listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				}
			}
			/*EntityCondition tmpCondition = null;
			tmpCondition = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = del.findList("ProductStore", tmpCondition, null, listSortFields, opts, false);
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "LEGAL_ORGANIZATION"));*/
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "OWNER"));
			listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organization));
			listIterator = delegator.find("ProductStoreRoleAndPartyDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch(Exception e){
			String errMsg = "Fatal error calling getListProductStore service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductStoreAndDetailDistributor(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listIterator = new ArrayList<GenericValue>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		try {
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.NOT_EQUAL, "LEGAL_ORGANIZATION"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "OWNER"));
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productStoreId");
			}
			/*listIterator = delegator.findList("ProductStoreRoleAndPartyDetail",
						EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields,
						opts, false);*/
			listIterator = delegator.findList("ProductStoreRoleAndPartyCodeDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND),  null, listSortFields, opts, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStore(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String organizationPartyId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationPartyId));
			listIterator = delegator.find("ProductStore", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStore service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStorePriceRule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition(
				EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PRODSTORE_DISABLED"),
				EntityOperator.OR,
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
			));
		//GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			/* TODOCHANGE this comment for CORE-DMS
			String organizationPartyId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationPartyId)); */
			listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.NOT_EQUAL, "SMCHANNEL_ECOMMERCE"));
			if (UtilValidate.isEmpty(listSortFields)) listSortFields.add("productStoreId");
			listIterator = delegator.find("ProductStore", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStore service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDistributor(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "DISTRIBUTOR"));
			listIterator = delegator.find("PartyRoleNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListDistributor service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListGeos(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listAllConditions.add(EntityCondition.makeCondition("geoTypeId", "COUNTRY"));
			tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = delegator.find("Geo", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListGeos service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createProductStore(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		String productStoreId = (String) context.get("productStoreId");
		productStoreId = productStoreId.toUpperCase();
		String storeName = (String) context.get("storeName");
		String payToPartyId = (String) context.get("payToPartyId");
		String title = (String) context.get("title");
		String subtitle = (String) context.get("subtitle");
		String defaultCurrencyUomId = (String) context.get("defaultCurrencyUomId");
		String storeCreditAccountEnumId = (String) context.get("storeCreditAccountEnumId");
		String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
		String vatTaxAuthGeoId = (String) context.get("vatTaxAuthGeoId");
		String vatTaxAuthPartyId;

		String manualAuthIsCapture, autoSaveCart, viewCartOnAdd, autoApproveReviews, isDemoStore,
				isImmediatelyFulfilled, oneInventoryFacility, requireInventory, balanceResOnOrderCreation,
				explodeOrderItems, checkGcBalance, usePrimaryEmailUsername, requireCustomerRole,
				selectPaymentTypePerItem, showPricesWithVatTax, enableAutoSuggestionList, enableDigProdUpload,
				managedByLot;
		manualAuthIsCapture = autoSaveCart = viewCartOnAdd = autoApproveReviews = explodeOrderItems = usePrimaryEmailUsername = requireCustomerRole = selectPaymentTypePerItem = showPricesWithVatTax = enableAutoSuggestionList = enableDigProdUpload = managedByLot = "N";
		String prorateShipping, prorateTaxes, checkInventory, reserveInventory, allowPassword, retryFailedAuths,
				autoInvoiceDigitalItems, reqShipAddrForDigItems, showCheckoutGiftOptions, showTaxIsExempt,
				prodSearchExcludeVariants, autoOrderCcTryExp, autoOrderCcTryOtherCards, autoOrderCcTryLaterNsf,
				autoApproveInvoice, autoApproveOrder, shipIfCaptureFails, showOutOfStockProducts,
				addToCartRemoveIncompat, addToCartReplaceUpsell, splitPayPrefPerShpGrp;
		prorateShipping = checkInventory = reserveInventory = allowPassword = retryFailedAuths = autoInvoiceDigitalItems = reqShipAddrForDigItems = showCheckoutGiftOptions = showTaxIsExempt = prodSearchExcludeVariants = autoOrderCcTryExp = autoOrderCcTryOtherCards = autoOrderCcTryLaterNsf = autoApproveInvoice = autoApproveOrder = shipIfCaptureFails = showOutOfStockProducts = "Y";
		String headerApprovedStatus = "ORDER_APPROVED";
		String itemApprovedStatus = "ITEM_APPROVED";
		String digitalItemApprovedStatus = "ITEM_APPROVED";
		String headerDeclinedStatus;
		String itemDeclinedStatus;
		String headerCancelStatus = "ORDER_CANCELLED";
		String itemCancelStatus = "ITEM_CANCELLED";
		String visualThemeId;
		String inventoryFacilityId, defaultSalesChannelEnumId;
		String primaryStoreGroupId, setOwnerUponIssuance, reqReturnInventoryReceive, orderDecimalQuantity, reserveOrderEnumId;
		
		if(salesMethodChannelEnumId.equals("SMCHANNEL_POS")){
			primaryStoreGroupId = "_NA_";
			prorateTaxes = splitPayPrefPerShpGrp = "N";
			requireInventory = isDemoStore  = isImmediatelyFulfilled = oneInventoryFacility = balanceResOnOrderCreation = checkGcBalance = setOwnerUponIssuance = reqReturnInventoryReceive = orderDecimalQuantity = "Y";
			reserveOrderEnumId = "INVRO_FIFO_REC";
			defaultSalesChannelEnumId = "POS_SALES_CHANNEL";
			inventoryFacilityId = headerDeclinedStatus = itemDeclinedStatus = null;
			visualThemeId = vatTaxAuthPartyId = addToCartRemoveIncompat = addToCartReplaceUpsell = null;
		}else {
			primaryStoreGroupId = setOwnerUponIssuance = reqReturnInventoryReceive = orderDecimalQuantity = reserveOrderEnumId = defaultSalesChannelEnumId = null;
			prorateTaxes = splitPayPrefPerShpGrp = addToCartRemoveIncompat = addToCartReplaceUpsell = "Y";
			requireInventory = isDemoStore = isImmediatelyFulfilled = oneInventoryFacility = balanceResOnOrderCreation = checkGcBalance = "N";
			headerDeclinedStatus = "ORDER_REJECTED";
			itemDeclinedStatus = "ITEM_REJECTED";
			visualThemeId = "ACEADMIN";
			vatTaxAuthPartyId = (String) context.get("vatTaxAuthPartyId");
			inventoryFacilityId = (String) context.get("inventoryFacilityId");
		}

		GenericValue productStore = delegator.makeValue("ProductStore");
		GenericValue productStoreFacility = delegator.makeValue("ProductStoreFacility");
		GenericValue productStoreRole = delegator.makeValue("ProductStoreRole");
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", (Locale)context.get("locale")));
		productStore.set("productStoreId", productStoreId);
		productStore.set("storeName", storeName);
		productStore.set("payToPartyId", payToPartyId);
		productStore.set("title", title);
		productStore.set("subtitle", subtitle);
		productStore.set("manualAuthIsCapture", manualAuthIsCapture);
		productStore.set("prorateShipping", prorateShipping);
		productStore.set("prorateTaxes", prorateTaxes);
		productStore.set("viewCartOnAdd", viewCartOnAdd);
		productStore.set("autoSaveCart", autoSaveCart);
		productStore.set("autoApproveReviews", autoApproveReviews);
		productStore.set("isDemoStore", isDemoStore);
		productStore.set("isImmediatelyFulfilled", isImmediatelyFulfilled);
		if(UtilValidate.isNotEmpty(inventoryFacilityId)){
			productStore.set("inventoryFacilityId", inventoryFacilityId);
		}else {
			productStore.set("inventoryFacilityId", null);
		}
		productStore.set("oneInventoryFacility", oneInventoryFacility);
		productStore.set("checkInventory", checkInventory);
		productStore.set("reserveInventory", reserveInventory);
		productStore.set("requireInventory", requireInventory);
		productStore.set("balanceResOnOrderCreation", balanceResOnOrderCreation);
		productStore.set("defaultCurrencyUomId", defaultCurrencyUomId);
		productStore.set("allowPassword", allowPassword);
		productStore.set("explodeOrderItems", explodeOrderItems);
		productStore.set("checkGcBalance", checkGcBalance);
		productStore.set("retryFailedAuths", retryFailedAuths);
		productStore.set("headerApprovedStatus", headerApprovedStatus);
		productStore.set("itemApprovedStatus", itemApprovedStatus);
		productStore.set("digitalItemApprovedStatus", digitalItemApprovedStatus);
		productStore.set("headerDeclinedStatus", headerDeclinedStatus);
		productStore.set("itemDeclinedStatus", itemDeclinedStatus);
		productStore.set("headerCancelStatus", headerCancelStatus);
		productStore.set("itemCancelStatus", itemCancelStatus);
		productStore.set("visualThemeId", visualThemeId);
		productStore.set("storeCreditAccountEnumId", storeCreditAccountEnumId);
		productStore.set("usePrimaryEmailUsername", usePrimaryEmailUsername);
		productStore.set("requireCustomerRole", requireCustomerRole);
		productStore.set("autoInvoiceDigitalItems", autoInvoiceDigitalItems);
		productStore.set("reqShipAddrForDigItems", reqShipAddrForDigItems);
		productStore.set("showCheckoutGiftOptions", showCheckoutGiftOptions);
		productStore.set("selectPaymentTypePerItem", selectPaymentTypePerItem);
		productStore.set("showPricesWithVatTax", showPricesWithVatTax);
		productStore.set("showTaxIsExempt", showTaxIsExempt);
		productStore.set("enableAutoSuggestionList", enableAutoSuggestionList);
		productStore.set("enableDigProdUpload", enableDigProdUpload);
		productStore.set("prodSearchExcludeVariants", prodSearchExcludeVariants);
		productStore.set("autoOrderCcTryExp", autoOrderCcTryExp);
		productStore.set("autoOrderCcTryOtherCards", autoOrderCcTryOtherCards);
		productStore.set("autoOrderCcTryLaterNsf", autoOrderCcTryLaterNsf);
		productStore.set("autoApproveInvoice", autoApproveInvoice);
		productStore.set("autoApproveOrder", autoApproveOrder);
		productStore.set("shipIfCaptureFails", shipIfCaptureFails);
		productStore.set("addToCartRemoveIncompat", addToCartRemoveIncompat);
		productStore.set("addToCartReplaceUpsell", addToCartReplaceUpsell);
		productStore.set("splitPayPrefPerShpGrp", splitPayPrefPerShpGrp);
		productStore.set("managedByLot", managedByLot);
		productStore.set("showOutOfStockProducts", showOutOfStockProducts);
		productStore.set("defaultSalesChannelEnumId", defaultSalesChannelEnumId);
		productStore.set("vatTaxAuthGeoId", vatTaxAuthGeoId);
		productStore.set("vatTaxAuthPartyId", vatTaxAuthPartyId);
		productStore.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
		productStore.set("primaryStoreGroupId", primaryStoreGroupId);
		productStore.set("setOwnerUponIssuance", setOwnerUponIssuance);
		productStore.set("reqReturnInventoryReceive", reqReturnInventoryReceive);
		productStore.set("orderDecimalQuantity", orderDecimalQuantity);
		productStore.set("reserveOrderEnumId", reserveOrderEnumId);
		
		Date today=new Date(System.currentTimeMillis());
		Timestamp todayTs = new Timestamp(today.getTime());
		
		productStoreRole.set("productStoreId", productStoreId);
		productStoreRole.set("roleTypeId", "OWNER");
		productStoreRole.set("partyId", payToPartyId);
		productStoreRole.set("fromDate", todayTs);
		
		if(salesMethodChannelEnumId.equals("SMCHANNEL_POS")){
		}else{
			if(UtilValidate.isNotEmpty(inventoryFacilityId)){
				Long sequence = new Long(1);
				productStoreFacility.set("productStoreId", productStoreId);
				productStoreFacility.set("facilityId", inventoryFacilityId);
				productStoreFacility.set("fromDate", todayTs);
				productStoreFacility.set("sequenceNum", sequence);
			}
		}
		try {
			delegator.create(productStore);
			delegator.create(productStoreRole);
			if(salesMethodChannelEnumId.equals("SMCHANNEL_POS")){
			}else{
				if(UtilValidate.isNotEmpty(inventoryFacilityId)){
					delegator.create(productStoreFacility);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("productStoreId", productStoreId);
		result.put("storeName", storeName);
		return result;
	}
	
	public static Map<String, Object> editProductStoreOlb(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aProductStore = (String) context.get("aProductStore");
		JSONObject listJson = JSONObject.fromObject(aProductStore);
		Date today=new Date(System.currentTimeMillis());
		Timestamp todayTs = new Timestamp(today.getTime());

		if (UtilValidate.isNotEmpty(listJson)) {
			String productStoreId = (String) listJson.get("productStoreId");
			String storeName = (String) listJson.get("storeName");
			String payToPartyId = (String) listJson.get("payToPartyId");
			String title = (String) listJson.get("title");
			String subtitle = (String) listJson.get("subtitle");
			String defaultCurrencyUomId = (String) listJson.get("defaultCurrencyUomId");
			String salesMethodChannelEnumId = listJson.getString("salesMethodChannelEnumId");
			String storeCreditAccountEnumId = (String) listJson.get("storeCreditAccountEnumId");
			String inventoryFacilityId = (String) listJson.get("inventoryFacilityId");
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			GenericValue productStoreFacilityy = delegator.findOne("ProductStoreFacility", UtilMisc.toMap("productStoreId", productStoreId, "facilityId", inventoryFacilityId, "fromDate", todayTs), false);
			if (UtilValidate.isNotEmpty(productStore)) {
				productStore.set("productStoreId", productStoreId);
				productStore.set("storeName", storeName);
				productStore.set("payToPartyId", payToPartyId);
				productStore.set("title", title);
				productStore.set("subtitle", subtitle);
				productStore.set("inventoryFacilityId", inventoryFacilityId);
				if (UtilValidate.isNotEmpty(defaultCurrencyUomId)) {
					productStore.set("defaultCurrencyUomId", defaultCurrencyUomId);
				}
				if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
					productStore.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
				}
				if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
					productStore.set("storeCreditAccountEnumId", storeCreditAccountEnumId);
				}
				productStore.store();
				
				if(UtilValidate.isNotEmpty(productStoreFacilityy)){
				}else{
					GenericValue productStoreFacility = delegator.makeValue("ProductStoreFacility");
					productStoreFacility.set("productStoreId", productStoreId);
					productStoreFacility.set("facilityId", inventoryFacilityId);
					productStoreFacility.set("fromDate", todayTs);
					delegator.create(productStoreFacility);
				}
			}
			Map<String, Object> resultSc = ServiceUtil.returnSuccess();
			resultSc.put("productStoreId", productStoreId);
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	/* CHANNEL */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListChannel(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator del = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		@SuppressWarnings("unused")
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		EntityListIterator listIterator = null;
//		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try{
			EntityCondition tmpCondition = null;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("enumTypeId", "SALES_METHOD_CHANNEL"));
			tmpCondition = EntityCondition.makeCondition(conditions, EntityJoinOperator.AND);
			listIterator = del.find("Enumeration", tmpCondition, null, null, listSortFields, opts);
		} catch(Exception e){
			String errMsg = "Fatal error calling getListChannel service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createChannel(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
		String enumId = (String)context.get("enumId");
		//enumId = enumId.toUpperCase();
		String enumCode = (String)context.get("enumCode");
		//enumCode = enumCode.toUpperCase();
		String description = (String)context.get("description");
		String sequenceId = (String)context.get("sequenceId");
		String channelType = "SALES_METHOD_CHANNEL";
		
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List<EntityCondition> checkCode = FastList.newInstance();
		checkCode.add(EntityCondition.makeCondition("enumCode", EntityOperator.EQUALS, enumCode));
		checkCode.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, channelType));
		List<GenericValue> checkEnumCode = delegator.findList("Enumeration", EntityCondition.makeCondition(checkCode, EntityOperator.AND), null, null, findOptions, false);
		if(UtilValidate.isNotEmpty(checkEnumCode)){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSEnumcodeNotCoincidence", locale));
		}
		
		/*List<EntityCondition> checkSequence = FastList.newInstance();
		checkSequence.add(EntityCondition.makeCondition("sequenceId", EntityOperator.EQUALS, sequenceId));
		checkSequence.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, channelType));
		List<GenericValue> checkSequenceId = delegator.findList("Enumeration", EntityCondition.makeCondition(checkSequence, EntityOperator.AND), null, null, findOptions, false);
		if(UtilValidate.isNotEmpty(checkSequenceId)){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSSequenceIdNotCoincidence", (Locale)context.get("locale")));
		}*/
		
		GenericValue channel = delegator.makeValue("Enumeration");
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "Successful", locale));
		channel.set("enumId", enumId);
		channel.set("enumCode", enumCode);
		channel.set("sequenceId", sequenceId);
		channel.set("enumTypeId", channelType);
		channel.set("description", description);
		
		try {
			delegator.create(channel);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return result;
	}
	
	public static Map<String, Object> editChannel(DispatchContext dcpt, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator= dcpt.getDelegator();
		String enumId = (String)context.get("enumId");
		String enumCode = (String)context.get("enumCode");
		//enumCode = enumCode.toUpperCase();
		String description = (String)context.get("description");
		String sequenceId = (String)context.get("sequenceId");
		String channelType = "SALES_METHOD_CHANNEL";
		Locale locale = (Locale)context.get("locale");
		
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		
		GenericValue channel = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
		String channelCode = (String) channel.get("enumCode");
		//String channelSequence = (String) channel.get("sequenceId");
		
		if(enumCode.equals(channelCode)){
		} else {
			List<EntityCondition> checkCode = FastList.newInstance();
			checkCode.add(EntityCondition.makeCondition("enumCode", EntityOperator.EQUALS, enumCode));
			checkCode.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, channelType));
			List<GenericValue> checkEnumCode = delegator.findList("Enumeration", EntityCondition.makeCondition(checkCode, EntityOperator.AND), null, null, findOptions, false);
			if(UtilValidate.isNotEmpty(checkEnumCode)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSEnumcodeNotCoincidence", locale));
			}
		}
		
		/*if(sequenceId.equals(channelSequence)){
		} else {
			List<EntityCondition> checkSequence = FastList.newInstance();
			checkSequence.add(EntityCondition.makeCondition("sequenceId", EntityOperator.EQUALS, sequenceId));
			checkSequence.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, channelType));
			List<GenericValue> checkSequenceId = delegator.findList("Enumeration", EntityCondition.makeCondition(checkSequence, EntityOperator.AND), null, null, findOptions, false);
			if(UtilValidate.isNotEmpty(checkSequenceId)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSSequenceIdNotCoincidence", locale));
			}
		}*/
		
		if(UtilValidate.isNotEmpty(channel)){
			channel.set("enumId", enumId);
			channel.set("enumCode", enumCode);
			channel.set("description", description);
			channel.set("sequenceId", sequenceId);
			channel.store();
		}
		Map<String,Object> result= ServiceUtil.returnSuccess();
		return result;
	}
	/* END CHANNEL */
	
	/* SHIPMENT COST */
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListProductStoreShipmentCost(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			String productStoreId = parameters.get("productStoreId")[0];
    			listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    			listIterator = delegator.find("ShipmentCostEstimate", tmpConditon, null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStoreShipmentCost service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createShipmentCost(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		String productStoreId = (String)context.get("productStoreId");
//		String  = delegator.getNextSeqId("ProductStoreShipmentMeth");
		String shipmentCostEstimateId = (String)context.get("shipmentCostEstimateId");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		String carrierPartyId = (String)context.get("carrierPartyId");
		String carrierRoleTypeId = (String)context.get("carrierRoleTypeId");
		BigDecimal orderFlatPrice = (BigDecimal)context.get("orderFlatPrice");
		BigDecimal orderPricePercent = (BigDecimal)context.get("orderPricePercent");
		BigDecimal orderItemFlatPrice = (BigDecimal)context.get("orderItemFlatPrice");
//		Long sequenceNumber = (Long)context.get("sequenceNumber");
		
		GenericValue shipmentCostEstimate = delegator.makeValue("ShipmentCostEstimate");
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		Map<String,Object> result= ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", (Locale)context.get("locale")));
		shipmentCostEstimate.set("productStoreId", productStoreId);
		shipmentCostEstimate.set("shipmentCostEstimateId", shipmentCostEstimateId);
		shipmentCostEstimate.set("shipmentMethodTypeId", shipmentMethodTypeId);
		shipmentCostEstimate.set("carrierPartyId", carrierPartyId);
		shipmentCostEstimate.set("carrierRoleTypeId", carrierRoleTypeId);
		shipmentCostEstimate.set("orderFlatPrice", orderFlatPrice);
		shipmentCostEstimate.set("orderPricePercent", orderPricePercent);
		shipmentCostEstimate.set("orderItemFlatPrice", orderItemFlatPrice);
		try {
			delegator.create(shipmentCostEstimate);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> updateShipmentCost(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String cMemberr = (String)context.get("cMemberr");
		JSONObject listJson = JSONObject.fromObject(cMemberr);
		
		if(UtilValidate.isNotEmpty(listJson)){
			String productStoreId = (String) listJson.get("productStoreId");
			String shipmentCostEstimateId = (String) listJson.get("shipmentCostEstimateId");
			String shipmentMethodTypeId = (String) listJson.get("shipmentMethodTypeId");
			String carrierPartyId = (String) listJson.get("carrierPartyId");
			String carrierRoleTypeId = (String) listJson.get("carrierRoleTypeId");
			BigDecimal orderFlatPrice = new BigDecimal(listJson.getString("orderFlatPrice"));
			BigDecimal orderPricePercent = new BigDecimal(listJson.getString("orderPricePercent"));
			BigDecimal orderItemFlatPrice = new BigDecimal(listJson.getString("orderItemFlatPrice"));
			GenericValue member = delegator.findOne("ShipmentCostEstimate", UtilMisc.toMap("shipmentCostEstimateId", shipmentCostEstimateId), false);		
			if(UtilValidate.isNotEmpty(member)){
				member.set("productStoreId", productStoreId);
				member.set("shipmentCostEstimateId", shipmentCostEstimateId);
				member.set("shipmentMethodTypeId", shipmentMethodTypeId);
				member.set("carrierPartyId", carrierPartyId);
				member.set("carrierRoleTypeId", carrierRoleTypeId);
				member.set("orderFlatPrice", orderFlatPrice);
				member.set("orderPricePercent", orderPricePercent);
				member.set("orderItemFlatPrice", orderItemFlatPrice);
				member.store();
			}
			Map<String,Object> resultSc= ServiceUtil.returnSuccess();
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	/* END SHIPMENT COST */
	
	/* END PRODUCT STORE */
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listAgreementTerm(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("termTypeId");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "AGREEMENT_TERM"));
			EntityListIterator listIterator = delegator.find("TermType",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
    }
	
	public static Map<String, Object> updateTermType(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue termType = delegator.makeValidValue("TermType", context);
			termType.store();
		} catch (Exception e){
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> updateTermTypeAttr(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue termTypeAttr = delegator.makeValidValue("TermTypeAttr", context);
			termTypeAttr.store();
		} catch (Exception e){
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFacilityAvailable(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    		if (parameters.containsKey("productStoreId")) {
    			List<EntityCondition> conditions = FastList.newInstance();
    			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
    			conditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.EQUALS, parameters.get("productStoreId")[0]));
    			List<GenericValue> productStoreFacilities = delegator.findList("ProductStoreFacility",
    					EntityCondition.makeCondition(conditions), UtilMisc.toSet("facilityId"), null, null, false);
    			List<String> facilityIds = EntityUtil.getFieldListFromEntityList(productStoreFacilities, "facilityId", true);
    			if (UtilValidate.isNotEmpty(facilityIds)) {
    				listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.NOT_IN, facilityIds));
				}
    			//listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", organization));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", organization));
			EntityListIterator listIterator = delegator.find("FacilityAll",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEvaluateMostProfitableCustomer(DispatchContext ctx, Map<String, Object> context) {
		//Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<Map<String, Object>> listIterator = null;
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		/*
    		<attribute name="fromDate" type="Timestamp" mode="IN" optional="false"/>
	        <attribute name="thruDate" type="Timestamp" mode="IN" optional="false"/>
	        <attribute name="productStore[]" mode="IN" type="List" optional="true"/>
	        <attribute name="channel[]" mode="IN" type="List" optional="true"/>
    		 */
    		String fromDateStr = null;
    		if (parameters.containsKey("fromDate") && parameters.get("fromDate").length > 0) {
    			fromDateStr = parameters.get("fromDate")[0];
    		}
    		String thruDateStr = null;
    		if (parameters.containsKey("thruDate") && parameters.get("thruDate").length > 0) {
    			thruDateStr = parameters.get("thruDate")[0];
    		}
    		String viewSizeStr = (String) parameters.get("pagesize")[0];
    		Long viewSize = Long.parseLong(viewSizeStr);
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
            	Debug.logWarning("Error when format date time", module);
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            }
    		
            // init = true: get totalsize
            // limit: number record want to take 
            Map<String, Object> contextMap = UtilMisc.toMap(
            			"fromDate", fromDate, 
            			"thruDate", thruDate,
            			"userLogin", userLogin,
            			"locale", locale,
            			"init", true,
            			"limit", viewSize
            		);
            Map<String, Object> resultValue = dispatcher.runSync("evaluateMostProfitableCustomer", contextMap);
            if (ServiceUtil.isError(resultValue)) {
            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultValue));
            }
            listIterator = (List<Map<String, Object>>) resultValue.get("data");
            Integer totalsize = (Integer) resultValue.get("totalsize");
            successResult.put("TotalRows", String.valueOf(totalsize));
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListEvaluateMostProfitableCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerType(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "PARTY_GROUP_CUSTOMER"));
			listIterator = delegator.find("PartyType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerType service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createCustomerType(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "", "MODULE", "SALES_CUSTOMERTYPE_NEW");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
			}
			
			String partyTypeId = (String) context.get("partyTypeId");
			String description = (String) context.get("description");
			GenericValue partyTypeNew = delegator.makeValue("PartyType", 
					UtilMisc.toMap("partyTypeId", partyTypeId, 
							"parentTypeId", "PARTY_GROUP_CUSTOMER", 
							"hasTable", "Y", 
							"description", description));
			delegator.create(partyTypeNew);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling createCustomerType service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> deleteCustomerType(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "", "MODULE", "SALES_CUSTOMERTYPE_DELETE");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotDeletePermission", locale));
			}
			
			String partyTypeId = (String) context.get("partyTypeId");
			GenericValue partyType = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId), false);
			if (partyType != null) delegator.removeValue(partyType);
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteCustomerType service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorCannotDeleteThisDataHadUsed", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> checkProductStoreId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkProductStoreId(delegator, context.get("productStoreId"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}
	
	public static Map<String, Object> quickCreateRetailStore(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkProductStoreId(delegator, context.get("productStoreId"));
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			GenericValue hrm = PartyUtil.getHrmAdmin(delegator, organizationId);
			Object productStoreId = context.get("productStoreId");
			Object managerId = context.get("managerId");
			
			//Object salesmanId = context.get("salesmanId");
			if (UtilValidate.isEmpty(managerId)) {
				Object managerCode = context.get("managerCode");
				Object managerLoginId = context.get("managerLoginId");
				UniqueUtil.checkPartyCode(delegator, managerId, managerCode);
				UniqueUtil.checkUserLoginId(delegator, managerLoginId);

				Map<String, Object> manager = (Map<String, Object>) UniqueUtil
						.demarcatePersonName((String) context.get("managerName"));
				manager.putAll(UtilMisc.toMap("statusId", "PARTY_ENABLED", "userLogin", userLogin));
				result = dispatcher.runSync("createPerson", manager);
				managerId = result.get("partyId");
				delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", managerCode),
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", managerId)));

				dispatcher.runSync("createUserLogin",
						UtilMisc.toMap("userLoginId", managerLoginId, "enabled", "Y", "currentPassword", "123456",
								"currentPasswordVerify", "123456", "requirePasswordChange", "Y", "partyId", managerId,
								"userLogin", hrm));
				GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", managerLoginId), false);
				userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
				userLoginNew.store();

				dispatcher.runSync("createEmploymentWorkInfo",
						UtilMisc.toMap("emplPositionTypeId", "SALES_ADMIN_MANAGER", "partyIdFrom", "DSA_ADMIN", "partyIdTo", managerId,
								"fromDate", UtilDateTime.nowTimestamp(), "periodTypeId", "MONTHLY", "rateAmount",
								BigDecimal.ZERO, "userLogin", hrm));

				List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
						UtilMisc.toMap("emplPositionTypeId", "SALES_ADMIN_MANAGER"), null, false);
				for (GenericValue tempGv : emplPosTypeSecGroupConfig) {
					String groupId = tempGv.getString("groupId");
					dispatcher.runSync("addUserLoginToSecurityGroupHR",
							UtilMisc.toMap("userLoginId", managerLoginId, "groupId", groupId, "fromDate",
									UtilDateTime.nowTimestamp(), "organizationId", organizationId, "userLogin", system));
				}
			}
			/*if (UtilValidate.isEmpty(salesmanId)) {
				Object salesmanCode = context.get("salesmanCode");
				Object salesmanLoginId = context.get("salesmanLoginId");
				UniqueUtil.checkPartyCode(delegator, salesmanId, salesmanCode);
				UniqueUtil.checkUserLoginId(delegator, salesmanLoginId);

				Map<String, Object> salesman = (Map<String, Object>) UniqueUtil
						.demarcatePersonName((String) context.get("salesmanName"));
				salesman.putAll(UtilMisc.toMap("statusId", "PARTY_ENABLED", "userLogin", userLogin));
				result = dispatcher.runSync("createPerson", salesman);
				salesmanId = result.get("partyId");
				delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", salesmanCode),
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", salesmanId)));

				dispatcher.runSync("createUserLogin",
						UtilMisc.toMap("userLoginId", salesmanLoginId, "enabled", "Y", "currentPassword", "123456",
								"currentPasswordVerify", "123456", "requirePasswordChange", "Y", "partyId", salesmanId,
								"userLogin", hrm));

				dispatcher.runSync("createEmploymentWorkInfo",
						UtilMisc.toMap("emplPositionTypeId", "SALES_SUP_GT", "partyIdFrom", "SUP_GT_R1A_0001", "partyIdTo", salesmanId,
								"fromDate", UtilDateTime.nowTimestamp(), "periodTypeId", "MONTHLY", "rateAmount",
								BigDecimal.ZERO, "userLogin", hrm));

				List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
						UtilMisc.toMap("emplPositionTypeId", "SALES_SUP_GT"), null, false);
				for (GenericValue tempGv : emplPosTypeSecGroupConfig) {
					String groupId = tempGv.getString("groupId");
					dispatcher.runSync("addUserLoginToSecurityGroupHR",
							UtilMisc.toMap("userLoginId", salesmanLoginId, "groupId", groupId, "fromDate",
									UtilDateTime.nowTimestamp(), "organizationId", organizationId, "userLogin", system));
				}
			}*/
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", managerId, "roleTypeId", "MANAGER", "userLogin", system));
			
			String facilityId = "FA" + productStoreId;
			// createProductStoreChannel
			dispatcher.runSync("createProductStoreChannel",
					UtilMisc.toMap("storeName", context.get("storeName"), "payToPartyId", context.get("payToPartyId"),
							"vatTaxAuthGeoId", context.get("vatTaxAuthGeoId"), "vatTaxAuthPartyId", context.get("vatTaxAuthPartyId"),
							"defaultSalesChannelEnumId", context.get("defaultSalesChannelEnumId"), "defaultCurrencyUomId", context.get("defaultCurrencyUomId"), 
							"reserveOrderEnumId", context.get("reserveOrderEnumId"), "includeOtherCustomer", context.get("includeOtherCustomer"),
							"productStoreId", productStoreId, "requireInventory", "Y",
							"salesMethodChannelEnumId", context.get("salesMethodChannelEnumId"), "showPricesWithVatTax", "N",
							"storeCreditAccountEnumId", "FIN_ACCOUNT", "userLogin", userLogin));
			
			dispatcher.runSync("createProductStoreShipmentMethod",
					UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", "NO_SHIPPING", "partyId",
							"_NA_", "roleTypeId", "CARRIER", "sequenceNumber", Long.valueOf(0), "userLogin", userLogin));
			
			dispatcher.runSync("createProductStorePaymentMethod",
					UtilMisc.toMap("paymentMethodTypeId", "EXT_COD", "productStoreId", productStoreId,
							"paymentServiceTypeEnumId", "PRDS_PAY_EXTERNAL", "userLogin", userLogin));
			dispatcher.runSync("createProductStorePaymentMethod",
					UtilMisc.toMap("paymentMethodTypeId", "EXT_OFFLINE", "productStoreId", productStoreId,
							"paymentServiceTypeEnumId", "PRDS_PAY_EXTERNAL", "userLogin", userLogin));
			
			dispatcher.runSync("createProductStoreCatalogOlb",
					UtilMisc.toMap("productStoreId", productStoreId, "prodCatalogId", context.get("prodCatalogId"), "userLogin", userLogin));
			// createFacility
			/*dispatcher.runSync("updateFacility",
					UtilMisc.toMap("facilityId", facilityId, "facilityCode", facilityId, "facilityName", context.get("storeName"),
							"facilitySize", BigDecimal.ZERO, "facilitySizeUomId", "AREA_m2", "facilityTypeId",
							"WAREHOUSE", "fromDate", String.valueOf(System.currentTimeMillis()), "fromDateManager",
							String.valueOf(System.currentTimeMillis()), "managerPartyId", managerId, "ownerPartyId",
							organizationId, "phoneNumber", context.get("phoneNumber"), "primaryFacilityGroupId", "_NA_",
							"provinceGeoId", context.get("provinceGeoId"), "wardGeoId", context.get("wardGeoId"),
							"address", context.get("address"), "countryGeoId", context.get("countryGeoId"),
							"districtGeoId", context.get("districtGeoId"), "listProductStoreId",
							UtilMisc.toList(UtilMisc.toMap("productStoreId", productStoreId)), "userLogin", userLogin));*/
			dispatcher.runSync("updateFacility",
					UtilMisc.toMap("facilityId", facilityId, "facilityCode", facilityId, "facilityName", context.get("storeName"),
							"facilitySize", BigDecimal.ZERO, "facilitySizeUomId", "AREA_m2", "facilityTypeId",
							"WAREHOUSE", "fromDate", String.valueOf(System.currentTimeMillis()), "fromDateManager",
							String.valueOf(System.currentTimeMillis()), "ownerPartyId",
							organizationId, "phoneNumber", context.get("phoneNumber"), "primaryFacilityGroupId", "FACILITY_INTERNAL",
							"provinceGeoId", context.get("provinceGeoId"), "wardGeoId", context.get("wardGeoId"),
							"address", context.get("address"), "countryGeoId", context.get("countryGeoId"),
							"districtGeoId", context.get("districtGeoId"), "listProductStoreId",
							UtilMisc.toList(UtilMisc.toMap("productStoreId", productStoreId)), "userLogin", userLogin));
			delegator.storeByCondition("ProductStore", UtilMisc.toMap("inventoryFacilityId", facilityId),
					EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)));
			// createProductStoreRole
			dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", managerId, "roleTypeId", "MANAGER",
					"productStoreId", productStoreId, "userLogin", userLogin));
			
			/*dispatcher.runSync("addPosSellerOfStore", UtilMisc.toMap("partyId", salesmanId, "productStoreId", productStoreId,
					"userLogin", userLogin));*/
			/*dispatcher.runSync("addSellerOfStore", UtilMisc.toMap("partyId", salesmanId, "productStoreId", productStoreId,
					"userLogin", userLogin));*/
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> quickUpdateRetailStore(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			GenericValue hrm = PartyUtil.getHrmAdmin(delegator, organizationId);
			Object productStoreId = context.get("productStoreId");
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (UtilValidate.isNotEmpty(productStore)) {
				productStore.set("storeName", context.get("storeName"));
				productStore.set("includeOtherCustomer", context.get("includeOtherCustomer"));
				productStore.set("defaultCurrencyUomId", context.get("defaultCurrencyUomId"));
				productStore.set("reserveOrderEnumId", context.get("reserveOrderEnumId"));
				productStore.set("vatTaxAuthGeoId", context.get("vatTaxAuthGeoId"));
				productStore.set("vatTaxAuthPartyId", context.get("vatTaxAuthPartyId"));
				productStore.store();
				
				checkAndCreateProductStoreCatalogExclusively(delegator, dispatcher, userLogin, productStoreId, context.get("prodCatalogId"));
				
				Object managerId = context.get("managerId");
				//Object salesmanId = context.get("salesmanId");
				if (UtilValidate.isEmpty(managerId)) {
					Object managerCode = context.get("managerCode");
					Object managerLoginId = context.get("managerLoginId");
					if (UtilValidate.isNotEmpty(managerLoginId) && UtilValidate.isNotEmpty(managerCode)) {
						UniqueUtil.checkPartyCode(delegator, managerId, managerCode);
						UniqueUtil.checkUserLoginId(delegator, managerLoginId);

						Map<String, Object> manager = (Map<String, Object>) UniqueUtil
								.demarcatePersonName((String) context.get("managerName"));
						manager.putAll(UtilMisc.toMap("statusId", "PARTY_ENABLED", "userLogin", userLogin));
						result = dispatcher.runSync("createPerson", manager);
						managerId = result.get("partyId");
						delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", managerCode),
								EntityCondition.makeCondition(UtilMisc.toMap("partyId", managerId)));

						dispatcher.runSync("createUserLogin",
								UtilMisc.toMap("userLoginId", managerLoginId, "enabled", "Y", "currentPassword", "123456",
										"currentPasswordVerify", "123456", "requirePasswordChange", "Y", "partyId", managerId,
										"userLogin", hrm));

						dispatcher.runSync("createEmploymentWorkInfo",
								UtilMisc.toMap("emplPositionTypeId", "SALES_ADMIN_MANAGER", "partyIdFrom", "DSA_ADMIN", "partyIdTo", managerId,
										"fromDate", UtilDateTime.nowTimestamp(), "periodTypeId", "MONTHLY", "rateAmount",
										BigDecimal.ZERO, "userLogin", hrm));

						List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
								UtilMisc.toMap("emplPositionTypeId", "SALES_ADMIN_MANAGER"), null, false);
						for (GenericValue tempGv : emplPosTypeSecGroupConfig) {
							String groupId = tempGv.getString("groupId");
							dispatcher.runSync("addUserLoginToSecurityGroupHR",
									UtilMisc.toMap("userLoginId", managerLoginId, "groupId", groupId, "fromDate",
											UtilDateTime.nowTimestamp(), "organizationId", organizationId, "userLogin", system));
						}
					}
				}
				/*if (UtilValidate.isEmpty(salesmanId)) {
					Object salesmanCode = context.get("salesmanCode");
					Object salesmanLoginId = context.get("salesmanLoginId");
					if (UtilValidate.isNotEmpty(salesmanLoginId) && UtilValidate.isNotEmpty(salesmanCode)) {
						UniqueUtil.checkPartyCode(delegator, salesmanId, salesmanCode);
						UniqueUtil.checkUserLoginId(delegator, salesmanLoginId);

						Map<String, Object> salesman = (Map<String, Object>) UniqueUtil
								.demarcatePersonName((String) context.get("salesmanName"));
						salesman.putAll(UtilMisc.toMap("statusId", "PARTY_ENABLED", "userLogin", userLogin));
						result = dispatcher.runSync("createPerson", salesman);
						salesmanId = result.get("partyId");
						delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", salesmanCode),
								EntityCondition.makeCondition(UtilMisc.toMap("partyId", salesmanId)));

						dispatcher.runSync("createUserLogin",
								UtilMisc.toMap("userLoginId", salesmanLoginId, "enabled", "Y", "currentPassword", "123456",
										"currentPasswordVerify", "123456", "requirePasswordChange", "Y", "partyId", salesmanId,
										"userLogin", hrm));

						dispatcher.runSync("createEmploymentWorkInfo",
								UtilMisc.toMap("emplPositionTypeId", "SALES_SUP_GT", "partyIdFrom", "SUP_GT_R1A_0001", "partyIdTo", salesmanId,
										"fromDate", UtilDateTime.nowTimestamp(), "periodTypeId", "MONTHLY", "rateAmount",
										BigDecimal.ZERO, "userLogin", hrm));

						List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
								UtilMisc.toMap("emplPositionTypeId", "SALES_SUP_GT"), null, false);
						for (GenericValue tempGv : emplPosTypeSecGroupConfig) {
							String groupId = tempGv.getString("groupId");
							dispatcher.runSync("addUserLoginToSecurityGroupHR",
									UtilMisc.toMap("userLoginId", salesmanLoginId, "groupId", groupId, "fromDate",
											UtilDateTime.nowTimestamp(), "organizationId", organizationId, "userLogin", system));
						}
					}
				}*/
				List<EntityCondition> conditions = FastList.newInstance();
				if (UtilValidate.isNotEmpty(managerId)) {
					dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", managerId, "roleTypeId", "MANAGER", "userLogin", system));
					dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", managerId, "roleTypeId", "LOG_STOREKEEPER", "userLogin", system));
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "partyId", managerId, "roleTypeId", "MANAGER")));
					List<GenericValue> productStoreRole = delegator.findList("ProductStoreRole",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					if (UtilValidate.isEmpty(productStoreRole)) {
						// createProductStoreRole
						dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", managerId, "roleTypeId", "MANAGER",
								"productStoreId", productStoreId, "userLogin", userLogin));
					}
				}
				
				/*if (UtilValidate.isNotEmpty(salesmanId)) {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "partyId", salesmanId, "roleTypeId", "SELLER")));
					List<GenericValue> productStoreRole = delegator.findList("ProductStoreRole",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					if (UtilValidate.isEmpty(productStoreRole)) {
						dispatcher.runSync("addPosSellerOfStore", UtilMisc.toMap("partyId", salesmanId, "productStoreId", productStoreId,
								"userLogin", userLogin));
					}
				}*/
				
				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", context.get("facilityId")), false);
				facility.set("facilityName", context.get("storeName"));
				facility.store();
				
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", context.get("contactMechPostalAddressId")), false);
				postalAddress.set("address1", context.get("address"));
				postalAddress.set("countryGeoId", context.get("countryGeoId"));
				postalAddress.set("stateProvinceGeoId", context.get("provinceGeoId"));
				postalAddress.set("districtGeoId", context.get("districtGeoId"));
				postalAddress.set("wardGeoId", context.get("wardGeoId"));
				postalAddress.store();
				
				GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", context.get("contactMechTelecomNumberId")), false);
				telecomNumber.set("contactNumber", context.get("phoneNumber"));
				telecomNumber.store();
				
				result.clear();
				result.put("productStoreId", productStoreId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> loadRetailStoreDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String,Object> storeDetail = FastMap.newInstance();
			Object productStoreId = context.get("productStoreId");
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (UtilValidate.isNotEmpty(productStore)) {
				Map<String,Object> idMap = FastMap.newInstance();
				
				storeDetail.put("productStoreId", productStore.get("productStoreId"));
				storeDetail.put("storeName", productStore.get("storeName"));
				storeDetail.put("includeOtherCustomer", productStore.get("includeOtherCustomer"));
				storeDetail.put("defaultCurrencyUomId", productStore.get("defaultCurrencyUomId"));
				storeDetail.put("reserveOrderEnumId", productStore.get("reserveOrderEnumId"));
				storeDetail.put("vatTaxAuthGeoId", productStore.get("vatTaxAuthGeoId"));
				storeDetail.put("vatTaxAuthPartyId", productStore.get("vatTaxAuthPartyId"));
				
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.EQUALS, productStoreId));
				List<GenericValue> productStoreCatalog = delegator.findList("ProductStoreCatalog",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				storeDetail.put("prodCatalogId", EntityUtil.getFieldListFromEntityList(productStoreCatalog, "prodCatalogId", true));
				
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "roleTypeId", "MANAGER")));
				List<GenericValue> productStoreRole = delegator.findList("ProductStoreRole",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				storeDetail.put("managerId", EntityUtil.getFieldListFromEntityList(productStoreRole, "partyId", true));
				
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "roleTypeId", "SELLER")));
				productStoreRole = delegator.findList("ProductStoreRole",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				storeDetail.put("salesmanId", EntityUtil.getFieldListFromEntityList(productStoreRole, "partyId", true));
				
				List<GenericValue> facilities = delegator.findList("FacilityAndPostalAddressAndTelecomNumber",
						EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, productStore.get("inventoryFacilityId")),
						null, null, null, false);
				if (UtilValidate.isNotEmpty(facilities)) {
					GenericValue facility = EntityUtil.getFirst(facilities);
					storeDetail.put("countryGeoId", facility.get("countryGeoId"));
					storeDetail.put("provinceGeoId", facility.get("stateProvinceGeoId"));
					storeDetail.put("districtGeoId", facility.get("districtGeoId"));
					storeDetail.put("wardGeoId", facility.get("wardGeoId"));
					storeDetail.put("phoneNumber", facility.get("contactNumber"));
					storeDetail.put("address", facility.get("address1"));
					
					idMap.put("facilityId", facility.get("facilityId"));
					idMap.put("contactMechPostalAddressId", facility.get("contactMechPostalAddressId"));
					idMap.put("contactMechTelecomNumberId", facility.get("contactMechTelecomNumberId"));
				}
				idMap.put("productStoreId", productStoreId);
				storeDetail.put("idMap", idMap);
				result.put("storeDetail", storeDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> checkAndCreateProductStoreCatalogExclusively(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Object productStoreId, Object prodCatalogId) throws Exception {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		boolean create = true;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)));
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> productStoreCatalogs = delegator.findList("ProductStoreCatalog",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, false);
		if (UtilValidate.isNotEmpty(productStoreCatalogs)) {
			for (GenericValue x : productStoreCatalogs) {
				boolean thruDate = false;
				if (prodCatalogId.equals(x.get("prodCatalogId"))) {
					if (!create) {
						thruDate = true;
					}
					create = false;
				} else {
					thruDate = true;
				}
				if (thruDate) {
					//x.set("thruDate", UtilDateTime.nowTimestamp());
					//x.store();
					Map<String, Object> deleteProdStoreCatalog = dispatcher.runSync("deleteProductStoreCatalogOlb", 
							UtilMisc.toMap("productStoreId", productStoreId, "prodCatalogId", x.get("prodCatalogId"), "fromDate", x.get("fromDate"), "userLogin", userLogin));
					if (ServiceUtil.isError(deleteProdStoreCatalog)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(deleteProdStoreCatalog));
					}
				}
			}
		}
		if (create) {
			Map<String, Object> createProdStoreCatalog = dispatcher.runSync("createProductStoreCatalogOlb",
					UtilMisc.toMap("productStoreId", productStoreId, "prodCatalogId", prodCatalogId, "userLogin", userLogin));
			if (ServiceUtil.isError(createProdStoreCatalog)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdStoreCatalog));
			}
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAgentChain(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "CUSTOMER_CHAIN_GROUP"));
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
            if (UtilValidate.isEmpty(listSortFields)) listSortFields.add("partyId");
            listIterator = delegator.find("Party", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListProductStore service: " + e.toString();
            Debug.log(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
}
