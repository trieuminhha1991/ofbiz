package com.olbius.basepos.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class WorkShiftJQServices {
	public static final String module = WorkShiftJQServices.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListWorkShift(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, Object> mapCondition = new HashMap<String, Object>();
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	mapCondition.put("organizationPartyId", parameters.get("organizationPartyId")[0]);
       	listAllConditions.add(EntityCondition.makeCondition(mapCondition));
		try {
			listIterator = delegator.find("PosWorkShiftAndOrg", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListWorkShift service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOtherIncome(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, Object> mapCondition = new HashMap<String, Object>();
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	EntityCondition condition= EntityCondition.makeCondition(UtilMisc.toMap("posTerminalStateId", parameters.get("posTerminalStateId")[0]));
       	try {
       		listIterator = delegator.find("WorkShiftInCome", condition , null, null, listSortFields, opts);
   		} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling jqGetListOtherIncome service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }

    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOtherCost(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, Object> mapCondition = new HashMap<String, Object>();
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	EntityCondition condition= EntityCondition.makeCondition(UtilMisc.toMap("posTerminalStateId", parameters.get("posTerminalStateId")[0]));
       	try {
       		listIterator = delegator.find("WorkShiftCost", condition , null, null, listSortFields, opts);
   		} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling jqGetListOtherCost service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }

}
