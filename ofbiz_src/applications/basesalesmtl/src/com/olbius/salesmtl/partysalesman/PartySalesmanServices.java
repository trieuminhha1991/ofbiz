package com.olbius.salesmtl.partysalesman;

import com.olbius.acc.utils.UtilServices;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.service.annotations.Service;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javax.rmi.CORBA.Util;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/26/18.
 */
public class PartySalesmanServices {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createPartySalesman(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
	    String departmentId = (String)context.get("departmentId");
        List<String> managers = SalesPartyUtil.getManagerIdOfDept(delegator, departmentId);
	    String supervisorId = null;
	    if(managers != null && managers.size() > 0)
	    	supervisorId = managers.get(0);

	    
        GenericValue partySalesman = delegator.makeValue("PartySalesman");
        partySalesman.set("partyId", context.get("partyId"));
        partySalesman.set("partyCode", context.get("partyCode"));
        partySalesman.set("statusId", context.get("statusId"));
        partySalesman.set("fullName", context.get("fullName"));
        partySalesman.set("statusId", context.get("statusId"));
        partySalesman.set("supervisorId", supervisorId);
        partySalesman.set("distributorId", context.get("distributorId"));
        partySalesman.set("createdDate", UtilDateTime.nowTimestamp());
        partySalesman.set("preferredCurrencyUomId", context.get("preferredCurrencyUomId"));
        partySalesman.set("officeSiteName", context.get("officeSiteName"));
        partySalesman.create();
        result.put("partyId", context.get("partyId"));
        return result;
    }

    public static Map<String, Object> updatePartySalesman(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("workingStatusId");
        Delegator delegator = ctx.getDelegator();
        GenericValue partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", partyId), false);
        if(UtilValidate.isNotEmpty(partySalesman)){
            if(UtilValidate.isNotEmpty(statusId)){
                partySalesman.set("statusId", statusId);
            }
            partySalesman.store();
        }
        successResult.put("partyId", partyId);
        return successResult;
    }
}
