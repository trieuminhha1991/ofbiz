package com.olbius.baseecommerce.party;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.crm.CallcenterServices;

import javolution.util.FastMap;

public class ProfileUtils {

	public final static String module = ProfileUtils.class.getName();
	public static final String resource = "BaseEcommerceUiLabels";

	public static String quickCreateCustomer(Delegator delegator, LocalDispatcher dispatcher, String name, String gender, String birthDate)
			throws GenericEntityException, GenericServiceException{
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", system);
		party.putAll(CallcenterServices.demarcatePersonName(name));
		if(UtilValidate.isNotEmpty(birthDate)){
			long d = Long.parseLong(birthDate);
			Date date = new Date(d);
			party.put("birthDate", date);
		}
		party.put("gender", gender);
		Map<String, Object> outParty = dispatcher.runSync("createPerson", party);
		return (String) outParty.get("partyId");
	}
	public static void createUserLogin(HttpServletRequest request, HttpServletResponse response, LocalDispatcher dispatcher, String partyId, String username, String password, String passwordVerify, String email)
			throws GenericServiceException{
		Map<String, Object> party = FastMap.newInstance();
		party.put("partyId", partyId);
		party.put("request", request);
		party.put("response", response);
		party.put("username", username);
		party.put("emailAddress", email);
		party.put("password", password);
		party.put("passwordVerify", passwordVerify);
		Map<String, Object> createUserLogin = dispatcher.runSync("createUpdateUserLogin", party);
		if (ServiceUtil.isError(createUserLogin)) {
			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(createUserLogin));
		}
	}
	public static void quickCreatePartyRole(Delegator delegator, LocalDispatcher dispatcher, String partyId)
			throws GenericEntityException, GenericServiceException{
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("roleTypeId", "CUSTOMER");
		dispatcher.runSync("createPartyRole", party);
	}
	public static void updatePerson(Delegator delegator, LocalDispatcher dispatcher, String partyId, String name, String gender){
		try {
			GenericValue system = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> party = FastMap.newInstance();
			Map<String, Object> fullName = CallcenterServices
					.demarcatePersonName(name);
			party.put("userLogin", system);
			party.put("partyId", partyId);
			party.put("firstName", fullName.get("firstName"));
			party.put("middleName", fullName.get("middleName"));
			party.put("lastName", fullName.get("lastName"));
			party.put("gender", gender);
			Map<String, Object> outParty;
			outParty = dispatcher.runSync("updatePerson",
					party);
			partyId = (String) outParty.get("partyId");
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
	}
	public static String createPartyEmail(Delegator delegator, LocalDispatcher dispatcher, String partyId, String email)
			throws GenericEntityException, GenericServiceException{
		return createPartyEmail(delegator, dispatcher, partyId, email, "PRIMARY_EMAIL");
	}
	public static String createPartyEmail(Delegator delegator, LocalDispatcher dispatcher, String partyId, String email, String purpose)
			throws GenericEntityException, GenericServiceException{
		List<GenericValue> listEmail = delegator.findList("PartyContactWithPurpose",
				EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "infoString", email)),
				null, null, null, false);
		if(UtilValidate.isEmpty(listEmail)){
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> party = FastMap.newInstance();
			party.put("userLogin", system);
			party.put("partyId", partyId);
			party.put("emailAddress", email);
			party.put("contactMechPurposeTypeId", purpose);
			Map<String, Object> resultCtx = dispatcher.runSync("createPartyEmailAddress", party);
			if (ServiceUtil.isSuccess(resultCtx)) {
				return (String) resultCtx.get("contactMechId");
			}
		}
		return null;
	}
	public static String createPartyTelephone(Delegator delegator, LocalDispatcher dispatcher, String partyId, String phone)
			throws GenericEntityException, GenericServiceException{
		List<GenericValue> listPhone = delegator.findList("PartyContactDetailByPurpose",
				EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactNumber", phone)), null, null, null, false);
		if(UtilValidate.isEmpty(listPhone)){
			GenericValue system = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> party = FastMap.newInstance();
			party.put("userLogin", system);
			party.put("partyId", partyId);
			party.put("contactNumber", phone);
			Map<String, Object> resultCtx = dispatcher.runSync("createPartyEcommerceTelecomNumber", party);
			if (ServiceUtil.isSuccess(resultCtx)) {
				return (String) resultCtx.get("contactMechId");
			}
		}
		return null;
	}
	public static void createPartyRole(Delegator delegator, LocalDispatcher dispatcher, String partyId){
		createPartyRole(delegator, dispatcher, partyId, "CUSTOMER");
	}
	public static void createPartyRole(Delegator delegator, LocalDispatcher dispatcher, String partyId, String roleTypeId){
		try {
			GenericValue system = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> party = FastMap.newInstance();
			party.put("userLogin", system);
			party.put("partyId", partyId);
			party.put("roleTypeId", "CUSTOMER");
			dispatcher.runSync("createPartyRole", party);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
	}
	public static void createPartyProductStoreRole(Delegator delegator, LocalDispatcher dispatcher, String partyId, String productStoreId)
			throws GenericEntityException, GenericServiceException{
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("roleTypeId", "CUSTOMER");
		party.put("productStoreId", productStoreId);
		dispatcher.runSync("createProductStoreRole", party);
		
		productStoreId = productStoreId.equals("ECOMMERCE_01")?"ECOMMERCE_02":"ECOMMERCE_01";
		party.put("productStoreId", productStoreId);
		dispatcher.runSync("createProductStoreRole", party);
		
		delegator.create("PartyProfileDefault", UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId));
	}
	public static String createPostalAddress(Delegator delegator, LocalDispatcher dispatcher,
				String partyId, String toName, String address1, String city, String district, String productStoreId)
						throws GenericEntityException, GenericServiceException{
		return createPostalAddress(delegator, dispatcher, partyId, toName, null, address1, city, district, productStoreId);
	}
	public static String createPostalAddress(Delegator delegator, LocalDispatcher dispatcher,
			String partyId, String toName, String attnName, String address1, String city, String district, String productStoreId)
					throws GenericEntityException, GenericServiceException{
		address1 = address1.trim();
		GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", partyUserLogin);
		party.put("partyId", partyId);
		party.put("address1", address1);
		party.put("toName", toName);
		party.put("attnName", attnName);
		party.put("stateProvinceGeoId", city);
		party.put("city", city);
		party.put("countryGeoId", "VNM");
		party.put("districtGeoId", district);
		party.put("postalCode", "70000");
		party.put("productStoreId", productStoreId);
		party.put("setShippingPurpose", "Y");
		party.put("setBillingPurpose", "Y");
		Map<String, Object> res = dispatcher.runSync("createPostalAddressAndPurposes", party);
		return (String) res.get("contactMechId");
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> CustomerProfile(Delegator delegator, String partyId)
			throws GenericEntityException {
		Map<String, Object> customerProfile = FastMap.newInstance();
		GenericValue party = delegator.findOne("PersonAndPartyGroup", UtilMisc.toMap("partyId", partyId), false);
		customerProfile.put("partyFullName", party.getString("partyFullName").trim());
		customerProfile.put("gender", party.get("gender"));
		customerProfile.put("birthDate", party.get("birthDate"));
		List<Map<String, Object>> partyContactMechs = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
		for (Map<String, Object> m : partyContactMechs) {
			GenericValue contactMechType = (GenericValue) m.get("contactMechType");
			if (UtilValidate.isNotEmpty(contactMechType)) {
				String contactMechTypeId = contactMechType.getString("contactMechTypeId");
				switch (contactMechTypeId) {
				case "TELECOM_NUMBER":
					customerProfile.put("TELECOM_NUMBER", m);
					break;
				case "EMAIL_ADDRESS":
					customerProfile.put("EMAIL_ADDRESS", m);
					break;
				case "POSTAL_ADDRESS":
					List<GenericValue> partyContactMechPurposes = (List<GenericValue>) m.get("partyContactMechPurposes");
					for (GenericValue x : partyContactMechPurposes) {
						String contactMechPurposeTypeId = x.getString("contactMechPurposeTypeId");
						if ("BILLING_LOCATION".equals(contactMechPurposeTypeId)) {
							customerProfile.put("BILLING_LOCATION", m.get("postalAddress"));
						}
					}
					break;
				default:
					break;
				}
			}
		}
		return customerProfile;
	}
}
