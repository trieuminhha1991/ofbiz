package com.olbius.ecommerce.party;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.crm.CallcenterServices;

import javolution.util.FastMap;

public class ProfileEvents {

    public final static String module = ProfileEvents.class.getName();
    public static final String resource = "DpcEcommerceUiLabels";

    public static String createCustomerProfile(HttpServletRequest request, HttpServletResponse response) {
	String partyId = (String) request.getParameter("partyId");
	try{
		if(UtilValidate.isEmpty(partyId)){
			return createCustomer(request, response);
		}else{
			String msg = updateCustomer(request, response);
			if(msg.equals("error")){
				return "error";
			}
		}
		long shipBeTm = Long.parseLong((String) request.getParameter("shipBeforeDate"));
		Timestamp shipBeforeDate = new Timestamp(shipBeTm);
		HttpSession session = request.getSession();
		request.setAttribute("shipBeforeDate", shipBeforeDate.toString());
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		for(ShoppingCartItem item : cart.items()){
			item.setShipBeforeDate(shipBeforeDate);
			item.setEstimatedShipDate(shipBeforeDate);
			item.setDesiredDeliveryDate(shipBeforeDate);
		}
	}catch(Exception e){
		e.printStackTrace();
		return "error";
	}
		return "success";
    }
    public static String createCustomer(HttpServletRequest request, HttpServletResponse response){
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	String gender = (String) request.getParameter("gender");
	String name = (String) request.getParameter("name");
	String phone = (String) request.getParameter("phone");
	String email = (String) request.getParameter("email");
	String city = (String) request.getParameter("city");
	String district = (String) request.getParameter("district");
	String address1 = (String) request.getParameter("address");
	String productStoreId = (String) request.getParameter("productStoreId");
	Locale locale = UtilHttp.getLocale(request);
	try{
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		Map<String, Object> fullName = CallcenterServices.demarcatePersonName(name);
		party.put("userLogin", system);
		party.put("firstName", fullName.get("firstName"));
		party.put("middleName", fullName.get("middleName"));
		party.put("lastName", fullName.get("lastName"));
		party.put("gender", gender);
		Map<String, Object> outParty = dispatcher.runSync("createPerson", party);
		String partyId = (String) outParty.get("partyId");
//        		createUpdateUserLogin
		party = FastMap.newInstance();
		party.put("partyId", partyId);
		party.put("request", request);
		party.put("response", response);
		party.put("username", email);
		party.put("emailAddress", email);
		party.put("password", phone);
		party.put("passwordVerify", phone);
		dispatcher.runSync("createUpdateUserLogin", party);
//        		createPartyRole
		party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("roleTypeId", "CUSTOMER");
		dispatcher.runSync("createPartyRole", party);
//    			create email address createPartyEmailAddress
		party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("emailAddress", email);
		party.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
		dispatcher.runSync("createPartyEmailAddress", party);
//    			create shipping & primary address createPostalAddressAndPurposes
		party = FastMap.newInstance();
		address1 = address1.trim();
		GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", email), false);
		party.put("userLogin", partyUserLogin);
		party.put("partyId", partyId);
		party.put("address1", address1);

		party.put("toName", address1);
		party.put("stateProvinceGeoId", city);
		party.put("city", city);
		party.put("countryGeoId", "VNM");
		party.put("districtGeoId", district);
		party.put("postalCode", "70000");
		party.put("productStoreId", productStoreId);
		party.put("setShippingPurpose", "Y");
		party.put("setBillingPurpose", "Y");
		dispatcher.runSync("createPostalAddressAndPurposes", party);
//        		"createPartyTelecomNumber"
		party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("contactMechPurposeTypeId", "PHONE_BILLING");
		party.put("contactNumber", phone);
		dispatcher.runSync("createPartyTelecomNumber", party);
//    			createProductStoreRole
                party = FastMap.newInstance();
		party.put("userLogin", system);
		party.put("partyId", partyId);
		party.put("roleTypeId", "CUSTOMER");
		party.put("productStoreId", productStoreId);
		dispatcher.runSync("createProductStoreRole", party);
	}catch(Exception e){
		e.printStackTrace();
		String msg = UtilProperties.getMessage(resource, "CREATE_CUSTOMER_ERROR", locale);
		request.setAttribute("_ERROR_MESSAGE_", msg);
		return "error";
	}
	return "success";
    }
    public static String updateCustomer(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = (String) request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);
		String gender = (String) request.getParameter("gender");
		String name = (String) request.getParameter("name");
		String phone = (String) request.getParameter("phone");
		String city = (String) request.getParameter("city");
		String district = (String) request.getParameter("district");
		String address1 = (String) request.getParameter("address");
		String productStoreId = (String) request.getParameter("productStoreId");
		try {
			List<GenericValue> listUser = delegator.findList("UserLoginAndSecurityGroup", EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
								EntityCondition.makeCondition("groupId", "ECOMMERCE_CUSTOMER"),
								EntityUtil.getFilterByDateExpr())), null, null, null, false);
			if(UtilValidate.isEmpty(listUser)){
				String msg = UtilProperties.getMessage(resource, "DONT_HAVE_PERMISSION_SHOPPING_CART_ECOMMERCE", locale);
				request.setAttribute("_ERROR_MESSAGE_", msg);
				return "error";
			}
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> party = FastMap.newInstance();
			Map<String, Object> fullName = CallcenterServices.demarcatePersonName(name);
		party.put("userLogin", system);
		party.put("firstName", fullName.get("firstName"));
		party.put("middleName", fullName.get("middleName"));
		party.put("lastName", fullName.get("lastName"));
		party.put("partyId", partyId);
		party.put("gender", gender);
		dispatcher.runSync("updatePerson", party);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		address1 = address1.trim();
		List<GenericValue> addresses = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyId", partyId),
					EntityCondition.makeCondition("address1", address1))), null, null, null, false);
		if(UtilValidate.isEmpty(addresses)){
			party = FastMap.newInstance();
			party.put("userLogin", userLogin);
			party.put("partyId", partyId);
			party.put("address1", address1);

			party.put("toName", address1);
			party.put("stateProvinceGeoId", city);
			party.put("city", city);
			party.put("countryGeoId", "VNM");
			party.put("districtGeoId", district);
			party.put("postalCode", "70000");
			party.put("productStoreId", productStoreId);
			party.put("setShippingPurpose", "Y");
			party.put("setBillingPurpose", "Y");
			dispatcher.runSync("createPostalAddressAndPurposes", party);
		}

//        		"createPartyTelecomNumber"
		List<GenericValue> phones = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(UtilMisc.toList(
																		EntityCondition.makeCondition("partyId", partyId),
																		EntityCondition.makeCondition("contactNumber", phone))), null, null, null, false);
		if(UtilValidate.isEmpty(phones)){
			party = FastMap.newInstance();
			party.put("userLogin", system);
			party.put("partyId", partyId);
			party.put("contactMechPurposeTypeId", "PHONE_BILLING");
			party.put("contactNumber", phone);
			dispatcher.runSync("createPartyTelecomNumber", party);
		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String msg = UtilProperties.getMessage(resource, "UPDATE_CUSTOMER_ERROR", locale);
			request.setAttribute("_ERROR_MESSAGE_", msg);
			return "error";
		}

	return "success";
    }
}
