package com.olbius.recruitment.services;

import java.util.HashMap;
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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

public class JQJobRequestServices {
	
	public static final String module = JQJobRequestServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListJobRequest(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(!PartyUtil.isAdmin(userLogin.getString("partyId"), delegator) && !PartyUtil.isCEO(delegator, userLogin)){
				//Handle if userLogin is not hrmadmin and ceo
				String partyId = null;
				try {
					partyId = PartyUtil.getOrgByManager(userLogin.getString("partyId"), delegator);
				} catch (Exception e1) {
					String errMsg = "Fatal error calling jqGetListJobRequest service: " + e1.toString();
					Debug.logError(e1, errMsg, module);
				}
				mapCondition.put("partyId", partyId);
			}else if (PartyUtil.isAdmin(userLogin.getString("partyId"), delegator)){
				EntityCondition statusCon = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("JR_PROPOSED","JR_SCHEDULED", "JR_ACCEPTED", "JR_APPROVED", "JR_REJECTED"));
				listAllConditions.add(statusCon);
			}else if(PartyUtil.isCEO(delegator, userLogin)){
				EntityCondition statusCon = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("JR_ACCEPTED", "JR_APPROVED", "JR_PROPOSED", "JR_SCHEDULED", "JR_REJECTED"));
				listAllConditions.add(statusCon);
			}
		} catch (Exception e1) {
			String errMsg = "Fatal error calling jqGetListJobRequest service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
			
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("JobRequest", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListJobRequest service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	 public static Map<String, Object> jqGetListRecruitmentCriteria(DispatchContext ctx, Map<String, Object> context) {
	 	//Get parameters
		String jobRequestId = (String)context.get("jobRequestId");

		//Get delegator
		Delegator delegator = ctx.getDelegator();

		//Get List
		Map<String, Object> conditionMap = FastMap.newInstance();
		conditionMap.put("jobRequestId", jobRequestId);
		List<GenericValue> listGenericValue = null;
		try {
			listGenericValue = delegator.findList("JobRequestCriteriaView", EntityCondition.makeCondition(conditionMap, EntityJoinOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listGenericValue", listGenericValue);
		return result;
	 }
}
