
package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import org.ofbiz.Mobile;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.util.Calendar;

import javolution.util.FastMap;

public class ReportServices implements Mobile {

	public static final String module = ReportServices.class.getName();
	public static final String resource = "AppBaseUiLabels";


	
	public static Map<String, Object> getSalesReportDataWithTime(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String) context.get("partyId");
		String time = (String) context.get("time");
		String service = (String) context.get("service");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isEmpty(partyId)){
				partyId = userLogin.getString("partyId");
				context.put("partyId", partyId);
			}
			Map<String, Object> dm = getTime(time, context);
			context.putAll(dm);
			context.remove("service");
			context.remove("time");
			res = dispatcher.runSync(service, context);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> getOrderTotalReport(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String time = (String) context.get("time");
		String service = (String) context.get("service");
		try {
			Map<String, Object> dm = getTime(time, context);
			context.putAll(dm);
			context.remove("service");
			context.remove("time");
			context.put("flag", "b");
			res = dispatcher.runSync(service, context);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> getSalesmanTopReport(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String time = (String) context.get("time");
		String service = (String) context.get("service");
		try {
			Map<String, Object> dm = getTime(time, context);
			context.putAll(dm);
			context.remove("service");
			context.remove("time");
			res = dispatcher.runSync(service, context);
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> getTime(String time, Map<String, Object> context){
		Map<String, Object> res = FastMap.newInstance();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp fromDate = null;
		Timestamp thruDate = UtilDateTime.nowTimestamp();
		switch(time){
			case "WEEK" :
				cal.set(Calendar.DAY_OF_WEEK, 2);
				fromDate = new Timestamp(cal.getTimeInMillis());
				break;
			case "MONTH" :
				cal.set(Calendar.DAY_OF_MONTH, 0);
				fromDate = new Timestamp(cal.getTimeInMillis());
				break;
			case "YEAR" :
				cal.set(Calendar.DAY_OF_YEAR, 1);
				fromDate = new Timestamp(cal.getTimeInMillis());
				break;
			case "PREVYEAR" :
				cal.add(Calendar.YEAR, -1);
				fromDate = new Timestamp(cal.getTimeInMillis());
				break;
			case "TODAY" :
				fromDate = new Timestamp(cal.getTimeInMillis());
				break;
			case "RANGE" :
				fromDate = (Timestamp) context.get("fromDate");
				thruDate = (Timestamp) context.get("thruDate");
				break;
		}
		res.put("fromDate", fromDate);
		res.put("thruDate", thruDate);
		return res;
	}

    public static Map<String, Object> mGetCustomerReports(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        boolean recalculateCurrentSales = false;
        if (!context.containsKey("customerId")) {
            return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCustomerNotFound", locale));
        }
        String customerId = (String) context.get("customerId");
        List<GenericValue> itemLines = FastList.newInstance();
        try {
            //get DateDimension
            String yearAndMonthCurrent = "", yearAndMonthLastMonth = "", yearAndMonthTwoMonthAgo = "", yearAndMonthThreeMonthAgo = "";
            Calendar calendarAtThisTime = Calendar.getInstance();
            calendarAtThisTime.setTime(new Date(System.currentTimeMillis()));
            if (calendarAtThisTime.get(Calendar.MONTH) < 9) {
                yearAndMonthCurrent = calendarAtThisTime.get(Calendar.YEAR) + "-0" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            } else {
                yearAndMonthCurrent = calendarAtThisTime.get(Calendar.YEAR) + "-" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            }
            calendarAtThisTime.add(Calendar.MONTH, -1);
            if (calendarAtThisTime.get(Calendar.MONTH) < 9) {
                yearAndMonthLastMonth = calendarAtThisTime.get(Calendar.YEAR) + "-0" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            } else {
                yearAndMonthLastMonth = calendarAtThisTime.get(Calendar.YEAR) + "-" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            }
            calendarAtThisTime.add(Calendar.MONTH, -1);
            if (calendarAtThisTime.get(Calendar.MONTH) < 9) {
                yearAndMonthTwoMonthAgo = calendarAtThisTime.get(Calendar.YEAR) + "-0" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            } else {
                yearAndMonthTwoMonthAgo = calendarAtThisTime.get(Calendar.YEAR) + "-" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            }
            calendarAtThisTime.add(Calendar.MONTH, -1);
            if (calendarAtThisTime.get(Calendar.MONTH) < 9) {
                yearAndMonthThreeMonthAgo = calendarAtThisTime.get(Calendar.YEAR) + "-0" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            } else {
                yearAndMonthThreeMonthAgo = calendarAtThisTime.get(Calendar.YEAR) + "-" + (calendarAtThisTime.get(Calendar.MONTH) + 1);
            }

            listAllConditions.clear();
            List<String> yearAndMonths = FastList.newInstance();
            Map<Long, Object> fromDimToYearAndMonth = FastMap.newInstance();
            yearAndMonths.add(yearAndMonthCurrent);
            yearAndMonths.add(yearAndMonthLastMonth);
            yearAndMonths.add(yearAndMonthTwoMonthAgo);
            yearAndMonths.add(yearAndMonthThreeMonthAgo);
            listAllConditions.add(EntityCondition.makeCondition("yearAndMonth", EntityOperator.IN, yearAndMonths));
            List<GenericValue> dateDimensions = delegator.findList("DateDimension",
                    EntityCondition.makeCondition(listAllConditions), null, null, null, false);
            for (GenericValue gv : dateDimensions) {
                for (String yearAndMonth : yearAndMonths) {
                    if (yearAndMonth.equals(gv.getString("yearAndMonth"))) {
                        fromDimToYearAndMonth.put(gv.getLong("dimensionId"), yearAndMonth);
                    }
                }
            }

            List<Long> dateDimIds = EntityUtil.getFieldListFromEntityList(dateDimensions, "dimensionId", true);

            //get customerDimId
            List<GenericValue> partyDimensions = delegator.findList("PartyDimension",
                    EntityCondition.makeCondition("partyId", customerId), null, null, null, false);
            if (UtilValidate.isEmpty(partyDimensions)) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCustomerNotFound", locale));
            }
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("isPromo", "N"));
            listAllConditions.add(EntityCondition.makeCondition("customerDimId", partyDimensions.get(0).getLong("dimensionId")));
            listAllConditions.add(EntityCondition.makeCondition("orderDateDimId", EntityOperator.IN, dateDimIds));
            listAllConditions.add(EntityCondition.makeCondition("orderStatusId", "ORDER_COMPLETED"));
            listAllConditions.add(EntityCondition.makeCondition("orderItemStatusId", "ITEM_COMPLETED"));
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("orderDateDimId");
            fieldsToSelect.add("orderDate");
            fieldsToSelect.add("orderId");
            fieldsToSelect.add("productDimId");
            fieldsToSelect.add("totalAmount");
            fieldsToSelect.add("isPromo");
            fieldsToSelect.add("customerDimId");
            List<GenericValue> orderItems = delegator.findList("SalesOrderNewFact",
                    EntityCondition.makeCondition(listAllConditions), UtilMisc.toSet(fieldsToSelect), UtilMisc.toList("-orderDate"), null, false);
            //calculate averageSales
            List<GenericValue> currentItems = FastList.newInstance();
            List<GenericValue> lastMonthItems = FastList.newInstance();
            List<GenericValue> twoMonthAgoItems = FastList.newInstance();
            List<GenericValue> threeMonthAgoItems = FastList.newInstance();
            for (GenericValue orderItem : orderItems) {
                String yam = (String) fromDimToYearAndMonth.get(orderItem.getLong("orderDateDimId"));
                if (yam.equals(yearAndMonthCurrent)) {
                    currentItems.add(orderItem);
                } else if (yam.equals(yearAndMonthLastMonth)) {
                    lastMonthItems.add(orderItem);
                } else if (yam.equals(yearAndMonthTwoMonthAgo)) {
                    twoMonthAgoItems.add(orderItem);
                } else if (yam.equals(yearAndMonthThreeMonthAgo)) {
                    threeMonthAgoItems.add(orderItem);
                }
            }

            BigDecimal currentSales = BigDecimal.ZERO;
            BigDecimal lastMonthSales = BigDecimal.ZERO;
            BigDecimal twoMonthAgoSales = BigDecimal.ZERO;
            BigDecimal threeMonthAgoSales = BigDecimal.ZERO;
            Long currentNumberOfOrder = 0L, lastMonthNumberOfOrder = 0L, twoMonthAgoNumberOfOrder = 0L, threeMonthAgoNumberOfOrder = 0L;
            Long currentSku = 0L, lastMonthSku = 0L, twoMonthAgoSku = 0L, threeMonthAgoSku = 0L;

            List<GenericValue> tmpOrders = FastList.newInstance();
            List<String> tmpOrderIds = FastList.newInstance();
            List<String> tmpProductDimIds = FastList.newInstance();
            List<Map<String, Object>> lastFiveOrders = FastList.newInstance();
            Map<String, Object> anItem = FastMap.newInstance();

            //get last five order
            Map<String, Object> result = dispatcher.runSync("mGetLastFiveOrders", context);
            if (UtilValidate.isNotEmpty(result) && UtilValidate.isNotEmpty(result.get("orders"))) {
                lastFiveOrders = (List<Map<String,Object>>)result.get("orders");
            }

            if (UtilValidate.isNotEmpty(orderItems)) {
                GenericValue tempItem = orderItems.get(0); // last orderItem by report
                for (Map<String, Object> item: lastFiveOrders) {
                    if (tempItem.getTimestamp("orderDate").before ((Timestamp) item.get("orderDate"))) {
                        recalculateCurrentSales = true;
                        break;
                    }
                }
            }

            //calculate for current month
            Map<String, Object> todaySales = FastMap.newInstance();
            if (recalculateCurrentSales) {
                todaySales = mGetTodayOrderByCustomers(ctx, context);
            }
            if (UtilValidate.isNotEmpty(todaySales)) {
                tmpOrders = (List<GenericValue>)todaySales.get("orders");
                tmpOrderIds = (List<String>)todaySales.get("orderIds");
                tmpProductDimIds  = (List<String>)todaySales.get("productDimIds");
                currentSku = Long.valueOf(tmpProductDimIds.size());
                currentNumberOfOrder = Long.valueOf(tmpOrderIds.size());
                for (GenericValue tmpOrder : tmpOrders) {
                    currentSales = currentSales.add(tmpOrder.getBigDecimal("grandTotal"));
                }
            }

            for (GenericValue item : currentItems) {
                if (!tmpOrderIds.contains(item.getString("orderId"))) {
                    tmpOrderIds.add(item.getString("orderId"));
                    currentNumberOfOrder += 1;
                }
                if (!tmpProductDimIds.contains(item.getString("productDimId"))) {
                    tmpProductDimIds.add(item.getString("productDimId"));
                    currentSku += 1;
                }
                if (item.getTimestamp("orderDate").before(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))) {
                    currentSales = currentSales.add(item.getBigDecimal("totalAmount"));
                }
            }
            //calculate for last month
            tmpOrderIds.clear();
            tmpProductDimIds.clear();
            for (GenericValue item : lastMonthItems) {
                if (!tmpOrderIds.contains(item.getString("orderId"))) {
                    tmpOrderIds.add(item.getString("orderId"));
                    lastMonthNumberOfOrder += 1;
                }
                if (!tmpProductDimIds.contains(item.getString("productDimId"))) {
                    tmpProductDimIds.add(item.getString("productDimId"));
                    lastMonthSku += 1;
                }
                lastMonthSales = lastMonthSales.add(item.getBigDecimal("totalAmount"));
            }
            //calculate for two month ago
            tmpOrderIds.clear();
            tmpProductDimIds.clear();
            for (GenericValue item : twoMonthAgoItems) {
                if (!tmpOrderIds.contains(item.getString("orderId"))) {
                    tmpOrderIds.add(item.getString("orderId"));
                    twoMonthAgoNumberOfOrder += 1;
                }
                if (!tmpProductDimIds.contains(item.getString("productDimId"))) {
                    tmpProductDimIds.add(item.getString("productDimId"));
                    twoMonthAgoSku += 1;
                }
                twoMonthAgoSales = twoMonthAgoSales.add(item.getBigDecimal("totalAmount"));
            }
            //calculate for three month ago
            tmpOrderIds.clear();
            tmpProductDimIds.clear();
            for (GenericValue item : threeMonthAgoItems) {
                if (!tmpOrderIds.contains(item.getString("orderId"))) {
                    tmpOrderIds.add(item.getString("orderId"));
                    threeMonthAgoNumberOfOrder += 1;
                }
                if (!tmpProductDimIds.contains(item.getString("productDimId"))) {
                    tmpProductDimIds.add(item.getString("productDimId"));
                    threeMonthAgoSku += 1;
                }
                threeMonthAgoSales = threeMonthAgoSales.add(item.getBigDecimal("totalAmount"));
            }

            successResult.put("lastFiveOrders", lastFiveOrders);
            successResult.put("currentNumberOfOrder",currentNumberOfOrder);
            successResult.put("currentSales",currentSales);
            successResult.put("currentSku",currentSku);

            successResult.put("lastMonthNumberOfOrder",lastMonthNumberOfOrder);
            successResult.put("lastMonthSales",lastMonthSales);
            successResult.put("lastMonthSku",lastMonthSku);

            successResult.put("twoMonthAgoNumberOfOrder",twoMonthAgoNumberOfOrder);
            successResult.put("twoMonthAgoSales",twoMonthAgoSales);
            successResult.put("twoMonthAgoSku",twoMonthAgoSku);

            successResult.put("threeMonthAgoNumberOfOrder",threeMonthAgoNumberOfOrder);
            successResult.put("threeMonthAgoSales",threeMonthAgoSales);
            successResult.put("threeMonthAgoSku",threeMonthAgoSku);

        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String,Object> mGetLastFiveOrders (DispatchContext ctx, Map<String, Object> context) {
	    Map<String, Object> successReturn = ServiceUtil.returnSuccess();
	    Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!context.containsKey("customerId")) {
            return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCustomerNotFound", locale));
        }
        String customerId = (String) context.get("customerId");
        List<GenericValue> orders = FastList.newInstance();
        try {
            EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setLimit(5);
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("orderId");
            fieldsToSelect.add("customerId");
            fieldsToSelect.add("sellerId");
            fieldsToSelect.add("orderDate");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("grandTotal");
            orders = delegator.findList("OrderHeaderAndOrderRoleFromTo",
                    EntityCondition.makeCondition("customerId", customerId),UtilMisc.toSet(fieldsToSelect), UtilMisc.toList("-orderDate"),findOptions,false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successReturn.put("orders", orders);
        return successReturn;
    }

    public static Map<String,Object> mGetTodayOrderByCustomers (DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> conds = FastList.newInstance();
        if (!context.containsKey("customerId")) {
            return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesCustomerNotFound", locale));
        }
        String customerId = (String) context.get("customerId");
        List<GenericValue> orders = FastList.newInstance();
        List<String> productIds = FastList.newInstance();
        List<String> productDimIds = FastList.newInstance();
        List<String> orderIds = FastList.newInstance();
        try {
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("orderId");
            fieldsToSelect.add("customerId");
            fieldsToSelect.add("sellerId");
            fieldsToSelect.add("orderDate");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("grandTotal");
            Timestamp beginOfDay = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
            conds.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfDay));
            conds.add(EntityCondition.makeCondition("customerId", customerId));
            orders = delegator.findList("OrderHeaderAndOrderRoleFromTo",
                    EntityCondition.makeCondition(conds),UtilMisc.toSet(fieldsToSelect), UtilMisc.toList("-orderDate"),null,false);

            orderIds = EntityUtil.getFieldListFromEntityList(orders, "orderId", false);
            List<GenericValue> orderItems  = FastList.newInstance();
            if (UtilValidate.isNotEmpty(orderIds)) {
                conds.clear();
                conds.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
                conds.add(EntityCondition.makeCondition("isPromo", "N"));
                conds.add(EntityCondition.makeCondition("statusId", "ITEM_COMPLETED"));
                orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conds), null,null,null,false);
            }
            productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
            conds.clear();
            if (UtilValidate.isNotEmpty(productIds)) {
                conds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
                List<GenericValue> productDims = delegator.findList("ProductDimension", EntityCondition.makeCondition(conds), UtilMisc.toSet("dimensionId"),null,null, false);
                productDimIds = EntityUtil.getFieldListFromEntityList(productDims, "dimensionId", true);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successResult.put("orders", orders);
        successResult.put("orderIds", orderIds);
        successResult.put("productIds", productIds);
        successResult.put("productDimIds", productDimIds);
        return successResult;
    }

    public static Map<String,Object> mGetTodayOrderBySalesmans (DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
        List<EntityCondition> conds = FastList.newInstance();
        List<GenericValue> orders = FastList.newInstance();
        List<String> productIds = FastList.newInstance();
        List<String> productDimIds = FastList.newInstance();
        List<String> orderIds = FastList.newInstance();
        List<GenericValue> orderItems  = FastList.newInstance();
        try {
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("orderId");
            fieldsToSelect.add("customerId");
            fieldsToSelect.add("sellerId");
            fieldsToSelect.add("orderDate");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("grandTotal");
            Timestamp beginOfDay = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
            conds.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfDay));
            conds.add(EntityCondition.makeCondition("createdBy", userLoginPartyId));
            orders = delegator.findList("OrderHeaderAndOrderRoleFromTo",
                    EntityCondition.makeCondition(conds),UtilMisc.toSet(fieldsToSelect), UtilMisc.toList("-orderDate"),null,false);

            orderIds = EntityUtil.getFieldListFromEntityList(orders, "orderId", false);
            if (UtilValidate.isNotEmpty(orderIds)) {
                conds.clear();
                conds.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
                conds.add(EntityCondition.makeCondition("isPromo", "N"));
                conds.add(EntityCondition.makeCondition("statusId", "ITEM_COMPLETED"));
                orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conds), null,null,null,false);
            }
            productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
            conds.clear();
            if (UtilValidate.isNotEmpty(productIds)) {
                conds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
                List<GenericValue> productDims = delegator.findList("ProductDimension", EntityCondition.makeCondition(conds), UtilMisc.toSet("dimensionId"),null,null, false);
                productDimIds = EntityUtil.getFieldListFromEntityList(productDims, "dimensionId", true);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successResult.put("orders", orders);
        successResult.put("orderIds", orderIds);
        successResult.put("orderItems", orderItems);
        successResult.put("productIds", productIds);
        successResult.put("productDimIds", productDimIds);
        return successResult;
    }

    @SuppressWarnings({"unchecked"})
    public static Map<String, Object> mGetTurnoverBySalesmanReports(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
        String userLoginId = userLogin.getString("userLoginId");
        try {
            /*get DateDimension*/
            String yearAndMonthCurrent = "", dateValueToday = "";
            Long dayOfMonth = 0L, month = 0L, year = 0L;
            Timestamp timeAtThisTime = new Timestamp(System.currentTimeMillis());
            Calendar calendarAtThisTime = Calendar.getInstance();
            calendarAtThisTime.setTime(new Date(System.currentTimeMillis()));
            dayOfMonth = Long.valueOf(calendarAtThisTime.get(Calendar.DAY_OF_MONTH));
            month = Long.valueOf(calendarAtThisTime.get(Calendar.MONTH));
            year = Long.valueOf(calendarAtThisTime.get(Calendar.YEAR));
            if (month < 9) {
                yearAndMonthCurrent = year + "-0" + (month + 1);
                if (dayOfMonth < 10) {
                    dateValueToday = yearAndMonthCurrent + "-0" + dayOfMonth;
                } else {
                    dateValueToday = yearAndMonthCurrent + "-" + dayOfMonth;
                }
            } else {
                yearAndMonthCurrent = year + "-" + (month + 1);
                if (dayOfMonth < 10) {
                    dateValueToday = yearAndMonthCurrent + "-0" + dayOfMonth;
                } else {
                    dateValueToday = yearAndMonthCurrent + "-" + dayOfMonth;
                }
            }

            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("yearAndMonth", yearAndMonthCurrent));
            listAllConditions.add(EntityCondition.makeCondition("dayOfMonth", EntityOperator.LESS_THAN, dayOfMonth));
            List<GenericValue> dateDimensionCurrentMonths = delegator.findList("DateDimension",
                    EntityCondition.makeCondition(listAllConditions), null, null, null, false);
            List<GenericValue> todayDateDimensions = delegator.findList("DateDimension", EntityCondition.makeCondition("yearMonthDay", dateValueToday), null, null, null, false);
            if (UtilValidate.isEmpty(dateDimensionCurrentMonths) || UtilValidate.isEmpty(todayDateDimensions)) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesDateNotFound", locale));
            }
            List<Long> dateDimensionIds = EntityUtil.getFieldListFromEntityList(dateDimensionCurrentMonths, "dimensionId", true);
            Long todayDateDimensionId = todayDateDimensions.get(0).getLong("dimensionId");

            /*get salesmanDimId*/
            List<GenericValue> partyDimensions = delegator.findList("PartyDimension",
                    EntityCondition.makeCondition("partyId", userLoginPartyId), null, null, null, false);
            if (UtilValidate.isEmpty(partyDimensions)) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesUiLabels", "MobileServicesSalesmanNotFound", locale));
            }
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("isPromo", "N"));
            listAllConditions.add(EntityCondition.makeCondition("creatorDimId", partyDimensions.get(0).getLong("dimensionId")));
            listAllConditions.add(EntityCondition.makeCondition("orderDateDimId", EntityOperator.IN, dateDimensionIds));
            listAllConditions.add(EntityCondition.makeCondition("orderStatusId", "ORDER_COMPLETED"));
            listAllConditions.add(EntityCondition.makeCondition("orderItemStatusId", "ITEM_COMPLETED"));
            List<String> fieldsToSelect = FastList.newInstance();
            fieldsToSelect.add("orderDateDimId");
            fieldsToSelect.add("orderDate");
            fieldsToSelect.add("orderId");
            fieldsToSelect.add("productDimId");
            fieldsToSelect.add("totalAmount");
            fieldsToSelect.add("isPromo");
            fieldsToSelect.add("customerDimId");
            List<GenericValue> currentMonthItems = delegator.findList("SalesOrderNewFact",
                    EntityCondition.makeCondition(listAllConditions), UtilMisc.toSet(fieldsToSelect), UtilMisc.toList("-orderDate"), null, false);
            /*calculate averageSales*/
            BigDecimal currentMonthSales = BigDecimal.ZERO;
            Long currentMonthNumberOfOrder = 0L;
            Long currentMonthSku = 0L;
            BigDecimal currentMonthQuantity = BigDecimal.ZERO;
            BigDecimal currentMonthQuantityPerSku = BigDecimal.ZERO;

            List<GenericValue> tmpOrders = FastList.newInstance();
            List<String> tmpOrderIds = FastList.newInstance();
            List<String> tmpProductDimIds = FastList.newInstance();
            List<GenericValue> tmpTodayOrderItems = FastList.newInstance();
            Map<String, Object> anItem = FastMap.newInstance();

            Map<String, Object> todayOrders = FastMap.newInstance();
            todayOrders = mGetTodayOrderBySalesmans(ctx, context);

            /*add today*/
            if (UtilValidate.isNotEmpty(todayOrders)) {
                tmpOrders = (List<GenericValue>)todayOrders.get("orders");
                tmpOrderIds = (List<String>)todayOrders.get("orderIds");
                tmpProductDimIds  = (List<String>)todayOrders.get("productDimIds");
                tmpTodayOrderItems  = (List<GenericValue>)todayOrders.get("orderItems");
                currentMonthSku = Long.valueOf(tmpProductDimIds.size());
                currentMonthNumberOfOrder = Long.valueOf(tmpOrderIds.size());
                for (GenericValue tmpOrder : tmpOrders) {
                    if (UtilValidate.isNotEmpty(tmpOrder.getBigDecimal("grandTotal"))) {
                        currentMonthSales = currentMonthSales.add(tmpOrder.getBigDecimal("grandTotal"));
                    }
                }
                for (GenericValue tmpTodayOrderItem : tmpTodayOrderItems) {
                    if (UtilValidate.isNotEmpty(tmpTodayOrderItem.getBigDecimal("quantity"))) {
                        currentMonthQuantity = currentMonthQuantity.add(tmpTodayOrderItem.getBigDecimal("quantity"));
                    }
                    if (UtilValidate.isNotEmpty(tmpTodayOrderItem.getBigDecimal("cancelQuantity"))) {
                        currentMonthQuantity = currentMonthQuantity.subtract(tmpTodayOrderItem.getBigDecimal("cancelQuantity"));
                    }
                }
            }

            BigDecimal todaySales = currentMonthSales;
            Long todayNumberOfOrder = currentMonthNumberOfOrder;
            Long todaySku = currentMonthSku;
            BigDecimal todayQuantity = currentMonthQuantity;
            BigDecimal todayQuantityPerSku = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(todaySku) && !todaySku.equals(0L)) {
                todayQuantityPerSku = todayQuantity.divide(BigDecimal.valueOf(todaySku), 2, RoundingMode.HALF_UP);
            }

            /*add order currentMonth*/
            for (GenericValue item : currentMonthItems) {
                if (!tmpOrderIds.contains(item.getString("orderId"))) {
                    tmpOrderIds.add(item.getString("orderId"));
                    currentMonthNumberOfOrder += 1;
                }
                if (!tmpProductDimIds.contains(item.getString("productDimId"))) {
                    tmpProductDimIds.add(item.getString("productDimId"));
                    currentMonthSku += 1;
                }
                if (item.getTimestamp("orderDate").before(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))) {
                    if (UtilValidate.isNotEmpty(item.getBigDecimal("totalAmount"))) {
                        currentMonthSales = currentMonthSales.add(item.getBigDecimal("totalAmount"));
                    }
                    if (UtilValidate.isNotEmpty(item.getBigDecimal("totalQuantity"))) {
                        currentMonthQuantity = currentMonthQuantity.add(item.getBigDecimal("totalQuantity"));
                    }
                }
            }
            if (UtilValidate.isNotEmpty(currentMonthSku) && !currentMonthSku.equals(0L)) {
                currentMonthQuantityPerSku = currentMonthQuantity.divide(BigDecimal.valueOf(currentMonthSku), 2, RoundingMode.HALF_UP);
            }

            /*get created customer*/
            //today
            Long todayVisitedCustomer = 0L, todayCreatedCustomer = 0L, todayTimeHasVisitedCustomer = 0L;
            Long currentMonthVisitedCustomer = 0L, currentMonthCreatedCustomer = 0L, currentMonthTimeHasVisitedCustomer = 0L;
            Timestamp beginOfToday = UtilDateTime.getDayStart(timeAtThisTime);
            Timestamp endOfToday = UtilDateTime.getDayEnd(timeAtThisTime);
            Timestamp beginOfMonth = UtilDateTime.getMonthStart(timeAtThisTime);
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfToday));
            listAllConditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN, endOfToday));
            listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", userLoginId));
            List<GenericValue> todayCreatedCustomers = delegator.findList("TemporaryParty",
                    EntityCondition.makeCondition(listAllConditions),null,null,null,false);
            if (UtilValidate.isNotEmpty(todayCreatedCustomers)){
                todayCreatedCustomer = Long.valueOf(todayCreatedCustomers.size());
            }

            //month
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfMonth));
            listAllConditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN, endOfToday));
            listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", userLoginId));
            List<GenericValue> monthCreatedCustomers = delegator.findList("TemporaryParty",
                    EntityCondition.makeCondition(listAllConditions),null,null,null,false);
            currentMonthCreatedCustomer = Long.valueOf(monthCreatedCustomers.size());

            /*get visited customer*/
            //today
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfToday));
            listAllConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.LESS_THAN, endOfToday));
            listAllConditions.add(EntityCondition.makeCondition("partyId", userLoginPartyId));
            List<GenericValue> todayVisitedCustomers = delegator.findList("CheckInHistory",
                    EntityCondition.makeCondition(listAllConditions),null,null,null,false);
            //current month
            listAllConditions.clear();
            listAllConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.GREATER_THAN_EQUAL_TO, beginOfToday));
            listAllConditions.add(EntityCondition.makeCondition("checkInDate", EntityOperator.LESS_THAN, endOfToday));
            listAllConditions.add(EntityCondition.makeCondition("partyId", userLoginPartyId));
            List<GenericValue> currentMonthVisitedCustomers = delegator.findList("CheckInHistory",
                    EntityCondition.makeCondition(listAllConditions),null,null,null,false);
            Timestamp tmpCheckInDate = null;
            Timestamp tmpCheckOutDate = null;
            Boolean tmpCheckInOk = null;
            if (UtilValidate.isNotEmpty(todayVisitedCustomers)) {
                todayVisitedCustomer = Long.valueOf(EntityUtil.getFieldListFromEntityList(todayVisitedCustomers, "customerId", true).size());
                for (GenericValue todayCustomer : todayVisitedCustomers) {
                    tmpCheckInDate = todayCustomer.getTimestamp("checkInDate");
                    tmpCheckOutDate = todayCustomer.getTimestamp("checkOutDate");
                    tmpCheckInOk = todayCustomer.getBoolean("checkInOk");
                    if (UtilValidate.isNotEmpty(tmpCheckInDate) && UtilValidate.isNotEmpty(tmpCheckOutDate) && UtilValidate.isNotEmpty(tmpCheckInOk) && tmpCheckInOk) {
                        todayTimeHasVisitedCustomer += tmpCheckOutDate.getTime() - tmpCheckInDate.getTime();
                    }
                }
            }

            if (UtilValidate.isNotEmpty(currentMonthVisitedCustomers)) {
                currentMonthVisitedCustomer = Long.valueOf(EntityUtil.getFieldListFromEntityList(currentMonthVisitedCustomers, "customerId", true).size());
                for (GenericValue todayCustomer : currentMonthVisitedCustomers) {
                    tmpCheckInDate = todayCustomer.getTimestamp("checkInDate");
                    tmpCheckOutDate = todayCustomer.getTimestamp("checkOutDate");
                    tmpCheckInOk = todayCustomer.getBoolean("checkInOk");
                    if (UtilValidate.isNotEmpty(tmpCheckInDate) && UtilValidate.isNotEmpty(tmpCheckOutDate) && UtilValidate.isNotEmpty(tmpCheckInOk) && tmpCheckInOk) {
                        currentMonthTimeHasVisitedCustomer += tmpCheckOutDate.getTime() - tmpCheckInDate.getTime();
                    }
                }
            }

            successResult.put("todaySales",todaySales);
            successResult.put("todayVisitedCustomer",todayVisitedCustomer);
            successResult.put("todayCreatedCustomer",todayCreatedCustomer);
            successResult.put("todayTimeHasVisitedCustomer",todayTimeHasVisitedCustomer);
            successResult.put("todayNumberOfOrder",todayNumberOfOrder);
            successResult.put("todaySku",todaySku);
            successResult.put("todayQuantity",todayQuantity);
            successResult.put("todayQuantityPerSku",todayQuantityPerSku);
            successResult.put("currentMonthSales",currentMonthSales);
            successResult.put("currentMonthNumberOfOrder",currentMonthNumberOfOrder);
            successResult.put("currentMonthSku",currentMonthSku);
            successResult.put("currentMonthVisitedCustomer",currentMonthVisitedCustomer);
            successResult.put("currentMonthCreatedCustomer",currentMonthCreatedCustomer);
            successResult.put("currentMonthTimeHasVisitedCustomer",currentMonthTimeHasVisitedCustomer);
            successResult.put("currentMonthQuantity",currentMonthQuantity);
            successResult.put("currentMonthQuantityPerSku",currentMonthQuantityPerSku);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }
}
