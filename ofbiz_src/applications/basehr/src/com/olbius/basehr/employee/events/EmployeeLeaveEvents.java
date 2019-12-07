package com.olbius.basehr.employee.events;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;

public class EmployeeLeaveEvents {
	public static String createEmplLeave(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		Security security = (Security) request.getAttribute("security");
		if(security.hasEntityPermission("HR_PROFILE", "_VIEW", userLogin)){
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			String fromDateStr = request.getParameter("fromDate");
			String thruDateStr = request.getParameter("thruDate");
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			paramMap.put("fromDate", fromDate);
			paramMap.put("thruDate", thruDate);
			paramMap.put("partyId", partyId);
			paramMap.put("userLogin", userLogin);
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEmplLeave", paramMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
				context.put("description", request.getParameter("description"));
				Map<String, Object> resultService = dispatcher.runSync("createEmplLeave", context);
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", UtilHttp.getLocale(request)));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				}
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, e.getLocalizedMessage());
			}
		}
		return "success";
	}
	
	public static String getEmplLeaveInfo(HttpServletRequest request, HttpServletResponse response){
		String yearStr = request.getParameter("year");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Integer year = null;
		Calendar cal = Calendar.getInstance();
		try{
			year = Integer.parseInt(yearStr);
		}catch(NumberFormatException e){
			year = cal.get(Calendar.YEAR);
		}
		
		String partyId = userLogin.getString("partyId");
		try {
			Map<String, Object> context = FastMap.newInstance();
			context.put("year", year);
			context.put("partyId", partyId);
			context.put("userLogin", userLogin);
			context.put("locale", UtilHttp.getLocale(request));
			context.put("timeZone", UtilHttp.getTimeZone(request));
			Map<String, Object> resultService = dispatcher.runSync("getEmplLeaveInfo", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				return "error";
			}
			request.setAttribute("annualLeft", resultService.get("annualLeft"));
			request.setAttribute("annualLeaveDayYear", resultService.get("annualLeaveDayYear"));
			request.setAttribute("annualLastYearTransferred", resultService.get("annualLastYearTransferred"));	
			request.setAttribute("annualLeaveRemain", resultService.get("annualLeaveRemain"));
			request.setAttribute("unpaidLeave", resultService.get("unpaidLeave"));
			request.setAttribute("annualGrantedLeaveInYear", resultService.get("annualGrantedLeaveInYear"));
		}  catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String cancelEmplLeave(HttpServletRequest request, HttpServletResponse response){
		//Security security = (Security) request.getAttribute("security");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String emplLeaveId = request.getParameter("emplLeaveId");
		try {
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			if(emplLeave == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "cannot found leave application");
				return "error";
			}
			String partyId = emplLeave.getString("partyId");
			if(userLogin.getString("partyId").equals(partyId)){
				String statusId = emplLeave.getString("statusId");
				if(!"LEAVE_CREATED".equals(statusId)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "OnlyCancalEmplLeaveApplInCreatedStatus", UtilHttp.getLocale(request)));
					return "error";
				}
				emplLeave.set("statusId", "LEAVE_CANCEL");
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "CancelEmplLeaveSuccess", UtilHttp.getLocale(request)));
				emplLeave.store();
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "you cannot permission to cancel this application");
				return "error";
				
			}
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
}
