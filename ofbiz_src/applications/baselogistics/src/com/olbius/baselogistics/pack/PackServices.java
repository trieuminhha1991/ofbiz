package com.olbius.baselogistics.pack;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.net.ntp.TimeStamp;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class PackServices {

    public static final String module = PackServices.class.getName();
    public static final String resource = "BaseLogisticsUiLabels";
    public static final String resourceCommonEntity = "CommonEntityLabels";
    public static final String OrderEntityLabels = "OrderEntityLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    public static enum PACK_ITEM_STATUS {
        PACK_ITEM_CREATED,
        PACK_ITEM_BEING_DLIED,
        PACK_ITEM_CANCELLED,
        PACK_ITEM_DELIVERED
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createPack(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
            Map<String, Object> result = ServiceUtil.returnSuccess();
            Delegator delegator = ctx.getDelegator();
            LocalDispatcher dispatcher = ctx.getDispatcher();
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String customerId = (String) context.get("customerId");
            String destContactMechId = (String) context.get("destContactMechId");
            String description = (String) context.get("description");
            String shipBeforeDateStr = (String) context.get("shipBeforeDate");
            String shipAfterDateStr = (String) context.get("shipAfterDate");
            List<Object> listItemTmp = (List<Object>) context.get("listProducts");
            //String packId = (String) context.get("packId");
            Timestamp shipBeforeDate = null;
            if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
                shipBeforeDate = new Timestamp(Long.valueOf(shipBeforeDateStr));
            }
            Timestamp shipAfterDate = null;
            if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
                shipAfterDate = new Timestamp(Long.valueOf(shipAfterDateStr));
            }
            GenericValue pack = delegator.makeValue("Pack");
            //if (UtilValidate.isEmpty(packId)){
            String packId = delegator.getNextSeqId("Pack");
            // }
            pack.put("packId", packId);
            pack.put("partyIdTo", customerId);
            pack.put("shipAfterDate", shipAfterDate);
            pack.put("shipBeforeDate", shipBeforeDate);
            pack.put("destContactMechId", destContactMechId);
            pack.put("createdDate", UtilDateTime.nowTimestamp());
            pack.put("createdByUserLogin", userLogin.get("userLoginId"));
            pack.put("statusId", "PACK_CREATED");
            try {
                delegator.create(pack);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getStackTrace().toString());
            }
            // create pack items

            Boolean isJson = false;
            if (!listItemTmp.isEmpty()) {
                if (listItemTmp.get(0) instanceof String) {
                    isJson = true;
                }
            }

            List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
            if (isJson) {
                String stringJson = "[" + (String) listItemTmp.get(0) + "]";
                JSONArray lists = JSONArray.fromObject(stringJson);
                for (int i = 0; i < lists.size(); i++) {
                    HashMap<String, Object> mapItem = new HashMap<String, Object>();
                    JSONObject item = lists.getJSONObject(i);
                    if (item.containsKey("productId")) {
                        mapItem.put("productId", item.getString("productId"));
                    }
                    // quantity in case using amount it will be weight
                    if (item.containsKey("quantity")) {
                        mapItem.put("quantity", new BigDecimal(item.getString("quantity")));
                    }
                    if (item.containsKey("quantityUomId")) {
                        mapItem.put("quantityUomId", item.getString("quantityUomId"));
                    }
                    if (item.containsKey("weightUomId")) {
                        mapItem.put("weightUomId", item.getString("weightUomId"));
                    }
                    if (item.containsKey("deliveryId")) {
                        mapItem.put("deliveryId", item.getString("deliveryId"));
                    }
                    if (item.containsKey("deliveryItemSeqId")) {
                        mapItem.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
                    }

                    listProducts.add(mapItem);
                }
            } else {
                listProducts = (List<Map<String, Object>>) context.get("listProducts");
            }
            HashSet<String> set = new HashSet<>();
            for (Map<String, Object> item : listProducts) {
                String productId = (String) item.get("productId");
                BigDecimal quantity = (BigDecimal) item.get("quantity");
                String quantityUomId = (String) item.get("quantityUomId");
                String weightUomId = (String) item.get("weightUomId");

                String deliveryId = (String) item.get("deliveryId");
                set.add(deliveryId);
                String deliveryItemSeqId = (String) item.get("deliveryItemSeqId");
                GenericValue packItem = delegator.makeValue("PackItem");
                packItem.put("packId", packId);
                delegator.setNextSubSeqId(packItem, "packItemSeqId", 5, 1);
                packItem.put("productId", productId);
                packItem.put("quantityUomId", quantityUomId);
                packItem.put("deliveryId", deliveryId);
                packItem.put("deliveryItemSeqId", deliveryItemSeqId);
                packItem.put("statusId", PACK_ITEM_STATUS.PACK_ITEM_CREATED.name());
                try {
                    GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
                    GenericValue objDelivery = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
                    BigDecimal packedQuantity = objDelivery.getBigDecimal("packedQuantity") == null ? new BigDecimal(0) : objDelivery.getBigDecimal("packedQuantity");
                    BigDecimal qOH = objDelivery.getBigDecimal("quantity");


                    //if (packedQuantity.add(new BigDecimal(quantity)).compareTo(qOH) > 0)
                    //throw new GenericEntityException("Quantity must left than equal QOH");
                    packItem.put("quantity", qOH);
                    delegator.create(packItem);
                    objDelivery.set("packedQuantity", packedQuantity.add(qOH));
                    objDelivery.store();

                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("OLBIUS: Create Pack item error");
                }
            }
            for (String x : set) {
                GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", x));
                delivery.set("isPacked", "Y");
                delivery.store();
            }
            result.put("packId", packId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }
    }

    public static Map<String, Object> JQGetListPackItem(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String> mapCondition = new HashMap<String, String>();
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PACK_ITEM_CANCELLED", "PACK_ITEM_REJECTED"));
        listAllConditions.add(statusConds);
        if (parameters.get("packId") != null && parameters.get("packId").length > 0) {
            String packId = (String) parameters.get("packId")[0];
            if (packId != null && !"".equals(packId)) {
                mapCondition.put("packId", packId);
            }
            EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
            listAllConditions.add(tmpConditon);

        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> listItems = null;
        try {
            listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PackItemAndProductIdView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling getListpackItem service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }

        successResult.put("listIterator", listItems);
        return successResult;
    }

    public static Map<String, Object> JQGetListPack(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String> mapCondition = new HashMap<String, String>();
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> listItems = null;
        if(parameters.get("packTypeId") != null){
            EntityCondition filterByStatus = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PACK_CREATED", "PACK_OUT_TRIP"));
            listAllConditions.add(filterByStatus);
        }
        try {
            listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "Pack", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "Fatal error calling JQGetListPack service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }

        successResult.put("listIterator", listItems);
        return successResult;
    }

    @SuppressWarnings("unused")
    public static Map<String, Object> updatePackAndItem(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> returnResult = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String packId = (String) context.get("packId");
        Long shipBeforeDate = Long.parseLong((String) context.get("shipBeforeDate"));
        Long shipAfterDate = Long.parseLong((String) context.get("shipAfterDate"));
        String description = (String) context.get("description");
        JSONArray listProducts = JSONArray.fromObject(context.get("listProducts"));

        try {
            Map<String, Object> packInfoMap = context;
            packInfoMap.remove("listProducts");
            packInfoMap.put("userLogin", userLogin);
            dispatcher.runSync("updatePackInfo", packInfoMap);
        } catch (GenericServiceException e) {
            String errMsg = "OLBIUS: Fatal error when run Service updatePackInfo: " + e.toString();
            Debug.logError(e, errMsg, module);
            e.printStackTrace();
            return ServiceUtil.returnError(errMsg);
        }

        List<Map<String, Object>> productArr = FastList.newInstance();
        for (int i = 0; i < listProducts.size(); i++) {
            JSONObject objectJSON = JSONObject.fromObject(listProducts.get(i));
            Map<String, Object> product = new HashMap<String, Object>();
            product.put("userLogin", userLogin);
            product.put("deliveryId", objectJSON.getString("deliveryId"));
            product.put("deliveryItemSeqId", objectJSON.getString("deliveryItemSeqId"));
            product.put("productId", objectJSON.get("productId"));
            if (objectJSON.containsKey("quantity")) {
                /*if (!objectJSON.getString("quantity").equals("null")) {


                    //packItem.put("quantity", new BigDecimal(quantity));
                    product.put("quantity", BigDecimal.valueOf(Double.parseDouble(objectJSON.get("quantity").toString())));
                }*/
            }
            product.put("quantityUomId", objectJSON.get("uomId"));
            product.put("packId", packId);
            /*if (UtilValidate.isNotEmpty(objectJSON.get("statusId"))) {
                product.put("statusId", objectJSON.get("statusId"));
            }*/
            productArr.add(product);
        }
        try {
            List<GenericValue> listPackItems = delegator.findList("PackItem", EntityCondition.makeCondition("packId", packId), null, null, null, false);
            HashSet<String> setPackItemsOld = new HashSet<>();
            for (int i = 0; i < listPackItems.size(); i++) {
                setPackItemsOld.add((String) listPackItems.get(i).get("deliveryId"));
            }

            HashSet<String> setPackItemsNew = new HashSet<>();
            for (int i = 0; i < productArr.size(); i++) {
                setPackItemsNew.add((String) productArr.get(i).get("deliveryId"));
            }
            for (Map<String, Object> product : productArr) {
                if(!setPackItemsOld.contains(product.get("deliveryId"))) {
                    product.put("statusId", PACK_ITEM_STATUS.PACK_ITEM_CREATED.name());
                    dispatcher.runSync("addPackItem", product);
                }
            }
            for (GenericValue packItem : listPackItems) {
                if(!setPackItemsNew.contains(packItem.get("deliveryId"))) {
                    packItem.put("statusId", PACK_ITEM_STATUS.PACK_ITEM_CANCELLED.name());
                    packItem.store();
                }
            }
            for(String deliveryId : setPackItemsOld){
                if(!setPackItemsNew.contains(deliveryId)){
                    GenericValue objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
                    objDelivery.put("isPacked","N");
                    objDelivery.store();
                }
            }
            for(String deliveryId : setPackItemsNew){
                if(!setPackItemsNew.contains(deliveryId)){
                    GenericValue objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
                    objDelivery.put("isPacked","Y");
                    objDelivery.store();
                }
            }
        } catch (GenericServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        returnResult.put("packId", packId);

        return returnResult;
    }

    @SuppressWarnings("unused")
    public static Map<String, Object> updatePackInfo(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> returnResult = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String packId = (String) context.get("packId");
        Long shipBeforeDate = Long.parseLong((String) context.get("shipBeforeDate"));
        Long shipAfterDate = Long.parseLong((String) context.get("shipAfterDate"));
        String description = (String) context.get("description");

        try {
            GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
            if (UtilValidate.isNotEmpty(pack)) {
                pack.set("shipBeforeDate", new Timestamp(shipBeforeDate));
                pack.set("shipAfterDate", new Timestamp(shipAfterDate));
                if (UtilValidate.isNotEmpty(description))
                    pack.set("description", description);
                delegator.store(pack);
                new TimeStamp(shipAfterDate);
            }
        } catch (GenericEntityException e) {
            String errMsg = "OLBIUS: Fatal error when run Service updatePackInfo: " + e.toString();
            Debug.logError(e, errMsg, module);
            e.printStackTrace();
            return ServiceUtil.returnError(errMsg);
        }
        returnResult.put("packId", packId);
        return returnResult;
    }

    public static Map<String, Object> updatePackItem(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> returnResult = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        @SuppressWarnings("unused")
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String packId = (String) context.get("packId");
        String sequenceId = (String) context.get("sequenceId");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal amount = (BigDecimal) context.get("amount");
        String quantityUomId = (String) context.get("quantityUomId");
        String weightUomId = (String) context.get("weightUomId");
        String productId = (String) context.get("productId");
        String statusId = (String) context.get("statusId");

        try {

            if (sequenceId == null) {
                try {
                    context.remove("sequenceId");
                    dispatcher.runSync("addPackItem", context);
                } catch (GenericServiceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                GenericValue product = delegator.findOne("PackItem", false, UtilMisc.toMap("packId", packId, "packItemSeqId", sequenceId));
                BigDecimal oldQuantity = product.getBigDecimal("quantity");
                if (UtilValidate.isNotEmpty(product)) {
                    GenericValue objDelivery = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", context.get("deliveryId"), "deliveryItemSeqId", context.get("deliveryItemSeqId")));
                    BigDecimal packedQuantity = objDelivery.getBigDecimal("packedQuantity") == null ? new BigDecimal(0) : objDelivery.getBigDecimal("packedQuantity");
                    BigDecimal qOH = objDelivery.getBigDecimal("quantity");
                    if (packedQuantity.add(new BigDecimal(product.get("quantity").toString())).subtract((BigDecimal) product.get("quantity")).compareTo(qOH) > 0)

                        throw new GenericEntityException("Quantity must left than equal QOH");
                    product.put("quantity", quantity);
                    product.put("quantityUomId", quantityUomId);
                    product.put("productId", productId);
                    product.put("statusId", statusId);
                    product.store();
                    objDelivery.set("packedQuantity", packedQuantity.subtract(oldQuantity).add(quantity));
                    objDelivery.store();
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "OLBIUS: Fatal error when run Service updatePackInfo: " + e.toString();
            Debug.logError(e, errMsg, module);
            e.printStackTrace();
            return ServiceUtil.returnError(errMsg);
        }
        returnResult.put("packId", packId);

        return returnResult;
    }

    public static Map<String, Object> addPackItem(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> returnResult = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        @SuppressWarnings("unused")
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String packId = (String) context.get("packId");
        GenericValue item = delegator.makeValue("PackItem");

        item.put("packId", packId);
        delegator.setNextSubSeqId(item, "packItemSeqId", 5, 1);
        GenericValue objDelivery = null;
        try {
            objDelivery = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", context.get("deliveryId"), "deliveryItemSeqId", context.get("deliveryItemSeqId")));

            BigDecimal packedQuantity = objDelivery.getBigDecimal("packedQuantity") == null ? new BigDecimal(0) : objDelivery.getBigDecimal("packedQuantity");
            BigDecimal qOH = objDelivery.getBigDecimal("quantity");


            /*if (packedQuantity.add(new BigDecimal(context.get("quantity").toString())).compareTo(qOH) > 0)

                throw new GenericEntityException("Quantity must left than equal QOH");*/
            item.put("quantity", qOH);
            item.put("quantityUomId", context.get("quantityUomId"));
            item.put("statusId", PACK_ITEM_STATUS.PACK_ITEM_CREATED.name());
            item.put("productId", context.get("productId"));
            item.put("deliveryId", context.get("deliveryId"));
            item.put("deliveryItemSeqId", context.get("deliveryItemSeqId"));
            item.create();
            //objDelivery.set("packedQuantity", packedQuantity.add(new BigDecimal(context.get("quantity").toString())));
            objDelivery.set("packedQuantity", qOH);
            objDelivery.store();
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String errMsg = "Fatal error calling addPackItem service: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError("OLBIUS: addPackItem error! " + e.toString());
        }
        returnResult.put("packId", packId);

        return returnResult;
    }

    public static Map<String, Object> setPackStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String packId = (String) context.get("packId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        GenericValue pack = null;
        try {
            pack = delegator.findOne("Pack", UtilMisc.toMap("packId", packId), false);

            if (pack == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "PackCannotBeFound", locale));
            }

            if (pack.getString("statusId").equals(statusId)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CannotChangeStatusWithTheSameStatusId", locale));
            }
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", pack.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CannotChangeBetweenTwoStatus", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            pack.set("statusId", statusId);

            pack.store();

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return successResult;
    }
    
    // Get list pack which a shipper 
    public static Map<String, Object> JQGetListPackByShipper(DispatchContext ctx, Map<String, ? extends Object> context) {
      Delegator delegator = ctx.getDelegator();
      List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
      List<String> listSortFields = (List<String>) context.get("listSortFields");
      EntityFindOptions opts = (EntityFindOptions) context.get("opts");
      Map<String, String> mapCondition = new HashMap<String, String>();
      Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
      Map<String, Object> successResult = ServiceUtil.returnSuccess();
      List<GenericValue> listItems = null;
      if(parameters.get("packTypeId") != null){
          EntityCondition filterByStatus = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PACK_CREATED", "PACK_OUT_TRIP"));
          listAllConditions.add(filterByStatus);
      }
      if(parameters.get("shipperPartyId") != null){
          String executorId = (String)parameters.get("shipperPartyId")[0];
          EntityCondition filterByShipper = EntityCondition.makeCondition("executorId", EntityOperator.LIKE, UtilMisc.toList(executorId));
          listAllConditions.add(filterByShipper);
      }
      try {
          listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "DeliveryClusterCutomerAndPack", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
      } catch (GenericEntityException e) {
          String errMsg = "Fatal error calling JQGetListPack service: " + e.toString();
          Debug.logError(e, errMsg, module);
      }

      successResult.put("listIterator", listItems);
      return successResult;
    }
}