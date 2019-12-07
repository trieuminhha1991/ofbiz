package com.olbius.baselogistics.shippingTrip;

import com.olbius.baselogistics.delivery.DeliveryServices;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShippingTripServices{
  public static final String module = ShippingTripServices.class.getName();
  public static final String resource = "BaseLogisticsUiLabels";
  public static final String resourceCommonEntity = "CommonEntityLabels";
  public static final String OrderEntityLabels = "OrderEntityLabels";
  public static final String resourceError = "BaseLogisticsErrorUiLabels";
  public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
  public static enum TRIP_STATUS {
      TRIP_CREATED,
      TRIP_CANCELED,
      TRIP_CONFIRMED,
      TRIP_CANCELLED,
      TRIP_COMPLETED
  }
  
  @SuppressWarnings("unchecked")
  public static Map<String, Object> createTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
      try {
          Map<String, Object> result = ServiceUtil.returnSuccess();
          Delegator delegator = ctx.getDelegator();
          LocalDispatcher dispatcher = ctx.getDispatcher();
          GenericValue userLogin = (GenericValue) context.get("userLogin");
          String shipperId = (String) context.get("shipperId");
          BigDecimal tripCost = (BigDecimal) context.get("tripCost");
          BigDecimal costCustomerPaid = (BigDecimal) context.get("costCustomerPaid");
          String description = (String) context.get("description");
          String shipBeforeDateTmp = (String) context.get("shipBeforeDate");
          String shipAfterDateTmp = (String) context.get("shipAfterDate");
          String facilityId = (String) context.get("facilityId");
          String originContactMechId = (String) context.get("originContactMechId");
          List<Object> listItemTmp = (List<Object>) context.get("packItems");
          Timestamp shipBeforeDate = null;
          if (UtilValidate.isNotEmpty(shipBeforeDateTmp)) {
              shipBeforeDate = new Timestamp(Long.valueOf(shipBeforeDateTmp));
          }
          Timestamp shipAfterDate = null;
          if (UtilValidate.isNotEmpty(shipAfterDateTmp)) {
              shipAfterDate = new Timestamp(Long.valueOf(shipAfterDateTmp));
          }
          GenericValue trip = delegator.makeValue("ShippingTrip");
          String tripId = delegator.getNextSeqId("ShippingTrip");
          trip.put("shippingTripId", tripId);
          trip.put("shipperId", shipperId);
          trip.put("facilityId", facilityId);
          trip.put("originContactMechId", originContactMechId);
          trip.put("startDateTime", shipBeforeDate);
          trip.put("createdDate",UtilDateTime.nowTimestamp());
          trip.put("finishedDateTime", shipBeforeDate);
          trip.put("tripCost",tripCost);
          trip.put("statusId", TRIP_STATUS.TRIP_CREATED.name());
          trip.put("costCustomerPaid",costCustomerPaid);
          trip.put("description",description);
          try {
              delegator.create(trip);
          } catch (GenericEntityException e) {
              e.printStackTrace();
              return ServiceUtil.returnError(e.getStackTrace().toString());
          }
          // create trip items
          Boolean isJson = false;
          if (!listItemTmp.isEmpty()) {
              if (listItemTmp.get(0) instanceof String) {
                  isJson = true;
              }
          }
          List<Map<String, String>> listPacks = new ArrayList<Map<String, String>>();
          if (isJson) {
              String stringJson = "[" + (String) listItemTmp.get(0) + "]";
              JSONArray lists = JSONArray.fromObject(stringJson);
              for (int i = 0; i < lists.size(); i++) {
                  HashMap<String, String> mapItem = new HashMap<String, String>();
                  JSONObject item = lists.getJSONObject(i);
                  if (item.containsKey("packId")) {
                      mapItem.put("packId", item.getString("packId"));
                  }
                  listPacks.add(mapItem);
              }
          } else {
              listPacks = (List<Map<String, String>>) context.get("packItems");
          }
          for (Map<String, String> item : listPacks) {
              String packId = item.get("packId");
              GenericValue tripPack = delegator.makeValue("ShippingTripPack");
              tripPack.put("shippingTripId", tripId);  
              tripPack.put("packId", packId);
              tripPack.put("statusId", "SHIP_PACK_CREATED");
              try {
                delegator.create(tripPack);
              } catch(GenericEntityException e) {
                return ServiceUtil.returnError("OLBIUS: Create Pack item error");
              }
              // Update status of Pack
              GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
              String statusPack = "PACK_CREATED";
              pack.set("statusId",statusPack);
              try {
                pack.store();
              } catch(GenericEntityException e) {
                return ServiceUtil.returnError("OLBIUS: Update Pack's status error");
              }  
          }
          result.put("tripId", tripId);
          return result;
      } catch (Exception e) {
          return ServiceUtil.returnError(e.getStackTrace().toString());
      }
  }
  
  public static Map<String, Object> JQGetListPackByTripId(DispatchContext ctx, Map<String, ? extends Object> context) {
      Delegator delegator = ctx.getDelegator();
      List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
      List<String> listSortFields = (List<String>) context.get("listSortFields");
      EntityFindOptions opts = (EntityFindOptions) context.get("opts");
      Map<String, String> mapCondition = new HashMap<String, String>();
      Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
      Map<String, Object> successResult = ServiceUtil.returnSuccess();
      List<GenericValue> listItems = null;
      List<GenericValue> listPacks = null;
      String shippingTripId = (String) parameters.get("shippingTripId")[0];
          EntityCondition filterByTripId = EntityCondition.makeCondition("shippingTripId", EntityOperator.LIKE, shippingTripId);
      listAllConditions.add(filterByTripId);
      listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, DeliveryServices.ShippingTripPackStatusEnum.SHIP_PACK_CANCELLED.name()));
      
      try {
          listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ShippingTripPackOrderDeliveryPackSummaryView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
      } catch (GenericEntityException e) {
          String errMsg = "Fatal error calling JQGetListPack service: " + e.toString();
          Debug.logError(e, errMsg, module);
      }

      successResult.put("listIterator", listItems);
      return successResult;
  }
  @SuppressWarnings("unchecked")
  public static Map<String, Object> updateShippingTripStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
      try {
          Map<String, Object> result = ServiceUtil.returnSuccess();
          Delegator delegator = ctx.getDelegator();
          LocalDispatcher dispatcher = ctx.getDispatcher();
          GenericValue userLogin = (GenericValue) context.get("userLogin");
          String shippingTripId = (String) context.get("shippingTripId");
          List<Object> listPackStatusTmp = (List<Object>) context.get("listPackStatus");
          // create trip items
          Boolean isJson = false;
          if (!listPackStatusTmp.isEmpty()) {
              if (listPackStatusTmp.get(0) instanceof String) {
                  isJson = true;
              }
          }
          
          List<Map<String, String>> listPackStatus = new ArrayList<Map<String, String>>();
          if (isJson) {
              String stringJson = "[" + (String) listPackStatusTmp.get(0) + "]";
              JSONArray lists = JSONArray.fromObject(stringJson);
              for (int i = 0; i < lists.size(); i++) {
                  HashMap<String, String> mapItem = new HashMap<String, String>();
                  JSONObject item = lists.getJSONObject(i);
                  if (item.containsKey("packId") && item.containsKey("packStatusId")) {
                      mapItem.put("packId", item.getString("packId"));
                      mapItem.put("packStatusId", item.getString("packStatusId"));
                  }
                  listPackStatus.add(mapItem);
              }
          } else {
              listPackStatus = (List<Map<String, String>>) context.get("listPackStatus");
          }
          for (Map<String, String> item : listPackStatus) {
              String packId = item.get("packId");
              String packStatusId = item.get("packStatusId");
              String tripPackStatusId ;
              if(packStatusId.equals("PACK_CANCELLED")){
                tripPackStatusId = "SHIP_PACK_FAIL";
              }else {
                tripPackStatusId = "SHIP_PACK_COMPLETED";
              }
              // Update status of Pack
              GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
              GenericValue tripPack = delegator.findOne("ShippingTripPack", false, UtilMisc.toMap("packId", packId, "shippingTripId", shippingTripId));
              pack.set("statusId",packStatusId);
              tripPack.set("statusId",tripPackStatusId);
              try {
                pack.store();
                tripPack.store();
              } catch(GenericEntityException e) {
                e.printStackTrace();
                return ServiceUtil.returnError("OLBIUS: Update Pack's status error");
              }
          }
          GenericValue trip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
          String shippingTripStatusId = TRIP_STATUS.TRIP_COMPLETED.name();
          trip.set("statusId",shippingTripStatusId);
          result.put("shippingTripId", shippingTripId);
          try {
            trip.store();
          } catch(Exception e) {
              e.printStackTrace();
            return ServiceUtil.returnError(e.getStackTrace().toString());
          }
          return result;
      } catch (Exception e) {
          e.printStackTrace();
        return ServiceUtil.returnError(e.getStackTrace().toString());
      }
  }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> changeShippingTripStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String shippingTripId = (String) context.get("shippingTripId");
        String statusId = (String) context.get("statusId");
        try {
            GenericValue trip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
            trip.set("statusId", statusId);
            trip.store();
            if (statusId.equals("TRIP_CANCELLED")) {
                String shippingTripPackStatusId = DeliveryServices.ShippingTripPackStatusEnum.SHIP_CANCELLED.name();
                String packStatus = "PACK_CANCELLED";
                List<EntityCondition> condsToFindPack = new ArrayList<EntityCondition>();
                condsToFindPack.add(EntityCondition.makeCondition("shippingTripId", EntityOperator.LIKE, shippingTripId));
                List<GenericValue> listTripPacks = new ArrayList<GenericValue>();
                try {
                    listTripPacks = delegator.findList("ShippingTripPack", EntityCondition.makeCondition(condsToFindPack), null, null, null, false);
                    for (GenericValue item : listTripPacks) {
                        try {
                            String packId = item.getString("packId");
                            GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
                            GenericValue tripPack = delegator.findOne("ShippingTripPack", false, UtilMisc.toMap("packId", packId, "shippingTripId", shippingTripId));
                            pack.set("statusId", packStatus);
                            tripPack.set("statusId", shippingTripPackStatusId);
                            List<GenericValue> listPackItems = delegator.findList("PackItem", EntityCondition.makeCondition(UtilMisc.toMap("packId", packId)), null, null, null, false);
                            for(int ii=0;ii<listPackItems.size();ii++) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("deliveryId", listPackItems.get(ii).get("deliveryId"));
                                map.put("statusId", DeliveryServices.DeliveryStatusEnum.DLV_CANCELLED.name());
                                map.put("userLogin", userLogin);
                                dispatcher.runSync("changeDeliveryStatus", map);
                            }
                            pack.store();
                            tripPack.store();
                        } catch (Exception e) {
                            return ServiceUtil.returnError(e.getStackTrace().toString());
                        }
                    }
                } catch (Exception e) {
                    return ServiceUtil.returnError(e.getStackTrace().toString());
                }
            } else {
            }
            result.put("shippingTripId", shippingTripId);
            return result;
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getStackTrace().toString());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> JQGetListOrderByCluster(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listToResult = FastList.newInstance();
        List<Map<String, Object>> orderByCluster = FastList.newInstance();
      try {
          List<EntityCondition> conditionsDeliveryAvailable = FastList.newInstance();
          EntityCondition condStatusDC = EntityCondition.makeCondition("statusId", "DELIVERY_CLUSTER_ENABLED");
          conditionsDeliveryAvailable.add(condStatusDC);
//          List<GenericValue> listClusterAvailable =  delegator.findList("DeliveryCluster", EntityCondition.makeCondition(conditionsDeliveryAvailable),null , null, opts, false);
          List<GenericValue> listClusterAvailable =  delegator.findList("OrderAndClusterDataSheetClusterDistinct", EntityCondition.makeCondition(listAllConditions),null , null, opts, false);
          List<GenericValue> listClusterAndOrderDataSheet =  delegator.findList("OrderAndClusterDataSheet", EntityCondition.makeCondition(listAllConditions),null , null, opts, false);
          for ( GenericValue cluster: listClusterAvailable){
              Map<String, Object> tempData = FastMap.newInstance();
              List<GenericValue> clusterAndOrdersGroupByStatus =  EntityUtil.filterByCondition(listClusterAndOrderDataSheet, EntityCondition.makeCondition(UtilMisc.toMap("deliveryClusterId",cluster.getString("deliveryClusterId"))) );
              tempData.put("shipperId", cluster.getString("shipperId"));
              tempData.put("deliveryClusterId", cluster.getString("deliveryClusterId"));
              tempData.put("shipperName", cluster.getString("shipperName"));
              List<GenericValue> clusterAndOrdersGroupByStatusTemp =  EntityUtil.filterByCondition(clusterAndOrdersGroupByStatus, EntityCondition.makeCondition(UtilMisc.toMap("statusId","PACK_DELIVERED")) );
              int amountOfCompletedOrder = clusterAndOrdersGroupByStatusTemp.size();
              tempData.put("amountOfCompletedOrder",amountOfCompletedOrder );
              clusterAndOrdersGroupByStatusTemp =  EntityUtil.filterByCondition(clusterAndOrdersGroupByStatus, EntityCondition.makeCondition(UtilMisc.toMap("statusId","PACK_BEING_DLIED")) );
              int amountOfRemainOrder = clusterAndOrdersGroupByStatusTemp.size();
              tempData.put("amountOfRemainOrder", amountOfRemainOrder );
              List<EntityCondition> conditionsOrder = FastList.newInstance();
              EntityCondition orderWasNotBeingDlied = EntityCondition.makeCondition("statusId","PACK_CREATED");
              EntityCondition orderWasNotInTrip = EntityCondition.makeCondition("statusId",null);
              conditionsOrder.add(orderWasNotBeingDlied);
              conditionsOrder.add(orderWasNotInTrip);
              clusterAndOrdersGroupByStatusTemp =  EntityUtil.filterByCondition(clusterAndOrdersGroupByStatus, EntityCondition.makeCondition(conditionsOrder, EntityOperator.OR) );
              tempData.put("amountOfOrderInProcessed", clusterAndOrdersGroupByStatusTemp.size() );
//                clusterAndOrdersGroupByStatusTemp = EntityUtil.filterByCondition(clusterAndOrdersGroupByStatus, EntityCondition.makeCondition(UtilMisc.toMap("statusId","PACK_CANCELLED", "tripPackStatusId", "")) );
//                int amountOfOrder = clusterAndOrdersGroupByStatusTemp.size();
//                tempData.put("amountOfOrder",amountOfOrder );
              List<EntityCondition> conditionsDelivery = FastList.newInstance();
              EntityCondition condDeliveyId = EntityCondition.makeCondition("deliveryClusterId",cluster.getString("deliveryClusterId"));
              conditionsDelivery.add(condDeliveyId);
              int amountOfOrder = 0;
              List<GenericValue> tempDeliveryAmount = delegator.findList("OrderAndClusterDataSheetClusterDistinctOrder", EntityCondition.makeCondition(conditionsDelivery), null, null,opts, false );
              if ( tempDeliveryAmount != null){
                  amountOfOrder = Integer.parseInt(tempDeliveryAmount.get(0).getString("amountOfOrder"));
              }
              tempData.put("amountOfOrder",amountOfOrder );
              orderByCluster.add(tempData);
            }
          successResult.put("listIterator", orderByCluster);
      } catch (GenericEntityException e) {
          String errMsg = "Fatal error calling JQGetListOrderByCluster service: " + e.toString();
          Debug.logError(e, errMsg, module);
      }
      return successResult;
  }
}





