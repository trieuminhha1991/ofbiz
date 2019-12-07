package com.olbius.employee.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
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

import org.apache.commons.lang.StringUtils;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.util.TimekeepingUtils;
import com.olbius.util.PartyUtil;
import com.olbius.util.PropertiesUtil;
import com.olbius.workflow.WorkFlowUtils;

public class EmployeeServices {

	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	public static final int maxPunishmentLevel = 2;
	public static int transactionTimeout = 3000;
	/**
	 * Create a new employee
	 * 
	 * @param dpCtx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createEmployee(DispatchContext dpCtx,
			Map<String, Object> context) {
		
		//Delegator delegator = dpCtx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = dpCtx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		
		Map<String, Object> resultsContext = FastMap.newInstance();
		try {
			//party.create();
			//person.create();
			Map<String, Object> resutls = dispatcher.runSync("createPerson", ServiceUtil.setServiceFields(dispatcher, "createPerson", context, userLogin, timeZone, locale));
			String partyId = (String)resutls.get("partyId");
			Map<String, Object> roleMap = FastMap.newInstance();
			roleMap.put("partyId", partyId);
			roleMap.put("roleTypeId", "EMPLOYEE");
			roleMap.put("userLogin", userLogin);
			dispatcher.runSync("createPartyRole", roleMap);
			roleMap.put("roleTypeId", "EMPLOYEE_HR");
			dispatcher.runSync("createPartyRole", roleMap);
			
			// Create UserLogin
			String userLoginId = (String)context.get("userLoginId");
			String currentPassword = (String)context.get("currentPassword");
			String currentPasswordVerify = (String)context.get("currentPasswordVerify");
			String requirePasswordChange = (String)context.get("requirePasswordChange");
			String passwordHint = (String)context.get("passwordHint");
			
			resultsContext.put("userLoginId", userLoginId);
			resultsContext.put("currentPassword", currentPassword);
			resultsContext.put("currentPasswordVerify", currentPasswordVerify);
			resultsContext.put("requirePasswordChange", requirePasswordChange);
			resultsContext.put("passwordHint", passwordHint);
			resultsContext.put("enabled", "Y");
			resultsContext.put("externalAuthId", null);
			resultsContext.put("partyId", partyId);
		}catch (GenericServiceException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "CreateError",
					new Object[] { e.getMessage() }, locale));
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "CreateError",
					new Object[] { e.getMessage() }, locale));
		}
		
		resultsContext.put(ModelService.RESPOND_SUCCESS, UtilProperties.getMessage(resource, "createSuccessfully", locale));
		return resultsContext;
	}		
	
	public static Map<String, Object> createEmplContactInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String partyName = PartyHelper.getPartyName(delegator, partyId, false);
		String phone_mobile = (String)context.get("phone_mobile");
		String phone_work = (String) context.get("phone_work");
		String phone_home = (String) context.get("phone_home");
		String primaryEmailAddress = (String) context.get("primaryEmailAddress");
		String personalEmailAddress = (String) context.get("personalEmailAddress");
		String otherEmailAddress = (String) context.get("otherEmailAddress");
		String address1_PermanentResidence = (String) context.get("address1_PermanentResidence");
		String permanentResidence_countryGeoId = (String) context.get("permanentResidence_countryGeoId");
		String permanentResidence_stateProvinceGeoId = (String) context.get("permanentResidence_stateProvinceGeoId");
		String permanentResidence_districtGeoId = (String) context.get("permanentResidence_districtGeoId");
		String permanentResidence_wardGeoId = (String) context.get("permanentResidence_wardGeoId");
		String address1_CurrResidence = (String) context.get("address1_CurrResidence");
		String currResidence_countryGeoId = (String) context.get("currResidence_countryGeoId");
		String currResidence_stateProvinceGeoId = (String) context.get("currResidence_stateProvinceGeoId");
		String currResidence_districtGeoId = (String) context.get("currResidence_districtGeoId");
		String currResidence_wardGeoId = (String) context.get("currResidence_wardGeoId");
		Map<String, Object> contactCtx = null;
		GenericValue provinceGeo;	
		Map<String, Object> results;
		Map<String, Object> retMap = FastMap.newInstance(); 
		try {
			//create permanent Residence	
			if(UtilValidate.isNotEmpty(address1_PermanentResidence)){
				
				if(UtilValidate.isEmpty(permanentResidence_countryGeoId)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "CountryPermanentNotSelected", locale));
				}
				if(UtilValidate.isEmpty(permanentResidence_stateProvinceGeoId)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "StateProvincePermanentNotSelected", locale));
				}
				provinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", permanentResidence_stateProvinceGeoId), false);
				String cityPermanentResidence = (String)provinceGeo.get("geoName", locale);
				contactCtx =  UtilMisc.toMap("address1", address1_PermanentResidence,
										  "partyId", partyId,
										  "city", cityPermanentResidence,
										  "userLogin", context.get("userLogin"),
										  "postalCode", "10000",
										  "stateProvinceGeoId", permanentResidence_stateProvinceGeoId,
										  "countryGeoId", permanentResidence_countryGeoId,
										  "districtGeoId", permanentResidence_districtGeoId,
										  "wardGeoId", permanentResidence_wardGeoId,
										  "contactMechPurposeTypeId", PropertiesUtil.PERMANENT_RESIDENCE,
										  "toName", partyName);
				results = dispatcher.runSync("createPartyPostalAddress", contactCtx);
				retMap.put(PropertiesUtil.PERMANENT_RESIDENCE, results.get("contactMechId"));
			}
			
			//create current Residence
			if(UtilValidate.isNotEmpty(address1_CurrResidence)){
				if(UtilValidate.isEmpty(currResidence_countryGeoId)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "CountryCurrResidenceNotSelected", locale));
				}
				if(UtilValidate.isEmpty(currResidence_stateProvinceGeoId)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "StateProvinceCurrResidenceNotSelected", locale));
				}
				provinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", currResidence_stateProvinceGeoId), false); 
				String cityCurrResidence = (String)provinceGeo.get("geoName", locale);
				contactCtx = UtilMisc.toMap("address1", address1_CurrResidence,
											  "partyId", partyId,
											  "city", cityCurrResidence,
											  "userLogin", context.get("userLogin"),
											  "postalCode", "10000",
											  "stateProvinceGeoId", currResidence_stateProvinceGeoId,
											  "countryGeoId", currResidence_countryGeoId,
											  "districtGeoId", currResidence_districtGeoId,
											  "wardGeoId", currResidence_wardGeoId,
											  "contactMechPurposeTypeId", PropertiesUtil.CURRENT_RESIDENCE,
											  "toName", partyName);
			    results = dispatcher.runSync("createPartyPostalAddress", contactCtx);
			    retMap.put(PropertiesUtil.CURRENT_RESIDENCE, results.get("contactMechId"));
			    
			}
			
			//create phone number contact
			if(UtilValidate.isNotEmpty(phone_mobile)){
				 results = dispatcher.runSync("createPartyTelecomNumber", UtilMisc.toMap("partyId", partyId,
						 																 "contactNumber", phone_mobile,
						 																"contactMechPurposeTypeId", "PHONE_MOBILE",
						 																 "userLogin", context.get("userLogin")));
				 retMap.put("PHONE_MOBILE", results.get("contactMechId"));
			}
			if(UtilValidate.isNotEmpty(phone_work)){
				results = dispatcher.runSync("createPartyTelecomNumber", UtilMisc.toMap("partyId", partyId,
																						 "contactNumber", phone_work,
																						 "contactMechPurposeTypeId", "PHONE_WORK",
																						 "userLogin", context.get("userLogin")));
				retMap.put("PHONE_WORK", results.get("contactMechId"));
			}
			if(UtilValidate.isNotEmpty(phone_home)){
				results = dispatcher.runSync("createPartyTelecomNumber", UtilMisc.toMap("partyId", partyId,
																						 "contactNumber", phone_home,
																						 "contactMechPurposeTypeId", "PHONE_HOME",
																						 "userLogin", context.get("userLogin")));
				retMap.put("PHONE_HOME", results.get("contactMechId"));
			}
			
			//create email contact
			if(UtilValidate.isNotEmpty(primaryEmailAddress)){
				results = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("emailAddress", primaryEmailAddress,
																					   "partyId", partyId,
																					   "contactMechPurposeTypeId", "PRIMARY_EMAIL",
																					   "userLogin", context.get("userLogin")));
				retMap.put("PRIMARY_EMAIL", results.get("contactMechId"));
			}
			if(UtilValidate.isNotEmpty(personalEmailAddress)){
				results = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("emailAddress", personalEmailAddress,
						   "partyId", partyId,
						   "contactMechPurposeTypeId", "PERSONAL_EMAIL",
						   "userLogin", context.get("userLogin")));
				retMap.put("PERSONAL_EMAIL", results.get("contactMechId"));
			}
			if(UtilValidate.isNotEmpty(otherEmailAddress)){
				results = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("emailAddress", otherEmailAddress,
						   "partyId", partyId,
						   "contactMechPurposeTypeId", "OTHER_EMAIL",
						   "userLogin", context.get("userLogin")));
				retMap.put("OTHER_EMAIL", results.get("contactMechId"));
			}			
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		
		return retMap;
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
			employeeList = PartyUtil.getListEmployeeOfManager(delegator, managerId);
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
	
	public static Map<String, Object> updateEmplPunishment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String) context.get("partyId");
		String punishmentTypeId = (String) context.get("punishmentTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue partyPunishment = delegator.findOne("PartyPunishment", UtilMisc.toMap("partyId", partyId, "punishmentTypeId", punishmentTypeId), false);
			
			if(UtilValidate.isEmpty(partyPunishment)){
				partyPunishment = delegator.makeValue("PartyPunishment", UtilMisc.toMap("partyId", partyId, "punishmentTypeId", punishmentTypeId));				
			}
			Long punishmentCount = partyPunishment.getLong("punishmentCount");
			//increase reminder by 1
			punishmentCount = punishmentCount != null? punishmentCount + 1 : 1;
			partyPunishment.set("punishmentCount", punishmentCount);
			partyPunishment.set("partyPunishingId", userLogin.getString("partyId"));
			partyPunishment.set("datePunishment", UtilDateTime.nowTimestamp());
			//GenericValue punishment = delegator.findOne("PunishmentType", UtilMisc.toMap("punishmentTypeId", punishmentTypeId), false);
			delegator.createOrStore(partyPunishment);
			GenericValue punishmentType = delegator.findOne("PunishmentType", UtilMisc.toMap("punishmentTypeId", punishmentTypeId), false);
			dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", partyId,
																	"header", UtilProperties.getMessage("EmploymentUiLabels", "PartyPunished", UtilMisc.toMap("punishmentType", punishmentType.getString("description")), locale),
																	"dateTime", UtilDateTime.nowTimestamp(),
																	"state", "open",
																	"ntfType", "ONE",
																	"userLogin", userLogin,
																	"targetLink", "partyId=" + partyId + ";punishmentTypeId=" + punishmentTypeId, 
																	"action", "ViewRemindPunishment"));
			
			
			dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", PartyUtil.getHrmAdmin(delegator),
								"header", PartyHelper.getPartyName(delegator,partyId, false) + " bị nhắc vì " + punishmentType.getString("description"),
								"dateTime", UtilDateTime.nowTimestamp(),
								"state", "open",
								"userLogin", userLogin,
								"ntfType", "ONE",
								"targetLink", "partyId=" + partyId + ";punishmentTypeId=" + punishmentTypeId, 
								"action", "ViewRemindPunishment"));
			//GenericValue punishmentType = delegator.findOne("PunishmentType", UtilMisc.toMap("punishmentTypeId", punishmentTypeId), false);			
			Long numberRemindToWarning = punishmentType.getLong("numberRemindToWarning");

			//increase warning level by 1 if the number of reminder equals numberRemindToWarning
			GenericValue partyPunishmentLevel = delegator.findOne("PartyPunishmentLevel", UtilMisc.toMap("partyId", partyId), false);
			Long punishmentLevel = partyPunishmentLevel != null? partyPunishmentLevel.getLong("punishmentLevel") : 0;
			if((punishmentCount % numberRemindToWarning) == 0){
				if(UtilValidate.isEmpty(partyPunishmentLevel)){
					partyPunishmentLevel = delegator.makeValue("PartyPunishmentLevel", UtilMisc.toMap("partyId", partyId));					
				}
				punishmentLevel = punishmentLevel != null? punishmentLevel + 1 :  1;
				partyPunishmentLevel.set("punishmentLevel", punishmentLevel);
				delegator.createOrStore(partyPunishmentLevel);
				dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", partyId,
						"header", UtilProperties.getMessage("EmploymentUiLabels", "PartyPunishmentLevel", UtilMisc.toMap("punishmentLevel", punishmentLevel), locale),
						"dateTime", UtilDateTime.nowTimestamp(),
						"state", "open",
						"ntfType", "ONE",
						"userLogin", userLogin,
						"targetLink", "partyId=" + partyId, 
						"action", "ViewRemindPunishment"));

				dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", PartyUtil.getHrmAdmin(delegator),
																"header", UtilProperties.getMessage("EmploymentUiLabels", "PartyIsPunishedLevel", UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId), "punishmentLevel", punishmentLevel), locale),
																"dateTime", UtilDateTime.nowTimestamp(),
																"state", "open",
																"userLogin", userLogin,
																"targetLink", "partyId=" + partyId, 
																"action", "ViewRemindPunishment"));
				
			}
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			if(punishmentLevel >= maxPunishmentLevel){
				dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", userLogin.getString("partyId"),
						"header", UtilProperties.getMessage("EmploymentUiLabels", "CreateRequestDiscipline", UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId)), locale) ,
						"dateTime", UtilDateTime.nowTimestamp(),
						"state", "open",
						"userLogin", systemUserLogin,
						"ntfType", "ONE",
						"targetLink", "partyId=" + partyId, 
						"action", "	EditRequestDisciplineProposal"));
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (Exception e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> getGeneralInfoOfEmpl(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String) context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = FastMap.newInstance();
		try {
			GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
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
			if(UtilValidate.isNotEmpty(department)){
				deptName = PartyHelper.getPartyName(delegator, department.getString("partyIdFrom"), false);
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
				if(overTimeFromDate != null && overTimeThruDate!= null){
					overtimeRegisHours += (float)(overTimeThruDate.getTime() - overTimeFromDate.getTime())/(1000 * 3600);
				}
				if(actualStartTime != null && actualEndTime != null){
					actualOvertimeHours += (float)(actualEndTime.getTime() - actualStartTime.getTime())/(1000 * 3600);
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
		}
		
		return retMap;
	}
	
	public Map<String, Object> getNbrDayLeaveEmplInfo(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		//Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
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
			Map<String, Object> emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, monthBegin, nowDate, partyId);
			/*List<Map<String, Object>> listDayLeaveAndWorkShift = (List<Map<String, Object>>)emplDayLeaveTimekeeping.get("listDayLeaveAndWorkShift"); 
			Map<String, Float> dateLeaveApproved = TimekeepingUtils.getDateLeaveApproved(dctx, listDayLeaveAndWorkShift, partyId, allWorkingShift.size());
			Float leaveInMonthFromTimekeep = (Float)dateLeaveApproved.get("totalLeavePaid");*/
			Float leaveInMonthFromTimekeep = (Float)emplDayLeaveTimekeeping.get("leavePaid");
			
			//leave that paid in year
			emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, yearBegin, nowDate, partyId);
			/*listDayLeaveAndWorkShift = (List<Map<String, Object>>)emplDayLeaveTimekeeping.get("listDayLeaveAndWorkShift");
			dateLeaveApproved = TimekeepingUtils.getDateLeaveApproved(dctx, listDayLeaveAndWorkShift, partyId, allWorkingShift.size());
			Float leaveInYearFromTimekeep = (Float)dateLeaveApproved.get("totalLeavePaid");*/
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createEmplLeave(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		//String leaveTypeId = (String)context.get("leaveTypeId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String leaveUnpaid = (String)context.get("leaveUnpaid");
		List<String> workingShiftList = (List<String>)context.get("workingShiftList");
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Boolean isLeaveUnpaid = false;
		if(leaveUnpaid != null && leaveUnpaid.equals("Y")){
			isLeaveUnpaid = true;
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "createEmplLeaveSuccessfully", locale));
		
		try {
			
			List<GenericValue> checkEntity = delegator.findByAnd("EmplLeave", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate), UtilMisc.toList("-fromDate"), false);
			if(UtilValidate.isNotEmpty(checkEntity)){
				//GenericValue leaveType = delegator.findOne("EmplLeaveType", UtilMisc.toMap("leaveTypeId", leaveTypeId), false);
				Calendar cal = Calendar.getInstance();
				for(GenericValue tempGv: checkEntity){
					List<GenericValue> emplLeaveWorkingShift = delegator.findByAnd("EmplLeaveWorkingShift", UtilMisc.toMap("emplLeaveId", tempGv.get("emplLeaveId")), null, false);
					List<String> workingShiftLeft = EntityUtil.getFieldListFromEntityList(emplLeaveWorkingShift, "workingShiftId", true);
					if(workingShiftLeft.containsAll(workingShiftList) || workingShiftList.containsAll(workingShiftLeft)){
						cal.setTime(tempGv.getTimestamp("fromDate"));
						String fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
						cal.setTime(tempGv.getTimestamp("thruDate"));
						String thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", tempGv.getString("leaveStatus")), false);
						List<String> wsDesc = FastList.newInstance();
						for(String ws: workingShiftLeft){
							GenericValue tempWS = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", ws), false);
							wsDesc.add(tempWS.getString("description"));
						}
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "EmplLeaveCreated", 
								UtilMisc.toMap("fromDate", fromDateStr, "thruDate", thruDateStr, "status", statusItem.getString("description"),
										       "workingShift", StringUtils.join(wsDesc, ", ")), locale));	
					}
				}
			}
			
			//Map<String, Object> results = dispatcher.runSync("getNbrDayLeaveEmplInfo", ServiceUtil.setServiceFields(dispatcher, "getNbrDayLeaveEmplInfo", context, userLogin, timeZone, locale));
			Map<String, Object> results = dispatcher.runSync("getDayLeaveRegulation", ServiceUtil.setServiceFields(dispatcher, "getDayLeaveRegulation", context, userLogin, timeZone, locale));
			//Float nbrDayLeavePaidInYear = (Float)results.get("nbrDayLeavePaidInYear");
			
			float dayLeaveRequest = UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1;
			if(!isLeaveUnpaid){
				Long dayLeaveRegulation = (Long)results.get("dayLeaveRegulation");		
				if(dayLeaveRegulation == null){
					dayLeaveRegulation = 0L;
				}
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
				Timestamp monthStart = UtilDateTime.getMonthStart(nowTimestamp);
				Timestamp yearStart = UtilDateTime.getYearStart(nowTimestamp);
				Timestamp nowDate = UtilDateTime.getDayEnd(nowTimestamp);
				//List<GenericValue> allWorkingShift = TimekeepingUtils.getAllWorkingShift(delegator);
				
				//leave that paid in month
				Map<String, Object> emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, monthStart, nowDate, partyId);
				/*List<Map<String, Object>> listDayLeaveAndWorkShift = (List<Map<String, Object>>)emplDayLeaveTimekeeping.get("listDayLeaveAndWorkShift"); 
				Map<String, Float> dateLeaveApproved = TimekeepingUtils.getDateLeaveApproved(dctx, listDayLeaveAndWorkShift, partyId, allWorkingShift.size());
				Float leaveInMonthFromTimekeep = (Float)dateLeaveApproved.get("totalLeavePaid");*/
				Float leaveInMonthFromTimekeep = (Float)emplDayLeaveTimekeeping.get("leavePaid");
				
				//leave that paid in year
				emplDayLeaveTimekeeping = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, yearStart, nowDate, partyId);
				/*listDayLeaveAndWorkShift = (List<Map<String, Object>>)emplDayLeaveTimekeeping.get("listDayLeaveAndWorkShift");
				dateLeaveApproved = TimekeepingUtils.getDateLeaveApproved(dctx, listDayLeaveAndWorkShift, partyId, allWorkingShift.size());
				Float leaveInYearFromTimekeep = (Float)dateLeaveApproved.get("totalLeavePaid");*/
				Float leaveInYearFromTimekeep = (Float)emplDayLeaveTimekeeping.get("leavePaid");
				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) + 1;
				if((leaveInYearFromTimekeep + dayLeaveRequest) > dayLeaveRegulation || 
						(leaveInMonthFromTimekeep + dayLeaveRequest) > ((dayLeaveRegulation/12) * month + 2)){
					return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "dayLeaveExceedRegulation", locale));
				}
			}
			Map<String, Object> entityMap = FastMap.newInstance();
			entityMap.putAll(context);
			entityMap.put("thruDate", thruDate);
			entityMap.put("fromDate", fromDate);
			entityMap.put("leaveStatus", "LEAVE_CREATED");
			GenericValue emplLeave = delegator.makeValidValue("EmplLeave", entityMap);
			String emplLeaveId = delegator.getNextSeqId("EmplLeave");
			emplLeave.set("emplLeaveId", emplLeaveId);
			emplLeave.set("dateApplication", UtilDateTime.nowTimestamp());
			delegator.create(emplLeave);
			for(String tempWS: workingShiftList){
				GenericValue emplLeaveWS = delegator.makeValue("EmplLeaveWorkingShift");
				emplLeaveWS.set("emplLeaveId", emplLeaveId);
				emplLeaveWS.set("workingShiftId", tempWS);
				emplLeaveWS.create();
			}
			
			retMap.put("emplLeaveId", emplLeaveId);
			GenericValue nbrDayLeaveSentToCEO = delegator.findOne("GlobalHrSetting", UtilMisc.toMap("attrId", "NBR_DAY_LEAVE"), false);
			int dayLeaveSendToCeo = 0;
			if(nbrDayLeaveSentToCEO != null){
				String attrValue = nbrDayLeaveSentToCEO.getString("attrValue");
				try{
					dayLeaveSendToCeo = Integer.parseInt(attrValue);
				}catch(Exception e){
				}	
			}
			String manager = PartyUtil.getManagerOfEmpl(delegator, partyId);
			String roleTypeId = "ELA_APPROVER";
			if(dayLeaveSendToCeo <= dayLeaveRequest){
				//need CEO approval this emplLeave
				String ceoId = PartyUtil.getCEO(delegator);
				
				dispatcher.runSync("createEmplLeaveApprRoleType", UtilMisc.toMap("emplLeaveId", emplLeaveId, "partyId", ceoId, 
																				"roleTypeId", "ELA_DECIDER",
																				"userLogin", userLogin));
			}else{
				roleTypeId = "ELA_DECIDER";
			}
			
			dispatcher.runSync("createEmplLeaveApprRoleType", UtilMisc.toMap("emplLeaveId", emplLeaveId, "partyId", manager, 
																				"roleTypeId", roleTypeId,
																				"userLogin", userLogin));
			retMap.put("roleTypeIdNtf", roleTypeId);
			String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
			
			if(!manager.equals(hrmAdmin)){
				dispatcher.runSync("createEmplLeaveApprRoleType", UtilMisc.toMap("emplLeaveId", emplLeaveId, "partyId", hrmAdmin, "roleTypeId", "ELA_VIEWER",
						"userLogin", userLogin));
			}
			
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
	
	@SuppressWarnings("unchecked")
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
			String managerId = PartyUtil.getManagerOfEmpl(delegator, userLogin.getString("partyId"));
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
				/*delegator.setNextSubSeqId(partyBusinessTripPlan, "busTripSeqId", 5, 1);*/
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
					/*delegator.setNextSubSeqId(partyBussinessTripVehicle, "vehBusTripSeqId", 5, 1);*/
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
	}
	
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
		Delegator delegator = dctx.getDelegator();
		//ByteBuffer image = (ByteBuffer)context.get("uploadedFile");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		context.put("dataCategoryId", "PERSONAL");
		context.put("statusId", "CTNT_PUBLISHED");
		context.put("partyContentTypeId", "LGOIMGURL");
		context.put("contentTypeId", "DOCUMENT");
		context.put("isPublic", "Y");
		context.put("roleTypeId", "_NA_");
		Map<String, Object> retMap = FastMap.newInstance();
		
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> resutls =  dispatcher.runSync("uploadPartyContentFile", ServiceUtil.setServiceFields(dispatcher, "uploadPartyContentFile", context, systemUserLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resutls)){
				String contentId = (String)resutls.get("contentId");
				List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", context.get("partyId"), "partyContentTypeId", "LGOIMGURL"), UtilMisc.toList("-fromDate"), false);
				if(UtilValidate.isNotEmpty(partyContentList)){
					String partyContentId = EntityUtil.getFirst(partyContentList).getString("contentId");
					if(UtilValidate.isNotEmpty(partyContentId)){
						String contentUrl = "/content/control/stream?contentId=" + contentId;
						retMap.put("contentUrl", contentUrl);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.RESPOND_SUCCESS);
		return retMap;
	}
	
	
	/*public static Map<String, Object> createDeptTransferProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyIdTransfer = (String)context.get("partyIdTransfer");
		String emplPositionTypeToId = (String)context.get("emplPositionTypeToId");
		String internalOrgUnitToId = (String)context.get("internalOrgUnitToId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp dateLeave = (Timestamp)context.get("dateLeave");
		Timestamp dateMoveTo = (Timestamp)context.get("dateMoveTo");	
		Map<String, Object> retMap = FastMap.newInstance();
		if(dateLeave == null){
			context.put("dateLeave", dateMoveTo);
		}
		try {
			boolean checkPosInDept = CommonUtil.checkPositionTypeInDept(emplPositionTypeToId, internalOrgUnitToId, delegator);
			if(checkPosInDept){
				//GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				Map<String, Object> resultService = FastMap.newInstance();
				GenericValue transferDeptProposal = delegator.makeValue("TransferDeptProposal");
				resultService = dispatcher.runSync("createJobTransProposal", ServiceUtil.setServiceFields(dispatcher, "createJobTransProposal", context, userLogin, timeZone, locale));
				String jobTransferProposalId = (String)resultService.get("jobTransferProposalId");
				transferDeptProposal.set("jobTransferProposalId", jobTransferProposalId);
				transferDeptProposal.setNonPKFields(context);
				transferDeptProposal.create();
				//transferDeptProposal.set("statusId", "JTP_CREATED");
				//create party approve job transfer proposal
				String managerOfEmpl = PartyUtil.getManagerOfEmpl(delegator, partyIdTransfer);
				String managerOfDeptMoveTo = PartyUtil.getManagerbyOrg(internalOrgUnitToId, delegator);
				String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
				String ceo = PartyUtil.getCEO(delegator);
				dispatcher.runSync("createJobTransferApprovalRoleType", UtilMisc.toMap("partyId", managerOfDeptMoveTo, "roleTypeId", "JTP_APPROVER", "userLogin", userLogin, "jobTransferProposalId", jobTransferProposalId));
				dispatcher.runSync("createJobTransferApprovalRoleType", UtilMisc.toMap("partyId", managerOfEmpl, "roleTypeId", "JTP_APPROVER", "userLogin", userLogin, "jobTransferProposalId", jobTransferProposalId));
				dispatcher.runSync("createJobTransferApprovalRoleType", UtilMisc.toMap("partyId", hrmAdmin, "roleTypeId", "JTP_CONFIRMER", "userLogin", userLogin, "jobTransferProposalId", jobTransferProposalId));
				dispatcher.runSync("createJobTransferApprovalRoleType", UtilMisc.toMap("partyId", ceo, "roleTypeId", "JTP_DECIDER", "userLogin", userLogin, "jobTransferProposalId", jobTransferProposalId));
				retMap.put("jobTransferProposalId", jobTransferProposalId);
				retMap.put("headerNtf", UtilProperties.getMessage("EmployeeUiLabels", "ntfTransDeptProposal", UtilMisc.toMap("partyName", PartyHelper.getPartyName(delegator, partyIdTransfer, false)), locale));
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "employee_departmentHaveNoPositionType", UtilMisc.toMap("departmentName", PartyHelper.getPartyName(delegator, internalOrgUnitToId, false)), locale));
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
		retMap.put(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage("EmployeeUiLabels", "employee_CreateIntOrgTransferSuccess", UtilMisc.toMap("departmentName", PartyHelper.getPartyName(delegator, internalOrgUnitToId, false)), locale));
		return retMap;
	}*/
	
	public static Map<String, Object> createJobTransProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyIdTransfer = (String)context.get("partyIdTransfer");
		String internalOrgUnitToId = (String)context.get("internalOrgUnitToId");
		String proposerId = userLogin.getString("partyId");
		Locale locale = (Locale)context.get("locale");
		if(partyIdTransfer == null){
			partyIdTransfer = proposerId;
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "CreateJobTransferPPSLSuccessful", locale));
		try {
			Map<String, Object> resultService = dispatcher.runSync("createEmplProposal", UtilMisc.toMap("emplProposalTypeId", "JOB_TRANSFER_PPSL", 
																										"statusId", "PPSL_CREATED", "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				String emplProposalId = (String)resultService.get("emplProposalId");
				retMap.put("emplProposalId", emplProposalId);
				GenericValue jobTransferProposal = delegator.makeValue("JobTransferProposal");
				jobTransferProposal.set("jobTransferProposalId", emplProposalId);
				jobTransferProposal.setNonPKFields(context);
				jobTransferProposal.create();
				
				String managerOfPartyIdTransfer = PartyUtil.getManagerOfEmpl(delegator, partyIdTransfer);
				String managerOfOrgTo = PartyUtil.getManagerbyOrg(internalOrgUnitToId, delegator);
				String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
				String ceo = PartyUtil.getCEO(delegator);
				String proposerName = PartyHelper.getPartyName(delegator, proposerId, false);
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyIdTransfer, 
						"roleTypeId", "PPSL_PROPOSED", "userLogin", userLogin));
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", managerOfPartyIdTransfer, 
						"roleTypeId", "PPSL_APPROVER", "userLogin", userLogin));
				if(!managerOfOrgTo.equals(managerOfPartyIdTransfer)){
					dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", managerOfOrgTo, 
							"roleTypeId", "PPSL_APPROVER", "userLogin", userLogin));
				}
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", hrmAdmin, 
						"roleTypeId", "PPSL_CONFIRMER", "userLogin", userLogin));
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", ceo, 
						"roleTypeId", "PPSL_DECIDER", "userLogin", userLogin));
				String proposedName = PartyHelper.getPartyName(delegator, partyIdTransfer, false);
				String header = "";
				if(proposerId.equals(partyIdTransfer)){
					header = UtilProperties.getMessage("EmployeeUiLabels", "ApprovalJobTransferSelfPPSL", UtilMisc.toMap("proposedName", proposedName), locale);
				}else{
					header = UtilProperties.getMessage("EmployeeUiLabels", "ApprovalJobTransferPPSL", UtilMisc.toMap("proposerName", proposerName, "proposedName", proposedName), locale);
				}
				String action = "JobTransProposalApproval";
				String targetLink = "emplProposalId=" + emplProposalId;
				dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "roleTypeId", "PPSL_APPROVER",
																				"header", header, "action", action, "targetLink", targetLink,
																				"userLogin", userLogin));
				if(!proposerId.equals(partyIdTransfer)){
					//notify to partyIdTransfer is proposed					
					header = UtilProperties.getMessage("EmploymentUiLabels", "EmplProposalJobTransfer", UtilMisc.toMap("proposerName", proposerName),locale);
					dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "roleTypeId", "PPSL_PROPOSED",
							"header", header, "action", action, "targetLink", targetLink,
							"userLogin", userLogin));
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return retMap;
	}
	
	
	/*public static Map<String, Object> createJobTransferApprovalRoleType(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		String jobTransferProposalId = (String)context.get("jobTransferProposalId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", systemUserLogin));
			GenericValue jobTransferApprovalRoleType = delegator.makeValue("JobTransferApprovalRoleType");
			jobTransferApprovalRoleType.setAllFields(context, false, null, null);
			jobTransferApprovalRoleType.set("fromDate", UtilDateTime.nowTimestamp());
			// userLogin is create proposal, so update status is accepted
			if(userLogin.getString("partyId").equals(partyId)){
				jobTransferApprovalRoleType.set("isApproved", "Y");
				dispatcher.runSync("createJobTransferApproval", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId, "partyId", partyId, "roleTypeId", roleTypeId, "approvalStatusId", "JTP_APPR_ACCEPTED", "userLogin", userLogin));
			}else{
				jobTransferApprovalRoleType.set("isApproved", "N");
			}
			jobTransferApprovalRoleType.create();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	/*public static Map<String, Object> createJobTransferApproval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue jobTransferApproval = delegator.makeValue("JobTransferApproval");
		jobTransferApproval.setNonPKFields(context);
		jobTransferApproval.set("approvalDate", UtilDateTime.nowTimestamp());
		String jobTransferApprovalId = delegator.getNextSeqId("JobTransferApproval");
		jobTransferApproval.set("jobTransferApprovalId", jobTransferApprovalId);
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("jobTransferApprovalId", jobTransferApprovalId);
		try {
			jobTransferApproval.create();			
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	/*public static Map<String, Object> updateJobTransferProposalStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String roleTypeId = (String)context.get("roleTypeId");
		String jobTransferProposalId = (String)context.get("jobTransferProposalId");
		String approvalStatusId = (String)context.get("approvalStatusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue jobTransferProposal = delegator.findOne("JobTransferProposal", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false);			
			String statusId = jobTransferProposal.getString("statusId");
			String partyIdTransfer = jobTransferProposal.getString("partyIdTransfer");
			String newStatusId = null;
			String partyName = PartyHelper.getPartyName(delegator, partyIdTransfer, false);
			List<GenericValue> allPartyApproverProposal = delegator.findList("JobTransferApprovalRoleType", EntityCondition.makeCondition(EntityCondition.makeCondition("jobTransferProposalId", jobTransferProposalId),
																											EntityOperator.AND,
																											EntityUtil.getFilterByDateExpr()), null, null, null, false);
			if(roleTypeId.equals("JTP_DECIDER")){
				String resultAppovalProposal = null;
				if(approvalStatusId.equals("JTP_APPR_ACCEPTED")){
					newStatusId = "JTP_ACCEPTED";
					resultAppovalProposal = UtilProperties.getMessage("EmployeeUiLabels", "ntfTransDeptProposalAccept", UtilMisc.toMap("partyName", partyName), locale);
					GenericValue transferDeptProposal = delegator.findOne("TransferDeptProposal", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false);
					//update position and department of employee
					dispatcher.runSync("updateEmplDeptAndEmplPosition", UtilMisc.toMap("partyId", partyIdTransfer, "emplPositionTypeOldId", jobTransferProposal.getString("emplPositionTypeFromId"),
														  "newInternalOrgId", transferDeptProposal.getString("internalOrgUnitToId"),
														  "emplPositionTypeNewId", transferDeptProposal.getString("emplPositionTypeToId"),
														  "dateMoveTo", transferDeptProposal.getTimestamp("dateMoveTo"),
														  "dateLeave", jobTransferProposal.getTimestamp("dateLeave"),
														  "userLogin", userLogin));
				}else{
					newStatusId = "JTP_REJECTED";
					resultAppovalProposal = UtilProperties.getMessage("EmployeeUiLabels", "ntfTransDeptProposalReject", UtilMisc.toMap("partyName", partyName), locale);
				}				
				List<String> partyListNtf = EntityUtil.getFieldListFromEntityList(allPartyApproverProposal, "partyId", true);
				partyListNtf.add(partyIdTransfer);
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("action", "TransDeptProposalApproval");
				ntfCtx.put("state", "open");
				ntfCtx.put("targetLink", "jobTransferProposalId=" + jobTransferProposalId);
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("partiesList", partyListNtf);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("header", resultAppovalProposal);
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				if(statusId.equals("JTP_CREATED")){
					newStatusId = "JTP_IN_PROCESS";
				}
				String message = UtilProperties.getMessage("EmployeeUiLabels", "ntfTransDeptProposal", UtilMisc.toMap("partyName", partyName), locale);
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("jobTransferProposalId", jobTransferProposalId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "JTP_DECIDER"));
				conditions.add(EntityCondition.makeCondition("isApproved", "N"));
				List<GenericValue> notApprJobTransList = EntityUtil.filterByCondition(allPartyApproverProposal, EntityCondition.makeCondition(conditions));
				if(UtilValidate.isEmpty(notApprJobTransList)){
					//if all person have role "APPROVER and CONFIRMER" approve, then send notify to person have role "JTP_DECIDER"
					dispatcher.runSync("createNtfToApprJobTransPropl", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId, 
																					   "roleTypeId", "JTP_DECIDER",
																					   "headerNtf", message,
																					   "userLogin", userLogin));
				}else{
					if(roleTypeId.equals("JTP_APPROVER")){
						List<GenericValue> approverRoleNotAppr = EntityUtil.filterByAnd(allPartyApproverProposal, UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId, "roleTypeId", "JTP_APPROVER", "isApproved", "N"));
						if(UtilValidate.isEmpty(approverRoleNotAppr)){
							//if all person have role "APPROVER" approved, send nofify to person have role "CONFIRMER"
							dispatcher.runSync("createNtfToApprJobTransPropl", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId, 
																							   "roleTypeId", "JTP_CONFIRMER",
																							   "headerNtf", message,
																							   "userLogin", userLogin));
						}
					}	
				}
				//}
			}
			if(newStatusId != null){
				jobTransferProposal.set("statusId", newStatusId);
				jobTransferProposal.store();
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	/*public static Map<String, Object> createNtfToApprJobTransPropl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String jobTransferProposalId = (String)context.get("jobTransferProposalId");
		String header = (String)context.get("headerNtf");
		String roleTypeId = (String)context.get("roleTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("jobTransferProposalId", jobTransferProposalId));
		conditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		conditions.add(EntityCondition.makeCondition("isApproved", "N"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> partyApprList = delegator.findList("JobTransferApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> partyListNtf = EntityUtil.getFieldListFromEntityList(partyApprList, "partyId", true);
			Map<String, Object> ntfCtx = FastMap.newInstance();			
			ntfCtx.put("action", "TransDeptProposalApproval");// need replace
			ntfCtx.put("state", "open");
			ntfCtx.put("targetLink", "jobTransferProposalId=" + jobTransferProposalId);
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("partiesList", partyListNtf);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("header", header);
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
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
	
	public static Map<String, Object> createWorkFlowApprTermination(DispatchContext dctx, Map<String, Object> context){
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
	}
	
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
	
	/*public static Map<String, Object> createWorkOffProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String terminationTypeId = (String)context.get("terminationTypeId");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			Map<String, Object> serviceCtx = ServiceUtil.setServiceFields(dispatcher, "createEmplTerminationProposal", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("createEmplTerminationProposal", serviceCtx);
			String emplTerminationProposalId = (String)resultService.get("emplTerminationProposalId");
			String managerOfEmpl = PartyUtil.getManagerOfEmpl(delegator, partyId);
			String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
			String ceo = PartyUtil.getCEO(delegator);
			String partyName = PartyHelper.getPartyName(delegator, partyId, false);
			String mgrOfEmplRole = "TERM_PPSL_APPROVER"; 
			String ntfHeader = null;
			if("RETIRE".equals(terminationTypeId)){
				boolean isRetiredAge = PartyUtil.isRetiredAge(delegator, partyId);
				ntfHeader = UtilProperties.getMessage("EmployeeUiLabels", "EmplRetiredProposal", UtilMisc.toMap("partyName", partyName), locale);
				if(isRetiredAge){
					mgrOfEmplRole = "TERM_PPSL_VIEWER";
				}
				retMap.put(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage(resource, "EmplRetiredProposalSent", locale));
			}else if("RESIGN".equals(terminationTypeId)){
				ntfHeader = UtilProperties.getMessage("EmployeeUiLabels", "ntfResignationProposal", UtilMisc.toMap("partyName", partyName), locale);
				retMap.put(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage(resource, "employee_ResignationProposalCreateSuccessful", locale));
			}
			dispatcher.runSync("createEmplTerminationApprRoleType", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, "partyId", managerOfEmpl, "roleTypeId", mgrOfEmplRole, "userLogin", userLogin));
			dispatcher.runSync("createEmplTerminationApprRoleType", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, "partyId", hrmAdmin, "roleTypeId", "TERM_PPSL_CONFIRMER", "userLogin", userLogin));
			dispatcher.runSync("createEmplTerminationApprRoleType", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, "partyId", ceo, "roleTypeId", "TERM_PPSL_DECIDER", "userLogin", userLogin));
			
			//create notify to role "TERM_PPSL_APPROVER", "TERM_PPSL_VIEWER"
			dispatcher.runSync("createNtfToApprTermPropl", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, 
																		"roleTypeId", "TERM_PPSL_APPROVER","headerNtf", ntfHeader, "userLogin", userLogin));
			dispatcher.runSync("createNtfToApprTermPropl", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, 
																		"roleTypeId", "TERM_PPSL_VIEWER","headerNtf", ntfHeader, "userLogin", userLogin));
			retMap.put("emplTerminationProposalId", emplTerminationProposalId);
			retMap.put("ntfHeader", ntfHeader);
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return retMap;
	}*/
	
	/*public static Map<String, Object> createEmplTerminationApprRoleType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		GenericValue userLoign = (GenericValue)context.get("userLogin");
		GenericValue emplTermApprRoleType = delegator.makeValue("EmplTerminationApprovalRoleType");
		emplTermApprRoleType.setAllFields(context, false, null, null);
		emplTermApprRoleType.set("fromDate", UtilDateTime.nowTimestamp());
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", systemUserLogin));
			if(!roleTypeId.equals("TERM_PPSL_VIEWER")){
				if(userLoign.getString("partyId").equals(partyId)){
					emplTermApprRoleType.set("isApproved", "Y");
					dispatcher.runSync("createEmplTerminationApproval", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, "partyId", partyId, "roleTypeId", roleTypeId, "approvalStatusId", "TER_APPR_ACCEPTED", "userLogin", userLoign));
				}else{
					emplTermApprRoleType.set("isApproved", "N");
				}
			}
			emplTermApprRoleType.create();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	/*public Map<String, Object> createNtfToApprTermPropl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		String header = (String)context.get("headerNtf");
		String roleTypeId = (String)context.get("roleTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("emplTerminationProposalId", emplTerminationProposalId));
		conditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isApproved", "N"), EntityOperator.OR, EntityCondition.makeCondition("isApproved", null)));
		conditions.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> partyApprList = delegator.findList("EmplTerminationApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> partyNtfList = EntityUtil.getFieldListFromEntityList(partyApprList, "partyId", true);
			Map<String, Object> ntfCtx = FastMap.newInstance();			
			ntfCtx.put("action", "ApprovalResignationProposal");// 
			ntfCtx.put("state", "open");
			ntfCtx.put("targetLink", "emplTerminationProposalId=" + emplTerminationProposalId);
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("partiesList", partyNtfList);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("header", header);
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	//update status of empl termination proposal
	/*public static Map<String, Object> updateTerminationProposalStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String roleTypeId = (String)context.get("roleTypeId");
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		String approvalStatusId = (String)context.get("approvalStatusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue emplTerminationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
			String statusId = emplTerminationProposal.getString("statusId");
			String partyId = emplTerminationProposal.getString("partyId");
			String newStatusId = null;
			String partyName = PartyHelper.getPartyName(delegator, partyId, false);
			List<GenericValue> allPartyApproverProposal = delegator.findList("EmplTerminationApprovalRoleType",
																			EntityCondition.makeCondition(
																				EntityCondition.makeCondition("isApproved", EntityOperator.NOT_EQUAL, null), 
																							EntityOperator.AND,
																											EntityCondition.makeCondition(EntityCondition.makeCondition("emplTerminationProposalId", emplTerminationProposalId),
																												EntityOperator.AND,
																												EntityUtil.getFilterByDateExpr())), null, null, null, false);
			if(roleTypeId.equals("TERM_PPSL_DECIDER")){
				String resultAppovalProposal = null;
				if(approvalStatusId.equals("TER_APPR_ACCEPTED")){
					resultAppovalProposal = UtilProperties.getMessage("EmployeeUiLabels", "ntfTermProposalAccept", UtilMisc.toMap("partyName", partyName), locale);
					newStatusId = "TER_PPSL_ACCEPTED";
				}else{
					newStatusId = "TER_PPSL_REJECTED";
					resultAppovalProposal = UtilProperties.getMessage("EmployeeUiLabels", "ntfTermProposalReject", UtilMisc.toMap("partyName", partyName), locale);
				}
				List<String> partyListNtf = EntityUtil.getFieldListFromEntityList(allPartyApproverProposal, "partyId", true);
				partyListNtf.add(partyId);
				Map<String, Object> ntfCtx = FastMap.newInstance();
				//FIXME need setup action
				ntfCtx.put("action", "");
				ntfCtx.put("state", "open");
				ntfCtx.put("targetLink", "emplTerminationProposalId=" + emplTerminationProposalId);
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("partiesList", partyListNtf);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("header", resultAppovalProposal);
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				if(statusId.equals("TER_PPSL_CREATED")){
					newStatusId = "TER_PPSL_IN_PROCESS";
				}
				String message = UtilProperties.getMessage("EmployeeUiLabels", "ntfTerminationProposal", UtilMisc.toMap("partyName", partyName), locale);
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("emplTerminationProposalId", emplTerminationProposalId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "JTP_DECIDER"));
				conditions.add(EntityCondition.makeCondition("isApproved", "N"));
				List<GenericValue> notApprJobTransList = EntityUtil.filterByCondition(allPartyApproverProposal, EntityCondition.makeCondition(conditions));
				if(UtilValidate.isEmpty(notApprJobTransList)){
					//if all person have role "APPROVER and CONFIRMER" approve, then send notify to person have role "JTP_DECIDER"
					dispatcher.runSync("createNtfToApprTermPropl", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, 
																					   "roleTypeId", "TERM_PPSL_DECIDER",
																					   "headerNtf", message,
																					   "userLogin", userLogin));
				}else{
					if(roleTypeId.equals("TERM_PPSL_APPROVER")){
						List<GenericValue> approverRoleNotAppr = EntityUtil.filterByAnd(allPartyApproverProposal, UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, 
																																"roleTypeId", "JTP_APPROVER", "isApproved", "N"));
						if(UtilValidate.isEmpty(approverRoleNotAppr)){
							//if all person have role "APPROVER" approved, send nofify to person have role "CONFIRMER"
							dispatcher.runSync("createNtfToApprTermPropl", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId, 
																							   "roleTypeId", "TERM_PPSL_CONFIRMER",
																							   "headerNtf", message,
																							   "userLogin", userLogin));
						}
					}	
				}
			}
			if(newStatusId != null){
				emplTerminationProposal.set("statusId", newStatusId);
				emplTerminationProposal.store();
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	/*public static Map<String, Object> updateEmplTerminationProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplTerminationProposalId = (String)context.get("emplTerminationProposalId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		try {
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, timeZone, locale));
			List<GenericValue> emplTerminationApproval = delegator.findByAnd("EmplTerminationApproval", UtilMisc.toMap("partyId", partyId, "emplTerminationProposalId", emplTerminationProposalId), null, false);
			if(UtilValidate.isNotEmpty(emplTerminationApproval)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "PartyApprovedProposal", locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			conditions.add(EntityCondition.makeCondition("emplTerminationProposalId", emplTerminationProposalId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> emplTermApprovalRoleType = delegator.findList("EmplTerminationApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("partyId", partyId);
			serviceCtx.put("emplTerminationProposalId", emplTerminationProposalId);
			serviceCtx.put("approvalStatusId", context.get("approvalStatusId"));
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("comments", context.get("comments"));
			for(GenericValue tempGv: emplTermApprovalRoleType){
				serviceCtx.put("roleTypeId", tempGv.getString("roleTypeId"));
				tempGv.set("isApproved", "Y");
				tempGv.store();
				dispatcher.runSync("createEmplTerminationApproval", serviceCtx);
			}
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "updateApprovalStatusSuccessful", locale)); 
	}*/
	
	/*public static Map<String, Object> createEmplTerminationApproval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		//TODO need check party approved this termination proposal
		GenericValue emplTerminationApproval = delegator.makeValue("EmplTerminationApproval");
		emplTerminationApproval.setNonPKFields(context);
		String terminationApprId = delegator.getNextSeqId("EmplTerminationApproval");
		emplTerminationApproval.set("approvalDate", UtilDateTime.nowTimestamp());
		emplTerminationApproval.set("terminationApprId", terminationApprId);		
		retMap.put("terminationApprId", terminationApprId);
		try {
			emplTerminationApproval.create();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}*/
	
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
}
