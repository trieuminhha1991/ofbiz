package com.olbius.basepo.configPacking;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepo.utils.POUtil;
import com.olbius.security.util.SecurityUtil;

public class ConfigPackingServices {
	public static final String module = ConfigPackingServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUomTypeAndConfigPacking(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listAllConditions.add(EntityCondition.makeCondition("quantityConvert", EntityOperator.GREATER_THAN, BigDecimal.ONE));
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listIterator = delegator.find("ConfigPackingAndProduct", EntityCondition.makeCondition(listAllConditions),
					null, null, listSortFields, opts);
			Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			List<GenericValue> list = POUtil.getIteratorPartialList(listIterator, parameters, successResult);
			successResult.put("listIterator", list);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling UomTypeAndConversionDated service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}

	public static Map<String, Object> UpdateProductConfigPacking(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		boolean hasPermission1 = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"CONFIG_PRODPACK_NEW");
		boolean hasPermission2 = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"CONFIG_PRODPACK_EDIT");
		if (!hasPermission1 && !hasPermission2) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}

		String productId = (String) context.get("productId");
		String uomFromId = (String) context.get("uomFromId");
		String uomToId = (String) context.get("uomToId");
		String description = (String) context.get("description");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		BigDecimal quantityConvert = (BigDecimal) context.get("quantityConvert");

		GenericValue configPacking = delegator.makeValue("ConfigPacking");
		configPacking.put("uomFromId", uomFromId);
		configPacking.put("uomToId", uomToId);
		configPacking.put("productId", productId);
		configPacking.put("fromDate", fromDate);
		configPacking.put("thruDate", thruDate);
		configPacking.put("quantityConvert", quantityConvert);
		configPacking.put("description", description);
		try {
			delegator.createOrStore(configPacking);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
}