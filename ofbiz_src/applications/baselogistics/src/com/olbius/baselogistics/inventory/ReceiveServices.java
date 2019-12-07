package com.olbius.baselogistics.inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.delivery.DeliveryItemSubject;
import com.olbius.baselogistics.delivery.DeliveryObserver;
import com.olbius.baselogistics.delivery.ItemSubject;
import com.olbius.baselogistics.delivery.Observer;
import com.olbius.baselogistics.transfer.TransferReadHepler;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReceiveServices {
	
	public static final String module = ReceiveServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resources = "AccountingUiLabels";
	public static final String resourceError = "BaseLogisticsErrorUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveProductFromPurchaseDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String deliveryId = (String)context.get("deliveryId");
    	GenericValue objDelivery = null;
    	if (UtilValidate.isNotEmpty(deliveryId)) {
			try {
				objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Delivery: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
    	if (UtilValidate.isEmpty(objDelivery)) {
    		String errMsg = "OLBIUS: Fatal error when receiveProductFromPurchaseDelivery: Delivery not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	String facilityId = objDelivery.getString("destFacilityId");
    	String contactMechId = objDelivery.getString("destContactMechId");
    	if (UtilValidate.isEmpty(facilityId)) {
    		String errMsg = "OLBIUS: Fatal error when receiveProductFromPurchaseDelivery: destFacilityId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	if (UtilValidate.isEmpty(contactMechId)) {
    		String errMsg = "OLBIUS: Fatal error when findOne Delivery: destContactMechId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	
		String listItems = null;
		if (UtilValidate.isNotEmpty(context.get("listDeliveryItems"))) {
			listItems = (String)context.get("listDeliveryItems");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		String orderId = objDelivery.getString("orderId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseDelivery - JqxWidgetSevices.convert error!");
		}
		
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseDelivery - JqxWidgetSevices.convert error!");
			}
		}
		
		if (!listProducts.isEmpty() && ("DLV_APPROVED".equals(objDelivery.getString("statusId")) || "DLV_EXPORTED".equals(objDelivery.getString("statusId"))) 
				&& UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			

			if ("DLV_APPROVED".equals(objDelivery.getString("statusId"))){
				// auto set actual_exported_quantity = quantity
				EntityCondition cond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_APPROVED");
				EntityCondition cond2 = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				conds.add(cond2);
				List<GenericValue> listDeliveryItems = FastList.newInstance();
				try {
					listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listDeliveryItems.isEmpty()){
					for (GenericValue item : listDeliveryItems) {
						item.set("actualExportedQuantity", item.getBigDecimal("quantity"));
						item.store();
					}
				}
				
				// auto change to exported
				Map<String, Object> map = FastMap.newInstance();
				map.put("deliveryId", deliveryId);
				map.put("statusId", "DLV_EXPORTED");
				map.put("userLogin", userLogin);
				try {
					Map<String, Object> mapReturn = dispatcher.runSync("changeDeliveryStatus", map);
					if (ServiceUtil.isError(mapReturn)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when runSync changeDeliveryStatus: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
			
			Map<String, Object> mapAttributes = FastMap.newInstance();
			for (Map<String, Object> dlvItem : listProducts){
				String deliveryItemSeqId = null;
				if (dlvItem.containsKey("deliveryItemSeqId")){
					deliveryItemSeqId = (String)dlvItem.get("deliveryItemSeqId");
				}
				String productId = null;
				if (dlvItem.containsKey("productId")){
					productId = (String)dlvItem.get("productId");
				}
				String orderItemSeqId = null;
				if (dlvItem.containsKey("orderItemSeqId")){
					orderItemSeqId = (String)dlvItem.get("orderItemSeqId");
				}
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (dlvItem.containsKey("quantity")){
					quantityStr = (String)dlvItem.get("quantity");
					quantity = new BigDecimal(quantityStr);
				}
				
				if (!listProductAttrs.isEmpty()) {
					List<Map<String, Object>> listAttributes = FastList.newInstance();
					for (Map<String, Object> map : listProductAttrs) {
						if (map.containsKey("orderItemSeqId")){
							String prId = (String)map.get("orderItemSeqId");
							if (UtilValidate.isNotEmpty(prId) && orderItemSeqId.equals(prId)) {
								listAttributes.add(map);
							}
						}
					}
					mapAttributes.put(orderItemSeqId, listAttributes);
				}

				if (UtilValidate.isNotEmpty(productId)) {
					if (UtilValidate.isNotEmpty(deliveryItemSeqId)) {
						
						GenericValue dlvItemDB = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
						BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
						BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
						if (ProductUtil.isWeightProduct(delegator, productId)){
							actualDeliveredQuantity = BigDecimal.ONE;
							actualDeliveredAmount = quantity;
						} else {
							actualDeliveredQuantity = quantity;
						}
						BigDecimal quantityCreated = dlvItemDB.getBigDecimal("quantity");
						if (actualDeliveredQuantity.compareTo(quantityCreated) > 0){
							return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLProductHasBeenReceived", (Locale)context.get("locale")));
						}
						dlvItemDB.put("actualDeliveredQuantity", actualDeliveredQuantity);
						dlvItemDB.put("actualDeliveredAmount", actualDeliveredAmount);
						delegator.store(dlvItemDB);
					} 
				}
			}
			objDelivery.refresh();
			Map<String, Object> map = FastMap.newInstance();
			map.put("deliveryId", deliveryId);
			map.put("statusId", "DLV_DELIVERED");
			map.put("userLogin", userLogin);
			try {
				Map<String, Object> mapReturn = dispatcher.runSync("changeDeliveryStatus", map);
				if (ServiceUtil.isError(mapReturn)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when runSync changeDeliveryStatus: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			objDelivery.refresh();
			
			Map<String, Object> shipmentParam = FastMap.newInstance();
			shipmentParam.put("deliveryId", deliveryId);
			shipmentParam.put("userLogin", userLogin);
			Map<String, Object> mapShipment = FastMap.newInstance();
			String shipmentId = null;
			try {
				mapShipment = dispatcher.runSync("createShipmentForPurchaseDelivery", shipmentParam);
				shipmentId = (String)mapShipment.get("shipmentId");
			} catch (GenericServiceException e1) {
				return ServiceUtil.returnError("createShipmentForPurchaseDelivery Error!");
			}
            objDelivery.put("shipmentId", shipmentId);
            objDelivery.store();
			
			List<GenericValue> listOrderShipments = FastList.newInstance();
			EntityCondition cond2 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond2);
			try {
				listOrderShipments = delegator.findList("OrderShipment", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList OrderShipment error!");
			}
//			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			if (UtilValidate.isNotEmpty(listOrderShipments)) {
				for (GenericValue item : listOrderShipments) {
					String orderItemSeqId = item.getString("orderItemSeqId");
					BigDecimal quantityShip = item.getBigDecimal("quantity");
					BigDecimal weightShip = item.getBigDecimal("weight");
					GenericValue objShipmentItem = null;
					try {
						objShipmentItem = delegator.findOne("ShipmentItem", false, UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.get("shipmentItemSeqId")));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne ShipmentItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					String productId = objShipmentItem.getString("productId");
					
					List<Map<String, Object>> listAttribues = FastList.newInstance();
					if (mapAttributes.containsKey(orderItemSeqId)){
						listAttribues = (List<Map<String, Object>>)mapAttributes.get(orderItemSeqId);
					}
					
					// get cost
					BigDecimal unitPrice = BigDecimal.ZERO;
					GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
					String currencyUom = orderHeader.getString("currencyUom");
					GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", company));
					String baseCurrencyUomId = partyAcctgPreference.getString("baseCurrencyUomId");
					// Calculate price by amount
					GenericValue objOrderItem = null;
					try {
						objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne OrderItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					unitPrice = objOrderItem.getBigDecimal("unitPrice");
					if (ProductUtil.isWeightProduct(delegator, productId)) {
						BigDecimal amount = objOrderItem.getBigDecimal("selectedAmount");
						BigDecimal unitPriceInit = objOrderItem.getBigDecimal("unitPrice");
						unitPrice = unitPriceInit.divide(amount, decimals, rounding);
					}
					
					BigDecimal avgUnitPrice = BigDecimal.ZERO;
					map = FastMap.newInstance();
					
					if (!baseCurrencyUomId.equals(currencyUom)){
						avgUnitPrice = getReceiveCost(delegator, dispatcher, company, orderId, orderItemSeqId, deliveryId);
						map.put("unitCost", avgUnitPrice);
						map.put("orderCurrencyUnitPrice", unitPrice);
					} else {
						map.put("unitCost", unitPrice);
					}
					String shipmentItemSeqId = item.getString("shipmentItemSeqId");
					
					if (!listAttribues.isEmpty()){
						// xu ly nhap sp co HSD, NSX, Lo SX
						BigDecimal quantityFree = quantityShip;
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							quantityFree = weightShip;
						} 
						
						for (Map<String, Object> mapAttr : listAttribues) {
							Map<String, Object> attributes = FastMap.newInstance();
							String qtyStr = null;
							if (mapAttr.containsKey("quantity") && mapAttr.containsKey("productId")){
								qtyStr = (String)mapAttr.get("quantity");
								if (UtilValidate.isNotEmpty(qtyStr)) {
									BigDecimal quantityP = new BigDecimal(qtyStr); // quantity tuong ung se la can nang voi san pham can nang
									quantityFree = quantityFree.subtract(quantityP);
									if (quantityP.compareTo(BigDecimal.ZERO) > 0){
										for (String key : mapAttr.keySet()) {
											if ("expireDate".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("expireDate"))) {
													String expStr = (String)mapAttr.get("expireDate");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("datetimeManufactured".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("datetimeManufactured"))) {
													String expStr = (String)mapAttr.get("datetimeManufactured");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("lotId".equals(key)){ 
												String lotId = (String)mapAttr.get(key);
												GenericValue objLot = null;
												try {
													objLot = delegator.findOne("Lot", false,
															UtilMisc.toMap("lotId", lotId));
												} catch (GenericEntityException e) {
													Debug.logError(e.toString(), module);
													return ServiceUtil.returnError("OLBIUS: findOne Lot error!");
												}
												if (UtilValidate.isEmpty(objLot)) {
													// create new lot
													objLot = delegator.makeValue("Lot");
													objLot.put("lotId", lotId);
													objLot.put("creationDate", UtilDateTime.nowTimestamp());
													delegator.create(objLot); 
												}
												attributes.put(key, lotId);
											} else {
												attributes.put(key, mapAttr.get(key));
											}
										}
										
										map.put("orderId", orderId);
										map.put("orderItemSeqId", orderItemSeqId);
										map.put("productId", productId);
										map.put("shipmentItemSeqId", shipmentItemSeqId);
										map.put("shipmentId", shipmentId);
										map.put("quantityAccepted", quantityP);
										if (ProductUtil.isWeightProduct(delegator, productId)) {
											map.put("amountAccepted", quantityP);
											map.put("quantityAccepted", BigDecimal.ONE);
										} 
										map.put("quantityExcess", BigDecimal.ZERO);
										map.put("quantityRejected", BigDecimal.ZERO);
										map.put("quantityQualityAssurance", BigDecimal.ZERO);
										map.put("ownerPartyId", company);
										map.put("statusId", null);
										map.put("userLogin", userLogin);
										map.put("facilityId", facilityId);
										map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
										map.putAll(attributes);
										try {
                                            map.put("isImportTrade", InvoiceWorker.isImportTrade(dispatcher, company, orderHeader.getString("currencyUom"), userLogin));
                                            dispatcher.runSync("receiveInventoryProduct", map);
										} catch (GenericServiceException e) {
											Debug.logError(e.toString(), module);
											return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
										}
									}
								}
							}
						}
						if (quantityFree.compareTo(BigDecimal.ZERO) < 0){
							return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseDelivery - quantity not true!");
						} else if (quantityFree.compareTo(BigDecimal.ZERO) > 0){
							map.put("orderId", orderId);
							map.put("orderItemSeqId", orderItemSeqId);
							map.put("productId", productId);
							map.put("shipmentItemSeqId", shipmentItemSeqId);
							map.put("shipmentId", shipmentId);
							map.put("quantityAccepted", quantityFree);
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								map.put("amountAccepted", quantityFree);
								map.put("quantityAccepted", BigDecimal.ONE);
							} 
							map.put("quantityExcess", BigDecimal.ZERO);
							map.put("quantityRejected", BigDecimal.ZERO);
							map.put("quantityQualityAssurance", BigDecimal.ZERO);
							map.put("ownerPartyId", company);
							map.put("statusId", null);
							map.put("userLogin", userLogin);
							map.put("facilityId", facilityId);
							map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
                            try {
                                map.put("isImportTrade", InvoiceWorker.isImportTrade(dispatcher, company, orderHeader.getString("currencyUom"), userLogin));
                                dispatcher.runSync("receiveInventoryProduct", map);
							} catch (GenericServiceException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
						}
					} else {
						map.put("orderId", orderId);
						map.put("orderItemSeqId", orderItemSeqId);
						map.put("productId", productId);
						map.put("shipmentItemSeqId", shipmentItemSeqId);
						map.put("shipmentId", shipmentId);
						map.put("quantityAccepted", quantityShip);
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							map.put("amountAccepted", weightShip);
							map.put("quantityAccepted", BigDecimal.ONE);
						} 
						map.put("quantityExcess", BigDecimal.ZERO);
						map.put("quantityRejected", BigDecimal.ZERO);
						map.put("quantityQualityAssurance", BigDecimal.ZERO);
						map.put("ownerPartyId", company);
						map.put("statusId", null);
						map.put("userLogin", userLogin);
						map.put("facilityId", facilityId);
						map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
                        try {
                            map.put("isImportTrade", InvoiceWorker.isImportTrade(dispatcher, company, orderHeader.getString("currencyUom"), userLogin));
                            dispatcher.runSync("receiveInventoryProduct", map);
						} catch (GenericServiceException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
						}
					}
				}
			}
		} else {
			String errMsg = "OLBIUS: Fatal error when findOne Delivery: Cannot receive with status" + objDelivery.getString("statusId");
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("deliveryId", deliveryId);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveProductFromTransferDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String deliveryId = (String)context.get("deliveryId");
		GenericValue objDelivery = null;
		if (UtilValidate.isNotEmpty(deliveryId)) {
			try {
				objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Delivery: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		if (UtilValidate.isEmpty(objDelivery)) {
			String errMsg = "OLBIUS: Fatal error when receiveProductFromTransferDelivery: Delivery not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String originFacilityId = objDelivery.getString("originFacilityId");
		String facilityId = objDelivery.getString("destFacilityId");
		String contactMechId = objDelivery.getString("destContactMechId");
		if (UtilValidate.isEmpty(facilityId)) {
			String errMsg = "OLBIUS: Fatal error when receiveProductFromTransferDelivery: destFacilityId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			String errMsg = "OLBIUS: Fatal error when findOne Delivery: destContactMechId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		String listItems = null;
		if (UtilValidate.isNotEmpty(context.get("listDeliveryItems"))) {
			listItems = (String)context.get("listDeliveryItems");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		String transferId = objDelivery.getString("transferId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromTransferDelivery - JqxWidgetSevices.convert error!");
		}
		
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromTransferDelivery - JqxWidgetSevices.convert error!");
			}
		}
		
		if (!listProducts.isEmpty() && "DLV_EXPORTED".equals(objDelivery.getString("statusId")) 
				&& UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			
			Map<String, Object> mapAttributes = FastMap.newInstance();
			for (Map<String, Object> dlvItem : listProducts){
				String transferItemSeqId = null;
				if (dlvItem.containsKey("transferItemSeqId")){
					transferItemSeqId = (String)dlvItem.get("transferItemSeqId");
				}
				String productId = null;
				if (dlvItem.containsKey("productId")){
					productId = (String)dlvItem.get("productId");
				}
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (dlvItem.containsKey("quantity")){
					quantityStr = (String)dlvItem.get("quantity");
					quantity = new BigDecimal(quantityStr);
				}
				
				if (!listProductAttrs.isEmpty()) {
					List<Map<String, Object>> listAttributes = FastList.newInstance();
					for (Map<String, Object> map : listProductAttrs) {
						if (map.containsKey("productId")){
							String prId = (String)map.get("productId");
							if (UtilValidate.isNotEmpty(prId) && productId.equals(prId)) {
								listAttributes.add(map);
							}
						}
					}
					mapAttributes.put(productId, listAttributes);
				}
				
				Observer o = new DeliveryObserver();
				ItemSubject is = new DeliveryItemSubject();
				is.attach(o);
				
				if (UtilValidate.isNotEmpty(productId)) {
					if (UtilValidate.isNotEmpty(transferItemSeqId)) {
						boolean isWeight = ProductUtil.isWeightProduct(delegator, productId);
						List<GenericValue> listDeliveryItems = FastList.newInstance();
						try {
							listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromTransferItemSeqId", transferItemSeqId)), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						BigDecimal quantityRemain = quantity;
						for (GenericValue dlvItemDB : listDeliveryItems) {
							BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
							BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
							BigDecimal quantityTmp = BigDecimal.ZERO;
							BigDecimal actualExportedQuantity = dlvItemDB.getBigDecimal("actualExportedQuantity");
							if (isWeight) actualExportedQuantity = dlvItemDB.getBigDecimal("actualExportedAmount");
							if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
							if (quantityRemain.compareTo(actualExportedQuantity) > 0){
								quantityRemain = quantityRemain.subtract(actualExportedQuantity);
								quantityTmp = actualExportedQuantity;
							} else if (quantityRemain.compareTo(actualExportedQuantity) == 0){
								quantityTmp = quantityRemain;
								quantityRemain = BigDecimal.ZERO;
							} else {
								quantityTmp = quantityRemain;
								quantityRemain = BigDecimal.ZERO;
							}
							
							if (isWeight){
								actualDeliveredQuantity = BigDecimal.ONE;
								actualDeliveredAmount = quantityTmp;
								if (actualDeliveredAmount.compareTo(actualExportedQuantity) > 0){
									return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLProductHasBeenReceived", (Locale)context.get("locale")));
								}
							} else {
								actualDeliveredQuantity = quantityTmp;
								if (actualDeliveredQuantity.compareTo(actualExportedQuantity) > 0){
									return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLProductHasBeenReceived", (Locale)context.get("locale")));
								}
							}
							
							Map<String, Object> updateItem = FastMap.newInstance();
		    				updateItem.put("deliveryId", deliveryId);
		    				updateItem.put("deliveryItemSeqId", dlvItemDB.getString("deliveryItemSeqId"));
		    				updateItem.put("actualDeliveredQuantity", actualDeliveredQuantity);
		    				updateItem.put("delegator", delegator);
		    				try {
								is.updateDeliveredQuantity(updateItem);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when updateDeliveredQuantity DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
		    				
		    				updateItem = FastMap.newInstance();
		    				updateItem.put("deliveryId", deliveryId);
		    				updateItem.put("deliveryItemSeqId", dlvItemDB.getString("deliveryItemSeqId"));
		    				updateItem.put("actualDeliveredAmount", actualDeliveredAmount);
		    				updateItem.put("delegator", delegator);
		    				try {
		    					is.updateDeliveredAmount(updateItem);
		    				} catch (GenericEntityException e) {
		    					String errMsg = "OLBIUS: Fatal error when updateDeliveredAmount DeliveryItem: " + e.toString();
		    					Debug.logError(e, errMsg, module);
		    					return ServiceUtil.returnError(errMsg);
		    				}
						}
					} 
				}
			}
			
			Map<String, Object> map = FastMap.newInstance();
			map.put("deliveryId", deliveryId);
			map.put("statusId", "DLV_DELIVERED");
			map.put("userLogin", userLogin);
			try {
				Map<String, Object> mapReturn = dispatcher.runSync("changeDeliveryStatus", map);
				if (ServiceUtil.isError(mapReturn)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when runSync changeDeliveryStatus: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			Map<String, Object> shipmentParam = FastMap.newInstance();
			shipmentParam.put("deliveryId", deliveryId);
			shipmentParam.put("userLogin", userLogin);
			Map<String, Object> mapShipment = FastMap.newInstance();
			String shipmentId = null;
			try {
				mapShipment = dispatcher.runSync("createShipmentToReceiveForTransferDelivery", shipmentParam);
				shipmentId = (String)mapShipment.get("shipmentId");
			} catch (GenericServiceException e1) {
				return ServiceUtil.returnError("createShipmentToReceiveForTransferDelivery Error!");
			}
			
			List<GenericValue> listTransferShipments = FastList.newInstance();
			EntityCondition cond2 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond2);
			try {
				listTransferShipments = delegator.findList("TransferShipment", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList TransferShipment error!");
			}
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			if (UtilValidate.isNotEmpty(listTransferShipments)) {
				for (GenericValue item : listTransferShipments) {
					
					BigDecimal quantityShip = item.getBigDecimal("quantity");
					BigDecimal weightShip = item.getBigDecimal("amount");
					GenericValue objShipmentItem = null;
					try {
						objShipmentItem = delegator.findOne("ShipmentItem", false, UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.get("shipmentItemSeqId")));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne ShipmentItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					String productId = objShipmentItem.getString("productId");
					
					List<Map<String, Object>> listAttribues = FastList.newInstance();
					if (mapAttributes.containsKey(productId)){
						listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
					}
					
					// get cost
					String transferItemSeqId = item.getString("transferItemSeqId");
					BigDecimal unitPrice = TransferReadHepler.getAverageCostProductExportedByDelivery(delegator, deliveryId, productId, originFacilityId, null, company);
					
					map = FastMap.newInstance();
					map.put("unitCost", unitPrice);
					String shipmentItemSeqId = item.getString("shipmentItemSeqId");
					
					if (!listAttribues.isEmpty()){
						// xu ly nhap sp co HSD, NSX, Lo SX
						BigDecimal quantityFree = quantityShip;
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							quantityFree = weightShip;
						} 
						
						for (Map<String, Object> mapAttr : listAttribues) {
							Map<String, Object> attributes = FastMap.newInstance();
							String qtyStr = null;
							if (mapAttr.containsKey("quantity") && mapAttr.containsKey("productId")){
								qtyStr = (String)mapAttr.get("quantity");
								if (UtilValidate.isNotEmpty(qtyStr)) {
									BigDecimal quantityP = new BigDecimal(qtyStr); // quantity tuong ung se la can nang voi san pham can nang
									quantityFree = quantityFree.subtract(quantityP);
									if (quantityP.compareTo(BigDecimal.ZERO) > 0){
										for (String key : mapAttr.keySet()) {
											if ("expireDate".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("expireDate"))) {
													String expStr = (String)mapAttr.get("expireDate");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("datetimeManufactured".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("datetimeManufactured"))) {
													String expStr = (String)mapAttr.get("datetimeManufactured");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("lotId".equals(key)){ 
												String lotId = (String)mapAttr.get(key);
												GenericValue objLot = null;
												try {
													objLot = delegator.findOne("Lot", false,
															UtilMisc.toMap("lotId", lotId));
												} catch (GenericEntityException e) {
													Debug.logError(e.toString(), module);
													return ServiceUtil.returnError("OLBIUS: findOne Lot error!");
												}
												if (UtilValidate.isEmpty(objLot)) {
													// create new lot
													objLot = delegator.makeValue("Lot");
													objLot.put("lotId", lotId);
													objLot.put("creationDate", UtilDateTime.nowTimestamp());
													delegator.create(objLot); 
												}
												attributes.put(key, lotId);
											} else {
												attributes.put(key, mapAttr.get(key));
											}
										}
										
										map.put("transferId", transferId);
										map.put("transferItemSeqId", transferItemSeqId);
										map.put("productId", productId);
										map.put("shipmentItemSeqId", shipmentItemSeqId);
										map.put("shipmentId", shipmentId);
										map.put("quantityAccepted", quantityP);
										if (ProductUtil.isWeightProduct(delegator, productId)) {
											map.put("amountAccepted", quantityP);
											map.put("quantityAccepted", BigDecimal.ONE);
										} 
										map.put("quantityExcess", BigDecimal.ZERO);
										map.put("quantityRejected", BigDecimal.ZERO);
										map.put("quantityQualityAssurance", BigDecimal.ZERO);
										map.put("ownerPartyId", company);
										map.put("statusId", null);
										map.put("userLogin", system);
										map.put("facilityId", facilityId);
										map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
										map.putAll(attributes);
										try {
											dispatcher.runSync("receiveInventoryProduct", map);
										} catch (GenericServiceException e) {
											Debug.logError(e.toString(), module);
											return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
										}
									}
								}
							}
						}
						if (quantityFree.compareTo(BigDecimal.ZERO) < 0){
							return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - quantity not true!");
						} else if (quantityFree.compareTo(BigDecimal.ZERO) > 0){
							map.put("transferId", transferId);
							map.put("transferItemSeqId", transferItemSeqId);
							map.put("productId", productId);
							map.put("shipmentItemSeqId", shipmentItemSeqId);
							map.put("shipmentId", shipmentId);
							map.put("quantityAccepted", quantityFree);
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								map.put("amountAccepted", quantityFree);
								map.put("quantityAccepted", BigDecimal.ONE);
							} 
							map.put("quantityExcess", BigDecimal.ZERO);
							map.put("quantityRejected", BigDecimal.ZERO);
							map.put("quantityQualityAssurance", BigDecimal.ZERO);
							map.put("ownerPartyId", company);
							map.put("statusId", null);
							map.put("userLogin", system);
							map.put("facilityId", facilityId);
							map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							try {
								dispatcher.runSync("receiveInventoryProduct", map);
							} catch (GenericServiceException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
						}
					} else {
						map.put("transferId", transferId);
						map.put("transferItemSeqId", transferItemSeqId);
						map.put("productId", productId);
						map.put("shipmentItemSeqId", shipmentItemSeqId);
						map.put("shipmentId", shipmentId);
						map.put("quantityAccepted", quantityShip);
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							map.put("amountAccepted", weightShip);
							map.put("quantityAccepted", BigDecimal.ONE);
						} 
						map.put("quantityExcess", BigDecimal.ZERO);
						map.put("quantityRejected", BigDecimal.ZERO);
						map.put("quantityQualityAssurance", BigDecimal.ZERO);
						map.put("ownerPartyId", company);
						map.put("statusId", null);
						map.put("userLogin", system);
						map.put("facilityId", facilityId);
						map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						try {
							dispatcher.runSync("receiveInventoryProduct", map);
						} catch (GenericServiceException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
						}
					}
					
					//  update transferItem
					String statusNew = TransferReadHepler.checkTransferItemStatus(delegator, transferId, transferItemSeqId);
					GenericValue objTransferItem = null;
					try {
						objTransferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne TransferItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if ("TRANS_ITEM_DELIVERED".equals(statusNew) && !statusNew.equals(objTransferItem.getString("statusId"))){
						Map<String, Object> mapChangeStatus = FastMap.newInstance();
						mapChangeStatus.put("transferId", transferId);
						mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
						mapChangeStatus.put("fromStatusId", objTransferItem.getString("statusId"));
						mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
						mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							mapChangeStatus.put("statusId", "TRANS_ITEM_DELIVERED");
							dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("Change trasfer item status Error!");
						}
						
						// change to complete 
						mapChangeStatus = FastMap.newInstance();
						mapChangeStatus.put("transferId", transferId);
						mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
						mapChangeStatus.put("fromStatusId", "TRANS_ITEM_DELIVERED");
						mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
						mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							mapChangeStatus.put("statusId", "TRANS_ITEM_COMPLETED");
							dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("Change trasfer item status Error!");
						}
					}
				}
			}
		} else {
			String errMsg = "OLBIUS: Fatal error when findOne Delivery: Cannot receive with status" + objDelivery.getString("statusId");
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("deliveryId", deliveryId);
		return mapReturn;
	}
	
	public static BigDecimal getReceiveCost (Delegator delegator, LocalDispatcher dispatcher, String company, String orderId, String orderItemSeqId, String deliveryId) throws GenericEntityException{
		// Process exchange rate by VietTB
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		String currencyUom = orderHeader.getString("currencyUom");
		GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", company));
		String baseCurrencyUomId = partyAcctgPreference.getString("baseCurrencyUomId");
		// Calculate price by amount
		GenericValue objOrderItem = null;
		try {
			objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		} catch (GenericEntityException e) {
			return BigDecimal.ZERO;
		}
		BigDecimal unitPrice = objOrderItem.getBigDecimal("unitPrice");
		String productId = objOrderItem.getString("productId");
		if (ProductUtil.isWeightProduct(delegator, productId)) {
			BigDecimal amount = objOrderItem.getBigDecimal("selectedAmount");
			BigDecimal unitPriceInit = objOrderItem.getBigDecimal("unitPrice");
			unitPrice = unitPriceInit.divide(amount, decimals, rounding);
		}
		BigDecimal totalPaymentApplied = BigDecimal.ZERO;
		BigDecimal totalDeliveredItem = BigDecimal.ZERO;
		BigDecimal totalDeliveryItem = BigDecimal.ZERO;
		BigDecimal avgExchangeRate = BigDecimal.ZERO;
		BigDecimal avgUnitPrice = BigDecimal.ZERO;
		List<GenericValue> listPayment = null;
        try {
        	List<String> paymentStatusId = new FastList<String>();
        	paymentStatusId.add("PMNT_SENT");
        	paymentStatusId.add("PMNT_CONFIRMED");
        	EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                 EntityCondition.makeCondition("statusId", EntityOperator.IN, paymentStatusId),
                     EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.IN, com.olbius.accounting.invoice.InvoiceWorker.getOrderPaymentPreferenceIds(delegator, orderId))),
                     EntityOperator.AND);
        	listPayment = delegator.findList("PaymentAcctgTrans", condition, null, UtilMisc.toList("transactionDate"), null, false);
        	
        	if (UtilValidate.isNotEmpty(listPayment))
        	{
	        	for (GenericValue payment : listPayment)
	        	{
	        		totalPaymentApplied = totalPaymentApplied.add(payment.getBigDecimal("amount"));
	        	}
        	}
        	
        	EntityConditionList<EntityExpr> conditionDelivery = EntityCondition.makeCondition(UtilMisc.toList(
                     EntityCondition.makeCondition("fromOrderId", EntityOperator.EQUALS, orderId),
                     EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_DELIVERED"),
	                     EntityCondition.makeCondition("deliveryId", EntityOperator.NOT_EQUAL, deliveryId )),
	                     EntityOperator.AND);
        	List<GenericValue> listDeliveredItem = delegator.findList("DeliveryItemView", conditionDelivery, null, null, null, false);
        	
        	if (UtilValidate.isNotEmpty(listDeliveredItem))
        	{
	        	for (GenericValue deliveredItem : listDeliveredItem)
	        	{
	        		String productDiId = (String)deliveredItem.get("productId");
					GenericValue objProductDi = delegator.findOne("Product", false, UtilMisc.toMap("productId", productDiId));
					
					BigDecimal unitDiPrice = deliveredItem.getBigDecimal("unitPrice");
					if (UtilValidate.isNotEmpty(objProductDi.get("requireAmount")) && "Y".equals(objProductDi.getString("requireAmount"))) {
						BigDecimal amountDi = deliveredItem.getBigDecimal("selectedAmount");
						BigDecimal unitDiPriceInit = deliveredItem.getBigDecimal("unitPrice");
						unitDiPrice = unitDiPriceInit.divide(amountDi, decimals, rounding);
					}			
					
	        		BigDecimal amount = unitDiPrice.multiply(deliveredItem.getBigDecimal("actualDeliveredQuantity"));
	        		totalDeliveredItem = totalDeliveredItem.add(amount);
	        	}
        	}	
        	
        	List<GenericValue> listItemDelivery = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
        	
        	for (GenericValue deliveryItem : listItemDelivery)
        	{
        								        		
        		String productDiId = (String)deliveryItem.get("productId");
				GenericValue objProductDi = delegator.findOne("Product", false, UtilMisc.toMap("productId", productDiId));
				
				BigDecimal unitDiPrice = deliveryItem.getBigDecimal("unitPrice");
				if (UtilValidate.isNotEmpty(objProductDi.get("requireAmount")) && "Y".equals(objProductDi.getString("requireAmount"))) {
					BigDecimal amountDi = deliveryItem.getBigDecimal("selectedAmount");
					BigDecimal unitDiPriceInit = deliveryItem.getBigDecimal("unitPrice");
					unitDiPrice = unitDiPriceInit.divide(amountDi, decimals, rounding);
				}
				
        		BigDecimal amount = unitDiPrice.multiply(deliveryItem.getBigDecimal("actualDeliveredQuantity"));
        		totalDeliveryItem = totalDeliveryItem.add(amount);
        	}
        		BigDecimal deliveredItemDis = totalDeliveredItem; 
        		BigDecimal deliveryItemDis = totalDeliveryItem; 
        		BigDecimal totalAmount = BigDecimal.ZERO;
        		String strBankId = "";
        		Timestamp dtTransactionDate ;
        		int i = 0;
        		if (UtilValidate.isNotEmpty(listPayment))
        		{
        			GenericValue payment = listPayment.get(i);
        			BigDecimal paymentAmount = payment.getBigDecimal("amount");
	        		while (deliveredItemDis.compareTo(paymentAmount) > 0) 
	        		{
	        			deliveredItemDis = deliveredItemDis.subtract(paymentAmount);
	        			i++;
	        			payment = listPayment.get(i);
	        			paymentAmount = payment.getBigDecimal("amount");
	        		}
	        		BigDecimal  paymentAmountDis = paymentAmount.subtract(deliveredItemDis);
	        		BigDecimal convertValued = BigDecimal.ZERO;
	        		while (deliveryItemDis.compareTo(BigDecimal.ZERO) > 0)
	        		{
	        			deliveryItemDis = deliveryItemDis.subtract(paymentAmountDis);
	        			String strPaymentMethodID = payment.getString("paymentMethodId");
	        			dtTransactionDate = payment.getTimestamp("transactionDate");
	        			if (strPaymentMethodID != null)
	        			{
	        				GenericValue paymentMethod = delegator.findOne("PaymentMethod", false, UtilMisc.toMap("paymentMethodId", strPaymentMethodID));
	        				if (paymentMethod != null && !paymentMethod.isEmpty())
	        				{
	        					String strFinAccountId = paymentMethod.getString("finAccountId");
	        					GenericValue finAccount = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", strFinAccountId));
	        					if (finAccount != null && !finAccount.isEmpty())
	        					{
	        						strBankId = finAccount.getString("bankId");
	        					}
	        				}
	        			}
	        			if (deliveryItemDis.compareTo(BigDecimal.ZERO) < 0 )
	        			{
	        				convertValued = paymentAmountDis.add(deliveryItemDis);
	        			} else convertValued = paymentAmountDis;
	        			
	        			Map<String, Object> priceResults = FastMap.newInstance();
                        try {
                            priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", convertValued , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
                            if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
                            } 
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                        }
                            
	        			totalAmount = totalAmount.add( (BigDecimal)priceResults.get("convertedValue"));
	        			i++;
	        			if (i==listPayment.size())
	        			{
	        				if (deliveryItemDis.compareTo(BigDecimal.ZERO) > 0)
	        				{
	        					dtTransactionDate = null;
	                            try {
	                                priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", deliveryItemDis , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
	                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
	                                    Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
	                                } 
	                            } catch (GenericServiceException e) {
	                                Debug.logError(e, module);
	                            }
	                            totalAmount = totalAmount.add( (BigDecimal)priceResults.get("convertedValue"));
	        				}
	        				break;
	        			}
	        			payment = listPayment.get(i);
	        			paymentAmountDis = payment.getBigDecimal("amount");
	        		}
	        		avgExchangeRate = totalAmount.divide(totalDeliveryItem, decimals, rounding );
	        		avgUnitPrice = unitPrice.multiply(avgExchangeRate).setScale(decimals, rounding);
        		}
        		else
        		{
        			List<GenericValue> listAllPayment = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.IN, com.olbius.accounting.invoice.InvoiceWorker.getOrderPaymentPreferenceIds(delegator, orderId)), null, UtilMisc.toList("effectiveDate"), null, false);
        			if (UtilValidate.isNotEmpty(listAllPayment))
        			{
        				GenericValue paymentAll = listAllPayment.get(0);
        				dtTransactionDate = null;
        				String strPaymentMethodID = paymentAll.getString("paymentMethodId");
        				if (strPaymentMethodID != null)
	        			{
	        				GenericValue paymentMethod = delegator.findOne("PaymentMethod", false, UtilMisc.toMap("paymentMethodId", strPaymentMethodID));
	        				if (paymentMethod != null && !paymentMethod.isEmpty())
	        				{
	        					String strFinAccountId = paymentMethod.getString("finAccountId");
	        					GenericValue finAccount = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", strFinAccountId));
	        					if (finAccount != null && !finAccount.isEmpty())
	        					{
	        						strBankId = finAccount.getString("bankId");
	        					}
	        				}
	        			}
        				Map<String, Object> priceResults = FastMap.newInstance();
                        try {
                            priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", unitPrice , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
                            if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
                            } 
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                        }
                        avgUnitPrice = (BigDecimal)priceResults.get("convertedValue");
        			}
        			else {
        			    GenericValue finAccount = EntityUtil.getFirst(delegator.findList("FinAccount", EntityCondition.makeCondition("ownerPartyId", company), null, null, null, false));
        			    strBankId = finAccount.getString("bankId");
                        Map<String, Object> priceResults = FastMap.newInstance();
                        try {
                            priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", unitPrice , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", null, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
                            if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
                            }
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                        }
                        avgUnitPrice = (BigDecimal)priceResults.get("convertedValue");
                    }
        		}
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting Payment list", module);
        }				
		return avgUnitPrice;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveProductFromPurchaseShipmentDis(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String deliveryId = (String)context.get("deliveryId");
    	GenericValue objDelivery = null;
    	if (UtilValidate.isNotEmpty(deliveryId)) {
			try {
				objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Delivery: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
    	if (UtilValidate.isEmpty(objDelivery)) {
    		String errMsg = "OLBIUS: Fatal error when receiveProductFromPurchaseShipmentDis: Delivery not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	String facilityId = (String)context.get("facilityId");
    	String contactMechId = null; 
    	if (UtilValidate.isEmpty(facilityId)) {
    		String errMsg = "OLBIUS: Fatal error when receiveProductFromPurchaseShipmentDis: destFacilityId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	contactMechId = objDelivery.getString("destContactMechId");
    	if (UtilValidate.isEmpty(contactMechId)) {
    		List<GenericValue> listCtms = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "SHIPPING_LOCATION");
    		if (!listCtms.isEmpty()) contactMechId = listCtms.get(0).getString("contactMechId");
    	}
    	if (UtilValidate.isEmpty(contactMechId)) {
    		String errMsg = "OLBIUS: Fatal error when findOne Delivery: destContactMechId not found" + deliveryId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	
		String listItems = null;
		if (UtilValidate.isNotEmpty(context.get("listDeliveryItems"))) {
			listItems = (String)context.get("listDeliveryItems");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		String orderId = objDelivery.getString("orderId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = userLogin.getString("partyId");
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseShipmentDis - JqxWidgetSevices.convert error!");
		}
		
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseShipmentDis - JqxWidgetSevices.convert error!");
			}
		}
		
		if (!listProducts.isEmpty() && ("DLV_DELIVERED".equals(objDelivery.getString("statusId")) || "DLV_EXPORTED".equals(objDelivery.getString("statusId"))) 
				&& UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			
			Map<String, Object> mapAttributes = FastMap.newInstance();
			List<Map<String, Object>> listTmps = FastList.newInstance();
			for (Map<String, Object> dlvItem : listProducts){
				Map<String, Object> xx = FastMap.newInstance();
				String productId = null;
				if (dlvItem.containsKey("productId")){
					productId = (String)dlvItem.get("productId");
					xx.put("productId", productId);
				}
				String orderItemSeqId = null;
				if (dlvItem.containsKey("orderItemSeqId")){
					orderItemSeqId = (String)dlvItem.get("orderItemSeqId");
					xx.put("orderItemSeqId", orderItemSeqId);
				}
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (dlvItem.containsKey("quantity")){
					quantityStr = (String)dlvItem.get("quantity");
					quantity = new BigDecimal(quantityStr);
				}
				xx.put("quantity", quantity);
				xx.put("orderId", orderId);
				listTmps.add(xx);
				
				if (!listProductAttrs.isEmpty()) {
					List<Map<String, Object>> listAttributes = FastList.newInstance();
					for (Map<String, Object> map : listProductAttrs) {
						if (map.containsKey("orderItemSeqId")){
							String prId = (String)map.get("orderItemSeqId");
							if (UtilValidate.isNotEmpty(prId) && orderItemSeqId.equals(prId)) {
								listAttributes.add(map);
							}
						}
					}
					mapAttributes.put(orderItemSeqId, listAttributes);
				}
			}
			Map<String, Object> map = FastMap.newInstance();
			
			Map<String, Object> shipmentParam = FastMap.newInstance();
			shipmentParam.put("deliveryId", deliveryId);
			shipmentParam.put("facilityId", facilityId);
			shipmentParam.put("contactMechId", contactMechId);
			shipmentParam.put("listProducts", listTmps);
			shipmentParam.put("userLogin", userLogin);
			Map<String, Object> mapShipment = FastMap.newInstance();
			String shipmentId = null;
			try {
				mapShipment = dispatcher.runSync("createShipmentForPurchaseDeliveryDistributor", shipmentParam);
				shipmentId = (String)mapShipment.get("shipmentId");
			} catch (GenericServiceException e1) {
				return ServiceUtil.returnError("createShipmentForPurchaseDelivery Error!");
			}
            objDelivery.put("shipmentDistributorId", shipmentId);
            objDelivery.store();
			
			List<GenericValue> listOrderShipments = FastList.newInstance();
			EntityCondition cond2 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond2);
			try {
				listOrderShipments = delegator.findList("OrderShipment", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList OrderShipment error!");
			}
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			if (UtilValidate.isNotEmpty(listOrderShipments)) {
				for (GenericValue item : listOrderShipments) {
					String orderItemSeqId = item.getString("orderItemSeqId");
					BigDecimal quantityShip = item.getBigDecimal("quantity");
					BigDecimal weightShip = item.getBigDecimal("weight");
					GenericValue objShipmentItem = null;
					try {
						objShipmentItem = delegator.findOne("ShipmentItem", false, UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.get("shipmentItemSeqId")));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne ShipmentItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					String productId = objShipmentItem.getString("productId");
					
					List<Map<String, Object>> listAttribues = FastList.newInstance();
					if (mapAttributes.containsKey(orderItemSeqId)){
						listAttribues = (List<Map<String, Object>>)mapAttributes.get(orderItemSeqId);
					}
					
					// get cost
					BigDecimal unitPrice = BigDecimal.ZERO;
					// Calculate price by amount
					GenericValue objOrderItem = null;
					try {
						objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne OrderItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					unitPrice = objOrderItem.getBigDecimal("unitPrice");
					if (ProductUtil.isWeightProduct(delegator, productId)) {
						BigDecimal amount = objOrderItem.getBigDecimal("selectedAmount");
						BigDecimal unitPriceInit = objOrderItem.getBigDecimal("unitPrice");
						unitPrice = unitPriceInit.divide(amount, decimals, rounding);
					}
					
					map = FastMap.newInstance();
					map.put("unitCost", unitPrice);
					String shipmentItemSeqId = item.getString("shipmentItemSeqId");
					
					if (!listAttribues.isEmpty()){
						// xu ly nhap sp co HSD, NSX, Lo SX
						BigDecimal quantityFree = quantityShip;
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							quantityFree = weightShip;
						} 
						
						for (Map<String, Object> mapAttr : listAttribues) {
							Map<String, Object> attributes = FastMap.newInstance();
							String qtyStr = null;
							if (mapAttr.containsKey("quantity") && mapAttr.containsKey("productId")){
								qtyStr = (String)mapAttr.get("quantity");
								if (UtilValidate.isNotEmpty(qtyStr)) {
									BigDecimal quantityP = new BigDecimal(qtyStr); // quantity tuong ung se la can nang voi san pham can nang
									quantityFree = quantityFree.subtract(quantityP);
									if (quantityP.compareTo(BigDecimal.ZERO) > 0){
										for (String key : mapAttr.keySet()) {
											if ("expireDate".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("expireDate"))) {
													String expStr = (String)mapAttr.get("expireDate");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("datetimeManufactured".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("datetimeManufactured"))) {
													String expStr = (String)mapAttr.get("datetimeManufactured");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("lotId".equals(key)){ 
												String lotId = (String)mapAttr.get(key);
												GenericValue objLot = null;
												try {
													objLot = delegator.findOne("Lot", false,
															UtilMisc.toMap("lotId", lotId));
												} catch (GenericEntityException e) {
													Debug.logError(e.toString(), module);
													return ServiceUtil.returnError("OLBIUS: findOne Lot error!");
												}
												if (UtilValidate.isEmpty(objLot)) {
													// create new lot
													objLot = delegator.makeValue("Lot");
													objLot.put("lotId", lotId);
													objLot.put("creationDate", UtilDateTime.nowTimestamp());
													delegator.create(objLot); 
												}
												attributes.put(key, lotId);
											} else {
												attributes.put(key, mapAttr.get(key));
											}
										}
										
										map.put("orderId", orderId);
										map.put("orderItemSeqId", orderItemSeqId);
										map.put("productId", productId);
										map.put("shipmentItemSeqId", shipmentItemSeqId);
										map.put("shipmentId", shipmentId);
										map.put("quantityAccepted", quantityP);
										if (ProductUtil.isWeightProduct(delegator, productId)) {
											map.put("amountAccepted", quantityP);
											map.put("quantityAccepted", BigDecimal.ONE);
										} 
										map.put("quantityExcess", BigDecimal.ZERO);
										map.put("quantityRejected", BigDecimal.ZERO);
										map.put("quantityQualityAssurance", BigDecimal.ZERO);
										map.put("ownerPartyId", company);
										map.put("statusId", null);
										map.put("userLogin", system);
										map.put("facilityId", facilityId);
										map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
										map.putAll(attributes);
										try {
											dispatcher.runSync("receiveInventoryProduct", map);
										} catch (GenericServiceException e) {
											Debug.logError(e.toString(), module);
											return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
										}
									}
								}
							}
						}
						if (quantityFree.compareTo(BigDecimal.ZERO) < 0){
							return ServiceUtil.returnError("OLBIUS: receiveProductFromPurchaseShipmentDis - quantity not true!");
						} else if (quantityFree.compareTo(BigDecimal.ZERO) > 0){
							map.put("orderId", orderId);
							map.put("orderItemSeqId", orderItemSeqId);
							map.put("productId", productId);
							map.put("shipmentItemSeqId", shipmentItemSeqId);
							map.put("shipmentId", shipmentId);
							map.put("quantityAccepted", quantityFree);
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								map.put("amountAccepted", quantityFree);
								map.put("quantityAccepted", BigDecimal.ONE);
							} 
							map.put("quantityExcess", BigDecimal.ZERO);
							map.put("quantityRejected", BigDecimal.ZERO);
							map.put("quantityQualityAssurance", BigDecimal.ZERO);
							map.put("ownerPartyId", company);
							map.put("statusId", null);
							map.put("userLogin", system);
							map.put("facilityId", facilityId);
							map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							try {
								dispatcher.runSync("receiveInventoryProduct", map);
							} catch (GenericServiceException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
						}
					} else {
						map.put("orderId", orderId);
						map.put("orderItemSeqId", orderItemSeqId);
						map.put("productId", productId);
						map.put("shipmentItemSeqId", shipmentItemSeqId);
						map.put("shipmentId", shipmentId);
						map.put("quantityAccepted", quantityShip);
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							map.put("amountAccepted", quantityShip);
							map.put("quantityAccepted", BigDecimal.ONE);
						} 
						map.put("quantityExcess", BigDecimal.ZERO);
						map.put("quantityRejected", BigDecimal.ZERO);
						map.put("quantityQualityAssurance", BigDecimal.ZERO);
						map.put("ownerPartyId", company);
						map.put("statusId", null);
						map.put("userLogin", system);
						map.put("facilityId", facilityId);
						map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						try {
							dispatcher.runSync("receiveInventoryProduct", map);
						} catch (GenericServiceException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
						}
					}
				}
			}
		} else {
			String errMsg = "OLBIUS: Fatal error receiveProductFromPurchaseShipmentDis: Cannot receive with status" + objDelivery.getString("statusId");
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("deliveryId", deliveryId);
		return mapReturn;
	}
}
