package com.olbius.basepo.supplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.utils.POUtil;
import com.olbius.crm.CallcenterServices;
import com.olbius.security.util.SecurityUtil;

public class SupplierServices {
	public static final String module = SupplierServices.class.getName();
	public static final String properties = "po.properties";
	public static final String contact_mect_purpose_pros = "po.contact.mech.purpose.type";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSupplierSimple(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		//Locale locale = (Locale) context.get("locale");
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("partyCode");
			}
			listIterator = delegator.find("PartySupplierDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSupplierSimple service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPartySupplier(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");

            List<GenericValue> partySuppliers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartySupplierDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			List<Map<String, Object>> listPartySupplier = FastList.newInstance();
			for (GenericValue x : partySuppliers) {
				Map<String, Object> supplier = FastMap.newInstance();
				supplier.putAll(x);
				supplier.putAll(getPartyTaxAuthInfo(delegator, x.get("partyId")));

                List<EntityCondition> conds = FastList.newInstance();
                conds.add(EntityCondition.makeCondition("partyId", x.get("partyId")));
                List<GenericValue> contacts = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conds), null, null, null, false);
                for(GenericValue contact : contacts) {
                    if(contact.getString("contactMechPurposeTypeId").contains("LOCATION")) {
                        GenericValue address = EntityUtil.getFirst(delegator.findList("PostalAddressDetail", EntityCondition.makeCondition("contactMechId", contact.get("contactMechId")), null, null, null, false));
                        supplier.put("addressDetail", address.get("fullName"));
                        supplier.put("locationContactMechId", contact.get("contactMechId"));
                    }
                    else if(contact.getString("contactMechPurposeTypeId").contains("EMAIL")) {
                        GenericValue email = EntityUtil.getFirst(delegator.findList("ContactMech", EntityCondition.makeCondition("contactMechId", contact.get("contactMechId")), null, null, null, false));
                        supplier.put("infoString", email.get("infoString"));
                        supplier.put("emailContactMechId", contact.get("contactMechId"));
                    }

                    else if(contact.getString("contactMechPurposeTypeId").contains("PHONE")) {
                        GenericValue phone = EntityUtil.getFirst(delegator.findList("TelecomNumber", EntityCondition.makeCondition("contactMechId", contact.get("contactMechId")), null, null, null, false));
                        supplier.put("contactNumber", phone.get("contactNumber"));
                        supplier.put("phoneContactMechId", contact.get("contactMechId"));
                    }
                }
                listPartySupplier.add(supplier);
			}
			successResult.put("listIterator", listPartySupplier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}

	public static Map<String, Object> getPartyTaxAuthInfo(Delegator delegator, Object partyId)
			throws GenericEntityException {
		Map<String, Object> partyTax = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
		List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
			GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
			partyTax.put("taxCode", partyTaxAuthInfo.get("partyTaxId"));
			partyTax.put("taxAuth",
					partyTaxAuthInfo.get("taxAuthGeoId") + "|" + partyTaxAuthInfo.get("taxAuthPartyId"));
		}
		return partyTax;
	}

	private static void createOrStorePartyTaxAuthInfo(LocalDispatcher dispatcher, Delegator delegator, Object partyId,
			String taxAuth, Object partyTaxId, GenericValue userLogin)
			throws GenericEntityException, GenericServiceException {
		String taxAuthGeoId = "";
		String taxAuthPartyId = "";
		if (UtilValidate.isNotEmpty(taxAuth)) {
			String[] taxAuthArray = taxAuth.split("\\|");
			taxAuthGeoId = taxAuthArray[0];
			taxAuthPartyId = taxAuthArray[1];
			
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(taxAuthGeoId)
					&& UtilValidate.isNotEmpty(taxAuthPartyId) && UtilValidate.isNotEmpty(partyTaxId)) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId",
						taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId)));
				List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(partyTaxAuthInfos)) {
					// createPartyTaxAuthInfo
					dispatcher.runSync("createPartyTaxAuthInfo",
							UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId",
									taxAuthPartyId, "partyTaxId", partyTaxId, "userLogin", userLogin));
				} else {
					for (GenericValue x : partyTaxAuthInfos) {
						x.set("partyTaxId", partyTaxId);
						x.store();
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> createPartySupplier(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"SUP_SUPPLIER_NEW");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = "NCC" + delegator.getNextSeqId("Party");
			String partyCode = (String) context.get("partyCode");
			String groupName = (String) context.get("groupName");
			String taxAuth = (String) context.get("taxAuth");

			dispatcher.runSync("createPartyGroup", UtilMisc.toMap("partyId", partyId, "preferredCurrencyUomId",
					context.get("preferredCurrencyUomId"), "groupName", groupName, "userLogin", userLogin));
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)),
					EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			// Create PartyRole
			createSupplierRole(delegator, partyId);

			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));

			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", organizationId, "partyIdTo", partyId, "roleTypeIdFrom",
							"INTERNAL_ORGANIZATIO", "roleTypeIdTo", "SUPPLIER", "fromDate", UtilDateTime.nowTimestamp(),
							"partyRelationshipTypeId", "SUPPLIER_REL", "userLogin", userLogin));

			createOrStorePartyTaxAuthInfo(dispatcher, delegator, partyId, taxAuth, context.get("taxCode"), userLogin);

			CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, null, "POSTAL_ADDRESS", groupName,
					groupName, "PRIMARY_LOCATION", partyId, context.get("address1"), context.get("countryGeoId"),
					context.get("stateProvinceGeoId"), context.get("districtGeoId"), context.get("wardGeoId"),
					context.get("stateProvinceGeoId"), context.get("postalCode"), userLogin);
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"),
					"PRIMARY_EMAIL", partyId, userLogin);
			CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE",
					context.get("contactNumber"), groupName, partyId, userLogin);
			
			//create finAcc for supplier
			String finAccountName = (String)context.get("finAccountName");
			String finAccountCode = (String)context.get("finAccountCode");
			if(finAccountName != null && finAccountCode != null){
				Map<String, Object> finAccMap = FastMap.newInstance();
				finAccMap.put("userLogin", userLogin);
				finAccMap.put("finAccountName", finAccountName);
				finAccMap.put("finAccountCode", finAccountCode);
				finAccMap.put("finAccountTypeId", "BANK_ACCOUNT");
				finAccMap.put("organizationPartyId", partyId);
				finAccMap.put("ownerPartyId", partyId);
				finAccMap.put("currencyUomId", context.get("preferredCurrencyUomId"));
				finAccMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
				finAccMap.put("countryGeoId", context.get("countryGeoId"));
				Map<String, Object> resultService = dispatcher.runSync("createFinAccount", finAccMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return resultService;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	private static void createSupplierRole(Delegator delegator, Object partyId) throws GenericEntityException {
		List<String> roles = Arrays.asList("SUPPLIER", "SUPPLIER_AGENT", "SHIP_FROM_VENDOR", "BILL_FROM_VENDOR");
		List<GenericValue> toBeStored = FastList.newInstance();
		for (String r : roles) {
			toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", r)));
		}
		delegator.storeAll(toBeStored);
	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> updatePartySupplier(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));

			String partyId = (String) context.get("partyId");
			String groupName = (String) context.get("groupName");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String taxAuth = (String) context.get("taxAuth");
			String phoneContactMechId = (String) context.get("phoneContactMechId");
			String emailContactMechId = (String) context.get("emailContactMechId");
			String locationContactMechId = (String) context.get("locationContactMechId");

			dispatcher.runSync("updatePartyGroup", UtilMisc.toMap("partyId", partyId, "preferredCurrencyUomId",
					context.get("preferredCurrencyUomId"), "groupName", groupName, "userLogin", userLogin));
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", context.get("partyCode")),
					EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));

			createOrStorePartyTaxAuthInfo(dispatcher, delegator, partyId, taxAuth, context.get("taxCode"), userLogin);

			if (UtilValidate.isNotEmpty(locationContactMechId)) {
				CallcenterServices.updateContactMechPostalAddress(dispatcher, delegator, locationContactMechId,
						"POSTAL_ADDRESS", groupName, groupName, "PRIMARY_LOCATION", partyId, context.get("address1"),
						context.get("countryGeoId"), context.get("stateProvinceGeoId"), context.get("districtGeoId"),
						context.get("wardGeoId"), context.get("stateProvinceGeoId"), context.get("postalCode"),
						userLogin);
			} else {
				CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, locationContactMechId,
						"POSTAL_ADDRESS", groupName, groupName, "PRIMARY_LOCATION", partyId, context.get("address1"),
						context.get("countryGeoId"), context.get("stateProvinceGeoId"), context.get("districtGeoId"),
						context.get("wardGeoId"), context.get("stateProvinceGeoId"), context.get("postalCode"),
						userLogin);
			}
			if (UtilValidate.isNotEmpty(emailContactMechId)) {
				CallcenterServices.updateContactMechEmail(dispatcher, delegator, emailContactMechId, "EMAIL_ADDRESS",
						context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			} else {
				CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"),
						"PRIMARY_EMAIL", partyId, userLogin);
			}
			if (UtilValidate.isNotEmpty(phoneContactMechId)) {
				CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, phoneContactMechId,
						"TELECOM_NUMBER", "PRIMARY_PHONE", context.get("contactNumber"), groupName, partyId, userLogin);
			} else {
				CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE",
						context.get("contactNumber"), groupName, partyId, userLogin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public Map<String, Object> createSupplierTarget(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"SUP_TARGET_NEW");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}

		String partyId = (String) context.get("partyId");
		String productId = (String) context.get("productId");
		String quantityUomId = (String) context.get("quantityUomId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");

		List<GenericValue> checkExists = delegator.findByAnd("SupplierTarget",
				UtilMisc.toMap("partyId", partyId, "productId", productId, "quantityUomId", quantityUomId), null, false);
		if (UtilValidate.isNotEmpty(checkExists)) {
			return ServiceUtil.returnError(UtilProperties.getMessage("BasePOUiLabels", "BPOSupplierTargetExisted",
					(Locale) context.get("locale")));
		}

		GenericValue supTarget = delegator.makeValue("SupplierTarget");
		supTarget.set("partyId", partyId);
		supTarget.set("productId", productId);
		supTarget.set("quantityUomId", quantityUomId);
		supTarget.set("quantity", quantity);
		supTarget.set("fromDate", fromDate);
		supTarget.set("thruDate", thruDate);

		try {
			supTarget.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

	public Map<String, Object> updateSupplierTarget(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException {
	    Map<String,Object> succesReturn = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"SUP_TARGET_EDIT");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}

		String partyId = (String) context.get("partyId");
		String productId = (String) context.get("productId");
		String quantityUomId = (String) context.get("quantityUomId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
        Long tmpFromDate = (Long) context.get("fromDate");
        Long tmpThruDate = (Long) context.get("thruDate");
        Timestamp fromDate = new Timestamp(tmpFromDate);
        Timestamp thruDate = new Timestamp(tmpThruDate);

		GenericValue supTarget = delegator.findOne("SupplierTarget",
				UtilMisc.toMap("partyId", partyId, "productId", productId, "quantityUomId", quantityUomId, "fromDate",fromDate), false);
		if (UtilValidate.isNotEmpty(supTarget)) {
			supTarget.set("quantity", quantity);
            supTarget.set("fromDate", fromDate);
            supTarget.set("thruDate", thruDate);
			supTarget.store();
		}
        succesReturn.put("messages","success");
		return succesReturn;
	}

	public Map<String, Object> deleteSupplierTarget(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"SUP_TARGET_DELETE");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotDeletePermission", locale));
		}

		String partyId = (String) context.get("partyId");
		String productId = (String) context.get("productId");
		String quantityUomId = (String) context.get("quantityUomId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

		GenericValue supTarget = delegator.findOne("SupplierTarget",
				UtilMisc.toMap("partyId", partyId, "productId", productId, "quantityUomId", quantityUomId,"fromDate",fromDate), false);
		if (UtilValidate.isNotEmpty(supTarget)) {
			try {
				supTarget.remove();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public Map<String, Object> loadPostalAddress(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		try {
			Object contactMechId = context.get("contactMechId");
			GenericValue postalAddress = delegator.findOne("PostalAddress",
					UtilMisc.toMap("contactMechId", contactMechId), false);
			result.put("postalAddress", postalAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Object> createOrStorePostalAddress(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue lookup = delegator.makeValidValue("PostalAddress", context);
			GenericValue postalAddress = delegator.createOrStore(lookup);
			result.put("contactMechId", postalAddress.getString("contactMechId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	public Map<String, Object> createPartySupplierQuick(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SUP_SUPPLIER_NEW");
		if (!hasPermission) {
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}
		
		try {
			String partyCode = (String) context.get("partyCode");
			if (UtilValidate.isNotEmpty(partyCode)) {
				UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			}
			String partyId = "NCC" + delegator.getNextSeqId("Party");
			if (UtilValidate.isEmpty(partyCode)) {
				partyCode = partyId;
			}
			String groupName = (String) context.get("groupName");
			
			// create party group
			Map<String, Object> createPartyResult = dispatcher.runSync("createPartyGroup", UtilMisc.toMap("partyId", partyId, 
							"preferredCurrencyUomId", context.get("preferredCurrencyUomId"), 
							"groupName", groupName, 
							"userLogin", userLogin));
			if (ServiceUtil.isError(createPartyResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createPartyResult));
			}
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyCode), EntityCondition.makeCondition("partyId", partyId));
			
			// create party role
			createSupplierRole(delegator, partyId);
			
			// create party relationship
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (organizationId != null) {
				Map<String, Object> createPartyRelResult = dispatcher.runSync("createPartyRelationship",
								UtilMisc.toMap("partyIdFrom", organizationId, 
										"partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", 
										"roleTypeIdTo", "SUPPLIER", "fromDate", UtilDateTime.nowTimestamp(),
										"partyRelationshipTypeId", "SUPPLIER_REL", "userLogin", userLogin));
				if (ServiceUtil.isError(createPartyRelResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createPartyRelResult));
				}
			}
			
			String phoneNumber = (String) context.get("phoneNumber");
			String emailAddress = (String) context.get("emailAddress");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String address1 = (String) context.get("address1");
			
			// create portal address
			if (UtilValidate.isNotEmpty(countryGeoId)) {
				Map<String, Object> contactMechCtx = UtilMisc.toMap(
						"countryGeoId", countryGeoId,
						"stateProvinceGeoId", stateProvinceGeoId,
						"districtGeoId", districtGeoId,
						"wardGeoId", wardGeoId,
						"address1", address1,
						"city", stateProvinceGeoId,
						"userLogin", userLogin, "locale", locale);
				Map<String, Object> postalAddressCreateResult = dispatcher.runSync("createPostalAddress", contactMechCtx);
				if (ServiceUtil.isError(postalAddressCreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(postalAddressCreateResult));
				}
				
				String postalAddressId = (String) postalAddressCreateResult.get("contactMechId");
				if (postalAddressId != null) {
					// add party to postal address
					Map<String, Object> partyPACreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("contactMechId", postalAddressId, "partyId", partyId, "userLogin", userLogin));
					if (ServiceUtil.isError(partyPACreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyPACreateResult));
					}
					// add others purpose into postal address
					List<String> postalAddressPurposes = UtilMisc.toList("BILLING_LOCATION", "PRIMARY_LOCATION", "PAYMENT_LOCATION");
					for (String postalAddressPurpose : postalAddressPurposes) {
						Map<String, Object> partyPAPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
								"contactMechId", postalAddressId, "contactMechPurposeTypeId", postalAddressPurpose, "userLogin", userLogin));
						if (ServiceUtil.isError(partyPAPCreateResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyPAPCreateResult));
						}
					}
				}
			}
			
			// create telecom number
			if (UtilValidate.isNotEmpty(phoneNumber)) {
				Map<String, Object> telecomNumberCreateResult = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap("contactNumber", phoneNumber, "askForName", groupName, "userLogin", userLogin));
				if (ServiceUtil.isError(telecomNumberCreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(telecomNumberCreateResult));
				}
				String telecomNumberId = (String) telecomNumberCreateResult.get("contactMechId");
				if (telecomNumberId != null) {
					// add party to telecom number
					Map<String, Object> partyTELCreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("contactMechId", telecomNumberId, "partyId", partyId, "userLogin", userLogin));
					if (ServiceUtil.isError(partyTELCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyTELCreateResult));
					}
					// add others purpose into postal address
					List<String> telecomNumberPurposes = UtilMisc.toList("PRIMARY_PHONE");
					for (String telecomNumberPurpose : telecomNumberPurposes) {
						Map<String, Object> partyTELPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
								"contactMechId", telecomNumberId, "contactMechPurposeTypeId", telecomNumberPurpose, "userLogin", userLogin));
						if (ServiceUtil.isError(partyTELPCreateResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyTELPCreateResult));
						}
					}
				}
			}
			
			// create email
			if (UtilValidate.isNotEmpty(emailAddress)) {
				Map<String, Object> emailCreateResult = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, 
						"contactMechTypeId", "EMAIL_ADDRESS", "infoString", emailAddress, "userLogin", userLogin));
				String emailId = (String) emailCreateResult.get("contactMechId");
				if (emailId != null) {
					// add others purpose into postal address
					List<String> emailPurposes = UtilMisc.toList("PRIMARY_EMAIL");
					for (String emailPurpose : emailPurposes) {
						Map<String, Object> partyEmPCreateResult = dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
								"contactMechId", emailId, "contactMechPurposeTypeId", emailPurpose, "userLogin", userLogin));
						if (ServiceUtil.isError(partyEmPCreateResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(partyEmPCreateResult));
						}
					}
				}
			}
			
			// create tax info
			String taxCode = (String) context.get("taxCode");
			String taxAuthGeoId = (String) context.get("taxAuthGeoId");
			String taxAuthPartyId = (String) context.get("taxAuthPartyId");
			if (UtilValidate.isNotEmpty(taxAuthGeoId) && UtilValidate.isNotEmpty(taxAuthPartyId) && UtilValidate.isNotEmpty(taxCode)) {
				Map<String, Object> createResult = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, 
								"taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId, 
								"partyTaxId", taxCode, "userLogin", userLogin));
				if (ServiceUtil.isError(createResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
				}
			}
			
			result.put("partyId", partyId);
		} catch (Exception e) {
			String errMsg = "Fatal error calling createPartySupplierQuick service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSupplierProducts(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productId");
			}
			Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				String partyId = parameters.get("partyId")[0];
				listAllConditions.add(EntityCondition.makeCondition("isVirtual", EntityJoinOperator.EQUALS, "N"));
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
				listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate")));
				listAllConditions.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("purchaseDiscontinuationDate", EntityOperator.EQUALS, null),
						EntityOperator.OR, EntityCondition.makeCondition("purchaseDiscontinuationDate",
								EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis()))));
				EntityListIterator listIterator = delegator.find("SupplierProductAndProduct",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
    }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSupplierProductsWithoutSupplier(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productCode");
			}
			/*Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0
					&& parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
				String partyId = parameters.get("partyId")[0];
				String currencyUomId = parameters.get("currencyUomId")[0];
				List<String> productIds = productIdsOfSupplier(delegator, partyId, currencyUomId);
				if (UtilValidate.isNotEmpty(productIds)) {
					listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_IN, productIds));
				}
				EntityListIterator listIterator = delegator.find("ProductAndCategoryPrimary",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}*/
			EntityListIterator listIterator = delegator.find("ProductAndCategoryPrimary",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/*private static List<String> productIdsOfSupplier(Delegator delegator,Object partyId, Object currencyUomId) {
		List<String> productIds = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "currencyUomId", currencyUomId)));
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate")));
			List<GenericValue> supplierProducts = delegator.findList("SupplierProduct",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			productIds = EntityUtil.getFieldListFromEntityList(supplierProducts, "productId", true);
		} catch (Exception e) {
		}
		return productIds;
	}*/


	public static Map<String, Object> addProductsToSupplier(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			String productItemsStr = (String) context.get("productItems");
			
			List<Map<String, Object>> listProduct = FastList.newInstance();
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productItemsStr)) {
				jsonArray = JSONArray.fromObject(productItemsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					productItem.put("productId", prodItem.getString("productId"));
					if (prodItem.containsKey("uomId")) productItem.put("uomId", prodItem.getString("uomId"));
					if (prodItem.containsKey("purchasePrice")) {
						productItem.put("purchasePrice", prodItem.getString("purchasePrice"));
						//String purchasePriceStr = prodItem.getString("purchasePrice");
						//if (UtilValidate.isNotEmpty(purchasePriceStr)) productItem.put("purchasePrice", new BigDecimal(purchasePriceStr));
					}
					listProduct.add(productItem);
				}
			}
			
			if (listProduct.size() < 1) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouNotYetChooseProduct", locale));
			}
			
			List<String> responseMessages = FastList.newInstance();
			List<String> productIdsSuccess = FastList.newInstance();
			List<String> productIdsNotSuccess = FastList.newInstance();
			
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if (party == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSIsNotFound", locale));
			}
			String currencyUomId = party.getString("preferredCurrencyUomId");
			String comments = null;
			String availableFromDateStr = (String) context.get("availableFromDate");
			String availableThruDateStr = (String) context.get("availableThruDate");
			/*Timestamp availableFromDate = null;
			if (UtilValidate.isNotEmpty(availableFromDateStr)) {
	        	Long availableFromDateL = Long.parseLong(availableFromDateStr);
	        	availableFromDate = new Timestamp(availableFromDateL);
	        }
			Timestamp availableThruDate = null;
			if (UtilValidate.isNotEmpty(availableThruDateStr)) {
	        	Long availableThruDateL = Long.parseLong(availableThruDateStr);
	        	availableThruDate = new Timestamp(availableThruDateL);
	        }*/
			String canDropShip = "N";
			String supplierPrefOrderId = "10_MAIN_SUPPL";
			
			Map<String, Object> productCtx = FastMap.newInstance();
			for (Map<String, Object> productItem : listProduct) {
				String productId = (String) productItem.get("productId");
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("partyId", partyId);
				productCtx.put("currencyUomId", currencyUomId);
				productCtx.put("quantityUomId", productItem.get("uomId"));
				productCtx.put("comments", comments);
				productCtx.put("availableFromDate", availableFromDateStr);
				productCtx.put("availableThruDate", availableThruDateStr);
				productCtx.put("lastPrice", productItem.get("purchasePrice"));
				productCtx.put("shippingPrice", null);
				productCtx.put("minimumOrderQuantity", "1");
				productCtx.put("canDropShip", canDropShip);
				productCtx.put("supplierPrefOrderId", supplierPrefOrderId);
				productCtx.put("supplierProductId", productId);
				productCtx.put("userLogin", userLogin);
				productCtx.put("locale", locale);
				Map<String, Object> addResult = dispatcher.runSync("addNewSupplierProduct", productCtx);
				if (ServiceUtil.isError(addResult)) {
					Debug.logWarning("ERROR when create supplier product with [supplierId = " + partyId + ", productId = " + productId + "]. Error message: " + ServiceUtil.getErrorMessage(addResult), module);
					productIdsNotSuccess.add(productId);
				} else {
					productIdsSuccess.add(productId);
				}
			}
			if (responseMessages.size() > 0) {
				responseMessages.add(0, "Ket qua: " + productIdsSuccess.size() + " san pham thanh cong. " + productIdsNotSuccess.size() + " san pham loi");
				successResult = ServiceUtil.returnSuccess(responseMessages);
				successResult.put("productIdsNotSuccess", productIdsNotSuccess);
				Debug.logWarning("Missing sales or puchase price. List product don't add to category: " + productIdsNotSuccess.toString(), module);
			}
			successResult.put("productIdsSuccess", productIdsSuccess);
		} catch (Exception e) {
    		String errMsg = "Fatal error calling addProductsToSupplier service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
		return successResult;
	}
	
	
}
