package com.olbius.basepo.utils;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.SecurityUtil;

public class LogisticsPartyUtil {
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	public static List<String> getStorekeeperOfFacility(Delegator delegator, String facilityId)
			throws GenericEntityException {
		List<String> listPartyIds = new ArrayList<String>();
		EntityCondition facCond = EntityCondition.makeCondition("facilityId", facilityId);
		EntityCondition roleTypeManager = EntityCondition.makeCondition("roleTypeId",
				UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
		EntityCondition roleTypeStorekeeper = EntityCondition.makeCondition("roleTypeId",
				UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
		List<GenericValue> listManagerParties = delegator.findList("FacilityParty",
				EntityCondition.makeCondition(UtilMisc.toList(facCond, roleTypeManager)), null, null, null, false);
		listManagerParties = EntityUtil.filterByDate(listManagerParties);
		if (!listManagerParties.isEmpty()) {
			for (GenericValue manager : listManagerParties) {
				listPartyIds.add(manager.getString("partyId"));
			}
		}
		EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.IN, listPartyIds);
		List<GenericValue> listStorekeeperParties = delegator.findList("FacilityParty",
				EntityCondition.makeCondition(UtilMisc.toList(facCond, roleTypeStorekeeper, partyCond)), null, null,
				null, false);
		listStorekeeperParties = EntityUtil.filterByDate(listStorekeeperParties);
		listPartyIds = new ArrayList<String>();
		if (!listStorekeeperParties.isEmpty()) {
			for (GenericValue party : listStorekeeperParties) {
				listPartyIds.add(party.getString("partyId"));
			}
		}
		return listPartyIds;
	}

	public static List<GenericValue> getFacilityByRole(Delegator delegator, String partyId, String roleTypeId)
			throws GenericEntityException {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		EntityCondition roleTypeCond = EntityCondition.makeCondition("roleTypeId", roleTypeId);
		EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
		List<GenericValue> listFacilityParty = delegator.findList("FacilityParty",
				EntityCondition.makeCondition(UtilMisc.toList(roleTypeCond, partyCond)), null, null, null, false);
		listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
		if (!listFacilityParty.isEmpty()) {
			for (GenericValue fa : listFacilityParty) {
				GenericValue fac = delegator.findOne("Facility", false,
						UtilMisc.toMap("facilityId", fa.getString("facilityId")));
				listFacilities.add(fac);
			}
		}
		return listFacilities;
	}

	public static List<GenericValue> getFacilityByRoles(Delegator delegator, String partyId, List<String> roleTypeIds)
			throws GenericEntityException {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		for (String roleTypeId : roleTypeIds) {
			if (!listFacilities.isEmpty()) {
				List<String> listFaIds = new ArrayList<String>();
				for (GenericValue fac : listFacilities) {
					listFaIds.add(fac.getString("facilityId"));
				}
				EntityCondition facCond = EntityCondition.makeCondition("partyId", EntityOperator.IN, listFaIds);
				EntityCondition roleTypeCond = EntityCondition.makeCondition("roleTypeId", roleTypeId);
				EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
				List<GenericValue> listFacilityParty = delegator.findList("FacilityParty",
						EntityCondition.makeCondition(UtilMisc.toList(roleTypeCond, partyCond, facCond)), null, null,
						null, false);
				listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
				if (!listFacilityParty.isEmpty()) {
					for (GenericValue fa : listFacilityParty) {
						Boolean check = true;
						for (GenericValue item : listFacilities) {
							if (fa.getString("facilityId").equals(item.getString("facilityId")))
								check = false;
							break;
						}
						if (check) {
							GenericValue fac = delegator.findOne("Facility", false,
									UtilMisc.toMap("facilityId", fa.getString("facilityId")));
							listFacilities.add(fac);
						}
					}
				}
			} else {
				List<GenericValue> listFacilityTmp = LogisticsPartyUtil.getFacilityByRole(delegator, partyId,
						roleTypeId);
				if (!listFacilityTmp.isEmpty()) {
					for (GenericValue fac : listFacilityTmp) {
						listFacilities.add(fac);
					}
				}
			}
		}
		return listFacilities;
	}

	public static List<GenericValue> getFacilityByRolesAndFacilityType(Delegator delegator, String partyId,
			List<String> roleTypeIds, String facilityTypeId) throws GenericEntityException {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		List<GenericValue> listFacilityTmp = LogisticsPartyUtil.getFacilityByRoles(delegator, partyId, roleTypeIds);
		if (!listFacilityTmp.isEmpty()) {
			for (GenericValue fa : listFacilityTmp) {
				GenericValue fac = delegator.findOne("Facility", false,
						UtilMisc.toMap("facilityId", fa.getString("facilityId")));
				if (UtilValidate.isNotEmpty(facilityTypeId) && UtilValidate.isNotEmpty(fac)
						&& facilityTypeId.equals(fac.getString("facilityTypeId"))) {
					listFacilities.add(fac);
				}
			}
		}
		return listFacilities;
	}

	public static List<GenericValue> getFacilityByRolesAndFacilityTypes(Delegator delegator, String partyId,
			List<String> roleTypeIds, List<String> facilityTypeIds) throws GenericEntityException {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		List<GenericValue> listFacilityTmp = LogisticsPartyUtil.getFacilityByRoles(delegator, partyId, roleTypeIds);
		if (!listFacilityTmp.isEmpty()) {
			List<String> listFaIds = new ArrayList<String>();
			for (GenericValue fa : listFacilityTmp) {
				listFaIds.add(fa.getString("facilityId"));
			}
			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFaIds);
			EntityCondition facTypeCond = EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,
					facilityTypeIds);
			listFacilities = delegator.findList("Facility",
					EntityCondition.makeCondition(UtilMisc.toList(facTypeCond, facCond)), null, null, null, false);
		}
		return listFacilities;
	}

	public static List<GenericValue> getFacilityByRolesAndFacilityTypesAndOwner(Delegator delegator, String partyId,
			List<String> roleTypeIds, List<String> facilityTypeIds, String ownerPartyId) throws GenericEntityException {
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		List<GenericValue> listFacilityTmp = LogisticsPartyUtil.getFacilityByRoles(delegator, partyId, roleTypeIds);
		if (!listFacilityTmp.isEmpty()) {
			List<String> listFaIds = new ArrayList<String>();
			for (GenericValue fa : listFacilityTmp) {
				listFaIds.add(fa.getString("facilityId"));
			}
			EntityCondition facCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFaIds);
			EntityCondition facTypeCond = EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,
					facilityTypeIds);
			EntityCondition ownerCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,
					ownerPartyId);
			listFacilities = delegator.findList("Facility",
					EntityCondition.makeCondition(UtilMisc.toList(facTypeCond, facCond, ownerCond)), null, null, null,
					false);
		}
		return listFacilities;
	}

	public static Boolean checkStorekeeperOfFacility(Delegator delegator, String partyId, String facilityId)
			throws GenericEntityException {
		List<String> listPartyIds = LogisticsPartyUtil.getStorekeeperOfFacility(delegator, facilityId);
		for (String partyIdTmp : listPartyIds) {
			if (partyIdTmp.equals(partyId))
				return true;
		}
		return false;
	}

	public static List<String> getPartiesByRoles(Delegator delegator, List<String> roleTypeIds, boolean isPerson,
			GenericValue userLogin) {
		List<String> listPartyId = new ArrayList<String>();
		if (roleTypeIds == null)
			return listPartyId;
		for (String roleTypeId : roleTypeIds) {
			List<String> tmp = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, roleTypeId, delegator);
			if (tmp != null)
				listPartyId.addAll(tmp);
		}
		return listPartyId;
	}

	public static Boolean checkPartyHasRoles(Delegator delegator, List<String> roleTypeIds, String partyId) {
		Boolean check = false;
		if (roleTypeIds == null)
			return false;
		for (String roleTypeId : roleTypeIds) {
			check = SecurityUtil.hasRole(roleTypeId, partyId, delegator);
			if (!check)
				return check;
		}
		return check;
	}

	public static Boolean hasRolesWithFacility(Delegator delegator, List<String> roleTypeIds, String partyId,
			String facilityId) throws GenericEntityException {
		Boolean check = true;
		if (!roleTypeIds.isEmpty()) {
			for (String roleTypeId : roleTypeIds) {
				List<GenericValue> facilityParty = delegator.findList("FacilityParty",
						EntityCondition.makeCondition(
								UtilMisc.toMap("facilityId", facilityId, "roleTypeId", roleTypeId, "partyId", partyId)),
						null, null, null, false);
				facilityParty = EntityUtil.filterByDate(facilityParty);
				if (facilityParty.isEmpty()) {
					check = false;
					break;
				}
			}
		}
		return check;
	}
}
