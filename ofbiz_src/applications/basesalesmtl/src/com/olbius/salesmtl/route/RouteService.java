package com.olbius.salesmtl.route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
/*
 * by: dungkhmt
 * start date: 2017-08-11
 */
public class RouteService {
	public static final String module = RouteService.class.getName();
	
	public static Map<String, Object> correctCoordinateGeoPointVN(DispatchContext ctx, 
			Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		Delegator delegator = ctx.getDelegator();
		
		try{
			List<GenericValue> GP = delegator.findList("GeoPoint",null,null,null,null,false);
			int count = 0;
			for(GenericValue gv: GP){
				double lat = (Double)gv.get("latitude");
				double lng = (Double)gv.get("longitude");
				if(lat > 30 || lng > 200){
					count++;
					Debug.log(module + "::correctCoordinateGeoPointVN, " + count + ", inormal coordinate (" + lat + 
							"," + lng);
					
					while(lat > 30) lat = lat/10.0;
					while(lng > 200) lng = lng/10.0;
					gv.put("latitude",lat);
					gv.put("longitude",lng);
					Debug.log(module + "::correctCoordinateGeoPointVN, after correcting coordinate (" + lat + 
							"," + lng);
					
					delegator.store(gv);
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
		
	}
	
	public static Map<String, Object> getCustomersOfASalesman(DispatchContext ctx, 
			Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String salesmanId = (String)context.get("partyId");
		
		try{
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("salesmanId",EntityOperator.EQUALS,salesmanId));
			
			List<GenericValue> lst = delegator.findList("PartyCustomerAddressGeoPointAndSalesman", 
					EntityCondition.makeCondition(conds), null,null,null,false);
			retSucc.put("customers", lst);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retSucc;
		/*
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		
		try{
			List<EntityCondition> conds = FastList.newInstance();
			//conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS,"SALES_EXECUTIVE"));
			//conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS,"CUSTOMER"));
			//conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"SALES_REP_REL"));
			conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,partyId));
			conds.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PARTY_ENABLED"));
			conds.add(EntityUtil.getFilterByDateExpr("SRFromDate","SRThruDate"));
			
			//List<GenericValue> customers = delegator.findList("PartyRelationship",
			List<GenericValue> customers = delegator.findList("CustomerSalesman",
					EntityCondition.makeCondition(conds), 
					null, 
					null, 
					null, 
					false);
			
			Debug.log(module + "::getCustomersOfASalesman, GOT customers.sz = " + customers.size());
			
			retSucc.put("customers", customers);
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
		*/
	}
	
	public static Map<String, Object> getVisitingRoutesOfACustomer(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String customerId = (String)context.get("partyId");
		try{
			List<GenericValue> routes = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("customerId",customerId));
			routes = delegator.findList("RouteCustomerAndCustomerAndScheduleAvail",EntityCondition.makeCondition(conds),null,null,null,false);
			retSucc.put("routes", routes);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> getCustomerNoneRoute(DispatchContext ctx, 
			Map<String, ? extends Object> context){
		
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		Delegator delegator = ctx.getDelegator();
		
		String salesmanId = (String)context.get("salesmanId");
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> inputGetStores = FastMap.newInstance();
		inputGetStores.put("partyId", salesmanId);
		inputGetStores.put("userLogin", context.get("userLogin"));
		inputGetStores.put("timeZone", context.get("timeZone"));
		inputGetStores.put("locale", context.get("locale"));
		try {
			Debug.log(module + "::getCustomerNoneRoute: salesmanId = " + salesmanId);
			Map<String, Object> resultGetCustomers = dispatcher.runSync("getCustomersOfASalesman", inputGetStores);
			List<Map<String, Object>> customers = (List<Map<String, Object>>) resultGetCustomers.get("customers");
			List<Map<String, Object>> ret_routes = new ArrayList<Map<String, Object>>();
			Debug.log(module + "::getCustomerNoneRoute, GOT customers.sz = " + customers.size());
			for(Map<String, Object> customer: customers) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,customer.get("customerId")));
				conditions.add(EntityCondition.makeCondition("routeStatusId",EntityOperator.EQUALS,"PARTY_ENABLED"));
				conditions.add(EntityUtil.getFilterByDateExpr("fromDate","thruDate"));
				Debug.log(module + "::getCustomerNoneRoute, GOT customerId = " + customer.get("customerId"));
				List<GenericValue> routes =  delegator.findList("CustomerRoutes", 
						EntityCondition.makeCondition(conditions), 
						//null,
						null, 
						null, 
						null, 
						false);
				if(routes.size()==0) {
					ret_routes.add(customer);
				}
				Debug.log(module + "::getCustomerNoneRoute, GOT routes.size = " + routes.size());
				Debug.log(module + "::getCustomerNoneRoute, GOT ret_routes.size = " + ret_routes.size());
			}
		
			Debug.log(module + "::getCustomerNoneRoute, GOT routes.sz = " + ret_routes.size());
			retSucc.put("customers", ret_routes);
			return retSucc;
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	/*
	 * return a sequence of outlets visited by the given salesman on each day 
	 */
	public static Map<String, Object> getRouteDaySalesman(DispatchContext ctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> retSucc = ServiceUtil.returnSuccess();
        String salesmanId = (String) context.get("salesmanId");
        String scheduleRoute = (String) context.get("scheduleRoute");
        List<EntityCondition> conditions = FastList.newInstance();

        conditions.add(EntityCondition.makeCondition("executorId", salesmanId));
        if (UtilValidate.isNotEmpty(scheduleRoute)) {
            conditions.add(EntityCondition.makeCondition("scheduleRoute", EntityOperator.EQUALS, scheduleRoute));
        }
        try {
            List<GenericValue> routes = delegator.findList("RouteAndSalesRouteSchedule",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            HashMap<String, List<String>> mapDayOfRoutes = new HashMap<String, List<String>>();
            for (GenericValue gv : routes) {
                String routeId = (String) gv.get("routeId");
                String day = (String) gv.get("scheduleRoute");
                if (mapDayOfRoutes.get(day) == null) {
                    mapDayOfRoutes.put(day, new ArrayList<String>());
                }
                mapDayOfRoutes.get(day).add(routeId);
            }
            Map<String, List<GenericValue>> retRoutes = FastMap.newInstance();
            for (String day : mapDayOfRoutes.keySet()) {
                List<GenericValue> customersOfRoute = FastList.newInstance();
                List<String> routeIds = mapDayOfRoutes.get(day);

                List<EntityCondition> condi = FastList.newInstance();
                condi.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routeIds));
                condi.add(EntityUtil.getFilterByDateExpr());
                customersOfRoute = delegator.findList("RouteCustomerView", EntityCondition.makeCondition(condi), null, null, null, false);

                Map<String, Object> input = FastMap.newInstance();
                List<GenericValue> validCustomers = FastList.newInstance();
                List<GenericValue> invalidCustomers = FastList.newInstance();
                for (GenericValue ci : customersOfRoute) {
                    if (ci.get("latitude") != null && ci.get("longitude") != null) {
                        validCustomers.add(ci);
                    } else {
                        invalidCustomers.add(ci);
                    }
                }
                input.put("customers", validCustomers);
                List<GenericValue> listCustomer = validCustomers;
                for (GenericValue ci : invalidCustomers) {
                    listCustomer.add(ci);
                }
                retRoutes.put(day, listCustomer);
            }
            retSucc.put("routes", retRoutes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ServiceUtil.returnError(ex.getMessage());
        }
        return retSucc;
    }
	
	public static Map<String, Object> changeScheduleRouteSalesman(DispatchContext ctx,
			Map<String, Object> context){
		
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String salesmanId = (String)context.get("salesmanId");
		String storeId = (String)context.get("storeId");
		String scheduleRoute = (String)context.get("scheduleRoute");
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		
		conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,salesmanId));
		conditions.add(EntityCondition.makeCondition("storeId",EntityOperator.EQUALS,storeId));

		try{
			List<GenericValue> routes = delegator.findList("PartyRelationshipRouteDetail", 
					EntityCondition.makeCondition(conditions), 
					null, 
					null,
					null,
					false);
			
			Debug.log(module + "::getRouteDaySalesman, salesmanId = " + salesmanId + ", routes.sz = " + routes.size());
			
			for(GenericValue gv: routes){
				Debug.log(module + "::changeRouteDaySalesman, salesmanId = " + salesmanId + ", route " + gv.get("routeId") 
						+ ", schedule " + gv.get("scheduleRoute"));
				gv.set("scheduleRoute", scheduleRoute);
			}
			delegator.storeAll(routes);
			
			Map<String, Object> input = FastMap.newInstance();
			input.put("salesmanId",salesmanId);
			input.put("scheduleRoute",scheduleRoute);
			Map<String, Object> resultGetRouteDaySalesman = dispatcher.runSync("getRouteDaySalesman", input);
			
			retSucc.put("routes", resultGetRouteDaySalesman.get("routes"));
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
}
