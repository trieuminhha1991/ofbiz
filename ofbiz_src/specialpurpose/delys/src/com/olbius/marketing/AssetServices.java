package com.olbius.marketing;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.sun.net.httpserver.Authenticator.Success;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AssetServices {
	public static final String module = AssetServices.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceCustom = "DelysMarketingUiLabels";

	public static Map<String, Object> getListCooler(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition("fixedAssetTypeId",
				"COOLER"));
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("fixedAssetId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		try {
			listIterator = delegator.find("FixedAsset", tmpCond, null, null,
					listSortFields, opts);
			List<GenericValue> res = listIterator.getPartialList(start, end);
			successResult.put("TotalRows",
					String.valueOf(listIterator.getCompleteList().size()));
			for (GenericValue tmp : res) {
				Map<String, Object> cur = FastMap.newInstance();
				cur.putAll(tmp);
				List<Map<String, Object>> con = ContactMechWorker
						.getPartyContactMechValueMaps(delegator,
								(String) tmp.get("partyId"), false,
								"TELECOM_NUMBER");
				if (!con.isEmpty()) {
					Map<String, Object> contact = (Map<String, Object>) con
							.get(0);
					GenericValue phone = (GenericValue) contact
							.get("telecomNumber");
					cur.put("contactNumber", phone.get("contactNumber"));
					cur.put("countryCode", phone.get("countryCode"));
					cur.put("areaCode", phone.get("areaCode"));
				}
				listReturn.add(cur);
			}
			// while (tmpList.next() != null) {
			// }
			listIterator.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static Map<String, Object> getListCoolerInventory(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition("fixedAssetTypeId",
				"COOLER"));
		listAllConditions.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, null));
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("fixedAssetId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");

		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		try {
			listIterator = delegator.find("FixedAsset", tmpCond, null, null,
					listSortFields, opts);
			successResult.put("TotalRows",
					String.valueOf(listIterator.getCompleteList().size()));
			List<GenericValue> res = listIterator.getPartialList(start, end);
			for (GenericValue tmp : res) {
				Map<String, Object> cur = FastMap.newInstance();
				cur.putAll(tmp);
				List<Map<String, Object>> con = ContactMechWorker
						.getPartyContactMechValueMaps(delegator,
								(String) tmp.get("partyId"), false,
								"TELECOM_NUMBER");
				if (!con.isEmpty()) {
					Map<String, Object> contact = (Map<String, Object>) con
							.get(0);
					GenericValue phone = (GenericValue) contact
							.get("telecomNumber");
					cur.put("contactNumber", phone.get("contactNumber"));
					cur.put("countryCode", phone.get("countryCode"));
					cur.put("areaCode", phone.get("areaCode"));
				}
				listReturn.add(cur);
			}
			listIterator.close();
			// while (tmpList.next() != null) {
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static Map<String, Object> getListCoolerAgreement(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition("fixedAssetTypeId",
				"COOLER"));
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("fixedAssetId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "DELYS_CUSTOMER_GT"));
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		try {
			listIterator = delegator.find("FixedAssetPartyRole", tmpCond, null,
					null, listSortFields, opts);
			successResult.put("TotalRows",
					String.valueOf(listIterator.getCompleteList().size()));
			List<GenericValue> res = listIterator.getPartialList(start, end);
			for (GenericValue tmp : res) {
				Map<String, Object> cur = FastMap.newInstance();
				cur.putAll(tmp);
				List<Map<String, Object>> con = ContactMechWorker
						.getPartyContactMechValueMaps(delegator,
								(String) tmp.get("partyId"), false,
								"TELECOM_NUMBER");
				if (!con.isEmpty()) {
					Map<String, Object> contact = (Map<String, Object>) con
							.get(0);
					GenericValue phone = (GenericValue) contact
							.get("telecomNumber");
					cur.put("contactNumber", phone.get("contactNumber"));
					cur.put("countryCode", phone.get("countryCode"));
					cur.put("areaCode", phone.get("areaCode"));
				}
				listReturn.add(cur);
			}
			listIterator.close();
			// while (tmpList.next() != null) {
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static Map<String, Object> getListCoolerMaintenance(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("maintHistSeqId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		Locale locale = (Locale)context.get("locale");
		try {
			listIterator = delegator.find("FixedAssetMaintPayment", tmpCond,
					null, null, listSortFields, opts);
			successResult.put("TotalRows",
					String.valueOf(listIterator.getCompleteList().size()));
			List<GenericValue> res = listIterator.getPartialList(start, end);
			for (GenericValue tmp : res) {
				Map<String, Object> cur = FastMap.newInstance();
				cur.putAll(tmp);
				String status = UtilProperties.getMessage(resourceCustom,
	                  (String) cur.get("statusId"), locale);
				cur.put("statusId", status);
				listReturn.add(cur);
			}
			listIterator.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static Map<String, Object> requestCashAdvance(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String fixedAssetId = (String) context.get("fixedAssetId");
		String estimatedCost = (String) context.get("estimatedCost");
		BigDecimal ec = new BigDecimal(estimatedCost);
		Timestamp startDate = (Timestamp) context.get("startDate");
		try {
			GenericValue fam = delegator.makeValue("FixedAssetMaint");
			String maintId = delegator.getNextSeqId("FixedAssetMaint");
			fam.set("fixedAssetId", fixedAssetId);
			fam.set("statusId", "FAM_CREATED");
			fam.set("maintHistSeqId", maintId);
			delegator.create(fam);
			if (!maintId.isEmpty()) {
				GenericValue inset = delegator
						.makeValue("CoolerMaintenancePayment");
				inset.set("maintHistSeqId", maintId);
				inset.set("marketingCostTypeId", "COOLER_MAINT");
				inset.set("estimatedCost", ec);
				inset.set("startDate", startDate);
				delegator.create(inset);
			}
			successResult.put("message", "success");
		} catch (Exception e) {
			successResult.put("message", "error");
			e.printStackTrace();
		}
		return successResult;
	}
}
