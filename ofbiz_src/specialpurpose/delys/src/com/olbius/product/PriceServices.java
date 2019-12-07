package com.olbius.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;

public class PriceServices {
	public static final String module = PriceServices.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";
    public static final String resourceProduct = "ProductUiLabels";
    
	/** Service for changing the status on an order header */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> setQuotationStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productQuotationId = (String) context.get("productQuotationId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        /*Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }*/
        GenericValue productQuotation = null;
        try {
        	productQuotation = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);

        	if (productQuotation == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationErrorCouldNotChangeQuotationStatusQuotationCannotBeFound", locale));
			}

            // first save off the old status
        	successResult.put("oldStatusId", productQuotation.get("statusId"));

        	if (productQuotation.getString("statusId").equals(statusId)) {
        		return successResult;
        	}
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", productQuotation.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "DAQuotationErrorCouldNotChangeQuotationStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "DAQuotationErrorCouldNotChangeQuotationStatus",locale) + e.getMessage() + ").");
            }

            // update the current status
            productQuotation.set("statusId", statusId);

            // now create a status change
            GenericValue quotationStatus = delegator.makeValue("ProductQuotationStatus");
            quotationStatus.put("quotationStatusId", delegator.getNextSeqId("ProductQuotationStatus"));
            quotationStatus.put("statusId", statusId);
            quotationStatus.put("productQuotationId", productQuotationId);
            quotationStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            quotationStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            quotationStatus.put("changeReason", changeReason);

            productQuotation.store();
            quotationStatus.create();
            
            if (statusId != null && statusId.equals("QUOTATION_ACCEPTED")) {
            	List<GenericValue> listPriceRule = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
                if (listPriceRule != null && listPriceRule.size() > 0) {
                	for (GenericValue priceRuleItem : listPriceRule) {
    					if (productQuotation.get("fromDate") != null) {
    						priceRuleItem.set("fromDate", productQuotation.get("fromDate"));
    					}
    					if (productQuotation.get("thruDate") != null) {
    						priceRuleItem.set("thruDate", productQuotation.get("thruDate"));
    					}
    					priceRuleItem.store();
    				}
                }
			}
            
            //Debug.logInfo("For setOrderStatus orderHeader is " + orderHeader, module);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "DAQuotationErrorCouldNotChangeQuotationStatus",locale) + e.getMessage() + ").");
        }
        String ntfId = (String) context.get("ntfId");
        successResult.put("ntfId", ntfId);
        
    	String header = "";
    	String state = "open";
    	String action = "";
    	String targetLink = "";
    	Timestamp dateTime = UtilDateTime.nowTimestamp();
    	try {
    		if ("QUOTT_NBD_ACCEPTED".equals(statusId)) {
    			// Send message to CEO
    			String notiToId = "";
    	    	try {
    	    		notiToId = SalesPartyUtil.getCeoPersonId(delegator);
    	    		header = UtilProperties.getMessage(resource, "DAApproveProductQuotation",locale) + " [" + productQuotationId +"]";
    	    		action = "viewQuotation";
    	    		targetLink = "productQuotationId="+productQuotationId;
    	    	} catch (Exception e) {
    	    		Debug.logError(e, "Error when set value for notify", module);
    	    	}
    	    	Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partyId", notiToId, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", dateTime, "userLogin", userLogin));
        		if (ServiceUtil.isError(tmpResult)) {
        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
                }
    		} else if ("QUOTATION_ACCEPTED".equals(statusId)) {
    			// Inform to many people has relationship with quotation
    			header = "[" + UtilProperties.getMessage(resource, "DAInform",locale) + "] " + UtilProperties.getMessage(resource, "DAHaveNewProductQuotation",locale) + " [" + productQuotationId +"]";
        		action = "viewQuotation";
        		targetLink = "productQuotationId="+productQuotationId;
        		String sendToGroup = "Y";
        		List<String> listPartyIdFinal = new ArrayList<String>();
        		List<String> listRoleNeedSend = new ArrayList<String>();
        		boolean isSend = false;
        		if (productQuotation != null && "SALES_GT_CHANNEL".equals(productQuotation.getString("salesChannel"))) {
        			//Send to DELYS_CSM_GT, DELYS_RSM_GT, DELYS_ASM_GT, DELYS_SALESSUP_GT, DELYS_SALESMAN_GT, DELYS_CUSTOMER_GT, DELYS_NBD, SALESADMIN_MANAGER
        			listRoleNeedSend.add("DELYS_NBD");
        			listRoleNeedSend.add("SALESADMIN_MANAGER");
        			listRoleNeedSend.add("DELYS_SALESADMIN_GT");
        			listRoleNeedSend.add("DELYS_CSM_GT");
        			listRoleNeedSend.add("DELYS_RSM_GT");
        			listRoleNeedSend.add("DELYS_SALESSUP_GT");
        			listRoleNeedSend.add("DELYS_SALESMAN_GT");
        			listRoleNeedSend.add("DELYS_ACCOUNTANTS");
        			isSend = true;
        		} else if (productQuotation != null && "SALES_MT_CHANNEL".equals(productQuotation.getString("salesChannel"))) {
        			//Send to DELYS_CSM_MT, DELYS_RSM_MT, DELYS_ASM_MT, DELYS_SALESSUP_MT, DELYS_SALESMAN_MT, DELYS_CUSTOMER_MT, SALESADMIN_MANAGER
        			listRoleNeedSend.add("DELYS_NBD");
        			listRoleNeedSend.add("SALESADMIN_MANAGER");
        			listRoleNeedSend.add("DELYS_SALESADMIN_MT");
        			listRoleNeedSend.add("DELYS_CSM_MT");
        			listRoleNeedSend.add("DELYS_RSM_MT");
        			listRoleNeedSend.add("DELYS_SALESSUP_MT");
        			listRoleNeedSend.add("DELYS_SALESMAN_MT");
        			listRoleNeedSend.add("DELYS_ACCOUNTANTS");
        			isSend = true;
        		}
        		if (isSend) {
        			List<String> listPartyIdInternal = new ArrayList<String>();
        			String deptIdSales = EntityUtilProperties.getPropertyValue("delys.properties", "department.id.sales", delegator);
        			Map<String, Object> resultGetParties = dispatcher.runSync("getPartiesByRootAndRoles", UtilMisc.<String, Object>toMap("partyId", deptIdSales, "roleTypeIds", listRoleNeedSend, "userLogin", userLogin));
        			if (ServiceUtil.isSuccess(resultGetParties)) {
        				listPartyIdInternal.addAll((List<String>) resultGetParties.get("parties"));
        			}
        			listPartyIdFinal.addAll(listPartyIdInternal);
        			List<GenericValue> listRoleTypeInQuotation = delegator.findByAnd("ProductQuotationRoleType", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
        			if (listRoleTypeInQuotation != null) {
        				List<String> roleTypesApply = EntityUtil.getFieldListFromEntityList(listRoleTypeInQuotation, "roleTypeId", true);
        				for (String roleTypeId : roleTypesApply) {
        					listPartyIdFinal.addAll(SalesPartyUtil.getPartiesByRole(delegator, dispatcher, roleTypeId, true));
        				}
        			}
            		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
            				UtilMisc.<String, Object>toMap("partiesList", listPartyIdFinal, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", dateTime, "ntfType", "ONE", "sendToGroup", sendToGroup, "sendrecursive", "N", "userLogin", userLogin));
            		if (ServiceUtil.isError(tmpResult)) {
            			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
                    }
        		}
    		} else if ("QUOTATION_CANCELLED".equals(statusId)) {
    			// send to user created
    			String createdByUserLogin = productQuotation.getString("createdByUserLogin");
	    		GenericValue partyCreated = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", createdByUserLogin), false);
	    		if (UtilValidate.isNotEmpty(partyCreated.getString("partyId"))) {
	    			String notiToId = "";
	    			String ntfType = "ONE";
	    	    	try {
		    			notiToId = partyCreated.getString("partyId");
	    	    		header = UtilProperties.getMessage(resource, "DAProductQuotationWasCancelled",locale) + " [" + productQuotationId +"]";
	    	    		action = "viewQuotation";
	    	    		targetLink = "productQuotationId="+productQuotationId;
	    	    	} catch (Exception e) {
	    	    		Debug.logError(e, "Error when set value for notify", module);
	    	    	}
	    	    	Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partyId", notiToId, "header", header, 
	    	    			"state", state, "action", action, "targetLink", targetLink, "dateTime", dateTime, "ntfType", ntfType, "userLogin", userLogin));
	        		if (ServiceUtil.isError(tmpResult)) {
	        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
	                }
	    		}
    		}
    	} catch (Exception e) {
    		Debug.logError(e, "Error when create notify", module);
    	}
    	
        successResult.put("productQuotationId", productQuotationId);
        successResult.put("quotationStatusId", statusId);
        //Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
        return successResult;
    }
    
    @SuppressWarnings("unused")
	private static Map<String, Object> createQuotationRule(Delegator delegator, String productQuotationId, String description, 
    		String quotationName, String productId, String strPriceToDist, String strPriceToMarket, String strPriceToConsumer) {
    	try {
	    	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    		List<GenericValue> partyRoleTypesApplysFind = delegator.findByAnd("ProductQuotationRoleType", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
			if (partyRoleTypesApplysFind == null) {
				// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
				String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
				GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
						"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
				delegator.create(newEntityProductPriceRule);
				
				/* Create product price cond: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
					cond1: product 
					cond2: quantityUomId - PRIP_QUANTITY_UOMID */
				GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
				newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
	            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
	            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
	            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
	            newEntityProductPriceCond.put("condValue", productId);
	            delegator.create(newEntityProductPriceCond);
	            
	            if (UtilValidate.isNotEmpty(product.getString("productPackingUomId"))) {
	            	GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
		            newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
		            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond2.put("condValue", product.getString("productPackingUomId"));
		            delegator.create(newEntityProductPriceCond2);
	            }
	            
	            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
	            if (UtilValidate.isNotEmpty(strPriceToDist)) {
	            	// priceToDist
            		BigDecimal priceToDist = new BigDecimal(strPriceToDist);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
					newEntityProductPriceAction.put("amount", priceToDist);
		            delegator.create(newEntityProductPriceAction);
	            }
	            if (UtilValidate.isNotEmpty(strPriceToMarket)) {
	            	// priceToMarket
            		BigDecimal priceToMarket = new BigDecimal(strPriceToMarket);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_MARKET");
					newEntityProductPriceAction.put("amount", priceToMarket);
		            delegator.create(newEntityProductPriceAction);
	            }
	            if (UtilValidate.isNotEmpty(strPriceToConsumer)) {
	            	// priceToConsumer
            		BigDecimal priceToConsumer = new BigDecimal(strPriceToConsumer);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_CONSUMER");
					newEntityProductPriceAction.put("amount", priceToConsumer);
		            delegator.create(newEntityProductPriceAction);
	            }
			} else {
				for (GenericValue partyRoleTypesApplyItem : partyRoleTypesApplysFind) {
					// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
					String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
					GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.<String, Object>toMap("productPriceRuleId", productPriceRuleId, 
							"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
					delegator.create(newEntityProductPriceRule);
					
					/* Create product price condition: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
						cond1: product
						cond2: roleType of party
						cond3: quantityUomId - PRIP_QUANTITY_UOMID */
					GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
					newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
		            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond.put("condValue", productId);
		            delegator.create(newEntityProductPriceCond);
		            
					GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
					newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
					delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_ROLE_TYPE");
		            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond2.put("condValue", partyRoleTypesApplyItem.get("roleTypeId"));
		            delegator.create(newEntityProductPriceCond2);
		            
		            if (UtilValidate.isNotEmpty(product.getString("productPackingUomId"))) {
		            	GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
			            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
			            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
			            newEntityProductPriceCond3.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
			            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
			            newEntityProductPriceCond3.put("condValue", product.getString("productPackingUomId"));
			            delegator.create(newEntityProductPriceCond3);
		            }
		            
		            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
		            if (UtilValidate.isNotEmpty(strPriceToDist)) {
		            	// priceToDist
	            		BigDecimal priceToDist = new BigDecimal(strPriceToDist);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
						newEntityProductPriceAction.put("amount", priceToDist);
			            delegator.create(newEntityProductPriceAction);
		            }
		            if (UtilValidate.isNotEmpty(strPriceToMarket)) {
		            	// priceToMarket
	            		BigDecimal priceToMarket = new BigDecimal(strPriceToMarket);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_MARKET");
						newEntityProductPriceAction.put("amount", priceToMarket);
			            delegator.create(newEntityProductPriceAction);
		            }
		            if (UtilValidate.isNotEmpty(strPriceToConsumer)) {
		            	// priceToConsumer
	            		BigDecimal priceToConsumer = new BigDecimal(strPriceToConsumer);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_CONSUMER");
						newEntityProductPriceAction.put("amount", priceToConsumer);
			            delegator.create(newEntityProductPriceAction);
		            }
				}
			}
    	} catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
		return null;
    }
    
    private static Map<String, Object> createQuotationRule(Delegator delegator, String productQuotationId, String description, 
    		String quotationName, String productId, String strPriceToDist, String strPriceToMarket, String strPriceToConsumer, List<String> partyIdsApply) {
    	try {
	    	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    		List<GenericValue> partyRoleTypesApplysFind = delegator.findByAnd("ProductQuotationRoleType", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
			if (partyRoleTypesApplysFind == null) {
				// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
				String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
				GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
						"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
				delegator.create(newEntityProductPriceRule);
				
				/* Create product price cond: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
					cond1: product 
					cond2: quantityUomId - PRIP_QUANTITY_UOMID 
					cond3s: partyId list*/
				GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
				newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
	            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
	            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
	            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
	            newEntityProductPriceCond.put("condValue", productId);
	            delegator.create(newEntityProductPriceCond);
	            
	            if (UtilValidate.isNotEmpty(product.getString("productPackingUomId"))) {
	            	GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
		            newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
		            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond2.put("condValue", product.getString("productPackingUomId"));
		            delegator.create(newEntityProductPriceCond2);
	            }
	            
	            if (UtilValidate.isNotEmpty(partyIdsApply)) {
	            	for (String partyId : partyIdsApply) {
	            		GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
			            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
			            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
			            newEntityProductPriceCond3.put("inputParamEnumId", "PRIP_PARTY_ID");
			            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
			            newEntityProductPriceCond3.put("condValue", partyId);
			            delegator.create(newEntityProductPriceCond3);
	            	}
	            }
	            
	            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
	            if (UtilValidate.isNotEmpty(strPriceToDist)) {
	            	// priceToDist
            		BigDecimal priceToDist = new BigDecimal(strPriceToDist);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
					newEntityProductPriceAction.put("amount", priceToDist);
		            delegator.create(newEntityProductPriceAction);
	            }
	            if (UtilValidate.isNotEmpty(strPriceToMarket)) {
	            	// priceToMarket
            		BigDecimal priceToMarket = new BigDecimal(strPriceToMarket);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_MARKET");
					newEntityProductPriceAction.put("amount", priceToMarket);
		            delegator.create(newEntityProductPriceAction);
	            }
	            if (UtilValidate.isNotEmpty(strPriceToConsumer)) {
	            	// priceToConsumer
            		BigDecimal priceToConsumer = new BigDecimal(strPriceToConsumer);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_CONSUMER");
					newEntityProductPriceAction.put("amount", priceToConsumer);
		            delegator.create(newEntityProductPriceAction);
	            }
			} else {
				for (GenericValue partyRoleTypesApplyItem : partyRoleTypesApplysFind) {
					// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
					String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
					GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.<String, Object>toMap("productPriceRuleId", productPriceRuleId, 
							"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
					delegator.create(newEntityProductPriceRule);
					
					/* Create product price condition: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
						cond1: product
						cond2: roleType of party
						cond3: quantityUomId - PRIP_QUANTITY_UOMID 
						cond4s: partyId list*/
					GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
					newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
		            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond.put("condValue", productId);
		            delegator.create(newEntityProductPriceCond);
		            
					GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
					newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
					delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_ROLE_TYPE");
		            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond2.put("condValue", partyRoleTypesApplyItem.get("roleTypeId"));
		            delegator.create(newEntityProductPriceCond2);
		            
		            if (UtilValidate.isNotEmpty(product.getString("productPackingUomId"))) {
		            	GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
			            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
			            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
			            newEntityProductPriceCond3.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
			            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
			            newEntityProductPriceCond3.put("condValue", product.getString("productPackingUomId"));
			            delegator.create(newEntityProductPriceCond3);
		            }
		            
		            if (UtilValidate.isNotEmpty(partyIdsApply)) {
		            	for (String partyId : partyIdsApply) {
		            		GenericValue newEntityProductPriceCond4 = delegator.makeValue("ProductPriceCond");
				            newEntityProductPriceCond4.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
				            delegator.setNextSubSeqId(newEntityProductPriceCond4, "productPriceCondSeqId", 2, 1);
				            newEntityProductPriceCond4.put("inputParamEnumId", "PRIP_PARTY_ID");
				            newEntityProductPriceCond4.put("operatorEnumId", "PRC_EQ");
				            newEntityProductPriceCond4.put("condValue", partyId);
				            delegator.create(newEntityProductPriceCond4);
		            	}
		            }
		            
		            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
		            if (UtilValidate.isNotEmpty(strPriceToDist)) {
		            	// priceToDist
	            		BigDecimal priceToDist = new BigDecimal(strPriceToDist);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
						newEntityProductPriceAction.put("amount", priceToDist);
			            delegator.create(newEntityProductPriceAction);
		            }
		            if (UtilValidate.isNotEmpty(strPriceToMarket)) {
		            	// priceToMarket
	            		BigDecimal priceToMarket = new BigDecimal(strPriceToMarket);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_MARKET");
						newEntityProductPriceAction.put("amount", priceToMarket);
			            delegator.create(newEntityProductPriceAction);
		            }
		            if (UtilValidate.isNotEmpty(strPriceToConsumer)) {
		            	// priceToConsumer
	            		BigDecimal priceToConsumer = new BigDecimal(strPriceToConsumer);
						GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
						newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
						delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
						newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_CONSUMER");
						newEntityProductPriceAction.put("amount", priceToConsumer);
			            delegator.create(newEntityProductPriceAction);
		            }
				}
			}
    	} catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
		return null;
    }
    
    private static Map<String, Object> createQuotationRule(Delegator delegator, String productQuotationId, String description, 
    		String quotationName, String productId, String strPrice, String roleTypeId, boolean isExtra, String strTaxPercentage) {
    	try {
	    	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
				String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
				String isExtraVar = isExtra == true ? "Y" : "N";
				GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.<String, Object>toMap("productPriceRuleId", productPriceRuleId, 
						"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N", "isExtra", isExtraVar));
				delegator.create(newEntityProductPriceRule);
				
				/* Create product price condition: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
					cond1: product
					cond2: roleType of party
					cond3: quantityUomId - PRIP_QUANTITY_UOMID 
					cond4s: partyId list*/
				GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
				newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
	            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
	            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
	            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
	            newEntityProductPriceCond.put("condValue", productId);
	            delegator.create(newEntityProductPriceCond);
	            
				GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
				newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
				delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
	            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_ROLE_TYPE");
	            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
	            newEntityProductPriceCond2.put("condValue", roleTypeId);
	            delegator.create(newEntityProductPriceCond2);
	            
	            if (UtilValidate.isNotEmpty(product.getString("productPackingUomId"))) {
	            	GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
		            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond3.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
		            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond3.put("condValue", product.getString("productPackingUomId"));
		            delegator.create(newEntityProductPriceCond3);
	            }
	            
	            /*if (UtilValidate.isNotEmpty(partyIdsApply)) {
	            	for (String partyId : partyIdsApply) {
	            		GenericValue newEntityProductPriceCond4 = delegator.makeValue("ProductPriceCond");
			            newEntityProductPriceCond4.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
			            delegator.setNextSubSeqId(newEntityProductPriceCond4, "productPriceCondSeqId", 2, 1);
			            newEntityProductPriceCond4.put("inputParamEnumId", "PRIP_PARTY_ID");
			            newEntityProductPriceCond4.put("operatorEnumId", "PRC_EQ");
			            newEntityProductPriceCond4.put("condValue", partyId);
			            delegator.create(newEntityProductPriceCond4);
	            	}
	            }*/
	            
	            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
	            BigDecimal hundred = new BigDecimal(100);
	            BigDecimal taxPercentage = null;
	            if (UtilValidate.isNotEmpty(strTaxPercentage)) {
	            	taxPercentage = new BigDecimal(strTaxPercentage);
	            } else {
	            	taxPercentage = BigDecimal.ZERO;
	            }
	            if (UtilValidate.isNotEmpty(strPrice) && taxPercentage != null) {
	            	// price
            		BigDecimal priceToDist = new BigDecimal(strPrice);
            		BigDecimal priceAction = (priceToDist.divide(taxPercentage.add(hundred), 2, RoundingMode.HALF_UP)).multiply(hundred);
					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
					newEntityProductPriceAction.put("amount", priceAction);
		            delegator.create(newEntityProductPriceAction);
	            }
			}
    	} catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
		return null;
    }
    
    /*Create an Product Quotation*/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createProductQuotation(DispatchContext ctx, Map<String, ? extends Object> context) {
    	/*LocalDispatcher dispatcher = ctx.getDispatcher();*/
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        Security security = ctx.getSecurity();
        boolean hasPermission = security.hasEntityPermission("DELYS_QUOTATION", "_CREATE", userLogin);;
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationCreatePermissionError",locale));
        }
        
        String productQuotationId = (String) context.get("productQuotationId");
        String quotationName = (String) context.get("quotationName");
        String description = (String) context.get("description");
        String salesChannel = (String) context.get("salesChannel");
        String currencyUomId = (String) context.get("currencyUomId");
        List<String> partyRoleTypesApplys = (List<String>) context.get("partyRoleTypesApply");
        String productListStr = (String) context.get("productListStr");
        String fromDateStr = (String) context.get("fromDate");
        String thruDateStr = (String) context.get("thruDate");
        List<String> partyIdsApply = (List<String>) context.get("partyIdsApply");
        String partyRoleTypesApplyMarket = (String) context.get("partyRoleTypesApplyMarket");
        
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
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        }
        
        List<String> errorMessageList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(productQuotationId)) {
        	try {
	        	GenericValue productQuotationIdFind = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
	        	if (productQuotationIdFind != null) {
	        		errorMessageList.add(UtilProperties.getMessage(resource, "DATheQuotationIsAlreadyExists", locale));
	        	}
        	} catch (Exception e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        if (UtilValidate.isEmpty(quotationName)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DAQuotaionNameMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(salesChannel)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DASalesChannelMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(currencyUomId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DACurrencyUomIdMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(partyRoleTypesApplys) && UtilValidate.isEmpty(partyIdsApply)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DAPartyApplyMustNotBeEmpty", locale));
        }
        if (UtilValidate.isNotEmpty(partyRoleTypesApplyMarket)) {
        	try {
	        	GenericValue partyRoleTypesApplyMarketGEN = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", partyRoleTypesApplyMarket), false);
	        	if (partyRoleTypesApplyMarketGEN == null) {
	        		errorMessageList.add(UtilProperties.getMessage(resource, "DATheRoleTypeOfXIsNotExisted", UtilMisc.toMap("fieldName", "partyRoleTypesApplyMarket"), locale));
	        	}
        	} catch (Exception e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        
        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        // begin create quotation
        List<String> alertMessageList = FastList.newInstance();
        if (UtilValidate.isEmpty(productQuotationId)) {
        	productQuotationId = delegator.getNextSeqId("ProductQuotation");
        }
        
        GenericValue productQuotation = delegator.makeValue("ProductQuotation");
        productQuotation.setNonPKFields(context);
        productQuotation.set("createDate", UtilDateTime.nowTimestamp());
        productQuotation.set("createdByUserLogin", userLogin.get("userLoginId"));
        productQuotation.set("fromDate", fromDate);
        productQuotation.set("thruDate", thruDate);
        productQuotation.set("description", description);
        productQuotation.set("salesChannel", salesChannel);
        productQuotation.set("statusId", "QUOTATION_CREATED");
        productQuotation.set("productQuotationId", productQuotationId);
        // first try to create the ProductQuotation; if this does not fail, continue.
        try {
            delegator.create(productQuotation);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create ProductQuotation entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationCreationFailedPleaseNotifyCustomerService",locale));
        }
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        GenericValue productQuotationStatus = delegator.makeValue("ProductQuotationStatus");
        String quotationStatusId = delegator.getNextSeqId("ProductQuotationStatus");
        productQuotationStatus.put("quotationStatusId", quotationStatusId);
        productQuotationStatus.put("statusId", "QUOTATION_CREATED");
        productQuotationStatus.put("productQuotationId", productQuotationId);
        productQuotationStatus.put("statusDatetime", nowTimestamp);
        productQuotationStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
        // first try to create the ProductQuotation; if this does not fail, continue.
        try {
            delegator.create(productQuotationStatus);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create ProductQuotationStatus entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationStatusCreationFailedPleaseNotifyCustomerService",locale));
        }
        
        try {
	        for (String partyRoleTypesApply : partyRoleTypesApplys) {
	        	dispatcher.runSync("createProductQuotationRoleType", UtilMisc.<String, Object>toMap("productQuotationId", productQuotationId, "roleTypeId", partyRoleTypesApply, "userLogin", userLogin));
	        }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        
        /* Create quotation rules - createQuotationRule (services)*/
        String[] strDataLineArr = productListStr.split("\\|OLBIUS\\|"); //item (productId - priceToDist - priceToMarket - priceToConsumer - priceToDistAfterVAT - taxPercentage)
    	if (("N".equals(strDataLineArr[0]) && strDataLineArr.length > 1)) {
    		for (int i = 1; i < strDataLineArr.length; i++) {
    			String[] lineValues = strDataLineArr[i].split("\\|SUIBLO\\|");
    			String productId = lineValues.length > 0 ? lineValues[0] : "";
    			String strPriceToDist = lineValues.length > 1 ? lineValues[1] : "";
    			String strPriceToMarket = lineValues.length > 2 ? lineValues[2] : "";
    			String strPriceToConsumer = lineValues.length > 3 ? lineValues[3] : "";
    			String strTaxPercentage = lineValues.length > 5 ? lineValues[5] : "";
				// check product exists or not
    			try {
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					if (product == null || UtilValidate.isEmpty(strPriceToDist)) {
						if (product == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "DAProductNotExists",locale));
						}
						if (UtilValidate.isEmpty(strPriceToDist)) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "DAPriceToDistOfProductXIsEmptyDontCreate", UtilMisc.<String, Object> toMap("productId", productId), locale));
		    			}
		        	} else {
    					// get list product pricing rules of quotation
    					List<GenericValue> priceRulesOfProduct = delegator.findByAnd("ProductQuotationAndPriceRCA", 
    							UtilMisc.<String, Object>toMap("pq_ProductQuotationId", productQuotationId, "productId", productId, 
    									"inputParamEnumId", "PRIP_PRODUCT_ID", "productPriceActionTypeId", "PRICE_FLAT"), null, false);
    					if (priceRulesOfProduct != null && priceRulesOfProduct.size() > 0) {
    						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAThisProductIsAlreadyExistsInQuotation",locale));
    					}
    					
    					/* parameters: productQuotationId productId inputCategory priceToDist priceToMarket priceToConsumer
    					 * productQuotationSelected: productQuotationId *, quotationName, description, currencyUomId, fromDate Timestamp */
    					Map<String, Object> resultValue = createQuotationRule(delegator, productQuotationId, description, quotationName, productId, strPriceToDist, strPriceToMarket, strPriceToConsumer, partyIdsApply);
    					if (resultValue != null) {
    						return resultValue;
    					}
    					if (UtilValidate.isNotEmpty(partyRoleTypesApplyMarket) && UtilValidate.isNotEmpty(strPriceToMarket)) {
    						Map<String, Object> resultValue2 = createQuotationRule(delegator, productQuotationId, description, quotationName, productId, strPriceToMarket, partyRoleTypesApplyMarket, true, strTaxPercentage);
    						if (resultValue2 != null) {
        						return resultValue2;
        					}
    					}
		        	}
    			} catch (Exception e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
    		}
    	}
    	
    	List<String> partiesList = new ArrayList<String>();
    	String header = "";
    	String state = "open";
    	String action = "";
    	String targetLink = "";
    	try {
    		partiesList = SalesPartyUtil.getListNbdPersonId(delegator);
    		header = UtilProperties.getMessage(resource, "DAApproveProductQuotation",locale) + " [" + productQuotationId +"]";
    		action = "viewQuotation";
    		targetLink = "productQuotationId="+productQuotationId;
    	} catch (Exception e) {
    		Debug.logError(e, "Error when set value for notify", module);
    	}
    	try {
    		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", nowTimestamp, "userLogin", userLogin));
    		if (ServiceUtil.isError(tmpResult)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
            }
    	} catch (Exception e) {
			Debug.logError(e, "Error when create notify", module);
		}
    	alertMessageList.add(UtilProperties.getMessage(resource, "DACreateSuccessful", locale));
    	
    	if (alertMessageList.size() > 0) {
			successResult.put(ModelService.SUCCESS_MESSAGE_LIST, alertMessageList);
		}
        successResult.put("productQuotationId", productQuotationId);
        return successResult;
    }
    
    public static Map<String, Object> updateProductQuotation(DispatchContext ctx, Map<String, Object> context) {
    	/*LocalDispatcher dispatcher = ctx.getDispatcher();*/
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        Security security = ctx.getSecurity();
        boolean hasPermission = security.hasEntityPermission("DELYS", "_QUOTATION_UPDATE", userLogin);;
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationUpdatePermissionError",locale));
        }
        
        String productQuotationId = (String) context.get("productQuotationId");
        String quotationName = (String) context.get("quotationName");
        String description = (String) context.get("description");
        //String salesChannel = (String) context.get("salesChannel");
        //String currencyUomId = (String) context.get("currencyUomId");
        //List<String> partyRoleTypesApplys = (List<String>) context.get("partyRoleTypesApply");
        String productListStr = (String) context.get("productListStr");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        
        GenericValue quotationSelected = null;
        List<String> errorMessageList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(productQuotationId)) {
        	try {
	        	quotationSelected = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
	        	if (quotationSelected == null) {
	        		errorMessageList.add(UtilProperties.getMessage(resource_error, "DAProductQuotationNotFound", locale));
	        	}
        	} catch (Exception e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        if (UtilValidate.isEmpty(quotationName)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DAQuotaionNameMustNotBeEmpty", locale));
        }
        /*if (UtilValidate.isEmpty(salesChannel)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DASalesChannelMustNotBeEmpty", locale));
        }*/
        /*if (UtilValidate.isEmpty(currencyUomId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DACurrencyUomIdMustNotBeEmpty", locale));
        }*/
        /*if (UtilValidate.isEmpty(partyRoleTypesApplys)) {
        	errorMessageList.add(UtilProperties.getMessage(resource, "DAPartyApplyMustNotBeEmpty", locale));
        }*/
        
        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        List<String> partyIdsApply = new ArrayList<String>();
        String partyRoleTypesApplyMarket = "";
        
        try {
	        // get roleTypeId apply price to market
	 		List<EntityCondition> listRuleRoleTypeMarket = new ArrayList<EntityCondition>();
	 		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
	 		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "Y"));
	 		List<GenericValue> listProductPriceRuleMarket = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket, EntityOperator.AND), null, null, null, false);
	 		if (listProductPriceRuleMarket != null && listProductPriceRuleMarket.size() > 0) {
	 			GenericValue productPriceRuleIdMarket = EntityUtil.getFirst(listProductPriceRuleMarket);
	 			
	 			List<EntityCondition> listCondRoleTypeMarket2 = new ArrayList<EntityCondition>();
	 			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket.get("productPriceRuleId")));
	 			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_ROLE_TYPE"));
	 			List<GenericValue> listProductPriceCondMarket = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket2, EntityOperator.AND), null, null, null, false);
	 			if (UtilValidate.isNotEmpty(listProductPriceCondMarket)) {
	 				List<String> roleTypeMarketes = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket, "condValue", true);
	 				if (UtilValidate.isNotEmpty(roleTypeMarketes)) {
	 					partyRoleTypesApplyMarket = roleTypeMarketes.get(0);
	 				}
	 			}
	 		}
	 		
	 		// get partyId condition
	 		List<EntityCondition> listRuleRoleTypeMarket3 = new ArrayList<EntityCondition>();
	 		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
	 		List<EntityCondition> listRuleRoleTypeMarketOr3 = new ArrayList<EntityCondition>();
	 		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "N"));
	 		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, null));
	 		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition(listRuleRoleTypeMarketOr3, EntityOperator.OR));
	 		List<GenericValue> listProductPriceRuleMarket3 = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
	 		if (listProductPriceRuleMarket3 != null && listProductPriceRuleMarket3.size() > 0) {
	 			GenericValue productPriceRuleIdMarket3 = EntityUtil.getFirst(listProductPriceRuleMarket3);
	 			
	 			List<EntityCondition> listCondRoleTypeMarket3 = new ArrayList<EntityCondition>();
	 			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket3.get("productPriceRuleId")));
	 			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_ID"));
	 			List<GenericValue> listProductPriceCondMarket3 = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
	 			if (listProductPriceCondMarket3 != null && listProductPriceCondMarket3.size() > 0) {
	 				List<String> listPartyIdApply = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket3, "condValue", true);
	 				if (UtilValidate.isNotEmpty(listPartyIdApply)) {
	 					partyIdsApply.addAll(listPartyIdApply);
	 				}
	 			}
	 		}
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update ProductQuotation entity; problems with get data", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationUpdateFailedPleaseNotifyCustomerService",locale));
        }
        
        // begin update
        List<String> alertMessageList = FastList.newInstance();
        quotationSelected.set("quotationName", quotationName);
        quotationSelected.set("description", description);
        quotationSelected.set("fromDate", fromDate);
        quotationSelected.set("thruDate", thruDate);
        //quotationSelected.set("currencyUomId", currencyUomId);
        
        // first try to update the ProductQuotation; if this does not fail, continue.
        try {
            delegator.store(quotationSelected);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update ProductQuotation entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationUpdateFailedPleaseNotifyCustomerService",locale));
        }
        
        /* Update quotation rules - updateQuotationRule (services)*/
        String[] strDataLineArr = productListStr.split("\\|OLBIUS\\|"); //item (productId - priceToDist - priceToMarket - priceToConsumer - priceToDistAfterVAT - taxPercentage
				// productPriceRuleId - ppa_productPriceActionSeqId - ppam_productPriceActionSeqId - ppac_productPriceActionSeqId - .... - ....)
    	if (("N".equals(strDataLineArr[0]) && strDataLineArr.length > 1)) {
    		List<String> listQuotationRuleIdUpdate = new ArrayList<String>();
    		List<String> listQuotationRuleIdAll = null;
    		try {
    			listQuotationRuleIdAll = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productQuotationId", productQuotationId), null, false), "productPriceRuleId", true);
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
            }
    		for (int i = 1; i < strDataLineArr.length; i++) {
    			String[] lineValues = strDataLineArr[i].split("\\|SUIBLO\\|");
    			String productId = lineValues.length > 0 ? lineValues[0] : "";
    			String strPriceToDist = lineValues.length > 1 ? lineValues[1] : "";
    			String strPriceToMarket = lineValues.length > 2 ? lineValues[2] : "";
    			String strPriceToConsumer = lineValues.length > 3 ? lineValues[3] : "";
    			String strTaxPercentage = lineValues.length > 5 ? lineValues[5] : "";
    			// check product exists or not
    			try {
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					if (product == null || UtilValidate.isEmpty(strPriceToDist)) {
						if (product == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "DAProductNotExists",locale));
						}
						if (UtilValidate.isEmpty(strPriceToDist)) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "DAPriceToDistOfProductXIsEmptyDontCreate", UtilMisc.<String, Object> toMap("productId", productId), locale));
		    			}
		        	} else {
						// get list product pricing rules of quotation
						/* List<GenericValue> priceRulesOfProduct = delegator.findByAnd("ProductQuotationAndPriceRCA", UtilMisc.toMap("pq_ProductQuotationId", productQuotationId, "productId", productId, "inputParamEnumId", "PRIP_PRODUCT_ID", "productPriceActionTypeId", "PRICE_FLAT"), null, false);*/
						List<EntityCondition> listCond = new ArrayList<EntityCondition>();
						listCond.add(EntityCondition.makeCondition("pq_ProductQuotationId", productQuotationId));
						listCond.add(EntityCondition.makeCondition("productId", productId));
						listCond.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PRODUCT_ID"));
						EntityFindOptions findOpts = new EntityFindOptions();
						findOpts.setDistinct(true);
		        		List<GenericValue> priceRulesOfProduct = delegator.findList("ProductQuotationAndPriceRCA", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, findOpts, false);
						if (priceRulesOfProduct == null || priceRulesOfProduct.isEmpty()) {
							// return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAProductQuotationRuleForThisProductNotFound",locale));
							// INSERT: create new rule (conditions/actions)
							Map<String, Object> resultValue = createQuotationRule(delegator, productQuotationId, description, quotationName, productId, strPriceToDist, strPriceToMarket, strPriceToConsumer, partyIdsApply);
	    					if (resultValue != null) {
	    						return resultValue;
	    					}
	    					if (UtilValidate.isNotEmpty(partyRoleTypesApplyMarket) && UtilValidate.isNotEmpty(strPriceToMarket)) {
	    						Map<String, Object> resultValue2 = createQuotationRule(delegator, productQuotationId, description, quotationName, productId, strPriceToMarket, partyRoleTypesApplyMarket, true, strTaxPercentage);
	    						if (resultValue2 != null) {
	        						return resultValue2;
	        					}
	    					}
						} else {
							// UPDATE: product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
				            
							//List<String> listProductPriceRuleId = EntityUtil.getFieldListFromEntityList(priceRulesOfProduct, "productPriceRuleId", true);
							for (GenericValue productPriceRuleRCA : priceRulesOfProduct) {
								boolean hasPriceToMarket = false;
								boolean hasPriceToConsumer = false;
								String productPriceRuleId = productPriceRuleRCA.getString("productPriceRuleId");
								String productPriceActionSeqId = "";
								if ("PRICE_FLAT".equals(productPriceRuleRCA.getString("productPriceActionTypeId"))) {
									//  priceToDist 
						            if (UtilValidate.isNotEmpty(strPriceToDist)) {
						            	productPriceActionSeqId = productPriceRuleRCA.getString("ppa_productPriceActionSeqId");
					            		BigDecimal priceToDist = new BigDecimal(strPriceToDist);
					            		GenericValue productPriceActionFirst = delegator.findOne("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productPriceActionSeqId", productPriceActionSeqId), false);
					            		if (productPriceActionFirst != null) {
					            			productPriceActionFirst.set("amount", priceToDist);
					            			delegator.store(productPriceActionFirst);
					            		}
						            }
								}
								if ("PRICE_MARKET".equals(productPriceRuleRCA.getString("ppam_productPriceActionTypeId")) && !hasPriceToMarket) {
									productPriceActionSeqId = productPriceRuleRCA.getString("ppam_productPriceActionSeqId");
									// priceToMarket
						            BigDecimal priceToMarket = null;
						            if (UtilValidate.isNotEmpty(strPriceToMarket)) priceToMarket = new BigDecimal(strPriceToMarket);
				            		GenericValue productPriceActionSecond = delegator.findOne("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productPriceActionSeqId", productPriceActionSeqId), false);
				            		if (productPriceActionSecond != null) {
				            			productPriceActionSecond.set("amount", priceToMarket);
				            			delegator.store(productPriceActionSecond);
				            		}
				            		hasPriceToMarket = true;
				            		
				            		// new code
				            		if (UtilValidate.isNotEmpty(partyRoleTypesApplyMarket) && UtilValidate.isNotEmpty(strPriceToMarket)) {
				            			//ProductQuotationAndPriceRCAIsExtra
					            		List<GenericValue> listPriceRulesOfProductMarket = delegator.findByAnd("ProductQuotationAndPriceRCAIsExtra", 
												UtilMisc.toMap("productQuotationId", productQuotationId, "condProductId", productId, "condRoleTypeId", partyRoleTypesApplyMarket), null, false);
					            		if (UtilValidate.isNotEmpty(listPriceRulesOfProductMarket)) {
					            			for (GenericValue prMarket : listPriceRulesOfProductMarket) {
					            				GenericValue productPriceActionSecondMarket = delegator.findOne("ProductPriceAction", 
					            						UtilMisc.toMap("productPriceRuleId", prMarket.get("productPriceRuleId"), "productPriceActionSeqId", prMarket.get("productPriceActionSeqId")), false);
						            			if (UtilValidate.isNotEmpty(productPriceActionSecondMarket)) {
						            				BigDecimal hundred = new BigDecimal(100);
						            	            BigDecimal taxPercentage = null;
						            	            if (UtilValidate.isNotEmpty(strTaxPercentage)) {
						            	            	taxPercentage = new BigDecimal(strTaxPercentage);
						            	            } else {
						            	            	taxPercentage = BigDecimal.ZERO;
						            	            }
						            	            if (taxPercentage != null) {
						            	            	// price
						                        		BigDecimal priceAction = (priceToMarket.divide(taxPercentage.add(hundred), 2, RoundingMode.HALF_UP)).multiply(hundred);
						                        		productPriceActionSecondMarket.put("amount", priceAction);
						                        		delegator.store(productPriceActionSecondMarket);
						                        		
						                        		listQuotationRuleIdUpdate.add(prMarket.getString("productPriceRuleId"));
						            	            }
						            			}
					            			}
					            			
					            		}
				            		}
				            		
								}
								if ("PRICE_CONSUMER".equals(productPriceRuleRCA.getString("ppac_productPriceActionTypeId")) && !hasPriceToConsumer) {
									productPriceActionSeqId = productPriceRuleRCA.getString("ppac_productPriceActionSeqId");
									// priceToConsumer
						            BigDecimal priceToConsumer = null;
						            if (UtilValidate.isNotEmpty(strPriceToConsumer)) priceToConsumer = new BigDecimal(strPriceToConsumer);
				            		GenericValue productPriceActionThird = delegator.findOne("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productPriceActionSeqId", productPriceActionSeqId), false);
				            		if (productPriceActionThird != null) {
				            			productPriceActionThird.set("amount", priceToConsumer);
				            			delegator.store(productPriceActionThird);
				            		}
				            		hasPriceToConsumer = true;
								}
								// INSERT: new action
								if (!hasPriceToMarket) {
									BigDecimal priceToMarket = null;
						            if (UtilValidate.isNotEmpty(strPriceToMarket)) {
						            	priceToMarket = new BigDecimal(strPriceToMarket);
						            	GenericValue newEntityProductPriceActionMarket = delegator.makeValue("ProductPriceAction");
				            			newEntityProductPriceActionMarket.set("productPriceRuleId", productPriceRuleId);
				            			delegator.setNextSubSeqId(newEntityProductPriceActionMarket, "productPriceActionSeqId", 2, 1);
			            				newEntityProductPriceActionMarket.set("productPriceActionTypeId", "PRICE_MARKET");
			            				newEntityProductPriceActionMarket.set("amount", priceToMarket);
			            				delegator.create(newEntityProductPriceActionMarket);
			            				hasPriceToMarket = true;
			            				
			            				BigDecimal hundred = new BigDecimal(100);
			            	            BigDecimal taxPercentage = null;
			            	            if (UtilValidate.isNotEmpty(strTaxPercentage)) {
			            	            	taxPercentage = new BigDecimal(strTaxPercentage);
			            	            } else {
			            	            	taxPercentage = BigDecimal.ZERO;
			            	            }
			            	            if (UtilValidate.isNotEmpty(strPriceToMarket) && taxPercentage != null) {
			            	            	// price
			                        		BigDecimal priceAction = (priceToMarket.divide(taxPercentage.add(hundred), 2, RoundingMode.HALF_UP)).multiply(hundred);
			            					GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
			            					newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
			            					delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
			            					newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
			            					newEntityProductPriceAction.put("amount", priceAction);
			            		            delegator.create(newEntityProductPriceAction);
			            	            }
						            }
								}
								if (!hasPriceToConsumer) {
									BigDecimal priceToConsumer = null;
									if (UtilValidate.isNotEmpty(strPriceToConsumer)) {
										priceToConsumer = new BigDecimal(strPriceToConsumer);
										GenericValue newEntityProductPriceActionConsumer = delegator.makeValue("ProductPriceAction");
				            			newEntityProductPriceActionConsumer.set("productPriceRuleId", productPriceRuleId);
				            			delegator.setNextSubSeqId(newEntityProductPriceActionConsumer, "productPriceActionSeqId", 2, 1);
				            			newEntityProductPriceActionConsumer.set("productPriceActionTypeId", "PRICE_CONSUMER");
				            			newEntityProductPriceActionConsumer.set("amount", priceToConsumer);
			            				delegator.create(newEntityProductPriceActionConsumer);
			            				hasPriceToConsumer = true;
									}
								}
								listQuotationRuleIdUpdate.add(productPriceRuleId);
							}
						}
		        	}
    			} catch (Exception e) {
           		 	Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
    			}
    		}
    		
    		// DELETE: check and delete product quotation rule
    		if (listQuotationRuleIdAll != null) {
    			try {
	    			for (String productPriceRuleId : listQuotationRuleIdAll) {
	    				if (!listQuotationRuleIdUpdate.contains(productPriceRuleId)) {
	    					/*boolean hasPermissionDelete = security.hasEntityPermission("DELYS_QUOTATION", "_DELETE", userLogin);;
	    			        if (!hasPermissionDelete) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationDeletePermissionError",locale));*/
	    			        
	    			        // find all condition of a quotation rule
	    			        List<GenericValue> listProductPriceCond = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
	    			        if (listProductPriceCond != null) delegator.removeAll(listProductPriceCond);
	    			        
	    			        // find all action of a quotation rule
	    			        List<GenericValue> listProductPriceAct = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
	    			        if (listProductPriceAct != null) delegator.removeAll(listProductPriceAct);
	    			        
	    			        GenericValue productPriceRuleSelected = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), false);
	    			        if (productPriceRuleSelected != null) delegator.removeValue(productPriceRuleSelected);
	    				}
	    			}
    			} catch (Exception e) {
    				Debug.logError(e, "Cannot delete ProductPriceRule entity; problems with delete", module);
    	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAQuotationRuleDeleteFailedPleaseNotifyCustomerService",locale));
     			}
    		}
    	}
    	alertMessageList.add(UtilProperties.getMessage(resource, "DAUpdateSuccessful", locale));
    	if (alertMessageList.size() > 0) {
			successResult.put(ModelService.SUCCESS_MESSAGE_LIST, alertMessageList);
		}
        successResult.put("productQuotationId", productQuotationId);
        return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductQuotationAndPriceRCA(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.containsKey("productQuotationId")) {
    		String productQuotationId = (String) parameters.get("productQuotationId")[0];
    		if (UtilValidate.isNotEmpty(productQuotationId)) {
    			listAllConditions.add(EntityCondition.makeCondition("pq_ProductQuotationId", EntityOperator.EQUALS, productQuotationId));
    			listAllConditions.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PRODUCT_ID"));
    			listAllConditions.add(EntityCondition.makeCondition("productPriceActionTypeId", EntityOperator.EQUALS, "PRICE_FLAT"));
    			try {
    	    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    				listIterator = delegator.find("ProductQuotationAndPriceRCA", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductQuotationAndPriceRCA service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotatonRules(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.containsKey("productQuotationId")) {
    		String productQuotationId = (String) parameters.get("productQuotationId")[0];
    		if (UtilValidate.isNotEmpty(productQuotationId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
    			/*listAllConditions.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PRODUCT_ID"));
    			listAllConditions.add(EntityCondition.makeCondition("productPriceActionTypeId", EntityOperator.EQUALS, "PRICE_FLAT"));*/
    			try {
    	    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    				listIterator = delegator.find("ProductQuotationRules", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductQuotatonRules service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotatonRulesAndTax(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.containsKey("productQuotationId")) {
    		String productQuotationId = (String) parameters.get("productQuotationId")[0];
    		if (UtilValidate.isNotEmpty(productQuotationId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
    			/*listAllConditions.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PRODUCT_ID"));
    			listAllConditions.add(EntityCondition.makeCondition("productPriceActionTypeId", EntityOperator.EQUALS, "PRICE_FLAT"));*/
    			try {
    	    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    				listIterator = delegator.find("ProductQuotationRulesAndTax", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductQuotatonRules service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    private static GenericValue getPriceValueForType(String productPriceTypeId, List<GenericValue> productPriceList, List<GenericValue> secondaryPriceList) {
        List<GenericValue> filteredPrices = EntityUtil.filterByAnd(productPriceList, UtilMisc.toMap("productPriceTypeId", productPriceTypeId));
        GenericValue priceValue = EntityUtil.getFirst(filteredPrices);
        if (filteredPrices != null && filteredPrices.size() > 1) {
            if (Debug.infoOn()) Debug.logInfo("There is more than one " + productPriceTypeId + " with the currencyUomId " + priceValue.getString("currencyUomId") + " and productId " + priceValue.getString("productId") + ", using the latest found with price: " + priceValue.getBigDecimal("price"), module);
        }
        if (priceValue == null && secondaryPriceList != null) {
            return getPriceValueForType(productPriceTypeId, secondaryPriceList, null);
        }
        return priceValue;
    }
    
    // TODOCHANE add new "quantityUomId"
    /**
     * <p>Calculates the price of a product from pricing rules given the following input, and of course access to the database:</p>
     * <ul>
     *   <li>productId
     *   <li>partyId
     *   <li>prodCatalogId
     *   <li>webSiteId
     *   <li>productStoreId
     *   <li>productStoreGroupId
     *   <li>agreementId
     *   <li>quantity
     *   <li>currencyUomId
     *   <li>quantityUomId
     *   <li>checkIncludeVat
     * </ul>
     */
    public static Map<String, Object> calculateProductPriceCustom(DispatchContext dctx, Map<String, ? extends Object> context) {
        // UtilTimer utilTimer = new UtilTimer();
        // utilTimer.timerString("Starting price calc", module);
        // utilTimer.setLog(false);

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        GenericValue product = (GenericValue) context.get("product");
        String productId = product.getString("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String webSiteId = (String) context.get("webSiteId");
        String checkIncludeVat = (String) context.get("checkIncludeVat");
        String surveyResponseId = (String) context.get("surveyResponseId");
        Map<String, Object> customAttributes = UtilGenerics.checkMap(context.get("customAttributes"));

        String findAllQuantityPricesStr = (String) context.get("findAllQuantityPrices");
        boolean findAllQuantityPrices = "Y".equals(findAllQuantityPricesStr);
        boolean optimizeForLargeRuleSet = "Y".equals(context.get("optimizeForLargeRuleSet"));

        String agreementId = (String) context.get("agreementId");

        String productStoreId = (String) context.get("productStoreId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        Locale locale = (Locale) context.get("locale");
        
        GenericValue productStore = null;
        try {
            // we have a productStoreId, if the corresponding ProductStore.primaryStoreGroupId is not empty, use that
            productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
        }
        if (UtilValidate.isEmpty(productStoreGroupId)) {
            if (productStore != null) {
                try {
                    if (UtilValidate.isNotEmpty(productStore.getString("primaryStoreGroupId"))) {
                        productStoreGroupId = productStore.getString("primaryStoreGroupId");
                    } else {
                        // no ProductStore.primaryStoreGroupId, try ProductStoreGroupMember
                        List<GenericValue> productStoreGroupMemberList = delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "-fromDate"), true);
                        productStoreGroupMemberList = EntityUtil.filterByDate(productStoreGroupMemberList, true);
                        if (productStoreGroupMemberList.size() > 0) {
                            GenericValue productStoreGroupMember = EntityUtil.getFirst(productStoreGroupMemberList);
                            productStoreGroupId = productStoreGroupMember.getString("productStoreGroupId");
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
                }
            }

            // still empty, default to _NA_
            if (UtilValidate.isEmpty(productStoreGroupId)) {
                productStoreGroupId = "_NA_";
            }
        }

        // if currencyUomId is null get from properties file, if nothing there assume USD (USD: American Dollar) for now
        String currencyDefaultUomId = (String) context.get("currencyUomId");
        String currencyUomIdTo = (String) context.get("currencyUomIdTo"); 
        if (UtilValidate.isEmpty(currencyDefaultUomId)) {
            currencyDefaultUomId = EntityUtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD", delegator);
        }
        
        // TODOCHANGE new quantityUomId
        String quantityDefaultUomId = (String) context.get("quantityUomId");

        // productPricePurposeId is null assume "PURCHASE", which is equivalent to what prices were before the purpose concept
        String productPricePurposeId = (String) context.get("productPricePurposeId");
        if (UtilValidate.isEmpty(productPricePurposeId)) {
            productPricePurposeId = "PURCHASE";
        }

        // termUomId, for things like recurring prices specifies the term (time/frequency measure for example) of the recurrence
        // if this is empty it will simply not be used to constrain the selection
        String termUomId = (String) context.get("termUomId");

        // if this product is variant, find the virtual product and apply checks to it as well
        String virtualProductId = null;
        if ("Y".equals(product.getString("isVariant"))) {
            try {
                virtualProductId = ProductWorker.getVariantVirtualId(product);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting virtual product id from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPriceCannotRetrieveVirtualProductId", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // get prices for virtual product if one is found; get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> virtualProductPrices = null;
        if (virtualProductId != null) {
            try {
                virtualProductPrices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", virtualProductId, "currencyUomId", currencyDefaultUomId, "productStoreGroupId", productStoreGroupId), UtilMisc.toList("-fromDate"), true);
            } catch (GenericEntityException e) {
                Debug.logError(e, "An error occurred while getting the product prices", module);
            }
            virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, true);
        }

        // NOTE: partyId CAN be null
        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId) && context.get("userLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            partyId = userLogin.getString("partyId");
        }

        // check for auto-userlogin for price rules
        if (UtilValidate.isEmpty(partyId) && context.get("autoUserLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("autoUserLogin");
            partyId = userLogin.getString("partyId");
        }

        BigDecimal quantity = (BigDecimal) context.get("quantity");
        if (quantity == null) quantity = BigDecimal.ONE;

        BigDecimal amount = (BigDecimal) context.get("amount");

        List<EntityCondition> productPriceEcList = FastList.newInstance();
        productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        // this funny statement is for backward compatibility purposes; the productPricePurposeId is a new pk field on the ProductPrice entity and in order databases may not be populated, until the pk is updated and such; this will ease the transition somewhat
        if ("PURCHASE".equals(productPricePurposeId)) {
            productPriceEcList.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId),
                    EntityOperator.OR,
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, null)));
        } else {
            productPriceEcList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId));
        }
        productPriceEcList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyDefaultUomId));
        productPriceEcList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
        List<EntityCondition> productPriceEcListBefore = FastList.newInstance();
        productPriceEcListBefore.addAll(productPriceEcList);
        if (UtilValidate.isNotEmpty(termUomId)) {//TODOCHANGE used to is quantityUomId
            productPriceEcList.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, termUomId));
        }
        EntityCondition productPriceEc = EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND);
        
        //TODOCHANGE new quantityUomId
        BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
        
        // for prices, get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> productPrices = null;
        try {
            productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
            
            // TODOCHANGE add new process
            if (UtilValidate.isEmpty(productPrices)) {
            	// convert quantityUomId (input) to productPackingUomId
				if (!quantityDefaultUomId.equals(product.getString("productPackingUomId"))) {
					try {
						GenericValue userLogin = (GenericValue) context.get("userLogin");
						Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", product.getString("productPackingUomId"), "userLogin", userLogin));
						if (ServiceUtil.isSuccess(resultValue)) {
							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
							
							List<EntityCondition> productPriceEcList2 = FastList.newInstance();
							productPriceEcList2.addAll(productPriceEcListBefore);
							productPriceEcList2.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, product.getString("productPackingUomId")));
							EntityCondition productPriceEc2 = EntityCondition.makeCondition(productPriceEcList2, EntityOperator.AND);
							productPrices = delegator.findList("ProductPrice", productPriceEc2, null, UtilMisc.toList("-fromDate"), null, true);
							if (UtilValidate.isEmpty(productPrices)) {
								if (!quantityDefaultUomId.equals(product.getString("quantityUomId"))) {
									Map<String, Object> resultValue2 = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", product.getString("quantityUomId"), "userLogin", userLogin));
									if (ServiceUtil.isSuccess(resultValue2)) {
										quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
										
										List<EntityCondition> productPriceEcList3 = FastList.newInstance();
										productPriceEcList3.addAll(productPriceEcListBefore);
										productPriceEcList3.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, product.getString("productPackingUomId")));
										EntityCondition productPriceEc3 = EntityCondition.makeCondition(productPriceEcList3, EntityOperator.AND);
										productPrices = delegator.findList("ProductPrice", productPriceEc3, null, UtilMisc.toList("-fromDate"), null, true);
									}
								}
							}
						}
					} catch (Exception e) {
    		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
    		        }
				} else {
					quantityUomIdToDefault = BigDecimal.ONE;
				}
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "An error occurred while getting the product prices", module);
        }
        productPrices = EntityUtil.filterByDate(productPrices, true);

        // ===== get the prices we need: list, default, average cost, promo, min, max =====
        // if any of these prices is missing and this product is a variant, default to the corresponding price on the virtual product
        GenericValue listPriceValue = getPriceValueForType("LIST_PRICE", productPrices, virtualProductPrices);
        GenericValue defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", productPrices, virtualProductPrices);

        // If there is an agreement between the company and the client, and there is
        // a price for the product in it, it will override the default price of the
        // ProductPrice entity.
        if (UtilValidate.isNotEmpty(agreementId)) {
            try {
                List<GenericValue> agreementPrices = delegator.findByAnd("AgreementItemAndProductAppl", UtilMisc.toMap("agreementId", agreementId, "productId", productId, "currencyUomId", currencyDefaultUomId), null, false);
                GenericValue agreementPriceValue = EntityUtil.getFirst(agreementPrices);
                if (agreementPriceValue != null && agreementPriceValue.get("price") != null) {
                    defaultPriceValue = agreementPriceValue;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting agreement info from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPriceCannotRetrieveAgreementInfo", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        GenericValue competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", productPrices, virtualProductPrices);
        GenericValue averageCostValue = getPriceValueForType("AVERAGE_COST", productPrices, virtualProductPrices);
        GenericValue promoPriceValue = getPriceValueForType("PROMO_PRICE", productPrices, virtualProductPrices);
        GenericValue minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", productPrices, virtualProductPrices);
        GenericValue specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", productPrices, virtualProductPrices);

        // now if this is a virtual product check each price type, if doesn't exist get from variant with lowest DEFAULT_PRICE
        if ("Y".equals(product.getString("isVirtual"))) {
            // only do this if there is no default price, consider the others optional for performance reasons
            if (defaultPriceValue == null) {
                // Debug.logInfo("Product isVirtual and there is no default price for ID " + productId + ", trying variant prices", module);

                //use the cache to find the variant with the lowest default price
                try {
                    List<GenericValue> variantAssocList = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"), true));
                    BigDecimal minDefaultPrice = null;
                    List<GenericValue> variantProductPrices = null;
                    @SuppressWarnings("unused")
					String variantProductId = null;
                    for (GenericValue variantAssoc: variantAssocList) {
                        String curVariantProductId = variantAssoc.getString("productIdTo");
                        List<GenericValue> curVariantPriceList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", curVariantProductId), UtilMisc.toList("-fromDate"), true), nowTimestamp);
                        List<GenericValue> tempDefaultPriceList = EntityUtil.filterByAnd(curVariantPriceList, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                        GenericValue curDefaultPriceValue = EntityUtil.getFirst(tempDefaultPriceList);
                        if (curDefaultPriceValue != null) {
                            BigDecimal curDefaultPrice = curDefaultPriceValue.getBigDecimal("price");
                            if (minDefaultPrice == null || curDefaultPrice.compareTo(minDefaultPrice) < 0) {
                                // check to see if the product is discontinued for sale before considering it the lowest price
                                GenericValue curVariantProduct = delegator.findOne("Product", UtilMisc.toMap("productId", curVariantProductId), true);
                                if (curVariantProduct != null) {
                                    Timestamp salesDiscontinuationDate = curVariantProduct.getTimestamp("salesDiscontinuationDate");
                                    if (salesDiscontinuationDate == null || salesDiscontinuationDate.after(nowTimestamp)) {
                                        minDefaultPrice = curDefaultPrice;
                                        variantProductPrices = curVariantPriceList;
                                        variantProductId = curVariantProductId;
                                        // Debug.logInfo("Found new lowest price " + minDefaultPrice + " for variant with ID " + variantProductId, module);
                                    }
                                }
                            }
                        }
                    }

                    if (variantProductPrices != null) {
                        // we have some other options, give 'em a go...
                        if (listPriceValue == null) {
                            listPriceValue = getPriceValueForType("LIST_PRICE", variantProductPrices, null);
                        }
                        if (defaultPriceValue == null) {
                            defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", variantProductPrices, null);
                        }
                        if (competitivePriceValue == null) {
                            competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", variantProductPrices, null);
                        }
                        if (averageCostValue == null) {
                            averageCostValue = getPriceValueForType("AVERAGE_COST", variantProductPrices, null);
                        }
                        if (promoPriceValue == null) {
                            promoPriceValue = getPriceValueForType("PROMO_PRICE", variantProductPrices, null);
                        }
                        if (minimumPriceValue == null) {
                            minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", variantProductPrices, null);
                        }
                        if (maximumPriceValue == null) {
                            maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", variantProductPrices, null);
                        }
                        if (wholesalePriceValue == null) {
                            wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", variantProductPrices, null);
                        }
                        if (specialPromoPriceValue == null) {
                            specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", variantProductPrices, null);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "An error occurred while getting the product prices", module);
                }
            }
        }

        //boolean validPromoPriceFound = false;
        BigDecimal promoPrice = BigDecimal.ZERO;
        if (promoPriceValue != null && promoPriceValue.get("price") != null) {
            promoPrice = promoPriceValue.getBigDecimal("price");
            //validPromoPriceFound = true;
        }

        //boolean validWholesalePriceFound = false;
        BigDecimal wholesalePrice = BigDecimal.ZERO;
        if (wholesalePriceValue != null && wholesalePriceValue.get("price") != null) {
            wholesalePrice = wholesalePriceValue.getBigDecimal("price");
            //validWholesalePriceFound = true;
        }

        boolean validPriceFound = false;
        BigDecimal defaultPrice = BigDecimal.ZERO;
        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        if (defaultPriceValue != null) {
            // If a price calc formula (service) is specified, then use it to get the unit price
            if ("ProductPrice".equals(defaultPriceValue.getEntityName()) && UtilValidate.isNotEmpty(defaultPriceValue.getString("customPriceCalcService"))) {
                GenericValue customMethod = null;
                try {
                    customMethod = defaultPriceValue.getRelatedOne("CustomMethod", false);
                } catch (GenericEntityException gee) {
                    Debug.logError(gee, "An error occurred while getting the customPriceCalcService", module);
                }
                if (UtilValidate.isNotEmpty(customMethod) && UtilValidate.isNotEmpty(customMethod.getString("customMethodName"))) {
                    Map<String, Object> inMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "product", product);
                    inMap.put("initialPrice", defaultPriceValue.getBigDecimal("price"));
                    inMap.put("currencyUomId", currencyDefaultUomId);
                    inMap.put("quantity", quantity);
                    inMap.put("amount", amount);
                    if (UtilValidate.isNotEmpty(surveyResponseId)) {
                        inMap.put("surveyResponseId", surveyResponseId);
                    }
                    if (UtilValidate.isNotEmpty(customAttributes)) {
                        inMap.put("customAttributes", customAttributes);
                    }
                    try {
                        Map<String, Object> outMap = dispatcher.runSync(customMethod.getString("customMethodName"), inMap);
                        if (!ServiceUtil.isError(outMap)) {
                            BigDecimal calculatedDefaultPrice = (BigDecimal)outMap.get("price");
                            orderItemPriceInfos = UtilGenerics.checkList(outMap.get("orderItemPriceInfos"));
                            if (UtilValidate.isNotEmpty(calculatedDefaultPrice)) {
                                defaultPrice = calculatedDefaultPrice;
                                validPriceFound = true;
                            }
                        }
                    } catch (GenericServiceException gse) {
                        Debug.logError(gse, "An error occurred while running the customPriceCalcService [" + customMethod.getString("customMethodName") + "]", module);
                    }
                }
            }
            if (!validPriceFound && defaultPriceValue.get("price") != null) {
                defaultPrice = defaultPriceValue.getBigDecimal("price");
                validPriceFound = true;
            }
        }

        BigDecimal listPrice = listPriceValue != null ? listPriceValue.getBigDecimal("price") : null;
        
        if (listPrice == null) {
            // no list price, use defaultPrice for the final price

            // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
            BigDecimal maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getBigDecimal("price") : null;
            if (maxSellPrice != null && defaultPrice.compareTo(maxSellPrice) > 0) {
                defaultPrice = maxSellPrice;
            }
            // min price second to override max price, safety net
            BigDecimal minSellPrice = minimumPriceValue != null ? minimumPriceValue.getBigDecimal("price") : null;
            if (minSellPrice != null && defaultPrice.compareTo(minSellPrice) < 0) {
                defaultPrice = minSellPrice;
                // since we have found a minimum price that has overriden a the defaultPrice, even if no valid one was found, we will consider it as if one had been...
                validPriceFound = true;
            }

            result.put("basePrice", defaultPrice);
            result.put("price", defaultPrice);
            result.put("defaultPrice", defaultPrice);
            result.put("competitivePrice", competitivePriceValue != null ? competitivePriceValue.getBigDecimal("price") : null);
            result.put("averageCost", averageCostValue != null ? averageCostValue.getBigDecimal("price") : null);
            result.put("promoPrice", promoPriceValue != null ? promoPriceValue.getBigDecimal("price") : null);
            result.put("specialPromoPrice", specialPromoPriceValue != null ? specialPromoPriceValue.getBigDecimal("price") : null);
            result.put("validPriceFound", Boolean.valueOf(validPriceFound));
            result.put("isSale", Boolean.FALSE);
            result.put("orderItemPriceInfos", orderItemPriceInfos);

            Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                    checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
            if (errorResult != null) return errorResult;
        } else {
            try {
                List<GenericValue> allProductPriceRules = makeProducePriceRuleListCustom(delegator, optimizeForLargeRuleSet, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, currencyDefaultUomId, quantityDefaultUomId);
                // TODOCHANGE filter fromDate of pricing rule
                EntityCondition cond = EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null);
                allProductPriceRules = EntityUtil.filterByCondition(allProductPriceRules, cond);
                allProductPriceRules = EntityUtil.filterByDate(allProductPriceRules, true);

                List<GenericValue> quantityProductPriceRules = null;
                List<GenericValue> nonQuantityProductPriceRules = null;
                String quantityUomId = quantityDefaultUomId;
                if (findAllQuantityPrices) {
                    // split into list with quantity conditions and list without, then iterate through each quantity cond one
                    quantityProductPriceRules = FastList.newInstance();
                    nonQuantityProductPriceRules = FastList.newInstance();
                    for (GenericValue productPriceRule: allProductPriceRules) {
                        List<GenericValue> productPriceCondList = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRule.get("productPriceRuleId")), null, true);

                        boolean foundQuantityInputParam = false;
                        // only consider a rule if all conditions except the quantity condition are true
                        boolean allExceptQuantTrue = true;
                        for (GenericValue productPriceCond: productPriceCondList) {
                            if ("PRIP_QUANTITY".equals(productPriceCond.getString("inputParamEnumId"))) {
                                foundQuantityInputParam = true;
                            } else {
                                if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp)) {
                                    allExceptQuantTrue = false;
                                }
                            }
                        }

                        if (foundQuantityInputParam && allExceptQuantTrue) {
                            quantityProductPriceRules.add(productPriceRule);
                        } else {
                            nonQuantityProductPriceRules.add(productPriceRule);
                        }
                    }
                }

                if (findAllQuantityPrices) {
                    List<Map<String, Object>> allQuantityPrices = FastList.newInstance();

                    // if findAllQuantityPrices then iterate through quantityProductPriceRules
                    // foreach create an entry in the out list and eval that rule and all nonQuantityProductPriceRules rather than a single rule
                    for (GenericValue quantityProductPriceRule: quantityProductPriceRules) {
                        List<GenericValue> ruleListToUse = FastList.newInstance();
                        ruleListToUse.add(quantityProductPriceRule);
                        ruleListToUse.addAll(nonQuantityProductPriceRules);

                        Map<String, Object> quantCalcResults = calcPriceResultFromRulesCustom(ruleListToUse, listPrice, defaultPrice, promoPrice,
                            wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                            averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                            webSiteId, partyId, null, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                        Map<String, Object> quantErrorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(quantCalcResults, competitivePriceValue, specialPromoPriceValue, productStore,
                            checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                        if (quantErrorResult != null) return quantErrorResult;
                        quantCalcResults.remove("isNext");
                        // also add the quantityProductPriceRule to the Map so it can be used for quantity break information
                        quantCalcResults.put("quantityProductPriceRule", quantityProductPriceRule);

                        allQuantityPrices.add(quantCalcResults);
                    }
                    
                    result.put("allQuantityPrices", allQuantityPrices);

                    // use a quantity 1 to get the main price, then fill in the quantity break prices
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, BigDecimal.ONE, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                    calcResults.remove("isNext");
                    result.putAll(calcResults);
                    // The orderItemPriceInfos out parameter requires a special treatment:
                    // the list of OrderItemPriceInfos generated by the price rule is appended to
                    // the existing orderItemPriceInfos list and the aggregated list is returned.
                    List<GenericValue> orderItemPriceInfosFromRule = UtilGenerics.checkList(calcResults.get("orderItemPriceInfos"));
                    if (UtilValidate.isNotEmpty(orderItemPriceInfosFromRule)) {
                        orderItemPriceInfos.addAll(orderItemPriceInfosFromRule);
                    }
                    result.put("orderItemPriceInfos", orderItemPriceInfos);

                    Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                            checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                    if (errorResult != null) return errorResult;
                } else {
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, quantity, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                    
                    // TODOCHANGE new code
                    if (!quantityDefaultUomId.equals(product.getString("quantityUomId"))) {
                    	if (calcResults.containsKey("isNext")) {
                        	boolean isNext = (Boolean) calcResults.get("isNext");
                        	if (isNext) {
                        		quantityUomId = product.getString("productPackingUomId");
                        		calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                                        webSiteId, partyId, quantity, currencyDefaultUomId, quantityUomId, delegator, nowTimestamp, locale);
                        		if (calcResults.containsKey("isNext")) {
                                	isNext = (Boolean) calcResults.get("isNext");
                                	if (isNext) {
                                		quantityUomId = product.getString("quantityUomId");
                                		calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                                                wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                                                averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                                                webSiteId, partyId, quantity, currencyDefaultUomId, quantityUomId, delegator, nowTimestamp, locale);
                                	}
                        		}
                        	}
                        }
                    }
    				if (!quantityDefaultUomId.equals(quantityUomId)) {
    					// convert quantityDefaultUomId (input) to quantityUomId
    					try {
    						Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", quantityUomId));
    						if (ServiceUtil.isSuccess(resultValue)) {
    							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
    						}
    					} catch (Exception e) {
        		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
        		        }
    				} else {
    					quantityUomIdToDefault = BigDecimal.ONE;
    				}
                    if (quantityUomIdToDefault.compareTo(BigDecimal.ONE) > 0) {
                		String[] listPriceType = new String[]{"basePrice", "price", "listPrice", "defaultPrice", "averageCost"};
                		for (String priceType : listPriceType) {
                			BigDecimal valueNew = ((BigDecimal) calcResults.get(priceType)).multiply(quantityUomIdToDefault);
                			calcResults.put(priceType, valueNew);
                		}
                    }
                    calcResults.remove("isNext");
                    // End new code
                    
                    result.putAll(calcResults);
                    // The orderItemPriceInfos out parameter requires a special treatment:
                    // the list of OrderItemPriceInfos generated by the price rule is appended to
                    // the existing orderItemPriceInfos list and the aggregated list is returned.
                    List<GenericValue> orderItemPriceInfosFromRule = UtilGenerics.checkList(calcResults.get("orderItemPriceInfos"));
                    if (UtilValidate.isNotEmpty(orderItemPriceInfosFromRule)) {
                        orderItemPriceInfos.addAll(orderItemPriceInfosFromRule);
                    }
                    result.put("orderItemPriceInfos", orderItemPriceInfos);

                    Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                        checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                    if (errorResult != null) return errorResult;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting rules from the database while calculating price", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPriceCannotRetrievePriceRules", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // Convert the value to the price currency, if required
        if("true".equals(UtilProperties.getPropertyValue("ecommerce.properties", "convertProductPriceCurrency"))){
            if (UtilValidate.isNotEmpty(currencyDefaultUomId) && UtilValidate.isNotEmpty(currencyUomIdTo) && !currencyDefaultUomId.equals(currencyUomIdTo)) {
                if(UtilValidate.isNotEmpty(result)){
                    Map<String, Object> convertPriceMap = FastMap.newInstance();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        BigDecimal tempPrice = BigDecimal.ZERO;
                        if(entry.getKey() == "basePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "price")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "defaultPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "competitivePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "averageCost")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "promoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "specialPromoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "listPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        
                        if(tempPrice != null && tempPrice != BigDecimal.ZERO){
                            Map<String, Object> priceResults = FastMap.newInstance();
                            try {
                                priceResults = dispatcher.runSync("convertUom", UtilMisc.<String, Object>toMap("uomId", currencyDefaultUomId, "uomIdTo", currencyUomIdTo, "originalValue", tempPrice , "defaultDecimalScale" , Long.valueOf(2) , "defaultRoundingMode" , "HalfUp"));
                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                    Debug.logWarning("Unable to convert " + entry.getKey() + " for product  " + productId , module);
                                } 
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                            }
                            convertPriceMap.put(entry.getKey(), priceResults.get("convertedValue"));
                        }else{
                            convertPriceMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    if(UtilValidate.isNotEmpty(convertPriceMap)){
                        convertPriceMap.put("currencyUsed", currencyUomIdTo);
                        result = convertPriceMap;
                    }
                }
            }
        }
        
        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
        return result;
    }
    
    public static List<GenericValue> makeProducePriceRuleListCustom(Delegator delegator, boolean optimizeForLargeRuleSet, String productId, String virtualProductId, String prodCatalogId, 
    										String productStoreGroupId, String webSiteId, String partyId, String currencyUomId, String quantityUomId) throws GenericEntityException {
        List<GenericValue> productPriceRules = null;

        // At this point we have two options: optimize for large ruleset, or optimize for small ruleset
        // NOTE: This only effects the way that the rules to be evaluated are selected.
        // For large rule sets we can do a cached pre-filter to limit the rules that need to be evaled for a specific product.
        // Genercally I don't think that rule sets will get that big though, so the default is optimize for smaller rule set.
        if (optimizeForLargeRuleSet) {
            // ========= find all rules that must be run for each input type; this is kind of like a pre-filter to slim down the rules to run =========
            // utilTimer.timerString("Before create rule id list", module);
            TreeSet<String> productPriceRuleIds = new TreeSet<String>();

            // ------- These are all of the conditions that DON'T depend on the current inputs -------

            // by productCategoryId
            // for we will always include any rules that go by category, shouldn't be too many to iterate through each time and will save on cache entries
            // note that we always want to put the category, quantity, etc ones that find all rules with these conditions in separate cache lists so that they can be easily cleared
            Collection<GenericValue> productCategoryIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CAT_ID"), null, true);
            if (UtilValidate.isNotEmpty(productCategoryIdConds)) {
                for (GenericValue productCategoryIdCond: productCategoryIdConds) {
                    productPriceRuleIds.add(productCategoryIdCond.getString("productPriceRuleId"));
                }
            }

            // by productFeatureId
            Collection<GenericValue> productFeatureIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_FEAT_ID"), null, true);
            if (UtilValidate.isNotEmpty(productFeatureIdConds)) {
                for (GenericValue productFeatureIdCond: productFeatureIdConds) {
                    productPriceRuleIds.add(productFeatureIdCond.getString("productPriceRuleId"));
                }
            }

            // by quantity -- should we really do this one, ie is it necessary?
            // we could say that all rules with quantity on them must have one of these other values
            // but, no we'll do it the other way, any that have a quantity will always get compared
            Collection<GenericValue> quantityConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_QUANTITY"), null, true);
            if (UtilValidate.isNotEmpty(quantityConds)) {
                for (GenericValue quantityCond: quantityConds) {
                    productPriceRuleIds.add(quantityCond.getString("productPriceRuleId"));
                }
            }

            // by roleTypeId
            Collection<GenericValue> roleTypeIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_ROLE_TYPE"), null, true);
            if (UtilValidate.isNotEmpty(roleTypeIdConds)) {
                for (GenericValue roleTypeIdCond: roleTypeIdConds) {
                    productPriceRuleIds.add(roleTypeIdCond.getString("productPriceRuleId"));
                }
            }

            // TODO, not supported yet: by groupPartyId
            // TODO, not supported yet: by partyClassificationGroupId
            // later: (by partyClassificationTypeId)

            // by listPrice
            Collection<GenericValue> listPriceConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_LIST_PRICE"), null, true);
            if (UtilValidate.isNotEmpty(listPriceConds)) {
                for (GenericValue listPriceCond: listPriceConds) {
                    productPriceRuleIds.add(listPriceCond.getString("productPriceRuleId"));
                }
            }

            // ------- These are all of them that DO depend on the current inputs -------

            // by productId
            Collection<GenericValue> productIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", productId), null, true);
            if (UtilValidate.isNotEmpty(productIdConds)) {
                for (GenericValue productIdCond: productIdConds) {
                    productPriceRuleIds.add(productIdCond.getString("productPriceRuleId"));
                }
            }

            // by virtualProductId, if not null
            if (virtualProductId != null) {
                Collection<GenericValue> virtualProductIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", virtualProductId), null, true);
                if (UtilValidate.isNotEmpty(virtualProductIdConds)) {
                    for (GenericValue virtualProductIdCond: virtualProductIdConds) {
                        productPriceRuleIds.add(virtualProductIdCond.getString("productPriceRuleId"));
                    }
                }
            }

            // by prodCatalogId - which is optional in certain cases
            if (UtilValidate.isNotEmpty(prodCatalogId)) {
                Collection<GenericValue> prodCatalogIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CLG_ID", "condValue", prodCatalogId), null, true);
                if (UtilValidate.isNotEmpty(prodCatalogIdConds)) {
                    for (GenericValue prodCatalogIdCond: prodCatalogIdConds) {
                        productPriceRuleIds.add(prodCatalogIdCond.getString("productPriceRuleId"));
                    }
                }
            }

            // by productStoreGroupId
            if (UtilValidate.isNotEmpty(productStoreGroupId)) {
                Collection<GenericValue> storeGroupConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_SGRP_ID", "condValue", productStoreGroupId), null, true);
                if (UtilValidate.isNotEmpty(storeGroupConds)) {
                    for (GenericValue storeGroupCond: storeGroupConds) {
                        productPriceRuleIds.add(storeGroupCond.getString("productPriceRuleId"));
                    }
                }
            }

            // by webSiteId
            if (UtilValidate.isNotEmpty(webSiteId)) {
                Collection<GenericValue> webSiteIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_WEBSITE_ID", "condValue", webSiteId), null, true);
                if (UtilValidate.isNotEmpty(webSiteIdConds)) {
                    for (GenericValue webSiteIdCond: webSiteIdConds) {
                        productPriceRuleIds.add(webSiteIdCond.getString("productPriceRuleId"));
                    }
                }
            }

            // by partyId
            if (UtilValidate.isNotEmpty(partyId)) {
                Collection<GenericValue> partyIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PARTY_ID", "condValue", partyId), null, true);
                if (UtilValidate.isNotEmpty(partyIdConds)) {
                    for (GenericValue partyIdCond: partyIdConds) {
                        productPriceRuleIds.add(partyIdCond.getString("productPriceRuleId"));
                    }
                }
            }

            // by currencyUomId
            Collection<GenericValue> currencyUomIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_CURRENCY_UOMID", "condValue", currencyUomId), null, true);
            if (UtilValidate.isNotEmpty(currencyUomIdConds)) {
                for (GenericValue currencyUomIdCond: currencyUomIdConds) {
                    productPriceRuleIds.add(currencyUomIdCond.getString("productPriceRuleId"));
                }
            }
            
            // TODOCHANGE new "quantityUomId"
            // by quantityUomId
            Collection<GenericValue> quantityUomIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_QUANTITY_UOMID", "condValue", quantityUomId), null, true);
            if (UtilValidate.isNotEmpty(quantityUomIdConds)) {
                for (GenericValue quantityUomIdCond: quantityUomIdConds) {
                    productPriceRuleIds.add(quantityUomIdCond.getString("productPriceRuleId"));
                }
            }

            productPriceRules = FastList.newInstance();
            for (String productPriceRuleId: productPriceRuleIds) {
                GenericValue productPriceRule = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), true);
                if (productPriceRule == null) continue;
                productPriceRules.add(productPriceRule);
            }
        } else {
            // this would be nice, but we can't cache this so easily...
            // List pprExprs = UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
            // EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
            // productPriceRules = delegator.findByOr("ProductPriceRule", pprExprs);

            productPriceRules = delegator.findList("ProductPriceRule", null, null, null, null, true);
            if (productPriceRules == null) productPriceRules = FastList.newInstance();
        }

        return productPriceRules;
    }
    
    public static boolean checkPriceConditionCustom(GenericValue productPriceCond, String productId, String virtualProductId, String prodCatalogId,
            String productStoreGroupId, String webSiteId, String partyId, BigDecimal quantity, BigDecimal listPrice,
            String currencyUomId, String quantityUomId, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Checking price condition: " + productPriceCond, module);
        int compare = 0;

        if ("PRIP_PRODUCT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = productId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_PROD_CAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            // if a ProductCategoryMember exists for this productId and the specified productCategoryId
            String productCategoryId = productPriceCond.getString("condValue");
            List<GenericValue> productCategoryMembers = delegator.findByAnd("ProductCategoryMember",
                    UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId), null, true);
            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp, null, null, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (UtilValidate.isNotEmpty(productCategoryMembers)) {
                compare = 0;
            } else {
                compare = 1;
            }

            // if there is a virtualProductId, try that given that this one has failed
            // NOTE: this is important becuase of the common scenario where a virtual product is a member of a category but the variants will typically NOT be
            // NOTE: we may want to parameterize this in the future, ie with an indicator on the ProductPriceCond entity
            if (compare == 1 && UtilValidate.isNotEmpty(virtualProductId)) {
                List<GenericValue> virtualProductCategoryMembers = delegator.findByAnd("ProductCategoryMember",
                        UtilMisc.toMap("productId", virtualProductId, "productCategoryId", productCategoryId), null, true);
                // and from/thru date within range
                virtualProductCategoryMembers = EntityUtil.filterByDate(virtualProductCategoryMembers, nowTimestamp, null, null, true);
                if (UtilValidate.isNotEmpty(virtualProductCategoryMembers)) {
                    // we found a member record? great, then this condition is satisfied
                    compare = 0;
                }
            }
        } else if ("PRIP_PROD_FEAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            // NOTE: DEJ20070130 don't retry this condition with the virtualProductId as well; this breaks various things you might want to do with price rules, like have different pricing for a variant products with a certain distinguishing feature

            // if a ProductFeatureAppl exists for this productId and the specified productFeatureId
            String productFeatureId = productPriceCond.getString("condValue");
            List<GenericValue> productFeatureAppls = delegator.findByAnd("ProductFeatureAppl",
                    UtilMisc.toMap("productId", productId, "productFeatureId", productFeatureId), null, true);
            // and from/thru date within range
            productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls, nowTimestamp, null, null, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (UtilValidate.isNotEmpty(productFeatureAppls)) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PRIP_PROD_CLG_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(prodCatalogId)) {
                compare = prodCatalogId.compareTo(productPriceCond.getString("condValue"));
            } else {
                // this shouldn't happen because if prodCatalogId is null no PRIP_PROD_CLG_ID prices will be in the list
                compare = 1;
            }
        } else if ("PRIP_PROD_SGRP_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(productStoreGroupId)) {
                compare = productStoreGroupId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_WEBSITE_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(webSiteId)) {
                compare = webSiteId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_QUANTITY".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (quantity == null) {
                // if no quantity is passed in, assume all quantity conditions pass
                // NOTE: setting compare = 0 won't do the trick here because the condition won't always be or include and equal
                return true;
            } else {
                compare = quantity.compareTo(new BigDecimal(productPriceCond.getString("condValue")));
            }
        } else if ("PRIP_PARTY_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(partyId)) {
                compare = partyId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_PARTY_GRP_MEM".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isEmpty(partyId)) {
                compare = 1;
            } else {
                String groupPartyId = productPriceCond.getString("condValue");
                if (partyId.equals(groupPartyId)) {
                    compare = 0;
                } else {
                    // look for PartyRelationship with
                    // partyRelationshipTypeId=GROUP_ROLLUP, the partyIdTo is
                    // the group member, so the partyIdFrom is the groupPartyId
                    List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", groupPartyId, "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
                    // and from/thru date within range
                    partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, nowTimestamp, null, null, true);
                    // then 0 (equals), otherwise 1 (not equals)
                    if (UtilValidate.isNotEmpty(partyRelationshipList)) {
                        compare = 0;
                    } else {
                        compare = checkConditionPartyHierarchy(delegator, nowTimestamp, groupPartyId, partyId);
                    }
                }
            }
        } else if ("PRIP_PARTY_CLASS".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isEmpty(partyId)) {
                compare = 1;
            } else {
                String partyClassificationGroupId = productPriceCond.getString("condValue");
                // find any PartyClassification
                List<GenericValue> partyClassificationList = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyId", partyId, "partyClassificationGroupId", partyClassificationGroupId), null, true);
                // and from/thru date within range
                partyClassificationList = EntityUtil.filterByDate(partyClassificationList, nowTimestamp, null, null, true);
                // then 0 (equals), otherwise 1 (not equals)
                if (UtilValidate.isNotEmpty(partyClassificationList)) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            }
        } else if ("PRIP_ROLE_TYPE".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                // if a PartyRole exists for this partyId and the specified roleTypeId
                GenericValue partyRole = delegator.findOne("PartyRole",
                        UtilMisc.toMap("partyId", partyId, "roleTypeId", productPriceCond.getString("condValue")), true);

                // then 0 (equals), otherwise 1 (not equals)
                if (partyRole != null) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            } else {
                compare = 1;
            }
        } else if ("PRIP_LIST_PRICE".equals(productPriceCond.getString("inputParamEnumId"))) {
            BigDecimal listPriceValue = listPrice;

            compare = listPriceValue.compareTo(new BigDecimal(productPriceCond.getString("condValue")));
        } else if ("PRIP_CURRENCY_UOMID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = currencyUomId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_QUANTITY_UOMID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = quantityUomId.compareTo(productPriceCond.getString("condValue"));
        } else {
            Debug.logWarning("An un-supported productPriceCond input parameter (lhs) was used: " + productPriceCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Price Condition compare done, compare=" + compare, module);

        if ("PRC_EQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare == 0) return true;
        } else if ("PRC_NEQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare != 0) return true;
        } else if ("PRC_LT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare < 0) return true;
        } else if ("PRC_LTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare <= 0) return true;
        } else if ("PRC_GT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare > 0) return true;
        } else if ("PRC_GTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare >= 0) return true;
        } else {
            Debug.logWarning("An un-supported productPriceCond condition was used: " + productPriceCond.getString("operatorEnumId") + ", returning false, ie check failed", module);
            return false;
        }
        return false;
    }
    
    public static Map<String, Object> calcPriceResultFromRulesCustom(List<GenericValue> productPriceRules, BigDecimal listPrice, BigDecimal defaultPrice, BigDecimal promoPrice,
        BigDecimal wholesalePrice, GenericValue maximumPriceValue, GenericValue minimumPriceValue, boolean validPriceFound,
        GenericValue averageCostValue, String productId, String virtualProductId, String prodCatalogId, String productStoreGroupId,
        String webSiteId, String partyId, BigDecimal quantity, String currencyUomId, String quantityUomId, Delegator delegator, Timestamp nowTimestamp,
        Locale locale) throws GenericEntityException {

        Map<String, Object> calcResults = FastMap.newInstance();

        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        boolean isSale = false;

        // ========= go through each price rule by id and eval all conditions =========
        // utilTimer.timerString("Before eval rules", module);
        int totalConds = 0;
        int totalActions = 0;
        int totalRules = 0;

        // get some of the base values to calculate with
        BigDecimal averageCost = (averageCostValue != null && averageCostValue.get("price") != null) ? averageCostValue.getBigDecimal("price") : listPrice;
        BigDecimal margin = listPrice.subtract(averageCost);

        // calculate running sum based on listPrice and rules found
        BigDecimal price = listPrice;
        boolean isNext = true;
        for (GenericValue productPriceRule: productPriceRules) {
            String productPriceRuleId = productPriceRule.getString("productPriceRuleId");

            // check from/thru dates
            java.sql.Timestamp fromDate = productPriceRule.getTimestamp("fromDate");
            java.sql.Timestamp thruDate = productPriceRule.getTimestamp("thruDate");

            if (fromDate != null && fromDate.after(nowTimestamp)) {
                // hasn't started yet
                continue;
            }
            if (thruDate != null && thruDate.before(nowTimestamp)) {
                // already expired
                continue;
            }

            // check all conditions
            boolean allTrue = true;
            StringBuilder condsDescription = new StringBuilder();
            List<GenericValue> productPriceConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, true);
            for (GenericValue productPriceCond: productPriceConds) {

                totalConds++;

                if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyUomId, quantityUomId, delegator, nowTimestamp)) {
                    allTrue = false;
                    break;
                }

                // add condsDescription string entry
                condsDescription.append("[");
                GenericValue inputParamEnum = productPriceCond.getRelatedOne("InputParamEnumeration", true);

                condsDescription.append(inputParamEnum.getString("enumCode"));
                // condsDescription.append(":");
                GenericValue operatorEnum = productPriceCond.getRelatedOne("OperatorEnumeration", true);

                condsDescription.append(operatorEnum.getString("description"));
                // condsDescription.append(":");
                condsDescription.append(productPriceCond.getString("condValue"));
                condsDescription.append("] ");
            }

            // add some info about the prices we are calculating from
            condsDescription.append("[list:");
            condsDescription.append(listPrice);
            condsDescription.append(";avgCost:");
            condsDescription.append(averageCost);
            condsDescription.append(";margin:");
            condsDescription.append(margin);
            condsDescription.append("] ");

            boolean foundFlatOverride = false;

            // if all true, perform all actions
            if (allTrue) {
            	isNext = false;
                // check isSale
                if ("Y".equals(productPriceRule.getString("isSale"))) {
                    isSale = true;
                }

                List<GenericValue> productPriceActions = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, true);
                for (GenericValue productPriceAction: productPriceActions) {

                    totalActions++;

                    // yeah, finally here, perform the action, ie, modify the price
                    BigDecimal modifyAmount = BigDecimal.ZERO;

                    if ("PRICE_POD".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = defaultPrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                            price = defaultPrice;
                        }
                    } else if ("PRICE_POL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = listPrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POAC".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = averageCost.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POM".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = margin.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POWHS".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null && wholesalePrice != null) {
                            modifyAmount = wholesalePrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_FOL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = productPriceAction.getBigDecimal("amount");
                        }
                    } else if ("PRICE_FLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // this one is a bit different, break out of the loop because we now have our final price
                        foundFlatOverride = true;
                        if (productPriceAction.get("amount") != null) {
                            price = productPriceAction.getBigDecimal("amount");
                        } else {
                            Debug.logInfo("ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                            price = defaultPrice;
                            isSale = false;                // reverse isSale flag, as this sale rule was actually not applied
                        }
                    } else if ("PRICE_PFLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // this one is a bit different too, break out of the loop because we now have our final price
                        foundFlatOverride = true;
                        price = promoPrice;
                        if (productPriceAction.get("amount") != null) {
                            price = price.add(productPriceAction.getBigDecimal("amount"));
                        }
                        if (price.compareTo(BigDecimal.ZERO) == 0) {
                            if (defaultPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("PromoPrice and ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                price = defaultPrice;
                            } else if (listPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("PromoPrice and ProductPriceAction had null amount and no default price was available, using list price: " + listPrice + " for product with id " + productId, module);
                                price = listPrice;
                            } else {
                                Debug.logError("PromoPrice and ProductPriceAction had null amount and no default or list price was available, so price is set to zero for product with id " + productId, module);
                                price = BigDecimal.ZERO;
                            }
                            isSale = false;                // reverse isSale flag, as this sale rule was actually not applied
                        }
                    } else if ("PRICE_WFLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // same as promo price but using the wholesale price instead
                        foundFlatOverride = true;
                        price = wholesalePrice;
                        if (productPriceAction.get("amount") != null) {
                            price = price.add(productPriceAction.getBigDecimal("amount"));
                        }
                        if (price.compareTo(BigDecimal.ZERO) == 0) {
                            if (defaultPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("WholesalePrice and ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                price = defaultPrice;
                            } else if (listPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("WholesalePrice and ProductPriceAction had null amount and no default price was available, using list price: " + listPrice + " for product with id " + productId, module);
                                price = listPrice;
                            } else {
                                Debug.logError("WholesalePrice and ProductPriceAction had null amount and no default or list price was available, so price is set to zero for product with id " + productId, module);
                                price = BigDecimal.ZERO;
                            }
                            isSale = false; // reverse isSale flag, as this sale rule was actually not applied
                        }
                    }

                    // add a orderItemPriceInfo element too, without orderId or orderItemId
                    StringBuilder priceInfoDescription = new StringBuilder();

                    
                    priceInfoDescription.append(condsDescription.toString());
                    priceInfoDescription.append("[");
                    priceInfoDescription.append(UtilProperties.getMessage(resource, "ProductPriceConditionType", locale));
                    priceInfoDescription.append(productPriceAction.getString("productPriceActionTypeId"));
                    priceInfoDescription.append("]");

                    GenericValue orderItemPriceInfo = delegator.makeValue("OrderItemPriceInfo");

                    orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceRuleId"));
                    orderItemPriceInfo.set("productPriceActionSeqId", productPriceAction.get("productPriceActionSeqId"));
                    orderItemPriceInfo.set("modifyAmount", modifyAmount);
                    orderItemPriceInfo.set("rateCode", productPriceAction.get("rateCode"));
                    // make sure description is <= than 250 chars
                    String priceInfoDescriptionString = priceInfoDescription.toString();

                    if (priceInfoDescriptionString.length() > 250) {
                        priceInfoDescriptionString = priceInfoDescriptionString.substring(0, 250);
                    }
                    orderItemPriceInfo.set("description", priceInfoDescriptionString);
                    orderItemPriceInfos.add(orderItemPriceInfo);

                    if (foundFlatOverride) {
                        break;
                    } else {
                        price = price.add(modifyAmount);
                    }
                }
            }

            totalRules++;

            if (foundFlatOverride) {
                break;
            }
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("Unchecked Calculated price: " + price, module);
            Debug.logVerbose("PriceInfo:", module);
            for (GenericValue orderItemPriceInfo: orderItemPriceInfos) {
                Debug.logVerbose(" --- " + orderItemPriceInfo.toString(), module);
            }
        }

        // if no actions were run on the list price, then use the default price
        if (totalActions == 0) {
            price = defaultPrice;
            // here we will leave validPriceFound as it was originally set for the defaultPrice since that is what we are setting the price to...
        } else {
            // at least one price rule action was found, so we will consider it valid
            validPriceFound = true;
        }

        // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
        BigDecimal maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getBigDecimal("price") : null;
        if (maxSellPrice != null && price.compareTo(maxSellPrice) > 0) {
            price = maxSellPrice;
        }
        // min price second to override max price, safety net
        BigDecimal minSellPrice = minimumPriceValue != null ? minimumPriceValue.getBigDecimal("price") : null;
        if (minSellPrice != null && price.compareTo(minSellPrice) < 0) {
            price = minSellPrice;
            // since we have found a minimum price that has overriden a the defaultPrice, even if no valid one was found, we will consider it as if one had been...
            validPriceFound = true;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Final Calculated price: " + price + ", rules: " + totalRules + ", conds: " + totalConds + ", actions: " + totalActions, module);

        calcResults.put("basePrice", price);
        calcResults.put("price", price);
        calcResults.put("listPrice", listPrice);
        calcResults.put("defaultPrice", defaultPrice);
        calcResults.put("averageCost", averageCost);
        calcResults.put("orderItemPriceInfos", orderItemPriceInfos);
        calcResults.put("isSale", Boolean.valueOf(isSale));
        calcResults.put("validPriceFound", Boolean.valueOf(validPriceFound));
        // if all true, perform all actions
        calcResults.put("isNext", isNext);
        return calcResults;
    }
    
    private static int checkConditionPartyHierarchy(Delegator delegator, Timestamp nowTimestamp, String groupPartyId, String partyId) throws GenericEntityException{
        List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
        partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, nowTimestamp, null, null, true);
        for (GenericValue genericValue : partyRelationshipList) {
            String partyIdFrom = (String)genericValue.get("partyIdFrom");
            if (partyIdFrom.equals(groupPartyId)) {
                return 0;
            }
            if (0 == checkConditionPartyHierarchy(delegator, nowTimestamp, groupPartyId, partyIdFrom)) {
                return 0;
            }
        }
        
        return 1;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductQuotaton(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Security security = ctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String viewIndexStr = (String)parameters.get("pagenum")[0];
    	String viewSizeStr = (String)parameters.get("pagesize")[0];
    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
    	List<GenericValue> listQuotation = FastList.newInstance();
    	EntityListIterator listIterator2 = null;
    	try {
    		if (security.hasEntityPermission("DELYS_QUOTATION", "_VIEW", userLogin) || security.hasEntityPermission("QUOTATION", "_VIEW", userLogin)) {
    			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    			listIterator2 = delegator.find("ProductQuotation", tmpConditon, null, null, listSortFields, opts);
    		} else if (security.hasEntityPermission("QUOTATION_ROLE", "_VIEW", userLogin)) {
    			if (SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator)) {
    	    		Map<String, Object> tmpResult = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
    	    		if (ServiceUtil.isSuccess(tmpResult)) {
    	    			List<GenericValue> listStore = (List<GenericValue>) tmpResult.get("listProductStore");
    	    			if (UtilValidate.isNotEmpty(listStore)) {
    	    				List<String> salesMethodEnumIds = EntityUtil.getFieldListFromEntityList(listStore, "salesMethodChannelEnumId", true);
    	    				if (UtilValidate.isNotEmpty(salesMethodEnumIds)) listAllConditions.add(EntityCondition.makeCondition("salesChannel", EntityOperator.IN, salesMethodEnumIds));
    	    				listIterator2 = delegator.find("ProductQuotation", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	    			}
    	    		}
    			}
    		} else if (security.hasPermission("QUOTATION_CUST_VIEW", userLogin)) {
    			// get quotations of customer
    			List<String> listRoleType = EntityUtil.getFieldListFromEntityList(
    					delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, false), "roleTypeId", true);
    			if (UtilValidate.isNotEmpty(listRoleType)) {
    				List<GenericValue> listQuotationRole = EntityUtil.getFieldListFromEntityList(
    						delegator.findList("ProductQuotationRoleType", EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleType), null, null, null, false), "productQuotationId", true);
    				if (UtilValidate.isNotEmpty(listQuotationRole)) {
    					listAllConditions.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, listQuotationRole));
    					EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    					listIterator2 = delegator.find("ProductQuotation", tmpConditon, null, null, listSortFields, opts);
    				}
    			}
    		}
    		if (UtilValidate.isNotEmpty(listIterator2)) {
    			if (viewSize != 0) {
    				if (viewIndex == 0) {
    					listQuotation = listIterator2.getPartialList(0, viewSize);
    				} else {
    					listQuotation = listIterator2.getPartialList(viewIndex * viewSize + 1, viewSize);
    				}
    			} else {
    				listQuotation = listIterator2.getCompleteList();
    			}
    		}
    	    if (UtilValidate.isNotEmpty(listQuotation)) {
    	    	for (GenericValue itemProd : listQuotation) {
    				Map<String, Object> row = new HashMap<String, Object>();
    				row.put("productQuotationId", itemProd.get("productQuotationId"));
    				row.put("quotationName", itemProd.get("quotationName"));
    				row.put("salesChannel", itemProd.get("salesChannel"));
    				row.put("currencyUomId", itemProd.get("currencyUomId"));
    				row.put("fromDate", itemProd.get("fromDate"));
    				row.put("thruDate", itemProd.get("thruDate"));
    				row.put("statusId", itemProd.get("statusId"));
    				row.put("createDate", itemProd.get("createDate"));
    				
    				String partyApplies = "";
    				List<GenericValue> roleTypes = delegator.findByAnd("ProductQuotationRoleTypeAndRoleType", UtilMisc.toMap("productQuotationId", itemProd.get("productQuotationId")), null, false);
					if (roleTypes != null) {
						if (roleTypes.size() == 1) {
							GenericValue roleType = roleTypes.get(0);
							if (roleType.get("description") != null) partyApplies += roleType.getString("description");
							else partyApplies += roleType.getString("roleTypeId");
						} else {
							GenericValue roleType0 = roleTypes.get(0);
							if (roleType0.getString("description") != null) partyApplies += roleType0.getString("description");
							else partyApplies += roleType0.getString("roleTypeId");
							for (int i = 1; i < roleTypes.size(); i++) {
								GenericValue roleType = roleTypes.get(i);
								if (roleType.getString("description") != null) partyApplies += ", " + roleType.getString("description");
								else partyApplies += ", " + roleType.getString("roleTypeId");
							}
						}
					}
    				row.put("partyApplies", partyApplies);
    				listIterator.add(row);
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCategoryCatalogLM service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	if (listIterator2 != null) {
			try {
				int totalRows = listIterator2.getResultsSizeAfterPartialList();
				successResult.put("TotalRows", String.valueOf(totalRows));
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error when get size of list iterator", module);
			} finally {
				try {
					listIterator2.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
    	}
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
