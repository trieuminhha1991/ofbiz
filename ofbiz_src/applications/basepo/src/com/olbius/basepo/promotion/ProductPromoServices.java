package com.olbius.basepo.promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

public class ProductPromoServices {
	public static final String module = ProductPromoServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listProductPromo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			// check permission
			boolean hasPermission = security.hasEntityPermission("PROMOTION_PO", "_VIEW", userLogin);

			if (!hasPermission) {
				return ServiceUtil
						.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotViewPermission", locale));
			}

			String ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, ownerId));

			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
			Set<String> listSelectFields = new HashSet<String>();
			listSelectFields.add("productPromoId");
			listSelectFields.add("promoName");
			listSelectFields.add("createdDate");
			listSelectFields.add("fromDate");
			listSelectFields.add("thruDate");
			listSelectFields.add("statusId");
			listSelectFields.add("organizationPartyId");
			opts.setDistinct(true);

			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				if (UtilValidate.isNotEmpty(partyId)) {
					listAllConditions.add(EntityCondition.makeCondition("supplierId", EntityOperator.EQUALS, partyId));
				}
			}

			listIterator = delegator.find("ProductPromoApplSupplier", EntityCondition.makeCondition(listAllConditions),
					null, listSelectFields, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductPO(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		// Map<String,String[]> parameters = (Map<String, String[]>)
		// context.get("parameters");

		// result map
		List<Map<String, Object>> listProduct = new ArrayList<>();
		try {
			listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
			GenericValue item = null;
			while ((item = listIterator.next()) != null) {
				Map<String, Object> itemMap = new HashMap<>();
				itemMap.put("productId", item.getString("productId"));
				itemMap.put("productName", item.getString("productName") + " [" + item.getString("productCode") + "]");
				listProduct.add(itemMap);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally {
			try {
				listIterator.close();
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		}
		successResult.put("listIterator", listProduct);
		return successResult;
	}
}
