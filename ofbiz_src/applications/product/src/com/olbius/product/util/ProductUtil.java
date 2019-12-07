package com.olbius.product.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductUtil {
	private static int decimals = UtilNumber.getBigDecimalScale("supplierprice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("supplierprice.rounding");
    
	public static List<String> getProductPackingUoms(Delegator delegator, String productId) throws GenericEntityException{
		List<String> listUoms = new ArrayList<String>();
		List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
		listConfigs = EntityUtil.filterByDate(listConfigs);
		if (!listConfigs.isEmpty()){
			for (GenericValue config : listConfigs){
				String uomFromId = config.getString("uomFromId");
				String uomToId = config.getString("uomToId");
				if (!listUoms.contains(uomToId)){
					GenericValue objUom = null;
					try {
						objUom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", uomToId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne Uom: " + e.toString();
						Debug.logError(e, errMsg);
					}
					if ("PRODUCT_PACKING".equals(objUom.getString("uomTypeId"))){
						listUoms.add(uomToId);
					}
				}
				if (!listUoms.contains(uomFromId)){
					GenericValue objUom = null;
					try {
						objUom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", uomFromId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne Uom: " + e.toString();
						Debug.logError(e, errMsg);
					}
					if ("PRODUCT_PACKING".equals(objUom.getString("uomTypeId"))){
						listUoms.add(uomFromId);
					}
				}
			}
		} else {
			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			if (UtilValidate.isNotEmpty(product)){
				String quantityUomId = product.getString("quantityUomId");
				if (UtilValidate.isNotEmpty(quantityUomId)){
					listUoms.add(quantityUomId);
				}
			}
		}
		return listUoms;
	}
	
	public static List<Map<String, Object>> getProductPackingUomWithConvertNumbers(Delegator delegator, String productId) throws GenericEntityException{
		List<String> listUomIds = getProductPackingUoms(delegator, productId);
		List<Map<String, Object>> listUomWithConverts = new ArrayList<Map<String, Object>>();
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String baseUomId = product.getString("quantityUomId");
		for (String uomId : listUomIds) {
			Map<String, Object> map = FastMap.newInstance();
			map.put("quantityUomId", uomId);
			BigDecimal convert = getConvertPackingNumber(delegator, productId, uomId, baseUomId);
			map.put("convertNumber", convert);
			listUomWithConverts.add(map);
		}
		return listUomWithConverts;
	}
	
	public static List<Map<String, Object>> getProductWeightUomWithConvertNumbers(Delegator delegator, String productId) throws GenericEntityException{
		List<String> listUomIds =  new ArrayList<String>();
		List<GenericValue> list = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false);
		listUomIds = EntityUtil.getFieldListFromEntityList(list, "uomId", true);
		List<Map<String, Object>> listUomWithConverts = new ArrayList<Map<String, Object>>();
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String baseUomId = product.getString("weightUomId");
		for (String uomId : listUomIds) {
			GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomId, "uomIdTo", baseUomId));
			if (UtilValidate.isNotEmpty(conversion)) {
				BigDecimal convert = conversion.getBigDecimal("conversionFactor");
				Map<String, Object> map = FastMap.newInstance();
				map.put("uomId", uomId);
				map.put("convertNumber", convert);
				listUomWithConverts.add(map);
			}
		}
		if (UtilValidate.isNotEmpty(baseUomId)) {
			Map<String, Object> map = FastMap.newInstance();
			map.put("uomId", baseUomId);
			map.put("convertNumber", BigDecimal.ONE);
			listUomWithConverts.add(map);
		}
		return listUomWithConverts;
	}
	
	public static BigDecimal getLastPriceBySupplierProductAndQuantity(Delegator delegator, String productId, String partyId, String currencyUomId, String uomId, BigDecimal quantity) throws GenericEntityException{
		BigDecimal price = BigDecimal.ZERO;
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		if (UtilValidate.isNotEmpty(product)){
			String requireAmount = product.getString("requireAmount");
			BigDecimal convertUom = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				if (UtilValidate.isNotEmpty(product.get("weightUomId"))) {
					String weightUomId = product.getString("weightUomId");
					if (uomId.equals(weightUomId)) {
						convertUom = BigDecimal.ONE;
					} else {
						GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomId, "uomIdTo", weightUomId));
						if (UtilValidate.isNotEmpty(conversion)) {
							convertUom = conversion.getBigDecimal("conversionFactor");
						} else {
							conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomId, "uomIdTo", uomId));
							if (UtilValidate.isNotEmpty(conversion)) {
								convertUom = conversion.getBigDecimal("conversionFactor");
							} 
						}
					}
				} else {
					product.put("weightUomId", uomId);
					delegator.store(product);
					convertUom = BigDecimal.ONE;
				}
			} else {
				convertUom = getConvertPackingToBaseUom(delegator, productId, uomId);
			}
			
			if (convertUom.compareTo(BigDecimal.ZERO) <= 0) return price;
			BigDecimal baseQuantity = quantity.multiply(convertUom);
			
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(partyId)) listConds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			EntityCondition productCond = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition currencyCond = EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId);
			EntityCondition qtyCond = EntityCondition.makeCondition("minimumOrderQuantity", EntityOperator.LESS_THAN_EQUAL_TO, baseQuantity);
			EntityCondition timeConde = EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate");
			listConds.add(productCond);
			listConds.add(currencyCond);
			listConds.add(qtyCond);
			listConds.add(timeConde);
			List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("-availableFromDate"), null, false);
			listSupplierProduct = EntityUtil.filterByDate(listSupplierProduct, UtilDateTime.nowTimestamp(), "availableFromDate", "availableThruDate", true);
			if (!listSupplierProduct.isEmpty()){
				GenericValue objPrice = null;
				String quantityUomId = product.getString("quantityUomId");
				listSupplierProduct = EntityUtil.orderBy(listSupplierProduct, UtilMisc.toList("-minimumOrderQuantity"));
				List<GenericValue> listOks = FastList.newInstance();
				
				for (GenericValue sp : listSupplierProduct) {
					if (UtilValidate.isNotEmpty(sp.get("quantityUomId"))) {
						if (uomId.equals(sp.getString("quantityUomId"))) {
							listOks.add(sp);
						}
					}
				}
				if (!listOks.isEmpty()){
					listOks = EntityUtil.orderBy(listOks, UtilMisc.toList("-availableFromDate"));
					for (GenericValue sp : listOks) {
						if (UtilValidate.isNotEmpty(sp.getBigDecimal("lastPrice"))) {
							objPrice = sp;
							price = objPrice.getBigDecimal("lastPrice");
							break;
						}
					}
				}
				if (UtilValidate.isEmpty(objPrice)) {
					listOks = FastList.newInstance();
					for (GenericValue sp : listSupplierProduct) {
						if (UtilValidate.isNotEmpty(sp.get("quantityUomId")) && UtilValidate.isNotEmpty(quantityUomId)) {
							if (quantityUomId.equals(sp.getString("quantityUomId"))) {
								listOks.add(sp);
							}
						}
					}
					if (!listOks.isEmpty()){
						listOks = EntityUtil.orderBy(listOks, UtilMisc.toList("-availableFromDate"));
						for (GenericValue sp : listOks) {
							if (UtilValidate.isNotEmpty(sp.getBigDecimal("lastPrice"))) {
								objPrice = sp;
								price = objPrice.getBigDecimal("lastPrice").multiply(convertUom).setScale(decimals,rounding);
								break;
							}
						}
					}
				}
				if (UtilValidate.isEmpty(objPrice)) {
					listSupplierProduct = EntityUtil.orderBy(listSupplierProduct, UtilMisc.toList("-availableFromDate"));
					objPrice = listSupplierProduct.get(0);
					String uomPrice = objPrice.getString("quantityUomId");
					BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, uomPrice, uomId);
					if (convert.compareTo(BigDecimal.ZERO) > 0){
						if (UtilValidate.isNotEmpty(objPrice.getBigDecimal("lastPrice"))) {
							price = objPrice.getBigDecimal("lastPrice").divide(convert, decimals, rounding);
						}
					} else {
						convert = ProductUtil.getConvertPackingNumber(delegator, productId, uomId, uomPrice);
						if (convert.compareTo(BigDecimal.ZERO) > 0){
							if (UtilValidate.isNotEmpty(objPrice.getBigDecimal("lastPrice"))) {
								price = objPrice.getBigDecimal("lastPrice").multiply(convert).setScale(decimals,rounding);
							}
						} else {
							if (UtilValidate.isNotEmpty(objPrice.getBigDecimal("lastPrice"))) {
								price = objPrice.getBigDecimal("lastPrice");
							}
						}
					}
				}
			}
		}
		return price;
	}
	
	public static BigDecimal getConvertPackingToBaseUom(Delegator delegator, String productId, String uomFromId) throws GenericEntityException{
		BigDecimal convert = BigDecimal.ONE;
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		if (UtilValidate.isNotEmpty(product)){
			String quantityUomId = product.getString("quantityUomId");
			if (UtilValidate.isNotEmpty(quantityUomId)){
				convert = getConvertPackingNumber(delegator, productId, uomFromId, quantityUomId);
			} else {
				return new BigDecimal(-1);
			}
		} else {
			return new BigDecimal(-1);
		}
		return convert;
	}
	
	public static BigDecimal getConvertPackingNumber(Delegator delegator, String productId, String uomFromId, String uomToId){
		BigDecimal convert = BigDecimal.ONE;
		try {
			if (uomFromId.equals(uomToId)){
				return convert;
			} else {
				GenericValue config = null;
				List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId)), null, null, null, false);
				listConfigs = EntityUtil.filterByDate(listConfigs);
				if (!listConfigs.isEmpty()){
					config = listConfigs.get(0);
				} else {
					return new BigDecimal(-1);
				}
				if (config != null){
					convert = config.getBigDecimal("quantityConvert");
					return convert;
				} else {
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String uomBase = (String)product.get("quantityUomId");
					convert = getConvertNumber(delegator, convert, productId, uomFromId, uomToId);
					if (convert.compareTo(BigDecimal.ZERO) == 0){
						List<String> listUomConvertFrom = getListUomToConvert(delegator, productId, uomFromId, uomBase);
						List<String> listUomConvertTo = getListUomToConvert(delegator, productId, uomToId, uomBase);
						listUomConvertFrom.remove(uomBase);
						listUomConvertTo.remove(uomBase);
						String uomChild = null;
						if (!listUomConvertFrom.isEmpty()){
							for (String uomId : listUomConvertFrom){
								if (listUomConvertTo.contains(uomId)){
									uomChild = uomId;
									break;
								}
							}
						}
						if (uomChild != null){
							BigDecimal convertFrom = BigDecimal.ONE;
							BigDecimal convertTo = BigDecimal.ONE;
							convertFrom = getConvertNumber(delegator, convertFrom, productId, uomFromId, uomChild);
							convertTo = getConvertNumber(delegator, convertTo, productId, uomToId, uomChild);
							if (convertTo.compareTo(BigDecimal.ZERO) == 1 && convertFrom.compareTo(BigDecimal.ZERO) == 1 && convertFrom.compareTo(convertTo) == 1){
								convert = convertFrom.divide(convertTo);
							} else {
								convert = new BigDecimal(-1);
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}
	
	private static BigDecimal getConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId){
		List<String> listUomToConvert = getListUomToConvert(delegator, productId, uomFromId, uomToId);
		convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomToId, listUomToConvert);
		return convert;
	}
	
	private static List<String> getListUomToConvert(Delegator delegator, String productId, String uomFromId, String uomToId){
		Queue<String> listConfigs = new LinkedList<String>() ;
		List<String> listUomToConvert = new ArrayList<String>();
		listUomToConvert.add(uomToId);
		listConfigs.clear();
		listConfigs.add(uomToId);
        while(!listConfigs.isEmpty()) {
        	String uomCur = listConfigs.remove();
			try {
				if (!uomFromId.equals(uomCur)){
					List<GenericValue> listConfigParents = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", uomCur)), null, null, null, false);
		            if (!listConfigParents.isEmpty()){
						for(GenericValue cf : listConfigParents) {
			            	String uomParentId = (String)cf.get("uomFromId");
			                if(!listUomToConvert.contains(uomParentId)) {
			                	listConfigs.add(uomParentId);
			                	listUomToConvert.add(uomParentId);
			                }
			            }
		            } else {
		            	listUomToConvert.remove(uomCur);
		            }
				} else {
					break;
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
        }
        return listUomToConvert;
    }
	
	private static BigDecimal getProductConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId, List<String> listUomToConvert){
		try {
			List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId",productId, "uomToId", uomToId)), null, null, null, false);
			listConfigs = EntityUtil.filterByDate(listConfigs, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
			if (!listConfigs.isEmpty()){
				boolean check = false;
				String uomParentCur = null;
				for (GenericValue cf : listConfigs){
					if (listUomToConvert.contains((String)cf.get("uomFromId"))){
						convert = convert.multiply(cf.getBigDecimal("quantityConvert"));
						uomParentCur = (String)cf.get("uomFromId");
						check = true;
						break;
					}
				}
				if (check){
					if (!uomFromId.equals(uomParentCur)){
						convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomParentCur, listUomToConvert);
					} else {
						return convert;
					}
				} else {
					return BigDecimal.ONE;
				}
			} else {
				return BigDecimal.ONE;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}
	
	public static BigDecimal getOrderItemQuantityReceived(Delegator delegator, String orderId, String orderItemSeqId) throws GenericEntityException{
		BigDecimal quantity = BigDecimal.ZERO;
		EntityCondition cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
		List<GenericValue> listDetail = delegator.findList("InventoryItemReceiveByOrder",
				EntityCondition.makeCondition(cond1, cond2), null, null, null, false);
		if (!listDetail.isEmpty()){
			for (GenericValue item : listDetail) {
				quantity = quantity.add(item.getBigDecimal("quantityOnHandDiff"));
			}
		}
		return quantity;
	}
	
	public static BigDecimal getConvertWeightNumber(Delegator delegator, String productId, String uomFromId, String uomToId){
		BigDecimal convert = BigDecimal.ONE;
		if (uomFromId.equals(uomToId)) return convert;
		try {
			GenericValue uomConversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomFromId, "uomIdTo", uomToId));
			if (UtilValidate.isNotEmpty(uomConversion)) {
				convert = uomConversion.getBigDecimal("conversionFactor");
			} else {
				uomConversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomToId, "uomIdTo", uomFromId));
				if (UtilValidate.isNotEmpty(uomConversion)) {
					convert = BigDecimal.ONE.divide(uomConversion.getBigDecimal("conversionFactor"), 3, RoundingMode.HALF_UP);
				} else {
					return new BigDecimal(-1);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}
	
	public static Map<String, Object> getOrderItemQuantityEditable(Delegator delegator, String orderId, String orderItemSeqId) throws GenericEntityException, GenericServiceException{
		GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		String orderTypeId = orderHeader.getString("orderTypeId");
		Map<String, Object> item = FastMap.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
		List<EntityCondition> listConds = new ArrayList<>(); 
		listConds.add(cond2);
		listConds.add(cond1);
		BigDecimal quantity = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(objOrderItem)) {
			quantity = objOrderItem.getBigDecimal("quantity");
			if (UtilValidate.isNotEmpty(objOrderItem.get("cancelQuantity"))) {
				quantity = quantity.subtract(objOrderItem.getBigDecimal("cancelQuantity"));
			}
			item.putAll(objOrderItem);
			String productId = objOrderItem.getString("productId");
			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			String requireAmount = objProduct.getString("requireAmount");
			if ("PURCHASE_ORDER".equals(orderTypeId)) {
				EntityCondition cond3 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				listConds.add(cond3);
				List<GenericValue> listInvDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					BigDecimal selectedAmount = objOrderItem.getBigDecimal("selectedAmount");
					BigDecimal amount = BigDecimal.ZERO;
					for (GenericValue inv : listInvDetails) {
						BigDecimal amountTmp = inv.getBigDecimal("amountOnHandDiff");
						amount = amount.add(amountTmp);
					}
					if (amount.compareTo(BigDecimal.ZERO) > 0){
						if (selectedAmount.compareTo(amount) > 0){
							item.put("selectedAmount", selectedAmount.subtract(amount));
						} else {
							item.put("selectedAmount", BigDecimal.ZERO);
							quantity = BigDecimal.ZERO;
						}
					}
				} else {
					for (GenericValue inv : listInvDetails) {
						BigDecimal qoh = inv.getBigDecimal("quantityOnHandDiff");
						quantity = quantity.subtract(qoh);
					}
				}
			} else if ("SALES_ORDER".equals(orderTypeId)){
				EntityCondition cond3 = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO);
				listConds.add(cond3);
				List<GenericValue> listInvDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					BigDecimal selectedAmount = objOrderItem.getBigDecimal("selectedAmount");
					BigDecimal amount = BigDecimal.ZERO;
					for (GenericValue inv : listInvDetails) {
						BigDecimal amountTmp = inv.getBigDecimal("amountOnHandDiff");
						amount = amount.add(amountTmp);
					}
					if (amount.compareTo(BigDecimal.ZERO) > 0){
						BigDecimal qtyExp = selectedAmount.divide(amount);
						if (qtyExp.compareTo(BigDecimal.ZERO) > 0){
							quantity = quantity.subtract(qtyExp);
						}
					}
				} else {
					for (GenericValue inv : listInvDetails) {
						BigDecimal qoh = inv.getBigDecimal("quantityOnHandDiff").negate();
						quantity = quantity.subtract(qoh);
					}
				}
			}
			item.put("quantity", quantity);
		}
		return item;
	}
	
	public static List<Map<String, Object>> getOrderItemsEditable(Delegator delegator, String orderId) throws GenericEntityException, GenericServiceException{
		List<Map<String, Object>> listOrderItems = new ArrayList<Map<String, Object>>();
		EntityCondition cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ITEM_CREATED", "ITEM_APPROVED", "ITEM_ESTIMATED"));
		List<EntityCondition> listCond1s = new ArrayList<>(); 
		listCond1s.add(cond1);
		listCond1s.add(cond2);
		List<GenericValue> listItemInits = delegator.findList("OrderItem", EntityCondition.makeCondition(listCond1s), null, null, null, false);
		
		if (!listItemInits.isEmpty()){
			for (GenericValue oi : listItemInits) {
				String orderItemSeqId = oi.getString("orderItemSeqId");
				Map<String, Object> map = FastMap.newInstance();
				map = getOrderItemQuantityEditable(delegator, orderId, orderItemSeqId);
				listOrderItems.add(map);
			}
		}
		return listOrderItems;
	}
	
	public static boolean isWeightProduct(Delegator delegator, String productId){
		GenericValue objProduct = null;
		try {
			objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			Debug.log(e.toString());
			return false;
		}
		if (UtilValidate.isNotEmpty(objProduct)) {
			if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && UtilValidate.isNotEmpty(objProduct.get("amountUomTypeId"))) {
				if ("Y".equals(objProduct.getString("requireAmount")) && "WEIGHT_MEASURE".equals(objProduct.get("amountUomTypeId"))) return true;
			}
		}
		return false;
	}

	public static Map<String, Object> getBiggestUom(Delegator delegator, String productId) {
		Map<String, Object> uomMap = FastMap.newInstance();
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conds.add(EntityCondition.makeCondition("largest", EntityOperator.EQUALS, "Y"));
		
		try {
			List<GenericValue> listUom = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null, null, null, false);
			
			if (UtilValidate.isNotEmpty(listUom)){
				GenericValue uom = listUom.get(0);
				uomMap.put("uomId", uom.getString("uomFromId"));
				uomMap.put("quantityConvert", uom.getBigDecimal("quantityConvert"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return uomMap;
	}
	
	public static BigDecimal getProductReturnPrice(Delegator delegator, String productId, String organizationPartyId, String facilityId) throws GenericEntityException{
		// Lay theo gia von
		BigDecimal returnPrice = BigDecimal.ZERO;
		EntityCondition Cond1 = EntityCondition.makeCondition("productAverageCostTypeId", EntityOperator.EQUALS, "SIMPLE_AVG_COST");
		EntityCondition Cond2 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
		EntityCondition Cond3 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		EntityCondition Cond4 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
		List<EntityCondition> cond2s = FastList.newInstance();
		cond2s.add(Cond1);
		cond2s.add(Cond2);
		cond2s.add(Cond3);
		cond2s.add(Cond4);
		List<GenericValue> listAverages = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(cond2s), null, null, null, false);
		listAverages = EntityUtil.filterByDate(listAverages);
		if (!listAverages.isEmpty()){
			returnPrice = listAverages.get(0).getBigDecimal("averageCost");
		}
		return returnPrice;
	}

	public static BigDecimal getCurrentAverageCost(Delegator delegator, String productId, String facilityId, String productAverageCostTypeId, String organizationPartyId){
		BigDecimal cost = BigDecimal.ZERO;
		cost = getAverageCostByTime(delegator, productId, facilityId, productAverageCostTypeId, organizationPartyId, null);
		return cost;
	}	
	
	/**
	 * 
	 * @param delegator
	 * @param productId 
	 * @param facilityId
	 * @param productAverageCostTypeId
	 * @param organizationPartyId
	 * @param time thoi diem muon lay gia von
	 * @return
	 */
	public static BigDecimal getAverageCostByTime(Delegator delegator, String productId, String facilityId, String productAverageCostTypeId, String organizationPartyId, Timestamp time){
		BigDecimal cost = BigDecimal.ZERO;
		if (UtilValidate.isEmpty(time)) {
			time = UtilDateTime.nowTimestamp();
		}
		EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		EntityCondition cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
		EntityCondition cond3 = EntityCondition.makeCondition("productAverageCostTypeId", EntityOperator.EQUALS, productAverageCostTypeId);
		EntityCondition cond4 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
		EntityCondition cond5 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, time);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond2);
		conds.add(cond3);
		conds.add(cond4);
		conds.add(cond5);
		List<GenericValue> listAverageCosts = FastList.newInstance();
		try {
			listAverageCosts = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(conds), null, UtilMisc.toList("fromDate DESC"), null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ProductAverageCost: " + e.toString();
			Debug.logError(e, errMsg);
			return BigDecimal.ZERO;
		}
		if (!listAverageCosts.isEmpty()){
			cost = listAverageCosts.get(0).getBigDecimal("averageCost");
		}
		return cost;
	}
	
	/**
	 * 
	 * @param delegator
	 * @param productId 
	 * @return Nganh hang
	 */
	public static List<Map<String, Object>> getAllProductCatalogCategory(Delegator delegator){
		String browseRoot = null;
		List<GenericValue> listCCs = FastList.newInstance();
		try {
			listCCs = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogCategoryTypeId", "PCCT_BROWSE_ROOT")), null, null, null, false);
			listCCs = EntityUtil.filterByDate(listCCs);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList tableName: " + e.toString();
			Debug.logError(e, errMsg);
			return null;
		}
		if (!listCCs.isEmpty()){
			browseRoot = listCCs.get(0).getString("productCategoryId");
		}
		List<GenericValue> listCatalogCategory = FastList.newInstance();
		try {
			listCatalogCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY", "primaryParentCategoryId", browseRoot)), null, null, null, false);
		} catch (GenericEntityException e3) {
			Debug.log(e3.toString());
			return null;
		}
		List<Map<String, Object>> listMapReturns = FastList.newInstance();
		if (!listCatalogCategory.isEmpty()){
			for (GenericValue map : listCatalogCategory) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("productCategoryId", map.getString("productCategoryId"));
				item.put("categoryName", map.getString("categoryName"));
				listMapReturns.add(item);
			}
		}
		return listMapReturns;
	}

}
