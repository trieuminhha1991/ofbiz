package com.olbius.bi.olap.webapp.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.webapp.event.ServiceEventHandler;

import com.olbius.bi.olap.grid.export.OlapExport;
import com.olbius.bi.olap.grid.export.excel.OlbiusWorkbook;
import com.olbius.bi.olap.grid.export.excel.config.XmlWorkbook;
import com.olbius.bi.olap.services.OlbiusOlapService;

import javolution.util.FastList;
import javolution.util.FastMap;

public class OlapExportEventHandler extends OlapServiceEventHandler {

	@Override
	public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response)
			throws EventHandlerException {
		
		String filename = request.getParameter("filename");
		String exportType = request.getParameter("exportType");
		String exportFormat = request.getParameter("exportFormat");
		
		OlapExport export = null;
		
		if("xls".equals(exportType) && exportFormat != null) {
			try {
				export = new OlbiusWorkbook(new XmlWorkbook(exportFormat), request.getParameter("olapTitle"));
			} catch (Exception e) {
				Debug.logError(e, OlapExportEventHandler.class.getName());;
			}
			if(filename == null || filename.isEmpty()) {
				filename = "export";
			}
			filename += "." + exportType;
		}
		
		if(export == null) {
			return "error";
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }

        String serviceName = request.getParameter("serviceName");

        if (serviceName == null) {
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        }

        // some needed info for when running the service
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        // get the service model to generate context
        ModelService model = null;

        try {
            model = dctx.getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Problems getting the service model", e);
        }

        if (model == null) {
            throw new EventHandlerException("Problems getting the service model");
        }

        Map<String, Object> multiPartMap = FastMap.newInstance();

        Map<String, Object> rawParametersMap = UtilHttp.getParameterMap(request, null, null);
        Set<String> urlOnlyParameterNames = UtilHttp.getUrlOnlyParameterMap(request).keySet();

        // we have a service and the model; build the context
        Map<String, Object> serviceContext = FastMap.newInstance();
        for (ModelParam modelParam: model.getInModelParamList()) {
            String name = modelParam.name;

            // don't include userLogin, that's taken care of below
            if ("userLogin".equals(name)) continue;
            // don't include locale, that is also taken care of below
            if ("locale".equals(name)) continue;
            // don't include timeZone, that is also taken care of below
            if ("timeZone".equals(name)) continue;

            Object value = null;
            if (UtilValidate.isNotEmpty(modelParam.stringMapPrefix)) {
                Map<String, Object> paramMap = UtilHttp.makeParamMapWithPrefix(request, multiPartMap, modelParam.stringMapPrefix, null);
                value = paramMap;
                if (Debug.verboseOn()) Debug.logVerbose("Set [" + modelParam.name + "]: " + paramMap, module);
            } else if (UtilValidate.isNotEmpty(modelParam.stringListSuffix)) {
                List<Object> paramList = UtilHttp.makeParamListWithSuffix(request, multiPartMap, modelParam.stringListSuffix, null);
                value = paramList;
            } else {
                // first check the multi-part map
                value = multiPartMap.get(name);

                // next check attributes; do this before parameters so that attribute which can be changed by code can override parameters which can't
                if (UtilValidate.isEmpty(value)) {
                    Object tempVal = request.getAttribute(UtilValidate.isEmpty(modelParam.requestAttributeName) ? name : modelParam.requestAttributeName);
                    if (tempVal != null) {
                        value = tempVal;
                    }
                }

                // check the request parameters
                if (UtilValidate.isEmpty(value)) {
                    ServiceEventHandler.checkSecureParameter(requestMap, urlOnlyParameterNames, name, session, serviceName, dctx.getDelegator());

                    // if the service modelParam has allow-html="any" then get this direct from the request instead of in the parameters Map so there will be no canonicalization possibly messing things up
                    if ("any".equals(modelParam.allowHtml)) {
                        value = request.getParameter(name);
                    } else {
                        // use the rawParametersMap from UtilHttp in order to also get pathInfo parameters, do canonicalization, etc
                        value = rawParametersMap.get(name);
                    }

                    // make any composite parameter data (e.g., from a set of parameters {name_c_date, name_c_hour, name_c_minutes})
                    if (value == null) {
                        value = UtilHttp.makeParamValueFromComposite(request, name, locale);
                    }
                }

                // then session
                if (UtilValidate.isEmpty(value)) {
                    Object tempVal = request.getSession().getAttribute(UtilValidate.isEmpty(modelParam.sessionAttributeName) ? name : modelParam.sessionAttributeName);
                    if (tempVal != null) {
                        value = tempVal;
                    }
                }

                // no field found
                if (value == null) {
                    //still null, give up for this one
                    continue;
                }

                if (value instanceof String && ((String) value).length() == 0) {
                    // interpreting empty fields as null values for each in back end handling...
                    value = null;
                }
            }
            // set even if null so that values will get nulled in the db later on
            serviceContext.put(name, value);
        }

        // get only the parameters for this service - converted to proper type
        // TODO: pass in a list for error messages, like could not convert type or not a proper X, return immediately with messages if there are any
        List<Object> errorMessages = FastList.newInstance();
        serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM, true, errorMessages, timeZone, locale);
        if (errorMessages.size() > 0) {
            // uh-oh, had some problems...
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorMessages);
            return "error";
        }

        // include the UserLogin value object
        if (userLogin != null) {
            serviceContext.put("userLogin", userLogin);
        }

        // include the Locale object
        if (locale != null) {
            serviceContext.put("locale", locale);
        }

        // include the TimeZone object
        if (timeZone != null) {
            serviceContext.put("timeZone", timeZone);
        }
        
        // include export object
        serviceContext.put("export", export);

        // invoke the service
        Map<String, Object> result = null;
        try {
        	Class<?> c = Class.forName(model.location);
        	if(OlbiusOlapService.class.isAssignableFrom(c)) {
        		OlbiusOlapService service = (OlbiusOlapService) c.newInstance();
        		result = service.run(dctx, serviceContext);
        	}
        } catch (Exception e) {
        	Debug.logError(e, "Service invocation error", module);
            return "error";
		}
		
		if(result.get("out") != null) {
			ByteArrayOutputStream stream = (ByteArrayOutputStream) result.get("out");
			request.removeAttribute("out");
			byte[] bytes = stream.toByteArray();
			response.setHeader("content-disposition",
					"attachment;filename=" + filename);
			response.setContentType("application/vnd.xls");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			try {
				response.getOutputStream().write(bytes);
			} catch (IOException e) {
				Debug.logError(e, OlapExportEventHandler.class.getName());
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					Debug.logError(e, OlapExportEventHandler.class.getName());
				}
			}
		}
		
		return "success";
	}
	
}
