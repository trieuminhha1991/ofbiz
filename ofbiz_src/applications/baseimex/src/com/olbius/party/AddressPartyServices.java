package com.olbius.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyUtil;

public class AddressPartyServices {
	public static final String module = AddressPartyServices.class.getName();

	public static Map<String, Object> getPartyTelecomNumbers(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech",
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()) {
				for (GenericValue ctm : listPartyContactMechs) {
					List<GenericValue> listContactMechPurpose = delegator.findList("PartyContactMechPurpose",
							EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId",
									(String) ctm.get("contactMechId"), "contactMechPurposeTypeId",
									contactMechPurposeTypeId)),
							null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()) {
						for (GenericValue contact : listContactMechPurpose) {
							List<GenericValue> listPostalAddress = delegator.findList("TelecomNumber",
									EntityCondition.makeCondition(
											UtilMisc.toMap("contactMechId", contact.get("contactMechId"))),
									null, null, null, false);
							for (GenericValue pa : listPostalAddress) {
								if (!listContactMechs.contains(pa)) {
									listContactMechs.add(pa);
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyTelecomNumbers", listContactMechs);
		return result;
	}

	public static Map<String, Object> getPartyContactMechs(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech",
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()) {
				for (GenericValue ctm : listPartyContactMechs) {
					List<GenericValue> listContactMechPurpose = delegator.findList("PartyContactMechPurpose",
							EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId",
									(String) ctm.get("contactMechId"), "contactMechPurposeTypeId",
									contactMechPurposeTypeId)),
							null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()) {
						for (GenericValue contact : listContactMechPurpose) {
							List<GenericValue> listPostalAddress = delegator.findList("PostalAddress",
									EntityCondition.makeCondition(
											UtilMisc.toMap("contactMechId", contact.get("contactMechId"))),
									null, null, null, false);
							for (GenericValue pa : listPostalAddress) {
								if (!listContactMechs.contains(pa)) {
									listContactMechs.add(pa);
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyContactMechs", listContactMechs);
		return result;
	}

	public static Map<String, Object> getPartyFinAccounts(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String finAccountTypeId = (String) context.get("finAccountTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listFinAccounts = new ArrayList<GenericValue>();
		try {
			listFinAccounts = delegator.findList("FinAccount",
					EntityCondition.makeCondition(
							UtilMisc.toMap("organizationPartyId", partyId, "finAccountTypeId", finAccountTypeId)),
					null, null, null, false);
			listFinAccounts = EntityUtil.filterByDate(listFinAccounts);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyFinAccounts", listFinAccounts);
		return result;
	}

	public static Map<String, Object> getPartyRepresents(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String) context.get("roleTypeIdTo");
		String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listPartyRepresents = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom",
							roleTypeIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId)),
					null, null, null, false);
			listRelationships = EntityUtil.filterByDate(listRelationships);
			if (!listRelationships.isEmpty()) {
				for (GenericValue relation : listRelationships) {
					List<GenericValue> listPartyRoles = delegator.findList("PartyPersonPartyRole",
							EntityCondition.makeCondition(
									UtilMisc.toMap("partyId", relation.get("partyIdTo"), "roleTypeId", roleTypeIdTo)),
							null, null, null, false);
					for (GenericValue pa : listPartyRoles) {
						if (!listPartyRepresents.contains(pa)) {
							listPartyRepresents.add(pa);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyRepresents", listPartyRepresents);
		return result;
	}

	public static Map<String, Object> getCurrencyUomIdBySupplier(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("currencyUomId");
		List<GenericValue> listSupp = delegator.findList("SupplierProduct",
				EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), fieldsToSelect, null, opts, false);
		result.put("listgetCurrencyUomIdBySupplier", listSupp);
		return result;
	}

	public static Map<String, Object> getPartyPrimaryEmails(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String contactMechTypeId = (String) context.get("contactMechTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech",
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()) {
				for (GenericValue contact : listPartyContactMechs) {
					List<GenericValue> contactMechs = delegator.findList(
							"ContactMech", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId",
									contact.get("contactMechId"), "contactMechTypeId", contactMechTypeId)),
							null, null, null, false);
					for (GenericValue pa : contactMechs) {
						if (!listContactMechs.contains(pa)) {
							listContactMechs.add(pa);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyPrimaryEmails", listContactMechs);
		return result;
	}
	
	public static Map<String, Object> getPartyPostalAddressByPurpose(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listPartyPostalAddress = PartyUtil.getPartyPostalAddressByPurpose(delegator, partyId, contactMechPurposeTypeId);
		result.put("partyId", partyId);
		result.put("listPartyPostalAddress", listPartyPostalAddress);
		return result;
	}
}
