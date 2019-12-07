package com.olbius.basehr.kpiperfreview.events;

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

import org.apache.commons.lang.StringUtils;
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.*;

import java.math.BigDecimal;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.kpiperfreview.helper.PerfReviewKPIHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.PartyUtil;
public class PerfReviewKPIEvents {
	public static String getPerfCriteriaByType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String perfCriteriaTypeId = request.getParameter("perfCriteriaTypeId");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("perfCriteriaTypeId", perfCriteriaTypeId));
			conditions.add(EntityCondition.makeCondition("statusId", "KPI_ACTIVE"));
			if(emplPositionTypeId != null){
				List<GenericValue> emplPosTypePerfCriList = delegator.findByAnd("EmplPosTypePerfCri", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "statusId", "KPI_ACTIVE"), null, false);
				if(UtilValidate.isNotEmpty(emplPosTypePerfCriList)){
					List<String> criteriaIdList = EntityUtil.getFieldListFromEntityList(emplPosTypePerfCriList, "criteriaId", true);
					conditions.add(EntityCondition.makeCondition("criteriaId", EntityJoinOperator.NOT_IN, criteriaIdList));
				}
			}
			List<GenericValue> perfCriteria = delegator.findList("PerfCriteria", EntityCondition.makeCondition(conditions), 
					UtilMisc.toSet("criteriaId", "criteriaName", "description", "perfCriteriaTypeId", "periodTypeId", "uomId", "target", "statusId"), 
					UtilMisc.toList("criteriaName"), null, false);
			request.setAttribute("listReturn", perfCriteria);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String getPerfCriteriaOfEmplPosType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String perfCriteriaTypeId = request.getParameter("perfCriteriaTypeId");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			conditions.add(EntityCondition.makeCondition("statusId", "KPI_ACTIVE"));
			if(perfCriteriaTypeId != null){
				List<GenericValue> perfCriteria = delegator.findByAnd("PerfCriteria", UtilMisc.toMap("perfCriteriaTypeId", perfCriteriaTypeId, "statusId", "KPI_ACTIVE"), null, false);
				if(UtilValidate.isNotEmpty(perfCriteria)){
					List<String> criteriaIdList = EntityUtil.getFieldListFromEntityList(perfCriteria, "criteriaId", true);
					conditions.add(EntityCondition.makeCondition("criteriaId", EntityJoinOperator.IN, criteriaIdList));
				}else{
					request.setAttribute("listReturn", FastList.newInstance());
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					return "success";
				}
			}
			List<GenericValue> emplPosTypePerfCriList = delegator.findList("EmplPosTypeAndPerfCri", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("criteriaName"), null, false);
			request.setAttribute("listReturn", emplPosTypePerfCriList);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	/*public static String editEmplPosTypePerfCriteria(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		if(emplPositionTypeId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "position type is null");
			return "error";
		}
		String listPerfCriteriaParam = request.getParameter("listPerfCriteria");
		JSONArray listPerfCriteriaJson = JSONArray.fromObject(listPerfCriteriaParam);
		try {
			TransactionUtil.begin();
			Map<String, Object> resultService = null;
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("emplPositionTypeId", emplPositionTypeId);
			for(int i = 0; i < listPerfCriteriaJson.size(); i++){
				String criteriaId = listPerfCriteriaJson.getString(i);
				context.put("criteriaId", criteriaId);
				resultService = dispatcher.runSync("createEmplPosTypePerfCri", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			TransactionUtil.commit();
		} catch (GenericTransactionException e) {
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
	
	public static String removeEmplPosTypePerfCriteria(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		if(emplPositionTypeId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "position type is null");
			return "error";
		}
		String listPerfCriteriaParam = request.getParameter("listPerfCriteria");
		JSONArray listPerfCriteriaJson = JSONArray.fromObject(listPerfCriteriaParam);
		try {
			TransactionUtil.begin();
			Map<String, Object> resultService = null;
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("emplPositionTypeId", emplPositionTypeId);
			for(int i = 0; i < listPerfCriteriaJson.size(); i++){
				String criteriaId = listPerfCriteriaJson.getString(i);
				context.put("criteriaId", criteriaId);
				resultService = dispatcher.runSync("deleteEmplPosTypePerfCri", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			TransactionUtil.commit();
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}*/
	
	public static String addKPIForEmployee(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		//String partyId = request.getParameter("partyId");
		String partyIds = (String)paramMap.get("partyIds");
		JSONArray partyIdJsonArr = JSONArray.fromObject(partyIds);
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		String weightStr = request.getParameter("weight");
		String listProductsParam = request.getParameter("listProducts");
		if(listProductsParam != null){
			JSONArray listProductsJson = JSONArray.fromObject(listProductsParam);
			List<Map<String, Object>> listProductMap = FastList.newInstance();
			for(int i = 0; i < listProductsJson.size(); i++){
				Map<String, Object> tempMap = FastMap.newInstance();
				JSONObject productJson = listProductsJson.getJSONObject(i);
				tempMap.put("productId", productJson.get("productId"));
				tempMap.put("quantityTarget", productJson.has("productId") ? productJson.get("quantityTarget") : null);
				tempMap.put("uomId", productJson.has("uomId") ? productJson.get("uomId") : null);
				listProductMap.add(tempMap);
			}
		}
		if(weightStr != null){
			paramMap.put("weight", new BigDecimal(weightStr));
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPartyPerfCriteria", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			int totalEmpl = partyIdJsonArr.size();
			int successCreate = 0;
			Map<String, Object> resultService = null;
			for(int i = 0; i < totalEmpl; i++){
				String partyId = partyIdJsonArr.getString(i);
				context.put("partyId", partyId);
				resultService = dispatcher.runSync("createPartyPerfCriteria", context);
				if(ServiceUtil.isSuccess(resultService)){
					successCreate++;
				}
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "AddKPIForEmplSuccessful",
							UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId)), locale));
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			//Map<String, Object> resultService = dispatcher.runSync("createPartyPerfCriteria", context);
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String settingKPIForEmplByPosType(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		Locale locale = UtilHttp.getLocale(request);
		if(emplPositionTypeId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NoEmplPositionTypeSelect", locale));
			return "error";
		}
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if(fromDateStr != null){
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
		}else{
			fromDate = UtilDateTime.getDayStart(nowTimestamp);
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		String selectAllParty = request.getParameter("selectAllParty");
		String selectAllKPI = request.getParameter("selectAllKPI");
		List<String> emplList = null;
		List<GenericValue> kpiList = null;
		try {
			if("Y".equals(selectAllParty)){
				emplList = EmployeeHelper.getListEmplHavePositionTypeInPeriod(delegator, emplPositionTypeId, userLogin, fromDate, thruDate);
			}else{
				emplList = FastList.newInstance();
				String partyIdSelectedParam = request.getParameter("partyIdSelected");
				JSONArray partyIdSelectedJson = JSONArray.fromObject(partyIdSelectedParam);
				for(int i = 0; i < partyIdSelectedJson.size(); i++){
					emplList.add(partyIdSelectedJson.getString(i));
				}
			}
			if("Y".equals(selectAllKPI)){
				kpiList = PerfReviewKPIHelper.getListKPIOfEmplPositionType(delegator, emplPositionTypeId);
			}else{
				String listKPISelectedParam = request.getParameter("listKPISelected");
				JSONArray listKPISelected = JSONArray.fromObject(listKPISelectedParam);
				kpiList = FastList.newInstance();
				for(int i = 0; i < listKPISelected.size(); i++){
					JSONObject kpiJson = listKPISelected.getJSONObject(i);
					GenericValue emplPosTypePerfCri = delegator.makeValue("EmplPosTypePerfCri");
					emplPosTypePerfCri.put("criteriaId", kpiJson.getString("criteriaId"));
					emplPosTypePerfCri.put("periodTypeId", kpiJson.getString("periodTypeId"));
					String targetStr = kpiJson.getString("target");
					emplPosTypePerfCri.put("target", new BigDecimal(targetStr));
					emplPosTypePerfCri.put("uomId", kpiJson.getString("uomId"));
					BigDecimal weight = BigDecimal.ZERO;
					if(kpiJson.has("weight")){
						String weightStr = kpiJson.getString("weight");
						weight = new BigDecimal(weightStr);
					}
					emplPosTypePerfCri.put("weight", weight);
					kpiList.add(emplPosTypePerfCri);
				}
			}
			EntityCondition dateConds = EntityConditionUtils.makeDateConds(fromDate, thruDate);
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("fromDate", fromDate);
			context.put("thruDate", thruDate);
			Map<String, Object> resultService = null;
			for(String partyId: emplList){
				EntityCondition partyCommonConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, EntityCondition.makeCondition("partyId", partyId));
				for(GenericValue tempKpi: kpiList){
					String criteriaId = tempKpi.getString("criteriaId");
					EntityCondition checkConds = EntityCondition.makeCondition(partyCommonConds, EntityJoinOperator.AND, EntityCondition.makeCondition("criteriaId", criteriaId));
					List<GenericValue> partyPerfCriteriaList = delegator.findList("PartyPerfCriteria", checkConds, null, null, null, false);
					if(UtilValidate.isEmpty(partyPerfCriteriaList)){
						BigDecimal weight = tempKpi.getBigDecimal("weight");
						if(weight == null){
							weight = BigDecimal.ZERO;
						}
						context.put("partyId", partyId);
						context.put("criteriaId", criteriaId);
						context.put("periodTypeId", tempKpi.get("periodTypeId"));
						context.put("target", tempKpi.get("target"));
						context.put("uomId", tempKpi.get("uomId"));
						context.put("weight", weight);
						resultService = dispatcher.runSync("createPartyPerfCriteria", context);
						if(!ServiceUtil.isSuccess(resultService)){
							GenericValue criteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "SettingKPIForPartyError", 
									UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "criteriaName", criteria.get("criteriaName")), locale));
							return "error";
						}
					}
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "SettingKPIForEmployeeSuccess", locale));
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
	@SuppressWarnings("unchecked")
	public static String expireEmplTypeCriteria(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String data = request.getParameter("dataSubmit");
		JSONArray arr = new JSONArray();
		try {
			if(UtilValidate.isNotEmpty(data)){
				arr = JSONArray.fromObject(data);
			}
			
			if(UtilValidate.isNotEmpty(arr)){
				for(int i=0 ; i<arr.size(); i++){
					JSONObject obj = arr.getJSONObject(i);
					String emplPositionTypeId = obj.getString("emplPositionTypeId");
					String criteriaId = obj.getString("criteriaId");
					GenericValue emplPosTypePerfCri = delegator.findOne("EmplPosTypePerfCri", 
							UtilMisc.toMap("criteriaId", criteriaId, "emplPositionTypeId", emplPositionTypeId), false);
					if(UtilValidate.isNotEmpty(emplPosTypePerfCri)){
						emplPosTypePerfCri.set("statusId", "KPI_INACTIVE");
						emplPosTypePerfCri.store();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	public static String getPerfCriteriaRateGrade(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> perfCriteriaRateGradeList = delegator.findList("PerfCriteriaRateGrade", null, 
					UtilMisc.toSet("perfCriteriaRateGradeId", "fromRating", "toRating", "perfCriteriaRateGradeName"), UtilMisc.toList("-fromRating"), null, false);
			request.setAttribute("listReturn", perfCriteriaRateGradeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	public static String createPerfCriteriaPolicy(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		if(fromDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			paramMap.put("fromDate", fromDate);
		}
		if(thruDateStr != null){
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			paramMap.put("thruDate", thruDate);
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPerfCriteriaPolicy", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("createPerfCriteriaPolicy", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, resultService.get(ModelService.SUCCESS_MESSAGE));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
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
	public static String getKPIAssessmentPeriod(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> periodTypeList = delegator.findList("PeriodType", EntityCondition.makeCondition("groupPeriodTypeId", "KPI_ASSESSMENT_PERIOD"), 
					UtilMisc.toSet("periodTypeId", "description", "periodLength", "uomId"), 
					UtilMisc.toList("description"), null, false);
			request.setAttribute("listReturn", periodTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String createPerfCriteriaAssessment(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		if(fromDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			paramMap.put("fromDate", fromDate);
		}
		if(thruDateStr != null){
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			paramMap.put("thruDate", thruDate);
		}
		try {
			String enumAssessmentTypeId = request.getParameter("enumAssessmentTypeId");
			if("KPI_ASSESS_DISTR".equals(enumAssessmentTypeId)){
				paramMap.put("partyId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
				paramMap.put("periodTypeId", "KPI_MONTH");
			}
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPerfCriteriaAssessment", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createPerfCriteriaAssessment", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("perfCriteriaAssessmentId", resultService.get("perfCriteriaAssessmentId"));
				request.setAttribute(ModelService.SUCCESS_MESSAGE,UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String addPerfCriteriaAssessmentParty(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String partyParam = request.getParameter("partyIds");
		String perfCriteriaAssessmentId = request.getParameter("perfCriteriaAssessmentId");
		if(partyParam != null && perfCriteriaAssessmentId != null){
			JSONArray partyJson = JSONArray.fromObject(partyParam);
			Map<String, Object> context = FastMap.newInstance();
			context.put("perfCriteriaAssessmentId", perfCriteriaAssessmentId);
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("timeZone", UtilHttp.getTimeZone(request));
			Map<String, Object> resultService = null;
			List<String> partyIdAddErr = FastList.newInstance(); 
			for(int i = 0; i < partyJson.size(); i++){
				String partyId = partyJson.getString(i);
				context.put("partyId", partyId);
				try {
					resultService = dispatcher.runSync("createPerfCriteriaAssessmentParty", context);
					if(!ServiceUtil.isSuccess(resultService)){
						partyIdAddErr.add(partyId);
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
					partyIdAddErr.add(partyId);
				}
			}
			try {
				if(UtilValidate.isEmpty(partyIdAddErr)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "AddEmplToPerfCriteriaAssessmentSuccess", locale));
				}else{
					List<String> emplNameList = FastList.newInstance();
					for(String partyIdErr: partyIdAddErr){
						emplNameList.add(PartyUtil.getPersonName(delegator, partyIdErr));
					}
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", 
							"ErrorWhenAddEmplToPerfCriteriaAssessmentSuccess", UtilMisc.toMap("emplNameListErr", StringUtils.join(emplNameList, ", ")), locale));
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "LackInfoAboutAssessmentIdAndParty", locale));
		}
		return "success";
	}
	
	public static String addKPIForEmplPositionType(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String targetStr = request.getParameter("target");
		if(targetStr != null){
			paramMap.put("target", new BigDecimal(targetStr));
		}
		String weightStr = request.getParameter("weight");
		if(weightStr != null){
			paramMap.put("weight", new BigDecimal(weightStr));
		}
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEmplPosTypePerfCri", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultService = dispatcher.runSync("createEmplPosTypePerfCri", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
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
	
	public static String updatePerfCriteriaAssessmentParty(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String salaryRateStr = request.getParameter("salaryRate");
		if(salaryRateStr != null && salaryRateStr.length() > 0){
			paramMap.put("salaryRate", new BigDecimal(salaryRateStr));
		}
		String allowanceRateStr = request.getParameter("allowanceRate");
		if(allowanceRateStr != null && allowanceRateStr.length() > 0){
			paramMap.put("allowanceRate", new BigDecimal(allowanceRateStr));
		}
		String bonusAmountStr = request.getParameter("bonusAmount");
		if(bonusAmountStr != null && bonusAmountStr.length() > 0){
			paramMap.put("bonusAmount", new BigDecimal(bonusAmountStr));
		}
		String punishmentAmountStr = request.getParameter("punishmentAmount");
		if(punishmentAmountStr != null && punishmentAmountStr.length() > 0){
			paramMap.put("punishmentAmount", new BigDecimal(punishmentAmountStr));
		}
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePerfCriteriaAssessmentParty", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultService = dispatcher.runSync("updatePerfCriteriaAssessmentParty", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
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
	public static String getKPIUomList(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> listKPIUom = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", "KPI_MEASURE"), 
					UtilMisc.toSet("uomId", "abbreviation", "description"), UtilMisc.toList("abbreviation"), null, false);
			request.setAttribute("listReturn", listKPIUom);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String createKPIUom(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String abbreviation = request.getParameter("abbreviation");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = FastMap.newInstance();
		context.put("abbreviation", abbreviation);
		context.put("description", abbreviation);
		context.put("uomTypeId", "KPI_MEASURE");
		context.put("locale", locale);
		context.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createUomHR", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String createNewKpiAndSetup(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String setup = (String) request.getParameter("setup");
		JSONArray obj = new JSONArray();
		String criteriaName = request.getParameter("criteriaName");
		String description = request.getParameter("description");
		String perfCriteriaTypeId = request.getParameter("perfCriteriaTypeId");
		String periodTypeId = request.getParameter("periodTypeId");
		String uomId = request.getParameter("uomId");
		String perfCriDevelopmetTypeId = request.getParameter("perfCriDevelopmetTypeId");
		long target_long = (long) Double.parseDouble(request.getParameter("target"));
		BigDecimal target = BigDecimal.valueOf(target_long);
		if(UtilValidate.isNotEmpty(setup)){
			obj = JSONArray.fromObject(setup);
		}
		Map<String, Object> context = FastMap.newInstance();
		context.put("criteriaName", criteriaName);
		context.put("description", description);
		context.put("perfCriteriaTypeId", perfCriteriaTypeId);
		context.put("periodTypeId", periodTypeId);
		context.put("uomId", uomId);
		context.put("target", target);
		context.put("perfCriDevelopmetTypeId", perfCriDevelopmetTypeId);
		context.put("userLogin", userLogin);
		Map<String, Object> resultMap = FastMap.newInstance();
 		try {
			resultMap = dispatcher.runSync("CreateCriteria", context);
			if(!ServiceUtil.isSuccess(resultMap)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultMap));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		
		long fromDate_long = Long.parseLong(request.getParameter("fromDate"));
		Timestamp fromDate = new Timestamp(fromDate_long);
		Timestamp thruDate = null;
		if(UtilValidate.isNotEmpty(request.getParameter("thruDate"))){
			long thruDate_long = Long.parseLong(request.getParameter("thruDate"));
			thruDate = new Timestamp(thruDate_long);
		}
		String criteriaId = (String) resultMap.get("criteriaId");
		String perfCriteriaPolicyId = delegator.getNextSeqId("PerfCriteriaPolicy");
		GenericValue PerfCriteriaPolicy = delegator.makeValue("PerfCriteriaPolicy");
		PerfCriteriaPolicy.set("perfCriteriaPolicyId", perfCriteriaPolicyId);
		PerfCriteriaPolicy.set("criteriaId", criteriaId);
		PerfCriteriaPolicy.set("fromDate", fromDate);
		PerfCriteriaPolicy.set("thruDate", thruDate);
		try {
			PerfCriteriaPolicy.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "error";
		}
		
		if(UtilValidate.isNotEmpty(obj)){
			for(int i = 0 ; i < obj.size() ; i++){
				JSONObject tmp = obj.getJSONObject(i);
				Map<String, Object> map = FastMap.newInstance();
				long fromRating_long = tmp.getLong("fromRating");
				BigDecimal fromRating = BigDecimal.valueOf(fromRating_long);
				String toRating_string = tmp.getString("toRating");
				BigDecimal toRating = null;
				if(!toRating_string.equals("null")){
					Long toRating_long = tmp.getLong("toRating");
					toRating = BigDecimal.valueOf(toRating_long);
				}
				long amount_long = tmp.getLong("amount");
				BigDecimal amount = BigDecimal.valueOf(amount_long);
				map.put("fromRating", fromRating);
				map.put("toRating", toRating);
				map.put("amount", amount);
				map.put("kpiPolicyEnumId", tmp.getString("kpiPolicyEnumId"));
				long criteriaPolSeqId = i + 1;
				map.put("criteriaPolSeqId",criteriaPolSeqId);
				map.put("perfCriteriaPolicyId", perfCriteriaPolicyId);
				
				GenericValue perfCriteriaPolicyItem = delegator.makeValidValue("PerfCriteriaPolicyItem", map);
				try {
					perfCriteriaPolicyItem.create();
				} catch (GenericEntityException e) {
					e.printStackTrace();
					return "error";
				}
			}
		}
		return "success";
	}
	
	public static String updateKpiPolicy(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String perfCriteriaPolicyId = request.getParameter("perfCriteriaPolicyId");
		String setup = request.getParameter("setup");
		Locale locale = UtilHttp.getLocale(request);
		
		try {
			JSONObject obj = new JSONObject();
			if(UtilValidate.isNotEmpty(setup)){
				obj = JSONObject.fromObject(setup);
			}
			if(UtilValidate.isNotEmpty(obj)){
				BigDecimal fromRating = BigDecimal.valueOf((double) obj.getLong("fromRating"));
				BigDecimal toRating = null;
				obj.has("toRating");
				if(obj.has("toRating") && !obj.containsValue(null)){
					toRating = BigDecimal.valueOf((double) obj.getLong("toRating"));
				}
				BigDecimal amount = BigDecimal.valueOf((double) obj.getLong("amount"));
				String kpiEnumId = obj.getString("kpiPolicyEnumId");
				List<GenericValue> listG = delegator.findList("PerfCriteriaPolicyItem",
						EntityCondition.makeCondition("perfCriteriaPolicyId",perfCriteriaPolicyId), null, null, null, false);
				if(UtilValidate.isNotEmpty(listG)){
					for (GenericValue g : listG) {
						BigDecimal x = g.getBigDecimal("fromRating");
						BigDecimal y = g.getBigDecimal("toRating");
						if(UtilValidate.isEmpty(toRating)){
							if(UtilValidate.isEmpty(y)){
								request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
								request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
								return "error";
							}else{
								if(fromRating.compareTo(y) <= 0){
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
									return "error";
								}
							}
						}else{
							if(UtilValidate.isNotEmpty(y)){
								if((fromRating.compareTo(x) >= 0 && fromRating.compareTo(y) <= 0) || (toRating.compareTo(x) >= 0 && toRating.compareTo(y) <= 0)){
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
									return "error";
								}else{
									if(fromRating.compareTo(x) < 0 && toRating.compareTo(x) >= 0){
										request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
										request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
										return "error";
									}
								}
							}else{
								if(toRating.compareTo(x) > 0){
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
									return "error";
								}
							}
						}
					}
					long seqId = (long) listG.size() + 1;
					GenericValue PerfCriteriaPolicyItemNew = delegator.makeValue("PerfCriteriaPolicyItem");
					PerfCriteriaPolicyItemNew.set("perfCriteriaPolicyId", perfCriteriaPolicyId);
					PerfCriteriaPolicyItemNew.set("fromRating", fromRating);
					PerfCriteriaPolicyItemNew.set("toRating", toRating);
					PerfCriteriaPolicyItemNew.set("amount", amount);
					PerfCriteriaPolicyItemNew.set("kpiPolicyEnumId", kpiEnumId);
					PerfCriteriaPolicyItemNew.set("criteriaPolSeqId", seqId);
					PerfCriteriaPolicyItemNew.create();
				}else{
					GenericValue PerfCriteriaPolicyItemNew = delegator.makeValue("PerfCriteriaPolicyItem");
					int seqId = 1;
					PerfCriteriaPolicyItemNew.set("perfCriteriaPolicyId", perfCriteriaPolicyId);
					PerfCriteriaPolicyItemNew.set("fromRating", fromRating);
					PerfCriteriaPolicyItemNew.set("toRating", toRating);
					PerfCriteriaPolicyItemNew.set("amount", amount);
					PerfCriteriaPolicyItemNew.set("kpiPolicyEnumId", kpiEnumId);
					PerfCriteriaPolicyItemNew.set("criteriaPolSeqId", (long) seqId);
					PerfCriteriaPolicyItemNew.create();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	public static String createPolicyKpi(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String perfCriteriaPolicyId = request.getParameter("policyId");
		String criteriaId = request.getParameter("criteriaId");
		long fromDate_long = Long.parseLong(request.getParameter("fromDate"));
		Timestamp fromDate = new Timestamp(fromDate_long);
		Timestamp thruDate = null;
		if(UtilValidate.isNotEmpty(request.getParameter("thruDate"))){
			long thruDate_long = Long.parseLong(request.getParameter("thruDate"));
			thruDate = new Timestamp(thruDate_long);
		}
		try {
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityCondition.makeCondition("criteriaId", criteriaId));
			List<GenericValue> listG = delegator.findList("PerfCriteriaPolicy", 
					EntityCondition.makeCondition(listCond), null, null, null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaPolicyId)){
				GenericValue perfCriteriaPolicy = delegator.findOne("PerfCriteriaPolicy", UtilMisc.toMap("perfCriteriaPolicyId", perfCriteriaPolicyId), false);
				if(UtilValidate.isNotEmpty(perfCriteriaPolicy)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "ExistedPolicyId", locale));
					return "error";
				}else{
					for (GenericValue g : listG) {
						boolean b = PerfReviewKPIHelper.checkSetupKpiPolicy(fromDate, g.getTimestamp("fromDate"), thruDate, g.getTimestamp("thruDate"));
						if(!b){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
							return "error";
						}
					}
					GenericValue g = delegator.makeValue("PerfCriteriaPolicy");
					g.set("perfCriteriaPolicyId", perfCriteriaPolicyId);
					g.set("criteriaId", criteriaId);
					g.set("fromDate", fromDate);
					g.set("thruDate", thruDate);
					g.create();
				}
			}else{
				for (GenericValue g : listG) {
					boolean b = PerfReviewKPIHelper.checkSetupKpiPolicy(fromDate, g.getTimestamp("fromDate"), thruDate, g.getTimestamp("thruDate"));
					if(!b){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
						return "error";
					}
				}
				String id = delegator.getNextSeqId("PerfCriteriaPolicy");
				GenericValue g = delegator.makeValue("PerfCriteriaPolicy");
				g.set("perfCriteriaPolicyId", id);
				g.set("criteriaId", criteriaId);
				g.set("fromDate", fromDate);
				g.set("thruDate", thruDate);
				g.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	public static String editKpiPolicy(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String perfCriteriaPolicyId = request.getParameter("policyId");
		String criteriaId = request.getParameter("criteriaId");
		long fromDate_long = Long.parseLong(request.getParameter("fromDate"));
		Timestamp fromDate = new Timestamp(fromDate_long);
		Timestamp thruDate = null;
		if(UtilValidate.isNotEmpty(request.getParameter("thruDate"))){
			long thruDate_long = Long.parseLong(request.getParameter("thruDate"));
			thruDate = new Timestamp(thruDate_long);
		}
		try {
			GenericValue g = delegator.findOne("PerfCriteriaPolicy", UtilMisc.toMap("perfCriteriaPolicyId", perfCriteriaPolicyId), false);
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityCondition.makeCondition("criteriaId", criteriaId));
			listCond.add(EntityCondition.makeCondition("perfCriteriaPolicyId", EntityJoinOperator.NOT_EQUAL, perfCriteriaPolicyId));
			List<GenericValue> listG = delegator.findList("PerfCriteriaPolicy",
					EntityCondition.makeCondition(listCond), null, null, null, false);
			if(UtilValidate.isNotEmpty(listG)){
				for (GenericValue gtmp : listG) {
					boolean b = PerfReviewKPIHelper.checkSetupKpiPolicy(fromDate, gtmp.getTimestamp("fromDate"), thruDate, gtmp.getTimestamp("thruDate"));
					if(!b){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "HRNotUpdateDate", locale));
						return "error";
					}
				}
			}
			if(UtilValidate.isNotEmpty(g)){
				g.set("fromDate", fromDate);
				g.set("thruDate", thruDate);
				g.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "HRNotEditSuccess", locale));
		return "success";
	}
}
