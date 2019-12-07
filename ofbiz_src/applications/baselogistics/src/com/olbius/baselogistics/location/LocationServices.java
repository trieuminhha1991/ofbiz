package com.olbius.baselogistics.location;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.baselogistics.LogisticsServices;

public class LocationServices {
	
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	
	public static Map<String, Object> getLocationFacilityAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		
		List<String> orderBy = new ArrayList<String>();
		List<GenericValue> listlocationFacility = delegator.findList("LocationFacilityInventoryItemLocationSum",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);	
		result.put("listlocationFacility", listlocationFacility);
		return result;
	}
	
	public static Map<String, Object> getListProductAvalibleAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProductAvalible = FastList.newInstance();
		if (clearInventoryItemInLocation(delegator)) {
			String productId =  (String) context.get("productId");
			String facilityId =  (String) context.get("facilityId");
			Set<String> fieldSelect = FastSet.newInstance();
			fieldSelect.add("locationId");
			List<GenericValue> listLocationFacility = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), fieldSelect, null, null, false);
			List<String> listLocationId = FastList.newInstance();
			for (GenericValue g : listLocationFacility) {
				listLocationId.add(g.getString("locationId"));
			}
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("expireDate");
			for (String s : listLocationId) {
				List<GenericValue> listInventoryItemLocation = FastList.newInstance();
				if (UtilValidate.isEmpty(productId)) {
					listInventoryItemLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", s)), null, orderBy, null, false);
				} else {
					listInventoryItemLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", s, "productId", productId)), null, orderBy, null, false);
				}
				
				listProductAvalible.addAll(listInventoryItemLocation);
			}
		}
		result.put("listProductAvalible", listProductAvalible);
		return result;
	}

	private static boolean clearInventoryItemInLocation(Delegator delegator) {
		BigDecimal zezo = BigDecimal.ZERO;
		try {
			List<GenericValue> listInventoryItemInLocationClear = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("quantity", zezo)), null, null, null, false);
			if (UtilValidate.isEmpty(listInventoryItemInLocationClear)) {
				return true;
			}
			delegator.removeAll(listInventoryItemInLocationClear);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInventoryItemInLocation (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> mapInventoryItemInLocation = new FastMap<String, Object>();
		if (clearInventoryItemInLocation(delegator)) {
			List<String> locationId = (List<String>) context.get("arrayLocationId[]");
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("expireDate");
			for (String string : locationId) {
				List<GenericValue> listInventoryItemInLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", string)), null, orderBy, null, false);
				mapInventoryItemInLocation.put(string, listInventoryItemInLocation);
			}
		}
		result.put("mapInventoryItemInLocation", mapInventoryItemInLocation);
		return result;
	}
	
	public static void addToLocationEventAjax(HttpServletRequest request, HttpServletResponse response) throws GenericTransactionException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		JSONArray jsonData = JSONArray.fromObject(request.getParameter("totalRecord"));
		boolean success = true;
		boolean beganTx = TransactionUtil.begin(7200);
		for (int i = 0; i < jsonData.size(); i++) {
			JSONObject thisRow = jsonData.getJSONObject(i);
			String inventoryItemId = (String) thisRow.get("inventoryItemId");
			String productId = (String) thisRow.get("productId");
			String locationId = (String) thisRow.get("locationId");
			String uomId = (String) thisRow.get("uomId");
			Object quantity = (Integer)thisRow.get("quantity");
			
			if (UtilValidate.isEmpty(quantity)) {
				continue;
			}
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setGroupingSeparator(',');
			symbols.setDecimalSeparator('.');
			String pattern = "#,##0.0#";
			DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
			decimalFormat.setParseBigDecimal(true);
			try {
				quantity = (BigDecimal) decimalFormat.parse(quantity.toString());
			} catch (ParseException e) {
				Debug.logError(e, module);
			}
			Long strExpireDate = (Long) thisRow.get("expireDate");
			java.util.Date date = new java.util.Date(strExpireDate);
			Timestamp expireDate =  new java.sql.Timestamp((new java.sql.Date(date.getTime())).getTime());
			
			GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "productId", productId, "locationId", locationId, "uomId", uomId, "quantity", quantity, "expireDate", expireDate));
			try {
				delegator.createOrStore(inventoryItemLocation);
			} catch (Exception e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				success = false;
				break;
			}
		}
		TransactionUtil.commit(beganTx);
		if (clearInventoryItemInLocation(delegator)) {
			if (success) {
				request.setAttribute("RESULT_MESSAGE", "SUSSESS");
			} else {
				request.setAttribute("RESULT_MESSAGE", "ERROR");
			}
		}
	}
	
	public static Map<String, Object> checkHasInventoryInLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String) context.get("facilityId");
		Set<String> field = FastSet.newInstance();
		field.add("locationId");
		List<GenericValue> listProductInventoryItem = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), field, null, null, false);
		Map<String, Boolean> mapResult = FastMap.newInstance();
		List<GenericValue> listInventoryItemLocation = FastList.newInstance();
		for (GenericValue x : listProductInventoryItem) {
			boolean boolResult = true;
			String locationId = (String) x.get("locationId");
			listInventoryItemLocation = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			if (UtilValidate.isEmpty(listInventoryItemLocation)) {
				boolResult = false;
			}
			mapResult.put(locationId, boolResult);
		}
		result.put("result", mapResult);
		return result;
	}
	
	public static Map<String, Object> updateLocationFacilityAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String locationId = (String)context.get("locationId");
		String locationCode = (String)context.get("locationCode");
		String description = (String)context.get("description");
		Map<String, String> fieldsLocationFacility = UtilMisc.toMap("locationId", locationId, "locationCode", locationCode, "description", description);
		boolean success = true;
		try {
			GenericValue newLocationFacility = delegator.makeValue("LocationFacility", fieldsLocationFacility);
			delegator.store(newLocationFacility);
		} catch (GenericEntityException e) {
			success = false;
		} finally {
			result.put("success", success);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deleteLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericTransactionException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<String> arrayLocationId = (List<String>) context.get("arrayLocationId[]");
		String facilityId = (String) context.get("facilityId");
		List<GenericValue> listInventoryItemInLocation = FastList.newInstance();
		boolean beganTx = TransactionUtil.begin(7200);
		/*for (String string : locationId) {
			try {
				listInventoryItemInLocation = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", string)), null, null, null, false);
				
				delegator.removeAll(listInventoryItemInLocation);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
			try {
				List<GenericValue> listLocation = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				Map<String, String> mapLocation = FastMap.newInstance();
				for (GenericValue x : listLocation) {
					String locationIdKey = (String) x.get("locationId");
					String parentLocationId = (String) x.get("parentLocationId");
					mapLocation.put(locationIdKey, parentLocationId);
				}
				List<GenericValue> listLocationParents = FastList.newInstance();
				for (GenericValue x : listInventoryItemInLocation) {
					String thisLocationId = (String) x.get("locationId");
					String parentLocationId = mapLocation.get(thisLocationId);
					if (UtilValidate.isEmpty(parentLocationId)) {
						continue;
					}
					String inventoryItemId = (String) x.get("inventoryItemId");
					x.set("locationId", parentLocationId);
					listLocationParents = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", parentLocationId)), null, null, null, false);
					if (UtilValidate.isNotEmpty(listLocationParents)) {
						for (GenericValue g : listLocationParents) {
							String parentsInventoryItemId = (String) g.get("inventoryItemId");
							if (inventoryItemId.equals(parentsInventoryItemId)) {
								BigDecimal oldQuantity = g.getBigDecimal("quantity"); 
								BigDecimal newQuantity = x.getBigDecimal("quantity"); 
								x.set("quantity", oldQuantity.add(newQuantity));
							}
						}
					}
//						delegator.createOrStore(x);
				}
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
			try {
				delegator.removeByAnd("LocationFacility", UtilMisc.toMap("locationId", string));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
		}*/
		
		for (String locationId : arrayLocationId) {
			boolean checkExsits = true;
			try {
				listInventoryItemInLocation = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
				List<String> listLocationIdChildren = getAllLocationFacility(locationId, facilityId, delegator);
				if(UtilValidate.isNotEmpty(listLocationIdChildren)){
					for (String locationIdChildent : listLocationIdChildren) {
						List<GenericValue> listListIIChildren = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationIdChildent)), null, null, null, false);
						if(UtilValidate.isNotEmpty(listListIIChildren)){
							checkExsits = false;
						}
					}
					
					if(checkExsits == true){
						if(UtilValidate.isEmpty(listInventoryItemInLocation)){
							delegator.removeByAnd("LocationFacility", UtilMisc.toMap("locationId", locationId));
						}
					}
				}else{
					if(checkExsits == true){
						if(UtilValidate.isEmpty(listInventoryItemInLocation)){
							delegator.removeByAnd("LocationFacility", UtilMisc.toMap("locationId", locationId));
						}
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		TransactionUtil.commit(beganTx);
		result.put("result", "success");
		return result;
	}
	
	public static List<String> getAllLocationFacility(String locationId, String facilityId, Delegator delegator) throws GenericEntityException{
		List<GenericValue> listTmp = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("parentLocationId", locationId, "facilityId", facilityId)), UtilMisc.toSet("locationId", "parentLocationId","facilityId"), null, null, false);
		List<String> listLocationId = FastList.newInstance();
		if(listTmp != null){
			String itt = "";
			for (GenericValue genericValue : listTmp) {
				itt = genericValue.getString("locationId");
				listLocationId.add(itt);
				listLocationId.addAll(getAllLocationFacility(itt, facilityId, delegator));
			}
		}
		return listLocationId;
	}
	
	public static Map<String, Object> createLocationFacilityAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String locationId = delegator.getNextSeqId("LocationFacility");
		String facilityId = (String)context.get("facilityId");
		String parentLocationId = (String)context.get("parentLocationId");
		String locationCode = (String)context.get("locationCode");
		String locationFacilityTypeId = (String)context.get("locationFacilityTypeId");
		String description = (String)context.get("description");
		Map<String, String> fieldsLocationFacility = UtilMisc.toMap("locationId", locationId, "facilityId", facilityId, "parentLocationId", parentLocationId, "locationCode", locationCode, "locationFacilityTypeId", locationFacilityTypeId, "description", description);
		try {
			GenericValue newLocationFacility = delegator.makeValue("LocationFacility", fieldsLocationFacility);
			delegator.create(newLocationFacility);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("locationId", locationId);
		return result;
	}
	
	public static Map<String, Object> getGeneralQuantityAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		Map<String, Integer> mapTotalQuantity = FastMap.newInstance();
		Map<String, Integer> mapTotalQuantityOriginal = FastMap.newInstance();
		List<GenericValue> listLocationFacility = delegator.findList("LocationFacility",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		List<GenericValue> listLocationFacilityQuantity = delegator.findList("LocationFacilityAndInventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		for (GenericValue x : listLocationFacility) {
			String locationId = (String) x.get("locationId");
			int quantity = 0;
			for (GenericValue g : listLocationFacilityQuantity) {
				String locationIdQuantity = (String) g.get("locationId");
				if (locationId.equals(locationIdQuantity)) {
					quantity += g.getBigDecimal("quantity").intValue();
				}
			}
			mapTotalQuantity.put(locationId, quantity);
			mapTotalQuantityOriginal.put(locationId, quantity);
		}
		for (GenericValue g : listLocationFacility) {
			String parentLocationId = (String) g.get("parentLocationId");
			if (UtilValidate.isEmpty(parentLocationId)) {
				String locationId = (String) g.get("locationId");
				if (checkHasChild(listLocationFacility, locationId)) {
					mapTotalQuantity.put(locationId, increasingFromChilds(mapTotalQuantity, locationId, listLocationFacility));
				}
			}
		}
		for (GenericValue x : listLocationFacility) {
			String locationId = (String) x.get("locationId");
			if (checkHasChild(listLocationFacility, locationId)) {
				mapTotalQuantity.put(locationId, mapTotalQuantity.get(locationId) + mapTotalQuantityOriginal.get(locationId));
			}
		}
		result.put("totalQuantity", mapTotalQuantity);
		return result;
	}
	
	private static Boolean checkHasChild(List<GenericValue> listLocationFacility, String locationId) {
		for (GenericValue x : listLocationFacility) {
			String parentLocationId = (String) x.get("parentLocationId");
			if (locationId.equals(parentLocationId)) {
				return true;
			}
		}
		return false;
	}
	
	private static Integer increasingFromChilds( Map<String, Integer> mapTotalQuantity, String parents, List<GenericValue> listLocationFacility) {
		int result = 0;
		for (GenericValue x : listLocationFacility) {
			String parentLocationId = (String) x.get("parentLocationId");
			if (parents.equals(parentLocationId)) {
				String locationId = (String) x.get("locationId");
				if (checkHasChild(listLocationFacility, locationId)) {
					mapTotalQuantity.put(locationId, increasingFromChilds(mapTotalQuantity, locationId, listLocationFacility));
				}
				int quantity = mapTotalQuantity.get(locationId);
				result += quantity;
			}
		}
		return result;
	}
	
	public static Map<String, Object> getLocationFacility(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	String facilityId= (String)context.get("facilityId");
    	try {    		
    		listIterator = delegator.find("LocationFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, null);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLocationFacilityJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator.getCompleteList());
    	return successResult;
    }
	
	public static Map<String, Object> updateLocationType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String parentLocationFacilityTypeId = (String) context.get("parentLocationFacilityTypeId");
		String description = (String) context.get("description");
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId), false);
		if(parentLocationFacilityTypeId != null){
//				GenericValue locationFacilityTypeCheckParent = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", parentLocationFacilityTypeId), false);
//				String parentLocationFacilityIdData = (String) locationFacilityTypeCheckParent.get("parentLocationFacilityTypeId");
//				if(parentLocationFacilityTypeId.equals(locationFacilityTypeId)){
//					result.put("value", "errorParent");
//					return result;
//				}
			int check = checkParentLocationFacilityType(dpx, parentLocationFacilityTypeId, locationFacilityTypeId);
			if(check == 2){
				result.put("value", "parentError");
				return result;
			}
//					if(parentLocationFacilityIdData != null){
//						if(parentLocationFacilityIdData.equals(locationFacilityTypeId)){
//							result.put("value", "parentError");
//							return result;
//						}
//					}
			
			locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
			locationFacilityType.put("description", description);
			delegator.store(locationFacilityType);
			result.put("value", "success");
		} else{
			locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
			locationFacilityType.put("description", description);
			delegator.store(locationFacilityType);
			result.put("value", "success");
		}
		return result;
	}
	
	public static int checkParentLocationFacilityType(DispatchContext dpx, String parentLocationFacilityTypeIdInput, String locationFacilityTypeId) throws GenericEntityException {
		int index = 0;
		Delegator delegator = dpx.getDelegator();
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", false, UtilMisc.toMap("locationFacilityTypeId", parentLocationFacilityTypeIdInput));;
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
	
	public static Map<String, Object> checkProductNotLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("inventoryItemId");
		
		List<GenericValue> listProductInventoryItem = delegator.findList("InventoryAndItemProduct", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		List<GenericValue> listProductHasLocation = delegator.findList("LocationFacilityAndInventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		
		if(UtilValidate.isEmpty(listProductInventoryItem)){
			result.put("result", false);
			return result;
		}
		if(UtilValidate.isEmpty(listProductHasLocation)){
			result.put("result", true);
			return result;
		}
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal remain = BigDecimal.ZERO;
		for (GenericValue x : listProductInventoryItem) {
			String inventoryItemId = (String) x.get("inventoryItemId");
			remain = x.getBigDecimal("quantityOnHandTotal");
			for (GenericValue z : listProductHasLocation) {
				if (z.containsValue(inventoryItemId)) {
					quantity = z.getBigDecimal("quantity");
					remain = remain.subtract(quantity);
				}
			}
			x.set("quantityOnHandTotal", remain);
		}
		List<GenericValue> noQuantity = FastList.newInstance();
		for (GenericValue f : listProductInventoryItem) {
			BigDecimal thisQuantityOnHandTotal = (BigDecimal) f.get("quantityOnHandTotal");
			if (thisQuantityOnHandTotal.intValue() <= 0) {
				noQuantity.add(f);
			}
		}
		listProductInventoryItem.removeAll(noQuantity);
		boolean boolresult = true;
		if (UtilValidate.isEmpty(listProductInventoryItem)) {
			boolresult = false;
		}
		result.put("result", boolresult);
		return result;
	}
	
	public static Map<String, Object> getAllProductNotLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("inventoryItemId");
		
		List<GenericValue> listProductInventoryItem = delegator.findList("InventoryAndItemProduct", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		List<GenericValue> listProductHasLocation = delegator.findList("LocationFacilityAndInventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal remain = BigDecimal.ZERO;
		if (!listProductInventoryItem.isEmpty() && !listProductInventoryItem.isEmpty()){
			for (GenericValue x : listProductInventoryItem) {
				String inventoryItemId = (String) x.get("inventoryItemId");
				remain = x.getBigDecimal("quantityOnHandTotal");
				for (GenericValue z : listProductHasLocation) {
					if (z.containsValue(inventoryItemId)) {
						quantity = z.getBigDecimal("quantity");
						remain = remain.subtract(quantity);
					}
				}
				x.set("quantityOnHandTotal", remain);
			}
			List<GenericValue> noQuantity = FastList.newInstance();
			for (GenericValue f : listProductInventoryItem) {
				BigDecimal thisQuantityOnHandTotal = (BigDecimal) f.get("quantityOnHandTotal");
				if (thisQuantityOnHandTotal.intValue() <= 0) {
					noQuantity.add(f);
				}
			}
			listProductInventoryItem.removeAll(noQuantity);
		}
		result.put("listProductNotLocation", listProductInventoryItem);
		return result;
	}
	
	/**
	 * location and pick list item
	 */
	public static Map<String, Object> createPicklistItemLocation (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String locationId = (String)context.get("locationId");
		String picklistBinId = (String)context.get("picklistBinId");
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		String inventoryItemId = (String)context.get("inventoryItemId");
		String shipGroupSeqId = (String)context.get("shipGroupSeqId");
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		
		BigDecimal amount = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(context.get("amount"))) {
			amount = (BigDecimal)context.get("amount");
		}
		
		Map<String, Object> map = FastMap.newInstance();
		map.put("locationId", locationId);
		map.put("picklistBinId", picklistBinId);
		map.put("orderId", orderId);
		map.put("orderItemSeqId", orderItemSeqId);
		map.put("inventoryItemId", inventoryItemId);
		map.put("shipGroupSeqId", shipGroupSeqId);
		try {
			GenericValue objPicklistItemLocation = delegator.findOne("PicklistItemLocation", false, map);
			if (UtilValidate.isNotEmpty(objPicklistItemLocation)) {
				return ServiceUtil.returnError("OLBIUS: PicklistItemLocation already existed!");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: findOne PicklistItemLocation error! " + e.toString());
		}
		
		GenericValue newObj = delegator.makeValue("PicklistItemLocation");
		newObj.set("locationId", locationId);
		newObj.set("picklistBinId", picklistBinId);
		newObj.set("orderId", orderId);
		newObj.set("orderItemSeqId", orderItemSeqId);
		newObj.set("inventoryItemId", inventoryItemId);
		newObj.set("quantity", quantity);
		newObj.set("shipGroupSeqId", shipGroupSeqId);
		newObj.set("amount", amount);
		
		try {
			delegator.create(newObj);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: create PicklistItemLocation error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
    }
	
}
