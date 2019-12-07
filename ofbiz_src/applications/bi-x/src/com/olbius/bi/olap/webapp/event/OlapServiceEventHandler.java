package com.olbius.bi.olap.webapp.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.webapp.event.ServiceEventHandler;

public class OlapServiceEventHandler extends ServiceEventHandler {

	@Override
	public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response)
			throws EventHandlerException {
		String service = request.getParameter("serviceName");
		return super.invoke(new Event("service", null, service, event.globalTransaction), requestMap, request, response);
	}
	
}
