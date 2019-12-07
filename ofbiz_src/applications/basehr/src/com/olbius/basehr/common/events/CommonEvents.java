package com.olbius.basehr.common.events;

import java.sql.Timestamp;
import java.util.Calendar;
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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class CommonEvents {
	public static String getPostalAddressGeoDetail(HttpServletRequest request, HttpServletResponse response){
		String contactMechId = request.getParameter("contactMechId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(contactMechId != null){
			try {
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				if(postalAddress != null){
					Map<String, Object> retMap = FastMap.newInstance();
					retMap.put("address1", postalAddress.getString("address1"));
					retMap.put("countryGeoId", postalAddress.getString("countryGeoId"));
					retMap.put("stateProvinceGeoId", postalAddress.getString("stateProvinceGeoId"));
					retMap.put("districtGeoId", postalAddress.getString("districtGeoId"));
					retMap.put("wardGeoId", postalAddress.getString("wardGeoId"));
					request.setAttribute("postalAddress", retMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return "error";
			}
		}
		request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		return "success";
	}
	
	public static String getCustomTimePeriodByParent(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String parentPeriodId = request.getParameter("parentPeriodId");
		try {
			List<GenericValue> listCustomTimePeriod = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("parentPeriodId", parentPeriodId), UtilMisc.toList("fromDate"), false);
			List<Map<String, Object>> listReturn = FastList.newInstance();
			for(GenericValue tmpGv: listCustomTimePeriod){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("customTimePeriodId", tmpGv.getString("customTimePeriodId"));
				tempMap.put("periodName", tmpGv.getString("periodName"));
				tempMap.put("fromDate", tmpGv.getDate("fromDate").getTime());
				tempMap.put("thruDate", tmpGv.getDate("thruDate").getTime());
				listReturn.add(tempMap);
			}
			request.setAttribute("listCustomTimePeriod", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getListRoleTypeByParent(HttpServletRequest request, HttpServletResponse response){
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String parentTypeId = request.getParameter("parentTypeId");
		try {
			List<GenericValue> roleTypeList = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", parentTypeId), UtilMisc.toList("description"), false);
			for(GenericValue child: roleTypeList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String roleTypeId = child.getString("roleTypeId");
				tempMap.put("roleTypeId", roleTypeId);
				tempMap.put("value", roleTypeId);
				tempMap.put("label", child.getString("description"));
				//check if partyIdTo have children
				List<GenericValue> tempChildList = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", roleTypeId), 
						null, UtilMisc.toList("description"), null, false);
				if(UtilValidate.isNotEmpty(tempChildList)){
					Map<String, Object> childs = FastMap.newInstance();
					childs.put("label", "Loading...");
					childs.put("value", "getListRoleTypeByParent");
					List<Map<String, Object>> listChilds = FastList.newInstance();
					listChilds.add(childs);
					tempMap.put("items", listChilds);
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "error";
		}
		request.setAttribute("listReturn", listReturn);
		return "success";
	}
	
	public static String getListPartyRelByParent(HttpServletRequest request, HttpServletResponse response){
		String partyIdFrom = request.getParameter("partyIdFrom");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(partyIdFrom != null){
			try {
				listReturn = CommonUtil.getListPartyRelByParent(delegator, partyIdFrom);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		request.setAttribute("listReturn", listReturn);
		return "success";
	}
	
	public static String getListEmplPositionTypeByParty(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			if(partyId == null){
				return "success";
			}
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
			List<GenericValue> listEmplPos = delegator.findList("EmplPositionAndTypeAndParty", EntityCondition.makeCondition(conditions), UtilMisc.toSet("emplPositionTypeId"), UtilMisc.toList("emplPositionTypeId"), null, false);
			for(GenericValue tempGv: listEmplPos){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("emplPositionTypeId", tempGv.getString("emplPositionTypeId"));
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", tempGv.getString("emplPositionTypeId")), false);
				tempMap.put("description", emplPositionType.getString("description"));
				listReturn.add(tempMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String getListAllEmplPositionTypeOfParty(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String yearStr = request.getParameter("year");
		String monthStr = request.getParameter("month");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		Timestamp thruDate = fromDate;
		if(partyId == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			return "error";
		}
		try {
			if(yearStr != null){
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);
				if(monthStr != null){
					cal.set(Calendar.MONTH, Integer.parseInt(monthStr));
				}else{
					cal.set(Calendar.MONTH, 0);
				}
				cal.set(Calendar.YEAR, Integer.parseInt(yearStr));
				Timestamp ts = new Timestamp(cal.getTimeInMillis());
				fromDate = UtilDateTime.getYearStart(ts);
				thruDate = UtilDateTime.getYearEnd(ts, timeZone, locale);
			}
			List<GenericValue> emplPosList = PartyUtil.getListAllEmplPositionTypeOfParty(delegator, partyId, fromDate, thruDate);
			List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPosList, "emplPositionTypeId", true);
			for(String emplPositionTypeId: emplPositionTypeList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("emplPositionTypeId", emplPositionTypeId);
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				tempMap.put("description", emplPositionType.getString("description"));
				listReturn.add(tempMap);
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", listReturn);
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getListMajor(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> majorList = delegator.findList("Major", null, UtilMisc.toSet("description", "majorId"), UtilMisc.toList("description"), null, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", majorList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String getListEducationSchool(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> schoolList = delegator.findList("EducationSchool", null, UtilMisc.toSet("schoolId", "schoolName"), 
					UtilMisc.toList("schoolName"), null, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", schoolList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
		}
		return "success";
	}
	
	public static String getAncestorTreeOfPartyGroup(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		if(partyId != null){
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			try {
				List<String> rootList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if(UtilValidate.isEmpty(rootList)){
					return "error";
				}
				List<String> ancestorTreeList = FastList.newInstance();
				String tempPartyId = partyId;
				while (!rootList.contains(tempPartyId)) {
					GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempPartyId);
					if(parentOrg == null){
						break;
					}
					String parentOrgId = parentOrg.getString("partyIdFrom");
					ancestorTreeList.add(0, parentOrgId);
					tempPartyId = parentOrgId;
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("ancestorTree", ancestorTreeList);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}
	
	public static String getDataTypeGeneralService(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String serviceName = request.getParameter("serviceName");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(serviceName == null){
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "serviceName is null");
			return "error";
		}
		String conditionParam = request.getParameter("condition");
		List<EntityCondition> listAllCondition = FastList.newInstance();
		if(conditionParam != null && conditionParam.trim().length() > 0){
			JSONArray conditionJsonArr = JSONArray.fromObject(conditionParam);
			for(int i = 0; i < conditionJsonArr.size(); i++){
				JSONObject condJson = conditionJsonArr.getJSONObject(i);
				if(condJson.has("fieldName") && condJson.has("fieldValue")){
					listAllCondition.add(EntityCondition.makeCondition(condJson.getString("fieldName"), condJson.getString("fieldValue")));
				}
			}
		}
		Map<String, Object> context = FastMap.newInstance();
		context.put("listAllCondition", listAllCondition);
		context.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync(serviceName, context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute("listReturn", resultService.get("listReturn"));
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String getListParentSkillType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> skillTypeList = delegator.findByAnd("SkillType", UtilMisc.toMap("parentTypeId", null), UtilMisc.toList("description"), false);
			request.setAttribute("listReturn", skillTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String checkUserLoginExists(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String userLoginId = request.getParameter("userLoginId");
		try {
			GenericValue userLoginCheck = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			if(userLoginCheck != null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExists", UtilHttp.getLocale(request)));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
}
