package com.olbius.activemq.receive.handle;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.Delegator;

public class OlbiusReceiveDataFactory {

private static Map<String, OlbiusReceiveData> receiveDatas;
	
	public static OlbiusReceiveData getInstance(Delegator delegator) {
		if(receiveDatas == null) {
			receiveDatas = new HashMap<String, OlbiusReceiveData>();
		}
		
		if(receiveDatas.get(delegator.getDelegatorName()) == null) {
			receiveDatas.put(delegator.getDelegatorName(), new OlbiusReceiveData(delegator));
		}
		
		return receiveDatas.get(delegator.getDelegatorName());
		
	}
	
}
