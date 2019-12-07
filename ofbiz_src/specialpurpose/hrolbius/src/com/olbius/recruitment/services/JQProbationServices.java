package com.olbius.recruitment.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

public class JQProbationServices {
	
	public static final String MODULE = JQProbationServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProbation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		try {
			if (!PartyUtil.isAdmin(delegator, userLogin) && !PartyUtil.isCEO(delegator, userLogin)) {
				String partyIdFrom = PartyUtil.getDeptNameById(userLogin.getString("partyId"), delegator);
				mapCondition.put("partyIdWork", partyIdFrom);
			}
		} catch (Exception e1) {
			String errMsg = "Fatal error calling jqGetListProbation service: " + e1.toString();
			Debug.logError(e1, errMsg, MODULE);
		}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("OfferProbation", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListProbation service: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmplProbation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("partyId", userLogin.getString("partyId"));
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("OfferProbation", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListProbation service: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
}
