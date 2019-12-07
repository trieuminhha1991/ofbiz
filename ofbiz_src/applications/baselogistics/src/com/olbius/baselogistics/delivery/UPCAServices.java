package com.olbius.baselogistics.delivery;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class UPCAServices {
	public static Map<String, Object> getProductDetailByUPCA(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			result = dispatcher.runSync("getProductIdAndAmountByUPCA", context);
			if (ServiceUtil.isSuccess(result)) {
				Object productId = result.get("productId");
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
				if (UtilValidate.isNotEmpty(product)) {
					Map<String, Object> productDetail = FastMap.newInstance();
					productDetail.put("productId", productId);
					productDetail.put("price", result.get("price"));
					productDetail.put("amount", result.get("amount"));
					productDetail.put("productCode", product.get("productCode"));
					productDetail.put("productName", product.get("productName"));
					productDetail.put("quantityUomId", product.get("quantityUomId"));
					productDetail.put("weightUomId", product.get("weightUomId"));
					productDetail.put("weightUomName", getUomDescription(delegator, locale, product.get("weightUomId")));
					productDetail.put("idUPCA", context.get("idUPCA"));
					result.clear();
					result.put("productDetail", productDetail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Service getDeliveryById:" + e.toString());
		}
		return result;
	}

	public static String getUomDescription(Delegator delegator, Locale locale, Object uomId) throws GenericEntityException {
		String description = "";
		GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), true);
		if (UtilValidate.isNotEmpty(uom)) {
			description = (String) uom.get("description", locale);
		}
		return description;
	}
}
