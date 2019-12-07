package com.olbius.basehr.workflow;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


public class WorkFlowUtils {

	public static String createWrokFlowGroup(Delegator delegator, String processId) throws GenericEntityException {
		GenericValue workFlowGroup = delegator.makeValue("WorkFlowGroup");
		String groupId = delegator.getNextSeqId("WorkFlowGroup");
		workFlowGroup.set("groupId", groupId);
		workFlowGroup.set("processId", processId);
		workFlowGroup.create();
		return groupId;
	}

	//TODO for test, maybe delete
	public static String createWrokFlowGroup(Delegator delegator, String processId, String idSuffix) throws GenericEntityException {
		GenericValue workFlowGroup = delegator.makeValue("WorkFlowGroup");
		String groupId = delegator.getNextSeqId("WorkFlowGroup");
		if(idSuffix != null){
			groupId += idSuffix;
		}
		workFlowGroup.set("groupId", groupId);
		workFlowGroup.set("processId", processId);
		workFlowGroup.create();
		return groupId;
	}

	public static void addMemberToGroup(Delegator delegator, List<String> listParty, String groupId) throws GenericEntityException {
		if(UtilValidate.isNotEmpty(listParty)){
			for(String partyId: listParty){
				GenericValue checkEtt = delegator.findOne("WorkFlowGroupMember", UtilMisc.toMap("groupId", groupId, "partyId", partyId), false);
				if(checkEtt == null){
					GenericValue workFlowGroupMember = delegator.makeValue("WorkFlowGroupMember");
					workFlowGroupMember.set("groupId", groupId);
					workFlowGroupMember.set("partyId", partyId);
					workFlowGroupMember.create();
				}
			}
		}
	}

	public static String createProcessState(Delegator delegator,
			String processId, String statusId, String description) throws GenericEntityException {
		GenericValue workFlowProcessStatus = delegator.makeValue("WorkFlowProcessStatus");
		workFlowProcessStatus.set("processId", processId);
		workFlowProcessStatus.set("statusId", statusId);
		workFlowProcessStatus.set("description", description);
		String processStatusId = delegator.getNextSeqId("WorkFlowProcessStatus");
		workFlowProcessStatus.set("processStatusId", processStatusId);
		workFlowProcessStatus.create();
		return processStatusId;
	}
	
	//TODO test, maybe delete
	public static String createProcessState(Delegator delegator,
			String processId, String statusId, String description, String idSuffix) throws GenericEntityException {
		GenericValue workFlowProcessStatus = delegator.makeValue("WorkFlowProcessStatus");
		workFlowProcessStatus.set("processId", processId);
		workFlowProcessStatus.set("statusId", statusId);
		workFlowProcessStatus.set("description", description);
		String processStatusId = delegator.getNextSeqId("WorkFlowProcessStatus");
		if(idSuffix != null){
			processStatusId += idSuffix;
		}
		workFlowProcessStatus.set("processStatusId", processStatusId);
		workFlowProcessStatus.create();
		return processStatusId;
	}

	public static String createTransition(Delegator delegator,
			String processId, String currState, String nextState) throws GenericEntityException {
		List<GenericValue> checkEtt = delegator.findByAnd("WorkFlowTransition", UtilMisc.toMap("processId", processId, "processStatusId", currState, "nextProcessStatusId", nextState), null, false);
		if(UtilValidate.isNotEmpty(checkEtt)){
			return checkEtt.get(0).getString("transitionId");
		}
		GenericValue workFlowTransition = delegator.makeValue("WorkFlowTransition");
		workFlowTransition.set("processId", processId);
		workFlowTransition.set("processStatusId", currState);
		workFlowTransition.set("nextProcessStatusId", nextState);
		String transitionId = delegator.getNextSeqId("WorkFlowTransition");
		workFlowTransition.set("transitionId", transitionId);
		workFlowTransition.create();
		return transitionId;
	}
	
	//TODO test, maybe delete
	public static String createTransition(Delegator delegator,
			String processId, String currState, String nextState, String suffix) throws GenericEntityException {
		List<GenericValue> checkEtt = delegator.findByAnd("WorkFlowTransition", UtilMisc.toMap("processId", processId, "processStatusId", currState, "nextProcessStatusId", nextState), null, false);
		if(UtilValidate.isNotEmpty(checkEtt)){
			return checkEtt.get(0).getString("transitionId");
		}
		GenericValue workFlowTransition = delegator.makeValue("WorkFlowTransition");
		workFlowTransition.set("processId", processId);
		workFlowTransition.set("processStatusId", currState);
		workFlowTransition.set("nextProcessStatusId", nextState);
		String transitionId = delegator.getNextSeqId("WorkFlowTransition");
		transitionId = transitionId + suffix;
		workFlowTransition.set("transitionId", transitionId);
		workFlowTransition.create();
		return transitionId;
	}

	public static String createWorkFlowAction(Delegator delegator,
			String processId, String actionTypeId, String name, String description) throws GenericEntityException {
		GenericValue workFlowAction = delegator.makeValue("WorkFlowAction");
		workFlowAction.set("processId", processId);
		workFlowAction.set("actionTypeId", actionTypeId);
		workFlowAction.set("name", name);
		workFlowAction.set("description", description);
		String actionId = delegator.getNextSeqId("WorkFlowAction");
		workFlowAction.set("actionId", actionId);
		workFlowAction.create();
		return actionId;
	}
	
	//TODO test, maybe delete
	public static String createWorkFlowAction(Delegator delegator,
			String processId, String actionTypeId, String name, String description, String suffix) throws GenericEntityException {
		GenericValue workFlowAction = delegator.makeValue("WorkFlowAction");
		workFlowAction.set("processId", processId);
		workFlowAction.set("actionTypeId", actionTypeId);
		workFlowAction.set("name", name);
		workFlowAction.set("description", description);
		String actionId = delegator.getNextSeqId("WorkFlowAction");
		actionId = actionId + suffix;
		workFlowAction.set("actionId", actionId);
		workFlowAction.create();
		return actionId;
	}

	public static void createTransitionAction(Delegator delegator,
			String transitionId, String actionId) throws GenericEntityException {
		if(transitionId == null || actionId == null){
			return;
		}
		GenericValue checkEntity = delegator.findOne("WorkFlowTransitionAction", UtilMisc.toMap("transitionId", transitionId, "actionId", actionId), false);
		if(checkEntity == null){
			GenericValue transitionAction = delegator.makeValue("WorkFlowTransitionAction");
			transitionAction.set("transitionId", transitionId);
			transitionAction.set("actionId", actionId);
			transitionAction.create();
		}
	}

	public static String createActionRoleType(Delegator delegator,
			String actionId, String roleTypeId, String groupId) throws GenericEntityException {
		List<GenericValue> checkEntt = delegator.findByAnd("WorkFlowActionRoleType", UtilMisc.toMap("actionId", actionId, "groupId", groupId, "roleTypeId", roleTypeId), null, false);
		if(UtilValidate.isEmpty(checkEntt)){
			GenericValue workFlowActionRoleType = delegator.makeValue("WorkFlowActionRoleType");
			workFlowActionRoleType.set("actionId", actionId);
			workFlowActionRoleType.set("groupId", groupId);
			workFlowActionRoleType.set("roleTypeId", roleTypeId);
			
			String actionRoleTypeId = delegator.getNextSeqId("WorkFlowActionRoleType");
			workFlowActionRoleType.set("actionRoleTypeId", actionRoleTypeId);
			workFlowActionRoleType.create();
			return actionRoleTypeId;
		}
		return null;
	}

	public static void createRequestAttr(Delegator delegator, String requestId,
			String attrName, String attrValue) throws GenericEntityException {
		GenericValue workFlowRequestAttr = delegator.makeValue("WorkFlowRequestAttr");
		workFlowRequestAttr.set("requestId", requestId);
		workFlowRequestAttr.set("attrName", attrName);
		workFlowRequestAttr.set("attrValue", attrValue);
		workFlowRequestAttr.create();
	}

	public static void createRequestAction(Delegator delegator,
			String requestId, String actionId, String transitionId,
			String isActive, String isComplete) throws GenericEntityException {
		//check if requestAction exists
		Map<String, Object> requestActionExistsMap = FastMap.newInstance();
		requestActionExistsMap.put("actionId", actionId);
		requestActionExistsMap.put("requestId", requestId);
		requestActionExistsMap.put("transitionId", transitionId);
		requestActionExistsMap.put("isActive", "Y");
		requestActionExistsMap.put("isComplete", "N");
		List<GenericValue> requestActionExists = delegator.findByAnd("WorkFlowRequestAction", requestActionExistsMap, null, false);
		if(UtilValidate.isEmpty(requestActionExists)){
			GenericValue workFlowRequestAction = delegator.makeValue("WorkFlowRequestAction");
			workFlowRequestAction.set("requestId", requestId);
			workFlowRequestAction.set("actionId", actionId);
			workFlowRequestAction.set("transitionId", transitionId);
			workFlowRequestAction.set("isActive", isActive);
			workFlowRequestAction.set("isComplete", isComplete);
			String requestActionId = delegator.getNextSeqId("WorkFlowRequestAction");
			workFlowRequestAction.set("requestActionId", requestActionId);
			workFlowRequestAction.create();
		}
	}

	public static void createWorkFlowRequestStakeHolder(Delegator delegator,
			String requestId, Set<String> listParty) throws GenericEntityException {
		for(String partyId: listParty){
			GenericValue checkEtt = delegator.findOne("WorkFlowRequestStakeHolder", UtilMisc.toMap("partyId", partyId, "requestId", requestId), false);
			if(checkEtt == null){
				GenericValue workFlowRequestStakeHolder = delegator.makeValue("WorkFlowRequestStakeHolder");
				workFlowRequestStakeHolder.set("partyId", partyId);
				workFlowRequestStakeHolder.set("requestId", requestId);
				workFlowRequestStakeHolder.create();
			}
		}
	}

	public static String createWorkFlowActivity(Delegator delegator,
			String processId, String activityTypeId, String name, String description) throws GenericEntityException {
		GenericValue workFlowActivity = delegator.makeValue("WorkFlowActivity");
		workFlowActivity.set("processId", processId);
		workFlowActivity.set("activityTypeId", activityTypeId);
		workFlowActivity.set("name", name);
		workFlowActivity.set("description", description);
		String activityId = delegator.getNextSeqId("WorkFlowActivity");
		workFlowActivity.set("activityId", activityId);
		workFlowActivity.create();
		return activityId;
	}

	public static String createActivityRoleType(Delegator delegator,
			String activityId, String roleTypeId,
			String groupId) throws GenericEntityException {
		List<GenericValue> checkEtt = delegator.findByAnd("WorkFlowActivityRoleType", UtilMisc.toMap("activityId", activityId, "roleTypeId", roleTypeId, "groupId", groupId), null, false);
		if(UtilValidate.isEmpty(checkEtt)){
			GenericValue workFlowActivityRoleType = delegator.makeValue("WorkFlowActivityRoleType");
			workFlowActivityRoleType.set("activityId", activityId);
			workFlowActivityRoleType.set("groupId", groupId);
			workFlowActivityRoleType.set("roleTypeId", roleTypeId);
			String activityRoleTypeId = delegator.getNextSeqId("WorkFlowActivityRoleType");
			workFlowActivityRoleType.set("activityRoleTypeId", activityRoleTypeId);
			workFlowActivityRoleType.create();
			return activityRoleTypeId;
		}
		return null;
	}

	public static void createStateActivity(Delegator delegator,
			String activityId, String stateId) throws GenericEntityException {
		GenericValue checkEtt = delegator.findOne("WorkFlowStatusActivity", UtilMisc.toMap("activityId", activityId, "processStatusId", stateId), false);
		if(checkEtt == null){
			GenericValue workFlowStatusActivity = delegator.makeValue("WorkFlowStatusActivity");
			workFlowStatusActivity.set("activityId", activityId);
			workFlowStatusActivity.set("processStatusId", stateId);
			workFlowStatusActivity.create();
		}
	}

	public static void createActivityAction(Delegator delegator,
			String activityId, String actionId) throws GenericEntityException {
		if(actionId == null || activityId == null){
			return;
		}
		GenericValue checkEtt = delegator.findOne("WorkFlowActivityAction", UtilMisc.toMap("activityId", activityId, "actionId", actionId), false);
		if(checkEtt == null){
			GenericValue workFlowActivityAction = delegator.makeValue("WorkFlowActivityAction");
			workFlowActivityAction.set("activityId", activityId);
			workFlowActivityAction.set("actionId", actionId);
			workFlowActivityAction.create();
		}
	}


	public static void executeActivity(LocalDispatcher dispatcher, Delegator delegator,
			GenericValue activity, String requestId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		List<GenericValue> workFlowActivityRoleType = delegator.findByAnd("WorkFlowActivityRoleType", UtilMisc.toMap("activityId", activity.getString("activityId")), null, false);
		for(GenericValue activityRoleType: workFlowActivityRoleType){
			if("SEND_NOTIFY".equals(activity.getString("activityTypeId"))){
				String groupId = activityRoleType.getString("groupId");
				List<GenericValue> workFlowGroupMember = delegator.findByAnd("WorkFlowGroupMember", UtilMisc.toMap("groupId", groupId), null, false);
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(workFlowGroupMember, "partyId", false);
				if(UtilValidate.isNotEmpty(partyIdList)){
					GenericValue requestAttrTarget = delegator.findOne("WorkFlowRequestAttr", UtilMisc.toMap("requestId", requestId, "attrName", "targetLink"), false);
					GenericValue requestAttrAction = delegator.findOne("WorkFlowRequestAttr", UtilMisc.toMap("requestId", requestId, "attrName", "action"), false);
					String targetLink = "requestId=" + requestId + ";" + requestAttrTarget.getString("attrValue");
					Map<String, Object> ntfCtx = FastMap.newInstance();
					ntfCtx.put("partiesList", partyIdList);
					ntfCtx.put("header", activity.getString("description"));
					ntfCtx.put("targetLink", targetLink);
					ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
					ntfCtx.put("action", requestAttrAction.getString("attrValue"));
					ntfCtx.put("ntfType", "MANY");
					ntfCtx.put("userLogin", userLogin);
					ntfCtx.put("sendToSender", "Y");
					dispatcher.runSync("createNotification", ntfCtx);
				}
			}else if("ADD_STAKEHOLDER".equals(activity.getString("activityTypeId"))){
				String groupId = activityRoleType.getString("groupId");
				List<GenericValue> workFlowGroupMember = delegator.findByAnd("WorkFlowGroupMember", UtilMisc.toMap("groupId", groupId), null, false);
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(workFlowGroupMember, "partyId", false);
				Set<String> stakeHolder = FastSet.newInstance();
				stakeHolder.addAll(partyIdList);
				WorkFlowUtils.createWorkFlowRequestStakeHolder(delegator, requestId, stakeHolder);
			}
		}
	}

	public static void executeActivityByState(LocalDispatcher dispatcher,
			Delegator delegator, String requestId, String state, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		List<GenericValue> processStatusActivity = delegator.findByAnd("WorkFlowStatusActivity", UtilMisc.toMap("processStatusId", state), null, false);
		for(GenericValue temp: processStatusActivity){
			GenericValue activity = delegator.findOne("WorkFlowActivity", UtilMisc.toMap("activityId", temp.getString("activityId")), false);
			executeActivity(dispatcher, delegator, activity, requestId, userLogin);
		}
	}

	public static void updateRequestAction(Delegator delegator,
			String currState, String processId, String requestId, String groupId) throws GenericEntityException {
		List<GenericValue> transitionList = delegator.findByAnd("WorkFlowTransition", 
				UtilMisc.toMap("processId", processId, "processStatusId", currState), null, false);
		Map<String, Object> condMap = FastMap.newInstance();
		boolean disableOldAction = false;
		for(GenericValue transition: transitionList){
			condMap.clear();
			String transitionId = transition.getString("transitionId");
			condMap.put("transitionId", transitionId);
			condMap.put("requestId", requestId);
			condMap.put("isComplete", "N");
			List<GenericValue> requestAction = delegator.findByAnd("WorkFlowRequestAction", condMap, null, false);
			if(UtilValidate.isEmpty(requestAction)){
				// If all RequestActions with 'transitionId' are marked as Completed, then we disable all remaining actions 
				disableOldAction = true;
				break;
			}
		}
		if(disableOldAction){
			List<String> transitionIdList = EntityUtil.getFieldListFromEntityList(transitionList, "transitionId", true);
			List<EntityCondition> disableConds = FastList.newInstance();
			disableConds.add(EntityCondition.makeCondition("requestId", requestId));
			disableConds.add(EntityCondition.makeCondition("isActive", "Y"));
			disableConds.add(EntityCondition.makeCondition("transitionId", EntityJoinOperator.IN, transitionIdList));
			List<GenericValue> disableRequestAction = delegator.findList("WorkFlowRequestAction", EntityCondition.makeCondition(disableConds), null, null, null, false);
			for(GenericValue reqAct: disableRequestAction){
				reqAct.set("isActive", "N");
				reqAct.store();
			}
		}
		
		//disable all action of group
		if(groupId != null){
			List<GenericValue> workFlowActionRoleType = delegator.findByAnd("WorkFlowActionRoleType", UtilMisc.toMap("groupId", groupId), null, false);
			List<String> actionIdList = EntityUtil.getFieldListFromEntityList(workFlowActionRoleType, "actionId", false);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("requestId", requestId));
			conds.add(EntityCondition.makeCondition("isActive", "Y"));
			conds.add(EntityCondition.makeCondition("actionId", EntityJoinOperator.IN, actionIdList));
			List<GenericValue> workFlowRequestAction = delegator.findList("WorkFlowRequestAction", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue reqAct: workFlowRequestAction){
				reqAct.set("isActive", "N");
				reqAct.store();
			}
		}
	}
	
	public static Map<String, Object> executeAction(LocalDispatcher dispatcher,
			Delegator delegator, GenericValue userLogin, Locale locale,
			String requestId, String processId, String actionId, 
			String currStateRequest, String actionTypeId, String groupId) throws GenericServiceException, GenericEntityException {
		List<GenericValue> requestActionList = delegator.findByAnd("WorkFlowRequestAction", UtilMisc.toMap("actionId", actionId, "requestId", requestId, 
				"isActive", "Y", "isComplete", "N"), null, false);
		if(UtilValidate.isNotEmpty(requestActionList)){
			dispatcher.runSync("executeActivityAction", UtilMisc.toMap("actionId", actionId, "requestId", requestId, "userLogin", userLogin));
			for(GenericValue requestAction: requestActionList){
				String transitionId = requestAction.getString("transitionId");
				if(transitionId != null){
					GenericValue transition = delegator.findOne("WorkFlowTransition", UtilMisc.toMap("transitionId", transitionId), false);
					String nextState = transition.getString("nextProcessStatusId");
					String currState = transition.getString("processStatusId");
					requestAction.set("isActive", "N");
					requestAction.set("isComplete", "Y");
					requestAction.store();
					WorkFlowUtils.updateRequestAction(delegator, currState, processId, requestId, groupId);
					
					//get transition and add new entry to requestAction if request change state
					if(!nextState.equals(currStateRequest)){
						dispatcher.runSync("updateWorkFlowRequest", UtilMisc.toMap("requestId", requestId, "processStatusId", nextState, "userLogin", userLogin));
						List<GenericValue> nextTransitionList = delegator.findByAnd("WorkFlowTransition", UtilMisc.toMap("processId", processId, "processStatusId", nextState), null, false);
						for(GenericValue tempTransition: nextTransitionList){
							String tempTransitionId = tempTransition.getString("transitionId");
							List<GenericValue> tempActionList = delegator.findByAnd("WorkFlowTransitionAction", UtilMisc.toMap("transitionId", tempTransitionId), null, false);
							for(GenericValue tempAction: tempActionList){
								WorkFlowUtils.createRequestAction(delegator, requestId, tempAction.getString("actionId"), tempTransitionId, "Y", "N");								
							}
						}
					}
				}
			}
			return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale));
		}else{
			GenericValue currenRequestProcessStt = delegator.findOne("WorkFlowProcessStatus", UtilMisc.toMap("processStatusId", currStateRequest), false);
			GenericValue actionType = delegator.findOne("WorkFlowActionType", UtilMisc.toMap("actionTypeId", actionTypeId), false);
			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotApplyActionTypeInRequestState", 
					UtilMisc.toMap("currentReqState", currenRequestProcessStt.getString("description"), "actionTypeId", actionType.getString("description")), locale));
		}
	}
	
	public static boolean checkApprvalPerm(Delegator delegator, String partyId, String requestId) throws GenericEntityException{
		GenericValue requestGv = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
		String processId = requestGv.getString("processId");		
		List<GenericValue> groupFlowList = delegator.findByAnd("WorkFlowGroup", UtilMisc.toMap("processId", processId), null, false);
		List<String> groupIdList = EntityUtil.getFieldListFromEntityList(groupFlowList, "groupId", true);
		EntityCondition groupConds = EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupIdList);
		List<GenericValue> groupOfPartyList = delegator.findList("WorkFlowGroupMember", EntityCondition.makeCondition(
																											EntityCondition.makeCondition("partyId", partyId),
																											EntityJoinOperator.AND,
																											groupConds), null, null, null, false);
		List<String> groupIdOfParty = EntityUtil.getFieldListFromEntityList(groupOfPartyList, "groupId", true);
		List<GenericValue> workFlowActionRoleType = delegator.findList("WorkFlowActionRoleType", EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupIdOfParty), null, null, null, false);
		List<String> actionIdList = EntityUtil.getFieldListFromEntityList(workFlowActionRoleType, "actionId", true);
		
		List<EntityCondition> requestActionConds = FastList.newInstance();
		requestActionConds.add(EntityCondition.makeCondition("actionId", EntityJoinOperator.IN, actionIdList));
		requestActionConds.add(EntityCondition.makeCondition("isActive", "Y"));
		requestActionConds.add(EntityCondition.makeCondition("requestId", requestId));
		List<GenericValue> requestAction = delegator.findList("WorkFlowRequestAction", EntityCondition.makeCondition(requestActionConds), null, null, null, false);
		if(UtilValidate.isNotEmpty(requestAction)){
			return true;
		}
		return false;
	}

	public static void buildWorkFlow(Delegator delegator, String processId,
			String requestId, Map<String, Integer> partyWorkFlowLevel,
			String startState, String completeState) throws GenericEntityException {
		ValueComparator comparator = new ValueComparator(partyWorkFlowLevel);
		TreeMap<String, Integer> sortMap = new TreeMap<String, Integer>(comparator);
		sortMap.putAll(partyWorkFlowLevel);
		String prevState = startState;
		String lastKey = sortMap.lastKey();
		String firstKey = sortMap.firstKey();
		for(Map.Entry<String, Integer> entry: sortMap.entrySet()){
			String partyId = entry.getKey();
			String groupId = WorkFlowUtils.createWrokFlowGroup(delegator, processId);
			WorkFlowUtils.addMemberToGroup(delegator, UtilMisc.toList(partyId), groupId);
			String actionAppr = WorkFlowUtils.createWorkFlowAction(delegator, processId, "APPROVE", null, "approve by " + partyId, "ApprBy" + partyId);
			String actionDenied = WorkFlowUtils.createWorkFlowAction(delegator, processId, "DENY", null, "denied by " + partyId, "DeniedBy" + partyId);
			String apprState = null;
			if(partyId.equals(lastKey)){
				apprState = completeState;
			}else{
				apprState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_APPROVE", "ApprStateBy" + partyId, "ApprStt" + partyId);
			}
			String deniedState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_DENIED", "DeniedStateBy" + partyId, "DeniedStt" + partyId);
			String transitionPrevStateToAppr = WorkFlowUtils.createTransition(delegator, processId, prevState, apprState, "Appr" + partyId);
			String transitionPrevStateToDenied = WorkFlowUtils.createTransition(delegator, processId, prevState, deniedState, "Denied" + partyId);
			WorkFlowUtils.createTransitionAction(delegator, transitionPrevStateToAppr, actionAppr);
			WorkFlowUtils.createTransitionAction(delegator, transitionPrevStateToDenied, actionDenied);
			WorkFlowUtils.createActionRoleType(delegator, actionAppr, null, groupId);
			WorkFlowUtils.createActionRoleType(delegator, actionDenied, null, groupId);
			String activitySendNotify = WorkFlowUtils.createWorkFlowActivity(delegator, processId, "SEND_NOTIFY", 
					"send notify to " + partyId, "Approval termination application");
			WorkFlowUtils.createActivityRoleType(delegator, activitySendNotify, null, groupId);
			WorkFlowUtils.createStateActivity(delegator, activitySendNotify, prevState);
			if(!partyId.equals(lastKey)){
				String activityAddPartyStakeHolder = WorkFlowUtils.createWorkFlowActivity(delegator, processId, 
						"ADD_STAKEHOLDER", "add " + partyId + " to stakeholder", "add " + partyId + " to stakeholder");
				WorkFlowUtils.createActivityRoleType(delegator, activityAddPartyStakeHolder, null, groupId);
				WorkFlowUtils.createActivityAction(delegator, activityAddPartyStakeHolder, actionAppr);
			}
			if(firstKey.equals(partyId)){
				WorkFlowUtils.createRequestAction(delegator, requestId, actionAppr, transitionPrevStateToAppr, "Y", "N");
				WorkFlowUtils.createRequestAction(delegator, requestId, actionDenied, transitionPrevStateToDenied, "Y", "N");
			}
			prevState = apprState;
		}
	}
}
