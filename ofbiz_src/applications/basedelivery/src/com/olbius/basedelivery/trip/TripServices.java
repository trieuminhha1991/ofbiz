package com.olbius.basedelivery.trip;

import com.olbius.basedelivery.DeliveryWorker;
import com.olbius.basesales.order.OrderWorker;
import com.olbius.security.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

;

/**
 * Created by user on 11/30/17.
 */
public class TripServices {
    public static final String module = TripServices.class.getName();
    public static final String resource_error = "BaseSalesErrorUiLabels";

    public static Map<String, Object> getContactMechName(DispatchContext ctx, Map<String, ? extends Object> context) {
        String contactMechId = (String) context.get("contactMechId");
        String fullName = null;
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue mech = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", contactMechId), false);
            fullName = (String) mech.get("fullName");
        } catch (GenericEntityException e) {
            ServiceUtil.returnError(e.toString());
        }
        Map<String, Object> result = FastMap.newInstance();
        result.put("fullName", fullName);
        return result;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> editTrip(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String tripId = (String) context.get("tripId");
        String vehicleId = (String) context.get("vehicleId");
//        String driverId = (String) context.get("driverId");
        String vehicleName = (String) context.get("vehicleName");
        String statusId = (String) context.get("statusId");
        try {
            GenericValue trip = delegator.findOne("Trip", UtilMisc.toMap("tripId", tripId), false);
            trip.set("vehicleId", vehicleId);
//            trip.set("driverId", driverId);
//            trip.set("vehicleName", vehicleName);
            trip.set("statusId", statusId);
            delegator.store(trip);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("CannotVerify");
        }
        successResult.put("tripId", tripId);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createTrip(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
//
//        Security security = ctx.getSecurity();
//        if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_NEW")) {
//            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
//        }
//
        List<String> deliveryList = (List<String>) context.get("deliveryList");
        String description = (String) context.get("description");
        String requirementStartDateStr = (String) context.get("requirementStartDate");
        String contractorId = (String) context.get("contractorId");
        String vehicleId = (String) context.get("vehicleId");
//        String vehicleName = (String) context.get("vehicleName");
        BigDecimal totalWeight = new BigDecimal(context.get("totalWeight").toString());
//
        Timestamp tripStartDate = com.olbius.common.util.BaseUtil.convertDateStrToDate(requirementStartDateStr);
        if (tripStartDate == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tripStartDate.getTime());

        List<String> errorMessageList = FastList.newInstance();
        if (UtilValidate.isEmpty(requirementStartDateStr)) {
            errorMessageList.add(UtilProperties.getMessage(resource_error, "BSFromDateMustNotBeEmpty", locale));
        }

        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        if (UtilValidate.isEmpty(deliveryList)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
        }

        String tripId = "TRIP" + delegator.getNextSeqId("Trip");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        GenericValue trip = delegator.makeValue("Trip", UtilMisc.<String, Object>toMap(
                "tripId", tripId,
                "tripNo", "tripNo",
                "scLogId", userLogin.get("partyId"), //Công ty sử dụng dịch vụ, thông thường sẽ = partyId - người tạo
                "contractorId",contractorId, //Công ty vận chuyển - phải tự chọn
                "vehicleId", vehicleId, //Xe tải - có thể trống, trong bước verify thì cần phải điền
//                "vehicleName", vehicleName,
                "driverId", null, //Tài xế - có thể trống, trong bước verify thì cần phải điền
                "tripAmount", BigDecimal.ZERO, //Tổng tiền chuyến đi - tripAmount hoặc là sẽ link với 1 chứng từ nào đó
                "description", description, //Mô tả
                "statusId", "TRIP_CREATED", //Trạng thái
                "createdDate", nowTimestamp, //Ngày tạo
                "totalWeight", totalWeight,
                "tripStartDate", tripStartDate, //Ngày bắt đầu chạy xe
                "createdByUserLogin", userLogin.get("userLoginId"))); //Người tạo
        try {
            delegator.create(trip);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create Trip entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }

        // update delivery status
        if(!DeliveryWorker.updateDeliveryStatus(delegator, deliveryList)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }

        if(!TripWorker.createTripDetail(delegator, tripId, deliveryList)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }

        try {
            com.olbius.basesales.util.NotificationWorker.sendNotifyWhenCreateTrip(delegator, dispatcher, locale, tripId, userLogin);
        } catch (GenericEntityException | GenericServiceException e) {
            Debug.logWarning(e, "Cannot send notification when create OrderRequirementCommitment; problems with insert", module);
        }

        successResult.put("tripId", tripId);
        return successResult;
    }

    @SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jqGetListDeliveryByTrip(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();
//        try {
//            DeliveryWorker.getPricePetrolium();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        //EntityListIterator listIterator = null;
        List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        try {
            //check permission for each order type
            boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_VIEW");
            if (!hasPermission) {
                Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
                //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
                return successResult;
            }

            String tripId = null;
            if (parameters.containsKey("tripId") && parameters.get("tripId").length > 0) {
                tripId = parameters.get("tripId")[0];
            }

            if (UtilValidate.isNotEmpty(tripId)) {
                List<GenericValue> listTripDetail = getListTripItemByTripId(delegator, tripId);
                for(GenericValue tripDetail : listTripDetail) {
                    String deliveryId = (String) tripDetail.get("deliveryId");
                    GenericValue delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
                    BigDecimal totalWeight = TripWorker.getWeightInDelivery(delegator, dispatcher, deliveryId);

                    Map<String, Object> deliveryFinal = FastMap.newInstance();
                    deliveryFinal.putAll(delivery);
                    deliveryFinal.put("totalWeight", totalWeight);

                    listIterator.add(deliveryFinal);
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListDeliveryByTrip service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jqGetListItemInTrip(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        try {
//            //check permission for each order type
//            boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_VIEW");
//            if (!hasPermission) {
//                Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
//                //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
//                return successResult;
//            }

            String tripId = null;
            if (parameters.containsKey("tripId") && parameters.get("tripId").length > 0) {
                tripId = parameters.get("tripId")[0];
            }

            if (UtilValidate.isNotEmpty(tripId)) {
                listAllConditions.add(EntityCondition.makeCondition("tripId", tripId));
                List<GenericValue> tripItemDetails = delegator.findList("TripItemDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
                for(int i = 0 ; i < tripItemDetails.size(); i ++) {
                    GenericValue tripItem = tripItemDetails.get(i);
                    Map<String, Object> item = FastMap.newInstance();
                    item.put("productId", tripItem.get("productId"));
                    item.put("tripId", tripItem.get("tripId"));
                    item.put("exportedQuantity", tripItem.get("exportedQuantity"));
                    item.put("productName", tripItem.get("productName"));
                    String productId = (String) item.get("productId");
                    BigDecimal quantity = (BigDecimal) item.get("exportedQuantity");
                    BigDecimal totalWeight = OrderWorker.getTotalWeightProduct(delegator, dispatcher, productId, quantity);
                    totalWeight = totalWeight.setScale(2, BigDecimal.ROUND_HALF_UP);
                    item.put("totalWeight", totalWeight);
                    listIterator.add(item);
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqGetListOrderItemReqDelivery service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTrip(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator del = ctx.getDelegator();
        List<GenericValue> listIterator = FastList.newInstance();
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            listSortFields.add("createdDate DESC");
            EntityCondition tmpCondition = EntityCondition.makeCondition(listAllConditions);
            listIterator = del.findList("Trip", tmpCondition, null, listSortFields, opts, false);
        } catch (Exception e) {
            e.printStackTrace();
            successResult = ServiceUtil.returnError("error");
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static List<GenericValue> getListTripItemByTripId(Delegator delegator, String tripId) {
        EntityCondition condition = EntityCondition.makeCondition("tripId", EntityOperator.EQUALS, tripId);
        try {
            return delegator.findList("TripDetail", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
