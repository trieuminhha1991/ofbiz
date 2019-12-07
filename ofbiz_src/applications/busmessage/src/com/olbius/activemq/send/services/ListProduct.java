package com.olbius.activemq.send.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.service.OlbiusService;

public class ListProduct implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		Map<String, Object> result = ServiceUtil.returnSuccess();

		List<Map<String, Object>> data = new ArrayList<>();

		String productStoreId = (String) context.get("productStoreId");

		if(productStoreId == null) {
			return result;
		}
		
		Map<String, Object> map = dctx.getDispatcher().runSync("getListProductIdByProductStoreId",
				UtilMisc.toMap("userLogin", context.get("userLogin"), "productStoreId", productStoreId));

		List<?> list = (List<?>) map.get("listProductIds");

		for (Object x : list) {

			GenericValue value = dctx.getDelegator().findOne("Product", UtilMisc.toMap("productId", x), false);

			if (value != null) {

				Map<String, Object> tmp = new HashMap<>();

				tmp.put("productId", value.get("productId"));
				tmp.put("productName", value.get("productName"));

				GenericValue val = dctx.getDelegator().findOne("Uom",
						UtilMisc.toMap("uomId", value.get("quantityUomId")), false);

				tmp.put("uom", val.get("description") != null ? val.get("description") : val.get("abbreviation"));

				Map<String, Object> input = new HashMap<String, Object>();

				input.put("userLogin", context.get("userLogin"));

				input.put("product", value);

				input.put("productStoreId", productStoreId);

				input.put("quantityUomId", value.get("quantityUomId"));

				input.put("quantity", new BigDecimal(1));

				Map<String, Object> tmpResult = dctx.getDispatcher().runSync("calculateProductPriceCustom", input);

				if(((BigDecimal)tmpResult.get("basePrice")).compareTo(BigDecimal.ZERO) > 0) {
					tmp.put("price", tmpResult.get("basePrice"));
					data.add(tmp);
				}

			}

		}

		result.put("data", data);
		
		return result;
	}

}
