package com.olbius.importServices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.olbius.util.SecurityUtil;

public class ImportUpdateDocumentServices {
	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;
	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}
	public static final String module = ImportServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	
	public static Map<String, Object> getExternalOrderType(DispatchContext ctx, Map<String,Object> context){
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Set<String> field = FastSet.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		field.add("externalOrderTypeId");
		field.add("externalOrderTypeName");
		List<GenericValue> listOrderType = FastList.newInstance();
		try {
			listOrderType = delegator.findList("ExternalOrderType", null, field, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String,Object> contextTmp = new HashMap<String, Object>();
		contextTmp.put("userLogin", userLogin);
		Map<String, Object> mapResult = FastMap.newInstance();
		try {
			mapResult = dispatcher.runSync("getAgreementNotBill", contextTmp);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("listAgreementNotBill", mapResult.get("listAgreementNotBill"));
		result.put("listOrderType", listOrderType);
		return result;
	};
	public static Map<String, Object> createContainerAndPackingList(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> result = FastMap.newInstance();
        String packingList = (String)context.get("packingList");
        Map<String, Object> re = FastMap.newInstance();
        re = getJsonAndUpdateContainerAndPL(packingList, ctx, userLogin);
        String packingListId = (String)re.get("packingListId");
        String purchaseOrderId = (String)re.get("purchaseOrderId");
        String packingListDetail = (String)context.get("packingListDetail");
        result.put("containerId", (String)re.get("containerId"));
        getJsonAndUpdatePackingListDetail(packingListDetail, ctx, userLogin, packingListId, purchaseOrderId);
        
        Map<String,Object> contextTmp = new HashMap<String, Object>();
        contextTmp.put("containerId", (String)re.get("containerId"));
		contextTmp.put("userLogin", userLogin);
		Map<String, Object> mapResult = FastMap.newInstance();
		try {
			mapResult = dispatcher.runSync("getDetailContainer", contextTmp);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("resultContainer", mapResult);
		
		return result;
	}
	
	public static Map<String, Object> getDetailContainer(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		String containerId = (String)context.get("containerId");
		String externalOrderNumber = "";
		BigDecimal netWeightTotal = new BigDecimal(0);
		BigDecimal grossWeightTotal = new BigDecimal(0);
		Long packingUnitTotal = new Long(0);
		List<GenericValue> listPackingListHeader = FastList.newInstance();
		try {
			listPackingListHeader = delegator.findList("PackingListHeader", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String, Object>> listPackingList = FastList.newInstance();
		if(!UtilValidate.isEmpty(listPackingListHeader)){
			int checkSize = listPackingListHeader.size();
			for(GenericValue packingList : listPackingListHeader){
				Map<String, Object> mapPL = FastMap.newInstance();
				mapPL.put("packingListId", (String)packingList.get("packingListId"));
				mapPL.put("packingListNumber", (String)packingList.get("packingListNumber"));
				listPackingList.add(mapPL);
				//get order of supplier
				checkSize--;
				externalOrderNumber += "SAP-"+(String)packingList.get("externalOrderNumber");
				if(checkSize != 0){
					externalOrderNumber += " & ";
				}
				//Sum Karton
				GenericValue packingListDetailSum = null;
				try {
					packingListDetailSum = delegator.findOne("PackingListDetailSum", UtilMisc.toMap("packingListId", (String)packingList.get("packingListId")), false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(packingListDetailSum != null && packingListDetailSum.get("packingUnit") != null){
					packingUnitTotal += (Long)packingListDetailSum.getLong("packingUnit");
				}
				//Sum netWeight
				netWeightTotal = netWeightTotal.add((BigDecimal)packingList.get("netWeightTotal"));
				//Sum gross Weight
				grossWeightTotal = grossWeightTotal.add((BigDecimal)packingList.get("grossWeightTotal"));
				
			}
		}
		Map<String, Object> rowDetail = FastMap.newInstance();
		rowDetail.put("externalOrderNumber", externalOrderNumber);
		rowDetail.put("netWeightTotal", netWeightTotal.toString());
		rowDetail.put("grossWeightTotal", grossWeightTotal.toString());
		rowDetail.put("packingUnitTotal", packingUnitTotal.toString());
		rowDetail.put("listPackingListHeader", listPackingList);
		return rowDetail;
	}
	
	public static Map<String, Object> getPurchaseOrderByAgreementId(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String agreementId = (String)context.get("agreementId");
		List<GenericValue> listAgreementAndOrder = FastList.newInstance();
		try {
			listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String orderId = null;
		if(!UtilValidate.isEmpty(listAgreementAndOrder)){
			GenericValue order = EntityUtil.getFirst(listAgreementAndOrder);
			orderId = (String)order.get("orderId");
			result.put("orderId", orderId);
		}
		return result;
	}
	
	public static Map<String, Object> getJsonAndUpdateContainerAndPL(String packingList,  DispatchContext ctx, GenericValue userLogin){
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		JSONObject packingListJson = JSONObject.fromObject(packingList);
		String billId = (String)packingListJson.get("billId");
		String packingListNumber = (String)packingListJson.get("packingListNumber");
        Long packingListDateLong = (Long)packingListJson.get("packingListDate");
        java.sql.Date packingListDate = new java.sql.Date(packingListDateLong);
        String externalInvoiceNumber = (String)packingListJson.get("invoiceNumber");
        Long externalInvoiceDateLong = (Long)packingListJson.get("invoiceDate");
        java.sql.Date externalInvoiceDate = new java.sql.Date(externalInvoiceDateLong);
        String externalOrderNumber = (String)packingListJson.get("orderNumberSupp");
        String externalOrderTypeId = (String)packingListJson.get("orderTypeSuppId");
        String containerId = (String)packingListJson.get("containerId");
        String purchaseAgreementId = (String)packingListJson.get("purchaseOrderId");
        String netWeightTotalStr = (String)packingListJson.get("totalNetWeight");
        BigDecimal netWeightTotal = new BigDecimal(Integer.parseInt(netWeightTotalStr));
        String grossWeightTotalStr = (String)packingListJson.get("totalGrossWeight");
        BigDecimal grossWeightTotal = new BigDecimal(Integer.parseInt(grossWeightTotalStr));
        String packingListId = (String)packingListJson.get("packingListId");
        String sealNumber = (String)packingListJson.get("sealNumber");
        String containerNumber = (String)packingListJson.get("containerNumber");
        String purchaseOrderId = null;
        //update status agreement 
        Map<String,Object> contextTmpSttAgreement = new HashMap<String, Object>();
        contextTmpSttAgreement.put("agreementId", purchaseAgreementId);
        contextTmpSttAgreement.put("statusId", "AGREEMENT_PROCESSING");
        contextTmpSttAgreement.put("userLogin", userLogin);
		try {
			dispatcher.runSync("updateStatusAgreement", contextTmpSttAgreement);
		} catch (GenericServiceException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
        //get order from AgreementAndOrder
        Map<String,Object> contextTmpAgreement = new HashMap<String, Object>();
        contextTmpAgreement.put("agreementId", purchaseAgreementId);
        contextTmpAgreement.put("userLogin", userLogin);
		try {
			Map<String, Object> order = dispatcher.runSync("getPurchaseOrderByAgreementId", contextTmpAgreement);
			purchaseOrderId = (String)order.get("orderId");
		} catch (GenericServiceException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
        
        //update Container
        Map<String,Object> contextTmpContainer = new HashMap<String, Object>();
        contextTmpContainer.put("containerId", containerId);
        contextTmpContainer.put("containerNumber", containerNumber);
        contextTmpContainer.put("containerTypeId", "STANDARD_CONTAINER");
        contextTmpContainer.put("sealNumber", sealNumber);
        contextTmpContainer.put("billId", billId);
        contextTmpContainer.put("userLogin", userLogin);
		try {
			Map<String, Object> mapCont = dispatcher.runSync("updateContainer", contextTmpContainer);
			containerId = (String)mapCont.get("containerId");
		} catch (GenericServiceException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		//update OrderAndContainer
		Map<String,Object> contextTmpOrderAndContainer = new HashMap<String, Object>();
		contextTmpOrderAndContainer.put("containerId", containerId);
		contextTmpOrderAndContainer.put("orderId", purchaseOrderId);
		contextTmpOrderAndContainer.put("billId", billId);
		contextTmpOrderAndContainer.put("userLogin", userLogin);
		try {
			dispatcher.runSync("updateOrderAndContainer", contextTmpOrderAndContainer);
		} catch (GenericServiceException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
        
        Map<String,Object> contextTmpPL = new HashMap<String, Object>();
        contextTmpPL.put("packingListId", packingListId);
        contextTmpPL.put("packingListNumber", packingListNumber);
        contextTmpPL.put("packingListDate", packingListDate);
        contextTmpPL.put("externalInvoiceNumber", externalInvoiceNumber);
        contextTmpPL.put("externalInvoiceDate", externalInvoiceDate);
        contextTmpPL.put("externalOrderNumber", externalOrderNumber);
        contextTmpPL.put("externalOrderTypeId", externalOrderTypeId);
        contextTmpPL.put("containerId", containerId);
        contextTmpPL.put("purchaseOrderId", purchaseOrderId);
        contextTmpPL.put("netWeightTotal", netWeightTotal);
        contextTmpPL.put("grossWeightTotal", grossWeightTotal);
        contextTmpPL.put("userLogin", userLogin);
		try {
			result = dispatcher.runSync("updatePackingListHeader", contextTmpPL);
		} catch (GenericServiceException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		return result;
	}
	
	public static void getJsonAndUpdatePackingListDetail(String listPackingListDetail, DispatchContext ctx, GenericValue userLogin, String packingListId, String purchaseOrderId){
		
		JSONArray jsonArrPLDetail = JSONArray.fromObject(listPackingListDetail);
		if(jsonArrPLDetail.size() > 0){
			for(int i=0; i<jsonArrPLDetail.size(); i++){
				JSONObject jsonObjPLDetail = jsonArrPLDetail.getJSONObject(i);
	//			String packingListId = null;
//				if(jsonObjPLDetail.containsKey("packingListId")){
//					packingListId = (String)jsonObjPLDetail.get("packingListId");
//				}
				String packingListSeqId = null;
				if(jsonObjPLDetail.containsKey("packingListSeqId")){
					packingListSeqId = (String)jsonObjPLDetail.get("packingListSeqId");
				}
				String orderItemSeqId = null;
				if(jsonObjPLDetail.containsKey("orderItemSeqId")){
					orderItemSeqId = (String)jsonObjPLDetail.get("orderItemSeqId");
				}
				String productId = null;
				if(jsonObjPLDetail.containsKey("productId")){
					productId = (String)jsonObjPLDetail.get("productId");
				}
				String batchNumber = null;
				if(jsonObjPLDetail.containsKey("batchNumber") && !jsonObjPLDetail.get("batchNumber").toString().equals("") && jsonObjPLDetail.get("batchNumber").toString() != null && jsonObjPLDetail.get("batchNumber").toString() != "null"){
					batchNumber = (String)jsonObjPLDetail.get("batchNumber");
				}
				String gtin = null;
				if(jsonObjPLDetail.containsKey("globalTradeItemNumber") && !jsonObjPLDetail.get("globalTradeItemNumber").toString().equals("") && jsonObjPLDetail.get("globalTradeItemNumber").toString() != null  && jsonObjPLDetail.get("globalTradeItemNumber").toString() != "null"){
					gtin = (String)jsonObjPLDetail.get("globalTradeItemNumber");
				}
				Long orderUnit = null;
				if(jsonObjPLDetail.containsKey("orderUnit")){
					Integer orderUnitInt = (Integer)jsonObjPLDetail.get("orderUnit");
					orderUnit = orderUnitInt.longValue();
				}
				Long packingUnit = null;
				if(jsonObjPLDetail.containsKey("packingUnit")){
					Integer packingUnitInt = (Integer)jsonObjPLDetail.get("packingUnit");
					packingUnit = packingUnitInt.longValue();
				}
				Calendar cur = Calendar.getInstance();
				java.sql.Date datetimeManufactured = new java.sql.Date(cur.getTime().getTime());
				if(jsonObjPLDetail.containsKey("datetimeManufactured")){
					Long datetimeManufacturedLong = (Long)jsonObjPLDetail.get("datetimeManufactured");
					datetimeManufactured = new java.sql.Date(datetimeManufacturedLong);
					
				}
				java.sql.Date expireDate = new java.sql.Date(cur.getTime().getTime());
				if(jsonObjPLDetail.containsKey("expireDate")){
					Long expireDateLong = (Long)jsonObjPLDetail.get("expireDate");
					expireDate = new java.sql.Date(expireDateLong);
				}
				BigDecimal originOrderUnit = new BigDecimal(0);
				if(jsonObjPLDetail.containsKey("originOrderUnit") && !jsonObjPLDetail.get("originOrderUnit").toString().equals("") && jsonObjPLDetail.get("originOrderUnit").toString() != "" && jsonObjPLDetail.get("originOrderUnit").toString() != null && jsonObjPLDetail.get("originOrderUnit").toString() != "null"){
					Integer originOrderUnitInt = (Integer)jsonObjPLDetail.get("originOrderUnit");
					originOrderUnit = new BigDecimal(originOrderUnitInt);
				}
				
				LocalDispatcher dispatcher = ctx.getDispatcher();
				// update orderItem
				Map<String,Object> contextTmpOrderItem = new HashMap<String, Object>();
				contextTmpOrderItem.put("orderId", purchaseOrderId);
				contextTmpOrderItem.put("orderItemSeqId", orderItemSeqId);
				contextTmpOrderItem.put("quantity", new BigDecimal(orderUnit));
				Timestamp datetimeManufacturedTimestamp = new Timestamp(datetimeManufactured.getTime());
				Timestamp expireDateTimestamp = new Timestamp(expireDate.getTime());
				contextTmpOrderItem.put("datetimeManufactured", datetimeManufacturedTimestamp);
				contextTmpOrderItem.put("expireDate", expireDateTimestamp);
				contextTmpOrderItem.put("productId", productId);
				contextTmpOrderItem.put("userLogin", userLogin);
				try {
					Map<String, Object> orderItemMap = dispatcher.runSync("updateOrderItemFromPackingList", contextTmpOrderItem);
					orderItemSeqId = (String)orderItemMap.get("orderItemSeqId");
				} catch (GenericServiceException e) {
	//				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
				}
				// update packing list detail
				Map<String,Object> contextTmpPL = new HashMap<String, Object>();
		        contextTmpPL.put("packingListId", packingListId);
		        contextTmpPL.put("packingListSeqId", packingListSeqId);
		        contextTmpPL.put("orderItemSeqId", orderItemSeqId);
		        contextTmpPL.put("productId", productId);
		        contextTmpPL.put("batchNumber", batchNumber);
		        contextTmpPL.put("globalTradeItemNumber", gtin);
		        contextTmpPL.put("packingUnit", packingUnit);
		        contextTmpPL.put("orderUnit", orderUnit);
		        contextTmpPL.put("originOrderUnit", originOrderUnit);
		        contextTmpPL.put("datetimeManufactured", datetimeManufactured);
		        contextTmpPL.put("expireDate", expireDate);
		        contextTmpPL.put("userLogin", userLogin);
				try {
					dispatcher.runSync("updatePackingListDetail", contextTmpPL);
				} catch (GenericServiceException e) {
	//				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
				}
			}
		}
	}
	
	public static Map<String,Object> updateOrderItemFromPackingList(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		BigDecimal documentQuantity =  (BigDecimal)context.get("quantity");
		String quantityStr = documentQuantity.toString();
		String productId = (String)context.get("productId");
		Timestamp datetimeManufactured = null;
		if(context.get("datetimeManufactured") != null){
			datetimeManufactured = (Timestamp)context.get("datetimeManufactured");
		}
		Timestamp expireDate = null;
		if(context.get("expireDate") != null){
			expireDate = (Timestamp)context.get("expireDate");
		}
		String overridePriceMapCtx=null;
		if(context.get("overidePriceMap") != null){
			overridePriceMapCtx = (String)context.get("overidePriceMap");
		}
		String itemPriceMapCtx = null;
		if(context.get("itemPriceMap") != null){
			itemPriceMapCtx = (String)context.get("itemPriceMap");
		}
		List<String> listOrderItemSeq = new ArrayList<String>();
		try {
			List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
			if(!UtilValidate.isEmpty(listOrderItems)){
				for(GenericValue orItem : listOrderItems){
					String orderItemSeqIdCheck = (String)orItem.get("orderItemSeqId");
					listOrderItemSeq.add(orderItemSeqIdCheck);
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			List<GenericValue> listShipGroupSeqId;
			GenericValue shipGroupId = null;
				listShipGroupSeqId = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId",orderId)), null, null, null, false);
				if(!UtilValidate.isEmpty(listShipGroupSeqId)){
					shipGroupId = EntityUtil.getFirst(listShipGroupSeqId);
				}
			GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
			//update order item if exi
			if (orderItem != null){
				BigDecimal quantity = orderItem.getBigDecimal("alternativeQuantity");
				if ((quantity == null) || (quantity.compareTo(BigDecimal.ZERO) == 0)){
					if(shipGroupId != null){
				        Map<String, String> itemDescriptionMap = FastMap.newInstance();
				        Map<String, String> itemReasonMap = FastMap.newInstance();
				        Map<String, String> itemCommentMap = FastMap.newInstance();
				        Map<String, String> itemAttributesMap = FastMap.newInstance();
				        Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
				        Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
						String shipGroupSeqId = (String)shipGroupId.get("shipGroupSeqId");
						Map<String, String> itemQtyMap = FastMap.newInstance();
						Map<String, String> itemPriceMap = FastMap.newInstance();
						Map<String, String> overridePriceMap = FastMap.newInstance();
						if(overridePriceMapCtx != null){
							overridePriceMap.put(orderItemSeqId, overridePriceMapCtx);
						}
						if(itemPriceMapCtx != null){
							itemPriceMap.put(orderItemSeqId, itemPriceMapCtx);
						}else{
							itemPriceMap.put(orderItemSeqId, "0");
						}
						itemQtyMap.put(orderItemSeqId + ":" + shipGroupSeqId, quantityStr);
						itemReasonMap.put(orderItemSeqId, "");
						itemCommentMap.put(orderItemSeqId, "");
//						itemEstimatedShipDateMap.put("isdm_" + orderItemSeqId, "");
//						itemEstimatedDeliveryDateMap.put("iddm_" + orderItemSeqId, "");
//						overridePriceMap.put(orderItemSeqId, "N");
						Map<String,Object> contextTmp = new HashMap<String, Object>();
						contextTmp.put("orderId", orderId);
						contextTmp.put("orderTypeId", "PURCHASE_ORDER");
						contextTmp.put("itemQtyMap", itemQtyMap);
						contextTmp.put("itemPriceMap", itemPriceMap);
						contextTmp.put("itemDescriptionMap", itemDescriptionMap);
						contextTmp.put("itemReasonMap", itemReasonMap);
						contextTmp.put("itemCommentMap", itemCommentMap);
						contextTmp.put("itemAttributesMap", itemAttributesMap);
						contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
						contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
						contextTmp.put("overridePriceMap", overridePriceMap);
						GenericValue admin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"), false);
						contextTmp.put("userLogin", admin);
						//contextTmp.put("userLogin", userLogin);
						try {
//							dispatcher.runSync("updateOrderItemsPOImport", contextTmp);
							dispatcher.runSync("updateOrderItems", contextTmp);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
					if(expireDate != null && datetimeManufactured != null){
						GenericValue orderItemUpdateDate = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
						orderItemUpdateDate.put("productId", productId);
						orderItemUpdateDate.put("expireDate", expireDate);
						orderItemUpdateDate.put("datetimeManufactured", datetimeManufactured);
						delegator.store(orderItemUpdateDate);
					}
//					orderItem.put("quantity", documentQuantity);
				} else {
					orderItem.put("alternativeQuantity", documentQuantity);
				}
			}
			
			else{// append item order if seqItem not exist
				if(shipGroupId != null){
					String shipGroupSeqId = (String)shipGroupId.get("shipGroupSeqId");
					Map<String,Object> contextTmp = new HashMap<String, Object>();
					contextTmp.put("orderId", orderId);
					contextTmp.put("shipGroupSeqId", shipGroupSeqId);
					contextTmp.put("productId", productId);
					contextTmp.put("quantity", documentQuantity);
					contextTmp.put("expireDate", expireDate);
					contextTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("appendOrderItem", contextTmp);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
				
				List<GenericValue> listOrderItems2 = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
				if(!UtilValidate.isEmpty(listOrderItems2)){
					for(GenericValue orItem : listOrderItems2){
						String orderItemSeqIdCheck = (String)orItem.get("orderItemSeqId");
						if(!listOrderItemSeq.contains(orderItemSeqIdCheck)){
							orderItemSeqId = orderItemSeqIdCheck;
							orItem.put("datetimeManufactured", datetimeManufactured);
							orItem.put("expireDate", expireDate);
							delegator.store(orItem);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		result.put("orderId", orderId);
		result.put("orderItemSeqId", orderItemSeqId);
		return result;
	}
	
	public static Map<String, Object> getPackingListByContainer(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		String containerId = (String)context.get("containerId");
		Set<String> fld = FastSet.newInstance();
		Map<String, Object> mapResult = FastMap.newInstance();
		List<GenericValue> listMapAgreement = FastList.newInstance();
//		GenericValue agreementMap = null;
		fld.add("packingListId");
		fld.add("packingListNumber");
		fld.add("purchaseOrderId");
		List<GenericValue> listPackingList = delegator.findList("PackingListHeader", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), fld, null, null, false);
		if(!UtilValidate.isEmpty(listPackingList)){
			for(GenericValue packingList : listPackingList){
				String orderId = (String)packingList.get("purchaseOrderId");
				Map<String,Object> contextTmp = new HashMap<String, Object>();
		        contextTmp.put("orderId", orderId);
		        contextTmp.put("userLogin", userLogin);
				try {
					mapResult = dispatcher.runSync("getAgreementByOrder", contextTmp);
					if(mapResult.containsKey("listAgreement")){
						GenericValue agreementMap = (GenericValue) mapResult.get("listAgreement");
						listMapAgreement.add(agreementMap);
					}
				} catch (GenericServiceException e) {
//						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
				}
			}
		}
		result.put("listAgreement", listMapAgreement);
		result.put("listPackingList", listPackingList);
		return result;
	}
	
	public static Map<String, Object> doSomethingWhenSelectPLNumber(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> resultReturn = FastMap.newInstance();
		String packingListId = (String)context.get("packingListId");
		GenericValue packingListHeader = delegator.findOne("PackingListHeader", UtilMisc.toMap("packingListId", packingListId), false);
		result.put("packingListDate", ((Date)packingListHeader.get("packingListDate")).toString());
		result.put("packingListId", (String)packingListHeader.get("packingListId"));
		result.put("externalOrderNumber", (String)packingListHeader.get("externalOrderNumber"));
		result.put("externalOrderTypeId", (String)packingListHeader.get("externalOrderTypeId"));
		result.put("externalInvoiceNumber", (String)packingListHeader.get("externalInvoiceNumber"));
		result.put("externalInvoiceDate", ((Date)packingListHeader.get("externalInvoiceDate")).toString());
		result.put("netWeightTotal", (BigDecimal)packingListHeader.get("netWeightTotal"));
		result.put("grossWeightTotal", (BigDecimal)packingListHeader.get("grossWeightTotal"));
		GenericValue packingListDetailSum = delegator.findOne("PackingListDetailSum", UtilMisc.toMap("packingListId", packingListId), false);
		Long packingUnit = new Long(0);
		if(packingListDetailSum != null){
			packingUnit = (Long)packingListDetailSum.get("packingUnit");
		}
		result.put("packingUnit", packingUnit);
		resultReturn.put("resultPackingListHeader", result);
//		result.put("packingListHeader", packingListHeader);
		return resultReturn;
	}
	
	public static Map<String, Object> jqxGetPackingListDetail (DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String packingListId = (String)param.get("packingListId")[0];
    	List<GenericValue> listPackingListDetail = delegator.findList("PackingListDetail", EntityCondition.makeCondition(UtilMisc.toMap("packingListId", packingListId)), null, null, null, false);
		result.put("listIterator", listPackingListDetail);
		return result;
	}
	
	public static Map<String, Object> getDocumentCustomsByContainer(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> resultReturn = FastMap.newInstance();
		String containerId = (String)context.get("containerId");
		String documentCustomsTypeId = (String)context.get("documentCustomsTypeId");
		String documentCustomsId="";
		String registerNumber = "";
		String registerDate = "";
		String sampleSendDate = "";
		List<GenericValue> listDocumentCustoms = delegator.findList("DocumentCustoms", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId, "documentCustomsTypeId", documentCustomsTypeId)), null, null, null, false);
		GenericValue documentCustoms = null;
		if(!UtilValidate.isEmpty(listDocumentCustoms)){
			documentCustoms = EntityUtil.getFirst(listDocumentCustoms);
			documentCustomsId = (String)documentCustoms.get("documentCustomsId");
			registerNumber = (String)documentCustoms.get("registerNumber");
			registerDate = ((Date)documentCustoms.get("registerDate")).toString();
			sampleSendDate = ((Date)documentCustoms.get("sampleSendDate")).toString();
		}
		result.put("documentCustomsId", documentCustomsId);
		result.put("registerNumber", registerNumber);
		result.put("registerDate", registerDate);
		result.put("sampleSendDate", sampleSendDate);
		resultReturn.put("resultListDoc", result);
//		result.put("packingListHeader", packingListHeader);
		return resultReturn;
	}
	public static Map<String, Object> updateDocumentCustomsAjax(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String containerId = (String)context.get("containerId");
		String documentCustomsId = (String)context.get("documentCustomsId");
		String documentCustomsTypeId = (String)context.get("documentCustomsTypeId");
		String registerNumber = (String)context.get("registerNumber");
		String registerDate = (String)context.get("registerDate");
		String sampleSendDate = (String)context.get("sampleSendDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String,Object> contextTmp = new HashMap<String, Object>();
        contextTmp.put("containerId", containerId);
        contextTmp.put("documentCustomsId", documentCustomsId);
        contextTmp.put("documentCustomsTypeId", documentCustomsTypeId);
        contextTmp.put("registerNumber", registerNumber);
        contextTmp.put("registerDate", new java.sql.Date(Long.parseLong(registerDate)));
        contextTmp.put("sampleSendDate", new java.sql.Date(Long.parseLong(sampleSendDate)));
        contextTmp.put("userLogin", userLogin);
		try {
			dispatcher.runSync("updateDocumentCustoms", contextTmp);
		} catch (GenericServiceException e) {
//				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		
		return result;
	}
	
	public static Map<String, Object> getAgreementByOrder(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String orderId = (String)context.get("orderId");
		List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		if(!UtilValidate.isEmpty(listAgreementAndOrder)){
			GenericValue agreementAndOrder = EntityUtil.getFirst(listAgreementAndOrder);
			String agreementId = (String)agreementAndOrder.get("agreementId");
			Set<String> fieldToSelects = FastSet.newInstance();
			fieldToSelects.add("agreementId");
	    	fieldToSelects.add("attrValue");
			GenericValue listAgreement = delegator.findOne("AgreementAndAgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
			result.put("listAgreement", listAgreement);
		}
		return result;
	}
	public static Map<String, Object> notifyToImportSpecialist(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String billId = (String)context.get("billId");
		String billNumber = (String)context.get("billNumber");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp departureDate = (Timestamp) context.get("departureDate");
		Timestamp arrivalDate = (Timestamp) context.get("arrivalDate");
//		Date arrivalDateDate = new Date(arrivalDate.getTime());
		String header ="" + UtilProperties.getMessage(resource, "noticeDate", (Locale)context.get("locale")) + " ";
		header += "" +arrivalDate+" " + UtilProperties.getMessage(resource, "goodFollowBill", (Locale)context.get("locale")) + " " +billNumber+" "+UtilProperties.getMessage(resource, "arrivalPort", (Locale)context.get("locale"))+"";
		String headerLast = ""+UtilProperties.getMessage(resource, "goodFollowBillToday", (Locale)context.get("locale"))+" "+billNumber+"";
		List<String> listLogSpecialists = new ArrayList<String>();
		List<String> listPartyGroups = SecurityUtil.getPartiesByRoles("IMPORT_SPECIALIST", delegator);
		if (!listPartyGroups.isEmpty()){
			for (String group : listPartyGroups){
				try {
					List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "IMPORT_SPECIALIST")), null, null, null, false);
					listManagers = EntityUtil.filterByDate(listManagers);
					if (!listManagers.isEmpty()){
						for (GenericValue manager : listManagers){
							listLogSpecialists.add(manager.getString("partyIdFrom"));
						}
					}
				} catch (GenericEntityException e) {
					ServiceUtil.returnError("get Party relationship error!");
				}
			}
		}
		if(!listLogSpecialists.isEmpty()){
			for (String managerParty : listLogSpecialists){
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				String targetLink = "billId="+billId;
				mapContext.put("partyId", sendToPartyId);
				mapContext.put("action", "receiveAgreement");
				mapContext.put("targetLink", targetLink);
				mapContext.put("header", header);
				mapContext.put("userLogin", userLogin);
				Map<String, Object> mapContextLast = new HashMap<String, Object>();
				mapContextLast.put("partyId", sendToPartyId);
				mapContextLast.put("action", "receiveAgreement");
				mapContextLast.put("targetLink", targetLink);
				mapContextLast.put("header", headerLast);
				mapContextLast.put("openTime", arrivalDate);
				mapContextLast.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createNotification", mapContext);
					dispatcher.runSync("createNotification", mapContextLast);
				} catch (GenericServiceException e) {
					ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateNotificationError", (Locale)context.get("locale")));
				}
			}
		}
			result.put("billId", billId);
			return result;
	}
}
