package com.olbius.basehr.timekeeping.events;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
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
import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.importExport.ColumnConfig;
import com.olbius.basehr.importExport.DataType;
import com.olbius.basehr.importExport.HSSFBuilder;
import com.olbius.basehr.importExport.ImportExportExcel;
import com.olbius.basehr.importExport.ImportExportWorker;
import com.olbius.basehr.importExport.SheetConfig;
import com.olbius.basehr.timekeeping.helper.TimekeepingHelper;
import com.olbius.basehr.timekeeping.utils.TimekeepingUtils;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class TimekeepingEvents {
	/*public static String updateAllEmplWorkingLateInPeriod(HttpServletRequest request, HttpServletResponse response) {
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
	   // String partyId = userLogin.getString("partyId");
	    try {
	    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
	    	Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			List<String> emplList = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);
			fromDate = UtilDateTime.getDayStart(fromDate);
			thruDate = UtilDateTime.getDayEnd(thruDate);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("timeZone", timeZone);
			Map<String, Object> resultService;
			for(String employeeId: emplList){
				ctxMap.put("partyId", employeeId);
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
	    request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		return "success";
	}*/
	
	//maybe delete
	public static String updateEmplAttendanceTracker(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    Delegator delegator = (Delegator)request.getAttribute("delegator");
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
		    	String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				Map<String, Object> resultService = dispatcher.runSync("updateEmplAttendanceTracker", 
						UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance, 
										"startTime", startTime, "endTime", endTime, 
										"orgId", orgId,
										"userLogin", userLogin, "timeZone", timeZone));
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, (String)resultService.get(ModelService.ERROR_MESSAGE));
				}
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
	    }
		return "success";
	}
	//maybe delete
	public static String deletePartyAttendance(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String dateAttendanceStr = request.getParameter("date");
		Date dateAttendance = new Date(Long.parseLong(dateAttendanceStr));
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> partyAttendance = delegator.findByAnd("EmplAttendanceTracker", 
					UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance, "orgId", orgId), null, false);
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
	
	//maybe delete
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
	
	public static String updateEmplTimesheetAttendance(HttpServletRequest request, HttpServletResponse response){
		String editType = request.getParameter("editType");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	    Locale locale = UtilHttp.getLocale(request);
	    String dateAttendanceStr = request.getParameter("dateAttendance");
	    TimeZone timeZone = UtilHttp.getTimeZone(request);
	    Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	    Map<String, Object> context = null;
	    Map<String, Object> resultServices;
	    Date dateAttendance = new Date(Long.parseLong(dateAttendanceStr));
	    try {
	    	if("DELETE".equals(editType)){
				context = ServiceUtil.setServiceFields(dispatcher, "deleteEmplTimesheetAttendance", paramMap, userLogin, timeZone, locale);
				context.put("locale", locale);
				context.put("dateAttendance", dateAttendance);
				resultServices = dispatcher.runSync("deleteEmplTimesheetAttendance", context);
	    	}else{
	    		context = ServiceUtil.setServiceFields(dispatcher, "updateEmplTimesheetAttendance", paramMap, userLogin, timeZone, locale);
				context.put("locale", locale);
				context.put("dateAttendance", dateAttendance);
				resultServices = dispatcher.runSync("updateEmplTimesheetAttendance", context);
	    	}
	    	if(ServiceUtil.isSuccess(resultServices)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultServices.get(ModelService.ERROR_MESSAGE));
			}
	    } catch (GeneralServiceException e) {
	    	request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
	    	request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
	    	e.printStackTrace();
	    } catch (GenericServiceException e) {
	    	request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
	    	request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success"; 
	}
	
	public static String getWorkdayEmpl(HttpServletRequest request, HttpServletResponse response){
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(customTimePeriod == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				return "error";
			}
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			Float dayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dispatcher.getDispatchContext(), partyId, fromDateTs, thruDateTs, UtilHttp.getLocale(request), UtilHttp.getTimeZone(request));
			List<String> listDept = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDateTs, thruDateTs);
			Set<String> orgSet = FastSet.newInstance();
			for(String deptId: listDept){
				orgSet.add(PartyUtil.getSubsidiaryOfPartyGroup(delegator, deptId));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, orgSet));
			List<GenericValue> emplTimesheetList = delegator.findList("EmplTimesheets", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplTimesheetList)){
				Float workdayCalcPayroll = 0f;
				for(GenericValue emplTimesheet: emplTimesheetList){
					String emplTimesheetId = emplTimesheet.getString("emplTimesheetId");
					Map<String, Object> emplLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dispatcher.getDispatchContext(), emplTimesheetId, fromDateTs, thruDateTs, partyId);
					workdayCalcPayroll += dayWork - ((Float)emplLeave.get("totalDayLeave") - (Float)emplLeave.get("leavePaid"));
				}
				request.setAttribute("workdayCalcPayroll", workdayCalcPayroll);
			}else{
				request.setAttribute("workdayCalcPayroll", UtilProperties.getMessage("BaseHRUiLabels", "HRNotCalculate", UtilHttp.getLocale(request)));
			}
			request.setAttribute("dayWork", dayWork);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return "success";
	}
	public static String updateEmplWorkovertime(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		try {
			List<String> emplListId = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
			if(UtilValidate.isEmpty(emplListId) || !emplListId.contains(partyId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "ManagerNotManageEmployee", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "partyId", partyId), locale));
				return "error";
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateEmplWorkovertime", UtilHttp.getParameterMap(request), userLogin, UtilHttp.getTimeZone(request), locale);
			Map<String, Object> resultService = dispatcher.runSync("updateEmplWorkovertime", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String createTimekeepingDetail(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			try {
				Map<String, Object> parametersMap = CommonUtil.getParameterMapWithFileUploaded(request);
				String sheetIndexStr = (String)parametersMap.get("sheetIndex");
				String startLineStr = (String)parametersMap.get("startLine");
				String columnMapJson = (String)parametersMap.get("columnMap");
				String fromDateStr = (String)parametersMap.get("fromDate");
				String thruDateStr = (String)parametersMap.get("thruDate");
				if(fromDateStr == null || thruDateStr == null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, "From date and thru date is null");
					return "error";
				}
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
				parametersMap.put("fromDate", fromDate);
				parametersMap.put("thruDate", thruDate);
				int startLine = 0;
				try{
					startLine = Integer.parseInt(startLineStr);
				}catch(NumberFormatException e){
					startLine = 0;
				}
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createTimekeepingDetail", parametersMap, userLogin, timeZone, locale);
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createTimekeepingDetail", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				
				List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>)parametersMap.get("listFileUploaded");
				if(UtilValidate.isEmpty(listFileUploaded)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotNotFindExcelFileToImport", locale));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
				String _uploadedFile_contentType = (String)fileUploadedMap.get("_uploadedFile_contentType");
				ByteBuffer uploadedFile = (ByteBuffer)fileUploadedMap.get("uploadedFile");
				if(!ImportExportExcel.isExcelFile(_uploadedFile_contentType)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyAccpetXLSXFile", locale));
					TransactionUtil.rollback();
					return "error";
				}
				TransactionUtil.commit();
				String timekeepingDetailId = (String)resultService.get("timekeepingDetailId");
				Map<Integer, Object> columnExcelMap = ImportExportWorker.readColumnMapFromJson(columnMapJson);
				int totalRowImportSuccess = TimekeepingHelper.importTimekeepingDetailParty(dispatcher, delegator, userLogin, locale,
						timekeepingDetailId, uploadedFile, columnExcelMap, Integer.parseInt(sheetIndexStr), startLine);
				if(totalRowImportSuccess == 0){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NoDataFromExcelImported", locale));
					return "error";
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ImportDataEmplTimekeepingSuccess", 
						UtilMisc.toMap("totalRowImportSuccess", totalRowImportSuccess), locale));
			} catch (FileUploadException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (IOException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getTimekeepingDetailPartyInDateTimekeeping(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Security security = (Security) request.getAttribute("security");
		Locale locale = UtilHttp.getLocale(request);
		String userLoginPartyId = userLogin.getString("partyId");
		String partyId = request.getParameter("partyId");
		if(!userLoginPartyId.equals(partyId) && !security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		String timekeepingDetailId = request.getParameter("timekeepingDetailId");
		String dateTimekeepingStr = request.getParameter("dateTimekeeping");
		Date dateTimekeeping = null;
		try{
			dateTimekeeping = new Date(Long.parseLong(dateTimekeepingStr));
		}catch(NumberFormatException e){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		try {
			GenericValue timekeepingDetailParty = delegator.findOne("TimekeepingDetailParty", 
					UtilMisc.toMap("timekeepingDetailId", timekeepingDetailId, "dateTimekeeping", dateTimekeeping, "partyId", partyId), false);
			GenericValue timekeepingDetail = delegator.findOne("TimekeepingDetail", UtilMisc.toMap("timekeepingDetailId", timekeepingDetailId), false);
			if(timekeepingDetail == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				return "error";
			}
			GenericValue workingShiftEmpl = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", dateTimekeeping), false);
			String workingShiftId = null;
			if(workingShiftEmpl != null){
				workingShiftId = workingShiftEmpl.getString("workingShiftId");
			}else{
				workingShiftId = timekeepingDetail.getString("workingShiftId");
			}
			Map<String, Boolean> checkMapDayLeave = TimekeepingHelper.checkDayIsDayLeaveOfParty(delegator, partyId, workingShiftId, dateTimekeeping);
			request.setAttribute("isDayLeave", checkMapDayLeave.get("isDayLeave"));
			if(timekeepingDetailParty != null){
				Map<String, Object> data = timekeepingDetailParty.getFields(UtilMisc.toList("workdayActual", "workdayLeavePaid", 
						"overtimeHours", "workingLateMinutes", "workdayStandard"));
				request.setAttribute("data", data);
			}
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	public static String updateTimekeepingDetailParty(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateTimekeepingStr = request.getParameter("dateTimekeeping");
		Date dateTimekeeping = null;
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try{
			dateTimekeeping = new Date(Long.parseLong(dateTimekeepingStr));
		}catch(NumberFormatException e){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		paramMap.put("dateTimekeeping", dateTimekeeping);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateTimekeepingDetailParty", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updateTimekeepingDetailParty", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
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
	@SuppressWarnings("unchecked")
	public static String updateTimekeepingDetailPartyList(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		try {
			Map<String, Object> parametersMap = CommonUtil.getParameterMapWithFileUploaded(request);
			String timekeepingDetailId = (String)parametersMap.get("timekeepingDetailId");
			String columnMapParam = (String)parametersMap.get("columnMap");
			String sheetIndexStr = (String)parametersMap.get("sheetIndex");
			String startLineStr = (String)parametersMap.get("startLine");
			if(columnMapParam == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotNotFindExcelFileToImport", locale));
				return "error";
			}
			List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>)parametersMap.get("listFileUploaded");
			if(UtilValidate.isEmpty(listFileUploaded)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotNotFindExcelFileToImport", locale));
				return "error";
			}
			Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
			String _uploadedFile_contentType = (String)fileUploadedMap.get("_uploadedFile_contentType");
			ByteBuffer uploadedFile = (ByteBuffer)fileUploadedMap.get("uploadedFile");
			if(!ImportExportExcel.isExcelFile(_uploadedFile_contentType)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyAccpetXLSXFile", locale));
				return "error";
			}
			int startLine = 0;
			try{
				startLine = Integer.parseInt(startLineStr);
			}catch(NumberFormatException e){
				startLine = 0;
			}
			Map<Integer, Object> columnExcelMap = ImportExportWorker.readColumnMapFromJson(columnMapParam);
			int totalRowImportSuccess = TimekeepingHelper.importTimekeepingDetailParty(dispatcher, delegator, userLogin, locale,
					timekeepingDetailId, uploadedFile, columnExcelMap, Integer.parseInt(sheetIndexStr), startLine);
			if(totalRowImportSuccess == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NoDataFromExcelImported", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "ImportDataEmplTimekeepingSuccess", 
					UtilMisc.toMap("totalRowImportSuccess", totalRowImportSuccess), locale));
		} catch (FileUploadException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		
		return "success";
	}
	public static String updateTimekeepingDataRelated(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		//Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Security security = (Security) request.getAttribute("security");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		if(!security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null; 
		try{
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
		}catch(NumberFormatException e){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateTimekeepingDataRelated", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updateTimekeepingDataRelated", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRPayrollUiLabels", "UpdateTimekeepingDataRelatedSuccess", locale));
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
	public static String getTimekeepingDetailInMonthYear(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Security security = (Security) request.getAttribute("security");
		Locale locale = UtilHttp.getLocale(request);
		if(!security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		String monthStr = request.getParameter("month");
		String yearStr = request.getParameter("year");
		String partyId = request.getParameter("partyId");
		Long month = null, year = null;
		try {
			month = Long.parseLong(monthStr);
			year = Long.parseLong(yearStr);
		} catch(NumberFormatException e){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		if(partyId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "partyId is null");
			return "error";
		}
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> allDeptList = buildOrg.getAllDepartmentList(delegator);
			List<String> allDeptId = null;
			if(UtilValidate.isNotEmpty(allDeptList)){
				allDeptId = EntityUtil.getFieldListFromEntityList(allDeptList, "partyId", true);
			}else{
				allDeptId = FastList.newInstance();
			}
			allDeptId.add(partyId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("month", month));
			conds.add(EntityCondition.makeCondition("year", year));
			conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allDeptId));
			List<GenericValue> listTimekeepingDetail = delegator.findList("TimekeepingDetail", EntityCondition.makeCondition(conds), 
					UtilMisc.toSet("timekeepingDetailId", "timekeepingDetailName"), UtilMisc.toList("timekeepingDetailName"), null, false);
			request.setAttribute("listReturn", listTimekeepingDetail);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String createEmplTimesheetSummary(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		//Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null;
		String timekeepingDetailIdParam = request.getParameter("timekeepingDetailId");
		if(timekeepingDetailIdParam == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "timekeeping detail is null");
			return "error";
		}
		if(fromDateStr == null || thruDateStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "From date and thru date is null");
			return "error";
		}
		try{
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			paramMap.put("fromDate", fromDate);
			paramMap.put("thruDate", thruDate);
		}catch(NumberFormatException e){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		try{
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createTimekeepingSummary", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createTimekeepingSummary", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String timekeepingSummaryId = (String)resultService.get("timekeepingSummaryId");
				JSONArray timekeepingDetailIdJson = JSONArray.fromObject(timekeepingDetailIdParam);
				context.clear();
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				context.put("timekeepingSummaryId", timekeepingSummaryId);
				for(int i = 0; i < timekeepingDetailIdJson.size(); i++){
					String timekeepingDetailId = timekeepingDetailIdJson.getString(i);
					context.put("timekeepingDetailId", timekeepingDetailId);
					resultService = dispatcher.runSync("createTimekeepingSummaryDetail", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				context.clear();
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				context.put("timekeepingSummaryId", timekeepingSummaryId);
				resultService = dispatcher.runSync("updateTimekeepingSummaryPartyFromTimekeepingDetail", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
				TransactionUtil.commit();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String getTimekeepingSummaryByMonthYear(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Security security = (Security) request.getAttribute("security");
		Locale locale = UtilHttp.getLocale(request);
		if(!security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		String monthStr = request.getParameter("month");
		String yearStr = request.getParameter("year");
		if(monthStr == null || yearStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		Long month = Long.parseLong(monthStr);
		Long year = Long.parseLong(yearStr);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("year", year));
		conds.add(EntityCondition.makeCondition("month", month));
		List<GenericValue> timekeepingSummaryList;
		try {
			timekeepingSummaryList = delegator.findList("TimekeepingSummary", EntityCondition.makeCondition(conds), 
					UtilMisc.toSet("timekeepingSummaryId", "timekeepingSummaryName"), UtilMisc.toList("timekeepingSummaryName"), null, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", timekeepingSummaryList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String downloadTimesheetTemplate(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Security security = (Security) request.getAttribute("security");
		Locale locale = UtilHttp.getLocale(request);
		if(!security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", userLogin)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		String monthStr = request.getParameter("month");
		String yearStr = request.getParameter("year");
		Integer month = null, year = null;
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(fromDateStr)));
		}else{
			fromDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}else{
			thruDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		}
		Calendar cal = Calendar.getInstance();
		if(monthStr != null){
			month = Integer.parseInt(monthStr);
		}else{
			month = cal.get(Calendar.MONTH) + 1;
		}
		if(yearStr != null){
			year = Integer.parseInt(yearStr);
		}else{
			year = cal.get(Calendar.YEAR);
		}
		List<String> dayNameOfWeekShortList = FastList.newInstance();
		dayNameOfWeekShortList.add(0, UtilProperties.getMessage("BaseHRUiLabels", "CommonSundayShort", locale));
		dayNameOfWeekShortList.add(1, UtilProperties.getMessage("BaseHRUiLabels", "CommonMondayShort", locale));
		dayNameOfWeekShortList.add(2, UtilProperties.getMessage("BaseHRUiLabels", "CommonTuesdayShort", locale));
		dayNameOfWeekShortList.add(3, UtilProperties.getMessage("BaseHRUiLabels", "CommonWednesdayShort", locale));
		dayNameOfWeekShortList.add(4, UtilProperties.getMessage("BaseHRUiLabels", "CommonThursdayShort", locale));
		dayNameOfWeekShortList.add(5, UtilProperties.getMessage("BaseHRUiLabels", "CommonFridayShort", locale));
		dayNameOfWeekShortList.add(6, UtilProperties.getMessage("BaseHRUiLabels", "CommonSaturdayShort", locale));
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<Map<String, Object>> excelData = FastList.newInstance();
			for(GenericValue empl: emplList){
				excelData.add(empl.getAllFields());
			}
			SheetConfig sheet1 = new SheetConfig();
			sheet1.setSheetName(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimekeeping", locale));
			sheet1.setTitle(UtilProperties.getMessage("BaseHRPayrollUiLabels", "TimesheetDetailListParty", 
					UtilMisc.toMap("month", String.valueOf(month), "year", String.valueOf(year)), locale));
			sheet1.setHeaderHeight((short)800);
			List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();
			ColumnConfig partyCodeColumn = new ColumnConfig();
			partyCodeColumn.setDataType(DataType.STRING);
			partyCodeColumn.setWidth(15 * 256);
			partyCodeColumn.setHeader(UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmployeeId", locale));
			partyCodeColumn.setName("partyCode");
			ColumnConfig employeeNameColumn = new ColumnConfig();
			employeeNameColumn.setDataType(DataType.STRING);
			employeeNameColumn.setWidth(30 * 256);
			employeeNameColumn.setHeader(UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmployeeName", locale));
			employeeNameColumn.setName("fullName");
			columnConfigList.add(partyCodeColumn);
			columnConfigList.add(employeeNameColumn);
			Timestamp tempFromDate = (Timestamp)fromDate.clone();
			while(tempFromDate.compareTo(thruDate) < 0){
				cal.setTime(tempFromDate);
				ColumnConfig dateColumn = new ColumnConfig();
				dateColumn.setDataType(DataType.STRING);
				dateColumn.setWidth(11 * 256);
				dateColumn.setHeader(dayNameOfWeekShortList.get(cal.get(Calendar.DAY_OF_WEEK) - 1) + "-" + DateUtil.getDateTowDigits(cal) + "/" + DateUtil.getMonthTowDigits(cal));
				columnConfigList.add(dateColumn);
				tempFromDate = UtilDateTime.getDayStart(tempFromDate, 1);
			}
			sheet1.setColumnConfig(columnConfigList);
			sheet1.setDataConfig(excelData);
			HSSFBuilder builder = new HSSFBuilder();
			Workbook wb = builder.build(UtilMisc.toList(sheet1), locale, fromDate, thruDate);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				wb.write(baos);
				byte[] bytes = baos.toByteArray();
				response.setHeader("content-disposition", "attachment;filename=" + 
						UtilProperties.getMessage("BaseHRPayrollUiLabels", "TimesheetDetailListParty",  
								UtilMisc.toMap("month", String.valueOf(month), "year", String.valueOf(year)), locale) +".xlsx");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/vnd.ms-excel");
				response.getOutputStream().write(bytes);
			} catch (IOException e) {
				Debug.log(e.getMessage());
			} finally {
				if(baos != null){
					try {
						baos.close();
					} catch (IOException e) {
						Debug.log(e.getMessage());
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
