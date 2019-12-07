package com.olbius.recruitment.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;

public class RecruitmentDataPreparation implements RoleTyle {
	/**
	 * Job request in plan?
	 * @param dpct
	 * @param partyId
	 * @param emplPositionTypeId
	 * @param fromDate
	 * @return
	 * @throws Exception 
	 */
	
	public static boolean checkInPlan(DispatchContext dpct,long resourceNumber, String partyId, String emplPositionTypeId, Timestamp fromDate) throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int requestingMonth = cal.get(Calendar.MONTH);
		String requestingYear = new Integer(cal.get(Calendar.YEAR)).toString();
		Delegator delegator = dpct.getDelegator();
		
		EntityCondition condition1 = EntityCondition.makeCondition("partyId", partyId);
		EntityCondition condition2 = EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
		EntityCondition condition3 = EntityCondition.makeCondition("year", requestingYear);
		EntityCondition condition4 = EntityCondition.makeCondition("statusId", "RPH_ACCEPTED");
		EntityCondition condition5 = EntityCondition.makeCondition("actorRoleTypeId", CEO_ROLE);
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, condition1, condition2, condition3, condition4, condition5);
		List<GenericValue> tmpList = delegator.findList("RecruitmentPlanAndJobRequest", conditionList, null, null, null, false);
		GenericValue recruitmentPlan = delegator.findOne("RecruitmentPlan", false, UtilMisc.toMap("partyId", partyId, "emplPositionTypeId", emplPositionTypeId, "year", requestingYear));
		if(tmpList == null || recruitmentPlan == null){ //Job Request is not plan
			//throw new NullPointerException();
			return false;
		}
		
		//Calculate total resource number in plan in requesting month
		long count = resourceNumber;
		for(GenericValue item : tmpList){
			Timestamp tmpFromDate =  item.getTimestamp("fromDate");
			cal.setTime(tmpFromDate);
			int requestedMonth = cal.get(Calendar.MONTH);
			if(requestingMonth == requestedMonth){
				count += item.getLong("resourceNumber");
			}
		}
		
		//Check condition to job request is in plan
		long planResourceNumber = 0;
		switch (requestingMonth) {
		case 0:
			if(recruitmentPlan.getLong("firstMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("firstMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 1:
			if(recruitmentPlan.getLong("secondMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("secondMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 2:
			if(recruitmentPlan.getLong("thirdMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("thirdMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 3:
			if(recruitmentPlan.getLong("fourthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("fourthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 4:
			if(recruitmentPlan.getLong("fifthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("fifthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 5:
			if(recruitmentPlan.getLong("sixthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("sixthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 6:
			if(recruitmentPlan.getLong("seventhMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("seventhMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 7:
			if(recruitmentPlan.getLong("eighthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("eighthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 8:
			if(recruitmentPlan.getLong("ninthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("ninthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 9:
			if(recruitmentPlan.getLong("tenthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("tenthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 10:
			if(recruitmentPlan.getLong("eleventhMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("eleventhMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		case 11:
			if(recruitmentPlan.getLong("twelfthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("twelfthMonth");
			}
			if(count > planResourceNumber){
				return false;
			}
			break;
		default:
			throw new Exception("Month is not valid");
		}
		return true;
	}
	
	public static long getResourceInPlan(Delegator delegator, String partyId, String emplPositionTypeId, Timestamp fromDate) throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int requestingMonth = cal.get(Calendar.MONTH);
		String requestingYear = new Integer(cal.get(Calendar.YEAR)).toString();
		
		EntityCondition condition1 = EntityCondition.makeCondition("partyId", partyId);
		EntityCondition condition2 = EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
		EntityCondition condition3 = EntityCondition.makeCondition("year", requestingYear);
		EntityCondition condition4 = EntityCondition.makeCondition("statusId", "RPH_ACCEPTED");
		EntityCondition condition5 = EntityCondition.makeCondition("actorRoleTypeId", CEO_ROLE);
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, condition1, condition2, condition3, condition4, condition5);
		List<GenericValue> tmpList = delegator.findList("RecruitmentPlanAndJobRequest", conditionList, null, null, null, false);
		GenericValue recruitmentPlan = delegator.findOne("RecruitmentPlanDT", false, UtilMisc.toMap("partyId", partyId, "emplPositionTypeId", emplPositionTypeId, "year", requestingYear));
		if(UtilValidate.isEmpty(recruitmentPlan)){ //Job Request is not plan
			//throw new NullPointerException();
			return 0;
		}
		
		//Calculate total resource number in plan in requesting month
		long count = 0;
		for(GenericValue item : tmpList){
			Timestamp tmpFromDate =  item.getTimestamp("fromDate");
			cal.setTime(tmpFromDate);
			int requestedMonth = cal.get(Calendar.MONTH);
			if(requestingMonth == requestedMonth){
				count += item.getLong("resourceNumber");
			}
		}
		
		//Check condition to job request is in plan
		long planResourceNumber = 0;
		long result = 0;
		switch (requestingMonth) {
		case 0:
			if(recruitmentPlan.getLong("firstMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("firstMonth");
			}
			result = planResourceNumber - count; 
			break;
		case 1:
			if(recruitmentPlan.getLong("secondMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("secondMonth");
			}
			result = planResourceNumber - count; 
			break;
		case 2:
			if(recruitmentPlan.getLong("thirdMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("thirdMonth");
			}
			result = planResourceNumber - count;
			break;
		case 3:
			if(recruitmentPlan.getLong("fourthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("fourthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 4:
			if(recruitmentPlan.getLong("fifthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("fifthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 5:
			if(recruitmentPlan.getLong("sixthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("sixthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 6:
			if(recruitmentPlan.getLong("seventhMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("seventhMonth");
			}
			result = planResourceNumber - count;
			break;
		case 7:
			if(recruitmentPlan.getLong("eighthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("eighthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 8:
			if(recruitmentPlan.getLong("ninthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("ninthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 9:
			if(recruitmentPlan.getLong("tenthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("tenthMonth");
			}
			result = planResourceNumber - count;
			break;
		case 10:
			if(recruitmentPlan.getLong("eleventhMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("eleventhMonth");
			}
			result = planResourceNumber - count;
			break;
		case 11:
			if(recruitmentPlan.getLong("twelfthMonth") != null) {
				planResourceNumber = recruitmentPlan.getLong("twelfthMonth");
			}
			result = planResourceNumber - count;
			break;
		default:
			throw new Exception("Month is not valid");
		}
		return result;
	}
	
}
