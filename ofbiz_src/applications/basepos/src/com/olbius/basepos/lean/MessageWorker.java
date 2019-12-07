package com.olbius.basepos.lean;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;

/*
 * This class used for handling messages
 * */
public class MessageWorker {
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	private Locale locale;
	private String resourceName;
	private HttpServletRequest request;
	public MessageWorker(HttpServletRequest request, String resourceName){
		this.request = request;
		this.setLocale(UtilHttp.getLocale(request));
		this.resourceName = resourceName;
	}
	public String sendErrorMessage(String strKey){
		String errorMessage = UtilProperties.getMessage(resourceName, strKey, locale);
		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
		return ERROR;
	}
	public static String sendSuccessMessage(){
		return SUCCESS;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
