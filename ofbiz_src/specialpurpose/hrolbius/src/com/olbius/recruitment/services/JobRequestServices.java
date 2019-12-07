package com.olbius.recruitment.services;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.employee.services.EmployeeServices;
import com.olbius.recruitment.helper.JobRequestHelper;

public class JobRequestServices {
	
	public static final String module = EmployeeServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	
	/**
	 * Create a Job Request
	 * 
	 * @param dpctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createJobRequest(DispatchContext dpctx, Map<String, Object> context) {
		//Get locale
		Locale locale = (Locale)context.get("locale");
		
		try {
			return JobRequestHelper.createJobRequest(dpctx, context);
		} catch (Exception e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	/**
	 * Update a Job Request
	 * @param dpctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateJobRequest(DispatchContext dpctx, Map<String, Object> context) {
		//Get locale
		Locale locale = (Locale)context.get("locale");
		
		try {
			return JobRequestHelper.updateJobRequest(dpctx, context);
		} catch (Exception e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
	}
}
