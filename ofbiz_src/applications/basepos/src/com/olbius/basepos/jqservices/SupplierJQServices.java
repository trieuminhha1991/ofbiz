package com.olbius.basepos.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

public class SupplierJQServices {
	public static final String module = SupplierJQServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListSupplier(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(mapCondition));
		conditions.add(EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND));
		try {
			listIterator = delegator.find("SupplierAndInfo",
					EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqListSupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductStorePOS(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator del = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			String organization = MultiOrganizationUtil.getCurrentOrganization(del, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organization));
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productStoreId");
				listSortFields.add("posTerminalId");
			}
			listIterator = del.find("TerminalFacilityStore", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductStorePOS service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListFacilityByStore(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator del = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		String productStoreId = null;
		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
			productStoreId = parameters.get("productStoreId")[0];
		}

		EntityCondition storeCond = EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId));
		EntityCondition dateCond = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
		listAllConditions.add(dateCond);
		listAllConditions.add(storeCond);
		try {
			EntityCondition tmpCondition = null;
			tmpCondition = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			Set<String> listSelectFields = FastSet.newInstance();
			listSelectFields.add("facilityId");
			listSelectFields.add("facilityTypeId");
			listSelectFields.add("facilityName");
			listSelectFields.add("sequenceNum");
			opts.setDistinct(true);
			listIterator = del.findList("ProductStoreFacilityAndFacility", tmpCondition, listSelectFields,
					listSortFields, opts, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListFacilityByStore service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

}
