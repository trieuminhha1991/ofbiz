package com.olbius.acc.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class Cache {
	
	Map<String, Object> cachedElms;
	Timestamp lastUpdate;
	
	public Cache() throws GenericEntityException {
		cachedElms = new HashMap<String, Object>();
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		GenericValue pentahoService = delegator.findOne("PentahoServices", UtilMisc.toMap("service", "acctgTransTotal"), false);
		if(pentahoService != null && pentahoService.getTimestamp("lastUpdated") != null) {
			this.setLastUpdate(pentahoService.getTimestamp("lastUpdated"));
		}else {
			lastUpdate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		}
	}
	
	public void clearCache() {
		cachedElms = new HashMap<String, Object>(); 
	}
	
	protected String getKey(String rawKey) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(rawKey.getBytes());
		String encryptedString = new String(messageDigest.digest());
		return encryptedString;
	}
	
	public void putObject(String rawKey, Object value) throws NoSuchAlgorithmException {
		cachedElms.put(getKey(rawKey), value);
	}
	
	public Object getObject(String rawKey) throws NoSuchAlgorithmException {
		return cachedElms.get(getKey(rawKey));
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
