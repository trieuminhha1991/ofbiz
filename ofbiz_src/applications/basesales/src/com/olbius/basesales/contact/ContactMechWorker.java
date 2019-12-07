package com.olbius.basesales.contact;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class ContactMechWorker {
	public static final String module = ContactMechWorker.class.getName();
	public static final String resource = "SalesUiLabels";
    public static final String resource_error = "SalesErrorUiLabels";
    public static String RESOURCE_PROPERTIES = "dms.properties";
    
    public static String getGeoName(Delegator delegator, String geoId) throws GenericEntityException {
    	String geoName = null;
    	if (UtilValidate.isEmpty(geoId)) return geoName;
    	
    	GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), true);
		if (geo != null) {
			geoName = geo.getString("geoName");
			if ("_NA_".equals(geoId)) geoName = "___";
		}
    	return geoName;
    }
}
