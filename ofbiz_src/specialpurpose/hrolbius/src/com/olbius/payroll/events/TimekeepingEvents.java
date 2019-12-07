package com.olbius.payroll.events;

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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TimekeepingEvents {
	public static String updateAllEmplWorkingLateInPeriod(HttpServletRequest request, HttpServletResponse response) {
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		if(fromDateStr == null || thruDateStr == null){
			return "error";
		}
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    Delegator delegator = (Delegator)request.getAttribute("delegator");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    String partyId = userLogin.getString("partyId");
	    try {
			String orgPartyManageId = PartyUtil.getOrgByManager(partyId, delegator);
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgPartyManageId, false);
			List<GenericValue> emplList = buildOrg.getDirectEmployee(delegator);
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
			fromDate = UtilDateTime.getDayStart(fromDate);
			thruDate = UtilDateTime.getDayEnd(thruDate);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("timeZone", timeZone);
			Map<String, Object> resultService;
			for(GenericValue employee: emplList){
				ctxMap.put("partyId", employee.getString("partyId"));
				resultService = dispatcher.runSync("updateEmplWorkingLateInPeriod", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
					return "error";
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
	    request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
	    request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
		return "success";
	}
	
	public static String updateEmplAttendanceTracker(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    //Delegator delegator = (Delegator)request.getAttribute("delegator");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    TimeZone timeZone = UtilHttp.getTimeZone(request);
	    Locale locale = UtilHttp.getLocale(request);
	    String dateAttendanceStr = request.getParameter("date");
	    String startTimeStr = request.getParameter("startTime");
	    String endTimeStr = request.getParameter("endTime");
	    String partyId = request.getParameter("partyId");
	    Time startTime = null, endTime = null;
	    
	    if(dateAttendanceStr != null){
	    	try {
		    	Date dateAttendance = new Date(Long.parseLong(dateAttendanceStr));
		    	if(startTimeStr != null){
		    		startTime = new Time(Long.parseLong(startTimeStr));
		    	}
		    	if(endTimeStr != null){
		    		endTime = new Time(Long.parseLong(endTimeStr));
		    	}
	    	
				Map<String, Object> resultService = dispatcher.runSync("updateEmplAttendanceTracker", 
						UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance, 
										"startTime", startTime, "endTime", endTime, 
										"userLogin", userLogin, "timeZone", timeZone));
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
				}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
	    }
		return "success";
	}
	
	public static String deletePartyAttendance(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String dateAttendanceStr = request.getParameter("date");
		Date dateAttendance = new Date(Long.parseLong(dateAttendanceStr));
		try {
			List<GenericValue> partyAttendance = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance), null, false);
			for(GenericValue tempGV: partyAttendance){
				tempGV.remove();
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
 		return "success";
	}
	
	public static String getEmplTimesheetStatus(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String emplTimesheetId = request.getParameter("emplTimesheetId");
		try {
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet != null){
				String statusId = emplTimesheet.getString("statusId");
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("statusId", statusId);
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getEmplTimesheetAttendanceWorkdayAndHours(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String emplTimesheetId = request.getParameter("emplTimesheetId");
		String partyId = request.getParameter("partyId");
		String dateStr = request.getParameter("dateAttendance");
		Date dateAttendance = new Date(Long.parseLong(dateStr));
		try {
			List<GenericValue> emplTimesheetAttendance = delegator.findByAnd("EmplTimesheetAttendance", 
					UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "dateAttendance", dateAttendance), null, false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tempGv: emplTimesheetAttendance){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("emplTimekeepingSignId", tempGv.get("emplTimekeepingSignId"));
				tempMap.put("hours", tempGv.get("hours"));
				tempMap.put("workday", tempGv.get("workday"));
				listReturn.add(tempMap);
			}
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
