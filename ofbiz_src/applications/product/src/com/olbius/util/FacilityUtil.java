package com.olbius.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FacilityUtil {
	public static List<GenericValue> getFacilityContactMechs(Delegator delegator, String facilityId, String contactMechPurposeTypeId) throws GenericEntityException{
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(facilityId) && UtilValidate.isNotEmpty(contactMechPurposeTypeId)){
			List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
			listFacilityContactMech = EntityUtil.filterByDate(listFacilityContactMech);
			if (!listFacilityContactMech.isEmpty()){
				for (GenericValue ctm : listFacilityContactMech){
					List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "contactMechId", (String)ctm.get("contactMechId"))), null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()){
						for (GenericValue contact : listContactMechPurpose){
							List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
							for (GenericValue pa : listPostalAddress){
								if (!listContactMechs.contains(pa)){
									GenericValue address = delegator.findOne("PostalAddressDetail", false, UtilMisc.toMap("contactMechId", pa.getString("contactMechId"))); 
									pa.put("address1", address.getString("fullName"));
									listContactMechs.add(pa);
								}
							}
						}
					}
				}
			}
		}
		return listContactMechs;
	}
	
	/*
	 * lay ra danh sach kho hang thuoc cua hang ma user login quan ly
	 * 
	 */
	public static List<String> getFacilityByProductStoreManages (Delegator delegator, GenericValue userLogin){
		List<String> listFacilityIds = FastList.newInstance();
		String partyId = userLogin.getString("partyId");
		listFacilityIds = getFacilityByProductStoreManagesByParty (delegator, partyId);
		return listFacilityIds;
	}
	
	/*
	 * lay ra danh sach kho hang thuoc cua hang theo partyId
	 * 
	 */
	public static List<String> getFacilityByProductStoreManagesByParty (Delegator delegator, String partyId){
		List<String> listFacilityIds = FastList.newInstance();
		List<GenericValue> listProductStoreRole = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		EntityCondition cond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
		EntityCondition cond3 = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr());
		conds.add(cond1);
		conds.add(cond2);
		conds.add(cond3);
		try {
			listProductStoreRole = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.toString());
			return listFacilityIds;
		}
		if (!listProductStoreRole.isEmpty()){
			List<String> storeIds = FastList.newInstance();
			storeIds = EntityUtil.getFieldListFromEntityList(listProductStoreRole, "productStoreId", true);
			if (!storeIds.isEmpty()){
				EntityCondition cond4 = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, storeIds);
				EntityCondition cond5 = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr());
				List<EntityCondition> cond2s = FastList.newInstance();
				cond2s.add(cond4);
				cond2s.add(cond5);
				List<GenericValue> listProductStoreFacility = FastList.newInstance();
				try {
					listProductStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(cond2s), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.log(e.toString());
					return listFacilityIds;
				}
				if (!listProductStoreFacility.isEmpty()){
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listProductStoreFacility, "facilityId", true);
				}
			}
		}
		return listFacilityIds;
	}
	
	public static List<String> getFacilityManages (Delegator delegator, GenericValue userLogin){
		List<String> listFacilityIds = FastList.newInstance();
		String partyId = userLogin.getString("partyId");
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		EntityCondition cond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond2);
		conds.add(cond1);
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listFacility = FastList.newInstance();
		try {
			listFacility = delegator.findList("FacilityParty", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
			Debug.logError(e, errMsg);
			return listFacilityIds;
		}
		if (!listFacility.isEmpty()){
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
		}
		return listFacilityIds;
	}
	
	/**
	 * check role party with facility
	 */
	public static Boolean checkRoleWithFacility(Delegator delegator, String facilityId, String partyId, String roleTypeId) throws GenericEntityException{
		Boolean is = false;
		Map<String, Object> map = FastMap.newInstance();
		map.put("facilityId", facilityId);
		map.put("partyId", partyId);
		map.put("roleTypeId", roleTypeId);
		List<GenericValue> listFacilityAndRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(map), null, null, null, false);
 		listFacilityAndRoles = EntityUtil.filterByDate(listFacilityAndRoles);
 		if (!listFacilityAndRoles.isEmpty()) is = true;
		return is;
	}
	
	/**
	 * check role party with facility
	 */
	public static  List<String> getFacilityByOwnerParty (Delegator delegator, String ownerPartyId){
		Map<String, Object> map = FastMap.newInstance();
		map.put("ownerPartyId", ownerPartyId);
		List<GenericValue> listFacility = FastList.newInstance();
		List<String> listFacilityIds = FastList.newInstance();
		try {
			listFacility = delegator.findList("FacilityParty", EntityCondition.makeCondition(map), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
			Debug.logError(e, errMsg);
			return listFacilityIds;
		}
		if (!listFacility.isEmpty()){
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
		}
		return listFacilityIds;
	}
	
}