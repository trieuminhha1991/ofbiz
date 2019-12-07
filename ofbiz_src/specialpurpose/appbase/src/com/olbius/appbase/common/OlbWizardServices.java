package com.olbius.appbase.common;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OlbWizardServices {
	public final static String module = OlbWizardServices.class.getName();
	public final static String resource = "AppBaseUiLabels";
	
	public static Map<String, Object> wizardGetListPartyOrg(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
    	try {
    		GenericValue isHaveSubsidiary = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "isHaveSubsidiary"), true);
    		List<EntityCondition> conds = FastList.newInstance();
    		if (isHaveSubsidiary != null && isHaveSubsidiary.get("systemValue") != null && isHaveSubsidiary.getString("systemValue").toLowerCase().equals("true")) {
    			// have subsidiary
    			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SUBSIDIARY"));
    			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    			conds.add(EntityUtil.getFilterByDateExpr());
    			listIterator = delegator.findList("PartyToAndPartyNameDetail", EntityCondition.makeCondition(conds), null, null, null, false);
    		} else {
    			// have company
    			listIterator = delegator.findByAnd("PartyFullNameDetail", UtilMisc.toMap("partyId", "company"), null, false);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling wizardGetListPartyOrg service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("results", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> wizardSetupOrganization(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			String partyName = (String) context.get("partyName");
			String phoneNumber = (String) context.get("phoneNumber");
			String emailAddress = (String) context.get("emailAddress");
			String officeSiteName = (String) context.get("officeSiteName");
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String currencyUomId = (String) context.get("currencyUomId");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String address1 = (String) context.get("address1");
			
			String fixOrganizationId = "company";
			
			// check exists party organization
			GenericValue party = null;
			if (UtilValidate.isNotEmpty(partyId)) {
				party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if (!fixOrganizationId.equals(partyId)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSOrganizationIdIsNotValid", locale));
				}
			}
			if (party == null) {
				partyId = fixOrganizationId;
				List<String> roleTypeIds = UtilMisc.toList("PARENT_ORGANIZATION", "INTERNAL_ORGANIZATIO", "OWNER");
				Map<String, Object> partyCreateResult = createPartyGroupOrPerson(delegator, dispatcher, locale, partyId, null, "PARTY_GROUP", partyName, 
						officeSiteName, currencyUomId, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, address1, phoneNumber, emailAddress, taxAuthInfos, roleTypeIds, null, null);
				if (ServiceUtil.isError(partyCreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyCreateResult));
				}
			}
			
			successResult.put("partyId", partyId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling wizardSetupOrganization service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	private static Map<String, Object> createPartyGroupOrPerson(Delegator delegator, LocalDispatcher dispatcher, Locale locale, 
			String partyId, String partyCode, String partyTypeId, String partyName, String officeSiteName, String currencyUomId, 
			String countryGeoId, String stateProvinceGeoId, String districtGeoId, String wardGeoId, String address1, 
			String phoneNumber, String emailAddress, String taxAuthInfos, List<String> roleTypeIds, Date birthDate, String gender) throws GenericServiceException, GenericEntityException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		if ("PERSON".equals(partyTypeId)) {
			// create person
			String lastName = null;
			String middleName = null;
			String firstName = null;
			String[] partyNameItem = partyName.split(" ");
			if (UtilValidate.isNotEmpty(partyNameItem)) {
				if (partyNameItem.length == 1) {
					firstName = partyNameItem[0];
				} else if (partyNameItem.length == 2) {
					lastName = partyNameItem[0];
					firstName = partyNameItem[1];
				} else if (partyNameItem.length > 2) {
					lastName = partyNameItem[0];
					firstName = partyNameItem[partyNameItem.length - 1];
					middleName = partyName.substring(0, partyName.length() - firstName.length());
					middleName = middleName.substring(lastName.length() + 1, middleName.length());
				}
			}
			
			String currentPassword = SalesUtil.getPropertyValue(delegator, "password.wizard.default");
			
			Map<String, Object> person = UtilMisc.toMap("lastName", lastName, "middleName", middleName, "firstName", firstName);
			person.put("partyId", partyId);
			person.put("partyTypeId", partyTypeId);
			person.put("description", partyName);
			person.put("statusId", "PARTY_ENABLED");
			person.put("birthDate", birthDate);
			person.put("gender", gender);
			person.put("userLoginId", partyCode);
			person.put("currentPassword", currentPassword);
			person.put("currentPasswordVerify", currentPassword);
			person.put("requirePasswordChange", "Y");
			Map<String, Object> partyCreateResult = dispatcher.runSync("createPersonAndUserLogin", person);
			if (ServiceUtil.isError(partyCreateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyCreateResult));
			}
			partyId = (String) partyCreateResult.get("partyId");
		} else {
			// create party organization
			Map<String, Object> partyGroup = UtilMisc.toMap("groupName", partyName, "officeSiteName", officeSiteName);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("description", partyName);
			partyGroup.put("statusId", "PARTY_ENABLED");
			partyGroup.put("preferredCurrencyUomId", currencyUomId);
			Map<String, Object> partyCreateResult = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(partyCreateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyCreateResult));
			}
			partyId = (String) partyCreateResult.get("partyId");
		}
		
		// store party code, because service "createPartyGroup" not store field "partyCode"
		if (UtilValidate.isEmpty(partyCode)) partyCode = partyId;
		delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyCode), EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
		
		// create roles
		if (UtilValidate.isNotEmpty(roleTypeIds)) {
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			for (String roleTypeId : roleTypeIds) {
				// dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
				toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId)));
			}
			delegator.storeAll(toBeStored);
		}
		
		// create relationships
		// NO RELATIONSHIP, because company is first party
		
		// create user login
		// NO USER LOGIN
		
		// create user login group
		// NO GROUP
		
		GenericValue systemUserLoginId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		// create portal address
		if (UtilValidate.isNotEmpty(countryGeoId)) {
			Map<String, Object> contactMechCtx = UtilMisc.toMap(
					"countryGeoId", countryGeoId,
					"stateProvinceGeoId", stateProvinceGeoId,
					"districtGeoId", districtGeoId,
					"wardGeoId", wardGeoId,
					"address1", address1,
					"city", stateProvinceGeoId,
					"userLogin", systemUserLoginId, "locale", locale);
			Map<String, Object> postalAddressCreateResult = dispatcher.runSync("createPostalAddress", contactMechCtx);
			if (ServiceUtil.isError(postalAddressCreateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(postalAddressCreateResult));
			}
			
			String postalAddressId = (String) postalAddressCreateResult.get("contactMechId");
			if (postalAddressId != null) {
				// add party to postal address
				Map<String, Object> partyPACreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("contactMechId", postalAddressId, "partyId", partyId, "userLogin", systemUserLoginId));
				if (ServiceUtil.isError(partyPACreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyPACreateResult));
				}
				// add others purpose into postal address
				List<String> postalAddressPurposes = UtilMisc.toList("BILLING_LOCATION", "PRIMARY_LOCATION", "PAYMENT_LOCATION");
				for (String postalAddressPurpose : postalAddressPurposes) {
					Map<String, Object> partyPAPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
							"contactMechId", postalAddressId, "contactMechPurposeTypeId", postalAddressPurpose, "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyPAPCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyPAPCreateResult));
					}
				}
			}
		}
		
		// create telecom number
		if (UtilValidate.isNotEmpty(phoneNumber)) {
			Map<String, Object> telecomNumberCreateResult = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap("contactNumber", phoneNumber, "askForName", partyName, "userLogin", systemUserLoginId));
			if (ServiceUtil.isError(telecomNumberCreateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(telecomNumberCreateResult));
			}
			String telecomNumberId = (String) telecomNumberCreateResult.get("contactMechId");
			if (telecomNumberId != null) {
				// add party to telecom number
				Map<String, Object> partyTELCreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("contactMechId", telecomNumberId, "partyId", partyId, "userLogin", systemUserLoginId));
				if (ServiceUtil.isError(partyTELCreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyTELCreateResult));
				}
				// add others purpose into postal address
				List<String> telecomNumberPurposes = UtilMisc.toList("PRIMARY_PHONE");
				for (String telecomNumberPurpose : telecomNumberPurposes) {
					Map<String, Object> partyTELPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
							"contactMechId", telecomNumberId, "contactMechPurposeTypeId", telecomNumberPurpose, "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyTELPCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyTELPCreateResult));
					}
				}
			}
		}
		
		// create email
		if (UtilValidate.isNotEmpty(emailAddress)) {
			Map<String, Object> emailCreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, 
					"contactMechTypeId", "EMAIL_ADDRESS", "infoString", emailAddress, "userLogin", systemUserLoginId));
			String emailId = (String) emailCreateResult.get("contactMechId");
			if (emailId != null) {
				// add others purpose into postal address
				List<String> emailPurposes = UtilMisc.toList("PRIMARY_EMAIL");
				for (String emailPurpose : emailPurposes) {
					Map<String, Object> partyEmPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
							"contactMechId", emailId, "contactMechPurposeTypeId", emailPurpose, "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyEmPCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyEmPCreateResult));
					}
				}
			}
		}
		
		// create tax info
		if (UtilValidate.isNotEmpty(taxAuthInfos)) {
			String defaultTaxAuthGeoId = "VNM";
			String defaultTaxAuthPartyId = "VNM_TAX";
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", systemUserLoginId));
			if (ServiceUtil.isError(resultTaxInfo)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultTaxInfo));
			}
		}
		
		successResult.put("partyId", partyId);
		
		return successResult;
	}
	
	public static Map<String, Object> wizardSetupSubsidiary(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		try {
			String partySubsidiary = (String) context.get("partySubsidiary");
			Boolean noHaveSubsidiary = (Boolean) context.get("noHaveSubsidiary");
			
			GenericValue organization = delegator.findOne("Party", UtilMisc.toMap("partyId", "company"), true);
			if (organization == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSNotFoundOrganization", locale));
			}
			
			if (!noHaveSubsidiary) {
				if (UtilValidate.isNotEmpty(partySubsidiary)) {
					JSONArray childrenArray = JSONArray.fromObject((String) partySubsidiary);
					for(int i = 0; i< childrenArray.size(); i++){
						JSONObject o = childrenArray.getJSONObject(i);
						
						String partyId = o.containsKey("partyId") ? o.getString("partyId") : null;
						String partyCode = o.containsKey("partyCode") ? o.getString("partyCode") : null;
						String groupName = o.containsKey("groupName") ? o.getString("groupName") : null;
						String phoneNumber = o.containsKey("phoneNumber") ? o.getString("phoneNumber") : null;
						String emailAddress = o.containsKey("emailAddress") ? o.getString("emailAddress") : null;
						String officeSiteName = o.containsKey("officeSiteName") ? o.getString("officeSiteName") : null;
						String taxAuthInfos = o.containsKey("taxAuthInfos") ? o.getString("taxAuthInfos") : null;
						String currencyUomId = o.containsKey("currencyUomId") ? o.getString("currencyUomId") : null;
						String countryGeoId = o.containsKey("countryGeoId") ? o.getString("countryGeoId") : null;
						String stateProvinceGeoId = o.containsKey("stateProvinceGeoId") ? o.getString("stateProvinceGeoId") : null;
						String districtGeoId = o.containsKey("districtGeoId") ? o.getString("districtGeoId") : null;
						String wardGeoId = o.containsKey("wardGeoId") ? o.getString("wardGeoId") : null;
						String address1 = o.containsKey("address1") ? o.getString("address1") : null;
						
						GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), true);
						if (party == null) {
							// create party subsidiary
							List<String> roleTypeIds = UtilMisc.toList("INTERNAL_ORGANIZATIO", "SUBSIDIARY", "OWNER", "BUYER", "BILL_FROM_VENDOR");
							Map<String, Object> partyCreateResult = createPartyGroupOrPerson(delegator, dispatcher, locale, partyId, partyCode, "LEGAL_ORGANIZATION", groupName, 
									officeSiteName, currencyUomId, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, address1, phoneNumber, emailAddress, taxAuthInfos, roleTypeIds, null, null);
							if (ServiceUtil.isError(partyCreateResult)) {
								return partyCreateResult;
							}
							
							// create relationship with company
							String organizationId = "company";
							GenericValue systemUserLoginId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
							Map<String, Object> partyRelResult = dispatcher.runSync("createPartyRelationship",
									UtilMisc.toMap("partyIdFrom", organizationId, "partyIdTo", partyId, "roleTypeIdFrom", "PARENT_ORGANIZATION",
											"roleTypeIdTo", "SUBSIDIARY", "partyRelationshipTypeId", "GROUP_ROLLUP", "userLogin", systemUserLoginId));
							if (ServiceUtil.isError(partyRelResult)) {
								return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult));
							}
						}
					}
				}
			}
			
			GenericValue isHaveSubsidiary = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "isHaveSubsidiary"), true);
			if (isHaveSubsidiary == null) {
				isHaveSubsidiary = delegator.makeValue("SystemConfig");
				isHaveSubsidiary.set("systemConfigId", "isHaveSubsidiary");
				isHaveSubsidiary.set("systemValue", "" + !noHaveSubsidiary);
				isHaveSubsidiary.set("description", "Company has Susidiaries");
				delegator.create(isHaveSubsidiary);
			} else {
				isHaveSubsidiary.put("systemValue", "" + !noHaveSubsidiary);
				delegator.store(isHaveSubsidiary);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling wizardSetupSubsidiary service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> wizardSetupHrManager(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			String partyName = (String) context.get("partyName");
			String phoneNumber = (String) context.get("phoneNumber");
			String emailAddress = (String) context.get("emailAddress");
			Long birthDateL = (Long) context.get("birthDate");
			String gender = (String) context.get("gender");
			List<String> organizationIds = (List<String>) context.get("organizationIds[]");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String address1 = (String) context.get("address1");
			

			if (UtilValidate.isEmpty(organizationIds)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSHaveNotYetChosenOrganization", locale));
			}
			
			// check exists party organization
			GenericValue party = null;
			if (UtilValidate.isNotEmpty(partyId)) {
				party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			}
			if (party == null && UtilValidate.isNotEmpty(partyCode)) {
				// check party code
				party = EntityUtil.getFirst(delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false));
			}
			if (party == null) {
				if (UtilValidate.isEmpty(partyId)) {
					partyId = "OLB" + delegator.getNextSeqId("Party");
				}
				if (UtilValidate.isEmpty(partyCode)) {
					partyCode = partyId;
				}
				
				Date birthDate = birthDateL != null ? new Date(birthDateL) : null;
				
				List<String> roleTypeIds = UtilMisc.toList("EMPLOYEE", "HR_MANAGER");
				Map<String, Object> partyCreateResult = createPartyGroupOrPerson(delegator, dispatcher, locale, partyId, partyCode, "PERSON", partyName, 
						null, null, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, address1, phoneNumber, emailAddress, null, roleTypeIds, birthDate, gender);
				if (ServiceUtil.isError(partyCreateResult)) {
					return partyCreateResult;
				}
				
				partyId = (String) partyCreateResult.get("partyId");
				String userLoginId = partyCode;
				GenericValue hrManagerUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
				if (hrManagerUserLogin == null) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSCreateUserLoginForHrManagerIsNotSuccessfull", locale));
				}

				GenericValue systemUserLoginId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				
				// create HR department
				for (String organizationId : organizationIds) {
					GenericValue organization = delegator.findOne("Party", UtilMisc.toMap("partyId", organizationId), false);
					List<String> hrDeptRoles = UtilMisc.toList("INTERNAL_ORGANIZATIO", "HR_DEPARTMENT");
					Map<String, Object> hrDeptCreateResult = createPartyGroupOrPerson(delegator, dispatcher, locale, 
							"DHRM_" + organizationId, null, "LEGAL_ORGANIZATION", "Phong HCNS thuoc " + organizationId, 
							null, organization.getString("preferredCurrencyUomId"), null, null, null, null, null, null, null, 
							null, hrDeptRoles, null, null);
					if (ServiceUtil.isError(hrDeptCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(hrDeptCreateResult));
					}
					
					String hrDeptId = (String) hrDeptCreateResult.get("partyId");
					
					// create relationship with organization
					Map<String, Object> partyRelResult = dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", organizationId, "partyIdTo", hrDeptId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
									"roleTypeIdTo", "HR_DEPARTMENT", "partyRelationshipTypeId", "GROUP_ROLLUP", "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyRelResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult));
					}
					
					// apply HR manager into HR department
					Map<String, Object> partyRelResult1 = dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", hrDeptId, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
									"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "EMPLOYMENT", "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyRelResult1)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult1));
					}
					Map<String, Object> partyRelResult2 = dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", hrDeptId, "roleTypeIdFrom", "HR_MANAGER",
									"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "MANAGER", "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyRelResult2)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult2));
					}
					Map<String, Object> partyRelResult3 = dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", "HR_MANAGER_SERCURITY", "partyIdTo", partyId, "roleTypeIdFrom", "SECURITY_GROUP",
									"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "SECURITY_GROUP_REL", "userLogin", systemUserLoginId));
					if (ServiceUtil.isError(partyRelResult3)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult3));
					}
					
					// apply security group for user login
					GenericValue userLoginSecurityGroup1 = delegator.makeValue("UserLoginSecurityGroup");
					userLoginSecurityGroup1.put("userLoginId", userLoginId);
					userLoginSecurityGroup1.put("groupId", "HRMADMIN");
					userLoginSecurityGroup1.put("organizationId", organizationId);
					userLoginSecurityGroup1.put("fromDate", UtilDateTime.nowTimestamp());
					delegator.create(userLoginSecurityGroup1);
					
					GenericValue userLoginSecurityGroup2 = delegator.makeValue("UserLoginSecurityGroup");
					userLoginSecurityGroup2.put("userLoginId", userLoginId);
					userLoginSecurityGroup2.put("groupId", "EMPLOYEE");
					userLoginSecurityGroup2.put("organizationId", organizationId);
					userLoginSecurityGroup2.put("fromDate", UtilDateTime.nowTimestamp());
					delegator.create(userLoginSecurityGroup2);
					
					hrManagerUserLogin.set("lastOrg", organizationId);
					delegator.store(hrManagerUserLogin);
				}
			}
			
			successResult.put("partyId", partyId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling wizardSetupHrManager service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}

	public static Map<String, Object> wizardSetupOlbiusAdmin(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		try {
			Boolean useHrManager = (Boolean) context.get("useHrManager");
			String partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			
			if (useHrManager) {
				// get HR manager
				List<EntityCondition> conds = new ArrayList<EntityCondition>();
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "HR_MANAGER"));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
				conds.add(EntityUtil.getFilterByDateExpr());
				GenericValue hrManagerRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
				if (hrManagerRel != null) {
					GenericValue hrManager = delegator.findOne("Party", UtilMisc.toMap("partyId", hrManagerRel.get("partyIdFrom")), false);
					if (hrManager != null) {
						partyId = hrManager.getString("partyId");
						partyCode = hrManager.getString("partyCode");
					}
				}
				
			} else {
				String partyName = (String) context.get("partyName");
				String phoneNumber = (String) context.get("phoneNumber");
				String emailAddress = (String) context.get("emailAddress");
				Long birthDateL = (Long) context.get("birthDate");
				String gender = (String) context.get("gender");
				String countryGeoId = (String) context.get("countryGeoId");
				String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
				String districtGeoId = (String) context.get("districtGeoId");
				String wardGeoId = (String) context.get("wardGeoId");
				String address1 = (String) context.get("address1");
				
				// check exists party organization
				GenericValue party = null;
				if (UtilValidate.isNotEmpty(partyId)) {
					party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				}
				if (party == null && UtilValidate.isNotEmpty(partyCode)) {
					// check party code
					party = EntityUtil.getFirst(delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false));
				}
				if (party == null) {
					if (UtilValidate.isEmpty(partyId)) {
						partyId = "OLB" + delegator.getNextSeqId("Party");
					}
					if (UtilValidate.isEmpty(partyCode)) {
						partyCode = partyId;
					}
					
					Date birthDate = birthDateL != null ? new Date(birthDateL) : null;
					
					List<String> roleTypeIds = UtilMisc.toList("EMPLOYEE", "HR_MANAGER");
					Map<String, Object> partyCreateResult = createPartyGroupOrPerson(delegator, dispatcher, locale, partyId, partyCode, "PERSON", partyName, 
							null, null, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, address1, phoneNumber, emailAddress, null, roleTypeIds, birthDate, gender);
					if (ServiceUtil.isError(partyCreateResult)) {
						return partyCreateResult;
					}
					
					partyId = (String) partyCreateResult.get("partyId");
				}
			}
			
			if (UtilValidate.isEmpty(partyId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSNotFoundAccount", locale));
			}
			if (UtilValidate.isEmpty(partyCode)) partyCode = partyId;
			
			String organizationId = "company";
			String userLoginId = partyCode;
			
			if (!useHrManager) {
				GenericValue adminUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
				if (adminUserLogin == null) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSCreateUserLoginForHrManagerIsNotSuccessfull", locale));
				}
				adminUserLogin.set("lastOrg", organizationId);
				adminUserLogin.set("lastModule", "SYS_ADMINISTRATTION");
				delegator.store(adminUserLogin);
			}
			
			GenericValue systemUserLoginId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			// add role type to olbius admin
			Map<String, Object> partyRoleResult = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SYS_ADMINISTRATOR", "userLogin", systemUserLoginId));
			if (ServiceUtil.isError(partyRoleResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRoleResult));
			}
			
			// create relationship with organization
			Map<String, Object> partyRelResult = dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "SYS_ADMINISTRATOR",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYMENT", "userLogin", systemUserLoginId));
			if (ServiceUtil.isError(partyRelResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult));
			}
			if (!useHrManager) {
				Map<String, Object> partyRelResult1 = dispatcher.runSync("createPartyRelationship",
						UtilMisc.toMap("partyIdFrom", organizationId, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
								"roleTypeIdTo", "EMPLOYEE", "partyRelationshipTypeId", "EMPLOYMENT", "userLogin", systemUserLoginId));
				if (ServiceUtil.isError(partyRelResult1)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyRelResult1));
				}
			}
			
			// apply security group for user login
			GenericValue userLoginSecurityGroup1 = delegator.makeValue("UserLoginSecurityGroup");
			userLoginSecurityGroup1.put("userLoginId", userLoginId);
			userLoginSecurityGroup1.put("groupId", "SYSTEM_ADMINISTRATION");
			userLoginSecurityGroup1.put("organizationId", organizationId);
			userLoginSecurityGroup1.put("fromDate", UtilDateTime.nowTimestamp());
			delegator.create(userLoginSecurityGroup1);
			
			if (!useHrManager) {
				GenericValue userLoginSecurityGroup2 = delegator.makeValue("UserLoginSecurityGroup");
				userLoginSecurityGroup2.put("userLoginId", userLoginId);
				userLoginSecurityGroup2.put("groupId", "EMPLOYEE");
				userLoginSecurityGroup2.put("organizationId", organizationId);
				userLoginSecurityGroup2.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(userLoginSecurityGroup2);
			}
			
			successResult.put("partyId", partyId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling wizardSetupOlbiusAdmin service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	public static Map<String, Object> checkSetupComplete(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			boolean isComplete = true;
			
			// check exists company
			GenericValue company = delegator.findOne("Party", UtilMisc.toMap("partyId", "company"), true);
			if (company == null) {
				isComplete = false;
				successResult.put("isComplete", isComplete);
				return successResult;
			}
			
			// check user human resource manager
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "HR_MANAGER"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			conds.add(EntityUtil.getFilterByDateExpr());
			GenericValue hrManagerRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
			if (hrManagerRel == null) {
				isComplete = false;
				successResult.put("isComplete", isComplete);
				return successResult;
			}
			
			// check user olbius admin
			List<EntityCondition> conds2 = new ArrayList<EntityCondition>();
			conds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "SYS_ADMINISTRATOR"));
			conds2.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
			conds2.add(EntityUtil.getFilterByDateExpr());
			GenericValue olbiusAdminRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds2), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
			if (olbiusAdminRel == null) {
				isComplete = false;
				successResult.put("isComplete", isComplete);
				return successResult;
			}
			successResult.put("isComplete", isComplete);
		} catch (Exception e) {
			String errMsg = "Fatal error calling checkSetupComplete service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
}
