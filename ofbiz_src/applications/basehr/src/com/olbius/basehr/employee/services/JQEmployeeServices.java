package com.olbius.basehr.employee.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.common.util.EntityMiscUtil;

public class JQEmployeeServices {

	//Constant
	public static final String module = JQEmployeeServices.class.getName();

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplNotification(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", parameters.get("partyId")[0])));
		List<EntityCondition> tmpList = new ArrayList<EntityCondition>();
		tmpList.add(EntityCondition.makeCondition("openTime",EntityJoinOperator.EQUALS, null));
		tmpList.add(EntityCondition.makeCondition("openTime",EntityJoinOperator.LESS_THAN_EQUAL_TO, new Timestamp((new Date()).getTime())));
		listAllConditions.add(EntityCondition.makeCondition(tmpList,EntityJoinOperator.OR));
		try {
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "Notification", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListNotification service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplPayrollParameter(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listSortFields.add("paramCharacteristicId");
		try {
			String fromDateStr = request.getParameter("fromDate");
			String thruDateStr = request.getParameter("thruDate");
			if(fromDateStr != null && thruDateStr != null){
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
				EntityCondition dateConds = EntityConditionUtils.makeDateConds(fromDate, thruDate);
				listAllConditions.add(dateConds);
				listAllConditions.add(EntityCondition.makeCondition("paramCharacteristicId", EntityJoinOperator.IN, UtilMisc.toList("PHU_CAP", "THUONG")));
				tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
				listIterator = delegator.find("PayrollEmplAndParametersPG", tmpConditon, null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling JQGetListEmplPayrollParameter service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplPosFulfillment(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("employeePartyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("EmplPositionAndFulfillment", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplPosFulfillment service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplSkill(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PartySkill", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplSkill service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplQual(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PartyQual", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplQual service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplOvertimeHistory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("WorkOvertimeRegistration", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplOvertimeHistory service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplWorkLate(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("EmplWorkingLate", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplWorkLate service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplFamily(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(!security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", userLogin)){
			partyId = userLogin.getString("partyId");
		}
		mapCondition.put("relPartyId", partyId);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<GenericValue> data = null;
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("familyFirstName");
			}
			data = delegator.findList("PartyFamilyView", tmpConditon, null, listSortFields, null, false);
			if(end > data.size()){
				end = data.size();
			}
			totalRows = data.size();
			data = data.subList(start, end);
			for(GenericValue tempGv: data){
				Map<String,Object> tempMap = tempGv.getAllFields();
				String tempPartyId = tempGv.getString("partyFamilyId");
				Map<String, Object> resultService = dispatcher.runSync("getPartyTelephone", 
						UtilMisc.toMap("partyId", tempPartyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", userLogin));
				String contactNumberString = "";
				if (UtilValidate.isNotEmpty(resultService)) {
					contactNumberString = (String) resultService.get("contactNumber");
				}
				String isDependent = tempGv.getString("isDependent");
				if("Y".equals(isDependent)){
					tempMap.put("isDependent", true);
					Timestamp dependentStartDate = tempGv.getTimestamp("dependentStartDate");
					if(dependentStartDate != null){
						tempMap.put("dependentStartDate", dependentStartDate.getTime());
					}
				}else{
					tempMap.put("isDependent", false);
				}
				tempMap.put("phoneNumber", contactNumberString);
				listReturn.add(tempMap);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListEmplFamily service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplEducation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PersonEducation", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplEducation service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplWorkProcess(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PersonWorkingProcess", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListEmplWorkProcess service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> createPersonFamilyBackground(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		Security security = dctx.getSecurity();
		String partyId = (String)context.get("partyId");
		String firstName = (String)context.get("firstName");
		String middleName = (String)context.get("middleName");
		String lastName = (String)context.get("lastName");
		Date birthDate = (Date)context.get("birthDate");
		String phoneNumber = (String)context.get("phoneNumber");
		String isDependent=(String)context.get("isDependent");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		String personFamilyId = null;
		try {
			String statusId = "DEP_ACCEPT";
			if(!security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", userLogin)){
				partyId = userLogin.getString("partyId");
				statusId = "DEP_WAITING_APPR";
			}
			if(partyId == null || partyId.trim().length() == 0){
				partyId = userLogin.getString("partyId");
			}
			Map<String, Object> familyPerson = dispatcher.runSync("createPerson", UtilMisc.toMap("firstName", firstName, "middleName", middleName, "lastName", lastName, "birthDate", birthDate));
			String familyPersonId = (String)familyPerson.get("partyId");
			GenericValue personFamilyBackground = delegator.makeValue("PersonFamilyBackground");
			personFamilyId = delegator.getNextSeqId("PersonFamilyBackground");
			personFamilyBackground.set("fromDate", new Timestamp(System.currentTimeMillis()));
			personFamilyBackground.set("personFamilyBackgroundId", personFamilyId);
			personFamilyBackground.set("partyId", partyId);
			personFamilyBackground.set("partyFamilyId", familyPersonId);
			personFamilyBackground.setNonPKFields(context);
			if("Y".equals(isDependent)){
				personFamilyBackground.set("isDependent", "Y");
				personFamilyBackground.set("statusId", statusId);
			}else{
				personFamilyBackground.set("isDependent", "N");
			}
			personFamilyBackground.create();
			dispatcher.runSync("createPartyTelecomNumber", UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_MOBILE", "partyId", familyPersonId, "contactNumber", phoneNumber, "userLogin", userLogin));
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}	
		successResult.put("personFamilyId", personFamilyId);
		return successResult;
	}
	
	public static Map<String, Object> deletePersonFamilyBackgroundSv(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Map<String,Object> result= ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String personFamilyBackgroundId = (String)context.get("personFamilyBackgroundId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String success = "success";
		if(UtilValidate.isNotEmpty(personFamilyBackgroundId)){
			GenericValue pfbg = delegator.findOne("PersonFamilyBackground", UtilMisc.toMap("personFamilyBackgroundId",personFamilyBackgroundId), false);
			if(UtilValidate.isNotEmpty(pfbg)){
				String partyId= pfbg.getString("partyFamilyId");
				if(UtilValidate.isNotEmpty(partyId)){
					List<GenericValue> ctm = EntityUtil.filterByDate(delegator.findList("PartyContactMech", EntityCondition.makeCondition("partyId",partyId), null, null, null, false));
					for(GenericValue entry:ctm){
						String contactMechId= entry.getString("contactMechId");
						try {
							dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",contactMechId,"userLogin", userLogin));
						} catch (GenericServiceException e) {
							success = "error";
						}
					}
				}
				pfbg.remove();
			}
		}
		
		result.put("success", success);
		return result;
	}
}
