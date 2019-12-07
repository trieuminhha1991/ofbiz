package com.olbius.basepos.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.olbius.basepos.session.WebPosSession;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SalesHistory {
	public static String resource_error = "BasePosErrorUiLabels";
	
	@SuppressWarnings("unchecked")
	public static String getOverViewSalesHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		String orderId = request.getParameter("orderId");
		String posTerminalLogId = request.getParameter("posTerminalLogId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if(UtilValidate.isNotEmpty(orderId)){
			Map<String, String> mapCondition = FastMap.newInstance();
			mapCondition.put("orderId", orderId);
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if (UtilValidate.isNotEmpty(orderHeader)){
				String orderTypeId = orderHeader.getString("orderTypeId");
				if (orderTypeId.equals("SALES_ORDER")){
					mapCondition.put("roleTypeId", "PLACING_CUSTOMER");
				} 
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(mapCondition);
			List<GenericValue> listOrderHeaderAndRole = FastList.newInstance();
			try {
				listOrderHeaderAndRole = delegator.findList("OrderHeaderAndRoles", mainCondition, null, null, null, false);
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			String customerId = null;
			GenericValue customer = null;
			if(UtilValidate.isNotEmpty(listOrderHeaderAndRole)){
				GenericValue orderHeaderAndRole = listOrderHeaderAndRole.get(0);
				if(UtilValidate.isNotEmpty(orderHeaderAndRole)){
					customerId = orderHeaderAndRole.getString("partyId");
					if(UtilValidate.isNotEmpty(customerId)){
						try {
							customer = delegator.findOne("Person", UtilMisc.toMap("partyId", customerId), false);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(customer)) {
				if(!customerId.equalsIgnoreCase("_NA_")){
					String customerName = "";
					if(UtilValidate.isNotEmpty(customer.getString("lastName"))){
						customerName = customer.getString("lastName");
					}
					if(UtilValidate.isNotEmpty(customer.getString("middleName"))){
						customerName = customerName + " " + customer.getString("middleName");
					}
					if(UtilValidate.isNotEmpty(customer.getString("firstName"))){
						customerName = customerName + " " + customer.getString("firstName");
					}
					request.setAttribute("customerName", customerName);
					//get address
					Map<String, Object> addressContext = FastMap.newInstance();
					addressContext.put("partyId", customerId);
					addressContext.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
					addressContext.put("userLogin", userLogin);
					Map<String, Object> addressMap  = FastMap.newInstance();
					try {
						addressMap = dispatcher.runSync("getPartyPostalAddressAddState", addressContext);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
					if(ServiceUtil.isError(addressMap)){
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAddressForCustomer", UtilMisc.toMap("partyId", customerId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}else{
						String address = (String)addressMap.get("address1");
						if(UtilValidate.isNotEmpty(addressMap.get("city"))){
							address = address + " " + (String) addressMap.get("city");
						}
						request.setAttribute("address", address);
					}
					//get telecomnumber
					Map<String, Object> telecomnumberContext = FastMap.newInstance();
					telecomnumberContext.put("partyId", customerId);
					telecomnumberContext.put("userLogin", userLogin);
					Map<String, Object> telecomnumberMap  = FastMap.newInstance();
					try {
						telecomnumberMap = dispatcher.runSync("getPartyTelephone", telecomnumberContext);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
					if(ServiceUtil.isError(telecomnumberMap)){
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAddressForCustomer", UtilMisc.toMap("partyId", customerId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}else{
						String telecomNumber = (String)telecomnumberMap.get("contactNumber");
						request.setAttribute("telecomnumber", telecomNumber);
					}
				}
			}else{
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotFindCustomerForOrder", UtilMisc.toMap("orderId", orderId), locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			//get information of order that related to money
			getOverViewSalesOrder(request, orderId);
			GenericValue posLog = delegator.findOne("PosTerminalLog", UtilMisc.toMap("posTerminalLogId", posTerminalLogId), false);
			if(posLog.getString("statusId") != null && posLog.getString("statusId").equals("POSTX_RETURNED")){
				String message = UtilProperties.getMessage("BasePosUiLabels", "BPOSReturnedOrder", locale);
				request.setAttribute("orderReturned", message);
			}else{
				//check the order whether is returned or no
				Map<String, Object> returnRes = null;
				Map<GenericValue, Map<String, Object>> returnableItems = null;
				//return items
				try {
					if (UtilValidate.isNotEmpty(orderHeader)){
						String orderTypeId = orderHeader.getString("orderTypeId");
						if (orderTypeId.equals("SALES_ORDER")){
							returnRes = dispatcher.runSync("getReturnableItems", UtilMisc.toMap("orderId", orderId));
						}
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
				if(UtilValidate.isNotEmpty(returnRes)){
					returnableItems  = (Map<GenericValue, Map<String, Object>>) returnRes.get("returnableItems");	
				}
				if(UtilValidate.isEmpty(returnableItems)){
					String message = UtilProperties.getMessage("BasePosUiLabels", "BPOSOrderIsReturned", locale);
					request.setAttribute("orderReturned", message);
				}
			}
		}else{
			String returnId = request.getParameter("returnId");
			if(returnId != null && !returnId.isEmpty()){
				Map<String, String> mapCondition = FastMap.newInstance();
				mapCondition.put("returnId", returnId);
				GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
				if (UtilValidate.isNotEmpty(returnHeader)){
					String fromParty = returnHeader.getString("fromPartyId");
					if(fromParty != null && !fromParty.isEmpty() && !fromParty.equalsIgnoreCase("_NA_")){
						GenericValue customer = null;
						try {
							customer = delegator.findOne("Person", UtilMisc.toMap("partyId", fromParty), false);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(customer)) {
							if(!fromParty.equalsIgnoreCase("_NA_")){
								String customerName = customer.getString("lastName");
								if(UtilValidate.isNotEmpty(customer.getString("middleName"))){
									customerName = customerName + " " + customer.getString("middleName");
								}
								if(UtilValidate.isNotEmpty(customer.getString("firstName"))){
									customerName = customerName + " " + customer.getString("firstName");
								}
								request.setAttribute("customerName", customerName);
								//get address
								Map<String, Object> addressContext = FastMap.newInstance();
								addressContext.put("partyId", fromParty);
								addressContext.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
								addressContext.put("userLogin", userLogin);
								Map<String, Object> addressMap  = FastMap.newInstance();
								try {
									addressMap = dispatcher.runSync("getPartyPostalAddressAddState", addressContext);
								} catch (GenericServiceException e) {
									e.printStackTrace();
								}
								if(ServiceUtil.isError(addressMap)){
									String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAddressForCustomer", UtilMisc.toMap("partyId", fromParty), locale);
									request.setAttribute("_ERROR_MESSAGE_", errorMessage);
									return "error";
								}else{
									String address = (String)addressMap.get("address1");
									if(UtilValidate.isNotEmpty(addressMap.get("city"))){
										address = address + " " + (String) addressMap.get("city");
									}
									request.setAttribute("address", address);
								}
								//get telecomnumber
								Map<String, Object> telecomnumberContext = FastMap.newInstance();
								telecomnumberContext.put("partyId", fromParty);
								telecomnumberContext.put("userLogin", userLogin);
								Map<String, Object> telecomnumberMap  = FastMap.newInstance();
								try {
									telecomnumberMap = dispatcher.runSync("getPartyTelephone", telecomnumberContext);
								} catch (GenericServiceException e) {
									e.printStackTrace();
								}
								if(ServiceUtil.isError(telecomnumberMap)){
									String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAddressForCustomer", UtilMisc.toMap("partyId", fromParty), locale);
									request.setAttribute("_ERROR_MESSAGE_", errorMessage);
									return "error";
								}else{
									String telecomNumber = (String)telecomnumberMap.get("contactNumber");
									request.setAttribute("telecomnumber", telecomNumber);
								}
							}
						}
					}
					getOverViewReturnOrder(request, returnId);
					String message = UtilProperties.getMessage("BasePosUiLabels", "BPOSReturnedOrder", locale);
					request.setAttribute("orderReturned", message);
				}else{
					// FIXME Return warning message
				}
			}else{
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotHaveOrder", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		}
			
		return "success";
	}
	
	public static void getOverViewSalesOrder(HttpServletRequest request, String orderId) throws GenericEntityException{
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue orderHeader = null;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(orderHeader)){
			OrderReadHelper orderReaderHelper = new OrderReadHelper(orderHeader);
			
			String showPricesWithVatTax = "Y";
			GenericValue productStore = orderReaderHelper.getProductStore();
			if (UtilValidate.isNotEmpty(productStore)){
				showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
			}
			
			String orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT_NOTAX";
			if (showPricesWithVatTax.equals("N")){
				orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT";
			} 
			
			Timestamp orderDate = orderHeader.getTimestamp("orderDate");
			Date orderDateTmp = new Date(orderDate.getTime());
			SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			String orderDateValue = simpleFormat.format(orderDateTmp);
			request.setAttribute("orderDate", orderDateValue);
			request.setAttribute("currencyUom", orderHeader.getString("currencyUom"));
			List<GenericValue> orderHeaderAdjustments = orderHeader.getRelated("OrderAdjustment", null, null, false);
			BigDecimal amountDiscount = BigDecimal.ZERO;
			BigDecimal amountSalesTax = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(orderHeaderAdjustments) && orderHeaderAdjustments.size() > 0){
				for (GenericValue itemAdj : orderHeaderAdjustments){
					if (itemAdj.getString("orderAdjustmentTypeId").equals("SALES_TAX")){
						amountSalesTax = amountSalesTax.add(itemAdj.getBigDecimal("amount"));
					} 
					if ((itemAdj.getString("orderAdjustmentTypeId").equals(orderAdjustmentTypeId)) && (itemAdj.getString("orderItemSeqId").equals("_NA_"))){
						amountDiscount = amountDiscount.add(itemAdj.getBigDecimal("amount"));
					}
				}
				if (!amountDiscount.equals(BigDecimal.ZERO)){
					amountDiscount = amountDiscount.negate();
				}
				request.setAttribute("orderDiscount", amountDiscount.toString());
				request.setAttribute("orderSalesTax", amountSalesTax.toString());
			}
			BigDecimal returnedTotal = orderReaderHelper.getOrderReturnedRefundTotalBd();
			if(UtilValidate.isNotEmpty(returnedTotal)){
				request.setAttribute("returnedTotal", returnedTotal.toString());
			}
			List<GenericValue> listOrderItems = orderReaderHelper.getOrderItems();
			List<GenericValue> orderAdjustments = orderReaderHelper.getAdjustments();
			
			BigDecimal grandTotal = OrderReadHelper.getOrderGrandTotal(listOrderItems, orderAdjustments);
			if(UtilValidate.isNotEmpty(grandTotal)){
				GenericValue posLog = delegator.findOne("PosTerminalLog", UtilMisc.toMap("posTerminalLogId", request.getParameter("posTerminalLogId")), false);
				if(posLog.getString("statusId") != null && posLog.getString("statusId").equals("POSTX_SOLD") && posLog.getString("returnId") != null){
					GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", posLog.getString("returnId")), false);
					request.setAttribute("grandTotal", grandTotal.subtract(returnHeader.getBigDecimal("grandTotal")).toString());
				}else{
					request.setAttribute("grandTotal", grandTotal.toString());
				}
			}
			
			BigDecimal orderSubTotal = orderReaderHelper.getOrderItemsSubTotal();
			if (showPricesWithVatTax.equals("Y")){
				orderSubTotal = grandTotal.add(amountDiscount);
			}
			
			request.setAttribute("orderTotal", orderSubTotal.toString());
		}
	}
	
	public static void getOverViewReturnOrder(HttpServletRequest request, String returnId) throws GenericEntityException{
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue returnHeader = null;
		try {
			returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(returnHeader)){
			EntityCondition mainCondition1 = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
			EntityCondition mainCondition2 = EntityCondition.makeCondition("returnAdjustmentTypeId", EntityOperator.NOT_EQUAL, "RET_SALES_TAX_ADJ");
			List<GenericValue> returnHeaderAdjustments = delegator.findList("ReturnAdjustment", EntityCondition.makeCondition(EntityJoinOperator.AND, mainCondition1, mainCondition2), 
					UtilMisc.toSet("amount"), null, null, false);
			BigDecimal amountDiscount = BigDecimal.ZERO;
			for (GenericValue genericValue : returnHeaderAdjustments) {
				amountDiscount = amountDiscount.add(genericValue.getBigDecimal("amount"));
			}
			request.setAttribute("returnDiscount", amountDiscount.toString());
			if(amountDiscount.signum() > 0){
				request.setAttribute("grandTotal", returnHeader.getBigDecimal("grandTotal").subtract(amountDiscount));
			}
			request.setAttribute("returnDate", returnHeader.get("entryDate"));
			request.setAttribute("currencyUom", returnHeader.getString("currencyUomId"));
			request.setAttribute("returnTotal", returnHeader.getBigDecimal("grandTotal"));
		}
	}
	
	public static Map<String, Object> getOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String , Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listOrderItems = FastList.newInstance();
		String orderId = (String) context.get("orderId");
		String posTerminalLogId = (String) context.get("posTerminalLogId");
		GenericValue posLog = delegator.findOne("PosTerminalLog", UtilMisc.toMap("posTerminalLogId", posTerminalLogId), false);
		if(orderId != null){
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			OrderReadHelper orh = new OrderReadHelper(orderHeader);
			String showPricesWithVatTax = "Y";
			GenericValue productStore = orh.getProductStore();
			if (UtilValidate.isNotEmpty(productStore)){
				showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
			}
			
			if(posLog.getString("statusId") != null && posLog.getString("statusId").equals("POSTX_RETURNED")){
				List<GenericValue> returnItems = FastList.newInstance();
				EntityCondition mainCond = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, posLog.get("returnId"));
				returnItems = delegator.findList("ReturnItem", mainCond, null, null, null, false);
				for (GenericValue returnItem : returnItems) {
					Map<String, Object> returnItemMap = FastMap.newInstance();
					String productId = returnItem.getString("productId");
					returnItemMap.put("currencyUom", orderHeader.getString("currencyUom"));
					returnItemMap.put("productId", productId);
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					String productCode = "";
					String productName = "";
					if (UtilValidate.isNotEmpty(product)){
						productCode = product.getString("productCode");
						productName = product.getString("productName");
					}
					returnItemMap.put("productCode", productCode);
					returnItemMap.put("productName", productName);
					String quantityUomId = returnItem.getString("quantityUomId");
					returnItemMap.put("quantityUomId", quantityUomId);
					returnItemMap.put("itemQuantity", returnItem.getBigDecimal("returnQuantity").negate());
					
					// get adjustment 
					List<GenericValue> returnAdjustment = returnItem.getRelated("ReturnAdjustment", UtilMisc.toMap("returnId", posLog.get("returnId"), "returnItemSeqId", returnItem.getString("returnItemSeqId")), null, false);
					BigDecimal itemTotal = returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
					BigDecimal adjustment = new BigDecimal(0);
					BigDecimal itemPrice = returnItem.getBigDecimal("returnPrice");
					for (GenericValue genericValue : returnAdjustment) {
						if(!genericValue.getString("returnAdjustmentTypeId").equals("RET_SALES_TAX_ADJ")){
							adjustment = adjustment.add(genericValue.getBigDecimal("amount"));
						}else{
							itemPrice = itemPrice.add(genericValue.getBigDecimal("amount"));
							itemTotal = itemTotal.add(genericValue.getBigDecimal("amount"));
						}
					}
					returnItemMap.put("itemPrice", itemPrice);
					returnItemMap.put("itemAdjustment", adjustment.negate());
					returnItemMap.put("itemTotal",itemTotal.add(adjustment).negate());
					listOrderItems.add(returnItemMap);
				}
			}else{
				List<GenericValue> orderItems = FastList.newInstance();
				// Get return items
				String returnId = posLog.getString("returnId");
				if(returnId != null && !returnId.isEmpty()){
					List<GenericValue> returnItems = FastList.newInstance();
					EntityCondition mainCond = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
					returnItems = delegator.findList("ReturnItem", mainCond, null, null, null, false);
					for (GenericValue returnItem : returnItems) {
						Map<String, Object> returnItemMap = FastMap.newInstance();
						String productId = returnItem.getString("productId");
						returnItemMap.put("currencyUom", orderHeader.getString("currencyUom"));
						returnItemMap.put("productId", productId);
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						String productCode = "";
						String productName = "";
						if (UtilValidate.isNotEmpty(product)){
							productCode = product.getString("productCode");
							productName = product.getString("productName");
						}
						returnItemMap.put("productCode", productCode);
						returnItemMap.put("productName", productName);
						String quantityUomId = returnItem.getString("quantityUomId");
						returnItemMap.put("quantityUomId", quantityUomId);
						returnItemMap.put("itemQuantity", returnItem.getBigDecimal("returnQuantity").negate());
						
						// get adjustment 
						List<GenericValue> returnAdjustment = returnItem.getRelated("ReturnAdjustment", UtilMisc.toMap("returnId", posLog.get("returnId"), "returnItemSeqId", returnItem.getString("returnItemSeqId")), null, false);
						BigDecimal itemTotal = returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
						BigDecimal adjustment = new BigDecimal(0);
						BigDecimal itemPrice = returnItem.getBigDecimal("returnPrice");
						for (GenericValue genericValue : returnAdjustment) {
							if(!genericValue.getString("returnAdjustmentTypeId").equals("RET_SALES_TAX_ADJ")){
								adjustment = adjustment.add(genericValue.getBigDecimal("amount"));
							}else{
								itemPrice = itemPrice.add(genericValue.getBigDecimal("amount"));
								itemTotal = itemTotal.add(genericValue.getBigDecimal("amount"));
							}
						}
						returnItemMap.put("itemPrice", itemPrice);
						returnItemMap.put("itemTotal",itemTotal.add(adjustment).negate());
						returnItemMap.put("itemAdjustment", adjustment.negate());
						listOrderItems.add(returnItemMap);
					}
				}
				
				EntityCondition mainCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
				orderItems = delegator.findList("OrderItem", mainCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(orderItems)){
					for (GenericValue orderItem : orderItems) {
						Map<String, Object> orderItemMap = FastMap.newInstance();
						String productId = orderItem.getString("productId");
						orderItemMap.put("currencyUom", orderHeader.getString("currencyUom"));
						orderItemMap.put("productId", productId);
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						String productCode = "";
						String internalName = "";
						if (UtilValidate.isNotEmpty(product)){
							productCode = product.getString("productCode");
							internalName = product.getString("internalName");
						}
						orderItemMap.put("productCode", productCode);
						orderItemMap.put("internalName", internalName);
						orderItemMap.put("productName", orderItem.getString("itemDescription"));
						String quantityUomId = orderItem.getString("quantityUomId");
						orderItemMap.put("quantityUomId", quantityUomId);
						
						if (showPricesWithVatTax.equals("Y")){
							if(UtilValidate.isNotEmpty(quantityUomId)){
								BigDecimal quantitySaled = orderItem.getBigDecimal("alternativeQuantity");
								orderItemMap.put("itemQuantity", quantitySaled);
							} else {
								BigDecimal quantitySaled = orderItem.getBigDecimal("quantity");
								orderItemMap.put("itemQuantity", quantitySaled);
							}
							BigDecimal priceSaled = orderItem.getBigDecimal("unitPrice");
							// get adjustment 
							List<GenericValue> orderAdjustment = orderItem.getRelated("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")), null, false);
							for (GenericValue genericValue : orderAdjustment) {
								if(genericValue.getString("orderAdjustmentTypeId").equals("SALES_TAX")){
									priceSaled = priceSaled.add(genericValue.getBigDecimal("amount"));
								}
							}
							orderItemMap.put("itemPrice", priceSaled);
						} else {
							if(UtilValidate.isNotEmpty(quantityUomId)){
								BigDecimal quantitySaled = orderItem.getBigDecimal("alternativeQuantity");
								orderItemMap.put("itemQuantity", quantitySaled);
								BigDecimal priceSaled = orderItem.getBigDecimal("alternativeUnitPrice");
								orderItemMap.put("itemPrice", priceSaled);
							} else {
								BigDecimal quantitySaled = orderItem.getBigDecimal("quantity");
								orderItemMap.put("itemQuantity", quantitySaled);
								BigDecimal priceSaled = orderItem.getBigDecimal("unitPrice");
								orderItemMap.put("itemPrice", priceSaled);
							}
						}
						
						BigDecimal itemTax = orh.getOrderItemTax(orderItem);
						BigDecimal totalItem  = orh.getOrderItemSubTotal(orderItem);
						if(UtilValidate.isNotEmpty(totalItem)){
							if (showPricesWithVatTax.equals("Y")){
								totalItem = totalItem.add(itemTax);
							}
							orderItemMap.put("itemTotal", totalItem);
						}
						BigDecimal adjustment  = orh.getOrderItemAdjustmentsTotal(orderItem);
						if(UtilValidate.isNotEmpty(adjustment)){
							orderItemMap.put("itemAdjustment", adjustment.negate());
						}
						listOrderItems.add(orderItemMap);
					}
				}
			}
		}else{
			String returnId = (String) context.get("returnId");
			List<GenericValue> returnItems = FastList.newInstance();
			EntityCondition mainCond = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
			returnItems = delegator.findList("ReturnItem", mainCond, null, null, null, false);
			for (GenericValue returnItem : returnItems) {
				Map<String, Object> returnItemMap = FastMap.newInstance();
				String productId = returnItem.getString("productId");
				returnItemMap.put("currencyUom", returnHeader.getString("currencyUomId"));
				returnItemMap.put("productId", productId);
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				String productCode = "";
				String productName = "";
				if (UtilValidate.isNotEmpty(product)){
					productCode = product.getString("productCode");
					productName = product.getString("productName");
				}
				returnItemMap.put("productCode", productCode);
				returnItemMap.put("productName", productName);
				String quantityUomId = returnItem.getString("quantityUomId");
				returnItemMap.put("quantityUomId", quantityUomId);
				returnItemMap.put("itemQuantity", returnItem.getBigDecimal("returnQuantity").negate());
				
				// get adjustment 
				List<GenericValue> returnAdjustment = returnItem.getRelated("ReturnAdjustment", UtilMisc.toMap("returnId", posLog.get("returnId"), "returnItemSeqId", returnItem.getString("returnItemSeqId")), null, false);
				BigDecimal itemTotal = returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
				BigDecimal adjustment = new BigDecimal(0);
				BigDecimal itemPrice = returnItem.getBigDecimal("returnPrice");
				for (GenericValue genericValue : returnAdjustment) {
					if(!genericValue.getString("returnAdjustmentTypeId").equals("RET_SALES_TAX_ADJ")){
						adjustment = adjustment.add(genericValue.getBigDecimal("amount"));
					}else{
						itemPrice = itemPrice.add(genericValue.getBigDecimal("amount"));
						itemTotal = itemTotal.add(genericValue.getBigDecimal("amount"));
					}
				}
				returnItemMap.put("itemPrice", itemPrice);
				returnItemMap.put("itemAdjustment", adjustment.negate());
				returnItemMap.put("itemTotal",itemTotal.add(adjustment).negate());
				listOrderItems.add(returnItemMap);
			}
		}
		result.put("listOrderItems", listOrderItems);
		return result;
	}
	
	public static String getOrderSalesHistory(HttpServletRequest request, HttpServletResponse response){
		String facilityId = request.getParameter("facilityId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Map<String, String> mapCondition = FastMap.newInstance();
		mapCondition.put("roleTypeId", "PLACING_CUSTOMER");
		mapCondition.put("statusId", "ORDER_COMPLETED");
		mapCondition.put("orderTypeId", "SALES_ORDER");
		mapCondition.put("salesChannelEnumId", "POS_SALES_CHANNEL");
		List<Map<String, Object>> listOrderSales = FastList.newInstance();
		List<GenericValue>listOrderSalesTmp = FastList.newInstance();
		if(UtilValidate.isNotEmpty(facilityId)){
			mapCondition.put("originFacilityId", facilityId);
		}
		EntityCondition mainCondition = EntityCondition.makeCondition(mapCondition);
		try {
			listOrderSalesTmp = delegator.findList("OrderHeaderAndRoleAndPerson", mainCondition, null, UtilMisc.toList("orderDate DESC"), null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(listOrderSalesTmp)){
			for (GenericValue order : listOrderSalesTmp) {
				Map<String, Object> mapOrder = FastMap.newInstance();
				mapOrder.put("orderId", order.getString("orderId"));
				Timestamp orderDatetmp = order.getTimestamp("orderDate");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String orderDate = simpleDateFormat.format(orderDatetmp);
				mapOrder.put("orderDate", orderDate);
			
				BigDecimal grandTotal = order.getBigDecimal("grandTotal");
				mapOrder.put("grandTotal", grandTotal.toString());
				mapOrder.put("orderTypeId", order.getString("orderTypeId"));
				if(!order.getString("partyId").equalsIgnoreCase("_NA_")){
					mapOrder.put("partyId", order.getString("partyId"));
				}
				mapOrder.put("currencyUom", order.getString("currencyUom"));
				
				if(UtilValidate.isNotEmpty(order.getString("firstName"))){
					mapOrder.put("firstName", order.getString("firstName"));
				}
				if(UtilValidate.isNotEmpty(order.getString("lastName"))){
					mapOrder.put("lastName", order.getString("lastName"));
				}
				if(UtilValidate.isNotEmpty(order.getString("middleName"))){
					mapOrder.put("middleName", order.getString("middleName"));
				}
				
				listOrderSales.add(mapOrder);
			}
		}
		if(UtilValidate.isNotEmpty(listOrderSales)){
			request.setAttribute("listOrderSales", listOrderSales);
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String getListHoldCart(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<Map<String, Object>> listHoldCarts = FastList.newInstance();
		List<Map<String, Object>> listCartHold = (List<Map<String, Object>>) session.getAttribute("webPosSessionHolderList");
    	
    	if(UtilValidate.isNotEmpty(listCartHold)){
    		for (int i=0; i < listCartHold.size(); i++){
    			Map<String, Object> itemHoldCart = new HashMap<String, Object>();
    			WebPosSession webposSession = (WebPosSession)listCartHold.get(i);
    			ShoppingCart cart = webposSession.getCart();
    			
    			String productStoreId = cart.getProductStoreId();
	    		GenericValue productStore = null;
	    		String showPricesWithVatTax = "Y";
				try {
					productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				} catch (GenericEntityException e2) {
					e2.printStackTrace();
				}
				
				if (UtilValidate.isNotEmpty(productStore)){
					showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
				}
    			
    			String partyId = cart.getBillToCustomerPartyId();
    			String firstName = " ";
    			String middleName = " ";
    			String lastName = " ";
    			String address1 = " ";
    			String city = " ";
    			String phone = " ";
    			String billingContactMechId = cart.getContactMech("PRIMARY_LOCATION");
    			if (UtilValidate.isNotEmpty(billingContactMechId)){
    				GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", billingContactMechId), false);
    				if (UtilValidate.isNotEmpty(contactMech)){
    					GenericValue customerPostalAddress = contactMech.getRelatedOne("PostalAddress", false);
    					if (UtilValidate.isNotEmpty(customerPostalAddress)){
    						address1 = customerPostalAddress.getString("address1");
    						city = customerPostalAddress.getString("city");
    					}
    				}
    			}
    			List<EntityCondition> phoneMobileConditions = FastList.newInstance();
    			phoneMobileConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    			phoneMobileConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_MOBILE"));
    			phoneMobileConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
    			phoneMobileConditions.add(EntityUtil.getFilterByDateExpr());
    			List<GenericValue> partyContactMechPurposeForMobilePhone = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(phoneMobileConditions, EntityOperator.AND), null, UtilMisc.toList("fromDate DESC"), null, false);
    			if(UtilValidate.isNotEmpty(partyContactMechPurposeForMobilePhone)){
    				String contactMechId = partyContactMechPurposeForMobilePhone.get(0).getString("contactMechId");
    				GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
    				if (UtilValidate.isNotEmpty(telecomNumber)){
    					phone = telecomNumber.getString("contactNumber");
    				}
    			}
    			GenericValue userLoginPos = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")), false);
    			if (UtilValidate.isNotEmpty(partyId)){
    				if (!partyId.equals(userLoginPos.getString("partyId"))){
    					GenericValue customer = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyId), false);
    					if (UtilValidate.isNotEmpty(customer)){
    						firstName = customer.getString("firstName");
    						middleName = customer.getString("middleName");
    						lastName = customer.getString("lastName");
    					}
    				}
    			}
    			List<GenericValue> discount = cart.getAdjustments();
    			BigDecimal amountDiscount = BigDecimal.ZERO;
				BigDecimal amountSalesTax = BigDecimal.ZERO;
				String orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT_NOTAX";
				if (showPricesWithVatTax.equals("N")){
					orderAdjustmentTypeId = "DISCOUNT_ADJUSTMENT";
				}
				
    			if (discount.size() > 0){
    				for (GenericValue itemAdj : discount){
    					if (itemAdj.getString("orderAdjustmentTypeId").equals("SALES_TAX")){
    						amountSalesTax = amountSalesTax.add(itemAdj.getBigDecimal("amount"));
    					} 
    					if ((itemAdj.getString("orderAdjustmentTypeId").equals(orderAdjustmentTypeId))){
    						amountDiscount = amountDiscount.add(itemAdj.getBigDecimal("amount"));
    					}
    				}
    				if (!amountDiscount.equals(BigDecimal.ZERO)){
    					amountDiscount = amountDiscount.negate();
    				}
    			}
    			
    			if (amountSalesTax.equals(BigDecimal.ZERO)){
    				amountSalesTax = cart.getTotalSalesTax();
    			}
    			
    			Timestamp createdTimetmp = cart.getCartCreatedTime();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String createdTime = simpleDateFormat.format(createdTimetmp);
				
				BigDecimal grandTotalNotAdj = cart.getSubTotalAndTax();
				BigDecimal grandTotal = cart.getGrandTotal();
				if (showPricesWithVatTax.equals("N")){
					grandTotalNotAdj = cart.getGrandTotalNotAdjusment();
				} 
    			
    			itemHoldCart.put("id", cart.getTransactionId());
    			itemHoldCart.put("createdTime", createdTime);
    			itemHoldCart.put("firstName", firstName);
    			itemHoldCart.put("middleName", middleName);
    			itemHoldCart.put("lastName", lastName);
    			itemHoldCart.put("address1", address1);
    			itemHoldCart.put("city", city);
    			itemHoldCart.put("phone", phone);
    			itemHoldCart.put("grandTotalNotAdj", grandTotalNotAdj);
    			itemHoldCart.put("grandTotal", grandTotal); 
    			itemHoldCart.put("currency", cart.getCurrency());
    			itemHoldCart.put("discountAmount", amountDiscount);
    			itemHoldCart.put("salesTaxAmount", amountSalesTax);
    			listHoldCarts.add(itemHoldCart);
    		}
		}
		if(UtilValidate.isNotEmpty(listHoldCarts)){
			request.setAttribute("listHoldCarts", listHoldCarts);
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String getHoldCartItems(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		String indextmp = request.getParameter("index");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		List<Map<String, Object>> listHoldCartItems = FastList.newInstance();
		List<Map<String, Object>> listCartHold = (List<Map<String, Object>>) session.getAttribute("webPosSessionHolderList");
		int count = 0;
    	if(UtilValidate.isNotEmpty(listCartHold)){
    		WebPosSession webposSession = (WebPosSession)listCartHold.get(Integer.parseInt(indextmp));
    		ShoppingCart cart = webposSession.getCart();
    		
    		String productStoreId = cart.getProductStoreId();
    		GenericValue productStore = null;
    		String showPricesWithVatTax = "Y";
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
			
			if (UtilValidate.isNotEmpty(productStore)){
				showPricesWithVatTax = productStore.getString("showPricesWithVatTax");
			}
    		
    		List<ShoppingCartItem> shoppingCartList = cart.items();
    		for(ShoppingCartItem item : shoppingCartList){
    			count = count + 1;
    			BigDecimal quantity = null;
    			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item.getProductId()), false);
    			Map<String, Object> itemProduct = new HashMap<String, Object>();
    			itemProduct.put("id", count);
    			itemProduct.put("productId", item.getProductId());
    			itemProduct.put("productCode", product.getString("productCode"));
    			itemProduct.put("internalName", product.getString("internalName"));
    			itemProduct.put("productName", item.getName());
    			String uomId = (String) item.getAttribute("quantityUomId");
    			itemProduct.put("uomId", uomId);
    			if (UtilValidate.isNotEmpty(product)&&UtilValidate.isNotEmpty(product.getString("salesUomId"))){
    				String uomToId = product.getString("quantityUomId");
    				if (UtilValidate.isNotEmpty(uomId)){
    					String uomFromId = uomId;
    					String revert = "revert";
    					quantity = UomWorker.customConvertUom(item.getProductId(), uomFromId, uomToId, item.getQuantity(), revert, delegator);
    				}
    			}
    			
    			if (UtilValidate.isNotEmpty(quantity)){
    				itemProduct.put("quantity", quantity);
    			} else {
    				itemProduct.put("quantity", item.getQuantity());
    			}
    			
    			if (showPricesWithVatTax.equals("Y")){
    				itemProduct.put("price", item.getAlterPriceWithTax(cart, dispatcher));
    			} else {
    				if (UtilValidate.isNotEmpty(uomId)){
        				itemProduct.put("price", item.getAlternativeUnitPrice());
        			} else {
        				itemProduct.put("price", item.getBasePrice());
        			}
    			}
    			
    			itemProduct.put("itemAdjustments", item.getAdjustment());
    			itemProduct.put("amount", item.getDisplayItemSubTotal());
    			itemProduct.put("currencyUom", webposSession.getCurrencyUomId());
    			
    			listHoldCartItems.add(itemProduct);
    		}
    	}
    	
		if(UtilValidate.isNotEmpty(listHoldCartItems)){
			request.setAttribute("listHoldCartItems", listHoldCartItems);
		}
		return "success";
		
	}
	
	public static String getDescriptionEnumeration(DispatchContext dctx, Map<String, ? extends Object> context, String enumId) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String description = "";
		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
		if (UtilValidate.isNotEmpty(enumeration)){
			description = (String) enumeration.get("description", locale); 
		}
		return description;
	}
	
	public static Map<String, Object> getContentPromotion(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String , Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String productPromoId = (String) context.get("productPromoId");
		List<Map<String, Object>> listRuleItems = FastList.newInstance();
		List<GenericValue> ruleItems = FastList.newInstance();
		GenericValue productPromo = null;
		String usePriceWithTax = UtilProperties.getMessage("BaseSalesUiLabels", "BSUsePriceWithTax", locale);
		String quantityLabel = UtilProperties.getMessage("BaseSalesUiLabels", "Quantity", locale);
		String amountLabel = UtilProperties.getMessage("BaseSalesUiLabels", "BSAmountOrPercent", locale);
		String productLabel = UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale);
		String partyLabel = UtilProperties.getMessage("BaseSalesUiLabels", "BSPartyId", locale);
		String operatorLabel = UtilProperties.getMessage("BaseSalesUiLabels", "BSOperator", locale);
		String checkInventoryItem = UtilProperties.getMessage("BaseSalesUiLabels", "BSCheckInventoryItem", locale);
		try {
			productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(productPromo)){
			EntityCondition mainCond = EntityCondition.makeCondition("productPromoId", EntityOperator.EQUALS, productPromoId);
			try {
				ruleItems = delegator.findList("ProductPromoRule", mainCond, null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(ruleItems)){
				for (GenericValue rule : ruleItems) {
					Map<String, Object> ruleItemMap = FastMap.newInstance();
					String ruleName = rule.getString("ruleName");
					if (UtilValidate.isNotEmpty(rule.getString("ruleText"))) {
						ruleName += " - " + rule.getString("ruleText");
					}
					ruleItemMap.put("ruleName", ruleName);
					ruleItemMap.put("productPromoRuleId", rule.getString("productPromoRuleId"));
					List<GenericValue> productPromoConds = rule.getRelated("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId"), false);
					List<GenericValue> productPromoActions = rule.getRelated("ProductPromoAction", null, UtilMisc.toList("productPromoActionSeqId"), false);
					String condition = "";
					String action = "";
					String productCategoryCond = "<div class='margin-top2'>";
					String productCategoryAction = "<div class='margin-top2'>";
					if (UtilValidate.isNotEmpty(productPromoConds)){
						for (GenericValue cond : productPromoConds){
							String inputParam = getDescriptionEnumeration(dctx, context, cond.getString("inputParamEnumId"));
							String operator = getDescriptionEnumeration(dctx, context, cond.getString("operatorEnumId"));
							if (!inputParam.equals("")){
								String detailCondition = inputParam + " " + operator + " " + cond.getString("condValue");
								detailCondition += "<ul style='margin-left: -20px;'>" + "<li>" + usePriceWithTax + ": " + (cond.getString("usePriceWithTax")==null?"N":cond.getString("usePriceWithTax")) + "</li>";
								condition += detailCondition + "</ul>" + "<br>";
							}
							
							List<GenericValue> condProductPromoCategories = cond.getRelated("ProductPromoCategory", null, null, false);
							if (UtilValidate.isNotEmpty(condProductPromoCategories)){
								for (GenericValue condProductPromoCategory : condProductPromoCategories){
									GenericValue condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true);
									productCategoryCond += "<span class='text-success span-product-category'>C </span>";
									if (UtilValidate.isNotEmpty(condProductCategory.get("description",locale))){
										productCategoryCond += condProductCategory.get("description",locale);
									}
									productCategoryCond += " [" + condProductPromoCategory.getString("productCategoryId") + "]" + "<br>";
								}
							}
							
							List<GenericValue> condProductPromoProducts = cond.getRelated("ProductPromoProduct", null, null, false);
							if (UtilValidate.isNotEmpty(condProductPromoProducts)){
								for (GenericValue condProductPromoProduct : condProductPromoProducts){
									GenericValue condProduct = condProductPromoProduct.getRelatedOne("Product", true);
									productCategoryCond += "<span class='text-success span-product-category'>P </span>";
									if (UtilValidate.isNotEmpty(condProduct.getString("productName"))){
										productCategoryCond += condProduct.getString("productName");
									}
									productCategoryCond += " [" + condProduct.getString("productCode") + "]" + "<br>";
								}
							}
							
							productCategoryCond += "<br>";
						}
					}
					if (UtilValidate.isNotEmpty(productPromoActions)){
						for (GenericValue act : productPromoActions){
							String actionEnum = getDescriptionEnumeration(dctx, context, act.getString("productPromoActionEnumId"));
							if (!actionEnum.equals("")){
								GenericValue actionOperEnum = act.getRelatedOne("OperatorEnumeration", true);
								String detailAction = actionEnum + "<ul style='margin-left: -20px;'>";
								if (UtilValidate.isNotEmpty(act.getBigDecimal("quantity"))){
									BigDecimal quantity = act.getBigDecimal("quantity");
									int quantityI = quantity.intValue();
									detailAction += "<li>" + quantityLabel + ": " + quantityI + "</li>";
								}
								if (UtilValidate.isNotEmpty(act.getBigDecimal("amount"))){
									BigDecimal amount = act.getBigDecimal("amount");
									int amountI = amount.intValue();
									detailAction += "<li>" + amountLabel + ": " + amountI + "</li>";
								}
								if (UtilValidate.isNotEmpty(act.getString("productId"))){
									detailAction += "<li>" + productLabel + ": " + act.getString("productId") + "</li>";
								}
								if (UtilValidate.isNotEmpty(act.getString("partyId"))){
									detailAction += "<li>" + partyLabel + ": " + act.getString("partyId") + "</li>";
								}
								if (UtilValidate.isNotEmpty(actionOperEnum.get("description",locale))){
									detailAction += "<li>" + operatorLabel + ": " + actionOperEnum.get("description", locale) + "</li>";
								}
								if (UtilValidate.isNotEmpty(act.getString("isCheckInv"))){
									detailAction += "<li>" + checkInventoryItem + ": " + act.getString("isCheckInv") + "</li>";
								}
								action += detailAction + "</ul>" + "<br>";
							}
							List<GenericValue> actionProductPromoCategories = act.getRelated("ProductPromoCategory", null, null, false);
							if (UtilValidate.isNotEmpty(actionProductPromoCategories)){
								for (GenericValue actionProductPromoCategory : actionProductPromoCategories){
									GenericValue actionProductCategory = actionProductPromoCategory.getRelatedOne("ProductCategory", true);
									productCategoryAction += "<span class='text-success span-product-category'>C </span>";
									if (UtilValidate.isNotEmpty(actionProductCategory.get("description",locale))){
										productCategoryAction += actionProductCategory.get("description",locale);
									}
									productCategoryAction += " [" + actionProductCategory.getString("productCategoryId") + "]" + "<br>";
								}
							}
							List<GenericValue> actionProductPromoProducts = act.getRelated("ProductPromoProduct", null, null, false);
							if (UtilValidate.isNotEmpty(actionProductPromoProducts)){
								for (GenericValue actionProductPromoProduct : actionProductPromoProducts){
									GenericValue actionProduct = actionProductPromoProduct.getRelatedOne("Product", true);
									productCategoryAction += "<span class='text-success span-product-category'>P </span>";
									if (UtilValidate.isNotEmpty(actionProduct.getString("productName"))){
										productCategoryAction += actionProduct.getString("productName");
									}
									productCategoryAction += " [" + actionProduct.getString("productCode") + "]" + "<br>";
								}
							}
							
							productCategoryAction += "<br>";
						}
					}
					productCategoryCond += "</div>";
					productCategoryAction += "</div>";
					ruleItemMap.put("condition", condition);
					ruleItemMap.put("productCategoryCond", productCategoryCond);
					ruleItemMap.put("action", action);
					ruleItemMap.put("productCategoryAction", productCategoryAction);
					listRuleItems.add(ruleItemMap);
				}
			}
		}
		result.put("listRuleItems", listRuleItems);
		return result;
	}
	
	public static String getOverviewPromotion(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		String productPromoId = request.getParameter("productPromoId");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		if(UtilValidate.isNotEmpty(productPromoId)){
			GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
			if (UtilValidate.isNotEmpty(productPromo)){
				request.setAttribute("productPromoId", productPromoId);
				request.setAttribute("promoName", productPromo.getString("promoName"));
				request.setAttribute("promoText", productPromo.getString("promoText"));
				Timestamp fromDate = productPromo.getTimestamp("fromDate");
				Timestamp thruDate = productPromo.getTimestamp("thruDate");
				SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
				String fromDateValue = "";
				String thruDateValue = "";
				if (UtilValidate.isNotEmpty(fromDate)){
					Date fromDateTmp = new Date(fromDate.getTime());
					fromDateValue = simpleFormat.format(fromDateTmp);
				}
				if (UtilValidate.isNotEmpty(thruDate)){
					Date thruDateTmp = new Date(thruDate.getTime());
					thruDateValue = simpleFormat.format(thruDateTmp);
				}
				request.setAttribute("fromDate", fromDateValue);
				request.setAttribute("thruDate", thruDateValue);
				List<EntityCondition> condRole = FastList.newInstance();
				condRole.add(EntityCondition.makeCondition("productPromoId", productPromoId));
				condRole.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				List<GenericValue> listPromoRoleType = delegator.findList("ProductPromoRoleTypeAppl", EntityCondition.makeCondition(condRole, EntityJoinOperator.AND), 
						null, null, null, false);
				String roleType = "";
				if (UtilValidate.isNotEmpty(listPromoRoleType)){
					for (GenericValue role : listPromoRoleType){
						roleType += role.getString("roleTypeId") + " ";
					}
				}
				request.setAttribute("roleTypeId", roleType);
				request.setAttribute("requireCode", productPromo.getString("requireCode"));
				request.setAttribute("useLimitPerOrder", productPromo.getString("useLimitPerOrder"));
				request.setAttribute("useLimitPerCustomer", productPromo.getString("useLimitPerCustomer"));
				request.setAttribute("useLimitPerPromotion", productPromo.getString("useLimitPerPromotion"));
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetInfoPromotion", UtilMisc.toMap("productPromoId", productPromoId), locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		}else{
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetInfoPromotion", UtilMisc.toMap("productPromoId", productPromoId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
			
		return "success";
	}
}
