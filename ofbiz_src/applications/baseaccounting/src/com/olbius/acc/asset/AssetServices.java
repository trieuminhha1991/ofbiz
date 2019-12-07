package com.olbius.acc.asset;

import com.olbius.acc.asset.entity.AllocCost;
import com.olbius.acc.asset.entity.FAAllocCost;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class AssetServices {
	
	public static final String MODULE = AssetServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAssets(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listIterator = delegator.find("FixedAssetAndDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAssetsNotDepreciation(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String dateStr = parameters.get("date") != null? parameters.get("date")[0] : null;	
		String month = parameters.get("month") != null? parameters.get("month")[0] : null;
		String year = parameters.get("year") != null? parameters.get("year")[0] : null;    	
    	try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("dateAcquired");
			}
			
			Timestamp date = new Timestamp(System.currentTimeMillis());
			if(dateStr != null){
				date = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(dateStr)));
			}
			
			List<GenericValue> faUsedList = delegator.findList("FixedAssetDecreaseItemSumByDate",
					EntityCondition.makeCondition("voucherDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, date),
					UtilMisc.toSet("fixedAssetId", "quantityDecrease", "quantity"), null, null, false);
			if (UtilValidate.isNotEmpty(faUsedList)) {
				Set<String> fixedAssetIds = FastSet.newInstance();
				for (GenericValue item : faUsedList) {
					if (item.getLong("quantityDecrease") >= item.getLong("quantity")) {
						fixedAssetIds.add(item.getString("fixedAssetId"));
					}
				}
				listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, fixedAssetIds));
			}
			    		
			List<GenericValue> fixedAssetDepCalItemList = delegator.findList("FixedAssetDepreciationCalcAndItem",
					EntityCondition.makeCondition(EntityCondition.makeCondition("month", Integer.valueOf(month)),
							EntityJoinOperator.AND, EntityCondition.makeCondition("year", Integer.valueOf(year))), null, null, null, false);
			if(UtilValidate.isNotEmpty(fixedAssetDepCalItemList)){
				List<String> fixedAssetIds = EntityUtil.getFieldListFromEntityList(fixedAssetDepCalItemList, "fixedAssetId", true);
				listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, fixedAssetIds));
			}			
		
			listAllConditions.add(EntityCondition.makeCondition("dateAcquired", EntityJoinOperator.LESS_THAN_EQUAL_TO, date));
			
    		listIterator = delegator.find("FixedAssetAndDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListFixedAssetsDepreciation(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String depreciationCalcId = parameters.get("depreciationCalcId") != null? parameters.get("depreciationCalcId")[0] : null;	
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("dateAcquired");
			}
			
			listAllConditions.add(EntityCondition.makeCondition("depreciationCalcId", EntityJoinOperator.EQUALS, depreciationCalcId));
			
			listIterator = delegator.find("FixedAssetDepreCalcItemAndFA", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListFixedAssetsDepreciation service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}		
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAssetsNotIncrease(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("isIncrease", EntityJoinOperator.EQUALS, null));
    		listIterator = delegator.find("FixedAssetAndDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAssignParties(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	try {
    		String fixedAssetId = parameters.get("fixedAssetId")[0];
    		mapCondition.put("fixedAssetId", fixedAssetId);
    		EntityCondition fixedAssetCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(fixedAssetCond);
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-allocatedDate");
    		}
    		listIterator = delegator.find("PartyFixedAssetAssignmentAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssignParties service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListFixedAssetDeps(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	try {
    		String fixedAssetId = parameters.get("fixedAssetId")[0];
    		mapCondition.put("fixedAssetId", fixedAssetId);
    		EntityCondition fixedAssetCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(fixedAssetCond);
    		listIterator = delegator.find("FixedAssetDepreciation", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListFixedAssetDeps service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public Map<String, Object> createFixedAssetDep(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		GenericValue fixedAssetDep = delegator.makeValue("FixedAssetDepreciation");
		fixedAssetDep.setNonPKFields(context);
		String fixedAssetDepId = delegator.getNextSeqId("FixedAssetDepreciation");
		fixedAssetDep.set("fixedAssetDepId", fixedAssetDepId);
		try {
			fixedAssetDep.create();
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, fixedAssetDepId);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("fixedAssetDepId", fixedAssetDepId);
		return result;
	}
	
	/*@SuppressWarnings({ "unchecked"})
	public Map<String, Object> createFixedAssetAndDep(DispatchContext dispatcher, Map<String, Object> context){
		LocalDispatcher localdispatcher = dispatcher.getDispatcher();
		Map<String, Object> createFixedAssetRs = null;
		Delegator delegator = dispatcher.getDelegator();
		String fixedAssetId = (String)context.get("fixedAssetId"); 
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", fixedAssetId), false);
			if(UtilValidate.isNotEmpty(fixedAsset)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCFixedAssetIdIsExists", 
						UtilMisc.toMap("fixedAssetId", fixedAssetId), locale));
			}
			Map<String, Object> createFixedAssetCtx = ServiceUtil.setServiceFields(localdispatcher, "createFixedAsset", context, (GenericValue)context.get("userLogin"), (TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
			createFixedAssetRs = localdispatcher.runSync("createFixedAsset", createFixedAssetCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		
		//Create Fixed Asset Depreciation
		Map<String, Object> createFixedAssetDepRs = null;
		try {
			Map<String, Object> createFixedAssetDepCtx = ServiceUtil.setServiceFields(localdispatcher, "createFixedAssetDep", context, (GenericValue)context.get("userLogin"), (TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
			createFixedAssetDepCtx.put("fixedAssetId", createFixedAssetRs.get("fixedAssetId"));
			createFixedAssetDepRs = localdispatcher.runSync("createFixedAssetDep", createFixedAssetDepCtx);
		} catch (GenericServiceException | GeneralServiceException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		//Create Fixed Asset Allocation
		try {																																				
			List<Map<String, String>> listAllocs = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)context.get("listAllocs"));
			if(listAllocs != null)
				for(Map<String, String> item: listAllocs) {
					Map<String, Object> createFixedAssetAllocCtx = new HashMap<String, Object>();
					createFixedAssetAllocCtx.put("fixedAssetId", createFixedAssetRs.get("fixedAssetId"));
					createFixedAssetAllocCtx.put("seqId", item.get("seqId"));
					createFixedAssetAllocCtx.put("allocPartyId", item.get("allocPartyId"));
					BigDecimal allocRate = BigDecimal.valueOf(Double.parseDouble((String) item.get("allocRate")));
					createFixedAssetAllocCtx.put("allocRate", allocRate);
					createFixedAssetAllocCtx.put("allocGlAccountId", item.get("allocGlAccountId"));
					createFixedAssetAllocCtx.put("userLogin", context.get("userLogin"));
					localdispatcher.runSync("createFixedAssetAlloc", createFixedAssetAllocCtx);
				}
		} catch (ParseException | GenericServiceException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("fixedAssetDepId", createFixedAssetDepRs.get("fixedAssetDepId"));
		result.put("fixedAssetId", createFixedAssetRs.get("fixedAssetId"));
		return result;
	}*/
	
	public Map<String, Object> getCostAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		List<AllocCost> alloCosts = new ArrayList<AllocCost>();
		try {
			List<GenericValue> listFixedAssets = delegator.findList("FixedAsset", null, null, UtilMisc.toList("partyId ASC"), null, false);
			for(GenericValue fixedAsset : listFixedAssets) {
				List<GenericValue> fixedAssetAlloc = delegator.findByAnd("FixedAssetAlloc", UtilMisc.toMap("fixedAssetId", fixedAsset.getString("fixedAssetId")), null, false);
				for(GenericValue alloc : fixedAssetAlloc) {
					FAAllocCost allocCost = new FAAllocCost();
					List<GenericValue> listFixedAssetDep = delegator.findByAnd("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetId", fixedAsset.getString("fixedAssetId")), null, false);
					List<GenericValue> listFixedAssetPeriod = delegator.findByAnd("FixedAssetCustomTimePeriod", UtilMisc.toMap("fixedAssetId", fixedAsset.getString("fixedAssetId")), null, false);
					GenericValue fixedAssetPeriod = !UtilValidate.isEmpty(listFixedAssetPeriod) ? listFixedAssetPeriod.get(0) : null;
					GenericValue fixedAssetDep = !UtilValidate.isEmpty(listFixedAssetDep) ? listFixedAssetDep.get(0) : null;
					allocCost.setFixedAssetId(fixedAsset.getString("fixedAssetId"));
					allocCost.setFixedAssetName(fixedAsset.getString("fixedAssetName"));
					allocCost.setAllocDate(fixedAsset.getTimestamp("dateAcquired"));
					BigDecimal purchaseCost = fixedAsset.getBigDecimal("purchaseCost").multiply(alloc.getBigDecimal("allocRate").divide(BigDecimal.valueOf(100d)));
					BigDecimal remainingValue = fixedAssetDep != null ? fixedAssetDep.getBigDecimal("remainingValue").multiply(alloc.getBigDecimal("allocRate").divide(BigDecimal.valueOf(100d))) : BigDecimal.ZERO;
					BigDecimal monthlyDepAmount = fixedAssetDep != null ? fixedAssetDep.getBigDecimal("monthlyDepAmount").multiply(alloc.getBigDecimal("allocRate").divide(BigDecimal.valueOf(100d))) : BigDecimal.ZERO;
					BigDecimal amount = fixedAssetPeriod != null ? fixedAssetPeriod.getBigDecimal("amount").multiply(alloc.getBigDecimal("allocRate").divide(BigDecimal.valueOf(100d))) : BigDecimal.ZERO; 
					BigDecimal accumulatedDep = fixedAssetDep != null ? fixedAssetDep.getBigDecimal("accumulatedDep").multiply(alloc.getBigDecimal("allocRate").divide(BigDecimal.valueOf(100d))):BigDecimal.ZERO;
					allocCost.setAmount(purchaseCost);
					allocCost.setRemainingValue(remainingValue);
					allocCost.setMonthNumber(fixedAssetDep != null ? fixedAssetDep.getLong("usefulLives") : Long.parseLong("0"));
					allocCost.setMonthlyAllocAmount(monthlyDepAmount);
					allocCost.setAllowAmount(amount);
					allocCost.setAccumulatedAllocAmount(accumulatedDep);
					allocCost.setUomId(fixedAsset.getString("uomId"));
					allocCost.setPartyId(alloc.getString("allocPartyId"));
					
					if(fixedAssetDep != null)
					{
						if(fixedAssetDep.getDate("preDepDate").after(new Date(Calendar.getInstance().getTimeInMillis()))) {
							allocCost.setPreAccumulatedAllocAmount(accumulatedDep.subtract(amount));
						}else {
							allocCost.setPreAccumulatedAllocAmount(accumulatedDep);
						}
					}else{
						allocCost.setPreAccumulatedAllocAmount(accumulatedDep);
					}
					
					alloCosts.add(allocCost);
				}
			}
			result = ServiceUtil.returnSuccess();
			result.put("listCostAlloc", alloCosts);
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public Map<String, Object> createFixedAssetAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String fixedAssetId = (String)context.get("fixedAssetId");
		
		GenericValue prepaidExpAlloc = delegator.makeValue("FixedAssetAlloc");
		prepaidExpAlloc.setNonPKFields(context);
		prepaidExpAlloc.set("fixedAssetId", fixedAssetId);
		delegator.setNextSubSeqId(prepaidExpAlloc, "seqId", 2, 1);
		try {
			prepaidExpAlloc.create();
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createFixedAssetAccompany(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetAccompany = delegator.makeValue("FixedAssetAccompany");
		fixedAssetAccompany.setAllFields(context, false, null, null);
		delegator.setNextSubSeqId(fixedAssetAccompany, "accompanySeqId", 2, 1);
		try {
			delegator.create(fixedAssetAccompany);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFixedAssetAccompanyJQ(DispatchContext dctx, Map<String, Object> context){
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
    			listSortFields.add("accompanySeqId");
    		}
			listIterator = delegator.find("FixedAssetAccompany", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFixedAssetIncreaseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-dateArising");
			}
			listIterator = delegator.find("FixedAssetIncreaseAndTotal", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFixedAssetDepreciationCalcJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-voucherDate");
			}
			listIterator = delegator.find("FixedAssetDepreciationCalcAndAmount", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFixedAssetDecreaseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-voucherDate");
			}
			listIterator = delegator.find("FixedAssetDecreaseAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAssetsNotDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String fixedAssetDecreaseId = parameters.get("fixedAssetDecreaseId") != null? parameters.get("fixedAssetDecreaseId")[0] : null; 
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("fixedAssetId");
			}
			EntityCondition cond = null;
			if(fixedAssetDecreaseId != null){
				cond = EntityCondition.makeCondition("fixedAssetDecreaseId", EntityJoinOperator.NOT_EQUAL, fixedAssetDecreaseId);
			}
			List<GenericValue> listFixedAssetDecrease = delegator.findList("FixedAssetDecreaseItem", cond, null, null, null, false);
			if(UtilValidate.isNotEmpty(listFixedAssetDecrease)){
				List<String> fixedAssetIdList = EntityUtil.getFieldListFromEntityList(listFixedAssetDecrease, "fixedAssetId", true);
				listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, fixedAssetIdList));
			}
			listIterator = delegator.find("FixedAssetAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createFixedAssetIncrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue fixedAssetIncrease = delegator.makeValue("FixedAssetIncrease");
		fixedAssetIncrease.setNonPKFields(context);
		String fixedAssetIncreaseId = delegator.getNextSeqId("FixedAssetIncrease");
		fixedAssetIncrease.put("fixedAssetIncreaseId", fixedAssetIncreaseId);
		try {
			delegator.create(fixedAssetIncrease);
			retMap.put("fixedAssetIncreaseId", fixedAssetIncreaseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createOrStoreFixedAssetIncreaseItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetIncreaseItem = delegator.makeValue("FixedAssetIncreaseItem");
		fixedAssetIncreaseItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(fixedAssetIncreaseItem);
			
			GenericValue fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", fixedAssetIncreaseItem.getString("fixedAssetId")), false);
			fixedAsset.set("isIncrease", "1");
			fixedAsset.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createFixedAssetDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue fixedAssetDecrease = delegator.makeValue("FixedAssetDecrease");
		fixedAssetDecrease.setNonPKFields(context);
		String fixedAssetDecreaseId = delegator.getNextSeqId("FixedAssetDecrease");
		fixedAssetDecrease.put("fixedAssetDecreaseId", fixedAssetDecreaseId);
		try {
			delegator.create(fixedAssetDecrease);
			retMap.put("fixedAssetDecreaseId", fixedAssetDecreaseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateFixedAssetDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetDecrease = delegator.makeValue("FixedAssetDecrease");
		fixedAssetDecrease.setAllFields(context, false, null, null);
		try {
			fixedAssetDecrease.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateFixedAssetIncrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetIncrease = delegator.makeValue("FixedAssetIncrease");
		fixedAssetIncrease.setAllFields(context, false, null, null);
		try {
			fixedAssetIncrease.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createOrStoreFixedAssetDecreaseItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetDecreaseItem = delegator.makeValue("FixedAssetDecreaseItem");
		fixedAssetDecreaseItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(fixedAssetDecreaseItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createFixedAssetDepreciationCalc(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue fixedAssetDepreciationCalc = delegator.makeValue("FixedAssetDepreciationCalc");
		fixedAssetDepreciationCalc.setNonPKFields(context);
		String depreciationCalcId = delegator.getNextSeqId("FixedAssetDepreciationCalc");
		fixedAssetDepreciationCalc.put("depreciationCalcId", depreciationCalcId);
		try {
			delegator.create(fixedAssetDepreciationCalc);
			retMap.put("depreciationCalcId", depreciationCalcId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> updateFixedAssetDepreciationCalc(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetDepreciationCalc = delegator.makeValue("FixedAssetDepreciationCalc");
		fixedAssetDepreciationCalc.setAllFields(context, false, null, null);
		try {
			delegator.store(fixedAssetDepreciationCalc);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createOrStoreFixedAssetDepreCalcItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue fixedAssetDepreCalcItem = delegator.makeValue("FixedAssetDepreCalcItem");
		fixedAssetDepreCalcItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(fixedAssetDepreCalcItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createFixedAssetDecrReasonType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
		String decreaseReasonTypeId = (String)context.get("decreaseReasonTypeId");
		String description = (String)context.get("description");
		try {
			if(decreaseReasonTypeId != null){
				decreaseReasonTypeId = decreaseReasonTypeId.trim();
				GenericValue fixedAssetDecrReasonType = delegator.findOne("FixedAssetDecrReasonType", UtilMisc.toMap("decreaseReasonTypeId", decreaseReasonTypeId), false);
				if(fixedAssetDecrReasonType != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "ReasonTypeIdIsExistsed", UtilMisc.toMap("decreaseReasonTypeId", decreaseReasonTypeId), locale));
				}
			}else{
				decreaseReasonTypeId = delegator.getNextSeqId("FixedAssetDecrReasonType");
			}
			description = description.trim();
			List<GenericValue> fixedAssetDecrReasonTypeList = delegator.findByAnd("FixedAssetDecrReasonType", UtilMisc.toMap("description", description), null, false);
			if(UtilValidate.isNotEmpty(fixedAssetDecrReasonTypeList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "ReasonTypeIdDescExistsed", UtilMisc.toMap("description", description), locale));
			}
			GenericValue fixedAssetDecrReasonType = delegator.makeValue("FixedAssetDecrReasonType");
			fixedAssetDecrReasonType.put("decreaseReasonTypeId", decreaseReasonTypeId);
			fixedAssetDecrReasonType.put("description", description);
			delegator.create(fixedAssetDecrReasonType);
			retMap.put("decreaseReasonTypeId", decreaseReasonTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getFixedAssetReportS09DNNData(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getMonthStart(fromDate);
		thruDate = UtilDateTime.getMonthEnd(thruDate, timeZone, locale);
		String fixedAssetTypeIds = (String)context.get("fixedAssetTypeIds");
		List<EntityCondition> conds = FastList.newInstance();
		
		//get info
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true);
		String companyName = "";
		if (UtilValidate.isNotEmpty(partyGroup)) {
			companyName = partyGroup.getString("groupName");
		}
		retMap.put("companyName", companyName);

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> dummy = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null, null, true);
		String address = "";
		if (UtilValidate.isNotEmpty(dummy)) {
			address = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
			if (UtilValidate.isNotEmpty(address)) {
				address = address.replaceAll(", __", "");
			}
		}
		retMap.put("address", address);
		
		if(fixedAssetTypeIds != null){
			List<String> fixedAssetTypeIdList = FastList.newInstance();
			JSONArray fixedAssetTypeIdJsonArr = JSONArray.fromObject(fixedAssetTypeIds);
			for(int i = 0; i < fixedAssetTypeIdJsonArr.size(); i++){
				String fixedAssetTypeId = fixedAssetTypeIdJsonArr.getString(i);
				fixedAssetTypeIdList.add(fixedAssetTypeId);
			}
			conds.add(EntityCondition.makeCondition("fixedAssetTypeId", EntityJoinOperator.IN, fixedAssetTypeIdList));
		}
		conds.add(EntityCondition.makeCondition("datePurchase", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
		try {
			List<GenericValue> listFixedAsset = delegator.findList("FixedAssetAndDetail", EntityCondition.makeCondition(conds), null, UtilMisc.toList("fixedAssetId"), null, false);
			List<String> fixedAssetTypeIdList = EntityUtil.getFieldListFromEntityList(listFixedAsset, "fixedAssetTypeId", true);
			Map<String, Object> fixedAssetAndTypeMap = FastMap.newInstance();
			for(String fixedAssetTypeId: fixedAssetTypeIdList){
				List<GenericValue> tempFixedAssetList = EntityUtil.filterByCondition(listFixedAsset, EntityCondition.makeCondition("fixedAssetTypeId", fixedAssetTypeId));
				fixedAssetAndTypeMap.put(fixedAssetTypeId, tempFixedAssetList);
			}
			retMap.put("fixedAssetAndTypeMap", fixedAssetAndTypeMap);
			retMap.put("fixedAssetTypeIdList", fixedAssetTypeIdList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateFixedAssetDepreciation(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String fixedAssetDepId = (String)context.get("fixedAssetDepId");
		try {
			GenericValue fixedAssetDep = delegator.findOne("FixedAssetDepreciation", UtilMisc.toMap("fixedAssetDepId", fixedAssetDepId), false);
			if(fixedAssetDep == null){
				return ServiceUtil.returnError("Cannot find fixedAssetDep to update");
			}
			fixedAssetDep.setNonPKFields(context);
			fixedAssetDep.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
}