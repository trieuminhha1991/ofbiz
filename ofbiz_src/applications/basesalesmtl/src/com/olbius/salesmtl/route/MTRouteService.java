package com.olbius.salesmtl.route;


import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.salesmtl.util.SupUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import org.ofbiz.base.util.*;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTRouteService {
    public static final String module = MTRouteService.class.getName();
    public static final String resource = "BaseSalesMtlUiLabels";
    public static final String resource_error = "BaseSalesMtlErrorUiLabels";
    public static final String CHANNEL_MT = "SMCHANNEL_MT";
    public static final String CHANNEL_GT = "SMCHANNEL_GT";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListRouteMT(DispatchContext dpct, Map<String, ? extends Object> context) {
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
            for (EntityCondition cond : listAllConditions) {
                String condStr = cond.toString();
                if (condStr.contains("scheduleRoute") && condStr.contains("AND")) {
                    listAllConditions.remove(cond);
                } else if (condStr.contains("scheduleRoute")) {
                    listAllConditions.remove(cond);
                    Matcher matcher = pattern.matcher(condStr);
                    while (matcher.find()) {
                        conds.add(EntityCondition.makeCondition("scheduleRoute", EntityOperator.LIKE, "%" + matcher.group(0) + "%"));
                    }
                    listAllConditions.add(EntityCondition.makeCondition(conds, EntityJoinOperator.OR));
                }
            }
            //end process to filter routeSchedule
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = userLogin.getString("userLoginId");
            String userLoginPartyId = userLogin.getString("partyId");
            boolean isSearch = true;
            if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("executorId", userLoginPartyId));
            }

            if (isSearch) {
                listAllConditions.add(EntityCondition.makeCondition("statusId", SupUtil.ROUTE_ENABLED));
                listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", CHANNEL_MT));
                EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
                listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "RouteViewDetail",
                        cond, null, null, listSortFields, opts);
            }
            result.put("listIterator", listIterator);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get getListRouteMT");
        }
        return result;
    }

    public static Map<String, Object> createRouteMT(DispatchContext dpct, Map<String, ? extends Object> context) {
    	Locale locale = (Locale) context.get("locale");
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
            List<GenericValue> lr = SupUtil.getRouteListFromCode(delegator, partyCode);
            if (lr != null && lr.size() > 0) {                
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "BSRouteCodeHasAlreadyExisted", locale));
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
            r.put("salesMethodChannelEnumId", CHANNEL_MT);
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
                    sr.put("fromDate", new Timestamp(System.currentTimeMillis()));
                    sr.put("statusId", SupUtil.ENABLED);
                    delegator.create(sr);
                }
            res.put("routeId", routeId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static Map<String, Object> JQGetListRouteCustomerChangeSaleman(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) dpct.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String routeId = parameters.get("routeId")[0];
        String salesmanId = parameters.get("salemanId")[0];
        listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("routeId", routeId)));
        listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.NOT_EQUAL, salesmanId));

        listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null),EntityOperator.OR,
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
        Map<String, Object> result = ServiceUtil.returnSuccess();//FastMap.newInstance();
        EntityListIterator listIterator = null;
        try {
            listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "PartyCustomerAndRouteFullDetail",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            result.put("listIterator", listIterator);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get getListRouteMT");
        }
        return result;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateRouteMT(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = (Delegator) dpct.getDelegator();
        LocalDispatcher dispatcher = dpct.getDispatcher();
        String routeId = (String) context.get("routeId");
        String routeCode = (String) context.get("routeCode");
        String routeName = (String) context.get("routeName");
        String description = (String) context.get("description");
        String sche = (String) context.get("scheduleRoute");
        String weeks = (String) context.get("weeks");
        String salesmanId = (String) context.get("salesmanId");
        String isPartyChange = (String) context.get("isPartyChange");
        Locale locale = (Locale) context.get("locale");
        GenericValue curl = (GenericValue) context.get("userLogin");
        try {
            Map<String, Object> in = FastMap.newInstance();
            GenericValue userLogin = (GenericValue)context.get("userLogin");
            in.put("userLogin", userLogin);
            in.put("partyId", salesmanId);
            Map<String, Object> rs = dispatcher.runSync("getSupManagerN", in);
            String supId=(String) rs.get("partyIdTo");
            GenericValue route = delegator.findOne("Route", UtilMisc.toMap("routeId", routeId), false);
            route.set("routeCode", routeCode);
            route.set("routeName", routeName);
            route.set("description", description);
            route.set("weeks", weeks);
            route.set("executorId", salesmanId);
            route.set("managerId", supId);
            route.store();
            if (UtilValidate.isNotEmpty(isPartyChange) && isPartyChange.equals("Y") ) {
                List<GenericValue> routeCustomers = delegator.findList("PartyCustomerAndRouteFullDetail", EntityCondition.makeCondition(UtilMisc.toMap("routeId", routeId)), null, null, null, false);
                List<String> customerIds = new ArrayList<>();
                for (GenericValue gv : routeCustomers) {
                    customerIds.add((String) gv.get("partyId"));
                }

                List<GenericValue> partyCustomers = delegator.findList("PartyCustomer", EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds), null, null, null, false);
                for(int i=0;i<partyCustomers.size();i++){
                    GenericValue customer=partyCustomers.get(i);
                    customer.set("salesmanId",salesmanId);
                    customer.set("supervisorId",supId);
                    customer.store();
                }
            }
            // remove old schedule date and detail
            in = FastMap.newInstance();
            in.put("routeId", routeId);
            rs = dispatcher.runSync("removeSaleRouteScheduleAndDetailDate", in);

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
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "updateRouteError", locale));
        }
        return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "updateRouteSuccess", locale));
    }

    public static Map<String, Object> deleteRouteMT(DispatchContext dpct, Map<String, ? extends Object> context) {
        Delegator delegator = dpct.getDelegator();
        String routeId = (String) context.get("routeId");
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        List<EntityCondition> conds = FastList.newInstance();
        try {
            if (routeId == null) {
                ServiceUtil.returnError("error deleteRoute");
            }
            //Disable route
            GenericValue aRoute = delegator.findOne("Route", UtilMisc.toMap("routeId", routeId), false);
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

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListSalesmanManagementMT(DispatchContext dpct, Map<String, ? extends Object> context) throws GenericEntityException {
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

            //get all salesmanMt
            List<String> salesmanMTId = FastList.newInstance();
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
            conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALESMAN_MT"));
            conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> salesmanMTRls = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
            salesmanMTId = EntityUtil.getFieldListFromEntityList(salesmanMTRls, "partyIdFrom", true);
            //end get all salesmanMt
            opt.setDistinct(true);
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("partyCode");
            }
            listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"),
                    EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
            listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, salesmanMTId));
            EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
            List<String> fieldToSelect = UtilMisc.toList("partyId", "partyCode", "fullName");
            list = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartySalesman", cond, null, UtilMisc.toSet(fieldToSelect), listSortFields, opt);
            successResult.put("listIterator", list);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get getListSalesmanManagementMT");
        }
        return successResult;
    }

    public static Map<String, Object> jqGetCustAssignedToRouteMT(DispatchContext ctx, Map<String, ? extends Object> context) {
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

    public static Map<String, Object> jqGetCustAvailableMT(DispatchContext ctx, Map<String, ? extends Object> context) {
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
                    EntityCondition.makeCondition(condAssigned), UtilMisc.toSet("customerId"), null, null, false), "customerId", true);
            Timestamp now = UtilDateTime.nowTimestamp();
            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", CHANNEL_MT));
            listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
            if (UtilValidate.isNotEmpty(custAssignedIds)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, custAssignedIds));
            }
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("partyCode");
            fieldsToSelect.add("fullName");
            fieldsToSelect.add("postalAddressName");
            fieldsToSelect.add("salesmanId");
            fieldsToSelect.add("supervisorId");
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyCustomerAddressGeoPoint",
                    EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toSet(fieldsToSelect), listSortFields, opts);
            successResult.put("listIterator", listIterator);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("error");
        }
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRouteSimpleMT(DispatchContext dpct, Map<String, ? extends Object> context) {
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

            listAllConditions.add(EntityCondition.makeCondition("statusId", SupUtil.ROUTE_ENABLED));
            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", CHANNEL_MT));
            if (salesmanId != null && !salesmanId.equals("")) {
                listAllConditions.add(EntityCondition.makeCondition("executorId", salesmanId));
            }
            listRoute = EntityMiscUtil.processIterator(parameters, returnSuccess, delegator, "Route",
                    EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

            returnSuccess.put("listIterator", listRoute);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error jqGetListRouteSimpleMT");
        }
        return returnSuccess;
    }

    public static Map<String, Object> getCustAvailableBySalesmanMT(DispatchContext ctx, Map<String, ? extends Object> context) {
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
                    EntityCondition.makeCondition(condAssigned), UtilMisc.toSet("customerId"), null, null, false), "customerId", true);
            Timestamp now = UtilDateTime.nowTimestamp();
            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", CHANNEL_MT));
            //listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLogin.get("partyId")));
            //listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
            listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
            if (UtilValidate.isNotEmpty(custAssignedIds)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, custAssignedIds));
            }
            customers = delegator.findList("PartyCustomerDetailAndRoute",
                    EntityCondition.makeCondition(listAllConditions), null, null, null, false);

            successResult.put("customers", customers);
        } catch (Exception e) {
            return ServiceUtil.returnError("error getCustAvailableBySalesmanMT");
        }
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListMobileDeviceLogMT(DispatchContext dpct, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = (Delegator) dpct.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        try {
            //get all salesmanMt
            List<String> salesmanMTId = FastList.newInstance();
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
            conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALESMAN_MT"));
            conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
            conds.add(EntityUtil.getFilterByDateExpr());
            List<GenericValue> salesmanMTRls = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
            salesmanMTId = EntityUtil.getFieldListFromEntityList(salesmanMTRls, "partyIdFrom", true);
            //end get all salesmanMt
            opts.setDistinct(true);
            listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, salesmanMTId));
            listSortFields.add("-updatedTime");
            listSortFields.add("partyCode");
            listSortFields.add("deviceId");
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator,
                    "MobileDeviceAndPartySalesman", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            successResult.put("listIterator", listIterator);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get jqGetListMobileDeviceLogMT");
        }
        return successResult;
    }

}
