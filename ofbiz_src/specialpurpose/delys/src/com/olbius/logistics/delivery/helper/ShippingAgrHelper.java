package com.olbius.logistics.delivery.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class ShippingAgrHelper {
	
	public static Map<String, Object> createPostalAddress(Map<String, Object> context) throws GenericServiceException{
		//Get Parameters
		String address1 = (String)context.get("address1");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		
		//Create Address
		Map<String, Object> addressCtx = FastMap.newInstance();
		addressCtx.put("address1", address1);
		addressCtx.put("userLogin", userLogin);
		Map<String, Object> createContactMechGeoResult = null;
		createContactMechGeoResult = dispatcher.runSync("createContactMechGeo", addressCtx);
		
		//Create Link Org And Address
		Map<String, Object> partyContactMechCtx = FastMap.newInstance();
		partyContactMechCtx.put("partyId", partyId);
		partyContactMechCtx.put("contactMechId", createContactMechGeoResult.get("contactMechId"));
		partyContactMechCtx.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
		partyContactMechCtx.put("userLogin", userLogin);
		partyContactMechCtx.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
		Map<String, Object> result = dispatcher.runSync("createPartyContactMech", partyContactMechCtx);
		return result;
	}
	
	public static Map<String, Object> createTelecomNumber(Map<String, Object> context) throws GenericServiceException{
		//Get Parameters
		String phoneNumber = (String)context.get("phoneNumber");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		
		//Create Telephone Number
		Map<String, Object> createTelephoneNumberCtx = FastMap.newInstance();
		createTelephoneNumberCtx.put("contactNumber", phoneNumber);
		createTelephoneNumberCtx.put("userLogin", userLogin);
		Map<String, Object> createTeleNumResult = null;
		createTeleNumResult = dispatcher.runSync("createTelecomNumber", createTelephoneNumberCtx);
		
		//Create Link Org And Telephone Number
		Map<String, Object> partyContactMechCtx = FastMap.newInstance();
		partyContactMechCtx.put("partyId", partyId);
		partyContactMechCtx.put("contactMechId", createTeleNumResult.get("contactMechId"));
		partyContactMechCtx.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
		partyContactMechCtx.put("userLogin", userLogin);
		partyContactMechCtx.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
		Map<String, Object> result = dispatcher.runSync("createPartyContactMech", partyContactMechCtx);
		return result;
	}
	
	public static Map<String, Object> createFaxNumber(Map<String, Object> context) throws GenericServiceException{
		//Get Parameters
		String faxNumber = (String)context.get("faxNumber");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		//Create Telephone Number
		Map<String, Object> createFaxNumberCtx = FastMap.newInstance();
		createFaxNumberCtx.put("faxNumber", faxNumber);
		createFaxNumberCtx.put("userLogin", userLogin);
		Map<String, Object> createFaxNumberResult = null;
		createFaxNumberResult = dispatcher.runSync("createFaxNumber", createFaxNumberCtx);
		
		//Create Link Org And Telephone Number
		Map<String, Object> partyContactMechCtx = FastMap.newInstance();
		partyContactMechCtx.put("partyId", partyId);
		partyContactMechCtx.put("contactMechId", createFaxNumberResult.get("contactMechId"));
		partyContactMechCtx.put("fromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
		partyContactMechCtx.put("userLogin", userLogin);
		partyContactMechCtx.put("contactMechPurposeTypeId", "FAX_NUMBER");
		Map<String, Object> result = dispatcher.runSync("createPartyContactMech", partyContactMechCtx);
		return result;
	}
	
	public static Map<String, Object> createTaxParty(Map<String, Object> context) throws GenericServiceException{
		//Get Parameters
		String partyId = (String)context.get("partyId");
		String partyTaxId = (String)context.get("taxId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp fromDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		String taxAuthGeoId = "VNM";
		String taxAuthPartyId = "VNM_TAX";
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		//Create createPartyTaxAuthInfo
		Map<String, Object> createPartyTaxAuthInfoCtx = FastMap.newInstance();
		createPartyTaxAuthInfoCtx.put("fromDate", fromDate);
		createPartyTaxAuthInfoCtx.put("userLogin", userLogin);
		createPartyTaxAuthInfoCtx.put("partyId", partyId);
		createPartyTaxAuthInfoCtx.put("partyTaxId", partyTaxId);
		createPartyTaxAuthInfoCtx.put("taxAuthGeoId", taxAuthGeoId);
		createPartyTaxAuthInfoCtx.put("taxAuthPartyId", taxAuthPartyId);
		Map<String, Object> result = dispatcher.runSync("createPartyTaxAuthInfo", createPartyTaxAuthInfoCtx);
		return result;
	}
	
	public static Map<String, Object> createFinAccount(Map<String, Object> context) throws GenericServiceException{
		//Get Parameters
		String partyId = (String)context.get("partyId");
		String finAccountCode = (String)context.get("finAccountCode");
		String finAccountName = (String)context.get("finAccountName");
		String finAccountTypeId = "BANK_ACCOUNT";
		Timestamp fromDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		String statusId = "FNACT_ACTIVE";
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		//Create createFinAccount
		Map<String, Object> createFinAccountCtx = FastMap.newInstance();
		createFinAccountCtx.put("finAccountCode", finAccountCode);
		createFinAccountCtx.put("finAccountName", finAccountName);
		createFinAccountCtx.put("finAccountTypeId", finAccountTypeId);
		createFinAccountCtx.put("fromDate", fromDate);
		createFinAccountCtx.put("statusId", statusId);
		createFinAccountCtx.put("ownerPartyId", partyId);
		createFinAccountCtx.put("userLogin", userLogin);
		Map<String, Object> result = dispatcher.runSync("createFinAccount", createFinAccountCtx);
		return result;
	}

	public static void createEmplPositionAndFulfillment(Map<String, Object> context) throws GenericServiceException{
		//Get parameters
		String orgId = (String)context.get("orgId");
		String partyId = (String)context.get("partyId");
		DispatchContext ctx = (DispatchContext)context.get("ctx");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String emplPostionTypeId = (String)context.get("emplPostionTypeId");

		//Create EmplPositionAndFulfillment
		Map<String, Object> createEmplPosAndFulCtx = FastMap.newInstance();
		createEmplPosAndFulCtx.put("emplPositionTypeId", emplPostionTypeId);
		createEmplPosAndFulCtx.put("internalOrgId", orgId);
		createEmplPosAndFulCtx.put("partyId", partyId);
		createEmplPosAndFulCtx.put("userLogin", userLogin);
		createEmplPosAndFulCtx.put("actualFromDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));

		dispatcher.runSync("createEmplPositionAndFulfillment", createEmplPosAndFulCtx);
	}
}
