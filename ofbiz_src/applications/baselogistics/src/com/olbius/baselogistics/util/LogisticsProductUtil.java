package com.olbius.baselogistics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyHelper;
import com.olbius.product.util.ProductUtil;

public class LogisticsProductUtil {
	
	public static final String module = LogisticsProductUtil.class.getName();
	
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
	
	public static BigDecimal getConvertWeightNumber(Delegator delegator, String productId, String uomFromId, String uomToId) throws GenericEntityException{
		BigDecimal convert = BigDecimal.ONE;
		if (uomFromId.equals(uomToId)) return convert;
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
	
	public static Map<String, String> getOrderSupplier(Delegator delegator, String orderId){
		String supplierName = null;
		String partyId = null;
		Map<String, String> map = FastMap.newInstance();
		try {
			List<GenericValue> orderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
			if (!orderRole.isEmpty()){
				partyId = (String)orderRole.get(0).get("partyId");
				GenericValue objParty = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
				if (UtilValidate.isNotEmpty(objParty)) {
					supplierName = PartyHelper.getPartyName(delegator, partyId, true);
					map.put("partyName", supplierName);
					map.put("partyId", partyId);
					map.put("partyCode", objParty.getString("partyCode"));
				}
			} else {
				ServiceUtil.returnError("OrderRole not found!");
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError(e.toString());
		}
		return map;
	}
	
	public static BigDecimal getQuantityReceived(Delegator delegator, String orderId, String orderItemSeqId){
		BigDecimal quantityReceived = BigDecimal.ZERO;
		if (orderId != null){
			try {
				List<GenericValue> listDeliverys = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (!listDeliverys.isEmpty()){
					for (GenericValue delivery : listDeliverys){
						if ("DLV_DELIVERED".equals((String)delivery.get("statusId"))){
							List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", delivery.get("deliveryId"), "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
							for (GenericValue item : listDeliveryItems){
								if (!"DELI_ITEM_CANCELLED".equals(item.getString("statusId"))){
									quantityReceived = quantityReceived.add(item.getBigDecimal("actualDeliveredQuantity"));
								}
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
	
	public static Map<String, Object> checkInventoryAvailableFacility(Delegator delegator, String productId, BigDecimal quantity, String viewATPForAll, String organizationPartyId, String facilityId) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		Boolean isOK = false;
		List<EntityCondition> listAllCond = new ArrayList<EntityCondition>();
		
		if (UtilValidate.isEmpty(facilityId)){
//			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
//			listAllCond.add(facCond);
//		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", BigDecimal.ZERO);
			return result;
		}
		EntityCondition prodCOnd = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		listAllCond.add(prodCOnd);
//		
//		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organizationPartyId);
//		listAllCond.add(ownerCond);

//		EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
//		listAllCond.add(statusCond);
		
//		List<GenericValue> listInventortyByProduct = delegator.findList("InventoryItemProductTotal", EntityCondition.makeCondition(listAllCond, EntityOperator.AND), null, null, null, false);
		GenericValue objProductFacility = null;
		try {
			objProductFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductFacility: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objProductFacility)) {

			BigDecimal atp = objProductFacility.getBigDecimal("lastInventoryCount");
			if (quantity != null && atp != null && quantity.compareTo(atp) <= 0){
				isOK = true;
				quantityOnHandTotal = atp;
				result.put("isOK", isOK);
				if ("Y".equals(viewATPForAll)){
					result.put("quantityOnHandTotal", quantityOnHandTotal);
				}
			} else {
				isOK = false;
				quantityOnHandTotal = atp;
				result.put("isOK", isOK);
				result.put("quantityOnHandTotal", quantityOnHandTotal);
			}
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		return result;
	}
	
	public static Map<String, Object> checkInventoryAvailable(Delegator delegator, String productId, BigDecimal quantity, String viewATPForAll, String organizationPartyId, String productStoreId) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		Boolean isOK = false;
		List<EntityCondition> listAllCond = new ArrayList<EntityCondition>();
		List<String> listFacilityId = new ArrayList<String>();
		List<GenericValue> listStoreFac = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		listStoreFac = EntityUtil.filterByDate(listStoreFac);
		if (!listStoreFac.isEmpty()){
			for (GenericValue storeFac : listStoreFac){
				if (!listFacilityId.contains(storeFac.getString("facilityId"))){
					listFacilityId.add(storeFac.getString("facilityId"));
				}
			}
		}
		List<GenericValue> listFacilityByStore = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		if (!listFacilityByStore.isEmpty()){
			for (GenericValue fac : listFacilityByStore){
				String facilityId = fac.getString("facilityId");
				if (!listFacilityId.contains(facilityId)){
					listFacilityId.add(facilityId);
				}
			}
		}
		
		if (!listFacilityId.isEmpty()){
			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityId);
			listAllCond.add(facCond);
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", BigDecimal.ZERO);
			return result;
		}
		EntityCondition prodCOnd = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		listAllCond.add(prodCOnd);
//		
//		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organizationPartyId);
//		listAllCond.add(ownerCond);
//
//		EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
//		listAllCond.add(statusCond);
		
		List<GenericValue> listProducts = delegator.findList("ProductFacility", EntityCondition.makeCondition(listAllCond, EntityOperator.AND), null, null, null, false);
		if (!listProducts.isEmpty()){
			BigDecimal qoh = BigDecimal.ZERO;
			BigDecimal atp = BigDecimal.ZERO;
			for (GenericValue inv : listProducts){
				qoh = qoh.add(inv.getBigDecimal("lastInventoryCount"));
				atp = atp.add(inv.getBigDecimal("atpInventoryCount"));
			}
				
			if (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(qoh) <= 0){
				isOK = true;
				result.put("isOK", isOK);
				if ("Y".equals(viewATPForAll)){
					result.put("availableToPromiseTotal", atp);
					result.put("quantityOnHandTotal", qoh);
				}
			}
			isOK = false;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", qoh);
			result.put("availableToPromiseTotal", atp);
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
			result.put("availableToPromiseTotal", BigDecimal.ZERO);
		}
		return result;
	}
	
	public static List<Map<String, Object>> checkInventoryAvailableList(Delegator delegator, List<Map<String, Object>> mapProducts, String viewATPForAll, String organizationPartyId, String productStoreId) throws GenericEntityException{
		
		List<Map<String, Object>> listReturn = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> product : mapProducts){
			String productId = (String)product.get("productId");
			String uomId = (String)product.get("uomId");
			BigDecimal quantity = new BigDecimal((String)product.get("quantity"));
			Map<String, Object> mapTmp = FastMap.newInstance();
			Boolean isOK = false;
			BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
			BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(uomId)){
				Map<String, Object> mapUom = checkInventoryAvailableWithUom(delegator, productId, quantity, uomId, viewATPForAll, organizationPartyId, productStoreId);
				isOK = (Boolean)mapUom.get("isOK");
				quantityOnHandTotal = (BigDecimal)mapUom.get("quantityOnHandTotal");
				availableToPromiseTotal = (BigDecimal)mapUom.get("availableToPromiseTotal");
			} else {
				Map<String, Object> mapNonUom = checkInventoryAvailable(delegator, productId, quantity, viewATPForAll, organizationPartyId, productStoreId);
				isOK = (Boolean)mapNonUom.get("isOK");
				quantityOnHandTotal = (BigDecimal)mapNonUom.get("quantityOnHandTotal");
				availableToPromiseTotal = (BigDecimal)mapNonUom.get("availableToPromiseTotal");
			}
			mapTmp.put("productId", productId);
			mapTmp.put("quantity", quantity);
			mapTmp.put("uomId", uomId);
			mapTmp.put("available", isOK);
			mapTmp.put("quantityOnHandTotal", quantityOnHandTotal);
			mapTmp.put("availableToPromiseTotal", availableToPromiseTotal);
			listReturn.add(mapTmp);
		}
		return listReturn;
	}
	
	public static List<Map<String, Object>> checkInventoryAvailableFacility(Delegator delegator, List<Map<String, Object>> mapProducts, String viewATPForAll, String organizationPartyId, String facilityId) throws GenericEntityException{
		List<Map<String, Object>> listReturn = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> product : mapProducts){
			String productId = (String)product.get("productId");
			String uomId = (String)product.get("uomId");
			BigDecimal quantity = new BigDecimal((String)product.get("quantity"));
			Map<String, Object> mapTmp = FastMap.newInstance();
			Boolean isOK = false;
			BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(uomId) && !"".equals(uomId)){
				Map<String, Object> mapUom = checkInventoryAvailableFacilityWithUom(delegator, productId, quantity, uomId, viewATPForAll, organizationPartyId, facilityId);
				isOK = (Boolean)mapUom.get("isOK");
				quantityOnHandTotal = (BigDecimal)mapUom.get("quantityOnHandTotal");
			} else {
				Map<String, Object> mapNonUom = checkInventoryAvailableFacility(delegator, productId, quantity, viewATPForAll, organizationPartyId, facilityId);
				isOK = (Boolean)mapNonUom.get("isOK");
				quantityOnHandTotal = (BigDecimal)mapNonUom.get("quantityOnHandTotal");
			}
			mapTmp.put("productId", productId);
			mapTmp.put("quantity", quantity);
			mapTmp.put("uomId", uomId);
			mapTmp.put("available", isOK);
			mapTmp.put("quantityOnHandTotal", quantityOnHandTotal);
			listReturn.add(mapTmp);
		}
		return listReturn;
	}
	
	public static Map<String, Object> checkInventoryAvailableFacilityWithUom(Delegator delegator, String productId, BigDecimal quantity, String uomId, String viewATPForAll, String organizationPartyId, String facilityId) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		Boolean isOK = false;
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		List<EntityCondition> listAllCond = new ArrayList<EntityCondition>();
		
		if (UtilValidate.isEmpty(facilityId)){
//			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
//			listAllCond.add(facCond);
//		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", BigDecimal.ZERO);
			return result;
		}
		EntityCondition prodCOnd = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		listAllCond.add(prodCOnd);
		
//		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organizationPartyId);
//		listAllCond.add(ownerCond);
//
//		EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
//		listAllCond.add(statusCond);
		
//		List<GenericValue> listInventortyByProduct = delegator.findList("ProductFacility", EntityCondition.makeCondition(listAllCond, EntityOperator.AND), null, null, null, false);
		GenericValue objProductFacility = null;
		try {
			objProductFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductFacility: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String quantityUomId = product.getString("quantityUomId");
		if (quantityUomId == null){
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		BigDecimal convertNumber = getConvertPackingNumber(delegator, productId, uomId, quantityUomId);
		if (convertNumber.compareTo(BigDecimal.ZERO) < 0){
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		if (UtilValidate.isNotEmpty(objProductFacility)) {
			Boolean check = false;
			BigDecimal atp = objProductFacility.getBigDecimal("lastInventoryCount");
			if (UtilValidate.isNotEmpty(quantity) && UtilValidate.isNotEmpty(atp) && (quantity.multiply(convertNumber)).compareTo(atp) <= 0){
				isOK = true;
				quantityOnHandTotal = atp;
				if ("Y".equals(viewATPForAll)){
					result.put("quantityOnHandTotal", quantityOnHandTotal);
				}
				result.put("isOK", isOK);
				check = true;
			} else {
				if (!check){
					isOK = false;
					quantityOnHandTotal = atp;
					result.put("isOK", isOK);
					result.put("quantityOnHandTotal", quantityOnHandTotal);
				}
			}
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		return result;
	}
	
	public static Map<String, Object> checkInventoryAvailableWithUom(Delegator delegator, String productId, BigDecimal quantity, String uomId, String viewATPForAll, String organizationPartyId, String productStoreId) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		Boolean isOK = false;
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		
		List<EntityCondition> listAllCond = new ArrayList<EntityCondition>();
		List<String> listFacilityId = new ArrayList<String>();
		List<GenericValue> listStoreFac = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		listStoreFac = EntityUtil.filterByDate(listStoreFac);
		if (!listStoreFac.isEmpty()){
			for (GenericValue storeFac : listStoreFac){
				if (!listFacilityId.contains(storeFac.getString("facilityId"))){
					listFacilityId.add(storeFac.getString("facilityId"));
				}
			}
		}
		List<GenericValue> listFacilityByStore = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		if (!listFacilityByStore.isEmpty()){
			for (GenericValue fac : listFacilityByStore){
				String facilityId = fac.getString("facilityId");
				if (!listFacilityId.contains(facilityId)){
					listFacilityId.add(facilityId);
				}
			}
		}
		
		if (!listFacilityId.isEmpty()){
			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityId);
			listAllCond.add(facCond);
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("availableToPromiseTotal", BigDecimal.ZERO);
			result.put("quantityOnHandTotal", BigDecimal.ZERO);
			return result;
		}
		EntityCondition prodCOnd = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
		listAllCond.add(prodCOnd);
		
//		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organizationPartyId);
//		listAllCond.add(ownerCond);
//
//		EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
//		listAllCond.add(statusCond);
		
		List<GenericValue> listInventortyByProduct = delegator.findList("ProductFacility", EntityCondition.makeCondition(listAllCond, EntityOperator.AND), null, null, null, false);
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String quantityUomId = product.getString("quantityUomId");
		if (quantityUomId == null){
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("availableToPromiseTotal", quantityOnHandTotal);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		BigDecimal convertNumber = getConvertPackingNumber(delegator, productId, uomId, quantityUomId);
		if (convertNumber.compareTo(BigDecimal.ZERO) < 0){
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("availableToPromiseTotal", quantityOnHandTotal);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		if (!listInventortyByProduct.isEmpty()){
			BigDecimal atp = BigDecimal.ZERO;
			BigDecimal qoh = BigDecimal.ZERO;
			for (GenericValue inv : listInventortyByProduct){
				atp = atp.add(inv.getBigDecimal("atpInventoryCount"));
				qoh = qoh.add(inv.getBigDecimal("lastInventoryCount"));
			}
			if (UtilValidate.isNotEmpty(quantity) && (quantity.multiply(convertNumber)).compareTo(qoh) <= 0){
				isOK = true;
				quantityOnHandTotal = qoh;
				if ("Y".equals(viewATPForAll)){
					result.put("availableToPromiseTotal", atp);
					result.put("quantityOnHandTotal", quantityOnHandTotal);
				}
				result.put("isOK", isOK);
			} else {
				isOK = false;
				quantityOnHandTotal = qoh;
				result.put("isOK", isOK);
				result.put("availableToPromiseTotal", atp);
				result.put("quantityOnHandTotal", quantityOnHandTotal);
			}
		} else {
			isOK = false;
			quantityOnHandTotal = BigDecimal.ZERO;
			result.put("isOK", isOK);
			result.put("availableToPromiseTotal", BigDecimal.ZERO);
			result.put("quantityOnHandTotal", quantityOnHandTotal);
		}
		return result;
	}
	
	public static List<GenericValue> getOrderItemRemains(Delegator delegator, String orderId, String facilityTypeId) throws GenericEntityException, GenericServiceException{
		return getOrderItemRemains(delegator, orderId, facilityTypeId, null, null);
	}
	
	public static List<GenericValue> getOrderItemRemains(Delegator delegator, String orderId, String facilityTypeId, String deliveryId) throws GenericEntityException, GenericServiceException{
		return getOrderItemRemains(delegator, orderId, facilityTypeId, deliveryId, null);
	}
	
	public static List<GenericValue> getOrderItemRemains(Delegator delegator, String orderId, String facilityTypeId, String deliveryId, String orderItemSeqId) throws GenericEntityException, GenericServiceException{
		EntityCondition statusItemCond = EntityCondition.makeCondition("statusId", EntityComparisonOperator.EQUALS, "ITEM_APPROVED");
   	    EntityCondition condOrder = EntityCondition.makeCondition("orderId", EntityComparisonOperator.EQUALS, orderId);
   	    List<EntityCondition> condLists = new ArrayList<EntityCondition>();
   	    if (UtilValidate.isNotEmpty(orderItemSeqId)) {
   	    	EntityCondition condOrderItem = EntityCondition.makeCondition("orderItemSeqId", EntityComparisonOperator.EQUALS, orderItemSeqId);
   	    	condLists.add(condOrderItem);
		}
   	    condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, condOrder, statusItemCond));
		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(condLists, EntityJoinOperator.AND), null, null, null, false);
		
		// check reserves 
//		String billToPartyId = null;
//		List<GenericValue> partys = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
//		billToPartyId = partys.get(0).getString("partyId");
		EntityCondition orderCond = EntityCondition.makeCondition("orderId", EntityComparisonOperator.EQUALS, orderId);
//		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityComparisonOperator.EQUALS, billToPartyId);
		condLists = new ArrayList<EntityCondition>(); 
		if (UtilValidate.isNotEmpty(facilityTypeId)){
			EntityCondition facilityCond = EntityCondition.makeCondition("facilityTypeId", EntityComparisonOperator.EQUALS, facilityTypeId);
			condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, facilityCond));
		}
//   	    condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, ownerCond, orderCond));
   	    condLists.add(EntityCondition.makeCondition(orderCond));
		
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		EntityCondition cond1 = EntityCondition.makeCondition("fromOrderId", EntityOperator.EQUALS, orderId);
		conds.add(cond1);
		if (UtilValidate.isNotEmpty(deliveryId)) {
			EntityCondition cond2 = EntityCondition.makeCondition("deliveryId", EntityOperator.NOT_EQUAL, deliveryId);
			conds.add(cond2);
		}
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
		List<GenericValue> listOrderItemReturns = new ArrayList<GenericValue>();
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		String orderTypeId = orderHeader.getString("orderTypeId");
		if ("PURCHASE_ORDER".equals(orderTypeId)){
			if (!listOrderItems.isEmpty() && !deliveryItems.isEmpty()){
				for (GenericValue orderItem : listOrderItems){
					String productId = (String)orderItem.get("productId");
					BigDecimal orderQty = orderItem.getBigDecimal("quantity");
					
	   	    		if (UtilValidate.isNotEmpty(orderItem.getBigDecimal("cancelQuantity"))) {
	   	    			BigDecimal cancelQty = orderItem.getBigDecimal("cancelQuantity");
	   	    			orderQty = orderQty.subtract(cancelQty);
					}
					BigDecimal totalCreated = BigDecimal.ZERO;
					
					BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
					BigDecimal quantity = orderItem.getBigDecimal("quantity");
					
					if (UtilValidate.isNotEmpty(selectedAmount) && ProductUtil.isWeightProduct(delegator, productId)) {
						BigDecimal orderAmount = selectedAmount.multiply(quantity);
						for (GenericValue dlvItem : deliveryItems){
							if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
								if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedAmount");
									totalCreated  = totalCreated.add(itemQty);
								} else if ("DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("actualDeliveredAmount");
									totalCreated  = totalCreated.add(itemQty);
								} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("amount");
									totalCreated  = totalCreated.add(itemQty);
								}
							}
						}
						if (orderAmount.compareTo(totalCreated) > 0){
							orderItem.put("quantity", BigDecimal.ONE);
							orderItem.put("selectedAmount", orderAmount.subtract(totalCreated));
							listOrderItemReturns.add(orderItem);
						} 
					} else {
						for (GenericValue dlvItem : deliveryItems){
							if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
								if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
									totalCreated  = totalCreated.add(itemQty);
								} else if ("DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("actualDeliveredQuantity");
									totalCreated  = totalCreated.add(itemQty);
								} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
									BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
									totalCreated  = totalCreated.add(itemQty);
								}
							}
						}
						if (orderQty.compareTo(totalCreated) > 0){
							orderItem.put("quantity", orderQty.subtract(totalCreated));
							listOrderItemReturns.add(orderItem);
						} 
					}
				}
			} else {
				for (GenericValue orderItem : listOrderItems){
					String productId = orderItem.getString("productId");
					BigDecimal orderQty = BigDecimal.ZERO;
					if (ProductUtil.isWeightProduct(delegator, productId)){
						orderQty = orderItem.getBigDecimal("selectedAmount");
					} else {
						orderQty = orderItem.getBigDecimal("quantity");
						if (UtilValidate.isNotEmpty(orderItem.getBigDecimal("cancelQuantity"))) {
		   	    			BigDecimal cancelQty = orderItem.getBigDecimal("cancelQuantity");
		   	    			orderQty = orderQty.subtract(cancelQty);
						}
					}
					orderItem.put("quantity", orderQty);
					listOrderItemReturns.add(orderItem);
				}
			}
		} else  if ("SALES_ORDER".equals(orderTypeId)){
			if (!listOrderItems.isEmpty()){
				for (GenericValue orderItem : listOrderItems){
					String productId = (String)orderItem.get("productId");
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId)); 
					
					BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
					BigDecimal quantity = orderItem.getBigDecimal("quantity");
					if (UtilValidate.isNotEmpty(selectedAmount) && ProductUtil.isWeightProduct(delegator, productId)) {
						BigDecimal orderAmount = selectedAmount.multiply(quantity);
						BigDecimal totalCreated = BigDecimal.ZERO;
						if (!deliveryItems.isEmpty()){
							for (GenericValue dlvItem : deliveryItems){
								if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
									if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
										BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedAmount");
										totalCreated  = totalCreated.add(itemQty);
									} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
										BigDecimal itemQty = dlvItem.getBigDecimal("amount");
										totalCreated  = totalCreated.add(itemQty);
									}
								}
							}
						}
						if (orderAmount.compareTo(totalCreated) > 0){
							orderItem.put("quantity", orderAmount.subtract(totalCreated).divide(selectedAmount, RoundingMode.HALF_UP));
							orderItem.put("alternativeQuantity", orderItem.get("alternativeQuantity"));
							orderItem.put("selectedAmount", selectedAmount);
							listOrderItemReturns.add(orderItem);
						}
					} else {
						String uomFromId = orderItem.getString("quantityUomId");
						String uomToId = (String)product.get("quantityUomId");
						BigDecimal convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
						
						BigDecimal orderQtyTotal = BigDecimal.ZERO;
						orderQtyTotal = orderItem.getBigDecimal("alternativeQuantity").multiply(convertNumber);
						BigDecimal totalCreated = BigDecimal.ZERO;
						if (!deliveryItems.isEmpty()){
							for (GenericValue dlvItem : deliveryItems){
								if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
									if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
										BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
										totalCreated  = totalCreated.add(itemQty);
									} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
										BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
										totalCreated  = totalCreated.add(itemQty);
									}
								}
							}
						}
						if (orderQtyTotal.compareTo(totalCreated) > 0){
							orderItem.put("alternativeQuantity", (orderQtyTotal).subtract(totalCreated));
							orderItem.put("quantity", (orderQtyTotal).subtract(totalCreated));
							listOrderItemReturns.add(orderItem);
						}
					}
				}
			} else {
				listOrderItemReturns.addAll(listOrderItems);
			}
		}
		
		return listOrderItemReturns;
	}
	
	public static List<GenericValue> getTransferItemRemains(Delegator delegator, String orderId, String facilityTypeId, String ownerPartyId, String deliveryId) throws GenericEntityException, GenericServiceException{
		return getTransferItemRemains(delegator, orderId, facilityTypeId, ownerPartyId, deliveryId, null);
	}
	
	public static List<GenericValue> getTransferItemRemains(Delegator delegator, String transferId, String facilityTypeId, String ownerPartyId) throws GenericEntityException, GenericServiceException{
		return getTransferItemRemains(delegator, transferId, facilityTypeId, ownerPartyId, null, null);
	}
	
	public static List<GenericValue> getTransferItemRemains(Delegator delegator, String transferId, String facilityTypeId, String ownerPartyId, String deliveryId, String transferItemSeqId) throws GenericEntityException, GenericServiceException{
		EntityCondition statusItemCond = EntityCondition.makeCondition("statusId", EntityComparisonOperator.EQUALS, "TRANS_ITEM_APPROVED");
   	    EntityCondition condTransfer = EntityCondition.makeCondition("transferId", EntityComparisonOperator.EQUALS, transferId);
   	    List<EntityCondition> condLists = new ArrayList<EntityCondition>(); 
   	    if (UtilValidate.isNotEmpty(transferItemSeqId)) {
	    	EntityCondition condItem = EntityCondition.makeCondition("transferItemSeqId", EntityComparisonOperator.EQUALS, transferItemSeqId);
	    	condLists.add(condItem);
		}
   	    condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, condTransfer, statusItemCond));
		List<GenericValue> listTransferItems = delegator.findList("TransferItem", EntityCondition.makeCondition(condLists, EntityJoinOperator.AND), null, null, null, false);
		
		// check reserves 
		EntityCondition transferCond = EntityCondition.makeCondition("transferId", EntityComparisonOperator.EQUALS, transferId);
		EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityComparisonOperator.EQUALS, ownerPartyId);
		condLists = new ArrayList<EntityCondition>(); 
		if (UtilValidate.isNotEmpty(facilityTypeId)){
			EntityCondition facilityCond = EntityCondition.makeCondition("facilityTypeId", EntityComparisonOperator.EQUALS, facilityTypeId);
			condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, facilityCond));
		}
   	    condLists.add(EntityCondition.makeCondition(EntityJoinOperator.AND, ownerCond, transferCond));
		List<GenericValue> listTransferItemRes = delegator.findList("TransferItemShipGrpInvResDetail", EntityCondition.makeCondition(condLists, EntityJoinOperator.AND), null, null, null, false);
		
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		EntityCondition cond1 = EntityCondition.makeCondition("fromTransferId", EntityOperator.EQUALS, transferId);
		conds.add(cond1);
		if (UtilValidate.isNotEmpty(deliveryId)) {
			EntityCondition cond2 = EntityCondition.makeCondition("deliveryId", EntityOperator.NOT_EQUAL, deliveryId);
			conds.add(cond2);
		}
		
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
		List<GenericValue> listTransferItemReturns = new ArrayList<GenericValue>();
		
		GenericValue objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		String needReserves = objTransferHeader.getString("needReservesInventory");
		if (!listTransferItems.isEmpty()) {
			if (UtilValidate.isNotEmpty(needReserves) && "Y".equals(needReserves)) {
				if (listTransferItemRes.isEmpty()){
					return listTransferItemReturns;
				}
			} 
			for (GenericValue transferItem : listTransferItems){
				String productId = (String)transferItem.get("productId");
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId)); 
				Boolean reqAmount = ProductUtil.isWeightProduct(delegator, productId);
				
				String uomFromId = transferItem.getString("quantityUomId");
				String uomToId = (String)product.get("quantityUomId");
				
				String weightUomId = transferItem.getString("weightUomId");
				String baseWeightUomId = (String)product.get("weightUomId");
				BigDecimal convertWeightNumber = BigDecimal.ONE;
				if (UtilValidate.isNotEmpty(weightUomId) && UtilValidate.isNotEmpty(baseWeightUomId)) {
					convertWeightNumber = LogisticsProductUtil.getConvertWeightNumber(delegator, productId, weightUomId, baseWeightUomId);
				}
				BigDecimal convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
				
				BigDecimal transferQtyTotal = BigDecimal.ZERO;
				transferQtyTotal = transferItem.getBigDecimal("quantity").multiply(convertNumber);
				if (reqAmount) transferQtyTotal = transferItem.getBigDecimal("amount").multiply(convertWeightNumber);
				BigDecimal totalCreated = BigDecimal.ZERO;
				if (!deliveryItems.isEmpty()){
					for (GenericValue dlvItem : deliveryItems){
						if (transferItem.getString("transferItemSeqId").equals(dlvItem.getString("fromTransferItemSeqId"))){
							if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
								BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
								if (reqAmount) itemQty = dlvItem.getBigDecimal("actualExportedAmount");
								totalCreated  = totalCreated.add(itemQty);
							} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
								BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
								if (reqAmount) itemQty = dlvItem.getBigDecimal("quantity");
								totalCreated  = totalCreated.add(itemQty);
							}
						}
					}
				}
				if (transferQtyTotal.compareTo(totalCreated) > 0){
					if (reqAmount) {
						transferItem.put("amount", (transferQtyTotal).subtract(totalCreated));
						transferItem.put("quantity", BigDecimal.ONE);
					} else {
						transferItem.put("quantity", (transferQtyTotal).subtract(totalCreated));
					}
					listTransferItemReturns.add(transferItem);
				}
			}
		}
		
		return listTransferItemReturns;
	}
	
	public static Boolean checkPurchaseOrderReceipt(Delegator delegator, String orderId) throws GenericEntityException, GenericServiceException{
		Boolean createdDone = true;
		EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED");
		EntityCondition IDCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		List<EntityCondition> listConds = UtilMisc.toList(statusCond, IDCond);
		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId)), null, null, null, false);
		if (!listOrderItems.isEmpty() && !deliveryItems.isEmpty()){
			for (GenericValue orderItem : listOrderItems){
				BigDecimal orderQty = orderItem.getBigDecimal("quantity");
				boolean checkWeightProduct = ProductUtil.isWeightProduct(delegator, (String) orderItem.get("productId"));
				if (checkWeightProduct){
					orderQty = orderQty.multiply(orderItem.getBigDecimal("selectedAmount"));
				}
				BigDecimal totalCreated = BigDecimal.ZERO;
				for (GenericValue dlvItem : deliveryItems){
					if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
						if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
							if (checkWeightProduct) {
								itemQty = itemQty.multiply(dlvItem.getBigDecimal("actualExportedAmount"));
							}
							totalCreated  = totalCreated.add(itemQty);
						} else if ("DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("actualDeliveredQuantity");
							if (checkWeightProduct) {
								itemQty = itemQty.multiply(dlvItem.getBigDecimal("actualDeliveredAmount"));
							}
							totalCreated  = totalCreated.add(itemQty);
							
						} else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
							if (checkWeightProduct) {
								itemQty = itemQty.multiply(dlvItem.getBigDecimal("amount"));
							}
							totalCreated  = totalCreated.add(itemQty);
						}
					}
				}
				if (orderQty.compareTo(totalCreated) > 0){
					createdDone = false;
					break;
				} 
			}
		} else {
			if (deliveryItems.isEmpty()){
				createdDone = false;
			}
		}
		return createdDone;
	}
	
	public static Boolean checkAllSalesOrderItemCreatedDelivery(Delegator delegator, String orderId) throws GenericEntityException, GenericServiceException{
		Boolean createdDone = true;
		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId)), null, null, null, false);
		if (!listOrderItems.isEmpty() && !deliveryItems.isEmpty()){
			for (GenericValue orderItem : listOrderItems){
				String productId = (String)orderItem.get("productId");
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String uomFromId = (String)orderItem.get("quantityUomId");
				String uomToId = (String)product.get("quantityUomId");
				BigDecimal convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
				BigDecimal orderQty = orderItem.getBigDecimal("alternativeQuantity").multiply(convertNumber);
				BigDecimal totalCreated = BigDecimal.ZERO;
				if (ProductUtil.isWeightProduct(delegator, productId) && UtilValidate.isNotEmpty(orderItem.get("selectedAmount"))) {
					orderQty = orderQty.multiply(orderItem.getBigDecimal("selectedAmount"));
				}
					
				for (GenericValue dlvItem : deliveryItems){
					if (orderItem.getString("orderItemSeqId").equals(dlvItem.getString("fromOrderItemSeqId"))){
						if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
							totalCreated  = totalCreated.add(itemQty);
						}  else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
							BigDecimal itemAmount = dlvItem.getBigDecimal("amount");
							if (itemAmount != null && itemAmount.compareTo(BigDecimal.ZERO) > 0) {
								totalCreated = totalCreated.add(itemAmount);
							} else {
								totalCreated  = totalCreated.add(itemQty);
							}
						}
					}
				}
				if (orderQty.compareTo(totalCreated) > 0){
					createdDone = false;
					break;
				} 
			}
		} else {
			if (deliveryItems.isEmpty()){
				createdDone = false;
			}
		}
		return createdDone;
	}
	
	public static Boolean checkAllTransferItemCreatedDelivery(Delegator delegator, String transferId) throws GenericEntityException, GenericServiceException{
		Boolean createdDone = true;
		List<GenericValue> listTransferItems = delegator.findList("TransferItem", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", transferId)), null, null, null, false);
		if (!listTransferItems.isEmpty() && !deliveryItems.isEmpty()){
			for (GenericValue transferItem : listTransferItems){
				String productId = (String)transferItem.get("productId");
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
				BigDecimal transferQty = transferItem.getBigDecimal("quantity");
				if (isKg) {
					transferQty = transferItem.getBigDecimal("amount");
					String uomFromId = (String)transferItem.get("weightUomId");
					String uomToId = (String)product.get("weightUomId");
					if (!uomToId.equals(uomFromId)) {
						BigDecimal convertNumber = LogisticsProductUtil.getConvertWeightNumber(delegator, productId, uomFromId, uomToId);
						transferQty = transferQty.multiply(convertNumber);
					}
				} else {
					String uomFromId = (String)transferItem.get("quantityUomId");
					String uomToId = (String)product.get("quantityUomId");
					BigDecimal convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
					transferQty = transferQty.multiply(convertNumber);
				}
				
				BigDecimal totalCreated = BigDecimal.ZERO;
				for (GenericValue dlvItem : deliveryItems){
					if (transferItem.getString("transferItemSeqId").equals(dlvItem.getString("fromTransferItemSeqId"))){
						if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
							if (isKg) {
								itemQty = dlvItem.getBigDecimal("actualExportedAmount");
							}
							totalCreated  = totalCreated.add(itemQty);
						}  else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
							BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
							if (isKg) {
								itemQty = dlvItem.getBigDecimal("amount");
							}
							totalCreated  = totalCreated.add(itemQty);
						}
					}
				}
				if (transferQty.compareTo(totalCreated) > 0){
					createdDone = false;
					break;
				} 
			}
		} else {
			if (deliveryItems.isEmpty()){
				createdDone = false;
			}
		}
		return createdDone;
	}
	
	public static String checkOrderStatus(Delegator delegator, String orderId) throws GenericEntityException, GenericServiceException{
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		String statusId = orderHeader.getString("statusId");
		return statusId;
	}
	
	public static String checkTransferStatus(Delegator delegator, String transferId) throws GenericEntityException, GenericServiceException{
		GenericValue transferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		String statusId = transferHeader.getString("statusId");
		return statusId;
	}
	
	public static Boolean checkInventoryAvailableWithPartyOwner(Delegator delegator, String partyId, String productId, BigDecimal quantity) throws GenericEntityException{
		Boolean isOK = false;
		List<GenericValue> listInventortyByProduct = delegator.findList("InventoryItemProductTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "ownerPartyId", partyId, "statusId", null)), null, null, null, false);
		if (!listInventortyByProduct.isEmpty()){
			for (GenericValue inv : listInventortyByProduct){
				BigDecimal atp = inv.getBigDecimal("quantityOnHandTotal");
				if (quantity != null && atp != null && quantity.compareTo(atp) <= 0){
					isOK = true;
					break;
				}
			}
		} else {
			isOK = false;
		}
		return isOK;
	}
	
	public static List<Map<String, Object>> checkInventoryAvailableListWithOwner(Delegator delegator, String ownerPartyId, List<Map<String, Object>> mapProducts) throws GenericEntityException{
		
		List<Map<String, Object>> listReturn = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> product : mapProducts){
			String productId = (String)product.get("productId");
			String uomId = (String)product.get("uomId");
			BigDecimal quantity = new BigDecimal((String)product.get("quantity"));
			Map<String, Object> mapTmp = FastMap.newInstance();
			Boolean isOK = false;
			if (uomId != null && !"".equals(uomId)){
				isOK = checkInventoryAvailableWithUomWithOwnerParty(delegator, ownerPartyId, productId, quantity, uomId);
			} else {
				isOK = checkInventoryAvailableWithPartyOwner(delegator, ownerPartyId, productId, quantity);
			}
			mapTmp.put("productId", productId);
			mapTmp.put("quantity", quantity);
			mapTmp.put("uomId", uomId);
			mapTmp.put("available", isOK);
			listReturn.add(mapTmp);
		}
		return listReturn;
	}
	
	public static Boolean checkInventoryAvailableWithUomWithOwnerParty(Delegator delegator, String ownerPartyId, String productId, BigDecimal quantity, String uomId) throws GenericEntityException{
		Boolean isOK = false;
		List<GenericValue> listInventortyByProduct = delegator.findList("InventoryItemProductTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "ownerPartyId", ownerPartyId, "statusId", null)), null, null, null, false);
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String quantityUomId = product.getString("quantityUomId");
		if (quantityUomId == null){
			return false;
		}
		BigDecimal convertNumber = getConvertPackingNumber(delegator, productId, uomId, quantityUomId);
		if (convertNumber.compareTo(BigDecimal.ZERO) < 0){
			return false;
		}
		if (!listInventortyByProduct.isEmpty()){
			for (GenericValue inv : listInventortyByProduct){
				BigDecimal atp = inv.getBigDecimal("quantityOnHandTotal");
				if (quantity != null && atp != null && (quantity.multiply(convertNumber)).compareTo(atp) <= 0){
					isOK = true;
					break;
				}
			}
		} else {
			isOK = false;
		}
		return isOK;
	}
	
	public static List<String> getProductPackingUoms(Delegator delegator, String productId) throws GenericEntityException{
		List<String> listUoms = new ArrayList<String>();
		List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
		listConfigs = EntityUtil.filterByDate(listConfigs);
		if (!listConfigs.isEmpty()){
			for (GenericValue config : listConfigs){
				String uomFromId = config.getString("uomFromId");
				String uomToId = config.getString("uomToId");
				if (!listUoms.contains(uomToId)){
					listUoms.add(uomToId);
				}
				if (!listUoms.contains(uomFromId)){
					listUoms.add(uomFromId);
				}
			}
		} else {
			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			if (UtilValidate.isNotEmpty(product)){
				String quantityUomId = product.getString("quantityUomId");
				if (UtilValidate.isNotEmpty(quantityUomId)){
//					GenericValue newConfigPacking = delegator.makeValue("ConfigPacking");
//					newConfigPacking.put("productId", productId);
//					newConfigPacking.put("uomFromId", quantityUomId);
//					newConfigPacking.put("uomToId", quantityUomId);
//					newConfigPacking.put("quantityConvert", BigDecimal.ONE);
//					newConfigPacking.put("fromDate", UtilDateTime.nowTimestamp());
//					delegator.createOrStore(newConfigPacking);
					listUoms.add(quantityUomId);
				}
			}
		}
		return listUoms;
	}
	
	public static Timestamp getProductExpireDate(Delegator delegator, String facilityId, String productId) throws GenericEntityException{
		Timestamp expiredDate = UtilDateTime.nowTimestamp();
		GenericValue productAttr = delegator.findOne("ProductAttribute", false, UtilMisc.toMap("productId", productId, "attrName", "DAYN"));
		GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		if (UtilValidate.isNotEmpty(productAttr) && UtilValidate.isNotEmpty(productFacility)){
			String dayNTmp = "0";
			if (UtilValidate.isNotEmpty(productAttr)) {
				dayNTmp = productAttr.getString("attrValue");
			}
			BigDecimal dayN = new BigDecimal(dayNTmp);
			BigDecimal thresholds = productFacility.getBigDecimal("thresholds");
			Calendar cal = Calendar.getInstance();
			cal.setTime(UtilDateTime.nowTimestamp());
			cal.add(Calendar.DATE, dayN.add(thresholds).intValue());
			Date tmp = cal.getTime();
			expiredDate = new Timestamp(tmp.getTime());
		}
		return expiredDate;
	}
	
	public static Map<String, Object> getProductAttributeDate(Delegator delegator, String facilityId, String productId, Timestamp expiredDate) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		// get Date can receive
		Timestamp dateCanReceive = expiredDate;
		GenericValue productAttr = delegator.findOne("ProductAttribute", false, UtilMisc.toMap("productId", productId, "attrName", "DAYN"));
		GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		if (UtilValidate.isNotEmpty(productAttr) && UtilValidate.isNotEmpty(productFacility)){
			String dayNTmp = "0";
			if (UtilValidate.isNotEmpty(productAttr)) {
				dayNTmp = productAttr.getString("attrValue");
			}
			BigDecimal dayN = new BigDecimal(dayNTmp);
			BigDecimal thresholds = productFacility.getBigDecimal("thresholds");
			Calendar cal = Calendar.getInstance();
			cal.setTime(expiredDate);
			cal.add(Calendar.DATE, -dayN.add(thresholds).intValue());
			Date tmp = cal.getTime();
			dateCanReceive = new Timestamp(tmp.getTime());
			
		}
		// get manufactured date
		Timestamp manufacturedDate = UtilDateTime.nowTimestamp();
		GenericValue productAttrShelfLife = delegator.findOne("ProductAttribute", false, UtilMisc.toMap("productId", productId, "attrName", "SHELFLIFE"));
		if (UtilValidate.isNotEmpty(productAttrShelfLife)){
			String shelfLifeTmp = "0";
			if (UtilValidate.isNotEmpty(productAttrShelfLife)) {
				shelfLifeTmp = productAttrShelfLife.getString("attrValue");
			}
			Calendar cal1 = Calendar.getInstance();
			BigDecimal shelfLife = new BigDecimal(shelfLifeTmp);
			cal1.setTime(expiredDate);
			cal1.add(Calendar.DATE, -shelfLife.intValue());
			Date tmp1 = cal1.getTime();
			manufacturedDate = new Timestamp(tmp1.getTime());
		}
		
		result.put("dateCanReceive", dateCanReceive);
		result.put("manufacturedDate", manufacturedDate);
		return result;
	}
	
	public static Boolean checkShipmentCreatedDoneForDeliveryEntry(Delegator delegator, String shipmentId) throws GenericEntityException, GenericServiceException{
		Boolean createdDone = true;
   	    List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
   	    List<GenericValue> listShipmentItemInDEs = delegator.findList("DeliveryEntryShipment", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
   	    if (!listShipmentItems.isEmpty() && !listShipmentItemInDEs.isEmpty()){
   	    	for (GenericValue item : listShipmentItems){
   	    		BigDecimal shipmentQty = item.getBigDecimal("quantity");
   	    		BigDecimal createdQty = BigDecimal.ZERO;
   	    		for (GenericValue itemInDe : listShipmentItemInDEs){
   	    			if (itemInDe.getString("shipmentId").equals(item.getString("shipmentId")) && itemInDe.getString("shipmentItemSeqId").equals(item.getString("shipmentItemSeqId"))){
   	    				createdQty = createdQty.add(itemInDe.getBigDecimal("quantity"));
   	    			}
   	    		}
   	    		if (shipmentQty.compareTo(createdQty) > 0){
   	    			createdDone = false;
   	    			break;
   	    		}
   	    	}
   	    } else {
   	    	if (listShipmentItemInDEs.isEmpty() && !listShipmentItems.isEmpty()){
   	    		createdDone = false;
   	    	} else {
   	    		createdDone = true;
   	    	}
   	    }
   	    return createdDone;
	}
	
	public static List<Map<String, Object>> getListPromotionForDeliveryWithTax(Delegator delegator, String deliveryId) throws GenericEntityException{
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId",deliveryId));
		String orderId = delivery.getString("orderId");
		List<GenericValue> adjustmentTmps = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT")), null, null, null, false);
		List<Map<String, Object>> mapReturn = new ArrayList<Map<String, Object>>();
		if (!adjustmentTmps.isEmpty()){
			List<String> listProductPromoIds = EntityUtil.getFieldListFromEntityList(adjustmentTmps, "productPromoId", true);
			for (String promoId : listProductPromoIds) {
				BigDecimal totalAmount = BigDecimal.ZERO;
				Map<String, Object> map = FastMap.newInstance();
				GenericValue promo = delegator.findOne("ProductPromo", false, UtilMisc.toMap("productPromoId", promoId));
				map.put("productPromoId", promoId);
				map.put("promoName", promo.getString("promoName"));
				
				List<GenericValue> listPromos = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "productPromoId", promoId, "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT")), null, null, null, false);
				
				for (GenericValue adj : listPromos) {
					BigDecimal amount = BigDecimal.ZERO;
					String orderItemSeqId = adj.getString("orderItemSeqId");
					GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					if (UtilValidate.isNotEmpty(orderItem)){
						List<GenericValue> listOrderItems = new ArrayList<GenericValue>();
						listOrderItems.add(orderItem);
						amount = com.olbius.basesales.order.OrderWorker.getTotalTaxOrderItemPromo(delegator, listOrderItems, UtilDateTime.nowTimestamp());
						amount = amount.add(adj.getBigDecimal("amount").negate());
						String productId = orderItem.getString("productId");
						BigDecimal orderQuantity = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(orderItem.get("alternativeQuantity")) && UtilValidate.isNotEmpty(orderItem.get("quantityUomId"))){
							String quantityUomId = orderItem.getString("quantityUomId");
							orderQuantity = orderItem.getBigDecimal("alternativeQuantity").multiply(LogisticsProductUtil.getConvertPackingToBaseUom(delegator, productId, quantityUomId)); 
						} else {
							orderQuantity = orderItem.getBigDecimal("quantity"); 
						}
						List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
						BigDecimal dlvItemQuantity = BigDecimal.ZERO;
						for (GenericValue dlvItem : listDlvItems){
							String statusId = dlvItem.getString("statusId");
							if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
								dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("quantity"));
							}
							if ("DELI_ITEM_EXPORTED".equals(statusId) || "DELI_ITEM_DELIVERED".equals(statusId)){
								dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
							}
						}
						amount = amount.multiply(dlvItemQuantity.divide(orderQuantity, 10, BigDecimal.ROUND_HALF_UP));
						totalAmount = totalAmount.add(amount);
					} else {
						BigDecimal rotation = LogisticsProductUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);
						amount = amount.multiply(rotation);
						totalAmount = totalAmount.add(amount);
					}
				}
				map.put("amount", totalAmount);
				mapReturn.add(map);
			}
		}
		return mapReturn;
	}
	
	public static BigDecimal calcRotationPriceOfDeliveryAndOrder(Delegator delegator, String deliveryId) throws GenericEntityException{
		BigDecimal rotation = BigDecimal.ZERO;
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId",deliveryId));
		String orderId = delivery.getString("orderId");
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
		List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "isPromo", "N")), null, null, null, false);
		BigDecimal grandOrderNotTax = com.olbius.basesales.order.OrderReadHelper.getOrderItemsSubTotal(orderItems, null);
		if (!deliveryItems.isEmpty()){
			BigDecimal grandDeliveryNotTax = BigDecimal.ZERO;
			for (GenericValue item : deliveryItems) {
				String statusId = item.getString("statusId");
				BigDecimal dlvItemQuantity = BigDecimal.ZERO;
				if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
					dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("quantity"));
				}
				if ("DELI_ITEM_EXPORTED".equals(statusId) || "DELI_ITEM_DELIVERED".equals(statusId)){
					dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualExportedQuantity"));
				}
//				if ("DELI_ITEM_DELIVERED".equals(statusId)){
//					dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualDeliveredQuantity"));
//				}
				GenericValue orderItemTmp = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")));
				if ("N".equals(orderItemTmp.getString("isPromo"))){
					BigDecimal orderQuantity = orderItemTmp.getBigDecimal("quantity");
					BigDecimal unitPrice = orderItemTmp.getBigDecimal("unitPrice");
					grandDeliveryNotTax = grandDeliveryNotTax.add(dlvItemQuantity.divide(orderQuantity, 10, RoundingMode.HALF_UP).multiply(unitPrice.multiply(orderQuantity)))                                                                                   ;
				} else {
					continue;
				}
			}
			if (grandOrderNotTax.compareTo(BigDecimal.ZERO) > 0){
				rotation = grandDeliveryNotTax.divide(grandOrderNotTax, 10, RoundingMode.HALF_UP);
			}
		}
		return rotation;
	}
	
	public static List<GenericValue> getListProdConfigItemProduct(Delegator delegator, String productId){
		List<GenericValue> listProductItems = new ArrayList<GenericValue>();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(productId)) {
			EntityCondition condDateTime = EntityUtil.getFilterByDateExpr();
    		List<EntityCondition> condAll = new ArrayList<EntityCondition>();
    		condAll.add(condDateTime);
    		condAll.add(EntityCondition.makeCondition("productId", productId));
    		List<String> configItemIds;
			try {
				configItemIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductConfig", EntityCondition.makeCondition(condAll, EntityOperator.AND), UtilMisc.toSet("configItemId"), UtilMisc.toList("-fromDate", "sequenceNum"), null, true), "configItemId", true);
	    		if (UtilValidate.isNotEmpty(configItemIds)) {
	    			List<String> configOptionIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductConfigOption", EntityCondition.makeCondition("configItemId", EntityOperator.IN, configItemIds), UtilMisc.toSet("configOptionId"), null, null, true), "configOptionId", true);
	    			if (UtilValidate.isNotEmpty(configOptionIds)) {
	    				listAllConditions.add(EntityCondition.makeCondition("configItemId", EntityOperator.IN, configItemIds));
	    				listAllConditions.add(EntityCondition.makeCondition("configOptionId", EntityOperator.IN, configOptionIds));
	    				listProductItems = delegator.findList("ProductConfigProductAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
	    			}
	    		}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return listProductItems;
	}
	
	public static BigDecimal getProductConfigAmount(Delegator delegator, String productId){
		BigDecimal unitAmount = BigDecimal.ZERO;
		List<GenericValue> listProductChilds = LogisticsProductUtil.getListProdConfigItemProduct(delegator, productId);
		for (GenericValue pr : listProductChilds) {
			if (UtilValidate.isNotEmpty(pr.getBigDecimal("amount")) && UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
				unitAmount = unitAmount.add(pr.getBigDecimal("amount").multiply(pr.getBigDecimal("quantity")));
			}
		}
		return unitAmount;
	}
	
	public static GenericValue getFormulaForProduct(Delegator delegator, String productId, String formulaTypeId) {
		List<GenericValue> listProductAndFormulas;
		GenericValue formula = null;
		try {
			EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition cond2 = EntityCondition.makeCondition("formulaTypeId", EntityOperator.EQUALS, formulaTypeId);
			EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FML_ACTIVATED");
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			listConds.add(cond1);
			listConds.add(cond2);
			listConds.add(cond3);
			
			listProductAndFormulas = delegator.findList("FormulaProductDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
			listProductAndFormulas = EntityUtil.filterByDate(listProductAndFormulas);
			if (!listProductAndFormulas.isEmpty()) {
				formula = listProductAndFormulas.get(0);
			} else {
				List<EntityCondition> listCond2s = new ArrayList<EntityCondition>();
				listCond2s.add(cond2);
				listCond2s.add(cond3);
				List<GenericValue> listFormulaByType = delegator.findList("Formula", EntityCondition.makeCondition(listCond2s), null, null, null, false);
				if (!listFormulaByType.isEmpty()){
					List<String> listForIdTmp = new ArrayList<String>();
					listForIdTmp = EntityUtil.getFieldListFromEntityList(listFormulaByType, "formulaId", true);
					List<String> orderBy = new ArrayList<String>();
					orderBy.add("-changeDate");
					List<GenericValue> listFormulaHistory = delegator.findList("FormulaHistory",
							EntityCondition.makeCondition("formulaId", EntityOperator.IN, listForIdTmp), null, orderBy, null, false);
					if (!listFormulaHistory.isEmpty()) {
						String formulaId = listFormulaHistory.get(0).getString("formulaId");
						formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
					} else {
						formula = listFormulaByType.get(0);
					}
				}
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return formula;
	}
}