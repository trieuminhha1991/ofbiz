package com.olbius.basehr.recruitment.events;

import java.sql.Date;
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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.recruitment.helper.RecruitmentHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;

import java.sql.Timestamp;

public class RecruitmentEvents {
	public static String createHRPlanning(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String approverPartyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String yearCustomTimePeriodId = request.getParameter("customTimePeriodId");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String yearRequest=request.getParameter("year");
		String customTimePeriodId=request.getParameter("customTimePeriodId");

		try {
			//Creat Row and Year New And Creat Month
			if(yearCustomTimePeriodId.equals("L"))
			{
				yearCustomTimePeriodId=delegator.getNextSeqId("CustomTimePeriod");
				String periodName="Năm "+yearRequest;
				String fromDateNewYearString=yearRequest+"-01-01";
				String thruDateNewYearString=yearRequest+"-12-31";
				Date fromDateNewYear=Date.valueOf(fromDateNewYearString);
				Date thruDateNewYear=Date.valueOf(thruDateNewYearString);
				GenericValue CustomTimePeriod = delegator.makeValue("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", yearCustomTimePeriodId, "periodTypeId","YEARLY_HRPLAN","periodName",periodName,"fromDate",fromDateNewYear,"thruDate",thruDateNewYear,"isClosed","N"));
				delegator.create(CustomTimePeriod);
				for(int i=1;i<=12;i++)
				{
					String customTimePeriodIdMonth=delegator.getNextSeqId("CustomTimePeriod");
					String parentPeriodIdMonth=yearCustomTimePeriodId;
					String periodTypeIdMonth="MONTHLY_HRPLAN";
					String periodNameMonth="Tháng "+i;
					Date fromDateMonth,thruDateMonth;
					if(i<10) {
						 String fromDateMonthString = yearRequest +"-0"+i+"-01";
						 fromDateMonth=Date.valueOf(fromDateMonthString);
						 String thruDateMonthString = yearRequest +"-0"+i+"-31";
						 thruDateMonth=Date.valueOf(thruDateMonthString);
					}
					else{
						 String fromDateMonthString = yearRequest +"-"+i+"-01";
						 fromDateMonth=Date.valueOf(fromDateMonthString);
						 String thruDateMonthString = yearRequest +"-"+i+"-31";
						 thruDateMonth=Date.valueOf(thruDateMonthString);
					}
					GenericValue CustomTimePeriodMonth = delegator.makeValue("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodIdMonth,"parentPeriodId",parentPeriodIdMonth, "periodTypeId",periodTypeIdMonth,"periodName",periodNameMonth,"fromDate",fromDateMonth,"thruDate",thruDateMonth,"isClosed","N"));
					delegator.create(CustomTimePeriodMonth);
				}
			}
			//Finish Creat Row and Year New And Creat Month
			GenericValue yearCustomTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", yearCustomTimePeriodId), false);
			if(yearCustomTimePeriod == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "HRPlanningCustomTimePeriodNotFound", locale));
				return "error";
			}
			Calendar cal = Calendar.getInstance();
			Date fromDate = yearCustomTimePeriod.getDate("fromDate");
			cal.setTime(fromDate);
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("emplPositionTypeId", emplPositionTypeId);
			context.put("approvedPartyId", approverPartyId);
			List<EntityCondition> customTimePeriodConds = FastList.newInstance();
			customTimePeriodConds.add(EntityCondition.makeCondition("parentPeriodId", yearCustomTimePeriodId));
			customTimePeriodConds.add(EntityCondition.makeCondition("periodTypeId", "MONTHLY_HRPLAN"));
			EntityCondition commonCustomPeriodConds = EntityCondition.makeCondition(customTimePeriodConds);
			Map<String, Object> resultService = null;
			for(int i = 1; i <= 12; i++){
				String quantityStr = request.getParameter("month" + i);
				if(quantityStr != null){
					BigDecimal quantity = new BigDecimal(quantityStr);
//					if(quantity.compareTo(BigDecimal.ZERO) > 0){
						cal.set(Calendar.MONTH, i - 1);
						int year = cal.get(Calendar.YEAR);
						int month = i;
						int startDay = 1;
						int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						Date tempFromDate = Date.valueOf(String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(startDay));
						Date tempThruDate = Date.valueOf(String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(endDay));
						List<GenericValue> monthCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(commonCustomPeriodConds,
								EntityJoinOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(tempFromDate, tempThruDate))),
								null, null, null, false);
						if(UtilValidate.isNotEmpty(monthCustomTimePeriod)){
							context.put("customTimePeriodId", monthCustomTimePeriod.get(0).get("customTimePeriodId"));
							context.put("quantity", quantity);
							resultService = dispatcher.runSync("createHRPlanning", context);
							if(!ServiceUtil.isSuccess(resultService)){
								request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
								request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
								return "error";
							}
						}
//					}
				}
			}
			String action = "ViewListHRPlanning";
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			String header = UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "ApprovalHRPlanningInYear",
					UtilMisc.toMap("yearCustomTimePeriod", yearCustomTimePeriod.get("periodName"), "emplPositionType", emplPositionType.getString("description")), locale);
			CommonUtil.sendNotify(dispatcher, locale, approverPartyId, userLogin, header, action, null);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	public static String createRecruitmentAnticipate(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentAnticipate", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createRecruitmentAnticipate", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			context.clear();
			String recruitAnticipateId = (String)resultService.get("recruitAnticipateId");
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("recruitAnticipateId", recruitAnticipateId);
			for(int i = 1; i <= 12; i++){
				String quantityStr = request.getParameter("month" + i);
				if(quantityStr != null){
					long recruitAnticipateSeqId = (long)(i - 1);
					BigDecimal quantity = new BigDecimal(quantityStr);
					context.put("quantity", quantity);
					context.put("recruitAnticipateSeqId", recruitAnticipateSeqId);
					resultService = dispatcher.runSync("createRecruitmentAnticipateItem", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						return "error";
					}
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
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
	
	public static String approvalRecruitmentAnticipate(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String apprRecruitAnticipateItemListParam = request.getParameter("apprRecruitAnticipateItemList");
		String recruitAnticipateId = request.getParameter("recruitAnticipateId");
		if(apprRecruitAnticipateItemListParam == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NoRowSelected", locale));
			return "error";
		}
		JSONArray apprRecruitAnticipateItemListJson = JSONArray.fromObject(apprRecruitAnticipateItemListParam);
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("recruitAnticipateId", recruitAnticipateId);
		Map<String, Object> resultService = null;
		try {
			try {
				GenericValue recruitmentAnticipate = delegator.findOne("RecruitmentAnticipate", UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId), false);
				if(recruitmentAnticipate == null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "not found record to update", locale));
					return "error";
				}
				TransactionUtil.begin();
				for(int i = 0; i < apprRecruitAnticipateItemListJson.size(); i++){
					JSONObject apprRecruitAnticipateItemJson = apprRecruitAnticipateItemListJson.getJSONObject(i);
					Long recruitAnticipateSeqId = apprRecruitAnticipateItemJson.getLong("recruitAnticipateSeqId");
					context.put("recruitAnticipateSeqId", recruitAnticipateSeqId);
					context.put("changeReason", apprRecruitAnticipateItemJson.has("changeReason")? apprRecruitAnticipateItemJson.get("changeReason") : null);
					context.put("statusId", apprRecruitAnticipateItemJson.getString("statusId"));
					resultService = dispatcher.runSync("approvalRecruitmentAnticipateItem", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				String currStatusId = recruitmentAnticipate.getString("statusId");
				List<GenericValue> recruitmentAnticipateNotAppr = delegator.findByAnd("RecruitmentAnticipateItem", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "statusId", currStatusId), 
						UtilMisc.toList("recruitAnticipateSeqId"), false);
				if(UtilValidate.isNotEmpty(recruitmentAnticipateNotAppr)){
					List<Long> monthNotAppr = EntityUtil.getFieldListFromEntityList(recruitmentAnticipateNotAppr, "recruitAnticipateSeqId", true);
					List<String> monthNotApprStr = FastList.newInstance();
					for(int i = 0; i < monthNotAppr.size(); i++){
						monthNotApprStr.add(String.valueOf(monthNotAppr.get(i) + 1));
					}
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentAnticipateItemNotAppr", 
							UtilMisc.toMap("monthNotAppr", StringUtils.join(monthNotApprStr, ", ")), locale));
					TransactionUtil.rollback();
					return "error";
				}
				resultService = dispatcher.runSync("getRecruitmentAnticipateStatusAfterAppr", 
						UtilMisc.toMap("recruitAnticipateId", recruitAnticipateId, "userLogin", userLogin, "locale", locale));
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String newStatusId = (String)resultService.get("newStatusId");
				if(newStatusId != null && !newStatusId.equals(currStatusId)){
					context.clear();
					context.put("recruitAnticipateId", recruitAnticipateId);
					context.put("statusId", newStatusId);
					context.put("userLogin", userLogin);
					context.put("locale", locale);
					resultService = dispatcher.runSync("updateRecruitmentAnticipate", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
					resultService = dispatcher.runSync("sendNotifyApprRecruitmentAnticipate", context);
				}
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", recruitmentAnticipate.get("emplPositionTypeId")), false);
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", recruitmentAnticipate.get("partyId")), false);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "ApprovalRecruitmentAnticipateSuccess",
						UtilMisc.toMap("emplPositionType", emplPositionType.get("description"), 
										"groupName", partyGroup.get("groupName"), 
										"year",  recruitmentAnticipate.get("year")), locale));
				TransactionUtil.commit();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e1.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createRecruitmentRequireAndCond(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String quantityStr = request.getParameter("quantity");
		Integer quantityAppr=0;
		String quantityUnplannedStr = request.getParameter("quantityUnplanned");
		String isUnplanned = request.getParameter("isUnplanned");
		try {
			TransactionUtil.begin();
			try {
				if(quantityStr != null){
					paramMap.put("quantity", new BigDecimal(quantityStr));
					quantityAppr=Integer.parseInt(quantityStr);
				}
				if("Y".equals(isUnplanned) && quantityUnplannedStr != null){
					paramMap.put("quantityUnplanned", new BigDecimal(quantityUnplannedStr));
					paramMap.put("enumRecruitReqTypeId", "RECRUIT_REQUIRE_UNPLANNED");
					quantityAppr=quantityAppr+Integer.parseInt(quantityUnplannedStr);
				}else{
					paramMap.put("enumRecruitReqTypeId", "RECRUIT_REQUIRE_PLANNED");
				}
				paramMap.put("quantityAppr", new BigDecimal(quantityAppr));
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentRequire", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				context.put("locale", locale);
				context.put("comment", request.getParameter("comment"));
				Map<String, Object> resultService = dispatcher.runSync("createRecruitmentRequire", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String recruitmentRequireId = (String)resultService.get("recruitmentRequireId");
				String recruitReqCondParam = request.getParameter("recruitReqCond");
				if(recruitReqCondParam != null){
					Map<String, Object> recruitmentRequirementMap = FastMap.newInstance();
					recruitmentRequirementMap.put("recruitmentRequireId", recruitmentRequireId);
					resultService = RecruitmentHelper.createRecruitmentRequirementFromJson(dispatcher, userLogin, "createRecruitmentRequireConds", recruitmentRequirementMap, recruitReqCondParam);
					if(resultService.get("isError") != null && (boolean)resultService.get("isError")){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CreateRecruitmentRequireSuccessfully", locale));
			} catch (GeneralServiceException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String updateRecruitmentRequireAndCond(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String recruitmentRequireId = request.getParameter("recruitmentRequireId");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String quantityStr = request.getParameter("quantity");
		String quantityUnplannedStr = request.getParameter("quantityUnplanned");
		try {
			TransactionUtil.begin();
			try {
				if(quantityStr != null && quantityStr.length() > 0){
					paramMap.put("quantity", new BigDecimal(quantityStr));
				}
				if(quantityUnplannedStr != null && quantityUnplannedStr.length() > 0){
					paramMap.put("quantityUnplanned", new BigDecimal(quantityUnplannedStr));
				}
				Map<String, Object> context;
				context = ServiceUtil.setServiceFields(dispatcher, "updateRecruitmentRequire", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				context.put("locale", locale);
				context.put("comment", request.getParameter("comment"));
				context.put("changeReason", request.getParameter("changeReason"));
				Map<String, Object> resultService = dispatcher.runSync("updateRecruitmentRequire", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				resultService = dispatcher.runSync("deleteRecruitmentRequireConds", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId, "userLogin", userLogin));
				String recruitReqCondParam = request.getParameter("recruitReqCond");
				if(recruitReqCondParam != null){
					Map<String, Object> recruitmentRequirementMap = FastMap.newInstance();
					recruitmentRequirementMap.put("recruitmentRequireId", recruitmentRequireId);
					resultService = RecruitmentHelper.createRecruitmentRequirementFromJson(dispatcher, userLogin, "createRecruitmentRequireConds", recruitmentRequirementMap, recruitReqCondParam);
					if(resultService.get("isError") != null && (boolean)resultService.get("isError")){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
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
			
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getRecruitmentCostCategoryType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> recruitmentCostCategoryTypeList = delegator.findList("RecruitmentCostCategoryType", null, UtilMisc.toSet("recruitCostCatTypeId", "recruitCostCatName"), 
					UtilMisc.toList("recruitCostCatName"), null, false);
			request.setAttribute("listReturn", recruitmentCostCategoryTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getRecruitmentCostItemType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> recruitCostItemAndCatTypeList = delegator.findList("RecruitmentCostItemAndCatType", null, 
					UtilMisc.toSet("recruitCostItemTypeId", "recruitCostItemName", "recruitCostCatName", "recruitCostCatTypeId"), 
					UtilMisc.toList("recruitCostItemName"), null, false);
			request.setAttribute("listReturn", recruitCostItemAndCatTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createRecruitmentPlan(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String recruitmentRequireId = request.getParameter("recruitmentRequireId");
		String salaryAmountStr = request.getParameter("salaryAmount");
		String estimatedCostStr = request.getParameter("estimatedCost");
		String quantityStr = request.getParameter("quantity");
		String recruitmentFromDateStr = request.getParameter("recruitmentFromDate");  
		String recruitmentThruDateStr = request.getParameter("recruitmentThruDate");
		String applyFromDateStr = request.getParameter("applyFromDate");  
		String applyThruDateStr = request.getParameter("applyThruDate");
		if(salaryAmountStr != null){
			paramMap.put("salaryAmount", new BigDecimal(salaryAmountStr));
		}
		if(estimatedCostStr != null){
			paramMap.put("estimatedCost", new BigDecimal(estimatedCostStr));
		}
		if(quantityStr != null){
			paramMap.put("quantity", new BigDecimal(quantityStr));
		}
		if(recruitmentFromDateStr != null){
			paramMap.put("recruitmentFromDate", new Timestamp(Long.parseLong(recruitmentFromDateStr)));
		}
		if(recruitmentThruDateStr != null){
			Timestamp recruitmentThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(recruitmentThruDateStr)));
			paramMap.put("recruitmentThruDate", recruitmentThruDate);
		}
		if(applyFromDateStr != null){
			paramMap.put("applyFromDate", new Timestamp(Long.parseLong(applyFromDateStr)));
		}
		if(applyThruDateStr != null){
			Timestamp applyThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(applyThruDateStr)));
			paramMap.put("applyThruDate", applyThruDate);
		}
		try {
			TransactionUtil.begin();
			Map<String, Object> context;
			try {
				if(recruitmentRequireId != null){
					context = FastMap.newInstance();
					GenericValue recruitmentRequire = delegator.findOne("RecruitmentRequire", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), false);
					context = recruitmentRequire.getFields(UtilMisc.toList("emplPositionTypeId", "partyId", "month", "year"));
					context.put("recruitmentRequireId", recruitmentRequireId);
					context.put("applyFromDate", paramMap.get("applyFromDate"));
					context.put("applyThruDate", paramMap.get("applyThruDate"));
					context.put("recruitmentFromDate", paramMap.get("recruitmentFromDate"));
					context.put("recruitmentThruDate", paramMap.get("recruitmentThruDate"));
					BigDecimal quantityPlanned = recruitmentRequire.getBigDecimal("quantity");
					BigDecimal quantityUnplanned = recruitmentRequire.getBigDecimal("quantityUnplanned");
					BigDecimal quantity = BigDecimal.ZERO;
					if(quantityPlanned != null){
						quantity = quantity.add(quantityPlanned);
					}
					if(quantityUnplanned != null){
						quantity = quantity.add(quantityUnplanned);
					}
					context.put("quantity", quantity);
					String emplPositionTypeId = recruitmentRequire.getString("emplPositionTypeId");
					GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
					context.put("recruitmentPlanName", UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentPlanNameBaseRequire", 
							UtilMisc.toMap("emplPositionType", emplPositionType.get("description")), locale));
				}else{
					context = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentPlan", paramMap, userLogin, timeZone, locale);
				}
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				context.put("userLogin", userLogin);
				Map<String, Object> resultService = dispatcher.runSync("createRecruitmentPlan", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String recruitmentPlanId = (String)resultService.get("recruitmentPlanId");
				String recruitmentBoardListParam = request.getParameter("recruitmentBoardList");
				if(recruitmentBoardListParam != null){
					JSONArray recruitmentBoardListJson = JSONArray.fromObject(recruitmentBoardListParam);
					Map<String, Object> recruitmentBoardMap = FastMap.newInstance();
					recruitmentBoardMap.put("recruitmentPlanId", recruitmentPlanId);
					recruitmentBoardMap.put("userLogin", userLogin);
					for(int i = 0; i < recruitmentBoardListJson.size(); i++){
						JSONObject recruitmentBoardJson = recruitmentBoardListJson.getJSONObject(i);
						recruitmentBoardMap.put("partyId", recruitmentBoardJson.getString("partyId"));
						recruitmentBoardMap.put("jobTitle", recruitmentBoardJson.getString("jobTitle"));
						recruitmentBoardMap.put("roleDescription", recruitmentBoardJson.getString("roleDescription"));
						resultService = dispatcher.runSync("createRecruitmentPlanBoard", recruitmentBoardMap);
						if(!ServiceUtil.isSuccess(resultService)){
							TransactionUtil.rollback();
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
					}
				}
				String recruitmentCostListParam = request.getParameter("recruitmentCostList");
				if(recruitmentCostListParam != null){
					JSONArray recruitmentCostListJson = JSONArray.fromObject(recruitmentCostListParam);
					Map<String, Object> recruitmentCostMap = FastMap.newInstance();
					recruitmentCostMap.put("recruitmentPlanId", recruitmentPlanId);
					recruitmentCostMap.put("userLogin", userLogin);
					for(int i = 0; i < recruitmentCostListJson.size(); i++){
						JSONObject recruitmentCostJson = recruitmentCostListJson.getJSONObject(i);
						String amountStr = recruitmentCostJson.getString("amount");
						recruitmentCostMap.put("recruitCostItemTypeId", recruitmentCostJson.getString("recruitCostItemTypeId"));
						recruitmentCostMap.put("amount", new BigDecimal(amountStr));
						recruitmentCostMap.put("comment", recruitmentCostJson.getString("comment"));
						resultService = dispatcher.runSync("createRecruitmentPlanCostItem", recruitmentCostMap);
						if(!ServiceUtil.isSuccess(resultService)){
							TransactionUtil.rollback();
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
					}
				}
				String roundListParam = request.getParameter("roundList");
				if(roundListParam != null){
					String roundSubjectListParam = request.getParameter("roundSubjectList");
					String roundInterviewerListParam = request.getParameter("roundInterviewerList");
					JSONObject roundSubjectListJson = JSONObject.fromObject(roundSubjectListParam);
					JSONObject roundInterviewerListJson = JSONObject.fromObject(roundInterviewerListParam);
					JSONArray roundListJson = JSONArray.fromObject(roundListParam);
					Map<String, Object> recruitmentRoundMap = FastMap.newInstance();
					recruitmentRoundMap.put("recruitmentPlanId", recruitmentPlanId);
					recruitmentRoundMap.put("userLogin", userLogin);
					 
					for(int i = 0; i < roundListJson.size(); i++){
						JSONObject roundJson = roundListJson.getJSONObject(i);
						String roundOrder = roundJson.getString("roundOrder");
						String enumRoundTypeId = roundJson.has("enumRoundTypeId")? roundJson.getString("enumRoundTypeId") : null;
						Long roundOrderNbr = Long.parseLong(roundOrder);
						recruitmentRoundMap.put("roundOrder", roundOrderNbr);
						recruitmentRoundMap.put("roundName", roundJson.get("roundName"));
						recruitmentRoundMap.put("comment", roundJson.has("comment")? roundJson.get("comment") : null);
						recruitmentRoundMap.put("enumRoundTypeId", enumRoundTypeId);
						resultService = dispatcher.runSync("createRecruitmentPlanRound", recruitmentRoundMap);
						if(!ServiceUtil.isSuccess(resultService)){
							TransactionUtil.rollback();
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
						if(roundSubjectListJson.has(roundOrder) && "RECRUIT_ROUND_TEST".equals(enumRoundTypeId)){
							JSONArray roundSubjectArrJson = roundSubjectListJson.getJSONArray(roundOrder); 
							Map<String, Object> recruitPlanRoundSubjMap = FastMap.newInstance();
							recruitPlanRoundSubjMap.put("roundOrder", roundOrderNbr);
							recruitPlanRoundSubjMap.put("recruitmentPlanId", recruitmentPlanId);
							recruitPlanRoundSubjMap.put("userLogin", userLogin);
							for(int j = 0; j < roundSubjectArrJson.size(); j++){
								JSONObject roundSubjectJson = roundSubjectArrJson.getJSONObject(j);
								recruitPlanRoundSubjMap.put("subjectId", roundSubjectJson.getString("subjectId"));
								recruitPlanRoundSubjMap.put("ratio", roundSubjectJson.getDouble("ratio"));
								resultService = dispatcher.runSync("createRecruitmentPlanRoundSubject", recruitPlanRoundSubjMap);
								if(!ServiceUtil.isSuccess(resultService)){
									TransactionUtil.rollback();
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
									return "error";
								}
							}
						}
						if(roundInterviewerListJson.has(roundOrder)){
							JSONArray roundInterviewerArrJson = roundInterviewerListJson.getJSONArray(roundOrder);
							Map<String, Object> roundInterviewerMap = FastMap.newInstance();
							roundInterviewerMap.put("roundOrder", roundOrderNbr);
							roundInterviewerMap.put("recruitmentPlanId", recruitmentPlanId);
							roundInterviewerMap.put("userLogin", userLogin);
							roundInterviewerMap.put("locale", locale);
							for(int j = 0; j < roundInterviewerArrJson.size(); j++){
								String interviewerId = roundInterviewerArrJson.getString(j);
								roundInterviewerMap.put("interviewerId", interviewerId);
								resultService = dispatcher.runSync("createRecruitmentPlanRoundInterviewer", roundInterviewerMap);
								if(!ServiceUtil.isSuccess(resultService)){
									TransactionUtil.rollback();
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
									return "error";
								}
							}
						}
					}
				}
				if(recruitmentRequireId == null){
					String recruitReqCondParam = request.getParameter("recruitReqCond");
					if(recruitReqCondParam != null){
						Map<String, Object> recruitmentPlanCondMap = FastMap.newInstance();
						recruitmentPlanCondMap.put("recruitmentPlanId", recruitmentPlanId);
						resultService = RecruitmentHelper.createRecruitmentRequirementFromJson(dispatcher, userLogin, "createRecruitmentPlanConds", recruitmentPlanCondMap, recruitReqCondParam);
						if(resultService.get("isError") != null && (boolean)resultService.get("isError")){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}else{
					List<GenericValue> recruitmentReqCond = delegator.findByAnd("RecruitmentRequireCond", UtilMisc.toMap("recruitmentRequireId", recruitmentRequireId), null, false);
					context.clear();
					context.put("userLogin", userLogin);
					context.put("recruitmentPlanId", recruitmentPlanId);
					context.put("locale", locale);
					for(GenericValue tempGv: recruitmentReqCond){
						context.putAll(tempGv.getFields(UtilMisc.toList("recruitmentReqCondTypeId", "conditionDesc")));
						resultService = dispatcher.runSync("createRecruitmentPlanConds", context);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
				TransactionUtil.commit();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			} catch (GenericEntityException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
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
	
	public static String createNewRecruitmentCandidate(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		String candidateId = request.getParameter("candidateId");
		String roundOrderStr = request.getParameter("roundOrder");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		try {
			if(!CommonUtil.checkValidStringId(candidateId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyContainInvalidChar", locale));
				return "error";
			}
			List<GenericValue> partyCandidateAtt = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("attrName", "REC_CANDIDATE_ID", "attrValue", candidateId), null, false);
			if(UtilValidate.isNotEmpty(partyCandidateAtt)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIdExists", UtilMisc.toMap("candidateId", candidateId), locale));
				return "error";
			}
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			String birthDateStr = request.getParameter("birthDate");
			if(birthDateStr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				paramMap.put("birthDate", birthDate);
			}
			if(request.getParameter("idIssueDate") != null){
				paramMap.put("idIssueDate", new Date(Long.parseLong(request.getParameter("idIssueDate"))));
			}
			
			String dateReceiveApplyStr = request.getParameter("dateReceiveApply");
			if(dateReceiveApplyStr != null){
				paramMap.put("dateReceiveApply", new Timestamp(Long.parseLong(dateReceiveApplyStr)));
			}
			
			try {
				TransactionUtil.begin();
				Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPerson", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				Map<String, Object> resultService = dispatcher.runSync("createPerson", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String partyId = (String)resultService.get("partyId");
				resultService = dispatcher.runSync("createPartyAttribute", 
						UtilMisc.toMap("partyId", partyId, "attrName", "REC_CANDIDATE_ID", "attrValue", candidateId, "userLogin", userLogin));
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String permanentRes = request.getParameter("permanentRes");
				if(permanentRes != null){
					JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
					Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
					if(permanentResMap != null){
						permanentResMap.put("userLogin", userLogin);
						permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
						permanentResMap.put("postalCode", "10000");
						permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
						permanentResMap.put("partyId", partyId);
						dispatcher.runSync("createPartyPostalAddress", permanentResMap);
					}
				}
				String currRes = request.getParameter("permanentRes");
				if(currRes != null){
					JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
					Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
					if(currResMap != null){
						currResMap.put("userLogin", userLogin);
						currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
						currResMap.put("postalCode", "10000");
						currResMap.put("city", currResMap.get("stateProvinceGeoId"));
						currResMap.put("partyId", partyId);
						dispatcher.runSync("createPartyPostalAddress", currResMap);
					}
				}
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentCandidate", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				ctxMap.put("partyId", partyId);
				resultService = dispatcher.runSync("createRecruitmentCandidate", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				if(roundOrderStr != null){
					Long roundOrder = Long.parseLong(roundOrderStr);
					ctxMap.clear();
					ctxMap.put("userLogin", userLogin);
					ctxMap.put("roundOrder", roundOrder);
					ctxMap.put("recruitmentPlanId", recruitmentPlanId);
					ctxMap.put("partyId", partyId);
					ctxMap.put("locale", locale);
					resultService = dispatcher.runSync("createRecruitmentRoundCandidate", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));;
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getListRecruitmentRound(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		if(recruitmentPlanId == null){
			request.setAttribute("listReturn", FastList.newInstance());
			return "success";
		}
		try {
			List<GenericValue> listRecruitmentRound = delegator.findList("RecruitmentPlanRound", EntityCondition.makeCondition("recruitmentPlanId", recruitmentPlanId), 
					UtilMisc.toSet("recruitmentPlanId", "roundOrder", "roundName", "enumRoundTypeId"), UtilMisc.toList("roundOrder"), null, false);
			if(listRecruitmentRound.size() > 1){
				listRecruitmentRound.add(listRecruitmentRound.remove(0));
			}
			request.setAttribute("listReturn", listRecruitmentRound);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	public static String getListRecruitRoundPartyEngagedBoard(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		if(recruitmentPlanId == null){
			request.setAttribute("listReturn", FastList.newInstance());
			return "success";
		}
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		EntityCondition conds = EntityCondition.makeCondition(UtilMisc.toMap("interviewerId", partyId, "recruitmentPlanId", recruitmentPlanId));
		try {
			List<GenericValue> listRecruitmentRound = delegator.findList("RecruitmentPlanRoundAndInterviewer", conds, 
					UtilMisc.toSet("roundOrder", "roundName", "enumRoundTypeId"), UtilMisc.toList("roundOrder"), null, false);
			request.setAttribute("listReturn", listRecruitmentRound);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String moveCandidateToFirstRound(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		try {
			List<GenericValue> recruitmentPlanRound = delegator.findByAnd("RecruitmentPlanRound", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId), UtilMisc.toList("roundOrder"), false);
			if(UtilValidate.isEmpty(recruitmentPlanRound)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentRoundNotExists", UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId), locale));
				return "error";
			}
			Long roundOrder = null;
			if(recruitmentPlanRound.size() > 1){
				roundOrder = recruitmentPlanRound.get(1).getLong("roundOrder");
			}else{
				roundOrder = recruitmentPlanRound.get(0).getLong("roundOrder");
			}
			Map<String, Object> context = FastMap.newInstance();
			context.put("roundOrder", roundOrder);
			context.put("partyId", partyId);
			context.put("recruitmentPlanId", recruitmentPlanId);
			context.put("locale", locale);
			context.put("userLogin", userLogin);
			Map<String, Object> resultService =  dispatcher.runSync("createRecruitmentRoundCandidate", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "AddCandidateToRecruitmentRoundSuccess", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String updateRecruitRoundCandidateResult(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String roundOrderStr = request.getParameter("roundOrder");
		String moveNextRound = request.getParameter("moveNextRound");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		if(roundOrderStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "recruitment round is not exists");
			return "error";
		}
		paramMap.put("roundOrder", Long.parseLong(roundOrderStr));
		String dateInterviewStr = request.getParameter("dateInterview");
		if(dateInterviewStr != null){
			Timestamp dateInterview = new Timestamp(Long.parseLong(dateInterviewStr));
			paramMap.put("dateInterview", dateInterview);
		}
		if("Y".equals(moveNextRound)){
			paramMap.put("statusId", "RR_REC_PASSED");
		}else{
			paramMap.put("statusId", "RR_REC_NOTPASSED");
		}
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateRecruitmentRoundCandidate", paramMap, userLogin, timeZone, locale);
				context.put("locale", locale);
				Map<String, Object> resultService = dispatcher.runSync("updateRecruitmentRoundCandidate", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				/*String interviewerParam = request.getParameter("interviewer");
				if(interviewerParam != null){
					JSONArray interviewerJsonArr = JSONArray.fromObject(interviewerParam);
					Map<String, Object> examinerMap = FastMap.newInstance();
					examinerMap.put("partyId", paramMap.get("partyId"));
					examinerMap.put("recruitmentPlanId", paramMap.get("recruitmentPlanId"));
					examinerMap.put("roundOrder", paramMap.get("roundOrder"));
					examinerMap.put("userLogin", userLogin);
					for(int i = 0; i < interviewerJsonArr.size(); i++){
						String examinerId = interviewerJsonArr.getString(i);
						examinerMap.put("examinerId", examinerId);
						resultService = dispatcher.runSync("createRecruitRoundCandidateExaminer", examinerMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
					}
				}*/
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "StoreInterviewSuccess", locale));
				TransactionUtil.commit();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				return "error";
			} catch (GenericServiceException e) {
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				e.printStackTrace();
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String recruitmentScheduleInterviewCandidate(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String roundOrderStr = request.getParameter("roundOrder");
		Long roundOrder = Long.parseLong(roundOrderStr);
		String overlapTimeInterviewStr = request.getParameter("overlapTimeInterview");
		String nbrApplicantForInterviewStr = request.getParameter("nbrApplicantForInterview");
		String timeForInterviewStr = request.getParameter("timeForInterview");
		String isInterviewMorning = request.getParameter("isInterviewMorning");
		String isInterviewAfternoon = request.getParameter("isInterviewAfternoon");
		String interviewMorningToStr = request.getParameter("interviewMorningTo");
		String interviewMorningFromStr = request.getParameter("interviewMorningFrom");
		String interviewAfternoonToStr = request.getParameter("interviewAfternoonTo");
		String interviewAfternoonFromStr = request.getParameter("interviewAfternoonFrom");
		String startTimeInterviewStr = request.getParameter("startTimeInterview");
		int overlapTimeInterviewMinute = Integer.parseInt(overlapTimeInterviewStr);
		int nbrApplicantForInterview = Integer.parseInt(nbrApplicantForInterviewStr);
		int timeForInterview = Integer.parseInt(timeForInterviewStr);
		Calendar interviewMorningFrom = null, interviewMorningTo = null, interviewAfternoonFrom = null, interviewAfternoonTo = null;
		
		if("Y".equals(isInterviewMorning)){
			interviewMorningFrom = Calendar.getInstance();
			interviewMorningTo = Calendar.getInstance();
			interviewMorningFrom.setTimeInMillis(Long.parseLong(interviewMorningFromStr));
			interviewMorningTo.setTimeInMillis(Long.parseLong(interviewMorningToStr));
		}
		if("Y".equals(isInterviewAfternoon)){
			interviewAfternoonFrom = Calendar.getInstance();
			interviewAfternoonTo = Calendar.getInstance();
			interviewAfternoonFrom.setTimeInMillis(Long.parseLong(interviewAfternoonFromStr));
			interviewAfternoonTo.setTimeInMillis(Long.parseLong(interviewAfternoonToStr));
		}
		Calendar startTimeInterview = Calendar.getInstance();
		startTimeInterview.setTimeInMillis(Long.parseLong(startTimeInterviewStr));
		Map<String, Object> context = FastMap.newInstance();
		context.put("recruitmentPlanId", recruitmentPlanId);
		context.put("roundOrder", roundOrder);
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		Map<String, Object> resultService = null;
		try {
			List<GenericValue> recruitmentRoundCandidateList = delegator.findByAnd("RecruitmentRoundCandidate", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder, "statusId", "RR_RECRUITING"), UtilMisc.toList("interviewOrder"), false);
			Set<String> listCandidateFailSchduleInterview = FastSet.newInstance();
			for(int i = 0, ordering = 0; i < recruitmentRoundCandidateList.size(); i = i + nbrApplicantForInterview, ordering++){
				Timestamp dateInterview = RecruitmentHelper.createDateInterviewForCandidate(startTimeInterview, interviewMorningFrom, interviewMorningTo, 
						interviewAfternoonFrom, interviewAfternoonTo, timeForInterview, overlapTimeInterviewMinute, ordering);
				for(int j = 0; j < nbrApplicantForInterview || (i + j) < recruitmentRoundCandidateList.size(); j++){
					GenericValue recruitmentRoundCandidate = recruitmentRoundCandidateList.get(i + j);
					String partyId = recruitmentRoundCandidate.getString("partyId");
					context.put("partyId", partyId);
					context.put("dateInterview", dateInterview);
					try {
						resultService = dispatcher.runSync("updateRecruitmentRoundCandidate", context);
						if(!ServiceUtil.isSuccess(resultService)){
							listCandidateFailSchduleInterview.add(partyId);
						}
					} catch (GenericServiceException e) {
						e.printStackTrace();
						listCandidateFailSchduleInterview.add(partyId);
					}
				}
			}
			if(UtilValidate.isNotEmpty(listCandidateFailSchduleInterview)){
				List<String> errorScheduleList = FastList.newInstance();
				for(String partyId: listCandidateFailSchduleInterview){
					errorScheduleList.add(PartyUtil.getPersonName(delegator, partyId));
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentScheduleForCandidateFails", 
						UtilMisc.toMap("candidateFailScheduleList", StringUtils.join(errorScheduleList, ", ")), locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "RecruitmentSchedulingInterviewCandidateSuccssfull", locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} 
		return "success";
	}
	public static String recruitmentReceiveCandidate(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String partyCodeTo = request.getParameter("partyCodeTo");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String partyIdTo = request.getParameter("partyIdTo");
		Security security = (Security) request.getAttribute("security");
		try {
			if(!security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", userLogin)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "you don't have permission to do that");
				return "error";
			}
			GenericValue candidateInPassedRound = delegator.findOne("RecruitmentRoundCandidate", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "partyId", partyIdTo, "roundOrder", 0L), false);
			if(candidateInPassedRound == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIsNotPassed", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyIdTo)), locale));
				return "error";
			}
			String statusId = candidateInPassedRound.getString("statusId");
			if(!"RR_REC_RECEIVE".equals(statusId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIsNotInReceiveStatus", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyIdTo)), locale));
				return "error";
			}
			TransactionUtil.begin();
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			Timestamp dateJoinCompany = null;
			if(request.getParameter("dateJoinCompany") != null){
				dateJoinCompany = new Timestamp(Long.parseLong(request.getParameter("dateJoinCompany")));
			}else{
				dateJoinCompany = UtilDateTime.nowTimestamp();
			}
			paramMap.put("dateJoinCompany", dateJoinCompany);
			Date dateParticipateIns = null;
			if(request.getParameter("dateParticipateIns") != null){
				dateParticipateIns = new Date(Long.parseLong(request.getParameter("dateParticipateIns")));
			}
			paramMap.put("dateParticipateIns", dateParticipateIns);
			Timestamp effectiveFromDate = null;
			if(request.getParameter("heathInsuranceFromDate") != null){
				effectiveFromDate = new Timestamp(Long.parseLong(request.getParameter("heathInsuranceFromDate")));
			}
			paramMap.put("heathInsuranceFromDate", effectiveFromDate);
			Timestamp effectiveThruDate = null;
			if(request.getParameter("heathInsuranceThruDate") != null){
				effectiveThruDate = new Timestamp(Long.parseLong(request.getParameter("heathInsuranceThruDate")));
			}
			paramMap.put("heathInsuranceThruDate", effectiveThruDate);
			Map<String, Object> employmentWorkInfo = ServiceUtil.setServiceFields(dispatcher, "createEmploymentWorkInfo", paramMap, userLogin, UtilHttp.getTimeZone(request), locale);
			employmentWorkInfo.put("locale", locale);
			employmentWorkInfo.put("fromDate", dateJoinCompany);
			employmentWorkInfo.put("rateAmount", new BigDecimal(request.getParameter("salaryBaseFlat")));
			if(request.getParameter("insuranceSalary") != null){
				employmentWorkInfo.put("insuranceSalary", new BigDecimal(request.getParameter("insuranceSalary")));
			}
			employmentWorkInfo.put("partyIdTo", partyIdTo);
			Map<String, Object> resultService = dispatcher.runSync("createEmploymentWorkInfo", employmentWorkInfo);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			
			PartyUtil.updatePartyCode(delegator, partyIdTo, partyCodeTo);
			String probationaryDeadLineStr = (String)paramMap.get("probationaryDeadLine");
			if(probationaryDeadLineStr != null){
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyIdTo), false);
				person.put("probationaryDeadLine", new Date(Long.parseLong(probationaryDeadLineStr)));
				person.store();
			}
			//update recruitment status of candidate
			resultService = dispatcher.runSync("updateRecruitmentRoundCandidate", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", 0L, "partyId", partyIdTo, "statusId", "RR_REC_EMPL", "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			TransactionUtil.commit();
			//create userLogin
			String userLoginId = request.getParameter("userLoginId");
			if(userLoginId != null){
				GenericValue checkUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
				if(checkUserLogin != null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExistsParam", UtilMisc.toMap("userLoginId", userLoginId), UtilHttp.getLocale(request)));
					return "error";
				}
				TransactionUtil.begin();
				String lastOrg = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				resultService = dispatcher.runSync("createUserLogin", 
						UtilMisc.toMap("userLoginId", request.getParameter("userLoginId"),
										"enabled", "Y", "currentPassword", request.getParameter("password"),
										"currentPasswordVerify", request.getParameter("password"),
										"requirePasswordChange", "Y",
										"partyId", partyIdTo, "userLogin", userLogin, "locale", locale));
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", request.getParameter("userLoginId")), false);
				userLoginNew.set("lastOrg", lastOrg);
				userLoginNew.store();
				//------
				List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
						UtilMisc.toMap("emplPositionTypeId", request.getParameter("emplPositionTypeId")), null, false);
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				for(GenericValue tempGv: emplPosTypeSecGroupConfig){
					String groupId = tempGv.getString("groupId");
					resultService = dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginId,
																					"groupId", groupId,
																					"fromDate", dateJoinCompany,
																					"organizationId", lastOrg,
																					"userLogin", systemUserLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", 
					"RecruitmentReceiveCandidateSuccessfully", UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyIdTo)), locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
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
	
	public static String getCandidateGeneralInfo(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			Map<String, Object> personInfo = EmployeeHelper.getEmployeeInfo(delegator, partyId);
			if(personInfo != null){
				request.setAttribute("personInfo", personInfo);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getCandidateContactMechs(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Map<String, Object> permanentResInfo;
		try {
			permanentResInfo = EmployeeHelper.getCurrentPartyPostalAddress(delegator, dispatcher, userLogin, partyId, "PERMANENT_RESIDENCE");
			if(permanentResInfo != null){
				request.setAttribute("permanentResInfo", permanentResInfo);
			}
			Map<String, Object> currResInfo = EmployeeHelper.getCurrentPartyPostalAddress(delegator, dispatcher, userLogin, partyId, "CURRENT_RESIDENCE");
			if(currResInfo != null){
				request.setAttribute("currResInfo", currResInfo);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
	
	public static String updateCandidateInfo(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String birthDateStr = request.getParameter("birthDate");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String candidateId = request.getParameter("candidateId");
		if(birthDateStr != null){
			Date birthDate = new Date(Long.parseLong(birthDateStr));
			paramMap.put("birthDate", birthDate);
		}
		if(request.getParameter("idIssueDate") != null){
			paramMap.put("idIssueDate", new Date(Long.parseLong(request.getParameter("idIssueDate"))));
		}
		Map<String, Object> context;
		try {
			if(!CommonUtil.checkValidStringId(candidateId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyContainInvalidChar", locale));
				return "error";
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, partyId));
			conditions.add(EntityCondition.makeCondition("attrName", "REC_CANDIDATE_ID"));
			conditions.add(EntityCondition.makeCondition("attrValue", candidateId));
			List<GenericValue> partyCandidateAtt = delegator.findList("PartyAttribute", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyCandidateAtt)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "CandidateIdExists", UtilMisc.toMap("candidateId", candidateId), locale));
				return "error";
			}
			context = ServiceUtil.setServiceFields(dispatcher, "updatePerson", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePerson", context);
			if(ServiceUtil.isSuccess(resultService)){
				RecruitmentHelper.updateCandidateId(delegator, partyId, candidateId);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "UpdateCandidateInfoSuccessfully", locale));
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
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String updateCandidateContactMechs(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String partyId = request.getParameter("partyId");
		String permanentRes = request.getParameter("permanentRes");
		try {
			if(permanentRes != null){
				JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
				Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
				if(permanentResMap != null){
					String serviceName = "createPartyPostalAddress";
					if(jsonPermanentRes.has("contactMechId")){
						String contactMechId = jsonPermanentRes.getString("contactMechId");
						if(contactMechId != null){
							serviceName = "updatePartyPostalAddress";
							permanentResMap.put("contactMechId", contactMechId);
						}
					}
					permanentResMap.put("userLogin", userLogin);
					permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
					permanentResMap.put("postalCode", "10000");
					permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
					permanentResMap.put("partyId", partyId);
					dispatcher.runSync(serviceName, permanentResMap);
				}
			}
			String currRes = request.getParameter("currRes");
			if(currRes != null){
				JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
				Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
				if(currResMap != null){
					String serviceName = "createPartyPostalAddress";
					if(jsonCurrRes.has("contactMechId")){
						String contactMechId = jsonCurrRes.getString("contactMechId");
						if(contactMechId != null){
							serviceName = "updatePartyPostalAddress";
							currResMap.put("contactMechId", contactMechId);
						}
					}
					currResMap.put("userLogin", userLogin);
					currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
					currResMap.put("postalCode", "10000");
					currResMap.put("city", currResMap.get("stateProvinceGeoId"));
					currResMap.put("partyId", partyId);
					dispatcher.runSync(serviceName, currResMap);
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "UpdateCandidateContactSuccessfully", UtilHttp.getLocale(request)));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getHRPlanningByCustomTimePeriod(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Security security = (Security) request.getAttribute("security");
		if(!security.hasEntityPermission("HR_RECRUITMENT", "_UPDATE", userLogin)){
			request.setAttribute(ModelService.ERROR_MESSAGE, "You don't have permission");
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		try {
			GenericValue humanResourcePlanning = delegator.findOne("HumanResourcePlanning", 
					UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "customTimePeriodId", customTimePeriodId), false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			if(humanResourcePlanning != null){
				request.setAttribute("statusId", humanResourcePlanning.getString("statusId"));
				request.setAttribute("quantity", humanResourcePlanning.get("quantity"));
				request.setAttribute("approvedPartyId", humanResourcePlanning.get("approvedPartyId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getRecruitmentSourceType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> recruitmentSourceTypeList = delegator.findList("RecruitmentSourceType", null, 
					UtilMisc.toSet("recruitSourceTypeId", "recruitSourceName", "comment"), UtilMisc.toList("recruitSourceName"), null, false);
			request.setAttribute("listReturn", recruitmentSourceTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String getRecruitmentChannelType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> recruitmentChannelTypeList = delegator.findList("RecruitmentChannelType", null, 
					UtilMisc.toSet("recruitChannelTypeId", "recruitChannelName", "comment"), UtilMisc.toList("recruitChannelName"), null, false);
			request.setAttribute("listReturn", recruitmentChannelTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String getRecruitmentReqCondTypeList(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		List<GenericValue> recruitmentReqCondTypeList;
		try {
			recruitmentReqCondTypeList = delegator.findList("RecruitmentReqCondType", null, 
					UtilMisc.toSet("recruitmentReqCondTypeId", "recruitmentReqCondTypeName"), UtilMisc.toList("recruitmentReqCondTypeName"), null, false);
			request.setAttribute("listReturn", recruitmentReqCondTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String getListRecruitmentRequireCond(HttpServletRequest request, HttpServletResponse response){
		String recruitmentRequireId = request.getParameter("recruitmentRequireId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try {
			List<GenericValue> listReturn = delegator.findList("RecruitmentRequireCondAndType", 
					EntityCondition.makeCondition("recruitmentRequireId", recruitmentRequireId), null, UtilMisc.toList("recruitmentReqCondTypeName"), null, false);
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getListRecruitmentRoundSubjectParty(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String examinerId = request.getParameter("examinerId");
		String partyId = request.getParameter("partyId");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String roundOrderStr = request.getParameter("roundOrder");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		if(partyId == null || recruitmentPlanId == null || roundOrderStr == null){
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			return "error";
		}
		if(examinerId == null || examinerId.length() <= 0){
			examinerId = userLogin.getString("partyId");
		}
		Long roundOrder = Long.parseLong(roundOrderStr);
		try {
			Map<String, Object> primKeys = UtilMisc.toMap("examinerId", examinerId, "recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder,"partyId", partyId);
			List<GenericValue> listSubject = delegator.findByAnd("RecruitmentPlanRoundAndSubject", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), UtilMisc.toList("subjectName"), false);
			for(GenericValue tempGv: listSubject){
				Map<String, Object> tempMap = tempGv.getFields(UtilMisc.toList("subjectName", "ratio", "subjectId"));
				String subjectId = tempGv.getString("subjectId");
				primKeys.put("subjectId", subjectId);
				GenericValue recruitmentRoundSubjectParty = delegator.findOne("RecruitmentRoundSubjectParty", primKeys, false);
				if(recruitmentRoundSubjectParty != null){
					tempMap.put("point", recruitmentRoundSubjectParty.get("point"));
				}
				listReturn.add(tempMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String getRecruitmentINTVWEvalParty(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String interviewerId = request.getParameter("interviewerId");
		String partyId = request.getParameter("partyId");
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String roundOrderStr = request.getParameter("roundOrder");
		if(interviewerId == null || interviewerId.length() <= 0){
			interviewerId = userLogin.getString("partyId");
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		if(partyId == null || recruitmentPlanId == null || roundOrderStr == null){
			request.setAttribute("listReturn", listReturn);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			return "error";
		}
		Long roundOrder = Long.parseLong(roundOrderStr);
		Map<String, Object> primKeys = UtilMisc.toMap("interviewerId", interviewerId, "recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder,"partyId", partyId);
		try {
			List<GenericValue> recruitmentIntvwStandardEval = delegator.findByAnd("RecruitmentIntvwStandardEval", null, null, false);
			for(GenericValue tempStandardGv: recruitmentIntvwStandardEval){
				Map<String, Object> tempMap = tempStandardGv.getFields(UtilMisc.toList("standardEvalId", "standardEvalName"));
				String standardEvalId = tempStandardGv.getString("standardEvalId");
				primKeys.put("standardEvalId", standardEvalId);
				GenericValue recruitmentRoundIntvwEval = delegator.findOne("RecruitmentRoundIntvwEval", primKeys, false);
				if(recruitmentRoundIntvwEval != null){
					tempMap.put(recruitmentRoundIntvwEval.getString("statusId"), true);
				}
				listReturn.add(tempMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String updateRecruitRoundCandidateExaminer(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String roundOrderStr = request.getParameter("roundOrder");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		if(roundOrderStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find round order to update");
			return "error";
		}
		Long roundOrder = Long.parseLong(roundOrderStr);
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String partyId = request.getParameter("partyId");
		String examinerInterviewerId = userLogin.getString("partyId");
		paramMap.put("roundOrder", roundOrder);
		try {
			TransactionUtil.begin();
			try {
				GenericValue recruitmentPlanRound = delegator.findOne("RecruitmentPlanRound", 
						UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder), false);
				if(recruitmentPlanRound == null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find round order to update");
					return "error";
				}
				
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateRecruitRoundCandidateExaminer", paramMap, userLogin, timeZone, locale);
				context.put("locale", locale);
				context.put("examinerId", examinerInterviewerId);
				Map<String, Object> resultService = dispatcher.runSync("updateRecruitRoundCandidateExaminer", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String enumRoundTypeId = recruitmentPlanRound.getString("enumRoundTypeId");
				Map<String, Object> recruitmentPlanRoundEvalMap = FastMap.newInstance();
				recruitmentPlanRoundEvalMap.put("recruitmentPlanId", recruitmentPlanId);
				recruitmentPlanRoundEvalMap.put("roundOrder", roundOrder);
				recruitmentPlanRoundEvalMap.put("partyId", partyId);
				recruitmentPlanRoundEvalMap.put("userLogin", userLogin);
				recruitmentPlanRoundEvalMap.put("locale", locale);
				if("RECRUIT_ROUND_TEST".equals(enumRoundTypeId)){
					recruitmentPlanRoundEvalMap.put("examinerId", examinerInterviewerId);
					String recruitmentRoundSubjectParam = request.getParameter("recruitmentRoundSubject");
					if(recruitmentRoundSubjectParam != null){
						JSONArray recruitmentRoundSubjectJsonArr = JSONArray.fromObject(recruitmentRoundSubjectParam);
						for(int i = 0; i < recruitmentRoundSubjectJsonArr.size(); i++){
							JSONObject recruitmentRoundSubjectJson = recruitmentRoundSubjectJsonArr.getJSONObject(i);
							String subjectId = recruitmentRoundSubjectJson.getString("subjectId");
							String pointStr = recruitmentRoundSubjectJson.has("point")? recruitmentRoundSubjectJson.getString("point") : null;
							if(pointStr != null){
								Double point = Double.parseDouble(pointStr);
								recruitmentPlanRoundEvalMap.put("subjectId", subjectId);
								recruitmentPlanRoundEvalMap.put("point", point);
								resultService = dispatcher.runSync("updateRecruitmentRoundSubjectParty", recruitmentPlanRoundEvalMap);
							}
						}
					}
				}else if("RECRUIT_ROUND_INTERVIEW".equals(enumRoundTypeId)){
					recruitmentPlanRoundEvalMap.put("interviewerId", examinerInterviewerId);
					String recruitmentIntvwStandardEvalParam = request.getParameter("recruitmentIntvwStandardEval");
					if(recruitmentIntvwStandardEvalParam != null){
						JSONArray recruitmentIntvwStandardEvalJsonArr = JSONArray.fromObject(recruitmentIntvwStandardEvalParam);
						for(int i = 0; i < recruitmentIntvwStandardEvalJsonArr.size(); i++){
							JSONObject recruitmentIntvwStandardEvalJson = recruitmentIntvwStandardEvalJsonArr.getJSONObject(i);
							String standardEvalId = recruitmentIntvwStandardEvalJson.has("standardEvalId")? recruitmentIntvwStandardEvalJson.getString("standardEvalId") : null;
							String statusId = recruitmentIntvwStandardEvalJson.has("statusId")? recruitmentIntvwStandardEvalJson.getString("statusId") : null;
							if(standardEvalId != null && statusId != null){
								recruitmentPlanRoundEvalMap.put("standardEvalId", standardEvalId);
								recruitmentPlanRoundEvalMap.put("statusId", statusId);
								resultService = dispatcher.runSync("updateRecruitmentRoundIntvwEval", recruitmentPlanRoundEvalMap);
								if(!ServiceUtil.isSuccess(resultService)){
									request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
									request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
									TransactionUtil.rollback();
									return "error";
								}
							}
						}
					}
				}else{
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, "Type recruitment round is undefined");
					TransactionUtil.rollback();
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "EvaluationCandidateSuccessful", locale));
				TransactionUtil.commit();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e1.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	public static String getRecruitRoundCandidateExaminer(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String roundOrderStr = request.getParameter("roundOrder");
		if(roundOrderStr == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "not found recruitment round");
			return "error";
		}
		Long roundOrder = Long.parseLong(roundOrderStr);
		String recruitmentPlanId = request.getParameter("recruitmentPlanId");
		String partyId = request.getParameter("partyId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String examinerId = request.getParameter("examinerId");
		if(examinerId == null || examinerId.length() <= 0){
			examinerId = userLogin.getString("partyId");
		}
		try {
			GenericValue recruitRoundCandidateExaminer = delegator.findOne("RecruitRoundCandidateExaminer", 
					UtilMisc.toMap("recruitmentPlanId", recruitmentPlanId, "roundOrder", roundOrder, "partyId", partyId, "examinerId", examinerId), false);
			if(recruitRoundCandidateExaminer != null){
				request.setAttribute("comment", recruitRoundCandidateExaminer.get("comment"));
				request.setAttribute("resultTypeId", recruitRoundCandidateExaminer.get("resultTypeId"));
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
	
	public static String createRecruitmentSalesEmpl(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		String partyCode = request.getParameter("partyCode");
		String userLoginIdNewEmpl = request.getParameter("userLoginId");
		List<GenericValue> partyCheck;
		try {
			try {
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				partyCheck = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
				if(UtilValidate.isNotEmpty(partyCheck)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "PartyHaveExists", UtilMisc.toMap("partyId", partyCode), locale));
					return "error";
				}
				if(userLoginIdNewEmpl != null){
					GenericValue userLoginTest = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginIdNewEmpl), false);
					if(userLoginTest != null){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExists", UtilMisc.toMap("userLoginId", userLoginIdNewEmpl), locale));
						return "error";
					}
				}
				String birthDateStr = request.getParameter("birthDate");
				if(birthDateStr != null){
					Date birthDate = new Date(Long.parseLong(birthDateStr));
					paramMap.put("birthDate", birthDate);
				}
				if(request.getParameter("idIssueDate") != null){
					paramMap.put("idIssueDate", new Date(Long.parseLong(request.getParameter("idIssueDate"))));
				}
				Timestamp startWorkingFromDate = null;
				if(request.getParameter("fromDate") != null){
					startWorkingFromDate = new Timestamp(Long.parseLong(request.getParameter("fromDate")));
				}else{
					startWorkingFromDate = UtilDateTime.nowTimestamp();
				}
				paramMap.put("fromDate", startWorkingFromDate);
				Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPerson", paramMap, systemUserLogin, UtilHttp.getTimeZone(request), locale);
				ctxMap.put("locale", locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createPerson", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String partyId = (String)resultService.get("partyId");
				PartyUtil.updatePartyCode(delegator, partyId, partyCode);
				String permanentRes = request.getParameter("permanentRes");
				if(permanentRes != null){
					JSONObject jsonPermanentRes = JSONObject.fromObject(permanentRes);
					Map<String, Object> permanentResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonPermanentRes);
					if(permanentResMap != null){
						permanentResMap.put("userLogin", systemUserLogin);
						permanentResMap.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
						permanentResMap.put("postalCode", "10000");
						permanentResMap.put("city", permanentResMap.get("stateProvinceGeoId"));
						permanentResMap.put("partyId", partyId);
						dispatcher.runSync("createPartyPostalAddress", permanentResMap);
					}
				}
				String currRes = request.getParameter("permanentRes");
				if(currRes != null){
					JSONObject jsonCurrRes = JSONObject.fromObject(currRes);
					Map<String, Object> currResMap = EmployeeHelper.getPostalAddressMapFromJson(jsonCurrRes);
					if(currResMap != null){
						currResMap.put("userLogin", systemUserLogin);
						currResMap.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
						currResMap.put("postalCode", "10000");
						currResMap.put("city", currResMap.get("stateProvinceGeoId"));
						currResMap.put("partyId", partyId);
						dispatcher.runSync("createPartyPostalAddress", currResMap);
					}
				}
				String primaryPhone = request.getParameter("phoneNumber");
				if(primaryPhone != null){
					Map<String, Object> primaryPhoneMap = FastMap.newInstance();
					primaryPhoneMap.put("userLogin", systemUserLogin);
					primaryPhoneMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
					primaryPhoneMap.put("contactNumber", primaryPhone);
					primaryPhoneMap.put("partyId", partyId);
					dispatcher.runSync("createPartyTelecomNumber", primaryPhoneMap);
				}
				String primaryEmail = request.getParameter("emailAddress");
				if(primaryEmail != null){
					Map<String, Object> primaryEmailMap = FastMap.newInstance();
					primaryEmailMap.put("userLogin", systemUserLogin);
					primaryEmailMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
					primaryEmailMap.put("emailAddress", primaryEmail);
					primaryEmailMap.put("partyId", partyId);
					dispatcher.runSync("createPartyEmailAddress", primaryEmailMap);
				}
				Map<String, Object> employmentWorkInfo = ServiceUtil.setServiceFields(dispatcher, "createEmploymentWorkInfo", paramMap, systemUserLogin, UtilHttp.getTimeZone(request), locale);
				employmentWorkInfo.put("fromDate", startWorkingFromDate);
				employmentWorkInfo.put("orgId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
				employmentWorkInfo.put("rateAmount", new BigDecimal(request.getParameter("salaryBaseFlat")));
				if(request.getParameter("insuranceSalary") != null){
					employmentWorkInfo.put("insuranceSalary", new BigDecimal(request.getParameter("insuranceSalary")));
				}
				employmentWorkInfo.put("partyIdTo", partyId);
				resultService = dispatcher.runSync("createEmploymentWorkInfo", employmentWorkInfo);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				if(userLoginIdNewEmpl != null){
					//create userLogin
					resultService = dispatcher.runSync("createUserLogin", 
							UtilMisc.toMap("userLoginId", paramMap.get("userLoginId"),
									"enabled", "Y", "currentPassword", paramMap.get("password"),
									"currentPasswordVerify", paramMap.get("password"),
									"requirePasswordChange", "Y",
									"partyId", partyId, "userLogin", userLogin));
					if(!ServiceUtil.isSuccess(resultService)){
						TransactionUtil.rollback();
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						return "error";
					}
					//update lastOrg new userLogin
					GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginIdNewEmpl), false);
					userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
					userLoginNew.store();
					//------
					List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
							UtilMisc.toMap("emplPositionTypeId", request.getParameter("emplPositionTypeId")), null, false);
					for(GenericValue tempGv: emplPosTypeSecGroupConfig){
						String groupId = tempGv.getString("groupId");
						dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginIdNewEmpl,
								"groupId", groupId,
								"fromDate", startWorkingFromDate,
								"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
								"userLogin", systemUserLogin));
					}
				}
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createRecruitmentSalesEmpl", paramMap, userLogin, timeZone, locale);
				ctxMap.put("locale", locale);
				ctxMap.put("timeZone", timeZone);
				ctxMap.put("partyGroupId", request.getParameter("partyIdFrom"));
				ctxMap.put("partyId", partyId);
				resultService = dispatcher.runSync("createRecruitmentSalesEmpl", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "AddEmplToRecruitmentSalesSuccess", locale));
				TransactionUtil.commit();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e1.getLocalizedMessage());
		}
		return "success";
	}
	public static String createRecruitmentSalesOfferBySUP(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String recruitmentPlanSalesId = request.getParameter("recruitmentPlanSalesId");
		String partyIdsParam = request.getParameter("partyIds");
		if(partyIdsParam == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRRecruitmentUiLabels", "NoRecruimentSaleOffer", locale));
			return "error";
		}
		JSONArray partyIdsJson = JSONArray.fromObject(partyIdsParam);
		try {
			try {
				GenericValue recruitmentPlanSales = delegator.findOne("RecruitmentPlanSales", UtilMisc.toMap("recruitmentPlanSalesId", recruitmentPlanSalesId), false);
				if(recruitmentPlanSales == null){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, "cannot find recruitment sales plan");
					return "error";
				}
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("recruitmentPlanSalesId", recruitmentPlanSalesId);
				ctxMap.put("statusId", "RECSALES_OFFERED");
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("locale", locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateRecruitmentPlanSales", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				ctxMap.put("statusId", null);
				List<String> partyIdAcceptList = FastList.newInstance();
				for(int i = 0; i < partyIdsJson.size(); i++){
					String partyId = partyIdsJson.getString(i);
					partyIdAcceptList.add(partyId);
					ctxMap.put("partyId", partyId);
					ctxMap.put("approvalType", PropertiesUtil.APPR_ACCEPT);
					resultService = dispatcher.runSync("updateRecruitmentSalesEmpl", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				List<EntityCondition> salesEmplConds = FastList.newInstance();
				salesEmplConds.add(EntityCondition.makeCondition("recruitmentPlanSalesId", recruitmentPlanSalesId));
				if(UtilValidate.isNotEmpty(partyIdAcceptList)){
					salesEmplConds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN, partyIdAcceptList));
				}
				List<GenericValue> recruitmentSalesEmplList = delegator.findList("RecruitmentSalesEmpl", EntityCondition.makeCondition(salesEmplConds), null, null, null, false);
				for(GenericValue rejectEmpl: recruitmentSalesEmplList){
					String partyId = rejectEmpl.getString("partyId");
					ctxMap.put("partyId", partyId);
					ctxMap.put("approvalType", PropertiesUtil.APPR_REJECT);
					resultService = dispatcher.runSync("updateRecruitmentSalesEmpl", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				if(partyIdsJson.size() > 0){
					ctxMap.clear();
					ctxMap.put("userLogin", userLogin);
					ctxMap.put("locale", locale);
					ctxMap.put("customTimePeriodId", recruitmentPlanSales.get("customTimePeriodId"));
					ctxMap.put("partyIdOffer", recruitmentPlanSales.get("partyId"));
					ctxMap.put("quantityOffer", new Long(partyIdsJson.size()));
					resultService = dispatcher.runSync("createRecruitmentSalesOffer", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgproposesuccess", locale));
				TransactionUtil.commit();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
		}
		return "success";
	}
	
	public static String approvalRecruitmentSalesEmpl(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		String recruitmentSalesOfferId = request.getParameter("recruitmentSalesOfferId");
		String partyIdsParam = request.getParameter("partyIds");
		if(partyIdsParam == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "No Employee selected");
			return "error";
		}
		JSONArray partyIdsJson = JSONArray.fromObject(partyIdsParam);
		List<String> partysId = FastList.newInstance();
		for(int i = 0; i < partyIdsJson.size(); i++){
			partysId.add(partyIdsJson.getString(i));
		}
		Map<String, Object> context = FastMap.newInstance();
		context.put("listPartyAccepted", partysId);
		context.put("recruitmentSalesOfferId", recruitmentSalesOfferId);
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("timeZone", timeZone);
		try {
			Map<String, Object> resultService = dispatcher.runSync("approvalRecruitmentSalesEmpl", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
