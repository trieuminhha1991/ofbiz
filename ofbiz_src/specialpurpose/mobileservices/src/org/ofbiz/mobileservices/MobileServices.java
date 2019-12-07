package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.Mobile;
import org.ofbiz.ProcessMobileApps;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.conversion.DateTimeConverters.TimestampToString;
import org.ofbiz.base.util.Debug;
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
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.service.SalesmanServices;

public class MobileServices implements Mobile {
	
	public static final String module = MobileServices.class.getName();
	public static String currencyUom = null;
	public static boolean result = true;
	private static ProcessMobileApps process = new ProcessMobileApps();
	public static String getProductList(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> products = FastList.newInstance();
		try {
			List<GenericValue> gvList = delegator.findList("Product", null,
					null, null, null, false);

			for (GenericValue value : gvList) {
				Map<String, Object> product = FastMap.newInstance();
				product.put("productId", value.get("productId"));
				product.put("productName", value.get("productName"));
				product.put("quanlity", 0);
				products.add(product);
			}
			request.setAttribute("products", products);
		} catch (GenericEntityException ge) {
			return "error";
		}
		return "success";
	}

	/*
	 */
	public static String getNotification(HttpServletRequest request,
			HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Map<String, Object> result = null;
		try {
			GenericValue userLogin = (GenericValue) request.getSession()
					.getAttribute("userLogin");
			if (userLogin != null) {
				result = dispatcher.runSync("getLastSystemInfoNoteMobile",
						UtilMisc.toMap("userLogin", userLogin));
				GenericValue lastSystemInfoNote1 = (GenericValue) result
						.get("lastSystemInfoNote1");
				GenericValue lastSystemInfoNote2 = (GenericValue) result
						.get("lastSystemInfoNote2");
				GenericValue lastSystemInfoNote3 = (GenericValue) result
						.get("lastSystemInfoNote3");
				String middleTopMessage1 = lastSystemInfoNote1 != null ? lastSystemInfoNote1
						.get("noteDateTime").toString().substring(0, 16)
						+ " " + lastSystemInfoNote1.get("noteInfo")
						: "";
				String middleTopMessage2 = lastSystemInfoNote2 != null ? lastSystemInfoNote2
						.get("noteDateTime").toString().substring(0, 16)
						+ " " + lastSystemInfoNote2.get("noteInfo")
						: "";
				String middleTopMessage3 = lastSystemInfoNote3 != null ? lastSystemInfoNote3
						.get("noteDateTime").toString().substring(0, 16)
						+ " " + lastSystemInfoNote3.get("noteInfo")
						: "";
				// "${lastSystemInfoNote1.moreInfoUrl}${groovy: if (lastSystemInfoNote1&amp;&amp;lastSystemInfoNote1.moreInfoItemName&amp;&amp;lastSystemInfoNote1.moreInfoItemId)&quot;?&quot; + lastSystemInfoNote1.moreInfoItemName + &quot;=&quot; + lastSystemInfoNote1.moreInfoItemId + &quot;&amp;id=&quot; + lastSystemInfoNote1.moreInfoItemId;}"
				String middleTopLink1 = lastSystemInfoNote1 != null ?
				/*
				 * * lastSystemInfoNote1 .get( "moreInfoUrl" ) .toString ()
				 */

				"mobileservices/control/taskView" : "";
				if ((lastSystemInfoNote1 != null)
						&& (lastSystemInfoNote1.get("moreInfoItemName") != null)
						&& (lastSystemInfoNote1.get("moreInfoItemId") != null)) {
					middleTopLink1 += "?"
							+ lastSystemInfoNote1.get("moreInfoItemName")
									.toString() + "="
							+ lastSystemInfoNote1.getString("moreInfoItemId")
							+ "&id="
							+ lastSystemInfoNote1.getString("moreInfoItemId");
				}
				String middleTopLink2 = lastSystemInfoNote2 != null ?
				/**
				 * lastSystemInfoNote2 .get( "moreInfoUrl" ) .toString ()
				 */

				"mobileservices/control/taskView" : "";
				if ((lastSystemInfoNote2 != null)
						&& (lastSystemInfoNote2.get("moreInfoItemName") != null)
						&& (lastSystemInfoNote2.get("moreInfoItemId") != null)) {
					middleTopLink2 += "?"
							+ lastSystemInfoNote2.get("moreInfoItemName")
									.toString() + "="
							+ lastSystemInfoNote2.getString("moreInfoItemId")
							+ "&id="
							+ lastSystemInfoNote2.getString("moreInfoItemId");
				}
				String middleTopLink3 = lastSystemInfoNote3 != null ?
				/**
				 * lastSystemInfoNote3 .get( "moreInfoUrl" ) .toString ()
				 */

				"mobileservices/control/taskView" : "";
				if ((lastSystemInfoNote3 != null)
						&& (lastSystemInfoNote3.get("moreInfoItemName") != null)
						&& (lastSystemInfoNote3.get("moreInfoItemId") != null)) {
					middleTopLink3 += "?"
							+ lastSystemInfoNote3.get("moreInfoItemName")
									.toString() + "="
							+ lastSystemInfoNote3.getString("moreInfoItemId")
							+ "&id="
							+ lastSystemInfoNote3.getString("moreInfoItemId");
				}
				request.setAttribute("middleTopMessage1", middleTopMessage1);
				request.setAttribute("middleTopMessage2", middleTopMessage2);
				request.setAttribute("middleTopMessage3", middleTopMessage3);
				request.setAttribute("middleTopLink1", middleTopLink1);
				request.setAttribute("middleTopLink2", middleTopLink2);
				request.setAttribute("middleTopLink3", middleTopLink3);
			}
			return "success";
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}

	public static String getProjectTask(HttpServletRequest request,
			HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");

		String taskId = request.getParameter("workEffortId");
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		Map<String, Object> combineMap = UtilHttp.getCombinedMap(request);
		combineMap.put("taskId", taskId);
		try {
			if (userLogin != null) {
				Map<String, Object> resultMap = dispatcher.runSync(
						"getProjectTask", combineMap);
				Map<String, Object> projectIdAndNameFromTask = dispatcher
						.runSync("getProjectIdAndNameFromTask", combineMap);
				request.setAttribute("taskResult", resultMap);
				request.setAttribute("projectIdAndName",
						projectIdAndNameFromTask);
			}
			return "success";
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}

	public static String getAllProductPromotion(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Map<String, Object> promotionInfoMap = FastMap.newInstance();
		Set<String> productIdsCond = new HashSet<String>();
		Set<String> productIdsAction = new HashSet<String>();
		List<String> productIds;
		DateTimeConverters.TimestampToString converter = new TimestampToString();
		List<GenericValue> productPromos = ProductPromoWorker
				.getStoreProductPromos(delegator, dispatcher, request);
		for (GenericValue productPromo : productPromos) {
			productIds = null;
			String productPromoId = productPromo.getString("productPromoId");
			try {
				ProductPromoWorker.makeProductPromoCondActionIdSets(
						productPromoId, productIdsCond, productIdsAction,
						delegator, null);
				productIds = UtilMisc.toList(productIdsCond);
				productIds.addAll(productIdsAction);
				List<GenericValue> products = delegator.findList("Product",
						EntityCondition.makeCondition("productId",
								EntityOperator.IN, productIds), UtilMisc.toSet(
								"productId", "productName"), null, null, false);
				String promoText = (productPromo.getString("promoText") != null) ? productPromo
						.getString("promoText") : productPromoId;
				EntityCondition ec = EntityCondition
						.makeCondition("productPromoId", EntityOperator.EQUALS,
								productPromoId);
				List<GenericValue> productStorePromoAppls = delegator.findList(
						"ProductStorePromoAppl", ec, null,
						UtilMisc.toList("productPromoId"), null, false);
				String fromDate = converter.convert(
						productStorePromoAppls.get(0).getTimestamp("fromDate"),
						Locale.getDefault(), TimeZone.getDefault(),
						UtilDateTime.DATE_FORMAT);
				String thruDate = converter.convert(
						productStorePromoAppls.get(0).getTimestamp("thruDate"),
						Locale.getDefault(), TimeZone.getDefault(),
						UtilDateTime.DATE_FORMAT);
				promotionInfoMap.put(productPromoId, UtilMisc.toMap(
						"promoName", productPromo.getString("promoName"),
						"promoText", promoText, "productIdsCond",
						productIdsCond, "productIdsAction", productIdsAction,
						"products", products, "fromDate", fromDate, "thruDate",
						thruDate));

			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		request.setAttribute("productPromosInfo", promotionInfoMap);
		return "success";
	}

	/*
	 * begin dashboard screen
	 */
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getReportProduct(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String StoreId = (String) request.getParameter("partyId");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		
		try {
			List<EntityCondition> list = FastList.newInstance();
			if(UtilValidate.isNotEmpty(userLogin)&&UtilValidate.isNotEmpty(StoreId))
			{
				list.add(EntityCondition.makeCondition("createdBy",EntityJoinOperator.EQUALS,(String)userLogin.get("partyId")));
				list.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS,(String)StoreId));
			}
			List<GenericValue> productQuantityList = delegator.findList(
					"OrderReportSalesMan", EntityCondition.makeCondition(list,EntityJoinOperator.AND), null, null, null, false);
			Map<String, Map<String, Object>> sumQtyOfProductId = FastMap
					.newInstance();
			if(UtilValidate.isNotEmpty(productQuantityList))
			{
				for (GenericValue product : productQuantityList) {
					String productId = product.getString("productId");
					if (sumQtyOfProductId.containsKey(productId)) {
						BigDecimal quantity = (BigDecimal) sumQtyOfProductId.get(
								productId).get("quantity");
						quantity = quantity.add(product.getBigDecimal("quantity"));
						sumQtyOfProductId.get(productId).put("quantity", quantity);
					} else {
						sumQtyOfProductId.put(productId, UtilMisc.toMap("quantity",
								product.getBigDecimal("quantity"), "internalName",
								product.get("internalName")));
					}
				}
			}
			request.setAttribute("quantityIfo", sumQtyOfProductId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getBestProductInMonth(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String month = (String) context.get("month");
		if (userLogin != null) {
			Timestamp monthStart = UtilDateTime.monthBegin();
			Timestamp monthEnd = UtilDateTime.getMonthEnd(
					UtilDateTime.nowTimestamp(), TimeZone.getDefault(),
					Locale.getDefault());
			if (month == null || month.equalsIgnoreCase("thisMonth")) {
				monthStart = UtilDateTime.monthBegin();
				monthEnd = UtilDateTime.getMonthEnd(
						UtilDateTime.nowTimestamp(), TimeZone.getDefault(),
						Locale.getDefault());
			} else if (month.equalsIgnoreCase("lastMonth")) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -1);
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				monthStart = UtilDateTime.getMonthStart(timestamp);
				monthEnd = UtilDateTime.getMonthEnd(timestamp,
						TimeZone.getDefault(), Locale.getDefault());
			}

			EntityConditionList<EntityExpr> ecl = EntityCondition
					.makeCondition(UtilMisc.toList(EntityCondition
							.makeCondition("orderDate",
									EntityOperator.GREATER_THAN_EQUAL_TO,
									monthStart), EntityCondition.makeCondition(
							"orderDate", EntityOperator.LESS_THAN_EQUAL_TO,
							monthEnd)), EntityOperator.AND);

			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("OI", "OrderHeaderAndItems");

			dynamicView.addAlias("OI", "quantity", "quantity", "quantity",
					Boolean.FALSE, Boolean.FALSE, "sum");
			dynamicView.addAlias("OI", "productId", "productId", "productId",
					Boolean.FALSE, Boolean.TRUE, null);
			dynamicView.addAlias("OI", "orderDate");
			EntityFindOptions findOpts = new EntityFindOptions(true,
					EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, true);
			findOpts.setLimit(5);
			dynamicView.setGroupBy(UtilMisc.toList("orderDate"));
			try {
				EntityListIterator listBestProduct = delegator
						.findListIteratorByCondition(dynamicView, ecl, null,
								null, UtilMisc.toList("quantity DESC"),
								findOpts);
				List<GenericValue> result = listBestProduct.getCompleteList();
				Map<String, Object> data = FastMap.newInstance();
				for (GenericValue bestProduct : result) {
					data.put(bestProduct.getString("productId"),
							bestProduct.getBigDecimal("quantity"));
				}

				listBestProduct.close();
				Map<?, ?> retMap = UtilMisc.toMap("bestProduct", data);
				return (Map<String, Object>) retMap;
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<?, ?> retMap = UtilMisc.toMap("bestProduct",
				ServiceUtil.returnError("error"));
		return (Map<String, Object>) retMap;
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerAmount(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String month = (String) context.get("month");
		Timestamp monthBegin;
		Timestamp monthEnd;
		if (month != null && month.equalsIgnoreCase("lastMonth")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			Timestamp lastMonth = new Timestamp(c.getTimeInMillis());
			monthBegin = UtilDateTime.getMonthStart(lastMonth);
			monthEnd = UtilDateTime.getMonthEnd(lastMonth,
					TimeZone.getDefault(), Locale.getDefault());
		} else {
			monthBegin = UtilDateTime
					.getMonthStart(UtilDateTime.nowTimestamp());
			monthEnd = UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(),
					TimeZone.getDefault(), Locale.getDefault());
		}
		EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
				UtilMisc.toList(EntityCondition.makeCondition("createdStamp",
						EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin),
						EntityCondition.makeCondition("createdStamp",
								EntityOperator.LESS_THAN_EQUAL_TO, monthEnd),
						EntityCondition.makeCondition("roleTypeId",
								EntityOperator.EQUALS, "BILL_TO_CUSTOMER")),
				EntityOperator.AND);
		DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
		dynamicViewEntity.addMemberEntity("OrderRole", "OrderRole");
		dynamicViewEntity.addMemberEntity("OH", "OrderHeader");
		dynamicViewEntity.addMemberEntity("PS", "Person");
		dynamicViewEntity.addAlias("OrderRole", "orderId");
		dynamicViewEntity.addAlias("OrderRole", "partyId", "partyId",
				"partyId", Boolean.FALSE, Boolean.TRUE, null);
		dynamicViewEntity.addAlias("OH", "grandTotal", "grandTotal",
				"grandTotal", Boolean.FALSE, Boolean.FALSE, "sum");
		dynamicViewEntity.addAlias("OrderRole", "roleTypeId");
		dynamicViewEntity.addAlias("OrderRole", "createdStamp");
		dynamicViewEntity.addViewLink("OH", "OrderRole", Boolean.FALSE,
				UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
		EntityFindOptions findOpts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, true);
		EntityListIterator eli;
		try {
			eli = delegator.findListIteratorByCondition(dynamicViewEntity, ecl,
					null, UtilMisc.toList("partyId", "grandTotal"),
					UtilMisc.toList("grandTotal DESC"), findOpts);
			List<GenericValue> bestCusInMonthList = eli.getCompleteList();
			eli.close();
			BigDecimal sum = new BigDecimal(0);// sum of all grand total in
												// bestCusInMonthList
			BigDecimal sumOfGrandTotalInData = new BigDecimal(0); // sum of
																	// grand
																	// total
																	// that add
																	// to data
																	// Map
			Map<String, Object> data = FastMap.newInstance();
			GenericValue partyGroupName = null;
			int sizeOfdata = NUMBER_OF_BEST_CUS;
			for (GenericValue bestCusInMonth : bestCusInMonthList) {
				partyGroupName = delegator
						.findOne(
								"PartyGroup",
								UtilMisc.toMap("partyId",
										bestCusInMonth.get("partyId")), false);

				// TODO please check

				if (partyGroupName != null) {
					String partyName = partyGroupName.getString("groupName");
					sum = sum.add(bestCusInMonth.getBigDecimal("grandTotal"));
					if (data.size() < sizeOfdata) {
						data.put(partyName, bestCusInMonth.get("grandTotal"));
						sumOfGrandTotalInData = sumOfGrandTotalInData
								.add(bestCusInMonth.getBigDecimal("grandTotal"));
					}
				}
			}
			if (sum.subtract(sumOfGrandTotalInData).compareTo(BigDecimal.ZERO) > 0) {
				data.put("others", sum.subtract(sumOfGrandTotalInData));
			}
			Map<?, ?> retMap = UtilMisc.toMap("bestCus", data);
			return (Map<String, Object>) retMap;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<?, ?> retMap = UtilMisc.toMap("bestCus",
				ServiceUtil.returnError("error"));
		return (Map<String, Object>) retMap;
	}

	/*
	 * end dashboard screen
	 */


	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws GenericServiceException
	 */
	public static Map<String, Object> getProductStore(DispatchContext dcx, Map<String, Object> context) throws GenericServiceException{
		Map<String, Object> success = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		context.put("partyId", partyId);
		LocalDispatcher dispatcher = dcx.getDispatcher();
		Boolean isCustomerApp = dcx.getName().equals("mobilecustomer");
		Map<String, Object> in = FastMap.newInstance();
		in.put("partyId", partyId);
		try {
			if (isCustomerApp) {
				success = dispatcher.runSync("getListProductStoreByCustomer", in);
			} else {
				success = dispatcher.runSync("getListProductStoreBySeller", in);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			success = ServiceUtil.returnError(e.getMessage());
			throw e;
		}
		return success;
	}
	/*
	 * get all products
	 */
	public static String getAllProducts(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		String productStoreId = (String) request.getParameter("productStoreId");
		List<Map<String, Object>> resultList = MobileUtils.getAllProducts(request, productStoreId);
		request.setAttribute("listProduct", resultList);
		return "success";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getProductOfCat(HttpServletRequest request,
			HttpServletResponse response) {
		String prodCatId = (String) request.getParameter("productCategoryId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<EntityCondition> allConditions = FastList.newInstance();
		allConditions.add(EntityCondition.makeCondition("productCategoryId",
				EntityOperator.EQUALS, prodCatId));
		allConditions
				.add(EntityCondition.makeCondition("priceProductPriceTypeId",
						EntityOperator.EQUALS, "LIST_PRICE"));
		EntityCondition temp = EntityCondition.makeCondition(allConditions,
				EntityOperator.AND);
		List<Map<String, Object>> resultList = FastList.newInstance();
		try {
			String viewSize = request.getParameter("viewSize");
			String viewIndex = request.getParameter("viewIndex");
			int page = 0;
			int size = 10;
			if (viewSize != null) {
				size = Integer.parseInt(viewSize);
			}
			if (viewIndex != null) {
				page = Integer.parseInt(viewIndex);
			}
			EntityFindOptions options = new EntityFindOptions(true,
					EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, true);
			EntityListIterator iterator = delegator.find(
					"ProductCategoryMemberAndPrice", temp, null, UtilMisc
							.toSet("productId", "productInternalName",
									"productCategoryId", "pricePrice",
									"priceCurrencyUomId"), null, options);
			List<GenericValue> listProduct = iterator.getPartialList(size
					* page, size);
			Integer total = iterator.getResultsSizeAfterPartialList();
			for (GenericValue product : listProduct) {
				resultList.add(UtilMisc.toMap("productId",
						product.get("productId"), "productName",
						product.get("productInternalName"),
						"productCategoryId", product.get("productCategoryId"),
						"unitPrice", product.getBigDecimal("pricePrice"),
						"uom", product.get("priceCurrencyUomId")));
			}
			request.setAttribute("listProduct", resultList);
			request.setAttribute("total", total);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		return "success";
	}


	/**
	 * get customer detail information
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getCustomerDetailInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customerId = request.getParameter("customerId");
		// String time = request.getParameter("time");
		Map<String, Object> retMap = FastMap.newInstance();
		if (customerId != null) {
			try {
				GenericValue party = delegator.findOne("Party",
						UtilMisc.toMap("partyId", customerId), false);
				List<GenericValue> partyGeoView = delegator.findByAnd(
						"PartyGroupGeoView",
						UtilMisc.toMap("partyIdTo", customerId), null, true);
				GenericValue contactMech = EntityUtil.getFirst(ContactHelper
						.getContactMech(party, null, "POSTAL_ADDRESS", false));
				String city = "";
				if (contactMech != null) {
					GenericValue postalAddress = contactMech.getRelatedOne(
							"PostalAddress", false);
					if (postalAddress.get("city") != null) {
						city = postalAddress.getString("city");
					}
					retMap.put("address1", postalAddress.get("address1"));
					retMap.put("contactMechId",
							contactMech.get("contactMechId"));
					retMap.put("city", city);
					retMap.put("contactMechTypeId", "POSTAL_ADDRESS");
					retMap.put("postalCode", postalAddress.get("postalCode"));
					if (postalAddress.get("address2") != null)
						retMap.put("address2", postalAddress.get("address2"));
				}
				if (partyGeoView != null && !partyGeoView.isEmpty()) {
					retMap.put("groupName", partyGeoView.get(0)
							.get("groupName"));
					retMap.put("latitude", partyGeoView.get(0).get("latitude"));
					retMap.put("longitude", partyGeoView.get(0)
							.get("longitude"));
				}

			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
		}
		request.setAttribute("cusDetailsInfo", retMap);
		return "success";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getCusQtyProductInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customerId = request.getParameter("customerId");
		String time = request.getParameter("time");
		Map<String, Object> retMap = FastMap.newInstance();
		if (customerId != null) {
			try {
				Timestamp timeStart = UtilDateTime.getMonthStart(UtilDateTime
						.nowTimestamp());
				Timestamp timeEnd = UtilDateTime.getMonthEnd(
						UtilDateTime.nowTimestamp(), TimeZone.getDefault(),
						Locale.getDefault());
				if (time.equalsIgnoreCase("thisWeek")) {
					timeStart = UtilDateTime.getWeekStart(UtilDateTime
							.nowTimestamp());
					timeEnd = UtilDateTime.getWeekEnd(UtilDateTime
							.nowTimestamp());
				} else if (time.equalsIgnoreCase("lastMonth")) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MONTH, -1);
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					timeStart = UtilDateTime.getMonthStart(timestamp);
					timeEnd = UtilDateTime.getMonthEnd(timestamp,
							TimeZone.getDefault(), Locale.getDefault());
				}
				EntityConditionList<EntityExpr> ecl = EntityCondition
						.makeCondition(UtilMisc.toList(EntityCondition
								.makeCondition("partyId",
										EntityOperator.EQUALS, customerId),
								EntityCondition.makeCondition("createdStamp",
										EntityOperator.GREATER_THAN_EQUAL_TO,
										timeStart),
								EntityCondition.makeCondition("createdStamp",
										EntityOperator.LESS_THAN_EQUAL_TO,
										timeEnd)), EntityOperator.AND);
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<GenericValue> orderIdList = delegator.findList(
						"OrderRole", ecl, UtilMisc.toSet("orderId"), null,
						findOptions, false);
				List<String> orderIdListStr = FastList.newInstance();
				for (GenericValue orderId : orderIdList) {
					orderIdListStr.add(orderId.getString("orderId"));
				}

				List<GenericValue> quantityProductOrderedList = delegator
						.findList("OrderItemAndProduct", EntityCondition
								.makeCondition("orderId", EntityOperator.IN,
										orderIdListStr), UtilMisc.toSet(
								"orderId", "orderItemSeqId", "productId",
								"quantity"), null, findOptions, false);
				Map<String, BigDecimal> totalQtyProductMap = FastMap
						.newInstance();
				for (GenericValue qtyProductOrder : quantityProductOrderedList) {
					String productId = qtyProductOrder.getString("productId");
					if (totalQtyProductMap.containsKey(productId)) {
						totalQtyProductMap.get(productId).add(
								qtyProductOrder.getBigDecimal("quantity"));
					} else {
						totalQtyProductMap.put(productId,
								qtyProductOrder.getBigDecimal("quantity"));
					}
				}
				if (orderIdList.size() > 0)
					retMap.put("topProductQty", totalQtyProductMap);
				request.setAttribute("cusQtyProductInfo", retMap);
			} catch (Exception e) {
			}
		}
		return "success";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getCusOrderInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customerId = request.getParameter("customerId");
		String time = request.getParameter("time");
		Map<String, Object> retMap = FastMap.newInstance();
		DecimalFormat format = new DecimalFormat("###,###.###");
		if (customerId != null) {
			try {
				Timestamp timeStart = UtilDateTime.getMonthStart(UtilDateTime
						.nowTimestamp());
				Timestamp timeEnd = UtilDateTime.getMonthEnd(
						UtilDateTime.nowTimestamp(), TimeZone.getDefault(),
						Locale.getDefault());
				if (time.equalsIgnoreCase("thisWeek")) {
					timeStart = UtilDateTime.getWeekStart(UtilDateTime
							.nowTimestamp());
					timeEnd = UtilDateTime.getWeekEnd(UtilDateTime
							.nowTimestamp());
				} else if (time.equalsIgnoreCase("lastMonth")) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MONTH, -1);
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					timeStart = UtilDateTime.getMonthStart(timestamp);
					timeEnd = UtilDateTime.getMonthEnd(timestamp,
							TimeZone.getDefault(), Locale.getDefault());
				}
				EntityConditionList<EntityExpr> ecl = EntityCondition
						.makeCondition(UtilMisc.toList(EntityCondition
								.makeCondition("partyId",
										EntityOperator.EQUALS, customerId),
								EntityCondition.makeCondition("createdStamp",
										EntityOperator.GREATER_THAN_EQUAL_TO,
										timeStart),
								EntityCondition.makeCondition("createdStamp",
										EntityOperator.LESS_THAN_EQUAL_TO,
										timeEnd)), EntityOperator.AND);
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<GenericValue> orderIdList = delegator.findList(
						"OrderRole", ecl, UtilMisc.toSet("orderId"), null,
						findOptions, false);
				List<String> orderIdListStr = FastList.newInstance();
				for (GenericValue orderId : orderIdList) {
					orderIdListStr.add(orderId.getString("orderId"));
				}
				List<GenericValue> cusOrderInfoList = delegator.findList(
						"OrderHeader", EntityCondition.makeCondition("orderId",
								EntityOperator.IN, orderIdListStr), UtilMisc
								.toSet("orderId", "grandTotal"), null,
						findOptions, false);
				BigDecimal totalAmount = BigDecimal.ZERO;
				for (GenericValue cusOrderInfo : cusOrderInfoList) {
					totalAmount = totalAmount.add(cusOrderInfo
							.getBigDecimal("grandTotal"));
				}
				retMap.put("totalAmountOrders", format.format(totalAmount));
				retMap.put("totalOfNumberOrders", orderIdList.size());
				request.setAttribute("cusOrderInfo", retMap);
			} catch (Exception e) {
			}
		}
		return "success";
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updatePersonSalesman(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ctxMap = FastMap.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			ctxMap.put("firstName", context.get("firstName"));
			ctxMap.put("lastName", context.get("lastName"));
			ctxMap.put("partyId",
					((GenericValue) context.get("userLogin")).get("partyId"));
			ctxMap.put("userLogin", systemUserLogin);
			if (context.get("middleName") != null) {
				ctxMap.put("middleName", context.get("middleName"));
			}
			retMap = dispatcher.runSync("updatePerson", ctxMap);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<?, ?> tempMap = UtilMisc.toMap("updateMessage", retMap);
		return (Map<String, Object>) tempMap;
	}
	
	/*
	 * method get Data Detail info of SalesMan
	 * @param DispatchContext,Map<?,?>
	 * 
	 * */
	public static Map<String,Object> getInfoSalesMan(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = dpct.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Map<String,Object> data  = FastMap.newInstance();
		List<GenericValue> listData = FastList.newInstance();
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		try {
			EntityCondition cond1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId));
			EntityCondition cond2 = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp));
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(cond1);
			listCond.add(cond2);
			listData = delegator.findList("DetailInfoSalesMan",EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, UtilMisc.toList("-fromDate"), options, false);
			if(UtilValidate.isNotEmpty(listData))
			{//
				List<GenericValue> result = FastList.newInstance();
				result.add(listData.get(0));
				data.put("listInfo", result);
			}else {
				return ServiceUtil.returnError("empty");
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}catch (Exception e) {
			Debug.log("can't get Data from entity " + e.getMessage(),module);
			e.printStackTrace();
		}
		return data;
	}
	
	public static Map<String,Object> getAllProduct(DispatchContext dpct,Map<?,?> context) throws Exception{
		Delegator delegator = dpct.getDelegator();
		org.ofbiz.entity.condition.EntityCondition cond = (org.ofbiz.entity.condition.EntityCondition) context.get("inputParams");
		EntityFindOptions options = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_SENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, true);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			EntityListIterator returnValue = delegator.find("ProductCategoryMemberAndPrice",
					cond, null, UtilMisc.toSet("productId",
							"productProductName", "pricePrice", "productCategoryId"), UtilMisc.toList("productId", "productProductName"),
					options);
			returnMap.put("outputParams", returnValue);
		} catch (Exception e) {
			Debug.logError(e.toString(), module);
			throw e;
		}
		return returnMap;
	}
	
}

