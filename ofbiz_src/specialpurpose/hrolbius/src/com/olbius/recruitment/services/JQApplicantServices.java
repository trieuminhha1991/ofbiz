package com.olbius.recruitment.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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

public class JQApplicantServices {
public static final String module = JQApplicantServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListApplicant(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters =(Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortId", parameters.get("workEffortId")[0]);
		mapCondition.put("roleTypeId", "APPLICANT");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("WorkEffortApplAssignView", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListApplicant service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSelectedApplicant(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters =(Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortId", parameters.get("workEffortId")[0]);
		EntityCondition statusCon = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("PROB_INIT"));
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(statusCon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("OfferProbationView", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListSelectedApplicant service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListInductedApplicant(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters =(Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortId", parameters.get("workEffortId")[0]);
		/*EntityCondition statusCon = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("PROB_APPROVED", "PROB_PROB_AGR"));*/
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		/*listAllConditions.add(statusCon);*/
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("OfferProbationView", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListInductedApplicant service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
    public static Map<String, Object> getRecruitmentRound(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = null;
    	try {
			List<GenericValue> listChildWorkEffort = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", context.get("workEffortId")), null, false);
			if(UtilValidate.isEmpty(listChildWorkEffort)) {
				List<GenericValue>	listWorkEffort = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", context.get("workEffortId")),null, false);
				List<GenericValue>	listWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", context.get("workEffortId")),null, false);
		    	result = ServiceUtil.returnSuccess();
		    	result.put("workEffortName", EntityUtil.getFirst(listWorkEffort).getString("workEffortName"));
		    	result.put("availabilityStatusId", EntityUtil.getFirst(listWorkEffortPartyAss).getString("availabilityStatusId"));
			}else {
				List<GenericValue>	listWorkEffort = delegator.findByAnd("WorkEffortPartyAssignmentToView", UtilMisc.toMap("workEffortIdFrom", context.get("workEffortId"), "partyId", context.get("partyId")),null, false);
				if(UtilValidate.isEmpty(listWorkEffort)) {
					result = ServiceUtil.returnSuccess();
			    	result.put("workEffortName", "");
			    	result.put("availabilityStatusId", "");
				}else {
					result = ServiceUtil.returnSuccess();
			    	result.put("workEffortName", EntityUtil.getFirst(listWorkEffort).getString("workEffortName"));
			    	result.put("availabilityStatusId", EntityUtil.getFirst(listWorkEffort).getString("availabilityStatusId"));
				}
			}
    	} catch (GenericEntityException e1) {
			Debug.logError(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError(e1.getMessage());
		}
    	return result;
    }
	
}
