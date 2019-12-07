package com.olbius.baselogistics.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;

import com.olbius.basehr.util.PartyUtil;

import javolution.util.FastMap;

public class InventoryUtil {
	public static BigDecimal getQuantityOnHandTotal(Delegator delegator, String productId) throws GenericEntityException{
		BigDecimal qoh = BigDecimal.ZERO;
		List<GenericValue> listInv = delegator.findList("InventoryItemTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
		for (GenericValue item : listInv) {
			qoh = qoh.add(item.getBigDecimal("quantityOnHandTotal"));
		}
		return qoh;
	}
	
	public static BigDecimal getQuantityOnHandTotal(Delegator delegator, String productId, String facilityId) throws GenericEntityException{
		BigDecimal qoh = BigDecimal.ZERO;
		List<GenericValue> listInv = delegator.findList("InventoryItemTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "facilityId", facilityId)), null, null, null, false);
		for (GenericValue item : listInv) {
			qoh = qoh.add(item.getBigDecimal("quantityOnHandTotal"));
		}
		return qoh;
	}
	
	public static BigDecimal getQuantityOnHandTotalByOwner(Delegator delegator, String productId, String ownerPartyId) throws GenericEntityException{
		BigDecimal qoh = BigDecimal.ZERO;
		List<GenericValue> listInv = delegator.findList("InventoryItemTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "ownerPartyId", ownerPartyId)), null, null, null, false);
		for (GenericValue item : listInv) {
			qoh = qoh.add(item.getBigDecimal("quantityOnHandTotal"));
		}
		return qoh;
	}
	
	public static BigDecimal getQuantityOnHandTotal(Delegator delegator, String productId, GenericValue userLogin) throws GenericEntityException{
		BigDecimal qoh = BigDecimal.ZERO;
		String currentOrg = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> listInv = delegator.findList("InventoryItemTotal", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "ownerPartyId", currentOrg)), null, null, null, false);
		for (GenericValue item : listInv) {
			qoh = qoh.add(item.getBigDecimal("quantityOnHandTotal"));
		}
		return qoh;
	}
	
	public static List<GenericValue> getListInventoryItems(Delegator delegator, Map<String, Object> attributes) throws GenericEntityException{
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		ModelEntity model = delegator.getModelEntity("InventoryItem");
		List<String> listAllFields = model.getAllFieldNames();
		List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
		for (String field : listAllFields){
			if (attributes.containsKey(field)){
				if (("expireDate".equals(field) && UtilValidate.isNotEmpty(attributes.get(field))) || ("datetimeManufactured".equals(field) && UtilValidate.isNotEmpty(attributes.get(field)))){
					Timestamp tmp = (Timestamp)(attributes.get(field));
					Date date = new Date(tmp.getTime());                     
					Calendar cal = Calendar.getInstance();      
					cal.setTime(date);                          
					cal.set(Calendar.HOUR_OF_DAY, 0);           
					cal.set(Calendar.MINUTE, 0);                
					cal.set(Calendar.SECOND, 0);                
					cal.set(Calendar.MILLISECOND, 0);           
					Date tmpDate = cal.getTime();      
					Timestamp expfrom = new Timestamp(tmpDate.getTime());
					EntityCondition Condf = EntityCondition.makeCondition(field, EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
					
					cal.set(Calendar.HOUR_OF_DAY, 23);           
					cal.set(Calendar.MINUTE, 59);                
					cal.set(Calendar.SECOND, 59);                
					cal.set(Calendar.MILLISECOND, 999);           
					Date tmpDate2 = cal.getTime();      
					Timestamp expTo = new Timestamp(tmpDate2.getTime());
					EntityCondition Condt = EntityCondition.makeCondition(field, EntityOperator.LESS_THAN_EQUAL_TO, expTo);
					EntityCondition Cond = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
					listAllConds.add(Cond);
				} else {
					EntityCondition Cond = EntityCondition.makeCondition(field, EntityOperator.EQUALS, attributes.get(field));
					listAllConds.add(Cond);
				}
			}
		}
		EntityCondition Cond = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		listAllConds.add(Cond);
		listInventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(listAllConds), null, UtilMisc.toList("quantityOnHandTotal"), null, false);
		return listInventoryItems;
	}
	
	public static String splitInventoryItemByQuantity(Delegator delegator, String inventoryItemId, BigDecimal quantity) throws GenericEntityException{
		GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
		if (inv.getBigDecimal("quantityOnHandTotal").compareTo(quantity) == 0){
			return inventoryItemId;
		}
		if (inv.getBigDecimal("quantityOnHandTotal").compareTo(quantity) < 0){
			return "SPLIT_ERROR";
		}
		String newInvId = null;
		newInvId = delegator.getNextSeqId("InventoryItem");
		Map<String, Object> map = inv.getAllFields();
		map.remove("inventoryItemId");
		map.remove("quantityOnHandTotal");
		map.remove("availableToPromiseTotal");
		map.put("inventoryItemId", newInvId);
		GenericValue newInv = delegator.makeValue("InventoryItem");
		newInv.putAll(map);
		delegator.create(newInv);
		
		GenericValue tmpInvDetailNew = delegator.makeValue("InventoryItemDetail");
		tmpInvDetailNew.set("inventoryItemId", newInvId);
		tmpInvDetailNew.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		tmpInvDetailNew.set("effectiveDate", UtilDateTime.nowTimestamp());
		tmpInvDetailNew.set("quantityOnHandDiff", quantity);
		tmpInvDetailNew.set("availableToPromiseDiff", quantity);
		tmpInvDetailNew.set("accountingQuantityDiff", quantity);
		tmpInvDetailNew.create();
		
		GenericValue tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		tmpInvDetail.set("inventoryItemId", inventoryItemId);
		tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		tmpInvDetail.set("quantityOnHandDiff", quantity.negate());
		tmpInvDetail.set("availableToPromiseDiff", quantity.negate());
		tmpInvDetail.set("accountingQuantityDiff", quantity.negate());
		tmpInvDetail.create();
		
		return newInvId;
	}
	
	public static String splitInventoryItemByAmount(Delegator delegator, String inventoryItemId, BigDecimal amount) throws GenericEntityException{
		GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
		if (inv.getBigDecimal("amountOnHandTotal").compareTo(amount) == 0){
			return inventoryItemId;
		}
		if (inv.getBigDecimal("amountOnHandTotal").compareTo(amount) < 0){
			return "SPLIT_ERROR";
		}
		String newInvId = null;
		newInvId = delegator.getNextSeqId("InventoryItem");
		Map<String, Object> map = inv.getAllFields();
		map.remove("inventoryItemId");
		map.remove("quantityOnHandTotal");
		map.remove("amountOnHandTotal");
		map.remove("availableToPromiseTotal");
		map.put("inventoryItemId", newInvId);
		GenericValue newInv = delegator.makeValue("InventoryItem");
		newInv.putAll(map);
		delegator.create(newInv);
		
		GenericValue tmpInvDetailNew = delegator.makeValue("InventoryItemDetail");
		tmpInvDetailNew.set("inventoryItemId", newInvId);
		tmpInvDetailNew.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		tmpInvDetailNew.set("effectiveDate", UtilDateTime.nowTimestamp());
		tmpInvDetailNew.set("quantityOnHandDiff", BigDecimal.ONE);
		tmpInvDetailNew.set("amountOnHandDiff", amount);
		tmpInvDetailNew.set("availableToPromiseDiff", BigDecimal.ONE);
		tmpInvDetailNew.set("accountingQuantityDiff", BigDecimal.ONE);
		tmpInvDetailNew.create();
		
		GenericValue tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		tmpInvDetail.set("inventoryItemId", inventoryItemId);
		tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		tmpInvDetail.set("quantityOnHandDiff", new BigDecimal(-1));
		tmpInvDetail.set("amountOnHandDiff", amount.negate());
		tmpInvDetail.set("availableToPromiseDiff", new BigDecimal(-1));
		tmpInvDetail.set("accountingQuantityDiff", new BigDecimal(-1));
		tmpInvDetail.create();
		
		return newInvId;
	}
	
	public static Map<String, Object> getDetailQuantityInventory(Delegator delegator, Map<String, Object> attributes) throws GenericEntityException{
		String productId = (String) attributes.get("productId");
		String facilityId = (String) attributes.get("facilityId");
		Timestamp expireDate = (Timestamp) attributes.get("expireDate");
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal amountOnHandTotal = BigDecimal.ZERO;
		BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(facilityId)) {
			GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			String ownerPartyId = facility.getString("ownerPartyId");
			if (UtilValidate.isNotEmpty(expireDate)) {
				List<GenericValue> listInventoryItem = delegator.findList("GroupProductInventory", EntityCondition.makeCondition(UtilMisc.toMap("facilityId",
										facilityId, "productId", productId, "expireDate", expireDate, "ownerPartyId", ownerPartyId)), null, null, null, false);
				if (!listInventoryItem.isEmpty()) {
					quantityOnHandTotal = listInventoryItem.get(0).getBigDecimal("QOH");
					availableToPromiseTotal = listInventoryItem.get(0).getBigDecimal("ATP");
					amountOnHandTotal = listInventoryItem.get(0).getBigDecimal("AOH");
				}
			} else {
				List<GenericValue> listInventoryItem = delegator.findList("InventoryItemGroupByProductAndFacility", EntityCondition.makeCondition(UtilMisc
								.toMap("facilityId", facilityId, "productId", productId, "ownerPartyId", ownerPartyId)), null, null, null, false);
				if (!listInventoryItem.isEmpty()) {
					for (GenericValue inv : listInventoryItem) {
						quantityOnHandTotal = quantityOnHandTotal.add(inv.getBigDecimal("quantityOnHandTotal"));
						if (UtilValidate.isNotEmpty(inv.getBigDecimal("amountOnHandTotal"))) {
							amountOnHandTotal = amountOnHandTotal.add(inv.getBigDecimal("amountOnHandTotal"));
						}
						availableToPromiseTotal = availableToPromiseTotal.add(inv.getBigDecimal("availableToPromiseTotal"));
					}
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("availableToPromiseTotal", availableToPromiseTotal);
		mapReturn.put("amountOnHandTotal", amountOnHandTotal);
		mapReturn.put("quantityOnHandTotal", quantityOnHandTotal);
		return mapReturn;
	}
}
