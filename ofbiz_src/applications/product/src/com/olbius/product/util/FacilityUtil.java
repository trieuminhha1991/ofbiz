package com.olbius.product.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FacilityUtil {
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	public static List<String> getFacilityIdByRole(Delegator delegator, String partyId, String roleTypeId) throws GenericEntityException{
		List<String> listFacilityIds = new ArrayList<String>();
		Map<String, Object> map = FastMap.newInstance();
		map.put("partyId", partyId);
		map.put("roleTypeId", roleTypeId);
		List<GenericValue> listFacilityAndRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(map), null, null, null, false);
 		listFacilityAndRoles = EntityUtil.filterByDate(listFacilityAndRoles);
 		if (!listFacilityAndRoles.isEmpty()) {
 			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacilityAndRoles, "facilityId", true);
 		}
		return listFacilityIds;
    }
	
	public static List<String> getAllFacilityRecursive(Delegator delegator, String rootFacilityId) throws GenericEntityException{
		List<String> listAllChilds = FastList.newInstance();
		EntityCondition condParent = EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, rootFacilityId);
		EntityCondition condClosedDate1 = EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null);
		EntityCondition condClosedDate2 = EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp());
		List<EntityCondition> condDate = FastList.newInstance();
		condDate.add(condClosedDate1);
		condDate.add(condClosedDate2);
		EntityCondition condDateOr = EntityCondition.makeCondition(condDate, EntityOperator.OR);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(condParent);
		conds.add(condDateOr);
		List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(conds), null, null, null, false);
		List<GenericValue> listChilds = new ArrayList<GenericValue>();
		for (GenericValue fac : listFacility){
			if (rootFacilityId.equals(fac.getString("parentFacilityId"))){
				listChilds.add(fac);
				listAllChilds.add(fac.getString("facilityId"));
			}
		}
		if (!listChilds.isEmpty()){
			for (GenericValue child : listChilds){
				getAllFacilityRecursive(delegator, child.getString("facilityId"));	
			}
		}
		return listAllChilds;
	}
	
	public static Boolean hasRoles (Delegator delegator, String partyId, String roleTypeId, String facilityId) throws GenericEntityException{
		Map<String, Object> map = FastMap.newInstance();
		map.put("partyId", partyId);
		map.put("roleTypeId", roleTypeId);
		map.put("facilityId", facilityId);
		List<GenericValue> listFacilityAndRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(map), null, null, null, false);
 		listFacilityAndRoles = EntityUtil.filterByDate(listFacilityAndRoles);
 		if (!listFacilityAndRoles.isEmpty()) {
 			return true;
 		}
		return false;
    }
	
	public static Boolean hasAccessRoles (Security security, GenericValue userLogin) throws GenericEntityException{
		if (!security.hasPermission("LOGISTICS_ADMIN", userLogin)) {
        	if (!security.hasPermission("LOGISTICS_VIEW", userLogin)) {
        		if (!security.hasPermission("FACILITY_ADMIN", userLogin)) {
        			if (!security.hasPermission("FACILITY_VIEW", userLogin)) {
        				return false;
        			}
        		}
        	}
        }
		return true;
	}
	
	public static List<String> getFacilityManages(Delegator delegator, GenericValue userLogin) throws GenericEntityException{
		List<String> listFacilityIds = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		
		conds.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
		conds.add(EntityCondition.makeCondition("roleTypeId", "MANAGER"));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listFacilityParty = FastList.newInstance();
		try {
			listFacilityParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
			Debug.logError(e, errMsg);
			throw new GenericEntityException(errMsg);
		}
		if (!listFacilityParty.isEmpty()){
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacilityParty, "facilityId", true);
		}
		return listFacilityIds;
	}
	
}
