package com.olbius.salesmtl.distributor;

import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/22/18.
 */
public class PartyDistributorServices {
    public static final String module = PartyDistributorServices.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createPartyDistributor(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        String partyId =(String) context.get("partyId");
        String supervisorId = (String) context.get("supervisorId");
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        GenericValue partyGroup = delegator.findOne("PartyGroup" , UtilMisc.toMap("partyId", partyId), false);
        GenericValue partyDistributor = delegator.makeValue("PartyDistributor");
        partyDistributor.set("partyId", partyId);
        partyDistributor.set("partyCode", party.get("partyCode"));
        partyDistributor.set("statusId", party.get("statusId"));
        partyDistributor.set("fullName", partyGroup.get("groupName"));
        partyDistributor.set("preferredCurrencyUomId", party.get("preferredCurrencyUomId"));
        partyDistributor.set("officeSiteName", partyGroup.get("officeSiteName"));
        partyDistributor.set("supervisorId", supervisorId);
        partyDistributor.create();
        successResult.put("partyId", partyId);
        return successResult;
    }

    public static Map<String, Object> updatePartyDistributor(DispatchContext ctx, Map<String, ? extends  Object> context) throws GenericEntityException {
        Map<String, Object> successResult = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("statusId");
        String supervisorId = (String) context.get("supervisorId");
        GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyId), false);
        if(UtilValidate.isNotEmpty(supervisorId)) {
            if(!supervisorId.equals(partyDistributor.get("supervisorId"))) {
                partyDistributor.set("supervisorId", supervisorId);
                List<GenericValue> partySalesmans = delegator.findList("PartySalesman", EntityCondition.makeCondition("distributorId", partyId), null, null, null, false);
                for(GenericValue partySalesman : partySalesmans) {
                    partySalesman.set("distributorId", null);
                    partySalesman.store();
                }
            }
        }
        if(UtilValidate.isNotEmpty(statusId)) {
            partyDistributor.set("statusId", statusId);
        }
        partyDistributor.store();
        successResult.put("partyId", partyId);
        return successResult;
    }
    public static Map<String, Object> createOrUpdatePartyDistributor(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyId), false);
        if(partyDistributor == null) return dispatcher.runSync("createPartyDistributor", context);
        else return dispatcher.runSync("updatePartyDistributor", context);
    }

    public static Map<String, Object> updateCoordinateDistributor(DispatchContext ctx, Map<String, ? extends Object> context){
        Map<String, Object> result = ServiceUtil.returnSuccess();

        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        try {
            String partyId = (String) context.get("partyId");
            String lat = (String) context.get("lat");
            String lng = (String) context.get("lng");
            String postalAddressId = (String) context.get("postalAddressId");
            String dataSourceId = (String) context.get("dataSourceId");
            String geoPointId = (String) context.get("geoPointId");

            Double latitude = 15.7480949D;
            Double longitude = 101.4137231D;

            result.put("partyId", partyId);

            try {
                if (UtilValidate.isNotEmpty(lat)) {
                    latitude = Double.valueOf(lat);
                }
                if (UtilValidate.isNotEmpty(lng)) {
                    longitude = Double.valueOf(lng);
                }
            } catch (Exception e) {
                return ServiceUtil.returnError("Not valid latitude/longitude");
            }
            if(UtilValidate.isNotEmpty(geoPointId)) {
                GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", geoPointId), false);

                if(UtilValidate.isNotEmpty(geoPoint)) {
                    geoPoint.put("latitude", latitude);
                    geoPoint.put("longitude", longitude);
                    geoPoint.put("dataSourceId", dataSourceId);

                    delegator.store(geoPoint);

                    //Dung chung service voi Outlet
                    Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
                            "indexName","customer",
                            "partyId",partyId,
                            "latitude", latitude,
                            "longitude",longitude));

                    result.put("message", "Update coordinate Success");
                    return result;
                }
            }

            if(UtilValidate.isNotEmpty(postalAddressId)) {
                GenericValue contact = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", postalAddressId), false);
                if(UtilValidate.isNotEmpty(contact)) {
                    String gpId = contact.getString("geoPointId");
                    //System.out.print("geoPointId" + gpId);
                    if(UtilValidate.isNotEmpty(gpId)) {
                        GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", gpId), false);

                        if(UtilValidate.isNotEmpty(geoPoint)) {
                            geoPoint.put("latitude", latitude);
                            geoPoint.put("longitude", longitude);
                            geoPoint.put("dataSourceId", dataSourceId);

                            delegator.store(geoPoint);

                            Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
                                    "indexName","customer",
                                    "partyId",partyId,
                                    "latitude", latitude,
                                    "longitude",longitude));

                            result.put("message", "Update coordinate Success");
                            return result;
                        }
                    } else {
                        GenericValue newPoint = delegator.makeValue("GeoPoint");
                        newPoint.put("geoPointId", delegator.getNextSeqId("GeoPoint"));
                        newPoint.put("latitude", latitude);
                        newPoint.put("longitude", longitude);
                        newPoint.put("dataSourceId", dataSourceId);
                        contact.put("geoPointId", newPoint.get("geoPointId"));
                        delegator.create(newPoint);
                        delegator.store(contact);

                        Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
                                "indexName","customer",
                                "partyId",partyId,
                                "latitude", latitude,
                                "longitude",longitude));

                        result.put("message", "Update coordinate Success");
                        return result;
                    }
                }
            }



            GenericValue pctd = delegator.findOne("PartyContactTempData", UtilMisc.toMap("partyId", partyId), false);

            if(UtilValidate.isNotEmpty(pctd)) {
                String  pAddressId = pctd.getString("postalAddressId");
                GenericValue contact = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", pAddressId), false);
                if(UtilValidate.isNotEmpty(contact)) {
                    String gpId = contact.getString("geoPointId");
                    if(UtilValidate.isNotEmpty(gpId)) {
                        GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", gpId), false);

                        if(UtilValidate.isNotEmpty(geoPoint)) {
                            geoPoint.put("latitude", latitude);
                            geoPoint.put("longitude", longitude);
                            geoPoint.put("dataSourceId", dataSourceId);

                            delegator.store(geoPoint);

                            Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
                                    "indexName","customer",
                                    "partyId",partyId,
                                    "latitude", latitude,
                                    "longitude",longitude));

                            result.put("message", "Update coordinate Success");
                            return result;
                        }
                    } else {
                        GenericValue newPoint = delegator.makeValue("GeoPoint");
                        newPoint.put("geoPointId", delegator.getNextSeqId("GeoPoint"));
                        newPoint.put("latitude", latitude);
                        newPoint.put("longitude", longitude);
                        newPoint.put("dataSourceId", dataSourceId);
                        contact.put("geoPointId", newPoint.get("geoPointId"));
                        delegator.create(newPoint);
                        delegator.store(contact);

                        Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
                                "indexName","customer",
                                "partyId",partyId,
                                "latitude", latitude,
                                "longitude",longitude));

                        result.put("message", "Update coordinate Success");
                        return result;
                    }
                }
            } else {

                result.put("message", "Not found contact");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", "Not found contact");
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDistIdBySalesmanCode(DispatchContext ctx, Map<String, ? extends Object> context){
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String salesmanCode = (String) context.get("salesmanCode");
        String distributorId = null;
        String distributorName = null;
        GenericValue aDistributor = null;
        try {
            if (salesmanCode!=null) {
                List<GenericValue> partySalesmans = delegator.findList("PartySalesman",
                        EntityCondition.makeCondition("partyCode",salesmanCode),UtilMisc.toSet("distributorId"),null,null,false);
                if (UtilValidate.isNotEmpty(partySalesmans)) {
                    distributorId = partySalesmans.get(0).getString("distributorId");
                    aDistributor= delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId",distributorId),false);
                }
            }
            if (UtilValidate.isNotEmpty(aDistributor)) {
                distributorName = aDistributor.getString("fullName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("distributorId",distributorId);
        result.put("distributorName",distributorName);
        return result;
    }
}
