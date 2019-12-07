package com.olbius.logistics.delivery;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.logistics.delivery.helper.ShippingAgrHelper;

public class ShippingAgrServices {
	
	public static final String logisticsUiLabels = "DelysLogisticsUiLabels";
	public static final String module = ShippingAgrServices.class.getName();
	
	public static Map<String, Object> createShippingAgreement(DispatchContext ctx, Map<String, Object> context){
		//Get Dispatcher
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		Timestamp agreementDate = (Timestamp)context.get("agreementDate");
		String agreementId = (String)context.get("agreementId");
		String description = (String)context.get("description");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyIdFrom = (String)context.get("partyIdFrom");
		String partyIdTo = (String)context.get("partyIdTo");
		String repIdFrom = (String)context.get("repIdFrom");
		String repIdTo = (String)context.get("repIdTo");
		String repToPos = (String)context.get("repToPos");
		String repFromPos = (String)context.get("repFromPos");
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String)context.get("roleTypeIdTo");
		String textData = (String)context.get("textData");
		Locale locale = (Locale)context.get("locale");
		String addressFrom = (String)context.get("addressFrom");
		String addressTo = (String)context.get("addressTo");
		String faxNumberFrom = (String)context.get("faxNumberFrom");
		String faxNumberTo  = (String) context.get("faxNumberTo");
		String taxIdFrom = (String)context.get("taxIdFrom");
		String taxIdTo = (String)context.get("taxIdTo");
		String finAccountIdTo = (String)context.get("finAccountIdTo");
		String phoneNumberTo = (String)context.get("phoneNumberTo");
		String phoneNumberFrom = (String)context.get("phoneNumberFrom");
		/**
		 * Create Shipping Agreement
		 */
		//Set up context for createAgreement Service
		Map<String, Object> createShippingAgrCtx = FastMap.newInstance();
		createShippingAgrCtx.put("agreementDate", agreementDate);
		createShippingAgrCtx.put("agreementId", agreementId);
		createShippingAgrCtx.put("agreementTypeId", "SHIPPING_AGREEMENT");
		createShippingAgrCtx.put("description", description);
		createShippingAgrCtx.put("fromDate", fromDate);
		createShippingAgrCtx.put("thruDate", thruDate);
		createShippingAgrCtx.put("userLogin", userLogin);
		createShippingAgrCtx.put("partyIdFrom", partyIdFrom);
		createShippingAgrCtx.put("partyIdTo", partyIdTo);
		createShippingAgrCtx.put("roleTypeIdFrom", roleTypeIdFrom);
		createShippingAgrCtx.put("roleTypeIdTo", roleTypeIdTo);
		createShippingAgrCtx.put("textData", textData);
		
		//Call createAgreement service
		Map<String, Object> createShippingAgrResult = null;
		try {
			createShippingAgrResult = dispatcher.runSync("createAgreement", createShippingAgrCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		
		/**
		 * Create Party From Role
		 */
		GenericValue userLoginSystem = null;
		try {
			userLoginSystem = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		Map<String, Object> createPartyRoleFromCtx = FastMap.newInstance();
		createPartyRoleFromCtx.put("partyId", repIdFrom);
		createPartyRoleFromCtx.put("roleTypeId", "REPRESENT_PARTY_FROM");
		createPartyRoleFromCtx.put("userLogin", userLoginSystem);
		//Call createAgreementRole service
		try {
			dispatcher.runSync("createPartyRole", createPartyRoleFromCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		/**
		 * Create Representative From
		 */
		//Set up context for createAgreementRole
		Map<String, Object> createRepFromCtx = FastMap.newInstance();
		createRepFromCtx.put("partyId", repIdFrom);
		createRepFromCtx.put("roleTypeId", "REPRESENT_PARTY_FROM");
		createRepFromCtx.put("agreementId", createShippingAgrResult.get("agreementId"));
		createRepFromCtx.put("userLogin", userLogin);
		//Call createAgreementRole service
		try {
			dispatcher.runSync("createAgreementRole", createRepFromCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		
		/**
		 * Create Party To Role
		 */
		Map<String, Object> createPartyRoleToCtx = FastMap.newInstance();
		createPartyRoleToCtx.put("partyId", repIdTo);
		createPartyRoleToCtx.put("roleTypeId", "REPRESENT_PARTY_TO");
		createPartyRoleToCtx.put("userLogin", userLoginSystem);
		//Call createAgreementRole service
		try {
			dispatcher.runSync("createPartyRole", createPartyRoleToCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		
		/**
		 * Create Representative To
		 */
		//Set up context for createAgreementRole
		Map<String, Object> createRepToCtx = FastMap.newInstance();
		createRepToCtx.put("partyId", repIdTo);
		createRepToCtx.put("userLogin", userLogin);
		createRepToCtx.put("roleTypeId", "REPRESENT_PARTY_TO");
		createRepToCtx.put("agreementId", createShippingAgrResult.get("agreementId"));
		
		//Call createAgreementRole service
		try {
			dispatcher.runSync("createAgreementRole", createRepToCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
		}
		
		//Create Employee Position Id attribute From
		if(repFromPos != null){
			GenericValue agreementAtrrFrom = delegator.makeValue("AgreementAttribute");
			agreementAtrrFrom.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			agreementAtrrFrom.put("attrName", "EMPL_POS_ID_FROM");
			agreementAtrrFrom.put("attrValue", repFromPos);
			try {
				agreementAtrrFrom.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Employee Position Id attribute To
		if(repToPos != null){
			GenericValue agreementAtrrTo = delegator.makeValue("AgreementAttribute");
			agreementAtrrTo.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			agreementAtrrTo.put("attrName", "EMPL_POS_ID_TO");
			agreementAtrrTo.put("attrValue", repToPos);
			try {
				agreementAtrrTo.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Address From
		if(addressFrom != null){
			GenericValue addressFromAttr = delegator.makeValue("AgreementAttribute");
			addressFromAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			addressFromAttr.put("attrName", "ADDRESS_FROM");
			addressFromAttr.put("attrValue", addressFrom);

			try {
				addressFromAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Address To
		if(addressTo != null){
			GenericValue addressToAttr = delegator.makeValue("AgreementAttribute");
			addressToAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			addressToAttr.put("attrName", "ADDRESS_TO");
			addressToAttr.put("attrValue", addressTo);

			try {
				addressToAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Phone Number To
		if(phoneNumberTo != null){
			GenericValue telecomNumberToAttr = delegator.makeValue("AgreementAttribute");
			telecomNumberToAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			telecomNumberToAttr.put("attrName", "TELECOM_TO");
			telecomNumberToAttr.put("attrValue", phoneNumberTo);

			try {
				telecomNumberToAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Phone Number From
		if(phoneNumberFrom != null){
			GenericValue telecomNumberFromAttr = delegator.makeValue("AgreementAttribute");
			telecomNumberFromAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			telecomNumberFromAttr.put("attrName", "TELECOM_FROM");
			telecomNumberFromAttr.put("attrValue", phoneNumberFrom);

			try {
				telecomNumberFromAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Fax Number From
		if(faxNumberFrom != null){
			GenericValue faxNumberFromAttr = delegator.makeValue("AgreementAttribute");
			faxNumberFromAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			faxNumberFromAttr.put("attrName", "FAX_FROM");
			faxNumberFromAttr.put("attrValue", faxNumberFrom);

			try {
				faxNumberFromAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Fax Number To
		if(faxNumberTo != null){
			GenericValue faxNumberToAttr = delegator.makeValue("AgreementAttribute");
			faxNumberToAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			faxNumberToAttr.put("attrName", "FAX_TO");
			faxNumberToAttr.put("attrValue", faxNumberTo);

			try {
				faxNumberToAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute tax Id From
		if(taxIdFrom != null){
			GenericValue taxIdFromAttr = delegator.makeValue("AgreementAttribute");
			taxIdFromAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			taxIdFromAttr.put("attrName", "TAX_FROM");
			taxIdFromAttr.put("attrValue", taxIdFrom);

			try {
				taxIdFromAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement Attribute Tax To
		if(taxIdTo != null){
			GenericValue taxIdToAttr = delegator.makeValue("AgreementAttribute");
			taxIdToAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			taxIdToAttr.put("attrName", "TAX_TO");
			taxIdToAttr.put("attrValue", taxIdTo);

			try {
				taxIdToAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		//Create Agreement finAccountId To
		if(finAccountIdTo != null){
			GenericValue finAccountIdToAttr = delegator.makeValue("AgreementAttribute");
			finAccountIdToAttr.put("agreementId", (String)createShippingAgrResult.get("agreementId"));
			finAccountIdToAttr.put("attrName", "FIN_ACCOUNT_TO");
			finAccountIdToAttr.put("attrValue", finAccountIdTo);

			try {
				finAccountIdToAttr.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createShippingAgreementFail", locale));
			}
		}

		return createShippingAgrResult;
	}
	
	public Map<String, Object> createPartyQuickly(DispatchContext ctx, Map<String, Object> context){
		//Get dispatcher
		LocalDispatcher dispatcher = ctx.getDispatcher();
		//get parameters
		String groupName = (String)context.get("groupName");
		String firstName = (String)context.get("firstName");
		String middleName = (String)context.get("middleName");
		String lastName = (String)context.get("lastName");
		String positionName = (String)context.get("positionName");
		String address = (String)context.get("address");
		String phoneNumber = (String)context.get("phoneNumber");
		String taxId = (String)context.get("taxId");
		String faxNumber = (String)context.get("faxNumber");
		String accountNumber = (String)context.get("accountNumber");
		String bankName = (String)context.get("bankName");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		GenericValue userSystem = null;
		try {
			userSystem = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		//Create Organization
		Map<String, Object> partyGroupCtx = FastMap.newInstance();
		partyGroupCtx.put("groupName", groupName);
		partyGroupCtx.put("partyTypeId", "PARTY_GROUP");
		partyGroupCtx.put("userLogin", userSystem);
		partyGroupCtx.put("locale", locale);
		Map<String, Object> createPartyGroupResult = null;
		try {
			createPartyGroupResult = dispatcher.runSync("createPartyGroup", partyGroupCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create createPartyRole
		Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
		createPartyRoleCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createPartyRoleCtx.put("roleTypeId", "SHIP_VENDOR");
		createPartyRoleCtx.put("userLogin", userSystem);
		try {
			dispatcher.runSync("createPartyRole", createPartyRoleCtx);
		} catch (GenericServiceException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		//Create Representative
		Map<String, Object> RepresentativeCtx = FastMap.newInstance();
		RepresentativeCtx.put("firstName", firstName);
		RepresentativeCtx.put("middleName", middleName);
		RepresentativeCtx.put("lastName", lastName);
		RepresentativeCtx.put("userLogin", userSystem);
		Map<String, Object> createPersonResult = null;
		try {
			createPersonResult = dispatcher.runSync("createPerson", RepresentativeCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create EmplPos
		Map<String, Object> emplPosCtx = FastMap.newInstance();
		emplPosCtx.put("emplPositionTypeId", positionName);
		emplPosCtx.put("ctx", ctx);
		emplPosCtx.put("userLogin", userSystem);
		emplPosCtx.put("partyId", createPersonResult.get("partyId"));
		emplPosCtx.put("orgId", createPartyGroupResult.get("partyId"));
		try {
			ShippingAgrHelper.createEmplPositionAndFulfillment(emplPosCtx);
		} catch (Exception e1) {
			Debug.log(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}

		//Create Address
		Map<String, Object> createPostalAddressCtx = FastMap.newInstance();
		createPostalAddressCtx.put("address1", address);
		createPostalAddressCtx.put("userLogin", userSystem);
		createPostalAddressCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createPostalAddressCtx.put("ctx", ctx);
		Map<String, Object> createPostalAddressResult = null;
		try {
			createPostalAddressResult = ShippingAgrHelper.createPostalAddress(createPostalAddressCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create telephone number
		Map<String, Object> createTeleNumberCtx = FastMap.newInstance();
		createTeleNumberCtx.put("phoneNumber", phoneNumber);
		createTeleNumberCtx.put("userLogin", userSystem);
		createTeleNumberCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createTeleNumberCtx.put("ctx", ctx);
		Map<String, Object> createTeleNumberResult = null;
		try {
			createTeleNumberResult = ShippingAgrHelper.createTelecomNumber(createTeleNumberCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create Fax Number
		Map<String, Object> createFaxNumberCtx = FastMap.newInstance();
		createFaxNumberCtx.put("faxNumber", faxNumber);
		createFaxNumberCtx.put("userLogin", userSystem);
		createFaxNumberCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createFaxNumberCtx.put("ctx", ctx);
		Map<String, Object> createFaxNumberResult = null;
		try {
			createFaxNumberResult = ShippingAgrHelper.createFaxNumber(createFaxNumberCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create Tax
		Map<String, Object> createTaxCtx = FastMap.newInstance();
		createTaxCtx.put("taxId", taxId);
		createTaxCtx.put("userLogin", userSystem);
		createTaxCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createTaxCtx.put("ctx", ctx);
		Map<String, Object> createTaxResult = null;
		try {
			createTaxResult = ShippingAgrHelper.createTaxParty(createTaxCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		//Create FinAccount
		Map<String, Object> createFinAccountCtx = FastMap.newInstance();
		createFinAccountCtx.put("finAccountCode", accountNumber);
		createFinAccountCtx.put("finAccountName", bankName);
		createFinAccountCtx.put("userLogin", userSystem);
		createFinAccountCtx.put("partyId", createPartyGroupResult.get("partyId"));
		createFinAccountCtx.put("ctx", ctx);
		Map<String, Object> createFinAccountResult = null;
		try {
			createFinAccountResult = ShippingAgrHelper.createFinAccount(createTaxCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklyFail", locale));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.SUCCESS_MESSAGE);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(logisticsUiLabels, "createPartyQuicklySuccessfully", locale));
		result.put("partyIdTo", createPartyGroupResult.get("partyId"));
		result.put("repIdTo", createPersonResult.get("partyId"));
		result.put("roleTypeIdTo", "SHIP_VENDOR");
		result.put("emplPositionTypeIdTo", positionName);
		result.put("postalAddressIdTo", createPostalAddressResult.get("contactMechId"));
		result.put("telecomNumberIdTo", createTeleNumberResult.get("contactMechId"));
		result.put("faxNumberIdTo", createFaxNumberResult.get("contactMechId"));
		result.put("taxIdTo", createTaxResult.get("taxIdTo"));
		result.put("finAccountIdTo", createFinAccountResult.get("finAccountId"));
		return result;
	}
}
