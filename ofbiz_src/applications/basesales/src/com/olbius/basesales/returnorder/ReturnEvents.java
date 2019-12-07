package com.olbius.basesales.returnorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class ReturnEvents {
	public static final String module = ReturnEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
	public static String createReturnHeaderItemAdjustmentCustomer(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
        
        if (!(securityOlb.olbiusHasPermission(session, null, "MODULE", "RETURN_ORDER_NEW") || securityOlb.olbiusHasPermission(userLogin, "CREATE", "ENTITY", "DIS_RETURNORDER"))) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String orderId = request.getParameter("orderId");
        String returnId = request.getParameter("returnId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        List<String> errMsgList = new ArrayList<String>();
        
        boolean beganTx = false;
        String returnIdSuccess = returnId;
        try {
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	        	List<Map<String, Object>> tobeStored = new LinkedList<Map<String, Object>>();
	            for (int i = 0; i < rowCount; i++) {
	            	String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	            	
	            	String _rowSubmit = null;
	            	if (paramMap.containsKey("_rowSubmit" + thisSuffix)) {
	            		_rowSubmit = (String) paramMap.remove("_rowSubmit" + thisSuffix);
	                }
	            	if (!"Y".equals(_rowSubmit)) {
	            		continue;
	            	}
	            	
	            	// process return item
	    	        BigDecimal amount = null;
	    	        String lotId = null;
	    	        String comments = null;
	    	        String correspondingProductId = null;
	    	        String customerReferenceId = null;
	    	        Timestamp datetimeManufactured = null;
	    	        String description = null;
	    	        BigDecimal exemptAmount = null;
	    	        String expectedItemStatus = null;
	    	        Timestamp expiredDate = null;
	    	        String includeInShipping = null;
	    	        String includeInTax = null;
	    	        Timestamp createdDate = nowTimestamp;
	    	        String createdByUserLogin = userLogin.getString("userLoginId");
	    	        Timestamp lastModifiedDate = nowTimestamp;
	    	        String lastModifiedByUserLogin = userLogin.getString("userLoginId");
	    	        // locale
	    	        // userLogin
	    	        String orderAdjustmentId = null;
	    	        // orderId
	    	        String orderItemSeqId = null;
	    	        String overrideGlAccountId = null;
	    	        String primaryGeoId = null;
	    	        String productFeatureId = null;
	    	        String productId = null;
	    	        String quantityUomId = null;
	    	        String productPromoId, productPromoRuleId, productPromoActionSeqId;
	    	        BigDecimal receivedQuantity = null;
	    	        String returnAdjustmentId, returnAdjustmentTypeId;
	    	        // returnId
	    	        String returnItemResponseId = null;
	    	        String returnItemSeqId = null;
	    	        String returnItemTypeId = null;
	    	        BigDecimal returnPrice = null;
	    	        BigDecimal returnQuantity = null;
	    	        String returnReasonId = null;
	    	        String returnTypeId = null;
	    	        String secondaryGeoId = null;
	    	        String shipGroupSeqId = null;
	    	        BigDecimal sourcePercentage = null;
	    	        String sourceReferenceId = null;
	    	        String statusId = null;
	    	        String taxAuthGeoId, taxAuthPartyId, taxAuthorityRateSeqId;
	    	        
	    	        productPromoId = productPromoRuleId = productPromoActionSeqId = null;
	    	        returnAdjustmentId = returnAdjustmentTypeId = null;
	    	        taxAuthGeoId = taxAuthPartyId = taxAuthorityRateSeqId = null;
	    	        
	    	        if (paramMap.containsKey("amount" + thisSuffix)) {
	    	        	String amountStr = (String) paramMap.remove("amount" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(amountStr)) {
    	                	// parse the quantity
    	                    try {
    	                    	amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
    	                    } catch (Exception e) {
    	                        Debug.logWarning(e, "Problems parsing quantity string: " + amountStr, module);
    	                        amount = BigDecimal.ZERO;
    	                    }
    	                }
	                }
	    	        if (paramMap.containsKey("lotId" + thisSuffix)) {
	    	        	lotId = (String) paramMap.remove("lotId" + thisSuffix);
	                }
	    	        if (paramMap.containsKey("comments" + thisSuffix)) {
	    	        	comments = (String) paramMap.remove("comments" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("correspondingProductId" + thisSuffix)) {
	    	        	correspondingProductId = (String) paramMap.remove("correspondingProductId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("customerReferenceId" + thisSuffix)) {
	    	        	customerReferenceId = (String) paramMap.remove("customerReferenceId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("datetimeManufactured" + thisSuffix)) {
	    	        	String datetimeManufacturedStr = (String) paramMap.remove("datetimeManufactured" + thisSuffix);
	    	        	datetimeManufactured = new Timestamp(Long.parseLong(datetimeManufacturedStr));
	    	        }
	    	        if (paramMap.containsKey("description" + thisSuffix)) {
	    	        	description = (String) paramMap.remove("description" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("exemptAmount" + thisSuffix)) {
	    	        	String exemptAmountStr = (String) paramMap.remove("exemptAmount" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(exemptAmountStr)) {
    	                	// parse the quantity
    	                    try {
    	                    	exemptAmount = (BigDecimal) ObjectType.simpleTypeConvert(exemptAmountStr, "BigDecimal", null, locale);
    	                    } catch (Exception e) {
    	                        Debug.logWarning(e, "Problems parsing quantity string: " + exemptAmountStr, module);
    	                        exemptAmount = BigDecimal.ZERO;
    	                    }
    	                }
	                }
	    	        if (paramMap.containsKey("expectedItemStatus" + thisSuffix)) {
	    	        	expectedItemStatus = (String) paramMap.remove("expectedItemStatus" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("expiredDate" + thisSuffix)) {
	    	        	String expiredDateStr = (String) paramMap.remove("expiredDate" + thisSuffix);
	    	        	expiredDate = new Timestamp(Long.parseLong(expiredDateStr));
	    	        }
	    	        if (paramMap.containsKey("includeInShipping" + thisSuffix)) {
	    	        	includeInShipping = (String) paramMap.remove("includeInShipping" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("includeInTax" + thisSuffix)) {
	    	        	includeInTax = (String) paramMap.remove("includeInTax" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("orderAdjustmentId" + thisSuffix)) {
	    	        	orderAdjustmentId = (String) paramMap.remove("orderAdjustmentId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
	    	        	orderItemSeqId = (String) paramMap.remove("orderItemSeqId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("overrideGlAccountId" + thisSuffix)) {
	    	        	overrideGlAccountId = (String) paramMap.remove("overrideGlAccountId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("productFeatureId" + thisSuffix)) {
	    	        	productFeatureId = (String) paramMap.remove("productFeatureId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("productId" + thisSuffix)) {
	    	        	productId = (String) paramMap.remove("productId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("productPromoActionSeqId" + thisSuffix)) {
	    	        	productPromoActionSeqId = (String) paramMap.remove("productPromoActionSeqId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("productPromoId" + thisSuffix)) {
	    	        	productPromoId = (String) paramMap.remove("productPromoId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("productPromoRuleId" + thisSuffix)) {
	    	        	productPromoRuleId = (String) paramMap.remove("productPromoRuleId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("quantityUomId" + thisSuffix)) {
	    	        	quantityUomId = (String) paramMap.remove("quantityUomId" + thisSuffix);
	    	        }
	    	        if (UtilValidate.isEmpty(quantityUomId)) {
	    	        	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	    	        	if (product != null) quantityUomId = product.getString("quantityUomId");
	    	        }
	    	        if (paramMap.containsKey("receivedQuantity" + thisSuffix)) {
	    	        	String receivedQuantityStr = (String) paramMap.remove("receivedQuantity" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(receivedQuantityStr)) {
    	                	// parse the quantity
    	                    try {
    	                    	receivedQuantity = (BigDecimal) ObjectType.simpleTypeConvert(receivedQuantityStr, "BigDecimal", null, locale);
    	                    } catch (Exception e) {
    	                        Debug.logWarning(e, "Problems parsing quantity string: " + receivedQuantityStr, module);
    	                        receivedQuantity = BigDecimal.ZERO;
    	                    }
    	                }
	                }
	    	        if (paramMap.containsKey("returnAdjustmentId" + thisSuffix)) {
	    	        	returnAdjustmentId = (String) paramMap.remove("returnAdjustmentId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnAdjustmentTypeId" + thisSuffix)) {
	    	        	returnAdjustmentTypeId = (String) paramMap.remove("returnAdjustmentTypeId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnItemResponseId" + thisSuffix)) {
	    	        	returnItemResponseId = (String) paramMap.remove("returnItemResponseId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnItemSeqId" + thisSuffix)) {
	    	        	returnItemSeqId = (String) paramMap.remove("returnItemSeqId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnItemTypeId" + thisSuffix)) {
	    	        	returnItemTypeId = (String) paramMap.remove("returnItemTypeId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnPrice" + thisSuffix)) {
	    	        	String returnPriceStr = (String) paramMap.remove("returnPrice" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(returnPriceStr)) {
    	                	// parse the quantity
    	                    try {
    	                    	returnPrice = (BigDecimal) ObjectType.simpleTypeConvert(returnPriceStr, "BigDecimal", null, locale);
    	                    } catch (Exception e) {
    	                        Debug.logWarning(e, "Problems parsing quantity string: " + returnPriceStr, module);
    	                        returnPrice = BigDecimal.ZERO;
    	                    }
    	                }
	                }
	    	        if (paramMap.containsKey("returnQuantity" + thisSuffix)) {
	    	        	String returnQuantityStr = (String) paramMap.remove("returnQuantity" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(returnQuantityStr)) {
	    	        		// parse the quantity
	    	        		try {
	    	        			returnQuantity = (BigDecimal) ObjectType.simpleTypeConvert(returnQuantityStr, "BigDecimal", null, locale);
	    	        		} catch (Exception e) {
	    	        			Debug.logWarning(e, "Problems parsing quantity string: " + returnQuantityStr, module);
	    	        			returnQuantity = BigDecimal.ZERO;
	    	        		}
	    	        	}
	    	        }
	    	        if (paramMap.containsKey("returnReasonId" + thisSuffix)) {
	    	        	returnReasonId = (String) paramMap.remove("returnReasonId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("returnTypeId" + thisSuffix)) {
	    	        	returnTypeId = (String) paramMap.remove("returnTypeId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("secondaryGeoId" + thisSuffix)) {
	    	        	secondaryGeoId = (String) paramMap.remove("secondaryGeoId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("shipGroupSeqId" + thisSuffix)) {
	    	        	shipGroupSeqId = (String) paramMap.remove("shipGroupSeqId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("sourcePercentage" + thisSuffix)) {
	    	        	String sourcePercentageStr = (String) paramMap.remove("sourcePercentage" + thisSuffix);
	    	        	if (UtilValidate.isNotEmpty(sourcePercentageStr)) {
	    	        		// parse the quantity
	    	        		try {
	    	        			sourcePercentage = (BigDecimal) ObjectType.simpleTypeConvert(sourcePercentageStr, "BigDecimal", null, locale);
	    	        		} catch (Exception e) {
	    	        			Debug.logWarning(e, "Problems parsing quantity string: " + sourcePercentageStr, module);
	    	        			sourcePercentage = BigDecimal.ZERO;
	    	        		}
	    	        	}
	    	        }
	    	        if (paramMap.containsKey("sourceReferenceId" + thisSuffix)) {
	    	        	sourceReferenceId = (String) paramMap.remove("sourceReferenceId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("statusId" + thisSuffix)) {
	    	        	statusId = (String) paramMap.remove("statusId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("taxAuthGeoId" + thisSuffix)) {
	    	        	taxAuthGeoId = (String) paramMap.remove("taxAuthGeoId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("taxAuthPartyId" + thisSuffix)) {
	    	        	taxAuthPartyId = (String) paramMap.remove("taxAuthPartyId" + thisSuffix);
	    	        }
	    	        if (paramMap.containsKey("taxAuthorityRateSeqId" + thisSuffix)) {
	    	        	taxAuthorityRateSeqId = (String) paramMap.remove("taxAuthorityRateSeqId" + thisSuffix);
	    	        }
	    	        
	    	        Map<String, Object> contextMapReturnItem = UtilMisc.<String, Object>toMap(
			    	        		"amount", amount, "lotId", lotId, "comments", comments, "correspondingProductId", correspondingProductId,
			    	        		"createdByUserLogin", createdByUserLogin, "createdDate", createdDate,
			    	        		"customerReferenceId", customerReferenceId, "datetimeManufactured", datetimeManufactured,
			    	        		"description", description, "exemptAmount", exemptAmount,
			    	        		"expectedItemStatus", expectedItemStatus, "expiredDate", expiredDate,
			    	        		"includeInShipping", includeInShipping, "includeInTax", includeInTax,
			    	        		"lastModifiedByUserLogin", lastModifiedByUserLogin, "lastModifiedDate", lastModifiedDate, 
			    	        		"locale", locale, "userLogin", userLogin,
			    	        		"orderAdjustmentId", orderAdjustmentId, "orderId", orderId,
			    	        		"orderItemSeqId", orderItemSeqId, "overrideGlAccountId", overrideGlAccountId,
			    	        		"primaryGeoId", primaryGeoId, "productFeatureId", productFeatureId,
			    	        		"productId", productId, "productPromoActionSeqId", productPromoActionSeqId,
			    	        		"productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId,
			    	        		"quantityUomId", quantityUomId, "receivedQuantity", receivedQuantity,
			    	        		"returnAdjustmentId", returnAdjustmentId, "returnAdjustmentTypeId", returnAdjustmentTypeId,
			    	        		"returnId", returnIdSuccess, "returnItemResponseId", returnItemResponseId,
			    	        		"returnItemSeqId", returnItemSeqId, "returnItemTypeId", returnItemTypeId,
			    	        		"returnPrice", returnPrice, "returnQuantity", returnQuantity,
			    	        		"returnReasonId", returnReasonId, "returnTypeId", returnTypeId,
			    	        		"secondaryGeoId", secondaryGeoId, "shipGroupSeqId", shipGroupSeqId,
			    	        		"sourcePercentage", sourcePercentage, "sourceReferenceId", sourceReferenceId,
			    	        		"statusId", statusId, "taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId, "taxAuthorityRateSeqId", taxAuthorityRateSeqId
	    	        		);
	    	        tobeStored.add(contextMapReturnItem);
	            }
	            
	            // finish prepare process data, create return header and return items
	            if (UtilValidate.isNotEmpty(tobeStored)) {
	            	if (UtilValidate.isNotEmpty(orderId)) {
	            		if (UtilValidate.isEmpty(returnId)) {
	            			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
	            			if (returnHeader == null) {
	            				// create return header
	            				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	            				List<GenericValue> orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
	                    		String fromPartyId = null;
	                    		String toPartyId = null;
	                    		String originFacilityId = null;
	                    		if (UtilValidate.isNotEmpty(orderRoles)) {
	                    			List<GenericValue> listFromPartyId = EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId", "PLACING_CUSTOMER"));
	                    			if (UtilValidate.isNotEmpty(listFromPartyId)) {
	                    				fromPartyId = listFromPartyId.get(0).getString("partyId");
	                    			}
	                    			List<GenericValue> listToPartyId = EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId", "BILL_FROM_VENDOR"));
	                    			if (UtilValidate.isNotEmpty(listToPartyId)) {
	                    				toPartyId = listToPartyId.get(0).getString("partyId");
	                    			}
	                    		}
	                    		if (orderHeader != null) {
	                    			originFacilityId = orderHeader.getString("originFacilityId");
	                    		}
	                    		if (toPartyId == null || fromPartyId == null) {
	                    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"From party and to party must not empty", locale));
	                    			return "error";
	                    		}
	                    		Map<String, Object> contextMap =  UtilMisc.<String, Object>toMap(
	                    						"returnHeaderTypeId", "CUSTOMER_RETURN", "statusId", "RETURN_REQUESTED",
	                    						"fromPartyId", fromPartyId, "toPartyId", toPartyId,
	                    						"paymentMethodId", null, "finAccountId", null,
	                    						"billingAccountId", null, "entryDate", nowTimestamp,
	                    						"originContactMechId", null, "destinationFacilityId", originFacilityId,
	                    						"needsInventoryReceive", "Y", "currencyUomId", SalesUtil.getCurrentCurrencyUom(delegator),
	                    						"supplierRmaId", null, "userLogin", userLogin, "locale", locale
	                    				);
	                        	Map<String, Object> result0 = dispatcher.runSync("createReturnHeader", contextMap);
	                        	
	                        	// no values for price and paramMap (a context for adding attributes)
	                            controlDirective = SalesUtil.processResult(result0, request);
	                            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                            	try {
	                                    TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
	                                } catch (Exception e1) {
	                                    Debug.logError(e1, module);
	                                }
	                                return "error";
	                            }
	                            
	                            returnIdSuccess = (String) result0.get("returnId");
	            			}
	            		}
	            	}
	            
	            	String returnAdjustmentId = null;
	            	String returnItemSeqId = null;
	            	for (Map<String, Object> contextMapReturnItem : tobeStored) {
	            		contextMapReturnItem.put("returnId", returnIdSuccess);
	            		contextMapReturnItem.put("returnAdjustmentId", returnAdjustmentId);
	            		contextMapReturnItem.put("returnItemSeqId", returnItemSeqId);
	            		Map<String, Object> result0 = dispatcher.runSync("createReturnItemOrAdjustment", contextMapReturnItem);
	            		// no values for price and paramMap (a context for adding attributes)
	                    controlDirective = SalesUtil.processResult(result0, request);
	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                    	try {
	                            TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
	                        } catch (Exception e1) {
	                            Debug.logError(e1, module);
	                        }
	                        return "error";
	                    }
	                    returnAdjustmentId = (String) result0.get("returnAdjustmentId");
	                    returnItemSeqId = (String) result0.get("returnItemSeqId");
	            	}
	            } else {
	            	try {
	                    TransactionUtil.rollback(beganTx, "No item to process", null);
	                } catch (Exception e2) {
	                    Debug.logError(e2, module);
	                }
	            	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
	            	return "error";
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
        	/*
        	try {
				NotificationWorker.sendNotifyWhenCreateReturnOrder(delegator, dispatcher, locale, orderId, returnIdSuccess, userLogin);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logWarning(e, errMsg, module);
			} catch (GenericServiceException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logWarning(e, errMsg, module);
			}
			*/
        }
        request.setAttribute("returnId", returnIdSuccess);
        return "success";
    }
}
