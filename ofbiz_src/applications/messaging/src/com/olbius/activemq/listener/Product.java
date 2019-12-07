package com.olbius.activemq.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Nguyen Ha
 *
 */
public class Product implements OlbiusMessageListener{
	private Delegator delegator;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				Debug.log("Received message: " + textMessage.getText(), Product.class.getName());
				EntityReader reader = new EntityReader(delegator);
				List<GenericValue> values = reader.getGenericValues(textMessage.getText());
				Debug.log("Generic Value: " + values.toString(), Product.class.getName());
				
				List<GenericValue> valueStores = new ArrayList<GenericValue>();
				
				for(GenericValue value : values) {
					GenericValue tmp = delegator.makeValue("Product");
					tmp.set("productId", UUID.randomUUID().toString().replaceAll("-", ""));
					tmp.set("internalName", value.get("internalName"));
					tmp.set("productName", value.get("productName"));
					valueStores.add(tmp);
				}
				
				Debug.log("Generic Value Store: " + valueStores.toString(), Product.class.getName());
				
				delegator.storeAll(valueStores);
			}
		} catch (Exception e) {
			Debug.logError(e, Product.class.getName());
		}
	}

	@Override
	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}
}
