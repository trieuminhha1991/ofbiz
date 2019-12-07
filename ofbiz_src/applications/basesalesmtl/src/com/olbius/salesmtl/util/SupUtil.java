package com.olbius.salesmtl.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.olbius.basehr.util.Organization;
import com.olbius.basesales.util.SalesPartyUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.salesmtl.DistributorServices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SupUtil {
	public static final String module = SupUtil.class.getName();
	public static String RESOURCE_PROPERTIES = "basesalesmtl.properties";
	public static final String ROUTE_ENABLED = "ROUTE_ENABLED";
	public static final String ROUTE_DISABLED = "ROUTE_DISABLED";
	public static final String ENABLED = "ENABLED";
	public static final String DISABLED = "DISABLED";

	public static List<String> getManagerIdsOfChildDeptBySalesManager(Delegator delegator, GenericValue userLogin) {
	    List<String> result = FastList.newInstance();
        try {
            Organization org = PartyUtil.buildOrg(delegator, PartyUtil.getCurrentOrganization(delegator, (String)userLogin.get("userLoginId")), true, true);
            List<GenericValue> listDept  = org.getAllDepartmentList(delegator);
            List<String> listDeptIds  = EntityUtil.getFieldListFromEntityList(listDept, "partyIdTo", true);
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityUtil.getFilterByDateExpr());
            conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listDeptIds));
            conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
            conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
            List<GenericValue> listEmpl = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds),UtilMisc.toSet("partyIdFrom"),null,null,false);
            result = EntityUtil.getFieldListFromEntityList(listEmpl, "partyIdFrom", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	public static Date getNextDate(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return new Date(cal.getTimeInMillis());
	}

	public static List<Date> filterDates(List<Date> dates,
			String visitFrequencyTypeId) {
		List<Date> retList = FastList.newInstance();
		int n = 1;
		if (visitFrequencyTypeId != null) {
			if (visitFrequencyTypeId.equals("F1")) {
				n = 4;
			} else if (visitFrequencyTypeId.equals("F2")) {
				n = 2;
			} else if (visitFrequencyTypeId.equals("F4")) {
				n = 1;
			} else if (visitFrequencyTypeId.equals("F8")) {
				n = 1;
			}
		}
		for (int i = 0; i < dates.size(); i++) {
			if (i % n == 0)
				retList.add(dates.get(i));
		}
		return retList;
	}

	public static List<Date> generateDates(Timestamp fromDateTime,
			Timestamp thruDateTime, String scheduleDate,
			String visitFrequencyTypeId) {
		// visitFrequencyTypeId = F1 (1 thang 1 lan), F2 (1 thang 2 lan), F4 (1
		// thang 4 lan), F8 (1 thang 8 lan)
		// scheduleDate: thu trong tuan (MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
		// FRIDAY, SATURDAY, SUNDAY
		String[] days = new String[] { "SUNDAY", "MONDAY", "TUESDAY",
				"WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
		HashMap<String, Integer> mDay = new HashMap<String, Integer>();
		for (int i = 0; i < days.length; i++)
			mDay.put(days[i], i);
		int n = 1;
		if (visitFrequencyTypeId.equals("F1")) {
			n = 4;
		} else if (visitFrequencyTypeId.equals("F2")) {
			n = 2;
		} else if (visitFrequencyTypeId.equals("F4")) {
			n = 1;
		} else if (visitFrequencyTypeId.equals("F8")) {
			n = 1;
		}

		List<Date> dates = FastList.newInstance();
		Date fromDate = new Date(fromDateTime.getTime());
		Date thruDate = new Date(thruDateTime.getTime());
		int count = 0;
		while (fromDate.compareTo(thruDate) <= 0) {
			int day = fromDate.getDay();
			int idxday = mDay.get(scheduleDate);
			if (day == idxday) {
				if (count % n == 0)
					dates.add(fromDate);
				count++;
			}

			fromDate = getNextDate(fromDate);
		}
		return dates;
	}

	//public static List<Date> generateDates(Timestamp fromDateTime,
	//		Timestamp thruDateTime, String scheduleDate) {
	public static List<Date> generateDates(java.sql.Date fromDate,
				java.sql.Date thruDate, String scheduleDate) {
			
		// visitFrequencyTypeId = F1 (1 thang 1 lan), F2 (1 thang 2 lan), F4 (1
		// thang 4 lan), F8 (1 thang 8 lan)
		// scheduleDate: thu trong tuan (MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
		// FRIDAY, SATURDAY, SUNDAY
		String[] days = new String[] { "SUNDAY", "MONDAY", "TUESDAY",
				"WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
		HashMap<String, Integer> mDay = new HashMap<String, Integer>();
		for (int i = 0; i < days.length; i++)
			mDay.put(days[i], i);

		List<Date> dates = FastList.newInstance();
		//Date fromDate = new Date(fromDateTime.getTime());
		//Date thruDate = new Date(thruDateTime.getTime());
		while (fromDate.compareTo(thruDate) <= 0) {
			int day = fromDate.getDay();
			int idxday = mDay.get(scheduleDate);
			if (day == idxday) {
				dates.add(fromDate);
			}

			fromDate = getNextDate(fromDate);
		}
		return dates;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addSaleRouteScheduleDate(
			Delegator delegator, String routeId, List<String> scheduleDate) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			for (String date : scheduleDate) {
				GenericValue gv = delegator.makeValue("SalesRouteSchedule");
				String salesRouteScheduleId = delegator.getNextSeqId("SalesRouteSchedule");
				gv.put("salesRouteScheduleId", salesRouteScheduleId);
				gv.put("routeId", routeId);
				gv.put("scheduleRoute", date);
				gv.put("statusId", "ENABLED");
				gv.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(gv);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> removeSaleRouteScheduleAndDetailDate(
			Delegator delegator, String routeId) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("routeId",
					EntityOperator.EQUALS, routeId));
			delegator.removeByCondition("RouteScheduleDetailDate",
					EntityCondition.makeCondition(conds));

			List<GenericValue> rs = delegator.findList("SalesRouteSchedule",
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			for (GenericValue gv : rs) {
				gv.put("statusId", "DISABLED");
				gv.put("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(gv);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> removeRouteScheduleDetailDate(
            Delegator delegator, String routeId) {
        Map<String, Object> retSucc = ServiceUtil.returnSuccess();
        try {
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("routeId",
                    EntityOperator.EQUALS, routeId));
            delegator.removeByCondition("RouteScheduleDetailDate",
                    EntityCondition.makeCondition(conds));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retSucc;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> removeSaleRouteScheduleDetailDateForOneCustomerRemoved(
			Delegator delegator, String customerId, String routeId) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("customerId", customerId));
			conds.add(EntityCondition.makeCondition("routeId", routeId));
			List<GenericValue> lst = delegator.findList(
					"RouteScheduleDetailDate",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			for (GenericValue gv : lst) {
				delegator.removeValue(gv);
				Debug.log(module
						+ "::removeSaleRouteScheduleDetailDateForOneCustomerRemoved, REMOVE /"
						+ lst.size());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> generateSaleRouteScheduleDetailDateForOneCustomerAdded(
			Delegator delegator, String routeId, String customerId) {
		Map<String, Object> retSucc = FastMap.newInstance();

		Debug.log(module
				+ "::generateSaleRouteScheduleDetailDateForOneCustomerAdded, routeId = "
				+ routeId + ", customerId = " + customerId);
		try {
			
			GenericValue route = delegator.findOne("Route",
					UtilMisc.toMap("routeId", routeId), false);
			if (route == null) {
				return ServiceUtil.returnError("Route NOT exists");
			}
			String salesmanId = route.getString("executorId");

			Date fromDate = null;
			Date thruDate = null;
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("routeId",
					EntityOperator.EQUALS, routeId));
			
			List<GenericValue> l_rsdd = delegator.findList("RouteScheduleDetailDate", 
					EntityCondition.makeCondition(conds), null,null,null,false);
			if (UtilValidate.isEmpty(l_rsdd)) {
				return ServiceUtil.returnError("RouteScheduleDetailDate for route:" +routeId+ " is empty");
			}
			//HashSet<Date> sche_dates = new HashSet<Date>();
			for(GenericValue g: l_rsdd){
				Date d = g.getDate("date");
				if(fromDate == null) fromDate = d;
				if(thruDate == null) thruDate = d;
				if(d.after(thruDate)) thruDate = d;
				if(d.before(fromDate)) fromDate = d;
				//sche_dates.add(d);
			}
			
			//List<EntityCondition> conds = FastList.newInstance();
			conds.clear();
			conds.add(EntityCondition.makeCondition("routeId",
					EntityOperator.EQUALS, routeId));
			conds.add(EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, SupUtil.ENABLED));
            conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> l_srs = delegator.findList("SalesRouteSchedule",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			conds.clear();
			conds.add(EntityCondition.makeCondition("routeId",
					EntityOperator.EQUALS, routeId));
			// conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> lstCustomers = delegator.findList(
					"RouteCustomerView", EntityCondition.makeCondition(conds),
					null, null, null, false);

			List<List<Date>> sche_dates = FastList.newInstance();
			List<String> salesRouteScheduleIds = FastList.newInstance();
			// create new items in RouteScheduleDetailDate
			for (GenericValue srs : l_srs) {
				String salesRouteScheduleId = (String) srs
						.get("salesRouteScheduleId");
				// String routeId = (String)srs.get("routeId");
				String scheduleRoute = (String) srs.get("scheduleRoute");
				//Timestamp fromDate = (Timestamp) srs.get("fromDate");
				//Timestamp thruDate = (Timestamp) srs.get("thruDate");
				List<Date> dates = SupUtil.generateDates(fromDate, thruDate,
						scheduleRoute);
				sche_dates.add(dates);
				salesRouteScheduleIds.add(salesRouteScheduleId);
			}
			
			
			GenericValue c = delegator.findOne("PartyCustomer",
					UtilMisc.toMap("partyId", customerId), false);

			String visitFrequencyTypeId = (String) c
					.get("visitFrequencyTypeId");
			Long seq = c.getLong("sequenceNum");
			Debug.log(module
					+ "::generateSaleRouteScheduleDetailDateForOneCustomerAdded, routeId = "
					+ routeId + ", customerId = " + customerId
					+ ", sche_date = " + sche_dates.size());

			for (int i = 0; i < sche_dates.size(); i++) {
				String salesRouteScheduleId = salesRouteScheduleIds.get(i);
				List<Date> sel_dates = SupUtil.filterDates(sche_dates.get(i),
						visitFrequencyTypeId);
				for (Date d : sel_dates) {
					Debug.log(module
							+ "::generateSaleRouteScheduleDetailDateForOneCustomerAdded, routeId = "
							+ routeId + ", customerId = " + customerId
							+ ", date = " + d);

					GenericValue rsdd = delegator.findOne(
							"RouteScheduleDetailDate", UtilMisc.toMap("date",
									d, "customerId", customerId, "salesmanId",
									salesmanId, "routeId", routeId), false);
					if (rsdd != null) {
						rsdd.put("sequenceNum", seq);
						rsdd.put("salesRouteScheduleId", salesRouteScheduleId);
						delegator.store(rsdd);
					} else {
						rsdd = delegator.makeValue("RouteScheduleDetailDate");
						rsdd.put("date", d);
						rsdd.put("customerId", customerId);
						rsdd.put("sequenceNum", seq);
						rsdd.put("routeId", routeId);
						rsdd.put("salesmanId", salesmanId);
						rsdd.put("salesRouteScheduleId", salesRouteScheduleId);
						delegator.create(rsdd);
						Debug.log(module
								+ "::generateSaleRouteScheduleDetailDateForOneCustomerAdded, routeId = "
								+ routeId + ", customerId = " + customerId
								+ ", date " + d + " CREATED");

					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	public static List<GenericValue> getRouteListFromCode(Delegator delegator,
			String routeCode) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("routeCode",
					EntityOperator.EQUALS, routeCode));
			conds.add(EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, ROUTE_ENABLED));
			List<GenericValue> lst = delegator.findList("Route",
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void createAndUpdateRouteInformation(Delegator delegator,
			String routeId, String description) throws GenericEntityException {
		GenericValue route = delegator.findOne("RouteInformation",
				UtilMisc.toMap("routeId", routeId), false);
		if (UtilValidate.isEmpty(route)) {
			route = delegator.makeValue("RouteInformation");
			route.set("routeId", routeId);
			route.set("description", description);
			route.create();
		} else {
			route.set("description", description);
			route.store();
		}
	}

	public static void createAndUpdateRouteSchedule(Delegator delegator,
			String routeId, String scheduleRoute, Timestamp fromDate,
			Timestamp thruDate) throws GenericEntityException {
		GenericValue route = delegator.findOne("RouteSchedule", UtilMisc.toMap(
				"routeId", routeId, "scheduleRoute", scheduleRoute, "fromDate",
				fromDate), false);
		if (UtilValidate.isEmpty(route)) {
			route = delegator.makeValue("RouteSchedule");
			route.set("routeId", routeId);
			route.set("scheduleRoute", scheduleRoute);
			if (UtilValidate.isNotEmpty(fromDate)) {
				route.set("fromDate", fromDate);
			} else {
				route.set("fromDate", UtilDateTime.nowTimestamp());
			}
			if (UtilValidate.isNotEmpty("thruDate")) {
				route.set("thruDate", thruDate);
			}
			route.create();
		} else {
			if (UtilValidate.isNotEmpty("thruDate")) {
				route.set("thruDate", thruDate);
			}
			route.store();
		}
	}

	public static void updateRouteSalesRelationship(Delegator delegator,
			LocalDispatcher dispatcher, String routeId, String employeeIdFrom,
			String employeeId, boolean isCreated, boolean isRemoved)
			throws GenericEntityException, GenericServiceException {
		createAndUpdateRouteSalesmanRelationship(delegator, dispatcher,
				routeId, employeeId, isCreated, isRemoved);
		if (UtilValidate.isNotEmpty(employeeIdFrom)) {
			createAndUpdateRouteSalesmanRelationship(delegator, dispatcher,
					routeId, employeeIdFrom, false, isRemoved);
			EntityListIterator listCustomerInRoute = null;
			List<String> cirPartyIds = FastList.newInstance();
			GenericValue cir = null;
			try {
				listCustomerInRoute = delegator
						.find("PartyRelationship",
								EntityCondition.makeCondition(UtilMisc.toList(
										EntityUtil.getFilterByDateExpr(),
										EntityCondition.makeCondition(
												"roleTypeIdFrom", "ROUTE"),
										EntityCondition.makeCondition(
												"roleTypeIdTo", "CUSTOMER"),
										EntityCondition.makeCondition(
												"partyRelationshipTypeId",
												"SALES_ROUTE"), EntityCondition
												.makeCondition("partyIdFrom",
														routeId))), null, null,
								null, null);
				cirPartyIds = FastList.newInstance();
				while ((cir = listCustomerInRoute.next()) != null) {
					cirPartyIds.add(cir.getString("partyIdTo"));
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			} finally {
				if (listCustomerInRoute != null) {
					listCustomerInRoute.close();
				}
			}
			if (UtilValidate.isNotEmpty(cirPartyIds)) {
				EntityListIterator listCustomerOwnedBySm = null;
				try {
					cir = null;
					listCustomerOwnedBySm = delegator.find("PartyRelationship",
							EntityCondition.makeCondition(UtilMisc.toList(
									EntityUtil.getFilterByDateExpr(),
									EntityCondition
											.makeCondition("roleTypeIdFrom",
													"SALES_EXECUTIVE"),
									EntityCondition.makeCondition(
											"roleTypeIdTo", "CUSTOMER"),
									EntityCondition.makeCondition(
											"partyRelationshipTypeId",
											"SALES_REP_REL"), EntityCondition
											.makeCondition("partyIdFrom",
													employeeIdFrom),
									EntityCondition.makeCondition("partyIdTo",
											EntityOperator.IN, cirPartyIds))),
							null, null, null, null);
					while ((cir = listCustomerOwnedBySm.next()) != null) {
						cir.set("thruDate", UtilDateTime.nowTimestamp());
						cir.store();
						createAndUpdateRelationship(delegator, dispatcher,
								employeeId, cir.getString("partyIdTo"),
								"SALES_EXECUTIVE", "CUSTOMER", "SALES_REP_REL",
								isCreated, isRemoved);
					}
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				} finally {
					if (listCustomerOwnedBySm != null) {
						listCustomerOwnedBySm.close();
					}
				}
			}
		}
	}

	public static void createAndUpdateRouteSalesmanRelationship(
			Delegator delegator, LocalDispatcher dispatcher, String routeId,
			String employeeId, boolean isCreated, boolean isRemoved)
			throws GenericEntityException, GenericServiceException {
		createAndUpdateRelationship(delegator, dispatcher, employeeId, routeId,
				"SALES_EXECUTIVE", "ROUTE", "SALES_ROUTE", isCreated, isRemoved);
	}

	public static void createAndUpdateRouteStoreRelationship(
			Delegator delegator, LocalDispatcher dispatcher, String routeId,
			String customerId, boolean isCreated, boolean isRemoved)
			throws GenericEntityException, GenericServiceException {
		/*createAndUpdateRelationship(delegator, dispatcher, routeId, customerId,
				"ROUTE", "CUSTOMER", "SALES_ROUTE", isCreated, isRemoved);*/
		//create RouteCustomer and thruDate it.
		RouteUtils.bindCustomerToRoute(delegator, customerId, routeId);
		RouteUtils.unbindCustomerToRoute(delegator, customerId, routeId);
	}

	public static void createAndUpdateRelationship(Delegator delegator,
			LocalDispatcher dispatcher, String partyIdFrom, String partyIdTo,
			String roleTypeIdFrom, String roleTypeIdTo,
			String partyRelationshipTypeId, boolean isCreated, boolean isRemoved)
			throws GenericEntityException, GenericServiceException {
		List<GenericValue> route = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("partyIdFrom", partyIdFrom),
						EntityCondition.makeCondition("partyIdTo", partyIdTo),
						EntityCondition.makeCondition("roleTypeIdFrom",
								roleTypeIdFrom), EntityCondition.makeCondition(
								"roleTypeIdTo", roleTypeIdTo), EntityCondition
								.makeCondition("partyRelationshipTypeId",
										partyRelationshipTypeId), EntityUtil
								.getFilterByDateExpr())), null, null, null,
				false);

		if (UtilValidate.isNotEmpty(route) && isRemoved) {
			for (GenericValue e : route) {
				e.set("thruDate", UtilDateTime.nowTimestamp());
				e.store();
			}
		}
		if (isCreated) {
			GenericValue userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", userLogin);
			input.put("partyIdFrom", partyIdFrom);
			input.put("partyIdTo", partyIdTo);
			input.put("fromDate", UtilDateTime.nowTimestamp());
			input.put("roleTypeIdFrom", roleTypeIdFrom);
			input.put("roleTypeIdTo", roleTypeIdTo);
			input.put("partyRelationshipTypeId", partyRelationshipTypeId);
			dispatcher.runSync("createPartyRelationship", input);
		}
	}

	public static void deleteRouteSchedule(Delegator delegator, String routeId,
			String scheduleRoute, Timestamp fromDate)
			throws GenericEntityException {
		createAndUpdateRouteSchedule(delegator, routeId, scheduleRoute,
				fromDate, UtilDateTime.nowTimestamp());
	}

	public static List<EntityCondition> buildConditionFindRoute(
			Delegator delegator, List<EntityCondition> listAllConditions,
			EntityFindOptions opts) throws GenericEntityException {
		List<EntityCondition> condi = FastList.newInstance();
		for (EntityCondition e : listAllConditions) {
			String tmp = e.toString();
			if (tmp.contains("employeeId")) {
				String[] tmpL = tmp.split(" LIKE|= ");
				if (UtilValidate.isNotEmpty(tmpL) && tmpL.length == 2) {
					List<GenericValue> employee = delegator.findList(
							"PartyRelationShipAndPerson",
							EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition
											.makeCondition("roleTypeIdFrom",
													"SALES_EXECUTIVE"),
									EntityCondition.makeCondition(
											"roleTypeIdTo", "ROUTE"),
									EntityCondition.makeCondition(
											"partyCode",
											EntityOperator.LIKE,
											new StringBuilder()
													.append("%")
													.append(tmpL[1]
															.replace("\"", "")
															.replace("%", "")
															.replace("'", "")
															.replace(" ", ""))
													.append("%").toString()),
									EntityUtil.getFilterByDateExpr())),
							UtilMisc.toSet("partyIdFrom", "partyIdTo",
									"fromDate"), UtilMisc.toList("-fromDate"),
							opts, false);
					List<String> tmem = FastList.newInstance();
					for (GenericValue e1 : employee) {
						tmem.add(e1.getString("partyIdTo"));
					}
					if (UtilValidate.isNotEmpty(tmem)) {
						condi.add(EntityCondition.makeCondition("partyId",
								EntityOperator.IN, tmem));
					} else {
						condi.add(EntityCondition
								.makeCondition("partyId", null));
					}
				} else {
					condi.add(EntityCondition.makeCondition("partyId", null));
				}
				break;
			} else if (tmp.contains("scheduleRoute")) {
				List<GenericValue> routes = delegator.findList("RouteSchedule",
						e, UtilMisc.toSet("routeId"), null, opts, false);
				List<String> routesCond = FastList.newInstance();
				for (GenericValue y : routes) {
					routesCond.add(y.getString("routeId"));
				}
				condi.add(EntityCondition.makeCondition("partyId",
						EntityOperator.IN, routesCond));
			} else {
				condi.add(e);
			}
		}
		return condi;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerAddress(Delegator delegator,
			String partyId) throws GenericEntityException {
		List<Map<String, Object>> partyContactMechs = ContactMechWorker
				.getPartyContactMechValueMaps(delegator, partyId, false);
		Map<String, Object> res = FastMap.newInstance();
		for (Map<String, Object> m : partyContactMechs) {
			GenericValue contactMechType = (GenericValue) m
					.get("contactMechType");
			if (UtilValidate.isNotEmpty(contactMechType)) {
				String contactMechTypeId = contactMechType
						.getString("contactMechTypeId");
				if (contactMechTypeId.equals("POSTAL_ADDRESS")) {
					List<GenericValue> partyContactMechPurposes = (List<GenericValue>) m
							.get("partyContactMechPurposes");
					for (GenericValue x : partyContactMechPurposes) {
						String contactMechPurposeTypeId = x
								.getString("contactMechPurposeTypeId");
						if ("SHIPPING_LOCATION"
								.equals(contactMechPurposeTypeId)) {
							if (UtilValidate.isNotEmpty(m.get("postalAddress"))) {
								GenericValue postalAddress = (GenericValue) m
										.get("postalAddress");
								res.putAll(postalAddress);
								String stateProvinceGeoId = (String) postalAddress
										.get("stateProvinceGeoId");
								if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
									String city = DistributorServices
											.getGeoName(delegator,
													stateProvinceGeoId);
									res.put("city", city);
								}
								String districtGeoId = (String) postalAddress
										.get("districtGeoId");
								if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
									String district = DistributorServices
											.getGeoName(delegator,
													districtGeoId);
									res.put("district", district);
								}
								break;
							}
						}
					}
					break;
				}
			}
		}
		return res;
	}

	public static List<GenericValue> getAllRouteValue(Delegator delegator,
			String userLoginId) throws GenericEntityException {
/*		List<GenericValue> routes = delegator.findList("RouteDetail",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("createdByUserLogin", userLoginId),
						EntityCondition.makeCondition("partyTypeId",
								"SALES_ROUTE"), EntityCondition.makeCondition(
								"statusId", "PARTY_ENABLED"))), UtilMisc.toSet(
						"partyId", "groupName", "partyCode"), UtilMisc
						.toList("partyId"), null, false);*/
		List<GenericValue> routes = delegator.findList("Route",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("managerId", userLoginId), EntityCondition.makeCondition(
						"statusId", "ROUTE_ENABLED"))), UtilMisc.toSet(
						"routeId", "routeName", "routeCode"), UtilMisc
						.toList("routeId"), null, false);
		return routes;
	}

	public static List<String> getAllRoute(Delegator delegator,
			String userLoginId) throws GenericEntityException {
/*		List<GenericValue> routes = delegator.findList("RouteDetail",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("createdByUserLogin", userLoginId),
						EntityCondition.makeCondition("partyTypeId",
								"SALES_ROUTE"), EntityCondition.makeCondition(
								"statusId", "PARTY_ENABLED"))), UtilMisc.toSet(
						"partyId", "groupName", "partyCode"), UtilMisc
						.toList("partyId"), null, false);
		List<String> res = EntityUtil.getFieldListFromEntityList(routes,
				"partyId", true);*/
		List<GenericValue> routes = delegator.findList("Route",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
								.makeCondition("managerId", userLoginId),
						EntityCondition.makeCondition(
								"statusId", "ROUTE_ENABLED"))), UtilMisc.toSet(
						"routeId", "routeName", "routeCode"), UtilMisc
						.toList("routeId"), null, false);
		List<String> res = EntityUtil.getFieldListFromEntityList(routes,
				"routeId", true);
		return res;
	}

	public static List<GenericValue> getAllRouteValue(Delegator delegator)
			throws GenericEntityException {
/*		List<GenericValue> routes = delegator.findList("RouteDetail",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("partyTypeId", "SALES_ROUTE"),
						EntityCondition.makeCondition("statusId",
								"PARTY_ENABLED"))), UtilMisc.toSet("partyId"),
				UtilMisc.toList("partyId"), null, false);*/
		List<GenericValue> routes = delegator.findList("Route",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId",
						"ROUTE_ENABLED"))), UtilMisc.toSet("routeId"),
				UtilMisc.toList("routeId"), null, false);
		return routes;
	}

	public static List<String> getAllRoute(Delegator delegator)
			throws GenericEntityException {
/*		List<GenericValue> routes = delegator.findList("RouteDetail",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("partyTypeId", "SALES_ROUTE"),
						EntityCondition.makeCondition("statusId",
								"PARTY_ENABLED"))), UtilMisc.toSet("partyId"),
				UtilMisc.toList("partyId"), null, false);
		List<String> res = EntityUtil.getFieldListFromEntityList(routes,
				"partyId", true);*/
		List<GenericValue> routes = delegator.findList("Route",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId",
						"ROUTE_ENABLED"))), UtilMisc.toSet("routeId"),
				UtilMisc.toList("routeId"), null, false);
		List<String> res = EntityUtil.getFieldListFromEntityList(routes,
				"routeId", true);
		return res;
	}

	public static List<String> getSalesmanManagement(DispatchContext dpct,
			String partyId) throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityListIterator list = null;
		List<String> parties = FastList.newInstance();
		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			List<String> pt = PartyUtil.getDepartmentOfEmployee(delegator,
					partyId, UtilDateTime.nowTimestamp());
			if (UtilValidate.isNotEmpty(pt)) {
				String sup = pt.get(0);
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc
						.toList(EntityCondition.makeCondition("statusId",
								"PARTY_ENABLED"), EntityCondition
								.makeCondition("statusId", null)),
						EntityOperator.OR));
				listAllConditions.add(EntityCondition.makeCondition(
						"partyIdTo", sup));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				EntityCondition cond = EntityCondition.makeCondition(
						listAllConditions, EntityJoinOperator.AND);

				list = delegator.find("PartyRelationshipSalesmanSup", cond,
						null,
						UtilMisc.toSet("partyId", "partyCode", "fullName"),
						null, null);
				GenericValue e = null;
				while ((e = list.next()) != null) {
					parties.add(e.getString("partyId"));
				}
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return parties;
	}

	public static String getSupManager(DispatchContext dpct, String partyId)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityListIterator list = null;
		String manager = null;
		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("statusId",
							"PARTY_ENABLED"), EntityCondition.makeCondition(
							"statusId", null)), EntityOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom",
					partyId));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);

			list = delegator.find("PartyRelationshipSalesmanSup", cond, null,
					UtilMisc.toSet("partyId", "partyCode", "fullName",
							"partyIdTo"), null, null);
			List<GenericValue> allsup = list.getCompleteList();
			GenericValue e = EntityUtil.getFirst(allsup);
			String supId = e.getString("partyIdTo");
			GenericValue supman = PartyUtil.getCurrentManagerOrg(delegator,
					supId, "MANAGER");
			if (UtilValidate.isNotEmpty(supman)) {
				manager = supman.getString("partyId");
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return manager;
	}


	public static String getSupManagerN(DispatchContext dpct, String partyId)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<GenericValue> lgvs = null;
		String manager = null;
		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("statusId",
							"PARTY_ENABLED"), EntityCondition.makeCondition(
							"statusId", null)), EntityOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition("partyId",
					partyId));
			//listAllConditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);

			lgvs = delegator.findList("PartySalesman", cond,
					UtilMisc.toSet("partyId", "partyCode", "fullName",
							"supervisorId"),null, null, false);


			manager = lgvs.get(0).getString("supervisorId");
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return manager;
	}

	public static String getDistributorRelated(Delegator delegator,
			String partyId) throws GenericEntityException {
		String partyIdFrom = null;
		List<GenericValue> list = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toList(EntityCondition
						.makeCondition("partyIdTo", partyId), EntityCondition
						.makeCondition("roleTypeIdFrom", "DISTRIBUTOR"),
						EntityCondition.makeCondition("roleTypeIdTo",
								"SALES_EXECUTIVE"), EntityCondition
								.makeCondition("partyRelationshipTypeId",
										"SALES_EMPLOYMENT"), EntityUtil
								.getFilterByDateExpr())), UtilMisc.toSet(
						"partyIdFrom", "partyIdTo", "fromDate"), UtilMisc
						.toList("-fromDate"), null, false);
		if (UtilValidate.isNotEmpty(list)) {
			GenericValue e = EntityUtil.getFirst(list);
			partyIdFrom = e.getString("partyIdFrom");
		}
		return partyIdFrom;
	}

	public static EntityCondition getMapCustomerCondition(DispatchContext dpct,
			Map<String, Object> context) {
		EntityCondition listConditions = null;
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		String districtGeoId = (String) context.get("districtGeoId");
		List<String> sups = (List<String>) context.get("sups");
		LocalDispatcher dispatcher = dpct.getDispatcher();
		try {
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> dis = FastList.newInstance();
			boolean b1 = UtilValidate.isNotEmpty(stateProvinceGeoId);
			List<String> states = null;
			List<String> districts = null;
			// Not a Sup
			if (UtilValidate.isNotEmpty(sups)) {
				for (String s : sups) {
					List<String> tmp = PartyWorker.getDistributorIdsBySupDept(
							delegator, s);
					dis.addAll(tmp);
				}
				if (!b1) {
					List<GenericValue> addresses = PartyWorker
							.getAllPartyPostalAddressValue(delegator, sups,
									null);
					states = EntityUtil.getFieldListFromEntityList(addresses,
							"stateProvinceGeoId", true);
					districts = EntityUtil.getFieldListFromEntityList(
							addresses, "districtGeoId", true);
				}
			} else {
				// is a sup
				sups = PartyUtil.getDepartmentOfEmployee(delegator, partyId,
						UtilDateTime.nowTimestamp());
				dis = PartyWorker.getDistributorIdsBySup(delegator, partyId);
				if (UtilValidate.isNotEmpty(sups)) {
					String sup = sups.get(0);
					List<GenericValue> addresses = PartyWorker
							.getAllPartyPostalAddressValue(delegator, sup, null);
					states = EntityUtil.getFieldListFromEntityList(addresses,
							"stateProvinceGeoId", true);
					districts = EntityUtil.getFieldListFromEntityList(
							addresses, "districtGeoId", true);
				}
			}
			boolean b2 = UtilValidate.isNotEmpty(states)
					|| UtilValidate.isNotEmpty(districts);
			if (!b1 && b2) {
				List<EntityExpr> tmp = UtilMisc.toList(EntityCondition
						.makeCondition("stateProvinceGeoId", EntityOperator.IN,
								states));
				if (UtilValidate.isNotEmpty(districts)) {
					tmp.add(EntityCondition.makeCondition("districtGeoId",
							EntityOperator.IN, districts));
				}
				listAllConditions.add(EntityCondition.makeCondition(tmp,
						EntityOperator.OR));
			} else if (b1) {
				listAllConditions.add(EntityCondition.makeCondition(
						"stateProvinceGeoId", stateProvinceGeoId));
				if (UtilValidate.isNotEmpty(districtGeoId)) {
					listAllConditions.add(EntityCondition.makeCondition(
							"districtGeoId", districtGeoId));
				}
			}
			if (UtilValidate.isNotEmpty(dis) && (b1 || b2)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"partyIdTo", EntityOperator.IN, dis));
				listAllConditions.add(EntityCondition.makeCondition(
						"roleTypeIdFrom", "CUSTOMER"));
				listAllConditions.add(EntityCondition.makeCondition(
						"roleTypeIdTo", "DISTRIBUTOR"));
				listAllConditions.add(EntityCondition.makeCondition(
						"contactMechPurposeTypeId", "PRIMARY_LOCATION"));
				listAllConditions.add(EntityCondition.makeCondition("latitude",
						EntityOperator.NOT_EQUAL, null));
				listAllConditions.add(EntityCondition.makeCondition(
						"longitude", EntityOperator.NOT_EQUAL, null));
				listAllConditions.add(EntityUtil.getFilterByDateExpr(
						"relationFromDate", "relationThruDate"));
				listAllConditions.add(EntityUtil.getFilterByDateExpr(
						"ctmFromDate", "ctmThruDate"));
				Set<String> fieldsToSelect = UtilMisc.toSet("partyIdFrom",
						"groupName", "latitude", "longitude", "address1");
				fieldsToSelect.addAll(UtilMisc.toSet("cityGeoName",
						"districtGeoName"));
				listConditions = EntityCondition.makeCondition(
						listAllConditions, EntityJoinOperator.AND);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return listConditions;
	}

	public static EntityCondition getMapContactCondition(DispatchContext dpct,
			Map<String, Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		String districtGeoId = (String) context.get("districtGeoId");
		List<String> sups = (List<String>) context.get("sups");
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(
				delegator, userLogin.getString("userLoginId"));
		EntityCondition res = null;
		try {
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> states = null;
			List<String> districts = null;
			if (UtilValidate.isNotEmpty(stateProvinceGeoId)
					|| UtilValidate.isNotEmpty(districtGeoId)) {
				if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
					states = UtilMisc.toList(stateProvinceGeoId);
				}
				if (UtilValidate.isNotEmpty(districtGeoId)) {
					districts = UtilMisc.toList(districtGeoId);
				}
			} else {
				if (UtilValidate.isNotEmpty(sups)) {
					List<GenericValue> addresses = PartyWorker
							.getAllPartyPostalAddressValue(delegator, sups,
									null);
					states = EntityUtil.getFieldListFromEntityList(addresses,
							"stateProvinceGeoId", true);
					districts = EntityUtil.getFieldListFromEntityList(
							addresses, "districtGeoId", true);
				} else {
					sups = PartyUtil.getDepartmentOfEmployee(delegator,
							partyId, UtilDateTime.nowTimestamp());
					if (UtilValidate.isNotEmpty(sups)) {
						String sup = sups.get(0);
						List<GenericValue> addresses = PartyWorker
								.getAllPartyPostalAddressValue(delegator, sup,
										null);
						states = EntityUtil.getFieldListFromEntityList(
								addresses, "stateProvinceGeoId", true);
						districts = EntityUtil.getFieldListFromEntityList(
								addresses, "districtGeoId", true);
					}
				}
			}
			if (UtilValidate.isNotEmpty(states)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"stateProvinceGeoId", EntityOperator.IN, states));
			}
			if (UtilValidate.isNotEmpty(districts)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"districtGeoId", EntityOperator.IN, districts));
			}
			// listAllConditions.add(EntityCondition.makeCondition("partyIdTo",
			// partyIdTo));
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "CONTACT"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"INTERNAL_ORGANIZATIO"));
			listAllConditions.add(EntityCondition.makeCondition(
					"contactMechPurposeTypeId", "PRIMARY_LOCATION"));
			listAllConditions.add(EntityCondition.makeCondition("latitude",
					EntityOperator.NOT_EQUAL, null));
			listAllConditions.add(EntityCondition.makeCondition("longitude",
					EntityOperator.NOT_EQUAL, null));
			listAllConditions.add(EntityUtil.getFilterByDateExpr(
					"relationFromDate", "relationThruDate"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("ctmFromDate",
					"ctmThruDate"));
			Set<String> fieldsToSelect = UtilMisc.toSet("partyIdFrom",
					"groupName", "latitude", "longitude", "address1");
			fieldsToSelect.addAll(UtilMisc.toSet("cityGeoName",
					"districtGeoName"));
			res = EntityCondition.makeCondition(listAllConditions,
					EntityJoinOperator.AND);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
		return res;
	}
}
