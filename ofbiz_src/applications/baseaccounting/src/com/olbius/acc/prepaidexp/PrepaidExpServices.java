package com.olbius.acc.prepaidexp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.prepaidexp.entity.PEAllocCost;
import com.olbius.acc.utils.ErrorUtils;

public class PrepaidExpServices {
	
	public static final String MODULE = PrepaidExpServices.class.getName();
	
	public Map<String, Object> createPrepaidExp(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get parameters
		String prepaidExpId = (String)context.get("prepaidExpId");
		String prepaidExpName = (String)context.get("prepaidExpName");
		String description = (String)context.get("description");
		String prepaidExpGlAccountId = (String)context.get("prepaidExpGlAccountId");
		BigDecimal amount = (BigDecimal)context.get("amount");
		Long allocPeriodNum = (Long)context.get("allocPeriodNum");
		BigDecimal amountEachPeriod = (BigDecimal)context.get("amountEachPeriod");
		Timestamp acquiredDate = (Timestamp)context.get("acquiredDate");
		
		//Create PrepaidExp
		GenericValue prepaidExp = delegator.makeValue("PrepaidExp");
		prepaidExp.put("prepaidExpId", prepaidExpId);
		prepaidExp.put("prepaidExpName", prepaidExpName);
		prepaidExp.put("description", description);
		prepaidExp.put("prepaidExpGlAccountId", prepaidExpGlAccountId);
		prepaidExp.put("amount", amount);
		prepaidExp.put("allocPeriodNum", allocPeriodNum);
		prepaidExp.put("amountEachPeriod", amountEachPeriod);
		prepaidExp.put("acquiredDate", acquiredDate);
		prepaidExp.put("preAllocationDate", acquiredDate);
		prepaidExp.put("remainingValue", amount);
		try {
			prepaidExp.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("prepaidExpId", prepaidExpId);
		return result;
	}
	
	
	public Map<String, Object> createPrepaidExpAlloc(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get parameters
		String prepaidExpId = (String)context.get("prepaidExpId");
		String seqId = (String)context.get("seqId");
		String allocPartyId = (String)context.get("allocPartyId");
		BigDecimal allocRate = (BigDecimal)context.get("allocRate");
		String allocGlAccountId = (String)context.get("allocGlAccountId");
		
		//Create PrepaidExp
		GenericValue prepaidExpAlloc = delegator.makeValue("PrepaidExpAlloc");
		prepaidExpAlloc.put("prepaidExpId", prepaidExpId);
		prepaidExpAlloc.put("seqId", seqId);
		prepaidExpAlloc.put("allocPartyId", allocPartyId);
		prepaidExpAlloc.put("allocRate", allocRate);
		prepaidExpAlloc.put("allocGlAccountId", allocGlAccountId);
		
		try {
			prepaidExpAlloc.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("prepaidExpId", prepaidExpId);
		result.put("seqId", seqId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListPrepaidExp(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listIterator = delegator.find("PrepaidExp", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListPrepaidExp service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public Map<String, Object> getPECostAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		List<PEAllocCost> alloCosts = new ArrayList<PEAllocCost>();
		try {
			List<GenericValue> listPrepaidExps = delegator.findList("PrepaidExp", null, null, null, null, false);
			for(GenericValue prepaidExp : listPrepaidExps) {
				List<GenericValue> prepaidExpAllocList = delegator.findByAnd("PrepaidExpAlloc", UtilMisc.toMap("prepaidExpId", prepaidExp.getString("prepaidExpId")), null, false);
				for(GenericValue alloc : prepaidExpAllocList) {
					PEAllocCost allocCost = new PEAllocCost();
					List<GenericValue> listPEPeriods = delegator.findByAnd("PrepaidExpCustomTimePeriod", UtilMisc.toMap("prepaidExpId", prepaidExp.getString("prepaidExpId")), null, false);
					BigDecimal allocAmount = BigDecimal.ZERO;
					if(listPEPeriods.size() > 0) {
						GenericValue equipPeriod = listPEPeriods.get(0);
						allocAmount = equipPeriod.getBigDecimal("amount");
					}
					BigDecimal amount = prepaidExp.getBigDecimal("amount").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal remainingValue = prepaidExp.getBigDecimal("remainingValue").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal amountEachPeriod = prepaidExp.getBigDecimal("amountEachPeriod").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal allocatedAmount = (amount.subtract(remainingValue)).multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					
					allocCost.setPrepaidExpId(prepaidExp.getString("prepaidExpId"));
					allocCost.setPrepaidExpName(prepaidExp.getString("prepaidExpName"));
					allocCost.setAllocDate(prepaidExp.getTimestamp("acquiredDate"));
					allocCost.setAmount(amount);
					allocCost.setRemainingValue(remainingValue);
					allocCost.setMonthNumber(prepaidExp.getLong("allocPeriodNum"));
					allocCost.setMonthlyAllocAmount(amountEachPeriod);
					allocCost.setAllowAmount(allocAmount);
					allocCost.setAccumulatedAllocAmount(allocatedAmount);
					allocCost.setUomId(prepaidExp.getString("currencyUomId"));
					allocCost.setPartyId(alloc.getString("allocPartyId"));
					if(prepaidExp.getTimestamp("preAllocationDate").after(new Timestamp(Calendar.getInstance().getTimeInMillis()))) {
						allocCost.setPreAccumulatedAllocAmount(allocatedAmount.subtract(allocAmount));
					}else {
						allocCost.setPreAccumulatedAllocAmount(allocatedAmount);
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
}
