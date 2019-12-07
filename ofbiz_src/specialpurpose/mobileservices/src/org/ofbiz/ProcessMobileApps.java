
package org.ofbiz;

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.customer.Customer;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.mobilefeature.SalesmanApps;
import org.ofbiz.mobileservices.OrderEvents;
import org.ofbiz.mobileservices.ProductServices;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.salesman.Salesman;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.shoppingcart.CheckOutWithoutAccTransHelper;

public class ProcessMobileApps extends SalesmanApps implements Mobile{
	public static final String resource = "MobileServicesErrorUiLabels";
	Salesman salesman = new Salesman();
	Customer customer = new Customer();
	public String result = "";

	@Override
	public void createOrders(HttpServletRequest request, HttpServletResponse response, String module) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Boolean exception = Boolean.parseBoolean(request.getParameter("exception"));
		String productStoreId = request.getParameter("productStoreId");
		if (UtilValidate.isEmpty(productStoreId)) {
			productStoreId = (String) request.getAttribute("productStoreId");
		}
		Double latitude = null;
		Double longitude = null;
		try {
			if(!request.getParameter("latitude").equals("null")){
				String latOrder = new String(request.getParameter("latitude"));
				latitude = Double.parseDouble(latOrder);
			}
			if(!request.getParameter("longitude").equals("null")){
				String longOrder = new String(request.getParameter("longitude"));
				longitude = Double.parseDouble(longOrder);
			}
		} catch (Exception e) {
			Debug.logWarning("Error when convert latitude or longitude to double type" + e.getMessage(), module);
		}
		HttpSession session = request.getSession();
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		if(cart == null){
			String resultCreateCart = OrderEvents.createCart(request, response);
			if ("error".equals(resultCreateCart)) return;

			cart = (ShoppingCart) session.getAttribute("shoppingCart");
		}
		if(exception){
//			try {
//				List<String> routes = MobileUtils.getRoadTodayWithPartyId(delegator, userLogin.getString("partyId"));
//				String routeId = request.getParameter("routeId");
//				if(!routes.contains(routeId)){
					cart.setOrderAttribute("EXCEPTION", "Y");
//				}
//			} catch (GenericEntityException e) {
//				Debug.log(e.getMessage());
//			}
		}
		CheckOutWithoutAccTransHelper checkoutWithoutAcc = new CheckOutWithoutAccTransHelper(dispatcher, delegator, cart);

		/** add cash on delivery to payment of order */
		Map<String, Map<String, Object>> selectedPaymentMethods = new HashMap<String, Map<String, Object>>();
		Map<String, Object> paymentMethodInfo = FastMap.newInstance();
		paymentMethodInfo.put("amount", null);
		selectedPaymentMethods.put("EXT_COD", paymentMethodInfo);
		
		Map<String, Object> callResult = checkoutWithoutAcc.setCheckOutPayment(selectedPaymentMethods, null, null);
		if (ServiceUtil.isError(callResult)) {
			request.setAttribute(Mobile.ERROR_MESSAGE, ServiceUtil.getErrorMessage(callResult));
		}
		
		/** end add cash on delivery to payment */
		try {
			String orderId = null;
			Map<String, Object> orderCreateResult = checkoutWithoutAcc.createOrder(userLogin);
	        if (orderCreateResult != null) {
	            ServiceUtil.getMessages(request, orderCreateResult, null);
	            if (ServiceUtil.isError(orderCreateResult)) {
	                // messages already setup with the getMessages call, just return the error response code
	            	request.setAttribute(Mobile.ERROR_MESSAGE, ServiceUtil.getErrorMessage(orderCreateResult));
	                result = Mobile.RESPONSE_ERROR;
	            	return;
	            }
	            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
	                // set the orderId for use by chained events
	                orderId = cart.getOrderId();
	            }
	        }
			if (orderId == null) {
				orderId = (String) orderCreateResult.get("orderId");
				if (UtilValidate.isEmpty(orderId)) {
					request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "MobileServiceCantGetOrderId", locale));
					result = Mobile.RESPONSE_ERROR;
	            	return;
				}
			}
			//Create order's position created
			if(latitude != null && longitude != null){
				try {
					String idGeoPoint = MobileUtils.createGeoPoint(delegator, latitude, longitude);
					MobileUtils.createOrderGeoPoint(delegator, orderId, idGeoPoint);
				} catch (Exception e) {
					Debug.log(e.getMessage());
				}
			}
			request.setAttribute("orderId", orderId);
			Map<String, Object> notin = FastMap.newInstance();
			notin.put("userLogin", userLogin);
			notin.put("orderId", orderId);
			dispatcher.runSync("sendNotifyWhenCreateOrder", notin);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
	}
	public List<Map<String, Object>> getPromotions(ShoppingCart cart){
		Delegator delegator = cart.getDelegator();
		List<Map<String, Object>> ListPromotions = FastList.newInstance();
		List<ShoppingCartItem> items = cart.items();
		for(ShoppingCartItem item : items){
			Map<String,Object> itemTmp = FastMap.newInstance();
			String productId = item.getProductId();
			if(item.getIsPromo()){
				itemTmp.put("productPrice", item.getBasePrice());
				itemTmp.put("productName", item.getName());
				itemTmp.put("productId", productId);
				Map<String, Object> image = ProductServices.getProductImage(delegator, productId);
				itemTmp.putAll(image);
				itemTmp.put("productQuantity", item.getQuantity());
				ListPromotions.add(itemTmp);
			}
		}
		return ListPromotions;
	}
	@Override
	public void getListPromotions() {
		
	}

	@Override
	public void getListProducts() {
		
	}

	@Override
	public void statistic() {
		
	}

	@Override
	public void leave() {
		
	}

	@Override
	public void synchronize() {
		
	}

	@Override
	public List<Map<String, Customer>> getListCustomer() {
		return null;
	}

	@Override
	public Map<String,Object> getListRouteAndSalesMan(DispatchContext dpct,
			Map<String, ? extends Object> context) throws ParseException {
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listSalesMan  = null;
		EntityListIterator listCustomers  = null;
		EntityListIterator listDistributionCustomers  = null;
		List<GenericValue> listRoute = FastList.newInstance();
		EntityListIterator listDistribution = null;
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		Map<String,Object> obj  = FastMap.newInstance();
		Set<String> fieldsSelect = UtilMisc.toSet("routeId","description");
		try {
			options.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
			this.pagenum = Integer.parseInt(context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
			this.pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize"): "20");
			listCustomers = delegator.find("PartyRoleAndPartyDetail",EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId","DELYS_CUSTOMER_GT")), null, null, UtilMisc.toList("partyId DESC"), options);
			listSalesMan  = delegator.find("PartyPersonPartyRole", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId","DELYS_SALESMAN_GT")), null, null, UtilMisc.toList("partyId DESC"), options);
			listRoute = delegator.findList("RouteInformation", null, fieldsSelect, null, options, false);
			EntityCondition cond1 = EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo","DELYS_CUSTOMER_GT","roleTypeIdFrom","DELYS_ROUTE"));
			EntityCondition cond2 = EntityCondition.makeCondition("partyIdFrom",EntityJoinOperator.NOT_EQUAL,"ROUTE1");
			EntityCondition cond3 = EntityCondition.makeCondition("partyIdFrom",EntityJoinOperator.NOT_EQUAL,"ROUTE2");
			EntityCondition cond4 = EntityCondition.makeCondition("thruDate",EntityJoinOperator.EQUALS,null);
			List<EntityCondition> list = FastList.newInstance();
			list.add(cond1);
			list.add(cond2);
			list.add(cond3);
			list.add(cond4);
			listDistributionCustomers = delegator.find("PartyRelationship",EntityCondition.makeCondition(list,EntityJoinOperator.AND), null, null, UtilMisc.toList("partyIdFrom DESC"), options);
			EntityCondition cond5 = EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom","DELYS_SALESMAN_GT","roleTypeIdTo","DELYS_ROUTE"));
			EntityCondition cond6 = EntityCondition.makeCondition("partyIdFrom",EntityJoinOperator.NOT_EQUAL,"ROUTE1");
			EntityCondition cond7 = EntityCondition.makeCondition("partyIdFrom",EntityJoinOperator.NOT_EQUAL,"ROUTE2");
			EntityCondition cond8 = EntityCondition.makeCondition("thruDate",EntityJoinOperator.EQUALS,null);
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(cond5);
			listCond.add(cond6);
			listCond.add(cond7);
			listCond.add(cond8);
			listDistribution = delegator.find("PartyRelationship",EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null, UtilMisc.toList("partyIdFrom DESC"), options);
			if(UtilValidate.isNotEmpty(listRoute)) {
				obj.put("listRoute", listRoute);
			}
			
			if(UtilValidate.isNotEmpty(listSalesMan)){
				obj.put("listSalesMan", listSalesMan.getPartialList(this.pagenum, this.pagesize));
			}
			
			if(UtilValidate.isNotEmpty(listDistribution)){
				obj.put("listDistribution", listDistribution.getPartialList(this.pagenum, this.pagesize));
			}
			
			if(UtilValidate.isNotEmpty(listCustomers)){
				obj.put("listCustomer", listCustomers.getPartialList(this.pagenum, this.pagesize));
			}
			
			if(UtilValidate.isNotEmpty(listDistributionCustomers)){
				obj.put("listDistributionCustomers", listDistributionCustomers.getPartialList(this.pagenum, this.pagesize));
			}
			
		} catch	(GenericEntityException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				listSalesMan.close();
				listCustomers.close();
				listDistribution.close();
				listDistributionCustomers.close();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

}