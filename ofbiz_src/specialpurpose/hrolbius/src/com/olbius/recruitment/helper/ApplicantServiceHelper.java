package com.olbius.recruitment.helper;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.JqxWidgetSevices;

public class ApplicantServiceHelper {
	
	public static Map<String, Object> createApplicant(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay)
			throws GenericEntityException, GenericServiceException, GeneralServiceException, ParseException {
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> createPersonRs = dispatcher.runSync("createPerson", ServiceUtil.setServiceFields(dispatcher, "createPerson", context, userLogin, null, null));
		
		if(ServiceUtil.isSuccess(createPersonRs)) {
			Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
			String partyId = (String) createPersonRs.get("partyId");
			createPartyRoleCtx.put("partyId", partyId);
			createPartyRoleCtx.put("roleTypeId", "APPLICANT");
			createPartyRoleCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyRoleRs = dispatcher.runSync("createPartyRole", createPartyRoleCtx);
			if (!ServiceUtil.isSuccess(createPartyRoleRs)) {
				okay = false;
			}
		}else {
			okay = false;
		}
		return createPersonRs;
	}

	public static void createPartyContact(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay)throws GenericEntityException, GenericServiceException {

		//Create PostalAddress(purpose: 'PERMANENT_RESIDENCE')
		Map<String, Object> createPrPostalAddressCtx = FastMap.newInstance();
		String prAddress1 = (String) context.get("prAddress");
		String partyId = (String) context.get("partyId");
		String prCountryGeoId = (String) context.get("prCountry");
		String prStateProvinceGeoId = (String) context.get("prProvince");
		String prDistrictGeoId = (String)context.get("prDistrict");
		String prWardGeoId = (String)context.get("prWard");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		createPrPostalAddressCtx.put("address1", prAddress1);
		createPrPostalAddressCtx.put("countryGeoId", prCountryGeoId);
		createPrPostalAddressCtx.put("stateProvinceGeoId", prStateProvinceGeoId);
		createPrPostalAddressCtx.put("districtGeoId", prDistrictGeoId);
		createPrPostalAddressCtx.put("wardGeoId", prWardGeoId);
		createPrPostalAddressCtx.put("userLogin", userLogin);
		Map<String, Object> createPrCtRs = dispatcher.runSync("createContactMechGeo", createPrPostalAddressCtx);
		if(ServiceUtil.isSuccess(createPrCtRs)) {
			Map<String, Object> createPartyContactCtx = FastMap.newInstance();
			createPartyContactCtx.put("partyId", partyId);
			createPartyContactCtx.put("contactMechId", createPrCtRs.get("contactMechId"));
			createPartyContactCtx.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
			createPartyContactCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
			if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
				okay = false;
			}
		}else {
			okay = false;
		}
		
		//Create PostalAddress(purpose: 'CURRENT_RESIDENCE')
		Map<String, Object> createCrPostalAddressCtx = FastMap.newInstance();
		String crAddress1 = (String) context.get("crAddress");
		String crCountryGeoId = (String) context.get("crCountry");
		String crStateProvinceGeoId = (String) context.get("crProvince");
		String crDistrictGeoId = (String)context.get("crDistrict");
		String crWardGeoId = (String)context.get("crWard");
		
		createCrPostalAddressCtx.put("address1", crAddress1);
		createCrPostalAddressCtx.put("countryGeoId", crCountryGeoId);
		createCrPostalAddressCtx.put("stateProvinceGeoId", crStateProvinceGeoId);
		createCrPostalAddressCtx.put("districtGeoId", crDistrictGeoId);
		createCrPostalAddressCtx.put("wardGeoId", crWardGeoId);
		createCrPostalAddressCtx.put("userLogin", userLogin);
		Map<String, Object> createCrCtRs = dispatcher.runSync("createContactMechGeo", createCrPostalAddressCtx);
		if(ServiceUtil.isSuccess(createPrCtRs)) {
			Map<String, Object> createPartyContactCtx = FastMap.newInstance();
			createPartyContactCtx.put("partyId", partyId);
			createPartyContactCtx.put("contactMechId", createCrCtRs.get("contactMechId"));
			createPartyContactCtx.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
			createPartyContactCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
			if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
				okay = false;
			}
		}else {
			okay = false;
		}
		
		//Create TelecomNumber(purpose: 'PHONE_HOME')
		String homeTel = (String) context.get("homeTel");
		if (!UtilValidate.isEmpty(homeTel)) {
			Map<String, Object> createPhoneHomeCtx = FastMap.newInstance();
			createPhoneHomeCtx.put("contactNumber", homeTel);
			createPhoneHomeCtx.put("userLogin", userLogin);
			Map<String, Object> createPhoneHomeRs = dispatcher.runSync("createTelecomNumber", createPhoneHomeCtx);
			if(ServiceUtil.isSuccess(createPhoneHomeRs)) {
				Map<String, Object> createPartyContactCtx = FastMap.newInstance();
				createPartyContactCtx.put("partyId", partyId);
				createPartyContactCtx.put("contactMechId", createPhoneHomeRs.get("contactMechId"));
				createPartyContactCtx.put("contactMechPurposeTypeId", "PHONE_HOME");
				createPartyContactCtx.put("userLogin", userLogin);
				Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
				if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
					okay = false;
				}
			}else {
				okay = false;
			}
		}
		
		//Create TelecomNumber(purpose: 'PHONE_MOBILE')
		Map<String, Object> createPhoneMobileCtx = FastMap.newInstance();
		String mobile = (String) context.get("mobile");
		createPhoneMobileCtx.put("contactNumber", mobile);
		createPhoneMobileCtx.put("userLogin", userLogin);
		Map<String, Object> createPhoneMobileRs = dispatcher.runSync("createTelecomNumber", createPhoneMobileCtx);
		if(ServiceUtil.isSuccess(createPhoneMobileRs)) {
			Map<String, Object> createPartyContactCtx = FastMap.newInstance();
			createPartyContactCtx.put("partyId", partyId);
			createPartyContactCtx.put("contactMechId", createPhoneMobileRs.get("contactMechId"));
			createPartyContactCtx.put("contactMechPurposeTypeId", "PHONE_MOBILE");
			createPartyContactCtx.put("userLogin", userLogin);
			Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
			if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
				okay = false;
			}
		}else {
			okay = false;
		}
		
		//Create TelecomNumber(purpose: 'OTHER_PHONE')
		String diffTel = (String) context.get("diffTel");
		if(!UtilValidate.isEmpty(diffTel)) {
			Map<String, Object> createOtherPhoneCtx = FastMap.newInstance();
			createOtherPhoneCtx.put("contactNumber", diffTel);
			createOtherPhoneCtx.put("userLogin", userLogin);
			Map<String, Object> createOtherPhoneRs = dispatcher.runSync("createTelecomNumber", createOtherPhoneCtx);
			if(ServiceUtil.isSuccess(createOtherPhoneRs)) {
				Map<String, Object> createPartyContactCtx = FastMap.newInstance();
				createPartyContactCtx.put("partyId", partyId);
				createPartyContactCtx.put("contactMechId", createOtherPhoneRs.get("contactMechId"));
				createPartyContactCtx.put("contactMechPurposeTypeId", "OTHER_PHONE");
				createPartyContactCtx.put("userLogin", userLogin);
				Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
				if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
					okay = false;
				}
			}else {
				okay = false;
			}
		}
		
		//Create Email(purpose: 'PERSONAL_EMAIL')
		String email = (String) context.get("email");
		if(!UtilValidate.isEmpty(email)) {
			Map<String, Object> createEmailCtx = FastMap.newInstance();
			createEmailCtx.put("emailAddress", email);
			createEmailCtx.put("userLogin", userLogin);
			Map<String, Object> createEmailRs = dispatcher.runSync("createEmailAddress", createEmailCtx);
			if(ServiceUtil.isSuccess(createEmailRs)) {
				Map<String, Object> createPartyContactCtx = FastMap.newInstance();
				createPartyContactCtx.put("partyId", partyId);
				createPartyContactCtx.put("contactMechId", createEmailRs.get("contactMechId"));
				createPartyContactCtx.put("contactMechPurposeTypeId", "PERSONAL_EMAIL");
				createPartyContactCtx.put("userLogin", userLogin);
				Map<String, Object> createPartyContactMechRs = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
				if(!ServiceUtil.isSuccess(createPartyContactMechRs)) {
					okay = false;
				}
			}else {
				okay = false;
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static void createPartyFamilyMem(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws ParseException, GenericServiceException{
		List<Map<String, Object>> listFamilyMem = (List<Map<String,Object>>)context.get("fmData");
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for(Map<String, Object> item: listFamilyMem) {
			Map<String, Object> createFamilyMember = FastMap.newInstance();
			createFamilyMember.put("partyId", partyId);
			Date birthDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)item.get("birthDate"));
			createFamilyMember.put("birthDate", birthDate);
			createFamilyMember.put("emergencyContact", item.get("emergencyContact"));
			createFamilyMember.put("firstName", item.get("firstName"));
			createFamilyMember.put("lastName", item.get("lastName"));
			createFamilyMember.put("middleName", item.get("middleName"));
			createFamilyMember.put("occupation", item.get("occupation"));
			createFamilyMember.put("partyRelationshipTypeId", item.get("partyRelationshipTypeId"));
			createFamilyMember.put("phoneNumber", item.get("phoneNumber"));
			createFamilyMember.put("placeWork", item.get("placeWork"));
			createFamilyMember.put("userLogin", userLogin);
			Map<String, Object> createFamilyMemberRs = dispatcher.runSync("createPersonFamilyBackground", createFamilyMember);
			if(!ServiceUtil.isSuccess(createFamilyMemberRs)) {
				okay = false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createPartyEducation(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws ParseException, GenericServiceException{
		List<Map<String, Object>> listEducation = (List<Map<String,Object>>)context.get("eduData");
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for(Map<String, Object> item: listEducation) {
			Map<String, Object> createEducationCtx = FastMap.newInstance();
			createEducationCtx.put("partyId", partyId);
			createEducationCtx.put("classificationTypeId", item.get("classificationTypeId"));
			createEducationCtx.put("educationSystemTypeId", item.get("educationSystemTypeId"));
			createEducationCtx.put("schoolId", item.get("schoolId"));
			createEducationCtx.put("studyModeTypeId", item.get("studyModeTypeId"));
			createEducationCtx.put("majorId", item.get("majorId"));
			createEducationCtx.put("fromDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("fromDate")));
			createEducationCtx.put("thruDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("thruDate")));
			createEducationCtx.put("userLogin", userLogin);
			Map<String, Object> createPersonEducationRs = dispatcher.runSync("createPersonEducation", createEducationCtx);
			if(!ServiceUtil.isSuccess(createPersonEducationRs)) {
				okay = false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createPartyWorkProcess(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws ParseException, GenericServiceException{
		List<Map<String, Object>> listWorkProcess = (List<Map<String,Object>>)context.get("wpData");
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for(Map<String, Object> item: listWorkProcess) {
			Map<String, Object> createWorkingProcessCtx = FastMap.newInstance();
			createWorkingProcessCtx.put("partyId", partyId);
			createWorkingProcessCtx.put("companyName", item.get("companyName"));
			createWorkingProcessCtx.put("emplPositionTypeId", item.get("emplPositionTypeId"));
			createWorkingProcessCtx.put("jobDescription", item.get("jobDescription"));
			createWorkingProcessCtx.put("payroll", item.get("payroll"));
			createWorkingProcessCtx.put("terminationReasonId", item.get("terminationReasonId"));
			createWorkingProcessCtx.put("rewardDiscrip", item.get("rewardDiscrip"));
			createWorkingProcessCtx.put("fromDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("fromDate")));
			createWorkingProcessCtx.put("thruDate", (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("thruDate")));
			createWorkingProcessCtx.put("userLogin", userLogin);
			Map<String, Object> createWorkingProcessRs = dispatcher.runSync("createPersonWorkingProcess", createWorkingProcessCtx);
			if(!ServiceUtil.isSuccess(createWorkingProcessRs)) {
				okay = false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createPartySkill(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws ParseException, GenericServiceException{
		List<Map<String, Object>> listSkill = (List<Map<String,Object>>)context.get("skillData");
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for(Map<String, Object> item: listSkill) {
			Map<String, Object> createSkillCtx = FastMap.newInstance();
			createSkillCtx.put("partyId", partyId);
			createSkillCtx.put("skillLevelId", item.get("skillLevel"));
			createSkillCtx.put("skillTypeId", item.get("skillTypeId"));
			createSkillCtx.put("userLogin", userLogin);
			Map<String, Object> createSkillRs = dispatcher.runSync("HRCreatePartySkill", createSkillCtx);
			if(!ServiceUtil.isSuccess(createSkillRs)) {
				okay = false;
			}
		}
	}
	
	public static void createPartyHealth(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws GenericServiceException, GeneralServiceException{
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> createPartyHealthCtx = FastMap.newInstance();
		createPartyHealthCtx.put("partyId", context.get("partyId"));
		createPartyHealthCtx.put("badHealth", context.get("badHealth"));
		createPartyHealthCtx.put("badHealthDetail", context.get("badHealthDetail"));
		createPartyHealthCtx.put("badInfo", context.get("badInfo"));
		createPartyHealthCtx.put("badInfoDetail", context.get("badInfoDetail"));
		createPartyHealthCtx.put("userLogin", userLogin);
		Map<String, Object> createPartyHealthRs = dispatcher.runSync("createPartyHealth", createPartyHealthCtx);
		if(!ServiceUtil.isSuccess(createPartyHealthRs)) {
			okay = false;
		}
	}
	
	public static void createFinAccount(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws GenericServiceException, GeneralServiceException{
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> createFinAccountCtx = FastMap.newInstance();
		createFinAccountCtx.put("finAccountCode", context.get("finAccountCode"));
		createFinAccountCtx.put("finAccountName", context.get("finAccountName"));
		createFinAccountCtx.put("finAccountTypeId", "BANK_ACCOUNT");
		createFinAccountCtx.put("statusId", "FNACT_ACTIVE");
		createFinAccountCtx.put("ownerPartyId", context.get("partyId"));
		createFinAccountCtx.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
		createFinAccountCtx.put("userLogin", userLogin);
		Map<String, Object> createFinAccountRs = dispatcher.runSync("createFinAccount", createFinAccountCtx);
		if(!ServiceUtil.isSuccess(createFinAccountRs)) {
			okay = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createPartyAqc(LocalDispatcher dispatcher, Map<String, Object> context, Boolean okay) throws ParseException, GenericServiceException{
		List<Map<String, Object>> listAqc = (List<Map<String,Object>>)context.get("aqcData");
		String partyId = (String)context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for(Map<String, Object> item: listAqc) {
			Map<String, Object> createAqcCtx = FastMap.newInstance();
			createAqcCtx.put("partyId", partyId);
			createAqcCtx.put("birthDate", (Date)JqxWidgetSevices.convert("java.sql.Date", (String)item.get("birthDate")));
			createAqcCtx.put("firstName", item.get("firstName"));
			createAqcCtx.put("lastName", item.get("lastName"));
			createAqcCtx.put("middleName", item.get("middleName"));
			createAqcCtx.put("occupation", item.get("occupation"));
			createAqcCtx.put("partyRelationshipTypeId", item.get("partyRelationshipTypeId"));
			createAqcCtx.put("phoneNumber", item.get("phoneNumber"));
			createAqcCtx.put("placeWork", item.get("placeWork"));
			createAqcCtx.put("userLogin", userLogin);
			Map<String, Object> createAqcRs = dispatcher.runSync("createAcquaintance", createAqcCtx);
			if(!ServiceUtil.isSuccess(createAqcRs)) {
				okay = false;
			}
		}
	}
}
