package com.olbius.jms.data.handle;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.Delegator;

public class OlbiusBusDataFactory {

	private static Map<String, OlbiusBusData> busDatas;
	
	public static OlbiusBusData getInstance(Delegator delegator) {
		if(busDatas == null) {
			busDatas = new HashMap<String, OlbiusBusData>();
		}
		
		if(busDatas.get(delegator.getDelegatorName()) == null) {
			busDatas.put(delegator.getDelegatorName(), new OlbiusBusData(delegator));
		}
		
		return busDatas.get(delegator.getDelegatorName());
		
	}
	
}
