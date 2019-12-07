package com.olbius.jms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.activemq.Activemq;
import com.olbius.activemq.ActivemqFactory;
import com.olbius.activemq.TextMessage;

public class Publisher {

	public static Map<String, Object> sendTextMessage(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		String message = (String) context.get("message");
		String destination = (String) context.get("destination");
		String destinationType = (String) context.get("destinationType");
		try {
			TextMessage textMessage = new TextMessage();

			if ("topic".equalsIgnoreCase(destinationType)) {
				textMessage.setType(Activemq.TOPIC);
			} else {
				textMessage.setType(Activemq.QUEUE);
			}

			textMessage.setDestination(destination);

			textMessage.setMessage(message);

			ActivemqFactory.getInstance().sendMessage(textMessage);

		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> sendProduct(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String productId = (String) context.get("productId");

		Delegator delegator = ctx.getDelegator();

		try {

			List<String> orderBy = new ArrayList<String>();
			orderBy.add("productId");
			List<GenericValue> gvs = delegator.findByAnd("Product", UtilMisc.toMap("productId", productId), orderBy, false);
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			printWriter.println("<entity-engine-xml>");
			for (GenericValue gv : gvs) {
				
				GenericValue value = delegator.makeValue("Product");
				
				value.set("internalName", gv.get("internalName"));
				value.set("productName", gv.get("productName"));
				
				value.writeXmlText(printWriter, null);
			}
			printWriter.println("</entity-engine-xml>");
			result.put("productXml", sw.toString());
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
