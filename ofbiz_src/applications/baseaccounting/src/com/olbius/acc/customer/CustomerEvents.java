package com.olbius.acc.customer;

import com.olbius.acc.utils.ErrorUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CustomerEvents {
	public static String createCustomer(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String fromDateStr = request.getParameter("fromDate");
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createCustomerACC", paramMap, userLogin, timeZone, locale);
			context.put("fromDate", fromDate);
			Map<String, Object> resultService = dispatcher.runSync("createCustomerACC", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updateCustomer(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = request.getParameter("fromDate");
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateCustomerACC", paramMap, userLogin, timeZone, locale);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			context.put("fromDate", fromDate);
			Map<String, Object> resultService = dispatcher.runSync("updateCustomerACC", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
