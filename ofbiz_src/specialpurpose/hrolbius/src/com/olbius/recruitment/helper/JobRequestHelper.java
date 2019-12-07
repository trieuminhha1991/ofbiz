package com.olbius.recruitment.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;
import com.olbius.util.RoleHelper;

public class JobRequestHelper implements RoleTyle {
	
	public static final String module = JobRequestHelper.class.getName();
	
	public static Map<String, Object> createJobRequest(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		Delegator delegator = dpctx.getDelegator();
		// Get parameters
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		String recruitmentTypeId = (String) context.get("recruitmentTypeId");
		String recruitmentFormId = (String) context.get("recruitmentFormId");
		String reason = (String)context.get("overPlanReason");
		Long resourceNumber = (Long) context.get("resourceNumber");
		Long availableNumber = (Long) context.get("availableNumber");
		String jobDescription = (String) context.get("jobDescription");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String workLocation = (String)context.get("workLocation");
		String genderId = (String)context.get("genderId");
		String age = (String)context.get("age");
		String experience = (String)context.get("experience");
		String educationSystemTypeId = (String)context.get("educationSystemTypeId");
		String englishSkillId = (String)context.get("englishSkillId");
		String workSkillId = (String)context.get("workSkillId");
		String itSkillId = (String)context.get("itSkillId");
		Long proposalSal = (Long) context.get("proposalSal");
		// Create auto increase id
		String jobRequestId = getNextSeqId(delegator);
		
		// Initial status
		String statusId = "JR_INIT";
		if (PartyUtil.isAdmin(userLogin.getString("partyId"), delegator)) {
			statusId = "JR_ACCEPTED";
		}
		// Check in plan
		String isInPlan = "IJR_NINPLAN";
		if (RecruitmentDataPreparation.checkInPlan(dpctx, resourceNumber, partyId, emplPositionTypeId, fromDate)) {
			isInPlan = "IJR_INPLAN";
		}
		// Set data for Job request
		GenericValue jobRequest = delegator.makeValue("JobRequest");
		jobRequest.set("jobRequestId", jobRequestId);
		jobRequest.set("emplPositionTypeId", emplPositionTypeId);
		jobRequest.set("fromDate", fromDate);
		jobRequest.set("recruitmentTypeId", recruitmentTypeId);
		jobRequest.set("recruitmentFormId", recruitmentFormId);
		jobRequest.set("resourceNumber", resourceNumber);
		jobRequest.set("availableNumber", availableNumber);
		jobRequest.set("jobDescription", jobDescription);
		jobRequest.set("partyId", partyId);
		jobRequest.set("statusId", statusId);
		jobRequest.set("isInPlan", isInPlan);
		jobRequest.put("workLocation", workLocation);
		jobRequest.put("genderId", genderId);
		jobRequest.put("age", age);
		jobRequest.put("experience", experience);
		jobRequest.put("educationSystemTypeId", educationSystemTypeId);
		jobRequest.put("englishSkillId", englishSkillId);
		jobRequest.put("workSkillId", workSkillId);
		jobRequest.put("itSkillId", itSkillId);
		jobRequest.put("reason", reason);
		jobRequest.put("proposalSal", proposalSal);
		jobRequest.put("actorPartyId", userLogin.get("partyId"));
		jobRequest.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		// Create
		jobRequest.create();
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("jobRequestId", jobRequestId);
		return result;
	}
	
	public static Map<String, Object> updateJobRequest(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		//Get parameters
		String jobRequestId = (String)context.get("jobRequestId");
		String partyId = (String) context.get("partyId");
		String isInPlan = (String) context.get("isInPlan");
		String statusId = (String)context.get("statusId");
		String reason = (String)context.get("reason");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dpctx.getDelegator();
		LocalDispatcher dispatcher = dpctx.getDispatcher();

		//Update RecruitmentPlanHeader
		GenericValue oldValue = delegator.findOne("JobRequest", UtilMisc.toMap("jobRequestId", jobRequestId), false);
		oldValue.put("statusId", statusId);
		oldValue.put("reason", reason);
		oldValue.put("actorPartyId", userLogin.getString("partyId"));
		oldValue.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		oldValue.store();

		//Send a notification to headofdept
		if("JR_REJECTED".equals(statusId) && PartyUtil.isAdmin(userLogin.getString("partyId"), delegator)){
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			createNotiCtx.put("partyId", PartyUtil.getManagerbyOrg(partyId, delegator));
			createNotiCtx.put("header", "Kết quả kiểm tra yêu cầu tuyển dụng " + jobRequestId);
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindJobRequest");
			createNotiCtx.put("targetLink", "jobRequestId=" + jobRequestId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			dispatcher.runSync("createNotification", createNotiCtx);
		}else if("JR_PROPOSED".equals(statusId) && PartyUtil.isAdmin(userLogin.getString("partyId"), delegator)) {
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			createNotiCtx.put("partyId", PartyUtil.getCEO(delegator));
			createNotiCtx.put("header", "Phê duyệt yêu cầu tuyển dụng " + jobRequestId);
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindJobRequest");
			createNotiCtx.put("targetLink", "jobRequestId=" + jobRequestId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			dispatcher.runSync("createNotification", createNotiCtx);
		}else if(PartyUtil.isCEO(delegator, userLogin)) {
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			List<String> partiesList = new ArrayList<String>();
			partiesList.add(PartyUtil.getManagerbyOrg(partyId, delegator));
			partiesList.add(PartyUtil.getHrmAdmin(delegator));
			createNotiCtx.put("partiesList", partiesList);
			createNotiCtx.put("header", "Kết quả xét duyệt yêu cầu tuyển dụng " + jobRequestId);
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindJobRequest");
			createNotiCtx.put("targetLink", "jobRequestId=" + jobRequestId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			dispatcher.runSync("createNotification", createNotiCtx);
		}else if (!PartyUtil.isCEO(delegator, userLogin) && !PartyUtil.isAdmin(userLogin.getString("partyId"), delegator) && "JR_SCHEDULED".equals(statusId)) {
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindJobRequest");
			createNotiCtx.put("targetLink", "jobRequestId=" + jobRequestId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			if ("IJR_NINPLAN".equals(isInPlan)) {
				createNotiCtx.put("header", "Phê duyệt yêu cầu tuyển dụng " + jobRequestId);
				createNotiCtx.put("partyId", PartyUtil.getCEO(delegator));
			}else {
				createNotiCtx.put("header", "Kiểm tra yêu cầu tuyển dụng " + jobRequestId);
				createNotiCtx.put("partyId", PartyUtil.getHrmAdmin(delegator));
			}
			dispatcher.runSync("createNotification", createNotiCtx);
		}
		return ServiceUtil.returnSuccess();
	}
	
	private static String getNextSeqId(Delegator delegator) throws GenericEntityException {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, 1);    
		Timestamp start = new Timestamp(cal.getTimeInMillis());

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, Calendar.DECEMBER); // 11 = december
		cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
		Timestamp end = new Timestamp(cal.getTimeInMillis());
		
		EntityCondition startCon = EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, start);
		EntityCondition endCon = EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, end);
		List<GenericValue> listJobRequest = delegator.findList("JobRequest", EntityCondition.makeCondition(startCon, EntityJoinOperator.AND, endCon), null, null, null, false);
		int seq = listJobRequest.size() + 1;
		return seq + "/" + year;
	}
}
