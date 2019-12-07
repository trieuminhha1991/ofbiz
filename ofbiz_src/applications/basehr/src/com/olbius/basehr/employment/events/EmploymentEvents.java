package com.olbius.basehr.employment.events;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.olbius.basehr.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;

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
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employee.helper.EmployeeHelper;

public class EmploymentEvents {
	@SuppressWarnings("unchecked")
	public static String getPositionByPositionTypeAndParty(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String partyId = (String)parameterMap.get("partyId");
		String emplPositionTypeId = (String)parameterMap.get("emplPositionTypeId");
		String totalRow = "0";
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		if(fromDateStr != null){
			parameterMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		}
		if(thruDateStr != null){
			parameterMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		if(partyId != null && emplPositionTypeId != null){
			try {
				Map<String, Object> resultService = dispatcher.runSync("getPositionByPositionTypeAndParty", ServiceUtil.setServiceFields(dispatcher, "getPositionByPositionTypeAndParty", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
				listReturn = (List<Map<String,Object>>)resultService.get("listReturn");
				totalRow = (String)resultService.get("TotalRows");
			} catch (GeneralServiceException e) {
				e.printStackTrace();
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		request.setAttribute("listReturn", listReturn);
		request.setAttribute("TotalRows", totalRow);
		return "success";
	}
	
	public static String editOrgPostalAddress(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String contactMechId = request.getParameter("contactMechId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("partyId", partyId);
		ctxMap.put("address1", request.getParameter("address1"));
		ctxMap.put("countryGeoId", request.getParameter("countryGeoId"));
		ctxMap.put("postalCode", "10000");
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		ctxMap.put("stateProvinceGeoId", stateProvinceGeoId);
		ctxMap.put("city", stateProvinceGeoId);
		ctxMap.put("districtGeoId", request.getParameter("districtGeoId"));
		ctxMap.put("wardGeoId", request.getParameter("wardGeoId"));
		ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
		Map<String, Object> resultService;
		String contactMechIdEdit = null;
		try {
			if(contactMechId != null){
				ctxMap.put("contactMechId", contactMechId);
				resultService = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
				contactMechIdEdit = (String)resultService.get("contactMechId");
			}else{
				resultService = dispatcher.runSync("createPartyPostalAddress", ctxMap);
				contactMechIdEdit = (String)resultService.get("contactMechId");
			}
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute("contactMechId", contactMechIdEdit);
				String postAddressDesc = CommonUtil.getPostalAddressDetails(delegator, contactMechIdEdit);
				request.setAttribute("postAddressDesc", postAddressDesc);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				return "error";
			}
			
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String updatePartyOrgInfo(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		String partyIdFromNew = request.getParameter("partyIdFromNew");
		String partyIdFromOld = request.getParameter("partyIdFromOld");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		try {
			dispatcher.runSync("updatePartyGroup", ServiceUtil.setServiceFields(dispatcher, "updatePartyGroup", parameterMap, userLogin, timeZone, locale));
			if(partyIdFromOld != null && partyIdFromNew != null){
				if(!partyIdFromOld.equals(partyIdFromNew)){
					GenericValue partyIdFromOldGv = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromOld), false);
					GenericValue partyIdFromNewGv = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromNew), false);
					if(partyIdFromNewGv != null && partyIdFromOldGv != null){
						TransactionUtil.begin();
						//thruDate old relationship						
						List<EntityCondition> conditions = FastList.newInstance();
						conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
						conditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdFromOld));
						conditions.add(EntityUtil.getFilterByDateExpr());
						List<GenericValue> relationshipExpire = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), 
								null, null, null, false);
						GenericValue expireGv = relationshipExpire.get(0);
						expireGv.set("thruDate", UtilDateTime.nowTimestamp());
						expireGv.store();
						//create new relationship
						Map<String, Object> ctxMap = FastMap.newInstance();
						ctxMap.put("partyIdFrom", partyIdFromNew);
						ctxMap.put("partyIdTo", partyId);
						List<String> roleOfPartyIdFrom = SecurityUtil.getCurrentRoles(partyIdFromNew, delegator);
						if(UtilValidate.isNotEmpty(roleOfPartyIdFrom)){
							ctxMap.put("roleTypeIdFrom", roleOfPartyIdFrom.get(0));
							ctxMap.put("roleTypeIdTo", expireGv.getString("roleTypeIdTo"));
							ctxMap.put("partyRelationshipTypeId", "GROUP_ROLLUP");
							ctxMap.put("userLogin", userLogin);
							Map<String , Object> resultService = dispatcher.runSync("createPartyRelationship", ctxMap);
							if(ServiceUtil.isSuccess(resultService)){
								TransactionUtil.commit();
							}else{
								request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
								request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							}
						}else{
							request.setAttribute(ModelService.ERROR_MESSAGE, "have no role");
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						}
						
					}
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	
	public static String createEmplPosition(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String partyId = request.getParameter("partyId");
		String actualFromDateStr = request.getParameter("actualFromDate");
		String actualThruDateStr = request.getParameter("actualThruDate");
		String quantityStr = request.getParameter("quantity");
		int quantity = 1;
		try {
			quantity = Integer.parseInt(quantityStr);
		} catch (NumberFormatException e) {
			quantity = 1;
		}
		Timestamp actualFromDate = new Timestamp(Long.parseLong(actualFromDateStr));
		Timestamp actualThruDate = null;
		if(actualThruDateStr != null){
			actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(actualThruDateStr)));
		}
		Map<String, Object> ctxMap = FastMap.newInstance();
		ctxMap.put("emplPositionTypeId", emplPositionTypeId);
		ctxMap.put("partyId", partyId);
		ctxMap.put("emplPositionTypeId", emplPositionTypeId);
		ctxMap.put("actualFromDate", actualFromDate);
		ctxMap.put("actualThruDate", actualThruDate);
		ctxMap.put("userLogin", userLogin);
		ctxMap.put("locale", locale);
		try{
			for(int i = 0; i < quantity; i++){
				Map<String, Object> resultService = dispatcher.runSync("createEmplPositionHR", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String createEmplPositionTypeForOrg(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String partyId = request.getParameter("partyId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		Locale locale = UtilHttp.getLocale(request);
		String actualFromDateStr = request.getParameter("actualFromDate");
		String actualThruDateStr = request.getParameter("actualThruDate");
		Timestamp actualFromDate = new Timestamp(Long.parseLong(actualFromDateStr));
		Timestamp actualThruDate = null;
		if(actualThruDateStr != null){
			actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(actualThruDateStr)));
		}
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
			List<String> allDeptId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
			if(allDeptId == null){
				allDeptId = FastList.newInstance();
			}
			allDeptId.add(partyId);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allDeptId));
			conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			List<GenericValue> listEmplPos = delegator.findList("EmplPosition", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(listEmplPos)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmploymentUiLabels", "EmplPositionTypeIsExistsInOrg", locale));
				return "error";
			}
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("partyId", partyId);
			ctxMap.put("emplPositionTypeId", emplPositionTypeId);
			ctxMap.put("actualFromDate", actualFromDate);
			ctxMap.put("actualThruDate", actualThruDate);
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createEmplPositionHR", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
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
	
	public static String createEmplPositionFulfillment(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String emplPositionId = request.getParameter("emplPositionId");
		String partyId = request.getParameter("partyId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = null;
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		try {
			GenericValue emplPosition = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId), false);
			if(emplPosition == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "not found position id: " + emplPositionId);
				return "error";
			}
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("employeePartyId", partyId));
            conds.add(EntityCondition.makeCondition("partyId", emplPosition.getString("partyId")));
            conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
            List<GenericValue> emplPositionFuls = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conds), null, null, null, false);
            if(UtilValidate.isNotEmpty(emplPositionFuls)){
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmplIsExist", locale));
                return "error";
            }
			String partyGroupId = emplPosition.getString("partyId");
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			if(!emplListId.contains(partyId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmployeeNotInPartyGroup", UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId), "orgName", PartyHelper.getPartyName(delegator, partyGroupId, false)), locale));
				return "error";
			}
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("partyId", partyId);
			ctxMap.put("emplPositionId", emplPositionId);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("createEmplPositionFulfillmentOlbius", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				String emplPositionTypeId = emplPosition.getString("emplPositionTypeId");
				String partyIdFrom = emplPosition.getString("partyId");
				//create role for employee
				List<GenericValue> emplPosTypePartyRelConfig = delegator.findByAnd("EmplPosTypePartyRelConfig", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
				resultService = EmployeeHelper.createEmplPosTypePartyRel(dispatcher, delegator, emplPosTypePartyRelConfig, fromDate, thruDate, userLogin, partyIdFrom, partyId);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
					TransactionUtil.rollback();
					return "error";
				}
				//create groupId for userLogin of employee
				List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
						UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false);
				if(UtilValidate.isNotEmpty(userLoginList)){
					String userLoginId = userLoginList.get(0).getString("userLoginId");
					for(GenericValue tempGv: emplPosTypeSecGroupConfig){
						String groupId = tempGv.getString("groupId");
						dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginId,
								"groupId", groupId,
								"fromDate", fromDate,
								"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
								"userLogin", systemUserLogin));
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHREmployeeUiLabels", "AssignPositionForEmplSuccess", locale));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericEntityException e) {
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
	
	public static String changingEmplPositionAndPartyRel(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyIdTo = request.getParameter("partyId");
		String partyIdFrom = request.getParameter("partyIdFrom");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		List<GenericValue> partyRelList;
		try {
			Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
			Timestamp thruDate = thruDateStr != null? UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))) : null;
			partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			GenericValue partyRel = partyRelList.get(0);
			String partyIdFromOld = partyRel.getString("partyIdFrom");
			Timestamp fromDateOld = partyRel.getTimestamp("fromDate");
			Timestamp thruDateOld = partyRel.getTimestamp("thruDate");
			List<GenericValue> emplPositionList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(UtilMisc.toMap("employeePartyId", partyIdTo, "partyId", partyIdFrom)), 
					null, UtilMisc.toList("-fromDate"), null, false);
			String emplPositionTypeIdOld = null, emplPositionIdOld = null;
			if(UtilValidate.isNotEmpty(emplPositionList)){
				GenericValue emplPosition = emplPositionList.get(0);
				emplPositionIdOld = emplPosition.getString("emplPositionId");
				emplPositionTypeIdOld = emplPosition.getString("emplPositionTypeId");
				fromDateOld = emplPosition.getTimestamp("fromDate");
				thruDateOld = emplPosition.getTimestamp("thruDate");
			}
			if(emplPositionTypeId.equals(emplPositionTypeIdOld) && thruDateOld == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NewPositionTypeMustBeDiffOldPositionType", locale));
				return "error";
			}
			if(thruDateOld != null){
				Timestamp newFromDateAllow = UtilDateTime.getNextDayStart(thruDateOld);
				if(fromDate.compareTo(newFromDateAllow) != 0){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmploymentUiLabels", "OnlyAllowValueAfterOldPositionThruDateOneDay", locale));
					return "error";
				}
			}else{
				thruDateOld = UtilDateTime.getDayEnd(fromDate, -1L);
			}
			
			TransactionUtil.begin();
			if(emplPositionIdOld != null){
				GenericValue emplPositionOld = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionIdOld), false);
				GenericValue emplPositionFulfillment = delegator.findOne("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId", emplPositionIdOld, "partyId", partyIdTo, "fromDate", fromDateOld), false);
				emplPositionOld.set("actualThruDate", thruDateOld);
				emplPositionFulfillment.set("thruDate", thruDateOld);
				emplPositionOld.store();
				emplPositionFulfillment.store();
			}
			String newEmplPositionId = EmployeeHelper.getEmplPositionNotFullfillment(delegator, emplPositionTypeId, partyIdFrom, fromDate, null);
			Map<String, Object> resultService = null;
			if(newEmplPositionId == null){
				resultService = dispatcher.runSync("createEmplPosition", 
						UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "partyId", partyIdFrom, "actualFromDate", fromDate,
										"actualThruDate", thruDate,
										"userLogin", userLogin));
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				newEmplPositionId = (String)resultService.get("emplPositionId");
			}
			resultService = dispatcher.runSync("createEmplPositionFulfillment", 
					UtilMisc.toMap("emplPositionId", newEmplPositionId, "partyId", partyIdTo, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			context.put("timeZone", UtilHttp.getTimeZone(request));
			context.put("partyIdTo", partyIdTo);
			context.put("fromDate", fromDateOld);
			context.put("thruDate", thruDateOld);
			//context.put("emplPositionTypeId", emplPositionTypeIdOld);
			context.put("isUpdateEmployment", !partyIdFrom.equals(partyIdFromOld));
			resultService = dispatcher.runSync("updateEmplRelAndSecurityGroupByPositionType", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			List<GenericValue> emplPosTypePartyRelConfig = delegator.findByAnd("EmplPosTypePartyRelConfig", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
			resultService = EmployeeHelper.createEmplPosTypePartyRel(dispatcher, delegator, emplPosTypePartyRelConfig, fromDate, thruDate, userLogin, partyIdFrom, partyIdTo);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
				TransactionUtil.rollback();
				return "error";
			}
			//create groupId for userLogin of employee
			List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
					UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			String lastOrg = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyIdTo, "lastOrg", lastOrg), null, false);
			if(UtilValidate.isNotEmpty(userLoginList)){
				String userLoginId = userLoginList.get(0).getString("userLoginId");
				for(GenericValue tempGv: emplPosTypeSecGroupConfig){
					String groupId = tempGv.getString("groupId");
					dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginId,
							"groupId", groupId,
							"fromDate", fromDate,
							"thruDate", thruDate,
							"organizationId", lastOrg,
							"userLogin", systemUserLogin));
				}
			}
			TransactionUtil.commit();
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
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
	
	public static String updatePartyEmergency(HttpServletRequest request, HttpServletResponse response){
		String personFamilyBackgroundId = request.getParameter("personFamilyBackgroundId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue personFamilyBackground = delegator.findOne("PersonFamilyBackground", UtilMisc.toMap("personFamilyBackgroundId", personFamilyBackgroundId), false);
			if(personFamilyBackground == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "error");
				return "error";
			}
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("emergencyContact", "Y"));
			conds.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
			conds.add(EntityCondition.makeCondition("partyFamilyId", EntityJoinOperator.NOT_EQUAL, personFamilyBackground.getString("partyFamilyId")));
			List<GenericValue> listEmergency = delegator.findList("PersonFamilyBackground", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue tempGv: listEmergency){
				tempGv.set("emergencyContact", "N");
				tempGv.store();
			}
			personFamilyBackground.set("emergencyContact", "Y");
			personFamilyBackground.store();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getPersonFamilyBackground(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		try {
			List<Map<String, Object>> listReturn = FastList.newInstance();
			List<GenericValue> listFamilyPerson = delegator.findByAnd("PartyFamilyView", UtilMisc.toMap("relPartyId", userLogin.getString("partyId")), null, false);
			for(GenericValue tempGv: listFamilyPerson){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("personFamilyBackgroundId", tempGv.getString("personFamilyBackgroundId"));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempGv.getString("partyFamilyId")));
				tempMap.put("partyRelationshipTypeId", tempGv.getString("partyRelationshipTypeId"));
				Map<String, Object> resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("userLogin", userLogin, "partyId", tempGv.get("partyFamilyId"), "contactMechPurposeTypeId", "PHONE_MOBILE"));
				if(ServiceUtil.isSuccess(resultService)){
					tempMap.put("telephone", resultService.get("contactNumber"));
				}
				listReturn.add(tempMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listReturn);
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
	
	public static String checkHRPlanningForPositionInOrg(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		String actualFromDateStr = request.getParameter("actualFromDate");
		String actualThruDateStr = request.getParameter("actualThruDate");
		Timestamp actualFromDate = new Timestamp(Long.parseLong(actualFromDateStr));
		if(actualThruDateStr != null){
			Timestamp actualThruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(actualThruDateStr)));
			paramMap.put("actualThruDate", actualThruDate);
		}
		String quantityStr = request.getParameter("quantity");
		BigDecimal quantity = new BigDecimal(quantityStr);
		paramMap.put("actualFromDate", actualFromDate);
		paramMap.put("quantity", quantity);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "checkHRPlanningForPositionInOrg", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("checkHRPlanningForPositionInOrg", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			BigDecimal quantityInPlanning = (BigDecimal)resultService.get("quantityInPlanning");
			BigDecimal quantityPositionExists = (BigDecimal)resultService.get("quantityPositionExists");
			if(quantityInPlanning != null && quantityPositionExists != null){
				if(quantityPositionExists.add(quantity).compareTo(quantityInPlanning) > 0){
					GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
					Date fromDatePlanning = (Date)resultService.get("fromDatePlanning");
					Date thruDatePlanning = (Date)resultService.get("thruDatePlanning");
					DateUtil.getDateMonthYearDesc(fromDatePlanning);
					request.setAttribute("warningMessage", UtilProperties.getMessage("BaseHREmploymentUiLabels", 
							"WarningExceedHRPlanning", UtilMisc.toMap("quantityInPlanning", quantityInPlanning, 
																	   "quantityPositionExists", quantityPositionExists,
																	   "quantity", quantity,
																	   "emplPositionType", emplPositionType.getString("description"),
																	   "fromDate", DateUtil.getDateMonthYearDesc(fromDatePlanning),
																	   "thruDate", DateUtil.getDateMonthYearDesc(thruDatePlanning)), locale));
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			
		} catch(GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch(GenericServiceException e) {
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
	public static String approvalDependentFamilyEmpl(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String birthDateStr = request.getParameter("birthDate");
		String dependentStartDateStr = request.getParameter("dependentStartDate");
		String dependentEndDateStr = request.getParameter("dependentEndDate");
		if(dependentEndDateStr != null){
			paramMap.put("dependentEndDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(dependentEndDateStr))));
		}
		if(dependentStartDateStr != null){
			paramMap.put("dependentStartDate", new Timestamp(Long.parseLong(dependentStartDateStr)));
		}
		if(birthDateStr != null){
			paramMap.put("birthDate", new Date(Long.parseLong(birthDateStr)));
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "approvalDependentFamilyEmpl", paramMap, userLogin, timeZone, locale);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			Map<String, Object> resultService = dispatcher.runSync("approvalDependentFamilyEmpl", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
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
	public static String createPersonFamilyBackground(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String birthDateStr = request.getParameter("birthDate");
		if(birthDateStr != null){
			Date birthDate = new Date(Long.parseLong(birthDateStr));
			paramMap.put("birthDate", birthDate);
		}
		String dependentStartDateStr = request.getParameter("dependentStartDate");
		if(dependentStartDateStr != null){
			Timestamp dependentStartDate = new Timestamp(Long.parseLong(dependentStartDateStr));
			paramMap.put("dependentStartDate", dependentStartDate);
		}
		String dependentEndDateStr = request.getParameter("dependentEndDate");
		if(dependentEndDateStr != null){
			Timestamp dependentEndDate = new Timestamp(Long.parseLong(dependentEndDateStr));
			paramMap.put("dependentEndDate", dependentEndDate);
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPersonFamilyBackground", paramMap, userLogin, timeZone, locale);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultServices = dispatcher.runSync("createPersonFamilyBackground", context);
			if(!ServiceUtil.isSuccess(resultServices)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultServices));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
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
	
	public static String resetPasswordUserLoginToDefault(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(!SecurityUtil.hasRole(PropertiesUtil.HRM_ROLE, userLogin.getString("partyId"), delegator)){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE,  UtilProperties.getMessage("BaseHRUiLabels", "YouDoNotHavePermissitonToPerformThisAction", locale));
			return "error";
		}
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> resultService = dispatcher.runSync("resetPassword", UtilMisc.toMap("userLogin", systemUserLogin, "userLoginId", request.getParameter("userLoginId")));
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
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
	
}
