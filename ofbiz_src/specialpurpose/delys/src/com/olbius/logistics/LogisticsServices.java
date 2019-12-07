package com.olbius.logistics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.PartyUtil;
import com.olbius.util.ProductUtil;
import com.olbius.util.SecurityUtil;

import javolution.util.FastMap;

public class LogisticsServices {

	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";

	public static Map<String, Object> acceptReceiptRequirements(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String tmp = (String) context.get("listReceiptRequirements");
		JSONArray listReceiptRequirements = JSONArray.fromObject(tmp);
		for (int i = 0; i < listReceiptRequirements.size(); i++) {
			JSONObject item = listReceiptRequirements.getJSONObject(i);
			String facilityId = item.getString("facilityId");
			String contactMechId = item.getString("contactMechId");
			String requirementId = item.getString("requirementId");
			String agreementId = item.getString("agreementId");
			String orderId = item.getString("orderId");

			Map<String, Object> mapReceiptReq = FastMap.newInstance();
			mapReceiptReq.put("facilityId", facilityId);
			mapReceiptReq.put("contactMechId", contactMechId);
			mapReceiptReq.put("agreementId", agreementId);
			mapReceiptReq.put("requirementId", requirementId);
			mapReceiptReq.put("orderId", orderId);
			mapReceiptReq.put("userLogin", userLogin);
			try {
				dispatcher.runSync("acceptReceiptRequirement", mapReceiptReq);
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		List<String> listImportSpecialists = new ArrayList<String>();
		List<String> listPartyGroups = SecurityUtil.getPartiesByRoles("IMPORT_SPECIALIST", delegator);
		if (!listPartyGroups.isEmpty()){
			for (String group : listPartyGroups){
				try {
					List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "IMPORT_SPECIALIST")), null, null, null, false);
					listManagers = EntityUtil.filterByDate(listManagers);
					if (!listManagers.isEmpty()){
						for (GenericValue manager : listManagers){
							listImportSpecialists.add(manager.getString("partyIdFrom"));
						}
					}
				} catch (GenericEntityException e) {
					ServiceUtil.returnError("get Party relationship error!");
				}
			}
		}
		if(!listImportSpecialists.isEmpty()){
			for (String managerParty : listImportSpecialists){
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				String targetLink = "statusId=REQ_ACCEPTED";
				String header = UtilProperties.getMessage(resource, "ReceiptRequirementAccepted", (Locale)context.get("locale"));
				mapContext.put("partyId", sendToPartyId);
				mapContext.put("action", "getListReceiptRequirements");
				mapContext.put("targetLink", targetLink);
				mapContext.put("header", header);
				mapContext.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createNotification", mapContext);
				} catch (GenericServiceException e) {
					ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateNotificationError", (Locale)context.get("locale")));
				}
			}
		}
		return result;
	}
	public static Map<String, Object> getPartyName(DispatchContext ctx, Map<String, ? extends Object> context) {
		String partyId = (String)context.get("partyId");
		String partyName = null;
		Delegator delegator = ctx.getDelegator();
		try {
			partyName = PartyUtil.getPartyName(delegator, partyId);
		} catch(GenericEntityException e){
			ServiceUtil.returnError(e.toString());
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("partyName", partyName);
		return result;
	}
	
	public static Map<String, Object> createReturnDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		String returnId = (String)context.get("returnId");
		Delegator delegator = ctx.getDelegator();
		GenericValue delivery = delegator.makeValue("Delivery");
		String deliveryId = delegator.getNextSeqId("Delivery");
		delivery.put("deliveryId", deliveryId);
		delivery.put("returnId", returnId);
		delivery.put("partyIdTo", context.get("partyIdTo"));
		delivery.put("statusId", context.get("statusId"));
		delivery.put("destFacilityId", context.get("destFacilityId"));
		delivery.put("destContactMechId", context.get("destContactMechId"));
		delivery.put("deliveryDate", context.get("deliveryDate"));
		delivery.put("deliveryTypeId", context.get("deliveryTypeId"));
		try {
			delegator.create(delivery);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError(e.toString());
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String destFacilityId = (String)context.get("destFacilityId"); 
		Timestamp deliveryDate = (Timestamp)context.get("deliveryDate");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String listItemString = (String)context.get("listReturnItems");
		JSONArray listReturnItems = JSONArray.fromObject(listItemString);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for (int i = 0; i < listReturnItems.size(); i++) {
			JSONObject item = listReturnItems.getJSONObject(i);
			GenericValue deliveryItem = delegator.makeValue("DeliveryItem");
			deliveryItem.put("deliveryId", deliveryId);
			delegator.setNextSubSeqId(deliveryItem, "deliveryItemSeqId", 5, 1);
			deliveryItem.put("fromReturnId", returnId);
			deliveryItem.put("fromReturnItemSeqId", item.getString("returnItemSeqId"));
			if (item.containsKey("actualExpireDate") && item.getString("actualExpireDate") != null){
				Timestamp actualExpireDate = null;
				String tmp = item.getString("actualExpireDate");
			    try {
					java.util.Date parsedTimeStamp = dateFormat.parse(tmp);
					actualExpireDate = new Timestamp(parsedTimeStamp.getTime());
				} catch (ParseException e1) {
					ServiceUtil.returnError(e1.toString());
				}
				deliveryItem.put("actualExpireDate", actualExpireDate);
			}
			if (item.containsKey("actualManufacturedDate") && item.getString("actualManufacturedDate") != null){
				Timestamp actualManufacturedDate = null;
			    try {
					java.util.Date parsedTimeStamp = dateFormat.parse(item.getString("actualManufacturedDate"));
					actualManufacturedDate = new Timestamp(parsedTimeStamp.getTime());
				} catch (ParseException e1) {
					ServiceUtil.returnError(e1.toString());
				}
			    deliveryItem.put("actualManufacturedDate", actualManufacturedDate);
			}
			deliveryItem.put("actualDeliveredQuantity", new BigDecimal(item.getString("actualDeliveredQuantity")));
			deliveryItem.put("quantity", new BigDecimal(item.getString("quantity")));
			deliveryItem.put("statusId", item.get("statusId"));
			try {
				delegator.create(deliveryItem);
			} catch (GenericEntityException e) {
				ServiceUtil.returnError(e.toString());
			}
			Map<String, Object> mapInv = new FastMap<String, Object>();
			GenericValue returnItem = null;
			try {
				returnItem = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", item.getString("returnItemSeqId")));
			} catch (GenericEntityException e1) {
				ServiceUtil.returnError(e1.toString());
			}
			mapInv.put("productId", returnItem.get("productId"));
			mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
			if (item.containsKey("actualExpireDate") && item.get("actualExpireDate") != null){
				Timestamp actualExpireDate = null;
			    try {
					java.util.Date parsedTimeStamp = dateFormat.parse(item.getString("actualExpireDate"));
					actualExpireDate = new Timestamp(parsedTimeStamp.getTime());
				} catch (ParseException e1) {
					ServiceUtil.returnError(e1.toString());
				}
				mapInv.put("expireDate", actualExpireDate);
			}
			if (item.containsKey("actualManufacturedDate") && item.getString("actualManufacturedDate") != null){
				Timestamp actualManufacturedDate = null;
			    try {
					java.util.Date parsedTimeStamp = dateFormat.parse(item.getString("actualManufacturedDate"));
					actualManufacturedDate = new Timestamp(parsedTimeStamp.getTime());
				} catch (ParseException e1) {
					ServiceUtil.returnError(e1.toString());
				}
				mapInv.put("actualManufacturedDate", actualManufacturedDate);
			}
			mapInv.put("statusId", item.getString("inventoryStatusId"));
			mapInv.put("datetimeReceived", deliveryDate);
			mapInv.put("facilityId", destFacilityId);
			mapInv.put("quantityAccepted", new BigDecimal(item.getString("actualDeliveredQuantity")));
			mapInv.put("quantityExcess", BigDecimal.ZERO);
			mapInv.put("quantityRejected", BigDecimal.ZERO);
			mapInv.put("quantityQualityAssurance", BigDecimal.ZERO);
			mapInv.put("userLogin", userLogin);
			try {
				dispatcher.runSync("receiveInventoryProduct", mapInv);
			} catch (GenericServiceException e) {
				ServiceUtil.returnError("service receiveInventoryProduct error!" + e.toString());
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	public static Map<String, Object> getQuantityUomBySupplier(DispatchContext ctx, Map<String, ?> context){
		String productId = (String)context.get("productId");
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		String quantityUomId = null;
		quantityUomId = ProductUtil.getQuantityUomBySupplier(delegator, productId, orderId);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("quantityUomId", quantityUomId);
		return result;
	}
	
	public static Map<String, Object> getRequirementDateByOrder(DispatchContext ctx, Map<String, ?> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		Timestamp requirementDate = null;
		try {
			List<GenericValue> orderReq = delegator.findList("OrderRequirement", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (orderReq != null){
				requirementDate = (Timestamp)orderReq.get(0).getTimestamp("requirementDate");
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError(e.toString());
		}
		result.put("requirementDate", requirementDate);
		result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String, Object> deleteItemOfRequirement(DispatchContext ctx, Map<String, ?> context){
		String requirementId = (String)context.get("requirementId");
		Delegator delegator = ctx.getDelegator();
		try {
			delegator.removeByAnd("RequirementItem", UtilMisc.toMap("requirementId", requirementId));
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("remove RequirementItem error");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> updateFacilityByProductStore(DispatchContext ctx, Map<String, ?> context){
		String productStoreId = (String)context.get("productStoreId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		try {
			listFacilities = delegator.findList("ProductStoreFacilityDetail", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
			listFacilities = EntityUtil.filterByDate(listFacilities);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("remove updateFacilityByProductStore error");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("productStoreId", productStoreId);
		mapReturn.put("listFacilities", listFacilities);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getLogCostsDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String departmentId = null;
    	if (parameters.get("departmentId") != null && parameters.get("departmentId").length > 0){
    		departmentId = (String)parameters.get("departmentId")[0];
    	}
    	if (departmentId != null && !"".equals(departmentId)){
    		mapCondition.put("departmentId", departmentId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	} else {
    		EntityCondition deptCondition = EntityCondition.makeCondition("departmentId",EntityJoinOperator.IN, UtilMisc.toList("LOG_WAREHOUSE", "LOG_DELIVERY", "LOGISTICS"));
    		listAllConditions.add(deptCondition);
    	}
    	List<GenericValue> listCosts = new ArrayList<GenericValue>();
		List<GenericValue> listCostsTmp = new ArrayList<GenericValue>();
    	List<GenericValue> listParents = new ArrayList<GenericValue>();
    	try {
    		listCostsTmp = delegator.findList("CostAccBaseGroupByInvoiceItemType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		if (!listCostsTmp.isEmpty()){
    			for (GenericValue item : listCostsTmp){
    				GenericValue invItem = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", item.getString("invoiceItemTypeId")));
    				if (invItem != null){
    					if ("LOGISTICS_COST".equals(invItem.getString("parentTypeId"))){
    						listParents.add(item);
    					}
    				}
    			}
    		}
    		if (!listParents.isEmpty()){
    			for (GenericValue pr : listParents){
    				listCosts.add(pr);
    				List<GenericValue> listChilds = new ArrayList<GenericValue>();
    				listChilds = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", pr.get("invoiceItemTypeId"))), null, null, null, false);
    				if (!listChilds.isEmpty()){
    					for (GenericValue item : listCostsTmp){
        					for(GenericValue child : listChilds){
        						if (item.getString("invoiceItemTypeId").equals(child.getString("invoiceItemTypeId"))){
        							listCosts.add(item);
        						}
        					}
        				}
    				} 
    			}
    		}
    	} catch (GenericEntityException e){
    		ServiceUtil.returnError("getLogCostsDetail error!");
    	}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listCosts);
    	return successResult;
	}
	public static Map<String, Object> getCostsByInvoiceItemType (DispatchContext ctx, Map<String, ?> context){
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String departmentId = (String)context.get("departmentId");
		BigDecimal costMonth1 = BigDecimal.ZERO;
		BigDecimal costMonth2 = BigDecimal.ZERO;
		BigDecimal costMonth3 = BigDecimal.ZERO;
		BigDecimal costMonth4 = BigDecimal.ZERO;
		BigDecimal costMonth5 = BigDecimal.ZERO;
		BigDecimal costMonth6 = BigDecimal.ZERO;
		BigDecimal costMonth7 = BigDecimal.ZERO;
		BigDecimal costMonth8 = BigDecimal.ZERO;
		BigDecimal costMonth9 = BigDecimal.ZERO;
		BigDecimal costMonth10 = BigDecimal.ZERO;
		BigDecimal costMonth11 = BigDecimal.ZERO;
		BigDecimal costMonth12 = BigDecimal.ZERO;
		
		Delegator delegator = ctx.getDelegator();
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		Boolean isParent = false;
		Boolean isMulti = false;
		
		try {
			GenericValue invItem = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
			if (invItem != null){
				if ("LOGISTICS_COST".equals(invItem.getString("parentTypeId"))){
					List<GenericValue> listChilds = new ArrayList<GenericValue>();
					listChilds = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", invoiceItemTypeId)), null, null, null, false);
					if (!listChilds.isEmpty()){
						isParent = true;
						isMulti = false;
						for (int i=1; i<=12; i++){
							BigDecimal costsNumber = BigDecimal.ZERO;
							if (!listChilds.isEmpty()){
								for (GenericValue child : listChilds){
									List<GenericValue> listCostOfChild = delegator.findList("LogCostDetail", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", child.get("invoiceItemTypeId"), "departmentId", departmentId)),null, null, null, false);
									if (!listCostOfChild.isEmpty()){
										for (GenericValue childCost : listCostOfChild){
											Timestamp invoiceDate1 = childCost.getTimestamp("invoiceDate");
											long timestamp1 = invoiceDate1.getTime();
											Calendar cal1 = Calendar.getInstance();
											cal1.setTimeInMillis(timestamp1);
											int costYear1 = cal1.get(Calendar.YEAR);
											int costMonthtmp = cal1.get(Calendar.MONTH) + 1;
											if (costYear1 == year && costMonthtmp == i){	
												costsNumber = costsNumber.add(childCost.getBigDecimal("costPriceActual"));
											}
										}
									}
								}
							}
							switch(i){
							case 1:
								costMonth1 = costsNumber;
								break;
							case 2:
								costMonth2 = costsNumber;
								break;
							case 3:
								costMonth3 = costsNumber;
								break;
							case 4:
								costMonth4 = costsNumber;
								break;
							case 5:
								costMonth5 = costsNumber;
								break;
							case 6:
								costMonth6 = costsNumber;
								break;
							case 7:
								costMonth7 = costsNumber;
								break;
							case 8:
								costMonth8 = costsNumber;
								break;
							case 9:
								costMonth9 = costsNumber;
								break;
							case 10:
								costMonth10 = costsNumber;
								break;
							case 11:
								costMonth11 = costsNumber;
								break;
							case 12:
								costMonth12 = costsNumber;
								break;
							}
						}
					} else {
						isMulti = true;
						for (int i=1; i<=12; i++){
							BigDecimal costsNumber = BigDecimal.ZERO;
							List<GenericValue> listObjects = delegator.findList("LogCostDetail", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "departmentId", departmentId)),null, null, null, false);
							if (!listObjects.isEmpty()){
								for (GenericValue obj : listObjects){
									Timestamp invoiceDate1 = obj.getTimestamp("invoiceDate");
									long timestamp1 = invoiceDate1.getTime();
									Calendar cal1 = Calendar.getInstance();
									cal1.setTimeInMillis(timestamp1);
									int costYear1 = cal1.get(Calendar.YEAR);
									int costMonthtmp = cal1.get(Calendar.MONTH) + 1;
									if (costYear1 == year && costMonthtmp == i){	
										costsNumber = costsNumber.add(obj.getBigDecimal("costPriceActual"));
									}
								}
							}
							switch(i){
							case 1:
								costMonth1 = costsNumber;
								break;
							case 2:
								costMonth2 = costsNumber;
								break;
							case 3:
								costMonth3 = costsNumber;
								break;
							case 4:
								costMonth4 = costsNumber;
								break;
							case 5:
								costMonth5 = costsNumber;
								break;
							case 6:
								costMonth6 = costsNumber;
								break;
							case 7:
								costMonth7 = costsNumber;
								break;
							case 8:
								costMonth8 = costsNumber;
								break;
							case 9:
								costMonth9 = costsNumber;
								break;
							case 10:
								costMonth10 = costsNumber;
								break;
							case 11:
								costMonth11 = costsNumber;
								break;
							case 12:
								costMonth12 = costsNumber;
								break;
							}
						}
					}
				} else {
					isParent = false;
					for (int i=1; i<=12; i++){
						BigDecimal costsNumber = BigDecimal.ZERO;
						List<GenericValue> listCosts = delegator.findList("LogCostDetail", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "departmentId", departmentId)),null, null, null, false);
						if (!listCosts.isEmpty()){
							for (GenericValue cost : listCosts){
								Timestamp invoiceDate1 = cost.getTimestamp("invoiceDate");
								long timestamp1 = invoiceDate1.getTime();
								Calendar cal1 = Calendar.getInstance();
								cal1.setTimeInMillis(timestamp1);
								int costYear1 = cal1.get(Calendar.YEAR);
								int costMonthtmp = cal1.get(Calendar.MONTH) + 1;
								if (costYear1 == year && costMonthtmp == i){	
									costsNumber = costsNumber.add(cost.getBigDecimal("costPriceActual"));
								}
							}
						}
						switch(i){
						case 1:
							costMonth1 = costsNumber;
							break;
						case 2:
							costMonth2 = costsNumber;
							break;
						case 3:
							costMonth3 = costsNumber;
							break;
						case 4:
							costMonth4 = costsNumber;
							break;
						case 5:
							costMonth5 = costsNumber;
							break;
						case 6:
							costMonth6 = costsNumber;
							break;
						case 7:
							costMonth7 = costsNumber;
							break;
						case 8:
							costMonth8 = costsNumber;
							break;
						case 9:
							costMonth9 = costsNumber;
							break;
						case 10:
							costMonth10 = costsNumber;
							break;
						case 11:
							costMonth11 = costsNumber;
							break;
						case 12:
							costMonth12 = costsNumber;
							break;
						}
					}
				}
			} 
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("getCostsByInvoiceItemType error" + e.toString());
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("costMonth1", costMonth1);
		mapReturn.put("costMonth2", costMonth2);
		mapReturn.put("costMonth3", costMonth3);
		mapReturn.put("costMonth4", costMonth4);
		mapReturn.put("costMonth5", costMonth5);
		mapReturn.put("costMonth6", costMonth6);
		mapReturn.put("costMonth7", costMonth7);
		mapReturn.put("costMonth8", costMonth8);
		mapReturn.put("costMonth9", costMonth9);
		mapReturn.put("costMonth10", costMonth10);
		mapReturn.put("costMonth11", costMonth11);
		mapReturn.put("costMonth12", costMonth12);
		mapReturn.put("isParent", isParent);
		mapReturn.put("isMulti", isMulti);
		return mapReturn;
	}
	
//	public static Map<String, Object> getTotalCostsByInvoiceItemType(DispatchContext ctx, Map<String, ?> context){
//		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
//		String monthString = (String)context.get("month");
//		String yearString = (String)context.get("year");
//		String departmentId = (String)context.get("departmentId");
//		BigDecimal month = new BigDecimal(monthString);
//		BigDecimal year = new BigDecimal(yearString);
//		Delegator delegator = ctx.getDelegator();
//		List<GenericValue> listChilds = new ArrayList<GenericValue>();
//		BigDecimal totalCosts = BigDecimal.ZERO;
//		try {
//			listChilds = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", invoiceItemTypeId)), null, null, null, false);
//			if (!listChilds.isEmpty()){
//				for (GenericValue item : listChilds){
//					List<GenericValue> listCosts = delegator.findList("LogCostDetail", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", item.get("invoiceItemTypeId"), "departmentId", departmentId)), null, null, null, false);
//					if (!listCosts.isEmpty()){
//						for (GenericValue child : listCosts){
//							Timestamp invoiceDate = child.getTimestamp("invoiceDate");
//							long timestamp = invoiceDate.getTime();
//							Calendar cal = Calendar.getInstance();
//							cal.setTimeInMillis(timestamp);
//							int costYear = cal.get(Calendar.YEAR);
//							int costMonth = cal.get(Calendar.MONTH) + 1;
//							BigDecimal childCosts = child.getBigDecimal("costPriceActual");
//							if (costYear == year.intValue() && costMonth == month.intValue()){
//								totalCosts = totalCosts.add(childCosts);
//							}
//						}
//					}
//				}
//			}
//		} catch (GenericEntityException e){
//			ServiceUtil.returnError("getCostsByInvoiceItemType error!" + e.toString());
//		}
//		Map<String, Object> mapReturn = FastMap.newInstance();
//		mapReturn.put("totalCosts", totalCosts);
//		return mapReturn;
//	}
	
	public static Map<String, Object> updateLogisticsCosts (DispatchContext ctx, Map<String, ?> context){
		String tmp = (String)context.get("listCosts");
		JSONArray listCosts = JSONArray.fromObject(tmp);
		Delegator delegator = ctx.getDelegator();
		for (int i = 0; i < listCosts.size(); i++) {
			JSONObject item = listCosts.getJSONObject(i);
			String departmentId = item.getString("departmentId");
			String invoiceItemTypeId = item.getString("invoiceItemTypeId");
			String costTmp = item.getString("costPriceActual");
			String month = item.getString("month");
			String year = item.getString("year");
			BigDecimal costPriceActual = new BigDecimal(costTmp);
			List<GenericValue> listAccBase = new ArrayList<GenericValue>();
			try {
				listAccBase = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("departmentId", departmentId, "invoiceItemTypeId", invoiceItemTypeId)), null, null, null, false);
				if (!listAccBase.isEmpty()){
					GenericValue newCostAcc = null;
					for (GenericValue base : listAccBase){
						List<GenericValue> listCostAccByBaseId = delegator.findList("CostAccounting", EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", base.get("costAccBaseId"))), null, null, null, false);
						if (!listCostAccByBaseId.isEmpty()){
							for (GenericValue cost : listCostAccByBaseId){
								Timestamp invoiceDate = cost.getTimestamp("invoiceDate");
								long timestamp = invoiceDate.getTime();
								Calendar cal = Calendar.getInstance();
								cal.setTimeInMillis(timestamp);
								int costYear = cal.get(Calendar.YEAR);
								int costMonth = cal.get(Calendar.MONTH) + 1;
								if (Integer.parseInt(month) == costMonth && costYear == Integer.parseInt(year)){
									cost.put("costPriceActual", BigDecimal.ZERO);
								}
							}
							newCostAcc = listCostAccByBaseId.get(0);
						} 
					}
					if (newCostAcc != null){
						newCostAcc.put("costAccBaseId", listAccBase.get(0).get("costAccBaseId"));
						newCostAcc.put("costPriceActual", costPriceActual);
						newCostAcc.put("invoiceDate", UtilDateTime.nowTimestamp());
					} else {
						newCostAcc = delegator.makeValue("CostAccounting");
						newCostAcc.put("costAccBaseId", listAccBase.get(0).get("costAccBaseId"));
						newCostAcc.put("costPriceActual", costPriceActual);
						newCostAcc.put("invoiceDate", UtilDateTime.nowTimestamp());
					}
					delegator.createOrStore(newCostAcc);
				}
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("updateLogisticsCosts error!" + e.toString());
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCostsByAccBase(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String departmentId = null;
    	if (parameters.get("departmentId") != null && parameters.get("departmentId").length > 0){
    		departmentId = (String)parameters.get("departmentId")[0];
    	}
    	if (departmentId != null && !"".equals(departmentId)){
    		mapCondition.put("departmentId", departmentId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	} else {
    		EntityCondition deptCondition = EntityCondition.makeCondition("departmentId",EntityJoinOperator.IN, UtilMisc.toList("LOG_WAREHOUSE", "LOG_DELIVERY", "LOGISTICS"));
    		listAllConditions.add(deptCondition);
    	}
    	String invoiceItemTypeId = null;
    	if (parameters.get("invoiceItemTypeId") != null && parameters.get("invoiceItemTypeId").length > 0){
    		invoiceItemTypeId = (String)parameters.get("invoiceItemTypeId")[0];
    	}
    	if (invoiceItemTypeId != null && !"".equals(invoiceItemTypeId)){
    		mapCondition = new HashMap<String, String>();
    		mapCondition.put("invoiceItemTypeId", invoiceItemTypeId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	}
    	List<GenericValue> listCostsParent = new ArrayList<GenericValue>();
    	List<GenericValue> listCosts = new ArrayList<GenericValue>();
    	try {
    		listCostsParent = delegator.findList("LogCostDetail", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		if (!listCostsParent.isEmpty() && invoiceItemTypeId != null && departmentId != null){
    			List<GenericValue> listInvoiceItemChild = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", invoiceItemTypeId)), null, null, null, false);
    			if (!listInvoiceItemChild.isEmpty()){
    				for (GenericValue pr : listCostsParent){
    					listCosts.add(pr);
	    				for (GenericValue child : listInvoiceItemChild){
	    					List<GenericValue> tmp = delegator.findList("LogCostDetail", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", child.get("invoiceItemTypeId"), "departmentId", departmentId))), null, null, null, false);
	    					if (!tmp.isEmpty()){
								listCosts.add(tmp.get(0));
	    					}
	    				}
    				}
    			}
    		}
    	} catch (GenericEntityException e){
    		ServiceUtil.returnError("getLogCostsDetail error!");
    	}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listCosts);
    	return successResult;
	}
	public static Map<String, Object> getCostsOfVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String departmentId = (String)context.get("departmentId");
		String vehicleId = (String)context.get("vehicleId");
		Delegator delegator = ctx.getDelegator();
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		BigDecimal costMonth1 = BigDecimal.ZERO;
		BigDecimal costMonth2 = BigDecimal.ZERO;
		BigDecimal costMonth3 = BigDecimal.ZERO;
		BigDecimal costMonth4 = BigDecimal.ZERO;
		BigDecimal costMonth5 = BigDecimal.ZERO;
		BigDecimal costMonth6 = BigDecimal.ZERO;
		BigDecimal costMonth7 = BigDecimal.ZERO;
		BigDecimal costMonth8 = BigDecimal.ZERO;
		BigDecimal costMonth9 = BigDecimal.ZERO;
		BigDecimal costMonth10 = BigDecimal.ZERO;
		BigDecimal costMonth11 = BigDecimal.ZERO;
		BigDecimal costMonth12 = BigDecimal.ZERO;
		try {
			List<GenericValue> listCostsAccs = delegator.findList("LogCostDetail", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("departmentId", departmentId, "invoiceItemTypeId", invoiceItemTypeId, "vehicleId", vehicleId))), null, null, null, false);
			if (!listCostsAccs.isEmpty()){
				for (GenericValue cost : listCostsAccs){
					Timestamp invoiceDate = cost.getTimestamp("invoiceDate");
					long timestamp = invoiceDate.getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int costYear = cal.get(Calendar.YEAR);
					int costMonth = cal.get(Calendar.MONTH) + 1;
					if (year == costYear){
						switch(costMonth){
						case 1:
							costMonth1 = costMonth1.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 2:
							costMonth2 = costMonth2.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 3:
							costMonth3 = costMonth3.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 4:
							costMonth4 = costMonth4.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 5:
							costMonth5 = costMonth5.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 6:
							costMonth6 = costMonth6.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 7:
							costMonth7 = costMonth7.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 8:
							costMonth8 = costMonth8.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 9:
							costMonth9 = costMonth9.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 10:
							costMonth10 = costMonth10.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 11:
							costMonth11 = costMonth11.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 12:
							costMonth12 = costMonth12.add(cost.getBigDecimal("costPriceActual"));
							break;
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("getCostsOfVehicle error!");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("costMonth1", costMonth1);
		mapReturn.put("costMonth2", costMonth2);
		mapReturn.put("costMonth3", costMonth3);
		mapReturn.put("costMonth4", costMonth4);
		mapReturn.put("costMonth5", costMonth5);
		mapReturn.put("costMonth6", costMonth6);
		mapReturn.put("costMonth7", costMonth7);
		mapReturn.put("costMonth8", costMonth8);
		mapReturn.put("costMonth9", costMonth9);
		mapReturn.put("costMonth10", costMonth10);
		mapReturn.put("costMonth11", costMonth11);
		mapReturn.put("costMonth12", costMonth12);
		return mapReturn;
	}
	public static Map<String, Object> getCostsAccByVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
		String vehicleId = (String)context.get("vehicleId");
		String departmentId = (String)context.get("departmentId");
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String year = (String)context.get("year");
		List<GenericValue> listCosts = new ArrayList<GenericValue>();
		Delegator delegator = ctx.getDelegator();
		try {
			listCosts = delegator.findList("LogCostDetail", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("departmentId", departmentId, "vehicleId", vehicleId))), null, null, null, false);
			List<GenericValue> listCostsTmp = new ArrayList<GenericValue>();;
			for (GenericValue cost : listCosts){
				Timestamp invoiceDate = cost.getTimestamp("invoiceDate");
				long timestamp = invoiceDate.getTime();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(timestamp);
				int costYear = cal.get(Calendar.YEAR);
				if (Integer.parseInt(year) != costYear || invoiceItemTypeId.equals(cost.get("invoiceItemTypeId"))){
					listCostsTmp.add(cost);
				}
			}
			listCosts.removeAll(listCostsTmp);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("getCostsOfVehicle error!");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("listCosts", listCosts);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRentedFacilities(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	String company = MultiOrganizationUtil.getCurrentOrganization(delegator);
    	EntityCondition deptCondition = EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.NOT_EQUAL, company);
		listAllConditions.add(deptCondition);
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		List<GenericValue> listRentedFacilities = new ArrayList<GenericValue>();
    	try {
    		listFacilities = delegator.findList("Facility", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		if (!listFacilities.isEmpty()){
    			for (GenericValue fac : listFacilities){
    				List<GenericValue> relationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "partyIdTo",  fac.getString("ownerPartyId"), "roleTypeIdFrom", "ORGANIZATION_PARTY", "roleTypeIdTo", "RENT_FACILITY")), null, null, null, false);
    				relationship = EntityUtil.filterByDate(relationship);
    				if (!relationship.isEmpty()){
    					listRentedFacilities.add(fac);
    				}
    			}
    		}
    	} catch (GenericEntityException e){
    		ServiceUtil.returnError("getLogCostsDetail error!");
    	}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listRentedFacilities);
    	return successResult;
	}
	public static Map<String, Object> getCostsByFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String invoiceItemTypeId = "RENTED_WH_COST";
    	String departmentId = "LOG_WAREHOUSE";
    	String facilityId = (String)context.get("facilityId");
    	Delegator delegator = ctx.getDelegator();
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		BigDecimal costMonth1 = BigDecimal.ZERO;
		BigDecimal costMonth2 = BigDecimal.ZERO;
		BigDecimal costMonth3 = BigDecimal.ZERO;
		BigDecimal costMonth4 = BigDecimal.ZERO;
		BigDecimal costMonth5 = BigDecimal.ZERO;
		BigDecimal costMonth6 = BigDecimal.ZERO;
		BigDecimal costMonth7 = BigDecimal.ZERO;
		BigDecimal costMonth8 = BigDecimal.ZERO;
		BigDecimal costMonth9 = BigDecimal.ZERO;
		BigDecimal costMonth10 = BigDecimal.ZERO;
		BigDecimal costMonth11 = BigDecimal.ZERO;
		BigDecimal costMonth12 = BigDecimal.ZERO;
		try {
			List<GenericValue> listCostsAccs = delegator.findList("LogCostDetail", EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("departmentId", departmentId, "invoiceItemTypeId", invoiceItemTypeId, "facilityId", facilityId))), null, null, null, false);
			if (!listCostsAccs.isEmpty()){
				for (GenericValue cost : listCostsAccs){
					Timestamp invoiceDate = cost.getTimestamp("invoiceDate");
					long timestamp = invoiceDate.getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int costYear = cal.get(Calendar.YEAR);
					int costMonth = cal.get(Calendar.MONTH) + 1;
					if (year == costYear){
						switch(costMonth){
						case 1:
							costMonth1 = costMonth1.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 2:
							costMonth2 = costMonth2.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 3:
							costMonth3 = costMonth3.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 4:
							costMonth4 = costMonth4.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 5:
							costMonth5 = costMonth5.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 6:
							costMonth6 = costMonth6.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 7:
							costMonth7 = costMonth7.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 8:
							costMonth8 = costMonth8.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 9:
							costMonth9 = costMonth9.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 10:
							costMonth10 = costMonth10.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 11:
							costMonth11 = costMonth11.add(cost.getBigDecimal("costPriceActual"));
							break;
						case 12:
							costMonth12 = costMonth12.add(cost.getBigDecimal("costPriceActual"));
							break;
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("getCostsByFacility error!");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("costMonth1", costMonth1);
		mapReturn.put("costMonth2", costMonth2);
		mapReturn.put("costMonth3", costMonth3);
		mapReturn.put("costMonth4", costMonth4);
		mapReturn.put("costMonth5", costMonth5);
		mapReturn.put("costMonth6", costMonth6);
		mapReturn.put("costMonth7", costMonth7);
		mapReturn.put("costMonth8", costMonth8);
		mapReturn.put("costMonth9", costMonth9);
		mapReturn.put("costMonth10", costMonth10);
		mapReturn.put("costMonth11", costMonth11);
		mapReturn.put("costMonth12", costMonth12);
    	return mapReturn;
	}
	public static Map<String, Object> getInvoiceItemTypeByParent(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String parentTypeId = (String)context.get("parentTypeId");
    	String departmentId = (String)context.get("departmentId");
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listInvoices = new ArrayList<GenericValue>();
    	try {
    		listInvoices = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", parentTypeId)), null, null, null, false);
    		if (!listInvoices.isEmpty()){
    			List<GenericValue> listTmp = new ArrayList<GenericValue>();
    			for (GenericValue item : listInvoices){
    				List<GenericValue> listInvoiceByDepart = delegator.findList("CostAccBaseGroupByInvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", item.get("invoiceItemTypeId"), "departmentId", departmentId)), null, null, null, false);
    				if (listInvoiceByDepart.isEmpty()){
    					listTmp.add(item);
    				}
    			}
    			if (!listTmp.isEmpty()){
    				listInvoices.removeAll(listTmp);
    			}
    		}
    	} catch(GenericEntityException e){
    		ServiceUtil.returnError("getInvoiceItemTypeByParent" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("listInvoiceItemTypes", listInvoices);
    	return mapReturn;
	}
	
	public static Map<String, Object> getObjectByInvoiceItemType(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
    	String objectId = (String)context.get("objectId");
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listObjects = new ArrayList<GenericValue>();
    	try {
    		String company = MultiOrganizationUtil.getCurrentOrganization(delegator);
    		if (objectId.equals("vehicleId") && invoiceItemTypeId.equals("AUTO_DRIVE_COST")){
    			listObjects = delegator.findList("Vehicle", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", company, "shipmentMethodTypeId", "AUTO", "vehicleTypeId", "CARS")), null, null, null, false);
    		} else if (objectId.equals("vehicleId") && invoiceItemTypeId.equals("VEHICLE_DEPRE_COST")){
    			listObjects = delegator.findList("Vehicle", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", company, "shipmentMethodTypeId", "AUTO", "vehicleTypeId", "CARS")), null, null, null, false);
    		} else if (objectId.equals("vendorId") && invoiceItemTypeId.equals("DLV_3PL_COST")){
    			listObjects = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "roleTypeIdFrom", "BILL_FROM_VENDOR", "roleTypeIdTo", "BILL_TO_CUSTOMER")), null, null, null, false);
    		}
    	} catch(GenericEntityException e){
    		ServiceUtil.returnError("getInvoiceItemTypeByParent" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("listObjects", listObjects);
    	return mapReturn;
	}
	
	public static Map<String, Object> addLogisticsCosts(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
    	String parentTypeId = (String)context.get("parentTypeId");
    	String vehicleId = (String)context.get("vehicleId");
    	String vendorId = (String)context.get("vendorId");
    	String costsValue = (String)context.get("costsValue");
    	String costTemporary = (String)context.get("costsTemporary");
    	Long invoiceDateString = (Long)context.get("invoiceDate");
    	String departmentId = (String)context.get("departmentId");
    	Delegator delegator = ctx.getDelegator();
    	
    	Timestamp invoiceDate = new Timestamp(invoiceDateString);
    	BigDecimal cotsPriceActual = new BigDecimal(costsValue);
    	BigDecimal cotsPriceTemp = BigDecimal.ZERO;
    	if (costTemporary != null){
    		cotsPriceTemp = new BigDecimal(costTemporary);
    	}
    	try {
    		GenericValue costsAcc = delegator.makeValue("CostAccounting");
			costsAcc.put("costAccountingId", delegator.getNextSeqId("CostAccounting"));
    		if (invoiceItemTypeId != null){
    			List<GenericValue> listBase = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "departmentId", departmentId)), null, null, null, false); 
    			costsAcc.put("costAccBaseId", listBase.get(0).getString("costAccBaseId"));
				costsAcc.put("costPriceActual", cotsPriceActual);
				costsAcc.put("costPriceTemporary", cotsPriceTemp);
				costsAcc.put("invoiceDate", invoiceDate);
    			if (vehicleId != null){
    				costsAcc.put("vehicleId", vehicleId);
    			} else if (vendorId != null){
    				costsAcc.put("vendorId", vendorId);
    			}
    		} else if (parentTypeId != null) {
    			List<GenericValue> listBase = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", parentTypeId, "departmentId", departmentId)), null, null, null, false);
    			costsAcc.put("costAccBaseId", listBase.get(0).getString("costAccBaseId"));
				costsAcc.put("costPriceActual", cotsPriceActual);
				costsAcc.put("costPriceTemporary", cotsPriceTemp);
				costsAcc.put("invoiceDate", invoiceDate);
    			if (vehicleId != null){
    				costsAcc.put("vehicleId", vehicleId);
    			} else if (vendorId != null){
    				costsAcc.put("vendorId", vendorId);
    			}
    		}
    		delegator.create(costsAcc);
    	} catch(GenericEntityException e){
    		ServiceUtil.returnError("addLogisticsCosts" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	return mapReturn;
	}
	
	public static Map<String, Object> getDetailQuantityInventory(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String productId = (String)context.get("productId");
    	String facilityId = (String)context.get("originFacilityId");
    	Timestamp expireDate = (Timestamp)context.get("expireDate");
    	BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
    	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
    	Delegator delegator = ctx.getDelegator(); 
	    try {	
	    	if (expireDate != null){
		    	List<GenericValue> listInventoryItem = delegator.findList("GroupProductInventory", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId, "expireDate", expireDate)), null, null, null, false);
		    	if (!listInventoryItem.isEmpty()){
			    	quantityOnHandTotal = listInventoryItem.get(0).getBigDecimal("QOH");
			    	availableToPromiseTotal = listInventoryItem.get(0).getBigDecimal("ATP");
		    	}
	    	} else {
	    		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId)), null, null, null, false);
	    		if (!listInventoryItem.isEmpty()){
	    			for (GenericValue inv : listInventoryItem){
	    				quantityOnHandTotal = quantityOnHandTotal.add(inv.getBigDecimal("quantityOnHandTotal"));
	    				availableToPromiseTotal = availableToPromiseTotal.add(inv.getBigDecimal("availableToPromiseTotal"));
	    			}
	    		}
	    	}
	    }
    	catch (GenericEntityException e){
    		ServiceUtil.returnError("getDetailQuantityInventory error" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("availableToPromiseTotal", availableToPromiseTotal);
    	mapReturn.put("quantityOnHandTotal", quantityOnHandTotal);
    	return mapReturn;
	}
	
	public static Map<String, Object> getInventoryItemLabeledStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String inventoryItemId = (String)context.get("inventoryItemId");
    	String statusId = null;
    	Delegator delegator = ctx.getDelegator(); 
	    try {	
	    	if (inventoryItemId != null){
		    	List<GenericValue> listInventoryItemLabel = delegator.findList("InventoryItemAndLabel", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inventoryItemId)), null, null, null, false);
		    	if (!listInventoryItemLabel.isEmpty()){
		    		statusId = "INV_LABELED";
		    	} else {
		    		statusId = "INV_NO_LABEL";
		    	}
	    	} else {
	    		ServiceUtil.returnError("getInventoryItem error");
	    	}
	    }
    	catch (GenericEntityException e){
    		ServiceUtil.returnError("getDetailQuantityInventory error" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("statusId", statusId);
    	return mapReturn;
	}
	
	public static Map<String, Object> getOrderPartyNameView(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator(); 
    	List<GenericValue> listOrderParties = new ArrayList<GenericValue>();
	    try {	
	    	List<GenericValue> listOrderRole = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
	    	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	    	for(int i = 0; i < listOrderRole.size(); i++){
	    	    listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, listOrderRole.get(i).get("partyId")));
	    	}
	    	listOrderParties = delegator.findList("PartyNameView", EntityCondition.makeCondition(listCond, EntityOperator.OR), null, null, null, false);
	    }
    	catch (GenericEntityException e){
    		ServiceUtil.returnError("getDetailQuantityInventory error" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("listOrderParties", listOrderParties);
    	return mapReturn;
	}
	
	public static Map<String, Object> checkRoleByDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String deliveryId = (String)context.get("deliveryId");
    	Boolean isStorekeeperFrom = false;
    	Boolean isStorekeeperTo = false;
    	Boolean isSpecialist = false;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = ctx.getDelegator(); 
	    try {	
	    	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
	    	
	    	List<GenericValue> listStorekeeperFrom = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", "LOG_STOREKEEPER")), null, null, null, false);
	    	listStorekeeperFrom = EntityUtil.filterByDate(listStorekeeperFrom);
			if (!listStorekeeperFrom.isEmpty()){
				isStorekeeperFrom = true;
			}
			
			List<GenericValue> listStorekeeperTo = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("destFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", "LOG_STOREKEEPER")), null, null, null, false);
			listStorekeeperTo = EntityUtil.filterByDate(listStorekeeperTo);
			if (!listStorekeeperTo.isEmpty()){
				isStorekeeperTo  = true;
			}
			
			List<GenericValue> listSpecialist = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", "LOG_SPECIALIST")), null, null, null, false);
			listSpecialist = EntityUtil.filterByDate(listSpecialist);
			if (!listSpecialist.isEmpty()){
				isSpecialist = true;
			}
	    }
    	catch (GenericEntityException e){
    		ServiceUtil.returnError("checkRoleByDelivery error" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("isStorekeeperFrom", isStorekeeperFrom);
    	mapReturn.put("isStorekeeperTo", isStorekeeperTo);
    	mapReturn.put("isSpecialist", isSpecialist);
    	return mapReturn;
	}
	
	public static Map<String,Object> getDetailRequirementById(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String requirementId = (String)context.get("requirementId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue requirement = null;
		String createdByUserLogin = null;
		GenericValue partyNameView = null;
		try {
			requirement = delegator.findOne("TransferRequirementDetail", UtilMisc.toMap("requirementId", requirementId), false);
			createdByUserLogin = requirement.getString("createdByUserLogin");
			if (createdByUserLogin != null){
				partyNameView = delegator.findOne("PartyNameView", false, UtilMisc.toMap("partyId", createdByUserLogin));
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("getDetailRequirementById error!");
		}
		
		String createdByPartyName = null; 
		if (partyNameView != null){
			if (partyNameView.getString("firstName") != null){
				createdByPartyName = partyNameView.getString("firstName");
			}
			if (partyNameView.getString("middleName") != null){
				createdByPartyName = createdByPartyName + partyNameView.getString("middleName");
			}
			if (partyNameView.getString("lastName") != null){
				createdByPartyName = createdByPartyName + partyNameView.getString("lastName");
			}
		}
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "getTransferRequirementSuccessfully", locale));
		result.put("requirementId", requirementId);
		result.put("statusId", requirement.get("statusId"));
		result.put("facilityIdFrom", requirement.get("facilityIdFrom"));
		result.put("facilityIdTo", requirement.get("facilityIdTo"));
		result.put("createdDate", requirement.get("createDate"));
		result.put("requirementStartDate", requirement.get("requirementStartDate"));
		result.put("requiredByDate", requirement.get("requiredByDate"));
		result.put("destContactMechId", requirement.get("destContactMechId"));
		result.put("originContactMechId", requirement.get("originContactMechId"));
		result.put("description", requirement.get("description"));
		result.put("createdByPartyName", createdByPartyName);
		result.put("createdByUserLogin", requirement.get("createdByUserLogin"));
		result.put("reason", requirement.get("reason"));
		result.put("facilityFromName", requirement.get("facilityFromName"));
		result.put("facilityToName", requirement.get("facilityToName"));
		result.put("facilityFromAddress", requirement.get("facilityFromAddress"));
		result.put("facilityToAddress", requirement.get("facilityToAddress"));
		return result;
	}
	
	public static Map<String, Object> getFacilityByPartyId(DispatchContext ctx, Map<String, Object> context){
		
		String partyId = (String)context.get("partyId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
	
		try {
			listFacilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", partyId)), null, null, null, false);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("get Facility by Party error!");
		}
	
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("partyId", partyId);
		result.put("listFacilities", listFacilities);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWarehouseRentAgreement(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		List<GenericValue> listAgreements = new ArrayList<GenericValue>();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String agreementTypeId = null;
    	if (parameters.get("agreementTypeId") != null && parameters.get("agreementTypeId").length > 0){
    		agreementTypeId = (String)parameters.get("agreementTypeId")[0];
    	}
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	if (agreementTypeId != null && !"".equals(agreementTypeId)){
    		mapCondition.put("agreementTypeId", agreementTypeId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	}
    	try {
    		listAgreements = delegator.findList("AgreementAndAgreementAttribute", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    	} catch (GenericEntityException e){
    		ServiceUtil.returnError("getWarehouseRentAgreement error!");
    	}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listAgreements);
    	return successResult;
	}
	
}