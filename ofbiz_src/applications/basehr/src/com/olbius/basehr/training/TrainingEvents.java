package com.olbius.basehr.training;

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
import net.sf.json.JSONObject;

import org.ofbiz.base.util.StringUtil;
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

import com.olbius.basehr.util.CommonUtil;

public class TrainingEvents {
	@SuppressWarnings("unchecked")
	public static String createNewTrainingCourse(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String registerFromDateStr = request.getParameter("registerFromDate");
		String registerThruDateStr = request.getParameter("registerThruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		Timestamp registerFromDate = new Timestamp(Long.parseLong(registerFromDateStr));
		Timestamp registerThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(registerThruDateStr)));
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		paramMap.put("registerFromDate", registerFromDate);
		paramMap.put("registerThruDate", registerThruDate);
		try {
			TransactionUtil.begin();
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createTrainingCourse", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("createTrainingCourse", context);
			if(ServiceUtil.isSuccess(resultService)){
				String trainingCourseId = (String)resultService.get("trainingCourseId");
				String skillTypeStr = request.getParameter("skillTypeIds");
				if(skillTypeStr != null){
					JSONArray skillTypeJson = JSONArray.fromObject(skillTypeStr);
					Map<String, Object> trainingSkillTypeMap = FastMap.newInstance();
					trainingSkillTypeMap.put("userLogin", userLogin);
					trainingSkillTypeMap.put("trainingCourseId", trainingCourseId);
					for(int i = 0; i < skillTypeJson.size(); i++){
						JSONObject obj = skillTypeJson.getJSONObject(i);
						trainingSkillTypeMap.put("skillTypeId", obj.getString("skillTypeId"));
						trainingSkillTypeMap.put("resultTypeId", obj.has("resultTypeId")? obj.getString("resultTypeId") : null);
						dispatcher.runSync("createTrainingCourseSkillType", trainingSkillTypeMap);
					}
				}
				
				//get party attendance
				String partyAttExpectedStr = request.getParameter("partyIds");
				JSONArray partyAttExpectedJson = null;
				List<Map<String, Object>> listParty = null;
				if(partyAttExpectedStr != null){
					partyAttExpectedJson = JSONArray.fromObject(partyAttExpectedStr);
				}
				
				//get party employee if tranining course allow all party can regist 
				String isPublic = request.getParameter("isPublic");
				if(isPublic != null && isPublic.equals("Y")){
					Map<String, Object> mapContext = FastMap.newInstance();
					mapContext.put("userLogin", userLogin);
					Map<String, Object> serviceResult = dispatcher.runSync("getAllEmployeeInOrg", mapContext);
					listParty = (List<Map<String, Object>>) serviceResult.get("listReturn");
				}
				
				//remove duplicate element between list all party and list party attendance
				if(listParty != null){
					for(int i=0; i < partyAttExpectedJson.size(); i++){
						for(int j=0; j< listParty.size(); j++){
							if(listParty.get(j).get("partyId").toString().equals(partyAttExpectedJson.get(i).toString())){
								listParty.remove(j);
							}
						}
					}
				}
				
				//send notification to party attendance training course
				if(partyAttExpectedJson.size() > 0){
					Map<String, Object> trainingPartyMap = FastMap.newInstance();
					trainingPartyMap.put("userLogin", userLogin);
					trainingPartyMap.put("trainingCourseId", trainingCourseId);
					for(int i = 0; i < partyAttExpectedJson.size(); i++){
						trainingPartyMap.put("partyId", partyAttExpectedJson.getString(i));
						trainingPartyMap.put("isExpectedAttend", Boolean.TRUE);
						dispatcher.runSync("createTrainingCoursePartyAttendance", trainingPartyMap);
						
						trainingPartyMap.remove("isExpectedAttend");
						dispatcher.runSync("sendNtfTrainingCourseToParty", trainingPartyMap);
					}
				}
				
				//send notification to party can regist except party attendance
				if(listParty !=  null){
					Map<String, Object> trainingPartyMap = FastMap.newInstance();
					trainingPartyMap.put("userLogin", userLogin);
					trainingPartyMap.put("trainingCourseId", trainingCourseId);
					for(Map<String, Object> party: listParty){
						trainingPartyMap.put("partyId", party.get("partyId").toString());
						dispatcher.runSync("sendNtfTrainingCourseToParty", trainingPartyMap);
					}
				}
				
				String trainingPurposeTypeIds = request.getParameter("trainingPurposeTypeIds");
				if(trainingPurposeTypeIds != null){
					JSONArray trainingPurposeTypeIdsJson = JSONArray.fromObject(trainingPurposeTypeIds);
					Map<String, Object> trainingPurposeMap = FastMap.newInstance();
					trainingPurposeMap.put("userLogin", userLogin);
					trainingPurposeMap.put("trainingCourseId", trainingCourseId);
					for(int i = 0; i < trainingPurposeTypeIdsJson.size(); i++){
						trainingPurposeMap.put("trainingPurposeTypeId", trainingPurposeTypeIdsJson.getString(i));
						dispatcher.runSync("createTrainingCoursePurpose", trainingPurposeMap);
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
				TransactionUtil.commit();
			}else{
				String err = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, err);
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
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
	
	public static String updateTrainingCourse(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String trainingCourseId = request.getParameter("trainingCourseId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String registerFromDateStr = request.getParameter("registerFromDate");
		String registerThruDateStr = request.getParameter("registerThruDate");
		if(fromDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			paramMap.put("fromDate", fromDate);
		}
		if(thruDateStr != null){
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			paramMap.put("thruDate", thruDate);
		}
		if(registerFromDateStr != null){
			Timestamp registerFromDate = new Timestamp(Long.parseLong(registerFromDateStr));
			paramMap.put("registerFromDate", registerFromDate);
		}
		if(registerThruDateStr != null){
			Timestamp registerThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(registerThruDateStr)));
			paramMap.put("registerThruDate", registerThruDate);
		}
		try {
			TransactionUtil.begin();
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateTrainingCourse", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updateTrainingCourse", context);
			if(ServiceUtil.isSuccess(resultService)){
				String trainingPurposeTypeIds = request.getParameter("trainingPurposeTypeIds");
				List<String> trainingCoursePurposeList = FastList.newInstance();
				if(trainingPurposeTypeIds != null){
					JSONArray trainingPurposeTypeIdsJson = JSONArray.fromObject(trainingPurposeTypeIds);
					for(int i = 0; i < trainingPurposeTypeIdsJson.size(); i++){
						trainingCoursePurposeList.add(trainingPurposeTypeIdsJson.getString(i));
					}
				}
				Map<String, Object> trainingPurposeMap = FastMap.newInstance();
				trainingPurposeMap.put("trainingCourseId", trainingCourseId);
				trainingPurposeMap.put("userLogin", userLogin);
				trainingPurposeMap.put("trainingPurposeTypeIds", trainingCoursePurposeList);
				dispatcher.runSync("editTrainingCoursePurpose", trainingPurposeMap);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
				TransactionUtil.commit();
			}else{
				String err = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, err);
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
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
	
	public static String getTrainingCourseInfo(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String trainingCourseId = request.getParameter("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindTrainingCourse", 
						UtilMisc.toMap("trainingCourseId", trainingCourseId), UtilHttp.getLocale(request)));
				return "error";
			}
			Map<String, Object> retMap = trainingCourse.getAllFields();
			List<GenericValue> trainingCoursePurpose = delegator.findByAnd("TrainingCoursePurpose", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
			if(UtilValidate.isNotEmpty(trainingCoursePurpose)){
				List<String> trainingPurposeTypeIds = EntityUtil.getFieldListFromEntityList(trainingCoursePurpose, "trainingPurposeTypeId", true);
				retMap.put("trainingPurposeTypeIds", trainingPurposeTypeIds);
			}
			Timestamp fromDate = trainingCourse.getTimestamp("fromDate");
			Timestamp thruDate = trainingCourse.getTimestamp("thruDate");
			/*Timestamp actualFromDate = trainingCourse.getTimestamp("actualFromDate");
			Timestamp actualThruDate = trainingCourse.getTimestamp("actualThruDate");*/
			Timestamp registerFromDate = trainingCourse.getTimestamp("registerFromDate");
			Timestamp registerThruDate = trainingCourse.getTimestamp("registerThruDate");
			if(fromDate != null){
				retMap.put("fromDate", fromDate.getTime());	
			}
			if(thruDate != null){
				retMap.put("thruDate", thruDate.getTime());	
			}
			/*if(actualFromDate != null){
				retMap.put("actualFromDate", actualFromDate.getTime());	
			}
			if(actualThruDate != null){
				retMap.put("actualThruDate", actualThruDate.getTime());	
			}*/
			if(registerFromDate != null){
				retMap.put("registerFromDate", registerFromDate.getTime());	
			}
			if(registerThruDate != null){
				retMap.put("registerThruDate", registerThruDate.getTime());	
			}
			request.setAttribute("trainingCourse", retMap);
			request.setAttribute("description", trainingCourse.getString("description"));
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getTrainingCourseCost(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String trainingCourseId = request.getParameter("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindTrainingCourse", 
						UtilMisc.toMap("trainingCourseId", trainingCourseId), UtilHttp.getLocale(request)));
				return "error";
			}
			Map<String, Object> retMap = FastMap.newInstance();
			retMap.put("providerId", trainingCourse.get("providerId"));
			retMap.put("estimatedEmplPaid", trainingCourse.get("estimatedEmplPaid"));
			retMap.put("estimatedNumber", trainingCourse.get("estimatedNumber"));
			retMap.put("amountCompanySupport", trainingCourse.get("amountCompanySupport"));
			retMap.put("isPublic", trainingCourse.get("isPublic"));
			retMap.put("isCancelRegister", trainingCourse.get("isCancelRegister"));
			retMap.put("cancelBeforeDay", trainingCourse.get("cancelBeforeDay"));
			request.setAttribute("trainingCostData", retMap);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String editTrainingCourseSkillType(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		String skillTypeIds = request.getParameter("skillTypeId");
		String trainingCourseId = request.getParameter("trainingCourseId");
		JSONArray skillTypeIdJson = JSONArray.fromObject(skillTypeIds);
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("trainingCourseId", trainingCourseId);
		for(int i = 0; i < skillTypeIdJson.size(); i++){
			JSONObject skillTypeObj = skillTypeIdJson.getJSONObject(i);
			context.put("skillTypeId", skillTypeObj.get("skillTypeId"));
			context.put("resultTypeId", skillTypeObj.has("resultTypeId")? skillTypeObj.getString("resultTypeId") : null);
			try {
				dispatcher.runSync("createTrainingCourseSkillType", context);
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		return "success";
	}
	
	public static String createTrainingPartyExpectedAtt(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String partyIds = (String)parameterMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		String trainingCourseId = request.getParameter("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return "error";
			}
			String statusId = trainingCourse.getString("statusId");
			if(!"TRAINING_PLANNED".equals(statusId) && !"TRAINING_PLANNED_REJ".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotAddPartyToTrainingInStatus", 
						UtilMisc.toMap("status", statusItem.getString("description")), locale));
				return "error";
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createTrainingCoursePartyAttendance", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			context.put("isExpectedAttend", Boolean.TRUE);
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("createTrainingCoursePartyAttendance", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
			}
			if(successCreate == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "AddListEmplToTrainigCourseFail", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "AddListEmplToTrainigCourseSuccess",
					UtilMisc.toMap("successCreate", successCreate, "total", totalEmpl), locale));
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} 
		return "success";
	}
	
	public static String createTrainingCoursePartyAttendance(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String partyIds = (String)parameterMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		String trainingCourseId = request.getParameter("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return "error";
			}
			String statusId = trainingCourse.getString("statusId");
			if(!("TRAINING_SUMMARY".equals(statusId) || "TRAINING_PLANNED_ACC".equals(statusId))){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotAddPartyToTrainingInStatus", 
						UtilMisc.toMap("status", statusItem.getString("description")), locale));
				return "error";
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createTrainingCoursePartyAttendance", parameterMap, 
					userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			context.put("statusId", "TCR_ATTENDANCE");
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("createTrainingCoursePartyAttendance", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
			}
			if(successCreate == 0){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "AddListEmplToTrainigCourseFail", locale));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "AddListEmplToTrainigCourseSuccess",
					UtilMisc.toMap("successCreate", successCreate, "total", totalEmpl), locale));
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		} 
		return "success";
	}
	
	public static String getTotalEmplAttendanceTraining(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String trainingCourseId = request.getParameter("trainingCourseId");
		
		try {
			List<GenericValue> totalEmplAtt = delegator.findByAnd("TrainingCourseAndTotalPartyAtt", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
			if(UtilValidate.isNotEmpty(totalEmplAtt)){
				request.setAttribute("totalActualAtt", totalEmplAtt.get(0).get("totalPartyAtt"));
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String summaryTrainingCourse(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("actualFromDate");
		String thruDateStr = request.getParameter("actualThruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Timestamp actualFromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		paramMap.put("actualFromDate", actualFromDate);
		paramMap.put("actualThruDate", actualThruDate);
		paramMap.put("statusId", "TRAINING_SUMMARY");
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "summaryTrainingCourse", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("summaryTrainingCourse", context);
			if(!ServiceUtil.isSuccess(resultService)){
				String err = CommonUtil.getErrorMessageFromService(resultService);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, err);
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
	public static String updateTrainingCourseSummarized(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("actualFromDate");
		String thruDateStr = request.getParameter("actualThruDate");
		String trainingCourseId = request.getParameter("trainingCourseId");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		if(fromDateStr != null){
			Timestamp actualFromDate = new Timestamp(Long.parseLong(fromDateStr));
			paramMap.put("actualFromDate", actualFromDate);
		}
		if(thruDateStr != null){
			Timestamp actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			paramMap.put("actualThruDate", actualThruDate);
		}
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseNotFound", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
				return "error";
			}
			String statusId = trainingCourse.getString("statusId");
			if(!"TRAINING_SUMMARY".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotUpdateInStatus", UtilMisc.toMap("status", statusItem.get("description")), locale));
				return "error";
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateTrainingCourse", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updateTrainingCourse", context);
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
			return "error";
		} catch (GenericEntityException e) {
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
	
	public static String approvalEmplRegisterTraining(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String partyIds = request.getParameter("partyIds");
		String accept = request.getParameter("accept");
		String trainingCourseId = request.getParameter("trainingCourseId");
		if(partyIds == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotFindEmployeeToApprove", locale));
			return "error";
		}
		JSONArray partyJsonArr = JSONArray.fromObject(partyIds);
		String statusId = null;
		if("Y".equals(accept)){
			statusId = "TCR_REGIS_ACC";
		}else{
			statusId = "TCR_REGIS_REJ";
		}
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("timeZone", timeZone);
		context.put("statusIdRegister", statusId);
		context.put("trainingCourseId", trainingCourseId);
		int totalSuccess = 0;
		Map<String, Object> resultServices = null;
		for(int i = 0; i < partyJsonArr.size(); i++){
			String partyId = partyJsonArr.getString(i);
			context.put("partyId", partyId);
			try {
				resultServices = dispatcher.runSync("updateTrainingPartyAttendance", context);
				if(ServiceUtil.isSuccess(resultServices)){
					totalSuccess++;
				}
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		if(totalSuccess == 0){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "ApprovalTrainingRegisterFail", locale));
			return "error";
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRTrainingUiLabels", "ApprovalTrainingRegisterSuccess",
				UtilMisc.toMap("totalSuccess", totalSuccess, "total", partyJsonArr.size()), locale));
		return "success";
	}
}
