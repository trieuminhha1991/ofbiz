package com.olbius.jms.event.handle;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.SupplierProduct;
import com.olbius.jms.data.handle.AbstractOlbiusBusEvent;
import com.olbius.jms.event.OfbizDataServices;

/**
 * @author Nguyen Ha
 *
 */
public class GetSupplier extends AbstractOlbiusBusEvent {

	@Override
	public void busHandle(MessageData messageData, String user) throws Exception {
	}

	@Override
	protected void end(String id, String user, String type) {

		try {

			Timestamp timestamp = BusEvent.getTimeStamp(delegator, user, type);

			List<GenericValue> supplierProducts = delegator.findList("SupplierProduct",
					EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, timestamp), null, null, null, false);

			OlbiusMessage tmp = new OlbiusMessage();
			tmp.setType(GET_SUPPLIER);
			tmp.setUser(user);

			Map<String, SupplierProduct> map = new HashMap<String, SupplierProduct>();

			for (GenericValue supplierProduct : supplierProducts) {

				GenericValue value = delegator.findOne("BusConvert", UtilMisc.toMap("partyId", user, "busId", supplierProduct.getString("partyId")),
						false);

				if (value != null && "Y".equals(value.getString("own"))) {
					continue;
				}

				SupplierProduct suppProduct = map.get(supplierProduct.getString("partyId"));

				if (suppProduct == null) {

					suppProduct = new SupplierProduct();

					suppProduct.setGroup(OfbizDataServices.getPartyGroupBus(delegator, supplierProduct.getString("partyId")));

					map.put(supplierProduct.getString("partyId"), suppProduct);
				}

				Product product = OfbizDataServices.getProductBus(delegator, supplierProduct.getString("productId"));

				product.getMessageData().put("availableFromDate", supplierProduct.getTimestamp("availableFromDate").getTime());
				product.getMessageData().put("availableThruDate", supplierProduct.getTimestamp("availableThruDate") != null
						? supplierProduct.getTimestamp("availableThruDate").getTime() : null);
				product.getMessageData().put("lastPrice", supplierProduct.getBigDecimal("lastPrice"));
				product.getMessageData().put("minimumOrderQuantity", supplierProduct.getBigDecimal("minimumOrderQuantity"));

				product.setQuantityUom(OfbizDataServices.getUomBus(delegator, supplierProduct.getString("quantityUomId")));
				product.setCurrencyUom(OfbizDataServices.getUomBus(delegator, supplierProduct.getString("currencyUomId")));

				suppProduct.getProducts().add(product);

			}

			for (String sp : map.keySet()) {
				tmp.getDatas().add(map.get(sp));
			}

			send(ActivemqSession.QUEUE, tmp, user, true);

			BusEvent.udpateTimeStamp(delegator, user, type);

		} catch (Exception e) {
			Debug.logError(e, ProductPublisher.class.getName());
		}

	}

}
