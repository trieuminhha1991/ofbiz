package com.olbius.acc.report.balancetrial;

import com.olbius.acc.report.balancetrial.entity.GlAccountBal;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings({ "unchecked" })
public class BalanceTrialServices {
	 public static final String module = BalanceTrialServices.class.getName();
	 public static final String resource = "widgetUiLabels";
	 public static final String resourceError = "widgetErrorUiLabels";
	 
	 public static Map<String, Object> getListJqTrialBalanceAccount(DispatchContext ctx, Map<String, Object> context) {
    	
		//Variables 
	    Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		List<GlAccountBal> listGlAccountBals = new ArrayList<GlAccountBal>();
		
		//Get context parameters
    	String organizationPartyId =(String)context.get("organizationPartyId");
    	String customTimePeriodId = context.containsKey("customTimePeriodId") ? (String)context.get("customTimePeriodId") : null;
    	
    	try {
			//Get parameters
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
	    	List<GenericValue> glAccountList = delegator.findList("GlAccountOrgDetail", EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toList("glAccountId"), null, false);
			for(GenericValue item : glAccountList) {
	    		GlAccountBal bal = new GlAccountBal();
	    		List<GenericValue> listChild = delegator.findList("GlAccount", EntityCondition.makeCondition("parentGlAccountId", item.getString("glAccountId")), null, null, null, false);
	    		if(UtilValidate.isEmpty(listChild)) {
	    			bal.setIsLeaf("Y");
	    		}else {
	    			bal.setIsLeaf("N");
	    		}
	    		bal.setGlAccountId(item.getString("glAccountId"));
	    		bal.setAccountName(item.getString("accountName"));
	    		bal.setParentId(item.getString("parentGlAccountId"));
	    		Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningBalance(item.getString("glAccountId"), customTimePeriodId, organizationPartyId, delegator);
	    		Map<String, BigDecimal> postedAmount = BalanceWorker.getPostedAmount(item.getString("glAccountId"), customTimePeriodId, organizationPartyId, delegator);
	    		Map<String, BigDecimal> endingAmount = BalanceWorker.getEndingBalance(item.getString("glAccountId"), customTimePeriodId, organizationPartyId, delegator);
	    		bal.setOpeningDrBalance(openingBal.get(BalanceWorker.DEBIT));
	    		bal.setOpeningCrBalance(openingBal.get(BalanceWorker.CREDIT));
	    		bal.setPostedCredits(postedAmount.get(BalanceWorker.CREDIT));
	    		bal.setPostedDebits(postedAmount.get(BalanceWorker.DEBIT));
	    		bal.setEndingDrBalance(endingAmount.get(BalanceWorker.DEBIT));
	    		bal.setEndingCrBalance(endingAmount.get(BalanceWorker.CREDIT));
	    		listGlAccountBals.add(bal);
	    		
	    		if(item.getString("glAccountId").equals("1121") && customTimePeriodId.equals("121223")){
	    			Debug.log(module + "::getListJqTrialBalanceAccount "
	    					+ ", openBalanceDr = " + bal.getOpeningDrBalance()
	    					+ ", openBalanceCr = " + bal.getOpeningCrBalance()
	    					+ ", postedDr = " + bal.getPostedDebits()
	    					+ ", postedCr = " + bal.getPostedCredits()
	    					+ ", endBalanceDr = " + bal.getEndingDrBalance()
	    					+ ", endBalanceCr = " + bal.getEndingCrBalance()
	    						);
	    		}
			}
		} catch (GenericEntityException | NoSuchAlgorithmException e) {
			ErrorUtils.processException(e, module);
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
			BalanceWorker.clearCache(delegator);
		}
    	successResult.put("listBal", listGlAccountBals);
    	return successResult;
     }
	 
	 public static Map<String,Object> getListInventoryAverageCost(DispatchContext dpct, Map<String,Object> context){
		 	Delegator delegator = dpct.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	try {
		    	List<GenericValue> inventoryItems = delegator.findList("InventoryItem", null, UtilMisc.toSet("productId"), UtilMisc.toList("productId"), null, false);
		    	List<String> inventoryItemProducts = EntityUtil.getFieldListFromEntityList(inventoryItems, "productId", true);
		    	List<Map<String,Object>> inventoryAverageCosts = FastList.newInstance();
		    	for(String productId : inventoryItemProducts){
		    		Map<String,Object> result = dpct.getDispatcher().runSync("calculateProductAverageCost", UtilMisc.toMap("productId", productId, "userLogin", userLogin));
		    		BigDecimal totalQuantityOnHand = (BigDecimal) result.get("totalQuantityOnHand");
		    		BigDecimal totalInventoryCost = (BigDecimal) result.get("totalInventoryCost");
	    	        BigDecimal productAverageCost = (BigDecimal) result.get("productAverageCost");
	    	        String currencyUomId =(String) result.get("currencyUomId");
	    	        if (!totalQuantityOnHand.equals(BigDecimal.ZERO)) {
	    	        	Map<String,Object> mapTmp = FastMap.newInstance();
	    	        	mapTmp.put("productId", productId);
	    	        	GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
	    	        	mapTmp.put("productName", product.get("productName"));
	    	        	String uomId = (String) product.get("quantityUomId");
	    	        	GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", uomId));
	    	        	mapTmp.put("uom", uom.get("description"));
	    	        	mapTmp.put("totalQuantityOnHand", totalQuantityOnHand);
	    	        	mapTmp.put("productAverageCost", productAverageCost);
	    	        	mapTmp.put("totalInventoryCost", totalInventoryCost);
	    	        	mapTmp.put("currencyUomId", currencyUomId);
	    	        	inventoryAverageCosts.add(mapTmp);
	    	        }
		    	}
	    	inventoryAverageCosts = EntityMiscUtil.filterMap(inventoryAverageCosts, listAllConditions);
	    	successResult.put("listIterator", inventoryAverageCosts);
	    	successResult.put("TotalRows", String.valueOf(inventoryAverageCosts.size()));
			} catch (Exception e) {
				Debug.log("error call services getListInventoryAverageCost" + e.getMessage());
			}
		 return successResult;
	 }
}
