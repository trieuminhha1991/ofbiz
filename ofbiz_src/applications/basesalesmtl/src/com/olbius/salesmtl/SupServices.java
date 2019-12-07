package com.olbius.salesmtl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.olbius.basesales.party.PartyWorker;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.salesmtl.util.SupUtil;

public class SupServices {
	public static final String module = SupServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String RESOURCE_DL = "basesalesmtl.properties";
	public static final String RSN_PRTROLE_ROUTE_DL = "party.role.route.BaseSales";
	public static final String RSN_PRTROLE_SALESMAN_GT_DL = "party.role.salesman.gt.BaseSales";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRoutesOfACustomer(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

        String customerId = null;
        if (parameters.containsKey("customerId") && parameters.get("customerId").length > 0) {
            customerId = parameters.get("customerId")[0];
        }
        try {
            if (customerId == null) {
                return ServiceUtil.returnError("getRoutesOfACustomer not has: customerId");
            }
            listAllConditions.add(EntityCondition.makeCondition("customerId",
                    EntityOperator.EQUALS, customerId));
            opts.setDistinct(true);

            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "RouteCustomerAndCustomerAndScheduleAvail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

        } catch (Exception e) {
            String errMsg = "Fatal error calling getRoutesOfACustomer service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

	public static Map<String, Object> generateCustomerSequenceOfRoute(
			DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<EntityCondition> conds = FastList.newInstance();
		String routeId = (String)context.get("routeId");
		try {
			Map<String, Object> in = FastMap.newInstance();
			in.put("routeId", routeId);
			Map<String, Object> customerInRoutes = dispatcher.runSync("getAllCustomerInRouteWithLatLng", in);
			List<GenericValue> customers = (List<GenericValue>)customerInRoutes.get("results");
			if (UtilValidate.isEmpty(customers)) {
			    return retSucc;
            }
			//get routeCustomers with latitude, longitude
            List<GenericValue> routeCustomersWithLatLng = FastList.newInstance();
            List<GenericValue> routeCustomersWithoutLatLng = FastList.newInstance();

            List<GenericValue> customersWithLatLng = FastList.newInstance();
            List<GenericValue> customersWithoutLatLng = FastList.newInstance();

            List<String> customerIds = EntityUtil.getFieldListFromEntityList(customers,"customerId", true);
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerIds));
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds),null,UtilMisc.toList("customerId"),null,false);

            for (int i=0; i<customerIds.size(); i++) {
                GenericValue aCustomer = customers.get(i);
                if (aCustomer.get("latitude") != null && aCustomer.get("longitude") != null) {
                    customersWithLatLng.add(aCustomer);
                    routeCustomersWithLatLng.add(routeCustomers.get(i));
                } else {
                    customersWithoutLatLng.add(aCustomer);
                    routeCustomersWithoutLatLng.add(routeCustomers.get(i));
                }
            }

			in.clear();
			//in.put("customers", customers);
            in.put("customers", customersWithLatLng);
			Map<String, Object> result_sorted_customers = dispatcher.runSync("computeOptimalSequence", in);
			List<GenericValue> sorted_customers = (List<GenericValue>)result_sorted_customers.get("route");

			Map<String,Long> sequenceNumMap = FastMap.newInstance();
			for (int i = 0; i < sorted_customers.size(); i++) {
                sequenceNumMap.put(sorted_customers.get(i).getString("customerId"), (long) (i + 1));
            }
            for (GenericValue gv : routeCustomersWithLatLng) {
			    gv.set("sequenceNum",sequenceNumMap.get(gv.getString("customerId")));
            }
            for (GenericValue gv : routeCustomersWithoutLatLng) {
			    gv.set("sequenceNum", null);
            }
            delegator.storeAll(routeCustomersWithLatLng);
            delegator.storeAll(routeCustomersWithoutLatLng);
			/*
			List<EntityCondition> conds = FastList.newInstance();
			
			for(int i = 0; i < sorted_customers.size(); i++){
				GenericValue c = sorted_customers.get(i);
				conds.clear();
				conds.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS,routeId));
				conds.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
				conds.add(EntityCondition.makeCondition("customerId",EntityOperator.EQUALS,c.getString("customerId")));
				
				List<GenericValue> L = delegator.findList("RouteCustomer", 
						EntityCondition.makeCondition(conds), null,null,null,false);
				if(L != null && L.size() > 0){
					GenericValue rc = L.get(0);
					rc.put("sequenceNum", (long)(i+1));
					delegator.store(rc);
					Debug.log(module + "::generateCustomerSequenceOfRoute, UPDATE sequence num, routeId = " + routeId
							+ ", customerId = " + rc.getString("customerId") + ", SEQ = " + (i+1));
				}
			}
			*/
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retSucc;
	}
		
	public static Map<String, Object> updateCustomerSalesmanWithNewEntityModelDB(
			DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {

			//PrintWriter log = new PrintWriter("C:/tmp/dms.log.txt");

			List<EntityCondition> conds = FastList.newInstance();
			Map<String, String> mCustomer2Distributor = FastMap.newInstance();
			Map<String, String> mCustomer2Salesman = FastMap.newInstance();
			Map<String, String> mDistributor2Department = FastMap.newInstance();
			Map<String, String> mDepartment2SaleSup = FastMap.newInstance();
			Map<String, List<String>> mSalesman2Customers = FastMap.newInstance();
			
			HashSet<String> salesmans = new HashSet<String>();
			HashSet<String> distributors = new HashSet<String>();

			// get list of customers
			conds.add(EntityCondition.makeCondition("partyTypeId",
					EntityOperator.EQUALS, "RETAIL_OUTLET"));
			conds.add(EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, "PARTY_ENABLED"));
			List<GenericValue> lstCustomers = delegator.findList(
					"PartyAndPartyGroupDetail",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			// get customer and distributor
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId",
					EntityOperator.EQUALS, "CUSTOMER_REL"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "CUSTOMER"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DISTRIBUTOR"));
			List<GenericValue> lstCustomerAndDistributor = delegator.findList(
					"PartyRelationship", EntityCondition.makeCondition(conds),
					null, null, null, false);
			for (GenericValue cd : lstCustomerAndDistributor) {
				String customerId = cd.getString("partyIdFrom");
				String distributorId = cd.getString("partyIdTo");
				mCustomer2Distributor.put(customerId, distributorId);

				if (distributorId != null)
					distributors.add(distributorId);
			}

			// get department and distributor
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId",
					EntityOperator.EQUALS, "DISTRIBUTION"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "SALESSUP_DEPT"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DISTRIBUTOR"));
			List<GenericValue> lstDepartmentAndDistributor = delegator
					.findList("PartyRelationship",
							EntityCondition.makeCondition(conds), null, null,
							null, false);
			for (GenericValue dd : lstDepartmentAndDistributor) {
				String departmentId = dd.getString("partyIdFrom");
				String distributorId = dd.getString("partyIdTo");
				mDistributor2Department.put(distributorId, departmentId);
			}

			// get saleSup and department
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId",
					EntityOperator.EQUALS, "MANAGER"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "MANAGER"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
			List<GenericValue> lstSaleSupAndDepartment = delegator.findList(
					"PartyRelationship", EntityCondition.makeCondition(conds),
					null, null, null, false);
			for (GenericValue ssd : lstSaleSupAndDepartment) {
				String supId = ssd.getString("partyIdFrom");
				String departmentId = ssd.getString("partyIdTo");
				mDepartment2SaleSup.put(departmentId, supId);
			}

			// get list Salesman and Customer
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId",
					EntityOperator.EQUALS, "SALES_REP_REL"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "SALES_EXECUTIVE"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "CUSTOMER"));
			List<GenericValue> lstSalemanAndCustomer = delegator.findList(
					"PartyRelationship", EntityCondition.makeCondition(conds),
					null, null, null, false);
			for (GenericValue sc : lstSalemanAndCustomer) {
				String salesmanId = sc.getString("partyIdFrom");
				String customerId = sc.getString("partyIdTo");
				mCustomer2Salesman.put(customerId, salesmanId);
				
				if (salesmanId != null){
					salesmans.add(salesmanId);
					if(mSalesman2Customers.get(salesmanId) == null)
						mSalesman2Customers.put(salesmanId, new ArrayList<String>());
					mSalesman2Customers.get(salesmanId).add(customerId);
				}
			}

			// extract and update into DB
			for (GenericValue c : lstCustomers) {
				String customerId = c.getString("partyId");
				String partyTypeId = c.getString("partyTypeId");
				String fullName = c.getString("groupName");
				String partyCode = c.getString("partyCode");

				String salesmanId = mCustomer2Salesman.get(customerId);
				String distributorId = mCustomer2Distributor.get(customerId);
				String departmentId = null;

				if (distributorId != null && !distributorId.equals(""))
					departmentId = mDistributor2Department.get(distributorId);
				String supId = null;
				if (departmentId != null && !departmentId.equals(""))
					supId = mDepartment2SaleSup.get(departmentId);

				//log.println(customerId + "\t" + partyTypeId + "\t" + salesmanId
				//		+ "\t" + distributorId + "\t" + departmentId + "\t"
				//		+ supId);
				//System.out.println(customerId + "\t" + partyTypeId + "\t"
				//		+ salesmanId + "\t" + distributorId + "\t"
				//		+ departmentId + "\t" + supId);

				GenericValue pc = delegator.findOne("PartyCustomer",
						UtilMisc.toMap("partyId", customerId), false);
				if (pc == null) {
					pc = delegator.makeValue("PartyCustomer");
					pc.put("partyId", customerId);
					pc.put("partyTypeId", partyTypeId);
					pc.put("fullName", fullName);
					pc.put("partyCode", partyCode);
					pc.put("statusId", "PARTY_ENABLED");
					if (distributorId != null)
						pc.put("distributorId", distributorId);
					if (salesmanId != null)
						pc.put("salesmanId", salesmanId);
					if (supId != null)
						pc.put("supervisorId", supId);
					pc.put("visitFrequencyTypeId", "F0"); //default is F0

					delegator.create(pc);
				}
			}

			for (String d : distributors) {
				//log.println(d);
				GenericValue dis = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId",d), false);
				String depId = mDistributor2Department.get(d);
				String supId = null;
				if(depId != null)
					supId = mDepartment2SaleSup.get(depId);
				System.out.println("PREPARE Add Distributor " + d + ", deptId = " + depId + ", supId = " + supId);
				if(dis != null){
					GenericValue pd = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId",d),false);
					if(pd == null){
						pd = delegator.makeValue("PartyDistributor");
						pd.put("partyId", d);
						pd.put("fullName", dis.getString("groupName"));
						pd.put("partyCode", dis.getString("partyCode"));
						pd.put("statusId", "PARTY_ENABLED");
						if(supId != null)
							pd.put("supervisorId", supId);
						delegator.create(pd);
						
					}else{
						pd.put("partyId", d);
						pd.put("fullName", dis.getString("groupName"));
						pd.put("partyCode", dis.getString("partyCode"));
						pd.put("statusId", "PARTY_ENABLED");
						if(supId != null)
							pd.put("supervisorId", supId);
						delegator.store(pd);
						
					}
				}
			}
			for (String s : salesmans) {
				//log.println(s);
				String distributorId = null;
				String supId = null;
				for(String customerId: mSalesman2Customers.get(s)){
					if(mCustomer2Distributor.get(customerId) != null){
						distributorId = mCustomer2Distributor.get(customerId);
						break;
					}
				}
				String depId = null;
				if(distributorId != null)
					depId = mDistributor2Department.get(distributorId);
				if(depId != null)
					supId = mDepartment2SaleSup.get(depId);
				System.out.println("PREPARE Add salesman " + s + ", depId = " + depId + ", supId = " + supId + ", distributorId = " + distributorId);
				GenericValue sm = delegator.findOne("Party", UtilMisc.toMap("partyId",s), false);
				if(sm != null){
					GenericValue ps = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId",s), false);
					if(ps == null){
						ps = delegator.makeValue("PartySalesman");
						ps.put("partyId", s);
						ps.put("fullName", sm.getString("description"));
						ps.put("partyCode", sm.getString("partyCode"));
						ps.put("statusId", "PARTY_ENABLED");
						if(distributorId != null)
							ps.put("distributorId", distributorId);
						if(supId != null)
							ps.put("supervisorId", supId);
						
						delegator.create(ps);
					}else{
						ps.put("partyId", s);
						ps.put("fullName", sm.getString("description"));
						ps.put("partyCode", sm.getString("partyCode"));
						ps.put("statusId", "PARTY_ENABLED");
						if(distributorId != null)
							ps.put("distributorId", distributorId);
						if(supId != null)
							ps.put("supervisorId", supId);
						
						delegator.store(ps);
					}
				}
			}
			//log.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getScheduleDatesOfCustomer(
			DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

        String customerId = null;
        if (parameters.containsKey("customerId") && parameters.get("customerId").length > 0) {
            customerId = parameters.get("customerId")[0];
        }
        try {
            if (customerId == null) {
                return ServiceUtil.returnError("getScheduleDatesOfCustomer not has: customerId");
            }
            listAllConditions.add(EntityCondition.makeCondition("customerId",
                    EntityOperator.EQUALS, customerId));
            opts.setDistinct(true);
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "RouteScheduleDetailDateAndSchedule", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getScheduleDatesOfCustomer service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> generateSaleRouteScheduleDetailDate(
			DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String routeId = (String) context.get("routeId");
        String fromDateT = (String)context.get("fromDate");
        String thruDateT = (String)context.get("thruDate");
		Date fromDate = new Date(Long.parseLong(fromDateT));
		Date thruDate = new Date(Long.parseLong(thruDateT));
		Debug.log(module + "::generateSaleRouteScheduleDetailDate, routeId = "
				+ routeId);
		try {
            // remove old schedule detail
            Map<String, Object> in = FastMap.newInstance();
            in.put("routeId", routeId);
            Map<String, Object> rs = dispatcher.runSync("removeRouteScheduleDetailDate", in);

            GenericValue route = delegator.findOne("Route",
					UtilMisc.toMap("routeId", routeId), false);
			if (route == null) {
				return ServiceUtil.returnError("Route NOT exists");
			}
			String salesmanId = route.getString("executorId");

			List<EntityCondition> conds = FastList.newInstance();
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
			conds.add(EntityUtil.getFilterByDateExpr());
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

			for (GenericValue c : lstCustomers) {
				String visitFrequencyTypeId = (String) c
						.get("visitFrequencyTypeId");
				if(!visitFrequencyTypeId.equals("F0")) continue;// tam thoi chua xu ly F1, F2, F3, F4
				
				String customerId = (String) c.get("customerId");
				Long seq = c.getLong("sequenceNum");
				for (int i = 0; i < sche_dates.size(); i++) {
					String salesRouteScheduleId = salesRouteScheduleIds.get(i);
					List<Date> sel_dates = sche_dates.get(i);////SupUtil.filterDates(sche_dates.get(i), visitFrequencyTypeId);
					for (Date d : sel_dates) {
                        GenericValue rsdd = delegator.makeValue("RouteScheduleDetailDate");
                        rsdd.put("date", d);
                        rsdd.put("customerId", customerId);
                        rsdd.put("sequenceNum", seq);
                        rsdd.put("routeId", routeId);
                        rsdd.put("salesmanId", salesmanId);
                        rsdd.put("salesRouteScheduleId", salesRouteScheduleId);
                        delegator.create(rsdd);
                    }
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			retSucc.put("message","error");
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRouteAddresses(DispatchContext dpct,
			Map<String, Object> context) throws GenericEntityException {
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", userLogin);
			input.put("partyId", partyId);
			Map<String, Object> outsup = dispatcher.runSync(
					"jqxGetAddressFamily", input);
			List<Map<String, Object>> listAddress = (List<Map<String, Object>>) outsup
					.get("listAddress");
			result.put("listIterator", listAddress);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get customer");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomersNoRoute(DispatchContext dpct,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, Object> result = FastMap.newInstance();
		EntityListIterator list = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
		int start = pagenum * pagesize + 1;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = userLogin.getString("partyId");
			Map<String, Object> inSup = FastMap.newInstance();
			inSup.put("userLogin", userLogin);
			inSup.put("partyId", partyId);
			Map<String, Object> outsup = dispatcher.runSync(
					"getListDistributorIdBySup", inSup);
			List<String> distributor = (List<String>) outsup
					.get("listDistributorIds");
/*			List<String> routes = SupUtil.getAllRoute(delegator,
					userLogin.getString("userLoginId"));*/
			List<String> routes = SupUtil.getAllRoute(delegator, partyId);
			if (UtilValidate.isNotEmpty(routes)) {
				EntityFindOptions opc = new EntityFindOptions();
				opc.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
				EntityListIterator listC = delegator.find("PartyRelationship",
						EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("partyIdFrom",
										EntityOperator.IN, routes), EntityUtil
										.getFilterByDateExpr(), EntityCondition
										.makeCondition("roleTypeIdFrom",
												"ROUTE"), EntityCondition
										.makeCondition("roleTypeIdTo",
												"CUSTOMER"), EntityCondition
										.makeCondition(
												"partyRelationshipTypeId",
												"SALES_ROUTE"))), null, null,
						null, opc);
				int sz = listC.getResultsTotalSize();
				if (sz != 0) {
					List<String> cust = FastList.newInstance();
					GenericValue ew = null;
					while ((ew = listC.next()) != null) {
						cust.add(ew.getString("partyIdTo"));
					}
					if (UtilValidate.isNotEmpty(cust)) {
						listAllConditions.add(EntityCondition.makeCondition(
								"partyIdFrom", EntityOperator.NOT_IN, cust));
					}
				}
				listC.close();
			} else {
				return ServiceUtil.returnError("Empty route");
			}
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("statusId",
							"PARTY_ENABLED"), EntityCondition.makeCondition(
							"statusId", null)), EntityOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "CUSTOMER"));
			listAllConditions.add(EntityCondition.makeCondition(
					"partyRelationshipTypeId", "CUSTOMER_REL"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"DISTRIBUTOR"));
			// Get all party in distributor
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo",
					EntityOperator.IN, distributor));

			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			list = delegator.find("PartyRelationshipCustomerDistributor", cond,
					null, null, listSortFields, opt);
			List<GenericValue> tmpList = list.getPartialList(start, pagesize);
			List<Map<String, Object>> resList = FastList.newInstance();
			for (GenericValue e : tmpList) {
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				o.put("address",
						SupUtil.getCustomerAddress(delegator,
								e.getString("customerPartyId")));
				resList.add(o);
			}
			int size = list.getResultsTotalSize();
			result.put("TotalRows", String.valueOf(size));
			result.put("listIterator", resList);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get customer");
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPotentialCustomerRelateRoute(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, Object> result = FastMap.newInstance();
		EntityListIterator list = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
		int start = pagenum * pagesize + 1;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = userLogin.getString("partyId");
			Map<String, Object> inSup = FastMap.newInstance();
			inSup.put("userLogin", userLogin);
			inSup.put("partyId", partyId);
			Map<String, Object> outsup = dispatcher.runSync(
					"getListDistributorIdBySup", inSup);
			List<String> distributor = (List<String>) outsup
					.get("listDistributorIds");
			context.put("distributor", distributor);
			Map<String, Object> tt1 = getListRouteDistributorResultSet(dpct,
					context);
			EntityListIterator listI = (EntityListIterator) tt1
					.get("listIterator");
			if (listI != null) {
				List<GenericValue> routes = listI.getCompleteList();
				List<String> routeList = FastList.newInstance();
				for (GenericValue e : routes) {
					routeList.add(e.getString("partyId"));
				}
				if (UtilValidate.isNotEmpty(routeList)) {
					EntityFindOptions opc = new EntityFindOptions();
					opc.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
					EntityListIterator listC = delegator.find(
							"PartyRelationship", EntityCondition
									.makeCondition(UtilMisc.toList(
											EntityCondition.makeCondition(
													"partyIdFrom",
													EntityOperator.IN,
													routeList), EntityUtil
													.getFilterByDateExpr())),
							null, null, null, opc);
					int sz = listC.getResultsTotalSize();
					if (sz != 0) {
						List<String> cust = FastList.newInstance();
						GenericValue ew = null;
						while ((ew = listC.next()) != null) {
							cust.add(ew.getString("partyIdTo"));
						}
						if (UtilValidate.isNotEmpty(cust)) {
							listAllConditions.add(EntityCondition
									.makeCondition("partyIdFrom",
											EntityOperator.NOT_IN, cust));
						}
					}
					listC.close();
				}
				listI.close();
			} else {
				return ServiceUtil.returnError("Empty route");
			}
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("statusId",
							"PARTY_ENABLED"), EntityCondition.makeCondition(
							"statusId", null)), EntityOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "CUSTOMER"));
			listAllConditions.add(EntityCondition.makeCondition(
					"partyRelationshipTypeId", "CUSTOMER_REL"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"DISTRIBUTOR"));
			// Get all party in distributor
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo",
					EntityOperator.IN, distributor));

			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			list = delegator.find("PartyRelationshipCustomerDistributor", cond,
					null, null, listSortFields, opt);
			List<GenericValue> tmpList = list.getPartialList(start, pagesize);
			List<Map<String, Object>> resList = FastList.newInstance();
			for (GenericValue e : tmpList) {
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				o.put("address",
						SupUtil.getCustomerAddress(delegator,
								e.getString("partyId")));
				resList.add(o);
			}
			result.put("listIterator", resList);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get customer");
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	public static Map<String, Object> getCustomerInRouteWithLatLng(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String routeId = (String) context.get("routeId");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("routeId",
					EntityOperator.EQUALS, routeId));
			conds.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.EQUALS, null));

			List<GenericValue> lst = delegator.findList("RouteCustomerView",
					EntityCondition.makeCondition(conds), null, UtilMisc.toList("customerId"), null,
					false);

			result.put("results", lst);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;

		/*
		 * Delegator delegator = (Delegator) dpct.getDelegator(); Map<String,
		 * Object> result = ServiceUtil.returnSuccess(); try {
		 * 
		 * List<EntityCondition> listAllConditions = FastList.newInstance();
		 * String routeId = (String) context.get("routeId");
		 * listAllConditions.add(EntityCondition.makeCondition("routeId",
		 * routeId)); listAllConditions.add(EntityCondition.makeCondition(
		 * "routeStatusId", "PARTY_ENABLED"));
		 * listAllConditions.add(EntityCondition.makeCondition(
		 * "customerStatusId", "PARTY_ENABLED"));
		 * listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDate",
		 * "thruDate")); EntityCondition cond = EntityCondition.makeCondition(
		 * listAllConditions, EntityJoinOperator.AND); List<GenericValue>
		 * resList = delegator.findList("CustomerRoutes", cond, null, null,
		 * null, false);
		 * 
		 * Debug.log(module + "::getCustomerInRouteWithLatLng, resList.sz = " +
		 * resList.size()); result.put("results", resList); } catch (Exception
		 * e) { Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError(e.getMessage()); } return result;
		 */
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerInRouteNoLatLng(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			List<EntityCondition> listAllConditions = FastList.newInstance();
			Map<String, Object> hasLatLng = getCustomerInRouteWithLatLng(dpct,
					context);
			List<Map<String, Object>> listLatLng = (List<Map<String, Object>>) hasLatLng
					.get("results");
			if (UtilValidate.isNotEmpty(listLatLng)) {
				List<String> party = FastList.newInstance();
				for (Map<String, Object> e : listLatLng) {
					party.add((String) e.get("partyIdTo"));
				}
				if (UtilValidate.isNotEmpty(party)) {
					listAllConditions.add(EntityCondition.makeCondition(
							"partyIdTo", EntityOperator.NOT_IN, party));
				}
			}

			String routeId = (String) context.get("routeId");
			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom",
					routeId));
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "ROUTE"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"CUSTOMER"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			Set<String> fieldsToSelect = UtilMisc.toSet("partyIdTo",
					"disGroupName", "partyIdFrom");
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			List<GenericValue> tmpList = delegator.findList(
					"PartyRelationshipCustomerDistributor", cond,
					fieldsToSelect, UtilMisc.toList("partyIdTo"), null, false);
			List<Map<String, Object>> resList = FastList.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			for (GenericValue e : tmpList) {
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				Map<String, Object> fkc = dispatcher
						.runSync("getPartyPostalAddress",
								UtilMisc.toMap("userLogin", userLogin,
										"partyId", e.getString("partyIdTo"),
										"contactMechPurposeTypeId",
										"SHIPPING_LOCATION"));
				String contactMechId = UtilValidate.isNotEmpty(fkc)
						&& ServiceUtil.isSuccess(fkc) ? (String) fkc
						.get("contactMechId") : null;
				if (UtilValidate.isNotEmpty(contactMechId)) {
					GenericValue postalAddress = delegator.findOne(
							"PostalAddressAndGeo",
							UtilMisc.toMap("contactMechId", contactMechId),
							false);
					if (UtilValidate.isNotEmpty(postalAddress)) {
						o.put("address", postalAddress);
					}
				}
				resList.add(o);
			}
			result.put("results", resList);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> getCustomerInRouteResultSet(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		EntityListIterator list = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
		int start = pagenum * pagesize + 1;
		try {
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "ROUTE"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"CUSTOMER"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDate",
					"thruDate"));
			Set<String> fieldsToSelect = UtilMisc.toSet("partyIdTo",
					"disGroupName", "partyIdFrom");
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			opt.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			opt.setDistinct(true);
			list = delegator.find("PartyRelationshipCustomerDistributor", cond,
					null, fieldsToSelect, UtilMisc.toList("partyIdTo"), opt);
			result.put("TotalRows", String.valueOf(list.getResultsTotalSize()));
			List<GenericValue> tmpList = list.getPartialList(start, pagesize);
			List<Map<String, Object>> resList = FastList.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			for (GenericValue e : tmpList) {
				Map<String, Object> o = FastMap.newInstance();
				Map<String, Object> fkc = dispatcher
						.runSync("getPartyPostalAddress",
								UtilMisc.toMap("userLogin", userLogin,
										"partyId", e.getString("partyIdTo"),
										"contactMechPurposeTypeId",
										"SHIPPING_LOCATION"));
				String contactMechId = (String) fkc.get("contactMechId");
				if (UtilValidate.isNotEmpty(contactMechId)) {
					GenericValue postalAddress = delegator.findOne(
							"PostalAddressAndGeo",
							UtilMisc.toMap("contactMechId", contactMechId),
							false);
					if (UtilValidate.isNotEmpty(postalAddress)) {
						o.put("address", postalAddress);
						o.put("latitude", postalAddress.getString("latitude"));
						o.put("longitude", postalAddress.getString("longitude"));
					}
				}
				o.putAll(e);
				resList.add(o);
			}
			result.put("listIterator", resList);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListSalesmanManagement(DispatchContext dpct, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator list = null;
		try {
            opt.setDistinct(true);
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            } else {
                isSearch = false;
            }

            if (isSearch){
                if (UtilValidate.isEmpty(listSortFields)) {
                    listSortFields.add("partyCode");
                }
                listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"),
                        EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
                EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
                List<String> fieldToSelect = UtilMisc.toList("partyId","partyCode", "fullName");
                list = EntityMiscUtil.processIterator(parameters,successResult,delegator,"PartySalesman",cond,null,UtilMisc.toSet(fieldToSelect),listSortFields,opt);
            }
            successResult.put("listIterator", list);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get getListSalesmanManagement");
		}
		return successResult;
	}

	/**
	 * get all route sales
	 */
	public static Map<String, Object> getAllRoute(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		try {
			List<String> routeIds = SupUtil.getAllRoute(delegator);
			res.put("results", routeIds);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}

		return res;
	}

	/*
	 * description : get list route of all npp
	 * 
	 * @param : DispatchContext
	 * 
	 * @param : context
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRouteDistributorResultSet(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, Object> result = FastMap.newInstance();
		EntityListIterator list = null;
		try {
			List<String> distributor = (List<String>) context
					.get("distributor");
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(
					"partyStatusId", "PARTY_ENABLED"));
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId",
					"PARTY_GROUP"));
			listAllConditions.add(EntityCondition.makeCondition(
					"roleTypeIdFrom", "SALES_EXECUTIVE"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo",
					"ROUTE"));
			listAllConditions.add(EntityCondition.makeCondition(
					"partyRelationshipTypeId", "SALES_ROUTE"));

			if (UtilValidate.isNotEmpty(distributor)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"partyIdTo", EntityOperator.IN, distributor));
			}
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			list = delegator.find("PartyRelationshipAndDetail", cond, null,
					null, listSortFields, opt);
			result.put("listIterator", list);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get list routedetail");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRoute(DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> result = ServiceUtil.returnSuccess();//FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			//process to filter routeSchedule
			String regex = "[A-Z]{5,}";
			Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
			List<EntityCondition> conds = FastList.newInstance();
			for (EntityCondition cond: listAllConditions) {
				String condStr = cond.toString();
				if (condStr.contains("scheduleRoute") && condStr.contains("AND")) {
					listAllConditions.remove(cond);
				} else if (condStr.contains("scheduleRoute")) {
					listAllConditions.remove(cond);
					Matcher matcher = pattern.matcher(condStr);
					while (matcher.find()) {
						conds.add(EntityCondition.makeCondition("scheduleRoute", EntityOperator.LIKE, "%"+matcher.group(0)+"%"));
					}
					listAllConditions.add(EntityCondition.makeCondition(conds, EntityJoinOperator.OR));
				}
			}
            //end process to filter routeSchedule
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = userLogin.getString("partyId");
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("executorId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("managerId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            } else {
                isSearch = false;
            }

            if (isSearch){
                listAllConditions.add(EntityCondition.makeCondition("statusId", SupUtil.ROUTE_ENABLED));
                EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
                listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "RouteViewDetail",
                        cond, null, null, listSortFields, opts);
            }
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get getListRoute");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRouteHistory(DispatchContext dpct,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		parameters = (Map<String, String[]>) (parameters == null ? FastMap
				.newInstance() : parameters);
		// int pagenum = Integer.parseInt((String)
		// parameters.get("pagenum")[0]);
		// int pagesize = Integer.parseInt((String)
		// parameters.get("pagesize")[0]);
		Map<String, Object> result = FastMap.newInstance();
		/*EntityListIterator list = null;*/
		List<GenericValue> list = null;
		String bounds = context.containsKey("bounds") ? (String) context
				.get("bounds") : null;
		String pid = context.containsKey("partyId") ? (String) context
				.get("partyId") : null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			opts = opts != null ? opts : new EntityFindOptions();
			opts.setDistinct(true);
			opts.setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
			if (listAllConditions != null)
				listAllConditions = SupUtil.buildConditionFindRoute(delegator,
						listAllConditions, opts);
			else
				listAllConditions = FastList.newInstance();

			if (pid != null) {
				List<String> idList = FastList.newInstance();
				JSONArray jarr = new JSONArray();
				jarr = JSONArray.fromObject(pid);
				if (!jarr.isEmpty())
					for (int i = 0; i < jarr.size(); i++) {
						JSONObject obj = (JSONObject) jarr.get(i);
						String id = (String) obj.values().iterator().next();
						if (!id.equals("") && !id.equals("undefined")
								&& !id.equals("null"))
							idList.add(id);
					}

/*				if (!idList.isEmpty())
					listAllConditions.add(EntityCondition.makeCondition(
							"partyId", EntityJoinOperator.IN, idList));
			}

			listAllConditions.add(EntityCondition.makeCondition(
					"createdByUserLogin", userLoginId));
			listAllConditions.add(EntityCondition.makeCondition("statusId",
					"PARTY_ENABLED"));
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId",
					"SALES_ROUTE"));
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			list = delegator.find("RouteDetail", cond, null, null,
					listSortFields, opts);*/
				if (!idList.isEmpty())
					listAllConditions.add(EntityCondition.makeCondition(
							"routeId", EntityJoinOperator.IN, idList));
			}

			listAllConditions.add(EntityCondition.makeCondition(
					"managerId", userLogin.getString("partyId")));
			listAllConditions.add(EntityCondition.makeCondition("statusId",
					"ROUTE_ENABLED"));
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			list = delegator.findList("Route", cond, null, listSortFields, opts, false);
			// int start = pagenum*pagesize + 1;
			/*List<GenericValue> listTmp = null;*/

			if (bounds != null)
				return null;
			// listTmp = list.getPartialList(start, pagesize);
			/*else
				listTmp = list.getCompleteList();*/

			List<Map<String, Object>> resList = FastList.newInstance();

			List<Map<String, List<Map<String, Object>>>> listDetail = FastList
					.newInstance();

			for (GenericValue e : list) {
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				List<GenericValue> employee = delegator.findList(
						"PartyRelationShipAndPerson", EntityCondition
								.makeCondition(UtilMisc.toList(EntityCondition
										.makeCondition("roleTypeIdFrom",
												"SALES_EXECUTIVE"),
										EntityCondition.makeCondition(
												"roleTypeIdTo", "ROUTE"),
										EntityCondition.makeCondition(
												"partyIdTo",
												e.getString("partyId")),
										EntityUtil.getFilterByDateExpr())),
						UtilMisc.toSet("partyCode", "fromDate"), UtilMisc
								.toList("-fromDate"), opts, false);
				List<GenericValue> sch = delegator.findList("RouteSchedule",
						EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("routeId",
										e.getString("partyId")),
								EntityUtil.getFilterByDateExpr())), null,
						UtilMisc.toList("scheduleRoute"), null, false);

				List<Map<String, Object>> listCustomerInRoute = FastList
						.newInstance();
				context.put("routeId", e.get("partyId"));
				listCustomerInRoute
						.addAll((Collection<? extends Map<String, Object>>) getCustomerInRouteWithLatLng(
								dpct, context).get("results"));
				listCustomerInRoute
						.addAll((Collection<? extends Map<String, Object>>) getCustomerInRouteNoLatLng(
								dpct, context).get("results"));

				if (UtilValidate.isNotEmpty(sch)) {
					o.put("scheduleRoute", sch);
				}
				o.put("employeeId", employee);

				resList.add(o);

				parameters.put("partyId",
						new String[] { (String) o.get("partyId") });
				context.put("parameters", parameters);
				List<Map<String, Object>> listAddress = FastList.newInstance();
				listAddress.add(UtilMisc.<String, Object> toMap(
						"customerInRoute", listCustomerInRoute));
				Map<String, Object> address = SupServices.getRouteAddresses(
						dpct, context);

				if (address.containsKey("listIterator"))
					listAddress
							.addAll((Collection<? extends Map<String, Object>>) address
									.get("listIterator"));

				Map<String, List<Map<String, Object>>> mapTpx = FastMap
						.newInstance();
				listAddress.add(o);
				mapTpx.put((String) o.get("partyId"), listAddress);
				listDetail.add(mapTpx);
			}
			result.put("listroute", listDetail);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get list routedetail");
		} /*finally {
			if (list != null) {
				list.close();
			}
		}*/
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRouteView(DispatchContext dpct,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
		int start = pagenum * pagesize;
		Map<String, Object> result = FastMap.newInstance();
		EntityListIterator list = null;
		String[] parties = parameters.get("partyId");
		List<Map<String, Object>> resList = FastList.newInstance();
		try {

			if (UtilValidate.isNotEmpty(parties)) {
				String partyId = parties[0];
				GenericValue userLogin = (GenericValue) context
						.get("userLogin");
				EntityFindOptions opts = new EntityFindOptions();
				opts.setDistinct(true);

				String userLoginPartyId = userLogin.getString("partyId");
				List<GenericValue> listTmp = null;
				if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
					Map<String, Object> input = FastMap.newInstance();
					input.put("partyId", partyId);
					input.put("userLogin", userLogin);
					Map<String, Object> outp = dispatcher.runSync(
							"getAllRouteOwnedBy", input);
					listTmp = (List<GenericValue>) outp.get("results");
				} else {
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition("partyId",
							userLoginPartyId));
					listCond.add(EntityCondition.makeCondition("statusId",
							"PARTY_ENABLED"));
					listCond.add(EntityUtil.getFilterByDateExpr("SRFromDate",
							"SRThruDate"));
					EntityFindOptions options = new EntityFindOptions();
					options.setDistinct(true);
					Set<String> fields = UtilMisc
							.toSet("partyId", "partyCode", "groupName",
									"routeId", "description", "SRFromDate");
					fields.add("RoutePartyCode");
					listTmp = delegator.findList(
							"PartyRelationshipRouteDetail",
							EntityCondition.makeCondition(listCond,
									EntityOperator.AND), fields, UtilMisc
									.toList("-SRFromDate"), options, false);
				}

				int end = listTmp.size();
				if (end > 0) {
					int to = start + pagesize;
					to = to < end ? to : end;
					listTmp = listTmp.subList(start, to);
				}

				for (GenericValue e : listTmp) {
					Map<String, Object> o = FastMap.newInstance();
					o.putAll(e);
					List<GenericValue> sch = delegator
							.findList("RouteSchedule", EntityCondition
									.makeCondition(UtilMisc.toList(
											EntityCondition.makeCondition(
													"routeId",
													e.getString("routeId")),
											EntityUtil.getFilterByDateExpr())),
									null, UtilMisc.toList("scheduleRoute"),
									null, false);
					o.put("partyCode", e.getString("RoutePartyCode"));
					if (UtilValidate.isNotEmpty(sch)) {
						o.put("scheduleRoute", sch);
					}
					resList.add(o);
				}
				result.put("TotalRows", String.valueOf(end));
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get list routedetail");
		} finally {
			if (list != null) {
				list.close();
			}
		}
		result.put("listIterator", resList);
		return result;
	}

	/*
	 * delete route
	 * 
	 * @param DispatchContext
	 */
	public static Map<String, Object> deleteRoute(DispatchContext dpct, Map<String, ? extends Object> context){
		Delegator delegator = dpct.getDelegator();
		String routeId = (String) context.get("routeId");
		Map<String,Object> successReturn = ServiceUtil.returnSuccess();
		List<EntityCondition> conds = FastList.newInstance();
		try {
		    if (routeId == null) {
		        ServiceUtil.returnError("error deleteRoute");
            }
            //Disable route
            GenericValue aRoute = delegator.findOne("Route", UtilMisc.toMap("routeId", routeId),false);
            aRoute.set("statusId", SupUtil.ROUTE_DISABLED);
            delegator.store(aRoute);

            //Thrudate SalesRouteSchedule, disable
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> salesRouteSchedules = delegator.findList("SalesRouteSchedule", EntityCondition.makeCondition(conds), null, null, null, false);
            for (GenericValue gv : salesRouteSchedules) {
                gv.set("thruDate", UtilDateTime.nowTimestamp());
                gv.set("statusId", SupUtil.DISABLED);
            }
            delegator.storeAll(salesRouteSchedules);

            //Disable RouteCustomer
            conds.clear();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            for (GenericValue gv : routeCustomers) {
                gv.set("thruDate", UtilDateTime.nowTimestamp());
            }
            delegator.storeAll(routeCustomers);

            //Delete RouteScheduleDetailDate
            conds.clear();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            List<GenericValue> routeScheduleDetailDates = delegator.findList("RouteScheduleDetailDate", EntityCondition.makeCondition(conds), null, null, null, false);
            delegator.removeAll(routeScheduleDetailDates);
        } catch (Exception e) {
            ServiceUtil.returnError("error deleteRoute");
        }
		return successReturn;
	}

	/*
	 * createRoute
	 * 
	 * @param DispatchContext,Map<?,?>
	 */
	public static Map<String, Object> createRoute(DispatchContext dpct,
			Map<?, ?> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String routeName = (String) context.get("routeName");
		String description = (String) context.get("description");
		String scheduleRoute = (String) context.get("scheduleRoute");
		String partyCode = (String) context.get("routeCode");
		String employeeId = (String) context.get("salesmanId");
        String weeks = (String) context.get("weeks");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
        String managerId = userLogin.getString("partyId");

		try {
			// Debug.log(module + "::createRoute, routeName = " + routeName +
			// ", description = " + description
			// + ", scheduleRoute = " + scheduleRoute + ", partyCode = " +
			// partyCode + ", employeeId = " + employeeId);
			List<GenericValue> lr = SupUtil.getRouteListFromCode(delegator,
					partyCode);
			if (lr != null && lr.size() > 0) {
				String errorMessage = UtilProperties.getMessage(resource, "BSRouteId", (Locale)context.get("locale"))  + " " + partyCode ;
				errorMessage += " " + UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSSomethingWasInExistence", (Locale)context.get("locale"));
				return ServiceUtil.returnError(errorMessage);
			}

			GenericValue r = delegator.makeValue("Route");
			String routeId = delegator.getNextSeqId("Route");
			r.put("routeId", routeId);
			r.put("routeName", routeName);
			r.put("routeCode", partyCode);
			r.put("description", description);
			Timestamp createDate = new Timestamp(System.currentTimeMillis());
			r.put("createdDate", createDate);
			r.put("createdByUserLoginId", userLoginId);
            r.put("managerId", managerId);
			r.put("executorId", employeeId);
            r.put("weeks", weeks);
			r.put("statusId", SupUtil.ROUTE_ENABLED);
			delegator.create(r);

			// create schedule
			scheduleRoute = scheduleRoute.substring(1,
					scheduleRoute.length() - 1);
			String[] day = scheduleRoute.split(",");
			if (day != null && day.length > 0)
				for (int i = 0; i < day.length; i++) {
					GenericValue sr = delegator.makeValue("SalesRouteSchedule");
					String salesRouteScheduleId = delegator
							.getNextSeqId("SalesRouteSchedule");
					sr.put("salesRouteScheduleId", salesRouteScheduleId);
					sr.put("routeId", routeId);
					String scheduleDay = day[i].trim();
					scheduleDay = scheduleDay.substring(1,
							scheduleDay.length() - 1);
					sr.put("scheduleRoute", scheduleDay);
					// Debug.log(module +
					// "::createRoute, create SalesRouteSchedule, day = " +
					// day[i]);
					sr.put("fromDate", new Timestamp(System.currentTimeMillis()));
					sr.put("statusId", SupUtil.ENABLED);
					delegator.create(sr);
				}

			res.put("routeId", routeId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;

		/*
		 * Delegator delegator = (Delegator) dpct.getDelegator();
		 * LocalDispatcher dispatcher = dpct.getDispatcher(); String groupName =
		 * (String) context.get("groupName"); String description = (String)
		 * context.get("description"); String scheduleRoute = (String)
		 * context.get("scheduleRoute"); String partyCode = (String)
		 * context.get("partyCode"); String employeeId = (String)
		 * context.get("employeeId"); Map<String, Object> res =
		 * ServiceUtil.returnSuccess(); GenericValue userLogin = (GenericValue)
		 * context.get("userLogin"); try { //create Party Group
		 * Map<String,Object> map = FastMap.newInstance(); map.put("userLogin",
		 * userLogin); map.put("groupName", groupName); map.put("partyTypeId",
		 * "SALES_ROUTE");
		 * 
		 * String partyId; try { Map<String, Object> out =
		 * dispatcher.runSync("createPartyGroup", map); partyId = (String)
		 * out.get("partyId"); GenericValue party = delegator.findOne("Party",
		 * UtilMisc.toMap("partyId", partyId), false); party.set("partyCode",
		 * partyCode); party.store(); res.put("partyId", partyId); } catch
		 * (Exception e) { Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError(e.getMessage()); }
		 * if(UtilValidate.isNotEmpty(partyId)){ try { Map<String,Object>
		 * mapRole = FastMap.newInstance(); mapRole.put("userLogin",userLogin);
		 * mapRole.put("partyId", partyId); mapRole.put("roleTypeId", "ROUTE");
		 * dispatcher.runSync("createPartyRole", mapRole); } catch (Exception e)
		 * { e.printStackTrace(); return
		 * ServiceUtil.returnError(e.getMessage()); } }
		 * if(UtilValidate.isNotEmpty(scheduleRoute)){ JSONArray routes =
		 * JSONArray.fromObject(scheduleRoute); for(int i = 0; i <
		 * routes.size(); i++){ SupUtil.createAndUpdateRouteSchedule(delegator,
		 * partyId, routes.getString(i), UtilDateTime.nowTimestamp(), null); } }
		 * SupUtil.createAndUpdateRouteInformation(delegator, partyId,
		 * description); //create relationship route vs salesman
		 * if(UtilValidate.isNotEmpty(employeeId)){
		 * SupUtil.createAndUpdateRouteSalesmanRelationship(delegator,
		 * dispatcher, partyId, employeeId, true, true); } }
		 * catch(GenericEntityException e){ Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError("Failed when create route"); } catch
		 * (Exception e) { Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError("Failed when create route"); } return res;
		 */

	}

	/*
	 * update Route
	 * 
	 * @param DispatchContext
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static Map<String, Object> removeSaleRouteScheduleAndDetailDate(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String routeId = (String)context.get("routeId");
		try{
			Map<String, Object> rs = SupUtil.removeSaleRouteScheduleAndDetailDate(delegator, routeId);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	public static Map<String, Object> removeRouteScheduleDetailDate(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String routeId = (String)context.get("routeId");
		try{
			Map<String, Object> rs = SupUtil.removeRouteScheduleDetailDate(delegator, routeId);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> addSaleRouteScheduleDate(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String routeId = (String)context.get("routeId");
		List<String> scheduleDates = (List<String>)context.get("scheduleDates");
		try{
			Map<String, Object> rs = SupUtil.addSaleRouteScheduleDate(delegator, routeId, scheduleDates);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateRoute(DispatchContext dpct,
			Map<String, ? extends Object> context) {

		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String routeId = (String) context.get("routeId");
		String routeCode = (String) context.get("routeCode");
		String routeName = (String) context.get("routeName");
		String description = (String) context.get("description");
		String sche = (String) context.get("scheduleRoute");
        String weeks = (String) context.get("weeks");
		//String employeeId = (String) context.get("salesmanId");  //Khong cho phep doi salesman tai day
		
		Locale locale = (Locale) context.get("locale");
		GenericValue curl = (GenericValue) context.get("userLogin");
		try {
			GenericValue route = delegator.findOne("Route", UtilMisc.toMap("routeId", routeId), false);
            route.set("routeCode", routeCode);
            route.set("routeName", routeName);
            route.set("description", description);
            route.set("weeks", weeks);
			route.store();
			
			// remove old schedule date and detail
			Map<String, Object> in = FastMap.newInstance();
			in.put("routeId", routeId);
			Map<String, Object> rs = dispatcher.runSync("removeSaleRouteScheduleAndDetailDate", in);
			
			List<String> schedule = FastList.newInstance();
			JSONArray routes = JSONArray.fromObject(sche);
			for (int i = 0; i < routes.size(); i++) {
				schedule.add(routes.getString(i));
			}
			
			// add new schedule date
			in.clear();
			in.put("routeId", routeId);
			in.put("scheduleDates", schedule);
			rs = dispatcher.runSync("addSaleRouteScheduleDate", in);

			/*
			// generate detail schedule dates
			in.clear();
			in.put("routeId", routeId);
			rs = dispatcher.runSync("generateSaleRouteScheduleDetailDate", in);
			*/
						
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"updateRouteError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource,
				"updateRouteSuccess", locale));
		
		/*
		Delegator delegator = (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String partyId = (String) context.get("partyId"); // route
		String partyCode = (String) context.get("partyCode");
		String groupName = (String) context.get("groupName");
		String description = (String) context.get("description");
		String sche = (String) context.get("scheduleRoute");
		String employeeId = (String) context.get("employeeId");
		Locale locale = (Locale) context.get("locale");
		GenericValue curl = (GenericValue) context.get("userLogin");
		try {
			Map<String, Object> inr = FastMap.newInstance();
			inr.put("partyId", partyId);
			inr.put("userLogin", curl);
			Map<String, Object> outr = dispatcher.runSync(
					"getAllEmployeeOnRoad", inr);
			List<GenericValue> resoutr = (List<GenericValue>) outr
					.get("results");
			GenericValue firstE = EntityUtil.getFirst(resoutr);
			String employeeIdFrom = null;
			if (UtilValidate.isNotEmpty(firstE)) {
				employeeIdFrom = firstE.getString("partyId");
			}

			Map<String, Object> inp = FastMap.newInstance();
			GenericValue userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			inp.put("userLogin", userLogin);
			inp.put("partyId", partyId);
			inp.put("groupName", groupName);
			dispatcher.runSync("updatePartyGroup", inp);
			GenericValue party = delegator.findOne("Party",
					UtilMisc.toMap("partyId", partyId), false);
			party.set("partyCode", partyCode);
			party.store();
			SupUtil.createAndUpdateRouteInformation(delegator, partyId,
					description);
			List<GenericValue> listSchedule = delegator.findList(
					"RouteSchedule",
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("routeId", partyId),
							EntityUtil.getFilterByDateExpr())), null, UtilMisc
							.toList("scheduleRoute"), null, false);
			List<String> schedule = FastList.newInstance();
			List<String> old = FastList.newInstance();
			for (GenericValue e : listSchedule) {
				old.add(e.getString("scheduleRoute"));
			}
			JSONArray routes = JSONArray.fromObject(sche);
			for (int i = 0; i < routes.size(); i++) {
				schedule.add(routes.getString(i));
			}
			for (String sch : schedule) {
				if (!old.contains(sch)) {
					// create
					SupUtil.createAndUpdateRouteSchedule(delegator, partyId,
							sch, null, null);
				}
			}
			for (int i = 0; i < old.size(); i++) {
				if (!schedule.contains(old.get(i))) {
					// delete
					GenericValue del = listSchedule.get(i);
					del.set("thruDate", UtilDateTime.nowTimestamp());
					del.store();
				}
			}
			// update salesman vs route, not find old -> create
			if (UtilValidate.isNotEmpty(employeeId)
					&& !employeeId.equals(employeeIdFrom)) {
				SupUtil.updateRouteSalesRelationship(delegator, dispatcher,
						partyId, employeeIdFrom, employeeId, true, true);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"updateRouteError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource,
				"updateRouteSuccess", locale));
		*/
	}

	/*
	 * update customer in route
	 */

	public static Map<String, Object> removeSMOutRoute(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String cusId = (String) context.get("cusId");
		Locale locale = (Locale) context.get("locale");
		try {
			if (UtilValidate.isNotEmpty(cusId)) {
				List<GenericValue> ptRelation = delegator.findList(
						"PartyRelationship", EntityCondition
								.makeCondition(UtilMisc.toMap("partyIdFrom",
										cusId, "roleTypeIdFrom",
										"BaseSales_SALESMAN_GT", "thruDate",
										null)), null, null, null, false);
				if (UtilValidate.isNotEmpty(ptRelation)) {
					Map<String, Object> maptpx = FastMap.newInstance();
					maptpx.put("userLogin",
							(GenericValue) context.get("userLogin"));
					maptpx.put("partyIdFrom",
							ptRelation.get(0).getString("partyIdFrom"));
					maptpx.put("partyIdTo",
							ptRelation.get(0).getString("partyIdTo"));
					maptpx.put("roleTypeIdTo",
							ptRelation.get(0).getString("roleTypeIdTo"));
					maptpx.put("roleTypeIdFrom",
							ptRelation.get(0).getString("roleTypeIdFrom"));
					maptpx.put("fromDate",
							ptRelation.get(0).getString("fromDate"));
					maptpx.put("thruDate", UtilDateTime.nowTimestamp());
					maptpx.put("partyRelationshipTypeId", "GROUP_ROLLUP");
					try {
						dpct.getDispatcher().runSync("updatePartyRelationship",
								maptpx);
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError(UtilProperties
								.getMessage(resource, "removeerror", locale));
					}
				}
			}
		} catch (Exception e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"removeerror", locale));
			// TODO: handle exception
		}

		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource,
				"removesuccess", locale));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> getListCustomerBaseSales(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pagesize * pageNum + 1;
			int end = start + pagesize;
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId",
					"BaseSales_CUSTOMER_GT"));
			listIterator = delegator.find("PartyRoleAndPartyDetail",
					EntityCondition.makeCondition(listAllConditions,
							EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			// List<GenericValue> listCust = listIterator.getPartialList(start,
			// end);
			result.put("listIterator", listIterator);
			// result.put("TotalRows",String.valueOf(listIterator.getCompleteList().size()));
			// listIterator.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal get list customer cause : "
					+ e.getMessage());
			// TODO: handle exception
		}
		return result;
	}

	/*
	 * get list Exhibited Register for Sup
	 * 
	 * @DispatchContext
	 * 
	 * @Context
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> JQgetListExhibitedRegister(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> listPromotions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> result = FastMap.newInstance();
		try {
			Map<String, Object> tmpResult = dpct.getDispatcher().runSync(
					"getListStoreCompanyViewedByUserLogin",
					UtilMisc.toMap("userLogin",
							(GenericValue) context.get("userLogin")));
			if (ServiceUtil.isError(tmpResult)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if (UtilValidate.isNotEmpty(listStores)) {
				for (GenericValue store : listStores) {
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap(
							"productStoreId",
							store.getString("productStoreId"),
							"productPromoTypeId", "EXHIBITED")));
					listCond.add(EntityCondition.makeCondition(EntityUtil
							.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
					List<GenericValue> listTmp = delegator.findList(
							"ProductStorePromoApplFilterLoose", EntityCondition
									.makeCondition(listCond,
											EntityJoinOperator.AND), null,
							null, opt, false);
					if (UtilValidate.isNotEmpty(listTmp)) {
						for (GenericValue tmp : listTmp) {
							listPromotions.add(tmp);
						}
					}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	/*
	 * get list accumulate Register for Sup
	 * 
	 * @DispatchContext
	 * 
	 * @Context
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> JQgetListAccumulateRegister(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> listPromotions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> result = FastMap.newInstance();
		try {
			Map<String, Object> tmpResult = dpct.getDispatcher().runSync(
					"getListStoreCompanyViewedByUserLogin",
					UtilMisc.toMap("userLogin",
							(GenericValue) context.get("userLogin")));
			if (ServiceUtil.isError(tmpResult)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if (UtilValidate.isNotEmpty(listStores)) {
				for (GenericValue store : listStores) {
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap(
							"productStoreId",
							store.getString("productStoreId"),
							"productPromoTypeId", "ACCUMULATE")));
					listCond.add(EntityCondition.makeCondition(EntityUtil
							.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
					List<GenericValue> listTmp = delegator.findList(
							"ProductStorePromoApplFilterLoose", EntityCondition
									.makeCondition(listCond,
											EntityJoinOperator.AND), null,
							null, opt, false);
					if (UtilValidate.isNotEmpty(listTmp)) {
						for (GenericValue tmp : listTmp) {
							listPromotions.add(tmp);
						}
					}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListExhibited(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = FastMap.newInstance();
		List<GenericValue> listPromotions = FastList.newInstance();
		try {
			Map<String, Object> tmpResult = dpct.getDispatcher().runSync(
					"getListStoreCompanyViewedByUserLogin",
					UtilMisc.toMap("userLogin",
							(GenericValue) context.get("userLogin")));
			if (ServiceUtil.isError(tmpResult)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if (UtilValidate.isNotEmpty(listStores)) {
				for (GenericValue store : listStores) {
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap(
							"productStoreId",
							store.getString("productStoreId"),
							"productPromoTypeId", "EXHIBITED")));
					listCond.add(EntityCondition.makeCondition(EntityUtil
							.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
					List<GenericValue> listTmp = delegator.findList(
							"ProductStorePromoApplFilterLoose", EntityCondition
									.makeCondition(listCond,
											EntityJoinOperator.AND), null,
							null, null, false);
					if (UtilValidate.isNotEmpty(listTmp)) {
						for (GenericValue tmp : listTmp) {
							listPromotions.add(tmp);
						}
					}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	public static Map<String, Object> getLevelExhibited(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String exhibitedId = (String) context.get("exhibitedId");
		Map<String, Object> mapLevel = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(exhibitedId)) {
				List<GenericValue> listLevel = delegator.findList(
						"ProductPromoRule", EntityCondition
								.makeCondition(UtilMisc.toMap("productPromoId",
										exhibitedId)), null, UtilMisc
								.toList("+productPromoRuleId"), null, false);
				mapLevel.put("listLevel", listLevel);
			}
		} catch (Exception e) {
			return ServiceUtil
					.returnError("Fatal error when get list level cause : "
							+ e.getMessage());
		}
		return mapLevel;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> getListCustomerRegister(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String promoId = (String) context.get("promoId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> result = FastMap.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
					"productPromoId", parameters.get("promoId")[0])));
			List<GenericValue> listCustomer = delegator.findList(
					"ProductPromoRegisterAndCustomerDetail", EntityCondition
							.makeCondition(listAllConditions,
									EntityJoinOperator.AND), null,
					listSortFields, opt, false);
			result.put("listIterator", listCustomer);
			result.put("TotalRows", String.valueOf(listCustomer.size()));
		} catch (Exception e) {
			return ServiceUtil
					.returnError("Fatal error when get list Customer Register cause : "
							+ e.getMessage());
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> JQgetListExhibitedMarking(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> results = FastMap.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
					"registerStatus", "REG_PROMO_ACCEPTED", "promoMarkValue",
					null)));
			List<GenericValue> listPromos = delegator.findList(
					"ProductPromoRegisterAndCustomerDetail", EntityCondition
							.makeCondition(listAllConditions,
									EntityJoinOperator.AND), null,
					listSortFields, opt, false);
			List<Map<String, Object>> listCustomer = FastList.newInstance();

			if (UtilValidate.isNotEmpty(listPromos)) {
				List<GenericValue> listpromosTmp = FastList.newInstance();
				for (GenericValue po : listPromos) {
					List<EntityCondition> listAll = FastList.newInstance();
					listAll.add(EntityCondition.makeCondition("productPromoId",
							po.getString("productPromoId")));
					listAll.add(EntityCondition.makeCondition(
							"productPromoTypeId", "EXHIBITED"));
					listAll.add(EntityUtil.getFilterByDateExpr("fromDate",
							"thruDate"));
					List<GenericValue> tmp = delegator.findList("ProductPromo",
							EntityCondition.makeCondition(listAll,
									EntityJoinOperator.AND), null, null, null,
							false);
					if (UtilValidate.isNotEmpty(tmp)) {
						listpromosTmp.add(po);
					}
				}
				for (GenericValue promos : listpromosTmp) {
					if (!listCustomer.isEmpty()) {
						boolean checkIn = false;
						for (Map<String, Object> cus : listCustomer) {
							if (cus.get("partyId").equals(
									promos.getString("partyId"))) {
								checkIn = true;
								break;
							} else
								continue;
						}
						if (!checkIn) {
							Map<String, Object> tmp = FastMap.newInstance();
							tmp.put("partyId", promos.getString("partyId"));
							tmp.put("groupName", promos.getString("groupName"));
							listCustomer.add(tmp);
						}
					} else {
						Map<String, Object> tmp = FastMap.newInstance();
						tmp.put("partyId", promos.getString("partyId"));
						tmp.put("groupName", promos.getString("groupName"));
						listCustomer.add(tmp);
					}
				}
				results.put("listIterator", listCustomer);
				results.put("TotalRows", String.valueOf(listCustomer.size()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError("Fatal get list customer register approved cause : "
							+ e.getMessage());
			// TODO: handle exception
		}
		return results;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> JQgetListAccumulateMarking(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> results = FastMap.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
					"registerStatus", "REG_PROMO_ACCEPTED", "promoMarkValue",
					null)));
			List<GenericValue> listPromos = delegator.findList(
					"ProductPromoRegisterAndCustomerDetail", EntityCondition
							.makeCondition(listAllConditions,
									EntityJoinOperator.AND), null,
					listSortFields, opt, false);
			List<Map<String, Object>> listCustomer = FastList.newInstance();

			if (UtilValidate.isNotEmpty(listPromos)) {
				List<GenericValue> listpromosTmp = FastList.newInstance();
				for (GenericValue po : listPromos) {
					List<EntityCondition> listAll = FastList.newInstance();
					listAll.add(EntityCondition.makeCondition("productPromoId",
							po.getString("productPromoId")));
					listAll.add(EntityCondition.makeCondition(
							"productPromoTypeId", "ACCUMULATE"));
					listAll.add(EntityUtil.getFilterByDateExpr("fromDate",
							"thruDate"));
					List<GenericValue> tmp = delegator.findList("ProductPromo",
							EntityCondition.makeCondition(listAll,
									EntityJoinOperator.AND), null, null, null,
							false);
					if (UtilValidate.isNotEmpty(tmp)) {
						listpromosTmp.add(po);
					}
				}
				for (GenericValue promos : listpromosTmp) {
					if (!listCustomer.isEmpty()) {
						boolean checkIn = false;
						for (Map<String, Object> cus : listCustomer) {
							if (cus.get("partyId").equals(
									promos.getString("partyId"))) {
								checkIn = true;
								break;
							} else
								continue;
						}
						if (!checkIn) {
							Map<String, Object> tmp = FastMap.newInstance();
							tmp.put("partyId", promos.getString("partyId"));
							tmp.put("groupName", promos.getString("groupName"));
							listCustomer.add(tmp);
						}
					} else {
						Map<String, Object> tmp = FastMap.newInstance();
						tmp.put("partyId", promos.getString("partyId"));
						tmp.put("groupName", promos.getString("groupName"));
						listCustomer.add(tmp);
					}
				}
				results.put("listIterator", listCustomer);
				results.put("TotalRows", String.valueOf(listCustomer.size()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil
					.returnError("Fatal get list customer register approved cause : "
							+ e.getMessage());
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQgetListExhibitedForMark(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> results = FastMap.newInstance();
		List<Map<String, Object>> listFilterEx = FastList.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyId",
					(String) parameters.get("customerId")[0]));
			List<GenericValue> listEx = delegator.findList(
					"ExhibitedsOfStores", EntityCondition.makeCondition(
							listAllConditions, EntityJoinOperator.AND), null,
					listSortFields, opt, false);
			List<GenericValue> listPPM = delegator.findList(
					"ProductPromoMarking", null, null, null, null, false);
			if (UtilValidate.isNotEmpty(listPPM)) {
				for (GenericValue ex : listEx) {
					boolean flag = false;
					for (GenericValue ppm : listPPM) {
						if (ex.getString("productPromoRegisterId").equals(
								ppm.getString("productPromoRegisterId"))) {
							if (!ppm.getString("result").isEmpty()) {
								flag = true;
								break;
							} else
								continue;
						}
					}
					if (!flag) {
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("productPromoRegisterId",
								ex.getString("productPromoRegisterId"));
						mapTmp.put("partyId", ex.getString("partyId"));
						mapTmp.put("groupName", ex.getString("groupName"));
						mapTmp.put("promoName", ex.getString("promoName"));
						mapTmp.put("productPromoRuleId",
								ex.getString("productPromoRuleId"));
						listFilterEx.add(mapTmp);
					}
					;
				}
				results.put("listIterator", listFilterEx);
				results.put("TotalRows", String.valueOf(listFilterEx.size()));
			} else {
				results.put("listIterator", listEx);
				results.put("TotalRows", String.valueOf(listEx.size()));
			}

		} catch (Exception e) {
			return ServiceUtil
					.returnError("Fatal error when get list Exhibited Marking cause : "
							+ e.getMessage());
			// TODO: handle exception
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQgetListAccumulateForMark(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> results = FastMap.newInstance();
		List<Map<String, Object>> listFilterEx = FastList.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyId",
					(String) parameters.get("customerId")[0]));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDate",
					"thruDate"));
			List<GenericValue> listEx = delegator.findList(
					"AccumulateOfStores", EntityCondition.makeCondition(
							listAllConditions, EntityJoinOperator.AND), null,
					listSortFields, opt, false);
			List<GenericValue> listPPM = delegator.findList(
					"ProductPromoMarking", null, null, null, null, false);
			if (UtilValidate.isNotEmpty(listPPM)) {
				for (GenericValue ex : listEx) {
					boolean flag = false;
					for (GenericValue ppm : listPPM) {
						if (ex.getString("productPromoRegisterId").equals(
								ppm.getString("productPromoRegisterId"))) {
							if (!ppm.getString("result").isEmpty()) {
								flag = true;
								break;
							} else
								continue;
						}
					}
					if (!flag) {
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("productPromoRegisterId",
								ex.getString("productPromoRegisterId"));
						mapTmp.put("partyId", ex.getString("partyId"));
						mapTmp.put("groupName", ex.getString("groupName"));
						mapTmp.put("promoName", ex.getString("promoName"));
						mapTmp.put("productPromoRuleId",
								ex.getString("productPromoRuleId"));
						listFilterEx.add(mapTmp);
					}
					;
				}
				results.put("listIterator", listFilterEx);
				results.put("TotalRows", String.valueOf(listFilterEx.size()));
			} else {
				results.put("listIterator", listEx);
				results.put("TotalRows", String.valueOf(listEx.size()));
			}

		} catch (Exception e) {
			return ServiceUtil
					.returnError("Fatal error when get list Exhibited Marking cause : "
							+ e.getMessage());
			// TODO: handle exception
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> ResultMarking(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String listEx = (String) context.get("listEx");
		Locale locale = (Locale) context.get("locale");

		try {
			JSONArray arr = new JSONArray();
			if (!listEx.isEmpty()) {
				arr = JSONArray.fromObject(listEx);
				if (UtilValidate.isNotEmpty(arr)) {
					for (int i = 0; i < arr.size(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						String registerId = obj
								.getString("productPromoRegisterId");
						String result = obj.getString("result");
						GenericValue regis = delegator
								.makeValue("ProductPromoMarking");
						regis.set("productPromoRegisterId", registerId);
						regis.set("result", result);
						regis.set("createdBy", (String) ((GenericValue) context
								.get("userLogin")).getString("partyId"));
						regis.set("createdDate", UtilDateTime.nowTimestamp());
						regis.create();
					}
				}

			}

		} catch (Exception e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"markingerror", locale));
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource,
				"markingsuccess", locale));
	}

	/*
	 * exhibited Register
	 * 
	 * @param DispatchContext dpct,context
	 */
	public static Map<String, Object> exhibitedRegisterSUP(
			DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String createdBy = ((GenericValue) context.get("userLogin"))
				.getString("partyId");
		String customerId = (String) context.get("customerId");
		String productPromoId = (String) context.get("productPromoId");
		String ruleId = (String) context.get("ruleId");
		String registerStatus = "REG_PROMO_CREATED";
		Timestamp createdDate = (Timestamp) context.get("createdDate");
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> tmp = FastMap.newInstance();
		try {
			List<GenericValue> listRegistered = delegator.findList(
					"ProductPromoRegister", null, null, null, null, false);
			if (UtilValidate.isNotEmpty(listRegistered)) {
				boolean checkIn = false;
				for (GenericValue regis : listRegistered) {
					if (regis.getString("productPromoId")
							.equals(productPromoId)
							&& regis.getString("partyId").equals(customerId)) {
						tmp.put("duplicate", "duplicate");
						result.put("result", tmp);
						checkIn = true;
						return result;
					} else {
						continue;
					}
				}
				if (!checkIn) {
					tmp.put("duplicate", "");
					GenericValue ex = delegator
							.makeValue("ProductPromoRegister");
					String productPromoRegisterId = delegator
							.getNextSeqId("ProductPromoRegister");
					ex.set("productPromoRegisterId", productPromoRegisterId);
					ex.set("createdBy", createdBy);
					ex.set("partyId", customerId);
					ex.set("productPromoId", productPromoId);
					ex.set("productPromoRuleId", ruleId);
					ex.set("registerStatus", registerStatus);
					if (createdDate == null) {
						ex.set("createdDate", nowtimestamp);
					} else
						ex.set("createdDate", createdDate);
					ex.create();
					tmp.put("productPromoId", productPromoId);
					tmp.put("customerId", customerId);
					tmp.put("registerStatus", registerStatus);
				}
			} else {
				GenericValue ex = delegator.makeValue("ProductPromoRegister");
				String productPromoRegisterId = delegator
						.getNextSeqId("ProductPromoRegister");
				ex.set("productPromoRegisterId", productPromoRegisterId);
				ex.set("createdBy", createdBy);
				ex.set("partyId", customerId);
				ex.set("productPromoId", productPromoId);
				ex.set("productPromoRuleId", ruleId);
				ex.set("registerStatus", registerStatus);
				if (createdDate == null) {
					ex.set("createdDate", nowtimestamp);
				} else
					ex.set("createdDate", createdDate);
				ex.create();
				tmp.put("productPromoId", productPromoId);
				tmp.put("customerId", customerId);
				tmp.put("registerStatus", registerStatus);
			}
		} catch (Exception e) {
			Debug.logError(
					"Can't create register for exhibited " + e.getMessage(),
					module);
			e.printStackTrace();
		}
		result.put("result", tmp);
		return result;
	}

	/*
	 * reject exhibited
	 */
	public static Map<String, Object> rejectExhibited(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		try {
			if (UtilValidate.isNotEmpty(partyId)
					&& UtilValidate.isNotEmpty(productPromoId)
					&& UtilValidate.isNotEmpty(productPromoRuleId)) {
				List<GenericValue> ppm = delegator.findList(
						"ProductPromoRegister", EntityCondition
								.makeCondition(UtilMisc.toMap("partyId",
										partyId, "productPromoId",
										productPromoId, "productPromoRuleId",
										productPromoRuleId)), null, null, null,
						false);
				ppm.get(0).set("registerStatus", "REG_PROMO_CANCELED");
				ppm.get(0).store();
			}
		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	}

	/*
	 * update exhibited
	 */
	public static Map<String, Object> updateExhibited(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		String productPromoRegisterId = (String) context
				.get("productPromoRegisterId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		try {
			if (UtilValidate.isNotEmpty(productPromoRegisterId)
					&& UtilValidate.isNotEmpty(productPromoRuleId)) {
				GenericValue ppm = delegator.findOne("ProductPromoRegister",
						false, UtilMisc.toMap("productPromoRegisterId",
								productPromoRegisterId));
				if (UtilValidate.isNotEmpty(ppm)) {
					ppm.set("productPromoRuleId", productPromoRuleId);
					ppm.store();
				}
			}
		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> JQgetListPPM(DispatchContext dpct,
			Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		Map<String, Object> results = FastMap.newInstance();
		try {
			listAllConditions.add(EntityCondition.makeCondition("partyId",
					parameters.get("customerId")[0]));
			opt.setDistinct(true);
			if (!parameters.get("customerId")[0].isEmpty()) {
				List<GenericValue> listPPM = delegator.findList(
						"ProductPromoMarkingAndRegister", EntityCondition
								.makeCondition(listAllConditions,
										EntityJoinOperator.AND), null, null,
						opt, false);
				results.put("listIterator", listPPM);
				results.put("TotalRows", String.valueOf(listPPM.size()));
			}
		} catch (Exception e) {
			return ServiceUtil
					.returnError("Fatal error when get list ppm cause : "
							+ e.getMessage());
		}
		return results;
	}

	public static Map<String, Object> createRouteStores(DispatchContext dpct,
			Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String customers = (String) context.get("parties");
		String routeId = (String) context.get("routeId");
		Debug.log(module + "::createRouteStores, routeId = " + routeId
				+ ", customers = " + customers);
		try {
			customers = customers.substring(1, customers.length() - 1);
			String[] customer = customers.split(",");
			for (int i = 0; i < customer.length; i++) {
				String customerId = customer[i].substring(1,
						customer[i].length() - 1);
				Timestamp fromDate = new Timestamp(System.currentTimeMillis());
				GenericValue rc = delegator.makeValue("RouteCustomer");
				rc.put("routeId", routeId);
				rc.put("customerId", customerId);
				rc.put("fromDate", fromDate);

				delegator.create(rc);

				SupUtil.generateSaleRouteScheduleDetailDateForOneCustomerAdded(delegator, routeId, customerId);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return res;
		/*
		 * LocalDispatcher dispatcher = dpct.getDispatcher(); Map<String,
		 * Object> res = ServiceUtil.returnSuccess(); Delegator delegator =
		 * dpct.getDelegator(); String customers = (String)
		 * context.get("parties"); String routeId = (String)
		 * context.get("routeId"); try { if (UtilValidate.isNotEmpty(customers))
		 * { JSONArray parties = JSONArray.fromObject(customers); for (int i =
		 * 0; i < parties.size(); i++) { String partyId = parties.getString(i);
		 * // has old -> clear, has new -> create
		 * SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher,
		 * routeId, partyId, true, true); } } } catch (Exception e) {
		 * Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError(e.getMessage()); } return res;
		 */
	}

	public static Map<String, Object> removeAllRouteStores(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		LocalDispatcher dispatcher = dpct.getDispatcher();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String routeId = (String) context.get("routeId");
		EntityListIterator list = null;
		try {
			if (UtilValidate.isNotEmpty(routeId)) {
				list = delegator.find("PartyRelationship", EntityCondition
						.makeCondition(UtilMisc.toList(EntityCondition
								.makeCondition(EntityCondition.makeCondition(
										"roleTypeIdFrom", "ROUTE"),
										EntityCondition.makeCondition(
												"roleTypeIdTo", "CUSTOMER"),
										EntityCondition.makeCondition(
												"partyRelationshipTypeId",
												"SALES_ROUTE"), EntityCondition
												.makeCondition("partyIdFrom",
														routeId), EntityUtil
												.getFilterByDateExpr()))),
						null, null, null, null);
				GenericValue e = null;
				while ((e = list.next()) != null) {
					SupUtil.createAndUpdateRouteStoreRelationship(delegator,
							dispatcher, routeId, e.getString("partyIdTo"),
							false, true);
				}
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} finally {
			if (list != null) {
				list.close();
			}
		}
		return res;
	}

	public static Map<String, Object> removeRouteStores(DispatchContext dpct,
			Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String customers = (String) context.get("parties");
		String routeId = (String) context.get("routeId");
		Debug.log(module + "::removeRouteStores, routeId = " + routeId
				+ ", customers = " + customers);
		try {
			customers = customers.substring(1, customers.length() - 1);
			String[] customer = customers.split(",");
			List<EntityCondition> conds = FastList.newInstance();

			for (int i = 0; i < customer.length; i++) {
				String customerId = customer[i].substring(1,
						customer[i].length() - 1);
				conds.clear();
				conds.add(EntityCondition.makeCondition("routeId",
						EntityOperator.EQUALS, routeId));
				conds.add(EntityCondition.makeCondition("customerId",
						EntityOperator.EQUALS, customerId));
				conds.add(EntityCondition.makeCondition("thruDate",
						EntityOperator.EQUALS, null));
				List<GenericValue> lst = delegator.findList("RouteCustomer",
						EntityCondition.makeCondition(conds), null, null, null,
						false);
				Debug.log(module + "::removeRouteStores, lst = " + lst.size()
						+ ", customerId = " + customerId);
				for (GenericValue rc : lst) {
					Timestamp thruDate = new Timestamp(
							System.currentTimeMillis());
					rc.put("thruDate", thruDate);
					delegator.store(rc);
					Debug.log(module + "::removeRouteStores, lst = "
							+ lst.size() + ", customerId = " + customerId
							+ " DELETE");

					SupUtil.removeSaleRouteScheduleDetailDateForOneCustomerRemoved(
							delegator, customerId, routeId);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return res;
		/*
		 * LocalDispatcher dispatcher = dpct.getDispatcher(); Map<String,
		 * Object> res = ServiceUtil.returnSuccess(); Delegator delegator =
		 * dpct.getDelegator(); String customers = (String)
		 * context.get("parties"); String routeId = (String)
		 * context.get("routeId"); try { if (UtilValidate.isNotEmpty(customers))
		 * { JSONArray parties = JSONArray.fromObject(customers); for (int i =
		 * 0; i < parties.size(); i++) { String partyId = parties.getString(i);
		 * // has old -> clear, not create new
		 * SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher,
		 * routeId, partyId, false, true); } } } catch (Exception e) {
		 * Debug.log(e.getMessage()); return
		 * ServiceUtil.returnError(e.getMessage()); } return res;
		 */
	}

	public static Map<String, Object> deleteAllPartyContactMechPurpose(
			DispatchContext dpct, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String contactMechId = (String) context.get("contactMechId");
		String partyId = (String) context.get("partyId");
		try {
			List<GenericValue> list = delegator.findList(
					"PartyContactMechPurpose", EntityCondition
							.makeCondition(UtilMisc.toList(EntityCondition
									.makeCondition("partyId", partyId),
									EntityCondition.makeCondition(
											"contactMechId", contactMechId))),
					null, null, null, false);
			for (GenericValue e : list) {
				e.set("thruDate", UtilDateTime.nowTimestamp());
				e.store();
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	/* special promos */
	/*
	 * @SuppressWarnings("unchecked") public static Map<String, Object>
	 * getCustomerPromosRegistration(DispatchContext dpct, Map<String, Object>
	 * context) throws GenericEntityException{ Locale locale = (Locale)
	 * context.get("locale"); Delegator delegator = dpct.getDelegator();
	 * Map<String, Object> res = ServiceUtil.returnSuccess(); GenericValue
	 * userLogin = (GenericValue) context.get("userLogin"); String userLoginId =
	 * userLogin.getString("userLoginId"); Map<String, String[]> parameters =
	 * (Map<String,String[]>) context.get("parameters"); int pagenum =
	 * Integer.parseInt((String) parameters.get("pagenum")[0]); int pagesize =
	 * Integer.parseInt((String) parameters.get("pagesize")[0]); int start =
	 * pagenum*pagesize + 1; EntityListIterator resList = null; try {
	 * List<String> getAllRoute = SupUtil.getAllRoute(delegator, userLoginId);
	 * List<EntityCondition> listCond = (List<EntityCondition>)
	 * context.get("listAllConditions");
	 * listCond.add(EntityUtil.getFilterByDateExpr("relFromDate",
	 * "relThruDate")); listCond.add(EntityUtil.getFilterByDateExpr());
	 * listCond.add(EntityCondition.makeCondition("parStatusId",
	 * "PARTY_ENABLED"));
	 * listCond.add(EntityCondition.makeCondition("roleTypeIdFrom", "ROUTE"));
	 * listCond.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER"));
	 * listCond.add(EntityCondition.makeCondition("partyIdFrom",
	 * EntityOperator.IN, getAllRoute));
	 * 
	 * EntityCondition finalCond = EntityCondition.makeCondition(listCond);
	 * EntityFindOptions options = new EntityFindOptions();
	 * options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
	 * resList = delegator.find("PartyRelationshipAndPartyToPromosExt",
	 * finalCond, null, null, UtilMisc.toList("fromDate"), options);
	 * List<GenericValue> tmpList = resList.getPartialList(start, pagesize);
	 * List<Map<String, Object>> finList = FastList.newInstance(); int size =
	 * resList.getResultsTotalSize(); for(GenericValue e : tmpList){ Map<String,
	 * Object> o = FastMap.newInstance(); GenericValue status =
	 * delegator.findOne("StatusItem", UtilMisc.toMap("statusId",
	 * e.getString("statusId")), false); String description = (String)
	 * status.get("description", locale); o.put("statusDescription",
	 * description); GenericValue en = delegator.findOne("Enumeration",
	 * UtilMisc.toMap("enumId", e.getString("resultEnumId")), false);
	 * if(UtilValidate.isNotEmpty(en)){ String enCode = (String)
	 * en.get("description", locale); o.put("resultDescription", enCode); }
	 * o.putAll(e); finList.add(o); } res.put("TotalRows",
	 * String.valueOf(size)); res.put("listIterator", finList); } catch
	 * (GenericEntityException e) { Debug.log(e.getMessage()); return
	 * ServiceUtil.returnError(e.getMessage()); }finally { if(resList != null){
	 * resList.close(); } }
	 * 
	 * return res; }
	 */

/*	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerPromosRegistration(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dpct.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");

		try {
			String userLoginPartyId = userLogin.getString("partyId");

			String productPromoTypeId = SalesUtil.getParameter(parameters,
					"typeId");
			if (UtilValidate.isNotEmpty(productPromoTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"productPromoTypeId", productPromoTypeId));

				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}

				if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId)
						|| SalesPartyUtil.isSalesAdminManager(delegator,
								userLoginPartyId)
						|| SalesPartyUtil.isSalesAdmin(delegator,
								userLoginPartyId)) {

					listAllConditions.add(EntityUtil.getFilterByDateExpr());
					listIterator = delegator.find(
							"ProductPromoExtRegisterDetail",
							EntityCondition.makeCondition(listAllConditions),
							null, null, listSortFields, opts);
				} else if (SalesPartyUtil.isDistributor(delegator,
						userLogin.getString("partyId"))) {
					// condition with userLogin is distributor
					listAllConditions.add(EntityCondition.makeCondition(
							"parStatusId", "PARTY_ENABLED"));
					listAllConditions.add(EntityCondition.makeCondition(
							"roleTypeIdFrom", "CUSTOMER"));
					listAllConditions.add(EntityCondition.makeCondition(
							"roleTypeIdTo", "DISTRIBUTOR"));

					 * listAllConditions.add(EntityCondition.makeCondition(
					 * "statusId", "PROMO_REGISTRATION_ACCEPTED"));

					listAllConditions.add(EntityCondition.makeCondition(
							"partyIdTo", userLogin.get("partyId")));
					listAllConditions.add(EntityCondition.makeCondition(
							"partyRelationshipTypeId", "CUSTOMER_REL"));
					listAllConditions.add(EntityUtil.getFilterByDateExpr(
							"relFromDate", "relThruDate"));
					listAllConditions.add(EntityUtil.getFilterByDateExpr());

					listIterator = delegator.find(
							"PartyFromRelAndPartyToPromosExt",
							EntityCondition.makeCondition(listAllConditions),
							null, null, listSortFields, opts);
				} else {
					// condition with userLogin is employee
					List<String> getAllRoute = SupUtil.getAllRoute(delegator,
							userLogin.getString("userLoginId"));

					// listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId",
					// productPromoTypeId));
					listAllConditions.add(EntityCondition.makeCondition(
							"parStatusId", "PARTY_ENABLED"));
					listAllConditions.add(EntityCondition.makeCondition(
							"roleTypeIdFrom", "ROUTE"));
					listAllConditions.add(EntityCondition.makeCondition(
							"roleTypeIdTo", "CUSTOMER"));
					listAllConditions.add(EntityCondition.makeCondition(
							"partyIdFrom", EntityOperator.IN, getAllRoute));
					listAllConditions.add(EntityUtil.getFilterByDateExpr(
							"relFromDate", "relThruDate"));
					listAllConditions.add(EntityUtil.getFilterByDateExpr());

					listIterator = delegator.find(
							"PartyRelationshipAndPartyToPromosExt",
							EntityCondition.makeCondition(listAllConditions),
							null, null, listSortFields, opts);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getCustomerPromosRegistration service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}

		successResult.put("listIterator", listIterator);
		return successResult;
	}*/

	public static Map<String, Object> getCustomerPromosRegistration(DispatchContext dpct, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dpct.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		/*EntityListIterator listIterator = null;*/
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		try {
			String partyId = userLogin.getString("partyId");
			String productPromoTypeId = SalesUtil.getParameter(parameters,"typeId");
			if (UtilValidate.isNotEmpty(productPromoTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", productPromoTypeId));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				if (SalesPartyUtil.isSalesManager(delegator, partyId) || SalesPartyUtil.isSalesAdminManager(delegator, partyId) || SalesPartyUtil.isSalesAdmin(delegator, partyId)) {
					//nothing
				} else if (SalesPartyUtil.isDistributor(delegator, partyId)) {
					// condition with userLogin is distributor
					listAllConditions.add(EntityCondition.makeCondition("distributorId", partyId));
				} else if (SalesPartyUtil.isSalessup(delegator, partyId)){
					// condition with userLogin is sup
					listAllConditions.add(EntityCondition.makeCondition("supervisorId", partyId));
				}else if (SalesPartyUtil.isSalesman(delegator, partyId)){
					// condition with userLogin is employee
					listAllConditions.add(EntityCondition.makeCondition("salesmanId", partyId));
				}
				listIterator = delegator.findList("ProductPromoExtRegisterDetailSUP", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getCustomerPromosRegistration service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}

		successResult.put("listIterator", listIterator);
		return successResult;
	}

	/*
	 * @SuppressWarnings("unchecked") public static Map<String, Object>
	 * getCustomerPromosRegistrationDis(DispatchContext dpct, Map<String,
	 * Object> context) throws GenericEntityException{ Locale locale = (Locale)
	 * context.get("locale"); Delegator delegator = dpct.getDelegator();
	 * Map<String, Object> res = ServiceUtil.returnSuccess(); GenericValue
	 * userLogin = (GenericValue) context.get("userLogin"); Map<String,
	 * String[]> parameters = (Map<String,String[]>) context.get("parameters");
	 * int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
	 * int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
	 * int start = pagenum*pagesize + 1; EntityListIterator resList = null; try
	 * { List<EntityCondition> listCond = (List<EntityCondition>)
	 * context.get("listAllConditions");
	 * listCond.add(EntityUtil.getFilterByDateExpr("relFromDate",
	 * "relThruDate")); listCond.add(EntityUtil.getFilterByDateExpr());
	 * listCond.add(EntityCondition.makeCondition("parStatusId",
	 * "PARTY_ENABLED"));
	 * listCond.add(EntityCondition.makeCondition("roleTypeIdFrom",
	 * "CUSTOMER")); listCond.add(EntityCondition.makeCondition("roleTypeIdTo",
	 * "DISTRIBUTOR")); listCond.add(EntityCondition.makeCondition("statusId",
	 * "PROMO_REGISTRATION_ACCEPTED"));
	 * listCond.add(EntityCondition.makeCondition("partyIdTo",
	 * EntityOperator.EQUALS, userLogin.get("partyId")));
	 * 
	 * EntityCondition finalCond = EntityCondition.makeCondition(listCond);
	 * EntityFindOptions options = new EntityFindOptions();
	 * options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
	 * resList = delegator.find("DisAndPartyToPromosExt", finalCond, null, null,
	 * UtilMisc.toList("fromDate"), options); List<GenericValue> tmpList =
	 * resList.getPartialList(start, pagesize); List<Map<String, Object>>
	 * finList = FastList.newInstance(); int size =
	 * resList.getResultsTotalSize(); for(GenericValue e : tmpList){ Map<String,
	 * Object> o = FastMap.newInstance(); GenericValue status =
	 * delegator.findOne("StatusItem", UtilMisc.toMap("statusId",
	 * e.getString("statusId")), false); String description = (String)
	 * status.get("description", locale); o.put("statusDescription",
	 * description); GenericValue en = delegator.findOne("Enumeration",
	 * UtilMisc.toMap("enumId", e.getString("resultEnumId")), false);
	 * if(UtilValidate.isNotEmpty(en)){ String enCode = (String)
	 * en.get("description", locale); o.put("resultDescription", enCode); }
	 * o.putAll(e); finList.add(o); } res.put("TotalRows",
	 * String.valueOf(size)); res.put("listIterator", finList); } catch
	 * (GenericEntityException e) { Debug.log(e.getMessage()); return
	 * ServiceUtil.returnError(e.getMessage()); }finally { if(resList != null){
	 * resList.close(); } }
	 * 
	 * return res; }
	 */

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerPromosEvaluation(
			DispatchContext dpct, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = dpct.getDelegator();

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");

		try {
			String partyId = null;
			String productPromoId = null;
			String productPromoRuleId = null;
			if (parameters.containsKey("partyId")
					&& parameters.get("partyId").length > 0)
				partyId = parameters.get("partyId")[0];
			if (parameters.containsKey("productPromoId")
					&& parameters.get("productPromoId").length > 0)
				productPromoId = parameters.get("productPromoId")[0];
			if (parameters.containsKey("productPromoRuleId")
					&& parameters.get("productPromoRuleId").length > 0)
				productPromoRuleId = parameters.get("productPromoRuleId")[0];

			listAllConditions.add(EntityCondition.makeCondition("partyId",
					partyId));
			listAllConditions.add(EntityCondition.makeCondition(
					"productPromoId", productPromoId));
			listAllConditions.add(EntityCondition.makeCondition(
					"productPromoRuleId", productPromoRuleId));

			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-entryDate");
			}

			listIterator = delegator.find(
					"ProductPromoExtRegistrationEvalDetail",
					EntityCondition.makeCondition(listAllConditions), null,
					null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getCustomerPromosEvaluation service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}

		successResult.put("listIterator", listIterator);
		return successResult;
	}

	/*
	 * @SuppressWarnings("unchecked") public static Map<String, Object>
	 * getCustomerPromosEvaluation(DispatchContext dpct, Map<String, Object>
	 * context) throws GenericEntityException{ Locale locale = (Locale)
	 * context.get("locale"); Delegator delegator = dpct.getDelegator();
	 * Map<String, Object> res = ServiceUtil.returnSuccess(); Map<String,
	 * String[]> parameters = (Map<String,String[]>) context.get("parameters");
	 * String partyId = (String) parameters.get("partyId")[0]; String
	 * productPromoId = (String) parameters.get("productPromoId")[0]; String
	 * productPromoRuleId = (String) parameters.get("productPromoRuleId")[0];
	 * int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
	 * int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
	 * int start = pagenum*pagesize + 1; EntityListIterator resList = null; try
	 * { List<EntityCondition> listCond = FastList.newInstance();
	 * listCond.add(EntityCondition.makeCondition("partyId", partyId));
	 * listCond.add(EntityCondition.makeCondition("productPromoId",
	 * productPromoId));
	 * listCond.add(EntityCondition.makeCondition("productPromoRuleId",
	 * productPromoRuleId));
	 * 
	 * EntityCondition finalCond = EntityCondition.makeCondition(listCond);
	 * EntityFindOptions options = new EntityFindOptions();
	 * options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
	 * resList = delegator.find("ProductPromoExtRegistrationEval", finalCond,
	 * null, null, UtilMisc.toList("-entryDate"), options); List<GenericValue>
	 * tmpList = resList.getPartialList(start, pagesize); List<Map<String,
	 * Object>> finList = FastList.newInstance(); int size =
	 * resList.getResultsTotalSize(); for(GenericValue e : tmpList){ Map<String,
	 * Object> o = FastMap.newInstance(); GenericValue en =
	 * delegator.findOne("Enumeration", UtilMisc.toMap("enumId",
	 * e.getString("resultEnumId")), false); if(UtilValidate.isNotEmpty(en)){
	 * String enCode = (String) en.get("description", locale);
	 * o.put("resultDescription", enCode); } o.putAll(e); finList.add(o); }
	 * res.put("TotalRows", String.valueOf(size)); res.put("listIterator",
	 * finList); } catch (GenericEntityException e) { Debug.log(e.getMessage());
	 * return ServiceUtil.returnError(e.getMessage()); } finally { if(resList !=
	 * null){ resList.close(); } }
	 * 
	 * return res; }
	 */
	/* special promos */

	public static Map<String, Object> getSupManager(DispatchContext dpc,
			Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		try {
			String supId = SupUtil.getSupManager(dpc, partyId);
			res.put("partyIdTo", supId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return res;
	}

	public static Map<String, Object> getSupManagerN(DispatchContext dpc,
													Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		try {
			String supId = SupUtil.getSupManagerN(dpc, partyId);
			res.put("partyIdTo", supId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return res;
	}


	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductCoverage(DispatchContext dpct,
			Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dpct.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context
					.get("listAllConditions");
			List<String> listSortFields = (List<String>) context
					.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<GenericValue> dummy = delegator.findList("PartyOutletOrdered",
					EntityCondition.makeCondition("statusId",
							EntityJoinOperator.EQUALS, "ITEM_COMPLETED"),
					UtilMisc.toSet("productId"), null, null, false);
			List<String> productIds = EntityUtil.getFieldListFromEntityList(
					dummy, "productId", true);
			listAllConditions.add(EntityCondition.makeCondition("productId",
					EntityJoinOperator.IN, productIds));
			listSortFields.add("productName");
			EntityListIterator listIterator = delegator.find("Product",
					EntityCondition.makeCondition(listAllConditions), null,
					null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("Error get customer");
		}
		return result;
	}

	public static Map<String, Object> jqGetCustAssignedToRoute(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		opts.setDistinct(true);
		try {
			String routeId = null;
			if (parameters.containsKey("routeId") && parameters.get("routeId").length > 0) {
				routeId = (String) parameters.get("routeId")[0];
			}
			if (routeId == null) {
				return ServiceUtil.returnError("error");
			}
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listAllConditions.add(EntityCondition.makeCondition("routeId", routeId));
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "RouteCustomerAndPartyCustomerDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return successResult;
	}

	public static Map<String, Object> jqGetCustAvailable(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		opts.setDistinct(true);
		try {
			String salesmanId = null;
			String routeId = null;
			if (parameters.containsKey("salesmanId") && parameters.get("salesmanId").length > 0) {
				salesmanId = (String) parameters.get("salesmanId")[0];
			}
			if (parameters.containsKey("routeId") && parameters.get("routeId").length > 0) {
                routeId = (String) parameters.get("routeId")[0];
			}
			if (salesmanId == null || routeId == null) {
				return ServiceUtil.returnError("error");
			}
            List<EntityCondition> condAssigned = FastList.newInstance();
            condAssigned.add(EntityUtil.getFilterByDateExpr());
            condAssigned.add(EntityCondition.makeCondition("routeId", routeId));
            List<String> custAssignedIds = EntityUtil.getFieldListFromEntityList(delegator.findList("RouteCustomerAndPartyCustomer",
                    EntityCondition.makeCondition(condAssigned),UtilMisc.toSet("customerId"),null,null,false),"customerId",true);
            Timestamp now = UtilDateTime.nowTimestamp();

			/*EntityCondition condTime0 = EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null);
			EntityCondition condTime1 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, now);
			EntityCondition condTime2 = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, now);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(condTime0, condTime1, condTime2), EntityJoinOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition("routeId", EntityOperator.NOT_EQUAL,routeId));*/
			listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLogin.get("partyId")));
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
            if (UtilValidate.isNotEmpty(custAssignedIds)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, custAssignedIds));
            }
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("partyCode");
            fieldsToSelect.add("fullName");
            fieldsToSelect.add("distributorId");
            fieldsToSelect.add("postalAddressName");
            fieldsToSelect.add("salesmanId");
            fieldsToSelect.add("supervisorId");
			fieldsToSelect.add("geoPointId");
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyCustomerAddressGeoPoint",
							EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toSet(fieldsToSelect), listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return successResult;
	}

	//using createRouteStores
	@Deprecated
	public static Map<String, Object> assignCustToRoute(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> Conds = FastList.newInstance();
		try {
			String routeId = (String) context.get("routeId");
			String customerId = (String) context.get("customerId");
			if (routeId == null || customerId == null) {
				return ServiceUtil.returnError("error");
			}
			Conds.add(EntityCondition.makeCondition("routeId", routeId));
			Conds.add(EntityCondition.makeCondition("customerId", customerId));
			Conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listRC = delegator.findList("RouteCustomer",
					EntityCondition.makeCondition(Conds), null, null, null,
					false);
			for (GenericValue rc : listRC) {
				rc.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(rc);
			}
			GenericValue newRC = delegator.makeValue("RouteCustomer", UtilMisc
					.toMap("routeId", routeId, "customerId", customerId,
							"fromDate", UtilDateTime.nowTimestamp()));
			newRC.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

    //using removeRouteStores
    @Deprecated
	public static Map<String, Object> unassignCustFromRoute(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> Conds = FastList.newInstance();
		try {
			String routeId = (String) context.get("routeId");
			String customerId = (String) context.get("customerId");
			if (routeId == null || customerId == null) {
				return ServiceUtil.returnError("error");
			}
			Conds.add(EntityCondition.makeCondition("routeId", routeId));
			Conds.add(EntityCondition.makeCondition("customerId", customerId));
			Conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listRC = delegator.findList("RouteCustomer",
					EntityCondition.makeCondition(Conds), null, null, null,
					false);
			for (GenericValue rc : listRC) {
				rc.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(rc);
			}
			GenericValue pc = delegator.findOne("PartyCustomer",
					UtilMisc.toMap("partyId", customerId), false);
			pc.set("routeId", null);
			delegator.store(pc);

		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getListRouteScheduleDetailDate(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
        EntityListIterator listIterator = null;
		try {
		    List<String> supIds = FastList.newInstance();
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("salesmanId",EntityOperator.EQUALS,userLoginPartyId));
            } else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                supIds.add(userLoginPartyId);
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            } else {
                isSearch = false;
            }
            if (isSearch){
                List<EntityCondition> conds = FastList.newInstance();
                boolean findBySup = true;
                if (UtilValidate.isEmpty(supIds)) {
                    findBySup = false;
                } else if (supIds.size() == 1) {
                    conds.add(EntityCondition.makeCondition("supervisorId", supIds.get(0)));
                } else {
                    conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, supIds));
                }
                if (findBySup) {
                    List<GenericValue> partySalesmans = delegator.findList("PartySalesman", EntityCondition.makeCondition(conds), null,null,null,false);
                    List<String> salesmenIds = EntityUtil.getFieldListFromEntityList(partySalesmans , "partyId", true);
                    listAllConditions.add(EntityCondition.makeCondition("salesmanId",EntityOperator.IN,salesmenIds));
                }
                listSortFields.add("date");
                listSortFields.add("routeId");
                listSortFields.add("sequenceNum");
                listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator,
                        "RouteScheduleDetailDateAndSalesmanAndCust", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            }
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return successResult;
	}

	public static Map<String, Object> jqGetListMobileDeviceLog(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
        EntityListIterator listIterator = null;
		try {
		    List<String> supIds = FastList.newInstance();
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("salesmanId",EntityOperator.EQUALS,userLoginPartyId));
            } else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                supIds.add(userLoginPartyId);
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                supIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            } else {
                isSearch = false;
            }
            if (isSearch){
                List<EntityCondition> conds = FastList.newInstance();
                boolean findBySup = true;
                if (UtilValidate.isEmpty(supIds)) {
                    findBySup = false;
                } else if (supIds.size() == 1) {
                    conds.add(EntityCondition.makeCondition("supervisorId", supIds.get(0)));
                } else {
                    conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, supIds));
                }
                if (findBySup) {
                    List<GenericValue> partySalesmans = delegator.findList("PartySalesman", EntityCondition.makeCondition(conds), null,null,null,false);
                    List<String> salesmenIds = EntityUtil.getFieldListFromEntityList(partySalesmans , "partyId", true);
                    listAllConditions.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,salesmenIds));
                }
                listSortFields.add("-updatedTime");
                listSortFields.add("partyCode");
                listSortFields.add("deviceId");
                listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator,
                        "MobileDeviceAndPartySalesman", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            }
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return successResult;
	}

	public static Map<String, Object> getSalesRouteScheduleByRoute(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) ctx.getDelegator();
        Map<String,Object> result = ServiceUtil.returnSuccess();
        String routeId = (String) context.get("routeId");
        try {
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> sch = delegator.findList("SalesRouteSchedule", EntityCondition.makeCondition(conds), null, null, null, false);
            result.put("listSalesRouteSchedule", sch);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListRouteBySalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            List<EntityCondition> conditions = FastList.newInstance();
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
            String salesmanId = "";
            if (parameters.containsKey("salesmanId")) {
                salesmanId = parameters.get("salesmanId")[0];
            }
            conditions.add(EntityCondition.makeCondition("statusId", SupUtil.ROUTE_ENABLED));
            conditions.add(EntityCondition.makeCondition("executorId", salesmanId));
            EntityListIterator listIterator= EntityMiscUtil.processIterator(parameters,result,delegator,"Route",
                    EntityCondition.makeCondition(conditions),null,null,listSortFields,opts);


            result.put("listIterator", listIterator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateRouteCustomerSequenceNum(DispatchContext dpct, Map<String, Object> context) {
        Map<String, Object> res = ServiceUtil.returnSuccess();
        Delegator delegator = dpct.getDelegator();
        String routeId = (String) context.get("routeId");
        String customerId = (String) context.get("customerId");
        Long sequenceNum = (Long) context.get("sequenceNum");
        List<EntityCondition> conds = FastList.newInstance();
        try {
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityCondition.makeCondition("sequenceNum", sequenceNum));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> routeCustomersRemoveSequence = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            for (GenericValue aRouteCustomer : routeCustomersRemoveSequence) {
                aRouteCustomer.set("sequenceNum", null);
            }
            delegator.storeAll(routeCustomersRemoveSequence);

            conds.clear();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> routeCustomers = delegator.findList("RouteCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
            for (GenericValue aRouteCustomer : routeCustomers) {
                aRouteCustomer.set("sequenceNum", sequenceNum);
            }
            delegator.storeAll(routeCustomers);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ServiceUtil.returnError(ex.getMessage());
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRouteSimple(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) dpct.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
        EntityListIterator listRoute = null;
        String salesmanId = null;
        try {
            if (parameters.containsKey("salesmanId") && parameters.get("salesmanId").length > 0) {
                salesmanId = (String) parameters.get("salesmanId")[0];
            }
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("managerId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("managerId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            }  else {
                isSearch = false;
            }

            if (isSearch){
                listAllConditions.add(EntityCondition.makeCondition("statusId", SupUtil.ROUTE_ENABLED));
                if (salesmanId != null && !salesmanId.equals("")) {
                    listAllConditions.add(EntityCondition.makeCondition("executorId", salesmanId));
                }
                listRoute = EntityMiscUtil.processIterator(parameters, returnSuccess, delegator, "Route",
                        EntityCondition.makeCondition(listAllConditions),null,null,listSortFields,opts);
            }
            returnSuccess.put("listIterator", listRoute);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error jqGetListRouteSimple");
        }
        return returnSuccess;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetEmpty(DispatchContext dpct, Map<String, ? extends Object> context) {
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        EntityListIterator listRoute = null;
        try {
            returnSuccess.put("listIterator", listRoute);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error jqGetEmpty");
        }
        return returnSuccess;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListCustomerPerRoute(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) dpct.getDelegator();
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> listRoute = FastList.newInstance();
        List<String> routeIds = FastList.newInstance();
        List<EntityCondition> conds = FastList.newInstance();
        String routeIdsStr = null;
        try {
            if (context.containsKey("routeIds") && context.get("routeIds") != null) {
                routeIdsStr = (String) context.get("routeIds");
            } else {
                return ServiceUtil.returnError("Error getListCustomerPerRoute");
            }
            routeIdsStr = routeIdsStr.substring(1, routeIdsStr.length() - 1);
            String[] routeIdsArr = routeIdsStr.split(",");
            for (int i = 0; i < routeIdsArr.length; i++) {
                String routeId = routeIdsArr[i].substring(1, routeIdsArr[i].length() - 1);
                routeIds.add(routeId);
            }
            conds.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routeIds));
            conds.add(EntityUtil.getFilterByDateExpr());
            listRoute= delegator.findList("RouteCustomerView", EntityCondition.makeCondition(conds),null,UtilMisc.toList("sequenceNum"),null,false);
            returnSuccess.put("routes", listRoute);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error getListCustomerPerRoute");
        }
        return returnSuccess;
    }

    public static Map<String, Object> getCustAvailableBySalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> customers = FastList.newInstance();
        try {
            String salesmanId = (String) context.get("salesmanId");
            String routeId = (String) context.get("routeId");
            if (salesmanId == null || routeId == null) {
                return ServiceUtil.returnError("error");
            }
            List<EntityCondition> condAssigned = FastList.newInstance();
            condAssigned.add(EntityUtil.getFilterByDateExpr());
            condAssigned.add(EntityCondition.makeCondition("routeId", routeId));
            List<String> custAssignedIds = EntityUtil.getFieldListFromEntityList(delegator.findList("RouteCustomerAndPartyCustomer",
                    EntityCondition.makeCondition(condAssigned),UtilMisc.toSet("customerId"),null,null,false),"customerId",true);
            Timestamp now = UtilDateTime.nowTimestamp();

            /*EntityCondition condTime0 = EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null);
            EntityCondition condTime1 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, now);
            EntityCondition condTime2 = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, now);
            listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(condTime0, condTime1, condTime2), EntityJoinOperator.OR));*/
            listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLogin.get("partyId")));
            listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
            if (UtilValidate.isNotEmpty(custAssignedIds)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN,custAssignedIds));
            }
            customers = delegator.findList("PartyCustomerDetailAndRoute",
                    EntityCondition.makeCondition(listAllConditions),null,null,null,false);

            successResult.put("customers", customers);
        } catch (Exception e) {
            return ServiceUtil.returnError("error getCustAvailableBySalesman");
        }
        return successResult;
    }

    public static Map<String, Object> loadRouteInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        try {
            Map<String, Object> routeInfo = FastMap.newInstance();
            String routeId = (String) context.get("routeId");
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("routeId", routeId));
            List<GenericValue> routes = delegator.findList("RouteViewDetail", EntityCondition.makeCondition(conds), null,null,null,false);
            if (UtilValidate.isNotEmpty(routes)) {
                GenericValue aRoute = routes.get(0);
                routeInfo.put("salesmanId", aRoute.get("salesmanId"));
                routeInfo.put("salesmanCode", aRoute.get("salesmanCode"));
                routeInfo.put("salesmanName", aRoute.get("salesmanName"));
                routeInfo.put("scheduleRoute", aRoute.get("scheduleRoute"));
                routeInfo.put("statusId", aRoute.get("statusId"));
                routeInfo.put("executorId", aRoute.get("executorId"));
                routeInfo.put("description", aRoute.get("description"));
                routeInfo.put("managerId", aRoute.get("managerId"));
                routeInfo.put("routeCode", aRoute.get("routeCode"));
                routeInfo.put("routeName", aRoute.get("routeName"));
                routeInfo.put("createdByUserLoginId", aRoute.get("createdByUserLoginId"));
                routeInfo.put("createdDate", aRoute.get("createdDate"));
            }
            routeInfo.put("routeId", routeId);
            result.put("routeInfo", routeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRouteVisitHistory(DispatchContext dpct, Map<String, ? extends Object> context) {
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> conds = FastList.newInstance();
		String fromDateStr = null;
		String thruDateStr = null;
		String salesmanId = null;
		Timestamp fromDate, thruDate;
		List<GenericValue> customers = FastList.newInstance();
		try {
			if (context.containsKey("fromDate") && context.get("fromDate") != null) {
				fromDateStr = (String) context.get("fromDate");
			}
			fromDate = new Timestamp(Long.parseLong(fromDateStr));
			if (context.containsKey("thruDate") && context.get("thruDate") != null) {
				thruDateStr = (String) context.get("thruDate");
			}
			thruDate = new Timestamp(Long.parseLong(thruDateStr));
			if (context.containsKey("salesmanId") && context.get("salesmanId") != null) {
				salesmanId = (String) context.get("salesmanId");
			}
			if (fromDateStr == null || thruDateStr == null || salesmanId == null) {
				return ServiceUtil.returnError("Error getRouteVisitHistory");
			}
			conds.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
			conds.add(EntityCondition.makeCondition("checkInDate", EntityOperator.LESS_THAN, UtilDateTime.getDayEnd(thruDate)));
			conds.add(EntityCondition.makeCondition("checkInOk", true));
			conds.add(EntityCondition.makeCondition("partyId", salesmanId));
			customers = delegator.findList("CheckInHistoryPartyCustomer", EntityCondition.makeCondition(conds), null, null, null, false);

			returnSuccess.put("customers", customers);
		} catch (Exception e) {
			Debug.log(e.getMessage());

			return ServiceUtil.returnError("Error getRouteVisitHistory");
		}
		return returnSuccess;
	}
}
