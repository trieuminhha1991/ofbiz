package com.olbius.acc.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import org.ofbiz.entity.GenericEntityException;

public class BalCache extends Cache{
	public BalCache() throws GenericEntityException {
		super();
		
		//Init Again
		cachedElms = new HashMap<String, Object>();
		lastUpdate = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
}
