package com.olbius.basesales.product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

public class ProductStoreServices {
	public static final String module = ProductStoreServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStoreByOrg(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (UtilValidate.isNotEmpty(organizationId)) {
				listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationId));
				listIterator = delegator.find("ProductStore", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreByOrg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStorePosByOrg(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		// Map<String, String[]> parameters = (Map<String, String[]>)
		// context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (UtilValidate.isNotEmpty(organizationId)) {
				listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationId));
				listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_POS"));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("productStoreId");
				}
				listIterator = delegator.find("ProductStore", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStorePosByOrg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStoreBySeller(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
    		String partyId = null;
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			partyId = parameters.get("partyId")[0];
    		} else {
    			partyId = userLogin.getString("partyId");
    		}
    		boolean checkOrg = false;
    		if (parameters.containsKey("ckorg") && parameters.get("ckorg").length > 0) {
    			String checkOrgStr = parameters.get("ckorg")[0];
    			if ("Y".equals(checkOrgStr)) checkOrg = true;
    		}
    		listAllConditions.add(EntityCondition.makeCondition("statusId", "PRODSTORE_ENABLED"));
    		listIterator = ProductStoreWorker.getListProductStoreSell(delegator, userLogin, partyId, checkOrg, listAllConditions, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreBySeller service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> getListProductStoreBySeller(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		try {
			String partyId = (String) context.get("partyId");
            if (UtilValidate.isNotEmpty(partyId)) {
                listIterator = ProductStoreWorker.getListProductStoreByRole(delegator, partyId, "SELLER");
            }
        } catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStoreBySeller service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listProductStore", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListProductStoreByCustomer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		try {
			String partyId = (String) context.get("partyId");
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isNotEmpty(partyId)) {
					listIterator = ProductStoreWorker.getListProductStoreByRole(delegator, partyId, "CUSTOMER");
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStoreByCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listProductStore", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> setProductStoreRoleCustomer(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productStoreId = (String) context.get("productStoreId");
		try {
			if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(productStoreId)) {
				List<String> errorMsgs = FastList.newInstance();
				errorMsgs.add(UtilProperties.getMessage(resource_error, "BSProductStoreMustNotBeEmpty", locale));
				errorMsgs.add(UtilProperties.getMessage(resource_error, "BSPartyMustNotBeEmpty", locale));
				return ServiceUtil.returnError(errorMsgs);
			}
			List<String> roleTypeIdsPartyBuy = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.party.buy.enable");
			String roleCustomer = EntityUtilProperties.getPropertyValue(SalesUtil.RESOURCE_PROPERTIES, "role.customer", delegator);
			
			if (SalesPartyUtil.hasRole(delegator, partyId, roleTypeIdsPartyBuy)) {
				// add party to customer of product store
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
				GenericValue productStoreRoleNew = delegator.makeValue("ProductStoreRole");
				productStoreRoleNew.set("productStoreId", productStoreId);
				productStoreRoleNew.set("partyId", partyId);
				productStoreRoleNew.set("roleTypeId", roleCustomer);
				productStoreRoleNew.set("fromDate", nowTimestamp);
				delegator.create(productStoreRoleNew);
			}
		} catch(Exception e){
			String errMsg = "Fatal error calling setProductStoreRoleCustomer service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> addPosSellerOfStore(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Locale locale = (Locale) context.get("locale");
    	//GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String) context.get("partyId");
    	String productStoreId = (String) context.get("productStoreId");
    	
    	try {
    		if (SecurityUtil.hasRole("EMPLOYEE", partyId, delegator)) {
    			// is employee
    			
    			List<EntityCondition> listConds = FastList.newInstance();
    			listConds.add(EntityUtil.getFilterByDateExpr());
    			listConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    			listConds.add(EntityCondition.makeCondition("partyId", partyId));
    			listConds.add(EntityCondition.makeCondition("roleTypeId", "SELLER"));
    			List<GenericValue> listProductRole = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    			if (UtilValidate.isNotEmpty(listProductRole)) {
    				return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DAThisPartyWasSellerOfStore", locale));
    			}
    			
    			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    			tobeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SELLER")));
    			tobeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "POS_EMPLOYEE")));
    			//tobeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SALES_REP")));
    			GenericValue productStoreRole = delegator.makeValue("ProductStoreRole", 
    					UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyId, "roleTypeId", "SELLER", "fromDate", nowTimestamp));
    			tobeStored.add(productStoreRole);
    			
    			List<String> facilityIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", productStoreId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false), "facilityId", true);
    			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    			if (productStore != null) {
    				if (UtilValidate.isNotEmpty(productStore.getString("inventoryFacilityId"))) facilityIds.add(productStore.getString("inventoryFacilityId"));
    			}
    			
    			if (facilityIds != null) {
    				for (String facilityId : facilityIds) {
    					List<GenericValue> facilityRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", "POS_EMPLOYEE")), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
    					if (UtilValidate.isEmpty(facilityRoles)) {
    						GenericValue facilityRole = delegator.makeValue("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", "POS_EMPLOYEE", "fromDate", nowTimestamp));
        					tobeStored.add(facilityRole);
    					}
        			}
    			}
    			
    			List<String> userLoginIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false), "userLoginId", true);
    			if (userLoginIds != null) {
    				for (String userLoginId : userLoginIds) {
    					List<GenericValue> userLoginSecus = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "groupId", "POS_EMPLOYEE", "organizationId", productStore.getString("payToPartyId"))), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
    					if (UtilValidate.isEmpty(userLoginSecus)) {
    						GenericValue userLoginSecu = delegator.makeValue("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", "POS_EMPLOYEE", "organizationId", productStore.getString("payToPartyId"), "fromDate", nowTimestamp));
        					tobeStored.add(userLoginSecu);
    					}
    				}
    			}
    			
    			delegator.storeAll(tobeStored);
    		} else {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAThisPartyIsNotEmployeeOfCurrentOrganization", locale));
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling addPosSellerOfStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
	
	/**
	 * Parameter of product store: 
	 * "productStoreId": id of product store;
	 * "storeName";
	 * "payToPartyId";
	 * "title";
	 * "subtitle";
	 * "manualAuthIsCapture";
	 * "prorateShipping";
	 * "prorateTaxes";
	 * "viewCartOnAdd";
	 * "autoSaveCart";
	 * "autoApproveReviews";
	 * "isDemoStore";
	 * "isImmediatelyFulfilled";
	 * "inventoryFacilityId";
	 * "oneInventoryFacility";
	 * "checkInventory";
	 * "reserveInventory";
	 * "requireInventory";
	 * "balanceResOnOrderCreation";
	 * "defaultCurrencyUomId";
	 * "allowPassword";
	 * "explodeOrderItems";
	 * "checkGcBalance";
	 * "retryFailedAuths";
	 * "headerApprovedStatus";
	 * "itemApprovedStatus";
	 * "digitalItemApprovedStatus";
	 * "headerDeclinedStatus";
	 * "itemDeclinedStatus";
	 * "headerCancelStatus";
	 * "itemCancelStatus";
	 * "visualThemeId";
	 * "storeCreditAccountEnumId";
	 * "usePrimaryEmailUsername";
	 * "requireCustomerRole";
	 * "autoInvoiceDigitalItems";
	 * "reqShipAddrForDigItems";
	 * "showCheckoutGiftOptions";
	 * "selectPaymentTypePerItem";
	 * "showPricesWithVatTax";
	 * "showTaxIsExempt";
	 * "enableAutoSuggestionList";
	 * "enableDigProdUpload";
	 * "prodSearchExcludeVariants";
	 * "autoOrderCcTryExp";
	 * "autoOrderCcTryOtherCards";
	 * "autoOrderCcTryLaterNsf";
	 * "autoApproveInvoice";
	 * "autoApproveOrder";
	 * "shipIfCaptureFails";
	 * "addToCartRemoveIncompat";
	 * "addToCartReplaceUpsell";
	 * "splitPayPrefPerShpGrp";
	 * "managedByLot";
	 * "showOutOfStockProducts";
	 * "defaultSalesChannelEnumId";
	 * "vatTaxAuthGeoId";
	 * "vatTaxAuthPartyId";
	 * "salesMethodChannelEnumId";
	 * "primaryStoreGroupId";
	 * "setOwnerUponIssuance";
	 * "reqReturnInventoryReceive";
	 * "orderDecimalQuantity";
	 * "reserveOrderEnumId";
	 * NULL = PRIMARY_STORE_GROUP_ID, MANUAL_AUTH_IS_CAPTURE, VIEW_CART_ON_ADD, AUTO_SAVE_CART, 
		NULL = AUTO_APPROVE_REVIEWS, IS_DEMO_STORE, REQUIREMENT_METHOD_ENUM_ID, 
		NULL = DEFAULT_PASSWORD, CHECK_GC_BALANCE
		NULL = USE_PRIMARY_EMAIL_USERNAME, REQUIRE_CUSTOMER_ROLE, AUTO_INVOICE_DIGITAL_ITEMS, REQ_SHIP_ADDR_FOR_DIG_ITEMS, 
		NULL = SHOW_CHECKOUT_GIFT_OPTIONS	SELECT_PAYMENT_TYPE_PER_ITEM	SHOW_PRICES_WITH_VAT_TAX	SHOW_TAX_IS_EXEMPT	
		NULL = VAT_TAX_AUTH_GEO_ID	VAT_TAX_AUTH_PARTY_ID	ENABLE_AUTO_SUGGESTION_LIST	ENABLE_DIG_PROD_UPLOAD	PROD_SEARCH_EXCLUDE_VARIANTS	
		NULL = DIG_PROD_UPLOAD_CATEGORY_ID	AUTO_ORDER_CC_TRY_EXP	AUTO_ORDER_CC_TRY_OTHER_CARDS	AUTO_ORDER_CC_TRY_LATER_NSF	AUTO_ORDER_CC_TRY_LATER_MAX
		NULL = SET_OWNER_UPON_ISSUANCE
		NULL = ADD_TO_CART_REMOVE_INCOMPAT	ADD_TO_CART_REPLACE_UPSELL	SPLIT_PAY_PREF_PER_SHP_GRP	MANAGED_BY_LOT
		NULL = ORDER_DECIMAL_QUANTITY	STYLE_SHEET	HEADER_LOGO	HEADER_MIDDLE_BACKGROUND	HEADER_RIGHT_BACKGROUND
		
		PRODUCT_STORE_ID
		STORE_NAME
		COMPANY_NAME
		TITLE
		SUBTITLE
		PAY_TO_PARTY_ID = company
		DAYS_TO_CANCEL_NON_PAY = 30
		PRORATE_SHIPPING = Y
		PRORATE_TAXES = Y
		INVENTORY_FACILITY_ID = WebStoreWarehouse
		RESERVE_ORDER_ENUM_ID = INVRO_FIFO_REC
		REQUIRE_INVENTORY = N
		ORDER_NUMBER_PREFIX = WS
		DEFAULT_LOCALE_STRING = en_US
		DEFAULT_CURRENCY_UOM_ID = USD
		ALLOW_PASSWORD = Y
		EXPLODE_ORDER_ITEMS = N
		RETRY_FAILED_AUTHS = Y
		HEADER_APPROVED_STATUS = ORDER_APPROVED
		ITEM_APPROVED_STATUS = ITEM_APPROVED
		DIGITAL_ITEM_APPROVED_STATUS = ITEM_APPROVED
		HEADER_DECLINED_STATUS = ORDER_REJECTED
		ITEM_DECLINED_STATUS = ITEM_REJECTED
		HEADER_CANCEL_STATUS = ORDER_CANCELLED
		ITEM_CANCEL_STATUS = ITEM_CANCELLED
		AUTH_DECLINED_MESSAGE = There has been a problem with your method of payment. Please try a different method or call customer service.
		AUTH_FRAUD_MESSAGE = Your order has been rejected and your account has been disabled due to fraud.
		AUTH_ERROR_MESSAGE = Problem connecting to payment processor; we will continue to retry and notify you by email.
		VISUAL_THEME_ID = EC_DEFAULT
		AUTO_APPROVE_INVOICE = Y
		AUTO_APPROVE_ORDER = Y
		SHIP_IF_CAPTURE_FAILS = Y
		
		
		Product store BINH THUONG:
				1. IS_IMMEDIATELY_FULFILLED = NULL
				2. ONE_INVENTORY_FACILITY = Y
				3. CHECK_INVENTORY = Y
				4. RESERVE_INVENTORY = Y
				5. BALANCE_RES_ON_ORDER_CREATION = Y
				6. DEFAULT_SALES_CHANNEL_ENUM_ID = WEB_SALES_CHANNEL
				7. STORE_CREDIT_ACCOUNT_ENUM_ID = FIN_ACCOUNT
				8. STORE_CREDIT_VALID_DAYS = 90
				9. REQ_RETURN_INVENTORY_RECEIVE = N
				10. SHOW_OUT_OF_STOCK_PRODUCTS = Y
		Product store POS:
		 		1. IS_IMMEDIATELY_FULFILLED = Y
		 		2. ONE_INVENTORY_FACILITY = N
		 		3. CHECK_INVENTORY = N
		 		4. RESERVE_INVENTORY = N
		 		5. BALANCE_RES_ON_ORDER_CREATION = NULL
		 		6. DEFAULT_SALES_CHANNEL_ENUM_ID = NULL
		 		7. STORE_CREDIT_ACCOUNT_ENUM_ID = NULL
		 		8. STORE_CREDIT_VALID_DAYS = NULL
		 		9. REQ_RETURN_INVENTORY_RECEIVE = NULL
		 		10. SHOW_OUT_OF_STOCK_PRODUCTS = NULL
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createProductStore(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSSuccessK", locale));
		
		String productStoreId = (String) context.get("productStoreId");
		String storeName = (String) context.get("storeName");
		String payToPartyId = (String) context.get("payToPartyId");
		String title = (String) context.get("title");
		String subtitle = (String) context.get("subtitle");
		String defaultCurrencyUomId = (String) context.get("defaultCurrencyUomId");
		String storeCreditAccountEnumId = (String) context.get("storeCreditAccountEnumId");
		String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
		String vatTaxAuthGeoId = (String) context.get("vatTaxAuthGeoId");
		String vatTaxAuthPartyId = (String) context.get("vatTaxAuthPartyId");
		String inventoryFacilityId = (String) context.get("inventoryFacilityId");
		String defaultSalesChannelEnumId = (String) context.get("defaultSalesChannelEnumId");
		if (UtilValidate.isEmpty(inventoryFacilityId)) inventoryFacilityId = null;
		if (UtilValidate.isEmpty(defaultSalesChannelEnumId)) defaultSalesChannelEnumId = null;
		
		String reserveOrderEnumId = (String) context.get("reserveOrderEnumId");
		String showPricesWithVatTax = (String) context.get("showPricesWithVatTax");
		String includeOtherCustomer = (String) context.get("includeOtherCustomer");
		String requireInventory = (String) context.get("requireInventory");
		String isDemoStore = "N";

		String manualAuthIsCapture, autoSaveCart, viewCartOnAdd, autoApproveReviews,
				isImmediatelyFulfilled, oneInventoryFacility, balanceResOnOrderCreation,
				explodeOrderItems, checkGcBalance, usePrimaryEmailUsername, requireCustomerRole,
				selectPaymentTypePerItem, enableAutoSuggestionList, enableDigProdUpload, managedByLot, 
				prorateShipping, prorateTaxes, checkInventory, reserveInventory, allowPassword, retryFailedAuths,
				autoInvoiceDigitalItems, reqShipAddrForDigItems, showCheckoutGiftOptions, showTaxIsExempt,
				prodSearchExcludeVariants, autoOrderCcTryExp, autoOrderCcTryOtherCards, autoOrderCcTryLaterNsf,
				autoApproveInvoice, autoApproveOrder, shipIfCaptureFails, showOutOfStockProducts,
				splitPayPrefPerShpGrp, visualThemeId, addToCartRemoveIncompat, addToCartReplaceUpsell, 
				setOwnerUponIssuance, reqReturnInventoryReceive, orderDecimalQuantity;
		String headerApprovedStatus = "ORDER_APPROVED";
		String itemApprovedStatus = "ITEM_APPROVED";
		String digitalItemApprovedStatus = "ITEM_APPROVED";
		String headerDeclinedStatus = "ORDER_REJECTED";
		String itemDeclinedStatus = "ITEM_REJECTED";
		String headerCancelStatus = "ORDER_CANCELLED";
		String itemCancelStatus = "ITEM_CANCELLED";
		String statusId = "PRODSTORE_ENABLED";
		
		manualAuthIsCapture = autoSaveCart = viewCartOnAdd = autoApproveReviews = explodeOrderItems = usePrimaryEmailUsername 
				= requireCustomerRole = selectPaymentTypePerItem = enableAutoSuggestionList = enableDigProdUpload = managedByLot = "N";
		prorateShipping = checkInventory = reserveInventory = allowPassword = retryFailedAuths = autoInvoiceDigitalItems = reqShipAddrForDigItems 
				= showCheckoutGiftOptions = showTaxIsExempt = prodSearchExcludeVariants = autoOrderCcTryExp = autoOrderCcTryOtherCards 
				= autoOrderCcTryLaterNsf = autoApproveInvoice = autoApproveOrder = shipIfCaptureFails = showOutOfStockProducts = "Y";
		
		visualThemeId = addToCartRemoveIncompat = addToCartReplaceUpsell = setOwnerUponIssuance = reqReturnInventoryReceive = orderDecimalQuantity = null;
		//String primaryStoreGroupId; _NA_, null
		//defaultSalesChannelEnumId = null;
		//productStoreId = productStoreId.toUpperCase();
		
		boolean isPosStore = "POS_SALES_CHANNEL".equals(defaultSalesChannelEnumId);
		
		if (isPosStore) {
			//primaryStoreGroupId = "_NA_"; requireInventory = isDemoStore = "Y"
			prorateTaxes = splitPayPrefPerShpGrp = "N";
			isImmediatelyFulfilled = oneInventoryFacility = balanceResOnOrderCreation = checkGcBalance = "Y";
			setOwnerUponIssuance = reqReturnInventoryReceive = orderDecimalQuantity = "Y";
			//reserveOrderEnumId = "INVRO_FIFO_REC";
		} else {
			//primaryStoreGroupId = null; requireInventory = isDemoStore = "N"
			//setOwnerUponIssuance = reqReturnInventoryReceive = orderDecimalQuantity = reserveOrderEnumId = defaultSalesChannelEnumId = null;
			prorateTaxes = splitPayPrefPerShpGrp = addToCartRemoveIncompat = addToCartReplaceUpsell = "Y";
			isImmediatelyFulfilled = oneInventoryFacility = balanceResOnOrderCreation = checkGcBalance = "N";
			visualThemeId = "ACEADMIN";
		}
		
		if (UtilValidate.isEmpty(productStoreId)) {
			productStoreId = delegator.getNextSeqId("ProductStore");
		}
		
		Map<String, Object> productStoreCtx = UtilMisc.<String, Object>toMap("productStoreId", productStoreId, "storeName", storeName, 
				"payToPartyId", payToPartyId, "title", title, "subtitle", subtitle, "manualAuthIsCapture", manualAuthIsCapture, "prorateShipping", prorateShipping, 
				"prorateTaxes", prorateTaxes, "viewCartOnAdd", viewCartOnAdd, "autoSaveCart", autoSaveCart, "autoApproveReviews", autoApproveReviews, 
				"isDemoStore", isDemoStore, "isImmediatelyFulfilled", isImmediatelyFulfilled, "oneInventoryFacility", oneInventoryFacility, 
				"checkInventory", checkInventory, "reserveInventory", reserveInventory, "requireInventory", requireInventory, 
				"balanceResOnOrderCreation", balanceResOnOrderCreation, "defaultCurrencyUomId", defaultCurrencyUomId, "allowPassword", allowPassword, 
				"explodeOrderItems", explodeOrderItems, "checkGcBalance", checkGcBalance, "retryFailedAuths", retryFailedAuths, "headerApprovedStatus", headerApprovedStatus, 
				"itemApprovedStatus", itemApprovedStatus, "digitalItemApprovedStatus", digitalItemApprovedStatus, "headerDeclinedStatus", headerDeclinedStatus, 
				"itemDeclinedStatus", itemDeclinedStatus, "headerCancelStatus", headerCancelStatus, "itemCancelStatus", itemCancelStatus, "visualThemeId", visualThemeId, 
				"storeCreditAccountEnumId", storeCreditAccountEnumId, "usePrimaryEmailUsername", usePrimaryEmailUsername, "requireCustomerRole", requireCustomerRole, 
				"autoInvoiceDigitalItems", autoInvoiceDigitalItems, "reqShipAddrForDigItems", reqShipAddrForDigItems, "showCheckoutGiftOptions", showCheckoutGiftOptions, 
				"selectPaymentTypePerItem", selectPaymentTypePerItem, "showPricesWithVatTax", showPricesWithVatTax, "showTaxIsExempt", showTaxIsExempt, 
				"enableAutoSuggestionList", enableAutoSuggestionList, "enableDigProdUpload", enableDigProdUpload, "prodSearchExcludeVariants", prodSearchExcludeVariants, 
				"autoOrderCcTryExp", autoOrderCcTryExp, "autoOrderCcTryOtherCards", autoOrderCcTryOtherCards, "autoOrderCcTryLaterNsf", autoOrderCcTryLaterNsf, 
				"autoApproveInvoice", autoApproveInvoice, "autoApproveOrder", autoApproveOrder, "shipIfCaptureFails", shipIfCaptureFails, 
				"addToCartRemoveIncompat", addToCartRemoveIncompat, "addToCartReplaceUpsell", addToCartReplaceUpsell, "splitPayPrefPerShpGrp", splitPayPrefPerShpGrp, 
				"managedByLot", managedByLot, "showOutOfStockProducts", showOutOfStockProducts, "defaultSalesChannelEnumId", defaultSalesChannelEnumId, 
				"vatTaxAuthGeoId", vatTaxAuthGeoId, "vatTaxAuthPartyId", vatTaxAuthPartyId, "salesMethodChannelEnumId", salesMethodChannelEnumId, 
				"setOwnerUponIssuance", setOwnerUponIssuance, "reqReturnInventoryReceive", reqReturnInventoryReceive, 
				"orderDecimalQuantity", orderDecimalQuantity, "reserveOrderEnumId", reserveOrderEnumId, "inventoryFacilityId", inventoryFacilityId, "includeOtherCustomer", includeOtherCustomer,
				"statusId", statusId);

		// remove: "primaryStoreGroupId", primaryStoreGroupId
		GenericValue productStore = delegator.makeValue("ProductStore", productStoreCtx);
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		GenericValue productStoreRole = delegator.makeValue("ProductStoreRole");
		productStoreRole.set("productStoreId", productStoreId);
		productStoreRole.set("roleTypeId", "OWNER");
		productStoreRole.set("partyId", payToPartyId);
		productStoreRole.set("fromDate", nowTimestamp);
		
		GenericValue productStoreFacility = null;
		if (!isPosStore && UtilValidate.isNotEmpty(inventoryFacilityId)) {
			Long sequence = new Long(1);
			productStoreFacility = delegator.makeValue("ProductStoreFacility");
			productStoreFacility.set("productStoreId", productStoreId);
			productStoreFacility.set("facilityId", inventoryFacilityId);
			productStoreFacility.set("fromDate", nowTimestamp);
			productStoreFacility.set("sequenceNum", sequence);
		}
		try {
			delegator.create(productStore);
			delegator.create(productStoreRole);
			
			if (productStoreFacility != null) delegator.create(productStoreFacility);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productStoreId", productStoreId);
		successResult.put("storeName", storeName);
		return successResult;
	}
	
	public static Map<String, Object> createProductStoreChannel(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String productStoreId = (String) context.get("productStoreId");
		String storeName = (String) context.get("storeName");
		String payToPartyId = (String) context.get("payToPartyId");
		String title = (String) context.get("title");
		String subtitle = (String) context.get("subtitle");
		String defaultCurrencyUomId = (String) context.get("defaultCurrencyUomId");
		String storeCreditAccountEnumId = (String) context.get("storeCreditAccountEnumId");
		String vatTaxAuthGeoId = (String) context.get("vatTaxAuthGeoId");
		String vatTaxAuthPartyId = (String) context.get("vatTaxAuthPartyId");
		String inventoryFacilityId = (String) context.get("inventoryFacilityId");
		String defaultSalesChannelEnumId = (String) context.get("defaultSalesChannelEnumId");
		
		String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
		String reserveOrderEnumId = (String) context.get("reserveOrderEnumId");
		String showPricesWithVatTax = (String) context.get("showPricesWithVatTax");
		String includeOtherCustomer = (String) context.get("includeOtherCustomer");
		String requireInventory = (String) context.get("requireInventory");
		
		try {
			/*List<GenericValue> listSalesMethodChannelEnum = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", salesMethodChannelEnumId, "enumTypeId", "SALES_METHOD_CHANNEL"), null, false);
			if (UtilValidate.isNotEmpty(listSalesMethodChannelEnum)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisRecordIsExisted", locale));
			}
			
			Map<String, Object> salesMethodChannelEnumCtx = UtilMisc.toMap("enumId", salesMethodChannelEnumId, "enumTypeId", "SALES_METHOD_CHANNEL", "description", storeName);
			GenericValue salesMethodChannelEnum = delegator.makeValue("Enumeration", salesMethodChannelEnumCtx);
			delegator.create(salesMethodChannelEnum);
			
			Map<String, Object> productStoreCtx = UtilMisc.toMap("productStoreId", salesMethodChannelEnumId, "storeName", storeName, 
					"payToPartyId", payToPartyId, "title", title, "subtitle", subtitle, "defaultCurrencyUomId", defaultCurrencyUomId, 
					"storeCreditAccountEnumId", storeCreditAccountEnumId, "vatTaxAuthGeoId", vatTaxAuthGeoId, "vatTaxAuthPartyId", vatTaxAuthPartyId, 
					"inventoryFacilityId", inventoryFacilityId, "defaultSalesChannelEnumId", defaultSalesChannelEnumId, 
					"salesMethodChannelEnumId", salesMethodChannelEnumId, "userLogin", userLogin, "locale", locale);
			Map<String, Object> resultValue = dispatcher.runSync("createProductStoreOlb", productStoreCtx);
			if (ServiceUtil.isError(resultValue)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultValue));
			}*/
			
			List<GenericValue> listProductStore = delegator.findByAnd("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), null, false);
			if (UtilValidate.isNotEmpty(listProductStore)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisRecordIsExisted", locale));
			}
			if (UtilValidate.isEmpty(reserveOrderEnumId)) reserveOrderEnumId = null;
			
			Map<String, Object> productStoreCtx = UtilMisc.toMap("productStoreId", productStoreId, "storeName", storeName, 
					"payToPartyId", payToPartyId, "title", title, "subtitle", subtitle, "defaultCurrencyUomId", defaultCurrencyUomId, 
					"storeCreditAccountEnumId", storeCreditAccountEnumId, "vatTaxAuthGeoId", vatTaxAuthGeoId, "vatTaxAuthPartyId", vatTaxAuthPartyId, 
					"inventoryFacilityId", inventoryFacilityId, "defaultSalesChannelEnumId", defaultSalesChannelEnumId, 
					"salesMethodChannelEnumId", salesMethodChannelEnumId, "reserveOrderEnumId", reserveOrderEnumId, 
					"showPricesWithVatTax", showPricesWithVatTax, "includeOtherCustomer", includeOtherCustomer, "requireInventory", requireInventory, 
					"userLogin", userLogin, "locale", locale);
			Map<String, Object> resultValue = dispatcher.runSync("createProductStoreOlb", productStoreCtx);
			if (ServiceUtil.isError(resultValue)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultValue));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreChannel service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		//successResult.put("productStoreId", salesMethodChannelEnumId);
		successResult.put("productStoreId", productStoreId);
		successResult.put("storeName", storeName);
		return successResult;
	}
	
	public static Map<String, Object> editProductStore(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String aProductStore = (String) context.get("aProductStore");
		JSONObject listJson = JSONObject.fromObject(aProductStore);
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		if (UtilValidate.isNotEmpty(listJson)) {
			String productStoreId = null;
			String storeName = null;
			String payToPartyId = null;
			String title = null;
			String subtitle = null;
			String defaultCurrencyUomId = null;
			String storeCreditAccountEnumId = null;
			String inventoryFacilityId = null;
			String reserveOrderEnumId = null;
			String showPricesWithVatTax = null;
			String includeOtherCustomer = null;
			String requireInventory = null;
			if (listJson.containsKey("productStoreId")) productStoreId = listJson.getString("productStoreId");
			if (listJson.containsKey("storeName")) storeName = listJson.getString("storeName");
			if (listJson.containsKey("payToPartyId")) payToPartyId = listJson.getString("payToPartyId");
			if (listJson.containsKey("title")) title = listJson.getString("title");
			if (listJson.containsKey("subtitle")) subtitle = listJson.getString("subtitle");
			if (listJson.containsKey("defaultCurrencyUomId")) defaultCurrencyUomId = listJson.getString("defaultCurrencyUomId");
			if (listJson.containsKey("storeCreditAccountEnumId")) storeCreditAccountEnumId = listJson.getString("storeCreditAccountEnumId");
			if (listJson.containsKey("inventoryFacilityId")) inventoryFacilityId = listJson.getString("inventoryFacilityId");
			if (listJson.containsKey("reserveOrderEnumId")) reserveOrderEnumId = listJson.getString("reserveOrderEnumId");
			if (listJson.containsKey("showPricesWithVatTax")) showPricesWithVatTax = listJson.getString("showPricesWithVatTax");
			if (listJson.containsKey("includeOtherCustomer")) includeOtherCustomer = listJson.getString("includeOtherCustomer");
			if (listJson.containsKey("requireInventory")) requireInventory = listJson.getString("requireInventory");
			//String salesMethodChannelEnumId = listJson.getString("salesMethodChannelEnumId");
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (UtilValidate.isNotEmpty(productStore)) {
				if (UtilValidate.isEmpty(reserveOrderEnumId)) reserveOrderEnumId = null;
				// update sales channel
				/*if (storeName != null 
						&& !storeName.equals(productStore.getString("storeName"))
						&& productStoreId.equals(productStore.getString("salesMethodChannelEnumId"))) {
					GenericValue salesMethodChannelEnum = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", productStore.getString("salesMethodChannelEnumId")), false);
					if (salesMethodChannelEnum != null) {
						salesMethodChannelEnum.set("description", storeName);
						delegator.store(salesMethodChannelEnum);
					}
				}*/
				
				//productStore.set("productStoreId", productStoreId);
				productStore.set("storeName", storeName);
				productStore.set("payToPartyId", payToPartyId);
				productStore.set("title", title);
				productStore.set("subtitle", subtitle);
				
				if (UtilValidate.isNotEmpty(inventoryFacilityId)) {
					GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", inventoryFacilityId), false);
					if (facility != null) {
						//GenericValue productStoreFacility = delegator.findOne("ProductStoreFacility", UtilMisc.toMap("productStoreId", productStoreId, "facilityId", inventoryFacilityId, "fromDate", nowTimestamp), false);
						List<GenericValue> productStoreFacility = FastList.newInstance();
						List<EntityCondition> listAllConditions = FastList.newInstance();
						listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
						listAllConditions.add(EntityCondition.makeCondition("facilityId", inventoryFacilityId));
                        try {
                        	productStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
                        	productStoreFacility = EntityUtil.filterByDate(productStoreFacility);
                        } catch (GenericEntityException e) {
                            String errMsg = "OLBIUS: Fatal error when findList ProductStoreFacility: " + e.toString();
                            Debug.logError(e, errMsg, module);
                            return ServiceUtil.returnError(errMsg);
                        }
						if (UtilValidate.isEmpty(productStoreFacility)) {
							GenericValue productStoreFacilityGV = delegator.makeValue("ProductStoreFacility");
							productStoreFacilityGV.set("productStoreId", productStoreId);
							productStoreFacilityGV.set("facilityId", inventoryFacilityId);
							productStoreFacilityGV.set("fromDate", nowTimestamp);
							delegator.create(productStoreFacilityGV);
						}
						
						productStore.set("inventoryFacilityId", inventoryFacilityId);
					}
				}
				productStore.set("defaultCurrencyUomId", defaultCurrencyUomId);
				//productStore.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
				//productStore.set("storeCreditAccountEnumId", storeCreditAccountEnumId);
				productStore.set("reserveOrderEnumId", reserveOrderEnumId);
				productStore.set("showPricesWithVatTax", showPricesWithVatTax);
				productStore.set("includeOtherCustomer", includeOtherCustomer);
				productStore.set("requireInventory", requireInventory);
				
				productStore.store();
			}
			Map<String, Object> resultSc = ServiceUtil.returnSuccess();
			resultSc.put("productStoreId", productStoreId);
			return resultSc;
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStoreCatalog(DispatchContext ctx, Map<String, Object> context) {
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
    				listSortFields.add("sequenceNum");
    				listSortFields.add("-thruDate");
    				listSortFields.add("-fromDate");
    			}
    			listIterator = delegator.find("ProductStoreCatalogDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createProductStoreCatalog(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String prodCatalogId = (String) context.get("prodCatalogId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			Long sequenceNum = (Long) context.get("sequenceNum");
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> prodStoreCatalogExisted = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(conds), UtilMisc.toSet("prodCatalogId"), null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreCatalogExisted)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisDataIsExisted", locale));
			}
			
			if (UtilValidate.isEmpty(fromDate)) {
				fromDate = UtilDateTime.nowTimestamp();
			}
			
			// create product store catalog
			GenericValue newData = delegator.makeValue("ProductStoreCatalog");
			newData.put("productStoreId", productStoreId);
			newData.put("prodCatalogId", prodCatalogId);
			newData.put("fromDate", fromDate);
			newData.put("thruDate", thruDate);
			newData.put("sequenceNum", sequenceNum);
			delegator.create(newData);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	public static Map<String, Object> deleteProductStoreCatalog(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String prodCatalogId = (String) context.get("prodCatalogId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
			conds.add(EntityCondition.makeCondition("fromDate", fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			List<GenericValue> prodStoreCatalogs = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreCatalogs)) {
				for (GenericValue item : prodStoreCatalogs) {
					item.set("thruDate", nowTimestamp);
					item.set("sequenceNum", null);
				}
				delegator.storeAll(prodStoreCatalogs);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductStoreCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStoreFalicity(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("sequenceNum");
    				listSortFields.add("-thruDate");
    				listSortFields.add("-fromDate");
    			}
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				listIterator = delegator.find("ProductStoreFacilityDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreFalicity service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createProductStoreFacility(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String facilityId = (String) context.get("facilityId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			Long sequenceNum = (Long) context.get("sequenceNum");
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("facilityId", facilityId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> prodStoreCatalogExisted = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(conds), UtilMisc.toSet("facilityId"), null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreCatalogExisted)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisDataIsExisted", locale));
			}
			
			if (UtilValidate.isEmpty(fromDate)) {
				fromDate = UtilDateTime.nowTimestamp();
			}
			
			// create product store catalog
			GenericValue newData = delegator.makeValue("ProductStoreFacility");
			newData.put("productStoreId", productStoreId);
			newData.put("facilityId", facilityId);
			newData.put("fromDate", fromDate);
			newData.put("thruDate", thruDate);
			newData.put("sequenceNum", sequenceNum);
			delegator.create(newData);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreFacility service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	public static Map<String, Object> deleteProductStoreFacility(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String facilityId = (String) context.get("facilityId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("facilityId", facilityId));
			conds.add(EntityCondition.makeCondition("fromDate", fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			List<GenericValue> prodStoreFacilities = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreFacilities)) {
				for (GenericValue item : prodStoreFacilities) {
					item.set("thruDate", nowTimestamp);
					item.set("sequenceNum", null);
				}
				delegator.storeAll(prodStoreFacilities);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductStoreFacility service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	public static Map<String, Object> createProductStoreRole(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String,Object> successResult= ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String partyId = (String) context.get("partyId");
			String roleTypeId = (String) context.get("roleTypeId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
			if (productStore == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundProductStoreHasProductStoreIdIs", UtilMisc.toMap("productStoreId", productStoreId), locale));
			}
			
			GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
			if (partyRole == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSTheRoleTypeIsNotAvailable", locale));
			}
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> prodStoreRoleExisted = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), UtilMisc.toSet("roleTypeId"), null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreRoleExisted)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisDataIsExisted", locale));
			}
			
			if (UtilValidate.isEmpty(fromDate)) {
				fromDate = UtilDateTime.nowTimestamp();
			}
			
			GenericValue productStoreRole = delegator.makeValue("ProductStoreRole");
			productStoreRole.put("roleTypeId", roleTypeId);
			productStoreRole.put("partyId", partyId);
			productStoreRole.put("productStoreId", productStoreId);
			productStoreRole.put("fromDate", fromDate);
			productStoreRole.put("thruDate", thruDate);
			
			delegator.create(productStoreRole);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> deleteProductStoreRole(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreId = (String) context.get("productStoreId");
			String roleTypeId = (String) context.get("roleTypeId");
			String partyId = (String) context.get("partyId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			conds.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("fromDate", fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			List<GenericValue> prodStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreRoles)) {
				for (GenericValue item : prodStoreRoles) {
					item.set("thruDate", nowTimestamp);
					item.set("sequenceNum", null);
				}
				delegator.storeAll(prodStoreRoles);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductStoreRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStoreGroup(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			listIterator = delegator.find("ProductStoreGroup", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStoreGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createProductStoreGroupOlb(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String productStoreGroupId = (String) context.get("productStoreGroupId");
			if (UtilValidate.isEmpty(productStoreGroupId)) {
				productStoreGroupId = delegator.getNextSeqId("ProductStoreGroup");
				context.put("productStoreGroupId", productStoreGroupId);
			}
			
			Map<String, Object> createStoreGroupCtx = ServiceUtil.setServiceFields(dispatcher, "createProductStoreGroup", context, userLogin, null, locale);
			Map<String, Object> createStoreGroupResult = dispatcher.runSync("createProductStoreGroup", createStoreGroupCtx);
        	if (ServiceUtil.isError(createStoreGroupResult)) {
    			Debug.logError(ServiceUtil.getErrorMessage(createStoreGroupResult), module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        	}
        	
        	productStoreGroupId = (String) createStoreGroupResult.get("productStoreGroupId");
        	successResult.put("productStoreGroupId", productStoreGroupId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductStoreGroupOlb service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}

    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStoreGroupMember(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String productStoreGroupId = SalesUtil.getParameter(parameters, "productStoreGroupId");
			if (UtilValidate.isNotEmpty(productStoreGroupId)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreGroupId", productStoreGroupId));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				listIterator = delegator.find("ProductStoreGroupAndMemberAndOwner", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch(Exception e){
			String errMsg = "Fatal error calling jqGetListProductStoreGroupMember service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductStoreAddToGroup(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("primaryStoreGroupId", null));
			listIterator = delegator.find("ProductStore", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch(Exception e){
    		String errMsg = "Fatal error calling jqGetListProductStoreAddToGroup service: " + e.toString();
    		Debug.log(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Long getNextSequeceProductStoreGroupMember(Delegator delegator, String productProductGroupId) throws GenericEntityException {
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productStoreGroupId", productProductGroupId));
		conditions.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.NOT_EQUAL, null));
		GenericValue sequenceNumberGV = EntityUtil.getFirst(delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-sequenceNum"), null, false));
		Long sequenceNumber = new Long(1);
		if (sequenceNumberGV != null) {
			sequenceNumber = sequenceNumberGV.getLong("sequenceNum");
		}
		return ++sequenceNumber;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addProductStoresToStoreGroup(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		try {
			String productStoreGroupId = (String) context.get("productStoreGroupId");
			List<String> listProductStoreId = (List<String>) context.get("productStoreIds[]");
			GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId", productStoreGroupId), false);
			if (UtilValidate.isEmpty(productStoreGroup)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductStoreGroupHasIdIsNotFound", UtilMisc.toMap("productStoreGroupId", productStoreGroupId), locale));
			}
			if (UtilValidate.isEmpty(listProductStoreId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseProductStore", locale));
			}
			
			Map<String, Object> productCtx = FastMap.newInstance();
			List<EntityCondition> listConds = FastList.newInstance();
			Long sequenceNum = getNextSequeceProductStoreGroupMember(delegator, productStoreGroupId);
			EntityFindOptions opts = new EntityFindOptions();
			opts.setMaxRows(1);
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			for (String productStoreId : listProductStoreId) {
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				if (productStore == null) {
					continue;
				}
				
				listConds.clear();
				listConds.add(EntityCondition.makeCondition("productStoreGroupId", productStoreGroupId));
				listConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				listConds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> productStoreMemberExists = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(listConds), UtilMisc.toSet("productStoreId"), null, opts, false);
				if (UtilValidate.isNotEmpty(productStoreMemberExists)) {
					continue;
				}
				
				productCtx.clear();
				productCtx.put("productStoreGroupId", productStoreGroupId);
				productCtx.put("productStoreId", productStoreId);
				productCtx.put("sequenceNum", sequenceNum);
				productCtx.put("fromDate", nowTimestamp);
				productCtx.put("userLogin", userLogin);
				Map<String, Object> addResult = dispatcher.runSync("createProductStoreGroupMember", productCtx);
				if (ServiceUtil.isError(addResult)) {
					Debug.logWarning(ServiceUtil.getErrorMessage(addResult), module);
				} else {
					sequenceNum++;
					if (UtilValidate.isEmpty(productStore.get("primaryStoreGroupId"))) {
						productStore.set("primaryStoreGroupId", productStoreGroupId);
						delegator.store(productStore);
					}
				}
			}
		} catch (Exception e) {
    		String errMsg = "Fatal error calling addProductStoresToStoreGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
    	}
		return successResult;
	}
    
    public static Map<String, Object> removeProductStoresInStoreGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		String productStoreGroupId = (String) context.get("productStoreGroupId");
    		String productStoreId = (String) context.get("productStoreId");
    		Timestamp fromDate = (Timestamp) context.get("fromDate");
    		GenericValue productStoreMember = delegator.findOne("ProductStoreGroupMember", UtilMisc.toMap("productStoreGroupId", productStoreGroupId, "productStoreId", productStoreId, "fromDate", fromDate), false);
    		if (UtilValidate.isEmpty(productStoreMember)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordIsNotFound", locale));
    		}
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		
    		productStoreMember.put("thruDate", nowTimestamp);
    		delegator.store(productStoreMember);
    		
    		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    		if (productStore != null && productStoreGroupId.equals(productStore.getString("primaryStoreGroupId"))) {
    			productStore.put("primaryStoreGroupId", null);
    			
    			// find other store group replace
    			List<EntityCondition> conds = FastList.newInstance();
        		conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
        		conds.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.NOT_EQUAL, productStoreGroupId));
        		conds.add(EntityUtil.getFilterByDateExpr());
        		GenericValue productStoreGroupOther = EntityUtil.getFirst(delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(conds), null, UtilMisc.toList("fromDate"), null, false));
        		if (productStoreGroupOther != null) {
        			productStore.put("primaryStoreGroupId", productStoreGroupOther.get("productStoreGroupId"));
        		}
        		delegator.store(productStore);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling removeProductStoresInStoreGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
    	}
    	return successResult;
    }
    
    public static Map<String, Object> changeProductStoreStatus(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String productStoreId = (String) context.get("productStoreId");
			String statusId = (String) context.get("statusId");
			Timestamp statusDate = UtilDateTime.nowTimestamp();
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (productStore == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundProductStoreHasProductStoreIdIs", UtilMisc.toMap("productStoreId", productStoreId), locale));
			}
			String currentStatusId = productStore.getString("statusId");
			try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", currentStatusId, "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "BSErrorCouldNotChangeStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorCouldNotChangeStatus",locale) + e.getMessage() + ").");
            }
			productStore.set("statusId", statusId);
			delegator.store(productStore);
			
			// record this status change in ProductStoreStatus table
			GenericValue productStoreStatus = delegator.makeValue("ProductStoreStatus", UtilMisc.toMap("productStoreId", productStoreId, "statusId", statusId, "statusDate", statusDate));
			productStoreStatus.create();

			successResult.put("productStoreId", productStoreId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeProductStoreStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
}
