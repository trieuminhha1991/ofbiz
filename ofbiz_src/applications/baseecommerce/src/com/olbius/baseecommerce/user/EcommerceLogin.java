package com.olbius.baseecommerce.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.securityext.login.LoginEvents;
import org.ofbiz.webapp.control.LoginWorker;

public class EcommerceLogin {
	public static String storeLogin(HttpServletRequest request, HttpServletResponse response) {
		if (UtilValidate.isNotEmpty(request.getParameter("isSubmit"))) {
			String responseString = LoginWorker.login(request, response);
	        if (!"success".equals(responseString)) {
	            return responseString;
	        }
	        if ("Y".equals(request.getParameter("rememberMe"))) {
	            LoginEvents.setUsername(request, response);
	        }
	        return ProductEvents.checkStoreCustomerRole(request, response);
		} else {
			return "error";
		}
    }
}
