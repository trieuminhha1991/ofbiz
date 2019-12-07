package com.olbius.basehr.workflow;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class WorkFlowService {
	public static Map<String, Object> createWorkFlowRequest(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		GenericValue workFlowRequest = delegator.makeValue("WorkFlowRequest");
		workFlowRequest.setNonPKFields(context);
		String requestId = delegator.getNextSeqId("WorkFlowRequest");
		workFlowRequest.set("requestId", requestId);
		workFlowRequest.create();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		retMap.put("requestId", requestId);
		return retMap;
	}
	
	public static Map<String, Object> createWorkFlowProcess(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue workFlowProcess = delegator.makeValue("WorkFlowProcess");
		workFlowProcess.setNonPKFields(context);
		String processId = delegator.getNextSeqId("WorkFlowProcess");
		workFlowProcess.set("processId", processId);
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			workFlowProcess.create();
			retMap.put("processId", processId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> approvalWorkFlowRequest(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}
		String requestId = (String)context.get("requestId");
		String actionTypeId = (String)context.get("actionTypeId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale));
		try {
			GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
			String processId = workFlowRequest.getString("processId");
			String currStateRequest = workFlowRequest.getString("processStatusId");
			String requesterId = workFlowRequest.getString("partyId");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("actionTypeId", actionTypeId));
			conds.add(EntityCondition.makeCondition("processId", processId));
			List<GenericValue> listWorkFlowAction = delegator.findList("WorkFlowAction", EntityCondition.makeCondition(conds), null, null, null, false);
			List<String> actionIdList = EntityUtil.getFieldListFromEntityList(listWorkFlowAction, "actionId", true);
			
			List<GenericValue> groupListGv = delegator.findByAnd("WorkFlowGroup", UtilMisc.toMap("processId", processId), null, false);
			List<String> groupList = EntityUtil.getFieldListFromEntityList(groupListGv, "groupId", true);
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupList));
			List<GenericValue> groupMember = delegator.findList("WorkFlowGroupMember", EntityCondition.makeCondition(conds), null, null, null, false);
			
			if(UtilValidate.isNotEmpty(groupMember) && !"RESTART".equals(actionTypeId)){
				String groupId = groupMember.get(0).getString("groupId");
				conds.clear();
				conds.add(EntityCondition.makeCondition("groupId", groupId));
				conds.add(EntityCondition.makeCondition("actionId", EntityJoinOperator.IN, actionIdList));
				List<GenericValue> actionRoleTypeList = delegator.findList("WorkFlowActionRoleType", EntityCondition.makeCondition(conds), null, null, null, false);
				if(UtilValidate.isNotEmpty(actionRoleTypeList)){
					String actionId = actionRoleTypeList.get(0).getString("actionId");
					retMap = WorkFlowUtils.executeAction(dispatcher, delegator, userLogin, locale, 
							requestId, processId, actionId, currStateRequest, actionTypeId, groupId);
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "GroupCannotPerformAction", locale));
				}
			}else if("RESTART".equals(actionTypeId) && requesterId.equals(partyId)){
				String actionId = actionIdList.get(0);
				retMap = WorkFlowUtils.executeAction(dispatcher, delegator, userLogin, locale, requestId, 
						processId, actionId, currStateRequest, actionTypeId, null);
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "NotBelongGroupApprove", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> executeActivityAction(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String actionId = (String)context.get("actionId");
		String requestId = (String)context.get("requestId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<GenericValue> workFlowActivityAction = delegator.findByAnd("WorkFlowActivityAction", UtilMisc.toMap("actionId", actionId), null, false);
			for(GenericValue activityAction: workFlowActivityAction){
				String activityId = activityAction.getString("activityId");
				GenericValue activity = delegator.findOne("WorkFlowActivity", UtilMisc.toMap("activityId", activityId), false);
				WorkFlowUtils.executeActivity(dispatcher, delegator, activity, requestId, userLogin);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateWorkFlowRequest(DispatchContext dctx, Map<String, Object> context){
		String requestId = (String)context.get("requestId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
			if(workFlowRequest == null){
				return ServiceUtil.returnError("cannot find request");
			}
			workFlowRequest.setNonPKFields(context);
			workFlowRequest.store();
			WorkFlowUtils.executeActivityByState(dispatcher, delegator, requestId, workFlowRequest.getString("processStatusId"), userLogin);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}	
}
