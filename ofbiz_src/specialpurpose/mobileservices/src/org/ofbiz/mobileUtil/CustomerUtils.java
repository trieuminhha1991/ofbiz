package org.ofbiz.mobileUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.mobileservices.CustomerServices;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import java.sql.Timestamp;

public class CustomerUtils {
	public static final String module = CustomerUtils.class.getName();
	public static List<GenericValue> getAllRoadEmployee(Delegator delegator, String partyId) throws GenericEntityException{
		List<EntityCondition> listCond = FastList.newInstance();
		/*
		listCond.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS,partyId));
		listCond.add(EntityCondition.makeCondition("statusId",EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
		listCond.add(EntityUtil.getFilterByDateExpr("SRFromDate", "SRThruDate"));
		List<GenericValue> listRel = null;
		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			Set<String> fields = UtilMisc.toSet("partyId", "partyCode", "groupName", "routeId", "description", "SRFromDate");
			fields.add("RoutePartyCode");
			listRel = delegator.findList("PartyRelationshipRouteDetail",
					EntityCondition.makeCondition(listCond,EntityOperator.AND), fields, UtilMisc.toList("-SRFromDate"), options, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		*/
        listCond.add(EntityCondition.makeCondition("executorId",partyId));
        listCond.add(EntityCondition.makeCondition("statusId","ROUTE_ENABLED")); //need change to SupUtil.ROUTE_ENABLED
        List<GenericValue> routes = delegator.findList("Route", EntityCondition.makeCondition(listCond),null,
                null, null,false);
		return routes;
	}
	public static boolean checkCustomerHasOrder(Delegator delegator, String partyId, String createdBy) throws GenericEntityException{
		Timestamp entryDateStartDay = getStartDateTimestamp();
		Timestamp entryDateEndDay = getEndDateTimestamp();
		EntityListIterator list = delegator.find("OrderHeaderAndRoleType",
				EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeId", "CUSTOMER"),
						EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"),
						EntityCondition.makeCondition("partyId", partyId),
						EntityCondition.makeCondition("createdBy", createdBy),
						EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDateStartDay),
						EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, entryDateEndDay)
						)), null, null, null, null);
		int size = list.getResultsTotalSize();
		list.close();
		if(size != 0){
			return true;
		}
		return false;
	}
	public static boolean checkCusomerRegisterPromos(Delegator delegator, String partyId){
		EntityListIterator list = null;
		boolean flag = false;
		try {
			list = delegator.find("ProductPromoExtRegister",
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"),
							EntityUtil.getFilterByDateExpr())), null, null, null, null);
			int size = list.getResultsTotalSize();
			list.close();
			if(size != 0){
				flag = true;
			}
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
		} finally {
			if(list != null){
				try {
					list.close();
				} catch (GenericEntityException e) {
					Debug.logWarning(e.getMessage(), module);
				}
			}
		}
		return flag;
	}
	public static boolean checkCusomerEvaluated(Delegator delegator, String partyId){
		EntityListIterator list = null;
		boolean flag = false;
		Timestamp entryDateStartDay = getStartDateTimestamp();
		try {
			list = delegator.find("ProductPromoExtRegistrationEval",
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDateStartDay))), null, null, null, null);
			int size = list.getResultsTotalSize();
			list.close();
			if(size != 0){
				flag = true;
			}
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
		} finally {
			if(list != null){
				try {
					list.close();
				} catch (GenericEntityException e) {
					Debug.logWarning(e.getMessage(), module);
				}
			}
		}
		return flag;
	}
	public static boolean checkCustomerCheckedInventory(Delegator delegator, String userLoginPartyId, String partyId){
		EntityListIterator list = null;
		boolean flag = false;
		try {
			Timestamp cur = getStartDateTimestamp();
			list = delegator.find("CustomerProductInventory",
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, cur),
							EntityCondition.makeCondition("createdBy", userLoginPartyId))
						), null, null, null, null);
			int size = list.getResultsTotalSize();
			list.close();
			if(size != 0){
				flag = true;
			}
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
		} finally {
			if(list != null){
				try {
					list.close();
				} catch (GenericEntityException e) {
					Debug.logWarning(e.getMessage(), module);
				}
			}
		}
		return flag;
	}
	public static List<Map<String, Object>> getRouteAddress(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String partyId) throws GenericServiceException{
		Map<String, Object> input = FastMap.newInstance();
		input.put("userLogin", userLogin);
		input.put("partyId", partyId);
		Map<String, Object> outsup = dispatcher.runSync("jqxGetAddressFamily", input);
		List<Map<String, Object>> listAddress = (List<Map<String, Object>>) outsup.get("listAddress");
		return listAddress;
	}
	public static List<GenericValue> getRouteSchedule(Delegator delegator, String partyId) throws GenericEntityException{
		List<GenericValue> sch = delegator.findList("SalesRouteSchedule", EntityCondition.makeCondition(
				UtilMisc.toList(EntityCondition.makeCondition("routeId", partyId),
						EntityUtil.getFilterByDateExpr())), null, UtilMisc.toList("scheduleRoute"), null, false);
		return sch;
	}

	public static List<GenericValue> getRouteEmployee(Delegator delegator, String partyId) throws GenericEntityException{
        List<GenericValue> listResult = null;
        GenericValue route = delegator.findOne("Route",UtilMisc.toMap("routeId",partyId,"statusId", "ROUTE_ENABLED"),false);
        if (UtilValidate.isEmpty(route)) {
            return listResult;
        } else {
            listResult = delegator.findByAnd("PartySalesman",UtilMisc.toMap("partyId",route.getString("executorId")),null,false);
        }
        return listResult;
	}
	public static Timestamp getStartDateTimestamp(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp entryDateStartDay = new Timestamp(cal.getTimeInMillis());
		return entryDateStartDay;
	}
	public static Timestamp getEndDateTimestamp(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Timestamp entryDateEndDay = new Timestamp(cal.getTimeInMillis());
		return entryDateEndDay;
	}

    public static String getTodayScheduleRoute() {
        Calendar now = Calendar.getInstance();
        String[] strDays = new String[]{"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        return strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
    }

}
