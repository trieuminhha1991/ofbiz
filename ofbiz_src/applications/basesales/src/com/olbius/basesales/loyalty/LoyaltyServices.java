package com.olbius.basesales.loyalty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.security.util.SecurityUtil;

public class LoyaltyServices {
	public static final String module = LoyaltyServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    protected final static char[] smartChars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '2', '3', '4', '5', '6', '7', '8', '9' };
    
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListLoyalty(DispatchContext ctx, Map<String, Object> context) {
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
			if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_LOYALTY_VIEW")) {
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission",locale));
	        }
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
			listIterator = delegator.find("Loyalty", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLoyalty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListLoyaltyCode(DispatchContext ctx, Map<String, Object> context) {
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
			if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_LOYALTY_VIEW")) {
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission",locale));
	        }
			String loyaltyId = null;
    		if (parameters.containsKey("loyaltyId") && parameters.get("loyaltyId").length > 0) {
    			loyaltyId = parameters.get("loyaltyId")[0];
			}
    		if (UtilValidate.isNotEmpty(loyaltyId)) {
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("-createdDate");
    			}
    			listAllConditions.add(EntityCondition.makeCondition("loyaltyId", loyaltyId));
    			listIterator = delegator.find("LoyaltyCode", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLoyaltyCode service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> updateLoyaltyCodeCustom(DispatchContext ctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSUpdateSuccessful", locale));
    	
    	String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	
    	String loyaltyCodeId = (String) context.get("loyaltyCodeId");
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
            
            Map<String, Object> contextCtx = ServiceUtil.setServiceFields(dispatcher, "updateLoyaltyCode", context, userLogin, null, locale);
            Map<String, Object> resultValue = dispatcher.runSync("updateLoyaltyCode", contextCtx);
            if (ServiceUtil.isError(resultValue)) {
            	return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
            }
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling updateLoyaltyCodeCustom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("loyaltyCodeId", loyaltyCodeId);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> jqGetListLoyaltyCodeEmail(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String loyaltyCodeId = null;
			if (parameters.containsKey("loyaltyCodeId") && parameters.get("loyaltyCodeId").length > 0) {
				loyaltyCodeId = parameters.get("loyaltyCodeId")[0];
			}
			if (UtilValidate.isNotEmpty(loyaltyCodeId)) {
				listAllConditions.add(EntityCondition.makeCondition("loyaltyCodeId", loyaltyCodeId));
				listIterator = delegator.find("LoyaltyCodeEmail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLoyaltyCodeEmail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> jqGetListLoyaltyCodeParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String loyaltyCodeId = null;
			if (parameters.containsKey("loyaltyCodeId") && parameters.get("loyaltyCodeId").length > 0) {
				loyaltyCodeId = parameters.get("loyaltyCodeId")[0];
			}
			if (UtilValidate.isNotEmpty(loyaltyCodeId)) {
				listAllConditions.add(EntityCondition.makeCondition("loyaltyCodeId", loyaltyCodeId));
				listIterator = delegator.find("LoyaltyCodeParty", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLoyaltyCodeParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createLoyaltyCodeSet(DispatchContext dctx, Map<String, ? extends Object> context) {
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Long quantity = (Long) context.get("quantity");
        int codeLength = (Integer) context.get("codeLength");
        String loyaltyCodeLayout = (String) context.get("loyaltyCodeLayout");

        // For LoyaltyCodes we give the option not to use chars that are easy to mix up like 0<>O, 1<>I, ...
        boolean useSmartLayout = false;
        boolean useNormalLayout = false;
        if ("smart".equals(loyaltyCodeLayout)) {
            useSmartLayout = true;
        } else if ("normal".equals(loyaltyCodeLayout)) {
            useNormalLayout = true;
        }

        String newLoyaltyCodeId = "";
        StringBuilder bankOfNumbers = new StringBuilder();
        bankOfNumbers.append(UtilProperties.getMessage(resource, "BSLoyaltyCodesCreated", locale));
        for (long i = 0; i < quantity; i++) {
            Map<String, Object> createLoyaltyCodeMap = null;
            boolean foundUniqueNewCode = false;
            long count = 0;

            while (!foundUniqueNewCode) {
                if (useSmartLayout) {
                	newLoyaltyCodeId = RandomStringUtils.random(codeLength, smartChars);
                } else if (useNormalLayout) {
                	newLoyaltyCodeId = RandomStringUtils.randomAlphanumeric(codeLength);
                }
                GenericValue existingLoyaltyCode = null;
                try {
                	existingLoyaltyCode = delegator.findOne("LoyaltyCode", UtilMisc.toMap("loyaltyCodeId", newLoyaltyCodeId), true);
                }
                catch (GenericEntityException e) {
                    Debug.logWarning("Could not find LoyaltyCode for just generated ID: " + newLoyaltyCodeId, module);
                }
                if (existingLoyaltyCode == null) {
                    foundUniqueNewCode = true;
                }

                count++;
                if (count > 999999) {
                    return ServiceUtil.returnError("Unable to locate unique LoyaltyCode! Length [" + codeLength + "]");
                }
            }
            try {
                Map<String, Object> newContext = dctx.makeValidContext("createLoyaltyCode", "IN", context);
                newContext.put("loyaltyCodeId", newLoyaltyCodeId);
                createLoyaltyCodeMap = dispatcher.runSync("createLoyaltyCode", newContext);
            } catch (GenericServiceException err) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSLoyaltyCodeCannotBeCreated", locale), null, null, createLoyaltyCodeMap);
            }
            if (ServiceUtil.isError(createLoyaltyCodeMap)) {
                // what to do here? try again?
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSLoyaltyCodeCannotBeCreated", locale), null, null, createLoyaltyCodeMap);
            }
            bankOfNumbers.append((String) createLoyaltyCodeMap.get("loyaltyCodeId"));
            bankOfNumbers.append(",");
        }

        return ServiceUtil.returnSuccess(bankOfNumbers.toString());
    }
	
	public static Map<String, Object> processLoyaltyPoint(DispatchContext ctx, Map<String, Object> context) throws GeneralException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String isReturnOrder = (String) context.get("isReturnOrder");
		String orderId = (String) context.get("orderId");
		String returnId = (String) context.get("returnId");
		
		LoyaltyWorker loyaltyWorker = new LoyaltyWorker(delegator, dispatcher, locale);
		loyaltyWorker.setUserLogin(userLogin);
		loyaltyWorker.setIsReturnOrder(isReturnOrder);
		if (UtilValidate.isNotEmpty(orderId)){
			loyaltyWorker.setOrderId(orderId);
		}
		if (UtilValidate.isNotEmpty(returnId)){
			loyaltyWorker.setReturnId(returnId);
		}
		
		Map<String, Object> info = loyaltyWorker.getInformationOrder();
		String partyId = (String) info.get("partyId");
		if (!partyId.equals("_NA_")){
			loyaltyWorker.doLoyalties();
		}
		
		return successResult;
	}
	
	public static Map<String, Object> getLoyaltyPointCustomer(DispatchContext ctx, Map<String, Object> context) throws GeneralException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		ShoppingCart cart = (ShoppingCart) context.get("cart");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LoyaltyWorker loyaltyWorker = new LoyaltyWorker(delegator, dispatcher, locale);
		
		loyaltyWorker.setUserLogin(userLogin);
		loyaltyWorker.setIsReturnOrder("N");
		loyaltyWorker.setCart(cart);
		
		BigDecimal totalPointCustomer = BigDecimal.ZERO;
		BigDecimal totalPointOrder = BigDecimal.ZERO;
		
		Map<String, Object> info = loyaltyWorker.getInformationOrder();
		String partyId = (String) info.get("partyId");
		if (!partyId.equals("_NA_")){
			loyaltyWorker.doLoyalties();
			totalPointCustomer = loyaltyWorker.getTotalPointCustomer();
			totalPointOrder = loyaltyWorker.getTotalPointOrder();
		}
		
		successResult.put("totalPointOrder", totalPointOrder);
		successResult.put("totalPointCustomer", totalPointCustomer);
		
		return successResult;
	}
	
}
