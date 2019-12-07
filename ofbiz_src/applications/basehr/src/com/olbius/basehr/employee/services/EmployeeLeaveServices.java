package com.olbius.basehr.employee.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.insurance.helper.InsuranceHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class EmployeeLeaveServices {
	public static final String FIRST_HALF_DAY = "FIRST_HALF_DAY";
	public static final String SECOND_HALF_DAY = "SECOND_HALF_DAY";
	public static final String START_LEAVE = "START_LEAVE";
	public static final String END_LEAVE = "END_LEAVE";
	public static final String module = EmployeeLeaveServices.class.getName();
	public static Map<String, Object> preProcessCreateEmplLeave(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String workingShiftStr = (String)context.get("workingShift");
		JSONArray workingShiftArr = JSONArray.fromObject(workingShiftStr);
		List<String> workingShift = FastList.newInstance();
		for(int i = 0; i < workingShiftArr.size(); i++){
			workingShift.add(workingShiftArr.getJSONObject(i).getString("workingShiftId"));
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		Map<String, Object> serviceCtx = FastMap.newInstance();
		serviceCtx.putAll(context);
		serviceCtx.put("fromDate", fromDate);
		serviceCtx.put("thruDate", thruDate);
		serviceCtx.put("workingShiftList", workingShift);
		try {
			Map<String, Object> resultService = dispatcher.runSync("createEmplLeave", ServiceUtil.setServiceFields(dispatcher, "createEmplLeave", serviceCtx, (GenericValue)context.get("userLogin"), (TimeZone)context.get("timeZone"), locale));
			if(ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "createEmplLeaveSuccessfully", locale));
			}else{
				return ServiceUtil.returnError((String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
	}
	
	public static Map<String, Object> createEmplLeave(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue emplLeave = delegator.makeValue("EmplLeave");
		emplLeave.setNonPKFields(context);
		String emplLeaveId = delegator.getNextSeqId("EmplLeave");
		emplLeave.set("emplLeaveId", emplLeaveId);
		emplLeave.set("statusId", "LEAVE_CREATED");
		Timestamp dateApplication = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		emplLeave.set("dateApplication", dateApplication);
		String partyId = (String)context.get("partyId");
		try {
			delegator.create(emplLeave);
			retMap.put("emplLeaveId", emplLeaveId);
			String approverPartyId = (String)context.get("approverPartyId");
			if(approverPartyId == null){
				List<String> apprPartyList = PartyUtil.getManagerOfEmpl(delegator, partyId, UtilDateTime.nowTimestamp(), userLogin.getString("userLoginId"));
				if(UtilValidate.isNotEmpty(apprPartyList)){
					approverPartyId = apprPartyList.get(0);
				}
			}
			if(approverPartyId != null){
				String action = "ViewListEmplLeave";
				String header = UtilProperties.getMessage("BaseHREmployeeUiLabels", "ApprovalEmplLeaveApplication", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
				CommonUtil.sendNotify(dispatcher, locale, approverPartyId, userLogin, header, action, null);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplLeaveList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyId = userLogin.getString("partyId");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		String yearStr = request.getParameter("year");
		Timestamp fromDateCond = null, thruDateCond = null;
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("lcoale");
		if(yearStr != null){
			cal.set(Calendar.YEAR, Integer.parseInt(yearStr));
			fromDateCond = UtilDateTime.getYearStart(new Timestamp(cal.getTimeInMillis()));
		}else{
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			fromDateCond = UtilDateTime.getYearStart(nowTimestamp);
		}
		thruDateCond = UtilDateTime.getYearEnd(fromDateCond, timeZone, locale);
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDateCond));
		listConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDateCond));
		try {
			List<String> listSortFieldsFirst = FastList.newInstance();
			listSortFieldsFirst.add("-fromDate");
			List<GenericValue> emplLeaveList = delegator.findList("EmplLeave", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, listSortFieldsFirst, null, false);
//			
			boolean isFilterAndSortAdvance = false; 
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("partyId");
			listFieldInEntity.add("emplLeaveId");
			listFieldInEntity.add("statusId");
			listFieldInEntity.add("emplLeaveReasonTypeId");
			listFieldInEntity.add("dateApplication");
			listFieldInEntity.add("commentApproval");
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				emplLeaveList = EntityConditionUtils.doFilterGenericValue(emplLeaveList, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("emplLeaveId");
			}
			emplLeaveList = EntityUtil.orderBy(emplLeaveList, sortedFieldInEntity);
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				if(end > emplLeaveList.size()){
					end = emplLeaveList.size();
				}
				totalRows = emplLeaveList.size();
				emplLeaveList = emplLeaveList.subList(start, end);
			}else{
				isFilterAndSortAdvance = true;
			}
			if(end > emplLeaveList.size()){
				end = emplLeaveList.size();
			}
//			totalRows = emplLeaveList.size();
//			emplLeaveList = emplLeaveList.subList(start, end);
			for(GenericValue tempGv: emplLeaveList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", tempGv.get("partyId"));
				tempMap.put("emplLeaveId", tempGv.get("emplLeaveId"));
				tempMap.put("statusId", tempGv.get("statusId"));
				tempMap.put("emplLeaveReasonTypeId", tempGv.get("emplLeaveReasonTypeId"));
				String workingShiftId = tempGv.getString("workingShiftId");
				Timestamp fromDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, tempGv.getTimestamp("fromDate"), 
						tempGv.getString("fromDateLeaveTypeId"), EmployeeLeaveServices.START_LEAVE);
				Timestamp thruDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, tempGv.getTimestamp("thruDate"), 
						tempGv.getString("thruDateLeaveTypeId"), EmployeeLeaveServices.END_LEAVE);
				Timestamp dateApplication = tempGv.getTimestamp("dateApplication");
				Float nbrDayLeave = EmployeeHelper.getNbrDayLeave(delegator, tempGv);
				tempMap.put("nbrDayLeave", nbrDayLeave);
				tempMap.put("fromDate", fromDateLeave.getTime());
				tempMap.put("thruDate", thruDateLeave.getTime());
				tempMap.put("dateApplication", dateApplication.getTime());
				tempMap.put("commentApproval", tempGv.get("commentApproval"));
				listReturn.add(tempMap);
			}
			if(isFilterAndSortAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity) ){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				totalRows = listReturn.size();
				if(end > totalRows){
					end = totalRows;
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplLeave(DispatchContext ctx, Map<String, Object> context) {
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
			listIterator = delegator.find("EmplLeave", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListLeave service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplLeaveByCustomTimePeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyId = request.getParameter("partyId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String benefitClassTypeId = request.getParameter("benefitClassTypeId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			listSortFields.add("partyId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
			List<String> emplIdList = EntityUtil.getFieldListFromEntityList(emplList, "partyId", false);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplIdList));
			listAllConditions.add(EntityConditionUtils.makeDateConds(fromDateTs, thruDateTs));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
			List<GenericValue> partyInsuranceAllowancePayment = delegator.findByAnd("PartyInsuranceAllowancePayment", null, null, false);
			List<String> emplLeavePaid = EntityUtil.getFieldListFromEntityList(partyInsuranceAllowancePayment, "emplLeaveId", true);
			if(UtilValidate.isNotEmpty(emplLeavePaid)){
				listAllConditions.add(EntityCondition.makeCondition("emplLeaveId", EntityJoinOperator.NOT_IN, emplLeavePaid));
			}
			List<GenericValue> leaveReasonTypeOfBenefitClassType = delegator.findByAnd("InsAllowanceBenefitTypeAndLeaveReason", UtilMisc.toMap("benefitClassTypeId", benefitClassTypeId), null, false);
			if(UtilValidate.isEmpty(leaveReasonTypeOfBenefitClassType)){
				successResult.put("listIterator", listReturn);
				successResult.put("TotalRows", String.valueOf(totalRows));
				return successResult;
			}
			List<String> emplLeaveReasonTypeList = EntityUtil.getFieldListFromEntityList(leaveReasonTypeOfBenefitClassType, "emplLeaveReasonTypeId", true);
			listAllConditions.add(EntityCondition.makeCondition("isBenefitSocialIns", "Y"));
			listAllConditions.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityJoinOperator.IN, emplLeaveReasonTypeList));
			List<GenericValue> emplLeaveList = delegator.findList("EmplLeaveAndReasonType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
			totalRows = emplLeaveList.size();
			if(end > totalRows){
				end = totalRows;
			}
			emplLeaveList = emplLeaveList.subList(start, end);
			Locale locale = (Locale)context.get("locale");
			//Timestamp monthStart = UtilDateTime.getMonthStart(fromDateTs);
			for(GenericValue emplLeave: emplLeaveList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String tempPartyId = emplLeave.getString("partyId");
				Float totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave);
				String emplLeaveReasonTypeId = emplLeave.getString("emplLeaveReasonTypeId");
				String commonNormal = UtilProperties.getMessage("CommonUiLabels", "CommonNormal", locale); 
				//List<GenericValue> benefitTypeList = delegator.findByAnd("InsBenefitLeaveReasonType", UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId), null, false);
				Timestamp fromDateLeave = emplLeave.getTimestamp("fromDate");
				
				/*if(UtilValidate.isNotEmpty(benefitTypeList)){
					tempMap.put("benefitTypeId", benefitTypeList.get(0).getString("benefitTypeId"));
				}*/
				tempMap.put("totalDayLeave", totalDayLeave);
				tempMap.put("emplLeaveId", emplLeave.get("emplLeaveId"));
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempPartyId));
				tempMap.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
				tempMap.put("dateParticipateIns", InsuranceHelper.getDateParticipateIns(delegator, tempPartyId));
				tempMap.put("insuranceParticipatePeriod", InsuranceHelper.getDescInsParticipatePeriod(delegator, tempPartyId, fromDate, locale));
				if("HEALTH_IMPROVEMENT".equals(benefitClassTypeId)){
					tempMap.put("statusConditionBenefit", commonNormal);
				}else if("SICKNESS_PREGNANCY".equals(benefitClassTypeId)){
					tempMap.put("insuranceSalary", null);
					tempMap.put("accumulatedLeave", null);
					tempMap.put("allowanceAmount", null);
				}
				tempMap.put("fromDate", fromDateLeave.getTime());
				tempMap.put("thruDate", emplLeave.getTimestamp("thruDate").getTime());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
}
