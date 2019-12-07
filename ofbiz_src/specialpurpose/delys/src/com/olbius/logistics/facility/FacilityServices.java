package com.olbius.logistics.facility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.logistics.LogisticsServices;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.SetUtil;

public class FacilityServices {
    public static final String module = LogisticsServices.class.getName();
    public static final String resource = "DelysUiLabels";
    public static final String resourceError = "DelysErrorUiLabels";
    
    public static Map<String, Object> getAvailableINV(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        @SuppressWarnings("unchecked")
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        // 1. get  list of Facility of current Org
        String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator);
        List<GenericValue> listFacility = null;
        try {
            listFacility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", orgId), null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getAvailableINV:" + e.toString());
        }
        // 2. get list of orderItem
        List<GenericValue> listOrderItem = null;
        String orderId = (String)parameters.get("orderId")[0];
        try {
            listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getAvailableINV:" + e.toString());
        }
        List<GenericValue> listData = new ArrayList<GenericValue>();
        List<GenericValue> listTmpData = new ArrayList<GenericValue>();
        // FIXME check for amount of created delivery
        // 3. get list of INV by orderItem and facility
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        List<EntityCondition> listCond = (List<EntityCondition>) context.get("listAllConditions");
        if(listCond != null && !listCond.isEmpty()){
            L0: for(int j = 0; j < listOrderItem.size();j++){
                try {
                    listTmpData = delegator.findList("SumATPByProductAndEXP", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, listSortFields, null, false);
                } catch (GenericEntityException e) {
                    Debug.log(e.getStackTrace().toString(), module);
                    return ServiceUtil.returnError("Service getAvailableINV:" + e.toString());
                }
                for(int k = 0; k < listTmpData.size(); k++){
                    if(listTmpData.get(k).getBigDecimal("atp").compareTo(listOrderItem.get(j).getBigDecimal("quantity")) >= 0){
                        continue L0;
                    }
                }
                break;
            }
            if(listTmpData != null && !listTmpData.isEmpty()){
                for(int k = 0; k < listTmpData.size();k++){
                    for(int i = 0; i < listFacility.size(); i++){
                        if(listFacility.get(i).getString("facilityId").equals(listTmpData.get(k).getString("facilityId"))){
                            listData.add(listFacility.get(i));
                        }
                    }
                }
            }
            // remove duplicate value
            HashSet hs = new HashSet();
            hs.addAll(listData);
            listData.clear();
            listData.addAll(hs);
        }else{
            L1: for(int i = 0; i < listFacility.size(); i++){
                L2: for(int j = 0; j < listOrderItem.size();j++){
                    listCond = new ArrayList<EntityCondition>();
                    listCond.add(EntityCondition.makeCondition("productId", listOrderItem.get(j).getString("productId")));
                    listCond.add(EntityCondition.makeCondition("facilityId", listFacility.get(i).getString("facilityId")));
                    try {
                        listTmpData = delegator.findList("SumATPByProductAndEXP", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, listSortFields, null, false);
                    } catch (GenericEntityException e) {
                        Debug.log(e.getStackTrace().toString(), module);
                        return ServiceUtil.returnError("Service getAvailableINV:" + e.toString());
                    }
                    for(int k = 0; k < listTmpData.size(); k++){
                        if(listTmpData.get(k).getBigDecimal("atp").compareTo(listOrderItem.get(j).getBigDecimal("quantity")) >= 0){
                            continue L2;
                        }
                    }
                    continue L1;
                }
                listData.add(listFacility.get(i));
            }
        }
        // 4. return data
        Map<String, Object> result = new FastMap<String, Object>();
        result.put("listIterator", listData);
        return result;
    }
}