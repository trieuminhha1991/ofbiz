package com.olbius.basehr.configuration;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;

public class ConfigurationEvents {
	public static String createPayrollCustomTimePeriod(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String yearStr = request.getParameter("year");
		int year = Integer.parseInt(yearStr);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Locale locale = UtilHttp.getLocale(request);
		if(year < 0){
			year = 0;
		}
		Security security = (Security) request.getAttribute("security");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		String periodPayroll = request.getParameter("periodPayroll");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp startYearTs = UtilDateTime.getYearStart(timestamp); 
		Timestamp endYearTs = UtilDateTime.getYearEnd(timestamp, timeZone, locale);
		Date startYear = new Date(startYearTs.getTime());
		Date endYear = new Date(endYearTs.getTime());
		if(security.hasEntityPermission("HR_CONFIG", "_ADMIN", userLogin)){
			try {
				Map<String, Object> ctxMap = FastMap.newInstance();
				GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				ctxMap.put("userLogin", system);
				ctxMap.put("periodTypeId", "YEARLY");
				ctxMap.put("fromDate", startYear);
				ctxMap.put("thruDate", endYear);
				ctxMap.put("isClosed", "N");
				ctxMap.put("periodName", "Năm " + year);
				ctxMap.put("organizationPartyId", PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
				Map<String, Object> resultService = dispatcher.runSync("createCustomTimePeriod", ctxMap);
				if(ServiceUtil.isSuccess(resultService)){
					String parentCustomTimePeriodId = (String)resultService.get("customTimePeriodId");
					ctxMap.put("periodTypeId", "MONTHLY");
					ctxMap.put("parentPeriodId", parentCustomTimePeriodId);
					JSONArray periodPayrollJson = JSONArray.fromObject(periodPayroll);
					for(int i = 0; i < periodPayrollJson.size(); i++){
						JSONObject customTimePeriodJson = periodPayrollJson.getJSONObject(i);
						String periodName = customTimePeriodJson.getString("periodName");
						String fromDateStr = customTimePeriodJson.getString("fromDate");
						String thruDateStr = customTimePeriodJson.getString("thruDate");
						Date fromDate = new Date(Long.parseLong(fromDateStr));
						Date thruDate = new Date(Long.parseLong(thruDateStr));
						ctxMap.put("fromDate", fromDate);
						ctxMap.put("thruDate", thruDate);
						ctxMap.put("periodName", periodName);
						dispatcher.runSync("createCustomTimePeriod", ctxMap);
					}
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
				}else{
					String err = CommonUtil.getErrorMessageFromService(resultService);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, err);
				}
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You don't have permission");
		}
		return "success";
	}
	
	public static String updatePayrollCustomTimePeriod(HttpServletRequest request, HttpServletResponse response){
		Security security = (Security) request.getAttribute("security");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
		if(security.hasEntityPermission("HR_CONFIG", "_ADMIN", userLogin)){
			try {
				GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				String periodName = request.getParameter("periodName");
				String fromDateStr = request.getParameter("fromDate");
				String thruDateStr = request.getParameter("thruDate");
				String customTimePeriodId = request.getParameter("customTimePeriodId");
				Date fromDate = new Date(Long.parseLong(fromDateStr));
				Date thruDate = new Date(Long.parseLong(thruDateStr));
				Map<String, Object> context = UtilMisc.toMap("customTimePeriodId", customTimePeriodId, 
															"fromDate", fromDate, "thruDate", thruDate, 
															"periodName", periodName, "userLogin", system);
				Map<String, Object> resultService = dispatcher.runSync("updateCustomTimePeriod", context);
				if(ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
				}else{
					String err = CommonUtil.getErrorMessageFromService(resultService);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, err);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
			
		}else{
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, "You don't have permission");
		}
		return "success";
	}
}
