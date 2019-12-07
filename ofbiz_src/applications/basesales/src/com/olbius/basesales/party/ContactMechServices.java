package com.olbius.basesales.party;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.product.ProductServices;

public class ContactMechServices {
	public static final String module = ProductServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetListCountryGeo(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	try {
    		listIterator = CommonWorkers.getCountryList(delegator);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListCountryGeo service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> getListCountryGeo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		try {
			listIterator = CommonWorkers.getCountryList(delegator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListCountryGeo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("records", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetAssociatedStateListGeo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		try {
			if (parameters.containsKey("geoId") && parameters.get("geoId").length > 0) {
    			String geoId = parameters.get("geoId")[0];
    			if (UtilValidate.isNotEmpty(geoId)) {
    				listIterator = CommonWorkers.getAssociatedStateList(delegator, geoId, null);
    			}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetAssociatedStateListGeo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (listIterator == null) {
			listIterator = new ArrayList<GenericValue>();
		}
		if (listIterator.size() <= 0) {
			GenericValue geoAssocAndGeoToWithState = delegator.makeValue("GeoAssocAndGeoToWithState");
			geoAssocAndGeoToWithState.put("geoId", "_NA_");
			geoAssocAndGeoToWithState.put("geoName", UtilProperties.getMessage(resource, "BSNoAddressExists", locale));
			listIterator.add(geoAssocAndGeoToWithState);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetAssociatedStateOtherListGeo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		try {
			if (parameters.containsKey("geoId") && parameters.get("geoId").length > 0) {
				String geoId = parameters.get("geoId")[0];
				if (UtilValidate.isNotEmpty(geoId)) {
					listAllConditions.add(EntityCondition.makeCondition("geoIdFrom", geoId));
					listAllConditions.add(EntityCondition.makeCondition("geoAssocTypeId", "REGIONS"));
					listAllConditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("geoTypeId", "STATE"), 
							EntityCondition.makeCondition("geoTypeId", "PROVINCE"), 
							EntityCondition.makeCondition("geoTypeId", "MUNICIPALITY"),
                            EntityCondition.makeCondition("geoTypeId", "COUNTY"),
                            EntityCondition.makeCondition("geoTypeId", "DISTRICT"),
                            EntityCondition.makeCondition("geoTypeId", "WARD")));
					listIterator = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, true);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetAssociatedStateOtherListGeo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (listIterator == null) {
			listIterator = new ArrayList<GenericValue>();
		}
		if (listIterator.size() <= 0) {
			GenericValue geoAssocAndGeoTo = delegator.makeValue("GeoAssocAndGeoTo");
			geoAssocAndGeoTo.put("geoId", "_NA_");
			geoAssocAndGeoTo.put("geoName", UtilProperties.getMessage(resource, "BSNoAddressExists", locale));
			listIterator.add(geoAssocAndGeoTo);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createPostalAddressShippingForParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		//GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = (String) context.get("partyId");
			String contactMechTypeId = (String) context.get("contactMechTypeId");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String address1 = (String) context.get("address1");
			String postalCode = (String) context.get("postalCode");
			String allowSolicitation = (String) context.get("allowSolicitation");
			String isPrimaryLocation = (String) context.get("isPrimaryLocation");
			String contactMechPurposeTypeId = null;
			
			
			
			List<String> errorMessages = new ArrayList<String>();
			if (UtilValidate.isEmpty(partyId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "BSPartyIsEmpty", locale));
			}
			if (UtilValidate.isEmpty(address1)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "BSAddressMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(countryGeoId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "BSCountryGeoMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(stateProvinceGeoId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "BSStateProvinceGeoMustNotBeEmpty", locale));
			}
			if (errorMessages.size() > 0) {
				return ServiceUtil.returnError(errorMessages);
			}
			
			if(UtilValidate.isNotEmpty(isPrimaryLocation) && isPrimaryLocation.equals("Y")) {
				List<EntityCondition> locationCond = FastList.newInstance();
				locationCond.add(EntityCondition.makeCondition("partyId", partyId));
				locationCond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
				locationCond.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> listPrimaryLocation = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(locationCond, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(listPrimaryLocation)) {
					errorMessages.add(UtilProperties.getMessage(resource, "BSDistributorHasContainPrimaryLocation", locale));
					return ServiceUtil.returnError(errorMessages);
				}
				contactMechPurposeTypeId = "PRIMARY_LOCATION";
			}else if(UtilValidate.isNotEmpty(isPrimaryLocation) && isPrimaryLocation.equals("N")){
				contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
			}
			
			GenericValue city = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
			if (city == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSStateProvinceNotFound", locale));
			}
			
			GenericValue systemUser = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			
			String cityName = city.getString("geoName");
			Map<String, Object> contactMechCtx = FastMap.newInstance();
			contactMechCtx.put("partyId", partyId);
			contactMechCtx.put("contactMechTypeId", contactMechTypeId);
			contactMechCtx.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			contactMechCtx.put("toName", toName);
			contactMechCtx.put("attnName", attnName);
			contactMechCtx.put("countryGeoId", countryGeoId);
			contactMechCtx.put("stateProvinceGeoId", stateProvinceGeoId);
			// contactMechCtx.put("countyGeoId", countyGeoId);
			contactMechCtx.put("districtGeoId", districtGeoId);
			contactMechCtx.put("wardGeoId", wardGeoId);
			contactMechCtx.put("address1", address1);
			contactMechCtx.put("postalCode", postalCode);
			contactMechCtx.put("allowSolicitation", allowSolicitation);
			contactMechCtx.put("city", cityName);
			contactMechCtx.put("userLogin", systemUser);
			
			Map<String, Object> contactMechMap = ServiceUtil.setServiceFields(dispatcher, "createPostalAddress", contactMechCtx, systemUser, null, locale);
			Map<String, Object> returnValue = dispatcher.runSync("createPostalAddress", contactMechMap);
			if (ServiceUtil.isError(returnValue)) return returnValue;
			String contactMechId = (String) returnValue.get("contactMechId");
			if (UtilValidate.isEmpty(contactMechId)) return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSRunServiceError", locale)); 
			successResult.put("contactMechId", returnValue.get("contactMechId"));
			
			Map<String, Object> contactMechPurposeMap = ServiceUtil.setServiceFields(dispatcher, "createPartyContactMech", contactMechCtx, systemUser, null, locale);
			contactMechPurposeMap.put("contactMechId", contactMechId);
			contactMechPurposeMap.put("contactMechTypeId", "POSTAL_ADDRESS");
			Map<String, Object> returnValue2 = dispatcher.runSync("createPartyContactMech", contactMechPurposeMap);
			if (ServiceUtil.isError(returnValue2)) return returnValue;
			
		} catch (Exception e) {
			String errMsg = "Fatal error calling createPostalAddressShippingForParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> updatePostalAddressShippingForParty(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		//LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		//GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String contactMechId = (String) context.get("contactMechId");
			//String contactMechTypeId = (String) context.get("contactMechTypeId");
			//String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String countyGeoId = (String) context.get("countyGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String address1 = (String) context.get("address1");
			String postalCode = (String) context.get("postalCode");
			String allowSolicitation = (String) context.get("allowSolicitation");
			String partyId = (String) context.get("partyId");
			
			List<String> errorMessages = new ArrayList<String>();
			if (UtilValidate.isEmpty(contactMechId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "DANotFoundPostalAddress", locale));
			}
			if (UtilValidate.isEmpty(address1)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "DAAddressMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(countryGeoId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "DACountryGeoMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(stateProvinceGeoId)) {
				errorMessages.add(UtilProperties.getMessage(resource_error, "DAStateProvinceGeoMustNotBeEmpty", locale));
			}
			if (errorMessages.size() > 0) {
				return ServiceUtil.returnError(errorMessages);
			}
			
			GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
			if (postalAddress == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DANotFoundPostalAddress", locale));
			}
			
			GenericValue city = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
			if (city == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAStateProvinceNotFound", locale));
			}
			successResult.put("contactMechId", contactMechId);
			
			String cityName = city.getString("geoName");
			
			List<GenericValue> listPartyContactMech = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId, "partyId", partyId), null, false);
			
			/*
			GenericValue systemUser = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> contactMechCtx = FastMap.newInstance();
			contactMechCtx.put("partyId", partyId);
			contactMechCtx.put("contactMechId", contactMechId);
			contactMechCtx.put("contactMechTypeId", contactMechTypeId);
			contactMechCtx.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			contactMechCtx.put("toName", toName);
			contactMechCtx.put("attnName", attnName);
			contactMechCtx.put("countryGeoId", countryGeoId);
			contactMechCtx.put("stateProvinceGeoId", stateProvinceGeoId);
			// contactMechCtx.put("countyGeoId", countyGeoId);
			contactMechCtx.put("districtGeoId", countyGeoId);
			contactMechCtx.put("wardGeoId", wardGeoId);
			contactMechCtx.put("address1", address1);
			contactMechCtx.put("postalCode", postalCode);
			contactMechCtx.put("allowSolicitation", allowSolicitation);
			contactMechCtx.put("city", cityName);
			contactMechCtx.put("userLogin", systemUser);
			contactMechCtx.put("locale", locale);
			
			Map<String, Object> contactMechMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyPostalAddress", contactMechCtx, systemUser, null, locale);
			Map<String, Object> returnValue = dispatcher.runSync("updatePartyPostalAddress", contactMechMap);
			if (ServiceUtil.isError(returnValue)) return returnValue;
			if (UtilValidate.isEmpty(contactMechId)) return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DARunServiceError", locale)); */
			
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			postalAddress.put("toName", toName);
			postalAddress.put("attnName", attnName);
			postalAddress.put("countryGeoId", countryGeoId);
			postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
			// postalAddress.put("countyGeoId", countyGeoId);
			postalAddress.put("districtGeoId", countyGeoId);
			postalAddress.put("wardGeoId", wardGeoId);
			postalAddress.put("address1", address1);
			postalAddress.put("postalCode", postalCode);
			postalAddress.put("city", cityName);
			tobeStored.add(postalAddress);
			
			if (UtilValidate.isNotEmpty(listPartyContactMech)) {
				for (GenericValue partyContactMech : listPartyContactMech) {
					partyContactMech.put("allowSolicitation", allowSolicitation);
					tobeStored.add(partyContactMech);
				}
			}
			delegator.storeAll(tobeStored);
		} catch (Exception e) {
			String errMsg = "Fatal error calling updatePostalAddressShippingForParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> createPartyContactMechPurpose(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		
		String partyId = (String) context.get("partyId");
		String contactMechId = (String) context.get("contactMechId");
		String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			List<EntityCondition> listAllConditions = FastList.newInstance();
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("contactMechId", contactMechId));
			listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisRecordIsExisted", locale));
			}
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("partyId", partyId);
			contextMap.put("contactMechId", contactMechId);
			contextMap.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			contextMap.put("userLogin", userLogin);
			if (system == null) {
				contextMap.put("userLogin", system);
			}
			Map<String, Object> resultValue = dispatcher.runSync("createPartyContactMechPurpose", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				return resultValue;
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createPartyContactMechPurpose service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}
	
	public static Map<String, Object> deletePartyContactMechPurpose(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DACreateSuccessful", locale));
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		String partyId = (String) context.get("partyId");
		String contactMechId = (String) context.get("contactMechId");
		String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("partyId", partyId);
			contextMap.put("contactMechId", contactMechId);
			contextMap.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			contextMap.put("fromDate", fromDate);
			contextMap.put("userLogin", userLogin);
			if (system == null) {
				contextMap.put("userLogin", system);
			}
			Map<String, Object> resultValue = dispatcher.runSync("deletePartyContactMechPurpose", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				return resultValue;
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deletePartyContactMechPurpose service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPartyContactMechPurpose(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String partyId = null;
			String contactMechId = null;
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			partyId = parameters.get("partyId")[0];
			}
			if (parameters.containsKey("contactMechId") && parameters.get("contactMechId").length > 0) {
				contactMechId = parameters.get("contactMechId")[0];
			}
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("contactMechId", contactMechId));
			listIterator = delegator.find("PartyContactMechPurpose", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyContactMechPurpose service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListContactMechPurposeType(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		try {
			String contactMechTypeId = null;
			if (parameters.containsKey("contactMechTypeId") && parameters.get("contactMechTypeId").length > 0) {
				contactMechTypeId = parameters.get("contactMechTypeId")[0];
			}
			List<String> contactMechPurposeTypeIds = new ArrayList<String>();
			if (contactMechTypeId != null) {
				contactMechPurposeTypeIds = EntityUtil.getFieldListFromEntityList(
						delegator.findByAnd("ContactMechTypePurpose", UtilMisc.toMap("contactMechTypeId", contactMechTypeId), null, false), "contactMechPurposeTypeId", true);
			}
			if (UtilValidate.isNotEmpty(contactMechPurposeTypeIds)) {
				listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, contactMechPurposeTypeIds));
			}
			listIterator = delegator.findList("ContactMechPurposeType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
			if (listIterator != null) {
				for (GenericValue item : listIterator) {
					item.put("description", item.get("description", locale));
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListContactMechPurposeType service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	//for picklist
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetShippingAddressFullCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDateStoreRole", "thruDateStoreRole"));
			
			listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			
			Set<String> selectFields = new HashSet<String>();
			selectFields.add("contactMechId");
			selectFields.add("toName");
			selectFields.add("attnName");
			selectFields.add("address1");
			selectFields.add("address2");
			selectFields.add("city");
			selectFields.add("stateProvinceGeoId");
			selectFields.add("postalCode");
			selectFields.add("countryGeoId");
			selectFields.add("districtGeoId");
			selectFields.add("wardGeoId");
			selectFields.add("fullName");
			opts.setDistinct(true);
			
			listIterator = delegator.find("ProductStoreRoleAndPostalAddress", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingAddressFullCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetShippingAddressFullCustomer2(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDateStoreRole", "thruDateStoreRole"));
			
			listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			
			Set<String> selectFields = new HashSet<String>();
			selectFields.add("contactMechId");
			selectFields.add("toName");
			selectFields.add("attnName");
			selectFields.add("address1");
			selectFields.add("address2");
			selectFields.add("city");
			selectFields.add("stateProvinceGeoId");
			selectFields.add("postalCode");
			selectFields.add("countryGeoId");
			selectFields.add("districtGeoId");
			selectFields.add("wardGeoId");
			selectFields.add("fullName");
			selectFields.add("partyId");
			selectFields.add("partyCode");
			selectFields.add("partyName");
			opts.setDistinct(true);
			
			listIterator = delegator.find("ProductStoreRoleAndPostalAddress2", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingAddressFullCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
}
