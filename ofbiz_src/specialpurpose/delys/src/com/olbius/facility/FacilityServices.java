package com.olbius.facility;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SecurityUtil;


public class FacilityServices {
	
	public static final String module = FacilityServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static Map<String, Object> getProductId (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String)context.get("facilityId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("productId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProductId = delegator.findList("ListProductByInventoryItemId",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), fieldToSelects, null, null, false);
		result.put("listProductId", listProductId);
		return result;
	}
	
	public static Map<String, Object> getLocationByProductId (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String)context.get("productId");
		String facilityId = (String)context.get("facilityId");
		if(productId != null){
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listLocationByProductId = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "faciityId", facilityId)), null, null, null, false);
		List<GenericValue> listLocationByProductIds = new ArrayList<GenericValue>();
		if (!listLocationByProductId.isEmpty()){
			listLocationByProductIds.addAll(listLocationByProductId);
		}
		result.put("listLocationByProductId", listLocationByProductIds);
		}
		return result;
	}
	
	public static Map<String, Object> jqGetLocationByProductId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String locationId = parameters.get("locationId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("locationId", EntityOperator.EQUALS, locationId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("FindInventoryItemByLocationSeqId", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String,Object> jqCreateInventoryItemByProductId(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
		Locale locale = (Locale)context.get("locale");
    	
    	
    	Map<String, Object> result = new FastMap<String, Object>();
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
    	
    	String productId = (String)context.get("productId");
    	String locationId = (String)context.get("locationId");
    	BigDecimal quantity = (BigDecimal)context.get("quantity");
    	String uomId = (String)context.get("uomId");
    	String inventoryItemId = null;
    	BigDecimal quantityOnHandTotal = null;
    	
    	List<GenericValue> listAll = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
    	for (GenericValue list : listAll) {
			inventoryItemId = (String) list.get("inventoryItemId");
			GenericValue inventoryItems = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
			GenericValue inventoryItemLocationCheck = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId));
			quantityOnHandTotal = inventoryItems.getBigDecimal("quantityOnHandTotal");
			
			if(inventoryItemLocationCheck != null){
	    		return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkInventoryItemExits", locale));
	    	}
			
			int quantityOnHandTotalData = quantityOnHandTotal.intValue();
			int quantityCast = quantity.intValue();
			
			if(quantityCast > quantityOnHandTotalData){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkQuantityOnHandTotal", locale));
			}
			
			if(quantityCast < quantityOnHandTotalData){
				inventoryItemLocation.put("productId", productId);
		    	inventoryItemLocation.put("inventoryItemId", inventoryItemId);
		    	inventoryItemLocation.put("locationId", locationId);
		    	inventoryItemLocation.put("quantity", quantity);
		    	inventoryItemLocation.put("uomId", uomId);
		    	delegator.create(inventoryItemLocation);
		    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "CreateSucessful", locale));
			}
	    	
    	}
		return result;
    }
	
	public static Map<String, Object> jqGetInventoryItemByLocation(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	String locationSeqId = parameters.get("locationSeqId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		listAllConditions.add(EntityCondition.makeCondition("locationSeqId", EntityOperator.NOT_EQUAL, locationSeqId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("InventoryItemLocation", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	
	public static Map<String,Object> stockLocation(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
		Locale locale = (Locale)context.get("locale");
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
    	
    	String productId = (String)context.get("productId");
    	String facilityId = (String)context.get("facilityId");
    	String locationSeqIdTranfer = (String)context.get("locationSeqIdTranfer");
    	String locationSeqIdCurrent = (String)context.get("locationSeqIdCurrent");
    	String quantity = (String)context.get("quantityTransfer");
    	String quantityCurrent = (String)context.get("quantityCurrent");
    	String uomId = (String)context.get("uomId");
    	String inventoryItemId = null;
    	
    	
    	
    	List<GenericValue> listAllProductId = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
    	for (GenericValue list : listAllProductId) {
			inventoryItemId = (String) list.get("inventoryItemId");
		}
    	GenericValue inventoryItemLocationCurrentData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdCurrent));
		GenericValue inventoryItemLocationTranferToData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdTranfer));
    	
		BigDecimal quantityToData = (BigDecimal) inventoryItemLocationTranferToData.get("quantity");
		int quantityCastDataTo = quantityToData.intValue();
		String uomIdTranferToData = (String) inventoryItemLocationTranferToData.get("uomId");
		
		
    	BigDecimal quantityCastInput =  BigDecimal.ZERO;
    	NumberFormat fm = NumberFormat.getInstance(locale);
		if (quantity != null){
			try {
				quantityCastInput = new BigDecimal(fm.parse(quantity).toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		BigDecimal quantityCurrentCastInput =  BigDecimal.ZERO;
		if (quantityCurrent != null){
			try {
				quantityCurrentCastInput = new BigDecimal(fm.parse(quantityCurrent).toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
    	
		GenericValue inventoryItemLocationTranferData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdTranfer));
		if(inventoryItemLocationTranferData == null){
			inventoryItemLocation.put("productId", productId);
			inventoryItemLocation.put("inventoryItemId", inventoryItemId);
	    	inventoryItemLocation.put("facilityId", facilityId);
	    	inventoryItemLocation.put("locationSeqId", locationSeqIdTranfer);
	    	inventoryItemLocation.put("quantity", quantityCastInput);
	    	inventoryItemLocation.put("uomId", uomId);
	    	delegator.create(inventoryItemLocation);
	    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "CreateSucessful", locale));
		}
    	
		else{
			int quantityCurrentCastTo = quantityCurrentCastInput.intValue();
			int quantityCastTo = quantityCastInput.intValue();
			if(uomId.equals(uomIdTranferToData) == false){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkUomId", locale));
			}
			
			if(quantityCastTo > quantityCurrentCastTo){
					return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "CheckQuantityCurrent", locale));
			}
			int quantityInputCurrent = quantityCurrentCastTo - quantityCastTo;
			BigDecimal valnputCurrent = new BigDecimal(quantityInputCurrent);
					
			inventoryItemLocationCurrentData.put("quantity", valnputCurrent);
			delegator.store(inventoryItemLocationCurrentData);
					
			int quantityInputTo = quantityCastTo + quantityCastDataTo;
			BigDecimal valnputTranfer = new BigDecimal(quantityInputTo);
					
			inventoryItemLocationTranferToData.put("quantity", valnputTranfer);
			delegator.store(inventoryItemLocationTranferToData);
			return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "StockLocationInventoryItemSuccess", locale));
			
		}
    }
	
	public static Map<String,Object> stockLocationMany(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
		Locale locale = (Locale)context.get("locale");
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
    	
    	//String[] productId = (String[])context.get("productId[]");
    	List<String> productId = (List<String>)context.get("productId[]");
    	List<String> locationSeqIdCurrent = (List<String>)context.get("locationSeqIdCurrent[]");
    	String facilityId = (String)context.get("facilityId");
    	String locationSeqIdTranfer = (String)context.get("locationSeqIdTranfer");
    	List<String> quantity = (List<String>)context.get("quantityTranfers[]");
    	List<String> quantityCurrent = (List<String>)context.get("quantityCurrent[]");
    	List<String> uomId = (List<String>)context.get("uomId[]");
    	
    	String inventoryItemId = null;
    	GenericValue inventoryItemLocationCurrentData = null;
    	GenericValue inventoryItemLocationTranferToData = null;
    	GenericValue inventoryItemLocationTranferData = null;
    	BigDecimal quantityCurrentCastInput =  BigDecimal.ZERO;
    	BigDecimal quantityCastInput =  BigDecimal.ZERO;
    	
    	NumberFormat fm = NumberFormat.getInstance(locale);
		
		
    	for (String productIdFor : productId) {
    		if (quantity != null){
				try {
					for (String quantityTranferCast : quantity) {
						quantityCastInput = new BigDecimal(fm.parse(quantityTranferCast).toString());
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (quantityCurrent != null){
				try {
					for (String quantityCurrentCast : quantityCurrent) {
						quantityCurrentCastInput = new BigDecimal(fm.parse(quantityCurrentCast).toString());
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
    		List<GenericValue> listAllProductId = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productIdFor), null, null, null, false);
	    	for (GenericValue list : listAllProductId) {
				inventoryItemId = (String) list.get("inventoryItemId");
				inventoryItemLocationTranferToData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdTranfer));
				inventoryItemLocationTranferData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdTranfer));
	    	}
	    	int quantityCurrentCastTo = quantityCurrentCastInput.intValue();
			int quantityCastTo = quantityCastInput.intValue();
			
	    	for (String locationSeqIdCurrentFor : locationSeqIdCurrent) {
	    		inventoryItemLocationCurrentData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdCurrentFor));
	    	}
	    	

    		
    		BigDecimal quantityToData = (BigDecimal) inventoryItemLocationTranferToData.get("quantity");
			int quantityCastDataTo = quantityToData.intValue();
			String uomIdTranferToData = (String) inventoryItemLocationTranferToData.get("uomId");
			
		
			if(inventoryItemLocationTranferData == null){
				inventoryItemLocation.put("productId", productId);
				inventoryItemLocation.put("inventoryItemId", inventoryItemId);
		    	inventoryItemLocation.put("facilityId", facilityId);
		    	inventoryItemLocation.put("locationSeqId", locationSeqIdTranfer);
		    	inventoryItemLocation.put("quantity", quantityCastInput);
		    	inventoryItemLocation.put("uomId", uomId);
		    	delegator.create(inventoryItemLocation);
		    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "CreateSucessful", locale));
			}
	    	
			else{
				for (String uomIdCast : uomId) {
					if(uomIdCast.equals(uomIdTranferToData) == false){
						return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkUomId", locale));
					}
				}
				if(quantityCastTo > quantityCurrentCastTo){
						return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "CheckQuantityCurrent", locale));
				}
				int quantityInputCurrent = quantityCurrentCastTo - quantityCastTo;
				BigDecimal valnputCurrent = new BigDecimal(quantityInputCurrent);
						
					inventoryItemLocationCurrentData.put("quantity", valnputCurrent);
				delegator.store(inventoryItemLocationCurrentData);
						
				int quantityInputTo = quantityCastTo + quantityCastDataTo;
				BigDecimal valnputTranfer = new BigDecimal(quantityInputTo);
						
				inventoryItemLocationTranferToData.put("quantity", valnputTranfer);
				delegator.store(inventoryItemLocationTranferToData);
			}
	    	
	    	
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "StockLocationInventoryItemSuccess", locale));
    	
    }
	
	public static Map<String,Object> stockOneProductForLocationMany(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
		Locale locale = (Locale)context.get("locale");
    	Delegator delegator = ctx.getDelegator();
    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
    	
    	//String[] productId = (String[])context.get("productId[]");
    	String productId = (String)context.get("productId");
    	String locationSeqIdCurrent = (String)context.get("locationSeqIdCurrent");
    	String facilityId = (String)context.get("facilityId");
    	
    	List<String> lstlocationSeqIdTranfer=(List<String>)context.get("locationSeqIdTranfer[]");
    	List<String> lstquantity = (List<String>)context.get("quantityTranfers[]");
    	
    	Map<String, String> lstItemLocationTranfer = new HashMap<String,String>(); 	
    	for (int i = 0; i < lstlocationSeqIdTranfer.size(); i++) {				
    		lstItemLocationTranfer.put(lstlocationSeqIdTranfer.get(i), lstquantity.get(i));	    		
		}
    	
    	String uomId = (String)context.get("uomId");
    	
    	String inventoryItemId = null;
    	GenericValue inventoryItemLocationCurrentData = null;
    	GenericValue inventoryItemLocationTranferToData = null;
    	BigDecimal quantityCurrentCastInput =  BigDecimal.ZERO;
    	BigDecimal quantityCastInput =  BigDecimal.ZERO;
    	
    	NumberFormat fm = NumberFormat.getInstance(locale);
	    	String quantityCurrent = (String)context.get("quantityCurrent");
			if (quantityCurrent != null){
				try {
						quantityCurrentCastInput = new BigDecimal(fm.parse(quantityCurrent).toString());																		
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			int quantityCurrentCastTo = quantityCurrentCastInput.intValue();
			int quantityCastTo = quantityCastInput.intValue();
			String locationSeqId = null;
			int totalQuantityCastTo=0;
    		List<GenericValue> listAllProductId = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
	    	for (GenericValue list : listAllProductId) {
				inventoryItemId = (String) list.get("inventoryItemId");
				locationSeqId = (String) list.get("locationSeqId");
				for (Map.Entry<String,String> locationTranfersss : lstItemLocationTranfer.entrySet() ) {
					
					inventoryItemLocationTranferToData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationTranfersss.getKey()));
					inventoryItemLocationCurrentData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdCurrent));
					
					
					
					
					if(inventoryItemLocationTranferToData == null){
						
						totalQuantityCastTo+=Integer.parseInt(locationTranfersss.getValue());
						int quantityInputCurrent = quantityCurrentCastInput.intValue() - totalQuantityCastTo;
						BigDecimal valnputCurrent = new BigDecimal(quantityInputCurrent);
						
						inventoryItemLocationCurrentData.put("quantity", valnputCurrent);
						delegator.store(inventoryItemLocationCurrentData);
						BigDecimal quantityInputToDataaaa = new BigDecimal(locationTranfersss.getValue());
						
						inventoryItemLocation.put("productId", productId);
						inventoryItemLocation.put("inventoryItemId", inventoryItemId);
				    	inventoryItemLocation.put("facilityId", facilityId);
				    	inventoryItemLocation.put("locationSeqId", locationTranfersss.getKey());
				    	inventoryItemLocation.put("quantity", quantityInputToDataaaa);
				    	inventoryItemLocation.put("uomId", uomId);
				    	delegator.create(inventoryItemLocation);
				    	
					}
					
					else{
						if (locationTranfersss.getKey().contains(locationSeqId)) { 	
							
							totalQuantityCastTo+=Integer.parseInt(locationTranfersss.getValue());
							int quantityInputCurrent = quantityCurrentCastInput.intValue() - totalQuantityCastTo;
							BigDecimal valnputCurrent = new BigDecimal(quantityInputCurrent);
							
							BigDecimal quantityToData = (BigDecimal) inventoryItemLocationTranferToData.get("quantity");
							int quantityCastDataTo = quantityToData.intValue();
							
							String uomIdTranferToData = (String) inventoryItemLocationTranferToData.get("uomId");
							
							if(uomId.equals(uomIdTranferToData) == false){
								return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkUomId", locale));
							}
							
							if(quantityCastTo > quantityCurrentCastTo){
									return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "CheckQuantityCurrent", locale));
							}
							inventoryItemLocationCurrentData.put("quantity", valnputCurrent);
							delegator.store(inventoryItemLocationCurrentData);
									
							int quantityInputTo = quantityCastDataTo + Integer.parseInt(locationTranfersss.getValue());
							BigDecimal valnputTranfer = new BigDecimal(quantityInputTo);
									
							inventoryItemLocationTranferToData.put("quantity", valnputTranfer);
							delegator.store(inventoryItemLocationTranferToData);
						}
					}
				}
				
	    	}
	    	
	    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "CreateSucessful", locale));
    }
	
	public static Map<String, Object> jqGetLocationAndQuantityByFacilityId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	String locationSeqId = parameters.get("locationSeqId")[0];
    	List<GenericValue> listLocationSeqId = new ArrayList<GenericValue>();
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		listAllConditions.add(EntityCondition.makeCondition("locationSeqId", EntityOperator.NOT_EQUAL, locationSeqId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
    		opts.setDistinct(true);
    		listLocationSeqId = delegator.findList("FacilityLocation", cond, null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listLocationSeqId)){
    			for(GenericValue location : listLocationSeqId){
    				Map<String, Object> row = new HashMap<String, Object>();
    				row.put("locationSeqId", (String)location.get("locationSeqId"));
    				List<GenericValue> listDetail = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationSeqId", (String)location.get("locationSeqId"), "facilityId", facilityId)), null, null, null, false);
    				
    				List<Map<String, String>> rowDetail = new ArrayList<Map<String,String>>();
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
    							GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", (String)detail.get("uomId")));
        						Map<String, String> childDetail = new HashMap<String, String>(); 
        						String inventoryItemId = (String)detail.get("inventoryItemId");
        						String product = (String)detail.get("productId");
        						BigDecimal quantity = (BigDecimal)detail.getBigDecimal("quantity");
        						String quanityData = quantity.intValue() + "";
        						String descriptionUomId = (String) uom.get("description");
        						String locationSeqIdCurrent = (String) detail.get("locationSeqId");
        						childDetail.put("inventoryItemId", inventoryItemId);
        						childDetail.put("productId", product);
        						childDetail.put("quantity", quanityData);
        						childDetail.put("locationSeqIdCurrent", locationSeqIdCurrent);
        						childDetail.put("uomId", descriptionUomId);
        						rowDetail.add(childDetail);
    						
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetProductByFacilityIdPhysical(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("ListInventoryItemForPhysical", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> createPhysicalVariancesLog(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String temp = (String)context.get("quantityOnHandVar");
		String tempAvailableToPromiseVar = (String)context.get("availableToPromiseVar");
		GenericValue userlogin = (GenericValue)context.get("userLogin");
		String inventoryItemId = (String)context.get("inventoryItemId");
		BigDecimal quantityReason = new BigDecimal(temp);
		
		String variance = (String)context.get("varianceReasonId");
		
		int qohInput = quantityReason.intValue();
		GenericValue invItemList = null;
        try {
            invItemList = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralRuntimeException(e.getMessage());
        }

        
            if (invItemList != null) {
                /*int qoh = ((BigDecimal)invItemList.getBigDecimal("quantityOnHandTotal")).intValue();*/
            String commentQOHInput = null;
                if (qohInput < 0) {
                	commentQOHInput = "QOH less than stocktake correction";
                }
                else{
                	commentQOHInput = "QOH greater than or equal stocktake correction";
                }
                Map<String, Object> contextInput = UtilMisc.toMap("userLogin", userlogin, "inventoryItemId", invItemList.get("inventoryItemId"), "varianceReasonId", variance,"quantityOnHandVar", quantityReason, "comments", commentQOHInput);
                try {
                    dispatcher.runSync("createPhysicalInventoryAndVariance",contextInput);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "fixProductNegativeQOH failed on createPhysicalInventoryAndVariance invItemId"+invItemList.get("inventoryItemId"), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ProductErrorCreatePhysicalInventoryAndVariance", UtilMisc.toMap("inventoryItemId", invItemList.get("inventoryItemId")), locale));
                }
            }
        
        return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateProductIdByFacilityLocation(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String inventoryItemId = (String)context.get("inventoryItemId");
		String facilityId = (String)context.get("facilityId");
		String locationSeqId = (String) context.get("locationSeqId");
		String productId = (String) context.get("productId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		String uomId = (String) context.get("uomId");
		
		
		GenericValue inventoryItemLocation = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqId));
		if(inventoryItemId != null){
			inventoryItemLocation.put("productId", productId);
			inventoryItemLocation.put("quantity", quantity);
			inventoryItemLocation.put("uomId", uomId);
			delegator.store(inventoryItemLocation);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetContactMechInFacility(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("contactMechTypeId");
		/*List<String> orderBy = new ArrayList<String>();
		orderBy.add("contactMechTypeId");*/
		
    	String facilityId = parameters.get("facilityId")[0];
    	List<GenericValue> listContactMechInFacility = new ArrayList<GenericValue>();
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
    		opts.setDistinct(true);
    		listContactMechInFacility = delegator.findList("ContactMechInFacility", cond, fieldToSelects, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listContactMechInFacility)){
    			for(GenericValue contactMechType : listContactMechInFacility){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String contactMechTypeId = (String)contactMechType.get("contactMechTypeId");
    				row.put("contactMechTypeId", contactMechTypeId);
    				List<GenericValue> listDetail = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechTypeId", contactMechTypeId, "facilityId", facilityId)), null, null, null, false);
    				List<Map<String, String>> rowDetail = new ArrayList<Map<String,String>>();
    			
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
							Map<String, String> childDetail = new HashMap<String, String>();
							String contactMechId = (String) detail.get("contactMechId");
							String contactMechPurposeTypeId = (String) detail.get("contactMechPurposeTypeId");
        					String descriptionContactMechPurpuseType = (String) detail.get("descriptionContactMechPurpuseType");
        					String countryCode = (String) detail.get("countryCode");
        					String areaCode = (String) detail.get("areaCode");
        					String contactNumber = (String) detail.get("contactNumber");
        					String infoString = (String) detail.get("infoString");
        					String address1 = (String) detail.get("address1");
        					String toName = (String) detail.get("toName");
        					String attnName = (String) detail.get("attnName");
        					String address2 = (String) detail.get("address2");
        					String countryGeoId = (String) detail.get("countryGeoId");
        					String stateProvinceGeoId = (String) detail.get("stateProvinceGeoId");
        					String postalCode = (String) detail.get("postalCode");
        					String extension = (String) detail.get("extension");
        					childDetail.put("contactMechId", contactMechId);
        					childDetail.put("contactMechPurpuseTypeId", contactMechPurposeTypeId);
        					childDetail.put("descriptionContactMechPurpuseType", descriptionContactMechPurpuseType);
        					childDetail.put("countryCode", countryCode);
        					childDetail.put("areaCode", areaCode);
        					childDetail.put("contactNumber", contactNumber);
        					childDetail.put("infoString", infoString);
        					childDetail.put("address1", address1);
        					childDetail.put("toName", toName);
        					childDetail.put("attnName", attnName);
        					childDetail.put("address2", address2);
        					childDetail.put("postalCode", postalCode);
        					childDetail.put("countryGeoId", countryGeoId);
        					childDetail.put("stateProvinceGeoId", stateProvinceGeoId);
        					childDetail.put("extension", extension);
        					rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> loadContactMechTypePurposeList(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		Locale locale = (Locale)context.get("locale");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		GenericValue contactMechPurposeType = null;
		String contactMechPurposeTypeId = null;
		List<GenericValue> listContactTypePurpose = delegator.findList("ContactMechTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechTypeId", contactMechTypeId)), null, null, null, false);
		List<Map<String, String>> listcontactMechPurposeTypeMap = new ArrayList<Map<String,String>>();
		for (GenericValue contactTypePurpose : listContactTypePurpose) {
			if(contactTypePurpose != null){
				contactMechPurposeTypeId = (String) contactTypePurpose.get("contactMechPurposeTypeId");
				contactMechPurposeType = delegator.findOne("ContactMechPurposeType", false, UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId));
			}
			String description = (String) contactMechPurposeType.get("description", locale);
	    	Map<String, String> contactMechPurposeTypeMap =  new HashMap<String,String>(); 			
	    	contactMechPurposeTypeMap.put(contactMechPurposeTypeId, description);	  
			listcontactMechPurposeTypeMap.add(contactMechPurposeTypeMap);
			
		}
		result.put("listcontactMechPurposeTypeMap", listcontactMechPurposeTypeMap);
		return result;
	}
	
	public static Map<String, Object> loadGeoAssocListByGeoId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String geoId = (String)context.get("geoId");
		GenericValue geoAssoc = null;
		String geoIdTo = null;
		List<GenericValue> listGeoAssoc = delegator.findList("GeoAssoc", EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", "REGIONS")), null, null, null, false);
		List<Map<String, String>> listGeoAssocMap = new ArrayList<Map<String,String>>();
		for (GenericValue geoAssocData : listGeoAssoc) {
			if(geoAssocData != null){
				geoIdTo = (String) geoAssocData.get("geoIdTo");
				geoAssoc = delegator.findOne("Geo", false, UtilMisc.toMap("geoId", geoIdTo));
			}
			String geoName = (String) geoAssoc.get("geoName");
	    	Map<String, String> geoAssocMap =  new HashMap<String,String>(); 			
	    	geoAssocMap.put(geoIdTo, geoName);	  
	    	listGeoAssocMap.add(geoAssocMap);
			
		}
		result.put("listGeoAssocMap", listGeoAssocMap);
		return result;
	}
	
	public static Map<String, Object> createFacilityContactMechPostalAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String toName = (String)context.get("toName");
		String attnName = (String)context.get("attnName");
		String address1 = (String)context.get("address1");
		String address2 = (String)context.get("address2");
		String city = (String)context.get("city");
		String countryGeoId = (String)context.get("countryGeoId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String postalCode = (String)context.get("postalCode");
		if(numberOrNot(postalCode) == false){
			result.put("value", "postalCodeNotNumber");
		}else{
			long postalCodeInt = Long.parseLong(postalCode);
			if(postalCodeInt < 0){
				result.put("value", "postalCodeInt");
			}else{
				Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
				
				if(contactMechId == null){
					contactMechId = delegator.getNextSeqId("ContactMech");
				}
				if(address2 != null){
					if(address2.length() > 255){
						result.put("value", "address2MaxLength");
					}else{
						GenericValue contactMech = delegator.makeValue("ContactMech");
						contactMech.put("contactMechId", contactMechId);
						contactMech.put("contactMechTypeId", contactMechTypeId);
						delegator.create(contactMech);
						
						GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
						facilityContactMech.put("facilityId", facilityId);
						facilityContactMech.put("contactMechId", contactMechId);
						facilityContactMech.put("fromDate", nowTimeStamp);
						delegator.create(facilityContactMech);
						
						GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
						facilityContactMechPurpose.put("facilityId", facilityId);
						facilityContactMechPurpose.put("contactMechId", contactMechId);
						facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
						facilityContactMechPurpose.put("fromDate", nowTimeStamp);
						delegator.create(facilityContactMechPurpose);
						
						GenericValue postalAddress = delegator.makeValue("PostalAddress");
						postalAddress.put("contactMechId", contactMechId);
						postalAddress.put("toName", toName);
						postalAddress.put("attnName", attnName);
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.create(postalAddress);
						result.put("value", "success");
					}
				}else{
					GenericValue contactMech = delegator.makeValue("ContactMech");
					contactMech.put("contactMechId", contactMechId);
					contactMech.put("contactMechTypeId", contactMechTypeId);
					delegator.create(contactMech);
					
					GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
					facilityContactMech.put("facilityId", facilityId);
					facilityContactMech.put("contactMechId", contactMechId);
					facilityContactMech.put("fromDate", nowTimeStamp);
					delegator.create(facilityContactMech);
					
					GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
					facilityContactMechPurpose.put("facilityId", facilityId);
					facilityContactMechPurpose.put("contactMechId", contactMechId);
					facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
					facilityContactMechPurpose.put("fromDate", nowTimeStamp);
					delegator.create(facilityContactMechPurpose);
					
					GenericValue postalAddress = delegator.makeValue("PostalAddress");
					postalAddress.put("contactMechId", contactMechId);
					postalAddress.put("toName", toName);
					postalAddress.put("attnName", attnName);
					postalAddress.put("address1", address1);
					postalAddress.put("address2", address2);
					postalAddress.put("city", city);
					postalAddress.put("countryGeoId", countryGeoId);
					postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
					postalAddress.put("postalCode", postalCode);
					delegator.create(postalAddress);
					result.put("value", "success");
				}
			}
		}
		return result;
	}
	
	
	public static Map<String, Object> createContactMechTelecomNumber(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
		String extension = (String)context.get("extension");
		if(numberOrNot(countryCode) == false || numberOrNot(areaCode) == false || numberOrNot(contactNumber) == false){
			result.put("value", "notNumber");
		}else{
			long countryCodeInt = Long.parseLong(countryCode);
			long areaCodeInt = Long.parseLong(areaCode);
			long contactNumberInt = Long.parseLong(contactNumber);
			if(countryCodeInt < 0 || areaCodeInt < 0 || contactNumberInt < 0){
				result.put("value", "notNumber");
			}else{
				Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
				contactMechId = delegator.getNextSeqId("ContactMech");
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", contactMechId);
				contactMech.put("contactMechTypeId", contactMechTypeId);
				
				GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
				facilityContactMech.put("facilityId", facilityId);
				facilityContactMech.put("contactMechId", contactMechId);
				facilityContactMech.put("fromDate", nowTimeStamp);
				
				GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
				facilityContactMechPurpose.put("facilityId", facilityId);
				facilityContactMechPurpose.put("contactMechId", contactMechId);
				facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				facilityContactMechPurpose.put("fromDate", nowTimeStamp);
				
				GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
				if(extension != null){
					if(numberOrNot(extension) == false){
						result.put("value", "notNumber");
						return result;
					}else{
						long extensionInt = Long.parseLong(extension);
						if(extensionInt < 0){
							result.put("value", "notNumber");
							return result;
						}else{
							telecomNumber.put("contactMechId", contactMechId);
							telecomNumber.put("countryCode", countryCode);
							telecomNumber.put("areaCode", areaCode);
							telecomNumber.put("contactNumber", contactNumber);
							facilityContactMech.put("extension", extension);
							delegator.create(contactMech);
							delegator.create(facilityContactMech);
							delegator.create(facilityContactMechPurpose);
							delegator.create(telecomNumber);
						}
					}
				}else{
					telecomNumber.put("contactMechId", contactMechId);
					telecomNumber.put("countryCode", countryCode);
					telecomNumber.put("areaCode", areaCode);
					telecomNumber.put("contactNumber", contactNumber);
					delegator.create(contactMech);
					delegator.create(facilityContactMech);
					delegator.create(facilityContactMechPurpose);
					delegator.create(telecomNumber);
				}
				result.put("value", "success");
			}
		}
		return result;
	}
	
	public static Map<String, Object> createContactMechEmailAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String infoString = (String)context.get("infoString");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		
		if(contactMechId == null){
			contactMechId = delegator.getNextSeqId("ContactMech");
		}
		
		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.put("contactMechId", contactMechId);
		contactMech.put("contactMechTypeId", contactMechTypeId);
		contactMech.put("infoString", infoString);
		delegator.create(contactMech);
		
		GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
		facilityContactMech.put("facilityId", facilityId);
		facilityContactMech.put("contactMechId", contactMechId);
		facilityContactMech.put("fromDate", nowTimeStamp);
		delegator.create(facilityContactMech);
		
		GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
		facilityContactMechPurpose.put("facilityId", facilityId);
		facilityContactMechPurpose.put("contactMechId", contactMechId);
		facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		facilityContactMechPurpose.put("fromDate", nowTimeStamp);
		delegator.create(facilityContactMechPurpose);
			
		result.put("value", "success");
		return result;
	}
	
	public static Map<String, Object> createFacilityContactMechTelecomNumber(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
		String extension = (String)context.get("extension");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		
		if(countryCode.length() > 10 || areaCode.length() > 10 || extension.length() > 60){
			result.put("value", "maxLength");
		}else{
			if(contactMechId == null){
				contactMechId = delegator.getNextSeqId("ContactMech");
			}
			
			GenericValue contactMech = delegator.makeValue("ContactMech");
			contactMech.put("contactMechId", contactMechId);
			contactMech.put("contactMechTypeId", contactMechTypeId);
			delegator.create(contactMech);
			
			GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
			facilityContactMech.put("facilityId", facilityId);
			facilityContactMech.put("contactMechId", contactMechId);
			if(extension != ""){
				facilityContactMech.put("extension", extension);
			}
			facilityContactMech.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMech);
			
			GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
			facilityContactMechPurpose.put("facilityId", facilityId);
			facilityContactMechPurpose.put("contactMechId", contactMechId);
			facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			facilityContactMechPurpose.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMechPurpose);
			
			GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
			telecomNumber.put("contactMechId", contactMechId);
			telecomNumber.put("countryCode", countryCode);
			telecomNumber.put("areaCode", areaCode);
			telecomNumber.put("contactNumber", contactNumber);
			delegator.create(telecomNumber);
			result.put("value", "success");
		}
		return result;
	}
	
	public static Map<String, Object> createFacilityContactMechByEmailAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String) context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
		GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
		String infoString = (String) context.get("infoString");
		
		if(contactMechId == null){
			contactMechId = delegator.getNextSeqId("ContactMech");
		}
		
		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.put("contactMechId", contactMechId);
		contactMech.put("contactMechTypeId", contactMechTypeId);
		contactMech.put("infoString", infoString);
		delegator.create(contactMech);
		
		if(contactMechTypeId.equals("EMAIL_ADDRESS") == true || contactMechTypeId.equals("WEB_ADDRESS") == true|| contactMechTypeId.equals("LDAP_ADDRESS") == true){
			
			Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			facilityContactMech.put("facilityId", facilityId);
			facilityContactMech.put("contactMechId", contactMechId);
			facilityContactMech.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMech);
			
			
			facilityContactMechPurpose.put("facilityId", facilityId);
			facilityContactMechPurpose.put("contactMechId", contactMechId);
			facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			facilityContactMechPurpose.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMechPurpose);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLocationInFacility(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("LocationFacility", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInventoryItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    	List<GenericValue> listInventoryItem = delegator.findList("InventoryAndItemProduct", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<GenericValue> listInventoryItemTotal = delegator.findList("ProductNameByInventoryItemTotal", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue inventoryItemTotal : listInventoryItemTotal) {
    		String productIdTotal = inventoryItemTotal.getString("productId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(inventoryItemTotal);
			for (GenericValue inventoryItem : listInventoryItem) {
				String productId = inventoryItem.getString("productId");
				if (productIdTotal.equals(productId)) {
					listRowDetails.add(inventoryItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			for (GenericValue inv : listRowDetails){
				List<GenericValue> listInventoryItemLabel = delegator.findList("InventoryItemAndLabel", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inv.get("inventoryItemId"))), null, null, null, false);
		    	String statusId = null;
				if (!listInventoryItemLabel.isEmpty()){
		    		statusId = "INV_LABELED";
		    	} else {
		    		statusId = "INV_NO_LABEL";
		    	}
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("labelStatusId", statusId);
				mapTmp.put("inventoryItemId", inv.getString("inventoryItemId"));
				mapTmp.put("productId", inv.getString("productId"));
				mapTmp.put("expireDate", inv.getTimestamp("expireDate"));
				mapTmp.put("datetimeReceived", inv.getTimestamp("datetimeReceived"));
				mapTmp.put("facilityId", inv.getString("facilityId"));
				mapTmp.put("internalName", inv.getString("internalName"));
				mapTmp.put("quantityOnHandTotal", inv.getBigDecimal("quantityOnHandTotal"));
				mapTmp.put("availableToPromiseTotal", inv.getBigDecimal("availableToPromiseTotal"));
				mapTmp.put("quantityUomId", inv.getString("quantityUomId"));
				mapTmp.put("statusId", inv.getString("statusId"));
				
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> loadListLocationFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String) context.get("facilityId");
		List<GenericValue> listlocationFacilityList = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		List<GenericValue> listInventoryItem = new ArrayList<GenericValue>();
		Map<String, Object> listInventoryItemLocationDetailMap = new HashMap<String, Object>();
		for (GenericValue locationFacility : listlocationFacilityList) {
			String locationId = (String) locationFacility.get("locationId");
			listInventoryItem = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			listInventoryItemLocationDetailMap.put(locationId, listInventoryItem);
		}
		result.put("listlocationFacilityMap", listlocationFacilityList);
		result.put("listInventoryItemLocationDetailMap", listInventoryItemLocationDetailMap);
		return result;
		
	}
	
public static Map<String, Object> loadLocationFacilityType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = null;	
		List<GenericValue> listlocationFacilityTypeList = delegator.findList("LocationFacilityType", null, null, null, null, false);
		List<Map<String, String>> listlocationFacilityTypeMapData = new ArrayList<Map<String,String>>();
		for (GenericValue locationFacilityType : listlocationFacilityTypeList) {
			locationFacilityTypeId = (String) locationFacilityType.get("locationFacilityTypeId");
			String description = (String) locationFacilityType.get("description");
	    	Map<String, String> listLoactionFacilityTypeMap =  new HashMap<String,String>(); 			
	    	listLoactionFacilityTypeMap.put(locationFacilityTypeId, description);	  
	    	listlocationFacilityTypeMapData.add(listLoactionFacilityTypeMap);
			
		}
		result.put("listlocationFacilityTypeMap", listlocationFacilityTypeMapData);
		return result;
		
	}

	public static Map<String, Object> createNewLocationFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationId = null;	
		String locationCode = (String) context.get("locationCode");
		String facilityId = (String) context.get("facilityId");
		String parentLocationId = (String) context.get("parentLocationId");
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String description = (String) context.get("description");
		
		if(locationId == null){
			locationId = delegator.getNextSeqId("LocationFacility");
		}
		
		GenericValue locationFacility = delegator.makeValue("LocationFacility");
		locationFacility.put("locationId", locationId);
		locationFacility.put("locationCode", locationCode);
		locationFacility.put("facilityId", facilityId);
		locationFacility.put("parentLocationId", parentLocationId);
		locationFacility.put("locationFacilityTypeId", locationFacilityTypeId);
		locationFacility.put("description", description);
		delegator.create(locationFacility);
		
		List<GenericValue> listInventoryItemLocation = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", parentLocationId)), null, null, null, false);
		GenericValue inventoryItemLocationToData = delegator.makeValue("InventoryItemLocation");
		
		if(listInventoryItemLocation != null){
			for (GenericValue inventoryItemLocation : listInventoryItemLocation) {
				String inventoryItemId = (String) inventoryItemLocation.get("inventoryItemId");
				String productId = (String) inventoryItemLocation.get("productId");
				BigDecimal quantity = (BigDecimal) inventoryItemLocation.get("quantity");
				String uomId = (String) inventoryItemLocation.get("uomId");
				
				inventoryItemLocationToData.put("inventoryItemId", inventoryItemId);
				inventoryItemLocationToData.put("productId", productId);
				inventoryItemLocationToData.put("locationId", locationId);
				inventoryItemLocationToData.put("quantity", quantity);
				inventoryItemLocationToData.put("uomId", uomId);
				delegator.create(inventoryItemLocationToData);
				
				delegator.removeValue(inventoryItemLocation);
			}
		}
		
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListLocationFacilityType(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("locationFacilityTypeId", EntityOperator.EQUALS, "POSITION"));
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		List<GenericValue> listLoactionFacilityByPosition = delegator.findList("LocationFacility", tmpConditon, null, null, null, false);
    		for (GenericValue locationFacilityByPosition : listLoactionFacilityByPosition) {
				
    			String locationTypeIdPOSITION = (String) locationFacilityByPosition.get("locationTypeId");
    			String descriptionPOSITION = (String) locationFacilityByPosition.get("description");
    			
    			
    			String parentLocationIdPosition = (String) locationFacilityByPosition.get("parentLocationId");
				GenericValue locationFacilityByLocationId = delegator.findOne("LocationFacility", false, UtilMisc.toMap("locationId", parentLocationIdPosition));
				String locationTypeIdLEVEL = (String) locationFacilityByLocationId.get("locationTypeId");
				String descriptionLEVEL = (String) locationFacilityByLocationId.get("description");
				
				String parentLocationIdLevel = (String) locationFacilityByLocationId.get("parentLocationId");
				GenericValue locationFacilityByLocationIdLevel = delegator.findOne("LocationFacility", false, UtilMisc.toMap("locationId", parentLocationIdLevel));
				String locationTypeIdSECTION = (String) locationFacilityByLocationIdLevel.get("locationTypeId");
				String descriptionSECTION = (String) locationFacilityByLocationIdLevel.get("description");
				
				String parentLocationIdSection = (String) locationFacilityByLocationIdLevel.get("parentLocationId");
				GenericValue locationFacilityByLocationIdSection = delegator.findOne("LocationFacility", false, UtilMisc.toMap("locationId", parentLocationIdSection));
				String locationTypeIdAISLE = (String) locationFacilityByLocationIdSection.get("locationTypeId");
				String descriptionAISLE = (String) locationFacilityByLocationIdSection.get("description");
				
				String parentLocationIdAisle = (String) locationFacilityByLocationIdSection.get("parentLocationId");
				GenericValue locationFacilityByLocationIdAisle = delegator.findOne("LocationFacility", false, UtilMisc.toMap("locationId", parentLocationIdAisle));
				String locationTypeIdAREA = (String) locationFacilityByLocationIdAisle.get("locationTypeId");
				String descriptionAREA = (String) locationFacilityByLocationIdAisle.get("description");
				
				Map<String, Object> childDetailPOSITION = new HashMap<String, Object>();
				Map<String, Object> rowPOSITION = new HashMap<String, Object>();
				
				childDetailPOSITION.put("locationTypeId", locationTypeIdPOSITION);
				childDetailPOSITION.put("description", descriptionPOSITION);
				
				//List<Map<String, Object>> listMapPOSITION = new ArrayList<Map<String,Object>>();
				//listMapPOSITION.add(childDetailPOSITION);
				
				//rowPOSITION.put("rowDetail", listMapPOSITION);
				rowPOSITION.put("locationTypeId", locationTypeIdLEVEL);
				rowPOSITION.put("description", descriptionLEVEL);
				//List<Map<String, Object>> listMapLEVEL = new ArrayList<Map<String,Object>>();
				//listMapLEVEL.add(rowPOSITION);
				
				Map<String, Object> rowLEVEL = new HashMap<String, Object>();
				//rowLEVEL.put("rowDetail", listMapLEVEL);
				rowLEVEL.put("locationTypeId", locationTypeIdSECTION);
				rowLEVEL.put("description", descriptionSECTION);
				//List<Map<String, Object>> listMapSECTION = new ArrayList<Map<String,Object>>();
				//listMapSECTION.add(rowLEVEL);
				
				Map<String, Object> rowSECTION = new HashMap<String, Object>();
				//rowSECTION.put("rowDetail", listMapSECTION);
				rowSECTION.put("locationTypeId", locationTypeIdAISLE);
				rowSECTION.put("description", descriptionAISLE);
				
				Map<String, Object> rowAISLE = new HashMap<String, Object>();
				rowAISLE.put("locationTypeId", locationTypeIdAREA);
				rowAISLE.put("description", descriptionAREA);
				
				List<Map<String, Object>> listMapAISLE = new ArrayList<Map<String,Object>>();
				listMapAISLE.add(rowPOSITION);
				listMapAISLE.add(rowLEVEL);
				listMapAISLE.add(rowSECTION);
				listMapAISLE.add(rowAISLE);
				
				Map<String, Object> rowPOSITION2 = new HashMap<String, Object>();
				rowPOSITION2.put("rowDetail", listMapAISLE);
				rowPOSITION2.put("locationTypeId", locationTypeIdPOSITION);
				rowPOSITION2.put("description", descriptionPOSITION);
				//List<Map<String, Object>> listMapAREA = new ArrayList<Map<String,Object>>();
				//listMapAREA.add(rowAISLE);
				
				//Map<String, Object> row = new HashMap<String, Object>();
				//row.put("rowDetail", listMapAREA);
				listIterator.add(rowPOSITION2);
    		} 
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProducts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> loadLocationFacilityTypeId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String) context.get("facilityId");
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		GenericValue listlocationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		String parentLocationFacilityTypeId = (String) listlocationFacilityType.get("parentLocationFacilityTypeId");
		List<GenericValue> listlocationFacilityList = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "locationFacilityTypeId", parentLocationFacilityTypeId)), null, null, null, false);
		result.put("listlocationFacilityMap", listlocationFacilityList);
		return result;
		
	}
	
	public static boolean numberOrNot(String input)
    {
        try
        {
//            Integer.parseInt(input);
            Long.parseLong(input);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        return true;
    }
	@SuppressWarnings("unchecked")
	public static Map<String,Object> addProductInLocationFacility(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException, ParseException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	
	    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
	    	
	    	List<String> listLocationId = (List<String>)context.get("locationId[]");
			List<String> listProductId = (List<String>)context.get("productId[]");
	    	List<String> listInventoryItemId = (List<String>)context.get("inventoryItemId[]");
	    	List<String> listQuantity = (List<String>)context.get("quantity[]");
	    	List<String> listUomId = (List<String>)context.get("uomId[]");
	    	
	    	for(int i = 0; i < listLocationId.size(); i++){
	    		String locationId = listLocationId.get(i);
	    		String productId = listProductId.get(i);
	    		String inventoryItemId = listInventoryItemId.get(i);
	    		String quantity = listQuantity.get(i);
	    		String uomId = listUomId.get(i);
	    		GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
	    		/*List<GenericValue> listInventotyItemLocation = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);*/
	    		
	    		if(numberOrNot(quantity) == true){
	    			BigDecimal qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
		    		int qohData = qoh.intValue();
		    		
		    		BigDecimal quantityToDataBigTo = new BigDecimal(quantity);
		    		int quantityToDataCheck = quantityToDataBigTo.intValue();
		    		
		    		/*for (GenericValue inventory : listInventotyItemLocation) {
		    			BigDecimal quantityBigToData = inventory.getBigDecimal("quantity");
		    			
					}*/
	    			if(quantityToDataCheck > qohData){
		    			result.put("value", "checkQoh");
		    		}else{
		    			if(quantityToDataCheck == 0 || quantityToDataCheck < 0){
			    			result.put("value", "negative");
			    		}else{
			    			GenericValue inventoryItemLocationInData = delegator.findOne("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId, "uomId", uomId), false);
							if(inventoryItemLocationInData != null){
								BigDecimal quantityByData = inventoryItemLocationInData.getBigDecimal("quantity");
									int quantityToData = Integer.parseInt(quantity);
									int quantityByDataInt = quantityByData.intValue();
									int quantitySum = quantityByDataInt + quantityToData;
									String quantityToDataStr = Integer.toString(quantitySum);
									BigDecimal quantityToDataBig = new BigDecimal(quantityToDataStr);
									inventoryItemLocationInData.put("quantity", quantityToDataBig);
									delegator.store(inventoryItemLocationInData);
							}else{
								inventoryItemLocation.put("inventoryItemId", inventoryItemId);
								inventoryItemLocation.put("productId", productId);
								inventoryItemLocation.put("locationId", locationId);
								inventoryItemLocation.put("quantity", quantityToDataBigTo);
								inventoryItemLocation.put("uomId", uomId);
								try {
									delegator.create(inventoryItemLocation);
								} catch (Exception e) {
									result.put("value", "error555");
									return result;
								}
								
							}
							result.put("value", "success");
			    		}
		    		}
	    		}else{
	    			result.put("value", "number");
	    		}
	    	}
	    	return result;
	}
	
	public static Map<String, Object> loadExpireDateByProductIdInInventoryItem(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String) context.get("facilityId");
		String productId = (String) context.get("productId");
		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId)), null, null, null, false);
		List<GenericValue> listConfigPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
		result.put("listExpireDate", listInventoryItem);
		result.put("listConfigPacking", listConfigPacking);
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadRowDetailsByLocationId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listLocationId = (List<String>)context.get("locationId[]");
		Map<String, Object> listInventoryItemLocationByLocationIdMap = new HashMap<String, Object>();
		for (String locationId : listLocationId) {
			List<GenericValue> listInventoryItem = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			listInventoryItemLocationByLocationIdMap.put(locationId, listInventoryItem);
		}
		result.put("listInventoryItemLocationDetailsByLocationIdMap", listInventoryItemLocationByLocationIdMap);
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadProductByLocationIdInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listLocationId = (List<String>)context.get("locationIdTranfer[]");
		Map<String, Object> listMapProductLocationInFacility = new HashMap<String, Object>();
		for (String locationId : listLocationId) {
			List<GenericValue> listInventoryItem = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			listMapProductLocationInFacility.put(locationId, listInventoryItem);
		}
		result.put("listMapProductLocationInFacility", listMapProductLocationInFacility);
		return result;
		
	}
	
	public static Map<String, Object> createLocationFacilityTypeInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String parentLocationFacilityTypeId = (String) context.get("parentLocationFacilityTypeId");
		String description = (String) context.get("description");
		
		GenericValue locationFacilityType = delegator.makeValue("LocationFacilityType");
		GenericValue locationFacilityTypeCheck = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		if(description.length() > 10000){
			result.put("value", "descriptionMaxLength");
		}else{
			if(locationFacilityTypeCheck != null){
				result.put("value", "error");
			}else{
				locationFacilityType.put("locationFacilityTypeId", locationFacilityTypeId);
				locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
				locationFacilityType.put("description", description);
				delegator.create(locationFacilityType);
				result.put("value", "success");
			}
		}
		return result;
		
	}
	
	public static Map<String, Object> updateLocationFacilityTypeInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String parentLocationFacilityTypeId = (String) context.get("parentLocationFacilityTypeId");
		String description = (String) context.get("description");
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		String descriptionData = (String) locationFacilityType.get("description");
		String parentLocationFacilityTypeIdData = (String) locationFacilityType.get("parentLocationFacilityTypeId");
		if(parentLocationFacilityTypeId != null){
			GenericValue locationFacilityTypeCheckParent = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", parentLocationFacilityTypeId), false);
			String parentLocationFacilityIdData = (String) locationFacilityTypeCheckParent.get("parentLocationFacilityTypeId");
			if(parentLocationFacilityTypeId.equals(locationFacilityTypeId)){
				result.put("value", "errorParent");
				return result;
			}
			int check = checkParentLocationFacilityType(dpx, parentLocationFacilityTypeId, locationFacilityTypeId);
			if(check == 2){
				result.put("value", "parentError");
				return result;
			}
			if(check == 0){
				if(parentLocationFacilityIdData != null){
					if(parentLocationFacilityIdData.equals(locationFacilityTypeId)){
						result.put("value", "parentError");
						return result;
					}
				}
				
				if(parentLocationFacilityTypeIdData != null){
					if(descriptionData.equals(description) && parentLocationFacilityTypeIdData.equals(parentLocationFacilityTypeId)){
						result.put("value", "notEdit");
						return result;
					}else{
						
						locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
						locationFacilityType.put("description", description);
						delegator.store(locationFacilityType);
						result.put("value", "success");
					}
				}
				
				if(parentLocationFacilityTypeIdData == null){
					locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
					locationFacilityType.put("description", description);
					delegator.store(locationFacilityType);
					result.put("value", "success");
				}
			}
		}else{
				if(parentLocationFacilityTypeIdData == null){
					if(descriptionData.equals(description)){
						result.put("value", "notEdit");
					}else{
						locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
						locationFacilityType.put("description", description);
						delegator.store(locationFacilityType);
						result.put("value", "success");
					}
				}else{
					locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
					locationFacilityType.put("description", description);
					delegator.store(locationFacilityType);
					result.put("value", "success");
				}
		}
		return result;
	}
	
	public static int checkParentLocationIdInLocationFacilityType(String locationFacilityTypeId, DispatchContext dpx) throws GenericEntityException{
		int index = -1;
		Delegator delegator = dpx.getDelegator();
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		List<GenericValue> listLocationFacility = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId)), null, null, null, false);
		List<GenericValue> listLocationFacilityTypeByLocationFacilityTypeId = delegator.findList("LocationFacilityType", EntityCondition.makeCondition(UtilMisc.toMap("parentLocationFacilityTypeId", locationFacilityTypeId)), null, null, null, false);
		if(listLocationFacility.isEmpty()){
			if(listLocationFacilityTypeByLocationFacilityTypeId.isEmpty()){
				delegator.removeValue(locationFacilityType);
				index = 0;
				return index;
			}else{
				boolean check = true;
				for (GenericValue locationFacilityTypeByParentLocationFacilityTypeId : listLocationFacilityTypeByLocationFacilityTypeId) {
					String locationFacilityTypeIdByParent = (String) locationFacilityTypeByParentLocationFacilityTypeId.get("locationFacilityTypeId");
					int checkIndex = checkParentLocationIdInLocationFacilityType(locationFacilityTypeIdByParent, dpx);
					index = checkIndex;
					if(index == 1){
						check = false;
					}
				}
				if(check){
					delegator.removeValue(locationFacilityType);
					index = 0;
					return index;
				}else{
					index = 1;
					return index;
				}
			}
		}else{
			index =  1;
			return index;
		}
	}
	
	
	public static Map<String, Object> deleteLocationFacilityTypeByLocationFacilityTypeId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String)context.get("locationFacilityTypeId");
		int indexCheck = checkParentLocationIdInLocationFacilityType(locationFacilityTypeId, dpx);
		if(indexCheck == 0){
			result.put("value", "success");
		}else{
			result.put("value", "error");
		}
		return result;
	}
	
	public static Map<String, Object> loadListLocationFacilityTypeInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listlocationFacilityType = delegator.findList("LocationFacilityType", null , null, null, null, false);
		result.put("listlocationFacilityTypeMap", listlocationFacilityType);
		return result;
		
	}
	
	public static Map<String, Object> loadParentLocationFacilityTypeId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		if(locationFacilityType != null){
			result.put("value", "exists");
		}
		else{
			result.put("value", "success");
		}
		return result;
		
	}
	
	public static Map<String, Object> loadParentLocationFacilityTypeIdInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listParentLocationFacilityTypeMap = delegator.findList("LocationFacilityType", null, null, null, null, false);
		result.put("listParentLocationFacilityTypeMap", listParentLocationFacilityTypeMap);
		return result;
	}
	
	public static String updateLungTung(Delegator delegator, String locationId, String parentLocationId, String locationFacilityTypeId, String locationCode, String description) throws GenericEntityException{
		GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
		String result = new String();
		if(locationFacility != null){
			List<GenericValue> listInventoryItemLocationByParentLocationId = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", parentLocationId)), null, null, null, false);
			if(listInventoryItemLocationByParentLocationId != null){
				for (GenericValue inventoryItemLocation : listInventoryItemLocationByParentLocationId) {
					if(inventoryItemLocation != null){
						String inventoryItemId = (String) inventoryItemLocation.get("inventoryItemId");
						String productId = (String) inventoryItemLocation.get("productId");
						BigDecimal quantityBig = (BigDecimal) inventoryItemLocation.get("quantity");
						String uomId = (String) inventoryItemLocation.get("uomId");
						
						GenericValue inventoryItemLocationByLocationId = delegator.findOne("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId, "uomId", uomId), false);
						GenericValue inventoryItemLocationAdd = delegator.makeValue("InventoryItemLocation");
						if(inventoryItemLocationByLocationId != null){
							BigDecimal quantityDataBig = (BigDecimal) inventoryItemLocationByLocationId.get("quantity");
							int quantityInt = quantityBig.intValue();
							int quantityDataInt = quantityDataBig.intValue();
							int quantityUpdate = quantityInt + quantityDataInt;
							String quantityUpdateStr = Integer.toString(quantityUpdate);
							BigDecimal quantityBigToData = new BigDecimal(quantityUpdateStr);
							inventoryItemLocationByLocationId.put("quantity", quantityBigToData);
							delegator.store(inventoryItemLocationByLocationId);
						}else{
							inventoryItemLocationAdd.put("inventoryItemId", inventoryItemId);
							inventoryItemLocationAdd.put("productId", productId);
							inventoryItemLocationAdd.put("locationId", locationId);
							inventoryItemLocationAdd.put("quantity", quantityBig);
							inventoryItemLocationAdd.put("uomId", uomId);
							delegator.create(inventoryItemLocationAdd);
						}
						delegator.removeValue(inventoryItemLocation);
					}
				}
			}
			locationFacility.put("locationCode", locationCode);
			locationFacility.put("parentLocationId", parentLocationId);
			locationFacility.put("description", description);
			delegator.store(locationFacility);
			result = "success";
		}
		else{
			result = "error";
		}
		return result;
	}
	
	public static Map<String, Object> updateLocationFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationId = (String) context.get("locationId");
		String parentLocationId = (String) context.get("parentLocationId");
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String locationCode = (String) context.get("locationCode");
		String description = (String) context.get("description");
		GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
		
		String parentLocationIdCheck = (String) locationFacility.get("parentLocationId");
		String locationCodeCheck = (String) locationFacility.get("locationCode");
		String descriptionCheck = (String) locationFacility.get("description");
		if(parentLocationId != null){
			if(parentLocationIdCheck != null){
				if(parentLocationIdCheck.equals(parentLocationId) && locationCodeCheck.equals(locationCode) == true && description.equals(descriptionCheck) == true){
					result.put("value", "bbbb");
				}else{
					GenericValue locationFacilityTypeCheckParent = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", parentLocationId), false);
					String parentLocationFacilityIdData = (String) locationFacilityTypeCheckParent.get("parentLocationId");
					if(parentLocationFacilityIdData != null){
						if(parentLocationFacilityIdData.equals(locationId) == true){
							result.put("value", "parentError");
							return result;
						}	
					}
					if(parentLocationId.equals(locationId) == true){
						result.put("value", "errorParent");
						return result;
					}
					String resultString = updateLungTung(delegator, locationId, parentLocationId, locationFacilityTypeId, locationCode, description);
					if(resultString == "success"){
						result.put("value", "success");
					}else{
						result.put("value", "error");
					}
				}
			}else{
				if(parentLocationIdCheck == parentLocationId && locationCodeCheck.equals(locationCode) == true && description.equals(descriptionCheck) == true){
					result.put("value", "bbbb");
				}else{
					GenericValue locationFacilityTypeCheckParent = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", parentLocationId), false);
					String parentLocationFacilityIdData = (String) locationFacilityTypeCheckParent.get("parentLocationId");
					if(parentLocationFacilityIdData != null){
						if(parentLocationFacilityIdData.equals(locationId) == true){
							result.put("value", "parentError");
							return result;
						}	
					}
					if(parentLocationId.equals(locationId) == true){
						result.put("value", "errorParent");
						return result;
					}
					String resultString = updateLungTung(delegator, locationId, parentLocationId, locationFacilityTypeId, locationCode, description);
					if(resultString == "success"){
						result.put("value", "success");
					}else{
						result.put("value", "error");
					}
				}
			}
		}
		else{
			if(locationCodeCheck.equals(locationCode) == true && description.equals(descriptionCheck) == true){
				result.put("value", "bbbb");
			}else{
				GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
				String parentLocationFacilityTypeId = (String) locationFacilityType.get("parentLocationFacilityTypeId");
				
				if(parentLocationFacilityTypeId == null){
					String resultString = updateLungTung(delegator, locationId, parentLocationId, locationFacilityTypeId, locationCode, description);
					if(resultString == "success"){
						result.put("value", "success");
					}else{
						result.put("value", "error");
					}
				}else{
					result.put("value", "parentLocationFacilityTypeId");
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deleteLocationFacilityType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listLocationId = (List<String>)context.get("locationId[]");
		for (String locationId : listLocationId) {
			GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
			List<GenericValue> listInventoryItemLocation = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			if(listInventoryItemLocation.isEmpty() == true){
				if(locationFacility != null){
					delegator.removeValue(locationFacility);
					result.put("value", "success");
				}
			}else{
				result.put("value", "error");
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadDataRowToJqxGirdTree(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listLocationId = (List<String>)context.get("locationId[]");
		List<String> listLocationIdRemain = (List<String>)context.get("locationIdRemain[]");
		List<GenericValue> listLocationFacility = new ArrayList<GenericValue>();
		List<GenericValue> listLocationFacilityRemain = new ArrayList<GenericValue>();
		for (String locationId : listLocationId) {
			GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
			listLocationFacility.add(locationFacility);
		}
		for (String locationIdRemain : listLocationIdRemain) {
			GenericValue locationFacilityRemain = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationIdRemain), false);
			listLocationFacilityRemain.add(locationFacilityRemain);
		}
		result.put("listLocationFacility", listLocationFacility);
		result.put("listLocationFacilityRemain", listLocationFacilityRemain);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadDataRowToJqxGirdTreeAddProduct(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listLocationId = (List<String>)context.get("locationId[]");
		List<String> listLocationIdRemain = (List<String>)context.get("locationIdRemain[]");
		List<GenericValue> listLocationFacility = new ArrayList<GenericValue>();
		List<GenericValue> listLocationFacilityRemain = new ArrayList<GenericValue>();
		for (String locationId : listLocationId) {
			GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
			listLocationFacility.add(locationFacility);
		}
		result.put("listLocationFacility", listLocationFacility);
		return result;
	}
	
	public static Map<String,Object> addInventoryItemForFacility(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
		Locale locale = (Locale)context.get("locale");
    	
    	
    	Map<String, Object> result = new FastMap<String, Object>();
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation");
    	
    	
    	String productId = (String)context.get("productId");
    	String facilityId = (String)context.get("facilityId");
    	String locationSeqId = (String)context.get("locationSeqId");
    	String locationSeqIdCurrent = (String)context.get("locationSeqIdCurrent");
    	String quantity = (String)context.get("quantity");
    	String uomId = (String)context.get("uomId");
    	String inventoryItemId = null;
    	
    	
    	BigDecimal quantityCastInput =  BigDecimal.ZERO;
    	NumberFormat fm = NumberFormat.getInstance(locale);
		if (quantity != null){
			try {
				quantityCastInput = new BigDecimal(fm.parse(quantity).toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
    	
    	
    	
    	List<GenericValue> listAll = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
    	for (GenericValue list : listAll) {
			inventoryItemId = (String) list.get("inventoryItemId");
		}
    	
    	GenericValue inventoryItems = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
    	
    	/*List<GenericValue> listAll = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);*/
    	
    	BigDecimal quantityOnHandTotal = inventoryItems.getBigDecimal("quantityOnHandTotal");
    	
		int quantityOnHandTotalData = quantityOnHandTotal.intValue();
		int quantityCast = Integer.parseInt(quantity);
		
		GenericValue inventoryItemLocationCurrentData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdCurrent));
		GenericValue inventoryItemLocationToData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqId));
		
		
		/*if(locationSeqIdData != null){*/
			/*GenericValue inventoryItemLocationToData = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemId, "facilityId", facilityId, "locationSeqId", locationSeqIdTo));*/
			if(inventoryItemLocationCurrentData != null){
				String facilityIdDataCurrent = (String) inventoryItemLocationCurrentData.get("facilityId"); 
				String locationSeqIdDataCurrent = (String) inventoryItemLocationCurrentData.get("locationSeqId"); 
				BigDecimal quantityDataCurrent = (BigDecimal) inventoryItemLocationCurrentData.get("quantity");
				String uomIdDataCurrent = (String) inventoryItemLocationCurrentData.get("uomId");
				
				int quantityCastCurrent = quantityDataCurrent.intValue();
				
				if(inventoryItemLocationToData == null){
					if(quantityCast > quantityOnHandTotalData){
						return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkQuantityOnHandTotal", locale));
					}
					/*for (GenericValue list : listAll) {
						inventoryItemId = (String) list.get("inventoryItemId");
						
					}*/
					inventoryItemLocation.put("productId", productId);
					inventoryItemLocation.put("inventoryItemId", inventoryItemId);
			    	inventoryItemLocation.put("facilityId", facilityId);
			    	inventoryItemLocation.put("locationSeqId", locationSeqId);
			    	inventoryItemLocation.put("quantity", quantityCastInput);
			    	inventoryItemLocation.put("uomId", uomId);
			    	delegator.create(inventoryItemLocation);
			    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "CreateSucessful", locale));
				} 
				
				BigDecimal quantityDataTo = (BigDecimal) inventoryItemLocationToData.get("quantity");
				int quantityCastTo = quantityDataTo.intValue();
				
				if(uomId.equals(uomIdDataCurrent) == false){
					return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkUomId", locale));
				}
				
				if((facilityId.equals(facilityIdDataCurrent) == true) && (locationSeqId.equals(locationSeqIdDataCurrent) == true) && (uomId.equals(uomIdDataCurrent) == true)){
					return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "StockLocationError", locale));
				}
				if((facilityId.equals(facilityIdDataCurrent) == true) && (uomId.equals(uomIdDataCurrent) == true)){
					if(quantityCast > quantityCastCurrent){
						return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "CheckQuantityCurrent", locale));
					}else{
						int quantityInputCurrent = quantityCastCurrent - quantityCast;
						String quantityCurrent = String.valueOf(quantityInputCurrent);
						
						inventoryItemLocationCurrentData.put("quantity", quantityCurrent);
						delegator.store(inventoryItemLocationCurrentData);
						
						int quantityInputTo = quantityCastTo + quantityCast;
						String quantityIIL = String.valueOf(quantityInputTo);
						
						inventoryItemLocationToData.put("quantity", quantityIIL);
						delegator.store(inventoryItemLocationToData);
						return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysSalesUiLabels", "StockLocationInventoryItemSuccess", locale));
					}
				}
			}
		/*}*/
		if(quantityCast > quantityOnHandTotalData){
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysSalesUiLabels", "checkQuantityOnHandTotal", locale));
		}
		/*for (GenericValue list : listAll) {
			String productId = (String) list.get("productId");
			
		}*/
		inventoryItemLocation.put("productId", productId);
    	inventoryItemLocation.put("inventoryItemId", inventoryItemId);
    	inventoryItemLocation.put("facilityId", facilityId);
    	inventoryItemLocation.put("locationSeqId", locationSeqId);
    	inventoryItemLocation.put("quantity", quantityCastInput);
    	inventoryItemLocation.put("uomId", uomId);
    	delegator.create(inventoryItemLocation);
    	
    	result.put("facilityId", facilityId);
    	result.put("locationSeqId", locationSeqId);
		return result;
	}
	
	
	public static Map<String, Object> tranfersProductFromLocationToLocationInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationIdCurrent = (String)context.get("locationIdCurrent");
		List<String> listInventoryItemIdTranfers = (List<String>)context.get("inventoryItemIdTranfers[]");
		List<String> listLocationIdTranfers = (List<String>)context.get("locationIdTranfers[]");
		List<String> listProductIdTranfers = (List<String>)context.get("productIdTranfers[]");
		List<String> listQuantityCurrentTranfers = (List<String>)context.get("quantityCurrentTranfers[]");
		List<String> listQuantityTranferTranfer = (List<String>)context.get("quantityTranferTranfer[]");
		List<String> listUomIdTranfer = (List<String>)context.get("uomIdTranfer[]");
		
		
		GenericValue inventortItemLocationCurrent = null;
		GenericValue inventortItemLocationTranfer = null;
		GenericValue inventoryItemLocationCurrentData = delegator.makeValue("InventoryItemLocation");
		
		for (int i = 0; i < listInventoryItemIdTranfers.size(); i++) {
			String inventoryItemIdTranfer = listInventoryItemIdTranfers.get(i);
			String locationIdTranfer = listLocationIdTranfers.get(i);
			String productIdTranfer = listProductIdTranfers.get(i);
			String quantityCurrentTranfers = listQuantityCurrentTranfers.get(i);
			String quantityTranferTranfers = listQuantityTranferTranfer.get(i);
			String uomIdTranferTranfers = listUomIdTranfer.get(i);
			
			inventortItemLocationCurrent = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemIdTranfer, "locationId", locationIdCurrent, "uomId", uomIdTranferTranfers));
			inventortItemLocationTranfer = delegator.findOne("InventoryItemLocation", false, UtilMisc.toMap("inventoryItemId", inventoryItemIdTranfer, "locationId", locationIdTranfer, "uomId", uomIdTranferTranfers));	
			
			if(numberOrNot(quantityTranferTranfers) == true){
				BigDecimal quantityBig = new BigDecimal(quantityTranferTranfers);
				int quantityCurrentTranfersInt = Integer.parseInt(quantityCurrentTranfers);
				int quantityTranferTranfersInt = Integer.parseInt(quantityTranferTranfers);
				
				if(quantityTranferTranfersInt < 0 || quantityTranferTranfersInt == 0){
					result.put("value", "negative");
					return result;
				}
				else{
					if(quantityTranferTranfersInt <= quantityCurrentTranfersInt){
						
						int quantitySumTranfer = quantityCurrentTranfersInt - quantityTranferTranfersInt;
						if(quantitySumTranfer == 0){
							delegator.removeValue(inventortItemLocationTranfer);
						}
						else{
							String quantityStringSumTranfer = String.valueOf(quantitySumTranfer);
							BigDecimal quantityBigSumTranfer = new BigDecimal(quantityStringSumTranfer);
							inventortItemLocationTranfer.put("quantity", quantityBigSumTranfer);
							delegator.store(inventortItemLocationTranfer);
						}
						
						if(inventortItemLocationCurrent != null){
								BigDecimal quantityCurrentData = (BigDecimal) inventortItemLocationCurrent.get("quantity");
								String value = String.valueOf(quantityCurrentData.intValue());
								
								int quantityCurrentByData = Integer.parseInt(value);
								int quantityCurrentInputToData = quantityCurrentByData + quantityTranferTranfersInt;
								String quantityStringCurrentData = String.valueOf(quantityCurrentInputToData);
								BigDecimal quantityBigCurrentData = new BigDecimal(quantityStringCurrentData);
								
								inventortItemLocationCurrent.put("quantity", quantityBigCurrentData);
								delegator.store(inventortItemLocationCurrent);
						}
						else{
							inventoryItemLocationCurrentData.put("inventoryItemId", inventoryItemIdTranfer);
							inventoryItemLocationCurrentData.put("productId", productIdTranfer);
							inventoryItemLocationCurrentData.put("locationId", locationIdCurrent);
							inventoryItemLocationCurrentData.put("quantity", quantityBig);
							inventoryItemLocationCurrentData.put("uomId", uomIdTranferTranfers);
							delegator.create(inventoryItemLocationCurrentData);
						}
						result.put("value", "success");
					}else{
						result.put("value", "errorQuantityTranfer");
					}
				}
			}else{
				result.put("value", "number");
			}
		}
		return result;
	}
	
	public static Map<String, Object> loadParentLocationFacilityByLocationFacilityTypeId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		String parentLocationFacilityTypeId = (String) locationFacilityType.get("parentLocationFacilityTypeId");
		List<GenericValue> listParentLocationFacilityTypeId = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("locationFacilityTypeId", parentLocationFacilityTypeId)), null, null, null, false);
		result.put("listParentLocationFacilityTypeId", listParentLocationFacilityTypeId);
		return result;
	}
	
	public static int checkParentLocationIdInLocationFacility(String locationId, DispatchContext dpx) throws GenericEntityException{
		int index = -1;
		Delegator delegator = dpx.getDelegator();
		GenericValue locationFacility = delegator.findOne("LocationFacility", UtilMisc.toMap("locationId", locationId), false);
		List<GenericValue> listInventoryItemLocation = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
		List<GenericValue> listLocationFacilityByLocationId = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("parentLocationId", locationId)), null, null, null, false);
		if(listLocationFacilityByLocationId.isEmpty()){
			if(listInventoryItemLocation.isEmpty()){
				delegator.removeValue(locationFacility);
				index = 0;
				return index;
			}else{
				index =  1;
				return index;
			}
		}else{
			boolean check = true;
			for (GenericValue locationFacilityByParentLocationId : listLocationFacilityByLocationId) {
				String locationIdByParent = (String) locationFacilityByParentLocationId.get("locationId");
				int checkIndex = checkParentLocationIdInLocationFacility(locationIdByParent, dpx);
				index = checkIndex;
				if(index == 1){
					check = false;
				}
			}
			if(check){
				delegator.removeValue(locationFacility);
				index = 0;
				return index;
			}else{
				index = 1;
				return index;
			}
		}
	}
	
	
	public static Map<String, Object> deleteLocationFacilityNotParentLocation(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String locationId = (String)context.get("locationId");
		
		int indexCheck = checkParentLocationIdInLocationFacility(locationId, dpx);
		if(indexCheck == 0){
			result.put("value", "success");
		}else{
			result.put("value", "error");
		}

		return result;
	}
	
	public static Map<String, Object> loadDetailContactMechInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechTypeId = (String) context.get("contactMechTypeId");
		List<GenericValue> listDetailContactMechInFacility = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechTypeId", contactMechTypeId)), null, null, null, false);
		result.put("listDetailContactMechInFacility" , listDetailContactMechInFacility);
		return result;
	}
	
	public static Map<String, Object> loadContactMechDetailByEdit(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String) context.get("contactMechId");
		List<GenericValue> listDetailContactMechInFacility = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)), null, null, null, false);
		result.put("listContactMechDetailByEdit" , listDetailContactMechInFacility);
		return result;
	}
	
	
	public static Map<String, Object> editContactMechPostalAddressInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String toName = (String)context.get("toName");
		String attnName = (String)context.get("attnName");
		String address1 = (String)context.get("address1");
		String address2 = (String)context.get("address2");
		String city = (String)context.get("city");
		String countryGeoId = (String)context.get("countryGeoId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String postalCode = (String)context.get("postalCode");
		
		GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMechId));
		String toNameData = (String)postalAddress.get("toName");
		String attnNameData = (String)postalAddress.get("attnName");
		String address1Data = (String)postalAddress.get("address1");
		String address2Data = (String)postalAddress.get("address2");
		String cityData = (String)postalAddress.get("city");
		String countryGeoIdData = (String)postalAddress.get("countryGeoId");
		String stateProvinceGeoIdData = (String)postalAddress.get("stateProvinceGeoId");
		String postalCodeData = (String)postalAddress.get("postalCode");
		if(numberOrNot(postalCode) == false){
			result.put("value", "postalCodeNotNumber");
		}else{
			if(stateProvinceGeoId != null && address2 != null){
				if(address2.length() > 255){
					result.put("value", "address2MaxLength");
				}else{
					if(stateProvinceGeoIdData != null ){
						if(address2Data != null){
							if(toName.equals(toNameData) && attnName.equals(attnNameData) && address1.equals(address1Data) && address2.equals(address2Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && stateProvinceGeoId.equals(stateProvinceGeoIdData) && postalCode.equals(postalCodeData)){
								result.put("value", "notEdit");
							}else{
								postalAddress.put("toName", toName);
								postalAddress.put("attnName", attnName);
								postalAddress.put("address1", address1);
								postalAddress.put("address2", address2);
								postalAddress.put("city", city);
								postalAddress.put("countryGeoId", countryGeoId);
								postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
								postalAddress.put("postalCode", postalCode);
								delegator.store(postalAddress);
								result.put("value", "success");
							}
						}else{
							postalAddress.put("toName", toName);
							postalAddress.put("attnName", attnName);
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("toName", toName);
						postalAddress.put("attnName", attnName);
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
			}else{
				if(stateProvinceGeoId == null && address2 == null){
					if(stateProvinceGeoIdData == null && address2Data == null){
						if(toName.equals(toNameData) && attnName.equals(attnNameData) && address1.equals(address1Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && postalCode.equals(postalCodeData)){
							result.put("value", "notEdit");
						}else{
							postalAddress.put("toName", toName);
							postalAddress.put("attnName", attnName);
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("toName", toName);
						postalAddress.put("attnName", attnName);
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
				if(stateProvinceGeoId != null && address2 == null){
					if(stateProvinceGeoIdData != null && address2Data == null){
						if(toName.equals(toNameData) && attnName.equals(attnNameData) && address1.equals(address1Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && postalCode.equals(postalCodeData)){
							result.put("value", "notEdit");
						}else{
							postalAddress.put("toName", toName);
							postalAddress.put("attnName", attnName);
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("toName", toName);
						postalAddress.put("attnName", attnName);
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
				if(stateProvinceGeoId == null && address2 != null){
					if(address2.length() > 255){
						result.put("value", "address2MaxLength");
					}else{
						postalAddress.put("toName", toName);
						postalAddress.put("attnName", attnName);
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> editTelecomNumberInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
//		String extendsion = (String)context.get("extendsion");
		
		GenericValue telecomNumber = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId", contactMechId));
		if(telecomNumber != null){
			String countryCodeData = (String)telecomNumber.get("countryCode");
			String areaCodeData = (String)telecomNumber.get("areaCode");
			String contactNumberData = (String)telecomNumber.get("contactNumber");
			if(numberOrNot(countryCode) == false || numberOrNot(countryCode) == false || numberOrNot(contactNumber) == false){
				result.put("value", "notString");
			}else{
				long countryCodeLong = Long.parseLong(countryCode);
				long areaCodeLong = Long.parseLong(areaCode);
				long contactNumberLong = Long.parseLong(contactNumber);
				if(countryCode.equals(countryCodeData) && areaCode.equals(areaCodeData) && contactNumber.equals(contactNumberData)){
					result.put("value", "notEdit");
				}else{
					if(countryCodeLong < 0 || areaCodeLong < 0 || contactNumberLong < 0){
						result.put("value", "notString");
					}else{
						telecomNumber.put("countryCode", countryCode);
						telecomNumber.put("areaCode", areaCode);
						telecomNumber.put("contactNumber", contactNumber);
						delegator.store(telecomNumber);
						result.put("value", "success");
					}
				}
			}
		}
		return result;
	}
	public static Map<String, Object> editWebAddressOrLDAPAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String infoString = (String)context.get("infoString");
		
		GenericValue contactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contactMechId));
		if(contactMech != null){
			String infoStringData = (String)contactMech.get("infoString");
			if(infoString.equals(infoStringData)){
				result.put("value", "notEdit");
			}else{
				contactMech.put("infoString", infoString);
				delegator.store(contactMech);
				result.put("value", "success");
			}
		}
		return result;
	}
	public static Map<String, Object> deleteContactMechInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String facilityId = (String)context.get("facilityId");
		GenericValue contactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contactMechId));
		if(contactMech != null){
			String contactMechTypeId = (String) contactMech.get("contactMechTypeId");
			List<GenericValue>  facilityContactMech = delegator.findList("FacilityContactMech",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechId", contactMechId)), null, null, null, false);
			List<GenericValue>  facilityContactMechPurpose = delegator.findList("FacilityContactMechPurpose",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechId", contactMechId)), null, null, null, false);
			GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMechId));
			if(contactMechTypeId.equals("POSTAL_ADDRESS")){
				if(facilityContactMech != null){
					delegator.removeAll(facilityContactMech);
				}
				if(facilityContactMechPurpose != null){
					delegator.removeAll(facilityContactMechPurpose);
				}
				if(postalAddress != null){
					delegator.removeValue(postalAddress);
				}
				result.put("value", "success");
			}
			if(contactMechTypeId.equals("TELECOM_NUMBER")){
				GenericValue telecomNumber = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId", contactMechId));
				if(facilityContactMech != null){
					delegator.removeAll(facilityContactMech);
				}
				if(facilityContactMechPurpose != null){
					delegator.removeAll(facilityContactMechPurpose);
				}
				if(telecomNumber != null){
					delegator.removeValue(telecomNumber);
				}
				result.put("value", "success");
			}
			if(contactMechTypeId.equals("EMAIL_ADDRESS")){
				if(facilityContactMech != null){
					delegator.removeAll(facilityContactMech);
				}
				if(facilityContactMechPurpose != null){
					delegator.removeAll(facilityContactMechPurpose);
				}
				result.put("value", "success");
			}
			delegator.removeValue(contactMech);
		}
		return result;
	}
	
	public static Map<String, Object> loadDataJqxTreeGirdFacilityGroup(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listFacilityGroup = delegator.findList("ListFacilityGroupAndFacilityGroupRollup", null , null, null, null, false);
		result.put("listFacilityGroup", listFacilityGroup);
		return result;
		
	}
	
	public static Map<String, Object> createFacilityGroupAndRollupByHungNc(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityGroupId = null;
		String facilityGroupTypeId = (String)context.get("facilityGroupTypeId");
		String primaryParentGroupId = (String)context.get("primaryParentGroupId");
		String facilityGroupName = (String)context.get("facilityGroupName");
		String description = (String)context.get("description");
		String parentFacilityGroupId = (String)context.get("parentFacilityGroupId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		String sequenceNum = (String)context.get("sequenceNum");
		
		GenericValue facilityGroup = delegator.makeValue("FacilityGroup");
		GenericValue facilityGroupRollup = delegator.makeValue("FacilityGroupRollup");
		facilityGroupId = delegator.getNextSeqId("FacilityGroup");
		
		if(parentFacilityGroupId == null){
			facilityGroup.put("facilityGroupId", facilityGroupId);
			facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
			facilityGroup.put("facilityGroupName", facilityGroupName);
			facilityGroup.put("description", description);
			delegator.create(facilityGroup);
			result.put("value", "success");
		}else{
			if(fromDate != null){
				if(sequenceNum != null && thruDate != null){
					if(numberOrNot(sequenceNum) == false){
						result.put("value", "sequenceNumNotNumber");
						return result;
					}else{
						long sequenceNumInt = Long.parseLong(sequenceNum);
						long fromDateLong = Long.parseLong(fromDate);
						long thruDateLong = Long.parseLong(thruDate);
						Timestamp fromDateStamp = new Timestamp(fromDateLong);
						Timestamp thruDateStamp = new Timestamp(thruDateLong);
						if(fromDateLong > thruDateLong){
							result.put("value", "thruDateLongValid");
							return result;
						}else{
							if(primaryParentGroupId != null){
								Boolean checkPrimaryParentGroupId = Boolean.valueOf(primaryParentGroupId);
								if(checkPrimaryParentGroupId == true){
									facilityGroup.put("facilityGroupId", facilityGroupId);
									facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
									facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
									facilityGroup.put("facilityGroupName", facilityGroupName);
									facilityGroup.put("description", description);
									delegator.create(facilityGroup);
									result.put("value", "success");
								}else{
									facilityGroup.put("facilityGroupId", facilityGroupId);
									facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
									facilityGroup.put("facilityGroupName", facilityGroupName);
									facilityGroup.put("description", description);
									delegator.create(facilityGroup);
									result.put("value", "success");
								}
							}else{
								facilityGroup.put("facilityGroupId", facilityGroupId);
								facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
								facilityGroup.put("facilityGroupName", facilityGroupName);
								facilityGroup.put("description", description);
								delegator.create(facilityGroup);
								result.put("value", "success");
							}
							facilityGroupRollup.put("facilityGroupId", facilityGroupId);
							facilityGroupRollup.put("parentFacilityGroupId", parentFacilityGroupId);
							facilityGroupRollup.put("fromDate", fromDateStamp);
							facilityGroupRollup.put("thruDate", thruDateStamp);
							facilityGroupRollup.put("sequenceNum", sequenceNumInt);
							delegator.create(facilityGroupRollup);
						}
					}
				}
				if(sequenceNum != null && thruDate == null ){
					if(numberOrNot(sequenceNum) == false){
						result.put("value", "sequenceNumNotNumber");
						return result;
					}else{
						if(primaryParentGroupId != null){
							Boolean checkPrimaryParentGroupId = Boolean.valueOf(primaryParentGroupId);
							if(checkPrimaryParentGroupId == true){
								facilityGroup.put("facilityGroupId", facilityGroupId);
								facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
								facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
								facilityGroup.put("facilityGroupName", facilityGroupName);
								facilityGroup.put("description", description);
								delegator.create(facilityGroup);
								result.put("value", "success");
							}else{
								facilityGroup.put("facilityGroupId", facilityGroupId);
								facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
								facilityGroup.put("facilityGroupName", facilityGroupName);
								facilityGroup.put("description", description);
								delegator.create(facilityGroup);
								result.put("value", "success");
							}
						}else{
							facilityGroup.put("facilityGroupId", facilityGroupId);
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.create(facilityGroup);
							result.put("value", "success");
						}
						long sequenceNumInt = Long.parseLong(sequenceNum);
						long fromDateLong = Long.parseLong(fromDate);
						Timestamp fromDateStamp = new Timestamp(fromDateLong);
						
						facilityGroupRollup.put("facilityGroupId", facilityGroupId);
						facilityGroupRollup.put("parentFacilityGroupId", parentFacilityGroupId);
						facilityGroupRollup.put("fromDate", fromDateStamp);
						facilityGroupRollup.put("sequenceNum", sequenceNumInt);
						delegator.create(facilityGroupRollup);
					}
				}
				if(sequenceNum == null && thruDate != null ){
					long fromDateLong = Long.parseLong(fromDate);
					Timestamp fromDateStamp = new Timestamp(fromDateLong);
					long thruDateLong = Long.parseLong(thruDate);
					Timestamp thruDateStamp = new Timestamp(thruDateLong);
					if(fromDateLong > thruDateLong){
						result.put("value", "thruDateLongValid");
						return result;
					}else{
						if(primaryParentGroupId != null){
							Boolean checkPrimaryParentGroupId = Boolean.valueOf(primaryParentGroupId);
							if(checkPrimaryParentGroupId == true){
								facilityGroup.put("facilityGroupId", facilityGroupId);
								facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
								facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
								facilityGroup.put("facilityGroupName", facilityGroupName);
								facilityGroup.put("description", description);
								delegator.create(facilityGroup);
								result.put("value", "success");
							}else{
								facilityGroup.put("facilityGroupId", facilityGroupId);
								facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
								facilityGroup.put("facilityGroupName", facilityGroupName);
								facilityGroup.put("description", description);
								delegator.create(facilityGroup);
								result.put("value", "success");
							}
						}else{
							facilityGroup.put("facilityGroupId", facilityGroupId);
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.create(facilityGroup);
							result.put("value", "success");
						}
						facilityGroupRollup.put("facilityGroupId", facilityGroupId);
						facilityGroupRollup.put("parentFacilityGroupId", parentFacilityGroupId);
						facilityGroupRollup.put("fromDate", fromDateStamp);
						facilityGroupRollup.put("thruDate", thruDateStamp);
						delegator.create(facilityGroupRollup);
					}
				}
				if(sequenceNum == null && thruDate == null){
					if(primaryParentGroupId != null){
						Boolean checkPrimaryParentGroupId = Boolean.valueOf(primaryParentGroupId);
						if(checkPrimaryParentGroupId == true){
							facilityGroup.put("facilityGroupId", facilityGroupId);
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.create(facilityGroup);
							result.put("value", "success");
						}else{
							facilityGroup.put("facilityGroupId", facilityGroupId);
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.create(facilityGroup);
							result.put("value", "success");
						}
					}else{
						facilityGroup.put("facilityGroupId", facilityGroupId);
						facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
						facilityGroup.put("facilityGroupName", facilityGroupName);
						facilityGroup.put("description", description);
						delegator.create(facilityGroup);
						result.put("value", "success");
					}
					long fromDateLong = Long.parseLong(fromDate);
					Timestamp fromDateStamp = new Timestamp(fromDateLong);
					facilityGroupRollup.put("facilityGroupId", facilityGroupId);
					facilityGroupRollup.put("parentFacilityGroupId", parentFacilityGroupId);
					facilityGroupRollup.put("fromDate", fromDateStamp);
					delegator.create(facilityGroupRollup);
					result.put("value", "success");
				}
			}else{
				if(primaryParentGroupId != null){
					Boolean checkPrimaryParentGroupId = Boolean.valueOf(primaryParentGroupId);
					if(checkPrimaryParentGroupId == true){
						facilityGroup.put("facilityGroupId", facilityGroupId);
						facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
						facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
						facilityGroup.put("facilityGroupName", facilityGroupName);
						facilityGroup.put("description", description);
						delegator.create(facilityGroup);
						result.put("value", "success");
					}else{
						facilityGroup.put("facilityGroupId", facilityGroupId);
						facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
						facilityGroup.put("facilityGroupName", facilityGroupName);
						facilityGroup.put("description", description);
						delegator.create(facilityGroup);
						result.put("value", "success");
					}
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> createFacilityGroupByHungNc(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityGroupId = null;
		String facilityGroupTypeId = (String)context.get("facilityGroupTypeId");
		String primaryParentGroupId = (String)context.get("primaryParentGroupId");
		String facilityGroupName = (String)context.get("facilityGroupName");
		String description = (String)context.get("description");
		String parentFacilityGroupId = (String)context.get("parentFacilityGroupId");
		
		GenericValue facilityGroup = delegator.makeValue("FacilityGroup");
		facilityGroupId = delegator.getNextSeqId("FacilityGroup");
		if(parentFacilityGroupId == null){
			facilityGroup.put("facilityGroupId", facilityGroupId);
			facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
			facilityGroup.put("facilityGroupName", facilityGroupName);
			facilityGroup.put("description", description);
			delegator.create(facilityGroup);
			
			result.put("value", "success");
		}else{
			if(primaryParentGroupId.equals("true")){
				facilityGroup.put("facilityGroupId", facilityGroupId);
				facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
				facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
				facilityGroup.put("facilityGroupName", facilityGroupName);
				facilityGroup.put("description", description);
				delegator.create(facilityGroup);
			}
			if(primaryParentGroupId.equals("false")){
				facilityGroup.put("facilityGroupId", facilityGroupId);
				facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
				facilityGroup.put("facilityGroupName", facilityGroupName);
				facilityGroup.put("description", description);
				delegator.create(facilityGroup);
			}
			result.put("value", "success");
		}
		return result;
	}
	
	
	
	public static Map<String, Object> editFacilityGroupByHungNc(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityGroupId = (String)context.get("facilityGroupId");
		String facilityGroupTypeId = (String)context.get("facilityGroupTypeId");
		String primaryParentGroupId = (String)context.get("primaryParentGroupId");
		String facilityGroupName = (String)context.get("facilityGroupName");
		String description = (String)context.get("description");
		String parentFacilityGroupId = (String)context.get("parentFacilityGroupId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		String sequenceNum = (String)context.get("sequenceNum");
		String valueEditFacilityGroup = editFacilityGroup(dpx, facilityGroupId, facilityGroupTypeId, parentFacilityGroupId, primaryParentGroupId, facilityGroupName, description);
		if(parentFacilityGroupId != null && fromDate != null){
			String valueEditFacilityGroupRollup = editFacilityGroupRollup(dpx, facilityGroupId, parentFacilityGroupId, fromDate, thruDate, sequenceNum);
			if(valueEditFacilityGroup == "success" && valueEditFacilityGroupRollup == "success"){
				result.put("value", "success");
			}
			if(valueEditFacilityGroup == "success" && valueEditFacilityGroupRollup == "notEdit"){
				result.put("value", "success");
			}
			if(valueEditFacilityGroup == "notEdit" && valueEditFacilityGroupRollup == "success"){
				result.put("value", "success");
			}
			if(valueEditFacilityGroup == "notEdit" && valueEditFacilityGroupRollup == "notEdit"){
				result.put("value", "notEdit");
			}
			if(valueEditFacilityGroupRollup == "sequenceNumNotNumber"){
				result.put("value", "sequenceNumNotNumber");
			}
			if(valueEditFacilityGroupRollup == "error" || valueEditFacilityGroup == "error"){
				result.put("value", "error");
			}
		}else{
			if(valueEditFacilityGroup == "notEdit"){
				result.put("value", "notEdit");
			}
			if(valueEditFacilityGroup == "success"){
				result.put("value", "success");
			}
			if(valueEditFacilityGroup == "error"){
				result.put("value", "error");
			}
		}
		return result;
	}
	
	
	public static String editFacilityGroup(DispatchContext dpx, String facilityGroupId, String facilityGroupTypeId, String parentFacilityGroupId, String primaryParentGroupId, String facilityGroupName, String description) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		GenericValue facilityGroup = delegator.findOne("FacilityGroup", false, UtilMisc.toMap("facilityGroupId", facilityGroupId));
		String value = "";
		if(facilityGroup != null){
			String facilityGroupTypeIdByData = (String) facilityGroup.get("facilityGroupTypeId");
			String primaryParentGroupIdByData = (String) facilityGroup.get("primaryParentGroupId");
			String facilityGroupNameByData = (String) facilityGroup.get("facilityGroupName");
			String descriptionByData = (String) facilityGroup.get("description");
			if(parentFacilityGroupId != null){
				if(primaryParentGroupIdByData != null){
					if(primaryParentGroupId != null){
						if(primaryParentGroupId.equals(primaryParentGroupIdByData) && facilityGroupTypeId.equals(facilityGroupTypeIdByData) && facilityGroupName.equals(facilityGroupNameByData) && description.equals(descriptionByData)){
							value = "notEdit";
						}else{
							facilityGroup.put("primaryParentGroupId", primaryParentGroupId);
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.store(facilityGroup);
							value = "success";
						}
					}else{
						facilityGroup.put("primaryParentGroupId", primaryParentGroupId);
						facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
						facilityGroup.put("facilityGroupName", facilityGroupName);
						facilityGroup.put("description", description);
						delegator.store(facilityGroup);
						value = "success";
					}
				}else{
					if(primaryParentGroupId.equals("true")){
						facilityGroup.put("primaryParentGroupId", parentFacilityGroupId);
						facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
						facilityGroup.put("facilityGroupName", facilityGroupName);
						facilityGroup.put("description", description);
						delegator.store(facilityGroup);
						value = "success";
					}
					if(primaryParentGroupId.equals("false")){
						if(facilityGroupTypeId.equals(facilityGroupTypeIdByData) && facilityGroupName.equals(facilityGroupNameByData) && description.equals(descriptionByData)){
							value = "notEdit";
						}else{
							facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
							facilityGroup.put("facilityGroupName", facilityGroupName);
							facilityGroup.put("description", description);
							delegator.store(facilityGroup);
							value = "success";
						}
					}
				}
			}else{
				if(facilityGroupTypeId.equals(facilityGroupTypeIdByData) && facilityGroupName.equals(facilityGroupNameByData) && description.equals(descriptionByData)){
					value = "notEdit";
				}else{
					facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
					facilityGroup.put("facilityGroupName", facilityGroupName);
					facilityGroup.put("description", description);
					delegator.store(facilityGroup);
					value = "success";
				}
			}
		}else{
			value = "error";
		}
		return value;
	}
	
	public static String editFacilityGroupRollup(DispatchContext dpx, String facilityGroupId, String parentFacilityGroupId, String fromDate, String thruDate, String sequenceNum) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		long fromDateLong = Long.parseLong(fromDate);
		Timestamp fromDateTime = new Timestamp(fromDateLong);
		GenericValue facilityGroupRollup = delegator.findOne("FacilityGroupRollup", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "parentFacilityGroupId", parentFacilityGroupId, "fromDate", fromDateTime));
		String value = "";
		if(facilityGroupRollup != null){
			Timestamp thruDateByData = facilityGroupRollup.getTimestamp("thruDate");
			Long sequenceNumByData = facilityGroupRollup.getLong("sequenceNum");
			if(thruDate != null && sequenceNum != null){
				long thruDateLong = Long.parseLong(thruDate);
				Timestamp thruDateTime = new Timestamp(thruDateLong);
				if(numberOrNot(sequenceNum) == true){
					Long sequenceNumInput = Long.parseLong(sequenceNum);
					if(sequenceNumInput < 0){
						value = "sequenceNumNotNumber";
					}else{
						if(thruDateByData != null && sequenceNumByData != null){
							if(thruDateTime.equals(thruDateByData) && sequenceNum.equals(sequenceNumByData)){
								value = "notEdit";
							}else{
								facilityGroupRollup.put("thruDate", thruDateTime);
								facilityGroupRollup.put("sequenceNum", sequenceNumInput);
								delegator.store(facilityGroupRollup);
								value = "success";
							}
						}else{
							facilityGroupRollup.put("thruDate", thruDateTime);
							facilityGroupRollup.put("sequenceNum", sequenceNumInput);
							delegator.store(facilityGroupRollup);
							value = "success";
						}
					}
				}else{
					value = "sequenceNumNotNumber";
				}
			}else{
				if(thruDate != null && sequenceNum == null){
					long thruDateLong = Long.parseLong(thruDate);
					Timestamp thruDateTime = new Timestamp(thruDateLong);
					if(thruDateByData != null && sequenceNumByData == null){
						if(thruDateTime.equals(thruDateByData)){
							value = "notEdit";
						}else{
							facilityGroupRollup.put("thruDate", thruDateTime);
							delegator.store(facilityGroupRollup);
							value = "success";
						}
					}else{
						facilityGroupRollup.put("thruDate", thruDateTime);
						facilityGroupRollup.put("sequenceNum", sequenceNum);
						delegator.store(facilityGroupRollup);
						value = "success";
					}
				}
				if(thruDate == null && sequenceNum != null){
					if(numberOrNot(sequenceNum) == true){
						Long sequenceNumInput = Long.parseLong(sequenceNum);
						if(sequenceNumInput < 0){
							value = "sequenceNumNotNumber";
						}else{
							if(thruDateByData == null && sequenceNumByData != null){
								if(sequenceNum.equals(sequenceNumByData)){
									value = "notEdit";
								}else{
									facilityGroupRollup.put("sequenceNum", sequenceNum);
									delegator.store(facilityGroupRollup);
									value = "success";
								}
							}else{
								facilityGroupRollup.put("thruDate", thruDate);
								facilityGroupRollup.put("sequenceNum", sequenceNum);
								delegator.store(facilityGroupRollup);
								value = "success";
							}
						}
					}else{
						value = "sequenceNumNotNumber";
					}
				}
				if(thruDate == null && sequenceNum == null){
					if(thruDateByData == null && sequenceNumByData == null){
						value = "notEdit";
					}else{
						facilityGroupRollup.put("thruDate", thruDate);
						facilityGroupRollup.put("sequenceNum", sequenceNum);
						delegator.store(facilityGroupRollup);
						value = "success";
					}
				}
			}
		}else{
			value = "error";
		}
		return value;
	}
	
	public static Map<String, Object> loadDataJqxTreeGirdFacilityGroupRollup(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityGroupId = (String) context.get("facilityGroupId");
		List<GenericValue> listFacilityGroupRollup = delegator.findList("FacilityGroupRollup", EntityCondition.makeCondition(UtilMisc.toMap("facilityGroupId", facilityGroupId)) , null, null, null, false);
		result.put("listFacilityGroupRollup", listFacilityGroupRollup);
		return result;
		
	}
	
	public static Map<String, Object> loadParentFacilityGroupId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listFacilityGroup = delegator.findList("FacilityGroup", null, null, null, null, false);
		result.put("listParentFacilityGroupId", listFacilityGroup);
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetFacilityGroupRole(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityGroupId = parameters.get("facilityGroupId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, facilityGroupId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("FacilityGroupRole", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetFacilityGroupMember(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityGroupId = parameters.get("facilityGroupId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, facilityGroupId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("FacilityGroupMember", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	
	public static Map<String, Object> createFacilityGroupRole(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		String facilityGroupId = (String)context.get("facilityGroupId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		if(fromDate == null){
			result.put("value", "fromDateNull");
		}else{
			GenericValue facilityGroupRole = delegator.makeValue("FacilityGroupRole");
			if(facilityGroupId != null && partyId != null && roleTypeId != null){
				GenericValue facilityGroupRoleCheck = delegator.findOne("FacilityGroupRole", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "partyId", partyId, "roleTypeId", roleTypeId));
				if(facilityGroupRoleCheck != null){
					result.put("value", "exits");
				}else{
					Long fromDateLong = Long.parseLong(fromDate);
					Timestamp fromDateInput = new Timestamp(fromDateLong);
					facilityGroupRole.put("facilityGroupId", facilityGroupId);
					facilityGroupRole.put("partyId", partyId);
					facilityGroupRole.put("roleTypeId", roleTypeId);
					facilityGroupRole.put("fromDate", fromDateInput);
					if(thruDate != null){
						Long thruDateLong = Long.parseLong(thruDate);
						Timestamp thruDateInput = new Timestamp(thruDateLong);
						facilityGroupRole.put("thruDate", thruDateInput);
					}
					delegator.create(facilityGroupRole);
					result.put("value", "success");
				}
			}else{
				result.put("value", "error");
			}
		}
		return result;
	}
	
	
	public static Map<String, Object> createFacilityGroupMember(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String sequenceNum = (String)context.get("sequenceNum");
		String facilityGroupId = (String)context.get("facilityGroupId");
		String facilityId = (String)context.get("facilityId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		long fromDateLong = Long.parseLong(fromDate);
		Timestamp fromDateTime = new Timestamp(fromDateLong);
		GenericValue facilityGroupMember = delegator.makeValue("FacilityGroupMember");
		GenericValue facilityGroupMemberCheck = delegator.findOne("FacilityGroupMember", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "facilityId", facilityId, "fromDate", fromDateTime));
		if(facilityGroupMemberCheck != null){
			result.put("value", "exits");
		}else{
			if(thruDate !=  null){
				if(sequenceNum != null){
					if(numberOrNot(sequenceNum) == false){
						result.put("value", "sequenceNumNotNumber");
					}else{
						int sequenceNumInt = Integer.parseInt(sequenceNum);
						if(sequenceNumInt < 0){
							result.put("value", "sequenceNumNotNumber");
						}else{
							if(sequenceNum.length() > 20){
								result.put("value", "sequenceNumNotMaxLength");
							}else{
								long sequenceNumLong = Long.parseLong(sequenceNum);
								long thruDateLong = Long.parseLong(thruDate);
								Timestamp thruDateTime = new Timestamp(thruDateLong);
								facilityGroupMember.put("facilityId", facilityId);
								facilityGroupMember.put("facilityGroupId", facilityGroupId);
								facilityGroupMember.put("fromDate", fromDateTime);
								facilityGroupMember.put("thruDate", thruDateTime);
								facilityGroupMember.put("sequenceNum", sequenceNumLong);
								delegator.create(facilityGroupMember);
								result.put("value", "success");
							}
						}
					}
				}else{
					long thruDateLong = Long.parseLong(thruDate);
					Timestamp thruDateTime = new Timestamp(thruDateLong);
					facilityGroupMember.put("facilityId", facilityId);
					facilityGroupMember.put("facilityGroupId", facilityGroupId);
					facilityGroupMember.put("fromDate", fromDateTime);
					facilityGroupMember.put("thruDate", thruDateTime);
					delegator.create(facilityGroupMember);
					result.put("value", "success");
				}
			}else{
				if(sequenceNum != null){
					if(numberOrNot(sequenceNum) == false){
						result.put("value", "sequenceNumNotNumber");
					}else{
						int sequenceNumInt = Integer.parseInt(sequenceNum);
						if(sequenceNumInt < 0){
							result.put("value", "sequenceNumNotNumber");
						}else{
							if(sequenceNum.length() > 20){
								result.put("value", "sequenceNumNotMaxLength");
							}else{
								long sequenceNumLong = Long.parseLong(sequenceNum);
								facilityGroupMember.put("facilityId", facilityId);
								facilityGroupMember.put("facilityGroupId", facilityGroupId);
								facilityGroupMember.put("fromDate", fromDateTime);
								facilityGroupMember.put("sequenceNum", sequenceNumLong);
								delegator.create(facilityGroupMember);
								result.put("value", "success");
							}
						}
					}
				}else{
					facilityGroupMember.put("facilityId", facilityId);
					facilityGroupMember.put("facilityGroupId", facilityGroupId);
					facilityGroupMember.put("fromDate", fromDateTime);
					delegator.create(facilityGroupMember);
					result.put("value", "success");
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> editFacilityGroupRole(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		String facilityGroupId = (String)context.get("facilityGroupId");
		String thruDate = (String)context.get("thruDate");
		GenericValue facilityGroupRole = delegator.findOne("FacilityGroupRole", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "partyId", partyId, "roleTypeId", roleTypeId));;
		Timestamp thruDateByData = facilityGroupRole.getTimestamp("thruDate");
		if(thruDate != null){
			Long thruDateLong = Long.parseLong(thruDate);
			Timestamp thruDateInput = new Timestamp(thruDateLong);
			if(thruDateByData != null){
				Long thruDateLongByData = thruDateByData.getTime();
				if(thruDateLong.equals(thruDateLongByData)){
					result.put("value", "notEdit");
				}else{
					facilityGroupRole.put("thruDate", thruDateInput);
					delegator.store(facilityGroupRole);
					result.put("value", "success");
				}
			}else{
				facilityGroupRole.put("thruDate", thruDateInput);
				delegator.store(facilityGroupRole);
				result.put("value", "success");
			}
		}else{
			if(thruDateByData == null){
				result.put("value", "notEdit");
			}else{
				facilityGroupRole.put("thruDate", thruDate);
				delegator.store(facilityGroupRole);
				result.put("value", "success");
			}
		}
		return result;
	}
	
	public static Map<String, Object> deleteFacilityGroupRoleByPartyId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		String facilityGroupId = (String)context.get("facilityGroupId");
		GenericValue facilityGroupRole = delegator.findOne("FacilityGroupRole", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "partyId", partyId, "roleTypeId", roleTypeId));;
		delegator.removeValue(facilityGroupRole);
		result.put("value", "success");
		return result;
	}
	
	public static Map<String, Object> bindingEditLocationFacilityType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		String locationFacilityTypeId = (String)context.get("locationFacilityTypeId");
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> list = new ArrayList<GenericValue>();
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", false, UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId));;
		list.add(locationFacilityType);
		result.put("listLocationFacilityType", list);
		return result;
	}
	
	public static int checkParentLocationFacilityType(DispatchContext dpx, String parentLocationFacilityTypeIdInput, String locationFacilityTypeId) throws GenericEntityException {
		int index = 0;
		Delegator delegator = dpx.getDelegator();
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", false, UtilMisc.toMap("locationFacilityTypeId", parentLocationFacilityTypeIdInput));
		String parentLocationFacilityTypeId = (String) locationFacilityType.get("parentLocationFacilityTypeId");
		if(parentLocationFacilityTypeId != null){
			if(!locationFacilityTypeId.equals(parentLocationFacilityTypeId)){
				return checkParentLocationFacilityType(dpx, parentLocationFacilityTypeId, locationFacilityTypeId);
			}else{
				index = 2;
			}
		}else{
			index = 0;
		}
		return index;
	}
	
	public static Map<String, Object> editFacilityGroupMember(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityGroupId = (String)context.get("facilityGroupId");
		String facilityId = (String)context.get("facilityId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		String sequenceNum = (String)context.get("sequenceNum");
		long fromDateLong = Long.parseLong(fromDate);
		Timestamp fromDateTime = new Timestamp(fromDateLong);
		GenericValue facilityGroupMember = delegator.findOne("FacilityGroupMember", false, UtilMisc.toMap("facilityGroupId", facilityGroupId, "facilityId", facilityId, "fromDate", fromDateTime));;
		Timestamp thruDateByData = facilityGroupMember.getTimestamp("thruDate");
		Long sequenceNumByData = (Long) facilityGroupMember.get("sequenceNum");
		if(thruDate != null && sequenceNum != null){
			long thruDateLong = Long.parseLong(thruDate);
			Timestamp thruDateTime = new Timestamp(thruDateLong);
			if(numberOrNot(sequenceNum) == false){
				result.put("value", "sequenceNumNotNumber");
			}else{
				if(sequenceNum.length() > 20){
					result.put("value", "sequenceNumMaxLength");
				}else{
					long sequenceNumInt = Long.parseLong(sequenceNum);
					if(sequenceNumInt < 0){
						result.put("value", "sequenceNumNotNumber");
					}else{
						if(thruDateByData != null && sequenceNumByData != null){
							long thruDateByDataLong = thruDateByData.getTime();
							if(thruDateLong == thruDateByDataLong && sequenceNumByData == sequenceNumInt){
								result.put("value", "notEdit");
							}else{
								facilityGroupMember.put("thruDate", thruDateTime);
								facilityGroupMember.put("sequenceNum", sequenceNumInt);
								delegator.store(facilityGroupMember);
								result.put("value", "success");
							}
						}
						else{
							facilityGroupMember.put("thruDate", thruDateTime);
							facilityGroupMember.put("sequenceNum", sequenceNumInt);
							delegator.store(facilityGroupMember);
							result.put("value", "success");
						}
					}
				}
			}
		}
		if(thruDate != null && sequenceNum == null){
			long thruDateLong = Long.parseLong(thruDate);
			Timestamp thruDateTime = new Timestamp(thruDateLong);
			if(thruDateByData != null && sequenceNumByData == null){
				long thruDateByDataLong = thruDateByData.getTime();
				if(thruDateLong == thruDateByDataLong){
					result.put("value", "notEdit");
				}else{
					facilityGroupMember.put("thruDate", thruDateTime);
					facilityGroupMember.put("sequenceNum", sequenceNum);
					delegator.store(facilityGroupMember);
					result.put("value", "success");
				}
			}else{
				facilityGroupMember.put("thruDate", thruDateTime);
				facilityGroupMember.put("sequenceNum", sequenceNum);
				delegator.store(facilityGroupMember);
				result.put("value", "success");
			}
		}
		if(thruDate == null && sequenceNum != null){
			if(!numberOrNot(sequenceNum)){
				result.put("value", "sequenceNumNotNumber");
			}else{
				long sequenceNumInt = Long.parseLong(sequenceNum);
				if(sequenceNumInt < 0){
					result.put("value", "sequenceNumNotNumber");
				}else{
					if(thruDateByData == null && sequenceNumByData != null){
						if(sequenceNumByData == sequenceNumInt){
							result.put("value", "notEdit");
						}else{
							if(sequenceNum.length() > 20){
								result.put("value", "sequenceNumMaxLength");
							}else{
								facilityGroupMember.put("sequenceNum", sequenceNumInt);
								delegator.store(facilityGroupMember);
								result.put("value", "success");
							}
						}
					}else{
						facilityGroupMember.put("thruDate", thruDate);
						facilityGroupMember.put("sequenceNum", sequenceNumInt);
						delegator.store(facilityGroupMember);
						result.put("value", "success");
					}
				}
			}
		}
		if(thruDate == null && sequenceNum == null){
			if(thruDateByData == null && sequenceNumByData == null)
			{
				result.put("value", "notEdit");
			}else{
				facilityGroupMember.put("thruDate", thruDate);
				facilityGroupMember.put("sequenceNum", sequenceNum);
				delegator.store(facilityGroupMember);
				result.put("value", "success");
			}
		}
		return result;
	}
	
	public static Map<String, Object> loadDataToParty(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listPartyRelationShipWithPerson = new ArrayList<GenericValue>();
		listPartyRelationShipWithPerson = delegator.findList("PartyRelationShipWithPerson", null, null, null, null, false);
		result.put("listParty", listPartyRelationShipWithPerson);
		return result;
	}
	
	public static Map<String, Object> loadRoleTypeIdByPartyId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		List<GenericValue> listRoleTyleIdByPartyId = new ArrayList<GenericValue>();
		listRoleTyleIdByPartyId = delegator.findList("PartyRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
		result.put("listRoleTyleIdByPartyId", listRoleTyleIdByPartyId);
		return result;
	}
	
	public static Map<String, Object> loadDataConfigPacking(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listConfigPacking = new ArrayList<GenericValue>();
		listConfigPacking = delegator.findList("ConfigPacking", null, null, null, null, false);
		result.put("listConfigPacking", listConfigPacking);
		return result;
	}
	
	public static Map<String, Object> loadListParty(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listParty = delegator.findList("PartyNameView", null, null, null, null, false);
		result.put("listParty", listParty);   
		return result;
	}
	
	public static Map<String, Object> loadParentIdByFacilityId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listFacilityId = delegator.findList("Facility", null, null, null, null, false);
		result.put("listFacilityId", listFacilityId);   
		return result;
	}
	
	public static Map<String, Object> loadFacilityDetailByFacilityId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String) context.get("facilityId");
		List<GenericValue> listFacilityDetail = delegator.findList("FacilityPartyFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		result.put("listFacilityDetail", listFacilityDetail);   
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLableItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = parameters.get("facilityId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    	List<GenericValue> listLabelTotal = delegator.findList("LabelTotal", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listLabelItem = delegator.findList("LabelItem", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue lableTotal : listLabelTotal) {
    		String labelIdTotal = lableTotal.getString("labelId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(lableTotal);
			for (GenericValue lableItem : listLabelItem) {
				String labelId = (String) lableItem.get("labelId");
				if (labelIdTotal.equals(labelId)) {
					listRowDetails.add(lableItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("labelId", rowDetail.getString("labelId"));
				mapTmp.put("labelItemId", rowDetail.getString("labelItemId"));
				mapTmp.put("facilityId", rowDetail.getString("facilityId"));
				mapTmp.put("ownerPartyId", rowDetail.getString("ownerPartyId"));
				mapTmp.put("expireDate", rowDetail.getTimestamp("expireDate"));
				mapTmp.put("datetimeReceived", rowDetail.getTimestamp("datetimeReceived"));
				mapTmp.put("quantityOnHandTotal", rowDetail.getBigDecimal("quantityOnHandTotal"));
				mapTmp.put("availableToPromiseTotal", rowDetail.getBigDecimal("availableToPromiseTotal"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseLable(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LABEL_ITEM_CREATED"));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("Requirement", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> loadListFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listFacility = delegator.findList("Facility", null, null, null, null, false);
		result.put("listFacility", listFacility);   
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductsLabelByProductTypeId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("Product", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> loadUomIdByLabelItemProduct(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listUomIdByLabelItemProduct = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "PRODUCT_LABEL_ITEM"), null, null, null, false);
		result.put("listUomIdByLabelItemProduct", listUomIdByLabelItemProduct);   
		return result;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> createRequirementPurchaseLabelItemForFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Security sec = ctx.getSecurity();
		String facilityId = (String)context.get("facilityId");
		String requirementTypeId = (String)context.get("requirementTypeId");
		String description = (String)context.get("description");
		Timestamp requirementByDate = (Timestamp)context.get("requirementByDate");
		Timestamp requirementStartDate = (Timestamp)context.get("requirementStartDate");
		List<Map<String, String>> listProducts = (List<Map<String, String>>)context.get("listProducts");
		String statusId = "LABEL_ITEM_CREATED";
		GenericValue requirement = delegator.makeValue("Requirement");
		String requirementId = delegator.getNextSeqId("Requirement");
		Date date = new Date();
		long dateLong = date.getTime();
		Timestamp createDate = new Timestamp(dateLong);
		requirement.put("requirementId", requirementId);
		requirement.put("requirementTypeId", requirementTypeId);
		requirement.put("facilityId", facilityId);
		requirement.put("statusId", statusId);
		requirement.put("description", description);
		requirement.put("createdByUserLogin", userLogin.get("partyId"));
		requirement.put("createdDate", createDate);
		requirement.put("requiredByDate", requirementByDate);
		requirement.put("requirementStartDate", requirementStartDate);
		try {
			delegator.create(requirement);
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		int nextSeqId = 1; 
		GenericValue requirementItem = delegator.makeValue("RequirementItem");
    	for(Map<String, String> item: listProducts){
    		String productId = item.get("productId");
    		String quantity = item.get("quantity");
    		String quantityUomIdToTransfer = item.get("quantityUomIdToTransfer");
    		BigDecimal quantityBig = new BigDecimal(quantity);
    		requirementItem.put("requirementId", requirementId);
    		requirementItem.put("reqItemSeqId", UtilFormatOut.formatPaddedNumber(nextSeqId++, 5));
    		requirementItem.put("productId", productId);
    		requirementItem.put("quantity", quantityBig);
    		requirementItem.put("quantityUomId", quantityUomIdToTransfer);
    		requirementItem.put("createDate", createDate);
    		requirementItem.put("statusId", statusId);
    		try {
    			delegator.create(requirementItem);
    		} catch (GenericEntityException e) {
    		    return ServiceUtil.returnError(e.getStackTrace().toString());
    		}
    	}
		return result;
	}
	
	public static Map<String, Object> sendPurchaseRequirement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementId = (String)context.get("requirementId");
		String roleTypeId = (String)context.get("roleTypeId");
		String sendMessage = (String)context.get("sendMessage");
		String action = (String)context.get("action");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
			List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (requirement != null){
				requirement.put("statusId", "LABEL_ITEM_PROPOSAL");
				delegator.createOrStore(requirement);
			}
			if(!listRequirementItem.isEmpty()){
				for (GenericValue requirementItem : listRequirementItem) {
					requirementItem.put("statusId", "LABEL_ITEM_PROPOSAL");
					delegator.createOrStore(requirementItem);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		try {
			List<String> listQaAdmin = new ArrayList<String>();
			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
			if (!listPartyGroups.isEmpty()){
				for (String group : listPartyGroups){
					try {
						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
						listManagers = EntityUtil.filterByDate(listManagers);
						if (!listManagers.isEmpty()){
							for (GenericValue manager : listManagers){
								listQaAdmin.add(manager.getString("partyIdFrom"));
							}
						}
					} catch (GenericEntityException e) {
						ServiceUtil.returnError("get Party relationship error!");
					}
				}
			}
			if(!listQaAdmin.isEmpty()){
				for (String managerParty : listQaAdmin){
					String targetLink = "statusId=LABEL_ITEM_PROPOSAL";
					String sendToPartyId = managerParty;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", action);
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
					mapContext.put("userLogin", userLogin);
					dispatcher.runSync("createNotification", mapContext);
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("requirementId", requirementId);
		return result;
	}
	
	public static Map<String, Object> loadPurchaseRequirementAndRequirementItemByRequirementId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String requirementId = (String) context.get("requirementId");
		GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
		List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
		result.put("listRequirementItem", listRequirementItem);   
		result.put("requirement", requirement);   
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInventoryItemByLabelItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "RAW_MATERIAL"));
    	List<GenericValue> listInventoryItem = delegator.findList("InventoryAndItemProduct", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<GenericValue> listInventoryItemTotal = delegator.findList("ProductNameByInventoryItemTotal", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue inventoryItemTotal : listInventoryItemTotal) {
    		String productIdTotal = inventoryItemTotal.getString("productId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(inventoryItemTotal);
			for (GenericValue inventoryItem : listInventoryItem) {
				String productId = inventoryItem.getString("productId");
				if (productIdTotal.equals(productId)) {
					listRowDetails.add(inventoryItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			for (GenericValue inv : listRowDetails){
				List<GenericValue> listInventoryItemLabel = delegator.findList("InventoryItemAndLabel", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inv.get("inventoryItemId"))), null, null, null, false);
		    	String statusId = null;
				if (!listInventoryItemLabel.isEmpty()){
		    		statusId = "INV_LABELED";
		    	} else {
		    		statusId = "INV_NO_LABEL";
		    	}
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("labelStatusId", statusId);
				mapTmp.put("inventoryItemId", inv.getString("inventoryItemId"));
				mapTmp.put("productId", inv.getString("productId"));
				mapTmp.put("expireDate", inv.getTimestamp("expireDate"));
				mapTmp.put("datetimeReceived", inv.getTimestamp("datetimeReceived"));
				mapTmp.put("facilityId", inv.getString("facilityId"));
				mapTmp.put("internalName", inv.getString("internalName"));
				mapTmp.put("quantityOnHandTotal", inv.getBigDecimal("quantityOnHandTotal"));
				mapTmp.put("availableToPromiseTotal", inv.getBigDecimal("availableToPromiseTotal"));
				mapTmp.put("quantityUomId", inv.getString("quantityUomId"));
				mapTmp.put("statusId", inv.getString("statusId"));
				
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendPurchaseRequirementByRequirementData(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<String> listRequirementId = (List<String>)context.get("requirementData[]");
		String roleTypeId = (String)context.get("roleTypeId");
		String sendMessage = (String)context.get("sendMessage");
		String action = (String)context.get("action");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			for (int i = 0; i < listRequirementId.size(); i++) {
				String requirementId = listRequirementId.get(i);
				GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
				List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				if (requirement != null){
					requirement.put("statusId", "LABEL_ITEM_PROPOSAL");
					delegator.createOrStore(requirement);
				}
				if(!listRequirementItem.isEmpty()){
					for (GenericValue requirementItem : listRequirementItem) {
						requirementItem.put("statusId", "LABEL_ITEM_PROPOSAL");
						delegator.createOrStore(requirementItem);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		try {
			List<String> listQaAdmin = new ArrayList<String>();
			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
			if (!listPartyGroups.isEmpty()){
				for (String group : listPartyGroups){
					try {
						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
						listManagers = EntityUtil.filterByDate(listManagers);
						if (!listManagers.isEmpty()){
							for (GenericValue manager : listManagers){
								listQaAdmin.add(manager.getString("partyIdFrom"));
							}
						}
					} catch (GenericEntityException e) {
						ServiceUtil.returnError("get Party relationship error!");
					}
				}
			}
			if(!listQaAdmin.isEmpty()){
				for (String managerParty : listQaAdmin){
					String targetLink = "statusId=LABEL_ITEM_PROPOSAL";
					String sendToPartyId = managerParty;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", action);
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
					mapContext.put("userLogin", userLogin);
					dispatcher.runSync("createNotification", mapContext);
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseOrderToPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PURCH_CREATE"));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("Requirement", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListRequirementPurchaseOrderToPO service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }*/
	
}
