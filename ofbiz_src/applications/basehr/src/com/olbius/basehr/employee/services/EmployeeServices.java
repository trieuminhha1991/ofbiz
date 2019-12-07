package com.olbius.basehr.employee.services;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.timekeeping.utils.TimekeepingUtils;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PersonHelper;
import com.olbius.basehr.util.SecurityUtil;

public class EmployeeServices {

	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	public static final int maxPunishmentLevel = 2;
	public static int transactionTimeout = 3000;
	
	public static Map<String, Object> addUserLoginToSecurityGroup(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		if(fromDate == null){
			fromDate = UtilDateTime.nowTimestamp();
		}
		GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
		userLoginSecurityGroup.set("fromDate", fromDate);
		userLoginSecurityGroup.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(userLoginSecurityGroup);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}		
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createEmploymentWorkInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyIdFrom =(String)context.get("partyIdFrom");
		String partyIdTo = (String)context.get("partyIdTo");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		BigDecimal rateAmount = (BigDecimal)context.get("rateAmount");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String insHealthCard = (String)context.get("insHealthCard");
		Timestamp heathInsuranceFromDate = (Timestamp)context.get("heathInsuranceFromDate");
		Timestamp heathInsuranceThruDate = (Timestamp)context.get("heathInsuranceThruDate");
		String hospitalId = (String)context.get("healthCareProvider");
		String orgId = (String)context.get("orgId");
		String isParticipateIns = (String)context.get("isParticipateIns");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		//String currency = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		try {
			if(orgId == null){
				orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			String emplPositionId = EmployeeHelper.getEmplPositionNotFullfillment(delegator, emplPositionTypeId, partyIdFrom, fromDate, null);
			Map<String, Object> resultService;
			if(emplPositionId == null){
				resultService = dispatcher.runSync("createEmplPosition", 
						UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "partyId", partyIdFrom, "actualFromDate", fromDate,
										"userLogin", userLogin));
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				emplPositionId = (String)resultService.get("emplPositionId");
			}
			resultService = dispatcher.runSync("createEmplPositionFulfillment", 
					UtilMisc.toMap("emplPositionId", emplPositionId, "partyId", partyIdTo, "fromDate", fromDate, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			if("Y".equals(isParticipateIns)){
				Timestamp participateFromDate = (Timestamp)context.get("participateFromDate");
				Map<String, Object> insEmplAdjustParticipateMap = FastMap.newInstance();
				insEmplAdjustParticipateMap.put("statusId", "PARTICIPATING");
				insEmplAdjustParticipateMap.put("insuranceOriginateTypeId", "NEWLY_PARTICIPATE");
				insEmplAdjustParticipateMap.put("locale", locale);
				insEmplAdjustParticipateMap.put("timeZone", timeZone);
				insEmplAdjustParticipateMap.put("userLogin", userLogin);
				insEmplAdjustParticipateMap.put("partyId", partyIdTo);
				insEmplAdjustParticipateMap.put("fromDate", participateFromDate);
				insEmplAdjustParticipateMap.put("thruDate", thruDate);
				resultService = dispatcher.runSync("createInsEmplAdjustParticipate", insEmplAdjustParticipateMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				BigDecimal insuranceSalary = (BigDecimal)context.get("insuranceSalary");
				Map<String, Object> partyInsuranceSalMap = FastMap.newInstance();
				partyInsuranceSalMap.put("locale", locale);
				partyInsuranceSalMap.put("timeZone", timeZone);
				partyInsuranceSalMap.put("userLogin", userLogin);
				partyInsuranceSalMap.put("partyId", partyIdTo);
				partyInsuranceSalMap.put("fromDate", participateFromDate);
				partyInsuranceSalMap.put("thruDate", thruDate);
				partyInsuranceSalMap.put("amount", insuranceSalary);
				partyInsuranceSalMap.put("partyGroupId", partyIdFrom);
				partyInsuranceSalMap.put("emplPositionTypeId", emplPositionTypeId);
				resultService = dispatcher.runSync("createPartyInsuranceSalary", partyInsuranceSalMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				List<String> insuranceTypeIdList = (List<String>)context.get("insuranceTypeIdList");
				if(insuranceTypeIdList == null){
					insuranceTypeIdList = FastList.newInstance();
				}
				List<GenericValue> insuranceTypeCompulsoryList = delegator.findByAnd("InsuranceType", UtilMisc.toMap("isCompulsory", "Y"), null, false);
				List<String> insuranceTypeCompulsoryIds = EntityUtil.getFieldListFromEntityList(insuranceTypeCompulsoryList, "insuranceTypeId", true);
				insuranceTypeIdList.addAll(insuranceTypeCompulsoryIds);
				Map<String, Object> partyParticipateInsMap = FastMap.newInstance();
				partyParticipateInsMap.put("partyId", partyIdTo);
				partyParticipateInsMap.put("statusId", "PARTICIPATING");
				partyParticipateInsMap.put("fromDate", participateFromDate);
				partyParticipateInsMap.put("thruDate", thruDate);
				partyParticipateInsMap.put("locale", locale);
				partyParticipateInsMap.put("timeZone", timeZone);
				partyParticipateInsMap.put("userLogin", userLogin);
				for(String insuranceTypeId: insuranceTypeIdList){
					partyParticipateInsMap.put("insuranceTypeId", insuranceTypeId);
					resultService = dispatcher.runSync("createPartyParticipateInsurance", partyParticipateInsMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
				}
				if(insHealthCard != null && insHealthCard.trim().length() > 0 && heathInsuranceFromDate != null && hospitalId != null){
					resultService = dispatcher.runSync("createPartyInsuranceHealth", UtilMisc.toMap("partyId", partyIdTo, "insHealthCard", insHealthCard, 
							"fromDate", heathInsuranceFromDate,
							"thruDate", heathInsuranceThruDate,
							"hospitalId", hospitalId,
							"userLogin", userLogin));
				}
			}
			
			//create role for employee
			List<GenericValue> emplPosTypePartyRelConfig = delegator.findByAnd("EmplPosTypePartyRelConfig", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), null, false);
			for(GenericValue tempGv: emplPosTypePartyRelConfig){
				String roleTypeIdFrom = tempGv.getString("roleTypeIdFrom");
				String roleTypeIdTo = tempGv.getString("roleTypeIdTo");
				String partyRelationshipTypeId = tempGv.getString("partyRelationshipTypeId");
				String isFromOrgToPerson = tempGv.getString("isFromOrgToPerson");
				String primaryPartyId = tempGv.getString("primaryPartyId");
				if("_NA_".equals(primaryPartyId)){
					primaryPartyId = partyIdFrom;
				}
				if("Y".equals(isFromOrgToPerson)){
					resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", roleTypeIdTo, "userLogin", userLogin));
					if(partyRelationshipTypeId != null){
						resultService = dispatcher.runSync("createPartyRelationship", 
								UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdTo", roleTypeIdTo,
										"partyIdFrom", primaryPartyId, "roleTypeIdFrom", roleTypeIdFrom,
										"partyRelationshipTypeId", partyRelationshipTypeId, "fromDate", fromDate,
										"thruDate", thruDate, "userLogin", userLogin));
					}
				}else{
					resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", roleTypeIdFrom, "userLogin", userLogin));
					if(partyRelationshipTypeId != null){
						resultService = dispatcher.runSync("createPartyRelationship", 
								UtilMisc.toMap("partyIdTo", primaryPartyId, "roleTypeIdTo", roleTypeIdTo,
										"partyIdFrom", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom,
										"partyRelationshipTypeId", partyRelationshipTypeId, "fromDate", fromDate,
										"thruDate", thruDate, "userLogin", userLogin));
					}
				}
			}
			//create Employment
			resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", "EMPLOYEE", "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			resultService = dispatcher.runSync("createEmployment", UtilMisc.toMap("partyIdFrom", orgId, "partyIdTo", partyIdTo, 
					"roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "EMPLOYEE", 
					"fromDate",fromDate, "thruDate", thruDate, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			//create empl salary
			resultService = dispatcher.runSync("createPartySalaryBase", 
					UtilMisc.toMap("partyId", partyIdTo,
									"periodTypeId", context.get("periodTypeId"),
									"amount", rateAmount, 
									"fromDate", fromDate, "thruDate", thruDate,
									"userLogin", userLogin, "timeZone", timeZone, "locale", locale));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> findEmployeeByManager(DispatchContext dctx, Map<String, Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		List<EntityCondition> findConditions = FastList.newInstance();
		String managerId = userLogin.getString("partyId");
		List<GenericValue> employeeList = FastList.newInstance();
		try {
			employeeList = PartyUtil.getListEmployeeOfManager(delegator, userLogin.getString("userLoginId"));
			findConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, managerId));
			if(UtilValidate.isNotEmpty(firstName)){
				findConditions.add(EntityCondition.makeCondition(
							EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%")));
			}
			if(UtilValidate.isNotEmpty(lastName)){
				findConditions.add(EntityCondition.makeCondition(
							EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + lastName.toUpperCase() + "%")));
			}
			employeeList = EntityUtil.filterByAnd(employeeList, findConditions);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("listIt", employeeList);
		return retMap;
				
	}
	
	public static Map<String, Object> getGeneralInfoOfEmpl(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String) context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = FastMap.newInstance();
		try {
			List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
			List<GenericValue> emplPosition = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
			String deptName = " ";
			GenericValue emplRemindCount = EntityUtil.getFirst(delegator.findByAnd("PartyPunishmentRemindCount", UtilMisc.toMap("partyId", partyId), null, false));
			GenericValue emplPunishmentLevel = delegator.findOne("PartyPunishmentLevel", UtilMisc.toMap("partyId", partyId), false);
			List<String> emplPos = FastList.newInstance();
			for(GenericValue temp: emplPosition){
				GenericValue positionType = temp.getRelatedOne("EmplPositionType", false);
				if(UtilValidate.isNotEmpty(positionType)){
					emplPos.add(positionType.getString("description"));
				}
			}
			Long tempRemindCount = 0L;
			Long tempPunishmentLevel = 0L;
			if(UtilValidate.isNotEmpty(emplRemindCount)){
				tempRemindCount = emplRemindCount.getLong("punishmentCountSum");
			}
			if(UtilValidate.isNotEmpty(emplPunishmentLevel)){
				tempPunishmentLevel = emplPunishmentLevel.getLong("punishmentLevel");
			}
			if(UtilValidate.isNotEmpty(departmentList)){
				List<String> deptNames = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId", "groupName");
				deptName = StringUtils.join(deptNames, ", ");
			}
			results.put("emplRemindCount", tempRemindCount);
			results.put("emplPunishmentLevel", tempPunishmentLevel);
			results.put("departmentName", deptName);
			results.put("emplPosition", StringUtils.join(emplPos, ", "));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return results;
	}
	
	public static Map<String, Object> createEmplPositionAndFulfillment(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone= (TimeZone) context.get("timeZone");
		String emplPartyId = (String) context.get("partyId");
		String internalOrgId = (String) context.get("internalOrgId");
		context.put("partyId", internalOrgId);
		Map<String, Object> resultMap = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		try {
			Map<String, Object> emplPosCtx = ServiceUtil.setServiceFields(dispatcher, "createEmplPosition", context, userLogin, timeZone, locale);
			Map<String, Object> results = dispatcher.runSync("createEmplPosition", emplPosCtx);
			String emplPositionId = (String) results.get("emplPositionId");
			resultMap.put("emplPositionId", emplPositionId);
			Map<String, Object> emplPosFul = FastMap.newInstance();
			if(fromDate == null){
				fromDate = (Timestamp)context.get("actualFromDate");
			}
			emplPosFul.put("emplPositionId", emplPositionId);
			emplPosFul.put("partyId", emplPartyId);
			emplPosFul.put("fromDate", fromDate);
			emplPosFul.put("thruDate", thruDate);
			emplPosFul.put("userLogin", userLogin);
			dispatcher.runSync("createEmplPositionFulfillment", emplPosFul);
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public static Map<String, Object> getNbrDayLeaveEmp(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		//String leaveTypeId = (String)context.get("leaveTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("leaveStatus", "LEAVE_APPROVED"));
		Map<String, Object> retMap = FastMap.newInstance();
		List<Timestamp> listDayLeave = FastList.newInstance();
		if(UtilValidate.isNotEmpty(fromDate)){
			conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		}
		if(UtilValidate.isNotEmpty(thruDate)){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		}
		
		Map<String, Object> resultService;
		
		try {
			List<GenericValue> emplLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
			//float nbrDayLeave = 0;
			float nbrDayLeavePaid = 0;
			float nbrDayLeaveUnPaid = 0;
			List<GenericValue> allWorkingShift = TimekeepingUtils.getAllWorkingShift(delegator);
			for(GenericValue emplLeave: emplLeaves){
				String emplLeaveId = emplLeave.getString("emplLeaveId");
				Timestamp tempFromDate = emplLeave.getTimestamp("fromDate");
				Timestamp tempThruDate = emplLeave.getTimestamp("thruDate");
				if(fromDate != null && tempFromDate.before(fromDate)){
					tempFromDate = fromDate;
				}
				if(thruDate != null && tempThruDate.after(thruDate)){
					tempThruDate = thruDate;
				}
				//String tempLeaveTypeId = emplLeave.getString("leaveTypeId");
				
				float tempNbrDayLeave = UtilDateTime.getIntervalInDays(tempFromDate, tempThruDate) + 1;
				Timestamp tempTimestamp = tempFromDate;
				while (tempTimestamp.before(tempThruDate)) {
					//List<GenericValue> emplPosTypeAtTime = PartyUtil.getPositionTypeOfEmplAtTime(delegator, emplLeave.getString("partyId"), tempTimestamp);
					boolean isDayLeave = true;
					resultService = dispatcher.runSync("checkDayIsDayLeaveOfParty", UtilMisc.toMap("dateCheck", tempTimestamp, "partyId", emplLeave.getString("partyId"), "userLogin", context.get("userLogin")));
					isDayLeave = (Boolean)resultService.get("isDayLeave");
					if(isDayLeave){
						tempNbrDayLeave--;
					}else{
						listDayLeave.add(tempTimestamp);
					}
					tempTimestamp = UtilDateTime.getDayStart(tempTimestamp, 1);
				}
				List<GenericValue> workingShiftLeave = delegator.findByAnd("EmplLeaveWorkingShift", UtilMisc.toMap("emplLeaveId", emplLeaveId), null, false);
				tempNbrDayLeave = (tempNbrDayLeave * workingShiftLeave.size())/allWorkingShift.size(); 
				
				/*if(tempLeaveTypeId.equals("FIRST_HAFT_DAY") || tempLeaveTypeId.equals("SECOND_HAFT_DAY")){
					tempNbrDayLeave = tempNbrDayLeave/2;
				}*/
				//nbrDayLeave += tempNbrDayLeave;
				String leaveUnpaid = emplLeave.getString("leaveUnpaid");
				if(leaveUnpaid != null && leaveUnpaid.equals("Y")){
					nbrDayLeaveUnPaid += tempNbrDayLeave;
				}else{
					nbrDayLeavePaid += tempNbrDayLeave;
				}
			}
			retMap.put("nbrDayLeave", nbrDayLeaveUnPaid + nbrDayLeavePaid);
			retMap.put("nbrDayLeaveUnPaid", nbrDayLeaveUnPaid);
			retMap.put("nbrDayLeavePaid", nbrDayLeavePaid);
			//retMap.put("", value)
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getNbrHourWorkOvertime(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> retMap = FastMap.newInstance();
		List<EntityCondition> commonConds = FastList.newInstance();
		String statusId = (String)context.get("statusId");
		if(statusId == null){
			statusId = "WOTR_ACCEPTED";
		}
		commonConds.add(EntityCondition.makeCondition("statusId", statusId));
		commonConds.add(EntityCondition.makeCondition("partyId", partyId));
		Date dateStart = new Date(fromDate.getTime());
		Date dateEnd = new Date(thruDate.getTime());
		EntityCondition regisOvertimeConds = EntityCondition.makeCondition("dateRegistration", EntityOperator.BETWEEN, UtilMisc.toList(dateStart, dateEnd));
		//EntityCondition actualOvertimeConds = EntityCondition.makeCondition("actualDateWorkOvertime", EntityOperator.BETWEEN, UtilMisc.toList(dateStart, dateEnd));
		try {
			Float overtimeRegisHours = 0.0f;
			Float actualOvertimeHours = 0.0f;
			List<GenericValue> regisOvertime = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(EntityCondition.makeCondition(commonConds), EntityOperator.AND, regisOvertimeConds), null, null, null, false);
			//List<GenericValue> actualOvertimeWork = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(EntityCondition.makeCondition(commonConds), EntityOperator.AND, actualOvertimeConds), null, null, null, false);
			for(GenericValue tempGv: regisOvertime){
				Time overTimeFromDate = tempGv.getTime("overTimeFromDate");
				Time overTimeThruDate = tempGv.getTime("overTimeThruDate");
				Time actualStartTime = tempGv.getTime("actualStartTime");
				Time actualEndTime = tempGv.getTime("actualEndTime");
				Date dateWorkOT = tempGv.getDate("dateRegistration");
				actualOvertimeHours += TimekeepingUtils.getActualHourWorkingOvertime(delegator, partyId, dateWorkOT, actualStartTime, actualEndTime);
				if(overTimeFromDate != null && overTimeThruDate!= null){
					overtimeRegisHours += (float)(overTimeThruDate.getTime() - overTimeFromDate.getTime())/(1000 * 3600);
				}
			}
			/*for(GenericValue tempGv: actualOvertimeWork){
				Time actualStartTime = tempGv.getTime("actualStartTime");
				Time actualEndTime = tempGv.getTime("actualEndTime");
				if(actualStartTime != null && actualEndTime != null){
					actualOvertimeHours += (actualEndTime.getTime() - actualStartTime.getTime())/(1000 * 3600);
				}
			}*/
			retMap.put("hoursRegisWorkOvertime", overtimeRegisHours);
			retMap.put("hoursActualWorkOvertime", actualOvertimeHours);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return retMap;
	}
	
	public Map<String, Object> getNbrDayLeaveEmplInfo(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		//Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//Integer year = (Integer)context.get("year");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Timestamp monthBegin = UtilDateTime.getMonthStart(nowTimestamp);
		Timestamp monthEnd = UtilDateTime.getMonthEnd(nowTimestamp, timeZone, locale);
		Timestamp yearBegin = UtilDateTime.getYearStart(nowTimestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(nowTimestamp, timeZone, locale);
		Timestamp nowDate = UtilDateTime.getDayEnd(nowTimestamp);
		Map<String, Object> retMap = FastMap.newInstance();
		Map<String, Object> results = FastMap.newInstance();
		
		try {
			Map<String, Object> nbrDayLeaveInMonth = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", monthBegin, "thruDate", monthEnd));
			Map<String, Object> nbrDayLeaveInYear = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", yearBegin, "thruDate", yearEnd));
			retMap.put("nbrDayLeavePaidInMonth", nbrDayLeaveInMonth.get("nbrDayLeavePaid"));
			retMap.put("nbrDayLeaveUnPaidInMonth", nbrDayLeaveInMonth.get("nbrDayLeaveUnPaid"));
			retMap.put("nbrDayLeavePaidInYear", nbrDayLeaveInYear.get("nbrDayLeavePaid"));
			retMap.put("nbrDayLeaveUnPaidInYear", nbrDayLeaveInYear.get("nbrDayLeaveUnPaid"));
			results = dispatcher.runSync("getDayLeaveRegulation", ServiceUtil.setServiceFields(dispatcher, "getDayLeaveRegulation", context, userLogin, timeZone, locale));
			
			if(ServiceUtil.isSuccess(results)){
				Long dayLeaveRegulation = (Long)results.get("dayLeaveRegulation");
				if(results.get("dayLeaveRegulation") == null){
					dayLeaveRegulation = 0L;
				}
				retMap.put("dayLeaveRegulation", dayLeaveRegulation);
			}
			results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate));
			
			retMap.put("numberDayPaidLeave", results.get("nbrDayLeavePaid"));
			retMap.put("numberDayLeaveUnPaid", results.get("nbrDayLeaveUnPaid"));
			retMap.put("numberDayLeave", results.get("nbrDayLeavePaid"));
			retMap.put("nbrDayLeaveInYear", (Float)nbrDayLeaveInYear.get("nbrDayLeavePaid") + (Float)nbrDayLeaveInYear.get("nbrDayLeaveUnPaid"));
			
			//leave that paid in month
			Map<String, Object> emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, emplTimesheetId, monthBegin, nowDate, partyId);
			Float leaveInMonthFromTimekeep = (Float)emplDayLeaveTimekeeping.get("leavePaid");
			
			//leave that paid in year
			emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, emplTimesheetId, yearBegin, nowDate, partyId);
			Float leaveInYearFromTimekeep = (Float)emplDayLeaveTimekeeping.get("leavePaid");
			retMap.put("actualLeavePaidInMonth", leaveInMonthFromTimekeep);
			retMap.put("actualLeavePaidInYear", leaveInYearFromTimekeep);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	public static Map<String, Object> getDayLeaveRegulation(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		//Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> emplPositionTypes = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
			if(UtilValidate.isNotEmpty(emplPositionTypes)){
				//TODO need edit with employee hold 2 position
				String emplPosTypeId = EntityUtil.getFirst(emplPositionTypes).getString("emplPositionTypeId");
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPosTypeId), false);
				Long dayLeaveRegulation = emplPositionType.getLong("dayLeaveRegulation");
				if(UtilValidate.isEmpty(dayLeaveRegulation)){
					dayLeaveRegulation = 0L;
				}
				retMap.put("dayLeaveRegulation", dayLeaveRegulation);
			}else{
				//ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "NotFoundInfoEmplPositionType", locale));
				retMap.put("dayLeaveRegulation", 0L);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createNtfToApprEmplLeave(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String roleTypeId = (String)context.get("roleTypeId");
		
		String emplLeaveId = (String)context.get("emplLeaveId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		try {
			List<GenericValue> emplApprRoleType = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplApprRoleType, "partyId", true);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			if("ELA_VIEWER".equals(roleTypeId)){
				ntfCtx.put("ntfType", "ONE");
			}
			String partyId = emplLeave.getString("partyId");
			ntfCtx.put("partiesList", partyNtf);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("state", "open");
			ntfCtx.put("action", "LeaveApproval");
			ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "ApprovalEmplLeave", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, partyId, false)), (Locale)context.get("locale")));
			ntfCtx.put("targetLink", "emplLeaveId=" + emplLeaveId);
			ntfCtx.put("userLogin", userLogin);
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createEmplLeaveApprRoleType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		String emplLeaveId = (String)context.get("emplLeaveId");
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue emplLeaveApprRoleType = delegator.makeValue("EmplLeaveApprovalRoleType");
		
		emplLeaveApprRoleType.set("emplLeaveId", emplLeaveId);
		emplLeaveApprRoleType.set("partyId", partyId);
		emplLeaveApprRoleType.set("roleTypeId", roleTypeId);
		emplLeaveApprRoleType.set("fromDate", UtilDateTime.nowTimestamp());
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", systemUserLogin));
			emplLeaveApprRoleType.create();
			if(userLogin.getString("partyId").equals(partyId) && !"ELA_VIEWER".equals(roleTypeId)){
				//run service approved emplLeave
				//dispatcher.runSync("updateEmplLeaveApproval", UtilMisc.toMap("partyIdLeave", partyIdLeave, "leaveTypeId", leaveTypeId, "leaveFromDate", leaveFromDate, "approvalStatusId", "EMPL_LEAVE_ACCEPTED", "userLogin", userLogin));
				dispatcher.runSync("updateEmplLeaveApproval", UtilMisc.toMap("emplLeaveId", emplLeaveId, "approvalStatusId", "EMPL_LEAVE_ACCEPTED", "userLogin", userLogin));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmplLeaveApproval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		String emplLeaveId = (String)context.get("emplLeaveId");
		String leaveStatus = (String)context.get("approvalStatusId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		//List<String> workingShiftList = (List<String>)context.get("workingShiftList");
		String ntfId = (String)context.get("ntfId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		//Map<String, Object> results;
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale));
		
		//String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
		try {
			//GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("partyId", partyId, "leaveTypeId", leaveTypeId, "fromDate", fromDate), false);
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			if("LEAVE_APPROVED".equals(emplLeave.getString("statusId")) || "LEAVE_REJECTED".equals(emplLeave.getString("statusId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "LeaveApplicationApproved", locale));
			}
			String partyApprover = userLogin.getString("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyId", partyApprover));
			conditions.add(EntityCondition.makeCondition("emplLeaveId", emplLeaveId));
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", "ELA_APPROVER"), EntityOperator.OR, EntityCondition.makeCondition("roleTypeId", "ELA_DECIDER")));
			List<GenericValue> emplApprRole = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isEmpty(emplApprRole)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "NotPermissitonUpdateEmplLeave", locale));
			}
			if(fromDateStr != null){
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				emplLeave.set("fromDate", UtilDateTime.getDayStart(fromDate));
				emplLeave.store();
			}
			if(thruDateStr != null){
				Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
				emplLeave.set("thruDate", UtilDateTime.getDayEnd(thruDate));
				emplLeave.store();
			}
			for(GenericValue tempGv: emplApprRole){
				GenericValue emplLeaveApproval = delegator.makeValue("EmplLeaveApproval");
				emplLeaveApproval.set("emplLeaveId", emplLeaveId);
				emplLeaveApproval.set("partyId", partyApprover);
				emplLeaveApproval.set("roleTypeId", tempGv.getString("roleTypeId"));
				emplLeaveApproval.set("approvalStatusId", leaveStatus);
				emplLeaveApproval.set("approvalDate", UtilDateTime.nowTimestamp());
				emplLeaveApproval.set("comment", context.get("comment"));
				String emplLeaveApprovalId = delegator.getNextSeqId("EmplLeaveApproval");
				emplLeaveApproval.set("emplLeaveApprovalId", emplLeaveApprovalId);
				emplLeaveApproval.create();
			}
			if(UtilValidate.isNotEmpty(ntfId)){
				dispatcher.runSync("updateNotification", ServiceUtil.setServiceFields(dispatcher, "updateNotification", context, userLogin, timeZone, locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//retMap.put("leaveStatus", leaveStatus);
		return retMap;
	}
	
	/**
	 * run after updateEmplLeaveApproval service commit
	 */
	public static Map<String, Object> updateEmplLeaveStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
	
		String emplLeaveId = (String)context.get("emplLeaveId");
		String leaveStatus = (String)context.get("approvalStatusId");
		List<String> partyNtf = FastList.newInstance();		
		Map<String, Object> ntfCtx = FastMap.newInstance();
		ntfCtx.put("partiesList",partyNtf);
		ntfCtx.put("userLogin", userLogin);		
		ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
		ntfCtx.put("state", "open");
		ntfCtx.put("action", "LeaveApproval");		
		//ntfCtx.put("targetLink", "partyIdLeave=" + partyIdLeave +";leaveTypeId=" + leaveTypeId + ";leaveFromDate=" + leaveFromDate);
		ntfCtx.put("targetLink", "emplLeaveId=" + emplLeaveId);
		try {
			String partyApproverId = userLogin.getString("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("emplLeaveId", emplLeaveId));
			
			
			EntityCondition conditionCommon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> emplLeaveApproval = delegator.findList("EmplLeaveApprovalAndRoleType", EntityCondition.makeCondition(conditionCommon), null, null, null, false);
			List<String> partyAprrovedList = EntityUtil.getFieldListFromEntityList(emplLeaveApproval, "partyId", true);
			//check whether approver have DECIDER roleType?
			List<GenericValue> emplDeciderRoleAppr = EntityUtil.filterByAnd(emplLeaveApproval, UtilMisc.toMap("partyId", partyApproverId, "roleTypeId", "ELA_DECIDER"));
			//GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("partyId", partyIdLeave, "leaveTypeId", leaveTypeId, "fromDate", leaveFromDate), false);
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			String partyIdLeave = emplLeave.getString("partyId");
			//if approver set status is EMPL_LEAVE_REJECTED or approver have DECIDER role, then finish process EmplLeave and notify to party 
			if("EMPL_LEAVE_REJECTED".equals(leaveStatus) || UtilValidate.isNotEmpty(emplDeciderRoleAppr)){
				String emplLeaveStatus;
				if("EMPL_LEAVE_ACCEPTED".equals(leaveStatus)){
					emplLeaveStatus = "LEAVE_APPROVED";
					List<GenericValue> emplLeaveViewerRole = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(conditionCommon, 
																					EntityOperator.AND, EntityCondition.makeCondition("roleTypeId", "ELA_VIEWER")), null, null, null, false);
					List<String> tempList = EntityUtil.getFieldListFromEntityList(emplLeaveViewerRole, "partyId", true);
					partyNtf.addAll(tempList);
				}else{
					emplLeaveStatus = "LEAVE_REJECTED";
				}
				emplLeave.set("leaveStatus", emplLeaveStatus);
				emplLeave.store();
				partyNtf.add(partyIdLeave);
				partyNtf.addAll(partyAprrovedList);//notify to all party approved
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("header", UtilProperties.getMessage("EmployeeUiLabels", "LeaveApplicationApproved", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, partyIdLeave, false)), locale));
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				//if approver have not DECIDER role and approver accept this application leave, notify to DECIDER role approver if all APPROVER role approved
				EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "ELA_APPROVER");
				if(UtilValidate.isNotEmpty(partyAprrovedList)){
					condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyAprrovedList));
				}				 
				List<GenericValue> empRoleApprover = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(
																									conditionCommon,
																									EntityOperator.AND, condition1), null, null, null, false);
				//if all party have role ELA_APPROVER approved, notify to party have role ELA_DECIDER
				if(UtilValidate.isEmpty(empRoleApprover)){
					//partyNtf.add(e)
					List<GenericValue> emplRoleDecider = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(
							conditionCommon,
							EntityOperator.AND, EntityCondition.makeCondition("roleTypeId", "ELA_DECIDER")), null, null, null, false);
					List<String> tempList = EntityUtil.getFieldListFromEntityList(emplRoleDecider, "partyId", true);
					partyNtf.addAll(tempList);
					ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "ApprovalEmplLeave", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, partyIdLeave, false)), locale));
					dispatcher.runSync("createNotification", ntfCtx);
				}
			}
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> checkEmplLeaveDayInRegulation(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Timestamp monthEndFromDate = UtilDateTime.getMonthEnd(fromDate, timeZone, locale);
		String partyId = (String)context.get("partyId");
		Map<String, Object> results = null;
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Boolean dayLeaveInRegulation = true;
		try {
			List<GenericValue> emplPos = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
			if(UtilValidate.isNotEmpty(emplPos)){
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", EntityUtil.getFirst(emplPos).getString("emplPositionTypeId")), false);
				Long dayLeaveRegulation = emplPositionType.getLong("dayLeaveRegulation");
				
				//if day leave belong 2 difference month, must divide 2 period
				if(thruDate.after(monthEndFromDate)){
					Timestamp monthStarThruDate = UtilDateTime.getMonthStart(thruDate);
					float regulationDayLeaveFromDate = getRegulationDayLeaveInMonth(dayLeaveRegulation, fromDate, timeZone, locale);
					float regulationDayLeaveThruDate = getRegulationDayLeaveInMonth(dayLeaveRegulation, thruDate, timeZone, locale);
					results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", UtilDateTime.getMonthStart(fromDate), "thruDate", monthEndFromDate));
					Float dayLeaveInFromDateMonth = (Float)results.get("nbrDayLeave");
					results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", UtilDateTime.getMonthStart(thruDate), "thruDate", UtilDateTime.getMonthEnd(thruDate, timeZone, locale)));
					Float dayLeaveInThruDateMonth = (Float)results.get("nbrDayLeave");
					int nbrDayLeaveFromDate = UtilDateTime.getIntervalInDays(fromDate, monthEndFromDate);
					int nbrDayLeaveThruDate = UtilDateTime.getIntervalInDays(monthStarThruDate, thruDate);
					if(dayLeaveInFromDateMonth + nbrDayLeaveFromDate > regulationDayLeaveFromDate || dayLeaveInThruDateMonth + nbrDayLeaveThruDate > regulationDayLeaveThruDate){
						dayLeaveInRegulation = false;
					}
				}else{
					int nbrDayLeaveRequest = UtilDateTime.getIntervalInDays(fromDate, thruDate);
					float regulationDayLeave = getRegulationDayLeaveInMonth(dayLeaveRegulation, fromDate, timeZone, locale);
					results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", UtilDateTime.getMonthStart(fromDate), "thruDate", monthEndFromDate));
					Float dayLeaveInMonth = (Float)results.get("nbrDayLeave");
					if(dayLeaveInMonth + nbrDayLeaveRequest> regulationDayLeave){
						dayLeaveInRegulation = false;
					}
				}
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "NotFoundInfoEmplPositionType", locale));
			}
			retMap.put("dayLeaveInRegulation", dayLeaveInRegulation);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}

	
	private static float getRegulationDayLeaveInMonth(Long dayLeaveRegulation, Timestamp fromDate, TimeZone timeZone, Locale locale) {
		int month = UtilDateTime.getMonth(fromDate, timeZone, locale) + 1;
		float retValue = ((float)dayLeaveRegulation/12) * month + 2;
		return retValue;
	}
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> createBussinessTripProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		List<String> busTripContent = (List<String>)context.get("busTripContent");
		List<String> busDest = (List<String>)context.get("busDest");
		List<String> busFromDate = (List<String>)context.get("busFromDate");
		List<String> busThruDate = (List<String>)context.get("busThruDate");
		List<String> busNotes = (List<String>)context.get("busNotes");
		
		List<String> vehTypeId = (List<String>) context.get("vehTypeId");
		List<String> journey = (List<String>) context.get("journey");
		List<String> dateDeparture = (List<String>) context.get("dateDeparture");
		List<String> dateArrival = (List<String>) context.get("dateArrival");
		List<String> vehNotes = (List<String>) context.get("vehNotes");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "createProposalBusinessTripSuccessful", locale));
		if(UtilValidate.isEmpty(busTripContent)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "HaveNoBusinessTripContent", locale));
		}
		if(UtilValidate.isEmpty(busFromDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessFromDateIsEmpty", locale));
		}
		if(UtilValidate.isEmpty(busThruDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessThruDateIsEmpty", locale));
		}
		if(UtilValidate.isEmpty(busDest)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessDestIsEmpty", locale));
		}
		
		GenericValue partyBusTrip = delegator.makeValue("PartyBusinessTrip");
		String partyBusTripId = delegator.getNextSeqId("PartyBusinessTrip");
		partyBusTrip.put("partyBusinessTripId", partyBusTripId);
		
		partyBusTrip.put("statusId", "BUSS_TRIP_CREATED");
		partyBusTrip.put("partyId", partyId);
		retMap.put("partyBusinessTripId", partyBusTripId);
		
		try {
			delegator.create(partyBusTrip);
			//create approver for business trip proposal
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			List<String> managerId = PartyUtil.getManagerOfEmpl(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp());
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", managerId, "roleTypeId", "APPROVER", "userLogin", systemUserLogin));
			GenericValue businessTripApproval = delegator.makeValue("BusinessTripApprovalRole");
			businessTripApproval.put("partyBusinessTripId", partyBusTripId);
			businessTripApproval.put("partyId", managerId);
			businessTripApproval.put("roleTypeId", "APPROVER");
			businessTripApproval.put("fromDate", UtilDateTime.nowTimestamp());			
			delegator.create(businessTripApproval);
			
			for (int i = 0; i < busTripContent.size(); i++) {
				if(UtilValidate.isEmpty(busTripContent.get(i))){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessContentIsEmpty", locale));
				}
				if(UtilValidate.isEmpty(busDest.get(i))){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessFromDateIsEmpty", locale));				
				}
				if(UtilValidate.isEmpty(busFromDate.get(i))){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessFromDateIsEmpty", locale));
				}
				if(UtilValidate.isEmpty(busThruDate.get(i))){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "BusinessThruDateIsEmpty", locale));
				}
				GenericValue partyBusinessTripPlan = delegator.makeValue("PartyBusinessTripPlan");
				partyBusinessTripPlan.put("partyBusinessTripId", partyBusTripId);
				delegator.setNextSubSeqId(partyBusinessTripPlan, "busTripSeqId", 5, 1);
				partyBusinessTripPlan.put("businessTripContent", busTripContent.get(i));
				partyBusinessTripPlan.put("businessTripDest", busDest.get(i));
				
				Long tempFromDate = Long.parseLong(busFromDate.get(i));
				partyBusinessTripPlan.put("fromDate", new Timestamp(tempFromDate));
				
				Long tempThruDate = Long.parseLong(busThruDate.get(i));
				partyBusinessTripPlan.put("thruDate", new Timestamp(tempThruDate));
				
				if(UtilValidate.isNotEmpty(busNotes) && UtilValidate.isNotEmpty(busNotes.get(i))){
					partyBusinessTripPlan.put("notes", busNotes.get(i));
				}
				delegator.create(partyBusinessTripPlan);
			}
			if(UtilValidate.isNotEmpty(vehTypeId)){
				if(UtilValidate.isEmpty(journey)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "JourneyIsEmpty", locale));
				}
				if(UtilValidate.isEmpty(dateDeparture)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "DateDepartureIsEmpty", locale));	
				}
				if(UtilValidate.isEmpty(dateArrival)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "DateArrivalIsEmpty", locale));
				}
				for(int i = 0; i < vehTypeId.size(); i++){
					if(UtilValidate.isEmpty(vehTypeId.get(i))){
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "VehicleTypeIsEmpty", locale));
					}
					if(UtilValidate.isEmpty(journey.get(i))){
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "JourneyIsEmpty", locale));
					}
					if(UtilValidate.isEmpty(dateDeparture.get(i))){
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "DateDepartureIsEmpty", locale));
					}
					if(UtilValidate.isEmpty(dateArrival.get(i))){
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "DateArrivalIsEmpty", locale));
					}
					GenericValue partyBusinessTripVehicle = delegator.makeValue("PartyBusinessTripVehicle");
					partyBusinessTripVehicle.put("partyBusinessTripId", partyBusTripId);
					delegator.setNextSubSeqId(partyBussinessTripVehicle, "vehBusTripSeqId", 5, 1);
					partyBusinessTripVehicle.put("vehicleTypeId", vehTypeId.get(i));
					partyBusinessTripVehicle.put("journey", journey.get(i));
					
					Long tempFromDate = Long.parseLong(dateDeparture.get(i));
					partyBusinessTripVehicle.put("fromDate", new Timestamp(tempFromDate));
					Long tempThruDate = Long.parseLong(dateArrival.get(i));
					partyBusinessTripVehicle.put("thruDate", new Timestamp(tempThruDate));
					
					if(UtilValidate.isNotEmpty(vehNotes) && UtilValidate.isNotEmpty(vehNotes.get(i))){
						partyBusinessTripVehicle.put("notes", vehNotes.get(i));
					}
					delegator.create(partyBusinessTripVehicle);
				}
			}
			//create Notify for manager of employee
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("partyId", managerId);
			ntfCtx.put("state", "open");
			ntfCtx.put("header", UtilProperties.getMessage("EmployeeUiLabels", "EmplProposalBusinessTrip", locale));
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("action", "ApprovalBusinessTripProposal");
			ntfCtx.put("targetLink", "partyBusinessTripId=" + partyBusTripId);
			ntfCtx.put("userLogin", userLogin);
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return retMap;
	}*/
	
	public static Map<String, Object> approvalBussTripProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyBusinessTripId = (String)context.get("partyBusinessTripId");
		String statusId = (String)context.get("statusId");
		String approverId = (String)context.get("approverId");
		String notes = (String)context.get("notes");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			
			GenericValue partyBusinessTrip = delegator.findOne("PartyBusinessTrip", UtilMisc.toMap("partyBusinessTripId", partyBusinessTripId), false);
			String currStatusId = partyBusinessTrip.getString("statusId");
			if(!currStatusId.equals("BUSS_TRIP_CREATED") && !currStatusId.equals("BUSS_TRIP_IN_PROCESS")){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "CannotApprovalInCurrentStatus", UtilMisc.toList("BUSS_TRIP_IN_PROCESS", "BUSS_TRIP_CREATED"), locale));
			}
			if(UtilValidate.isEmpty(partyBusinessTrip)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "CannotFindPBusinessTripProposal", UtilMisc.toList(partyBusinessTripId), locale));
			}
			//check whether approver have role to approved
			Map<String, Object> results = dispatcher.runSync("checkPermissionApproveBussTripProposal", UtilMisc.toMap("partyId", approverId, "partyBusinessTripId", partyBusinessTripId, "userLogin", userLogin)); 
			
			if(ServiceUtil.isSuccess(results)){
				if(!(Boolean)results.get("isPermisson")){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
				}
				GenericValue businessTripApproval = delegator.makeValue("BusinessTripApproval");
				businessTripApproval.put("businessTripApprovalId", delegator.getNextSeqId("BusinessTripApproval"));
				businessTripApproval.put("partyId", approverId);
				businessTripApproval.put("approvalStatusId", statusId);
				businessTripApproval.put("partyBusinessTripId", partyBusinessTripId);
				businessTripApproval.put("roleTypeId", "APPROVER");
				businessTripApproval.put("approvalDate", UtilDateTime.nowTimestamp());
				
				if(UtilValidate.isNotEmpty(notes)){
					businessTripApproval.set("comments", notes);
				}
				businessTripApproval.create();
				//expire date of approver after approved
				GenericValue partyBusinessRole = (GenericValue)results.get("partyBusinessRole"); 
				partyBusinessRole.set("thruDate", UtilDateTime.nowTimestamp());
				partyBusinessRole.store();
				dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, (TimeZone)context.get("timeZone"), locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "BusinessTripProposalApproved", locale));
	}
	
	public static Map<String, Object> updateStatusBussTripProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyBusinessTripId = (String)context.get("partyBusinessTripId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String statusId = (String)context.get("statusId");
		GenericValue partyBusinessTrip;
		boolean approvalComplete = false;
		boolean allApproverAccept = true;
		try {
			partyBusinessTrip = delegator.findOne("PartyBusinessTrip", UtilMisc.toMap("partyBusinessTripId", partyBusinessTripId), false);
			String currStatus = partyBusinessTrip.getString("statusId");
			
			if(statusId.equals("BUSS_TRIP_REJECT")){
				partyBusinessTrip.set("statusId", "BUSS_TRIP_REJECT");
				partyBusinessTrip.store();
				approvalComplete = true;
			}else if(statusId.equals("BUSS_TRIP_APPROVAL")){
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("roleTypeId", "APPROVER"));
				conditions.add(EntityCondition.makeCondition("partyBusinessTripId", partyBusinessTripId));
				//conditions.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> partyBusiness = delegator.findList("BusinessTripApprovalRole", EntityCondition.makeCondition(conditions, EntityOperator.AND),null, null, null,false);
				
				List<EntityCondition> bussTripApprovalCond = FastList.newInstance();
				bussTripApprovalCond.add(EntityCondition.makeCondition("roleTypeId", "APPROVER"));
				bussTripApprovalCond.add(EntityCondition.makeCondition("partyBusinessTripId", partyBusinessTripId));
				
				//check if all approvers have approved, then update status of proposal
				for(GenericValue partyBuss: partyBusiness){
					String partyId = partyBuss.getString("partyId");
					List<GenericValue> partyBussApprovals =  delegator.findList("BusinessTripApproval", EntityCondition.makeCondition(
																	EntityCondition.makeCondition(bussTripApprovalCond, EntityOperator.AND),
																	EntityOperator.AND,
																	EntityCondition.makeCondition("partyId", partyId)
																), null, null, null, false);
					if(UtilValidate.isEmpty(partyBussApprovals)){
						allApproverAccept = false;
						break;
					}
				}
				if(allApproverAccept){
					approvalComplete = true;
					partyBusinessTrip.set("statusId", "BUSS_TRIP_APPROVAL");
					partyBusinessTrip.store();
				}
			}
			//send notify to proposer
			if(approvalComplete){
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("partyId", partyBusinessTrip.getString("partyId"));
				ntfCtx.put("state", "open");
				ntfCtx.put("header", UtilProperties.getMessage("EmployeeUiLabels", "BusinessTripProposalApproved", locale));
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("action", "ApprovalBusinessTripProposal");
				ntfCtx.put("targetLink", "partyBusinessTripId=" + partyBusinessTrip.getString("partyBusinessTripId"));
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("userLogin", userLogin);
				dispatcher.runSync("createNotification", ntfCtx);
			}else if(currStatus.equals("BUSS_TRIP_CREATED")){
				partyBusinessTrip.set("statusId", "BUSS_TRIP_IN_PROCESS");
				partyBusinessTrip.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> checkPermissionApproveBussTripProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyBusinessTripId = (String)context.get("partyBusinessTripId");
		String approverId = (String)context.get("partyId"); 
		Map<String, Object> retMap = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", approverId));
		conditions.add(EntityCondition.makeCondition("roleTypeId", "APPROVER"));
		conditions.add(EntityCondition.makeCondition("partyBusinessTripId", partyBusinessTripId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> partyBusinessRoles = delegator.findList("BusinessTripApprovalRole", EntityCondition.makeCondition(conditions, EntityOperator.AND),null, null, null,false);
			if(UtilValidate.isNotEmpty(partyBusinessRoles)){
				retMap.put("isPermisson", true);
				retMap.put("partyBusinessRole", EntityUtil.getFirst(partyBusinessRoles));
			}else{
				retMap.put("isPermisson", false);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateCurrentResidence(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin;
		String partyId = (String) context.get("partyId");
		String currResidence_countryGeoId = (String) context.get("currResidence_countryGeoId");
		String currResidence_stateProvinceGeoId = (String) context.get("currResidence_stateProvinceGeoId");
		String currResidence_districtGeoId = (String) context.get("currResidence_districtGeoId");
		String currResidence_wardGeoId = (String) context.get("currResidence_wardGeoId");
		String currentResidenceContactmechId = (String)context.get("currentResidenceContactmechId");
		String address1_CurrResidence = (String)context.get("address1_CurrResidence");
		Map<String, Object> retMap = FastMap.newInstance();
		String contactMechId = "";
		Map<String,Object> results;
		
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(currentResidenceContactmechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("CURRENT_RESIDENCE");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();
				GenericValue geoState = delegator.findOne("Geo", UtilMisc.toMap("geoId",currResidence_stateProvinceGeoId), false);
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", currentResidenceContactmechId);
				ctxMap.put("countryGeoIdstateProvinceGeoId", currResidence_countryGeoId);
				ctxMap.put("stateProvinceGeoId", currResidence_stateProvinceGeoId);
				ctxMap.put("districtGeoId", currResidence_districtGeoId);
				ctxMap.put("wardGeoId", currResidence_wardGeoId);
				ctxMap.put("city", geoState.getString("geoName"));
				ctxMap.put("address1", address1_CurrResidence);
				ctxMap.put("postalCode", "10000");
				results = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap; 
	}
	
	public static Map<String, Object> updatePermanentResidence(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin;
		String partyId = (String) context.get("partyId");
		String address1_PermanentResidence = (String) context.get("address1_PermanentResidence");
		String permanentResidence_countryGeoId = (String) context.get("permanentResidence_countryGeoId");
		String permanentResidence_stateProvinceGeoId = (String) context.get("permanentResidence_stateProvinceGeoId");
		String permanentResidence_districtGeoId = (String) context.get("permanentResidence_districtGeoId");
		String permanentResidence_wardGeoId = (String) context.get("permanentResidence_wardGeoId");
		String permanentResidenceContactmechId = (String)context.get("permanentResidenceContactmechId");
		Map<String, Object> retMap = FastMap.newInstance();
		String contactMechId = "";
		Map<String,Object> results;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(permanentResidenceContactmechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("PERMANENT_RESIDENCE");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();
				GenericValue geoState = delegator.findOne("Geo", UtilMisc.toMap("geoId",permanentResidence_stateProvinceGeoId), false);
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", permanentResidenceContactmechId);
				ctxMap.put("countryGeoIdstateProvinceGeoId", permanentResidence_countryGeoId);
				ctxMap.put("stateProvinceGeoId", permanentResidence_stateProvinceGeoId);
				ctxMap.put("districtGeoId", permanentResidence_districtGeoId);
				ctxMap.put("wardGeoId", permanentResidence_wardGeoId);
				ctxMap.put("city", geoState.getString("geoName"));
				ctxMap.put("address1", address1_PermanentResidence);
				ctxMap.put("postalCode", "10000");
				results = dispatcher.runSync("updatePartyPostalAddress", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap; 
	}
	
	public static Map<String, Object> updateHomeMobile(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin;
		String partyId = (String) context.get("partyId");
		String phone_home = (String) context.get("phone_home");
		String homePhoneContactMechId = (String)context.get("homePhoneContactMechId");
		String contactMechId = "";
		Map<String,Object> results;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(homePhoneContactMechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("PHONE_HOME");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();				
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", homePhoneContactMechId);
				ctxMap.put("contactNumber", phone_home);
				results = dispatcher.runSync("updatePartyTelecomNumber", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap;
	}
	
	public static Map<String, Object> updatePhoneMobile(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin;
		String contactMechId = "";
		Map<String,Object> results;
		String partyId = (String) context.get("partyId");
		String phone_mobile = (String)context.get("phone_mobile");
		String mobilePhoneContactMechId = (String)context.get("mobilePhoneContactMechId");
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(mobilePhoneContactMechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("PHONE_MOBILE");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();				
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", mobilePhoneContactMechId);
				ctxMap.put("contactNumber", phone_mobile);
				results = dispatcher.runSync("updatePartyTelecomNumber", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap;
	}
	
	public static Map<String, Object> updatePrimaryEmail(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin;
		String partyId = (String) context.get("partyId");
		String primaryEmailAddress = (String) context.get("primaryEmailAddress");
		String primaryEmailContactmechId = (String)context.get("primaryEmailContactmechId");
		String contactMechId = "";
		Map<String,Object> results;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(primaryEmailContactmechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("PRIMARY_EMAIL");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();				
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", primaryEmailContactmechId);
				ctxMap.put("emailAddress", primaryEmailAddress);
				results = dispatcher.runSync("updatePartyEmailAddress", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap;
	}
	
	public static Map<String, Object> updateOtherEmail(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin;
		String partyId = (String) context.get("partyId");
		String otherEmailAddress = (String) context.get("otherEmailAddress");
		String otherEmailContactmechId = (String)context.get("otherEmailContactmechId");
		String contactMechId = "";
		Map<String,Object> results;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(UtilValidate.isEmpty(otherEmailContactmechId)){
				results = dispatcher.runSync("createEmplContactInfo", ServiceUtil.setServiceFields(dispatcher, "createEmplContactInfo", context, userLogin, timeZone, locale));
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("OTHER_EMAIL");
				}
			}else{
				Map<String, Object> ctxMap = FastMap.newInstance();				
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechId", otherEmailContactmechId);
				ctxMap.put("emailAddress", otherEmailAddress);
				results = dispatcher.runSync("updatePartyEmailAddress", ctxMap);
				if(ServiceUtil.isSuccess(results)){
					contactMechId = (String)results.get("contactMechId");
				}
			}
		}catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        retMap.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage("PartyErrorUiLabels", "person.update.success", locale));
        retMap.put("contactMechId", contactMechId);
        return retMap;
	}
	
	public static Map<String, Object> updatePersonalImage(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}else{
			Security security = dctx.getSecurity();
			if (!security.hasEntityPermission("HR_PROFILE", "_ADMIN", userLogin) && !userLogin.getString("partyId").equals(partyId)) {
				return ServiceUtil.returnError("you have not permission to update");
			}
		}
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "hrmdoc";
		GenericValue systemUserLogin;
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("public", "Y");
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			if(!ServiceUtil.isSuccess(resultService)){
	        	return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
	        }
			String path = (String)resultService.get("path");
			
			Map<String, Object> dataResourceCtx = FastMap.newInstance();
			dataResourceCtx.put("objectInfo", path);
	        dataResourceCtx.put("dataResourceName", uploadFileNameStr);
	        dataResourceCtx.put("userLogin", systemUserLogin);
	        dataResourceCtx.put("dataResourceTypeId", "IMAGE_OBJECT");
	        dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
	        dataResourceCtx.put("isPublic", "Y");
	        resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
	        if(!ServiceUtil.isSuccess(resultService)){
	        	return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
	        }
	        String dataResourceId = (String) resultService.get("dataResourceId");
			
	        Map<String, Object> contentCtx = FastMap.newInstance();
	        contentCtx.put("dataResourceId", dataResourceId);
	        contentCtx.put("contentTypeId", "DOCUMENT");
	        contentCtx.put("contentName", uploadFileNameStr);
	        contentCtx.put("userLogin", systemUserLogin);
	        resultService = dispatcher.runSync("createContent", contentCtx);
	        if(!ServiceUtil.isSuccess(resultService)){
	        	return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
	        }
	        String contentId = (String)resultService.get("contentId");
			Map<String, Object> partyContentMap = FastMap.newInstance();
			partyContentMap.put("partyId", partyId);
			partyContentMap.put("contentId", contentId);
			partyContentMap.put("partyContentTypeId", "LGOIMGURL");
			partyContentMap.put("userLogin", systemUserLogin);
	        resultService = dispatcher.runSync("createPartyContent", partyContentMap);
	        if(!ServiceUtil.isSuccess(resultService)){
	        	return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
	        }
			retMap.put("contentUrl", path);
			retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEmplDeptAndEmplPosition(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String emplPositionTypeOldId = (String)context.get("emplPositionTypeOldId");
		String newInternalOrgId = (String)context.get("newInternalOrgId");
		String emplPositionTypeNewId = (String)context.get("emplPositionTypeNewId");
		Timestamp dateLeave = (Timestamp)context.get("dateLeave");
		Timestamp dateMoveTo = (Timestamp)context.get("dateMoveTo");
		try {
			//expire current department of employee
			List<EntityCondition> employmentConds = FastList.newInstance();
			employmentConds.add(EntityCondition.makeCondition("partyIdTo", partyId));
			employmentConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
			employmentConds.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
			employmentConds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> employmentList = delegator.findList("Employment", EntityCondition.makeCondition(employmentConds), null, null, null, false);
			for(GenericValue tempGv: employmentList){
				tempGv.set("thruDate", dateLeave);
				tempGv.store();
			}
			//set new department for employee
			GenericValue newEmployment = delegator.makeValue("Employment");
			newEmployment.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			newEmployment.set("roleTypeIdTo", "EMPLOYEE");
			newEmployment.set("partyIdTo", partyId);
			newEmployment.set("partyIdFrom", newInternalOrgId);
			newEmployment.set("fromDate", dateMoveTo);
			newEmployment.create();
			
			//expire curr emplPositionFulfillment
			List<EntityCondition> emplPosFulfillConds = FastList.newInstance();
			emplPosFulfillConds.add(EntityCondition.makeCondition("employeePartyId", partyId));
			emplPosFulfillConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeOldId));
			emplPosFulfillConds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> emplPosFul = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(emplPosFulfillConds), null, null, null, false);
			for(GenericValue tempGv: emplPosFul){
				GenericValue tempEmplPosFul = delegator.findOne("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId, "emplPositionId", tempGv.getString("emplPositionId"), "fromDate", tempGv.getTime("fromDate")), false);
				tempEmplPosFul.set("thruDate", dateLeave);
				tempEmplPosFul.store();
			}
			
			//create new emplPositon
			//FIXME maybe get emplPosition is not fill in newInternalOrgId
			GenericValue newEmplPosition = delegator.makeValue("EmplPosition");
			newEmplPosition.set("partyId", newInternalOrgId);
			newEmployment.set("emplPositionTypeId", emplPositionTypeNewId);
			newEmployment.set("actualFromDate", dateMoveTo);
			String emplPositionId = delegator.getNextSeqId("EmplPositionId");
			newEmployment.set("emplPositionId", emplPositionId);
			newEmployment.create();
			
			//create new EmplPositionFulfillment
			GenericValue newEmplPosFulfillment = delegator.makeValue("EmplPositionFulfillment");
			newEmplPosFulfillment.set("emplPositionId", emplPositionId);
			newEmplPosFulfillment.set("partyId", partyId);
			newEmplPosFulfillment.set("fromDate", dateMoveTo);
			newEmplPosFulfillment.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updatePartyApprovalJobTransfer(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplProposalId = (String)context.get("emplProposalId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> resultService = FastMap.newInstance();
		//String partyId = userLogin.getString("partyId");
		try {
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, timeZone, locale));
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindProposal", UtilMisc.toMap("emplProposalId", emplProposalId), locale));
			}
			String currStatus = emplProposal.getString("statusId");
			if("PPSL_REJECTED".equals(currStatus) || "PPSL_ACCEPTED".equals(currStatus)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatus), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotApprovalProposalDone", UtilMisc.toMap("status", statusItem.getString("description")), locale));
			}
			String partyIdApprover = userLogin.getString("partyId");
			
			List<EntityCondition> conditions = FastList.newInstance();
			//conditions.add(EntityCondition.makeCondition("partyId", partyId));
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition commonConds = EntityCondition.makeCondition(conditions);
			List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", commonConds, null, null, null, false);
			List<GenericValue> partyEmplProposalRoleType = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition(
					EntityCondition.makeCondition("partyId", partyIdApprover),
					EntityOperator.AND,
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove)));
			
			String statusIdEmplPpsl = null;
			List<GenericValue> allPartyApproved = delegator.findList("EmplProposalApprovalAndRoleType",EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyIdApprover)), null, null, null, false);
			for(GenericValue tempGv: partyEmplProposalRoleType){
				List<GenericValue> partyApproverProposal = EntityUtil.filterByCondition(allPartyApproved, EntityCondition.makeCondition("roleTypeId", tempGv.getString("roleTypeId")));
				if(UtilValidate.isNotEmpty(partyApproverProposal)){
					for(GenericValue tempApprPpsl: partyApproverProposal){
						resultService = dispatcher.runSync("updateEmplProposalApproval", UtilMisc.toMap("emplProposalApprovalId", tempApprPpsl.getString("emplProposalApprovalId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
					}
				}else{
					resultService = dispatcher.runSync("createEmplProposalApproval", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyIdApprover, "roleTypeId", tempGv.getString("roleTypeId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
				}
				if(resultService.get("statusId") != null){
					statusIdEmplPpsl = (String)resultService.get("statusId");
				}
			}
			List<GenericValue> emplProposedRoleType = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition("roleTypeId", "PPSL_PROPOSED"));
			String proposedId = null;
			String proposedName = "";
			String proposer = PartyHelper.getPartyName(delegator, emplProposal.getString("partyId"), false);
			if(UtilValidate.isNotEmpty(emplProposedRoleType)){
				proposedId = emplProposedRoleType.get(0).getString("partyId");
				proposedName = PartyHelper.getPartyName(delegator, proposedId , false);
			}
			Map<String, Object> ntfCtx = FastMap.newInstance();
			boolean doneEmplProposalProcess = false;
			if("PPSL_REJECTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "JobTransferProposalReject", UtilMisc.toMap("proposed", proposedName), locale));
				doneEmplProposalProcess = true;				
			}else if("PPSL_ACCEPTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "JobTransferProposalAccept", UtilMisc.toMap("proposed", proposedName), locale));
				doneEmplProposalProcess = true;
			}
			if(doneEmplProposalProcess){
				ntfCtx.put("state", "open");
				ntfCtx.put("action", "JobTransProposalApproval");
				ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "ONE");
				List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
				partyNtf.add(emplProposal.getString("partyId"));
				ntfCtx.put("partiesList", partyNtf);
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				resultService = dispatcher.runSync("getNextRoleTypeLevelApprProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "userLogin", userLogin));
				String roleTypeId = (String)resultService.get("roleTypeId");
				if(roleTypeId != null){
					String proposerId = emplProposal.getString("partyId");
					String header = UtilProperties.getMessage("EmploymentUiLabels", "ApprovalSackingProposalEmpl", UtilMisc.toMap("proposer", proposer, "proposed", proposedName), locale);
					if(proposerId.equals(proposedId)){
						header = UtilProperties.getMessage("EmployeeUiLabels", "ApprovalJobTransferSelfPPSL", UtilMisc.toMap("proposedName", proposedName), locale);
					}else{
						String proposerName = PartyHelper.getPartyName(delegator, proposerId, false);
						header = UtilProperties.getMessage("EmployeeUiLabels", "ApprovalJobTransferPPSL", UtilMisc.toMap("proposerName", proposerName, "proposedName", proposedName), locale);
					}
					dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("roleTypeId", roleTypeId, "emplProposalId", emplProposalId, "header", header, 
																					"targetLink", "emplProposalId=" + emplProposalId, "action", "JobTransProposalApproval",
																					"userLogin", userLogin));
				}
			}
			/*Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("partyId", partyId);
			serviceCtx.put("jobTransferProposalId", jobTransferProposalId);
			serviceCtx.put("approvalStatusId", context.get("approvalStatusId"));
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("comments", context.get("comments"));
			for(GenericValue tempGv: jobTransferApprovalRoleType){
				serviceCtx.put("roleTypeId", tempGv.getString("roleTypeId"));
				tempGv.set("isApproved", "Y");
				tempGv.store();
				dispatcher.runSync("createJobTransferApproval", serviceCtx);
			}*/
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "updateApprovalStatusSuccessful", locale));
	}

	public static Map<String, Object> createEmplTerminationPpsl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId),
																	EntityJoinOperator.AND,
																	EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "TER_PPSL_REJECTED"));
		Map<String, Object> retMap = ServiceUtil.returnSuccess(); 
		try {
			List<GenericValue> emplTerminationPpsl = delegator.findList("EmplTerminationProposal", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(emplTerminationPpsl)){
				GenericValue emplTerPpsl = emplTerminationPpsl.get(0);
				String statusId = emplTerPpsl.getString("statusId");
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "CannotCreateEmplTermination", UtilMisc.toMap("status", statusItem.getString("description")), locale));
			}
			GenericValue emplTerminationProposal = delegator.makeValue("EmplTerminationProposal");
			emplTerminationProposal.setNonPKFields(context);
			String emplTerminationProposalId = delegator.getNextSeqId("EmplTerminationProposal");
			emplTerminationProposal.set("emplTerminationProposalId", emplTerminationProposalId);
			emplTerminationProposal.set("createdDate", UtilDateTime.nowTimestamp());
			emplTerminationProposal.create();
			retMap.put("emplTerminationProposalId", emplTerminationProposalId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTerminationPpslList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	listSortFields.add("-createdDate");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
		try {
			listIterator = delegator.find("EmplTerminationProposalAndPerson", tmpConditon, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	/*public static Map<String, Object> createWorkFlowApprTermination(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");		
		try {
			Map<String, Object> resultService = dispatcher.runSync("createWorkFlowProcess", UtilMisc.toMap("processName", "termination application approval process", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				String processId = (String)resultService.get("processId");
				Map<String, Object> workFlowReqestMap = FastMap.newInstance();
				workFlowReqestMap.put("processId", processId);
				workFlowReqestMap.put("description", "Request approval termination proposal");
				workFlowReqestMap.put("dateRequested", UtilDateTime.nowTimestamp());
				workFlowReqestMap.put("partyId", userLogin.getString("partyId"));
				workFlowReqestMap.put("userLogin", userLogin);
				resultService = dispatcher.runSync("createWorkFlowRequest", workFlowReqestMap);
				String requestId = (String)resultService.get("requestId");
				
				//create request att
				WorkFlowUtils.createRequestAttr(delegator, requestId, "targetLink", "emplTerminationProposalId=" + emplTerminationProposalId);
				WorkFlowUtils.createRequestAttr(delegator, requestId, "action", "emplTerminationList");
				WorkFlowUtils.createRequestAttr(delegator, requestId, "emplTerminationProposalId", emplTerminationProposalId);
				GenericValue emplTerminationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId),false);
				String partyId = emplTerminationProposal.getString("partyId");
				GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
				if(department != null){					
					String directMgrId = PartyUtil.getManagerOfEmpl(delegator, partyId);			
					String nextDirectMgrId = PartyUtil.getManagerOfEmpl(delegator, directMgrId);
					String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
					String ceoId = PartyUtil.getCEO(delegator);
					Map<String, Integer> partyWorkFlowLevel = new HashMap<String, Integer>();
					int level = 1;
					partyWorkFlowLevel.put(directMgrId, level);
					level++;
					if(nextDirectMgrId != null){
						partyWorkFlowLevel.put(nextDirectMgrId, level);
						level++;
					}
					partyWorkFlowLevel.put(hrmAdmin, level);
					level++;
					partyWorkFlowLevel.put(ceoId, level);
					//create common state
					String startState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_START", "start", "startTer");
					String completeState = WorkFlowUtils.createProcessState(delegator, processId, "WORK_FLOW_COMPPLETE", "start", "completeState");
					WorkFlowUtils.buildWorkFlow(delegator, processId, requestId, partyWorkFlowLevel, startState, completeState);
					
					Set<String> stakeHolder = FastSet.newInstance();
					stakeHolder.add(userLogin.getString("partyId"));
					WorkFlowUtils.createWorkFlowRequestStakeHolder(delegator, requestId, stakeHolder);	
					dispatcher.runSync("updateWorkFlowRequest", UtilMisc.toMap("requestId", requestId, "processStatusId", startState, "userLogin", userLogin));
				}else{
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "EmployeeNotBelongDepartment", UtilMisc.toMap("partyId", partyId), locale));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	public static Map<String, Object> expireEmplRelationship(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		try {
			GenericValue emplTerminationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposal", emplTerminationProposalId), false);
			if(emplTerminationProposal == null){
				return ServiceUtil.returnError("could not found termination proposal");
			}
			Timestamp dateTermination = emplTerminationProposal.getTimestamp("dateTermination");
			String partyId = emplTerminationProposal.getString("partyId");
			//expire in Employment
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
			conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
			List<GenericValue> employment = delegator.findList("Employment", EntityCondition.makeCondition(conditions), null, null, null, false);
			for(GenericValue tempGv: employment){
				tempGv.set("thruDate", dateTermination);
				tempGv.store();
			}
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", partyId),
							EntityJoinOperator.OR,
							EntityCondition.makeCondition("partyIdFrom", partyId)));
			List<GenericValue> partyRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
			for(GenericValue tempGv: partyRel){
				Timestamp thruDate = tempGv.getTimestamp("thruDate");
				if(thruDate == null || thruDate.after(dateTermination)){
					tempGv.set("thruDate", dateTermination);
					tempGv.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyFixedAssetAssignment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	if(partyId == null){
    		partyId = userLogin.getString("partyId");
    	}
    	mapCondition.put("partyId", partyId);
    	mapCondition.put("statusId", "PRTYASGN_ASSIGNED");
    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("PartyFixedAssetAssignmentAndFixedAsset", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling listPartyFixedAssetsAssignmentsJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getPartyTerminationAssetAssignment(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		
		int start = size * page;
		int end = start + size;
    	int totalRows = 0;
    	String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
    	if(emplTerminationProposalId != null){
    		GenericValue emplTerminationProposal;
			try {
				emplTerminationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
				if(emplTerminationProposal != null){
	    			String partyId = emplTerminationProposal.getString("partyId");
	    			List<EntityCondition> conditions = FastList.newInstance();
	        		conditions.add(EntityCondition.makeCondition("partyId", partyId));
	        		conditions.add(EntityCondition.makeCondition("statusId", "PRTYASGN_ASSIGNED"));	
	        		Timestamp createdDate = emplTerminationProposal.getTimestamp("createdDate");
	        		//EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);	        		
	        		conditions.add(EntityUtil.getFilterByDateExpr(createdDate));
	        		List<GenericValue> listAsset = delegator.findList("PartyFixedAssetAssignmentAndFixedAsset", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
	        		totalRows = listAsset.size();
	        		if(end > listAsset.size()){
	    				end = listAsset.size();
	    			}
	        		listAsset = listAsset.subList(start, end);
	        		for(GenericValue assets: listAsset){
	        			Map<String, Object> tempMap = FastMap.newInstance();
	        			String roleTypeId = assets.getString("roleTypeId");
	        			tempMap.put("partyId", assets.getString("partyId"));
	        			tempMap.put("fixedAssetId", assets.getString("fixedAssetId"));
	        			tempMap.put("fixedAssetName", assets.getString("fixedAssetName"));
	        			tempMap.put("statusId", assets.getString("statusId"));
	        			tempMap.put("comments", assets.getString("comments"));
	        			if(roleTypeId != null){
	        				GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);
	        				tempMap.put("roleTypeId", roleType.get("description"));
	        			}
	        			tempMap.put("fromDate", assets.getTimestamp("fromDate").getTime());
	        			Timestamp thruDate = assets.getTimestamp("thruDate");
	        			if(thruDate != null){
	        				tempMap.put("thruDate", thruDate.getTime());
	        			}
	        			listReturn.add(tempMap);
	        		}
	    		}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
    	}
    	retMap.put("listReturn", listReturn);
    	retMap.put("TotalRows", String.valueOf(totalRows));
    	return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyInvoiceAR(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if(partyId == null){
    		partyId = userLogin.getString("partyId");
    	}
    	listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "SALES_INVOICE"));
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("INVOICE_IN_PROCESS", "INVOICE_READY")));
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<Map<String, Object>> listReturn = FastList.newInstance();
    	try {
			List<GenericValue> listInvoice = delegator.findList("InvoiceAndType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			if(end > listInvoice.size()){
				end = listInvoice.size();
			}
			totalRows = listInvoice.size();
			listInvoice = listInvoice.subList(start, end);
			for(GenericValue invoice: listInvoice){
				Map<String, Object> tempMap = FastMap.newInstance();
				String invoiceId = invoice.getString("invoiceId");
				Timestamp invoiceDate = invoice.getTimestamp("invoiceDate");
				tempMap.put("invoiceId", invoiceId);
				tempMap.put("invoiceTypeId", invoice.getString("invoiceTypeId"));
				tempMap.put("invoiceDate", invoiceDate != null? invoiceDate.getTime(): null);
				tempMap.put("statusId", invoice.getString("statusId"));
				tempMap.put("currencyUomId", invoice.getString("currencyUomId"));
				tempMap.put("description", invoice.getString("description"));
				String partyIdFrom = invoice.getString("partyIdFrom");
				tempMap.put("partyIdFrom", PartyHelper.getPartyName(delegator, partyIdFrom, false));
				BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator, invoiceId));
				BigDecimal amountToApply = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator, invoiceId));
				tempMap.put("total", total);
				tempMap.put("amountToApply", amountToApply);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
    	successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	
	public static Map<String, Object> getPartyTerminationInvoicePaid(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listInvoice = FastList.newInstance();
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	int totalRows = 0;
    	int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		
		int start = size * page;
		int end = start + size;
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		if(emplTerminationProposalId != null){
			try {
				GenericValue emplTerminationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
				if(emplTerminationProposal != null){
					String partyId = emplTerminationProposal.getString("partyId");
					//Timestamp createdDate = emplTerminationProposal.getTimestamp("createdDate");
					//EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
					/*DynamicViewEntity dynamicView = new DynamicViewEntity();
					dynamicView.addMemberEntity("INVSTT", "InvoiceStatus");
					dynamicView.addAlias("INVSTT", "invoiceId", null, null, null, true, null);
					dynamicView.addAlias("INVSTT", "statusDateMax", "statusDate", null, null, false, "max");
					dynamicView.addAlias("INVSTT", "statusDate", "statusDate", null, null, false, null);*/
					List<EntityCondition> listAllConditions = FastList.newInstance();
					listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			    	listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "SALES_INVOICE"));
			    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("INVOICE_IN_PROCESS", "INVOICE_READY")));
			    	
			    	//EntityCondition statusDateTimeConds = EntityCondition.makeCondition("statusDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, createdDate);
			    	//EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toList("INVOICE_IN_PROCESS", "INVOICE_READY"));
			    	
			    	/*EntityListIterator listIterator = delegator.findListIteratorByCondition(dynamicView, statusDateTimeConds, null, 
			    			UtilMisc.toSet("invoiceId", "statusDateMax"), null, opts);*/
			    	
			    	/*List<GenericValue> listInvoiceSttDateTime = listIterator.getCompleteList();
			    	listIterator.close();*/
			    	//List<String> tempInvoiceList = EntityUtil.getFieldListFromEntityList(listInvoiceSttDateTime, "invoiceId", true);
			    	//listInvoice = delegator.findList("InvoiceStatus", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			    	//tempInvoiceList = EntityUtil.getFieldListFromEntityList(listInvoiceStatus, "invoiceId", true);
			    	//listAllConditions.add(EntityCondition.makeCondition("invoiceId", EntityJoinOperator.IN, tempInvoiceList));
			    	
			    	listInvoice = delegator.findList("InvoiceAndType", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
					if(end > listInvoice.size()){
						end = listInvoice.size();
					}
					totalRows = listInvoice.size();
					listInvoice = listInvoice.subList(start, end);
					for(GenericValue invoice: listInvoice){
						Map<String, Object> tempMap = FastMap.newInstance();
						String invoiceId = invoice.getString("invoiceId");
						Timestamp invoiceDate = invoice.getTimestamp("invoiceDate");
						tempMap.put("invoiceId", invoiceId);
						tempMap.put("invoiceTypeId", invoice.getString("invoiceTypeId"));
						tempMap.put("invoiceDate", invoiceDate != null? invoiceDate.getTime(): null);
						tempMap.put("statusId", invoice.getString("statusId"));
						tempMap.put("currencyUomId", invoice.getString("currencyUomId"));
						tempMap.put("description", invoice.getString("description"));
						String partyIdFrom = invoice.getString("partyIdFrom");
						tempMap.put("partyIdFrom", PartyHelper.getPartyName(delegator, partyIdFrom, false));
						BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator, invoiceId));
						BigDecimal amountToApply = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator, invoiceId));
						tempMap.put("total", total);
						tempMap.put("amountToApply", amountToApply);
						listReturn.add(tempMap);
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
    	successResult.put("listReturn", listReturn);
    	successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyPaymentAR(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if(partyId == null){
    		partyId = userLogin.getString("partyId");
    	}
    	listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyId));
    	listAllConditions.add(EntityCondition.makeCondition("parentTypeId", "RECEIPT"));
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "PMNT_CANCELLED"));
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			List<GenericValue> listPayment = delegator.findList("PaymentAndTypeAndCreditCard", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			if(end > listPayment.size()){
				end = listPayment.size();
			}
			totalRows = listPayment.size();
			listPayment = listPayment.subList(start, end);
			for(GenericValue payment: listPayment){
				Map<String, Object> tempMap = FastMap.newInstance();
				String paymentId = payment.getString("paymentId");
				tempMap.put("paymentId", paymentId);
				tempMap.put("paymentTypeId", payment.getString("paymentTypeId"));
				tempMap.put("statusId", payment.getString("statusId"));
				tempMap.put("comments", payment.getString("comments"));
				tempMap.put("currencyUomId", payment.getString("currencyUomId"));
				Timestamp effectiveDate = payment.getTimestamp("effectiveDate");
				tempMap.put("effectiveDate", effectiveDate != null? effectiveDate.getTime(): null);
				String partyIdTo = payment.getString("partyIdTo");
				if(partyIdTo != null){
					tempMap.put("partyIdTo", PartyHelper.getPartyName(delegator, partyIdTo, false));
				}
				BigDecimal amountToApply = PaymentWorker.getPaymentNotApplied(delegator, paymentId); 
				tempMap.put("amountToApply", amountToApply);
				tempMap.put("amount", payment.getBigDecimal("amount"));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> getPartyTerminationPaymentPaid(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listInvoice = FastList.newInstance();
    	int totalRows = 0;
    	successResult.put("listReturn", listInvoice);
    	successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> updatePersonOlbius(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
			context.put("partyId", partyId);
		}
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			dispatcher.runSync("updatePerson", ServiceUtil.setServiceFields(dispatcher, "updatePerson", context, system, (TimeZone)context.get("timeZone"), (Locale)context.get("locale")));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	/* get addrresses of party group by partyId*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyGroupAddress(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("contactMechId ASC");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS"));
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PartyPostalAddressPurposeGeo", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/* get email of party group by partyId*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyGroupEmail(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context	
				.get("listSortFields");
		listSortFields.add("contactMechId ASC");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition("contactMechTypeId", "EMAIL_ADDRESS"));
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PartyContactMechPurposeType", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/* get telecom of party group by partyId*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyGroupTelecom(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("contactMechId ASC");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PartyTelecomNumberPurpose", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> deletePartyContact(DispatchContext ctx, Map<String, Object> context){
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			ModelService deleteCtm = ctx.getModelService("deletePartyContactMech");
			Map<String, Object> ctmMap = deleteCtm.makeValid(context, ModelService.IN_PARAM);
			Map<String, Object> cond1 = UtilMisc.toMap("partyId", (String) context.get("partyId"), "contactMechId", (String) context.get("contactMechId"),"fromDate", (Timestamp) context.get("fromDate"));
			GenericValue ctm = delegator.findOne("PartyContactMech", cond1, false);
			if(ctm != null){
				dispatcher.runSync("deletePartyContactMech", ctmMap);
			}
			ModelService deleteCtmp = ctx.getModelService("deletePartyContactMechPurpose");
			Map<String, Object> ctmpMap = deleteCtmp.makeValid(context, ModelService.IN_PARAM);
			cond1.put("contactMechPurposeTypeId", (String) context.get("contactMechPurposeTypeId"));
			GenericValue ctmp = delegator.findOne("PartyContactMechPurpose", cond1, false);
			if(ctmp != null){
				dispatcher.runSync("deletePartyContactMechPurpose", ctmpMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return res;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTerminationList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyGroupId = request.getParameter("partyGroupId");
    	
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
    	listAllConditions.add(EntityCondition.makeCondition("workingStatusId", EntityJoinOperator.NOT_EQUAL, "EMPL_WORKING"));
    	listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null));
    	listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
    	listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
    	EntityListIterator listIterator = null;
    	try {
    		if(partyGroupId == null){
        		partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        	}
    		Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
    		List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
    		List<String> allDeptId = null;
    		if(UtilValidate.isNotEmpty(allDept)){
    			allDeptId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
    		}else{
    			allDeptId = FastList.newInstance();
    		}
    		allDeptId.add(partyGroupId);
    		listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityJoinOperator.IN, allDeptId));
			listIterator = delegator.find("EmploymentAndPersonTermination", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	retMap.put("listIterator", listIterator);
		return retMap;
	}
	@SuppressWarnings("unchecked")
	public static Map<String,Object> updatePointForEmp(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		String listdata = (String) context.get("listdata");
		JSONArray arr = new JSONArray();
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		boolean isAdmin = PartyUtil.isAdmin(partyId, delegator);
		try {
			String fullName = "";
			String fullNameRated = "";
			String partyIdRated = "";
			String stageRatingId = "";
			String stageRatingName = "";
			if(UtilValidate.isNotEmpty(listdata)){
				arr = JSONArray.fromObject(listdata);
			}
			if(UtilValidate.isNotEmpty(arr)){
				for(int i=0;i<arr.size();i++){
					JSONObject obj = arr.getJSONObject(i);
					partyIdRated = obj.getString("partyId");
					stageRatingId = obj.getString("stageRatingId");
					GenericValue RatingPerWeight = delegator.findOne("RatingPerWeight", UtilMisc.toMap("stageRatingId", obj.getString("stageRatingId"), "partyId", obj.getString("partyId"), "partyIdFrom", partyId), false);
					if(UtilValidate.isNotEmpty(RatingPerWeight)){
						double r = Double.parseDouble(obj.getString("point"))*RatingPerWeight.getDouble("weight")/100;
						int decimalPlaces = 2;
						BigDecimal bd = new BigDecimal(r);
						bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
						r = bd.doubleValue();
						GenericValue PerStageRatingPoint = delegator.findOne("PerStageRatingPoint", UtilMisc.toMap("stageRatingId", obj.getString("stageRatingId"), "partyId", obj.getString("partyId"), "partyIdFrom", partyId, "criteriaId", obj.getString("criteriaId")), false);
						PerStageRatingPoint.set("point", r);
						PerStageRatingPoint.store();
					}
				}
			}
			List<EntityCondition> listConds1 = FastList.newInstance();
			listConds1.add(EntityCondition.makeCondition("partyId", partyId));
			listConds1.add(EntityCondition.makeCondition("partyTypeId", "PERSON"));
			List<GenericValue> listFullName = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(listConds1, EntityJoinOperator.AND), null, null, opts, false);
			if(listFullName.size() != 0){
				GenericValue GenTmp = EntityUtil.getFirst(listFullName);
				fullName = GenTmp.getString("fullName");
			}
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyId", partyIdRated));
			listConds2.add(EntityCondition.makeCondition("partyTypeId", "PERSON"));
			List<GenericValue> listFullNameRated = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(listConds2, EntityJoinOperator.AND), null, null, opts, false);
			if(listFullNameRated.size() != 0){
				GenericValue GenTmp = EntityUtil.getFirst(listFullNameRated);
				fullNameRated = GenTmp.getString("fullName");
			}
			List<GenericValue> stageRating = delegator.findList("StageOfRating", EntityCondition.makeCondition("stageRatingId", stageRatingId), null,null,opts,false);
			GenericValue stageRate = EntityUtil.getFirst(stageRating);
			if(stageRate.containsKey("stageRatingName")){
				stageRatingName = stageRate.getString("stageRatingName");
			}
			Timestamp tmp = stageRate.getTimestamp("thruDate");
			if(tmp == null || tmp.after(UtilDateTime.nowTimestamp())){
				if(isAdmin){
					return successResult;
				}else{
					String header = fullName + UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "UpdatedStagePointFor", locale) + fullNameRated + "(" + stageRatingName + ")";
					Map<String,Object> MapContext = new HashMap<String, Object>();
					MapContext.put("action", "listEmpPosRating");
					MapContext.put("header", header);
					String HrmAdmin = PartyUtil.getHrmAdmin(delegator);
					MapContext.put("partyId", HrmAdmin);
					MapContext.put("userLogin", userLogin);
					MapContext.put("ntfType", "ONE");
					try {
						dispatcher.runAsync("createNotification", MapContext);
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError("error");
						// TODO: handle exception
					}
				}
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "OverDateToRate", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
	@SuppressWarnings("unused")
	public static Map<String, Object> createNewUserLogin(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CreateSuccess", locale));
		String userLoginId = (String) context.get("userLoginId");
		String currentPassword = (String) context.get("currentPassword");
		String currentPasswordVerify = (String) context.get("currentPasswordVerify");
		String partyId = (String) context.get("partyId");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone= (TimeZone) context.get("timeZone");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		
		try {
			List<GenericValue> UserLogins = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", partyId), null, null, opts, false);
			if(UtilValidate.isNotEmpty(UserLogins)){
				GenericValue UserLoginFirst = EntityUtil.getFirst(UserLogins);
				String UserLoginExists = UserLoginFirst.getString("userLoginId");
				String isEnabled = UserLoginFirst.getString("enabled");
				if(UtilValidate.isNotEmpty(isEnabled)){
					if(isEnabled.equals("N")){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "EmployeeHasHadUserLogin", locale) + "," +  
								UtilProperties.getMessage("BaseHRUiLabels", "AccountWasUnEnabled", locale) + "," + 
								UtilProperties.getMessage("BaseHRUiLabels", "DoYouWantToReEnableIt", locale));
					}
//					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "EmployeeHasHadUserLogin", locale) + ";" + 
//							UtilProperties.getMessage("BaseHRUiLabels", "HRCommonAccount", locale) + ":" + UserLoginExists);
					Map<String,Object> contextNew = ServiceUtil.setServiceFields(dispatcher, "updatePassword", context, userLogin, timeZone, locale);
					contextNew.put("newPassword", currentPassword);
					contextNew.put("newPasswordVerify", currentPassword);
					contextNew.put("userLoginId", UserLoginExists);
					try {
						dispatcher.runSync("updatePassword", contextNew);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
				}else{
					Map<String,Object> contextNew = ServiceUtil.setServiceFields(dispatcher, "updatePassword", context, userLogin, timeZone, locale);
					contextNew.put("newPassword", currentPassword);
					contextNew.put("newPasswordVerify", currentPassword);
					contextNew.put("userLoginId", UserLoginExists);
					try {
						dispatcher.runSync("updatePassword", contextNew);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
				}
			}else{
				GenericValue UserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
				if(UtilValidate.isNotEmpty(UserLogin)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "AccountExisted", locale));
				}else{
					Map<String, Object> resultService = dispatcher.runSync("createUserLogin", 
							UtilMisc.toMap("userLoginId",userLoginId,"enabled", "Y", 
											"currentPassword",currentPassword,
											"currentPasswordVerify", currentPasswordVerify,
											"requirePasswordChange", "Y",
											"partyId", partyId, "userLogin", userLogin));
				}
			}
			GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
			userLoginNew.store();
//			add permissions
			List<GenericValue> PositionTypeOfEmplAtTimes = PartyUtil.getPositionTypeOfEmplAtTime(delegator, partyId, UtilDateTime.nowTimestamp());
			List<String> PositionTypeString = EntityUtil.getFieldListFromEntityList(PositionTypeOfEmplAtTimes, "emplPositionTypeId", true);
			for (String s : PositionTypeString) {
				List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig", 
						UtilMisc.toMap("emplPositionTypeId", s), null, false);
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				for(GenericValue tempGv: emplPosTypeSecGroupConfig){
					String groupId = tempGv.getString("groupId");
					Timestamp DateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
					if(UtilValidate.isNotEmpty(DateJoinCompany)){
						dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginId,
								"groupId", groupId,
								"fromDate", DateJoinCompany,
								"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
								"userLogin", systemUserLogin));
					}else{
						dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", userLoginId,
								"groupId", groupId,
								"fromDate", UtilDateTime.nowTimestamp(),
								"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
								"userLogin", systemUserLogin));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	
	public static String getUserLoginByPartyId(Delegator delegator, String partyId){
		String userLoginId = "";
		try {
			List<GenericValue> UserLogins = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", partyId), null, null, null, false);
			if(UtilValidate.isNotEmpty(UserLogins)){
				GenericValue UserLoginFirst = EntityUtil.getFirst(UserLogins);
				userLoginId = UserLoginFirst.getString("userLoginId");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLoginId;
	}
	public static Map<String,Object> reEnableUserLogin(DispatchContext ctx ,Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		try {
			GenericValue UserLogin = PartyUtil.getUserLoginByParty(delegator, partyId);
			UserLogin.set("enabled", "Y");
			UserLogin.store();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplByRoleTypeOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String orgRoleTypeId = (String)context.get("orgRoleTypeId");
		String emplRoleTypeId = (String)context.get("emplRoleTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Map<String, Object> resultService = null;
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		if(fromDate == null){
			fromDate = UtilDateTime.nowTimestamp();
		}
		List<String> listOrgHasRoleId = SecurityUtil.getPartiesByRoles(orgRoleTypeId, delegator, false);
		Set<String> setEmpl = FastSet.newInstance();
		try {
			String currentOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "getListEmplInPartyGroupByRole", context, userLogin, timeZone, locale);
			ctxMap.put("roleTypeId", emplRoleTypeId);
			for(String orgId: listOrgHasRoleId){
				if(PartyUtil.checkAncestorOfParty(delegator, currentOrgId, orgId, userLogin)){
					ctxMap.put("partyGroupId", orgId);
					resultService = dispatcher.runSync("getListEmplInPartyGroupByRole", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
					List<String> tempEmplListId = (List<String>)resultService.get("listEmployee");
					setEmpl.addAll(tempEmplListId);
				}
			}
			List<String> retList = new ArrayList<String>(setEmpl);
			successResult.put("listEmployee", retList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> getListEmplInPartyGroupByRole(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyGroupId = (String)context.get("partyGroupId");
		String roleTypeId = (String)context.get("roleTypeId");
		if(fromDate == null){
			fromDate = UtilDateTime.nowTimestamp();
		}
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> retList = FastList.newInstance();
			if(roleTypeId != null){
				for(GenericValue party: emplList){
					String partyId = party.getString("partyId");
					if(SecurityUtil.hasRole(roleTypeId, partyId, delegator)){
						retList.add(partyId);
					}
				}
			}else{
				retList = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			}
			successResult.put("listEmployee", retList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> getListEmplMgrByParty(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String userLoginId = (String)context.get("mgrUserLoginId");
		String roleTypeId = (String)context.get("roleTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		if(fromDate == null){
			fromDate = UtilDateTime.nowTimestamp();
		}
		if(thruDate == null){
			thruDate = UtilDateTime.nowTimestamp();
		}
		try {
			List<String> emplList = PartyUtil.getListEmplMgrByParty(delegator, userLoginId, fromDate, thruDate);
			List<String> retList = FastList.newInstance();
			if(roleTypeId != null){
				for(String partyId: emplList){
					if(SecurityUtil.hasRole(roleTypeId, partyId, delegator)){
						retList.add(partyId);
					}
				}
			}else{
				retList = emplList;
			}
			successResult.put("listEmployee", retList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getManagerOfEmpl(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null;
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}
		try {
			List<String> managerListId = PartyUtil.getManagerOfEmpl(delegator, partyId, UtilDateTime.nowTimestamp(), userLogin.getString("userLoginId"));
			int size = Integer.parseInt(parameters.get("pagesize")[0]);
			int page = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = size * page;
			int end = start + size;
			int totalRows = managerListId.size();
			List<Map<String, Object>> listReturn = FastList.newInstance();
			if(end > totalRows){
				end = totalRows;
			}
			managerListId = managerListId.subList(start, end);
			for(String managerId: managerListId){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", managerId);
				tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, managerId));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, managerId));
				List<GenericValue> emplPositionList = PartyUtil.getPositionTypeOfEmplAtTime(delegator, managerId, UtilDateTime.nowTimestamp());
				List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPositionList, "description", true);
				tempMap.put("emplPositionType", StringUtils.join(emplPositionTypeList, ", "));
				listReturn.add(tempMap);
			}
			successResult.put("listIterator", listReturn);
			successResult.put("TotalRows", String.valueOf(totalRows));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> getEmplLeaveInfo(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		Integer year = (Integer)context.get("year");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
		GenericValue emplLeaveRegulation;
		try {
			emplLeaveRegulation = delegator.findOne("EmplLeaveRegulation", UtilMisc.toMap("partyId", partyId, "fromDate", yearStart), false);
			BigDecimal annualLeaveRemain = BigDecimal.ZERO;
			BigDecimal totalDayLeft = EmployeeHelper.getTotalDayLeftOfEmplInYear(delegator, partyId, yearStart, yearEnd);
			if(emplLeaveRegulation != null){
				retMap.put("annualLeaveDayYear", emplLeaveRegulation.get("leaveDayYear"));
				retMap.put("annualLastYearTransferred", emplLeaveRegulation.get("lastYearTransferred"));
				retMap.put("annualGrantedLeaveInYear", emplLeaveRegulation.get("grantedLeave"));
				BigDecimal totalAnnualLeave = EmployeeHelper.getTotalAnnualDayLeaveInYear(emplLeaveRegulation.getBigDecimal("lastYearTransferred"), 
						emplLeaveRegulation.getBigDecimal("leaveDayYear"), emplLeaveRegulation.getBigDecimal("grantedLeave"));
				annualLeaveRemain = totalAnnualLeave.subtract(totalDayLeft);
				if(annualLeaveRemain.compareTo(BigDecimal.ZERO) < 0){
					annualLeaveRemain = BigDecimal.ZERO;
				}
			}
			BigDecimal unpaidLeave = EmployeeHelper.getTotalDayLeftUnpaid(delegator, partyId, yearStart, yearEnd);
			retMap.put("unpaidLeave", unpaidLeave);
			retMap.put("annualLeaveRemain", annualLeaveRemain);
			retMap.put("annualLeft", totalDayLeft);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createNationality(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		String description = (String)context.get("description");
		description = description.trim();
		try {
			List<GenericValue> listNationality = delegator.findByAnd("Nationality", UtilMisc.toMap("description", description), null, false);
			if(UtilValidate.isNotEmpty(listNationality)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "NationalityIsExist", UtilMisc.toMap("description", description), locale));
			}
			GenericValue nationality = delegator.makeValue("Nationality");
			nationality.setNonPKFields(context);
			String nationalityId = delegator.getNextSeqId("Nationality");
			retMap.put("nationalityId", nationalityId);
			nationality.put("nationalityId", nationalityId);
			delegator.create(nationality);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createEthnicOrigin(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		String description = (String)context.get("description");
		description = description.trim();
		try {
			List<GenericValue> listNationality = delegator.findByAnd("EthnicOrigin", UtilMisc.toMap("description", description), null, false);
			if(UtilValidate.isNotEmpty(listNationality)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "EthnicOriginIsExist", UtilMisc.toMap("description", description), locale));
			}
			GenericValue ethnicOrigin = delegator.makeValue("EthnicOrigin");
			ethnicOrigin.setNonPKFields(context);
			String ethnicOriginId = delegator.getNextSeqId("EthnicOrigin");
			retMap.put("ethnicOriginId", ethnicOriginId);
			ethnicOrigin.put("ethnicOriginId", ethnicOriginId);
			delegator.create(ethnicOrigin);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createReligion(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		String description = (String)context.get("description");
		description = description.trim();
		try {
			List<GenericValue> listNationality = delegator.findByAnd("Religion", UtilMisc.toMap("description", description), null, false);
			if(UtilValidate.isNotEmpty(listNationality)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "ReligionIsExist", UtilMisc.toMap("description", description), locale));
			}
			GenericValue religion = delegator.makeValue("Religion");
			religion.setNonPKFields(context);
			String religionId = delegator.getNextSeqId("Religion");
			retMap.put("religionId", religionId);
			religion.put("religionId", religionId);
			delegator.create(religion);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}

	public static Map<String, Object> createNewSalesmanImports(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String sizeProducts = parameters.get("sizeSalesman")[0];
			int len = Integer.parseInt(sizeProducts);
			for (int i=0; i < len; i++){
				String sequence = parameters.get("salesman["+i+"][sequence]")[0];
				String partyCode = parameters.get("salesman["+i+"][partyCode]")[0];
				String lastName = parameters.get("salesman["+i+"][lastName]")[0];
				String middleName = parameters.get("salesman["+i+"][middleName]")[0];
				String firstName = parameters.get("salesman["+i+"][firstName]")[0];
				String gender = parameters.get("salesman["+i+"][gender]")[0];
				String birthdate = parameters.get("salesman["+i+"][birthdate]")[0];
				String idNumber = parameters.get("salesman["+i+"][idNumber]")[0];
				String religion = parameters.get("salesman["+i+"][religion]")[0];
				String idIssueDate = parameters.get("salesman["+i+"][idIssueDate]")[0];
				String idIssuePlace = parameters.get("salesman["+i+"][idIssuePlace]")[0];
				String maritalStatusId = parameters.get("salesman["+i+"][maritalStatusId]")[0];
				String ethnicOrigin = parameters.get("salesman["+i+"][ethnicOrigin]")[0];
				String nationality = parameters.get("salesman["+i+"][nationality]")[0];
				String nativeLand = parameters.get("salesman["+i+"][nativeLand]")[0];
				String partyIdFrom = parameters.get("salesman["+i+"][partyIdFrom]")[0];
				String emplPositionTypeId = parameters.get("salesman["+i+"][emplPositionTypeId]")[0];
				String dateJoinCompany = parameters.get("salesman["+i+"][dateJoinCompany]")[0];
				String salaryBaseFlat = parameters.get("salesman["+i+"][salaryBaseFlat]")[0];
				String periodTypeId = parameters.get("salesman["+i+"][periodTypeId]")[0];
				String userLoginId = parameters.get("salesman["+i+"][userLoginId]")[0];
				String password = parameters.get("salesman["+i+"][password]")[0];
				String isParticipateIns = parameters.get("salesman["+i+"][isParticipateIns]")[0];
				String permanentResStr = parameters.get("salesman["+i+"][permanentRes]")[0];
				String currResStr = parameters.get("salesman["+i+"][currRes]")[0];
				String permanentRes = "{}";;
				String currRes = "{}";
				JSONObject jsonPrem = JSONObject.fromObject(permanentResStr);
				Map<String, Object> mapJsonPrem = EmployeeHelper.getPostalAddressMapFromJson(jsonPrem);
				if (UtilValidate.isNotEmpty(mapJsonPrem)) {
					if (checkValidInputData((String) mapJsonPrem.get("countryGeoId")).get("isValidStatus").equals("notValid") || checkValidInputData((String) mapJsonPrem.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")
							|| checkValidInputData((String) mapJsonPrem.get("districtGeoId")).get("isValidStatus").equals("notValid") || checkValidInputData((String) mapJsonPrem.get("wardGeoId")).get("isValidStatus").equals("notValid")) {
						permanentRes = "{}";
					} else {
						permanentRes = permanentResStr;
					}
				}

				JSONObject jsonCurr = JSONObject.fromObject(currResStr);
				Map<String, Object> mapJsonCurr = EmployeeHelper.getPostalAddressMapFromJson(jsonCurr);
				if (UtilValidate.isNotEmpty(mapJsonCurr)){
					if(checkValidInputData((String) mapJsonCurr.get("countryGeoId")).get("isValidStatus").equals("notValid") || checkValidInputData((String) mapJsonCurr.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")
							|| checkValidInputData((String) mapJsonCurr.get("districtGeoId")).get("isValidStatus").equals("notValid") || checkValidInputData((String) mapJsonCurr.get("wardGeoId")).get("isValidStatus").equals("notValid")){
						currRes = "{}";
					}else{
						currRes = currResStr;
					}
				}

				Map<String, Object> dataCtx = FastMap.newInstance();
				dataCtx.put("partyCode", partyCode);
				dataCtx.put("lastName", lastName);
				dataCtx.put("middleName", middleName);
				dataCtx.put("firstName", firstName);
				dataCtx.put("gender", gender);
				dataCtx.put("birthDate", birthdate);
				dataCtx.put("idNumber", idNumber);
				dataCtx.put("religion", religion);
				dataCtx.put("idIssueDate", idIssueDate);
				dataCtx.put("idIssuePlace", idIssuePlace);
				dataCtx.put("maritalStatusId", maritalStatusId);
				dataCtx.put("ethnicOrigin", ethnicOrigin);
				dataCtx.put("nationality", nationality);
				dataCtx.put("nativeLand", nativeLand);
				dataCtx.put("partyIdFrom", partyIdFrom);
				dataCtx.put("emplPositionTypeId", emplPositionTypeId);
				dataCtx.put("dateJoinCompany", dateJoinCompany);
				dataCtx.put("salaryBaseFlat", salaryBaseFlat);
				dataCtx.put("periodTypeId", periodTypeId);
				dataCtx.put("password", password);
				dataCtx.put("isParticipateIns", isParticipateIns);
				dataCtx.put("permanentRes", permanentRes);
				dataCtx.put("currRes", currRes);
				dataCtx.put("userLoginId", userLoginId);
				dataCtx.put("workingStatusId", "EMPL_WORKING");
				Map<String, String> validate = checkValidateSalesmanFromExcel(dataCtx, locale, delegator, permanentResStr, currResStr);
				if (validate.get("statusValidate") == "false"){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",validate.get("message"));
					dataCtx.put("sequence",sequence);
					listIterator.add(dataCtx);
					continue;
				}
				try {
					dataCtx.put("userLogin", userLogin);
					Map<String, Object>	result = dispatcher.runSync("createSalesmanFromFileExcel", dataCtx);
					if(result.containsKey("partyId")){
						dataCtx.put("statusImport","success");
						dataCtx.put("message", UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
					}else{
						dataCtx.put("statusImport","error");
						dataCtx.put("message",result.get("message"));
					}
				}catch (Exception e){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSErrorImportDataRequireFields", locale));
				}
				dataCtx.put("sequence",sequence);
				listIterator.add(dataCtx);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, String> checkValidInputData(String str){
		Map<String, String> results = FastMap.newInstance();
		if (UtilValidate.isEmpty(str)){
			results.put("isValidStatus", "empty");
		}else{
			if (str.equals("_NA_")){
				results.put("isValidStatus", "notValid");
			}else{
				results.put("isValidStatus", "valid");
			}
		}
		return results;
	}

	public static Map<String, String> checkValidateSalesmanFromExcel(Map<String, Object> dataCtx, Locale locale, Delegator delegator, String prem, String curr){
		Map<String, String> results = FastMap.newInstance();
		String message = "";
		if(checkValidInputData((String) dataCtx.get("partyCode")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRPartyCode", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if (checkValidInputData((String) dataCtx.get("partyCode")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRPartyCode", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}else{
			try {
				List<GenericValue> partyCheck = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", dataCtx.get("partyCode")), null, false);
				if(UtilValidate.isNotEmpty(partyCheck)){
					message += UtilProperties.getMessage("BaseHREmployeeUiLabels", "PartyHaveExists", UtilMisc.toMap("partyId", dataCtx.get("partyCode")), locale) + "\n";
				}
			} catch (Exception e) {
				message += UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRPartyCode", locale) + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}
		}
		if(checkValidInputData((String) dataCtx.get("lastName")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "LastName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if (checkValidInputData((String) dataCtx.get("lastName")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "LastName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("middleName")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "MiddleName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("firstName")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "FirstName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if (checkValidInputData((String) dataCtx.get("firstName")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "FirstName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("gender")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "Gender", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("birthDate")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "BirthDate", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("idNumber")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "IDNumber", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(UtilValidate.isNotEmpty((String) dataCtx.get("religion")) &&  dataCtx.get("religion").toString().equals("_NA")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "Religion", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("idIssueDate")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "IDIssueDate", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("idIssuePlace")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "IDIssuePlace", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("maritalStatusId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "MaritalStatus", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("ethnicOrigin")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "EthnicOrigin", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("nationality")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "Nationality", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("nativeLand")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "NativeLand", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		JSONObject jsonPrem = JSONObject.fromObject(prem);
		Map<String, Object> mapJsonPrem = EmployeeHelper.getPostalAddressMapFromJson(jsonPrem);
		if(UtilValidate.isNotEmpty(mapJsonPrem)){
			String strJ = " (" + UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")";
			if(checkValidInputData((String) mapJsonPrem.get("address1")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PartyAddressLine", locale) + strJ +
						": " + UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonPrem.get("countryGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "DACountryGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonPrem.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyState", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonPrem.get("districtGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyDistrictGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonPrem.get("wardGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyWardGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}
		}
		JSONObject jsonCurr = JSONObject.fromObject(curr);
		Map<String, Object> mapJsonCurr = EmployeeHelper.getPostalAddressMapFromJson(jsonCurr);
		if(UtilValidate.isNotEmpty(mapJsonCurr)){
			String strJ = " (" + UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")";
			if(checkValidInputData((String) mapJsonCurr.get("address1")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PartyAddressLine", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonCurr.get("countryGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "DACountryGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonCurr.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyState", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonCurr.get("districtGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyDistrictGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}else if(checkValidInputData((String) mapJsonCurr.get("wardGeoId")).get("isValidStatus").equals("notValid")){
				message += UtilProperties.getMessage("BaseHRUiLabels", "PartyWardGeoId", locale) + strJ + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}
		}
		if(checkValidInputData((String) dataCtx.get("partyIdFrom")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PartyIdWork", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("partyIdFrom")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PartyIdWork", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("emplPositionTypeId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "JobPosition", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("emplPositionTypeId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "JobPosition", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("partyIdFrom")).get("isValidStatus").equals("valid") &&
				checkValidInputData((String) dataCtx.get("emplPositionTypeId")).get("isValidStatus").equals("valid")){
			String partyIdFrom = (String) dataCtx.get("partyIdFrom");
			String emplPositionTypeId = (String) dataCtx.get("emplPositionTypeId");
			if((partyIdFrom.contains("GT") && emplPositionTypeId.equals("SALES_EXECUTIVE_MT")) || (partyIdFrom.contains("MT") && emplPositionTypeId.equals("SALES_EXECUTIVE_GT"))){
				message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRPartyIdFromAndEmplIsNotValid", locale) + "\n";
			}
		}
		if(checkValidInputData((String) dataCtx.get("dateJoinCompany")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "DateJoinCompany", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("dateJoinCompany")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "DateJoinCompany", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("salaryBaseFlat")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "SalaryBaseFlat", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("salaryBaseFlat")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "SalaryBaseFlat", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("periodTypeId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PeriodTypePayroll", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("periodTypeId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "PeriodTypePayroll", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("userLoginId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "UserLoginID", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("userLoginId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseHRDirectoryUiLabels", "UserLoginID", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}else{
			try {
				GenericValue userLoginTest = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", dataCtx.get("userLoginId")), false);
				if(UtilValidate.isNotEmpty(userLoginTest)){
					message += UtilProperties.getMessage("BaseHRUiLabels", "UserLoginHaveExistsParam", UtilMisc.toMap("userLoginId", dataCtx.get("userLoginId")), locale) + "\n";
				}
			} catch (Exception e) {
				message += UtilProperties.getMessage("BaseHREmployeeUiLabels", "UserLoginID", locale) + ": " +
						UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
			}
		}
		if (message.length()>0){
			results.put("statusValidate", "false");
		}else{
			results.put("statusValidate", "true");
		}
		results.put("message", message);
		return results;
	}

	public static Map<String, Object> createSalesmanFromFileExcel(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> messageError = FastMap.newInstance();
		String partyId;
		try {
			String partyCode = (String) context.get("partyCode");
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPerson", (Map<String, Object>) context, userLogin, null, locale);
			String birthDateStr = (String) ctxMap.get("birthDate");
			if(birthDateStr != null){
				Date birthDate = new Date(Long.parseLong(birthDateStr));
				ctxMap.put("birthDate", birthDate);
			}
			if(ctxMap.get("idIssueDate") != null){
				ctxMap.put("idIssueDate", new Date(Long.parseLong((String) ctxMap.get("idIssueDate"))));
			}
			Timestamp dateJoinCompany = null;
			if(context.get("dateJoinCompany") != null){
				dateJoinCompany = new Timestamp(Long.parseLong((String) context.get("dateJoinCompany")));
			}else{
				dateJoinCompany = UtilDateTime.nowTimestamp();
			}
			dateJoinCompany = UtilDateTime.getDayStart(dateJoinCompany);
			ctxMap.put("dateParticipateIns", null);
			ctxMap.put("workingStatusId", context.get("workingStatusId"));
			Map<String, Object> resultService = dispatcher.runSync("createPerson", ctxMap);
			partyId = (String)resultService.get("partyId");
			PartyUtil.updatePartyCode(delegator, partyId, partyCode);
			String permanentRes = (String) context.get("permanentRes");
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
			String currRes = (String) context.get("currRes");
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
			Map<String, Object> employmentWorkInfo = ServiceUtil.setServiceFields(dispatcher, "createEmploymentWorkInfo", (Map<String, Object>) context, userLogin, timeZone, locale);
			String insuranceTypeNotCompulsory = (String) context.get("insuranceTypeNotCompulsory");
			if(insuranceTypeNotCompulsory != null){
				List<String> insuranceTypeIdList = FastList.newInstance();
				JSONArray insuranceTypeNotCompulsoryJson = JSONArray.fromObject(insuranceTypeNotCompulsory);
				for(int i = 0; i < insuranceTypeNotCompulsoryJson.size(); i++){
					insuranceTypeIdList.add(insuranceTypeNotCompulsoryJson.getString(i));
				}
				employmentWorkInfo.put("insuranceTypeIdList", insuranceTypeIdList);
			}
			employmentWorkInfo.put("fromDate", dateJoinCompany);
			BigDecimal salaryBaseFlat = context.get("salaryBaseFlat")!= null? new BigDecimal((String) context.get("salaryBaseFlat")): BigDecimal.ZERO;
			BigDecimal insuranceSalary = context.get("insuranceSalary")!= null? new BigDecimal((String) context.get("insuranceSalary")): BigDecimal.ZERO;
			employmentWorkInfo.put("rateAmount", salaryBaseFlat);
			employmentWorkInfo.put("insuranceSalary", insuranceSalary);
			employmentWorkInfo.put("partyIdTo", partyId);
			employmentWorkInfo.put("locale", locale);
			employmentWorkInfo.put("timeZone", timeZone);
			employmentWorkInfo.put("userLogin", userLogin);
			resultService = dispatcher.runSync("createEmploymentWorkInfo", employmentWorkInfo);
			//create userLogin
			resultService = dispatcher.runSync("createUserLogin",
					UtilMisc.toMap("userLoginId", context.get("userLoginId"),
							"enabled", "Y", "currentPassword", context.get("password"),
							"currentPasswordVerify", context.get("password"),
							"requirePasswordChange", "Y",
							"partyId", partyId, "userLogin", userLogin));

			GenericValue userLoginNew = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", context.get("userLoginId")), false);
			userLoginNew.set("lastOrg", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
			userLoginNew.store();

			Map<String, Object> ctxEmplPosition = FastMap.newInstance();
			ctxEmplPosition.put("partyId", context.get("partyIdFrom"));
			ctxEmplPosition.put("emplPositionTypeId", context.get("emplPositionTypeId"));
			ctxEmplPosition.put("actualFromDate", new Timestamp(System.currentTimeMillis()));
			ctxEmplPosition.put("actualThruDate", null);
			ctxEmplPosition.put("userLogin", userLogin);
			ctxEmplPosition.put("locale", locale);
			Map<String, Object> emplPositionTypeMap = dispatcher.runSync("createEmplPositionHR", ctxEmplPosition);

			List<GenericValue> emplPosTypeSecGroupConfig = delegator.findByAnd("EmplPosTypeSecGroupConfig",
					UtilMisc.toMap("emplPositionTypeId", (String) emplPositionTypeMap.get("emplPositionTypeId")), null, false);
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			for(GenericValue tempGv: emplPosTypeSecGroupConfig){
				String groupId = tempGv.getString("groupId");
				dispatcher.runSync("addUserLoginToSecurityGroupHR", UtilMisc.toMap("userLoginId", context.get("userLoginId"),
						"groupId", groupId,
						"fromDate", dateJoinCompany,
						"organizationId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
						"userLogin", systemUserLogin));
			}

			if(((String) context.get("emplPositionTypeId")).contains("SALES_EXECUTIVE") || ((String) context.get("emplPositionTypeId")).contains("SALES_EXECUTIVE_MT") || ((String) context.get("emplPositionTypeId")).contains("SALES_EXECUTIVE_GT")) {
				String fullName = (String) context.get("firstName");
				String partyIdfrom = (String) context.get("partyIdFrom");
				if(!UtilValidate.isEmpty(context.get("middleName"))) fullName = context.get("middleName") + " " + fullName;
				if(!UtilValidate.isEmpty(context.get("lastName"))) fullName = context.get("lastName") + " " + fullName;

				dispatcher.runSync("createPartySalesman", UtilMisc.toMap("partyId", partyId,
						"partyCode", partyCode, "fullName", fullName, "statusId", "PARTY_ENABLED",
						"preferredCurrencyUomId", "VND", "departmentId", partyIdfrom, "userLogin", userLogin));
			}
			TransactionUtil.commit();
		} catch (Exception e) {
			Debug.logError(e, module);
			messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
			return messageError;
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	public static List<Map<String, String>> getListOrgManagedByParty(DispatchContext ctx, Map<String, ? extends Object> context){
		List<Map<String, String>> results = null;
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			List<String> rootOrgList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> listManager = null;
			if(rootOrgList.size()>0){
				for(String rootOrgId : rootOrgList){
					GenericValue gv = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", rootOrgId), false);
					if(gv != null){
						listManager.add(gv);
						Map<String, String> map = FastMap.newInstance();
						map.put("partyId", gv.getString("partyId"));
						map.put("partyName", gv.getString("groupName"));
						results.add(map);
					}
				}
			}
			if(listManager != null){
				for (GenericValue gv: listManager){
					List<Map<String, Object>> listChildOfManager = CommonUtil.getListPartyRelByParent(delegator, gv.getString("partyId"));
					Map<String, String> map = FastMap.newInstance();
					map.put("partyId", (String) listChildOfManager.get(0).get("id"));
					map.put("partyName", (String) listChildOfManager.get(0).get("value"));
				}
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return results;
	}

}
