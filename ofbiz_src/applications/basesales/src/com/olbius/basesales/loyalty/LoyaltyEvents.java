package com.olbius.basesales.loyalty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

public class LoyaltyEvents {
	public static final String module = LoyaltyEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    public static final String MULTI_ROW_DELIMITER = "_";
    public static final String MULTI_ROW_DELIMITER_SEQ = "_r_";
    public static final String MULTI_ROW_DELIMITER_SEQ_COND = "_c_";
    public static final String MULTI_ROW_DELIMITER_SEQ_ACT = "_a_";
    
	@SuppressWarnings("unchecked")
	public static String createLoyaltyAdvance(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization((Delegator)request.getAttribute("delegator"),(String) userLogin.get("userLoginId"));
        
        if (!(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_LOYALTY_NEW"))) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String loyaltyId = request.getParameter("loyaltyId");
        String loyaltyName = request.getParameter("loyaltyName");
        String[] productStoreIdsStr = request.getParameterValues("productStoreIds");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String loyaltyText = request.getParameter("loyaltyText");
        String loyaltyTypeId = request.getParameter("loyaltyTypeId");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        
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
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        	return "error";
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        String loyaltyIdSuccess = "";
        try {
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = null;
        	if (UtilValidate.isNotEmpty(roleTypeIdsStr)) roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	List<String> productStoreIds = null;
        	if (UtilValidate.isNotEmpty(productStoreIdsStr)) productStoreIds = Arrays.asList(productStoreIdsStr);
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("loyaltyId", loyaltyId);
        	contextMap.put("loyaltyName", loyaltyName);
        	contextMap.put("productStoreIds", productStoreIds);
        	contextMap.put("roleTypeIds", roleTypeIds);
        	contextMap.put("loyaltyText", loyaltyText);
        	contextMap.put("loyaltyTypeId", loyaltyTypeId);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("organizationId", organizationPartyId);
        	contextMap.put("locale", locale);
        	Map<String, Object> result0 = dispatcher.runSync("createLoyaltyCustom", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = SalesUtil.processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            loyaltyIdSuccess = (String) result0.get("loyaltyId");
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the productId
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                
	                if (UtilValidate.isEmpty(ruleName)) {
	                	continue;
	                }
	                
	                String loyaltyRuleId = "";
	                Map<String, Object> result1 = dispatcher.runSync("createLoyaltyRuleCustom", 
	                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyIdSuccess, "ruleName", ruleName, "userLogin", userLogin, "locale", locale));
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = SalesUtil.processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                loyaltyRuleId = (String) result1.get("loyaltyRuleId");
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String loyaltyApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
	    	    	        String usePriceWithTax = null;
	    	    	        String isReturnOrder = null;
	    	    	        String isRemoveCond = "N";
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyApplEnumId" + thisSuffixSeq)) {
	    	                	loyaltyApplEnumId = (String) paramMap.remove("loyaltyApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("usePriceWithTax" + thisSuffixSeq)) {
	    	                	usePriceWithTax = (String) paramMap.remove("usePriceWithTax" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("isReturnOrder" + thisSuffixSeq)) {
	    	                	isReturnOrder = (String) paramMap.remove("isReturnOrder" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (UtilValidate.isNotEmpty(condValue)) {
	    	                	Map<String, Object> result2 = dispatcher.runSync("createLoyaltyCondCustom", 
		    	                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyIdSuccess, 
		    	                				"loyaltyRuleId", loyaltyRuleId, 
		    	                				"productIdListCond", productIdListCond, "loyaltyApplEnumId", loyaltyApplEnumId, 
		    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, "isReturnOrder", isReturnOrder,
		    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale, "usePriceWithTax", usePriceWithTax));
		    	                // no values for price and paramMap (a context for adding attributes)
		    	                controlDirective = SalesUtil.processResult(result2, request);
		    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                	try {
		    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty condition callback", null);
		    	                    } catch (Exception e1) {
		    	                        Debug.logError(e1, module);
		    	                    }
		    	                    return "error";
		    	                }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyCondSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        String loyaltyApplEnumIdAction = null;
	    	    	        String loyaltyActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
    	    	        	String isRemoveAction = "N";
    	    	        	String loyaltyActionOperEnumId = null;
    	    	        	String ratingTypeId = null;
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("ratingTypeId" + thisSuffixSeq)) {
	    	    	        	ratingTypeId = (String) paramMap.remove("ratingTypeId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyApplEnumIdAction" + thisSuffixSeq)) {
	    	                	loyaltyApplEnumIdAction = (String) paramMap.remove("loyaltyApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyActionEnumId" + thisSuffixSeq)) {
	    	                	loyaltyActionEnumId = (String) paramMap.remove("loyaltyActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                if (paramMap.containsKey("loyaltyActionOperEnumId" + thisSuffixSeq)) {
	    	                	loyaltyActionOperEnumId = (String) paramMap.remove("loyaltyActionOperEnumId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (quantity != null || amount != null || ratingTypeId != null) {
	    	                	Map<String, Object> result3 = dispatcher.runSync("createLoyaltyActionCustom", 
	    	                    		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyIdSuccess, 
	    		                				"loyaltyRuleId", loyaltyRuleId, "loyaltyApplEnumId", loyaltyApplEnumIdAction, 
	    	                    				 "loyaltyActionEnumId", loyaltyActionEnumId, "quantity", quantity,
	    	                    				 "actionValue", ratingTypeId, "amount", amount, "userLogin", userLogin, "locale", locale, 
	    	                    				"operatorEnumId", loyaltyActionOperEnumId));
	    	                    // no values for price and paramMap (a context for adding attributes)
	    	                    controlDirective = SalesUtil.processResult(result3, request);
	    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                    	try {
	    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty action callback", null);
	    	                        } catch (Exception e1) {
	    	                            Debug.logError(e1, module);
	    	                        }
	    	                        return "error";
	    	                    }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyActionSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
        	if (UtilValidate.isNotEmpty(errMsgList)) {
        		try {
                    TransactionUtil.rollback(beganTx, "Have error when process", null);
                } catch (Exception e2) {
                    Debug.logError(e2, module);
                }
            	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            	return "error";
            } else {
            	// commit the transaction
                try {
                    TransactionUtil.commit(beganTx);
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        }
        request.setAttribute("loyaltyId", loyaltyIdSuccess);
        return "success";
    }
	
	@SuppressWarnings("unchecked")
	public static String updateLoyaltyAdvance(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization((Delegator)request.getAttribute("delegator"),(String) userLogin.get("userLoginId"));
        
        if (!(SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALES_LOYALTY_EDIT"))) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String loyaltyId = request.getParameter("loyaltyId");
        String loyaltyName = request.getParameter("loyaltyName");
        String loyaltyText = request.getParameter("loyaltyText");
        String loyaltyTypeId = request.getParameter("loyaltyTypeId");
        String[] productStoreIdsStr = request.getParameterValues("productStoreIds");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        
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
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        	return "error";
        }
        if (UtilValidate.isEmpty(loyaltyId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSLoyaltyIdMustNotBeEmpty", locale));
        	return "error";
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        try {
        	GenericValue loyalty = delegator.findOne("Loyalty", UtilMisc.toMap("loyaltyId", loyaltyId), false);
        	if (loyalty == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
                return "error";
        	}
        	
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = null;
        	if (UtilValidate.isNotEmpty(roleTypeIdsStr)) roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	List<String> productStoreIds = null;
        	if (UtilValidate.isNotEmpty(productStoreIdsStr)) productStoreIds = Arrays.asList(productStoreIdsStr);
        	
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("loyaltyId", loyaltyId);
        	contextMap.put("loyaltyName", loyaltyName);
        	contextMap.put("loyaltyText", loyaltyText);
        	contextMap.put("loyaltyTypeId", loyaltyTypeId);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("organizationId", organizationPartyId);
        	contextMap.put("locale", locale);
        	Map<String, Object> result0 = dispatcher.runSync("updateLoyalty", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = SalesUtil.processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Update loyalty callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            
            List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	// update list product store loyalty appl
            List<GenericValue> listProductStoreLoyaltyAppl = delegator.findByAnd("ProductStoreLoyaltyAppl", 
            		UtilMisc.toMap("loyaltyId", loyaltyId), null, false);
            if (listProductStoreLoyaltyAppl != null) {
            	List<String> productStoreIdAppls = EntityUtil.getFieldListFromEntityList(listProductStoreLoyaltyAppl, "productStoreId", true);
            	for (GenericValue productStoreLoyaltyAppl : listProductStoreLoyaltyAppl) {
            		if (productStoreIds != null && productStoreIds.contains(productStoreLoyaltyAppl.getString("productStoreId"))) {
            			if (fromDate.equals(productStoreLoyaltyAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productStoreLoyaltyAppl record + create new record with fromDate
            				productStoreLoyaltyAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(productStoreLoyaltyAppl);
            				GenericValue newProductStoreLoyaltyAppl = delegator.makeValue("ProductStoreLoyaltyAppl");
            				newProductStoreLoyaltyAppl.put("productStoreId", productStoreLoyaltyAppl.get("productStoreId"));
            				newProductStoreLoyaltyAppl.put("loyaltyId", productStoreLoyaltyAppl.get("loyaltyId"));
            				newProductStoreLoyaltyAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductStoreLoyaltyAppl);
            			}
            		} else {
            			// this record was deleted
            			productStoreLoyaltyAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(productStoreLoyaltyAppl);
            		}
            	}
            	if (productStoreIds != null) {
            		for (String productStoreId : productStoreIds) {
                		if (productStoreIdAppls.contains(productStoreId)) {
                			// no action
                		} else {
                			// create new
                			GenericValue newProductStoreLoyaltyAppl = delegator.makeValue("ProductStoreLoyaltyAppl");
                			newProductStoreLoyaltyAppl.put("productStoreId", productStoreId);
                			newProductStoreLoyaltyAppl.put("loyaltyId", loyaltyId);
                			newProductStoreLoyaltyAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductStoreLoyaltyAppl);
                		}
                	}
            	}
            }
            
            // update list role type loyalty appl
            List<GenericValue> listRoleTypeLoyaltyAppl = delegator.findByAnd("LoyaltyRoleTypeAppl", 
            		UtilMisc.toMap("loyaltyId", loyaltyId), null, false);
            if (listRoleTypeLoyaltyAppl != null) {
            	List<String> roleTypeIdAppls = EntityUtil.getFieldListFromEntityList(listRoleTypeLoyaltyAppl, "roleTypeId", true);
            	for (GenericValue roleTypeLoyaltyAppl : listRoleTypeLoyaltyAppl) {
            		if (roleTypeIds != null && roleTypeIds.contains(roleTypeLoyaltyAppl.getString("roleTypeId"))) {
            			if (fromDate.equals(roleTypeLoyaltyAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this loyaltyRoleTypeAppl record + create new record with fromDate
            				roleTypeLoyaltyAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(roleTypeLoyaltyAppl);
            				GenericValue newLoyaltyRoleTypeAppl = delegator.makeValue("LoyaltyRoleTypeAppl");
            				newLoyaltyRoleTypeAppl.put("roleTypeId", roleTypeLoyaltyAppl.get("roleTypeId"));
            				newLoyaltyRoleTypeAppl.put("loyaltyId", roleTypeLoyaltyAppl.get("loyaltyId"));
            				newLoyaltyRoleTypeAppl.put("fromDate", fromDate);
            				tobeStored.add(newLoyaltyRoleTypeAppl);
            			}
            		} else {
            			// this record was deleted
            			roleTypeLoyaltyAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(roleTypeLoyaltyAppl);
            		}
            	}
            	if (roleTypeIds != null) {
            		for (String roleTypeId : roleTypeIds) {
                		if (roleTypeIdAppls.contains(roleTypeId)) {
                			// no action
                		} else {
                			// create new
                			GenericValue newLoyaltyRoleTypeAppl = delegator.makeValue("LoyaltyRoleTypeAppl");
                			newLoyaltyRoleTypeAppl.put("roleTypeId", roleTypeId);
                			newLoyaltyRoleTypeAppl.put("loyaltyId", loyaltyId);
                			newLoyaltyRoleTypeAppl.put("fromDate", fromDate);
            				tobeStored.add(newLoyaltyRoleTypeAppl);
                		}
                	}
            	}
            }
            delegator.storeAll(tobeStored);
            
            
            List<GenericValue> listLoyaltyProduct = delegator.findByAnd("LoyaltyProduct", UtilMisc.toMap("loyaltyId", loyaltyId), null, false);
            List<GenericValue> listLoyaltyCategory = delegator.findByAnd("LoyaltyCategory", UtilMisc.toMap("loyaltyId", loyaltyId), null, false);
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        String loyaltyRuleId = "";
	    	        boolean isRuleExisted = false;
	    	        String isRemoveRule = "N";
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the ruleName
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                // get the ruleId
	                if (paramMap.containsKey("loyaltyRuleId" + thisSuffix)) {
	                	loyaltyRuleId = (String) paramMap.remove("loyaltyRuleId" + thisSuffix);
	                }
	                if (paramMap.containsKey("isRemoveRule" + thisSuffix)) {
	                	isRemoveRule = (String) paramMap.remove("isRemoveRule" + thisSuffix);
	                }

	                GenericValue loyaltyRule = delegator.findOne("LoyaltyRule", UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId), false);
	                if (loyaltyRule != null) {
	                	isRuleExisted = true;
	                }
	                
	                if ("Y".equals(isRemoveRule)) {
	                	if (isRuleExisted) {
	                		// delete condition
	                		List<GenericValue> listLoyaltyProd = delegator.findByAnd("LoyaltyProduct", 
	                        		UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId), null, false);
	    	        		if (UtilValidate.isNotEmpty(listLoyaltyProd)){
	    	        			for (GenericValue loyaltyProd : listLoyaltyProd){
	    	        				loyaltyProd.remove();
	    	        			}
	    	        		}
	    	        		List<GenericValue> listLoyaltyCat = delegator.findByAnd("LoyaltyCategory", 
	    	        				UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId), null, false);
	    	        		if (UtilValidate.isNotEmpty(listLoyaltyCat)){
	    	        			for (GenericValue loyaltyCat : listLoyaltyCat){
	    	        				loyaltyCat.remove();
	    	        			}
	    	        		}
	                		List<GenericValue> listLoyaltyCond = delegator.findByAnd("LoyaltyCondition", 
	                        		UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId), null, false);
	                		if (UtilValidate.isNotEmpty(listLoyaltyCond)){
	                			for (GenericValue loyaltyCond : listLoyaltyCond){
	                				dispatcher.runSync("deleteLoyaltyCond", UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, 
	                						"loyaltyCondSeqId", loyaltyCond.getString("loyaltyCondSeqId"), "userLogin", userLogin, "locale", locale));
	                			}
	                		}
	                		List<GenericValue> listLoyaltyAction = delegator.findByAnd("LoyaltyAction", 
	                        		UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId), null, false);
	                		if (UtilValidate.isNotEmpty(listLoyaltyAction)){
	                			for (GenericValue loyaltyAction : listLoyaltyAction){
	                				dispatcher.runSync("deleteLoyaltyAction", UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, 
	                						"loyaltyActionSeqId", loyaltyAction.getString("loyaltyActionSeqId"), "userLogin", userLogin, "locale", locale));
	                			}
	                		}
	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteLoyaltyRule", UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "userLogin", userLogin, "locale", locale));
	    	                // no values for price and paramMap (a context for adding attributes)
	    	                controlDirective = SalesUtil.processResult(result2, request);
	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                	try {
	    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete loyalty rule callback", null);
	    	                    } catch (Exception e1) {
	    	                        Debug.logError(e1, module);
	    	                    }
	    	                    return "error";
	    	                }
	    	                continue;
	                	} else {
	                		continue;
	                	}
	                }
	                
	                if (UtilValidate.isEmpty(ruleName)) {
	                	continue;
	                }
	                
	                Map<String, Object> result1 = null;
	                if (isRuleExisted) {
	                	result1 = dispatcher.runSync("updateLoyaltyRule", 
		                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, 
		                				"ruleName", ruleName, "userLogin", userLogin, "locale", locale));
	                } else {
		                result1 = dispatcher.runSync("createLoyaltyRuleCustom", 
		                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "ruleName", ruleName, "userLogin", userLogin, "locale", locale));
		                if (result1 != null) loyaltyRuleId = (String) result1.get("loyaltyRuleId");
	                }
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = SalesUtil.processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		String loyaltyCondSeqId = null;
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String loyaltyApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
	    	    	        String usePriceWithTax = null;
	    	    	        String isReturnOrder = null;
    	    	        	String isRemoveCond = "N";
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("loyaltyCondSeqId" + thisSuffixSeq)) {
	    	    	        	loyaltyCondSeqId = (String) paramMap.remove("loyaltyCondSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isCondExisted = false;
	    	                GenericValue loyaltyCond = delegator.findOne("LoyaltyCondition", UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId), false);
	    	                if (loyaltyCond != null) isCondExisted = true;
	    	    	        
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	if (isRuleExisted && isCondExisted) {
		    	                	// delete condition
	    	    	        		List<GenericValue> listLoyaltyCondProd = delegator.findByAnd("LoyaltyProduct", 
	    	                        		UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId), null, false);
	    	    	        		if (UtilValidate.isNotEmpty(listLoyaltyCondProd)){
	    	    	        			for (GenericValue loyaltyProd : listLoyaltyCondProd){
	    	    	        				loyaltyProd.remove();
	    	    	        			}
	    	    	        		}
	    	    	        		List<GenericValue> listLoyaltyCondCat = delegator.findByAnd("LoyaltyCategory", 
	    	    	        				UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId), null, false);
	    	    	        		if (UtilValidate.isNotEmpty(listLoyaltyCondCat)){
	    	    	        			for (GenericValue loyaltyCat : listLoyaltyCondCat){
	    	    	        				loyaltyCat.remove();
	    	    	        			}
	    	    	        		}
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteLoyaltyCond", UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = SalesUtil.processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete loyalty condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyApplEnumId" + thisSuffixSeq)) {
	    	                	loyaltyApplEnumId = (String) paramMap.remove("loyaltyApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("usePriceWithTax" + thisSuffixSeq)) {
	    	                	usePriceWithTax = (String) paramMap.remove("usePriceWithTax" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("isReturnOrder" + thisSuffixSeq)) {
	    	                	isReturnOrder = (String) paramMap.remove("isReturnOrder" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (isRuleExisted && isCondExisted) {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("updateLoyaltyCond", 
			    	                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, 
			    	                				"loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId, 
			    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
			    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale, 
			    	                				"usePriceWithTax", usePriceWithTax, "isReturnOrder", isReturnOrder));
	    	                		//"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
	    	                		// "productIdListCond", productIdListCond, "loyaltyApplEnumId", loyaltyApplEnumId, 
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = SalesUtil.processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing update loyalty condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                
			    	                List<GenericValue> tobeStoredCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> tobeDeletedCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> listLoyaltyProductCond = EntityUtil.filterByAnd(listLoyaltyProduct, 
			    	                		UtilMisc.toMap("loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId));
			    	                if (listLoyaltyProductCond != null) {
			    	                	List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listLoyaltyProductCond, "productId", true);
			    	                	for (GenericValue productAppl : listLoyaltyProductCond) {
			    	                		if (productIdListCond.contains(productAppl.getString("productId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(productAppl);
			    	                		}
			    	                	}
			    	                	for (String productId : productIdListCond) {
			    	                		if (productIdAppls.contains(productId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newLoyaltyProduct = delegator.makeValue("LoyaltyProduct");
			    	                			newLoyaltyProduct.put("loyaltyId", loyaltyId);
			    	                			newLoyaltyProduct.put("loyaltyRuleId", loyaltyRuleId);
			    	                			newLoyaltyProduct.put("loyaltyActionSeqId", "_NA_");
			    	                			newLoyaltyProduct.put("loyaltyCondSeqId", loyaltyCondSeqId);
			    	                			newLoyaltyProduct.put("productId", productId);
			    	                			newLoyaltyProduct.put("loyaltyApplEnumId", loyaltyApplEnumId);
			    	            				tobeStoredCond.add(newLoyaltyProduct);
			    	                		}
			    	                	}
			    	                }
			    	                List<GenericValue> listLoyaltyCategoryCond = EntityUtil.filterByAnd(listLoyaltyCategory, 
			    	                		UtilMisc.toMap("loyaltyRuleId", loyaltyRuleId, "loyaltyCondSeqId", loyaltyCondSeqId));
			    	                if (listLoyaltyCategoryCond != null) {
			    	                	List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listLoyaltyCategoryCond, "productCategoryId", true);
			    	                	for (GenericValue categoryAppl : listLoyaltyCategoryCond) {
			    	                		if (productCatIdListCond.contains(categoryAppl.getString("productCategoryId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(categoryAppl);
			    	                		}
			    	                	}
			    	                	for (String productCategoryId : productCatIdListCond) {
			    	                		if (categoryIdAppls.contains(productCategoryId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newLoyaltyCategory = delegator.makeValue("LoyaltyCategory");
			    	                			newLoyaltyCategory.put("loyaltyId", loyaltyId);
			    	                			newLoyaltyCategory.put("loyaltyRuleId", loyaltyRuleId);
			    	                			newLoyaltyCategory.put("loyaltyActionSeqId", "_NA_");
			    	                			newLoyaltyCategory.put("loyaltyCondSeqId", loyaltyCondSeqId);
			    	                			newLoyaltyCategory.put("productCategoryId", productCategoryId);
			    	                			newLoyaltyCategory.put("andGroupId", "_NA_");
			    	                			newLoyaltyCategory.put("loyaltyApplEnumId", loyaltyApplEnumId);
			    	                			newLoyaltyCategory.put("includeSubCategories", includeSubCategories);
			    	            				tobeStoredCond.add(newLoyaltyCategory);
			    	                		}
			    	                	}
			    	                }
			    	                delegator.storeAll(tobeStoredCond);
			    	                delegator.removeAll(tobeDeletedCond);
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyCondSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
		    	                }
	    	                } else {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("createLoyaltyCondCustom", 
		 	    	                		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, 
		 	    	                				"loyaltyRuleId", loyaltyRuleId, 
		 	    	                				"productIdListCond", productIdListCond, "loyaltyApplEnumId", loyaltyApplEnumId, 
		 	    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		 	    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
		 	    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale, 
		 	    	                				"usePriceWithTax", usePriceWithTax, "isReturnOrder", isReturnOrder));
		 	    	                // no values for price and paramMap (a context for adding attributes)
		 	    	                controlDirective = SalesUtil.processResult(result2, request);
		 	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		 	    	                	try {
		 	    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty condition callback", null);
		 	    	                    } catch (Exception e1) {
		 	    	                        Debug.logError(e1, module);
		 	    	                    }
		 	    	                    return "error";
		 	    	                }
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyCondSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = SalesUtil.getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        String loyaltyActionSeqId = null;
	    	    	        String loyaltyApplEnumIdAction = null;
	    	    	        String loyaltyActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
	    	    	        String isRemoveAction = "N";
	    	    	        String loyaltyActionOperEnumId = null;
	    	    	        String ratingTypeId = null;
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("loyaltyActionSeqId" + thisSuffixSeq)) {
	    	    	        	loyaltyActionSeqId = (String) paramMap.remove("loyaltyActionSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isActionExisted = false;
	    	                GenericValue loyaltyAction = delegator.findOne("LoyaltyAction", UtilMisc.toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyActionSeqId", loyaltyActionSeqId), false);
	    	                if (loyaltyAction != null) isActionExisted = true;
	    	                
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	if (isRuleExisted && isActionExisted) {
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteLoyaltyAction", UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, "loyaltyRuleId", loyaltyRuleId, "loyaltyActionSeqId", loyaltyActionSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = SalesUtil.processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete loyalty action callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("ratingTypeId" + thisSuffixSeq)) {
	    	    	        	ratingTypeId = (String) paramMap.remove("ratingTypeId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyApplEnumIdAction" + thisSuffixSeq)) {
	    	                	loyaltyApplEnumIdAction = (String) paramMap.remove("loyaltyApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("loyaltyActionEnumId" + thisSuffixSeq)) {
	    	                	loyaltyActionEnumId = (String) paramMap.remove("loyaltyActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                if (paramMap.containsKey("loyaltyActionOperEnumId" + thisSuffixSeq)) {
	    	                	loyaltyActionOperEnumId = (String) paramMap.remove("loyaltyActionOperEnumId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (isRuleExisted && isActionExisted) {
		    	                if (quantity != null || amount != null || ratingTypeId != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("updateLoyaltyAction", 
		    	                    		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, 
		    		                				"loyaltyRuleId", loyaltyRuleId, "loyaltyActionSeqId", loyaltyActionSeqId, 
		    	                    				"loyaltyActionEnumId", loyaltyActionEnumId, "quantity", quantity,
		    	                    				"actionValue", ratingTypeId, "amount", amount, "userLogin", userLogin, "locale", locale, 
		    	                    				"operatorEnumId", loyaltyActionOperEnumId));
		    	                	// "includeSubCategories", includeSubCategoriesAction, 
		    	                	// "productCatIdListAction", productCatIdListAction, 
		    	                	// "productIdListAction", productIdListAction, "loyaltyApplEnumId", loyaltyApplEnumIdAction, 
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = SalesUtil.processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing update loyalty action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyActionSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
		    	                }
	    	                } else {
	    	                	if (quantity != null || amount != null || ratingTypeId != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("createLoyaltyActionCustom", 
		    	                    		UtilMisc.<String, Object>toMap("loyaltyId", loyaltyId, 
		    		                				"loyaltyRuleId", loyaltyRuleId, "loyaltyApplEnumId", loyaltyApplEnumIdAction, "actionValue", ratingTypeId, 
		    	                    				"loyaltyActionEnumId", loyaltyActionEnumId, "quantity", quantity, "amount", amount, 
		    	                    				"userLogin", userLogin, "locale", locale, "operatorEnumId", loyaltyActionOperEnumId));
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = SalesUtil.processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create loyalty action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"BSLoyaltyApplyValueOfActionInTheRuleMustBeNotEmpty", UtilMisc.toMap("loyaltyActionSeqId", (j + 1), "loyaltyRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
            // commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        request.setAttribute("loyaltyId", loyaltyId);
        return "success";
    }
	
}
