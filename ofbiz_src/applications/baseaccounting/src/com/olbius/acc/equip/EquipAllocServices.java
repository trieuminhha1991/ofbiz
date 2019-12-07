package com.olbius.acc.equip;

import com.olbius.acc.equip.entity.AllocatedEquipment;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EquipAllocServices {
	
	public static final String MODULE = EquipAllocServices.class.getName();
	
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> getListEquipAllocs(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("periodTypeId", "ALLOC_MONTH"));
    	try {
    		listIterator = delegator.find("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling JqxGetListEquipAllocs service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> getListAllowEquips(DispatchContext dispatcher, Map<String, Object> context){
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
    		List<GenericValue> listEquips = delegator.findByAnd("EquipmentCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
    		Set<String> equipSet = new HashSet<String>();
    		for(GenericValue item : listEquips) {
    			equipSet.add(item.getString("equipmentId"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.IN, equipSet));
    		listIterator = delegator.find("Equipment", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListDepAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAVLEquips(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator 
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<AllocatedEquipment> listAllocEquips = new ArrayList<AllocatedEquipment>();
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String customTimePeriodId = parameters.get("customTimePeriodId")[0];
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    		
    		listIterator = delegator.find("Equipment", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		for(GenericValue item: listIterator.getCompleteList()) {
    			List<GenericValue> listEquipments = delegator.findByAnd("EquipmentAllocation", UtilMisc.toMap("equipmentId", item.getString("equipmentId")), null, false);
    			GenericValue equip = listEquipments.get(0);
    			AllocatedEquipment allocEquip = new AllocatedEquipment();
    			allocEquip.setEquipmentId(item.getString("equipmentId"));
    			allocEquip.setEquipmentName(item.getString("equipmentName"));
    			//TODO
    			Timestamp dateAcquired = equip.getTimestamp("preAllocationDate");
				Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
				Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
				if(dateAcquired !=null && dateAcquired.after(fromDate)) {
    				int dayNumber = UtilDateTime.getIntervalInDays(dateAcquired, thruDate) + 1;
    				int daysInMonth = UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1;
    				Double monthlyAllocAmount = 0d;
    				if(equip.getBigDecimal("monthlyAllocAmount") != null) {
    					monthlyAllocAmount = equip.getBigDecimal("monthlyAllocAmount").doubleValue();
    				}
    				allocEquip.setAmount(BigDecimal.valueOf(monthlyAllocAmount*dayNumber/daysInMonth));
    			}else {
    				allocEquip.setAmount(equip.getBigDecimal("monthlyAllocAmount"));
    			}
    			listAllocEquips.add(allocEquip);
    		}
    	} catch (Exception e) {
    		ErrorUtils.processException(e, MODULE);
		}finally {
			if(listIterator != null) {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					ErrorUtils.processException(e, MODULE);
				}
			}
		}
    	successResult.put("listIterator", listAllocEquips);
    	return successResult;
	}
	
	public Map<String, Object> postEquipAllocTrans(DispatchContext dispatcher, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get dispatcher
		LocalDispatcher localDispatcher = dispatcher.getDispatcher();
		//Get parameters
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		try {
			GenericValue customPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			List<GenericValue> eqipCustomPeriods = delegator.findByAnd("EquipmentCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), null, false);
			for(GenericValue item : eqipCustomPeriods) {
				
				Map<String, Object> createTransCtx = FastMap.newInstance();
				createTransCtx.put("acctgTransTypeId", "ALLOCATION");
				createTransCtx.put("description", customPeriod.get("description"));
				createTransCtx.put("glFiscalTypeId", "ACTUAL");
				createTransCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransCtx.put("userLogin", context.get("userLogin"));
				createTransCtx.put("transactionDate", new Timestamp(customPeriod.getDate("thruDate").getTime()));
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createTransCtx);
				
				//Create Trans
				List<GenericValue> listAllocs = delegator.findByAnd("EquipmentAllocation", UtilMisc.toMap("equipmentId", item.get("equipmentId")), null, false);
				GenericValue alloc = listAllocs.get(0);
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				createTransEntryCtx.put("amount", item.getBigDecimal("amount"));
				createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				createTransEntryCtx.put("debitCreditFlag", "C");
				if(alloc.getString("alloTimes").toString().equals("1")) {
					createTransEntryCtx.put("glAccountId", "153");
				}else {
					createTransEntryCtx.put("glAccountId", "242");
				}
				createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransEntryCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
				
				List<GenericValue> listEquipPartyAllocs = delegator.findByAnd("EquipmentPartyAlloc", UtilMisc.toMap("equipmentId", item.get("equipmentId")), null, false);
				for(GenericValue partyAlloc : listEquipPartyAllocs) {
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", partyAlloc.getBigDecimal("allocRate").multiply(item.getBigDecimal("amount").divide(BigDecimal.valueOf(100d))));
					createTransEntryCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", partyAlloc.getString("allocGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
					createTransEntryCtx.put("userLogin", context.get("userLogin"));
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
				}
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				postTransCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
				
				//Update 
				BigDecimal allocatedAmount = alloc.getBigDecimal("allocatedAmount") != null ? alloc.getBigDecimal("allocatedAmount") : BigDecimal.valueOf(0);
				BigDecimal remainingValue = alloc.getBigDecimal("remainingValue") != null ? alloc.getBigDecimal("remainingValue") : BigDecimal.valueOf(0);
				alloc.put("allocatedAmount", allocatedAmount.add(item.getBigDecimal("amount")));
				alloc.put("remainingValue", remainingValue.subtract(item.getBigDecimal("amount")));
				alloc.put("preAllocationDate", new Timestamp(UtilDateTime.addDaysToTimestamp(new Timestamp(customPeriod.getDate("thruDate").getTime()), 1).getTime()));
				alloc.store();
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
