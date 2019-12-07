package com.olbius.acc.prepaidexp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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

import com.olbius.acc.prepaidexp.entity.AllocatedPE;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;

public class PEAllocPeriodServices {
	
	public static final String MODULE = PrepaidExpServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListPEAllocPeriods(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("-customTimePeriodId");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("periodTypeId", "PE_ALLOC"));
    	try {
    		listIterator = delegator.find("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListPEAllocPeriods service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAllocPrepaidExp(DispatchContext dispatcher, Map<String, Object> context){
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
    		List<GenericValue> listPEs = delegator.findByAnd("PrepaidExpCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
    		Set<String> peSet = new HashSet<String>();
    		for(GenericValue item : listPEs) {
    			peSet.add(item.getString("prepaidExpId"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("prepaidExpId", EntityJoinOperator.IN, peSet));
    		listIterator = delegator.find("PrepaidExp", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAllocPrepaidExp service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAvailabelPEs(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<AllocatedPE> allPEList = new ArrayList<AllocatedPE>();
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		listIterator = delegator.find("PrepaidExp", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		String customTimePeriodId = parameters.get("customTimePeriodId")[0];
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    		for(GenericValue item : listIterator.getCompleteList()) {
    			AllocatedPE allocPE = new AllocatedPE();
    			Timestamp acquiredDate = item.getTimestamp("acquiredDate");
				Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
				Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
				if(acquiredDate != null && acquiredDate.after(fromDate)) {
    				int dayNumber = UtilDateTime.getIntervalInDays(acquiredDate, thruDate) + 1;
    				int daysInMonth = UtilDateTime.getIntervalInDays(fromDate, thruDate);
    				Double amountEachPeriod = item.getBigDecimal("amountEachPeriod").doubleValue();
    				allocPE.setAmount(BigDecimal.valueOf(amountEachPeriod*dayNumber/daysInMonth));
    			}else {
    				allocPE.setAmount(item.getBigDecimal("amountEachPeriod"));
    			}
    			allocPE.setPrepaidExpId(item.getString("prepaidExpId"));
    			allocPE.setPrepaidExpName(item.getString("prepaidExpName"));
    			allPEList.add(allocPE);
    		}
    	} catch (Exception e) {
    		ErrorUtils.processException(e, MODULE);
    		return ServiceUtil.returnError(e.getMessage());
		}
    	successResult.put("listIterator", allPEList);
    	return successResult;
	}
	
	public Map<String, Object> postPEAllocTrans(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get dispatcher
		LocalDispatcher localDispatcher = dispatcher.getDispatcher();
		//Get parameters
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		try {
			GenericValue customPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			List<GenericValue> peCustomPeriods = delegator.findByAnd("PrepaidExpCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
			for(GenericValue item : peCustomPeriods) {
				//Create Trans
				GenericValue prepaidExp = delegator.findOne("PrepaidExp", UtilMisc.toMap("prepaidExpId", item.getString("prepaidExpId")), false);
				List<GenericValue> prepaidExpAllocList = delegator.findByAnd("PrepaidExpAlloc", UtilMisc.toMap("prepaidExpId", item.getString("prepaidExpId")), null, false);
				Map<String, Object> createTransCtx = FastMap.newInstance();
				createTransCtx.put("acctgTransTypeId", "PE_ALLOCATION");
				createTransCtx.put("description", customPeriod.get("description"));
				createTransCtx.put("glFiscalTypeId", "ACTUAL");
				createTransCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransCtx.put("userLogin", context.get("userLogin"));
				createTransCtx.put("transactionDate", new Timestamp(customPeriod.getDate("thruDate").getTime()));
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createTransCtx);
				
				//Create Trans
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				createTransEntryCtx.put("amount", item.getBigDecimal("amount"));
				createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				createTransEntryCtx.put("debitCreditFlag", "C");
				createTransEntryCtx.put("glAccountId", prepaidExp.getString("prepaidExpGlAccountId"));
				createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransEntryCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
				
				for (GenericValue peAlloc : prepaidExpAllocList) {
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", peAlloc.getBigDecimal("allocRate").multiply(item.getBigDecimal("amount").divide(BigDecimal.valueOf(100d))));
					createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", peAlloc.getString("allocGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
					createTransEntryCtx.put("userLogin", context.get("userLogin"));
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
				}
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				postTransCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
				
				BigDecimal remainingValue = prepaidExp.getBigDecimal("remainingValue") != null ? prepaidExp.getBigDecimal("remainingValue") : BigDecimal.valueOf(0);
				prepaidExp.put("remainingValue", remainingValue.subtract(item.getBigDecimal("amount")));
				prepaidExp.put("preAllocationDate", new Timestamp(UtilDateTime.addDaysToTimestamp(new Timestamp(customPeriod.getDate("thruDate").getTime()), 1).getTime()));
				prepaidExp.store();
			}
			customPeriod.put("isClosed", "Y");
			customPeriod.store();
		} catch (GenericEntityException | GenericServiceException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
}
