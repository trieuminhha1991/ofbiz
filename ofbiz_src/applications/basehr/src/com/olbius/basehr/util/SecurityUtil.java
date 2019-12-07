package com.olbius.basehr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

@SuppressWarnings("unchecked")
public class SecurityUtil {
	public static final String module = SecurityUtil.class.getName();
	private static final List<String> listEntities = Arrays.asList("FacilityParty");
	
	// Check to has specific role
	public static boolean hasRole(String strRole, String strPartyId , Delegator delegator){
		List<String> tmpList =  getCurrentRoles(strPartyId, delegator);
		if(tmpList != null && !tmpList.isEmpty()){
			return tmpList.contains(strRole);
		}else{
			return false;
		}
	}
	// Check to has specific role on specific entity
	public static boolean hasRole(String strRole, String strPartyId , Delegator delegator, String strEntityName){
		List<String> tmpList =  getCurrentRoles(strPartyId, strEntityName, delegator);
		if(tmpList != null && !tmpList.isEmpty()){
			return tmpList.contains(strRole);
		}else{
			return false;
		}
	}
	// Check to has specific role
	public static boolean hasRoleWithCurrentOrg(String strRole, String strPartyId , Delegator delegator){
		List<String> tmpList =  getCurrentRolesWithCurrentOrg(strPartyId, delegator);
		if(tmpList != null && !tmpList.isEmpty()){
			return tmpList.contains(strRole);
		}else{
			return false;
		}
	}
	// TODO test the below method
	
	public static List<String> getCurrentRoles(String strPartyId, String strEntityName, Delegator delegator){
		List<String> listData = new ArrayList<String>();
		// check for Entity-roles map
		try {
			List<GenericValue> listTmp = delegator.findByAnd(strEntityName, UtilMisc.toMap("partyId", strPartyId), null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					listData.add(genericValue.getString("roleTypeId"));
				}
				listData = (List<String>) SetUtil.removeDuplicateElementInList(listData);
			}
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return listData;
	}
	
	public static List<String> getCurrentRoles(String strPartyId, Delegator delegator){
		List<String> listData = new ArrayList<String>();
		// check for Entity-roles map
		for (String entity : listEntities) {
			try {
				List<GenericValue> listTmp = delegator.findByAnd(entity, UtilMisc.toMap("partyId", strPartyId), null, false);
				listTmp = EntityUtil.filterByDate(listTmp);
				if(!listTmp.isEmpty()){
					for (GenericValue genericValue : listTmp) {
						listData.add(genericValue.getString("roleTypeId"));
					}
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		}
		// check for party relationship
		try {
			List<GenericValue> listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId), UtilMisc.toSet("roleTypeIdFrom"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					listData.add(genericValue.getString("roleTypeIdFrom"));
				}
			}
			listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId), UtilMisc.toSet("roleTypeIdTo"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					listData.add(genericValue.getString("roleTypeIdTo"));
				}
			}
			listData = (List<String>) SetUtil.removeDuplicateElementInList(listData);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return listData;
	}
	public static List<String> getPartiesByRoles(String strRoleTypeId, Delegator delegator, boolean isPerson){
		List<String> listData = new ArrayList<String>();
		if (isPerson) {
			List<String> partyIds = getPartiesByRoles(strRoleTypeId, delegator);
			if (UtilValidate.isNotEmpty(partyIds)) {
				try {
					List<String> tmpPartyIds = EntityUtil.getFieldListFromEntityList(
							delegator.findList("PartyAndPerson", EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds), UtilMisc.toSet("partyId"), UtilMisc.toList("partyId"), null, false), 
							"partyId", true);
					if (tmpPartyIds != null) listData.addAll(tmpPartyIds);
				} catch (Exception e) {
				    Debug.logError(e, module);
				}
			}
		} else {
			return getPartiesByRoles(strRoleTypeId, delegator);
		}
		return listData;
	}
	
	public static List<String> getSubsidiary(String userLoginId, String basePermission, String strRoleTypeId, Delegator delegator, boolean isPerson){
		List<String> listSubs = getPartiesByRoles(strRoleTypeId, delegator, isPerson);
		List<String> listResult = new ArrayList<String>();
		for(String item : listSubs) {
			try {
				List<GenericValue> listPermission = delegator.findByAnd("UserloginSecurityGroupPermission", UtilMisc.toMap("userLoginId", userLoginId, "organizationId", item, "permissionId", basePermission + "_VIEW"), null, false);
				if (!UtilValidate.isEmpty(listPermission)) {
					listResult.add(item);
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		}
		return listResult;
	}
	
	// create by CT
	public static List<String> getOrganization(String userLoginId, String basePermission, Delegator delegator, boolean isPerson){
		List<String> listResult = new ArrayList<String>();
		
		List<String> listSubs = getPartiesByRoles("SUBSIDIARY", delegator, isPerson);
		try {
			if (UtilValidate.isEmpty(listSubs)) {
				GenericValue company = delegator.findOne("Party", UtilMisc.toMap("partyId", "company"), true);
				if (company != null) {
					listSubs = UtilMisc.toList(company.getString("partyId"));
				}
			}
			for(String item : listSubs) {
				List<GenericValue> listPermission = delegator.findByAnd("UserloginSecurityGroupPermission", UtilMisc.toMap("userLoginId", userLoginId, "organizationId", item, "permissionId", basePermission + "_VIEW"), null, false);
				if (!UtilValidate.isEmpty(listPermission)) {
					listResult.add(item);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return listResult;
	}
	
	public static List<String> getPartiesByRoles(String strRoleTypeId, Delegator delegator){
	    List<String> listData = new ArrayList<String>();
	    // check for Entity-roles map
	    for (String entity : listEntities) {
	        try {
	            List<GenericValue> listTmp = delegator.findByAnd(entity, UtilMisc.toMap("roleTypeId", strRoleTypeId), null, false);
	            listTmp = EntityUtil.filterByDate(listTmp);
	            if(!listTmp.isEmpty()){
	                for (GenericValue genericValue : listTmp) {
	                	listData.add(genericValue.getString("partyId"));
	                }
	            }
	        } catch (Exception e) {
	            Debug.logError(e, module);
	        }
	    }
	    // check for party relationship
	    try {
	        List<GenericValue> listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, strRoleTypeId), UtilMisc.toSet("partyIdFrom"), null, null, false);
	        listTmp = EntityUtil.filterByDate(listTmp);
	        if(!listTmp.isEmpty()){
	            for (GenericValue genericValue : listTmp) {
	            	listData.add(genericValue.getString("partyIdFrom"));
	            }
	        }
	        listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, strRoleTypeId), UtilMisc.toSet("partyIdTo"), null, null, false);
	        listTmp = EntityUtil.filterByDate(listTmp);
	        if(!listTmp.isEmpty()){
	            for (GenericValue genericValue : listTmp) {
	            	listData.add(genericValue.getString("partyIdTo"));
	            }
	        }
	        listData = (List<String>) SetUtil.removeDuplicateElementInList(listData);
	    } catch (Exception e) {
	        Debug.logError(e, module);
	    }
	    return listData;
	}
	
	public static List<String> getPartiesByRolesWithCurrentOrg(GenericValue userLogin, String strRoleTypeId, Delegator delegator){
		String currentOrganization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		return getPartiesByRolesWithOrg(currentOrganization, strRoleTypeId, delegator);
	}
	
	public static List<String> getPartiesByRolesWithOrg(String organizationId, String strRoleTypeId, Delegator delegator){
		List<String> listData = new ArrayList<String>();
		// If party does not has relationship with any organizations
		if(organizationId == null || organizationId.isEmpty()){
			return listData;
		}
		try {
			// check for Entity-roles map
		    for (String entity : listEntities) {
		        switch (entity) {
				case "FacilityParty":{
						// get list of Facilities which have owner are  listCurrentOrganization
						List<EntityCondition> tmpCondList = new ArrayList<EntityCondition>();
						tmpCondList.add(EntityCondition.makeCondition("partyId", organizationId)); // Condition is always not NULL
						tmpCondList.add(EntityCondition.makeCondition("roleTypeId", "OWNER"));
						List<GenericValue> listGVTmp = delegator.findList("FacilityParty", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("facilityId"), null, null, false);
						listGVTmp = EntityUtil.filterByDate(listGVTmp);
						List<String> listFacilities = getStringListFromGVList(listGVTmp, "facilityId");
						// get list of Parties which have input role
						tmpCondList = new ArrayList<EntityCondition>();
						tmpCondList.add(EntityCondition.makeCondition("roleTypeId", strRoleTypeId));
						tmpCondList.add(makeConditionFromList("facilityId", EntityJoinOperator.OR, listFacilities));
						listGVTmp = delegator.findList("FacilityParty", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("partyId"), null, null, false);
						listGVTmp = EntityUtil.filterByDate(listGVTmp);
						listData.addAll(getStringListFromGVList(listGVTmp, "partyId"));
						break;
					}
				default:
					break;
				}
		    }
		    // check for party relationship: connect directly
		    List<EntityCondition> tmpCondList = new ArrayList<EntityCondition>();
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, strRoleTypeId));
		    tmpCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, organizationId));
	        List<GenericValue> listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false);
	        listTmp = EntityUtil.filterByDate(listTmp);
	        if(!listTmp.isEmpty()){
	            for (GenericValue genericValue : listTmp) {
	            	listData.add(genericValue.getString("partyIdFrom"));
	            }
	        }
	        tmpCondList.clear();
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, strRoleTypeId));
		    tmpCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, organizationId));
	        listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false);
	        listTmp = EntityUtil.filterByDate(listTmp);
	        if(!listTmp.isEmpty()){
	            for (GenericValue genericValue : listTmp) {
	            	listData.add(genericValue.getString("partyIdTo"));
	            }
	        }
	        // check for party relationship: connect indirectly
	        // get list of parties who have input role
	        tmpCondList.clear();
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, strRoleTypeId));
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
	        listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("partyIdTo", "partyIdFrom"), null, null, false);
	        for (GenericValue genericValue : listTmp) {
				if(PartyUtil.checkAncestorOfParty(delegator, organizationId, genericValue.getString("partyIdFrom"))){
					listData.add(genericValue.getString("partyIdTo"));
				}
			}
	        tmpCondList.clear();
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, strRoleTypeId));
		    tmpCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
	        listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), UtilMisc.toSet("partyIdTo", "partyIdFrom"), null, null, false);
	        for (GenericValue genericValue : listTmp) {
				if(PartyUtil.checkAncestorOfParty(delegator, organizationId, genericValue.getString("partyIdTo"))){
					listData.add(genericValue.getString("partyIdFrom"));
				}
			}
	        listData = (List<String>) SetUtil.removeDuplicateElementInList(listData);
		} catch (Exception e) {
	        Debug.logError(e, module);
	    }
		return listData;
	}
	
	public static List<String> getCurrentRolesWithCurrentOrg(String strPartyId, Delegator delegator){
		List<String> listData = new ArrayList<String>();
		// check for party relationship
		List<String> listCurrentOrganization = getCurrentOrgs(strPartyId, delegator);
		List<EntityCondition> listEC = new ArrayList<EntityCondition>();
		EntityCondition tmpEC = makeConditionFromList("partyIdFrom", EntityJoinOperator.OR, listCurrentOrganization);
		if(tmpEC != null){
			listEC.add(tmpEC);
		}
		tmpEC = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId);
		listEC.add(tmpEC);
		try {
			List<GenericValue> listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listEC, EntityOperator.AND), UtilMisc.toSet("roleTypeIdTo"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					listData.add(genericValue.getString("roleTypeIdTo"));
				}
			}
			listEC.clear();
			tmpEC = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId);
			listEC.add(tmpEC);
			tmpEC = makeConditionFromList("partyIdTo", EntityJoinOperator.OR, listCurrentOrganization);
			if(tmpEC != null){
				listEC.add(tmpEC);
			}
			listEC.add(tmpEC);
			listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listEC, EntityOperator.AND), UtilMisc.toSet("roleTypeIdFrom"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					listData.add(genericValue.getString("roleTypeIdFrom"));
				}
			}
			listData = (List<String>) SetUtil.removeDuplicateElementInList(listData);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return listData;
	}
	
	
	public static List<GenericValue> getGVCurrentRoles(String strPartyId, Delegator delegator){
		List<GenericValue> listData = new ArrayList<GenericValue>();
		// check for party relationship
		List<EntityCondition> listEC = new ArrayList<EntityCondition>();
		EntityCondition tmpEC = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId);
		listEC.add(tmpEC);
		try {
			List<GenericValue> listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listEC, EntityOperator.AND), UtilMisc.toSet("roleTypeIdTo"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", genericValue.getString("roleTypeIdTo")), false);
					listData.add(roleType);
				}
			}
			listEC.clear();
			tmpEC = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId);
			listEC.add(tmpEC);
			listTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listEC, EntityOperator.AND), UtilMisc.toSet("roleTypeIdFrom"), null, null, false);
			listTmp = EntityUtil.filterByDate(listTmp);
			if(!listTmp.isEmpty()){
				for (GenericValue genericValue : listTmp) {
					GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", genericValue.getString("roleTypeIdFrom")), false);
					listData.add(roleType);
				}
			}
			listData = (List<GenericValue>) SetUtil.removeDuplicateElementInList(listData);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return listData;
	}
	
	public static List<String> getBussinessRoles(String strPartyId, Delegator delegator){
		List<String> roleTypeList = null;
		roleTypeList = getCurrentRoles(strPartyId, delegator);
		List<String> businessMenus = new ArrayList<String>();
		for (String roleTypeId : roleTypeList) {
			GenericValue roleTypeAttr = null;
			try {
				roleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessMenu"), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			if (roleTypeAttr != null && roleTypeAttr.getString("attrValue") != null){
				businessMenus.add(roleTypeAttr.getString("attrValue"));
			}
		}
		return businessMenus;
	}
	
	// Get list of userLogins which input party has
	private static List<String> getUserLogins(String strPartyId, Delegator delegator){
		List<String> listReturn = new ArrayList<String>();
		try {
			// Make sure userLogin is enabled
			List<EntityCondition> tmpCondList = new ArrayList<EntityCondition>();
		    tmpCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strPartyId));
		    tmpCondList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
			List<GenericValue> listTmp = delegator.findList("UserLogin", EntityCondition.makeCondition(tmpCondList, EntityJoinOperator.AND), null, null, null, false);
			for(GenericValue gv:listTmp){
				listReturn.add(gv.getString("userLoginId"));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return listReturn;
	}
	
	// Get list of organizations which has relationship(s) with input party
	private static List<String> getCurrentOrgs(String strPartyId, Delegator delegator){
		List<String> listData = getUserLogins(strPartyId, delegator);
		List<String> listReturn = new ArrayList<String>();
		for(String strUserLoginId:listData){
			listReturn.add(MultiOrganizationUtil.getCurrentOrganization(delegator, strUserLoginId));
		}
		return listReturn;
	}
	
	// Make List<EntityCondition> from List<String> with EntityJointOperator
	private static EntityCondition makeConditionFromList(String fieldName, EntityJoinOperator operator, List<String> listValue){
		List<EntityCondition> listEC = new ArrayList<EntityCondition>();
		if(listValue.isEmpty()){
			return EntityCondition.makeCondition(listEC, operator);
		}else{
			if(listValue.size() == 1){
				return EntityCondition.makeCondition(fieldName, EntityOperator.EQUALS, listValue.get(0));
			}else{
				for(String strOrg:listValue){
					listEC.add(EntityCondition.makeCondition(fieldName, EntityOperator.EQUALS, strOrg));
				}
			}
		}
		return EntityCondition.makeCondition(listEC, operator);
	}
	
	// Extract One list<String> from One List<GenericValue>
	private static List<String> getStringListFromGVList(List<GenericValue> listGV, String fieldName){
		if(listGV == null || listGV.isEmpty()){
			return new ArrayList<String>();
		}
		List<String> listReturn = new ArrayList<String>();
		for(GenericValue gv: listGV){
			listReturn.add(gv.getString(fieldName));
		}
		return listReturn;
	}
}
