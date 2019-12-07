package com.olbius.basesales.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;

public class ProductStoreWorker {
	public static String module = SalesUtil.class.getName();
	public static String RESOURCE_PROPERTIES = "basesales.properties";
	
	public static List<GenericValue> getListProductStoreSell(Delegator delegator, GenericValue userLogin) {
		if (delegator == null || userLogin == null) return null;
		return getListProductStoreSell(delegator, userLogin, userLogin.getString("partyId"), true);
	}
	public static List<GenericValue> getListProductStoreSell(Delegator delegator, GenericValue userLogin, String partyId, Boolean checkOrg) {
		if (delegator == null || (userLogin == null && partyId == null)) return null;
		if (UtilValidate.isEmpty(partyId)) partyId = userLogin.getString("partyId");
		return getListProductStoreSell(delegator, userLogin, partyId, true, null, null, null, null);
	}
	public static List<GenericValue> getListProductStoreSell(Delegator delegator, GenericValue userLogin, String partyId, Boolean checkOrg, List<EntityCondition> listAllConditions, Set<String> listSelectFields, List<String> listSortFields, EntityFindOptions opts) {
		if (delegator == null || userLogin == null || partyId == null) return null;
		String roleSeller = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.sell.in.store", delegator);
		List<GenericValue> listProductStore = null;
		try {
			if (listAllConditions == null) listAllConditions = new ArrayList<EntityCondition>();
			if (checkOrg) {
				String organizationId = null;
				if (SalesPartyUtil.isEmployee(delegator, partyId) && !SalesPartyUtil.isSalesman(delegator, partyId) && !SalesPartyUtil.isSalessup(delegator, partyId)) {
					organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationId));
				}
			}
			if (!UtilValidate.isNotEmpty(listSortFields)) {
				listSortFields = UtilMisc.<String>toList("productStoreId", "storeName");
			}
			
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", roleSeller));
			listProductStore = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), listSelectFields, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method getListProductStoreSell", module);
		}
		return listProductStore;
	}
	public static List<GenericValue> getListProductStoreView(Delegator delegator, GenericValue userLogin, String partyId, Boolean checkOrg) {
		if (delegator == null || (userLogin == null && partyId == null)) return null;
		if (UtilValidate.isEmpty(partyId)) partyId = userLogin.getString("partyId");
		return getListProductStoreView(delegator, userLogin, partyId, checkOrg, null, null, null, null);
	}
	public static List<GenericValue> getListProductStoreView(Delegator delegator, GenericValue userLogin, String partyId, Boolean checkOrg, List<EntityCondition> listAllConditions, Set<String> listSelectFields, List<String> listSortFields, EntityFindOptions opts) {
		if (delegator == null || userLogin == null || partyId == null) return null;
		List<GenericValue> listProductStore = null;
		try {
			if (listAllConditions == null) listAllConditions = new ArrayList<EntityCondition>();
			if (checkOrg) {
				String organizationId = null;
				if (!SalesPartyUtil.isSalesman(delegator, partyId) && SalesPartyUtil.isEmployee(delegator, partyId)) {
					organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					listAllConditions.add(EntityCondition.makeCondition("payToPartyId", organizationId));
				}
			}
			if (!UtilValidate.isNotEmpty(listSortFields)) {
				listSortFields = UtilMisc.<String>toList("productStoreId", "storeName");
			}
			
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, null));
			listProductStore = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), listSelectFields, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method getListProductStoreView", module);
		}
		return listProductStore;
	}
	
	public static List<GenericValue> getListProductStoreSellByCustomer(Delegator delegator, GenericValue userLogin) {
		if (delegator == null || userLogin == null || userLogin.get("partyId") == null) return null;
		String roleCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.customer.in.store", delegator);
		List<GenericValue> listProductStore = null;
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			listAllCondition.add(EntityCondition.makeCondition("partyId", userLogin.get("partyId")));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeId", roleCustomer));
			listProductStore = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, UtilMisc.toList("productStoreId", "storeName"), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method getListProductStoreSellByCustomer", module);
		}
		return listProductStore;
	}
	
	public static List<String> getProductStoreIdContainCustomer(Delegator delegator, String partyId) {
		List<String> returnValue = new ArrayList<String>();
		String roleCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.customer", delegator);
		try {
			List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", roleCustomer));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
			if (productStoreRoles != null) {
				returnValue = EntityUtil.getFieldListFromEntityList(productStoreRoles, "productStoreId", true);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getProductStoreIdContainCustomer: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListProductStoreByRole(Delegator delegator, String partyId, String roleTypeId) throws GenericEntityException{
		List<GenericValue> listIterator = null;
		List<String> listSortFields = FastList.newInstance();
		listSortFields.add("productStoreId");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Set<String> fieldsToSelect = UtilMisc.toSet("productStoreId");
		fieldsToSelect.add("salesMethodChannelEnumId");
		fieldsToSelect.add("viewCartOnAdd");
		fieldsToSelect.add("requireCustomerRole");
		fieldsToSelect.add("headerDeclinedStatus");
		fieldsToSelect.add("requireInventory");
		fieldsToSelect.add("checkInventory");
		fieldsToSelect.add("partyId");
		fieldsToSelect.add("autoSaveCart");
		fieldsToSelect.add("inventoryFacilityId");
		fieldsToSelect.add("thruDate");
		fieldsToSelect.add("fromDate");
		fieldsToSelect.add("statusId");
		fieldsToSelect.add("storeName");
		fieldsToSelect.add("roleTypeId");
		fieldsToSelect.add("defaultCurrencyUomId");
		fieldsToSelect.add("vatTaxAuthGeoId");
		fieldsToSelect.add("vatTaxAuthPartyId");
		fieldsToSelect.add("payToPartyId");
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		listAllConditions.add(EntityCondition.makeCondition("statusId", "PRODSTORE_ENABLED"));
		listIterator = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), fieldsToSelect, listSortFields, opts, false);
		if (listIterator == null) listIterator = new ArrayList<GenericValue>();
		return listIterator;
	}
	
	public static List<GenericValue> getListProductStore(Delegator delegator, GenericValue userLogin) {
		return getListProductStore(delegator, userLogin, false);
	}
	public static List<GenericValue> getListProductStore(Delegator delegator, GenericValue userLogin, Boolean isOwner) {
		if (delegator == null || userLogin == null || userLogin.get("partyId") == null) return null;
		List<GenericValue> listProductStore = null;
		try {
			String organizationId = null;
			if (isOwner == null || !isOwner) {
				organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			} else {
				organizationId = userLogin.getString("partyId");
			}
			listProductStore = delegator.findByAnd("ProductStore", UtilMisc.toMap("payToPartyId", organizationId), UtilMisc.toList("productStoreId", "storeName"), false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method getListProductStore", module);
		}
		return listProductStore;
	}
}
