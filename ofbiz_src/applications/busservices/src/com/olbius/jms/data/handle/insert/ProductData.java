package com.olbius.jms.data.handle.insert;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.container.BusContainer;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.data.handle.OlbiusBusConvert;
import com.olbius.jms.data.handle.OlbiusBusData;

public class ProductData implements Insert {

	@Override
	public void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Callback callback) throws GenericEntityException, GenericServiceException {

		Product product = (Product) message;

		Map<String, Object> input = new HashMap<String, Object>();

		input.put("userLogin", userLogin);

		input.putAll(product.getMessageData());

		if (input.get("internalName") == null) {
			input.put("internalName", "internalName");
		}

		if (input.get("productTypeId") == null) {
			input.put("productTypeId", "FINISHED_GOOD");
		}

		input.put("quantityUomId", product.getQuantityUom() != null ? product.getQuantityUom().getBusId() : null);
		input.put("weightUomId", product.getWeightUom() != null ? product.getWeightUom().getBusId() : null);
		input.put("heightUomId", product.getHeightUom() != null ? product.getHeightUom().getBusId() : null);

		input.remove("quantity");

		input.remove("price");

		input.remove("productStoreId");

		input.remove("availableFromDate");

		input.remove("availableThruDate");

		input.remove("lastPrice");

		input.remove("minimumOrderQuantity");
		
		input.remove("unitPrice");
		
		input.remove("itemDescription");
		
		GenericValue value = delegator.findOne("Product", UtilMisc.toMap("productId", product.getBusId()), false);

		if (value == null) {
			input.put("productId", product.getBusId());
			dispatcher.runSync("createProduct", input);
		} else {
			input.put("productId", product.getBusId());
			dispatcher.runSync("updateProduct", input);
		}

		try {
			OlbiusBusData.CACHE.get(delegator, product.getBusId());
		} catch (Exception e) {
			BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
		}
	}

}
