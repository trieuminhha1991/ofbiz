package com.olbius.basedelivery.vehicle;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 11/29/17.
 */
public class VehicleEvents {
    public static final String module = VehicleEvents.class.getName();

    public static final String resource_error = "BaseSalesErrorUiLabels";

    public static String processEditVehicle(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        try {
            Map<String, Object> resultValue = dispatcher.runSync("updateVehicle", UtilMisc.toMap("request", request, "userLogin", userLogin, "locale", locale));
            if (ServiceUtil.isError(resultValue)) {
                ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service updateVehicle");
                return "error";
            }
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run service updateVehicle");
            return "error";
        }
        return "success";
    }

}
