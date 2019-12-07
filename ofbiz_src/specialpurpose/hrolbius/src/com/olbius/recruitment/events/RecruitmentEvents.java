package com.olbius.recruitment.events;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.ApplicantServiceHelper;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.PartyUtil;
import com.olbius.util.EntityUtil;

public class RecruitmentEvents {
	public static final String module = RecruitmentEvents.class.getName();
	public static final String EMPLOYEE_ROLE = "EMPLOYEE";
	/**
	 * Create Person, PartyRole, PostalAddress(purpose: 'PERMANENT_RESIDENCE, CURRENT_RESIDENCE')
	 * TelecomNumber(purpose: 'PHONE_HOME, PHONE_MOBILE, OTHER_PHONE'), Email(purpose: 'PERSONAL_EMAIL')
	 * PartyHealth, PersonFamilyBackground, PersonEducation, PersonWorkingProcess, Skill And Acquaintance
	 * @param request
	 * @param response
	 * @return message
	 */
	@SuppressWarnings("unchecked")
	public static String createApplicant(HttpServletRequest request, HttpServletResponse response) {
		//Get Delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get Dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
		boolean beganTransaction = false;
        Boolean okay = true;
        try {
        	//Get parameters
        	Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        	Date birthDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)parameters.get("birthDate"));
			String lastName = (String)parameters.get("lastName");
			String workEffortId = (String)parameters.get("workEffortId");
			String middleName = (String)parameters.get("middleName");
			String firstName = (String)parameters.get("firstName");
			String gender = (String)parameters.get("gender");
			String birthPlace = (String)parameters.get("birthPlace");
			String height = (String)parameters.get("height");
			String weight = (String)parameters.get("weight");
			String idNumber = (String)parameters.get("idNumber");
			String idIssuePlace = (String)parameters.get("idIssuePlace");
			Date idIssueDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)parameters.get("idIssueDate"));
			String maritalStatus = (String)parameters.get("maritalStatus");
			String numberChildren = (String)parameters.get("numberChildren");
			String ethnicOrigin = (String)parameters.get("ethnicOrigin");
			String religion = (String)parameters.get("religion");
			String nativeLand = (String)parameters.get("nativeLand");
			String homeTel = (String)parameters.get("homeTel");
			String mobile = (String)parameters.get("mobile");
			String diffTel = (String)parameters.get("diffTel");
			String email = (String)parameters.get("email");
			String prAddress = (String)parameters.get("prAddress");
			String prCountry = (String)parameters.get("prCountry");
			String prProvince = (String)parameters.get("prProvince");
			String prDistrict = (String)parameters.get("prDistrict");
			String prWard = (String)parameters.get("prWard");
			String crCountry = (String)parameters.get("crCountry");
			String crAddress = (String)parameters.get("crAddress");
			String crProvince = (String)parameters.get("crProvince");
			String crDistrict = (String)parameters.get("crDistrict");
			String crWard = (String)parameters.get("crWard");
			String jqxSpecialSkillEditor = (String)parameters.get("jqxSpecialSkillEditor");
			String badHealth = (String)parameters.get("badHealth");
			String badHealthDetail = (String)parameters.get("badHealthDetail");
			String badInfo = (String)parameters.get("badInfo");
			String badInfoDetail = (String)parameters.get("badInfoDetail");
			String sourceTypeId = (String)parameters.get("sourceTypeId");
			String referredByPartyId = (String)parameters.get("referredByPartyId");
			String finAccountName = (String)parameters.get("finAccountName");
			String finAccountCode = (String)parameters.get("finAccountCode");
			String comments = (String)parameters.get("comments");
			List<Map<String, String>> fmData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("fmData"));
			List<Map<String, String>> eduData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("eduData"));
			List<Map<String, String>> wpData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("wpData"));
			List<Map<String, String>> skillData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("skillData"));
			List<Map<String, String>> aqcData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("aqcData"));
        	
			//Begin transaction
			beganTransaction = TransactionUtil.begin();
			
			//Create Applicant
			Map<String, Object> createApplicantCtx = FastMap.newInstance();
			createApplicantCtx.put("birthDate", birthDate);
			createApplicantCtx.put("lastName", lastName);
			createApplicantCtx.put("middleName", middleName);
			createApplicantCtx.put("firstName", firstName);
			createApplicantCtx.put("gender", gender);
			createApplicantCtx.put("birthPlace", birthPlace);
			createApplicantCtx.put("height", height);
			createApplicantCtx.put("weight", weight);
			createApplicantCtx.put("idNumber", idNumber);
			createApplicantCtx.put("idIssuePlace", idIssuePlace);
			createApplicantCtx.put("idIssueDate", idIssueDate);
			createApplicantCtx.put("maritalStatus", maritalStatus);
			createApplicantCtx.put("numberChildren", numberChildren);
			createApplicantCtx.put("ethnicOrigin", ethnicOrigin);
			createApplicantCtx.put("religion", religion);
			createApplicantCtx.put("nativeLand", nativeLand);
			createApplicantCtx.put("userLogin", userLogin);
			Map<String, Object> createApplRs = ApplicantServiceHelper.createApplicant(dispatcher, createApplicantCtx, okay);
			if (ServiceUtil.isSuccess(createApplRs)) {
				//Create Contact
				Map<String, Object> createPartyContactCtx = FastMap.newInstance();
				createPartyContactCtx.put("homeTel", homeTel);
				createPartyContactCtx.put("mobile", mobile);
				createPartyContactCtx.put("diffTel", diffTel);
				createPartyContactCtx.put("email", email);
				createPartyContactCtx.put("prAddress", prAddress);
				createPartyContactCtx.put("prCountry", prCountry);
				createPartyContactCtx.put("prProvince", prProvince);
				createPartyContactCtx.put("prDistrict", prDistrict);
				createPartyContactCtx.put("prWard", prWard);
				createPartyContactCtx.put("crCountry", crCountry);
				createPartyContactCtx.put("crProvince", crProvince);
				createPartyContactCtx.put("crDistrict", crDistrict);
				createPartyContactCtx.put("crWard", crWard);
				createPartyContactCtx.put("userLogin", userLogin);
				createPartyContactCtx.put("partyId", createApplRs.get("partyId"));
				createPartyContactCtx.put("crAddress", crAddress);
				ApplicantServiceHelper.createPartyContact(dispatcher, createPartyContactCtx, okay);
				
				//Create PartyHealth
				Map<String, Object> createPartyHealthCtx = FastMap.newInstance();
				createPartyHealthCtx.put("partyId", createApplRs.get("partyId"));
				createPartyHealthCtx.put("badHealth", badHealth);
				createPartyHealthCtx.put("badHealthDetail", badHealthDetail);
				createPartyHealthCtx.put("badInfo", badInfo);
				createPartyHealthCtx.put("badInfoDetail", badInfoDetail);
				createPartyHealthCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyHealth(dispatcher, createPartyHealthCtx, okay);
				
				//Create Family Member
				Map<String, Object> createFamilyMemCtx = FastMap.newInstance();
				createFamilyMemCtx.put("fmData", fmData);
				createFamilyMemCtx.put("partyId", createApplRs.get("partyId"));
				createFamilyMemCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyFamilyMem(dispatcher, createFamilyMemCtx, okay);
				
				//Create Education
				Map<String, Object> createEducationCtx = FastMap.newInstance();
				createEducationCtx.put("eduData", eduData);
				createEducationCtx.put("partyId", createApplRs.get("partyId"));
				createEducationCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyEducation(dispatcher, createEducationCtx, okay);
				
				//Create Working Process
				Map<String, Object> createWorkingProcessCtx = FastMap.newInstance();
				createWorkingProcessCtx.put("wpData", wpData);
				createWorkingProcessCtx.put("partyId", createApplRs.get("partyId"));
				createWorkingProcessCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyWorkProcess(dispatcher, createWorkingProcessCtx, okay);
				
				//Create Skill
				Map<String, Object> createSkillCtx = FastMap.newInstance();
				createSkillCtx.put("skillData", skillData);
				createSkillCtx.put("partyId", createApplRs.get("partyId"));
				createSkillCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartySkill(dispatcher, createSkillCtx, okay);
				
				//Create Acquaintance
				Map<String, Object> createAqcCtx = FastMap.newInstance();
				createAqcCtx.put("aqcData", aqcData);
				createAqcCtx.put("partyId", createApplRs.get("partyId"));
				createAqcCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyAqc(dispatcher, createAqcCtx, okay);
				
				//Create Fin Account
				if(!UtilValidate.isEmpty(finAccountCode) && !UtilValidate.isEmpty(finAccountCode)) {
					Map<String, Object> createFinAccountCtx = FastMap.newInstance();
					createFinAccountCtx.put("finAccountCode", finAccountCode);
					createFinAccountCtx.put("finAccountName", finAccountName);
					createFinAccountCtx.put("partyId", createApplRs.get("partyId"));
					createFinAccountCtx.put("userLogin", userLogin);
					ApplicantServiceHelper.createFinAccount(dispatcher, createFinAccountCtx, okay);
				}
				
				//Create Party Attribute
				GenericValue partyAttribute = delegator.makeValue("PartyAttribute");
				partyAttribute.put("partyId", createApplRs.get("partyId"));
				partyAttribute.put("attrName", "specialSkill");
				partyAttribute.put("attrValue", jqxSpecialSkillEditor);
				partyAttribute.create();
				
				//Create WorkEffort Party Assignment
				GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment");
				workEffortPartyAssignment.put("workEffortId", workEffortId);
				workEffortPartyAssignment.put("partyId", createApplRs.get("partyId"));
				workEffortPartyAssignment.put("roleTypeId", "APPLICANT");
				workEffortPartyAssignment.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.put("assignedByUserLoginId",userLogin.get("partyId"));
				workEffortPartyAssignment.put("statusId", "APPL_RECRUITING");
				workEffortPartyAssignment.put("statusDateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.put("comments", comments);
				workEffortPartyAssignment.create();
				
				//Create EmploymentApplication
				if(!UtilValidate.isEmpty(referredByPartyId) || !UtilValidate.isEmpty(sourceTypeId)) {
					GenericValue emplApplication = delegator.makeValue("EmploymentApplication");
					String applicationId = delegator.getNextSeqId("EmploymentApplication");
					emplApplication.put("applicationId", applicationId);
					emplApplication.put("applyingPartyId", createApplRs.get("partyId"));
					emplApplication.put("referredByPartyId", referredByPartyId);
					emplApplication.put("employmentAppSourceTypeId", sourceTypeId);
					emplApplication.put("workEffortId",workEffortId);
					emplApplication.put("statusId", "APPL_RECRUITING");
					emplApplication.create();
				}
			}
			
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GeneralServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}catch (IllegalArgumentException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}finally {
			if (!okay) {
				//Roll back transaction
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing create applicant", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			} else {
				//Commit transaction
				try {
					TransactionUtil.commit(beganTransaction);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.SUCCESS_MESSAGE);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}
		}
        
        
		return "success";
	}
	
	/**
	 * Create Person, PartyRole, PostalAddress(purpose: 'PERMANENT_RESIDENCE, CURRENT_RESIDENCE')
	 * TelecomNumber(purpose: 'PHONE_HOME, PHONE_MOBILE, OTHER_PHONE'), Email(purpose: 'PERSONAL_EMAIL')
	 * PartyHealth, PersonFamilyBackground, PersonEducation, PersonWorkingProcess, Skill And Acquaintance
	 * @param request
	 * @param response
	 * @return message
	 */
	@SuppressWarnings("unchecked")
	public static String createDirectedApplicant(HttpServletRequest request, HttpServletResponse response) {
		//Get Delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get Dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
		boolean beganTransaction = false;
        Boolean okay = true;
        try {
        	//Get parameters
        	Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        	Date birthDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)parameters.get("birthDate"));
			String lastName = (String)parameters.get("lastName");
			String workEffortId = (String)parameters.get("workEffortId");
			String middleName = (String)parameters.get("middleName");
			String firstName = (String)parameters.get("firstName");
			String gender = (String)parameters.get("gender");
			String birthPlace = (String)parameters.get("birthPlace");
			String height = (String)parameters.get("height");
			String weight = (String)parameters.get("weight");
			String idNumber = (String)parameters.get("idNumber");
			String idIssuePlace = (String)parameters.get("idIssuePlace");
			Date idIssueDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)parameters.get("idIssueDate"));
			String maritalStatus = (String)parameters.get("maritalStatus");
			String numberChildren = (String)parameters.get("numberChildren");
			String ethnicOrigin = (String)parameters.get("ethnicOrigin");
			String religion = (String)parameters.get("religion");
			String nativeLand = (String)parameters.get("nativeLand");
			String homeTel = (String)parameters.get("homeTel");
			String mobile = (String)parameters.get("mobile");
			String diffTel = (String)parameters.get("diffTel");
			String email = (String)parameters.get("email");
			String prAddress = (String)parameters.get("prAddress");
			String prCountry = (String)parameters.get("prCountry");
			String prProvince = (String)parameters.get("prProvince");
			String prDistrict = (String)parameters.get("prDistrict");
			String prWard = (String)parameters.get("prWard");
			String crCountry = (String)parameters.get("crCountry");
			String crAddress = (String)parameters.get("crAddress");
			String crProvince = (String)parameters.get("crProvince");
			String crDistrict = (String)parameters.get("crDistrict");
			String crWard = (String)parameters.get("crWard");
			String jqxSpecialSkillEditor = (String)parameters.get("jqxSpecialSkillEditor");
			String badHealth = (String)parameters.get("badHealth");
			String badHealthDetail = (String)parameters.get("badHealthDetail");
			String badInfo = (String)parameters.get("badInfo");
			String badInfoDetail = (String)parameters.get("badInfoDetail");
			String sourceTypeId = (String)parameters.get("sourceTypeId");
			String referredByPartyId = (String)parameters.get("referredByPartyId");
			String partyIdWork = (String)parameters.get("partyIdWork");
			String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
			Timestamp inductedCompletionDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("inductedCompletionDate"));
			Timestamp inductedStartDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("inductedStartDate"));
			Long basicSalary = (Long)JqxWidgetSevices.convert("java.lang.Long", (String)parameters.get("basicSalary"));
			Long otherAllowance = (Long)JqxWidgetSevices.convert("java.lang.Long", (String)parameters.get("otherAllowance"));
			Long percentBasicSalary = (Long)JqxWidgetSevices.convert("java.lang.Long", (String)parameters.get("percentBasicSalary"));
			Long phoneAllowance = (Long)JqxWidgetSevices.convert("java.lang.Long", (String)parameters.get("phoneAllowance"));
			Long trafficAllowance = (Long)JqxWidgetSevices.convert("java.lang.Long", (String)parameters.get("trafficAllowance"));
			List<Map<String, String>> fmData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("fmData"));
			List<Map<String, String>> eduData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("eduData"));
			List<Map<String, String>> wpData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("wpData"));
			List<Map<String, String>> skillData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("skillData"));
			List<Map<String, String>> aqcData = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("aqcData"));
        	
			//Begin transaction
			beganTransaction = TransactionUtil.begin();
			
			//Create Applicant
			Map<String, Object> createApplicantCtx = FastMap.newInstance();
			createApplicantCtx.put("birthDate", birthDate);
			createApplicantCtx.put("lastName", lastName);
			createApplicantCtx.put("middleName", middleName);
			createApplicantCtx.put("firstName", firstName);
			createApplicantCtx.put("gender", gender);
			createApplicantCtx.put("birthPlace", birthPlace);
			createApplicantCtx.put("height", height);
			createApplicantCtx.put("weight", weight);
			createApplicantCtx.put("idNumber", idNumber);
			createApplicantCtx.put("idIssuePlace", idIssuePlace);
			createApplicantCtx.put("idIssueDate", idIssueDate);
			createApplicantCtx.put("maritalStatus", maritalStatus);
			createApplicantCtx.put("numberChildren", numberChildren);
			createApplicantCtx.put("ethnicOrigin", ethnicOrigin);
			createApplicantCtx.put("religion", religion);
			createApplicantCtx.put("nativeLand", nativeLand);
			createApplicantCtx.put("userLogin", userLogin);
			Map<String, Object> createApplRs = ApplicantServiceHelper.createApplicant(dispatcher, createApplicantCtx, okay);
			if (ServiceUtil.isSuccess(createApplRs)) {
				//Create Contact
				Map<String, Object> createPartyContactCtx = FastMap.newInstance();
				createPartyContactCtx.put("homeTel", homeTel);
				createPartyContactCtx.put("mobile", mobile);
				createPartyContactCtx.put("diffTel", diffTel);
				createPartyContactCtx.put("email", email);
				createPartyContactCtx.put("prAddress", prAddress);
				createPartyContactCtx.put("prCountry", prCountry);
				createPartyContactCtx.put("prProvince", prProvince);
				createPartyContactCtx.put("prDistrict", prDistrict);
				createPartyContactCtx.put("prWard", prWard);
				createPartyContactCtx.put("crCountry", crCountry);
				createPartyContactCtx.put("crProvince", crProvince);
				createPartyContactCtx.put("crDistrict", crDistrict);
				createPartyContactCtx.put("crWard", crWard);
				createPartyContactCtx.put("userLogin", userLogin);
				createPartyContactCtx.put("partyId", createApplRs.get("partyId"));
				createPartyContactCtx.put("crAddress", crAddress);
				ApplicantServiceHelper.createPartyContact(dispatcher, createPartyContactCtx, okay);
				
				//Create PartyHealth
				Map<String, Object> createPartyHealthCtx = FastMap.newInstance();
				createPartyHealthCtx.put("partyId", createApplRs.get("partyId"));
				createPartyHealthCtx.put("badHealth", badHealth);
				createPartyHealthCtx.put("badHealthDetail", badHealthDetail);
				createPartyHealthCtx.put("badInfo", badInfo);
				createPartyHealthCtx.put("badInfoDetail", badInfoDetail);
				createPartyHealthCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyHealth(dispatcher, createPartyHealthCtx, okay);
				
				//Create Family Member
				Map<String, Object> createFamilyMemCtx = FastMap.newInstance();
				createFamilyMemCtx.put("fmData", fmData);
				createFamilyMemCtx.put("partyId", createApplRs.get("partyId"));
				createFamilyMemCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyFamilyMem(dispatcher, createFamilyMemCtx, okay);
				
				//Create Education
				Map<String, Object> createEducationCtx = FastMap.newInstance();
				createEducationCtx.put("eduData", eduData);
				createEducationCtx.put("partyId", createApplRs.get("partyId"));
				createEducationCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyEducation(dispatcher, createEducationCtx, okay);
				
				//Create Working Process
				Map<String, Object> createWorkingProcessCtx = FastMap.newInstance();
				createWorkingProcessCtx.put("wpData", wpData);
				createWorkingProcessCtx.put("partyId", createApplRs.get("partyId"));
				createWorkingProcessCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyWorkProcess(dispatcher, createWorkingProcessCtx, okay);
				
				//Create Skill
				Map<String, Object> createSkillCtx = FastMap.newInstance();
				createSkillCtx.put("skillData", skillData);
				createSkillCtx.put("partyId", createApplRs.get("partyId"));
				createSkillCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartySkill(dispatcher, createSkillCtx, okay);
				
				//Create Acquaintance
				Map<String, Object> createAqcCtx = FastMap.newInstance();
				createAqcCtx.put("aqcData", aqcData);
				createAqcCtx.put("partyId", createApplRs.get("partyId"));
				createAqcCtx.put("userLogin", userLogin);
				ApplicantServiceHelper.createPartyAqc(dispatcher, createAqcCtx, okay);
				
				//Create Party Attribute
				GenericValue partyAttribute = delegator.makeValue("PartyAttribute");
				partyAttribute.put("partyId", createApplRs.get("partyId"));
				partyAttribute.put("attrName", "specialSkill");
				partyAttribute.put("attrValue", jqxSpecialSkillEditor);
				partyAttribute.create();
				
				//Create EmploymentApplication
				if(!UtilValidate.isEmpty(referredByPartyId) || !UtilValidate.isEmpty(sourceTypeId)) {
					GenericValue emplApplication = delegator.makeValue("EmploymentApplication");
					String applicationId = delegator.getNextSeqId("EmploymentApplication");
					emplApplication.put("applicationId", applicationId);
					emplApplication.put("applyingPartyId", createApplRs.get("partyId"));
					emplApplication.put("referredByPartyId", referredByPartyId);
					emplApplication.put("employmentAppSourceTypeId", sourceTypeId);
					emplApplication.put("workEffortId",workEffortId);
					emplApplication.put("statusId", "APPL_RECRUITING");
					emplApplication.create();
				}
				
				//Create OfferProbation
				Map<String, Object> createOfferProbationCtx = FastMap.newInstance();
				createOfferProbationCtx.put("basicSalary", basicSalary);
				createOfferProbationCtx.put("emplPositionTypeId", emplPositionTypeId);
				createOfferProbationCtx.put("inductedCompletionDate", inductedCompletionDate);
				createOfferProbationCtx.put("inductedStartDate", inductedStartDate);
				createOfferProbationCtx.put("otherAllowance", otherAllowance);
				createOfferProbationCtx.put("partyId", createApplRs.get("partyId"));
				createOfferProbationCtx.put("partyIdWork", partyIdWork);
				createOfferProbationCtx.put("percentBasicSalary", percentBasicSalary);
				createOfferProbationCtx.put("phoneAllowance", phoneAllowance);
				createOfferProbationCtx.put("trafficAllowance", trafficAllowance);
				createOfferProbationCtx.put("workEffortId", workEffortId);
				createOfferProbationCtx.put("statusId", "PROB_INIT");
				createOfferProbationCtx.put("userLogin", userLogin);
				dispatcher.runSync("createOfferProbation", createOfferProbationCtx);
			}
			
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GeneralServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}catch (IllegalArgumentException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}finally {
			if (!okay) {
				//Roll back transaction
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing create applicant", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			} else {
				//Commit transaction
				try {
					TransactionUtil.commit(beganTransaction);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.SUCCESS_MESSAGE);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}
		}
        
        
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String createRecruitmentProcess(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        boolean beganTransaction = false;
        boolean okay = true;
        try {
        	 //Get parameters
    		 Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        	 List<Map<String,String>> listRounds = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listRounds"));
             String workEffortName = (String)parameters.get("workEffortName");
             Timestamp estimatedStartDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("estimatedStartDate"));
             Timestamp estimatedCompletionDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("estimatedCompletionDate"));
             String description = (String)parameters.get("description");
             String resourceNumber = (String)parameters.get("resourceNumber");
             String workLocation = (String)parameters.get("workLocation");
             String recruitmentTypeId = (String)parameters.get("recruitmentTypeId");
             String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
             String recruitmentFormId = (String)parameters.get("recruitmentFormId");
             String jobRequestId = (String)parameters.get("jobRequestId");
             String partyId = (String)parameters.get("partyId");
        	 String ctPartyId = (String)parameters.get("ctPartyId");
        	 String ctEmplPositionTypeId = (String)parameters.get("ctEmplPositionTypeId");
        	 String ctEmail = (String)parameters.get("ctEmail");
        	 String ctMobile = (String)parameters.get("ctMobile");
             //Begin Transaction
             beganTransaction = TransactionUtil.begin();
             Map<String, Object> createWorkEffortCtx = FastMap.newInstance();
             createWorkEffortCtx.put("workEffortName", workEffortName);
			 createWorkEffortCtx.put("estimatedStartDate", estimatedStartDate);
			 createWorkEffortCtx.put("estimatedCompletionDate", estimatedCompletionDate);
			 createWorkEffortCtx.put("currentStatusId", "RECR_PCS_SCHEDULED");
			 createWorkEffortCtx.put("workEffortTypeId", "RECRUITMENT_PROCESS");
			 if(UtilValidate.isEmpty(jobRequestId)) {
				 //Check if sale recruitment
				 createWorkEffortCtx.put("workEffortPurposeTypeId", "RP_SALEEMPL");
			 }else {
				//Check if sale recruitment
				 createWorkEffortCtx.put("workEffortPurposeTypeId", "RP_OFFICEEMPL");
			 }
			 createWorkEffortCtx.put("description", description);
			 createWorkEffortCtx.put("userLogin", userLogin);
			 Map<String, Object> createWorkEffortResult = dispatcher.runSync("createWorkEffort", createWorkEffortCtx);
			 if(ServiceUtil.isSuccess(createWorkEffortResult)) {
				
				//Create WorkEffort Attributes
				GenericValue workEffortAttrRes = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrRes.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrRes.put("attrName", "resourceNumber");
				workEffortAttrRes.put("attrValue", resourceNumber);
				workEffortAttrRes.create();
				
				GenericValue workEffortAttrWL = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrWL.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrWL.put("attrName", "workLocation");
				workEffortAttrWL.put("attrValue", workLocation);
				workEffortAttrWL.create();
				
				GenericValue workEffortAttrRT = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrRT.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrRT.put("attrName", "recruitmentTypeId");
				workEffortAttrRT.put("attrValue", recruitmentTypeId);
				workEffortAttrRT.create();
				
				GenericValue workEffortAttrEPT = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrEPT.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrEPT.put("attrName", "emplPositionTypeId");
				workEffortAttrEPT.put("attrValue", emplPositionTypeId);
				workEffortAttrEPT.create();
				
				GenericValue workEffortAttrRF = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrRF.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrRF.put("attrName", "recruitmentFormId");
				workEffortAttrRF.put("attrValue", recruitmentFormId);
				workEffortAttrRF.create();
				
				GenericValue workEffortAttrParty = delegator.makeValue("WorkEffortAttribute");
				workEffortAttrParty.put("workEffortId", createWorkEffortResult.get("workEffortId"));
				workEffortAttrParty.put("attrName", "partyId");
				workEffortAttrParty.put("attrValue", partyId);
				workEffortAttrParty.create();
				
				if(!UtilValidate.isEmpty(jobRequestId)) {
					GenericValue workEffortRequestFul = delegator.makeValue("WorkEffortRequestFulfillment");
					workEffortRequestFul.put("workEffortId", createWorkEffortResult.get("workEffortId"));
					workEffortRequestFul.put("jobRequestId", jobRequestId);
					workEffortRequestFul.create();
				}
				
				//Create Contactor
				if(!UtilValidate.isEmpty(ctPartyId)) {
					Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
					createPartyRoleCtx.put("partyId", ctPartyId);
					createPartyRoleCtx.put("roleTypeId", "CONTACTOR");
					createPartyRoleCtx.put("userLogin", userLogin);
					Map<String, Object> createPartyRoleRs = dispatcher.runSync("createPartyRole", createPartyRoleCtx);
					if(ServiceUtil.isSuccess(createPartyRoleRs)) {
						GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment");
						workEffortPartyAssignment.put("workEffortId", createWorkEffortResult.get("workEffortId"));
						workEffortPartyAssignment.put("partyId", ctPartyId);
						workEffortPartyAssignment.put("roleTypeId", "CONTACTOR");
						workEffortPartyAssignment.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
						workEffortPartyAssignment.put("assignedByUserLoginId",userLogin.get("partyId"));
						workEffortPartyAssignment.put("statusId", "CTR_RECRUITING");
						workEffortPartyAssignment.put("statusDateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
						workEffortPartyAssignment.create();
						
						GenericValue workEffortAttrCPT = delegator.makeValue("WorkEffortAttribute");
						workEffortAttrCPT.put("workEffortId", createWorkEffortResult.get("workEffortId"));
						workEffortAttrCPT.put("attrName", "ctPartyId");
						workEffortAttrCPT.put("attrValue", ctPartyId);
						workEffortAttrCPT.create();
						
						GenericValue workEffortAttrCEP = delegator.makeValue("WorkEffortAttribute");
						workEffortAttrCEP.put("workEffortId", createWorkEffortResult.get("workEffortId"));
						workEffortAttrCEP.put("attrName", "ctEmplPositionTypeId");
						workEffortAttrCEP.put("attrValue", ctEmplPositionTypeId);
						workEffortAttrCEP.create();
						
						GenericValue workEffortAttrCM = delegator.makeValue("WorkEffortAttribute");
						workEffortAttrCM.put("workEffortId", createWorkEffortResult.get("workEffortId"));
						workEffortAttrCM.put("attrName", "ctMobile");
						workEffortAttrCM.put("attrValue", ctMobile);
						workEffortAttrCM.create();
						
						GenericValue workEffortAttrCE = delegator.makeValue("WorkEffortAttribute");
						workEffortAttrCE.put("workEffortId", createWorkEffortResult.get("workEffortId"));
						workEffortAttrCE.put("attrName", "ctEmail");
						workEffortAttrCE.put("attrValue", ctEmail);
						workEffortAttrCE.create();
					}
				}
				
				//Create Rounds
				for(Map<String, String> item: listRounds) {
					Map<String, Object> createWorkEffortItemCtx = FastMap.newInstance();
					createWorkEffortItemCtx.put("workEffortName", item.get("workEffortName"));
					createWorkEffortItemCtx.put("estimatedStartDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("estimatedStartDate")));
					createWorkEffortItemCtx.put("estimatedCompletionDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("estimatedCompletionDate")));
					createWorkEffortItemCtx.put("currentStatusId", "RECR_ROUND_SCHEDULED");
					createWorkEffortItemCtx.put("workEffortTypeId", item.get("workEffortTypeId"));
					createWorkEffortItemCtx.put("description", item.get("description"));
					createWorkEffortItemCtx.put("locationDesc", item.get("location"));
					createWorkEffortItemCtx.put("userLogin", userLogin);
					Map<String, Object> createWorkEffortItemResult =  dispatcher.runSync("createWorkEffort", createWorkEffortItemCtx);
					if (ServiceUtil.isSuccess(createWorkEffortItemResult)) {
						//Create Association
						Map<String, Object> createWorkEffortAssocCtx = FastMap.newInstance();
						createWorkEffortAssocCtx.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
						createWorkEffortAssocCtx.put("workEffortIdFrom", createWorkEffortResult.get("workEffortId"));
						createWorkEffortAssocCtx.put("workEffortIdTo", createWorkEffortItemResult.get("workEffortId"));
						createWorkEffortAssocCtx.put("workEffortAssocTypeId", "WORK_EFF_BREAKDOWN");
						createWorkEffortAssocCtx.put("sequenceNum", item.get("sequenceNum"));
						createWorkEffortAssocCtx.put("userLogin", userLogin);
						Map<String, Object> createWorkEffortAssocResult =  dispatcher.runSync("createWorkEffortAssoc", createWorkEffortAssocCtx);
						if (!ServiceUtil.isSuccess(createWorkEffortAssocResult)) {
							okay = false;
						}
					}else {
						okay = false;
					}
				}
			}else {
				okay = false;
			}
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}finally {
			if (!okay) {
				//Roll back transaction
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing create recruitment process", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			} else {
				//Commit transaction
				try {
					TransactionUtil.commit(beganTransaction);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}
		}
               
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String agreePreliminary(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        boolean beganTransaction = false;
        boolean okay = true;
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			List<Map<String, String>> listApplicant = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listApplicant"));
			beganTransaction  = TransactionUtil.begin();
			for(Map<String, String> item: listApplicant) {
				String workEffortId = item.get("workEffortId");
				List<GenericValue> listChildWorkEffort = delegator.findByAnd("WorkEffortAssocToView", UtilMisc.toMap("workEffortIdFrom", workEffortId), UtilMisc.toList("sequenceNum ASC"), false);
				
				//Create WorkEffort Party Assignment
				GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment");
				workEffortPartyAssignment.put("workEffortId", EntityUtil.getFirst(listChildWorkEffort).getString("workEffortId"));
				workEffortPartyAssignment.put("partyId", item.get("partyId"));
				workEffortPartyAssignment.put("roleTypeId", "APPLICANT");
				workEffortPartyAssignment.put("availabilityStatusId", "AAS_CONTACTYET");
				workEffortPartyAssignment.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.put("assignedByUserLoginId",userLogin.get("partyId"));
				String statusId = "APPL_RECRUITING";
				if("ROUND_SELECTED".equals(EntityUtil.getFirst(listChildWorkEffort).getString("workEffortTypeId"))) {
					statusId = "APPL_PASSED";
					List<GenericValue> listParentWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", item.get("partyId")),null,false);
					GenericValue parentWorkEffortPartyAss = EntityUtil.getFirst(listParentWorkEffortPartyAss);
					parentWorkEffortPartyAss.put("statusId", statusId);
					parentWorkEffortPartyAss.store();
				}
				workEffortPartyAssignment.put("statusId", statusId);
				workEffortPartyAssignment.put("statusDateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.create();
			}
        } catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}finally {
			if(!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
	
	public static String scoreInterview(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        boolean beganTransaction = false;
        boolean okay = true;
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			String resultId = (String)parameters.get("resultId");
			String workEffortId = (String)parameters.get("workEffortId");
			String partyId = (String)parameters.get("partyId");
			String isNextRound = (String)parameters.get("isNextRound");
			String face = (String)parameters.get("face");
			String figure = (String)parameters.get("figure");
			String voice = (String)parameters.get("voice");
			String communication = (String)parameters.get("communication");
			String confidence = (String)parameters.get("confidence");
			String circumstance = (String)parameters.get("circumstance");
			String agility = (String)parameters.get("agility");
			String logic = (String)parameters.get("logic");
			String answer = (String)parameters.get("answer");
			String honest = (String)parameters.get("honest");
			String experience = (String)parameters.get("experience");
			String expertise = (String)parameters.get("expertise");
			String workChangeId = (String)parameters.get("workChangeId");
			String parentBackgroundId = (String)parameters.get("parentBackgroundId");
			String siblingBackgroundId = (String)parameters.get("siblingBackgroundId");
			String spousesBackgroundId = (String)parameters.get("spousesBackgroundId");
			String childBackgroundId = (String)parameters.get("childBackgroundId");
			String uniCertificateId = (String)parameters.get("uniCertificateId");
			String itCertificateId = (String)parameters.get("itCertificateId");
			String engCertificateId = (String)parameters.get("engCertificateId");
			String teamWorkId = (String)parameters.get("teamWorkId");
			String aloneWorkId = (String)parameters.get("aloneWorkId");
			String currentSal = (String)parameters.get("currentSal");
			String proposeSal = (String)parameters.get("proposeSal");
			String genaralRate = (String)parameters.get("genaralRate");
			String propose = (String)parameters.get("propose");
			String jobRequirable = (String)parameters.get("jobRequirable");
			
			//Create RecruitmentTestResult
			String recruitmentTestResultId = delegator.getNextSeqId("RecruitmentTestResult");
			GenericValue recruitmentTestResult = delegator.makeValue("RecruitmentTestResult");
			recruitmentTestResult.set("recruitmentTestResultId", recruitmentTestResultId);
			recruitmentTestResult.set("recruitmentTestResultTypeId", "RESULT_INTERVIEW");
			recruitmentTestResult.set("resultId", resultId);
			recruitmentTestResult.create();
			
			//Create RecruitmentInterviewResult
			GenericValue recruitmentInterviewResult = delegator.makeValue("RecruitmentInterviewResult");
			recruitmentInterviewResult.set("recruitmentTestResultId", recruitmentTestResultId);
			recruitmentInterviewResult.set("face", face);
			recruitmentInterviewResult.set("figure", figure);
			recruitmentInterviewResult.set("voice", voice);
			recruitmentInterviewResult.set("communication", communication);
			recruitmentInterviewResult.set("confidence", confidence);
			recruitmentInterviewResult.set("circumstance", circumstance);
			recruitmentInterviewResult.set("agility", agility);
			recruitmentInterviewResult.set("logic", logic);
			recruitmentInterviewResult.set("answer", answer);
			recruitmentInterviewResult.set("honest", honest);
			recruitmentInterviewResult.set("experience", experience);
			recruitmentInterviewResult.set("expertise", expertise);
			recruitmentInterviewResult.set("workChangeId", workChangeId);
			recruitmentInterviewResult.set("parentBackgroundId", parentBackgroundId);
			recruitmentInterviewResult.set("siblingBackgroundId", siblingBackgroundId);
			recruitmentInterviewResult.set("spousesBackgroundId", spousesBackgroundId);
			recruitmentInterviewResult.set("childBackgroundId", childBackgroundId);
			recruitmentInterviewResult.set("uniCertificateId", uniCertificateId);
			recruitmentInterviewResult.set("itCertificateId", itCertificateId);
			recruitmentInterviewResult.set("engCertificateId", engCertificateId);
			recruitmentInterviewResult.set("teamWorkId", teamWorkId);
			recruitmentInterviewResult.set("aloneWorkId", aloneWorkId);
			recruitmentInterviewResult.set("currentSal", currentSal);
			recruitmentInterviewResult.set("proposeSal", proposeSal);
			recruitmentInterviewResult.set("genaralRate", genaralRate);
			recruitmentInterviewResult.set("propose", propose);
			recruitmentInterviewResult.set("jobRequirable", jobRequirable);
			recruitmentInterviewResult.create();
			
			//Create ApplicantTestResult
			GenericValue applTestResult = delegator.makeValue("ApplicantTestResult");
			applTestResult.set("workEffortId", workEffortId);
			applTestResult.set("partyId", partyId);
			applTestResult.set("recruitmentTestResultId", recruitmentTestResultId);
			applTestResult.create();
			
			if(isNextRound.equals("Y")) {
				List<GenericValue> listWorkEffortAssoc = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdTo", workEffortId, "workEffortAssocTypeId", "WORK_EFF_BREAKDOWN"), null, false);
				List<GenericValue> listParentWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", EntityUtil.getFirst(listWorkEffortAssoc).getString("workEffortIdFrom"), "partyId", partyId),null,false);
				List<GenericValue> listChildWorkEffort = delegator.findByAnd("WorkEffortAssocToView", UtilMisc.toMap("workEffortIdFrom", EntityUtil.getFirst(listWorkEffortAssoc).getString("workEffortIdFrom")), UtilMisc.toList("sequenceNum ASC"), false);
				int index = 0;
				for(GenericValue item : listChildWorkEffort) {
					index += 1;
					if(item.getString("workEffortId").equals(workEffortId)) {
						 break;
					}
				}
				GenericValue nextRound = listChildWorkEffort.get(index);
				
				//Remove previous assignment 
				delegator.removeByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", "APPLICANT"));
				
				//Assign next
				//Create WorkEffort Party Assignment
				String statusId = "APPL_RECRUITING";
				if("ROUND_SELECTED".equals(nextRound.getString("workEffortTypeId"))) {
					statusId = "APPL_PASSED";
					GenericValue parentWorkEffortPartyAss = EntityUtil.getFirst(listParentWorkEffortPartyAss);
					parentWorkEffortPartyAss.put("statusId", statusId);
					parentWorkEffortPartyAss.store();
				}
				GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment");
				workEffortPartyAssignment.put("workEffortId", nextRound.getString("workEffortId"));
				workEffortPartyAssignment.put("partyId", partyId);
				workEffortPartyAssignment.put("roleTypeId", "APPLICANT");
				workEffortPartyAssignment.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.put("assignedByUserLoginId",userLogin.get("partyId"));
				workEffortPartyAssignment.put("statusId", statusId);
				workEffortPartyAssignment.put("availabilityStatusId", "AAS_CONTACTYET");
				workEffortPartyAssignment.put("statusDateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.create();
			}else {
				//Update applicant status
				List<GenericValue> listWorkEffort =  delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", "APPLICANT"), null, false);
				for (GenericValue item : listWorkEffort) {
					item.put("statusId", "APPL_FAIL");
					item.store();
				}
			}
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			return "error";
		}finally {
			if(!okay) {
				try {
						TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
				}
			}
		}
        
        return "success";
	}
	
	public static String scoreExam(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        boolean beganTransaction = false;
        boolean okay = true;
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			String workEffortId = (String)parameters.get("workEffortId");
			String partyId = (String)parameters.get("partyId");
			String resultId = (String)parameters.get("resultId");
			String isNextRound = (String)parameters.get("isNextRound");
			Long score = (Long)parameters.get("score");
			
			//Create RecruitmentTestResult
			String recruitmentTestResultId = delegator.getNextSeqId("RecruitmentTestResult");
			GenericValue recruitmentTestResult = delegator.makeValue("RecruitmentTestResult");
			recruitmentTestResult.set("recruitmentTestResultId", recruitmentTestResultId);
			recruitmentTestResult.set("recruitmentTestResultTypeId", "RESULT_EXAM");
			recruitmentTestResult.set("resultId", resultId);
			recruitmentTestResult.create();
			
			//Create RecruitmentExamResult
			GenericValue recruitmentExamResult = delegator.makeValue("RecruitmentExamResult");
			recruitmentExamResult.set("recruitmentTestResultId", recruitmentTestResultId);
			recruitmentExamResult.set("score", score);
			recruitmentExamResult.create();
			
			//Create ApplicantTestResult
			GenericValue applicantTestResult = delegator.makeValue("ApplicantTestResult");
			applicantTestResult.set("recruitmentTestResultId", recruitmentTestResultId);
			applicantTestResult.set("partyId", partyId);
			applicantTestResult.set("workEffortId", workEffortId);
			applicantTestResult.create();
			
			if(isNextRound.equals("Y")) {
				List<GenericValue> listWorkEffortAssoc = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdTo", workEffortId, "workEffortAssocTypeId", "WORK_EFF_BREAKDOWN"), null, false);
				List<GenericValue> listParentWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", EntityUtil.getFirst(listWorkEffortAssoc).getString("workEffortIdFrom"), "partyId", partyId),null,false);
				List<GenericValue> listChildWorkEffort = delegator.findByAnd("WorkEffortAssocToView", UtilMisc.toMap("workEffortIdFrom", EntityUtil.getFirst(listWorkEffortAssoc).getString("workEffortIdFrom")), UtilMisc.toList("sequenceNum ASC"), false);
				int index = 0;
				for(GenericValue item : listChildWorkEffort) {
					index += 1;
					if(item.getString("workEffortId").equals(workEffortId)) {
						 break;
					}
				}
				GenericValue nextRound = listChildWorkEffort.get(index);
				
				//Remove previous assignment 
				delegator.removeByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", "APPLICANT"));
				
				//Assign next
				//Create WorkEffort Party Assignment
				String statusId = "APPL_RECRUITING";
				if("ROUND_SELECTED".equals(nextRound.getString("workEffortTypeId"))) {
					statusId = "APPL_PASSED";
					GenericValue parentWorkEffortPartyAss = EntityUtil.getFirst(listParentWorkEffortPartyAss);
					parentWorkEffortPartyAss.put("statusId", statusId);
					parentWorkEffortPartyAss.store();
				}
				GenericValue workEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment");
				workEffortPartyAssignment.put("workEffortId", nextRound.getString("workEffortId"));
				workEffortPartyAssignment.put("partyId", partyId);
				workEffortPartyAssignment.put("roleTypeId", "APPLICANT");
				workEffortPartyAssignment.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.put("assignedByUserLoginId",userLogin.get("partyId"));
				workEffortPartyAssignment.put("statusId", statusId);
				workEffortPartyAssignment.put("availabilityStatusId", "AAS_CONTACTYET");
				workEffortPartyAssignment.put("statusDateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				workEffortPartyAssignment.create();
			}else {
				//Update applicant status
				List<GenericValue> listWorkEffort =  delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", "APPLICANT"), null, false);
				for (GenericValue item : listWorkEffort) {
					item.put("statusId", "APPL_FAIL");
					item.store();
				}
			}
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}finally {
			if(!okay) {
				try {
						TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
	
	public static String getWorkEffortAttr(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        String attrValue = PartyUtil.getWorkEffortAttr(delegator, (String)parameters.get("partyId"), (String)parameters.get("attrName"));
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        request.setAttribute("attrValue", attrValue);
        return "success";
	}
	
	public static String createProbAgreement(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        boolean beganTransaction = false;
        boolean okay = true;
        
        try {
        	beganTransaction  = TransactionUtil.begin();
        	//Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            String partyIdFrom = (String)parameters.get("partyIdFrom");
            String roleTypeIdFrom = (String)parameters.get("roleTypeIdFrom");
            String repPartyIdFrom = (String)parameters.get("repPartyIdFrom");
            String partyIdTo = (String)parameters.get("partyIdTo");
            /*String workEffortId = (String)parameters.get("workEffortId");*/
            String offerProbationId = (String)parameters.get("offerProbationId");
			Timestamp inductedStartDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("inductedStartDate"));
			Timestamp inductedCompletionDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("inductedCompletionDate"));
			//String recruitmentTypeId = (String)parameters.get("recruitmentTypeId");
			String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
			String partyIdWork = (String)parameters.get("partyIdWork");
			String basicSalary = (String)parameters.get("basicSalary");
			String percentBasicSalary = (String)parameters.get("percentBasicSalary");
			String phoneAllowance = (String)parameters.get("phoneAllowance");
			String trafficAllowance = (String)parameters.get("trafficAllowance");
			String otherAllowance = (String)parameters.get("otherAllowance");
			String agreementId = EntityUtil.getEmplAgreementSeqId(delegator);
			//Create PartyRole
			Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
			createPartyRoleCtx.put("partyId", partyIdTo);
			createPartyRoleCtx.put("roleTypeId", EMPLOYEE_ROLE);
			createPartyRoleCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyRoleRs = dispatcher.runSync("createPartyRole", createPartyRoleCtx);
			if(!ServiceUtil.isSuccess(createPartyRoleRs)) {
				okay = false;
			}
			
			//Create PartyRole
			createPartyRoleCtx.clear();
			createPartyRoleCtx.put("partyId", partyIdTo);
			createPartyRoleCtx.put("userLogin", userLogin);
			createPartyRoleRs = dispatcher.runSync("createPartyRole", createPartyRoleCtx);
			if(!ServiceUtil.isSuccess(createPartyRoleRs)) {
				okay = false;
			}
			GenericValue newAgreement = delegator.makeValue("Agreement");
	        newAgreement.put("partyIdFrom", partyIdFrom);
	        newAgreement.put("roleTypeIdFrom", roleTypeIdFrom);
	        newAgreement.put("partyIdTo", partyIdTo);
	        newAgreement.put("roleTypeIdTo", EMPLOYEE_ROLE);
	        newAgreement.put("agreementTypeId", "PROB_AGREEMENT");
	        newAgreement.put("thruDate", inductedCompletionDate);
	        newAgreement.put("fromDate", inductedStartDate);
	        newAgreement.put("statusId", "AGREEMENT_CREATED");
	        newAgreement.put("agreementId", agreementId);
	        newAgreement.put("agreementDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        newAgreement.create();
	        
	        //Create Agreement Attr
	        GenericValue newAgreementAttr = delegator.makeValue("AgreementAttribute");
	        newAgreementAttr.put("agreementId", agreementId);
	        newAgreementAttr.put("attrName", "representFor");
	        newAgreementAttr.put("attrValue", repPartyIdFrom);
	        newAgreementAttr.create();
        	//Create Terms
        	Map<String, Object> createAgreementTermCtx = FastMap.newInstance();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "WORK_TERM");
	        createAgreementTermCtx.put("fromDate", inductedStartDate);
	        createAgreementTermCtx.put("thruDate", inductedCompletionDate);
	        createAgreementTermCtx.put("textValue", partyIdWork);
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
	        createAgreementTermCtx.clear();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "JOB_POSITION_TERM");
	        createAgreementTermCtx.put("fromDate", inductedStartDate);
	        createAgreementTermCtx.put("thruDate", inductedCompletionDate);
	        createAgreementTermCtx.put("textValue", emplPositionTypeId);
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
	        createAgreementTermCtx.clear();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "SALARY_TERM");
	        createAgreementTermCtx.put("fromDate", inductedStartDate);
	        createAgreementTermCtx.put("thruDate", inductedCompletionDate);
	        createAgreementTermCtx.put("termValue", (Long.parseLong(basicSalary) * Long.parseLong(percentBasicSalary))/100);
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
			//Create Employment
			Map<String, Object> createEmploymentCtx = FastMap.newInstance();
			createEmploymentCtx.put("partyIdFrom", repPartyIdFrom);
			createEmploymentCtx.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			createEmploymentCtx.put("partyIdTo", partyIdTo);
			createEmploymentCtx.put("roleTypeIdTo", EMPLOYEE_ROLE);
			createEmploymentCtx.put("userLogin", userLogin);
			createEmploymentCtx.put("fromDate", inductedStartDate);
			createEmploymentCtx.put("thruDate", inductedCompletionDate);
			Map<String, Object> createEmploymentRs = dispatcher.runSync("createEmployment", createEmploymentCtx);
			if(!ServiceUtil.isSuccess(createEmploymentRs)) {
				okay = false;
			}
			//Create PartyRelationship
			Map<String, Object> createPartyRelationshipCtx = FastMap.newInstance();
			createPartyRelationshipCtx.put("partyIdFrom", partyIdWork);
			createPartyRelationshipCtx.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			createPartyRelationshipCtx.put("partyIdTo", partyIdTo);
			createPartyRelationshipCtx.put("roleTypeIdTo", EMPLOYEE_ROLE);
			createPartyRelationshipCtx.put("userLogin", userLogin);
			createPartyRelationshipCtx.put("fromDate", inductedStartDate);
			createPartyRelationshipCtx.put("thruDate", inductedCompletionDate);
			createPartyRelationshipCtx.put("partyRelationshipTypeId", "EMPLOYMENT");
			Map<String, Object> createPartyRelationshipRs = dispatcher.runSync("createPartyRelationship", createPartyRelationshipCtx);
			if(!ServiceUtil.isSuccess(createPartyRelationshipRs)) {
				okay = false;
			}
			//Create Position And Fulfillment
			Map<String, Object> createPositionAndFulCtx = FastMap.newInstance();
			createPositionAndFulCtx.put("internalOrgId", partyIdWork);
			createPositionAndFulCtx.put("partyId", partyIdTo);
			createPositionAndFulCtx.put("emplPositionTypeId", emplPositionTypeId);
			createPositionAndFulCtx.put("fromDate", inductedStartDate);
			createPositionAndFulCtx.put("thruDate", inductedCompletionDate);
			createPositionAndFulCtx.put("userLogin", userLogin);
			Map<String, Object> createPositionAndFulRs = dispatcher.runSync("createEmplPositionAndFulfillment", createPositionAndFulCtx);
			if(!ServiceUtil.isSuccess(createPositionAndFulRs)) {
				okay = false;
			}
			
			//Create Party Rate Amount
			Map<String, Object> createPartyRateAmountCtx = FastMap.newInstance();
			createPartyRateAmountCtx.put("rateTypeId", "_NA_");
			createPartyRateAmountCtx.put("rateCurrencyUomId", "VND");
			createPartyRateAmountCtx.put("periodTypeId", "MONTHLY");
			createPartyRateAmountCtx.put("workEffortId", "_NA_");
			createPartyRateAmountCtx.put("partyId", partyIdTo);
			createPartyRateAmountCtx.put("emplPositionTypeId", emplPositionTypeId);
			createPartyRateAmountCtx.put("fromDate", inductedStartDate);
			createPartyRateAmountCtx.put("thruDate", inductedCompletionDate);
			Long rateAmount = (Long.parseLong(basicSalary) * Long.parseLong(percentBasicSalary))/100;
			createPartyRateAmountCtx.put("rateAmount",BigDecimal.valueOf(rateAmount));
			createPartyRateAmountCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyRateAmountRs = dispatcher.runSync("createPartyRateAmount", createPartyRateAmountCtx);
			if(!ServiceUtil.isSuccess(createPartyRateAmountRs)) {
				okay = false;
			}
			
			//Create phoneAllowance
			Map<String, Object> phoneAllowanceCtx = FastMap.newInstance();
			phoneAllowanceCtx.put("code", "PHU_CAP_DIEN_THOAI");
			phoneAllowanceCtx.put("partyId", partyIdTo);
			phoneAllowanceCtx.put("value", phoneAllowance);
			phoneAllowanceCtx.put("userLogin", userLogin);
			phoneAllowanceCtx.put("fromDate", inductedStartDate);
			phoneAllowanceCtx.put("thruDate", inductedCompletionDate);
			dispatcher.runSync("assignEmployeePayrollParameters", phoneAllowanceCtx);
			
			//Create trafficAllowance
			Map<String, Object> trafficAllowanceCtx = FastMap.newInstance();
			trafficAllowanceCtx.put("code", "PHU_CAP_XANG_XE");
			trafficAllowanceCtx.put("partyId", partyIdTo);
			trafficAllowanceCtx.put("value", trafficAllowance);
			trafficAllowanceCtx.put("userLogin", userLogin);
			trafficAllowanceCtx.put("fromDate", inductedStartDate);
			trafficAllowanceCtx.put("thruDate", inductedCompletionDate);
			dispatcher.runSync("assignEmployeePayrollParameters", trafficAllowanceCtx);
			
			//FIXME other allowance
			
			//Update Applicant Status
			GenericValue offerProbation = delegator.findOne("OfferProbation", UtilMisc.toMap("offerProbationId", offerProbationId),false);
			offerProbation.put("statusId", "PROB_PROB_AGR");
			try {
				offerProbation.store();
			} catch (Exception e) {
				Debug.log(e.getStackTrace().toString(), module);
				okay = false;
			}
			
        } catch (ParseException e) {
        	Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}finally {
			if(!okay) {
				try {
						TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					return "error";
				}
			}
		}
        
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
}
