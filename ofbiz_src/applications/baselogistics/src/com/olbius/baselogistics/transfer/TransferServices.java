package com.olbius.baselogistics.transfer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.net.ntp.TimeStamp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
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

import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.util.JsonUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TransferServices {
	
	public static final String module = TransferServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceCommonEntity = "CommonEntityLabels";
	public static final String OrderEntityLabels = "OrderEntityLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTransfer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		Security security = ctx.getSecurity();
		EntityListIterator listTransfers = null;
		List<EntityCondition> listFacilityCond = new ArrayList<EntityCondition>();
        if (!security.hasPermission("LOGISTICS_ADMIN", userLogin)) {
        	if (!security.hasPermission("LOGISTICS_VIEW", userLogin)) {
        		if (!security.hasPermission("FACILITY_ADMIN", userLogin)) {
        			if (security.hasPermission("FACILITY_VIEW", userLogin)) {
    					listFacilityCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
        	    		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
        	    		tmpListCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER"));
        	    		tmpListCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper")));
        	    		listFacilityCond.add(EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.OR));
        			} else {
        				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NotHasPermission", (Locale)context.get("locale")));
        			}
        		}
        	}
        }
        
    	try {
    		List<GenericValue> listFacilities = delegator.findList("FacilityPartyFacility", EntityCondition.makeCondition(listFacilityCond), null, null, null, false);
    		mapCondition = new HashMap<String, String>();
        	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        	String parentTypeId = null;
        	if (parameters.get("parentTypeId") != null && parameters.get("parentTypeId").length > 0){
        		parentTypeId = (String)parameters.get("parentTypeId")[0];
        	}
        	if (parentTypeId != null && !"".equals(parentTypeId)){
        		mapCondition.put("parentTypeId", parentTypeId);
        		EntityCondition typeConditon = EntityCondition.makeCondition(mapCondition);
            	listAllConditions.add(typeConditon);
        	}
        	
        	mapCondition = new HashMap<String, String>();
        	String transferTypeId = null;
        	if (parameters.get("transferTypeId") != null && parameters.get("transferTypeId").length > 0){
        		transferTypeId = (String)parameters.get("transferTypeId")[0];
        	}
        	if (transferTypeId != null && !"".equals(transferTypeId)){
        		mapCondition.put("transferTypeId", transferTypeId);
        		EntityCondition typeConditon = EntityCondition.makeCondition(mapCondition);
            	listAllConditions.add(typeConditon);
        	}
        	List<String> listFacilityIds = new ArrayList<String>();
    		for (GenericValue fac : listFacilities){
				String facilityId = (String)fac.get("facilityId");
				listFacilityIds.add(facilityId);
    		}
        	EntityCondition originFacCond = EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, listFacilityIds);
        	EntityCondition destFacCond = EntityCondition.makeCondition("destFacilityId", EntityOperator.IN, listFacilityIds);
        	List<EntityCondition> listFacCond = UtilMisc.toList(originFacCond, destFacCond); 
        	EntityCondition facCond = EntityCondition.makeCondition(listFacCond, EntityOperator.OR);
        	listAllConditions.add(facCond);
        	
        	if (listSortFields.isEmpty()){
        		listSortFields.add("-transferId");
        	}
        	listTransfers = delegator.find("TransferHeaderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListTransfer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listTransfers);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createTransfer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String transferTypeId = (String)context.get("transferTypeId");
		String statusId = (String)context.get("statusId");
		String itemStatusId = (String)context.get("itemStatusId");
		if (UtilValidate.isEmpty(statusId)) statusId = "TRANSFER_CREATED";
		if (UtilValidate.isEmpty(itemStatusId)) itemStatusId = "TRANS_ITEM_CREATED";
		
		String originFacilityId = (String)context.get("originFacilityId");
		String destFacilityId = (String)context.get("destFacilityId");
		String transferDateStr = (String)context.get("transferDate");
		Timestamp transferDate = null;
		if (UtilValidate.isNotEmpty(transferDateStr)){
			transferDate = new Timestamp(Long.valueOf(transferDateStr));
		}
		String shipBeforeDateStr = (String)context.get("shipBeforeDate");
		Timestamp shipBeforeDate = null;
		if (UtilValidate.isNotEmpty(shipBeforeDateStr)){
			shipBeforeDate = new Timestamp(Long.valueOf(shipBeforeDateStr));
		}
		String shipAfterDateStr = (String)context.get("shipAfterDate");
		Timestamp shipAfterDate = null;
		if (UtilValidate.isNotEmpty(shipAfterDateStr)){
			shipAfterDate = new Timestamp(Long.valueOf(shipAfterDateStr));
		}
		if (UtilValidate.isEmpty(transferDate) && UtilValidate.isEmpty(shipAfterDateStr) && UtilValidate.isEmpty(shipBeforeDateStr)){
			return ServiceUtil.returnError("OLBIUS: not has date time to transfer were selected");
		}
		BigDecimal priority = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty((String)context.get("priority"))){
			priority = new BigDecimal((String)context.get("priority"));
		}
		String description = (String)context.get("description");
		String needsReservesInventory = (String)context.get("needsReservesInventory");
		String maySplit = (String)context.get("maySplit");
		String invoicePerShipment = (String)context.get("invoicePerShipment");
		
		// create transfer header
		GenericValue transfer = delegator.makeValue("TransferHeader");
		String transferId = delegator.getNextSeqId("TransferHeader");
		transfer.put("transferId", transferId);
		transfer.put("priority", priority);
		transfer.put("invoicePerShipment", invoicePerShipment);
		transfer.put("needsReservesInventory", needsReservesInventory);
		transfer.put("maySplit", maySplit);
		transfer.put("originFacilityId", originFacilityId);
		transfer.put("destFacilityId", destFacilityId);
		transfer.put("transferTypeId", transferTypeId);
		transfer.put("createdDate", UtilDateTime.nowTimestamp());
		transfer.put("transferDate", transferDate);
		transfer.put("shipBeforeDate", shipBeforeDate);
		transfer.put("shipAfterDate", shipAfterDate);
		transfer.put("statusId", statusId);
		transfer.put("createdByUserLogin", userLogin.getString("partyId"));
		transfer.put("lastModifyByUserLogin", userLogin.getString("partyId"));
		transfer.put("description", description);
		
		try {
			delegator.create(transfer);
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		
		// create transfer items
		List<Object> listItemTmp = (List<Object>)context.get("listProducts");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listProducts = new ArrayList<Map<String, String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					mapItem.put("productId", item.getString("productId"));
				}
				// quantity in case using amount it will be weight
				if (item.containsKey("quantity")){
					mapItem.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")){
					mapItem.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("weightUomId")){
					mapItem.put("weightUomId", item.getString("weightUomId"));
				}
				if (item.containsKey("expiredDate")){
					mapItem.put("expiredDate", item.getString("expiredDate"));
				}
				if (item.containsKey("fromExpiredDate")){
					mapItem.put("fromExpiredDate", item.getString("fromExpiredDate"));
				}
				if (item.containsKey("toExpiredDate")){
					mapItem.put("toExpiredDate", item.getString("toExpiredDate"));
				}
				if (item.containsKey("requirementId")){
					mapItem.put("requirementId", item.getString("requirementId"));
				}
				if (item.containsKey("reqItemSeqId")){
					mapItem.put("reqItemSeqId", item.getString("reqItemSeqId"));
				}
				listProducts.add(mapItem);
			}
    	} else {
    		listProducts = (List<Map<String, String>>)context.get("listProducts");
    	}
		
    	for(Map<String, String> item: listProducts){
    		String productId = item.get("productId");
    		String quantity = item.get("quantity");
    		String quantityUomId = item.get("quantityUomId");
    		String weightUomId = item.get("weightUomId");
    		Timestamp expiredDate = null;
    		Timestamp itemShipBeforeDate = null;
    		Timestamp itemShipAfterDate = null;
    		Timestamp toExpiredDate = null;
    		Timestamp fromExpiredDate = null;
    		String expDateTmp = item.get("expiredDate");
    		if (expDateTmp != null && !"".equals(expDateTmp) && !"null".equals(expDateTmp)){
    			expiredDate = new Timestamp(Long.valueOf(expDateTmp));
    		}
    		if (item.containsKey("toExpiredDate")){
				toExpiredDate = new Timestamp(new Long(item.get("toExpiredDate")));
			}
			if (item.containsKey("fromExpiredDate")){
				fromExpiredDate = new Timestamp(new Long(item.get("fromExpiredDate")));
			}
    		String itemShipBeforeDateTmp = item.get("itemShipBeforeDate");
    		if (itemShipBeforeDateTmp != null && !"".equals(itemShipBeforeDateTmp) && !"null".equals(itemShipBeforeDateTmp)){
		        itemShipBeforeDate = new Timestamp(Long.valueOf(itemShipBeforeDateTmp));
    		} else {
    			if ((String)context.get("shipBeforeDate") != null && !"".equals((String)context.get("shipBeforeDate")) && !"null".equals((String)context.get("shipBeforeDate"))){
    				itemShipBeforeDate = new Timestamp(Long.valueOf((String)context.get("shipBeforeDate")));
    			}
    		}
    		String itemShipAfterDateTmp = item.get("itemShipAfterDate");
    		if (itemShipAfterDateTmp != null && !"".equals(itemShipAfterDateTmp) && !"null".equals(itemShipAfterDateTmp)){
		        itemShipAfterDate = new Timestamp(Long.valueOf(itemShipAfterDateTmp));
    		} else {
    			if ((String)context.get("shipAfterDate") != null && !"".equals((String)context.get("shipAfterDate")) && !"null".equals((String)context.get("shipAfterDate"))){
    				itemShipAfterDate = new Timestamp(Long.valueOf((String)context.get("shipAfterDate")));
    			}
    		}
    		
    		GenericValue transferItem = delegator.makeValue("TransferItem");
    		transferItem.put("transferId", transferId);
    		delegator.setNextSubSeqId(transferItem, "transferItemSeqId", 5, 1);
    		transferItem.put("productId", productId);
    		transferItem.put("cancelQuantity", BigDecimal.ZERO);
    		transferItem.put("cancelAmount", BigDecimal.ZERO);
    		transferItem.put("quantityUomId", quantityUomId);
    		transferItem.put("expiredDate", expiredDate);
    		transferItem.put("fromExpiredDate", fromExpiredDate);
    		transferItem.put("toExpiredDate", toExpiredDate);
			transferItem.put("shipBeforeDate", itemShipBeforeDate);
			transferItem.put("shipAfterDate", itemShipAfterDate);
    		transferItem.put("statusId", itemStatusId);
    		try {
    			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
    			String requireAmount = objProduct.getString("requireAmount"); 
    			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
    				transferItem.put("amount", new BigDecimal(quantity));
    				transferItem.put("weightUomId", weightUomId);
    				transferItem.put("quantity", BigDecimal.ONE);
    			} else {
    				transferItem.put("quantity", new BigDecimal(quantity));
    			}
				delegator.create(transferItem);
				if (UtilValidate.isNotEmpty(item.get("requirementId")) && UtilValidate.isNotEmpty(item.get("reqItemSeqId"))){
					// Create requirement transfer relation
					GenericValue reqTransfer = delegator.makeValue("TransferRequirement");
					reqTransfer.put("requirementId", item.get("requirementId"));
					reqTransfer.put("reqItemSeqId", item.get("reqItemSeqId"));
					reqTransfer.put("transferId", transferId);
					reqTransfer.put("transferItemSeqId", transferItem.getString("transferItemSeqId"));
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						reqTransfer.put("quantity", BigDecimal.ONE);
						reqTransfer.put("amount", new BigDecimal(quantity));
						reqTransfer.put("weightUomId", weightUomId);
					} else {
						reqTransfer.put("quantity", new BigDecimal(quantity));
					}
					reqTransfer.put("quantityUomId", quantityUomId);
					delegator.create(reqTransfer);
				}
			} catch (GenericEntityException e) {
			    return ServiceUtil.returnError("OLBIUS: Create transfer item error");
			}
    	}
		// create transfer item ship group
    	// create one group for all transfer item, can split to many group for one transfer (when extend function)
    	String originContactMechId = (String)context.get("originContactMechId");
		String destContactMechId = (String)context.get("destContactMechId");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		String carrierPartyId = null;
		
		if (UtilValidate.isNotEmpty((String)context.get("carrierPartyId"))){
			carrierPartyId = (String)context.get("carrierPartyId");
		} else {
			List<GenericValue> listTmps = FastList.newInstance();
			try {
				listTmps = delegator.findList("PartyRelationship", EntityCondition.makeCondition("roleTypeIdTo", "LOG_DEPARTMENT"), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			listTmps = EntityUtil.filterByDate(listTmps);
			if (!listTmps.isEmpty()){
				carrierPartyId = listTmps.get(0).getString("partyIdTo");
			}
		}
 		GenericValue transferItemShipGroup = delegator.makeValue("TransferItemShipGroup");
		transferItemShipGroup.put("transferId", transferId);
		delegator.setNextSubSeqId(transferItemShipGroup, "shipGroupSeqId", 5, 1);
		transferItemShipGroup.put("originFacilityId", originFacilityId);
		transferItemShipGroup.put("destFacilityId", destFacilityId);
		transferItemShipGroup.put("originContactMechId", originContactMechId);
		transferItemShipGroup.put("destContactMechId", destContactMechId);
		transferItemShipGroup.put("shipmentMethodTypeId", shipmentMethodTypeId);
		transferItemShipGroup.put("carrierPartyId", carrierPartyId);
		transferItemShipGroup.put("carrierRoleTypeId", "CARRIER");
		transferItemShipGroup.put("shipAfterDate", shipAfterDate);
		transferItemShipGroup.put("shipBeforeDate", shipBeforeDate);
		try {
			delegator.create(transferItemShipGroup);
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		
		String shipGroupSeqId = (String)transferItemShipGroup.get("shipGroupSeqId");
		
		// create transfer item ship group assoc (current all item in one shipgroup)
		List<GenericValue> listTransferItems = new ArrayList<GenericValue>();
		try {
			listTransferItems = delegator.findList("TransferItem", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
			if (!listTransferItems.isEmpty()){
				for (GenericValue item : listTransferItems){
					GenericValue assoc = delegator.makeValue("TransferItemShipGroupAssoc");
					assoc.put("transferId", transferId);
					assoc.put("shipGroupSeqId", shipGroupSeqId);
					assoc.put("transferItemSeqId", (String)item.get("transferItemSeqId"));
					assoc.put("quantity", item.getBigDecimal("quantity"));
					assoc.put("cancelQuantity", item.getBigDecimal("cancelQuantity"));
					assoc.put("amount", item.getBigDecimal("amount"));
					assoc.put("cancelAmount", item.getBigDecimal("cancelAmount"));
					delegator.create(assoc);
				}
			}
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		
		// create transfer item ship group inventory item reserved
		if (UtilValidate.isNotEmpty(needsReservesInventory) && "Y".equals(needsReservesInventory)){
			try {
				List<GenericValue> listTransItemShipGrpAssoc = delegator.findList("TransferItemShipGroupAssoc", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
				if (!listTransItemShipGrpAssoc.isEmpty()){
					for (GenericValue assoc : listTransItemShipGrpAssoc){
						Map<String, Object> mapInv = FastMap.newInstance();
						GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", (String)assoc.get("transferItemSeqId")));
						if (transferItem != null){
							String productId = (String)transferItem.get("productId");
							mapInv.put("productId", productId);
							mapInv.put("facilityId", originFacilityId);
							mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							mapInv.put("userLogin", userLogin);
							mapInv.put("expireDate", transferItem.get("expiredDate"));
							GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
							if (product == null){
								return ServiceUtil.returnError("Product not found for TransferItem:" + transferId + "transferItemSeqId:" + transferItem.getString("transferItemSeqId"));
							}
							String baseUomId = product.getString("quantityUomId");
							if (baseUomId == null){
								return ServiceUtil.returnError("QuantityUomId not found for Product:" + productId);
							}
							String quantityUomId = transferItem.getString("quantityUomId");
							BigDecimal invQuantity = assoc.getBigDecimal("quantity");
							BigDecimal invAmount = assoc.getBigDecimal("amount");
							if (!baseUomId.equals(quantityUomId)){
								BigDecimal convert = BigDecimal.ONE;
								convert = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseUomId);
								if (convert.compareTo(BigDecimal.ONE) == 1) {
									invQuantity = invQuantity.multiply(convert);
								}
							}
					        mapInv = FastMap.newInstance();
                            mapInv.put("requireInventory", "N");
                            mapInv.put("facilityId", originFacilityId);
                            mapInv.put("productId", productId);
                            mapInv.put("transferId", transferId);
                            mapInv.put("expireDate", transferItem.get("expiredDate"));
                            mapInv.put("transferItemSeqId", transferItem.getString("transferItemSeqId"));
                            mapInv.put("shipGroupSeqId", shipGroupSeqId);
                            mapInv.put("quantity", invQuantity);
                            mapInv.put("amount", invAmount);
                            mapInv.put("reserveTransferEnumId", "INVRO_FIFO_EXP");
                            mapInv.put("userLogin", userLogin);
                            try {
                                dispatcher.runSync("reserveProductInventoryForTransfer", mapInv);
                                // FIXME get first to update amount
                                if (UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals(product.getString("requireAmount"))) {
                                	EntityCondition cond1 = EntityCondition.makeCondition("transferId",
    										EntityOperator.EQUALS, transferId);
                                    EntityCondition cond2 = EntityCondition.makeCondition("transferItemSeqId",
                                    		EntityOperator.EQUALS, (String)assoc.get("transferItemSeqId"));
                                    List<GenericValue> listReserves = delegator.findList("TransferItemShipGrpInvRes",
    										EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2)), null, null, null, false);
                                    if (!listReserves.isEmpty()) {
                                    	GenericValue x = listReserves.get(0);
                                    	x.set("amount", invAmount);
                                    	delegator.store(x);
                                    }
								}
                            } catch (GenericServiceException e) {
                                return ServiceUtil.returnError(e.getStackTrace().toString());
                            }
						}
					}
				}
			} catch (GenericEntityException e) {
			    return ServiceUtil.returnError(e.getStackTrace().toString());
			}
		}	
		
		BigDecimal grandTotal = BigDecimal.ZERO;
		try {
			grandTotal = TransferReadHepler.getTransferTotal(delegator, transferId);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling TransferReadHepler.getTransferTotal service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		transfer.put("grandTotal", grandTotal);
		try {
			delegator.store(transfer);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling store TransferHeader service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		
		result.put("transferId", transferId);
		Security security = ctx.getSecurity();
		String roleType = null;
		if (!com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOG_DELIVERY")) {
			roleType = "role.manager.specialist";
		} else {
			roleType = "role.storekeeper";
		}
		String messages = null;
		if ("TRANSFER_CREATED".equals(statusId)){
			messages = "HasBeenCreated";
		} else if ("TRANSFER_APPROVED".equals(statusId)){
			messages = "HasBeenApproved";
		} else if ("TRANSFER_EXPORTED".equals(statusId)){
			messages = "HasBeenExported";
		} else if ("TRANSFER_DELIVERED".equals(statusId)){
			messages = "HasBeenDelivered";
		} else if ("TRANSFER_COMPLETED".equals(statusId)){
			messages = "HasBeenCompleted";
		} else if ("TRANSFER_CANCELLED".equals(statusId)){
			messages = "HasBeenCancelled";
		}
        try {
			dispatcher.runSync("createNotifyTransfer", UtilMisc.toMap("transferId", transferId, "messages", messages, "roleTypeProperties", roleType, "userLogin", userLogin));
			if ("TRANSFER_CREATED".equals(statusId)){
				dispatcher.runSync("createNotifyTransfer", UtilMisc.toMap("transferId", transferId, "messages", messages, "roleTypeProperties", "roleTypeId.receiveMsg.transfer.approved", "userLogin", userLogin));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTransferItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String transferId = null;
    	if (parameters.get("transferId") != null && parameters.get("transferId").length > 0){
    		transferId = (String)parameters.get("transferId")[0];
    	}
    	GenericValue objTransferHeader = null;
    	if (UtilValidate.isNotEmpty(transferId)) {
			try {
				objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne TransferHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
    	}
    	if (UtilValidate.isEmpty(objTransferHeader)) {
			String errMsg = "OLBIUS: Fatal error: TransferHeader not found with Id:" + transferId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		mapCondition.put("transferId", transferId);
		if (!"TRANSFER_CANCELLED".equals(objTransferHeader.getString("statusId"))){
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("TRANS_ITEM_CANCELLED", "TRANS_ITEM_REJECTED")));
		}
    	
    	listAllConditions.add(EntityCondition.makeCondition("transferId", transferId));
    	List<GenericValue> listItems = new ArrayList<GenericValue>();
    	try {
    		listItems = delegator.findList("TransferItemAndProduct", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		if (!listItems.isEmpty()){
    			for (GenericValue item : listItems) {
					String transferItemSeqId = item.getString("transferItemSeqId");
					List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", transferId, "fromTransferItemSeqId", transferItemSeqId)), EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED")), EntityOperator.AND), null, null, null, false);
					String requireAmount = item.getString("requireAmount");
					BigDecimal quantityDelivered = BigDecimal.ZERO;
					BigDecimal quantityRemain = BigDecimal.ZERO;
					BigDecimal quantityShipping = BigDecimal.ZERO;
					BigDecimal quantityScheduled = BigDecimal.ZERO;
					BigDecimal quantity = item.getBigDecimal("quantity");
					BigDecimal amount = item.getBigDecimal("amount");
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						quantityRemain = amount;
					} else {
						quantityRemain = quantity;
					}
					String productId = item.getString("productId");
					boolean isWeight = ProductUtil.isWeightProduct(delegator, productId);
							
					if (!"TRANSFER_CANCELLED".equals(objTransferHeader.getString("statusId"))){
						for (GenericValue dlvItem : listDlvItems) {
							
							String statusId = dlvItem.getString("statusId");
							if (UtilValidate.isNotEmpty(statusId) && "DELI_ITEM_DELIVERED".equals(statusId)){
								if (isWeight) {
									quantityDelivered = quantityDelivered.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
									quantityRemain = quantityRemain.subtract(dlvItem.getBigDecimal("actualExportedQuantity"));
									quantityShipping = quantityShipping.add(dlvItem.getBigDecimal("actualExportedAmount").subtract(dlvItem.getBigDecimal("actualDeliveredAmount")));
								} else {
									quantityDelivered = quantityDelivered.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
									quantityRemain = quantityRemain.subtract(dlvItem.getBigDecimal("actualExportedQuantity"));
									quantityShipping = quantityShipping.add(dlvItem.getBigDecimal("actualExportedQuantity").subtract(dlvItem.getBigDecimal("actualDeliveredQuantity")));
								}
							} else if (UtilValidate.isNotEmpty(statusId) && "DELI_ITEM_EXPORTED".equals(statusId)){
								if (isWeight) {
									quantityShipping = quantityShipping.add(dlvItem.getBigDecimal("actualExportedAmount"));
									quantityRemain = quantityRemain.subtract(dlvItem.getBigDecimal("actualExportedAmount"));
								} else {
									quantityShipping = quantityShipping.add(dlvItem.getBigDecimal("actualExportedQuantity"));
									quantityRemain = quantityRemain.subtract(dlvItem.getBigDecimal("actualExportedQuantity"));
								}
							} else if (UtilValidate.isNotEmpty(statusId) && ("DELI_ITEM_CREATED".equals(statusId) || "DELI_ITEM_APPROVED".equals(statusId))){
								if (isWeight) {
									quantityScheduled = quantityScheduled.add(dlvItem.getBigDecimal("amount"));
								} else {
									quantityScheduled = quantityScheduled.add(dlvItem.getBigDecimal("quantity"));
								}
							}
						}
					}
					item.put("quantityDelivered", quantityDelivered);
					if ("TRANSFER_COMPLETED".equals(objTransferHeader.getString("statusId"))){
						item.put("quantityRemain", BigDecimal.ZERO);
						item.put("quantityShipping", BigDecimal.ZERO);
					} else {
						item.put("quantityRemain", quantityRemain);
						item.put("quantityShipping", quantityShipping);
					}
					
					item.put("quantityScheduled", quantityScheduled);
				}
    		}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListTransferItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listItems);
    	return successResult;
	}
	
	public static Map<String,Object> getInvByTransferAndDlv(DispatchContext ctx, Map<String, Object> context){
	    Delegator delegator = ctx.getDelegator();
	    String deliveryId = (String)context.get("deliveryId");
	    String transferId = (String)context.get("transferId");
	    String facilityId = (String)context.get("facilityId");
	    List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	    List<GenericValue> listData = null;
	    if(facilityId!= null && !"".equals(facilityId)){
            listCond.add(EntityCondition.makeCondition("originFacilityId", facilityId));
        }
        if(deliveryId!= null && !"".equals(deliveryId)){
            listCond.add(EntityCondition.makeCondition("deliveryId", deliveryId));
        }
        if(transferId != null && !"".equals(transferId)){
            listCond.add(EntityCondition.makeCondition("transferId", transferId));
        }
	    try{
            listData = delegator.findList("DeliveryInvTransferItem", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, UtilMisc.toList("-expireDate"), null, false);
	    } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getInvByTransferAndDlv:" + e.toString());
        }
	    Map<String, Object> result = FastMap.newInstance();
	    result.put("listData", listData);
	    return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTransferDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.get("transferId") != null && parameters.get("transferId").length >= 1){
    		String transferId = (String)parameters.get("transferId")[0];
    		if (transferId != null && !"".equals(transferId)){
        		mapCondition.put("transferId", transferId);
        	}
    	}

    	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
    		String statusId = (String)parameters.get("statusId")[0];
        	if (statusId != null && !"".equals(statusId)){
        		mapCondition.put("statusId", statusId);
        	}
    	}
    	String deliveryId = null;
    	if (parameters.get("deliveryId") != null && parameters.get("deliveryId").length >= 1){
    		deliveryId = (String)parameters.get("deliveryId")[0];
    	}
    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	// condition for transfer only
    	listAllConditions.add(EntityCondition.makeCondition("transferId", EntityJoinOperator.NOT_EQUAL, null));
    	List<GenericValue> listDeliveries = new ArrayList<GenericValue>();
    	/*	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = ctx.getSecurity();
    	boolean transferAdmin = com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOG_TRANSFER");
    	if (!transferAdmin){
	    	List<String> listFacilityIds = FastList.newInstance();
			try {
				listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling FacilityUtil.getFacilityManages: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "HasErrorWhenProcessing", (Locale)context.get("locale")));
			}
			if (!listFacilityIds.isEmpty()){
				EntityCondition facilityCondOrigin = EntityCondition.makeCondition("originFacilityId", EntityJoinOperator.IN, listFacilityIds);
	    		EntityCondition facilityCondDest = EntityCondition.makeCondition("destFacilityId", EntityJoinOperator.IN, listFacilityIds);
	    		List<EntityCondition> listFacilityCond = new ArrayList<EntityCondition>();
	    		listFacilityCond.add(facilityCondOrigin);
	    		listFacilityCond.add(facilityCondDest);
	    		EntityCondition facilityCond = EntityCondition.makeCondition(listFacilityCond, EntityJoinOperator.OR);
	    		listAllConditions.add(facilityCond);
			} else {
				successResult.put("listIterator", listDeliveries);
		    	return successResult;
			}
    	}*/
		if (listSortFields.isEmpty()){
			listSortFields.add("-deliveryId");
		}
		try {
			listDeliveries = delegator.findList("DeliveryDetail", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling delegator.findList DeliveryDetail: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "HasErrorWhenProcessing", (Locale)context.get("locale")));
		}
		GenericValue deliveryToSelect = null;
		if (deliveryId != null && !"".equals(deliveryId)){
			try {
				deliveryToSelect = delegator.findOne("DeliveryDetail", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling delegator.findOne DeliveryDetail: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "HasErrorWhenProcessing", (Locale)context.get("locale")));
			}
		}
		if (!listDeliveries.isEmpty() && deliveryToSelect != null){
			listDeliveries.set(0,deliveryToSelect);
		}
    	successResult.put("listIterator", listDeliveries);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> getListTransferItemDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String transferId = parameters.get("transferId")[0];
       	
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("transferId", transferId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	
       	List<GenericValue> transferItems = new ArrayList<GenericValue>();
       	List<GenericValue> deliveryItems = new ArrayList<GenericValue>();
       	try {
       		transferItems = delegator.findList("TransferItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListTransferItemDelivery service: " + e.toString();
   			Debug.logError(e, errMsg, module);
		} 
       	List<GenericValue> listTransfered = new ArrayList<GenericValue>();
       	if (!transferItems.isEmpty()){
       		for(GenericValue item: transferItems){
       			try {
       				String productId = item.getString("productId");
           			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
           			String requireAmount = objProduct.getString("requireAmount");
           			BigDecimal quantity = item.getBigDecimal("quantity");
           			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
           				quantity = item.getBigDecimal("amount");
   					}
           			item.put("quantityToDelivery", quantity);
       				List<EntityCondition> condList = new ArrayList<EntityCondition>();
       				EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityComparisonOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
	           	    EntityCondition cond1 = EntityCondition.makeCondition("fromTransferId", EntityComparisonOperator.EQUALS, item.getString("transferId"));
	           	    EntityCondition cond2 = EntityCondition.makeCondition("fromTransferItemSeqId", EntityComparisonOperator.EQUALS, item.getString("transferItemSeqId"));
	           	    condList.add(EntityCondition.makeCondition(EntityJoinOperator.AND, cond1, cond2, statusCond));
       				deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condList), null, null, null, false);
       				if (!deliveryItems.isEmpty()){
       					BigDecimal totalTransferQty = BigDecimal.ZERO;
       					BigDecimal itemQuantity = item.getBigDecimal("quantity");
       					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
       						itemQuantity = item.getBigDecimal("amount");
       					}
       					
	       				for(GenericValue dlvItem: deliveryItems){
	       					if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId")) || "DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
	       						BigDecimal itemQty = dlvItem.getBigDecimal("actualExportedQuantity");
	       						if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
	       							itemQty = dlvItem.getBigDecimal("actualExportedAmount");
								} 
								totalTransferQty = totalTransferQty.add(itemQty);
							}  else if (!"DELI_ITEM_CANCELLED".equals(dlvItem.getString("statusId"))){
								BigDecimal itemQty = dlvItem.getBigDecimal("quantity");
								if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
									itemQty = dlvItem.getBigDecimal("amount");
								}
								totalTransferQty = totalTransferQty.add(itemQty);
							}
	       		       	}
	       				if (totalTransferQty.compareTo(itemQuantity) >= 0){
	       					listTransfered.add(item);
	       				} else {
	       					item.put("quantity", itemQuantity.subtract(totalTransferQty));
	       					item.put("quantityToDelivery", itemQuantity.subtract(totalTransferQty));
	       				}
       				}
       			} catch (GenericEntityException e1) {
       				e1.printStackTrace();
       			} 
       		}
       		if (!listTransfered.isEmpty()){
       			transferItems.removeAll(listTransfered);
       		}
       	}
       	
       	successResult.put("listIterator", transferItems);
       	return successResult;
   }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getTransferItemToDelivery(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String transferId = (String) context.get("transferId");
		LocalDispatcher dispathcher = ctx.getDispatcher();
	    Map<String, Object> mapFac = FastMap.newInstance();
		List<GenericValue> listAllConditions = new ArrayList<GenericValue>();
		List<GenericValue> listSortFields = new ArrayList<GenericValue>();
		EntityFindOptions opts = new EntityFindOptions();
		Map<String, String[]> parameters = FastMap.newInstance();
		String[] listTransferId = new String[1];
		listTransferId[0] = transferId;
		parameters.put("transferId", listTransferId);
		mapFac.put("listAllConditions", listAllConditions);
		mapFac.put("listSortFields", listSortFields);
		mapFac.put("opts", opts);
		mapFac.put("parameters", parameters);
		mapFac.put("userLogin", (GenericValue)context.get("userLogin"));
		List<GenericValue> listTransferItems = new ArrayList<GenericValue>();
		Map<String, Object> map = FastMap.newInstance();
		try {
			map = dispathcher.runSync("getListTransferItemDelivery", mapFac);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		listTransferItems = (List)map.get("listIterator");
		result.put("listTransferItems", listTransferItems); 
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createTransferDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	//Get Parameters
    	String transferId = (String)context.get("transferId");
    	GenericValue transfer = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
    	
    	String originFacilityId = (String)transfer.get("originFacilityId");
    	String destFacilityId = (String)transfer.get("destFacilityId");
    	GenericValue originFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", originFacilityId));
    	GenericValue destFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", destFacilityId));
    	
    	String deliveryId = delegator.getNextSeqId("Delivery");
    	
    	String partyIdFrom = (String)originFacility.get("ownerPartyId");
    	String partyIdTo = (String)destFacility.get("ownerPartyId");
		String statusId = null;
		String itemStatusId = null;
		statusId = "DLV_CREATED";
		itemStatusId = "DELI_ITEM_CREATED";
    	List<GenericValue> transferItemShipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
    	GenericValue shipGroup = null;
    	if (!transferItemShipGroups.isEmpty()){
    		shipGroup = transferItemShipGroups.get(0);
    	} 
    	String destContactMechId = null;
    	String originContactMechId = null;
    	if (shipGroup != null){
    		destContactMechId = (String)shipGroup.get("destContactMechId");
    		originContactMechId = (String)shipGroup.get("originContactMechId");
    	} else {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NotFoundShipInfoOfTransfer", (Locale)context.get("locale")));
    	}
    	String deliveryTypeId = (String)context.get("deliveryTypeId");
    	List<Map<String, String>> listTransferItems = (List<Map<String, String>>)context.get("listTransferItems");
    	
    	//Make Delivery
    	GenericValue delivery = delegator.makeValue("Delivery");
    	Timestamp deliveryDate = UtilDateTime.nowTimestamp();
    	if (UtilValidate.isNotEmpty(context.get("deliveryDate"))) {
    		deliveryDate = (Timestamp)context.get("deliveryDate");
		} else if (UtilValidate.isNotEmpty(transfer.get("transferDate"))){
			deliveryDate = transfer.getTimestamp("transferDate");
		} else if (UtilValidate.isNotEmpty(transfer.get("transferDate"))) {
			deliveryDate = transfer.getTimestamp("transferDate");
		}
    	Timestamp estimatedStartDate = (Timestamp)(context.get("estimatedStartDate"));
    	Timestamp estimatedArrivalDate = (Timestamp)(context.get("estimatedArrivalDate"));
    	String defaultWeightUomId = (String)context.get("defaultWeightUomId");
    	delivery.put("deliveryDate", deliveryDate);
    	delivery.put("estimatedStartDate", estimatedStartDate);
    	delivery.put("estimatedArrivalDate", estimatedArrivalDate);
    	delivery.put("defaultWeightUomId", defaultWeightUomId);
    	Timestamp createDate = UtilDateTime.nowTimestamp();
    	delivery.put("createDate", createDate);
    	delivery.put("partyIdFrom", partyIdFrom);
    	delivery.put("partyIdTo", partyIdTo);
    	delivery.put("deliveryTypeId", deliveryTypeId);
    	delivery.put("originContactMechId", originContactMechId);
    	delivery.put("originFacilityId", originFacilityId);
    	delivery.put("destFacilityId", destFacilityId);
    	delivery.put("destContactMechId", destContactMechId);
    	delivery.put("deliveryId", deliveryId);
    	delivery.put("transferId", transferId);
    	delivery.put("statusId", statusId);
    	delivery.create();
    	
    	//Make Delivery Item
    	if (!listTransferItems.isEmpty()){
    		for (Map<String, String> item : listTransferItems){
    			String transferItemSeqId = (String)item.get("transferItemSeqId");
    			GenericValue objTransferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
    			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", objTransferItem.getString("productId")));
    			String requireAmount = objProduct.getString("requireAmount");
    			
    			GenericValue deliveryItem = delegator.makeValue("DeliveryItem");
    			deliveryItem.put("deliveryId", deliveryId);
    			deliveryItem.put("quantity", BigDecimal.valueOf(Double.parseDouble(item.get("quantity"))));
    			deliveryItem.put("actualExportedQuantity", null);
    			deliveryItem.put("actualDeliveredQuantity", null);
    			deliveryItem.put("actualExportedAmount", null);
    			deliveryItem.put("actualDeliveredAmount", null);
    			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
    				deliveryItem.put("amount", BigDecimal.valueOf(Double.parseDouble(item.get("quantity"))));
    				deliveryItem.put("quantity", BigDecimal.ONE);
				}
    			
		        delegator.setNextSubSeqId(deliveryItem, "deliveryItemSeqId", 5, 1);
    			deliveryItem.put("fromTransferItemSeqId", transferItemSeqId);
    			deliveryItem.put("fromTransferId", transferId);
    			deliveryItem.put("statusId", itemStatusId);
    			deliveryItem.create();
    		}
    	} else {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NoProductSelected", (Locale)context.get("locale")));
    	}
    	
    	//Create DeliveryStatus
		String userLoginId = (String)userLogin.get("userLoginId");
		GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
		deliveryStatus.put("deliveryStatusId", delegator.getNextSeqId("DeliveryStatus"));
		deliveryStatus.put("deliveryId", deliveryId);
		deliveryStatus.put("statusId", statusId);
		deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
		deliveryStatus.put("statusUserLogin", userLoginId);
		delegator.createOrStore(deliveryStatus);
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
//		String roleType = null;
//		Boolean isAdmin = false;
//		if (security.hasPermission("DELIVERY_ADMIN", userLogin)) {
//			roleType = "role.manager.specialist";
//			isAdmin = true;
//		} else {
//			isAdmin = false; 
//			roleType = "role.storekeeper";
//		}
//		try {
//			dispatcher.runSync("createNotifyDelivery", UtilMisc.toMap("deliveryId", deliveryId, "messages", "HasBeenCreated", "roleTypeProperties", roleType, "userLogin", userLogin));
//		} catch (GenericServiceException e) {
//			e.printStackTrace();
//		}
		if (security.hasPermission("DELIVERY_ADMIN", userLogin)) {
			try {
				dispatcher.runSync("updateDeliveryStatus", UtilMisc.toMap("deliveryId", deliveryId, "newStatusId", "DLV_APPROVED", "setItemStatus", "Y", "newItemStatus", "DELI_ITEM_APPROVED", "userLogin", (GenericValue)context.get("userLogin")));
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: updateDeliveryStatus after create delivery error");
			}
		}
		
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
	public static Map<String, Object> checkDeliveryTransferStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String transferId = (String) context.get("transferId");
        // get the transfer header
        GenericValue transferHeader = null;
        try {
        	transferHeader = delegator.findOne("TransferHeader", UtilMisc.toMap("transferId", transferId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get TransferHeader record", module);
        }
        if (transferHeader == null) {
            Debug.logError("TransferHeader came back as null", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "TransferCannotBeFound", (Locale)context.get("locale")));
        }

        // get the transfer items and deliveries of transfer
        List<GenericValue> transferDeliveries = null;
        List<GenericValue> transferItems = null;
        try {
        	transferItems =  delegator.findByAnd("TransferItem", UtilMisc.toMap("transferId", transferId), null, false);
        	transferDeliveries = delegator.findByAnd("Delivery", UtilMisc.toMap("transferId", transferId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get Delivery by Transfer", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ProblemGettingDeliveryRecords", locale));
        }

        String transferHeaderStatusId = transferHeader.getString("statusId");
        
        String newStatus = null;
        // check new status of transfer
        if (!transferDeliveries.isEmpty() && !transferItems.isEmpty()) {
        	boolean test = true;
        	for (GenericValue item : transferItems){
        		BigDecimal itemQuantity = item.getBigDecimal("quantity");
        		String quantityUomId = item.getString("quantityUomId");
        		try {
        			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.getString("productId")));
            		String baseQuantityUomId = objProduct.getString("quantityUomId");
            		if (UtilValidate.isNotEmpty(quantityUomId) && !quantityUomId.equals(baseQuantityUomId)) {
            			BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, item.getString("productId"), quantityUomId, baseQuantityUomId);
            			itemQuantity = itemQuantity.multiply(convert);
					}
					List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", item.getString("transferId"), "fromTransferItemSeqId", item.getString("transferItemSeqId"))), null, null, null, false);
					if (!listDlvItems.isEmpty()){
						BigDecimal totalQuantityCreated = BigDecimal.ZERO;
						for (GenericValue dlvItem : listDlvItems){
							totalQuantityCreated = totalQuantityCreated.add(dlvItem.getBigDecimal("quantity"));
						}
						if (itemQuantity.compareTo(totalQuantityCreated) > 0){
							test = false;
							break; 
						}
					} else {
						test = false;
					}
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("get deliveryItem error!");
				}
        	}
        	boolean allCanceled = true;
            boolean allDelivered = true;
            boolean allApproved = true;
            boolean allExported = true;
            
        	for (GenericValue item : transferDeliveries) {
                String statusId = item.getString("statusId");
                //Debug.logInfo("Item Status: " + statusId, module);
                if (!"DLV_APPROVED".equals(statusId)) {
                    //Debug.logInfo("Not set to cancel", module);
                	allApproved = false;
                    if (!"DLV_EXPORTED".equals(statusId)) {
                        //Debug.logInfo("Not set to complete", module);
                    	allExported = false;
                        if (!"DLV_DELIVERED".equals(statusId)) {
                        	allDelivered = false;
                        	if (!"DLV_CANCELLED".equals(statusId)) {
	                            //Debug.logInfo("Not set to approve", module);
                        		allCanceled = false;
                            	break;
                            }
                        }
                    }
                }
            }
        	if (allApproved && test){
				newStatus = "TRANSFER_APPROVED";
			} else if (allExported && test){
				newStatus = "TRANSFER_EXPORTED";
			} else if (allDelivered && test){
				newStatus = "TRANSFER_DELIVERED";
			} else if (allCanceled && test){
				newStatus = "TRANSFER_CANCELLED";
			}   
            if (newStatus != null && !newStatus.equals(transferHeaderStatusId)) {
            	try {
	            	Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", transferHeader.getString("statusId"), "statusIdTo", newStatus);
	                GenericValue statusChange;
					try {
						statusChange = delegator.findOne("StatusValidChange", statusFields, true);
						if (statusChange != null) {
		                	Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("transferId", transferId, "statusId", newStatus, "userLogin", userLogin);
		                    dispatcher.runSync("changeTransferStatus", serviceContext);
		                }
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: changeTransferStatus error!");
					}
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeTransferStatus service", module);
                    return ServiceUtil.returnError("OLBIUS: changeTransferStatus error!");
                }
                
            }
        }

        return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> updateDeliveryByShipment(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String shipmentId = (String)context.get("shipmentId");
        GenericValue shipment = null;
        try {
        	shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
			if (shipment == null){
				return ServiceUtil.returnError("Shipment not found");
			}
			String shipmentStatusId = shipment.getString("statusId");
			if (!"SHIPMENT_INPUT".equals(shipmentStatusId) && !"SHIPMENT_SCHEDULED".equals(shipmentStatusId) && !"SHIPMENT_CANCELLED".equals(shipmentStatusId)){
				List<GenericValue> listDeliveries = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
				if (!listDeliveries.isEmpty()){
					for (GenericValue delivery : listDeliveries){
						String deliveryStatusId = (String)delivery.get("statusId");
						String newDeliveryStatusId = null;
						if ("SHIPMENT_PICKED".equals(shipmentStatusId) || "SHIPMENT_PACKED".equals(shipmentStatusId) || "SHIPMENT_SHIPPED".equals(shipmentStatusId)){
							newDeliveryStatusId = "DLV_EXPORTED";
						} else if ("SHIPMENT_DELIVERED".equals(shipmentStatusId)){
							newDeliveryStatusId = "DLV_DELIVERED";
						}
						if (newDeliveryStatusId != null && !newDeliveryStatusId.equals(deliveryStatusId)){
							List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", delivery.get("deliveryId"))), null, null, null, false);
							if (!deliveryItems.isEmpty()){
								for (GenericValue item : deliveryItems){
									if ("DLV_EXPORTED".equals(newDeliveryStatusId)){
										item.put("statusId", "DELI_ITEM_EXPORTED");
										item.put("actualExportedQuantity", item.getBigDecimal("quantity"));
										item.store();
									} else if ("DLV_DELIVERED".equals(newDeliveryStatusId)){
										item.store();
										item.put("statusId", "DELI_ITEM_DELIVERED");
										item.put("actualDeliveredQuantity", item.getBigDecimal("actualExportedQuantity"));
									}
								}
							}
							delivery.put("statusId", newDeliveryStatusId);
							delivery.store();
						}
					}
				}
			}
        } catch(GenericEntityException e){
        	e.printStackTrace();
        }
        return result;
	}
	
	public static Map<String, Object> setTransferStatus(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String transferId = (String) context.get("transferId");
        String statusId = (String) context.get("statusId");
        String newItemStatus = (String) context.get("newItemStatus");
        String setItemStatus = (String) context.get("setItemStatus");
        String changeReason = (String) context.get("changeReason");
        String noteInfo = (String)context.get("noteInfo");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        GenericValue transferHeader  = null;
        try {
            transferHeader = delegator.findOne("TransferHeader", UtilMisc.toMap("transferId", transferId), false);

            if (transferHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "TransferCannotBeFound", locale));
            }

            if (transferHeader.getString("statusId").equals(statusId)) {
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CannotChangeStatusWithTheSameStatusId", locale));
            }
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", transferHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CannotChangeBetweenTwoStatus", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
            	e.printStackTrace();
            }

            // update the current status
            transferHeader.set("statusId", statusId);

            // now create a status change
            GenericValue transferStatus = delegator.makeValue("TransferStatus");
            transferStatus.put("transferStatusId", delegator.getNextSeqId("TransferStatus"));
            transferStatus.put("statusId", statusId);
            transferStatus.put("transferId", transferId);
            transferStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            transferStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            transferStatus.put("changeReason", changeReason);

            transferHeader.store();
            transferStatus.create();
            
            if (UtilValidate.isNotEmpty(noteInfo)){
    			try {
					Map<String, Object> map = dispatcher.runSync("createNote", UtilMisc.toMap("userLogin", userLogin, "note", noteInfo, "partyId", userLogin.getString("partyId")));
					String noteId = (String)map.get("noteId");
					try {
						dispatcher.runSync("createTransferHeaderNote", UtilMisc.toMap("userLogin", userLogin, "transferId", transferId, "noteId", noteId));
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: runsync service createTransferHeaderNote error!");
					}
				} catch (GenericServiceException e){
					return ServiceUtil.returnError("OLBIUS: runsync service createNote error!");
				}
    		}

        } catch (GenericEntityException e) {
        	e.printStackTrace();
        }
        
        if (UtilValidate.isNotEmpty(setItemStatus) && "Y".equals(setItemStatus)){
        	List<GenericValue> listTransferItems = delegator.findList("TransferItem", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
        	for (GenericValue item : listTransferItems) {
				Map<String, Object> mapInput = UtilMisc.toMap("transferId", transferId, "transferItemSeqId", item.getString("transferItemSeqId"), "statusId", newItemStatus, "userLogin", userLogin);
				try {
					dispatcher.runSync("changeTransferItemStatus", mapInput);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: update transfer item status error!");
				}
			}
        }
        
//        if ("TRANSFER_APPROVED".equals(statusId)) {
//            try {
//				dispatcher.runSync("reserveInventoryForTransfer", UtilMisc.toMap("userLogin", userLogin,"transferId", transferId, "facilityId", (String)transferHeader.get("originFacilityId")));
//			} catch (GenericServiceException e) {
//				e.printStackTrace();
//			}
//        }
        
        String messages = null;
		if ("TRANSFER_CREATED".equals(statusId)){
			messages = "HasBeenCreated";
		} else if ("TRANSFER_APPROVED".equals(statusId)){
			messages = "HasBeenApproved";
		} else if ("TRANSFER_EXPORTED".equals(statusId)){
			messages = "HasBeenExported";
		} else if ("TRANSFER_DELIVERED".equals(statusId)){
			messages = "HasBeenDelivered";
		} else if ("TRANSFER_COMPLETED".equals(statusId)){
			messages = "HasBeenCompleted";
		} else if ("TRANSFER_CANCELLED".equals(statusId)){
			messages = "HasBeenCancelled";
		} else if ("TRANSFER_REJECTED".equals(statusId)){
			messages = "HasBeenRejected";
		}
		
        Security security = ctx.getSecurity();
        String roleType1 = "role.manager.specialist";
        String roleType2 = "role.storekeeper";
        if (com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "ACC_TRANSFER")) {
        	try {
				dispatcher.runSync("createNotifyTransfer", UtilMisc.toMap("transferId", transferId, "messages", messages, "roleTypeProperties", roleType1, "userLogin", userLogin));
				dispatcher.runSync("createNotifyTransfer", UtilMisc.toMap("transferId", transferId, "messages", messages, "roleTypeProperties", roleType2, "userLogin", userLogin));
        	} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		} else {
			String roleType = null;
			if (!com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOG_DELIVERY")) {
				roleType = roleType1;
			} else {
				roleType = roleType2;
			}
			
	        try {
				dispatcher.runSync("createNotifyTransfer", UtilMisc.toMap("transferId", transferId, "messages", messages, "roleTypeProperties", roleType, "userLogin", userLogin));
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
        return successResult;
    }
	
	public static Map<String, Object> updateProductCostForTransferShipmentFromItemIssuance(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
        Delegator delegator = ctx.getDelegator();
        String transItemIssuanceId = (String) context.get("transItemIssuanceId");
        GenericValue tranferItemIssuance = delegator.findOne("TransferItemIssuance", false, UtilMisc.toMap("transItemIssuanceId", transItemIssuanceId));
        if (UtilValidate.isNotEmpty(tranferItemIssuance)){
        	GenericValue inventoryItem = tranferItemIssuance.getRelatedOne("InventoryItem", false);
        	GenericValue shipmentItem = tranferItemIssuance.getRelatedOne("ShipmentItem", false);
        	 if (UtilValidate.isNotEmpty(shipmentItem)){
        		 shipmentItem.put("unitCost", inventoryItem.getBigDecimal("unitCost"));
        		 shipmentItem.put("purCost", inventoryItem.getBigDecimal("purCost"));
        		 shipmentItem.store();
        	 }
        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        return successResult;
	}
	
	public static Map<String, Object> setTransferItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String transferId = (String) context.get("transferId");
        String transferItemSeqId = (String) context.get("transferItemSeqId");
//        String fromStatusId = (String) context.get("fromStatusId");
        String statusId = (String) context.get("statusId");
        Timestamp statusDateTime = (Timestamp) context.get("statusDateTime");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order

        GenericValue transferItem = null;
        try {
        	transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "CannotGetTransferItemEntity",locale) + e.getMessage());
        }

        if (!transferItem.getString("statusId").equals(statusId)) {
        	try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", transferItem.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange != null) {
                	transferItem.set("statusId", statusId);
                    transferItem.store();
                    if (statusDateTime == null) {
                        statusDateTime = UtilDateTime.nowTimestamp();
                    }
                    // now create a status change
                    GenericValue transferStatus = delegator.makeValue("TransferStatus");
                    transferStatus.put("transferStatusId", delegator.getNextSeqId("TransferStatus"));
                    transferStatus.put("statusId", statusId);
                    transferStatus.put("transferId", transferId);
                    transferStatus.put("transferItemSeqId", transferItem.getString("transferItemSeqId"));
                    transferStatus.put("statusDatetime", statusDateTime);
                    transferStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
                    delegator.createOrStore(transferStatus);
                } else {
                	return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "CouldNotChangeItemStatus",locale));
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "CouldNotChangeItemStatus",locale) + e.getMessage());
            }
        }
        // cancel reserves
        if (!"TRANS_ITEM_APPROVED".equals(transferItem.getString("statusId")) && !"TRANS_ITEM_CREATED".equals(transferItem.getString("statusId"))) {
        	List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("transferId", transferId));
			conds.add(EntityCondition.makeCondition("transferItemSeqId", transferItemSeqId));
        	List<GenericValue> listTransferItemShipGrpInvRes = FastList.newInstance();
			try {
				listTransferItemShipGrpInvRes = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList TransferItemShipGrpInvRes: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listTransferItemShipGrpInvRes.isEmpty()){
				for (GenericValue item : listTransferItemShipGrpInvRes) {
					String inventoryItemId = item.getString("inventoryItemId");
					BigDecimal quantity = item.getBigDecimal("quantity");
					
					GenericValue tmpInvDetail = delegator.makeValue("InventoryItemDetail");
                    tmpInvDetail.set("inventoryItemId", inventoryItemId);
                    tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
                    tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
                    tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
                    tmpInvDetail.set("availableToPromiseDiff", quantity.negate());
                    tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
                    try {
						tmpInvDetail.create();
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when create InventoryItemDetail: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
				try {
					delegator.removeAll(listTransferItemShipGrpInvRes);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when removeAll InventoryItemDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
        }
        return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> checkTransferItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String transferId = (String) context.get("transferId");
        // get the transfer header
        GenericValue transferHeader = null;
        try {
        	transferHeader = delegator.findOne("TransferHeader", UtilMisc.toMap("transferId", transferId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get TransferHeader record", module);
        }
        if (transferHeader == null) {
            Debug.logError("TransferHeader came back as null", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "TransferCannotBeFound", (Locale)context.get("locale")));
        }

        // get the transfer items
        List<GenericValue> transferItems = null;
        try {
        	transferItems = delegator.findByAnd("TransferItem", UtilMisc.toMap("transferId", transferId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get TransferItem records", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ProblemGettingTransferItemRecords", locale));
        }

        String transferHeaderStatusId = transferHeader.getString("statusId");

        boolean allCanceled = true;
        boolean allCompleted = true;
        boolean allApproved = true;
        boolean allExported = true;
//        boolean isApartExported = true;
        boolean allDelivered = true;
//        boolean isApartDelivered = true;
        if (transferItems != null) {
            for (GenericValue item : transferItems) {
                String statusId = item.getString("statusId");
                if (!"TRANS_ITEM_CANCELLED".equals(statusId)) {
                    allCanceled = false;
                    if (!"TRANS_ITEM_COMPLETED".equals(statusId)) {
                        allCompleted = false;
                        if (!"TRANS_ITEM_DELIVERED".equals(statusId)) {
                        	allDelivered = false;
                            if (!"TRANS_ITEM_EXPORTED".equals(statusId)) {
                            	allExported = false;
                            	if (!"TRANS_ITEM_APPROVED".equals(statusId)) {
                            		allApproved = false;
                            		break;
                                }
                            }
                        }
                    }
                }
            }

            // find the next status to set to (if any)
            
            String newStatus = null;
            if (allCanceled) {
                newStatus = "TRANSFER_CANCELLED";
            } else if (allCompleted) {
                newStatus = "TRANSFER_COMPLETED";
            } else if (allDelivered){
            	newStatus = "TRANSFER_DELIVERED";
            } else if (allExported){
            	newStatus = "TRANSFER_EXPORTED";
            } else if (allApproved) {
            	newStatus = "TRANSFER_APPROVED";
            }  

            // now set the new transfer status
            if (newStatus != null && !newStatus.equals(transferHeaderStatusId)) {
            	try {
	            	Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", transferHeaderStatusId, "statusIdTo", newStatus);
	                GenericValue statusChange = null;
					try {
						statusChange = delegator.findOne("StatusValidChange", statusFields, true);
		                if (statusChange != null) {
			                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("transferId", transferId, "statusId", newStatus, "userLogin", userLogin);
			                Map<String, Object> newSttsResult = null;
			                newSttsResult = dispatcher.runSync("changeTransferStatus", serviceContext);
			                if (ServiceUtil.isError(newSttsResult)) {
			                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
			                }
		                }
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("StatusValidChange not found");
					}
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeTransferStatus service", module);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }
	
	public static Map<String,Object> createNotifyTransfer(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String transferId = (String)context.get("transferId");
		String messages = (String)context.get("messages");
		String roleTypeProperties = (String)context.get("roleTypeProperties");
		String header = "";
		String action = "";
//		String target = "";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listPartyTos = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, roleTypeProperties), delegator);
		if (listPartyTos.isEmpty()){
	    	ServiceUtil.returnSuccess("OLBUS: Party to receive notify not found. Notify cannot be created");
	    }
		GenericValue transfer = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		GenericValue transferType = delegator.findOne("TransferType", false, UtilMisc.toMap("transferTypeId", transfer.getString("transferTypeId")));
		header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)transferType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "TransferId", (Locale)context.get("locale")) +": [" +transferId+"]";
		if ("roleTypeId.receiveMsg.transfer.approved".equals(roleTypeProperties)) {
			action = "accViewDetailTransfer?transferId="+transferId+"&activeTab=general-tab";
		} else {
			action = "viewDetailTransfer?transferId="+transferId+"&activeTab=general-tab";
		}
//		target = "transferId="+transferId+ ";activeTab=general-tab";	
		LocalDispatcher dispatcher = ctx.getDispatcher();
		for (String partyId : listPartyTos) {
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyId", partyId);
    		mapContext.put("action", action);
    		mapContext.put("targetLink", "");
    		mapContext.put("header", header);
    		mapContext.put("ntfType", "ONE");
    		mapContext.put("userLogin", userLogin);
    		mapContext.put("openTime", UtilDateTime.nowTimestamp());
    		try {
    			dispatcher.runSync("createNotification", mapContext);
    		} catch (GenericServiceException e) {
    			e.printStackTrace();
    		}
		}
		
		result.put("transferId", transferId);
		return result;
	}
	
	public static Map<String, Object> checkTransferStatus(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String transferId = (String)context.get("transferId");
		String statusId = "";
		Delegator delegator = ctx.getDelegator();
		statusId = LogisticsProductUtil.checkTransferStatus(delegator, transferId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("statusId", statusId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListTransferByRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
		EntityListIterator listTransfers = null;
		String requirementId = null;
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		if (parameters.get("requirementId") != null && parameters.get("requirementId").length > 0){
			requirementId = (String)parameters.get("requirementId")[0];
    	}
    	if (requirementId != null && !"".equals(requirementId)){
			try {
				List<GenericValue> listTransferByReqs = delegator.findList("TransferRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				List<String> listIds = EntityUtil.getFieldListFromEntityList(listTransferByReqs, "transferId", true);
				EntityCondition transferIdCond = EntityCondition.makeCondition("transferId", EntityOperator.IN, listIds);
				listAllConditions.add(transferIdCond);
				listTransfers = delegator.find("TransferHeaderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			} catch (GenericEntityException e){
				return ServiceUtil.returnError("OLBIUS: get transfer error!");
			}
    	}
		successResult.put("listIterator", listTransfers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementTransfer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
		EntityListIterator listTransfers = null;
		String requirementId = null;
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		if (parameters.get("requirementId") != null && parameters.get("requirementId").length > 0){
			requirementId = (String)parameters.get("requirementId")[0];
    	}
    	if (requirementId != null && !"".equals(requirementId)){
			try {
				List<GenericValue> listTransferByReqs = delegator.findList("TransferRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				List<String> listIds = EntityUtil.getFieldListFromEntityList(listTransferByReqs, "transferId", true);
				EntityCondition transferIdCond = EntityCondition.makeCondition("transferId", EntityOperator.IN, listIds);
				listAllConditions.add(transferIdCond);
				listTransfers = delegator.find("TransferHeaderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			} catch (GenericEntityException e){
				return ServiceUtil.returnError("OLBIUS: get transfer error!");
			}
    	}
		successResult.put("listIterator", listTransfers);
		return successResult;
	}
	
	public static Map<String, Object> getTransferTypeByEnumId(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String enumId = (String)context.get("enumId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listTransferTypes = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(enumId)){
			listTransferTypes = delegator.findList("TransferTypeEnum", EntityCondition.makeCondition(UtilMisc.toMap("enumId", enumId)), null, null, null, false);
			listTransferTypes = EntityUtil.filterByDate(listTransferTypes);
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("listTransferTypes", listTransferTypes);
		return result;
	}
	
	public static Map<String, Object> checkTransferShipmentMethod(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryId = (String)context.get("deliveryId");
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		if ("DELIVERY_TRANSFER".equals(delivery.getString("deliveryTypeId"))) {
			String transferId = delivery.getString("transferId");
			String statusId = delivery.getString("statusId");
			if (UtilValidate.isNotEmpty(transferId) && "DLV_EXPORTED".equals(statusId)){
				List<GenericValue> list = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
				if (!list.isEmpty()){
					String shipmentMethodTypeId = list.get(0).getString("shipmentMethodTypeId");
					if (UtilValidate.isNotEmpty(shipmentMethodTypeId) && "NO_SHIPPING".equals(shipmentMethodTypeId)){
						List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
						if (!listDlvItems.isEmpty()){
							List<Map<String, Object>> listJson = new ArrayList<Map<String, Object>>();
							for (GenericValue item : listDlvItems) {
								Map<String, Object> map = FastMap.newInstance();
								map.put("fromTransferId", item.getString("fromTransferId"));
			                    map.put("fromTransferItemSeqId", item.getString("fromTransferItemSeqId"));
			                    map.put("inventoryItemId", item.getString("inventoryItemId"));
			                    map.put("deliveryId", item.getString("deliveryId"));
			                    map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
			                    map.put("actualExportedQuantity", item.getBigDecimal("actualExportedQuantity"));
			                    map.put("actualDeliveredQuantity", item.getString("actualExportedQuantity"));
								listJson.add(map);
							}
							String listDeliveryItems = JsonUtil.convertListMapToJSON(listJson);
							Timestamp now = UtilDateTime.nowTimestamp();
							Long actualArrivalDate = now.getTime();
							LocalDispatcher dispatcher = ctx.getDispatcher();
							try {
								dispatcher.runSync("updateDeliveryItemList", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "deliveryId", deliveryId, "listDeliveryItems", listDeliveryItems, "actualArrivalDate", actualArrivalDate));
							} catch (GenericServiceException e){
								return ServiceUtil.returnError("OLBIUS: runsync service updateDeliveryItemList error!");
							}
						}
					}
				}
			}
		}
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	public static Map<String, Object> cancelTransferInventoryReserved(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		String transferId = (String)context.get("transferId");
		GenericValue transferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		String needsReservesInventory = transferHeader.getString("needsReservesInventory");
		if (UtilValidate.isNotEmpty(needsReservesInventory) && "Y".equals(needsReservesInventory)){
			List<GenericValue> listItems = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
			if (!listItems.isEmpty()){
				LocalDispatcher dispatcher = ctx.getDispatcher();
				for (GenericValue item : listItems) {
					Map<String, Object> mapContext = FastMap.newInstance();
					mapContext.putAll(item.getFields(UtilMisc.toList("transferId", "transferItemSeqId", "shipGroupSeqId", "inventoryItemId")));
					mapContext.put("userLogin", (GenericValue)context.get("userLogin"));
					try {
						dispatcher.runSync("cancelTransferItemShipGrpInvRes", mapContext);
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: runsync service cancelTransferItemShipGrpInvRes error!");
					}
				}
			}
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("transferId", transferId);
		return result;
	}
	
	public static Map<String, Object> createTransferHeaderNote(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String transferId = (String)context.get("transferId");
    	String noteId = (String)context.get("noteId");
    	GenericValue reqNote = delegator.findOne("TransferHeaderNote", false, UtilMisc.toMap("transferId", transferId, "noteId", noteId));
    	if (UtilValidate.isEmpty(reqNote)){
    		reqNote = delegator.makeValue("TransferHeaderNote");
    		reqNote.put("transferId", transferId);
    		reqNote.put("noteId", noteId);
    		delegator.createOrStore(reqNote);
    	} else {
    		return ServiceUtil.returnError("OLBIUS: Note existed");
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("transferId", transferId);
		mapReturn.put("noteId", noteId);
		return mapReturn;
	}
	
	public static Map<String, Object> recalculateTransferGrandTotal(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		String transferId = (String)context.get("transferId");
		Map<String, Object> mapReturn = FastMap.newInstance();
		GenericValue objTransferHeader = null;
		try {
			objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne TransferHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objTransferHeader)) {
			BigDecimal grandTotal = BigDecimal.ZERO;
			try {
				grandTotal = TransferReadHepler.getTransferTotal(delegator, transferId);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when TransferReadHepler.getTransferTotal: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			objTransferHeader.put("grandTotal", grandTotal);
			try {
				delegator.store(objTransferHeader);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when store TransferReadHepler: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			mapReturn.put("grandTotal", grandTotal);
		} else {
			String errMsg = "OLBIUS: Fatal error when TransferHeader not found: " + transferId;
			return ServiceUtil.returnError(errMsg);
		}
		mapReturn.put("transferId", transferId);
		return mapReturn;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> updateTransferAndItem(DispatchContext ctx, Map<String, Object> context){
		
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher =ctx.getDispatcher(); 
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String transferId = (String) context.get("transferId");
		Long shipBeforeDate = Long.parseLong((String) context.get("shipBeforeDate"));
		Long shipAfterDate = Long.parseLong((String) context.get("shipAfterDate"));
		String description = (String) context.get("description");
		JSONArray listProducts = JSONArray.fromObject(context.get("listProducts"));
		
		try {
			Map<String, Object> transferInfoMap = context;
			transferInfoMap.remove("listProducts");
			transferInfoMap.put("userLogin", userLogin);
			dispatcher.runSync("updateTransferInfo", transferInfoMap);
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when run Service updateTransferInfo: " + e.toString();
			Debug.logError(e, errMsg, module);
			e.printStackTrace();
			return ServiceUtil.returnError(errMsg);
		}
		
		List<Map<String, Object>> productArr = FastList.newInstance();
		for(int i = 0; i< listProducts.size(); i++){
			JSONObject objectJSON = JSONObject.fromObject(listProducts.get(i));
			Map<String, Object> product = new HashMap<String, Object>();
			product.put("userLogin", userLogin);
			if(objectJSON.containsKey("sequenceId")) {
				if(!objectJSON.getString("sequenceId").equals("null")) {
					product.put("sequenceId", objectJSON.getString("sequenceId"));
				}else {
					product.put("sequenceId", null);
				}
				
			}
			product.put("productId", objectJSON.get("productId"));
			if(objectJSON.containsKey("quantity")) {
				if(!objectJSON.getString("quantity").equals("null")) {
					product.put("quantity", BigDecimal.valueOf(Double.parseDouble(objectJSON.get("quantity").toString())));
				}
			}
			if(objectJSON.containsKey("amount")) {
				if(!objectJSON.getString("amount").equals("null")) {
					product.put("amount", BigDecimal.valueOf(Double.parseDouble(objectJSON.get("amount").toString())));
				}
			}
			if(objectJSON.containsKey("weightUomId")) {
				if(!objectJSON.getString("weightUomId").equals("null")) {
					product.put("weightUomId", objectJSON.get("weightUomId"));
				}
			}
			product.put("quantityUomId", objectJSON.get("uomId"));
			product.put("transferId", transferId);
			if(UtilValidate.isNotEmpty(objectJSON.get("statusId"))) {
				product.put("statusId", objectJSON.get("statusId"));
			}
			productArr.add(product);
		}
		
		for(Map<String, Object> product : productArr){
			try {
				dispatcher.runSync("updateTransferItem", product);
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run Service updateTransferItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(errMsg);
			}
		}
		returnResult.put("transferId", transferId);
		return returnResult;
	}
	
	

	@SuppressWarnings("unused")
	public static Map<String, Object> updateTransferInfo(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String transferId = (String) context.get("transferId");
		Long shipBeforeDate = Long.parseLong((String) context.get("shipBeforeDate"));
		Long shipAfterDate = Long.parseLong((String) context.get("shipAfterDate"));
		String description = (String) context.get("description");
		
		try {
			GenericValue transfer = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
			if(UtilValidate.isNotEmpty(transfer)){
				transfer.set("shipBeforeDate", new Timestamp(shipBeforeDate));
				transfer.set("shipAfterDate", new Timestamp(shipAfterDate));
				transfer.set("description", description);
				delegator.store(transfer);
				new TimeStamp(shipAfterDate);
			}
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when run Service updateTransferInfo: " + e.toString();
			Debug.logError(e, errMsg, module);
			e.printStackTrace();
			return ServiceUtil.returnError(errMsg);
		}
		returnResult.put("transferId", transferId);
		return returnResult;
	}
	
	
	public static Map<String, Object> updateTransferItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher =ctx.getDispatcher(); 
		String transferId = (String) context.get("transferId");
		String sequenceId = (String) context.get("sequenceId");
		String statusId = (String) context.get("statusId");
		if (sequenceId == null && statusId.equals("TRANS_ITEM_CREATED")){
			try {
				context.remove("sequenceId");
				dispatcher.runSync("addTransferItem", context);
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run Service addTransferItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} else{
			GenericValue transferItem = null;
			try {
				transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", sequenceId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when run findOne TransferItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if(UtilValidate.isNotEmpty(transferItem)){
				transferItem.setNonPKFields(context);
				try {
					delegator.store(transferItem);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when run store TransferItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
		}
		returnResult.put("transferId", transferId);
		return returnResult;
	}
	
	public static Map<String, Object> addTransferItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		@SuppressWarnings("unused")
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String transferId = (String)context.get("transferId");
		GenericValue item = delegator.makeValue("TransferItem");
		
		item.put("transferId", transferId);
		delegator.setNextSubSeqId(item, "transferItemSeqId", 5, 1);
		item.put("quantity", context.get("quantity"));
		item.put("weightUomId", context.get("weightUomId"));
		item.put("amount", context.get("amount"));
		item.put("quantityUomId",context.get("quantityUomId"));
		item.put("statusId", "TRANS_ITEM_CREATED");
		item.put("productId", context.get("productId"));
		try {
			item.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				String errMsg = "Fatal error calling addTransferItem service: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError("OLBIUS: addTransferItem error! " + e.toString());
		}
		returnResult.put("transferId", transferId);
		
		return returnResult;
	}
	
	public static Map<String, Object> reserveInventoryForTransfer(DispatchContext ctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String transferId = (String) context.get("transferId");
        String facilityId = (String)context.get("facilityId");
        Locale locale = (Locale) context.get("locale");
		List<GenericValue> listTransItemShipGrpAssoc;
		try {
			listTransItemShipGrpAssoc = delegator.findList("TransferItemShipGroupAssoc", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
			if (!listTransItemShipGrpAssoc.isEmpty()){
				for (GenericValue assoc : listTransItemShipGrpAssoc){
					Map<String, Object> mapInv = FastMap.newInstance();
					GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", (String)assoc.get("transferItemSeqId")));
					if (transferItem != null){
						mapInv.put("productId", (String)transferItem.get("productId"));
						mapInv.put("facilityId", facilityId);
						mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						mapInv.put("userLogin", userLogin);
						mapInv.put("expireDate", transferItem.get("expireDate"));
						Map<String, Object> mapInvResult = FastMap.newInstance();
						String inventoryItemId = null;
						try {
							mapInvResult = dispatcher.runSync("createInventoryItem", mapInv);
							inventoryItemId = (String)mapInvResult.get("inventoryItemId");
							mapInv = FastMap.newInstance();
							mapInv.put("inventoryItemId", inventoryItemId);
							mapInv.put("userLogin", userLogin);
							mapInv.put("availableToPromiseTotal", transferItem.getBigDecimal("quantity").negate());
							mapInv.put("quantityOnHandTotal", BigDecimal.ZERO);
							dispatcher.runSync("createInventoryItemCheckSetAtpQoh", mapInv);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
				        if (inventoryItemId != null){
				        	GenericValue reservedItem = delegator.makeValue("TransferItemShipGrpInvRes");
				        	reservedItem.put("transferId", transferId);
				        	reservedItem.put("shipGroupSeqId", (String)assoc.get("shipGroupSeqId"));
				        	reservedItem.put("transferItemSeqId", (String)assoc.get("transferItemSeqId"));
				        	reservedItem.put("inventoryItemId", inventoryItemId);
				        	reservedItem.put("quantity", assoc.getBigDecimal("quantity"));
				        	reservedItem.put("quantityNotAvailable", assoc.getBigDecimal("cancelQuantity"));
				        	reservedItem.put("reservedDatetime", UtilDateTime.nowTimestamp());
				        	reservedItem.put("createdDatetime", UtilDateTime.nowTimestamp());
				        	delegator.create(reservedItem);
				        }
					}
				}
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CannotFoundTransferItemShipGroupAssoc", locale));
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		return successResult;
	}
	
	public static Map<String, Object> completeTransfer(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String transferId = null;
		if (UtilValidate.isNotEmpty(context.get("transferId"))) {
			transferId = (String) context.get("transferId");
		}
		GenericValue objTransferHeader = null;
		try {
			objTransferHeader = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne TransferHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objTransferHeader) && ("TRANSFER_APPROVED".equals(objTransferHeader.get("statusId")) || "TRANSFER_EXPORTED".equals(objTransferHeader.get("statusId")))) {
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("transferId", transferId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("TRANS_ITEM_CANCELLED", "TRANS_ITEM_REJECTED", "TRANS_ITEM_COMPLETED")));
			
			List<GenericValue> listTransferItem = FastList.newInstance();
			try {
				listTransferItem = delegator.findList("TransferItem", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList TransferItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listTransferItem.isEmpty()){
				LocalDispatcher dispatcher = ctx.getDispatcher();
				for (GenericValue item : listTransferItem) {
					String transferItemSeqId = item.getString("transferItemSeqId");
					String productId = item.getString("productId");
					boolean isWeight = ProductUtil.isWeightProduct(delegator, productId);
					BigDecimal quantity = item.getBigDecimal("quantity");
					if (isWeight) quantity = item.getBigDecimal("amount");
					if (UtilValidate.isNotEmpty(item.get("cancelQuantity"))) {
						quantity = quantity.subtract(item.getBigDecimal("cancelQuantity"));
					}
					if (UtilValidate.isNotEmpty(item.get("cancelAmount"))) {
						quantity = quantity.subtract(item.getBigDecimal("cancelAmount"));
					}
					
					BigDecimal exported = BigDecimal.ZERO; 
					
					conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("transferId", transferId));
					conds.add(EntityCondition.makeCondition("transferItemSeqId", transferItemSeqId));
					conds.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO));
					
					List<GenericValue> listInventoryItemDetail = FastList.newInstance();
					try {
						listInventoryItemDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conds), null,
								null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList InventoryItemDetail: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listInventoryItemDetail.isEmpty()){
						for (GenericValue inv : listInventoryItemDetail) {
							if (isWeight){
								exported = exported.add(inv.getBigDecimal("amountOnHandDiff").negate());
							} else {
								exported = exported.add(inv.getBigDecimal("quantityOnHandDiff").negate());
							}
						}
						if (quantity.compareTo(exported) > 0){
							Map<String, Object> map = FastMap.newInstance();
							map.put("transferId", transferId);
							map.put("sequenceId", transferItemSeqId);
							map.put("quantity", exported);
							map.put("statusId", item.getString("statusId"));
							try {
								Map<String, Object> rs = dispatcher.runSync("updateTransferItem", map);
								if (ServiceUtil.isError(rs)) {
									return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
								}
							} catch (GenericServiceException e) {
								String errMsg = "OLBIUS: Fatal error when run service updateTransferItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
						if ("TRANS_ITEM_APPROVED".equals(item.getString("statusId"))){
							Map<String, Object> mapChangeStatus = FastMap.newInstance();
							mapChangeStatus.put("transferId", transferId);
							mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
							mapChangeStatus.put("fromStatusId", item.getString("statusId"));
							mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
							mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
							try {
								mapChangeStatus.put("statusId", "TRANS_ITEM_EXPORTED");
								dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("Change trasfer item status Error!");
							}
						}
						Map<String, Object> mapChangeStatus = FastMap.newInstance();
						mapChangeStatus.put("transferId", transferId);
						mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
						mapChangeStatus.put("fromStatusId", "TRANS_ITEM_EXPORTED");
						mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
						mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							mapChangeStatus.put("statusId", "TRANS_ITEM_DELIVERED");
							dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("Change trasfer item status Error!");
						}
						
						// change to complete 
						mapChangeStatus = FastMap.newInstance();
						mapChangeStatus.put("transferId", transferId);
						mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
						mapChangeStatus.put("fromStatusId", "TRANS_ITEM_DELIVERED");
						mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
						mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							mapChangeStatus.put("statusId", "TRANS_ITEM_COMPLETED");
							dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("Change trasfer item status Error!");
						}
					} else {
						Map<String, Object> mapChangeStatus = FastMap.newInstance();
						mapChangeStatus.put("transferId", transferId);
						mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
						mapChangeStatus.put("fromStatusId", item.getString("statusId"));
						mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
						mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							mapChangeStatus.put("statusId", "TRANS_ITEM_CANCELLED");
							dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("Change trasfer item status Error!");
						}
					}
				}
				List<GenericValue> listDelivery = FastList.newInstance();
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("transferId", transferId));
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("DLV_CANCELLED", "DLV_EXPORTED", "DLV_DELIVERED")));
				try {
					listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(conds), null, null,
							null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Delivery: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				if (!listDelivery.isEmpty()){
					for (GenericValue dlv : listDelivery) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("deliveryId", dlv.getString("deliveryId"));
						map.put("statusId", "DLV_CANCELLED");
						
						try {
							Map<String, Object> rs = dispatcher.runSync("changeDeliveryStatus", map);
							if (ServiceUtil.isError(rs)) {
								return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
							}
						} catch (GenericServiceException e) {
							String errMsg = "OLBIUS: Fatal error when run service changeDeliveryStatus: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}
				}
			}
		}
		result.put("transferId", transferId);
		return result;
	}
	
}