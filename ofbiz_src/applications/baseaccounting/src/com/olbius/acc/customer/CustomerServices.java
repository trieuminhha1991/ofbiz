package com.olbius.acc.customer;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CustomerServices {
	public static final String CUSTOMER_ROLE = "CUSTOMER";
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListCustomerJQ(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        String statusId = parameters.get("statusId") != null? parameters.get("statusId")[0] : null;
        try {
            if(statusId == null || "active".equals(statusId)){
                listAllConditions.add(EntityUtil.getFilterByDateExpr());
            }else{
                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null));
            }
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyCustomerAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }
	
	public static Map<String, Object> createCustomer(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String partyCode = (String)context.get("partyCode");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String finAccountName = (String)context.get("finAccountName");
		String finAccountCode = (String)context.get("finAccountCode");
		String partyRepresent = (String)context.get("partyRepresent");
		String partyBeneficiary = (String)context.get("partyBeneficiary");
		String taxCode = (String)context.get("taxCode");
		String phoneNumber = (String)context.get("phoneNumber");
		String faxNumber = (String)context.get("faxNumber");
		String emailAddress = (String)context.get("emailAddress");
		String address1 = (String)context.get("address1");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String company = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> partyCodeList = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isNotEmpty(partyCodeList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "PartyCodeIsExists", UtilMisc.toMap("partyCode", partyCode), locale));
			}
			Map<String, Object> resultService = dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName", context.get("groupName"), "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			String partyId = (String)resultService.get("partyId");
			PartyUtil.updatePartyCode(delegator, partyId, partyCode);
			resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", CUSTOMER_ROLE, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			Map<String, Object> serviceMap = FastMap.newInstance();
			serviceMap.put("userLogin", userLogin);
			serviceMap.put("partyIdFrom", partyId);
			serviceMap.put("partyIdTo", company);
			serviceMap.put("roleTypeIdFrom", CUSTOMER_ROLE);
			serviceMap.put("roleTypeIdTo", "INTERNAL_ORGANIZATIO");
			serviceMap.put("partyRelationshipTypeId", "CUSTOMER_REL");
			serviceMap.put("fromDate", fromDate);
            resultService = dispatcher.runSync("createPartyRelationDmsLog", serviceMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			//create party customer
            GenericValue partyCustomer = delegator.makeValue("PartyCustomer");
			partyCustomer.set("partyId", partyId);
			partyCustomer.set("partyTypeId", "RETAIL_OUTLET");
			partyCustomer.set("fullName", context.get("groupName"));
			partyCustomer.set("partyCode", partyCode);
			partyCustomer.set("statusId", "PARTY_ENABLED");
			partyCustomer.set("visitFrequencyTypeId", "F0");
			partyCustomer.set("activatedDate", fromDate);
			partyCustomer.create();

			if(finAccountCode != null && finAccountName != null && finAccountCode.trim().length() > 0 && finAccountCode.trim().length() > 0){
				String defaultUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("finAccountName", finAccountName);
				serviceMap.put("finAccountCode", finAccountCode);
				serviceMap.put("finAccountTypeId", "BANK_ACCOUNT");
				serviceMap.put("organizationPartyId", partyId);
				serviceMap.put("ownerPartyId", partyId);
				serviceMap.put("currencyUomId", context.get("preferredCurrencyUomId") != null? context.get("preferredCurrencyUomId"): defaultUomId);
				serviceMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
				serviceMap.put("countryGeoId", context.get("countryGeoId"));
				resultService = dispatcher.runSync("createFinAccount", serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(partyBeneficiary != null && partyBeneficiary.trim().length() > 0){
				GenericValue partyBeneficiaryAtt = delegator.makeValue("PartyAttribute");
				partyBeneficiaryAtt.set("partyId", partyId);
				partyBeneficiaryAtt.set("attrName", "BENEFICIARY");
				partyBeneficiaryAtt.set("attrValue", partyBeneficiary);
				partyBeneficiaryAtt.create();
			}
			if(partyRepresent != null && partyRepresent.trim().length() > 0){
				GenericValue partyRepresentAtt = delegator.makeValue("PartyAttribute");
				partyRepresentAtt.set("partyId", partyId);
				partyRepresentAtt.set("attrName", "REPRESENTATIVE");
				partyRepresentAtt.set("attrValue", partyRepresent);
				partyRepresentAtt.create();
			}
			if(phoneNumber != null && phoneNumber.trim().length() > 0){
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				serviceMap.put("partyId", partyId);
				serviceMap.put("fromDate", fromDate);
				serviceMap.put("contactNumber", phoneNumber);
				resultService = dispatcher.runSync("createPartyTelecomNumber", serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(faxNumber != null && faxNumber.trim().length() > 0){
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "FAX_NUMBER");
				serviceMap.put("partyId", partyId);
				serviceMap.put("fromDate", fromDate);
				serviceMap.put("contactNumber", faxNumber);
				resultService = dispatcher.runSync("createPartyTelecomNumber", serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(emailAddress != null && emailAddress.trim().length() > 0){
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				serviceMap.put("partyId", partyId);
				serviceMap.put("fromDate", fromDate);
				serviceMap.put("emailAddress", emailAddress);
				resultService = dispatcher.runSync("createPartyEmailAddress", serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(address1 != null){
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				serviceMap.put("partyId", partyId);
				serviceMap.put("fromDate", fromDate);
				serviceMap.put("postalCode", "10000");
				serviceMap.put("address1", address1);
				serviceMap.put("countryGeoId", context.get("countryGeoId"));
				serviceMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
				serviceMap.put("districtGeoId", context.get("districtGeoId"));
				serviceMap.put("wardGeoId", context.get("wardGeoId"));
				resultService = dispatcher.runSync("createPartyPostalAddress", serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(taxCode != null){
				serviceMap.clear();
				String defaultGeoId = EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", "VNM", delegator);
				List<GenericValue> taxAuthorityList = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", context.get("countryGeoId")), null, false);
				if(UtilValidate.isEmpty(taxAuthorityList)){
					taxAuthorityList = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", defaultGeoId), null, false);
				}
				if(UtilValidate.isNotEmpty(taxAuthorityList)){
					serviceMap.put("userLogin", userLogin);
					serviceMap.put("partyTaxId", taxCode);
					serviceMap.put("partyId", partyId);
					serviceMap.put("fromDate", fromDate);
					serviceMap.put("taxAuthGeoId", taxAuthorityList.get(0).get("taxAuthGeoId"));
					serviceMap.put("taxAuthPartyId", taxAuthorityList.get(0).get("taxAuthPartyId"));
					resultService = dispatcher.runSync("createPartyTaxAuthInfo", serviceMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateCustomer(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyCode = (String)context.get("partyCode");
		String partyId = (String)context.get("partyId");
		String finAccountName = (String)context.get("finAccountName");
		String finAccountCode = (String)context.get("finAccountCode");
		String partyRepresent = (String)context.get("partyRepresent");
		String partyBeneficiary = (String)context.get("partyBeneficiary");
		String phoneNumber = (String)context.get("phoneNumber");
		String faxNumber = (String)context.get("faxNumber");
		String emailAddress = (String)context.get("emailAddress");
		String address1 = (String)context.get("address1");
		String locationContactMechId = (String)context.get("locationContactMechId");
		String emailContactMechId = (String)context.get("emailContactMechId");
		String faxContactMechId = (String)context.get("faxContactMechId");
		String phoneContactMechId = (String)context.get("phoneContactMechId");
		String taxCode = (String)context.get("taxCode");
		String taxAuthPartyId = (String)context.get("taxAuthPartyId");
		String taxAuthGeoId = (String)context.get("taxAuthGeoId");
		String finAccountId = (String)context.get("finAccountId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			Map<String, Object> serviceMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyGroup", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePartyGroup", serviceMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			PartyUtil.updatePartyCode(delegator, partyId, partyCode);

			//update party customer
            GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
            partyCustomer.set("fullName", context.get("groupName"));
            partyCustomer.set("partyCode", partyCode);
            partyCustomer.store();
			
			if(partyBeneficiary != null && partyBeneficiary.trim().length() > 0){
				GenericValue partyBeneficiaryAtt = delegator.makeValue("PartyAttribute");
				partyBeneficiaryAtt.set("partyId", partyId);
				partyBeneficiaryAtt.set("attrName", "BENEFICIARY");
				partyBeneficiaryAtt.set("attrValue", partyBeneficiary);
				delegator.createOrStore(partyBeneficiaryAtt);
			}
			if(partyRepresent != null && partyRepresent.trim().length() > 0){
				GenericValue partyRepresentAtt = delegator.makeValue("PartyAttribute");
				partyRepresentAtt.set("partyId", partyId);
				partyRepresentAtt.set("attrName", "REPRESENTATIVE");
				partyRepresentAtt.set("attrValue", partyRepresent);
				delegator.createOrStore(partyRepresentAtt);
			}
			if(finAccountCode != null && finAccountName != null && finAccountCode.trim().length() > 0 && finAccountCode.trim().length() > 0){
				serviceMap.clear();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("finAccountName", finAccountName);
				serviceMap.put("finAccountCode", finAccountCode);
				String serviceName = "updateFinAccount";
				if(UtilValidate.isEmpty(finAccountId)){
					String defaultUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
					serviceMap.put("finAccountTypeId", "BANK_ACCOUNT");
					serviceMap.put("organizationPartyId", partyId);
					serviceMap.put("ownerPartyId", partyId);
					serviceMap.put("currencyUomId", context.get("preferredCurrencyUomId") != null? context.get("preferredCurrencyUomId"): defaultUomId);
					serviceMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
					serviceMap.put("countryGeoId", context.get("countryGeoId"));
					serviceName = "createFinAccount";
				}else{
					serviceMap.put("finAccountId", finAccountId);
				}
				resultService = dispatcher.runSync(serviceName, serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(phoneNumber != null && phoneNumber.trim().length() > 0){
				serviceMap.clear();
				String serviceName = "updatePartyTelecomNumber";
				if(UtilValidate.isNotEmpty(phoneContactMechId) && phoneContactMechId.trim().length() > 0){
					serviceMap.put("contactMechId", phoneContactMechId);
				}else{
					serviceName = "createPartyTelecomNumber";
					serviceMap.put("fromDate", fromDate);
				}
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				serviceMap.put("partyId", partyId);
				serviceMap.put("contactNumber", phoneNumber);
				resultService = dispatcher.runSync(serviceName, serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(faxNumber != null && faxNumber.trim().length() > 0){
				serviceMap.clear();
				String serviceName = "updatePartyTelecomNumber";
				if(UtilValidate.isNotEmpty(faxContactMechId) && faxContactMechId.trim().length() > 0){
					serviceMap.put("contactMechId", faxContactMechId);
				}else{
					serviceName = "createPartyTelecomNumber";
					serviceMap.put("fromDate", fromDate);
				}
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "FAX_NUMBER");
				serviceMap.put("partyId", partyId);
				serviceMap.put("contactNumber", faxNumber);
				resultService = dispatcher.runSync(serviceName, serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(emailAddress != null && emailAddress.trim().length() > 0){
				serviceMap.clear();
				String serviceName = "updatePartyEmailAddress";
				if(UtilValidate.isNotEmpty(emailContactMechId) && emailContactMechId.trim().length() > 0){
					serviceMap.put("contactMechId", emailContactMechId);
				}else{
					serviceName = "createPartyEmailAddress";
					serviceMap.put("fromDate", fromDate);
				}
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				serviceMap.put("partyId", partyId);
				serviceMap.put("emailAddress", emailAddress);
				resultService = dispatcher.runSync(serviceName, serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			
			if(UtilValidate.isNotEmpty(address1) && address1.trim().length() > 0){
				serviceMap.clear();
				String serviceName = "updatePartyPostalAddress";
				if(UtilValidate.isNotEmpty(locationContactMechId) && locationContactMechId.trim().length() > 0){
					serviceMap.put("contactMechId", locationContactMechId);
					serviceMap.put("postalCode", "10000");
				}else{
					serviceName = "createPartyPostalAddress";
					serviceMap.put("fromDate", fromDate);
					serviceMap.put("postalCode", "10000");
				}
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				serviceMap.put("partyId", partyId);
				serviceMap.put("address1", address1);
				serviceMap.put("countryGeoId", context.get("countryGeoId"));
				serviceMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
				serviceMap.put("districtGeoId", context.get("districtGeoId"));
				serviceMap.put("wardGeoId", context.get("wardGeoId"));
				resultService = dispatcher.runSync(serviceName, serviceMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
			if(UtilValidate.isNotEmpty(taxCode) && taxCode.trim().length() > 0){
				serviceMap.clear();
				serviceMap.put("partyTaxId", taxCode);
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("partyId", partyId);
				if(taxAuthGeoId != null && taxAuthPartyId != null){
					List<GenericValue> partyTaxAuthInfoList = delegator.findByAnd("PartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId), UtilMisc.toList("-fromDate"), false);
					if(UtilValidate.isNotEmpty(partyTaxAuthInfoList)){
						GenericValue partyTaxAuthInfo = partyTaxAuthInfoList.get(0);
						serviceMap.put("fromDate", partyTaxAuthInfo.get("fromDate"));
						serviceMap.put("taxAuthGeoId", taxAuthGeoId);
						serviceMap.put("taxAuthPartyId", taxAuthPartyId);
						resultService = dispatcher.runSync("updatePartyTaxAuthInfo", serviceMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
					}
				}else{
					String defaultGeoId = EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", "VNM", delegator);
					List<GenericValue> taxAuthorityList = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", context.get("countryGeoId")), null, false);
					if(UtilValidate.isEmpty(taxAuthorityList)){
						taxAuthorityList = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthGeoId", defaultGeoId), null, false);
					}
					if(UtilValidate.isNotEmpty(taxAuthorityList)){
						serviceMap.put("fromDate", fromDate);
						serviceMap.put("taxAuthGeoId", taxAuthorityList.get(0).get("taxAuthGeoId"));
						serviceMap.put("taxAuthPartyId", taxAuthorityList.get(0).get("taxAuthPartyId"));
						resultService = dispatcher.runSync("createPartyTaxAuthInfo", serviceMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
					}
				}
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> expireCustomerRelationship(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		conds.add(EntityCondition.makeCondition("roleTypeIdFrom", CUSTOMER_ROLE));
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"));
		conds.add(EntityUtil.getFilterByDateExpr());
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			List<GenericValue> customerList = delegator.findList("PartyRelationDmsLog", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			for(GenericValue customer: customerList){
				customer.set("thruDate", nowTimestamp);
				delegator.store(customer);
			}
			GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
            partyCustomer.set("statusId", "PARTY_DISABLED");
            partyCustomer.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
	
	public static Map<String, Object> activeCustomerRelationship(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		List<EntityCondition> conds = FastList.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		conds.add(EntityCondition.makeCondition("roleTypeIdFrom", CUSTOMER_ROLE));
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"));
		conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN, nowTimestamp));
		conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null));
		try {
			List<GenericValue> customerList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			for(GenericValue customer: customerList){
				customer.set("thruDate", null);
				delegator.store(customer);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
}
