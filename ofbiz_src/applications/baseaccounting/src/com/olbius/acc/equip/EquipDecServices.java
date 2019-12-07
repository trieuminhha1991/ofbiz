package com.olbius.acc.equip;

import com.olbius.acc.equip.entity.AllocatedEquipment;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastMap;
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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class EquipDecServices {
	
	public static final String MODULE = EquipDecServices.class.getName();
	
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> getListEquipDecrements(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("decrementTypeId", "EQUIP_DECREMENT"));
    	try {
    		listIterator = delegator.find("Decrement", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling JqxGetListEquipDecrements service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> getListDecreasedEquipments(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String decrementId = parameters.get("decrementId")[0];
    		List<GenericValue> listFA = delegator.findByAnd("EquipmentDecrementItem", UtilMisc.toMap("decrementId", decrementId), null, false);
    		Set<String> equipSet = new HashSet<String>();
    		for(GenericValue item : listFA) {
    			equipSet.add(item.getString("equipmentId"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.IN, equipSet));
    		listIterator = delegator.find("Equipment", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling JqxGetListDecreasedEquipments service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListAVLDecEquips(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<AllocatedEquipment> listAllocEquips = new ArrayList<AllocatedEquipment>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listIterator = delegator.find("Equipment", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		for(GenericValue item: listIterator.getCompleteList()) {
    			List<GenericValue> listEquipments = delegator.findByAnd("EquipmentAllocation", UtilMisc.toMap("equipmentId", item.getString("equipmentId")), null, false);
    			GenericValue equip = listEquipments.get(0);
    			AllocatedEquipment allocEquip = new AllocatedEquipment();
    			allocEquip.setEquipmentId(item.getString("equipmentId"));
    			allocEquip.setEquipmentName(item.getString("equipmentName"));
    			allocEquip.setAmount(equip.getBigDecimal("remainingValue"));
    			listAllocEquips.add(allocEquip);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListAVLDecEquips service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}finally{
			try {
				listIterator.close();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
    	successResult.put("listIterator", listAllocEquips);
    	return successResult;
	}
	
	public Map<String, Object> postEquipDecTrans(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		LocalDispatcher localDispatcher = dispatcher.getDispatcher();
		String decrementId = (String) context.get("decrementId");
		try {
			GenericValue decrement = delegator.findOne("Decrement", UtilMisc.toMap("decrementId", decrementId), false);
			List<GenericValue> equipDecrementList = delegator.findByAnd("EquipmentDecrementItem", UtilMisc.toMap("decrementId", decrementId), null, false);
			for(GenericValue item : equipDecrementList) {
				//Create Trans
				Map<String, Object> createTransCtx = FastMap.newInstance();
				createTransCtx.put("amount", item.getBigDecimal("amount"));
				createTransCtx.put("description", decrement.get("description"));
				createTransCtx.put("debitGlAccountId", "632");
				//FIXME fix glAccountId
				createTransCtx.put("creditGlAccountId", "153");
				createTransCtx.put("glFiscalTypeId", "ACTUAL");
				createTransCtx.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
				createTransCtx.put("acctgTransTypeId", "EQUIP_DECREMENT");
				createTransCtx.put("userLogin", context.get("userLogin"));
				createTransCtx.put("transactionDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				Map<String, Object> createTransRs = localDispatcher.runSync("quickCreateAcctgTransAndEntries", createTransCtx);
				
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", createTransRs.get("acctgTransId"));
				postTransCtx.put("userLogin", context.get("userLogin"));
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
			}
			decrement.put("isClosed", "Y");
			decrement.store();
		} catch (GenericEntityException | GenericServiceException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
}
