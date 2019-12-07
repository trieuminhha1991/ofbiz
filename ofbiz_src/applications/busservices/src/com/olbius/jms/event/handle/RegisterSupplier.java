package com.olbius.jms.event.handle;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import com.olbius.activemq.container.BusContainer;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.PartyGroup;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.SupplierProduct;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;

/**
 * @author Nguyen Ha
 *
 */
public class RegisterSupplier extends AbstractOlbiusBusEvent {

	private void insert(SupplierProduct supplierProduct) throws GenericServiceException, GenericEntityException {

		PartyGroup group = supplierProduct.getGroup();

		for (Product product : supplierProduct.getProducts()) {

			List<GenericValue> genericValues = delegator.findByAnd(
					"SupplierProduct", UtilMisc.toMap("partyId", group.getBusId(), "productId", product.getBusId(), "currencyUomId",
							product.getCurrencyUom().getBusId(), "quantityUomId", product.getQuantityUom().getBusId()),
					UtilMisc.toList("-availableFromDate"), false);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			Map<String, Object> input = new HashMap<String, Object>();

			input.put("userLogin", userLogin);

			input.put("partyId", group.getBusId());
			input.put("productId", product.getBusId());
			input.put("currencyUomId", product.getCurrencyUom().getBusId());
			input.put("quantityUomId", product.getQuantityUom().getBusId());
			// input.put("minimumOrderQuantity", product.getQuantity());
			input.put("lastPrice", product.getMessageData().get("price"));

			boolean flagUpdate = false;

			boolean flagCreate = false;

			for (GenericValue value : genericValues) {

				if (value.getBigDecimal("minimumOrderQuantity").compareTo((BigDecimal) product.getMessageData().get("quantity")) == 0) {

					flagUpdate = true;

					if (value.getTimestamp("availableThruDate") != null && timestamp.after(value.getTimestamp("availableThruDate"))) {
						flagCreate = true;
					}

					if (flagCreate) {

						input.put("availableThruDate", timestamp);

					}

				} else if ((value.getTimestamp("availableThruDate") != null && timestamp.before(value.getTimestamp("availableThruDate"))
						|| value.getTimestamp("availableThruDate") == null)) {

					input.put("availableThruDate", timestamp);

				}

				input.put("minimumOrderQuantity", value.getBigDecimal("minimumOrderQuantity"));

				input.put("availableFromDate", value.getTimestamp("availableFromDate"));

				dispatcher.runSync("updateSupplierProduct", input);

			}

			if ((flagUpdate && flagCreate) || !flagUpdate) {

				input.put("minimumOrderQuantity", product.getMessageData().get("quantity"));

				input.put("availableFromDate", timestamp);

				input.put("availableThruDate", null);

				dispatcher.runSync("createSupplierProduct", input);
			}
		}

	}

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
		SupplierProduct supplierProduct = (SupplierProduct) messageData;

		insert(supplierProduct);

		for (Product product : supplierProduct.getProducts()) {

			GenericValue value = delegator.makeValue("BusProductStore");

			value.set("partyId", user);
			value.set("productId", product.getBusId());
			value.set("productStoreId", product.getMessageData().get("productStoreId"));

			delegator.createOrStore(value);

		}
	}
	
	@Override
	protected void end(String id, String user, String type) {
		OlbiusMessage tmp = new OlbiusMessage();

		tmp.setType(REGISTER_SUPPLIER);

		BusContainer.EVENT_FACTORY.getSendEvent(delegator).sendTopic(tmp);
	}

}
