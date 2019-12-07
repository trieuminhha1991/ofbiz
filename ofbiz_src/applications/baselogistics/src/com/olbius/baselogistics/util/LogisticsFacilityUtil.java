package com.olbius.baselogistics.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.product.util.FacilityUtil;
import org.ofbiz.security.Security;

public class LogisticsFacilityUtil {
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	public static final String module = LogisticsFacilityUtil.class.getName();
	public static List<GenericValue> listDepositFacilities(Delegator delegator, String userLoginId) {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		try {
			GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", userLoginId));
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			EntityCondition Cond1 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "FACILITY_ADMIN");
			EntityCondition Cond2 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId"));
			EntityCondition Cond3 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId);
			List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond3);
			EntityCondition allConds = EntityCondition.makeCondition(listConds,EntityOperator.AND);
			listFacilities = delegator.findList("FacilityPartyFacility", allConds, null, null, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listFacilities;
	}
	
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
	
	public static List<String> getFacilityPrimaryPhone(Delegator delegator, String facilityId, String primaryPhoneTypeId) throws GenericEntityException{
		List<String> result = FastList.newInstance(); 
		if (UtilValidate.isNotEmpty(facilityId)){
			List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
			listFacilityContactMech = EntityUtil.filterByDate(listFacilityContactMech);
			if (!listFacilityContactMech.isEmpty()){
				for (GenericValue ctm : listFacilityContactMech){
					List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", primaryPhoneTypeId, "contactMechId", (String)ctm.get("contactMechId"))), null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()){
						for (GenericValue contact : listContactMechPurpose){
							List<GenericValue> listPrimaryNumber = delegator.findList("TelecomNumber", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
							for (GenericValue pn : listPrimaryNumber){
								if (!result.contains(pn)){
									result.add(pn.getString("contactNumber"));
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
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
	
	public static List<GenericValue> getFacilityWithRole(Delegator delegator, String partyId, String roleTypeId) throws GenericEntityException{
		List<GenericValue> listFacility = new ArrayList<GenericValue>();
		Map<String, Object> map = FastMap.newInstance();
		map.put("partyId", partyId);
		map.put("roleTypeId", roleTypeId);
		List<GenericValue> listFacilityAndRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(map), null, null, null, false);
 		listFacilityAndRoles = EntityUtil.filterByDate(listFacilityAndRoles);
 		if (!listFacilityAndRoles.isEmpty()) {
 			List<String> listFacIds = EntityUtil.getFieldListFromEntityList(listFacilityAndRoles, "facilityId", true);
 			listFacility = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacIds), null, null, null, false);
 		}
		return listFacility;
	}
	
	public static List<GenericValue> getFacilityWithRoles(Delegator delegator, String partyId, List<String> roleTypeIds) throws GenericEntityException{
		List<GenericValue> listFacility = new ArrayList<GenericValue>();
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		EntityCondition cond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds);
		EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2));
		List<GenericValue> listFacilityAndRoles = delegator.findList("FacilityParty", EntityCondition.makeCondition(cond), null, null, null, false);
 		listFacilityAndRoles = EntityUtil.filterByDate(listFacilityAndRoles);
 		if (!listFacilityAndRoles.isEmpty()) {
 			List<String> listFacIds = EntityUtil.getFieldListFromEntityList(listFacilityAndRoles, "facilityId", true);
 			listFacility = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacIds), null, null, null, false);
 		}
		return listFacility;
	}
	
	public static List<String> getListFacilityByProductStore(Delegator delegator, String productStoreId) throws GenericEntityException{
		List<GenericValue> listProductStoreFacility = delegator.findList("ProductStoreFacility",
				EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId), null, null, null, false);
		List<String> listFacilityIds = new ArrayList<String>();
		listFacilityIds = EntityUtil.getFieldListFromEntityList(listProductStoreFacility, "facilityId", true);
		EntityCondition cond1 = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
		EntityCondition cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.NOT_IN, listFacilityIds);
		List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2)), null, null, null, false);
		if (!listFacility.isEmpty()){
			List<String> listFacilityIdAdd = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
			listFacilityIds.addAll(listFacilityIdAdd);
		}
		return listFacilityIds;
	}
	
	/* get facility allowed to view */
	public static List<String> getFacilityAllowedView(Delegator delegator, GenericValue userLogin) throws GenericEntityException{
		return getFacilityAllowedView(delegator, userLogin, null);
	}
	
	public static List<String> getFacilityAllowedView(Delegator delegator, GenericValue userLogin, Security security) throws GenericEntityException{
		List<String> listFacilityIds = new ArrayList<String>();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Boolean poManager = SecurityUtil.hasRole("PO_MANAGER", userLogin.getString("partyId"), delegator);
		Boolean logAdmin = SecurityUtil.hasRole("LOG_ADMIN", userLogin.getString("partyId"), delegator);
		Boolean accEmpl = SecurityUtil.hasRole("ACC_EMPLOYEE", userLogin.getString("partyId"), delegator);
		if (poManager || accEmpl || logAdmin){
    		List<GenericValue> listFacilitys = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company), null, null, null, false);
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacilitys, "facilityId", true);
			if (!logAdmin){
				EntityCondition condClosed1 = EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null);
				EntityCondition condClosed2 = EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp());
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(condClosed1);
				conds.add(condClosed2);
				EntityCondition condClosed = EntityCondition.makeCondition(conds, EntityOperator.OR);
				listAllConditions.add(condClosed);
			}
		} else {
			if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))){
				List<GenericValue> listFas = FastList.newInstance();
				try {
					listFas = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, userLogin.getString("partyId")), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg, module);
					return listFacilityIds;
				}
				if (!listFas.isEmpty()) {
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
				}
			} else if (UtilValidate.isNotEmpty(security) && com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PARTY_DISTRIBUTOR")) {
				List<String> listDistributors = PartyUtil.getDistributorManages(delegator, userLogin.getString("partyId"), company);
				List<GenericValue> listFas = FastList.newInstance();
				try {
					listFas = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, listDistributors), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg, module);
					return listFacilityIds;
				}
				if (!listFas.isEmpty()) {
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
				}
			} else {
				listFacilityIds = FacilityUtil.getFacilityIdByRole(delegator, userLogin.getString("partyId"), UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
			}
		}
		return listFacilityIds;
	}
	
	/* get facility consign allowed to view */
	public static List<String> getFacilityDepositAllowedView(Delegator delegator, GenericValue userLogin) throws GenericEntityException{
		List<String> listFacilityIds = new ArrayList<String>();
		List<String> listFacilityIdTmps = new ArrayList<String>();
		Boolean distributor = SecurityUtil.hasRole("DISTRIBUTOR", userLogin.getString("partyId"), delegator);
		if (distributor){
			listFacilityIdTmps = FacilityUtil.getFacilityIdByRole(delegator, userLogin.getString("partyId"), "FACILITY_ADMIN");
		} else {
			listFacilityIdTmps = FacilityUtil.getFacilityIdByRole(delegator, userLogin.getString("partyId"), UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
		}
		List<GenericValue> listFacility = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIdTmps));
		conds.add(EntityCondition.makeCondition("primaryFacilityGroupId", EntityOperator.EQUALS, "FACILITY_CONSIGN"));
		try {
			listFacility = delegator.findList("Facility", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (!listFacility.isEmpty()){
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
		}
		return listFacilityIds;
	}
	
	public static boolean isFacilityByOwnerParty (Delegator delegator, String facilityId){
		GenericValue facility = null;
		try {
			facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Facility: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!facility.isEmpty()) {
			if (facility.getString("primaryFacilityGroupId").equals("FACILITY_CONSIGN"))
				return true;
		}
		return false;
	}
}
