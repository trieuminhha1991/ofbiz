package com.olbius.basepo.productFacility;

import java.math.BigDecimal;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class ProductFacilityServices {
	public static final String module = ProductFacilityServices.class.getName();

	public static Map<String, Object> fullFillProductFaccility(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String inventoryItemId = (String) context.get("inventoryItemId");
		BigDecimal quantityOnHandDiff = (BigDecimal) context.get("quantityOnHandDiff");
		String productId = null;
		String facilityId = null;
		try {
			GenericValue invItem = delegator.findOne("InventoryItem",
					UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
			if (invItem != null) {
				productId = invItem.getString("productId");
				facilityId = invItem.getString("facilityId");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (productId != null && facilityId != null) {
			try {
				GenericValue checkProductFacility = delegator.findOne("ProductFacility",
						UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
				if (checkProductFacility == null) {
					GenericValue productFacility = delegator.makeValue("ProductFacility");
					productFacility.put("productId", productId);
					productFacility.put("facilityId", facilityId);
					productFacility.put("lastInventoryCount", quantityOnHandDiff);
					try {
						delegator.create(productFacility);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				} else {
					BigDecimal lastInventoryCount = checkProductFacility.getBigDecimal("lastInventoryCount");
					checkProductFacility.put("lastInventoryCount", quantityOnHandDiff.add(lastInventoryCount));
					delegator.store(checkProductFacility);
				}
			} catch (GenericEntityException e1) {
				e1.printStackTrace();
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> getProductFaccility(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("facilityId");

		Map<String, Object> result = FastMap.newInstance();
		try {
			GenericValue productFacility = delegator.findOne("ProductFacility",
					UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
			result.put("productFacility", productFacility);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
}
