package com.olbius.bi.system;

import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

public class ProcessServices {
	
	public final static String module = ProcessServices.class.getName();
	
	public final static String PROCESSING = "PROCESSING";
	
	public final static String COMPLETED = "COMPLETED";
	
	public static boolean checkRun(Delegator delegator, String service) throws GenericEntityException {
		GenericValue value = null;
		
		value = EntityUtil.getFirst(delegator.findByAnd("PentahoServices", UtilMisc.toMap("service", service), null, false));
		
		if(value == null) {
			return false;
		}
		
		if(PROCESSING.equals(value.getString("status"))) {
			return true;
		}
		
		return false;
	}
	
	public static void updateStatus(Delegator delegator, String service, String status, Timestamp timestamp) throws GenericEntityException {
		GenericValue value = null;
		
		value = EntityUtil.getFirst(delegator.findByAnd("PentahoServices", UtilMisc.toMap("service", service), null, false));
		
		if(value != null) {
			value.set("status", status);
			if(timestamp != null){
				value.set("lastUpdated", timestamp);
			}
			value.store();
		} else {
			value = delegator.makeValue("PentahoServices");
			value.set("service", service);
			value.set("status", status);
			if(timestamp != null){
				value.set("lastUpdated", timestamp);
			}
			value.create();
		}
	}
	
	public static Timestamp getLastUpdated(Delegator delegator, String service) throws GenericEntityException {
		GenericValue value = null;
		
		value = EntityUtil.getFirst(delegator.findByAnd("PentahoServices", UtilMisc.toMap("service", service), null, false));
		
		if(value != null) {
			return value.getTimestamp("lastUpdated");
		}
		
		return null;
	}
	
	public static String getStatus(Delegator delegator, String service) throws GenericEntityException {
		GenericValue value = null;
		
		value = EntityUtil.getFirst(delegator.findByAnd("PentahoServices", UtilMisc.toMap("service", service), null, false));
		
		if(value != null) {
			return value.getString("status");
		}
		
		return COMPLETED;
	}
	
}
