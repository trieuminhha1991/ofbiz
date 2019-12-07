package com.olbius.recruitment.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.RoleTyle;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;
import com.olbius.util.RoleHelper;
import com.olbius.util.SecurityUtil;

public class JQRecruitmentPlanServices implements RoleTyle {
	public static final String module = JQRecruitmentPlanServices.class.getName();

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentPlan(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("partyId", parameters.get("partyId")[0]);
		mapCondition.put("year", parameters.get("year")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("RecruitmentPlan", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlan service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> getRecruitmentPlan(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String statusId = (String)context.get("statusId");
		//Get delegator
		Delegator delegator = ctx.getDelegator();

		//Get List
		Map<String, Object> conditionMap = FastMap.newInstance();
		conditionMap.put("partyId", partyId);
		conditionMap.put("year", year);
		if(statusId != null) {
			conditionMap.put("statusId", statusId);
		}
		List<GenericValue> listGenericValue = null;
		try {
			listGenericValue = delegator.findList("RecruitmentPlan", EntityCondition.makeCondition(conditionMap, EntityJoinOperator.AND), null, UtilMisc.toList("-emplPositionTypeId"), null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listGenericValue", listGenericValue);
		return result;
	}
	
	public static Map<String, Object> getRecruitmentPlanDT(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");

		//Get delegator
		Delegator delegator = ctx.getDelegator();

		//Get List
		Map<String, Object> conditionMap = FastMap.newInstance();
		conditionMap.put("partyId", partyId);
		conditionMap.put("year", year);
		List<GenericValue> listGenericValue = null;
		try {
			listGenericValue = delegator.findList("RecruitmentPlanDT", EntityCondition.makeCondition(conditionMap, EntityJoinOperator.AND), null, UtilMisc.toList("-emplPositionTypeId"), null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listGenericValue", listGenericValue);
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentPlanHeader(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(!HRM_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator))){
				String partyId = null;
				try {
					partyId = PartyUtil.getOrgByManager(userLogin.getString("partyId"), delegator);
				} catch (Exception e1) {
					String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e1.toString();
					Debug.logError(e1, errMsg, module);
				}
				mapCondition.put("partyId", partyId);
			}else{
				//Handle if userLogin is hrmadmin
				List<GenericValue> listParty = delegator.findList("PartyGroupAndPartyRole", EntityCondition.makeCondition("roleTypeId", EntityJoinOperator.IN , UtilMisc.toSet("INTER_CONTROL_DEPT", "ASSIST_DEPT", "REWARD_DEPT", "DR_DEPARTMENT", "RENT_FACILITY", "HR_DEPARTMENT")), null, null, null, false);
				Set<String> setParty = FastSet.newInstance();
				for(GenericValue item: listParty) {
					setParty.add(item.getString("partyId"));
				}
				EntityCondition partyCon = EntityCondition.makeCondition("partyId",EntityJoinOperator.IN, setParty);
				listAllConditions.add(partyCon);
			}
		} catch (Exception e1) {
			String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("RecruitmentPlanHeader", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSaleRecruitmentPlanHeader(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if (SecurityUtil.hasRole(CEO_ROLE, userLogin.getString("partyId"), delegator) || SecurityUtil.hasRole(HRM_ROLE, userLogin.getString("partyId"), delegator)) {
				EntityCondition tmpConditon = EntityCondition.makeCondition("creatorRoleTypeId", NBD_ROLE);
				listAllConditions.add(tmpConditon);
				tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
				listIterator = delegator.find("RecruitmentPlanHeader", tmpConditon, null, null, listSortFields, opts);
			}else {
				Organization tree = PartyUtil.buildOrg(delegator, PartyUtil.getOrgByManager(userLogin, delegator));
				List<GenericValue> listChild = tree.getDirectChildList(delegator);
				Set<String> partySet = FastSet.newInstance();
				for(GenericValue item: listChild) {
					partySet.add(item.getString("partyId"));
				}
				partySet.add(PartyUtil.getOrgByManager(userLogin, delegator));
				EntityCondition tmpConditon = EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partySet );
				listAllConditions.add(tmpConditon);
				tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
				listIterator = delegator.find("RecruitmentPlanHeader", tmpConditon, null, null, listSortFields, opts);
			}
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentPlanDTHeader(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(!HRM_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator)) && !CEO_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator))){
				mapCondition.put("partyId", PartyUtil.getOrgByManager(userLogin, delegator));
			}else {
				EntityCondition tmpConditon = EntityCondition.makeCondition("creatorRoleTypeId", NBD_ROLE);
				listAllConditions.add(tmpConditon);
			}
			mapCondition.put("statusId", "RPH_ACCEPTED");
			mapCondition.put("actorRoleTypeId", CEO_ROLE);
		} catch (Exception e1) {
			String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("RecruitmentPlanDTHeader", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanDTHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentPlanProposal(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition1 = new HashMap<String, String>();
		mapCondition1.put("statusId", "RPH_ACCEPTED");
		mapCondition1.put("actorRoleTypeId", HRM_ROLE);
		EntityCondition tmpConditon1 = EntityCondition.makeCondition(mapCondition1);
		
		Map<String, String> mapCondition2 = new HashMap<String, String>();
		mapCondition2.put("statusId", "RPH_INIT");
		mapCondition2.put("creatorRoleTypeId", HRM_ROLE);
		EntityCondition tmpConditon2 = EntityCondition.makeCondition(mapCondition2);
		
		EntityCondition tmpCondition = EntityCondition.makeCondition(tmpConditon1, EntityJoinOperator.OR, tmpConditon2);
		listAllConditions.add(tmpCondition);
		try {
			listIterator = delegator.find("RecruitmentPlanHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentPlanApproval(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("statusId", "RPH_PROPOSED");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("RecruitmentPlanHeader", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRecruitmentPlanCheck(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("statusId", "RPH_PROPOSED");
		mapCondition.put("actorRoleTypeId", MANAGER_ROLE);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("RecruitmentPlanHeader", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRecruitmentPlanSumary(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		List<String> listSaleDepts = SecurityUtil.getPartiesByRoles("SALES_DEPARTMENT", delegator, false);
		String saleDeptId = listSaleDepts.get(0);
		listAllConditions.add(tmpConditon);
		List<GenericValue> tmpListResult = null;
		List<GenericValue> listResult = FastList.newInstance();
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			tmpListResult = delegator.findList("RecruitmentPlanHeader", tmpConditon, null, listSortFields, opts, false);
			Organization orgTree = PartyUtil.buildOrg(delegator, saleDeptId);
			for(GenericValue item: tmpListResult) {
				if (PartyUtil.checkInOrg(orgTree, item.getString("partyId"), delegator)) {
					continue;
				}
				listResult.add(item);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListRecruitmentPlanHeader service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listResult);
		return successResult;
    }
}
