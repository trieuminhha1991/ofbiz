package org.ofbiz.mobilecustomer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


import javolution.util.FastMap;
import javolution.util.FastList;

public class MobileCustomer {
	public static final String resourceError = "MobileCustomerUiLabels";
	@Deprecated
	public static Map<String, Object> getProductStoreCustomer(DispatchContext dtx, Map<String, Object> ctx) {
		Map<String, Object> result = null;
		LocalDispatcher dispatcher = dtx.getDispatcher();
		GenericValue userLogin = (GenericValue) ctx.get("userLogin");
		Map<String, Object> in = FastMap.newInstance();
		in.put("userLogin", userLogin);
		in.put("isCustomerApp", "Y");
		try {
			result = dispatcher.runSync("getProductStore", in);
		} catch (GenericServiceException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> registerCustomerAccount(DispatchContext dtx, Map<String, Object> ctx) {
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = dtx.getDelegator();
		Locale locale = (Locale) ctx.get("locale");
		String username = (String) ctx.get("username");
		String password = (String) ctx.get("password");
		String passwordRepeat = (String) ctx.get("passwordRepeat");
		String fullName = (String) ctx.get("fullName");
		Long birthday = (Long) ctx.get("birthday");
		String gender = (String) ctx.get("gender");
		String phoneNumber = (String) ctx.get("phoneNumber");
		String email = (String) ctx.get("email");
		String address = (String) ctx.get("address");
		String description = (String) ctx.get("description");
		String customerId = delegator.getNextSeqId("TemporaryParty");
		String webappName = dtx.getName();
		java.sql.Date birthDay = new java.sql.Date(birthday);
		if (!password.equals(passwordRepeat))
			return ServiceUtil
					.returnError(UtilProperties.getMessage(resourceError, "PasswordVerificationFailed", locale));
		try {
			List<GenericValue> checkUserLogin = delegator.findList("TemporaryParty",
					EntityCondition.makeCondition("userLoginId", username), null, null, null, false);
			if (!checkUserLogin.isEmpty())
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "AccountExists", locale));
			GenericValue pa = delegator.makeValue("TemporaryParty");
			pa.set("createdDate", UtilDateTime.nowTimestamp());
			pa.set("customerId", customerId);
			pa.set("customerName", fullName);
			pa.set("gender", gender);
			pa.set("phone", phoneNumber);
			pa.set("birthDay", birthDay);
			pa.set("address", address);
			pa.set("note", description);
			pa.set("emailAddress", email);
			pa.set("userLoginId", username);
			pa.set("currentPassword", HashCrypt.cryptUTF8(getHashType(), null, password));
			pa.set("webappName", webappName);
			pa.set("statusId", "PARTY_CREATED");
			pa.create();
		} catch (Exception ex) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "ErrorCreateAccout", locale));
		}
		success.put("customerId", customerId);
		return success;
	}
	
	public static String getHashType() {
		String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

		if (UtilValidate.isEmpty(hashType)) {
			hashType = "SHA";
		}

		return hashType;
	}
	
	public static Map<String, Object> mGetAssociatedStateWithCountry (DispatchContext dtx, Map<String, Object> ctx) {
		Delegator delegator = dtx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		try {
			listIterator = MobileCustomer.getCountryList(delegator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListCountryGeo service: " + e.toString();
			Debug.logError(e, errMsg, "");
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> mGetAssociatedStateOtherListGeo(DispatchContext dtx, Map<String, Object> ctx) {
		Delegator delegator = dtx.getDelegator();
		// LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String geoId = (String) ctx.get("geoId");
		Locale locale = (Locale) ctx.get("locale");
		try {
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
				listIterator = delegator.findList("GeoAssocAndGeoTo",
						EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, true);
			}
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		if (listIterator == null) {
			listIterator = new ArrayList<GenericValue>();
		}
		if (listIterator.size() <= 0) {
			GenericValue geoAssocAndGeoTo = delegator.makeValue("GeoAssocAndGeoTo");
			geoAssocAndGeoTo.put("geoId", "_NA_");
			geoAssocAndGeoTo.put("geoName", UtilProperties.getMessage(resourceError, "BSNoAddressExists", locale));
			listIterator.add(geoAssocAndGeoTo);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static List<GenericValue> getCountryList(Delegator delegator) {
        List<GenericValue> geoList = FastList.newInstance();
        String defaultCountry = EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", delegator);
        GenericValue defaultGeo = null;
        if (UtilValidate.isNotEmpty(defaultCountry)) {
            try {
                defaultGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", defaultCountry), true);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot lookup Geo", "");
            }
        }

        List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "COUNTRY"));
        List<String> countriesAvailable = StringUtil.split(UtilProperties.getPropertyValue("general.properties", "countries.geo.id.available"), ",");
        if (UtilValidate.isNotEmpty(countriesAvailable)) {
            // only available countries (we don't verify the list of geoId in countries.geo.id.available)
            exprs.add(EntityCondition.makeCondition("geoId", EntityOperator.IN, countriesAvailable));
        }

        List<GenericValue> countriesList = FastList.newInstance();
        try {
            countriesList = delegator.findList("Geo", EntityCondition.makeCondition(exprs), null, UtilMisc.toList("geoName"), null, true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot lookup Geo", "");
        }
        if (defaultGeo != null) {
            geoList.add(defaultGeo);
            boolean removeDefaultGeo = UtilValidate.isEmpty(countriesList);
            if (!removeDefaultGeo) {
                for (GenericValue country  : countriesList) {
                    if (country.get("geoId").equals(defaultGeo.get("geoId"))) {
                        removeDefaultGeo = true;
                    }
                }
            }
            if (removeDefaultGeo) {
                geoList.remove(0); // Remove default country to avoid double rows in drop-down, from 1st place to keep alphabetical order
            }
            geoList.addAll(countriesList);
        } else {
            geoList = countriesList;
        }
        return geoList;
    }
}
