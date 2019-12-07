package com.olbius.employment.events;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.employment.helper.TimeManagerHelper;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TimeManagerEvents {
	public static String createHolidayConfig(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Boolean lunarCalendar = request.getParameter("lunarCalendar") != null? Boolean.valueOf(request.getParameter("lunarCalendar")) : false;
		try {
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createHolidayConfig", paramMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			if(lunarCalendar){
				ctxMap.put("calendarType", "LUNAR_CALENDAR");
			}
			ctxMap.put("locale", UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("createHolidayConfig", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updateWorkingShift(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String workingShiftId = request.getParameter("workingShiftId");
		convertDataOfWorkingShift(request, paramMap);
		try {
			try {
				List<GenericValue> dayOfWeekList = delegator.findByAnd("DayOfWeek", null, null, false);
				Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "updateWorkingShift", paramMap, userLogin, timeZone, locale);
				ctxMap.put("locale", locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateWorkingShift", ctxMap);
				if(ServiceUtil.isSuccess(resultService)){
					Map<String, Object> updateWorkingShiftDayWeekMap = FastMap.newInstance();
					updateWorkingShiftDayWeekMap.put("workingShiftId", workingShiftId);
					updateWorkingShiftDayWeekMap.put("userLogin", userLogin);
					for(GenericValue dayOfWeek: dayOfWeekList){
						String workTypeId = request.getParameter(dayOfWeek.getString("dayOfWeek"));
						if(workTypeId != null){
							updateWorkingShiftDayWeekMap.put("dayOfWeek", dayOfWeek.getString("dayOfWeek"));
							updateWorkingShiftDayWeekMap.put("workTypeId", workTypeId);
							dispatcher.runSync("updateWorkingShiftDayWeek", updateWorkingShiftDayWeekMap);
						}
					}
					TransactionUtil.commit();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
					TransactionUtil.rollback();
				}
			}catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
		}
		return "success";
	}
	
	public static String createWorkingShift(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String workingShiftId = request.getParameter("workingShiftId");
		convertDataOfWorkingShift(request, paramMap);
		try {
			try {
				List<GenericValue> dayOfWeekList = delegator.findByAnd("DayOfWeek", null, null, false);
				Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createWorkingShift", paramMap, userLogin, timeZone, locale);
				ctxMap.put("locale", locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createWorkingShift", ctxMap);
				if(ServiceUtil.isSuccess(resultService)){
					workingShiftId = (String)resultService.get("workingShiftId");
					Map<String, Object> createWorkingShiftDayWeekMap = FastMap.newInstance();
					createWorkingShiftDayWeekMap.put("workingShiftId", workingShiftId);
					createWorkingShiftDayWeekMap.put("userLogin", userLogin);
					for(GenericValue dayOfWeek: dayOfWeekList){
						String workTypeId = request.getParameter(dayOfWeek.getString("dayOfWeek"));
						if(workTypeId != null){
							createWorkingShiftDayWeekMap.put("dayOfWeek", dayOfWeek.getString("dayOfWeek"));
							createWorkingShiftDayWeekMap.put("workTypeId", workTypeId);
							dispatcher.runSync("createWorkingShiftDayWeek", createWorkingShiftDayWeekMap);
						}
					}
					TransactionUtil.commit();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
					TransactionUtil.rollback();
				}
			}catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
		}
		return "success";
	}
	
	public static String getAllWorkingShift(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> allWorkingShift = delegator.findList("WorkingShift", null, UtilMisc.toSet("workingShiftId", "workingShiftName"), UtilMisc.toList("workingShiftId"), null, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", allWorkingShift);
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		
		return "success";
	}

	public static void convertDataOfWorkingShift(HttpServletRequest request, Map<String, Object> paramMap) {
		String shiftStartTimeStr = request.getParameter("shiftStartTime");
		String shiftEndTimeStr = request.getParameter("shiftEndTime");
		String shiftBreakStartTimeStr = request.getParameter("shiftBreakStartTime");
		String shiftBreakEndTimeStr = request.getParameter("shiftBreakEndTime");
		String startOverTimeAfterShiftStr = request.getParameter("startOverTimeAfterShift");
		String endOverTimeAfterShiftStr = request.getParameter("endOverTimeAfterShift");
		Time shiftStartTime = new Time(Long.parseLong(shiftStartTimeStr));
		Time shiftEndTime = new Time(Long.parseLong(shiftEndTimeStr));
		String isAllowOTAfterShiftStr = request.getParameter("isAllowOTAfterShift");
		boolean isAllowOTAfterShift = false;
		if(isAllowOTAfterShiftStr != null){
			isAllowOTAfterShift = Boolean.valueOf(isAllowOTAfterShiftStr);
		}
		if(isAllowOTAfterShift){
			paramMap.put("isAllowOTAfterShift", "Y");
		}else{
			paramMap.put("isAllowOTAfterShift", "N");
		}
		paramMap.put("shiftStartTime", shiftStartTime);
		paramMap.put("shiftEndTime", shiftEndTime);
		if(shiftBreakStartTimeStr != null){
			Time shiftBreakStartTime = new Time(Long.parseLong(shiftBreakStartTimeStr));
			paramMap.put("shiftBreakStart", shiftBreakStartTime);
		}
		if(shiftBreakEndTimeStr != null){
			Time shiftBreakEndTime = new Time(Long.parseLong(shiftBreakEndTimeStr));
			paramMap.put("shiftBreakEnd", shiftBreakEndTime);
		}
		if(startOverTimeAfterShiftStr != null){
			Time startOverTimeAfterShift = new Time(Long.parseLong(startOverTimeAfterShiftStr));
			paramMap.put("startOverTimeAfterShift", startOverTimeAfterShift);
		}
		if(endOverTimeAfterShiftStr != null){
			Time endOverTimeAfterShift = new Time(Long.parseLong(endOverTimeAfterShiftStr));
			paramMap.put("endOverTimeAfterShift", endOverTimeAfterShift);
		}
	}
	
	public static String assignWorkingShiftForParty(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(partyId != null){
			try {
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
				String workingShiftId = TimeManagerHelper.getWorkingShiftOfParty(delegator, partyId);
				GenericValue party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if(workingShiftId != null){
					List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
					Map<String, Object> ctxMap = FastMap.newInstance();
					ctxMap.put("workingShiftId", workingShiftId);
					ctxMap.put("fromDate", new Date(fromDate.getTime()));
					ctxMap.put("thruDate", new Date(thruDate.getTime()));
					ctxMap.put("userLogin", userLogin);
					for(GenericValue empl: emplList){
						String tempPartyId = empl.getString("partyId");
						ctxMap.put("partyId", tempPartyId);
						dispatcher.runSync("editWorkingShiftEmployeeInPeriod", ctxMap);
					}
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "AssignWorkingShiftPartySuccess", UtilMisc.toMap("partyName", party.getString("groupName")), UtilHttp.getLocale(request)));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "PartyNotConfigWorkingShift", UtilMisc.toMap("partyName", party.getString("groupName")), UtilHttp.getLocale(request)));
				}
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find partyId");
		}
		return "success";
	}
	
	public static String updateWorkingShiftEmployee(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String dateStr = request.getParameter("date");
		String workingShiftId = request.getParameter("workingShiftId");
		Date dateWork = new Date(Long.parseLong(dateStr)); 
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("partyId", partyId);
		ctxMap.put("workingShiftId", workingShiftId);
		ctxMap.put("dateWork", dateWork);
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("locale", UtilHttp.getLocale(request));
		try {
			Map<String, Object> resultService = dispatcher.runSync("updateWorkingShiftEmployee", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
}
