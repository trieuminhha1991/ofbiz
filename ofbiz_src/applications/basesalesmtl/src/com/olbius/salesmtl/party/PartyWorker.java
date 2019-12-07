package com.olbius.salesmtl.party;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PartyWorker {
	
	public static List<String> getDeptByManager(Delegator delegator, String partyId) throws GenericEntityException {
		List<String> deptIds = null;
		if (delegator == null || partyId == null) return deptIds;
		
		EntityCondition mainCond = EntityCondition.makeCondition(
				EntityCondition.makeCondition(UtilMisc.toMap(
					"partyIdFrom", partyId, 
					"roleTypeIdFrom", "MANAGER", 
					"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
					"partyRelationshipTypeId", "MANAGER")), 
				EntityOperator.AND, EntityUtil.getFilterByDateExpr());
		deptIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", mainCond, UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		return deptIds;
	}

    public static List<String> getSupByDist(Delegator delegator, String distId) throws GenericEntityException {
        List<String> supIds = null;
        if (delegator == null || distId == null) return supIds;
        EntityCondition mainCond = EntityCondition.makeCondition("partyId",distId);
        supIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDistributor", mainCond, UtilMisc.toSet("supervisorId"), null, null, false), "supervisorId", true);
        return supIds;
    }
}
