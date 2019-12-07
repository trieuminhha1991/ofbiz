package com.olbius.basedelivery.distributorgroup;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/12/17.
 */
public class DistributorGroupWorker {
    public static Boolean updateProductStoreGroupId(Delegator delegator, List<String> listDistributorId, String productStoreGroupId) {
        try {
            List<GenericValue> listDistributor = FastList.newInstance();
            String[] distributorIds = listDistributorId.get(0).split(",");
            for (String distributorId : distributorIds) {
                String originDistributorId = distributorId.replace("\"", "");
                GenericValue distributor = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", originDistributorId), false);
                distributor.set("primaryStoreGroupId", productStoreGroupId);
                listDistributor.add(distributor);
            }
            delegator.storeAll(listDistributor);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean removeProductStoreGroupId(Delegator delegator, String productStoreGroupId) {
        try {
            List<GenericValue> listDistributor = delegator.findList("ProductStore", EntityCondition.makeCondition("primaryStoreGroupId", productStoreGroupId), null, null, null, false);
            for(GenericValue distributor : listDistributor) {
                distributor.set("primaryStoreGroupId", null);
                delegator.store(distributor);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<String> getListPartyIdByProductStoreGroupId(Delegator delegator, String productStoreGroupId) {
        List<GenericValue> productStores;
        try {
            productStores = delegator.findList("ProductStore", EntityCondition.makeCondition("primaryStoreGroupId", productStoreGroupId), null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
        List<String> partyIds = new ArrayList<>();
        for(GenericValue productStore : productStores) {
            String payToPartyId = (String) productStore.get("payToPartyId");
            partyIds.add(payToPartyId);
        }
        return partyIds;
    }
}
