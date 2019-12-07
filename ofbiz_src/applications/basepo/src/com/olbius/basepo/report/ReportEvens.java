package com.olbius.basepo.report;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.report.PurchaseOrderReportImp.ResultOutPOReport;
import com.olbius.bi.olap.grid.OlapGrid;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReportEvens {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReportPurchaseOrder(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String dateType = (String) context.get("dateType");
		String filterSaleOrder = (String) context.get("filterSaleOrder");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> statusId = (List<Object>) context.get("statusId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		/*
		 * List<Object> productStoreId= (List<Object>)
		 * context.get("productStoreId[]");
		 */
		/* String useTo = (String) context.get("useTo"); */
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Locale locale = (Locale) context.get("locale");
		PurchaseOrderReportImp poReport = new PurchaseOrderReportImp();
		ResultOutPOReport resultOutPOReport = poReport.new ResultOutPOReport();
		poReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(poReport, resultOutPOReport);
		poReport.setOlapResult(gird);

		if (dateType == null) {
			dateType = "DAY";
		}
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");

		poReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		poReport.setFromDate(fromDate);
		poReport.setThruDate(thruDate);

		poReport.putParameter(PurchaseOrderReportImp.DATE_TYPE, dateType);
		poReport.putParameter(PurchaseOrderReportImp.PRODUCT_ID, productId);
		poReport.putParameter(PurchaseOrderReportImp.STATUS_ID, statusId);
		/*
		 * poReport.putParameter(PurchaseOrderReportImp.PRODUCT_STORE_ID,
		 * productStoreId);
		 */
		poReport.putParameter(PurchaseOrderReportImp.CATEGORY_ID, categoryId);
		poReport.putParameter(PurchaseOrderReportImp.USER_LOGIN_ID, partyIdByFacility);
		poReport.putParameter(PurchaseOrderReportImp.FILTER_SALE_ORDER, filterSaleOrder);
		poReport.putParameter(PurchaseOrderReportImp.LOCALE, locale);
		Map<String, Object> result = poReport.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportPurchaseOrderReportToPdf(HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String statusId = request.getParameter("statusId");

		String categoryId = request.getParameter("categoryId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<String> productIdInput = null;
		List<String> statusIdInput = null;

		List<String> categoryIdInput = null;

		if (productId.equals("") || productId.equals("null")) {
			productIdInput = null;
		}
		if (!productId.equals("") && !productId.equals("null")) {
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if (productIdData.length != 0) {
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}

		if (statusId.equals("") || statusId.equals("null")) {
			statusIdInput = null;
		}
		if (!statusId.equals("") && !statusId.equals("null")) {
			String[] statusIdData = statusId.split(",");
			statusIdInput = new ArrayList<>();
			if (statusIdData.length != 0) {
				for (String i : statusIdData) {
					statusIdInput.add(i);
				}
			}
		}

		if (categoryId.equals("") || categoryId.equals("null")) {
			categoryIdInput = null;
		}
		if (!categoryId.equals("") && !categoryId.equals("null")) {
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if (categoryIdData.length != 0) {
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dateType == null) {
			dateType = "DAY";
		}
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));

		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("statusId[]", statusIdInput);
		context.put("userLogin", userLogin);
		context.put("categoryId[]", categoryIdInput);
		Map<String, Object> resultService = dispatcher.runSync("jqGetListPurchaseOrderReport", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReturnProductReportOlapPO(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String dateType = (String) context.get("dateType");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		List<Object> returnReasonId = (List<Object>) context.get("returnReasonId[]");
		/*
		 * List<Map<String, String>> listMapFilter = new ArrayList<Map<String,
		 * String>>(); if(UtilValidate.isNotEmpty(filterData)){ JSONArray
		 * jsonArray = JSONArray.fromObject(filterData); for(int i = 0; i <
		 * jsonArray.size(); i++){ JSONObject filterObject =
		 * jsonArray.getJSONObject(i); String filterId =
		 * filterObject.getString("id"); String filterValue =
		 * filterObject.getString("value"); Map<String, String> mapFilter = new
		 * HashMap<String, String>(); mapFilter.put(filterId, filterValue);
		 * listMapFilter.add(mapFilter); } }
		 */

		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		ReturnProductOlapPOImpl rpo = new ReturnProductOlapPOImpl();
		ReturnProductOlapPOImpl.ResultOutReport resultOutPOReport = rpo.new ResultOutReport();
		rpo.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(rpo, resultOutPOReport);
		rpo.setOlapResult(gird);

		if (dateType == null) {
			dateType = "DAY";
		}
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");

		rpo.setFromDate(fromDate);
		rpo.setThruDate(thruDate);

		rpo.putParameter(ReturnProductOlapPOImpl.DATE_TYPE, dateType);
		rpo.putParameter(ReturnProductOlapPOImpl.PRODUCT_ID, productId);
		rpo.putParameter(ReturnProductOlapPOImpl.FACILITY_ID, facilityId);
		rpo.putParameter(ReturnProductOlapPOImpl.CATEGORY_ID, categoryId);
		rpo.putParameter(ReturnProductOlapPOImpl.RETURN_REASON_ID, returnReasonId);
		rpo.putParameter(ReturnProductOlapPOImpl.USER_ID, partyIdByFacility);
		rpo.putParameter(ReturnProductOlapPOImpl.LOCALE, locale);
		Map<String, Object> result = rpo.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> exportReturnProductOlapPOToPdf(HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, GenericEntityException, ParseException, GenericServiceException {
		String dateType = request.getParameter("dateType");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		String categoryId = request.getParameter("categoryId");
		String returnReasonId = request.getParameter("returnReasonId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<String> productIdInput = null;
		List<String> facilityIdInput = null;
		List<String> categoryIdInput = null;
		List<String> returnReasonIdInput = null;

		if (productId.equals("") || productId.equals("null")) {
			productIdInput = null;
		}
		if (!productId.equals("") && !productId.equals("null")) {
			String[] productIdData = productId.split(",");
			productIdInput = new ArrayList<>();
			if (productIdData.length != 0) {
				for (String i : productIdData) {
					productIdInput.add(i);
				}
			}
		}

		if (facilityId.equals("") || facilityId.equals("null")) {
			facilityIdInput = null;
		}
		if (!facilityId.equals("") && !facilityId.equals("null")) {
			String[] facilityIdData = facilityId.split(",");
			facilityIdInput = new ArrayList<>();
			if (facilityIdData.length != 0) {
				for (String i : facilityIdData) {
					facilityIdInput.add(i);
				}
			}
		}

		if (categoryId.equals("") || categoryId.equals("null")) {
			categoryIdInput = null;
		}
		if (!categoryId.equals("") && !categoryId.equals("null")) {
			String[] categoryIdData = categoryId.split(",");
			categoryIdInput = new ArrayList<>();
			if (categoryIdData.length != 0) {
				for (String i : categoryIdData) {
					categoryIdInput.add(i);
				}
			}
		}

		if (returnReasonId.equals("") || returnReasonId.equals("null")) {
			returnReasonIdInput = null;
		}
		if (!returnReasonId.equals("") && !returnReasonId.equals("null")) {
			String[] returnReasonIdData = returnReasonId.split(",");
			returnReasonIdInput = new ArrayList<>();
			if (returnReasonIdData.length != 0) {
				for (String i : returnReasonIdData) {
					returnReasonIdInput.add(i);
				}
			}
		}

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dateType == null) {
			dateType = "DAY";
		}
		long fromDateLog = Long.parseLong(fromDateStr);
		long thruDateLog = Long.parseLong(thruDateStr);
		Date fromDateTs = UtilDateTime.getDayStart(new Timestamp(fromDateLog));
		Date thruDateTs = UtilDateTime.getDayStart(new Timestamp(thruDateLog));

		Map<String, Object> context = FastMap.newInstance();
		context.put("dateType", dateType);
		context.put("productId[]", productIdInput);
		context.put("fromDate", fromDateTs);
		context.put("thruDate", thruDateTs);
		context.put("facilityId[]", facilityIdInput);
		context.put("categoryId[]", categoryIdInput);
		context.put("returnReasonId[]", returnReasonIdInput);
		context.put("userLogin", userLogin);
		Map<String, Object> resultService = dispatcher.runSync("jqGetListReturnProductReportOlapPO", context);
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
		return listData;
	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getEndOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getStartOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getEndOfTomorrow(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static Date getStartOfTomorrow(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Map<String, Object> getTotalOrderNeedApprovalToday(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		String checkTime = (String) context.get("checkTime");

		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		List<EntityCondition> listAllConditions = FastList.newInstance();
		String sqlCondition = "";
		if (checkTime.equals("OrderCanApprovedToday")) {
			sqlCondition = "(ORD.STATUS_ID = 'ORDER_CREATED' AND (PRO.ORE_PARTY_ID =" + "'" + ownerPartyId + "'"
					+ " AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE BETWEEN" + "'"
					+ currentFirstTimeToday + "'" + "AND" + "'" + currentEndTimeToday + "'" + "))";
		}
		if (checkTime.equals("OrderNotYetApproved")) {
			sqlCondition = "(ORD.STATUS_ID = 'ORDER_CREATED' AND (PRO.ORE_PARTY_ID =" + "'" + ownerPartyId + "'"
					+ " AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER'))";
		}
		if (UtilValidate.isNotEmpty(sqlCondition)) {
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		List<GenericValue> listOrderPurchaseCountTotal = delegator.findList("OrderPurchaseCountTotal", cond, null, null,
				null, false);
		int orderCount = 0;
		if (UtilValidate.isNotEmpty(listOrderPurchaseCountTotal)) {
			for (GenericValue totalOrderSum : listOrderPurchaseCountTotal) {
				Long orderId = totalOrderSum.getLong("orderId");
				orderCount += orderId.intValue();
			}
		}
		result.put("orderCount", orderCount);
		return result;
	}

	public static Map<String, Object> getNotShipOrder(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> condList = new ArrayList<>();
		condList.add(EntityCondition.makeCondition("statusId", "ORDER_APPROVED"));
		condList.add(EntityCondition.makeCondition("orderTypeId", "PURCHASE_ORDER"));
		List<EntityCondition> conds = FastList.newInstance();
		
		conds.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityJoinOperator.LESS_THAN, UtilDateTime.nowTimestamp()));
		conds.add(EntityCondition.makeCondition("shipBeforeDate", EntityJoinOperator.LESS_THAN, UtilDateTime.nowTimestamp()));
		condList.add(EntityCondition.makeCondition(conds, EntityOperator.OR));

		EntityListIterator listNotShipOrder = null;
		try {
			Long totalRows = delegator.findCountByCondition("OrderHeader", EntityCondition.makeCondition(condList), null, null);
			result.put("orderCount", totalRows.intValue());
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} finally {
			if (listNotShipOrder != null) {
				listNotShipOrder.close();
			}
		}
		return result;
	}

	public static Map<String, Object> loadListPurchaseOrder(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String valueCheck = (String) context.get("valueCheck");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());
		String sqlCondition = "";

		if (valueCheck.equals("OrderCanApprovedToday")) {
			sqlCondition = "(ORD.STATUS_ID = 'ORDER_CREATED' AND (PRO.ORE_PARTY_ID =" + "'" + ownerPartyId + "'"
					+ " AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER') AND (OITG.OI2_SHIP_BEFORE_DATE BETWEEN" + "'"
					+ currentFirstTimeToday + "'" + "AND" + "'" + currentEndTimeToday + "'" + "))";
		}
		if (valueCheck.equals("OrderNotYetApproved")) {
			sqlCondition = "(ORD.STATUS_ID = 'ORDER_CREATED' AND (PRO.ORE_PARTY_ID =" + "'" + ownerPartyId + "'"
					+ " AND PRO.ORE_ROLE_TYPE_ID='BILL_TO_CUSTOMER'))";
		}
		if (UtilValidate.isNotEmpty(sqlCondition)) {
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}

		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		List<GenericValue> listOrder = delegator.findList("OrderPurchaseTotalRowNotCount", cond, null, null, null,
				true);
		result.put("listOrder", listOrder);
		return result;
	}

	public static Map<String, Object> getTotalReturnOrderSupplier(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String checkTime = (String) context.get("checkTime");

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		List<EntityCondition> listAllConditions = FastList.newInstance();
		String sqlCondition = "";
		if (checkTime.equals("TotalOrderReturn")) {
			sqlCondition = "REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'";
		}
		if (checkTime.equals("OrderReturnThisMonth")) {
			sqlCondition = "(REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'" + " AND" + " (REH.ENTRY_DATE BETWEEN" + "'"
					+ getStartOfMonth() + "'" + "AND" + "'" + getEndOfMonth() + "'" + "))";
		}
		if (checkTime.equals("OrderReturnToday")) {
			Date currentDate = new Date();
			Date currentFirstDate = getStartOfDay(currentDate);
			Date currentEndDate = getEndOfDay(currentDate);
			Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
			Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());
			sqlCondition = "(REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'" + " AND" + " (REH.ENTRY_DATE BETWEEN" + "'"
					+ currentFirstTimeToday + "'" + "AND" + "'" + currentEndTimeToday + "'" + "))";
		}
		if (UtilValidate.isNotEmpty(sqlCondition)) {
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		List<GenericValue> listReturnSupplierTotalSum = delegator.findList("ReturnSupplierTotalSum", cond, null, null,
				null, true);
		int orderReturnCount = 0;
		if (UtilValidate.isNotEmpty(listReturnSupplierTotalSum)) {
			for (GenericValue totalReturnOrderSum : listReturnSupplierTotalSum) {
				Long returnId = totalReturnOrderSum.getLong("returnId");
				orderReturnCount += returnId.intValue();
			}
		}
		result.put("orderReturnCount", orderReturnCount);
		return result;
	}

	public static Map<String, Object> loadListReturnOrderSupplier(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String valueCheck = (String) context.get("valueCheck");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Date currentDate = new Date();
		Date currentFirstDate = getStartOfDay(currentDate);
		Date currentEndDate = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstDate.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndDate.getTime());
		String sqlCondition = "";
		if (valueCheck.equals("TotalOrderReturn")) {
			sqlCondition = "REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'";
		}
		if (valueCheck.equals("OrderReturnToday")) {
			sqlCondition = "(REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'" + " AND" + " (REH.ENTRY_DATE BETWEEN" + "'"
					+ currentFirstTimeToday + "'" + "AND" + "'" + currentEndTimeToday + "'" + "))";
		}
		if (valueCheck.equals("OrderReturnThisMonth")) {
			sqlCondition = "(REH.FROM_PARTY_ID =" + "'" + ownerPartyId + "'" + " AND" + " (REH.ENTRY_DATE BETWEEN" + "'"
					+ getStartOfMonth() + "'" + "AND" + "'" + getEndOfMonth() + "'" + "))";
		}
		if (UtilValidate.isNotEmpty(sqlCondition)) {
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
		}

		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		List<GenericValue> listReturn = delegator.findList("ReturnSupplierNotCount", cond, null, null, null, false);
		result.put("listReturn", listReturn);
		return result;
	}

	public static Map<String, Object> getTotalAmountBought(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String period = (String) context.get("period");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			Date fromDate = new Date(System.currentTimeMillis());
			switch (period) {
			case "InMonth":
				fromDate = getStartOfMonth();
				break;
			default:
				break;
			}
			TotalAmountPurchase totalAmountPurchase = new TotalAmountPurchase(
					new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), delegator,
					organization, fromDate);
			result.put("value", totalAmountPurchase.getValueTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
