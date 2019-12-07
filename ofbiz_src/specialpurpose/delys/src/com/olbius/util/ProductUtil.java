package com.olbius.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

public class ProductUtil {
	
	public static BigDecimal getConvertPackingNumber(Delegator delegator, String productId, String uomFromId, String uomToId){
		BigDecimal convert = BigDecimal.ONE;
		try {
			if (uomFromId.equals(uomToId)){
				return convert;
			} else {
				GenericValue config = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId));
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
	
	public static BigDecimal getConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId){
		List<String> listUomToConvert = getListUomToConvert(delegator, productId, uomFromId, uomToId);
		convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomToId, listUomToConvert);
		return convert;
	}
	
	public static List<String> getListUomToConvert(Delegator delegator, String productId, String uomFromId, String uomToId){
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
	
	public static BigDecimal getProductConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId, List<String> listUomToConvert){
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
	
	public static String getQuantityUomBySupplier(Delegator delegator, String productId, String orderId){
		String quantityUomId = null;
		try {
			List<GenericValue> orderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
			if (!orderRole.isEmpty()){
				String partyId = (String)orderRole.get(0).get("partyId");
				List<GenericValue> listSuppliers = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "productId", productId)), null, null, null, false);
				listSuppliers = EntityUtil.filterByDate(listSuppliers, null, "availableFromDate", "vailableThruDate", false);
				if (!listSuppliers.isEmpty()){
					quantityUomId = (String)listSuppliers.get(0).get("quantityUomId");
				} else {
					ServiceUtil.returnError("Supplier not found!");
				}
			} else {
				ServiceUtil.returnError("OrderRole not found!");
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError(e.toString());
		}
		
		return quantityUomId;
	}
	
	public static BigDecimal getQuantityReceied(Delegator delegator, String orderId, String orderItemSeqId){
		BigDecimal quantityReceived = BigDecimal.ZERO;
		if (orderId != null){
			try {
				List<GenericValue> listDeliverys = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (!listDeliverys.isEmpty()){
					for (GenericValue delivery : listDeliverys){
						if ("DLV_DELIVERED".equals((String)delivery.get("statusId"))){
							List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", delivery.get("deliveryId"), "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
							for (GenericValue item : listDeliveryItems){
								quantityReceived = quantityReceived.add(item.getBigDecimal("actualDeliveredQuantity"));
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				ServiceUtil.returnError(e.toString());
			}
		}
		return quantityReceived;
	}
}
