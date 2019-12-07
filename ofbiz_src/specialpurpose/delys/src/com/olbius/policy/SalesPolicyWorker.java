/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.olbius.policy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.product.product.ProductSearch;
import org.ofbiz.service.DispatchContext;

import com.olbius.util.SalesPartyUtil;
import com.olbius.util.SecurityUtil;

/**
 * SalesPolicyWorker - Worker class for catalog/product sales policy related functionality
 */
public class SalesPolicyWorker {

    public static final String module = SalesPolicyWorker.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    public static final int decimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

    public static final MathContext generalRounding = new MathContext(10);

    public static List<SalesCommissionEntity> getSalesCommissionList(DispatchContext ctx, String roleTypeId, String customTimePeriodId, Timestamp fromDate, Timestamp thruDate, Locale locale) {
		List<SalesCommissionEntity> listSalesCommission = new ArrayList<SalesCommissionEntity>();
		Delegator delegator = ctx.getDelegator();
		boolean isCustomTimePeriod = false;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isNotEmpty(customTimePeriodId)) {
			isCustomTimePeriod = true;
		}
		List<GenericValue> listParty = null;
		try {
			//TODO when add 2 field fromDate, thruDate for PartyRole entity then get list party by date time period
			listParty = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", roleTypeId), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
			return null;
		}
		if (listParty != null) {
			if (!isCustomTimePeriod) {
				try {
					for (GenericValue partyItem : listParty) {
						List<EntityCondition> conditions0 = new ArrayList<EntityCondition>();
						conditions0.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyItem.getString("partyId")));
						conditions0.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
						conditions0.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, thruDate));
						List<GenericValue> listSalesCommissionInDatabase = delegator.findList("SalesCommissionData", EntityCondition.makeCondition(conditions0, EntityOperator.AND), null, null, null, false);
						if (UtilValidate.isNotEmpty(listSalesCommissionInDatabase) && listSalesCommissionInDatabase.size() > 0) {
							continue;
						}
						
						List<EntityCondition> conditions = new ArrayList<EntityCondition>();
						conditions.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.EQUALS, partyItem.getString("partyId")));
						conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
						conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
						List<GenericValue> listSalesStatement = null;
						
						listSalesStatement = delegator.findList("SalesStatementHeader", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
						if (listSalesStatement != null) {
							for (GenericValue salesStatementItem : listSalesStatement) {
								List<EntityCondition> conditions2 = new ArrayList<EntityCondition>();
								conditions2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SALES_PL_ACCEPTED"));
								conditions2.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, salesStatementItem.get("fromDate")));
								conditions2.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, salesStatementItem.get("thruDate")));
								List<GenericValue> listSalesPolicy = null;
								listSalesPolicy = delegator.findList("SalesPolicy", EntityCondition.makeCondition(conditions2, EntityOperator.AND), null, null, null, false);
								if (listSalesPolicy != null) {
									//Browse roleType of party and salesPolicy
									List<GenericValue> listSalesPolicyApply = new ArrayList<GenericValue>();
									for (GenericValue policyItem : listSalesPolicy) {
										List<GenericValue> listPartyRole = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyItem.getString("partyId")), null, false);
										List<GenericValue> listPolicyRole = delegator.findByAnd("SalesPolicyRoleTypeApply", UtilMisc.toMap("salesPolicyId", policyItem.getString("salesPolicyId")), null, false);
										List<String> listPartyRoleStr = new ArrayList<String>();
										List<String> listPolicyRoleStr = new ArrayList<String>();
										for (GenericValue partyRoleItem2 : listPartyRole) {
											listPartyRoleStr.add(partyRoleItem2.getString("roleTypeId"));
										}
										for (GenericValue policyRoleItem2 : listPolicyRole) {
											listPolicyRoleStr.add(policyRoleItem2.getString("roleTypeId"));
										}
										if (listPolicyRoleStr != null) {
											for (String policyRoleItem : listPolicyRoleStr) {
												if (listPartyRoleStr.contains(policyRoleItem)) {
													listSalesPolicyApply.add(policyItem);
													break;
												}
											}
										}
									}
									//TODOCHANGE
									if (listSalesPolicyApply.size() > 0) {
										SalesCommissionEntity commissionItem = new SalesCommissionEntity();
										boolean isPass = false;
										try {
											isPass = runSalesPolicies(delegator, commissionItem, salesStatementItem, listSalesPolicyApply, fromDate, thruDate, nowTimestamp, true);
										} catch (GeneralException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										if (commissionItem != null && isPass) {
											listSalesCommission.add(commissionItem);
										}
									}
								}
							}
						}
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return listSalesCommission;
    }
    
    public static List<SalesCommissionEntity> getSalesCommissionList2(Delegator delegator, Locale locale, 
    		String roleTypeId, String customTimePeriodId, Timestamp fromDate, Timestamp thruDate) {
		
    	if (UtilValidate.isEmpty(roleTypeId)) return null;
		boolean useCustomTimePeriod = false;
		
		if (UtilValidate.isNotEmpty(customTimePeriodId)) useCustomTimePeriod = true;
		List<String> partyIds = new ArrayList<String>();
		List<String> roleTypeIds = SalesPartyUtil.getListDescendantRoleInclude(roleTypeId, delegator);
		for (String item : roleTypeIds) {
			List<String> tmp = SecurityUtil.getPartiesByRoles(item, delegator);
			if (tmp != null) partyIds.addAll(tmp);
		}
		
		List<SalesCommissionEntity> listSalesCommission = new ArrayList<SalesCommissionEntity>();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isNotEmpty(partyIds)) {
			if (!useCustomTimePeriod) {
				try {
					for (String partyId : partyIds) {
						List<GenericValue> listSalesCommissionDataStored = delegator.findList("SalesCommissionData", 
								EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate), EntityOperator.AND), UtilMisc.toSet("salesCommissionId"), null, null, false);
						if (UtilValidate.isNotEmpty(listSalesCommissionDataStored)) continue;
						
						// Sales Statement Header Entity
						List<EntityCondition> statementConds = FastList.newInstance();
						statementConds.add(EntityCondition.makeCondition("internalPartyId", partyId));
						statementConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
						statementConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
						
						List<GenericValue> listSalesStatement = delegator.findList("SalesStatementHeader", EntityCondition.makeCondition(statementConds, EntityOperator.AND), null, null, null, false);
						if (listSalesStatement != null) {
							for (GenericValue salesStatement : listSalesStatement) {
								// Sales Policy Entity
								List<EntityCondition> policyConds = FastList.newInstance();
								policyConds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SALES_PL_ACCEPTED"));
								policyConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, salesStatement.get("fromDate")));
								policyConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, salesStatement.get("thruDate")));
								List<GenericValue> listSalesPolicy = delegator.findList("SalesPolicy", EntityCondition.makeCondition(policyConds, EntityOperator.AND), null, null, null, false);
								if (listSalesPolicy != null) {
									//check roleTypeId between Party and SalesPolicy
									List<GenericValue> listSalesPolicyApply = new ArrayList<GenericValue>();
									for (GenericValue policy : listSalesPolicy) {
										List<EntityCondition> policyRoleConds = FastList.newInstance();
										policyRoleConds.add(EntityCondition.makeCondition("salesPolicyId", policy.getString("salesPolicyId")));
										policyRoleConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, policy.getTimestamp("fromDate")));
										policyRoleConds.add(EntityCondition.makeCondition(
												EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), 
												EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, policy.getTimestamp("thruDate"))));
										List<String> tmpPolicyRoleIds = EntityUtil.getFieldListFromEntityList(
												EntityUtil.filterByDate(delegator.findList("SalesPolicyRoleTypeApply", EntityCondition.makeCondition(policyRoleConds, EntityOperator.AND), null, null, null, false)), 
												"roleTypeId", true);
										Set<String> policyRoleIds = FastSet.newInstance();
										for (String tmpItem : tmpPolicyRoleIds) {
											List<String> tmpRoles = SalesPartyUtil.getListDescendantRoleInclude(tmpItem, delegator);
											if (tmpRoles != null) policyRoleIds.addAll(tmpRoles);
										}
										
										List<GenericValue> listPartyRole = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId), EntityOperator.AND, EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, policyRoleIds)), null, null, null, false);
										if (UtilValidate.isNotEmpty(listPartyRole)) {
											listSalesPolicyApply.add(policy);
										}
									}
									
									// TODOCHANGE
									
									// run sales policy applied
									if (listSalesPolicyApply.size() > 0) {
										SalesCommissionEntity commissionItem = new SalesCommissionEntity();
										boolean isPass = false;
										try {
											isPass = runSalesPolicies(delegator, commissionItem, salesStatement, listSalesPolicyApply, fromDate, thruDate, nowTimestamp, true);
										} catch (GeneralException e) {
											Debug.logError(e, e.toString(), module);
											return null;
										}
										if (commissionItem != null && isPass) {
											listSalesCommission.add(commissionItem);
										}
									}
								}
							}
						}
					}
				} catch (GenericEntityException e) {
					Debug.logError(e, e.toString(), module);
					return null;
				}
			}
		}
		return listSalesCommission;
    }
    
    protected static boolean runSalesPolicies(Delegator delegator, SalesCommissionEntity salesCommissionData, GenericValue salesStatement, List<GenericValue> salesPolicyList, Timestamp fromDate, Timestamp thruDate, Timestamp nowTimestamp, boolean isolatedTestRun) throws GeneralException {
        // this is our safety net; we should never need to loop through the rules more than a certain number of times, this is that number and may have to be changed for insanely large promo sets...
        long maxIterations = 1000;
        // part of the safety net to avoid infinite iteration
        long numberOfIterations = 0;

        // set a max limit on how many times each promo can be run, for cases where there is no use limit this will be the use limit
        //default to 2 times the number of items in the cart
        //long maxUseLimit = cart.getTotalQuantity().multiply(BigDecimal.valueOf(2)).setScale(0, BigDecimal.ROUND_CEILING).longValue();
        
        List<SalesCommissionAdjustmentEntity> salesCommissionAdjustmentEntity = new ArrayList<SalesCommissionAdjustmentEntity>();
        try {
            // repeat until no more rules to run: either all rules are run, or no changes to the cart in a loop
            boolean cartChanged = true;
            while (cartChanged) {
                cartChanged = false;
                numberOfIterations++;
                if (numberOfIterations > maxIterations) {
                    Debug.logError("ERROR: While calculating sales policies the sales policy rules where run more than " + maxIterations + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.", module);
                    break;
                }

                for (GenericValue salesPolicy : salesPolicyList) {
                    String salesPolicyId = salesPolicy.getString("salesPolicyId");

                    List<GenericValue> salesPolicyRules = salesPolicy.getRelated("SalesPolicyRule", null, null, true);
                    if (UtilValidate.isNotEmpty(salesPolicyRules)) {
                        // always have a useLimit to avoid unlimited looping, default to 1 if no other is specified
                        /*Long candidateUseLimit = getSalesPolicyUseLimit(salesPolicy, partyId, delegator);*/
                        /*Long candidateUseLimit = null;*/
                        /*Long useLimit = candidateUseLimit;*/
                        if (Debug.verboseOn()) Debug.logVerbose("Running sales policy [" + salesPolicyId + "], useLimit=" + null + ", # of rules=" + salesPolicyRules.size(), module);

                        /*boolean requireCode = "Y".equals(salesPolicy.getString("requireCode"));*/
                        
                        // check if promo code required
                        try {
                        	List<SalesCommissionAdjustmentEntity> listSalesCommissionAdj = runSalesPolicyRules(salesStatement, cartChanged, salesPolicy, salesPolicyRules, delegator, nowTimestamp);
                        	if (UtilValidate.isNotEmpty(listSalesCommissionAdj)) {
                        		salesCommissionAdjustmentEntity.addAll(listSalesCommissionAdj);
                        		cartChanged = true;
                        	}
                        } catch (RuntimeException e) {
                            throw new GeneralException("Error running sales policy with ID [" + salesPolicyId + "]", e);
                        }
                    }

                    // if this is an isolatedTestRun clear out adjustments and cart item promo use info
                    /*if (isolatedTestRun) {
                        cart.clearAllPromotionAdjustments();
                        cart.clearCartItemUseInPromoInfo();
                    }*/
                }

                // if this is an isolatedTestRun, then only go through it once, never retry
                if (isolatedTestRun) {
                    cartChanged = false;
                }
            }
        } catch (UseLimitException e) {
            Debug.logError(e, e.toString(), module);
        }
        if (UtilValidate.isNotEmpty(salesCommissionAdjustmentEntity)) {
        	BigDecimal totalAmount = BigDecimal.ZERO;
        	boolean hasQuantity = false;
        	for (SalesCommissionAdjustmentEntity item : salesCommissionAdjustmentEntity) {
        		if (item.getAmount() != null) {
        			totalAmount = totalAmount.add(item.getAmount());
        		}
        		if (item.getQuantity() != null && BigDecimal.ZERO.compareTo(item.getQuantity()) < 0) {
        			hasQuantity = true;
        		}
        	}
        	salesCommissionData.setSalesStatementId(salesStatement.getString("salesId"));
        	salesCommissionData.setPartyId(salesStatement.getString("internalPartyId"));
        	salesCommissionData.setFromDate(fromDate);
        	salesCommissionData.setThruDate(thruDate);
        	salesCommissionData.setAmount(totalAmount);
        	salesCommissionData.setHasQuantity(hasQuantity);
        	salesCommissionData.setListSalesCommissionsAdj(salesCommissionAdjustmentEntity);
        	return true;
        }
        return false;
    }
    
    protected static List<SalesCommissionAdjustmentEntity> runSalesPolicyRules(GenericValue salesStatement, boolean cartChanged, GenericValue salesPolicy, List<GenericValue> salesPolicyRules, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException, UseLimitException {
        /*String salesPolicyId = salesPolicy.getString("salesPolicyId");
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        BigDecimal quantityLeftInActions = BigDecimal.ZERO;*/
    	@SuppressWarnings("unused")
		boolean promoUsed = false;
        List<SalesCommissionAdjustmentEntity> returnValue = new ArrayList<SalesCommissionAdjustmentEntity>();

        Iterator<GenericValue> policyRulesIter = salesPolicyRules.iterator();
        while (policyRulesIter != null && policyRulesIter.hasNext()) {
            GenericValue salesPolicyRule = policyRulesIter.next();

            // if apply then performActions when no conditions are false, so default to true
            boolean performActions = true;

            // loop through conditions for rule, if any false, set allConditionsTrue to false
            List<GenericValue> salesPolicyConds = delegator.findByAnd("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicy.get("salesPolicyId"), "salesPolicyRuleId", salesPolicyRule.get("salesPolicyRuleId")), UtilMisc.toList("salesPolicyCondSeqId"), true);
            salesPolicyConds = EntityUtil.filterByAnd(salesPolicyConds, UtilMisc.toMap("salesPolicyRuleId", salesPolicyRule.get("salesPolicyRuleId")));
            // using the other method to consolodate cache entries because the same cache is used elsewhere: List salesPolicyConds = salesPolicyRule.getRelated("SalesPolicyCond", null, UtilMisc.toList("salesPolicyCondSeqId"), true);
            if (Debug.verboseOn()) Debug.logVerbose("Checking " + salesPolicyConds.size() + " conditions for rule " + salesPolicyRule, module);

            Iterator<GenericValue> salesPolicyCondIter = UtilMisc.toIterator(salesPolicyConds);
            while (salesPolicyCondIter != null && salesPolicyCondIter.hasNext()) {
                GenericValue salesPolicyCond = salesPolicyCondIter.next();

                boolean condResult = checkCondition(salesPolicyCond, salesStatement, delegator, nowTimestamp);

                // any false condition will cause it to NOT perform the action
                if (condResult == false) {
                    performActions = false;
                    break;
                }
            }
            
            if (performActions) {
                // perform all actions, either apply or unapply

                List<GenericValue> salesPolicyActions = salesPolicyRule.getRelated("SalesPolicyAction", null, UtilMisc.toList("salesPolicyActionSeqId"), true);
                Iterator<GenericValue> salesPolicyActionIter = UtilMisc.toIterator(salesPolicyActions);
                
                // List<SalesCommissionAdjustmentEntity> listSalesCommissionAdj = new ArrayList<SalesCommissionAdjustmentEntity>();
                while (salesPolicyActionIter != null && salesPolicyActionIter.hasNext()) {
                    GenericValue salesPolicyAction = salesPolicyActionIter.next();
                    try {
                    	SalesCommissionAdjustmentEntity salesCommissionAdjItem = performAction(salesPolicyAction, salesStatement, delegator, nowTimestamp);
                    	boolean actionChangedCart = false;
                    	if (UtilValidate.isNotEmpty(salesCommissionAdjItem)) {
                    		returnValue.add(salesCommissionAdjItem);
                    		actionChangedCart = true;
                    	}
                        //ActionResultInfo actionResultInfo = 
                        //totalDiscountAmount = totalDiscountAmount.add(actionResultInfo.totalDiscountAmount);
                        //quantityLeftInActions = quantityLeftInActions.add(actionResultInfo.quantityLeftInAction);

                        // only set if true, don't set back to false: implements OR logic (ie if ANY actions change content, redo loop)
                        //boolean actionChangedCart = actionResultInfo.ranAction;
                        if (actionChangedCart) {
                            promoUsed = true;
                            cartChanged = true;
                        }
                    } catch (CartItemModifyException e) {
                        Debug.logError(e, "Error modifying the cart while performing sales policy action [" + salesPolicyAction.getPrimaryKey() + "]", module);
                    }
                }
                
            }
        }

        /*if (promoUsed) {
            cart.addProductPromoUse(salesPolicy.getString("salesPolicyId"), productPromoCodeId, totalDiscountAmount, quantityLeftInActions);
        } else {
            // the sales policy was not used, don't try again until we finish a full pass and come back to see the promo conditions are now satisfied based on changes to the cart
            break;
        }*/


        /*if (cart.getProductPromoUseCount(salesPolicyId) > maxUseLimit) {
            throw new UseLimitException("ERROR: While calculating sales policies the sales policy [" + salesPolicyId + "] action was applied more than " + maxUseLimit + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.");
        }*/
        

        return returnValue;
    }
    
    /** returns true if the cart was changed and rules need to be re-evaluted */
    protected static SalesCommissionAdjustmentEntity performAction(GenericValue salesPolicyAction, GenericValue salesStatement, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException, CartItemModifyException {
    	SalesCommissionAdjustmentEntity actionResultInfo = new SalesCommissionAdjustmentEntity();
        performAction(actionResultInfo, salesPolicyAction, salesStatement, delegator, nowTimestamp);
        return actionResultInfo;
    }
    
    protected static boolean checkConditionsForItem(GenericValue salesPolicyActionOrCond, GenericValue salesStatement, GenericValue salesStatementItem, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        GenericValue salesPolicyRule = salesPolicyActionOrCond.getRelatedOne("SalesPolicyRule", true);

        List<GenericValue> salesPolicyConds = delegator.findByAnd("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicyRule.get("salesPolicyId")), UtilMisc.toList("salesPolicyCondSeqId"), true);
        salesPolicyConds = EntityUtil.filterByAnd(salesPolicyConds, UtilMisc.toMap("salesPolicyRuleId", salesPolicyRule.get("salesPolicyRuleId")));
        for (GenericValue salesPolicyCond: salesPolicyConds) {
            boolean passed = checkConditionForItem(salesPolicyCond, salesStatement, salesStatementItem, delegator, nowTimestamp);
            if (!passed) return false;
        }
        return true;
    }
    
    protected static boolean checkConditionForItem(GenericValue salesPolicyCond, GenericValue salesStatement, GenericValue salesStatementItem, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        /*String condValue = salesPolicyCond.getString("condValue");*/
        // String otherValue = salesPolicyCond.getString("otherValue");
        /*String inputParamEnumId = salesPolicyCond.getString("inputParamEnumId");
        String operatorEnumId = salesPolicyCond.getString("operatorEnumId");*/

        // don't get list price from cart because it may have tax included whereas the base price does not: BigDecimal listPrice = cartItem.getListPrice();
        // Map<String, String> priceFindMap = UtilMisc.toMap("productId", salesStatementItem.getString("productId"),
        //        "productPriceTypeId", "LIST_PRICE", "productPricePurposeId", "PURCHASE");
        //List<GenericValue> listProductPriceList = delegator.findByAnd("ProductPrice", priceFindMap, UtilMisc.toList("-fromDate"), false);
        //listProductPriceList = EntityUtil.filterByDate(listProductPriceList, true);
        //GenericValue listProductPrice = (listProductPriceList != null && listProductPriceList.size() > 0) ? listProductPriceList.get(0): null;
        //BigDecimal listPrice = (listProductPrice != null) ? listProductPrice.getBigDecimal("price") : null;

        /*if (listPrice == null) {
            // can't find a list price so this condition is meaningless, consider it passed
            return true;
        }*/

        //BigDecimal basePrice = cartItem.getBasePrice();
        /*BigDecimal amountOff = listPrice.subtract(basePrice);
        BigDecimal percentOff = amountOff.divide(listPrice, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100L));

        Integer compareBase = null;

        if ("PPIP_LPMUP_AMT".equals(inputParamEnumId)) {
            // NOTE: only check this after we know it's this type of cond, otherwise condValue may not be a number
            BigDecimal condValueBigDecimal = new BigDecimal(condValue);
            compareBase = Integer.valueOf(amountOff.compareTo(condValueBigDecimal));
        } else if ("PPIP_LPMUP_PER".equals(inputParamEnumId)) {
            // NOTE: only check this after we know it's this type of cond, otherwise condValue may not be a number
            BigDecimal condValueBigDecimal = new BigDecimal(condValue);
            compareBase = Integer.valueOf(percentOff.compareTo(condValueBigDecimal));
        } else {
            // condition doesn't apply to individual item, always passes
            return true;
        }*/

        //Debug.logInfo("Checking condition for item productId=" + cartItem.getProductId() + ", listPrice=" + listPrice + ", basePrice=" + basePrice + ", amountOff=" + amountOff + ", percentOff=" + percentOff + ", condValue=" + condValue + ", compareBase=" + compareBase + ", salesPolicyCond=" + salesPolicyCond, module);

        /*if (compareBase != null) {
            int compare = compareBase.intValue();
            if ("SPC_EQ".equals(operatorEnumId)) {
                if (compare == 0) return true;
            } else if ("PPC_NEQ".equals(operatorEnumId)) {
                if (compare != 0) return true;
            } else if ("PPC_LT".equals(operatorEnumId)) {
                if (compare < 0) return true;
            } else if ("PPC_LTE".equals(operatorEnumId)) {
                if (compare <= 0) return true;
            } else if ("PPC_GT".equals(operatorEnumId)) {
                if (compare > 0) return true;
            } else if ("PPC_GTE".equals(operatorEnumId)) {
                if (compare >= 0) return true;
            } else {
                Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderAnUnSupportedProductPromoCondCondition", UtilMisc.toMap("operatorEnumId",operatorEnumId) , cart.getLocale()), module);
                return false;
            }
            // was a compareBase and nothing returned above, so condition didn't pass, return false
            return false;
        }*/

        // no compareBase, this condition doesn't apply;
        return true;
    }
    
    protected static boolean checkCondition(GenericValue salesPolicyCond, GenericValue salesStatement, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        String condValue = salesPolicyCond.getString("condValue");
        String otherValue = salesPolicyCond.getString("otherValue");
        String inputParamEnumId = salesPolicyCond.getString("inputParamEnumId");
        String operatorEnumId = salesPolicyCond.getString("operatorEnumId");
        Locale locale = Locale.ENGLISH;
        /*String shippingMethod = "";
        String carrierPartyId = "";*/
        if (otherValue != null && otherValue.contains("@")) {
            /*carrierPartyId = otherValue.substring(0, otherValue.indexOf("@"));
            shippingMethod = otherValue.substring(otherValue.indexOf("@")+1);*/
            otherValue = "";
        }
        String partyId = salesStatement.getString("internalPartyId");
        /*GenericValue userLogin = cart.getUserLogin();
        if (userLogin == null) {
            userLogin = cart.getAutoUserLogin();
        }*/

        if (Debug.verboseOn()) Debug.logVerbose("Checking sales policy condition: " + salesPolicyCond, module);
        Integer compareBase = null;
        
        /*
        if ("PPIP_SERVICE".equals(inputParamEnumId)) {
        } else */
        if ("SPIP_PC_TURN_OVER".equals(inputParamEnumId)) {
        	// X% Turn over, vuot doanh so bao nhieu phan tram
            BigDecimal percentTarget = BigDecimal.ZERO; // phan tram theo ke hoach
            BigDecimal percentActual = BigDecimal.ZERO; // phan tram thuc te dat duoc
            if (UtilValidate.isNotEmpty(condValue)) {
                percentTarget = new BigDecimal(condValue);
                //percentValue = new BigDecimal(condValue);
            }
            if (percentTarget.compareTo(BigDecimal.ZERO) > 0) {
            	/*Set<String> productIds = SalesPolicyWorker.getPromoRuleCondProductIds(salesPolicyCond, delegator, nowTimestamp);
                List<GenericValue> listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
                if (listSalesStatementItem != null) {
                	Iterator<GenericValue> listSalesStatementIter = listSalesStatementItem.iterator();
                    while (listSalesStatementIter.hasNext()) {
                    	GenericValue salesStatementItem = listSalesStatementIter.next();
                    	String productId = salesStatementItem.getString("productId");
                        boolean passedItemConds = checkConditionsForItem(salesPolicyCond, salesStatement, salesStatementItem, delegator, nowTimestamp); // always is TRUE
                        if (passedItemConds && UtilValidate.isNotEmpty(productId) && productIds.contains(productId)) {
                        	BigDecimal amount = salesStatementItem.getBigDecimal("amount");
                        	BigDecimal amountActual = salesStatementItem.getBigDecimal("amountActual");
                        	if (UtilValidate.isEmpty(amount)) {
                        		amount = BigDecimal.ONE;
                        		amount = salesStatement.getBigDecimal("quantity").multiply(salesStatement.getBigDecimal("unitPrice"));
                        	}
                        	percentActual = amountActual.divide(amount, generalRounding).multiply(BigDecimal.valueOf(100));
                        }
                    }
                }*/
            	BigDecimal percentTotal = BigDecimal.ZERO;
            	int count = 0;
            	List<GenericValue> listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
                if (listSalesStatementItem != null) {
                	Iterator<GenericValue> listSalesStatementIter = listSalesStatementItem.iterator();
                    while (listSalesStatementIter.hasNext()) {
                    	GenericValue salesStatementTmp = listSalesStatementIter.next();
                    	BigDecimal quantityTarget = salesStatementTmp.getBigDecimal("quantity");
                    	BigDecimal quantityActual = salesStatementTmp.getBigDecimal("quantityActual");
                    	/*if (quantityTarget == null || (quantityActual != null && BigDecimal.ZERO.compareTo(quantityActual) < 0)) {
                    		quantityTarget = BigDecimal.ZERO;
                    	}*/
                    	if (quantityTarget != null && quantityActual != null && BigDecimal.ZERO.compareTo(quantityTarget) < 0) {
                    		percentTotal = percentTotal.add(quantityActual.multiply(new BigDecimal(100)).divide(quantityTarget, generalRounding));
                    		count++;
                    	}
                    }
                }
                if (count > 0) {
                	percentActual = percentTotal.divide(new BigDecimal(count), generalRounding);
                }
            }
            compareBase = percentActual.compareTo(percentTarget);
        } else if ("SPIP_PRODUCT_AMOUNT".equals(inputParamEnumId)) {
            // for this type of sales policy force the operatorEnumId = SPC_EQ, effectively ignore that setting because the comparison is implied in the code
            operatorEnumId = "SPC_EQ";

            // this type of condition requires items involved to not be involved in any other quantity consuming cond/action, and does not pro-rate the price, just uses the base price
            BigDecimal amountNeeded = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(condValue)) {
                amountNeeded = new BigDecimal(condValue);
            }

            // Debug.logInfo("Doing Amount Cond with Value: " + amountNeeded, module);
            Set<String> productIds = SalesPolicyWorker.getPromoRuleCondProductIds(salesPolicyCond, delegator, nowTimestamp);
            
            /*List<ShoppingCartItem> lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator<ShoppingCartItem> lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();*/
            List<GenericValue> listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
            Iterator<GenericValue> listSalesStatementIter = listSalesStatementItem.iterator();
            while (amountNeeded.compareTo(BigDecimal.ZERO) > 0 && listSalesStatementIter.hasNext()) {
                /*ShoppingCartItem cartItem = lineOrderedByBasePriceIter.next();*/
            	GenericValue salesStatementItem = listSalesStatementIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
            	String productId = "";
            	if (salesStatementItem.getString("productId") != null) {
            		productId = salesStatementItem.getString("productId");
            		
            	}
            	/*String productId = ;
                GenericValue product = cartItem.getProduct();
                String parentProductId = cartItem.getParentProductId();*/
                boolean passedItemConds = checkConditionsForItem(salesPolicyCond, salesStatement, salesStatementItem, delegator, nowTimestamp);
                if (passedItemConds && !"".equals(productId) && productIds.contains(productId)) {
                	amountNeeded = amountNeeded.subtract(salesStatementItem.getBigDecimal("amountActual"));
                }
                /*if (passedItemConds && !cartItem.getIsPromo() &&
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) &&
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {

                    BigDecimal basePrice = cartItem.getBasePrice();
                    // get a rough price, round it up to an integer
                    BigDecimal quantityNeeded = amountNeeded.divide(basePrice, generalRounding).setScale(0, BigDecimal.ROUND_CEILING);

                    // reduce amount still needed to qualify for promo (amountNeeded)
                    BigDecimal quantity = cartItem.addPromoQuantityCandidateUse(quantityNeeded, salesPolicyCond, false);
                    // get pro-rated amount based on discount
                    amountNeeded = amountNeeded.subtract(quantity.multiply(basePrice));
                }*/
            }

            // Debug.logInfo("Doing Amount Cond with Value after finding applicable cart lines: " + amountNeeded, module);

            // if amountNeeded > 0 then the promo condition failed, so remove candidate promo uses and increment the promoQuantityUsed to restore it
            if (amountNeeded.compareTo(BigDecimal.ZERO) > 0) {
                // failed, reset the entire rule, ie including all other conditions that might have been done before
                // cart.resetPromoRuleUse(salesPolicyCond.getString("salesPolicyId"), salesPolicyCond.getString("salesPolicyRuleId"));
                compareBase = Integer.valueOf(-1);
            } else {
                // we got it, the conditions are in place...
                compareBase = Integer.valueOf(0);
                // NOTE: don't confirm promo rule use here, wait until actions are complete for the rule to do that
            }
        } else if ("SPIP_PRODUCT_TOTAL".equals(inputParamEnumId)) {
            // this type of condition allows items involved to be involved in other quantity consuming cond/action, and does pro-rate the price
            if (UtilValidate.isNotEmpty(condValue)) {
                BigDecimal amountNeeded = new BigDecimal(condValue);
                BigDecimal amountAvailable = BigDecimal.ZERO;

                // Debug.logInfo("Doing Amount Not Counted Cond with Value: " + amountNeeded, module);

                Set<String> productIds = SalesPolicyWorker.getPromoRuleCondProductIds(salesPolicyCond, delegator, nowTimestamp);

                List<GenericValue> listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
                for (GenericValue salesStatementItem : listSalesStatementItem) {
                    // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                    String productId = salesStatementItem.getString("productId");
                    boolean passedItemConds = checkConditionsForItem(salesPolicyCond, salesStatement, salesStatementItem, delegator, nowTimestamp);
                    if (passedItemConds && !"".equals(productId) && productIds.contains(productId)) {
                        // just count the entire sub-total of the item
                        amountAvailable = amountAvailable.add(salesStatementItem.getBigDecimal("amountActual"));
                    }
                }

                // Debug.logInfo("Doing Amount Not Counted Cond with Value after finding applicable cart lines: " + amountNeeded, module);
                compareBase = Integer.valueOf(amountAvailable.compareTo(amountNeeded));
            }
        } else if ("SPIP_PRODUCT_QUANT".equals(inputParamEnumId)) {
            // for this type of promo force the operatorEnumId = SPC_EQ, effectively ignore that setting because the comparison is implied in the code
            operatorEnumId = "SPC_EQ";

            BigDecimal quantityNeeded = BigDecimal.ONE;
            if (UtilValidate.isNotEmpty(condValue)) {
                quantityNeeded = new BigDecimal(condValue);
            }

            Set<String> productIds = SalesPolicyWorker.getPromoRuleCondProductIds(salesPolicyCond, delegator, nowTimestamp);

            List<GenericValue> listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
            Iterator<GenericValue> listSalesStatementIter = listSalesStatementItem.iterator();
            while (quantityNeeded.compareTo(BigDecimal.ZERO) > 0 && listSalesStatementIter.hasNext()) {
                GenericValue salesStatementItem = listSalesStatementIter.next();
                
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String productId = salesStatementItem.getString("productId");
                boolean passedItemConds = checkConditionsForItem(salesPolicyCond, salesStatement, salesStatementItem, delegator, nowTimestamp);
                if (passedItemConds && productId != null && productIds.contains(productId)) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    quantityNeeded = quantityNeeded.subtract(salesStatementItem.getBigDecimal("quantityActual"));
                }
            }

            // if quantityNeeded > 0 then the promo condition failed, so remove candidate promo uses and increment the promoQuantityUsed to restore it
            if (quantityNeeded.compareTo(BigDecimal.ZERO) > 0) {
                // failed, reset the entire rule, ie including all other conditions that might have been done before
                // cart.resetPromoRuleUse(salesPolicyCond.getString("salesPolicyId"), salesPolicyCond.getString("salesPolicyRuleId"));
                compareBase = Integer.valueOf(-1);
            } else {
                // we got it, the conditions are in place...
                compareBase = Integer.valueOf(0);
                // NOTE: don't confirm rpomo rule use here, wait until actions are complete for the rule to do that
            }
        } else if ("SPIP_NEW_ACCT".equals(inputParamEnumId)) {
        	return false;
            /*if (UtilValidate.isNotEmpty(condValue)) {
                BigDecimal acctDays = cart.getPartyDaysSinceCreated(nowTimestamp);
                if (acctDays == null) {
                    // condition always fails if we don't know how many days since account created
                    return false;
                }
                compareBase = acctDays.compareTo(new BigDecimal(condValue));
            }*/
        } else if ("SPIP_PARTY_ID".equals(inputParamEnumId)) {
            if (partyId != null && UtilValidate.isNotEmpty(condValue)) {
                compareBase = Integer.valueOf(partyId.compareTo(condValue));
            } else {
                compareBase = Integer.valueOf(1);
            }
        } else if ("SPIP_PARTY_GRP_MEM".equals(inputParamEnumId)) {
            if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(condValue)) {
                compareBase = Integer.valueOf(1);
            } else {
                String groupPartyId = condValue;
                if (partyId.equals(groupPartyId)) {
                    compareBase = Integer.valueOf(0);
                } else {
                    // look for PartyRelationship with partyRelationshipTypeId=GROUP_ROLLUP, the partyIdTo is the group member, so the partyIdFrom is the groupPartyId
                    List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", groupPartyId, "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
                    // and from/thru date within range
                    partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, true);

                    if (UtilValidate.isNotEmpty(partyRelationshipList)) {
                        compareBase = Integer.valueOf(0);
                    } else {
                        compareBase = Integer.valueOf(checkConditionPartyHierarchy(delegator, nowTimestamp, groupPartyId, partyId));
                    }
                }
            }
        /*} else if ("PPIP_PARTY_CLASS".equals(inputParamEnumId)) {
        } else if ("PPIP_ROLE_TYPE".equals(inputParamEnumId)) {
        } else if ("PPIP_ORDER_TOTAL".equals(inputParamEnumId)) {
        } else if ("PPIP_ORST_HIST".equals(inputParamEnumId)) {
        } else if ("PPIP_ORST_YEAR".equals(inputParamEnumId)) {
        } else if ("PPIP_ORST_LAST_YEAR".equals(inputParamEnumId)) {
        } else if ("PPIP_RECURRENCE".equals(inputParamEnumId)) {
        } else if ("PPIP_ORDER_SHIPTOTAL".equals(inputParamEnumId) && shippingMethod.equals(cart.getShipmentMethodTypeId()) && carrierPartyId.equals(cart.getCarrierPartyId())) {
        } else if ("PPIP_LPMUP_AMT".equals(inputParamEnumId)) {
        } else if ("PPIP_LPMUP_PER".equals(inputParamEnumId)) {
        */
        } else {
            Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderAnUnSupportedProductPromoCondInputParameterLhs", UtilMisc.toMap("inputParamEnumId",salesPolicyCond.getString("inputParamEnumId")), locale), module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Condition compare done, compareBase=" + compareBase, module);

        if (compareBase != null) {
            int compare = compareBase.intValue();
            if ("SPC_EQ".equals(operatorEnumId)) {
                if (compare == 0) return true;
            } else if ("SPC_NEQ".equals(operatorEnumId)) {
                if (compare != 0) return true;
            } else if ("SPC_LT".equals(operatorEnumId)) {
                if (compare < 0) return true;
            } else if ("SPC_LTE".equals(operatorEnumId)) {
                if (compare <= 0) return true;
            } else if ("SPC_GT".equals(operatorEnumId)) {
                if (compare > 0) return true;
            } else if ("SPC_GTE".equals(operatorEnumId)) {
                if (compare >= 0) return true;
            } else {
                Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderAnUnSupportedProductPromoCondCondition", UtilMisc.toMap("operatorEnumId",operatorEnumId) , locale), module);
                return false;
            }
        }
        // default to not meeting the condition
        return false;
    }
    
    public static void performAction(SalesCommissionAdjustmentEntity actionResultInfo, GenericValue salesPolicyAction, GenericValue salesStatement, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException, CartItemModifyException {
        String productPromoActionEnumId = salesPolicyAction.getString("salesPolicyActionEnumId");

        //PROMO_ORDER_AMOUNT
        if ("POLICY_AWD".equals(productPromoActionEnumId)) {
            BigDecimal amount = salesPolicyAction.get("amount") == null ? BigDecimal.ZERO : salesPolicyAction.getBigDecimal("amount");
            // if amount is greater than the order sub total, set equal to order sub total, this normally wouldn't happen because there should be a condition that the order total be above a certain amount, but just in case...
            // BigDecimal subTotal = cart.getSubTotalForPromotions();
            /*if (amount.negate().compareTo(subTotal) > 0) {
                amount = subTotal.negate();
            }*/
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                // doOrderPromoAction(salesPolicyAction, cart, amount, "amount", delegator);
                actionResultInfo.setAmount(amount);
                actionResultInfo.setSalesPolicyId(salesPolicyAction.getString("salesPolicyId"));
                actionResultInfo.setSalesPolicyRuleId(salesPolicyAction.getString("salesPolicyRuleId"));
                actionResultInfo.setSalesPolicyActionSeqId(salesPolicyAction.getString("salesPolicyActionSeqId"));
            }
        } else {
            Debug.logError("An un-supported productPromoActionType was used: " + productPromoActionEnumId + ", not performing any action", module);
            //actionResultInfo.ranAction = false;
        }

        /*if (actionResultInfo.ranAction) {
            // in action, if doesn't have enough quantity to use the promo at all, remove candidate promo uses and increment promoQuantityUsed; this should go for all actions, if any action runs we confirm
            cart.confirmPromoRuleUse(salesPolicyAction.getString("salesPolicyId"), salesPolicyAction.getString("salesPolicyRuleId"));
        } else {
            cart.resetPromoRuleUse(salesPolicyAction.getString("salesPolicyId"), salesPolicyAction.getString("salesPolicyRuleId"));
        }*/
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

    public static Set<String> getPromoRuleCondProductIds(GenericValue salesPolicyCond, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        // get a cached list for the whole promo and filter it as needed, this for better efficiency in caching
        List<GenericValue> salesPolicyCategoriesAll = delegator.findByAnd("SalesPolicyCategory", UtilMisc.toMap("salesPolicyId", salesPolicyCond.get("salesPolicyId")), null, true);
        List<GenericValue> salesPolicyCategories = EntityUtil.filterByAnd(salesPolicyCategoriesAll, UtilMisc.toMap("salesPolicyRuleId", "_NA_", "salesPolicyCondSeqId", "_NA_"));
        salesPolicyCategories.addAll(EntityUtil.filterByAnd(salesPolicyCategoriesAll, UtilMisc.toMap("salesPolicyRuleId", salesPolicyCond.get("salesPolicyRuleId"), "salesPolicyCondSeqId", salesPolicyCond.get("salesPolicyCondSeqId"))));

        List<GenericValue> salesPolicyProductsAll = delegator.findByAnd("SalesPolicyProduct", UtilMisc.toMap("salesPolicyId", salesPolicyCond.get("salesPolicyId")), null, true);
        List<GenericValue> salesPolicyProducts = EntityUtil.filterByAnd(salesPolicyProductsAll, UtilMisc.toMap("salesPolicyRuleId", "_NA_", "salesPolicyCondSeqId", "_NA_"));
        salesPolicyProducts.addAll(EntityUtil.filterByAnd(salesPolicyProductsAll, UtilMisc.toMap("salesPolicyRuleId", salesPolicyCond.get("salesPolicyRuleId"), "salesPolicyCondSeqId", salesPolicyCond.get("salesPolicyCondSeqId"))));

        Set<String> productIds = FastSet.newInstance();
        makeProductPromoIdSet(productIds, salesPolicyCategories, salesPolicyProducts, delegator, nowTimestamp, false);
        return productIds;
    }

    public static void makeProductPromoIdSet(Set<String> productIds, List<GenericValue> salesPolicyCategories, List<GenericValue> salesPolicyProducts, Delegator delegator, Timestamp nowTimestamp, boolean filterOldProducts) throws GenericEntityException {
        // do the includes
        handleProductPromoCategories(productIds, salesPolicyCategories, "SPPA_INCLUDE", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, salesPolicyProducts, "SPPA_INCLUDE");

        // do the excludes
        handleProductPromoCategories(productIds, salesPolicyCategories, "SPPA_EXCLUDE", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, salesPolicyProducts, "SPPA_EXCLUDE");

        // do the always includes
        handleProductPromoCategories(productIds, salesPolicyCategories, "SPPA_ALWAYS", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, salesPolicyProducts, "SPPA_ALWAYS");
    }

    protected static void handleProductPromoCategories(Set<String> productIds, List<GenericValue> salesPolicyCategories, String salesPolicyApplEnumId, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        boolean include = !"SPPA_EXCLUDE".equals(salesPolicyApplEnumId);
        Set<String> productCategoryIds = FastSet.newInstance();
        Map<String, List<Set<String>>> productCategoryGroupSetListMap = FastMap.newInstance();

        for (GenericValue salesPolicyCategory : salesPolicyCategories) {
            if (salesPolicyApplEnumId.equals(salesPolicyCategory.getString("salesPolicyApplEnumId"))) {
                Set<String> tempCatIdSet = FastSet.newInstance();
                if ("Y".equals(salesPolicyCategory.getString("includeSubCategories"))) {
                    ProductSearch.getAllSubCategoryIds(salesPolicyCategory.getString("productCategoryId"), tempCatIdSet, delegator, nowTimestamp);
                } else {
                    tempCatIdSet.add(salesPolicyCategory.getString("productCategoryId"));
                }

                String andGroupId = salesPolicyCategory.getString("andGroupId");
                if ("_NA_".equals(andGroupId)) {
                    productCategoryIds.addAll(tempCatIdSet);
                } else {
                    List<Set<String>> catIdSetList = productCategoryGroupSetListMap.get(andGroupId);
                    if (catIdSetList == null) {
                        catIdSetList = FastList.newInstance();
                    }
                    catIdSetList.add(tempCatIdSet);
                }
            }
        }

        // for the ones with andGroupIds, if there is only one category move it to the productCategoryIds Set
        // also remove all empty SetLists and Sets
        Iterator<Map.Entry<String, List<Set<String>>>> pcgslmeIter = productCategoryGroupSetListMap.entrySet().iterator();
        while (pcgslmeIter.hasNext()) {
            Map.Entry<String, List<Set<String>>> entry = pcgslmeIter.next();
            List<Set<String>> catIdSetList = entry.getValue();
            if (catIdSetList.size() == 0) {
                pcgslmeIter.remove();
            } else if (catIdSetList.size() == 1) {
                Set<String> catIdSet = catIdSetList.iterator().next();
                if (catIdSet.size() == 0) {
                    pcgslmeIter.remove();
                } else {
                    // if there is only one set in the list since the set will be or'ed anyway, just add them all to the productCategoryIds Set
                    productCategoryIds.addAll(catIdSet);
                    pcgslmeIter.remove();
                }
            }
        }

        // now that the category Set and Map are setup, take care of the productCategoryIds Set first
        getAllProductIds(productCategoryIds, productIds, delegator, nowTimestamp, include);

        // now handle the productCategoryGroupSetListMap
        // if a set has more than one category (because of an include sub-cats) then do an or
        // all lists will have more than category because of the pre-pass that was done, so and them together
        for (Map.Entry<String, List<Set<String>>> entry : productCategoryGroupSetListMap.entrySet()) {
            List<Set<String>> catIdSetList = entry.getValue();
            // get all productIds for this catIdSetList
            List<Set<String>> productIdSetList = FastList.newInstance();

            for (Set<String> catIdSet : catIdSetList) {
                // make a Set of productIds including all ids from all categories
                Set<String> groupProductIdSet = FastSet.newInstance();
                getAllProductIds(catIdSet, groupProductIdSet, delegator, nowTimestamp, true);
                productIdSetList.add(groupProductIdSet);
            }

            // now go through all productId sets and only include IDs that are in all sets
            // by definition if each id must be in all categories, then it must be in the first, so go through the first and drop each one that is not in all others
            Set<String> firstProductIdSet = productIdSetList.remove(0);
            for (Set<String> productIdSet : productIdSetList) {
                firstProductIdSet.retainAll(productIdSet);
            }

            /* the old way of doing it, not as efficient, recoded above using the retainAll operation, pretty handy
            Iterator firstProductIdIter = firstProductIdSet.iterator();
            while (firstProductIdIter.hasNext()) {
                String curProductId = (String) firstProductIdIter.next();

                boolean allContainProductId = true;
                Iterator productIdSetIter = productIdSetList.iterator();
                while (productIdSetIter.hasNext()) {
                    Set productIdSet = (Set) productIdSetIter.next();
                    if (!productIdSet.contains(curProductId)) {
                        allContainProductId = false;
                        break;
                    }
                }

                if (!allContainProductId) {
                    firstProductIdIter.remove();
                }
            }
             */

            if (firstProductIdSet.size() >= 0) {
                if (include) {
                    productIds.addAll(firstProductIdSet);
                } else {
                    productIds.removeAll(firstProductIdSet);
                }
            }
        }
    }

    protected static void getAllProductIds(Set<String> productCategoryIdSet, Set<String> productIdSet, Delegator delegator, Timestamp nowTimestamp, boolean include) throws GenericEntityException {
        for (String productCategoryId : productCategoryIdSet) {
            // get all product category memebers, filter by date
            List<GenericValue> productCategoryMembers = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId), null, true);
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp);
            for (GenericValue productCategoryMember : productCategoryMembers) {
                String productId = productCategoryMember.getString("productId");
                if (include) {
                    productIdSet.add(productId);
                } else {
                    productIdSet.remove(productId);
                }
            }
        }
    }
    
    protected static void handleProductPromoProducts(Set<String> productIds, List<GenericValue> salesPolicyProducts, String salesPolicyApplEnumId) throws GenericEntityException {
        boolean include = !"SPPA_EXCLUDE".equals(salesPolicyApplEnumId);
        for (GenericValue salesPolicyProduct : salesPolicyProducts) {
            if (salesPolicyApplEnumId.equals(salesPolicyProduct.getString("salesPolicyApplEnumId"))) {
                String productId = salesPolicyProduct.getString("productId");
                if (include) {
                    productIds.add(productId);
                } else {
                    productIds.remove(productId);
                }
            }
        }
    }

    @SuppressWarnings("serial")
    protected static class UseLimitException extends Exception {
        public UseLimitException(String str) {
            super(str);
        }
    }
}
