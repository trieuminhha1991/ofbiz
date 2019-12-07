package org.ofbiz.mobileservices.geo;

import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class GeoServices {

	public static Map<String, Object> salesmanCheckIn(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        // check customer active
        Map<String, Object> check = MobileUtils.checkCustomerActive(delegator, locale, context.get("customerId").toString());
		if (check.get("responseMessage").equals("error")){
			return ServiceUtil.returnError(check.get("errorMessage").toString());
		}
		try {
			String message = "";
			Boolean checkInOk = false;
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Double latitude = Double.valueOf((String) context.get("latitude"));
			Double longitude = Double.valueOf((String) context.get("longitude"));
			Object customerId = context.get("customerId");

			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", customerId));
			conditions.add(EntityCondition.makeCondition("geoPointId", EntityJoinOperator.NOT_EQUAL, null));
			List<GenericValue> geos = delegator.findList("PartyCustomerAddressGeoPoint",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			Double customerLatitude = null;
			Double customerLongitude = null;
			Double distance = null;
			if (UtilValidate.isNotEmpty(geos)) {
				GenericValue geo = EntityUtil.getFirst(geos);
				customerLatitude = geo.getDouble("latitude");
				customerLongitude = geo.getDouble("longitude");
				distance = distance(latitude, customerLatitude, longitude, customerLongitude);
				Double acceptable = Double.valueOf(UtilProperties.getPropertyValue("geo.properties", "geo.distance.acceptable"));
                GenericValue sysConf = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "DistanceAcceptable"), true);
                if (sysConf != null) acceptable = Double.valueOf(sysConf.getString("systemValue"));
				if (UtilValidate.isNotEmpty(distance) && distance < acceptable) {
					checkInOk = true;
				} else {
					message = UtilProperties.getMessage("MobileServicesErrorUiLabels", "TheDistanceIsTooFar", locale) + " " + acceptable + " m";
				}
			}
			String checkInId = delegator.getNextSeqId("CheckInHistory");
			delegator.create("CheckInHistory",
					UtilMisc.toMap("checkInId", checkInId, "partyId", userLogin.get("partyId"), "latitude", latitude,
							"longitude", longitude, "customerId", customerId, "customerLatitude", customerLatitude,
							"customerLongitude", customerLongitude, "distance", distance, "checkInDate",
							UtilDateTime.nowTimestamp(), "checkInOk", checkInOk));
			if (checkInOk) {
			    message = UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCheckinSuccess", locale);
            } else if (message.equals("")) {
                message = UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesCheckinFalse", locale);
            }
			result.put("message", message);
			result.put("checkInOk", checkInOk);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> salesmanCheckOut(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> conditions = FastList.newInstance();
		try {
			String message = "";
			Boolean checkOutOk = false;
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginPartyId = userLogin.getString("partyId");
			Double latitude = Double.valueOf((String) context.get("latitude")); //current not using
			Double longitude = Double.valueOf((String) context.get("longitude")); //current not using
			String customerId = (String) context.get("customerId");

			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowTimestamp);
			calendar.add(Calendar.DATE, -3); //max is 3 days from today.
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Timestamp threeDaysAgo = UtilDateTime.getTimestamp(calendar.getTimeInMillis());

			conditions.add(EntityCondition.makeCondition("partyId", userLoginPartyId));
			conditions.add(EntityCondition.makeCondition("customerId", customerId));
			conditions.add(EntityCondition.makeCondition("checkInOk", true));
			conditions.add(EntityCondition.makeCondition("checkOutDate", null));
			conditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN, threeDaysAgo));

			List<GenericValue> checkInHistories = delegator.findList("CheckInHistory", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-checkInDate"), null, false);
			if (UtilValidate.isNotEmpty(checkInHistories)) {
				GenericValue checkInHistory = checkInHistories.get(0);
				checkInHistory.set("checkOutDate", nowTimestamp);
				checkInHistory.store();
				checkOutOk = true;
			}
			if (checkOutOk) {
				message = UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCheckOutSuccess", locale);
			} else {
				message = UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesCheckOutFalse", locale);
			}
			result.put("message", message);
			result.put("checkOutOk", checkOutOk);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Double distance(double lat1, double lat2, double lon1, double lon2) {
		Double distance = null;
		if (UtilValidate.isNotEmpty(lat1) && UtilValidate.isNotEmpty(lat2) && UtilValidate.isNotEmpty(lon1)
				&& UtilValidate.isNotEmpty(lon2)) {
			final int R = 6371; // Radius of the earth

			Double latDistance = Math.toRadians(lat2 - lat1);
			Double lonDistance = Math.toRadians(lon2 - lon1);
			Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
			Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			distance = R * c * 1000; // convert to meters

			distance = Math.pow(distance, 2);
		}
		return Math.sqrt(distance);
	}

	public static Map<String, Object> getTodayCheckedInCustomers(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<String> customers = FastList.newInstance();
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("checkInDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO,
					getStartOfDay()));
			conditions.add(
					EntityCondition.makeCondition("checkInDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, getEndOfDay()));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, context.get("partyId")));
			conditions.add(EntityCondition.makeCondition("checkInOk", EntityJoinOperator.EQUALS, true));
			List<GenericValue> checkInHistories = delegator.findList("CheckInHistory",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			customers = EntityUtil.getFieldListFromEntityList(checkInHistories, "customerId", true);
			result.put("customers", customers);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Timestamp getEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Timestamp getStartOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

    /*API New*/
    public static Map<String, Object> mGetCheckinStatus(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = userLogin.getString("partyId");
        successResult.put("checkInOk", false);
        if (!context.containsKey("customerId")) {
            return ServiceUtil.returnError("customerId null");
        }
        String customerId = (String)context.get("customerId");
        List<String> customers = FastList.newInstance();
        try {
            context.put("partyId",partyId);
            Map<String,Object> out = getTodayCheckedInCustomers(ctx,context);
            if (UtilValidate.isNotEmpty(out)) {
                customers = (List<String>)out.get("customers");
            }
            for (String customer : customers) {
                if (customer.equals(customerId)) {
                    successResult.put("checkInOk", true);
                    break;
                }
            }
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }
    /*end API New*/
}
