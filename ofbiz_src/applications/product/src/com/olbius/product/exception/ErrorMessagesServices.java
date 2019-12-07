package com.olbius.product.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

public class ErrorMessagesServices {
	public static Map<String, Object> refineErrorMessages(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
		List<String> errorMsgList = new ArrayList<String>();
		errorMsgList.add("some comprehensible error message");
		result.put(ModelService.ERROR_MESSAGE_LIST, errorMsgList);
		return result;
	}

}
