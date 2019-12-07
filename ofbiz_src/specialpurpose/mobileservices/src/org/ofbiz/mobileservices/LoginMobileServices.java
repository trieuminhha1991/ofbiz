package org.ofbiz.mobileservices;

import static org.ofbiz.base.util.UtilGenerics.checkMap;

import java.io.BufferedReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.security.SecurityConfigurationException;
import org.ofbiz.security.SecurityFactory;
import org.ofbiz.service.*;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.stats.VisitHandler;

import com.olbius.webapp.OlbiusLogin;

public class LoginMobileServices extends LoginWorker {
	public static String checkLogin(HttpServletRequest request,
			HttpServletResponse response) {
		GenericValue userLogin = checkLogout(request, response);
		// have to reget this because the old session object will be invalid
		HttpSession session = request.getSession();

		String username = null;
		String password = null;

		if (userLogin == null) {
			// check parameters
			username = request.getParameter("USERNAME");
			password = request.getParameter("PASSWORD");
			// check session attributes
			if (username == null)
				username = (String) session.getAttribute("USERNAME");
			if (password == null)
				password = (String) session.getAttribute("PASSWORD");

			// in this condition log them in if not already; if not logged in or
			// can't log in, save parameters and return error
			if ((username == null) || (password == null)
					|| ("error".equals(login(request, response)))) {
				request.removeAttribute("_LOGIN_PASSED_");

				session.setAttribute("_PREVIOUS_REQUEST_",
						request.getPathInfo());
				request.setAttribute("login", "FALSE");
				Map<String, Object> urlParams = UtilHttp
						.getUrlOnlyParameterMap(request);
				if (UtilValidate.isNotEmpty(urlParams)) {
					session.setAttribute("_PREVIOUS_PARAM_MAP_URL_", urlParams);
				}
				Map<String, Object> formParams = UtilHttp.getParameterMap(
						request, urlParams.keySet(), false);
				if (UtilValidate.isNotEmpty(formParams)) {
					session.setAttribute("_PREVIOUS_PARAM_MAP_FORM_",
							formParams);
				}

				return "error";
			}
		}
		Locale locale = UtilHttp.getLocale(request);
		request.setAttribute("login", "TRUE");
		request.setAttribute("locale", locale.toString());
		try {
			OlbiusLogin.getExternalLoginKey(request, response);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}

		return "success";
	}
	public static String dologin(HttpServletRequest request,
			HttpServletResponse response) {
		checkLogin(request, response);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String sz = request.getParameter("size");
		int size = UtilValidate.isNotEmpty(sz) ? Integer.parseInt(sz) : 200;
		Map<String, Object> in = FastMap.newInstance();
		in.put("userLogin", userLogin);
		in.put("page", 0);
		in.put("size", size);
		try {
			Map<String, Object> out = dispatcher.runSync("getStoreByRoadNew", in);
			List<Map<String, Object>> customers = (List<Map<String, Object>>)out.get("customers");
			List<Map<String, Object>> routes = (List<Map<String, Object>>)out.get("routes");
			Integer total = (Integer) out.get("total");
			request.setAttribute("total", total);
			request.setAttribute("customers", customers);
			request.setAttribute("routes", routes);
			in.put("other", "Y");
			out = dispatcher.runSync("getStoreByRoadNew", in);
			request.setAttribute("others", out);
		} catch (GenericServiceException e1) {
			e1.printStackTrace();
		}
		return "success";
	}
	public static String mobilelogin(HttpServletRequest request,
			HttpServletResponse response) {
		return checkLogin(request, response);
	}
	public static Map<String, Object> mGetCustomers(DispatchContext dctx, Map<String, ? extends Object> context) {
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale)context.get("locale");
		String productStoreId = (String) context.get("productStoreId");
		String pagenum = context.containsKey("viewIndex") ? (String) context.get("viewIndex") : "0";
		String pagesize = context.containsKey("viewSize") ? (String) context.get("viewSize") : "200";

		Map<String, Object> in = FastMap.newInstance();
		in.put("userLogin", userLogin);
		in.put("page", pagenum);
		in.put("size", pagesize);
		try {
			List<String> customerByStoreIds = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			if (productStoreId != null) {
                conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
                conds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
                conds.add(EntityUtil.getFilterByDateExpr());
                List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyId"), null, null, false);
                customerByStoreIds = EntityUtil.getFieldListFromEntityList(productStoreRoles, "partyId", true);
                in.put("customerByStoreIds", customerByStoreIds.toString());
            }
            Map<String, Object> out = dispatcher.runSync("getStoreByRoadNew", in); //old: getStoreByRoad
			List<Map<String, Object>> customers = (List<Map<String, Object>>)out.get("customers");
			List<String> routes = (List<String>)out.get("routes");
			Integer total = (Integer) out.get("total");
			successResult.put("total", total);
			successResult.put("customers", customers);
			successResult.put("routes", routes);
			in.put("other", "Y");
            if (UtilValidate.isNotEmpty(routes)) {
                in.put("routeIds", routes.toString());
            }
            out = dispatcher.runSync("getStoreByRoadNew", in); //old: getStoreByRoad
			successResult.put("others", out);
		} catch (GenericServiceException e1) {
			e1.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> mUpdatePassWordMobilemcs(DispatchContext ctx, Map<String, ? extends Object> context) {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = Locale.getDefault();
		String currentPassword = (String) context.get("currentPassword");
		String newPassword = (String) context.get("newPassword");
		String newPasswordVerify = (String) context.get("passwordVerify");
		try {
			Map<String,Object> result = FastMap.newInstance();
			List<String> errorMessageList = FastList.newInstance();
			LoginServices.checkNewPassword(userLogin, currentPassword, newPassword,
					newPasswordVerify, null, errorMessageList, false, locale);
			if(UtilValidate.isNotEmpty(errorMessageList)){
				return ServiceUtil.returnError(errorMessageList.get(0));
			}
			result = dispatcher.runSync("updatePassword", UtilMisc.toMap("userLogin", userLogin,"userLoginId", userLogin.getString("userLoginId"),
					"currentPassword",currentPassword,"newPassword",newPassword,"newPasswordVerify",newPasswordVerify));
			if(ServiceUtil.isError(result)){
				return ServiceUtil.returnError("errorChangePassword");
			}
		}catch(GenericServiceException e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} catch (Exception e) {
			Debug.logError("Can't update password SalesMan"+e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}

    public static Map<String, Object> mSubmitDeviceId(DispatchContext dctx, Map<String, ? extends Object> context) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String userLoginPartyId = userLogin.getString("partyId");
        String userLoginId = userLogin.getString("userLoginId");
        String deviceId = (String) context.get("deviceId");
        try {
            if (deviceId == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage("MobileServicesErrorUiLabels", "MobileServicesDeviceIdNotExisted", locale));
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyId", userLoginPartyId));
            conds.add(EntityCondition.makeCondition("deviceId", deviceId));
            List<GenericValue> mobileDevices = delegator.findList("MobileDevice", EntityCondition.makeCondition(conds), null, null, null, false);
            if (UtilValidate.isNotEmpty(mobileDevices)) {
                for (GenericValue mobileDevice : mobileDevices) {
                    mobileDevice.set("updatedTime", UtilDateTime.nowTimestamp());
                    mobileDevice.store();
                }
            } else {
                GenericValue mobileDevice = delegator.makeValue("MobileDevice");
                String mobileDeviceId = "MD" + delegator.getNextSeqId("MobileDevice");
                mobileDevice.set("mobileDeviceId", mobileDeviceId);
                mobileDevice.set("deviceId", deviceId);
                mobileDevice.set("partyId", userLoginPartyId);
                mobileDevice.set("createdByUserLogin", userLoginId);
                mobileDevice.set("createdTime", UtilDateTime.nowTimestamp());
                mobileDevice.set("updatedTime", UtilDateTime.nowTimestamp());
                mobileDevice.create();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successResult;
    }

}
