package com.olbius.acc.asset;

import com.olbius.acc.asset.entity.DecreasedFixedAsset;
import com.olbius.acc.asset.entity.FixedAsset;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastMap;
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

public class FADecrementServices {
	
	public static final String MODULE = FADecrementServices.class.getName();
	private static final int  RECIPROCAL_ITEM_SEQ_ID = 5;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListFADecrements(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("-decreasedDate");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("decrementTypeId", "FA_DECREMENT"));
    	try {
    		listIterator = delegator.find("Decrement", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListFADecrements service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> getListAVLDecFixedAssets(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<FixedAsset> listFixedAsset = new ArrayList<FixedAsset>();
    	Set<String> setFa = new HashSet<String>();
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		List<GenericValue> listAddedFixedAsset = delegator.findByAnd("FixedAssetDecrementItem", null, null, false);
    		for(GenericValue item : listAddedFixedAsset) {
    			setFa.add(item.getString("fixedAssetId"));
    		}
    		List<GenericValue> listAllFixedAssetDeps = delegator.findList("FixedAssetDepreciation", EntityCondition.makeCondition("preDepDate", EntityJoinOperator.GREATER_THAN , new Date(Calendar.getInstance().getTime().getTime())), null, null, null, false);
    		for(GenericValue item: listAllFixedAssetDeps) {
    			setFa.add(item.getString("fixedAssetId"));
    		}	
    		if(!setFa.isEmpty()) {
    			listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, setFa));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("statusId", "FA_USING"));
    		
    		listIterator = delegator.find("FixedAsset", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		for(GenericValue item: listIterator.getCompleteList()) {
    			DecreasedFixedAsset fa = new DecreasedFixedAsset();
    			fa.setFixedAssetId(item.getString("fixedAssetId"));
    			fa.setFixedAssetName(item.getString("fixedAssetName"));
    			List<GenericValue> listFixedAssetDeps = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), null, false);
    			GenericValue dep = listFixedAssetDeps.get(0);
    			fa.setRemainValue(dep.getBigDecimal("remainingValue"));
    			listFixedAsset.add(fa);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListAVLDecFixedAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
    	}finally {
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListDecFixedAssets(DispatchContext dispatcher, Map<String, Object> context){
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
    		String decrementId = parameters.get("decrementId")[0];
    		List<GenericValue> listFA = delegator.findByAnd("FixedAssetDecrementItem", UtilMisc.toMap("decrementId", decrementId), null, false);
    		Set<String> faSet = new HashSet<String>();
    		for(GenericValue item : listFA) {
    			faSet.add(item.getString("fixedAssetId"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.IN, faSet));
    		listIterator = delegator.find("FixedAsset", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getDecListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public Map<String, Object> createDecrement(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		//Get parameters
		Date decreasedDate = (Date) context.get("decreasedDate");
		String decrementTypeId = (String) context.get("decrementTypeId");
		String enumId = (String) context.get("enumId");
		String description = (String) context.get("description");
		
		//Create FixedAssetDepreciation
		GenericValue decrement = delegator.makeValue("Decrement");
		decrement.set("decreasedDate", decreasedDate);
		decrement.set("decrementTypeId", decrementTypeId);
		decrement.set("enumId", enumId);
		decrement.set("description", description);
		decrement.set("isClosed", "N");
		String decrementId = delegator.getNextSeqId("Decrement");
		decrement.set("decrementId", decrementId);
		try {
			decrement.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("decrementId", decrementId);
		return result;
	}
	
	public Map<String, Object> postFADecTrans(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get dispatcher
		LocalDispatcher localDispatcher = dispatcher.getDispatcher();
		//Get parameters
		String decrementId = (String) context.get("decrementId");
		try {
			GenericValue fixedAssetDecrement = delegator.findOne("Decrement", UtilMisc.toMap("decrementId", decrementId), false);
			List<GenericValue> faDecrements = delegator.findByAnd("FixedAssetDecrementView", UtilMisc.toMap("decrementId", decrementId), null, false);
			for(GenericValue item : faDecrements) {
				//Create Trans
				Map<String, Object> createTransCtx = FastMap.newInstance();
				createTransCtx.put("acctgTransTypeId", "DECREMENT");
				createTransCtx.put("description", fixedAssetDecrement.get("description"));
				createTransCtx.put("glFiscalTypeId", "ACTUAL");
				createTransCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransCtx.put("userLogin", context.get("userLogin"));
				//createTransCtx.put("transactionDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createTransCtx.put("transactionDate", new Timestamp(item.getDate("decreasedDate").getTime()));
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createTransCtx);
				List<GenericValue> fixedAssetGlAcc = delegator.findByAnd("FixedAssetTypeGlAccount", UtilMisc.toMap("fixedAssetTypeId", item.get("fixedAssetTypeId"), "organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId"))),null, false);
				//Create Trans Entry
				List<GenericValue> listFADeps = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.get("fixedAssetId")), null, false);
				GenericValue dep = listFADeps.get(0);
				
				int reciprocalSeqId = 1;
				String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				String strOrganizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId"));
				
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				if (dep.get("remainingValue") != null && !dep.get("remainingValue").toString().equals("null"))					
				{
					BigDecimal tmpBD = new BigDecimal(dep.get("remainingValue").toString());
					if (tmpBD.compareTo(BigDecimal.ZERO) != 0)
					{
						createTransEntryCtx.clear();
						createTransEntryCtx.put("amount", dep.get("remainingValue"));
						createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
						createTransEntryCtx.put("debitCreditFlag", "D");
						createTransEntryCtx.put("glAccountId", fixedAssetGlAcc.get(0).getString("lossGlAccountId"));
						createTransEntryCtx.put("organizationPartyId", strOrganizationPartyId);
						createTransEntryCtx.put("partyId", strOrganizationPartyId);
						createTransEntryCtx.put("userLogin", context.get("userLogin"));
						createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
						localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
						
						createTransEntryCtx.clear();
						createTransEntryCtx.put("amount", dep.get("remainingValue"));
						createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
						createTransEntryCtx.put("debitCreditFlag", "C");
						createTransEntryCtx.put("glAccountId", fixedAssetGlAcc.get(0).getString("assetGlAccountId"));
						createTransEntryCtx.put("organizationPartyId", strOrganizationPartyId);
						createTransEntryCtx.put("partyId", strOrganizationPartyId);
						createTransEntryCtx.put("userLogin", context.get("userLogin"));
						createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
						localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					}
				}
				
				reciprocalItemSeqId+=1;
				reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				if (dep.get("accumulatedDep") != null && !dep.get("accumulatedDep").toString().equals("null"))					
				{
					BigDecimal tmpBD = new BigDecimal(dep.get("accumulatedDep").toString());
					if (tmpBD.compareTo(BigDecimal.ZERO) != 0)
					{				
						createTransEntryCtx.clear();
						createTransEntryCtx.put("amount", dep.get("accumulatedDep"));
						createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
						createTransEntryCtx.put("debitCreditFlag", "D");
						createTransEntryCtx.put("glAccountId", fixedAssetGlAcc.get(0).getString("accDepGlAccountId"));
						createTransEntryCtx.put("organizationPartyId", strOrganizationPartyId);
						createTransEntryCtx.put("partyId", strOrganizationPartyId);
						createTransEntryCtx.put("userLogin", context.get("userLogin"));
						createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
						localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
						
						createTransEntryCtx.clear();
						createTransEntryCtx.put("amount", dep.get("accumulatedDep"));
						createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
						createTransEntryCtx.put("debitCreditFlag", "C");
						createTransEntryCtx.put("glAccountId", fixedAssetGlAcc.get(0).getString("assetGlAccountId"));
						createTransEntryCtx.put("organizationPartyId", strOrganizationPartyId);
						createTransEntryCtx.put("partyId", strOrganizationPartyId);
						createTransEntryCtx.put("userLogin", context.get("userLogin"));
						createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
						localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					}
				}
				
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				postTransCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
				
				//update fixed asset
				GenericValue fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), false);
				fixedAsset.put("statusId", "FA_UNMONITER");
				fixedAsset.store();
			}
			
			//update fixed asset decrement
			fixedAssetDecrement.put("isClosed", "Y");
			fixedAssetDecrement.store();
		} catch (GenericEntityException | GenericServiceException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updatePostedFixAssetAccTransDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher localDispatcher = dctx.getDispatcher();
		String fixedAssetDecreaseId = (String) context.get("fixedAssetDecreaseId");
		String isPosted = (String) context.get("isPosted");
		String message = null;
		
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		if ("Y".equals(isPosted)) {
			try {
				GenericValue fixedAssetDecrease = delegator.findOne("FixedAssetDecrease", UtilMisc.toMap("fixedAssetDecreaseId", fixedAssetDecreaseId), false);
				fixedAssetDecrease.set("isPosted", Boolean.TRUE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				Map<String, Object> createAccTrans = FastMap.newInstance();
				if (UtilValidate.isNotEmpty(fixedAssetDecrease.getString("decreaseReasonTypeId"))) {
					GenericValue reason = delegator.findOne("FixedAssetDecrReasonType", UtilMisc.toMap("decreaseReasonTypeId", fixedAssetDecrease.getString("decreaseReasonTypeId")), false);
					createAccTrans.put("description", reason.getString("description"));
				}
				createAccTrans.put("transactionDate", fixedAssetDecrease.getTimestamp("voucherDate"));
				createAccTrans.put("acctgTransTypeId", "FIXED_ASSET_DECREMENT");
				createAccTrans.put("organizationPartyId", organizationPartyId);
				createAccTrans.put("userLogin", userLogin);
				createAccTrans.put("glFiscalTypeId", "ACTUAL");
				
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createAccTrans);
				String acctgTransId = null;
				if (ServiceUtil.isSuccess(createTransRs)) {
					 acctgTransId = (String) createTransRs.get("acctgTransId");
				} else {
					return ServiceUtil.returnError("error");
				}
				
				List<GenericValue> fixedAssetDecreaseItem = delegator.findList("FixedAssetDecreaseItem", EntityCondition.makeCondition("fixedAssetDecreaseId", fixedAssetDecreaseId), null, null, null, false);
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				int reciprocalSeqId = 1;
				for (GenericValue item : fixedAssetDecreaseItem) {
					String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
					
					List<GenericValue> listFADeps = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), null, false);
					GenericValue dep = listFADeps.get(0);
					
					GenericValue fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", item.getString("fixedAssetId")), false);
					String partyId = organizationPartyId;
					if (UtilValidate.isNotEmpty(fixedAsset) && UtilValidate.isNotEmpty(fixedAsset.getString("partyId"))) {
						partyId = fixedAsset.getString("partyId");
					}
					
					GenericValue fixedAssetTypeGlAccount = delegator.findOne("FixedAssetTypeGlAccount",
							UtilMisc.toMap("fixedAssetTypeId", fixedAsset.getString("fixedAssetTypeId"), "fixedAssetId", "_NA_", "organizationPartyId", organizationPartyId), false);
					String lossGlAccountId = "";
					if (UtilValidate.isNotEmpty(fixedAssetTypeGlAccount)) {
						lossGlAccountId = fixedAssetTypeGlAccount.getString("lossGlAccountId");
					}
		            
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("accumulatedDepreciation"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "C");
					createTransEntryCtx.put("glAccountId", dep.getString("costGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("accumulatedDepreciation"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", item.getString("depreciationGlAccount"));
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					reciprocalSeqId +=1;
					String reciprocalItemSeqId1 = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
					
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("remainValue"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "C");
					createTransEntryCtx.put("glAccountId", dep.getString("costGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId1);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("remainValue"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", lossGlAccountId);
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId1);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					reciprocalSeqId +=1;
				}
				//update FixedAssetDecrease
				fixedAssetDecrease.put("acctgTransId", acctgTransId);
				fixedAssetDecrease.store();
				
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", acctgTransId);
				postTransCtx.put("userLogin", userLogin);
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return ServiceUtil.returnSuccess(message);
	}
}