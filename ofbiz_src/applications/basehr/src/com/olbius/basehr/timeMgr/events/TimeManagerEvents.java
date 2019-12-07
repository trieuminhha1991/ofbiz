package com.olbius.basehr.timeMgr.events;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.timeMgr.helper.TimeManagerHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

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
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
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
				List<Map<String, String>> orgQueue = FastList.newInstance();
				Map<String, String> rootPartyMap = FastMap.newInstance();
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
				String defaultWorkingShiftId = TimeManagerHelper.getWorkingShiftOfParty(delegator, partyId, userLogin.getString("userLoginId"));
				rootPartyMap.put("partyId", partyId);
				rootPartyMap.put("parentId", null);
				orgQueue.add(rootPartyMap);
				GenericValue party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				Map<String, String> orgAndWSMap = FastMap.newInstance();
				orgAndWSMap.put(partyId, defaultWorkingShiftId);
				if(defaultWorkingShiftId != null){
					while(orgQueue.size() > 0){
						Map<String, String> tempParty = orgQueue.remove(0);
						String parentId = tempParty.get("parentId");
						String tempPartyId = tempParty.get("partyId");
						String tempWSId = null;
						GenericValue workingShiftPartyConfig = delegator.findOne("WorkingShiftPartyConfig", UtilMisc.toMap("partyId", tempPartyId), false);
						if(workingShiftPartyConfig != null){
							tempWSId = workingShiftPartyConfig.getString("workingShiftId");
						}else if(parentId != null){
							tempWSId = orgAndWSMap.get(parentId);
						}
						if(tempWSId == null){
							tempWSId = defaultWorkingShiftId;
						}
						orgAndWSMap.put(tempPartyId, tempWSId);
						Organization buildOrg = PartyUtil.buildOrg(delegator, tempPartyId, false, false);
						List<GenericValue> emplList = buildOrg.getDirectEmployee(delegator, fromDate, thruDate);
						Map<String, Object> ctxMap = FastMap.newInstance();
						ctxMap.put("workingShiftId", tempWSId);
						ctxMap.put("fromDate", new Date(fromDate.getTime()));
						ctxMap.put("thruDate", new Date(thruDate.getTime()));
						ctxMap.put("userLogin", userLogin);
						for(GenericValue empl: emplList){
							String tempEmplId = empl.getString("partyId");
							ctxMap.put("partyId", tempEmplId);
							dispatcher.runSync("editWorkingShiftEmployeeInPeriod", ctxMap);
						}
						List<GenericValue> directListChild = buildOrg.getDirectChildList(delegator);
						if(directListChild != null){
							for(GenericValue child: directListChild){
								String childId = child.getString("partyId");
								Map<String, String> tempMap = FastMap.newInstance();
								tempMap.put("partyId", childId);
								tempMap.put("parentId", tempPartyId);
								orgQueue.add(tempMap);
							}
						}
					}
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "AssignWorkingShiftPartySuccess", 
							UtilMisc.toMap("partyName", party.getString("groupName")), UtilHttp.getLocale(request)));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "PartyNotConfigWorkingShift", 
							UtilMisc.toMap("partyName", party.getString("groupName")), UtilHttp.getLocale(request)));
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
	
	public static String assignWorkingShiftForGroupEmpl(HttpServletRequest request, HttpServletResponse response){
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String workingShiftId = request.getParameter("workingShiftId");
		if(fromDateStr == null || thruDateStr == null || workingShiftId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		Locale locale = UtilHttp.getLocale(request);
		String overrideData = request.getParameter("overrideData");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		JSONArray partyIdsJson = JSONArray.fromObject(request.getParameter("partyIds"));
		Date fromDate = new Date(Long.parseLong(fromDateStr));
		Date thruDate = new Date(Long.parseLong(thruDateStr));
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("workingShiftId", workingShiftId);
		ctxMap.put("fromDate", new Date(fromDate.getTime()));
		ctxMap.put("thruDate", new Date(thruDate.getTime()));
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("isOverride", overrideData);
		ctxMap.put("locale", UtilHttp.getLocale(request));
		Map<String, Object> resultService = null;
		List<String> partyIdErrorAssign = FastList.newInstance();
		for(int i = 0; i < partyIdsJson.size(); i++){
			String partyId = partyIdsJson.getString(i);
			ctxMap.put("partyId", partyId);
			try {
				resultService = dispatcher.runSync("editWorkingShiftEmployeeInPeriod", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					partyIdErrorAssign.add(partyId);
				}
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		if(UtilValidate.isNotEmpty(partyIdErrorAssign)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute("isUpdate", true);
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ErroWhenAssignWSForPartys", 
					UtilMisc.toMap("partyIdsErr", StringUtils.join(partyIdErrorAssign, ", ")), locale));
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "AssignWorkingShiftForPartySuccess", locale));
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
	
	public static String updateEmplLeaveFromTimeTracker(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		if(fromDateStr == null || thruDateStr == null){
			return "error";
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("locale", UtilHttp.getLocale(request));
		try {
			Map<String, Object> resultService = dispatcher.runSync("updateEmplLeaveFromTimeTracker", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", UtilHttp.getLocale(request)));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	public static String createWorkOvertimeRegis(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String dateRegisteredStr = request.getParameter("dateRegistered");
		String startTimeStr = request.getParameter("startTime");
		String endTimeStr = request.getParameter("endTime");
		String enumIdList = request.getParameter("enumIdList"); 
		Timestamp dateRegistered = new Timestamp(Long.parseLong(dateRegisteredStr));
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Time startTime = new Time(Long.parseLong(startTimeStr));
		Time endTime = new Time(Long.parseLong(endTimeStr));
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("dateRegistered", dateRegistered);
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createWorkOvertimeRegis", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			try {
				TransactionUtil.begin();
				Map<String, Object> resultServices = dispatcher.runSync("createWorkOvertimeRegis", context);
				if(!ServiceUtil.isSuccess(resultServices)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
					TransactionUtil.rollback();
					return "error";
				}
				String workOvertimeRegisId = (String)resultServices.get("workOvertimeRegisId");
				if(enumIdList != null){
					JSONArray enumIdListJson = JSONArray.fromObject(enumIdList);
					context.clear();
					context.put("userLogin", userLogin);
					context.put("workOvertimeRegisId", workOvertimeRegisId);
					for(int i = 0; i < enumIdListJson.size(); i++){
						String enumId = enumIdListJson.getString(i);
						context.put("enumId", enumId);
						resultServices = dispatcher.runSync("createWorkOvertimeRegisEnum", context);
						if(!ServiceUtil.isSuccess(resultServices)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
				TransactionUtil.commit();
			} catch (GenericTransactionException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			return "error";
		}
		return "success";
	}
	
	public static String approvalWorkingOvertimeRegister(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String dateRegisteredStr = request.getParameter("dateRegistered");
		String startTimeStr = request.getParameter("startTime");
		String endTimeStr = request.getParameter("endTime");
		Timestamp dateRegistered = new Timestamp(Long.parseLong(dateRegisteredStr));
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Time startTime = new Time(Long.parseLong(startTimeStr));
		Time endTime = new Time(Long.parseLong(endTimeStr));
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("dateRegistered", dateRegistered);
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "approvalWorkingOvertimeRegister", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultServices = dispatcher.runSync("approvalWorkingOvertimeRegister", context);
			if(!ServiceUtil.isSuccess(resultServices)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getWorkOvertimeRegisEnum(HttpServletRequest request, HttpServletResponse response){
		String workOvertimeRegisId = request.getParameter("workOvertimeRegisId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> workOvertimeRegisEnumList = delegator.findByAnd("WorkOvertimeRegisEnum", UtilMisc.toMap("workOvertimeRegisId", workOvertimeRegisId), null, false);
			List<String> enumIdList = EntityUtil.getFieldListFromEntityList(workOvertimeRegisEnumList, "enumId", true);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("enumIdList", enumIdList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	public static String getWorkingLateRegisEnum(HttpServletRequest request, HttpServletResponse response){
		String workingLateRegisterId = request.getParameter("workingLateRegisterId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> workingLateRegisEnumList = delegator.findByAnd("WorkingLateRegisEnum", UtilMisc.toMap("workingLateRegisterId", workingLateRegisterId), null, false);
			List<String> enumIdList = EntityUtil.getFieldListFromEntityList(workingLateRegisEnumList, "enumId", true);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("enumIdList", enumIdList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String createWorkingLateRegis(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String enumIdList = request.getParameter("enumIdList"); 
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createWorkingLateRegis", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			try {
				TransactionUtil.begin();
				Map<String, Object> resultServices = dispatcher.runSync("createWorkingLateRegis", context);
				if(!ServiceUtil.isSuccess(resultServices)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
					TransactionUtil.rollback();
					return "error";
				}
				String workingLateRegisterId = (String)resultServices.get("workingLateRegisterId");
				if(enumIdList != null){
					JSONArray enumIdListJson = JSONArray.fromObject(enumIdList);
					context.clear();
					context.put("userLogin", userLogin);
					context.put("workingLateRegisterId", workingLateRegisterId);
					for(int i = 0; i < enumIdListJson.size(); i++){
						String enumId = enumIdListJson.getString(i);
						context.put("enumId", enumId);
						resultServices = dispatcher.runSync("createWorkingLateRegisEnum", context);
						if(!ServiceUtil.isSuccess(resultServices)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
				TransactionUtil.commit();
			}  catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String approvalWorkingLateRegister(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "approvalWorkingLateRegister", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultServices = dispatcher.runSync("approvalWorkingLateRegister", context);
			if(!ServiceUtil.isSuccess(resultServices)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
}
