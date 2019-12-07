package com.olbius.acc.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ModelService;

public class ErrorUtils {
	public static void processException(Exception exception, String module) {
		if(Debug.verboseOn()) {
			exception.printStackTrace();
		}
		Debug.logError(exception.getMessage(), module);
		exception.printStackTrace();
	}
	
	public static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
	@SuppressWarnings("unchecked")
	public static String getErrorMessageFromService(Map<String, Object> resultService) {
		String errMes = "";
		if(resultService.get(ModelService.ERROR_MESSAGE_LIST) != null){
			List<String> errList = (List<String>)resultService.get(ModelService.ERROR_MESSAGE_LIST);
			errMes = errList.get(0);
		}else{
			errMes = (String)resultService.get(ModelService.ERROR_MESSAGE);
		}
		return errMes;
	}
}
