package com.olbius.basehr.employee.helper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employee.services.EmployeeLeaveServices;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class EmployeeHelper {
	public static void createEmployeeFamily(LocalDispatcher dispatcher,
			List<Map<String, Object>> personFamily, GenericValue userLogin, String partyId) throws GenericServiceException {
		for(Map<String, Object> tempMap: personFamily){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("partyId", partyId);
			ctxMap.put("firstNameFamily", tempMap.get("firstName"));
			ctxMap.put("middleNameFamily", tempMap.get("middleName"));
			ctxMap.put("lastNameFamily", tempMap.get("lastName"));
			ctxMap.put("partyRelationshipTypeId", tempMap.get("partyRelationshipTypeId"));
			ctxMap.put("birthDate", tempMap.get("birthDate"));
			ctxMap.put("occupation", tempMap.get("occupation"));
			ctxMap.put("placeWork", tempMap.get("placeWork"));
			ctxMap.put("phoneNumber", tempMap.get("phoneNumber"));
			ctxMap.put("emergencyContact", tempMap.get("emergencyContact"));
			ctxMap.put("userLogin", userLogin);
			dispatcher.runSync("createPersonFamilyBackground", ctxMap);
		}
	}

	public static void createEmployeeEducation(LocalDispatcher dispatcher,
			List<Map<String, Object>> personEducation, GenericValue userLogin, String partyId, TimeZone timeZone, Locale locale) throws GenericServiceException, GeneralServiceException {
		for(Map<String, Object> tempMap: personEducation){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.putAll(tempMap);
			ctxMap.put("partyId", partyId);
			dispatcher.runSync("createPersonEducation", ServiceUtil.setServiceFields(dispatcher, "createPersonEducation", 
					ctxMap, userLogin, timeZone, locale));
		}
	}

	public static void createEmployeeWorkingProcess(LocalDispatcher dispatcher,
			List<Map<String, Object>> personWorkingProcess, GenericValue userLogin, 
			String partyId, TimeZone timeZone, Locale locale) throws GenericServiceException, GeneralServiceException {
		for(Map<String, Object> tempMap: personWorkingProcess){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.putAll(tempMap);
			ctxMap.put("partyId", partyId);
			dispatcher.runSync("createPersonWorkingProcess", ServiceUtil.setServiceFields(dispatcher, "createPersonWorkingProcess", 
					ctxMap, userLogin, timeZone, locale));
		}
	}

	public static void createEmployeeContact(LocalDispatcher dispatcher,
			Map<String, Object> parameterMap, String partyId,
			GenericValue userLogin) throws GenericServiceException {
		Map<String, Object> contactInfoMap = FastMap.newInstance();
		contactInfoMap.put("partyId", partyId);
		contactInfoMap.put("phone_mobile", parameterMap.get("mobile"));
		contactInfoMap.put("phone_home", parameterMap.get("homeTel"));
		contactInfoMap.put("primaryEmailAddress", parameterMap.get("email"));
		contactInfoMap.put("address1_PermanentResidence", parameterMap.get("prAddress"));
		contactInfoMap.put("permanentResidence_countryGeoId", parameterMap.get("prCountry"));
		contactInfoMap.put("permanentResidence_stateProvinceGeoId", parameterMap.get("prProvince"));
		contactInfoMap.put("permanentResidence_districtGeoId", parameterMap.get("prDistrict"));
		contactInfoMap.put("permanentResidence_wardGeoId", parameterMap.get("prWard"));
		contactInfoMap.put("address1_CurrResidence", parameterMap.get("crAddress"));
		contactInfoMap.put("currResidence_countryGeoId", parameterMap.get("crCountry"));
		contactInfoMap.put("currResidence_stateProvinceGeoId", parameterMap.get("crProvince"));
		contactInfoMap.put("currResidence_districtGeoId", parameterMap.get("crDistrict"));
		contactInfoMap.put("currResidence_wardGeoId", parameterMap.get("crWard"));
		contactInfoMap.put("userLogin", userLogin);
		dispatcher.runSync("createEmplContactInfo", contactInfoMap);
	}

	public static String updateEmplTerminationStatus(Delegator delegator,
			String emplTerminationProposalId, String requestId) throws GenericEntityException {
		GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
		String processStatusId = workFlowRequest.getString("processStatusId");
		GenericValue workFlowProcessStatus = delegator.findOne("WorkFlowProcessStatus", UtilMisc.toMap("processStatusId", processStatusId), false);
		String statusId = workFlowProcessStatus.getString("statusId");
		GenericValue emplTermiationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
		String statusUpdateId = null;
		if("WORK_FLOW_COMPPLETE".equals(statusId)){
			emplTermiationProposal.set("statusId", "TER_PPSL_ACCEPTED");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_ACCEPTED";
		}else if("WORK_FLOW_DENIED".equals(statusId)){
			emplTermiationProposal.set("statusId", "TER_PPSL_REJECTED");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_REJECTED";
		}else{
			emplTermiationProposal.set("statusId", "TER_PPSL_IN_PROCESS");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_IN_PROCESS";
		}
		return statusUpdateId;
	}

	public static Map<String, Object> getPostalAddressMapFromJson(JSONObject jsonAddr) {
		Map<String, Object> retMap = FastMap.newInstance();
		if(jsonAddr.has("address1") && jsonAddr.getString("address1").length() > 0){
			retMap.put("address1", jsonAddr.getString("address1"));
			retMap.put("countryGeoId", jsonAddr.getString("countryGeoId"));
			retMap.put("stateProvinceGeoId", jsonAddr.getString("stateProvinceGeoId"));
			if(jsonAddr.has("districtGeoId") && jsonAddr.getString("districtGeoId").length() > 0){
				retMap.put("districtGeoId", jsonAddr.getString("districtGeoId"));
			}
			if(jsonAddr.has("wardGeoId") && jsonAddr.getString("wardGeoId").length() > 0){
				retMap.put("wardGeoId", jsonAddr.getString("wardGeoId"));
			}
			return retMap;
		}else{
			return null;
		}
	}
	
	public static String getEmplPositionNotFullfillment(Delegator delegator, String emplPositionTypeId, String partyId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("employeePartyId", null),
																EntityJoinOperator.AND,
																EntityCondition.makeCondition("fromDate", null));
		EntityCondition cond2 = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null), 
																EntityJoinOperator.AND,
																EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN, fromDate));
		conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition(cond1, EntityJoinOperator.OR, cond2));
		conds.add(EntityCondition.makeCondition("actualFromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
		if(thruDate != null){
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualThruDate", null),
												EntityJoinOperator.OR,
												EntityCondition.makeCondition("actualThruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
		}else{
			conds.add(EntityCondition.makeCondition("actualThruDate", null));
		}
		List<GenericValue> emplPositionNotFul = delegator.findList("AllEmplPositionAndFulfillment", EntityCondition.makeCondition(conds), null, 
				UtilMisc.toList("actualFromDate"), null, false);
		if(UtilValidate.isNotEmpty(emplPositionNotFul)){
			return emplPositionNotFul.get(0).getString("emplPositionId");
		}
		return null;
	}
	
	public static Float getNbrDayLeave(Delegator delegator, GenericValue emplLeave, boolean incAnnualLeave) throws GenericEntityException{
		if(incAnnualLeave){
			int nbrDayLeave = UtilDateTime.getIntervalInDays(emplLeave.getTimestamp("fromDate"), emplLeave.getTimestamp("thruDate"));
			return (float)(nbrDayLeave + 1);
		}else{
			return getNbrDayLeave(delegator, emplLeave);
		}
	}

	public static Float getNbrDayLeave(Delegator delegator, GenericValue emplLeave) throws GenericEntityException {
		Timestamp fromDate = emplLeave.getTimestamp("fromDate");
		Timestamp thruDate = emplLeave.getTimestamp("thruDate");
		String fromDateLeaveTypeId = emplLeave.getString("fromDateLeaveTypeId");
		String thruDateLeaveTypeId = emplLeave.getString("thruDateLeaveTypeId");
		String workingShiftId = emplLeave.getString("workingShiftId");
		return getNbrDayLeave(delegator, fromDate, thruDate, workingShiftId, fromDateLeaveTypeId, thruDateLeaveTypeId, true);
	}
	
	public static Float getNbrDayLeave(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String workingShiftId, 
			String fromDateLeaveTypeId, String thruDateLeaveTypeId, boolean thruDateIsEndOfDayLeave) throws GenericEntityException{
		Timestamp tempFromDate = UtilDateTime.getDayStart(fromDate, 1);
		Timestamp tempThruDate = UtilDateTime.getDayEnd(thruDate, -1L);
		Float retVal = 0f;
		String dayOfWeekFromDate = DateUtil.getDayName(new Date(fromDate.getTime()));
		String dayOfWeekThruDate = DateUtil.getDayName(new Date(thruDate.getTime()));
		GenericValue workingShiftDayWeekFromDate = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeekFromDate), false);
		GenericValue workingShiftDayWeekThruDate = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeekThruDate), false);
		String workTypeFromDate = workingShiftDayWeekFromDate.getString("workTypeId");
		String workTypeThruDate = workingShiftDayWeekThruDate.getString("workTypeId");
		boolean fromDateThruDateIsSameDate = UtilDateTime.getDayEnd(fromDate).compareTo(thruDate) == 0;
		if("ALL_SHIFT".equals(workTypeFromDate)){
			if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(fromDateLeaveTypeId)){
				if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(thruDateLeaveTypeId) && fromDateThruDateIsSameDate){
					retVal += 0.5f;
				}else{
					retVal++;
				}
			}else{
				retVal += 0.5f;
			}
		}else if("FIRST_HALF_SHIFT".equals(workTypeFromDate)){
			if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(fromDateLeaveTypeId)){
				retVal += 0.5f;
			}
		}else if("SECOND_HALF_SHIFT".equals(workTypeFromDate)){
			retVal += 0.5f;
		}
		if(!fromDateThruDateIsSameDate){
			if(thruDateIsEndOfDayLeave){
				if("ALL_SHIFT".equals(workTypeThruDate)){
					if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(thruDateLeaveTypeId)){
						retVal++;
					}else{
						retVal += 0.5f;
					}
				}else if("FIRST_HALF_SHIFT".equals(workTypeThruDate)){
					retVal += 0.5f;
				}else if("SECOND_HALF_SHIFT".equals(workTypeThruDate)){
					if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(thruDateLeaveTypeId)){
						retVal += 0.5f;
					}
				}
			}else{
				retVal ++;
			}
		}
		while(tempFromDate.before(tempThruDate)){
			Date tempDate = new Date(tempFromDate.getTime());
			String dayOfWeek = DateUtil.getDayName(tempDate);
			GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
			String workTypeId = workingShiftDayWeek.getString("workTypeId");
			if("ALL_SHIFT".equals(workTypeId)){
				retVal++;
			}else if("FIRST_HALF_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
				retVal += 0.5f;
			}
			tempFromDate = UtilDateTime.getDayStart(tempFromDate, 1);
		}
		return retVal;
	}
	
	/**
	 * get total annual day that employee left in year
	 * @param delegator
	 * @param partyId
	 * @param yearStart
	 * @param yearEnd
	 * @return
	 * @throws GenericEntityException
	 */
	public static BigDecimal getTotalDayLeftOfEmplInYear(Delegator delegator, String partyId, Timestamp yearStart, Timestamp yearEnd) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, yearStart));
		conditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, yearEnd));
		conditions.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("emplLeaveReasonTypeId", "NGHI_PHEP"),
											EntityJoinOperator.OR,
											EntityCondition.makeCondition("parentTypeId", "NGHI_PHEP")));
		List<GenericValue> emplLeaveList = delegator.findList("EmplLeaveAndReasonType", EntityCondition.makeCondition(conditions), null, null, null, false);
		BigDecimal retVal = BigDecimal.ZERO;
		for(GenericValue tempGv: emplLeaveList){
			float nbrDayLeave = getNbrDayLeave(delegator, tempGv);
			retVal = retVal.add(new BigDecimal(nbrDayLeave));
		}
		return retVal;
	}
	
	public static BigDecimal getTotalDayLeftUnpaid(Delegator delegator, String partyId, Timestamp yearStart, Timestamp yearEnd) throws GenericEntityException{
		BigDecimal retVal = BigDecimal.ZERO;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, yearStart));
		conditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, yearEnd));
		conditions.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
		conditions.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", "NGHI_KHONG_LUONG"));
		List<GenericValue> emplLeaveList = delegator.findList("EmplLeave", EntityCondition.makeCondition(conditions), null, null, null, false);
		for(GenericValue tempGv: emplLeaveList){
			float nbrDayLeave = getNbrDayLeave(delegator, tempGv);
			retVal = retVal.add(new BigDecimal(nbrDayLeave));
		}
		return retVal;
	}

	public static BigDecimal getTotalAnnualDayLeaveInYear(
			BigDecimal lastYearTransferred, BigDecimal leaveDayYear,
			BigDecimal grantedLeave) {
		BigDecimal retVal = BigDecimal.ZERO;
		if(lastYearTransferred != null){
			retVal = retVal.add(lastYearTransferred);
		}
		if(leaveDayYear != null){
			retVal = retVal.add(leaveDayYear);
		}
		if(grantedLeave != null){
			retVal = retVal.add(grantedLeave);
		}
		return retVal;
	}

	public static Timestamp getTimeEmplLeave(Delegator delegator,String workingShiftId, 
			Timestamp timestamp, String leaveTypeId, String startEndLeave) throws GenericEntityException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
		Time shiftStartTime = workingShift.getTime("shiftStartTime");
		Time shiftEndTime = workingShift.getTime("shiftEndTime");
		Time shiftBreakStart = workingShift.getTime("shiftBreakStart");
		Time shiftBreakEnd = workingShift.getTime("shiftBreakEnd");
		Calendar shiftCal = Calendar.getInstance();
		if(EmployeeLeaveServices.START_LEAVE.equals(startEndLeave)){
			if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(leaveTypeId)){
				shiftCal.setTime(shiftStartTime);
			}else if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(leaveTypeId)){
				Time temp = shiftBreakEnd;
				if(temp == null){
					temp = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
				}
				shiftCal.setTime(temp);
			}
		}else if(EmployeeLeaveServices.END_LEAVE.equals(startEndLeave)){
			if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(leaveTypeId)){
				Time temp = shiftBreakStart;
				if(temp == null){
					temp = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
				}
				shiftCal.setTime(temp);
			}else if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(leaveTypeId)){
				shiftCal.setTime(shiftEndTime);
			}
		}
		cal.set(Calendar.HOUR_OF_DAY, shiftCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, shiftCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, shiftCal.get(Calendar.SECOND));
		Timestamp retVal = new Timestamp(cal.getTimeInMillis());
		return retVal;
	}

	public static String getEmplWorkingStatus(Delegator delegator, String partyId) throws GenericEntityException {
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		if(person != null){
			return person.getString("workingStatusId");
		}
		return null;
	}

	public static void createPartyAttr(Delegator delegator, String partyId, String attrName, String attrValue) throws GenericEntityException {
		GenericValue partyAttr = delegator.makeValue("PartyAttribute");
		partyAttr.put("partyId", partyId);
		partyAttr.put("attrValue", attrValue);
		partyAttr.put("attrName", attrName);
		delegator.createOrStore(partyAttr);
	}
	
	public static GenericValue getEmploymentOfParty(Delegator delegator, String partyId, GenericValue userLogin) throws GenericEntityException {
		Map<String, String> mapConds = FastMap.newInstance();
		mapConds.put("partyIdTo", partyId);
		mapConds.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		List<GenericValue> employmentList = delegator.findByAnd("Employment", mapConds, UtilMisc.toList("-fromDate"), false);
		if(UtilValidate.isNotEmpty(employmentList)){
			return employmentList.get(0);
		}
		return null;
	}

	/*public static void createEmplWorkingStt(Delegator delegator, String partyId, String workingStatusId) throws GenericEntityException {
		createPartyAttr(delegator, partyId, "WorkingStatus", workingStatusId);
	}*/
	public static List<String> getListEmplHavePositionTypeInPeriod(
			Delegator delegator, String emplPositionTypeId, GenericValue mgrUserLogin, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		
		//lay ve danh sach nhan vien ma userLogin quan ly
		List<String> listOrgMgr = PartyUtil.getListOrgManagedByParty(delegator, mgrUserLogin.getString("userLoginId"), fromDate, thruDate);
		List<String> emplList = FastList.newInstance();
		for(String orgId: listOrgMgr){
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> tempEmplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> tempEmplListId = EntityUtil.getFieldListFromEntityList(tempEmplList, "partyId", true);
			emplList.addAll(tempEmplListId);
		}
		if(UtilValidate.isEmpty(emplList)){
			return emplList;
		}
		
		// lay ve danh sach nhan vien ma userLogin quan ly va co kieu vi tri la EmplPositionType
		List<EntityCondition> emplPosTypeConds = FastList.newInstance();
		emplPosTypeConds.add(EntityCondition.makeCondition("employeePartyId", EntityJoinOperator.IN, emplList));
		emplPosTypeConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		emplPosTypeConds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		List<GenericValue> emplListInPositionType = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(emplPosTypeConds), null, null, null, false);
		emplList = EntityUtil.getFieldListFromEntityList(emplListInPositionType, "employeePartyId", true);
		return emplList;
	}

	public static Map<String, Object> getEmployeeInfo(Delegator delegator, String partyId) throws GenericEntityException {
		GenericValue person = delegator.findOne("PartyAndPerson", UtilMisc.toMap("partyId", partyId), false);
		if(person == null){
			return null;
		}
		Map<String, Object> personInfo = FastMap.newInstance();
		Date idIssueDate = person.getDate("idIssueDate");
		Date birthDate = person.getDate("birthDate");
		String partyCode = person.getString("partyCode");
		if(partyCode == null){
			partyCode = partyId;
		}
		personInfo.put("partyId", partyId);
		personInfo.put("partyCode", partyCode);
		personInfo.put("lastName", person.get("lastName"));
		personInfo.put("firstName", person.get("firstName"));
		personInfo.put("middleName", person.get("middleName"));
		personInfo.put("idNumber", person.get("idNumber"));
		personInfo.put("maritalStatusId", person.get("maritalStatusId"));
		personInfo.put("ethnicOrigin", person.get("ethnicOrigin"));
		personInfo.put("religion", person.get("religion"));
		personInfo.put("nationality", person.get("nationality"));
		personInfo.put("gender", person.get("gender"));
		personInfo.put("nativeLand", person.get("nativeLand"));
		personInfo.put("idIssuePlace", person.get("idIssuePlace"));
		personInfo.put("birthPlace", person.get("birthPlace"));
		personInfo.put("majorId", person.get("majorId"));
		personInfo.put("maritalStatusId", person.get("maritalStatusId"));
		personInfo.put("graduationYear", person.get("graduationYear"));
		if(idIssueDate != null){
			personInfo.put("idIssueDate", idIssueDate.getTime());
		}
		if(birthDate != null){
			personInfo.put("birthDate", birthDate.getTime());
		}
		return personInfo;
	}

	public static Map<String, Object> getCurrentPartyPostalAddress(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String partyId,
			String contactMechPurposeTypeId) throws GenericEntityException, GenericServiceException {
		Map<String, Object> context = FastMap.newInstance();
		context.put("partyId", partyId);
		context.put("userLogin", userLogin);
		context.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		Map<String, Object> resultService = dispatcher.runSync("getPartyPostalAddress", context);
		if(ServiceUtil.isSuccess(resultService)){
			String contactMechId = (String) resultService.get("contactMechId");
			if(contactMechId != null){
				GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				Map<String, Object> postalAddrInfo = FastMap.newInstance();
				postalAddrInfo.put("contactMechId", contactMechId);
				postalAddrInfo.put("address1", postalAddr.getString("address1"));
				postalAddrInfo.put("countryGeoId", postalAddr.getString("countryGeoId"));
				postalAddrInfo.put("stateProvinceGeoId", postalAddr.getString("stateProvinceGeoId"));
				postalAddrInfo.put("wardGeoId", postalAddr.getString("wardGeoId"));
				postalAddrInfo.put("districtGeoId", postalAddr.getString("districtGeoId"));
				return postalAddrInfo;
			}
		}
		return null;
	}
	public static Map<String, Object> createEmplPosTypePartyRel(LocalDispatcher dispatcher,
			Delegator delegator, List<GenericValue> emplPosTypePartyRelConfig, Timestamp fromDate, Timestamp thruDate,
			GenericValue userLogin, String partyIdFrom, String partyIdTo) throws GenericServiceException, GenericEntityException {
		Map<String, Object> resultService = null;
		for(GenericValue tempGv: emplPosTypePartyRelConfig){
			String roleTypeIdFrom = tempGv.getString("roleTypeIdFrom");
			String roleTypeIdTo = tempGv.getString("roleTypeIdTo");
			String partyRelationshipTypeId = tempGv.getString("partyRelationshipTypeId");
			String isFromOrgToPerson = tempGv.getString("isFromOrgToPerson");
			String primaryPartyId = tempGv.getString("primaryPartyId");
			if("_NA_".equals(primaryPartyId)){
				primaryPartyId = partyIdFrom;
			}
			/*List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
						EntityJoinOperator.OR, EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));*/
			if("Y".equals(isFromOrgToPerson)){
				resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", roleTypeIdTo, "userLogin", userLogin));
				if(partyRelationshipTypeId != null){
					/*conds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
					conds.add(EntityCondition.makeCondition("partyIdFrom", primaryPartyId));
					List<GenericValue> partyRelExists = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);*/
					resultService = dispatcher.runSync("createPartyRelationship", 
							UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdTo", roleTypeIdTo,
									"partyIdFrom", primaryPartyId, "roleTypeIdFrom", roleTypeIdFrom,
									"partyRelationshipTypeId", partyRelationshipTypeId, "fromDate", fromDate,
									"thruDate", thruDate, "userLogin", userLogin));
				}
			}else{
				resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", roleTypeIdFrom, "userLogin", userLogin));
				if(partyRelationshipTypeId != null){
					/*conds.add(EntityCondition.makeCondition("partyIdTo", primaryPartyId));
					conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdTo));
					List<GenericValue> partyRelExists = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);*/
					resultService = dispatcher.runSync("createPartyRelationship", 
							UtilMisc.toMap("partyIdTo", primaryPartyId, "roleTypeIdTo", roleTypeIdTo,
									"partyIdFrom", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom,
									"partyRelationshipTypeId", partyRelationshipTypeId, "fromDate", fromDate,
									"thruDate", thruDate, "userLogin", userLogin));
				}
			}
		}
		return resultService;
	}
}
