package org.ofbiz.mobileservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.securityext.login.LoginEvents;

public class LoginEventsMobile extends LoginEvents{
	public static String storeLogin(HttpServletRequest request, HttpServletResponse response) {
        String responseString = LoginMobileServices.login(request, response);
        if (!"success".equals(responseString)) {
            return responseString;
        }
        if ("Y".equals(request.getParameter("rememberMe"))) {
            setUsername(request, response);
        }
        // if we logged in okay, do the check store customer role
        return ProductEvents.checkStoreCustomerRole(request, response);
    }
}
