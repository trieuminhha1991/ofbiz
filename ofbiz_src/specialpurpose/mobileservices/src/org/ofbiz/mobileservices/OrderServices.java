package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.ofbiz.Mobile;
import org.ofbiz.ProcessMobileApps;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.mobileUtil.OrderUtils;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;

public class OrderServices implements Mobile {

	public static final String module = OrderServices.class.getName();
	private static ProcessMobileApps process = new ProcessMobileApps();
	public static boolean result = true;
	public static String currencyUom = null;
	/*		getTotalOrderDetail
	 * @param DispatchContext dpct,context
	 *
	 *
	 * */
	public static Map<String,Object> getTotalOrderDetail(DispatchContext dpct,Map<String, ?extends Object> context){
		int countOrderNotPayment = 0;
		int grandTotal = 0 ;
		int remainingSubTotal = 0;
		String customerId = (String ) context.get("customerId");
		String checkDay = (String) context.get("month");
		EntityCondition checkDayCond =null;
		EntityCondition checkDayCon =null;
		Date date = new Date();
		TimeZone time = TimeZone.getDefault();
		Locale local = Locale.getDefault();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		Delegator delegator = dpct.getDelegator();
		EntityCondition cond1 = EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS,customerId);
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED"));
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(cond1);
		conditions.add(cond2);
		Set<String> fields = UtilMisc.toSet("orderId","statusId","partyId","remainingSubTotal","grandTotal");
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		Map<String,Object> result = FastMap.newInstance();
		Map<String,Object> res = FastMap.newInstance();
		Timestamp nowTime = new Timestamp(date.getTime());
		if(checkDay.equalsIgnoreCase("thisWeek")){
			Timestamp WeekEnd = UtilDateTime.getWeekEnd(nowTime);
			Timestamp WeekStart = UtilDateTime.getWeekStart(nowTime);
			checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,WeekStart);
			checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,WeekEnd);
		}else if(checkDay.equalsIgnoreCase("thisMonth")){
			Timestamp DayStartOfMonth = UtilDateTime.getMonthStart(nowTime, time, local);
			Timestamp DayEndOfMonth = UtilDateTime.getMonthEnd(nowTime, time, local);
			checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,DayStartOfMonth);
			checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,DayEndOfMonth);
		}else if(checkDay.equalsIgnoreCase("lastMonth")){
			Timestamp DayStartOfLastMonth = UtilDateTime.getMonthStart(nowTime, 0, -1);
			Timestamp DayEndOfLastMonth = UtilDateTime.getMonthEnd(DayStartOfLastMonth, time, local);
			checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,DayStartOfLastMonth);
			checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,DayEndOfLastMonth);
		}else checkDayCond = null;
		conditions.add(checkDayCond);
		conditions.add(checkDayCon);
		try {
			List<GenericValue> totalOrder  = delegator.findList("OrderHeaderDetail",
					EntityCondition.makeCondition(conditions,EntityJoinOperator.AND)
					, fields, null, options, false);
			for(GenericValue order : totalOrder){
				if(order.getString("statusId").equalsIgnoreCase("ORDER_CREATED")){
					countOrderNotPayment ++;
				}
				grandTotal += order.getBigDecimal("grandTotal").intValue();
				remainingSubTotal +=  order.getBigDecimal("remainingSubTotal").intValue();
			}
			if(UtilValidate.isNotEmpty(totalOrder)){
				result.put("totalOrder", totalOrder.size());
				result.put("totalOrderNotPayment", countOrderNotPayment);
				result.put("totalOrderPayment", totalOrder.size() - countOrderNotPayment);
				result.put("grandTotal", grandTotal);
				result.put("remainingSubTotal", remainingSubTotal);
				result.put("TotalAmountPaid", grandTotal - remainingSubTotal);
			}
			res.put("res", result);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Could not get list Order Detail" +e.getMessage() , module);
			e.printStackTrace();
		}
		return res;
	}
	/**
	 *
	 * get Product Order  Detail of Customer
	 * @param DispatchContext dpct,context
	 *
	 * **/
	public static Map<String,Object> getOrderProductDetailOfCustomer(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = dpct.getDelegator();
		String partyId = (String) context.get("partyId");
		EntityCondition statusCond = EntityCondition.makeCondition("statusId",EntityJoinOperator.NOT_IN,UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED"));
		EntityCondition partyCond = EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS,partyId);
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		Map<String,Object> res = FastMap.newInstance();
		Map<String,Map<String,Object>> inFo = FastMap.newInstance();
		String checkDay = (String) context.get("month");
		EntityCondition checkDayCond =null;
		EntityCondition checkDayCon =null;
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());
		TimeZone time = TimeZone.getDefault();
		Locale local = Locale.getDefault();
		Set<String> fields = UtilMisc.toSet("orderId","orderDate","partyId","quantity","productId","productName");
		try {
			if(checkDay.equalsIgnoreCase("thisWeek")){
				Timestamp WeekEnd = UtilDateTime.getWeekEnd(nowTime);
				Timestamp WeekStart = UtilDateTime.getWeekStart(nowTime);
				checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,WeekStart);
				checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,WeekEnd);
			}else if(checkDay.equalsIgnoreCase("thisMonth")){
				Timestamp DayStartOfMonth = UtilDateTime.getMonthStart(nowTime, time, local);
				Timestamp DayEndOfMonth = UtilDateTime.getMonthEnd(nowTime, time, local);
				checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,DayStartOfMonth);
				checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,DayEndOfMonth);
			}else if(checkDay.equalsIgnoreCase("lastMonth")){
				Timestamp DayStartOfLastMonth = UtilDateTime.getMonthStart(nowTime, 0, -1);
				Timestamp DayEndOfLastMonth = UtilDateTime.getMonthEnd(DayStartOfLastMonth, time, local);
				checkDayCond = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,DayStartOfLastMonth);
				checkDayCon = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,DayEndOfLastMonth);
			}else checkDayCond = null;
			conditions.add(checkDayCond);
			conditions.add(checkDayCon);
			conditions.add(statusCond);
			conditions.add(partyCond);
			List<GenericValue> listProductDetail = delegator.findList("OrderProductDetail", EntityCondition.makeCondition(conditions,EntityJoinOperator.AND), fields, null, options, false);

			if(UtilValidate.isNotEmpty(listProductDetail)){
			for(GenericValue item : listProductDetail){
				Map<String,Object> tmp = FastMap.newInstance();
				if(UtilValidate.isEmpty(item.getString("productId"))||UtilValidate.isEmpty(item.getString("productName"))||UtilValidate.isEmpty(item.getString("quantity")))
				{
					continue;
				}else {
					if(inFo.containsKey(item.get("productId")))
						{
						tmp = inFo.get(item.get("productId"));
						int quantity = 0;
						quantity = Integer.valueOf((tmp.get(item.getString("productName"))).toString());
						tmp.put(item.getString("productName"),quantity + item.getBigDecimal("quantity").intValue());
						inFo.put(item.getString("productId"), tmp);
					}else {
						tmp.put(item.getString("productName"), item.getBigDecimal("quantity").intValue());
						inFo.put(item.getString("productId"),tmp);
						}
					}
				}
			}
			res.put("res", inFo);
		} catch (Exception e) {
			Debug.logError("Could not get list product Detail" +e.getMessage() , module);
			e.printStackTrace();
		}
		return res;
	}
	
	/* update Order */
	public static String updateOrder(HttpServletRequest request,
			HttpServletResponse response) {
		return "success";
	}

	/*
	 * get detail information of an order input: id of order output: json type
	 */

	public static String getOrderDetail(HttpServletRequest request,
			HttpServletResponse response) {
		String orderId = (String) request.getParameter("id");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader",
					UtilMisc.toMap("orderId", orderId), false);
			OrderReadHelper orderHelper = OrderReadHelper
					.getHelper(orderHeader);
			// GenericValue billToParty = orderHelper.getBillToParty();
			List<EntityExpr> itemsCond = UtilMisc.toList(EntityCondition.makeCondition("orderId", orderId),
					EntityCondition.makeCondition("orderItemTypeId", "PRODUCT_ORDER_ITEM"),
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
			List<EntityExpr> itemsCondNoPromos = FastList.newInstance();
			itemsCondNoPromos.addAll(itemsCond);
			itemsCondNoPromos.add(EntityCondition.makeCondition("isPromo", "N"));
			List<String> filtItem = UtilMisc.toList("productId");
			Set<String> itemsField = UtilMisc.toSet("itemDescription", "quantity", "unitPrice", "productId", "alternativeQuantity");
			List<GenericValue> orderDetail = delegator.findList("OrderItem", EntityCondition.makeCondition(itemsCondNoPromos), itemsField, filtItem, null, false);
			List<Map<String, Object>> orderDetailInfo = FastList.newInstance();
			for(GenericValue e : orderDetail){
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				String productId = e.getString("productId");
				Map<String, Object> image = ProductServices.getProductImage(delegator, productId);
				o.putAll(image);
				orderDetailInfo.add(o);
			}
			GenericValue productStore = delegator.findOne(
					"ProductStore",
					UtilMisc.toMap("productStoreId",
							orderHelper.getProductStoreId()), false);
			Map<String, Object> productStoreFilter = FastMap.newInstance();
			productStoreFilter.put("productStoreId", productStore.getString("productStoreId"));
			productStoreFilter.put("storeName", productStore.getString("storeName"));
			OrderReadHelper orderReadHelper = OrderReadHelper.getHelper(orderHeader);
			BigDecimal taxAmount = orderHelper.getTaxTotal();
			Map<String, Object> hd = FastMap.newInstance();
			hd.putAll(orderHeader);
			hd.put("taxAmount", taxAmount);
			request.setAttribute("party", orderReadHelper.getShipToParty());
			List<GenericValue> ship = orderReadHelper.getShippingLocations();
			List<Map<String, Object>> listShip = FastList.newInstance();
			GenericValue e = null;
			for(GenericValue s : ship){
				Map<String, Object> shipInfo = FastMap.newInstance();
				shipInfo.putAll(s);
				e = delegator.findOne("Geo", UtilMisc.toMap("geoId", s.getString("districtGeoId")), true);
				if(e != null) shipInfo.put("districtGeoName", e.getString("geoName"));
				listShip.add(shipInfo);
			}
			List<GenericValue> adj = orderHelper.getAdjustments();
			BigDecimal subTotal = orderHeader.getBigDecimal("remainingSubTotal");
			BigDecimal discountAmount = OrderReadHelper.calcOrderPromoAdjustmentsBd(adj);
			BigDecimal totalAmount = subTotal.subtract(discountAmount);

			/*get promotion*/
			List<EntityExpr> itemsCondHasPromos = FastList.newInstance();
			itemsCondHasPromos.addAll(itemsCond);
			itemsCondHasPromos.add(EntityCondition.makeCondition("isPromo", "Y"));
			List<GenericValue> promotions = delegator.findList("OrderItem", EntityCondition.makeCondition(itemsCondHasPromos), itemsField, filtItem, null, false);
			List<Map<String, Object>> resp = FastList.newInstance();
			for(GenericValue p : promotions){
				Map<String, Object> o = FastMap.newInstance();
				String productId = p.getString("productId");
				o.putAll(ProductServices.getProductImage(delegator, productId));
				o.put("productQuantity", p.getBigDecimal("quantity"));
				o.put("alternativeQuantity", p.getBigDecimal("quantity"));
				o.put("productId", productId);
				o.put("productName", p.getString("itemDescription"));
				GenericValue pr = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
				o.put("productCode", pr.getString("productCode"));
				o.put("productPrice", p.getBigDecimal("unitPrice"));
				resp.add(o);
			}
			hd.put("totalAmount", totalAmount);
			hd.put("discountAmount", discountAmount);
			hd.put("promotions", resp);

			request.setAttribute("orderHeader", hd);
			request.setAttribute("orderShipment", listShip);
			request.setAttribute("orderDetail", orderDetailInfo);
			request.setAttribute("productStore", productStoreFilter);
			request.setAttribute("data", "success");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("data", "error");
			return "error";
		}

		return "success";
	}

	/**
	 * finish payment order
	 *
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static String paymentFinish(HttpServletRequest request,
			HttpServletResponse response) {
		String orderId = request.getParameter("orderId");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		List<GenericValue> orderRoles = null;
		GenericValue orderHeader = null;
		try {
			orderHeader = delegator.findOne("OrderHeader",
					UtilMisc.toMap("orderId", orderId), false);
			orderRoles = delegator.findList("OrderRole", EntityCondition
					.makeCondition("orderId", EntityOperator.EQUALS, orderId),
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problems reading order header from datasource.",
					module);
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(
					resource_error,
					"OrderProblemsReadingOrderHeaderInformation", locale));
			return "error";
		}
		String partyId = orderRoles.get(0).getString("partyId");
		BigDecimal grandTotal = BigDecimal.ZERO;
		if (orderHeader != null) {
			grandTotal = orderHeader.getBigDecimal("grandTotal");
		}

		String paymentMethodTypeId = "EXT_COD";
		String amountStr = grandTotal.toString();
		String paymentReference = "";// request.getParameter("paymentMethodTypeId"
										// + "_reference");//edit
		GenericValue placingCustomer = null;
		try {
			List<GenericValue> pRoles = delegator.findByAnd("OrderRole",
					UtilMisc.toMap("orderId", orderId, "roleTypeId",
							"PLACING_CUSTOMER"), null, false);
			if (UtilValidate.isNotEmpty(pRoles))
				placingCustomer = EntityUtil.getFirst(pRoles);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problems looking up order payment preferences",
					module);
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(
					resource_error, "OrderErrorProcessingOfflinePayments",
					locale));
			return "error";
		}

		List<GenericValue> toBeStored = FastList.newInstance();

		if (!UtilValidate.isEmpty(amountStr)) {
			BigDecimal paymentTypeAmount = BigDecimal.ZERO;
			try {
				paymentTypeAmount = (BigDecimal) ObjectType.simpleTypeConvert(
						amountStr, "BigDecimal", null, locale);
			} catch (GeneralException e) {
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties
						.getMessage(resource_error,
								"OrderProblemsPaymentParsingAmount", locale));
				return "error";
			}
			if (paymentTypeAmount.compareTo(BigDecimal.ZERO) > 0) {
				// create the OrderPaymentPreference
				// TODO: this should be done with a service
				Map<String, String> prefFields = UtilMisc
						.<String, String> toMap("orderPaymentPreferenceId",
								delegator
										.getNextSeqId("OrderPaymentPreference"));
				GenericValue paymentPreference = delegator.makeValue(
						"OrderPaymentPreference", prefFields);
				paymentPreference.set("paymentMethodTypeId",
						paymentMethodTypeId);
				paymentPreference.set("maxAmount", paymentTypeAmount);
				paymentPreference.set("statusId", "PAYMENT_RECEIVED");
				paymentPreference.set("orderId", orderId);
				paymentPreference.set("createdDate",
						UtilDateTime.nowTimestamp());
				if (userLogin != null) {
					paymentPreference.set("createdByUserLogin",
							userLogin.getString("userLoginId"));
				}

				try {
					delegator.create(paymentPreference);
				} catch (GenericEntityException ex) {
					Debug.logError(ex,
							"Cannot create a new OrderPaymentPreference",
							module);
					request.setAttribute("_ERROR_MESSAGE_", ex.getMessage());
					return "error";
				}

				// create a payment record
				Map<String, Object> results = null;
				try {
					Map<String, Object> context = UtilMisc.toMap("userLogin",
							userLogin, "orderPaymentPreferenceId",
							paymentPreference.get("orderPaymentPreferenceId"),
							"paymentRefNum", paymentReference, "comments",
							"Payment received offline and manually entered.");
					if (placingCustomer != null) {
						context.put("paymentFromId",
								placingCustomer.getString("partyId"));
					}
					results = dispatcher.runSync("createPaymentFromPreference",
							context);
				} catch (GenericServiceException e) {
					Debug.logError(
							e,
							"Failed to execute service createPaymentFromPreference",
							module);
					request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
					return "error";
				}

				if ((results == null)
						|| (results.get(ModelService.RESPONSE_MESSAGE)
								.equals(ModelService.RESPOND_ERROR))) {
					Debug.logError(
							(String) results.get(ModelService.ERROR_MESSAGE),
							module);
					request.setAttribute("_ERROR_MESSAGE_",
							results.get(ModelService.ERROR_MESSAGE));
					return "error";
				}
			}
		}
		// get the current payment prefs
		GenericValue offlineValue = null;
		List<GenericValue> currentPrefs = null;
		BigDecimal paymentTally = BigDecimal.ZERO;
		try {
			EntityConditionList<EntityExpr> ecl = EntityCondition
					.makeCondition(UtilMisc.toList(EntityCondition
							.makeCondition("orderId", EntityOperator.EQUALS,
									orderId), EntityCondition.makeCondition(
							"statusId", EntityOperator.NOT_EQUAL,
							"PAYMENT_CANCELLED")), EntityOperator.AND);
			currentPrefs = delegator.findList("OrderPaymentPreference", ecl,
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(
					e,
					"ERROR: Unable to get existing payment preferences from order",
					module);
		}
		if (UtilValidate.isNotEmpty(currentPrefs)) {
			for (GenericValue cp : currentPrefs) {
				String paymentMethodType = cp.getString("paymentMethodTypeId");
				if ("EXT_OFFLINE".equals(paymentMethodType)) {
					offlineValue = cp;
				} else {
					BigDecimal cpAmt = cp.getBigDecimal("maxAmount");
					if (cpAmt != null) {
						paymentTally = paymentTally.add(cpAmt);
					}
				}
			}
		}

		// now finish up
		boolean okayToApprove = false;
		if (paymentTally.compareTo(grandTotal) >= 0) {
			// cancel the offline preference
			okayToApprove = true;
			if (offlineValue != null) {
				offlineValue.set("statusId", "PAYMENT_CANCELLED");
				toBeStored.add(offlineValue);
			}
		}

		// store the status changes and the newly created payment preferences
		// and payments
		// TODO: updating order payment preference should be done with a service
		try {
			delegator.storeAll(toBeStored);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problems storing payment information", module);
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(
					resource_error,
					"OrderProblemStoringReceivedPaymentInformation", locale));
			return "error";
		}

		if (okayToApprove) {
			// update the status of the order and items
			OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
			request.setAttribute("statusOrder", "ORDER_APPROVED");
		}
		/** Complete order */
		try {
			userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);

			Map<String, Object> result = dispatcher.runSync(
					"quickShipEntireOrder",
					UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
			if (result.get("shipmentShipGroupFacilityList") != null) {
				request.setAttribute("statusOrder", "ORDER_COMPLETED");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getOrderAmount(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		TimeZone timeZone = TimeZone.getDefault();
		Date date = new Date();
		Locale locale = Locale.getDefault();
		Timestamp nowTimeStamp = new Timestamp(date.getTime());
		Timestamp dayBegin = UtilDateTime.getDayStart(nowTimeStamp);
		Timestamp dayEnd = UtilDateTime.getDayEnd(nowTimeStamp);
		try {
			EntityConditionList<EntityExpr> ecl = EntityCondition
					.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition(
											"orderItemSeqId",
											EntityOperator.EQUALS, null),
									EntityCondition.makeCondition(
											"orderPaymentPreferenceId",
											EntityOperator.EQUALS, null),
									EntityCondition
											.makeCondition(
													"statusDatetime",
													EntityOperator.GREATER_THAN_EQUAL_TO,
													dayBegin),
									EntityCondition.makeCondition(
											"statusDatetime",
											EntityOperator.LESS_THAN_EQUAL_TO,
											dayEnd)), EntityOperator.AND);

			List<GenericValue> dayList = delegator.findList("OrderStatus", ecl,
					null, null, null, false);
			request.setAttribute(
					"dayOrder",
					EntityUtil.filterByAnd(dayList,
							UtilMisc.toMap("statusId", "ORDER_CREATED")).size());
			request.setAttribute(
					"dayApprove",
					EntityUtil.filterByAnd(dayList,
							UtilMisc.toMap("statusId", "ORDER_APPROVED"))
							.size());
			request.setAttribute(
					"dayComplete",
					EntityUtil.filterByAnd(dayList,
							UtilMisc.toMap("statusId", "ORDER_COMPLETED"))
							.size());
			request.setAttribute(
					"dayCancelled",
					EntityUtil.filterByAnd(dayList,
							UtilMisc.toMap("statusId", "ORDER_CANCELLED"))
							.size());
			List<GenericValue> waitingPayment = delegator.findByAnd(
					"OrderHeader", UtilMisc.toMap("statusId", "ORDER_CREATED",
							"orderTypeId", "SALES_ORDER"), null, false);
			List<GenericValue> waitingComplete = delegator.findByAnd(
					"OrderHeader", UtilMisc.toMap("statusId", "ORDER_APPROVED",
							"orderTypeId", "SALES_ORDER"), null, false);
			request.setAttribute("waitingPayment", waitingPayment.size());
			request.setAttribute("waitingComplete", waitingComplete.size());

			// int month = UtilDateTime.getMonth(new Timestamp(date.getTime()),
			// timeZone, locale);

			Calendar cal = Calendar.getInstance();
			Timestamp timestamp = UtilDateTime.nowTimestamp();
			Timestamp monthBegin;
			Timestamp monthEnd;
			if (userLogin != null) {
				Map<String, Object> monthMap = FastMap.newInstance();
				for (int i = 5; i >= 0; i--) {
					// cal.set(Calendar.MONTH, i);
					monthBegin = UtilDateTime.getMonthStart(timestamp, 0, -i,
							timeZone, locale);
					monthEnd = UtilDateTime.getMonthEnd(monthBegin, timeZone,
							locale);
					cal.setTimeInMillis(monthBegin.getTime());
					ecl = EntityCondition
							.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("statusId",
											EntityOperator.NOT_EQUAL,
											"ORDER_REJECTED"),
									EntityCondition.makeCondition("statusId",
											EntityOperator.NOT_EQUAL,
											"ORDER_CANCELLED"),
									EntityCondition
											.makeCondition(
													"orderDate",
													EntityOperator.GREATER_THAN_EQUAL_TO,
													monthBegin),
									EntityCondition.makeCondition("orderDate",
											EntityOperator.LESS_THAN_EQUAL_TO,
											monthEnd), EntityCondition
											.makeCondition("orderTypeId",
													EntityOperator.EQUALS,
													"SALES_ORDER"),
									EntityCondition.makeCondition("createdBy",
											EntityOperator.EQUALS,
											userLogin.get("userLoginId"))));

					List<GenericValue> monthList = delegator.findList(
							"OrderHeader", ecl, UtilMisc.toSet("grandTotal"),
							null, null, false);
					BigDecimal total = BigDecimal.ZERO;
					for (GenericValue grandTotal : monthList) {
						total = total.add(grandTotal
								.getBigDecimal("grandTotal"));
					}
					monthMap.put(
							(cal.get(Calendar.MONTH) + 1) + "-"
									+ cal.get(Calendar.YEAR), total);
				}
				request.setAttribute("monthAmount", monthMap);
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "success";
	}
	/**
	 * lÃƒÆ’Ã‚Â¡Ãƒâ€šÃ‚ÂºÃƒâ€šÃ‚Â¥y vÃƒÆ’Ã‚Â¡Ãƒâ€šÃ‚Â»ÃƒÂ¯Ã‚Â¿Ã‚Â½ danh
	 * sÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ch cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡c
	 * ÃƒÆ’Ã¢â‚¬Å¾ÃƒÂ¢Ã¢â€šÂ¬Ã‹Å“ÃƒÆ’Ã¢â‚¬Â Ãƒâ€šÃ‚Â¡n hÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â ng
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws GenericEntityException
	 * @throws GenericServiceException
	 */

	@SuppressWarnings("unchecked")
	public static Map<String, Object> orderHeaderListView(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Integer page = Integer.parseInt((String)context.get("viewIndex"));
		Integer size = Integer.parseInt((String) context.get("viewSize"));
		String time = (String) context.get("time");
		String customers = (String) context.get("customers");
		Delegator delegator = dctx.getDelegator();
		String optionSelect = time;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Timestamp End = null;
		Timestamp Start = null;
		EntityCondition condStart = null;
		EntityCondition condEnd = null;
		if(UtilValidate.isEmpty(optionSelect) || optionSelect == null){
			optionSelect = "week";
		}
		String userLoginId = userLogin.getString("userLoginId");
		EntityCondition createBy = EntityCondition.makeCondition(
				"createdBy", EntityOperator.EQUALS, userLoginId);
		EntityCondition roleTypeId = EntityCondition.makeCondition(
				"roleTypeId", EntityOperator.EQUALS, "PLACING_CUSTOMER");
		if(optionSelect.equals("month")){
			End = UtilDateTime.getMonthEnd(nowTimestamp, TimeZone.getDefault(),Locale.getDefault());
			Start  = UtilDateTime.getMonthStart(nowTimestamp);
			condStart = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,Start);
			condEnd = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,End);
		}else if(optionSelect.equals("week")){
			End = UtilDateTime.getWeekEnd(nowTimestamp);
			Start = UtilDateTime.getWeekStart(nowTimestamp);
			condStart = EntityCondition.makeCondition("orderDate",EntityJoinOperator.GREATER_THAN_EQUAL_TO,Start);
			condEnd = EntityCondition.makeCondition("orderDate",EntityJoinOperator.LESS_THAN_EQUAL_TO,End);
		}
		List<EntityCondition> lc = UtilMisc.toList(createBy, roleTypeId, condStart, condEnd);
		if(UtilValidate.isNotEmpty(customers)){
			List<String> partyIds = FastList.newInstance();
			JSONArray listCustomer = JSONArray.fromObject(customers);
			for(int i = 0; i < listCustomer.size(); i++){
				String o = listCustomer.getString(i);
				partyIds.add(o);
			}
			if(!partyIds.isEmpty()){
				lc.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
				int orderCreated = OrderUtils.getTotalOrderByStatus(delegator, userLoginId, partyIds, Start, End, "ORDER_CREATED");
				res.put("orderCreated", orderCreated);
				int orderCancelled = OrderUtils.getTotalOrderByStatus(delegator, userLoginId, partyIds, Start, End, "ORDER_CANCELLED");
				res.put("orderCancelled", orderCancelled);
				int orderApproved = OrderUtils.getTotalOrderByStatus(delegator, userLoginId, partyIds, Start, End, "ORDER_APPROVED");
				res.put("orderApproved", orderApproved);
				int orderCompleted = OrderUtils.getTotalOrderByStatus(delegator, userLoginId, partyIds, Start, End, "ORDER_COMPLETED");
				res.put("orderCompleted", orderCompleted);
			}
		}

		List<Map<String, Object>> orderList = FastList.newInstance();
		EntityListIterator list = null;
		try {
			List<GenericValue> orderHeaderList = FastList.newInstance();
			EntityConditionList<EntityCondition> listCondition = EntityCondition
					.makeCondition(lc, EntityOperator.AND);
			Set<String> fields = UtilMisc.toSet("orderId", "orderDate",
					 "grandTotal", "roleTypeId", "groupName","statusId");
			List<String> orderBy = UtilMisc.toList("orderDate DESC");
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			list = delegator.find("OrderHeaderDetail", listCondition, null, fields, orderBy, options);
			res.put("totalOrder", list.getResultsTotalSize());
			int from = size * page;
			orderHeaderList = list.getPartialList(from, size);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd-MM-yyyy");
			for (GenericValue cur : orderHeaderList) {
				String e = simpleDateFormat.format((Timestamp) cur
						.getTimestamp("orderDate"));
				Map<String, Object> obj = FastMap.newInstance();
				obj.put("orderId", cur.getString("orderId"));
				obj.put("orderDate", e);
				obj.put("grandTotal", cur.getBigDecimal("grandTotal"));
				obj.put("roleTypeId", cur.getString("roleTypeId"));
				obj.put("groupName", cur.getString("groupName"));
				obj.put("statusId", cur.getString("statusId"));
				orderList.add(obj);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError("error");
		} finally {
			list.close();
		}
		res.put("listOrder", orderList);
		return res;
		// "orderStatusState", statusState
	}
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getOrderStatus(HttpServletRequest request,
			HttpServletResponse response) {
		String orderId = (String) request.getParameter("orderId");
		String statusUserLogin = null;
		GenericValue orderHeader = null;
		Map<String, Object> ctxMap = FastMap.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		DecimalFormat format = new DecimalFormat("###,###.###");
		if (currencyUom == null) {
			currencyUom = EntityUtilProperties.getPropertyValue(
					"general.properties", "currency.uom.id.default", "USD",
					delegator);
		}
		try {
			if (userLogin != null) {
				if (orderId != null) {
					orderHeader = delegator.findOne("OrderHeader",
							UtilMisc.toMap("orderId", orderId), false);
					List<GenericValue> orderStatuses = orderHeader.getRelated(
							"OrderStatus", null, null, false);
					List<GenericValue> filteredOrderStatusList = FastList
							.newInstance();
					boolean extOfflineModeExists = false;
					List<GenericValue> orderPaymentPreferences = orderHeader
							.getRelated(
									"OrderPaymentPreference",
									null,
									UtilMisc.toList("orderPaymentPreferenceId"),
									false);
					List<GenericValue> filteredOrderPaymentPreferences = EntityUtil
							.filterByCondition(orderPaymentPreferences,
									EntityCondition.makeCondition(
											"paymentMethodTypeId",
											EntityOperator.IN,
											UtilMisc.toList("EXT_OFFLINE")));
					if (filteredOrderPaymentPreferences != null) {
						extOfflineModeExists = true;
					}

					if (extOfflineModeExists) {
						filteredOrderStatusList = EntityUtil.filterByCondition(
								orderStatuses, EntityCondition.makeCondition(
										"statusId", EntityOperator.IN, UtilMisc
												.toList("ORDER_COMPLETED",
														"ORDER_APPROVED",
														"ORDER_CREATED")));
					} else {
						filteredOrderStatusList = EntityUtil.filterByCondition(
								orderStatuses, EntityCondition.makeCondition(
										"statusId", EntityOperator.IN, UtilMisc
												.toList("ORDER_COMPLETED",
														"ORDER_APPROVED")));

					}
					if (UtilValidate.isNotEmpty(filteredOrderStatusList)) {
						if (filteredOrderStatusList.size() < 2) {
							statusUserLogin = EntityUtil.getFirst(
									filteredOrderStatusList).getString(
									"statusUserLogin");
						} else {
							for (GenericValue orderStatus : filteredOrderStatusList) {
								if ("ORDER_COMPLETED".equals(orderStatus
										.get("statusId"))) {
									statusUserLogin = orderStatus
											.getString("statusUserLogin");
									userLogin = delegator.findOne("UserLogin",
											UtilMisc.toMap("userLoginId",
													statusUserLogin), false);
								}
							}
						}
					}
				}
			}
			// context.userLogin = userLogin;
			// String partyId = (String) request.getParameter("customerId");
			String roleTypeId = null;
			if (orderId != null) {
				orderHeader = delegator.findOne("OrderHeader",
						UtilMisc.toMap("orderId", orderId), false);
				if ("PURCHASE_ORDER".equals(orderHeader.get("orderTypeId"))) {
					roleTypeId = "SUPPLIER_AGENT";
				} else {
					roleTypeId = "PALCING_CUSTOMER";
				}
				ctxMap.put("roleTypeId", roleTypeId);

			}
			if (orderHeader != null) {
				GenericValue productStore = orderHeader.getRelatedOne(
						"ProductStore", true);
				OrderReadHelper orderReadHelper = new OrderReadHelper(
						orderHeader);
				List<GenericValue> orderItems = orderReadHelper.getOrderItems();
				List<GenericValue> orderAdjustments = orderReadHelper
						.getAdjustments();
				List<GenericValue> orderHeaderAdjustments = orderReadHelper
						.getOrderHeaderAdjustments();
				BigDecimal orderSubTotal = orderReadHelper
						.getOrderItemsSubTotal();
				List<GenericValue> orderItemShipGroups = orderReadHelper
						.getOrderItemShipGroups();
				List<GenericValue> headerAdjustmentsToShow = orderReadHelper
						.getOrderHeaderAdjustments();
				List<GenericValue> ajustments = orderReadHelper
						.getAdjustments();
				// GenericValue billAddr = orderReadHelper.getBillingAddress();
				GenericValue endUserParty = orderReadHelper.getEndUserParty();
				Map<String, BigDecimal> orderItemReturnedQuantities = orderReadHelper
						.getOrderItemReturnedQuantities();
				List<GenericValue> orderReturnedItems = orderReadHelper
						.getOrderReturnItems();
				boolean rejectedOrderItems = orderReadHelper
						.getRejectedOrderItems();
				GenericValue shipToParty = orderReadHelper.getShipToParty();
				BigDecimal shippableQuantity = orderReadHelper
						.getShippableQuantity();
				List<BigDecimal> shippableSizes = orderReadHelper
						.getShippableSizes();
				GenericValue shippingAddress = orderReadHelper
						.getShippingAddress();
				List<GenericValue> shippingLocations = orderReadHelper
						.getShippingLocations();
				BigDecimal shippingTotal = orderReadHelper.getShippingTotal();

				BigDecimal orderShippingTotal = OrderReadHelper
						.getAllOrderItemsAdjustmentsTotal(orderItems,
								orderAdjustments, false, false, true);
				orderShippingTotal = orderShippingTotal.add(OrderReadHelper
						.calcOrderAdjustments(orderHeaderAdjustments,
								orderSubTotal, false, false, true));

				BigDecimal orderTaxTotal = OrderReadHelper
						.getAllOrderItemsAdjustmentsTotal(orderItems,
								orderAdjustments, false, true, false);
				orderTaxTotal = orderTaxTotal.add(OrderReadHelper
						.calcOrderAdjustments(orderHeaderAdjustments,
								orderSubTotal, false, true, false));

				List<GenericValue> placingCustomerOrderRoles = delegator
						.findByAnd("OrderRole", UtilMisc.toMap("orderId",
								orderId, "roleTypeId", roleTypeId), null, false);
				GenericValue placingCustomerOrderRole = EntityUtil
						.getFirst(placingCustomerOrderRoles);
				GenericValue placingCustomerPerson = placingCustomerOrderRole == null ? null
						: delegator
								.findOne("Person", UtilMisc
										.toMap("partyId",
												placingCustomerOrderRole
														.get("partyId")), false);

				GenericValue billingAccount = orderHeader.getRelatedOne(
						"BillingAccount", false);

				List<GenericValue> orderPaymentPreferences = EntityUtil
						.filterByAnd(orderHeader.getRelated(
								"OrderPaymentPreference", null, null, false),
								UtilMisc.toList(EntityCondition.makeCondition(
										"statusId", EntityOperator.NOT_EQUAL,
										"PAYMENT_CANCELLED")));
				List<GenericValue> paymentMethods = FastList.newInstance();
				for (GenericValue opp : orderPaymentPreferences) {
					GenericValue paymentMethod = opp.getRelatedOne(
							"PaymentMethod", false);
					if (paymentMethod != null) {
						paymentMethods.add(paymentMethod);
					} else {
						GenericValue paymentMethodType = opp.getRelatedOne(
								"PaymentMethodType", false);
						if (paymentMethodType != null) {
							ctxMap.put("paymentMethodType", paymentMethodType);
						}
					}
				}
				String payToPartyId = productStore.getString("payToPartyId");
				GenericValue paymentAddress = PaymentWorker.getPaymentAddress(
						delegator, payToPartyId);
				if (paymentAddress != null) {
					ctxMap.put("paymentAddress", paymentAddress);
				}
				// get Shipment tracking info
				EntityFieldMap osisCond = EntityCondition.makeCondition(
						UtilMisc.toMap("orderId", orderId), EntityOperator.AND);
				List<String> osisOrder = UtilMisc.toList("shipmentId",
						"shipmentRouteSegmentId", "shipmentPackageSeqId");
				Set<String> osisFields = UtilMisc.toSet("shipmentId",
						"shipmentRouteSegmentId", "carrierPartyId",
						"shipmentMethodTypeId");
				osisFields.add("shipmentPackageSeqId");
				osisFields.add("trackingCode");
				osisFields.add("boxNumber");
				EntityFindOptions osisFindOptions = new EntityFindOptions();
				osisFindOptions.setDistinct(true);
				List<GenericValue> orderShipmentInfoSummaryList = delegator
						.findList("OrderShipmentInfoSummary", osisCond,
								osisFields, osisOrder, osisFindOptions, false);
				Set<String> customerPoNumberSet = new TreeSet<String>();
				for (GenericValue orderItemPo : orderItems) {
					String correspondingPoId = orderItemPo
							.getString("correspondingPoId");
					if (correspondingPoId != null
							&& !"(none)".equals(correspondingPoId)) {
						customerPoNumberSet.add(correspondingPoId);
					}
				}
				// check if there are returnable items
				BigDecimal returned = new BigDecimal("0.00");
				BigDecimal totalItems = new BigDecimal("0.00");
				for (GenericValue oitem : orderItems) {
					totalItems = totalItems
							.add(oitem.getBigDecimal("quantity"));
					List<GenericValue> ritems = oitem.getRelated("ReturnItem",
							null, null, false);
					for (GenericValue ritem : ritems) {
						GenericValue rh = ritem.getRelatedOne("ReturnHeader",
								false);
						if (!rh.get("statusId").equals("RETURN_CANCELLED")) {
							returned = returned.add(ritem
									.getBigDecimal("returnQuantity"));
						}
					}
					if (!orderReadHelper.getOrderItemAdjustments(oitem)
							.isEmpty()) {
						ctxMap.put(
								orderId + "_"
										+ oitem.getString("orderItemSeqId"),
								orderReadHelper.getOrderItemAdjustments(oitem));
					}
					ctxMap.put(
							orderId + "_" + oitem.getString("orderItemSeqId")
									+ "_adjustmentsTotal",
							format.format(orderReadHelper
									.getOrderItemAdjustmentsTotal(oitem))
									+ " "
									+ currencyUom);
					ctxMap.put(
							orderId + "_" + oitem.getString("orderItemSeqId")
									+ "_total",
							format.format(orderReadHelper
									.getOrderItemTotal(oitem))
									+ " "
									+ currencyUom);
				}
				if (totalItems.compareTo(returned) > 0) {
					ctxMap.put("returnLink", "Y");
				}
				if (ajustments.size() > 0) {
					Map<String, Object> productPromo = FastMap.newInstance();
					for (GenericValue ajusment : ajustments) {
						productPromo.put(ajusment.getString("productPromoId"),
								ajusment.getString("description"));
					}
					ctxMap.put("ajustment", orderReadHelper.getAdjustments());// maybe
																				// delete
					ctxMap.put("productPromo", productPromo);
				}

				ctxMap.put("statusId", orderHeader.getString("statusId"));

				ctxMap.put("orderId", orderId);
				ctxMap.put("orderHeader", orderHeader);

				ctxMap.put("endUserParty", endUserParty);
				ctxMap.put("orderItemReturnedQuantities",
						orderItemReturnedQuantities);
				ctxMap.put("rejectedOrderItems", rejectedOrderItems);
				ctxMap.put("shipToParty", shipToParty);
				ctxMap.put("shippableQuantity", shippableQuantity);
				ctxMap.put("orderReturnedItems", orderReturnedItems);
				ctxMap.put("shippableSizes", shippableSizes);
				ctxMap.put("shippingAddress", shippingAddress);
				ctxMap.put("shippingLocations", shippingLocations);
				ctxMap.put("shippingTotal", format.format(shippingTotal) + " "
						+ currencyUom);
				ctxMap.put("billToParty", orderReadHelper.getBillToParty());

				// ctxMap.put("localOrderReadHelper", orderReadHelper);
				ctxMap.put("orderItems", orderItems);
				ctxMap.put("orderAdjustments", orderAdjustments);
				ctxMap.put("orderHeaderAdjustments", orderHeaderAdjustments);
				ctxMap.put("orderSubTotal", format.format(orderSubTotal) + " "
						+ currencyUom);
				ctxMap.put("orderItemShipGroups", orderItemShipGroups);
				ctxMap.put("headerAdjustmentsToShow", headerAdjustmentsToShow);
				ctxMap.put("currencyUomId", orderReadHelper.getCurrency());

				ctxMap.put("orderShippingTotal", orderShippingTotal);
				ctxMap.put("orderTaxTotal", orderTaxTotal);
				ctxMap.put(
						"orderGrandTotal",
						format.format(OrderReadHelper.getOrderGrandTotal(
								orderItems, orderAdjustments))
								+ " "
								+ currencyUom);
				ctxMap.put("placingCustomerPerson", placingCustomerPerson);

				ctxMap.put("billingAccount", billingAccount);
				ctxMap.put("paymentMethods", paymentMethods);

				ctxMap.put("productStore", productStore);
				// ctxMap.put("isDemoStore", isDemoStore);

				ctxMap.put("orderShipmentInfoSummaryList",
						orderShipmentInfoSummaryList);
				ctxMap.put("customerPoNumberSet", customerPoNumberSet);
				List<GenericValue> orderItemChangeReasons = delegator
						.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId",
								"ODR_ITM_CH_REASON"), UtilMisc
								.toList("sequenceId"), false);
				ctxMap.put("orderItemChangeReasons", orderItemChangeReasons);
				request.setAttribute("orderStatus", ctxMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	public static Map<String, Object> getOrderItemsBeingTransfer(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		String productStoreId = (String) context.get("productStoreId");
		try {
			List<GenericValue> stores = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(
									UtilMisc.toList(EntityCondition.makeCondition("productStoreId", productStoreId),
											EntityCondition.makeCondition("roleTypeId", "OWNER"),
											EntityUtil.getFilterByDateExpr())), UtilMisc.toSet("partyId"), UtilMisc.toList("-fromDate"), null, false);
			GenericValue store = EntityUtil.getFirst(stores);
			if(UtilValidate.isNotEmpty(store)){
				String partyId = store.getString("partyId");
				List<EntityExpr> listConds = UtilMisc.toList(EntityCondition.makeCondition("statusId", "ORDER_IN_TRANSIT"),
						EntityCondition.makeCondition("customerId", partyId));
				List<GenericValue> orders = delegator.findList("OrderHeaderAndOrderRoleFromTo",
						EntityCondition.makeCondition(listConds), UtilMisc.toSet("orderId"), null, null, false);
				List<String> orderIds = FastList.newInstance();
				for(GenericValue o : orders){
					orderIds.add(o.getString("orderId"));
				}
				if(UtilValidate.isNotEmpty(orderIds)){
					EntityFindOptions options = new EntityFindOptions();
					options.setDistinct(true);
					List<GenericValue> orderItems = delegator.findList("OrderItemAndProduct",
							EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds),
									EntityCondition.makeCondition("isPromo", "N"))), UtilMisc.toSet("productId", "productName"), UtilMisc.toList("productId"), options, false);
					res.put("results", orderItems);
				}

			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());

		}

		return res;
	}

    /*API New*/
    public static Map<String, Object> mGetSalesOrders(DispatchContext ctx, Map<String, Object> context) {
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
        String productStoreId = (String) context.get("productStoreId");
        parameters.put("pagenum", new String[]{pagenum});
        parameters.put("pagesize", new String[]{pagesize});
        List<String> fieldsToSelect = FastList.newInstance();
        fieldsToSelect.add("orderId");
        fieldsToSelect.add("currencyUom");
        fieldsToSelect.add("statusId");
        fieldsToSelect.add("remainingSubTotal");
        fieldsToSelect.add("grandTotal");
        fieldsToSelect.add("orderDate");
        fieldsToSelect.add("orderTypeId");
        fieldsToSelect.add("createdBy");
        listSortFields.add("-orderDate");
        List<GenericValue> salesOrders = FastList.newInstance();
        try {
            listAllConditions.add(EntityCondition.makeCondition("createdBy", userLoginId));
            listAllConditions.add(EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"));
            listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
            salesOrders = EntityMiscUtil.processIteratorToList(parameters,successResult,delegator,"OrderHeader",
                    EntityCondition.makeCondition(listAllConditions),null,UtilMisc.toSet(fieldsToSelect),listSortFields,opts);
            successResult.put("salesOrders", salesOrders);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> mGetSalesOrderDetails(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        Locale locale = (Locale)context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        if (!context.containsKey("orderId")) {
            return ServiceUtil.returnError(UtilProperties.getMessage("OldUiLabels", "OrderNotFound", locale));
        }
        String orderId = (String)context.get("orderId");
        try {
            GenericValue orderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId",orderId),false);
            OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
            List<GenericValue> orderItems = orderReadHelper.getOrderItems();
            List<GenericValue> orderAdjustments = orderReadHelper.getAdjustments();
            String currencyUomId = orderReadHelper.getCurrency();
            GenericValue displayParty = orderReadHelper.getPlacingParty();
            String customerId = displayParty.getString("partyId");
            String customerName = displayParty.getString("groupName");

            //get address shipping
            String address = "";
            /*List<Map<String,GenericValue>> listContactMechMaps = ContactMechWorker.getOrderContactMechValueMaps(delegator,orderId);*/
            List<GenericValue> allOrderContactMechs = null;
            try {
                allOrderContactMechs = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId), null, false);
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
            for(GenericValue orderContactMech : allOrderContactMechs){
            	GenericValue contactMech = null;

                try {
                    contactMech = orderContactMech.getRelatedOne("ContactMech", false);
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
                if (contactMech != null) {
                    try{
                    	if ("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
                    		List<GenericValue> postalAddress =  delegator.findByAnd("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")), null, false);
                            for(GenericValue postalAdd : postalAddress){
                            	address = postalAdd.getString("fullName");
                            	break;
                            }
                    	}
                    } catch (GenericEntityException e){
                    	Debug.logWarning(e, module);
                    }
                }
            }         
          /*  for (Map<String,GenericValue> contactMechMap: listContactMechMaps) {
                GenericValue contactMech = contactMechMap.get("contactMech");
                if (UtilValidate.isNotEmpty(contactMech) && contactMech.getString("contactMechTypeId").equals("POSTAL_ADDRESS")) {
                    GenericValue postalAddress = contactMechMap.get("postalAddress");
                    address = postalAddress.getString("address1");
                    break;
                }
            }*/

            //getPriority
            GenericValue priorityGv = delegator.findOne("Enumeration",UtilMisc.toMap("enumId",orderHeader.getString("priority")),false);
            String priority = priorityGv.getString("description");

            //getOrderDate
            Timestamp orderDateTimestamp = orderHeader.getTimestamp("orderDate");
            long orderDate = 0;
            if (orderDateTimestamp!=null) {
                orderDate = orderDateTimestamp.getTime();
            }

            //get itemLines
            List<Map<String,Object>> listItemLines = FastList.newInstance();
            for (GenericValue orderItem : orderItems) {
                Map<String,Object> o = FastMap.newInstance();
                o.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                o.put("productId", orderItem.getString("productId"));
                GenericValue prod = delegator.findOne("Product", UtilMisc.toMap("productId", orderItem.getString("productId")), false);
                o.put("smallImageUrl", prod.getString("smallImageUrl"));
                o.put("largeImageUrl", prod.getString("largeImageUrl"));
                o.put("productName", orderItem.getString("itemDescription"));
                o.put("isPromo", orderItem.getString("isPromo"));
                //o.put("quantity", orderItem.getBigDecimal("quantity"));
                //o.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
                String uomId = orderItem.getString("quantityUomId");
                o.put("quantityUomId", uomId);
                GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
                o.put("quantityUomName", (String)uom.get("description"));
                o.put("alternativeQuantity", orderItem.getBigDecimal("alternativeQuantity"));
                o.put("alternativeUnitPrice", orderItem.getBigDecimal("alternativeUnitPrice"));
                listItemLines.add(o);
            }
            BigDecimal discountAmount = orderReadHelper.getOrderAdjustmentsTotal();
            BigDecimal grandTotal = orderReadHelper.getOrderGrandTotal();
            BigDecimal taxAmount = orderReadHelper.getTaxTotal();
            BigDecimal subTotal = orderReadHelper.getOrderItemsSubTotal();

            //set result
            successResult.put("orderId",orderId);
            if (orderDate > 0) {
                successResult.put("orderDate",orderDate);
            } else {
                successResult.put("orderDate",null);
            }
            successResult.put("customerId",customerId);
            successResult.put("customerName",customerName);
            successResult.put("address",address);
            successResult.put("priority",priority);

            successResult.put("subTotal",subTotal);
            successResult.put("discountAmount",discountAmount);
            successResult.put("taxAmount",taxAmount);
            successResult.put("grandTotal",grandTotal);

            successResult.put("itemLines",listItemLines);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }
    /*end API New*/
}
