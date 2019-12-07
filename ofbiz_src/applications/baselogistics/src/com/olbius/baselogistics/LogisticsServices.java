package com.olbius.baselogistics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
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

import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.baselogistics.util.GeoUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.common.util.EntityMiscUtil;

public class LogisticsServices {
	
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	 
	public static Map<String, Object> getGeoAssocs(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String geoId = (String)context.get("geoId");
		String geoAssocTypeId = (String)context.get("geoAssocTypeId");
		String geoTypeId = (String)context.get("geoTypeId");
		List<GenericValue> listGeos = GeoUtil.getGeoAssocs(delegator, geoId, geoAssocTypeId, geoTypeId);
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("listGeos", listGeos);
    	return successResult;
	}
	
	public static Map<String, Object> getConvertPackingNumber(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String productId = (String)context.get("productId");
    	Delegator delegator = ctx.getDelegator();
    	String uomFromId = (String)context.get("uomFromId");
    	String uomToId = (String)context.get("uomToId");
    	BigDecimal convertNumber = BigDecimal.ONE;
    	convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("convertNumber", convertNumber);
    	return mapReturn;
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
	
	public static Map<String, Object> getQuantityUomBySupplier(DispatchContext ctx, Map<String, ?> context){
		String productId = (String)context.get("productId");
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		String quantityUomId = null;
		quantityUomId = LogisticsProductUtil.getQuantityUomBySupplier(delegator, productId, orderId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("quantityUomId", quantityUomId);
		return result;
	}
	public static Map<String, Object> checkPurchaseOrderReceipt(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String orderId = (String)context.get("orderId");
		Boolean createdDone = true;
		Delegator delegator = ctx.getDelegator();
		createdDone = LogisticsProductUtil.checkPurchaseOrderReceipt(delegator, orderId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("createdDone", createdDone);
		return result;
	}
	
	public static Map<String, Object> checkAllSalesOrderItemCreatedDelivery(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String orderId = (String)context.get("orderId");
		Boolean createdDone = true;
		Delegator delegator = ctx.getDelegator();
		createdDone = LogisticsProductUtil.checkAllSalesOrderItemCreatedDelivery(delegator, orderId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("createdDone", createdDone);
		return result;
	}
	
	public static Map<String, Object> checkAllTransferItemCreatedDelivery(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String transferId = (String)context.get("transferId");
		Boolean createdDone = true;
		Delegator delegator = ctx.getDelegator();
		createdDone = LogisticsProductUtil.checkAllTransferItemCreatedDelivery(delegator, transferId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("createdDone", createdDone);
		return result;
	}
	
	public static Map<String, Object> checkOrderStatus(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String orderId = (String)context.get("orderId");
		String statusId = "";
		Delegator delegator = ctx.getDelegator();
		statusId = LogisticsProductUtil.checkOrderStatus(delegator, orderId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("statusId", statusId);
		return result;
	}
	
	public static Map<String, Object> getQuantityOnHandDiffInInventoryItemDetail(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String inventoryItemId = (String)context.get("inventoryItemId"); 
		String inventoryItemDetailSeqId = (String)context.get("inventoryItemDetailSeqId");
		GenericValue inventoryItemDetail = delegator.findOne("InventoryItemDetail", UtilMisc.toMap("inventoryItemId", inventoryItemId, "inventoryItemDetailSeqId", inventoryItemDetailSeqId), false);
		BigDecimal quantityOnHandDiff = null; 
		if(inventoryItemDetail != null){
			quantityOnHandDiff = inventoryItemDetail.getBigDecimal("quantityOnHandDiff");
		}
		result.put("quantityOnHandDiff", quantityOnHandDiff);
		return result;
	}
	
	public static Map<String, Object> loadProductByLog(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listProduct = delegator.findList("Product", null, null, null, null, false);
		result.put("listProduct", listProduct);   
		return result;
	}
	
	public static Map<String, Object> getUserLoginCreatedInfo(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String partyId = (String)context.get("partyId");
		Delegator delegator = ctx.getDelegator();
		String partyName = PartyHelper.getPartyName(delegator, partyId, true, true);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("partyName", partyName);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDeliverers(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityId = null;
    	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
    		facilityId = parameters.get("facilityId")[0];
    	}
    	if (facilityId != null && !"".equals(facilityId)){
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    	}
    	String roleTypeId = null;
    	if (parameters.get("roleTypeId") != null && parameters.get("roleTypeId").length > 0){
    		roleTypeId = parameters.get("roleTypeId")[0];
    	}
    	if (roleTypeId != null && !"".equals(roleTypeId)){
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
    	}
    	List<GenericValue> listParties = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<Map<String, Object>> listReturns = new ArrayList<Map<String, Object>>();
    			
    	for (GenericValue party : listParties){
    		Map<String, Object> map = FastMap.newInstance();
    		map.put("partyId", party.getString("partyId"));
    		map.put("firstName", party.getString("firstName"));
    		map.put("middleName", party.getString("middleName"));
    		map.put("lastName", party.getString("lastName"));
    		map.put("birthDate", party.getString("birthDate"));
    		map.put("partyTypeId", party.getString("partyTypeId"));
    		map.put("statusId", party.getString("statusId"));
    		map.put("partyCode", party.getString("partyCode"));
    		map.put("description", party.getString("description"));
    		map.put("gender", party.getString("gender"));
    		map.put("personalTitle", party.getString("personalTitle"));
    		map.put("groupName", party.getString("groupName"));
    		
    		List<GenericValue> listDlvEntryRole = delegator.findList("DeliveryEntryRole", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "LOG_DELIVERER", "partyId", party.getString("partyId"))), null, null, null, false);
    		List<GenericValue> listDlvEntryRoleTmp = new ArrayList<GenericValue>();
    		List<String> listDlvEntryMissed = new ArrayList<String>();
    		for (GenericValue item : listDlvEntryRole){
    			if (item.getTimestamp("thruDate") != null && item.getTimestamp("thruDate").before(UtilDateTime.nowTimestamp())){
    				listDlvEntryRoleTmp.add(item);
    			}
    		}
    		if (!listDlvEntryRoleTmp.isEmpty()){
    			listDlvEntryRole.removeAll(listDlvEntryRoleTmp);
    			for (GenericValue item : listDlvEntryRoleTmp){
    				GenericValue dlvEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", item.getString("deliveryEntryId")));
    				if (!"DELI_ENTRY_DELIVERED".equals(dlvEntry.getString("statusId"))){
    					listDlvEntryMissed.add(dlvEntry.getString("deliveryEntryId"));
    				}
    			}
    		}
    		List<String> listDlvEntry = new ArrayList<String>();
    		List<String> listDlvEntryShipping = new ArrayList<String>();
    		if (!listDlvEntryRole.isEmpty()){
    			for (GenericValue dlvRole : listDlvEntryRole){
    				GenericValue dlvEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", dlvRole.getString("deliveryEntryId")));
    				if ("DELI_ENTRY_SHIPPING".equals(dlvEntry.getString("statusId"))){
    					listDlvEntryShipping.add(dlvRole.getString("deliveryEntryId"));
    				} else if (!"DELI_ENTRY_DELIVERED".equals(dlvEntry.getString("statusId")) && !"DELI_ENTRY_COMPLETED".equals(dlvEntry.getString("statusId"))){
    					listDlvEntry.add(dlvRole.getString("deliveryEntryId"));
    				}
        		}
    		}
    		map.put("listEntryShipping", listDlvEntryShipping);
    		map.put("listDeliveryEntry", listDlvEntry);
    		map.put("listDlvEntryMissed", listDlvEntryMissed);
    		
    		listReturns.add(map);
    	}
		successResult.put("listIterator", listReturns);
		return successResult;
    }
	
	public static Map<String, Object> getPartyByRoleAndRelationship(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String roleTypeId = (String)context.get("roleTypeId");
		Delegator delegator = ctx.getDelegator();
		List<String> listPartyOk = com.olbius.basehr.util.SecurityUtil.getPartiesByRolesWithCurrentOrg((GenericValue)context.get("userLogin"), roleTypeId, delegator);
		List<GenericValue> listParties= new ArrayList<GenericValue>();
		if (!listPartyOk.isEmpty()){
			for (String partyId : listPartyOk){
				GenericValue party = delegator.findOne("PartyNameView", false, UtilMisc.toMap("partyId", partyId));
				listParties.add(party);
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("listParties", listParties);
		return result;
	}
	
	public static Map<String, Object> getVehicles(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String ownerPartyId = (String)context.get("ownerPartyId");
		Timestamp fromDate = new Timestamp((Long)context.get("fromDate"));
		Timestamp thruDate = new Timestamp((Long)context.get("thruDate"));
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listVehicles= new ArrayList<GenericValue>();
		List<GenericValue> listVehicleInDEs= new ArrayList<GenericValue>();
		
		List<EntityCondition> listCondTh1 = new ArrayList<EntityCondition>();
		listCondTh1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
		listCondTh1.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate));
		
		EntityCondition th1 = EntityCondition.makeCondition(listCondTh1, EntityOperator.AND);
		
		List<EntityCondition> listCondTh2 = new ArrayList<EntityCondition>();
		listCondTh2.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate));
		listCondTh2.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
		
		EntityCondition th2 = EntityCondition.makeCondition(listCondTh2, EntityOperator.AND);
		
		List<EntityCondition> listCondDate = new ArrayList<EntityCondition>();
		listCondDate.add(th1);
		listCondDate.add(th2);
		
		listVehicleInDEs = delegator.findList("DeliveryEntryFixedAsset", EntityCondition.makeCondition(listCondDate, EntityOperator.OR), null, null, null, false);
		List<String> listTmp = new ArrayList<String>();
		if (!listVehicleInDEs.isEmpty()){
			for (GenericValue item : listVehicleInDEs){
				listTmp.add(item.getString("vehicleId"));
			}
		}
		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
		if (!listTmp.isEmpty()){
			EntityCondition inDECondition = EntityCondition.makeCondition("vehicleId", EntityOperator.NOT_IN, listTmp);
			listCond.add(inDECondition);
		}
		listCond.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
		listVehicles = delegator.findList("Vehicle", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		Map<String, Object> result = FastMap.newInstance();
		result.put("listVehicles", listVehicles);
		return result;
	}
	
	public static Map<String, Object> getPartyCarrierVehicles(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String vehicleId = (String)context.get("vehicleId");
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listParties= new ArrayList<Map<String, Object>>();
		List<GenericValue> listVehicleParties= new ArrayList<GenericValue>();
		List<GenericValue> listPartiesTmp= new ArrayList<GenericValue>();
		listVehicleParties = delegator.findList("VehicleParty", EntityCondition.makeCondition(UtilMisc.toMap("vehicleId", vehicleId, "roleTypeId", "LOG_DRIVER")), null, null, null, false);
		listVehicleParties = EntityUtil.filterByDate(listVehicleParties);
		for (GenericValue item : listVehicleParties){
			GenericValue party = delegator.findOne("PartyNameView", false, UtilMisc.toMap("partyId", item.getString("partyId")));
			listPartiesTmp.add(party);
		}
		for (GenericValue item : listPartiesTmp){
			Map<String, Object> map = FastMap.newInstance();
			map.put("description", PartyUtil.getPersonName(delegator, item.getString("partyId")));
			map.put("partyId", item.getString("partyId"));
			listParties.add(map);
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("listParties", listParties);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAllVehicles(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listVehicles = delegator.findList("VehicleDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
		successResult.put("listIterator", listVehicles);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListInfoGeneralSaleOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("GeneralSaleOrderInfo", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getShipperByDeliveryEntryId(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryEntryId = (String)context.get("deliveryEntryId"); 
		List<GenericValue> listDeliveryEntryRole = delegator.findList("DeliveryEntryRole", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "roleTypeId", "LOG_DELIVERER")), null, null, null, false);
		Date currentDate = new Date();
		Timestamp currentDateTime = new Timestamp(currentDate.getTime());
		String shipper = "";
		if(!listDeliveryEntryRole.isEmpty()){
			for (GenericValue deliveryEntryRole : listDeliveryEntryRole) {
				Timestamp thruDate = deliveryEntryRole.getTimestamp("thruDate");
				if(thruDate != null){
					if(thruDate.compareTo(currentDateTime) > 0){
						String partyId = deliveryEntryRole.getString("partyId");
						GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
						shipper = person.getString("lastName") + " " + person.getString("middleName") + " " + person.getString("firstName");
					}
				}else{
					String partyId = deliveryEntryRole.getString("partyId");
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					shipper = person.getString("lastName") + " " + person.getString("middleName") + " " + person.getString("firstName");
				}
			}
		}
		result.put("shipper", shipper);
		return result;
	}
	
	public static Map<String, Object> getRemainingSubTotalByDeliveryId(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryId = (String)context.get("deliveryId"); 
		/*String orderId = (String)context.get("orderId"); */
		/*GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);*/
		/*BigDecimal remainingSubTotal = orderHeader.getBigDecimal("remainingSubTotal");*/
		/*List<GenericValue> listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		int remainingSubTotalInt = remainingSubTotal.intValue();*/
		List<GenericValue> listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
		int orderQuantity = 0;
		if(!listDeliveryItem.isEmpty()){
			for (GenericValue deliveryItem : listDeliveryItem) {
				BigDecimal actualExportedQuantity = deliveryItem.getBigDecimal("actualExportedQuantity");
				if(actualExportedQuantity != null){
					int actualExportedQuantityInt = actualExportedQuantity.intValue();
					String inventoryItemId  = deliveryItem.getString("inventoryItemId");
					if(inventoryItemId != null){
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						String productId = inventoryItem.getString("productId");
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						String quantityUomId = product.getString("quantityUomId");
						Map<String, Object> mapContext = FastMap.newInstance();
						mapContext.put("product", product);
						mapContext.put("quantityUomId", quantityUomId);
						
						Map<String, Object> mapTmp = dispatcher.runSync("calculateProductPriceCustom", mapContext);
						BigDecimal price = (BigDecimal)mapTmp.get("price");
						int priceInt = price.intValue();
						int toTalPrice = actualExportedQuantityInt*priceInt;
						orderQuantity += toTalPrice;
					}
				}
			}
		}
		BigDecimal orderQuantityBig = new BigDecimal(orderQuantity);
		result.put("orderQuantity", orderQuantityBig);
		return result;
	}
	
	public static Map<String, Object> updateSupplierPartyIdForOrder(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId"); 
		String supplierPartyId = (String)context.get("supplierPartyId"); 
		String facilityId = (String)context.get("facilityId");
		String contactMechId = (String)context.get("contactMechId"); 
		Timestamp shipByDate = null;
		List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		if(!listOrderItemShipGroup.isEmpty()){
			for (GenericValue item : listOrderItemShipGroup){
				if (item.getString("suppplierPartyId") == null){
					item.put("supplierPartyId", supplierPartyId);
					item.put("facilityId", facilityId);
					delegator.store(item);
				}
			}
		}
		List<GenericValue> orderItems = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		shipByDate = orderItems.get(0).getTimestamp("estimatedDeliveryDate");
		
		// create PO from SO
		Map<String, Object> createPO = FastMap.newInstance();
		createPO.put("orderId", orderId);
		createPO.put("userLogin", (GenericValue)context.get("userLogin"));
		dispatcher.runSync("checkCreateDropShipPurchaseOrders", createPO);
		List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		String poOrderId = null;
		if (!listOrderItemAssoc.isEmpty()){
			poOrderId = listOrderItemAssoc.get(0).getString("toOrderId");
		}
		
		// set date to ship from date ordered
		List<GenericValue> poShipgroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", poOrderId)), null, null, null, false);
		for (GenericValue group : poShipgroups){
			group.put("shipByDate", shipByDate);
			delegator.store(group);
		}
		
		// Approved PO order
		Map<String, Object> approvePO = FastMap.newInstance();
		approvePO.put("orderId", poOrderId);
		approvePO.put("statusId", "ORDER_APPROVED");
		approvePO.put("setItemStatus", "Y");
		approvePO.put("userLogin", (GenericValue)context.get("userLogin"));
		dispatcher.runSync("changeOrderStatus", approvePO);
		
		// Receive PO to facility and export after that
		Map<String, Object> quickReceivePO = FastMap.newInstance();
		quickReceivePO.put("orderId", poOrderId);
		quickReceivePO.put("userLogin", (GenericValue)context.get("userLogin"));
		quickReceivePO.put("contactMechId", contactMechId);
		dispatcher.runSync("quickReceivePurchaseOrder", quickReceivePO);
		
		result.put("orderId", orderId);
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> getListFacilityBySupplier(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	String supplierPartyId = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.get("supplierPartyId") != null & parameters.get("supplierPartyId").length > 0){
    		supplierPartyId = (String)parameters.get("supplierPartyId")[0];
    	}
    	String orderId = null;
    	if (parameters.get("orderId") != null & parameters.get("orderId").length > 0){
    		orderId = (String)parameters.get("orderId")[0];
    	}
    	List<GenericValue> orderRoleFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
    	String companyStr = orderRoleFroms.get(0).getString("partyId");
    	Map<String, String> mapFacRoleCondition = new HashMap<String, String>();
    	if (supplierPartyId != null && !"".equals(supplierPartyId)){
		mapFacRoleCondition.put("partyId", supplierPartyId);
    	}
    	mapFacRoleCondition.put("roleTypeId", "SUPPLIER");
		mapFacRoleCondition.put("facilityTypeId", "VIRTUAL_WAREHOUSE");
		mapFacRoleCondition.put("ownerPartyId", companyStr);
    	listAllConditions.add(EntityCondition.makeCondition(mapFacRoleCondition));
    	List<GenericValue> listFacilities = new ArrayList<GenericValue>();
    	try {
    		listFacilities = delegator.findList("FacilityAndFacilityParty", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListFacilityBySupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listFacilities);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static Map<String, Object> JQGetFacilityContactMechs(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
       	LocalDispatcher dispatcher = ctx.getDispatcher();
       	String facilityId = null;
       	String contactMechPurposeTypeId = null;
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.get("facilityId") != null & parameters.get("facilityId").length > 0){
    		facilityId = (String)parameters.get("facilityId")[0];
    	}
    	if (parameters.get("contactMechPurposeTypeId") != null & parameters.get("contactMechPurposeTypeId").length > 0){
    		contactMechPurposeTypeId = (String)parameters.get("contactMechPurposeTypeId")[0];
    	}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		if (facilityId != null){
			Map<String, Object> mapTmp = FastMap.newInstance();
			mapTmp.put("facilityId", facilityId);
			mapTmp.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
			Map<String, Object> mapReturn = dispatcher.runSync("getFacilityContactMechs", mapTmp);
			successResult.put("listIterator", mapReturn.get("listFacilityContactMechs"));
		}
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveInventoryProductByLog(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId"); 
		String inventoryItemTypeId = (String)context.get("inventoryItemTypeId"); 
		String datetimeReceivedStr = (String)context.get("datetimeReceived"); 
		long datetimeReceivedLog = Long.parseLong(datetimeReceivedStr);
		Timestamp datetimeReceived = new Timestamp(datetimeReceivedLog);
		String rejectionId = (String)context.get("rejectionId"); 
		List<String> productIdDataInput = (List<String>) context.get("productIdDataInput[]");
		List<String> quantityAcceptedDataInput = (List<String>) context.get("quantityAcceptedDataInput[]");
		List<String>  quantityRejectedDataInput = (List<String>) context.get("quantityRejectedDataInput[]");
		List<String> unitCostDataInput = (List<String>) context.get("unitCostDataInput[]");
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		for (int i = 0; i < productIdDataInput.size(); i++) {
			Map<String, Object> mapTmp = FastMap.newInstance();
			
			String productId = productIdDataInput.get(i);
			BigDecimal quantityAccepted = new BigDecimal(quantityAcceptedDataInput.get(i));
			BigDecimal quantityRejected = new BigDecimal(quantityRejectedDataInput.get(i));
			BigDecimal unitCost = new BigDecimal(unitCostDataInput.get(i));
			mapTmp.put("productId", productId);
			mapTmp.put("quantityAccepted", quantityAccepted);
			mapTmp.put("quantityRejected", quantityRejected);
			mapTmp.put("unitCost", unitCost);
			mapTmp.put("facilityId", facilityId);
			mapTmp.put("inventoryItemTypeId", inventoryItemTypeId);
			mapTmp.put("userLogin", system);
			mapTmp.put("datetimeReceived", datetimeReceived);
			mapTmp.put("rejectionId", rejectionId);
			dispatcher.runSync("receiveInventoryProduct", mapTmp);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementToLOG(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue createByUserGe = (GenericValue)context.get("userLogin");
		String createByUser = (String)createByUserGe.get("userLoginId");
    	listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "LOG_REQUIREMENT"));
    	listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createByUser)); 
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "REQ_CREATED")), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				mapTmp.put("expireDate", rowDetail.getTimestamp("expireDate"));
				mapTmp.put("datetimeManufactured", rowDetail.getTimestamp("datetimeManufactured"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> createRequirementReceviceProduct(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
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
		String statusId = "REQ_CREATED";
		GenericValue requirement = delegator.makeValue("Requirement");
		String requirementId = delegator.getNextSeqId("Requirement");
		Date date = new Date();
		long dateLong = date.getTime();
		String partyIdFrom = (String) userLogin.get("partyId");
		String userLoginId = userLogin.getString("userLoginId");
		String partyId = null;
		List<GenericValue> 	listPartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)), null, null, null, false);
		for (GenericValue partyRelationship : listPartyRelationship) {
			String roleTypeIdTo = (String) partyRelationship.get("roleTypeIdTo");
			String roleTypeIdFrom = (String) partyRelationship.get("roleTypeIdFrom");
			if(roleTypeIdFrom.equals("MANAGER")){
				partyId = (String) partyRelationship.get("partyIdTo");
			}
			/*GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdTo), false);
			String parentTypeId = (String) roleType.get("parentTypeId");
			if(parentTypeId != null){
				if(parentTypeId.equals("EMPLOYEE")){
					partyId = (String) partyRelationship.get("partyIdTo");
				}
			}*/
		}
		Timestamp createDate = new Timestamp(dateLong);
		requirement.put("requirementId", requirementId);
		requirement.put("requirementTypeId", requirementTypeId);
		if(!facilityId.equals("")){
			requirement.put("facilityId", facilityId);
			List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
			listFacilityContactMech = EntityUtil.filterByDate(listFacilityContactMech);
			if(!listFacilityContactMech.isEmpty()){
				for(GenericValue facilityContactMech: listFacilityContactMech){
					String contactMechId = facilityContactMech.getString("contactMechId");
					requirement.put("contactMechId", contactMechId);
				}
			}
		}
		requirement.put("statusId", statusId);
		requirement.put("description", description);
		requirement.put("createdByUserLogin", userLoginId);
		requirement.put("createdDate", createDate);
		requirement.put("requiredByDate", requirementByDate);
		requirement.put("requirementStartDate", requirementStartDate);
		requirement.put("partyId", partyId);
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
    		String quantityUomIdToTransfer = item.get("quantityUomId");
    		String datetimeManufactured = item.get("datetimeManufactured");  
    		String expireDate = item.get("expireDate");  
    		long datetimeManufacturedLog = Long.parseLong(datetimeManufactured);
    		long expireDateLog = Long.parseLong(expireDate);
    		Timestamp datetimeManufacturedTime = new Timestamp(datetimeManufacturedLog);
    		Timestamp expireDateLogTime = new Timestamp(expireDateLog);
    		BigDecimal quantityBig = new BigDecimal(quantity);
    		requirementItem.put("requirementId", requirementId);
    		requirementItem.put("datetimeManufactured", datetimeManufacturedTime);
    		requirementItem.put("expireDate", expireDateLogTime);
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
	
	public static Map<String, Object> editRequirementItemReceiveByLOG(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String requirementId = (String) context.get("requirementId");
		String reqItemSeqId = (String) context.get("reqItemSeqId");
		String productId = (String) context.get("productId");
		String quantity = (String) context.get("quantity");
		String quantityUomId = (String) context.get("quantityUomId");
		String datetimeManufacturedStr = (String) context.get("datetimeManufactured");
		long datetimeManufacturedLog = Long.parseLong(datetimeManufacturedStr);
		Timestamp datetimeManufactured = new Timestamp(datetimeManufacturedLog);
		String expireDateStr = (String) context.get("expireDate");
		long expireDateLog = Long.parseLong(expireDateStr);
		Timestamp expireDate = new Timestamp(expireDateLog);
		GenericValue requirementItem = delegator.findOne("RequirementItem", UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId), false);
		String productIdData = requirementItem.getString("productId");
		BigDecimal quantityData = requirementItem.getBigDecimal("quantity");
		String quantityUomIdData = requirementItem.getString("quantityUomId");
		Timestamp datetimeManufacturedData = requirementItem.getTimestamp("datetimeManufactured");
		Timestamp expireDateData = requirementItem.getTimestamp("expireDate");
		BigDecimal quantityBig = new BigDecimal(quantity);
		if(productId.equals(productIdData) && quantityBig.compareTo(quantityData) == 0 && quantityUomId.equals(quantityUomIdData) && datetimeManufacturedData.compareTo(datetimeManufactured) == 0 && expireDateData.compareTo(expireDate) == 0){
			result.put("value", "notEdit");
		}else{
			requirementItem.put("productId", productId);
			requirementItem.put("quantity", quantityBig);
			requirementItem.put("quantityUomId", quantityUomId);
			requirementItem.put("datetimeManufactured", datetimeManufactured);
			requirementItem.put("expireDate", expireDate);
			delegator.store(requirementItem);
			result.put("value", "success");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addRequirementItemToREQLOG(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String requirementId = (String) context.get("requirementId");
    	List<String> listProductId = (List<String>)context.get("productIdData[]");
    	List<String> listQuantity = (List<String>)context.get("quantityData[]");
    	List<String> listQuantityUomId = (List<String>)context.get("quantityUomIdData[]");
    	List<String> listDatetimeManufactured = (List<String>)context.get("datetimeManufacturedData[]");
    	List<String> listExpireDate = (List<String>)context.get("expireDateData[]");
    	Date date = new Date();
		long dateLong = date.getTime();
		Timestamp createDate = new Timestamp(dateLong);
		String statusId = "REQ_CREATED";
    	for (int i = 0; i < listProductId.size(); i++) {
			String productId = listProductId.get(i);
			String quantity = listQuantity.get(i);
			String quantityUomId = listQuantityUomId.get(i);
			String datetimeManufacturedStr = listDatetimeManufactured.get(i);
			long datetimeManufacturedLog = Long.parseLong(datetimeManufacturedStr);
			Timestamp datetimeManufactured = new Timestamp(datetimeManufacturedLog);
			String expireDateStr = listExpireDate.get(i);
			long expireDateLog = Long.parseLong(expireDateStr);
			Timestamp expireDate = new Timestamp(expireDateLog);
			BigDecimal quantityBig = new BigDecimal(quantity);
			List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "productId", productId, "quantityUomId", quantityUomId, "datetimeManufactured", datetimeManufactured, "expireDate", expireDate)), null, null, null, false);
    		if(!listRequirementItem.isEmpty()){
    			for (GenericValue requirementItemData : listRequirementItem) {
					BigDecimal quantityBigData = requirementItemData.getBigDecimal("quantity");
					int quantityIntData = quantityBigData.intValue();
					int quantityInt = quantityBig.intValue();
					int quantitySumData = quantityIntData + quantityInt;
					BigDecimal quantitySumDataBig = new BigDecimal(quantitySumData);
					requirementItemData.put("quantity", quantitySumDataBig);
					delegator.store(requirementItemData);
				}
    		} else{
    			GenericValue requirementItem = delegator.makeValue("RequirementItem");
            	String reqId = delegator.getNextSeqId("RequirementItem");
            	requirementItem.set("reqItemSeqId", reqId);
    			requirementItem.put("requirementId", requirementId);
        		requirementItem.put("productId", productId);
        		requirementItem.put("quantity", quantityBig);
        		requirementItem.put("quantityUomId", quantityUomId);
        		requirementItem.put("datetimeManufactured", datetimeManufactured);
        		requirementItem.put("expireDate", expireDate);
        		requirementItem.put("createDate", createDate);
        		requirementItem.put("statusId", statusId);
        		try {
        			delegator.create(requirementItem);
        		} catch (GenericEntityException e) {
        		    return ServiceUtil.returnError(e.getStackTrace().toString());
        		}
    		}
		}
    	return successResult;
    }
	
	public static Map<String, Object> updateProductPlanItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String)context.get("orderId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if ("PURCHASE_ORDER".equals((String)orderHeader.get("orderTypeId"))){
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("-fromDate");
			List<GenericValue> listPlanByOrders = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, orderBy, null, false);
			listPlanByOrders = EntityUtil.filterByDate(listPlanByOrders);
			String currentOrderStatus = (String)orderHeader.get("statusId");
			if (!listPlanByOrders.isEmpty()){
				String productPlanId = (String)listPlanByOrders.get(0).get("productPlanId");
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (!orderItems.isEmpty()){
					if("ORDER_COMPLETED".equals(currentOrderStatus)){
						for (GenericValue orderItem : orderItems){
							String productId = (String)orderItem.get("productId");
							//cap nhat so lieu cho 1 tuan
							List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
							if(!planItems.isEmpty()){
								Boolean check = false;
								for (GenericValue planItem : planItems){
									if (productId.equals(planItem.get("productId"))){
										check = true;
										BigDecimal recentPlanQuantity = new BigDecimal(0);
										if (planItem.getBigDecimal("recentPlanQuantity") != null){
											recentPlanQuantity = planItem.getBigDecimal("recentPlanQuantity");
										}
										BigDecimal quantity = orderItem.getBigDecimal("quantity");
										Map<String,Object> contextTmp = new HashMap<String, Object>();
										contextTmp.put("productPlanId", productPlanId);
										contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
										contextTmp.put("recentPlanQuantity", quantity.add(recentPlanQuantity));
										contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
										contextTmp.put("userLogin", userLogin);
										try {
											dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
										} catch (GenericServiceException e) {
											return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
										}
									}
								}
								//note1:dat: else tao moi them planItem ... note: viet service tao moi planItem thi eca goi service luu vet chinh sua
								//note2: ko nen thuc hien note1, chuyen sang thuc hien khi approved order, neu thuc hien buoc nay
								//thi truowng hop hanng ve kho xuat hien sp moi
								if(!check){
									Map<String,Object> contextTmp = new HashMap<String, Object>();
									contextTmp.put("productPlanId", productPlanId);
									contextTmp.put("productId", productId);
									contextTmp.put("recentPlanQuantity", orderItem.getBigDecimal("quantity"));
									contextTmp.put("planQuantity", new BigDecimal(0));
									contextTmp.put("orderedQuantity", new BigDecimal(0));
									contextTmp.put("inventoryForecast", new BigDecimal(0));
									contextTmp.put("statusId", "PLAN_ITEM_CREATED");
									contextTmp.put("userLogin", userLogin);
									try {
										dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
									}
								}
							}
							//tiep tuc thuc hien update cac so lieu tren cho thang'
							
						}
					} else {
						if("ORDER_CANCELLED".equals(currentOrderStatus)){
								for (GenericValue orderItem : orderItems){
									String productId = (String)orderItem.get("productId");
									List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
									if(!UtilValidate.isEmpty(planItems)){
										for(GenericValue planItem: planItems){
											if (productId.equals(planItem.get("productId"))){
												BigDecimal quantity = orderItem.getBigDecimal("quantity");
												BigDecimal orderedQuantity = new BigDecimal(0);
												if(planItem.getBigDecimal("orderedQuantity") != null){
													orderedQuantity = planItem.getBigDecimal("orderedQuantity");
												}
												Map<String,Object> contextTmp = new HashMap<String, Object>();
												contextTmp.put("productPlanId", productPlanId);
												contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
												contextTmp.put("orderedQuantity", orderedQuantity.subtract(quantity));
												contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
												contextTmp.put("userLogin", userLogin);
												try {
													dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
												} catch (GenericServiceException e) {
													return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
												}
												
											}
										}
									}
								}
						} else {
							if ("ORDER_APPROVED".equals(currentOrderStatus)){
									for (GenericValue orderItem : orderItems){
										String productId = (String)orderItem.get("productId");
										List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
										if(!UtilValidate.isEmpty(planItems)){
											Boolean check = false;
											for(GenericValue planItem : planItems){
												if (productId.equals(planItem.get("productId"))){
													check = true;
													BigDecimal quantity = orderItem.getBigDecimal("quantity");
													BigDecimal orderedQuantity = new BigDecimal(0);
													if(planItem.getBigDecimal("orderedQuantity") != null){
														orderedQuantity = planItem.getBigDecimal("orderedQuantity");
													}
													Map<String,Object> contextTmp = new HashMap<String, Object>();
													contextTmp.put("productPlanId", productPlanId);
													contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
													contextTmp.put("orderedQuantity", orderedQuantity.add(quantity));
													contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
													contextTmp.put("userLogin", userLogin);
													try {
														dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
													} catch (GenericServiceException e) {
														return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
													}
												}
											}
											if(!check){
												Map<String,Object> contextTmp = new HashMap<String, Object>();
												contextTmp.put("productPlanId", productPlanId);
												contextTmp.put("productId", productId);
												contextTmp.put("recentPlanQuantity", new BigDecimal(0));
												contextTmp.put("planQuantity", new BigDecimal(0));
												contextTmp.put("orderedQuantity", orderItem.getBigDecimal("quantity"));
												contextTmp.put("inventoryForecast", new BigDecimal(0));
												contextTmp.put("statusId", "PLAN_ITEM_CREATED");
												contextTmp.put("userLogin", userLogin);
												try {
													dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
												} catch (GenericServiceException e) {
													return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
												}
											}
										}
									}
							}
						}
					}
				}
//					planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
//					Boolean check = true;
//					for (GenericValue planItem : planItems){
//						if (!"PLAN_ITEM_COMPLETED".equals((planItem.get("statusId")))){
//							check = false;
//						}
//					}
//					if (check){
//						GenericValue planHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
//						planHeader.put("statusId", "PLAN_COMPLETED");
//						delegator.store(planHeader);
//					}
			}
		}
		result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String,Object> createNotifyNewOrderToLogStorekeeper(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	String facilityId = null;
    	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    	String messages = "";
    	if ("SALES_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		facilityId = orderHeader.getString("originFacilityId");
    		messages = "NeedsToPrepareProductToExport";
    	} else if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		List<GenericValue> listOrderShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);    	
        	if (!listOrderShipGroups.isEmpty()){
        		facilityId = listOrderShipGroups.get(0).getString("facilityId");
        	}
        	messages = "NeedsToPrepareWarehouseToReceive";
    	}
    	GenericValue orderType = delegator.findOne("Delivery", false, UtilMisc.toMap("orderTypeId", orderHeader.getString("orderTypeId")));
    	if (UtilValidate.isNotEmpty(facilityId)){
    		List<GenericValue> listStorekeepers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
    		listStorekeepers = EntityUtil.filterByDate(listStorekeepers);
    		if (!listStorekeepers.isEmpty()){
    			LocalDispatcher dispatcher = ctx.getDispatcher();
    			for (GenericValue party : listStorekeepers) {
    				Map<String, Object> mapContext = new HashMap<String, Object>();
    				String header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)orderType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "OrderId", (Locale)context.get("locale")) +": [" +orderId+"]";
    				String action = "viewDetailPO?orderId="+orderId;
//    				String target = "orderId="+orderId;
            		mapContext.put("partyId", party.getString("partyId"));
            		mapContext.put("action", action);
            		mapContext.put("targetLink", "");
            		mapContext.put("header", header);
            		mapContext.put("ntfType", "ONE");
            		mapContext.put("userLogin", (GenericValue)context.get("userLogin"));
            		mapContext.put("openTime", UtilDateTime.nowTimestamp());
            		try {
            			dispatcher.runSync("createNotification", mapContext);
            		} catch (GenericServiceException e) {
            			e.printStackTrace();
            		}
				}
    		}
    	}
    	Map<String, Object> result = new FastMap<String, Object>();
    	return result;
	}
	
	public static Map<String, Object> checkInternalParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String partyId = (String)context.get("partyId");
    	Delegator delegator = ctx.getDelegator();
    	String ancestorId = (String)context.get("ancestorId");
    	Boolean isChild = false;
    	isChild = PartyUtil.checkAncestorOfParty(delegator, ancestorId, partyId, (GenericValue)context.get("userLogin"));
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("isChild", isChild);
    	return result;
	}
	
	public static Map<String, Object> getPartyAssignedFixedAsset(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String roleTypeId = (String)context.get("roleTypeId");
    	String fixedAssetId = (String)context.get("fixedAssetId");
    	Delegator delegator = ctx.getDelegator();
    	List<String> listPartyIds = new ArrayList<String>();
    	List<GenericValue> listParties = delegator.findList("PartyFixedAssetAssignment", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", roleTypeId, "fixedAssetId", fixedAssetId)), null, null, null, false);
    	listParties = EntityUtil.filterByDate(listParties);
    	for (GenericValue item : listParties) {
    		listPartyIds.add(item.getString("partyId"));
		}
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("listParties", listPartyIds);
    	return result;
	}
	
	public static Map<String, Object> getFixedAssetByParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String roleTypeId = (String)context.get("roleTypeId");
    	String partyId = (String)context.get("partyId");
    	Delegator delegator = ctx.getDelegator();
    	List<String> listFixedAsset = new ArrayList<String>();
    	List<GenericValue> listPartyFixedAsset = delegator.findList("PartyFixedAssetAssignment", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", roleTypeId, "fixedAssetId", partyId)), null, null, null, false);
    	listPartyFixedAsset = EntityUtil.filterByDate(listPartyFixedAsset);
    	for (GenericValue item : listPartyFixedAsset) {
    		listFixedAsset.add(item.getString("fixedAssetId"));
		}
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("listFixedAsset", listFixedAsset);
    	return result;
	}
	
	public static Map<String, Object> getDetailPostalAddress(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
   		Delegator delegator = ctx.getDelegator();
   		String contactMechId = (String)context.get("contactMechId");
   		String fullName = "";
   		GenericValue postalAddressFullNameDetail = delegator.findOne("PostalAddressDetail", false, UtilMisc.toMap("contactMechId", contactMechId));
   		fullName = postalAddressFullNameDetail.getString("fullName");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	successResult.put("fullName", fullName);
       	return successResult;
   	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetFixedAssetVehicles(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(EntityCondition.makeCondition("fixedAssetTypeId", EntityOperator.EQUALS, "TRANSINS_TANFS"));
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<String> listDeparts = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), UtilDateTime.nowTimestamp());
        	listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, listDeparts)); 
        	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
        	
        	listIterator = delegator.find("FixedAssetAndPartyInfo", cond, null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetFixedAssetVehicles service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetAsset(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<String> listDeparts = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), UtilDateTime.nowTimestamp());
    		List<EntityCondition> conds = FastList.newInstance();
    		
    		conds.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDeparts)); 
    		conds.add(EntityCondition.makeCondition("partyIdFix", EntityOperator.IN, listDeparts)); 
    		
    		listAllConditions.add(EntityCondition.makeCondition(conds, EntityOperator.OR));
        	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
        	
        	listIterator = delegator.find("FixedAssetAndPartyInfo", cond, null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetFixedAssetVehicles service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListShiper(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	List<GenericValue> listParties = new ArrayList<GenericValue>();
    	try {
        	listAllConditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, "SHIPPER")); 
        	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
        	
        	listIterator = delegator.find("EmplPositionAndPartyShiper", cond, null, null, listSortFields, opts);
        	listParties = listIterator.getCompleteList();
        	listIterator.close();
        	if (listParties.isEmpty()){
        		listParties = delegator.findList("PartyRelationshipPartyShiper", null, null, null, null, false);
        	}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListShiper service: " + e.toString();
			Debug.log(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listParties);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetDeliveryPartyRole(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
        	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
        	
        	listIterator = delegator.find("DeliveryEntryRoleTutorial", cond, null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetDeliveryPartyRole service: " + e.toString();
			Debug.log(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListOrgManagedByParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
   		try {
   	   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
   	       	List<String> listSortFields = (List<String>) context.get("listSortFields");
   	       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
   	       	
	   	    GenericValue userLogin = (GenericValue)context.get("userLogin");
	   	    List<String> departmentIds = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("partyId"), null, null);
   	       	listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, departmentIds));
	   	    EntityListIterator listIterator = delegator.find("PartyAndGroup",
	 				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	   	    result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
	}
	
	public static String printBarCodeProducts(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws GenericEntityException{
		javax.servlet.http.HttpSession session = request.getSession();
		Locale locale = request.getLocale();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> productList = FastList.newInstance();
		List<Map<String, Object>> productReturns = FastList.newInstance();
		BigDecimal pdfWidth = new BigDecimal("21");
		BigDecimal pdfHeight = new BigDecimal("29.7");
		
		String includeProductId = (String)request.getParameter("includeProductId");
		String includeProductName = (String)request.getParameter("includeProductName");
		String includeUnitPrice = (String)request.getParameter("includeUnitPrice");
		String includeCompanyName = (String)request.getParameter("includeCompanyName");
		
		if(UtilValidate.isNotEmpty(userLogin)){
			String productIds = request.getParameter("productIds");
			if (UtilValidate.isNotEmpty(request.getParameter("pdfWidth")) && !"0".equals((String)request.getParameter("pdfWidth"))){
				pdfWidth = new BigDecimal((String)request.getParameter("pdfWidth"));
			}
			if (UtilValidate.isNotEmpty(request.getParameter("pdfHeight")) && !"0".equals((String)request.getParameter("pdfHeight"))){
				pdfHeight = new BigDecimal((String)request.getParameter("pdfHeight"));
			}
			List<Map<String, Object>> listProduct = new ArrayList<Map<String, Object>>();
			JSONArray lists = JSONArray.fromObject(productIds);
			List<String> listProductIds = new ArrayList<String>();
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, Object> mapItems = new HashMap<String, Object>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					mapItems.put("productId", item.getString("productId"));
					listProductIds.add(item.getString("productId"));
				}
				if (item.containsKey("quantity")){
					mapItems.put("quantity", new BigDecimal(item.getString("quantity")));
				}
				if (item.containsKey("height")){
					mapItems.put("height", new BigDecimal(item.getString("height")));
				}
				if (item.containsKey("quantity")){
					mapItems.put("width", new BigDecimal(item.getString("width")));
				}
				listProduct.add(mapItems);
			}
			
			productList = delegator.findList("ProductAndPriceAndGoodIdentificationSimple", EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds), null, null, null, false);
			
			if(UtilValidate.isNotEmpty(productList)){
				for (GenericValue item : productList) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("productId", item.getString("productId"));
					map.put("productCode", item.getString("productCode"));
					map.put("productName", item.getString("productName"));
					map.put("idValue", item.getString("idValue"));
					map.put("price", item.getBigDecimal("price"));
					map.put("currencyUom", item.getString("currencyUomId"));
					for (Map<String, Object> all : listProduct) {
						if (((String)all.get("productId")).equals(item.getString("productId"))){
							map.put("quantity", (BigDecimal)all.get("quantity"));
							map.put("height", (BigDecimal)all.get("height"));
							map.put("width", (BigDecimal)all.get("width"));
							break;
						}
					}
					productReturns.add(map);
				}
			}
		} else{
			String errorMessage = UtilProperties.getMessage(resource, "SettingNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("productList", productReturns);
		request.setAttribute("pdfHeight", pdfHeight);
		request.setAttribute("pdfWidth", pdfWidth);
		request.setAttribute("includeProductId", includeProductId);
		request.setAttribute("includeProductName", includeProductName);
		request.setAttribute("includeUnitPrice", includeUnitPrice);
		request.setAttribute("includeCompanyName", includeCompanyName);
		return "success";
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetPartyAndPositionInDepartment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
   		try {
   	   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
   	       	List<String> listSortFields = (List<String>) context.get("listSortFields");
   	       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
   	       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
   	       	
   	       	String emplPositionTypeId = null;
	    	if (parameters.get("emplPositionTypeId") != null && parameters.get("emplPositionTypeId").length > 0){
	    		emplPositionTypeId = parameters.get("emplPositionTypeId")[0];
	    	}
	    	if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
	    		listAllConditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId));
			}
   	       	
	   	    GenericValue userLogin = (GenericValue)context.get("userLogin");
	   	    List<String> departmentIds = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), null, null);
   	       	listAllConditions.add(EntityCondition.makeCondition("departmentId", EntityJoinOperator.IN, departmentIds));
	   	    EntityListIterator listIterator = delegator.find("EmplPositionFulfillmentDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	   	    
	   	    List<GenericValue> listReturns = new ArrayList<GenericValue>();
	   	    listReturns = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
	   	    
	   	    successResult.put("listIterator", listReturns);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetRoleTypeByPartyIdNotInFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			
			String partyId = null;
	    	if (parameters.get("partyId") != null && parameters.get("partyId").length > 0){
	    		partyId = parameters.get("partyId")[0];
	    	}
	    	if (UtilValidate.isNotEmpty(partyId)) {
	    		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
	    	String facilityId = null;
	    	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
	    		facilityId = parameters.get("facilityId")[0];
	    	}
	    	if (UtilValidate.isNotEmpty(facilityId)){
	    		List<GenericValue> listFacilityParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId)), null, null, null, false);
	    		listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
	    		if (!listFacilityParty.isEmpty()){
		    		List<String> listRoleTypeIdExisted = new ArrayList<String>();
		    		listRoleTypeIdExisted = EntityUtil.getFieldListFromEntityList(listFacilityParty, "roleTypeId", true);
		    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, listRoleTypeIdExisted));
	    		}
	    		
	    		List<GenericValue> listRoleTypeFacility = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeGroupId", "FACILITY_ROLE")), null, null, null, false);
	    		listRoleTypeFacility = EntityUtil.filterByDate(listRoleTypeFacility);
	    		if (!listRoleTypeFacility.isEmpty()){
		    		List<String> listRoleTypeIds = new ArrayList<String>();
		    		listRoleTypeIds = EntityUtil.getFieldListFromEntityList(listRoleTypeFacility, "roleTypeId", true);
		    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleTypeIds));
	    		}
	    	}
	    	
	    	listAllConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "LOG_ROLE"));
			EntityListIterator listIterator = delegator.find("PartyRoleDetailAndPartyDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			
			List<GenericValue> listReturns = new ArrayList<GenericValue>();
			listReturns = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
			
			successResult.put("listIterator", listReturns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getGridConditions(DispatchContext ctx, Map<String, ? extends Object> context)
			throws Exception {
		Map<String, Object> result = FastMap.newInstance();
		Map<String, String[]> parametersPrime = (Map<String, String[]>) context.get("parameters");
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.putAll(parametersPrime);

		String strSortDataField = ((String[]) parameters.get("sortdatafield") != null)
				? ((String) parameters.get("sortdatafield")[0]) : ("");
		String strSortOrder = ((String[]) parameters.get("sortorder") != null)
				? ((String) parameters.get("sortorder")[0]) : ("");
		String isNullLast = parameters.get("nullLast") != null ? (String) parameters.get("nullLast")[0] : "N";
		List<String> listSortFields = new ArrayList<String>();
		if (strSortDataField != null && !strSortDataField.isEmpty()) {
			if (!"asc".equals(strSortOrder)) {
				// strSortDataField = "-" + strSortDataField;
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					arrSortDataField[i] = "-" + arrSortDataField[i];
					if ("Y".equals(isNullLast)) {
						listSortFields.add(arrSortDataField[i] + " NULLS LAST");
					} else {
						listSortFields.add(arrSortDataField[i]);
					}
				}
			} else {
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					if ("Y".equals(isNullLast)) {
						listSortFields.add(arrSortDataField[i] + " NULLS LAST");
					} else {
						listSortFields.add(arrSortDataField[i]);
					}
				}
			}
		}

		String strFilterListFields = ((String[]) parameters.get("filterListFields") != null)
				? ((String) parameters.get("filterListFields")[0]) : ("");
		String strNoConditionsFind = parameters.get("noConditionFind") != null
				? (String) parameters.get("noConditionFind")[0] : "N";
		String strConditionsFind = parameters.get("conditionsFind") != null
				? (String) parameters.get("conditionsFind")[0] : "Y";
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();

		if ((strFilterListFields != null && !strFilterListFields.isEmpty()) || strNoConditionsFind.equals("N")) {
			List<EntityCondition> listTmpCondition = new ArrayList<EntityCondition>();
			String tmpFieldName = "";
			if (strFilterListFields == null || strFilterListFields.isEmpty())
				strFilterListFields = strConditionsFind;
			String[] arrField = strFilterListFields.split("\\|OLBIUS\\|");
			String tmpGO = "0";
			String tmpG1 = "0";
			for (int i = 1; i < arrField.length; i++) {
				String[] arrTmp = arrField[i].split("\\|SUIBLO\\|");
				SqlOperator so = SqlOperator.valueOf(arrTmp[2]);
				if (so.toString().equals("EMPTY")) {
					continue;
				}
				EntityComparisonOperator<?, ?> fieldOp = null;
				tmpGO = arrTmp[3]; // Filter Operator
				Object fieldValue = arrTmp[1].toString(); // Filter value
				String fieldName = arrTmp[0]; // Filter name
				if (parameters.containsKey(fieldName)) {
					String[] existsArr = parameters.get(fieldName);
					List<String> tempList = FastList.newInstance();
					tempList.addAll(Arrays.asList(existsArr));
					tempList.add((String) fieldValue);
					String[] tempArr = new String[tempList.size()];
					tempList.toArray(tempArr);
					parameters.put(fieldName, tempArr);
				} else {
					parameters.put(fieldName, new String[] { fieldValue.toString() });
				}
				switch (so) {
				case CONTAINS: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = "%" + fieldValue + "%";
					break;
				}
				case DOES_NOT_CONTAIN: {
					fieldOp = EntityOperator.NOT_LIKE;
					fieldValue = "%" + fieldValue + "%";
					break;
				}
				case EQUAL: {
					fieldOp = EntityOperator.EQUALS;
					break;
				}
				case NOT_EQUAL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					break;
				}
				case GREATER_THAN: {
					fieldOp = EntityOperator.GREATER_THAN;
					break;
				}
				case LESS_THAN: {
					fieldOp = EntityOperator.LESS_THAN;
					break;
				}
				case GREATER_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.GREATER_THAN_EQUAL_TO;
					break;
				}
				case LESS_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.LESS_THAN_EQUAL_TO;
					break;
				}
				case STARTS_WITH: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = fieldValue + "%";
					break;
				}
				case ENDS_WITH: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = "%" + fieldValue;
					break;
				}
				case NULL: {
					fieldOp = EntityOperator.EQUALS;
					fieldValue = null;
					break;
				}
				case NOT_NULL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					fieldValue = null;
					break;
				}
				case EMPTY: {
					fieldOp = EntityOperator.EQUALS;
					fieldValue = null;
					break;
				}
				case NOT_EMPTY: {
					fieldOp = EntityOperator.NOT_EQUAL;
					fieldValue = null;
					break;
				}
				default:
					break;
				}
				if (fieldName.contains("(BigDecimal)")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setGroupingSeparator(',');
					symbols.setDecimalSeparator('.');
					String pattern = "#,##0.0#";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					decimalFormat.setParseBigDecimal(true);
					// parse the string
					try {
						fieldValue = (BigDecimal) decimalFormat.parse((String) fieldValue);
					} catch (ParseException e) {

					}
				}
				if (fieldName.contains("Long")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					try {
						if (UtilValidate.isNotEmpty(fieldValue)) {
							fieldValue = new java.lang.Long((String) fieldValue);
						}
					} catch (Exception e) {

					}
				}

				if (fieldName.contains("Double")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					try {
						if (UtilValidate.isNotEmpty(fieldValue)) {
							fieldValue = new java.lang.Double((String) fieldValue);
						}
					} catch (Exception e) {

					}
				}

				if (fieldName.contains("(Date)")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					fieldValue = new java.sql.Date(((java.util.Date) sdf.parse((String) fieldValue)).getTime());
				}
				if (fieldName.contains("(Timestamp)")) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					if (fieldName.contains("[")) {
						dateFormat = new SimpleDateFormat(
								fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]")));
					} else {
						// define list of patterns
						List<DateRegexPattern> listPatterns = new ArrayList<DateRegexPattern>();
						listPatterns.add(new DateRegexPattern("HH:mm:ss dd-MM-yyyy",
								"^[0-9]{2}:[0-9]{2}:[0-9]{2}\\s[0-9]{2}-[0-9]{2}-[0-9]{4}$")); // HH:mm:ss
																								// dd-MM-yyyy
						listPatterns.add(new DateRegexPattern("HH:mm:ss dd/MM/yyyy",
								"^[0-9]{2}:[0-9]{2}:[0-9]{2}\\s[0-9]{2}/[0-9]{2}/[0-9]{4}$")); // HH:mm:ss
																								// dd/MM/yyyy
						// Iterate to get missing pattern
						for (DateRegexPattern tmpDRP : listPatterns) {
							Pattern r = Pattern.compile(tmpDRP.getRegexPattern());
							Matcher m = r.matcher(fieldValue.toString());
							if (m.find()) {
								dateFormat = new SimpleDateFormat(tmpDRP.getDatePattern());
								break;
							}
						}
					}
					try {
						Date parsedDate = new java.sql.Date(dateFormat.parse((String) fieldValue).getTime());
						fieldValue = new java.sql.Timestamp(parsedDate.getTime());
					} catch (ParseException e) {

					}
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
				}
				if (listTmpCondition.isEmpty()) {
					listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
					tmpFieldName = fieldName;
				} else {
					// Check for the same field listFieldName
					if (tmpFieldName.equals(fieldName)) { // same field
						listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
						tmpG1 = tmpGO;
					} else {
						// listAllConditions.addAll(listTmpCondition); // add
						// all
						if (tmpG1.equals("1")) {
							listAllConditions
									.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.OR));
						} else {
							listAllConditions
									.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.AND));
						}
						tmpFieldName = fieldName;
						listTmpCondition = new ArrayList<EntityCondition>(); // reset
																				// list
						listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
					}
				}
			}
			// add last
			if (listTmpCondition.size() > 1) {
				if (tmpG1.equals("1")) {
					listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.OR));
				} else {
					listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.AND));
				}
			} else {
				listAllConditions.addAll(listTmpCondition);
			}
		}
		result.put("listSortFields", listSortFields);
		result.put("listAllConditions", listAllConditions);
		return result;
	}
	
	public static Map<String, Object> plugflagConfigPacking(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String productId = (String) context.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (UtilValidate.isNotEmpty(product)) {
				String quantityUomId = product.getString("quantityUomId");
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)));
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				delegator.storeByCondition("ConfigPacking", UtilMisc.toMap("largest", "N"), EntityCondition.makeCondition(conditions));
				List<GenericValue> listUomProduct = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conditions), null, null, null, false);
				
				if (UtilValidate.isNotEmpty(listUomProduct)) {
					Map<String, Object> uomMap = FastMap.newInstance();
					BigDecimal max = BigDecimal.ZERO;
					GenericValue biggestUom = null;
					for (GenericValue uom : listUomProduct) {
						String uomFromId = uom.getString("uomFromId");
						String uomToId = uom.getString("uomToId");
						BigDecimal quantityConvert = uom.getBigDecimal("quantityConvert");
						
						while (!uomToId.equals(quantityUomId)) {
							if (UtilValidate.isNotEmpty(uomMap) && uomMap.containsKey(uomToId)) {
								quantityConvert = ((BigDecimal) uomMap.get(uomToId)).multiply(quantityConvert);
								break;
							} 
							
							conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("uomFromId", uomToId)));
							List<GenericValue> configPackings = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conditions), null, null, null, false);
							conditions.remove(EntityCondition.makeCondition(UtilMisc.toMap("uomFromId", uomToId)));
							
							if (UtilValidate.isNotEmpty(configPackings)) {
								uomToId = configPackings.get(0).getString("uomToId");
								quantityConvert = quantityConvert.multiply(configPackings.get(0).getBigDecimal("quantityConvert"));
							} else {
								quantityConvert = BigDecimal.ZERO;
								break;
							}
						}
						
						uomMap.put(uomFromId, quantityConvert);
						
						if (quantityConvert.compareTo(max) > 0) {
							max = quantityConvert;
							biggestUom = uom;
						}
					}
					
					if (UtilValidate.isNotEmpty(biggestUom)) {
						biggestUom.set("largest", "Y");
						biggestUom.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> plugflagAllConfigPacking(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		String productCategoryId = (String) context.get("productCategoryId");
		try {
			iterator = delegator.find("Product", EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productCategoryId)
					, null, null, null, null);
			GenericValue product = null;
			while ((product = iterator.next()) != null) {
				String quantityUomId = product.getString("quantityUomId");
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", product.get("productId"))));
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				delegator.storeByCondition("ConfigPacking", UtilMisc.toMap("largest", "N"), EntityCondition.makeCondition(conditions));
				List<GenericValue> listUomProduct = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conditions), null, null, null, false);
				
				if (UtilValidate.isNotEmpty(listUomProduct)) {
					Map<String, Object> uomMap = FastMap.newInstance();
					BigDecimal max = BigDecimal.ZERO;
					GenericValue biggestUom = null;
					for (GenericValue uom : listUomProduct) {
						String uomFromId = uom.getString("uomFromId");
						String uomToId = uom.getString("uomToId");
						BigDecimal quantityConvert = uom.getBigDecimal("quantityConvert");
						
						while (!uomToId.equals(quantityUomId)) {
							if (UtilValidate.isNotEmpty(uomMap) && uomMap.containsKey(uomToId)) {
								quantityConvert = ((BigDecimal) uomMap.get(uomToId)).multiply(quantityConvert);
								break;
							} 
							
							conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("uomFromId", uomToId)));
							List<GenericValue> configPackings = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conditions), null, null, null, false);
							conditions.remove(EntityCondition.makeCondition(UtilMisc.toMap("uomFromId", uomToId)));
							
							if (UtilValidate.isNotEmpty(configPackings)) {
								uomToId = configPackings.get(0).getString("uomToId");
								quantityConvert = quantityConvert.multiply(configPackings.get(0).getBigDecimal("quantityConvert"));
							} else {
								quantityConvert = BigDecimal.ZERO;
								break;
							}
						}
						
						uomMap.put(uomFromId, quantityConvert);
						
						if (quantityConvert.compareTo(max) > 0) {
							max = quantityConvert;
							biggestUom = uom;
						}
					}
					
					if (UtilValidate.isNotEmpty(biggestUom)) {
						biggestUom.set("largest", "Y");
						biggestUom.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyAndPositionInCompany(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			
			String emplPositionTypeId = null;
			if (parameters.get("emplPositionTypeId") != null && parameters.get("emplPositionTypeId").length > 0){
				emplPositionTypeId = parameters.get("emplPositionTypeId")[0];
			}
			if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId));
			}
			
			EntityListIterator listIterator = null;
			
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, 
					delegator, "EmplPositionFulfillmentDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
					null, null, listSortFields, opts);
			
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}
}

class DateRegexPattern {
	String datePattern;
	String regexPattern;

	public DateRegexPattern(String datePattern, String regexPattern) {
		this.datePattern = datePattern;
		this.regexPattern = regexPattern;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public String getRegexPattern() {
		return regexPattern;
	}

	public void setRegexPattern(String regexPattern) {
		this.regexPattern = regexPattern;
	}

}

enum SqlOperator {
	CONTAINS, DOES_NOT_CONTAIN, NOT_EMPTY, EMPTY, EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, STARTS_WITH, ENDS_WITH, NULL, NOT_NULL
}