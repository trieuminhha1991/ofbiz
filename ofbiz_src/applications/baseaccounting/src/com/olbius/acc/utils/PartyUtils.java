package com.olbius.acc.utils;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.SecurityUtil;

public class PartyUtils {
	
	public static final String MODULE = PartyUtils.class.getName();
	
	public static List<Party> getCustomers(Delegator delegator){
		List<String> listCusts = SecurityUtil.getPartiesByRoles("CUSTOMER", delegator);
		List<Party> results = FastList.newInstance();
		try {
			for(String partyId : listCusts) {
				GenericValue party = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyId), false);
				results.add(new Party(party));
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetCustomersBySetupOpenBalanceParty(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listIterator = delegator.find("ProductStoreRoleAndParty", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetCustomersBySetupOpenBalanceParty service: " + e.toString();
    		Debug.logError(e, errMsg, MODULE);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
