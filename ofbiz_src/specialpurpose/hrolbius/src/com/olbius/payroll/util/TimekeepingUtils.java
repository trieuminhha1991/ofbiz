package com.olbius.payroll.util;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.util.DateUtil;

public class TimekeepingUtils {
	
	public static Map<String, Object> getEmplDayLeaveByTimekeeper(DispatchContext dctx, Timestamp fromDate, Timestamp thruDate, String partyId) throws GenericEntityException, GenericServiceException{
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		if(fromDate == null){
			retMap.put("totalDayLeave", 0f);
			return retMap;
		}
		float totalDayLeave = 0f;
		float leavePaid = 0f;		
		
		Date startDate = new Date(fromDate.getTime());
		Date endDate = new Date(thruDate.getTime());
		List<EntityCondition> conditions = FastList.newInstance();
		EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateAttendance", EntityOperator.GREATER_THAN_EQUAL_TO, startDate),
																  EntityOperator.AND,
																  EntityCondition.makeCondition("dateAttendance", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		List<GenericValue> emplTimeKeepingSignLeaveList = delegator.findByAnd("EmplTimekeepingSign", UtilMisc.toMap("timekeepingSignTypeId", "NGHI"), null, false);
		List<String> emplTimeKeepingSignLeaveIds = EntityUtil.getFieldListFromEntityList(emplTimeKeepingSignLeaveList, "emplTimekeepingSignId", true);
		conditions.add(dateConds);
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("emplTimekeepingSignId", EntityJoinOperator.IN, emplTimeKeepingSignLeaveIds));
		EntityCondition commonConds = EntityCondition.makeCondition(conditions);
		List<GenericValue> emplTimesheetAttLeave = delegator.findList("EmplTimesheetAttendance", commonConds, null, UtilMisc.toList("dateAttendance"), null, false);
		for(GenericValue tempGv: emplTimesheetAttLeave){
			Double workday = tempGv.getDouble("workday");
			if(workday == null){
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("partyId", partyId);
				/*Map<String, Object> result = getWorkingShiftDayOfParty(dctx, ctxMap, tempGv.getDate("dateAttendance"));
				List<String> workingShiftList = (List<String>)result.get("workingShiftList");
				workday = (double)workingShiftList.size()/workingShift.size();*/
			}
			if(workday != null){
				String emplTimekeepingSignId = tempGv.getString("emplTimekeepingSignId");
				GenericValue emplTimekeepingSign = delegator.findOne("EmplTimekeepingSign", UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignId), false);
				tempGv.set("workday", workday);
				tempGv.store();
				Double rateBenefit = emplTimekeepingSign.getDouble("rateBenefit");
				if(rateBenefit != null){
					leavePaid += workday * rateBenefit;
				}
				totalDayLeave += workday;
			}
		}
		retMap.put("totalDayLeave", totalDayLeave);
		retMap.put("leavePaid", leavePaid);
		//retMap.put("leaveUnPaid", totalDayLeave - leavePaid);
		return retMap;
	} 
	
	
	/*maybe delete*/
	@SuppressWarnings("unchecked")
	public static Map<String, Float> getDateLeaveApproved(DispatchContext dctx, List<Map<String, Object>> listDayLeaveAndWorkShift, 
			String partyId, int totalWorkingShift) throws GenericEntityException{
		float totalLeaveApproved = 0f;
		float totalLeavePaid = 0f;
		Map<String, Float> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		if(listDayLeaveAndWorkShift == null){
			retMap.put("totalLeaveApproved", totalLeaveApproved);
			retMap.put("totalLeavePaid", totalLeavePaid);
			return retMap;
		}
		Timestamp timestampDateLeave;
		Set<String> workingShiftLeaveSet = FastSet.newInstance();
		Timestamp dayStart;
		Timestamp dayEnd;
		EntityCondition commonConds = EntityCondition.makeCondition("leaveStatus", "LEAVE_APPROVED");
		commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyId)); 
		List<EntityCondition> conditions = FastList.newInstance();
		//Time timepoint = Time.valueOf("12:00:00");
		for(Map<String, Object> tempMap: listDayLeaveAndWorkShift){
			conditions.clear();
			timestampDateLeave = (Timestamp)tempMap.get("dateLeave");
			dayStart = UtilDateTime.getDayStart(timestampDateLeave);
			dayEnd = UtilDateTime.getDayEnd(timestampDateLeave);
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayStart));
			conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd));
			workingShiftLeaveSet = (Set<String>)tempMap.get("workingShiftLeave");
			List<GenericValue> emplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition(conditions)), null, null, null, false);
			for(GenericValue tempGv: emplLeave){
				String leaveUnpaid = tempGv.getString("leaveUnpaid");
				//String emplLeaveTypeId = tempGv.getString("leaveTypeId");
				String emplLeaveId = tempGv.getString("emplLeaveId");
				List<GenericValue> emplLeaveWSApproved = delegator.findByAnd("EmplLeaveWorkingShift", UtilMisc.toMap("emplLeaveId", emplLeaveId), null, false);
				List<String> emplLeaveWSApprovedStr = EntityUtil.getFieldListFromEntityList(emplLeaveWSApproved, "workingShiftId", true);
				for(String workingShiftId: workingShiftLeaveSet){
					if(emplLeaveWSApprovedStr != null && emplLeaveWSApprovedStr.contains(workingShiftId)){
						totalLeaveApproved += 1f/totalWorkingShift;
						if(!"Y".equals(leaveUnpaid)){
							totalLeavePaid += 1f/totalWorkingShift;
						}
					}
				}
			}
		}
		retMap.put("totalLeaveApproved", totalLeaveApproved);
		retMap.put("totalLeavePaid", totalLeavePaid);
		return retMap;
	}
	
	public static Float getDayWorkOfPartyInPeriod(DispatchContext dctx,
			String partyId, Timestamp fromDate, Timestamp thruDate, Locale locale, TimeZone timeZone) throws GenericEntityException, GenericServiceException {
		return getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone, false);
	}
	
	public static Float getDayWorkOfPartyInPeriod(DispatchContext dctx,
			String partyId, Timestamp fromDate, Timestamp thruDate, Locale locale, TimeZone timeZone, boolean notIncludeHoliday) throws GenericEntityException, GenericServiceException{
		//Delegator delegator = dctx.getDelegator();
		Float totalDayWork = 0f;
		while(fromDate.before(thruDate)){
			Date date = new Date(fromDate.getTime());
			Map<String, Object> resultMap = getWorkingShiftDayOfParty(dctx, partyId, date);
			Boolean isDayLeave = (Boolean)resultMap.get("isDayLeave");
			if(!isDayLeave){
				String workTypeId = (String)resultMap.get("workTypeId");
				if("FIRST_HALF_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
					totalDayWork += 1f/2;
				}else if("ALL_SHIFT".equals(workTypeId)){
					totalDayWork += 1f;
				}
			}
			fromDate = UtilDateTime.getDayStart(fromDate, 1);
		}
		return totalDayWork;
	}

	public static Map<String, Object> getWorkingShiftDayOfParty(DispatchContext dctx, String partyId, Date date) throws GenericEntityException, GenericServiceException {
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("partyId", partyId);
		return getWorkingShiftDayOfParty(dctx, ctxMap, date);
	}
	
	public static Map<String, Object> getWorkingShiftDayOfParty(DispatchContext dctx, Map<String, Object> context, Date date) throws GenericEntityException, GenericServiceException {
		String partyId = (String)context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", date), false); 
		Timestamp timestamp = new Timestamp(date.getTime());
		boolean isDayLeave = true;
		//Timestamp fromDate = UtilDateTime.getDayStart(timestamp);
		//Timestamp thruDate = UtilDateTime.getDayEnd(timestamp);
		Map<String, Object> retMap = FastMap.newInstance();
		if(workingShiftEmployee != null){
			String workingShiftId = workingShiftEmployee.getString("workingShiftId");
			retMap.put("workingShiftId", workingShiftId);	
		}
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//int dayWeek = cal.get(Calendar.DAY_OF_WEEK);		
		Map<String, Object> resultService;
		resultService = dispatcher.runSync("checkDayIsDayLeaveOfParty", UtilMisc.toMap("partyId", partyId, "dateCheck", timestamp));
		isDayLeave = (Boolean)resultService.get("isDayLeave");
		String workTypeId = (String)resultService.get("workTypeId");
		retMap.put("isHoliday", resultService.get("isHoliday"));
		retMap.put("workTypeId", workTypeId);
		retMap.put("isDayLeave", isDayLeave);
		return retMap; 
	}

	public static float getTotalHoursWorkOvertime(DispatchContext dctx,
			String partyId, Timestamp fromDate, Timestamp thruDate, String code) throws GenericEntityException, GenericServiceException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Date beginDate = new Date(fromDate.getTime());
		Date endDate = new Date(thruDate.getTime());
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("statusId", "WOTR_ACCEPTED"));
		conditions.add(EntityCondition.makeCondition("dateRegistration", EntityOperator.GREATER_THAN_EQUAL_TO, beginDate));
		conditions.add(EntityCondition.makeCondition("dateRegistration", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		conditions.add(EntityCondition.makeCondition("actualStartTime", EntityOperator.NOT_EQUAL, null));
		conditions.add(EntityCondition.makeCondition("actualEndTime", EntityOperator.NOT_EQUAL, null));
		List<GenericValue> listWorkOverTime = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(conditions), null, null, null, false);
		Map<String, Object> resultService;
		float retValue = 0f;
		for(GenericValue tempGv: listWorkOverTime){
			Date tempDate = tempGv.getDate("dateRegistration");
			
			boolean isDayLeave = true;
			Boolean isHoliday = null;
			Time startTime = tempGv.getTime("actualStartTime");
			Time endTime = tempGv.getTime("actualEndTime");
			resultService = dispatcher.runSync("checkDayIsDayLeaveOfParty", UtilMisc.toMap("partyId", partyId, "dateCheck", new Timestamp(tempDate.getTime())));
			isDayLeave = (Boolean)resultService.get("isDayLeave");
			isHoliday = (Boolean)resultService.get("isHoliday");
			
			if("LAM_THEM_NGAY_THUONG".equals(code)){
				if(!isDayLeave){
					retValue += (float)(endTime.getTime() - startTime.getTime())/(1000*3600);
				}
			}else if("LAM_THEM_NGAY_NGHI".equals(code)){
				if(isDayLeave && (isHoliday == null || !isHoliday)){
					retValue += (float)(endTime.getTime() - startTime.getTime())/(1000*3600);
				}
			}else if("LAM_THEM_NGAY_LE".equals(code)){
				if(isDayLeave && isHoliday != null && isHoliday){
					retValue += (float)(endTime.getTime() - startTime.getTime())/(1000*3600);
				}
			}
		}
		return retValue;
	}
	
	public static List<GenericValue> getAllWorkingShift(Delegator delegator) throws GenericEntityException{
		return getAllWorkingShift(delegator, UtilMisc.toList("shiftStartTime"));
	}
	
	public static List<GenericValue> getAllWorkingShift(Delegator delegator, List<String> orderBy) throws GenericEntityException{
		//FIXME need find the way to get working shift
		List<GenericValue> workingShift = delegator.findList("WorkingShift", null ,null, orderBy, null, false);
		return workingShift;
	}

	public static Map<String, Object> checkCombineValidEmplTimekeepingSign(Delegator delegator, List<String> emplTimekeepingSignIdList) throws GenericEntityException {
		Map<String, Object> retMap = FastMap.newInstance();
		for(String emplTimekeepingSignId: emplTimekeepingSignIdList){
			for(String emplTimekeepingSignIdTo: emplTimekeepingSignIdList){
				if(!emplTimekeepingSignId.equals(emplTimekeepingSignIdTo)){
					GenericValue checkValidCombine1 = delegator.findOne("EmplTimekeepingSignValidCombine", 
							UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignId, "emplTimekeepingSignIdTo", emplTimekeepingSignIdTo), false);
					GenericValue checkValidCombine2 = delegator.findOne("EmplTimekeepingSignValidCombine", 
							UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignIdTo, "emplTimekeepingSignIdTo", emplTimekeepingSignId), false);
					if(checkValidCombine1 == null && checkValidCombine2 == null){
						retMap.put("isValid", false);
						retMap.put("emplTimekeepingSignId", emplTimekeepingSignId);
						retMap.put("emplTimekeepingSignIdTo", emplTimekeepingSignIdTo);
						return retMap;
					}
				}
			}
		}
		retMap.put("isValid", true);
		return retMap;
	}


	/*public static Time getEndTimeWorkingShift(Delegator delegator, String workingShiftId, String workTypeId) throws GenericEntityException {
		Time retVal = null;
		GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
		Time startOverTimeAfterShift = workingShift.getTime("startOverTimeAfterShift");
		if(startOverTimeAfterShift != null){
			return startOverTimeAfterShift;
		}
		Time shiftEndTime = workingShift.getTime("shiftEndTime");
		Time shiftStartTime = workingShift.getTime("shiftStartTime");
		if(shiftEndTime.before(shiftStartTime)){
			shiftEndTime = new Time(shiftEndTime.getTime() + DateUtil.ONE_DAY_MILLIS);
		}
		if("ALL_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
			return shiftEndTime;
		}
		Time shiftBreakStart = workingShift.getTime("shiftBreakStart");
		if("FIRST_HALF_SHIFT".equals(workTypeId)){
			if(shiftBreakStart != null){
				return shiftBreakStart;
			}else{				
				Long milis = shiftEndTime.getTime() - shiftStartTime.getTime();
				if(milis < 0){
					milis += DateUtil.ONE_DAY_MILLIS;
				}
				retVal = new Time(milis/2 + shiftStartTime.getTime());
			}
		}
		return retVal;
	}*/

	//get start time begin calculate overtime in working shift
	public static Time getTimeStartCalcOTWS(Delegator delegator, String workingShiftId) throws GenericEntityException {
		GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
		String workTypeId = workingShift.getString("workTypeId");
		Time shiftEndTime = workingShift.getTime("shiftEndTime");
		Time shiftStartTime = workingShift.getTime("shiftStartTime");
		Time startOverTimeAfterShift = workingShift.getTime("startOverTimeAfterShift");
		
		if("FIRST_HALF_SHIFT".equals(workTypeId) && startOverTimeAfterShift == null){
			shiftEndTime = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
		}
		if(startOverTimeAfterShift == null){
			startOverTimeAfterShift = shiftEndTime;
		}
		return startOverTimeAfterShift;
	}


	public static Time getTimeEndCalcOTWS(Delegator delegator, String workingShiftId) throws GenericEntityException {
		GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
		String workTypeId = workingShift.getString("workTypeId");
		Time shiftEndTime = workingShift.getTime("shiftEndTime");
		Time shiftStartTime = workingShift.getTime("shiftStartTime");
		Time endOverTimeAfterShift = workingShift.getTime("endOverTimeAfterShift");
		if("SECOND_HALF_SHIFT".equals(workTypeId) && endOverTimeAfterShift == null){
			shiftStartTime = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
		}
		if(endOverTimeAfterShift == null){
			endOverTimeAfterShift = shiftStartTime;
		}
		return endOverTimeAfterShift;
	}


	public static void editWorkOverTimeRegis(Delegator delegator, LocalDispatcher dispatcher,
			Time startTime, Time endTime, GenericValue userLogin, boolean checkBeforeUpdate, Date date, String partyId) throws GenericEntityException, GenericServiceException {
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		EntityCondition workOvertimeRegis = EntityCondition.makeCondition("dateRegistration", date);
		List<GenericValue> updateWorkOverRegisList = delegator.findList("WorkOvertimeRegistration", 
				EntityCondition.makeCondition(workOvertimeRegis, EntityOperator.AND, commonConds), null, null, null, false);
		if(UtilValidate.isNotEmpty(updateWorkOverRegisList)){
			for(GenericValue updateTemp: updateWorkOverRegisList){
				dispatcher.runSync("updateEmplWorkovertime", UtilMisc.toMap("actualStartTime", String.valueOf(startTime.getTime()), 
						"actualEndTime", String.valueOf(endTime.getTime()), 
						"workOvertimeRegisId", updateTemp.get("workOvertimeRegisId"),
						"userLogin", userLogin, "checkBeforeUpdate", false));
			}
		}else{
			GenericValue newEntity = delegator.makeValue("WorkOvertimeRegistration");
			newEntity.set("partyId", partyId);
			newEntity.set("dateRegistration", date);
			newEntity.set("actualStartTime", startTime);
			newEntity.set("actualEndTime", endTime);
			String workOvertimeRegisId = delegator.getNextSeqId("WorkOvertimeRegistration");
			newEntity.set("workOvertimeRegisId", workOvertimeRegisId);
			newEntity.set("statusId", "WOTR_CREATED");
			newEntity.create();
		}
	}
	

	/*public static Float getEmplDayLeaveByTimesheet(DispatchContext dctx, String partyId, String emplTimesheetId) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();			
		List<GenericValue> emplTimesheetLeavePaidAtt = delegator.findByAnd("EmplTimesheetAttendance", 
												UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "emplTimekeepingSignId", "NGHI_PHEP"), null, false);
		List<GenericValue> emplTimesheetLeaveUnpaidAtt = delegator.findByAnd("EmplTimesheetAttendance", 
				UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "emplTimekeepingSignId", "NGHI_KHONG_LUONG"), null, false);
		List<GenericValue> emplTimesheetLeaveNotApprAtt = delegator.findByAnd("EmplTimesheetAttendance", 
				UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "emplTimekeepingSignId", "NGHI_KHONG_PHEP"), null, false);
		float totalLeaveApproved = 0f;
		float totalLeavePaid = 0f;
		float totalLeave = 0f;
		for(GenericValue tempGv: emplTimesheetLeavePaidAtt){
			Date date = tempGv.getDate("dateAttendance");
			Timestamp tempFromDate = tempGv.getTimestamp("fromDate");
			Timestamp tempThruDate = tempGv.getTimestamp("thruDate");
		}
		return null;
	}*/
}
