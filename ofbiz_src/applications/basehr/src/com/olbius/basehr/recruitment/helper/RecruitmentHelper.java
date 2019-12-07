package com.olbius.basehr.recruitment.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;

public class RecruitmentHelper {
	public static Map<String, Object> createRecruitmentRequirementFromJson(LocalDispatcher dispatcher, 
			GenericValue userLogin, String serviceName, 
			Map<String, Object> context, String recruitReqCondParam) throws GenericServiceException{
		JSONArray recruitReqCondJsonList = JSONArray.fromObject(recruitReqCondParam);
		//Map<String, Object> recruitReqCondsMap = FastMap.newInstance();
		//recruitReqCondsMap.put("recruitmentRequireId", recruitmentRequireId);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService = FastMap.newInstance();
		for(int i = 0; i < recruitReqCondJsonList.size(); i++){
			JSONObject recruitReqCondJson = recruitReqCondJsonList.getJSONObject(i);
			String recruitmentReqCondTypeId = recruitReqCondJson.getString("recruitmentReqCondTypeId");
			String conditionDesc = recruitReqCondJson.getString("conditionDesc");
			context.put("recruitmentReqCondTypeId", recruitmentReqCondTypeId);
			context.put("conditionDesc", conditionDesc);
			resultService = dispatcher.runSync(serviceName, context);
			if(!ServiceUtil.isSuccess(resultService)){
				resultService.put("isError", true);
				resultService.put(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}
		}
		resultService.put("isError", false);
		return resultService;
	}

	public static Long getNextRecruitmentRoundOrder(Delegator delegator,
			Long roundOrder, String recruitmentPlanId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
		conditions.add(EntityCondition.makeCondition("roundOrder", EntityJoinOperator.GREATER_THAN, roundOrder));
		List<GenericValue> nextRoundOrderList = delegator.findList("RecruitmentPlanRound", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("roundOrder"), null, false);
		if(UtilValidate.isEmpty(nextRoundOrderList)){
			return 0L;
		}
		Long nextRoundOrder = nextRoundOrderList.get(0).getLong("roundOrder");
		return nextRoundOrder;
	}

	public static List<GenericValue> getNextRecruitmentRoundListOfCandidate(
			Delegator delegator, String partyId, String recruitmentPlanId, Long roundOrder) throws GenericEntityException {
		if(roundOrder == 0L){
			return null;
		}
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roundOrder", EntityJoinOperator.GREATER_THAN_EQUAL_TO, roundOrder),
					EntityJoinOperator.OR, EntityCondition.makeCondition("roundOrder", 0L)));
		List<GenericValue> listNextRoundOfCandidate = delegator.findList("RecruitmentRoundCandidate", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("roundOrder"), null, false);
		return listNextRoundOfCandidate;
	}

	public static Long getNextRecruitmentInterviewOrder(Delegator delegator,
			String recruitmentPlanId, Long roundOrder) throws GenericEntityException {
		List<GenericValue> recruitmentRoundCandidate = delegator.findByAnd("RecruitmentRoundCandidate", 
				UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), UtilMisc.toList("-interviewOrder"), false);
		if(UtilValidate.isEmpty(recruitmentRoundCandidate)){
			return 1L;
		}
		Long interviewOrder = recruitmentRoundCandidate.get(0).getLong("interviewOrder");
		return interviewOrder + 1L;
	}

	public static Timestamp createDateInterviewForCandidate(
			Calendar startTimeInterview, Calendar interviewMorningFrom,
			Calendar interviewMorningTo, Calendar interviewAfternoonFrom,
			Calendar interviewAfternoonTo, int timeForInterview,
			int overlapTimeInterviewMinute, int ordering) {
		if(interviewMorningFrom == null && interviewAfternoonFrom == null){
			return createDateInterviewForCandidate(startTimeInterview, timeForInterview, overlapTimeInterviewMinute, ordering);
		}else if(interviewMorningFrom != null && interviewAfternoonFrom == null){
			return createDateInterviewForCandidate(startTimeInterview, timeForInterview, interviewMorningFrom, interviewMorningTo, overlapTimeInterviewMinute, ordering);
		}else if(interviewMorningFrom == null && interviewAfternoonFrom != null){
			return createDateInterviewForCandidate(startTimeInterview, timeForInterview, interviewAfternoonFrom, interviewAfternoonTo, overlapTimeInterviewMinute, ordering);
		}
		Calendar interviewMorningFromTemp = (Calendar)startTimeInterview.clone(), 
				interviewMorningToTemp = (Calendar)startTimeInterview.clone(),
				interviewAfternoonFromTemp = (Calendar)startTimeInterview.clone(),
				interviewAfternoonToTemp = (Calendar)startTimeInterview.clone();
		interviewMorningFromTemp.set(Calendar.HOUR_OF_DAY, interviewMorningFrom.get(Calendar.HOUR_OF_DAY));
		interviewMorningFromTemp.set(Calendar.MINUTE, interviewMorningFrom.get(Calendar.MINUTE));
		interviewMorningFromTemp.set(Calendar.SECOND, interviewMorningFrom.get(Calendar.SECOND));
		
		interviewMorningToTemp.set(Calendar.HOUR_OF_DAY, interviewMorningTo.get(Calendar.HOUR_OF_DAY));
		interviewMorningToTemp.set(Calendar.MINUTE, interviewMorningTo.get(Calendar.MINUTE));
		interviewMorningToTemp.set(Calendar.SECOND, interviewMorningTo.get(Calendar.SECOND));
		
		interviewAfternoonFromTemp.set(Calendar.HOUR_OF_DAY, interviewAfternoonFrom.get(Calendar.HOUR_OF_DAY));
		interviewAfternoonFromTemp.set(Calendar.MINUTE, interviewAfternoonFrom.get(Calendar.MINUTE));
		interviewAfternoonFromTemp.set(Calendar.SECOND, interviewAfternoonFrom.get(Calendar.SECOND));
		
		interviewAfternoonToTemp.set(Calendar.HOUR_OF_DAY, interviewAfternoonTo.get(Calendar.HOUR_OF_DAY));
		interviewAfternoonToTemp.set(Calendar.MINUTE, interviewAfternoonTo.get(Calendar.MINUTE));
		interviewAfternoonToTemp.set(Calendar.SECOND, interviewAfternoonTo.get(Calendar.SECOND));
		//So luong ung vien co the phong van trong buoi sang
		int nbrCandidateInterviewInMorning = getNbrCandidateInterviewInPeriod(interviewMorningFromTemp, interviewMorningToTemp, timeForInterview, overlapTimeInterviewMinute);
		//So luong ung vien co the phong van trong buoi chieu
		int nbrCandidateInterviewInAfternoon = getNbrCandidateInterviewInPeriod(interviewAfternoonFromTemp, interviewAfternoonToTemp, timeForInterview, overlapTimeInterviewMinute);
		
		Calendar cal = (Calendar)startTimeInterview.clone();
		int totalCandidateInterviewPerDay = nbrCandidateInterviewInMorning + nbrCandidateInterviewInAfternoon;
		if(totalCandidateInterviewPerDay == 0){
			totalCandidateInterviewPerDay = 1;
		}
		int dateInterviewOffSet = ordering/totalCandidateInterviewPerDay; 
		cal.add(Calendar.DATE, dateInterviewOffSet);
		//thu tu ung vien duoc phong van trong ngay 
		int orderingInterviewInDay = ordering - dateInterviewOffSet * totalCandidateInterviewPerDay;
		int offSetTimeInterview = 0;
		if(orderingInterviewInDay < nbrCandidateInterviewInMorning){
			offSetTimeInterview = orderingInterviewInDay * (timeForInterview + overlapTimeInterviewMinute);
			cal.set(Calendar.HOUR_OF_DAY, interviewMorningFromTemp.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, interviewMorningFromTemp.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, interviewMorningFromTemp.get(Calendar.SECOND));
		}else{
			offSetTimeInterview = (orderingInterviewInDay - nbrCandidateInterviewInMorning) * (timeForInterview + overlapTimeInterviewMinute);
			cal.set(Calendar.HOUR_OF_DAY, interviewAfternoonFromTemp.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, interviewAfternoonFromTemp.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, interviewAfternoonFromTemp.get(Calendar.SECOND));
		}
		cal.add(Calendar.MINUTE, offSetTimeInterview);
		return new Timestamp(cal.getTimeInMillis());
	}

	public static Timestamp createDateInterviewForCandidate(Calendar startTimeInterview, int timeForInterview, 
			Calendar timeStart, Calendar timeEnd, int overlapTimeInterviewMinute, int ordering) {
		int nbrCandidateInterview = getNbrCandidateInterviewInPeriod(timeStart, timeEnd, timeForInterview, overlapTimeInterviewMinute);
		Calendar cal = (Calendar)startTimeInterview.clone();
		if(nbrCandidateInterview == 0){
			nbrCandidateInterview = 1;
		}
		int dateInterviewOffSet = ordering/nbrCandidateInterview; 
		cal.add(Calendar.DATE, dateInterviewOffSet);
		//thu tu ung vien duoc phong van trong ngay 
		int orderingInterviewInDay = ordering - dateInterviewOffSet * nbrCandidateInterview;
		cal.set(Calendar.HOUR_OF_DAY, timeStart.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, timeStart.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, timeStart.get(Calendar.SECOND));
		int offSetTimeInterview = orderingInterviewInDay * (timeForInterview + overlapTimeInterviewMinute);
		cal.add(Calendar.MINUTE, offSetTimeInterview);
		return new Timestamp(cal.getTimeInMillis());
	}

	public static Timestamp createDateInterviewForCandidate(Calendar startTimeInterview, int timeForInterview, int overlapTimeInterviewMinute, int ordering) {
		Calendar startTimeInterviewTemp = (Calendar)startTimeInterview.clone();
		int offset = (timeForInterview + overlapTimeInterviewMinute) * ordering;
		startTimeInterviewTemp.add(Calendar.MINUTE, offset);
		return new Timestamp(startTimeInterviewTemp.getTimeInMillis());
	}
	
	private static int getNbrCandidateInterviewInPeriod(
			Calendar startInterview, Calendar endInterview,
			int timeForInterview, int overlapTimeInterviewMinute) {
		long minuteInterview = endInterview.getTimeInMillis() - startInterview.getTimeInMillis();
		if(minuteInterview < 0){
			minuteInterview += DateUtil.ONE_DAY_MILLIS;
		}
		long retVal = (minuteInterview/(60 * 1000))/(timeForInterview + overlapTimeInterviewMinute);
		return (int)retVal;
	}

	public static void updateCandidateId(Delegator delegator, String partyId,
			String candidateId) throws GenericEntityException {
		GenericValue partyCandidateAttr = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "REC_CANDIDATE_ID"), false);
		if(partyCandidateAttr != null && candidateId != null && candidateId.trim().length() > 0){
			partyCandidateAttr.set("attrValue", candidateId.trim());
			partyCandidateAttr.store();
		}
	}

	public static Double getTotalPonitOfCandidateInRound(Delegator delegator,
			String recruitmentPlanId, Long roundOrder, String partyId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("roundOrder", roundOrder));
		conditions.add(EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId));
		List<GenericValue> candidateSubjectResultList = delegator.findList("RecruitmentPlanRoundAndSubjectParty", 
				EntityCondition.makeCondition(conditions), null, null, null, false);
		if(UtilValidate.isEmpty(candidateSubjectResultList)){
			return null;
		}
		Double retVal = 0d;
		for(GenericValue candidateSubjectResult: candidateSubjectResultList){
			Double point = candidateSubjectResult.getDouble("point");
			Double ratio = candidateSubjectResult.getDouble("ratio");
			retVal += ratio * point;
		}
		return retVal;
	}

	public static String getChangeReasonRecruitmentAnticipateItem(Delegator delegator, String recruitAnticipateId, Long recruitAnticipateSeqId) throws GenericEntityException {
		return getChangeReasonRecruitmentAnticipateItem(delegator, recruitAnticipateId, recruitAnticipateSeqId, null);
	}
	public static String getChangeReasonRecruitmentAnticipateItem(Delegator delegator, String recruitAnticipateId, Long recruitAnticipateSeqId, 
			List<String> statusList) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("recruitAnticipateId", recruitAnticipateId));
		conds.add(EntityCondition.makeCondition("recruitAnticipateSeqId", recruitAnticipateSeqId));
		if(UtilValidate.isNotEmpty(statusList)){
			conds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, statusList));
		}
		List<GenericValue> recruitmentAnticipateStatusList = delegator.findList("RecruitmentAnticipateStatus", 
				EntityCondition.makeCondition(conds), null, UtilMisc.toList("-statusDatetime"), null, false);
		if(UtilValidate.isNotEmpty(recruitmentAnticipateStatusList)){
			GenericValue recruitmentAnticipateStatus = recruitmentAnticipateStatusList.get(0);
			return recruitmentAnticipateStatus.getString("changeReason");
		}
		return null;
	}
}
