package com.olbius.acc.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AccountingReport {

	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> getJQDeliveryPOSReport(DispatchContext dpct,Map<String,Object> context) {
	 	Delegator delegator = dpct.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, 
    				"OrderPosDeliveryDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log("error call services getJQDeliveryPOSReport" + e.getMessage());
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	 }
	
	public static Map<String,Object> createGlAccountHistoryParty(DispatchContext dpct, Map<String,Object> context) throws GenericEntityException {
	 	Delegator delegator = dpct.getDelegator();
	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	 	Map<String, Object> prevPeriodMap = FastMap.newInstance();
	 	Map<String, Object> glAccountMap = FastMap.newInstance();
	 	
	 	List<EntityCondition> conds = FastList.newInstance();
	 	conds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
	 	conds.add(EntityCondition.makeCondition("flag", null));
    	List<GenericValue> acctgPartyList = delegator.findList("AcctgCustomTimePartySumFact", 
    			EntityCondition.makeCondition(conds), null, UtilMisc.toList("customTimePeriodId"), null, false);
    	for (GenericValue item : acctgPartyList) {
    		String customTimePeriodId = item.getString("customTimePeriodId");
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if (UtilValidate.isNotEmpty(customTimePeriod) && "Y".equals(customTimePeriod.getString("isClosed")) 
					&& "FISCAL_MONTH".equals(customTimePeriod.getString("periodTypeId"))) {
				String glAccountId = item.getString("glAccountId");
	    		String partyId = item.getString("partyId");
	    		BigDecimal crAmount = item.getBigDecimal("crAmount");
	    		BigDecimal drAmount = item.getBigDecimal("drAmount");
	    		
	    		GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
	    		if (!glAccountMap.containsKey(glAccountId)) {
	    			glAccountMap.put(glAccountId, UtilAccounting.isDebitAccount(glAccount));
	    		}
	    		boolean isDebit = (boolean) glAccountMap.get(glAccountId); 
	    		
				if (!prevPeriodMap.containsKey(customTimePeriodId)) {
					Date fromDate = customTimePeriod.getDate("fromDate");
        			Calendar cal = Calendar.getInstance();
        			cal.setTime(fromDate);
        			cal.add(Calendar.DATE, -1);
        			Date fromDateBefore1Day = cal.getTime();
        			
        			conds.clear();
        			conds.add(EntityCondition.makeCondition("isClosed", "Y"));
        			conds.add(EntityCondition.makeCondition("periodTypeId", "FISCAL_MONTH"));
        			conds.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(fromDateBefore1Day.getTime())));
        			conds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDateBefore1Day.getTime())));
        			List<GenericValue> prevCustomTimePeriod = delegator.findList("CustomTimePeriod", 
        					EntityCondition.makeCondition(conds), null, null, null, false);
        			if (UtilValidate.isNotEmpty(prevCustomTimePeriod)) {
        				prevPeriodMap.put(customTimePeriodId, prevCustomTimePeriod.get(0).getString("customTimePeriodId"));
        			} else {
        				prevPeriodMap.put(customTimePeriodId, null);
        			}
				}
				String prevCustomTimePeridId = (String) prevPeriodMap.get(customTimePeriodId);
				
				BigDecimal openingBalance = BigDecimal.ZERO;
				BigDecimal endingBalance = drAmount.subtract(crAmount);
				BigDecimal openingCrBalance = BigDecimal.ZERO;
				BigDecimal openingDrBalance = BigDecimal.ZERO;
				BigDecimal endingCrBalance = BigDecimal.ZERO;
				BigDecimal endingDrBalance = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(prevCustomTimePeridId)) {
					GenericValue prevGlAccountHistoryParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("glAccountId", glAccountId, 
							"partyId", partyId, "organizationPartyId", organizationPartyId, "customTimePeriodId", prevCustomTimePeridId), false);
					if (UtilValidate.isNotEmpty(prevGlAccountHistoryParty)) {
						openingBalance = prevGlAccountHistoryParty.getBigDecimal("endingBalance");
						if (isDebit) {
							openingDrBalance = prevGlAccountHistoryParty.getBigDecimal("endingDrBalance");
						} else {
							openingCrBalance = prevGlAccountHistoryParty.getBigDecimal("endingCrBalance");
						}
					}
				}
				if (isDebit) {
					endingDrBalance = endingBalance.add(openingBalance);
				} else {
					endingCrBalance = endingBalance.add(openingBalance);
				}
				
				//create or store gl_account_history_party
				GenericValue glAccountHistoryParty = delegator.makeValue("GlAccountHistoryParty");
				glAccountHistoryParty.set("glAccountId", glAccountId);
				glAccountHistoryParty.set("partyId", partyId);
				glAccountHistoryParty.set("organizationPartyId", organizationPartyId);
				glAccountHistoryParty.set("customTimePeriodId", customTimePeriodId);
				glAccountHistoryParty.set("openingBalance", openingBalance);
				glAccountHistoryParty.set("postedDebits", drAmount);
				glAccountHistoryParty.set("postedCredits", crAmount);
				glAccountHistoryParty.set("endingBalance", endingBalance);
				glAccountHistoryParty.set("openingCrBalance", openingCrBalance);
				glAccountHistoryParty.set("openingDrBalance", openingDrBalance);
				glAccountHistoryParty.set("endingCrBalance", endingCrBalance);
				glAccountHistoryParty.set("endingDrBalance", endingDrBalance);
				
				try {
					delegator.createOrStore(glAccountHistoryParty);
				} catch (GenericEntityException e) {
					e.printStackTrace();
					return ServiceUtil.returnError(e.getLocalizedMessage());
				}
				
				//mark visited in AcctgCustomTimePartySumFact
				item.set("flag", "Y");
				item.store();
    		}
    	}
    	
    	createGlAccountHistoryPartyQuarterOrYear(delegator, userLogin);
    	
    	return ServiceUtil.returnSuccess();
	 }
	
	public static void createGlAccountHistoryPartyQuarterOrYear(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
	 	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	 	Map<String, Object> beginPeriodMap = FastMap.newInstance();
	 	Map<String, Object> endPeriodMap = FastMap.newInstance();
	 	
	 	List<EntityCondition> conds = FastList.newInstance();
	 	conds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
	 	conds.add(EntityCondition.makeCondition("flag", null));
    	List<GenericValue> acctgPartyList = delegator.findList("AcctgCustomTimePartySumFact", 
    			EntityCondition.makeCondition(conds), null, UtilMisc.toList("customTimePeriodId"), null, false);
    	for (GenericValue item : acctgPartyList) {
    		String customTimePeriodId = item.getString("customTimePeriodId");
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if (UtilValidate.isNotEmpty(customTimePeriod) && "Y".equals(customTimePeriod.getString("isClosed")) 
					&& ("FISCAL_QUARTER".equals(customTimePeriod.getString("periodTypeId")) || "FISCAL_YEAR".equals(customTimePeriod.getString("periodTypeId")))) {
				String glAccountId = item.getString("glAccountId");
	    		String partyId = item.getString("partyId");
	    		BigDecimal crAmount = item.getBigDecimal("crAmount");
	    		BigDecimal drAmount = item.getBigDecimal("drAmount");
	    		
				if (!beginPeriodMap.containsKey(customTimePeriodId)) {
        			conds.clear();
        			conds.add(EntityCondition.makeCondition("isClosed", "Y"));
        			conds.add(EntityCondition.makeCondition("periodTypeId", "FISCAL_MONTH"));
        			conds.add(EntityCondition.makeCondition("fromDate", customTimePeriod.getDate("fromDate")));
        			List<GenericValue> beginCustomTimePeriod = delegator.findList("CustomTimePeriod", 
        					EntityCondition.makeCondition(conds), null, null, null, false);
        			if (UtilValidate.isNotEmpty(beginCustomTimePeriod)) {
        				beginPeriodMap.put(customTimePeriodId, beginCustomTimePeriod.get(0).getString("customTimePeriodId"));
        			} else {
        				beginPeriodMap.put(customTimePeriodId, null);
        			}
				}
				String beginCustomTimePeridId = (String) beginPeriodMap.get(customTimePeriodId);
				
				if (!endPeriodMap.containsKey(customTimePeriodId)) {
        			conds.clear();
        			conds.add(EntityCondition.makeCondition("isClosed", "Y"));
        			conds.add(EntityCondition.makeCondition("periodTypeId", "FISCAL_MONTH"));
        			conds.add(EntityCondition.makeCondition("thruDate", customTimePeriod.getDate("thruDate")));
        			List<GenericValue> endCustomTimePeriod = delegator.findList("CustomTimePeriod", 
        					EntityCondition.makeCondition(conds), null, null, null, false);
        			if (UtilValidate.isNotEmpty(endCustomTimePeriod)) {
        				endPeriodMap.put(customTimePeriodId, endCustomTimePeriod.get(0).getString("customTimePeriodId"));
        			} else {
        				endPeriodMap.put(customTimePeriodId, null);
        			}
				}
				String endCustomTimePeridId = (String) endPeriodMap.get(customTimePeriodId);
				
				BigDecimal openingBalance = BigDecimal.ZERO;
				BigDecimal endingBalance = drAmount.subtract(crAmount);
				BigDecimal openingCrBalance = BigDecimal.ZERO;
				BigDecimal openingDrBalance = BigDecimal.ZERO;
				BigDecimal endingCrBalance = BigDecimal.ZERO;
				BigDecimal endingDrBalance = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(beginCustomTimePeridId)) {
					GenericValue beginGlAccountHistoryParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("glAccountId", glAccountId, 
							"partyId", partyId, "organizationPartyId", organizationPartyId, "customTimePeriodId", beginCustomTimePeridId), false);
					if (UtilValidate.isNotEmpty(beginGlAccountHistoryParty)) {
						openingBalance = beginGlAccountHistoryParty.getBigDecimal("openingBalance");
						openingDrBalance = beginGlAccountHistoryParty.getBigDecimal("openingDrBalance");
						openingCrBalance = beginGlAccountHistoryParty.getBigDecimal("openingCrBalance");
					}
				}
				if (UtilValidate.isNotEmpty(endCustomTimePeridId)) {
					GenericValue endGlAccountHistoryParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("glAccountId", glAccountId, 
							"partyId", partyId, "organizationPartyId", organizationPartyId, "customTimePeriodId", endCustomTimePeridId), false);
					if (UtilValidate.isNotEmpty(endGlAccountHistoryParty)) {
						endingDrBalance = endGlAccountHistoryParty.getBigDecimal("endingDrBalance");
						endingCrBalance = endGlAccountHistoryParty.getBigDecimal("endingCrBalance");
					}
				}
				
				//create or store gl_account_history_party
				GenericValue glAccountHistoryParty = delegator.makeValue("GlAccountHistoryParty");
				glAccountHistoryParty.set("glAccountId", glAccountId);
				glAccountHistoryParty.set("partyId", partyId);
				glAccountHistoryParty.set("organizationPartyId", organizationPartyId);
				glAccountHistoryParty.set("customTimePeriodId", customTimePeriodId);
				glAccountHistoryParty.set("openingBalance", openingBalance);
				glAccountHistoryParty.set("postedDebits", drAmount);
				glAccountHistoryParty.set("postedCredits", crAmount);
				glAccountHistoryParty.set("endingBalance", endingBalance);
				glAccountHistoryParty.set("openingCrBalance", openingCrBalance);
				glAccountHistoryParty.set("openingDrBalance", openingDrBalance);
				glAccountHistoryParty.set("endingCrBalance", endingCrBalance);
				glAccountHistoryParty.set("endingDrBalance", endingDrBalance);
				
				try {
					delegator.createOrStore(glAccountHistoryParty);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				//mark visited in AcctgCustomTimePartySumFact
				item.set("flag", "Y");
				item.store();
    		}
    	}
	 }
}