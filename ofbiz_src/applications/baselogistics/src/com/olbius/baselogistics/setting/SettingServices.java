package com.olbius.baselogistics.setting;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.report.AccountingReportUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

public class SettingServices {
	public static final String module = SettingServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	try {
    		EntityCondition Cond1 = EntityCondition.makeCondition("thresholdsSale", EntityOperator.NOT_EQUAL, null);
    		EntityCondition Cond2 = EntityCondition.makeCondition("thresholdsDate", EntityOperator.NOT_EQUAL, null);
    		EntityCondition Cond3 = EntityCondition.makeCondition("thresholdsQuantity", EntityOperator.NOT_EQUAL, null);
    		List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond3);
    		EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.OR);
    		listAllConditions.add(allConds);
    		listIterator = delegator.find("ProductFacilityAndProduct", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductFacility service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addThresholdsProductFacilitys(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Map<String, Object> result = new FastMap<String, Object>();
		List<Object> listProductIdsJson = (List<Object>)context.get("listProductIds"); 
		List<Object> listFacilityIdsJson = (List<Object>)context.get("listFacilityIds"); 
		String thresholdsSale = (String)context.get("thresholdsSale");
		String thresholdsDate = (String)context.get("thresholdsDate");
		String thresholdsQuantity = (String)context.get("thresholdsQuantity");
		String thresholdsQuantityMax = (String)context.get("thresholdsQuantityMax");
		
		List<Map<String, Object>> listProductIds = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listFacilityIds = new ArrayList<Map<String, Object>>();
		
		Boolean isJson = false;
    	if (!listProductIdsJson.isEmpty()){
    		if (listProductIdsJson.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	if (isJson){
			String stringJson = "["+(String)listProductIdsJson.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					mapItems.put("productId", item.getString("productId"));
				}
				listProductIds.add(mapItems);
			}
    	} else {
    		listProductIds = (List<Map<String, Object>>)context.get("listProductIds");
    	}
    	
    	isJson = false;
    	if (!listFacilityIdsJson.isEmpty()){
    		if (listFacilityIdsJson.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	if (isJson){
			String stringJson = "["+(String)listFacilityIdsJson.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("facilityId")){
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				listFacilityIds.add(mapItems);
			}
    	} else {
    		listFacilityIds = (List<Map<String, Object>>)context.get("listProductIds");
    	}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	if (!listProductIds.isEmpty() && !listFacilityIds.isEmpty()){
    		for (Map<String, Object> facility : listFacilityIds) {
    			String facilityId = (String)facility.get("facilityId");
				for (Map<String, Object> product : listProductIds) {
					String productId = (String)product.get("productId");
					try {
						dispatcher.runSync("addThresholdsProductFacility", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "productId", productId, "facilityId", facilityId, "thresholdsSale", thresholdsSale, "thresholdsDate", thresholdsDate, "thresholdsQuantity", thresholdsQuantity, "thresholdsQuantityMax", thresholdsQuantityMax));
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: runsync service addThresholdsProductFacility error!");
					}
				}
			}
    	}
		return result;
	}
	
	public static Map<String, Object> addThresholdsProductFacility(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String)context.get("productId"); 
		String facilityId = (String)context.get("facilityId"); 
		String thresholdsSale = (String)context.get("thresholdsSale");
		String thresholdsDate = (String)context.get("thresholdsDate");
		String thresholdsQuantity = (String)context.get("thresholdsQuantity");
		String thresholdsQuantityMax = (String)context.get("thresholdsQuantityMax");
		GenericValue productFacility = delegator.findOne("ProductFacility", UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
		long thresholdsLong = Long.parseLong(thresholdsSale);
		BigDecimal thresholdsInput = new BigDecimal(thresholdsLong);
		long thresholdsDateLong = Long.parseLong(thresholdsDate);
		BigDecimal thresholdsDateInput = new BigDecimal(thresholdsDateLong);
		long thresholdsQuantityLong = Long.parseLong(thresholdsQuantity);
		long thresholdsQuantityMaxLong = Long.parseLong(thresholdsQuantityMax);
		BigDecimal thresholdsQuantityInput = new BigDecimal(thresholdsQuantityLong);
		BigDecimal thresholdsQuantityMaxInput = new BigDecimal(thresholdsQuantityMaxLong);
		
		if(productFacility != null){
			productFacility.put("thresholdsSale", thresholdsInput);
			productFacility.put("thresholdsDate", thresholdsDateInput);
			productFacility.put("thresholdsQuantity", thresholdsQuantityInput);
			productFacility.put("thresholdsQuantityMax", thresholdsQuantityMaxInput);
			delegator.store(productFacility);
			result.put("value", "update");
		}else{
			GenericValue productFacilityInput = delegator.makeValue("ProductFacility");
			productFacilityInput.put("productId", productId);
			productFacilityInput.put("facilityId", facilityId);
			productFacilityInput.put("thresholdsSale", thresholdsInput);
			productFacilityInput.put("thresholdsDate", thresholdsDateInput);
			productFacilityInput.put("thresholdsQuantity", thresholdsQuantityInput);
			productFacilityInput.put("thresholdsQuantityMax", thresholdsQuantityMaxInput);
			delegator.create(productFacilityInput);
			result.put("value", "create"); 
		}
		return result;
	}
	
	public static Map<String, Object> deleteProductFacility(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String)context.get("productId"); 
		String facilityId = (String)context.get("facilityId"); 
		GenericValue productFacility = delegator.findOne("ProductFacility", UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
		if(productFacility != null){
			delegator.removeValue(productFacility);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReturnReason(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	try {
    		listIterator = delegator.find("ReturnReason", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListReturnReason service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
   	 
   	public static Map<String, Object> addReturnReasonSetting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String returnReasonId = (String)context.get("returnReasonId"); 
		String description = (String)context.get("description"); 
		GenericValue returnReason = delegator.findOne("ReturnReason", UtilMisc.toMap("returnReasonId", returnReasonId.toUpperCase()), false);
		if(returnReason != null){
			returnReason.put("description", description);
			delegator.store(returnReason);
			result.put("value", "update");
		}else{
			GenericValue returnReasonInput = delegator.makeValue("ReturnReason");
			returnReasonInput.put("returnReasonId", returnReasonId.toUpperCase());
			returnReasonInput.put("description", description);
			delegator.create(returnReasonInput);
			result.put("value", "create");
		}
		return result;
	} 
   	
   	public static Map<String, Object> deleteReturnReasonSetting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String returnReasonId = (String)context.get("returnReasonId"); 
		GenericValue returnReason = delegator.findOne("ReturnReason", UtilMisc.toMap("returnReasonId", returnReasonId), false);
		List<GenericValue> listReturnReason = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnReasonId", returnReasonId)), null, null, null, false);
		if(!listReturnReason.isEmpty()){
			result.put("value", "exits");
		}else{
			if(returnReason != null){
				delegator.removeValue(returnReason);
				result.put("value", "success");
			}
		}
		return result;
	} 
   	
   	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListEnumInvoiceItemType(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	listAllConditions.add(cond);
    	try {
    		listIterator = delegator.find("EnumerationInvoiceItemType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListEnumInvoiceItemType service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
   	
   	public static Map<String, Object> loadEnumerationByEnumTypeId(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumTypeId = (String)context.get("enumTypeId"); 
		List<GenericValue> listEnum = delegator.findList("Enumeration", EntityCondition.makeCondition(UtilMisc.toMap("enumTypeId", enumTypeId)), null, null, null, false);
		result.put("listEnum", listEnum);
		return result;
	}
   	
   	public static Map<String, Object> loadInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumTypeId = (String)context.get("enumTypeId"); 
		/*List<GenericValue> listInvoiceItemType = new ArrayList<>();*/
		if(enumTypeId.equals("EXPORT_REASON")){  
			List<Map<String, Object>> listData = AccountingReportUtil.getAllInvoiceItemType("SINVOICE_ITM_ADJ", delegator);
			result.put("listInvoiceItemType", listData);
		}
		if(enumTypeId.equals("RECEIVE_REASON")){  
			List<Map<String, Object>> listData = AccountingReportUtil.getAllInvoiceItemType("PINV_PROD_ITEM", delegator);
			result.put("listInvoiceItemType", listData);
		}
		return result;
	} 
   	
   	public static Map<String, Object> addEnumerationInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumId = (String)context.get("enumId"); 
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId"); 
		String description = (String)context.get("description"); 
		String fromDateStr = (String)context.get("fromDate"); 
		String thruDateStr = (String)context.get("thruDate"); 
		long fromDateLog = Long.parseLong(fromDateStr);
		Timestamp fromDate = new Timestamp(fromDateLog);
		GenericValue enumerationInvoiceItemType = delegator.findOne("EnumerationInvoiceItemType", UtilMisc.toMap("enumId", enumId, "invoiceItemTypeId", invoiceItemTypeId, "fromDate", fromDate), false);
		if(enumerationInvoiceItemType != null){
			if(thruDateStr != null){
				long thruDateLog = Long.parseLong(thruDateStr);
				Timestamp thruDate = new Timestamp(thruDateLog);
				enumerationInvoiceItemType.put("thruDate", thruDate);
			}else{
				enumerationInvoiceItemType.put("thruDate", thruDateStr);
			}
			enumerationInvoiceItemType.put("description", description);
			delegator.store(enumerationInvoiceItemType);
			result.put("value", "update");  
		}else{
			GenericValue enumerationInvoiceItemTypeIn = delegator.makeValue("EnumerationInvoiceItemType");
			enumerationInvoiceItemTypeIn.put("enumId", enumId);
			enumerationInvoiceItemTypeIn.put("invoiceItemTypeId", invoiceItemTypeId);
			enumerationInvoiceItemTypeIn.put("description", description);
			enumerationInvoiceItemTypeIn.put("fromDate", fromDate);
			Timestamp thruDate = null;
			if(thruDateStr != null){
				long thruDateLog = Long.parseLong(thruDateStr);
				thruDate = new Timestamp(thruDateLog);
			}
			enumerationInvoiceItemTypeIn.put("thruDate", thruDate);
			delegator.create(enumerationInvoiceItemTypeIn);
			result.put("value", "create");  
		}
		return result;
	}
   	
   	public static Map<String, Object> deleteEnumerationInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumId = (String)context.get("enumId"); 
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId"); 
		String fromDateStr = (String)context.get("fromDate"); 
		long fromDateLog = Long.parseLong(fromDateStr);
		Timestamp fromDate = new Timestamp(fromDateLog);
		GenericValue enumerationInvoiceItemType = delegator.findOne("EnumerationInvoiceItemType", UtilMisc.toMap("enumId", enumId, "invoiceItemTypeId", invoiceItemTypeId, "fromDate", fromDate), false);
		Date currentDate = new Date();
		Timestamp thruDate = new Timestamp(currentDate.getTime());
		enumerationInvoiceItemType.put("thruDate", thruDate);
		delegator.store(enumerationInvoiceItemType);
		return result;
	}
   	
   	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListEnumerationTypeReasonWarehouse(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	try {
    		listIterator = delegator.find("EnumerationAndEnumerationType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListEnumerationTypeReasonWarehouse service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
   	
   	public static Map<String, Object> addEnumerationReasonSetting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
   		/*EnumerationInvoiceItemType*/
   		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();   
		String enumId = (String)context.get("enumId"); 
		String enumTypeId = (String)context.get("enumTypeId"); 
		String description = (String)context.get("description"); 
		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId.toUpperCase()), false);
		if(enumeration != null){
			enumeration.put("description", description);
			enumeration.put("enumTypeId", enumTypeId);
			delegator.store(enumeration);
			result.put("value", "update");
		}else{ 
			GenericValue enumerationInput = delegator.makeValue("Enumeration");
			enumerationInput.put("enumId", enumId.toUpperCase());
			enumerationInput.put("enumTypeId", enumTypeId);
			enumerationInput.put("enumCode", enumId.toUpperCase());
			enumerationInput.put("description", description);
			delegator.create(enumerationInput);
			result.put("value", "create");
		}
		return result;
	}
   	
   	public static Map<String, Object> deleteEnumerationReasonExportReceive(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumId = (String)context.get("enumId"); 
		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
		List<GenericValue> listEnumerationInvoiceItemType = delegator.findList("EnumerationInvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("enumId", enumId)), null, null, null, false);
		if(!listEnumerationInvoiceItemType.isEmpty()){
			result.put("value", "exits");
		}else{
			if(enumeration != null){
				delegator.removeValue(enumeration);
				result.put("value", "success");
			}
		}
		return result;
	}
   	
   	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListEnumInvoiceType(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	listAllConditions.add(cond);
    	try {
    		listIterator = delegator.find("EnumerationInvoiceType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListEnumInvoiceType service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
   	
   	public static Map<String, Object> loadInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumTypeId = (String)context.get("enumTypeId"); 
		List<GenericValue> listInvoiceType = FastList.newInstance();
		if(enumTypeId.equals("EXPORT_REASON")){  
			listInvoiceType = delegator.findList("InvoiceType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", "SALES_INVOICE")), null, null, null, false);
			result.put("listInvoiceType", listInvoiceType);
		}
		if(enumTypeId.equals("RECEIVE_REASON")){  
			listInvoiceType = delegator.findList("InvoiceType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", "PURCHASE_INVOICE")), null, null, null, false);
			result.put("listInvoiceType", listInvoiceType);
		}
		return result;
	} 
   	
   	public static Map<String, Object> addEnumerationInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumId = (String)context.get("enumId"); 
		String invoiceTypeId = (String)context.get("invoiceTypeId"); 
		String description = (String)context.get("description"); 
		String fromDateStr = (String)context.get("fromDate"); 
		String thruDateStr = (String)context.get("thruDate"); 
		long fromDateLog = Long.parseLong(fromDateStr);
		Timestamp fromDate = new Timestamp(fromDateLog);
		GenericValue enumerationInvoiceType = delegator.findOne("EnumerationInvoiceType", UtilMisc.toMap("enumId", enumId, "invoiceTypeId", invoiceTypeId, "fromDate", fromDate), false);
		if(enumerationInvoiceType != null){
			if(thruDateStr != null){
				long thruDateLog = Long.parseLong(thruDateStr);
				Timestamp thruDate = new Timestamp(thruDateLog);
				enumerationInvoiceType.put("thruDate", thruDate);
			}else{
				enumerationInvoiceType.put("thruDate", thruDateStr);
			}
			enumerationInvoiceType.put("description", description);
			delegator.store(enumerationInvoiceType);
			result.put("value", "update");  
		}else{
			GenericValue enumerationInvoiceTypeCr = delegator.makeValue("EnumerationInvoiceType");
			enumerationInvoiceTypeCr.put("enumId", enumId);
			enumerationInvoiceTypeCr.put("invoiceTypeId", invoiceTypeId);
			enumerationInvoiceTypeCr.put("description", description);
			enumerationInvoiceTypeCr.put("fromDate", fromDate);
			Timestamp thruDate = null;
			if(thruDateStr != null){
				long thruDateLog = Long.parseLong(thruDateStr);
				thruDate = new Timestamp(thruDateLog);
			}
			enumerationInvoiceTypeCr.put("thruDate", thruDate);
			delegator.create(enumerationInvoiceTypeCr);
			result.put("value", "create");  
		}
		return result;
	}
   	
   	public static Map<String, Object> deleteEnumerationInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String enumId = (String)context.get("enumId"); 
		String invoiceTypeId = (String)context.get("invoiceTypeId"); 
		String fromDateStr = (String)context.get("fromDate"); 
		long fromDateLog = Long.parseLong(fromDateStr);
		Timestamp fromDate = new Timestamp(fromDateLog);
		GenericValue enumerationInvoiceType = delegator.findOne("EnumerationInvoiceType", UtilMisc.toMap("enumId", enumId, "invoiceTypeId", invoiceTypeId, "fromDate", fromDate), false);
		Date currentDate = new Date();
		Timestamp thruDate = new Timestamp(currentDate.getTime());
		enumerationInvoiceType.put("thruDate", thruDate);
		delegator.store(enumerationInvoiceType);
		return result;
	}
   	
   	public static Map<String, Object> loadLocationFacilityType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listLocationType = delegator.findList("LocationFacilityType", null , null, null, null, false);
		result.put("listLocationType", listLocationType);
		return result;
	}
   	
   	public static Map<String, Object> loadParentLocationTypes(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		List<GenericValue> listParentLocationTypeMap = null;
		if(UtilValidate.isNotEmpty(locationFacilityTypeId)){
			List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
			EntityCondition entityCond = EntityCondition.makeCondition("locationFacilityTypeId", EntityOperator.NOT_EQUAL, locationFacilityTypeId);
			List<String> listData = getAllLocationFacilityType(locationFacilityTypeId, delegator);
			if(UtilValidate.isNotEmpty(listData)){
				tmpListCond.add(EntityCondition.makeCondition("locationFacilityTypeId", EntityOperator.NOT_IN, listData));
			}
			tmpListCond.add(entityCond);
			listParentLocationTypeMap = delegator.findList("LocationFacilityType", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
		}else{
			listParentLocationTypeMap = delegator.findList("LocationFacilityType", null, null, null, null, false);
		}
		result.put("listParentLocationTypeMap", listParentLocationTypeMap);
		return result;
	}
   	
   	public static Map<String, Object> createLocationType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String parentLocationFacilityTypeId = (String) context.get("parentLocationFacilityTypeId");
		String description = (String) context.get("description");
		String prefixCharacters = (String) context.get("prefixCharacters");
		String childNumber = (String) context.get("defaultChildNumber");
		BigDecimal defaultChildNumber = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(childNumber)) {
			defaultChildNumber = new BigDecimal (childNumber);
		}
		
		GenericValue locationFacilityType = delegator.makeValue("LocationFacilityType");
		GenericValue locationFacilityTypeUpdate = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId.toUpperCase()), false);
		if(UtilValidate.isNotEmpty(locationFacilityTypeUpdate)){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLLocationTypeIdExisted", (Locale)context.get("locale")));
		} else{
			if (UtilValidate.isNotEmpty(prefixCharacters)) {
				List<GenericValue> listByPrefix = delegator.findList("LocationFacilityType",
						EntityCondition.makeCondition("prefixCharacters", EntityOperator.EQUALS, prefixCharacters), null, null, null, false);
				if (!listByPrefix.isEmpty()) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLCharacterRepresentExisted", (Locale)context.get("locale")));
				}
			}
			locationFacilityType.put("locationFacilityTypeId", locationFacilityTypeId.toUpperCase());
			locationFacilityType.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
			locationFacilityType.put("description", description);
			locationFacilityType.put("prefixCharacters", prefixCharacters);
			locationFacilityType.put("defaultChildNumber", defaultChildNumber);
			delegator.create(locationFacilityType);
			result.put("value", "success");
		}
		return result;
	} 
   	
   	public static Map<String, Object> bindingEditLocationType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		String locationFacilityTypeId = (String)context.get("locationFacilityTypeId");
		Map<String, Object> result = new FastMap<String, Object>();
		GenericValue locationFacilityType = delegator.findOne("LocationFacilityType", false, UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId));;
		result.put("locationFacilityType", locationFacilityType);
		return result;
	}
   	
   	public static List<String> getAllLocationFacilityType(String locationFacilityTypeId, Delegator delegator) throws GenericEntityException{
		List<GenericValue> listTmp = delegator.findList("LocationFacilityType", EntityCondition.makeCondition("parentLocationFacilityTypeId", EntityOperator.EQUALS, locationFacilityTypeId), UtilMisc.toSet("locationFacilityTypeId", "parentLocationFacilityTypeId","description"), null, null, false);
		List<String> listLocationFacilityId = FastList.newInstance();
		if(listTmp != null){
			String itt = "";
			for (GenericValue genericValue : listTmp) {
				itt = genericValue.getString("locationFacilityTypeId");
				listLocationFacilityId.add(itt);
				listLocationFacilityId.addAll(getAllLocationFacilityType(itt, delegator));
			}
		}
		return listLocationFacilityId;
	}
   	
   	public static Map<String, Object> updateLocationFacilityType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String locationFacilityTypeId = (String) context.get("locationFacilityTypeId");
		String parentLocationFacilityTypeId = (String) context.get("parentLocationFacilityTypeId");
		String description = (String) context.get("description");
		String prefixCharacters = (String) context.get("prefixCharacters");
		String childNumber = (String) context.get("defaultChildNumber");
		
		GenericValue locationFacilityTypeUpdate = delegator.findOne("LocationFacilityType", UtilMisc.toMap("locationFacilityTypeId", locationFacilityTypeId.toUpperCase()), false);
		if(UtilValidate.isEmpty(locationFacilityTypeUpdate)){
			result.put("value", "error");
		} else{
			if (UtilValidate.isNotEmpty(prefixCharacters) && UtilValidate.isNotEmpty(locationFacilityTypeUpdate.get("prefixCharacters")) && !prefixCharacters.equals(locationFacilityTypeUpdate.get("prefixCharacters"))) {
				List<GenericValue> listByPrefix = delegator.findList("LocationFacilityType",
						EntityCondition.makeCondition("prefixCharacters", EntityOperator.EQUALS, prefixCharacters), null, null, null, false);
				if (!listByPrefix.isEmpty()) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLCharacterRepresentExisted", (Locale)context.get("locale")));
				}
			}
			BigDecimal defaultChildNumber = locationFacilityTypeUpdate.getBigDecimal("defaultChildNumber");
			if (UtilValidate.isNotEmpty(childNumber)) {
				defaultChildNumber = new BigDecimal (childNumber);
			}
			locationFacilityTypeUpdate.put("parentLocationFacilityTypeId", parentLocationFacilityTypeId);
			locationFacilityTypeUpdate.put("description", description);
			locationFacilityTypeUpdate.put("prefixCharacters", prefixCharacters);
			locationFacilityTypeUpdate.put("defaultChildNumber", defaultChildNumber);
			delegator.store(locationFacilityTypeUpdate);
			result.put("value", "success");
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
   	
   	public static Map<String, Object> deleteLocationType(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
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
   	
   	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInventoryItemLabel(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition cond = EntityCondition.makeCondition();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(cond);
    	try {
    		listIterator = delegator.find("InventoryItemLabel", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListInventoryItemLabel service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
   	
   	public static Map<String, Object> addInventoryItemLabelSetting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String inventoryItemLabelId = (String)context.get("inventoryItemLabelId"); 
		String inventoryItemLabelTypeId = (String)context.get("inventoryItemLabelTypeId"); 
		String description = (String)context.get("description");
		GenericValue inventoryItemLabel = delegator.findOne("InventoryItemLabel", UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId.toUpperCase()), false);
		if(inventoryItemLabel != null){
			inventoryItemLabel.put("inventoryItemLabelTypeId", inventoryItemLabelTypeId);
			inventoryItemLabel.put("description", description);
			delegator.store(inventoryItemLabel);
			result.put("value", "update");
		}else{
			GenericValue inventoryItemLabelInput = delegator.makeValue("InventoryItemLabel");
			inventoryItemLabelInput.put("inventoryItemLabelTypeId", inventoryItemLabelTypeId);
			inventoryItemLabelInput.put("inventoryItemLabelId", inventoryItemLabelId.toUpperCase());
			inventoryItemLabelInput.put("description", description);
			delegator.create(inventoryItemLabelInput);
			result.put("value", "create");
		}
		return result;
	} 
   	
   	public static Map<String, Object> deleteInventoryItemLabelSetting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String inventoryItemLabelId = (String)context.get("inventoryItemLabelId"); 
		GenericValue inventoryItemLabel = delegator.findOne("InventoryItemLabel", UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId), false);
		List<GenericValue> listInventoryItemLabelAppl = delegator.findList("InventoryItemLabelAppl", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId)), null, null, null, false);
		if(!listInventoryItemLabelAppl.isEmpty()){
			result.put("value", "exits");
		}else{
			if(inventoryItemLabel != null){
				delegator.removeValue(inventoryItemLabel);
				result.put("value", "success");
			}
		}
		return result;
	} 
}
