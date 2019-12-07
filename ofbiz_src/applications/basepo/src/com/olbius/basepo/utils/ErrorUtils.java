package com.olbius.basepo.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.ofbiz.base.util.Debug;

public class ErrorUtils {
	public static void processException(Exception exception, String module) {
		if (Debug.verboseOn()) {
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
}
