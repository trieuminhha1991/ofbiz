package com.olbius.acc.asset;

import com.olbius.acc.asset.entity.DepreciatedFixedAsset;
import com.olbius.acc.asset.entity.FixedAsset;
import com.olbius.acc.utils.ConditionUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class FADepServices {
	
	public static final String MODULE = AssetServices.class.getName();
	private static final int  RECIPROCAL_ITEM_SEQ_ID = 5;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListDepPeriods(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("-customTimePeriodId");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("periodTypeId", "DEP_MONTH"));
    	try {
    		listIterator = delegator.find("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling JqxGetListDepPeriods service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListDepAssets(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String customTimePeriodId = parameters.get("customTimePeriodId")[0];
    		List<GenericValue> listFA = delegator.findByAnd("FixedAssetCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
    		Set<String> faSet = new HashSet<String>();
    		for(GenericValue item : listFA) {
    			faSet.add(item.getString("fixedAssetId"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.IN, faSet));
    		listIterator = delegator.find("FixedAsset", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListDepAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		} 
    	successResult.put("listIterator", listIterator); 
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAVAFixedAssets(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<FixedAsset> listFixedAsset = new ArrayList<FixedAsset>();
		Set<String> faSet = FastSet.newInstance();
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		//Set up condition
    		String customTimePeriodId = parameters.get("customTimePeriodId")[0];
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    		listAllConditions.add(ConditionUtils.filterByThruDate("expectedEndOfLife", customTimePeriod.getDate("thruDate")));
    		List<GenericValue> listFixedAssetAdded = delegator.findList("FixedAssetCustomTimePeriodView", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "isClosed", "N")) , null, listSortFields, opts, false);
    		for(GenericValue item: listFixedAssetAdded) {
    			faSet.add(item.getString("fixedAssetId"));
    		}
			List<GenericValue> listAllFixedAssetDeps = delegator.findList("FixedAssetDepreciation", null , null, null,null, false);
			for(GenericValue dep : listAllFixedAssetDeps) {
				if(dep.getDate("preDepDate").after(customTimePeriod.getDate("thruDate"))) {
					faSet.add(dep.getString("fixedAssetId"));
    			}
			}
			if(!faSet.isEmpty()) {
				listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, faSet));
			}
			listAllConditions.add(EntityCondition.makeCondition("statusId", "FA_USING"));
    		
			//Set up list fixed assets
			listIterator = delegator.find("FixedAsset", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		for(GenericValue item: listIterator.getCompleteList()) {
    			DepreciatedFixedAsset fa = new DepreciatedFixedAsset();
    			fa.setFixedAssetId(item.getString("fixedAssetId"));
    			fa.setFixedAssetName(item.getString("fixedAssetName"));
    			List<GenericValue> listFixedAssetDeps = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), null, false);
    			GenericValue dep = listFixedAssetDeps.get(0);
    			Timestamp dateAcquired = new Timestamp(dep.getDate("preDepDate").getTime());
				Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
				Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
				if(dateAcquired !=null && dateAcquired.after(fromDate)) {
    				int dayNumber = UtilDateTime.getIntervalInDays(dateAcquired, thruDate) + 1;
    				int daysInMonth = UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1;
    				Double monthlyDepAmount = dep.getBigDecimal("monthlyDepAmount").doubleValue();
    				fa.setDepreciation(BigDecimal.valueOf(monthlyDepAmount*dayNumber/daysInMonth));
    			}else {
    				fa.setDepreciation(dep.getBigDecimal("monthlyDepAmount"));
    			}
				fa.setTotalDep(dep.getBigDecimal("remainingValue"));
    			listFixedAsset.add(fa);
    		} 
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListDepAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		} finally {
			try {
				if (listIterator != null)
				{
					listIterator.close();
				}				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
    	successResult.put("listIterator", listFixedAsset);
    	return successResult;
	}
	
	public Map<String, Object> postFADepTrans(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get dispatcher
		LocalDispatcher localDispatcher = dispatcher.getDispatcher();
		//Get parameters
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		try {
			GenericValue customPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			List<GenericValue> faCustomPeriods = delegator.findByAnd("FixedAssetCustomTimePeriodView", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
			for(GenericValue item : faCustomPeriods) {
				Map<String, Object> createTransCtx = FastMap.newInstance();
				createTransCtx.put("acctgTransTypeId", "DEPRECIATION");
				createTransCtx.put("description", customPeriod.get("description"));
				createTransCtx.put("glFiscalTypeId", "ACTUAL");
				createTransCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransCtx.put("userLogin", context.get("userLogin"));
				createTransCtx.put("transactionDate", new Timestamp(customPeriod.getDate("thruDate").getTime()));
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createTransCtx);
				
				//Create Trans
				List<GenericValue> listFADeps = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.get("fixedAssetId")), null, false);
				GenericValue dep = listFADeps.get(0);
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				int reciprocalSeqId = 1;
				
				List<GenericValue> listAllocs = delegator.findByAnd("FixedAssetAlloc", UtilMisc.toMap("fixedAssetId", item.get("fixedAssetId")), null, false);
				for(GenericValue alloc : listAllocs) {
					
		            String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
		            
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", alloc.getBigDecimal("allocRate").multiply(item.getBigDecimal("amount").divide(BigDecimal.valueOf(100d))));
					createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
					createTransEntryCtx.put("debitCreditFlag", "C");
					createTransEntryCtx.put("glAccountId", dep.get("depGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
					createTransEntryCtx.put("partyId",alloc.get("allocPartyId"));
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", context.get("userLogin"));
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", alloc.getBigDecimal("allocRate").multiply(item.getBigDecimal("amount").divide(BigDecimal.valueOf(100d))));
					createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", alloc.getString("allocGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
					createTransEntryCtx.put("partyId",alloc.get("allocPartyId"));
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", context.get("userLogin"));
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					reciprocalSeqId +=1;
				}
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				postTransCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
				
				//Update Dep
				BigDecimal accumulatedDep = dep.getBigDecimal("accumulatedDep") != null ? dep.getBigDecimal("accumulatedDep") : BigDecimal.valueOf(0);
				BigDecimal remainingValue = dep.getBigDecimal("remainingValue") != null ? dep.getBigDecimal("remainingValue") : BigDecimal.valueOf(0);
				GenericValue updateDep = delegator.findOne("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetDepId", dep.get("fixedAssetDepId")), false);
				updateDep.put("accumulatedDep", accumulatedDep.add(item.getBigDecimal("amount")));
				updateDep.put("remainingValue", remainingValue.subtract(item.getBigDecimal("amount")));
				updateDep.put("preDepDate", new Date(UtilDateTime.addDaysToTimestamp(new Timestamp(customPeriod.getDate("thruDate").getTime()), 1).getTime()));
				updateDep.store();
			}
			customPeriod.put("isClosed", "Y");
			customPeriod.store();
		} catch (GenericEntityException | GenericServiceException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updatePostedFixedAssetAcctgTransDeprecation(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String depreciationCalcId = (String)context.get("depreciationCalcId");
		String isPosted = (String)context.get("isPosted");
		String message = null;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId"));
		if("Y".equals(isPosted)){
			try {
				GenericValue fixedAssetDepreciation = delegator.findOne("FixedAssetDepreciationCalc", UtilMisc.toMap("depreciationCalcId",depreciationCalcId), false);
				fixedAssetDepreciation.set("isPosted", Boolean.TRUE);
				
				Map<String, Object> createAccTrans = FastMap.newInstance();
				createAccTrans.put("description", fixedAssetDepreciation.getString("description"));
				createAccTrans.put("transactionDate", fixedAssetDepreciation.getTimestamp("voucherDate"));
				createAccTrans.put("acctgTransTypeId", "DEPRECIATION");
				createAccTrans.put("partyId", organizationPartyId);
				createAccTrans.put("userLogin", context.get("userLogin"));
				createAccTrans.put("glFiscalTypeId", "ACTUAL");
				
				int reciprocalSeqId = 1;
				String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				
				List<GenericValue> fixedAssetDepItem = delegator.findList("FixedAssetDepreCalcItem", EntityCondition.makeCondition("depreciationCalcId", depreciationCalcId), null, null, null, false);
				List<GenericValue> acctgTransEntries = FastList.newInstance();
				for(GenericValue item : fixedAssetDepItem){
					GenericValue acctgTransEntryC = delegator.makeValue("AcctgTransEntry");
					String fixedAssetId = item.getString("fixedAssetId");
					if(UtilValidate.isNotEmpty(fixedAssetId)){
						GenericValue fixedAsset= delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId",fixedAssetId ), false);
						
						BigDecimal amount = item.getBigDecimal("depreciationAmount");
						
						acctgTransEntryC.put("amount",amount);
						acctgTransEntryC.put("organizationPartyId", organizationPartyId);
						acctgTransEntryC.put("currencyUomId", fixedAsset.getString("uomId"));
						acctgTransEntryC.put("origCurrencyUomId", fixedAsset.getString("uomId"));
						acctgTransEntryC.put("partyId", organizationPartyId);
												
						acctgTransEntryC.put("debitCreditFlag", "C");
						acctgTransEntryC.put("glAccountId", item.getString("creditGlAccountId"));
						acctgTransEntryC.put("reciprocalSeqId", reciprocalItemSeqId);
						acctgTransEntries.add(acctgTransEntryC);
						
						GenericValue acctgTransEntryD = (GenericValue) acctgTransEntryC.clone();
						acctgTransEntryD.put("debitCreditFlag", "D");
						acctgTransEntryD.put("glAccountId", item.getString("debitGlAccountId"));
						acctgTransEntries.add(acctgTransEntryD);
						reciprocalSeqId+=1;
						reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
						
						List<GenericValue> listFixedAssetDepreciation = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), null, false);
		    			if (listFixedAssetDepreciation != null && listFixedAssetDepreciation.size() > 0) {
		    				GenericValue fixedAssetDepreciationOne = listFixedAssetDepreciation.get(0);
		    				BigDecimal accumulatedDep =  fixedAssetDepreciationOne.getBigDecimal("accumulatedDep");
		    				fixedAssetDepreciationOne.put("accumulatedDep", accumulatedDep.add(amount));
		    				fixedAssetDepreciationOne.store();		
		    			}
					}
					
					createAccTrans.put("acctgTransEntries", acctgTransEntries);					
				}
				LocalDispatcher localDispatcher = dctx.getDispatcher();
				Map<String, Object> createAccTransResult= localDispatcher.runSync("createAcctgTransAndEntries", createAccTrans);
				
				if(ServiceUtil.isSuccess(createAccTransResult)){
					 String acctgTransId = (String)createAccTransResult.get("acctgTransId");
					 fixedAssetDepreciation.put("acctgTransId", acctgTransId);
					 fixedAssetDepreciation.store();
					 message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				}else{
					return ServiceUtil.returnError("error");
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}catch(GenericServiceException e){
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
			
		}
		
		return ServiceUtil.returnSuccess(message);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFixedAssetAllocationJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityListIterator listIterator = null;
    	String fixedAssetId = parameters.get("fixedAssetId") != null? parameters.get("fixedAssetId")[0] : null;
    	listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", fixedAssetId));
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("seqId");
    		}
			listIterator = delegator.find("FixedAssetAllocAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
}
