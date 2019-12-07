package com.olbius.baselogistics.inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ExportServices {
	
	public static final String module = ExportServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportProductFromTransferDelivery(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		String deliveryId = (String)context.get("deliveryId");
		String listItems = (String)context.get("listProducts");
		
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when findOne Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		delivery.set("actualStartDate", UtilDateTime.nowTimestamp());
		try {
			delegator.store(delivery);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when Store Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		String facilityId = delivery.getString("originFacilityId");
		String transferId = delivery.getString("transferId");
		String shipmentId = delivery.getString("shipmentId");
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: exportProductFromSalesDelivery - JqxWidgetSevices.convert error!");
		}
		

		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: exportProductFromSalesDelivery - JqxWidgetSevices.convert error!");
			}
		}
		
		Map<String, Object> mapAttributes = FastMap.newInstance();
		for (Map<String, Object> item : listProducts){
			String productId = (String)item.get("productId");
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
		}
		EntityCondition condDlv = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
		EntityCondition condOh = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
		Observer o = new DeliveryObserver();
		ItemSubject is = new DeliveryItemSubject();
		is.attach(o);
		for (Map<String, Object> item : listProducts){
			List<String> listTransferItemSeqIds = FastList.newInstance();
			String transferItemSeqId = (String)item.get("transferItemSeqId");
			if (!listTransferItemSeqIds.contains(transferItemSeqId)) listTransferItemSeqIds.add(transferItemSeqId);
			String productId = (String)item.get("productId");
			Boolean isWeight = false;
			if (ProductUtil.isWeightProduct(delegator, productId)){
				isWeight = true;
			}
			EntityCondition condPr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			
			String deliveryItemSeqId = null;
			EntityCondition cond2 = EntityCondition.makeCondition("fromTransferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(condDlv);
			conds.add(cond2);
			List<GenericValue> listDlvItems = FastList.newInstance();
			GenericValue deliveryItem = null;
			try {
				listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
				if (!listDlvItems.isEmpty()) {
					deliveryItemSeqId = listDlvItems.get(0).getString("deliveryItemSeqId");
					deliveryItem = EntityUtil.getFirst(listDlvItems);
				}
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			BigDecimal quantity = BigDecimal.ZERO;
			if (item.containsKey("quantity")){
				String quantityStr = (String)item.get("quantity");
				quantity = new BigDecimal(quantityStr);
			}
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				
				List<Map<String, Object>> listAttribues = FastList.newInstance();
				if (mapAttributes.containsKey(productId)){
					listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
				}
			
				List<Map<String, Object>> listInvs = FastList.newInstance();
				List<EntityCondition> condOis = FastList.newInstance();
				EntityCondition condStt = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_APPROVED");
				condOis.add(condPr);
				condOis.add(condStt);
				condOis.add(condDlv);
				
				if (!listAttribues.isEmpty()){
					for (Map<String, Object> map : listAttribues) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						
						String qtyStr = null;
						if (map.containsKey("quantity") && map.containsKey("productId")){
							qtyStr = (String)map.get("quantity");
							if (UtilValidate.isNotEmpty(qtyStr)) {
								BigDecimal quantityP = new BigDecimal(qtyStr);
								if (quantityP.compareTo(BigDecimal.ZERO) > 0){
									Boolean hasExp = false;
									Boolean hasMnf = false;
									for (String key : map.keySet()) {
										if ("expireDate".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("expireDate"))) {
												String expStr = (String)map.get("expireDate");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
															hasExp = true;
														}
													}
												}
											}
										} else if ("datetimeManufactured".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("datetimeManufactured"))) {
												String expStr = (String)map.get("datetimeManufactured");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
															hasMnf = true;
														}
													}
												}
											}
										} else {
											attributes.put(key, map.get(key));
										}
									}
									if (!hasExp){
										attributes.put("expireDate", null);
									}
									if (!hasMnf){
										attributes.put("datetimeManufactured", null);
									}
									List<Map<String, Object>> listInvTmps = FastList.newInstance();
									try {
										listInvTmps = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityP);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
									if (!listInvTmps.isEmpty()){
										for (Map<String, Object> inv : listInvTmps){
											inv.put("transferItemSeqId", transferItemSeqId);
											listInvs.add(inv);
										}
									}
								}
							}
						}
					}
				} else {
					if (UtilValidate.isNotEmpty(deliveryItem)) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						List<String> orderBy = FastList.newInstance();
						orderBy.add("expireDate");
						List<Map<String, Object>> listInvBasics = FastList.newInstance();
						try {
							listInvBasics = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantity, orderBy);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listInvBasics.isEmpty()) {
							for (Map<String, Object> map : listInvBasics) {
								map.put("transferItemSeqId", transferItemSeqId);
								listInvs.add(map);
							}
						}
					}
				}
				
				if (listInvs.isEmpty()) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				BigDecimal qohTotal = BigDecimal.ZERO;
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					qohTotal = qohTotal.add(qoh);
				}
				if (qohTotal.compareTo(quantity) < 0) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				
				List<GenericValue> listReservers = FastList.newInstance();
				EntityCondition condOiSeq = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.IN, listTransferItemSeqIds);
				List<EntityCondition> cond2s = FastList.newInstance();
				cond2s.add(condOh);
				cond2s.add(condOiSeq);
				try {
					listReservers = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(cond2s), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList TransferItemShipGrpInvRes: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listReservers.isEmpty()){
					// update old reserves
					for (String orSeqId : listTransferItemSeqIds) {
						BigDecimal remainQuantity = BigDecimal.ZERO;
						for (Map<String, Object> inv : listInvs) {
							String orSeqIdTmp = (String)inv.get("transferItemSeqId");
							if (orSeqId.equals(orSeqIdTmp) && UtilValidate.isNotEmpty(inv.get("quantity"))){
								remainQuantity = remainQuantity.add((BigDecimal)inv.get("quantity"));
							}
						}
						List<GenericValue> listTmps = FastList.newInstance();
						for (GenericValue res : listReservers) {
							if (res.getString("transferItemSeqId").equals(orSeqId)) {
								listTmps.add(res);
							}
						}
						if (listTmps.isEmpty()) continue;
						for (GenericValue res : listTmps) {
							BigDecimal quantityRes = res.getBigDecimal("quantity");
							if (isWeight) quantityRes = res.getBigDecimal("amount"); 
							if (quantityRes.compareTo(remainQuantity) <= 0){
								
								// tao detail bu ATP
								GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
				                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
				                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("availableToPromiseDiff", res.getBigDecimal("quantity"));
				                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("transferId", transferId);
				                tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
				                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
				                try {
									tmpInvDetail.create();
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
				                
				                try {
									delegator.removeValue(res);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Create TransferItemShipGrpInvRes: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
				                
								remainQuantity = remainQuantity.subtract(quantityRes);
							} else {
								if (isWeight) {
									res.put("amount", quantityRes.subtract(remainQuantity));
								} else {
									res.put("quantity", quantityRes.subtract(remainQuantity));
								}
								if (!isWeight) {
									BigDecimal quantityResNotAvai = res.getBigDecimal("quantityNotAvailable");
									if (UtilValidate.isNotEmpty(quantityResNotAvai)) {
										BigDecimal notAvai = quantityResNotAvai.subtract(remainQuantity);
										if (notAvai.compareTo(BigDecimal.ZERO) <= 0){
											notAvai = BigDecimal.ZERO;
										}
										res.put("quantityNotAvailable", notAvai);
									}
									
									GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
					                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
					                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
					                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
					                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
					                tmpInvDetail.set("availableToPromiseDiff", remainQuantity);
					                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
					                tmpInvDetail.set("transferId", transferId);
					                tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
					                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
					                try {
										tmpInvDetail.create();
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
								}
				                
								try {
									delegator.store(res);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Store TransferItemShipGrpInvRes: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
								remainQuantity = BigDecimal.ZERO;
							}
							if (remainQuantity.compareTo(BigDecimal.ZERO) < 0){
								break;
							}
						}
					}
				}
				
				BigDecimal createdQuantity = BigDecimal.ZERO;
				List<GenericValue> dlvInitItems = FastList.newInstance();
				if (!listTransferItemSeqIds.isEmpty()){
					EntityCondition condFromOi = EntityCondition.makeCondition("fromTransferItemSeqId", EntityOperator.IN, listTransferItemSeqIds);
					List<EntityCondition> cond5s = FastList.newInstance();
					cond5s.add(condDlv);
					cond5s.add(condFromOi);
					cond5s.add(condStt);
					try {
						dlvInitItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(cond5s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!dlvInitItems.isEmpty()){
						for (GenericValue x : dlvInitItems) {
							if (isWeight){
								createdQuantity = x.getBigDecimal("amount");
							} else {
								createdQuantity = x.getBigDecimal("quantity");
							}
						}
					}
				}
				
				BigDecimal remainQuantity = createdQuantity;
				List<String> listDlvItemIds = FastList.newInstance();
				for (Map<String, Object> map : listInvs) {
					String inventoryItemId = (String)map.get("inventoryItemId");
					String transferItemSeqIdTmp = (String)map.get("transferItemSeqId");
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					BigDecimal actualExportedQuantity = BigDecimal.ZERO;
					BigDecimal actualExportedAmount = BigDecimal.ZERO;
					actualExportedQuantity = qoh;
					if (isWeight){
						actualExportedAmount = actualExportedQuantity;
						actualExportedQuantity = BigDecimal.ONE;
					}
					
					// create reserves
					listReservers = FastList.newInstance();
					EntityCondition condinv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
					EntityCondition condSeq = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqIdTmp);
					List<EntityCondition> cond3s = FastList.newInstance();
					cond3s.add(condOh);
					cond3s.add(condSeq);
					cond3s.add(condinv);
					try {
						listReservers = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(cond3s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList TransferItemShipGrpInvRes: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (listReservers.isEmpty()){
						// create new
						GenericValue  tmpOISG = delegator.makeValue("TransferItemShipGrpInvRes");
		                tmpOISG.set("transferId", transferId);
		                tmpOISG.set("transferItemSeqId", transferItemSeqIdTmp);
		                tmpOISG.set("shipGroupSeqId", "00001"); // hard code
		                tmpOISG.set("inventoryItemId", inventoryItemId);
		                tmpOISG.set("reserveTransferEnumId", "INVRO_FIFO_REC");
		                tmpOISG.set("quantity", actualExportedQuantity);
		                if (isWeight) {
		                	tmpOISG.set("amount", actualExportedAmount);
		                }
		                tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
		                tmpOISG.set("reservedDatetime", UtilDateTime.nowTimestamp());
		                tmpOISG.set("createdDatetime", UtilDateTime.nowTimestamp());
		                tmpOISG.set("promisedDatetime", UtilDateTime.nowTimestamp());
		                try {
							tmpOISG.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Store TransferItemShipGrpInvRes: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
		                
		                GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", inventoryItemId);
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("transferId", transferId);
		                tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
		                try {
							tmpInvDetail.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
		                
					} else {
						// update
						GenericValue res = listReservers.get(0);
						BigDecimal resQuantity = res.getBigDecimal("quantity");
						res.put("quantity", resQuantity.add(actualExportedQuantity));
						if (isWeight){
							res.put("amount", res.getBigDecimal("amount").add(actualExportedAmount));
						}
						try {
							delegator.store(res);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Store TransferItemShipGrpInvRes: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("transferId", transferId);
		                tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
		                try {
							tmpInvDetail.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}
					
					BigDecimal weight = BigDecimal.ZERO;
					if (isWeight){
						weight = qoh;
						qoh = BigDecimal.ONE;
					}
					remainQuantity = remainQuantity.subtract(qoh);
					
					List<EntityCondition> condDlvIts = FastList.newInstance();
					condDlvIts.add(EntityCondition.makeCondition("fromTransferItemSeqId", EntityOperator.EQUALS, transferItemSeqIdTmp));
					condDlvIts.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_APPROVED"));
					if (!listDlvItemIds.isEmpty()){
						condDlvIts.add(EntityCondition.makeCondition("deliveryItemSeqId", EntityOperator.NOT_IN, listDlvItemIds));
					}
					condDlvIts.add(condDlv);
					List<GenericValue> items = FastList.newInstance();
					String dlvItemSeqId = null;
					try {
						items = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condDlvIts), null, null, null, false);
						if (!items.isEmpty()){
							dlvItemSeqId = items.get(0).getString("deliveryItemSeqId"); 
						}
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					
					GenericValue objDeliveryItem = null;
					if (!listDlvItemIds.contains(dlvItemSeqId) && UtilValidate.isNotEmpty(dlvItemSeqId)){
						listDlvItemIds.add(dlvItemSeqId);
						try {
							objDeliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", dlvItemSeqId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (UtilValidate.isNotEmpty(objDeliveryItem)) {
							objDeliveryItem.put("inventoryItemId", inventoryItemId);
							try {
								delegator.store(objDeliveryItem);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when store DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					} else {
						condDlvIts = FastList.newInstance();
						condDlvIts.add(EntityCondition.makeCondition("fromTransferItemSeqId", EntityOperator.EQUALS, transferItemSeqIdTmp));
						condDlvIts.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED"));
						condDlvIts.add(condDlv);
						items = FastList.newInstance();
						try {
							items = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condDlvIts), null, null, null, false);
							if (!items.isEmpty()){
								objDeliveryItem = items.get(0); 
							}
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						// create new delivery
						if (UtilValidate.isNotEmpty(objDeliveryItem)) {
							GenericValue newDeliveryItem = delegator.makeValue("DeliveryItem"); 
	        				newDeliveryItem.put("deliveryId", deliveryId);
	        				delegator.setNextSubSeqId(newDeliveryItem, "deliveryItemSeqId", 5, 1);
	        				newDeliveryItem.put("fromTransferId", transferId);
	        				newDeliveryItem.put("fromTransferItemSeqId", transferItemSeqIdTmp);
	        				newDeliveryItem.set("inventoryItemId", inventoryItemId);
	        				newDeliveryItem.put("statusId", "DELI_ITEM_APPROVED");
	        				newDeliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
	        				newDeliveryItem.put("quantity", objDeliveryItem.getBigDecimal("quantity"));
	        				newDeliveryItem.put("amount", objDeliveryItem.getBigDecimal("amount"));
	        				try {
								delegator.createOrStore(newDeliveryItem);
								dlvItemSeqId = newDeliveryItem.getString("deliveryItemSeqId");
								if (!listDlvItemIds.contains(dlvItemSeqId)) {
									listDlvItemIds.add(dlvItemSeqId);
								}
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when create DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					}
					
					Map<String, Object> updateItem = FastMap.newInstance();
    				updateItem.put("deliveryId", deliveryId);
    				updateItem.put("deliveryItemSeqId", dlvItemSeqId);
    				updateItem.put("actualExportedQuantity", actualExportedQuantity);
    				updateItem.put("delegator", delegator);
    				try {
						is.updateExportedQuantity(updateItem);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when updateExportedQuantity DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
    				
    				if (actualExportedAmount.compareTo(BigDecimal.ZERO) > 0){
	    				updateItem = FastMap.newInstance();
	    				updateItem.put("deliveryId", deliveryId);
	    				updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
	    				updateItem.put("actualExportedAmount", actualExportedAmount);
	    				updateItem.put("delegator", delegator);
	    				try {
							is.updateExportedAmount(updateItem);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when updateExportedAmount DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
    				}
    				
					Map<String, Object> issueContext = FastMap.newInstance();
					issueContext.put("inventoryItemId", inventoryItemId);
					issueContext.put("locale", locale);
					issueContext.put("transferId", transferId);
					issueContext.put("transferItemSeqId", transferItemSeqIdTmp);
					issueContext.put("shipGroupSeqId", "00001"); // hard code
					issueContext.put("shipmentId", shipmentId);
					issueContext.put("quantity", qoh);
					issueContext.put("weight", weight);
					issueContext.put("userLogin", userLogin);
					try {
						Map<String, Object> resultTmp = dispatcher.runSync("issueTransferItemShipGrpInvResToShipment", issueContext);
						if (ServiceUtil.isError(resultTmp)){
							String errMsg = "OLBIUS: Fatal error when runSync issueTransferItemShipGrpInvResToShipment: " + ServiceUtil.getErrorMessage(resultTmp);
							Debug.logError(errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					} catch (GenericServiceException e) {
						String errMsg = "OLBIUS: Fatal error when runSync issueTransferItemShipGrpInvResToShipment: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
			} else {
				Map<String, Object> updateItem = FastMap.newInstance();
				updateItem.put("deliveryId", deliveryId);
				updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
				updateItem.put("actualExportedQuantity", BigDecimal.ZERO);
				updateItem.put("delegator", delegator);
				try {
					is.updateExportedQuantity(updateItem);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when updateExportedQuantity DeliveryItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (isWeight){
					updateItem = FastMap.newInstance();
    				updateItem.put("deliveryId", deliveryId);
    				updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
    				updateItem.put("actualExportedAmount", BigDecimal.ZERO);
    				updateItem.put("delegator", delegator);
    				try {
						is.updateExportedAmount(updateItem);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when updateExportedAmount DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
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
			if ("TRANS_ITEM_EXPORTED".equals(statusNew) && !statusNew.equals(objTransferItem.getString("statusId"))){
				Map<String, Object> mapChangeStatus = FastMap.newInstance();
				mapChangeStatus.put("transferId", transferId);
				mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
				mapChangeStatus.put("fromStatusId", objTransferItem.getString("statusId"));
				mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
				mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
				try {
					mapChangeStatus.put("statusId", "TRANS_ITEM_EXPORTED");
					dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("Change trasfer item status Error!");
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("deliveryId", deliveryId);
		return result;
	}
}
