package com.olbius.basepo.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;

public class POWorker {
	public static boolean equalObject(BigDecimal obj1, BigDecimal obj2) {
		boolean result = false;
		
		if (obj1 == null) {
			if (obj2 == null) {
				return true;
			} else {
				return false;
			}
		} else {
			if (obj2 == null) {
				return false;
			} else {
				if (obj1.compareTo(obj2) == 0) {
					return true;
				}
			}
		}
		
		return result;
	}
	
	public static boolean equalObject(String obj1, String obj2) {
		if (UtilValidate.isNotEmpty(obj1)) {
			if (obj1.equals(obj2)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (UtilValidate.isEmpty(obj2)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static List<GenericValue> getListFacilityDisplayByUserLogin(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<GenericValue> listFacility = new ArrayList<GenericValue>();
		if (delegator == null || userLogin == null) return listFacility;
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Boolean poEmpl = com.olbius.basehr.util.SecurityUtil.hasRole("PO_EMPLOYEE", userLogin.getString("partyId"), delegator);
		if (poEmpl){
			Boolean poManager = com.olbius.basehr.util.SecurityUtil.hasRole("PO_MANAGER", userLogin.getString("partyId"), delegator);
			if (poManager) {
				listFacility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company), null, null, null, false);
    		} else {
    			Boolean storeManager = com.olbius.basehr.util.SecurityUtil.hasRole("STORE_MANAGER", userLogin.getString("partyId"), delegator);
    			if (storeManager){
    				EntityCondition condPt = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId"));
    	    		EntityCondition condMn = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
    	    		EntityCondition condTm = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr());
    	    		List<EntityCondition> conds = FastList.newInstance();
    	    		conds.add(condPt);
    	    		conds.add(condMn);
    	    		conds.add(condTm);
    	    		List<GenericValue> listProductStores = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
    	    		List<String> listStoreIds = FastList.newInstance();
    	    		listStoreIds = EntityUtil.getFieldListFromEntityList(listProductStores, "productStoreId", true);
    	    		EntityCondition condStore = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, listStoreIds);
    	    		List<EntityCondition> cond2s = FastList.newInstance();
    	    		cond2s.add(condTm);
    	    		cond2s.add(condStore);
    	    		List<GenericValue> listStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(cond2s), null, null, null, false);
    	    		if (UtilValidate.isNotEmpty(listStoreFacility)) {
    	    			List<String> listFacilityIds = EntityUtil.getFieldListFromEntityList(listStoreFacility, "facilityId", true);
    	    			listFacility = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds), null, null, null, false);
    	    		}
    			}
    		}
		}
		if (UtilValidate.isEmpty(listFacility)) {
			listFacility = delegator.findByAnd("Facility", null, null, false);
		}
		return listFacility;
	}
	
	public static List<String> getListFacilityIdsConstrainByUserLogin(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<String> listFacilityIds = new ArrayList<String>();
		if (delegator == null || userLogin == null) return listFacilityIds;
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Boolean poEmpl = com.olbius.basehr.util.SecurityUtil.hasRole("PO_EMPLOYEE", userLogin.getString("partyId"), delegator);
		if (poEmpl){
			Boolean poManager = com.olbius.basehr.util.SecurityUtil.hasRole("PO_MANAGER", userLogin.getString("partyId"), delegator);
			if (poManager) {
	    		List<GenericValue> listFacilitys = delegator.findList("Facility",
						EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company), null, null, null, false);
				listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacilitys, "facilityId", true);
    		} else {
    			Boolean storeManager = com.olbius.basehr.util.SecurityUtil.hasRole("STORE_MANAGER", userLogin.getString("partyId"), delegator);
    			if (storeManager){
    				EntityCondition condPt = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId"));
    	    		EntityCondition condMn = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
    	    		EntityCondition condTm = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr());
    	    		List<EntityCondition> conds = FastList.newInstance();
    	    		conds.add(condPt);
    	    		conds.add(condMn);
    	    		conds.add(condTm);
    	    		List<GenericValue> listProductStores = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
    	    		List<String> listStoreIds = FastList.newInstance();
    	    		listStoreIds = EntityUtil.getFieldListFromEntityList(listProductStores, "productStoreId", true);
    	    		EntityCondition condStore = EntityCondition.makeCondition("productStoreId", EntityOperator.IN, listStoreIds);
    	    		List<EntityCondition> cond2s = FastList.newInstance();
    	    		cond2s.add(condTm);
    	    		cond2s.add(condStore);
    	    		List<GenericValue> listStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(cond2s), null, null, null, false);
    	    		listFacilityIds = EntityUtil.getFieldListFromEntityList(listStoreFacility, "facilityId", true);
    			}
    		}
		}
		return listFacilityIds;
	}
}