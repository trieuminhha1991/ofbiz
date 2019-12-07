package com.olbius.salesmtl;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/22/18.
 */
public class DistributorWorkers {
    public static Map<String, Object> createPartyDistributor(Delegator delegator, String supervisorId, String distributorId) throws GenericEntityException {
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", distributorId), false);
        GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", distributorId), false);
        GenericValue partyDistributor = delegator.makeValue("PartyDistributor");
        partyDistributor.set("partyId", distributorId);
        partyDistributor.set("partyCode", party.get("partyCode"));
        partyDistributor.set("statusId", party.get("statusId"));
        partyDistributor.set("fullName", partyGroup.get("groupName"));
        partyDistributor.set("preferredCurrencyUomId", party.get("preferredCurrencyUomId"));
        partyDistributor.set("officeSiteName", partyGroup.get("officeSiteName"));
        partyDistributor.set("supervisorId", supervisorId);
        partyDistributor.set("createdDate", UtilDateTime.nowTimestamp());
        partyDistributor.create();
        returnSuccess.put("partyId", distributorId);
        return returnSuccess;
    }

    public static Map<String, Object> createRetailOutlet(Delegator delegator, String supervisorId, String distributorId, String salesmanId, String customerId) throws GenericEntityException {
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", customerId), false);
        GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", customerId), false);
        GenericValue partyCustomer = delegator.makeValue("PartyCustomer");
        partyCustomer.set("partyId", customerId);
        partyCustomer.set("partyCode", party.get("partyCode"));
        partyCustomer.set("statusId", party.get("statusId"));
        partyCustomer.set("fullName", partyGroup.get("groupName"));
        partyCustomer.set("preferredCurrencyUomId", party.get("preferredCurrencyUomId"));
        partyCustomer.set("officeSiteName", partyGroup.get("officeSiteName"));
        partyCustomer.set("supervisorId", supervisorId);
        partyCustomer.set("distributorId", distributorId);
        partyCustomer.set("salesmanId", salesmanId);
        partyCustomer.set("createdDate", UtilDateTime.nowTimestamp());
        partyCustomer.set("partyTypeId", party.get("partyTypeId"));
        partyCustomer.set("visitFrequencyTypeId", "F0");
        partyCustomer.create();
        returnSuccess.put("partyId", customerId);
        return returnSuccess;
    }

    public static Map<String, Object> createPartySalesman(Delegator delegator, String supervisorId, String distributorId, String salesmanId) throws GenericEntityException {
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", salesmanId), false);
        GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", salesmanId), false);
        GenericValue partyCustomer = delegator.makeValue("PartySalesman");
        partyCustomer.set("partyId", salesmanId);
        partyCustomer.set("partyCode", party.get("partyCode"));
        partyCustomer.set("statusId", party.get("statusId"));
        partyCustomer.set("fullName", partyGroup.get("groupName"));
        partyCustomer.set("preferredCurrencyUomId", party.get("preferredCurrencyUomId"));
        partyCustomer.set("officeSiteName", partyGroup.get("officeSiteName"));
        partyCustomer.set("supervisorId", supervisorId);
        partyCustomer.set("distributorId", distributorId);
        partyCustomer.set("salesmanId", salesmanId);
        partyCustomer.set("createdDate", UtilDateTime.nowTimestamp());
        partyCustomer.set("partyTypeId", "RETAIL_OUTLET");
        partyCustomer.create();
        returnSuccess.put("partyId", salesmanId);
        return returnSuccess;
    }

    public static Map<String, Object> updateThruDateRelationDmsLog(Delegator delegator, String partyIdTo, Timestamp thruDate) throws GenericEntityException {
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
        List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
        conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "SALESSUP_DEPT",
                "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTION")));
        List<GenericValue> dmsLogs = delegator.findList("PartyRelationDmsLog",
                EntityCondition.makeCondition(conditions), null, null, null, false);
        for(GenericValue dmsLog : dmsLogs) {
            dmsLog.set("thruDate", thruDate);
            dmsLog.store();
        }
        return returnSuccess;
    }
}
