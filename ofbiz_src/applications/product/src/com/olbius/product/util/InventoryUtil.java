package com.olbius.product.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class InventoryUtil {
	public static Map<String, Object> getDetailQuantityInventory(Delegator delegator, Map<String, Object> attributes) throws GenericEntityException{
		String productId = (String) attributes.get("productId");
		String facilityId = (String) attributes.get("facilityId");
		String ownerPartyId = (String) attributes.get("ownerPartyId");
		Timestamp expireDate = (Timestamp) attributes.get("expireDate");
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal amountOnHandTotal = BigDecimal.ZERO;
		BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
		List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(productId)) {
			EntityCondition prCond = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			listAllConds.add(prCond);
			if (UtilValidate.isNotEmpty(facilityId)) {
				GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
				if (UtilValidate.isEmpty(ownerPartyId)) {
					ownerPartyId = facility.getString("ownerPartyId");
				}
				EntityCondition faCond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
				listAllConds.add(faCond);
			} 
			if (UtilValidate.isNotEmpty(ownerPartyId)) {
				EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId);
				listAllConds.add(ownerCond);
			}
			if (UtilValidate.isNotEmpty(expireDate)) {
				EntityCondition expCond = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, expireDate);
				listAllConds.add(expCond);
				List<GenericValue> listInventoryItem = delegator.findList("GroupProductInventory", EntityCondition.makeCondition(listAllConds), null, null, null, false);
				if (!listInventoryItem.isEmpty()) {
					quantityOnHandTotal = listInventoryItem.get(0).getBigDecimal("QOH");
					availableToPromiseTotal = listInventoryItem.get(0).getBigDecimal("ATP");
					amountOnHandTotal = listInventoryItem.get(0).getBigDecimal("AOH");
				}
			} else {
				List<GenericValue> listInventoryItem = delegator.findList("InventoryItemGroupByProductAndFacility", EntityCondition.makeCondition(listAllConds), null, null, null, false);
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
	
	/**
	 * @param delegator
	 * @param attributes
	 * @param orderBy
	 * @return
	 * @throws GenericEntityException
	 * @description Chi ap dung cho cac attribute la String, expireDate, datetimeMenufactured
	 */
	public static List<GenericValue> getListInventoryItems(Delegator delegator, Map<String, Object> attributes, List<String> orderBy, List<String> exceptInventoryItemIds) throws GenericEntityException{
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		ModelEntity model = delegator.getModelEntity("InventoryItem");
		List<String> listAllFields = model.getAllFieldNames();
		List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
		for (String field : listAllFields){
			if (attributes.containsKey(field)){
				if ("expireDate".equals(field) || "datetimeManufactured".equals(field)){
					if (UtilValidate.isNotEmpty(attributes.get(field))){
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
						EntityCondition Cond = EntityCondition.makeCondition(field, EntityOperator.EQUALS, null);
						listAllConds.add(Cond);
					}
				} else {
					if (!"amountOnHandTotal".equals(field)){
						EntityCondition Cond = EntityCondition.makeCondition(field, EntityOperator.EQUALS, attributes.get(field));
						listAllConds.add(Cond);
					}
				}
			}
		}
		if (!attributes.containsKey("statusId")){
			EntityCondition cond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
			EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INV_AVAILABLE");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond2);
			EntityCondition condStt = EntityCondition.makeCondition(conds, EntityOperator.OR);
			listAllConds.add(condStt);
		}
		List<String> sortBy = FastList.newInstance();
		if (UtilValidate.isNotEmpty(orderBy)) {
			if (!orderBy.isEmpty()){
				sortBy.addAll(orderBy);
			}
		}
		if (sortBy.isEmpty()){
			sortBy = UtilMisc.toList("quantityOnHandTotal");
		}
		EntityCondition Cond = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		listAllConds.add(Cond);
		if (UtilValidate.isNotEmpty(exceptInventoryItemIds)){
			if (!exceptInventoryItemIds.isEmpty()){
				listAllConds.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.NOT_IN, exceptInventoryItemIds));
			}
		}
		listInventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(listAllConds), null, sortBy, null, false);
		return listInventoryItems;
	}
	
	public static List<GenericValue> getListInventoryItems(Delegator delegator, Map<String, Object> attributes) throws GenericEntityException{
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		listInventoryItems = getListInventoryItems(delegator, attributes, null, null);
		return listInventoryItems;
	}
	
	/*
	 * Find list inventory and quantity_on_hand enough for quantity  
	 * 
	 */
	public static List<Map<String, Object>> getInventoryItemsForQuantity(Delegator delegator, Map<String, Object> attributes, BigDecimal quantity, List<String> exceptInventoryItemIds) throws GenericEntityException{
		return getInventoryItemsForQuantity(delegator, attributes, quantity, null, exceptInventoryItemIds);
	}
	
	public static List<Map<String, Object>> getInventoryItemsForQuantity(Delegator delegator, Map<String, Object> attributes, BigDecimal quantity) throws GenericEntityException{
		 return getInventoryItemsForQuantity(delegator, attributes, quantity, null, null);
	}
	public static List<Map<String, Object>> getInventoryItemsForQuantity(Delegator delegator, Map<String, Object> attributes, BigDecimal quantity, List<String> orderBy, List<String> exceptInventoryItemIds) throws GenericEntityException{
		List<GenericValue> listInvs = getListInventoryItems(delegator, attributes, orderBy, exceptInventoryItemIds);
		List<Map<String, Object>> listReturns = FastList.newInstance();
		if (!listInvs.isEmpty()){
			if (attributes.containsKey("productId")){
				String productId = (String)attributes.get("productId");
				BigDecimal quantityRemain = quantity;
				for (GenericValue inv : listInvs) {
					if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					String inventoryItemId = inv.getString("inventoryItemId");
					if (ProductUtil.isWeightProduct(delegator, productId)){
						qoh = inv.getBigDecimal("amountOnHandTotal");
					}
					if (qoh.compareTo(quantityRemain) == 0){
						Map<String, Object> map = FastMap.newInstance();
						map.put("inventoryItemId", inventoryItemId);
						map.put("quantity", qoh);
						quantityRemain = BigDecimal.ZERO;
						listReturns.add(map);
					} else if (qoh.compareTo(quantityRemain) > 0){
						Map<String, Object> map = FastMap.newInstance();
						map.put("inventoryItemId", inventoryItemId);
						map.put("quantity", quantityRemain);
						quantityRemain = BigDecimal.ZERO;
						listReturns.add(map);
					} else if (qoh.compareTo(quantityRemain) < 0){
						Map<String, Object> map = FastMap.newInstance();
						map.put("inventoryItemId", inventoryItemId);
						map.put("quantity", qoh);
						quantityRemain = quantityRemain.subtract(qoh);
						listReturns.add(map);
					}
				}
			}
		}
		return listReturns;
	}
	//for picklinglist 
	public static List<Map<String, Object>> getInventoryItemLocationToExport(Delegator delegator, String productId, String facilityId, BigDecimal quantity, String locationCheck, String picklistBinId) 
			throws GenericEntityException{
		List<Map<String, Object>> listLocations = FastList.newInstance();
		if (UtilValidate.isNotEmpty(facilityId) && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantity)) {
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				EntityCondition condPr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
				EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
				BigDecimal quantityConvert = BigDecimal.ONE;
				String uomId = null;
				String baseUomId = null;
				if (UtilValidate.isNotEmpty(objProduct.get("quantityUomId"))) {
					baseUomId = objProduct.getString("quantityUomId");
				}
				if (UtilValidate.isNotEmpty(objProduct.get("exportUomId"))) {
					uomId = objProduct.getString("exportUomId");
					if (UtilValidate.isNotEmpty(baseUomId)) {
						quantityConvert = ProductUtil.getConvertPackingNumber(delegator, productId, uomId, baseUomId);
					}
				} else {
					Map<String, Object> mapUom = FastMap.newInstance();
					mapUom = ProductUtil.getBiggestUom(delegator, productId);
					if (mapUom.containsKey("uomId")){
						uomId = (String)mapUom.get("uomId");
						quantityConvert = (BigDecimal)mapUom.get("quantityConvert");
					}
				}
				
				BigDecimal quantityQC = BigDecimal.ZERO;
				BigDecimal quantityEA = quantity;
				if (UtilValidate.isNotEmpty(uomId)) {
					quantityQC = quantity.divide(quantityConvert, 0, RoundingMode.DOWN);
					quantityEA = quantity.subtract(quantityQC.multiply(quantityConvert));
				}
				List<Map<String, Object>> locationQcPick = FastList.newInstance();
				EntityCondition condUom = EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, uomId);
				EntityCondition condQty = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				EntityCondition condTyLoc = EntityCondition.makeCondition("locationFacilityTypeId", EntityOperator.EQUALS, "PICK_FACE");
				if (UtilValidate.isNotEmpty(uomId) && quantityQC.compareTo(BigDecimal.ZERO) > 0) {
					// check pick face full pallet - check quantity greater than full pallet capacity config
					BigDecimal fullPallet = BigDecimal.ZERO;
					List<EntityCondition> condPallet = FastList.newInstance();
					condPallet.add(condPr);
					condPallet.add(EntityCondition.makeCondition("quantityUomId", EntityOperator.EQUALS, uomId));
					condPallet.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> listProductAttrs = FastList.newInstance();
					listProductAttrs = delegator.findList("ProductPackingAttribute", EntityCondition.makeCondition(condPallet), null, null, null, false);
					if (!listProductAttrs.isEmpty()){
						GenericValue tmp = listProductAttrs.get(0);
						if (UtilValidate.isNotEmpty(tmp.get("maxQuantityInPallet"))) {
							fullPallet = tmp.getBigDecimal("maxQuantityInPallet");
						}
					}
					if (quantityQC.compareTo(fullPallet) >= 0 && fullPallet.compareTo(BigDecimal.ZERO) > 0){
						// tim slot full pallet
						BigDecimal palletNumber = quantityQC.divide(fullPallet, 0, RoundingMode.DOWN);
						EntityCondition condQtyPallet = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, fullPallet);
						List<EntityCondition> condLocs = FastList.newInstance();
						condLocs.add(condPr);
						condLocs.add(condQtyPallet);
						condLocs.add(condUom);
						
						List<GenericValue> listInventoryItemLocations = FastList.newInstance();
						listInventoryItemLocations = delegator.findList("InventoryItemLocationSum", EntityCondition.makeCondition(condLocs), null, null, null, false);
						if (!listInventoryItemLocations.isEmpty()){
							List<Map<String, Object>> mapLocPallet = FastList.newInstance();
							for (GenericValue loc : listInventoryItemLocations) {
								BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
								BigDecimal pallet = quantityInLoc.divide(fullPallet, 0, RoundingMode.DOWN);
								if (pallet.compareTo(BigDecimal.ZERO) > 0){
									if (pallet.compareTo(palletNumber) >= 0){
										Map<String, Object> map = FastMap.newInstance();
										map.put("locationId", loc.getString("locationId"));
										map.put("quantity", palletNumber);
										mapLocPallet.add(map);
										quantityQC = quantityQC.subtract(palletNumber);
										palletNumber = BigDecimal.ZERO;
									} else {
										Map<String, Object> map = FastMap.newInstance();
										map.put("locationId", loc.getString("locationId"));
										map.put("quantity", pallet);
										mapLocPallet.add(map);
										quantityQC = quantityQC.subtract(pallet);
										palletNumber = palletNumber.subtract(pallet);
									}
								}
								if (palletNumber.compareTo(BigDecimal.ZERO) <= 0) break;
							}
							for (Map<String, Object> mapTmp : mapLocPallet) {
								String locationId = (String)mapTmp.get("locationId");
								BigDecimal quantityRemain = (BigDecimal)mapTmp.get("quantity");
								quantityRemain = quantityRemain.multiply(quantityConvert);
								EntityCondition condLoc = EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId);
								
								condLocs = FastList.newInstance();
								condLocs.add(condPr);
								condLocs.add(condLoc);
								condLocs.add(condQty);
								condLocs.add(condUom);
								
								listInventoryItemLocations = FastList.newInstance();
								listInventoryItemLocations = delegator.findList("InventoryItemLocationDetail", EntityCondition.makeCondition(condLocs), null, null, null, false);
								
								if (!listInventoryItemLocations.isEmpty()){
									for (GenericValue loc : listInventoryItemLocations) {
										BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
										String inventoryItemId = loc.getString("inventoryItemId");
										BigDecimal quantityEAInLoc = quantityInLoc.multiply(quantityConvert);
										// subtract in other picklist
										EntityCondition condBin = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
										EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
										EntityCondition condInv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
										List<EntityCondition> condPickitem = FastList.newInstance();
										condPickitem.add(condPr);
										condPickitem.add(condFa);
										condPickitem.add(condBin);
										condPickitem.add(condStt);
										condPickitem.add(condTyLoc);
										condPickitem.add(condLoc);
										condPickitem.add(condInv);
										
										List<GenericValue> listPicklistItems = delegator.findList("PicklistItemLocationDetail", EntityCondition.makeCondition(condPickitem),
													null, null, null, false);
										BigDecimal inOtherPicklist = BigDecimal.ZERO;
										if (!listPicklistItems.isEmpty()){
											for (GenericValue item : listPicklistItems) {
												inOtherPicklist = inOtherPicklist.add(item.getBigDecimal("quantity"));
											}
										}
										quantityEAInLoc = quantityEAInLoc.subtract(inOtherPicklist);
										if (quantityEAInLoc.compareTo(BigDecimal.ZERO) > 0){
											Map<String, Object> map = FastMap.newInstance();
											if (quantityRemain.compareTo(quantityEAInLoc) >= 0){
												map.put("quantity", quantityEAInLoc);
												quantityRemain = quantityRemain.subtract(quantityEAInLoc);
											} else {
												map.put("quantity", quantityRemain);
												quantityRemain = BigDecimal.ZERO;
											}
											map.put("inventoryItemId", inventoryItemId);
											map.put("locationId", locationId);
											listLocations.add(map);
										}
										if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
									}
								}
								if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
							}
						}
					}
					if (quantityQC.compareTo(BigDecimal.ZERO) > 0){
						List<EntityCondition> conds = FastList.newInstance();
						conds.add(condPr);
						conds.add(condFa);
						conds.add(condTyLoc);
						List<GenericValue> listLocationPFs = FastList.newInstance();
						if (UtilValidate.isNotEmpty(locationCheck)) {
							conds.add(EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationCheck));
						}
						listLocationPFs = delegator.findList("LocationFacilityProductDetail", EntityCondition.makeCondition(conds), null, null, null, false);
						List<String> listLocationIds = FastList.newInstance();
						if (!listLocationPFs.isEmpty()){
							listLocationIds = EntityUtil.getFieldListFromEntityList(listLocationPFs, "locationId", true);
						}
						if (!listLocationIds.isEmpty()){
							BigDecimal quantityRemain = quantityQC.multiply(quantityConvert);
							for (String locationId : listLocationIds) {
								EntityCondition condLoc = EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId);
								
								List<EntityCondition> condLocs = FastList.newInstance();
								condLocs.add(condPr);
								condLocs.add(condLoc);
								condLocs.add(condQty);
								condLocs.add(condUom);
								
								List<GenericValue> listInventoryItemLocations = FastList.newInstance();
								listInventoryItemLocations = delegator.findList("InventoryItemLocationDetail", EntityCondition.makeCondition(condLocs), null, null, null, false);
								
								if (!listInventoryItemLocations.isEmpty()){
									for (GenericValue loc : listInventoryItemLocations) {
										BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
										String inventoryItemId = loc.getString("inventoryItemId");
										BigDecimal quantityEAInLoc = quantityInLoc.multiply(quantityConvert);
										// subtract in other picklist
										EntityCondition condBin = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
										EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
										EntityCondition condInv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
										List<EntityCondition> condPickitem = FastList.newInstance();
										condPickitem.add(condPr);
										condPickitem.add(condFa);
										condPickitem.add(condBin);
										condPickitem.add(condStt);
										condPickitem.add(condTyLoc);
										condPickitem.add(condLoc);
										condPickitem.add(condInv);
										
										List<GenericValue> listPicklistItems = delegator.findList("PicklistItemLocationDetail", EntityCondition.makeCondition(condPickitem),
													null, null, null, false);
										BigDecimal inOtherPicklist = BigDecimal.ZERO;
										if (!listPicklistItems.isEmpty()){
											for (GenericValue item : listPicklistItems) {
												inOtherPicklist = inOtherPicklist.add(item.getBigDecimal("quantity"));
											}
										}
										quantityEAInLoc = quantityEAInLoc.subtract(inOtherPicklist);
										if (quantityEAInLoc.compareTo(BigDecimal.ZERO) > 0){
											Map<String, Object> map = FastMap.newInstance();
											if (quantityRemain.compareTo(quantityEAInLoc) >= 0){
												map.put("quantity", quantityEAInLoc);
												quantityRemain = quantityRemain.subtract(quantityEAInLoc);
											} else {
												map.put("quantity", quantityRemain);
												quantityRemain = BigDecimal.ZERO;
											}
											map.put("inventoryItemId", inventoryItemId);
											map.put("locationId", locationId);
											locationQcPick.add(map);
											listLocations.add(map);
										}
										if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
									}
								}
								if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
							}
							if (quantityConvert.compareTo(BigDecimal.ONE) == 0 && quantityRemain.compareTo(BigDecimal.ZERO) > 0 && quantityEA.compareTo(BigDecimal.ZERO) == 0){
								quantityEA = quantityRemain;
							}
						}
					}
				}
				if (quantityEA.compareTo(BigDecimal.ZERO) > 0){
					// tim cac vi tri de lay theo EA
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(condPr);
					conds.add(condFa);
					conds.add(condTyLoc);
					List<GenericValue> listLocationPFs = FastList.newInstance();
					listLocationPFs = delegator.findList("LocationFacilityProductDetail", EntityCondition.makeCondition(conds), null, null, null, false);
					
					List<String> listLocationIds = FastList.newInstance();
					if (!listLocationPFs.isEmpty()){
						listLocationIds = EntityUtil.getFieldListFromEntityList(listLocationPFs, "locationId", true);
					}
					if (!listLocationIds.isEmpty()){
						BigDecimal quantityRemain = quantityEA;
						for (String locationId : listLocationIds) {
							EntityCondition condLoc = EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId);
							
							List<EntityCondition> condLocs = FastList.newInstance();
							condLocs.add(condPr);
							condLocs.add(condLoc);
							condLocs.add(condQty);
							condLocs.add(EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, baseUomId));
							
							List<GenericValue> listInventoryItemLocations = FastList.newInstance();
							if (UtilValidate.isNotEmpty(locationCheck)) {
								condLocs.add(EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationCheck));
							}
							listInventoryItemLocations = delegator.findList("InventoryItemLocationDetail", EntityCondition.makeCondition(condLocs), null, null, null, false);
							if (!listInventoryItemLocations.isEmpty()){
								for (GenericValue loc : listInventoryItemLocations) {
									BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
									Map<String, Object> map = FastMap.newInstance();
									if (quantityRemain.compareTo(quantityInLoc) >= 0){
										quantityRemain = quantityRemain.subtract(quantityInLoc);
										map.put("quantity", quantityInLoc);
									} else {
										map.put("quantity", quantityRemain);
										quantityRemain = BigDecimal.ZERO;
									}
									String inventoryItemId = loc.getString("inventoryItemId");
									map.put("inventoryItemId", inventoryItemId);
									map.put("locationId", locationId);
									listLocations.add(map);
									
									if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
								}
							}
							if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
						}
						if (quantityRemain.compareTo(BigDecimal.ZERO) > 0){
							// xe le thung ra de lay EA
							// uu tien xe le o cac slot da pick thung
							if (!locationQcPick.isEmpty()){
								for (Map<String, Object> location : locationQcPick) {
									String locationId = (String)location.get("locationId");
									String inventoryItemId = (String)location.get("inventoryItemId");
									BigDecimal quantityPicked = (BigDecimal)location.get("quantity");
									
									GenericValue loc = delegator.findOne("InventoryItemLocation", false,
												UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId));
									
									BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
									BigDecimal quantityEAInLoc = quantityInLoc.multiply(quantityConvert);
									// subtract in other picklist
									EntityCondition condBin = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
									EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
									EntityCondition condInv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
									EntityCondition condLoc = EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId);
									List<EntityCondition> condPickitem = FastList.newInstance();
									condPickitem.add(condPr);
									condPickitem.add(condFa);
									condPickitem.add(condBin);
									condPickitem.add(condStt);
									condPickitem.add(condTyLoc);
									condPickitem.add(condLoc);
									condPickitem.add(condInv);
									
									List<GenericValue> listPicklistItems = delegator.findList("PicklistItemLocationDetail", EntityCondition.makeCondition(condPickitem),
												null, null, null, false);
									BigDecimal inOtherPicklist = BigDecimal.ZERO;
									if (!listPicklistItems.isEmpty()){
										for (GenericValue item : listPicklistItems) {
											inOtherPicklist = inOtherPicklist.add(item.getBigDecimal("quantity"));
										}
									}
									quantityEAInLoc = quantityEAInLoc.subtract(inOtherPicklist);
									quantityEAInLoc = quantityEAInLoc.subtract(quantityPicked);
									if (quantityEAInLoc.compareTo(BigDecimal.ZERO) > 0){
										Map<String, Object> map = FastMap.newInstance();
										if (quantityRemain.compareTo(quantityEAInLoc) >= 0){
											map.put("quantity", quantityEAInLoc);
											quantityRemain = quantityRemain.subtract(quantityEAInLoc);
										} else {
											map.put("quantity", quantityRemain);
											quantityRemain = BigDecimal.ZERO;
										}
										map.put("inventoryItemId", inventoryItemId);
										map.put("locationId", locationId);
										for (Map<String,Object> locTmp : listLocations) {
											BigDecimal qty = (BigDecimal)locTmp.get("quantity");
											if (inventoryItemId.equals(locTmp.get("inventoryItemId")) && locationId.equals(locTmp.get("locationId"))){
												locTmp.put("quantity", qty.add((BigDecimal)map.get("quantity")));
											}
										}
									}
									if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
								}
							}
							if (quantityRemain.compareTo(BigDecimal.ZERO) > 0){
								// van khong du hang, tim cac slot khac de xe le
								conds = FastList.newInstance();
								List<String> notInIds = FastList.newInstance();
								for (Map<String,Object> map : locationQcPick) {
									notInIds.add((String)map.get("locationId"));
								}
								if (!notInIds.isEmpty()){
									EntityCondition condId = EntityCondition.makeCondition("locationId", EntityOperator.NOT_IN, notInIds);
									conds.add(condId);
								}
								conds.add(condFa);
								conds.add(condTyLoc);
								listLocationPFs = FastList.newInstance();
								listLocationPFs = delegator.findList("LocationFacility", EntityCondition.makeCondition(conds), null, null, null, false);
								
								listLocationIds = FastList.newInstance();
								if (!listLocationPFs.isEmpty()){
									listLocationIds = EntityUtil.getFieldListFromEntityList(listLocationPFs, "locationId", true);
								}
								if (!listLocationIds.isEmpty()){
									for (String locationId : listLocationIds) {
										EntityCondition condLoc = EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId);
										
										List<EntityCondition> condLocs = FastList.newInstance();
										condLocs.add(condPr);
										condLocs.add(condLoc);
										condLocs.add(condQty);
										condLocs.add(condUom);
										
										List<GenericValue> listInventoryItemLocations = FastList.newInstance();
										listInventoryItemLocations = delegator.findList("InventoryItemLocationDetail", EntityCondition.makeCondition(condLocs), null, null, null, false);
										if (!listInventoryItemLocations.isEmpty()){
											for (GenericValue loc : listInventoryItemLocations) {
												String inventoryItemId = loc.getString("inventoryItemId");
												BigDecimal quantityInLoc = loc.getBigDecimal("quantity");
												BigDecimal quantityEAInLoc = quantityInLoc.multiply(quantityConvert);
												// subtract in other picklist
												EntityCondition condBin = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
												EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
												EntityCondition condInv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
												List<EntityCondition> condPickitem = FastList.newInstance();
												condPickitem.add(condPr);
												condPickitem.add(condFa);
												condPickitem.add(condBin);
												condPickitem.add(condStt);
												condPickitem.add(condTyLoc);
												condPickitem.add(condLoc);
												condPickitem.add(condInv);
												
												List<GenericValue> listPicklistItems = delegator.findList("PicklistItemLocationDetail", EntityCondition.makeCondition(condPickitem),
															null, null, null, false);
												BigDecimal inOtherPicklist = BigDecimal.ZERO;
												if (!listPicklistItems.isEmpty()){
													for (GenericValue item : listPicklistItems) {
														inOtherPicklist = inOtherPicklist.add(item.getBigDecimal("quantity"));
													}
												}
												quantityEAInLoc = quantityEAInLoc.subtract(inOtherPicklist);
												if (quantityEAInLoc.compareTo(BigDecimal.ZERO) > 0){
													Map<String, Object> map = FastMap.newInstance();
													if (quantityRemain.compareTo(quantityEAInLoc) >= 0){
														map.put("quantity", quantityEAInLoc);
														quantityRemain = quantityRemain.subtract(quantityEAInLoc);
													} else {
														map.put("quantity", quantityRemain);
														quantityRemain = BigDecimal.ZERO;
													}
													map.put("inventoryItemId", inventoryItemId);
													map.put("locationId", locationId);
													listLocations.add(map);
												}
												if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
											}
										}
										if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) break;
									}
								}
							}
						}
					}
				}
			}
		}
		return listLocations;
	}
}
