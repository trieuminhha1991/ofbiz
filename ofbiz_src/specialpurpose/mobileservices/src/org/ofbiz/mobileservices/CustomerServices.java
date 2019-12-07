package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.Mobile;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.mobileUtil.CustomerUtils;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class CustomerServices implements Mobile {

	public static final String module = CustomerServices.class.getName();

	public static Map<String,Object> getRouteDetail(DispatchContext dpct,Map<String, Object> context) {
		Delegator delegator = dpct.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String routeId = (String) context.get("routeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyIdTo = userLogin.getString("userLoginId");
		LocalDispatcher dispatcher = dpct.getDispatcher();
		try {
			Map<String, Object> results = FastMap.newInstance();
			GenericValue route = delegator.findOne("PartyAndPartyGroup", UtilMisc.toMap("partyId", routeId), false);
			results.putAll(route);
			List<Map<String, Object>> addresses = CustomerUtils.getRouteAddress(delegator, dispatcher, userLogin, routeId);
			results.put("addresses", addresses);
			List<GenericValue> schedules = CustomerUtils.getRouteSchedule(delegator, routeId);
			List<String> sch = EntityUtil.getFieldListFromEntityList(schedules, "scheduleRoute", true);
			List<GenericValue> employees = CustomerUtils.getRouteEmployee(delegator, routeId);
			//List<String> em = EntityUtil.getFieldListFromEntityList(employees, "partyFullName", true);
			List<String> em = EntityUtil.getFieldListFromEntityList(employees, "fullName", true);
			results.put("schedule", sch);
			results.put("employee", em);
			res.put("results", results);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	public static Map<String,Object> getAllRouteOwnedBy(DispatchContext dpct,Map<String, Object> context) {
		Delegator delegator = dpct.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if(UtilValidate.isEmpty(partyId)){
			partyId = userLogin.getString("partyId");
		}
		try {
			List<GenericValue> routes = CustomerUtils.getAllRoadEmployee(delegator, partyId);
			res.put("results", routes);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	public static Map<String,Object> createCustomerAgent(DispatchContext dpct,Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String statusId = (String) context.get("statusId");
		try {
			if(UtilValidate.isEmpty(statusId)){
				statusId = "PARTY_CREATED";
			}
			context.put("statusId", statusId);
			res = createCustomerAgentProcess(dpct, context);
			MobileUtils.sendNotifyManager(dpct, context, "BSNewAgentNotify", "CustomerRegistration", "");
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return res;
	}
	public static Map<String,Object> updateCustomerAgent(DispatchContext dpct,Map<String, Object> context) {
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String statusId = (String) context.get("statusId");
		try {
			if(UtilValidate.isEmpty(statusId)){
				statusId = "PARTY_UPDATED";
			}
			context.put("statusId", statusId);
			res = dispatcher.runSync("updateRequestNewCustomer", context);
			MobileUtils.sendNotifyManager(dpct, context, "BSNewAgentUpdatedNotify", "CustomerRegistration", "");
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return res;
	}
	public static Map<String,Object> createCustomerAgentProcess(DispatchContext dpct,Map<String, Object> context) {
		Map<String, Object> put = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = (String) userLogin.getString("userLoginId");
		String userLoginPartyId = (String) userLogin.getString("partyId");
		try {
            String latitudeStr = (String) context.get("latitude");
            String longitudeStr = (String) context.get("longitude");
            Double latitude = 0.0;
            Double longitude = 0.0;
            if(UtilValidate.isNotEmpty(latitudeStr)) {
                latitude = Double.parseDouble(latitudeStr);
            }
            if(UtilValidate.isNotEmpty(longitudeStr)) {
                longitude = Double.parseDouble(longitudeStr);
            }
            context.put("latitude", latitude);
            context.put("longitude", longitude);
            if (latitude.equals(0.0) || longitude.equals(0.0)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "MobileServicesLatLonIsNotFound", locale));
            }
			String customerId = delegator.getNextSeqId("TemporaryParty");
			GenericValue pa = delegator.makeValidValue("TemporaryParty", context);
			String stateProvinceGeoName = (String) context.get("stateProvinceGeoId");
			String districtGeoName = (String) context.get("districtGeoId");
			String wardGeoName = (String) context.get("wardGeoId");
			String countryGeoName = (String) context.get("countryGeoId");
			String routeId = (String) context.get("routeId");
            String stateProvinceGeoId = "";
            if (stateProvinceGeoName == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "MobileServicesStateProvinceIsNotFound", locale));
            }
			if(UtilValidate.isNotEmpty(stateProvinceGeoName)){
                pa.set("stateProvinceGeoName", stateProvinceGeoName);
				stateProvinceGeoId = MobileUtils.getGeoId(delegator, stateProvinceGeoName, "PROVINCE");
				pa.set("stateProvinceGeoId", stateProvinceGeoId);
			}
			if(UtilValidate.isNotEmpty(districtGeoName)){
                pa.set("districtGeoName", districtGeoName);
				String districtGeoId = MobileUtils.getGeoId(delegator, districtGeoName, "DISTRICT");
				pa.set("districtGeoId", districtGeoId);
			}
			if(UtilValidate.isNotEmpty(countryGeoName)){
                pa.set("countryGeoName", countryGeoName);
				String countryGeoId = MobileUtils.getGeoId(delegator, countryGeoName, "COUNTRY");
				if (countryGeoId == null && !"".equals(stateProvinceGeoId)) {
                    List<GenericValue> geoAssocs = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoIdTo", stateProvinceGeoId, "geoAssocTypeId", "REGIONS"), null, false);
                    if (UtilValidate.isNotEmpty(geoAssocs)) {
                        countryGeoId = geoAssocs.get(0).getString("geoId");
                    } else {
                        countryGeoId = "VNM"; //TODO fix me
                    }
                }
				pa.set("countryGeoId", countryGeoId);
			}
			if(UtilValidate.isNotEmpty(wardGeoName)){
                pa.set("wardGeoName", wardGeoName);
				String wardGeoId = MobileUtils.getGeoId(delegator, wardGeoName, "WARD");
				pa.set("wardGeoId", wardGeoId);
			}
			if (UtilValidate.isEmpty(pa.getString("partyTypeId"))) {
				if (UtilValidate.isNotEmpty(pa.get("productStoreId"))) {
					GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", pa.get("productStoreId")), false);
					String salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
					List<GenericValue> partyTypeEnumAssoc = delegator.findList("PartyTypeEnumAssoc", EntityCondition.makeCondition(
							EntityCondition.makeCondition("enumId", salesMethodChannelEnumId), EntityOperator.AND,
							EntityUtil.getFilterByDateExpr()), null, null, null, false);
					if (partyTypeEnumAssoc != null && partyTypeEnumAssoc.size() == 1) {
						pa.set("partyTypeId", partyTypeEnumAssoc.get(0).getString("partyTypeId"));
					}
				}
			}
			pa.set("createdDate", UtilDateTime.nowTimestamp());
			pa.set("routeId", routeId);
			pa.set("customerId", customerId);
			pa.set("createdByUserLogin", userLoginId);
			pa.set("salesmanId", userLoginPartyId);
			pa.set("lastUpdatedByUserLogin", userLoginId);
			pa.create();

		}catch(GenericEntityException e){
			e.printStackTrace();
		} catch (Exception e) {
			Debug.logError("Can't create or update for this Customer" +e.getMessage(),module);
			e.printStackTrace();
		}
		return put;
	}
	
	public static Map<String,Object> deleteCustomerAgentProcess(DispatchContext dpct,Map<String, Object> context) {
		Map<String, Object> put = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String customerId = (String) context.get("customerId");
		try {
			GenericValue pa = delegator.findOne("TemporaryParty", UtilMisc.toMap("customerId", customerId), false);
			if(UtilValidate.isNotEmpty(pa)){
				pa.remove();
			}
		}catch (Exception e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return put;
	}
	public static Map<String,Object> updateLocationCustomer(DispatchContext dpct,Map<String,? extends Object> context) throws GenericTransactionException{
		Delegator delegator = dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String customerId = (String) context.get("customerId");
		Double latitude = Double.parseDouble((String) context.get("latitude"));
		Double longitude  =  Double.parseDouble((String) context.get("longitude"));
		String country =  (String) context.get("countryGeoId");
		String city =  (String) context.get("stateProvinceGeoId");
		String district  =  (String) context.get("districtGeoId");
		String address  =  (String) context.get("address");
		Boolean isClearOld  =  (Boolean) context.get("isClearOld");
		String roleTypeId  =  (String) context.get("roleTypeId");
		Map<String,Object> Notifcation = FastMap.newInstance();
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			String geoPointId = null;
			String contactMechId = null;
			geoPointId = MobileUtils.createGeoPoint(delegator, latitude, longitude);
			String countryGeoId = MobileUtils.getGeoId(delegator, country, "COUNTRY");
			String stateProvinceGeoId = MobileUtils.getGeoId(delegator, city, "PROVINCE");
			String districtGeoId = MobileUtils.getGeoId(delegator, district, "DISTRICT");
			if(UtilValidate.isEmpty(isClearOld)){
				isClearOld = true;
			}
			if(UtilValidate.isNotEmpty(roleTypeId)){
				MobileUtils.createPartyPostalAddress(delegator, dispatcher, customerId, contactMechId, countryGeoId, stateProvinceGeoId, city, districtGeoId, geoPointId, address, roleTypeId, isClearOld);
			}else{
				MobileUtils.createPartyPostalAddress(delegator, dispatcher, customerId, contactMechId, countryGeoId, stateProvinceGeoId, city, districtGeoId, geoPointId, address, isClearOld);
			}

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Could not get list party Geo Points" +e.getMessage() , module);
			e.printStackTrace();
			Notifcation.put("status", "error");
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		}finally {
			TransactionUtil.commit(beganTx);
		}

		Notifcation.put("status", "success");
		return Notifcation;
	}

	@SuppressWarnings("unchecked")
    public static Map<String,Object> getStoreByRoadNew(DispatchContext dpct,Map<String,Object> context) throws GenericEntityException {
        Delegator delegator = dpct.getDelegator();
        LocalDispatcher dispatcher = dpct.getDispatcher();
		String customerByStoreIdStr = (String) context.get("customerByStoreIds");
        String routeIdStr = (String) context.get("routeIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = userLogin.getString("partyId");
        String other = (String) context.get("other");
        List<EntityCondition> allConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
        Map<String, Object> rs = FastMap.newInstance();
		List<GenericValue> customers;
		Map<String, String[]> parameters = FastMap.newInstance();
		String pagenum = context.containsKey("page") ? (String) context.get("page") : "0";
		String pagesize = context.containsKey("size") ? (String) context.get("size") : "200";
		parameters.put("pagenum", new String[]{pagenum});
		parameters.put("pagesize", new String[]{pagesize});
		int totalRows = 0;
        try {
        	List<String> customerByStoreIds = FastList.newInstance();
			if (UtilValidate.isNotEmpty(customerByStoreIdStr)) {
				String[] customerIdArr = customerByStoreIdStr.replaceAll("[^\\w+,]","").split(",");
				for (String str: customerIdArr) {
					customerByStoreIds.add(str);
				}
			}

            Date todayDate = new Date(System.currentTimeMillis());
            String scheduleRoute = CustomerUtils.getTodayScheduleRoute();

            allConditions.add(EntityCondition.makeCondition("executorId", partyId));
            if (UtilValidate.isEmpty(other) || other.equals("N")) {
                allConditions.add(EntityCondition.makeCondition("scheduleRoute", scheduleRoute));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(UtilDateTime.nowTimestamp());
                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                if (weekOfYear < 10) {
                    allConditions.add(EntityCondition.makeCondition("weeks", EntityOperator.LIKE, "%0"+ String.valueOf(weekOfYear) +'%'));
                } else {
                    allConditions.add(EntityCondition.makeCondition("weeks", EntityOperator.LIKE, '%'+ String.valueOf(weekOfYear) +'%'));
                }
            } else {
                allConditions.add(EntityCondition.makeCondition("scheduleRoute", EntityOperator.NOT_EQUAL, scheduleRoute));
                if (UtilValidate.isNotEmpty(routeIdStr)) {
                    String[] routeIdArr = routeIdStr.replaceAll("[^\\w+,]","").split(",");
                    List<String> routeIdList = FastList.newInstance();
                    for (String str: routeIdArr) {
                        routeIdList.add(str);
                    }
                    allConditions.add(EntityCondition.makeCondition("routeId", EntityOperator.NOT_IN, routeIdList));
                }
            }
            List<GenericValue> salesRouteSchedules = delegator.findList("RouteAndSalesRouteSchedule", EntityCondition.makeCondition(allConditions),
                    null, null, null, false);
            List<String> routeIds = EntityUtil.getFieldListFromEntityList(salesRouteSchedules,"routeId",true);
            allConditions.clear();
            allConditions.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routeIds));
            allConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerByStoreIds));
            List<GenericValue> availableRouteCustomers = delegator.findList("RouteCustomerAvailable", EntityCondition.makeCondition(allConditions),
                    null, null, null, false);
            List<String> customerIds = EntityUtil.getFieldListFromEntityList(availableRouteCustomers,"customerId",true);

            //get checkInHistory
            List<GenericValue> checkInHistories = FastList.newInstance();
            HashMap<String, Long> checkInHistoriesMap = new HashMap<String, Long>();
            HashMap<String, String> statusMap = new HashMap<String, String>();
            String customerIdTmp = "";
            Long todayAtOh = UtilDateTime.getDayStart(new Timestamp(todayDate.getTime())).getTime();
            Long checkInTime = 0L;
            Long subTime = 0L;
            BigDecimal numberOfDays = BigDecimal.ZERO;
            allConditions.clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(todayDate);
            calendar.add(Calendar.MONTH , - 3); //max 3 months from today

            allConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerIds));
            allConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.NOT_EQUAL, null));
            allConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(calendar.getTimeInMillis())));
            checkInHistories = delegator.findList("CheckInHistory", EntityCondition.makeCondition(allConditions), null,UtilMisc.toList("-checkInDate"),null,false);

            for (GenericValue checkInHistory: checkInHistories) {
                customerIdTmp = checkInHistory.getString("customerId");
                Boolean checkInOk = checkInHistory.getBoolean("checkInOk");
                if (!checkInHistoriesMap.containsKey(customerIdTmp)) {
                    checkInTime = checkInHistory.getTimestamp("checkInDate").getTime();
                    subTime = todayAtOh - checkInTime;
                    if (subTime <= 0 && checkInOk) {
                        checkInHistoriesMap.put(customerIdTmp, 0L);
                    } else {
                        numberOfDays = BigDecimal.valueOf(subTime/86400000);
                        numberOfDays.setScale(0, BigDecimal.ROUND_CEILING);
                        checkInHistoriesMap.put(customerIdTmp, numberOfDays.longValue());
                    }
                }
            }

			allConditions.clear();
			checkInHistories.clear();
			allConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerIds));
			allConditions.add(EntityCondition.makeCondition("partyId", partyId));
			allConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
			checkInHistories = delegator.findList("CheckInHistory", EntityCondition.makeCondition(allConditions), null,UtilMisc.toList("-checkOutDate"),null,false);
			for (String customerId : customerIds) {
				statusMap.put(customerId, "NOT_VISITED");
			}
			for (GenericValue checkInHistory: checkInHistories) {
				String customerId = checkInHistory.getString("customerId");
				Boolean checkInOk = checkInHistory.getBoolean("checkInOk");
				Timestamp checkInDate = checkInHistory.getTimestamp("checkInDate");
				Timestamp checkOutDate = checkInHistory.getTimestamp("checkOutDate");
				if (UtilValidate.isNotEmpty(checkInDate) && checkInOk) {
					if (UtilValidate.isNotEmpty(checkOutDate)) {
						statusMap.put(customerId, "VISITED");
					} else {
						statusMap.put(customerId, "VISITING");
					}
				}
			}
            //end get checkInHistory
            rs.put("routes", routeIds);

            allConditions.clear();
            allConditions.add(EntityCondition.makeCondition("routeId", EntityOperator.IN,routeIds));
            allConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
            allConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
            //allConditions.add(EntityCondition.makeCondition("partyTypeId", "RETAIL_OUTLET")); //can be modified
            allConditions.add(EntityUtil.getFilterByDateExpr());

            EntityCondition queryConditionsList = EntityCondition
                    .makeCondition(allConditions, EntityOperator.AND);
            EntityFindOptions options = new EntityFindOptions();
            options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
            options.setDistinct(true);
            Set<String> fieldsToSelect = UtilMisc.toSet("partyId",
                    "fullName", "latitude", "longitude", "postalAddressName",
                    "city");
            fieldsToSelect.add("logoImageUrl");
            fieldsToSelect.add("phoneNumber");
			listSortFields.add("fullName");
			customers = EntityMiscUtil.processIteratorToList(parameters, rs, delegator, "CustomerAndPartyGroupAndGeoPoint",
					queryConditionsList, null, fieldsToSelect, listSortFields, options);
			if (UtilValidate.isNotEmpty(rs.get("TotalRows"))) {
				totalRows = Integer.parseInt((String) rs.get("TotalRows"));
				rs.remove("TotalRows");
			}
            List<Map<String, Object>> customerres = FastList.newInstance();
            for (GenericValue cus : customers) {
                Map<String, Object> o = FastMap.newInstance();
                Map<String, Object> phoneNumber = FastMap.newInstance();
                o.put("partyIdTo", cus.get("partyId"));
                o.put("groupName", cus.get("fullName"));
                o.put("latitude", cus.get("latitude"));
                o.put("longitude", cus.get("longitude"));
                o.put("address1", cus.get("postalAddressName"));
                o.put("city", cus.get("city"));
                o.put("logoImageUrl", cus.get("logoImageUrl"));
                String customerId = cus.getString("partyId");
                if (UtilValidate.isNotEmpty(cus.get("phoneNumber"))) {
                    phoneNumber.put("contactNumber", cus.get("phoneNumber"));
                } else {
                    phoneNumber.put("contactNumber", "");
                }
                o.put("phoneNumber", phoneNumber);
				o.put("status", statusMap.get(customerId));
                if (checkInHistoriesMap.containsKey(customerId)) {
                    o.put("missedDays", checkInHistoriesMap.get(customerId));
                } else {
                    o.put("missedDays", -1);
                }
                customerres.add(o);
            }
            rs.put("total", totalRows);
            rs.put("customers", customerres);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return rs;
    }

	public static Map<String,Object> getStore(DispatchContext dpct,Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = dpct.getDelegator();
		String routeId = (String) context.get("routeId");
		String storeId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		List<EntityCondition> allConditions = FastList.newInstance();
		Map<String,Object> rs = FastMap.newInstance();
		EntityListIterator list = null;
		try{
			if(UtilValidate.isEmpty(routeId)){
				List<String> stores = MobileUtils.getRoadToday(delegator, partyId);
				List<String> listStores = FastList.newInstance();
				if(UtilValidate.isNotEmpty(stores)){
                    listStores = stores;
					allConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listStores));
				}
			}else{
				allConditions.add(EntityCondition.makeCondition("partyIdFrom", routeId));
			}

			allConditions.add(EntityCondition.makeCondition("partyIdTo", storeId));
			allConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "ROUTE"));
			allConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER"));
			allConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
			allConditions.add(EntityUtil.getFilterByDateExpr("relationFromDate","relationThruDate"));
			allConditions.add(EntityUtil.getFilterByDateExpr("ctmFromDate","ctmThruDate"));

			EntityCondition queryConditionsList = EntityCondition
					.makeCondition(allConditions, EntityOperator.AND);
			EntityFindOptions options = new EntityFindOptions();
			options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			Set<String> fieldsToSelect =  UtilMisc.toSet("partyIdTo",
					"groupName", "latitude", "longitude", "address1",
					"city");
			fieldsToSelect.add("partyIdFrom");
			list = delegator.find("PartyGroupGeoView", queryConditionsList, null, fieldsToSelect, UtilMisc.toList("groupName ASC"), options);
			List<GenericValue> customers = list.getPartialList(0, 1);
			if(UtilValidate.isNotEmpty(customers)){
				GenericValue e = customers.get(0);
				Map<String, Object> o = UtilMisc.toMap("partyIdTo", e.getString("partyIdTo"), "groupName",  e.getString("groupName"),
														"latitude",  e.getString("latitude"), "longitude", e.getString("longitude"),
														"address1", e.getString("address1"));
				o.put("city", e.getString("city"));
				rs.put("customer", o);
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			list.close();
		}
		return rs;
	}
	public static Map<String, Object> getOpponents(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> listOpponent = delegator.findList("PartyRoleAndPartyDetail",
															EntityCondition.makeCondition(
																	UtilMisc.toList(
																		EntityCondition.makeCondition("partyTypeId", "PARTY_GROUP"),
																		EntityCondition.makeCondition("roleTypeId", "COM_SUPPLIER"))), UtilMisc.toSet("partyId", "groupName"), UtilMisc.toList("groupName"), null, false);
			res.put("listOpponent", listOpponent);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	/*
	 * @param DispathContext dpct,context
	 * submitFbCustomer
	 *
	 * */
	public static Map<String,Object> submitFbCustomer(DispatchContext dpct,Map<String, Object> context){
		Delegator delegator = dpct.getDelegator();
		String partyId = (String) context.get("customerId");
		String comment = (String) context.get("comment");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyIdTo = userLogin.getString("partyId");
		Map<String,Object> status = ServiceUtil.returnSuccess();
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		try {
			String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
			GenericValue com  = delegator.makeValue("CommunicationEvent");
			com.set("communicationEventId", communicationEventId);
			com.set("partyIdFrom", partyId);
			com.set("partyIdTo", partyIdTo);
			com.set("content",comment);
			com.set("entryDate", nowtimestamp);
			com.set("communicationEventTypeId", "FACE_TO_FACE_COMMUNI");
			com.set("statusId", "COM_COMPLETE");
			com.create();
			status.put("communicationEventId", communicationEventId);
			MobileUtils.sendMarketingManagerNotify(dpct, context, "MKNewCustomerFeedback", "Feedbacks", null);
		} catch (Exception e) {
			Debug.logError("can't not create Communication Event for Customer" + e.getMessage(), module);
		}
		return status;
	}

	/*
	 * @param DispatchContext dpct,context
	 * submitInfoOpponent
	 *
	 * */
	public static Map<String,Object> submitInfoOpponent(DispatchContext dpct,Map<String, Object> context){
		Delegator delegator = dpct.getDelegator();
		String partyId = (String) context.get("partyId");
		String comment = (String) context.get("comment");
		String description = (String) context.get("description");
		String image = (String) context.get("image");
		Map<String,Object> status = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue opp = delegator.makeValue("OpponentEvent");
			String opponentEventId = delegator.getNextSeqId("OpponentEvent");
			opp.set("opponentEventId", opponentEventId);
			opp.set("partyId", partyId);
			opp.set("comment", comment);
			opp.set("description", description);
			opp.set("image", image);
			opp.set("createdByPartyId", userLogin.get("partyId"));
			opp.create();
			status.put("opponentEventId", opponentEventId);
			MobileUtils.sendMarketingManagerNotify(dpct, context, "MKNewOpponentInfo", "Competitors", null);
		} catch (Exception e) {
			Debug.logError("can't not create Infomartion of Opponent in DB" + e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		};

		return status;
	}
	public static Map<String, Object> createGeoPointDynamic(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		Double latitude = (Double) context.get("latitude");
		Double longitude = (Double) context.get("longitude");
		try {
			String geoPointId = MobileUtils.createGeoPoint(delegator, latitude, longitude);
			res.put("geoPointId", geoPointId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> createOpponent(DispatchContext dpc, Map<String, Object> context){
		Delegator delegator = dpc.getDelegator();
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try {
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			context.put("userLogin", userLogin);
			Map<String, Object> out = dispatcher.runSync("createRival", context);
			res.putAll(out);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	/*API New*/
    public static Map<String, Object> mGetCustomerRegistration(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<String> listSortFields = FastList.newInstance();
        EntityFindOptions opts = new EntityFindOptions();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        Map<String,String[]> parameters = FastMap.newInstance();
        Locale locale = (Locale)context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        String pagenum = context.containsKey("pagenum") ? (String) context.get("pagenum") : "0";
        String pagesize = context.containsKey("pagesize") ? (String) context.get("pagesize") : "200";
        parameters.put("pagenum", new String[]{pagenum});
        parameters.put("pagesize", new String[]{pagesize});
        List<GenericValue> customers = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        fieldsToSelect.add("customerId");
        fieldsToSelect.add("customerName");
        fieldsToSelect.add("birthDay");
        fieldsToSelect.add("officeSiteName");
        fieldsToSelect.add("phone");
        fieldsToSelect.add("startDate");
        fieldsToSelect.add("productStoreId");
        fieldsToSelect.add("statusId");
        fieldsToSelect.add("city");
        fieldsToSelect.add("gender");
        fieldsToSelect.add("latitude");
        fieldsToSelect.add("longitude");
        fieldsToSelect.add("note");
        fieldsToSelect.add("url");
        fieldsToSelect.add("address");
        fieldsToSelect.add("routeId");
        fieldsToSelect.add("salesmanId");
        try {
            listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin",userLoginId));
            customers = EntityMiscUtil.processIteratorToList(parameters,successResult,delegator,"TemporaryParty",
                    EntityCondition.makeCondition(listAllConditions),null,UtilMisc.toSet(fieldsToSelect),listSortFields,opts);
            successResult.put("customers", customers);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> mSubmitFeedback(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        String partyIdTo = userLogin.getString("partyId");
        Locale locale = (Locale)context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Map<String, Object> errorResult = ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesFeedbackNotSubmitted", locale));
        try {
            if (!context.containsKey("customerId")) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCustomerNotFound", locale));
            }
            String customerId = (String) context.get("customerId");
            if (!context.containsKey("comment")) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesFeedbackInvalid", locale));
            }
            String comment = (String) context.get("comment");
            Timestamp nowtimestamp = UtilDateTime.nowTimestamp();

            String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
            GenericValue com  = delegator.makeValue("CommunicationEvent");
            com.set("communicationEventId", communicationEventId);
            com.set("partyIdFrom", customerId);
            com.set("partyIdTo", partyIdTo);
            com.set("content",comment);
            com.set("entryDate", nowtimestamp);
            com.set("communicationEventTypeId", "FACE_TO_FACE_COMMUNI");
            com.set("statusId", "COM_COMPLETE");
            com.create();
            MobileUtils.sendMarketingManagerNotify(ctx, context, "MKNewCustomerFeedback", "Feedbacks", null);
            successResult.put("message", UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesFeedbackSuccessfullySubmitted", locale));
            successResult.put("feedbackOk", true);
        } catch (Exception e) {
            successResult.put("message", UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesFeedbackNotSubmitted", locale));
            successResult.put("feedbackOk", false);
            return errorResult;
        }
        return successResult;
    }

    public static Map<String, Object> mCreateOpponent(DispatchContext dpc, Map<String, Object> context){
        Delegator delegator = dpc.getDelegator();
        LocalDispatcher dispatcher = dpc.getDispatcher();
        Locale locale = (Locale)context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Map<String, Object> errorResult = ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesOpponentNotCreated", locale));
        try {
            GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
            context.put("userLogin", userLogin);
            Map<String, Object> out = dispatcher.runSync("createRival", context);
            if (UtilValidate.isNotEmpty(out)) {
                successResult.put("message", UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesOpponentSuccessfullyCreated", locale));
                successResult.put("createOk", true);
            } else {
                successResult.put("message", ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesOpponentNotCreated", locale)));
                successResult.put("createOk", false);
            }
        } catch (Exception e) {
            errorResult.put("message", ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesOpponentNotCreated", locale)));
            errorResult.put("feedbackOk", false);
            return errorResult;
        }
        return successResult;
    }

    public static Map<String, Object> mGetOpponents(DispatchContext dctx, Map<String, Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            List<GenericValue> listOpponent = delegator.findList("PartyRoleAndPartyDetail",
                    EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyTypeId", "PARTY_GROUP"),
                            EntityCondition.makeCondition("roleTypeId", "COM_SUPPLIER"))),
                    UtilMisc.toSet("partyId", "groupName"), UtilMisc.toList("groupName"), null, false);
            successResult.put("listOpponent", listOpponent);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String,Object> mSubmitInfoOpponent(DispatchContext dpct,Map<String, Object> context){
        Delegator delegator = dpct.getDelegator();
        String partyId = (String) context.get("partyId");
        String comment = (String) context.get("comment");
        String description = (String) context.get("description");
        String image = (String) context.get("image");
        Locale locale = (Locale) context.get("locale");
        Map<String,Object> successResult = ServiceUtil.returnSuccess();
        Map<String, Object> errorResult = ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesFeedbackNotSubmitted", locale));
        try {
            GenericValue opp = delegator.makeValue("OpponentEvent");
            String opponentEventId = delegator.getNextSeqId("OpponentEvent");
            opp.set("opponentEventId", opponentEventId);
            opp.set("partyId", partyId);
            opp.set("comment", comment);
            opp.set("description", description);
            opp.set("image", image);
            opp.create();

            successResult.put("message", UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesFeedbackSuccessfullySubmitted", locale));
            successResult.put("submitOk", true);

            MobileUtils.sendMarketingManagerNotify(dpct, context, "MKNewOpponentInfo", "Competitors", null);
        } catch (Exception e) {
            errorResult.put("message", ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesFeedbackNotSubmitted", locale)));
            errorResult.put("submitOk", false);
            return errorResult;
        };
        return successResult;
    }
    /*end API New*/
}
