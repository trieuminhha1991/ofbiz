package com.olbius.baseecommerce.party;

import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;

import com.olbius.crm.CallcenterServices;

import javolution.util.FastMap;

public class ProfileEvents {

	public final static String module = ProfileEvents.class.getName();
	public static final String resource = "BaseEcommerceUiLabels";

	public static String createCustomerProfile(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException{
		String result = "success";
		Locale locale = UtilHttp.getLocale(request);
		boolean beganTx = TransactionUtil.begin(7200);
		String partyId = (String) request.getParameter("partyId");
		if (UtilValidate.isEmpty(partyId)) {
			try {
				createCustomerWithUserLogin(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				if (UtilValidate.isEmpty(request.getAttribute("_ERROR_MESSAGE_"))) {
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource, "CREATE_CUSTOMER_ERROR", locale));
				}
				LoginWorker.logout(request, response);
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				result = "error";
			}
		} else {
			try {
				updateCustomer(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				if (UtilValidate.isEmpty(request.getAttribute("_ERROR_MESSAGE_"))) {
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource, "UPDATE_CUSTOMER_ERROR", locale));
				}
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				result = "error";
			}
		}
		TransactionUtil.commit(beganTx);
		return result;
	}

	public static String createAndUpdateUserInformation(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		request.setAttribute("checkoutpage", request.getParameter("checkoutpage"));
		request.setAttribute("shipBeforeDate", request.getParameter("shipBeforeDate"));
		request.setAttribute("shipping_instructions", request.getParameter("shipping_instructions"));
		if(UtilValidate.isNotEmpty(userLogin)){
			return updateUserInformation(request, response);
		}
		return createUserInformation(request, response);
	}

	public static String createUserInformation(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String gender = (String) request.getParameter("gender");
		String name = (String) request.getParameter("name");
		String phone = (String) request.getParameter("phone");
		String city = (String) request.getParameter("city");
		String district = (String) request.getParameter("district");
		String address = (String) request.getParameter("address");
		String email = (String) request.getParameter("email");
		String productStoreId = (String) request.getParameter("productStoreId");
		Locale locale = UtilHttp.getLocale(request);
		boolean beganTx = TransactionUtil.begin(7200);
		String msg = UtilProperties.getMessage(resource, "CREATE_CUSTOMER_ERROR", locale);
		try {
//			String partyId = ProfileUtils.quickCreateCustomer(delegator, dispatcher, name, gender);
			String partyId = "_NA_";
			name = name.trim();
			String toName = name + " - " + gender;
			String contactMechId = ProfileUtils.createPostalAddress(delegator, dispatcher, partyId, toName, phone, address, city, district, productStoreId);
			if(UtilValidate.isEmpty(contactMechId)){
				TransactionUtil.rollback(beganTx, msg, null);
				return "error";
			}
			request.setAttribute("contactMechId", contactMechId);
			request.setAttribute("partyId", partyId);
			request.setAttribute("fullName", name);
			request.setAttribute("phone", phone);
			request.setAttribute("email", email);
			String contactMechIdEmail = ProfileUtils.createPartyEmail(delegator, dispatcher, partyId, email);
			if (UtilValidate.isNotEmpty(contactMechIdEmail)) {
				request.setAttribute("contactMechIdEmail", contactMechIdEmail);
			}
			String contactMechIdPhone = ProfileUtils.createPartyTelephone(delegator, dispatcher, partyId, phone);
			if (UtilValidate.isNotEmpty(contactMechIdPhone)) {
				request.setAttribute("contactMechIdPhone", contactMechIdPhone);
			}
			ProfileUtils.createPartyRole(delegator, dispatcher, partyId);
//			ProfileUtils.createPartyProductStoreRole(delegator, dispatcher, partyId, productStoreId);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", msg);
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return "error";
		} finally {
			TransactionUtil.commit(beganTx);
		}
		return "success";
	}
	public static String updateUserInformation(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String gender = (String) request.getParameter("gender");
		String name = (String) request.getParameter("name");
		String phone = (String) request.getParameter("phone");
		String city = (String) request.getParameter("city");
		String district = (String) request.getParameter("district");
		String address = (String) request.getParameter("address");
		String email = (String) request.getParameter("email");
		String productStoreId = (String) request.getParameter("productStoreId");
		Locale locale = UtilHttp.getLocale(request);
		boolean beganTx = TransactionUtil.begin(7200);
		String msg = UtilProperties.getMessage(resource,
				"CREATE_CUSTOMER_ERROR", locale);
		try {
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			String partyId = userLogin.getString("partyId");
			GenericValue party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			name = name.trim();
			String fullName = party.getString("lastName") + " " + party.getString("middleName") + "" + party.getString("firstName");
			if(!fullName.equals(name) || !gender.equals(party.getString("gender"))){
				ProfileUtils.updatePerson(delegator, dispatcher, partyId, name, gender);
			}
			String toName = name + " - " + gender;
			address = address.trim();
			List<GenericValue> fk = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"),
									EntityCondition.makeCondition("address1", address),
									EntityCondition.makeCondition("stateProvinceGeoId", city),
									EntityCondition.makeCondition("districtGeoId", district))), null, UtilMisc.toList("-contactMechId"), null, false);
			String contactMechId = null;
			if(UtilValidate.isEmpty(fk)){
				contactMechId = ProfileUtils.createPostalAddress(delegator, dispatcher, partyId, toName, address, city, district, productStoreId);
				if(UtilValidate.isEmpty(contactMechId)){
					TransactionUtil.rollback(beganTx, msg, null);
					return "error";
				}
			}else{
				GenericValue e = fk.get(0);
				contactMechId = e.getString("contactMechId");
			}
			request.setAttribute("contactMechId", contactMechId);
			List<GenericValue> fkemail = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("infoString", email),
									EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL"))), null, UtilMisc.toList("-contactMechId"), null, false);
			if(UtilValidate.isEmpty(fkemail)){
				ProfileUtils.createPartyEmail(delegator, dispatcher, partyId, email);
			}
			List<GenericValue> fkphone = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", "PHONE_BILLING"),
									EntityCondition.makeCondition("contactNumber", phone))), null, UtilMisc.toList("-contactMechId"), null, false);
			if(UtilValidate.isEmpty(fkphone)){
				ProfileUtils.createPartyTelephone(delegator, dispatcher, partyId, phone);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", msg);
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return "error";
		} finally {
			TransactionUtil.commit(beganTx);
		}
		return "success";
	}
	public static void createCustomerWithUserLogin(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, GenericServiceException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String gender = (String) request.getParameter("gender");
		String birthDate = (String) request.getParameter("birthDate");
		String name = (String) request.getParameter("fullName");
		String phone = (String) request.getParameter("phone");
		String email = (String) request.getParameter("email");
		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");
		String passwordVerify = (String) request.getParameter("passwordVerify");
		String city = (String) request.getParameter("city");
		String district = (String) request.getParameter("district");
		String address1 = (String) request.getParameter("address");
		String productStoreId = (String) request.getParameter("productStoreId");
		
		String partyId = ProfileUtils.quickCreateCustomer(delegator, dispatcher, name, gender, birthDate);
		delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyId),
				EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
		//	createUpdateUserLogin
		ProfileUtils.createUserLogin(request, response, dispatcher, partyId, username, password, passwordVerify, email);
		//	createPartyRole
		ProfileUtils.quickCreatePartyRole(delegator, dispatcher, partyId);
		if(UtilValidate.isNotEmpty(email)){
			//	create email address createPartyEmailAddress
			ProfileUtils.createPartyEmail(delegator, dispatcher, partyId, email);
		}
		if(UtilValidate.isNotEmpty(city)){
			//	create shipping & primary address createPostalAddressAndPurposes
			String toName = name + " - " + gender;
			ProfileUtils.createPostalAddress(delegator, dispatcher, partyId, toName, address1, city, district, productStoreId);
		}
		if(UtilValidate.isNotEmpty(phone)){
			// 	createPartyTelecomNumber
			ProfileUtils.createPartyTelephone(delegator, dispatcher, partyId, phone);
		}
		//	createProductStoreRole
		ProfileUtils.createPartyProductStoreRole(delegator, dispatcher, partyId, productStoreId);
	}

	public static void updateCustomer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		String name = request.getParameter("fullName");
		String birthDateS = request.getParameter("birthDate");
		Date birthDate = null;
		if (UtilValidate.isNotEmpty(birthDateS)) {
			Long birthDateL = Long.valueOf(birthDateS);
			birthDate = new Date(birthDateL);
		}
		List<GenericValue> listUser = delegator.findList("UserLoginAndSecurityGroup",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
								EntityCondition.makeCondition("groupId", "ECOMMERCE_CUSTOMER"), EntityUtil.getFilterByDateExpr())),
				null, null, null, false);
		if (UtilValidate.isEmpty(listUser)) {
			String msg = UtilProperties.getMessage(resource, "DONT_HAVE_PERMISSION_SHOPPING_CART_ECOMMERCE", locale);
			request.setAttribute("_ERROR_MESSAGE_", msg);
			throw new Exception();
		}
		
		delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyId),
				EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
		
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", system);
		party.putAll(CallcenterServices.demarcatePersonName(name));
		party.put("partyId", partyId);
		party.put("gender", request.getParameter("gender"));
		party.put("birthDate", birthDate);
		dispatcher.runSync("updatePerson", party);
		
		String postalAddressId = request.getParameter("postalAddressId");
		if (UtilValidate.isEmpty(postalAddressId)) {
			if(UtilValidate.isNotEmpty(request.getParameter("city"))){
				//	create shipping & primary address createPostalAddressAndPurposes
				String toName = name + " - " + request.getParameter("gender");
				ProfileUtils.createPostalAddress(delegator, dispatcher, partyId, toName, request.getParameter("address").trim(), request.getParameter("city"), request.getParameter("district"), request.getParameter("productStoreId"));
			}
		} else {
			party = FastMap.newInstance();
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			party.put("userLogin", userLogin);
			party.put("partyId", partyId);
			party.put("contactMechId", postalAddressId);
			party.put("address1", request.getParameter("address").trim());
			party.put("toName", name);
			party.put("stateProvinceGeoId", request.getParameter("city"));
			party.put("city", request.getParameter("city"));
			party.put("countryGeoId", "VNM");
			party.put("districtGeoId", request.getParameter("district"));
			party.put("postalCode", "70000");
			party.put("productStoreId", request.getParameter("productStoreId"));
			party.put("setShippingPurpose", "Y");
			party.put("setBillingPurpose", "Y");
			dispatcher.runSync("updatePostalAddressAndPurposes", party);
		}
		
		//	PartyTelecomNumber
		String contactNumberId = request.getParameter("contactNumberId");
		if (UtilValidate.isEmpty(contactNumberId)) {
			if(UtilValidate.isNotEmpty(request.getParameter("phone"))){
				// 	createPartyTelecomNumber
				ProfileUtils.createPartyTelephone(delegator, dispatcher, partyId, request.getParameter("phone"));
			}
		} else {
			delegator.storeByCondition("TelecomNumber", UtilMisc.toMap("contactNumber", request.getParameter("phone")), EntityCondition.makeCondition("contactMechId", contactNumberId));
		}
		String infoStringId = request.getParameter("infoStringId");
		if (UtilValidate.isEmpty(infoStringId)) {
			if (UtilValidate.isNotEmpty(request.getParameter("email"))) {
				ProfileUtils.createPartyEmail(delegator, dispatcher, partyId, request.getParameter("email"));
			}
		} else {
			delegator.storeByCondition("ContactMech", UtilMisc.toMap("infoString", request.getParameter("email")), EntityCondition.makeCondition("contactMechId", infoStringId));
		}
	}
}
