package com.olbius.jms.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.jms.data.MessageId;

public class OlbiusJmsData {
	
	private Map<String, Insert> insert = new HashMap<String, Insert>();
	
	public Insert getInsert(String s) {
		return insert.get(s);
	}
	
	public void load(String s) {
		Properties p = UtilProperties.getProperties(s);
		for(Object key : p.keySet()) {
			try {
				Class<?> tmp = Class.forName(p.getProperty((String) key));
				if(Insert.class.isAssignableFrom(tmp)) {
					insert.put((String) key, (Insert) tmp.newInstance());
				}
			} catch (Exception e) {
			}
		}
	}
	
	public static interface Insert {
		
		void exc(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Callback callback) throws Exception;
		
	}
	
	public static interface Callback {
		
		Object run(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin);
		
	}
}
