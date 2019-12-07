package com.olbius.activemq.receive.handle.data;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.receive.handle.OlbiusReceiveData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;

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

		input.put("quantityUomId", product.getQuantityUom() != null ? product.getQuantityUom().getOwnId() : null);
		input.put("weightUomId", product.getWeightUom() != null ? product.getWeightUom().getOwnId() : null);
		input.put("heightUomId", product.getHeightUom() != null ? product.getHeightUom().getOwnId() : null);

		input.remove("quantity");

		input.remove("price");

		input.remove("productStoreId");

		input.remove("availableFromDate");

		input.remove("availableThruDate");

		input.remove("lastPrice");

		input.remove("minimumOrderQuantity");

		input.remove("itemDescription");

		input.remove("unitPrice");
		
		String productId = product.getOwnId();

		GenericValue value = null;

		if (productId != null) {
			value = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		}

		if (value == null) {
			try {
				productId = OlbiusReceiveData.BUS_CODE + OlbiusReceiveData.CACHE.get(delegator, product.getBusId() + "#Product");
			} catch (Exception e) {
				throw new GenericServiceException(e);
			}
			value = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		}

		if (value == null) {
			input.put("productId", productId);
			input.put("productCode", productId);
			dispatcher.runSync("createProduct", input);
			product.setOwnId(productId);
			callback.run(product, dispatcher, delegator, userLogin);
		} else if (!"Y".equals(product.getOwnParty()) && product.isUpdate()) {
			input.put("productId", productId);
			dispatcher.runSync("updateProduct", input);
			product.setOwnId(productId);
		} else {
			product.setOwnId(productId);
		}
	}

}
