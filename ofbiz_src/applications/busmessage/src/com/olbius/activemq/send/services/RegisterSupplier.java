package com.olbius.activemq.send.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.PartyGroup;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.SupplierProduct;
import com.olbius.jms.event.OfbizDataServices;
import com.olbius.jms.event.OlbiusEvent;
import com.olbius.service.OlbiusService;

public class RegisterSupplier implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dctx.getDelegator();

		LocalDispatcher dispatcher = dctx.getDispatcher();

		String productStoreId = (String) context.get("productStoreId");

		List<?> products = (List<?>) context.get("products[]");

		if (products == null) {

			products = new ArrayList<>();
			
			Map<String, Object> map = dispatcher.runSync("getListProductIdByProductStoreId",
					UtilMisc.toMap("userLogin", context.get("userLogin"), "productStoreId", productStoreId));

			products = (List<?>) map.get("listProductIds");

		}

		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
				false);

		String partyId = productStore.getString("payToPartyId");

		OlbiusMessage data = new OlbiusMessage();

		data.setType(OlbiusEvent.REGISTER_SUPPLIER);

		SupplierProduct supplierProduct = new SupplierProduct();

		PartyGroup group = OfbizDataServices.getPartyGroup(delegator, partyId);

		supplierProduct.setGroup(group);
		
		data.getDatas().add(supplierProduct);
		
		for(Object x: products) {
			
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", x), false);
		
			if(product != null) {
				
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("userLogin", context.get("userLogin"));

				map.put("product", product);

				map.put("productStoreId", productStoreId);

				map.put("quantityUomId", product.get("quantityUomId"));

				map.put("quantity", new BigDecimal(1));

				Map<String, Object> tmpResult = dispatcher.runSync("calculateProductPriceCustom", map);

				if(((BigDecimal)tmpResult.get("basePrice")).compareTo(BigDecimal.ZERO) > 0) {
					Product p = OfbizDataServices.getProduct(delegator, product.getString("productId"));

					p.getMessageData().put("price", tmpResult.get("basePrice"));

					p.getMessageData().put("quantity", new BigDecimal(1));

					p.getMessageData().put("productStoreId", productStoreId);

					p.setQuantityUom(OfbizDataServices.getUom(delegator, (String) product.get("quantityUomId")));
					p.setCurrencyUom(OfbizDataServices.getUom(delegator, (String) tmpResult.get("currencyUsed")));

					supplierProduct.getProducts().add(p);
				}
				
			}
			
		}

		MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(data);

		return ServiceUtil.returnSuccess();
	}

}
