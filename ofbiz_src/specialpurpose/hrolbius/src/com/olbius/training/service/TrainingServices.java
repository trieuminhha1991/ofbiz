
package com.olbius.training.service;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.employee.services.EmployeeServices;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TrainingServices {

	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String inviteEmail = "InviteEmailLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	
	/*
	 * 
	 * Create Training Plan 
	 * @param dpctx
	 * @param context
	 * @throw Exception
	 * 
	 * */
	/*new service training*/
	
	public static Map<String, Object> createTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String estimatedFromDateStr = (String)context.get("estimatedFromDateLong");
		String estimatedThruDateStr = (String)context.get("estimatedThruDateLong");
		String uomId = (String)context.get("uomId");
		GenericValue userLogin = (GenericValue)context.get("userLogin"); 
		String emplPositionTypes = (String)context.get("emplPositionTypes");
		String skillTypes = (String)context.get("skillTypes");
		String partyIds = (String)context.get("partyIds");
		Locale locale = (Locale)context.get("locale");
		if(uomId == null){
			uomId = "VND";
		}
		Map<String, Object> retMap = FastMap.newInstance();
		
		Timestamp estimatedThruDate = null;
		Timestamp estimatedFromDate = null;
		String trainingCourseId = (String)context.get("trainingCourseId");
		if(!CommonUtil.checkValidStringId(trainingCourseId)){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "TrainingCourseIdContainInvalidChar", locale));
		}
		String statusId = (String)context.get("statusId");
		if(estimatedFromDateStr != null){
			estimatedFromDate = new Timestamp(Long.parseLong(estimatedFromDateStr));
		}
		if(estimatedThruDateStr != null){
			estimatedThruDate = new Timestamp(Long.parseLong(estimatedThruDateStr));
		}
		GenericValue trainingCourse = delegator.makeValue("TrainingCourse");
		trainingCourse.setAllFields(context, false, null, null);
		trainingCourse.set("estimatedFromDate", estimatedFromDate);
		trainingCourse.set("estimatedThruDate", estimatedThruDate);
		trainingCourse.set("uomId", uomId);
		if(statusId == null){
			statusId = "TRAINING_PLANNED";
			trainingCourse.set("statusId", statusId);
		}
		if(trainingCourseId == null){
			trainingCourseId = delegator.getNextSeqId("TrainingCourse");
			trainingCourse.set("trainingCourseId", trainingCourseId);
		}
		try {
			trainingCourse.create();
			if(skillTypes != null){
				JSONArray skillTypeJson = JSONArray.fromObject(skillTypes);
				if(skillTypeJson != null && skillTypeJson.size() > 0){
					dispatcher.runSync("addSkillTypeToTrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId, "skillTypes", skillTypes, "userLogin", userLogin));
				}
			}
			dispatcher.runSync("updateTrainingCourseTraineed", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyIds", partyIds, "emplPositionTypes", emplPositionTypes,"userLogin", userLogin));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", (Locale)context.get("locale")));
		return retMap;
	}
	
	public static Map<String, Object> createTrainingCourseProposal(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String trainingCourseId = (String)context.get("trainingCourseId");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> resultService = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		try {
			resultService = dispatcher.runSync("createEmplProposal", UtilMisc.toMap("emplProposalTypeId", "TRAIN_PLAN_PPSL", 
					"statusId", "PPSL_CREATED", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
				trainingCourse.set("statusId", "TRAINING_PLANNED_PPS");
				trainingCourse.store();
				String emplProposalId = (String)resultService.get("emplProposalId");
				GenericValue trainingCoursePoposal = delegator.makeValue("EmplTrainingCourseProposal");
				trainingCoursePoposal.set("emplProposalId", emplProposalId);
				trainingCoursePoposal.set("trainingCourseId", trainingCourseId);
				trainingCoursePoposal.create();
				String ceo = PartyUtil.getCEO(delegator);
				String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", hrmAdmin, 
						"roleTypeId", "PPSL_CONFIRMER", "userLogin", userLogin));
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", ceo, 
						"roleTypeId", "PPSL_DECIDER", "userLogin", userLogin));
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("header", UtilProperties.getMessage("TrainingUiLabels", "ApprovalTrainingPlanProposal", UtilMisc.toMap("proposer", PartyUtil.getPersonName(delegator, userLogin.getString("partyId"))), locale));
				ntfCtx.put("action", "ApprovalTrainingProposal");
				ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("state", "open");
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "MANY");
				ntfCtx.put("partyId", hrmAdmin);
				dispatcher.runSync("createNotification", ntfCtx);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("TrainingUiLabels", "TrainingCoursePlanSent", locale));
	}
	
	public static Map<String, Object> updateTrainingCourseProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String emplProposalId = (String)context.get("emplProposalId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//String approvalStatusId = (String)context.get("approvalStatusId");
		Map<String, Object> resultService = FastMap.newInstance();
		try {
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabel", "NotFoundRecordToUpdate", locale));
			}
			GenericValue emplTrainingCourseProposal = delegator.findOne("EmplTrainingCourseProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			String trainingCourseId = emplTrainingCourseProposal.getString("trainingCourseId");
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			String partyId = userLogin.getString("partyId");
			String partyProposalId = emplProposal.getString("partyId");
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, timeZone, locale));
			String currStatus = emplProposal.getString("statusId");
			if("PPSL_REJECTED".equals(currStatus) || "PPSL_ACCEPTED".equals(currStatus)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatus), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotApprovalProposalDone", UtilMisc.toMap("status", statusItem.getString("description")), locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			//conditions.add(EntityCondition.makeCondition("partyId", partyId));
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition commonConds = EntityCondition.makeCondition(conditions);
			List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", commonConds, null, null, null, false);
			List<GenericValue> partyEmplProposalRoleType = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition(
					EntityCondition.makeCondition("partyId", partyId),
					EntityOperator.AND,
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove)));
			String statusIdEmplPpsl = null;
			List<GenericValue> allPartyApproved = delegator.findList("EmplProposalApprovalAndRoleType",EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyId)), null, null, null, false);
			for(GenericValue tempGv: partyEmplProposalRoleType){
				List<GenericValue> partyApproverProposal = EntityUtil.filterByCondition(allPartyApproved, EntityCondition.makeCondition("roleTypeId", tempGv.getString("roleTypeId")));
				if(UtilValidate.isNotEmpty(partyApproverProposal)){
					for(GenericValue tempApprPpsl: partyApproverProposal){
						resultService = dispatcher.runSync("updateEmplProposalApproval", UtilMisc.toMap("emplProposalApprovalId", tempApprPpsl.getString("emplProposalApprovalId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
					}
				}else{
					resultService = dispatcher.runSync("createEmplProposalApproval", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyId, "roleTypeId", tempGv.getString("roleTypeId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
				}
				if(resultService.get("statusId") != null){
					statusIdEmplPpsl = (String)resultService.get("statusId");
				}
			}
			Map<String, Object> ntfCtx = FastMap.newInstance();
			boolean doneEmplProposalProcess = false;
			String partyProposalName = PartyUtil.getPersonName(delegator, partyProposalId);
			if("PPSL_REJECTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("TrainingUiLabels", "TrainingCourseProposalReject", UtilMisc.toMap("proposer", partyProposalName), locale));
				doneEmplProposalProcess = true;	
				trainingCourse.set("statusId", "TRAINING_PLANNED_REJ");
				trainingCourse.store();
			}else if("PPSL_ACCEPTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("TrainingUiLabels", "TrainingCourseProposalAccept", UtilMisc.toMap("proposer", partyProposalName), locale));
				doneEmplProposalProcess = true;
				trainingCourse.set("statusId", "TRAINING_PLANNED_ACC");
				trainingCourse.store();
			}
			if(doneEmplProposalProcess){
				ntfCtx.put("state", "open");
				ntfCtx.put("action", "ApprovalTrainingPlanProposal");
				ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "ONE");
				List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
				partyNtf.add(emplProposal.getString("partyId"));
				ntfCtx.put("partiesList", partyNtf);
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				resultService = dispatcher.runSync("getNextRoleTypeLevelApprProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "userLogin", userLogin));
				String roleTypeId = (String)resultService.get("roleTypeId");
				if(roleTypeId != null){					
					String header = UtilProperties.getMessage("TrainingUiLabels", "ApprovalTrainingPlanProposal", UtilMisc.toMap("proposer", partyProposalName), locale);
					
					dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("roleTypeId", roleTypeId, "emplProposalId", emplProposalId, "header", header, 
																					"targetLink", "emplProposalId=" + emplProposalId, "action", "ApprovalTrainingProposal",
																					"userLogin", userLogin));
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "updateApprovalStatusSuccessful", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingCoursePartyAttend(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	/*Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);*/
    	listAllConditions.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
    	try {
    		listIterator = delegator.find("TrainingCourseAndPartyAttendance", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTrainingCourseList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listSortFields.add("-createdStamp");
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("TrainingCourse", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTrainingCourseSkillType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	HttpServletRequest request = (HttpServletRequest)context.get("request");    	
    	//Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String trainingCourseId = request.getParameter("trainingCourseId");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("TrainingCourseSkillType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
		
	} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyAttendTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();    	
    	HttpServletRequest request = (HttpServletRequest)context.get("request");    	
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	String trainingCourseId = request.getParameter("trainingCourseId");
    	String[] departmentId = parameters.get("departmentId");
    	String[] emplPositionTypeId = parameters.get("emplPostionTypeId");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//String[] statusId = parameters.get("statusId");
    	/*int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;	*/
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
		if(departmentId != null){
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		}
		if(emplPositionTypeId != null){
			conditions.add(EntityUtil.getFilterByDateExpr("emplPosFromDate", "emplPosThruDate"));
		}
		listAllConditions.addAll(conditions);
		EntityListIterator listIterator = null;
		
		try {
			listIterator = delegator.find("TrainPartyAttenAndPartyRelAndPostionType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplListInOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();    	
    	HttpServletRequest request = (HttpServletRequest)context.get("request");    	
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	String partyId = request.getParameter("partyId");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRow = 0;
    	try {
			Organization emplOrg = PartyUtil.buildOrg(delegator, partyId);
			List<GenericValue> listEmpl = emplOrg.getEmployeeInOrg(delegator);
			if(end > listEmpl.size()){
				end = listEmpl.size();
			}
			totalRow = listEmpl.size();
			listEmpl = listEmpl.subList(start, end);
			successResult.put("TotalRows", String.valueOf(totalRow));
			List<Map<String, Object>> retList = FastList.newInstance();
			successResult.put("listIterator", retList);
			for(GenericValue tempGv: listEmpl){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = tempGv.getString("partyId");
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyIdFrom", PartyHelper.getPartyName(delegator, tempGv.getString("partyIdFrom"), false));
				
				tempMap.put("emplName", PartyUtil.getPersonName(delegator, tempPartyId));
				tempMap.put("emplPositionTypeId", PartyUtil.getCurrPosTypeOfEmplOverview(delegator, tempGv.getString("partyId")));
				retList.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return successResult;
	}
	
	public static Map<String, Object> addPartyRegisTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyIds = (String)context.get("partyIds");
		JSONArray partyJson = JSONArray.fromObject(partyIds);
		String sendNotify = (String)context.get("sendNotify");
		List<String> partyIdList = FastList.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			for(int i = 0; i < partyJson.size(); i++){
				String partyId = partyJson.getJSONObject(i).getString("partyId");
				partyIdList.add(partyId);
				dispatcher.runSync("createTrainingCoursePartyRegister", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId, "userLogin", userLogin));
			}
			if(sendNotify != null && Boolean.parseBoolean(sendNotify) && partyIdList.size() > 0){
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("partiesList", partyIdList);
				ntfCtx.put("header", "Đăng ký tham gia khóa đào tạo: " + trainingCourse.getString("trainingCourseName"));
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("action", "EmplConfirmRegisterTraining");
				ntfCtx.put("targetLink", "trainingCourseId=" + trainingCourseId);
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("state", "open");
				dispatcher.runSync("createNotification", ntfCtx);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	//@SuppressWarnings("unchecked")
	public static Map<String, Object> addPartyIdToTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyIds = (String)context.get("partyIds");
		JSONArray partyJson = JSONArray.fromObject(partyIds);
		String sendNotify = (String)context.get("sendNotify");
		List<String> partyIdList = FastList.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			for(int i = 0; i < partyJson.size(); i++){
				String partyId = partyJson.getJSONObject(i).getString("partyId");
				partyIdList.add(partyId);
				dispatcher.runSync("createTrainingCoursePartyAttendance", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "trainingCourseId", trainingCourseId, "userLogin", userLogin));
			}
			if(sendNotify != null && Boolean.parseBoolean(sendNotify) && partyIdList.size() > 0){
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("partiesList", partyIdList);
				ntfCtx.put("header", "Bạn đã được thêm vào danh sách tham gia khóa đào tạo: " + trainingCourse.getString("trainingCourseName"));
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("action", "EmplConfirmAttendanceTraining");
				ntfCtx.put("targetLink", "trainingCourseId=" + trainingCourseId);
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("state", "open");
				dispatcher.runSync("createNotification", ntfCtx);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> addPartyTrainingCouseAndUpdateRegister(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyId = (String)context.get("partyId");
		try {
			Map<String, Object> resultService = dispatcher.runSync("createTrainingCoursePartyAttendance", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "trainingCourseId", trainingCourseId, "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				dispatcher.runSync("updateEmplResgisterTrainingCourse", UtilMisc.toMap("userLogin", userLogin, "trainingCourseId", trainingCourseId, "partyId", partyId, "statusId", "COURS_REGIS_ADDED"));
				
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmplResgisterTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String trainingCourseId = (String)context.get("trainingCourseId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue emplRegisTraining = delegator.findOne("TrainingCoursePartyRegister", UtilMisc.toMap("trainingCourseId", trainingCourseId, 
																											"partyId", partyId), false);
			emplRegisTraining.set("statusId", context.get("statusId"));
			emplRegisTraining.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale)); 
	}
	
	public static Map<String, Object> updateEmplAttendanceTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String trainingCourseId = (String)context.get("trainingCourseId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue emplAttendanceTraining = delegator.findOne("TrainingCoursePartyAttendance", UtilMisc.toMap("trainingCourseId", trainingCourseId, 
																											"partyId", partyId), false);
			emplAttendanceTraining.set("statusId", context.get("statusId"));
			emplAttendanceTraining.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale)); 
	}
	
	public static Map<String, Object> createTrainingCoursePartyAttendance(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String trainingCourseId = (String)context.get("trainingCourseId");
		Delegator delegator = dctx.getDelegator();
		String statusId = (String)context.get("statusId");
		try {
			GenericValue partyAttendance = delegator.findOne("TrainingCoursePartyAttendance", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId), false);
			if(partyAttendance == null){
				partyAttendance = delegator.makeValue("TrainingCoursePartyAttendance");
				partyAttendance.setAllFields(context, false, null, null);
				if(statusId == null){
					partyAttendance.set("statusId", "COURS_ATT_WAIT_CON");
				}
				
				partyAttendance.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRegisAttendTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();    	
    	HttpServletRequest request = (HttpServletRequest)context.get("request");    	
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	String trainingCourseId = request.getParameter("trainingCourseId");
    	String[] departmentId = parameters.get("departmentId");
    	String[] emplPositionTypeId = parameters.get("emplPostionTypeId");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//String[] statusId = parameters.get("statusId");
    	/*int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;	*/
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
		if(departmentId != null){
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		}
		if(emplPositionTypeId != null){
			conditions.add(EntityUtil.getFilterByDateExpr("emplPosFromDate", "emplPosThruDate"));
		}
		listAllConditions.addAll(conditions);
		EntityListIterator listIterator = null;
		
		try {
			listIterator = delegator.find("TrainPartyRegisAndPartyRelAndPostionType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> addSkillTypeToTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		String trainingCourseId = (String)context.get("trainingCourseId");
		String skillTypes = (String)context.get("skillTypes");
		String requiredLevelStatusId = (String)context.get("requiredLevelStatusId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		
		JSONArray skillTypeJson = JSONArray.fromObject(skillTypes);
		if(skillTypeJson == null || skillTypeJson.size() == 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("TrainingUiLabels", "SkillTypeNotChoose", locale));
		}
		try {
			for(int i= 0; i < skillTypeJson.size(); i++){
				String skillTypeId = skillTypeJson.getJSONObject(i).getString("skillTypeId");
				dispatcher.runSync("createSkillTypeTrainingCourse", UtilMisc.toMap("skillTypeId", skillTypeId, "trainingCourseId", trainingCourseId, 
																				 "requiredLevelStatusId", requiredLevelStatusId, "userLogin", context.get("userLogin")));
				
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("TrainingUiLabels", "AddSkillTypeToTrainingCourse", locale));
	}
	
	public static Map<String, Object> createSkillTypeTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String skillTypeId = (String)context.get("skillTypeId");
		
		try {
			GenericValue trainingCourseSkillType = delegator.findOne("TrainingCourseSkillType", UtilMisc.toMap("trainingCourseId", trainingCourseId, "skillTypeId", skillTypeId), false);
			if(trainingCourseSkillType == null){
				trainingCourseSkillType = delegator.makeValidValue("TrainingCourseSkillType", context);
				trainingCourseSkillType.create();
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingCoursePartyRegister(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String trainingCourseId = (String)context.get("trainingCourseId");
		Delegator delegator = dctx.getDelegator();
		
		try {
			GenericValue partyRegister = delegator.findOne("TrainingCoursePartyRegister", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId), false);
			if(partyRegister == null){
				partyRegister = delegator.makeValue("TrainingCoursePartyRegister");
				partyRegister.setAllFields(context, false, null, null);
				partyRegister.set("statusId", "COURS_REGIS_WAIT_CON");
				partyRegister.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> uploadTrainingCourseDocument(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "/hrmdoc/training";
		String partyId = (String)context.get("partyId");
		String trainingCourseId = (String)context.get("trainingCourseId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
			List<GenericValue> listHrmAdminUserLogin = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", hrmAdmin), null, false);
			GenericValue hrmAdminUserLogin = EntityUtil.getFirst(listHrmAdminUserLogin);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("public", "Y");
			uploadedFileCtx.put("userLogin", hrmAdminUserLogin);
			Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			String path = (String)resultService.get("path");
			Map<String, Object> dataResourceCtx = FastMap.newInstance();
			dataResourceCtx.put("objectInfo", path);
	        dataResourceCtx.put("dataResourceName", uploadFileNameStr);
	        dataResourceCtx.put("userLogin", systemUserLogin);
	        dataResourceCtx.put("dataResourceTypeId", "URL_RESOURCE");
	        dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
	        dataResourceCtx.put("isPublic", "Y");
	        resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
	        String dataResourceId = (String) resultService.get("dataResourceId");
	        Map<String, Object> contentCtx = FastMap.newInstance();
	        contentCtx.put("dataResourceId", dataResourceId);
	        contentCtx.put("contentTypeId", "DOCUMENT");
	        contentCtx.put("contentName", uploadFileNameStr);
	        contentCtx.put("userLogin", systemUserLogin);
	        resultService = dispatcher.runSync("createContent", contentCtx);
	        String contentId = (String)resultService.get("contentId");
	        Map<String, Object> trainingCourseContent = FastMap.newInstance();
	        trainingCourseContent.put("partyId", partyId);
	        trainingCourseContent.put("trainingCourseId", trainingCourseId);
	        trainingCourseContent.put("contentId", contentId);
	        trainingCourseContent.put("userLogin", userLogin);
	        dispatcher.runSync("createTrainingCourseDocument", trainingCourseContent);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingCourseDocument(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue trainingCourseDocument = delegator.makeValue("TrainingCourseDocument");
		trainingCourseDocument.setAllFields(context, false, null, null);
		try {
			trainingCourseDocument.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateTrainingCoursePartyAttend(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue trainingCoursePartyAttendance = delegator.findOne("TrainingCoursePartyAttendance", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), false);
			trainingCoursePartyAttendance.setNonPKFields(context);
			trainingCoursePartyAttendance.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateTrainingCourseTraineed(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyIds = (String)context.get("partyIds");
		String emplPositionTypes = (String)context.get("emplPositionTypes");
		String isCompulsory = (String)context.get("isCompulsory");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("TrainingUiLabels", "CannotFindTrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			if(isCompulsory != null){
				trainingCourse.set("isCompulsory", isCompulsory);
				trainingCourse.store();
			}
			if(partyIds != null){
				JSONArray partyIdJson = JSONArray.fromObject(partyIds);
				List<String> partyIdList = FastList.newInstance();
				for(int i = 0; i < partyIdJson.size(); i++){
					partyIdList.add(partyIdJson.getJSONObject(i).getString("partyId"));
				}
				dispatcher.runSync("updateTrainingCoursePartyGroup", UtilMisc.toMap("partyIdList", partyIdList, "trainingCourseId", trainingCourseId, "userLogin", userLogin));
			}
			if(emplPositionTypes != null){
				JSONArray emplPositionTypeIdJson = JSONArray.fromObject(emplPositionTypes);
				List<String> emplPositionTypeIdList = FastList.newInstance();
				for(int i = 0; i< emplPositionTypeIdJson.size(); i++){
					emplPositionTypeIdList.add(emplPositionTypeIdJson.getJSONObject(i).getString("emplPositionTypeId"));
				}
				dispatcher.runSync("updateTrainingCourseEmplPosTypeId", UtilMisc.toMap("emplPositionTypeIdList", emplPositionTypeIdList, "trainingCourseId", trainingCourseId, "userLogin", userLogin));
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("TrainingUiLabels", "AddedTraineeToTrainingCourse", (Locale)context.get("locale")));
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateTrainingCoursePartyGroup(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String trainingCourseId = (String)context.get("trainingCourseId");
		List<String> partyIdList = (List<String>)context.get("partyIdList");
		//Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> trainingCoursePartyGroupTrainee = delegator.findByAnd("TrainingCoursePartyGroupTrainee", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
			List<GenericValue> deleteParty = EntityUtil.filterByCondition(trainingCoursePartyGroupTrainee, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyIdList));
			List<String> trainingCourseParty = EntityUtil.getFieldListFromEntityList(trainingCoursePartyGroupTrainee, "partyId", true);
			for(String tempPartyId: partyIdList){
				if(!trainingCourseParty.contains(tempPartyId)){
					dispatcher.runSync("createTrainingCoursePartyGroupTrainee", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", tempPartyId, "userLogin", context.get("userLogin")));
				}
			}
			for(GenericValue removeGv: deleteParty){
				removeGv.remove();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateTrainingCourseEmplPosTypeId(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String trainingCourseId = (String)context.get("trainingCourseId");
		List<String> emplPositionTypeIdList = (List<String>)context.get("emplPositionTypeIdList");
		try {
			List<GenericValue> trainingCourseEmplPosType = delegator.findByAnd("TrainingCourseEmplPosTypeTrainee", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
			List<GenericValue> deleteTrainingCourseEmplPosType = EntityUtil.filterByCondition(trainingCourseEmplPosType, EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_IN, emplPositionTypeIdList));
			List<String> trainingCourseEmplPosTypeList = EntityUtil.getFieldListFromEntityList(trainingCourseEmplPosType, "partyId", true);
			for(String tempEmplPosTypeId: emplPositionTypeIdList){
				if(!trainingCourseEmplPosTypeList.contains(tempEmplPosTypeId)){
					dispatcher.runSync("createTrainingCourseEmplPosType", UtilMisc.toMap("trainingCourseId", trainingCourseId, "emplPositionTypeId", tempEmplPosTypeId, "userLogin", context.get("userLogin")));
				}
			}
			for(GenericValue removeGv: deleteTrainingCourseEmplPosType){
				removeGv.remove();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingCourseEmplPosType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		try {
			GenericValue entity = delegator.findOne("TrainingCourseEmplPosTypeTrainee", UtilMisc.toMap("trainingCourseId", trainingCourseId, "emplPositionTypeId", emplPositionTypeId), false);
			if(entity == null){
				entity = delegator.makeValidValue("TrainingCourseEmplPosTypeTrainee", context);
				entity.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingCoursePartyGroupTrainee(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue entity = delegator.findOne("TrainingCoursePartyGroupTrainee", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), false);
			if(entity == null){
				entity = delegator.makeValidValue("TrainingCoursePartyGroupTrainee", context);
				entity.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	/*public static Map<String, Object> getListTrainingDocument(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String trainingCourseId = (String)context.get("trainingCourseId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		List<Map<String, Object>> retList = FastList.newInstance();
		retMap.put("listTrainingDocument", retList);
		retMap.put(ModelService.RESPOND_SUCCESS, ModelService.SUCCESS_MESSAGE);
		try {
			List<GenericValue> trainingCourseDocument = delegator.findByAnd("TrainingDocumentAndContent", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), UtilMisc.toList("-createdDate"), false);
			for(GenericValue tempGv: trainingCourseDocument){				
				String dataResourceId = tempGv.getString("dataResourceId");
				Map<String, Object> tempMap = FastMap.newInstance();
				if(dataResourceId != null){
					GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
					String dataResourceName = dataResource.getString("dataResourceName");
					String objectInfo = dataResource.getString("objectInfo");
					tempMap.put("dataResourceName", dataResourceName);
					tempMap.put("objectInfo", objectInfo);
					tempMap.put("contentId", tempGv.getString("contentId"));
					retList.add(tempMap);
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	/* ============================= end new service training =========================================================*/
	
	public static Map<String, Object> createTrainingPlan(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Delegator delegator= dpctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		boolean flad= false;
		GenericValue userlogin= (GenericValue)context.get("userLogin");
		Map<String, Object> result=ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		try {
			String partyIdFrom = userlogin.getString("partyId");
			String partyId = PartyUtil.getOrgByManager(partyIdFrom, delegator);
			context.put("partyId", partyId);
			flad= insertTrainingPlan(dpctx, context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		if(flad){
			result.put("flad","1");
		}
		return result;
	}
	/*
	 * 
	 * Create Propose Training 
	 * @param dpctx
	 * @param context
	 * @throw Exception
	 * 
	 * */
	public static Map<String,Object> createTrainingProposal(DispatchContext dpctx,
			Map<String, Object> context) throws Exception{
		Delegator delegator =dpctx.getDelegator();
		Locale locale=(Locale) context.get("locale");
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		String partyIdFrom = (String) userLogin.get("partyId");
		Map<String,Object> mapProposeTraining=FastMap.newInstance();
		Timestamp fromDate= (Timestamp) context.get("fromDate");
		Timestamp thruDate=(Timestamp)context.get("thruDate"); 	
		try {
				String partyIdTo= (String) PartyUtil.getOrgByManager(partyIdFrom, delegator);
				context.put("partyId",partyIdTo);
				if(DateUtil.checkDateTime(fromDate, thruDate)){
					mapProposeTraining = insertTrainingProposal(dpctx, context);
				}else return ServiceUtil.returnError(UtilProperties.getMessage(
						resourceNoti, "Error Time (fromDate>thruDate)", locale));	
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return mapProposeTraining;
	}
	/*
	 * 
	 * Create Training Class
	 * @param dpctx
	 * @param context
	 * 
	 * 
	 * */
	public static Map<String,Object> createTrainingClass(DispatchContext dpctx,Map<String,Object> context) throws Exception{
		Delegator delegator =dpctx.getDelegator();
		Locale locale=(Locale) context.get("locale");
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		String partyIdFrom = (String) userLogin.get("partyId");
		Timestamp fromDate= (Timestamp) context.get("fromDate");
		Timestamp thruDate=(Timestamp)context.get("thruDate"); 
		try {
			String partyIdTo= (String) PartyUtil.getOrgByManager(partyIdFrom, delegator);
			if(partyIdTo!=null){
				context.put("partyId",partyIdTo);
			if(DateUtil.checkDateTime(fromDate, thruDate)){
				insertTrainingClass(dpctx, context);
			}else return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "Error Time (fromDate>thruDate)", locale));	
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resourceNoti, "createError",locale));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		
	} 
	
	/*================maybe delete=========================================*/
	private static void insertTrainingClass(DispatchContext dpctx,
			Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		
		
		Delegator delegator=dpctx.getDelegator();
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		GenericValue createTL=delegator.makeValue("TrainingClass");
		String trainingClassId=(String) delegator.getNextSeqId("TrainingClass");
		String trainingProposalId=(String) context.get("trainingProposalId");
		String trainingClassName=(String) context.get("trainingClassName");
		String teacher=(String) context.get("teacher");
		String location=(String) context.get("location");
		Timestamp fromDate=(Timestamp) context.get("fromDate");
		Timestamp thruDate=(Timestamp) context.get("thruDate");
		createTL.set("trainingClassId",trainingClassId );
		createTL.set("trainingClassName",trainingClassName);
		createTL.set("teacher", teacher);
		createTL.set("location",location);
		createTL.set("fromDate", fromDate);
		createTL.set("thruDate", thruDate);
		createTL.set("trainingProposalId",trainingProposalId);
		createTL.set("statusId","CREATE_TRAINING");
		createTL.create();
		createTrainingPersonalVotes(dpctx, trainingProposalId,fromDate,thruDate,trainingClassId);
		//send Notification for each employee training
		List<GenericValue> listPP=delegator.findList("EmplTrainingProposal",EntityCondition.makeCondition("trainingProposalId",trainingProposalId),null,null,null,false);
		ArrayList<String> listEmplRequest=new ArrayList<String>();
		for(int i=0;i<listPP.size();i++){
			if(!listEmplRequest.contains(listPP.get(i).getString("partyId"))){
					listEmplRequest.add(listPP.get(i).getString("partyId"));
			}
		}
		
		//send Notificaton for each employee
		Timestamp dateTime= new Timestamp(new Date().getTime());
		String action="viewResultsTraining";
		String header="Thông báo danh sách lớp đào tạo ";
		String notiToId="";
		String targetId="";
		String targetLink="employeeId=";
		String state="open";
		Map<String,Object> sendNotification=FastMap.newInstance();
		sendNotification.put("action", action);
		sendNotification.put("header", header);
		sendNotification.put("state", state);
		sendNotification.put("dateTime", dateTime);
		sendNotification.put("userLogin", userLogin);
		for(String emplId:listEmplRequest){
			notiToId=emplId;
			targetId=emplId;
			targetLink+=targetId;
			sendNotification.put("partyId", notiToId);
			sendNotification.put("targetLink", targetLink);
			dpctx.getDispatcher().runSync("createNotification", sendNotification);
			sendNotification.put("notiToId", "");
			sendNotification.put("targetLink","");
			notiToId="";
			targetLink=targetLink.replace(targetId, "");
		}
		
		}
	/*
	 * 
	 * Insert Training Proposal
	 * @param dpctx
	 * @param context
	 * @throw GenericEntityException
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> insertTrainingProposal(DispatchContext dpctx, Map<String,?extends Object> context) throws Exception{
		Delegator delegator = dpctx.getDelegator();
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		Locale locale=(Locale) context.get("locale");	
		String partyId=(String) context.get("partyId");
		String userLoginId=userLogin.getString("partyId");
		List<String> listEmplId=null;
		
		String notiToId="";
		String header="";
		String state="";
		String targetLink="";
		String action="";
		Timestamp dateTime=new Timestamp(new Date().getTime());
		
		if(context.get("listEmployeeId") instanceof List)
		{
			listEmplId=(List<String>) context.get("listEmployeeId");
		}
		String trainingTypeId=(String) context.get("trainingTypeId");
		String trainingFormId=(String) context.get("trainingFormId");
		String trainingContentId=(String) context.get("trainingContentId");
		Date fromDate=(Date) context.get("fromDate");
		Date thruDate=(Date) context.get("thruDate");
		
		GenericValue tpl=delegator.makeValue("TrainingProposal");
		String trainingProposalId=delegator.getNextSeqId("TrainingProposal");
		GenericValue tp=delegator.makeValue("TypePropose");
		String typeProposeId="";
		//check propose in plan 
		if(checkProposeTrainingInPlan(dpctx, trainingTypeId, trainingFormId, trainingContentId, fromDate)){
			
			tpl.set("trainingProposalId",trainingProposalId);
			tpl.set("partyId", partyId);
			tpl.set("trainingTypeId", trainingTypeId);
			tpl.set("trainingFormId", trainingFormId);
			typeProposeId=delegator.getNextSeqId("TypePropose");
			tpl.set("typeProposeId", typeProposeId);
			tp.set("typeProposeId", typeProposeId);
			tp.set("description", "Đào tạo trong kế hoạch");
			tp.create();
			tpl.set("trainingContentId", trainingContentId);
			tpl.set("fromDate",fromDate);
			tpl.set("thruDate", thruDate);
			tpl.set("statusId", "ITS_INIT");
			tpl.create();
			if(listEmplId!=null){
				GenericValue tpl1=delegator.makeValue("EmplTrainingProposal");
				for(String emplId:listEmplId){
					tpl1.set("trainingProposalId", trainingProposalId);
					tpl1.set("partyId", emplId);
					tpl1.create();
				}
			}
			notiToId = PartyUtil.getHrmAdmin(delegator);
			header="Kiểm tra đề xuất đào tạo"+"["+PartyUtil.getDeptNameById(userLoginId, delegator)+"]";
			action="checkProposeTraining";
			state="open";
			targetLink="trainingProposalId="+trainingProposalId;
		}else {
			notiToId=PartyUtil.getCEO(delegator);
			header="Phê duyệt đề xuất đào tạo"+"["+PartyUtil.getDeptNameById(userLoginId, delegator)+"]";
			action="apprProTraining";
			state="open";
			targetLink="HdId="+userLoginId+";"+"HrId="+PartyUtil.getHrmAdmin(delegator)+";"+"ppId="+trainingProposalId;
			
			tpl.set("trainingProposalId",trainingProposalId);
			tpl.set("partyId", partyId);
			tpl.set("trainingTypeId", trainingTypeId);
			tpl.set("trainingFormId", trainingFormId);
			typeProposeId = delegator.getNextSeqId("TypePropose");
			tpl.set("typeProposeId", typeProposeId);
			tp.set("typeProposeId", typeProposeId);
			tp.set("description", "Đào tạo bổ sung");
			tp.create();
			tpl.set("trainingContentId", trainingContentId);
			tpl.set("fromDate",fromDate);
			tpl.set("thruDate", thruDate);
			tpl.set("statusId", "OUT_PLAN");
			tpl.create();
			if(listEmplId!=null){
				GenericValue tpl1=delegator.makeValue("EmplTrainingProposal");
				for(String emplId:listEmplId){
					tpl1.set("trainingProposalId", trainingProposalId);
					tpl1.set("partyId", emplId);
					tpl1.create();
				}
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("header", header);
		result.put("notiToId", notiToId);
		result.put("state", state);
		result.put("dateTime", dateTime);
		result.put("targetLink", targetLink);
		result.put("action", action);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				resourceNoti,"createSuccessfully", locale));
		return result;
	}	
	/*
	 * create Training Personal Votes
	 * @param dpctx
	 * throws GenericEntityException
	 * 
	 * */
	
	public static void createTrainingPersonalVotes(DispatchContext dpctx,String trainingProposalId,Timestamp fromDate,Timestamp thruDate,String trainingClassId) throws GenericEntityException{
		Delegator delegator = dpctx.getDelegator();
			GenericValue mapPro=null;
			List<GenericValue> listEmplId=null;
			mapPro = delegator.findOne("TrainingProposal", false, UtilMisc.toMap("trainingProposalId", trainingProposalId));
			listEmplId= delegator.findList("EmplTrainingProposal",EntityCondition.makeCondition("trainingProposalId",trainingProposalId),null,null,null,false );
			
			
				for(GenericValue empl :listEmplId)
				{
					GenericValue ctpv= delegator.makeValidValue("TrainingTracker");
					String partyId=empl.getString("partyId");
					String trainingContentId= mapPro.getString("trainingContentId");
					String trainingFormId= mapPro.getString("trainingFormId");
					ctpv.set("partyId", partyId);
					ctpv.set("trainingContentId", trainingContentId);
					ctpv.set("trainingFormId", trainingFormId);
					ctpv.set("fromDate", fromDate);
					ctpv.set("thruDate", thruDate);
					ctpv.set("trainingClassId", trainingClassId);
					ctpv.set("statusId", "TRS_PROGRESS");
					ctpv.create();
					
				}
			}
	
	/*
	 * check ProposeTrainingPlan isIn Plan
	 * @param trainingClassType,createDate,fromDate from TrainingPlan
	 * @return true,false
	 * throws GenericEntityException
	 * 
	 * */
	public static boolean checkProposeTrainingInPlan(DispatchContext dpctx,String trainingTypeId,String trainingFormId,String trainingContentId,Date fromDate) throws GenericEntityException{
		Delegator delegator=dpctx.getDelegator();
		EntityCondition condition1=EntityCondition.makeCondition("trainingTypeId",trainingTypeId);
		EntityCondition condition2=EntityCondition.makeCondition("fromDate",fromDate);
		EntityCondition condition3=EntityCondition.makeCondition("trainingFormId",trainingFormId);
		EntityCondition condition4=EntityCondition.makeCondition("trainingContentId",trainingContentId);
		EntityCondition condition=EntityCondition.makeCondition(EntityJoinOperator.AND,condition1,condition2,condition3,condition4);
		List<GenericValue> listCondition=delegator.findList("TrainingProposal", condition, null, null, null, false);
		if(!listCondition.isEmpty()){
			return true;
		}
		return false;
	}
	/*
	 * 
	 * insert Training Plan
	 * @param dpctx
	 * @param context
	 * @param throw GenericEntityException
	 * 
	 * 
	 * */
	public static boolean insertTrainingPlan(DispatchContext dpctx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpctx.getDelegator();
		String partyId= (String)context.get("partyId");
		Date fromDate= (Date)context.get("fromDate");
		Date thruDate= (Date)context.get("thruDate");
		Date createdDate =(Date)context.get("createdDate");
		String quality= (String)context.get("quality");
		String description= (String)context.get("description");
		String trainingClassTypeId= (String)context.get("trainingClassTypeId");
		GenericValue tpl=delegator.makeValidValue("TrainingPlan");
		if(UtilValidate.isNotEmpty(fromDate)&&UtilValidate.isNotEmpty(thruDate)){
			tpl.set("trainingClassTypeId", trainingClassTypeId);
			tpl.set("createdDate",createdDate);
			tpl.set("thruDate", thruDate);
			tpl.set("fromDate", fromDate);
			tpl.set("quality", quality);
			tpl.set("description",description);
			tpl.set("partyId", partyId);
			tpl.set("statusId", "ITRS_INIT");
			
			tpl.create();
			return true;
		}else{
			return false;
		}
	}
	/*
	 * check TrainingPlan isIn Plan
	 * @param trainingClassType,createDate,fromDate from TrainingPlan
	 * throws GenericEntityException
	 * 
	 * */
	public static boolean checkTrainingInPlan(DispatchContext dpctx,String trainingClassTypeId,Date fromDate,Date createdDate) throws GenericEntityException{
			Delegator delegator=dpctx.getDelegator();
			EntityCondition condition1=EntityCondition.makeCondition("trainingClassTypeId",trainingClassTypeId);
			EntityCondition condition2=EntityCondition.makeCondition("fromDate",fromDate);
			EntityCondition condition3=EntityCondition.makeCondition("createdDate",createdDate);
			EntityCondition condition=EntityCondition.makeCondition(EntityJoinOperator.AND,condition1,condition2,condition3);
			List<GenericValue> listCondition=delegator.findList("TrainingPlan", condition, null, null, null, false);
			if(listCondition.isEmpty()){
				return false;
			}
			return true;
	}
	/*
	 * create Notification to HR
	 * @param dpctx,context
	 * throws Exception
	 * 
	 * */
	public static Map<String,Object> sendNotificationHR(DispatchContext dpctx,Map<String,Object> context) throws Exception{
		Delegator delegator=dpctx.getDelegator();
		Map<String,Object> attrCreateNoti=FastMap.newInstance();
		String notiToId=PartyUtil.getHrmAdmin(delegator);
		String header="Kết quả phê duyệt đào tạo";
		String action="resultsAppr";
		String state="open";
		Timestamp dateTime=new Timestamp(new Date().getTime());
		String targetLink="";
		attrCreateNoti.put("action", action);
		attrCreateNoti.put("header", header);
		attrCreateNoti.put("notiToId", notiToId);
		attrCreateNoti.put("targetLink", targetLink);
		attrCreateNoti.put("state", state);
		attrCreateNoti.put("dateTime", dateTime);
		
		return attrCreateNoti;
	}
	
	public static Map<String,Object> ratingTraining(DispatchContext dpctx,Map<String,Object> context) throws Exception 
	{

		Delegator delegator=dpctx.getDelegator();
		Locale locale=(Locale) context.get("locale");
		String statusId=(String) context.get("statusId");
		String trainingClassId=(String) context.get("trainingClassId");
		String partyId=(String) context.get("partyId");
		Date fromDate= (Date) context.get("fromDate");
		Date thruDate=(Date) context.get("thruDate");
		GenericValue emplJobRequest=null;
		
		Map<String,Object> result=FastMap.newInstance();
		
		try{
		emplJobRequest=delegator.findOne("TrainingTracker", UtilMisc.toMap("trainingClassId", trainingClassId, "partyId", partyId,"fromDate",fromDate), false);
		}
		catch(GenericEntityException e){
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[] {e.getMessage()}, locale));
		
		}
		//store value in Entity 
		emplJobRequest.put("trainingClassId", trainingClassId);
		emplJobRequest.put("partyId", partyId);
		emplJobRequest.put("fromDate", fromDate);
		emplJobRequest.put("statusId", statusId);
		try {
			//send Notification for employee
			GenericValue Cn=delegator.findOne("TrainingClass", false,UtilMisc.toMap("trainingClassId", trainingClassId,"fromDate",fromDate,"thruDate",thruDate));
			String className=Cn.getString("trainingClassName");
			emplJobRequest.store();
			String notiToId=partyId;
			String action="viewResultsTraining";
			String targetLink="employeeId="+partyId;
			String header="Thông báo kết quả đào tạo với nhân viên tham gia lớp : ["+className+"]";
			Timestamp dateTime=new Timestamp(new Date().getTime());
			String state="open";
		
			result.put("notiToId", notiToId);
			result.put("header", header);
			result.put("action", action);
			result.put("targetLink", targetLink);
			result.put("dateTime", dateTime);
			result.put("state", state);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
					resourceNoti,"rateSuccessfully", locale));
			//create list employee training failed!
			} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[] {e.getMessage()}, locale));
	
		}
		 return result;
	}

	/*
	 * create list employee training failed
	 * @param delegator,context
	 * throws GenericValueException
	 * 
	 * */
	public static Map<String,Object> createFailedEmployeeClass(DispatchContext dpctx,Map<String, Object> context) throws Exception
	{
		Delegator delegator =dpctx.getDelegator();
		Locale locale=(Locale)context.get("locale");
		Map<String,Object> Result=FastMap.newInstance();
		Timestamp dateTime=new Timestamp(new Date().getTime());
		List<GenericValue> listVotes=null;
		GenericValue emplFailedId=null;
		EntityCondition condition=EntityCondition.makeCondition("statusId","FAILED_TRAINING");
		try{
			listVotes=delegator.findList("TrainingTracker", condition, null, null, null, false);
				 String notiToId=PartyUtil.getHrmAdmin(delegator);
				 String action="editResultsTraining";
				 String targetLink="HrId=";
				 String header="Thông báo chỉnh sửa đào tạo lại";
				 String state="open";
				 Result.put("notiToId", notiToId);
				 Result.put("header", header);
				 Result.put("action", action);
				 Result.put("targetLink", targetLink);
				 Result.put("dateTime", dateTime);
				 Result.put("state", state);
				 Result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				 Result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
						resourceNoti,"RatingSuccessfully!!!!!", locale));
			for(GenericValue item:listVotes){
				emplFailedId=delegator.findOne("EmplFailedTraining", false, UtilMisc.toMap("partyId", item.getString("partyId"),"trainingClassId",item.getString("trainingClassId")));
				if(emplFailedId==null){
					GenericValue eft=delegator.makeValidValue("EmplFailedTraining");
					eft.set("partyId", item.getString("partyId"));
					eft.set("trainingClassId",item.getString("trainingClassId"));
					eft.set("statusId", item.getString("statusId"));
					eft.create();
				}
			}	
		}catch(GenericEntityException e){
			Debug.log(e.getMessage(),module);
			
			
		}
		return Result;
	}
	
	public static Map<String,Object> EditResultsTraining(DispatchContext dpctx,Map<String, Object> context) throws Exception
	{
		Delegator delegator =dpctx.getDelegator();
		String partyId=(String) context.get("partyId");
		String trainingClassId=(String) context.get("trainingClassId");
		String statusId=(String) context.get("statusId");
		
		GenericValue resultsTraining1=null;
		List<GenericValue> resultsTraining2=null;
		EntityCondition condition1=EntityCondition.makeCondition("partyId",partyId);
		EntityCondition condition2=EntityCondition.makeCondition("trainingClassId",trainingClassId);
		EntityCondition rscondition=EntityCondition.makeCondition(EntityJoinOperator.AND,condition1,condition2);
		resultsTraining1=delegator.findOne("EmplFailedTraining", false, UtilMisc.toMap("trainingClassId",trainingClassId, "partyId", partyId));
		resultsTraining2=delegator.findList("TrainingTracker", rscondition,null,null,null,false );
		
		if(statusId.equals("PASS_TRAINING")){
			resultsTraining1.remove();
			resultsTraining2.get(0).put("partyId", partyId);
			resultsTraining2.get(0).put("trainingClassId", trainingClassId);
			resultsTraining2.get(0).put("statusId", statusId);
			resultsTraining2.get(0).store();
		}
		
		Map<String,Object> EditResult=FastMap.newInstance();
		Timestamp dateTime=new Timestamp(new Date().getTime());
		String notiToId=partyId;
		 String action="viewResultsTraining";
		 String targetLink="employeeId="+partyId;
		 String header="Thông báo đào tạo lại";
		 String state="open";
		 EditResult.put("notiToId", notiToId);
		 EditResult.put("header", header);
		 EditResult.put("action", action);
		 EditResult.put("targetLink", targetLink);
		 EditResult.put("dateTime", dateTime);
		 EditResult.put("state", state);
		return EditResult;
	}
	
	
	
}


