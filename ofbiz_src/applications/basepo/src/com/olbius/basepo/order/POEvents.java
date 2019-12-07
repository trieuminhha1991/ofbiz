package com.olbius.basepo.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.product.util.ProductUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class POEvents {
	public static final String module = POEvents.class.getName();

	public static String initializePurchaseOrderEntryService(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();

		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String currencyUomId = request.getParameter("currencyUomId");
		String supplierId = request.getParameter("supplierId");
		String salesMethodChannelEnumId = request.getParameter("salesMethodChannelEnumId");
		String contactMechId = request.getParameter("contactMechId");
		String listOrderItems = request.getParameter("orderItems");
		String originFacilityId = request.getParameter("facilityId");
		String shipBeforeDateStr = request.getParameter("shipBeforeDate");
		String shipAfterDateStr = request.getParameter("shipAfterDate");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String productPlanId = request.getParameter("productPlanId");

		org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);

		try {
			ShoppingCart cart = null;
			Map<String, Object> contextMap = FastMap.newInstance();
			contextMap.put("partyIdFrom", supplierId);
			contextMap.put("orderItems", listOrderItems);
			contextMap.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
			contextMap.put("contactMechId", contactMechId);
			contextMap.put("currencyUomId", currencyUomId);
			contextMap.put("shipBeforeDate", shipBeforeDateStr);
			contextMap.put("shipAfterDate", shipAfterDateStr);
			contextMap.put("originFacilityId", originFacilityId);
			contextMap.put("customTimePeriodId", customTimePeriodId);
			contextMap.put("productPlanId", productPlanId);
			contextMap.put("userLogin", userLogin);

			Map<String, Object> resultValue = dispatcher.runSync("initPurchaseOrderService", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultValue));
				org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
				return "error";
			}
			cart = (ShoppingCart) resultValue.get("shoppingCart");
			session.setAttribute("shoppingCart", cart);
		} catch (Exception e) {
			String errMsg = "Fatal error calling initializePurchaseOrderEntryService service: " + e.toString();
			Debug.logError(e, errMsg, module);

			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
			return "error";
		}

		return "success";
	}

	public static String processPurchaseOrder(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Security security = (Security) request.getAttribute("security");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String productPlanId = request.getParameter("productPlanId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"PURCHASEORDER_NEW");
		if (!hasPermission) {
			String errMsg = UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}

		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
		boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);

		String orderId = "";
		Map<String, Object> callResult = checkOutHelper.createOrder(userLogin, null, null, null, areOrderItemsExploded,
				null, null);
		if (!ServiceUtil.isError(callResult)) {
			orderId = (String) callResult.get("orderId");
			request.setAttribute("orderId", orderId);
			if (!productPlanId.equals("") && !customTimePeriodId.equals("")) {
				List<GenericValue> planHeader = FastList.newInstance();
				try {
					planHeader = delegator
							.findByAnd(
									"ProductPlanHeader", UtilMisc.toMap("customTimePeriodId", customTimePeriodId,
											"parentProductPlanId", productPlanId),
									null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findByAnd ProductPlanHeader: " + e.toString();
					Debug.logError(e, errMsg, module);
					return "error";
				}
				if (UtilValidate.isNotEmpty(planHeader)) {
					String productPlanIdWeek = planHeader.get(0).getString("productPlanId");
					GenericValue planOrder = delegator.makeValue("ProductPlanAndOrder");
					planOrder.set("productPlanId", productPlanIdWeek);
					planOrder.set("orderId", orderId);
					planOrder.set("fromDate", UtilDateTime.nowTimestamp());
					try {
						planOrder.create();
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when create ProductPlanAndOrder: " + e.toString();
						Debug.logError(e, errMsg, module);
						return "error";
					}

					List<GenericValue> orderItems = FastList.newInstance();
					try {
						orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findByAnd OrderItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return "error";
					}
					if (UtilValidate.isNotEmpty(orderItems)) {
						for (GenericValue item : orderItems) {
							GenericValue planItem = null;
							try {
								planItem = delegator.findOne("ProductPlanItem",
												UtilMisc.toMap("productPlanId", productPlanIdWeek, "customTimePeriodId",
														customTimePeriodId, "productId", item.getString("productId")),
												false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findOne ProductPlanItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return "error";
							}

							if (UtilValidate.isNotEmpty(planItem)) {
								BigDecimal quantity = item.getBigDecimal("quantity");
								if (UtilValidate.isNotEmpty(planItem.get("orderedQuantity"))) {
									quantity = quantity.add(planItem.getBigDecimal("orderedQuantity"));
								}
								planItem.set("orderedQuantity", quantity);
								try {
									planItem.store();
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when store ProductPlanItem: " + e.toString();
									Debug.logError(e, errMsg, module);
									return "error";
								}
							}
						}
					}
				}
			}

			org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);

			try {
				dispatcher.runSync("sendNotifyToAcc", UtilMisc.toMap("orderId", orderId, "oldUserLogin", userLogin,
						"userLogin", userLogin, "isEdit", "N"));
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when sendNotifyToAcc: " + e.toString();
				Debug.logError(e, errMsg, module);
				return "error";
			}
		} else {
			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
			return "error";
		}

		return "success";
	}

	public static String createNewReturnSupplier(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Security security = (Security) request.getAttribute("security");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"RETURNPO_NEW");
		String currencyUomId = request.getParameter("currencyUomId");
		String description = request.getParameter("description");
		String supplierId = request.getParameter("toPartyId");
		String destinationFacilityId = request.getParameter("destinationFacilityId");
		String returnHeaderTypeId = request.getParameter("returnHeaderTypeId");
		String statusId = request.getParameter("statusId");
		String listOrderItems = request.getParameter("orderItems");
		String listAdjustmentPromoItems = request.getParameter("adjustmentPromoItems");
		String needsInventoryReceive = request.getParameter("needsInventoryReceive");
		if (hasPermission) {
			List<Object> errMsgList = FastList.newInstance();
			boolean beganTx = false;
			String returnId;
			try {
				// begin the transaction
				beganTx = TransactionUtil.begin(7200);

				Map<String, Object> contextMap = FastMap.newInstance();
				contextMap.put("toPartyId", supplierId);
				contextMap.put("returnHeaderTypeId", returnHeaderTypeId);
				contextMap.put("statusId", statusId);
				contextMap.put("currencyUomId", currencyUomId);
				contextMap.put("entryDate", UtilDateTime.nowTimestamp());
				contextMap.put("description", description);
				contextMap.put("needsInventoryReceive", needsInventoryReceive);
				contextMap.put("destinationFacilityId", destinationFacilityId);
				contextMap.put("userLogin", userLogin);

				Map<String, Object> resultValue = dispatcher.runSync("createNewReturnSupplier", contextMap);
				if (ServiceUtil.isError(resultValue)) {
					try {
						TransactionUtil.rollback(beganTx, "Failure in processing Create new return supplier callback",
								null);
					} catch (Exception e1) {
						Debug.logError(e1, module);
					}
					return "error";
				}

				returnId = (String) resultValue.get("returnId");
				Map<String, Object> itemMap = FastMap.newInstance();
				itemMap.put("returnId", returnId);
				itemMap.put("orderItems", listOrderItems);
				itemMap.put("adjustmentPromoItems", listAdjustmentPromoItems);
				itemMap.put("userLogin", userLogin);

				Map<String, Object> itemValue = dispatcher.runSync("createReturnSupplierItems", itemMap);
				if (ServiceUtil.isError(itemValue)) {
					try {
						TransactionUtil.rollback(beganTx, "Failure in processing Create return supplier item callback",
								null);
					} catch (Exception e1) {
						Debug.logError(e1, module);
					}
					return "error";
				}
			} catch (Exception e) {
				Debug.logError(e, module);
				try {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
				} catch (Exception e1) {
					Debug.logError(e1, module);
				}
				errMsgList.add(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			} catch (Throwable t) {
				Debug.logError(t, module);
				request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
				try {
					TransactionUtil.rollback(beganTx, t.getMessage(), t);
				} catch (Exception e2) {
					Debug.logError(e2, module);
				}
				errMsgList.add(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			} finally {
				if (UtilValidate.isNotEmpty(errMsgList)) {
					try {
						TransactionUtil.rollback(beganTx, "Have error when process", null);
					} catch (Exception e2) {
						Debug.logError(e2, module);
					}
					request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
					return "error";
				} else {
					// commit the transaction
					try {
						TransactionUtil.commit(beganTx);
					} catch (Exception e) {
						Debug.logError(e, module);
					}
				}
			}

			request.setAttribute("returnId", returnId);
		} else {
			String errMsg = UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}

		return "success";
	}

	public static String getListUomByProduct(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);

		String productId = request.getParameter("productId");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String uomId = null;
		if (UtilValidate.isNotEmpty(product)) {
			uomId = product.getString("quantityUomId");
		}

		String description = "";
		if (UtilValidate.isNotEmpty(uomId)) {
			GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
			if (UtilValidate.isNotEmpty(uom)) {
				description = (String) uom.get("description", locale);
			}
		}

		request.setAttribute("uomId", uomId);
		request.setAttribute("description", description);

		return "success";
	}

	public static String getProductOrderMap(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Map<String, Object> productOrderMap = FastMap.newInstance();

		String supplierId = request.getParameter("supplierId");
		String productPlanId = request.getParameter("productPlanId");
		String customTimePeriodId = request.getParameter("customTimePeriodId");

		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		if (cart != null) {
			List<GenericValue> orderItems = cart.makeOrderItems();
			if (orderItems != null) {
				for (GenericValue orderItem : orderItems) {
					if (orderItem.get("productId") != null
							&& (orderItem.get("isPromo") == null || "N".equals(orderItem.get("isPromo")))) {
						Map<String, Object> itemMap = FastMap.newInstance();
						itemMap.put("quantity", orderItem.get("quantity"));
						itemMap.put("quantityUomId", orderItem.get("quantityUomId"));
						itemMap.put("lastPrice", orderItem.get("unitPrice"));
						productOrderMap.put(orderItem.getString("productId"), itemMap);
					}
				}
			}
		}

		if (!"".equals(productPlanId) && !"".equals(customTimePeriodId)) {
			List<GenericValue> planHeader = FastList.newInstance();
			try {
				planHeader = delegator
						.findByAnd(
								"ProductPlanHeader", UtilMisc.toMap("customTimePeriodId", customTimePeriodId,
										"parentProductPlanId", productPlanId, "productPlanTypeId", "PO_PLAN"),
								null, false);
			} catch (GenericEntityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(planHeader)) {
				List<GenericValue> planItem = FastList.newInstance();
				String productPlanIdWeek = planHeader.get(0).getString("productPlanId");
				try {
					planItem = delegator.findByAnd("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanIdWeek,
							"customTimePeriodId", customTimePeriodId), null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (UtilValidate.isNotEmpty(planItem)) {
					for (GenericValue item : planItem) {
						BigDecimal planQuantity = item.getBigDecimal("planQuantity") != null
								? item.getBigDecimal("planQuantity") : BigDecimal.ZERO;
						BigDecimal orderedQuantity = item.getBigDecimal("orderedQuantity") != null
								? item.getBigDecimal("orderedQuantity") : BigDecimal.ZERO;

						if (planQuantity.compareTo(orderedQuantity) > 0) {
							Map<String, Object> itemMap = FastMap.newInstance();
							itemMap.put("quantity", planQuantity.subtract(orderedQuantity));
							itemMap.put("lastPrice",
									getProductPriceBySupplier(item.getString("productId"), supplierId, delegator));
							productOrderMap.put(item.getString("productId"), itemMap);
						}
					}
				}
			}
		}

		request.setAttribute("productOrderMap", productOrderMap);

		return "success";
	}

	public static BigDecimal getProductPriceBySupplier(String productId, String supplierId, Delegator delegator) {
		BigDecimal lastPrice = BigDecimal.ZERO;

		List<GenericValue> productPrice = FastList.newInstance();
		List<EntityCondition> mainCondList = FastList.newInstance();
		mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		mainCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		mainCondList.add(EntityCondition.makeCondition("supplierPrefOrderId", EntityOperator.EQUALS, "10_MAIN_SUPPL"));
		mainCondList.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
		EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

		try {
			productPrice = delegator.findList("SupplierProductAndProductAndUom", mainCond, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(productPrice)) {
			lastPrice = productPrice.get(0).getBigDecimal("lastPrice");
		}

		return lastPrice;
	}
	

	public static String findProductsToReturn(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String productToSearch = request.getParameter("productToSearch");
    	String supplierId = request.getParameter("supplierPartyId");

		String currencyUomId = request.getParameter("currencyUomId");;
		List<EntityCondition> mainCondList = FastList.newInstance();
		List<EntityCondition> orConds = FastList.newInstance();
		
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		mainCondList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
		
		orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
		orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
    	
    	EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
    	
    	mainCondList.add(orCond);
		EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
		listAllConditions.add(mainCond);
		
		try {
			listProductTmps = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			if (!listProductTmps.isEmpty()) listProducts.addAll(listProductTmps);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("listProducts", listProducts);
    	return "success";
    }
	
	public static String findProductsPurchase(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String productToSearch = request.getParameter("productToSearch");
    	String supplierId = request.getParameter("supplierPartyId");
    	String facilityId = request.getParameter("facilityId");

		String currencyUomId = request.getParameter("currencyUomId");;
		List<EntityCondition> mainCondList = FastList.newInstance();
		List<EntityCondition> orConds = FastList.newInstance();
		
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		try {
			mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
			mainCondList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
			mainCondList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD"));
			
			orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
			orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	
        	EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
        	
        	mainCondList.add(orCond);
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setLimit(30);
			listProductTmps = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, opts, false);
			if (UtilValidate.isNotEmpty(listProductTmps)){
                for (GenericValue itemProd : listProductTmps) {
                	Map<String, Object> proMap = FastMap.newInstance();
                	proMap.putAll(itemProd);
                	
                	String productId = (String)itemProd.get("productId");
                	String purchaseUomId = (String)itemProd.get("purchaseUomId");
                	String requireAmount = (String)itemProd.get("requireAmount");
                	List<Map<String, Object>> listQtyUoms = new ArrayList<Map<String, Object>>();
                	List<Map<String, Object>> listWeUoms = new ArrayList<Map<String, Object>>();
                	if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)){
                		listWeUoms = ProductUtil.getProductWeightUomWithConvertNumbers(delegator, productId);
                		String weightUomId = itemProd.getString("weightUomId");
    					if (UtilValidate.isEmpty(purchaseUomId)) {
    						purchaseUomId = weightUomId;
    						proMap.put("uomId", purchaseUomId);
    					} else {
    						GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", purchaseUomId));
    						if (!"WEIGHT_MEASURE".equals(uom.getString("uomTypeId"))) {
    							purchaseUomId = weightUomId;
        						proMap.put("uomId", purchaseUomId);
    						}
    					}
                	} else {
                		String quantityUomId = itemProd.getString("quantityUomId");
    					if (UtilValidate.isEmpty(purchaseUomId)) {
    						purchaseUomId = quantityUomId;
    						proMap.put("uomId", purchaseUomId);
    					}
                	}
            		listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
					proMap.put("weightUomIds", listWeUoms);
					proMap.put("quantityUomIds", listQtyUoms);
					
					Boolean check = false;
					for (Map<String, Object> map : listQtyUoms) {
						String uomId = (String)map.get("quantityUomId");
						if (uomId.equals(purchaseUomId)){
							BigDecimal price = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, purchaseUomId, BigDecimal.ONE);
							proMap.put("lastPrice", price);
							proMap.put("quantityUomId", purchaseUomId);
							proMap.put("uomId", purchaseUomId);
							check = true;
							break;
						}
					}
					if (!check){
						for (Map<String, Object> map : listWeUoms) {
							String uomId = (String)map.get("uomId");
							if (uomId.equals(purchaseUomId)){
								BigDecimal price = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, purchaseUomId, BigDecimal.ONE);
								proMap.put("lastPrice", price);
								proMap.put("weightUomId", purchaseUomId);
								proMap.put("uomId", purchaseUomId);
								check = true;
								break;
							}
						}
					}
					if (!check && (UtilValidate.isEmpty(requireAmount) || !"Y".equals(requireAmount))){
						continue;
					}
					BigDecimal qoh = BigDecimal.ZERO;
                	BigDecimal atp = BigDecimal.ZERO;
                	BigDecimal aoh = BigDecimal.ZERO;
                	if (UtilValidate.isNotEmpty(facilityId)) {
                    	GenericValue objProductFacility = null;
						objProductFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
						if (UtilValidate.isNotEmpty(objProductFacility)) {
							qoh = qoh.add(objProductFacility.getBigDecimal("lastInventoryCount"));
		                	aoh = aoh.add(objProductFacility.getBigDecimal("lastInventoryCount"));
						} 
					}
                	proMap.put("quantityOnHandTotal", qoh);
                	proMap.put("availableToPromiseTotal", atp);
                	proMap.put("amountOnHandTotal", aoh);
                	listProducts.add(proMap);
                }
			}
			
    		//get quantity from session
    		Map<String, Object> productQuantitiesMap = FastMap.newInstance();
    		Map<String, Object> productUomIdsMap = FastMap.newInstance();
    		if (UtilValidate.isNotEmpty(listProducts) && request != null) {
				HttpSession session = request.getSession();
				ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
				if (cart != null) {
					List<GenericValue> orderItems = cart.makeOrderItems();
					if (orderItems != null) {
						for (GenericValue orderItem : orderItems) {
							if (orderItem.get("productId") != null && (orderItem.get("isPromo") == null || "N".equals(orderItem.get("isPromo")))) {
								productQuantitiesMap.put(orderItem.getString("productId"), orderItem.get("quantity"));
								productUomIdsMap.put(orderItem.getString("productId"), orderItem.get("quantityUomId"));
							}
						}
						if (productQuantitiesMap.size() > 0) {
							for (Map<String, Object> item : listProducts) {
								if (productQuantitiesMap.containsKey(item.get("productId"))) {
									item.put("quantity", productQuantitiesMap.get(item.get("productId")));
									item.put("quantityChild", productQuantitiesMap.get(item.get("productId")));
								}
								if (productUomIdsMap.containsKey(item.get("productId"))) {
									item.put("quantityUomId", productUomIdsMap.get(item.get("productId")));
								}
							}
						}
					}
				}
    		}
    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductBySupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", listProducts);
    	return "success";
    }
	
	public static String findProductsInPurchaseOrder(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String productToSearch = request.getParameter("productToSearch");

		List<EntityCondition> mainCondList = FastList.newInstance();
		List<GenericValue> listProducts = new ArrayList<GenericValue>();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		try {
			
			mainCondList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			listProducts = delegator.findList("Product", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductBySupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", listProducts);
    	return "success";
    }
	
	public static String findSuppliers(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String supplierToSearch = request.getParameter("supplierToSearch");
    	HttpSession session = request.getSession();

		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	
		List<EntityCondition> mainCondList = FastList.newInstance();
		List<EntityCondition> orConds = FastList.newInstance();
		
		List<Map<String, Object>> listSuppliers = new ArrayList<Map<String, Object>>();
		
		List<String> listSupplierPartyIds = com.olbius.basehr.util.SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, "SUPPLIER", delegator);
		
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		
		mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listSupplierPartyIds));
		
		orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + supplierToSearch +"%")));
		orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + supplierToSearch +"%")));
    	
    	EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
    	
    	mainCondList.add(orCond);
		EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
		listAllConditions.add(mainCond);
		List<GenericValue> listParties = FastList.newInstance();
		try {
			listParties = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyFullNameDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "error";
		}
		if (!listParties.isEmpty()) {
			listSuppliers.addAll(listParties);
		}
		
		request.setAttribute("listSuppliers", listSuppliers);
    	return "success";
    }
}
