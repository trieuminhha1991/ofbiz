package com.olbius.marketing;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class MarketingSettingServices {
	public static final String module = MarketingSettingServices.class.getName();
	public static final String resource = "MarketingUiLabels";
	public static final String resource_error = "SalesErrorUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCosts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator del = ctx.getDelegator();
		List<GenericValue> listIterator = FastList.newInstance();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityCondition tmpCondition = null;
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId", "MARKETING_COST"));
			tmpCondition = EntityCondition.makeCondition(listAllConditions);
			listIterator = del.findList("Enumeration", tmpCondition, null, listSortFields, opts, false);
		} catch (Exception e) {
			e.printStackTrace();
			successResult = ServiceUtil.returnError("error");
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createCostsType(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess(
				UtilProperties.getMessage("CustomMarketingUiLabels", "KSuccessful", (Locale) context.get("locale")));
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue inset = delegator.makeValidValue("Enumeration", context);
			String enumId = (String) context.get("enumId");
			if (UtilValidate.isEmpty(enumId)) {
				enumId = delegator.getNextSeqId("Enumeration");
				inset.set("enumId", enumId);
			}
			inset.set("enumTypeId", "MARKETING_COST");
			delegator.createOrStore(inset);
			result.put("enumId", enumId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> editCostsType(DispatchContext dcpt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("CustomMarketingUiLabels",
				"KUpdateSuccessful", (Locale) context.get("locale")));
		Delegator delegator = dcpt.getDelegator();
		try {
			String enumId = (String) context.get("enumId");
			if (!UtilValidate.isEmpty(enumId)) {
				GenericValue inset = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
				inset.setNonPKFields(context);
				inset.store();
			}
			result.put("enumId", enumId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> disableCostsType(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("CustomMarketingUiLabels",
				"KDisableSuccess", (Locale) context.get("locale")));
		Delegator delegator = ctx.getDelegator();
		try {
			String aCostsType = (String) context.get("aCostsType");
			JSONObject listJson = JSONObject.fromObject(aCostsType);
			if (UtilValidate.isNotEmpty(listJson)) {
				String marketingCostTypeId = (String) listJson.get("marketingCostTypeId");
				String name = (String) listJson.get("name");
				String description = (String) listJson.get("description");
				Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
				Timestamp thruDateNew = UtilDateTime.nowTimestamp();
				GenericValue member = delegator.findOne("MarketingCostType",
						UtilMisc.toMap("marketingCostTypeId", marketingCostTypeId), false);
				if (UtilValidate.isNotEmpty(member)) {
					member.set("marketingCostTypeId", marketingCostTypeId);
					member.set("name", name);
					member.set("description", description);
					member.set("fromDate", fromDate);
					if (fromDate.after(thruDateNew)) {
						member.set("thruDate", fromDate);
					} else {
						member.set("thruDate", thruDateNew);
					}
					member.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCommSubject(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator del = ctx.getDelegator();
		List<GenericValue> listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityCondition tmpCondition = null;
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId", "COMM_SUBJECT"));
			tmpCondition = EntityCondition.makeCondition(listAllConditions);
			listIterator = del.findList("Enumeration", tmpCondition, null, listSortFields, opts, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListCommSubject service: " + e.toString();
			Debug.log(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createCommSubject(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess(
				UtilProperties.getMessage("CustomMarketingUiLabels", "KSuccessful", (Locale) context.get("locale")));
		Delegator delegator = dctx.getDelegator();
		try {
			String enumId = delegator.getNextSeqId("Enumeration");
			String enumCode = (String) context.get("enumCode");
			enumCode = enumCode.toUpperCase();
			String sequenceId = (String) context.get("sequenceId");
			String description = (String) context.get("description");
			String enumTypeId = "COMM_SUBJECT";
			GenericValue reasonClaim = delegator.makeValue("Enumeration");
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			reasonClaim.set("enumId", enumId);
			reasonClaim.set("enumCode", enumCode);
			reasonClaim.set("sequenceId", sequenceId);
			reasonClaim.set("description", description);
			reasonClaim.set("enumTypeId", enumTypeId);
			delegator.create(reasonClaim);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> editCommSubject(DispatchContext dcpt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage("CustomMarketingUiLabels",
				"KUpdateSuccessful", (Locale) context.get("locale")));
		Delegator delegator = dcpt.getDelegator();
		try {
			String enumId = (String) context.get("enumId");
			String enumTypeId = (String) context.get("enumTypeId");
			String sequenceId = (String) context.get("sequenceId");
			String enumCode = (String) context.get("enumCode");
			String description = (String) context.get("description");
			GenericValue reasonClaim = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
			if (UtilValidate.isNotEmpty(reasonClaim)) {
				reasonClaim.set("enumId", enumId);
				reasonClaim.set("description", description);
				reasonClaim.set("enumTypeId", enumTypeId);
				reasonClaim.set("sequenceId", sequenceId);
				reasonClaim.set("enumCode", enumCode);
				reasonClaim.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
}