package com.olbius.basehr.recruitment.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;

import com.olbius.basehr.recruitment.helper.RecruitmentHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.security.util.SecurityUtil;

public class RecruitmentServices {
	
	public static final String module = RecruitmentServices.class.getName();
	 
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getHRPlanning(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		successResult.put("TotalRows", String.valueOf(totalRows));
		if(partyGroupId == null){
			return successResult;
		}
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			List<GenericValue> emplPosList = PartyUtil.getListAllEmplPositionTypeOfParty(delegator, partyGroupId, fromDateTs, thruDateTs);
			if(UtilValidate.isEmpty(emplPosList)){
				return successResult;
			}
			List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPosList, "emplPositionTypeId", true);
			List<EntityCondition> hrPlanningConds = FastList.newInstance();
			if(emplPositionTypeId != null && emplPositionTypeId.length() > 0){
				if(!emplPositionTypeList.contains(emplPositionTypeId)){
					return successResult;
				}
				hrPlanningConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			}else{
				hrPlanningConds.add(EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.IN, emplPositionTypeList));
			}
			List<GenericValue> customTimePeriodMonth = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("periodTypeId", "MONTHLY_HRPLAN", "parentPeriodId", customTimePeriodId), null, false);
			if(UtilValidate.isEmpty(customTimePeriodMonth)){
				return successResult;
			}
			List<String> customTimePeriodIdList = EntityUtil.getFieldListFromEntityList(customTimePeriodMonth, "customTimePeriodId", true);
			EntityCondition customTimePeriodConds = EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN, customTimePeriodIdList); 
			hrPlanningConds.add(customTimePeriodConds);
			List<GenericValue> hrPlanningList = delegator.findList("HRPlanningAndEmplPositionType", EntityCondition.makeCondition(hrPlanningConds), 
					UtilMisc.toSet("emplPositionTypeId", "description"), UtilMisc.toList("description"), null, false);
			totalRows = hrPlanningList.size();
			if(end > totalRows){
				end = totalRows;
			}
			Calendar cal = Calendar.getInstance();
			hrPlanningList = hrPlanningList.subList(start, end);
			for(GenericValue tempGv: hrPlanningList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempEmplPositionTypeId = tempGv.getString("emplPositionTypeId");
				List<GenericValue> humanResourcePlanning = delegator.findList("HRPlanningAndCustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("emplPositionTypeId", tempEmplPositionTypeId),
																																	EntityJoinOperator.AND, customTimePeriodConds), null, null, null, false);
				tempMap.put("emplPositionTypeId", tempEmplPositionTypeId);
				for(GenericValue tempHumanResourcePlanning: humanResourcePlanning){
					Date tempFromDate = tempHumanResourcePlanning.getDate("fromDate");
					cal.setTime(tempFromDate);
					int month = cal.get(Calendar.MONTH);
					tempMap.put("customTimePeriodId_" + month, tempHumanResourcePlanning.get("customTimePeriodId"));
					tempMap.put("statusId_" + month, tempHumanResourcePlanning.get("statusId"));
					tempMap.put("comment_" + month, tempHumanResourcePlanning.get("comment"));
					tempMap.put("quantity_" + month, tempHumanResourcePlanning.get("quantity"));
				}
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentAnticipateList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	successResult.put("listIterator", listIterator);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String partyGroupId = parameters.get("partyId")[0];
		String yearStr = parameters.get("year")[0];
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		try {
			if(partyGroupId != null){
				if(UtilValidate.isEmpty(listSortFields)){
					listSortFields.add("groupName");
					listSortFields.add("emplPositionTypeDesc");
				}
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> listDept = buildOrg.getAllDepartmentList(delegator);
				List<String> listDeptId = EntityUtil.getFieldListFromEntityList(listDept, "partyId", true);
				if(listDeptId == null){
					listDeptId = FastList.newInstance();
				}
				listDeptId.add(partyGroupId);
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listDeptId));
				listAllConditions.add(EntityCondition.makeCondition("year", Long.parseLong(yearStr)));
				List<GenericValue> recruitmentAnticipateList = delegator.findList("RecruitAnticipateAndPartyAndPosType", 
						EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
				totalRows = recruitmentAnticipateList.size();
				if(end > totalRows){
					end = totalRows;
				}
				recruitmentAnticipateList = recruitmentAnticipateList.subList(start, end);
				for(GenericValue tempGv: recruitmentAnticipateList){
					Map<String, Object> tempMap = tempGv.getAllFields();
					String recruitAnticipateId = tempGv.getString("recruitAnticipateId");
					List<GenericValue> recruitmentAnticipateItem = delegator.findByAnd("RecruitmentAnticipateItem", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), null, false);
					for(GenericValue itemGv: recruitmentAnticipateItem){
						Long recruitAnticipateSeqId = itemGv.getLong("recruitAnticipateSeqId");
						tempMap.put("recruitAnticipateSeqId_" + recruitAnticipateSeqId, recruitAnticipateSeqId);
						tempMap.put("quantity_" + recruitAnticipateSeqId, itemGv.get("quantity"));
						tempMap.put("statusId_" + recruitAnticipateSeqId, itemGv.get("statusId"));
					}
					listIterator.add(tempMap);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	public static Map<String, Object> getRecruitmentAnticipateItemList(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		successResult.put("listReturn", listIterator);
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		Locale locale = (Locale)context.get("locale");
		if(recruitAnticipateId != null){
			try {
				List<GenericValue> recruitmentAnticipateItemList = delegator.findByAnd("RecruitmentAnticipateItem", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), UtilMisc.toList("recruitAnticipateSeqId"), false);
				for(GenericValue tempGv: recruitmentAnticipateItemList){
					Map<String, Object> tempMap = tempGv.getFields(UtilMisc.toList("recruitAnticipateId", "recruitAnticipateSeqId", "quantity", "statusId"));
					Long recruitAnticipateSeqId = tempGv.getLong("recruitAnticipateSeqId");
					tempMap.put("month", UtilProperties.getMessage("BaseHRUiLabels", "HRCommonMonth", locale) + " " + (recruitAnticipateSeqId + 1));
					String changeReason = RecruitmentHelper.getChangeReasonRecruitmentAnticipateItem(delegator, recruitAnticipateId, recruitAnticipateSeqId);
					tempMap.put("changeReason", changeReason);
					tempMap.put("newStatusId", tempGv.get("statusId"));
					listIterator.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCandidateInRecruitment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	successResult.put("listIterator", listIterator);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null;
		if(recruitmentPlanId == null){
			successResult.put("TotalRows", String.valueOf(totalRows));
			return successResult;
		}
		listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("firstName");
		}
		try {
			List<GenericValue> recruitmentCandidateList = delegator.findList("RecruitmentCandidateAndPerson", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
			totalRows = recruitmentCandidateList.size();
			if(end > totalRows){
				end = totalRows;
			}
			recruitmentCandidateList = recruitmentCandidateList.subList(start, end);
			for(GenericValue tempGv: recruitmentCandidateList){
				Map<String, Object> tempMap = tempGv.getAllFields();
				String partyId = tempGv.getString("partyId");
				//kiem tra xem ung vien co trong vong trung tuyen khong
				GenericValue passedRoundCandidate = delegator.findOne("RecruitmentRoundCandidate", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "partyId", partyId, "roundOrder", 0L), false);
				if(passedRoundCandidate != null){
					//ung vien da trung tuyen
					GenericValue recruitmentPlanRound = delegator.findOne("RecruitmentPlanRound", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", 0L), false);
					tempMap.put("roundName", recruitmentPlanRound.getString("roundName"));
					tempMap.put("statusId", passedRoundCandidate.get("statusId"));
				}else{
					//ung vien chua trung tuyen, tim kiem vong tuyen dung hien tai cua ung vien
					List<GenericValue> roundCandidateList = delegator.findByAnd("RecruitmentRoundCandidate", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "partyId", partyId), UtilMisc.toList("-roundOrder"), false);
					if(UtilValidate.isNotEmpty(roundCandidateList)){
						GenericValue currRoundCandidate = roundCandidateList.get(0);
						GenericValue recruitmentPlanRound = delegator.findOne("RecruitmentPlanRound", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, 
								"roundOrder", currRoundCandidate.get("roundOrder")), false);
						tempMap.put("roundName", recruitmentPlanRound.getString("roundName"));
						tempMap.put("statusId", currRoundCandidate.get("statusId"));
					}
				}
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCandidateInRecruitRound(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String roundOrderStr = parameters.get("roundOrder") != null? parameters.get("roundOrder")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(recruitmentPlanId == null || roundOrderStr == null){
    		return successResult;
    	}
    	Long roundOrder = Long.parseLong(roundOrderStr);
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("firstName");
    	}
    	try {
			listIterator = delegator.find("RecruitmentRoundCandidateAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCandidateEvaluatedInRecruitRound(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String roundOrderStr = parameters.get("roundOrder") != null? parameters.get("roundOrder")[0] : null; 
    	String statusEvaludatedId = parameters.get("statusEvaludatedId") != null? parameters.get("statusEvaludatedId")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String examinerId = userLogin.getString("partyId");
    	if(recruitmentPlanId == null || roundOrderStr == null){
    		return successResult;
    	}
    	Long roundOrder = Long.parseLong(roundOrderStr);
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
    	listAllConditions.add(EntityCondition.makeCondition("examinerId", examinerId));
    	listAllConditions.add(EntityCondition.makeCondition("statusEvaludatedId", statusEvaludatedId));
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("firstName");
    	}
    	try {
			listIterator = delegator.find("RecruitRoundCandidateExaminerAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentRequireCond(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentRequireId = parameters.get("recruitmentRequireId") != null? parameters.get("recruitmentRequireId")[0] : null;
    	try {
	    	if(recruitmentRequireId != null){
	    		listAllConditions.add(EntityCondition.makeCondition("recruitmentRequireId", recruitmentRequireId));
	    		if(UtilValidate.isEmpty(listSortFields)){
	    			listSortFields.add("recruitmentReqCondTypeName");
	    		}
				listIterator = delegator.find("RecruitmentRequireCondAndType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				successResult.put("listIterator", listIterator);
	    	}
    	} catch (GenericEntityException e) {
    		e.printStackTrace();
    	}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCandidateInterviewOrder(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String roundOrderStr = parameters.get("roundOrder") != null? parameters.get("roundOrder")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(recruitmentPlanId == null || roundOrderStr == null){
    		return successResult;
    	}
    	Long roundOrder = Long.parseLong(roundOrderStr);
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
    	listAllConditions.add(EntityCondition.makeCondition("statusId", "RR_RECRUITING"));
    	listSortFields.add("interviewOrder");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("firstName");
    	}
    	try {
    		Set<String> selectedField = FastSet.newInstance();
    		selectedField.add("partyId");
    		selectedField.add("recruitmentPlanId");
    		selectedField.add("roundOrder");
    		selectedField.add("recruitCandidateId");
    		selectedField.add("fullName");
    		selectedField.add("gender");
    		selectedField.add("birthDate");
    		selectedField.add("dateInterview");
    		selectedField.add("interviewOrder");
    		selectedField.add("emailAddress");
    		selectedField.add("contactNumber");
    		selectedField.add("areaCode");
    		selectedField.add("countryCode");
			listIterator = delegator.find("RecruitmentCandidateInterviewAndPerson", EntityCondition.makeCondition(listAllConditions), null, selectedField, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentRoundSubjectParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	successResult.put("listIterator", listIterator);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String roundOrderStr = parameters.get("roundOrder") != null? parameters.get("roundOrder")[0] : null; 
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(recruitmentPlanId == null || roundOrderStr == null || partyId == null){
    		return successResult;
    	}
    	Long roundOrder = Long.parseLong(roundOrderStr);
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
    	try {
			List<GenericValue> listRoundSubjectList = delegator.findList("RecruitmentPlanRoundAndSubject", 
					EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toList("subjectName"), opts, false);
			totalRows = listRoundSubjectList.size();
			if(end > totalRows){
				end = totalRows;
			}
			listRoundSubjectList = listRoundSubjectList.subList(start, end);
			for(GenericValue tempGv: listRoundSubjectList){
				Map<String, Object> tempMap = tempGv.getAllFields();
				GenericValue recruitmentRoundSubjectParty = delegator.findOne("RecruitmentRoundSubjectParty", 
						UtilMisc.toMap("partyId", partyId, "recruitmentPlanId", recruitmentPlanId, 
								"roundOrder", roundOrder, "subjectId", tempGv.get("subjectId")), false);
				tempMap.put("partyId", partyId);
				if(recruitmentRoundSubjectParty != null){
					tempMap.put("point", recruitmentRoundSubjectParty.get("point"));
				}
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitRoundCandidateExaminer(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	EntityListIterator listIterator = null;
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String roundOrderStr = parameters.get("roundOrder") != null? parameters.get("roundOrder")[0] : null; 
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(recruitmentPlanId == null || roundOrderStr == null || partyId == null){
    		return successResult;
    	}
    	Long roundOrder = Long.parseLong(roundOrderStr);
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
    	listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("firstName");
    		}
			listIterator = delegator.find("RecruitRoundCandidateAndExaminer", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentProcessCandidate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	successResult.put("listIterator", listIterator);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null; 
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
    	if(recruitmentPlanId == null || partyId == null){
    		return null;
    	}
    	listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
    	listAllConditions.add(EntityCondition.makeCondition("roundOrder", EntityJoinOperator.NOT_EQUAL, 0L));
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add(0, "roundOrder");
    	Set<String> fieldsToSelect = FastSet.newInstance();
    	fieldsToSelect.add("partyId");
    	fieldsToSelect.add("recruitmentPlanId");
    	fieldsToSelect.add("roundOrder");
    	fieldsToSelect.add("roundName");
    	fieldsToSelect.add("statusId");
    	fieldsToSelect.add("resultTypeId");
    	fieldsToSelect.add("dateInterview");
    	fieldsToSelect.add("comment");
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
    	try {
			List<GenericValue> recruitmentProcessCandidateList = delegator.findList("RecruitmentRoundCandidateAndPerson", EntityCondition.makeCondition(listAllConditions), 
					fieldsToSelect, listSortFields, null, false);
			totalRows = recruitmentProcessCandidateList.size();
			successResult.put("TotalRows", String.valueOf(totalRows));
			if(end > totalRows){
				end = totalRows;
			}
			recruitmentProcessCandidateList = recruitmentProcessCandidateList.subList(start, end);
			for(GenericValue recruitmentProcessCandidate: recruitmentProcessCandidateList){
				Map<String, Object> tempMap = recruitmentProcessCandidate.getAllFields();
				Double totalPoint = RecruitmentHelper.getTotalPonitOfCandidateInRound(delegator, recruitmentPlanId, 
						recruitmentProcessCandidate.getLong("roundOrder"), partyId);
				tempMap.put("totalPoint", totalPoint);
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createHRPlanning(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		try {
			GenericValue humanResourcePlanning = delegator.findOne("HumanResourcePlanning", 
					UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "customTimePeriodId", customTimePeriodId), false);
			if(humanResourcePlanning == null){
				humanResourcePlanning = delegator.makeValue("HumanResourcePlanning");
				humanResourcePlanning.setPKFields(context);
			}
			humanResourcePlanning.setNonPKFields(context);
			humanResourcePlanning.put("createdByPartyId", partyId);
			humanResourcePlanning.put("statusId", "HR_PLANNING_WAIT");
			delegator.createOrStore(humanResourcePlanning);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Long year = (Long)context.get("year");
		Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> recruitmentAnticipateList = delegator.findByAnd("RecruitmentAnticipate", 
					UtilMisc.toMap("partyId", partyId, "emplPositionTypeId",emplPositionTypeId, "year", year), null, false);
			if(UtilValidate.isNotEmpty(recruitmentAnticipateList)){
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentAnticipateIsCreated",
						 UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"), 
									"year", year, 
									"groupName", partyGroup.get("groupName")), locale));
			}
			GenericValue recruitmentAnticipate = delegator.makeValue("RecruitmentAnticipate");
			recruitmentAnticipate.setNonPKFields(context);
			recruitmentAnticipate.set("createdByPartyId", userLogin.get("partyId"));
			recruitmentAnticipate.set("statusId", "REC_ATCP_HR_WAIT");
			String recruitAnticipateId = delegator.getNextSeqId("RecruitmentAnticipate");
			recruitmentAnticipate.set("recruitAnticipateId", recruitAnticipateId);
			delegator.create(recruitmentAnticipate);
			retMap.put("recruitAnticipateId", recruitAnticipateId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createRecruitmentAnticipateItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentAnticipateItem = delegator.makeValue("RecruitmentAnticipateItem");
		recruitmentAnticipateItem.setAllFields(context, false, null, null);
		recruitmentAnticipateItem.set("statusId", "REC_ATCP_HR_WAIT");
		try {
			delegator.createOrStore(recruitmentAnticipateItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateHRPlanning(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String approvedPartyId = (String)context.get("approvedPartyId");
		String headerUiLabelKey = null;
		try {
			GenericValue humanResourcePlanning = delegator.findOne("HumanResourcePlanning", 
					UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "customTimePeriodId", customTimePeriodId), false);
			
			if(humanResourcePlanning == null){
				Map<String, Object> resultService = dispatcher.runSync("createHRPlanning", context);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				headerUiLabelKey = "HRPlanningIsCreated";
			}else{
				String statusId = humanResourcePlanning.getString("statusId");
				if(!"HR_PLANNING_WAIT".equals(statusId) && !"HR_PLANNING_REJ".equals(statusId)){
					GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotUpdateHRPlanningBecauseStatus", UtilMisc.toMap("status", status.get("description")), locale));
				}
				if("HR_PLANNING_REJ".equals(statusId)){
					headerUiLabelKey = "HRPlanningIsUpdated";
				}
				humanResourcePlanning.setNonPKFields(context);
				humanResourcePlanning.set("statusId", "HR_PLANNING_WAIT");
				humanResourcePlanning.store();
				if(approvedPartyId == null){
					approvedPartyId = humanResourcePlanning.getString("approvedPartyId");
				}
			}
			if(approvedPartyId != null && headerUiLabelKey != null){
				//=====create notification====
				String action = "ViewListHRPlanning";
				String headerYear = CommonUtil.getMonthYearPeriodNameByCustomTimePeriod(delegator, customTimePeriodId);
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				String header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", headerUiLabelKey, 
						UtilMisc.toMap("periodName", headerYear, "emplPositionType", emplPositionType.getString("description")), locale);
				CommonUtil.sendNotify(dispatcher, locale, approvedPartyId, userLogin, header, action, null);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> approveHRPlanning(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		try {
			GenericValue humanResourcePlanning = delegator.findOne("HumanResourcePlanning", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "customTimePeriodId", customTimePeriodId), false);
			if(humanResourcePlanning == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotFindHRPlanningToApproval", locale));
			}
			String statusId = (String)context.get("statusId");
			String comment = (String)context.get("comment");
			humanResourcePlanning.set("statusId", statusId);
			humanResourcePlanning.set("comment", comment);
			humanResourcePlanning.store();
			
			//======create nofification
			String createdByPartyId = humanResourcePlanning.getString("createdByPartyId");
			String action = "ViewListHRPlanning";
			String headerYear = CommonUtil.getMonthYearPeriodNameByCustomTimePeriod(delegator, customTimePeriodId);
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			String header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "HRPlanningIsApproved", 
					UtilMisc.toMap("periodName", headerYear, "emplPositionType", emplPositionType.getString("description")), locale);
			CommonUtil.sendNotify(dispatcher, locale, createdByPartyId, userLogin, header, action, null);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "ApprovalHRPlanningSuccess", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentRequireListJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if(parameters.get("partyId") == null){
    		return successResult;
    	}
    	String partyGroupId = parameters.get("partyId")[0];
    	String yearStr = parameters.get("year") != null? parameters.get("year")[0] : null;
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
    	try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> listParty = buildOrg.getAllDepartmentList(delegator);
			List<String> listPartyId = EntityUtil.getFieldListFromEntityList(listParty, "partyId", true);
			if(UtilValidate.isEmpty(listPartyId)){
				listPartyId = FastList.newInstance();
			}
			listPartyId.add(partyGroupId);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyId));
			listAllConditions.add(EntityCondition.makeCondition("year", Long.parseLong(yearStr)));
			List<GenericValue> recruitReqList = delegator.findList("RecruitmentRequireAndParty", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			totalRows = recruitReqList.size();
			if(end > totalRows){
				end = totalRows;
			}
			recruitReqList = recruitReqList.subList(start, end);
			Map<String, Object> resultService = null;
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("userLogin", context.get("userLogin"));
			for(GenericValue tempGv: recruitReqList){
				Map<String, Object> tempMap = tempGv.getAllFields();
				ctxMap.put("partyId", tempGv.get("partyId"));
				ctxMap.put("emplPositionTypeId", tempGv.get("emplPositionTypeId"));
				ctxMap.put("year", tempGv.get("year"));
				ctxMap.put("month", tempGv.get("month"));
				resultService = dispatcher.runSync("getRecruitAnticipateByMonthYear", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				Boolean isPlanned = (Boolean)resultService.get("isPlanned");
				/*if(isPlanned){
					tempMap.put("quantityAppr", resultService.get("quantity"));
				}*/
				tempMap.put("recruitAnticipatePlanCreated", isPlanned);
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
    	successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentRequireApproved(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-year");
    		listSortFields.add("-month");
    	}
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("statusId", "RECREQ_DRT_ACC"));
			listIterator = delegator.find("RecruitmentRequireAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentSubject(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("subjectName");
    	}
    	try {
			listIterator = delegator.find("RecruitmentSubject", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentCostItemType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("recruitCostItemName");
		}
		try {
			listIterator = delegator.find("RecruitmentCostItemAndCatType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentCatType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("recruitCostCatName");
		}
		try {
			listIterator = delegator.find("RecruitmentCostCategoryType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentListCostItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("recruitCostItemName");
		}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String recruitmentPlanId = parameters.get("recruitmentPlanId") != null? parameters.get("recruitmentPlanId")[0] : null;
		if(recruitmentPlanId == null){
			return successResult;
		}
		listAllConditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
		try {
			listIterator = delegator.find("RecruitmentPlanCostItemAndType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRecruitmentPlanEngagedBoard(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Long year = Long.parseLong(parameters.get("year")[0]);
		try {
			listAllConditions.add(EntityCondition.makeCondition("partyId", userLogin.get("partyId")));
			listAllConditions.add(EntityCondition.makeCondition("year", year));
			listIterator = delegator.find("RecruitmentPlanAndBoardAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createRecruitmentRoundSubject(DispatchContext dctx, Map<String, Object> conext){
		Delegator delegator = dctx.getDelegator();
		String subjectId = (String)conext.get("subjectId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		if(subjectId == null){
			subjectId = delegator.getNextSeqId("RecruitmentSubject");
		}
		GenericValue recruitmentSubject = delegator.makeValue("RecruitmentSubject");
		recruitmentSubject.setNonPKFields(conext);
		recruitmentSubject.put("subjectId", subjectId);
		try {
			recruitmentSubject.create();
			retMap.put("subjectId", subjectId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createRecruitmentPlanRoundInterviewer(DispatchContext dctx, Map<String, Object> conext){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanRoundInterviewer = delegator.makeValue("RecruitmentPlanRoundInterviewer");
		recruitmentPlanRoundInterviewer.setAllFields(conext, false, null, null);
		try {
			delegator.create(recruitmentPlanRoundInterviewer);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> updateRecruitmentRoundSubject(DispatchContext dctx, Map<String, Object> conext){
		Delegator delegator = dctx.getDelegator();
		String subjectId = (String)conext.get("subjectId");
		
		GenericValue recruitmentSubject;
		try {
			recruitmentSubject = delegator.findOne("RecruitmentSubject", UtilMisc.toMap("subjectId", subjectId), false);
			if(recruitmentSubject == null){
				return ServiceUtil.returnError("cannot find recruitment subject to update");
			}
			recruitmentSubject.setNonPKFields(conext);
			recruitmentSubject.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateRecruitmentRoundSubjectParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanRoundSubject = delegator.makeValue("RecruitmentPlanRoundSubject");
		recruitmentPlanRoundSubject.setAllFields(context, false, null, null);
		try {
			recruitmentPlanRoundSubject.store();
			GenericValue recruitmentRoundSubjectParty = delegator.makeValue("RecruitmentRoundSubjectParty");
			recruitmentRoundSubjectParty.setAllFields(context, false, null, null);
			delegator.createOrStore(recruitmentRoundSubjectParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentPlanListJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	successResult.put("listIterator", listIterator);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyId");
		String yearStr = request.getParameter("year");
		successResult.put("TotalRows", String.valueOf(totalRows));
		if(partyGroupId == null){
			return successResult;
		}
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Organization buildOrg;
		try {
			buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> listParty = buildOrg.getAllDepartmentList(delegator);
			List<String> listPartyId = EntityUtil.getFieldListFromEntityList(listParty, "partyId", true);
			if(UtilValidate.isEmpty(listPartyId)){
				listPartyId = FastList.newInstance();
			}
			listPartyId.add(partyGroupId);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyId));
			listAllConditions.add(EntityCondition.makeCondition("year", Long.parseLong(yearStr)));
			
			List<GenericValue> recruitmentPlanList = delegator.findList("RecruitmentPlanAndParty", EntityCondition.makeCondition(listAllConditions), 
					null, listSortFields, opts, false);
			totalRows = recruitmentPlanList.size();
			if(end > totalRows){
				end = totalRows;
			}
			recruitmentPlanList = recruitmentPlanList.subList(start, end);
			for(GenericValue tempGv: recruitmentPlanList){
				Map<String, Object> tempMap = tempGv.getAllFields();
				String recruitmentRequireId = tempGv.getString("recruitmentRequireId");
				if(recruitmentRequireId != null){
					tempMap.put("isCreatedRecruitRequire", true);
				}else{
					tempMap.put("isCreatedRecruitRequire", false);
				}
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> createRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue recruitmentRequire = delegator.makeValue("RecruitmentRequire");
		Locale locale = (Locale)context.get("locale");
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		Long month = (Long)context.get("month");
		Long year = (Long)context.get("year");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String enumRecruitReqTypeId = (String)context.get("enumRecruitReqTypeId");
		try {
			recruitmentRequire.setNonPKFields(context);
			String partyId = userLogin.getString("partyId");
			if(quantity == null){
				quantity = BigDecimal.ZERO;
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getRecruitAnticipateByMonthYear", context, userLogin, (TimeZone)context.get("timeZone"), locale);
			ctxMap.put("month", month);
			ctxMap.put("year", year);
			ctxMap.put("locale", locale);
			ctxMap.put("timeZone", context.get("timeZone"));
			Map<String, Object> resultServices = dispatcher.runSync("getRecruitAnticipateByMonthYear", ctxMap);
			BigDecimal quantityAppr = BigDecimal.ZERO;
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
			if((Boolean)resultServices.get("isPlanned") && resultServices.get("quantity") != null){
				quantityAppr = (BigDecimal)resultServices.get("quantity");
			}
			if(quantity.compareTo(quantityAppr) > 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuantityPlannedGreateThanQuantityApproved", locale));
			}
			if("RECRUIT_REQUIRE_UNPLANNED".equals(enumRecruitReqTypeId)){
				BigDecimal quantityUnplanned = (BigDecimal)context.get("quantityUnplanned");
				if(quantityUnplanned == null || quantityUnplanned.compareTo(BigDecimal.ZERO) <= 0){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuantityUnplannedMustGreaterZero", locale));
				}
				recruitmentRequire.put("quantityUnplanned", quantityUnplanned);
			}
			//recruitmentRequire.set("statusId", "HR_RECREQ_WAIT");
			String recruitmentRequireId = delegator.getNextSeqId("RecruitmentRequire");
			recruitmentRequire.set("recruitmentRequireId", recruitmentRequireId);
			recruitmentRequire.set("createdByPartyId", partyId);
			delegator.create(recruitmentRequire);
			String approvedPartyId = (String)context.get("approvedPartyId");
			if(approvedPartyId != null){
				String action = "ViewListRecruitmentRequirement";
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", recruitmentRequire.get("emplPositionTypeId")), false);
				String header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "AppovalRecruitmentRequire", 
						UtilMisc.toMap("month", month, "year", year, 
								"emplPositionType", emplPositionType.get("description"),
								"department", PartyHelper.getPartyName(delegator, (String)context.get("partyId"), false)), locale);
				CommonUtil.sendNotify(dispatcher, locale, approvedPartyId, userLogin, header, action, null);
			}
			retMap.put("recruitmentRequireId", recruitmentRequireId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		BigDecimal quantityUnplanned = (BigDecimal)context.get("quantityUnplanned");
		String recruitmentFormTypeId = (String)context.get("recruitmentFormTypeId");
		try {
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			if(recruitmentRequire == null){
				return ServiceUtil.returnError("cannot not find recruitment requirement to update");
			}
			String statusId = recruitmentRequire.getString("statusId");
			Map<String, Object> resultService = dispatcher.runSync("checkRecruitmentRequireEditable", 
					UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId, "userLogin", userLogin, "locale", locale));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			Boolean isEditable = (Boolean)resultService.get("isEditable");
			if(!isEditable){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotUpdateRecruitmentRequire", UtilMisc.toMap("status", statusItem.get("description", locale)), locale));
			}
			BigDecimal quantityAppr = BigDecimal.ZERO;
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("emplPositionTypeId", recruitmentRequire.get("emplPositionTypeId"));
			ctxMap.put("partyId", recruitmentRequire.get("partyId"));
			ctxMap.put("month", recruitmentRequire.get("month"));
			ctxMap.put("year", recruitmentRequire.get("year"));
			ctxMap.put("locale", locale);
			Map<String, Object> resultServices = dispatcher.runSync("getRecruitAnticipateByMonthYear", ctxMap);
			if((Boolean)resultServices.get("isPlanned") && resultServices.get("quantity") != null){
				quantityAppr = (BigDecimal)resultServices.get("quantity");
			}
			if(quantity == null){
				quantity = BigDecimal.ZERO;
			}
			if(quantity.compareTo(quantityAppr) > 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "QuantityPlannedGreateThanQuantityApproved", locale));
			}
			String enumRecruitReqTypeId = recruitmentRequire.getString("enumRecruitReqTypeId");
			if("RECRUIT_REQUIRE_UNPLANNED".equals(enumRecruitReqTypeId)){
				recruitmentRequire.set("quantityUnplanned", quantityUnplanned);
			}
			recruitmentRequire.set("quantity", quantity);
			recruitmentRequire.set("recruitmentFormTypeId", recruitmentFormTypeId);
			if(!"RECREQ_HR_WAIT".equals(statusId)){
				recruitmentRequire.set("statusId", "RECREQ_HR_WAIT");
				resultService = dispatcher.runSync("createRecruitmentRequireStatus", 
						UtilMisc.toMap("newStatusId", "RECREQ_HR_WAIT", "recruitmentRequireId", recruitmentRequireId, 
								"changeReason", context.get("changeReason"), 
								"userLogin", userLogin, "locale", locale));
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			recruitmentRequire.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteRecruitmentRequireConds(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		try {
			List<GenericValue> recruitReqCondsList = delegator.findByAnd("RecruitmentRequireCond", 
					UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), null, false);
			for(GenericValue tempGv: recruitReqCondsList){
				tempGv.remove();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentRequireConds(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentRequireConds = delegator.makeValue("RecruitmentRequireCond");
		recruitmentRequireConds.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentRequireConds);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentPlanConds(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanConds = delegator.makeValue("RecruitmentPlanConds");
		recruitmentPlanConds.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanConds);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getRecruitmentRequireConds(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		try {
			List<GenericValue> recruitReqCondsList = delegator.findByAnd("RecruitmentRequireCondAndType", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), 
					UtilMisc.toList("recruitmentReqCondTypeName"), false);
			retMap.put("results", recruitReqCondsList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> approvalRecruitmentRequire(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String recruitmentRequireId = (String)context.get("recruitmentRequireId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
		try {
			GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
			if(recruitmentRequire == null){
				return ServiceUtil.returnError("cannot find recruitment require to update");
			}
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getNextStatusRecruitmentRequire", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("getNextStatusRecruitmentRequire", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String newStatusId = (String)resultService.get("newStatusId");
			recruitmentRequire.set("statusId", newStatusId);
			//recruitmentRequire.set("quantity", context.get("quantity"));
			recruitmentRequire.set("commentApproval", context.get("commentApproval"));
			recruitmentRequire.store();
			retMap.put("newStatusId", newStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createRecruitmentReqCondType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		Delegator delegator = dctx.getDelegator();
		String recruitmentReqCondTypeId = (String)context.get("recruitmentReqCondTypeId");
		String recruitmentReqCondTypeName = (String)context.get("recruitmentReqCondTypeName");
		if(recruitmentReqCondTypeName == null || recruitmentReqCondTypeName.trim().length() <= 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentReqCondTypeNameIsEmpty", locale));
		}
		try {
			List<GenericValue> recruitmentReqCondType = delegator.findByAnd("RecruitmentReqCondType", UtilMisc.toMap("recruitmentReqCondTypeName", recruitmentReqCondTypeName), null, false);
			if(UtilValidate.isNotEmpty(recruitmentReqCondType)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentReqCondTypeNameIsExists", 
						UtilMisc.toMap("recruitmentReqCondTypeName", recruitmentReqCondTypeName), locale));
			}
			GenericValue newEntity = delegator.makeValue("RecruitmentReqCondType");
			newEntity.setNonPKFields(context);
			if(recruitmentReqCondTypeId == null){
				recruitmentReqCondTypeId = delegator.getNextSeqId("RecruitmentReqCondType");
			}
			newEntity.set("recruitmentReqCondTypeId", recruitmentReqCondTypeId);
			delegator.create(newEntity);
			retMap.put("recruitmentReqCondTypeId", recruitmentReqCondTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return retMap;
	}
	
	public static Map<String, Object> createRecruitmentCostCatType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String recruitCostCatTypeId = (String)context.get("recruitCostCatTypeId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale)); 
		if(recruitCostCatTypeId == null){
			recruitCostCatTypeId = delegator.getNextSeqId("RecruitmentCostCategoryType");
		}
		GenericValue recruitmentCostCategoryType = delegator.makeValue("RecruitmentCostCategoryType");
		recruitmentCostCategoryType.setNonPKFields(context);
		recruitmentCostCategoryType.set("recruitCostCatTypeId", recruitCostCatTypeId);
		retMap.put("recruitCostCatTypeId", recruitCostCatTypeId);
		try {
			recruitmentCostCategoryType.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateRecruitmentCostCatType(DispatchContext dctx, Map<String, Object> context){
		String recruitCostCatTypeId = (String)context.get("recruitCostCatTypeId");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue recruitmentCostCategoryType = delegator.findOne("RecruitmentCostCategoryType", UtilMisc.toMap("recruitCostCatTypeId", recruitCostCatTypeId), false);
			if(recruitmentCostCategoryType == null){
				return ServiceUtil.returnError("cannot find cost category to update");
			}
			recruitmentCostCategoryType.setNonPKFields(context);
			recruitmentCostCategoryType.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentCostItemType(DispatchContext dctx, Map<String, Object> context){
		String recruitCostItemTypeId = (String)context.get("recruitCostItemTypeId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(); 
		if(recruitCostItemTypeId == null){
			recruitCostItemTypeId = delegator.getNextSeqId("RecruitmentCostItemType");
		}
		GenericValue recruitmentCostItemType = delegator.makeValue("RecruitmentCostItemType");
		recruitmentCostItemType.setNonPKFields(context);
		recruitmentCostItemType.set("recruitCostItemTypeId", recruitCostItemTypeId);
		try {
			recruitmentCostItemType.create();
			retMap.put("recruitCostItemTypeId", recruitCostItemTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> updateRecruitmentCostItemType(DispatchContext dctx, Map<String, Object> context){
		String recruitCostItemTypeId = (String)context.get("recruitCostItemTypeId");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue recruitCostItemType = delegator.findOne("RecruitmentCostItemType", UtilMisc.toMap("recruitCostItemTypeId", recruitCostItemTypeId), false);
			if(recruitCostItemType == null){
				return ServiceUtil.returnError("cannot find recruitment cost item type to update");
			}
			recruitCostItemType.setNonPKFields(context);
			recruitCostItemType.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentPlanRoundSubject(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanRoundSubject = delegator.makeValue("RecruitmentPlanRoundSubject");
		recruitmentPlanRoundSubject.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanRoundSubject);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentPlanRound(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanRound = delegator.makeValue("RecruitmentPlanRound");
		recruitmentPlanRound.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanRound);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentPlanCostItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanCostItem = delegator.makeValue("RecruitmentPlanCostItem");
		recruitmentPlanCostItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanCostItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createRecruitmentPlanBoard(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanBoard = delegator.makeValue("RecruitmentPlanBoard");
		recruitmentPlanBoard.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanBoard);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentPlan(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue recruitmentPlan = delegator.makeValue("RecruitmentPlan");
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		//String recruitmentPlanName = (String) context.get("recruitmentPlanName");
		if(recruitmentPlanId == null){
			recruitmentPlanId = delegator.getNextSeqId("RecruitmentPlan");
		}
		recruitmentPlan.setNonPKFields(context);
		recruitmentPlan.put("recruitmentPlanId", recruitmentPlanId);
		recruitmentPlan.put("createdPartyId", userLogin.get("partyId"));
		recruitmentPlan.put("statusId", "HR_RECPLAN_EXE");
		retMap.put("recruitmentPlanId", recruitmentPlanId);
		try {
			recruitmentPlan.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createRecruitmentCandidate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentCandidate = delegator.makeValue("RecruitmentCandidate");
		recruitmentCandidate.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentCandidate);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentRoundCandidate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Long roundOrder = (Long)context.get("roundOrder");
		String statusId = (String)context.get("statusId");
		Locale locale = (Locale)context.get("locale");
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue recruitmentRoundCandidate  = delegator.findOne("RecruitmentRoundCandidate", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "partyId", partyId, "roundOrder", roundOrder), false);
			if(UtilValidate.isNotEmpty(recruitmentRoundCandidate)){
				GenericValue recruitmentPlanRound = delegator.findOne("RecruitmentPlanRound", 
						UtilMisc.toMap("recruitmentPlanId", recruitmentRoundCandidate.get("recruitmentPlanId"), "roundOrder", recruitmentRoundCandidate.get("roundOrder")), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateAddedRecRound", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "roundName", recruitmentPlanRound.get("roundName")), locale));
			}
			
			List<GenericValue> listCandidateNextRecruitmentRound = RecruitmentHelper.getNextRecruitmentRoundListOfCandidate(delegator, partyId, recruitmentPlanId, roundOrder);
			if(UtilValidate.isNotEmpty(listCandidateNextRecruitmentRound)){
				GenericValue nextRoundCandidateRec = listCandidateNextRecruitmentRound.get(0);
				GenericValue recruitmentPlanRound = delegator.findOne("RecruitmentPlanRound", 
						UtilMisc.toMap("recruitmentPlanId", nextRoundCandidateRec.get("recruitmentPlanId"), "roundOrder", nextRoundCandidateRec.get("roundOrder")), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateAddedRecRound", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "roundName", recruitmentPlanRound.get("roundName")), locale));
			}
			recruitmentRoundCandidate = delegator.makeValue("RecruitmentRoundCandidate");
			recruitmentRoundCandidate.setAllFields(context, false, null, null);
			if(roundOrder == 0){
				statusId = "RR_REC_RECEIVE";
			}else{
				if(statusId == null){
					statusId = "RR_RECRUITING";
				}
				Long interviewOrder = RecruitmentHelper.getNextRecruitmentInterviewOrder(delegator, recruitmentPlanId, roundOrder);
				recruitmentRoundCandidate.put("interviewOrder", interviewOrder);
			}
			
			recruitmentRoundCandidate.set("statusId", statusId);
			recruitmentRoundCandidate.create();
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateRecruitmentRoundCandidate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		String partyId = (String)context.get("partyId");
		Long roundOrder = (Long)context.get("roundOrder");
		Locale locale = (Locale)context.get("locale");
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue recruitmentRoundCandidate = delegator.findOne("RecruitmentRoundCandidate", UtilMisc.toMap("partyId", partyId, "recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), false);
			if(recruitmentRoundCandidate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotFindPartyInRecruitToUpdate", locale));
			}
			String currStatusId = recruitmentRoundCandidate.getString("statusId");
			if(!"RR_RECRUITING".equals(currStatusId) && roundOrder != 0){
				GenericValue currStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIsCannotUpdate",
						UtilMisc.toMap("currStatus",  currStatus.get("description"), 
								"candidateName", PartyUtil.getPersonName(delegator, partyId)), locale));
			}
			if(roundOrder == 0 && !"RR_REC_EMPL".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotUpdateForPassedRound", locale));
			}
			recruitmentRoundCandidate.setNonPKFields(context);
			recruitmentRoundCandidate.store();
			if("RR_REC_PASSED".equals(statusId)){
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("recruitmentPlanId", recruitmentPlanId);
				ctxMap.put("partyId", partyId);
				ctxMap.put("locale", locale);
				Long nextRoundOrder = RecruitmentHelper.getNextRecruitmentRoundOrder(delegator, roundOrder, recruitmentPlanId);
				if(nextRoundOrder == 0){
					ctxMap.put("statusId", "RR_REC_RECEIVE");
				}else{
					ctxMap.put("statusId", "RR_RECRUITING");
				}
				ctxMap.put("roundOrder", nextRoundOrder);
				Map<String, Object> resultService = dispatcher.runSync("createRecruitmentRoundCandidate", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return resultService;
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getRecruitmentPlanBoard(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> listRecBoard = delegator.findList("RecruitmentPlanBoardAndPerson", 
					EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId), 
					UtilMisc.toSet("recruitmentPlanId", "partyId", "fullName"), UtilMisc.toList("firstName"), null, false);
			retMap.put("listReturn", listRecBoard);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createRecruitRoundCandidateExaminer(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		Long roundOrder = (Long)context.get("roundOrder");
		String partyId = (String)context.get("partyId");
		try {
			List<GenericValue> listPartyInterviewer = delegator.findByAnd("RecruitmentPlanRoundInterviewer", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), null, false);
			Map<String, Object> entityMap = FastMap.newInstance();
			entityMap.put("recruitmentPlanId", recruitmentPlanId);
			entityMap.put("roundOrder", roundOrder);
			entityMap.put("partyId", partyId);
			entityMap.put("statusId", "REC_INTW_NOT_ASSESS");
			for(GenericValue tempGv: listPartyInterviewer){
				String interviewerId = tempGv.getString("interviewerId");
				entityMap.put("examinerId", interviewerId);
				GenericValue recruitRoundCandidateExaminer = delegator.makeValue("RecruitRoundCandidateExaminer");
				recruitRoundCandidateExaminer.setAllFields(entityMap, false, null, null);
				delegator.create(recruitRoundCandidateExaminer);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateRecruitRoundCandidateExaminer(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitRoundCandidateExaminer = delegator.makeValue("RecruitRoundCandidateExaminer");
		recruitRoundCandidateExaminer.setAllFields(context, false, null, null);
		try {
			recruitRoundCandidateExaminer.set("statusId", "REC_INTW_ASSESS");
			delegator.createOrStore(recruitRoundCandidateExaminer);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateRecruitmentRoundIntvwEval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentRoundIntvwEval = delegator.makeValue("RecruitmentRoundIntvwEval");
		recruitmentRoundIntvwEval.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentRoundIntvwEval);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createRecruitmentCostItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentPlanCostItem = delegator.makeValue("RecruitmentPlanCostItem");
		recruitmentPlanCostItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(recruitmentPlanCostItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateRecruitmentCostItem(DispatchContext dctx, Map<String, Object> context){
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		String recruitCostItemTypeId = (String)context.get("recruitCostItemTypeId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue recruitmentPlanCostItem = delegator.findOne("RecruitmentPlanCostItem", 
					UtilMisc.toMap("recruitCostItemTypeId", recruitCostItemTypeId, "recruitmentPlanId", recruitmentPlanId), false);
			if(recruitmentPlanCostItem != null){
				recruitmentPlanCostItem.setNonPKFields(context);
				recruitmentPlanCostItem.store();
			}else{
				Map<String, Object> resultService = dispatcher.runSync("createRecruitmentCostItem", context);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteRecruitmentCostItem(DispatchContext dctx, Map<String, Object> context){
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		String recruitCostItemTypeId = (String)context.get("recruitCostItemTypeId");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue recruitmentPlanCostItem = delegator.findOne("RecruitmentPlanCostItem", 
					UtilMisc.toMap("recruitCostItemTypeId", recruitCostItemTypeId, "recruitmentPlanId", recruitmentPlanId), false);
			if(recruitmentPlanCostItem == null){
				return ServiceUtil.returnError("cannot find cost item to delete");
			}
			recruitmentPlanCostItem.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createRecruitmentChannelType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String recruitChannelTypeId = (String)context.get("recruitChannelTypeId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale)); 
		if(recruitChannelTypeId == null){
			recruitChannelTypeId = delegator.getNextSeqId("RecruitmentChannelType");
		}
		GenericValue recruitmentChannelType = delegator.makeValidValue("RecruitmentChannelType");
		recruitmentChannelType.setNonPKFields(context);
		recruitmentChannelType.set("recruitChannelTypeId", recruitChannelTypeId);
		retMap.put("recruitChannelTypeId", recruitChannelTypeId);
		try {
			recruitmentChannelType.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createRecruitmentSourceType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String recruitSourceTypeId = (String)context.get("recruitSourceTypeId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale)); 
		if(recruitSourceTypeId == null){
			recruitSourceTypeId = delegator.getNextSeqId("RecruitmentSourceType");
		}
		GenericValue recruitmentSourceType = delegator.makeValidValue("RecruitmentSourceType");
		recruitmentSourceType.setNonPKFields(context);
		recruitmentSourceType.set("recruitSourceTypeId", recruitSourceTypeId);
		try {
			recruitmentSourceType.create();
			retMap.put("recruitSourceTypeId", recruitSourceTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getTotalPointPartyInRecruitmentRound(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		String partyId = (String)context.get("partyId");
		String recruitmentPlanId = (String)context.get("recruitmentPlanId");
		Long roundOrder = (Long)context.get("roundOrder");
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> recruitPlanRoundAndSubjectPartyList = delegator.findByAnd("RecruitmentPlanRoundAndSubjectParty", 
					UtilMisc.toMap("partyId", partyId, "recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), null, false);
			Double totalPoint = 0d;
			for(GenericValue tempGv: recruitPlanRoundAndSubjectPartyList){
				Double point = tempGv.getDouble("point");
				Double ratio = tempGv.getDouble("ratio");
				if(point != null && ratio != null){
					totalPoint += point * ratio;
				}
			}
			retMap.put("totalPoint", totalPoint);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	/*public static Map<String, Object> updateCandidateInterviewOrder(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentRoundCandidate = delegator.makeValue("RecruitmentRoundCandidate");
		recruitmentRoundCandidate.setAllFields(context, false, null, null);
		try {
			recruitmentRoundCandidate.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}*/
	public static Map<String, Object> updateRecruitCandidateRoundToZero(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "StoreInterviewSuccess", locale));
		Delegator delegator = ctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String recruitmentPlanId = (String) context.get("recruitmentPlanId");
		try {
			Integer finalRound = 0;
			long finalRoundLong = Long.valueOf(finalRound.longValue());
			GenericValue RecruitmentRoundCandidate = delegator.findOne("RecruitmentRoundCandidate", UtilMisc.toMap("partyId", partyId, "recruitmentPlanId", recruitmentPlanId, "roundOrder", finalRoundLong), false);
			if(UtilValidate.isEmpty(RecruitmentRoundCandidate)){
				GenericValue RecruitmentSpecial = delegator.makeValue("RecruitmentRoundCandidate");
				RecruitmentSpecial.set("partyId", partyId);
				RecruitmentSpecial.set("recruitmentPlanId", recruitmentPlanId);
				RecruitmentSpecial.set("roundOrder", finalRoundLong);
				RecruitmentSpecial.set("statusId", "RR_REC_RECEIVE");
				RecruitmentSpecial.create();
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIsAddedInFinalRound", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> approvalRecruitmentAnticipateItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		Long recruitAnticipateSeqId = (Long)context.get("recruitAnticipateSeqId");
		try {
			Map<String, Object> checkPermContext = ServiceUtil.setServiceFields(dispatcher, "checkPermissionApprRecruitmentAnticipate", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("checkPermissionApprRecruitmentAnticipate", checkPermContext);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			Boolean hasPermission = (Boolean)resultService.get("hasPermission");
			if(hasPermission == null || !hasPermission){
				GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
				String statusId = recruitmentAnticipate.getString("statusId");
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CannotApprRecruitmentAnticipateBecausePermission", 
						UtilMisc.toMap("status", status.get("description", locale)), locale));
			}
			GenericValue recruitmentAnticipateItem = delegator.findOne("RecruitmentAnticipateItem", 
					UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "recruitAnticipateSeqId", recruitAnticipateSeqId), false);
			//TODO use statusValidChange
			recruitmentAnticipateItem.setNonPKFields(context);
			recruitmentAnticipateItem.store();
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentAnticipateStatus", context, userLogin, timeZone, locale);
			resultService = dispatcher.runSync("createRecruitmentAnticipateStatus", ctxMap);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
	}
	
	public static Map<String, Object> updateRecruitmentAnticipate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String recruitAnticipateId = (String)context.get("recruitAnticipateId");
		try {
			GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
			if(recruitmentAnticipate == null){
				return ServiceUtil.returnError("cannot find record RecruitmentAnticipate to update");
			}
			recruitmentAnticipate.setNonPKFields(context);
			recruitmentAnticipate.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createRecruitmentAnticipateStatus(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentAnticipateStatus = delegator.makeValue("RecruitmentAnticipateStatus");
		recruitmentAnticipateStatus.setNonPKFields(context);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		recruitmentAnticipateStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
		recruitmentAnticipateStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
		String recruitAnticipateStatusId = delegator.getNextSeqId("RecruitmentAnticipateStatus");
		recruitmentAnticipateStatus.put("recruitAnticipateStatusId", recruitAnticipateStatusId);
		try {
			delegator.create(recruitmentAnticipateStatus);
			retMap.put("recruitAnticipateStatusId", recruitAnticipateStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createRecruitmentRequireStatus(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue recruitmentRequireStatus = delegator.makeValue("RecruitmentRequireStatus");
		String newStatusId = (String)context.get("newStatusId");
		recruitmentRequireStatus.setNonPKFields(context);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		recruitmentRequireStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
		recruitmentRequireStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
		String recruitmentRequireStatusId = delegator.getNextSeqId("RecruitmentRequireStatus");
		recruitmentRequireStatus.put("recruitmentRequireStatusId", recruitmentRequireStatusId);
		recruitmentRequireStatus.put("statusId", newStatusId);
		try {
			delegator.create(recruitmentRequireStatus);
			retMap.put("recruitmentRequireStatusId", recruitmentRequireStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> getRecruitAnticipateByMonthYear(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Long month = (Long)context.get("month");
		Long year = (Long)context.get("year");
		Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> recruitmentAnticipateItemList = delegator.findByAnd("RecruitmentAnticipateAndItem", 
					UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "partyId", partyId, 
							"year", year, "recruitAnticipateSeqId", month, "statusId", "REC_ATCP_DRT_ACC"), null, false);
			if(UtilValidate.isNotEmpty(recruitmentAnticipateItemList)){
				GenericValue recruitmentAnticipateItem = recruitmentAnticipateItemList.get(0);
				BigDecimal quantity = recruitmentAnticipateItem.getBigDecimal("quantity");
				retMap.put("quantity", quantity);
				retMap.put("isPlanned", true);
			}else{
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				retMap.put("isPlanned", false);
				retMap.put("message", UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentAnticipateIsNotCreatedOrApproved",
						UtilMisc.toMap("emplPositionType", emplPositionType.getString("description"), 
								"groupName", partyGroup.get("groupName"), 
								"year", String.valueOf(year),
								"month", String.valueOf(month + 1)), locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> deletePersonFamilyBackground(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String personFamilyBackgroundId = (String) context.get("personFamilyBackgroundId");
		if(UtilValidate.isNotEmpty(personFamilyBackgroundId)){
			try {
				GenericValue pfbg = delegator.findOne("PersonFamilyBackground", UtilMisc.toMap("personFamilyBackgroundId",personFamilyBackgroundId), false);
				if(UtilValidate.isNotEmpty(pfbg)){
					String partyId= pfbg.getString("partyFamilyId");
					if(UtilValidate.isNotEmpty(partyId)){
						List<GenericValue> ctm= EntityUtil.filterByDate(delegator.findList("PartyContactMech", EntityCondition.makeCondition("partyId",partyId), null, null, null, false));
						for(GenericValue entry:ctm){
							String contactMechId= entry.getString("contactMechId");
							try {
								dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",contactMechId,"userLogin", userLogin));
							} catch (GenericServiceException e) {
								String errMsg = "Fatal error calling deletePartyContactMech service: " + e.toString();
						        Debug.logError(e, errMsg, module);
								e.printStackTrace();
							}
						}
					}
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					pfbg.set("thruDate", currentTimestamp);
					delegator.createOrStore(pfbg);
				}
			}catch (GenericEntityException e) {
				String errMsg = "Fatal error calling deletePersonFamilyBackground service: " + e.toString();
		        Debug.logError(e, errMsg, module);
				e.printStackTrace();
			}
        
		}
		return successResult;
	}
			
}
